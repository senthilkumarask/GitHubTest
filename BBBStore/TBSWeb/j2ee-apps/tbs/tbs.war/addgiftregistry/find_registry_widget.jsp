<dsp:page>
 <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
 <dsp:importbean bean="/atg/multisite/Site"/>
 <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
 <dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:getvalueof var="transient" bean="Profile.transient" />
    <dsp:importbean bean="/com/bbb/commerce/browse/droplet/StatesLookup" />
 		
	<dsp:getvalueof var="findRegistryFormCount" param="findRegistryFormCount" />
	<%-- <dsp:getvalueof id="siteId" idtype="java.lang.String" param="siteId" /> --%>
	<dsp:getvalueof var="siteId" bean="Site.id" />
	<dsp:getvalueof var="findRegistryFormId" param="findRegistryFormId" />
	<dsp:getvalueof var="bridalException" param="bridalException" />
	<dsp:getvalueof var="isFlyout" param="flyout" />
	<dsp:getvalueof var="autoComplete" value="on"/>
	<dsp:getvalueof var="largeClass" param="largeClass" />
	<dsp:getvalueof var="pageName" param="pageName" scope="page"/>
	<c:if test ="${isFlyout eq 'true'}">
		<dsp:getvalueof var="autoComplete" value="off"/>
	</c:if>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<c:if test='${findRegistryFormId != "frmRegInfo"}'>
	<c:if test="${empty largeClass}">
		<c:set var="largeClass">large-5</c:set>
	</c:if>
</c:if>
<c:if test='${!transient}'>
	<c:set var="loggedIn">true</c:set>
</c:if>
<dsp:form action="#" method="post" iclass="small-12 columns frmFindARegistry no-padding" id="${findRegistryFormId}">

	<c:choose>
		<c:when test="${(siteId == 'TBS_BuyBuyBaby') || (bridalException == 'true') || (siteId == 'TBS_BedBathCanada' && babyCAMode == 'true')}">
			<c:choose>
				<c:when test="${loggedIn eq 'true'}">
					<div class="input textBox small-12 columns no-padding">
				</c:when>
				<c:otherwise>
					<div class="input textBox small-12 large-5 columns no-padding">
				</c:otherwise>
			</c:choose>
				<div class="text">
					<dsp:input type="text" autocomplete="${autoComplete}" id="firstNameReg_${findRegistryFormCount}" value="" name="firstNameReg" bean="GiftRegistryFormHandler.registrySearchVO.firstName" iclass="required cannotStartWithSpecialChars alphabasicpunc" maxlength="30" >
						<c:set var="lbl_regflyout_first_name_placeholder">
							<bbbl:label key="lbl_regflyout_first_name_placeholder" language="${pageContext.request.locale.language}" />
						</c:set>
						<dsp:tagAttribute name="placeholder" value="${lbl_regflyout_first_name_placeholder}"/>
                        <dsp:tagAttribute name="aria-required" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblfirstNameReg_${findRegistryFormCount} errorfirstNameReg_${findRegistryFormCount}"/>
					</dsp:input>
				</div>				
			</div>
			<c:choose>
				<c:when test="${loggedIn eq 'true'}">
					<div class="input textBox small-12 columns no-padding">
				</c:when>
				<c:otherwise>
					<div class="input textBox small-12 large-4 columns">
				</c:otherwise>
			</c:choose>
				<div class="text">
					<dsp:input type="text" autocomplete="${autoComplete}" id="lastNameReg_${findRegistryFormCount}"  value="" name="lastNameReg" bean="GiftRegistryFormHandler.registrySearchVO.lastName" iclass="required cannotStartWithSpecialChars alphabasicpunc" maxlength="30">
						<c:set var="lbl_regflyout_last_name_placeholder">
							<bbbl:label key="lbl_regflyout_last_name_placeholder" language="${pageContext.request.locale.language}" />
						</c:set>
						<dsp:tagAttribute name="placeholder" value="${lbl_regflyout_last_name_placeholder}"/>
                        <dsp:tagAttribute name="aria-required" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbllastNameReg_${findRegistryFormCount} errorlastNameReg_${findRegistryFormCount}"/>
					</dsp:input>
				</div>				
			</div>
			<c:choose>
				<c:when test="${loggedIn eq 'true'}">
					<div class="input selectBox small-12 large-6 columns no-padding">	
				</c:when>
				<c:otherwise>
					<div class="input selectBox small-12 large-3 columns no-padding">	
				</c:otherwise>
			</c:choose>				
			<c:choose>
<c:when test ="${(siteId == 'TBS_BedBathCanada') && (pageName == 'Bridal') && (findRegistryFormCount == '1')}">

<c:set var="selector_primary" value="selector"/>
</c:when>
<c:otherwise>
<c:set var="selector_primary" value="selector_primary"/>

