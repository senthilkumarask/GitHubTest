<dsp:page>
<dsp:importbean bean="/com/bbb/cms/droplet/HomePageTemplateDroplet" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />
<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
<dsp:importbean bean="/com/bbb/profile/session/SessionBean"/>
<dsp:getvalueof bean="SessionBean" var="bean"/>
 <dsp:droplet name="/com/bbb/commerce/browse/droplet/BBBCreateSDDCookieDroplet">
 <dsp:param name="sessionBean" value="${bean}" />
</dsp:droplet>	  
<dsp:getvalueof id="currentSiteId" bean="Site.id" />
<dsp:getvalueof var="appid" bean="Site.id" />
<dsp:importbean bean="/atg/userprofiling/Profile" /> 

<c:set var="BedBathUSSite" scope="request"><bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" /></c:set>
<c:set var="BuyBuyBabySite" scope="request"><bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" /></c:set>
<c:set var="BedBathCanadaSite" scope="request"><bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" /></c:set>

<c:set var="homePageCachingOn">
		<bbbc:config key="HOME_PAGE_CACHING_ON_OFF" configName="FlagDrivenFunctions" />
</c:set>
<c:set var="saSrc"><bbbc:config key="socialAnnexURL" configName="ThirdPartyURLs" /></c:set>
	
<c:choose>
	<c:when test="${currentSiteId eq BedBathUSSite}">
		<c:set var="googleSiteVerificationCode"><bbbc:config key="googleSiteVerificationCode_BedBathUS" configName="ThirdPartyURLs" /></c:set>
	</c:when>
	<c:when test="${currentSiteId eq BuyBuyBabySite}">
		<c:set var="googleSiteVerificationCode"><bbbc:config key="googleSiteVerificationCode_BuyBuyBaby" configName="ThirdPartyURLs" /></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="googleSiteVerificationCode"><bbbc:config key="googleSiteVerificationCode_BedBathCanada"	configName="ThirdPartyURLs" /></c:set>
	</c:otherwise>
</c:choose>

<bbb:pageContainer>
		<jsp:attribute name="amigoMeta">
			<meta name="y_key" content="15641bbded7565f4" />
			<meta name="google-site-verification" content="${googleSiteVerificationCode}" />
			<meta name="msvalidate.01" content="E9DEE7A60E7649CECE5CE4A8851B1856" />
		</jsp:attribute>

	
