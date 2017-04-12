<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<dsp:page>
	<dsp:importbean bean="/com/bbb/integration/csr/CSRFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:getvalueof var="listOfSites" bean="CSRFormHandler.listOfSites" />
	<html>
	<head>
	<title>Category Rel Ranking - Admin Page</title>
	<link rel="stylesheet" href="/qas/qas.css" />
	</head>
	<body>
		<h2>Category Rel Ranking - Admin Page</h2>
		<dsp:droplet name="ErrorMessageForEach">
			<dsp:param param="formhandler.formExceptions" name="exceptions" />
			<dsp:oparam name="outputStart">
			</dsp:oparam>
			<dsp:oparam name="output">
			<ul class="error">
				<li class="error"><dsp:getvalueof param="message" var="err_msg_key" /> ${err_msg_key}
				</li>
			<dsp:oparam name="outputEnd">
			</dsp:oparam>
			</ul>
			</dsp:oparam>
		</dsp:droplet>
		<dsp:form id="cacheInfo" iclass="clearfix"
			action="displayChildCategories.jsp" method="post">
			<table border="noborder" style="width: 600px;">
				<tr>
					<td><strong>Admin Name</strong><span class="required">*</span>
					</td>
					<td><dsp:input type="text" bean="CSRFormHandler.adminName" maxlength="40" name="adminName" id="adminName" /></td>
				</tr>
				<tr>
					<td><strong>Admin Password</strong><span class="required">*</span>
					</td>
					<td><dsp:input type="password" bean="CSRFormHandler.adminPassword" maxlength="40" name="adminPassword" id="adminPassword" /></td>
				</tr>
				<tr>
					<td><strong>Site</strong><span class="required">*</span>
					</td>
					<td>
					<dsp:select bean="CSRFormHandler.currentSite">
		            <c:forEach items="${listOfSites}" var="entry">
		            <dsp:option value="${entry.key}">${entry.value}</dsp:option>
		            </c:forEach>
		            </dsp:select>
	        		</td>
				</tr>
				<tr>
					<td colspan="2"><dsp:input bean="CSRFormHandler.validateUser" id="cacheInfoBtn" type="Submit" value="Submit" />
					</td>
				</tr>
			</table>
		</dsp:form>
		</body>
	</html>
</dsp:page>
