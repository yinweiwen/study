
### start et in 10.8.30.99, script work dir is /home/yww/tes
### 20170911
echo <zookeeper>
./zookeeper-3.4.10/bin/zkServer.sh start

echo <kafka>
nohup ./kafka_2.11-0.11.0.0/bin/kafka-server-start.sh ./kafka_2.11-0.11.0.0/config/server.properties 1>/dev/null 2>&1 &
# ./kafka_2.11-0.11.0.0/bin/kafka-server-start.sh  -daemon  ./kafka_2.11-0.11.0.0/config/server.propertie
## ssh-keygen -t rsa -P ""
## cat $HOME/.ssh/id_rsa.pub >> $HOME/.ssh/authorized_keys
ssh localhost
cd tes
echo <hadoop>
./hadoop-2.7.4/sbin/start-all.sh

echo <ES>
./elasticsearch-5.5.2/bin/elasticsearch start -d

echo <ET>
./spark-2.1.1/bin/spark-submit --master yarn-cluster ./et/et-1.0-SNAPSHOT-jar-with-dependencies.jar 