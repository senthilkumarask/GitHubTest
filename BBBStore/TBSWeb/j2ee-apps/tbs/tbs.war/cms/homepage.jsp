<dsp:page>
<dsp:importbean bean="/com/bbb/cms/droplet/HomePageTemplateDroplet" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean bean="/com/bbb/certona/CertonaConfig" />
<dsp:importbean bean="/com/bbb/certona/droplet/CertonaDroplet"/>
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />
<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="atg/dynamo/service/IdGenerator"/>

<dsp:getvalueof id="dcPrefix" bean="IdGenerator.dcPrefix" />
<dsp:getvalueof id="currentSiteId" bean="Site.id" />
<dsp:getvalueof var="appid" bean="Site.id" />
<c:set var="CertonaContext" value="" scope="request" />
<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${appid}" />

<c:set var="TBS_BedBathUSSite" scope="request"><bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" /></c:set>
<c:set var="TBS_BuyBuyBabySite" scope="request"><bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" /></c:set>
<c:set var="TBS_BedBathCanadaSite" scope="request"><bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" /></c:set>

<c:choose>
	<c:when test="${currentSiteId eq TBS_BedBathUSSite}">
		<c:set var="googleSiteVerificationCode"><bbbc:config key="googleSiteVerificationCode_BedBathUS" configName="ThirdPartyURLs" /></c:set>
	</c:when>
	<c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
		<c:set var="googleSiteVerificationCode"><bbbc:config key="googleSiteVerificationCode_BuyBuyBaby" configName="ThirdPartyURLs" /></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="googleSiteVerificationCode"><bbbc:config key="googleSiteVerificationCode_BedBathCanada"	configName="ThirdPartyURLs" /></c:set>
	</c:otherwise>
</c:choose>

<bbb:pageContainer>
		<jsp:attribute name="bodyClass">home</jsp:attribute>
		<jsp:attribute name="amigoMeta">
			<meta name="y_key" content="15641bbded7565f4" />
			<meta name="google-site-verification" content="${googleSiteVerificationCode}" />
			<meta name="msvalidate.01" content="E9DEE7A60E7649CECE5CE4A8851B1856" />
		</jsp:attribute>


