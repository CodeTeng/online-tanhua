# online-tanhua
该项目是基于`SpirngBoot`+`SpringCloud`+`Dubbo`的微服务项目，中间采用`MongoDB`、`Redis`、`RabbitMQ`等技术以及采用第三方技术如`OSS`、`SMS`、百度云人脸识别，最后采用`Spark`+`Mlib`实现推荐系统的一个在线交友项目
****
项目背景，类似soul、陌陌、花田等交友APP

# 技术选型🎖️🎖️🎖️
前端：

`flutter` + `android` + `环信SDK` + `redux` + `shared_preferences` + `connectivity` + `iconfont` + `webview` + `sqflite`

后端：
- `Spring Boot` + `SpringMVC` + `Mybatis` + `MybatisPlus` + `Dubbo`
- `MongoDB geo`实现地理位置查询
- `MongoDB` 实现海量数据的存储
- `Redis`数据的缓存
- `Spark` + `MLlib`实现智能推荐
- 采用`RabbitMQ`作为消息服务中间件
- 采用分布式文件系统`FastDFS`存储小视频数据
- 采用第三方服务，如环信实现`IM`即时通讯、采用阿里云`OSS`、`SMS`、百度人脸识别 

# 技术架构🔥🔥🔥
![image](https://user-images.githubusercontent.com/82208902/222907134-72faf99f-0b32-4a19-96f4-99c18cb298f7.png)

# 相关模块
![image](https://user-images.githubusercontent.com/82208902/222907206-a2fdebdb-fad0-4408-b35c-304fd2018dd7.png)

![image](https://user-images.githubusercontent.com/82208902/222907211-f265deea-cfb2-455f-9d36-7bdacea0271d.png)
