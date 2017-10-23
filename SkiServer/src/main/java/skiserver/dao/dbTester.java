package skiserver.dao;

import com.xiao.RFIDLiftData;

import java.sql.SQLException;

public class dbTester {
    public static void main(String[] args) throws SQLException {
        SkiDao skiDao = new SkiDao();
        RFIDLiftData rfidLiftData = new RFIDLiftData(1, 1, 1, 1, 1);
        skiDao.create(rfidLiftData);
    }
}
