# Dasudian IoT DataHub Java SDK demo

## 介绍

该项目为大数点工业大数据分析和设备管理服务的数据采集端的Java SDK的Demo程序，其中包含了SDK库文件本身和一个使用该SDK的Demo程序，演示如何集成大数点IoT DataHub SDK以实现设备数据的采集。

大数点工业大数据服务让设备变得有生命，并让其整个生命周期为你管理，让她的每一次呼吸（产生数据）在大数点云端为你变现！关于如何获得大数点工业大数据分析服务，请联系大数点顾问团队sales@dasudian.com或support@dasudian.com，我们将竭诚为您展示大数点丰富多彩而致力于解决实际问题的数据分析服务。

## 目录结构说明
请将lib目录下的`dasudian-datahub-sdk-x.x.x.jar` 放到您项目对应的 `libs/` 目录下。

### demo目录

[demo](./demo/com/dasudian/iot/demo/Main.java)


### lib目录
该目录下有最新的SDK库。

### docs目录
该目录下有关于SDK的文档

## 编译和运行demo

JDK版本: openjdk version "1.8.0_131"

环境: Ubuntu 16.04

在本目录下运行下列命令

linux编译:

```
javac -classpath lib/dasudian-datahub-sdk-xxx.jar demo/com/dasudian/iot/demo/Main.java
```

linux运行:

```
java -classpath lib/dasudian-datahub-sdk-xxx.jar:demo/ com.dasudian.iot.demo.Main
```

Windows编译

```
javac -encoding UTF-8 -classpath lib/dasudian-datahub-sdk-XXX.jar demo/com/dasudian/iot/demo/Main.java
```

Windows运行

```
java -classpath lib/dasudian-datahub-sdk-Java.04.00.00.jar;demo/ com.dasudian.iot.demo.Main
```
