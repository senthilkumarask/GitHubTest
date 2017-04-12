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
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/ProfileExistCheckDroplet" />
	<dsp:importbean bean="/atg/multisite/Site"/>
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryVODroplet" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/POBoxValidateDroplet" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/RegistryAddressOrderDroplet"/>
    <dsp:importbean bean="/com/bbb/selfservice/StateDroplet" /> 
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/SimplifyRegFormHandler" />
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
     <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>	
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <c:set var="lbl_login_frag__forgot_password"> 
     <bbbl:label key="lbl_login_frag__forgot_password" language="${pageContext.request.locale.language}" />
     </c:set>
   
   
   <div class="grid_6 fr flatform" id="signInWrap">
		<div class="grid_6 alpha omega fl">
				<span class="txtOffScreen" aria-live="polite" aria-atomic="true" id="readLabel"></span>
				<dsp:getvalueof param="showLegacyPwdPopup" id="showLegacyPwdPopup"/>
				<dsp:getvalueof param="showMigratedPopup" id="showMigratedPopup"/>

				<div class="grid_3 alpha noMarTop clearfix">
					<%-- Fix For Defect BBBSL-3322 --%>	
					<div class="input_wrap grid_3" aria-live="assertive">
						<dsp:input tabindex="2" id="email" bean="SimplifyRegFormHandler.email" type="text" iclass="fbConnectEmail grid_3">						
							<dsp:tagAttribute name="aria-describedby" value="readLabel"/>
						</dsp:input>
             <dsp:droplet name="Switch">
             <dsp:param name="value" bean="SimplifyRegFormHandler.formError"/>
        <dsp:oparam name="true">
        <dsp:getvalueof bean="SimplifyRegFormHandler.formError" var="formError"/>
         
	                    <c:if test="${formError == true}">
	                            <dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
	                                <dsp:param param="SimplifyRegFormHandler.formExceptions" name="exceptions"/>
	                                <dsp:oparam name="output">
	                                        <dsp:getvalueof param="message"  var="errorMsg"/>
	                                      <p> User already exist.</p>
	                                </dsp:oparam>
	                            </dsp:droplet>
	                    </c:if>
	                    
	                    </dsp:oparam>
	<dsp:oparam name="false">								
		</dsp:oparam>
	 </dsp:droplet>
					</div>
				</div>
				<div class="formRow grid_3 clearfix noMarTop">
					<div class="input_wrap grid_3">
						<!--<label id="lblpassword" class="textLgray12 block" for="password"><bbbl:label key="lbl_login_frag__password" language="${pageContext.request.locale.language}" /> <span class="visuallyhidden"><bbbl:label key="lbl_for_existing_customers" language="${pageContext.request.locale.language}"/></span></label>-->
						<div class="passwordWrap grid_3 clearfix">
							<dsp:input tabindex="3" id="password" bean="SimplifyRegFormHandler.password" type="password" autocomplete="off" iclass="showpassLoginFrag grid_3">
								<dsp:tagAttribute name="aria-required" value="false"/>
								<dsp:tagAttribute name="aria-describedby" value="readLabel"/>
							</dsp:input>
								<div class="showPassDiv grid_3 clearfix">
		                   <input name="showPassword" type="checkbox" value="" data-toggle-class="showpassLoginFrag" class="showPassword" id="showPassword" />
						<label for="showPassword" class="lblShowPassword">show</label>
		               </div>
						</div>
						<div class="forgotPasswordWrap grid_3">
							<dsp:a tabindex="6" iclass="forgotPassword grid_3" page="/account/frags/forgot_password.jsp?omnitureForCheckout=${omnitureForCheckout}" title="${lbl_login_frag__forgot_password}" onclick="javascript:internalLinks('Forgot Password : Forgot Password Link');"><bbbl:label key="lbl_login_frag__forgot_password" language="${pageContext.request.locale.language}" /></dsp:a>
						</div>
					</div>
				</div>	
						 <div class="button hidden">
                         <dsp:input id="checkUser" type="submit" value="checkUser" bean="SimplifyRegFormHandler.verifyUser" iclass="btnAddtocart button-Med enableOnDOMReady" />
						</div>
		</div>
	</div>
	
</dsp:page>