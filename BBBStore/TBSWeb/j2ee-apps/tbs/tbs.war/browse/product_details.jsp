<dsp:page>

	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/EverLivingPDPDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BreadcrumbDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductViewedDroplet" />
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFlyoutDroplet"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/targeting/TargetingRandom" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean	bean="/com/bbb/profile/session/SessionBean"/>
	<dsp:importbean	bean="/com/bbb/commerce/browse/droplet/MinimalProductDetailDroplet" />
	<dsp:importbean	bean="/com/bbb/tbs/commerce/browse/droplet/TBSProductColorsDetailDroplet"/>
	<dsp:importbean bean="/com/bbb/certona/CertonaConfig" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BrandDetailDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/RedirectDroplet" />
	<dsp:importbean bean="/com/bbb/framework/security/ValidateParametersDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/GetApplicableLTLShippingMethodsDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/com/bbb/commerce/droplet/TBSItemExclusionDroplet"/>
	<dsp:importbean bean="/atg/repository/seo/CanonicalItemLink"/>
	<dsp:importbean bean="/atg/repository/seo/GuideItemLink"/>
	<dsp:importbean	bean="/com/bbb/commerce/browse/droplet/BVReviewContentDroplet" />
	<dsp:importbean	bean="/com/bbb/account/droplet/BBBConfigKeysDroplet"/>
	<dsp:importbean	bean="/com/bbb/account/droplet/BBBURLEncodingDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/DisplayShippingRestrictionsDroplet"/>
	<dsp:importbean bean="/atg/registry/RepositoryTargeters/TargeterPDPSecond"/>
	<dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>
	<dsp:getvalueof var="registryId" value=""/>
	<dsp:getvalueof var="parentProductId" param="productId" />
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof var="poc" param="poc" />
	<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${appid}" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
	<%-- <dsp:getvalueof var="CategoryId" param="categoryId" />

	<c:set var="poBoxEnabled" scope="request"><bbbc:config key="shipTo_POBoxOn" configName="FlagDrivenFunctions"/></c:set> --%>
 	<c:set var="scene7Path" scope="request"><bbbc:config key="scene7_url" configName="ThirdPartyURLs"/></c:set>
    <%-- BBBI-3048 Omniture Tagging Start--%>
	<dsp:getvalueof var="keyword" param="keyword"/>
	<dsp:getvalueof var="brandId" param="brandId"/>
	<dsp:getvalueof var="categoryId" param="categoryId"/>
	<dsp:getvalueof var="fromPage" param="fromPage"/>
	
	<dsp:getvalueof var="vendorParam" bean="SessionBean.vendorParam"/>
	<c:set var="fireEvent90" value="false"/>
	<c:set var="customizeCTACodes" scope="request">
		<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
	</c:set>
	
	<c:choose>
		<c:when test="${not empty sessionScope.boostCode && sessionScope.boostCode != '00'}">
			<c:set var="l2l3BoostFlag"><bbbc:config key="L2L3BoostFlag" configName="FlagDrivenFunctions"/></c:set> 
			<c:set var="brandsBoostFlag"><bbbc:config key="BrandsBoostFlag" configName="FlagDrivenFunctions"/></c:set> 
			<c:set var="keywordBoostFlag"><bbbc:config key="KeywordBoostFlag" configName="FlagDrivenFunctions"/></c:set> 
			<c:if test="${(((keywordBoostFlag && (not empty keyword || fromPage == 'searchPage')) 
				|| (brandsBoostFlag && (not empty brandId || fromPage == 'brandPage')) 
				|| (l2l3BoostFlag && (not empty categoryId || fromPage == 'categoryPage'))) && (isEndecaControl eq false))}">
	    		<c:set var="fireEvent90" value="true"/>
	    	</c:if>       
		</c:when>
		<c:when test="${(not empty vendorParam) && (not empty keyword  || fromPage == 'searchPage')}">
			<c:set var="fireEvent90" value="true"/> 
		</c:when>
	</c:choose>
	<%-- BBBI-3048 Omniture Tagging End--%>
	
	<%-- Validate external parameters --%>
	<dsp:droplet name="ValidateParametersDroplet">
		<dsp:param value="productId" name="paramArray" />
		<dsp:param value="${param.productId}" name="paramsValuesArray" />
		<dsp:oparam name="error">
			<dsp:droplet name="RedirectDroplet">
				<dsp:param name="url" value="/404.jsp" />
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>

	<c:if test="${showFlag == '1'}">
		<c:set var="pageVariation" value="bc" scope="request" />
		<c:set var="pageWrapperClass" value="productDetailsCollection" scope="request" />
	</c:if>
	<c:set var="CertonaContext" value="" scope="request" />

	<c:set var="omniProp5" value="Product" />
	<c:set var="productNotfound" value="false" />
	<c:set var="showQA" value="" scope="request" />

	<c:set var="askAndAnswerEnabled" value="false" scope="request" />
	
	<c:set var="scene7DomainPath">
		//s7dx.scene7.com/is/image/BedBathandBeyond
	</c:set>

	<bbb:pageContainer pageVariation="${pageVariation}">
		<jsp:attribute name="section">browse</jsp:attribute>
		<jsp:attribute name="PageType">ProductDetails</jsp:attribute>
		<jsp:attribute name="pageWrapper">productDetails ${pageWrapperClass} useStoreLocator useLiveClicker useBazaarVoice useFB  useCertonaAjax useScene7</jsp:attribute>
		<jsp:attribute name="bodyClass">pdp</jsp:attribute>
		<jsp:body>


		<jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request" />
		<c:set target="${placeHolderMap}" property="contextPath" value="${contextPath}" />
		<c:set target="${placeHolderMap}" property="imagePath" value="${imagePath}" />
		<c:set var="TBS_BedBathUSSite">
			<bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" />
		</c:set>
		<c:set var="enableKatoriFlag" scope="request"><bbbc:config key="enableKatori" configName="EXIMKeys"/></c:set>
		<input type="hidden" name="enableKatoriFlag" value="${enableKatoriFlag}"/>

		<c:set var="TBS_BuyBuyBabySite">
			<bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
		</c:set>
		<c:set var="TBS_BedBathCanadaSite">
			<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
		</c:set>
		<c:set var="AttributePDPTOP">
			<bbbl:label key='lbl_pdp_attributes_top' language="${pageContext.request.locale.language}" />
		</c:set>
		<c:set var="AttributePDPMiddle">
			<bbbl:label key='lbl_pdp_attributes_middle' language="${pageContext.request.locale.language}" />
		</c:set>
		<c:set var="AttributePDPPrice">
			<bbbl:label key='lbl_pdp_attributes_price' language="${pageContext.request.locale.language}" />
		</c:set>
		<c:set var="TruckSrc">
			<bbbc:config key="truck_image_src" configName="LTLConfig_KEYS" />
		</c:set>
		<c:set var="QuestMarkSrc">
		<bbbc:config key="quesmark_image_pdp" configName="LTLConfig_KEYS" />
		</c:set>
		<c:set var="WarrantyOn" scope="request">
			<bbbc:config key="WarrantyOn" configName="FlagDrivenFunctions" />
		</c:set>
	<c:choose>
	<c:when test="${appid == TBS_BedBathCanadaSite}">
	<c:set var="clearanceCategory">
		<bbbc:config key="BedBathCanada_clearanceCategories" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="whatsNewCategory">
		<bbbc:config key="whatsNewCategory_bedbathcanada" configName="ContentCatalogKeys" />
	</c:set>
	</c:when>
	<c:when test="${appid == TBS_BedBathUSSite}">
	<c:set var="clearanceCategory">
		<bbbc:config key="BedBathUS_clearanceCategories" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="whatsNewCategory">
		<bbbc:config key="whatsNewCategory_bedbathus" configName="ContentCatalogKeys" />
	</c:set>
	</c:when>
	<c:when test="${appid == TBS_BuyBuyBabySite}">
	<c:set var="clearanceCategory">
		<bbbc:config key="BuyBuyBaby_clearanceCategories" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="whatsNewCategory">
		<bbbc:config key="whatsNewCategory_buybuybaby" configName="ContentCatalogKeys" />
	</c:set>
	</c:when>
	</c:choose>
		<c:set var="parentProductId">
				<c:out value="${parentProductId}" />
			</c:set>
		<dsp:getvalueof var="personalizationMessage" bean="GiftlistFormHandler.personalizationMessage"/>
		<c:if test="${not empty personalizationMessage}">
	    	<input type="hidden" id="personalizationMessage" name="personalizationMessage" value="${personalizationMessage}">
    	</c:if>
		<c:set var="CertonaContext" scope="request">${parentProductId};</c:set>
		<c:set var="omniPersonalizeButtonClick">
			<bbbl:label key='omni_personalize_button_click' language="${pageContext.request.locale.language}" />
	    </c:set>
		<dsp:getvalueof var="colortemp" param="color" />
		<c:set var="color" value="${fn:replace(colortemp,'%20',' ')}" />

		<dsp:droplet name="CanonicalItemLink">
			<dsp:param name="id" param="productId" />
			<dsp:param name="itemDescriptorName" value="product" />
			<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="pageURL" vartype="java.lang.String" param="url" />
			</dsp:oparam>
		</dsp:droplet>
		<c:if test="${everLivingOn}">
		<dsp:droplet name="EverLivingPDPDroplet">
			<dsp:param name="id" param="productId" />
				<dsp:param name="siteId" value="${appid}" />
				<dsp:oparam name="output">
				<dsp:getvalueof var="everLivingProduct" param="everLivingProduct" />
				<dsp:getvalueof var="collection" param="collection" />
				</dsp:oparam>
				</dsp:droplet>
		</c:if>
		<c:choose>
		<c:when test="${everLivingOn && everLivingProduct}">
		<dsp:include page="everLivingPDP.jsp">
		<dsp:param name="id" param="productId" />
		<dsp:param name="siteId" value="${appid}" />
		<dsp:param name="skuId" param="skuId" />
		<dsp:param name="color" value="${color}" />
		</dsp:include>
		</c:when>
		<c:otherwise>
           <dsp:droplet name="GiftRegistryFlyoutDroplet">
			<dsp:param name="profile" bean="Profile"/>
						<dsp:oparam name="output">
					<%-- registrySummaryVO ::	 <dsp:valueof param="registrySummaryVO"/>
					registrySummaryVO ::	<dsp:valueof param="registrySummaryVO.registryId"/> --%>
					<dsp:getvalueof var="registrySummaryVO" param="registrySummaryVO"/>
					<dsp:getvalueof var="registryId" param="registrySummaryVO.registryId"/>

				</dsp:oparam>
			</dsp:droplet>
		<dsp:droplet name="ProductDetailDroplet">
			<dsp:param name="id" param="productId" />
				<dsp:param name="siteId" value="${appid}" />
				<dsp:param name="skuId" param="skuId" />
				<dsp:param name="color" value="${color}" />
				<dsp:param name="isDefaultSku" value="true"/>
				<dsp:param name="registryId" param="registryId" />
				<dsp:param name="isMainProduct" value="true"/>
				<dsp:param name="startIndex" value="0" />

				<dsp:param name="isTBSRequest" value="${true}"/>
				<dsp:param name="colorInQueryParamsTBS" value="true"/>
					<dsp:oparam name="output">
						<dsp:getvalueof var="nextIndex" param="nextIndex" scope="request"/>
						<dsp:getvalueof var="collectionId_Omniture" param="collectionVO.omnitureCollectionEvar29"/>
						<dsp:getvalueof var="skuVO" param="pSKUDetailVO" />
						<dsp:getvalueof var="giftFlag" param="productVO.giftCertProduct" />
						<dsp:getvalueof var="collParentId" param="parentProductId" />
						<dsp:getvalueof var="productId" param="productVO.productId" />
						<dsp:getvalueof var="isCustomizationRequired" param="pSKUDetailVO.customizableRequired"/>
						<dsp:getvalueof var="personalizationType" param="pSKUDetailVO.personalizationType"/>
						<dsp:getvalueof var="offsetDateVDC" param="offsetDateVDC"/>
						<dsp:param name="productBrand" param="productVO.productBrand" />
						<c:set var="giftFlag" scope="request" value="${giftFlag}" />
						<c:choose>
						<c:when test="${not empty skuVO}">
							<dsp:getvalueof var="attribs" param="pSKUDetailVO.skuAttributes" />
						</c:when>
						<c:otherwise>
							<dsp:getvalueof var="attribs" param="productVO.attributesList" />
						</c:otherwise>
						</c:choose>

						<div id="content" role="main">
							<div class="row show-for-large-up">
								<div class="left small-11 columns">
								<ul class="tbs-crumbs">
									<dsp:include page="breadcrumb.jsp">
									<dsp:param name="siteId" value="${appid}" />
									<dsp:param name="productId" value="${productId}" />
									</dsp:include>
								</ul>
								</div>
								<div class="row">
									<div class="right print-email">
										<a href="#" class="pdp-sprite email" title="<bbbl:label key="lbl_cartdetail_emailcart" language="${language}"/>"><span></span></a>|<a href="#" class="pdp-sprite print print-trigger" title="<bbbl:label key="lbl_cartdetail_print" language="${language}"/>"><span></span></a>
									</div>
								</div>
								<div id="emailModal" class="reveal-modal medium" data-reveal>
										<a class="close-reveal-modal">&#215;</a>
									</div>
							</div>
							<dsp:getvalueof var="isLeadSKU" param="collectionVO.leadSKU" />
							<dsp:getvalueof var="collection" param="productVO.collection" />
							<dsp:getvalueof var="linkStringNonRecproduct" param="linkStringNonRecproduct" />
							<dsp:getvalueof var="rkgProductList" param="rkgCollectionProdIds" />
							<dsp:getvalueof id="showFlag" idtype="java.lang.String" param="showFlag" />
							<dsp:getvalueof var="prodGuideId" param="productVO.shopGuideId" />
							<dsp:getvalueof var="vendorName" param="productVO.vendorInfoVO.vendorName"/>
							<dsp:include src="../_includes/modules/personalizationVar.jsp" >
								<dsp:param name="skuVO" value="${skuVO}"/>
							</dsp:include>


							<c:choose>
								<c:when test="${requestScope.personalizedSku ne null }">
									<c:set var="itemAlreadyPersonalized" value="true" />
								</c:when>
								<c:otherwise>
									<c:set var="itemAlreadyPersonalized" value="false" />
								</c:otherwise>
							</c:choose>
							<input type="hidden" value="${itemAlreadyPersonalized }" id="itemAlreadyPersonalized" name="itemAlreadyPersonalized"/>

							<c:choose>
								<c:when test="${skuVO.customizableRequired eq true and itemAlreadyPersonalized eq true}">
									<c:set var="disableSFL" value="false" />
								</c:when>
								<c:when test="${skuVO.customizationOffered eq true and itemAlreadyPersonalized eq true and personalizedSku.personalizationComplete eq false}">
									<c:set var="disableSFL" value="false" />
								</c:when>
								<c:when test="${skuVO.customizableRequired eq true and itemAlreadyPersonalized eq false}">
									<c:set var="disableSFL" value="true" />
								</c:when>
								<c:when test="${skuVO.customizationOffered eq true and itemAlreadyPersonalized eq false }">
									<c:set var="disableSFL" value="false" />
								</c:when>
							</c:choose>

							<c:if test="${isInternationalCustomer eq true and itemAlreadyPersonalized eq true }">
								<c:set var="disableSFL" value="true" />
								<c:set var="disableEditRemove" value="true" />
							</c:if>

							<%-- <c:set var ="removeVar" value="${param.removePersonalization}"/> --%>


							<div itemscope itemtype="http://schema.org/Product">
							<div class="row <c:if test="${showFlag == '1'}">hasProdNextCollection</c:if>">

							<div class="small-12 medium-6 columns zoomProduct">
								<c:if test="${showFlag == '1'}">
									<dsp:include page="/bbcollege/next_college_collection.jsp" />
								</c:if>

								<dsp:getvalueof var="largeImage" param="productVO.productImages.largeImage" />
								<dsp:getvalueof var="smallImage" param="productVO.productImages.smallImage" />
								<dsp:getvalueof var="thumbnailImage" param="productVO.productImages.thumbnailImage" />
								<dsp:getvalueof var="basicImage" param="productVO.productImages.basicImage" />
								<dsp:getvalueof var="productName" param="productVO.name" scope="request" />
								<dsp:getvalueof var="refinedNameProduct" param="productVO.refinedName" scope="request" />
								<dsp:getvalueof var="zoomFlag" param="productVO.zoomAvailable" />

								<c:set var="scene7ImgURL" value="${scene7Path}" />
								<c:if test="${(not empty largeImage)}">
									<c:choose>
										<c:when test="${productId % 2 == 0}">
											<c:set var="scene7ImgURL" value="${fn:replace(scene7DomainPath, 'x', '1')}" />
										</c:when>
										<c:when test="${productId % 2 == 1}">
											<c:set var="scene7ImgURL" value="${fn:replace(scene7DomainPath, 'x', '2')}" />
										</c:when>
									</c:choose>
								</c:if>

								<c:set var="largeImgPath" value="${scene7ImgURL}/${largeImage}" />

								<div id="s7ProductImageWrapper" <c:if test="${zoomFlag && SceneSevenOn && (not empty largeImage)}"> class="bbbS7ImageWrapper loadingGIF" data-s7ImgsetID="${largeImage}" data-s7ImageId="${basicImage}" </c:if> data-parentProdId="${parentProductId}">
									<div class="bbbS7Main">
										<div id="s7ProductImageWrapperInner" class="easyzoom easyzoom--adjacent">
											<%-- http://placehold.it/300x300/E5E5E5/A5A5A5&text=Product%20Image%20Default, (default)],
											[http://placehold.it/300x300/E5E5E5/A5A5A5&text=Product%20Image%20Small, (small)],
											[http://placehold.it/364x364/E5E5E5/A5A5A5&text=Product%20Image%20Medium, (medium)],
											[http://placehold.it/478x478/E5E5E5/A5A5A5&text=Product%20Image%20Large, (large)]" --%>
											<c:choose>
												<c:when test="${empty largeImage || 'null' == largeImage || !SceneSevenOn}">
													<img itemprop="image" id="mainProductImg" src="${imagePath}/_assets/global/images/no_image_available.jpg" class="mainProductImage noImageFound" height="380" width="380" alt="${productName}" 
															onClick="_gaq.push(['_trackEvent', 'zoom', 'hero image', '${refinedNameProduct}']);" />
												</c:when>
												<c:when test="${!zoomFlag && SceneSevenOn && (not empty largeImage)}">
													<img itemprop="image" id="mainProductImg" src="${scene7ImgURL}/${largeImage}" class="mainProductImage noImageFound" height="380" width="380" alt="${productName}" 
															onClick="_gaq.push(['_trackEvent', 'zoom', 'hero image', '${refinedNameProduct}']);"/>
												</c:when>
												<c:otherwise>
													<a id="mainProductImgZoom" class="mainProductImgZoom" data-zoomhref="${scene7ImgURL}/${basicImage}?hei=2000&wid=2000&qlt=50,1" href="javascript:void(0)">
														<img itemprop="image" id="mainProductImg" data-interchange="[${largeImgPath}, (default)], [${fn:replace(largeImgPath,'$478$','$395$')}, (small)], [${largeImgPath}, (large)]" 
															class="mainProductImage" alt="${productName}" onClick="_gaq.push(['_trackEvent', 'zoom', 'hero image', '${refinedNameProduct}']);" />
													</a>
												</c:otherwise>
											</c:choose>
										</div>
										<c:if test="${zoomFlag && SceneSevenOn && (not empty largeImage)}">
										<div class="bbbS7ZoomTipText">
											<span class="icon-fallback-text">
												<span class="icon-zoomin" aria-hidden="true"></span>
												<span class="icon-text">Zoom</span>
											</span>
											<span class="show-for-small-only">Click to view large image</span>
											<span class="show-for-medium-only"><bbbl:label key="lbl_js_s7_tipmessage_touch" language="${language}"/></span>
											<span class="show-for-large-up"><bbbl:label key="lbl_js_s7_tipmessage_desktop" language="${language}"/></span>
										</div>
									     </c:if> 
									</div>
								</div>
							</div>

				<dsp:setvalue bean="Profile.categoryId" value="${rootCategory}" />

				<div class="small-12 medium-6 columns product-info productDetails">
					<div class="row registryDataItemsWrap">
					<dsp:form name="prodForm" method="post" id="prodForm">
					<div class="listDataItemsWrap<c:if test="${ (skuVO.customizableRequired || skuVO.customizationOffered) && not empty skuVO.customizableCodes}"> personalizedItem</c:if>"
					data-refNum="<c:if test='${itemAlreadyPersonalized }'>${personalizedSku.refnum }</c:if>">
						<dsp:droplet name="BreadcrumbDroplet">
							<dsp:oparam name="output">
							<dsp:getvalueof var="output_circular" param="output_circular" />
							<dsp:getvalueof var="bts" param="bts" />
							<c:set var="btsValue" value="${bts}" scope="request" />
							<c:if test="${null ne  output_circular}">
							<c:set var="backToCircular">
							<bbbl:label key='lbl_pdp_breadcrumb_circular' language="${pageContext.request.locale.language}" />
							</c:set>
							<dsp:a iclass="backToCircular" href="/tbs/browse/frag/circular.jsp?${output_circular}" title="${backToCircular}">
							${backToCircular}
							</dsp:a>
							</c:if>
							</dsp:oparam>
						</dsp:droplet>

						<div class="prodAttribs_ltl prodAttribWrapper">
						<dsp:getvalueof var="isLTLProduct" param="productVO.ltlProduct" />
							<c:choose>
							<c:when test="${not empty isLTLProduct && isLTLProduct}">
								<p class="overrides">
									<img class="marBottom_5" width="20" height="15" src="${TruckSrc}" alt="Truck">
									<bbbl:label key='ltl_delivery_surcharge_may_apply' language="${pageContext.request.locale.language}" />
								</p>
							</c:when>
							</c:choose>
						</div>

						<input type="hidden" class="addToCartSubmitData" value="${btsValue}" name="bts" />
						<div class="prodAttribs prodAttribWrapper small-12 columns">
						<dsp:droplet name="ForEach">
						<dsp:param name="array" value="${attribs}" />
							<dsp:oparam name="output">
							<dsp:getvalueof var="placeHolderTop" param="key" />
								<c:if test="${(not empty placeHolderTop) && (placeHolderTop eq AttributePDPTOP)}">
									<dsp:droplet name="ForEach">
										<dsp:param param="element" name="array" />
										<dsp:param name="sortProperties" value="+priority" />
											<dsp:oparam name="output">
											<dsp:getvalueof var="placeHolderTop" param="element.placeHolder" />
											<dsp:getvalueof var="attributeDescripTop" param="element.attributeDescrip" />
											<dsp:getvalueof var="imageURLTop" param="element.imageURL" />
											<dsp:getvalueof var="actionURLTop" param="element.actionURL" />
											<div class="product-title">
												<c:choose>
													<c:when test="${null ne attributeDescripTop}">
													<h5 class="promotions-g">
														<c:choose>
															<c:when test="${null ne imageURLTop}">
																<a href="${actionURLTop}" class="newOrPopup"><img src="${imageURLTop}" alt="<dsp:valueof param="element.attributeDescrip" valueishtml="true"/>" /></a>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${null ne actionURLTop}">
																		<a href="${actionURLTop}" class="newOrPopup"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true" /></span></a>
																	</c:when>
																	<c:otherwise>
																		<dsp:valueof param="element.attributeDescrip" valueishtml="true" />
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
														</h5>
													</c:when>
													<c:otherwise>
														<c:if test="${null ne imageURLTop}">
															<c:choose>
																<c:when test="${null ne actionURLTop}">
																	<a href="${actionURLTop}" class="newOrPopup"><img src="${imageURLTop}" alt="" /></a>
																</c:when>
																<c:otherwise>
																	<img src="${imageURLTop}" alt="" />
																</c:otherwise>
															</c:choose>
														</c:if>
													</c:otherwise>
												</c:choose>
											</div>
											</dsp:oparam>
									</dsp:droplet>
								</c:if>
							</dsp:oparam>
							</dsp:droplet>
						</div>

						<c:choose>
							<c:when test="${empty thumbnailImage}">
								<img class="hidden productImage" height="83" width="83" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${productName}" />
							</c:when>
							<c:otherwise>
								<img class="hidden productImage noImageFound" height="83" width="83" src="${scene7Path}/${thumbnailImage}" alt="${productName}" />
							</c:otherwise>
						</c:choose>
						<dsp:getvalueof var="productName" param="productVO.name" />
						<div class="small-12 columns product-title">
							<!-- <p class="overrides">Free Shipping</p> -->
							<h1 id="productTitle" class="subheader productDetails" itemprop="name">
								<dsp:valueof param="productVO.name" valueishtml="true" />
							</h1>
						</div>

						<div class="small-12 columns collection-link">
							<c:if test="${empty poc }">
								<c:set var="poc" value="${collParentId}" />
							</c:if>
							<c:if test="${not empty poc }">
								<dsp:droplet name="ProductDetailDroplet">
								<dsp:param name="id" value="${poc}" />
								<dsp:param name="siteId" param="siteId" />

								<dsp:oparam name="output">
									<dsp:getvalueof var="collectionProductName" param="productVO.name" scope="request" />
									<dsp:getvalueof var="collectionSmallImage" param="productVO.productImages.smallImage" scope="request" />
								</dsp:oparam>
								</dsp:droplet>
							</c:if>
							<div class="row" itemprop="description">
								<c:if test="${not empty poc }">
									<div class="partCollection small-2 columns cb">
										<dsp:droplet name="CanonicalItemLink">
										<dsp:param name="id" value="${poc}" />
										<dsp:param name="itemDescriptorName" value="product" />
										<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="collectionFinalUrl" vartype="java.lang.String" param="url" />
										</dsp:oparam>
										</dsp:droplet>
										<dsp:a page="${collectionFinalUrl}" title="${collectionProductName}">
											<c:choose>
												<c:when test="${empty collectionSmallImage}">
													<div class="partCollectionImg"> <img class="collectionSmallImage" height="63" width="63" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${collectionProductName}" /> </div>
												</c:when>
												<c:otherwise>
													<div class="partCollectionImg"> <img class="collectionSmallImage noImageFound" height="63" width="63" src="${scene7Path}/${collectionSmallImage}" alt="${collectionProductName}" /> </div>
												</c:otherwise>
											</c:choose>
										</dsp:a>
									</div>
									<div class="small-10 columns">
										<dsp:a page="${collectionFinalUrl}" title="${collectionProductName}">
											<bbbl:label key='lbl_pdp_product_part_of' language="${pageContext.request.locale.language}" />
											&nbsp;<c:out value="${collectionProductName}" escapeXml="false" />
											&nbsp;<bbbl:label key='lbl_pdp_product_part_of_collection' language="${pageContext.request.locale.language}" />
										</dsp:a>
									</div>
								</c:if>
							</div>
						</div>

						<c:set var="client_ID">
						<bbbc:config key="${appid}" configName="BBBLiveClicker" />
						</c:set>
						<c:set var="component_ID">
						<bbbc:config key="${client_ID}_WVLink" configName='BBBLiveClicker' />
						</c:set>
						<div class="small-12 columns product-other-links">
							<div class="pdpAttributeContainer fl clearfix">
								<dsp:getvalueof var="mediaVO" param="mediaVO" />
								<span>
									<c:choose>
									<c:when test="${null ne mediaVO}">
										<dsp:getvalueof var="mediaURL" param="mediaVO.mediaSource" />
										<c:if test="${null ne mediaURL}">
											<a class="pdp-sprite video" href="${mediaURL}" onClick="javascript:rkg_micropixel('${currentSiteId}','video'); openmedia(); pdpOmnitureProxy('${parentProductId}', 'videoplay'); return true;" target="module35892976">
											Watch Video</a>
										</c:if>
									</c:when>
									<c:otherwise>
										<c:if test="${LiveClickerOn}">
										<%-- <div id="liveClickerWatchVideo" class="pdp-sprite video" data-parentProdId="${parentProductId}"> --%>
										<script id="tag" type="text/javascript" src="<bbbc:config key="liveclicker_pdp" configName="ThirdPartyURLs" />client_id=${client_ID}&component_id=${component_ID}&dim6=${parentProductId}&widget_id="></script>
										<!-- </div> -->
										</c:if>
									</c:otherwise>
									</c:choose>
								</span>
								<c:if test="${not empty prodGuideId}">
									<dsp:droplet name="GuideItemLink">
									<dsp:param name="repositoryName" value="/com/bbb/cms/repository/GuidesTemplate" />
									<dsp:param name="itemDescriptor" value="guides" />
									<dsp:param name="id" param="productVO.shopGuideId" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
									</dsp:oparam>
									</dsp:droplet>
									<span id="viewProductGuides" class="pdp-sprite product-guide">
										<a href="${contextPath}${finalUrl}" title="View Buying Guide" class="productGuideImg">View Product Guide</a>
									</span>
								</c:if>
								<c:if test="${collection==true}">
									<c:if test="${isLeadSKU==true}">
										<span class="productLinks">
											<a class="lnkCollectionItems smoothScrollTo viewAccessories" data-smoothscroll-topoffset="65" href="#collectionItems"><bbbl:label key='lbl_pdp_view_accessories' language="${pageContext.request.locale.language}" /></a>
										</span>
									</c:if>
								</c:if>
							</div>
							<%-- Rebate Container --%>
							<dsp:getvalueof var="hasRebate" param="pSKUDetailVO.hasRebate" />
							<c:set var="rebatesOn" value="${false}" />
							<c:if test="${not empty hasRebate && hasRebate}">
								<dsp:getvalueof var="chkEligibleRebates" param="pSKUDetailVO.eligibleRebates" />
								<c:if test="${(null != chkEligibleRebates) && (fn:length(chkEligibleRebates) == 1 )}">
									<c:set var="rebatesOn" value="${true}" />
								</c:if>
							</c:if>
							<dsp:droplet name="ForEach">
							<dsp:param value="${attribs}" name="array" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="placeHolderPrice" param="key" />
								<c:if test="${(not empty placeHolderPrice) && (placeHolderPrice eq AttributePDPPrice)}">
									<dsp:droplet name="ForEach">
									<dsp:param param="element" name="array" />
									<dsp:param name="sortProperties" value="+priority" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="chkCount" param="count" />
										<dsp:getvalueof var="chkSize" param="size" />
										<c:set var="sep" value="seperator" />
										<c:if test="${chkCount == chkSize}">
											<c:choose>
												<c:when test="${rebatesOn}">
													<c:set var="sep" value="seperator" />
												</c:when>
												<c:otherwise>
													<c:set var="sep" value="" />
												</c:otherwise>
											</c:choose>
										</c:if>
										<dsp:getvalueof var="placeHolderPrice" param="element.placeHolder" />
										<dsp:getvalueof var="attributeDescripPrice" param="element.attributeDescrip" />
										<dsp:getvalueof var="imageURLPrice" param="element.imageURL" />
										<dsp:getvalueof var="actionURLPrice" param="element.actionURL" />
										 <c:set var="actionURLPrice">${actionURLPrice}</c:set>
										 <dsp:getvalueof var="attributeName" param="element.attributeName"/>
										 <c:set var="vdcAttributesList">
										 <bbbc:config key="vdcAttributesList" configName="ContentCatalogKeys" />
										 </c:set>
										 <c:set var ="isVdcSku" value="${skuVO.vdcSku}"/>


										<c:choose>
											<c:when test="${null ne attributeDescripPrice}">
												<c:choose>
													<c:when test="${null ne imageURLPrice}">
														<span class="rebateAttribs attribs ${sep}"><a href="${actionURLPrice}" class="pdp-sprite gift-pack"><img src="${imageURLPrice}" alt="<dsp:valueof param="element.attributeDescrip" valueishtml="true"/>" /></a></span>
													</c:when>
													<c:otherwise>
														<c:choose>
															<c:when test="${null ne actionURLPrice}">
															<c:choose>
																<c:when test="${not empty skuVO.skuId && isVdcSku && null ne attributeName && fn:contains(attributeName,vdcAttributesList)}">
																	<span class="rebateAttribs attribs ${sep}"><a href="/tbs/browse/dynamicStaticContent.jsp?pageName=ItemShippedDirectlyFromVendor&isVdcSku=${isVdcSku}&skuId=${skuVO.skuId}" class="pdp-sprite gift-pack popupShipping" data-reveal-id="infoModal" data-reveal-ajax="true"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a></span>
																</c:when>
																<c:otherwise>
																<span class="rebateAttribs attribs ${sep}"><a href="${actionURLPrice}" class="pdp-sprite gift-pack popupShipping" data-reveal-id="infoModal" data-reveal-ajax="true"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a></span>
																</c:otherwise>
															</c:choose>
															</c:when>
															<c:otherwise>
																<span class="rebateAttribs attribs ${sep}"><dsp:valueof param="element.attributeDescrip" valueishtml="true" /></span>
															</c:otherwise>
														</c:choose>
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:otherwise>
												<c:if test="${null ne imageURLPrice}">
													<c:choose>
														<c:when test="${null ne actionURLPrice}">
															<span class="rebateAttribs attribs ${sep}"><a href="${actionURLPrice}" class="pdp-sprite gift-pack"><img src="${imageURLPrice}" alt="" /></a></span>
														</c:when>
														<c:otherwise>
															<span class="rebateAttribs attribs ${sep}"><img src="${imageURLPrice}" alt="" /></span>
														</c:otherwise>
													</c:choose>
												</c:if>
											</c:otherwise>
										</c:choose>
									</dsp:oparam>
									</dsp:droplet>
								</c:if>
							</dsp:oparam>
							</dsp:droplet>

							<c:if test="${not empty hasRebate && hasRebate}">
								<dsp:getvalueof var="eligibleRebates" param="pSKUDetailVO.eligibleRebates" />
								<dsp:droplet name="ForEach">
								<dsp:param name="array" value="${eligibleRebates}" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="chkCount1" param="count" />
									<dsp:getvalueof var="chkSize1" param="size" />
									<c:set var="sep1" value="seperator" />
									<c:if test="${chkCount1 == chkSize1}">
										<c:set var="sep1" value="" />
									</c:if>
									<dsp:getvalueof var="rebate" param="element" />
									<span class="rebateAttribs attribs ${sep1}"><a href="${rebate.rebateURL}" class="links" target="_blank" title="Rebate"><c:out value="${rebate.rebateDescription}" escapeXml="false" /></a></span>
								</dsp:oparam>
								</dsp:droplet>
							</c:if>


							<dsp:droplet name="DisplayShippingRestrictionsDroplet">
							          		  		<dsp:param name="skuId" param="pSKUDetailVO.skuId" />
							          		  		<dsp:oparam name="output">
							          		  			<dsp:getvalueof var="skuShipRestrictionsVO" param="skuShipRestrictionsVO"/>
														<dsp:getvalueof var="nonShippableStates" param="skuShipRestrictionsVO.nonShippableStates"/>
														<span class="attribs rebateAttribs seperator <c:if test="${empty nonShippableStates}">hidden</c:if>">
														<a data-reveal-id="openShipRestModal" class="shippingRestrictionsApplied pdp-sprite gift-pack" href="#" data-skuId="${childSKUId}">
														<span><bbbl:label key="lbl_shipping_restrictions_applied" language="<c:out param='${language}'/>"/></span>
														</a>
														</span>

														<div id="openShipRestModal" class="reveal-modal small" data-reveal>
															<h2><bbbl:label key="lbl_shipping_restrictions_applied" language="<c:out param='${language}'/>" /></h2>
															<div class="bulletsReset">
															<%-- <bbbl:label key="lbl_cartdetail_cantshipto" language="<c:out param='${language}'/>"/> ${skuDetailVO.commaSepNonShipableStates}</span> --%>

																	<c:if test="${not empty nonShippableStates}">
																		<div class="shipRestrictText"><bbbl:label key="lbl_cartdetail_cantshipto" language="<c:out param='${language}'/>"/> ${nonShippableStates}</div>
																		<c:if test="${not empty skuShipRestrictionsVO.zipCodesRestrictedForSkuMap}">
																		<h4><bbbl:label key="lbl_other_restrictions" language="<c:out param='${language}'/>"/></h4>
																		</c:if>
																	</c:if>

															<input type="hidden" class="openLink" value="#product_${parentProductId}">
															</div>
															<a class="close-reveal-modal">&#215;</a>
														</div>
							          		  		</dsp:oparam>
							   </dsp:droplet>




						</div>

						<div class="small-12 columns product-reviews">
                          	<c:if test="${BazaarVoiceOn}">
                            	<div id="BVRRSummaryContainer">
									<img width="20" height="20" alt="" src="/_assets/global/images/widgets/small_loader.gif" >
                                               </div>
                                 </c:if>
							</div>
						
							<dsp:getvalueof var="colorParam" param="color" />
							<dsp:getvalueof var="sizeParam" param="size" />
							<dsp:droplet name="TBSProductColorsDetailDroplet">
								<dsp:param name="productVO" param="productVO" />
								<dsp:param name="isCollection" param="productVO.collection" />
								<dsp:param name="siteId" bean="Site.id" />
								<dsp:oparam name="output">
									<div class="small-12 columns color-selector swatchPickers">
									<div class="colorPicker">
									<div class="swatches">
		
										<ul>
										<dsp:droplet name="ForEach">
											<dsp:param name="array" param="colorSkuSet" />
											<dsp:oparam name="output">
								
												<dsp:getvalueof var="swatchUrl" param="element.skuSwatchImageURL" />
												<dsp:getvalueof var="colorName" param="element.color" />
												<dsp:getvalueof var="colorImagePath" param="element.skuSwatchImageURL" />
												<dsp:getvalueof var="attribute" param="element.color" />
												<dsp:getvalueof var="largeImagePath" param="element.largeImage" />
												<dsp:getvalueof var="thumbnailImagePath" param="element.thumbnailImage" />
												<dsp:getvalueof var="countVar" param="count" />
						    
												<c:if test="${swatchUrl != null}">

														<%-- <img src="${scene7Path}/${swatchUrl}" height="10" width="10" alt="${colorName}"/> --%>
														<c:choose>
															<c:when test="${(colorParam != null) && (colorParam eq attribute)}">
															<li class="selected"><a href="#">
																<a href="#" class="fl selected" data="${fn:toLowerCase(attribute)}" title="${attribute}" data-s7ImgsetID="${largeImagePath}" data-imgURLThumb="${scene7Path}/${thumbnailImagePath}">
																<span>
																	<c:choose>
																		<c:when test="${(colorImagePath != null) && (colorImagePath ne '')}">
																			<img src="${scene7Path}/${colorImagePath}" height="10" width="10" alt="${attribute}" class="noImageFound" />
																		</c:when>
																		<c:otherwise>
																			<img src="${imagePath}/_assets/global/images/blank.gif" height="10" width="10" alt="${attribute}" />
																		</c:otherwise>
																		</c:choose>
																</span>
																											</a>
															</c:when>
															<c:otherwise>
															<li><a href="#">
																<a href="#" class="fl" data="${fn:toLowerCase(attribute)}" title="${attribute}" data-s7ImgsetID="${largeImagePath}" data-imgURLThumb="${scene7Path}/${thumbnailImagePath}">
																<span>
																	<c:choose>
																		<c:when test="${(colorImagePath != null) && (colorImagePath ne '')}">
																			<img src="${scene7Path}/${colorImagePath}" height="10" width="10" alt="${attribute}" class="noImageFound" />
																		</c:when>
																		<c:otherwise>
																			<img src="${imagePath}/_assets/global/images/blank.gif" height="10" width="10" alt="${attribute}" />
																		</c:otherwise>
																	</c:choose>
																</span>
																											</a>
															</c:otherwise>
														</c:choose>
														<%-- <c:choose>
															<c:when test="${(colorParam != null)}">
																<input type="hidden" name="prodColor" class="_prodColor addItemToRegis addItemToList" value="${colorParam}" />
															</c:when>
															<c:otherwise>
																<input type="hidden" name="prodColor" class="_prodColor addItemToRegis addItemToList" value="" />
															</c:otherwise>
														</c:choose> --%>
													</a></li>
												</c:if>
											</dsp:oparam>
										</dsp:droplet>
										</ul>
										</div>
										</div>
									</div>
								</dsp:oparam>
							</dsp:droplet>

						<%--
						<div class="desclight">
						</div>
						--%>

						<%-- Part Collection --%>
						 <div id="prodRatings" class="hidden">
							<c:if test="${askAndAnswerEnabled}">
								<div id="BVQASummaryContainer"></div>
							</c:if>
						</div>

						<%-- <div class="ratings clearfix">
								<div id="prodRatings">
									<input type="hidden" name="prodId" value="${parentProductId}" />
									<input type="hidden" name="userToken" value="${userTokenBVRR }" />
									<div class="clear"></div>
								<c:if test="${BazaarVoiceOn}">
									<div id="BVRRSummaryContainer">
										<img width="20" height="20" alt="" src="/_assets/global/images/widgets/small_loader.gif" >
									</div>
									<c:if test="${askAndAnswerEnabled}">
										<div id="BVQASummaryContainer"></div>
									</c:if>
								</c:if>
								</div>

							<div class="fbLikePrintMessage share marTop_10">
								<c:if test="${FBOn}">
								<!--[if IE 7]>
									<dsp:droplet name="/com/bbb/account/droplet/BBBURLEncodingDroplet">
										<dsp:param name="URL" value="${pageContext.request.requestURL}?${pageContext.request.queryString}"/>
										<dsp:oparam name="output">
											<dsp:getvalueof id="encodedURL" param="encodedURL"/>
										</dsp:oparam>
									</dsp:droplet>
									<dsp:droplet name="/com/bbb/account/droplet/BBBConfigKeysDroplet">
										<dsp:param name="configKey" value="FBAppIdKeys"/>
										<dsp:oparam name="output">
											<dsp:getvalueof var="fbAppIDConfigMap" param="configMap"/>
										</dsp:oparam>
									</dsp:droplet>
									<div class="fb-like">
										<iframe type="some_value_to_prevent_js_error_on_ie7" title="Facebook Like" src="<bbbc:config key='fb_like_plugin_url' configName='ThirdPartyURLs' />?href=${encodedURL}&amp;send=false&amp;layout=button_count&amp;width=90&amp;show_faces=false&amp;action=like&amp;colorscheme=light&amp;font&amp;height=21&amp;appId=${fbConfigMap[currentSiteId]}" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:90px; height:21px;" allowTransparency="true"></iframe>
									</div>
								<![endif]-->
								<!--[if !IE 7]><!-->
									<div class="fb-like" data-layout="button_count" data-send="false" data-width="90" data-show-faces="false"></div>
								<!--<![endif]-->
								</c:if>
								<a href="/tbs/browse/print/product_details.jsp?productId=${parentProductId}" class="print" title="Print"></a>
								<a href="#" class="email" title="Email"></a>
							</div>
							<c:set var="attributeCount" value="1" />

						<div class="rebates">

						</div>
						</div> --%>


				<dsp:include src="subprod_detail.jsp" flush="true">
					<dsp:param name="productVO" param="productVO" />
					<dsp:param name="colorParam" param="color" />
					<dsp:param name="sizeParam" param="size" />
				</dsp:include>

				<c:set var="showMoreAttibsDiv" value="${true}" />
			<dsp:droplet name="ForEach">
				<dsp:param value="${attribs}" name="array" />
				<dsp:oparam name="output">
				<dsp:getvalueof var="placeHolderMiddle" param="key" />
					<c:if test="${(not empty placeHolderMiddle) && (placeHolderMiddle eq AttributePDPMiddle)}">
						<dsp:droplet name="ForEach">
							<dsp:param param="element" name="array" />
							<dsp:param name="sortProperties" value="+priority" />
								<dsp:oparam name="output">
								<dsp:getvalueof var="placeHolderMiddle" param="element.placeHolder" />
								<dsp:getvalueof var="attributeDescripMiddle" param="element.attributeDescrip" />
								<dsp:getvalueof var="imageURLMiddle" param="element.imageURL" />
								<dsp:getvalueof var="actionURLMiddle" param="element.actionURL" />

								<c:if test="${showMoreAttibsDiv == true}">
									<div class="productLinks moreProdAttribs PDPM">
									<c:set var="showMoreAttibsDiv" value="${false}" />


																			</c:if>
								<div>
									<c:choose>
										<c:when test="${null ne attributeDescripMiddle}">
											<c:choose>
												<c:when test="${null ne imageURLMiddle}">
													<a href="${actionURLMiddle}" class="newOrPopup"> <img src="${imageURLMiddle}" alt="<dsp:valueof param="element.attributeDescrip" valueishtml="true"/>" /></a>
												</c:when>
												<c:otherwise>
													<a href="${actionURLMiddle}" class="newOrPopup"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true" /></span></a>
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<c:if test="${null ne imageURLMiddle}">
												<c:choose>
													<c:when test="${null ne actionURLMiddle}">
														<a href="${actionURLMiddle}" class="newOrPopup"><img src="${imageURLMiddle}" alt="" /></a>
													</c:when>
													<c:otherwise>
														<img src="${imageURLMiddle}" alt="" />
													</c:otherwise>
												</c:choose>
											</c:if>
										</c:otherwise>
									</c:choose>
								</div>
							</dsp:oparam>
						</dsp:droplet>
					</c:if>
				</dsp:oparam>
				</dsp:droplet>
				<c:if test="${showMoreAttibsDiv == false}">


													</div>
				</c:if>

				<dsp:getvalueof var="skuinStock" param="inStock" />
				<dsp:getvalueof var="oosProdId" param="productId" />
				<dsp:getvalueof var="isMultiSku" param="isMultiSku" />
				<c:choose>
				<c:when test="${skuinStock eq null}">
					<c:set var="inStock" value="true" />
				</c:when>
				<c:otherwise>
					<dsp:getvalueof var="inStock" param="inStock" />
				</c:otherwise>
				</c:choose>

				<c:if test="${skuVO != null}">
					<dsp:getvalueof var="pDefaultChildSku" param="pSKUDetailVO.skuId" />
                    <dsp:getvalueof var="pDefaultChildUPC" param="pSKUDetailVO.upc" />
                    <dsp:getvalueof var="skuIncartFlag" param="pSKUDetailVO.inCartFlag" />
				</c:if>
				 <%--created to handle personalization with incart price --%>
				<input type="hidden" value="${skuIncartFlag}" id="inCartFlag"/>
			<dsp:getvalueof var="firstChildSKU" param="pFirstChildSKU" />
			<c:if test="${not empty firstChildSKU}">
				<div id="firstproduct" style="display: none">
				LT_F_PRD_ID:=${parentProductId}
				LT_F_SKU_ID:=${firstChildSKU}
				</div>
			</c:if>
			<c:set var="customizableCodes" value="${skuVO.customizableCodes}" scope="request"/>
			<c:choose>
				<c:when test="${not empty skuVO.customizableCodes && fn:contains(customizeCTACodes, skuVO.customizableCodes)}">
					<c:set var="personalizationTxt">
						<bbbl:label key="lbl_customize_this" language="${pageContext.request.locale.language}"/>
					</c:set>
					<input id="customizeCTAFlag" type="hidden" value="true"/>
				</c:when>
				<c:otherwise>
					<c:set var="personalizationTxt">
						<bbbl:label key="lbl_personalize_this" language="${pageContext.request.locale.language}"/>
					</c:set>
					<input id="customizeCTAFlag" type="hidden" value="false"/>
				</c:otherwise>
			</c:choose>
			<c:choose>
						<c:when test="${(skuVO.customizableRequired || skuVO.customizationOffered) && not empty skuVO.customizableCodes}">
						   <c:set var="omni_personalizationCheck" value="true" />
						    <div class="personalizationOffered small-12 columns personalizeDesktop">
								<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="#"</c:otherwise></c:choose> class="btnPersonalize personalize tiny button <c:if test="${not enableKatoriFlag}">disabled</c:if>" role="button" data-sku="${pDefaultChildSku}" data-custom-vendor="${vendorName}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="javascript:customLinks('Personalize This: button click')"</c:if>;>
									${personalizationTxt}
								</a>
								<c:choose>
									<c:when test="${enableKatoriFlag}">
										<span class="personalizationDetail">
											<bbbl:label key='lbl_cart_personalization_detail_tbs' language="${pageContext.request.locale.language}" />
										</span>
										<div id="openReturnPolicyModal" class="reveal-modal large" data-options="close_on_background_click:true;close_on_esc:true;" data-reveal=""></div>
						            </c:when>
									<c:otherwise>
										<p class="persUnavailableMsg"><bbbl:label key='lbl_pdp_personalization_unavailable' language="${pageContext.request.locale.language}" /></p>
									</c:otherwise>
								</c:choose>
							</div>
						</c:when>
						<c:otherwise>
									<div class="personalizationOffered small-12 columns personalizeDesktop hidden">

										<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="#"</c:otherwise></c:choose> class="btnPersonalize personalize tiny button <c:if test="${not enableKatoriFlag}">disabled</c:if>" role="button" data-sku="${pDefaultChildSku}" data-custom-vendor="${vendorName}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="javascript:customLinks('Personalize This: button click')"</c:if>;>
											${personalizationTxt}
										</a>
										<c:choose>
									<c:when test="${enableKatoriFlag}">
										<span class="personalizationDetail">
											<bbbl:label key='lbl_cart_personalization_detail_tbs' language="${pageContext.request.locale.language}" />
										</span>
										<div id="openReturnPolicyModal" class="reveal-modal large" data-options="close_on_background_click:true;close_on_esc:true;" data-reveal=""></div>
									</c:when>
									<c:otherwise>
										<p class="persUnavailableMsg"><bbbl:label key='lbl_pdp_personalization_unavailable' language="${pageContext.request.locale.language}" /></p>
									</c:otherwise>
								</c:choose>
									</div>
						</c:otherwise>
					</c:choose>

			<dsp:getvalueof var="showTab" param="tabLookUp" />
			<%-- <dsp:getvalueof var="inStock" param="inStock"/> --%>
			<dsp:getvalueof var="salePriceRangeDescription" param="productVO.salePriceRangeDescription" />
			<c:choose>
			<c:when test="${showTab}">

						<div class="small-12 large-7 columns">
						<div class="price prodPrice <c:if test="${personalizationType eq 'CR' && itemAlreadyPersonalized eq false}">priceTBD</c:if>" itemprop="price">
						<c:choose>
							<c:when test="${not empty pDefaultChildSku}">
							<c:choose>
							<c:when test="${itemAlreadyPersonalized and personalizedSku.personalizationComplete eq false && eximItemPrice <= 0.01 && (personalizationType eq 'CR') }">
							<bbbl:label key="lbl_dsk_pdp_price_is_tbd" language ="${pageContext.request.locale.language}"/>
							</c:when>

							<c:when test="${itemAlreadyPersonalized && (personalizedSku.personalizationComplete eq false)  && (personalizationType eq 'PY' || personalizationType eq 'CR')}">
							${shopperCurrency}${eximItemPrice }

							</c:when>

						<c:when test="${itemAlreadyPersonalized && (personalizedSku.personalizationComplete eq false)  && (personalizationType eq 'PB')}">
							${shopperCurrency}${eximItemPrice }

							</c:when>
							<c:when test="${itemAlreadyPersonalized}">
							${shopperCurrency}${eximItemPrice }
							</c:when>
							<c:otherwise>
								<dsp:include page="product_details_price.jsp">
									<dsp:param name="product" param="productId" />
									<dsp:param name="sku" value="${pDefaultChildSku}" />
									<dsp:param name="inCartFlag" value="${skuIncartFlag}"/>
								</dsp:include>
							</c:otherwise>
							</c:choose>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${not empty salePriceRangeDescription}">
										<h1 class="price price-sale">
											<dsp:valueof converter="currency" param="productVO.salePriceRangeDescription" />
										</h1>
										<dsp:getvalueof var="salePriceRange" param="productVO.salePriceRangeDescription" />
										<c:choose>
											<c:when test="${fn:contains(salePriceRange,'-')}">
																						<br />
											</c:when>
											<c:otherwise>&nbsp;</c:otherwise>
										</c:choose>
										<h3 class="price price-original">
											<dsp:valueof converter="currency" param="productVO.priceRangeDescription" />
										</h3>
										<dsp:getvalueof idtype="java.lang.String" id="salePrice" param="productVO.salePriceRangeDescription" />
									</c:when>
									<c:otherwise>
										<dsp:valueof converter="currency" param="productVO.priceRangeDescription" />
									</c:otherwise>
								</c:choose>
						</c:otherwise>
						</c:choose>
						</div>
						<span class="pricesbjctchnge <c:if test="${not (not empty pDefaultChildSku && itemAlreadyPersonalized && (personalizationComplete eq false)  && (personalizationType eq 'PY' || personalizationType eq 'CR'))}">hidden</c:if>"><bbbl:label key="lbl_price_change" language="${pageContext.request.locale.language}" /></span>
						</div>
					<div class="small-12 large-5 columns quantity">
						<h3>QTY</h3>
						<div class="qty-spinner">
							<a class="button minus secondary"><span></span></a>
							<input type="text" data-max-value="99" value="1" maxlength="2" name="qty" class="quantity-input qty addItemToRegis addItemToList itemQuantity" id="quantity">
							<a class="button plus secondary"><span></span></a>
						</div>
						<dsp:getvalueof var="prodId" param="productId" />
						<dsp:getvalueof var="regId" value="${registryId}" />
						<input type="hidden" name="registryId" class="sflRegistryId  addItemToRegis addItemToList" value="${registryId}" data-change-store-submit="registryId" />
						<input type="hidden" name="prodId" class="_prodId addItemToRegis productId addItemToList" value="${prodId}" data-change-store-submit="prodId" data-change-store-errors="required" data-change-store-internalData="true" />
						<input type="hidden" name="skuId" value="${pDefaultChildSku}" class="addItemToRegis _skuId addItemToList changeStoreSkuId" data-change-store-submit="skuId" data-change-store-errors="required" data-change-store-internalData="true" />
						<input type="hidden" name="price" value="${omniPrice}" class="addItemToList addItemToRegis" />
						<input type="hidden" class="addToCartSubmitData" name="storeId" value="" data-change-store-storeid="storeId" />
						<input type="hidden" class="addToCartSubmitData" name="bts" value="${bts}" data-change-store-storeid="bts" />
						<c:if test="${not empty registryId}">
							<input type="hidden" class="addToCartSubmitData" name="registryId" value="${registryId}" data-change-store-submit="registryId" />
						</c:if>
					</div>

					<%-- BBBH-844 Grey box description field from Katori on PDP with "Remove Personalization | JSP inclusion| changes start --%>
					<div class="clearfix"></div>

					<dsp:include src="../_includes/modules/personalizedPDPWrapper.jsp" >
							<dsp:param name="skuVO" value="${skuVO}"  />
							<dsp:param name="pDefaultChildSku" value="${pDefaultChildSku}"/>
						</dsp:include>

					<%-- BBBH-844 Grey box description field from Katori on PDP with "Remove Personalization | JSP inclusion| changes ends --%>
					<input type="hidden" name="isCustomizationRequired" value="${skuVO.customizableRequired}"/>
					<input type="hidden" name="personalizationType" value="${skuVO.personalizationType}"/>
					<input type="hidden" name="customizableCodes" value="${skuVO.customizableCodes}"/>
					<dsp:getvalueof param="pSKUDetailVO.vdcSku" var="vdcSku"/>
					<div class="small-12 columns product-other-links availability">
						<c:if test="${not skuVO.ltlItem && vdcSku ne true}">
							<dsp:include src="/tbs/browse/frag/Near_by_store_info.jsp" flush="true">
								<dsp:param name="skuId" param="pSKUDetailVO.skuId"/>
							</dsp:include>
						</c:if>
						<dsp:getvalueof param="pSKUDetailVO.skuId" var="skuId"/>
						<c:if test="${vdcSku eq true}">
							<dsp:droplet name="/com/bbb/commerce/browse/VDCShippingMessagingDroplet">
								<dsp:param name="skuId" param="pSKUDetailVO.skuId"/>
								<dsp:param name="fromShippingPage" value="true"/>
								<dsp:oparam name="output">
									<dsp:getvalueof var="vdcDelTime" param="vdcShipMsg"/>
								</dsp:oparam>
								<dsp:oparam name="error">
								</dsp:oparam>
							</dsp:droplet>
							<c:if test="${not empty vdcDelTime }">
								<a href="/tbs/browse/dynamicStaticContent.jsp?pageName=ItemShippedDirectlyFromVendor&isVdcSku=${vdcSku}&skuId=${skuId}" class="pdp-sprite online popupShipping <c:if test="${timeframe eq '0004'}"> no-stock</c:if>" <c:if test="${timeframe eq 0004}">disabled='disabled'</c:if>>
									<bbbl:label key="lbl_vdc_arrives_in" language="${pageContext.request.locale.language}" /> ${vdcDelTime}
								</a>
							</c:if>
							<c:set var="vdcOffsetFlag">
								<bbbc:config key="vdcOffsetFlag" configName="FlagDrivenFunctions" />
							</c:set>
							<c:if test="${vdcOffsetFlag && not empty offsetDateVDC}">
								<jsp:useBean id="placeHolderMapServiceLevel" class="java.util.HashMap" scope="request" />
								<c:set target="${placeHolderMapServiceLevel}" property="actualOffSetDate" value="${offsetDateVDC}" />
								<c:if test="${not empty skuVO && !skuVO.ltlItem}">
									<p class="vdcOffsetMsg highlightRed bold marTop_n7" >
										<bbbt:textArea key="txt_vdc_offset_msg" placeHolderMap="${placeHolderMapServiceLevel}" language="${pageContext.request.locale.language}" />
									</p>
								</c:if>
							</c:if>
						</c:if>
					</div>

					<dsp:getvalueof param="pSKUDetailVO.skuGiftCard" var="skuGiftCard"/>
					<c:if test="${skuGiftCard}">
						<c:choose>
							<c:when test="${empty skuinStock}">
								<c:set var="inStock" value="true" />
							</c:when>
							<c:otherwise>
								<dsp:getvalueof var="inStock" value="${skuinStock}" />
							</c:otherwise>
						</c:choose>
					</c:if>

					<dsp:getvalueof var="emailStockAlertsEnabled" param="pSKUDetailVO.emailStockAlertsEnabled" />
					<%--Verify why OutOfStockOnis always true
                    <c:if test="${OutOfStockOn && (inStock==false)}"> --%>
                    <div class="notifyme">
                    <c:if test="${(inStock==false)}">
					<%-- <c:if test="${(null eq emailStockAlertsEnabled) || (emailStockAlertsEnabled==true)}"> --%>
						<dsp:include page="notifyme.jsp">
							<dsp:param name="pSKUDetailVO" param="pSKUDetailVO" />
							<dsp:param name="skuId" value="${skuVO.skuId}" />
						</dsp:include>
					</c:if>
					</div>
					<%-- </c:if> --%>

					<div class="priceQuantity noPadTop <c:if test="${inStock==false}">priceQuantityNotAvailable</c:if>">

						<span class="prodAvailabilityStatus hidden">
							<c:choose>
							<c:when test="${inStock==false}">
								<link itemprop="availability" href="//schema.org/OutOfStock" />
															<bbbl:label key='lbl_pdp_out_of_stock' language="${pageContext.request.locale.language}" />
							</c:when>
							<c:otherwise>
								<link itemprop="availability" href="//schema.org/InStock" />
															<bbbl:label key='lbl_pdp_in_stock' language="${pageContext.request.locale.language}" />
							</c:otherwise>
							</c:choose>
						</span>
						</span>
						<dsp:getvalueof var="emailStockAlertsEnabled" param="pSKUDetailVO.emailStockAlertsEnabled" />

							<div class="small-12 columns message noMar cb marTop_20 marBottom_20 outofstock <c:if test="${inStock==true}">hidden</c:if>">
								<c:choose>
									<c:when test="${inStock==true}">
									<input type="hidden" value="" name="oosSKUId" class="_oosSKUId" />
									</c:when>
									<c:otherwise>
									<input type="hidden" value="${pDefaultChildSku}" name="oosSKUId" class="_oosSKUId" />
									</c:otherwise>
								</c:choose>
								<input type="hidden" value="${oosProdId}" name="oosProdId" />
								<div class="error">
														<bbbl:label key='lbl_pdp_product_notavailable_shipping' language="${pageContext.request.locale.language}" />
													</div>

								<%-- <c:if test="${OutOfStockOn}">
								<c:choose>
								<c:when test="${(null eq emailStockAlertsEnabled) || (emailStockAlertsEnabled==true)}">
								<div class="info"><a class="lnkNotifyOOS" href="#"><bbbl:label key='lbl_pdp_product_notify_item_available' language="${pageContext.request.locale.language}" /></a></div>
								</c:when>
								<c:otherwise>
								<div class="info hidden"><a class="lnkNotifyOOS" href="#"><bbbl:label key='lbl_pdp_product_notify_item_available' language="${pageContext.request.locale.language}" /></a></div>
								</c:otherwise>
								</c:choose>
								</c:if> --%>
								<c:if test="${MapQuestOn}">

								<dsp:getvalueof var="bopusAllowed" param="pSKUDetailVO.bopusAllowed" />
										<c:if test="${not bopusAllowed}">
											<%-- <div class="info"><a href="#" class="changeStore"><bbbl:label key='lbl_pdp_product_findstore_near' language="${pageContext.request.locale.language}" /></a></div> --%>
										</c:if>
								</c:if>
								</div>

					</div>
						<c:if test="${currentSiteId ne TBS_BedBathCanadaSite}">
							<c:choose>
								<c:when test="${not empty skuVO && skuVO.ltlItem}">
									<dsp:droplet name="GetApplicableLTLShippingMethodsDroplet">
										<dsp:param name="skuId" value="${skuVO.skuId}" />
										<dsp:param name="siteId" value="${appid}" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="shipMethodVOList" param="shipMethodVOList" />
										</dsp:oparam>
									</dsp:droplet>
									<input type="hidden" name="isLtlItem" value="${true}" />
									<%-- js is using this flag in some scenario --%>
									<input type="hidden" id="ltlFlag" name="ltlFlag" value="true" />
								</c:when>
								<c:otherwise>
										<input type="hidden" name="isLtlItem" value="${false}" />
								</c:otherwise>
							</c:choose>
							<div id="ltlDeliveryMethodWrapper" class="small-12 columns hidden">
								<div class="small-12 columns ltlDeliveryOptions">
									<img class="marBottom_5" width="20" height="15" src="${TruckSrc}" alt="Truck">
									<span><bbbl:label key='ltl_truck_delivery_options' language="${pageContext.request.locale.language}" />:
										<a href="${contextPath}/static/ltlDeliveryInfo" class="fl popupShipping" data-reveal-id="infoModal" data-reveal-ajax="true">
											<img class="quesMark" width="15" height="15" src="${QuestMarkSrc}" alt="Question Mark" />
										</a>
									</span>
								</div>

									<div class="small-12 large-6 columns no-padding">
										<a class="secondary radius button dropdown selServiceLevel large-12" data-dropdown="selServiceLevel_${productId}" href="#" ><bbbl:label key="lbl_pdp_sel_service" language="<c:out param='${language}'/>" /><span></span></a>
										<ul class="f-dropdown medium selServiceUl" data-dropdown-content data-ltlflag="true" aria-hidden="true" id="selServiceLevel_${productId}" name="selServiceLevel">
											<dsp:droplet name="ForEach">
												<dsp:param value="${shipMethodVOList}" name="array" />
												<dsp:oparam name="outputStart">
													<li class="selected"><a href="#"><bbbl:label key="lbl_pdp_sel_service" language="<c:out param='${language}'/>" /></a></li>
												</dsp:oparam>
												<dsp:oparam name="output">
													<dsp:getvalueof var="shipMethodId" param="element.shipMethodId" />
													<dsp:getvalueof var="shipMethodName" param="element.shipMethodDescription" />
													<dsp:getvalueof var="deliverySurchargeval" param="element.deliverySurcharge" />
													<c:if test="${deliverySurchargeval gt 0}">
														<c:set var="deliverySurcharge">
															<dsp:valueof converter="currency" param="element.deliverySurcharge" />
														</c:set>
													</c:if>
													<c:if test="${deliverySurchargeval eq 0}">
														<c:set var="deliverySurcharge">Free</c:set>
													</c:if>
													<li><a href="#" data-method_id="${shipMethodId}" data-method_name="${shipMethodName}" data-surcharge="${deliverySurcharge}">${shipMethodName} - ${deliverySurcharge}</a></li>
												</dsp:oparam>
												<dsp:oparam name="outputEnd">
												</dsp:oparam>
											</dsp:droplet>
										</ul>
									</div>
									<div class="small-12 medium-3 columns popupShipping no-padding" id="selServiceLevelinput">
										<a href="${contextPath}/static/ltlShippingInfo" class="fl marTop_10 marLeft_10 popupShipping" data-productid="${productId}" data-reveal-ajax="true" data-reveal-id="infoModal"><bbbl:label key='ltl_shipping_label' language="${pageContext.request.locale.language}" /></a>
										<input type="hidden" value="" class="_prodSize addItemToRegis addItemToList" id="serviceLevelFlag" name="selServiceLevel">
									</div>
									<div class="small-12 large-5 columns ltlReturnPolicyLink no-padding-left">
										<a href="${contextPath}/static/EasyReturns">Truck Delivery Return Policy</a>
									</div>

									<br/>
							</div>
						</c:if>

						 <c:choose>
						<c:when test="${(skuVO.customizableRequired || skuVO.customizationOffered) && not empty skuVO.customizableCodes}">
						 <c:set var="omni_personalizationCheck" value="true" />
						  <div class="personalizationOffered small-12 columns personalizeMobile">
								<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="#"</c:otherwise></c:choose> class="btnPersonalize personalize tiny button small-12 columns<c:if test="${not enableKatoriFlag}">disabled</c:if>" role="button" data-sku="${pDefaultChildSku}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="javascript:customLinks('Personalize This: button click')"</c:if>;>
									${personalizationTxt}
								</a>
								<c:choose>
									<c:when test="${enableKatoriFlag}">
										<span class="personalizationDetail">
											<bbbl:label key='lbl_cart_personalization_detail_tbs' language="${pageContext.request.locale.language}" />
										</span>
										<div id="openReturnPolicyModal" class="reveal-modal large" data-options="close_on_background_click:true;close_on_esc:true;" data-reveal=""></div>
									</c:when>
									<c:otherwise>
										<p class="persUnavailableMsg"><bbbl:label key='lbl_pdp_personalization_unavailable' language="${pageContext.request.locale.language}" /></p>
									</c:otherwise>
								</c:choose>
							</div>
						</c:when>
						<c:otherwise>
									<div class="personalizationOffered small-12 columns personalizeMobile hidden">

										<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="#"</c:otherwise></c:choose> class="btnPersonalizes personalize tiny button small-12 columns <c:if test="${not enableKatoriFlag}">disabled</c:if>" role="button" data-sku="${pDefaultChildSku}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="javascript:customLinks('Personalize This: button click')"</c:if>;>
											${personalizationTxt}
										</a>
										<c:choose>
									<c:when test="${enableKatoriFlag}">
										<span class="personalizationDetail">
											<bbbl:label key='lbl_cart_personalization_detail_tbs' language="${pageContext.request.locale.language}" />
										</span>
										<div id="openReturnPolicyModal" class="reveal-modal large" data-options="close_on_background_click:true;close_on_esc:true;" data-reveal=""></div>
									</c:when>
									<c:otherwise>
										<p class="persUnavailableMsg"><bbbl:label key='lbl_pdp_personalization_unavailable' language="${pageContext.request.locale.language}" /></p>
									</c:otherwise>
								</c:choose>
									</div>
							</c:otherwise>
						</c:choose>
						<dsp:getvalueof var="registryIdValue" param="registryId"/>
						<input type="hidden" name="personalizationType" value="${personalizationType}"/>

						<dsp:droplet name="TBSItemExclusionDroplet">
							<dsp:param name="siteId" value="${appid}"/>
							<dsp:param name="skuId" value="${skuVO.skuId}"/>
							<dsp:oparam name="output">
								<dsp:getvalueof var="isItemExcluded" param="validItemExcludedSku"/>
								<dsp:getvalueof var="caDisabled" param="caDisabled"/>
								<dsp:getvalueof var="reasonCode" param="reasonCode"/>
							</dsp:oparam>
						</dsp:droplet>
						<c:choose>
							<c:when test="${isItemExcluded  || caDisabled}">
								<div class="small-12 columns product-other-links availabilityMsgExclusion" style="color:red;font-weight:bold;">
							 		<dsp:include page="/browse/frag/reasonCodeMessage.jsp" flush="true" >
										<dsp:param name="reasonCode" value="${reasonCode}"/>
									</dsp:include>
							    </div>
								<div class="small-12 large-4 columns itemexclu" >
									<div class=" button_disabled">
										<input type="submit" name="btnAddToCart" value="Add to Cart" onclick="rkg_micropixel('${appid}','cart')" role="button" aria-pressed="false"
										disabled="disabled" <c:choose><c:when test="${inStock==true && !isItemExcluded && !caDisabled and !disableCTA}">class="tiny button expand transactional"</c:when><c:otherwise>class="tiny button expand disabled transactional" disabled="disabled"</c:otherwise></c:choose> />
									</div>
								</div>
							</c:when>
							<c:otherwise>
								<div class="small-12 large-4 columns addToCart" >
									<div class="button_disabled">
										<input type="submit" name="btnAddToCart" value="Add to Cart" onclick="rkg_micropixel('${appid}','cart')" role="button" aria-pressed="false"
										disabled="disabled"<c:choose><c:when test="${inStock==true and !disableCTA}">class="enableOnDOMReady tiny button expand transactional"</c:when><c:otherwise>class="tiny button expand disabled transactional"</c:otherwise></c:choose> />
									</div>
								</div>
							</c:otherwise>
						</c:choose>

						<div class="small-12 medium-6 large-4 columns large-no-padding-left addToList">
							<c:choose>
								<c:when test="${isItemExcluded || caDisabled}">
									<c:choose>
										<c:when test="">
										</c:when>
										<c:when test="">
										</c:when>
										<c:otherwise>

										</c:otherwise>
									</c:choose>
									<input type="button"  value="Save for Later" disabled="disabled" role="button" aria-pressed="false" class="tiny button expand secondary" />
								</c:when>
							   	<c:when test="${(currentSiteId ne BedBathCanadaSite) && not empty skuVO && skuVO.ltlItem && not empty registryIdValue || disableSFL}">
									<input type="button" name="btnAddToList" id="btnAddToList" value="Save for Later" disabled="disabled" role="button" aria-pressed="false" <c:choose><c:when test="${inStock==true}">class="tiny button expand secondary"</c:when><c:otherwise>class="tiny button expand disabled secondary"</c:otherwise></c:choose> onclick="rkg_micropixel('${appid}','wish')"/>
								</c:when>
								<c:otherwise>
									<input type="button" name="btnAddToList" id="btnAddToList" value="Save for Later" role="button" aria-pressed="false" <c:choose><c:when test="${inStock==true}">class="tiny button expand secondary"</c:when><c:otherwise>class="tiny button expand secondary"</c:otherwise></c:choose> onclick="rkg_micropixel('${appid}','wish')"/>
								</c:otherwise>
							</c:choose>
						</div>

							<%-- <c:if test="${MapQuestOn}">
								<div class="small-12 medium-6 large-4 columns large-no-padding-left">
									<dsp:getvalueof var="bopusAllowed" param="pSKUDetailVO.bopusAllowed"/>
									<c:choose>
										<c:when test="${bopusAllowed}">
											<input type="button" name="btnFindInStore" value="Find in Store" disabled="disabled" class="tiny button expand secondary" role="button" aria-pressed="false" />
										</c:when>
										<c:otherwise>
										<input type="button" name="btnFindInStore" value="Find in Store" role="button" aria-pressed="false" class="tiny button expand secondary" onclick="pdpOmnitureProxy('${parentProductId}', 'findinstore');rkg_micropixel('${appid}','findstore');"/>
										</c:otherwise>
									</c:choose>
								</div>
							</c:if> --%>
							<div class="small-12 medium-6 large-4 columns large-no-padding-left">
								<dsp:include page="../addgiftregistry/add_item_gift_registry.jsp">
									<c:if test="${not empty skuVO && skuVO.ltlItem}">
										<dsp:param name="ltlFlag" value="true" />
									</c:if>
									<dsp:param name="disableCTA" value="${disableCTA}"/>
									<dsp:param name="isItemExcluded" value="${isItemExcluded}" />
									<dsp:param name="isCustomizationRequired" value="${skuVO.customizableRequired}" />
								</dsp:include>

								<input type="hidden" name="parentProdId" class="_prodId addItemToRegis addItemToList" value="${parentProductId}" />
							</div>
						<div class="hidden appendSKUInfo">
							<c:if test="${not empty pDefaultChildSku}">
								<p class="smalltext prodSKU">
								<bbbl:label key='lbl_pdp_sku_id' language="${pageContext.request.locale.language}" /> ${pDefaultChildSku}</p>
							</c:if>
						</div>

						<!--  CR Changes  -->
						<c:if test="${MapQuestOn && (appid eq TBS_BuyBuyBabySite)}">
							<bbbt:textArea key="txt_pdp_pickupinstore" language="${pageContext.request.locale.language}" />
						</c:if>

						<div class="message">
							<div id="addToCartErrorDiv" class="error"></div>
						</div>
						<dsp:getvalueof var="brandId" param="productBrand.brandId" />
						<dsp:getvalueof var="brandName" param="productBrand.brandName" />
						<%-- <dsp:droplet name="MinimalProductDetailDroplet">
							<dsp:param name="id" param="productId" />
							<dsp:param name="pageFrom" value="pdptbs" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="brandId" param="productBrand.brandId" />
								<dsp:getvalueof var="brandName" param="productBrand.brandName" /> --%>
								<%-- R2.2 Story - SEO Friendly URL changes --%>
								<dsp:droplet name="BrandDetailDroplet">
									<dsp:param name="origBrandName" value="${brandName}" />
									<dsp:oparam name="seooutput">
										<dsp:getvalueof var="seoUrl" param="seoUrl" />
										<c:set var="Keyword" value="${name}" scope="request" />
										<c:url var="seoUrl" value="${seoUrl}" scope="request" />
									</dsp:oparam>
								</dsp:droplet>
								<c:url var="brandUrl" value="${seoUrl}" />
								<c:if test="${null ne brandId }">
								<div class="small-12 medium-6 columns" itemscope itemtype="//schema.org/Organization">
								<c:choose>
								<c:when test="${GoogleAnalyticsOn}">
									<script type="text/javascript">
										<!--
										<%--
										A brand name with &#39; in it's string is causing js error on IE, like Gloria Jean&#39;s
										For those cases we are forced to replace all instances of &#39; in the string with a ' ' to prevent any js error
										thus Gloria Jean&#39;s ... becomes ... Gloria Jean s

										NOTE: This change only affects the brand name string passed to Google Analytics
										--%>
										var _ga_refinedBrandName = '${brandName}'.replace(/&#39;/,' ');
										// -->
									</script>
								<a href="${brandUrl}" title="${brandName}" onclick="_gaq.push(['_trackEvent', 'Product_BrandLink', 'click', _ga_refinedBrandName]);">
									<bbbl:label key='lbl_pdp_shop_all' language="${pageContext.request.locale.language}" /> <span itemprop="name">${brandName}</span></a>
								</c:when>
								<c:otherwise>
								<a href="${brandUrl}" title="${brandName}">
									<bbbl:label key='lbl_pdp_shop_all' language="${pageContext.request.locale.language}" /> [<span itemprop="name">${brandName}</span>]</a>
								</c:otherwise>
								</c:choose>
								</div>
								</c:if>
						<%-- </dsp:oparam>
						</dsp:droplet> --%>
						<div class="small-12 medium-6 columns">
						<a href="${contextPath}/browse/shipping_policies.jsp" class="popupShipping"><bbbl:label key='lbl_pdp_shipping_cost' language="${pageContext.request.locale.language}" /></a>
						</div>

					</c:when>
					<c:otherwise>

					<div class="alpha">
						<div class="priceQuantity clearfix">
							<span itemprop="offers" itemscope itemtype="//schema.org/Offer">
								<div class="prodPrice" itemprop="price">
									<c:choose>
										<c:when test="${not empty salePriceRangeDescription}">
											<dsp:getvalueof var="salePriceRange" param="productVO.salePriceRangeDescription" />
											<c:choose>
												<c:when test="${fn:contains(salePriceRange,'-')}">
																						<br />
																					</c:when>
												<c:otherwise>&nbsp;</c:otherwise>
											</c:choose>
											<h1 class="price price-sale">
												<dsp:valueof converter="currency" param="productVO.salePriceRangeDescription" />
											</h1>

											<h3 class="price price-original"> <dsp:valueof converter="currency" param="productVO.priceRangeDescription" />
											</h3>
											<dsp:getvalueof idtype="java.lang.String" id="salePrice" param="productVO.salePriceRangeDescription" />
										</c:when>
										<c:otherwise>
											<c:choose>
												<c:when test="${collection==true}">
													<c:if test="${isLeadSKU==false}">
														<div class="small-12 columns price">
															<dsp:valueof converter="currency" param="productVO.priceRangeDescription" />
														</div>
													</c:if>
												</c:when>
												<c:otherwise>
													<div class="small-12 large-7 columns price">
														<dsp:valueof converter="currency" param="productVO.priceRangeDescription" />
													</div>
												</c:otherwise>
											</c:choose>
										</c:otherwise>
									</c:choose>
									<c:if test="${currentSiteId ne BedBathCanadaSite && not collection}">
										<span id="deliver"> <bbbl:label key='ltl_delivery_label' language="${pageContext.request.locale.language}" /></span>
									</c:if>
								</div>
							</span>
						</div>

						<%-- R2.2 117-a1 story --%>
						<c:if test="${collection==true}">
							<c:if test="${isLeadSKU==false}">
								<a class="tiny button secondary" href="#shopCollectionWrapper"><bbbl:label key='lbl_pdp_show_collection' language="${pageContext.request.locale.language}" /></a>
							</c:if>
						</c:if>

					<!-- US: DSK & MOB: Now accepting PayPal banner_ 83 BI |Sprint1(2.2.1)| [Start]-->
						<%-- <c:if test="${paypalOn}">
							<bbbt:textArea key="txt_pdp_paypal_banner" placeHolderMap="${placeHolderMap}" language="${pageContext.request.locale.language}" />
						</c:if> --%>
					<!-- US: DSK & MOB: Now accepting PayPal banner_ 83 BI |Sprint1(2.2.1)| [End]-->
					<!-- Added to display Show All label  -->
					<div class="clearfix cb">
						<dsp:getvalueof var="brandId" param="productBrand.brandId" />
						<dsp:getvalueof var="brandName" param="productBrand.brandName" />
						<%-- <dsp:droplet name="MinimalProductDetailDroplet">
							<dsp:param name="id" param="productId" />
							<dsp:param name="pageFrom" value="pdptbs" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="brandId" param="productBrand.brandId" />
								<dsp:getvalueof var="brandName" param="productBrand.brandName" /> --%>
								<%-- R2.2 Story - SEO Friendly URL changes --%>
								<dsp:droplet name="BrandDetailDroplet">
									<dsp:param name="origBrandName" value="${brandName}" />
									<dsp:oparam name="seooutput">
										<dsp:getvalueof var="seoUrl" param="seoUrl" />
										<c:set var="Keyword" value="${name}" scope="request" />
										<c:url var="seoUrl" value="${seoUrl}" scope="request" />
									</dsp:oparam>
								</dsp:droplet>
								<c:url var="brandUrl" value="${seoUrl}" />
								<c:if test="${null ne brandId }">
								<div class="fl shopAll" itemscope itemtype="//schema.org/Organization">
								<c:choose>
								<c:when test="${GoogleAnalyticsOn}">
									<script type="text/javascript">
										<!--
										<%--
										A brand name with &#39; in it's string is causing js error on IE, like Gloria Jean&#39;s
										For those cases we are forced to replace all instances of &#39; in the string with a ' ' to prevent any js error
										thus Gloria Jean&#39;s ... becomes ... Gloria Jean s

										NOTE: This change only affects the brand name string passed to Google Analytics
										--%>
										var _ga_refinedBrandName = '${brandName}'.replace(/&#39;/,' ');
										// -->
									</script>
								<a href="${brandUrl}" title="${brandName}" onclick="_gaq.push(['_trackEvent', 'Product_BrandLink', 'click', _ga_refinedBrandName]);">
									<bbbl:label key='lbl_pdp_shop_all' language="${pageContext.request.locale.language}" /> <span itemprop="name">${brandName}</span></a>
								</c:when>
								<c:otherwise>
								<a href="${brandUrl}" title="${brandName}">
									<bbbl:label key='lbl_pdp_shop_all' language="${pageContext.request.locale.language}" /> <span itemprop="name">${brandName}</span></a>
								</c:otherwise>
								</c:choose>
								</div>
								</c:if>
							 <%-- </dsp:oparam>
							</dsp:droplet> --%>
						</div>

						<!-- Added to display Show All label Ends -->


					</div>
					</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${appid eq TBS_BedBathUSSite}">
							<c:set var="tipsOn">
						<tpsw:switch tagName="TipsOn_US" />
					</c:set>
						</c:when>
						<c:when test="${appid eq TBS_BuyBuyBabySite}">
							<c:set var="tipsOn">
						<tpsw:switch tagName="TipsOn_baby" />
					</c:set>
						</c:when>
						<c:when test="${appid eq TBS_BedBathCanadaSite}">
							<c:set var="tipsOn">
						<tpsw:switch tagName="TipsOn_CA" />
					</c:set>
						</c:when>
					</c:choose>
					<c:if test="${tipsOn}">
						<dsp:droplet name="TargetingRandom">
							<dsp:param bean="TargeterPDPSecond" name="targeter" />
							<dsp:param name="howMany" value="1" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="tipsTxt" param="element.tipsTxt" />
								<dsp:getvalueof var="tipsTitle" param="element.tipsTitle" />
							</dsp:oparam>
						</dsp:droplet>
						<c:if test="${not empty tipsTxt}">
						<div class="width_6 returnByStore">
							<h3> ${tipsTitle} </h3>
							<div class="desclight2">
								${tipsTxt}
							</div>
								<div class="botShadow"></div>
							</div>
						</c:if>
					</c:if>
					</div>
					</dsp:form>
				</div>
				</div>
			</div>
			<br />

			<div id="productTabs" class="row">
			<ul class="tabs radius" data-tab>
				<li id="prodDetailsTab" class="tab-title tab active"><a onclick="pdpOmniture('${parentProductId}', 'productspec');" href="#panel2-1">Product Details</a></li>

				<c:set var="count" value="2" />
				<dsp:droplet name="ForEach">
					<dsp:param param="productTabs" name="array" />
					<dsp:oparam name="output">
						<li class="tab-title tab"><a onclick="pdpOmniture('${parentProductId}', 'videoplay');" title="<dsp:valueof param='element.tabName'/>" href="#prodExplore-tabs<c:out value='${count}'/>"><dsp:valueof param="element.tabName" /></a></li>
						<c:set var="count" value="${count+1}" />

					</dsp:oparam>
				</dsp:droplet>
				<c:if test="${BazaarVoiceOn}">
					<li id="bvRRTab" class="tab-title tab">
					<a  onclick="pdpOmniture('${parentProductId}', 'ratingreviews');" title="<bbbl:label key='lbl_pdp_product_ratings_reviews' language="${pageContext.request.locale.language}" />" href="#prodExplore-tabs<c:out value='${count}'/>"><bbbl:label key='lbl_pdp_product_ratings_reviews'
									language="${pageContext.request.locale.language}" /></a>
					</li>
					<%-- Start for Chat and Ask and Answer 2.1.1 --%>
					<c:if test="${askAndAnswerEnabled}">
						<li id="askAnswerTab" class="tab-title tab">
						<a onclick="pdpOmniture('${parentProductId}', 'ask and answer');" title="<bbbl:label key='lbl_pdp_product_ask_answer' language="${pageContext.request.locale.language}" />" class="aaBeta" href="#prodExplore-tabs<c:out value='${count+1}'/>"><span><bbbl:label key='lbl_pdp_product_ask_answer'
											language="${pageContext.request.locale.language}" /></span><span class="aaBetaImg"></span><span class="clear"></span></a>
						</li>
					</c:if>
					<c:set var="showQA" value="true" scope="request"/>
					<%-- End for Chat and Ask and Answer 2.1.1 --%>
				</c:if>

			</ul>

			<div class="tabs-content">
				<div class="content active" id="panel2-1">
					<p class="upc-sku" id="showUPC">
						 <c:if test="${not empty pDefaultChildUPC}">
                            UPC:&nbsp;${pDefaultChildUPC}
                        </c:if>
					</p>
                    <p class="upc-sku" id="showSKU">
                        <c:if test="${not empty pDefaultChildSku}">
                            ,&nbsp;SKU:&nbsp;${pDefaultChildSku}
                        </c:if>
                    </p>
                    <p>
                    	<dsp:valueof param="productVO.shortDescription" valueishtml="true" />
                    </p>
					<p>
						<dsp:valueof param="productVO.longDescription" valueishtml="true" />
					</p>
					<c:if test="${LiveClickerOn}">
						<div id="prodVideo" class="omega" data-parentProdId="${parentProductId}">
							<input type="hidden" name="videoProdId" value="${parentProductId}" />
						</div>
					</c:if>
					<!-- R 2.2.1 || Harmon START -->
					<dsp:getvalueof var="harmonLongDescription" param="productVO.harmonLongDescription" />
					<c:if test="${not empty harmonLongDescription}">
						<div class="harmonDesc noMar">
							<dsp:valueof param="productVO.harmonLongDescription" valueishtml="true" />
						</div>
					</c:if>
					<!-- R 2.2.1 || Harmon END   -->
					<c:if test="${WarrantyOn}">
						<dsp:getvalueof var="isWarrantyPriceCheck" param="productVO.warrantyPriceCheck"/>
						<c:if test="${isWarrantyPriceCheck}">
							<dsp:include page="../browse/frag/warranty_place_holder.jsp" >
								<dsp:param name="attribs" value="${attribs}"/>
							</dsp:include>
						</c:if>
			        </c:if>
				</div>
				<c:set var="count" value="2" />
						<dsp:droplet name="ForEach">
							<dsp:param param="productTabs" name="array" />
						<dsp:oparam name="output">


						<div id="prodExplore-tabs<c:out value='${count}'/>" class="content categoryProductTabsData">
								<dsp:valueof param="element.tabContent" valueishtml="true" />
							</div>
						<c:set var="count" value="${count+1}" />

						</dsp:oparam>
					</dsp:droplet>
					<c:if test="${BazaarVoiceOn}">
					<div id="prodExplore-tabs<c:out value='${count}'/>" class="content categoryProductTabsData clearfix divRatings">
						<%--<bbbl:label key='lbl_pdp_product_rate_and_review' language="${pageContext.request.locale.language}" />--%>

						<div id="BVRRContainer">
								<%-- <dsp:droplet name="BVReviewContentDroplet">
									<dsp:param name="productId" param="productId" />
									<dsp:param name="pageURL" value="${pageURL}" />
									<dsp:param name="content" value="REVIEWS" />
									<dsp:oparam name="output">
										<dsp:valueof param="bvContent" valueishtml="true" />
									</dsp:oparam>
								</dsp:droplet> --%>
						<img width="20" height="20" alt="" src="/_assets/global/images/widgets/small_loader.gif">
						</div>

					</div>
					</c:if>

				<%--  Start For BV Integration Container Tag 2.1 #216 --%>

				<c:if test="${showQA}">
					<div id="prodExplore-tabs<c:out value='${count+1}'/>" class="content categoryProductTabsData clearfix divQuestions">
						<dsp:include page="../browse/frag/ask_and_answer.jsp">
							<dsp:param name="productId" value="${parentProductId}" />
							<dsp:param name="pageURL" value="${pageURL}" />
						</dsp:include>
					</div>
					<c:set var="count" value="${count+1}" />
				</c:if>
				<%--  End For BV Integration Container Tag 2.1 #216 --%>
			</div>
		</div>
		<div class="row">
			<c:if test="${collection==true}">
				<input type="hidden" value="${productId}" id="collectionProdId"/>
				<div class="shopCollectionWrapper" id="shopCollectionWrapper"></div>
				<c:choose>
					<c:when test="${isLeadSKU==true}">
						<%-- <dsp:include page="accessoriesForms.jsp">
							<dsp:param name="color" value="${color}" />
							<dsp:param name="parentProductId" value="${parentProductId}" />
							<dsp:param name="crossSellFlag" value="true" />
							<dsp:param name="desc" value="Accessories (pdp)" />
							<dsp:param name="color" value="${color}" />
							<dsp:param name="pageURL" value="${contextPath}${pageURL}"/>
						</dsp:include>
						<dsp:getvalueof var="childProducts" param="collectionVO.childProducts" /> --%>
						<c:set var="omniProp5" value="Product with Accessory" />
					</c:when>
					<c:otherwise>
						<%-- <dsp:include page="collectionForms.jsp">
							<dsp:param name="parentProductId" value="${parentProductId}" />
							<dsp:param name="crossSellFlag" value="true" />
							<dsp:param name="desc" value="Collection (pdp)" />
							<dsp:param name="color" value="${color}" />
							<dsp:param name="pageURL" value="${contextPath}${pageURL}"/>
						</dsp:include>
						<dsp:getvalueof var="childProducts" param="collectionVO.childProducts" /> --%>
						<c:set var="omniProp5" value="Collection" />
					</c:otherwise>
				</c:choose>
				<input type="hidden" id="nextIndex" value="${nextIndex}" />
			</c:if>
		</div>
			<dsp:droplet name="Switch">
				<dsp:param name="value" bean="Profile.transient" />
				<dsp:oparam name="false">
					<dsp:getvalueof var="userId" bean="Profile.id" />
				</dsp:oparam>
				<dsp:oparam name="true">
					<dsp:getvalueof var="userId" value="" />
				</dsp:oparam>
			</dsp:droplet>
			<c:if test="${TagManOn}">
				<dsp:include page="/tagman/frag/productDetails_frag.jsp">
					<dsp:param name="categoryPath" value="${categoryPath}" />
					<dsp:param name="pDefaultChildSku" value="${pDefaultChildSku}" />
				</dsp:include>
			</c:if>
				<%--BBBSL-2541 - Converting certna to AJAX call --%>
				<div id="botCrossSell" class="clearfix loadAjaxContent" role="complementary" 
					data-ajax-url="${contextPath}/browse/pdp_certona_slots.jsp" 
					data-ajax-params-count="3" 
					data-ajax-param1-name="parentProductId"
					data-ajax-param1-value="${parentProductId}" 
					data-ajax-param2-name="productId" 
					data-ajax-param2-value="${productId}" 
					data-ajax-param3-name="linkStringNonRecproduct" 
					data-ajax-param3-value="${linkStringNonRecproduct}">
                    <div>
						<img width="20" height="20" alt="small loader" src="/_assets/global/images/widgets/small_loader.gif" />
					</div>
                </div>
				<%--BBBSL-2541 - Converting certna to AJAX call - End --%>

		</div>
		<dsp:droplet name="ProductViewedDroplet">
			<dsp:param name="id" param="productId" />
		</dsp:droplet>

		</dsp:oparam>
		<dsp:oparam name="error">
		<c:set var="productNotfound" value="true" />
		<div id="content" class="container_12 clearfix" role="main">
		<div class="marTop_20">
			<h3>
							<span class="error">
			<bbbl:label key='lbl_pdp_product_not_available' language="${pageContext.request.locale.language}" />
			</span>
						</h3>
		</div>
		</div>
		</dsp:oparam>
	</dsp:droplet>

	</c:otherwise>
	</c:choose>
	<dsp:droplet name="CanonicalItemLink">
		<dsp:param name="id" value="${parentProductId}" />
		<dsp:param name="itemDescriptorName" value="product" />
		<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
		</dsp:oparam>
	</dsp:droplet>
	<c:import url="/selfservice/find_in_store.jsp">
	<c:param name="enableStoreSelection" value="true" />
			</c:import>
	<c:import url="/_includes/modules/change_store_form.jsp">
		<c:param name="action" value="${contextPath}${finalUrl}" />
	</c:import>
		<%--  New version of view map/get directions
	<c:import url="/selfservice/store/find_store_pdp.jsp" ></c:import>--%>
<c:if test="${TellApartOn}">
<bbb:tellApart actionType="pv" pageViewType="Product" />
<script type="text/javascript">
function tellAPartAddToCart(formData) {
		var i, j;
		if (typeof __cmbLoaded !== 'undefined' && __cmbLoaded && typeof formData !== 'undefined' && typeof formData.addItemResults !== 'undefined' && formData.addItemResults instanceof Array && formData.addItemResults.length > 0) {
			var action=TellApartCrumb.makeCrumbAction("<bbbc:config key='tellapart_merchant_${appid}' configName='ThirdPartyURLs' />", "updatecart");

			action.setActionAttr("UpdateCartType", "PartialAdd");
			for (i=0, j=formData.addItemResults.length; i<j; i++) {
				//console.log(formData.addItemResults[i]);
				action.beginItem();
				action.setItemAttr("SKU", formData.addItemResults[i].skuId);
				action.setItemAttr("ProductPrice", (formData.addItemResults[i].price).substr(1));
				action.setItemAttr("ProductCurrency", "USD");
				action.setItemAttr("ItemCount", formData.addItemResults[i].qty);
				action.endItem();
			}
			action.setUserId("${email}");
			action.finalize();
		}
	}

</script>
</c:if>
<dsp:getvalueof var="addToList" param="addToList" />
<dsp:getvalueof var="prodList" param="prodList" />
<dsp:getvalueof var="showpopup" param="showpopup" />
<dsp:getvalueof var="registryId" param="registryId" />
<dsp:getvalueof var="registryName" param="registryName" />
<dsp:getvalueof var="totQuantity" param="totQuantity" />
<dsp:getvalueof id="omniList" value="${sessionScope.added}" />
<script type="text/javascript">
$(function () {
	var registryId = '${fn:escapeXml(registryId)}';
	var addtoList='${addToList}';
	if(registryId.length>0){
	rkg_micropixel("${appid}","addtoregistry");
	}
	if(addtoList.length>0){
	rkg_micropixel("${appid}","wish");
	}
});

function addToWishList(){
	var qty = '${param.qty}';
	var price = '${certonaPrice}';
	var totalPrice=qty*price;
	var personalizationMessage ='${personalizationMessage}';
	var isPersonalized = false;
	var finalOut= ';'+'${fn:escapeXml(param.productId)}'+';;;event38='+'${fn:escapeXml(param.qty)}'+'|event39='+totalPrice+';eVar30='+'${fn:escapeXml(param.skuId)}';
	if (personalizationMessage.length > 0) {
		isPersonalized = true;
	}
	additemtolist(finalOut, isPersonalized, personalizationMessage);
}
</script>
<dsp:droplet name="/atg/dynamo/droplet/Switch">
	<dsp:param name="value" bean="Profile.transient" />
	<dsp:oparam name="false">

			<c:set var="check" value="true" scope="session" />

	</dsp:oparam>
</dsp:droplet>
<c:choose>
	<c:when test="${not empty collectionId_Omniture}">
		<c:set var="omni_prod">${fn:substring(collectionId_Omniture,0,fn:length(collectionId_Omniture)-1)}</c:set>
	</c:when>
	<c:otherwise>
		<%-- <c:choose>
			<c:when test="${not empty pDefaultChildSku}">
				<c:set var="omni_prod" >;${parentProductId};;;;eVar30=${pDefaultChildSku}</c:set>
			</c:when>
			<c:otherwise> --%>
				<c:set var="omni_prod">;${parentProductId}</c:set>
			<%-- </c:otherwise>
		</c:choose> --%>
	</c:otherwise>
</c:choose>
	<c:choose>
		<c:when test="${null ne addToList}">
		<script type="text/javascript">
			var resx = new Object();
			resx.event = "wishlist";
			resx.pageid = "";
			var prodList = "<c:out value="${prodList}"/>";
			resx.itemid = prodList.replace(/[\[\s]/g,'').replace(/[,\]]/g,';');
		</script>
		</c:when>
		<c:otherwise>
		<script type="text/javascript">
				var resx = new Object();
				resx.appid = "${appIdCertona}";
				resx.event = "product";
				resx.top1 = 100000;
				resx.top2 = 100000;
				resx.itemid = '${linkStringNonRecproduct}';
				resx.customerid = "${userId}";
		//In case of add to cart event, call from browse.js
				function callCertonaResxRun(certonaString) {
						resx.event = "addtocart_op";
						resx.itemid = certonaString;
						resx.pageid = "";
						resx.links = "";
						if (typeof certonaResx === 'object') { certonaResx.run();  }
				}

				function certonaFeed(json) {

						var cfProdId = '',
							cfPrice = '',
							cfQty = '',
							cfRegistryId = '',
							cfRegistryName = '';

						for (var i in json.addItemResults) {
							var getPrice = json.addItemResults[i].price,
								intPrice = getPrice.replace('$',''),
								intQty = parseInt(json.addItemResults[i].qty, 10);

							cfProdId += json.addItemResults[i].prodId + ';';
							cfQty +=  intQty + ';';
							cfPrice +=  (intPrice*intQty) + ';';
							cfRegistryId += json.addItemResults[i].registryId + ';';
						}

						cfRegistryName += json.registryName + ';';
						resx.appid = "${appIdCertona}";
						resx.links = '${linksCertona}' + '${linkStringNonRecproduct}' + '${productList}';
						resx.pageid = "${pageIdCertona}";
						resx.customerid = "${userId}";

						resx.event = "op_Registry+" + cfRegistryName;
						resx.itemid = cfProdId;
						resx.qty = cfQty;
						resx.price = cfPrice;
						resx.transactionid = cfRegistryId;
						if (typeof certonaResx === 'object') { certonaResx.run();  }
					}

				function certonaFeedAddItem() {

					var productId='${fn:escapeXml(param.productId)}';
					var skuId='${fn:escapeXml(param.skuId)}';
					var registryName= '${param.registryName}';
					var registryId='${fn:escapeXml(param.registryId)}';
					var qty = '${param.totQuantity}';
					var price = '${certonaPrice}';
						resx.appid = "${appIdCertona}";
						resx.links = '${linksCertona}' + '${linkStringNonRecproduct}' + '${productList}';
						resx.pageid = "${pageIdCertona}";
						resx.customerid = "${userId}";

						resx.event = "op_Registry+" + registryName;
						resx.itemid = productId;
						resx.qty = qty;
						resx.price = price;
						resx.transactionid = registryId;
						if (typeof certonaResx === 'object') { certonaResx.run();  }
					}

			</script>
		</c:otherwise>
		</c:choose>

		<dsp:droplet name="BBBConfigKeysDroplet">
			<dsp:param name="configKey" value="DimNonDisplayConfig" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="configMap" param="configMap" />
			</dsp:oparam>
		</dsp:droplet>

		<dsp:droplet name="BBBURLEncodingDroplet">
			<dsp:param name="URL" value="${pageContext.request.requestURL}?${pageContext.request.queryString}" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="queryString" param="queryString" />
			</dsp:oparam>
		</dsp:droplet>
		<dsp:getvalueof var="productId" param="productId" />


		<c:forEach var="conMap" items="${configMap}">
			<c:if test="${conMap.key eq appid}">
					<c:choose>
					<c:when test="${phantomCategory}">
					<c:set var="floatingNodes" value="Floating nodes" />
					<c:set var="prop2Var">${floatingNodes}>${rootCategoryName}</c:set>
					<c:set var="rkgCategoryNames" value="${floatingNodes},${rootCategoryName}" />
					<c:if test="${!empty categoryNameL1}">
					<c:set var="prop3Var">${floatingNodes}>${rootCategoryName}>${categoryNameL1}</c:set>
					<c:set var="rkgCategoryNames" value="${floatingNodes},${rootCategoryName},${categoryNameL1}" />
					</c:if>
					<c:set var="channel">${floatingNodes}</c:set>
					<c:if test="${fn:contains(queryString, 'fromCollege')}">
						<c:set var="rootCategoryName" value="College" />
						<c:set var="prop3Var"> ${rootCategoryName}>${rootCategoryName}>${refinedNameProduct}</c:set>
						<c:set var="rkgCategoryNames" value="${rootCategoryName},${refinedNameProduct}" />
					</c:if>
					</c:when>
					<c:otherwise>
					<c:choose>
					<c:when test="${(clearanceCategory == rootCategory) || (whatsNewCategory==rootCategory) }">
					<c:set var="prop2Var">${rootCategoryName}>${rootCategoryName}</c:set>
					<c:set var="rkgCategoryNames" value="${rootCategoryName},${refinedNameProduct}" />

					<c:set var="channel">${rootCategoryName}</c:set>
					<c:if test="${!empty categoryNameL1}">
						<c:set var="prop3Var">${rootCategoryName}>${rootCategoryName}>${categoryNameL1}</c:set>
						<c:set var="rkgCategoryNames" value="${rootCategoryName},${refinedNameProduct},${categoryNameL1}" />
					</c:if>
					</c:when>
					<c:otherwise>
					<c:set var="queryString" value="${queryString}" />
					<c:choose>
					<c:when test="${!empty categoryNameL1}">
					<c:set var="prop2Var">${rootCategoryName}>${categoryNameL1}</c:set>
					<c:set var="rkgCategoryNames" value="${rootCategoryName},${categoryNameL1}" />
					</c:when>
					<c:otherwise>
					<c:set var="prop2Var">${rootCategoryName}>${rootCategoryName}</c:set>
					<c:set var="rkgCategoryNames" value="${rootCategoryName}" />
					</c:otherwise>
					</c:choose>
					<c:set var="channel">${rootCategoryName}</c:set>
					<c:if test="${!empty categoryNameL2}">
						<c:set var="prop3Var">${rootCategoryName}>${categoryNameL1}>${categoryNameL2}</c:set>
						<c:set var="rkgCategoryNames" value="${rootCategoryName},${categoryNameL1},${categoryNameL2}" />
					</c:if>
					</c:otherwise>
					</c:choose>
					</c:otherwise>
					</c:choose>
				</c:if>
			</c:forEach>


		<dsp:getvalueof var="searchKeyword" param="Keyword" />
		<c:choose>
		<c:when test="${empty searchKeyword}">
			<c:set var="prop9Var" value="" />
		</c:when>
		<c:otherwise>
			<c:set var="prop9Var" value="products"></c:set>
		</c:otherwise>
		</c:choose>
		<div id="energyGuideLink" class="reveal-modal" data-reveal-ajax="true" data-reveal>
			<iframe src="" width="1050px" height="900px"></iframe>
			<a class='close-reveal-modal'>&#215;</a>
		</div>
		<div id="lightingFactsLink" class="reveal-modal" data-reveal-ajax="true" data-reveal>
			<iframe src="" width="1050px" height="900px"></iframe>
			<a class='close-reveal-modal'>&#215;</a>
		</div>
		<script type="text/javascript">
			$(".prod-attrib-view-energy-guide").parents("a").attr("data-reveal-id","energyGuideLink").removeClass("newOrPopup").parents(".productLinks.PDPM").addClass("marLeft_10 marBottom_10");
			$(".prod-attrib-Lighting-Facts").parents("a").attr("data-reveal-id","lightingFactsLink").removeClass("newOrPopup");
		</script>
		<script type="text/javascript">
			var omni_refinedNameProduct = '${fn:replace(fn:replace(refinedNameProduct,'\'',''),'"','')}';
			var omni_omniProdName = '${fn:replace(fn:replace(omniProdName,'\'',''),'"','')}';
			var omni_channel = '${fn:replace(fn:replace(channel,'\'',''),'"','')}';
			var omni_prop2Var = '${fn:replace(fn:replace(prop2Var,'\'',''),'"','')}';
			var omni_prop3Var = '${fn:replace(fn:replace(prop3Var,'\'',''),'"','')}';
			var omni_omniProp5 = '${fn:replace(fn:replace(omniProp5,'\'',''),'"','')}';
			var omni_prop9Var = '${fn:replace(fn:replace(prop9Var,'\'',''),'"','')}';
			var productNotfound = '${productNotfound}';
			var collection = '${collection}';
			var everLivingProduct = '${everLivingProduct}';
			var fireEvent90="${fireEvent90}";
			var event90='';
			var BBB = BBB || {};
			//code updates to fix issue 62 from omniture defects spread sheet
			var event = 'prodView';
			if(collection == 'true'){
				event = event+',event54';
			}
			if(fireEvent90 == 'true') {
	        	  event90=',event90';
	       	}
			if(productNotfound == 'true'){
				BBB.productInfo = {
					pageNameIdentifier: 'productDetails',
					pageName: 'product not available',
					channel: 'Error page',
					prop1: 'Product Details Page',
					prop2: 'Error page',
					prop3: 'Error page',
					prop4: '',
					events: 'prodView,event56' + event90,
					products: '<c:out value="${omni_prod}"/>'
				};
			}if(everLivingProduct== 'true' ){
				BBB.productInfo = {
						pageNameIdentifier: 'productDetails',
						pageName: 'Product Details Page>'+omni_omniProdName,
						channel: 'EverLiving Product details page',
						prop1: 'EverLiving Product details page',
						prop2: 'EverLiving Product details page',
						prop3: 'EverLiving Product details page',
						prop4: '',
						events: event+',event56' + event90,
						products: '<c:out value="${omni_prod}"/>'
					};
				}else{
				BBB.productInfo = {
					pageNameIdentifier: 'productDetails',
					pageName: 'Product Detail>'+omni_refinedNameProduct,
					channel: omni_channel,
					prop1: 'Product Details Page',
					prop2: omni_prop2Var,
					prop3: omni_prop3Var,
					prop4: '',
					prop5: omni_omniProp5,
					prop9: omni_prop9Var,
					events: event + event90,
					<c:if test="${enableKatoriFlag}">
				<%-- BBBH-3759 - in case of personlizable product event81 should get fired --%>
					<c:if test="${ ((skuVO.customizableRequired || skuVO.customizationOffered) && not empty skuVO.customizableCodes)}">		
						events: event+',event81',
					</c:if>
				</c:if>	
					products: '<c:out value="${omni_prod}"/>'
				};
			}

		<c:choose>
		<%-- defect 68 when viewing out stock items. event40 fires. It should be event17
		when viewing out stock items. event40 fires. It should be event17 --%>
		<%--RM#39232 set event40--%>
			<c:when test="${inStock == false}">
				BBB.productInfo.events = event + ',event17,event40' + event90
						<c:if test="${ ((skuVO.customizableRequired || skuVO.customizationOffered) && not empty skuVO.customizableCodes)}">
				            +',event81'
			            </c:if>
						<c:choose>
				<c:when test="${isMultiSku == true}">
					BBB.productInfo.products = ';${parentProductId};;;;eVar30=${pDefaultChildSku}';
			</c:when>
	   			<c:otherwise>
	   				BBB.productInfo.products = ';${parentProductId}';
	   			</c:otherwise>
		</c:choose>
			</c:when>
		</c:choose>

		
		
		function pdpOmnitureProxy(productId, event) {
				if (typeof pdpOmniture === 'function') { pdpOmniture(productId, event); }
		}

		function openmedia()
		{

		window.open('','module35892976','toolbar=no,location=no,status=no,menubar=no,scrollbars=yes,resizable=yes')
		}

		</script>
		<!-- YourAmigo code starts  6/18/2013-->
		<c:if test="${YourAmigoON}">
		<dsp:getvalueof var="isTransient" bean="Profile.transient" />
		<c:if test="${isTransient eq false}">
			<!-- ######################################################################### -->
			<!--  Configuring the javascript for tracking signups (to be placed on the     -->
			<!--  signup confirmation page, if any).                                       -->
			<!-- ######################################################################### -->
