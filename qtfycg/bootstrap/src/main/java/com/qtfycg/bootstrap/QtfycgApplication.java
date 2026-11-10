/*
 * Copyright (c)
 * 2026
 * qtfycg
 * All rights reserved
 */

package com.qtfycg.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(scanBasePackages = "com.qtfycg")
public class QtfycgApplication {

    public static void main(String[] args) {
        SpringApplication.run(QtfycgApplication.class, args);
        log.info("==========启动成功==========");
    }
}
