<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<dsp:page>

<dsp:importbean bean="/com/bbb/cms/droplet/EmailTemplateDroplet" />

<dsp:getvalueof var="emailTemplateVO" param="emailTemplateVO" />
<dsp:getvalueof var="emailType" param="emailTemplateVO.emailType" />
<dsp:getvalueof var="internationalFlag" param="emailTemplateVO.internationalFlag"/>
<dsp:importbean bean="/com/bbb/internationalshipping/formhandler/InternationalShipFormHandler"/>
<dsp:importbean bean="/com/bbb/email/internationalDetail/UpdateEmailPropertyFormHandler"/>

<c:choose>
	<c:when test="${(emailType eq 'EmailCart') or (emailType eq 'OrderConfirmation') or (emailType eq 'EmailSavedItems')}">
		<c:choose>
			<c:when test="${(emailTemplateVO.currencyCode != null) and (emailTemplateVO.countryCode != null) and (internationalFlag eq true)}">
				<dsp:setvalue bean="InternationalShipFormHandler.userSelectedCurrency" paramvalue="emailTemplateVO.currencyCode"/>
				<dsp:setvalue bean="InternationalShipFormHandler.userSelectedCountry" paramvalue="emailTemplateVO.countryCode"/>
				<dsp:setvalue bean="InternationalShipFormHandler.updateUserContext" paramvalue="Submit"/>
			</c:when>
			<c:otherwise>
				<dsp:setvalue bean="InternationalShipFormHandler.userSelectedCurrency" value="USD"/>
				<dsp:setvalue bean="InternationalShipFormHandler.userSelectedCountry" value="US"/>
				<dsp:setvalue bean="InternationalShipFormHandler.updateUserContext" paramvalue="Submit"/>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<c:if test="${(emailTemplateVO.currencyCode != null) and (emailTemplateVO.countryCode != null)}">
			<dsp:setvalue bean="UpdateEmailPropertyFormHandler.userSelectedCurrency" paramvalue="emailTemplateVO.currencyCode"/>
			<dsp:setvalue bean="UpdateEmailPropertyFormHandler.userSelectedCountry" paramvalue="emailTemplateVO.countryCode"/>
			<dsp:setvalue bean="UpdateEmailPropertyFormHandler.updateUserContextForEmail" paramvalue="Submit"/>
		</c:if>
	</c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${emailType eq 'EmailCart'}">
        <dsp:include page="/emailtemplates/cart/cart_emailer.jsp" >
        	<dsp:param name="emailHeader" param="emailTemplateVO.emailHeader"/>
        	<dsp:param name="order" param="order" />
        	<dsp:param name="emailFooter" param="emailTemplateVO.emailFooter"/>
        </dsp:include>
    </c:when>
    <c:when test="${emailType eq 'OrderConfirmation'}">
        <dsp:include page="/emailtemplates/order/order_confirmation_email.jsp" >
        	<dsp:param name="emailHeader" param="emailTemplateVO.emailHeader"/>
        	<dsp:param name="order" param="order"/>
        	<dsp:param name="userid" param="userid"/>
        	<dsp:param name="emailFooter" param="emailTemplateVO.emailFooter"/>
        </dsp:include>
    </c:when>
    <c:when test="${emailType eq 'EmailSavedItems'}">
        <dsp:include page="/emailtemplates/saveditems_email_template.jsp" >
        	<dsp:param name="emailHeader" param="emailTemplateVO.emailHeader"/>
        	<dsp:param name="savedItems" param="savedItems" />
        	<dsp:param name="emailFooter" param="emailTemplateVO.emailFooter"/>
	    </dsp:include>
    </c:when>
    <c:otherwise>
        <dsp:include page="/emailtemplates/common/generic_email.jsp" >
	        <dsp:param name="emailHeader" param="emailTemplateVO.emailHeader"/>
        	<dsp:param name="emailBody"   param="emailTemplateVO.emailBody" />
        	<dsp:param name="emailFooter" param="emailTemplateVO.emailFooter"/>
		</dsp:include>
    </c:otherwise>
</c:choose>
</dsp:page>