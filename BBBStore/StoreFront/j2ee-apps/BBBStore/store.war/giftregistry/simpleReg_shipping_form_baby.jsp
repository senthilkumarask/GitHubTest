<dsp:page>
    <dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
    <dsp:importbean bean="/com/bbb/selfservice/StateDroplet" /> 
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/RegistryAddressOrderDroplet"/>
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/POBoxValidateDroplet" />
    <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
       <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
    <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
 <dsp:getvalueof bean="SessionBean.registryTypesEvent" id="event"/>
    <c:set var="BedBathCanadaSiteCode">
        <bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
    </c:set>

    <c:choose>
        <c:when test="${currentSiteId == BedBathCanadaSiteCode}">
            <c:set var="CADate" value="CA" scope="request" />
        </c:when>
        <c:otherwise>
            <c:set var="CADate" value="" scope="request" />
        </c:otherwise>
    </c:choose>
        
        <div id="" class="grid_8 alpha omega clearfix">
          <div id="" class="grid_4 alpha omega stepInformation form clearfix">
                <div class="grid_4 alpha">
                    <div class="entry pushDown">
                        <div class="field"><bbbl:label key="lbl_shipping_current_addr" language ="${pageContext.request.locale.language}"/></div>
                        <div class="value" id="valRegistryCurrentShippingAddress"></div>
                    </div>
                </div>
               
                <div class="clear"></div>
            </div>
        <dsp:getvalueof var="inputListMap" param="inputListMap"/>
          <div class="grid_8 center flatform">
                  <div class="grid_4 alpha omega marRight_20">
                    <c:set var="tabIndex" value="1" /> 
                      <%-- Radio Buttons for Shipping Addresses : Address book--%>
                      <c:if test="${inputListMap['showContactAddress'].isDisplayonForm || inputListMap['showShippingAddress'].isDisplayonForm}">
                        <%@ include file="frags/simpleReg_registrant_address_book_baby.jsp" %>
                      </c:if>
					  <c:if test="${inputListMap['showFutureShippingAddr'].isDisplayonForm || inputListMap['futureShippingDate'].isDisplayonForm}">
					  <%@ include file="frags/simpleReg_future_shipping_baby.jsp" %>
					</c:if>
                </div>
           
				<c:if test="${inputListMap['PhoneNumber'].isDisplayonForm}">
				  <c:set var="isRequired" value=""></c:set> 
          <c:if test="${inputListMap['PhoneNumber'].isMandatoryOnCreate}">
				  <c:set var="isRequired" value="required"></c:set>
				</c:if>
					<div class="grid_2 noMarLeft marRight_20">
						<div class="inputPhone clearfix phoneFieldWrap"
							aria-live="assertive">
							<fieldset class="phoneFields">
								<legend class="phoneFieldLegend">
									<bbbl:label key='lbl_phonefield_phnumber'
										language='${pageContext.request.locale.language}' />
								</legend>
								<input type="hidden" id="requiredBccPhn" value="${inputListMap['PhoneNumber'].isMandatoryOnCreate}">
								<label id="lbltxtRegistrantPrimaryPhone"
									for="txtregistrantPhoneNumberMask"><bbbl:label key='lbl_reg_phone'
										language='${pageContext.request.locale.language}' /></label>
										
								<dsp:getvalueof var="primaryPhoneFromBean" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.primaryPhone"/>
								<c:choose>
								<c:when test="${not empty primaryPhoneFromBean}">
									<dsp:getvalueof var="phn" value="${primaryPhoneFromBean}"/>
								</c:when>
								<c:otherwise>
									<dsp:getvalueof var="phn" bean="Profile.PhoneNumber"/>
								</c:otherwise>
								</c:choose>
								
									<c:choose>
								<c:when test="${isTransient}"> 
								<dsp:input id="txtregistrantPhoneNumber"
									bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.primaryPhone"
									type="hidden" name="phone" 
									iclass="${isRequired} phone phoneField widthHundred ${focusPHClass}" />
									<input id="txtregistrantPhoneNumberMask" name="phoneNumberMask" class="${isRequired} phone phoneField widthHundred phoneNumberMask form-control bfh-phone escapeHTMLTag valid" type="tel" aria-labelledby="errortxtregistrantPhoneNumber" placeholder="<bbbl:label key='lbl_reg_ph_phone'
										language='${pageContext.request.locale.language}' />" value="${phn}" data-format="(ddd) ddd-dddd">
									
									
									</c:when>
									<c:otherwise>
									<dsp:input id="txtregistrantPhoneNumber"
									bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.primaryPhone"
									type="hidden" name="phone" maxlength="12"
									iclass="${isRequired} phone phoneField widthHundred ${focusPHClass}" beanvalue="Profile.PhoneNumber"/>
									<input id="txtregistrantPhoneNumberMask" name="phoneNumberMask" class="${isRequired} phone phoneField widthHundred phoneNumberMask form-control bfh-phone escapeHTMLTag valid" type="tel" aria-labelledby="errortxtregistrantPhoneNumber" placeholder="<bbbl:label key='lbl_reg_ph_phone'
										language='${pageContext.request.locale.language}' />" value="${phn}" data-format="(ddd) ddd-dddd">
									</c:otherwise>
									</c:choose>
								<div class="cb marTop_5">
									<label id="errorlbltxtRegistrantPrimaryPhone"
										for="txtRegistrantPrimaryPhone" class="error" generated="true"></label>

								</div>

							</fieldset>
						</div>
					</div>
				</c:if>
				<c:if test="${inputListMap['MobileNumber'].isDisplayonForm}">
				 <c:set var="isRequired" value=""></c:set> 
													 <c:if test="${inputListMap['MobileNumber'].isMandatoryOnCreate}">
													 <c:set var="isRequired" value="required"></c:set>
													 </c:if>
        <div class="grid_2 alpha omega">
                        <div class="inputPhone clearfix phoneFieldWrap" aria-live="assertive">
                            <fieldset class="phoneFields">
                                <legend class="phoneFieldLegend">
                                    <bbbl:label key='lbl_phonefield_phnumber'
                                        language='${pageContext.request.locale.language}' />
                                </legend>
                          		<input type="hidden" id="requiredBccMob" value="${inputListMap['MobileNumber'].isMandatoryOnCreate}">
								<label id="lbltxtRegistrantMobilePhone" for="txtRegistrantMobileNumberMask"><bbbl:label key="lbl_reg_cell_no" language ="${pageContext.request.locale.language}"/></label>
								
								<dsp:getvalueof var="cellPhoneFromBean" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.cellPhone"/>
								<c:choose>
								<c:when test="${not empty cellPhoneFromBean}">
									<dsp:getvalueof var="cellPhn" value="${cellPhoneFromBean}"/>
								</c:when>
								<c:otherwise>
									<dsp:getvalueof var="cellPhn" bean="Profile.mobileNumber"/>
								</c:otherwise>
								</c:choose>
								
                                <dsp:input id="txtRegistrantMobileNumber"
                                    bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.cellPhone"
                                    type="hidden" 
                                    iclass="${isRequired} phone phoneField widthHundred phoneNumberMask ${focusPHClass}" />
                                    
                                <input id="txtRegistrantMobileNumberMask" name="mobileNumberMask" value="${cellPhn}" class="${isRequired} widthHundred form-control bfh-phone escapeHTMLTag" type="tel" aria-labelledby="errortxtRegistrantMobileNumber" placeholder="<bbbl:label key='lbl_reg_ph_phone'
                                        language='${pageContext.request.locale.language}' />" data-format="(ddd) ddd-dddd">


                            </fieldset>
                        </div>
                    </div>
                    </c:if>
				
	</div>
     </div>
</dsp:page>