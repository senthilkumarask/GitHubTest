<dsp:page>
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <dsp:importbean bean="/com/bbb/account/droplet/BBBConfigKeysDroplet" />
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
    <dsp:importbean bean="/atg/userprofiling/Profile"/>
    <dsp:include page="/_includes/third_party_on_of_tags.jsp"/>
    <dsp:getvalueof var="step" param="step"/>
    <dsp:getvalueof var="link" param="link"/>
    <dsp:getvalueof var="isTransient" bean="Profile.transient"/>
    <dsp:getvalueof id="siteId" idtype="java.lang.String" param="siteId" />
    
     <c:choose>
            <c:when test="${siteId == 'BedBathCanada'}">
                <c:set var="CADate" value="CA" scope="request" />
            </c:when>
            <c:otherwise>
                <c:set var="CADate" value="" scope="request" />
            </c:otherwise>
        </c:choose>

                
                    <div id="" class="grid_4 alpha omega clearfix">
                            <div class="simpleRegForm">
                  <dsp:getvalueof var="inputListMap" param="inputListMap"/>
                                          
                         <c:if test="${inputListMap['showerDate'].isDisplayonForm}">
                                       <c:set var="isRequired" value=""></c:set> 
													 <c:if test="${inputListMap['showerDate'].isMandatoryOnCreate}">
													 <c:set var="isRequired" value="required"></c:set>
													 </c:if>
													  <c:set var="datePlaceHolder"><bbbl:label key='lbl_reg_ph_date' language ='${pageContext.request.locale.language}'/></c:set>
                                <div id="eventflex1" class="inputField clearfix grid_2 alpha omega" id="eventDate" >
                                    <div class="label">
                                        <label id="lbltxtRegistryExpectedDateShower${CADate}" for="txtRegistryShowerDate${CADate}"><bbbl:label key='lbl_reg_shower_date'
										language='${pageContext.request.locale.language}' /></label>
                           
                                    </div>
                                    <div class="text">
                                        <div class="grid_2 alpha omega" aria-live="assertive">
										<c:choose>
											<c:when test="${not empty regVO.event.showerDate}">
											<dsp:input bean="GiftRegistryFormHandler.registryVO.event.showerDate"
                                                type="text" id="txtRegistryShowerDate${CADate}"  name="simpletxtShowerDateUS${CADate}"  iclass="${isRequired} step1FocusField" value="${regVO.event.showerDate}">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryExpectedDateShower${CADate} errortxtRegistryExpectedDate${CADate}"/>
                                            </dsp:input>
											</c:when>
											<c:otherwise>
											<dsp:input bean="GiftRegistryFormHandler.registryVO.event.showerDate"
                                                type="text" id="txtRegistryShowerDate${CADate}"  name="simpletxtShowerDate${CADate}"  iclass="${isRequired} step1FocusField">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryExpectedDateShower${CADate} errortxtRegistryExpectedDate${CADate}"/>
                                                <dsp:tagAttribute name="placeholder" value="${datePlaceHolder}"/>
                                            </dsp:input>
											</c:otherwise>
											</c:choose>
                                              <div id="txtRegistryShowerDateButton${CADate}" class="calendericon">
                                              <span class="icon-fallback-text"><span class="icon-calendar" aria-hidden="true"></span><span class="icon-text"></span></span>
                                        </div>
                                    </div>
                                </div>
                                </div>
                               
                            </c:if> 
                                  <c:if test="${inputListMap['MobileNumber'].isDisplayonForm}">
				 <c:set var="isRequired" value=""></c:set> 
													 <c:if test="${inputListMap['MobileNumber'].isMandatoryOnCreate}">
													 <c:set var="isRequired" value="required"></c:set>
													 </c:if>
					<div class="grid_2 alpha omega">
						<div class="inputPhone clearfix phoneFieldWrap"
							aria-live="assertive">
							<fieldset class="phoneFields">
								<legend class="phoneFieldLegend">
									<bbbl:label key='lbl_phonefield_phnumber'
										language='${pageContext.request.locale.language}' />
								</legend>
								<input type="hidden" id="requiredBccMob" value="${inputListMap['MobileNumber'].isMandatoryOnCreate}">
								<label id="lbltxtRegistrantMobilePhone"
									for="txtRegistrantMobileNumberMask"><bbbl:label key='lbl_reg_MobilePhone'
										language='${pageContext.request.locale.language}' /></label>
								
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
										<input id="txtRegistrantMobileNumberMask" value="${cellPhn}" name="mobileNumberMask" class="widthHundred form-control bfh-phone escapeHTMLTag ${isRequired}" type="tel" data-format="(ddd) ddd-dddd" aria-labelledby="errortxtRegistrantMobileNumber" placeholder="<bbbl:label key="lbl_reg_ph_phone" language ="${pageContext.request.locale.language}"/>">

							</fieldset>
						</div>
					</div>
				</c:if>
                            </div>
                    </div>
                 
</dsp:page>