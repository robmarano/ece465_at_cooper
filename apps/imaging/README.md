# Distributed Imaging Service

## Build
```bash
./build.sh
```

## Run
```bash
# running node-1
cd build/node1
./node1.sh && tail ./logs/node-1.log
```
```bash
# running node-2
cd build/node2
./node2.sh && tail ./logs/node-2.log
```

## Kill
```bash
ps aux|egrep java | egrep ImagingNode | awk '{print $2}' | xargs kill
```

## DEPRECATED
## Run Server
```bash
cd ./build/imaging
./server.sh
```
Check if running on port, e.g., 1859
```bash
netstat -an | grep 1859
```

## Run Client
```bash
./client.sh
```

You can also test with the nc command:
```bash
nc localhost 1859
```


