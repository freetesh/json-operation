package com.github.fritesh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class start {

    private static Logger logger = LoggerFactory.getLogger(start.class);

    public static void main(String[] args){
        logger.info("Started");
        SpringApplication.run(start.class, args);
        logger.info("Stopped");
    }
}
