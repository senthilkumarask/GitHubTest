<dsp:page>
  <dsp:importbean bean="/atg/multisite/Site"/>
    <dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
    <dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>
    <dsp:getvalueof bean="SessionBean.registryTypesEvent" id="event">
        <dsp:getvalueof id="currentSiteId" bean="Site.id" />
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

        <c:set var="BedBathCanadaSite">
            <bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
        </c:set>
    </dsp:getvalueof>
    <div id="flex" class="clearfix grid_10 center"> 
   <dsp:getvalueof var="inputListMap" param="inputListMap"/>
         
                    <div id="flex1" class="grid_4 alpha omega clearfix">
                    <c:if test="${inputListMap['CoRegistrantFirstName'].isDisplayonForm || inputListMap['CoRegistrantLastName'].isDisplayonForm}">
                   <label class="padTop_10 padBottom_5" for="txtCoRegistrantFullName"> <bbbl:label key="lbl_co_reg" language ="${pageContext.request.locale.language}"/></label>
                   </c:if>
                   <div class="inputField clearfix">
                    <div class="inputField noMarRight clearfix alpha omega grid_4 ">  
                      <c:if test="${inputListMap['CoRegistrantFirstName'].isDisplayonForm || inputListMap['CoRegistrantLastName'].isDisplayonForm}">
                                                <c:set var="isRequired" value=""></c:set> 
                           <c:if test="${inputListMap['CoRegistrantFirstName'].isMandatoryOnCreate || inputListMap['CoRegistrantLastName'].isMandatoryOnCreate}">
                           <c:set var="isRequired" value="required"></c:set>
                           </c:if>

		  <dsp:getvalueof var="coRegFrstNameValueFromBean" bean="GiftRegistryFormHandler.registryVO.coRegistrant.firstName" />
		  <dsp:getvalueof var="coRegLstNameValueFromBean" bean="GiftRegistryFormHandler.registryVO.coRegistrant.lastName" />
          <c:choose>
          <c:when test="${not empty coRegFrstNameValueFromBean && not empty coRegLstNameValueFromBean}">
            <input type="text" id="txtCoRegistrantFullName" name="txtCoRegistrantFullName" class="${isRequired} " value="${coRegFrstNameValueFromBean} ${coRegLstNameValueFromBean}"/>
          </c:when>
          <c:otherwise>
            <input type="text" id="txtCoRegistrantFullName" placeholder="<bbbl:label key='lbl_reg_ph_fullname' language ='${pageContext.request.locale.language}'/>" name="txtCoRegistrantFullName" class="${isRequired}"/>
          </c:otherwise>
          </c:choose>
                    </c:if>
                    <c:if test="${inputListMap['CoRegistrantFirstName'].isDisplayonForm}">
                                                <c:set var="isRequired" value=""></c:set> 
                           <c:if test="${inputListMap['CoRegistrantFirstName'].isMandatoryOnCreate}">
                           <c:set var="isRequired" value="required"></c:set>
                           </c:if>
                    <div class="inputField noMarRight clearfix alpha omega grid_2 width130">
          <c:choose>
          <c:when test="${not empty regVO.coRegistrant.firstName && not empty regVO.coRegistrant.lastName}">
            <label class="txtOffScreen" id="lbltxtCoRegistrantFirstName" for="txtCoRegistrantFirstName">First Name</label>
            <dsp:input id="txtCoRegistrantFirstName" type="text" name="txtCoRegistrantFirstNameAltName"
                                                    bean="GiftRegistryFormHandler.registryVO.coRegistrant.firstName"  maxlength="30" value="${regVO.coRegistrant.firstName}">
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtCoRegistrantFirstName errortxtcoRegistrantFirstNameAlt"/>
                            <dsp:tagAttribute name="class" value="${isRequired} cannotStartWithSpecialChars alphabasicpunc escapeHTMLTag hidden borderrightnone "/>
                        </dsp:input>
          </c:when>
          <c:otherwise>
             <c:if test="${inputListMap['CoRegistrantLastName'].isDisplayonForm}">
          <c:set var="borderLastName" value="borderrightnone">
          </c:set>
          </c:if>
            <label class="txtOffScreen" id="lbltxtCoRegistrantFirstName" for="txtCoRegistrantFirstName">First Name</label>
            <dsp:input id="txtCoRegistrantFirstName" type="text" name="txtCoRegistrantFirstNameAltName"
                                                    bean="GiftRegistryFormHandler.registryVO.coRegistrant.firstName"  maxlength="30" >
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtCoRegistrantFirstName errortxtcoRegistrantFirstNameAlt"/>
                            <dsp:tagAttribute name="placeholder" value="First Name"/>
                            <dsp:tagAttribute name="class" value="${isRequired}  ${borderLastName} cannotStartWithSpecialChars alphabasicpunc escapeHTMLTag hidden"/>
                        </dsp:input>
          </c:otherwise>
          </c:choose>
                    
                        </div>
                          
                         
                           </c:if>
                           <c:if test="${inputListMap['CoRegistrantLastName'].isDisplayonForm}">
                               <c:set var="isRequired" value=""></c:set> 
                           <c:if test="${inputListMap['CoRegistrantLastName'].isMandatoryOnCreate}">
                           <c:set var="isRequired" value="required"></c:set>
                           </c:if>
                           <div class="inputField noMarRight clearfix alpha omega grid_2 width180">
              <c:choose>
              <c:when test="${not empty regVO.coRegistrant.firstName && not empty regVO.coRegistrant.lastName}">
                <dsp:input id="txtCoRegistrantLastName" type="text"    name="txtCoRegistrantLastName" bean="GiftRegistryFormHandler.registryVO.coRegistrant.lastName" maxlength="30" value="${regVO.coRegistrant.lastName}">
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lblCoRegistrantLastName errorlastNameReg"/>
                                    <dsp:tagAttribute name="class" value="${isRequired} cannotStartWithSpecialChars alphabasicpunc escapeHTMLTag hidden"/>
                                </dsp:input>
                <label class="txtOffScreen" id="lblCoRegistrantLastName" for="txtCoRegistrantLastName">last name</label>                
              </c:when>
              <c:otherwise>
                <dsp:input id="txtCoRegistrantLastName" type="text"    name="txtCoRegistrantLastName" bean="GiftRegistryFormHandler.registryVO.coRegistrant.lastName" maxlength="30" >
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lblCoRegistrantLastName errorlastNameReg"/>
                                    <dsp:tagAttribute name="placeholder" value="Last Name"/>
                                    <dsp:tagAttribute name="class" value="${isRequired} cannotStartWithSpecialChars alphabasicpunc  escapeHTMLTag hidden"/>
                                </dsp:input>
                <label class="txtOffScreen" id="lblCoRegistrantLastName" for="txtCoRegistrantLastName">last name</label>                                 
              </c:otherwise>
              </c:choose>
                            </div>
                        
                          </c:if>  
                            <c:if test="${inputListMap['CoRegistrantFirstName'].isDisplayonForm || inputListMap['CoRegistrantLastName'].isDisplayonForm}">
                                <c:if test="${event == 'BRD' || event == 'COM'}">
                                    <div id="flex2" class="grid_1 clearfix alpha omega radioPosLeft2">
                                        <div class="square-radio brideBox">
                                            <label id="lblBG" for="coRegbrideRadioBtn" aria-hidden="true"><span aria-hidden="true">B</span><br><span class="smlTxt"><bbbl:label key='lbl_reg_bride' language ='${pageContext.request.locale.language}'/></span></label>
                                            <dsp:input type="radio" iclass="square-radio--content" bean="GiftRegistryFormHandler.coRegBG"  value="B" >
                                                <dsp:tagAttribute name="aria-hidden" value="false"/>
                                                <dsp:tagAttribute name="id" value="coRegbrideRadioBtn"/>
                                                <dsp:tagAttribute name="aria-label" value="bride"/>
                                            </dsp:input>
                                        </div>
                                        <div class="square-radio brideBox">
                                             <label id="lblBG" for="coRegGroomRadioBtn" aria-hidden="true"><span aria-hidden="true"><span aria-hidden="true">G</span><br><span class="smlTxt"><bbbl:label key='lbl_reg_groom' language ='${pageContext.request.locale.language}'/></span></label> 
                                            <dsp:input type="radio" iclass="square-radio--content"  bean="GiftRegistryFormHandler.coRegBG"  value="G">
                                                <dsp:tagAttribute name="aria-hidden" value="false"/>
                                                <dsp:tagAttribute name="id" value="coRegGroomRadioBtn"/>
                                                <dsp:tagAttribute name="aria-label" value="groom"/>
                                            </dsp:input>
                                        </div>
                                    </div>
                                </c:if> 
                            </c:if>
                                                      
                           </div>
                          </div>
                          </div>
                         
                           <c:if test="${inputListMap['CoRegistrantEmail'].isDisplayonForm}"> 
                          <c:set var="coregemail"><bbbl:label key='lbl_reg_ph_coregemail' language ='${pageContext.request.locale.language}'/></c:set>
                         
                              <c:set var="isRequired" value=""></c:set> 
                           <c:if test="${inputListMap['CoRegistrantEmail'].isMandatoryOnCreate}">
                           <c:set var="isRequired" value="required"></c:set>
                           </c:if>  
                           <c:set var="coregemailPlaceHolder"><bbbl:label key='lbl_reg_ph_coregemail' language ='${pageContext.request.locale.language}'/></c:set>
                   <div id="flex3" class="grid_6">

          <c:choose>
            <c:when test="${event == 'BA1' }">
                 <div class="grid_4 omega clearfix">
            </c:when>
            <c:otherwise>
               <div class="grid_3 omega clearfix">
            </c:otherwise>
        </c:choose>
                     



                     <label id="lbltxtcoRegEmail" for="txtcoRegEmail" class="padTop_10 padBottom_5 widthHundred"><bbbl:label key="lbl_coReg_email" language ="${pageContext.request.locale.language}"/></label>        
           <c:choose>
          <c:when test="${not empty regVO.coRegistrant.email}">
            <dsp:input id="txtcoRegEmail" type="email" name="txtcoRegEmailName"
                                                    bean="GiftRegistryFormHandler.registryVO.coRegistrant.email" disabled="${coregLinked}" value="${regVO.coRegistrant.email}">
                            <dsp:tagAttribute name="aria-required" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtcoRegEmail errortxtcoRegEmail"/>
                            <dsp:tagAttribute name="class" value="${isRequired} email widthHundred"/>
                        </dsp:input>
          </c:when>
          <c:otherwise>
            <dsp:input id="txtcoRegEmail" type="email" name="txtcoRegEmailName"
                                                    bean="GiftRegistryFormHandler.registryVO.coRegistrant.email" disabled="${coregLinked}">
                            <dsp:tagAttribute name="aria-required" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtcoRegEmail errortxtcoRegEmail"/>
                            <dsp:tagAttribute name="placeholder" value=" ${coregemail}"/>
                            <dsp:tagAttribute name="class" value="${isRequired} email widthHundred"/>
                        </dsp:input>
          </c:otherwise>
          </c:choose>
                                    </div>
                                    
                                   <c:choose>
            <c:when test="${event ne 'BA1' }">
                  <div class="formRow grid_3 clearfix marLeft_10 omega alpha">
                    <div class="alpha fl marLeft_10 marTop_15 font11">
                   <bbbl:label key='lbl_reg_inviteCoreg' language ='${pageContext.request.locale.language}'/></div>
                </div>
            </c:when>
            <c:otherwise>
            </c:otherwise>
        </c:choose>
                     
                                    </div>
                             </c:if> 
 </div>
</dsp:page>
