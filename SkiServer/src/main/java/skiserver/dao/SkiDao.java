package skiserver.dao;

import skiserver.model.RFIDLiftData;

import java.sql.*;
import java.util.List;

public class SkiDao {

    private ConnectionManager connectionManager;

    public SkiDao() throws SQLException {
        connectionManager = new ConnectionManager();
    }

    public void batchCreate(List<RFIDLiftData> cachedDataList) throws SQLException {
        Connection connection = connectionManager.getConnection();

        String insert = "INSERT INTO RFIDLiftData " +
                "(resortId, dayNum, skierId, liftId, time, vertical) " +
                "VALUES(?, ?, ?, ?, ?, ?)";
        PreparedStatement insertStmt = null;

        try {
            insertStmt = connection.prepareStatement(insert);

            for (RFIDLiftData rFIDLiftData : cachedDataList) {
                insertStmt.setInt(1, rFIDLiftData.getResortID());
                insertStmt.setInt(2, rFIDLiftData.getDayNum());
                insertStmt.setInt(3, rFIDLiftData.getSkierID());
                insertStmt.setInt(4, rFIDLiftData.getLiftID());
                insertStmt.setInt(5, rFIDLiftData.getTime());
                insertStmt.setInt(6, rFIDLiftData.getVertical());
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();
            connection.commit();
            System.out.println(cachedDataList.size() + " is inserted to db.");

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            // close connection
            if (connection != null) {
                connection.close();
            }
            if (insertStmt != null) {
                insertStmt.close();
            }

        }

    }

    public String getStats(int skierId, int dayNum) throws SQLException {
        String getStats = "SELECT SUM(vertical) AS verticalSum, COUNT(skierId) AS count " +
                "FROM RFIDLiftData " +
                "WHERE skierId=? AND dayNum=? " +
                "GROUP BY skierId;";

        Connection connection = connectionManager.getConnection();
        PreparedStatement selectStmt = connection.prepareStatement(getStats);
        ResultSet results = null;
        try {
            selectStmt.setInt(1, skierId);
            selectStmt.setInt(2, dayNum);
            results = selectStmt.executeQuery();

            if (results.next()) {
                int verticalSum = results.getInt("verticalSum");
                int count = results.getInt("count");

                System.out.println(verticalSum);
                System.out.println(count);

                return "verticalSum: " + verticalSum + " count: " + count;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (selectStmt != null) {
                selectStmt.close();
            }
            if (results != null) {
                results.close();
            }

        }

        return null;
    }
}