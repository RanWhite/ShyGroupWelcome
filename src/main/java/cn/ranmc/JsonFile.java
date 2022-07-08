package cn.ranmc;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class JsonFile {

    public static JSONObject readJson(String name) {
        try {
            File jsonFile = new File(System.getProperty("user.dir") + "/group/" + name + ".json");
            jsonFile.getCanonicalFile();
            return new JSONObject(FileUtils.readFileToString(jsonFile, "utf8"));
        } catch (Exception e) {
            //e.printStackTrace();
            return new JSONObject();
        }
    }

    public static void writeJosn(String name,String text) {
        try {
            File jsonFile = new File(System.getProperty("user.dir") + "/group/" + name + ".json");
            FileUtils.write(jsonFile, text, "utf8", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
