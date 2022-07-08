package cn.ranmc;

import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    @Getter
    private static String key = "";
    private static WebSocketClient client;
    private static List<String> list = new ArrayList<>();
    private static String wxid = "null";
    private static boolean done = false;
    private static String groupList;

    public static void main(String[] args) {
        System.out.println("进群欢迎 By阿然");
        System.out.println("作者QQ 2263055528");

        HttpGet get = new HttpGet("http://127.0.0.1:8203/ext/www/key.ini");
        try {
            HttpResponse response = HttpClients.createDefault().execute(get);
            HttpEntity entity = response.getEntity();
            JSONObject obj = new JSONObject(EntityUtils.toString(entity, "UTF-8"));
            key = obj.getString("key");
            System.out.println("获取成功key");

            client = new WebSocketClient(new URI("ws://127.0.0.1:8202/wx?name=www&key=" + key),
                    new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    System.out.println("连接成功");
                }

                @Override
                public void onMessage(String result) {
                    if (groupList == null) {
                        list = Group.checkGroup(result);
                        groupList = result;
                    }
                    Group.checkGroupUser(client, done, wxid, result);
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    System.out.println("连接关闭");
                }

                @Override
                public void onError(Exception e) {
                    System.out.println("连接错误");
                }
            };
            client.connect();
            Thread.sleep(1000);
            System.out.println("获取群成员列表中...");
            //获取群列表
            client.send(Group.getGroup());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("启动失败");
        }

        new Timer().schedule(new TimerTask() {
            public void run() {

                if (done) {
                    if (list.isEmpty()) {
                        list = Group.checkGroup(groupList);
                    } else {
                        client.send(Group.getGroupUser(list.get(0)));
                        wxid = list.get(0);
                        list.remove(wxid);
                    }
                } else {
                    if (list.isEmpty()) {
                        System.out.println("获取群成员列表成功");
                        done = true;
                    } else {
                        client.send(Group.getGroupUser(list.get(0)));
                        wxid = list.get(0);
                        list.remove(wxid);
                    }
                }

            }
        }, 200L, 200L);

    }


}
