import comm.models.{ErrorInfo, IotaAcqResult, IotaData}
import comm.utils.JsonHelper
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.eclipse.paho.client.mqttv3.{MqttClient, MqttConnectOptions, MqttMessage}
import org.joda.time.DateTime

/**
  * Created by yww08 on 2019/1/16.
  */
object MqttKW {

	def main(args: Array[String]): Unit = {
		// 194
		/**
		  * id  |            iota_device_id            | iota_device_serial | sensor | params | id  | structure |     name     | factor | portrait | labels | manual_data | extras
		  * -----+--------------------------------------+--------------------+--------+--------+-----+-----------+--------------+--------+----------+--------+-------------+--------
		  * 725 | bff1c82a-6483-411f-a7e5-f539c751e70c |                  0 |    550 | {}     | 550 |       194 | 电表         |    259 |          | {}     | f           |
		  * 717 | 05903f9e-0e06-4549-bd93-568a1455de66 |                  0 |    543 | {}     | 543 |       194 | 女厕1#（蹲） |    262 |          | {}     | f           |
		  * 718 | 44eb6790-3936-486f-918d-d80da9904191 |                  0 |    544 | {}     | 544 |       194 | 女厕2#（蹲） |    262 |          | {}     | f           |
		  * 719 | 93009e0a-1081-4cb4-8e60-ccd971d99a5e |                  0 |    545 | {}     | 545 |       194 | 女厕3#（蹲） |    262 |          | {}     | f           |
		  * 720 | 00d0a409-691b-4052-85ff-3c9b63dc1c74 |                  0 |    546 | {}     | 546 |       194 | 男厕1#（蹲） |    262 |          | {}     | f           |
		  * 723 | 3279ccca-2d4d-4b73-a477-d60396a14242 |                  0 |    542 | {}     | 542 |       194 | 厕所环境     |    263 |          | {}     | f           |
		  * 715 | c95ccc34-f091-40ef-953e-3260d8f51959 |                  0 |    541 | {}     | 541 |       194 | 厕所人流量   |    261 |          | {}     | f           |
		  * 721 | 039e2412-770e-4099-b97d-b21ff138c9f7 |                  0 |    547 | {}     | 547 |       194 | 男厕2#（蹲） |    262 |          | {}     | f           |
		  * 722 | 2d366e3d-765c-4119-b970-1c56f7c72591 |                  0 |    548 | {}     | 548 |       194 | 男厕3#（蹲） |    262 |          | {}     | f           |
		  * 724 | eb71d42c-772e-4de0-8e1a-0dfcbf3c1025 |                  0 |    549 | {}     | 549 |       194 | 水表         |    260 |          | {}     | f           |
		  *
		  */
		val connOpt = new MqttConnectOptions()
		connOpt.setCleanSession(false)
		val client = new MqttClient("tcp://221.230.55.28:1883", "test-send-mqtt-client", new MemoryPersistence())
		client.connect(connOpt)

		val thingId = "9b1f09da-372b-4e10-9f58-ae4a84d1a66e"
		// 用电
		//		val thingId = "9b1f09da-372b-4e10-9f58-ae4a84d1a66e"
		//		val deviceId = "bff1c82a-6483-411f-a7e5-f539c751e70c"
		//		val key = "readingNumber"
		//		val start = new DateTime(2019, 1, 10, 1, 0)
		//		val end = new DateTime(2019, 1, 18, 1, 0)
		//		val stepHour = 1
		//		val startVal = 245.6
		//		val stepVal = 0.25
		//		val randVal = 0.1
		//		val precision = 2

		val devices = Array(
			"05903f9e-0e06-4549-bd93-568a1455de66",
			"44eb6790-3936-486f-918d-d80da9904191",
			"93009e0a-1081-4cb4-8e60-ccd971d99a5e",
			"00d0a409-691b-4052-85ff-3c9b63dc1c74",
			"039e2412-770e-4099-b97d-b21ff138c9f7",
			"2d366e3d-765c-4119-b970-1c56f7c72591"
		)
		devices.foreach(d => {
			val data = IotaData("1b2d8739-627e-4b7d-9480-3eee6e9396fe",
				thingId,
				d,
				"715cc7f8-873a-48f5-a0c2-af6e8c9e89ab",
				"dd1202cd-3e51-49d5-a326-e617f9d1008c",
				new DateTime(2019, 1, 16, 12, 0), new DateTime(2019, 1, 16, 12, 0),
				IotaAcqResult(ErrorInfo(0, None, None), Some(Map(
					"pitstate" -> "0"
				)))
			)

			val line = JsonHelper.Object2Json(data)._1.get

			println(line)

			client.publish("anxinyun_data2", new MqttMessage(line.getBytes("UTF-8")))

			Thread.sleep(100)
		})


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
