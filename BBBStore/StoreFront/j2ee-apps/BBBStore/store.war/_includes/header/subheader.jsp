<dsp:page>
	<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/SelectHeaderDroplet" />
	<dsp:importbean	bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean bean="/com/bbb/commerce/checklist/droplet/CheckListDroplet" />
	<dsp:getvalueof var="currentSiteId" bean="/atg/multisite/Site.id" />
		<dsp:importbean bean="/atg/userprofiling/Profile" />
		<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
        <dsp:getvalueof var="transient" bean="Profile.transient" />
        <dsp:getvalueof bean="SessionBean.values.userRegistriesList" var="userRegistriesList" />
        <dsp:getvalueof var="selectedGuideType" bean="SessionBean.values.selectedGuideType"/>
        <dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO" var="registrySummaryVO" />
        
        <c:if test="${empty showRegistryRibbon}">
			<dsp:getvalueof var="showRegistryRibbon" param="showRegistryRibbon" />	
		</c:if>
	
        <c:if test="${not empty registrySummaryVO}">
		    <c:set var="regType" value="${registrySummaryVO.registryType.registryTypeName}" />
		</c:if>
        
        <dsp:getvalueof bean="SessionBean" var="bean"/>
			<c:set var="userRegistriesListSize">${fn:length(userRegistriesList) }</c:set>
		<dsp:getvalueof var="PageType" param="PageType" />
		<c:set var="disableRegistryDropdown" value="${false}" scope="request"/>
		
		<dsp:droplet name="CheckListDroplet">
			<dsp:param name="registryType" value="${regType}" />
			<dsp:param name="guideType" value="${selectedGuideType}" />
			<dsp:param name="sessionBean" value="${bean}" />	
			<dsp:param name="getChecklistFlag" value="true" />
				<dsp:oparam name="output">
						<dsp:getvalueof var="isDisabled" param="isDisabled"/>
				</dsp:oparam>
		</dsp:droplet>
		
		<dsp:droplet name="SelectHeaderDroplet">
			<dsp:param name="siteId" value="${currentSiteId}" />
			<dsp:oparam name="output">
			<dsp:getvalueof id="subheader" param="subheader"/>
			<dsp:getvalueof id="showStaticHeader" param="showStaticHeader"/>
			<dsp:getvalueof id="registryListViewUri" param="registryListViewUri"/>
			<dsp:getvalueof id="categorypages" param="categorypages"/>
			<dsp:getvalueof var="disableRegistryDropdown" param="disableRegistryDropdown" scope="request"/>
			<dsp:getvalueof var="registryOwnerView" param="registryOwnerView"/>
			<c:set var="categorypages" scope="request">${categorypages}</c:set>
			<c:set var="registryListViewUri" scope="request">${registryListViewUri}</c:set>
			<c:set var="showStaticHeader" scope="request">${showStaticHeader}</c:set>
            <c:set var="subHeaderType" scope="request">${subheader}</c:set>
            <c:set var="interactiveCheckListFlag">
				<bbbc:config key="interactive_checklist_key" configName="FlagDrivenFunctions" />
			</c:set>
			<div id="registryRibbonSubheader">
			<input type="hidden" name="interactiveCheckListFlag" value="${interactiveCheckListFlag}"/>
			<div id="loaderChecklistInt" class="hidden">
			   <div class="loading-checklist"><bbbl:label key="lbl_interactive_checklist_loading" language="${pageContext.request.locale.language}"/></div>
                              <img alt='Loader Image' src='/_assets/global/images/widgets/small_loader.gif' class="loading-image">
                                <div class="checklist-error hidden" style="text-align: center;color: red;"><span class = "checklist-error-icon"></span> <span style="display: inline-block;"><bbbl:label key='checklist_system_error_occurred' language="${pageContext.request.locale.language}"/></span></div>
                              
			</div>
		
            <c:if test="${subheader == 'showGenericHeader'}">
				<%-- code changes for BBBI-364 starts --%>
				<div class="container_12 clearfix subHeadRegistryDetail" id="subHeadGenRegistryDetail">
						<%-- <dsp:getvalueof param="registrySummaryVO" var="registrySummaryVO" scope="request"></dsp:getvalueof>
			    		<dsp:include page="/_includes/header/generic_subheader.jsp"> 
						   <dsp:param name="currentSiteId" value="${currentSiteId}"/>
						   <dsp:param name="registrySummaryVO" value="${registrySummaryVO}"/>
						</dsp:include> --%>
							<input type="hidden" id="disableRegistryDropdown" value="${disableRegistryDropdown}"/>
							<input type="hidden" id="registryListViewUri" value="${registryListViewUri}"/>
							<input type="hidden" id="categorypages" value="${categorypages}"/>
							<input type="hidden" id="showStaticHeader" value="${showStaticHeader}"/>
							<input type="hidden" id="loadChecklistRibbon" value="showGenericHeader"/>
							<input type="hidden" id="registryOwnerView" value="${registryOwnerView}"/>
						<div class="subheader-Loader <c:if test="${(cookie.checklistExpanded.value eq 'expanded' or cookie.checklistExpanded.value eq null or empty cookie.checklistExpanded.value) and !isDisabled}">loaderSubheaderMaxHeight</c:if>"><img alt='Loader Image' src='/_assets/global/images/widgets/small_loader.gif' ></div>
				  </div>
				  
			<%-- code changes for BBBI-364 ends --%>
			</c:if>
			<c:choose>
			<c:when test="${PageType eq 'StaticPage' }">
				<c:if test="${subheader == 'showPersistentHeader' && showRegistryRibbon}">
				<div class="container_12 clearfix subHeadRegistryDetail" id="subHeadRegistryDetail">
						<input type="hidden" id="disableRegistryDropdown" value="${disableRegistryDropdown}"/>
							<input type="hidden" id="registryListViewUri" value="${registryListViewUri}"/>
							<input type="hidden" id="categorypages" value="${categorypages}"/>
							<input type="hidden" id="showStaticHeader" value="${showStaticHeader}"/>
						  <input type="hidden" id="loadChecklistRibbon" value="showPersistentHeader"/>
						<div class="subheader-Loader <c:if test="${(cookie.checklistExpanded.value eq 'expanded' or cookie.checklistExpanded.value eq null or empty cookie.checklistExpanded.value) and !isDisabled}">loaderSubheaderMaxHeight</c:if>"><img alt='Loader Image' src='/_assets/global/images/widgets/small_loader.gif' ></div>
				  </div>
				  </c:if>
			</c:when>
			<c:otherwise>
				<c:if test="${subheader == 'showPersistentHeader'}">
				<div class="container_12 clearfix subHeadRegistryDetail" id="subHeadRegistryDetail">
							<input type="hidden" id="disableRegistryDropdown" value="${disableRegistryDropdown}"/>
							<input type="hidden" id="registryListViewUri" value="${registryListViewUri}"/>
							<input type="hidden" id="categorypages" value="${categorypages}"/>
							<input type="hidden" id="showStaticHeader" value="${showStaticHeader}"/>
						  <input type="hidden" id="loadChecklistRibbon" value="showPersistentHeader"/>
						<div class="subheader-Loader <c:if test="${(cookie.checklistExpanded.value eq 'expanded' or cookie.checklistExpanded.value eq null or empty cookie.checklistExpanded.value) and !isDisabled}">loaderSubheaderMaxHeight</c:if>"><img alt='Loader Image' src='/_assets/global/images/widgets/small_loader.gif' ></div>
				  </div>
				  </c:if>
			</c:otherwise>
			</c:choose>
			

			 <c:if test="${subheader == 'noHeader'}">
			 	<c:choose>
					<c:when test="${currentSiteId == 'BuyBuyBaby'}">
						<div id="navBottomNotes">
							<div class="container_12 clearfix">
								<div id="navBottomInfo" class="grid_12 clearfix">
									<div id="navBottomInfoContent"></div>
								</div>
							</div>
						</div>		
					</c:when>
					<c:otherwise>
					</c:otherwise>
				</c:choose>
			 </c:if>
			 	</div>
		</dsp:oparam>
		</dsp:droplet>

</dsp:page>