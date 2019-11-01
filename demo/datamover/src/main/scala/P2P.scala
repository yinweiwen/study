import Beautiful.Db
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import comm.utils.{JsonHelper, Loader}
import datamover.EsHelper
import org.joda.time.DateTime

import scalaj.http.Http

/**
  * Created by yww08 on 2019/11/1.
  */
object P2P {

	def main(args: Array[String]): Unit = {

		println("java -cp data-mover.jar P2P {old struct id} {new struct id} {old thing id} {new thing id}")

		val olds = args(0).toInt
		val news = args(1).toInt
		val oldthing = args(2)
		val newthing = args(3)

		val props = Loader.from("/config.properties", args: _*)


		val es = new EsHelper()

		es.initHelper("anxin-cloud", props.getProperty("es.nodes"))

		val od = JsonHelper.Json2Object[deploy](Http(props.getProperty("proxy.url") + s"things/$oldthing/deploys/devices").asString.body)._1.get.devices.map(d => d.name -> d.id).toMap
		val nd = JsonHelper.Json2Object[deploy](Http(props.getProperty("proxy.url") + s"things/$newthing/deploys/devices").asString.body)._1.get.devices

		println(s"old devices: ${od.size}  new devices:${nd.length}")
		for (d <- nd) {
			val nid = d.id
			val oidOpt = od.get(d.name)
			if (oidOpt.isEmpty)
				println(s"设备找不到 ${d.name}")
			else {
				es.migrateRawData(oidOpt.get, nid, news, new DateTime(2019, 1, 1,0,0), new DateTime(2020, 1, 1,0,0), 0)
			}
		}

		println("设备数据迁移完成")

		val ss = Db.queryAllStations()
		val os = ss.filter(s => s.stid == olds).map(s => s.sname -> s.sid).toMap
		val ns = ss.filter(s => s.stid == news)

		println(s"old stations: ${os.size}  new stations:${ns.length}")
		for (s <- ns) {
			val nid = s.sid
			val oidOpt = os.get(s.sname)
			if (oidOpt.isEmpty) {
				println(s"测点找不到 ${s.sname}")
			} else {
				es.migrateThemeData(oidOpt.get, nid, news, new DateTime(2019, 1, 1,0,0), new DateTime(2020, 1, 1,0,0), 0)
			}
		}
		println("测点数据迁移完成")
	}
}

@JsonIgnoreProperties(ignoreUnknown = true)
case class deploy(total: Int, devices: Array[deployitem])

@JsonIgnoreProperties(ignoreUnknown = true)
case class deployitem(id: String, name: String);
