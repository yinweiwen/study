package cn.com.free_sun.units4s

import cn.com.free_sun.units4s.dimension.dimension

/**
  * Created by yww08 on 2018-12-05.
  */
case class unit(name: String, coef: Double, d: dimension) {

}
//μ用u代替
object repository {
  val units = Array(
    // --长度
    // 公制
    ("m", 1, dimension.length,"米"),
    ("km", 1e3, dimension.length,"千米"),
    ("dm", 0.1, dimension.length,"分米"),
    ("cm", 0.01, dimension.length,"厘米"),
    ("mm", 1e-3, dimension.length,"毫米"),
    ("um", 1e-6, dimension.length,"微米"),
    ("nm", 1e-9, dimension.length,"纳米"),
    ("pm", 1e-12, dimension.length,"皮米"),
    ("ly", 9.4607e+15, dimension.length,"光年"),
    ("AU", 1.496e+11, dimension.length,"天文单位"),
    // 英制
    ("in", 0.0254, dimension.length,"英寸,inch"),
    ("ft", 0.3048, dimension.length,"英尺"), //英尺
    ("yd", 0.9144, dimension.length,"码"), //码
    ("mi", 1609.344, dimension.length,"英里"), //英里
    ("nmi", 1852, dimension.length,"海里"), //海里
    ("fm", 1.8288, dimension.length,"英寻"), //英寻
    ("fur", 201.168, dimension.length,"弗隆"), //弗隆
    // 市制
    ("里", 500, dimension.length,"里"),
    ("丈", 3.3333333, dimension.length,"丈"),
    ("尺", 0.3333333, dimension.length,"尺"),
    ("寸", 0.0333333, dimension.length,"寸"),
    ("分", 0.0033333, dimension.length,"分"),
    ("厘", 0.0003333, dimension.length,"厘"),
    ("毫", 0.0000333, dimension.length,"毫"),

    // --质量
    // 公制
    ("kg",1,dimension.weight,"千克,公斤"),
    ("g",0.001,dimension.weight,"克"),
    ("mg",1e-6	,dimension.weight,"毫克"),
    ("ug",1e-9	,dimension.weight,"微克"),
    ("t",1000,dimension.weight,"吨"),
    ("q",100,dimension.weight,"公担"),
    ("ct",0.0002,dimension.weight,"克拉"),
    ("point",2e-6,dimension.weight,"分"),
    // 英制
    ("dwt",1.36078,dimension.weight,"英钱"),//英钱
    ("oz",0.0283495	,dimension.weight,"盎司"),// 盎司
    ("lb",0.4535924,dimension.weight,"磅"),//磅
    ("dr",0.0017718,dimension.weight,"打兰"),//打兰
    // 市制
    ("两",0.05,dimension.weight,"两"),
    ("钱",0.005,dimension.weight,"钱"),
    ("斤",0.5,dimension.weight,"斤,市斤"),

    // --时间
    ("yr",31536000,dimension.time,"年,year"),
    ("week",604800,dimension.time,"周,week"),
    ("d",86400,dimension.time,"天,day"),
    ("h",3600,dimension.time,"时,小时,hour"),
    ("min",60,dimension.time,"分,分钟,minute"),
    ("s",1,dimension.time,"秒,second"),
    ("ms",0.001,dimension.time,"毫秒"),
    ("us",1e-6,dimension.time,"微秒"),//μ用u代替
    ("ns",1e-9,dimension.time,"纳秒"),

    // --电流
    ("A",1,dimension.current,"安"),
    ("kA",1e3,dimension.current,"千安"),
    ("MA",1e6,dimension.current,"兆安"),
    ("GA",1e9,dimension.current,""),
    ("mA",1e-3,dimension.current,"毫安"),
    ("uA",1e-6,dimension.current,"微安"),
    ("nA",1e-9,dimension.current,"纳安"),
    ("C/s",0.1,dimension.current,"")
  )
}

object dimension extends Enumeration {
  type dimension = Value
  val length, weight,time,current,volume = Value
}