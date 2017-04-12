<dsp:getvalueof var="contactAddressLine1FromBean" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine1"/>
<dsp:getvalueof var="contactAddressLine2FromBean" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine2"/>
<dsp:getvalueof var="contactCityFromBean" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.city"/>
<dsp:getvalueof var="contactStateFromBean" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.state"/>
<dsp:getvalueof var="contactZipFromBean" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.zip"/>
<dsp:getvalueof var="formExceptions" bean="GiftRegistryFormHandler.formExceptions"/>
<c:set var="focusRegABClass" value="" />
<div class="favStoreZipWrapper registryForm marBottom_10">
    <div class="radioGroup ">
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
		<c:set var="addr_addressline1" />
		<c:set var="addr_addressline2" />
		<c:set var="addr_city" />
		<c:set var="addr_state" />
		<c:set var="addr_postalcode" />
        <%--  Variable to find count of profile addresses --%>
        <c:set var="profileAddressesCount" value="0" />
        
        <%--  Variable to find count of pobox addresses --%>
        <c:set var="poBoxAddressesCount" value="0" />

        <%--  Flag to check/selected radio --%>
        <c:set var="regContactAddrRadioBtn" value="true" scope="page" />
        
        <c:set var="shipTo_POBoxOn" scope="request"><bbbc:config key="shipTo_POBoxOn" configName="FlagDrivenFunctions"/></c:set>
		
		<c:choose>
			<c:when test="${not empty shipTo_POBoxOn &&  shipTo_POBoxOn}">
				<c:set var="iclassValue" value=""/>
			</c:when>
			<c:otherwise>
				<c:set var="iclassValue" value="poBoxAddNotAllowed"/>
			</c:otherwise>
		</c:choose>
                        
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
				<c:if test="${regVO.primaryRegistrant.contactAddress.addressLine1 ne'' && regVO.primaryRegistrant.contactAddress.addressLine1 ne null}">
                <c:if test="${!(isWSAddressInAddressBook)}">
                    <div class="row radioItem input ">
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
                                    <c:when test="${isValid}">
                                       <div class="small-1 columns radio">
                                            <dsp:input tabindex="${tabIndex}" id="radRegistryRegistrantAddress" iclass="${focusRegABClass}" type="radio" name="radRegistryRegistrantAddressName" value="oldRegistrantAddressFromWS" bean="GiftRegistryFormHandler.regContactAddress" checked="${regContactAddrRadioBtn}">
                                                <dsp:tagAttribute name="aria-checked" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryRegistrantAddress errorradRegistryRegistrantAddressName"/>
                                            </dsp:input>
                                        </div>
                                        <c:set var="poBoxAddressesCount" value="${poBoxAddressesCount+1}" />
                                        <c:set var="regContactAddrRadioBtn" value="false" scope="page" />
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

                                        <div class="small-1 columns radio">
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
                        
                        <div class="small-11 columns label">
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
                                  <span class="displayToggle"><span class="hidden">
                                               <dsp:valueof
                                    value="${regVO.primaryRegistrant.contactAddress.firstName}"  /> <dsp:valueof
                                    value="${regVO.primaryRegistrant.contactAddress.lastName}" /> 
                                       </span></span>
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
                                        <div class="row radioItem input ">
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
                                                        <c:when test="${isValid}">
                                                            <div class="small-1 columns radio">
                                                                <dsp:input tabindex="${tabIndex}" id="radRegistryRegistrantAddress${counter}" type="radio"
                                                                    name="radRegistryRegistrantAddressName"
                                                                    value="${addressId}"
                                                                    iclass="${focusRegABClass}"
                                                                    bean="GiftRegistryFormHandler.regContactAddress" checked="${regContactAddrRadioBtn}">
                                                                    <dsp:tagAttribute name="aria-checked" value="false"/>
                                                                    <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryRegistrantAddress errorradRegistryRegistrantAddressName"/>
                                                                </dsp:input>
                                                            </div>
                                                            <c:set var="poBoxAddressesCount" value="${poBoxAddressesCount+1}" />
							    							<c:set var="regContactAddrRadioBtn" value="false" scope="page" />
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
                                                            <div class="small-1 columns radio">
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
                                            
                                            <div class="small-11 columns label">
                                            <dsp:getvalueof var="address2Value" param="address.address2"/> 

											<label class="lblradRegistryRegistrantAddress" data-add1="<dsp:valueof param="address.address1"/>" data-city="<dsp:valueof param="address.city"/> " data-state="<dsp:valueof param="address.state"/>" data-zip="<dsp:valueof param="address.postalCode"/>" data-add2="<c:if test='${not empty address2Value}'><dsp:valueof param="address.address2"/></c:if>" for="radRegistryRegistrantAddress${counter}">
                                                <span class="displayToggle">
												<c:choose>
                                                	<c:when test="${!(counter > 1) && (isWSAddressInAddressBook) && !(empty regVO.primaryRegistrant.contactAddress.addressLine1)}">
                                                		<span class="hidden"> ${regVO.primaryRegistrant.contactAddress.firstName} ${regVO.primaryRegistrant.contactAddress.lastName} </span>                                                       
                                                	</c:when>
                                                	<c:otherwise>                                                    
                                                		<dsp:valueof param="address.firstName"/> <dsp:valueof param="address.lastName"/>
                                                	</c:otherwise>
                                                </c:choose>
                                                </span>
                                                    <dsp:valueof param="address.address1"/><br/>
                                                    <c:if test='${not empty address2Value}'><dsp:valueof param="address.address2"/> <br/></c:if>
                                                    <dsp:valueof param="address.city"/> 
                                                    <dsp:valueof param="address.state"/>
                                                    <dsp:valueof param="address.postalCode"/>
													
													<c:set var="addr_addressline1" ><dsp:valueof param="address.address1"/></c:set>
													<c:set var="addr_addressline2" ><dsp:valueof param="address.address2"/></c:set>
													<c:set var="addr_city" ><dsp:valueof param="address.city"/> </c:set>
													<c:set var="addr_state" ><dsp:valueof param="address.state"/></c:set>
													<c:set var="addr_postalcode" ><dsp:valueof param="address.postalCode"/></c:set>
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
        
        <div class="row radioItem input last ">
            <div class="small-1 columns radio">
                <c:choose>
                    <%-- If all the profile addresses were pobox addresses or there was not address in profile --%>
                    <c:when test="${(poBoxAddressesCount ge  profileAddressesCount) && regContactAddrRadioBtn}">
                        <dsp:input tabindex="${tabIndex}" id="radRegistryRegistrantAddressNew"
                            value="newPrimaryRegAddress" type="radio"
                            name="radRegistryRegistrantAddressName"
                            bean="GiftRegistryFormHandler.regContactAddress">
                            <dsp:tagAttribute name="aria-checked" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryRegistrantAddressNew"/>
                        </dsp:input>
                    </c:when>
                    <c:otherwise>
                        <dsp:input tabindex="${tabIndex}" id="radRegistryRegistrantAddressNew"
                            value="newPrimaryRegAddress" type="radio"
                            name="radRegistryRegistrantAddressName" 
                            bean="GiftRegistryFormHandler.regContactAddress" >
                            <dsp:tagAttribute name="aria-checked" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryRegistrantAddressNew"/>
                        </dsp:input>
                    </c:otherwise>
                </c:choose>
                <c:set var="tabIndex" value="${tabIndex + 1}" /> 
            </div>
            <div class="small-11 columns label">
                <label id="lblradRegistryRegistrantAddressNew" for="radRegistryRegistrantAddressNew" class="newAddress">
                    <bbbl:label key="lbl_registrants_new_contact_addr" language ="${pageContext.request.locale.language}"/>
                </label>
            </div>
            
            <div class='subForm hidden validateFormWithQAS'>
                <div class="inputField ">
                    <label id="lbltxtRegistryRegistrantContactFirstName" for="txtRegistryRegistrantContactFirstName" class="marTop_10">
                        <bbbl:label key="lbl_registrants_firstname" language ="${pageContext.request.locale.language}"/> 
                        <span class="required">*</span>
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

                <div class="inputField ">
                    <label id="lbltxtRegistryRegistrantContactLastName" for="txtRegistryRegistrantContactLastName">
                        <bbbl:label key="lbl_registrants_lastname" language ="${pageContext.request.locale.language}"/>     
                        <span class="required">*</span>
                    </label>
                    
                    <dsp:input tabindex="${tabIndex}" id="txtRegistryRegistrantContactLastName"
                            type="text"
                            bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.lastName"
                            name="txtRegistryRegistrantContactLastNameName_1"
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
                <div class="inputField " id="regContactAddrLine1">
                    <label id="lbltxtRegistryRegistrantContactAddress1" for="txtRegistryRegistrantContactAddress1">
                        <bbbl:label key="lbl_registrants_addr_line1" language ="${pageContext.request.locale.language}"/>
                        <span class="required">*</span>
                    </label>
					
				<c:choose>
                    <c:when test="${!(empty regVO.primaryRegistrant.contactAddress.addressLine1)}">
                        <dsp:input tabindex="${tabIndex}" id="txtRegistryRegistrantContactAddress1"
                            type="text" value="${regVO.primaryRegistrant.contactAddress.addressLine1}"
                            name="txtRegistryRegistrantContactAddress1Name_1"
                            bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine1" 
                            iclass="${iclassValue} required addressAlphanumericbasicpunc cannotStartWithWhiteSpace QASAddress1 ${focusRegABClass}" maxlength="30" >
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactAddress1 errortxtRegistryRegistrantContactAddress1"/>
                    </dsp:input>
                    </c:when>
                    <c:when test="${not empty formExceptions && not empty contactAddressLine1FromBean && contactAddressLine1FromBean ne ''}">
                        <dsp:input tabindex="${tabIndex}" id="txtRegistryRegistrantContactAddress1"
                            type="text" value="${contactAddressLine1FromBean}"
                            name="txtRegistryRegistrantContactAddress1Name_1"
                            bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine1" 
                            iclass="${iclassValue} required addressAlphanumericbasicpunc cannotStartWithWhiteSpace QASAddress1 ${focusRegABClass}" maxlength="30" >
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactAddress1 errortxtRegistryRegistrantContactAddress1"/>
                    </dsp:input>
                    </c:when>
                    <c:otherwise>
                        <dsp:input tabindex="${tabIndex}" id="txtRegistryRegistrantContactAddress1"
                            type="text" value="${addr_addressline1}"
                            name="txtRegistryRegistrantContactAddress1Name_1"
                            bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine1" 
                            iclass="poBoxAddNotAllowed required addressAlphanumericbasicpunc cannotStartWithWhiteSpace QASAddress1 ${focusRegABClass}" maxlength="30" >
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactAddress1 errortxtRegistryRegistrantContactAddress1"/>
                    </dsp:input>
                    </c:otherwise>
                </c:choose>
                    <c:set var="tabIndex" value="${tabIndex + 1}" /> 
                </div>

                <div class="inputField " id="regContactAddrLine2">
                    <label id="lbltxtRegistryRegistrantContactAddress2" for="txtRegistryRegistrantContactAddress2">
                        <bbbl:label key="lbl_registrants_addr_line2" language ="${pageContext.request.locale.language}"/>
                    </label>
					<c:choose>
						<c:when test="${!(empty regVO.primaryRegistrant.contactAddress.addressLine2)}">
							<dsp:input tabindex="${tabIndex}" id="txtRegistryRegistrantContactAddress2"
									type="text" value="${regVO.primaryRegistrant.contactAddress.addressLine2}"
									name="txtRegistryRegistrantContactAddress2Name_1"
									bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine2" iclass="${iclassValue} addressAlphanumericbasicpunc cannotStartWithWhiteSpace QASAddress2" maxlength="30" >
							<dsp:tagAttribute name="aria-required" value="false"/>
							<dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactAddress2 errortxtRegistryRegistrantContactAddress2"/>
						</dsp:input>
						</c:when>
						<c:when test="${not empty formExceptions && not empty contactAddressLine2FromBean && contactAddressLine2FromBean ne ''}">
							<dsp:input tabindex="${tabIndex}" id="txtRegistryRegistrantContactAddress2"
									type="text" value="${contactAddressLine2FromBean}"
									name="txtRegistryRegistrantContactAddress2Name_1"
									bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine2" iclass="${iclassValue} addressAlphanumericbasicpunc cannotStartWithWhiteSpace QASAddress2" maxlength="30" >
							<dsp:tagAttribute name="aria-required" value="false"/>
							<dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactAddress2 errortxtRegistryRegistrantContactAddress2"/>
						</dsp:input>
						</c:when>
						<c:otherwise>
							<dsp:input tabindex="${tabIndex}" id="txtRegistryRegistrantContactAddress2"
									type="text" value="${addr_addressline2}"
									name="txtRegistryRegistrantContactAddress2Name_1"
									bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine2" iclass="poBoxAddNotAllowed addressAlphanumericbasicpunc cannotStartWithWhiteSpace QASAddress2" maxlength="30" >
							<dsp:tagAttribute name="aria-required" value="false"/>
							<dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactAddress2 errortxtRegistryRegistrantContactAddress2"/>
						</dsp:input>
						</c:otherwise>
					</c:choose>
                    <c:set var="tabIndex" value="${tabIndex + 1}" /> 
                </div>

                <div class="inputField " id="regContactCity">
                    <label id="lbltxtRegistryRegistrantContactCity" for="txtRegistryRegistrantContactCity"> 
                        <bbbl:label key="lbl_registrants_city" language ="${pageContext.request.locale.language}"/>
                        <span class="required">*</span>
                    </label>
					
					<c:choose>
						<c:when test="${!(empty regVO.primaryRegistrant.contactAddress.city)}">
							 <dsp:input tabindex="${tabIndex}" id="txtRegistryRegistrantContactCity"
                                type="text" name="txtRegistryRegistrantContactCityName_1" value="${regVO.primaryRegistrant.contactAddress.city}"
                                bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.city" iclass="required cannotStartWithWhiteSpace QASCityName" maxlength="25" >
							<dsp:tagAttribute name="aria-required" value="true"/>
							<dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactCity errortxtRegistryRegistrantContactCity"/>
						</dsp:input>
						</c:when>
						<c:when test="${not empty formExceptions && not empty contactCityFromBean && contactCityFromBean ne ''}">
							 <dsp:input tabindex="${tabIndex}" id="txtRegistryRegistrantContactCity"
                                type="text" name="txtRegistryRegistrantContactCityName_1" value="${contactCityFromBean}"
                                bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.city" iclass="required cannotStartWithWhiteSpace QASCityName" maxlength="25" >
							<dsp:tagAttribute name="aria-required" value="true"/>
							<dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactCity errortxtRegistryRegistrantContactCity"/>
						</dsp:input>
						</c:when>
						<c:otherwise>
							 <dsp:input tabindex="${tabIndex}" id="txtRegistryRegistrantContactCity"
                                type="text" name="txtRegistryRegistrantContactCityName_1" value="${addr_city}"
                                bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.city" iclass="required cannotStartWithWhiteSpace QASCityName" maxlength="25" >
							<dsp:tagAttribute name="aria-required" value="true"/>
							<dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactCity errortxtRegistryRegistrantContactCity"/>
						</dsp:input>
						</c:otherwise>
					</c:choose>
                    <c:set var="tabIndex" value="${tabIndex + 1}" /> 
                </div>

                <div class="inputSelect pushDown" id="regContactState">
                    <label id="lblselRegistryRegistrantContactState" for="selRegistryRegistrantContactState">
                        <dsp:getvalueof id="siteId" idtype="java.lang.String" param="siteId" />
                        <c:choose>
                            <c:when test="${siteId == TBS_BedBathCanadaSite}">
                                <bbbl:label key="lbl_registrants_statecanada" language ="${pageContext.request.locale.language}"/>
                            </c:when>
                            <c:otherwise>
                                <bbbl:label key="lbl_registrants_state" language ="${pageContext.request.locale.language}"/>
                            </c:otherwise>
                        </c:choose>
                        <span class="required">*</span>
                    </label>

					<c:choose>
						<c:when test="${!(empty regVO.primaryRegistrant.contactAddress.state)}">
							<dsp:getvalueof value="${regVO.primaryRegistrant.contactAddress.state}" var="primaryRegistrantState" />
						</c:when>
						<c:when test="${not empty formExceptions && not empty contactStateFromBean && contactStateFromBean ne ''}">
							<dsp:getvalueof value="${contactStateFromBean}" var="primaryRegistrantState" />
						</c:when>
						<c:otherwise>
							<dsp:getvalueof value="${addr_state}" var="primaryRegistrantState" />							 
						</c:otherwise>
					</c:choose>
					
                    <dsp:select tabindex="${tabIndex}" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.state" id="selRegistryRegistrantContactState" name="selRegistryRegistrantContactStateName" iclass="uniform requiredValidation QASStateName required">
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblselRegistryRegistrantContactState errorselRegistryRegistrantContactState"/>
                        <dsp:droplet name="StateDroplet">
                        <dsp:param name="NoShowUSTerr" value="noShowOnRegistry" />
                            <dsp:oparam name="output">
                                <c:choose>
                                    <c:when test="${siteId == TBS_BedBathCanadaSite}">
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
                                        <c:choose>
                                        <c:when test="${primaryRegistrantState eq stateCode}">
                                        <dsp:option value="${stateCode}" selected="true">
                                            ${elementVal}
                                        </dsp:option>
                                        </c:when>
                                        <c:otherwise>
                                        <dsp:option value="${stateCode}">
                                        ${elementVal}
                                    </dsp:option>
                                        </c:otherwise>
                                        </c:choose>
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
                            <c:when test="${siteId == TBS_BedBathCanadaSite}">
                                <bbbl:label key="lbl_registrants_statecanada" language ="${pageContext.request.locale.language}"/>
                            </c:when>
                            <c:otherwise>
                                <bbbl:label key="lbl_registrants_state" language ="${pageContext.request.locale.language}"/>
                            </c:otherwise>
                        </c:choose>
                        <span class="required">*</span>
                    </label>
                </div>

                <div class="inputField " id="regContactZip">
                    <label id="lbltxtRegistryRegistrantContactZip" for="txtRegistryRegistrantContactZip">
                        <c:choose>
                            <c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
                                <bbbl:label key="lbl_registrants_zip_ca" language ="${pageContext.request.locale.language}"/>
								<c:set var="zipcaClass" value="zipCA"/>
                            </c:when>
                            <c:otherwise>
                                <bbbl:label key="lbl_registrants_zip" language ="${pageContext.request.locale.language}"/>
								<c:set var="zipcaClass" value="zipUS"/>
                            </c:otherwise>
                        </c:choose>
                        <span class="required">*</span>
                    </label>
					
					
					<c:choose>
						<c:when test="${!(empty regVO.primaryRegistrant.contactAddress.zip)}">
							 <dsp:input tabindex="${tabIndex}" id="txtRegistryRegistrantContactZip"
                                type="text" name="txtRegistryRegistrantContactZipName_1" value="${regVO.primaryRegistrant.contactAddress.zip}"
                                bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.zip" iclass="required ${zipcaClass} QASZipUS favStoreZip" >
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactZip errortxtRegistryRegistrantContactZip"/>
                            </dsp:input>
						</c:when>
						<c:when test="${not empty formExceptions && not empty contactZipFromBean && contactZipFromBean ne ''}">
							 <dsp:input tabindex="${tabIndex}" id="txtRegistryRegistrantContactZip"
                                type="text" name="txtRegistryRegistrantContactZipName_1" value="${contactZipFromBean}"
                                bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.zip" iclass="required ${zipcaClass} QASZipUS favStoreZip" >
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactZip errortxtRegistryRegistrantContactZip"/>
                            </dsp:input>
						</c:when>
						<c:otherwise>
							<dsp:input tabindex="${tabIndex}" id="txtRegistryRegistrantContactZip"
                                type="text" name="txtRegistryRegistrantContactZipName_1" value="${addr_postalcode}"
                                bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.zip" iclass="required ${zipcaClass} QASZipUS favStoreZip" >
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactZip errortxtRegistryRegistrantContactZip"/>
                            </dsp:input>						
						</c:otherwise>
					</c:choose>
                    
                    <c:set var="tabIndex" value="${tabIndex + 1}" /> 
                </div>
                
            </div>
            
        </div>
        
    </div>
</div>
