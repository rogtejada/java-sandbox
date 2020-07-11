eval $(minikube docker-env)
docker build -f src/main/docker/Dockerfile.native -t quarkus-cheatsheet/myapp .

kubectl run quarkus-cheatsheet --image=quarkus-cheatsheet/myapp:latest --port=8080 --image-pull-policy=IfNotPresent

kubectl expose deployment quarkus-cheatsheet --type=NodePort
