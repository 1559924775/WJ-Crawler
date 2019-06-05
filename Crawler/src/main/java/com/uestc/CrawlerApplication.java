package com.uestc;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportResource;



@MapperScan("com.uestc.dao")
@SpringBootApplication
@EnableDubboConfiguration
@ImportResource(locations = "classpath:tcc-transaction.xml")
public class CrawlerApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext run=SpringApplication.run(CrawlerApplication.class, args);
	}
}
