package controller.delivery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.BaseServlet;
import model.Order;
import model.User;
import service.OrderService;
import service.ServiceException;

/**
 * Servlet for the delivery person dashboard
 */
@WebServlet("/delivery/dashboard")
public class DeliveryDashboardServlet extends BaseServlet {
    
    private OrderService orderService;
    
    @Override
    public void init() throws ServletException {
        orderService = new OrderService();
    }
    
    /**
     * Handles GET requests - displays the delivery dashboard
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Check if logged in and has DELIVERY_PERSON role
        if (!requireLogin(req, resp) || !requireRole(req, resp, User.ROLE_DELIVERY)) {
            return;
        }
        
        try {
            User deliveryPerson = getLoggedInUser(req);
            List<Order> assignedOrders = orderService.getOrdersByDeliveryPerson(deliveryPerson.getId());
            
            // Separate active and completed orders
            List<Order> activeOrders = new ArrayList<>();
            List<Order> completedOrders = new ArrayList<>();
            
            for (Order order : assignedOrders) {
                if ("DELIVERED".equals(order.getOrderStatus()) || "CANCELLED".equals(order.getOrderStatus())) {
                    completedOrders.add(order);
                } else {
                    activeOrders.add(order);
                }
            }
            
            req.setAttribute("activeOrders", activeOrders);
            req.setAttribute("completedOrders", completedOrders);
            req.getRequestDispatcher("/WEB-INF/views/delivery/dashboard.jsp").forward(req, resp);
        } catch (ServiceException e) {
            getServletContext().log("Error loading delivery dashboard", e);
            req.setAttribute("error", "An error occurred while loading the dashboard.");
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        }
    }
}