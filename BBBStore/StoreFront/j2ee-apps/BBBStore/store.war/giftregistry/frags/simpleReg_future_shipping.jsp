<div class="grid_4 center flatform">
<div class="grid_4 alpha omega">
<div class="simpleRegForm">
<dsp:getvalueof var="isTransient" bean="Profile.transient"/>
<c:set var="futureShippingAddr" value=""/>

<dsp:getvalueof var="fshipfirstNameFromBean" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.firstName"/>
<dsp:getvalueof var="fshiplastNameFromBean" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.lastName"/>
<dsp:getvalueof var="fshipAddrLineOneFromBean" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.addressLine1"/>
<dsp:getvalueof var="fshipAddrLineTwoFromBean" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.addressLine2"/>
<dsp:getvalueof var="fshipCityFromBean" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.city"/>
<dsp:getvalueof var="fshipStateFromBean" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.state"/>
<dsp:getvalueof var="fshipZipFromBean" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.zip"/>

<c:if test="${fshipAddrLineOneFromBean ne '' && fshipAddrLineOneFromBean ne null}">
	<c:set var="futureShippingAddr">
			${fshipAddrLineOneFromBean}, ${fshipCityFromBean}, ${fshipStateFromBean}, ${fshipZipFromBean}
	</c:set>
