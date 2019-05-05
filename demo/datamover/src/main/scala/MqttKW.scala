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
		  * 229 | c70eae8f-db00-4266-a275-987b9a081d80 |             23 | 武阳前进村公厕
		  */
		val connOpt = new MqttConnectOptions()
		connOpt.setCleanSession(false)
		val client = new MqttClient("tcp://221.230.55.28:1883", "test-send-mqtt-client", new MemoryPersistence())
		client.connect(connOpt)

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

		val thingId = "c70eae8f-db00-4266-a275-987b9a081d80"
		val devices = Array(
//			285ca-286a-4ff8-becb-709c9ddbb80d","22f97f0f-5b35-4c56-931a-5861f828a8a9","4c85fda4-0a7d-45b7-8b8a-fa8b2903bbaa","c3a33940-e743-40fa-9ea3-22cd7e36315a","21b06ace-a758-4bba-90f9-e72d21f700d9","bd215b9f-d8e6-4787-be5e-214d82c1335a","aea959b8-ea26-422d-9c39-e32c6b38b506","35ab973f-29f9-4da7-bce9-d0828ad6395a","1a6943a5-f391-49d6-988d-539989c9737e","cb9cc066-d6be-4e48-86a6-8851b2626d58","fbf95a4c-4dfa-4b5a-a094-05606ef0aa54","076c096c-7fa6-4a26-902f-3763679475e7"
//			"99997f45-473b-47f5-b868-a491fa9ddf22","33a5d08b-2ece-4f8c-9d92-8719b433e21b","8cebb6d1-9003-4922-84cd-d7e2457fc567","46f01dd0-7cd2-41da-bbf1-9c9adb97619d","1531b67c-da0e-4a95-86f7-507154e935b6","dcaf7f91-e513-44f5-9315-4b1f2dd479b2","abab2fba-3f5f-4ce7-bd30-0486300eca88","15119bdb-caad-4325-b825-f8a9bb9e25f3","68d87dbe-6c04-4247-bcce-7f1d29be5ab0"
			"048685d8-eec6-4624-88c9-19d595628175"," 0caed072-eba5-4db0-b68c-cadfa766a0a4"," 1b21912a-e50f-462f-9338-5b23f2f540c7"," 5484e2d8-8c9f-42ce-a95d-a012fbe3a10e"," c89ab24b-3fc1-4661-8dff-195a75de3ab0"," 4598bbad-87b1-42da-8748-1042798178bc"," 4cb8fd09-6708-49fa-b3dc-047519f594cf"," 7cf82bec-46ba-4958-9331-fb09fe993a72"," 1eea9f63-b6c5-4ad7-bffc-96badaae35aa"
		)
		devices.foreach(d => {
			val data = IotaData("1b2d8739-627e-4b7d-9480-3eee6e9396fe",
				thingId,
				d,
				"715cc7f8-873a-48f5-a0c2-af6e8c9e89ab",
				"dd1202cd-3e51-49d5-a326-e617f9d1008c",
				new DateTime(2019, 3, 10, 9, 40), new DateTime(2019, 3, 10, 9, 40),
				IotaAcqResult(ErrorInfo(0, None, None), Some(Map(
					"pitstate" -> "0"
				)))
			)

			val line = JsonHelper.Object2Json(data)._1.get

			println(line)

			client.publish("anxinyun_data", new MqttMessage(line.getBytes("UTF-8")))

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
