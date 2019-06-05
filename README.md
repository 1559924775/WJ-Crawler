# WJ-Crawler
开发工具：IDEA
依赖工具:spring-boot、mybatis、redis、dubbo、zookeeper、mysql、tcc-trasaction

<b2>项目介绍：</b2>
分布式爬虫（抓取纵横小说和豆瓣刚上映电影，观察每日的数据变化趋势）<br/>
项目分三个模块：<br/>

1.爬虫模块：分布式爬虫<br/>
2.日志模块：统一个各个节点的日志，方便分析<br/>
3.监控模块：监控各个节点的情况，节点宕机发邮件提醒<br/>

<b>1.爬虫模块：</b><br/>
1）```分布式爬虫。```爬虫任务分布在多台机器上，每台机器多线程运行多个爬虫任务（纵横小说、豆瓣电影等），加快爬取速度同时防止单一ip频繁抓取被封。使用HttpClient，设置请求头模拟浏览器浏览。
2）```工作窃取。```各个爬虫节点采用工作窃取的方式工作<br/>
  每一个爬虫任务（如爬取豆瓣电影任务），对应一个主任务队列（存列表url和详情页url），每个爬虫节点各有一个任务队列（存放详情页url）<br/>
  <img src="https://github.com/1559924775/WJ-Crawler/blob/master/work-stealing.png" width="600" alt="工作窃取"/><br/>
3）```主节点选举。```</font> 各个节点启动时，选举出master节点从数据库中拉取起始url放入各任务的主任务队列中，从节点监听到master将url放入后开始工作<br/>
  主节点完成起始任务的拉取后创建/master-election/success节点，其他爬虫节点监听到success节点后执行爬虫任务。
<img src="https://github.com/1559924775/WJ-Crawler/blob/master/master选举用节点.JPG" width="300" alt="master选举用节点"/><br/>
4）```布隆过滤器去重。```爬取的url由布隆过滤器配合已爬取Set（doneSet）实现去重<br/>
     * 验证逻辑：<br/>
     *  （1）节点本地localDoneSet验证，若存在，那肯定爬过了，不存在->（2）<br/>
     *  （2）redis验证,先用布隆过滤器验证，若不存在，加入url，若返回存在->（3）<br/>
     *  （3）遍历redis中的doneSet验证<br/>
5）```分布式事务。```爬取的数据包括静态数据（如名字，作者）和动态数据（如点击量，推荐量），分别存入两个表中，基于可扩展性的考虑（未来数据库可能会分库分表）所以引入tcc-transaction依赖，使用TCC的方式构建分布式事务。<br/>
<img src="https://github.com/1559924775/WJ-Crawler/blob/master/TCC.JPG" width="600" alt="tcc事务模型"/><br/>
静态数据：</br>
<img src="https://github.com/1559924775/WJ-Crawler/blob/master/静态数据.JPG" width="600" alt="静态数据"/><br/>
动态数据：</br>
<img src="https://github.com/1559924775/WJ-Crawler/blob/master/动态数据.JPG" width="600" alt="动态数据"/><br/>
6）分布式唯一id。使用雪花算法生成唯一id</br>
<img src="https://github.com/1559924775/WJ-Crawler/blob/master/雪花ID.png" width="450" alt="分布式唯一id"/><br/>
6）```定时任务。```由于爬虫是周期性的爬虫数据，使用quartz定时开启任务（00 00 23 * * ?）。<br/>
7）```容灾措施。```爬虫节点宕机后可使用SpiderRecoveryStart类快速恢复爬虫任务（打开@Component注释）。<br/>

<b>2.日志模块：</b><br/>
包括每个节点的动态执行日志，和每一轮爬虫任务结束后记录统计信息<br/>
在dubbo上注册服务，交由各个爬虫节点远程调用，实现统一的日志管理<br/>

<b>3.监控模块：</b><br/>
各个爬虫节点在zookeeper上注册，通过监听节点，监听各个爬虫节点的工作情况，出现宕机，发送邮件报告。<br/>
检测到爬虫节点下线后，将节点的任务队列中任务，移动到容灾队列中（spider_douban_recovery），检查还有没有节点在线，有就平均分配到其他节点的任务队列中去。最终爬虫结束后会将所有容灾队列中任务写入到failList.txt中去。


<b2>部署:</b2>
