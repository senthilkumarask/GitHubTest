<dsp:page>

<dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<c:set var="lblReviewsCount"><bbbl:label key="lbl_reviews_count" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblReviewCount"><bbbl:label key="lbl_review_count" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblForThe"><bbbl:label key="lbl_accessibility_for_the" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblAboutThe"><bbbl:label key="lbl_accessibility_about_the" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="writeReviewLink"><bbbl:label key='lbl_grid_write_review_link' language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="omniProp5" value="Product modal"/>
	<c:set var="productNotfound" value="false"/>
	<c:set var="productOOS" value="false"/>
	<c:set var="customizeCTACodes">
		<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
	</c:set>
	
	<c:set var="shippingAttributesList">
		<bbbc:config key="shippingAttributesList" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="sddAttributeList">
		<bbbc:config key="sameDayDelAttributeKeyList" configName="SameDayDeliveryKeys" />
	</c:set>
	<c:set var="vdcAttributesList">
		<bbbc:config key="vdcAttributesList" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="AttributePDPTOP">
		<bbbl:label key='lbl_pdp_attributes_top' language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="AttributePDPPrice">
		<bbbl:label key='lbl_pdp_attributes_price' language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="scene7Path" scope="request">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
	<c:set var="BedBathUSSite" scope="request">
		<bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BuyBuyBabySite" scope="request">
		<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BedBathCanadaSite" scope="request">
		<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:choose>
		<c:when test="${currentSiteId eq BedBathUSSite}">
			<c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_us"/></c:set>
		</c:when>
		<c:when test="${currentSiteId eq BuyBuyBabySite}">
			<c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_baby"/></c:set>
		</c:when>
		<c:otherwise>
			<c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_ca"/></c:set>
		</c:otherwise>
	</c:choose>
	<%--   CERTONA AND SCENE 7 INTEGRATION STARTS --%>

	<c:choose>
		<c:when test="${currentSiteId eq BedBathUSSite}">
			<c:set var="SceneSevenOn" scope="request">
				<tpsw:switch tagName="SceneSevenTag_us"/>
			</c:set>
		</c:when>
		<c:when test="${currentSiteId eq BuyBuyBabySite}">
			<c:set var="SceneSevenOn" scope="request">
				<tpsw:switch tagName="SceneSevenTag_baby"/>
			</c:set>
		</c:when>
		<c:when test="${currentSiteId eq BedBathCanadaSite}">
			<c:set var="SceneSevenOn" scope="request">
				<tpsw:switch tagName="SceneSevenTag_ca"/>
			</c:set>
		</c:when>
	</c:choose>
	<c:set var="scene7DomainPath">
		<bbbc:config key="BBBSceneSevenDomainPath" configName="ThirdPartyURLs" />
	</c:set>

	<c:set var="enableKatoriFlag" scope="request"><bbbc:config key="enableKatori" configName="EXIMKeys"/></c:set>
	<input type="hidden" id="enableKatoriFlag" name="enableKatoriFlag" value="${enableKatoriFlag}">
	<c:set var="enableUGCQV" scope="request"><bbbc:config key="enableUGCQV" configName="FlagDrivenFunctions"/></c:set>
	<input type="hidden" id="enableUGCQV" name="enableUGCQV" value="${enableUGCQV}">

	<%--   CERTOAN AND SCENE 7 INTEGRATION ENDS --%>
	<dsp:getvalueof var="personalisedCode" param="personalisedCode"/>
	<dsp:getvalueof var="customizedPrice" param="customizedPrice"/>
	<dsp:getvalueof var="customizationDetails" param="customizationDetails"/>
	<dsp:getvalueof var="upc" param="upc" />
	<dsp:getvalueof var="color" param="color"/>
	<dsp:getvalueof var="size" param="size"/>
	<dsp:getvalueof var="skuID" param="skuId"/>
	<dsp:getvalueof var="personalizedImageUrls" param="personalizedImageUrls"/>
	<dsp:getvalueof var="personlisedPrice" param="personlisedPrice"/>
	<dsp:getvalueof var="personalizedImageUrlThumbs" param="personalizedImageUrlThumbs"/>
    <dsp:getvalueof var="refNum" param="refNum"/>
	<dsp:getvalueof var="disableItemDetails" param="disableItemDetails"/>
	<dsp:getvalueof var="personalizationType" param="personalizationType" />
	<dsp:getvalueof var="registryView" param="registryView" />
	<dsp:getvalueof var="regItemOldQty" param="regItemOldQty" />
	<dsp:getvalueof var="qtyPurchased" param="qtyPurchased" />
	<dsp:getvalueof var="rowID" param="rowID" />
	<dsp:getvalueof var="itemType" param="itemType" />
	<dsp:getvalueof var="productId" param="productId" />
	<dsp:getvalueof var="eventType" param="eventType" />
	<dsp:getvalueof var="isBelowLineItem" param="isBelowLineItem" />
	<dsp:getvalueof var="isIntlRestricted" param="isIntlRestricted" />
	<dsp:getvalueof var="isInternationalCustomer" param="isInternationalCustomer" />
	<dsp:getvalueof var="enableLTLReg" param="enableLTLReg" />
	<dsp:getvalueof var="regAddress" value="${fn:escapeXml(param.regAddress)}" />
	<dsp:getvalueof var="deliverySurcharge" param="deliverySurcharge"/>



	<dsp:droplet name="/com/bbb/common/droplet/EximCustomizationDroplet">
	    <dsp:param name="personalizationOptions" param="personalisedCode" />
		<dsp:oparam name="output">
			 <dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
			 <dsp:getvalueof var="personalizationOptionsDisplay" param="personalizationOptionsDisplay" />
		</dsp:oparam>
	</dsp:droplet>
