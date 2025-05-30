package controller.admin;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.BaseServlet;
import model.Order;
import service.OrderService;
import service.ServiceException;

/**
 * Servlet for the admin dashboard
 */
@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends BaseServlet {
    
    private OrderService orderService;
    
    @Override
    public void init() throws ServletException {
        orderService = new OrderService();
    }
    
    /**
     * Handles GET requests - displays the admin dashboard
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Check if logged in and has ADMIN role
        if (!requireLogin(req, resp) || !requireRole(req, resp, "ADMIN")) {
            return;
        }
        
        try {
            // Get recent orders for the dashboard
            List<Order> recentOrders = orderService.getAllOrders();
            req.setAttribute("recentOrders", recentOrders);
            
            req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
        } catch (ServiceException e) {
            getServletContext().log("Error loading admin dashboard", e);
            req.setAttribute("error", "An error occurred while loading the dashboard.");
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        }
    }
}