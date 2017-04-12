<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<div class="grid_6 omega">
    <div class="findARegistryForm clearfix">
        <div class="findARegistryFormTitle">
            <h2><bbbt:textArea key="txt_looking_for_registry_title" language ="${pageContext.request.locale.language}"/></h2>
            <div class="grid_3">
                <p><bbbt:textArea key="txt_looking_for_registry_desc" language ="${pageContext.request.locale.language}"/></p>            
            </div>
            <div class="clear"></div>
        </div>
        <div class="findARegistryFormForm">
            <dsp:form id="findRegHome" action="${contextPath}/giftregistry/registry_search_guest.jsp" method="post" iclass="clearfix frmFindARegistry" requiresSessionConfirmation="false">
  
                <p><bbbt:textArea key="txt_registrants_enter_Info" language ="${pageContext.request.locale.language}"/></p> 
                <div class="input textBox grid_2"> 
                    
                    <div class="text">            
                         <c:set var="lbl_registrants_firstname">
							<bbbl:label key="lbl_registrants_firstname" language="${pageContext.request.locale.language}" />
						</c:set>
                        <dsp:input id="firstNameReg" value="" name="firstNameReg" title="${lbl_registrants_firstname}" type="text"    bean="GiftRegistryFormHandler.registrySearchVO.firstName">                            
                            <dsp:tagAttribute name="placeholder" value="${lbl_registrants_firstname}" />
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="firstNameReg lblfirstNameReg errorfirstNameReg"/>
                        </dsp:input>
                        <label id="errorfirstNameReg" for="firstNameReg" generated="true" class="error w110 block"></label>
                        <label id="lblfirstNameReg" for="firstNameReg" class="offScreen">${lbl_registrants_firstname}</label>
                    </div>
                </div>
                <div class="input textBox grid_2">
                    <div class="text">
                        <c:set var="lbl_registrants_lastname">
							<bbbl:label key="lbl_registrants_lastname" language="${pageContext.request.locale.language}" />
						</c:set>
                        <dsp:input id="lastNameReg" value="" name="lastNameReg" title="${lbl_registrants_lastname}" type="text" bean="GiftRegistryFormHandler.registrySearchVO.lastName">
                            <dsp:tagAttribute name="placeholder" value="${lbl_registrants_lastname}" />
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lastNameReg lbllastNameReg errorlastNameReg"/>
                        </dsp:input>
                        <label id="errorlastNameReg" for="lastNameReg" generated="true" class="error w110 block"></label>
                        <label id="lbllastNameReg" for="lastNameReg" class="offScreen">${lbl_registrants_lastname}</label>
                    </div>
                </div>
                <div class="input selectBox grid_1">				
					<div class="select">
						<dsp:select bean="GiftRegistryFormHandler.registrySearchVO.state" name="stateReg" id="stateName" iclass="selector_primary">
						<dsp:option>State</dsp:option>
						<dsp:droplet name="/com/bbb/selfservice/StateDroplet">
	                        <dsp:param name="NoShowUSTerr" value="noShowOnRegistry" />
							<dsp:oparam name="output">
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								   <dsp:param name="array" param="location" />
								   	<dsp:oparam name="output">
								   	<dsp:getvalueof param="element.stateName" id="stateName" />
								   	<dsp:getvalueof param="element.stateCode" id="stateCode" />
								   	<dsp:option value="${stateCode}" selected="${stateCode == sessState}">
										${stateName}
								   	</dsp:option>														
									</dsp:oparam>
								</dsp:droplet>
							</dsp:oparam>
						</dsp:droplet>
							
	                   		<dsp:tagAttribute name="aria-required" value="true"/>
	                        <dsp:tagAttribute name="aria-labelledby" value="lblstateName errorstateName"/>
						</dsp:select>
					</div>
					<div class="error"></div>
				</div>
                <dsp:input bean="GiftRegistryFormHandler.hidden" type="hidden" value="1" />
                <div class="formButtons"> 
                    
                    <div class="button button_primary button_active">
                         <c:set var="lbl_find_reg_submit_button">
                            <bbbl:label key='lbl_find_reg_submit_button' language='${pageContext.request.locale.language}' />
                        </c:set>                                           
                                            
                            <dsp:input type="submit" bean="GiftRegistryFormHandler.registrySearchFromHomePage" value="${lbl_find_reg_submit_button}" id="btnFindRegistry" name="btnFindRegistry" onclick="javascript:customLinkTracking('home page: search for registry button')">
                               
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