<%@ page session="false" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/jsp/prefix.jsp" %>
<div class="box padded">
  <div style="color:red;font-size:1.4em">&nbsp;${openid_servlet_filter_msg}</div>
  <p>Login with your <span style="color:orange">openid</span></p>
  <form method="POST">
    <input id="openid_identifier" name="openid_identifier" type="text" size=60/>
    <input class="btn" type="submit" value="send"/>
  </form>
</div>
<!-- BEGIN ID SELECTOR -->
<script type="text/javascript" id="__openidselector" src="https://www.idselector.com/selector/e0ed3a269b77fa785de90aeaa20fa0f985746767" charset="utf-8"></script>
<!-- END ID SELECTOR -->
<%@ include file="/WEB-INF/views/jsp/suffix.jsp" %>