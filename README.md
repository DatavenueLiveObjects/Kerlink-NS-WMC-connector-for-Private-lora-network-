## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Configuration](#configuration)

## General info
![Architecture](/assets/architecture.png)
This repository contains everything you need to create 'Kerlink to Live Objects' connector. This connector was designed to synchronize data between Kerlink 'Wanesy Management Center' and Live Objects Platform. Current version of connector allows to make one way synchronization - we can synchronize information from Kerlink to Live Objects.

Three main features are:
* **devices synchronization** - every device created in Kerlink will appear in LO and every device deleted from Kerlink will be also deleted from LO
* **messages synchronization** - every message which will be send from device to Kerlink will appear in LO
* **commands synchronization** - every command created in LO will be sent to Kerlink and status in LO will be updated

One connector can handle one customer (one Kerlink account). If you have more accounts you need to setup one instance of connector per each account. 

It can be only one instance of connector per Kerlink account. Two or more instances connected to to the same Kerlink account will cause problems.

## Technologies
* Java 8
* Spring Boot 2.1.8.RELEASE
* Eclipse Paho 1.2.0
* Apache HttpComponents Client 4.5.9

## Configuration
All configuration can be found in **application.yaml** file located in src/main/resources

```
 1 server:
 2   port: 8080
 3 spring:
 4   application:
 5     name: Kerlink2IotHub
 6     
 7 kerlink:
 8   base-url: _url_
 9   login: _login_
10   password: _password_
11   login-interval: 32400000
12   page-size: 20
13   
14 lo:
15   api-key: _key_
16   api-url: https://liveobjects.orange-business.com/api/
17   connector-api-key: _key_
18   connector-user: connector
19   connector-mqtt-url: ssl://liveobjects.orange-business.com:8883
20   synchronization-device-interval: 10000
21   synchronization-thread-pool-size: 40
22   page-size: 20
23   device-group-name: kerlink
24   device-prefix: 'urn:lo:nsid:x-connector:'
25   
26   message-sender-max-thread-pool-size: 100
27   message-sender-min-thread-pool-size: 10
28   message-qos: 1
29  message-decoder: 
```
You can change all values but the most important are:

*2* - Tomcat port

*8* - Kerlink REST API url

*9* -  Kerlink user

*10* -  Kerlink password

*11* -  JWT token you receive after login is valid for 10 hours so we need to refresh this token every some time less than 10 hours. In this example refresh process is executed every 9h * 60m * 60s * 1000 ms = 32400000

*12* - Kerlink REST page size (max 1000)

*15 - Live Objects API key with at least DEVICE\_R and DEVICE\_W roles 

*16* - Live Objects REST API url

*17* - Live Objects API key with at least CONNECTOR_ACCESS role

*19* - Live Objects mqtt url

*20* - Interval between devices synchronization process (in milliseconds)

*21* - How many threads will be used in devices synchronization process

*20* - Live Objects REST page size (max 1000)

*23* - Device group name. If group name does not exists it will be created

*24* - Do not change it

*26* - How many threads (at least) will be used in message synchronization process

*27* - How many threads (at most) will be used in message synchronization process

*28* - message QoS

*29* - Name of Live Objects message decoder. Can be empty but if set it will be applied to all messages from every device

#### Loging
Logging configuration can be found in **logback.xml** file located in src/main/resources. You can find more information about how to configure your logs [here](http://logback.qos.ch/manual/configuration.html) 

#### Generate API keys
Login to Orange Web Portal an go to Configuration -> API keys 

![Api Keys 1](/assets/api_key_1.png) 

Click **Add** button and fill fields

![Api Keys 2](/assets/api_key_2.png)


#### Devices group
You can easily find devices group in main devices view. Just go to **devices** in main top menu

![Devices](/assets/devices.png)

#### Kerlink Push mechanizm
Login to Kerlink Wanesy Management Center and go to **Administration -> Clusters -> Push Configurations**

![Push Confiuration](/assets/push_configuration.png)

Click **plus** button to add new push configuration

![Push Confiuration 2](/assets/push_configuration_2.png)

Fill fields in next screens

![Push Confiuration 3](/assets/push_configuration_3.png)

![Push Confiuration 4](/assets/push_configuration_4.png)

And now go to **Administration -> Clusters** and click edit icon next to the cluster you want to edit and choose new push configuration 

![Cluster Confiuration](/assets/cluster_configuration.png)