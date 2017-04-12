<fieldset class="radioGroup clearfix grid_4 alpha omega" id="future-address">
    <legend>
        <bbbl:label key="lbl_preview_future_shipping_addr" language ="${pageContext.request.locale.language}"/>
    </legend>

    <div class="radioItem input clearfix grid_4 alpha omega">
        <div class="radio">
            <dsp:input tabindex="${tabIndex}" value="futShipAdrressSameAsRegistrant"
                id="radRegistryFutureShippingContactAddress" type="radio" checked="true"
                name="radRegistryFutureShippingAddressName" bean="GiftRegistryFormHandler.futureShippingAddress">
                <dsp:tagAttribute name="aria-checked" value="true"/>
                <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryFutureShippingContactAddress"/>
            </dsp:input>
            <c:set var="tabIndex" value="${tabIndex + 1}" /> 
        </div>
        <div class="label">
            <label id="lblradRegistryFutureShippingContactAddress" for="radRegistryFutureShippingContactAddress" class="sameAsReg">
                <bbbl:label key="lbl_registry_addr_sameas_coreg" language ="${pageContext.request.locale.language}"/>
            </label>
        </div>
    </div>
    
    <%--  Variable to find count of profile addresses --%>
    <c:set var="futAddressBookSize" value="0" />
    <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
    
    <%--  Variable to find count of pobox addresses --%>
    <c:set var="poBoxFutShipAddrCount" value="0" />

    <%--  Flag to check/selected radio --%>
    <c:set var="isFShipAddressRadioChecked" value="true" scope="page" />
    
    <%-- Code from address book --%>
    <%-- Iterate on address book to display addresses in address book --%>
    <%-- <dsp:droplet name="MapToArrayDefaultFirst"> --%>
    <dsp:droplet name="RegistryAddressOrderDroplet">
        <dsp:param name="defaultId" bean="Profile.shippingAddress.repositoryId"/>
        <dsp:param name="map" bean="Profile.secondaryAddresses"/>
        <dsp:param name="sortByKeys" value="true"/>
        <dsp:param name="wsAddressVO" value="${regVO.shipping.futureshippingAddress}"/>
        
        <dsp:oparam name="output">
            <c:set var="counter" value="0"/>
            <dsp:getvalueof var="futAddrSortedArray" vartype="java.lang.Object" param="sortedArray"/>

            <%-- If registrant address from webservice is not present in AddressBook Show it on top--%>
            <dsp:getvalueof id="isWSAddressInAddressBook" param="isWSAddressInAddressBook" idtype="java.lang.Boolean"/>
            <c:if test="${!(isWSAddressInAddressBook) && !(empty regVO.shipping.futureshippingAddress.addressLine1)}">
                <div class="radioItem input clearfix grid_4 alpha omega">
                    <c:set var="futShipAddr">
                        <dsp:valueof
                            value="${regVO.shipping.futureshippingAddress.addressLine1}"  /> <dsp:valueof
                            value="${regVO.shipping.futureshippingAddress.addressLine2}" />
                    </c:set>
                    <dsp:droplet name="POBoxValidateDroplet">
                        <dsp:param value="${futShipAddr}" name="address" />
                        <dsp:oparam name="output">
                            <dsp:getvalueof var="isValid" param="isValid"/>
                            <c:choose>
                                <c:when test="${isValid  && (!shipTo_POBoxOn && currentSiteId ne BedBathCanadaSite)}">
                                    <div class="radio">
                                        <dsp:input tabindex="${tabIndex}" value="oldFutShippingAddressFromWS"
                                            id="radRegistryFutureShippingAddress01" type="radio"
                                            name="radRegistryFutureShippingAddressName" bean="GiftRegistryFormHandler.futureShippingAddress" disabled="true">
                                            <dsp:tagAttribute name="aria-checked" value="false"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryFutureShippingAddress01"/>
                                        </dsp:input>
                                    </div>
                                    <c:set var="poBoxFutShipAddrCount" value="${poBoxFutShipAddrCount+1}" />
                                </c:when>
                                <c:otherwise>
                                    <div class="radio">
                                        <dsp:input tabindex="${tabIndex}" value="oldFutShippingAddressFromWS"
                                            id="radRegistryFutureShippingAddress01" type="radio"
                                            name="radRegistryFutureShippingAddressName" bean="GiftRegistryFormHandler.futureShippingAddress" checked="${isFShipAddressRadioChecked}">
                                            <dsp:tagAttribute name="aria-checked" value="false"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryFutureShippingAddress01"/>
                                        </dsp:input>
                                    </div>
                                    <c:set var="isFShipAddressRadioChecked" value="false" scope="page" />
                                </c:otherwise>
                            </c:choose>
                            <c:set var="tabIndex" value="${tabIndex + 1}" /> 
                        </dsp:oparam>
                    </dsp:droplet>
                    <%-- Check if Address is PO BOX Address --%>
                    
                    <div class="label">
                        <label id="lblradRegistryFutureShippingAddress01" for="radRegistryFutureShippingAddress01">
                            <dsp:input type="hidden" name="regFutFName0" bean="GiftRegistryFormHandler.futureShippingAddressFromWS.firstName"
                             value="${regVO.shipping.futureshippingAddress.firstName}"/>
                            <dsp:input type="hidden" name="regFutLName0" bean="GiftRegistryFormHandler.futureShippingAddressFromWS.lastName" 
                             value="${regVO.shipping.futureshippingAddress.lastName}"/>
                            <dsp:input type="hidden" name="regFutAddLine01" bean="GiftRegistryFormHandler.futureShippingAddressFromWS.addressLine1"
                             value="${regVO.shipping.futureshippingAddress.addressLine1}"/>
                            <dsp:input type="hidden" name="regFutAddLine02" bean="GiftRegistryFormHandler.futureShippingAddressFromWS.addressLine2"
                             value="${regVO.shipping.futureshippingAddress.addressLine2}"/>
                            <dsp:input type="hidden" name="regFutCity0" bean="GiftRegistryFormHandler.futureShippingAddressFromWS.city" 
                             value="${regVO.shipping.futureshippingAddress.city}"/>
                            <dsp:input type="hidden" name="regFutState0" bean="GiftRegistryFormHandler.futureShippingAddressFromWS.state" 
                             value="${regVO.shipping.futureshippingAddress.state}"/>
                            <dsp:input type="hidden" name="regFutZip0" bean="GiftRegistryFormHandler.futureShippingAddressFromWS.zip"
                             value="${regVO.shipping.futureshippingAddress.zip}" />
                            <span class="displayToggle">
                                                 <dsp:valueof
                                value="${regVO.shipping.futureshippingAddress.firstName}"  /> <dsp:valueof
                                value="${regVO.shipping.futureshippingAddress.lastName}" />
                                                </span>
                             <dsp:valueof
                                value="${regVO.shipping.futureshippingAddress.addressLine1}"  /> <dsp:valueof
                                value="${regVO.shipping.futureshippingAddress.addressLine2}" /> <br /> <dsp:valueof
                                value="${regVO.shipping.futureshippingAddress.city}"/>, <dsp:valueof
                                value="${regVO.shipping.futureshippingAddress.state}"/> <dsp:valueof
                                value="${regVO.shipping.futureshippingAddress.zip}" />
                         </label>
                    </div>
                </div>
            </c:if>
            
            <c:choose>
                <c:when test="${empty futAddrSortedArray}">
                </c:when>
                <c:otherwise>
                    <c:set var="futAddressBookSize" value="${fn:length(futAddrSortedArray)}" />
                    
                    <c:forEach var="fShippingAddress" items="${futAddrSortedArray}">
                        <dsp:setvalue param="fShippingAddress" value="${fShippingAddress}"/>
                        <c:set var="counter"  value="${counter+1}" />
                          
                        <c:if test="${not empty fShippingAddress}">
                            <dsp:param name="address" param="fShippingAddress.value"/>
    
                            <dsp:getvalueof var="addressValue" param="address.country"/>
                            <c:choose>
                                <c:when test='${addressValue == ""}'/>
                                <c:otherwise>
                                    <div class="radioItem input clearfix grid_4 alpha omega">
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
                                                            <dsp:input tabindex="${tabIndex}" value="${addressId}"
                                                                id="radRegistryFutureShippingAddress${counter}" type="radio"
                                                                name="radRegistryFutureShippingAddressName" bean="GiftRegistryFormHandler.futureShippingAddress" 
                                                                disabled="true">
                                                                <dsp:tagAttribute name="aria-checked" value="false"/>
                                                                <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryFutureShippingAddress${counter}"/>
                                                            </dsp:input>
                                                        </div>
                                                        <c:set var="poBoxFutShipAddrCount" value="${poBoxFutShipAddrCount+1}" />
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="radio">
                                                            <dsp:input tabindex="${tabIndex}" value="${addressId}"
                                                                id="radRegistryFutureShippingAddress${counter}" type="radio"
                                                                name="radRegistryFutureShippingAddressName" bean="GiftRegistryFormHandler.futureShippingAddress" 
                                                                checked="${isFShipAddressRadioChecked}">
                                                                <dsp:tagAttribute name="aria-checked" value="false"/>
                                                                <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryFutureShippingAddress${counter}"/>
                                                            </dsp:input>
                                                        </div>
                                                        <c:set var="isFShipAddressRadioChecked" value="false" scope="page" />
                                                    </c:otherwise>
                                                </c:choose>
                                                <c:set var="tabIndex" value="${tabIndex + 1}" /> 
                                            </dsp:oparam>
                                        </dsp:droplet>
                                        <%-- Check if Address is PO BOX Address --%>
                                        
                                        <div class="label">
                                            <label id="lblradRegistryFutureShippingAddress${counter}" for="radRegistryFutureShippingAddress${counter}">
                                                <span class="displayToggle">
                                                <c:choose>
                                                	<c:when test="${!(counter > 1) && (isWSAddressInAddressBook) && !(empty regVO.shipping.futureshippingAddress.addressLine1)}">
                                                		${regVO.shipping.futureshippingAddress.firstName} ${regVO.shipping.futureshippingAddress.lastName}
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
    <%--End Iterate on address book to display addresses in address book --%>    

    <div class="radioItem input clearfix grid_4 alpha omega last">
        <div class="radio">
            <c:choose>
                <%-- If all the profile addresses were pobox addresses or there was not address in profile --%>
                <c:when test="${(poBoxFutShipAddrCount ge  futAddressBookSize) && isFShipAddressRadioChecked && futAddressBookSize ne 0}">
                    <dsp:input tabindex="${tabIndex}" value="newFutureShippingAddress"
                        id="radRegistryFutureShippingAddressNew" type="radio"
                        name="radRegistryFutureShippingAddressName" 
                        bean="GiftRegistryFormHandler.futureShippingAddress" checked="true">
                        <dsp:tagAttribute name="aria-checked" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryFutureShippingAddressNew"/>
                    </dsp:input>
                </c:when>
                <c:otherwise>
                    <dsp:input tabindex="${tabIndex}" value="newFutureShippingAddress"
                        id="radRegistryFutureShippingAddressNew" type="radio"
                        name="radRegistryFutureShippingAddressName" 
                        bean="GiftRegistryFormHandler.futureShippingAddress">
                        <dsp:tagAttribute name="aria-checked" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryFutureShippingAddressNew"/>
                    </dsp:input>
                </c:otherwise>
            </c:choose>
            <c:set var="tabIndex" value="${tabIndex + 1}" /> 
        </div>
        
        <div class="label">
            <label id="lblradRegistryFutureShippingAddressNew" for="radRegistryFutureShippingAddressNew" class="newAddress"><bbbl:label key='lbl_regcreate_reginfo_add_diffadd' language="${pageContext.request.locale.language}" /></label>
        </div>

        <div class="clear"></div>

        <div class="subForm validateFormWithQAS">
            <div class="inputField clearfix" id="shippingFutureAddrFirstName">
                <label id="lbltxtRegistryFutureShippingFirstName" for="txtRegistryFutureShippingFirstName">
                    <bbbl:label key="lbl_registrants_firstname" language ="${pageContext.request.locale.language}"/>
                    <span class="required">*</span>
                </label>
                <dsp:input tabindex="${tabIndex}" id="txtRegistryFutureShippingFirstName"
                    type="text" name="txtRegistryFutureShippingFirstNameName"
                    bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.firstName" iclass="required cannotStartWithSpecialChars alphabasicpunc" maxlength="30" >
                    <dsp:tagAttribute name="aria-required" value="true"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryFutureShippingFirstName"/>
                </dsp:input>
                <c:set var="tabIndex" value="${tabIndex + 1}" /> 
            </div>
            <div class="inputField clearfix" id="shippingFutureAddrLastName">
                <label id="lbltxtRegistryFutureShippingLastName" for="txtRegistryFutureShippingLastName">
                    <bbbl:label key="lbl_registrants_lastname" language ="${pageContext.request.locale.language}"/>
                    <span class="required">*</span>
                </label>
                <dsp:input tabindex="${tabIndex}" id="txtRegistryFutureShippingLastName"
                    type="text" name="txtRegistryFutureShippingLastNameName"
                    bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.lastName" iclass="required cannotStartWithSpecialChars alphabasicpunc" maxlength="30" >
                    <dsp:tagAttribute name="aria-required" value="true"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryFutureShippingLastName"/>
                </dsp:input>
                <c:set var="tabIndex" value="${tabIndex + 1}" /> 
            </div>
            <div class="inputField clearfix" id="shippingFutureAddrLine1">
                <label id="lbltxtRegistryFutureShippingAddress1" for="txtRegistryFutureShippingAddress1">
                    <bbbl:label key="lbl_registrants_addr_line1" language ="${pageContext.request.locale.language}"/>
                    <span class="required">*</span>
                </label>
                <c:choose>
					<c:when test="${not empty shipTo_POBoxOn &&  shipTo_POBoxOn && (currentSiteId ne BedBathCanadaSite)}">
					<c:set var="iclassValue" value=""/>
					</c:when>
					<c:otherwise>
					<c:set var="iclassValue" value="poBoxAddNotAllowed"/>
					</c:otherwise>
				</c:choose>
                <dsp:input tabindex="${tabIndex}" id="txtRegistryFutureShippingAddress1" type="text"
                    value="" name="txtRegistryFutureShippingAddress1Name" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.addressLine1" iclass="${iclassValue} required addressAlphanumericbasicpunc cannotStartWithWhiteSpace QASAddress1" maxlength="30" >
                    <dsp:tagAttribute name="aria-required" value="true"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryFutureShippingAddress1 errortxtRegistryFutureShippingAddress1"/>
                </dsp:input>
                <c:set var="tabIndex" value="${tabIndex + 1}" /> 
            </div>
            <div class="inputField clearfix" id="shippingFutureAddrLine2">
                <label id="lbltxtRegistryFutureShippingAddress2" for="txtRegistryFutureShippingAddress2">
                    <bbbl:label key="lbl_registrants_addr_line2" language ="${pageContext.request.locale.language}"/>
                </label>
                <dsp:input tabindex="${tabIndex}" id="txtRegistryFutureShippingAddress2"
                    type="text" name="txtRegistryFutureShippingAddress2Name"
                    bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.addressLine2" iclass="${iclassValue} addressAlphanumericbasicpunc cannotStartWithWhiteSpace QASAddress2" maxlength="30" >
                    <dsp:tagAttribute name="aria-required" value="false"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryFutureShippingAddress2"/>
                </dsp:input>
                <c:set var="tabIndex" value="${tabIndex + 1}" /> 
            </div>
            <div class="inputField clearfix" id="shippingFutureAddrCity">
                <label id="lbltxtRegistryFutureShippingCity" for="txtRegistryFutureShippingCity">
                    <bbbl:label key="lbl_registrants_city" language ="${pageContext.request.locale.language}"/>
                    <span class="required">*</span>
                </label>
                <dsp:input tabindex="${tabIndex}" id="txtRegistryFutureShippingCity" type="text"
                    name="txtRegistryFutureShippingCityName"
                    bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.city" iclass="required cannotStartWithWhiteSpace QASCityName" maxlength="25">
                    <dsp:tagAttribute name="aria-required" value="true"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryFutureShippingCity errortxtRegistryFutureShippingCity"/>
                </dsp:input>
                <c:set var="tabIndex" value="${tabIndex + 1}" /> 
            </div>
           <div class="inputSelect pushDown">
                <label id="lblselRegistryFutureShippingState" for="selRegistryFutureShippingState">
                    <c:choose>
                        <c:when test="${siteId eq BedBathCanadaSiteCode}">
                            <bbbl:label key="lbl_registrants_statecanada" language ="${pageContext.request.locale.language}"/>
                        </c:when>
                        <c:otherwise>
                            <bbbl:label key="lbl_registrants_state" language ="${pageContext.request.locale.language}"/>
                        </c:otherwise>
                    </c:choose>
                    <span class="required">*</span>
                </label>
                <dsp:select tabindex="${tabIndex}" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.state" name="selRegistryFutureShippingStateName" id="selRegistryFutureShippingState" iclass="uniform requiredValidation QASStateName required">
                <dsp:tagAttribute name="aria-required" value="true"/>
                <dsp:tagAttribute name="aria-labelledby" value="lblselRegistryFutureShippingState errorselRegistryFutureShippingState"/>
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
                <label id="errorselRegistryFutureShippingState" for="selRegistryFutureShippingState" generated="true" class="error" style="display:none;"></label>
            </div>
            <div class="inputField clearfix" id="shippingFutureAddrZip">
                <label id="lbltxtRegistryFutureShippingZip" for="txtRegistryFutureShippingZip"> 
                    <bbbl:label key="lbl_registrants_zip" language ="${pageContext.request.locale.language}"/>
                    <span class="required">*</span>

                </label>
                <c:choose>
                    <c:when test="${currentSiteId eq BedBathCanadaSiteCode}">
                    <dsp:input tabindex="${tabIndex}" id="txtRegistryFutureShippingZip" type="text"
                        name="txtRegistryFutureShippingZipName"
                        bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.zip" iclass="required zipCA QASZipUS" >
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryFutureShippingZip errortxtRegistryFutureShippingZip"/>
                    </dsp:input>
                    </c:when>
                    <c:otherwise>
                        <dsp:input tabindex="${tabIndex}" id="txtRegistryFutureShippingZip" type="text"
                            name="txtRegistryFutureShippingZipName"
                            bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.zip" iclass="required zipUS QASZipUS" >
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryFutureShippingZip errortxtRegistryFutureShippingZip"/>
                        </dsp:input>
                    </c:otherwise>
               </c:choose>
               <c:set var="tabIndex" value="${tabIndex + 1}" />
               
               	<input type="hidden" name="QASCountryName" id="QASCountryName"/>
		   		<dsp:input type="hidden" name="QASFuturePoBoxFlag" bean="GiftRegistryFormHandler.futurePoBoxFlag" id="QASFuturePoBoxFlag" iclass="QASPoBoxFlag" />
		   		<dsp:input type="hidden" name="QASFuturePoBoxStatus" bean="GiftRegistryFormHandler.futurePoBoxStatus" id="QASFuturePoBoxStatus" iclass="QASPoBoxStatus" />               
             
           </div>
            <div class="clear"></div>
        </div>
        <div class="clear"></div>
    </div>
    <div class="clear"></div>
</fieldset>
