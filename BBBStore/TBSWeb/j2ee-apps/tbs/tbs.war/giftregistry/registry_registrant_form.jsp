<dsp:page>
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/ProfileExistCheckDroplet" />
    <dsp:importbean bean="/atg/multisite/Site"/>
    <dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryVODroplet" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/POBoxValidateDroplet" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/RegistryAddressOrderDroplet"/>
    <dsp:importbean bean="/com/bbb/selfservice/StateDroplet" /> 
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
    
    <dsp:getvalueof bean="SessionBean.registryTypesEvent" id="event">
        <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
        <c:choose>
            <c:when test="${event == 'BRD' || event == 'COM' }">
                <c:set var="eventState" value="mandatory" scope="page" />
            </c:when>
            <c:otherwise>
                <c:set var="eventState" value="optional" scope="page" />
            </c:otherwise>
        </c:choose>

        <dsp:getvalueof bean="Profile.email" var="profilemail" />
        <c:set var="coRegistrantemailLowerCase" value="${fn:toLowerCase(regVO.coRegistrant.email)}" scope="page" />
        <c:set var="profilemailLowerCase" value="${fn:toLowerCase(profilemail)}" scope="page" />
        <c:choose>
            <c:when test="${coRegistrantemailLowerCase eq profilemailLowerCase}">
                <c:set var="viewType" value="CO" scope="page" />
            </c:when>
            <c:otherwise>
                <c:set var="viewType" value="RE" scope="page" />
            </c:otherwise>
        </c:choose>

        <c:set var="TBS_BedBathCanadaSite">
            <bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
        </c:set>
    </dsp:getvalueof>
    
    <div class="steps step2">
        <%--Preview--%>
        <div id="step2Preview" class="row">
            <div class="small-12 large-8 columns alpha omega no-padding">
                <div class=" regsitryItem">
                    <h3>
                        <span class="regsitryItemNumber">2</span>
                        <span class="regsitryItemTitle arrowClosedSmall"><bbbl:label key='lbl_regcreate_reginfo_tellus' language="${pageContext.request.locale.language}" /> <a href="#edit2" id="step2EditLink" class="editRegistryLink" title='<bbbl:label key='lbl_regcreate_reginfo_edit' language="${pageContext.request.locale.language}" />'><bbbl:label key='lbl_regcreate_reginfo_edit' language="${pageContext.request.locale.language}" /></a></span>
                    </h3>
                </div>
            </div>
            <br/><br/><br/>
            
            <div id="step2Information" class="small-12 columns stepInformation form">
                <div class="small-6 columns no-padding">
                        <div class="entry pushDown">
                            <div class="field"><bbbl:label key="lbl_preview_registrants" language ="${pageContext.request.locale.language}"/></div>
                            <div class="value">
                                <div>
                                    <%-- <c:choose>
                                        <c:when test="${viewType == 'CO'}">
                                           <span>${regVO.primaryRegistrant.firstName}</span>&nbsp;<span>${regVO.primaryRegistrant.lastName}</span>&nbsp;<c:if test="${event == 'BA1'}"><span id="valRegistrantMaiden"></span></c:if>
                                        </c:when>
                                        <c:otherwise>
                                           <span>${regVO.primaryRegistrant.firstName}</span>&nbsp;<span>${regVO.primaryRegistrant.lastName}</span>&nbsp;<c:if test="${event == 'BA1'}"><span id="valRegistrantMaiden"></span></c:if>
                                        </c:otherwise>
                                    </c:choose> --%>
                                        <c:choose>
                                         <c:when test="${not empty regVO.primaryRegistrant.firstName}">
                                          <span>${regVO.primaryRegistrant.firstName}</span>&nbsp;<span>${regVO.primaryRegistrant.lastName}</span>&nbsp;<c:if test="${event == 'BA1'}"><span id="valRegistrantMaiden"></span></c:if>
                                         </c:when>
                                         <c:otherwise>
                                         <span><dsp:valueof bean="Profile.firstName" /></span>&nbsp;<span><dsp:valueof bean="Profile.LastName" /></span>&nbsp;<c:if test="${event == 'BA1'}"><span id="valRegistrantMaiden"></span></c:if>
                                         </c:otherwise>
                                         </c:choose>
                                </div>
                                <c:choose>
                                    <c:when test="${not empty regVO.primaryRegistrant.email}">
                                        ${regVO.primaryRegistrant.email}
                                    </c:when>
                                    <c:otherwise>
                                        <dsp:valueof bean="Profile.email" />
                                    </c:otherwise>
                                </c:choose>
                                <c:if test="${viewType == 'RE' || fn:length(regVO.coRegistrant.email) == 0}">
                                    <div id="registrantPrimaryPhoneWrapper">
                                        <span id="valRegistrantPrimaryPhone"></span>
                                    </div>
                                    <div id="registrantMobilePhoneWrapper">
                                        <span id="valRegistrantMobilePhone"></span>
                                    </div>
                                </c:if>    
                                <div class="value" id="valRegistrantShippingAddress"></div>
                            </div>
                        </div>
                </div>
                <div class="small-6 columns no-padding" id="sectionCoReg">
                        <div class="entry pushDown">
                            <div class="field"><c:choose><c:when test="${event == 'COL'}">&nbsp;</c:when><c:otherwise><bbbl:label key="lbl_preview_co_registrants" language ="${pageContext.request.locale.language}"/></c:otherwise></c:choose></div>
                            <div class="value">
                                <div>
                                    <span id="valCoRegistrantFirstName"></span>&nbsp;<span id="valCoRegistrantLastName"></span>&nbsp;<c:if test="${event == 'BA1'}"><span id="valCoRegistrantMaidenName"></span></c:if>
                                </div>
                                <div class="marTop_5" id="valCoRegistrantEmail"></div>
                            </div>
                        </div>
                </div>
                
            </div>
            
        </div>
        
        <%--Preview End--%>

        <div id="step2Form" class="row">
            <div id="step2FormPost" class="small-12 columns no-padding">
                <c:set var="step2FocusSet" value="${false}" />
                    <div class="row">
                    <div class="small-12 medium-6 columns alpha">
                            <fieldset class=" padBottom_20">
                                <legend class="formTitle"><bbbl:label key="lbl_preview_registrants" language ="${pageContext.request.locale.language}"/></legend>
                                <%--                      
                                <p class="noMar padBottom_5">
                                 (<bbbl:label key="lbl_preview_to_edit" language ="${pageContext.request.locale.language}"/> 
                                 <a href="../account/myaccount.jsp"><bbbl:label key="lbl_preview_my_account" language ="${pageContext.request.locale.language}"/></a>)
                                </p>
                                 --%>                        
                                <div class="registryForm padTop_5">
                                    <div class="entry padTop_5 padBottom_10 noMar inline">
                                        <div class="field bbPushDown inline"><bbbl:label key="lbl_registrants_firstname" language ="${pageContext.request.locale.language}"/></div>
                                        <div class="value inline">
                                        
                                                 <c:choose>
                                                 <c:when test="${not empty regVO.primaryRegistrant.firstName}">
                                                   <dsp:input type="text" iclass="required cannotStartWithSpecialChars alphabasicpunc step2FocusField"  name="regFirstNm" value="${regVO.primaryRegistrant.firstName}" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.firstName" />
                                                 </c:when>
                                                 <c:otherwise>
                                                 <dsp:valueof bean="Profile.firstName" /> 
                                                    <dsp:input type="hidden" name="regFirstNm" beanvalue="Profile.firstName" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.firstName" />
                                                 </c:otherwise>
                                                 </c:choose>
                                        </div>
                                    </div>
                                    <br/>
                                    <div class="entry padTop_5 padBottom_10 noMar inline">
                                        <div class="field bbPushDown inline"><bbbl:label key="lbl_registrants_lastname" language ="${pageContext.request.locale.language}"/></div>
                                        <div class="value inline">
                                        
                                                <c:choose>
                                                 <c:when test="${not empty regVO.primaryRegistrant.lastName}">
                                                    <dsp:input type="text" iclass="required cannotStartWithSpecialChars alphabasicpunc"  name="regLastNm" value="${regVO.primaryRegistrant.lastName}" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.lastName" />
                                                 </c:when>
                                                 <c:otherwise>
                                                   <dsp:valueof bean="Profile.LastName" />
                                                    <dsp:input type="hidden" name="regLastNm" beanvalue="Profile.lastName" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.lastName" />
                                                 </c:otherwise>
                                                 </c:choose>
                                               </div>
                                    </div>
                                    <br/>
                                    <c:set var="step2FocusSet" value="${true}" />
                                    <c:set var="focusCoRegClass" value="step2FocusField" />
                                    <c:choose>
                                        <c:when test="${event == 'COL'}">
                                            <c:set var="step2FocusSet" value="${false}" />
                                            <c:set var="focusCoRegClass" value="" />
                                        </c:when>
                                        <c:otherwise>
                                            <c:choose>
                                                <c:when test="${viewType == 'CO'}">
                                                    <c:set var="step2FocusSet" value="${false}" />
                                                    <c:set var="focusCoRegClass" value="" />
                                                </c:when>
                                                <c:when test="${viewType == 'RE' && fn:length(regVO.coRegistrant.email) > 0}">
                                                    <c:set var="step2FocusSet" value="${false}" />
                                                    <c:set var="focusCoRegClass" value="" />
                                                </c:when>
                                            </c:choose>
                                        </c:otherwise>
                                    </c:choose>
                                    <c:choose>
                                    <c:when test="${event == 'BA1'}">
                                        <c:set var="focusMNClass" value="" />
                                        <c:if test="${step2FocusSet == false}">
                                            <c:set var="focusMNClass" value="step2FocusField" />
                                            <c:set var="step2FocusSet" value="${true}" />
                                            <c:if test="${viewType == 'CO'}">    
                                                <c:set var="focusMNClass" value="" />
                                                <c:set var="step2FocusSet" value="${false}" />
                                            </c:if>
                                        </c:if>
                                        <c:set var="coviewtypeFieldState" value="false" scope="page" />
                                        <c:if test="${viewType == 'CO'}">    
                                            <c:set var="coviewtypeFieldState" value="true" scope="page" />
                                        </c:if>
                                        <div class="inputField padTop_5 padBottom_10">
                                            <label id="lbltxtRegistrantMaiden" for="txtRegistrantMaiden" class="noMarTop">
                                                <bbbl:label key="lbl_registrants_maiden_name" language ="${pageContext.request.locale.language}"/>
                                            </label>
                                            <dsp:input tabindex="5" id="txtRegistrantMaiden" type="text" disabled="${coviewtypeFieldState}" name="txtRegistrantMaidenNameName" value="${regVO.primaryRegistrant.babyMaidenName}" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.babyMaidenName" iclass="cannotStartWithSpecialChars alphabasicpunc ${focusMNClass}" maxlength="30">
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistrantMaiden errortxtRegistrantMaiden"/>
                                            </dsp:input>
                                        </div>
                                    </c:when>
                                    </c:choose>
                                    <div class="entry padTop_5 padBottom_10 noMar inline">
                                        <div class="field bbPushDown inline"><bbbl:label key="lbl_registrants_co_email" language ="${pageContext.request.locale.language}"/></div>
                                        <div class="value inline">
                                               <c:choose>
                                                 <c:when test="${not empty regVO.primaryRegistrant.email}">
                                                    ${regVO.primaryRegistrant.email}
                                                    <dsp:input id="txtRegEmail" type="hidden" name="txtRegEmail" value="${regVO.primaryRegistrant.email}" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.email" />
                                                 </c:when>
                                                 <c:otherwise>
                                                  <dsp:valueof bean="Profile.email" />
                                                    <dsp:input id="txtRegEmail" type="hidden" name="txtRegEmail" beanvalue="Profile.email" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.email" />
                                                 </c:otherwise>
                                                 </c:choose>
                                        </div>
                                    </div>
                                    <br/><br/>
                                    <dsp:getvalueof var="registryId" param="registryId"/>
                                    <c:choose>
                                        <c:when test="${viewType == 'RE' || fn:length(regVO.coRegistrant.email) == 0}">
                                            <c:set var="focusPHClass" value="" />
                                            <c:if test="${step2FocusSet == false}">
                                                <c:set var="step2FocusSet" value="${true}" />
                                                <c:set var="focusPHClass" value="step2FocusField" />
                                            </c:if>
                                            <div class="inputPhone phoneFieldWrap requiredPhone">
                                                <div class="phoneFields">
                                                    <%-- <legend class="phoneFieldLegend"><bbbl:label key='lbl_phonefield_phnumber' language='${pageContext.request.locale.language}'/></legend> --%>
                                                    <%-- <label id="lbltxtRegistrantPrimaryPhone" for="txtRegistrantPrimaryPhone"><bbbl:label key="lbl_registrants_co_primary_phone" language ="${pageContext.request.locale.language}"/> <span class="required">*</span></label> --%>
                                                    <input tabindex="6" placeholder="<bbbl:label key='lbl_phonefield_phnumber' language='${pageContext.request.locale.language}'/>" 
                                                        id="txtRegistrantPrimaryPhone" type="text" class="phone phoneField ${focusPHClass}" name="txtRegistrantPrimaryPhone" maxlength="10" 
                                                        aria-required="true" aria-labelledby="lbltxtRegistrantPrimaryPhone errorlbltxtRegistrantPrimaryPhone" /> 
                                                    <label id="errorlbltxtRegistrantPrimaryPhone" for="txtRegistrantPrimaryPhone" class="error" generated="true"></label>
    
                                                    <c:choose>
                                                        <c:when test="${not empty registryId}">
                                                            <dsp:input id="txtregistrantPhoneNumber" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.primaryPhone"  value="${regVO.primaryRegistrant.primaryPhone}" type="hidden" name="phone" maxlength="12" iclass="fullPhoneNum" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <dsp:input id="txtregistrantPhoneNumber" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.primaryPhone" beanValue="Profile.phoneNumber" type="hidden" name="phone" maxlength="12" iclass="fullPhoneNum" />
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>

                                            <div class="inputPhone phoneFieldWrap">
                                                <div class="phoneFields">
                                                <%-- <legend class="phoneFieldLegend"><bbbl:label key='lbl_phonefield_cellnumber' language='${pageContext.request.locale.language}'/></legend> --%>
                                                <%-- <label id="lbltxtRegistrantMobilePhone" for="txtRegistrantMobilePhone"><bbbl:label key="lbl_registrants_cell_phone" language ="${pageContext.request.locale.language}"/></label> --%>
                                                <input tabindex="9" id="txtRegistrantMobilePhone" type="text" placeholder="<bbbl:label key="lbl_registrants_cell_phone" language ="${pageContext.request.locale.language}"/>"
                                                    class="phone phoneField" name="txtRegistrantMobilePhone" maxlength="10" aria-required="false" aria-labelledby="lbltxtRegistrantMobilePhone errorlbltxtRegistrantMobilePhone" />                                        
                                                <label id="errorlbltxtRegistrantMobilePhone" for="txtRegistrantMobilePhone" class="error" generated="true"></label>
                                                <c:choose>
                                                    <c:when test="${not empty registryId}">
                                                        <dsp:input id="txtregistrantMobilePhoneNumber" iclass="fullPhoneNum" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.cellPhone" value="${regVO.primaryRegistrant.cellPhone}" type="hidden" name="cellphone" maxlength="12" />
                                                    </c:when>
                                                    <c:otherwise>
                                                        <dsp:input id="txtregistrantMobilePhoneNumber" iclass="fullPhoneNum" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.cellPhone" beanValue="Profile.mobileNumber" type="hidden" name="cellphone" maxlength="12" />
                                                    </c:otherwise>
                                                </c:choose>
                                                </div>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <dsp:input id="txtregistrantPhoneNumber" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.primaryPhone" value="${regVO.primaryRegistrant.primaryPhone}" type="hidden" name="phone" maxlength="12" iclass="fullPhoneNum" />
                                            <dsp:input id="txtregistrantMobilePhoneNumber" iclass="fullPhoneNum" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.cellPhone" value="${regVO.primaryRegistrant.cellPhone}" type="hidden" name="cellphone" maxlength="12" />
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </fieldset>
                    </div>
                    <div class="small-12 medium-6 columns omega hide-for-small">
                        <div class="registryNotice">
                            <bbbt:textArea key="txt_reg_create_registrant_form" language="${pageContext.request.locale.language}"/>
                        </div>
                    </div>
                    </div>
                    <div class="row">
                    <div class="small-12 medium-6 columns">
                    <fieldset>
                        <c:choose>
                            <c:when test="${event ne 'COL'}">
                                <legend class="formTitle"><bbbl:label key="lbl_preview_co_registrants" language ="${pageContext.request.locale.language}"/></legend>
                                <div class="registryForm">
                                    <div class="inputField" id="coRegFirstName">
                                        <label id="lbltxtCoRegistrantFirstName" for="txtCoRegistrantFirstName" class="noMarTop padTop_10"> 
                                            <bbbl:label key="lbl_registrants_co_firstname" language ="${pageContext.request.locale.language}"/>
                                            <c:if test="${eventState == 'mandatory'}">            
                                                <span class="required">*</span> 
                                            </c:if>
                                        </label>
                                        <c:choose>
                                            <c:when test="${fn:length(regVO.coRegistrant.email) > 0}">
                                                <dsp:getvalueof var="appid" bean="Site.id" />
                                                <dsp:droplet name="ProfileExistCheckDroplet">
                                                    <dsp:param name="siteId" value="${appid}"/>
                                                    <dsp:param value="${regVO.coRegistrant.email}" name="emailId" />
                                                    <dsp:oparam name="output">
                                                        <dsp:getvalueof var="coregExists" param="doesCoRegistrantExist"/>
                                                    </dsp:oparam>
                                                </dsp:droplet>
                                            
                                                <c:choose>
                                                    <c:when test="${coregExists == 'false'}">
                                                        <c:set var="coregLinked" value="false" scope="page" />                    
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:set var="coregLinked" value="true" scope="page" />
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="coregLinked" value="false" scope="page" />
                                            </c:otherwise>
                                        </c:choose>
                                       
                                            <c:choose>
                                            <c:when test="${event == 'BRD' || event == 'COM' }">
                                                <dsp:input tabindex="1" id="txtCoRegistrantFirstName" type="text" disabled="${coregLinked}" 
                                                    name="txtCoRegistrantFirstNameName" value="${regVO.coRegistrant.firstName}"
                                                    bean="GiftRegistryFormHandler.registryVO.coRegistrant.firstName" iclass="required cannotStartWithSpecialChars alphabasicpunc ${focusCoRegClass}" maxlength="30" >
                                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtCoRegistrantFirstName errortxtCoRegistrantFirstName"/>
                                                </dsp:input>
                                                    <dsp:input  type="hidden" 
                                                    name="txtCoRegistrantFirstNameName" value="${regVO.coRegistrant.firstName}"
                                                    bean="GiftRegistryFormHandler.registryVO.coRegistrant.firstName"/>
                                        
                                            </c:when>
                                            <c:otherwise>
                                                <dsp:input tabindex="1" id="txtCoRegistrantFirstName" type="text" disabled="${coregLinked}" 
                                                    name="txtCoRegistrantFirstNameName" value="${regVO.coRegistrant.firstName}"
                                                    bean="GiftRegistryFormHandler.registryVO.coRegistrant.firstName" iclass="optRequiredFirstName cannotStartWithSpecialChars alphabasicpunc ${focusCoRegClass}" maxlength="30" >
                                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtCoRegistrantFirstName errortxtCoRegistrantFirstName"/>
                                                </dsp:input>
                                                        <dsp:input  type="hidden" 
                                                    name="txtCoRegistrantFirstNameName" value="${regVO.coRegistrant.firstName}"
                                                    bean="GiftRegistryFormHandler.registryVO.coRegistrant.firstName"/>
                                            </c:otherwise>
                                        </c:choose>
                                          
                                    </div>
                                    <div class="inputField" id="coRegLastName">
                                        <label id="lbltxtCoRegistrantLastName" for="txtCoRegistrantLastName"> 
                                            <bbbl:label key="lbl_registrants_co_lastname" language ="${pageContext.request.locale.language}"/>
                                            <c:if test="${eventState == 'mandatory'}">            
                                                <span class="required">*</span> 
                                            </c:if>
                                        </label>
                                        
                                         <c:choose>
                                            <c:when test="${event == 'BRD' || event == 'COM'}">
                                                <dsp:input tabindex="2" id="txtCoRegistrantLastName" type="text" disabled="${coregLinked}" 
                                                    name="txtCoRegistrantLastNameName" value="${regVO.coRegistrant.lastName}"
                                                    bean="GiftRegistryFormHandler.registryVO.coRegistrant.lastName" iclass="required cannotStartWithSpecialChars alphabasicpunc" maxlength="30" >
                                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtCoRegistrantLastName errortxtCoRegistrantLastName"/>
                                                </dsp:input>
                                                <dsp:input type="hidden" 
                                                    name="txtCoRegistrantLastNameName" value="${regVO.coRegistrant.lastName}"
                                                    bean="GiftRegistryFormHandler.registryVO.coRegistrant.lastName" />                                                
                                            </c:when>
                                            <c:otherwise>
                                                <dsp:input tabindex="2" id="txtCoRegistrantLastName" type="text" disabled="${coregLinked}" 
                                                    name="txtCoRegistrantLastNameName" value="${regVO.coRegistrant.lastName}"
                                                    bean="GiftRegistryFormHandler.registryVO.coRegistrant.lastName" iclass="optRequiredLastName cannotStartWithSpecialChars alphabasicpunc" maxlength="30">
                                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtCoRegistrantLastName errortxtCoRegistrantLastName"/>
                                                </dsp:input>
                                                <dsp:input type="hidden" 
                                                    name="txtCoRegistrantLastNameName" value="${regVO.coRegistrant.lastName}"
                                                    bean="GiftRegistryFormHandler.registryVO.coRegistrant.lastName" />        
                                            </c:otherwise>
                                        </c:choose>      
                                    </div>
                                    <c:choose>
                                    <c:when test="${event == 'BA1'}">
                                        <div class="inputField" id="coRegMaidenName">
                                            <label id="lbltxtCoRegistrantMaidenName" for="txtCoRegistrantMaidenName"> 
                                                <bbbl:label key="lbl_registrants_maiden_name" language ="${pageContext.request.locale.language}"/>
                                            </label>
                                            <dsp:input tabindex="3" id="txtCoRegistrantMaidenName" type="text" 
                                                        name="txtCoRegistrantMaidenNameName" value="${regVO.coRegistrant.babyMaidenName}"
                                                        bean="GiftRegistryFormHandler.registryVO.coRegistrant.babyMaidenName" disabled="${coregLinked}" iclass="cannotStartWithSpecialChars alphabasicpunc" maxlength="30" >
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtCoRegistrantMaidenName errortxtCoRegistrantMaidenName"/>
                                            </dsp:input>
                                            <dsp:input  type="hidden" 
                                                        name="txtCoRegistrantMaidenNameName" value="${regVO.coRegistrant.babyMaidenName}"
                                                        bean="GiftRegistryFormHandler.registryVO.coRegistrant.babyMaidenName"/>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                      <dsp:input  type="hidden" name="txtCoRegistrantMaidenNameName" value="" bean="GiftRegistryFormHandler.registryVO.coRegistrant.babyMaidenName"/>
                                    </c:otherwise>
                                    </c:choose>
                                    <div class="inputField" id="coRegEmail">
                                        <label id="lbltxtCoRegistrantEmail" for="txtCoRegistrantEmail"> 
                                            <bbbl:label key="lbl_registrants_co_email" language ="${pageContext.request.locale.language}"/>
                                           
                                        </label>
                                                <dsp:input tabindex="4" id="txtCoRegistrantEmail" type="text"
                                                    name="txtCoRegistrantEmailName" value="${regVO.coRegistrant.email}"
                                                    bean="GiftRegistryFormHandler.registryVO.coRegistrant.email" iclass="email" disabled="${coregLinked}">
                                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtCoRegistrantEmail errortxtCoRegistrantEmail"/>
                                                </dsp:input>
                                                <dsp:input type="hidden"
                                                    name="txtCoRegistrantEmailName" value="${regVO.coRegistrant.email}"
                                                    bean="GiftRegistryFormHandler.registryVO.coRegistrant.email" />

                                        <div class="subtext marTop_5"><bbbt:textArea key='txt_regcreate_reginfo_emailreq_mngreg' language="${pageContext.request.locale.language}" /></div>
                                    </div>
                                </div>
                                <%-- Removed CoRegistrant Address Check : refer to coRegAddress.jsp --%>
                            </c:when>
                            <c:otherwise>
                             <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.coRegistrant.firstName"  name="txtCoRegistrantFirstNameName" value=""/>
                                <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.coRegistrant.lastName"   name="txtCoRegistrantLastNameName" value=""/>    
                                <dsp:input type="hidden"  bean="GiftRegistryFormHandler.registryVO.coRegistrant.email" name="txtCoRegistrantEmailName" value="" />
                                <dsp:input  type="hidden" name="txtCoRegistrantMaidenNameName" value="" bean="GiftRegistryFormHandler.registryVO.coRegistrant.babyMaidenName"/>
                             </c:otherwise>
                        </c:choose>
                    </fieldset>
                    </div>
                    <div class="small-12 medium-6 columns">
                    <fieldset>
                        <c:set var="tabIndex" value="12" /> 
                        <c:choose>
                            <c:when test="${viewType == 'RE' || fn:length(regVO.coRegistrant.email) == 0}">
                                <c:choose>
                                    <c:when test="${event == 'BA1'}">
                                        <legend class="formTitle"><bbbl:label key="lbl_registrants_contact_addr_reg" language ="${pageContext.request.locale.language}"/></legend>
                                    </c:when>
                                    <c:otherwise>
                                        <legend class="formTitle"><bbbl:label key="lbl_registrants_contact_addr" language ="${pageContext.request.locale.language}"/></legend>
                                    </c:otherwise>
                                </c:choose>
                                <%-- Radio Buttons for Registrant Addresses : Address book--%>
                       
                          <dsp:input  type="hidden" name="txtRegistryRegistrantContactFirstNameName" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.firstName" value="${regVO.primaryRegistrant.firstName}"/>
                                <dsp:input  type="hidden" name="txtRegistryRegistrantContactLastNameName" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.lastName" value="${regVO.primaryRegistrant.lastName}"/>
                                <dsp:input  type="hidden" name="txtRegistryRegistrantContactAddress1Name" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine1"  value="${regVO.primaryRegistrant.contactAddress.addressLine1}"/>    
                                <dsp:input  type="hidden" name="txtRegistryRegistrantContactAddress2Name" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine2" value="${regVO.primaryRegistrant.contactAddress.addressLine2}"/>
                                <dsp:input  type="hidden" name="txtRegistryRegistrantContactCityName" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.city"  value="${regVO.primaryRegistrant.contactAddress.city}"/>
                                <dsp:input  type="hidden" name="txtRegistryRegistrantContactStateName" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.state"  value="${regVO.primaryRegistrant.contactAddress.state}"/>
                                <dsp:input  type="hidden" name="txtRegistryRegistrantContactZipName" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.zip"  value="${regVO.primaryRegistrant.contactAddress.zip}"//>        
                                <%@ include file="frags/registry_registrant_address_book.jsp" %>
                            </c:when>
                            <c:otherwise>
                                <dsp:input  type="hidden" name="txtRegistryRegistrantContactFirstNameName" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.firstName" value="${regVO.primaryRegistrant.firstName}"/>
                                <dsp:input  type="hidden" name="txtRegistryRegistrantContactLastNameName" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.lastName" value="${regVO.primaryRegistrant.lastName}"/>
                                <dsp:input  type="hidden" name="txtRegistryRegistrantContactAddress1Name" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine1"  value="${regVO.primaryRegistrant.contactAddress.addressLine1}"/>    
                                <dsp:input  type="hidden" name="txtRegistryRegistrantContactAddress2Name" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine2" value="${regVO.primaryRegistrant.contactAddress.addressLine2}"/>
                                <dsp:input  type="hidden" name="txtRegistryRegistrantContactCityName" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.city"  value="${regVO.primaryRegistrant.contactAddress.city}"/>
                                <dsp:input  type="hidden" name="txtRegistryRegistrantContactStateName" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.state"  value="${regVO.primaryRegistrant.contactAddress.state}"/>
                                <dsp:input  type="hidden" name="txtRegistryRegistrantContactZipName" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.zip"  value="${regVO.primaryRegistrant.contactAddress.zip}"//>        
                            </c:otherwise>
                        </c:choose>
                    </fieldset>
                    </div>
                    </div>
                <c:if test="${empty regVO}">
                <div class="input submit-3 columns small pushDown">
                         <input tabindex="${tabIndex}" id="step2FormPostButton" class="small-3 columns button service" type="button" name="" value="<bbbl:label key='lbl_event_next' language='<c:out param="${language}"/>' />" role="button" aria-pressed="false" aria-labelledby="step2FormPostButton" />
                </div>
                <div class="columns">&nbsp;</div>
                </c:if>
            </div>
        </div>
    </div>
    
</dsp:page>
