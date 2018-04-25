package define;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

/**
 * Json format defined in JsonTasksDefine.
 */
public class TaskDefine {

    @SerializedName("jobType")
    public String jobType;

    @SerializedName("jdbc")
    public String jdbc;

    @SerializedName("sql")
    public String sql;

    @SerializedName("command")
    public String command;

    @SerializedName("outputs")
    public String[] outputs;

    @SerializedName("username")
    public String username;

    @SerializedName("password")
    public String password;

    @Override
    public String toString() {
        return "TaskDefine{" +
                "jobType='" + jobType + '\'' +
                ", jdbc='" + jdbc + '\'' +
                ", sql='" + sql + '\'' +
                ", command='" + command + '\'' +
                ", outputs=" + Arrays.toString(outputs) +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
