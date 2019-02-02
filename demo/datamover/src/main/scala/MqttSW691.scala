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
object MqttSW691 {

	def main(args: Array[String]): Unit = {
		// 200
		// 691 --> 735

		// 830c705c-b1ed-4244-831d-973d12e53ab2  737  6700
		// 7595f123-ae20-4bbb-be36-c7d525ebfdec  735  2500
		/**
		  * 899 | ecba90f8-10c0-4c8d-9e49-064342059cb0 |                  0 |    688 | {}     | 688 |       200 | 雨量     |      3 | image/8049d0e2-c8d6-4545-abde-36d6681f102b/image.png | {}     | f           |
		  * 902 | 964ace0a-2fde-44a5-bdae-21c44d12dcc0 |                  0 |    689 | {}     | 689 |       200 | 生态环境 |    250 |                                                      | {}     | f           | {"latitude": 28.85585, "longitude": 115.37817, "stationTypeId": "1", "divisionTypeId": "4"}
		  * 900 | 5f83b25c-55a4-4600-b0e6-4004be0dfedd |                  0 |    691 | {}     | 691 |       200 | 水位     |     31 | image/5d64b984-73f3-4922-8136-03835fd3aac9/image.png | {}     | f           |
		  * 896 | de701ade-350c-4d63-922e-b9516fa1cc57 |                  0 |    690 | {}     | 690 |       200 | 水质     |    256 |                                                      | {}     | f           | {"latitude": 28.87694, "longitude": 115.385928, "divisionTypeId": "4", "fractureTypeId": "9"}
		  *
		  */
		val connOpt = new MqttConnectOptions()
		connOpt.setCleanSession(false)
		val client = new MqttClient("tcp://221.230.55.28:1883", "test-send-mqtt-client", new MemoryPersistence())
		client.connect(connOpt)

		val thingId = "eda323af-b141-4975-9a90-22b7f975485f"
		val deviceId = "830c705c-b1ed-4244-831d-973d12e53ab2"

		val start = new DateTime(2019, 1, 22, 0, 0)
		val end = new DateTime(2019, 1, 31, 13, 10)
		val stepHour = 1
		val stepVal: Double = 6.7 // mm 6700

		val randVal: Double = 0.1

		val precision = 2

		val skipBeginHour = 24
		val skipEndHour = 0

		var time = start
		while (time.isBefore(end)) {
			if (time.getHourOfDay <= skipBeginHour && time.getHourOfDay >= skipEndHour) {

				val water = BigDecimal(stepVal + (Random.nextDouble() - 0.5) * randVal * 2).setScale(precision, RoundingMode.HALF_UP).toDouble

				val data = IotaData("1b2d8739-627e-4b7d-9480-3eee6e9396fe",
					thingId,
					deviceId,
					"715cc7f8-873a-48f5-a0c2-af6e8c9e89ab",
					"dd1202cd-3e51-49d5-a326-e617f9d1008c",
					time, time,
					IotaAcqResult(ErrorInfo(0, None, None), Some(Map(
						"length" -> water
					)))
				)

				val line = JsonHelper.Object2Json(data)._1.get

				println(line)

				client.publish("anxinyun_data2", new MqttMessage(line.getBytes("UTF-8")))

				Thread.sleep(100)
			}

			time = time.plusHours(stepHour)

		}


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
