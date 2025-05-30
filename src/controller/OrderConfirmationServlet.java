package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Order;
import service.OrderService;
import service.ServiceException;

/**
 * Servlet for showing order confirmation
 */
@WebServlet("/checkout/confirmation")
public class OrderConfirmationServlet extends BaseServlet {
    
    private OrderService orderService;
    
    @Override
    public void init() throws ServletException {
        orderService = new OrderService();
    }
    
    /**
     * Handles GET requests - displays the order confirmation
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Must be logged in to see confirmation
        if (!requireLogin(req, resp)) {
            return;
        }
        
        // Get the last order ID from the session
        HttpSession session = req.getSession();
        Integer lastOrderId = (Integer) session.getAttribute("lastOrderId");
        
        // If there's no order ID, redirect to orders list
        if (lastOrderId == null) {
            resp.sendRedirect(req.getContextPath() + "/orders");
            return;
        }
        
        try {
            // Get the order details
            Order order = orderService.getOrderById(lastOrderId);
            
            // Check if the order belongs to the current user
            if (order == null || order.getUserId() != getLoggedInUser(req).getId()) {
                resp.sendRedirect(req.getContextPath() + "/orders");
                return;
            }
            
            // Remove the order ID from the session to prevent refresh issues
            session.removeAttribute("lastOrderId");
            
            // Set the order in the request and show the confirmation page
            req.setAttribute("order", order);
            req.getRequestDispatcher("/WEB-INF/views/checkout/confirmation.jsp").forward(req, resp);
        } catch (ServiceException e) {
            getServletContext().log("Error getting order for confirmation", e);
            setFlashMessage(req, "error", "An error occurred. Please check your orders for status.");
            resp.sendRedirect(req.getContextPath() + "/orders");
        }
    }
}