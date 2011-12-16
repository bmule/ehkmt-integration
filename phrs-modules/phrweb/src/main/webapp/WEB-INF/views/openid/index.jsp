<%@ page session="false" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/jsp/prefix.jsp" %>
<div class="box clearfix">
  <div class="rightcol">
    <a href="<c:url value="/logout/openid"/>">logout</a>
  </div>
  <div>
    Welcome <span style="color:green">${openid_user.claimedId}</span>
  </div>
  <br/>
  <div>
    <a href="<c:url value="/popup_login.html"/>">popup_login.html</a> <span style="font-size:.8em">Signing in without refreshing or leaving the page</span>
  </div>
</div>
<%@ include file="/WEB-INF/views/jsp/suffix.jsp" %>