<c:choose>
<c:when test="${(currentSiteId eq TBS_BuyBuyBabySite)}">
<script src="https://support.youramigo.com/52657396/tracev2.js"></script>
<c:set var="ya_cust" value="52657396"></c:set>
</c:when>
<c:when test="${(currentSiteId eq TBS_BedBathUSSite)}">
<script src="https://support.youramigo.com/73053126/trace.js"></script>
<c:set var="ya_cust" value="73053126"></c:set>
</c:when>
</c:choose>
			<script type="text/javascript">
			/* <![CDATA[ */

				/*** YA signup tracking code for Bed Bath & Beyond (www.bedbathandbeyond.com) ***/

				// --- begin customer configurable section ---

				ya_tid = Math.floor(Math.random()*1000000);	// Set XXXXX to the ID counting the signup, or to a random
									// value if you have no such id - eg,
									// ya_tid = Math.random();
				ya_pid = ""; // Set YYYYY to the type of signup - can be blank
									// if you have only one signup type.

				ya_ctype = "REG"; // Indicate that this is a signup and not a purchase.
				// --- end customer configurable section. DO NOT CHANGE CODE BELOW ---

				ya_cust = '${ya_cust}';
				try { yaConvert(); } catch(e) {}

			/* ]]> */
			</script>
		</c:if>
		</c:if>

		<%--RKG Comparison Shopping tracking starts--%>
		<c:if test="${RKGOn}">
		<script type="text/javascript" src="<bbbc:config key="secure_merchand_ma2q_js" configName="ThirdPartyURLs" />"></script>
		</c:if>
		<%--RKG Comparison Shopping tracking ends--%>
		<dsp:getvalueof bean="CartModifierFormHandler.collectionQty" var="collectionQty"/>
		<c:if test="${collectionQty > 0 }">
			<script type="text/javascript">
				var qty = ${collectionQty};
				$(function(){
					if(qty == 1){
						$('#infoModal').html("<h2>"+ qty +"&nbsp;new item added to your cart</h2> <a class='close-reveal-modal'>&#215;</a>").foundation('reveal', 'open');
					} else {
						$('#infoModal').html("<h2>"+ qty +"&nbsp;new items added to your cart</h2> <a class='close-reveal-modal'>&#215;</a>").foundation('reveal', 'open');
					}
				});
			</script>
		</c:if>
  </div> <!-- added to adjust for footer -->
	</jsp:body>
	</bbb:pageContainer>

</dsp:page>
