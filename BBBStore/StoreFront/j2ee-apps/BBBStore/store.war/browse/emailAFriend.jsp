<dsp:page>
<div title="Email a Friend">
  <dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/com/bbb/browse/EmailAFriendFormHandler"/>
  <dsp:importbean bean="/com/bbb/commerce/browse/droplet/RedirectDroplet"/>
  <dsp:importbean bean="/com/bbb/framework/security/ValidateParametersDroplet" />

<dsp:getvalueof id="contextroot" idtype="java.lang.String"
                  bean="/OriginatingRequest.contextPath"/>
                  
<%-- <c:set var="productId">
		<c:out value="${productId}" />
</c:set> --%>
	<c:set var="productId"><dsp:valueof value="${fn:escapeXml(param.productId)}"/></c:set>
<c:set var="categoryId">
	<c:out value="${param.categoryId}" />
</c:set>
<c:set var="productName">
	<c:out value="${param.productName}" />
</c:set>
                  
	              
	<%-- Validate external parameters --%>
	<dsp:droplet name="ValidateParametersDroplet">
		 <dsp:param value="productId" name="paramArray" />
		 <dsp:param value="${productId}" name="paramsValuesArray" />
		 <dsp:oparam name="error">
		   <dsp:droplet name="RedirectDroplet">
		     <dsp:param name="url" value="/404.jsp" />
		   </dsp:droplet>
		 </dsp:oparam>
	</dsp:droplet>                   
                  
