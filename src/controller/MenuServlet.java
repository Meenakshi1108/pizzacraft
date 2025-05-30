package controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Category;
import model.Pizza;
import service.CategoryService;
import service.PizzaService;
import service.ServiceException;

/**
 * Servlet for displaying the menu
 */
@WebServlet("/menu")
public class MenuServlet extends BaseServlet {
    
    private CategoryService categoryService;
    private PizzaService pizzaService;
    
    @Override
    public void init() throws ServletException {
        categoryService = new CategoryService();
        pizzaService = new PizzaService();
    }
    
    /**
     * Handles GET requests - displays the menu
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        try {
            // Get categories for the sidebar
            List<Category> categories = categoryService.getAllCategories();
            req.setAttribute("categories", categories);
            
            // Check if a specific category is requested
            String categoryIdParam = req.getParameter("category");
            List<Pizza> pizzas;
            
            if (categoryIdParam != null && !categoryIdParam.isEmpty()) {
                // If category is specified, get pizzas for that category
                int categoryId = Integer.parseInt(categoryIdParam);
                pizzas = pizzaService.getPizzasByCategory(categoryId);
                
                // Get the category name for the heading
                Category category = categoryService.getCategoryById(categoryId);
                req.setAttribute("selectedCategory", category);
            } else if ("vegetarian".equals(req.getParameter("filter"))) {
                // If vegetarian filter is applied
                pizzas = pizzaService.getVegetarianPizzas();
                req.setAttribute("filterTitle", "Vegetarian Pizzas");
            } else {
                // Otherwise get all pizzas
                pizzas = pizzaService.getAllPizzas();
            }
            
            req.setAttribute("pizzas", pizzas);
            req.getRequestDispatcher("/WEB-INF/views/menu/index.jsp").forward(req, resp);
        } catch (ServiceException e) {
            // Log the error and show an error page
            getServletContext().log("Error displaying menu", e);
            req.setAttribute("error", "An error occurred while loading the menu.");
            req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            // Invalid category ID parameter
            resp.sendRedirect(req.getContextPath() + "/menu");
        }
    }
}