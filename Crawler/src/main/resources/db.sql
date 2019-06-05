USE SPIDER;
CREATE TABLE douban1(
    id VARCHAR(64),
    name VARCHAR(50),
    director VARCHAR(50),
    language VARCHAR(50) ,
    duration VARCHAR(50),
    type VARCHAR(50),
    time VARCHAR(50),
    area VARCHAR(50),
    url VARCHAR(100)
);

create table douban2(
    id varchar(64),  -- 关联douban1的id
    fid varchar(64), -- 关联douban1的id
    commentnumber varchar(50),
    score varchar(50),
    date varchar(50)
);
create table zongheng1(
    id varchar(64),
    name varchar(50),
    number varchar(50),-- 字数
    url varchar(100),
    type varchar(50)
);
create table zongheng2(
    id varchar(64),
    fid varchar(64), -- 关联zongheng1的id
    recommend varchar(50),
    click varchar(50),
    date varchar(50)
);
create table startUrl(
  id int primary key auto_increment,
  name varchar(20),-- 任务名称
  url varchar(300)
);

-- tcc事务用这个表来记录事务情况
CREATE TABLE `TCC_TRANSACTION` (
  `TRANSACTION_ID` int(11) NOT NULL AUTO_INCREMENT,
  `DOMAIN` varchar(100) DEFAULT NULL,
  `GLOBAL_TX_ID` varbinary(32) NOT NULL,
  `BRANCH_QUALIFIER` varbinary(32) NOT NULL,
  `CONTENT` varbinary(8000) DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `TRANSACTION_TYPE` int(11) DEFAULT NULL,
  `RETRIED_COUNT` int(11) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `LAST_UPDATE_TIME` datetime DEFAULT NULL,
  `VERSION` int(11) DEFAULT NULL,
  `IS_DELETE` tinyint(1) DEFAULT 0 NOT NULL,
  PRIMARY KEY (`TRANSACTION_ID`),
  UNIQUE KEY `UX_TX_BQ` (`GLOBAL_TX_ID`,`BRANCH_QUALIFIER`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;