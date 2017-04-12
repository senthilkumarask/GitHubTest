<dsp:importbean
	bean="/com/bbb/commerce/browse/droplet/CategoryLandingDroplet" />
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
    <dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/com/bbb/account/droplet/BBBICrossTagDroplet"/>
    <dsp:getvalueof id="applicationId" bean="Site.id" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BreadcrumbDroplet"/>
	<dsp:getvalueof var="categoryId" param="categoryId"/>
<dsp:page>

 	<bbb:pageContainer>

		<jsp:attribute name="section">browse</jsp:attribute>
		<jsp:attribute name="PageType">CategoryLandingDetails</jsp:attribute>
		<jsp:attribute name="pageWrapper">category useCertonaAjax</jsp:attribute>
		<jsp:body>
 	   
 	   
 		<dsp:getvalueof var="categoryL1" param="categoryL1" />
		<dsp:getvalueof var="categoryL2" param="categoryL2" />
		<dsp:getvalueof var="categoryL3" param="categoryL3" />
 		<dsp:getvalueof var="categoryId" param="categoryId" />
 		<dsp:getvalueof var="landingTemplateVO" param="landingTemplateVO" scope="request"/>
 	   					
		<c:set var="isPromoPage" value="false" />
		<c:set var="lblSelectOtherCollection">
			<bbbl:label key="lblSelectOtherCollection" language="${pageContext.request.locale.language}" />
		</c:set>
		<c:set var="l1CacheTimeout">
			<bbbc:config key="L1CategoryCacheTimeout" configName="HTMLCacheKeys" />
		</c:set>
		<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
		<c:set var="personalizedShopCatId">
			<bbbc:config key="PersonalizedShopCategoryId" configName="EXIMKeys" />
		</c:set>
		<c:choose>
			<c:when test="${fn:contains(personalizedShopCatId, categoryId)}">
					<dsp:include page="/browse/personalizedShop_landing.jsp">
						<dsp:param name="categoryId" value="${categoryId}" />
				      </dsp:include>
			</c:when>
			<c:otherwise>
			<dsp:droplet name="/atg/dynamo/droplet/Cache">
				<dsp:param name="key" value="L1Category_${currentSiteId}_${categoryId}" />
				<dsp:param name="cacheCheckSeconds" value="${l1CacheTimeout}"/>
			<dsp:oparam name="output">
			  <%-- Multiple Calls to Bredcrumb droplet has been reused by using include jsp  --%>
	 	    <dsp:include page="category_breadcrumb.jsp"></dsp:include>
		         
		    <c:if test="${TagManOn}">
				<dsp:include page="/tagman/frag/categoryLanding_frag.jsp">
	       			<dsp:param name="categoryPath" value="${categoryPath_frag}"/>
	      		</dsp:include>
			</c:if>
		
			<dsp:droplet name="CategoryLandingDroplet">
					<dsp:param param="categoryId" name="id" />
					<dsp:param name="siteId" value="${applicationId}"/>
					<dsp:param name="fetchSubCategories" value="true"/>
					<dsp:oparam name="error">
						<dsp:include page="../404.jsp" flush="true"/>
					</dsp:oparam>
					<dsp:oparam name="output">
										
						<dsp:getvalueof var="categoryVO" param="categoryVO" />
					
					<c:choose>
						<c:when test="${not empty categoryVO.bccManagedPromoCategoryVO }">
							<c:set var="isPromoPage" value="true" />
							<div class="alpha omega newPlp" id="dcNavCount">
							   <div class="alpha omega container_12 clearfix" id="dcNav">
							   							  
							    <div class="grid_8 alpha headerCont">
							            <h1 id="address_l2_l3_brand" class="catHeader">
							            	${categoryVO.categoryName}
							            </h1>
							      <span class="description">${categoryVO.catDescription}</span>  
							   </div>
							   
							      <c:if test="${not empty categoryVO.subCategories}">
							         <div id="collectionDrp1" class="dcDropDownCont omega">
							            <div class="dcDropDownTextReplace">${lblSelectOtherCollection}</div>
							            <ul id="otherCollectdrop" class="dcThemes">
							               <div class="viewport clearfix">
							                  <div class="overview" tabindex="0">
							                     <dsp:droplet name="/atg/dynamo/droplet/ForEach">
							                        <dsp:param param="categoryVO.subCategories" name="array" />
							                        <dsp:oparam name="output">			
														<dsp:getvalueof var="categoryURL" param="element.seoURL"/>			
							                           <li class="subCategoryItem">
							                              <a href="/store${categoryURL}">
							                                 <dsp:valueof param="element.categoryName" />
							                              </a>
							                           </li>
							                        </dsp:oparam>
							                     </dsp:droplet>
							                  </div>
							               </div>
							            </ul>
							         </div>
							      </c:if>
							   </div>
							</div>
							
														
							<jsp:include page="hero_rotator.jsp" >
								<jsp:param name="landingTemplateVO" value="${landingTemplateVO}"/>
								<jsp:param value="true" name="fromMerchPLPPage"/>
							</jsp:include>
							<c:set var="catBannerContent" >${categoryVO.bannerContent}</c:set>
							<c:if test="${not empty catBannerContent}">									

								<c:if test="${not empty categoryVO.jsFilePath}">
									<script type="text/javascript" src="${categoryVO.jsFilePath}"></script>
								</c:if>
								<c:if test="${not empty categoryVO.cssFilePath}">
									<link rel="stylesheet" type="text/css" href="${categoryVO.cssFilePath }" />
								</c:if>							
									<c:out value="${catBannerContent}" escapeXml="false"></c:out>																

							</c:if>
						</c:when>
						<c:otherwise>
							<dsp:getvalueof var="subcategoriesList" param="subcategoriesList" />	
								<jsp:include page="hero_rotator.jsp" >
									<jsp:param name="landingTemplateVO" value="${landingTemplateVO}"/>
								</jsp:include>

								<div id="content" class="container_12 clearfix" role="main">
									<dsp:include page="subcategories_list.jsp" >
									  <dsp:param name="subcategoriesList" value="${subcategoriesList}"/>
									  <dsp:param name="landingTemplateVO" value="${landingTemplateVO}"/>
									   <dsp:param name="categoryId" value="${categoryId}"/>
									</dsp:include>
				
									<jsp:include page="category_promo.jsp" >
									  <jsp:param name="landingTemplateVO" value="${landingTemplateVO}"/>
									</jsp:include>


									<%-- <dsp:droplet name="BBBICrossTagDroplet">
										<dsp:param name="configKey" value="ICROSSINGTag_Cat_Keys"/>
										<dsp:param name="categoryId" value="${categoryId}"/>
										<dsp:oparam name="output">
											<dsp:getvalueof var="isCategoryIdPresent" param="isCategoryIdPresent"/>
										</dsp:oparam>
									</dsp:droplet> --%>
								</div>
						</c:otherwise>
					</c:choose>
					
				</dsp:oparam>
		</dsp:droplet>
		</dsp:oparam>
		</dsp:droplet>
				
				<c:if test="${isPromoPage eq 'false'}">
					<div id="content" class="container_12 clearfix" role="main">
              
              		<%-- BBBSL-4343 DoubleClick Floodlight START
					<c:if test="${DoubleClickOn}">
						<c:if test="${(currentSiteId eq BedBathUSSite)}">
						   <c:set var="cat"><bbbc:config key="cat_category_bedBathUS" configName="RKGKeys" /></c:set>
						   <c:set var="src"><bbbc:config key="src_bedBathUS" configName="RKGKeys" /></c:set>
						   <c:set var="type"><bbbc:config key="type_1_bedBathUS" configName="RKGKeys" /></c:set>
						</c:if>
						 <c:if test="${(currentSiteId eq BuyBuyBabySite)}">
						   <c:set var="cat"><bbbc:config key="cat_category_baby" configName="RKGKeys" /></c:set>
						   <c:set var="src"><bbbc:config key="src_baby" configName="RKGKeys" /></c:set>
						   <c:set var="type"><bbbc:config key="type_1_baby" configName="RKGKeys" /></c:set>
						 </c:if>
						  <c:if test="${(currentSiteId eq BedBathCanadaSite)}">
		    		  		 <c:set var="cat"><bbbc:config key="cat_category_bedbathcanada" configName="RKGKeys" /></c:set>
		    		   		 <c:set var="src"><bbbc:config key="src_bedbathcanada" configName="RKGKeys" /></c:set>
		    		  		 <c:set var="type"><bbbc:config key="type_1_bedbathcanada" configName="RKGKeys" /></c:set>
		    		 </c:if>
						<dsp:include page="/_includes/double_click_tag.jsp">
							<dsp:param name="doubleClickParam"
										value="src=${src};type=${type};cat=${cat};u10=${categoryId};u11=${categoryL1}" />
						</dsp:include>
					</c:if>
					DoubleClick Floodlight END --%>
					
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
       
        			<%--BBBSL-6574 | Printing Certona WS call on source --%>
		             <div id="requestResponse" class="hidden">
		             </div> 
                    
				<%-- R2.1 implementation for SEO Static Text on Category Landing Page --%>
				<dsp:getvalueof param="landingTemplateVO.seoStaticText" var="seoStaticText" />
				<c:if test="${not empty seoStaticText}">
					<div class="grid_12 clearfix catSEOTxt">
						<c:out value="${seoStaticText}" escapeXml="false"/>
					</div>
				</c:if>
				<%-- BBBSL-4068 implementation for Popular Search Terms on Category Landing Page --%>
				<dsp:droplet name="/atg/dynamo/droplet/IsNull"> 
			  		<dsp:param name="value" param="landingTemplateVO.catPopSearch"/>
			  		<dsp:oparam name="false">
						<div class="grid_12 clearfix marTop_20">
							<h3><bbbl:label key="lbl_popular_search" language="${pageContext.request.locale.language}" /></h3>
							<div class="marTop_10">
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param param="landingTemplateVO.catPopSearch" name="array"/>
									<dsp:getvalueof param="count" var="count"/>
									<dsp:getvalueof param="size" var="size"/>
									<dsp:oparam name="output">
										<dsp:getvalueof param="element" var="CurrentPopTerm" />
										<a href="/store/s/${CurrentPopTerm}" title="${CurrentPopTerm}">${CurrentPopTerm}</a><c:if test="${count ne size}">,</c:if>
									</dsp:oparam>
								</dsp:droplet>
							</div>
						</div>
					</dsp:oparam>
				</dsp:droplet>
			</div>
				</c:if>
				
				<c:if test="${TellApartOn}">
					<bbb:tellApart actionType="pv" pageViewType="ProductCategory" />
				</c:if>
				<script type="text/javascript">
					var resx = new Object();
				</script>
			</c:otherwise>
	</c:choose>	
			
	
		
 </jsp:body>
	
		 <jsp:attribute name="footerContent">
		 <c:if test="${personalizedShopCatId ne categoryId}">
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
                    s.prop2= 'Main Level Page';
                }
                s.channel=pageName;
                s.prop1='Main Level Page';
                s.prop3='Main Level Page';// sub categories
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
        </c:if>
        </jsp:attribute>
	</bbb:pageContainer>

</dsp:page>