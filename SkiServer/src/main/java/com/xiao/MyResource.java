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
        int vertial = getVertical(data.getLiftID());

        skiDao.create(data, vertial);
        System.out.println(data.toString());

    }

    public int getVertical(int liftId) {
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
