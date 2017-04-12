<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
<dsp:importbean bean="/com/bbb/commerce/checklist/droplet/CheckListDroplet" />
<dsp:page>
	<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
	<dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO" var="sessionRegistry" />
	<dsp:getvalueof bean="SessionBean.activateGuideInRegistryRibbon" var="activateGuideInRegistryRibbon" />
	<c:set var="registryChecklist"><bbbl:label key="lbl_registry_checklist" language="${pageContext.request.locale.language}"/></c:set>
	<c:set var="guideChecklist"><bbbl:label key="lbl_guide_checklist" language="${pageContext.request.locale.language}"/></c:set>
	<c:if test="${not empty sessionRegistry}">
		<c:set var="regType" value="${sessionRegistry.registryType.registryTypeDesc}" />
	</c:if>
	<c:if test="${activateGuideInRegistryRibbon}">
		<dsp:getvalueof param="guideType" var="guideType" />
	</c:if>
	<dsp:getvalueof bean="SessionBean" var="bean"/>
	<dsp:droplet name="CheckListDroplet">
		<dsp:param name="registryType" value="${regType}" />
		<dsp:param name="guideType" value="${guideType}" />
		<dsp:param name="sessionBean" value="${bean}" />
		<dsp:param name="getChecklistFlag" value="false" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="isDisabled" param="isDisabled" scope="request" />
		</dsp:oparam>
	</dsp:droplet>

	<c:if test="${not isDisabled}">
		<a id="registryChecklistLink" class="registryChecklist checklistOpen <c:if test="${not empty selectedGuideVO}"> fromGuide</c:if>" href="javascript:void(0);" data-menu-content="#registryCheckListContent") aria-label="<c:choose><c:when test='${!activateGuideInRegistryRibbon}'>${registryChecklist}</c:when><c:otherwise>${guideChecklist}</c:otherwise></c:choose>">
			
			<c:choose>
				<c:when test="${not empty guideType}">
					<span aria-hidden="true" class="icon-file-text-o"></span>
					<span><bbbl:label key="lbl_checklist_guide" language="${pageContext.request.locale.language}"/></span>
				</c:when>
				<c:otherwise>
					<span aria-hidden="true" class="icon-checklist"></span>
					<span><bbbl:label key='lbl_registry_owner_checklist_tab' language="${pageContext.request.locale.language}" /></span>
				</c:otherwise>
			</c:choose>
			<span class="visuallyhidden"><bbbl:label key='lbl_click_expand_checklist' language="${pageContext.request.locale.language}" /></span>
			<span aria-hidden="true" class="icon-downarrow"></span>
		</a>
		<div id="registryCheckListContent" class="flyout-menu-content hidden">
			<div style="height: 200px; width: 300px">
				<div>
					<button id="btnChecklistRedirect" type="button">
						<bbbl:label key='lbl_Open_Checklist'
							language="${pageContext.request.locale.language}" />
					</button>
				</div>
			</div>
		</div>
	</c:if>
</dsp:page>