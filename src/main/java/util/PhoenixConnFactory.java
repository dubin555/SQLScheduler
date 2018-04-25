package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import static config.Constants.*;


/**
 * Factory for phoenix connection. Better make a pool.
 * @ TODO: 16/04/2018 add a pool for the connection, but jdbc may differ from each other, may need a ConcurrentHashMap or so.
 */
public class PhoenixConnFactory {

    private static Logger logger = LoggerFactory.getLogger(PhoenixConnFactory.class.getName());

    public static Connection getConn(String jdbc) {
        try {
            Class.forName(PHOENIX_DRIVER);
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
        }

        try {
            // The SQL may be running for a long time, so proper higher timeout needed.
            Properties props = new Properties();
            props.setProperty(HBASE_RPC_TIMEOUT_KEY, HBASE_RPC_TIMEOUT);
            props.setProperty(HBASE_CLIENT_OPERATION_TIMEOUT_KEY, HBASE_CLIENT_OPERATION_TIMEOUT);
            props.setProperty(HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD_KEY, HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD);
            return DriverManager.getConnection(jdbc, props);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return null;
    }
}
