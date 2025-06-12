package controller.admin;

import java.io.IOException;
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
import service.UserService;
import service.ValidationException;

/**
 * Servlet for managing orders in the admin area
 */
@WebServlet("/admin/orders")
public class AdminOrdersServlet extends BaseServlet {
    private OrderService orderService;
    private UserService userService;
    private boolean initialized = false;
    
    @Override
    public void init() throws ServletException {
        try {
            orderService = new OrderService();
            userService = new UserService();
            initialized = true;
        } catch (Exception e) {
            throw new ServletException("Failed to initialize AdminOrdersServlet", e);
        }
    }
    
    /**
     * Handles GET requests - displays the list of all orders
     */
    @Override    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // Check if servlet is properly initialized
        if (!initialized) {
            try {
                orderService = new OrderService();
                userService = new UserService();
                initialized = true;
            } catch (Exception e) {
                throw new ServletException("Failed to reinitialize AdminOrdersServlet", e);
            }
        }
        
        // Check if logged in and has ADMIN role
        if (!requireLogin(req, resp) || !requireRole(req, resp, User.ROLE_ADMIN)) {
            return;
        }
        
        try {
            List<Order> orders = orderService.getAllOrders();
            List<User> deliveryPersons = userService.getDeliveryPersons();
            
            req.setAttribute("orders", orders);
            req.setAttribute("deliveryPersons", deliveryPersons);
            
            req.getRequestDispatcher("/WEB-INF/views/admin/orders.jsp").forward(req, resp);
        } catch (ServiceException e) {
            getServletContext().log("Error getting orders for admin", e);
            req.setAttribute("error", "An error occurred while retrieving orders: " + e.getMessage());
            req.setAttribute("errorDetail", e.toString());
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        } catch (Exception e) {
            getServletContext().log("Unexpected error in AdminOrdersServlet", e);
            req.setAttribute("error", "An unexpected error occurred: " + e.getMessage());
            req.setAttribute("errorDetail", e.toString());
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        }
    }    // Update the doPost method to handle order assignment    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // Check if logged in and has ADMIN role
        if (!requireLogin(req, resp) || !requireRole(req, resp, User.ROLE_ADMIN)) {
            return;
        }
        
        try {
            String orderIdParam = req.getParameter("orderId");
            String action = req.getParameter("action");
            
            if (orderIdParam == null || orderIdParam.trim().isEmpty()) {
                throw new ValidationException("Order ID is required");
            }
            
            int orderId = Integer.parseInt(orderIdParam);
            
            if ("update-status".equals(action)) {
                String status = req.getParameter("status");
                orderService.updateOrderStatus(orderId, status);
                setFlashMessage(req, "success", "Order status updated successfully.");
            } 
            else if ("assign".equals(action)) {
                String deliveryPersonIdParam = req.getParameter("deliveryPersonId");
                
                if (deliveryPersonIdParam == null || deliveryPersonIdParam.trim().isEmpty()) {
                    throw new ValidationException("Delivery person must be selected");
                }
                
                int deliveryPersonId = Integer.parseInt(deliveryPersonIdParam);
                orderService.assignOrderToDeliveryPerson(orderId, deliveryPersonId);
                setFlashMessage(req, "success", "Order assigned to delivery person successfully.");
            } else {
                setFlashMessage(req, "error", "Unknown action specified");
            }
            
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
            
        } catch (ValidationException e) {
            setFlashMessage(req, "error", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
            
        } catch (ServiceException e) {
            getServletContext().log("Error updating order", e);
            setFlashMessage(req, "error", "An error occurred while updating the order: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
            
        } catch (NumberFormatException e) {
            setFlashMessage(req, "error", "Invalid ID format");
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
            
        } catch (Exception e) {
            getServletContext().log("Unexpected error in AdminOrdersServlet.doPost", e);
            setFlashMessage(req, "error", "An unexpected error occurred: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
        }
    }
}