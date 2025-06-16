<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Manage Menu" />
</jsp:include>

<div class="container mt-4">
    <h1 class="mb-4">Manage Menu</h1>
    
    <!-- Alert Messages -->
    <c:if test="${not empty sessionScope.success}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle me-2"></i> ${sessionScope.success}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="success" scope="session" />
    </c:if>
    
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-circle me-2"></i> ${sessionScope.error}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="error" scope="session" />
    </c:if>
    
    <!-- Add New Pizza Button -->
    <div class="mb-4">
        <button class="btn btn-success" data-bs-toggle="modal" data-bs-target="#addPizzaModal">
            <i class="fas fa-plus me-2"></i>Add New Pizza
        </button>
    </div>
    
    <!-- Pizza List -->
    <div class="row">
        <c:choose>
            <c:when test="${empty pizzas}">
                <div class="col-12">
                    <div class="alert alert-info">No pizzas found in menu.</div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead class="table-dark">
                            <tr>
                                <th>ID</th>
                                <th>Image</th>
                                <th>Name</th>
                                <th>Price</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${pizzas}" var="pizza">
                                <tr>
                                    <td>${pizza.id}</td>
                                    <td>
                                        <img src="${pizza.imageUrl}" alt="${pizza.name}" style="width: 50px; height: 50px; object-fit: cover;" />
                                    </td>
                                    <td>${pizza.name}</td>
                                    <td>₹${pizza.price}</td>
                                    <td>
                                        <span class="badge ${pizza.available ? 'bg-success' : 'bg-danger'}">
                                            ${pizza.available ? 'Available' : 'Out of Stock'}
                                        </span>
                                    </td>
                                    <td>
                                        <button class="btn btn-sm btn-primary edit-pizza" 
                                                data-id="${pizza.id}" 
                                                data-bs-toggle="modal" 
                                                data-bs-target="#editPizzaModal">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        <button class="btn btn-sm btn-danger delete-pizza" 
                                                data-id="${pizza.id}"
                                                data-bs-toggle="modal" 
                                                data-bs-target="#deletePizzaModal">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- Modal forms for adding/editing/deleting pizzas -->

