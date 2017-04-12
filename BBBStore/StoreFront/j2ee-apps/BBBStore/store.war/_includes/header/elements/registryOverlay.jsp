<dsp:page>
 <%--This JSP would load in cases of Dynamic/Static Checklist

--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
<dsp:importbean bean="/com/bbb/commerce/checklist/droplet/CheckListDroplet" />
<dsp:importbean bean="/com/bbb/integration/interactive/InteractiveChecklistFormHandler"/>
<dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO" var="registrySummaryVO" />
 <dsp:getvalueof var="fromRegistryActivity" param="fromRegistryActivity"/>
 <dsp:getvalueof bean="SessionBean.activateGuideInRegistryRibbon" var="activateGuideInRegistryRibbon" />
 <dsp:getvalueof var="sessionMap" bean="SessionBean.values"/>
 <dsp:getvalueof var="registryId" param="registryId" /> 
  <dsp:getvalueof var="selectedGuideType" value="${sessionMap.selectedGuideType}"/>
 <dsp:getvalueof var="guideType" param="guideType"/>
 <dsp:getvalueof var="currentSiteId" bean="/atg/multisite/Site.id"/>
 <dsp:getvalueof bean="SessionBean" var="bean"/>
 <c:if test="${empty guideType and not empty selectedGuideType and activateGuideInRegistryRibbon}">
 	<dsp:getvalueof var="guideType" value="${selectedGuideType}"/>
 </c:if>
 <c:set var="interactiveChecklistCacheTimeout"><bbbc:config key="interactiveChecklistCacheTimeout" configName="HTMLCacheKeys" /></c:set>
 
 <dsp:setvalue param="checklistVO" beanvalue="SessionBean.checklistVO"/>
 <dsp:getvalueof var="checklistVO" param="checklistVO"/>
<c:if test="${not empty registrySummaryVO}">
<c:choose>
	<c:when test="${not empty registryId && registryId eq registrySummaryVO.registryId}">
    <c:set var="regType" value="${registrySummaryVO.registryType.registryTypeName}" />
    </c:when>
    <c:otherwise>
    	<dsp:setvalue bean="InteractiveChecklistFormHandler.registryId" value="${registryId}" />
		<dsp:setvalue bean="InteractiveChecklistFormHandler.registrySummaryVO" value="true" />
		<dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO" var="registrySummaryVO" />
		<c:set var="regType" value="${registrySummaryVO.registryType.registryTypeName}" />
    </c:otherwise>
</c:choose>
</c:if>

<dsp:droplet name="CheckListDroplet">
			<dsp:param name="registryType" value="${regType}" />
			<dsp:param name="guideType" value="${guideType}" />
			<dsp:param name="sessionBean" value="${bean}" />	
			<dsp:param name="getChecklistFlag" value="false" />
				<dsp:oparam name="output">
						<dsp:getvalueof var="isDisabled" param="isDisabled"/>
				</dsp:oparam>
				</dsp:droplet>


<c:if test="${!isDisabled}">				
<c:choose>
<c:when test="${not empty checklistVO && checklistVO.registryId eq registrySummaryVO.registryId && !fromRegistryActivity && not activateGuideInRegistryRibbon}">
<dsp:include page="/_includes/header/elements/dynamicRegistryOverlay.jsp">
	</dsp:include>
	<input type="hidden" id="loadDynamicchecklist" value="false"/>
</c:when>
<c:otherwise>
<c:set var="registryStaticCheckList" value="${registrySummaryVO.registryType.registryTypeDesc}CheckListProgress"/>
<c:set var="progressBarAvailable"><bbbc:config key="${registryStaticCheckList}" configName="ContentCatalogKeys" /></c:set>

<c:if test="${fn:containsIgnoreCase(registrySummaryVO.registryType.registryTypeDesc, 'Commitment')}">
<c:set var="progressBarAvailable"><bbbc:config key="CommitmentCheckListProgress" configName="ContentCatalogKeys" /></c:set>
</c:if>

<c:if test="${fn:containsIgnoreCase(registrySummaryVO.registryType.registryTypeDesc, 'College') || fn:containsIgnoreCase(registrySummaryVO.registryType.registryTypeDesc, 'University')}">
<c:set var="progressBarAvailable"><bbbc:config key="CollegeCheckListProgress" configName="ContentCatalogKeys" /></c:set>
</c:if>

<%-- ILD -166 : Adding site id to key to handle scenarios with different checklist progress value on different sites. --%>
	<c:choose>
		<c:when test="${activateGuideInRegistryRibbon}">
			<c:set var="cacheKey" value="CacheStaticCategoryList_${guideType}_${currentSiteId}"></c:set>
			<c:set var="progressBarAvailable" value="notApplicable"></c:set>
		</c:when>
		<c:otherwise><c:set var="cacheKey" value="CacheStaticCategoryList_${regType}_${currentSiteId}"></c:set></c:otherwise>
	
	</c:choose>
     <dsp:droplet name="/atg/dynamo/droplet/Cache">
              <dsp:param name="key" value="${cacheKey}" />
                <dsp:param name="cacheCheckSeconds" value="${interactiveChecklistCacheTimeout}"/>
                <dsp:oparam name="output">                      
                 <dsp:include page="/_includes/header/elements/staticRegistryOverlay.jsp">
                 		<dsp:param name="guideType" value="${guideType}" />
                 		<dsp:param name="checklistType" value="${regType}" />
						<dsp:param name="progressBarAvailable" value="${progressBarAvailable}"/>
				</dsp:include>
              </dsp:oparam>
          </dsp:droplet>
       
<c:choose>
<c:when test="${fn:containsIgnoreCase(progressBarAvailable,'on') and not activateGuideInRegistryRibbon}">      
 <dsp:droplet name="CheckListDroplet">
	<dsp:param name="registryId" value="${registrySummaryVO.registryId}" />
	<dsp:param name="registryType" value="${registrySummaryVO.registryType.registryTypeDesc}" />
	<dsp:param name="staticChecklist" value="false" />
	<dsp:param name="sessionBean" value="${bean}" />
	<dsp:param name="fromRegistryActivity" value="${fromRegistryActivity}" />
 </dsp:droplet>
 <input type="hidden" id="loadDynamicchecklist" value="true"/>
</c:when>
<c:otherwise>
<input type="hidden" id="loadDynamicchecklist" value="false"/>
</c:otherwise>	
</c:choose>
</c:otherwise>
</c:choose>
</c:if>

</dsp:page>