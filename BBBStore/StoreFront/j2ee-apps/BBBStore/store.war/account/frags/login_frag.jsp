<dsp:page>
   <dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof var="isCheckoutPage" param="checkoutFlag"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:getvalueof var="SinglePageCheckout" bean="SessionBean.singlePageCheckout"/>
	<dsp:getvalueof var="registryTypesEvent" bean="SessionBean.registryTypesEvent" />
<c:set var="BedBathUSSite">
		<bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BuyBuyBabySite">
		<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BedBathCanadaSite">
		<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="referer" bean="/OriginatingRequest.referer"/>
	<c:set var="urlParam" value="${param.writeReview}"/>
		
	<dsp:getvalueof var ="securityStatus" bean="Profile.securityStatus"/>
	<dsp:getvalueof var="pageName" param="pageName" />
    <c:set var="lbl_login_frag__forgot_password"> 
     <bbbl:label key="lbl_login_frag__forgot_password" language="${pageContext.request.locale.language}" />
     </c:set>
    <dsp:getvalueof param="pageNameFB" var="pageNameFB"/>
    <dsp:getvalueof param="trackOrder" var="trackOrder"/>
    <c:choose>
     <c:when test="${trackOrder == 'track_order'}">
    	<div id="existingCustomer" class="grid_4 bdr_left prefix_1 omega fbConnectWrap">
     </c:when>
     <c:otherwise>
     	<div id="existingCustomer" class="grid_4 prefix_1 bdr_left omega fbConnectWrap">
     </c:otherwise>
    </c:choose>
	<%-- Fix For Defect BBBSL-3322 --%>
	<c:choose>
		<c:when test="${trackOrder == 'track_order' }">
			<h3><bbbl:label key="lbl_login_frag__existing_customers_track_order" language="${pageContext.request.locale.language}" /></h3>
		</c:when>
		<c:otherwise>
			<h3><bbbl:label key="lbl_login_frag__existing_customers" language="${pageContext.request.locale.language}" /></h3>
		</c:otherwise>
	</c:choose>
		
		<c:choose>
			<c:when test="${not empty trackOrder}">
				<p><bbbt:textArea key="txtarea_trackorder_existing_user_head" language="${pageContext.request.locale.language}"/></p>							
			</c:when>
			<c:otherwise>
				<h4><bbbl:label key="lbl_login_frag__sign_in" language="${pageContext.request.locale.language}" /></h4>							
			</c:otherwise>
		</c:choose>
		<c:choose>
			<c:when test="${securityStatus eq '2' }">
				<c:set var="formId" value="recUserSignIn"/>
				<c:set var="formAction" value="${contextPath}/account/frags/recognizeUserLoginStatusJson.jsp"/>
			</c:when>
			<c:otherwise>
				<c:set var="formId" value="signIn"/>
				<c:set var="formAction" value="${contextPath}/account/login.jsp"/>
			</c:otherwise>
		</c:choose>
		<dsp:form id="${formId}" action="${formAction}" method="post">
			   <input type="hidden"  name="registryTab"  id="registryTab" />
			   <div id="recErrorRes" class="hidden error"></div>
			   <dsp:getvalueof param="showLegacyPwdPopup" id="showLegacyPwdPopup"/>
			   <dsp:getvalueof param="showMigratedPopup" id="showMigratedPopup"/>
			   <dsp:getvalueof param="disableUid" id="disableUid"/>
			   <c:if test="${(empty showLegacyPwdPopup) and (empty showMigratedPopup)}">
			   		<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
						<dsp:param name="value" bean="ProfileFormHandler.errorMap.loginPasswordError"/>
						<dsp:oparam name="false">
							<div class="error">
							    	<dsp:valueof bean="ProfileFormHandler.errorMap.loginPasswordError"></dsp:valueof>
							    
						   </div>
						</dsp:oparam>
						<dsp:oparam name="true">
							<dsp:include page="/global/gadgets/errorMessage.jsp">
				      			<dsp:param name="formhandler" bean="ProfileFormHandler"/>
				    		</dsp:include>
						</dsp:oparam>
					</dsp:droplet>
				   
				</c:if>

   
			<div class="formRow clearfix">
				<div class="input clearfix noMarBot" aria-live="assertive">
				
				<div class="label">
					<label id="lblemail" class="textLgray12 block" for="email"><bbbl:label key="lbl_login_frag__email" language="${pageContext.request.locale.language}" /><span class="visuallyhidden"><bbbl:label key="lbl_for_existing_customers" language="${pageContext.request.locale.language}"/></span> </label> 
				</div>	
					<div class="text noMarBot">
						<dsp:input  id="email" name="email" bean="ProfileFormHandler.value.login"  type="text" iclass="input_large211 block fbConnectEmail" readonly="${disableUid}">
						
						<dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
						</dsp:input>
					</div>	

				</div>
			</div>
			<div class="clear"></div>
			<div class="formRow clearfix noMarTop">
				<div class="input clearfix noMarBot">
					<div class="label posRel">
						<label id="lblpassword" class="textLgray12 block" for="password"><bbbl:label key="lbl_login_frag__password" language="${pageContext.request.locale.language}" /> <span class="visuallyhidden"><bbbl:label key="lbl_for_existing_customers" language="${pageContext.request.locale.language}"/></span></label>
                        <div class="showPassDiv clearfix">
                            <input name="showPassword" type="checkbox" value="" data-toggle-class="showpassLoginFrag" class="showPassword" id="showPassword" />
                            <label for="showPassword" class="lblShowPassword"><bbbl:label key="lbl_show_password" language="${pageContext.request.locale.language}"/></label>
                            <div class="clear"></div> 
                        </div>
					</div>
					<div class="text noMarBot">
						<dsp:input  id="password" bean="ProfileFormHandler.value.password" name="loginPasswd" value="" type="password" autocomplete="off" iclass="showpassLoginFrag">
						<dsp:tagAttribute name="aria-required" value="false"/>
						<dsp:tagAttribute name="aria-labelledby" value="lblpassword errorpassword"/>
						</dsp:input>
					</div>
					<dsp:getvalueof id="trackOrder" param="trackOrder"/>
					
					<div class="fl marTop_10">
						<c:if test="${appid ne 'BedBathCanada'}">
						<span><bbbt:textArea key="txt_password_condition" language="${pageContext.request.locale.language}" /></span>
						</c:if>
						<c:set var="omnitureForCheckout" value="false" />
						<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
							<dsp:param name="value" param="checkoutFlag"/>
							<dsp:oparam name="false">
								<c:set var="omnitureForCheckout" value="true" />
							</dsp:oparam>
						</dsp:droplet>
						<c:if test="${pageContext.request.requestURI=='/store/account/track_order.jsp'}">
						<c:set var="pageName" value="orderTracking"/>
						</c:if>
						<c:if test="${isCheckoutPage == '1'}">
						<c:set var="pageName" value="multiShip"/>
						</c:if>
						<dsp:a  iclass="forgotPassword" href="forgot_password.jsp?omnitureForCheckout=${omnitureForCheckout}&pageName=${pageName}" title="${lbl_login_frag__forgot_password}" onclick="javascript:internalLinks('Forgot Password : Forgot Password Link');"><bbbl:label key="lbl_login_frag__forgot_password" language="${pageContext.request.locale.language}" /></dsp:a>
					</div>

				</div>
			</div>	
		
			<c:set var="signInValue">
				<bbbl:label key='lbl_login_frag__sign_in' language='${pageContext.request.locale.language}' />
			</c:set>
			<%-- Client DOM XSRF | Part -1
			  dsp:input bean="ProfileFormHandler.reclaimLegacyAccountSuccessURL" type="hidden" value="${contextPath}/account/create_account.jsp"/>
			<dsp:input bean="ProfileFormHandler.reclaimLegacyAccountIncorrectPasswordURL" type="hidden" value="${contextPath}/account/login.jsp"/> --%>
			<div id="signinButton" class="button button_active button_active_orange button_disabled" >
				
				<c:choose>
					<c:when test="${GoogleAnalyticsOn and isCheckoutPage == '1'}">
						<dsp:input bean="ProfileFormHandler.checkoutPageLogin" type="submit" value="${signInValue}" onclick="_gaq.push(['_trackEvent', 'checkout', 'click', 'Sign In']);" disabled="true" iclass="enableOnDOMReady">
						
						</dsp:input>
					</c:when>
					<c:when test="${trackOrder == 'track_order'}">
					<c:choose>
					<c:when test="${securityStatus eq '2' }">
						<input name="dummyLoginButton" value="${signInValue}" class="dummyLoginButton" type="button">
						 <dsp:input bean="ProfileFormHandler.recogUserLogin" type="submit" value="${signInValue}" disabled="true" iclass="enableOnDOMReady hidden">
                         </dsp:input>
                         <dsp:input bean="ProfileFormHandler.loginErrorURL" type="hidden" value="${contextPath}/account/frags/recognizeUserLoginStatusJson.jsp"/>
                         <dsp:input bean="ProfileFormHandler.loginSuccessURL" type="hidden" value="${contextPath}/account/frags/recognizeUserLoginStatusJson.jsp"/>
                         <dsp:input bean="ProfileFormHandler.redirectURL" type="hidden" value="order_summary.jsp"/>
					</c:when>
					<c:otherwise>
						<dsp:input bean="ProfileFormHandler.trackOrderlogin" type="submit" value="${signInValue}" disabled="true" iclass="enableOnDOMReady">
						</dsp:input>
					</c:otherwise>
					</c:choose>
					</c:when>
					<c:when test="${urlParam == 'true'}">
					  <input type="hidden" name="writeReview" id="writeReview" value="${urlParam}"/>
					  <dsp:getvalueof var="successUrl" bean="ProfileFormHandler.loginSuccessURL" />
					  <c:if test="${empty successUrl}">
					    <c:choose>
						    <c:when test="${fn:contains(referer, '?') }">
						      <c:set var="delimiter" value="&"/>
						    </c:when>
						    <c:otherwise>
						       <c:set var="delimiter" value="?"/>
						    </c:otherwise>
					  	</c:choose>
					    <c:set var="successUrl" value="${referer}${delimiter}writeReview=true" />
					  </c:if>
					  
					  <dsp:input bean="ProfileFormHandler.loginSuccessURL" type="hidden" value="${successUrl}"/>	
					  <dsp:input bean="ProfileFormHandler.loginErrorURL" type="hidden" value="${contextPath}/account/login.jsp?writeReview=true"/>		  
					  <dsp:input bean="ProfileFormHandler.reviewPageLogin" type="submit" value="${signInValue}" disabled="true" iclass="enableOnDOMReady"/>
					</c:when>
					<c:otherwise>
					<c:choose>
						<c:when test="${securityStatus eq '2' }">
							<c:if test="${not empty param.checkout && param.checkout eq true}">
						    	<c:set var="REC_USER_CHECKOUT" value="REC_USER_CHECKOUT" scope="session" />
						    </c:if>
							<input name="dummyLoginButton" value="${signInValue}" class="dummyLoginButton" type="button">
							<dsp:input bean="ProfileFormHandler.recogUserLogin" type="submit" value="${signInValue}" disabled="true" iclass="enableOnDOMReady hidden">
							</dsp:input>
							<dsp:input bean="ProfileFormHandler.loginErrorURL" type="hidden" value="${contextPath}/account/frags/recognizeUserLoginStatusJson.jsp"/>
	                       	<dsp:input bean="ProfileFormHandler.loginSuccessURL" type="hidden" value="${contextPath}/account/frags/recognizeUserLoginStatusJson.jsp"/>
							<dsp:input bean="ProfileFormHandler.redirectURL" type="hidden" value="${redirectURL}"/>
							<dsp:input bean="ProfileFormHandler.regEventType" type="hidden" value="${registryTypesEvent}"/>
							<dsp:input bean="ProfileFormHandler.value.SinglePageCheckout" type="hidden" value="${SinglePageCheckout}"/>
						</c:when>
						<c:when test="${securityStatus ne '2' }">
							<dsp:input bean="ProfileFormHandler.regularLogin" type="submit" value="${signInValue}" disabled="true" iclass="enableOnDOMReady">
							</dsp:input>
						 </c:when>
					</c:choose>
					</c:otherwise>				
				</c:choose>
			</div>
					
			<dsp:input type="hidden" bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates.currentLevel" value="SHIPPING_SINGLE"/>
			<div class="clear"></div>
		</dsp:form>		
			<c:if test="${FBOn}">	
		<p class="marTop_20 marBottom_20"><bbbl:label key="lbl_OR" language="${pageContext.request.locale.language}" /></p>
		<div class="renderFBConnect" data-fb-connect-section="loginSignin">
			<img src="/_assets/global/images/fb-loader.gif" alt="Facebook User" class="fbConnectLoader" />
		</div>
		</c:if>
	
	</div>
	<c:if test="${securityStatus eq '2' }">
			<input type="hidden" id="sessionEmailId" name="sessionEmailId"  value='<dsp:valueof bean="/atg/userprofiling/Profile.email" />' />
			<dsp:form id="removeItemfromOrderAndLogOut" action="${contextPath}/account/frags/recognizeUserLogOutStatusJson.jsp" method="post">
					<dsp:input bean="ProfileFormHandler.removeItemsFromOrderAndLogOut" type="submit" iclass="hidden" />	
					<dsp:input bean="ProfileFormHandler.logoutErrorURL" type="hidden" value="${contextPath}/account/frags/recognizeUserLogOutStatusJson.jsp"/>
					<dsp:input bean="ProfileFormHandler.logoutSuccessURL" type="hidden" value="${contextPath}/account/frags/recognizeUserLogOutStatusJson.jsp"/>
			</dsp:form>
	</c:if> 
</dsp:page>
