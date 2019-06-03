package com.uestc;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnableDubboConfiguration
public class LogApplication {
	public static void main(String[] args) {
		SpringApplication.run(LogApplication.class, args);
	}

}
