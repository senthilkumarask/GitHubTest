<dsp:page>

	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet"/>
	<dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/EverLivingPDPDroplet"/>
	<dsp:importbean bean="/com/bbb/selfservice/ChatAskAndAnswerDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BreadcrumbDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProdToutDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductViewedDroplet"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean"/>
	<dsp:importbean bean="/com/bbb/profile/session/BBBSessionBean"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ExitemIdDroplet" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:importbean bean="/atg/targeting/TargetingRandom"/>
    <dsp:importbean bean="/atg/multisite/Site"/>
    <dsp:importbean bean="/com/bbb/certona/droplet/CertonaDroplet"/>
   	<dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
    <dsp:importbean bean="/com/bbb/cms/droplet/PageTabsOrderingDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/CategoryLandingDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BrandDetailDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/RedirectDroplet"/>
	<dsp:importbean bean="/com/bbb/framework/security/ValidateParametersDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/GetApplicableLTLShippingMethodsDroplet" />
	<dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
	
	<dsp:getvalueof var="parentProductId" param="productId" />
	<dsp:getvalueof var="skuIdfromURL" param="skuId" />
  	<dsp:getvalueof var="appid" bean="Site.id" />
  	<dsp:getvalueof var="registryId" param="registryId" />
  	<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${appid}"/>
  	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
  	<dsp:getvalueof var="CategoryId" param="categoryId"/>
	<dsp:getvalueof var="poc" param="poc" scope="request"/>
	<dsp:getvalueof var="view" value="${fn:escapeXml(param.view)}" scope="request"/>
	<dsp:getvalueof var="sopt" bean="/OriginatingRequest.sopt"/>
	<%-- BBBI-3048 Omniture Tagging Start--%>
	<dsp:getvalueof var="keyword" param="Keyword"/>
	<dsp:getvalueof var="brandId" param="brandId"/>
	<dsp:getvalueof var="categoryId" param="categoryId"/>
	<dsp:getvalueof var="fromPage" param="fromPage"/>
	<dsp:getvalueof var="vendorParam" bean="SessionBean.vendorParam"/>
	<c:set var="fireEvent90" value="false"/>
	<c:choose>
		<c:when test="${not empty sessionScope.boostCode && sessionScope.boostCode != '00'}">
			<c:set var="l2l3BoostFlag"><bbbc:config key="L2L3BoostFlag" configName="FlagDrivenFunctions"/></c:set> 
			<c:set var="brandsBoostFlag"><bbbc:config key="BrandsBoostFlag" configName="FlagDrivenFunctions"/></c:set> 
			<c:set var="keywordBoostFlag"><bbbc:config key="KeywordBoostFlag" configName="FlagDrivenFunctions"/></c:set> 
			<c:if test="${(((keywordBoostFlag && (not empty keyword || fromPage == 'searchPage')) 
				|| (brandsBoostFlag && (not empty brandId || fromPage == 'brandsPage')) 
				|| (l2l3BoostFlag && (not empty categoryId || fromPage == 'categoryPage'))) && (isEndecaControl eq false))}">
	    		<c:set var="fireEvent90" value="true"/>
	    	</c:if>       
		</c:when>
		<c:when test="${(not empty vendorParam) && (not empty keyword || fromPage == 'searchPage')}">
			<c:set var="fireEvent90" value="true"/> 
		</c:when>
	</c:choose>
	<%-- BBBI-3048 Omniture Tagging End--%>
	
	<!--  BBBJ-268 STARTS -->		
	 <c:set var="lblReviewsCount"><bbbl:label key="lbl_reviews_count" language="${pageContext.request.locale.language}" /></c:set>
     <c:set var="lblReviewCount"><bbbl:label key="lbl_review_count" language="${pageContext.request.locale.language}" /></c:set>
     <c:set var="lblGridWriteReviewLink"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></c:set>
<!--  BBBJ-268 ENDS -->		
	<c:set var="productId"><dsp:valueof value="${fn:escapeXml(param.productId)}"/></c:set>
	<c:set var="refinedNameProduct" value="${productVO.refinedName}"/>
	<c:set var="ShipMsgDisplayFlag" scope="request"><bbbc:config key="ShipMsgDisplayFlag" configName="FlagDrivenFunctions"/></c:set>
	<c:set var="shippingAttributesList">
		<bbbc:config key="shippingAttributesList" configName="ContentCatalogKeys" />
	</c:set>
	<dsp:getvalueof var="personalizationMessage" bean="GiftlistFormHandler.personalizationMessage"/>
	<dsp:getvalueof var="strategyName" param="strategyName" />
	<c:set var="lblAboutThe"><bbbl:label key="lbl_accessibility_about_the" language="${pageContext.request.locale.language}" /> product </c:set>
	<c:set var="writeFirstReview">Write the first review</c:set>	
	<c:set var="lblForThe"><bbbl:label key="lbl_accessibility_for_the" language="${pageContext.request.locale.language}" /> product </c:set>
	<%--BBBSL-2450 start --%>
	<c:if test="${empty poc }">
		<dsp:droplet name="/com/bbb/commerce/browse/droplet/CollectionParentDroplet">
			<dsp:param name="productId" param="productId" />
			<dsp:param name="siteId" value="${appid}"/>
			<dsp:oparam name="output">
				<dsp:getvalueof var="poc" scope="request" param="collectionParentProductId" />
			</dsp:oparam>
		</dsp:droplet>
	</c:if>
	<%--BBBSL-2450 end --%>

<dsp:getvalueof var="serverName" param="serverName" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>

