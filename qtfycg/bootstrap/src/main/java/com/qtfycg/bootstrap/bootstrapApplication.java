/*
 * Copyright (c)
 * 2026
 * qtfycg
 * All rights reserved
 */

/*
 * Copyright (c)
 */

package com.qtfycg.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class bootstrapApplication {
    public static void main(String[] args) {
        SpringApplication.run(bootstrapApplication.class, args);
        log.info("==========项目启动成功==========");
    }
}