<dsp:droplet name="ProductDetailDroplet">
	<dsp:param name="id" param="productId" />
	<dsp:param name="siteId" value="${appid}"/>
	<dsp:param name="skuId" param="skuId"/>
	<dsp:param name="registryId" param="registryId" />
	<dsp:param name="isMainProduct" value="true" />
	<c:if test="${!registryView eq 'owner'}">
		<dsp:param name="showIncartPrice" value="true" />
	</c:if>
	<dsp:getvalueof var="collection" param="productVO.collection"/>
	<dsp:oparam name="output">
		<dsp:getvalueof var="skuVO" param="pSKUDetailVO" />
        <dsp:getvalueof var="skuinStock" param="inStock" />
        <dsp:getvalueof var="priceLabelCodeSKU" param="pSKUDetailVO.pricingLabelCode"/>
        <dsp:getvalueof var="inCartFlagSKU" param="pSKUDetailVO.inCartFlag"/>
        <dsp:getvalueof var="ltlItemFlag" param="pSKUDetailVO.ltlItem"/>
        <dsp:getvalueof var="parentProdId" param="pSKUDetailVO.parentProdId"/>
        <dsp:getvalueof var="childSKUs" param="productVO.childSKUs"/>
        <dsp:getvalueof var="prodGuideId" param="productVO.shopGuideId"/>

        <c:set var="isMultiSku" value="false" />
        <c:if test="${fn:length(childSKUs) > 1}">
        <c:set var="isMultiSku" value="true" />
        </c:if>
		<c:choose>
				<c:when test="${not empty skuVO}">
					<dsp:getvalueof var="attribs" param="pSKUDetailVO.skuAttributes"/>
				</c:when>
				<c:otherwise>
					<dsp:getvalueof var="attribs" param="productVO.attributesList"/>
				</c:otherwise>
		</c:choose>
		<c:choose>
				<c:when test="${not empty skuVO}">
					<dsp:getvalueof var="displayShipMsg" param="pSKUDetailVO.displayShipMsg"/>
				</c:when>
				<c:otherwise>
					<dsp:getvalueof var="displayShipMsg" param="productVO.displayShipMsg"/>
				</c:otherwise>
		</c:choose>
		<c:choose>
					<c:when test="${skuinStock eq null}">
						<c:set var="inStock" value="true" />
					</c:when>
					<c:otherwise>
						<dsp:getvalueof var="inStock" param="inStock" />
					</c:otherwise>
		</c:choose>
		<c:if test="${inStock==false}">
                    <c:set var="productOOS" value="true"/>
        </c:if>
		<dsp:getvalueof var="productName" param="productVO.name"/>

		<%--   : QUICK VIEW CONTAINER STARTS --%>
		<div title="<c:out value='${productName}'/>" id="collectionMultiSku">
		    <div class="width_10 clearfix">

				<%--   : PERSONALIZATION MESSAGE CONTAINER STARTS --%>
			    <c:if test="${not empty refNum}">
			    <div class= "personalizedPDPContainer">
					<h3 class= "savePersonalization">
					<c:choose>
						<c:when test="${not empty personalisedCode && fn:contains(customizeCTACodes, personalisedCode)}">
							<bbbl:label key='lbl_quick_view_item_ready_to_purchase_custom' language="${pageContext.request.locale.language}" />
						</c:when>
						<c:otherwise>
							<bbbl:label key='lbl_quick_view_item_ready_to_purchase' language="${pageContext.request.locale.language}" />
						</c:otherwise>
					</c:choose>
					</h3>
			    </div>
			    </c:if>
				<%--   : PERSONALIZATION MESSAGE CONTAINER ENDS --%>

				<%--   : IMAGE CONTAINER STARTS --%>
		        <div class="width_5 fl prodImageWrapper">
		            <dsp:getvalueof var="largeImage" param="productVO.productImages.largeImage"/>
	                <dsp:getvalueof var="smallImage" param="productVO.productImages.smallImage"/>
	                <dsp:getvalueof var="thumbnailImage" param="productVO.productImages.thumbnailImage" />
					<dsp:getvalueof var="basicImage" param="productVO.productImages.basicImage" />
	                <dsp:getvalueof var="productName" param="productVO.name" scope="request"/>
	                <dsp:getvalueof var="refinedNameProduct" param="productVO.refinedName" scope="request"/>
	                <dsp:getvalueof var="zoomFlag" param="productVO.zoomAvailable"/>
					<dsp:getvalueof var="zoomQuery" param="zoomQuery"/>
		            <%--<c:choose>
					<c:when test="${not empty refNum}">
					<img id="mainProductImg" src="${personalizedImageUrls}" class="prodImage" height="380" width="380" alt="<c:out value='${productName}'/>" onerror="this.src = '${imagePath}/_assets/global/images/no_image_available.jpg';"/>
					</c:when>
					<c:otherwise>
					<img id="mainProductImg" data-original-url="${scene7Path}/${largeImage}" src="/_assets/global/images/blank.gif" class="loadingGIF mainProductImage noImageFound" height="380" width="380" alt="<c:out value='${productName}'/>" />
					</c:otherwise>
				</c:choose> --%>
				<input type="hidden" value="${personalizedImageUrls}" id="personalizedMainImageURL" />
					<div class="width_312px alpha zoomProduct">
						<c:if test="${(not empty largeImage)}">
							<c:choose>
								<c:when test="${productId % 2 == 0}">
									<c:set var="scene7ImgURL" value="${fn:replace(scene7DomainPath, 'x', '1')}" />
								</c:when>
								<c:when test="${productId % 2 == 1}">
									<c:set var="scene7ImgURL" value="${fn:replace(scene7DomainPath, 'x', '2')}" />
								</c:when>
								<c:otherwise>
									<c:set var="scene7ImgURL" value="${scene7Path}" />
								</c:otherwise>
							</c:choose>
						</c:if>
						<div id="collectionMultiSkuImageWrapper" <c:if test="${zoomFlag && SceneSevenOn && (not empty largeImage)}"> class="loadingGIF bbbS7ImageWrapper" data-s7ImgsetID="${largeImage}" </c:if> data-parentProdId="${productId}">
							<div class="bbbS7Main ">
								<div id="s7ProductImageWrapperInner" class="easyzoom easyzoom--adjacent ">
								<c:choose>
									<c:when test="${empty largeImage || !SceneSevenOn}">
										<img itemprop="image" id="mainProductImg" src="${imagePath}/_assets/global/images/no_image_available.jpg" class="mainProductImage noImageFound" height="312" width="312" alt="${productName}" />
								   </c:when>
								   <c:when test="${!zoomFlag && SceneSevenOn && (not empty largeImage)}">
										<img itemprop="image" id="mainProductImg" src="${scene7ImgURL}/${largeImage}" class="mainProductImage noImageFound" height="312" width="312" alt="${productName}" />
								   </c:when>
								   <c:otherwise>
											<a id="mainProductImgZoom" data-zoomhref="${scene7ImgURL}/${basicImage}?${zoomQuery}" href="javascript:void(0)" tabindex="2">
												<img itemprop="image" id="mainProductImg" src="${scene7ImgURL}/${largeImage}" class="mainProductImage"  alt="${productName}" height="312" width="312"  />
											</a>
											<%-- <img id="mainProductImg" src="${imagePath}/_assets/global/images/blank.gif" class="mainProductImage noImageFound" height="312" width="312" alt="${productName}" /> --%>
								   </c:otherwise>
								 </c:choose>
							</div>
						</div>
					</div>
					</div>
		        </div>
				<%--   : IMAGE CONTAINER ENDS --%>
				<%--   : PRODUCT DETAILS CONTAINER STARTS --%>
				<div class="width_5 prodDetailsWrapper fl">
					<%--   PRODUUCT ATTRIBUTES STARTS --%>
					<div id="prodAttribs" class="prodAttribs prodAttribWrapper">
						<c:set var="showShipCustomMsg" value="true"/>
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
											<dsp:getvalueof var="attrId" param="element.attributeName" />
											<c:if test="${!fn:contains(sddAttributeList,attrId)}">
											<c:if test="${fn:contains(shippingAttributesList,attrId)}">
													<c:set var="showShipCustomMsg" value="false"/>
												</c:if>
											<div class="attribs">
												<c:choose>
													<c:when test="${null ne attributeDescripTop}">
														<c:choose>
															<c:when test="${null ne imageURLTop}">
																<a href="${actionURLTop}" class="qvAttribPopup"><img src="${imageURLTop}" alt="<dsp:valueof param="element.attributeDescrip" valueishtml="true"/>" /></a>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${null ne actionURLTop}">
																		<a href="${actionURLTop}" class="qvAttribPopup"><span class="prod-attrib"><dsp:valueof param="element.attributeDescrip" valueishtml="true" /></span></a>
																	</c:when>
																	<c:otherwise>
																		<span class="prod-attrib"><dsp:valueof param="element.attributeDescrip" valueishtml="true" /></span>
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<c:if test="${null ne imageURLTop}">
															<c:choose>
																<c:when test="${null ne actionURLTop}">
																	<a href="${actionURLTop}" class="qvAttribPopup"><img src="${imageURLTop}" alt="" /></a>
																</c:when>
																<c:otherwise>
																	<img src="${imageURLTop}" alt="" />
																</c:otherwise>
															</c:choose>
														</c:if>
													</c:otherwise>
												</c:choose>
											</div>
										  </c:if>
										</dsp:oparam>
									</dsp:droplet>
								</c:if>
							</dsp:oparam>
						</dsp:droplet>
					</div><%-- prodAttribs --%>
					<%--   PRODUUCT ATTRIBUTES ENDS --%>

					<dsp:getvalueof var="productId" param="productId" />
					<dsp:getvalueof var="registryId" param="registryId" />
					<dsp:getvalueof var="sopt" param="sopt" />
					<dsp:getvalueof var="skuId" param="skuId" />
					<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
						<dsp:param name="id" value="${productId}" />
						<dsp:param name="itemDescriptorName" value="product" />
						<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="pdpURL" vartype="java.lang.String"	param="url" />
						</dsp:oparam>
					</dsp:droplet>
					<c:set var="pdpURL"	value="${pdpURL}?skuId=${skuId}&registryId=${registryId}&sopt=${sopt}" />

					<div class="clear"></div>

					 <%--   PRODUCT TITLE STARTS --%>
					<h2 id="productTitle" class="productDetails">
						<c:choose>
							<c:when test="${ltlItemFlag && registryView eq 'guest'}">
								<dsp:valueof param="productVO.name" valueishtml="true" />
							</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${!(not empty refNum && disableItemDetails eq 'true')}">
									<a href="${contextPath}${pdpURL}"><dsp:valueof param="productVO.name" valueishtml="true" /></a>
								</c:when>
								<c:otherwise>
									<a href="javascript:void(0);" oncontextmenu="return false;" class="cursorDefault"><dsp:valueof param="productVO.name" valueishtml="true" /></a></h2>
								</c:otherwise>
							</c:choose>
						</c:otherwise>
						</c:choose>
					</h2>
					 <%--   PRODUCT TITLE STARTS --%>
					<div class="clear"></div>

					<c:if test="${not empty prodGuideId}">
									<dsp:droplet name="/atg/repository/seo/GuideItemLink">
										<dsp:param name="repositoryName" value="/com/bbb/cms/repository/GuidesTemplate" />
										<dsp:param name="itemDescriptor" value="guides" />
										<dsp:param name="id" param="productVO.shopGuideId"/>
										<dsp:oparam name="output">
											<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
										</dsp:oparam>
									</dsp:droplet>
									<div id="viewProductGuides" class="productLinks viewProductGuides clearfix">
										 <a href="${contextPath}${finalUrl}" onclick="pdpOmnitureProxy('${parentProductId}', 'prodguide')" title="View Buying Guide" class="productGuideImg"><bbbl:label key='lbl_view_Product_Guide' language="${pageContext.request.locale.language}" />
										 </a>
											</div>
								</c:if>

					 <%--   REBATE CONTAINER STARTS --%>
					<%-- Rebate display--%>
					<c:set var="rebateDivCreated" value="${false}" />
					<dsp:getvalueof var="hasRebate" param="pSKUDetailVO.hasRebate"/>
					<c:set var="rebatesOn" value="${false}" />
					<c:if test="${not empty hasRebate && hasRebate}">
						<dsp:getvalueof var="chkEligibleRebates" param="pSKUDetailVO.eligibleRebates"/>
						<c:if test="${(null != chkEligibleRebates) && (fn:length(chkEligibleRebates) == 1 )}">
							<c:set var="rebatesOn" value="${true}" />
						</c:if>
					</c:if>

					<%-- attribs droplet --%>
					<c:set var="showShipCustomMsg" value="true"/>
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
										<dsp:getvalueof var="attrId" param="element.attributeName" />
										<c:if test="${!fn:contains(sddAttributeList,attrId)}">
											<c:if test="${fn:contains(shippingAttributesList,attrId)}">
												<c:set var="showShipCustomMsg" value="false"/>
											</c:if>
										<c:choose>
											 <c:when test="${null ne attributeDescripPrice}">
												<c:if test="${rebateDivCreated == false}">
													<div id="rebateContainer" class="rebateContainer prodAttribWrapper">
													<c:set var="rebateDivCreated" value="${true}" />
												</c:if>
													<c:choose>
														<c:when test="${null ne imageURLPrice}">
															<div class="attribs ${sep}">
																<a href="${actionURLPrice}" class="qvAttribPopup">
																	<img src="${imageURLPrice}" alt="<dsp:valueof param="element.attributeDescrip" valueishtml="true"/>"/>
																</a>
															 </div>
														</c:when>
														<c:otherwise>
															<c:choose>
																<c:when test="${null ne actionURLPrice}">
																	<c:choose>
																		<c:when test="${not empty skuID && null ne attributeName && fn:contains(attributeName,vdcAttributesList)}">
																			<div class="attribs ${sep}"><a href="/store/browse/dynamicStaticContent.jsp?pageName=ItemShippedDirectlyFromVendor&isVdcSku=true&skuId=${skuID}" class="qvAttribPopup"><span class="prod-attrib"><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a></div>
																		</c:when>
																		<c:otherwise>
																			<div class="attribs ${sep}"><a href="${actionURLPrice}?skuID=${skuID}" class="qvAttribPopup"><span class="prod-attrib"><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a></div>
																		</c:otherwise>
																	</c:choose>
																</c:when>
																<c:otherwise>
																	<div class="attribs ${sep}"><span class="prod-attrib"><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></div>
																</c:otherwise>
															</c:choose>
														</c:otherwise>
													</c:choose>
											</c:when>
											<c:otherwise>
												<c:if test="${null ne imageURLPrice}">
												   <c:if test="${rebateDivCreated == false}">
													   <div id="rebateContainer" class="rebateContainer prodAttribWrapper">
													   <c:set var="rebateDivCreated" value="${true}" />
												   </c:if>
												   <c:choose>
														  <c:when test="${null ne actionURLPrice}">
																<div class="attribs ${sep}"><a href="${actionURLPrice}" class="qvAttribPopup"><img src="${imageURLPrice}" alt=""/></a></div>
														  </c:when>
														  <c:otherwise>
																<div class="attribs ${sep}"><img src="${imageURLPrice}" alt="" /></div>
														  </c:otherwise>
												   </c:choose>
												</c:if>
											 </c:otherwise>
										</c:choose>
										</c:if>
										</dsp:oparam>
								</dsp:droplet>
							</c:if>
						</dsp:oparam>
					</dsp:droplet>
					<%-- attribs droplet --%>
					<c:if test="${not empty hasRebate && hasRebate}">
						<dsp:getvalueof var="eligibleRebates" param="pSKUDetailVO.eligibleRebates"/>
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array" value="${eligibleRebates}"/>
							<dsp:oparam name="output">
								<c:if test="${rebateDivCreated == false}">
									<div id="rebateContainer" class="rebateContainer prodAttribWrapper">
									<c:set var="rebateDivCreated" value="${true}" />
								</c:if>
								<dsp:getvalueof var="chkCount1" param="count"/>
								<dsp:getvalueof var="chkSize1" param="size"/>
								<c:set var="sep1" value="seperator" />
								<c:if test="${chkCount1 == chkSize1}">
									<c:set var="sep1" value="" />
								</c:if>
								<dsp:getvalueof var="rebate" param="element"/>
								<div class="attribs ${sep1}" ><a href="${rebate.rebateURL}" class="links" target="_blank" title="Rebate"><c:out value="${rebate.rebateDescription}" escapeXml="false"/></a></div>
							</dsp:oparam>
						</dsp:droplet>
					</c:if>

					<c:if test="${rebateDivCreated == true}">
						 <div class="clear"></div>
						 </div> <%-- close rebate div --%>
					</c:if>
					<%-- End rebate --%>
					<div class="clear"></div>
					 <%--   REBATE CONTAINER ENDS --%>

					<%--   PRODUCT RATING STARTS --%>
					<c:if test="${!(registryView eq 'guest' && ltlItemFlag)}">
					<%--  <c:if test="${BazaarVoiceOn}">
						<div id="prodRatings">
							<dsp:getvalueof var="ratingAvailable" param="productVO.bvReviews.ratingAvailable"></dsp:getvalueof>
							<dsp:getvalueof param="productVO.bvReviews.averageOverallRating" var="averageOverallRating"/>
							<dsp:getvalueof param="element.bvReviews.totalReviewCount" var="totalReviewCount"/>
							<dsp:getvalueof param="productVO.bvReviews.ratingsTitle" var="ratingsTitle"/>
							<c:choose>
								<c:when test="${ratingAvailable == true}">
									<dsp:getvalueof var="fltValue" param="productVO.bvReviews.averageOverallRating" />
									<dsp:getvalueof param="productVO.bvReviews.totalReviewCount" var="totalReviewCount" />
									<li class="clearfix metaFeedback"><span title="${fltValue}" class="ratingReg ratingTxt prodReview prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel">${ratingsTitle}</span></span>
									<span>

											<c:if test="${!(not empty refNum && disableItemDetails eq 'true')}">
												<a href="${contextPath}${pdpURL}&showRatings=true">
												<c:choose>
													<c:when test="${totalReviewCount == 1}">
														(<dsp:valueof param="productVO.bvReviews.totalReviewCount" /><bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" />)
													</c:when>
													<c:otherwise>
														(<dsp:valueof param="productVO.bvReviews.totalReviewCount" /><bbbl:label key='lbl_reviews_count' language="${pageContext.request.locale.language}" />)
													</c:otherwise>
												</c:choose>
												</a>
											</c:if>
										</span>
									</li>
								</c:when>
								<c:otherwise>
									<c:set var="writeReviewLink"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></c:set>
									<div class="prodReview clearfix">
										<c:if test="${!(not empty refNum && disableItemDetails eq 'true')}">
											<a href="${contextPath}${pdpURL}&writeReview=true">
												<bbbl:label key='lbl_grid_write_review_link' language="${pageContext.request.locale.language}" />
											</a>
										</c:if>
									</div>
								</c:otherwise>
							</c:choose>
						</div>
					</c:if> --%>
					 <c:if test="${BazaarVoiceOn}">
					 <dsp:getvalueof var="productVO" param="productVO"/>
						<dsp:getvalueof var="reviews" value="${productVO.bvReviews.totalReviewCount}" />
						<dsp:getvalueof var="ratingsTitle" value="${productVO.bvReviews.ratingsTitle}" />
						<dsp:getvalueof var="ratings" value="${productVO.bvReviews.averageOverallRating}" vartype="java.lang.Integer" />
						<dsp:getvalueof var="rating" value="${ratings * 10}" vartype="java.lang.Integer" />
						<c:choose>
							<c:when test="${empty productVO}">
								<dsp:getvalueof var="totalReviewCount" value="0"></dsp:getvalueof>
							</c:when>
							<c:otherwise>
								<dsp:getvalueof var="totalReviewCount" value="${productVO.bvReviews.totalReviewCount}"></dsp:getvalueof>
								<dsp:getvalueof var="ratingsTitle" value="${productVO.bvReviews.ratingsTitle}" />
							</c:otherwise>
						</c:choose>
						<c:if test="${not flagOff}">
							<c:choose>
								<c:when test="${ratings ne null && ratings ne '0' && (reviews eq '1' || reviews gt '1') }">
									<div class="prodReviews clearfix metaFeedback">
										<span class="ratingTxt ratingsReviews prodReviews<fmt:formatNumber value="${rating}" pattern="#0" />"><span class="ariaLabel">${ratingsTitle}</span></span><span class="reviewTxt"><a onclick="javascript:(typeof quickViewCrossSell === 'function')?quickViewCrossSell():void(0);" href="${contextPath}${pdpUrl}?skuId=${skuId}&amp;categoryId=${CategoryId}&amp;showRatings=true" role="link" aria-label="${reviews} ${lblSflReviews} ${lblForThe} ${productName}"> ${totalReviewCount} <bbbl:label key="lbl_sfl_reviews" language="<c:out param='${language}'/>" />
										</a></span></div>
								</c:when>
								<c:otherwise>
									<div class="prodReviews clearfix metaFeedback">
									<span class="ratingsReviews prodReviews<fmt:formatNumber value="${rating}" pattern="#0" /> ratingTxt" ><span class="ariaLabel">${ratingsTitle}</span></span><span class="reviewTxt"><a onclick="javascript:(typeof quickViewCrossSell === 'function')?quickViewCrossSell():void(0);" href="${contextPath}${pdpUrl}?skuId=${skuId}&amp;showRatings=true" role="link" aria-label="${reviews} ${lblSflReviews} ${lblForThe} ${productName}">	${totalReviewCount} <bbbl:label key="lbl_sfl_reviews" language="<c:out param='${language}'/>" />
										</a></span></div>
								</c:otherwise>
							</c:choose>
						</c:if>
					</c:if> 
					</c:if>
					 <%--   PRODUCT RATING ENDS --%>

				 <%--   FREE SHIPPING CONTAINER STARTS --%>
	            <div class="freeShipBadge">
					<c:if test="${showShipCustomMsg}">
						${displayShipMsg}
					</c:if>
				</div>
	            <div class="clear"></div>
				 <%--   FREE SHIPPING CONTAINER ENDS --%>

				 <%--   PRICE CONTAINER STARTS --%>
				<div class="priceBtnWrap clearfix">
	                <div id="prodPrice" class="width_3 fl">
	                    <c:choose>
							<c:when test="${not empty personalisedCode && not empty refNum}">
								<c:if test="${inCartFlagSKU && registryView eq 'guest'}">
									<div class="disPriceString">
								    	 <bbbl:label key="lbl_discounted_incart_text" language="${pageContext.request.locale.language}" />
								    </div>
								</c:if>
								<div class="prodPrice">
		                                   ${personlisedPrice}
								</div>
								<span class="staticTextOnRLP">* Price subject to change.</span>

							</c:when>
							<c:otherwise>
								<div class="prodPrice">
				                    <dsp:include page="/browse/product_details_price.jsp">
										<dsp:param name="product" param="productId"/>
										<dsp:param name="sku" param="skuId"/>
										<dsp:param name="priceLabelCodeSKU" value="${priceLabelCodeSKU}" />
								        <dsp:param name="inCartFlagSKU" value="${inCartFlagSKU}" />
								        <c:if test="${!registryView eq 'owner'}">
								        	<dsp:param name="showInCartPrice" value="true" />
								        </c:if>
									</dsp:include>
								</div>
							</c:otherwise>
						</c:choose>
	                </div>
	                <div class="clear"></div>
	            </div>
				 <%--   PRICE CONTAINER ENDS --%>

				 <%--   PRSONALIZATION DETAILS STARTS --%>
				<c:if test="${not empty refNum}">
				<div class="personalizedPDPWrapper">
					<div class="productContent clearfix">
						<img src="${personalizedImageUrlThumbs}" class="personalizedImg" alt="<c:out value='${productName}'/>" height="100px" width="100px" onerror="this.src = '${imagePath}/_assets/global/images/no_image_available.jpg';"/>
						<ul class="prodInfo grid_3 padLeft_5">
							<%--Displaying actual Sku Color and size --%>
							<li class="personalisedProdDetails propColor">
								   <c:if test='${not empty color}'>
										<bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}" />
													:
										 <dsp:valueof value="${color}" valueishtml="true" />
								   </c:if>
							</li>
							<li class="personalisedProdDetails propSize ">
								<c:if test='${not empty size}'>
									<bbbl:label key="lbl_item_size" language="${pageContext.request.locale.language}" /> :
									<dsp:valueof value="${size}" valueishtml="true" />
								</c:if>
							</li>
							<li class="personalisedProdDetails">
								<span class="personalizationType">${eximCustomizationCodesMap[personalisedCode]}:</span> <span class="prodNameDrop">${customizationDetails}</span>
							</li>

							<div class="priceAdditionText">
								<span class="personalizationAttr">
									<%-- BBBSL-8154 --%>
									<%-- <span class="eximIcon"  aria-hidden="true">${personalizationOptionsDisplay}</span>--%>
								</span>
								<c:choose>
									<c:when test="${personalizationType == 'PB'}">
										<bbbl:label key='lbl_PB_Fee_detail' language="${pageContext.request.locale.language}" />
									</c:when>
									<c:when test="${personalizationType == 'PY'}">
										<span class="addedPrice">${customizedPrice}</span>
										<span class='addPriceText'>
											<bbbl:label key="lbl_exim_added_price" language="${pageContext.request.locale.language}"/>
										</span>
									</c:when>
									<c:when test="${personalizationType == 'CR'}">
										<span class="addedPrice">${customizedPrice}</span>
										<span class='addPriceText'>
										<c:choose>
											<c:when test="${not empty personalisedCode && fn:contains(customizeCTACodes, personalisedCode)}">
												<bbbl:label key="lbl_exim_cr_added_price_customize" language="${pageContext.request.locale.language}"/>
											</c:when>
											<c:otherwise>
												<bbbl:label key="lbl_exim_cr_added_price" language="${pageContext.request.locale.language}"/>
											</c:otherwise>
										</c:choose>
										</span>
									</c:when>
								</c:choose>
							</div>
						</ul>
					</div>
				</div>
				</c:if>
				<div class="clear"></div>
				 <%--   PRSONALIZATION DETAILS ENDS --%>

				 <%--   PRODUCT DESCRIPTION STARTS - NEW CHANGES  --%>
				<div class="productSkuDetails productAttributes clearfix">
					<c:if test="${upc ne null}">
						<div class="fl prodUPC attributes">
							<strong><bbbl:label key='lbl_mng_regitem_upc' language="${pageContext.request.locale.language}" /></strong>
							${upc}</div>
					</c:if>
					<c:if test='${not empty color}'>
						<div class="fl prodColor attributes">
							<strong><bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}" />: </strong>
							<dsp:valueof value="${color}" valueishtml="true" />
						</div>
					</c:if>

					<c:if test='${not empty size}'>
						<div class="fl prodSize attributes">
							<strong><bbbl:label key="lbl_item_size" language="${pageContext.request.locale.language}" />: </strong>
							<dsp:valueof value="${size}" valueishtml="true" />
						</div>
					</c:if>
				</div>
				<div class="productDescWrapper">
					<p class="prodDescHead">Product Description</p>
					<div id="productDesc" class="bulletsReset productDesc">
						<div class="viewport">
							<div class="overview">
								<%--<dsp:valueof param="productVO.shortDescription"	valueishtml="true" />--%>
								 <%-- LONG DESCRIPTION STARTS--%>
								<dsp:valueof param="productVO.longDescription"	valueishtml="true" />
								 <%-- LONG DESCRIPTION ENDS--%>
							</div>
						</div>
					</div>
				</div>
				<div class="clear"></div>
				 <%--   PRODUCT DESCRIPTION ENDS --%>


	        </div>
	        <div class="clear"></div>
			<%--   : QUICK VIEW CONTAINER ENDS --%>
	    </div>

		 <%--   CTA CONTAINER STARTS --%>
		<div class="fr registryCTAContainer">
			<div class="grid_2 fl productRequested">
					<input type="hidden" name="registryId" value="${registryId}" class="frmAjaxSubmitData" />
                    <input type="hidden" name="skuId" value="${skuId}" class="frmAjaxSubmitData" />
                    <input type="hidden" name="prodId" value="${productId}" class="frmAjaxSubmitData" />
                    <input type="hidden" name="rowId" value="${rowID}" class="frmAjaxSubmitData" />
                    <c:choose>
                    <c:when test="${not empty sopt}">
                    <input type="hidden" name="ltlDeliveryServices" value="${sopt}" class="frmAjaxSubmitData" />
                    </c:when>
                    <c:otherwise>
                    <input type="hidden" name="ltlDeliveryServices" value="" class="frmAjaxSubmitData" />
                    </c:otherwise>
                    </c:choose>
                    <input type="hidden" name="ltlDeliveryPrices" value="${deliverySurcharge}" class="frmAjaxSubmitData" />
                    <input type="hidden" name="alternateNum" value="" class="frmAjaxSubmitData" />
                    <input type="hidden" name="regItemOldQty" value="${regItemOldQty}" class="frmAjaxSubmitData" />
                    <input type="hidden" name="itemTypes" value="${itemType}" class="frmAjaxSubmitData" />
                    <input type="hidden" name="refNum" value="${refNum}" class="frmAjaxSubmitData" />
                    <input type="hidden" name="updateDslFromModal" value="" class="frmAjaxSubmitData" />
                    <input type="hidden" name="itemPrice" value="" class="frmAjaxSubmitData" />
                    <input type="hidden" name="bts" value="false" class="addToCartSubmitData" />

				<span class="columnHeader"><bbbl:label key='lbl_mng_regitem_requested' language="${pageContext.request.locale.language}" /></span>
				<%-- Requested Count --%>
				<div class="input alpha marTop_5 clearfix spinner">
                    <div class="text clearfix">
						<c:set var="titleqt">
                         <bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />
                        </c:set>
                        <a href="#" class="down button-Med btnSecondary" aria-label="Decrease quantity of ${productName}. Please select update once you have finalized your quantity.">
                        	<span class="icon-fallback-text">
	                        	<span class="icon-minus" aria-hidden="true"></span>
                            </span>
                        </a>
                        <span class="incDec visuallyhidden" aria-live="assertive" aria-atomic="true"></span>
                        <input id="quantityPurchased" aria-labelledby="${regItemOldQty} of ${productName}" title="${regItemOldQty} of ${productName}." name="qty" value="${regItemOldQty}" class="valQtyGiftReg input_tiny_giftView fl itemQuantity" data-change-store-errors="required digits nonZero" data-change-store-submit="qty" type="text" aria-required="false" maxlength="2">
                        <a href="#" class="up button-Med btnSecondary" aria-label="Increase quantity of ${productName}. Please select update once you have finalized your quantity. ">
                            <span class="icon-fallback-text">
                                <span class="icon-plus" aria-hidden="true"></span>
                            </span>
                        </a>
                    </div>
                </div>
				<%-- Requested Count --%>
            </div>
			<div class="grid_2 fl productPurchased">
				<span class="columnHeader"><bbbl:label key='lbl_mng_regitem_purchased' language="${pageContext.request.locale.language}" /></span>
				<div class="input alpha marTop_5 clearfix spinner">
					${qtyPurchased}
				</div>

			</div>
			<div class="grid_2 fl">
				<div class="updateButton clearfix">
					<a href="#" data-trigger-button="update" class="validateQuantity button-Med btnPrimary" onclick="omniRegistryUpdateItem('${productId}','${eventType}','${registryId}', '${skuId}');">
                        <span class="txtOffScreen">Update Quantity of${productName} &nbsp. Please select update once you have finalized your quantity
                        </span>
                        <bbbl:label key='lbl_mng_regitem_update' language="${pageContext.request.locale.language}" />
                    </a>
				</div>

				<c:choose>
														<c:when test="${(isBelowLineItem eq 'false')}">
															<c:choose>
																<c:when test="${(regItemOldQty > qtyPurchased) || (regItemOldQty == 0 && qtyPurchased ==0)}">
																	<div class="addToCart marTop_5 <c:if test="${isInternationalCustomer && (isIntlRestricted || not empty refNum) }">disabled</c:if>">
			                                                         	<c:set var="lblAddTOCart"><bbbl:label key='lbl_mng_regitem_additemcart' language="${pageContext.request.locale.language}" /></c:set>
			                                                            <input class="button-Med btnSecondary <c:if test="${empty regAddress}">editRegInfo completeShipping</c:if>" type="button" name="<c:if test="${not empty regAddress}">btnAddToCart</c:if>" title="${lblAddTOCart}" aria-label="Add ${productName} to your cart" value="${lblAddTOCart}" role="button" <c:if test="${(isInternationalCustomer && isIntlRestricted) || (enableKatoriFlag eq false && not empty refNum)}">disabled="disabled"</c:if>/>

																	</div>
																	<c:if test="${isInternationalCustomer && isIntlRestricted}">
									                               		<div class="notAvailableIntShipMsg regIntShipMsg cb clearfix"><bbbl:label key='lbl_reg_intl_restricted_message' language="${pageContext.request.locale.language}" /></div>
								                                   </c:if>
																</c:when>
													            <c:otherwise>
																	<div class="purchasedConfirmation">
																		<span class="icon-fallback-text">
																			<span class="icon-checkmark" aria-hidden="true"></span>
																			<span class="icon-text"><bbbl:label key='lbl_mng_regitem_purchased_confirm' language="${pageContext.request.locale.language}" /></span>
																		</span>
															 			<bbbl:label key='lbl_mng_regitem_purchased_confirm' language="${pageContext.request.locale.language}" />
																	</div>
																</c:otherwise>
													        </c:choose>

																			<%-- <dsp:droplet name="IsProductSKUShippingDroplet">
																		<dsp:param name="siteId" value="${currentSiteId}"/>

																		<dsp:param name="skuId" value="${skuID}"/>
																			<dsp:oparam name="true">
																			<div class="fl clearfix cb padTop_10">

																			<div class="clearfix">

															                	<dsp:getvalueof var="restrictedAttributes" param="restrictedAttributes"/>
																				<c:forEach var="item" items="${restrictedAttributes}">

																				<c:choose>
																				<c:when test="${null ne item.actionURL}">
																				 <a href="${item.actionURL}" class="popup"><span>${item.attributeDescrip}</span></a>
																				</c:when>
																				<c:otherwise>
																				${item.attributeDescrip}
																				</c:otherwise>
																				</c:choose>
																				</c:forEach>
													              			</div>
													              			</div>
																		</dsp:oparam>
																		<dsp:oparam name="false">
																		</dsp:oparam>
																		</dsp:droplet>  --%>
																		</c:when>
														<c:otherwise>
															<span class="itemNotAvailable padTop_10 block"><bbbl:label key='lbl_mng_item_belowline' language="${pageContext.request.locale.language}" /></span>
														</c:otherwise>
													</c:choose>


			</div>
			<div class="clear"></div>
			<div class="lnkViewProductDetails">
				<a href="${contextPath}${pdpURL}"><bbbl:label key="lbl_pdp_view_item_detail" language="${pageContext.request.locale.language}" /></a>
			</div>
        </div>
		<div class="clear"></div>
		 <%--   CTA CONTAINER ENDS --%>
	</div>
	</dsp:oparam>
	<dsp:oparam name="error">
		<c:set var="productNotfound" value="true"/>
	</dsp:oparam>