<c:set var="serverPath" value="https://${serverName}"/>

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
	<c:set var="CertonaContext" value="" scope="request"/>
	<c:set var="SchemeName" value="pdp_cav;pdp_fbw"/>
	<c:set var="omniProp5" value="Product"/>
	<c:set var="productNotfound" value="false"/>
	<c:set var="showQA" value="" scope="request"/>

	<c:set var="askAndAnswerEnabled" value="false" scope="request" />
	<c:set var="chatEnabled" value="false" scope="request" />
	<c:set var="chatCode" value="" scope="request" />

	<c:set var="scene7DomainPath">
		<bbbc:config key="BBBSceneSevenDomainPath" configName="ThirdPartyURLs" />
	</c:set>
	<c:set var="saSrc"><bbbc:config key="socialAnnexURL" configName="ThirdPartyURLs" /></c:set>
	<c:set var="vdcAttributesList">
		<bbbc:config key="vdcAttributesList" configName="ContentCatalogKeys" />
	</c:set>
	
	
	<dsp:droplet name="/com/bbb/personalstore/droplet/ContextCookieDroplet">
		<dsp:param name="productId"  param="productId" />
	</dsp:droplet>
	
	<c:set var="WarrantyOn" scope="request">
		<bbbc:config key="WarrantyOn" configName="FlagDrivenFunctions" />
	</c:set>
	
	<bbb:pageContainer pageVariation="${pageVariation}">
	<input type="hidden" id="socialAnnex" name="socialAnnex" value="<bbbc:config key="socialAnnexFlag" configName="FlagDrivenFunctions" />">
	<c:if test="${not empty personalizationMessage}">
	  	<input type="hidden" id="personalizationMessage" name="personalizationMessage" value="${personalizationMessage}">
	</c:if>
	<jsp:attribute name="section">browse</jsp:attribute>
	<jsp:attribute name="PageType">ProductDetails</jsp:attribute>
	<jsp:attribute name="pageWrapper">productDetails ${pageWrapperClass} useStoreLocator useLiveClicker useBazaarVoice useFB useCertonaAjax useScene7</jsp:attribute>
	<jsp:body>
	<input type="hidden" id="enableKatoriFlag" name="enableKatoriFlag" value="${enableKatoriFlag}">
	  	<jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request"/>
      	<c:set target="${placeHolderMap}" property="contextPath" value="${contextPath}"/>
      	<c:set target="${placeHolderMap}" property="imagePath" value="${imagePath}"/>
	  	<c:set var="BedBathUSSite">
			<bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
		</c:set>
		<c:set var="BuyBuyBabySite">
			<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
		</c:set>
		<c:set var="BedBathCanadaSite">
			<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
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
		<c:set var="SurchargeMayApply">
			<bbbl:label key='ltl_delivery_surcharge_may_apply' language="${pageContext.request.locale.language}" />
		</c:set>
		<c:set var="LTLToolTip">
			<bbbl:label key='ltl_tool_tip' language="${pageContext.request.locale.language}" />
		</c:set>
		<c:set var="TruckSrc">
			<bbbc:config key="truck_image_src" configName="LTLConfig_KEYS" />
		</c:set>
		<c:set var="QuestMarkSrc">
		<bbbc:config key="quesmark_image_pdp" configName="LTLConfig_KEYS" />
		</c:set>
		<c:set var="fetchRelatedCategories">
		<bbbc:config key="fetchRelatedCategories" configName="ContentCatalogKeys" />
		</c:set>
	<c:choose>
     <c:when test="${appid == BedBathCanadaSite}">
     <c:set var="clearanceCategory">
		<bbbc:config key="BedBathCanada_clearanceCategories" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="whatsNewCategory">
		<bbbc:config key="whatsNewCategory_bedbathcanada" configName="ContentCatalogKeys" />
	</c:set>
	</c:when>
	<c:when test="${appid == BedBathUSSite}">
     <c:set var="clearanceCategory">
		<bbbc:config key="BedBathUS_clearanceCategories" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="whatsNewCategory">
		<bbbc:config key="whatsNewCategory_bedbathus" configName="ContentCatalogKeys" />
	</c:set>
	</c:when>
	<c:when test="${appid == BuyBuyBabySite}">
     <c:set var="clearanceCategory">
		<bbbc:config key="BuyBuyBaby_clearanceCategories" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="whatsNewCategory">
		<bbbc:config key="whatsNewCategory_buybuybaby" configName="ContentCatalogKeys" />
	</c:set>
	</c:when>
	</c:choose>
		<c:set var="BedBathUSSite">
		<bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
		</c:set>
		<c:set var="BuyBuyBabySite">
			<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
		</c:set>
		<c:set var="BedBathCanadaSite">
			<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
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

		<c:set var="parentProductId"><c:out value="${parentProductId}"/></c:set>
		<c:set var="CertonaContext" scope="request">${parentProductId};</c:set>

		<dsp:getvalueof var="colortemp" param="color"/>
        <c:set var="color" value="${fn:replace(colortemp,'%20',' ')}" />
		
	
		<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
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
					<dsp:param name="siteId" value="${appid}"/>
					<dsp:oparam name="output">
					<dsp:getvalueof var="everLivingProduct" param="everLivingProduct" />
					</dsp:oparam>
					</dsp:droplet>
	   		 <dsp:droplet name="ProductLookup">
			    <dsp:param name="id" param="productId"/>
			    <dsp:oparam name="output">
			    <dsp:setvalue param="product" paramvalue="element"/>
				<dsp:getvalueof var="collection" vartype="java.lang.String" param="product.collection"/>
				</dsp:oparam>
			</dsp:droplet>
		</c:if>
		<c:choose>
		<c:when test="${everLivingOn && everLivingProduct}">
		<dsp:droplet name="ProductLookup">
      	<dsp:param name="id" param="productId"/>
      	<dsp:oparam name="output">
	      	<dsp:setvalue param="product" paramvalue="element"/>
			<dsp:getvalueof var="collection" vartype="java.lang.String" param="product.collection"/>
      	</dsp:oparam>
      	</dsp:droplet>
		<dsp:include page="everLivingPDP.jsp">
			<dsp:param name="id" param="productId" />
			<dsp:param name="siteId" value="${appid}"/>
			<dsp:param name="skuId" param="skuId"/>
			<dsp:param name="color" value="${color}"/>
		</dsp:include>
		</c:when>
		<c:otherwise>
		<dsp:droplet name="ProductDetailDroplet">
			<dsp:param name="id" param="productId" />
				<dsp:param name="siteId" value="${appid}"/>
				<dsp:param name="skuId" param="skuId"/>
				<dsp:param name="color" value="${color}"/>
				<dsp:param name="isMainProduct" value="true"/>
				<dsp:param name="registryId" param="registryId" />
				<dsp:param name="poc" value="${poc}"/>
					<dsp:oparam name="output">
						<dsp:getvalueof var="skuVO" param="pSKUDetailVO" />
						<c:set var ="vdcSkuId" value="${skuVO.skuId}"/>
						<c:set var ="isVdcSku" value="${skuVO.vdcSku}"/>
						<dsp:getvalueof var="giftFlag" param="productVO.giftCertProduct"/>
						<dsp:getvalueof var="isItemInWishlist" param="isItemInWishlist" />
						<dsp:getvalueof var="ltlDsl" vartype="java.util.List" param="ltlDsl" />
						<%--
						<dsp:getvalueof var="parentCollProductId" param="productVO.giftCertProduct"/>
						 --%>
						<dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>
						<dsp:getvalueof var="productId" param="productVO.productId"/>
						<dsp:getvalueof var="isLtlProduct" param="productVO.ltlProduct"/>
						<dsp:getvalueof var="collectionId_Omniture" param="collectionVO.omnitureCollectionEvar29"/>
						<dsp:getvalueof var="isIntlRestricted" param="productVO.intlRestricted"/>
						<dsp:getvalueof var="isCollectionIntlRestricted" param="collectionVO.intlRestricted"/>
						<dsp:getvalueof var="childSKUs" param="productVO.childSKUs"/>
						<dsp:getvalueof var="zoomQuery" param="zoomQuery"/>
						<dsp:getvalueof var="storeDetails" param="storeDetails"/>
						<dsp:getvalueof var="favStoreStockStatus" param="favStoreStockStatus"/>
						<c:set var ="skuId" value="${skuVO.skuId}"/>
						<input type="hidden" id="zoomQuery" name="zoomQuery" value="${zoomQuery}">
						<c:set var="giftFlag" scope="request" value="${giftFlag}"/>
						<dsp:getvalueof var="shipMsgFlag" param="productVO.shipMsgFlag"/>
						<dsp:getvalueof var="displayMsg" param="pSKUDetailVO.displayShipMsg"/>
						<dsp:getvalueof var="hasSddAttribute" param="pSKUDetailVO.hasSddAttribute"/>
						<input autocomplete="off" type="hidden" name="sddAttr" id="sddAttr" value="${hasSddAttribute}"/>
						<c:if test="${empty displayMsg}">
							<dsp:getvalueof var="displayMsg" param="productVO.displayShipMsg"/>
						</c:if>
						
						<c:choose>
						<c:when test="${not empty skuVO}">
							<dsp:getvalueof var="attribs" param="pSKUDetailVO.skuAttributes"/>
						</c:when>
						<c:otherwise>
							<dsp:getvalueof var="attribs" param="productVO.attributesList"/>
						</c:otherwise>
						</c:choose>

						<div id="content" class="container_12 clearfix" role="main">
							<div class="breadcrumbs grid_12">
								<div class="grid_11 alpha">
									<dsp:include page="breadcrumb.jsp">
									<dsp:param name="siteId" value="${appid}"/>
									 <dsp:param name="productId" value="${productId}" />
									 <dsp:param name="poc" value="${poc}" />
									</dsp:include>
								</div>
							 	<div class="grid_1 omega fr share">
                                    <%-- <a href="/store/browse/print/product_details.jsp?productId=${parentProductId}" class="print" title="Print"></a> --%>
	                                <a href="#" class="print" title="Print"></a>
									<a href="#" class="email" title="Email"></a>
	                            </div>
                            </div>
							<dsp:getvalueof var="isLeadSKU" param="collectionVO.leadSKU"/>
							<dsp:getvalueof var="collection" param="productVO.collection"/>
							<dsp:getvalueof var="linkStringNonRecproduct" param="linkStringNonRecproduct" />
							<dsp:getvalueof var="sameDayDelEligible" param="sameDayDelEligible" />
						
							<dsp:getvalueof var="rkgProductList" param="rkgCollectionProdIds" />
							<dsp:getvalueof id="showFlag" idtype="java.lang.String"	param="showFlag" />
							<dsp:getvalueof var="prodGuideId" param="productVO.shopGuideId"/>
							<dsp:getvalueof var="vendorName" param="productVO.vendorInfoVO.vendorName"/>							
							<dsp:include src="../_includes/modules/personalizationVar.jsp" >
								<dsp:param name="skuVO" value="${skuVO}"/>								
							</dsp:include>
							
							<div  itemscope itemtype="http://schema.org/Product">
							<div class="grid_12 marTop_10 <c:if test="${showFlag == '1'}">hasProdNextCollection</c:if>">

							<div class="grid_6 alpha zoomProduct">
                                <c:if test="${showFlag == '1'}">
                                    <dsp:include page="/bbcollege/next_college_collection.jsp"/>
                                </c:if>

                                <dsp:getvalueof var="largeImage" param="productVO.productImages.largeImage"/>
                                <dsp:getvalueof var="smallImage" param="productVO.productImages.smallImage"/>
                                <dsp:getvalueof var="thumbnailImage" param="productVO.productImages.thumbnailImage" />
                                <dsp:getvalueof var="basicImage" param="productVO.productImages.basicImage" />
                                <dsp:getvalueof var="prdName" param="productVO.name" scope="request"/>
                                <dsp:getvalueof var="refinedNamePrd" param="productVO.refinedName" scope="request"/>
                                <dsp:getvalueof var="zoomFlag" param="productVO.zoomAvailable"/>
                                
                                <c:set var="productName">${fn:escapeXml(prdName)}</c:set>
                                <c:set var="refinedNameProduct">${fn:escapeXml(refinedNamePrd)}</c:set>

                               <%-- <div>${productName}</div> --%>
								<%-- 
								<img src="http://s7d9.scene7.com/is/image/BedBathandBeyond/${largeImage}" />
								<dsp:valueof param="productVO.productImages"/>

								Domain sharding S7 Url
								--%>							
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
								

								
								<div id="s7ProductImageWrapper" <c:if test="${zoomFlag && SceneSevenOn && (not empty largeImage)}"> class="bbbS7ImageWrapper" data-s7ImgsetID="${largeImage}" data-s7ImageId="${basicImage}" </c:if> data-parentProdId="${parentProductId}">
                                    <div class="bbbS7Main ">
	                                    <div id="s7ProductImageWrapperInner" class="easyzoom easyzoom--adjacent ">
                                        <c:choose>
                                        	<%--  <c:when test="${itemAlreadyPersonalized }">
                                        		<img id="mainProductImg" src="${personalizedLargeImage}" class="mainProductImage"  alt="${productName}" />
                                        	</c:when>  --%>
                                            <c:when test="${empty largeImage || !SceneSevenOn}">
                                                <img itemprop="image" id="mainProductImg" onload="BBB.addPerfMark('ux-primary-content-displayed');" src="${imagePath}/_assets/global/images/no_image_available.jpg" class="mainProductImage noImageFound" height="478" width="478" alt="${productName}" onClick="_gaq.push(['_trackEvent', 'zoom', 'hero image', '${refinedNameProduct}']);"/>
	                                            <script>BBB.addPerfMark('ux-primary-content-displayed');</script>
                                            </c:when>
                                            <c:when test="${!zoomFlag && SceneSevenOn && (not empty largeImage)}">
                                                <img itemprop="image" id="mainProductImg" onload="BBB.addPerfMark('ux-primary-content-displayed');" src="${scene7ImgURL}/${largeImage}" class="mainProductImage noImageFound" height="478" width="478" alt="${productName}" onClick="_gaq.push(['_trackEvent', 'zoom', 'hero image', '${refinedNameProduct}']);"/>
												<script>BBB.addPerfMark('ux-primary-content-displayed');</script>
                                            </c:when>
                                            <c:otherwise>	                                                                                        	
												<a id="mainProductImgZoom" data-zoomhref="${scene7ImgURL}/${basicImage}?hei=2000&wid=2000&qlt=50,1" href="javascript:void(0)">
                                               		<img itemprop="image" id="mainProductImg" onload="BBB.addPerfMark('ux-primary-content-displayed');" src="${scene7ImgURL}/${largeImage}" class="mainProductImage"  alt="${productName}" onClick="_gaq.push(['_trackEvent', 'zoom', 'hero image', '${refinedNameProduct}']);"/>
													<script>BBB.addPerfMark('ux-primary-content-displayed');</script>
	                                           	</a>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>

                            </div>

                <dsp:setvalue bean="Profile.categoryId" value="${rootCategory}"/>

				<div class="grid_6 omega productDetails">
					<dsp:form name="prodForm" method="post" id="prodForm">

					<div class="registryDataItemsWrap listDataItemsWrap<c:if test="${ (skuVO.customizableRequired || skuVO.customizationOffered) && not empty skuVO.customizableCodes}"> personalizedItem</c:if>" 
					data-refNum="<c:if test='${itemAlreadyPersonalized }'>${personalizedSku.refnum }</c:if>"  
					<c:if test="${itemAlreadyPersonalized }"> data-mainimg='${personalizedLargeImage }' data-thumbimg='${personalizedMediumImage }' data-pdetails="${personalizedSku.eximResponse.description }"
					data-poptions='${personalizedSku.eximResponse.customizationService }' data-pstatus='${personalizedSku.eximResponse.customizationStatus}' data-pprice='${personalizedSku.eximResponse.retailPriceAdder}'
					</c:if>>
                        <dsp:droplet name="BreadcrumbDroplet">
                            <dsp:oparam name="output">
                            <dsp:getvalueof var="output_circular" param="output_circular"/>
                            <dsp:getvalueof var="bts" param="bts"/>
                            <c:set var="btsValue" value="${bts}" scope="request"/>
                            <c:if test="${null ne  output_circular}" >
                            <c:set var="backToCircular">
                             <bbbl:label key='lbl_pdp_breadcrumb_circular' language="${pageContext.request.locale.language}" />
                            </c:set>
                            <dsp:a iclass="backToCircular" href="/store/browse/gadgets/circular.jsp?${output_circular}" title="${backToCircular}">
                           	${backToCircular}
                            </dsp:a>
                            </c:if>
                            </dsp:oparam>
                        </dsp:droplet>

						<%-- Start for Chat and Ask and Answer 2.1.1 --%>
                        <dsp:droplet name="ChatAskAndAnswerDroplet">
							<dsp:param name="productId" param="parentProductId" />
							<dsp:param name="categoryId" param="CategoryId" />
							<dsp:param name="siteId" value="${appid}" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="PDPAttributesVo" param="PDPAttributesVo"/>
								<c:set var="chatEnabled" value="${PDPAttributesVo.chatEnabled}" scope="request"/>
								<c:set var="askAndAnswerEnabled" value="${PDPAttributesVo.askAndAnswerEnabled}" scope="request"/>
								<c:set var="chatCode" value="${PDPAttributesVo.categoryId}" scope="request"/>
							</dsp:oparam>
						</dsp:droplet>
						<%-- End for Chat and Ask and Answer 2.1.1 --%>
						
                        <input type="hidden" class="addToCartSubmitData" value="${btsValue}" name="bts" />
						<div class="prodAttribs prodAttribWrapper">
						<c:set var="showShipCustomMsg" value="true"/>
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param name="array" value="${attribs}"/>
							<dsp:oparam name="output">
							<dsp:getvalueof var="placeHolderTop" param="key"/>
								<c:if test="${(not empty placeHolderTop) && (placeHolderTop eq AttributePDPTOP)}">
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
										<dsp:param param="element" name="array" />
										<dsp:param name="sortProperties" value="+priority"/>
											<dsp:oparam name="output">
											
											<dsp:getvalueof var="attrId" param="element.attributeName" />
															
																<c:if test="${fn:contains(shippingAttributesList,attrId)}">
																	<c:set var="showShipCustomMsg" value="false"/>
																</c:if>
											<dsp:getvalueof var="placeHolderTop" param="element.placeHolder"/>
											<dsp:getvalueof var="attributeDescripTop" param="element.attributeDescrip"/>
											<dsp:getvalueof var="imageURLTop" param="element.imageURL"/>
											<dsp:getvalueof var="actionURLTop" param="element.actionURL"/>
											<dsp:getvalueof var="attributeName" param="element.attributeName"/>
											<dsp:getvalueof var="actionURLPrice" param="element.actionURL"/>
											<div>
												<c:choose>
													<c:when test="${null ne attributeDescripTop}">
														<c:choose>
														    <c:when test="${null ne imageURLTop}">
														  		<a href="${actionURLTop}" class="newOrPopup"><img src="${imageURLTop}" alt="<dsp:valueof param="element.attributeDescrip" valueishtml="true"/>"/></a>
														    </c:when>
														    <c:otherwise>
													            <c:choose>
														            <c:when test="${null ne actionURLTop}">
														            	<c:choose>
																			<c:when test="${not empty vdcSkuId && isVdcSku && null ne attributeName && fn:contains(attributeName,vdcAttributesList)}">
																			 	<a href="/store/browse/dynamicStaticContent.jsp?pageName=ItemShippedDirectlyFromVendor&isVdcSku=${isVdcSku}&skuId=${vdcSkuId}" class="newOrPopup"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a>
																			</c:when>
																			<c:otherwise>
																			  <span class="rebateAttribs attribs ${sep}"><a href="${actionURLTop}" class="newOrPopup"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a></span>
																			</c:otherwise>
																		</c:choose>
														            </c:when>
														            <c:otherwise>
														            	<span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span>
														            </c:otherwise>
													            </c:choose>
														    </c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<c:if test="${null ne imageURLTop}">
															<c:choose>
																<c:when test="${null ne actionURLTop}">
																	<a href="${actionURLTop}" class="newOrPopup"><img src="${imageURLTop}" alt="Attribute Image" /></a>
																</c:when>
																<c:otherwise>
																	<img src="${imageURLTop}" alt="Attribute Image" />
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
							
							
							<jsp:useBean id="placeHolderMapThrsldVal" class="java.util.HashMap" scope="request"/>
							<c:set target="${placeHolderMapThrsldVal}" property="higherShipThreshhold" value="$49"/>
							
							<c:if test="${showShipCustomMsg && ShipMsgDisplayFlag}">
								<c:set var="freeShipingMsg">${displayMsg}</c:set>
							</c:if>
						</div>

						<c:choose>
							<c:when test="${empty thumbnailImage}">
								<img class="hidden productImage" height="83" width="83" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${productName}" />
							</c:when>
							<c:otherwise>
								<img class="hidden productImage noImageFound" height="83" width="83" src="${scene7Path}/${thumbnailImage}" alt="${productName}" />
							</c:otherwise>
						</c:choose>
						<dsp:getvalueof var="productName" param="productVO.name"/>
						<h1 id="productTitle" class="productDetails" itemprop="name"><dsp:valueof param="productVO.name" valueishtml="true"/></h1>
						<script>BBB.addPerfMark('ux-destination-verified');</script>
						<div class="bulletsReset productDesc">
							<div class="noprint" itemprop="description">
                                <dsp:valueof param="productVO.shortDescription" valueishtml="true"/>
                            </div>
                            <div class="hidden printProdDescription">
                                <dsp:valueof param="productVO.longDescription" valueishtml="true"/>
                                <dsp:getvalueof var="harmonLongDescription" param="productVO.harmonLongDescription"/>
                                <c:if test="${not empty harmonLongDescription}"><div class="harmonDesc"><dsp:valueof param="productVO.harmonLongDescription" valueishtml="true"/></div></c:if>
                                <div class="appendSKUInfo">
                                    <dsp:getvalueof var="childSKUId" param="pSKUDetailVO.skuId" />
                                    <c:if test="${not empty childSKUId}">
                                        <p class="smalltext prodSKU"><bbbl:label key='lbl_pdp_sku_id' language="${pageContext.request.locale.language}" /> ${childSKUId}</p>
                                    </c:if>
                                </div>
                            </div>
							<%-- R2.2 178-a1 story requirement.
							<a href="#" class="readMore"><bbbl:label key='lbl_see_full_details' language="${pageContext.request.locale.language}" /></a>  --%>
						</div>
						<script>BBB.addPerfMark('ux-secondary-content-displayed');</script>

                        <%--
						<div class="desclight">
                        </div>
                        --%>

						<c:set var="client_ID">
						<bbbc:config key="${appid}" configName="BBBLiveClicker" />
						</c:set>
						<c:set var="component_ID">
						<bbbc:config key="${client_ID}_WVLink" configName='BBBLiveClicker' />
						</c:set>
						<div class="productLinksWrapper rebateContainer prodAttribWrapper clearfix">
							<div class="pdpAttributeContainer fl clearfix">
								<dsp:getvalueof var="mediaVO" param="mediaVO"/>
								<c:choose>
								<c:when test="${null ne mediaVO}">
										<dsp:getvalueof var="mediaURL" param="mediaVO.mediaSource"/>
										<c:if test="${null ne mediaURL}">
										<div class="productLinks watchVideo clearfix"><div>
										<a href="${mediaURL}" onClick="javascript:rkg_micropixel('${currentSiteId}','video'); openmedia(); pdpOmnitureProxy('${parentProductId}', 'videoplay'); return true;" target="module35892976">
										<img src="/_assets/global/images/watch_video.png" alt="Watch Video" title="Watch Video" width="119" height="25" border="0"></a></div></div>
										</c:if>
								</c:when>
								<c:otherwise>
										<c:if test="${LiveClickerOn}">
										<div id="liveClickerWatchVideo" aria-label="Watch the video for ${productName}" class="productLinks watchVideo clearfix" data-parentProdId="${parentProductId}">
										<script id="tag" type="text/javascript" src="<bbbc:config key="liveclicker_pdp" configName="ThirdPartyURLs" />client_id=${client_ID}&component_id=${component_ID}&dim6=${parentProductId}&widget_id="></script>
										</div>
										</c:if>
								</c:otherwise>
								</c:choose>
								<c:if test="${not empty prodGuideId}">
									<dsp:droplet name="/atg/repository/seo/GuideItemLink">
										<dsp:param name="repositoryName" value="/com/bbb/cms/repository/GuidesTemplate" />
										<dsp:param name="itemDescriptor" value="guides" />
										<dsp:param name="id" param="productVO.shopGuideId"/>
										<dsp:oparam name="output">
											<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
										</dsp:oparam>
									</dsp:droplet>
									<div id="viewProductGuides" class="productLinks viewProductGuides clearfix" role="link">
										 <a href="${contextPath}${finalUrl}" onclick="pdpOmnitureProxy('${parentProductId}', 'prodguide')" title="View Buying Guide" class="productGuideImg"><bbbl:label key='lbl_view_Product_Guide' language="${pageContext.request.locale.language}" />
										 </a>
											</div>
								</c:if>
								<c:if test="${collection==true}">
									<c:if test="${isLeadSKU==true}">
									<dsp:getvalueof var="childProducts" param="collectionVO.childProducts"/>
										<c:if test="${not empty childProducts}">
										<div class="productLinks">
											<a class="lnkCollectionItems smoothScrollTo viewAccessories" data-smoothscroll-topoffset="65" href="#collectionItems"><bbbl:label key='lbl_pdp_view_accessories' language="${pageContext.request.locale.language}" /></a>
											<script>BBB.addPerfMark('ux-primary-action-available');</script>
										</div>
										</c:if>
									</c:if>
								</c:if>
							</div>
							<%-- Rebate Container --%>
								<dsp:getvalueof var="hasRebate" param="pSKUDetailVO.hasRebate"/>
								<c:set var="rebatesOn" value="${false}" />
								<c:if test="${not empty hasRebate && hasRebate}">
									<dsp:getvalueof var="chkEligibleRebates" param="pSKUDetailVO.eligibleRebates"/>
									<c:if test="${(null != chkEligibleRebates) && (fn:length(chkEligibleRebates) == 1 )}">
										<c:set var="rebatesOn" value="${true}" />
									</c:if>
								</c:if>
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param value="${attribs}" name="array" />
									<dsp:oparam name="output">
									<dsp:getvalueof var="placeHolderPrice" param="key"/>
										<c:if test="${(not empty placeHolderPrice) && (placeHolderPrice eq AttributePDPPrice)}">
											<dsp:droplet name="/atg/dynamo/droplet/ForEach">
												<dsp:param param="element" name="array" />
												<dsp:param name="sortProperties" value="+priority"/>
													<dsp:oparam name="output">
													<dsp:getvalueof var="chkCount" param="count"/>
													<dsp:getvalueof var="chkSize" param="size"/>
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
													<dsp:getvalueof var="placeHolderPrice" param="element.placeHolder"/>
													<dsp:getvalueof var="attributeDescripPrice" param="element.attributeDescrip"/>
													<dsp:getvalueof var="imageURLPrice" param="element.imageURL"/>
													<dsp:getvalueof var="actionURLPrice" param="element.actionURL"/>
													<dsp:getvalueof var="attributeName" param="element.attributeName"/>
													<c:choose>
														<c:when test="${null ne attributeDescripPrice}">
															<c:choose>
																<c:when test="${null ne imageURLPrice}">
																	<span class="rebateAttribs attribs ${sep}"><a href="${actionURLPrice}" class="newOrPopup"><img src="${imageURLPrice}" alt="<dsp:valueof param="element.attributeDescrip" valueishtml="true"/>"/></a></span>
																</c:when>
																<c:otherwise>
																	<c:choose>
																		<c:when test="${null ne actionURLPrice}">
																		<%-- START BPSI 1928 Retrieve VDC attributes to formulate logic to construct the message--%>
																		<c:choose>
																			<c:when test="${not empty vdcSkuId && isVdcSku && null ne attributeName && fn:contains(attributeName,vdcAttributesList)}">
																			  <span class="rebateAttribs attribs ${sep}"><a href="/store/browse/dynamicStaticContent.jsp?pageName=ItemShippedDirectlyFromVendor&isVdcSku=${isVdcSku}&skuId=${vdcSkuId}" class="newOrPopup"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a></span>
																			</c:when>
																			<c:otherwise>
																			<span class="rebateAttribs attribs ${sep}"><a href="${actionURLPrice}" class="newOrPopup"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a></span>
																			</c:otherwise>
																		</c:choose>
																		<%-- END BPSI 1928 Retrieve VDC attributes to formulate logic to construct the message--%>
																		</c:when>
																		<c:otherwise>
																			<span class="rebateAttribs attribs ${sep}"><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span>
																		</c:otherwise>
																	</c:choose>
																</c:otherwise>
															</c:choose>
														</c:when>
														<c:otherwise>
															<c:if test="${null ne imageURLPrice}">
																<c:choose>
																	<c:when test="${null ne actionURLPrice}">
																		<span class="rebateAttribs attribs ${sep}"><a href="${actionURLPrice}" class="newOrPopup"><img src="${imageURLPrice}" alt="Image For Attribute"/></a></span>
																	</c:when>
																	<c:otherwise>
																		<span class="rebateAttribs attribs ${sep}"><img src="${imageURLPrice}" alt="Image For Attribute" /></span>
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
									<dsp:getvalueof var="eligibleRebates" param="pSKUDetailVO.eligibleRebates"/>
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param name="array" value="${eligibleRebates}"/>
										<dsp:oparam name="output">
											<dsp:getvalueof var="chkCount1" param="count"/>
											<dsp:getvalueof var="chkSize1" param="size"/>
											<c:set var="sep1" value="seperator" />
											<c:if test="${chkCount1 == chkSize1}">
												<c:set var="sep1" value="" />
											</c:if>
											<dsp:getvalueof var="rebate" param="element"/>
											<span class="rebateAttribs attribs ${sep1}" ><a href="${rebate.rebateURL}" class="links" target="_blank" title="Rebate"><c:out value="${rebate.rebateDescription}" escapeXml="false"/></a></span>
										</dsp:oparam>
									</dsp:droplet>
									</c:if>
									<span class="attribs rebateAttribs<c:if test="${not skuVO.shippingRestricted or empty skuVO.shippingRestricted}"> hidden</c:if>">
		  								<a class="shippingRestrictionsApplied" href="/store/cart/static/shipping_restrictions_applied.jsp" data-skuId="${childSKUId}" title="<bbbl:label key="lbl_shipping_restrictions_applied" language="${pageContext.request.locale.language}"/>">
		  								<span><span class="prod-attrib"><bbbl:label key="lbl_shipping_restrictions_details" language="<c:out param='${language}'/>"/></span></span></a>
      								</span>
						</div>

						<%-- Part Collection --%>
						
						<c:if test="${not empty poc }">
							<div class="partCollection clearfix cb">
								<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
									<dsp:param name="id" param="poc" />
									<dsp:param name="itemDescriptorName" value="product" />
									<dsp:param name="repositoryName"
										value="/atg/commerce/catalog/ProductCatalog" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="collectionFinalUrl" vartype="java.lang.String"
											param="url" />
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
										<div class="collectionName">
											<table class="noMar" border="0">
												<tr>
													<td> <bbbl:label key='lbl_pdp_product_part_of' language="${pageContext.request.locale.language}" />
													&nbsp;<c:out value="${collectionProductName}" escapeXml="false"/>
													&nbsp;<bbbl:label key='lbl_pdp_product_part_of_collection' language="${pageContext.request.locale.language}" />
													</td>
												</tr>
											</table>
										</div>
									</dsp:a>
							</div>
						</c:if>

						<div class="ratings clearfix">
								<div id="prodRatings">
									<input type="hidden" name="prodId" value="${parentProductId}" />
									<input type="hidden" name="pdpUpdateReq" value="true" />
									<input type="hidden" name="userToken" value="${userTokenBVRR }" />
									<div class="clear"></div>
								<c:if test="${BazaarVoiceOn}">
						                               
	                   <!--  BBBJ-268 STARTS -->		
							<dsp:getvalueof var="pdpUrl" param="productVO.seoUrl"/>
							<dsp:getvalueof var="productImageUrl" value="http://${scene7Path}/${largeImage}"/>
							<dsp:getvalueof var="bazaarVoiceReviews" param="productVO.bvReviews"/>
							<dsp:getvalueof value="${bazaarVoiceReviews.totalReviewCount}" var="bvTotalReviewCount"/>
							<dsp:getvalueof var="reviewServer" value="http://reviews.bedbathandbeyond.com/2009-en_us/share.htm?"/>
								
							<c:set var="imageParam" value="image="/>
							<c:set var="robotParam" value="robot="/>
							<c:set var="siteParam" value="site="/>
							<c:set var="titleParam" value="title="/>
							<c:set var="urlParam" value="url="/>
							<c:set var="ampersand" value="&"/>
							<c:set var="siteContextPath" value="http://www.bedbathandbeyond.com"/>

							<c:set var="notRobot" value="no"/>
							<c:set var="twitterSiteName" value="Twitter"/>
							<c:set var="facebookSiteName" value="Facebook"/>
							<c:set var="pinterestSiteName" value="Pinterest"/>
                                
								<dsp:droplet name="/com/bbb/account/droplet/BBBURLEncodingDroplet">
									<dsp:param name="URL" value="${productImageUrl}"/>
									<dsp:oparam name="output">				 						
									  <dsp:getvalueof var="encodedImageUrl" param="encodedURL" scope="request"/>										
								    </dsp:oparam>
								</dsp:droplet>
								<dsp:droplet name="/com/bbb/account/droplet/BBBURLEncodingDroplet">
									<dsp:param name="URL" value="${productName}"/>
									<dsp:oparam name="output">			 						
									  <dsp:getvalueof var="encodedproductName" param="encodedURL" scope="request"/>									
								    </dsp:oparam>
								</dsp:droplet>
								<dsp:droplet name="/com/bbb/account/droplet/BBBURLEncodingDroplet">
									<dsp:param name="URL" value="${siteContextPath}${pageContext.request.contextPath}${pdpUrl}"/>
									<dsp:oparam name="output">				 						
									  <dsp:getvalueof var="encodedPdpUrl" param="encodedURL" scope="request"/>										
								    </dsp:oparam>
								</dsp:droplet>
										
								<dsp:getvalueof var="twitterShareURL" value="${reviewServer}${imageParam}${encodedImageUrl}${ampersand}${robotParam}${notRobot}${ampersand}${siteParam}${twitterSiteName}${ampersand}${titleParam}${encodedproductName}${ampersand}${urlParam}${encodedPdpUrl}"/>
                                <dsp:getvalueof var="facebookShareURL" value="${reviewServer}${imageParam}${encodedImageUrl}${ampersand}${robotParam}${notRobot}${ampersand}${siteParam}${facebookSiteName}${ampersand}${titleParam}${encodedproductName}${ampersand}${urlParam}${encodedPdpUrl}"/>
                                <dsp:getvalueof var="pinterestShareURL" value="${reviewServer}${imageParam}${encodedImageUrl}${ampersand}${robotParam}${notRobot}${ampersand}${siteParam}${pinterestSiteName}${ampersand}${titleParam}${encodedproductName}${ampersand}${urlParam}${encodedPdpUrl}"/>						
								<fmt:parseNumber var="bvAverageRatingCssInput" integerOnly="true" value="${bazaarVoiceReviews.averageOverallRating * 10}"/>
								<c:if test="${bazaarVoiceReviews.averageOverallRating == '0.0'}">
								  <c:set var="bvAverageRatingCssInput" value="00"/>
								</c:if>
						<div id="prodSummaryContainer" class="prodSummaryContainer">
							<c:choose>
								<c:when test="${bvTotalReviewCount < 1}">

								     <span><span aria-hidden='true' class="ratingTxt ratingsReviews prodReviews${bvAverageRatingCssInput}" ></span><span class="visuallyhidden" aria-hidden='false'>not yet rated</span></span>
									 <script>BBB.addPerfMark('ux-primary-content-displayed');</script>
								     <c:choose>
                                        <c:when test="${isTransient}">
                                        	<a href="${contextPath}/account/login.jsp?writeReview=true" aria-label="${writeFirstReview} ${lblAboutThe} ${productName}" class="reviewText" data-BVProductId="${parentProductId}" data-BVCampaignId="BV_RATING_SUMMARY_ZERO_REVIEWS" title="${lblGridWriteReviewLink}">${writeFirstReview}</a>                                                     
                                        </c:when>
                                        <c:otherwise>
                                          <a href="#" class="reviewText triggerBVsubmitReview" aria-label="${writeFirstReview} ${lblAboutThe} ${productName}" data-BVProductId="${parentProductId}" data-BVCampaignId="BV_RATING_SUMMARY_ZERO_REVIEWS" title="${lblGridWriteReviewLink}">${writeFirstReview}</a>                                           
                                        </c:otherwise>
                                      </c:choose>
									  <script>BBB.addPerfMark('ux-secondary-content-displayed');</script>
	                            </c:when>
                                  <c:otherwise>
                                   		<span class="ratingTxt ratingsReviews prodReviews${bvAverageRatingCssInput}" ><span class="visuallyhidden" aria-hidden='false'>${bazaarVoiceReviews.ratingsTitle} ${lblForThe} ${productName}</span></span>
										<script>BBB.addPerfMark('ux-primary-content-displayed');</script>

                                      	<c:choose>
											<c:when test="${bvTotalReviewCount == 1}">
												<a title='Read all reviews' href="#BVRRWidgetID" class="numberOfReviews" aria-label="${bvTotalReviewCount} ${lblReviewCount} ${lblForThe} ${productName}"> 
	                                                <span class="bvTotalReviewCountClass">${bvTotalReviewCount}</span>
	                                                <span class="bvSingleReview"> ${lblReviewCount}</span>
		                                        </a>
				                            </c:when>
		                                 	<c:when test="${bvTotalReviewCount > 1}">
		                                 		<a title='Read all reviews' href="#BVRRWidgetID" class="numberOfReviews" aria-label="${bvTotalReviewCount} ${lblReviewsCount} ${lblForThe} ${productName}">
	                                                <span class="bvTotalReviewCountClass">${bvTotalReviewCount}</span>
	                                                <span class="bvMultipleReviews">${lblReviewsCount}</span>
		                                        </a>
		                                 	</c:when>
		                                </c:choose>	
		                                <c:choose>
	                                         <c:when test="${isTransient}">
	                                         	<a href="${contextPath}/account/login.jsp?writeReview=true" aria-label="${lblGridWriteReviewLink} ${lblAboutThe} ${productName} class="reviewText" data-BVProductId="${parentProductId}" data-BVCampaignId="BV_RATING_SUMMARY_ZERO_REVIEWS" title="${lblGridWriteReviewLink}">${lblGridWriteReviewLink}</a>                                                     
	                                         </c:when>
	                                         <c:otherwise>
	                                           <a href="#" class="reviewText triggerBVsubmitReview" aria-label="${lblGridWriteReviewLink} ${lblAboutThe} ${productName} data-BVProductId="${parentProductId}" data-BVCampaignId="BV_RATING_SUMMARY_ZERO_REVIEWS" title="${lblGridWriteReviewLink}">${lblGridWriteReviewLink}</a>                                           
	                                         </c:otherwise>
                                       </c:choose>
                                
                                  </c:otherwise>
                                </c:choose>
                                
                                <div class="socialBookmarkingLinks">
                                     <div><bbbl:label key='LBL_SHARETHISPRODUCT' language="${pageContext.request.locale.language}" /></div>
                                          <dsp:getvalueof var="domainName" value="http://www.bedbathandbeyond.com"/>                                         
                                                <a href="${facebookShareURL}">
	                                                 <img width="20" height="20" src="/_assets/bazaarVoice/fb_icon.png" alt="Facebook" title="Add to Facebook">
                                                </a>

                                                <a onfocus="this.href=bvReplaceTokensInSocialURL(this.href);" onclick="this.href=bvReplaceTokensInSocialURL(this.href);window.open(this.href,'','left=0,top=0,width=795,height=700,toolbar=1,location=0,resizable=1,scrollbars=1'); return false;"
                                                   rel="nofollow" name="BV_TrackingTag_Rating_Summary_1_SocialBookmarkTwitter_3280103"
                                                   href="${twitterShareURL}">                                                 
                                                   <img width="20" height="20" src="/_assets/bazaarVoice/twitter_icon.png" alt="Twitter" title="Tweet this">
                                                </a>

                                                <a href="${pinterestShareURL}">
                                                 <img width="20" height="20" src="/_assets/bazaarVoice/pinterest_icon.png" alt="Pinterest" title="Pin It!">
                                                </a>
                               </div>
                            </div>
                           <!--  BBBJ-268 ENDS -->
                           
	                                <c:if test="${askAndAnswerEnabled}">
	                                	<dsp:include page="askAndAnswer.jsp">
	                                </dsp:include>
	                                </c:if>
								</c:if>
								</div>

							<div class="fbLikePrintMessage share">
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
                                    <div class="fb-like" data-href="${fbURL}" data-layout="button_count" data-send="false" data-width="90" data-show-faces="false"></div>
                                <!--<![endif]-->
                                </c:if>
								<%--<a href="/store/browse/print/product_details.jsp?productId=${parentProductId}" class="print" title="Print"></a>
								<a href="#" class="email" title="Email"></a>--%>
							</div>
							<c:set var="attributeCount" value="1" />
                            <%--
						<div class="rebates">

						</div>--%>
						</div>


						<dsp:getvalueof var="colorParam" param="color"/>
						<dsp:getvalueof var="sizeParam" param="size"/>
				<dsp:include src="subprod_detail.jsp" flush="true">
					<dsp:param name="productVO" param="productVO" />
					<dsp:param name="colorParam" param="color" />
					<dsp:param name="sizeParam" param="size" />
				</dsp:include>
				<dsp:getvalueof var="isMultiSku" param="isMultiSku" />

               <div class="productLinks moreProdAttribs PDPM">
               <dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:param value="${attribs}" name="array" />
				<dsp:oparam name="output">
				<dsp:getvalueof var="placeHolderMiddle" param="key"/>
					<c:if test="${(not empty placeHolderMiddle) && (placeHolderMiddle eq AttributePDPMiddle)}">
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param param="element" name="array" />
							<dsp:param name="sortProperties" value="+priority"/>
								<dsp:oparam name="output">
								<dsp:getvalueof var="placeHolderMiddle" param="element.placeHolder"/>
								<dsp:getvalueof var="attributeDescripMiddle" param="element.attributeDescrip"/>
								<dsp:getvalueof var="imageURLMiddle" param="element.imageURL"/>
								<dsp:getvalueof var="actionURLMiddle" param="element.actionURL"/>
								<dsp:getvalueof var="attributeName" param="element.attributeName"/>	
								<div>
									<c:choose>
										<c:when test="${null ne attributeDescripMiddle}">
											<c:choose>
												<c:when test="${null ne imageURLMiddle}">
													<a href="${actionURLMiddle}" class="newOrPopup"> <img src="${imageURLMiddle}" alt="<dsp:valueof param="element.attributeDescrip" valueishtml="true"/>"/></a>
												</c:when>
												<c:otherwise>
													<c:choose>
														<c:when test="${null ne actionURLMiddle}">
															<c:choose>
																<c:when test="${not empty vdcSkuId && isVdcSku && null ne attributeName && fn:contains(attributeName,vdcAttributesList)}">
																  <a href="/store/browse/dynamicStaticContent.jsp?pageName=ItemShippedDirectlyFromVendor&isVdcSku=${isVdcSku}&skuId=${vdcSkuId}" class="newOrPopup"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a>
																</c:when>
																<c:otherwise>
																  <span class="rebateAttribs attribs ${sep}"><a href="${actionURLMiddle}" class="newOrPopup"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a></span>
																</c:otherwise>
															</c:choose>
														</c:when>
														<c:otherwise>
															<span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span>
														</c:otherwise>
													</c:choose>
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<c:if test="${null ne imageURLMiddle}">
												<c:choose>
													<c:when test="${null ne actionURLMiddle}">
														<a href="${actionURLMiddle}" class="newOrPopup"><img src="${imageURLMiddle}" alt="Image for Attribute" /></a>
													</c:when>
													<c:otherwise>
														<img src="${imageURLMiddle}" alt="Image for Attribute" />
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
				<div class="clear"></div>
				
               
				<dsp:getvalueof var="skuinStock" param="inStock" />
				
				
				<dsp:getvalueof var="oosProdId" param="productId" />
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
				</c:if>
				<%--BPSI-1241 Katori Integration Story. Personalize button on PDP --%>				
				
				<dsp:include page="personalization_frag_pdp.jsp">
					<dsp:param name="skuVO" value="${skuVO}"/>
					<dsp:param name="productId" value="${productId}"/>
					<dsp:param name="isInternationalCustomer" value="${isInternationalCustomer}"/>
					<dsp:param name="enableKatoriFlag" value="${enableKatoriFlag}"/>
					<dsp:param name="pDefaultChildSku" value="${pDefaultChildSku}"/>
					<dsp:param name="itemAlreadyPersonalized" value="${itemAlreadyPersonalized}"/>
					<dsp:param name="vendorName" value="${vendorName}"/>					
				</dsp:include>

			<%-- END:: code changes for defect BPSI-1241 --%>
			<dsp:getvalueof var="firstChildSKU" param="pFirstChildSKU" />
			<c:if test="${not empty firstChildSKU}">
				<div id="firstproduct" style="display:none">
				LT_F_PRD_ID:=${parentProductId}
				LT_F_SKU_ID:=${firstChildSKU}
				</div>
			</c:if>
			<dsp:getvalueof var="showTab" param="tabLookUp"/>
			<%-- <dsp:getvalueof var="inStock" param="inStock"/> --%>
			<dsp:getvalueof var="salePriceRangeDescription" param="productVO.salePriceRangeDescription" />
			<c:choose>
			<c:when test="${showTab}">

						<%-- LTL-PDP related changes Nikhil Koul start --%>
						<c:if test="${currentSiteId ne BedBathCanadaSite}">
							<dsp:droplet name="GetApplicableLTLShippingMethodsDroplet">
								<dsp:param name="skuId" value="${skuVO.skuId}" />
								<dsp:param name="siteId" value="${appid}" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="shipMethodVOList" param="shipMethodVOList"/>
								</dsp:oparam>
							</dsp:droplet>
							
							<c:choose>
								<c:when test="${not empty skuVO && skuVO.ltlItem}">
										<input type="hidden" name="isLtlItem" value="${true}" />
										<%-- js is using this flag in some scenario --%>
										<input type="hidden" id="ltlFlag" name="ltlFlag" value="true"/>
								</c:when>
								<c:otherwise>
										<input type="hidden" name="isLtlItem" value="${false}" />
								</c:otherwise>
							</c:choose>
							
							<jsp:useBean id="placeHolderMapContext" class="java.util.HashMap" scope="request"/>
							<c:set target="${placeHolderMapContext}" property="contextPath" value="${contextPath}"/>
							<div id="ltlDeliveryMethodWrapper" class="clearfix hidden">
										<a href="<bbbt:textArea key="txt_static_deliveryinfo" placeHolderMap="${placeHolderMapContext}" language ="${pageContext.request.locale.language}"/>" class="fl popupShipping" role="link">
											<img class="marBottom_5" width="20" height="15" src="/_assets/global/images/LTL/truck.png" alt="Truck">
											<span><bbbl:label key='ltl_truck_delivery_options' language="${pageContext.request.locale.language}" /> :</span>
											<img class="quesMark marLeft_5" width="15" height="15" src="/_assets/global/images/LTL/quesMark.png" alt="Question Mark" />
										</a>
