package skierserver.context;

import com.amazonaws.services.sqs.AmazonSQS;
import skierserver.utils.SQSClient;
import skierserver.utils.Scanner;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ContextListener implements ServletContextListener {
    public static AmazonSQS sqs;
    private static ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext context = servletContextEvent.getServletContext();

        // get sqs
        SQSClient sqsClient = SQSClient.getInstance();

        // add sqs to context
        context.setAttribute("sqsClient", sqsClient);

        // set up scanner
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Scanner(context), 5, 5, TimeUnit.SECONDS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("context destroyed");
        if (scheduler != null) {
            scheduler.shutdown();
        }

    }
}
