package datamover;

import org.elasticsearch.ElasticsearchTimeoutException;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.joda.time.DateTime;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

//import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class EsHelper {
    public static String AGGREGATION = "anxinyun_aggregation";
    public static String THEMES = "anxinyun_themes";
    public static String RAW = "anxinyun_raws";

    public static void SetPrefix(String prefix) {
        AGGREGATION = prefix + "_aggregation";
        THEMES = prefix + "_themes";
        RAW = prefix + "_raws";
    }

    private TransportClient client = null;

    private String clusterName;
    private String nodes;
    // 重新尝试连接之前的延时(ms)
    private static final long mRetryLagMillis = 500;
    private int mBulkRequestTimeoutTryLimit = 3;
    private int mBulkRequestTimeoutMillis = 5000;

    private final int mQueryTimeoutMillis = 60000;
    private final int mQueryScrollSize = 2000;

    public int getBulkRequestTimeoutMillis() {
        return mBulkRequestTimeoutMillis;
    }

    public void setBulkRequestTimeoutMillis(int mBulkRequestTimeoutMillis) {
        this.mBulkRequestTimeoutMillis = mBulkRequestTimeoutMillis;
    }

    public int getBulkRequestTimeoutTryLimit() {
        return mBulkRequestTimeoutTryLimit;
    }

    public void setBulkRequestTimeoutTryLimit(int mBulkRequestTimeoutTryLimit) {
        this.mBulkRequestTimeoutTryLimit = mBulkRequestTimeoutTryLimit;
    }

    public void close() {
        if (client != null)
            client.close();
    }

    //clusterName: "es-savoir"
    //nodes: "10.8.30.35:9300,10.8.30.36:9300,10.8.30.37:9300"
    public boolean initHelper(String clusterName, String nodes) {
        this.clusterName = clusterName;
        this.nodes = nodes;

        System.setProperty("es.set.netty.runtime.available.processors", "false");

        List<String> lstNodes = Arrays.asList(nodes.split(","));
        if (lstNodes.size() == 0)
            return false;

        Settings settings = Settings.builder()
                .put("cluster.name", clusterName)
                .put("client.transport.sniff", true).build();
        try {
            client = new PreBuiltTransportClient(settings);
            for (String node : lstNodes) {
                String nodeAddrPort[] = node.split(":");
                if (nodeAddrPort.length != 2)
                    return false;
                client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(nodeAddrPort[0]), Integer.valueOf(nodeAddrPort[1])));
//                client.addTransportAddress(new TransportAddress(InetAddress.getByName(nodeAddrPort[0]), Integer.valueOf(nodeAddrPort[1])));
            }
            return true;
        } catch (UnknownHostException e) {

            return false;
        }
    }

    public void closeHelpler() {
        if (client != null) {
            client.close();
            client = null;
        }
    }

    public boolean indexDocs(String indexName, String typeName, List<Map<String, Object>> mapDocs) {
        if (client == null) {
            if (!initHelper(this.clusterName, this.nodes))
                return false;
        }

        try {
            BulkRequestBuilder bulkRequest = client.prepareBulk();
            for (Map<String, Object> doc : mapDocs) {
                IndexRequestBuilder request = client.prepareIndex(indexName, typeName).setSource(doc, XContentType.JSON);
                bulkRequest.add(request);
            }
            BulkResponse bulkResponse = request(bulkRequest, mBulkRequestTimeoutTryLimit, mBulkRequestTimeoutMillis);
            if (bulkResponse.hasFailures()) {
                Iterator<BulkItemResponse> itr = bulkResponse.iterator();
                while (itr.hasNext()) {
                    BulkItemResponse response = itr.next();
                }
            }
            System.out.println("indexed " + mapDocs.size());
            return !bulkResponse.hasFailures();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean upsert(String indexName, String typeName, List<Map<String, Object>> mapDocs, String... queryFields) {
        try {
            BulkRequestBuilder bulkRequest = client.prepareBulk();
            for (Map<String, Object> doc : mapDocs) {
                BoolQueryBuilder query = QueryBuilders.boolQuery();
                for (String queryField : queryFields) {
                    query = query.filter(QueryBuilders.termsQuery(queryField, doc.get(queryField)));
                }
                SearchResponse resp = client.prepareSearch()
                        .setIndices(indexName)
                        .setTypes(typeName)
                        .setQuery(query)
                        .get();
                SearchHit[] hits = resp.getHits().getHits();
                if (hits.length == 0) {
                    IndexRequestBuilder request = client.prepareIndex(indexName, typeName).setSource(doc, XContentType.JSON);
                    bulkRequest.add(request);
                } else {
                    String docId = hits[0].getId();
                    UpdateRequestBuilder request = client.prepareUpdate(indexName, typeName, docId).setDoc(doc, XContentType.JSON);//.setDocAsUpsert(true)
                    bulkRequest.add(request);
                }
            }

            BulkResponse bulkResponse = request(bulkRequest, mBulkRequestTimeoutTryLimit, mBulkRequestTimeoutMillis);
            if (bulkResponse.hasFailures()) {
                Iterator<BulkItemResponse> itr = bulkResponse.iterator();
                while (itr.hasNext()) {
                    BulkItemResponse response = itr.next();
                }
            }

            return !bulkResponse.hasFailures();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Map<String, Object>> query(SearchResponse scrollResp) {
        long readTotal = 0, totalDocs;
        List<Map<String, Object>> docs = new LinkedList<Map<String, Object>>();
        totalDocs = scrollResp.getHits().getTotalHits();

        do {
            SearchHit[] hits = scrollResp.getHits().getHits();
            readTotal += hits.length;
            for (SearchHit hit : hits) {
                Map<String, Object> source = hit.getSourceAsMap();
                docs.add(source);
            }
            if (readTotal == totalDocs) break;
            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(mQueryTimeoutMillis)).execute().actionGet();
        } while (scrollResp.getHits().getHits().length != 0);

        return docs;
    }

    public void migrateRawData(String device, String cs, int st, DateTime dtBegin, DateTime dtEnd, int hourDelay) {
        SearchResponse scrollResp = client.prepareSearch(RAW)
                .setTypes("raw")
                .setScroll(new TimeValue(mQueryTimeoutMillis))
                .setQuery(QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery("iota_device", device))
                        .must(QueryBuilders.rangeQuery("collect_time").gte(dtBegin.plusHours(-hourDelay)).lt(dtEnd.plusHours(-hourDelay))))
                .addSort("collect_time", SortOrder.ASC)
                .setSize(mQueryScrollSize).get();
        List<Map<String, Object>> ss = query(scrollResp);

        for (Map<String, Object> item : ss) {
            item.put("iota_device", cs);
            item.put("structId", st);
            if (hourDelay != 0) {
                item.put("collect_time", DateTime.parse(item.get("collect_time").toString()).plusHours(hourDelay));
            }
        }
        if (ss.size() > 0)
            indexDocs(RAW, "raw", ss);
    }

    public void migrateAggData(int stationId, int cs, DateTime dtBegin, DateTime dtEnd, int hourDelay) {
        migrateAggData(stationId, cs, dtBegin, dtEnd, hourDelay, 1);
    }

    public void migrateAggData(int stationId, int cs, DateTime dtBegin, DateTime dtEnd, int hourDelay, double coef) {
        SearchResponse scrollResp = client.prepareSearch(AGGREGATION)
                .setTypes("agg")
                .setScroll(new TimeValue(mQueryTimeoutMillis))
                .setQuery(QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery("sensor", stationId))
                        .must(QueryBuilders.rangeQuery("date").gte(dtBegin.plusHours(-hourDelay)).lt(dtEnd.plusHours(-hourDelay))))
                .addSort("date", SortOrder.ASC)
                .setSize(mQueryScrollSize).get();
        List<Map<String, Object>> ss = query(scrollResp);
        for (Map<String, Object> item : ss) {
            item.put("sensor", cs);
            if (hourDelay != 0) {
                item.put("date", DateTime.parse(item.get("date").toString()).plusHours(hourDelay));
            }

            if (coef != 1) {
                Map<String, Object> dt = (Map<String, Object>) item.get("data");
                Set<String> keys = dt.keySet();
                for (String k : keys) {
                    dt.put(k, Double.parseDouble(dt.get(k).toString()) * coef);
                }
                item.put("data", dt);
            }
        }
        if (ss.size() > 0)
            indexDocs(AGGREGATION, "agg", ss);
    }

    public void migrateThemeData(int stationId, int cs, int st, DateTime dtBegin, DateTime dtEnd, int hourDelay) {
        migrateThemeData(stationId, cs, st, dtBegin, dtEnd, hourDelay, 1);
    }

    public void migrateThemeData(int stationId, int cs, int st, DateTime dtBegin, DateTime dtEnd, int hourDelay, double coef) {
        SearchResponse scrollResp = client.prepareSearch(THEMES)
                .setTypes("theme")
                .setScroll(new TimeValue(mQueryTimeoutMillis))
                .setQuery(QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery("sensor", stationId))
                        .must(QueryBuilders.rangeQuery("collect_time").gte(dtBegin.plusHours(-hourDelay)).lt(dtEnd.plusHours(-hourDelay))))
                .addSort("collect_time", SortOrder.ASC)
                .setSize(mQueryScrollSize).get();
        List<Map<String, Object>> ss = query(scrollResp);
        for (Map<String, Object> item : ss) {
            item.put("sensor", cs);
            item.put("structure", st);
            if (hourDelay != 0) {
                item.put("collect_time", DateTime.parse(item.get("collect_time").toString()).plusHours(hourDelay));
            }
            if (coef != 1) {
                Map<String, Object> dt = (Map<String, Object>) item.get("data");
                Set<String> keys = dt.keySet();
                for (String k : keys) {
                    dt.put(k, Double.parseDouble(dt.get(k).toString()) * coef);
                }
                item.put("data", dt);
            }
        }
        if (ss.size() > 0)
            indexDocs(THEMES, "theme", ss);
    }

    public long delete(String indexName, String typeName, QueryBuilder query) {
        BulkByScrollResponse response = DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .filter(query)
                .source(indexName)
                .get();
        long deleted = response.getDeleted();
        return deleted;
    }

    /**
     * es索引请求； 超时允许重试
     *
     * @param bulkRequest   bulkrRequest
     * @param tryLimit      尝试次数
     * @param timeoutMillis 超时时长(ms)
     * @return
     */
    private BulkResponse request(BulkRequestBuilder bulkRequest, int tryLimit, int timeoutMillis) throws InterruptedException {
        try {
            return bulkRequest.get(TimeValue.timeValueMillis(timeoutMillis));
        } catch (ElasticsearchTimeoutException ete) {
            tryLimit--;
            if (tryLimit <= 0) throw ete;
            Thread.sleep(mRetryLagMillis);
            return request(bulkRequest, tryLimit, timeoutMillis);
        }
    }

    public List<Map<String, Object>> queryThemeData(int structId, int factor, DateTime begin, DateTime end) {
        SearchResponse sr = client.prepareSearch(THEMES)
                .setTypes("theme")
                .setScroll(new TimeValue(mQueryTimeoutMillis))
                .setQuery(QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery("structure", structId))
                        .must(QueryBuilders.termQuery("factor", factor))
                        .must(QueryBuilders.rangeQuery("collect_time").gte(begin).lt(end)))
                .addSort("collect_time", SortOrder.ASC)
                .setSize(mQueryScrollSize).get();
        return query(sr);
    }
}
