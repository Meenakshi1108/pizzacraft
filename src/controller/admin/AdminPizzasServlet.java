package controller.admin;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.BaseServlet;
import model.Pizza;
import model.User;
import service.PizzaService;
import service.ServiceException;

@WebServlet("/admin/pizzas")
public class AdminPizzasServlet extends BaseServlet {
    
    private PizzaService pizzaService;
    
    @Override
    public void init() throws ServletException {
        pizzaService = new PizzaService();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Check if logged in and has ADMIN role
        if (!requireLogin(req, resp) || !requireRole(req, resp, User.ROLE_ADMIN)) {
            return;
        }
        
        try {
            List<Pizza> pizzas = pizzaService.getAllPizzas();
            req.setAttribute("pizzas", pizzas);
            req.getRequestDispatcher("/WEB-INF/views/admin/pizzas.jsp").forward(req, resp);
        } catch (ServiceException e) {
            getServletContext().log("Error getting pizzas for admin", e);
            req.setAttribute("error", "An error occurred while retrieving pizzas.");
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        }
    }
    
    // You'll need to implement doPost() for creating/updating/deleting pizzas
}