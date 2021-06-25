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

One connector can handle many customers (many Kerlink accounts).  

It can be only one instance of connector. Two or more instances connected to to the same Kerlink accounts will cause problems.

## Technologies
* Java 8
* Spring Boot 2.1.8.RELEASE
* Eclipse Paho 1.2.0
* Apache HttpComponents Client 4.5.9
* Apache Commons Text 1.9

## Configuration
All configuration can be found in **application.yaml** file located in src/main/resources

```
 1  server:
 2    port: 8080
 3  spring:
 4    application:
 5      name: Kerlink2Lo
    
 6  lo:
 7    hostname: liveobjects.orange-business.com
 8    api-key: _api_key_
 9    keep-alive-interval-seconds: 30
10    automatic-reconnect: true
11    message-qos: 1
12    mqtt-persistence-dir: ${basedir:.}/temp/
13    connection-timeout: 30000
14    page-size: 20
15    synchronization-device-interval: PT10M
16    message-decoder: test_csv
    
17  kerlink-global:
18    login-interval: PT5H
    
19  kerlink-list:
20    -
21      base-url: https://_your_wmc_host_/gms
22      login: _kerlink_login_
23      password: _kerlink_password
24      page-size: 20
25      kerlink-account-name: _kerlink_account_name
    
26    -
27      base-url: https://_your_wmc_host_/gms
28      login: _kerlink_login_
29      password: _kerlink_password
30      page-size: 20
31      kerlink-account-name: _kerlink_account_name

```
You can change all values but the most important are:

**2** - Tomcat port

**7** - Live Objects REST API url

**8** - Live Objects API key with at least DEVICE\_R and DEVICE\_W roles 

**10** - Live Objects API key with at least CONNECTOR_ACCESS role

**11** - message QoS

**14** - Live Objects REST page size (max 1000)

**15** - Interval between devices synchronization process (using Java Duration syntax, or in milliseconds)

**16** - Name of Live Objects message decoder. Can be empty but if set it will be applied to all messages from every device

**18** - Interval login attempts to Kerlink (in Java Duration syntax, or in milliseconds). Should be shorter than Kerlink
JWT token expiration period (typically 10 hours).

**20** - First Kerlink account configuration

**21** - Kerlink REST API url

**22** -  Kerlink user

**23** -  Kerlink password

**24** - Kerlink REST page size (max 1000)

**25** - Kerlink account name (the same device group will be created in Live Objects Platform)

**26** - Second Kerlink account configuration (you can add as many Kerlink accounts as you wish)


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

#### Kerlink Push mechanism
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