</c:otherwise>
</c:choose>

				<div class="select">
					<dsp:select bean="GiftRegistryFormHandler.registrySearchVO.state"
						name="bbRegistryState" id="stateName" iclass="${selector_primary}" nodefault="true">
						<dsp:option>
							<c:choose>
								<c:when test="${siteId == 'TBS_BedBathCanada'}">
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
			
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${loggedIn eq 'true'}">
					<div class="input textBox small-12 columns no-padding">
				</c:when>
				<c:otherwise>
					<div class="input textBox small-12 large-5 columns no-padding">
				</c:otherwise>
			</c:choose>

			
				<div class="text">
					<dsp:input type="text" autocomplete="${autoComplete}" name="firstNameReg" value="" id="firstNameReg_${findRegistryFormCount}" bean="GiftRegistryFormHandler.registrySearchVO.firstName" iclass="required cannotStartWithSpecialChars alphabasicpunc" maxlength="30" >
                        <c:set var="lbl_regflyout_first_name_placeholder">
							<bbbl:label key="lbl_regflyout_first_name_placeholder" language="${pageContext.request.locale.language}" />
						</c:set>
						<dsp:tagAttribute name="placeholder" value="${lbl_regflyout_first_name_placeholder}"/>
                        <dsp:tagAttribute name="aria-required" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblfirstNameReg_${findRegistryFormCount} errorfirstNameReg_${findRegistryFormCount}"/>
                    </dsp:input>
				</div>
				<div class="error"></div>
			</div>
			<c:choose>
				<c:when test="${loggedIn eq 'true'}">
					<div class="input textBox small-12 columns no-padding">
				</c:when>
				<c:otherwise>
					<div class="input textBox small-12 large-4 columns">
				</c:otherwise>
			</c:choose>
				<div class="text">
					<dsp:input type="text" autocomplete="${autoComplete}" name="lastNameReg" value="" id="lastNameReg_${findRegistryFormCount}" bean="GiftRegistryFormHandler.registrySearchVO.lastName" iclass="required cannotStartWithSpecialChars alphabasicpunc" maxlength="30" >
                        <c:set var="lbl_regflyout_last_name_placeholder">
							<bbbl:label key="lbl_regflyout_last_name_placeholder" language="${pageContext.request.locale.language}" />
						</c:set>
						<dsp:tagAttribute name="placeholder" value="${lbl_regflyout_last_name_placeholder}"/>
                        <dsp:tagAttribute name="aria-required" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbllastNameReg_${findRegistryFormCount} errorlastNameReg_${findRegistryFormCount}"/>
                    </dsp:input>
				</div>
				<div class="error"></div>
			</div>
			<c:choose>
				<c:when test="${loggedIn eq 'true'}">
					<div class="input selectBox small-12 large-6 columns no-padding">	
				</c:when>
				<c:otherwise>
					<div class="input selectBox small-12 large-3 columns no-padding">	
				</c:otherwise>
			</c:choose>
						
				<div class="select">
				
					<dsp:select bean="GiftRegistryFormHandler.registrySearchVO.state" name="stateReg" id="stateReg"  iclass="uniform">
                        <dsp:droplet name="/com/bbb/selfservice/StateDroplet">
                        <dsp:param name="NoShowUSTerr" value="noShowOnRegistry" />
							<dsp:oparam name="output">
						<dsp:option>
							<c:choose>
								<c:when test="${siteId == 'TBS_BedBathCanada'}">
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
                        <dsp:tagAttribute name="aria-labelledby" value="lblstateName errorstateName"/>
					</dsp:select>
					
				</div>
				<div class="error"></div>
			</div>
		</c:otherwise>
	</c:choose>
	<div class="input selectBox small-12 columns no-padding event-type"> 
		<div class="select">
			<select name="eventType" autocomplete="off">
				  <option value=""> Event Type </option>
				  <option value="AA"> AA-Armed Forces of Americas </option> 
		    </select>
		</div> <div class="error"></div> 
	</div>
	<c:choose>
		<c:when test="${loggedIn eq 'true'}">
			<div class="formButtons small-12 large-5 columns ">
		</c:when>
		<c:otherwise>		 
			<div class="input large-2 large-offset-9 small-12 columns no-padding m-top">	
		</c:otherwise>
	</c:choose>
	
	
		<dsp:getvalueof var="successURL" param="successURL" />
		<dsp:getvalueof var="errorURL" param="errorURL" />
		<dsp:getvalueof var="submitText" param="submitText" />
		<dsp:getvalueof var="handlerMethod" param="handlerMethod" />
		
		

		<dsp:input bean="GiftRegistryFormHandler.hidden" type="hidden" value="8"></dsp:input>
		
		<c:choose>
			<c:when test="${(siteId == 'TBS_BuyBuyBaby') || ((siteId == 'TBS_BedBathCanada') and (babyCAMode == 'true') and (findRegistryFormCount == '0'))}">
					<c:if test="${(siteId == 'TBS_BuyBuyBaby')}">
						<c:set var="additionalBtnStyle" value="service"/>
					</c:if>
					<dsp:input type="submit" iclass="button tiny small-12 ${additionalBtnStyle}" value="${submitText}" name="btnFindRegistry_${findRegistryFormCount}" id="btnFindRegistry_${findRegistryFormCount}" bean="GiftRegistryFormHandler.${handlerMethod}" >
                        <dsp:tagAttribute name="aria-pressed" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="btnFindRegistry_${findRegistryFormCount}"/>
                        <dsp:tagAttribute name="role" value="button"/>
                    </dsp:input>
			</c:when>
			<c:otherwise>
					<dsp:input type="submit" iclass="button tiny small-12" value="${submitText}" name="btnFindRegistry_${findRegistryFormCount}" id="btnFindRegistry_${findRegistryFormCount}" bean="GiftRegistryFormHandler.${handlerMethod}" onclick="_gaq.push(['_trackEvent', 'bridal', 'click go', 'find']);">
                        <dsp:tagAttribute name="aria-pressed" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="btnFindRegistry_${findRegistryFormCount}"/>
                        <dsp:tagAttribute name="role" value="button"/>
                    </dsp:input>
			</c:otherwise>
		</c:choose>
		<%-- <dsp:input type="hidden" bean="GiftRegistryFormHandler.registrySearchSuccessURL" value="${contextPath}/giftregistry/registry_search_guest.jsp" />
		<dsp:input type="hidden" bean="GiftRegistryFormHandler.registrySearchErrorURL" value="${contextPath}/giftregistry/registry_search_guest.jsp" /> --%>
		<dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="header" />
	</div>
	
</dsp:form>
</dsp:page>