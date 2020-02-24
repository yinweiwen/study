## Install Minikube

[HERE](https://kubernetes.io/docs/tasks/tools/install-minikube/)

```shell
curl -Lo minikube https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64 \
  && chmod +x minikube

sudo mkdir -p /usr/local/bin/
sudo install minikube /usr/local/bin/

// run minikube on docker
 sudo minikube start --vm-driver=none
```