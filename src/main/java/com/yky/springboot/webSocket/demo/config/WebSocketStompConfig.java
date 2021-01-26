package com.yky.springboot.webSocket.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @version 1.0
 * @ClassName WebSocketStompConfig
 * @Description TODO
 * @Author YKY
 * @Date 2021/1/25 15:11
 **/
@Configuration
public class WebSocketStompConfig {

    /**
     * 注册 Bean 用于扫描带有 ServerEndpoint 的注解成为 websocket 如果你使用外置的 tomcat 就不需要该配置文件
     *
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
