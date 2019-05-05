package datamover;

import org.joda.time.DateTime;

/**
 * Created by yww08 on 2019/1/16.
 */
public class MoverXu {

    public static void main(String[] args) {

        long hours = (new DateTime(2019, 2, 25, 0, 0).getMillis() -
                new DateTime(2019, 1, 28, 0, 0).getMillis()) / (60 * 60 * 1000);

        System.out.println(hours);

        // DateTime dt1 = new DateTime(2018, 01, 01, 0, 0);
        // DateTime dt2 = new DateTime(2019, 01, 15, 0, 0);
        // int hourDelay = 7 * 24;
        // int structId = 194;

        /**
         * id | iota_device_id | iota_device_serial | sensor | params
         * -----+--------------------------------------+--------------------+--------+--------
         * 295 | 73fa4fbb-6ed1-426c-8e84-13670588f871 | 0 | 215 | {} 296 |
         * a003fb16-a029-4f59-909b-22417dbc5312 | 0 | 216 | {} 725 |
         * bff1c82a-6483-411f-a7e5-f539c751e70c | 0 | 550 | {} 289 |
         * dbe4d5f5-a238-4089-83f4-331bbd04c971 | 0 | 211 | {} 715 |
         * c95ccc34-f091-40ef-953e-3260d8f51959 | 0 | 541 | {} 724 |
         * eb71d42c-772e-4de0-8e1a-0dfcbf3c1025 | 0 | 549 | {}
         */
        // es.migrateRawData("73fa4fbb-6ed1-426c-8e84-13670588f871",
        // "eb71d42c-772e-4de0-8e1a-0dfcbf3c1025", structId, dt1, dt2, hourDelay);
        // es.migrateRawData("a003fb16-a029-4f59-909b-22417dbc5312",
        // "bff1c82a-6483-411f-a7e5-f539c751e70c", structId, dt1, dt2, hourDelay);
        // es.migrateRawData("dbe4d5f5-a238-4089-83f4-331bbd04c971",
        // "c95ccc34-f091-40ef-953e-3260d8f51959", structId, dt1, dt2, hourDelay);

        // 青山桥倾斜CX3：31、CX2：28
        DateTime dt1 = new DateTime(2019, 2, 22, 0, 40);
        DateTime dt2 = new DateTime(2019, 2, 28, 13, 00);
        int hourDelay = 4920;
        System.out.println(dt1 + " ~ " + dt2);

        EsHelper es = new EsHelper();

        es.initHelper("anxin-cloud", "anxinyun-m2:9300,anxinyun-n1:9300,anxinyun-n2:9300,anxinyun-n3:9300");
//        es.migrateThemeData(31, 684, 199, dt1, dt2, hourDelay);
//        es.migrateThemeData(28, 685, 199, dt1, dt2, hourDelay);

        // 路长乡政府734
        dt1 = new DateTime(2019, 2, 25, 0, 30);
        dt2 = new DateTime(2019, 2, 28, 13, 0);
        int hd2=672;
        es.migrateThemeData(734, 693, 202, dt1, dt2, hd2);
        es.migrateThemeData(734, 689, 200, dt1, dt2, hd2);

        // es.migrateAggData(215, 549, dt1, dt2, hourDelay);
        // es.migrateAggData(216, 550, dt1, dt2, hourDelay);
        // es.migrateAggData(211, 541, dt1, dt2, hourDelay);

        System.out.println("finished");
    }
}
