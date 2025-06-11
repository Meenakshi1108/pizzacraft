package listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import database.DatabaseConnection;

@WebListener
public class AppContextListener implements ServletContextListener {
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DatabaseConnection.shutdown();
    }
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Initialization code if needed
    }
}