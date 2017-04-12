<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/CategoryLandingDroplet" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/account/droplet/BBBICrossTagDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BreadcrumbDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	
    <dsp:getvalueof id="applicationId" bean="Site.id" />
	<dsp:getvalueof var="categoryId" param="categoryId"/>

 	<bbb:pageContainer>
		<jsp:attribute name="section">browse</jsp:attribute>
		<jsp:attribute name="PageType">CategoryLandingDetails</jsp:attribute>
		<jsp:attribute name="pageWrapper">category useCertonaAjax</jsp:attribute>
		<jsp:body>
		
		<dsp:droplet name="BreadcrumbDroplet">
			<dsp:param name="categoryId" param="categoryId" />
			<dsp:param name="siteId" value="${applicationId}"/>
				<dsp:oparam name="output">
					<dsp:droplet name="ForEach">
					<dsp:param name="array" param="breadCrumb" />
						<dsp:oparam name="outputStart">
							<c:set var="categoryPath" scope="request"></c:set>
						</dsp:oparam>
						<dsp:oparam name="output">
							<c:set var="categoryPath" scope="request">${categoryPath} > <dsp:valueof param="element.categoryName" /></c:set>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
		</dsp:droplet>
		<%-- KP COMMENT: Removing  --%>
		<%--
		<c:if test="${TagManOn}">
			<dsp:include page="/tagman/frag/categoryLanding_frag.jsp">
				<dsp:param name="categoryPath" value="${categoryPath}"/>
			</dsp:include>
		</c:if>
		--%>
		<dsp:droplet name="CategoryLandingDroplet">
				<dsp:param param="categoryId" name="id" />
				<dsp:param name="fetchSubCategories" value="true" />
				<dsp:param name="siteId" value="${applicationId}"/>
				<dsp:oparam name="error">
					<dsp:include page="../404.jsp" flush="true"/>
				</dsp:oparam>
				<dsp:oparam name="output">
				<dsp:getvalueof var="landingTemplateVO" param="landingTemplateVO" scope="request"/>


					<dsp:getvalueof var="categoryId" param="categoryId" />


					<dsp:getvalueof var="subcategoriesList" param="subcategoriesList" />

					<dsp:getvalueof var="categoryL1" param="categoryL1" />
					<dsp:getvalueof var="categoryL2" param="categoryL2" />
					<dsp:getvalueof var="categoryL3" param="categoryL3" />


					<%-- KP COMMENT: Removing Carousel --%>
					<%-- <jsp:include page="hero_rotator.jsp" >
						<jsp:param name="landingTemplateVO" value="${landingTemplateVO}"/>
					</jsp:include> --%>
						
			
			
				<dsp:include page="subcategories_list.jsp" >
					<dsp:param name="subcategoriesList" value="${subcategoriesList}"/>
					<dsp:param name="landingTemplateVO" value="${landingTemplateVO}"/>
					<dsp:param name="categoryId" value="${categoryId}"/>
				</dsp:include>
				
			
				<jsp:include page="category_promo.jsp" >
					<jsp:param name="landingTemplateVO" value="${landingTemplateVO}"/>
				</jsp:include>




				<%-- Commenting Icrossing as part of 34473
				<dsp:droplet name="BBBICrossTagDroplet">
					<dsp:param name="configKey" value="ICROSSINGTag_Cat_Keys"/>
					<dsp:param name="categoryId" value="${categoryId}"/>
					<dsp:oparam name="output">
						<dsp:getvalueof var="isCategoryIdPresent" param="isCategoryIdPresent"/>
					</dsp:oparam>
				</dsp:droplet>
				--%>


				<%--DoubleClick Floodlight START --%>
				<%-- Commenting out DoubleClick as part of 34473
				<c:if test="${DoubleClickOn}">
					<c:if test="${(currentSiteId eq TBS_BedBathUSSite)}">
						<c:set var="cat"><bbbc:config key="cat_category_bedBathUS" configName="RKGKeys" /></c:set>
						<c:set var="src"><bbbc:config key="src_bedBathUS" configName="RKGKeys" /></c:set>
						<c:set var="type"><bbbc:config key="type_1_bedBathUS" configName="RKGKeys" /></c:set>
					</c:if>
					<c:if test="${(currentSiteId eq TBS_BuyBuyBabySite)}">
						<c:set var="cat"><bbbc:config key="cat_category_baby" configName="RKGKeys" /></c:set>
						<c:set var="src"><bbbc:config key="src_baby" configName="RKGKeys" /></c:set>
						<c:set var="type"><bbbc:config key="type_1_baby" configName="RKGKeys" /></c:set>
					</c:if>
					<dsp:include page="/_includes/double_click_tag.jsp">
						<dsp:param name="doubleClickParam"
									value="src=${src};type=${type};cat=${cat};u4=null;u5=null;u11=${categoryL1}${categoryL2}${categoryL3}" />
					</dsp:include>
				</c:if>
				--%>
				<%--DoubleClick Floodlight END --%>

					<div class="row">
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
                    <c:set var="cert_clearanceDealsFlag" scope="request"><dsp:valueof param="landingTemplateVO.clearanceDealsFlag" /></c:set>
                    <c:set var="cert_justForYouFlag" scope="request"><dsp:valueof param="landingTemplateVO.justForYouFlag" /></c:set>
                    <c:set var="cert_flagFunNewProducts" scope="request">false</c:set>
                    <c:set var="cert_bottomTabs" scope="request">true</c:set>
                    <c:set var="cert_linksCertonaNonRecomm" scope="request"><dsp:valueof param="linkString" /></c:set>
                    <div id="certonaBottomTabs" class="clearfix loadAjaxContent" 
                        data-ajax-url="${contextPath}/common/certona_slots.jsp" 
                        data-ajax-target-divs="#certonaBottomTabs" 
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
                     </div>
			
			
			<%-- KP COMMENT: Commenting out SEO text, which we shouldn't need on an internal application --%>
			<div class="row">
				<div class="small-12 columns">
					<dsp:getvalueof param="landingTemplateVO.seoStaticText" var="seoStaticText" />
					<c:if test="${not empty seoStaticText}">
						<div class="category-description">
							<c:out value="${seoStaticText}" escapeXml="false"/>
						</div>
					</c:if>
				</div>
			</div>
			</dsp:oparam>
	</dsp:droplet>
	<c:if test="${TellApartOn}">
		<bbb:tellApart actionType="pv" pageViewType="ProductCategory" />
	</c:if>
	<script type="text/javascript">
		var resx = new Object();
	</script>
 </jsp:body>

		 <jsp:attribute name="footerContent">
           <script type="text/javascript">
            if(typeof s !=='undefined') {
                <dsp:getvalueof var="pageName" param="categoryL1"/>
                <dsp:getvalueof var="omniCategoryL2" param="categoryL2"/>
                <dsp:getvalueof var="omniCategoryL3" param="categoryL3"/>
                var pageName = '${fn:replace(fn:replace(pageName,'\'',''),'"','')}';
                var omniCategoryL2 = '${fn:replace(fn:replace(omniCategoryL2,'\'',''),'"','')}';
                var omniCategoryL3 = '${fn:replace(fn:replace(omniCategoryL3,'\'',''),'"','')}';
                if (pageName == 'Clearance'){
                    s.pageName='Clearance';
                    s.prop2= 'Main Level Page';
                }else{
                    s.pageName= pageName;
                    s.prop2= 'Category Page';
                }
                s.channel=pageName;
                s.prop1='Category Page';
                s.prop3='Category Page';// sub categories
                s.prop4='';// Details of all the sub categories for the category
                s.prop5='';
                s.eVar4= pageName;
                s.eVar5= omniCategoryL2;
                s.eVar6= omniCategoryL3;
                s.prop6='${pageContext.request.serverName}';
                s.eVar9='${pageContext.request.serverName}';
                //s.prop2= '<dsp:valueof param="categoryL1"/>';
                // s.prop3='Home > <dsp:valueof param="categoryL1"/>';// sub categories
                // s.prop4='List of Sub categories';// Details of all the sub categories for the category
                // s.prop5='category details and its subcategories';
                fixOmniSpacing();
                var s_code=s.t();
                if(s_code)document.write(s_code);
            }
        </script>
        </jsp:attribute>
	</bbb:pageContainer>

</dsp:page>