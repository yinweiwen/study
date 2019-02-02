package datamover;

import org.joda.time.DateTime;

public class Mover {

    public static void main(String[] args) {

        EsHelper es = new EsHelper();

        es.initHelper("anxin-cloud", "anxinyun-m2:9300,anxinyun-n1:9300,anxinyun-n2:9300,anxinyun-n3:9300");

        DateTime dt1 = new DateTime(2019, 01, 28, 0, 0);
        DateTime dt2 = new DateTime(2019, 02, 03, 10, 0);
        int hourDelay = 24;
        int structId = 158;

        /**
         * id  |            iota_device_id            | iota_device_serial | sensor | params
         -----+--------------------------------------+--------------------+--------+--------
         295 | 73fa4fbb-6ed1-426c-8e84-13670588f871 |                  0 |    215 | {}
         296 | a003fb16-a029-4f59-909b-22417dbc5312 |                  0 |    216 | {}
         725 | bff1c82a-6483-411f-a7e5-f539c751e70c |                  0 |    550 | {}
         289 | dbe4d5f5-a238-4089-83f4-331bbd04c971 |                  0 |    211 | {}
         715 | c95ccc34-f091-40ef-953e-3260d8f51959 |                  0 |    541 | {}
         724 | eb71d42c-772e-4de0-8e1a-0dfcbf3c1025 |                  0 |    549 | {}
         */
//        es.migrateRawData("73fa4fbb-6ed1-426c-8e84-13670588f871", "eb71d42c-772e-4de0-8e1a-0dfcbf3c1025", structId, dt1, dt2, hourDelay);
//        es.migrateRawData("a003fb16-a029-4f59-909b-22417dbc5312", "bff1c82a-6483-411f-a7e5-f539c751e70c", structId, dt1, dt2, hourDelay);
//        es.migrateRawData("dbe4d5f5-a238-4089-83f4-331bbd04c971", "c95ccc34-f091-40ef-953e-3260d8f51959", structId, dt1, dt2, hourDelay);

        // 水
        es.migrateThemeData(215, 814, structId, dt1, dt2, hourDelay);
        // 电
        es.migrateThemeData(216, 813, structId, dt1, dt2, hourDelay);
        // 人
//        es.migrateThemeData(211, 541, structId, dt1, dt2, hourDelay);

        es.migrateAggData(215, 814, dt1, dt2, hourDelay);
        es.migrateAggData(216, 813, dt1, dt2, hourDelay);
//        es.migrateAggData(211, 541, dt1, dt2, hourDelay);

        System.out.println("finished");
    }
}
