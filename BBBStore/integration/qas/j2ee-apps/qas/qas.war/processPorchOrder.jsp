<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/com/bbb/commerce/porch/formhandler/PorchOrderProcessFormHandler"/>
	
	<html>
	<head>
	<title>Process Porch Orders Here !!</title>
	<link rel="stylesheet" href="/qas/qas.css" />
	</head>
	<body>
		<c:set var="userAuthenticated">
	<%=atg.servlet.ServletUtil.getCurrentRequest().getSession().getAttribute("UserAuthenticated")%>
</c:set>
    <c:set var="userAuthenticated" scope="request"><c:out value="${param.userAuthenticated}"/></c:set> 
 <c:if test = "${!userAuthenticated}"> 
	      <dsp:droplet name="/com/bbb/commerce/browse/droplet/RedirectDroplet"> 
 	      	<dsp:param name="url" value="/porchOrdersLogin.jsp" />
	      </dsp:droplet> 
 	</c:if> 
	<dsp:getvalueof var="projectids" bean="PorchOrderProcessFormHandler.porchProjectIdList"/>
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
			action="displayPorchSucess.jsp" method="post">
					<strong>Enter Porch Internal Orders</strong><span class="required">*</span>
					<dsp:input type="text" bean="PorchOrderProcessFormHandler.orderIds" name="orders" id="orders" />
				<dsp:input bean="PorchOrderProcessFormHandler.validateOrderId" id="cacheInfoBtn" type="Submit" value="Submit" />
		</dsp:form>
		</body>
	</html>
</dsp:page>