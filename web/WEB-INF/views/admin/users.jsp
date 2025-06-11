<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Manage Users" />
</jsp:include>

<div class="container mt-4">
    <h1 class="mb-4">Manage Users</h1>
    
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
      <!-- Add New User Button -->
    <div class="mb-4">
        <button class="btn btn-success" data-bs-toggle="modal" data-bs-target="#addUserModal">
            <i class="fas fa-user-plus me-2"></i> Add New User
        </button>
            <i class="fas fa-user-plus me-2"></i>Add New User
        </button>
    </div>
    
    <!-- User List -->
    <c:choose>
        <c:when test="${empty users}">
            <div class="alert alert-info">No users found.</div>
        </c:when>
        <c:otherwise>
            <div class="table-responsive">
                <table class="table table-striped table-hover">
                    <thead class="table-dark">
                        <tr>
                            <th>ID</th>
                            <th>Username</th>
                            <th>Full Name</th>
                            <th>Email</th>
                            <th>Phone</th>
                            <th>Role</th>
                            <th>Joined On</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${users}" var="user">
                            <tr>
                                <td>${user.id}</td>
                                <td>${user.username}</td>
                                <td>${user.fullName}</td>
                                <td>${user.email}</td>
                                <td>${user.phone}</td>
                                <td>
                                    <span class="badge 
                                        ${user.role eq 'ADMIN' ? 'bg-danger' : 
                                         user.role eq 'DELIVERY' ? 'bg-info' :
                                         'bg-success'}">
                                        ${user.role}
                                    </span>
                                </td>
                                <td><fmt:formatDate value="${user.createdAt}" pattern="yyyy-MM-dd" /></td>
                                <td>
                                    <div class="btn-group">
                                        <button class="btn btn-sm btn-primary edit-user" 
                                                data-id="${user.id}" 
                                                data-bs-toggle="modal" 
                                                data-bs-target="#editUserModal${user.id}"
                                                title="Edit User">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        <button class="btn btn-sm btn-danger delete-user" 
                                                data-id="${user.id}"
                                                data-bs-toggle="modal" 
                                                data-bs-target="#deleteUserModal${user.id}"
                                                title="Delete User">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </div>
                                    
                                    <!-- Edit User Modal -->
                                    <div class="modal fade" id="editUserModal${user.id}" tabindex="-1" 
                                         aria-labelledby="editUserModalLabel${user.id}" aria-hidden="true">
                                        <div class="modal-dialog">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <h5 class="modal-title" id="editUserModalLabel${user.id}">Edit User</h5>
                                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                </div>
                                                <form action="${pageContext.request.contextPath}/admin/users" method="post">
                                                    <div class="modal-body">
                                                        <input type="hidden" name="action" value="update">
                                                        <input type="hidden" name="id" value="${user.id}">
                                                        
                                                        <div class="mb-3">
                                                            <label for="fullName${user.id}" class="form-label">Full Name</label>
                                                            <input type="text" class="form-control" id="fullName${user.id}" 
                                                                   name="fullName" value="${user.fullName}" required>
                                                        </div>
                                                        
                                                        <div class="mb-3">
                                                            <label for="email${user.id}" class="form-label">Email</label>
                                                            <input type="email" class="form-control" id="email${user.id}" 
                                                                   name="email" value="${user.email}" required>
                                                        </div>
                                                        
                                                        <div class="mb-3">
                                                            <label for="phone${user.id}" class="form-label">Phone</label>
                                                            <input type="tel" class="form-control" id="phone${user.id}" 
                                                                   name="phone" value="${user.phone}" required>
                                                        </div>
                                                        
                                                        <div class="mb-3">
                                                            <label for="role${user.id}" class="form-label">Role</label>
                                                            <select class="form-control" id="role${user.id}" name="role" required>
                                                                <option value="CUSTOMER" ${user.role eq 'CUSTOMER' ? 'selected' : ''}>Customer</option>
                                                                <option value="DELIVERY" ${user.role eq 'DELIVERY' ? 'selected' : ''}>Delivery Person</option>
                                                                <option value="ADMIN" ${user.role eq 'ADMIN' ? 'selected' : ''}>Admin</option>
                                                            </select>
                                                        </div>
                                                    </div>
                                                    <div class="modal-footer">
                                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                                        <button type="submit" class="btn btn-primary">Save Changes</button>
                                                    </div>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <!-- Delete User Modal -->
                                    <div class="modal fade" id="deleteUserModal${user.id}" tabindex="-1" 
                                         aria-labelledby="deleteUserModalLabel${user.id}" aria-hidden="true">
                                        <div class="modal-dialog">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <h5 class="modal-title" id="deleteUserModalLabel${user.id}">Delete User</h5>
                                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                </div>
                                                <div class="modal-body">
                                                    <p>Are you sure you want to delete user: <strong>${user.username}</strong>?</p>
                                                    <p class="text-danger">This action cannot be undone.</p>
                                                </div>
                                                <div class="modal-footer">
                                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                                    <form action="${pageContext.request.contextPath}/admin/users" method="post">
                                                        <input type="hidden" name="action" value="delete">
                                                        <input type="hidden" name="id" value="${user.id}">
                                                        <button type="submit" class="btn btn-danger">Delete User</button>
                                                    </form>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- Add User Modal -->
<div class="modal fade" id="addUserModal" tabindex="-1" 
     aria-labelledby="addUserModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="addUserModalLabel">Add New User</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/users" method="post">
                <div class="modal-body">
                    <input type="hidden" name="action" value="create">
                    
                    <div class="mb-3">
                        <label for="username" class="form-label">Username</label>
                        <input type="text" class="form-control" id="username" name="username" required>
                    </div>
                    
                    <div class="mb-3">
                        <label for="password" class="form-label">Password</label>
                        <input type="password" class="form-control" id="password" name="password" required>
                    </div>
                    
                    <div class="mb-3">
                        <label for="fullName" class="form-label">Full Name</label>
                        <input type="text" class="form-control" id="fullName" name="fullName" required>
                    </div>
                    
                    <div class="mb-3">
                        <label for="email" class="form-label">Email</label>
                        <input type="email" class="form-control" id="email" name="email" required>
                    </div>
                    
                    <div class="mb-3">
                        <label for="phone" class="form-label">Phone</label>
                        <input type="tel" class="form-control" id="phone" name="phone" required>
                    </div>
                    
                    <div class="mb-3">
                        <label for="role" class="form-label">Role</label>
                        <select class="form-control" id="role" name="role" required>
                            <option value="CUSTOMER">Customer</option>
                            <option value="DELIVERY">Delivery Person</option>
                            <option value="ADMIN">Admin</option>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-success">Add User</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
