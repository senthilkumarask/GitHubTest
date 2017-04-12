<dsp:page>
	<dsp:importbean
		bean="/com/bbb/commerce/checkout/formhandler/BBBBillingAddressFormHandler" />
		<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<fieldset id="billingContactInfo">
	<legend class="hidden"><bbbl:label key="lbl_checkout_billing_contact" language="${pageContext.request.locale.language}" /></legend>
		
		<dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
		<h3 class="sectionHeading">
			<bbbl:label key="lbl_contact_info_heading" language="${pageContext.request.locale.language}" />
		</h3>
		<div class="subForm">
            
            <dsp:getvalueof var="userEmail" bean="/atg/commerce/ShoppingCart.current.billingAddress.email"/>
            <c:if test="${empty userEmail}">
                <dsp:getvalueof var="userEmail" bean="/atg/userprofiling/Profile.email"/>
            </c:if>
			<div class="fieldsInlineWrapper clearfix">
				
				<div class="input">
					<div class="label">
						<label id="lblemail" for="email">
							<bbbl:label key="lbl_cart_email_email" language="${pageContext.request.locale.language}" /> 
							<span class="required">*</span>
						</label>
					</div>
					<div class="text">
						<div>
							<%--R2.2 Story - AY - Disable field if order is of type Paypal and continue to paypal is selected --%>
						<c:choose>
							<c:when test = "${payPalOrder eq true}">
								<dsp:input type="text" name="email"
								bean="BBBBillingAddressFormHandler.billingAddress.email"
								value="${userEmail}" id="email" required="true" disabled="true">
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblemail erroremail"/>
                                <dsp:tagAttribute name="data-paypalEmail" value="${userEmail}"/>
                            	</dsp:input>
							</c:when>
							<c:otherwise>
								<dsp:input type="text" name="email"
								bean="BBBBillingAddressFormHandler.billingAddress.email"
								value="${userEmail}" id="email" required="true">
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblemail erroremail"/>
								<dsp:tagAttribute name="aria-describedby" value="erroremail"/> 
                            	</dsp:input>
							</c:otherwise>
						</c:choose>
							
						</div>
					</div>
				</div>
				

				
				<div class="input">
					<div class="label">
						<label id="lblemailConfirm" for="emailConfirm">
							<bbbl:label key="lbl_checkout_email_confirm" language="${pageContext.request.locale.language}" />
							<span class="required">*</span> </label>
					</div>
                    <dsp:getvalueof var="conEmail" bean="BBBBillingAddressFormHandler.confirmedEmail"/>
                    <c:if test="${empty conEmail}">                        
                        <c:set var="conEmail" value="${userEmail}" />
                    </c:if>
					<div class="text">
						<div>
						<%--R2.2 Story - AY - Disable field if order is of type Paypal and continue to paypal is selected --%>
						<c:choose>
							<c:when test = "${payPalOrder eq true}">
								<dsp:input type="text" name="emailConfirm"
								bean="BBBBillingAddressFormHandler.confirmedEmail"
								value="${conEmail}" id="emailConfirm" required="true" disabled="true">
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblemailConfirm erroremailConfirm"/>
								<dsp:tagAttribute name="aria-describedby" value="erroremailConfirm"/> 
                                <dsp:tagAttribute name="data-paypalConfEmail" value="${conEmail}"/>
                            	</dsp:input>
							</c:when>
							<c:otherwise>
								<dsp:input type="text" name="emailConfirm"
								bean="BBBBillingAddressFormHandler.confirmedEmail"
								value="${conEmail}" id="emailConfirm" required="true">
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblemailConfirm erroremailConfirm"/>
								<dsp:tagAttribute name="aria-describedby" value="erroremailConfirm"/> 
                            	</dsp:input>
							</c:otherwise>
						</c:choose>
							
						</div>
					</div>
				</div>
				
			</div>
            <dsp:getvalueof var="mobileNumber" bean="/atg/commerce/ShoppingCart.current.billingAddress.mobileNumber"/>
            <c:if test="${empty mobileNumber}">
                <dsp:getvalueof var="mobileNumber" bean="/atg/userprofiling/Profile.mobileNumber"/>
            </c:if>
			<c:if test="${empty mobileNumber}">
                <dsp:getvalueof var="mobileNumber" bean="/atg/userprofiling/Profile.phoneNumber"/>
            </c:if> 
			<div class="grid_3 alpha clearfix">
				<div class="inputPhone clearfix phoneFieldWrap requiredPhone">
                    <fieldset class="phoneFields">
						<legend class="phoneFieldLegend"><bbbl:label key='lbl_phonefield_phnumber' language='${pageContext.request.locale.language}'/></legend>
						<label id="lblbasePhoneFull" for="basePhoneFull">
							<bbbl:label key="lbl_checkout_phone" language="${pageContext.request.locale.language}" /><span class="required">&nbsp;*</span>
						</label>					
						<%--R2.2 Story - AY - Disable field if order is of type Paypal and continue to paypal is selected --%>
						<c:choose>
							<c:when test = "${payPalOrder eq true}">
								<input id="basePhoneFull" role="textbox" type="text" value="" name="basePhoneFull" class="phoneField required" maxlength="10" aria-required="true" 
								aria-labelledby="errormessage1 lblbasePhoneFull errorbasePhoneFull" aria-describedby="errorbasePhoneFull" disabled="disabled" data-paypalMobile="${mobileNumber}"/>
							</c:when>
							<c:otherwise>
								<input id="basePhoneFull" role="textbox" type="text" value="" name="basePhoneFull" class="phoneField required" maxlength="10" aria-required="false" aria-describedby="errorbasePhoneFull" aria-labelledby="errormessage1 lblbasePhoneFull errorbasePhoneFull"/>
							</c:otherwise>
						</c:choose>
						<dsp:input type="hidden" name="mobileNumber" iclass="fullPhoneNum" bean="BBBBillingAddressFormHandler.billingAddress.mobileNumber" value="${mobileNumber}" />
						<div class="cb">
							<label id="errorbasePhoneFull" for="basePhoneFull" generated="true" class="error"></label>
						</div>
                    </fieldset>
				</div>
			</div>
			<div class="clear"></div>
		</div>
	</fieldset>
</dsp:page>