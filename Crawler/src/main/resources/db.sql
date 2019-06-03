use spider;
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
    recommend varchar(50),
    click varchar(50),
    date varchar(50)
);
create table startUrl(
  id int primary key auto_increment,
  name varchar(20),-- 任务名称
  url varchar(300)
);