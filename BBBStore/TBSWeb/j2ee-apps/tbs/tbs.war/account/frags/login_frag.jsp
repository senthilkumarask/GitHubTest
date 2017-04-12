<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>

	<%-- Variables --%>
	<c:set var="TBS_BedBathUSSite">
		<bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BuyBuyBabySite">
		<bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BedBathCanadaSite">
		<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<c:set var="lbl_login_frag__forgot_password">
		<bbbl:label key="lbl_login_frag__forgot_password" language="${pageContext.request.locale.language}" />
	</c:set>
	<dsp:getvalueof param="pageNameFB" var="pageNameFB"/>
	<dsp:getvalueof param="trackOrder" var="trackOrder"/>

	<%-- KP COMMENT START: these are identical, and i think unnecessary --%>
	<%--
	<c:choose>
		<c:when test="${trackOrder == 'track_order'}">
			<div id="existingCustomer" class="grid_4 bdr_left prefix_1 omega fbConnectWrap">
		</c:when>
		<c:otherwise>
			<div id="existingCustomer" class="grid_4 prefix_1 bdr_left omega fbConnectWrap">
		</c:otherwise>
	</c:choose>
	--%>
	<%-- KP COMMENT END --%>

	<dsp:getvalueof var="isGuestCheckout" param="guestCheckoutFlag"/>
	<c:choose>
		<c:when test="${isGuestCheckout}">
			<c:set var="gridClasses" value="large-offset-1 large-9 columns" scope="request" />
		</c:when>
		<c:otherwise>
			<c:set var="gridClasses" value="large-offset-2 large-9 columns" scope="request" />
		</c:otherwise>
	</c:choose>


	<div class="small-12 large-6 columns">
		<div class="row">
			<div class="${gridClasses}">
				<h2 class="divider"><bbbl:label key="lbl_login_frag__existing_customers" language="${pageContext.request.locale.language}" /></h2>
				<c:choose>
					<c:when test="${not empty trackOrder}">
						<p><bbbt:textArea key="txtarea_trackorder_existing_user_head" language="${pageContext.request.locale.language}"/></p>
					</c:when>
					<c:otherwise>
						<h3><bbbl:label key="lbl_login_frag__sign_in" language="${pageContext.request.locale.language}" /></h3>
					</c:otherwise>
				</c:choose>
				<div class="row">
					<dsp:form id="signIn" action="${contextPath}/account/login.jsp" method="post">

						<dsp:getvalueof param="showLegacyPwdPopup" id="showLegacyPwdPopup"/>
						<dsp:getvalueof param="showMigratedPopup" id="showMigratedPopup"/>
						<c:if test="${(empty showLegacyPwdPopup) and (empty showMigratedPopup)}">
							<dsp:droplet name="IsEmpty">
								<dsp:param name="value" bean="ProfileFormHandler.errorMap.loginPasswordError"/>
								<dsp:oparam name="false">
									<div class="small-12 columns">
										<p class="error">
											<dsp:valueof bean="ProfileFormHandler.errorMap.loginPasswordError"></dsp:valueof>
										</p>
									</div>
								</dsp:oparam>
								<dsp:oparam name="true">
									<dsp:include page="/global/gadgets/errorMessage.jsp">
										<dsp:param name="formhandler" bean="ProfileFormHandler"/>
									</dsp:include>
								</dsp:oparam>
							</dsp:droplet>
						</c:if>
						<c:set var="emailPlaceholder">
							<bbbl:label key="lbl_login_frag__email" language="${pageContext.request.locale.language}" />
						</c:set>
						<c:set var="passPlaceholder">
							<bbbl:label key="lbl_login_frag__password" language="${pageContext.request.locale.language}" />
						</c:set>
						<dsp:getvalueof id="trackOrder" param="trackOrder"/>
						<dsp:getvalueof id="customOrderKey" param="customOrderKey"/>
						<c:set var="omnitureForCheckout" value="false" />
						<dsp:droplet name="IsEmpty">
							<dsp:param name="value" param="checkoutFlag"/>
							<dsp:oparam name="false">
								<c:set var="omnitureForCheckout" value="true" />
							</dsp:oparam>
						</dsp:droplet>

						<div class="small-12 columns">
							<dsp:input id="email" name="email" bean="ProfileFormHandler.value.login" type="email">
								<dsp:tagAttribute name="aria-required" value="true"/>
								<dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
								<dsp:tagAttribute name="placeholder" value="${emailPlaceholder}"/>
							</dsp:input>
						</div>
						<div class="small-12 columns">
							<dsp:input id="password" bean="ProfileFormHandler.value.password" name="currentpassword" type="password" autocomplete="off" value="">
								<dsp:tagAttribute name="aria-required" value="true"/>
								<dsp:tagAttribute name="aria-labelledby" value="lblcurrentpassword errorcurrentpassword"/>
								<dsp:tagAttribute name="placeholder" value="${passPlaceholder}"/>
							</dsp:input>
						</div>
                        <div class="small-12 columns">
						     <dsp:input type="checkbox" bean="ProfileFormHandler.value.isTermsConditions" name="loginConfirmation" iclass="loginConfirmation" id="loginConfirmation"/>
								I agree to the Bed Bath & Beyond <a title="Privacy Policy" href="${contextPath}/static/PrivacyPolicy">Privacy Policy</a>.
						</div>
                        <div class="small-12 large-6 columns">
							<dsp:a id="forgotPasswordDialogLink" iclass="small forgot-password popupShipping" href="forgot_password.jsp?omnitureForCheckout=${omnitureForCheckout}" title="${lbl_login_frag__forgot_password}" onclick="javascript:internalLinks('Forgot Password : Forgot Password Link')";><bbbl:label key="lbl_login_frag__forgot_password" language="${pageContext.request.locale.language}" /></dsp:a>
						</div>
						<div id="forgotPasswordDialog" class="reveal-modal" data-reveal>
						</div>
						<div class="small-12 large-6 columns">
							<c:set var="signInValue">
								<bbbl:label key='lbl_login_frag__sign_in' language='${pageContext.request.locale.language}' />
							</c:set>
							<!--<dsp:input bean="ProfileFormHandler.reclaimLegacyAccountSuccessURL" type="hidden" value="${contextPath}/account/create_account.jsp"/>
							<dsp:input bean="ProfileFormHandler.reclaimLegacyAccountIncorrectPasswordURL" type="hidden" value="${contextPath}/account/login.jsp"/>-->
							<dsp:getvalueof var="isCheckoutPage" param="checkoutFlag"/>
							<c:choose>
								<c:when test="${GoogleAnalyticsOn and isCheckoutPage == '1'}">
									<dsp:input id="regularLoginbutton" bean="ProfileFormHandler.checkoutPageLogin" type="submit" value="${signInValue}" onclick="_gaq.push(['_trackEvent', 'checkout', 'click', 'Sign In']);" disabled="true" iclass="small button service right">
										<dsp:tagAttribute name="aria-pressed" value="false"/>
									</dsp:input>
								</c:when>
								<c:when test="${trackOrder == 'track_order'}">
									<dsp:input id="regularLoginbutton" bean="ProfileFormHandler.trackOrderlogin" type="submit" value="${signInValue}" disabled="true" iclass="small button service right">
										<dsp:tagAttribute name="aria-pressed" value="false"/>
									</dsp:input>
								</c:when>
								<c:when test="${not empty customOrderKey}">
									<dsp:input bean="ProfileFormHandler.value.customOrderKey" type="hidden" paramvalue="customOrderKey"></dsp:input>
									<dsp:input id="regularLoginbutton" bean="ProfileFormHandler.loginForCustomOrder" type="submit" value="${signInValue}" disabled="true" iclass="small button service right">
										<dsp:tagAttribute name="aria-pressed" value="false"/>
									</dsp:input>
								</c:when>
								<c:otherwise>
									<dsp:input tabindex="6" bean="ProfileFormHandler.regularLogin" type="submit" value="${signInValue}" id="regularLoginbutton" iclass="small button service right" disabled="true">
										<dsp:tagAttribute name="aria-pressed" value="false"/>
									</dsp:input>
								</c:otherwise>
							</c:choose>
						</div>
						<dsp:getvalueof var="shippingGr" param="shippingGr"/>
						<c:choose>
							<c:when test="${shippingGr == 'multi'}">
								<dsp:input type="hidden" bean="CheckoutProgressStates.currentLevel" value="SHIPPING_MULTIPLE"/>
							</c:when>
							<c:otherwise>
								<dsp:input type="hidden" bean="CheckoutProgressStates.currentLevel" value="SHIPPING_SINGLE"/>
							</c:otherwise>
						</c:choose>
					</dsp:form>

					<%-- KP COMMENT START: Not sure if FB login is in scope for TBS, hiding for now --%>
					<%-- <c:if test="${FBOn}">
						<div class="renderFBConnect" data-fb-connect-section="loginSignin">
							<img src="/_assets/global/images/fb-loader.gif" alt="Facebook User" class="fbConnectLoader" />
						</div>
					</c:if> --%>
					<%-- KP COMMENT END --%>

				</div>

				<%-- Display 'why create and account' copy if this is part of guest checkout --%>
				<dsp:getvalueof var="isGuestCheckout" param="guestCheckoutFlag"/>
				<c:if test="${isGuestCheckout}">
					<div class="row">
						<div class="small-12 columns">
							<bbbt:textArea key="txt_login_benefits_account" language="<c:out param='${language}'/>"/>
						</div>
					</div>
				</c:if>

			</div>
		</div>
	</div>
	<script type="text/javascript">
		$(document).ready(function(){
			var $targetLogin = $('#loginConfirmation');
			$targetLogin.attr('checked',false);
			$(".forgot-password").attr("data-reveal-ajax","true");
            $(document).foundation('reflow');
			/* $('#forgotPasswordDialogLink').click(function(){
				$('#forgotPasswordDialog').foundation('reveal', 'open')}); */
				$('#loginConfirmation').click(function(){
					var $target = $('#regularLoginbutton');
					var loginConf = $('#loginConfirmation').is(':checked');
					if(loginConf == true){
						$target.removeAttr('disabled');
					}else if(loginConf == false) {
						$target.attr('disabled',true);
					}
				});
		});
	
	</script>
<%-- 	</div> --%>
</dsp:page>
