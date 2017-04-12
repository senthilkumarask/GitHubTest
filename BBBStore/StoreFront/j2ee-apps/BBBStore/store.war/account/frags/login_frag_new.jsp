<dsp:page>
   <dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
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
    <c:set var="lbl_login_frag__forgot_password"> 
     <bbbl:label key="lbl_login_frag__forgot_password" language="${pageContext.request.locale.language}" />
     </c:set>
    <dsp:getvalueof param="pageNameFB" var="pageNameFB"/>
    <dsp:getvalueof param="trackOrder" var="trackOrder"/>

	<div id="existingCustomer" class="fbConnectWrap">

		<dsp:form id="signInModal" action="/store/account/login_frag_json.jsp" method="post">
		<input type="hidden"  name="registryTab"  id="registryTab" />
		<dsp:getvalueof param="showLegacyPwdPopup" id="showLegacyPwdPopup"/>
		<dsp:getvalueof param="showMigratedPopup" id="showMigratedPopup"/>
		<dsp:getvalueof param="disableUid" id="disableUid"/>
		<dsp:include page="/global/gadgets/errorMessage.jsp">
			<dsp:param name="formhandler" bean="ProfileFormHandler"/>
		</dsp:include>

			<div class="formRow clearfix">
				<div class="input clearfix noMarBot" aria-live="assertive">
				
				<div class="label">
					<label id="lblemail" class="textLgray12 block" for="email"><bbbl:label key="lbl_login_frag__email" language="${pageContext.request.locale.language}" /><span class="visuallyhidden"><bbbl:label key="lbl_for_existing_customers" language="${pageContext.request.locale.language}"/></span> </label> 
				</div>	
					<div class="text noMarBot width_295">
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
                            <input name="showPassword" type="checkbox" value="" data-toggle-class="showpassLoginFrag" class="showPasswordModal" id="showPasswordModal" />
                            <label for="showPassword" class="lblShowPassword"><bbbl:label key="lbl_show_password" language="${pageContext.request.locale.language}"/></label>
                            <div class="clear"></div> 
                        </div>
					</div>
					<div class="text noMarBot width_295">
						<dsp:input  id="password" bean="ProfileFormHandler.value.password" name="loginPasswd" value="" type="password" autocomplete="off" iclass="showpassLoginFrag">
						<dsp:tagAttribute name="aria-required" value="false"/>
						<dsp:tagAttribute name="aria-labelledby" value="lblpassword errorpassword"/>
						</dsp:input>
					</div>
					<dsp:getvalueof id="trackOrder" param="trackOrder"/>
					
					<div class="fl marTop_10">
						<c:if test="${appid ne 'BedBathCanada'}">
						<div><bbbt:textArea key="txt_password_condition" language="${pageContext.request.locale.language}" /></div>
						</c:if>
						<c:set var="omnitureForCheckout" value="false" />
						<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
							<dsp:param name="value" param="checkoutFlag"/>
							<dsp:oparam name="false">
								<c:set var="omnitureForCheckout" value="true" />
							</dsp:oparam>
						</dsp:droplet>
						<dsp:a  iclass="forgotPasswordModal" href="forgot_password.jsp?omnitureForCheckout=${omnitureForCheckout}&pageName=${pageName}" title="${lbl_login_frag__forgot_password}" onclick="javascript:internalLinks('Forgot Password : Forgot Password Link');"><bbbl:label key="lbl_login_frag__forgot_password" language="${pageContext.request.locale.language}" /></dsp:a>
					</div>

				</div>
			</div>	
		
			<c:set var="signInValue">
				<bbbl:label key='lbl_login_frag__sign_in' language='${pageContext.request.locale.language}' />
			</c:set>
			<dsp:input bean="ProfileFormHandler.reclaimLegacyAccountSuccessURL" type="hidden" value="${contextPath}/account/create_account.jsp"/>
			<dsp:input bean="ProfileFormHandler.reclaimLegacyAccountIncorrectPasswordURL" type="hidden" value="${contextPath}/account/login.jsp"/>
			
			<div id="signinButton" class="button_disabled">
			<dsp:input type="submit" bean="ProfileFormHandler.confirmPassword" value="${signInValue}" iclass="enableOnDOMReady blueButton button-Med">
			<%-- <dsp:tagAttribute name="aria-pressed" value="true" /> --%>
			</dsp:input>
	
			</div>

				<input id="pageRedirection" name="pageRedirection" type="hidden" readonly="readonly" data-referurl="">
				
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
</dsp:page>