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

```
sudo apt-get update && sudo apt-get upgrade -y
sudo apt-get install -y \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg \
    lsb-release
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
echo \
  "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io
sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

```
scp dist/app-coordinator.tar USERNAME@HOSTNAME:~/
scp dist/ui-coordinator.tar USERNAME@HOSTNAME:~/
scp deploy/docker-compose-remote.yaml USERNAME@HOSTNAME:~/
```

```
sudo docker load --input app-coordinator.tar
sudo docker load --input ui-coordinator.tar
```


```

```

### Edge

```
sudo apt-get update && sudo apt-get upgrade -y
sudo apt-get install -y openjdk-11-jdk
```

```
scp dist/client.jar USERNAME@HOSTNAME:~/	
```


```
export MASTER_SERVER_ENDPOINT=HOSTNAME/api/ws
java -jar ./client.jar
```

## Upload file

1. Download https://archive.ics.uci.edu/ml/datasets/Bar+Crawl%3A+Detecting+Heavy+Drinking
2. Save only time, x, y, z columns
3. Upload file to edge instance

## Execute query based on File

```
select max(time) from file('/home/ubuntu/Downloads/dataset.csv', CSV, 'time Int64, x Float32, y Float32, z Float32')
```

More about file at [here](https://clickhouse.tech/docs/en/sql-reference/table-functions/file/)
