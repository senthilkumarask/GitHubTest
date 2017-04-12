<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<dsp:page>

<dsp:importbean bean="/com/bbb/cms/droplet/EmailTemplateDroplet" />

<dsp:getvalueof var="emailType" param="emailTemplateVO.emailType" />

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
        	<dsp:param name="hostUrl" param="emailTemplateVO.hostUrl"/>
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