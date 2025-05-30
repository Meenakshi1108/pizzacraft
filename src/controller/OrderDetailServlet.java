package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Order;
import model.User;
import service.OrderService;
import service.ServiceException;

/**
 * Servlet for displaying order details
 */
@WebServlet("/orders/*")
public class OrderDetailServlet extends BaseServlet {
    
    private OrderService orderService;
    
    @Override
    public void init() throws ServletException {
        orderService = new OrderService();
    }
    
    /**
     * Handles GET requests - displays the details of a specific order
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Must be logged in to see order details
        if (!requireLogin(req, resp)) {
            return;
        }
        
        try {
            // Extract the order ID from the URL path
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.sendRedirect(req.getContextPath() + "/orders");
                return;
            }
            
            int orderId;
            try {
                orderId = Integer.parseInt(pathInfo.substring(1));
            } catch (NumberFormatException e) {
                resp.sendRedirect(req.getContextPath() + "/orders");
                return;
            }
            
            // Get the order details
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                // Order not found
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            // Check if the order belongs to the current user (unless admin)
            User user = getLoggedInUser(req);
            if (order.getUserId() != user.getId() && !hasRole(req, "ADMIN")) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            
            // Set the order in the request and show the details page
            req.setAttribute("order", order);
            req.getRequestDispatcher("/WEB-INF/views/orders/detail.jsp").forward(req, resp);
        } catch (ServiceException e) {
            getServletContext().log("Error getting order details", e);
            req.setAttribute("error", "An error occurred while retrieving the order details.");
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        }
    }
}