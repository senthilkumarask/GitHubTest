<c:set var="focusRegABClass" value="" />
<div class="favStoreZipWrapper registryForm marBottom_10">
    <fieldset class="radioGroup clearfix">
        <%-- <legend>
            <c:choose>
                <c:when test="${event == 'BA1'}">
                    <bbbl:label key="lbl_registrants_contact_addr_reg" language ="${pageContext.request.locale.language}"/>
                </c:when>
                <c:otherwise>
                    <bbbl:label key="lbl_registrants_contact_addr" language ="${pageContext.request.locale.language}"/>
                </c:otherwise>
            </c:choose>
        </legend> --%>

        <%--  Variable to find count of profile addresses --%>
        <c:set var="profileAddressesCount" value="0" />
        
        <%--  Variable to find count of pobox addresses --%>
        <c:set var="poBoxAddressesCount" value="0" />

        <%--  Flag to check/selected radio --%>
        <c:set var="regContactAddrRadioBtn" value="true" scope="page" />
                        
        <%-- Code from address book --%>
        <%-- Iterate on address book to display addresses in address book --%>
        <%-- <dsp:droplet name="MapToArrayDefaultFirst"> --%>
        <dsp:droplet name="RegistryAddressOrderDroplet">
            <dsp:param name="defaultId" bean="Profile.shippingAddress.repositoryId"/>
            <dsp:param name="map" bean="Profile.secondaryAddresses"/>
            <dsp:param name="sortByKeys" value="true"/>
            <dsp:param name="wsAddressVO" value="${regVO.primaryRegistrant.contactAddress}"/>
            
            <dsp:oparam name="output">
                <c:set var="counter" value="0"/>
                <dsp:getvalueof var="sortedArray" vartype="java.lang.Object" param="sortedArray"/>

                <%-- If registrant address from webservice is not present in AddressBook Show it on top--%>
                <dsp:getvalueof id="isWSAddressInAddressBook" param="isWSAddressInAddressBook" idtype="java.lang.Boolean"/>
                <c:if test="${!(isWSAddressInAddressBook)}">
                    <div class="radioItem input clearfix">
                        <c:set var="regAddr">
                             <dsp:valueof
                                value="${regVO.primaryRegistrant.contactAddress.addressLine1}"  /> <dsp:valueof
                                value="${regVO.primaryRegistrant.contactAddress.addressLine2}" />
                        </c:set>
                        <dsp:droplet name="POBoxValidateDroplet">
                            <dsp:param value="${regAddr}" name="address" />
                            <dsp:oparam name="output">
                                <dsp:getvalueof var="isValid" param="isValid"/>
                                <c:choose>
                                    <c:when test="${isValid && (!shipTo_POBoxOn && currentSiteId ne BedBathCanadaSite)}">
                                        <div class="radio">
                                        	<input type="hidden" value="${isValid}" name="isPOBox"/>
                                            <dsp:input tabindex="${tabIndex}" id="radRegistryRegistrantAddress" type="radio"
                                                    name="radRegistryRegistrantAddressName"
                                                    value="oldRegistrantAddressFromWS"
                                                    bean="GiftRegistryFormHandler.regContactAddress" disabled="true" >
                                                <dsp:tagAttribute name="aria-checked" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryRegistrantAddress errorradRegistryRegistrantAddressName"/>
                                            </dsp:input>
                                        </div>
                                        <c:set var="poBoxAddressesCount" value="${poBoxAddressesCount+1}" />
                                    </c:when>
                                    <c:otherwise>
                                        <c:choose>
                                            <c:when test="${step2FocusSet == false}">
                                                <c:set var="step2FocusSet" value="${true}" />
                                                <c:set var="focusRegABClass" value="step2FocusField" />
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="focusRegABClass" value="" />
                                            </c:otherwise>
                                        </c:choose>

                                        <div class="radio">
                                            <dsp:input tabindex="${tabIndex}" id="radRegistryRegistrantAddress" iclass="${focusRegABClass}" type="radio" name="radRegistryRegistrantAddressName" value="oldRegistrantAddressFromWS" bean="GiftRegistryFormHandler.regContactAddress" checked="${regContactAddrRadioBtn}">
                                                <dsp:tagAttribute name="aria-checked" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryRegistrantAddress errorradRegistryRegistrantAddressName"/>
                                            </dsp:input>
                                        </div>
                                        <c:set var="regContactAddrRadioBtn" value="false" scope="page" />
                                    </c:otherwise>
                                </c:choose>
                                <c:set var="tabIndex" value="${tabIndex + 1}" /> 
                            </dsp:oparam>
                        </dsp:droplet>
                        <%-- Check if Address is PO BOX Address --%>
                        
                        <div class="label">
                            <label id="lblradRegistryRegistrantAddress" for="radRegistryRegistrantAddress">
                                <dsp:input type="hidden" name="regFName0" bean="GiftRegistryFormHandler.registrantAddressFromWS.firstName"
                                 value="${regVO.primaryRegistrant.firstName}"/>
                                <dsp:input type="hidden" name="regLName0" bean="GiftRegistryFormHandler.registrantAddressFromWS.lastName" 
                                 value="${regVO.primaryRegistrant.lastName}"/>
                                <dsp:input type="hidden" name="regAddLine10" bean="GiftRegistryFormHandler.registrantAddressFromWS.addressLine1"
                                 value="${regVO.primaryRegistrant.contactAddress.addressLine1}"/>
                                <dsp:input type="hidden" name="regAddLine20" bean="GiftRegistryFormHandler.registrantAddressFromWS.addressLine2"
                                 value="${regVO.primaryRegistrant.contactAddress.addressLine2}"/>
                                <dsp:input type="hidden" name="regCity0" bean="GiftRegistryFormHandler.registrantAddressFromWS.city" 
                                 value="${regVO.primaryRegistrant.contactAddress.city}"/>
                                <dsp:input type="hidden" name="regState0" bean="GiftRegistryFormHandler.registrantAddressFromWS.state" 
                                 value="${regVO.primaryRegistrant.contactAddress.state}"/>
                                <dsp:input type="hidden" name="regZip0" bean="GiftRegistryFormHandler.registrantAddressFromWS.zip"
                                 value="${regVO.primaryRegistrant.contactAddress.zip}" />
                                  <span class="displayToggle">
                                               <dsp:valueof
                                    value="${regVO.primaryRegistrant.contactAddress.firstName}"  /> <dsp:valueof
                                    value="${regVO.primaryRegistrant.contactAddress.lastName}" /> 
                                                </span>
                                 <dsp:valueof
                                    value="${regVO.primaryRegistrant.contactAddress.addressLine1}"  /> <dsp:valueof
                                    value="${regVO.primaryRegistrant.contactAddress.addressLine2}" /> <br /> <dsp:valueof
                                    value="${regVO.primaryRegistrant.contactAddress.city}"/>, <dsp:valueof
                                    value="${regVO.primaryRegistrant.contactAddress.state}"/> <dsp:valueof
                                    value="${regVO.primaryRegistrant.contactAddress.zip}" />
                            </label>
							<input type="hidden" name="zipvalue" class="favStoreZip" value='<dsp:valueof
                                value="${regVO.primaryRegistrant.contactAddress.zip}" />' />
                        </div>
                    </div>
                </c:if>
                
                <c:choose>
                    <c:when test="${empty sortedArray}">
                    </c:when>
                    <c:otherwise>
                        <c:set var="profileAddressesCount" value="${fn:length(sortedArray)}" />
                        <c:forEach var="shippingAddress" items="${sortedArray}">
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
                                            <c:set var="regAddr">
                                                <dsp:valueof param="address.address1"/>
                                                <dsp:valueof param="address.address2"/>
                                            </c:set>
                                            <dsp:droplet name="POBoxValidateDroplet">
                                                <dsp:param value="${regAddr}" name="address" />
                                                <dsp:oparam name="output">
                                                    <dsp:getvalueof var="isValid" param="isValid"/>
                                                    <c:choose>
                                                        <c:when test="${isValid && (!shipTo_POBoxOn && currentSiteId ne BedBathCanadaSite)}">
                                                            <div class="radio">
                                                            	<input type="hidden" value="${isValid}" name="isPOBox"/>
                                                            	<c:set value="true" var="isPOBox"/>
                                                                <dsp:input tabindex="${tabIndex}" id="radRegistryRegistrantAddress${counter}" type="radio"
                                                                        name="radRegistryRegistrantAddressName"
                                                                        value="${addressId}"
                                                                        bean="GiftRegistryFormHandler.regContactAddress" disabled="true" />
                                                            </div>
                                                            <c:set var="poBoxAddressesCount" value="${poBoxAddressesCount+1}" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:choose>
                                                                <c:when test="${step2FocusSet == false}">
                                                                    <c:set var="step2FocusSet" value="${true}" />
                                                                    <c:set var="focusRegABClass" value="step2FocusField" />
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <c:set var="focusRegABClass" value="" />
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <div class="radio">
                                                                <dsp:input tabindex="${tabIndex}" id="radRegistryRegistrantAddress${counter}" type="radio"
                                                                    name="radRegistryRegistrantAddressName"
                                                                    value="${addressId}"
                                                                    iclass="${focusRegABClass}"
                                                                    bean="GiftRegistryFormHandler.regContactAddress" checked="${regContactAddrRadioBtn}">
                                                                    <dsp:tagAttribute name="aria-checked" value="false"/>
                                                                    <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryRegistrantAddress errorradRegistryRegistrantAddressName"/>
                                                                </dsp:input>
                                                            </div>
                                                            <c:set var="regContactAddrRadioBtn" value="false" scope="page" />
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <c:set var="tabIndex" value="${tabIndex + 1}" /> 
                                                </dsp:oparam>
                                            </dsp:droplet>
                                            <%-- Check if Address is PO BOX Address --%>
                                            
                                            <div class="label">
                                                <label id="lblradRegistryRegistrantAddress" for="radRegistryRegistrantAddress${counter}">
                                                <span class="displayToggle">
                                                <c:choose>
                                                	<c:when test="${!(counter > 1) && (isWSAddressInAddressBook) && !(empty regVO.primaryRegistrant.contactAddress.addressLine1)}">
                                                		${regVO.primaryRegistrant.contactAddress.firstName} ${regVO.primaryRegistrant.contactAddress.lastName}
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
                    <c:when test="${(poBoxAddressesCount ge  profileAddressesCount) && regContactAddrRadioBtn}">
                        <dsp:input tabindex="${tabIndex}" id="radRegistryRegistrantAddressNew"
                            value="newPrimaryRegAddress" type="radio"
                            name="radRegistryRegistrantAddressName" checked="true"
                            bean="GiftRegistryFormHandler.regContactAddress">
                            <dsp:tagAttribute name="aria-checked" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryRegistrantAddressNew"/>
                        </dsp:input>
                    </c:when>
                    <c:otherwise>
                        <dsp:input tabindex="${tabIndex}" id="radRegistryRegistrantAddressNew"
                            value="newPrimaryRegAddress" type="radio"
                            checked="${isPOBox}"
                            name="radRegistryRegistrantAddressName" 
                            bean="GiftRegistryFormHandler.regContactAddress" >
                            <dsp:tagAttribute name="aria-checked" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryRegistrantAddressNew"/>
                        </dsp:input>
                    </c:otherwise>
                </c:choose>
                <c:set var="tabIndex" value="${tabIndex + 1}" /> 
            </div>
            <div class="label">
                <label id="lblradRegistryRegistrantAddressNew" for="radRegistryRegistrantAddressNew" class="newAddress">
                    <bbbl:label key="lbl_registrants_new_contact_addr" language ="${pageContext.request.locale.language}"/>
                </label>
            </div>
            <div class="clear"></div>
            <div class="subForm clearfix hidden validateFormWithQAS">
                <div class="inputField clearfix">
                    <label id="lbltxtRegistryRegistrantContactFirstName" for="txtRegistryRegistrantContactFirstName" class="marTop_10">
                        <bbbl:label key="lbl_registrants_firstname" language ="${pageContext.request.locale.language}"/> 
                        <span class="required">*</span>
			<span class="txtOffScreen"><bbbl:label key='lbl_enter_first_name' language="${pageContext.request.locale.language}" /></span>
                    </label>
                    <dsp:input tabindex="${tabIndex}" id="txtRegistryRegistrantContactFirstName"
                        type="text"
                        bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.firstName"
                        name="txtRegistryRegistrantContactFirstNameName"
                        beanvalue="Profile.firstName" disabled="true" >
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactFirstName errortxtRegistryRegistrantContactFirstName"/>
                    </dsp:input>
                    <c:set var="tabIndex" value="${tabIndex + 1}" /> 

                    <dsp:input id="txtRegistryRegistrantContactFirstNameHidden"
                        type="hidden"
                        name="txtRegistryRegistrantContactFirstNameHidden"
                        bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.firstName"
                        beanvalue="Profile.firstName"/>
                </div>

                <div class="inputField clearfix">
                    <label id="lbltxtRegistryRegistrantContactLastName" for="txtRegistryRegistrantContactLastName">
                        <bbbl:label key="lbl_registrants_lastname" language ="${pageContext.request.locale.language}"/>     
                        <span class="required">*</span>
			<span class="txtOffScreen"><bbbl:label key='lbl_enter_last_name' language="${pageContext.request.locale.language}" /></span>
                    </label>
                    
                    <dsp:input tabindex="${tabIndex}" id="txtRegistryRegistrantContactLastName"
                            type="text"
                            bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.lastName"
                            name="txtRegistryRegistrantContactLastNameName"
                            beanvalue="Profile.lastName" disabled="true">
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactLastName errortxtRegistryRegistrantContactLastName"/>
                    </dsp:input>
                    <c:set var="tabIndex" value="${tabIndex + 1}" /> 
                    
                    <dsp:input id="txtRegistryRegistrantContactLastNameHidden"
                            type="hidden"
                            name="txtRegistryRegistrantContactLastNameHidden"
                            bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.lastName"
                            beanvalue="Profile.lastName"/>
                </div>
                <c:choose>
                    <c:when test="${step2FocusSet == false && (poBoxAddressesCount ge  profileAddressesCount) && regContactAddrRadioBtn}">
                        <c:set var="step2FocusSet" value="${true}" />
                        <c:set var="focusRegABClass" value="step2FocusField" />
                    </c:when>
                    <c:otherwise>
                        <c:set var="focusRegABClass" value="" />
                    </c:otherwise>
                </c:choose>
                <div class="inputField clearfix" id="regContactAddrLine1">
                    <label id="lbltxtRegistryRegistrantContactAddress1" for="txtRegistryRegistrantContactAddress1">
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
                    <dsp:input tabindex="${tabIndex}" id="txtRegistryRegistrantContactAddress1"
                            type="text"
                            name="txtRegistryRegistrantContactAddress1Name"
                            bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine1" 
                            iclass="${iclassValue} required addressAlphanumericbasicpunc cannotStartWithWhiteSpace QASAddress1 ${focusRegABClass}" maxlength="30" >
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactAddress1 errortxtRegistryRegistrantContactAddress1"/>
                    </dsp:input>
                    <c:set var="tabIndex" value="${tabIndex + 1}" /> 
                </div>

                <div class="inputField clearfix" id="regContactAddrLine2">
                    <label id="lbltxtRegistryRegistrantContactAddress2" for="txtRegistryRegistrantContactAddress2">
                        <bbbl:label key="lbl_registrants_addr_line2" language ="${pageContext.request.locale.language}"/>
                    </label>
                    <dsp:input tabindex="${tabIndex}" id="txtRegistryRegistrantContactAddress2"
                                type="text"
                                name="txtRegistryRegistrantContactAddress2Name"
                                bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine2" iclass="${iclassValue} addressAlphanumericbasicpunc cannotStartWithWhiteSpace QASAddress2" maxlength="30" >
                        <dsp:tagAttribute name="aria-required" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactAddress2 errortxtRegistryRegistrantContactAddress2"/>
                    </dsp:input>
                    <c:set var="tabIndex" value="${tabIndex + 1}" /> 
                </div>

                <div class="inputField clearfix" id="regContactCity">
                    <label id="lbltxtRegistryRegistrantContactCity" for="txtRegistryRegistrantContactCity"> 
                        <bbbl:label key="lbl_registrants_city" language ="${pageContext.request.locale.language}"/>
                        <span class="required">*</span>
                    </label>
                    <dsp:input tabindex="${tabIndex}" id="txtRegistryRegistrantContactCity"
                                type="text" name="txtRegistryRegistrantContactCityName"
                                bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.city" iclass="required cannotStartWithWhiteSpace QASCityName" maxlength="25" >
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactCity errortxtRegistryRegistrantContactCity"/>
                    </dsp:input>
                    <c:set var="tabIndex" value="${tabIndex + 1}" /> 
                </div>

                <div class="inputSelect pushDown" id="regContactState">
                    <label id="lblselRegistryRegistrantContactState" for="selRegistryRegistrantContactState">
                        <dsp:getvalueof id="siteId" idtype="java.lang.String" param="siteId" />
                        <c:choose>
                            <c:when test="${siteId == BedBathCanadaSite}">
                                <bbbl:label key="lbl_registrants_statecanada" language ="${pageContext.request.locale.language}"/>
                            </c:when>
                            <c:otherwise>
                                <bbbl:label key="lbl_registrants_state" language ="${pageContext.request.locale.language}"/>
                            </c:otherwise>
                        </c:choose>
                        <span class="required">*</span>
                    </label>
                    
                    <dsp:select tabindex="${tabIndex}" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.state" id="selRegistryRegistrantContactState" name="selRegistryRegistrantContactStateName" iclass="uniform requiredValidation QASStateName required">
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblselRegistryRegistrantContactState errorselRegistryRegistrantContactState"/>
                        <dsp:droplet name="StateDroplet">
                        <dsp:param name="NoShowUSTerr" value="noShowOnRegistry" />
                            <dsp:oparam name="output">
                                <c:choose>
                                    <c:when test="${siteId == BedBathCanadaSite}">
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
                    <label id="errorselRegistryRegistrantContactState" for="selRegistryRegistrantContactState" generated="true" class="error block"></label>
					<label for="selRegistryRegistrantContactState" class="offScreen">
                        <dsp:getvalueof id="siteId" idtype="java.lang.String" param="siteId" />
                        <c:choose>
                            <c:when test="${siteId == BedBathCanadaSite}">
                                <bbbl:label key="lbl_registrants_statecanada" language ="${pageContext.request.locale.language}"/>
                            </c:when>
                            <c:otherwise>
                                <bbbl:label key="lbl_registrants_state" language ="${pageContext.request.locale.language}"/>
                            </c:otherwise>
                        </c:choose>
                        <span class="required">*</span>
                    </label>
                </div>

                <div class="inputField clearfix" id="regContactZip">
                    <label id="lbltxtRegistryRegistrantContactZip" for="txtRegistryRegistrantContactZip">
                        <c:choose>
                            <c:when test="${currentSiteId eq BedBathCanadaSite}">
                                <bbbl:label key="lbl_registrants_zip_ca" language ="${pageContext.request.locale.language}"/>
                            </c:when>
                            <c:otherwise>
                                <bbbl:label key="lbl_registrants_zip" language ="${pageContext.request.locale.language}"/>
                            </c:otherwise>
                        </c:choose>
                        <span class="required">*</span>
                    </label>
                    <c:choose>
                        <c:when test="${currentSiteId eq BedBathCanadaSite}">
                            <dsp:input tabindex="${tabIndex}" id="txtRegistryRegistrantContactZip"
                                type="text" name="txtRegistryRegistrantContactZipName"
                                bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.zip" iclass="required zipCA QASZipUS favStoreZip" >
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactZip errortxtRegistryRegistrantContactZip"/>
                            </dsp:input>
                        </c:when>
                        <c:otherwise>
                            <dsp:input tabindex="${tabIndex}" id="txtRegistryRegistrantContactZip"
                                type="text" name="txtRegistryRegistrantContactZipName"
                                bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.zip" iclass="required zipUS QASZipUS favStoreZip" >
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactZip errortxtRegistryRegistrantContactZip"/>
                            </dsp:input>
                        </c:otherwise>
                    </c:choose>
                    <c:set var="tabIndex" value="${tabIndex + 1}" /> 
                    
                    <input type="hidden" name="QASCountryName" id="QASCountryName"/>
		    		<dsp:input type="hidden" name="QASContactPoBoxFlag" bean="GiftRegistryFormHandler.contactPoBoxFlag" id="QASContactPoBoxFlag" iclass="QASPoBoxFlag" />
		    		<dsp:input type="hidden" name="QASContactPoBoxStatus" bean="GiftRegistryFormHandler.contactPoBoxStatus" id="QASContactPoBoxStatus" iclass="QASPoBoxStatus" />
                       
                </div>
                <div class="clear"></div>
            </div>
            <div class="clear"></div>
        </div>
        <div class="clear"></div>
    </fieldset>
</div>
<div class="clear"></div>
