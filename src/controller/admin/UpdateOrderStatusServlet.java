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
 * Servlet for updating order status
 */
@WebServlet("/admin/update-order-status")
public class UpdateOrderStatusServlet extends BaseServlet {
    
    private OrderService orderService;
    
    @Override
    public void init() throws ServletException {
        orderService = new OrderService();
    }
    
    /**
     * Handles POST requests - updates order status
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
            String status = req.getParameter("status");
            
            orderService.updateOrderStatus(orderId, status);
            setFlashMessage(req, "success", "Order status updated successfully.");
            
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
        } catch (ValidationException e) {
            setFlashMessage(req, "error", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
        } catch (ServiceException e) {
            getServletContext().log("Error updating order status", e);
            setFlashMessage(req, "error", "An error occurred while updating the order status.");
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/orders");
        }
    }
}
