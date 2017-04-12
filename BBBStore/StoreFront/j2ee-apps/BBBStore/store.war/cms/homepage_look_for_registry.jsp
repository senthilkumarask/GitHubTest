<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof id="currentSiteId" bean="Site.id" />
<div class="grid_6 omega">
    <div class="findARegistryForm clearfix">
        <div class="findARegistryFormTitle">
            <h2><bbbt:textArea key="txt_looking_for_registry_title" language ="${pageContext.request.locale.language}"/></h2>
            <p class="regFixedContent"><bbbt:textArea key="txt_registrants_enter_Info" language ="${pageContext.request.locale.language}"/></p>
        </div>
        <div class="findARegistryFormForm">
            <dsp:form id="findRegHome" action="${contextPath}/giftregistry/registry_search_guest.jsp" method="post" requiresSessionConfirmation="false">
                <div class="input textBox grid_2">
                    <c:set var="titleFirstName">
						<bbbl:label key="lbl_registrants_firstname" language="${pageContext.request.locale.language}" />
					</c:set>
                    <div class="text">
                        <%-- <label id="lblfirstNameReg" for="firstNameReg" title="${titleFirstName}"><bbbl:label key="lbl_registrants_firstname" language="${pageContext.request.locale.language}" /></label> --%>
                        <label id="lblfirstNameReg" class="txtOffScreen" for="firstNameReg"><bbbl:label key="lbl_regflyout_firstname" language ="${pageContext.request.locale.language}"/></label>
						<dsp:input id="firstNameReg" value="" name="firstNameReg" title="${titleFirstName}" type="text" bean="GiftRegistryFormHandler.registrySearchVO.firstName" >
                            <dsp:tagAttribute name="placeholder" value="${titleFirstName}"/>
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblfirstNameReg errorfirstNameReg"/>
                        </dsp:input>
                        <label id="errorfirstNameReg" for="firstNameReg" generated="true" class="error w110 block"></label>
                    </div>
                </div>
                <div class="input textBox grid_2">
					<c:set var="titleLastName">
						<bbbl:label key="lbl_registrants_lastname" language="${pageContext.request.locale.language}" />
					</c:set>
                    <div class="text">
                        <label id="lbllastNameReg" class="txtOffScreen" for="lastNameReg"><bbbl:label key="lbl_regflyout_lastname" language ="${pageContext.request.locale.language}"/></label>
                        <dsp:input id="lastNameReg" value="" name="lastNameReg" title="${titleLastName}" type="text" bean="GiftRegistryFormHandler.registrySearchVO.lastName" >
                        	<dsp:tagAttribute name="placeholder" value="${titleLastName}"/>
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbllastNameReg errorlastNameReg"/>
                        </dsp:input>
                        <label id="errorlastNameReg" for="lastNameReg" generated="true" class="error w110 block"></label>
                    </div>
                </div>
                <div class="input select grid_1">				
					<div class="text">
						<label id="lblstateName" class="txtOffScreen" for="stateName"><bbbl:label key="lbl_bridalbook_select_state" language ="${pageContext.request.locale.language}"/></label>
						<dsp:select bean="GiftRegistryFormHandler.registrySearchVO.state"
						name="stateReg" id="stateName" iclass="uniform">
						<c:choose>
						<c:when test="${currentSiteId eq BedBathCanadaSite}">
						<dsp:option>Province</dsp:option>
						</c:when>
						<c:otherwise>
						<dsp:option>State</dsp:option>
						</c:otherwise>
						</c:choose>
						
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array" bean="ProfileFormHandler.stateList" />
							<dsp:param name="elementName" value="statelist" />
							<dsp:oparam name="output">
								<dsp:getvalueof id="stateName" param="statelist.stateName" />
								<dsp:getvalueof id="stateCode" param="statelist.stateCode" />
								<dsp:option value="${stateCode}">
									<c:out value="${stateName}" />
								</dsp:option>
							</dsp:oparam>
						</dsp:droplet>
                   		<dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblstateName errorstateName"/>
					</dsp:select>
					</div>
					<div class="error"></div>
				</div>
                <dsp:input bean="GiftRegistryFormHandler.hidden" type="hidden" value="1" />
                <div class="formButtons grid_1">
                    
                    <div class="button button_secondary button_active">                        
                        
                        <c:set var="lbl_look_reg_submit_button">
                            <bbbl:label key='lbl_look_reg_submit_button' language='${pageContext.request.locale.language}' />
                        </c:set>
                        
                            <dsp:input id="btnFindRegistry" name="btnFindRegistry" value="${lbl_look_reg_submit_button}" type="submit" bean="GiftRegistryFormHandler.registrySearchFromHomePage" onclick="javascript:customLinkTracking('home page: search for registry button')">
                                
                                <dsp:tagAttribute name="aria-labelledby" value="btnFindRegistry"/>
                                <dsp:tagAttribute name="aria-label" value="Search for a gift Registry"/>
                               
                            </dsp:input>
                        
                    </div>
                </div>
            </dsp:form>
            <div class="clear"></div>
        </div>
    </div>
</div>
</dsp:page>