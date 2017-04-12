<dsp:page>
    <dsp:importbean bean="/com/bbb/commerce/browse/droplet/EverLivingDetailsDroplet" />
    <dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet" />
    <dsp:importbean bean="/com/bbb/commerce/browse/droplet/EverLivingPDPDroplet" />
    <dsp:importbean bean="/com/bbb/selfservice/ChatAskAndAnswerDroplet" />
    <dsp:importbean bean="/com/bbb/commerce/browse/droplet/BreadcrumbDroplet" />
    <dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductViewedDroplet" />
    <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler" />
    <dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler" />
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
    <dsp:importbean bean="/com/bbb/profile/session/BBBSessionBean" />
    <dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:importbean bean="/atg/targeting/TargetingRandom" />
    <dsp:importbean bean="/atg/multisite/Site" />
    <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
    <dsp:importbean bean="/com/bbb/cms/droplet/PageTabsOrderingDroplet" />
    <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
    <dsp:importbean bean="/com/bbb/commerce/browse/droplet/CategoryLandingDroplet" />
    <dsp:importbean bean="/com/bbb/commerce/browse/droplet/BrandDetailDroplet" />
    <dsp:importbean bean="/atg/multisite/Site" />
    <c:set var="appid" value="BedBathUS" />
    <c:set var="BuyBuyBabySite">
        <bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
    </c:set>
    <dsp:getvalueof var="appid" bean="Site.id" />
    <c:set var="SchemeName" value="pdp_cav;pdp_fbw" />
    <dsp:getvalueof var="productId" param="productId" />
    <dsp:getvalueof var="parentProductId" param="productId" />
    <dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
        <dsp:param name="id" param="productId" />
        <dsp:param name="itemDescriptorName" value="product" />
        <dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
        <dsp:oparam name="output">
            <dsp:getvalueof var="pageURL" vartype="java.lang.String" param="url" />
        </dsp:oparam>
    </dsp:droplet>
    <c:set var="submitAddToRegistryBtn">
        <bbbl:label key='lbl_add_to_registry' language="${pageContext.request.locale.language}"></bbbl:label>
    </c:set>
    <dsp:droplet name="EverLivingDetailsDroplet">
        <dsp:param name="id" param="productId" />
        <dsp:param name="siteId" value="${appid}" />
        <dsp:param name="skuId" param="skuId" />
        <dsp:param name="color" value="${color}" />
		<dsp:param name="checkInventory" value="false"/>
        <dsp:oparam name="output">
            <dsp:getvalueof var="skuVO" param="pSKUDetailVO" />
            <dsp:getvalueof var="categoryId" param="categoryId" />
            <dsp:getvalueof var="giftFlag" param="productVO.giftCertProduct" />
            <c:set var="giftFlag" scope="request" value="${giftFlag}" />
            <c:choose>
                <c:when test="${not empty skuVO}">
                    <dsp:getvalueof var="attribs" param="pSKUDetailVO.skuAttributes" />
                </c:when>
                <c:otherwise>
                    <dsp:getvalueof var="attribs" param="productVO.attributesList" />
                </c:otherwise>
            </c:choose>
            <div id="content" class="container_12 clearfixEverLiving" role="main">
                <div class="grid_12 row show-for-large-up">
                    <div class="grid_11 alpha left small-11 columns">
                        <ul class="tbs-crumbs">
                            <dsp:include page="breadcrumb.jsp">
                                <dsp:param name="siteId" value="${appid}" />
                            </dsp:include>
                        </ul>
                    </div>
                    <div class="grid_1 omega fr share">
                        <a href="/store/browse/print/everLivingPDP_print.jsp?productId=${productId}" class="print" title="Print"></a>
                        <a href="#" class="email" title="Email"></a>
                    </div>
                </div>
                <dsp:getvalueof var="isLeadSKU" param="collectionVO.leadSKU" />
                <dsp:getvalueof var="collection" param="productVO.collection" />
                <dsp:getvalueof var="linkStringNonRecproduct" param="linkStringNonRecproduct" />
                <dsp:getvalueof var="rkgProductList" param="rkgCollectionProdIds" />
                <dsp:getvalueof id="showFlag" idtype="java.lang.String" param="showFlag" />
                <dsp:getvalueof var="prodGuideId" param="productVO.shopGuideId" />
                <div itemscope itemtype="//schema.org/Product">
                    <div class="row grid_12 marTop_10 <c:if test=" ${showFlag=='1' } ">hasProdNextCollection</c:if>">
                        <dsp:getvalueof var="refinedNameProduct" param="productVO.refinedName" scope="request" />
                        <c:set var="warningHeader">
                            <bbbl:label key='lbl_everLivingPDP_warning_header' language="${pageContext.request.locale.language}" />
                        </c:set>
                        <c:set var="warningDetails">
                            <bbbt:textArea key='lbl_everLivingPDP_warning_details' language="${pageContext.request.locale.language}" />
                        </c:set>
                        <div class="grid_6 alpha zoomProduct small-12 medium-6 columns">
                            <c:if test="${showFlag == '1'}">
                                <dsp:include page="/bbcollege/next_college_collection.jsp" />
                            </c:if>
                            <dsp:getvalueof var="largeImage" param="productVO.productImages.largeImage" />
                            <dsp:getvalueof var="smallImage" param="productVO.productImages.smallImage" />
                            <dsp:getvalueof var="thumbnailImage" param="productVO.productImages.thumbnailImage" />
                            <dsp:getvalueof var="basicImage" param="productVO.productImages.basicImage" />
                            <dsp:getvalueof var="productName" param="productVO.name" scope="request" />
                            <dsp:getvalueof var="zoomFlag" param="productVO.zoomAvailable" />
                            <dsp:getvalueof var="refinedNameProduct" param="productVO.refinedName" scope="request" />
                            <div id="s7ProductImageWrapper" <c:if test="${zoomFlag && SceneSevenOn && (not empty largeImage)}"> class="bbbS7ImageWrapper" data-s7ImgsetID="${largeImage} " </c:if> data-parentProdId="${parentProductId}">
                                <!-- <div class="bbbS7Main "> -->
                                <!--       <div id="s7ProductImageWrapperInner" class="easyzoom easyzoom--adjacent "> -->
                                <c:choose>
                                    <c:when test="${empty largeImage || !SceneSevenOn}">
                                        <img id="mainProductImg" src="${imagePath}/_assets/global/images/no_image_available.jpg" class="mainProductImage noImageFound" height="380" width="380" alt="${productName}" onClick="_gaq.push(['_trackEvent', 'zoom', 'hero image', '${refinedNameProduct}']);" />
                                    </c:when>
                                    <c:when test="${!zoomFlag && SceneSevenOn && (not empty largeImage)}">
                                        <img id="mainProductImg" src="${scene7Path}/${largeImage}" class="mainProductImage noImageFound" height="380" width="380" alt="${productName}" onClick="_gaq.push(['_trackEvent', 'zoom', 'hero image', '${refinedNameProduct}']);" />
                                    </c:when>
                                    <c:otherwise>
                                        <a id="mainProductImgZoom" data-zoomhref="${scene7Path}/${basicImage}?hei=2000&wid=2000&qlt=50,1" href="javascript:void(0)">
                                            <img id="mainProductImg" src="${scene7Path}/${largeImage}" class="mainProductImage" alt="${productName}" onClick="_gaq.push(['_trackEvent', 'zoom', 'hero image', '${refinedNameProduct}']);" />
                                        </a>
                                    </c:otherwise>
                                </c:choose>
                                <!--  </div>
                                </div> -->
                            </div>
                        </div>
                        <dsp:setvalue bean="Profile.categoryId" value="${rootCategory}" />
                        <div class="grid_6 omega small-12 medium-6 columns product-info productDetails">
                            <div class="alert  alert-alert-everliving small-12 columns no-padding">
                                <br />
                                <c:choose>
                                    <c:when test="${(appid eq BuyBuyBabySite)}">
                                        <span>"${refinedNameProduct }"
									${warningHeader}</span>
                                        <br />
                                    </c:when>
                                    <c:otherwise>
                                        <span>"${refinedNameProduct }"
									${warningHeader}</span>
                                        <br />
                                    </c:otherwise>
                                </c:choose>
                                <span>${warningDetails}</span>
                                <br />
                                <br />
                            </div>
                            <div id="row registryDataItemsWrap">
                                <dsp:form name="prodForm" method="post" id="prodForm">
                                    <div class="registryDataItemsWrap listDataItemsWrap" style="padding-bottom:10px;">
                                        <dsp:droplet name="BreadcrumbDroplet">
                                            <dsp:oparam name="output">
                                                <dsp:getvalueof var="output_circular" param="output_circular" />
                                                <dsp:getvalueof var="bts" param="bts" />
                                                <c:set var="btsValue" value="${bts}" scope="request" />
                                                <c:if test="${null ne  output_circular}">
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
                                            <%-- <dsp:droplet name="ChatAskAndAnswerDroplet">
							<dsp:param name="productId" param="parentProductId" />
							<dsp:param name="categoryId" value="${categoryId}" />
							<dsp:param name="siteId" value="${appid}" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="PDPAttributesVo" param="PDPAttributesVo"/>
								<c:set var="chatEnabled" value="${PDPAttributesVo.chatEnabled}" scope="request"/>
								<c:set var="askAndAnswerEnabled" value="${PDPAttributesVo.askAndAnswerEnabled}" scope="request"/>
								<c:set var="chatCode" value="${PDPAttributesVo.categoryId}" scope="request"/>
							</dsp:oparam>
						</dsp:droplet> --%>
                                                <%-- End for Chat and Ask and Answer 2.1.1 --%>
                                                    <input type="hidden" class="addToCartSubmitData" value="${btsValue}" name="bts" />
                                                    <div class="prodAttribs prodAttribWrapper">
                                                        <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                                                            <dsp:param name="array" value="${attribs}" />
                                                            <dsp:oparam name="output">
                                                                <dsp:getvalueof var="placeHolderTop" param="key" />
                                                                <c:if test="${(not empty placeHolderTop) && (placeHolderTop eq AttributePDPTOP)}">
                                                                    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                                                                        <dsp:param param="element" name="array" />
                                                                        <dsp:param name="sortProperties" value="+priority" />
                                                                        <dsp:oparam name="output">
                                                                            <dsp:getvalueof var="placeHolderTop" param="element.placeHolder" />
                                                                            <dsp:getvalueof var="attributeDescripTop" param="element.attributeDescrip" />
                                                                            <dsp:getvalueof var="imageURLTop" param="element.imageURL" />
                                                                            <dsp:getvalueof var="actionURLTop" param="element.actionURL" />
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
                                                                                                        <a href="${actionURLTop}" class="newOrPopup"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a>
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
                                                    <h1 id="productTitle" class="productDetails" itemprop="name"><dsp:valueof param="productVO.name" valueishtml="true"/></h1>
                                                    <div class="bulletsReset productDesc" itemprop="description">
                                                        <dsp:valueof param="productVO.shortDescription" valueishtml="true" />
                                                        <%-- R2.2 178-a1 story requirement.
							<a href="#" class="readMore"><bbbl:label key='lbl_see_full_details' language="${pageContext.request.locale.language}" /></a>  --%>
                                                    </div>
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
                                                        <div class="productLinksWrapper rebateContainer prodAttribWrapper clearfix small-12 columns product-other-links">
                                                            <div class="pdpAttributeContainer fl clearfix">
                                                                <dsp:getvalueof var="mediaVO" param="mediaVO" />
                                                                <c:choose>
                                                                    <c:when test="${null ne mediaVO}">
                                                                        <dsp:getvalueof var="mediaURL" param="mediaVO.mediaSource" />
                                                                        <c:if test="${null ne mediaURL}">
                                                                            <div class="productLinks watchVideo clearfix">
                                                                                <div>
                                                                                    <a href="${mediaURL}" onClick="javascript:rkg_micropixel('${currentSiteId}','video'); openmedia(); pdpOmnitureProxy('${parentProductId}', 'videoplay'); return true;" target="module35892976">
                                                                                        <img src="/_assets/global/images/watch_video.png" alt="Watch Video" title="Watch Video" width="119" height="25" border="0"></a>
                                                                                </div>
                                                                            </div>
                                                                        </c:if>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <c:if test="${LiveClickerOn}">
                                                                            <span id="liveClickerWatchVideo" class="productLinks watchVideo" data-parentProdId="${parentProductId}">
										<script id="tag" type="text/javascript" src="<bbbc:config key="liveclicker_pdp" configName="ThirdPartyURLs" />client_id=${client_ID}&component_id=${component_ID}&dim6=${parentProductId}&widget_id="></script>
										</span>
                                                                        </c:if>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                                <c:if test="${not empty prodGuideId}">
                                                                    <dsp:droplet name="/atg/repository/seo/GuideItemLink">
                                                                        <dsp:param name="repositoryName" value="/com/bbb/cms/repository/GuidesTemplate" />
                                                                        <dsp:param name="itemDescriptor" value="guides" />
                                                                        <dsp:param name="id" param="productVO.shopGuideId" />
                                                                        <dsp:oparam name="output">
                                                                            <dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
                                                                        </dsp:oparam>
                                                                    </dsp:droplet>
                                                                    <span id="viewProductGuides" class="productLinks viewProductGuides pdp-sprite product-guide">
										 <a href="${contextPath}${finalUrl}" class="productGuideImg"><bbbl:label key="lbl_view_Product_Guide" language="${pageContext.request.locale.language}" />
										 </a>
									</span>
                                                                </c:if>
                                                                <c:if test="${collection==true}">
                                                                    <c:if test="${isLeadSKU==true}">
                                                                        <span class="productLinks">
											<a class="lnkCollectionItems viewAccessories" data-smoothscroll-topoffset="65" href="#collectionItems"><bbbl:label key='lbl_pdp_view_accessories' language="${pageContext.request.locale.language}" /></a>
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
                                                                <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                                                                    <dsp:param value="${attribs}" name="array" />
                                                                    <dsp:oparam name="output">
                                                                        <dsp:getvalueof var="placeHolderPrice" param="key" />
                                                                        <c:if test="${(not empty placeHolderPrice) && (placeHolderPrice eq AttributePDPPrice)}">
                                                                            <dsp:droplet name="/atg/dynamo/droplet/ForEach">
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
                                                                                    <c:choose>
                                                                                        <c:when test="${null ne attributeDescripPrice}">
                                                                                            <c:choose>
                                                                                                <c:when test="${null ne imageURLPrice}">
                                                                                                    <span class="rebateAttribs attribs ${sep}"><a href="${actionURLPrice}" class="newOrPopup"><img src="${imageURLPrice}" alt="<dsp:valueof param="element.attributeDescrip" valueishtml="true"/>"/></a></span>
                                                                                                </c:when>
                                                                                                <c:otherwise>
                                                                                                    <c:choose>
                                                                                                        <c:when test="${null ne actionURLPrice}">
                                                                                                            <span class="rebateAttribs attribs ${sep}"><a href="${actionURLPrice}" class="newOrPopup"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a>
                                                                                                            </span>
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
                                                                                                        <span class="rebateAttribs attribs ${sep}"><a href="${actionURLPrice}" class="newOrPopup"><img src="${imageURLPrice}" alt=""/></a></span>
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
                                                                    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                                                                        <dsp:param name="array" value="${eligibleRebates}" />
                                                                        <dsp:oparam name="output">
                                                                            <dsp:getvalueof var="chkCount1" param="count" />
                                                                            <dsp:getvalueof var="chkSize1" param="size" />
                                                                            <c:set var="sep1" value="seperator" />
                                                                            <c:if test="${chkCount1 == chkSize1}">
                                                                                <c:set var="sep1" value="" />
                                                                            </c:if>
                                                                            <dsp:getvalueof var="rebate" param="element" />
                                                                            <span class="rebateAttribs attribs ${sep1}"><a href="${rebate.rebateURL}" class="links" target="_blank" title="Rebate"><c:out value="${rebate.rebateDescription}" escapeXml="false"/></a></span>
                                                                        </dsp:oparam>
                                                                    </dsp:droplet>
                                                                </c:if>
                                                        </div>
                                                        <%-- Part Collection --%>
                                                            <c:if test="${not empty poc }">
                                                                <div class="partCollection clearfix cb">
                                                                    <dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
                                                                        <dsp:param name="id" param="poc" />
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
                                                                        <div class="collectionName">
                                                                            <table class="noMar" border="0">
                                                                                <tr>
                                                                                    <td>
                                                                                        <bbbl:label key='lbl_pdp_product_part_of' language="${pageContext.request.locale.language}" /> &nbsp;
                                                                                        <c:out value="${collectionProductName}" escapeXml="false" /> &nbsp;
                                                                                        <bbbl:label key='lbl_pdp_product_part_of_collection' language="${pageContext.request.locale.language}" />
                                                                                    </td>
                                                                                </tr>
                                                                            </table>
                                                                        </div>
                                                                    </dsp:a>
                                                                </div>
                                                            </c:if>
                                                            <div class="ratings clearfix small-12 columns product-reviews">
                                                                <div id="prodRatings">
                                                                    <input type="hidden" name="prodId" value="${parentProductId}" />
                                                                    <input type="hidden" name="userToken" value="${userTokenBVRR }" />
                                                                    <div class="clear"></div>
                                                                    <c:if test="${BazaarVoiceOn}">
                                                                        <div id="BVRRSummaryContainer">
                                                                            <img width="20" height="20" alt="" src="/_assets/global/images/widgets/small_loader.gif">
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
                                                                    <%--<a href="/store/browse/print/product_details.jsp?productId=${parentProductId}" class="print" title="Print"></a>
								<a href="#" class="email" title="Email"></a>--%>
                                                                </div>
                                                                <c:set var="attributeCount" value="1" />
                                                                <%--
						<div class="rebates">

						</div>--%>
                                                            </div>
                                                            <dsp:getvalueof var="colorParam" param="color" />
                                                            <dsp:getvalueof var="sizeParam" param="size" />
                                                            <dsp:include src="everLiving_subprod_detail.jsp" flush="true">
                                                                <dsp:param name="productVO" param="productVO" />
                                                                <dsp:param name="colorParam" param="color" />
                                                                <dsp:param name="sizeParam" param="size" />
                                                            </dsp:include>
                                                            <c:set var="showMoreAttibsDiv" value="${true}" />
                                                            <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                                                                <dsp:param value="${attribs}" name="array" />
                                                                <dsp:oparam name="output">
                                                                    <dsp:getvalueof var="placeHolderMiddle" param="key" />
                                                                    <c:if test="${(not empty placeHolderMiddle) && (placeHolderMiddle eq AttributePDPMiddle)}">
                                                                        <dsp:droplet name="/atg/dynamo/droplet/ForEach">
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
                                                                                                    <a href="${actionURLMiddle}" class="newOrPopup"> <img src="${imageURLMiddle}" alt="<dsp:valueof param="element.attributeDescrip" valueishtml="true"/>"/></a>
                                                                                                </c:when>
                                                                                                <c:otherwise>
                                                                                                    <c:choose>
                                                                                                        <c:when test="${null ne actionURLMiddle}">
                                                                                                            <a href="${actionURLMiddle}" class="newOrPopup"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a>
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
                                                            <dsp:getvalueof var="firstChildSKU" param="pFirstChildSKU" />
                                                            <c:if test="${not empty firstChildSKU}">
                                                                <div id="firstproduct" style="display:none">
                                                                    LT_F_PRD_ID:=${parentProductId} LT_F_SKU_ID:=${firstChildSKU}
                                                                </div>
                                                            </c:if>
                                                            <dsp:getvalueof var="showTab" param="tabLookUp" />
                                                            <%-- <dsp:getvalueof var="inStock" param="inStock"/> --%>
                                                                <%-- Adding everLiving Error message --%>
                                                                    <c:set var="everLivingAlert">
                                                                        <bbbl:label key='lbl_everLivingPDP_error' language="${pageContext.request.locale.language}" />
                                                                    </c:set>
                                                                    <div class="no-padding small-12 columns">${everLivingAlert }</div>
                                                                    <dsp:getvalueof var="salePriceRangeDescription" param="productVO.salePriceRangeDescription" />
                                                                    <c:choose>
                                                                        <c:when test="${showTab}">
                                                                            <%--<div class="priceQuantity noPadTop <c:if test="${inStock==false}">priceQuantityNotAvailable</c:if>">
					<span itemprop="offers" itemscope itemtype="//schema.org/Offer">
						<div class="prodPrice" itemprop="price">
						<c:choose>
						<c:when test="${not empty pDefaultChildSku}">
							<dsp:include page="product_details_price.jsp">
								<dsp:param name="product" param="productId"/>
								<dsp:param name="sku" value="${pDefaultChildSku}"/>
							</dsp:include>
						</c:when>
						<c:otherwise>
						<c:choose>
							<c:when test="${not empty salePriceRangeDescription}">
								<span class="prodPriceNEW">
									<dsp:valueof converter="currency" param="productVO.salePriceRangeDescription" />
								</span>
								<dsp:getvalueof var="salePriceRange" param="productVO.salePriceRangeDescription"/>
								<c:choose>
									<c:when test="${fn:contains(salePriceRange,'-')}"><br /></c:when>
									<c:otherwise>&nbsp;</c:otherwise>
								</c:choose>								
								<span class="prodPriceOLD">
									<span class="was"><bbbl:label key='lbl_old_price_text' language="${pageContext.request.locale.language}" /></span>
									<span class="oldPriceNum"> <dsp:valueof converter="currency" param="productVO.priceRangeDescription" /></span>
								</span>
								<dsp:getvalueof idtype="java.lang.String" id="salePrice" param="productVO.salePriceRangeDescription" />
							</c:when> 
							<c:otherwise>
								<dsp:valueof converter="currency" param="productVO.priceRangeDescription" />
							</c:otherwise>
						</c:choose>
						</c:otherwise>
						</c:choose>
						</div>
						<span class="prodAvailabilityStatus hidden">
                             <c:choose>
                             <c:when test="${inStock==false}">
                             	<link itemprop="availability" href="//schema.org/OutOfStock"/><bbbl:label key='lbl_pdp_out_of_stock' language="${pageContext.request.locale.language}" />
                             </c:when>
                             <c:otherwise>
                             	<link itemprop="availability" href="//schema.org/InStock"/><bbbl:label key='lbl_pdp_in_stock' language="${pageContext.request.locale.language}" />
                             </c:otherwise>
                             </c:choose>
                        </span>
                        <span class="error" style="font-size: 10px;">No Longer Available For Sale Online</span>
						</span>
						<dsp:getvalueof var="emailStockAlertsEnabled" param="pSKUDetailVO.emailStockAlertsEnabled" />

							<div class="message <c:if test="${inStock==true}">hidden</c:if>">
								<c:choose>
									<c:when test="${inStock==true}">
									<input type="hidden" value="" name="oosSKUId" class="_oosSKUId"/>
									</c:when>
									<c:otherwise>
									<input type="hidden" value="${pDefaultChildSku}" name="oosSKUId" class="_oosSKUId"/>
									</c:otherwise>
								</c:choose>
								<input type="hidden" value="${oosProdId}" name="oosProdId"/>
								<div class="error"><bbbl:label key='lbl_pdp_product_notavailable_shipping' language="${pageContext.request.locale.language}" /></div>

								<c:if test="${OutOfStockOn}">
								<c:choose>
								<c:when test="${(null eq emailStockAlertsEnabled) || (emailStockAlertsEnabled==true)}">
								<div class="info"><a class="lnkNotifyOOS" href="#"><bbbl:label key='lbl_pdp_product_notify_item_available' language="${pageContext.request.locale.language}" /></a></div>
								</c:when>
								<c:otherwise>
								<div class="info hidden"><a class="lnkNotifyOOS" href="#"><bbbl:label key='lbl_pdp_product_notify_item_available' language="${pageContext.request.locale.language}" /></a></div>
								</c:otherwise>
								</c:choose>
								</c:if>
								<c:if test="${MapQuestOn && (appid ne BedBathCanadaSite)}">

								<dsp:getvalueof var="bopusAllowed" param="pSKUDetailVO.bopusAllowed"/>
									   	<c:if test="${not bopusAllowed}">
											<div class="info"><a href="#" class="changeStore" role="link"><bbbl:label key='lbl_pdp_product_findstore_near' language="${pageContext.request.locale.language}" /></a></div>
										</c:if>
								</c:if>
							</div>
						
						<div class="quantityPDP">
						
							<label id="lblquantity" class="fl"><bbbl:label key='lbl_pdp_product_quantity' language="${pageContext.request.locale.language}" /></label>
							<div class="spinner fr">
						 		<label id="lblpdpDecQty" class="txtOffScreen" for="pdpDecQty"><bbbl:label key="lbl_decrease_quantity" language="${pageContext.request.locale.language}" /></label>
								<a href="#" id="pdpDecQty" class="scrollDown down" title="<bbbl:label key='lbl_decrease_quantity' language="${pageContext.request.locale.language}" />"><span class="txtOffScreen"><bbbl:label key="lbl_deccrease_quantity" language="${pageContext.request.locale.language}" /></span></a>
								<input id="quantity" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="fl addItemToRegis _qty itemQuantity addItemToList" type="text" role="textbox" name="qty" value="1" maxlength="2" data-change-store-submit="qty" data-change-store-errors="required digits nonZero" aria-required="true" aria-describedby="quantity" />
						 		<label id="lblpdpIncQty" class="txtOffScreen" for="pdpIncQty"><bbbl:label key="lbl_increase_quantity" language="${pageContext.request.locale.language}" /></label>
								<a href="#" id="pdpIncQty" class="scrollUp up" title="<bbbl:label key='lbl_increase_quantity' language="${pageContext.request.locale.language}" />"><span class="txtOffScreen"><bbbl:label key="lbl_increase_quantity" language="${pageContext.request.locale.language}" /></span></a>
								<dsp:getvalueof var="prodId" param="productId" />
								<dsp:getvalueof var="regId" param="registryId" />
								<input type="hidden" name="registryId" class="sflRegistryId  addItemToRegis addItemToList" value="${regId}" data-change-store-submit="registryId" />
								<input type="hidden" name="prodId" class="_prodId addItemToRegis productId addItemToList" value="${prodId}" data-change-store-submit="prodId" data-change-store-errors="required"  data-change-store-internalData="true"/>
								<input type="hidden" name="skuId" value="${pDefaultChildSku}" class="addItemToRegis _skuId addItemToList changeStoreSkuId" data-change-store-submit="skuId" data-change-store-errors="required"  data-change-store-internalData="true"/>
								<input type="hidden" name="price" value="${omniPrice}" class="addItemToList addItemToRegis" />
								<input type="hidden" class="addToCartSubmitData" name="storeId" value="" data-change-store-storeid="storeId"/>
								<input type="hidden" class="addToCartSubmitData" name="bts" value="${bts}" data-change-store-storeid="bts"/>
								<c:if test="${not empty regId}">
									<input type="hidden" class="addToCartSubmitData" name="registryId" value="${regId}" data-change-store-submit="registryId"/>
								</c:if>
							</div>
						</div>
					</div> --%>
                                                                                <%--<div class="btnPD clearfix">
							<div class="fl addToList noMarLeft">
								<div class="button button_disabled">
									<input type="button" name="btnAddToList" id="btnAddToList" value="Save for Later" role="button" aria-pressed="false"  onclick="rkg_micropixel('${appid}','wish')"/>
								</div>
							</div>

							<c:if test="${MapQuestOn && (appid ne BedBathCanadaSite)}">
								<div class="fl findInStore">
								    <dsp:getvalueof var="bopusAllowed" param="pSKUDetailVO.bopusAllowed"/>
								   	<c:choose>
									   	<c:when test="${bopusAllowed}">
									   	   <div class="button changeStoreItemWrap button_active_orange button_disabled">
										     <input type="button" name="btnFindInStore" value="Find in Store" disabled="disabled" class="changeStore" role="button" aria-pressed="false" />
										   </div>
										</c:when>
										<c:otherwise>
										   <div class="button changeStoreItemWrap button_active_orange">
									     <input type="button" name="btnFindInStore" value="Find in Store" role="button" aria-pressed="false" onclick="pdpOmnitureProxy('${parentProductId}', 'findinstore');rkg_micropixel('${appid}','findstore');"  class="changeStore"/>
										   </div>
										</c:otherwise>
									</c:choose>
								</div>
							</c:if>
							<div class="fl">
							<dsp:include page="../addgiftregistry/add_item_gift_registry.jsp">
								</dsp:include>
								<input type="hidden" name="parentProdId" class="_prodId addItemToRegis addItemToList" value="${parentProductId}" />
								<div class="fl addToRegistry">
								<div class="button button_disabled">
								<input  class="addItemToRegNoLogin" name="addItemToRegNoLogin"
										type="button" value="${submitAddToRegistryBtn}" role="button" />
								</div>
								</div>
							</div>

							<div class="fl addToCart button_disabled">
								<div class="button button_disabled">
									<input type="submit" name="btnAddToCart" value="Add to Cart" onclick="rkg_micropixel('${appid}','cart')" role="button" aria-pressed="false" disabled="disabled" <c:if test="${inStock==true}">class="enableOnDOMReady"</c:if> />
								</div>
							</div>

						</div> --%>
                                    </div>
                                    <div class="hidden appendSKUInfo">
                                        <c:if test="${not empty pDefaultChildSku}">
                                            <p class="smalltext prodSKU">
                                                <bbbl:label key='lbl_pdp_sku_id' language="${pageContext.request.locale.language}" /> ${pDefaultChildSku}</p>
                                        </c:if>
                                    </div>
                                    <%--  CR Changes  --%>
                                        <c:if test="${MapQuestOn && (appid eq BuyBuyBabySite)}">
                                            <bbbt:textArea key="txt_pdp_pickupinstore" language="${pageContext.request.locale.language}" />
                                        </c:if>
                                        <div class="message">
                                            <div id="addToCartErrorDiv" class="error"></div>
                                        </div>
                                        <dsp:getvalueof var="brandId" param="productVO.productBrand.brandId" />
                                        <dsp:getvalueof var="brandName" param="productVO.productBrand.brandName" />
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
                                            <div class="clearfix cb">
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
                                                                var _ga_refinedBrandName = '${brandName}'.replace(/&#39;/, ' ');
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
                                                <%--<div class="fl shopAll">
						<a href="${contextPath}/browse/shipping_policies.jsp" class="popupShipping"><bbbl:label key='lbl_pdp_shipping_cost' language="${pageContext.request.locale.language}" /></a>
						</div> --%>
                                            </div>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="grid_6 alpha" style="padding-top:10px;">
                                                    <%-- This sections displays prices for collection and accessories --%>
                                                        <%--<div class="priceQuantity">
							<div itemprop="offers" itemscope itemtype="//schema.org/Offer">
							<div class="prodPrice" itemprop="price">
							<c:choose>
								<c:when test="${not empty salePriceRangeDescription}">
									<dsp:getvalueof var="salePriceRange" param="productVO.salePriceRangeDescription"/>
									<c:choose>
										<c:when test="${fn:contains(salePriceRange,'-')}"><br /></c:when>
										<c:otherwise>&nbsp;</c:otherwise>
									</c:choose>
									<span class="prodPriceNEW">
										<dsp:valueof converter="currency"
											param="productVO.salePriceRangeDescription" />
									</span>

									<span class="prodPriceOLD">
										<span class="was"><bbbl:label key='lbl_old_price_text' language="${pageContext.request.locale.language}" /></span>
										<span class="oldPriceNum"> <dsp:valueof converter="currency"
												param="productVO.priceRangeDescription" /></span>
									</span>
									<dsp:getvalueof idtype="java.lang.String" id="salePrice" param="productVO.salePriceRangeDescription" />
								</c:when>
								<c:otherwise>
									<dsp:valueof converter="currency" param="productVO.priceRangeDescription" />
								</c:otherwise>
							</c:choose>
							</div>
							</div>
						</div>--%>
                                                            <%-- R2.2 117-a1 story --%>
                                                                <c:if test="${collection==true}">
                                                                    <c:if test="${isLeadSKU==false}">
                                                                        <div class="productLinks noMarTop clearfix marBottom_5">
                                                                            <div class="button button_active">
                                                                                <a class="lnkCollectionItems smoothScrollTo" data-smoothscroll-topoffset="65" href="#collectionItems">
                                                                                    <bbbl:label key='lbl_pdp_show_collection' language="${pageContext.request.locale.language}" />
                                                                                </a>
                                                                            </div>
                                                                        </div>
                                                                    </c:if>
                                                                </c:if>
                                                                <%-- Added to display Show All label  --%>
                                                                    <div class="clearfix cb">
                                                                        <dsp:getvalueof var="brandId" param="productVO.productBrand.brandId" />
                                                                        <dsp:getvalueof var="brandName" param="productVO.productBrand.brandName" />
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
                                                                                            var _ga_refinedBrandName = '${brandName}'.replace(/&#39;/, ' ');
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
                                                    <c:set var="tipsOn">
                                                        <tpsw:switch tagName="TipsOn_US" />
                                                    </c:set>
                                                </c:when>
                                                <c:when test="${appid eq BuyBuyBabySite}">
                                                    <c:set var="tipsOn">
                                                        <tpsw:switch tagName="TipsOn_baby" />
                                                    </c:set>
                                                </c:when>
                                                <c:when test="${appid eq BedBathCanadaSite}">
                                                    <c:set var="tipsOn">
                                                        <tpsw:switch tagName="TipsOn_CA" />
                                                    </c:set>
                                                </c:when>
                                            </c:choose>
                                            <c:if test="${tipsOn}">
                                                <dsp:droplet name="TargetingRandom">
                                                    <dsp:param bean="/atg/registry/RepositoryTargeters/TargeterPDPSecond" name="targeter" />
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
                <script type="text/javascript">
                var resx = new Object();
                </script>
                <div id="prodExplore productTabs" class="grid_12 row" role="complementary">
                    <%-- <span class="fl"><bbbl:label key='lbl_pdp_product_explore' language="${pageContext.request.locale.language}" /></span> --%>
                        <%-- Start for Chat and Ask and Answer 2.1.1 --%>
                            <%-- Commenting Click to Chat as part of 34473 
               	<c:if test="${chatEnabled}">
               	<h2 class="clearfix">
					<dsp:include page="/common/click2chatlink.jsp">
					    <dsp:param name="pageId" value="${chatCode}"/>
					    <dsp:param name="prodId" value="${parentProductId}"/>
					    <dsp:param name="divApplied" value="${true}"/>
						<dsp:param name="divClass" value="grid_3 omega fr"/>
					</dsp:include>
				</h2>
				</c:if>
				--%>
                                <%-- End for Chat and Ask and Answer 2.1.1 --%>
                                    <!-- <div class="categoryProductTabs marTop_10 tabs radius"> -->
                                    <ul class="categoryProductTabsLinks tabs radius" data-tab>
                                        <li id="prodDetailsTab" class="tab-title tab active">
                                            <div class="arrowSouth"></div>
                                            <a title='<bbbl:label key=' lbl_pdp_product_info ' language="${pageContext.request.locale.language}" />' href="#prodExplore-tabs1" onclick="pdpOmnitureProxy('${parentProductId}', 'productspec')">
                                                <bbbl:label key='lbl_pdp_product_info' language="${pageContext.request.locale.language}" />
                                            </a>
                                        </li>
                                        <c:set var="count" value="2" />
                                        <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                                            <dsp:param param="productTabs" name="array" />
                                            <dsp:oparam name="output">
                                                <li id="prodDetailsTab" class="tab-title tab">
                                                    <div class="arrowSouth"></div>
                                                    <a title="<dsp:valueof param=" element.tabName "/>" href="#prodExplore-tabs<c:out value='${count}'/>">
                                                        <dsp:valueof param="element.tabName" />
                                                    </a>
                                                </li>
                                                <c:set var="count" value="${count+1}" />
                                            </dsp:oparam>
                                        </dsp:droplet>
                                        <c:if test="${BazaarVoiceOn}">
                                            <li id="bvRRTab" class="tab-title tab">
                                                <div class="arrowSouth"></div>
                                                <a title="<bbbl:label key='lbl_pdp_product_ratings_reviews' language=" ${pageContext.request.locale.language} " />" href="#prodExplore-tabs<c:out value='${count}'/>" onclick="pdpOmnitureProxy('${parentProductId}', 'ratingreviews')">
                                                    <bbbl:label key='lbl_pdp_product_ratings_reviews' language="${pageContext.request.locale.language}" />
                                                </a>
                                            </li>
                                            <%-- Start for Chat and Ask and Answer 2.1.1 --%>
                                                <c:if test="${askAndAnswerEnabled}">
                                                    <li id="bvRRTab" class="tab-title tab">
                                                        <div class="arrowSouth"></div>
                                                        <a title="<bbbl:label key='lbl_pdp_product_ask_answer' language=" ${pageContext.request.locale.language} " />" class="aaBeta" href="#prodExplore-tabs<c:out value='${count+1}'/>" onclick="pdpOmnitureProxy('${parentProductId}', 'askAndAnswer')"><span><bbbl:label key='lbl_pdp_product_ask_answer' language="${pageContext.request.locale.language}" /></span><span class="aaBetaImg"></span><span class="clear"></span></a>
                                                    </li>
                                                    <c:set var="showQA" value="true" scope="request" />
                                                </c:if>
                                                <%-- End for Chat and Ask and Answer 2.1.1 --%>
                                        </c:if>
                                    </ul>
                                    <div class="tabs-content">
                                        <div id="prodExplore-tabs1" class="categoryProductTabsData content active">
                                            <div class="bulletsReset grid_6 alpha appendSKUInfo" itemprop="description">
                                                <dsp:valueof param="productVO.longDescription" valueishtml="true" />
                                                <c:if test="${not empty pDefaultChildSku}">
                                                    <p class="smalltext prodSKU">
                                                        <bbbl:label key='lbl_pdp_sku_id' language="${pageContext.request.locale.language}" /> ${pDefaultChildSku}</p>
                                                </c:if>
                                            </div>
                                            <%-- R 2.2.1 || Harmon START --%>
                                                <dsp:getvalueof var="harmonLongDescription" param="productVO.harmonLongDescription" />
                                                <c:if test="${not empty harmonLongDescription}">
                                                    <div class="harmonDesc grid_12 noMar">
                                                        <dsp:valueof param="productVO.harmonLongDescription" valueishtml="true" />
                                                    </div>
                                                </c:if>
                                                <%-- R 2.2.1 || Harmon END   --%>
                                                    <%-- <c:if test="${LiveClickerOn}">

						<div id="prodVideo" class="grid_6 omega" data-parentProdId="${parentProductId}">
							<input type="hidden" name="videoProdId" value="${parentProductId}" />
						</div>
						</c:if> --%>
                                        </div>
                                        <c:set var="count" value="2" />
                                        <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                                            <dsp:param param="productTabs" name="array" />
                                            <dsp:oparam name="output">
                                                <div id="prodExplore-tabs<c:out value='${count}'/>" class="categoryProductTabsData content categoryProductTabsData clearfix">
                                                    <dsp:valueof param="element.tabContent" valueishtml="true" />
                                                </div>
                                                <c:set var="count" value="${count+1}" />
                                            </dsp:oparam>
                                        </dsp:droplet>
                                        <c:if test="${BazaarVoiceOn}">
                                            <div id="prodExplore-tabs<c:out value='${count}'/>" class="categoryProductTabsData content categoryProductTabsData clearfix divRatings">
                                                <%--<bbbl:label key='lbl_pdp_product_rate_and_review' language="${pageContext.request.locale.language}" />--%>
                                                    <div id="BVRRContainer">
                                                        <%-- Commenting out SmartSEO as part of 34473
							<c:if test="${SmartSEOOn}">
								<dsp:droplet name="/com/bbb/commerce/browse/droplet/BVReviewContentDroplet">
									<dsp:param name="productId" param="productId"/>
									<dsp:param name="pageURL" value="${pageURL}"/>
									<dsp:param name="content" value="REVIEWS"/>
									<dsp:oparam name="output"> 
										<dsp:valueof param="bvContent" valueishtml="true"/>
									</dsp:oparam>
								</dsp:droplet>
							</c:if>
							--%>
                                                            <img width="20" height="20" alt="" src="/_assets/global/images/widgets/small_loader.gif">
                                                    </div>
                                            </div>
                                        </c:if>
                                        <%--  Start For BV Integration Container Tag 2.1 #216 --%>
                                            <c:if test="${showQA}">
                                                <div id="prodExplore-tabs<c:out value='${count+1}'/>" class="categoryProductTabsData clearfix divQuestions">
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
                <%-- Adding child products for collection and accessories --%>
                    <c:if test="${collection==true}">
                        <c:choose>
                            <c:when test="${isLeadSKU==true}">
                                <dsp:include page="accessoriesForms.jsp">
                                    <dsp:param name="color" value="${color}" />
                                    <dsp:param name="parentProductId" value="${parentProductId}" />
                                    <dsp:param name="crossSellFlag" value="true" />
                                    <dsp:param name="desc" value="Accessories (pdp)" />
                                    <dsp:param name="color" value="${color}" />
                                    <dsp:param name="isEverLivingProd" value="true" />
                                </dsp:include>
                                <dsp:getvalueof var="childProducts" param="collectionVO.childProducts" />
                                <c:set var="SchemeName" value="pdp_acccav;pdp_accfbw" />
                                <c:set var="omniProp5" value="Product with Accessory" />
                            </c:when>
                            <c:otherwise>
                                <dsp:include page="collectionForms.jsp">
                                    <dsp:param name="parentProductId" value="${parentProductId}" />
                                    <dsp:param name="crossSellFlag" value="true" />
                                    <dsp:param name="desc" value="Collection (ever living pdp)" />
                                    <dsp:param name="color" value="${color}" />
                                    <dsp:param name="isEverLivingProd" value="true" />
                                </dsp:include>
                                <dsp:getvalueof var="childProducts" param="collectionVO.childProducts" />
                                <c:set var="SchemeName" value="pdp_collcav;pdp_collfbw" />
                                <c:set var="omniProp5" value="Collection" />
                            </c:otherwise>
                        </c:choose>
			<input type="hidden" id="nextIndex" value="-1" />
                    </c:if>
                    <input type="hidden" id="nextIndex" value="-1" />
                    <%--End Adding child products for collection and accessories --%>
                        <div id="botCrossSell" class="marTop_20 container_12" role="complementary">
                            <%-- Shashank Start2 --%>
                                <c:if test="${(not empty lastviewedProductsList)}">
                                    <div id="botCrossSell" class="marTop_10 grid_12">
                                        <div class="categoryProductTabsNew marTop_20">
                                            <c:set var="lastViewedTab">
                                                <bbbl:label key='lbl_pdp_product_last_viewed' language="${pageContext.request.locale.language}" />
                                            </c:set>
                                            <ul class="categoryProductTabsLinks">
                                                <c:if test="${not empty lastviewedProductsList}">
                                                    <%--<li><div class="arrowSouth"></div><a title="Last Viewed Items" href="#botCrossSell-tabs3" ><bbbl:label key='lbl_pdp_product_last_viewed' language="${pageContext.request.locale.language}" /></a></li> --%>
                                                </c:if>
                                            </ul>
                                            <c:if test="${not empty lastviewedProductsList}">
                                                <h2><bbbl:label key='lbl_pdp_product_last_viewed' language="${pageContext.request.locale.language}" /></h2>
                                                <br/>
                                                <div id="botCrossSell-tabs3" class="categoryProductTabsData">
                                                    <dsp:include page="last_viewed.jsp">
                                                        <dsp:param name="lastviewedProductsList" value="${lastviewedProductsList}" />
                                                        <dsp:param name="desc" value="Last Viewed (ever living pdp)" />
                                                    </dsp:include>
                                                </div>
                                            </c:if>
                                        </div>
                                    </div>
                                </c:if>
                        </div>
                        <%--Setting values to pass in ajax call --%>
                            <c:set var="certona_on">
                                <bbbc:config key="certona_no_search_results_flag" configName="FlagDrivenFunctions" />
                            </c:set>
                            <c:set var="cert_scheme" value="pdp_acccav;pdp_accfbw" />
                            <dsp:getvalueof var="productId" param="productId" />
                            <c:set var="custAlsoViewedProdMax" scope="request">
                                <bbbc:config key="PDPCustAlsoViewProdMax" configName="CertonaKeys" />
                            </c:set>
                            <c:set var="frequentlyBuyProdMax" scope="request">
                                <bbbc:config key="PDPFreqBoughtProdMax" configName="CertonaKeys" />
                            </c:set>
                            <dsp:getvalueof var="linkStringNonRecproduct" param="linkStringNonRecproduct" />
                            <%--Ajax Call --%>
                                <div id="certonaBottomTabs" class="clearfix loadAjaxContent" data-ajax-url="${contextPath}/browse/certona_everliving_slots.jsp" data-ajax-target-divs="#certonaBottomTabs" data-ajax-params-count="6" data-ajax-param1-name="scheme" data-ajax-param1-value="${SchemeName}" data-ajax-param2-name="certonaSwitch" data-ajax-param2-value="${CertonaOn}" data-ajax-param3-name="productId" data-ajax-param3-value="${productId}" data-ajax-param4-name="number" data-ajax-param4-value="${custAlsoViewedProdMax};${frequentlyBuyProdMax}}" 
                                data-ajax-param5-name="parentProductId"	data-ajax-param5-value="${parentProductId}" data-ajax-param6-name="linkStringNonRecproduct" data-ajax-param6-value="${linkStringNonRecproduct}" 				
                                role="complementary">
                                    <div class="grid_12 clearfix"><img width="20" height="20" alt="small loader" src="/_assets/global/images/widgets/small_loader.gif" /></div>
                                </div>
            </div>
            </div>
            <dsp:droplet name="ProductViewedDroplet">
                <dsp:param name="id" param="productId" />
            </dsp:droplet>
        </dsp:oparam>
        <dsp:oparam name="error">
            <c:set var="productNotfound" value="true" />
            <div id="content" class="container_12 clearfix" role="main">
                <div class="grid_12 clearfix marTop_20">
                    <h3><span class="error">
			<bbbl:label key='lbl_pdp_product_not_available' language="${pageContext.request.locale.language}" />
			</span></h3>
                </div>
            </div>
        </dsp:oparam>
    </dsp:droplet>
    <script type="text/javascript">
    var resx = new Object();
    </script>
</dsp:page>
