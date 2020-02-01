# mini-spring
## 背景：
mini-spring是为了更快速的开发出一些简单的项目。
我有时候会为老师做一些项目的Demo，而这些Demo通常都很简单。
Spring Boot功能强大，但是简单的项目用到的内容并不多。
而且文件上传部分，我们工作室流行使用阿里的OSS。数据库使用MySQL。
每次要写同样的代码很不方便。而且SpringBoot的很多功能用不到，却要引用整个jar包。
基于此，我写了一个mini-spring框架（灵感来源于慕课网某老师的课程）。
## 简介：
mini-spring具备Spring Boot主流的功能：controller、bean注入、监听器、异常处理等。
内嵌Tomcat。
增加了两个组件：OSSComponent（OSS组件）和DatabaseComponent（数据库组件）。
只需要在application.properties中配置相关参数，就可以快速将文件上传至OSS并与数据库建立连接。
## 使用：
http://mini-spring-test.oss-accelerate.aliyuncs.com/use.mp4"
## 其他：
我在github源码上写了很多注释。如果有小伙伴想了解细节部分，可自行下载源码阅读。
有一定Java开发经验的小伙伴配合注释能很快理解代码。
## 联系方式：
邮箱： 1583111727@qq.com


