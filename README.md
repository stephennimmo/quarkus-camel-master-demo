# quarkus-camel-master-demo

## Run/Demo Locally

Start First Instance  
```
./mvnw clean quarkus:dev
```

Start Second Instance
```
./mvnw clean quarkus:dev -Dquarkus.http.port=8081
```

One of the instances should have the lock (probably the first one). To demo the lock switch, kill the master and watch the second one pick up the lock and run.

## Push New Version to Registry
```
./mvnw clean package -Dquarkus.container-image.push=true -Dquarkus.container-image.registry=quay.io
```

## Deploy to OpenShift

```
oc apply -k manifests
```