<%-- 										<a href="#" class="info"><img width="15" height="15" src="/_assets/global/images/LTL/quesMark.png" class="quesMark marLeft_5" alt="Question Mark"><span class="textLeft">Some text related to LTL tooltip will come here.</span></a> --%>
									<select data-ltlflag="true" aria-required="true" class="customSelectBox" id="selServiceLevel" name="selServiceLevel"> 
									<option value=""><bbbl:label key="lbl_pdp_sel_service" language="<c:out param='${language}'/>"/></option> 
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
										<dsp:param value="${shipMethodVOList}" name="array" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="shipMethodId" param="element.shipMethodId"/>
											<c:set var="itemSaved" value="false"/>
											<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								     					<dsp:param param="ltlDsl" name="array" />
								      					<dsp:oparam name="output">
									  						 <dsp:getvalueof var="ltlDslValue" param="element" />
									  						 <c:if test="${shipMethodId eq  ltlDslValue}"> 
									  						 	<c:set var="itemSaved" value="true"/>
									  						 </c:if>
									   					</dsp:oparam>
								   					</dsp:droplet>
											<dsp:getvalueof var="shipMethodName" param="element.shipMethodDescription"/>
											<dsp:getvalueof var="deliverySurchargeval" param="element.deliverySurcharge"/>
											<c:if test="${deliverySurchargeval gt 0}">
												<c:set var="deliverySurcharge"><dsp:valueof  converter="currency" param="element.deliverySurcharge" /></c:set>
											</c:if>
											<c:if test="${deliverySurchargeval eq 0}">
												<c:set var="deliverySurcharge">Free</c:set>
											</c:if>
											<c:choose>
												<c:when test="${shipMethodId eq sopt}">
													
												   <option data-saved="${itemSaved}" value="${shipMethodId}" selected="selected">${shipMethodName} - ${deliverySurcharge}</option>
												</c:when>
												<c:otherwise>
											<option data-saved="${itemSaved}" value="${shipMethodId}">${shipMethodName} - ${deliverySurcharge}</option>
												</c:otherwise>
											</c:choose>
										</dsp:oparam>
									</dsp:droplet>
									</select>
								
									<input type="hidden" value="" class="_prodSize addItemToRegis addItemToList" id="serviceLevelFlag" name="selServiceLevel"> 
									<a href="${contextPath}/static/EasyReturns" title="<bbbl:label key='lbl_return_policy_title' language="${pageContext.request.locale.language}" />" class="fr marTop_10 marLeft_10"><bbbl:label key='lbl_return_policy' language="${pageContext.request.locale.language}" /></a>
							</div>
						</c:if>
						<%-- LTL-PDP related changes Nikhil Koul end --%>
						
						<%-- Katori detail Wrapper--%>
					<c:set var="customizeCTACodes">
						<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
					</c:set>
					
							
							<div class="addToCartPopUp hidden">
								<span>
									<h3 class="modalTitle hidden"><span class="qtyInPopUp">&nbsp;</span>&nbsp;<bbbt:textArea key="txt_cart_items_count" language ="${pageContext.request.locale.language}"/></h3>
								</span>
								<div class="fl noMarBot clearfix">
								   <div class="fl button button_active">
										<input class="close-any-dialog" type="submit" name="CLOSE" value="CLOSE" role="button" aria-pressed="false" >
								   </div>
									<div class="fl bold marTop_5 marLeft_10">
										<a href="${contextPath}/cart/cart.jsp"><bbbl:label key='lbl_cart_personalization_view_manage' language="${pageContext.request.locale.language}" /></a>
									<div class="clear"></div>
									</div>
								</div>
								<div class="cb padTop_15 personalizeAddToCartModal">
								<c:choose>
									<c:when test="${not empty skuVO.customizableCodes && fn:contains(customizeCTACodes, skuVO.customizableCodes)}">
										<bbbt:textArea key="txt_cart_items_close_modal_customize" language ="${pageContext.request.locale.language}"/>
									</c:when>
									<c:otherwise>
										<bbbt:textArea key="txt_cart_items_close_modal" language ="${pageContext.request.locale.language}"/>
									</c:otherwise>
								</c:choose>
								</div>
							</div>

						<%-- Katori detail Wrapper ends--%>
						
						<dsp:include page="price_frag_pdp.jsp">
						<dsp:param name="productVO" param="productVO"/>
						<dsp:param name="isFromPDP" value="true"/>
						<dsp:param name="productId" param="productId"/>
						<dsp:param name="registryId" param="registryId"/>
						<dsp:param name="inStock" value="${inStock}"/>
						<dsp:param name="pDefaultChildSku" value="${pDefaultChildSku}"/>
						<dsp:param name="skuIdfromURL" value="${skuIdfromURL}"/>
						<dsp:param name="certonaPrice" value="${certonaPrice}"/>
						<dsp:param name="oosProdId" value="${oosProdId}"/>
						<dsp:param name="OutOfStockOn" value="${OutOfStockOn}"/>
						<dsp:param name="isInternationalCustomer" value="${isInternationalCustomer}"/>
						<dsp:param name="parentProductId" value="${parentProductId}"/>
						<dsp:param name="omniPrice" value="${omniPrice}"/>
						<dsp:param name="bts" value="${bts}"/>
						<dsp:param name="MapQuestOn" value="${MapQuestOn}"/>
						<dsp:param name="itemAlreadyPersonalized" value="${itemAlreadyPersonalized }" />
						<dsp:param name="personalizationComplete" value="${personalizedSku.personalizationComplete }" />
						<dsp:param name="personalizationType" value="${skuVO.personalizationType}" />
						<dsp:param name="freeShipingMsg" value="${freeShipingMsg}" />
						<dsp:param name="priceLabelCodeSKU" value="${skuVO.pricingLabelCode}" />						
						<dsp:param name="inCartFlagSKU" value="${skuVO.inCartFlag}" />
						<dsp:param name="porchPayLoadJson" value="${registryJsonResultString}" />
						</dsp:include>
						
					<%-- BPSI - 1940 DSK | VDC messaging - combine cart and PDP | offset message --%>
						<div class="clear"></div>
						
						<dsp:include src="../_includes/modules/personalizedPDPWrapper.jsp" >
							<dsp:param name="skuVO" value="${skuVO}"  />
							<dsp:param name="pDefaultChildSku" value="${pDefaultChildSku}"/>
							<dsp:param name="vendorName" value="${vendorName}"/>
						</dsp:include>
						

						<%--
						personalizationType: ${personalizationType};
						bopusAllowed: <dsp:valueof  param="pSKUDetailVO.bopusAllowed"/>
						
						<c:if test="${localStorePDP eq false}">
						<c:if test="${not bopusAllowed}">
						
						TODO
						check if product is porch enabled
						check configs
						disable for CANADA

						--%>
						<%-- need the droplet on when to include Porch code --%>
						<c:set var="porchEligible" value="${false}" />

						<%-- <c:set var="porchEligible" value="${true}" /> --%>
						<%-- --%>
						<dsp:getvalueof var="porchServiceTypeCode" param="productVO.porchServiceFamilyCodes[0]"/>
						 <c:if test="${not isInternationalCustomer}">
						<dsp:droplet name="/atg/dynamo/droplet/IsNull">
							<dsp:param name="value" value="${porchServiceTypeCode}"/>
							<dsp:oparam name="true">
							 
							</dsp:oparam>
							
							<dsp:oparam name="false">
								<dsp:droplet name="com/bbb/config/BBBPorchConfigDroplet">
									<dsp:param name="siteId" value="${currentSiteId}"/>
									<dsp:param name="pageName" value="PDPPage"/>
									<dsp:param name="inStock" value="${inStock}"/>
									<dsp:param name="registryId" param="registryId"/>
									<dsp:param name="productVO" param="productVO"/>
								 	<dsp:param name="profile" bean="Profile"/>
									<dsp:oparam name="PDPPorch">
										<c:set var="porchEligible" value="${true}" /> 		
										<div class="porchWrapper" id="porchPDP" style="visibility: hidden;">
										
										<div id="porch-widget-body" class="porch-widget" ></div>
										<div class="clear"></div>								

										<p class="porchDisclaimer" style="display: none;">
											<bbbl:label key="lbl_bbby_porch_service_disclaimer_pdp" language="<c:out param='${language}'/>"/>
										</p>										
										</div>
									</dsp:oparam>
								</dsp:droplet>
							
							</dsp:oparam>
						</dsp:droplet>	
						
						</c:if>						

						<dsp:droplet name="/com/bbb/account/droplet/BBBURLEncodingDroplet">
							<dsp:param name="URL" value="${pageContext.request.requestURL}?${pageContext.request.queryString}"/>
							<dsp:oparam name="output">
								<dsp:getvalueof var="queryString" param="queryString"/>
				            </dsp:oparam>
				         </dsp:droplet>


						<dsp:getvalueof var="offsetDateVDC" param="offsetDateVDC"/>
						 <c:set var="vdcOffsetFlag">
							<bbbc:config key="vdcOffsetFlag" configName="FlagDrivenFunctions" />
						</c:set>
						<dsp:droplet name="/atg/dynamo/droplet/IsNull">
					  		<dsp:param name="value" param="pSKUDetailVO"/>

							<dsp:oparam name="true">
							    <p class="grid_6 vdcOffsetMsg highlightRed bold hidden"></p>
							</dsp:oparam>
							<dsp:oparam name="false">
								<c:if test="${vdcOffsetFlag && not empty offsetDateVDC}">
										<jsp:useBean id="placeHolderMapServiceLevel" class="java.util.HashMap" scope="request" />
												<c:set target="${placeHolderMapServiceLevel}" property="actualOffSetDate" value="${offsetDateVDC}" />
												<c:if test="${not empty skuVO && !skuVO.ltlItem}">
													<p class="grid_6 vdcOffsetMsg highlightRed bold marTop_n7">
												 		<bbbt:textArea key="txt_vdc_offset_msg" placeHolderMap="${placeHolderMapServiceLevel}" language="${pageContext.request.locale.language}" />
												 	</p>
												 </c:if>
										</c:if>
							</dsp:oparam>
						</dsp:droplet>

					<%-- BPSI - 1940 DSK | VDC messaging - combine cart and PDP | offset message --%>
						
						<input type="hidden" name="isCustomizationRequired" value="${skuVO.customizableRequired}"/>
						<input type="hidden" name="personalizationType" value="${skuVO.personalizationType}"/>
						<input type="hidden" name="customizableCodes" value="${skuVO.customizableCodes}"/>
						<dsp:getvalueof var="bopusAllowed" param="pSKUDetailVO.bopusAllowed"/>
						<input type="hidden" name="bopusAllowed" value="${bopusAllowed}"/>
						<dsp:getvalueof var="vdcSku" param="pSKUDetailVO.vdcSku"/>
						<input type="hidden" name="vdcSku" value="${vdcSku}"/>
						<input type="hidden" name="itemAlreadyPersonalized" value="${itemAlreadyPersonalized}"/>
						<input type="hidden" name="customizationOffered" value="${skuVO.customizationOffered}"/>
                        <dsp:getvalueof var="registryIdValue" param="registryId"/>
						<div class="btnPD clearfix">
							<div class="fr">
							<div class="fl marginButtonFix" id="selectBoxRedesign">
								<dsp:include page="../addgiftregistry/add_item_gift_registry.jsp">
								<dsp:param name="ltlFlag" value="${not empty skuVO && skuVO.ltlItem}"/>
								<dsp:param name="isCustomizationRequired" value="${skuVO.customizableRequired}"/>
								<dsp:param name="disableCTA" value="${disableCTA}"/>
								<dsp:param name="itemAlreadyPersonalized" value="${itemAlreadyPersonalized}"/>
								<dsp:param name="ltlProductFlag" value="${isLtlProduct}"/>
								</dsp:include>
								<script>BBB.addPerfMark('ux-primary-action-available');</script>
								<input type="hidden" name="parentProdId" class="_prodId addItemToRegis addItemToList" value="${parentProductId}" />
							</div>


							<div class="fl addToCart" id="buttonRedesign">
								
								<c:choose>
								  	<c:when test="${(isInternationalCustomer && isIntlRestricted) || disableCTA}">
									<div class="button_disabled">
										<input class="button-Large btnSecondary" type="submit" name="btnAddToCart" value="Add to Cart" onclick="rkg_micropixel('${appid}','cart')" role="button" disabled="disabled" />
										<script>BBB.addPerfMark('ux-primary-action-available');</script>
									</div>
									</c:when>
									<c:otherwise>
									<div class="mainProductContainer <c:if test="${inStock!=true}">button_disabled</c:if>">
							             <!-- <div id="of" style="display:none" class="visuallyhidden">
                                         <span class="qtty">1</span> of </div>
                                         <div style="display:none" id="custAddCart" class="visuallyhidden">added to cart</div>  -->
										<input type="submit" name="btnAddToCart" value="Add to Cart" onclick="rkg_micropixel('${appid}','cart')" role="button" disabled="disabled" class='button-Large btnSecondary <c:if test="${inStock==true}">enableOnDOMReady</c:if>' />
										<script>BBB.addPerfMark('ux-primary-action-available');</script>
									</div>
									</c:otherwise>	
								</c:choose>
								
							</div>
							
							</div>
						</div>
						<div class="hidden appendSKUInfo">
							<c:if test="${not empty pDefaultChildSku}">
								<p class="smalltext prodSKU"><bbbl:label key='lbl_pdp_sku_id' language="${pageContext.request.locale.language}" /> ${pDefaultChildSku}</p>
							</c:if>
						</div>

						<%--  CR Changes  --%>
						<c:if test="${MapQuestOn && (appid eq BuyBuyBabySite)}">
                                <bbbt:textArea key="txt_pdp_pickupinstore" language ="${pageContext.request.locale.language}"/>
                        </c:if>

						<div class="message">
							<div id="addToCartErrorDiv" class="error"></div>
						</div>
						<c:if test="${isInternationalCustomer && isIntlRestricted}">
                                <div class="notAvailableIntShipMsg pdpIntShipMsg cb clearfix marBottom_10"><bbbl:label key='lbl_pdp_intl_restricted_message' language="${pageContext.request.locale.language}" /></div>
                        </c:if>
						<dsp:getvalueof var="brandId" param="productVO.productBrand.brandId"/>
						<dsp:getvalueof var="brandName" param="productVO.productBrand.brandName"/>
						<%-- R2.2 Story - SEO Friendly URL changes --%>
						<dsp:droplet name="BrandDetailDroplet">
							<dsp:param name="origBrandName" value="${brandName}"/>
							<dsp:oparam name="seooutput">
								<dsp:getvalueof var="seoUrl" param="seoUrl" />
								<c:set var="Keyword" value="${name}" scope="request" />
								<c:url var="seoUrl" value="${seoUrl}" scope="request" />
							</dsp:oparam>
						</dsp:droplet>
						<c:url var="brandUrl" value="${seoUrl}"/>
						 
						
						  <%-- US: DSK & MOB: Now accepting PayPal banner_ 83 BI |Sprint1(2.2.1)| [Start]--%>
						  <%--  Hide the paypal for internationalUser --%>
                        <%--  <c:if test="${paypalOn && not isInternationalCustomer}">		
						    <bbbt:textArea key="txt_pdp_paypal_banner"  placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/>
                         </c:if> --%>
						 <%-- END:: code changes for defect BPS-439 --%>
                         <%-- US: DSK & MOB: Now accepting PayPal banner_ 83 BI |Sprint1(2.2.1)| [End]--%>
						
						
						<div class="clearfix cb linkHoverFocus">
						<c:if test="${null ne brandId }">
						<div class="fl shopAll linkStyle" itemprop="brand" itemscope itemtype="http://schema.org/Brand">
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

							<c:if test="${not empty storeDetails && (favStoreStockStatus[skuId] ne 1 || isMultiSku)}">
								<dsp:include page="fav_store_frag_pdp.jsp">
									<dsp:param name="isMultiSku" value="${isMultiSku}" />
									<dsp:param name="skuId" value="${skuId}" />
									<dsp:param name="skuIdfromURL" value="${skuIdfromURL}" />
									<dsp:param name="storeDetails" value="${storeDetails}" />
									<dsp:param name="favStoreStockStatus" value="${favStoreStockStatus}" />
								</dsp:include>
							
							</c:if>
							<input type="hidden" id="ltlDsl" name="ltlDsl" value="${ltlDsl}">
                            <div class="fr addToList noMarLeft marginButtonFix">

								<c:choose>
								   	<c:when test="${disableSFL}">
								   	   <a tabindex="0" href="javascript:void(0)" class="disable_link" id="btnAddToList" aria-label="save for later" aria-disabled='true'>
			                                <div aria-hidden='true' class="saveForLaterIcon link <c:choose><c:when test="${isItemInWishlist eq 'true'}">filled</c:when><c:otherwise>empty</c:otherwise></c:choose>"></div>
			                                <span aria-hidden='true' class="btn-border" id="sflLink">SAVE FOR LATER</span>
		                               </a>
									</c:when>
									<c:otherwise>
									   <a tabindex="0" href="javascript:void(0)" id="btnAddToList"

										   	<c:choose>
										   		<c:when test="${isItemInWishlist eq 'true'}">aria-label="saved for later"</c:when>
										   		<c:otherwise>aria-label="save for later"</c:otherwise>
										   	</c:choose>>
			                                <div aria-hidden='true'  class="saveForLaterIcon link <c:choose><c:when test="${isItemInWishlist eq 'true'}">filled</c:when><c:otherwise>empty</c:otherwise></c:choose>"></div>
			                                <span aria-hidden='false' class="btn-border" id="sflLink">SAVE FOR LATER</span>
		                               </a>
									</c:otherwise>
								</c:choose>
							</div>                          
						</div>
					</c:when>
					<c:otherwise>
					<div class="grid_6 alpha">
						<div class="priceQuantity clearfix">
						<input type="hidden" value="true" id="isFromPDP">
						<dsp:getvalueof var="isdynamicPriceEligible" param="productVO.dynamicPricingProduct" />
						<dsp:getvalueof var="priceLabelCodeProd" param="productVO.priceLabelCode" />
						<dsp:getvalueof var="inCartFlagProd" param="productVO.inCartFlag" />
						<dsp:getvalueof var="priceRangeDescription" param="productVO.priceRangeDescription" />
						<dsp:getvalueof var="defaultPriceRange" param="productVO.defaultPriceRangeDescription" />
						<dsp:getvalueof var="salePriceRangeDescription" param="productVO.salePriceRangeDescription" />
						<dsp:getvalueof var="childSKUs" param="productVO.childSKUs" />
						<c:set var="format"><dsp:valueof param="productVO.defaultPriceRangeDescription"/></c:set>
						<c:choose>
							<c:when test="${not empty pDefaultChildSku}">
							<c:set var ="itempropClass">http://schema.org/Offer</c:set>
							</c:when>
							<c:when test="${fn:length(childSKUs) eq 1}">
							<c:set var ="itempropClass">http://schema.org/Offer</c:set>
							</c:when>
							<c:otherwise>
								<c:set var ="itempropClass">
									<c:choose>
										<c:when test="${fn:contains(format, '-')}">http://schema.org/AggregateOffer</c:when>
										<c:otherwise>http://schema.org/Offer</c:otherwise>
									</c:choose>
								</c:set>
							</c:otherwise>
						</c:choose>
					    <div itemprop="offers" itemscope itemtype="${itempropClass}">
							<div class="prodPrice">
									<dsp:include page="browse_price_frag.jsp">
						        		<dsp:param name="priceLabelCode" value="${priceLabelCodeProd}" />
										<dsp:param name="inCartFlag" value="${inCartFlagProd}" />
										<dsp:param name="salePrice" value="${salePriceRangeDescription}" />
										<dsp:param name="listPrice" value="${priceRangeDescription}" />
										<dsp:param name="defaultPriceRange" value="${defaultPriceRange}" />
										<dsp:param name="isdynamicPriceEligible" value="${isdynamicPriceEligible}" />
										<dsp:param name="isFromPDP" value="${true}" />
								    </dsp:include>    		
									<c:if test="${ShipMsgDisplayFlag && showShipCustomMsg}">
										<div class="freeShipBadge">${freeShipingMsg}</div>
									</c:if>
									<c:if test="${currentSiteId ne BedBathCanadaSite && not collection}">
										<span id="deliver"> <bbbl:label key='ltl_delivery_label' language="${pageContext.request.locale.language}" /></span>
									</c:if>
							   </div>
								<script>BBB.addPerfMark('ux-primary-content-displayed');</script>
						  </div>
					</div>

						<%-- R2.2 117-a1 story --%>
						<c:if test="${collection==true}">
							<c:if test="${isLeadSKU==false}">
								<div class="productLinks noMarTop clearfix marBottom_5">
									<div class="button button_active">
										<a class="lnkCollectionItems smoothScrollTo" data-smoothscroll-topoffset="65" href="#collectionItems"><bbbl:label key='lbl_pdp_show_collection' language="${pageContext.request.locale.language}" /></a>
									</div>
									<input type="submit" class="hidden" value=""/>
								</div>
								<c:if test="${isInternationalCustomer && isCollectionIntlRestricted}">
									<div class="notAvailableIntShipMsg collTopIntShipMsg cb clearfix marBottom_10">
										<bbbl:label key='lbl_pdp_collection_intl_restricted_message' language="${pageContext.request.locale.language}" />
									</div>
								</c:if>
							</c:if>
						</c:if>
						
                       <%-- US: DSK & MOB: Now accepting PayPal banner_ 83 BI |Sprint1(2.2.1)| [Start]--%>
                      	  <%-- Hide the paypal for internationalUser --%>
                        <%--  <c:if test="${paypalOn && not isInternationalCustomer}">		
						    <bbbt:textArea key="txt_pdp_paypal_banner"  placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/>
                         </c:if> --%>
                       <%-- US: DSK & MOB: Now accepting PayPal banner_ 83 BI |Sprint1(2.2.1)| [End]--%>
					<%-- Added to display Show All label  --%>
					<div class="clearfix cb">
						<dsp:getvalueof var="brandId" param="productVO.productBrand.brandId"/>
						<dsp:getvalueof var="brandName" param="productVO.productBrand.brandName"/>
						<%-- R2.2 Story - SEO Friendly URL changes --%>
						<dsp:droplet name="BrandDetailDroplet">
							<dsp:param name="origBrandName" value="${brandName}"/>
							<dsp:oparam name="seooutput">
								<dsp:getvalueof var="seoUrl" param="seoUrl" />
								<c:set var="Keyword" value="${name}" scope="request" />
								<c:url var="seoUrl" value="${seoUrl}" scope="request" />
							</dsp:oparam>
						</dsp:droplet>
						<c:url var="brandUrl" value="${seoUrl}"/>
						<c:if test="${null ne brandId }">
						<div class="fl shopAll linkStyle" itemprop="brand" itemscope itemtype="http://schema.org/Brand">
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
						</div>

						<%-- Added to display Show All label Ends --%>


					</div>
					</c:otherwise>
					</c:choose>  
					<c:choose>
						<c:when test="${appid eq BedBathUSSite}">
							<c:set var="tipsOn"><tpsw:switch tagName="TipsOn_US"/></c:set>
						</c:when>
						<c:when test="${appid eq BuyBuyBabySite}">
							<c:set var="tipsOn"><tpsw:switch tagName="TipsOn_baby"/></c:set>
						</c:when>
						<c:when test="${appid eq BedBathCanadaSite}">
							<c:set var="tipsOn"><tpsw:switch tagName="TipsOn_CA"/></c:set>
						</c:when>
					</c:choose>
					<c:if test="${tipsOn}">
						<dsp:droplet name="TargetingRandom">
						    <dsp:param bean="/atg/registry/RepositoryTargeters/TargeterPDPSecond" name="targeter"/>
							  <dsp:param name="howMany" value="1"/>
							  <dsp:oparam name="output">
							     <dsp:getvalueof var="tipsTxt" param="element.tipsTxt"/>
							     <dsp:getvalueof var="tipsTitle" param="element.tipsTitle"/>
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
					<c:set var="radius_default_us">
                   <bbbc:config key="radius_default_us" configName="MapQuestStoreType" />
                </c:set>
                <c:set var="radius_default_baby">
                    <bbbc:config key="radius_default_baby" configName="MapQuestStoreType" />
                </c:set>
                <c:set var="radius_default_ca">
                    <bbbc:config key="radius_default_ca" configName="MapQuestStoreType" />
                </c:set>
                <c:set var="radius_range_us">
                    <bbbc:config key="radius_range_us" configName="MapQuestStoreType" />
                </c:set>
                <c:set var="radius_range_baby">
                    <bbbc:config key="radius_range_baby" configName="MapQuestStoreType" />
                </c:set>
                <c:set var="radius_range_ca">
                    <bbbc:config key="radius_range_ca" configName="MapQuestStoreType" />
                </c:set>
                <c:set var="radius_range_type">
                    <bbbc:config key="radius_range_type" configName="MapQuestStoreType" />
                </c:set>
                <c:choose>
                    <c:when test="${currentSiteId eq 'BedBathUS'}">
                        <c:set var="radius_default_selected">${radius_default_us}</c:set>
                        <c:set var="radius_range">${radius_range_us}</c:set>
                    </c:when>
                    <c:when test="${currentSiteId eq 'BuyBuyBaby'}">
                        <c:set var="radius_default_selected">${radius_default_baby}</c:set>
                        <c:set var="radius_range">${radius_range_baby}</c:set>
                    </c:when>
                    <c:when test="${currentSiteId eq 'BedBathCanada'}">
                        <c:set var="radius_default_selected">${radius_default_ca}</c:set>
                        <c:set var="radius_range">${radius_range_ca}</c:set>
                    </c:when>
                    <c:otherwise>
                        <c:set var="radius_default_selected">${radius_default_us}</c:set>
                        <c:set var="radius_range">${radius_range_us}</c:set>
                    </c:otherwise>
                </c:choose>
                
                <c:set var="radiusCookie" value="${cookie['radiusStore'].value}"/>
                <c:choose>
                	<c:when test="${(radiusCookie eq null || empty radiusCookie)}">
                   		<input type="hidden" value="${radius_default_selected}" id="defaultRadius" name="defaultRadius">
                 	</c:when>
                	<c:otherwise>
                		<input type="hidden" value="${radiusCookie}" id="defaultRadius" name="defaultRadius">
                	</c:otherwise>
                </c:choose>
				
				
					<input type="hidden" name='isMultiSku' value="${isMultiSku}">
					<input type="hidden" name="isCollection" value="${collection}"/>
					<input type="hidden" name='isLeadSKU' value="${isLeadSKU}">

					<c:choose>
						<c:when test="${not empty isVdcSku && isVdcSku eq 'true'}">
							<input type="hidden" name="vdcSku" value="true" />
						</c:when>
						<c:otherwise>
							<input type="hidden" name="vdcSku" value="false" />
						</c:otherwise>
					</c:choose>

					<c:choose>
						<c:when test="${not empty isVdcSku && isVdcSku eq 'true' && !(skuVO.ltlItem) && !(skuVO.customizationOffered) && !(itemAlreadyPersonalized) && !(skuVO.customizableRequired)}">
							<c:set var="vdcPassed" value="true" />
						</c:when>
						<c:otherwise>
							<c:set var="vdcPassed" value="false" />
						</c:otherwise>
					</c:choose>
   
					<section class="storeDetails">
					</section>
					</dsp:form>
                    <c:if test="${CertonaOn}">
                    <input type="hidden"  id="oosCertonadispchk" value="${inStock && ((collection==true || everLivingProduct) || empty skuId || isLeadSKU !=true)}">
					<div class="oos_certona hidden">
						<div class="oos_certonaWrapper">
							<h2 class="noMar"><bbbl:label key='lbl_pdp_oos_similar_item' language="${pageContext.request.locale.language}" /></h2>
							<p class="bold noMar"><bbbl:label key='lbl_pdp_oos_similar_item_subtitle' language="${pageContext.request.locale.language}" /></p>
							<div class="marTop_10 loaderDiv" style="text-align:center">
								<img width="20" height="20" src="/_assets/global/images/widgets/small_loader.gif" alt="small loader">      
				</div>
							<div class="productContent marTop_20 clearfix hidden"></div>
						</div>
			</div>
					</c:if>
				</div>
			</div>
			<div class="grid_12 alpha omega fr">
				<div id="sa_s22_instagram" class="sa_s22_instagram_home"></div>
					<script type="text/javascript">
						var sa_page = "2",iOS8=navigator.userAgent.match(/iPad/i),
							sa_s22_product = '${parentProductId}';

						(function()
						{
							function sa_async_load()
							{
								var sa = document.createElement('script');
								sa.type = 'text/javascript';
								sa.async = true;
								sa.src = '${saSrc}';

								var sax = document.getElementsByTagName('script')[0];
								sax.parentNode.insertBefore(sa, sax);
							}
								if (window.attachEvent)
								{
									window.attachEvent('onload', sa_async_load);
								}
								else if(iOS8){
								window.addEventListener('load', sa_async_load(),false);
								}else{
								window.addEventListener('load', sa_async_load,false);
							    }
						}());
					</script>
			</div>

			<div id="prodExplore" class="grid_12" role="complementary">
			 <%-- <span class="fl"><bbbl:label key='lbl_pdp_product_explore' language="${pageContext.request.locale.language}" /></span> --%>
                <%-- Start for Chat and Ask and Answer 2.1.1 --%>
               	<c:if test="${chatEnabled}">
               	<h2 class="clearfix">
					<%-- <dsp:include page="/common/click2chatlink.jsp">
					    <dsp:param name="pageId" value="${chatCode}"/>
					    <dsp:param name="prodId" value="${parentProductId}"/>
					    <dsp:param name="divApplied" value="${true}"/>
						<dsp:param name="divClass" value="grid_3 omega fr"/>
					</dsp:include> --%>
				</h2>
				</c:if>
				<%-- End for Chat and Ask and Answer 2.1.1 --%>
				<div class="categoryProductTabs marTop_10">
					<ul class="categoryProductTabsLinks" role="tablist">
						<li id="prodViewDefault-tab1" role="tab"><div class="arrowSouth"></div><a title='<bbbl:label key='lbl_pdp_product_info' language="${pageContext.request.locale.language}" />' href="#prodExplore-tabs1" role="tab" class='productSpec' aria-controls="prodExplore-tabs1" aria-selected="true"><bbbl:label key='lbl_pdp_product_info' language="${pageContext.request.locale.language}" /></a></li>
						<c:set var="count" value="2" />
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param param="productTabs" name="array"/>
							<dsp:oparam name="output">

								<li  id="prodViewDefault-tab<c:out value='${count}'/>" role="tab"><div class="arrowSouth"></div><a title="<dsp:valueof param="element.tabName"/>" href="#prodExplore-tabs<c:out value='${count}'/>" role="tab" class='videoPlay' aria-controls="prodExplore-tabs<c:out value='${count}'/>" aria-selected="false"><dsp:valueof param="element.tabName"/></a></li>
								<c:set var="count" value="${count+1}" />

							</dsp:oparam>
						</dsp:droplet>

	                    <c:if test="${BazaarVoiceOn}">
	                    	<li id="prodViewDefault-tab<c:out value='${count}'/>" role="tab"><div class="arrowSouth"></div>
	                    	<a title="<bbbl:label key='lbl_pdp_product_ratings_reviews' language="${pageContext.request.locale.language}" />" href="#prodExplore-tabs<c:out value='${count}'/>" role="tab" class='ratingReviews' aria-controls="prodExplore-tabs<c:out value='${count}'/>" aria-selected="false"><bbbl:label key='lbl_pdp_product_ratings_reviews' language="${pageContext.request.locale.language}" /></a>
	                    	</li>
	                    	<%-- Start for Chat and Ask and Answer 2.1.1 --%>
							<c:if test="${askAndAnswerEnabled}">
								<li id="prodViewDefault-tab<c:out value='${count+1}'/>" role="tab"><div class="arrowSouth"></div>
			                    <a title="<bbbl:label key='lbl_pdp_product_ask_answer' language="${pageContext.request.locale.language}" />" href="#prodExplore-tabs<c:out value='${count+1}'/>" role="tab" class='askAndAnswer' aria-controls="prodExplore-tabs<c:out value='${count+1}'/>" aria-selected="false"><span><bbbl:label key='lbl_pdp_product_ask_answer' language="${pageContext.request.locale.language}" /></span><span class="aaBetaImg"></span><span class="txtOffScreen">beta</span><span class="clear"></span></a>
								</li>
								<c:set var="showQA" value="true" scope="request"/>
							</c:if>
				           	<%-- End for Chat and Ask and Answer 2.1.1 --%>
	                    </c:if>
					</ul>

					<div id="prodExplore-tabs1" role="tabpanel" aria-labelledby="prodViewDefault-tab1" class="categoryProductTabsData" aria-hidden="false">
						<div class="bulletsReset grid_6 alpha appendSKUInfo">
						<dsp:valueof param="productVO.longDescription" valueishtml="true"/>
						<c:if test="${not empty pDefaultChildSku}">
							<p class="smalltext prodSKU"><bbbl:label key='lbl_pdp_sku_id' language="${pageContext.request.locale.language}" /> ${pDefaultChildSku}</p>
						</c:if>
						<c:if test="${WarrantyOn and ! isInternationalCustomer }">
							<c:choose>
								<c:when test= "${everLivingProduct or (collection and !isLeadSKU) }">
									<%-- Don't show warrany place holder for everlivingPDP and collection --%>
								</c:when>
								<c:otherwise>
									<div id="warrPlaceHolder">
								<dsp:include page="../browse/frag/warranty_place_holder.jsp" >
									<dsp:param name="attribs" value="${attribs}"/>
										<dsp:param name="skuVO" value="${skuVO}"/>
								</dsp:include>
										</div>
								</c:otherwise>
							</c:choose>	 
							</c:if>
						</div>

                     <c:if test="${LiveClickerOn}">

						<div id="prodVideo" class="grid_6 omega" data-parentProdId="${parentProductId}">
							<input type="hidden" name="videoProdId" value="${parentProductId}" />
						</div>
						</c:if>
						<%-- R 2.2.1 || Harmon START --%>
							<dsp:getvalueof var="harmonLongDescription" param="productVO.harmonLongDescription"/>
                           	<c:if test="${not empty harmonLongDescription}">
                           		<div class="harmonDesc grid_12 noMar">
                           			<dsp:valueof param="productVO.harmonLongDescription" valueishtml="true"/>
                           		</div>
                           	</c:if>
                        <%-- R 2.2.1 || Harmon END   --%>

                        <div id="wc-power-page" class="grid_12 noMar"></div>
						<script type="text/javascript">acsReady(function(){Webcollage.loadContent(window._acssitecode, '${parentProductId}', {autoPlayAndStop: true});});</script>
					</div>
					<c:set var="count" value="2" />
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param param="productTabs" name="array"/>
						<dsp:oparam name="output">


						<div id="prodExplore-tabs<c:out value='${count}'/>" aria-hidden="true" aria-labelledby="prodViewDefault-tab<c:out value='${count}'/>" role="tabpanel" class="categoryProductTabsData"><dsp:valueof param="element.tabContent" valueishtml="true"/></div>
						<c:set var="count" value="${count+1}" />

						</dsp:oparam>
					</dsp:droplet>
					<c:if test="${BazaarVoiceOn}">
					<div id="prodExplore-tabs<c:out value='${count}'/>" aria-labelledby="prodViewDefault-tab<c:out value='${count}'/>" role="tabpanel" class="categoryProductTabsData clearfix divRatings" aria-hidden="true">
						<%--<bbbl:label key='lbl_pdp_product_rate_and_review' language="${pageContext.request.locale.language}" />--%>

						<div id="BVRRContainer">

							<c:if test="${SmartSEOOn}">
								<dsp:droplet name="/com/bbb/commerce/browse/droplet/BVReviewContentDroplet">
									<dsp:param name="productId" param="productId"/>
									<dsp:param name="pageURL" value="${pageURL}"/>
									<dsp:param name="content" value="RE"/>
									<dsp:oparam name="output">
										<dsp:valueof param="bvContent" valueishtml="true"/>
									</dsp:oparam>
								</dsp:droplet>
							</c:if>
						<img width="20" height="20" alt="Loader Image" src="/_assets/global/images/widgets/small_loader.gif" >
						</div>

					</div>
					</c:if>

				<%--  Start For BV Integration Container Tag 2.1 #216 --%>

				<c:if test="${showQA}">
					<div id="prodExplore-tabs<c:out value='${count+1}'/>" aria-labelledby="prodViewDefault-tab<c:out value='${count+1}'/>" role="tabpanel" class="categoryProductTabsData clearfix divQuestions" aria-hidden="true">
						<dsp:include page="../browse/frag/ask_and_answer.jsp" >
							<dsp:param name="productId" value="${parentProductId}"/>
							<dsp:param name="pageURL" value="${pageURL}"/>
						</dsp:include>
					</div>
					<c:set var="count" value="${count+1}" />
				 </c:if>
				 <%--  End For BV Integration Container Tag 2.1 #216 --%>
				</div>
			</div>
			</div>

			<c:choose>
            <c:when test="${collection==true}">
			<c:choose>
			<c:when test="${isLeadSKU==true}">
                <input type="hidden" id="omniPageTypeVar46" value="product detail page" name="omniPageTypeVar46" />
                <input type="hidden" id="productPage" value="accessoryPage" name="productPage"/>
			<dsp:getvalueof var="childProducts" param="collectionVO.childProducts"/>
			<c:set var="omniProp5" value="Product with Accessory"/>
				<div id="selectBoxCollectionAccesoriesRedesign" class="loadCollectionAccesories grid_12 clearfix"
                    data-ajax-url="${contextPath}/browse/accessoriesForms.jsp"
                    data-ajax-params-count="11"
                    data-ajax-param1-name="color" data-ajax-param1-value="${color}"
                    data-ajax-param2-name="parentProductId" data-ajax-param2-value="${parentProductId}"
                    data-ajax-param3-name="crossSellFlag" data-ajax-param3-value="true"
                    data-ajax-param4-name="desc" data-ajax-param4-value="Accessories (pdp)"
                    data-ajax-param5-name="registryId" data-ajax-param5-value="${registryId}"
                    data-ajax-param6-name="BazaarVoiceOn" data-ajax-param6-value="${BazaarVoiceOn}"
                    data-ajax-param7-name="MapQuestOn" data-ajax-param7-value="${MapQuestOn}"
                    data-ajax-param8-name="view" data-ajax-param8-value="${view}"
					data-ajax-param9-name="showAccessories" data-ajax-param9-value="true"
					data-ajax-param10-name="bts" data-ajax-param10-value="${bts}"
					data-ajax-param11-name="fromAjax" data-ajax-param11-value="true">
                </div>
			</c:when>
			<c:otherwise>
                <input type="hidden" id="omniPageTypeVar46" value="collection page" name="omniPageTypeVar46" />
                <input type="hidden" id="productPage" value="collectionPage" name="productPage"/>
				<div id="selectBoxCollectionAccesoriesRedesign" class="loadCollectionAccesories grid_12 clearfix"
                    data-ajax-url="${contextPath}/browse/collectionForms.jsp"
                    data-ajax-params-count="10"
                    data-ajax-param1-name="color" data-ajax-param1-value="${color}"
                    data-ajax-param2-name="parentProductId" data-ajax-param2-value="${parentProductId}"
                    data-ajax-param3-name="crossSellFlag" data-ajax-param3-value="true"
                    data-ajax-param4-name="desc" data-ajax-param4-value="Collection (pdp)"
                    data-ajax-param5-name="registryId" data-ajax-param5-value="${registryId}"
                    data-ajax-param6-name="BazaarVoiceOn" data-ajax-param6-value="${BazaarVoiceOn}"
                    data-ajax-param7-name="MapQuestOn" data-ajax-param7-value="${MapQuestOn}"
                    data-ajax-param8-name="view" data-ajax-param8-value="${view}"
                    data-ajax-param9-name="bts" data-ajax-param9-value="${bts}"
                    data-ajax-param10-name="fromAjax" data-ajax-param10-value="true">
                    >
                </div>
			<dsp:getvalueof var="childProducts" param="collectionVO.childProducts"/>
			<c:set var="omniProp5" value="Collection"/>
			</c:otherwise>
			</c:choose>
			</c:when>
            <c:otherwise>
                <input type="hidden" id="omniPageTypeVar46" value="product detail page" name="omniPageTypeVar46" />
            </c:otherwise>
            </c:choose>

			<dsp:droplet name="Switch">
				<dsp:param name="value" bean="Profile.transient"/>
				<dsp:oparam name="false">
					<dsp:getvalueof var="userId" bean="Profile.id"/>
				</dsp:oparam>
				<dsp:oparam name="true">
					<dsp:getvalueof var="userId" value=""/>
				</dsp:oparam>
			</dsp:droplet>
			<c:if test="${TagManOn}">
				<dsp:include page="/tagman/frag/productDetails_frag.jsp">
        			<dsp:param name="categoryPath" value="${categoryPath}"/>
        			<dsp:param name="pDefaultChildSku" value="${pDefaultChildSku}"/>
       			</dsp:include>
			</c:if>
			<input type="hidden" id="stockStatus" value="${inStock}" name="stockStatus" />
				<%--BBBSL-2541 - Converting certna to AJAX call --%>
				<div id="botCrossSell" class="clearfix loadAjaxContent" role="complementary" data-ajax-url="${contextPath}/browse/pdp_certona_slots.jsp" data-ajax-params-count="7" 
				data-ajax-param1-name="parentProductId" data-ajax-param1-value="${parentProductId}" data-ajax-param2-name="productId" data-ajax-param2-value="${productId}"
				data-ajax-param3-name="linkStringNonRecproduct" data-ajax-param3-value="${linkStringNonRecproduct}" data-ajax-param4-name="OutOfStockOn" data-ajax-param4-value="${OutOfStockOn} " data-ajax-param5-name="inStock" data-ajax-param5-value="${inStock}" data-ajax-param6-name="isMultiSku" data-ajax-param6-value="${isMultiSku}" data-ajax-param7-name="isLeadSKU" data-ajax-param7-value="${isLeadSKU}">
                	<div class="grid_12 clearfix"><img width="20" height="20" alt="small loader" src="/_assets/global/images/widgets/small_loader.gif" /></div>
                </div>
				
			<c:if test="${fetchRelatedCategories}">							
				<div id="relatedCategoriesPDP"
					data-ajax-url="${contextPath}/browse/frag/product_related_categories.jsp"
                    data-ajax-params-count="2"
                    data-ajax-param1-name="productId" data-ajax-param1-value="${productId}"
                    data-ajax-param2-name="siteId" data-ajax-param2-value="${currentSiteId}">
				</div>
			</c:if>
			
			<div class="sddExclusion visuallyhidden" aria-hidden="true" id="sddExclusion">
				<bbbt:textArea key="txt_sdd_more_details" language ="${pageContext.request.locale.language}"/>
			</div>
                <%--BBBSL-2541 - Converting certna to AJAX call - End --%>

		</div>
		<dsp:droplet name="ProductViewedDroplet">
			<dsp:param name="id" param="productId" />
		</dsp:droplet>

		</dsp:oparam>
		<dsp:oparam name="error">
		<c:set var="productNotfound" value="true"/>
		<div id="content" class="container_12 clearfix" role="main">
		<div class="grid_12 clearfix marTop_20">
			<h3><span class="error">
			<bbbl:label key='lbl_pdp_product_not_available' language="${pageContext.request.locale.language}" />
			</span></h3>
		</div>
		</div>
		</dsp:oparam>
	</dsp:droplet>
	</c:otherwise>
	</c:choose>
	<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
		<dsp:param name="id" value="${parentProductId}" />
		<dsp:param name="itemDescriptorName" value="product" />
		<dsp:param name="repositoryName"
			value="/atg/commerce/catalog/ProductCatalog" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
				param="url" />
		</dsp:oparam>
	</dsp:droplet>
	<c:import url="/selfservice/find_in_store.jsp" >
		<c:param name="enableStoreSelection" value="true"/>
		<c:param name="vdcPassed" value="${vdcPassed}" />
	</c:import>
	<c:import url="/_includes/modules/change_store_form.jsp" >
		<c:param name="action" value="${contextPath}${finalUrl}"/>
	</c:import>
		<%--  New version of view map/get directions --%>
	<c:import url="/selfservice/store/find_store_pdp.jsp" ></c:import>
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
<dsp:getvalueof var="addToList" param="addToList"/>
<dsp:getvalueof var="prodList" param="prodList"/>
<dsp:getvalueof var="showpopup" param="showpopup"/>
<dsp:getvalueof var="registryId" param="registryId"/>
<dsp:getvalueof var="registryName" param="registryName"/>
<dsp:getvalueof var="totQuantity" param="totQuantity"/>
<dsp:getvalueof id="omniList" value="${sessionScope.added}"/>
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
	   var price = document.getElementById('certonaPrice') ? document.getElementById('certonaPrice').value : "";
	   var totalPrice = (qty*price).toFixed(2);
	   var personalizationMessage ='${personalizationMessage}';
	   if(personalizationMessage.length>0){
	   	var finalOut= ';'+'${fn:escapeXml(param.productId)}'+';;;event38='+'${fn:escapeXml(param.qty)}'+'|event39='+totalPrice+';eVar30='+'${fn:escapeXml(param.skuId)}'+'|eVar54='+personalizationMessage;
	   }
	   else{
	   	var finalOut= ';'+'${fn:escapeXml(param.productId)}'+';;;event38='+'${fn:escapeXml(param.qty)}'+'|event39='+totalPrice+';eVar30='+'${fn:escapeXml(param.skuId)}';
	   }
		   additemtolist(finalOut);
}
</script>
<dsp:droplet name="/atg/dynamo/droplet/Switch">
	<dsp:param name="value" bean="/atg/userprofiling/Profile.transient"/>
	<dsp:oparam name="false">

			<c:set var="check" value="true" scope="session"/>

	</dsp:oparam>
