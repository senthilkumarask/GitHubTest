<%@ page language="java" import="javax.naming.InitialContext,javax.sql.*,javax.naming.NameNotFoundException"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="dspTaglib" prefix="dsp"%>

<%
	request.setAttribute("theme", request.getParameter("theme") != null ? request.getParameter("theme") : getCookie(request.getCookies(), "theme", "redmond").getValue());
%>

<dsp:page>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
<dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>
<c:choose>
  <c:when test="${not isTransient}">
      <dsp:getvalueof var="user" bean="/atg/userprofiling/Profile.firstName"/>
      <dsp:droplet name="/atg/dynamo/droplet/Redirect">   <dsp:param name="url" value="${contextPath}/index.jsp"/> </dsp:droplet>
  </c:when>
</c:choose>
<html lang="us">
<head>
	<meta charset="utf-8">
	<meta http-equiv="Cache-Control" content="no-store,no-cache,must-revalidate"> 
	<meta http-equiv="Pragma" content="no-cache"> 
	<meta http-equiv="Expires" content="-1">
	<meta http-equiv="X-UA-Compatible" content="IE=EDGE" />
	<title>Beyond&#174; Catalog Configuration Wizard</title>
	<link id="theme" href="jquery-ui/themes/<c:out value="${theme}"></c:out>/jquery-ui.css" rel="stylesheet">
	<link rel="shortcut icon" type="image/x-icon" href="images/favicon.ico" />
<style>
body, .ui-widget, .ui-widget select {
	font-family: "Verdana", "Helvetica", "Arial",  "sans-serif", "Trebuchet MS";
        font-size: 8pt;
}
.ui-widget-header {
	padding:3px 3px;
	font-weight:bold;
}
</style>
<script src="jquery-ui/external/jquery/jquery.js"></script>
<script src="jquery-ui/jquery-ui.js"></script>
<script src="scripts/common.js"></script>
</head>
<body>
<div id="header" style="height:64px;">
	<div style="float:left">
		<a href="."><img id="logo" border="0" src="images/logo-redmond.png" style="height:60px;padding-top:0px"></img></a>
	</div>
	<div style="float:right">

	<table style="margin-top:0px">
	<tr>

	<c:if test="${not isTransient}">
	<td>
		<dsp:form action="login.jsp" method="POST" style="display:inline">
		<dsp:input bean="/atg/userprofiling/InternalProfileFormHandler.logout" value="Logout" id="logout" type="hidden"></dsp:input>
		<button class="ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only"  role="button" aria-disabled="false" title="Logout <c:out value="${user}"/>" onclick="this.form.submit();return false;">
			<span class="ui-button-icon-primary ui-icon ui-icon-person"></span>
			<span class="ui-button-text">Logout</span>
		</button>
		</dsp:form>
	</td>
	</c:if>
	<td>
		<div style="padding:5px 5px;" class="ui-widget ui-widget-content ui-corner-all">
			<div class="ui-widget-header">Theme</div>
			<select id="theme" name="theme" onchange="return saveTheme(changeTheme(getSelectedOption(this)), true);">
			<c:set var="index" scope="request" value="${0}"/>
			<c:forTokens items="redmond,cupertino,ui-lightness,dot-luv,dark-hive,black-tie,blitzer,smoothness,flick,vader,eggplant,start,pepper-grinder,hot-sneaks,excite-bike,sunny,le-frog,overcast,humanity,south-street,mint-choc,trontastic,swanky-purse" delims="," var="item">
				<option  value="<c:out value="${index}"/>" <c:out value="${item == theme?'SELECTED':''}"/>><c:out value="${item}"/></option>
				<c:set var="index" scope="request" value="${index+1}"/>
			</c:forTokens>
			</select>
		</div>
	</td>
	</tr>
	</table>

	</div>
</div>