<jsp:attribute name="section">browse</jsp:attribute>
<jsp:attribute name="homepage">homepage</jsp:attribute>
<jsp:attribute name="pageWrapper">homePage useCertonaAjax useCertonaJs</jsp:attribute>
<jsp:attribute name="PageType">HomePage</jsp:attribute>
	<jsp:body>
		<dsp:droplet name="HomePageTemplateDroplet">
			<dsp:param name="siteId" value="${currentSiteId}" />
			<dsp:oparam name="output">
				
				<div class="row">
					<div class="small-12 large-6 columns no-padding">
						<dsp:getvalueof var="logoImage" param="homePageTemplateVO.logoImage" />
						<h1>
						  <c:choose>
							<c:when test="${fn:containsIgnoreCase(dcPrefix, 'STG3')}">
								<span style="font-size:26px; color:red;">The Beyond Store Training Site</span>
							</c:when>
							<c:otherwise>
								The Beyond Store
							</c:otherwise>
						</c:choose>
						<a href="#" id="storeNumber" data-reveal-id="storeNumberModal" class="">(<span id="storeNum"><c:out value="${sessionScope.storenumber}"/></span>)</a>
						</h1>
					</div>
					<div class="small-12 large-6 columns no-padding">
					<dsp:droplet name="/atg/dynamo/droplet/IsNull">
						<dsp:param name="value" value="homePageTemplateVO.announcement"/>
						<dsp:oparam name="false">
							<dsp:getvalueof var="announcementContent" param="homePageTemplateVO.announcement.promoBoxContent" />
							<c:choose>
								<c:when test="${not empty announcementContent}">
									<dsp:valueof param="homePageTemplateVO.announcement.promoBoxContent" valueishtml="true"/>
								</c:when>
								<c:otherwise>
									<dsp:getvalueof var="aImageAltText" param="homePageTemplateVO.announcement.imageAltText" />
									<dsp:getvalueof var="aImgSrc" param="homePageTemplateVO.announcement.imageURL" />
									<dsp:getvalueof var="aImgLink" param="homePageTemplateVO.announcement.imageLink" />
									<c:choose>
									    <c:when test="${not empty aImgSrc}">
											<a href="${aImgLink}" title="${aImageAltText}">
												<img data-interchange="[${aImgSrc}, (default)], [${aImgSrc}, (small)], [${aImgSrc}, (medium)],  [${aImgSrc}, (large)]" class="stretch">
												<noscript><img src="${aImgSrc}"></noscript>
											</a>
										</c:when>
										<c:otherwise>
										</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>
						</dsp:oparam>
					   </dsp:droplet>
					</div>
				</div>
				
				<dsp:droplet name="/atg/dynamo/droplet/IsNull">
					<dsp:param name="value" value="homePageTemplateVO.categoryContainer"/>
					<dsp:oparam name="false">
						<dsp:include page="/cms/popular_categories.jsp" >
							<dsp:param name="categoryContainer" param="homePageTemplateVO.categoryContainer"/>
						</dsp:include>
					</dsp:oparam>
				</dsp:droplet>
				
				<dsp:droplet name="/atg/dynamo/droplet/IsNull">
					<dsp:param name="value" value="homePageTemplateVO.featuredCategoryContainer"/>
					<dsp:oparam name="false">
						<dsp:include page="/cms/featured_categories.jsp" >
							<dsp:param name="categoryContainer" param="homePageTemplateVO.featuredCategoryContainer"/>
						</dsp:include>
					</dsp:oparam>
				</dsp:droplet>
				
				
				<div class="row">
					<div class="small-12 medium-6 large-3 columns radius-tile">
						<dsp:getvalueof var="promoBoxFirst" param="homePageTemplateVO.promoBoxFirst.promoBoxContent" />
						<c:choose>
							<c:when test="${not empty promoBoxFirst}">
								<dsp:valueof param="homePageTemplateVO.promoBoxFirst.promoBoxContent" valueishtml="true"/>
							</c:when>
							<c:otherwise>
								<dsp:getvalueof var="imageAltText1" param="homePageTemplateVO.promoBoxFirst.imageAltText" />
								<dsp:getvalueof var="imgSrc1" param="homePageTemplateVO.promoBoxFirst.imageURL" />
								<dsp:getvalueof var="imgLink1" param="homePageTemplateVO.promoBoxFirst.imageLink" />
								<dsp:getvalueof var="promoBoxTitle1" param="homePageTemplateVO.promoBoxFirst.promoBoxTitle" />
								<a href="${imgLink1}" title="${imageAltText1}">
									<img alt="${imageAltText1}" data-interchange="[${imgSrc1}, (default)], [${imgSrc1}, (small)], [${imgSrc1}, (medium)],  [${imgSrc1}, (large)]">
									<noscript><img src="${imgSrc1}"></noscript>
								</a>
							</c:otherwise>
						</c:choose>
					</div>
					
					<div class="small-12 medium-6 large-3 columns radius-tile">
						<dsp:getvalueof var="promoBoxSecond" param="homePageTemplateVO.promoBoxSecond.promoBoxContent" />
						<c:choose>
							<c:when test="${not empty promoBoxSecond}">
								<dsp:valueof param="homePageTemplateVO.promoBoxSecond.promoBoxContent" valueishtml="true"/>
							</c:when>
							<c:otherwise>
								<dsp:getvalueof var="imageAltText2" param="homePageTemplateVO.promoBoxSecond.imageAltText" />
								<dsp:getvalueof var="imgSrc2" param="homePageTemplateVO.promoBoxSecond.imageURL" />
								<dsp:getvalueof var="imgLink2" param="homePageTemplateVO.promoBoxSecond.imageLink" />
								<dsp:getvalueof var="promoBoxTitle2" param="homePageTemplateVO.promoBoxSecond.promoBoxTitle" />	
								<a href="${imgLink2}" title="${imageAltText2}">
									<img alt="${imageAltText1}" data-interchange="[${imgSrc2}, (default)], [${imgSrc2}, (small)], [${imgSrc2}, (medium)],  [${imgSrc2}, (large)]">
									<noscript><img src="${imgSrc2}"></noscript>
								</a>
							</c:otherwise>
						</c:choose>
					</div>
					
					<div class="small-12 large-6 columns radius-tile">
				
						<c:set var="TBS_BuyBuyBabySite">
							<bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
						</c:set>
						<c:choose>
							<c:when test="${currentSiteId == TBS_BuyBuyBabySite}">
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
					</div>
				</div>
						
					<dsp:getvalueof var="categoryId" param="categoryId" />

					<dsp:getvalueof var="promo1" param="homePageTemplateVO.promoTierLayout1" />
					<dsp:droplet name="/atg/dynamo/droplet/IsNull">
						<dsp:param name="value" value="${promo1}"/>
						<dsp:oparam name="false">
							<div class="row">
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param param="homePageTemplateVO.promoTierLayout1" name="array" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="firstPromoContent1" param="element.promoBoxFirstVOList.promoBoxContent" />
										<c:choose>
											<c:when test="${not empty firstPromoContent1}">
												<div class="small-12 medium-6 large-3 columns radius-tile"><dsp:valueof param="element.promoBoxFirstVOList.promoBoxContent" valueishtml="true"/></div>
											</c:when>
											<c:otherwise>
												<dsp:getvalueof var="imageAltText1" param="element.promoBoxFirstVOList.imageAltText" />
												<dsp:getvalueof var="imgSrc1" param="element.promoBoxFirstVOList.imageURL" />
												<dsp:getvalueof var="imgLink1" param="element.promoBoxFirstVOList.imageLink" />
												<dsp:getvalueof var="promoBoxTitle1" param="element.promoBoxFirstVOList.promoBoxTitle" />	
												<div class="small-12 medium-6 large-3 columns radius-tile">
												
													<a href="${imgLink1}" title="${imageAltText1}">
														<img alt="${imageAltText1}" data-interchange="[${imgSrc1}, (default)], [${imgSrc1}, (small)], [${imgSrc1}, (medium)],  [${imgSrc1}, (large)]">
														<noscript><img src="${imgSrc1}"></noscript>
													</a>
												</div>
											</c:otherwise>
										</c:choose>
										<dsp:getvalueof var="secondPromoContent1" param="element.promoBoxSecondVOList.promoBoxContent" />
										<c:choose>
											<c:when test="${not empty secondPromoContent1}">
												<div class="small-12 medium-6 large-3 columns radius-tile"><dsp:valueof param="element.promoBoxSecondVOList.promoBoxContent" valueishtml="true"/></div>
											</c:when>
											<c:otherwise>
												<dsp:getvalueof var="imageAltText2" param="element.promoBoxSecondVOList.imageAltText" />
												<dsp:getvalueof var="imgSrc2" param="element.promoBoxSecondVOList.imageURL" />
												<dsp:getvalueof var="imgLink2" param="element.promoBoxSecondVOList.imageLink" />
												<dsp:getvalueof var="promoBoxTitle2" param="element.promoBoxSecondVOList.promoBoxTitle" />
												<div class="small-12 medium-6 large-3 columns radius-tile">
												
													<a href="${imgLink2}" title="${imageAltText1}">
														<img alt="${imageAltText1}" data-interchange="[${imgSrc2}, (default)], [${imgSrc2}, (small)], [${imgSrc2}, (medium)],  [${imgSrc2}, (large)]">
														<noscript><img src="${imgSrc2}"></noscript>
													</a>
												</div>
											</c:otherwise>
										</c:choose>
										<dsp:getvalueof var="thirdPromoContent1" param="element.promoBoxThirdVOList.promoBoxContent" />
											
											<c:choose>
												<c:when test="${not empty thirdPromoContent1}">
													<div class="small-12 large-6 columns radius-tile"><dsp:valueof param="element.promoBoxThirdVOList.promoBoxContent" valueishtml="true"/></div>
												</c:when>
												<c:otherwise>
											
												<dsp:getvalueof var="imageAltText3" param="element.promoBoxThirdVOList.imageAltText" />
												<dsp:getvalueof var="imgSrc3" param="element.promoBoxThirdVOList.imageURL" />
												<dsp:getvalueof var="imgLink3" param="element.promoBoxThirdVOList.imageLink" />
												<dsp:getvalueof var="promoBoxTitle3" param="element.promoBoxThirdVOList.promoBoxTitle" />
												<div class="small-12 large-6 columns radius-tile">
													<a href="${imgLink3}" title="${imageAltText3}">
														<img data-interchange="[${imgSrc3}, (default)], [${imgSrc3}, (small)], [${imgSrc3}, (medium)], [${imgSrc3}, (large)]">
														<noscript><img src="${imgSrc3}"></noscript>
													</a>
												</div>
												</c:otherwise>
											</c:choose>
									</dsp:oparam>
								</dsp:droplet>
							</div>
						</dsp:oparam>
					</dsp:droplet>
			
					<dsp:getvalueof var="promo2" param="homePageTemplateVO.promoTierLayout2" />
					<dsp:droplet name="/atg/dynamo/droplet/IsNull">
						<dsp:param name="value" value="${promo2}"/>
						<dsp:oparam name="false">
							<div class="row">
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param param="homePageTemplateVO.promoTierLayout2" name="array" />
									<dsp:oparam name="output">
									<dsp:getvalueof var="firstPromoContent2" param="element.promoBoxFirstVOList.promoBoxContent" />
									<c:choose>
										<c:when test="${not empty firstPromoContent2}">
											<div class="small-12 medium-12 large-9 columns radius-tile"><dsp:valueof param="element.promoBoxFirstVOList.promoBoxContent" valueishtml="true"/></div>
										</c:when>
										<c:otherwise>
											
											<dsp:getvalueof var="imageAltText1" param="element.promoBoxFirstVOList.imageAltText" />
											<dsp:getvalueof var="imgSrc1" param="element.promoBoxFirstVOList.imageURL" />
											<dsp:getvalueof var="imgLink1" param="element.promoBoxFirstVOList.imageLink" />
											<dsp:getvalueof var="promoBoxTitle1" param="element.promoBoxFirstVOList.promoBoxTitle" />
											<div class="small-12 medium-12 large-9 columns radius-tile">
													
												<a href="${imgLink1}" title="${imageAltText1}">
													<img data-interchange="[${imgSrc1}, (default)], [${imgSrc1}, (small)], [${imgSrc1}, (medium)],  [${imgSrc1}, (large)]">
													<noscript><img src="${imgSrc1}"></noscript>
												</a>
											</div>
										</c:otherwise>
									</c:choose>
									
									<dsp:getvalueof var="secondPromoContent2" param="element.promoBoxSecondVOList.promoBoxContent" />
									
									<c:choose>
										<c:when test="${not empty secondPromoContent2}">
											<div class="small-12 medium-6 large-3 columns radius-tile"><dsp:valueof param="element.promoBoxSecondVOList.promoBoxContent" valueishtml="true"/></div>
										</c:when>
										<c:otherwise>
											<dsp:getvalueof var="imageAltText2" param="element.promoBoxSecondVOList.imageAltText" />
											<dsp:getvalueof var="imgSrc2" param="element.promoBoxSecondVOList.imageURL" />
											<dsp:getvalueof var="imgLink2" param="element.promoBoxSecondVOList.imageLink" />
											<dsp:getvalueof var="promoBoxTitle2" param="element.promoBoxSecondVOList.promoBoxTitle" />
											<div class="small-12 medium-6 large-3 columns radius-tile">
												
												<a href="${imgLink2}" title="${imageAltText1}">
													<img alt="${imageAltText1}" data-interchange="[${imgSrc2}, (default)], [${imgSrc2}, (small)], [${imgSrc2}, (medium)],  [${imgSrc2}, (large)]">
													<noscript><img src="${imgSrc2}"></noscript>
												</a>
											</div>
										</c:otherwise>
									</c:choose>
									</dsp:oparam>
									</dsp:droplet>
							</div>
						</dsp:oparam>
					</dsp:droplet>
				
					<dsp:getvalueof var="promo3" param="homePageTemplateVO.promoTierLayout3" />
					<dsp:droplet name="/atg/dynamo/droplet/IsNull">
						<dsp:param name="value" value="${promo3}"/>
						<dsp:oparam name="false">
							<div class="row">
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param param="homePageTemplateVO.promoTierLayout3" name="array" />
									<dsp:oparam name="output">
									<dsp:getvalueof var="firstPromoContent3" param="element.promoBoxFirstVOList.promoBoxContent" />
									<c:choose>
										<c:when test="${not empty firstPromoContent3}">
											<div class="small-12 medium-12 large-9 columns radius-tile"><dsp:valueof param="element.promoBoxFirstVOList.promoBoxContent" valueishtml="true"/></div>
										</c:when>
										<c:otherwise>

											<dsp:getvalueof var="imageAltText1" param="element.promoBoxFirstVOList.imageAltText" />
											<dsp:getvalueof var="imgSrc1" param="element.promoBoxFirstVOList.imageURL" />
											<dsp:getvalueof var="imgLink1" param="element.promoBoxFirstVOList.imageLink" />
											<dsp:getvalueof var="promoBoxTitle1" param="element.promoBoxFirstVOList.promoBoxTitle" />
											<div class="small-12 medium-12 large-9 columns radius-tile">
													
												<a href="${imgLink1}" title="${imageAltText1}">
													<img data-interchange="[${imgSrc1}, (default)], [${imgSrc1}, (small)], [${imgSrc1}, (medium)],  [${imgSrc1}, (large)]">
													<noscript><img src="${imgSrc1}"></noscript>
												</a>
											</div>

										</c:otherwise>
									</c:choose>
									
									<dsp:getvalueof var="secondPromoContent3" param="element.promoBoxSecondVOList.promoBoxContent" />
									
									<c:choose>
										<c:when test="${not empty secondPromoContent3}">
											<div class="small-12 medium-6 large-3 columns radius-tile"><dsp:valueof param="element.promoBoxSecondVOList.promoBoxContent" valueishtml="true"/></div>
										</c:when>
										<c:otherwise>

											<dsp:getvalueof var="imageAltText2" param="element.promoBoxSecondVOList.imageAltText" />
											<dsp:getvalueof var="imgSrc2" param="element.promoBoxSecondVOList.imageURL" />
											<dsp:getvalueof var="imgLink2" param="element.promoBoxSecondVOList.imageLink" />
											<dsp:getvalueof var="promoBoxTitle2" param="element.promoBoxSecondVOList.promoBoxTitle" />
											<div class="small-12 medium-6 large-3 columns radius-tile">
												
												<a href="${imgLink2}" title="${imageAltText2}">
													<img alt="${imageAltText2}" data-interchange="[${imgSrc2}, (default)], [${imgSrc2}, (small)], [${imgSrc2}, (medium)],  [${imgSrc2}, (large)]">
													<noscript><img src="${imgSrc2}"></noscript>
												</a>
											</div>

										</c:otherwise>
									</c:choose>
									
									<dsp:getvalueof var="thirdPromoContent3" param="element.promoBoxThirdVOList.promoBoxContent" />
									
									<c:choose>
										<c:when test="${not empty thirdPromoContent3}">
											<div class="small-12 medium-6 large-3 columns radius-tile"><dsp:valueof param="element.promoBoxThirdVOList.promoBoxContent" valueishtml="true"/></div>
										</c:when>
										<c:otherwise>
											<dsp:getvalueof var="imageAltText3" param="element.promoBoxThirdVOList.imageAltText" />
											<dsp:getvalueof var="imgSrc3" param="element.promoBoxThirdVOList.imageURL" />
											<dsp:getvalueof var="imgLink3" param="element.promoBoxThirdVOList.imageLink" />
											<dsp:getvalueof var="promoBoxTitle3" param="element.promoBoxThirdVOList.promoBoxTitle" />
											<div class="small-12 medium-6 large-3 columns radius-tile">
												
												<a href="${imgLink3}" title="${imageAltText3}">
													<img alt="${imageAltText3}" data-interchange="[${imgSrc3}, (default)], [${imgSrc3}, (small)], [${imgSrc3}, (medium)],  [${imgSrc3}, (large)]">
													<noscript><img src="${imgSrc3}"></noscript>
												</a>
											</div>
										</c:otherwise>
									</c:choose>
									
									</dsp:oparam>
									</dsp:droplet>

							</div>
						</dsp:oparam>
					</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>
	<dsp:droplet name="Switch">
		<dsp:param name="value" bean="Profile.transient" />
		<dsp:oparam name="false">
			<dsp:getvalueof var="userId" bean="Profile.id" />
		</dsp:oparam>
		<dsp:oparam name="true">
			<dsp:getvalueof var="userId" value="" />
		</dsp:oparam>
	</dsp:droplet>
 	 