</dsp:droplet>

<c:if test="${not empty collectionId_Omniture}">
    <c:set var="omni_prod" >${fn:substring(collectionId_Omniture,0,fn:length(collectionId_Omniture)-1)}</c:set>
</c:if>

<c:choose>
     <c:when test="${(fn:length(childSKUs) gt 1 ) && param.skuId ne null}">
            <c:set var="omni_prod" >${omni_prod};${parentProductId};;;;eVar30=${param.skuId}</c:set>
        </c:when>
        <c:otherwise> 
        	<c:if test="${empty collectionId_Omniture}">
			    <c:set var="omni_prod" >${omni_prod};${parentProductId}</c:set>			    
			</c:if>            
        </c:otherwise>
 </c:choose>
 <c:choose>
 <c:when test="${appIdCertona eq 'bedbathandbeyond01' || appIdCertona eq 'bedbathandbeyond03' || appIdCertona eq 'bedbathandbeyond05' || appIdCertona eq 'bedbathandbeyond06'}">
   <c:set var="onPageEvent" value="registrywedding_op"/>
 </c:when>
 <c:otherwise>
 <c:set var="onPageEvent" value="registrybaby_op"/>
 </c:otherwise>
</c:choose>

	<c:choose>
		<c:when test="${null ne addToList}">
		   <script type="text/javascript">
			   var resx = new Object();
			   resx.appid = "${appIdCertona}";
			   resx.event = "wishlist";
			   resx.pageid = "";
			   resx.links = "";
			   var prodList = "<c:out value="${prodList}"/>";
			   resx.itemid = prodList.replace(/[\[\s]/g,'').replace(/[,\]]/g,';');
		   </script>
		</c:when>
		<c:otherwise>
		  <script type="text/javascript">
		           var resx = new Object();
		           resx.appid = "${appIdCertona}";
		           resx.event = "product";
		           resx.itemid = '${linkStringNonRecproduct}';
		           resx.customerid = "${userId}";

		//In case of add to cart event, call from browse.js
		           function callCertonaResxRun(certonaString) {
						  resx.links = "";
		    			  resx.event = "shopping+cart";
		                  resx.itemid = certonaString;
		                  resx.pageid = "";
		                  
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
					    resx.links='';
					    resx.pageid = "${pageIdCertona}";
					    resx.customerid = "${userId}";

						resx.event = "op_Registry+" + cfRegistryName;
						resx.itemid = cfProdId;
						resx.qty = cfQty;
						resx.price = cfPrice;
						resx.transactionid = cfRegistryId;
						if (typeof certonaResx === 'object') { certonaResx.run();  }
					}
				   
				   function certonaAddRegModal(json) {
					    var cfProdId = '';
					    for (var i in json.addItemResults) {
					     cfProdId += json.addItemResults[i].prodId + ';';
				        }
						resx.event = "${onPageEvent}"; 
						resx.itemid = cfProdId; 
						resx.customerid = "${userId}";
						resx.pageid = ""; 
						resx.links = ""; 
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
					    resx.links='';
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

		<dsp:droplet name="/com/bbb/account/droplet/BBBConfigKeysDroplet">
		    <dsp:param name="configKey" value="DimNonDisplayConfig"/>
		    <dsp:oparam name="output">
		        <dsp:getvalueof var="configMap" param="configMap"/>
		    </dsp:oparam>
		</dsp:droplet>

		<dsp:droplet name="/com/bbb/account/droplet/BBBURLEncodingDroplet">
			<dsp:param name="URL" value="${pageContext.request.requestURL}?${pageContext.request.queryString}"/>
			<dsp:oparam name="output">
				<dsp:getvalueof var="queryString" param="queryString"/>
            </dsp:oparam>
         </dsp:droplet>
		<dsp:getvalueof var="productId" param="productId" />


		<c:forEach var="conMap" items="${configMap}">
		    <c:if test="${conMap.key eq appid}">
					<c:choose>
					<c:when test="${phantomCategory}">
					<c:set var="floatingNodes" value="Floating nodes" />
					<c:set var="prop2Var">${floatingNodes}>${rootCategoryName}</c:set>
					<c:set var="rkgCategoryNames" value="${floatingNodes},${rootCategoryName}"/>
					<c:if test="${!empty categoryNameL1}">
					<c:set var="prop3Var">${floatingNodes}>${rootCategoryName}>${categoryNameL1}</c:set>
					<c:set var="rkgCategoryNames" value="${floatingNodes},${rootCategoryName},${categoryNameL1}"/>
					</c:if>
					<c:set var="channel">${floatingNodes}</c:set>
					<c:if test="${fn:contains(queryString, 'fromCollege')}">
					   	<c:set var="rootCategoryName" value="College" />
						<c:set var="prop3Var"> ${rootCategoryName}>${rootCategoryName}>${refinedNameProduct}</c:set>
						<c:set var="rkgCategoryNames" value="${rootCategoryName},${refinedNameProduct}"/>
					</c:if>
					</c:when>
					<c:otherwise>
					<c:choose>
					<c:when test="${(clearanceCategory == rootCategory) || (whatsNewCategory==rootCategory) }">
					<c:set var="prop2Var">${rootCategoryName}>${rootCategoryName}</c:set>
					<c:set var="rkgCategoryNames" value="${rootCategoryName},${refinedNameProduct}"/>

					<c:set var="channel">${rootCategoryName}</c:set>
					<c:if test="${!empty categoryNameL1}">
						<c:set var="prop3Var">${rootCategoryName}>${rootCategoryName}>${categoryNameL1}</c:set>
						<c:set var="rkgCategoryNames" value="${rootCategoryName},${refinedNameProduct},${categoryNameL1}"/>
					</c:if>
					</c:when>
					<c:otherwise>
					<c:set var="queryString" value="${queryString}" />
					<c:choose>
					<c:when test="${!empty categoryNameL1}">
					<c:set var="prop2Var">${rootCategoryName}>${categoryNameL1}</c:set>
					<c:set var="rkgCategoryNames" value="${rootCategoryName},${categoryNameL1}"/>
					</c:when>
					<c:otherwise>
					<c:set var="prop2Var">${rootCategoryName}>${rootCategoryName}</c:set>
					<c:set var="rkgCategoryNames" value="${rootCategoryName}"/>
					</c:otherwise>
					</c:choose>
					<c:set var="channel">${rootCategoryName}</c:set>
					<c:if test="${!empty categoryNameL2}">
						<c:set var="prop3Var">${rootCategoryName}>${categoryNameL1}>${categoryNameL2}</c:set>
						<c:set var="rkgCategoryNames" value="${rootCategoryName},${categoryNameL1},${categoryNameL2}"/>
					</c:if>
					</c:otherwise>
					</c:choose>
					</c:otherwise>
					</c:choose>
				</c:if>
			</c:forEach>

				<%-- Setting omniture prop values end --%>

		<dsp:getvalueof var="searchKeyword" param="Keyword" />
		<c:choose>
		<c:when test="${empty searchKeyword}">
			<c:set var="prop9Var" value="" />
		</c:when>
		<c:otherwise>
			<c:set var="prop9Var" value="products"></c:set>
		</c:otherwise>
		</c:choose>
		
		
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
			var inStockProduct = '${inStock}';
			var evar1='${strategyName}';
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
					prop1: 'Error page',
					prop2: 'Error page',
					prop3: 'Error page',
					prop4: '',
					//events: 'event56',
					products: '<c:out value="${omni_prod}"/>'
				};
			} else if(everLivingProduct== 'true' ){
				BBB.productInfo = {
						pageNameIdentifier: 'productDetails',
						pageName: 'Product Details Page>'+omni_omniProdName,
						channel: 'EverLiving Product details page',
						prop1: 'EverLiving Product details page',
						prop2: 'EverLiving Product details page',
						prop3: 'EverLiving Product details page',
						prop4: '',
						evar1: evar1,
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
					evar1: evar1,
					/*prop9: omni_prop9Var,  prop9 is not expected on PDP ... BBBSL-2882*/
					events: event + event90,
					<c:if test="${enableKatoriFlag && not isInternationalCustomer}">
						<c:if test="${ ((skuVO.customizableRequired || skuVO.customizationOffered) && not empty skuVO.customizableCodes)}">		
							events: event+',event81'+event90,
						</c:if>
					</c:if>				
					products: '<c:out value="${omni_prod}"/>'
				};
			}

			   <c:choose>
				  <%-- defect 68 when viewing out stock items. event40 fires. It should be event17
				   when viewing out stock items. event40 fires. It should be event17 --%>
					    <c:when test="${inStock == false}">
						   BBB.productInfo.events = event + ',event17' + event90
						   <c:if test="${enableKatoriFlag && not isInternationalCustomer}">
								<c:if test="${ ((skuVO.customizableRequired || skuVO.customizationOffered) && not empty skuVO.customizableCodes)}">
									+',event81' + event90
								</c:if>
							</c:if>;
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

			<%-- This function now move to browse omniture_tracking.jsp
		   function pdpOmnitureProxy(productId, event) {
				   if (typeof pdpOmniture === 'function') { pdpOmniture(productId, event); }
		   }

			function openmedia(){

		   window.open('','module35892976','toolbar=no,location=no,status=no,menubar=no,scrollbars=yes,resizable=yes')
			} --%>

		</script>
		
     <script>
		
		$(function() {
	     var element_mswpTooltipSeenSS;
	
	    // MAIN PRODUCT //
	    $("#prodForm .addToCart").on({
	    mouseenter: function () {
	    	$(".mswpTooltipSS").remove();

			// If both color and size are not selected //
			if ($("#prodForm .colorPicker ul.swatches li").length && !$("#prodForm .colorPicker ul.swatches li").hasClass("selected") && $("#prodForm .sizePicker .dropDownWrapper a.selectedIndex").length && $("#prodForm .sizePicker .dropDownWrapper a.selectedIndex").attr("data-selected-value").length == 0) {
	        	$(this).parent(".fr").append("<div class='mswpTooltipSS'><p></p></div>");
				$(".mswpTooltipSS").fadeIn();
				$(".mswpTooltipSS p").text("Please choose a size and color");

				if(window.SS && SS.EventTrack){
			      SS.EventTrack.rp('element_mswpTooltipSeen');
			      element_mswpTooltipSeenSS = 1;
			    }
			}
			// If both finish and size are not selected //
			else if ($("#prodForm .finishPicker div.swatches a").length && !$("#prodForm .finishPicker div.swatches a").hasClass("selected") && $("#prodForm .sizePicker .dropDownWrapper a.selectedIndex").length && $("#prodForm .sizePicker .dropDownWrapper a.selectedIndex").attr("data-selected-value").length == 0) {
	        	$(this).parent(".fr").append("<div class='mswpTooltipSS'><p></p></div>");
				$(".mswpTooltipSS").fadeIn();
				$(".mswpTooltipSS p").text("Please choose a finish and size");

				if(window.SS && SS.EventTrack){
			      SS.EventTrack.rp('element_mswpTooltipSeen');
			      element_mswpTooltipSeenSS = 1;
			    }
			}
			// If color is not selected //
			else if ($("#prodForm .colorPicker ul.swatches li").length && !$("#prodForm .colorPicker ul.swatches li").hasClass("selected")) {
	        	$(this).parent(".fr").append("<div class='mswpTooltipSS'><p></p></div>");
				$(".mswpTooltipSS").fadeIn();
				$(".mswpTooltipSS p").text("Please choose a color");

				if(window.SS && SS.EventTrack){
			      SS.EventTrack.rp('element_mswpTooltipSeen');
			      element_mswpTooltipSeenSS = 1;
			    }
			}
			// If size is not selected //
			else if ($("#prodForm .sizePicker .dropDownWrapper a.selectedIndex").length && $("#prodForm .sizePicker .dropDownWrapper a.selectedIndex").attr("data-selected-value").length == 0) {
	        	$(this).parent(".fr").append("<div class='mswpTooltipSS'><p></p></div>");
				$(".mswpTooltipSS").fadeIn();
				$(".mswpTooltipSS p").text("Please choose a size");

				if(window.SS && SS.EventTrack){
			      SS.EventTrack.rp('element_mswpTooltipSeen');
			      element_mswpTooltipSeenSS = 1;
			    }
			}
			// If finish is not selected //
			else if ($("#prodForm .finishPicker div.swatches a").length && !$("#prodForm .finishPicker div.swatches a").hasClass("selected")) {
	        	$(this).parent(".fr").append("<div class='mswpTooltipSS'><p></p></div>");
				$(".mswpTooltipSS").fadeIn();
				$(".mswpTooltipSS p").text("Please choose a finish");

				if(window.SS && SS.EventTrack){
			      SS.EventTrack.rp('element_mswpTooltipSeen');
			      element_mswpTooltipSeenSS = 1;
			    }
			}
	    },
	    mouseleave: function () {
	        $(".mswpTooltipSS").fadeOut();
	    },
	    click: function () {
	    	var mswpCartUrlSS = new RegExp("^/store/cart/mini_cart[.a-zA-Z0-9]+");
			    	
	    	$( document ).ajaxSuccess(function( event, xhr, settings ) {
	    		var mswpAddCartSS = mswpCartUrlSS.test(settings.url);

				if (mswpAddCartSS) {
					if(window.SS && SS.EventTrack && element_mswpTooltipSeenSS == 1){
				      SS.EventTrack.rp('link_mswpATC');
				      element_mswpTooltipSeenSS = 0;
				    }
				}
			});
	    }
	});
});

