import akka.actor.{Actor, ActorPath, ActorSystem, Props}
import akka.event.Logging
import akka.util.Timeout

/**
  * Created by yww08 on 2018-12-03.
  *
  * https://github.com/godpan/akka-demo
  */
object demo {


}

trait Action {
	val message: String
	val time: Int
}

case class TurnOnLight(time: Int) extends Action {
	override val message: String = "turn on light"
}

case class BoilWater(time: Int) extends Action {
	override val message: String = "burn a pot of water"
}

class RobotActor extends Actor {
	val log = Logging(context.system, this)

	override def receive: Receive = {
		case t: TurnOnLight => log.info(s"${t.message} after ${t.time} hour")
		case b: BoilWater => log.info(s"${b.message} after ${b.time} hour")
		case _ => log.info("I cannot handle this message")
	}
}

object Example_01 extends App {
	val actorSystem = ActorSystem("robot-system")
	println(s"akka-system loglevel is ${actorSystem.settings.LogLevel}")
	val robotActor = actorSystem.actorOf(Props(new RobotActor), "robotActor")
	robotActor ! TurnOnLight(1)
	robotActor ! BoilWater(2)
	robotActor ! "who are you"
	actorSystem.terminate()
}

trait Message {
	val content: String
}

case class Business(content: String) extends Message {}

case class Meeting(content: String) extends Message {}

case class Confirm(content: String, actorPath: ActorPath) extends Message {}

case class DoAction(content: String) extends Message {}

case class Done(content: String) extends Message {}

import akka.actor._
import akka.pattern.ask

import scala.concurrent.duration._

class BossActor extends Actor {
	val log = Logging(context.system, this)
	implicit val askTimeout = Timeout(5 seconds)

	import context.dispatcher

	var taskCount = 0

	override def receive: Receive = {
		case b: Business =>
			log.info("I must to do some thing")
			println(self.path.address)

			val managerActors = (1 to 3).map(i =>
				context.actorOf(Props[ManagerActor], s"manager${i}")) //这里我们召唤3个主管

			managerActors foreach {
				_ ? Meeting("Meeting to discuss big plans") map {
					case c: Confirm =>
						log.info(c.actorPath.parent.toString)
						val manager = context.actorSelection(c.actorPath)
						manager ! DoAction("Do thing")
				}
			}

		case d: Done =>
			taskCount += 1
			if (taskCount == 3) {
				log.info("the project is done,we will earn much money")
				context.system.terminate()
			}
	}
}

class ManagerActor extends Actor {
	val log = Logging(context.system, this)

	override def receive: Receive = {
		case m: Meeting =>
			sender() ! Confirm("I have receive command", self.path)
		case d: DoAction =>
			val wk = context.actorOf(Props[WorkerActor], "worker")
			wk forward d
	}
}

class WorkerActor extends Actor {
	val log = Logging(context.system, this)

	override def receive: Receive = {
		case d: DoAction =>
			log.info("I have receive task")
			sender() ! Done("I have done work")
	}
}

object Example_02 extends App {
	val actorSystem = ActorSystem("BussinessSystem")
	val bossActor = actorSystem.actorOf(Props[BossActor], "boss")
	bossActor ! Business("Fitness industry has great prospects")
}