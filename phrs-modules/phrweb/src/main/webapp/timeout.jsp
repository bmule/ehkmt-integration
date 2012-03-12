<?xml version="1.0" encoding="UTF-8"?>
<%@page contentType="text/html; charset=UTF-8" import="java.util.Map" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>OpenId returned</title>
	<meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8" />
	<link rel="stylesheet" type="text/css" href="consumer-servlet.css" />
</head>
<body>

		<div>
			<fieldset>
				<legend>Time out</legend>
                <p>
                    Sorry, you have been logged out because too much time has passed.
                </p>
                <p>
                    Please return to the application home and try again.
                    Click here: <a href="<%= request.getContextPath() %>/index.xhtml">Home</a>
                </p>
			</fieldset>
		</div>

</body>
</html>
