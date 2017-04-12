<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/ManageRegistryChecklistDroplet" />
<dsp:importbean bean="/com/bbb/selfservice/ScheduleAppointmentDroplet" />
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/RecommendationInfoDisplayDroplet" />
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/DateCalculationDroplet" />
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryVODroplet" />
<dsp:importbean bean="/com/bbb/selfservice/ScheduleAppointmentDroplet" />
<dsp:importbean bean="com/bbb/selfservice/SearchStoreFormHandler" />
 <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
<dsp:page>
	<dsp:getvalueof bean="SessionBean" var="bean"/>
	<c:set var="BedBathUSSite"> <bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" /> </c:set>
	<c:set var="BuyBuyBabySite"> <bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" /> </c:set>
	<c:set var="BedBathCanadaSite"> <bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" /> </c:set>
	<c:set var="lblHideBlog"> <bbbl:label key="lbl_aria_shopping_guide" language="${pageContext.request.locale.language}" /> </c:set>
	<c:set var="lblMovingBlog"> <bbbl:label key="lbl_aria_moving_blog" language="${pageContext.request.locale.language}" /> </c:set>
	<dsp:getvalueof var="currentSiteId" bean="/atg/multisite/Site.id" />
	<c:set var="ManageRegistry"> <bbbl:label key="lbl_manage_registry" language="${pageContext.request.locale.language}" /> </c:set>
	<c:set var="book_an_appointment"> <bbbl:label key="lbl_book_an_appointment" language="${pageContext.request.locale.language}" /> </c:set>
	<c:set var="ask_a_friend"> <bbbl:label key="lbl_ask_a_friend" language="${pageContext.request.locale.language}" /> </c:set>
	<c:set var="quick_picks"> <bbbl:label key="lbl_quick_picks" language="${pageContext.request.locale.language}" /> </c:set>
	<c:set var="view_all_your_registries"> <bbbl:label key="lbl_view_all_your_registries" language="${pageContext.request.locale.language}" /> </c:set>
	<c:set var="inviteFriend"> <bbbc:config key="Invite_Friends_Key" configName="FlagDrivenFunctions" /> </c:set>
	<c:set var="inviteFriend" value="${fn:toLowerCase(inviteFriend)}" />
	<c:set var="skedgeURL"> <bbbc:config key="appointmentSkedgeURL" configName="ThirdPartyURLs" /> </c:set>

	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
	<c:set var="clickToView" scope="request"> <bbbl:label key='lbl_click_tap_open' language="${pageContext.request.locale.language}" /> </c:set>
	<c:set var="manageRegDDLabel" scope="request"> <bbbl:label key='lbl_manage_reg_dd' language="${pageContext.request.locale.language}" /> </c:set>
	<c:set var="manageGuideDDLabel" scope="request"> <bbbl:label key='lbl_manage_guide_dd' language="${pageContext.request.locale.language}" /> </c:set>
	<c:set var="registryText" scope="request"> <bbbl:label key='lbl_checklist_reg_text' language="${pageContext.request.locale.language}" /> </c:set>
	<c:set var="hideGuide" scope="request"><bbbl:label key="lbl_checklist_hide_guide_link" language="${pageContext.request.locale.language}"/></c:set>
	<c:set var="guideBlog" scope="request"><bbbl:label key="lbl_checklist_guide_blog_link" language="${pageContext.request.locale.language}"/></c:set>
	<dsp:getvalueof var="selectedGuideVO" param="selectedGuideVO" />
	
	<div class="">
		<a href="javascript:void(0);" class="manageRegistryLink flyout-menulink" data-menu-content="#manageRegistryContent"
			data-placement="bottom-right" aria-label="${clickToView} ${manageGuideDDLabel} of ${selectedGuideVO.guideDisplayName}">
			<span class="icon-settings"></span><bbbl:label key="lbl_checklist_manage" language="${pageContext.request.locale.language}"/>
		</a>
		<div id="manageRegistryContent" class="flyout-menu-content hidden">
			<dsp:droplet name="ManageRegistryChecklistDroplet">
				<dsp:param name="guideType" param="guideType" />	
				<dsp:oparam name="output">
					<dsp:getvalueof var="ManageCheckListLink" param="ManageCheckListLink" />
					<ul class="flyout-menu">
							<c:forEach items="${ManageCheckListLink.links}" var="linkVO">
					<c:if test="${linkVO.bannerText eq hideGuide}">
						<li data-guide-type="${selectedGuideVO.guideTypeCode}"><a class="hideGuide" aria-label="${lblHideBlog}" data-guide-type="${selectedGuideVO.guideTypeCode}">${linkVO.bannerText}</a></li>
					</c:if>
					<c:if test="${linkVO.bannerText eq guideBlog}">
						<li><a aria-label="${lblMovingBlog}" href="${linkVO.bannerLink}/">${selectedGuideVO.guideDisplayName} ${linkVO.bannerText}</a></li>
					</c:if>
				</c:forEach>
					</ul>
				</dsp:oparam>
			</dsp:droplet>
		</div>
	</div>
</dsp:page>