<div class="ui-widget ui-widget-content ui-corner-all" style="margin-top:50px;margin-left:auto;margin-right:auto;width:300px;padding:5px 5px">
	<dsp:form action="login.jsp" method="POST">
		<!--<input type=image src="data:image/gif;base64,R0lGODlhAQABAIAAAP///wAAACwAAAAAAQABAAACAkQBADs="></image>-->
		<div class="ui-widget-header">Please Login</div>
		<div style="padding:20px 25px 5px 25px">
			<div style="margin-top:2px;">

			<c:choose>
    			<c:when test="${param.DPSLogout ne null}">
				<div id="message" style="font-size:xx-small;color:red;padding:3px 3px"><span class="ui-icon ui-icon-alert" style="float:left;"></span>&nbsp;Logged Out.</div>
    			</c:when>
    			<c:when test="${param.DPSTimeout ne null}">
				<div id="message" style="font-size:xx-small;color:red;padding:3px 3px"><span class="ui-icon ui-icon-alert" style="float:left;"></span>&nbsp;Your session expired due to inactivity.</div>
    			</c:when>
			</c:choose>

			</div>
			<div style="margin-top:10px">
				<table style="font:inherit">
				<tr>
				<td>
    					<span>User</span>
				</td>
				</tr>
				<tr>
				<td>
					<dsp:input bean="/atg/userprofiling/InternalProfileFormHandler.value.login" type="text" style="width:245px;font:inherit" id="loginName"></dsp:input>
					<script>$("#loginName").attr({"placeholder":"enter user name"});</script>
				</td>
				<td>
					<span style="position:relative;left:-22px;z-index:1000" class="ui-icon ui-icon-person"></span>
				</td>
				</tr>
				</table>
			</div>
			<div style="margin-top:15px">
				<table style="font:inherit">
				<tr>
				<td>
    					<span>Password</span>
				</td>
				</tr>
				<tr>
				<td>
					<dsp:input bean="/atg/userprofiling/InternalProfileFormHandler.value.password"  type="text" style="width:245px;font:inherit" id="loginPassword"></dsp:input>
					<script>$("#loginPassword").attr({"type":"password","placeholder":"enter password"});</script>
				</td>
				<td>
					<span  style="position:relative;left:-22px;z-index:1000" class="ui-icon ui-icon-locked"></span>
					<dsp:input bean="/atg/userprofiling/InternalProfileFormHandler.loginSuccessURL" value="index.jsp" type="hidden"></dsp:input>
					<dsp:input bean="/atg/userprofiling/InternalProfileFormHandler.loginErrorURL" value="login.jsp" type="hidden"></dsp:input>
				</td>
				</tr>
				</table>
			</div>
			<div style="text-align:right;margin-top:10px">
				<a href="#"><span style="font-size:xx-small;">Forgot Password?</span></a>
			</div>
			<div style="text-align:center;margin-top:20px">
				<dsp:input id="login" style="font:inherit" bean="/atg/userprofiling/InternalProfileFormHandler.login" value="Login"  type="submit"></dsp:input>
				<script>$("#login").attr({"class":"ui-state-default"});</script>
			</div>
			<ul id="messages" style="font-size:xx-small;color:red;margin-top:5px;margin-left:15px;padding:2px 2px">
			<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
                            <dsp:param bean="/atg/userprofiling/InternalProfileFormHandler.formExceptions" name="exceptions"/>
              		    <dsp:oparam name="output">
				<li><dsp:valueof param="message"/></li>
			    </dsp:oparam>
                        </dsp:droplet>
			</ul>
		</div>
	</dsp:form>
</div>
</body>
</html>
<%!
	public Cookie getCookie(Cookie[] cookies, String name, String value) {
		if (cookies != null) {
			for(int i = 0; i < cookies.length; i++) { 
		    		if (cookies[i].getName().equals(name)) {
		        		return cookies[i];
		    		}
			}
		}
		return new Cookie(name, value);
	}
%>
</dsp:page>