</dsp:droplet>

	<%-- R2.2 Omniture Implementation Start --%>
	<c:choose>
		<c:when test="${(skuVO.customizableRequired || skuVO.customizationOffered) && not empty skuVO.customizableCodes}">
			<script type="text/javascript">
                var productNotfound = ${productNotfound},
                productOOS = ${productOOS},
                collectionPDP = ${collection};
                var BBB = BBB || {};
                var omni_channel = '${fn:replace(fn:replace(channel,'\'',''),'"','')}';
                var omni_refinedNameProduct = '${fn:replace(fn:replace(refinedNameProduct,'\'',''),'"','')}';
                var omni_prop2Var = '${fn:replace(fn:replace(prop2Var,'\'',''),'"','')}';
                var omni_prop3Var = '${fn:replace(fn:replace(prop3Var,'\'',''),'"','')}';
                if (typeof s !== "undefined") {
                    /* NOTE: (mbhatia3) Not an elegant solution for "UAT-591" ... but Omniture bbb_scode is VERY **smart** */
                    BBB.omnitureObj.backup_s("eVar1,eVar2,eVar3,eVar4,eVar5,eVar6,eVar7,eVar8,eVar47,productNum,prop7,prop8,prop25");
                    BBB.omnitureObj.clear_s("eVar3");
                    BBB.omnitureObj.delete_s("eVar1,eVar2,eVar4,eVar5,eVar6,eVar7,eVar8,eVar47,productNum,prop7,prop8,prop25");
                    s.prop1="Product Details Page";
					s.prop12="Product Quick View";
                    if(productNotfound){
                        s.pageName="product not available";
                        s.channel="Error page";
                        s.products="${omni_prod}".replace(/,(?=[^,]*$)/, '');
                        s.prop2="Error page";
                        s.prop3="Error page";
                        s.prop4="";
                        s.prop5="";
                        s.events="event57";
                    } else {
                        s.pageName="Product Detail > " + omni_refinedNameProduct;
                        s.channel=omni_channel;
                        s.products="${omni_prod}".replace(/,(?=[^,]*$)/, '');
                        s.prop2=omni_prop2Var;
                        s.prop3=omni_prop3Var;
                        s.prop5="${omniProp5}";
                        if (productOOS && !collectionPDP) {
                            		s.events="event57,event17,event81";
                        } else {
                        		s.events="event57,event81";
                        }
                    }
                    fixOmniSpacing();
                    s.t();
                    BBB.omnitureObj.restore_s("eVar1,eVar2,eVar3,eVar4,eVar5,eVar6,eVar7,eVar8,eVar47,productNum,prop7,prop8,prop25");
                }
			</script>
		</c:when>
		<c:otherwise>
				<script type="text/javascript">
                var productNotfound = ${productNotfound},
                productOOS = ${productOOS},
                collectionPDP = ${collection};
                var BBB = BBB || {};
                var omni_channel = '${fn:replace(fn:replace(channel,'\'',''),'"','')}';
                var omni_refinedNameProduct = '${fn:replace(fn:replace(refinedNameProduct,'\'',''),'"','')}';
                var omni_prop2Var = '${fn:replace(fn:replace(prop2Var,'\'',''),'"','')}';
                var omni_prop3Var = '${fn:replace(fn:replace(prop3Var,'\'',''),'"','')}';

                if (typeof s !== "undefined") {
                    /* NOTE: (mbhatia3) Not an elegant solution for "UAT-591" ... but Omniture bbb_scode is VERY **smart** */
                    BBB.omnitureObj.backup_s("eVar1,eVar2,eVar3,eVar4,eVar5,eVar6,eVar7,eVar8,eVar47,productNum,prop7,prop8,prop25");
                    BBB.omnitureObj.clear_s("eVar3");
                    BBB.omnitureObj.delete_s("eVar1,eVar2,eVar4,eVar5,eVar6,eVar7,eVar8,eVar47,productNum,prop7,prop8,prop25");
                    s.prop1="Product Details Page";
					s.prop12="Product Quick View";
                    if(productNotfound){
                        s.pageName="product not available";
                        s.channel="Error page";
                        s.products="${omni_prod}".replace(/,(?=[^,]*$)/, '');
                        s.prop2="Error page";
                        s.prop3="Error page";
                        s.prop4="";
                        s.prop5="";
                        s.events="event57";
                        alert(2);
                    } else {
                        s.pageName="Product Detail > " + omni_refinedNameProduct;
                        s.channel=omni_channel;
                        s.products="${omni_prod}".replace(/,(?=[^,]*$)/, '');
                        s.prop2=omni_prop2Var;
                        s.prop3=omni_prop3Var;
                        s.prop5="${omniProp5}";
                        if (productOOS && !collectionPDP) {
                            		s.events="event57,event17";
                        } else {
                        		s.events="event57";
                        }
                    }
                    fixOmniSpacing();
                    s.t();
                    BBB.omnitureObj.restore_s("eVar1,eVar2,eVar3,eVar4,eVar5,eVar6,eVar7,eVar8,eVar47,productNum,prop7,prop8,prop25");
                }
			</script>
		</c:otherwise>
	</c:choose>
			<%-- R2.2 Omniture Implementation End --%>

				<%-- Certona integration Starts--%>
				<c:if test="${not frmComparisonPage}">
					<c:if test="${productVO.collection and not empty productVO.childProducts }">
						<c:forEach items="${productVO.childProducts }" var="childProductCertona" varStatus="count">
							<c:choose>
								<c:when test="${count.count eq 1 }">
									<c:set var="childProductsCertonaPlk" value="${childProductCertona.productId }"/>
								</c:when>
								<c:otherwise>
									<c:set var="childProductsCertonaPlk" value="${childProductsCertonaPlk };${childProductCertona.productId }"/>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</c:if>
				</c:if>
				</div>
				<input class="hidden getSkuIdComp" value="${pDefaultChildSku}" />
				<input type='hidden' id="childProductsCertonaPlk" value="${childProductsCertonaPlk }"/>
				<div id="botCrossSell" class="clearfix quickViewCertona loadAjaxContent" role="complementary" data-ajax-url="${contextPath}/browse/quickView_certona_slots.jsp" data-ajax-params-count="6"
					data-ajax-param1-name="parentProductId" data-ajax-param1-value="${parentProdId}" data-ajax-param2-name="productId" data-ajax-param2-value="${productId}"
					data-ajax-param3-name="linkStringNonRecproduct" data-ajax-param3-value="${parentProdId}" data-ajax-param4-name="OutOfStockOn" data-ajax-param4-value="true" data-ajax-param5-name="inStock" data-ajax-param5-value="${skuinStock}" data-ajax-param6-name="isMultiSku" data-ajax-param6-value="${isMultiSku}">
				</div>
				<%-- Certona integration Ends--%>
</dsp:page>
