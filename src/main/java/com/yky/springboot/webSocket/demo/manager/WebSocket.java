package com.yky.springboot.webSocket.demo.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @ClassName WebSocket
 * @Description TODO
 * @Author YKY
 * @Date 2021/1/25 15:16
 **/
@Slf4j
@Component
@ServerEndpoint(value = "/connectWebSocket/{userId}")
public class WebSocket {

    /**
     * 在线人数
     */
    public static int onlineNumber = 0;

    /**
     * 以用户的姓名为key，WebSocket为对象保存起来
     */
    private static Map<String, WebSocket> clients = new ConcurrentHashMap<>();

    /**
     * 会话
     */
    private Session session;

    /**
     * 用户名称
     */
    private String userId;

    /**
     * 建立连接
     */
    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) {
        onlineNumber++;
        log.info("现在来链接的客户id：" + session.getId() + "用户名：" + userId);
        this.userId = userId;
        this.session = session;
        log.info("有新连接加入！ 当前在线人数" + onlineNumber);
        try {
            //messageType 1代表上线 2代表下线 3代表在线名单 4代表普通消息
            //先给所有人发送通知，说我上线了
            Map<String, Object> map1 = new HashMap<>();
            map1.put("messageType", 1);
            sendMessageAll(JSON.toJSONString(map1));

            //把自己的信息加入到 map 当中去
            clients.put(userId, this);
            log.info("有连接关闭！ 当前在线人数" + clients.size());
            //给自己发一条消息：告诉自己现在都有谁在线
            Map<String, Object> map2 = new HashMap<>();
            map2.put("messageType", 3);
            //移除掉自己
            Set<String> set = clients.keySet();
            map2.put("onlineUsers", set);
            sendMessageTo(JSON.toJSONString(map2), userId);
        } catch (IOException e) {
            log.info(userId + "上线的时候通知所有人发生了错误");
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.info("服务端发生了错误" + error.getMessage());
        //error.printStackTrace();
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose() {
        onlineNumber--;
        //webSockets.remove(this);
        clients.remove(userId);
        try {
            //messageType 1代表上线 2代表下线 3代表在线名单  4代表普通消息
            Map<String, Object> map1 = new HashMap<>();
            map1.put("messageType", 2);
            map1.put("onlineUsers", clients.keySet());
            sendMessageAll(JSON.toJSONString(map1));
        } catch (IOException e) {
            log.info(userId + "下线的时候通知所有人发生了错误");
        }
        //log.info("有连接关闭！ 当前在线人数" + onlineNumber);
        log.info("有连接关闭！ 当前在线人数" + clients.size());
    }

    /**
     * 收到客户端的消息
     *
     * @param message 消息
     * @param session 会话
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            log.info("来自客户端消息：" + message + "客户端的id是：" + session.getId());

            System.out.println("------------  :" + message);

            JSONObject jsonObject = JSON.parseObject(message);
            String textMessage = jsonObject.getString("message");
            String fromuserId = jsonObject.getString("userId");
            String touserId = jsonObject.getString("to");
            //如果不是发给所有，那么就发给某一个人
            //messageType 1代表上线 2代表下线 3代表在线名单  4代表普通消息
            Map<String, Object> map1 = new HashMap<>();
            map1.put("messageType", 4);
            map1.put("textMessage", textMessage);
            if (touserId.equals("All")) {
                map1.put("touserId", "所有人");
                sendMessageAll(JSON.toJSONString(map1));
            } else {
                map1.put("touserId", touserId);
                System.out.println("开始推送消息给" + touserId);
                sendMessageTo(JSON.toJSONString(map1), touserId);
            }
        } catch (Exception e) {

            e.printStackTrace();
            log.info("发生了错误了");
        }

    }


    public void sendMessageTo(String message, String TouserId) throws IOException {
        for (WebSocket item : clients.values()) {
            if (item.userId.equals(TouserId)) {
                System.out.println("推送信息给用户  ：" + item.userId.toString());
                item.session.getAsyncRemote().sendText(message);
                break;
            }
        }
    }

    public void sendMessageAll(String message) throws IOException {
        for (WebSocket item : clients.values()) {
            item.session.getAsyncRemote().sendText(message);
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineNumber;
    }


}
