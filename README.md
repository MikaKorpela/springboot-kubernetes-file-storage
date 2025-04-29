# springboot-kubernetes-file-storage #

This repository implements a simple Spring Boot application that demonstrates how to use Kubernetes for file storage.

The Spring Boot application stores entities to JSON file.

## Application Structure ##

The Spring Boot application implements a single API that can be used for CRUD operations on the `Duck` entities.

The Spring Boot application has layered implementation that consist of controller, service and repository classes.

The repository class needs a JSON file (`src/main/resources/ducks.json`) to store the `Duck` entities.

## Dockerfile ##

Following `Dockerfile` is used to build an image for the Spring Boot application

```dockerfile
FROM openjdk:21-jdk
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

### Image Creation with Minikube ###

If the used Kubernetes instance is minikube and it is running in Windows, then the Docker image must be created directly to the image repository in minikube, because the image repositories used by the local Docker daemon and minikube are not the same.

`minikube image build -t ducks-json:<version> .`

## Kubernetes Configurations ##

The Kubernetes configuration requires deployment, service and ingress configurations.

The `/src/main/resource/ducks.json` file does not require an actual volume, but it is mounted to the pod using a `configmap`.

The configurations are included into a single file `ducks-json-application.yaml`, which is deployed with following command:

`kubectl create -f ducks-json-application.yaml`

### Deployment ###

Precondition for the deployment is that the configmap is in place. The config map is a JSON file that contains an empty array.

```json
[]
```

The configmap is crated to Kubernetes with following command:

`kubectl create configmap ducks-empty-json --from-file=ducks.json`

The deployment configuration contains a container definition (`ducks-json-server`) and a reference to the `configmap` (`ducks-empty-json`) as a volume.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name:  ducks-json-deployment
  labels:
    name:  ducks-json-deployment
spec:
  replicas: 1
  selector:
    matchLabels:
      server: ducks-json
  strategy:
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 1
    type: RollingUpdate
  template:
    metadata:
      labels:
        server: ducks-json
    spec:
      containers:
      - name: ducks-json-server
        image: ducks-json:latest
        imagePullPolicy: Never
        resources:
          limits:
            memory: "500M"
            cpu: "500m"
        ports:
        - containerPort: 8080
        volumeMounts:
        - name: ducks-json
          mountPath: /src/main/resources
      volumes:
      - name: ducks-json
        configMap:
          name: ducks-empty-json
          items:
          - key: ducks.json
            path: ducks.json
```

### Service ###

The service configuration has a reference to container (`ducks-json` → `ducks-json-server`) and it exposes port 8180 to outside world.

```yaml
apiVersion: v1
kind: Service
metadata:
  name: ducks-json-service
spec:
  selector:
    server: ducks-json
  type: ClusterIP
  ports:
  - port: 8180
    targetPort: 8080
```

### Ingress ###

The ingress configuration has a reference to the service (`ducks-json-service`) and it exposes port 8180 to outside world.

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: ducks-json-ingress
  labels:
    name: ducks-json-ingress
spec:
  rules:
  - host: ducks-json.local
    http:
      paths:
      - pathType: Prefix
        path: "/"
        backend:
          service:
            name: ducks-json-service
            port:
              number: 8180
```
