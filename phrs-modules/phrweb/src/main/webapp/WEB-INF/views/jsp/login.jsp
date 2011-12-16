<%@ page session="false" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>Login</title>
<style type="text/css">
    #container {
      padding: 1em;      
    }
    
    #container p {
      font-size: 1.2em;
      font-family: Tahoma,Verdana;
    }
    
    #openid_identifier {
      background-image: url(images/login-bg.gif);
      background-position: 3px 2px;
      background-repeat: no-repeat;
      margin: 0;
      padding: 0.2em 0.2em 0.2em 20px;
      vertical-align: middle;
      width: 322px;
    }
</style>
</head>
<body>
<div id="container">

  <div style="clear:both"></div>
  <div style="color:red;font-size:1.4em">&nbsp;${openid_servlet_filter_msg}</div>
  <p>Login with your <b>OpenID</b></p>
  
  <form method="post">
    Provider: <input id="loginWith" name="loginWith" type="text" size="90" value="icardea"/>     
    OpenID: <input id="openid_identifier" name="openid_identifier" type="text" size="90"/>
    <input class="btn" type="submit" value="send"/>
  </form>
  <p>Other Providers
  http://localhost:8080/provider  
  </p>
  <!-- https://www.google.com/accounts/o8/id  -->

  <!--<p><a href="/home/">Home</a></p>-->
   <p><a href="/home.jsp">home.jsp</a> <span style="font-size:.8em">test,not filtered</span></p>
  <p><a href="/index.xhtml">index.xhtml</a> <span style="font-size:.8em">not filtered</span></p>
    <p><a href="/jsf/home.xhtml">/'protected'/home.xhtml</a> <span style="font-size:.8em">filter activates, expect openID login page</span></p>
</div>

</body>
</html>