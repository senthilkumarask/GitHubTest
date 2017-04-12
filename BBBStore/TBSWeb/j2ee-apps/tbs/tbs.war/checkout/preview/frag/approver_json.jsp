<%@ page contentType="text/json" %>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/order/purchase/CommitOrderFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />

	<dsp:droplet name="ErrorMessageForEach">
		<dsp:param bean="CommitOrderFormHandler.formExceptions" name="exceptions" />
		<dsp:oparam name="output">
			<dsp:getvalueof param="message" var="err_msg_key" />
		</dsp:oparam>
	</dsp:droplet>

	<c:choose>
		<c:when test="${not empty err_msg_key}">
			<json:object>
				<json:property name="result" value="${err_msg_key}"/>
			</json:object>
		</c:when>
		<c:otherwise>
			<%-- setting approval required flag to false --%>
			<dsp:setvalue bean="ShoppingCart.current.TBSApprovalRequired" value="false"/>
			<json:object>
				<json:property name="result" value="success"/>
			</json:object>
		</c:otherwise>
	</c:choose>

</dsp:page>
