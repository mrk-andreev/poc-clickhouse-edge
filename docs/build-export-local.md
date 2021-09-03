## Create artifacts

Build and save docker images:
```
docker save $(docker build --rm -q -f src/app-coordinator/src/main/docker/Dockerfile src/app-coordinator/) | gzip > dist/app-coordinator.tar
docker save $(docker build --rm -q -f src/ui-coordinator/Dockerfile src/ui-coordinator/) | gzip > dist/ui-coordinator.tar
```
Build client jar:
```
docker cp $(docker create --rm $(docker build --rm -q -f src/app-edge/src/main/docker/Dockerfile src/app-edge/)):/opt/app/target/client-0.0.1-SNAPSHOT.jar dist/client.jar
```

## Create environments

### Server


### Edge


