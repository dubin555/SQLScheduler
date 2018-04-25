package define;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

/**
 * Json format define in /jobs Post json
 */
public class JsonTasksDefine {

    @SerializedName("starttime")
    public int starttime;

    @SerializedName("freq")
    public int freq;

    @SerializedName("category")
    public String category;

    @SerializedName("tasks")
    public TaskDefine[] tasks;

    @Override
    public String toString() {
        return "JsonTasksDefine{" +
                "starttime=" + starttime +
                ", freq=" + freq +
                ", category='" + category + '\'' +
                ", tasks=" + Arrays.toString(tasks) +
                '}';
    }
}
