package controller.admin;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
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
public class AdminOrdersServlet extends BaseServlet {
    
    private OrderService orderService;
    private UserService userService;
    @Override
    public void init() throws ServletException {
        orderService = new OrderService();
        userService = new UserService();
    }
    
    /**
     * Handles GET requests - displays the list of all orders
     */
    // Update the doGet method to include delivery people
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Check if logged in and has ADMIN role
        if (!requireLogin(req, resp) || !requireRole(req, resp, User.ROLE_ADMIN)) {
            return;
        }
        
        try {
            List<Order> orders = orderService.getAllOrders();
            req.setAttribute("orders", orders);
            
            // Get delivery people for assignment
            List<User> deliveryPeople = userService.getUsersByRole(User.ROLE_DELIVERY);
            req.setAttribute("deliveryPeople", deliveryPeople);
            
            req.getRequestDispatcher("/WEB-INF/views/admin/orders.jsp").forward(req, resp);
        } catch (ServiceException e) {
            getServletContext().log("Error getting orders for admin", e);
            req.setAttribute("error", "An error occurred while retrieving orders.");
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        }
    }

    // Update the doPost method to handle order assignment
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Check if logged in and has ADMIN role
        if (!requireLogin(req, resp) || !requireRole(req, resp, User.ROLE_ADMIN)) {
            return;
        }
        
        try {
            int orderId = Integer.parseInt(req.getParameter("orderId"));
            String action = req.getParameter("action");
            
            if ("update-status".equals(action)) {
                String status = req.getParameter("status");
                orderService.updateOrderStatus(orderId, status);
                setFlashMessage(req, "success", "Order status updated successfully.");
            } 
            else if ("assign".equals(action)) {
                int deliveryPersonId = Integer.parseInt(req.getParameter("deliveryPersonId"));
                orderService.assignOrderToDeliveryPerson(orderId, deliveryPersonId);
                setFlashMessage(req, "success", "Order assigned to delivery person successfully.");
            }
            
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
        } catch (ValidationException e) {
            setFlashMessage(req, "error", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
        } catch (ServiceException e) {
            getServletContext().log("Error updating order", e);
            setFlashMessage(req, "error", "An error occurred while updating the order.");
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
        }
    }
}