<jsp:attribute name="section">browse</jsp:attribute>
<jsp:attribute name="homepage">homepage</jsp:attribute>
<jsp:attribute name="pageWrapper">homePage useCertonaAjax useLiveClicker</jsp:attribute>
<jsp:attribute name="PageType">HomePage</jsp:attribute>
	<jsp:body>
		<%--BBBSL-4343 DoubleClick Floodlight START
    		 <c:if test="${DoubleClickOn}">
    		 <c:if test="${(currentSiteId eq BedBathUSSite)}">
    		   <c:set var="cat"><bbbc:config key="cat_home_bedBathUS" configName="RKGKeys" /></c:set>
    		   <c:set var="src"><bbbc:config key="src_bedBathUS" configName="RKGKeys" /></c:set>
    		   <c:set var="type"><bbbc:config key="type_1_bedBathUS" configName="RKGKeys" /></c:set>
    		 </c:if>
    		 <c:if test="${(currentSiteId eq BuyBuyBabySite)}">
    		   <c:set var="cat"><bbbc:config key="cat_home_baby" configName="RKGKeys" /></c:set>
    		   <c:set var="src"><bbbc:config key="src_baby" configName="RKGKeys" /></c:set>
    		   <c:set var="type"><bbbc:config key="type_1_baby" configName="RKGKeys" /></c:set>
    		 </c:if>
  			<c:if test="${(currentSiteId eq BedBathCanadaSite)}">
				  <c:set var="cat"><bbbc:config key="cat_home_bedbathcanada" configName="RKGKeys" /></c:set>
				  <c:set var="src"><bbbc:config key="src_bedbathcanada" configName="RKGKeys" /></c:set>
				  <c:set var="type"><bbbc:config key="type_1_bedbathcanada" configName="RKGKeys" /></c:set>
			</c:if> 
			 		<dsp:include page="/_includes/double_click_tag.jsp">
			 			<dsp:param name="doubleClickParam" value="src=${src};type=${type};cat=${cat};u4=null;u5=null"/>
			 			<dsp:param name="currentSiteId" value="${currentSiteId}"/>
			 		</dsp:include>
		 		</c:if>
	 		 DoubleClick Floodlight END --%>	
	 	
		<dsp:droplet name="HomePageTemplateDroplet">
			<dsp:param name="siteId" value="${currentSiteId}" />
			<dsp:oparam name="output">
			<%--For international customer loading different hero images story id BPS-1414 start --%>	
           	 	<div class="intlHeroRotator <c:if test='${homePageCachingOn eq true}'>intlHRCachingOn</c:if> hidden">
					<div class="container_12 textCenter padTop_25 padBottom_25">
           	 			<img width="20" height="20" alt="small loader" src="/_assets/global/images/widgets/small_loader.gif" />
           	 		</div>	
           	 	</div>

                <dsp:include page="/browse/hero_rotator.jsp" >
                    <dsp:param name="landingTemplateVO" param="homePageTemplateVO"/>
                </dsp:include>
					
					 	<%--For international customer loading different hero images story id BPS-1414 End --%>	
				<div id="content" class="container_12 clearfix" role="main">
					
					<div id="justForYou" class="clearfix loadAjaxContent ui-tabs grid_12" role="complementary">			 				
	 					<div class="grid_12 clearfix"><img width="20" height="20" alt="small loader" src="/_assets/global/images/widgets/small_loader.gif" /></div>
			 		</div>

					<dsp:droplet name="/atg/dynamo/droplet/IsNull"> 
			  			<dsp:param name="value" value="homePageTemplateVO.categoryContainer"/>
			  			<dsp:oparam name="false">
							 <dsp:include page="/cms/popular_categories.jsp" >
                    			<dsp:param name="categoryContainer" param="homePageTemplateVO.categoryContainer"/>
               				 </dsp:include>
						</dsp:oparam>
				    </dsp:droplet>
					
					<div class="categoryContent grid_12 noMarBot">
						<div class="contentImages clearfix marBottom_20">
						
						    <dsp:getvalueof var="promoBoxFirst" param="homePageTemplateVO.promoBoxFirst.promoBoxContent" />
							<div class="grid_3 alpha noOverflow">		
							<c:choose>
							<c:when test="${not empty promoBoxFirst}">
								<dsp:valueof param="homePageTemplateVO.promoBoxFirst.promoBoxContent" valueishtml="true"/>
							</c:when>
							<c:otherwise>
								<dsp:getvalueof var="imageAltText1" param="homePageTemplateVO.promoBoxFirst.imageAltText" />
								<dsp:getvalueof var="imgSrc1" param="homePageTemplateVO.promoBoxFirst.imageURL" />
								<dsp:getvalueof var="imgLink1" param="homePageTemplateVO.promoBoxFirst.imageLink" />
								<a href="${imgLink1}" title="${imageAltText1}"><img width="229" height="293" class="lazyLoad loadingGIF" src="" data-lazyloadsrc="${imgSrc1}" alt="${imageAltText1}" /></a>
							</c:otherwise>
							</c:choose>
							</div>
							<dsp:getvalueof var="promoBoxSecond" param="homePageTemplateVO.promoBoxSecond.promoBoxContent" />
								<div class="grid_3 noOverflow">	
									<c:choose>
										<c:when test="${not empty promoBoxSecond}">
											<dsp:valueof param="homePageTemplateVO.promoBoxSecond.promoBoxContent" valueishtml="true"/>
										</c:when>
										<c:otherwise>
							
								<dsp:getvalueof var="imageAltText2" param="homePageTemplateVO.promoBoxSecond.imageAltText" />
								<dsp:getvalueof var="imgSrc2" param="homePageTemplateVO.promoBoxSecond.imageURL" />
								<dsp:getvalueof var="imgLink2" param="homePageTemplateVO.promoBoxSecond.imageLink" />
								<a href="${imgLink2}" title="${imageAltText2}"><img width="229" height="293" class="lazyLoad loadingGIF" src="" data-lazyloadsrc="${imgSrc2}" src="" alt="${imageAltText2}" /></a>
							
							</c:otherwise>
									</c:choose>
							</div>
						    <c:set var="BuyBuyBabySite">
							<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
							</c:set>
							<c:choose>
								<c:when test="${currentSiteId == BuyBuyBabySite}">
									<dsp:droplet name="Switch">
									<dsp:param param="homePageTemplateVO.registryStatus" name="value"/>
									<dsp:oparam name="0">
										<dsp:include page="/cms/homepage_bb_look_for_registry.jsp" />
									</dsp:oparam>
									<dsp:oparam name="1">
										<dsp:include page="/cms/homepage_bb_create_registry.jsp" />
									</dsp:oparam>
									<dsp:oparam name="2">
										<dsp:include page="/cms/homepage_bb_find_create_registry.jsp" />
									</dsp:oparam>
								 </dsp:droplet>
								</c:when>
								<c:otherwise>
									<dsp:droplet name="Switch">
									<dsp:param param="homePageTemplateVO.registryStatus" name="value"/>
									<dsp:oparam name="0">
										<dsp:include page="/cms/homepage_look_for_registry.jsp" />
									</dsp:oparam>
									<dsp:oparam name="1">
										<dsp:include page="/cms/homepage_create_registry.jsp" />
									</dsp:oparam>
									<dsp:oparam name="2">
										<dsp:include page="/cms/homepage_find_create_registry.jsp" />
									</dsp:oparam>
								 </dsp:droplet>
								</c:otherwise>
							</c:choose>
							<div class="clear"></div>
						</div>
						<div class="clear"></div>
					</div>
					
					 
					
					<dsp:getvalueof var="categoryId" param="categoryId" />
					   
					 
					  
					 
                        <div id="funNewProducts" class="clearfix" role="complementary">
                            <div class="grid_12 clearfix"><img width="20" height="20" alt="small loader" src="/_assets/global/images/widgets/small_loader.gif" /></div>
                        </div>
                    
					 
					<dsp:getvalueof var="promo1" param="homePageTemplateVO.promoTierLayout1" />
			  		<dsp:droplet name="/atg/dynamo/droplet/IsNull"> 
			  			<dsp:param name="value" value="${promo1}"/>
			  			<dsp:oparam name="false">
			  			    
			                   <dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param param="homePageTemplateVO.promoTierLayout1" name="array" />
									<dsp:oparam name="output">
										<div class="categoryContent grid_12 marTop_20 noMarBot">
										
											<div class="contentImages clearfix marBottom_20">
											
												<dsp:getvalueof var="firstPromoContent1" param="element.promoBoxFirstVOList.promoBoxContent" />
												<div class="grid_3 alpha noOverflow">
												<c:choose>
													<c:when test="${not empty firstPromoContent1}">
														<dsp:valueof param="element.promoBoxFirstVOList.promoBoxContent" valueishtml="true"/>
													</c:when>
													<c:otherwise>
												
													<dsp:getvalueof var="imageAltText1" param="element.promoBoxFirstVOList.imageAltText" />
													<dsp:getvalueof var="imgSrc1" param="element.promoBoxFirstVOList.imageURL" />
													<dsp:getvalueof var="imgLink1" param="element.promoBoxFirstVOList.imageLink" />
													<a href="${imgLink1}" title="${imageAltText1}"><img width="229" height="340" class="lazyLoad loadingGIF" src="" data-lazyloadsrc="${imgSrc1}" alt="${imageAltText1}" /> </a>
												
													</c:otherwise>
												</c:choose>
												</div>
												<dsp:getvalueof var="secondPromoContent1" param="element.promoBoxSecondVOList.promoBoxContent" />
												<div class="grid_3 noOverflow">
												<c:choose>
													<c:when test="${not empty secondPromoContent1}">
														<dsp:valueof param="element.promoBoxSecondVOList.promoBoxContent" valueishtml="true"/>
													</c:when>
													<c:otherwise>
												
													<dsp:getvalueof var="imageAltText2" param="element.promoBoxSecondVOList.imageAltText" />
													<dsp:getvalueof var="imgSrc2" param="element.promoBoxSecondVOList.imageURL" />
													<dsp:getvalueof var="imgLink2" param="element.promoBoxSecondVOList.imageLink" />
													<a href="${imgLink2}" title="${imageAltText2}"><img width="229" height="340" class="lazyLoad loadingGIF" src="" data-lazyloadsrc="${imgSrc2}" alt="${imageAltText2}"/></a>
												
													</c:otherwise>
												</c:choose>
												</div>
												<dsp:getvalueof var="thirdPromoContent1" param="element.promoBoxThirdVOList.promoBoxContent" />
												<div class="grid_6 omega noOverflow">
												<c:choose>
													<c:when test="${not empty thirdPromoContent1}">
														<dsp:valueof param="element.promoBoxThirdVOList.promoBoxContent" valueishtml="true"/>
													</c:when>
													<c:otherwise>
												
													<dsp:getvalueof var="imageAltText3" param="element.promoBoxThirdVOList.imageAltText" />
													<dsp:getvalueof var="imgSrc3" param="element.promoBoxThirdVOList.imageURL" />
													<dsp:getvalueof var="imgLink3" param="element.promoBoxThirdVOList.imageLink" />
													<a href="${imgLink3}" title="${imageAltText3}"><img width="478" height="340" class="lazyLoad loadingGIF" src="" data-lazyloadsrc="${imgSrc3}" alt="${imageAltText3}" /> </a>
												
													</c:otherwise>
												</c:choose>
												</div>
												<div class="clear"></div>
											</div>
											<div class="clear"></div>
										</div>
									</dsp:oparam>
								</dsp:droplet>
				 				
				 	</dsp:oparam>
			  		</dsp:droplet> 
					<dsp:getvalueof var="promo2" param="homePageTemplateVO.promoTierLayout2" />
			  		<dsp:droplet name="/atg/dynamo/droplet/IsNull"> 
			  			<dsp:param name="value" value="${promo2}"/>
			  			<dsp:oparam name="false">
			  			
                           
									          
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param param="homePageTemplateVO.promoTierLayout2" name="array" />
									<dsp:oparam name="output">
									
								 <div class="categoryContent grid_12 marTop_20 noMarBot">
									<div class="contentImages clearfix marBottom_20">
									<dsp:getvalueof var="firstPromoContent2" param="element.promoBoxFirstVOList.promoBoxContent" />
									<div class="grid_9 alpha noOverflow">
									<c:choose>
										<c:when test="${not empty firstPromoContent2}">
											<dsp:valueof param="element.promoBoxFirstVOList.promoBoxContent" valueishtml="true"/>
										</c:when>
										<c:otherwise>
									
										
											<dsp:getvalueof var="imageAltText1" param="element.promoBoxFirstVOList.imageAltText" />
											<dsp:getvalueof var="imgSrc1" param="element.promoBoxFirstVOList.imageURL" />
											<dsp:getvalueof var="imgLink1" param="element.promoBoxFirstVOList.imageLink" />
											<a href="${imgLink1}" title="${imageAltText1}"><img width="727" height="183" class="lazyLoad loadingGIF" src="" data-lazyloadsrc="${imgSrc1}" alt="${imageAltText1}" /> </a>
										
										
										</c:otherwise>
									</c:choose>
									</div>
									<dsp:getvalueof var="secondPromoContent2" param="element.promoBoxSecondVOList.promoBoxContent" />
									<div class="grid_3 omega noOverflow">
									<c:choose>
										<c:when test="${not empty secondPromoContent2}">
											<dsp:valueof param="element.promoBoxSecondVOList.promoBoxContent" valueishtml="true"/>
										</c:when>
										<c:otherwise>
										
										
											<dsp:getvalueof var="imageAltText2" param="element.promoBoxSecondVOList.imageAltText" />
											<dsp:getvalueof var="imgSrc2" param="element.promoBoxSecondVOList.imageURL" />
											<dsp:getvalueof var="imgLink2" param="element.promoBoxSecondVOList.imageLink" />
											<a href="${imgLink2}" title="${imageAltText2}"><img width="229" height="183" class="lazyLoad loadingGIF" src="" data-lazyloadsrc="${imgSrc2}" alt="${imageAltText2}" /></a>
										
										</c:otherwise>
									</c:choose>
									</div>
									<div class="clear"></div>
								</div>
								<div class="clear"></div>
							</div>
									</dsp:oparam>
									</dsp:droplet>
								
			  		 
			  			</dsp:oparam>
			  		</dsp:droplet> 
			  		
			  		<dsp:getvalueof var="promo3" param="homePageTemplateVO.promoTierLayout3" />
			  		<dsp:droplet name="/atg/dynamo/droplet/IsNull"> 
			  			<dsp:param name="value" value="${promo3}"/>
			  			<dsp:oparam name="false">
			  			
                            
									          
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param param="homePageTemplateVO.promoTierLayout3" name="array" />
									<dsp:oparam name="output">
									
							<div class="categoryContent grid_12 marTop_20 noMarBot">
								<div class="contentImages clearfix marBottom_20">
									
									<dsp:getvalueof var="firstPromoContent3" param="element.promoBoxFirstVOList.promoBoxContent" />
									<div class="grid_4 alpha noOverflow">
									<c:choose>
										<c:when test="${not empty firstPromoContent3}">
											<dsp:valueof param="element.promoBoxFirstVOList.promoBoxContent" valueishtml="true"/>
										</c:when>
										<c:otherwise>
									 
											<dsp:getvalueof var="imageAltText1" param="element.promoBoxFirstVOList.imageAltText" />
											<dsp:getvalueof var="imgSrc1" param="element.promoBoxFirstVOList.imageURL" />
											<dsp:getvalueof var="imgLink1" param="element.promoBoxFirstVOList.imageLink" />
											<a href="${imgLink1}" title="${imageAltText1}"><img width="312" height="220" class="lazyLoad loadingGIF" src="" data-lazyloadsrc="${imgSrc1}" alt="${imageAltText1}" /> </a>
										
										</c:otherwise>
									</c:choose>
									</div>	
									<dsp:getvalueof var="secondPromoContent3" param="element.promoBoxSecondVOList.promoBoxContent" />
									<div class="grid_4 noOverflow" >
									<c:choose>
										<c:when test="${not empty secondPromoContent3}">
											<dsp:valueof param="element.promoBoxSecondVOList.promoBoxContent" valueishtml="true"/>
										</c:when>
										<c:otherwise>	
										
											<dsp:getvalueof var="imageAltText2" param="element.promoBoxSecondVOList.imageAltText" />
											<dsp:getvalueof var="imgSrc2" param="element.promoBoxSecondVOList.imageURL" />
											<dsp:getvalueof var="imgLink2" param="element.promoBoxSecondVOList.imageLink" />
											<a href="${imgLink2}" title="${imageAltText2}"><img width="312" height="220" class="lazyLoad loadingGIF" src="" data-lazyloadsrc="${imgSrc2}" alt="${imageAltText2}" /></a>
										
										</c:otherwise>
									</c:choose>
									</div>
									<dsp:getvalueof var="thirdPromoContent3" param="element.promoBoxThirdVOList.promoBoxContent" />
									<div class="grid_4 omega noOverflow" >
									<c:choose>
										<c:when test="${not empty thirdPromoContent3}">
											<dsp:valueof param="element.promoBoxThirdVOList.promoBoxContent" valueishtml="true"/>
										</c:when>
										<c:otherwise>	
										
										
											<dsp:getvalueof var="imageAltText3" param="element.promoBoxThirdVOList.imageAltText" />
											<dsp:getvalueof var="imgSrc3" param="element.promoBoxThirdVOList.imageURL" />
											<dsp:getvalueof var="imgLink3" param="element.promoBoxThirdVOList.imageLink" />
											<a href="${imgLink3}" title="${imageAltText3}"><img width="312" height="220" class="lazyLoad loadingGIF" src="" data-lazyloadsrc="${imgSrc3}" alt="${imageAltText3}" /> </a>
										
										
										</c:otherwise>
									</c:choose>
									</div>
									<div class="clear"></div>
								</div>
								<div class="clear"></div>
							</div>
									</dsp:oparam>
									</dsp:droplet>
								
			  		
			  			</dsp:oparam>
			  		</dsp:droplet> 
		 			
                     
                        <c:set var="cert_categoryId" scope="request"><dsp:valueof param="categoryId" /></c:set>
                        <c:set var="cert_funNewProductMax" scope="request"><bbbc:config key="HPFunNewProdMax" configName="CertonaKeys" /></c:set>
                        <c:set var="cert_clearanceDealMax" scope="request"><bbbc:config key="HPClearanceDealProdMax" configName="CertonaKeys" /></c:set>
                        <c:set var="cert_just4YouMax" scope="request"><bbbc:config key="HPJust4YouProdMax" configName="CertonaKeys" /></c:set>
                        <c:set var="cert_lastViewedTabLbl" scope="request"><bbbl:label key="lbl_homepage_last_viewed_items" language="<c:out param='${pageContext.request.locale.language}'/>" /></c:set>
                        <c:set var="cert_clearanceTabLbl" scope="request"><bbbl:label key="lbl_homepage_clearance_deals" language="<c:out param='${pageContext.request.locale.language}'/>" /></c:set>
                        <c:set var="cert_justForYouTabLbl" scope="request"><bbbl:label key="lbl_homepage_just_for_you" language="<c:out param='${pageContext.request.locale.language}'/>" /></c:set>
                        <c:set var="cert_scheme" scope="request">hp_cd;hp_fnp;hp_jfy</c:set>
                        <c:set var="cert_fnp" scope="request">hp_fnp</c:set>
                        <c:set var="cert_cd" scope="request">hp_cd</c:set>
                        <c:set var="cert_jfy" scope="request">hp_jfy</c:set>
                        <c:set var="cert_number" scope="request">${cert_clearanceDealMax};${cert_funNewProductMax};${cert_just4YouMax}</c:set>
                        <c:set var="cert_pageName" scope="request">Home Page</c:set>
                        <c:set var="cert_omniCrossSellPageName" scope="request">(home page)</c:set>
                        <c:set var="cert_clearanceDealsFlag" scope="request"><dsp:valueof param="homePageTemplateVO.clearanceDealsFlag" /></c:set>
                        <c:set var="cert_justForYouFlag" scope="request"><dsp:valueof param="homePageTemplateVO.justForYouFlag" /></c:set>
                        <c:set var="cert_flagFunNewProducts" scope="request">true</c:set>
                        <c:set var="cert_bottomTabs" scope="request">true</c:set>
                        <div id="certonaBottomTabs" class="clearfix loadAjaxContent" 
                            data-ajax-url="${contextPath}/common/certona_slots.jsp" 
                            data-ajax-target-divs="#certonaBottomTabs,#funNewProducts,#justForYou,#requestResponse" 
                            data-ajax-params-count="16" 
                            data-ajax-param1-name="categoryId" data-ajax-param1-value="${cert_categoryId}" 
                            data-ajax-param2-name="scheme" data-ajax-param2-value="${cert_scheme}" 
                            data-ajax-param3-name="number" data-ajax-param3-value="${cert_number}" 
                            data-ajax-param4-name="lastViewedTabLbl" data-ajax-param4-value="${cert_lastViewedTabLbl}" 
                            data-ajax-param5-name="justForYouTabLbl" data-ajax-param5-value="${cert_justForYouTabLbl}" 
                            data-ajax-param6-name="clearanceTabLbl" data-ajax-param6-value="${cert_clearanceTabLbl}" 
                            data-ajax-param7-name="fnp" data-ajax-param7-value="${cert_fnp}" 
                            data-ajax-param8-name="cd" data-ajax-param8-value="${cert_cd}" 
                            data-ajax-param9-name="jfy" data-ajax-param9-value="${cert_jfy}" 
                            data-ajax-param10-name="clearanceDealsFlagParam" data-ajax-param10-value="${cert_clearanceDealsFlag}" 
                            data-ajax-param11-name="justForYouFlagParam" data-ajax-param11-value="${cert_justForYouFlag}" 
                            data-ajax-param12-name="omniCrossSellPageName" data-ajax-param12-value="${cert_omniCrossSellPageName}" 
                            data-ajax-param13-name="certonaPageName" data-ajax-param13-value="${cert_pageName}" 
                            data-ajax-param14-name="funNewProductsFlagParam" data-ajax-param14-value="${cert_flagFunNewProducts}" 
                            data-ajax-param15-name="certonaBottomTabsFlagParam" data-ajax-param15-value="${cert_bottomTabs}"
                            data-ajax-param16-name="certonaSwitch" data-ajax-param16-value="${CertonaOn}" 
                        role="complementary">
                            <div class="grid_12 clearfix"><img width="20" height="20" alt="small loader" src="/_assets/global/images/widgets/small_loader.gif" /></div>
                        </div>
             
             <%--BBBSL-6574 | Printing Certona WS call on source --%>
		             <div id="requestResponse" class="hidden">
		             </div> 
             
			<dsp:droplet name="/atg/dynamo/droplet/IsNull"> 
				<dsp:param name="value" param="homePageTemplateVO.homePopSearch"/>
	  			<dsp:oparam name="false">
		  			<div class="grid_12 clearfix marTop_30">
						<h3><bbbl:label key="lbl_popular_search" language="${pageContext.request.locale.language}" /></h3>
						<div class="marTop_10">
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param param="homePageTemplateVO.homePopSearch" name="array"/>
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
		</dsp:oparam>
	</dsp:droplet>
	
	<c:if test="${homePageCachingOn eq false}">
	
	 <dsp:getvalueof value="${bean.showSDD}" var="sddFlag"/>
		 
	 <input id="showSDD" type="hidden" value="${sddFlag}" />
	
	<c:set var="sddOnHomePage" scope="request">
		<bbbc:config key="sddOnHomePage" configName="SameDayDeliveryKeys" />
	</c:set>
	
	<input type="hidden" value="${sddOnHomePage}" id="onOffSddFlag"/>
	
	</c:if>
    
