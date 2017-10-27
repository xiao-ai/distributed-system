package skiserver.listener;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;

public class CacheListener implements ServletContextAttributeListener {

    @Override
    public void attributeAdded(ServletContextAttributeEvent servletContextAttributeEvent) {
        System.out.println("An attribute was added to context: " + servletContextAttributeEvent.getName());
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent servletContextAttributeEvent) {
        System.out.println("An attribute was removed to context: " + servletContextAttributeEvent.getName());
    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent servletContextAttributeEvent) {
        System.out.println("An attribute was reset: " + servletContextAttributeEvent.getName());
    }
}
