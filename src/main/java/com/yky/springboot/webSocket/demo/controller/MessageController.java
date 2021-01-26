package com.yky.springboot.webSocket.demo.controller;

import com.yky.springboot.webSocket.demo.manager.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @version 1.0
 * @ClassName MessageController
 * @Description TODO
 * @Author YKY
 * @Date 2021/1/25 15:46
 **/
@RestController
public class MessageController {
    @Autowired
    WebSocket webSocket;

    @GetMapping("/sendTo")
    public String sendTo(@RequestParam("userId") String userId, @RequestParam("msg") String msg) throws IOException {
        webSocket.sendMessageTo(msg, userId);
        return "推送成功";
    }

    @GetMapping("/sendAll")
    public String sendAll(@RequestParam("msg") String msg) throws IOException {
        webSocket.sendMessageAll(msg);
        return "推送成功";
    }
}
