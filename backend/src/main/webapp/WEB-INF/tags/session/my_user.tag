<%-- 
    Document   : my_user
    Created on : Oct 25, 2022, 11:51:59 AM
    Author     : dskaster
--%>

<%@tag description="User authentication handler" pageEncoding="UTF-8"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@attribute name="context" required="true"%>

<c:if test="${empty sessionScope.usuario}">
    <c:redirect url="/" />
</c:if>