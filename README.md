# PizzaCraft - Indian Flavored Pizza Delivery System

## 🍕 Project Overview

PizzaCraft is a web-based pizza ordering and delivery system featuring authentic Indian flavors. The system allows customers to order handcrafted pizzas with traditional Indian spices and toppings, track their orders, and get them delivered right to their doorstep.

## ✨ Key Features

- **Authentic Indian Menu**: Tandoori Paneer, Butter Chicken, and other Indian-inspired pizzas
- **Vegetarian & Non-Vegetarian Options**: Clearly marked with badges for easy identification
- **Spice Level Indicators**: Choose your preferred spice level for each pizza
- **User Registration & Login**: Create an account to track your orders and get personalized offers
- **Order Tracking**: Real-time updates on your order status
- **Express Delivery**: Fast delivery within 30 minutes to locations in Coimbatore
- **Mobile-Friendly Design**: Order pizzas on the go from any device

## 🛠️ Technologies Used

- **Backend**: Java, JSP, Servlets
- **Frontend**: HTML, CSS, JavaScript, Bootstrap
- **Database**: MySQL
- **Build Tool**: Maven
- **Server**: Apache Tomcat 9.0

## 🚀 Getting Started

### Prerequisites
- JDK 8 or higher
- MySQL 5.7 or higher
- Apache Tomcat 8.5 or higher
- Maven (for dependency management)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Meenakshi1108/pizzacraft
   ```

2. **Set up the database**
   ```bash
   mysql -u root -p < database/create_database.sql
   mysql -u root -p pizza_delivery < database/update_schema.sql
   ```

3. **Configure database connection**
   - Navigate to `WEB-INF/classes/`
   - Update `db.properties` with your MySQL credentials:
     ```properties
     db.url=jdbc:mysql://localhost:3306/pizza_delivery
     db.username=your_username
     db.password=your_password
     db.driver=com.mysql.cj.jdbc.Driver
     ```

4. **Deploy to Tomcat**
   - Build the WAR file: `mvn package`
   - Deploy the WAR file to Tomcat's webapps directory
   - Or configure your IDE to deploy directly to Tomcat

5. **Access the application**
   - Open your browser and go to: http://localhost:8080/PizzaDeliverySystem/

## 📷 Screenshots

![Home Page](screenshots/home.png)
![Menu Page](screenshots/menu.png)
![Order Tracking](screenshots/order-tracking.png)

## ⚙️ Project Structure

```
PizzaDeliverySystem/
├── database/                  # Database scripts
├── src/                       # Java source code
├── web/                       # Web resources
│   ├── WEB-INF/
│   │   ├── views/             # JSP views
│   │   │   ├── common/        # Common elements (header, footer)
│   │   │   ├── menu/          # Menu-related pages
│   │   │   └── ...
│   │   ├── classes/           # Compiled classes
│   │   └── lib/               # Library JARs
│   ├── css/                   # CSS files
│   ├── js/                    # JavaScript files
│   ├── images/                # Image files
│   └── index.jsp              # Home page
└── README.md                  # This file
```
