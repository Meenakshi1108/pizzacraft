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

@WebServlet("/view-order/*")
public class OrderDetailsServlet extends BaseServlet {
    
    private OrderService orderService;
    
    @Override
    public void init() throws ServletException {
        orderService = new OrderService();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Ensure user is logged in
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            // Store intended destination
            req.getSession().setAttribute("redirectUrl", req.getRequestURI());
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        try {            // Extract order ID from path
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.sendRedirect(req.getContextPath() + "/");
                return;
            }
            
            int orderId = Integer.parseInt(pathInfo.substring(1));
            Order order = orderService.getOrderById(orderId);
            
            if (order == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
                return;
            }
            
            // Authorization check
            boolean isAuthorized = 
                user.getRole().equals(User.ROLE_ADMIN) || 
                order.getUserId() == user.getId() || 
                (order.getDeliveryPersonId() != null && order.getDeliveryPersonId() == user.getId());
                
            if (!isAuthorized) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Not authorized to view this order");
                return;
            }
            
            req.setAttribute("order", order);
            req.getRequestDispatcher("/WEB-INF/views/orders/detail.jsp").forward(req, resp);
            
        } catch (Exception e) {
            req.setAttribute("error", "Error retrieving order: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        }
    }
}