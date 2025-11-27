package com.lyp.cdspringboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lyp.cdspringboot.mapper")
public class CDspringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(CDspringbootApplication.class, args);
    }

}
