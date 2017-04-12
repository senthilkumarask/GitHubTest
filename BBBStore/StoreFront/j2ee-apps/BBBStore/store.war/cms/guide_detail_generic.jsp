<dsp:page>
    <dsp:getvalueof id="servername" idtype="java.lang.String" bean="/OriginatingRequest.servername"/>
	<dsp:getvalueof id="scheme" idtype="java.lang.String" bean="/OriginatingRequest.scheme"/>
	<dsp:getvalueof var="referer" bean="/OriginatingRequest.referer"/>
	<c:set var="section" value="cms" scope="request" />
	<c:set var="pageWrapper" value="guideDetail" scope="request" />
	<dsp:getvalueof var="view" param="view"/>
	<dsp:getvalueof var="searchTerm" param="Keyword"/>
	<dsp:getvalueof var="CatalogId" param="CatalogId"/>
	<%-- R2.2 Story - SEO Friendly URL changes --%>
	<dsp:getvalueof var="url" vartype="java.lang.String" value="/store/s/${searchTerm}" />
	<dsp:getvalueof var="guideId" param="guideId"/>
	<c:set var="BuyBuyBabySite">
		<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="titleString"><dsp:valueof param="title" valueishtml="true"/></c:set>
	<dsp:getvalueof var="title" param="title"/>
		<dsp:getvalueof var="longDescription" param="longDescription"/>
		<dsp:getvalueof var="omnitureData" param="omnitureData"/>
		<c:if test="${not empty omnitureData}">
			<c:set var="omniPageName">${omnitureData['pageName'] }</c:set>
			<c:set var="channel">${omnitureData['channel'] }</c:set>
			<c:set var="prop1">${omnitureData['prop1'] }</c:set>
			<c:set var="prop2">${omnitureData['prop2'] }</c:set>
			<c:set var="prop3">${omnitureData['prop3'] }</c:set>
			<c:set var="prop4">${omnitureData['prop4'] }</c:set>
		</c:if>
		<c:set var="brGuides" value="Guides" scope="request" />
		<c:set var="lnkGuidesBk" value="/store/static/GuidesPage" scope="request" />
		<c:if test="${fn:contains(referer, 'BabyGuidesAndAdviceLandingPage')}">
		<c:set var="brGuides" value="Baby Guides & Advice" scope="request" />
		<c:set var="lnkGuidesBk" value="/store/registry/BabyGuidesAndAdviceLandingPage" scope="request" />
		</c:if>
	<bbb:pageContainer section="${section}" pageWrapper="${pageWrapper}" titleString="${titleString}">
		<div id="content" class="container_12 clearfix" role="main">
			<c:choose>
				<c:when test="${searchTerm eq null}">
					<c:choose>
						<c:when test="${currentSiteId == BuyBuyBabySite}">
							<div id="cmsBreadCrumbs" class="breadcrumbs grid_12 marTop_10 marBottom_5">
								<a href="${scheme}://${servername}"><bbbl:label key="lbl_reg_feature_home" language ="${pageContext.request.locale.language}"/></a>
								<span class="rightCarrot">&gt;</span>
								<a href="${contextPath}/registry/GuidesAndAdviceLandingPage" title="Guides">Guides</a>
								<span class="rightCarrot">&gt;</span>
								<span class="bold">${title}</span>
							</div>
							<div id="cmsPageHead" class="grid_12 clearfix">
								<h1>${title}</h1>
								<a id="cmsLnkBack" href="/store/registry/GuidesAndAdviceLandingPage" title='<bbbl:label key="lbl_back_guide" language ="${pageContext.request.locale.language}"/>'><bbbl:label key="lbl_back_guide" language ="${pageContext.request.locale.language}"/></a>
							</div>
						</c:when>
						<c:otherwise>
						<c:choose>
						<c:when test="${(currentSiteId  == 'BedBathCanada') && (babyCAMode == 'true')}">
						<c:set var="lnkRegistryBk" value="/store/page/BabyCanadaRegistry" scope="request" />
						</c:when>
						<c:otherwise>
						<c:set var="lnkRegistryBk" value="/store/page/Registry" scope="request" /></c:otherwise>
						</c:choose>
							<div id="cmsBreadCrumbs" class="breadcrumbs grid_12 marTop_10 marBottom_5">
								<a href="${scheme}://${servername}"><bbbl:label key="lbl_reg_feature_home" language ="${pageContext.request.locale.language}"/></a>
								<span class="rightCarrot">&gt;</span>
								<a href="${lnkRegistryBk}" title="Registry">Registry</a>
								<span class="rightCarrot">&gt;</span>
								<a href="${lnkGuidesBk}" title="Guides">${brGuides}</a>
								<span class="rightCarrot">&gt;</span>
								<span class="bold">${title}</span>
							</div>
							<div id="cmsPageHead" class="grid_12 clearfix">
								<h1>${title}</h1>
								<a id="cmsLnkBack" href="${contextPath}/static/GuidesPage" title='<bbbl:label key="lbl_back_guide" language ="${pageContext.request.locale.language}"/>'><bbbl:label key="lbl_back_guide" language ="${pageContext.request.locale.language}"/></a>
							</div>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<div id="cmsBreadCrumbs" class="breadcrumbs grid_12 marTop_10 marBottom_5">
						<a href=${pageContext.request.contextPath}><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></a>
						<span class="rightCarrot">&gt;</span>
						<a href="${contextPath}/s/${searchTerm}" title="Guides">Guides</a>
						<span class="rightCarrot">&gt;</span>
						<span class="bold">${title}</span>
					</div>
					<div id="cmsPageHead" class="grid_12 clearfix">
						<h1>${title}</h1>
						<a id="cmsLnkBack" href="${contextPath}/s/${searchTerm}" title="Back to guides"/><bbbl:label key="lbl_back_guide" language ="${pageContext.request.locale.language}"/></a>
					</div>
				</c:otherwise>
			</c:choose>
			<div id="cmsPageContent">
				${longDescription}
			</div>
			<c:choose>
				<c:when test="${searchTerm eq null}">
					<div id="cmsPageFooter" class="grid_12 clearfix">
						<c:choose>
							<c:when test="${currentSiteId == BuyBuyBabySite}">
								<a id="cmsLnkBack" href="/store/registry/GuidesAndAdviceLandingPage" title='<bbbl:label key="lbl_back_guide" language ="${pageContext.request.locale.language}"/>'><bbbl:label key="lbl_back_guide" language ="${pageContext.request.locale.language}"/></a>
							</c:when>
							<c:otherwise>
					 <a id="cmsLnkBack" href="${lnkGuidesBk}" title='<bbbl:label key="lbl_back_guide" language ="${pageContext.request.locale.language}"/>'><bbbl:label key="lbl_back_guide" language ="${pageContext.request.locale.language}"/></a>
							</c:otherwise>
						</c:choose>
					</div>
				</c:when>
				<c:otherwise>
					<div id="cmsPageFooter" class="grid_12 clearfix">
						<a id="cmsLnkBack" href="${contextPath}/s/${searchTerm}" title='<bbbl:label key="lbl_back_guide" language ="${pageContext.request.locale.language}"/>'><bbbl:label key="lbl_back_guide" language ="${pageContext.request.locale.language}"/></a>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
		<jsp:attribute name="footerContent">
			<script type="text/javascript">
                if(typeof s !=='undefined') {
                    var omni_titleString='${fn:replace(fn:replace(titleString,'\'',''),'"','')}';
                    var omni_omniPageName='${fn:replace(fn:replace(omniPageName,'\'',''),'"','')}';
                    var omni_channel='${fn:replace(fn:replace(channel,'\'',''),'"','')}';
                    var omni_prop1='${fn:replace(fn:replace(prop1,'\'',''),'"','')}';
                    var omni_prop2='${fn:replace(fn:replace(prop2,'\'',''),'"','')}';
                    var omni_prop3='${fn:replace(fn:replace(prop3,'\'',''),'"','')}';
                    var omni_prop4='${fn:replace(fn:replace(prop4,'\'',''),'"','')}';
                    if(omni_omniPageName.length>0){
                        s.pageName=omni_omniPageName; // pageName
                        s.channel=omni_channel;
                        s.prop1=omni_prop1;
                        s.prop2=omni_prop2;
                        s.prop3=omni_prop3;
                        s.prop4=omni_prop4;
                        s.prop6='${pageContext.request.serverName}';
                        s.eVar9='${pageContext.request.serverName}';
                        s.events = '';
                        s.products = '';
                        s.server='${pageContext.request.serverName}';
                    }
                    else{
                        s.channel='Guides & Advice';
                        s.pageName='Content Page > ' + omni_titleString;// pagename
                        s.prop1='Content Page';
                        s.prop2='Content Page';
                        s.prop3='Content Page';
                        s.prop4='Guides & Advice';
                        s.prop6='${pageContext.request.serverName}';
                        s.prop9='Guides & Advice';
                        s.eVar9='${pageContext.request.serverName}';
                    }
                    fixOmniSpacing();
                    var s_code=s.t();
                    if(s_code)document.write(s_code);
                }
			</script>
		</jsp:attribute>
	</bbb:pageContainer>
</dsp:page>