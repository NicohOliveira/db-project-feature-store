<%-- 
    Document   : create
    Created on : Oct 18, 2022, 11:45:32 AM
    Author     : dskaster
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%@include file="/view/include/head.jsp"  %>        
        <title>[User App] Usuários: cadastro</title>
    </head>
    <body>
       <div class="container">
            <h2 class="text-center">Inserção de um novo usuário</h2>

            <form action="${pageContext.request.contextPath}/usuario" method="post">
                <input type="hidden" name="action" value="create"/>

                <label>Username:</label>
                <input type="text" name="username" required maxlength="20"/>

                <label>Senha:</label>
                <input type="password" name="senha" required/>

                <button type="submit">Cadastrar</button>
            </form>
        </div>

        <%@include file="/view/include/scripts.jsp"%>
        <script src="${pageContext.servletContext.contextPath}/assets/js/user.js"></script>

    </body>
</html>