<script type="text/javascript">
	var resx = new Object();
</script> 
              
                   <!--script to load all Library and Logic js file for UGC product start-->                    
 <script type="text/javascript">
var sa_page="1",iOS8=navigator.userAgent.match(/iPad/i);
(
		function() {
			function sa_async_load() {
				
var sa = document.createElement('script');
sa.type = 'text/javascript';
sa.async = true;
sa.src = '${saSrc}';
var sax = document.getElementsByTagName('script')[0];
sax.parentNode.insertBefore(sa, sax);
}
			
if (window.attachEvent) {
	window.attachEvent('onload', sa_async_load);
	}
    else if(iOS8){
		window.addEventListener('load', sa_async_load(),false);
		}else{
		window.addEventListener('load', sa_async_load,false);
	    }
	})
	();
</script>
<!--script for UGC product End Here-->

  <!--Div is used to display UGC photo and upload photo upload-->
                        
 <div id="sa_s22_instagram" class="container_12 sa_s22_instagram_product"></div> 

             
<!-- display UGC photo and upload photo  End Here-->        

	</jsp:body>
	 
	 <jsp:attribute name="footerContent">  
		    <c:if test="${fn:containsIgnoreCase(homePageCachingOn, 'FALSE')  || homePageCachingOn eq null  ||  empty homePageCachingOn}">   
		     <script type="text/javascript">       
		           if(typeof s !=='undefined') {
					s.pageName='Home Page';
		            s.channel='Home Page'; <%--set s.channel equal to the main nav category--%>
		            s.prop1='Home Page';<%--set prop1 equal to the page type--%>
		            s.prop2='Home Page';
		            s.prop3='Home Page';<%-- sub categories--%>
					s.eVar9='${pageContext.request.serverName}';
		            var s_code=s.t();
		            if(s_code)document.write(s_code);           
		           }           
		          
		           /* function pdpOmnitureProxy(productId, event,event1,desc) {
					   if(event == 'crossSell') {
						   if (typeof s_crossSell === 'function') { s_crossSell(); }
					   } 
					   if(event1 == 'pfm') {
						
						   if (typeof s_crossSell === 'function') { s_crossSell(desc); }
					   } 
					   
				   }
		            
		           function pdpOmnitureProxy(event1,desc) {
		     		  
		    		   if(event1 == 'pfm') {
		    			
		    			   if (typeof s_crossSell === 'function') { s_crossSell(desc); }
		    		   } 
		    		   
		    	   }*/
		           
		        </script>
		      </c:if>

        </jsp:attribute>
</bbb:pageContainer>
<c:if test="${TellApartOn}">
	<bbb:tellApart actionType="pv" />		
</c:if>
</dsp:page>