package com.xiao;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import skierserver.dao.SkiDao;
import skierserver.model.SkierData;
import skierserver.model.SkierStats;
import skierserver.utils.SQSClient;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/")
public class MyResource {
    @Context
    ServletContext context;

    @GET
    @Path("myvert")
    @Produces(MediaType.TEXT_PLAIN)
    public String getVerticals(@QueryParam("skierId") int skierId,
                               @QueryParam("dayNum") int dayNum) {

        Map<Integer, Map<Integer, SkierStats>> skierDayStatsMap =
                (Map<Integer, Map<Integer, SkierStats>>) context.getAttribute("skierDayStatsMap");

        if (skierDayStatsMap == null || skierDayStatsMap.size() == 0) {
            skierDayStatsMap = updateDayStats(dayNum);
        }

        if (skierDayStatsMap.containsKey(skierId)) {
            SkierStats skierStats = skierDayStatsMap.get(skierId).get(dayNum);
            return String.valueOf(skierStats.getVertical());
        } else {
            return "0";
        }

    }

    public Map<Integer, Map<Integer, SkierStats>> updateDayStats(int dayNum) {

        List<SkierStats> skierStatsList = new ArrayList<>();

        SkiDao skiDao = new SkiDao();

        try {
            skierStatsList = skiDao.getSkiersDaystats(dayNum);
            System.out.println(skierStatsList.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Map<Integer, Map<Integer, SkierStats>> skierDayStatsMap
                = processSkierDayStats(skierStatsList);

        context.setAttribute("skierDayStatsMap", skierDayStatsMap);

        return skierDayStatsMap;
    }

    public Map<Integer, Map<Integer, SkierStats>> processSkierDayStats(List<SkierStats> skierDayStatsList) {
        Map<Integer, Map<Integer, SkierStats>> skierDayStatsMap = new HashMap<>();

        for (SkierStats skierStats : skierDayStatsList) {
            int skierId = skierStats.getSkierId();
            int dayNum = skierStats.getDayNum();

            Map<Integer, SkierStats> dayStatsMap = new HashMap<>();
            dayStatsMap.put(dayNum, skierStats);
            skierDayStatsMap.put(skierId, dayStatsMap);
        }

        return skierDayStatsMap;
    }

    @POST
    @Path("load")
    @Consumes(MediaType.TEXT_PLAIN)
    public String postData(String str) {
        long postStartTime = System.currentTimeMillis();

        SkierData skierData = renderSkierData(str);
        String message = skierData.objToJson(skierData);

        SQSClient sqsClient = (SQSClient) context.getAttribute("sqsClient");
        String cacheQueueUrl = "https://sqs.us-west-2.amazonaws.com/358115688307/CacheQueue";
        String metricsQueueUrl = "https://sqs.us-west-2.amazonaws.com/358115688307/MetricsQueue";
        AmazonSQS sqs = sqsClient.getSQS();

        try {
            sqs.sendMessage(new SendMessageRequest(cacheQueueUrl, message));
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it " +
                    "to Amazon SQS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered " +
                    "a serious internal problem while trying to communicate with SQS, such as not " +
                    "being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }

        // send metrics to MetricsQueue
        long postEndTime = System.currentTimeMillis();
        long latency = postEndTime - postStartTime;
        System.out.println(latency);

        try {
            sqs.sendMessage(new SendMessageRequest(metricsQueueUrl, String.valueOf(latency) + ",3"));
        } catch (AmazonServiceException ase) {
            System.out.println("Error Message:    " + ase.getMessage());
        } catch (AmazonClientException ace) {
            System.out.println("Error Message: " + ace.getMessage());
        }

        return skierData.toString();

    }

    private SkierData renderSkierData(String str) {
        String[] arr = str.split(",");

        int resortID = Integer.parseInt(arr[0]);
        int dayNum = Integer.parseInt(arr[1]);
        int skierID = Integer.parseInt(arr[2]);
        int liftID = Integer.parseInt(arr[3]);
        int time = Integer.parseInt(arr[4]);
        int vertical = getVertical(skierID);

        return new SkierData(resortID, dayNum, skierID, liftID, time, vertical);
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
