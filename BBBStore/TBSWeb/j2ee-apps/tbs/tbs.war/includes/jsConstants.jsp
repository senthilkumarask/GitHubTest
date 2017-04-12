<%@ page contentType="application/javascript" %>

<dsp:page>

<%--
- File Name: jsConstants.jsp
- Author(s):
- Copyright Notice:
- Description: Sets parameters in a javascript object for use in plugin scripts.
-    PLEASE NOTE: a lot of variables are set as jstl vars then output on page using c:out. We are
-    taking advantage of the escapeXML attribute (true by default) to escape values in these strings.
- Parameters:
-
--%>

	<dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>

	var CONSTANTS = {
<%-- 		contextRoot : '<c:out value="${contextRoot}"/>' --%>
			contextPath : '${contextPath}'
	},
	BBB = BBB || {};
	BBB.loginStatus = {};

</dsp:page>
