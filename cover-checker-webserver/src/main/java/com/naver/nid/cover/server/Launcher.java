package com.naver.nid.cover.server;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Launcher {

    public static void main(String[] args) {
        final Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);


        try {
            tomcat.init();
        } catch (LifecycleException e) {
            log.error("Init failed", e);
            System.exit(1);
        }

        try {
            tomcat.start();
        } catch (LifecycleException e) {
            log.error("Start failed", e);
            System.exit(1);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Server close");
            try {
                tomcat.stop();
                tomcat.destroy();
            } catch (LifecycleException e) {
                log.error("Stop failed", e);
                System.exit(1);
            }
        }));
    }
}
