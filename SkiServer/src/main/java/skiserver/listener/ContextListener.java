package skiserver.listener;

import skiserver.model.RFIDLiftData;
import skiserver.model.SkierStats;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext context = servletContextEvent.getServletContext();

        List<RFIDLiftData> cachedDataList = new ArrayList<>();
        Map<Integer, SkierStats> skierStasMap = new HashMap<>();

        context.setAttribute("cachedDataList", cachedDataList);
        context.setAttribute("skierStasMap", skierStasMap);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("context destroyed");
    }
}
