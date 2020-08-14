## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Configuration](#configuration)

## General info
![Architecture](/assets/architecture.png)
This repository contains everything you need to create 'Kerlink to Live Objects' connector. This connector was designed to synchronize data between many Kerlink 'Wanesy Management Center' accounts and Live Objects Platform. Current version of connector allows to make one way synchronization - we can synchronize information from Kerlink to Live Objects.

Three main features are:
* **devices synchronization** - every device created in Kerlink will appear in LO and every device deleted from Kerlink will be also deleted from LO
* **messages synchronization** - every message which will be send from device to Kerlink will appear in LO
* **commands synchronization** - every command created in LO will be sent to Kerlink and status in LO will be updated

One connector can handle many customers (many Kerlink account).  

It can be only one instance of connector. Two or more instances connected to to the same Kerlink accounts will cause problems.

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
 5     name: Kerlink2Lo
 6     
 7 lo:
 8   api-key: _api_key_
 9   api-url: https://liveobjects.orange-business.com/api/
10   connector-api-key: _connector_api_key_
11   connector-user: connector
12   connector-mqtt-url: ssl://liveobjects.orange-business.com:8883
13   
14   synchronization-device-interval: 10000
15   synchronization-thread-pool-size: 40
16   
17   page-size: 20
18   device-prefix: 'urn:lo:nsid:x-connector:'
19   
20   message-sender-max-thread-pool-size: 100
21   message-sender-min-thread-pool-size: 10
22   message-qos: 1
23   message-decoder: test_csv
24   
25 kerlink-list:
26   -
27     base-url: https://_your_wmc_host_/gms
28     login: _kerlink_login_
29     password: _kerlink_password
30     login-interval: 32400000
31     page-size: 20
32     kerlink-account-name: _kerlink_account_name
33     
34   -
35     base-url: https://_your_wmc_host_/gms
36     login: _kerlink_login_
37     password: _kerlink_password
38     login-interval: 32400000
39     page-size: 20
40     kerlink-account-name: _kerlink_account_name    


```
You can change all values but the most important are:

**2** - Tomcat port

**8** - Live Objects API key with at least DEVICE\_R and DEVICE\_W roles 

**9** - Live Objects REST API url

**10** - Live Objects API key with at least CONNECTOR_ACCESS role

**11** - Do not change it

**12** - Live Objects mqtt url

**14** - Interval between devices synchronization process (in milliseconds)

**15** - How many threads will be used in devices synchronization process

**17** - Live Objects REST page size (max 1000)

**18** - Do not change it

**20** - How many threads (at least) will be used in message synchronization process

**21** - How many threads (at most) will be used in message synchronization process

**22** - message QoS

**23** - Name of Live Objects message decoder. Can be empty but if set it will be applied to all messages from every device

**26** - First Kerlink account configuration

**27** - Kerlink REST API url

**28** -  Kerlink user

**29** -  Kerlink password

**30** -  JWT token you receive after login is valid for 10 hours so we need to refresh this token every some time less than 10 hours. In this example refresh process is executed every 9h * 60m * 60s * 1000 ms = 32400000

**31** - Kerlink REST page size (max 1000)

**32** - Kerlink account name (the same device group will be created in Live Objects Platform)

**34** - Second Kerlink account configuration (you can add as many Kerlink accounts as you wish)


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

Set header "Kerlink-Account" as shown in the screenshot. The value of this header must be the same as field "kerlink-account-name" in configuration file. 

![Push Confiuration 4](/assets/push_configuration4.png)

And now go to **Administration -> Clusters** and click edit icon next to the cluster you want to edit and choose new push configuration 

![Cluster Confiuration](/assets/cluster_configuration.png)