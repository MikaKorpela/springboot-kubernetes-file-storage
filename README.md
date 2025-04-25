# springboot-kubernetes-file-storage #

This repository implements a simple Spring Boot application that demonstrates how to use Kubernetes for file storage.

The Spring Boot application stores entities to JSON file.

## Application Structure ##

The Spring Boot application implements a singel API that can be used for CRUD operations on the `Duck` entities.

The Spring Boot application has layered implementation that consist of controller, service and repository classes.

THe repository class needs a JSON file (`src/main/resources/ducks.json`) to store the `Duck` entities.

## Dockerfile ##

Following `Dockerfile` is used to build an image for the Spring Boot application

```dockerfile
FROM openjdk:21-jdk
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## Kubernetes Configurations ##

The Kubernetes configuration requires deployment, service and ingress configurations.

The `/src/main/resource/ducks.json` file does not require an actual volume, but it is mounted to the pod using a configmap.

The configurations are included into a single file ducks.yaml, which is deployed with following command:

`kubectl create -f ducks.yaml`

```yaml

### Deployment ###

Precondition for the deployment is that the configmap is in place. The config map is a JSON file that contains an empty array.

```json
[]
```

The configmap is crated to Kubernetes with following command:

`kubectl create configmap ducks-empty-json --from-file=ducks.json`

The deployment configuration contains a container definition (`ducks-server`) and a reference to the configmap (`ducks-empty-json`) as a volume.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name:  ducks-deployment
  labels:
    name:  ducks-deployment
spec:
  replicas: 1
  selector:
    matchLabels:
      server: ducks
  strategy:
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 1
    type: RollingUpdate
  template:
    metadata:
      labels:
        server: ducks
    spec:
      containers:
      - name: ducks-server
        image: ducks:1
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

The service configuration has a reference to container (`ducks` → `ducks-server`) and it exposes port 8080 to outside world.

```yaml
apiVersion: v1
kind: Service
metadata:
  name: ducks-service
spec:
  selector:
    server: ducks
  type: ClusterIP
  ports:
  - port: 8180
    targetPort: 8080
```

### Ingress ###

The ingress configuration has a reference to the service (`ducks-service`) and it exposes port 8180 to outside world.

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: ducks-ingress
  labels:
    name: ducks-ingress
spec:
  rules:
  - host: ducks.local
    http:
      paths:
      - pathType: Prefix
        path: "/"
        backend:
          service:
            name: ducks-service
            port:
              number: 8180
```
