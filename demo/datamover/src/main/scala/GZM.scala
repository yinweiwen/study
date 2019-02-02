import java.sql.DriverManager

/**
  * Created by yww08 on 2019-01-07.
  */
object GZM {

	def main(args: Array[String]): Unit = {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
		val connStr = "jdbc:sqlserver://10.8.30.32:1433;databaseName=DW_iSecureCloud_Empty_20181118"

		val dbConn = DriverManager.getConnection(connStr, "sa", "123")

		val stmt = dbConn.createStatement()
		val rs = stmt.executeQuery("select * from T_DIM_SENSOR")

		while (rs != null && rs.next()) {
			val sid = rs.getInt("SENSOR_ID")
			println(sid)
		}

		dbConn.close()
	}
}