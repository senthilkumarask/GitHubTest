<dsp:getvalueof var="pageName" param="pageName"/>
<dsp:getvalueof var="breadCrumb" param="breadCrumb"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />

<c:set var="lbl_reg_feature_home"><bbbl:label key="lbl_reg_feature_home" language="${pageContext.request.locale.language}" /></c:set>

<div id="content" class="container_12 clearfix">
	<div id="cmsBreadCrumbs" class="breadcrumbs grid_12 marTop_10 noMarBot">
		<a href="${contextPath}" title="${lbl_reg_feature_home}"><bbbl:label key="lbl_reg_feature_home" language ="${pageContext.request.locale.language}"/></a>
		<!-- <span class="rightCarrot">&gt;</span> -->
		<dsp:droplet name="ForEach">
			<dsp:param name="array" param="breadCrumb" />
			<dsp:param name="elementName" value="breadCrumbValue" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="breadCrumb" param="breadCrumbValue" />
				<dsp:getvalueof var="bbbPageName" param="breadCrumbValue.bbbPageName" />
				<dsp:getvalueof var="pageTitle" param="breadCrumbValue.pageTitle" />
				<a href="${contextPath}/static/${bbbPageName}" title="${pageTitle}">${pageTitle}</a>
				<span class="rightCarrot">&gt;</span>
			</dsp:oparam>
		</dsp:droplet>
		<span class="bold"><dsp:valueof param="staticTemplateData.pageTitle" valueishtml="true" /></span>
	</div>

	<div id="cmsPageHead" class="grid_12 clearfix">
		<h1><dsp:valueof param="staticTemplateData.pageTitle" valueishtml="true" /></h1>
		<dsp:valueof param="staticTemplateData.pageHeaderCopy" valueishtml="true"/>
	</div>
			
	<dsp:getvalueof var ="pageCopy" param="staticTemplateData.pageCopy"/>
	<c:set var="pageCopyNew" value="${fn:replace(pageCopy, '/store','/tbs')}" />
	<c:set var="pageCopyNew" value="${fn:replace(pageCopyNew, 'grid_6','grid_6 small-12 columns large-6')}" />
				
	<c:choose>
		<c:when test="${pageName == 'ShippingPolicies'}">
			<div id="cmsPageContent">
				<dsp:include page="/common/shippinginfo.jsp">
					<dsp:param name="siteId" value="${currentSiteId}" />
				</dsp:include>
	
				<dsp:valueof value="${pageCopyNew}" valueishtml="true"/>
			</div>
		</c:when>
		<c:otherwise>		
			<dsp:valueof value="${pageCopyNew}" valueishtml="true"/>   
		</c:otherwise>
	</c:choose>
</div>