package controller.delivery;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.BaseServlet;
import model.User;
import service.OrderService;
import service.ServiceException;
import service.ValidationException;

/**
 * Servlet for handling delivery order status updates
 */
@WebServlet("/delivery/update-order")
public class DeliveryOrderUpdateServlet extends BaseServlet {
    
    private OrderService orderService;
    
    @Override
    public void init() throws ServletException {
        orderService = new OrderService();
    }
    
    /**
     * Handles POST requests - updates order status from delivery person
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Check if logged in and has DELIVERY_PERSON role
        if (!requireLogin(req, resp) || !requireRole(req, resp, User.ROLE_DELIVERY)) {
            return;
        }
        
        try {
            int orderId = Integer.parseInt(req.getParameter("orderId"));
            String action = req.getParameter("action");
            User deliveryPerson = getLoggedInUser(req);
            
            boolean success = false;
            
            if ("pickup".equals(action)) {
                success = orderService.markOrderOutForDelivery(orderId, deliveryPerson.getId());
                if (success) {
                    setFlashMessage(req, "success", "Order marked as out for delivery.");
                }
            } else if ("deliver".equals(action)) {
                success = orderService.markOrderDelivered(orderId, deliveryPerson.getId());
                if (success) {
                    setFlashMessage(req, "success", "Order marked as delivered. Well done!");
                }
            }
            
            if (!success) {
                setFlashMessage(req, "error", "Failed to update order status.");
            }
            
            resp.sendRedirect(req.getContextPath() + "/delivery/dashboard");
        } catch (ValidationException e) {
            setFlashMessage(req, "error", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/delivery/dashboard");
        } catch (ServiceException e) {
            getServletContext().log("Error updating order status", e);
            setFlashMessage(req, "error", "An error occurred while updating the order.");
            resp.sendRedirect(req.getContextPath() + "/delivery/dashboard");
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/delivery/dashboard");
        }
    }
}