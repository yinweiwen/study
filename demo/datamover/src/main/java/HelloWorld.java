import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by yww08 on 2018-12-26.
 */
public class HelloWorld {
    public static void main(String[] args) {

        EsHelper es=new EsHelper();

        es.initHelper("anxin-cloud","anxinyun-m1:9300,anxinyun-n1:9300,anxinyun-n2:9300");

        DateTime dt1=new DateTime(2018,12,26,22,0);
        DateTime dt2=new DateTime(2018,12,28,0,0);

        es.queryRawData("73fa4fbb-6ed1-426c-8e84-13670588f871","118cfadc-a959-4ebf-a2ac-836cd7ffb255",112,dt1,dt2);
        es.queryRawData("a003fb16-a029-4f59-909b-22417dbc5312","55914fff-ef4a-4f12-8f96-823be75156af",112,dt1,dt2);

        es.queryThemeData(215,475,112,dt1,dt2);
        es.queryThemeData(216,471,112,dt1,dt2);
        es.queryThemeData(212,339,112,dt1,dt2);

        es.queryAggData(215,475,dt1,dt2);
        es.queryAggData(216,471,dt1,dt2);
        es.queryAggData(212,339,dt1,dt2);

        System.out.println("finished");
    }
}
