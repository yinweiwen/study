验证：

root@iota-m2:/usr/hdp/current/kafka-broker/bin# ./kafka-console-producer.sh --broker-list iota-m2:6667 --topic Registry3

./kafka-console-consumer.sh --bootstrap-server iota-m2:6667 --topic Registry3



更换：

root@anxin-m1:/home/anxin/iota/k8s# vi config-product/iota-proxy.yaml 

root@anxin-m1:/home/anxin/iota/k8s# vi config-product/iota-dac.yaml

root@anxin-m1:/home/anxin/iota/k8s# kubectl delete pod -n iota iota-proxy-787f4cf968-rz7h2