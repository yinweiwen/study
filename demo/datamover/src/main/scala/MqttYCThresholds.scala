import comm.models.{ErrorInfo, IotaAcqResult, IotaData}
import comm.utils.JsonHelper
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.eclipse.paho.client.mqttv3.{MqttClient, MqttConnectOptions, MqttMessage}
import org.joda.time.DateTime

import scala.math.BigDecimal.RoundingMode
import scala.util.Random

/**
  * Created by yww08 on 2019/1/16.
  */
object MqttYCThresholds {

	def main(args: Array[String]): Unit = {

		/**
		  * -----+--------------------------------------+--------------------+--------+--------+-----+-----------+------+--------+----------+--------+-------------+---------------------------------------------------------------------------------------------
		  * 898 | b2158a50-4152-4504-9088-35c7b9f3daec |                  0 |    693 | {}     | 693 |       202 | 扬尘 |    250 |          | {}     | f           | {"latitude": 28.88402, "longitude": 115.38859, "stationTypeId": "1", "divisionTypeId": "4"}
		  *
		  */
		val connOpt = new MqttConnectOptions()
		connOpt.setCleanSession(false)
		val client = new MqttClient("tcp://221.230.55.28:1883", "test-send-mqtt-client", new MemoryPersistence())
		client.connect(connOpt)

		val thingId = "940c71fb-b561-4ae9-994a-e1417d3af004"
		val deviceId = "b2158a50-4152-4504-9088-35c7b9f3daec"

		val time = new DateTime(2019, 1, 10, 0, 0)

		var line = JsonHelper.Object2Json(IotaData("1b2d8739-627e-4b7d-9480-3eee6e9396fe",
			thingId,
			deviceId,
			"715cc7f8-873a-48f5-a0c2-af6e8c9e89ab",
			"dd1202cd-3e51-49d5-a326-e617f9d1008c",
			new DateTime(2019, 1, 14, 18, 30), time,
			IotaAcqResult(ErrorInfo(0, None, None), Some(Map(

				"pm25" -> 32.6,
				"pm10" -> 23.2,
				"noise" -> 45.1,
				"temperature" -> 3.5,
				"humidity" -> 96.1,
				"speed" -> 0,
				"direction" -> 233
			)))
		))._1.get

		println(line)

		client.publish("anxinyun_data", new MqttMessage(line.getBytes("UTF-8")))


		//		val source = Source.fromInputStream(getClass.getResourceAsStream("/mqtts.txt"))
		//		val lines = source.mkString.split("\r\n")
		//		lines.sliding(20).foreach(dts => {
		//			dts.foreach(d => {
		//				if (d != null && d.length > 0)
		//					client.publish("anxinyun_data", new MqttMessage(d.getBytes("UTF-8")))
		//			})
		//			Thread.sleep(5 * 1000)
		//		})

		client.disconnect()
		client.close()
	}
}
