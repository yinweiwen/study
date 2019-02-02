import comm.models.StationData
import comm.utils.{ConfigHelper, StorageConsumer}
import datamover.EsHelper
import org.joda.time.DateTime

/**
  * Created by yww08 on 2019/1/16.
  */
object LGJX {


	def main(args: Array[String]): Unit = {


		val es = new EsHelper()

		es.initHelper("anxin-cloud", "anxinyun-m1:9300,anxinyun-n1:9300,anxinyun-n2:9300")

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

		// 电表数据
		var datas = Array(
			DData("bff1c82a-6483-411f-a7e5-f539c751e70c", DateTime.parse("2019-01-10T00:00:00.000+0800"), Map("total"->1))
		)
		val sid = ConfigHelper.getDeviceStationIds("bff1c82a-6483-411f-a7e5-f539c751e70c")(0)
		val station = ConfigHelper.getStationInfo(sid).get

		StorageConsumer.saveStationData(datas.map(data => StationData(station, data.time, null, Some(data.data))))
	}


	case class DData(deviceId: String, time: DateTime, data: Map[String, Any])

}
