<%--
  Created by IntelliJ IDEA.
  User: amievil
  Date: 16.10.13
  Time: 23:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <title>Login Page</title>
    <style>
        .errorblock {
            color: #ff0000;
            background-color: #ffEEEE;
            border: 3px solid #ff0000;
            padding: 8px;
            margin: 16px;
        }
    </style>
</head>
<body onload='document.f.username.focus();'>
<h3>Login with Username and Password (Custom Page)</h3>

<c:if test="${not empty error}">
    <div class="errorblock">
        Your login attempt was not successful, try again.<br /> Caused :
            ${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}
    </div>
</c:if>

<form name='f' action="<c:url value='/login' />" method='POST'>
    <input type="hidden"  name="${_csrf.parameterName}"   value="${_csrf.token}"/>
    <table>
        <tr>
            <td>User:</td>
            <td><input type='text' name='username' value='' >
            </td>
        </tr>
        <tr>
            <td>Password:</td>
            <td><input type='password' name='password' />
            </td>
        </tr>
        <tr>
            <td colspan='2'><input name="submit" type="submit" value="submit" />
            </td>
        </tr>
        <tr>
            <td colspan='2'><input name="reset" type="reset" />
            </td>
        </tr>
    </table>

</form>
</body>
</html>