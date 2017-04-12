<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<dsp:page>

<dsp:getvalueof var="emailType" param="emailTemplateVO.emailType" />
<dsp:getvalueof var="channel" param="emailTemplateVO.channelId" />
<c:choose>
    <c:when test="${((emailType eq 'GSEmailCart')||(emailType eq 'GSEmailRegistry')) && (channel eq 'FF1')}">
        <dsp:include page="/emailtemplates/cart/cart_emailer_ff1.jsp" >
        	<dsp:param name="emailHeader" param="emailTemplateVO.emailHeader"/>
        	<dsp:param name="emailTemplateVO" param="emailTemplateVO" />
        	<dsp:param name="emailFooter" param="emailTemplateVO.emailFooter"/>
        </dsp:include>
    </c:when>
    <c:when test="${(emailType eq 'GSEmailCart') && (channel eq 'FF2')}">
        <dsp:include page="/emailtemplates/cart/cart_emailer_ff2.jsp" >
        	<dsp:param name="emailHeader" param="emailTemplateVO.emailHeader"/>
        	<dsp:param name="emailTemplateVO" param="emailTemplateVO" />
        	<dsp:param name="emailFooter" param="emailTemplateVO.emailFooter"/>
        </dsp:include>
    </c:when>
    <c:when test="${(emailType eq 'GSEmailTable') && (channel eq 'FF1')}">
        <dsp:include page="/emailtemplates/table/table_registry_emailer_ff1.jsp" >
        	<dsp:param name="emailHeader" param="emailTemplateVO.emailHeader"/>
        	<dsp:param name="emailTemplateVO" param="emailTemplateVO" />
        	<dsp:param name="emailFooter" param="emailTemplateVO.emailFooter"/>
        </dsp:include>
    </c:when>
	<c:when test="${(emailType eq 'GSEmailCompareProduct')&&(channel eq 'FF1')}">
        <dsp:include page="/emailtemplates/compare/compare_product_email_ff1.jsp" >
        	<dsp:param name="emailHeader" param="emailTemplateVO.emailHeader"/>
        	<dsp:param name="emailTemplateVO" param="emailTemplateVO" />
        	<dsp:param name="emailFooter" param="emailTemplateVO.emailFooter"/>
        </dsp:include>
    </c:when>
    <c:when test="${(emailType eq 'GSEmailCompareProduct')&&(channel eq 'FF2')}">
        <dsp:include page="/emailtemplates/compare/compare_product_email_ff2.jsp" >
        	<dsp:param name="emailHeader" param="emailTemplateVO.emailHeader"/>
        	<dsp:param name="emailTemplateVO" param="emailTemplateVO" />
        	<dsp:param name="emailFooter" param="emailTemplateVO.emailFooter"/>
        </dsp:include>
    </c:when>
    <c:when test="${emailType eq 'SubmitFeedback'}">
        <dsp:include page="/emailtemplates/feedback/feedback_email.jsp" >
        	<dsp:param name="emailHeader" param="emailTemplateVO.emailHeader"/>
        	<dsp:param name="emailBody"   param="emailTemplateVO.emailBody" />        	
        	<dsp:param name="emailFooter" param="emailTemplateVO.emailFooter"/>
        </dsp:include>
    </c:when>
    <c:when test="${emailType eq 'GSEmailTableChecklist'}">
        <dsp:include page="/emailtemplates/table/table_checklist_emailer.jsp" >
        	<dsp:param name="emailHeader" param="emailTemplateVO.emailHeader"/>
        	<dsp:param name="emailBody"   param="emailTemplateVO.emailBody" />        	
        	<dsp:param name="emailFooter" param="emailTemplateVO.emailFooter"/>
        </dsp:include>
    </c:when>
    <c:when test="${emailType eq 'GSEmailPDP'}">
        <dsp:include page="/emailtemplates/product/pdp_email.jsp" >
        	<dsp:param name="emailHeader" param="emailTemplateVO.emailHeader"/>
        	<dsp:param name="emailBody"   param="emailTemplateVO.emailBody" />        	
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