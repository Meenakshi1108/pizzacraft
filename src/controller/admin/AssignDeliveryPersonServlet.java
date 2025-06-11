package controller.admin;

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
 * Servlet for assigning delivery persons to orders
 */
@WebServlet("/admin/assign-delivery")
public class AssignDeliveryPersonServlet extends BaseServlet {
    
    private OrderService orderService;
    
    @Override
    public void init() throws ServletException {
        orderService = new OrderService();
    }
    
    /**
     * Handles POST requests - assigns a delivery person to an order
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Check if logged in and has ADMIN role
        if (!requireLogin(req, resp) || !requireRole(req, resp, User.ROLE_ADMIN)) {
            return;
        }
        
        try {
            int orderId = Integer.parseInt(req.getParameter("orderId"));
            int deliveryPersonId = Integer.parseInt(req.getParameter("deliveryPersonId"));
            
            orderService.assignOrderToDeliveryPerson(orderId, deliveryPersonId);
            setFlashMessage(req, "success", "Order assigned to delivery person successfully.");
            
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
        } catch (ValidationException e) {
            setFlashMessage(req, "error", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
        } catch (ServiceException e) {
            getServletContext().log("Error assigning delivery person", e);
            setFlashMessage(req, "error", "An error occurred while assigning the delivery person.");
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
        }
    }
}
