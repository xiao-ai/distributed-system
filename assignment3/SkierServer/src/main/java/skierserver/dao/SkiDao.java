package skierserver.dao;

import skierserver.model.SkierData;
import skierserver.model.SkierStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SkiDao {
    private ConnectionManager connectionManager;
    private Connection connection;

    public SkiDao() {
        connectionManager = new ConnectionManager();
    }

    public int bacthInsert(List<SkierData> skierDataList) throws SQLException {
        int failedCount = 0;

        String query = "INSERT INTO SkierData " +
                "(resortId, dayNum, skierId, liftId, time, vertical) " +
                "VALUES(?, ?, ?, ?, ?, ?)";

        PreparedStatement statement = null;

        try {
            connection = connectionManager.getConnection();
            statement = connection.prepareStatement(query);
            for (SkierData skierData : skierDataList) {
                statement.setInt(1, skierData.getResortID());
                statement.setInt(2, skierData.getDayNum());
                statement.setInt(3, skierData.getSkierID());
                statement.setInt(4, skierData.getLiftID());
                statement.setInt(5, skierData.getTime());
                statement.setInt(6, skierData.getVertical());
                statement.addBatch();
            }

            // log failed ones
            int[] results = statement.executeBatch();

            for (int i = 0; i < results.length; ++i) {
                if (results[i] == statement.EXECUTE_FAILED) {
                    failedCount++;
                }
            }

            System.out.println(skierDataList.size() + " inserted to db, failed " + failedCount);

        } finally {
            if (connection != null) {
                connection.close();
            }
            if (statement != null) {
                statement.close();
            }
        }

        return failedCount;
    }

    public List<SkierStats> getSkiersDaystats(int dayNum) throws SQLException {
        List<SkierStats> skierStatsList = new ArrayList<>();

        String query = "SELECT skierId, dayNum, verticals FROM SkierStats WHERE dayNum=?";
        PreparedStatement statement = null;
        ResultSet results = null;

        try {
            connection = connectionManager.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, dayNum);
            results = statement.executeQuery();

            while (results.next()) {
                int skierId = results.getInt("skierId");
                int verticals = results.getInt("verticals");

                skierStatsList.add(new SkierStats(skierId, dayNum, verticals));
            }

        } finally {
            if (connection != null) {
                connection.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (results != null) {
                results.close();
            }
        }

        return skierStatsList;
    }

    public void updateSkierStatsView() throws SQLException {

        String query = "CREATE OR REPLACE VIEW SkierStats AS " +
                "SELECT skierID, dayNum, sum(vertical) AS verticals " +
                "FROM SkierData " +
                "GROUP BY skierID;";

        PreparedStatement statement = null;

        try {
            connection = connectionManager.getConnection();
            statement = connection.prepareStatement(query);

            statement.executeQuery();
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
    }


    public int getVerticals(int skierId, int dayNum) throws SQLException {
        String query = "SELECT verticals FROM SkierStats WHERE skierId=? AND dayNum=?";
        PreparedStatement statement = null;
        ResultSet results = null;

        int verticals = 0;

        try {
            connection = connectionManager.getConnection();
            statement = connection.prepareStatement(query);

            statement.setInt(1, skierId);
            statement.setInt(2, dayNum);

            results = statement.executeQuery();

            if (results.next()) {
                verticals = results.getInt("verticals");
            }

            System.out.println(verticals);

        } finally {
            if (connection != null) {
                connection.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (results != null) {
                results.close();
            }
        }

        return verticals;
    }
}