<%-- There is no container on HP and there're no porduct information to be sent to Certona --%>
  	<%-- <c:set var="cert_categoryId" scope="request"><dsp:valueof param="categoryId" /></c:set>
	<c:set var="cert_scheme" scope="request">hp_cd</c:set>
	<c:set var="cert_cd" scope="request">hp_cd</c:set>
	<c:set var="cert_pageName" scope="request">Home Page</c:set>
	<dsp:droplet name="CertonaDroplet">
      <dsp:param name="scheme" value="${cert_scheme}"/>
      <dsp:param name="userid" value="${userId}"/>
      <dsp:param name="siteId" value="${appid}"/>
      <dsp:oparam name="output">
          <dsp:getvalueof var="certona_clearenceProductsList" param="certonaResponseVO.resonanceMap.${cert_cd}.productsVOsList"/>
          <dsp:getvalueof var="linksCertona" param="certonaResponseVO.resxLinks"/>
          <dsp:getvalueof var="pageIdCertona" param="certonaResponseVO.pageId"/>
      </dsp:oparam>
      <dsp:oparam name="error">
          <c:set var="displayFlag" value="false"/>
      </dsp:oparam>
      <dsp:oparam name="empty">
          <c:set var="displayFlag" value="false"/>
      </dsp:oparam>
  </dsp:droplet>  --%>
   

<script type="text/javascript">
	var resx = new Object();
	resx.appid = "${appIdCertona}";
	resx.top1 = 100000;
	resx.top2 = 100000;
	resx.customerid = "${userId}";
    resx.pageid = "${pageIdCertona}";
    resx.links = '';

</script>

	</jsp:body>
	
	<jsp:attribute name="footerContent">
	
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

		function pdpOmnitureProxy(event1,desc) {

			if(event1 == 'pfm') {

				if (typeof s_crossSell === 'function') { s_crossSell(desc); }
			}

		}

		</script>
		</jsp:attribute>

</bbb:pageContainer>

<c:if test="${TellApartOn}">
	<bbb:tellApart actionType="pv" />
</c:if>
</dsp:page>
