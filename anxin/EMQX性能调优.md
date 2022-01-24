## EMQX性能调优

安装Erlang：https://www.erlang.org/downloads

下载 **[emqtt-bench](https://github.com/emqx/emqtt-bench)**

>  PUB SUB QOS的关系 **是服务器只会按pub和sub两者qos等级最小的那个qos规则来发送消息。**

部署麻烦放弃。



使用docker：

```sh
 docker run -it emqx/emqtt-bench pub --help
 
 Usage: emqtt_bench pub [--help <help>] [-h [<host>]] [-p [<port>]]
                       [-V [<version>]] [-c [<count>]]
                       [-n [<startnumber>]] [-i [<interval>]]
                       [-I [<interval_of_msg>]] [-u <username>]
                       [-P <password>] [-t <topic>] [-s [<size>]]
                       [-q [<qos>]] [-r [<retain>]] [-k [<keepalive>]]
                       [-C [<clean>]] [-x [<expiry>]] [-L [<limit>]]
                       [-S [<ssl>]] [--certfile <certfile>]
                       [--keyfile <keyfile>] [--ws [<ws>]]
                       [--quic [<quic>]] [--ifaddr <ifaddr>]
                       [--prefix <prefix>] [-l <lowmem>]

  --help                 help information
  -h, --host             mqtt server hostname or IP address [default:
                         localhost]
  -p, --port             mqtt server port number [default: 1883]
  -V, --version          mqtt protocol version: 3 | 4 | 5 [default: 5]
  -c, --count            max count of clients [default: 200]
  -n, --startnumber      start number [default: 0]
  -i, --interval         interval of connecting to the broker [default: 10]
  -I, --interval_of_msg  interval of publishing message(ms) [default: 1000]
  -u, --username         username for connecting to server
  -P, --password         password for connecting to server
  -t, --topic            topic subscribe, support %u, %c, %i variables
  -s, --size             payload size [default: 256]
  -q, --qos              subscribe qos [default: 0]
  -r, --retain           retain message [default: false]
  -k, --keepalive        keep alive in seconds [default: 300]
  -C, --clean            clean start [default: true]
  -x, --session-expiry   Set 'Session-Expiry' for persistent sessions
                         (seconds) [default: 0]
  -L, --limit            The max message count to publish, 0 means
                         unlimited [default: 0]
  -S, --ssl              ssl socoket for connecting to server [default:
                         false]
  --certfile             client certificate for authentication, if
                         required by server
  --keyfile              client private key for authentication, if
                         required by server
  --ws                   websocket transport [default: false]
  --quic                 QUIC transport [default: false]
  --ifaddr               One or multiple (comma-separated) source IP
                         addresses
  --prefix               client id prefix
  -l, --lowmem           enable low mem mode, but use more CPU
```



```sh
 docker run -it emqx/emqtt-bench pub -h 10.8.30.157 -p 1883 -c 1 -I 1 -t bench/%i -s 256
```

