package datamover;

import org.joda.time.DateTime;

public class Mover {

    public static void main(String[] args) {

        EsHelper es = new EsHelper();

        es.initHelper("anxin-cloud", "anxinyun-m2:9300,anxinyun-n1:9300,anxinyun-n2:9300,anxinyun-n3:9300");

        DateTime dt1 = new DateTime(2019, 9, 9, 0, 0);
        DateTime dt2 = new DateTime(2019, 9, 14, 11, 0);
        int hourDelay = 0;
        int structId = 176;

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

        /*
        192 | 95818a5f-c317-4069-a30f-f0f08dccca88

         */
//        es.migrateRawData("73fa4fbb-6ed1-426c-8e84-13670588f871", "eb71d42c-772e-4de0-8e1a-0dfcbf3c1025", structId, dt1, dt2, hourDelay);
//        es.migrateRawData("a003fb16-a029-4f59-909b-22417dbc5312", "bff1c82a-6483-411f-a7e5-f539c751e70c", structId, dt1, dt2, hourDelay);
//        es.migrateRawData("dbe4d5f5-a238-4089-83f4-331bbd04c971", "c95ccc34-f091-40ef-953e-3260d8f51959", structId, dt1, dt2, hourDelay);

        // 水
        es.migrateThemeData(475, 1167, structId, dt1, dt2, hourDelay,1.1);
        // 电
        es.migrateThemeData(471, 1166, structId, dt1, dt2, hourDelay,1.1);
        // 人
//        es.migrateThemeData(211, 817, structId, dt1, dt2, hourDelay);
        // 环
//        es.migrateThemeData(212, 830, structId, dt1, dt2, hourDelay);

        es.migrateAggData(475, 1167, dt1, dt2, hourDelay,1.1);
        es.migrateAggData(471, 1166, dt1, dt2, hourDelay,1.1);
//        es.migrateAggData(211, 817, dt1, dt2, hourDelay);
//        es.migrateAggData(212, 830, dt1, dt2, hourDelay);

        System.out.println("finished");
    }
}