<!-- Add Pizza Modal -->
<div class="modal fade" id="addPizzaModal" tabindex="-1" aria-labelledby="addPizzaModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="addPizzaModalLabel">Add New Pizza</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/pizzas" method="post" enctype="multipart/form-data">
                <input type="hidden" name="action" value="add">
                <div class="modal-body">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="name" class="form-label">Pizza Name</label>
                            <input type="text" class="form-control" id="name" name="name" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="price" class="form-label">Price (₹)</label>
                            <input type="number" step="0.01" class="form-control" id="price" name="price" required min="0">
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="categoryId" class="form-label">Category</label>
                            <select class="form-select" id="categoryId" name="categoryId" required>
                                <c:forEach items="${categories}" var="category">
                                    <option value="${category.id}">${category.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="available" class="form-label">Availability</label>
                            <select class="form-select" id="available" name="available">
                                <option value="true" selected>Available</option>
                                <option value="false">Out of Stock</option>
                            </select>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="description" class="form-label">Description</label>
                        <textarea class="form-control" id="description" name="description" rows="3" required></textarea>
                    </div>
                    <div class="mb-3">
                        <label for="imageFile" class="form-label">Pizza Image</label>
                        <input type="file" class="form-control" id="imageFile" name="imageFile" accept="image/*">
                        <small class="text-muted">Leave empty to use a default image</small>
                    </div>
                    <div class="mb-3">
                        <label for="imageUrl" class="form-label">Image URL (alternative to uploading)</label>
                        <input type="text" class="form-control" id="imageUrl" name="imageUrl" 
                               placeholder="http://example.com/pizza.jpg">
                        <small class="text-muted">Only fill this if you're not uploading an image file</small>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-success">Add Pizza</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Edit Pizza Modal -->
<div class="modal fade" id="editPizzaModal" tabindex="-1" aria-labelledby="editPizzaModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="editPizzaModalLabel">Edit Pizza</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/pizzas" method="post" enctype="multipart/form-data">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" id="editPizzaId" name="id">
                <div class="modal-body">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="editName" class="form-label">Pizza Name</label>
                            <input type="text" class="form-control" id="editName" name="name" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="editPrice" class="form-label">Price (₹)</label>
                            <input type="number" step="0.01" class="form-control" id="editPrice" name="price" required min="0">
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="editCategoryId" class="form-label">Category</label>
                            <select class="form-select" id="editCategoryId" name="categoryId" required>
                                <c:forEach items="${categories}" var="category">
                                    <option value="${category.id}">${category.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="editAvailable" class="form-label">Availability</label>
                            <select class="form-select" id="editAvailable" name="available">
                                <option value="true">Available</option>
                                <option value="false">Out of Stock</option>
                            </select>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="editDescription" class="form-label">Description</label>
                        <textarea class="form-control" id="editDescription" name="description" rows="3" required></textarea>
                    </div>
                    <div class="mb-3">
                        <label for="editImageFile" class="form-label">Pizza Image</label>
                        <input type="file" class="form-control" id="editImageFile" name="imageFile" accept="image/*">
                        <div class="mt-2">
                            <img id="currentImage" src="" alt="Current pizza image" style="max-height: 100px;" class="img-thumbnail">
                        </div>
                        <small class="text-muted">Leave empty to keep current image</small>
                    </div>
                    <div class="mb-3">
                        <label for="editImageUrl" class="form-label">Image URL</label>
                        <input type="text" class="form-control" id="editImageUrl" name="imageUrl">
                        <small class="text-muted">Only fill this if you're not uploading a new image file</small>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Update Pizza</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Delete Pizza Modal -->
<div class="modal fade" id="deletePizzaModal" tabindex="-1" aria-labelledby="deletePizzaModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="deletePizzaModalLabel">Confirm Delete</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <p>Are you sure you want to delete this pizza? This action cannot be undone.</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <form action="${pageContext.request.contextPath}/admin/pizzas" method="post">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" id="deletePizzaId" name="id">
                    <button type="submit" class="btn btn-danger">Delete Pizza</button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- JavaScript for Modal Data Handling -->
<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Handle edit pizza click
        const editButtons = document.querySelectorAll('.edit-pizza');
        editButtons.forEach(button => {
            button.addEventListener('click', function() {
                const pizzaId = this.getAttribute('data-id');
                // Fetch pizza data via AJAX
                fetch('${pageContext.request.contextPath}/admin/pizzas?action=get&id=' + pizzaId)
                    .then(response => response.json())
                    .then(data => {
                        // Populate the form
                        document.getElementById('editPizzaId').value = data.id;
                        document.getElementById('editName').value = data.name;
                        document.getElementById('editPrice').value = data.price;
                        document.getElementById('editDescription').value = data.description;
                        document.getElementById('editImageUrl').value = data.imageUrl;
                        document.getElementById('editAvailable').value = data.available;
                        
                        if (data.categoryId) {
                            document.getElementById('editCategoryId').value = data.categoryId;
                        }
                        
                        // Show current image
                        const currentImage = document.getElementById('currentImage');
                        if (data.imageUrl) {
                            currentImage.src = data.imageUrl;
                            currentImage.style.display = 'block';
                        } else {
                            currentImage.style.display = 'none';
                        }
                    })
                    .catch(error => {
                        console.error('Error fetching pizza data:', error);
                        alert('Failed to load pizza data. Please try again.');
                    });
            });
        });
        
        // Handle delete pizza click
        const deleteButtons = document.querySelectorAll('.delete-pizza');
        deleteButtons.forEach(button => {
            button.addEventListener('click', function() {
                const pizzaId = this.getAttribute('data-id');
                document.getElementById('deletePizzaId').value = pizzaId;
            });
        });
    });
</script>

<jsp:include page="../common/footer.jsp" />