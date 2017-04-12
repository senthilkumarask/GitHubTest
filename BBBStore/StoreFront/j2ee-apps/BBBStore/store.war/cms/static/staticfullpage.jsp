<dsp:getvalueof var="pageName" param="pageName" />
<dsp:getvalueof var="breadCrumb" param="breadCrumb" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:getvalueof id="servername" idtype="java.lang.String" bean="/OriginatingRequest.servername"/>
<dsp:getvalueof id="scheme" idtype="java.lang.String" bean="/OriginatingRequest.scheme"/>
<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
<dsp:getvalueof var="isInternationalCustomer" bean="SessionBean.internationalShippingContext" />

<c:set var="lbl_reg_feature_home">
	<bbbl:label key="lbl_reg_feature_home"
		language="${pageContext.request.locale.language}" />
</c:set>
<c:choose>
	<c:when test="${pageName == 'SameDayDelivery'}">
			<c:if test="${isInternationalCustomer ne true}">
			<dsp:include page="/_includes/sameDayDeliveryInfo.jsp">
				<dsp:param name="siteId" value="${currentSiteId}" />
			</dsp:include>
			</c:if>
			<dsp:valueof param="staticTemplateData.pageCopy" valueishtml="true" />
	</c:when>
	<c:otherwise>
		<div id="content" class="container_12 clearfix">
			<div id="cmsBreadCrumbs"
				class="breadcrumbs grid_12 marTop_10 noMarBot">
				<a href="${scheme}://${servername}" title="${lbl_reg_feature_home}"><bbbl:label
						key="lbl_reg_feature_home"
						language="${pageContext.request.locale.language}" /></a> <span
					class="rightCarrot">&gt;</span>
				<dsp:droplet name="ForEach">
					<dsp:param name="array" param="breadCrumb" />
					<dsp:param name="elementName" value="breadCrumbValue" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="breadCrumb" param="breadCrumbValue" />
						<dsp:getvalueof var="bbbPageName"
							param="breadCrumbValue.bbbPageName" />
						<dsp:getvalueof var="pageTitle" param="breadCrumbValue.pageTitle" />
						<a href="${contextPath}/static/${bbbPageName}"
							title="${pageTitle}">${pageTitle}</a>
						<span class="rightCarrot">&gt;</span>
					</dsp:oparam>
				</dsp:droplet>
				<span class="bold"><dsp:valueof
						param="staticTemplateData.pageTitle" valueishtml="true" /></span>
			</div>



			<div id="cmsPageHead" class="grid_12 clearfix">
				<h1>
					<dsp:valueof param="staticTemplateData.pageTitle"
						valueishtml="true" />
				</h1>
				<dsp:valueof param="staticTemplateData.pageHeaderCopy"
					valueishtml="true" />
			</div>

			<c:choose>
				<c:when test="${pageName == 'ShippingPolicies'}">
					<div id="cmsPageContent">
						<dsp:include page="/common/shippinginfo.jsp">
							<dsp:param name="siteId" value="${currentSiteId}" />
						</dsp:include>
						<dsp:valueof param="staticTemplateData.pageCopy"
							valueishtml="true" />
					</div>
				</c:when>
				<c:otherwise>
					<dsp:valueof param="staticTemplateData.pageCopy" valueishtml="true" />
				</c:otherwise>
			</c:choose>
	</c:otherwise>
</c:choose>
</div>