</c:if>

    <fieldset class="radioGroup">
        
                <div class="checkboxItem inputField clearfix movingAdd">
							<div class="checkbox">
							<dsp:getvalueof var="futureShippingDateSelected" bean="GiftRegistryFormHandler.futureShippingDateSelected"/>
							 <dsp:getvalueof var="formExceptions" bean="GiftRegistryFormHandler.formExceptions"/>
							<c:choose>
							<c:when test="${not empty formExceptions && futureShippingDateSelected ne ''}">
										<dsp:input type="checkbox" name="futureShippingDateSelectedCheckBox" id="radRegistryMovingAddress"
											bean="GiftRegistryFormHandler.futureShippingDateSelected" checked="${futureShippingDateSelected}">
	                                <dsp:tagAttribute name="aria-hidden" value="false"/>
	                                
	                           </dsp:input>
	                           <%-- Added to unhide the shipping address text box if the value is from newShippingaddress from bean --%>
	                           <input type="hidden" id="futureAddChecked" value="${futureShippingDateSelected}">
	                           </c:when>
	                           <c:otherwise>
	                           <dsp:input type="checkbox" name="futureShippingDateSelectedCheckBox" id="radRegistryMovingAddress"
											bean="GiftRegistryFormHandler.futureShippingDateSelected" checked="false">
	                                <dsp:tagAttribute name="aria-hidden" value="false"/>
	                                
	                           </dsp:input>
	                           </c:otherwise>
	                           </c:choose>
							</div>
							<div class="label">
								<label id="lblradRegistryMovingAddress" for="radRegistryMovingAddress" class="addChkboxTxt">
									 <bbbl:label key='lbl_reg_moving_store' language ='${pageContext.request.locale.language}'/>
								</label>
							</div>
                           
						</div>
                        
                        <!-- Moving address -->
                         <div class="grid_4 alpha omega  form clearfix hidden marTop_10" id="movingaddres">
                        <div class="inputField clearfix grid_2 alpha omega" id="eventDate" >
                                    <div class="label">
                                 <c:choose>
                				<c:when test="${event == 'BA1' }">
                				<label id="lbltxtMovingAddDate${CADate}" for="txtMovingAddDate${CADate}">arrival date</label>
                			 </c:when>
					         <c:otherwise>
                                        <label id="lbltxtMovingAddDate${CADate}" for="txtMovingAddDate${CADate}"><!--bbbl:label key="lbl_event_eventdate" language ="${pageContext.request.locale.language}"/ --> future date</label>
                              </c:otherwise>
                                      </c:choose>
                                    </div>
                                    
                                     
                                <c:set var="isRequired" value=""></c:set> 
													 <c:if test="${inputListMap['futureShippingDate'].isMandatoryOnCreate}">
													 <c:set var="isRequired" value="required"></c:set>
													 </c:if>
													 <c:set var="futuredate"><bbbl:label key='lbl_reg_ph_futuredate' language ='${pageContext.request.locale.language}'/></c:set>
                                    <div class="text">
                                        <div class="grid_2 alpha omega" aria-live="assertive">
                                            <dsp:input 
                                                bean="GiftRegistryFormHandler.registryVO.shipping.futureShippingDate"
                                                type="text" id="txtMovingAddDate${CADate}"  name="txtMovingAddDateName${CADate}" iclass="${isRequired} step1FocusField">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtMovingAddDate${CADate} errortxtMovingAddDate${CADate}"/>
                                                <dsp:tagAttribute name="placeholder" value="${futuredate}" />
                                            </dsp:input>
                                              <div id="MovingAddDateButton${CADate}" class="calendericon">
                                              <span class="icon-fallback-text"><span class="icon-calendar" aria-hidden="true"></span><span class="icon-text"></span></span>
                                        </div>
                                    </div>
                                </div>
                                
                                
                                </div>
                                
                                <c:set var="isRequired" value=""></c:set> 
													 <c:if test="${inputListMap['showFutureShippingAddr'].isMandatoryOnCreate}">
													 <c:set var="isRequired" value="required"></c:set>
													 </c:if>
                              <div class="inputField clearfix noMarRight" id="shippingFutureAdd" >
							  	<div class="grid_4 alpha omega">
                          <input  id="txtSimpleRegFutureShippingGoogleSuggest" type="text" name="txtSimpleRegFutureShippingGoogleSuggestName" class="${isRequired} cannotStartWithWhiteSpace QASAddress1" maxlength="90" placeholder="<bbbl:label key='lbl_reg_ph_futureaddress' language ='${pageContext.request.locale.language}'/>" value="${futureShippingAddr}" aria-labelledby="lblSimpleRegFutureShippingGoogleSuggest lblErrorSimpleRegFutureShippingGoogleSuggest">
                          <label class="txtOffScreen" for="txtSimpleRegFutureShippingGoogleSuggest" id="lblSimpleRegFutureShippingGoogleSuggest"><bbbl:label key='lbl_reg_ph_futureaddress' language ='${pageContext.request.locale.language}'/></label>
						  <label for="txtSimpleRegFutureShippingGoogleSuggest" generated="true" class="contactValidAddMsg error errorLabel" id="lblErrorSimpleRegFutureShippingGoogleSuggest">
					</label>
					<input type="hidden" class='addressHidden' name="txtSimpleRegFutureShippingGoogleSuggestHidden" id="txtSimpleRegFutureShippingGoogleSuggestHidden" value="${futureShippingAddr}">
                          
		                          <c:if test="${! isTransient}">
		                            <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.firstName" beanvalue="Profile.firstName"/>
										<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.lastName" beanvalue="Profile.lastName"/>
		                          </c:if>
		                        <dsp:input type="hidden" bean="GiftRegistryFormHandler.futureShippingAddress" id="newFutureShippingAddress"/>
                    			<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.firstName" value="${fshipfirstNameFromBean}" id="futureFirstName" iclass="QASFirstName"/>
								<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.lastName" value="${fshiplastNameFromBean}" id="futureLastName" iclass="QASLastName"/>
                                <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.addressLine1" id="txtSimpleRegFutureShippingAddress1" iclass="address1"/>
                                <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.addressLine2" id="txtSimpleRegFutureShippingAddress2" iclass="address2"/>
                                <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.city" id="txtSimpleRegFutureShippingCity" iclass="QASCity"/>
                                <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.state" id="txtSimpleRegFutureShippingState"  iclass="QASState"/>
                                <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.zip" id="txtSimpleRegFutureShippingZip" iclass="QASZip"/>
								<input type="hidden" id="txtSimpleRegFutureShippingCountryName"  value="" class="countryName"/>
 								<input type="hidden" id="PoBoxFlag"  value="" class="PoBoxFlag"/>
 								<input type="hidden" id="PoBoxStatus"  value="" class="PoBoxStatus"/>
								<input type="hidden" id="txtRegistryFutureShippingBtn"/>
								</div>
								<div class="grid_2 alpha omega marTop_15">
								<label class="txtOffScreen" id="lbltxtRegistryFutureShippingAddress2" for="txtSimpleRegFutureShippingGoogleSuggestApt">${lbl_reg_ph_apt_building}</label>
                 <input id="txtSimpleRegFutureShippingGoogleSuggestApt" maxlength="30" name="txtRegistryCurrentShippingAddress2Name" type="text" autocomplete="off"  class="cannotStartWithWhiteSpace ApartmentNo escapeHTMLTag" placeholder="Apt/Bldg (optional)" aria-labelledby="lbltxtRegistryFutureShippingAddress2" data-previousapt=""> 
								</div>
                    			</div>
                    			
                    </div>
                        <!-- End Moving address -->
        </div>
    </fieldset>
</div>
</div>

 