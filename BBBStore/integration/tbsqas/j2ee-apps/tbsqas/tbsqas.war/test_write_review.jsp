<%@ taglib uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" prefix="dsp" %>
<%@ taglib uri="http://www.atg.com/taglibs/json" prefix="json" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
	<dsp:importbean var="TestWriteReviewFormHandler" bean="/com/bbb/bazaarvoice/formhandler/TestWriteReviewFormHandler" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
	<title>Test Write Review Form</title>	
	<script type="text/javascript">
	function changeFormFields() {
		if (document.getElementById('customerType').value=="S")
		{
			document.getElementById("eventTypeRow").style.display="none";
		}
		else {
			document.getElementById("eventTypeRow").style.display="";
		}
	}
</script>
	
	</head>
	<body>
		<h1>Test Write Review Form</h1>
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
		
			<dsp:form iclass="form" action="/store/pie_redirect.jsp" id="testWriteReviewForm" method="post">
				<dsp:input bean="TestWriteReviewFormHandler.sucessUrl" type="hidden" value="/store/pie_redirect.jsp" />
				<dsp:input bean="TestWriteReviewFormHandler.errorUrl" type="hidden" value="test_write_review.jsp" />
				<table>
					<tr>
						<td>Campaign Type:</td>
						<td>
							<dsp:select bean="TestWriteReviewFormHandler.campaignType" name="campaignType" id="campaignType" iclass="selector_primary">
								<dsp:option value="PIE">Pie</dsp:option>
								<dsp:option value="PIE_Reminder">Pie Reminder</dsp:option>
							</dsp:select>
						</td>
					</tr>
					<tr>
						<td>Transaction Type:</td>
						<td>
							<dsp:select bean="TestWriteReviewFormHandler.customerType" name="transactionType" id="transactionType" iclass="selector_primary" onchange="changeFormFields();">
								<dsp:option value="R">Registry</dsp:option>
								<dsp:option value="S">Order</dsp:option>
							</dsp:select>
						</td>
					</tr>

					<tr id="eventTypeRow">
						<td>Event Type:</td>
						<td>
							<dsp:select bean="TestWriteReviewFormHandler.evnetType" iclass="selector_primary">
								<dsp:droplet name="GiftRegistryTypesDroplet">
									<dsp:param name="siteId" value="BedBathUS"/>
									<dsp:oparam name="output">
									<dsp:option value="" selected="selected"><bbbl:label key="lbl_kickstarters_create_registry_dropdown_text" language="${pageContext.request.locale.language}" /></dsp:option>
										<dsp:droplet name="ForEach">
											<dsp:param name="array" param="registryTypes" />
											<dsp:oparam name="output">
												<dsp:param name="regTypes" param="element" />	
												<dsp:getvalueof var="regTypesId" param="regTypes.registryName" />
												<dsp:getvalueof var="registryCode" param="regTypes.registryCode" />
												<dsp:option value="${registryCode}"><dsp:valueof param="element.registryName"></dsp:valueof>
												</dsp:option>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>					
							</dsp:select>
						</td>
					</tr>

					<tr>
						<td>Transaction ID:</td>
						<td>
							<dsp:input bean="TestWriteReviewFormHandler.transactionID" name="transactionID" size="65" id="transactionID" type="text" />
						</td>
					</tr>
					<tr>
						<td>Sku:</td>
						<td>
							<dsp:input bean="TestWriteReviewFormHandler.sku" name="sku" size="65" id="sku" type="text"/>
						</td>
					</tr>
					<tr>
						<td>Token:</td>
						<td>
							<dsp:input bean="TestWriteReviewFormHandler.token" name="token" size="65" id="token" type="text"/>
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td>
							<dsp:input id="btnSubmitForm" type="submit" value="Send" bean="TestWriteReviewFormHandler.writeReview" />
						</td>
					</tr>
				</table>
		</dsp:form>
	</body>
</html>
</dsp:page>