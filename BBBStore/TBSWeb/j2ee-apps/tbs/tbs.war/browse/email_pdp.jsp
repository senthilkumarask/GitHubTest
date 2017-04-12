<dsp:page>
	<dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
  	<dsp:importbean bean="/atg/userprofiling/Profile"/>
  	<dsp:importbean bean="/com/bbb/browse/EmailAFriendFormHandler"/>
  	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/RedirectDroplet"/>
  	<dsp:importbean bean="/com/bbb/framework/security/ValidateParametersDroplet" />

	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
   	<dsp:getvalueof var="templateUrlVar" param="templateUrl" />
                   
	<c:set var="productId"><dsp:valueof value="${fn:escapeXml(param.productId)}"/></c:set>
	<c:set var="categoryId"><c:out value="${param.categoryId}" /></c:set>
	<c:set var="productName"><c:out value="${param.productName}" /></c:set>                         
  
  	<div title="Email a Friend" id="emailFriendFormContainer">               
		<dsp:form id="frmEmailAFriend" method="post" action="${contextPath}/browse/email_pdp.jsp">
			
			<input type="hidden" name="productId" value="${productId}"/>
    		<c:if test="${not empty templateUrlVar}">
				<dsp:input type="hidden" bean="EmailAFriendFormHandler.templateUrl" value="${originatingRequest.contextPath}${templateUrlVar}" />
			</c:if>

    		<fmt:message key="browse_emailAFriend.defaultSubject" var="defaultSubjectText"/>
    		<c:set var="defaultSubjectTxt"><bbbl:label key='lbl_emailAFriendFormInputs_subject' language='${pageContext.request.locale.language}'/></c:set>
    		<dsp:input type="hidden" bean="EmailAFriendFormHandler.subject" value="${defaultSubjectTxt}" />
			<dsp:input type="hidden" bean="EmailAFriendFormHandler.productId" value="${productId}" />
    		
    		<c:url var="successUrlVar" value="/browse/email_pdp_confirm.jsp">
				<c:param name="productId" value="${productId}"/>    
		    </c:url>
		    <dsp:input bean="EmailAFriendFormHandler.queryParam"  type="hidden" value="productId=${param.productId}&categoryId=${param.categoryId}" />
		     <dsp:input bean="EmailAFriendFormHandler.fromPage" type="hidden" value="email_pdp" />
    		<%-- <dsp:input type="hidden" bean="EmailAFriendFormHandler.successURL" value="${successUrlVar}"/> --%>
    
		    <c:url var="errorUrlVar" value="/browse/email_pdp.jsp">
				<c:param name="productId" value="${productId}"/>
				<c:param name="categoryId" value="${param.categoryId}"/>      
		    </c:url>
    		<%-- <dsp:input type="hidden" bean="EmailAFriendFormHandler.errorURL" value="${errorUrlVar}"/> --%>

    		<dsp:getvalueof var="url" vartype="java.lang.String" param="product.template.url"/>
    		
    		<c:choose>
				<c:when test="${not empty url}">
			        <%-- Product Template is set --%>
			        <c:set var="cancelUrlTarget" value="${originatingRequest.contextPath}${url}"/>
		        	<dsp:input type="hidden" bean="EmailAFriendFormHandler.cancelURL" value="${cancelUrlTarget}?productId=${productId}"/>
		      	</c:when>
		      	<c:otherwise>
					<%-- Product Template not set --%>
		        	<dsp:input type="hidden" bean="EmailAFriendFormHandler.cancelURL" value="${originatingRequest.requestURI}?productId=${productId}"/>
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
		
			<fieldset>
				<legend class="hidden">Email a friend</legend>
				<div class="input clearfix row">
					<div class="label small-12 large-2 columns">
						<label id="lblfrndEmail" for="frndEmail"><bbbl:label key="lbl_emailafriend_friend_email" language ="${pageContext.request.locale.language}"/> <span class="required">*</span></label>
					</div>
					<div class="text small-12 large-4 columns end">
						<dsp:input type="text" bean="EmailAFriendFormHandler.recipientEmail" id="frndEmail" name="frndEmail">
	                        <dsp:tagAttribute name="aria-required" value="true"/>
	                        <dsp:tagAttribute name="aria-labelledby" value="lblfrndEmail errorfrndEmail"/>
	                    </dsp:input>
					</div>			
				</div>
				<div class="input clearfix row">
					<div class="label small-12 large-2 columns">
						<label id="lblyourEmail" for="yourEmail"><bbbl:label key="lbl_emailafriend_your_email" language ="${pageContext.request.locale.language}"/> <span class="required">*</span></label>
					</div>
					<div class="text small-12 large-4 columns end">
					
							<dsp:getvalueof var="userEmail"
								bean="/atg/userprofiling/Profile.email" />
						<dsp:input type="text" bean="EmailAFriendFormHandler.senderEmail" value="${userEmail}" id="yourEmail" name="yourEmail">
	                        <dsp:tagAttribute name="aria-required" value="true"/>
	                        <dsp:tagAttribute name="aria-labelledby" value="lblyourEmail erroryourEmail"/>
	                    </dsp:input>
					</div>			
				</div>
				<div class="input clearfix row">
					<div class="label small-12 large-2 columns">
						<label for="comments"><bbbl:label key="lbl_emailafriend_comments" language ="${pageContext.request.locale.language}"/> </label>
					</div>
					<div class="textarea small-12 large-8 columns end">
						<dsp:textarea name="comments" id="comments" bean="EmailAFriendFormHandler.message" maxlength="1500"></dsp:textarea>
					</div>			
				</div>
				<div class="row">
					<div class="small-6 large-3 columns">
						<dsp:input type="hidden" value="submit" bean="EmailAFriendFormHandler.send" name="sendPDPEmail" id="sendPDPEmail"/>
		             	<input type="submit" value="Submit" name="sendEmailButton" id="sendEmailButton" class="button tiny service expand">
		            </div>
					<div class="small-6 large-3 columns end">
	                	<a class="button tiny download expand close-modal" href="#" role="link">Cancel</a>
	                </div>
				</div>
			</fieldset>
		
		</dsp:form>
	</div>
</dsp:page>