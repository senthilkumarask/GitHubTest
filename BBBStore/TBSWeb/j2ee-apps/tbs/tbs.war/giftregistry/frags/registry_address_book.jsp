<div class="registryForm">
    <fieldset class="radioGroup">
        <legend>
            <c:choose>
                <c:when test="${event == 'BA1'}">
                    <bbbl:label key="lbl_registrants_contact_addr_reg" language ="${pageContext.request.locale.language}"/>
                </c:when>
                <c:otherwise>
                    <bbbl:label key="lbl_registrants_contact_addr" language ="${pageContext.request.locale.language}"/>
                </c:otherwise>
            </c:choose>
        </legend>
        
        <%-- Iterate on address book to display addresses in address book --%>
        <div class="radioItem input clearfix">
                <div class="small-1 large-2 columns radio no-padding-right">
                    <dsp:input id="radRegistryRegistrantAddress3" type="radio"
                            name="radRegistryRegistrantAddressName"
                            value="regShippingAddress"
                            bean="GiftRegistryFormHandler.regContactAddress" >
                        <dsp:tagAttribute name="aria-checked" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryRegistrantAddress3"/>
                    </dsp:input>
                </div>
                <div class="small-11 large-10 columns label no-padding">
                    <label id="lblradRegistryRegistrantAddress3" for="radRegistryRegistrantAddress3"> 
                    <span class="displayToggle"><%-- Address label --%></span>
                    </label>
                </div>
                 <div class="error"></div>
        </div>
        <%-- Iterate on address book to display addresses in address book --%>
        
        <div class="radioItem input last clearfix">
            <div class="small-1 large-2 columns radio no-padding-right">
                <dsp:input id="radRegistryRegistrantAddressNew"
                    value="newPrimaryRegAddress" type="radio"
                    name="radRegistryRegistrantAddressName" 
                    bean="GiftRegistryFormHandler.regContactAddress" >
                    <dsp:tagAttribute name="aria-checked" value="false"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryRegistrantAddressNew"/>
                </dsp:input>
            </div>          
            <div class="small-11 large-10 columns label no-padding">
                <label id="lblradRegistryRegistrantAddressNew" for="radRegistryRegistrantAddressNew">
                    <bbbl:label key="lbl_registrants_new_contact_addr" language ="${pageContext.request.locale.language}"/>
                    </label>
    
            </div>
            <div class="clear"></div>

            <div class="subForm clearfix validateFormWithQAS">
                    
                    <div class="inputField clearfix">
                        <label id="lbltxtRegistryRegistrantContactFirstName" for="txtRegistryRegistrantContactFirstName" class="marTop_10">
                                <bbbl:label key="lbl_registrants_firstname" language ="${pageContext.request.locale.language}"/> 
                                <span class="required">*</span>
                        </label>
                        <dsp:input id="txtRegistryRegistrantContactFirstName"
                            type="text"
                            name="txtRegistryRegistrantContactFirstNameName"
                            bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.firstName"
                            beanvalue="Profile.firstName" disabled="true" >
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactFirstName errortxtRegistryRegistrantContactFirstName"/>
                        </dsp:input>
                    </div>
                    

                    
                    <div class="inputField clearfix">
                        <label id="lbltxtRegistryRegistrantContactLastName" for="txtRegistryRegistrantContactLastName">
                        <bbbl:label key="lbl_registrants_lastname" language ="${pageContext.request.locale.language}"/>     
                            <span class="required">*</span>
                        </label>
                        
                        <dsp:input id="txtRegistryRegistrantContactLastName"
                                type="text"
                                name="txtRegistryRegistrantContactLastNameName"
                                bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.lastName"
                                beanvalue="Profile.lastName" disabled="true">
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactLastName errortxtRegistryRegistrantContactLastName"/>
                        </dsp:input>
                    </div>
                    
                    <div class="inputField clearfix" id="regContactAddrLine1">
                        <label id="lbltxtRegistryRegistrantContactAddress1" for="txtRegistryRegistrantContactAddress1">
                                <bbbl:label key="lbl_registrants_addr_line1" language ="${pageContext.request.locale.language}"/>
                                <span class="required">*</span>
                        </label>
                        <dsp:input id="txtRegistryRegistrantContactAddress1"
                                type="text"
                                name="txtRegistryRegistrantContactAddress1Name"
                                bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine1" 
                                iclass="poBoxAddNotAllowed required addressAlphanumericbasicpunc cannotStartWithWhiteSpace QASAddress1" maxlength="30" >
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactAddress1 errortxtRegistryRegistrantContactAddress1"/>
                        </dsp:input>
                    </div>
                    

                    
                    <div class="inputField clearfix" id="regContactAddrLine2">
                        <label id="lbltxtRegistryRegistrantContactAddress2" for="txtRegistryRegistrantContactAddress2">
                            <bbbl:label key="lbl_registrants_addr_line2" language ="${pageContext.request.locale.language}"/>
                        </label>
                        <dsp:input id="txtRegistryRegistrantContactAddress2"
                                    type="text"
                                    name="txtRegistryRegistrantContactAddress2Name"
                                    bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine2" iclass="poBoxAddNotAllowed addressAlphanumericbasicpunc cannotStartWithWhiteSpace QASAddress2" maxlength="30" >
                            <dsp:tagAttribute name="aria-required" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactAddress2 errortxtRegistryRegistrantContactAddress2"/>
                        </dsp:input>
                    </div>
                    

                    

                    <div class="inputField clearfix" id="regContactCity">
                        <label id="lbltxtRegistryRegistrantContactCity" for="txtRegistryRegistrantContactCity"> 
                    <bbbl:label key="lbl_registrants_city" language ="${pageContext.request.locale.language}"/>
                            <span class="required">*</span>
                        </label>
                        <dsp:input id="txtRegistryRegistrantContactCity"
                                    type="text" name="txtRegistryRegistrantContactCityName"
                                    bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.city" iclass="required cannotStartWithWhiteSpace QASCityName" maxlength="30" >
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactCity errortxtRegistryRegistrantContactCity"/>
                        </dsp:input>
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
                        
                        <dsp:select bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.state" id="selRegistryRegistrantContactState" name="selRegistryRegistrantContactStateName" iclass="uniform requiredValidation QASStateName">
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblselRegistryRegistrantContactState errorselRegistryRegistrantContactState"/>
                            <dsp:droplet name="StateDroplet">
                                <dsp:param name="NoShowUSTerr" value="noShowOnRegistry" />
                                <dsp:oparam name="output">
                                        <c:choose>
                                            <c:when test="${siteId == TBS_BedBathCanadaSite}">
                                                <dsp:option value="Select"><bbbl:label key='lbl_regcreate_reginfo_selectcanada' language="${pageContext.request.locale.language}" /></dsp:option>
                                            </c:when>
                                            <c:otherwise>
                                                <dsp:option value="Select"><bbbl:label key='lbl_regcreate_reginfo_select' language="${pageContext.request.locale.language}" /></dsp:option>
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
                        <label id="errorselRegistryRegistrantContactState" for="selRegistryRegistrantContactState" generated="true" class="error" style="display:none;"></label>
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
                    
                    <div class="inputField clearfix" id="regContactZip">
                        <label id="lbltxtRegistryRegistrantContactZip" for="txtRegistryRegistrantContactZip">
                        <c:choose>
                        <c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
                            <bbbl:label key="lbl_registrants_zip_ca" language ="${pageContext.request.locale.language}"/>
                            </c:when>
                            <c:otherwise>
                            <bbbl:label key="lbl_registrants_zip" language ="${pageContext.request.locale.language}"/>
                            </c:otherwise></c:choose>
                            
                             <span
                                class="required">*</span>
                        </label>
                        <c:choose>
                                            <c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
                                <dsp:input id="txtRegistryRegistrantContactZip"
                                    type="text" name="txtRegistryRegistrantContactZipName"
                                    bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.zip" iclass="required zipCA QASZipUS" >
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactZip errortxtRegistryRegistrantContactZip"/>
                                </dsp:input>
                            </c:when>
                            <c:otherwise>
                                <dsp:input id="txtRegistryRegistrantContactZip"
                                    type="text" name="txtRegistryRegistrantContactZipName"
                                    bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.zip" iclass="required zipUS QASZipUS" >
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactZip errortxtRegistryRegistrantContactZip"/>
                                </dsp:input>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    
            </div>
        </div>
    </fieldset>
</div>
