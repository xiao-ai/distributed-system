package skiserver.dao;

import com.xiao.RFIDLiftData;

import java.sql.*;

public class SkiDao {
    protected ConnectionManager connectionManager;

    public SkiDao() {
        connectionManager = new ConnectionManager();
    }

    public void create(RFIDLiftData rFIDLiftData, int vertical) throws SQLException {
        String insertData = "INSERT INTO RFIDLiftData " +
                "(resortId, dayNum, skierId, liftId, time, vertical) " +
                "VALUES(?, ?, ?, ?, ?, ?)";

        Connection connection = connectionManager.getConnection();
        PreparedStatement insertStmt = connection.prepareStatement(insertData);

        try {
            insertStmt.setInt(1, rFIDLiftData.getResortID());
            insertStmt.setInt(2, rFIDLiftData.getDayNum());
            insertStmt.setInt(3, rFIDLiftData.getSkierID());
            insertStmt.setInt(4, rFIDLiftData.getLiftID());
            insertStmt.setInt(5, rFIDLiftData.getTime());
            insertStmt.setInt(6, vertical);
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (insertStmt != null) {
                insertStmt.close();
            }

        }
    }

}
