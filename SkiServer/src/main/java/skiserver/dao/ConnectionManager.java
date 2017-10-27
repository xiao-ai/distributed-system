package skiserver.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {

    private final String user = "aix";
    private final String password = "xihaxiao";
    private final String hostName = "rds-mysql.cg9fmive4zgh.us-west-2.rds.amazonaws.com";
    private final int port = 3306;
    private final String schema = "skidatabase";

    public Connection getConnection() throws SQLException {
        Connection connection = null;

        try {
            Properties connectionProperties = new Properties();
            connectionProperties.put("user", this.user);
            connectionProperties.put("password", this.password);

            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new SQLException(e);
            }
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + this.hostName + ":" + this.port + "/" + this.schema +
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
