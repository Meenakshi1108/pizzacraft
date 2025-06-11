package controller.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.BaseServlet;
import database.DatabaseConnection;
import model.Order;
import model.User;
import service.OrderService;
import service.ServiceException;
import service.UserService;

/**
 * Servlet for diagnosing system issues
 */
@WebServlet("/admin/diagnostic")
public class DiagnosticServlet extends BaseServlet {
    
    private OrderService orderService;
    private UserService userService;
    
    @Override
    public void init() throws ServletException {
        orderService = new OrderService();
        userService = new UserService();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Set content type
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        
        // Start HTML response
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>System Diagnostic</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 20px; }");
        out.println(".success { color: green; }");
        out.println(".error { color: red; }");
        out.println(".warning { color: orange; }");
        out.println(".section { margin-bottom: 20px; border: 1px solid #ddd; padding: 10px; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>System Diagnostic Results</h1>");
        
        // Authentication Check
        out.println("<div class='section'>");
        out.println("<h2>Authentication</h2>");
        User currentUser = getLoggedInUser(req);
        if (currentUser != null) {
            out.println("<p class='success'>User logged in: " + currentUser.getUsername() + " (Role: " + currentUser.getRole() + ")</p>");
        } else {
            out.println("<p class='error'>No user is logged in</p>");
        }
        out.println("</div>");
        
        // Database Connectivity
        out.println("<div class='section'>");
        out.println("<h2>Database Connectivity</h2>");
        try {
            long startTime = System.currentTimeMillis();
            Connection conn = DatabaseConnection.getConnection();
            long endTime = System.currentTimeMillis();
            
            out.println("<p class='success'>Database connection successful (Took " + (endTime - startTime) + "ms)</p>");
            
            // Show connection details
            out.println("<p>Connection Details:</p>");
            out.println("<ul>");
            out.println("<li>Auto-commit: " + conn.getAutoCommit() + "</li>");
            out.println("<li>Read-only: " + conn.isReadOnly() + "</li>");
            out.println("<li>Valid: " + conn.isValid(5) + "</li>");
            out.println("</ul>");
            
            conn.close();
        } catch (SQLException e) {
            out.println("<p class='error'>Database connection failed: " + e.getMessage() + "</p>");
        }
        out.println("</div>");
        
        // Delivery Persons
        out.println("<div class='section'>");
        out.println("<h2>Delivery Persons</h2>");
        try {
            List<User> deliveryPersons = userService.getDeliveryPersons();
            if (deliveryPersons != null && !deliveryPersons.isEmpty()) {
                out.println("<p class='success'>Found " + deliveryPersons.size() + " delivery persons</p>");
                out.println("<ul>");
                for (User dp : deliveryPersons) {
                    out.println("<li>ID: " + dp.getId() + ", Name: " + dp.getFullName() + ", Role: " + dp.getRole() + "</li>");
                }
                out.println("</ul>");
            } else {
                out.println("<p class='warning'>No delivery persons found</p>");
            }
        } catch (ServiceException e) {
            out.println("<p class='error'>Error fetching delivery persons: " + e.getMessage() + "</p>");
        }
        out.println("</div>");
        
        // Orders
        out.println("<div class='section'>");
        out.println("<h2>Orders</h2>");
        try {
            List<Order> orders = orderService.getAllOrders();
            if (orders != null && !orders.isEmpty()) {
                out.println("<p class='success'>Found " + orders.size() + " orders</p>");
                out.println("<ul>");
                for (Order order : orders) {
                    out.println("<li>ID: " + order.getId() + ", Status: " + order.getOrderStatus() + 
                               ", Delivery Person ID: " + order.getDeliveryPersonId() + 
                               ", Assigned To: " + order.getAssignedToUserId() + "</li>");
                }
                out.println("</ul>");
            } else {
                out.println("<p class='warning'>No orders found</p>");
            }
        } catch (ServiceException e) {
            out.println("<p class='error'>Error fetching orders: " + e.getMessage() + "</p>");
        }
        out.println("</div>");
        
        // End HTML response
        out.println("<p><a href='" + req.getContextPath() + "/admin/orders'>Return to Orders page</a></p>");
        out.println("</body>");
        out.println("</html>");
    }
}
