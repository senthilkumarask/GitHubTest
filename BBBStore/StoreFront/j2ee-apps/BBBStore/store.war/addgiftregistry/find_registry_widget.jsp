<dsp:page>
 <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
 <dsp:importbean bean="/atg/multisite/Site"/>
 <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/StatesLookup" /> 		
	<dsp:getvalueof var="findRegistryFormCount" param="findRegistryFormCount" />
	<%-- <dsp:getvalueof id="siteId" idtype="java.lang.String" param="siteId" /> --%>
	<dsp:getvalueof var="siteId" bean="Site.id" />
	<dsp:getvalueof var="findRegistryFormId" param="findRegistryFormId" />
	<dsp:getvalueof var="bridalException" param="bridalException" />
	<dsp:getvalueof var="isFlyout" param="flyout" />
	<dsp:getvalueof var="autoComplete" value="on"/>
	<dsp:getvalueof var="pageName" param="pageName" scope="page"/>
	<c:set var="enableRegSearchById" scope="request"><bbbc:config key="enableRegSearchById" configName="FlagDrivenFunctions" /></c:set>
	<c:if test ="${isFlyout eq 'true'}">
		<dsp:getvalueof var="autoComplete" value="off"/>
	</c:if>
	<c:set var="grid_2" value="grid_2 noMarLeft"/>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:form action="#" method="post" iclass="clearfix frmFindARegistry" id="${findRegistryFormId}" requiresSessionConfirmation="false">

	<c:choose>
		<c:when test="${(siteId == 'BuyBuyBaby') || (bridalException == 'true') || (siteId == 'BedBathCanada' && babyCAMode == 'true')}">
			<div class="input textBox ${grid_2}">
				<div class="label">
					<label id="lblfirstNameReg_${findRegistryFormCount}" for="firstNameReg_${findRegistryFormCount}"><bbbl:label key="lbl_regflyout_first_name_placeholder" language="${pageContext.request.locale.language}" /></label>
				</div>
				<c:choose>
					<c:when test="${enableRegSearchById == 'true'}">
						 <div class="text">
							<dsp:input type="text" autocomplete="${autoComplete}" id="firstNameReg_${findRegistryFormCount}" value="" name="firstNameRegWithRegistryId_${findRegistryFormCount}"  title="First Name" bean="GiftRegistryFormHandler.registrySearchVO.firstName" iclass="required cannotStartWithSpecialChars alphabasicpunc firstNameRegClass" maxlength="30" >
								<c:set var="lbl_regflyout_first_name_placeholder">
									<bbbl:label key="lbl_regflyout_first_name_placeholder" language="${pageContext.request.locale.language}" />
								</c:set>
								<dsp:tagAttribute name="placeholder" value="${lbl_regflyout_first_name_placeholder}"/>
		                        <dsp:tagAttribute name="aria-required" value="false"/>
		                        <dsp:tagAttribute name="aria-labelledby" value="errorfirstNameReg_${findRegistryFormCount}"/>
							</dsp:input>
							<label id="errorfirstNameReg_${findRegistryFormCount}" for="firstNameReg_${findRegistryFormCount}" class="error" generated="true"></label>
						</div>	
					</c:when>
					<c:otherwise>
						 <div class="text">
							<dsp:input type="text" autocomplete="${autoComplete}" title="First Name" id="firstNameReg_${findRegistryFormCount}" value="" name="firstNameReg" bean="GiftRegistryFormHandler.registrySearchVO.firstName" iclass="required cannotStartWithSpecialChars alphabasicpunc firstNameRegClass" maxlength="30" >
								<c:set var="lbl_regflyout_first_name_placeholder">
									<bbbl:label key="lbl_regflyout_first_name_placeholder" language="${pageContext.request.locale.language}" />
								</c:set>
								<dsp:tagAttribute name="placeholder" value="${lbl_regflyout_first_name_placeholder}"/>
		                        <dsp:tagAttribute name="aria-required" value="false"/>
		                        <dsp:tagAttribute name="aria-labelledby" value="errorfirstNameReg_${findRegistryFormCount}"/>
							</dsp:input>
							<label id="errorfirstNameReg_${findRegistryFormCount}" for="firstNameReg_${findRegistryFormCount}" class="error" generated="true"></label>
						</div>	
					</c:otherwise>
				</c:choose>	
							
			</div>
			<div class="input textBox ${grid_2} noMarBot">
				<div class="label">
					<label id="lbllastNameReg_${findRegistryFormCount}" for="lastNameReg_${findRegistryFormCount}"><bbbl:label key="lbl_regflyout_last_name_placeholder" language="${pageContext.request.locale.language}" /></label>
				</div>
				<c:choose>
					<c:when test="${enableRegSearchById == 'true'}">
						 <div class="text">
							<dsp:input type="text" autocomplete="${autoComplete}" title="Last Name" id="lastNameReg_${findRegistryFormCount}"  value="" name="lastNameRegWithRegistryId_${findRegistryFormCount}" bean="GiftRegistryFormHandler.registrySearchVO.lastName" iclass="required cannotStartWithSpecialChars alphabasicpunc lastNameRegClass" maxlength="30">
								<c:set var="lbl_regflyout_last_name_placeholder">
									<bbbl:label key="lbl_regflyout_last_name_placeholder" language="${pageContext.request.locale.language}" />
								</c:set>
								<dsp:tagAttribute name="placeholder" value="${lbl_regflyout_last_name_placeholder}"/>
		                        <dsp:tagAttribute name="aria-required" value="false"/>
		                        <dsp:tagAttribute name="aria-labelledby" value="errorlastNameReg_${findRegistryFormCount}"/>
							</dsp:input>
							<label id="errorlastNameReg_${findRegistryFormCount}" for="lastNameReg_${findRegistryFormCount}" class="error" generated="true"></label>
						</div>	
					</c:when>
					<c:otherwise>
						 <div class="text">
							<dsp:input type="text" autocomplete="${autoComplete}" title="Last Name" id="lastNameReg_${findRegistryFormCount}"  value="" name="lastNameReg" bean="GiftRegistryFormHandler.registrySearchVO.lastName" iclass="required cannotStartWithSpecialChars alphabasicpunc lastNameRegClass" maxlength="30">
								<c:set var="lbl_regflyout_last_name_placeholder">
									<bbbl:label key="lbl_regflyout_last_name_placeholder" language="${pageContext.request.locale.language}" />
								</c:set>
								<dsp:tagAttribute name="placeholder" value="${lbl_regflyout_last_name_placeholder}"/>
		                        <dsp:tagAttribute name="aria-required" value="false"/>
		                        <dsp:tagAttribute name="aria-labelledby" value="errorlastNameReg_${findRegistryFormCount}"/>
							</dsp:input>
							<label id="errorlastNameReg_${findRegistryFormCount}" for="lastNameReg_${findRegistryFormCount}" class="error" generated="true"></label>
						</div>	
					</c:otherwise>
				</c:choose>
							
			</div>
			<div class="input selectBox grid_1 noMarBot">				
			<c:choose>
