package com.eric.ekcloudgallerybackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@MapperScan("com.eric.ekcloudgallerybackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class EkCloudGalleryBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EkCloudGalleryBackendApplication.class, args);
    }

}
