package com.xiao;

import skiserver.dao.ConnectionManager;
import skiserver.dao.SkiDao;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Root resource (exposed at "/" path)
 */
@Path("/")
public class MyResource {
    private SkiDao skiDao = new SkiDao();

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Path("myvert")
    @Produces(MediaType.TEXT_PLAIN)
    public String getVerticals(@QueryParam("skierId") String skierId,
                               @QueryParam("dayNum") String dayNum) {
        return skierId;
    }

    @POST
    @Path("load")
    @Consumes(MediaType.APPLICATION_JSON)
    public void postText(RFIDLiftData data) throws SQLException {

        RFIDLiftData rfidLiftData = skiDao.create(data);
        System.out.println(rfidLiftData.toString());


//        return data.getLiftID();

    }
}
