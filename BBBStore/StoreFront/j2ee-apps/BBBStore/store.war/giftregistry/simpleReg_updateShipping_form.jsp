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
        
		<c:choose>
            <c:when test="${event == 'BA1' }">
				<div id="" class="grid_10 alpha omega clearfix marLeft_20" style="float: right;">
				<div class="grid_10 center flatform">
				<div class="grid_4 alpha">
            </c:when>
            <c:otherwise>
				<div id="" class="grid_6 alpha omega clearfix marLeft_20" style="float: right;">
				<div class="grid_6 center flatform">
				<div class="grid_4 alpha omega marRight_20">
            </c:otherwise>
        </c:choose>
          <%--<div id="" class="grid_6 alpha omega stepInformation form clearfix">
                <div class="grid_4 alpha">
                    <div class="entry pushDown">
                        <div class="field"><bbbl:label key="lbl_shipping_current_addr" language ="${pageContext.request.locale.language}"/></div>
                        <div class="value" id="valRegistryCurrentShippingAddress"></div>
                    </div>
                </div>
               
                <div class="clear"></div>
            </div>--%>
        <dsp:getvalueof var="inputListMap" param="inputListMap"/>
                                <c:set var="tabIndex" value="1" /> 
                                <%-- Radio Buttons for Shipping Addresses : Address book--%>
                                <%@ include file="frags/simpleRegUpdate_registrant_address_book.jsp" %>
								<%@ include file="frags/simpleRegUpdate_future_shipping.jsp" %>
                  </div>

				  <c:choose>
				  <c:when test="${event == 'BA1' }">
					<div class="grid_2 omega alpha marLeft_10 marRight_20">
                  </c:when>
                  <c:otherwise>
					<div class="grid_2 omega alpha">
                  </c:otherwise>
                  </c:choose>
				  
						<div class="inputPhone clearfix phoneFieldWrap"
							aria-live="assertive">
							<fieldset class="phoneFields">
								<legend class="phoneFieldLegend">
									<bbbl:label key='lbl_phonefield_phnumber'
										language='${pageContext.request.locale.language}' />
								</legend>
								<label id="lbltxtRegistrantPrimaryPhone"
									for="txtregistrantPhoneNumberMask"><bbbl:label key='lbl_reg_phone'
										language='${pageContext.request.locale.language}' /></label>
								<c:choose>
								<c:when test="${not empty regVO.primaryRegistrant.primaryPhone}">
									<dsp:getvalueof var="phn" value="${regVO.primaryRegistrant.primaryPhone}"/>
								</c:when>
								<c:otherwise>
									<dsp:getvalueof var="phn" bean="Profile.PhoneNumber"/>
								</c:otherwise>
								</c:choose>
									<c:choose>
								<c:when test="${isTransient}"> 
								<dsp:input tabindex="6" id="txtregistrantPhoneNumber"
									bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.primaryPhone"
									type="hidden" name="phone" 
									iclass="isMandatoryField phone phoneField widthHundred ${focusPHClass}" />
									<input id="txtregistrantPhoneNumberMask" name="phoneNumberMask" tabindex="6" class="isMandatoryField phone phoneField widthHundred phoneNumberMask form-control bfh-phone escapeHTMLTag valid" type="tel" aria-labelledby="errortxtregistrantPhoneNumber" placeholder="<bbbl:label key='lbl_reg_ph_phone'
										language='${pageContext.request.locale.language}' />" data-format="(ddd) ddd-dddd">
									
									
									</c:when>
									<c:otherwise>
									<dsp:input tabindex="6" id="txtregistrantPhoneNumber"
									bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.primaryPhone"
									type="hidden" name="phone" maxlength="12"
									iclass="isMandatoryField phone phoneField widthHundred ${focusPHClass}" value="${phn}"/>
									<input id="txtregistrantPhoneNumberMask" name="phoneNumberMask" tabindex="6" class="isMandatoryField phone phoneField widthHundred phoneNumberMask form-control bfh-phone scapeHTMLTag valid" type="tel" aria-labelledby="errortxtregistrantPhoneNumber" placeholder="<bbbl:label key='lbl_reg_ph_phone'
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
					
					<c:if test="${event == 'BA1'}">
					<c:set var="chngLblForBabyReg" value="chngLblForBabyReg" />
					<div class="grid_2 alpha omega marRight_20">
						<div class="inputPhone clearfix phoneFieldWrap"
							aria-live="assertive">
							<fieldset class="phoneFields">
								<legend class="phoneFieldLegend">
									<bbbl:label key='lbl_phonefield_phnumber'
										language='${pageContext.request.locale.language}' />
								</legend>
								<label class="${chngLblForBabyReg}" id="lbltxtRegistrantMobilePhone"
									for="txtRegistrantMobileNumberMask"><bbbl:label key='lbl_reg_MobilePhone'
										language='${pageContext.request.locale.language}' /></label>
									
								<c:choose>
									<c:when test="${not empty regVO.primaryRegistrant.cellPhone}">
										<dsp:getvalueof var="cellPhn" value="${regVO.primaryRegistrant.cellPhone}"/>
									</c:when>
									<c:otherwise>
										<dsp:getvalueof var="cellPhn" bean="Profile.mobileNumber"/>
									</c:otherwise>
								</c:choose>
								
								<dsp:input tabindex="6" id="txtRegistrantMobileNumber"
									bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.cellPhone"
									type="hidden" value="${cellPhn}" iclass="phone phoneField widthHundred phoneNumberMask ${focusPHClass}"/>
									<input id="txtRegistrantMobileNumberMask" name="mobileNumberMask" value="${cellPhn}" tabindex="6" class="widthHundred form-control bfh-phone escapeHTMLTag" type="tel" aria-labelledby="errortxtRegistrantMobileNumber" placeholder="<bbbl:label key='lbl_reg_ph_phone' language='${pageContext.request.locale.language}' />" data-format="(ddd) ddd-dddd">

							</fieldset>
						</div>
					</div>
					
					<div id="" class="grid_2 alpha omega form clearfix nurseryDecor">
                   			<div class="grid_2 alpha clearfix">
                         		<div class="grid_3 alpha omega clearfix">
	                            	<label for="babyNurserytheme" class="formTitle marBottom_5"><bbbl:label key="lbl_nursery_decor_theme" language ="${pageContext.request.locale.language}"/></label>
	                        	</div>

								<c:choose>
								<c:when test="${not empty regVO.event.babyNurseryTheme}">
									<dsp:getvalueof var="babyNursTh" value="${regVO.event.babyNurseryTheme}"/>
								</c:when>
								<c:otherwise>
									<dsp:getvalueof var="babyNursTh" value=""/>
								</c:otherwise>
								</c:choose>
								<c:set var="reg_lbl_ph_nursery_decor_theme"><bbbl:label key="reg_lbl_ph_nursery_decor_theme" language ="${pageContext.request.locale.language}"/></c:set>
                            	<dsp:input id="babyNurserytheme" maxlength="30" name="babyNurserythemeName" iclass="cannotStartWithWhiteSpace escapeHTMLTag widthHundred" type="text" autocomplete="off" bean="GiftRegistryFormHandler.registryVO.event.babyNurseryTheme" value="${babyNursTh}">
                			        <dsp:tagAttribute name="aria-labelledby" value="babyNurserytheme" />
                			         <dsp:tagAttribute name="placeholder" value="${reg_lbl_ph_nursery_decor_theme}" />
                			         <dsp:tagAttribute name="aria-required" value="true" />
                			</dsp:input>
                			</div>
					</div>
					
					</c:if>
					
                    
	</div>
     </div>
</dsp:page>