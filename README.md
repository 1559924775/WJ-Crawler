# WJ-Crawler
开发工具：IDEA
依赖工具:spring-boot、mybatis、reids、dubbo、zookeeper、mysql


分布式爬虫（抓取纵横小说和豆瓣刚上映电影，观察每日的数据变化趋势）
项目分三个模块：
1.爬虫模块：分布式爬虫
2.日志模块：统一个各个节点的日志，方便分析
3.监控模块：监控各个节点的情况，节点宕机发邮件提醒

1.爬虫模块：
1）各个爬虫节点采用工作窃取的方式工作
  每一个爬虫任务（如爬取豆瓣电影任务），对应一个主任务队列（存列表url和详情页url），每个爬虫节点各有一个任务队列（存放详情页url）
  //![Image text](https://github.com/1559924775/WJ-Crawler/blob/master/work-stealing.png)
  <img src="https://github.com/1559924775/WJ-Crawler/blob/master/work-stealing.png" width="600" alt="工作窃取"/><br/>
2）各个节点启动时，选举出master节点从数据库中拉取起始url放入各任务的主任务队列中，从节点监听到master将url放入后开始工作
3）爬取的url由布隆过滤器配合已爬取Set（doneSet）实现去重

2.日志模块：
通过dubbo注册服务，交由各个爬虫节点调用，实现统一的日志管理

3.监控模块：
各个爬虫节点在zookeeper上注册，通过监听节点，监听各个爬虫节点的工作情况，出现宕机，发送邮件报告。


