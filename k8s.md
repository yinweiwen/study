## DELETE POD FORCE
kubectl delete pods [POD] -n anxinyun --grace-period=0 --force

## K8S SPARK
# WEB UI
	apiVersion: v1
	kind: Service
	metadata:
	  name: spark-et
	  namespace: anxinyun
	spec:
	  type: ClusterIP
	  ports:
	  - name: "sparkweb"
		port: 4040
		targetPort: 4040
	  selector:
		spark-role: driver
	  externalIPs:
	  - 10.8.30.35

## 检查权限是否过期 
find /etc/kubernetes/pki/ -type f -name "*.crt" -print|egrep -v 'ca.crt$'|xargs -L 1 -t -i bash -c 'openssl x509 -noout -text -in {}|grep After'

或者

kubeadm alpha certs check-expiration