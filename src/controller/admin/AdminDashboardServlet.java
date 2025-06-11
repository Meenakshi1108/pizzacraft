package controller.admin;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.BaseServlet;
import model.Order;
import model.User;
import service.OrderService;
import service.PizzaService;
import service.UserService;
import service.ServiceException;

/**
 * Servlet for the admin dashboard
 */
@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends BaseServlet {
    
    private OrderService orderService;
    private PizzaService pizzaService;
    private UserService userService;
    
    @Override
    public void init() throws ServletException {
        orderService = new OrderService();
        pizzaService = new PizzaService();
        userService = new UserService();
    }
    
    /**
     * Handles GET requests - displays the admin dashboard
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {        // Check if logged in and has ADMIN role
        if (!requireLogin(req, resp) || !requireRole(req, resp, User.ROLE_ADMIN)) {
            return;
        }
        
        try {
            // Get recent orders for the dashboard (limit to 10)
            List<Order> recentOrders = orderService.getRecentOrders(10);
            req.setAttribute("recentOrders", recentOrders);
            
            // Calculate dashboard statistics
            Map<String, Object> dashboardStats = new HashMap<>();
            
            try {
                // Order statistics - wrap each section in its own try-catch
                dashboardStats.put("totalOrders", orderService.countAllOrders());
                dashboardStats.put("pendingOrders", orderService.countOrdersByStatus("PLACED"));
                dashboardStats.put("preparingOrders", orderService.countOrdersByStatus("PREPARING"));
                dashboardStats.put("readyOrders", orderService.countOrdersByStatus("READY"));
                dashboardStats.put("activeDeliveries", orderService.countOrdersByStatus("OUT_FOR_DELIVERY"));
                dashboardStats.put("completedOrders", orderService.countOrdersByStatus("DELIVERED"));
                dashboardStats.put("cancelledOrders", orderService.countOrdersByStatus("CANCELLED"));
            } catch (ServiceException e) {
                getServletContext().log("Error loading order statistics", e);
                dashboardStats.put("orderStatsError", true);
            }
            
            try {
                // Revenue statistics
                dashboardStats.put("totalRevenue", orderService.calculateTotalRevenue());
                dashboardStats.put("todayRevenue", orderService.calculateTodayRevenue());
                dashboardStats.put("weeklyRevenue", orderService.calculateWeeklyRevenue());
            } catch (ServiceException e) {
                getServletContext().log("Error loading revenue statistics", e);
                dashboardStats.put("revenueStatsError", true);
            }
            
            try {
                // Menu statistics
                dashboardStats.put("availablePizzas", pizzaService.countAvailablePizzas());
                dashboardStats.put("outOfStockPizzas", pizzaService.countUnavailablePizzas());
                dashboardStats.put("totalMenuItems", pizzaService.countTotalPizzas());
            } catch (ServiceException e) {
                getServletContext().log("Error loading menu statistics: " + e.getMessage(), e);
                dashboardStats.put("menuStatsError", true);
            }
            
            try {
                // User statistics
                dashboardStats.put("totalCustomers", userService.countUsersByRole("CUSTOMER"));
                dashboardStats.put("totalDeliveryPersons", userService.countUsersByRole("DELIVERY"));
                dashboardStats.put("totalUsers", userService.countAllUsers());
            } catch (ServiceException e) {
                getServletContext().log("Error loading user statistics", e);
                dashboardStats.put("userStatsError", true);
            }
            
            req.setAttribute("stats", dashboardStats);
            
            req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
        } catch (ServiceException e) {
            getServletContext().log("Error loading admin dashboard", e);
            req.setAttribute("error", "An error occurred while loading the dashboard: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        }
    }
}