<dsp:form id="frmEmailAFriend" method="post" action="${contextroot}/browse/gadgets/email_friend_confirm.jsp">

    <dsp:getvalueof var="templateUrlVar" param="templateUrl" />
    <c:if test="${not empty templateUrlVar}">
      <dsp:input bean="EmailAFriendFormHandler.templateUrl" 
            value="${originatingRequest.contextPath}${templateUrlVar}" type="hidden"/>
    </c:if>

    <dsp:input bean="EmailAFriendFormHandler.productId" value="${productId}" type="hidden"/>
    <fmt:message key="browse_emailAFriend.defaultSubject" var="defaultSubjectText"/>
    
    <c:set var="defaultSubjectTxt"><bbbl:label key='lbl_emailAFriendFormInputs_subject' language='${pageContext.request.locale.language}'/></c:set>
    <dsp:input bean="EmailAFriendFormHandler.subject" value="${defaultSubjectTxt}" type="hidden"/>

    <!--<c:url var="successUrlVar" value="/browse/gadgets/email_friend_confirm.jsp">
      <c:param name="productId" value="${productId}"/>    
    </c:url>
    <dsp:input bean="EmailAFriendFormHandler.successURL" type="hidden" value="${successUrlVar}"/>
    
    <c:url var="errorUrlVar" value="/browse/emailAFriend.jsp">
      <c:param name="productId" value="${productId}"/>
      <c:param name="productId" value="${categoryId}"/>      
    </c:url>
    <dsp:input bean="EmailAFriendFormHandler.errorURL" type="hidden" value="${errorUrlVar}"/>-->
    <dsp:input bean="EmailAFriendFormHandler.queryParam"  type="hidden" value="productId=${productId}&categoryId=${categoryId}" />
    <dsp:input bean="EmailAFriendFormHandler.fromPage" type="hidden" value="emailafriend" />

    <dsp:getvalueof var="url" vartype="java.lang.String" param="product.template.url"/>
    <c:choose>
      <c:when test="${not empty url}">
        <%-- Product Template is set --%>
        <c:set var="cancelUrlTarget" value="${originatingRequest.contextPath}${url}"/>
        <dsp:input bean="EmailAFriendFormHandler.cancelURL" type="hidden"
            value="${cancelUrlTarget}?productId=${productId}"/>
      </c:when>
      <c:otherwise>
        <%-- Product Template not set --%>
        <dsp:input bean="EmailAFriendFormHandler.cancelURL" type="hidden"
          value="${originatingRequest.requestURI}?productId=${productId}"/>
      </c:otherwise>
    </c:choose>

    <p class="atg_store_pageDescription"></p>
	
  	<div class="error">
	<dsp:droplet name="/atg/dynamo/droplet/Switch">
		<dsp:param bean="EmailAFriendFormHandler.formError" name="value"/>
		<dsp:oparam name="true">
			<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
			<dsp:param bean="EmailAFriendFormHandler.formExceptions" name="exceptions"/>
			<dsp:oparam name="output">
				<dsp:valueof param="message"/>
			</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>
	</div>	
	
    <input name="productId" type="hidden" value="${productId}"/>
		<fieldset>
		<legend class="hidden">Email a friend</legend>
			<div class="input clearfix formRow marTop_10">
				<div class="label width_2 fl">
					<label id="lblfrndEmail" for="frndEmail"><bbbl:label key='lbl_emailafriend_friend_email' language="${pageContext.request.locale.language}" /> <span class="required">*</span></label>
				</div>
				<div class="text width_2 fl marRight_20">
					<dsp:input type="email" id="frndEmail" name="frndEmail" bean="EmailAFriendFormHandler.recipientEmail">
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblfrndEmail errorfrndEmail"/>
                    </dsp:input>
				</div>	
				<div class="fl frndEmailInfoTxt"><bbbl:label key='lbl_emailafriend_placeholder' language="${pageContext.request.locale.language}" /></div>
			</div>
			<div class="input clearfix formRow noMarTop">
				<div class="label width_2 fl">
					<label id="lblyourEmail" for="yourEmail"><bbbl:label key='lbl_emailafriend_your_email' language="${pageContext.request.locale.language}" /> <span class="required">*</span></label>
				</div>
				<div class="text width_2 fl">
						<dsp:getvalueof var="userEmail"
								bean="/atg/userprofiling/Profile.email" />
						<dsp:input type="email" id="yourEmail" name="yourEmail" value="${userEmail}" bean="EmailAFriendFormHandler.senderEmail">
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblyourEmail erroryourEmail"/>
                    </dsp:input>
				</div>			
			</div>
			<div class="input clearfix formRow noMarTop">
				<div class="label width_2 fl">
					<label for="comments"><bbbl:label key='lbl_emailafriend_comments' language="${pageContext.request.locale.language}" /> </label>
				</div>
				<div class="textarea width_4 fl">
					<dsp:textarea name="comments" id="comments" bean="EmailAFriendFormHandler.message" maxlength="1500"></dsp:textarea>
				</div>			
			</div>
			<div id="captchaDiv" class="clearfix noMarTop">
				<div class="input clearfix">
					<div class="label width_2 fl">
						<label id="lblcaptchaAnswer" for="captchaAnswer"><bbbl:label key='lbl_emailafriend_captcha' language="${pageContext.request.locale.language}" /> <span class="required">*</span></label>
					</div>
					<div class="text width_3 fl">
						<img width="300" height="100" src="" alt="captcha answer"/>
						<dsp:input type="text" name="captchaAnswer" id="captchaAnswer" value="" iclass="marTop_5" bean="EmailAFriendFormHandler.captchaAnswer" autocomplete="off">
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblcaptchaAnswer errorcaptchaAnswer"/>
                        </dsp:input>
					</div>			
				</div>
			</div>
			
			<%-- Change for BBBSL-3817,  Starts --%>
			<div class="input clearfix formRow noMarTop">
				<div class="checkEmailFriend clearfix">
                    <div class="checkbox fl">
                        <dsp:input id="emailPromotions" type="checkbox"
                                name="emailPromotions" checked="false" iclass="marRight_5"
                                bean="EmailAFriendFormHandler.ccFlag">
                            <dsp:tagAttribute name="aria-checked" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblemailPromotions"/>
                        </dsp:input>
                    </div>
                    <div class="label fl">
                        <label id="lblemailPromotions" for="emailPromotions"><bbbl:label key="lbl_email_checkbox_copy_email" language ="${pageContext.request.locale.language}"/></label>
                    </div>
                    <div class="clear"></div>
				</div>
			</div>
			<%-- Change for BBBSL-3817,  Ends --%>
			
			<div class="marTop_20 buttonpane clearfix">
				<div class="ui-dialog-buttonset">
					<div class="button button_active">
						<dsp:input type="submit"  bean="EmailAFriendFormHandler.send" value="SUBMIT" >
                            <%-- <dsp:tagAttribute name="aria-pressed" value="false"/> --%>
                            <dsp:tagAttribute name="role" value="button"/>
                        </dsp:input>
					</div>
					<a class="buttonTextLink close-any-dialog" href="#" role="link"><bbbl:label key="lbl_notifyme_cancel" language="${pageContext.request.locale.language}" /></a>
				</div>
			</div>
		</fieldset>
		
  </dsp:form>
</div>
<script type="text/javascript">
if (typeof s !== 'undefined') {
	var omni_productName = '${fn:replace(fn:replace(productName,'\'',''),'"','')}';
	s.pageName ='Product Detail > ' + omni_productName;
	s.channel = 'Product Details Page';
	s.prop1 = 'Product Details Page';
	s.prop2 = 'Product Details Page';
	s.prop3 = 'Product Details Page';
	s.events = 'event47';
	s.products = ';' + ${productId} + ';;;;eVar25=' + 'Email a Friend';
	s.eVar51='Email a Friend';
	fixOmniSpacing();
	var s_code = s.t();
	if (s_code)
		document.write(s_code);
	}
</script>	
</dsp:page>










