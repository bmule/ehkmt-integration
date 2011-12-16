<%@ page session="false" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/jsp/prefix.jsp" %>
<div>
  <ul>
    <li><a href="<c:url value="/oauth"/>">OAuth</a></li>
    <li><a href="<c:url value="/openid"/>">OpenID</a> <a href="<c:url value="/popup_login.html"/>">via popup</a></li>
    <li><a href="<c:url value="/hybrid"/>">Hybrid (OpenID+OAuth)</a> <a href="<c:url value="/popup_login_hybrid.html"/>">via popup</a></li>
  </ul>
</div>
<%@ include file="/WEB-INF/views/jsp/suffix.jsp" %>