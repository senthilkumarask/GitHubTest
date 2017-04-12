<c:choose>
	<c:when test="${event == 'BA1' }">
		<div class="grid_10 center flatform">
    </c:when>
    <c:otherwise>
		<div class="grid_6 center flatform">
    </c:otherwise>
</c:choose>

<div class="grid_4 alpha omega">
<div class="simpleRegForm">
	<dsp:getvalueof var="isTransient" bean="Profile.transient"/>
	<dsp:getvalueof var="futureShipAddr" value="${regVO.shipping.futureshippingAddress}" />
	<dsp:getvalueof var="futureShipAddressLine1" value="${regVO.shipping.futureshippingAddress.addressLine1}" />
	<dsp:getvalueof var="futureShipAddress2" value="${regVO.shipping.futureshippingAddress.addressLine2}" />
	<c:set var="voFutureShippingAddr" value=""/>
	<c:set var="checkFutureShippingBox" value="false"/>
	<c:set var="removeHiddenClassFromDiv" value="hidden"/>
	<c:if test="${futureShipAddr ne ''  && futureShipAddr ne null && futureShipAddressLine1 ne '' && futureShipAddressLine1 ne null}">
			<c:set var="voFutureShippingAddr">
				${futureShipAddressLine1}, <dsp:valueof value="${regVO.shipping.futureshippingAddress.city}" />, <dsp:valueof value="${regVO.shipping.futureshippingAddress.state}" />, <dsp:valueof value="${regVO.shipping.futureshippingAddress.zip}" />
			</c:set>
			
			<dsp:setvalue bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.addressLine1" value="${regVO.shipping.futureshippingAddress.addressLine1}" />
			<dsp:setvalue bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.addressLine2" value="${regVO.shipping.futureshippingAddress.addressLine2}" />
			<dsp:setvalue bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.city" value="${regVO.shipping.futureshippingAddress.city}" />
			<dsp:setvalue bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.state" value="${regVO.shipping.futureshippingAddress.state}" />
			<dsp:setvalue bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.zip" value="${regVO.shipping.futureshippingAddress.zip}" />
	</c:if>
	
	<c:choose>
		<c:when test="${not empty regVO.shipping.futureShippingDate}">
			<dsp:getvalueof var="voFutShipDate" value="${regVO.shipping.futureShippingDate}"/>
		</c:when>
		<c:otherwise>
			<dsp:getvalueof var="voFutShipDate" value=""/>
		</c:otherwise>
	</c:choose>
	
	<c:if test="${not empty voFutShipDate || not empty voFutureShippingAddr}">
		<c:set var="checkFutureShippingBox" value="true"/>
		<c:choose>
		<c:when test="${checkFutureShippingBox == 'true'}">
			<c:set var="removeHiddenClassFromDiv" value=""/>
		</c:when>
		<c:otherwise>
			<c:set var="removeHiddenClassFromDiv" value="hidden"/>
		</c:otherwise>
		</c:choose>
	</c:if>

    <fieldset class="radioGroup">
        
                <div class="checkboxItem inputField clearfix movingAdd">
							<div class="checkbox">
										<dsp:input type="checkbox" name="futureShippingDateSelectedCheckBox" id="radRegistryMovingAddress"
											bean="GiftRegistryFormHandler.futureShippingDateSelected" checked="${checkFutureShippingBox}">
	                                <dsp:tagAttribute name="aria-checked" value="${checkFutureShippingBox}"/>
	                                <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryMovingAddress"/>
	                           </dsp:input>
							</div>
							<div class="label">
								<label id="lblradRegistryMovingAddress" for="radRegistryMovingAddress">
									 <bbbl:label key='lbl_reg_moving_store' language ='${pageContext.request.locale.language}'/>
								</label>
							</div>
                           
						</div>
                        
                        <!-- Moving address -->
                         <div class="grid_4 alpha omega  form clearfix ${removeHiddenClassFromDiv} marBottom_20" id="movingaddres">
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
                                    
							<c:set var="futuredate"><bbbl:label key='lbl_reg_ph_futuredate' language ='${pageContext.request.locale.language}'/></c:set>
                                    <div class="text">
                                        <div class="grid_2 alpha omega" aria-live="assertive">
                                            <dsp:input 
                                                bean="GiftRegistryFormHandler.registryVO.shipping.futureShippingDate"
                                                type="text" id="txtMovingAddDate${CADate}"  name="txtMovingAddDateName${CADate}" iclass="isMandatoryField step1FocusField" value="${voFutShipDate}">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtMovingAddDate${CADate} errortxtMovingAddDate${CADate}"/>
                                                <dsp:tagAttribute name="placeholder" value="${futuredate}" />
                                            </dsp:input>
                                              <div id="MovingAddDateButton${CADate}">
                                              <span class="icon-fallback-text"><span class="icon-calendar" aria-hidden="true"></span><span class="icon-text"></span></span>
                                        </div>
                                    </div>
                                </div>
                                
                                </div>
								
                           <div class="inputField clearfix noMarRight" id="shippingFutureAdd">
							  <div class="grid_4 omega alpha">
		                          <input  id="txtSimpleRegFutureShippingGoogleSuggest" value="${voFutureShippingAddr}" type="text" name="txtSimpleRegFutureShippingGoogleSuggestName" class="isMandatoryField cannotStartWithWhiteSpace QASAddress1" maxlength="90" placeholder="<bbbl:label key='lbl_reg_ph_futureaddress' language ='${pageContext.request.locale.language}'/>" >
								  <input type="hidden" class='addressHidden' name="txtSimpleRegFutureShippingGoogleSuggestHidden" id="txtSimpleRegFutureShippingGoogleSuggestHidden" value="${voFutureShippingAddr}">
                          
		                          <c:if test="${! isTransient}">
		                            <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.firstName" beanvalue="Profile.firstName" iclass="QASFirstName"/>
										<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.lastName" beanvalue="Profile.lastName" iclass="QASLastName"/>
		                          </c:if>
			                        <dsp:input type="hidden" bean="GiftRegistryFormHandler.futureShippingAddress" id="newFutureShippingAddress" value=""/>
	                    			<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.firstName" id="futureFirstName" iclass="QASFirstName"/>
									<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.lastName" id="futureLastName" iclass="QASLastName"/>
									<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.addressLine1" id="txtSimpleRegFutureShippingAddress1"  iclass="address1"/>
									<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.addressLine2" id="txtSimpleRegFutureShippingAddress2"  iclass="address2"/>
									<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.city" id="txtSimpleRegFutureShippingCity" iclass="QASCity"/>
									<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.state" id="txtSimpleRegFutureShippingState" iclass="QASState"/>
									<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.zip" id="txtSimpleRegFutureShippingZip" iclass="QASZip"/>
									<input type="hidden" id="txtSimpleRegFutureShippingCountryName"  value="" class="countryName"/>
									<input type="hidden" id="PoBoxFlag"  value="" class="PoBoxFlag"/>
									<input type="hidden" id="PoBoxStatus"  value="" class="PoBoxStatus"/>
									<input type="hidden" id="txtRegistryFutureShippingBtn"/>
                    			</div>
								<div class="grid_2 omega alpha marTop_15">
									<label class="txtOffScreen" id="lbltxtRegistryFutureShippingAddress2" for="txtSimpleRegFutureShippingGoogleSuggestApt">${lbl_reg_ph_apt_building}</label>
                 					<input id="txtSimpleRegFutureShippingGoogleSuggestApt" maxlength="30" name="txtRegistryCurrentShippingAddress2Name" type="text" autocomplete="off"  class="cannotStartWithWhiteSpace ApartmentNo escapeHTMLTag" placeholder="Apt/Bldg (optional)" aria-labelledby="lbltxtRegistryFutureShippingAddress2" value="${futureShipAddress2}" data-previousapt="${futureShipAddress2}" > 
								</div>
                    		</div>
                    </div>
                        <!-- End Moving address -->
    </fieldset>
</div>
</div>
</div>

 