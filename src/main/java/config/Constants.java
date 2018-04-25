package config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Constants for the application, base on application.conf
 */
public class Constants {

    private static Config config = ConfigFactory.load();

    // RingSet setting
    public static final int HOURS_IN_DAY = 24;
    public static final int MINUTES_IN_HOUR = 60;
    public static final int MINUTES_IN_DAY = HOURS_IN_DAY * MINUTES_IN_HOUR;

    // Command template setting
    public static final String VARIABLE_SIGN = "\\$\\{(\\w+)\\}";

    // Phoenix & HBase setting
    public static String HBASE_RPC_TIMEOUT;

    public static final String HBASE_RPC_TIMEOUT_KEY = "hbase.rpc.timeout";

    public static String HBASE_CLIENT_OPERATION_TIMEOUT;

    public static final String HBASE_CLIENT_OPERATION_TIMEOUT_KEY = "hbase.client.operation.timeout";

    public static String HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD;

    public static final String HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD_KEY = "hbase.client.scanner.timeout.period";

    public static String PHOENIX_DRIVER = "org.apache.phoenix.jdbc.PhoenixDriver";

    public static int CONCURRENCY_SHELL;

    public static final String CONCURRENCY_SHELL_KEY = "app.concurrency.shell";

    public static int CONCURRENCY_PHOENIX;

    public static final String CONCURRENCY_PHOENIX_KEY = "app.concurrency.phoenix";

    public static int CONCURRENCY_WORKERS;

    public static final String CONCURRENCY_WORKERS_KEY = "app.concurrency.workers";

    public static String BIND_ADDRESS;

    public static final String BIND_ADDRESS_KEY = "app.web.address";

    public static int BIND_PORT;

    public static final String BIND_PORT_KEY = "app.web.port";

    static {
        HBASE_RPC_TIMEOUT = config.getString(HBASE_RPC_TIMEOUT_KEY);
        HBASE_CLIENT_OPERATION_TIMEOUT = config.getString(HBASE_CLIENT_OPERATION_TIMEOUT_KEY);
        HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD = config.getString(HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD_KEY);
        CONCURRENCY_SHELL = config.getInt(CONCURRENCY_SHELL_KEY);
        CONCURRENCY_PHOENIX = config.getInt(CONCURRENCY_PHOENIX_KEY);
        CONCURRENCY_WORKERS = config.getInt(CONCURRENCY_WORKERS_KEY);
        BIND_ADDRESS = config.getString(BIND_ADDRESS_KEY);
        BIND_PORT = config.getInt(BIND_PORT_KEY);
    }

}