</script>
	
			
		<%-- YourAmigo code starts  6/18/2013--%>

		<%--RKG Comparison Shopping tracking starts--%>
		<c:if test="${RKGOn}">
        <script type="text/javascript" src="<bbbc:config key="secure_merchand_ma2q_js" configName="ThirdPartyURLs" />"></script>
        </c:if>
		<%--RKG Comparison Shopping tracking ends--%>
		<%-- Start pdp Compare Metrics changes--%>
			

		<%-- End  pdp Compare Metrics changes--%>	

		<%-- Need to tell browse.js if the porch checkbox is preselected, 
			for in the case when trying to add a porch item to registry --%>
		<c:choose>
			<c:when test="${not empty registryJsonResultString}">
				<c:set var="isChecked" value="true" />
   			</c:when>
   			<c:otherwise>
   				<c:set var="isChecked" value="false" />
   			</c:otherwise>
   		</c:choose>

	 	<script type="text/javascript">

	 		var BBB = BBB || {};
	 		BBB.Porch = BBB.Porch || {};
	 		BBB.Porch.isChecked = ${isChecked};
	 	</script>

 		


		<c:if test="${porchEligible}">

			<c:set var="lbl_bbby_porch_service_learn_more">
            	<bbbl:label key='lbl_bbby_porch_service_learn_more' language="${pageContext.request.locale.language}" />
            </c:set>


		 	<script type="text/javascript">

							
				$(function() {					
					$.ajax({
					    url: "/store/browse/porch_zip_ajax.jsp",
					    dataType: "json",
					})
					.done(function(response) {
					    
						var zipcode = (response.hasOwnProperty("zipcode")) ? response.zipcode : '';			
					    var params = {
							fontFamily: " Arial, Helvetica, sans-serif",
							fontSize: "14px",
							fontColor: "#444",
							fontWeight: "normal",
							serviceFamilyCode: "${porchServiceTypeCode}", 
							partnerSku: "${productId}",
							productName: '${fn:escapeXml(productName)}',
							productUrl: "${contextPath}${pageURL}", 
							postalCode: zipcode,  
							learnMoreText: '${lbl_bbby_porch_service_learn_more}', 
							site: '${currentSiteId}',
							isChecked: ${isChecked}
						};

						bbbLazyLoader.js('/_assets/global/js/porch.js', function(){$('#porchPDP').BBBPorch(params)});
					})
					.fail(function() {
					    
					})
				});

			</script>
		</c:if>
           
	</jsp:body>
</bbb:pageContainer>

</dsp:page>
