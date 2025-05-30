</div><!-- Closing the container div opened in header.jsp -->
    
    <footer class="bg-dark text-white py-4 mt-5">
        <div class="container">
            <div class="row">
                <div class="col-md-4">
                    <h5>PizzaCraft</h5>
                    <p>Fresh, handcrafted pizzas delivered to your door.</p>
                </div>
                <div class="col-md-4">
                    <h5>Quick Links</h5>
                    <ul class="list-unstyled">
                        <li><a href="${pageContext.request.contextPath}/" class="text-white">Home</a></li>
                        <li><a href="${pageContext.request.contextPath}/menu" class="text-white">Menu</a></li>
                        <li><a href="${pageContext.request.contextPath}/cart" class="text-white">Cart</a></li>
                    </ul>
                </div>
                <div class="col-md-4">
                    <h5>Contact</h5>
                    <address>
                        <i class="fas fa-map-marker-alt"></i> Pizza Street, Coimbatore<br>
                        <i class="fas fa-phone"></i> (123) 456-7890<br>
                        <i class="fas fa-envelope"></i> info@pizzacraft.com
                    </address>
                </div>
            </div>
            <div class="text-center mt-3">
                <p>&copy; 2023 PizzaCraft. All rights reserved.</p>
            </div>
        </div>
    </footer>
    
    <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>