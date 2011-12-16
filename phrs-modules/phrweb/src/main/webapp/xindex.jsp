<?xml version="1.0" encoding="UTF-8"?>
<%@ page contentType="text/html; charset=UTF-8" import="java.util.Map,java.util.LinkedHashMap" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>OpenID Test</title>
	<meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8" />
	<link rel="stylesheet" type="text/css" href="consumer-servlet.css" />
	<script type="text/javascript">
	<!--
	function changeAll(v) {
		var inputs = document.getElementsByTagName("input");
		for (var i = 0; i < inputs.length; i++) {
			if (inputs[i].value == v) {
				inputs[i].checked = true;
			}
		}
	}
	//-->
	</script>
</head>
<body>
	<div>
		<fieldset>
			<legend>Test 1:</legend>
			<form action="consumer" method="post">
				<div>
					<input type="text" name="openid_identifier" />
				</div>
				<div>
					<button type="submit" name="login">Login</button>
				</div>
			</form>
		</fieldset>

		<fieldset>
			<legend>Test 2: Simple Registration extension(doc: <a href="http://code.google.com/p/openid4java/wiki/SRegHowTo">SRegHowTo</a>)</legend>
			<form action="consumer" method="post">
				<div id="sreg">
					<input type="text" name="openid_identifier" />

					<table>
						<tr>
							<th>All:</th>
							<td>
								<input type="radio" name="all" value="" id="all" onclick="changeAll('')" /><label for="all">None</label>
								<input type="radio" name="all" value="0" id="all0" onclick="changeAll('0')" /><label for="all0">Optional</label>
								<input type="radio" name="all" value="1" id="all1" onclick="changeAll('1')" /><label for="all1">Required</label>
							</td>
						</tr>
						<%
						Map<String, String> sRegAttrs = new LinkedHashMap<String, String>();
						sRegAttrs.put("nickname", "Nickname");
						sRegAttrs.put("email", "Email");
						sRegAttrs.put("fullname", "Fullname");
						sRegAttrs.put("dob", "Date of birth");
						sRegAttrs.put("gender", "Gender");
						sRegAttrs.put("postcode", "Postcode");
						sRegAttrs.put("country", "Country");
						sRegAttrs.put("language", "Language");
						sRegAttrs.put("timezone", "Timezone");
						%>
						<c:forEach items="<%=sRegAttrs %>" var="attribute">
						<tr>
							<th>${attribute.value}:</th>
							<td>
								<input type="radio" name="${attribute.key}" value="" id="${attribute.key}" checked="checked" /><label for="${attribute.key}">None</label>
								<input type="radio" name="${attribute.key}" value="0" id="${attribute.key}0" /><label for="${attribute.key}0">Optional</label>
								<input type="radio" name="${attribute.key}" value="1" id="${attribute.key}1" /><label for="${attribute.key}1">Required</label>
							</td>
						</tr>
						</c:forEach>
					</table>

					<button type="submit" name="login">Login</button>
				</div>
			</form>
		</fieldset>

		<fieldset>
			<legend>Test 3:  Attribute Exchange extension(doc: <a href="http://code.google.com/p/openid4java/wiki/AttributeExchangeHowTo">AttributeExchangeHowTo</a>)</legend>
			<form action="consumer" method="post">
				<div id="ax">
					<input type="text" name="openid_identifier" value="https://www.google.com/accounts/o8/id" />

					<table>
						<thead>
							<tr>
								<th>Alias</th>
								<th>TypeUri</th>
								<th>Required</th>
								<th>Count</th>
							</tr>
						</thead>
						<tbody>
							<%
							Map<String, String> attr1 = new LinkedHashMap<String, String>();
							attr1.put("country", "http://axschema.org/contact/country/home");
							attr1.put("email", "http://axschema.org/contact/email");
							attr1.put("firstname", "http://axschema.org/namePerson/first");
							attr1.put("lastname", "http://axschema.org/namePerson/last");
							attr1.put("language", "http://axschema.org/pref/language");
							%>
							<c:forEach items="<%=attr1%>" var="theAttr" varStatus="status">
							<tr>
								<th><input type="text" name="alias" value="${theAttr.key}" /></th>
								<td><input type="text" name="typeUri" value="${theAttr.value}" /></td>
								<td><input type="checkbox" name="required${status.index}" id="required${status.index}" checked="checked" /><label for="required${status.index}">Required</label></td>
								<td><input type="text" name="count" value="1" /></td>
							</tr>
							</c:forEach>
						</tbody>
						<!-- TODO
						<tfoot>
							<tr>
								<td colspan="4"><button type="button">Add row</button></td>
							</tr>
						</tfoot>
						-->
					</table>
					<button type="submit" name="login">Login</button>
				</div>
			</form>
		</fieldset>

		<fieldset>
			<legend>Test 4: local provider 1</legend>
			<form action="consumer" method="post">
				<div id="ax">
					<input type="text" name="openid_identifier" value="https://www.google.com/accounts/o8/id" />

					<table>
						<thead>
							<tr>
								<th>Alias</th>
								<th>TypeUri</th>
								<th>Required</th>
								<th>Count</th>
							</tr>
						</thead>
						<tbody>
							<%
							Map<String, String> attr2 = new LinkedHashMap<String, String>();
							attr2.put("country", "http://axschema.org/contact/country/home");
							attr2.put("email", "http://axschema.org/contact/email");
							attr2.put("firstname", "http://axschema.org/namePerson/first");
							attr2.put("lastname", "http://axschema.org/namePerson/last");
							attr2.put("language", "http://axschema.org/pref/language");
							%>
							<c:forEach items="<%=attr2%>" var="theAttr" varStatus="status">
							<tr>
								<th><input type="text" name="alias" value="${theAttr.key}" /></th>
								<td><input type="text" name="typeUri" value="${theAttr.value}" /></td>
								<td><input type="checkbox" name="required${status.index}" id="required${status.index}" checked="checked" /><label for="required${status.index}">Required</label></td>
								<td><input type="text" name="count" value="1" /></td>
							</tr>
							</c:forEach>
						</tbody>
	
					</table>
					<button type="submit" name="login">Login</button>
				</div>
			</form>
		</fieldset>

		<fieldset>
			<legend>Test 5: local provider 2</legend>
			<form action="consumer" method="post">
				<div id="ax">
					<input type="text" name="openid_identifier" value="https://www.google.com/accounts/o8/id" />

					<table>
						<thead>
							<tr>
								<th>Alias</th>
								<th>TypeUri</th>
								<th>Required</th>
								<th>Count</th>
							</tr>
						</thead>
						<tbody>
							<%
							Map<String, String> attr3 = new LinkedHashMap<String, String>();
							attr3.put("country", "http://axschema.org/contact/country/home");
							attr3.put("email", "http://axschema.org/contact/email");
							attr3.put("firstname", "http://axschema.org/namePerson/first");
							attr3.put("lastname", "http://axschema.org/namePerson/last");
							attr3.put("language", "http://axschema.org/pref/language");
							%>
							<c:forEach items="<%=attr3%>" var="theAttr" varStatus="status">
							<tr>
								<th><input type="text" name="alias" value="${theAttr.key}" /></th>
								<td><input type="text" name="typeUri" value="${theAttr.value}" /></td>
								<td><input type="checkbox" name="required${status.index}" id="required${status.index}" checked="checked" /><label for="required${status.index}">Required</label></td>
								<td><input type="text" name="count" value="1" /></td>
							</tr>
							</c:forEach>
						</tbody>
					</table>
					<button type="submit" name="login">Login</button>
				</div>
			</form>
		</fieldset>
	</div>
</body>
</html>
