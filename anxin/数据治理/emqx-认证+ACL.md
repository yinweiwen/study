## 1.SSL/TSL

官网文档：https://www.emqx.io/docs/zh/v4.3/advanced/auth.html#%E8%AE%A4%E8%AF%81%E6%96%B9%E5%BC%8F

例子：[Enable SSL/TLS for EMQX MQTT broker](https://www.emqx.com/en/blog/emqx-server-ssl-tls-secure-connection-configuration-guide#the-security-advantages-brought-by-ssl-tls)

### 自签证书验证

The self-signed certificate

```vim
openssl genrsa -out ca.key 2048

openssl req -x509 -new -nodes -key ca.key -sha256 -days 3650 -out ca.pem

openssl genrsa -out emqx.key 2048

# openssl.cnf
[req]
default_bits  = 2048
distinguished_name = ngaiot_edge
req_extensions = req_ext
x509_extensions = v3_req
prompt = no
[ngaiot_edge]
countryName = CN
stateOrProvinceName = Zhejiang
localityName = Hangzhou
organizationName = EMQX
commonName = Server certificate
[req_ext]
subjectAltName = @alt_names
[v3_req]
subjectAltName = @alt_names
[alt_names]
IP.1 = 127.0.0.1
DNS.1 = edge.ngaiot.com


openssl req -new -key ./emqx.key -config openssl.cnf -out emqx.csr

openssl x509 -req -in ./emqx.csr -CA ca.pem -CAkey ca.key -CAcreateserial -out emqx.pem -days 3650 -sha256 -extensions v3_req -extfile openssl.cnf

openssl verify -CAfile ca.pem emqx.pem
```

EMQX Config

```ini
## Path to the file containing the user's private PEM-encoded key.
##
## See: http://erlang.org/doc/man/ssl.html
##
## Value: File
listener.ssl.external.keyfile = etc/certs/emqx.key

## Path to a file containing the user certificate.
##
## See: http://erlang.org/doc/man/ssl.html
##
## Value: File
listener.ssl.external.certfile = etc/certs/emqx.pem

## Path to the file containing PEM-encoded CA certificates. The CA certificates
## are used during server authentication and when building the client certificate chain.
##
## Value: File
listener.ssl.external.cacertfile = etc/certs/ca.pem
```

![image-20221012213017759](imgs/emqx-%E8%AE%A4%E8%AF%81+ACL/image-20221012213017759.png)



公网可用的MQTT连接测试地址：

test.mosquitto.org

```
1883 : MQTT, unencrypted, unauthenticated
1884 : MQTT, unencrypted, authenticated
8883 : MQTT, encrypted, unauthenticated
8884 : MQTT, encrypted, client certificate required
8885 : MQTT, encrypted, authenticated
8886 : MQTT, encrypted, unauthenticated
8887 : MQTT, encrypted, server certificate deliberately expired
8080 : MQTT over WebSockets, unencrypted, unauthenticated
8081 : MQTT over WebSockets, encrypted, unauthenticated
8090 : MQTT over WebSockets, unencrypted, authenticated
8091 : MQTT over WebSockets, encrypted, authenticated
```



To request MQTT over TLS use one of `ssl`, `tls`, `mqtts`, `mqtt+ssl` or `tcps`

Golang实现TLS连接

```go
func NewTLSConfig() *tls.Config {
	// Import trusted certificates from CAfile.pem.
	// Alternatively, manually add CA certificates to
	// default openssl CA bundle.
	certpool := x509.NewCertPool()
	pemCerts, err := ioutil.ReadFile("conf/CA.pem")
	if err == nil {
		certpool.AppendCertsFromPEM(pemCerts)
	}

	// Import client certificate/key pair
	cert, err := tls.LoadX509KeyPair("samplecerts/client-crt.pem", "samplecerts/client-key.pem")
	if err != nil {
		panic(err)
	}

	// Just to print out the client certificate..
	cert.Leaf, err = x509.ParseCertificate(cert.Certificate[0])
	if err != nil {
		panic(err)
	}
	fmt.Println(cert.Leaf)

	// Create tls.Config with desired tls properties
	return &tls.Config{
		// RootCAs = certs used to verify server cert.
		RootCAs: certpool,
		// ClientAuth = whether to request cert from server.
		// Since the server is set up for SSL, this happens
		// anyways.
		ClientAuth: tls.NoClientCert,
		// ClientCAs = certs used to validate client cert.
		ClientCAs: nil,
		// InsecureSkipVerify = verify that cert contents
		// match server. IP matches what is in cert etc.
		InsecureSkipVerify: true,
		// Certificates = list of certs client sends to server.
		Certificates: []tls.Certificate{cert},
	}
}
```



华为云TLS证书

Golang代码实现

```go
package main

import (
	"crypto/tls"
	"crypto/x509"
	"fmt"
	"math/rand"
	"sync"
	"time"

	MQTT "github.com/eclipse/paho.mqtt.golang"
)

func main() {
	subClient := InitMqttClient(onSubConnectionLost)
	pubClient := InitMqttClient(onPubConnectionLost)

	wait := sync.WaitGroup{}
	wait.Add(1)

	go func() {
		for {
			time.Sleep(1*time.Second)
			pubClient.Publish("topic", 0, false, "hello world")
		}
	}()

	subClient.Subscribe("topic", 0, onReceived)

	wait.Wait()
}

func InitMqttClient(onConnectionLost MQTT.ConnectionLostHandler) MQTT.Client {
	pool := x509.NewCertPool()
	cert, err := tls.LoadX509KeyPair("/tmp/example_cert.crt", /tmp/example_cert.key")
	if err != nil {
		panic(err)
	}

	tlsConfig := &tls.Config{
		RootCAs: pool,
		Certificates: []tls.Certificate{cert},
		// 单向认证，client不校验服务端证书
		InsecureSkipVerify: true,
	}
    // 使用tls或者ssl协议，连接8883端口
	opts := MQTT.NewClientOptions().AddBroker("tls://127.0.0.1:8883").SetClientID(fmt.Sprintf("%f",rand.Float64()))
	opts.SetTLSConfig(tlsConfig)
	opts.OnConnect = onConnect
	opts.AutoReconnect = false
	// 回调函数，客户端与服务端断连后立刻被触发
	opts.OnConnectionLost = onConnectionLost
	client := MQTT.NewClient(opts)
	loopConnect(client)
	return client
}

func onReceived(client MQTT.Client, message MQTT.Message) {
	fmt.Printf("Receive topic: %s,  payload: %s \n", message.Topic(), string(message.Payload()))
}

// sub客户端与服务端断连后，触发重连机制
func onSubConnectionLost(client MQTT.Client, err error) {
	fmt.Println("on sub connect lost, try to reconnect")
	loopConnect(client)
	client.Subscribe("topic", 0, onReceived)
}

// pub客户端与服务端断连后，触发重连机制
func onPubConnectionLost(client MQTT.Client, err error) {
	fmt.Println("on pub connect lost, try to reconnect")
	loopConnect(client)
}

func onConnect(client MQTT.Client) {
	fmt.Println("on connect")
}

func loopConnect(client MQTT.Client) {
	for {
		token := client.Connect()
		if rs, err := CheckClientToken(token); !rs {
			fmt.Printf("connect error: %s\n", err.Error())
		} else {
			break
		}
		time.Sleep(1 * time.Second)
	}
}

func CheckClientToken(token MQTT.Token) (bool, error) {
	if token.Wait() && token.Error() != nil {
		return false, token.Error()
	}
	return true, nil
}
```

## 2.发布订阅ACL

官网文档：https://www.emqx.io/docs/zh/v4.3/advanced/acl.html





## 扩展1：Go生成证书

步骤一：产生自己的CA

```
openssl req -new -x509 -days 36500 -extensions v3_ca -keyout ca.key -out ca.crt
openssl req -new -x509 -days 36500 -extensions v3_ca -keyout ca.key -out ca.pem
```

步骤二：产生服务端证书

```
openssl genrsa -des3 -out server.key 2048
openssl req -out server.csr -key server.key -new
openssl x509 -req -in server.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out server.crt -days 36500
```

步骤三：产生客户端证书

```
openssl genrsa -out client-key.pem 2048
openssl req -out client.csr -key client-key.pem -new
openssl x509 -req -in client.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out client-crt.pem -days 36500
```

经过上面8条命令后，即可生成所需的所有证书文件，其中：

客户端使用：

```
ca.pem、client-crt.pem、client-key.pem
```

服务端使用：

```
ca.crt、server.crt、server.key
```

https://medium.com/@shaneutt/create-sign-x509-certificates-in-golang-8ac4ae49f903

```golang
package main

import (
	"bytes"
	"crypto/rand"
	"crypto/rsa"
	"crypto/tls"
	"crypto/x509"
	"crypto/x509/pkix"
	"encoding/pem"
	"fmt"
	"io/ioutil"
	"math/big"
	"net"
	"net/http"
	"net/http/httptest"
	"strings"
	"time"
)

func main() {
	// get our ca and server certificate
	serverTLSConf, clientTLSConf, err := certsetup()
	if err != nil {
		panic(err)
	}

	// set up the httptest.Server using our certificate signed by our CA
	server := httptest.NewUnstartedServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		fmt.Fprintln(w, "success!")
	}))
	server.TLS = serverTLSConf
	server.StartTLS()
	defer server.Close()

	// communicate with the server using an http.Client configured to trust our CA
	transport := &http.Transport{
		TLSClientConfig: clientTLSConf,
	}
	http := http.Client{
		Transport: transport,
	}
	resp, err := http.Get(server.URL)
	if err != nil {
		panic(err)
	}

	// verify the response
	respBodyBytes, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		panic(err)
	}
	body := strings.TrimSpace(string(respBodyBytes[:]))
	if body == "success!" {
		fmt.Println(body)
	} else {
		panic("not successful!")
	}
}

func certsetup() (serverTLSConf *tls.Config, clientTLSConf *tls.Config, err error) {
	// set up our CA certificate
	ca := &x509.Certificate{
		SerialNumber: big.NewInt(2019),
		Subject: pkix.Name{
			Organization:  []string{"Company, INC."},
			Country:       []string{"US"},
			Province:      []string{""},
			Locality:      []string{"San Francisco"},
			StreetAddress: []string{"Golden Gate Bridge"},
			PostalCode:    []string{"94016"},
		},
		NotBefore:             time.Now(),
		NotAfter:              time.Now().AddDate(10, 0, 0),
		IsCA:                  true,
		ExtKeyUsage:           []x509.ExtKeyUsage{x509.ExtKeyUsageClientAuth, x509.ExtKeyUsageServerAuth},
		KeyUsage:              x509.KeyUsageDigitalSignature | x509.KeyUsageCertSign,
		BasicConstraintsValid: true,
	}

	// create our private and public key
	caPrivKey, err := rsa.GenerateKey(rand.Reader, 4096)
	if err != nil {
		return nil, nil, err
	}

	// create the CA
	caBytes, err := x509.CreateCertificate(rand.Reader, ca, ca, &caPrivKey.PublicKey, caPrivKey)
	if err != nil {
		return nil, nil, err
	}

	// pem encode
	caPEM := new(bytes.Buffer)
	pem.Encode(caPEM, &pem.Block{
		Type:  "CERTIFICATE",
		Bytes: caBytes,
	})

	caPrivKeyPEM := new(bytes.Buffer)
	pem.Encode(caPrivKeyPEM, &pem.Block{
		Type:  "RSA PRIVATE KEY",
		Bytes: x509.MarshalPKCS1PrivateKey(caPrivKey),
	})

	// set up our server certificate
	cert := &x509.Certificate{
		SerialNumber: big.NewInt(2019),
		Subject: pkix.Name{
			Organization:  []string{"Company, INC."},
			Country:       []string{"US"},
			Province:      []string{""},
			Locality:      []string{"San Francisco"},
			StreetAddress: []string{"Golden Gate Bridge"},
			PostalCode:    []string{"94016"},
		},
		IPAddresses:  []net.IP{net.IPv4(127, 0, 0, 1), net.IPv6loopback},
		NotBefore:    time.Now(),
		NotAfter:     time.Now().AddDate(10, 0, 0),
		SubjectKeyId: []byte{1, 2, 3, 4, 6},
		ExtKeyUsage:  []x509.ExtKeyUsage{x509.ExtKeyUsageClientAuth, x509.ExtKeyUsageServerAuth},
		KeyUsage:     x509.KeyUsageDigitalSignature,
	}

	certPrivKey, err := rsa.GenerateKey(rand.Reader, 4096)
	if err != nil {
		return nil, nil, err
	}

	certBytes, err := x509.CreateCertificate(rand.Reader, cert, ca, &certPrivKey.PublicKey, caPrivKey)
	if err != nil {
		return nil, nil, err
	}

	certPEM := new(bytes.Buffer)
	pem.Encode(certPEM, &pem.Block{
		Type:  "CERTIFICATE",
		Bytes: certBytes,
	})

	certPrivKeyPEM := new(bytes.Buffer)
	pem.Encode(certPrivKeyPEM, &pem.Block{
		Type:  "RSA PRIVATE KEY",
		Bytes: x509.MarshalPKCS1PrivateKey(certPrivKey),
	})

	serverCert, err := tls.X509KeyPair(certPEM.Bytes(), certPrivKeyPEM.Bytes())
	if err != nil {
		return nil, nil, err
	}

	serverTLSConf = &tls.Config{
		Certificates: []tls.Certificate{serverCert},
	}

	certpool := x509.NewCertPool()
	certpool.AppendCertsFromPEM(caPEM.Bytes())
	clientTLSConf = &tls.Config{
		RootCAs: certpool,
	}

	return
}
```

