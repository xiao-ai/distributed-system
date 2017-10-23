package skiserver.dao;

import com.xiao.RFIDLiftData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SkiDao {
    protected ConnectionManager connectionManager;
//    protected Connection connection;

    public SkiDao()  {
        connectionManager = new ConnectionManager();
    }

    public RFIDLiftData create(RFIDLiftData rFIDLiftData) throws SQLException {
        String insertCost = "INSERT INTO RFIDLiftData " +
                "(resortId, dayNum, skierId, liftId, time) " +
                "VALUES(?, ?, ?, ?, ?)";

        Connection connection = connectionManager.getConnection();
        PreparedStatement insertStmt = connection.prepareStatement(insertCost);

        try {
            insertStmt.setInt(1, rFIDLiftData.getResortID());
            insertStmt.setInt(2, rFIDLiftData.getDayNum());
            insertStmt.setInt(3, rFIDLiftData.getSkierID());
            insertStmt.setInt(4, rFIDLiftData.getLiftID());
            insertStmt.setInt(5, rFIDLiftData.getTime());

            insertStmt.executeUpdate();
            return rFIDLiftData;
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
