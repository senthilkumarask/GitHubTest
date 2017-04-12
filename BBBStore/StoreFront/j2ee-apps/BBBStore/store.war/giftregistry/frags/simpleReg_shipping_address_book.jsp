<%-- Code from Shipping Addresses -List Address Book --%>
<div class="grid_4 center flatform">
<div class="grid_4 alpha omega">
<div class="simpleRegForm">
    <fieldset class="radioGroup">

        <%-- <legend><bbbl:label key="lbl_shipping_current_addr" language ="${pageContext.request.locale.language}"/></legend> --%>
        <%-- Disaplay same as registrant--%>
        
        
        <%--  Variable to find count of profile addresses --%>
        <c:set var="addressBookSize" value="0" />
        
        <%--  Variable to find count of pobox addresses --%>
        <c:set var="poBoxShipAddrCount" value="0" />
        
        <%--  Flag to check/selected radio --%>
        <c:set var="isShipAddressRadioChecked" value="true" scope="page" />
        
                <div class="checkboxItem inputField clearfix movingAdd">
							<div class="checkbox">
										<dsp:input type="checkbox" name="radRegistryCurrentShippingAddressName" id="radRegistryMovingAddress"
											bean="GiftRegistryFormHandler.shippingAddress" value="shipAdrressSameAsRegistrant" checked="false">
	                                <dsp:tagAttribute name="aria-checked" value="false"/>
	                                <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryCurrentShippingAddress"/>
	                           </dsp:input>
							</div>
							<div class="label">
								<label id="lblradRegistryMovingAddress" for="lblradRegistryMovingAddress" class="addChkboxTxt">
								<bbbl:label key="lbl_reg_moving_store" language="${pageContext.request.locale.language}"/>
								</label>
								<input type="hidden" name="zipvalue" class="favStoreZip" value="" />
							</div>
                           
						</div>
                        
                        <!-- Moving address -->
                         <div class="grid_4 alpha omega  form clearfix hidden marTop_10" id="movingaddres">
                        <div class="inputField clearfix grid_2 alpha omega" id="eventDate" >
                                    <div class="label">
                                 <c:choose>
                				<c:when test="${event == 'BA1' }">
                				<label id="lbltxtMovingAddDate${CADate}" for="txtMovingAddDate${CADate}"><bbbl:label key="lbl_arrival_date" language="${pageContext.request.locale.language}"/></label>
                			 </c:when>
					         <c:otherwise>
                                        <label id="lbltxtMovingAddDate${CADate}" for="txtMovingAddDate${CADate}"><!--bbbl:label key="lbl_event_eventdate" language ="${pageContext.request.locale.language}"/ --> <bbbl:label key="lbl_reg_ph_futuredate" language="${pageContext.request.locale.language}"/></label>
                              </c:otherwise>
                                      </c:choose>
                                    </div>
                                    <div class="text">
                                        <div class="grid_2 alpha omega" aria-live="assertive">
                                            <dsp:input tabindex="1" 
                                                bean="GiftRegistryFormHandler.registryVO.shipping.futureShippingDate"
                                                type="text" id="txtMovingAddDate${CADate}"  name="txtMovingAddDateName" value="${regVO.event.eventDate}" iclass="${required} step1FocusField">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtMovingAddDate${CADate} errortxtMovingAddDate${CADate}"/>
                                            </dsp:input>
                                              <div id="MovingAddDateButton">
                                              <span class="icon-fallback-text"><span class="icon-calendar" aria-hidden="true"></span><span class="icon-text"></span></span>
                                        </div>
                                    </div>
                                </div>
                                
                                </div>
                              <div class="inputField clearfix" >
                                <dsp:input tabindex="${tabIndex}" id="txtRegistryMovingAddress1"
			                        type="text"
			                        name="txtRegistryMovingAddress1Name"
			                        bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine1" iclass="${iclassValue} required addressAlphanumericbasicpunc cannotStartWithWhiteSpace QASAddress1 widthHundred" maxlength="30" >
			                        <dsp:tagAttribute name="aria-required" value="true"/>
			                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryMovingAddress1 errortxtRegistryMovingAddress1"/>
		                    	</dsp:input>
                    			</div>
                    </div>
                        <!-- End Moving address -->
                        
                <div class="inputField clearfix hidden" id="shippingNewAddrLine2">
                    <label id="lbltxtRegistryCurrentShippingAddress2" for="txtRegistryCurrentShippingAddress2">
                        <bbbl:label key="lbl_registrants_addr_line2" language ="${pageContext.request.locale.language}"/>
                    </label>
                    <dsp:input id="txtRegistryCurrentShippingAddress2"
                        type="text"
                        name="txtRegistryCurrentShippingAddress2Name"
                        bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine2" iclass="${iclassValue} addressAlphanumericbasicpunc cannotStartWithWhiteSpace QASAddress2" maxlength="30" >
                        <dsp:tagAttribute name="aria-required" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryCurrentShippingAddress2 errortxtRegistryCurrentShippingAddress2"/>
                    </dsp:input>
                    <c:set var="tabIndex" value="${tabIndex + 1}" /> 
                </div>

                <div class="inputField clearfix hidden" id="shippingNewAddrFirstCity">
                    <label id="lbltxtRegistryCurrentShippingCity" for="txtRegistryCurrentShippingCity"> 
                        <bbbl:label key="lbl_registrants_city" language ="${pageContext.request.locale.language}"/>
                        <span class="required">*</span>
                    </label>
                    <dsp:input tabindex="${tabIndex}" id="txtRegistryCurrentShippingCity"
                        type="text" name="txtRegistryCurrentShippingCityName"
                        bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.city" iclass="required cannotStartWithWhiteSpace QASCityName" maxlength="25" >
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryCurrentShippingCity errortxtRegistryCurrentShippingCity"/>
                    </dsp:input>
                    <c:set var="tabIndex" value="${tabIndex + 1}" /> 
                </div>

                <div class="inputSelect pushDown hidden">
                    <label id="lblselRegistryCurrentShippingState" for="selRegistryCurrentShippingState">
                        <dsp:getvalueof id="siteId" idtype="java.lang.String" param="siteId" />
                        <c:choose>
                            <c:when test="${siteId == BedBathCanadaSiteCode}">
                                <bbbl:label key="lbl_registrants_statecanada" language ="${pageContext.request.locale.language}"/>
                            </c:when>
                            <c:otherwise>
                                <bbbl:label key="lbl_registrants_state" language ="${pageContext.request.locale.language}"/>
                            </c:otherwise>
                        </c:choose>
                        <span class="required">*</span>
                    </label>
                    <dsp:select tabindex="${tabIndex}" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.state" name="selRegistryCurrentShippingStateName" id="selRegistryCurrentShippingState" iclass="uniform requiredValidation QASStateName required">
                    <dsp:tagAttribute name="aria-required" value="true"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lblselRegistryCurrentShippingState errorselRegistryCurrentShippingState"/>
                        <dsp:droplet name="StateDroplet">
                        <dsp:param name="NoShowUSTerr" value="noShowOnRegistry" />
                            <dsp:oparam name="output">
                                <c:choose>
                                    <c:when test="${siteId == BedBathCanadaSiteCode}">
                                        <dsp:option value=""><bbbl:label key='lbl_regcreate_reginfo_selectcanada' language="${pageContext.request.locale.language}" /></dsp:option>
                                    </c:when>
                                    <c:otherwise>
                                        <dsp:option value=""><bbbl:label key='lbl_regcreate_reginfo_select' language="${pageContext.request.locale.language}" /></dsp:option>
                                    </c:otherwise>
                                </c:choose>
                                <dsp:droplet name="ForEach">
                                    <dsp:param name="array" param="location" />
                                    <dsp:oparam name="output">
                                        <dsp:getvalueof param="element.stateName" id="elementVal">
                                        <dsp:getvalueof param="element.stateCode" id="stateCode"/>
                                        <dsp:option value="${stateCode}">
                                            ${elementVal}
                                        </dsp:option>
                                        </dsp:getvalueof>
                                    </dsp:oparam>
                                </dsp:droplet>
                            </dsp:oparam>
                        </dsp:droplet>
                    </dsp:select>
                    <c:set var="tabIndex" value="${tabIndex + 1}" /> 
                    <label id="errorselRegistryCurrentShippingState" for="selRegistryCurrentShippingState" generated="true" class="error" style="display:none;"></label>
					<label for="selRegistryCurrentShippingState" class="offScreen">
                        <dsp:getvalueof id="siteId" idtype="java.lang.String" param="siteId" />
                        <c:choose>
                            <c:when test="${siteId == BedBathCanadaSiteCode}">
                                <bbbl:label key="lbl_registrants_statecanada" language ="${pageContext.request.locale.language}"/>
                            </c:when>
                            <c:otherwise>
                                <bbbl:label key="lbl_registrants_state" language ="${pageContext.request.locale.language}"/>
                            </c:otherwise>
                        </c:choose>
                        <span class="required">*</span>
                    </label>
                </div>

                <div class="inputField clearfix hidden" id="shippingNewAddrZip">
                    <label id="lbltxtRegistryCurrentShippingZip" for="txtRegistryCurrentShippingZip"> 
                        <c:choose>
                            <c:when test="${siteId == BedBathCanadaSiteCode}">
                                <bbbl:label key="lbl_registrants_zip_ca" language ="${pageContext.request.locale.language}"/>
                            </c:when>
                            <c:otherwise>
                                <bbbl:label key="lbl_registrants_zip" language ="${pageContext.request.locale.language}"/>
                            </c:otherwise>
                        </c:choose>
                    </label>
                    <c:choose>
                        <c:when test="${currentSiteId eq BedBathCanadaSiteCode}">
                            <dsp:input tabindex="${tabIndex}" id="txtRegistryCurrentShippingZip" type="text"
                                name="txtRegistryCurrentShippingZipName"
                                bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.zip" iclass="zipCA QASZipUS favStoreZip" >
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryCurrentShippingZip errortxtRegistryCurrentShippingZip"/>
                            </dsp:input>
                        </c:when>
                        <c:otherwise>
                            <dsp:input tabindex="${tabIndex}" id="txtRegistryCurrentShippingZip" type="text"
                                name="txtRegistryCurrentShippingZipName"
                                bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.zip" iclass="required zipUS QASZipUS favStoreZip" >
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryCurrentShippingZip errortxtRegistryCurrentShippingZip"/>
                            </dsp:input>
                        </c:otherwise>
                    </c:choose>
                    <c:set var="tabIndex" value="${tabIndex + 1}" />
                    
                    <input type="hidden" name="QASCountryName" id="QASCountryName"/>
		    		<dsp:input type="hidden" name="QASShipPoBoxFlag" bean="GiftRegistryFormHandler.shipPoBoxFlag" id="QASShipPoBoxFlag" iclass="QASPoBoxFlag" />
		    		<dsp:input type="hidden" name="QASShipPoBoxStatus" bean="GiftRegistryFormHandler.shipPoBoxStatus" id="QASShipPoBoxStatus" iclass="QASPoBoxStatus" />
                      
                </div>
                
        </div>
<%--        </div> --%>
    </fieldset>
</div>
</div>

 