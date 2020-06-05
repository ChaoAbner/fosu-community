# Fosuhub校园论坛/SNS项目

- 项目总体使用SpringBoot+MyBatis+Thymeleaf开发
- 数据库使用MySQL和Redis。
- Kafka进行消息处理。
- Spring Security实现权限模块。
- Elasticsearch实现搜索引擎。

---
[更多介绍](https://github.com/ChaoAbner/fosu-community/tree/master/docs)

**功能**

1. 用户注册登录管理

2. 帖子管理

3. 消息中心与私信
4. 异步设计
5. Redis实现赞踩功能

6. 关注功能和粉丝实现
7. 站内全文搜索

**优化部分**

- 登录模块优化（使用Redis保存）
- 热门帖子优化（使用多级缓存：本地缓存和Redis）
- 异步队列

## 用户注册登录管理

### 登录/注册页面

![](http://img.fosuchao.com/20200411130726.png)

注册使用**邮箱激活**的方式进行注册。

登录使用验证码登录，包括**记住我**和**忘记密码**的功能。

### 导航

登录前

![](http://img.fosuchao.com/20200411130444.png)

登录后

![](http://img.fosuchao.com/20200411130527.png)

## 帖子管理

### 发布

登录后可见发布按钮，点击发布弹出发布窗口。

![](http://img.fosuchao.com/20200411131112.png)

### 铭感词过滤

![](http://img.fosuchao.com/20200411131240.png)

**过滤结果**

![](http://img.fosuchao.com/20200411131302.png)

### 帖子显示

![](http://img.fosuchao.com/20200411131535.png)

### 管理

置顶，加精和删除。不同角色分别由不同的权限。

![](http://img.fosuchao.com/20200411131719.png)

## 消息中心与私信

只有登录的用户才能访问此功能。

### 未读消息显示

![](http://img.fosuchao.com/20200411131949.png)

### 私信

![](http://img.fosuchao.com/20200411132031.png)

### 通知

目前包括**评论，点赞，关注**的三种通知。

![](http://img.fosuchao.com/20200411132102.png)

#### 详情

![](http://img.fosuchao.com/20200411132154.png)

## 异步设计

这里使用Kafka实现异步消息处理。

- Broker：就是Kafka服务器。因为Kafka是支持分布式部署的。
- Zookeeper：注册中心，Kafka依赖Zookeeper管理
- Topic：主题，对应一个主题来生产发布消息
- Partition：分段，一个主题可以有多个分段（队列），多线程消费消息。

![](http://img.fosuchao.com/20200411132645.png)

### 事件触发

评论、点赞、关注事件。

![](http://img.fosuchao.com/20200411132801.png)



## Redis实现赞踩功能

### 功能

- 支持对帖子、评论点赞（不同的实体）
- 点赞、取消点赞（根据点赞状态）
- 统计点赞数量

### 点赞服务

- 帖子、评论显示点赞数量
- 帖子、评论显示点赞状态
- 用户记录获得的点赞数量

![](http://img.fosuchao.com/20200411132923.png)

## 关注功能和粉丝实现

### 功能

- 用户关注了某个实体（收藏） -- TODO
- 用户关注了用户（关注/粉丝）
- 统计关注/粉丝数量

### 服务

- 对实体状态更新或者关注用户的发布行为进行推送 -- TODO
- 判断是否已关注
- 获取用户关注/粉丝数

### 关键

- 若A关注了B，则A是B的Follower（粉丝），B是A的关注目标（Followee）
- 关注的目标可以是用户、帖子等等、所以需要将关注的目标抽象成实体，通过实体类型来判断。

### 关注行为

![](http://img.fosuchao.com/20200411133015.png)

### 查看

![](http://img.fosuchao.com/20200411133035.png)

![](http://img.fosuchao.com/20200411133046.png)

## 站内全文搜索

使用Elasticsearch实现搜索引擎。

![](http://img.fosuchao.com/20200411133306.png)

