package controller.admin;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;

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
            System.out.println("DEBUG: AdminOrdersServlet initialized successfully");
        } catch (Exception e) {
            System.err.println("ERROR: AdminOrdersServlet initialization failed: " + e.getMessage());
            e.printStackTrace();
            throw new ServletException("Failed to initialize AdminOrdersServlet", e);
        }
    }
    
    /**
     * Handles GET requests - displays the list of all orders
     */
    // Update the doGet method to include delivery people    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        System.out.println("DEBUG: AdminOrdersServlet doGet() called");
        
        // Check if servlet is properly initialized
        if (!initialized) {
            System.out.println("ERROR: AdminOrdersServlet not properly initialized, reinitializing...");
            try {
                orderService = new OrderService();
                userService = new UserService();
                initialized = true;
                System.out.println("DEBUG: AdminOrdersServlet reinitialized successfully");
            } catch (Exception e) {
                System.err.println("ERROR: AdminOrdersServlet reinitialization failed: " + e.getMessage());
                e.printStackTrace();
                throw new ServletException("Failed to reinitialize AdminOrdersServlet", e);
            }
        }
        
        // Check if logged in and has ADMIN role
        User user = getLoggedInUser(req);
        if (user != null) {
            System.out.println("DEBUG: User logged in: " + user.getUsername() + ", Role: " + user.getRole());
        } else {
            System.out.println("DEBUG: No user logged in");
        }
        
        if (!requireLogin(req, resp) || !requireRole(req, resp, User.ROLE_ADMIN)) {
            System.out.println("DEBUG: Authentication check failed");
            return;
        }
        
        System.out.println("DEBUG: Authentication check passed");
          try {
            System.out.println("DEBUG: Fetching orders from orderService.getAllOrders()");
            List<Order> orders = orderService.getAllOrders();
            System.out.println("DEBUG: Successfully fetched " + orders.size() + " orders");
            
            System.out.println("DEBUG: Fetching delivery persons from userService.getDeliveryPersons()");
            List<User> deliveryPersons = userService.getDeliveryPersons();
            System.out.println("DEBUG: Successfully fetched " + deliveryPersons.size() + " delivery persons");
            
            req.setAttribute("orders", orders);
            req.setAttribute("deliveryPersons", deliveryPersons);
            
            System.out.println("DEBUG: Forwarding to /WEB-INF/views/admin/orders.jsp");
            req.getRequestDispatcher("/WEB-INF/views/admin/orders.jsp").forward(req, resp);
        } catch (ServiceException e) {
            e.printStackTrace();
            getServletContext().log("Error getting orders for admin", e);
            req.setAttribute("error", "An error occurred while retrieving orders: " + e.getMessage());
            req.setAttribute("errorDetail", e.toString());
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            getServletContext().log("Unexpected error in AdminOrdersServlet", e);
            req.setAttribute("error", "An unexpected error occurred: " + e.getMessage());
            req.setAttribute("errorDetail", e.toString());
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        }
    }    // Update the doPost method to handle order assignment
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        System.out.println("DEBUG: AdminOrdersServlet doPost() called");
        
        // Check if logged in and has ADMIN role
        User user = getLoggedInUser(req);
        if (user != null) {
            System.out.println("DEBUG: User logged in: " + user.getUsername() + ", Role: " + user.getRole());
        } else {
            System.out.println("DEBUG: No user logged in");
        }
        
        if (!requireLogin(req, resp) || !requireRole(req, resp, User.ROLE_ADMIN)) {
            System.out.println("DEBUG: Authentication check failed");
            return;
        }
        
        System.out.println("DEBUG: Authentication check passed");
        
        try {
            String orderIdParam = req.getParameter("orderId");
            String action = req.getParameter("action");
            
            System.out.println("DEBUG: Order ID parameter: " + orderIdParam);
            System.out.println("DEBUG: Action parameter: " + action);
            
            if (orderIdParam == null || orderIdParam.trim().isEmpty()) {
                throw new ValidationException("Order ID is required");
            }
            
            int orderId = Integer.parseInt(orderIdParam);
            
            if ("update-status".equals(action)) {
                String status = req.getParameter("status");
                System.out.println("DEBUG: Updating order " + orderId + " status to: " + status);
                
                orderService.updateOrderStatus(orderId, status);
                setFlashMessage(req, "success", "Order status updated successfully.");
            } 
            else if ("assign".equals(action)) {
                String deliveryPersonIdParam = req.getParameter("deliveryPersonId");
                System.out.println("DEBUG: Delivery person ID parameter: " + deliveryPersonIdParam);
                
                if (deliveryPersonIdParam == null || deliveryPersonIdParam.trim().isEmpty()) {
                    throw new ValidationException("Delivery person must be selected");
                }
                
                int deliveryPersonId = Integer.parseInt(deliveryPersonIdParam);
                System.out.println("DEBUG: Assigning order " + orderId + " to delivery person: " + deliveryPersonId);
                
                orderService.assignOrderToDeliveryPerson(orderId, deliveryPersonId);
                setFlashMessage(req, "success", "Order assigned to delivery person successfully.");
            } else {
                System.out.println("DEBUG: Unknown action: " + action);
                setFlashMessage(req, "error", "Unknown action specified");
            }
            
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
            
        } catch (ValidationException e) {
            System.err.println("DEBUG: Validation error: " + e.getMessage());
            setFlashMessage(req, "error", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
            
        } catch (ServiceException e) {
            System.err.println("DEBUG: Service error: " + e.getMessage());
            e.printStackTrace();
            getServletContext().log("Error updating order", e);
            setFlashMessage(req, "error", "An error occurred while updating the order: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
            
        } catch (NumberFormatException e) {
            System.err.println("DEBUG: Number format error: " + e.getMessage());
            setFlashMessage(req, "error", "Invalid ID format");
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
            
        } catch (Exception e) {
            System.err.println("DEBUG: Unexpected error in doPost: " + e.getMessage());
            e.printStackTrace();
            setFlashMessage(req, "error", "An unexpected error occurred: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
        }
    }
}