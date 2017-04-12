<dsp:importbean
	bean="/com/bbb/cms/droplet/PersonalizedShopTemplateDroplet" />
<dsp:page>
	<jsp:body>
				<div id="content" class="container_12 clearfix">
			 	   <dsp:droplet name="PersonalizedShopTemplateDroplet">
					<dsp:param name="categoryId" param="categoryId" />
					<dsp:oparam name="output">
					 <dsp:getvalueof var="jsFilePath" param="staticTemplateData.responseItems[0].jsFilePath" />
					  <dsp:getvalueof var="cssFilePath" param="staticTemplateData.responseItems[0].cssFilePath" />
					  <script type="text/javascript" src="${jsFilePath}"></script>
						<link rel="stylesheet" type="text/css" href="${cssFilePath}" />
					
						<div id="cmsPageHead" class="grid_12 clearfix">
							<h1><dsp:valueof param="staticTemplateData.responseItems[0].pageTitle" valueishtml="true" /></h1>
							<dsp:valueof param="staticTemplateData.responseItems[0].pageHeaderCopy" valueishtml="true"/>
						</div>
						<div id="cmsPageContent">
							<dsp:valueof param="staticTemplateData.responseItems[0].pageCopy" valueishtml="true"/>
						</div> 
				<dsp:getvalueof param="staticTemplateData.responseItems[0].seoStaticText" var="seoStaticText" />
				<c:if test="${not empty seoStaticText}">
					<div class="grid_12 clearfix catSEOTxt">
						<c:out value="${seoStaticText}" escapeXml="false"/>
					</div>
				</c:if>
				<c:set var="cert_categoryId" scope="request"><dsp:valueof param="categoryId" /></c:set>
                    <c:set var="cert_clearanceDealMax" scope="request"><bbbc:config key="CatClearanceDealProdMax" configName="CertonaKeys" /></c:set>
                    <c:set var="cert_just4YouMax" scope="request"><bbbc:config key="CatJust4YouProdMax" configName="CertonaKeys" /></c:set>
                    <c:set var="cert_lastViewedTabLbl" scope="request"><bbbl:label key="lbl_homepage_last_viewed_items" language="<c:out param='${pageContext.request.locale.language}'/>" /></c:set>
                    <c:set var="cert_clearanceTabLbl" scope="request"><bbbl:label key="lbl_homepage_clearance_deals" language="<c:out param='${pageContext.request.locale.language}'/>" /></c:set>
                    <c:set var="cert_justForYouTabLbl" scope="request"><bbbl:label key="lbl_homepage_just_for_you" language="<c:out param='${pageContext.request.locale.language}'/>" /></c:set>
                    <c:set var="cert_scheme" scope="request">clp_cd;clp_jfy</c:set>
                    <c:set var="cert_cd" scope="request">clp_cd</c:set>
                    <c:set var="cert_jfy" scope="request">clp_jfy</c:set>
                    <c:set var="cert_number" scope="request">${cert_clearanceDealMax};${cert_just4YouMax}</c:set>
                    <c:set var="cert_pageName" scope="request">Category Landing Page</c:set>
                    <c:set var="cert_omniCrossSellPageName" scope="request">(category page)</c:set>
                    <c:set var="cert_clearanceDealsFlag" scope="request">true</c:set>
					<c:set var="cert_justForYouFlag" scope="request">true</c:set>
					<c:set var="cert_flagFunNewProducts" scope="request">false</c:set>
                    <c:set var="cert_bottomTabs" scope="request">true</c:set>
                    <c:set var="cert_linksCertonaNonRecomm" scope="request"><dsp:valueof param="linkString" /></c:set>
					
                    <div id="certonaBottomTabs" class="clearfix loadAjaxContent" 
                        data-ajax-url="${contextPath}/common/certona_slots.jsp" 
                        data-ajax-target-divs="#certonaBottomTabs,#requestResponse" 
                        data-ajax-params-count="16" 
                        data-ajax-param1-name="categoryId" data-ajax-param1-value="${cert_categoryId}" 
                        data-ajax-param2-name="scheme" data-ajax-param2-value="${cert_scheme}" 
                        data-ajax-param3-name="number" data-ajax-param3-value="${cert_number}" 
                        data-ajax-param4-name="lastViewedTabLbl" data-ajax-param4-value="${cert_lastViewedTabLbl}" 
                        data-ajax-param5-name="justForYouTabLbl" data-ajax-param5-value="${cert_justForYouTabLbl}" 
                        data-ajax-param6-name="clearanceTabLbl" data-ajax-param6-value="${cert_clearanceTabLbl}" 
                        data-ajax-param7-name="cd" data-ajax-param7-value="${cert_cd}" 
                        data-ajax-param8-name="jfy" data-ajax-param8-value="${cert_jfy}" 
                        data-ajax-param9-name="clearanceDealsFlagParam" data-ajax-param9-value="${cert_clearanceDealsFlag}" 
                        data-ajax-param10-name="justForYouFlagParam" data-ajax-param10-value="${cert_justForYouFlag}" 
                        data-ajax-param11-name="omniCrossSellPageName" data-ajax-param11-value="${cert_omniCrossSellPageName}" 
                        data-ajax-param12-name="certonaPageName" data-ajax-param12-value="${cert_pageName}" 
                        data-ajax-param13-name="funNewProductsFlagParam" data-ajax-param13-value="${cert_flagFunNewProducts}" 
                        data-ajax-param14-name="certonaBottomTabsFlagParam" data-ajax-param14-value="${cert_bottomTabs}" 
                        data-ajax-param15-name="linksCertonaNonRecomm" data-ajax-param15-value="${cert_linksCertonaNonRecomm}"
                        data-ajax-param16-name="certonaSwitch" data-ajax-param16-value="${CertonaOn}"  
                    role="complementary">
                        <div class="grid_12 clearfix"><img width="20" height="20" alt="small loader" src="/_assets/global/images/widgets/small_loader.gif" /></div>
                    </div>
			</dsp:oparam>
			</dsp:droplet>
		</div>
		<script type="text/javascript">
			var resx = new Object();
		</script>
 </jsp:body>

</dsp:page>