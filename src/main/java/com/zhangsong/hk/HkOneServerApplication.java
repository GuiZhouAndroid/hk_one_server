package com.zhangsong.hk;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@MapperScan("com.zhangsong.hk.mapper")
@SpringBootApplication
public class HkOneServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(HkOneServerApplication.class, args);
    }

}
