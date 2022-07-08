package cn.ranmc;

import org.java_websocket.client.WebSocketClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Group {

    /**
     * 获取群成员
     */
    public static String getGroupUser(String wxid) {
        JSONObject obj = new JSONObject();
        obj.put("method", "getGroupUser");
        obj.put("wxid", wxid);
        obj.put("pid", "");
        return obj.toString();
    }

    /**
     * 获取群列表
     */
    public static String getGroup() {
        JSONObject obj = new JSONObject();
        obj.put("method", "getGroup");
        obj.put("pid", "");
        return obj.toString();
    }

    /**
     * 获取发送消息
     */
    public static String getSendText(String wxid, String msg) {
        JSONObject obj = new JSONObject();
        obj.put("method", "sendText");
        obj.put("pid", "");
        obj.put("atid", "");
        obj.put("wxid", wxid);
        obj.put("msg", msg);
        return obj.toString();
    }


    /**
     * 检查群成员
     * @param result
     * @return
     */
    public static void checkGroupUser(WebSocketClient client, boolean done, String wxid, String result) {
        try {
            JSONObject readJson = JsonFile.readJson(wxid);
            JSONObject obj = new JSONObject(result);
            if (obj.has("method") && obj.getString("method").equals("getGroupUser_Recv")) {
                if (done) {
                    JSONArray oldArr = readJson.getJSONArray("data");
                    JSONArray newArr = new JSONObject(result).getJSONArray("data");
                    for (Object newObj : newArr) {
                        boolean has = false;
                        for (Object oldObj : oldArr) {
                            if (((JSONObject)oldObj).getString("wxid").equals(((JSONObject)newObj).getString("wxid"))) has = true;
                        }
                        if (!has) {
                            String welcome = readJson.getString("欢迎语").replace("%nick%", ((JSONObject)newObj).getString("nickName"));
                            System.out.println(wxid + ":" + welcome);
                            client.send(getSendText(wxid, welcome));
                        }
                    }
                }
                if (readJson.has("欢迎语")) {
                    obj.put("欢迎语", readJson.getString("欢迎语"));
                } else {
                    obj.put("欢迎语", "欢迎%nick%加入本群");
                }
                JsonFile.writeJosn(wxid, obj.toString());
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    /**
     * 检查群列表
     * @param result
     * @return
     */
    public static List<String> checkGroup(String result) {
        List<String> list = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(result);
            if (obj.has("method") && obj.getString("method").equals("getGroup_Recv")) {
                JSONArray data = obj.getJSONArray("data");
                for (Object group : data) {
                    list.add(((JSONObject)group).getString("wxid"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
