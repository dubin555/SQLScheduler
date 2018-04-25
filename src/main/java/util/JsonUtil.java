package util;

import com.google.gson.Gson;
import define.JsonTasksDefine;

/**
 * Util for parse json.
 */
public class JsonUtil {

    /**
     * Return if the json Post to /jobs is reasonable.
     */
    public static boolean isJobRequestJsonReasonable(String s) {
        Gson gson = new Gson();
        try {
            gson.fromJson(s, JsonTasksDefine.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Return the JsonTasksDefine if json reasonable.
     */
    public static JsonTasksDefine getJsonTasksDefineFromJson(String s) {

        if (!isJobRequestJsonReasonable(s)) {
            return null;
        }

        Gson gson = new Gson();
        return gson.fromJson(s, JsonTasksDefine.class);
    }
}
