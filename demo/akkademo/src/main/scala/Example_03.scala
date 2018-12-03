import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, OneForOneStrategy, Props, SupervisorStrategy}
import akka.event.Logging

/**
  * Created by yww08 on 2018-12-03.
  *
  * 父监管
  * 4种策略：
  * 1、 恢复下属，保持下属当前积累的内部状态
  *2. 重启下属，清除下属的内部状态
  *3. 永久地停止下属
  *4. 升级失败（沿监管树向上传递失败），由此失败自己
  */
object Example_03 {

}

/**
  * Akka中有两种类型的监管策略：OneForOneStrategy和AllForOneStrategy，它们的主要区别在于：
  * *
  * OneForOneStrategy： 该策略只会应用到发生故障的子actor上。
  * AllForOneStrategy： 该策略会应用到所有的子actor上。
  */

import scala.concurrent.duration._

class Supervisor extends Actor {
	// 监控它的子节点
	override def supervisorStrategy: SupervisorStrategy =
		OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
			case _: ArithmeticException => Resume
			case _: NullPointerException => Restart
			case _: IllegalArgumentException => Stop
			case _: Exception => Escalate
		}

	var childIndex = 0

	override def receive: Receive = {
		case p: Props =>
			childIndex += 1
			sender() ! context.actorOf(p, s"child${childIndex}")
	}
}

class Child extends Actor {

	val log = Logging(context.system, this)
	var state = 0

	override def receive: Receive = {
		case ex: Exception => throw ex
		case x: Int => state = x
		case s: Command if s.content == "get" =>
			log.info(s"the ${s.self} state is ${state}")
			sender() ! state
	}
}

case class Command( //相应命令
                    content: String,
                    self: String
                  )