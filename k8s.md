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
