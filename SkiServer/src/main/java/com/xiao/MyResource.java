package com.xiao;

import org.omg.CORBA.INTERNAL;
import skiserver.dao.SkiDao;
import skiserver.model.RFIDLiftData;
import skiserver.model.SkierStats;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Root resource (exposed at "/" path)
 */
@Path("/")
public class MyResource {

    @Context
    ServletContext context;

    @GET
    @Path("myvert")
    @Produces(MediaType.TEXT_PLAIN)
    public String getVerticals(@QueryParam("skierId") String skierId,
                               @QueryParam("dayNum") String dayNum) throws SQLException {
        Map<Integer, SkierStats> skierStasMap = (Map<Integer, SkierStats>) context.getAttribute("skierStasMap");
        SkierStats skierStats = skierStasMap.get(Integer.parseInt(skierId));
        return skierStats.toString();
    }

    @POST
    @Path("load")
    @Consumes(MediaType.TEXT_PLAIN)
    public String postData(String str) throws SQLException {
        String[] arr = str.split(",");

        int resortID = Integer.parseInt(arr[0]);
        int dayNum = Integer.parseInt(arr[1]);
        int skierID = Integer.parseInt(arr[2]);
        int liftID = Integer.parseInt(arr[3]);
        int time = Integer.parseInt(arr[4]);
        int vertical = getVertical(skierID);

        RFIDLiftData data = new RFIDLiftData(resortID, dayNum, skierID, liftID, time, vertical);

        List<RFIDLiftData> cachedDataList = (List<RFIDLiftData>) context.getAttribute("cachedDataList");
        Map<Integer, SkierStats> skierStasMap = (Map<Integer, SkierStats>) context.getAttribute("skierStasMap");

        if (resortID == -1) {
            System.out.println("final");
            SkiDao skiDao = new SkiDao();
            skiDao.batchCreate(cachedDataList);
        } else if (cachedDataList.size() <= 5000) {
            // add data to list
            System.out.println("adding to cache");
            cachedDataList.add(data);

            if (skierStasMap.containsKey(skierID)) {
                SkierStats skierStats = skierStasMap.get(skierID);
                int verticalSum = skierStats.getVerticalSum();
                int count = skierStats.getCount();

                skierStats.setCount(count + 1);
                skierStats.setVerticalSum(verticalSum + vertical);

                skierStasMap.put(skierID, skierStats);
            } else {
                SkierStats skierStats = new SkierStats(skierID, dayNum, vertical, 1);
                skierStasMap.put(skierID, skierStats);
            }
        } else {
            // reset cachedDataList
            context.setAttribute("cachedDataList", new ArrayList<RFIDLiftData>());

            cachedDataList.add(data);

            // batch insert
            System.out.println("begin batch insert");
            SkiDao skiDao = new SkiDao();
            skiDao.batchCreate(cachedDataList);

        }
        return "" + data.getSkierID();
    }

    private int getVertical(int liftId) {
        if (liftId >= 1 && liftId <= 10) {
            return 200;
        } else if (liftId >= 11 && liftId <= 20) {
            return 300;
        } else if (liftId >= 21 && liftId <= 30) {
            return 400;
        } else {
            return 500;
        }
    }

}
