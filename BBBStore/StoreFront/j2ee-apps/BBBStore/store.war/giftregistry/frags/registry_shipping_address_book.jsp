<%-- Code from Shipping Addresses -List Address Book --%>
<div class="registryForm">
    <fieldset class="radioGroup">
        <%-- <legend><bbbl:label key="lbl_shipping_current_addr" language ="${pageContext.request.locale.language}"/></legend> --%>
        <%-- Disaplay same as registrant--%>
        <div class="radioItem input clearfix">
            <div class="radio">
                <dsp:input tabindex="${tabIndex}" id="radRegistryCurrentShippingAddress" type="radio"
                    name="radRegistryCurrentShippingAddressName" checked="true" iclass="step2FocusField" 
                    value="shipAdrressSameAsRegistrant" bean="GiftRegistryFormHandler.shippingAddress" >
                    <dsp:tagAttribute name="aria-checked" value="true"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryCurrentShippingAddress"/>
                </dsp:input>
                <c:set var="tabIndex" value="${tabIndex + 1}" /> 
            </div>
            <div class="label">
                <label id="lblradRegistryCurrentShippingAddress" for="radRegistryCurrentShippingAddress" class="sameAsReg"><bbbl:label key="lbl_registry_addr_sameas_coreg" language ="${pageContext.request.locale.language}"/></label>
				<input type="hidden" name="zipvalue" class="favStoreZip" value="" />
            </div>
        </div>
        
        <%--  Variable to find count of profile addresses --%>
        <c:set var="addressBookSize" value="0" />
        
        <%--  Variable to find count of pobox addresses --%>
        <c:set var="poBoxShipAddrCount" value="0" />
        
        <%--  Flag to check/selected radio --%>
        <c:set var="isShipAddressRadioChecked" value="true" scope="page" />
        
        <%-- Code from address book --%>
        <%-- Iterate on address book to display addresses in address book --%>
        <%-- <dsp:droplet name="MapToArrayDefaultFirst"> --%>
        <dsp:droplet name="RegistryAddressOrderDroplet">
            <dsp:param name="defaultId" bean="Profile.shippingAddress.repositoryId"/>
            <dsp:param name="map" bean="Profile.secondaryAddresses"/>
            <dsp:param name="sortByKeys" value="true"/>
            <dsp:param name="wsAddressVO" value="${regVO.shipping.shippingAddress}"/>
            <dsp:param name="shippingFirstName" value="${regVO.shipping.shippingAddress.firstName}" />
            <dsp:param name="shippingLastName" value="${regVO.shipping.shippingAddress.lastName}" />
            
            <dsp:oparam name="output">
                <c:set var="counter" value="0"/>
                <dsp:getvalueof var="shipAddrSortedArray" vartype="java.lang.Object" param="sortedArray"/>
                
                <%-- If registrant address from webservice is not present in AddressBook Show it on top--%>
                <dsp:getvalueof id="isWSAddressInAddressBook" param="isWSAddressInAddressBook" idtype="java.lang.Boolean"/>
                <c:if test="${!(isWSAddressInAddressBook)}">
                    <div class="radioItem input clearfix">
                        <c:set var="shipAddr">
                            <dsp:valueof
                                value="${regVO.shipping.shippingAddress.addressLine1}"  /> <dsp:valueof
                                value="${regVO.shipping.shippingAddress.addressLine2}" />
                        </c:set>
                        <dsp:droplet name="POBoxValidateDroplet">
                            <dsp:param value="${shipAddr}" name="address" />
                            <dsp:oparam name="output">
                                <dsp:getvalueof var="isValid" param="isValid"/>
                                <c:choose>
                                    <c:when test="${isValid && (!shipTo_POBoxOn && currentSiteId ne BedBathCanadaSite)}">
                                        <div class="radio">
                                            <dsp:input tabindex="${tabIndex}" id="radRegistryCurrentShippingAddress01" type="radio"
                                                name="radRegistryCurrentShippingAddressName" value="oldShippingAddressFromWS" 
                                                bean="GiftRegistryFormHandler.shippingAddress" disabled="true"/>
                                        </div>
                                        <c:set var="poBoxShipAddrCount" value="${poBoxShipAddrCount+1}" />
                                    </c:when>
                                    <c:otherwise>
                                        <div class="radio">
                                            <dsp:input tabindex="${tabIndex}" id="radRegistryCurrentShippingAddress01" type="radio"
                                                name="radRegistryCurrentShippingAddressName" value="oldShippingAddressFromWS" 
                                                bean="GiftRegistryFormHandler.shippingAddress" checked="${isShipAddressRadioChecked}"/>
                                        </div>
                                        <c:set var="isShipAddressRadioChecked" value="false" scope="page" />
                                    </c:otherwise>
                                </c:choose>
                                <c:set var="tabIndex" value="${tabIndex + 1}" /> 
                            </dsp:oparam>
                        </dsp:droplet>
                        <%-- Check if Address is PO BOX Address --%>
                        
                        <div class="label">
                            <label for="radRegistryCurrentShippingAddress01">
                                <dsp:input type="hidden" id="regSFName0" name="regSFName0" bean="GiftRegistryFormHandler.shippingAddressFromWS.firstName"
                                 value="${regVO.shipping.shippingAddress.firstName}"/>
                                <dsp:input type="hidden" id="regSLName0" name="regSLName0" bean="GiftRegistryFormHandler.shippingAddressFromWS.lastName" 
                                 value="${regVO.shipping.shippingAddress.lastName}"/>
                                <dsp:input type="hidden" id="regSAddLine01" name="regSAddLine01" bean="GiftRegistryFormHandler.shippingAddressFromWS.addressLine1"
                                 value="${regVO.shipping.shippingAddress.addressLine1}"/>
                                <dsp:input type="hidden" id="regSAddLine02" name="regSAddLine02" bean="GiftRegistryFormHandler.shippingAddressFromWS.addressLine2"
                                 value="${regVO.shipping.shippingAddress.addressLine2}"/>
                                <dsp:input type="hidden" id="regSCity0" name="regSCity0" bean="GiftRegistryFormHandler.shippingAddressFromWS.city" 
                                 value="${regVO.shipping.shippingAddress.city}"/>
                                <dsp:input type="hidden" id="regSState0" name="regSState0" bean="GiftRegistryFormHandler.shippingAddressFromWS.state" 
                                 value="${regVO.shipping.shippingAddress.state}"/>
                                <dsp:input type="hidden" id="regSZip0" name="regSZip0" bean="GiftRegistryFormHandler.shippingAddressFromWS.zip"
                                 value="${regVO.shipping.shippingAddress.zip}" />
								 <span class="displayToggle">
                                <dsp:valueof
                                    value="${regVO.shipping.shippingAddress.firstName}"  /> <dsp:valueof
                                    value="${regVO.shipping.shippingAddress.lastName}" /> 
									</span>  
                                  <dsp:valueof
                                    value="${regVO.shipping.shippingAddress.addressLine1}"  /> <dsp:valueof
                                    value="${regVO.shipping.shippingAddress.addressLine2}" /> <br /> <dsp:valueof
                                    value="${regVO.shipping.shippingAddress.city}"/>, <dsp:valueof
                                    value="${regVO.shipping.shippingAddress.state}"/> <dsp:valueof
                                    value="${regVO.shipping.shippingAddress.zip}" />
                             </label>
							 <input type="hidden" name="zipvalue" class="favStoreZip" value='<dsp:valueof
                                value="${regVO.shipping.shippingAddress.zip}" />' />
                        </div>
                    </div>
                </c:if>
                
                <c:choose>
                    <c:when test="${empty shipAddrSortedArray}">
                    </c:when>
                    <c:otherwise>
                        <c:set var="addressBookSize" value="${fn:length(shipAddrSortedArray)}" />
                        
                        <c:forEach var="shippingAddress" items="${shipAddrSortedArray}">
                            <c:set var="counter"  value="${counter+1}" />
                            <dsp:setvalue param="shippingAddress" value="${shippingAddress}"/>
                            <c:if test="${not empty shippingAddress}">
                                <dsp:param name="address" param="shippingAddress.value"/>
                                <dsp:getvalueof var="addressValue" param="address.country"/>
                                <c:choose>
                                    <c:when test='${addressValue == ""}'/>
                                    <c:otherwise>
                                        <div class="radioItem input clearfix">
                                            <dsp:getvalueof var="addressId" param="address.id"/>
                                            <%-- Check if Address is PO BOX Address If yes then disable it--%>
                                            <c:set var="shipAddr">
                                                <dsp:valueof param="address.address1"/>    
                                                <dsp:valueof param="address.address2"/>    
                                            </c:set>
                                            <dsp:droplet name="POBoxValidateDroplet">
                                                <dsp:param value="${shipAddr}" name="address" />
                                                <dsp:oparam name="output">
                                                    <dsp:getvalueof var="isValid" param="isValid"/>                                            
                                                    <c:choose>
                                                        <c:when test="${isValid && (!shipTo_POBoxOn && currentSiteId ne BedBathCanadaSite)}">
                                                            <div class="radio">
                                                                <dsp:input tabindex="${tabIndex}" id="radRegistryCurrentShippingAddress${counter}" type="radio"
                                                                    name="radRegistryCurrentShippingAddressName" 
                                                                    value="${addressId}" bean="GiftRegistryFormHandler.shippingAddress" disabled="true">
                                                                    <dsp:tagAttribute name="aria-checked" value="false"/>
                                                                    <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryCurrentShippingAddress${counter}"/>
                                                                </dsp:input>
                                                            </div>
                                                            <c:set var="poBoxShipAddrCount" value="${poBoxShipAddrCount+1}" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="radio">
                                                                <dsp:input tabindex="${tabIndex}" id="radRegistryCurrentShippingAddress${counter}" type="radio"
                                                                    name="radRegistryCurrentShippingAddressName" 
                                                                    value="${addressId}" bean="GiftRegistryFormHandler.shippingAddress" checked="${isShipAddressRadioChecked}">
                                                                    <dsp:tagAttribute name="aria-checked" value="false"/>
                                                                    <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryCurrentShippingAddress${counter}"/>
                                                                </dsp:input>
                                                            </div>
                                                            <c:set var="isShipAddressRadioChecked" value="false" scope="page" />
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <c:set var="tabIndex" value="${tabIndex + 1}" /> 
                                                </dsp:oparam>
                                            </dsp:droplet>
                                            <%-- Check if Address is PO BOX Address --%>
                                            
                                            <div class="label">
                                                <label id="lblradRegistryCurrentShippingAddress${counter}" for="radRegistryCurrentShippingAddress${counter}">
	                                                <span class="displayToggle">
		                                                <c:choose>
		                                                	<c:when test="${!(counter > 1) && (isWSAddressInAddressBook) && !(empty regVO.shipping.shippingAddress.addressLine1)}">
		                                                		${regVO.shipping.shippingAddress.firstName} ${regVO.shipping.shippingAddress.lastName}
		                                                	</c:when>
		                                                	<c:otherwise>
		                                                		<dsp:valueof param="address.firstName"/> <dsp:valueof param="address.lastName"/>
		                                                	</c:otherwise>
		                                                </c:choose>
                                                    </span>
                                                    <dsp:valueof param="address.address1"/><br/>
                                                    <dsp:getvalueof var="address2Value" param="address.address2"/>
													<c:if test='${not empty address2Value}'><dsp:valueof param="address.address2"/> <br/></c:if>
                                                    <dsp:valueof param="address.city"/> 
                                                    <dsp:valueof param="address.state"/>
                                                    <dsp:valueof param="address.postalCode"/>
                                                 </label>
												 <input type="hidden" name="zipvalue" class="favStoreZip" value='<dsp:valueof param="address.postalCode"/>' />
												 
                                            </div>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </dsp:oparam>
        </dsp:droplet> 
        <%-- Iterate on address book to display addresses in address book --%>
        
        <div class="radioItem input last clearfix">
            <div class="radio">
                <c:choose>
                    <%-- If all the profile addresses were pobox addresses or there was not address in profile --%>
                    <c:when test="${(poBoxShipAddrCount ge  addressBookSize ) && isShipAddressRadioChecked}">
                        <dsp:input tabindex="${tabIndex}" value="newShippingAddress" 
                            id="radRegistryCurrentShippingAddressNew" type="radio"
                            name="radRegistryCurrentShippingAddressName" 
                            bean="GiftRegistryFormHandler.shippingAddress">
                            <dsp:tagAttribute name="aria-checked" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryCurrentShippingAddressNew"/>
                        </dsp:input>
                    </c:when>
                    <c:otherwise>
                        <dsp:input tabindex="${tabIndex}" value="newShippingAddress"
                            id="radRegistryCurrentShippingAddressNew" type="radio"
                            name="radRegistryCurrentShippingAddressName" 
                            bean="GiftRegistryFormHandler.shippingAddress">
                            <dsp:tagAttribute name="aria-checked" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryCurrentShippingAddressNew"/>
                        </dsp:input>
                    </c:otherwise>
                </c:choose>
                <c:set var="tabIndex" value="${tabIndex + 1}" /> 
            </div>
            <div class="label">
                <label id="lblradRegistryCurrentShippingAddressNew" for="radRegistryCurrentShippingAddressNew" class="newAddress">
                    <bbbl:label key="lbl_shipping_new_addr" language ="${pageContext.request.locale.language}"/>
                </label>
            </div>
            <div class="clear"></div>
            
            <div class="subForm validateFormWithQAS">
                <div class="inputField clearfix" id="shippingNewAddrFirstName">
                    <label id="lbltxtRegistryCurrentShippingFirstName" for="txtRegistryCurrentShippingFirstName">
                        <bbbl:label key="lbl_registrants_firstname" language ="${pageContext.request.locale.language}"/>
                        <span class="required">*</span>
			<span class="txtOffScreen"><bbbl:label key='lbl_enter_first_name' language="${pageContext.request.locale.language}" /></span>
                    </label>
                    <dsp:input tabindex="${tabIndex}" id="txtRegistryCurrentShippingFirstName"
                        type="text"
                        name="txtRegistryCurrentShippingFirstNameName"
                        bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.firstName" iclass="required cannotStartWithSpecialChars alphabasicpunc" maxlength="30" >
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryCurrentShippingFirstName errortxtRegistryCurrentShippingFirstName"/>
                    </dsp:input>
                    <c:set var="tabIndex" value="${tabIndex + 1}" /> 
                </div>

                <div class="inputField clearfix" id="shippingNewAddrLastName">
                    <label id="lbltxtRegistryCurrentShippingLastName" for="txtRegistryCurrentShippingLastName">
                        <bbbl:label key="lbl_registrants_lastname" language ="${pageContext.request.locale.language}"/>
                        <span class="required">*</span>
			<span class="txtOffScreen"><bbbl:label key='lbl_enter_last_name' language="${pageContext.request.locale.language}" /></span>
                    </label>
                    <dsp:input tabindex="${tabIndex}" id="txtRegistryCurrentShippingLastName"
                        type="text"
                        name="txtRegistryCurrentShippingLastNameName"
                        bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.lastName" iclass="required cannotStartWithSpecialChars alphabasicpunc" maxlength="30" >
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryCurrentShippingLastName errortxtRegistryCurrentShippingLastName"/>
                    </dsp:input>
                    <c:set var="tabIndex" value="${tabIndex + 1}" /> 
                </div>

                <div class="inputField clearfix" id="shippingNewAddrLine1">
                    <label id="lbltxtRegistryCurrentShippingAddress1" for="txtRegistryCurrentShippingAddress1">
                        <bbbl:label key="lbl_registrants_addr_line1" language ="${pageContext.request.locale.language}"/>
                        <span class="required">*</span>
                    </label>
                    <c:choose>
					<c:when test="${not empty shipTo_POBoxOn &&  shipTo_POBoxOn && (currentSiteId ne BedBathCanadaSiteCode)}">
					<c:set var="iclassValue" value=""/>
					</c:when>
					<c:otherwise>
					<c:set var="iclassValue" value="poBoxAddNotAllowed"/>
					</c:otherwise>
				</c:choose>
                    <dsp:input tabindex="${tabIndex}" id="txtRegistryCurrentShippingAddress1"
                        type="text"
                        name="txtRegistryCurrentShippingAddress1Name"
                        bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine1" iclass="${iclassValue} required addressAlphanumericbasicpunc cannotStartWithWhiteSpace QASAddress1" maxlength="30" >
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryCurrentShippingAddress1 errortxtRegistryCurrentShippingAddress1"/>
                    </dsp:input>
                    <c:set var="tabIndex" value="${tabIndex + 1}" /> 
                </div>

                <div class="inputField clearfix" id="shippingNewAddrLine2">
                    <label id="lbltxtRegistryCurrentShippingAddress2" for="txtRegistryCurrentShippingAddress2">
                        <bbbl:label key="lbl_registrants_addr_line2" language ="${pageContext.request.locale.language}"/>
                    </label>
                    <dsp:input tabindex="${tabIndex}" id="txtRegistryCurrentShippingAddress2"
                        type="text"
                        name="txtRegistryCurrentShippingAddress2Name"
                        bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine2" iclass="${iclassValue} addressAlphanumericbasicpunc cannotStartWithWhiteSpace QASAddress2" maxlength="30" >
                        <dsp:tagAttribute name="aria-required" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryCurrentShippingAddress2 errortxtRegistryCurrentShippingAddress2"/>
                    </dsp:input>
                    <c:set var="tabIndex" value="${tabIndex + 1}" /> 
                </div>

                <div class="inputField clearfix" id="shippingNewAddrFirstCity">
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

                <div class="inputSelect pushDown">
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

                <div class="inputField clearfix" id="shippingNewAddrZip">
                    <label id="lbltxtRegistryCurrentShippingZip" for="txtRegistryCurrentShippingZip"> 
                        <c:choose>
                            <c:when test="${siteId == BedBathCanadaSiteCode}">
                                <bbbl:label key="lbl_registrants_zip_ca" language ="${pageContext.request.locale.language}"/>
                            </c:when>
                            <c:otherwise>
                                <bbbl:label key="lbl_registrants_zip" language ="${pageContext.request.locale.language}"/>
                            </c:otherwise>
                        </c:choose>
                        <span class="required">*</span>
                    </label>
                    <c:choose>
                        <c:when test="${currentSiteId eq BedBathCanadaSiteCode}">
                            <dsp:input tabindex="${tabIndex}" id="txtRegistryCurrentShippingZip" type="text"
                                name="txtRegistryCurrentShippingZipName"
                                bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.zip" iclass="required zipCA QASZipUS favStoreZip" >
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
                <div class="clear"></div>
            </div>
            <div class="clear"></div>
        </div>
        <div class="clear"></div>
    </fieldset>
</div>
