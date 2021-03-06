# demo_temu

## Build

launch  from the root package

```
mvn clean package
```




## Deployment

### Ubuntu < 15.04
The demo need docker to be installed on the machine with a version higher than 1.7. The java code need to communicate with the docker daemon using TCP.

>By default Docker server is using UNIX sockets for communication with the Docker client, however docker-java client uses TCP/IP to connect to the Docker server by default, so you will need to make sure that your Docker server is listening on TCP port. To allow Docker server to use TCP add the following line to /etc/default/docker
 
> src - [docker-java/docker-java](https://github.com/docker-java/docker-java)

```
DOCKER_OPTS="-H tcp://0.0.0.0:2375 -H unix:///var/run/docker.sock"
```


### Ubuntu > 15.04

Docker now use systemd for configuration, meaning that /etc/default/docker is not read on such systems. One has to configure a systemd socker config file instead. For that just created the following file

```
touch /etc/systemd/system/docker-tcp.socket
```

then type the following content:
```
[Unit]
Description=Docker Socket for the API

[Socket]
ListenStream=2375
Service=docker.service
BindIPv6Only=both

[Install]
WantedBy=sockets.target
```

all you need to do now is to reload the daemons, stop docker and relaunch the tcp version for the deamon:


```
> sudo systemctl daemon-reload
> sudo systemctl stop docker
> sudo systemctl start docker-tcp.socket
```


### Launch the whole solution with docker compose

go to docker/compose 

copy the template file application.env.tpl to application.env, and modify the IP address according to your deployment.

```
docker-compose -f girafe.yaml up
```


### as a service

to register girafe as a service, allowing automatic restart, and systemd management, create the following file

```
touch /etc/systemd/user/girafe.service
```

and type the following text (it may change according to where you did you repo clone)

```
[Unit]
Description=Girafe Project
After=docker-tcp.socket
BindsTo=docker-tcp.socket
Conflicts=shutdown.target reboot.target halt.target

[Service]
Environment=
TimeoutStartSec=0
TimeoutStopSec=30
Restart=always
RestartSec=10

WorkingDirectory=/home/ubuntu/temu-demo/docker/composer

ExecStartPre=-/usr/local/bin/docker-compose stop -f girafe.yml 
ExecStartPre=-/usr/local/bin/docker-compose rm -f girafe.yml

ExecStart=/usr/local/bin/docker-compose -f girafe.yml up --force-recreate --no-deps 

ExecStop=/usr/local/bin/docker-compose -f girafe.yml stop   

NotifyAccess=all

[Install]
WantedBy=multi-user.target

```

then just start the girage service. 
