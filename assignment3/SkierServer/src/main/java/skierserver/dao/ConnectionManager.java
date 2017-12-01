package skierserver.dao;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {
    private static final String hostName = "dbms.cg9fmive4zgh.us-west-2.rds.amazonaws.com";
    private static final int port = 3306;
    private static final String schema = "skidatabase";

    public Connection getConnection() throws SQLException {
        Connection connection = null;

        try {
            Properties connectionProperties = new Properties();
            connectionProperties.put("user", System.getenv("dbusername"));
            connectionProperties.put("password", System.getenv("dbpassword"));

            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new SQLException(e);
            }
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + hostName + ":" + port + "/" + schema +
                            "?rewriteBatchedStatements=true&relaxAutoCommit=true",
                    connectionProperties);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return connection;
    }

    /**
     * Close the connection to the database instance.
     */
    public void closeConnection(Connection connection) throws SQLException {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
