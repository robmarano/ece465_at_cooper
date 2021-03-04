# Distributed Imaging Service

## Build
```bash
./build.sh
```

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


