<dsp:page>
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
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <c:set var="lbl_spc_login_frag__forgot_password"> 
     <bbbl:label key="lbl_spc_login_frag__forgot_password" language="${pageContext.request.locale.language}" />
     </c:set>
    <dsp:getvalueof param="pageNameFB" var="pageNameFB"/>
    <dsp:getvalueof param="trackOrder" var="trackOrder"/>
    

   <div class="grid_7 alpha flatform" id="signInWrap">



		<div class="grid_7 alpha omega">		
							
			
				<h5><bbbl:label key="lbl_spc_registered_customers" language="${pageContext.request.locale.language}" /></h5>		
			
		</div>
		
		<div class="grid_7 alpha omega">
			<div class="error">
			   <c:if test="${(empty showLegacyPwdPopup) and (empty showMigratedPopup)}">
			   	<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
						<dsp:param name="value" bean="ProfileFormHandler.errorMap.loginPasswordError"/>
						<dsp:oparam name="false">
						   
						</dsp:oparam>
						<dsp:oparam name="true">
							<dsp:include page="/global/gadgets/errorMessage.jsp">
				      			<dsp:param name="formhandler" bean="ProfileFormHandler"/>
				    		</dsp:include>
						</dsp:oparam>
					</dsp:droplet>
				</c:if>
			</div>
		</div>

		<div class="grid_7 alpha omega marTop_10">
			<dsp:form id="signIn" action="${contextPath}/account/login.jsp" method="post" iclass="flatform">
				<span class="txtOffScreen" aria-live="polite" aria-atomic="true" id="readLabel"></span>
				<dsp:getvalueof param="showLegacyPwdPopup" id="showLegacyPwdPopup"/>
				<dsp:getvalueof param="showMigratedPopup" id="showMigratedPopup"/>

				

				<div class="formRow grid_3 alpha noMarTop clearfix">
					<%-- Fix For Defect BBBSL-3322 --%>	
					<div class="input_wrap " aria-live="assertive">					
						
						<label id="lblemail" class="textLgray12 block" for="email"><bbbl:label key="lbl_spc_login_frag__email" language="${pageContext.request.locale.language}" /><span class="visuallyhidden"><bbbl:label key="lbl_spc_for_existing_customers" language="${pageContext.request.locale.language}"/></span> </label> 
						
						<dsp:input tabindex="3" id="email" name="email" bean="ProfileFormHandler.value.login"  type="text" iclass="fbConnectEmail">						
							<dsp:tagAttribute name="aria-describedby" value="readLabel"/>
						</dsp:input>

					</div>
				</div>
				
				<div class="formRow grid_3 clearfix noMarTop">
					<div class="input_wrap ">
						<label id="lblpassword" class="textLgray12 block" for="password"><bbbl:label key="lbl_spc_login_frag__password" language="${pageContext.request.locale.language}" /> <span class="visuallyhidden"><bbbl:label key="lbl_spc_for_existing_customers" language="${pageContext.request.locale.language}"/></span></label>
						
						<div class="passwordWrap clearfix">
							<dsp:input tabindex="4" id="password" bean="ProfileFormHandler.value.password" name="loginPasswd" value="" type="password" autocomplete="off" iclass="showpassLoginFrag">
								<dsp:tagAttribute name="aria-required" value="false"/>
								<dsp:tagAttribute name="aria-describedby" value="readLabel"/>
							</dsp:input>
						</div>

					
						
						<div class="forgotPasswordWrap">
							<c:set var="omnitureForCheckout" value="false" />
							<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
								<dsp:param name="value" param="checkoutFlag"/>
								<dsp:oparam name="false">
									<c:set var="omnitureForCheckout" value="true" />
								</dsp:oparam>
							</dsp:droplet>
							<dsp:a tabindex="6" iclass="forgotPassword" page="/account/frags/forgot_password.jsp?omnitureForCheckout=${omnitureForCheckout}&pageName=spcPage" title="${lbl_spc_login_frag__forgot_password}" onclick="javascript:internalLinks('Forgot Password : Forgot Password Link');"><bbbl:label key="lbl_spc_login_frag__forgot_password" language="${pageContext.request.locale.language}" /></dsp:a>

							
							<div class="showPassDiv clearfix">
		                   <label for="showPassword" class="lblShowPassword"><bbbl:label key="lbl_spc_show_password" language="${pageContext.request.locale.language}"/></label>
		                   <input name="showPassword" type="checkbox" value="" data-toggle-class="showpassLoginFrag" class="showPassword" id="showPassword" />
		                   
		                         <div class="clear"></div> 
		               </div>
						</div>

					</div>
				</div>	
			
				<c:set var="signInValue">
					<bbbl:label key='lbl_spc_login_frag__sign_in' language='${pageContext.request.locale.language}' />
				</c:set>

				<dsp:input bean="ProfileFormHandler.reclaimLegacyAccountSuccessURL" type="hidden" value="${contextPath}/account/create_account.jsp"/>
				<dsp:input bean="ProfileFormHandler.reclaimLegacyAccountIncorrectPasswordURL" type="hidden" value="${contextPath}/account/login.jsp"/>
				
				<div class="grid_1 alpha omega marTop_15">
					<div id="signinButton" class="" tabindex="7">
						<dsp:getvalueof var="isCheckoutPage" param="checkoutFlag"/>
						<dsp:input bean="ProfileFormHandler.value.SinglePageCheckout" type="hidden" value="true"/>
						<c:choose>
							<c:when test="${GoogleAnalyticsOn and isCheckoutPage == '1'}">
								<dsp:input bean="ProfileFormHandler.spCheckoutLogin" type="submit" value="${signInValue}" onclick="_gaq.push(['_trackEvent', 'checkout', 'click', 'Sign In']);" disabled="true" iclass="btnAddtocart button-Small enableOnDOMReady">
								
								</dsp:input>
							</c:when>
							<c:when test="${trackOrder == 'track_order'}">
								<dsp:input bean="ProfileFormHandler.trackOrderlogin" type="submit" value="${signInValue}" disabled="true" iclass="btnAddtocart button-Med enableOnDOMReady ">
								
								</dsp:input>
							</c:when>
							<c:otherwise>
								<dsp:input bean="ProfileFormHandler.spCheckoutLogin" type="submit" value="${signInValue}" disabled="true" iclass="btnAddtocart button-Med enableOnDOMReady">
								
								</dsp:input>
							</c:otherwise>				
						</c:choose>
					</div>
				</div>
						
				<dsp:input type="hidden" bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates.currentLevel" value="SP_CHECKOUT_SINGLE"/>
				<div class="clear"></div>
			</dsp:form>		
		</div>

		<div class="clear"></div>

	</div>

	
		<%--<c:if test="${FBOn}">	
		<div class="grid_2 omega " id="FBSinglePageCheckout">
			<span id="spcFBor"><bbbl:label key="lbl_spc_or2" language="${pageContext.request.locale.language}"/></span>
			<h5><bbbl:label key="lbl_spc_sign_in_facebook" language="${pageContext.request.locale.language}"/></h5>			
			<div class="renderFBConnect" data-fb-connect-section="loginSignin">				
				<img src="/_assets/global/images/fb-loader.gif" alt="Facebook User" class="fbConnectLoader" />
			</div>
		</div>	
		</c:if> --%>

	
	
</dsp:page>