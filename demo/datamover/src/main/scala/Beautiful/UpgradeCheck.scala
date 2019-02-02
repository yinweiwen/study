package Beautiful

import java.sql.{Connection, ResultSet, Statement}

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import comm.utils.{Lazing, Loader}
import datamover.EsHelper
import org.apache.commons.dbcp2.BasicDataSource
import org.joda.time.DateTime

import scala.collection.JavaConversions
import scala.collection.mutable.ArrayBuffer
import scala.util.Try

/**
  * Created by yww08 on 2019/1/22.
  */
object UpgradeCheck {

	def main(args: Array[String]): Unit = {
		if (args.length < 1) {
			println(
				"""
				  |args:
				  | upgradeTime : yyyy-MM-ddTHH:mm:ss.SSSZ
				  | startTime [Option] : yyyy-MM-ddTHH:mm:ss.SSSZ
				  | endTime [Option] : yyyy-MM-ddTHH:mm:ss.SSSZ
				""".stripMargin)
			return
		}
		val upgradeTime = DateTime.parse(args(0))
		val startTime = if (args.length > 1) DateTime.parse(args(1)) else upgradeTime.plusDays(-1)
		val endTime = if (args.length > 2) DateTime.parse(args(2)) else upgradeTime.plusDays(1)

		val props = Loader.from("/config.properties", args: _*)

		EsHelper.SetPrefix(props.getProperty("platform.prefix"))
		val es = new EsHelper()
		es.initHelper(props.getProperty("es.cluster"), props.getProperty("es.nodes"))

		import scala.collection.JavaConversions._
		val stations = Db.queryAllStations()
		val total = stations.length
		var done = 0
		var lsResult = new ArrayBuffer[OFaultResult]()
		stations.groupBy(_.stid).foreach(stgp => {
			val stid = stgp._1
			stgp._2.groupBy(_.factor).foreach(fgp => {
				done += fgp._2.length
				printProgress(done, total)

				val factor = fgp._1
				val infoh = fgp._2.head
				println(s"check [${infoh.stname}]-[${infoh.factor}]")

				val docs = es.queryThemeData(stid, factor, startTime, endTime)

				val datas = JavaConversions.asScalaBuffer(docs.map(d => JavaConversions.mapAsScalaMap(d).toMap))
					.map(d => ThemeData.from(d)).filter(_ != null).toArray

				if (datas.length > 0) {
					val leftLen = datas.prefixLength(_.acqTime.isBefore(upgradeTime))
					val rightLen = datas.length - leftLen
					if (rightLen <= 0) {
						lsResult.append(OFaultResult(infoh, "中断"))
					} else {
						val sid = datas.head.sensor
						val dds = datas.filter(_.sensor == sid)
						val ll = dds.prefixLength(_.acqTime.isBefore(upgradeTime))

						if (ll > 2 && ll < dds.length) {
							val leftDatas = dds.take(ll)
							val metaks = dds.head.data.keys
							val ppv = metaks.map(k => {
								val kv = leftDatas.map(_.getD(k))
								k -> (kv.max - kv.min)
							}).toMap
							val latestOld = leftDatas.last
							val topnew = dds(ll)
							if (metaks.exists(k => {
								Math.abs(topnew.getD(k) - latestOld.getD(k)) > (ppv(k) * 1.30)
							})) {
								lsResult.append(OFaultResult(infoh, s"突变 (检查测点-$sid)"))
							}
						}
					}
				}
			}) // group by factor
		}) // group by structure

		println("check finished.")
		if (lsResult.isEmpty) {
			println("all structure data consistence, grateful upgrade")
		} else {
			println(s"total ${lsResult.length} structure-factor may have problems:")
			lsResult.foreach(r => {
				println(s"${r.station.stid}-${r.station.stname}\t|${r.station.factor}\t|${r.fault}")
			})
		}
	}

	def printProgress(cur: Int, total: Int): Unit = {
		val num = 100
		val n = cur * 1d / total * num
		val sb = new StringBuilder
		sb.append(f"$n%2.2f%%")
		for (i <- 0 until num) {
			if (i < n)
				sb.append(">")
			else
				sb.append(" ")
		}
		sb.append("|")
		println(sb.toString())
	}
}

@JsonIgnoreProperties(ignoreUnknown = true)
case class ThemeData(sensor: Int, acqTime: DateTime, data: Map[String, Any]) {
	def getD(field: String): Double = {
		Try(data(field) match {
			case n: Number => n.doubleValue()
			case s: String => s.toDouble
		}).getOrElse(0d)
	}
}

object ThemeData {
	val field_sensor = "sensor"
	val field_time = "collect_time"
	val field_data = "data"

	def from(doc: Map[String, AnyRef]): ThemeData = {
		try {
			val sensor = doc(field_sensor).asInstanceOf[Int]
			val acqTime = DateTime.parse(doc(field_time).toString)
			val dj = JavaConversions.mapAsScalaMap(doc(field_data).asInstanceOf[java.util.HashMap[String, Object]])
			ThemeData(sensor, acqTime, dj.map(d => (d._1, d._2)).toMap)
		} catch {
			case e: Exception => null
		}
	}
}


object Db extends Lazing {
	val bds = new BasicDataSource
	bds.setDriverClassName("org.postgresql.Driver")
	bds.setUrl(props.getProperty("db.url"))
	bds.setUsername(props.getProperty("db.user"))
	bds.setPassword(props.getProperty("db.pwd"))
	bds.setInitialSize(1)

	def queryAllStations(): Array[OStation] = {
		val conn: Connection = bds.getConnection()
		val stmt: Statement = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
		val rss: ResultSet = stmt.executeQuery(
			"select st.id as stid,st.iota_thing_id,st.name as stname,s.id as sid,s.name as sname,s.factor from t_structure st,t_sensor s where st.id=s.structure and s.manual_data=FALSE;")
		val sts: ArrayBuffer[OStation] = ArrayBuffer[OStation]()
		while (rss.next()) {
			sts append OStation(rss.getInt("stid"),
				rss.getString("stname"),
				rss.getString("iota_thing_id"),
				rss.getInt("sid"),
				rss.getString("sname"),
				rss.getInt("factor"))
		}

		sts.toArray
	}
}

case class OStation(stid: Int, stname: String, thingId: String,
                    sid: Int, sname: String,
                    factor: Int) {
}

case class OFaultResult(station: OStation, fault: String)