<c:when test ="${(siteId == 'BedBathCanada') && (pageName == 'Bridal') && (findRegistryFormCount == '1')}">

<c:set var="selector_primary" value="selector"/>
</c:when>
<c:otherwise>
<c:set var="selector_primary" value="selector_primary"/>

</c:otherwise>
</c:choose>

			<div class="select">
					<dsp:select bean="GiftRegistryFormHandler.registrySearchVO.state"
						name="bbRegistryState" id="stateReg_0" iclass="${selector_primary}" nodefault="true">
						<dsp:tagAttribute name="aria-hidden" value="false"/>
						<dsp:droplet name="/com/bbb/selfservice/StateDroplet">
	                        <dsp:param name="NoShowUSTerr" value="noShowOnRegistry" />
							<dsp:oparam name="output">
								<dsp:option>
									<c:choose>
										<c:when test="${currentSiteId eq 'BedBathCanada'}">
										  <bbbl:label key="lbl_reg_search_results_all_states_canada"
										  language="${pageContext.request.locale.language}" />
										</c:when>
										 <c:otherwise>
											<bbbl:label key="lbl_reg_search_results_all_states_usa"
											language="${pageContext.request.locale.language}" />
										</c:otherwise>
								   </c:choose>
								</dsp:option>
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
					</dsp:select>
					
				</div>
				<div class="error"></div>
			</div>
			
		</c:when>
		<c:otherwise>
			<div class="input textBox grid_2">
				<%-- 
				<div class="label">
					<label id="lblfirstNameReg_${findRegistryFormCount}" for="firstNameReg_${findRegistryFormCount}"><bbbl:label key="lbl_regflyout_firstname" language ="${pageContext.request.locale.language}"/></label>
				</div>
				--%>
				 <c:choose>
					<c:when test="${enableRegSearchById == 'true'}">
						 <div class="text">
							<label id="lblfirstNameReg_${findRegistryFormCount}" class="txtOffScreen" for="firstNameReg_${findRegistryFormCount}">Enter</label>
							<dsp:input type="text" autocomplete="${autoComplete}" name="firstNameRegWithRegistryId_${findRegistryFormCount}" title="First Name" value="" id="firstNameReg_${findRegistryFormCount}" bean="GiftRegistryFormHandler.registrySearchVO.firstName" iclass="required cannotStartWithSpecialChars alphabasicpunc firstNameRegClass" maxlength="30" >
		                        <c:set var="lbl_regflyout_first_name_placeholder">
									<bbbl:label key="lbl_regflyout_first_name_placeholder" language="${pageContext.request.locale.language}" />
								</c:set>
								<dsp:tagAttribute name="placeholder" value="${lbl_regflyout_first_name_placeholder}"/>
		                        <dsp:tagAttribute name="aria-required" value="false"/>
		                        <dsp:tagAttribute name="aria-labelledby" value="errorfirstNameReg_${findRegistryFormCount}"/>
		                    </dsp:input>
						</div>
					</c:when>
					<c:otherwise>
						 <div class="text">
							<label id="lblfirstNameReg_${findRegistryFormCount}" class="txtOffScreen" for="firstNameReg_${findRegistryFormCount}">Enter</label>
							<dsp:input type="text" autocomplete="${autoComplete}" title="First Name" name="firstNameReg" value="" id="firstNameReg_${findRegistryFormCount}" bean="GiftRegistryFormHandler.registrySearchVO.firstName" iclass="required cannotStartWithSpecialChars alphabasicpunc firstNameRegClass" maxlength="30" >
		                        <c:set var="lbl_regflyout_first_name_placeholder">
									<bbbl:label key="lbl_regflyout_first_name_placeholder" language="${pageContext.request.locale.language}" />
								</c:set>
								<dsp:tagAttribute name="placeholder" value="${lbl_regflyout_first_name_placeholder}"/>
		                        <dsp:tagAttribute name="aria-required" value="false"/>
		                        <dsp:tagAttribute name="aria-labelledby" value="errorfirstNameReg_${findRegistryFormCount}"/>
		                    </dsp:input>
						</div>
					</c:otherwise>
				</c:choose>	
				
				<div class="error"></div>
			</div>
			<div class="input textBox grid_2 noMarBot">
				<%-- 
				<div class="label">
					<label id="lbllastNameReg_${findRegistryFormCount}" for="lastNameReg_${findRegistryFormCount}"><bbbl:label key="lbl_regflyout_lastname" language ="${pageContext.request.locale.language}"/></label>
				</div>
				--%>
				 <c:choose>
					<c:when test="${enableRegSearchById == 'true'}">
						 <div class="text">
							<label id="lbllastNameReg_${findRegistryFormCount}" class="txtOffScreen" for="lastNameReg_${findRegistryFormCount}">Enter</label>
							<dsp:input type="text" title="Last Name" autocomplete="${autoComplete}" name="lastNameRegWithRegistryId_${findRegistryFormCount}" value="" id="lastNameReg_${findRegistryFormCount}" bean="GiftRegistryFormHandler.registrySearchVO.lastName" iclass="required cannotStartWithSpecialChars alphabasicpunc lastNameRegClass" maxlength="30" >
		                        <c:set var="lbl_regflyout_last_name_placeholder">
									<bbbl:label key="lbl_regflyout_last_name_placeholder" language="${pageContext.request.locale.language}" />
								</c:set>
								<dsp:tagAttribute name="placeholder" value="${lbl_regflyout_last_name_placeholder}"/>
		                        <dsp:tagAttribute name="aria-required" value="false"/>
		                        <dsp:tagAttribute name="aria-labelledby" value="errorlastNameReg_${findRegistryFormCount}"/>
		                    </dsp:input>
						</div>
					</c:when>
					<c:otherwise>
						 <div class="text">
							<label id="lbllastNameReg_${findRegistryFormCount}" class="txtOffScreen" for="lastNameReg_${findRegistryFormCount}">Enter</label>
							<dsp:input type="text" autocomplete="${autoComplete}" title="Last Name" name="lastNameReg" value="" id="lastNameReg_${findRegistryFormCount}" bean="GiftRegistryFormHandler.registrySearchVO.lastName" iclass="required cannotStartWithSpecialChars alphabasicpunc lastNameRegClass" maxlength="30" >
		                        <c:set var="lbl_regflyout_last_name_placeholder">
									<bbbl:label key="lbl_regflyout_last_name_placeholder" language="${pageContext.request.locale.language}" />
								</c:set>
								<dsp:tagAttribute name="placeholder" value="${lbl_regflyout_last_name_placeholder}"/>
		                        <dsp:tagAttribute name="aria-required" value="false"/>
		                        <dsp:tagAttribute name="aria-labelledby" value="errorlastNameReg_${findRegistryFormCount}"/>
		                    </dsp:input>
						</div>
					</c:otherwise>
				</c:choose>	
				
				<div class="error"></div>
			</div>
			<div class="input selectBox grid_1 noMarBot">				
				<div class="select">
					<label id="lblstateReg_${findRegistryFormCount}" class="txtOffScreen" for="stateReg_${findRegistryFormCount}"><bbbl:label key="lbl_reg_search_state" language ="${pageContext.request.locale.language}"/></label>
				
					<dsp:select bean="GiftRegistryFormHandler.registrySearchVO.state" name="stateReg" id="stateReg_${findRegistryFormCount}"  iclass="uniform">
                        <dsp:droplet name="/com/bbb/selfservice/StateDroplet">
                        <dsp:param name="NoShowUSTerr" value="noShowOnRegistry" />
							<dsp:oparam name="output">
						<dsp:option>
							<c:choose>
								<c:when test="${siteId == 'BedBathCanada'}">
									<bbbl:label key="lbl_registrants_statecanada"
												language="${pageContext.request.locale.language}" />								  
								</c:when>
								<c:otherwise>
									<bbbl:label key="lbl_reg_search_state"
								  			  language="${pageContext.request.locale.language}" />	
								</c:otherwise>
						   </c:choose>
						</dsp:option>
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								   <dsp:param name="array" param="location" />
							<dsp:oparam name="output">
								   	<dsp:getvalueof param="element.stateName" id="stateName" />
								   	<dsp:getvalueof param="element.stateCode" id="stateCode" />
								<dsp:option value="${stateCode}">
										${stateName}
								</dsp:option>
							</dsp:oparam>
						</dsp:droplet>
							</dsp:oparam>
				   		</dsp:droplet>
                   		<dsp:tagAttribute name="aria-required" value="true"/>
				<dsp:tagAttribute name="aria-hidden" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblstateName errorstateName"/>
					</dsp:select>
					
				</div>
				<div class="error"></div>
			</div>
		</c:otherwise>
	</c:choose>
    <c:if test="${enableRegSearchById == 'true'}">
	<div class="registryFlagOn" >
	<div class="clear"></div>
  		<div class="or">Or</div>
  			<div class="input textBox grid_2 noMarBot">
				<div class="text">
					<label id="regId_1" class="txtOffScreen" for="findRegistryByRegNumberIdFlyout_${findRegistryFormCount}">Registry Number</label>					
					<dsp:input type="text" id="findRegistryByRegNumberIdFlyout_${findRegistryFormCount}" title="Registry Number" value="" name="findRegistryByRegNumberIdFlyout_${findRegistryFormCount}" iclass="findRegistryByRegNumber" bean="GiftRegistryFormHandler.registrySearchVO.registryId" >
						<dsp:tagAttribute name="placeholder" value="Registry Number"/>
					</dsp:input>
				</div>
				<div class="error"></div>
  		</div>
  	</div>
  	</c:if>
	<div class="formButtons">
	
		<dsp:getvalueof var="successURL" param="successURL" />
		<dsp:getvalueof var="errorURL" param="errorURL" />
		<dsp:getvalueof var="submitText" param="submitText" />
		<dsp:getvalueof var="handlerMethod" param="handlerMethod" />
		
		<c:choose>
			<c:when test="${enableRegSearchById == 'true'}">				 
				<dsp:input bean="GiftRegistryFormHandler.hidden" type="hidden" value="0"></dsp:input>
			</c:when>
			<c:otherwise>
				 <dsp:input bean="GiftRegistryFormHandler.hidden" type="hidden" value="3"></dsp:input>
			</c:otherwise>
		</c:choose>	
		

		
		
		<c:choose>
			<c:when test="${(siteId == 'BuyBuyBaby') || ((siteId == 'BedBathCanada') and (babyCAMode == 'true') and (findRegistryFormCount == '0'))}">
				<div class="button button_primary button_active">
					<dsp:input type="submit" value="${submitText}" name="btnFindRegistry_${findRegistryFormCount}" id="btnFindRegistry_${findRegistryFormCount}" bean="GiftRegistryFormHandler.${handlerMethod}" >
                        <dsp:tagAttribute name="aria-label" value="Search for a gift Registry"/>
                       
                    </dsp:input>
				</div>
			</c:when>
			<c:otherwise>
			
			<div class="button button_secondary button_active">
					<dsp:input type="submit" value="${submitText}" name="btnFindRegistry_${findRegistryFormCount}" id="btnFindRegistry_${findRegistryFormCount}" bean="GiftRegistryFormHandler.${handlerMethod}" onclick="_gaq.push(['_trackEvent', 'bridal', 'click go', 'find']);">
                        <dsp:tagAttribute name="aria-label" value="Search for a gift Registry"/>
                        
                    </dsp:input>
				</div>
			</c:otherwise>
		</c:choose>
	
	</div>
	<c:if test="${(siteId == 'BuyBuyBaby') && (enableRegSearchById == 'false')}">
	
		<p><bbbl:label key="lbl_enter_reg_info_search" language ="${pageContext.request.locale.language}"/></p>
  	</c:if>
</dsp:form>
</dsp:page>
