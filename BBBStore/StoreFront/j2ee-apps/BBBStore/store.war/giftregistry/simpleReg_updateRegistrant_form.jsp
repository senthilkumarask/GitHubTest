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
	<c:if test="${event == 'BRD' || event == 'COM'}">
		<c:set var="isMandatoryField" value="isMandatoryField"/>
	</c:if>
    <div id="flex" class="clearfix grid_10 center"> 
   <dsp:getvalueof var="inputListMap" param="inputListMap"/>
         
                    <div id="flex1" class="grid_4 alpha omega clearfix">
					<label for="txtCoRegistrantFullName" class="padTop_10 padBottom_5"> <bbbl:label key="lbl_co_reg" language ="${pageContext.request.locale.language}"/></label>
					<c:choose>
						<c:when test="${event == 'BRD'}">
							<div class="inputField clearfix brdFields">
							<div id="regFullName" class="inputField noMarRight clearfix alpha omega grid_4">  
						</c:when>
						<c:otherwise>
							<div class="inputField clearfix">
							<div class="inputField noMarRight clearfix alpha omega grid_4">  
						</c:otherwise>
					</c:choose>

          <c:choose>
          <c:when test="${not empty regVO.coRegistrant.firstName && not empty regVO.coRegistrant.lastName}">
            <input type="text" id="txtCoRegistrantFullName" name="txtCoRegistrantFullName" class="${isMandatoryField}" value="${regVO.coRegistrant.firstName} ${regVO.coRegistrant.lastName}" autocomplete="off"/>
          </c:when>
          <c:otherwise>
            <input type="text" id="txtCoRegistrantFullName" placeholder="<bbbl:label key='lbl_reg_ph_fullname' language ='${pageContext.request.locale.language}'/>" name="txtCoRegistrantFullName" class="${isMandatoryField}" autocomplete="off"/>
          </c:otherwise>
          </c:choose>

                    <div class="inputField noMarRight clearfix alpha omega grid_2">
          <c:choose>
          <c:when test="${not empty regVO.coRegistrant.firstName}">
            <dsp:input tabindex="5" id="txtCoRegistrantFirstName" type="text" name="txtCoRegistrantFirstNameAltName"
                                                    bean="GiftRegistryFormHandler.registryVO.coRegistrant.firstName"  maxlength="30" value="${regVO.coRegistrant.firstName}" autocomplete="off">
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="txtcoRegistrantFirstNameAlt errortxtcoRegistrantFirstNameAlt"/>
                            <dsp:tagAttribute name="class" value="${isMandatoryField} cannotStartWithSpecialChars alphabasicpunc escapeHTMLTag hidden"/>
                        </dsp:input>
          </c:when>
          <c:otherwise>
            <dsp:input tabindex="5" id="txtCoRegistrantFirstName" type="text" name="txtCoRegistrantFirstNameAltName"
                                                    bean="GiftRegistryFormHandler.registryVO.coRegistrant.firstName"  maxlength="30" autocomplete="off">
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="txtcoRegistrantFirstNameAlt errortxtcoRegistrantFirstNameAlt"/>
                            <dsp:tagAttribute name="placeholder" value="First Name"/>
                            <dsp:tagAttribute name="class" value="${isMandatoryField} cannotStartWithSpecialChars alphabasicpunc escapeHTMLTag hidden"/>
                        </dsp:input>
          </c:otherwise>
          </c:choose>
                    
                        </div>
                          
                        <div class="inputField noMarRight clearfix alpha omega grid_2 width165">
              <c:choose>
              <c:when test="${not empty regVO.coRegistrant.lastName}">
                <dsp:input tabindex="6" id="txtCoRegistrantLastName" type="text"    name="txtCoRegistrantLastName" bean="GiftRegistryFormHandler.registryVO.coRegistrant.lastName" maxlength="30" value="${regVO.coRegistrant.lastName}" autocomplete="off">
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lastNameReg errorlastNameReg"/>
                                    <dsp:tagAttribute name="class" value="${isMandatoryField} cannotStartWithSpecialChars alphabasicpunc borderLeftnone borderRightnone escapeHTMLTag hidden"/>
                                </dsp:input>
              </c:when>
              <c:otherwise>
                <dsp:input tabindex="6" id="txtCoRegistrantLastName" type="text"    name="txtCoRegistrantLastName" bean="GiftRegistryFormHandler.registryVO.coRegistrant.lastName" maxlength="30" autocomplete="off">
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lastNameReg errorlastNameReg"/>
                                    <dsp:tagAttribute name="placeholder" value="Last Name"/>
                                    <dsp:tagAttribute name="class" value="${isMandatoryField} cannotStartWithSpecialChars alphabasicpunc borderLeftnone borderRightnone escapeHTMLTag hidden"/>
                                </dsp:input>
              </c:otherwise>
              </c:choose>
                            </div>

                          <c:if test="${event == 'BRD' || event == 'COM'}">
						  <c:if test="${not empty regVO.coRegBG}">
						  <c:set var="regBgBrideChecked" value=""/>
						  <c:set var="regBgGroomBtnChecked" value=""/>
							<c:choose>
							<c:when test="${regVO.coRegBG eq 'B'}">
								<c:set var="regBgBrideChecked" value="true"/>
							</c:when>
							<c:when test="${regVO.coRegBG eq 'G'}">
								<c:set var="regBgGroomBtnChecked" value="true"/>
							</c:when>
							<c:otherwise>
							</c:otherwise>
							</c:choose>
						  </c:if>
						  
                          <div id="flex2" class="grid_1 clearfix alpha omega radioPosLeft2">
                                           <div class="square-radio brideBox">
													  <label id="lblBG" for="coRegbrideRadioBtn"><span aria-hidden="true">B</span><br><span class="smlTxt"><bbbl:label key='lbl_reg_bride' language ='${pageContext.request.locale.language}'/></span></label>
                                                      <dsp:input tabindex="4" type="radio" iclass="square-radio--content" bean="GiftRegistryFormHandler.coRegBG"  value="B" checked="${regBgBrideChecked}">
                                                        <dsp:tagAttribute name="aria-checked" value="${regBgBrideChecked}"/>
														<dsp:tagAttribute name="aria-hidden" value="false"/>
														<dsp:tagAttribute name="id" value="coRegbrideRadioBtn"/>
														<dsp:tagAttribute name="aria-label" value="bride"/>
													  </dsp:input>  
                                                                                                      
                                           </div>
										   <div class="square-radio brideBox clearfix">
													  <label id="lblBG" for="coRegGroomRadioBtn"><span aria-hidden="true">G</span><br><span class="smlTxt"><bbbl:label key='lbl_reg_groom' language ='${pageContext.request.locale.language}'/></span></label>
													  <dsp:input tabindex="4" type="radio" iclass="square-radio--content"  bean="GiftRegistryFormHandler.coRegBG"  value="G" checked="${regBgGroomBtnChecked}">
														 <dsp:tagAttribute name="aria-checked" value="${regBgGroomBtnChecked}"/>
                                                         <dsp:tagAttribute name="aria-hidden" value="false"/>
														 <dsp:tagAttribute name="id" value="coRegGroomRadioBtn"/>
														 <dsp:tagAttribute name="aria-label" value="groom"/>
                                                      </dsp:input>
                                           </div>
						  </div>
                          </c:if> 
                                                      
                           </div>
                          </div>
                          </div>
                         
                          <c:set var="coregemail"><bbbl:label key='lbl_reg_ph_coregemail' language ='${pageContext.request.locale.language}'/></c:set>
                         
                           <c:set var="coregemailPlaceHolder"><bbbl:label key='lbl_reg_ph_coregemail' language ='${pageContext.request.locale.language}'/></c:set>
                   <div id="flex3" class="grid_6">

          
               <div class="grid_3 omega clearfix">
                     <label id="lbltxtcoRegEmail" for="txtcoRegEmail" class="padTop_10 padBottom_5 widthHundred"><bbbl:label key="lbl_coReg_email" language ="${pageContext.request.locale.language}"/></label>        
           <c:choose>
          <c:when test="${not empty regVO.coRegistrant.email}">
            <dsp:input tabindex="4" id="txtcoRegEmail" type="email" name="txtcoRegEmailName"
                                                    bean="GiftRegistryFormHandler.registryVO.coRegistrant.email" disabled="${coregLinked}" value="${regVO.coRegistrant.email}">
                            <dsp:tagAttribute name="aria-required" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtcoRegEmail errortxtcoRegEmail"/>
                            <dsp:tagAttribute name="class" value="email widthHundred"/>
                        </dsp:input>
          </c:when>
          <c:otherwise>
            <dsp:input tabindex="4" id="txtcoRegEmail" type="email" name="txtcoRegEmailName"
                                                    bean="GiftRegistryFormHandler.registryVO.coRegistrant.email" disabled="${coregLinked}">
                            <dsp:tagAttribute name="aria-required" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtcoRegEmail errortxtcoRegEmail"/>
                            <dsp:tagAttribute name="placeholder" value=" ${coregemail}"/>
                            <dsp:tagAttribute name="class" value="email widthHundred"/>
                        </dsp:input>
          </c:otherwise>
          </c:choose>
            </div>
                                    
                <div class="formRow grid_3 clearfix marLeft_10 omega alpha">
                   <div class="alpha fl marLeft_10 marTop_15 font11">
						<bbbl:label key='lbl_reg_inviteCoreg' language ='${pageContext.request.locale.language}'/>
				   </div>
                </div>
            </div>
 </div>
</dsp:page>
