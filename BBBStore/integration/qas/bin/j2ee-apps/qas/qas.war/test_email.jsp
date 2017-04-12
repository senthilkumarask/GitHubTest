<%@ taglib uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" prefix="dsp" %>
<%@ taglib uri="http://www.atg.com/taglibs/json" prefix="json" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
	<dsp:importbean var="TestEmailFormHandler" bean="/com/bbb/email/formhandler/TestEmailFormHandler" />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
	<title>Test Email Form</title>	
	</head>
	<body>
		<h1>Test Email Form</h1>
		<dsp:droplet name="Switch">
			<dsp:param name="value" bean="TestEmailFormHandler.successMessage"/>
			<dsp:oparam name="true">
				<ul class="error">
					<li class="error">
						<span style="color: #FF0000">
							Email Sent.
						</span>
					</li>
				</ul>
			</dsp:oparam>
		</dsp:droplet>
			<p>
				  <dsp:droplet name="ErrorMessageForEach">
					<dsp:param param="TestEmailFormHandler.formExceptions" name="exceptions"/>
				    <dsp:oparam name="outputStart">
				      <ul class="error">
				    </dsp:oparam>
				    <dsp:oparam name="output">
				      <li class="error">
				      	<span style="color: #FF0000">
					        <dsp:getvalueof param="message" var="err_msg_key" />
							<c:set var="under_score" value="_"/>
							<c:choose>
							<c:when test="${fn:contains(err_msg_key, under_score)}">
					        	<c:set var="err_msg" scope="page">
					        		<bbbe:error key="${err_msg_key}" language="${pageContext.request.locale.language}"/>
								</c:set>
					        </c:when>
					        </c:choose>
							<c:choose>
							<c:when test="${empty err_msg}"><dsp:valueof param="message" valueishtml="true" /></c:when>
							<c:otherwise>${err_msg}</c:otherwise>
							</c:choose>  
							<c:set var="err_msg" scope="page"></c:set>
						 </span>
				      </li>
				    </dsp:oparam>
				    <dsp:oparam name="outputEnd">
				      </ul>
				    </dsp:oparam>
				  </dsp:droplet>
			</p>
			<dsp:form iclass="form" action="test_email.jsp" id="testEmailForm" method="post">
				<dsp:input bean="TestEmailFormHandler.sucessUrl" type="hidden" value="test_email.jsp" />
				<dsp:input bean="TestEmailFormHandler.errorUrl" type="hidden" value="test_email.jsp" />
				<table>
					<tr>
						<td>To:</td>
						<td>
							<dsp:input bean="TestEmailFormHandler.recipientEmail" name="recipientEmail" size="65" id="recipientEmail" type="text" />
						</td>
					</tr>
					<tr>
						<td>From:</td>
						<td>
							<dsp:input bean="TestEmailFormHandler.senderEmail" name="senderEmail" size="65" id="senderEmail" type="text" value="bbbadmin@bedbath.com"/>
						</td>
					</tr>
					<tr>
						<td>Subject:</td>
						<td>
							<dsp:input bean="TestEmailFormHandler.subject" name="subject" size="65" id="subject" type="text" value="Test email from ${pageContext.request.serverName} server"/>
						</td>
					</tr>
					<tr>
						<td>Body:</td>
						<td>
							<dsp:textarea bean="TestEmailFormHandler.message" default="This is a test email, Please ignore."/>
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td>
							<dsp:input id="btnSubmitEmailRequest" type="submit" value="Send" bean="TestEmailFormHandler.sendEmail" />
						</td>
					</tr>
				</table>
		</dsp:form>
	</body>
</html>
</dsp:page>