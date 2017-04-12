<%-- R2.2 Story - 178 Product Comparison Page | Start--%>
<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductListHandler"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BreadcrumbDroplet"/>
	<dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<c:set var="enableKatoriFlag" scope="request"><bbbc:config key="enableKatori" configName="EXIMKeys"/></c:set>
	<input type="hidden" id="enableKatoriFlag" name="enableKatoriFlag" value="${enableKatoriFlag}">
	<c:set var="AttributePDPCollection">
		<bbbl:label key='lbl_pdp_attributes_collection' language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="omniPersonalizeButtonClick">
	<bbbl:label key='omni_personalize_button_click' language="${pageContext.request.locale.language}" />
   </c:set>	
	<c:set var="scene7Path">
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
	<c:set var="AttributePDPTOP">
			<bbbl:label key='lbl_pdp_attributes_top' language="${pageContext.request.locale.language}" />
		</c:set>
		<c:set var="AttributePDPMiddle">
			<bbbl:label key='lbl_pdp_attributes_middle' language="${pageContext.request.locale.language}" />
		</c:set>
		<c:set var="AttributePDPPrice">
			<bbbl:label key='lbl_pdp_attributes_price' language="${pageContext.request.locale.language}" />
		</c:set>
	<c:set var="lblReviewsCount"><bbbl:label key="lbl_reviews_count" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblReviewCount"><bbbl:label key="lbl_review_count" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblSaveThe"><bbbl:label key="lbl_accessibility_save_the" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblFuturePurchase"><bbbl:label key="lbl_accessibility_future_purchase" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblFindThe"><bbbl:label key="lbl_accessibility_find_the" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblStoreNear"><bbbl:label key="lbl_accessibility_store_near_you" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblForThe"><bbbl:label key="lbl_accessibility_for_the" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="shippingAttributesList">
		<bbbc:config key="shippingAttributesList" configName="ContentCatalogKeys" />
	</c:set>
	<c:choose>
		<c:when test="${currentSiteId eq BedBathUSSite}">
			<c:set var="MapQuestOn" scope="request">
				<tpsw:switch tagName="MapQuestTag_us" />
			</c:set>
			<c:set var="OutOfStockOn"><tpsw:switch tagName="OutOfStockTag_us"/></c:set>
		</c:when>
		<c:when test="${currentSiteId eq BuyBuyBabySite}">
			<c:set var="MapQuestOn" scope="request">
				<tpsw:switch tagName="MapQuestTag_baby" />
			</c:set>
			<c:set var="OutOfStockOn"><tpsw:switch tagName="OutOfStockTag_baby"/></c:set>
		</c:when>
		<c:otherwise>
			<c:set var="MapQuestOn" scope="request">
				<tpsw:switch tagName="MapQuestTag_ca" />
			</c:set>
			<c:set var="OutOfStockOn"><tpsw:switch tagName="OutOfStockTag_ca"/></c:set>
		</c:otherwise>
	</c:choose>
	<c:choose>
		<c:when test="${currentSiteId eq BedBathUSSite}">
			<c:set var="BazaarVoiceOn" scope="request">
				<tpsw:switch tagName="BazaarVoiceTag_us" />
			</c:set>
		</c:when>
		<c:when test="${currentSiteId eq BuyBuyBabySite}">
			<c:set var="BazaarVoiceOn" scope="request">
				<tpsw:switch tagName="BazaarVoiceTag_baby" />
			</c:set>
		</c:when>
		<c:when test="${currentSiteId eq BedBathCanadaSite}">
			<c:set var="BazaarVoiceOn" scope="request">
				<tpsw:switch tagName="BazaarVoiceTag_ca" />
			</c:set>
		</c:when>

	</c:choose>
	
		<%-- BBBI-3048 Omniture Tagging Start--%>
			
		<dsp:getvalueof var="vendorParam" bean="SessionBean.vendorParam"/>
		<dsp:getvalueof var="fromPage" param="fromPage"/>
		<c:set var="fireEvent90" value="false"/>
		<c:choose>
			<c:when test="${not empty sessionScope.boostCode && sessionScope.boostCode != '00'}">
				<c:set var="l2l3BoostFlag"><bbbc:config key="L2L3BoostFlag" configName="FlagDrivenFunctions"/></c:set> 
				<c:set var="brandsBoostFlag"><bbbc:config key="BrandsBoostFlag" configName="FlagDrivenFunctions"/></c:set> 
				<c:set var="keywordBoostFlag"><bbbc:config key="KeywordBoostFlag" configName="FlagDrivenFunctions"/></c:set> 
				<c:if test="${(((keywordBoostFlag && fromPage == 'searchPage') || (brandsBoostFlag && fromPage == 'brandsPage') || (l2l3BoostFlag && fromPage == 'categoryPage'))
							&& (isEndecaControl eq false))}">
		    		<c:set var="fireEvent90" value="true"/>
		    	</c:if>       
			</c:when>
			<c:when test="${(not empty vendorParam) && (fromPage == 'searchPage')}">
				<c:set var="fireEvent90" value="true"/> 
			</c:when>
		</c:choose>
			<%-- BBBI-3048 Omniture Tagging End--%>
	
	<c:set var="productNotfound" value="false"/>
		<c:set var="productOOS" value="false"/>
	<dsp:getvalueof var="frmComparisonPage" param="prodToCompare"/>
	<dsp:getvalueof var="categoryId" value="${fn:escapeXml(param.categoryId)}" />
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${appid}"/>
	<dsp:getvalueof var="strategyName" value="${fn:escapeXml(param.strategyName)}"/>

	<c:set var="count" value="1"/>
	<c:set var="countOmni" value="1" />
	<dsp:getvalueof var="parentProductId" value="${fn:escapeXml(param.prodId)}" />
	<c:if test="${parentProductId ne null}">
	<dsp:droplet name="BreadcrumbDroplet">		
						<dsp:param name="productId" value="${parentProductId}" />
						<dsp:param name="siteId" value="${currentSiteId}"/>
							<dsp:oparam name="output">
								<dsp:getvalueof param="isPrimaryCat" var="isPrimaryCat"/>
								<dsp:getvalueof param="isOrphanProduct" var="isOrphanProduct"/>
								<dsp:getvalueof var="bts" param="bts"/>
								 <dsp:droplet name="/atg/dynamo/droplet/ForEach">
							     <dsp:param name="array" param="breadCrumb"/>
								<dsp:oparam name="output">
								
									<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
										<dsp:param name="id" param="element.categoryId" />
										<dsp:param name="itemDescriptorName" value="category" />
										<dsp:param name="repositoryName"
											value="/atg/commerce/catalog/ProductCatalog" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="catName" param="element.categoryName" />
											<c:set var="categoryPath" scope="request">${categoryPath} > <dsp:valueof param="element.categoryName" /></c:set>	
											<c:set var="phantomCategory" scope="request"><dsp:valueof param="element.phantomCategory" /></c:set>
											<c:if test="${count eq '1'}">
												<c:set var="rootCategory" scope="request"><dsp:valueof param="element.categoryId" /></c:set>
												<c:set var="rootCategoryName" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
												<c:set var="count" value="${count+1}" />
											</c:if>
											<c:if test="${countOmni eq '2'}">
												<c:set var="categoryNameL1" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
											</c:if>			
											<c:if test="${countOmni eq '3'}">
												<c:set var="categoryNameL2" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
											</c:if>
											<c:set var="countOmni" value="${countOmni+1}" />
										</dsp:oparam>
									</dsp:droplet>
								</dsp:oparam>
							</dsp:droplet>
						</dsp:oparam>
					</dsp:droplet>
				</c:if>
	<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
		<dsp:param name="id" value="${parentProductId}" />
		<dsp:param name="itemDescriptorName" value="product" />
		<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="collectionFinalUrl" vartype="java.lang.String" param="url" />
		</dsp:oparam>
	</dsp:droplet>
	<dsp:getvalueof var="isMultiRollUpFlag" param="isMultiRollUpFlag" />
	<c:choose>
		<c:when test="${isMultiRollUpFlag eq 'true'}">
			<dsp:getvalueof var="color" value="${fn:escapeXml(param.selectedRollUpValue)}" />
		</c:when>
		<c:otherwise>
			<dsp:getvalueof var="skuId" value="${fn:escapeXml(param.selectedRollUpValue)}" />
		</c:otherwise>										
	</c:choose>
	
	
	
	<dsp:droplet name="ProductDetailDroplet">
		<dsp:param name="siteId" value="${appid}" />
		<dsp:param name="id" param="prodId" />
		<dsp:param name="color" value="${color}" />
		<dsp:param name="skuId" value="${skuId}" />
		<dsp:oparam name="output">
		<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductListHandler"/>
			<div class="collectionItemsBox clearfix productComparisonListView" id="shopCollection">
			
			
			
				<dsp:form method="post" id="compareForm" iclass="hidden">
				    <input type="hidden"  name="collectionProduct" value="${parentProductId}">
					<input type="hidden" name="prodIdToCompare" val="">
					<input type="hidden" name="skuIdToCompare" val="">
				<dsp:input type="submit" bean="ProductListHandler.addProduct" name="submitCompareForm" priority="-200" value="AddToCompare"/>
				</dsp:form>
				<dsp:form method="post" action="/_ajax/add_to_cart.json" name="collectionForm" id="collectionForm">
					<c:if test = "${not empty categoryId}">
						<c:set var = "collectionFinalUrl" value = "${collectionFinalUrl}?categoryId=${categoryId}"/> 
					</c:if>
					<h2>
						<bbbl:label key='lbl_compare_collection' language="${pageContext.request.locale.language}" />
						<span class="visitCollection"><dsp:a page="${collectionFinalUrl}"><bbbl:label key='lbl_qview_visit_collection' language="${pageContext.request.locale.language}" /></dsp:a></span>
					</h2>
						<dsp:getvalueof var="productVO" param="productVO" />
						<dsp:getvalueof var="refinedNameProduct" param="productVO.refinedName" scope="request" />
					<ul class="clearfix productRowContainer compareModalIE"> 
					<dsp:droplet name="/atg/dynamo/droplet/ForEach">
					<dsp:param param="collectionVO.childProducts" name="array" />
						<dsp:oparam name="output">
						<dsp:getvalueof var="childSKU" param="element.childSKUs"/>
						<dsp:getvalueof var="ltlProductFlag" param="element.ltlProduct"/>
						<c:if test="${not empty childSKU}">
							<li class="bdrBot collectionItems clearfix"> 
							<fieldset itemtype="http://schema.org/Product" itemscope="" class="registryDataItemsWrap listDataItemsWrap "> 
							<dsp:getvalueof var="prodImage" param="element.productImages.smallImage"/>
							<dsp:getvalueof var="prodName" param="element.name"/> 
							<dsp:getvalueof var="productID" param="element.productId"/>	
							<dsp:getvalueof var="prodThumbImage" param="element.productImages.thumbnailImage"/>
							<dsp:getvalueof var="intlRestricted" param="element.intlRestricted"/>
							<dsp:getvalueof var="displaySizeAsSwatch" param="element.displaySizeAsSwatch"/>
							<c:set var="omni_prod" value="${omni_prod};${productID};;;;eVar29=${parentProductId},"/>
							
							 <c:choose>
                                <c:when test="${empty prodThumbImage}">
                                    <img class="hidden productImage" height="83" width="83" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${prodName}" />
                                </c:when>
                                <c:otherwise>
                                    <img class="hidden productImage noImageFound" height="83" width="83" src="${scene7Path}/${prodThumbImage}" alt="${prodName}" />
                                </c:otherwise>
                            </c:choose>
							<dsp:getvalueof var="skuAttributes" value=""/>
							<dsp:getvalueof var="productAttributes" param="element.attributesList"/>
							<dsp:getvalueof var="hasRebate" value=""/>
							<dsp:getvalueof var="eligibleRebates" value=""/>
							<dsp:getvalueof var="skuid" value=""/>
							<dsp:getvalueof var="bopusAllowed" value=""/>
							<dsp:getvalueof var="emailStockAlertsEnabled" value="" />
							<dsp:getvalueof var="skuinStock" value="" />
							<dsp:getvalueof var="selectedSKUStock" value="" />
							<dsp:getvalueof var="colorMatched" param="element.colorMatched"/>
							<dsp:getvalueof var="skuVO" value="${null}"/>
							<dsp:getvalueof var="isCustomizationRequired" value="${null}"/>
							
							<c:if test="${(null != childSKU) && ((fn:length(childSKU) == 1 ) || ((fn:length(childSKU) > 1 ) && not empty color && colorMatched))}"> 
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param name="array" value="${childSKU}" />
									<dsp:oparam name="output">		
										<dsp:getvalueof var="childSkuId" param="element"/>
										<dsp:droplet name="ProductDetailDroplet">
										    <dsp:param name="id" value="${productID}" />
											<dsp:param name="siteId" param="siteId"/>
											<dsp:param name="skuId" value="${childSkuId}"/>
												<dsp:oparam name="output">
												<dsp:getvalueof var="skuColor" param="pSKUDetailVO.color"/>
												<dsp:getvalueof var="priceLabelCodeSKU" param="pSKUDetailVO.pricingLabelCode" />						
												<dsp:getvalueof var="inCartFlagSKU" param="pSKUDetailVO.inCartFlag" />
													<c:if test="${(fn:length(childSKU) == 1) || (not empty color and color eq skuColor)}">
														<dsp:getvalueof var="selectedSKUTemp" param="selected"/>
														<c:if test="${not empty selectedSKUTemp}">
															<c:set var="selectedSKU" value="${selectedSKUTemp}"/>
															<dsp:getvalueof var="selectedSKUStock" param="inStock" />
														</c:if>
														<c:if test="${not empty color and color eq skuColor}">
															<dsp:getvalueof var="prodName" param="pSKUDetailVO.displayName"/>
														</c:if>
														<dsp:getvalueof var="skuAttributes" param="pSKUDetailVO.skuAttributes"/>
														<dsp:getvalueof var="hasRebate" param="pSKUDetailVO.hasRebate"/>
														<dsp:getvalueof var="eligibleRebates" param="pSKUDetailVO.eligibleRebates"/>
														<dsp:getvalueof var="skuid" param="pSKUDetailVO.skuId"/>
														<dsp:getvalueof var="isLtlItem" param="pSKUDetailVO.ltlItem"/> <%-- LTL 99  --%>
														<dsp:getvalueof var="bopusAllowed" param="pSKUDetailVO.bopusAllowed"/>
														<dsp:getvalueof var="emailStockAlertsEnabled" param="pSKUDetailVO.emailStockAlertsEnabled" />
														<dsp:getvalueof var="skuinStock" param="inStock" />
													</c:if>
													<dsp:getvalueof var="isCustomizationRequired" param="pSKUDetailVO.customizableRequired"/>
													<dsp:getvalueof var="skuVO" param="pSKUDetailVO" />
													<dsp:getvalueof var="shippingRestricted" value="${skuVO.shippingRestricted}"/>
												</dsp:oparam>
											</dsp:droplet>
										</dsp:oparam>
									</dsp:droplet>
								</c:if>
								<!-- Display product attributes if MSWP else sku attributes if SSWP - Start -->
								<c:choose>
									<c:when test="${not empty skuAttributes}">
										<c:set var="attributeList" value= "${skuAttributes}"/>
									</c:when>
									<c:otherwise>
										<c:set var="attributeList" value="${productAttributes}"/>
									</c:otherwise>
								</c:choose>
								<!-- Display product attributes if MSWP else sku attributes if SSWP - End -->
								<dsp:getvalueof var="pDefaultChildSku" value="${skuid}" />
								
								<c:set var="collFirstChildSKU">
										<c:out value="${childSKU[0]}" />  
								</c:set>	
								<c:if test="${not empty collFirstChildSKU && count eq 1}">
									<div id="firstproduct" style="display:none">
										LT_F_PRD_ID:=${productID}
										LT_F_SKU_ID:=${collFirstChildSKU}
									</div>
									<c:set var="count" value="2"/>								
								</c:if>
								
								<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
									<dsp:param name="id" value="${productID}" />
									<dsp:param name="itemDescriptorName" value="product" />
									<dsp:param name="repositoryName"
										value="/atg/commerce/catalog/ProductCatalog" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
											param="url" />								
									</dsp:oparam>
								</dsp:droplet>
								<%-- <c:set var="finalUrl" value="${pageContext.request.contextPath}${finalUrl}"></c:set> --%>
								<dsp:getvalueof var="CategoryId" value="${fn:escapeXml(param.categoryId)}"/>
								<div class="grid_2 alpha"> 
									<c:choose>
										<c:when test="${not empty CategoryId}">
											<dsp:a iclass="prodImg" page="${finalUrl}?poc=${parentProductId}&categoryId=${CategoryId}&color=${color}&skuId=${skuid}&fromPage=${fromPage}" title="${prodName}">	
												<dsp:getvalueof var="showImage" param="collectionVO.showImagesInCollection"/>
												<c:if test="${showImage}">
													<c:choose>
														<c:when test="${empty prodThumbImage}">
															<img src="${imagePath}/_assets/global/images/no_image_available.jpg" width="146" height="146" alt="${prodName}"  />
														</c:when>
														<c:otherwise>
															<img class="noImageFound" src="${scene7Path}/${prodThumbImage}" width="146" height="146" alt="${prodName}"  />
														</c:otherwise>
													</c:choose>
												</c:if>
											</dsp:a>
										</c:when>
										<c:otherwise>
											<dsp:a iclass="prodImg" page="${finalUrl}?poc=${parentProductId}&color=${color}&skuId=${skuid}&fromPage=${fromPage}" title="${prodName}">	
												<dsp:getvalueof var="showImage" param="collectionVO.showImagesInCollection"/>
												<c:if test="${showImage}">								
													<c:choose>
													<c:when test="${empty prodThumbImage}">
														<img src="${imagePath}/_assets/global/images/no_image_available.jpg" width="146" height="146" alt="${prodName}"  />
													</c:when>
													<c:otherwise>
														<img class="noImageFound" src="${scene7Path}/${prodThumbImage}" width="146" height="146" alt="${prodName}"  />
													</c:otherwise>
													</c:choose>
												</c:if>
											</dsp:a>
										</c:otherwise>
									</c:choose>
								</div>
								
								<dsp:getvalueof var="oosProdId" value="${productID}" />
								<c:choose>
									<c:when test="${not empty selectedSKUStock}">
										<c:set var="inStock" value="${selectedSKUStock}" />
									</c:when>
									<c:when test="${empty skuinStock}">
										<c:set var="inStock" value="true" />
									</c:when>
									<c:otherwise>
										<dsp:getvalueof var="inStock" value="${skuinStock}" />
									</c:otherwise>
								</c:choose>	
						<c:if test="${inStock==false}">
                                            <c:set var="productOOS" value="true"/>
                        </c:if>	
								<div class="grid_8 omega clearfix listItemRowData">
									<div class="clearfix prodDetail">
										<div class="grid_5_half alpha fl">
											<ul class="prodInfo"> 
												<li class="prodName">
													<c:choose>
														<c:when test="${not empty CategoryId}">
															<dsp:a page="${finalUrl}?poc=${parentProductId}&categoryId=${CategoryId}&color=${color}&skuId=${skuid}&fromPage=${fromPage}" title="${prodName}">
																${prodName}
															</dsp:a>
														</c:when>
														<c:otherwise>
															<dsp:a page="${finalUrl}?poc=${parentProductId}&color=${color}&skuId=${skuid}&fromPage=${fromPage}" title="${prodName}">
																${prodName}
															</dsp:a>
														</c:otherwise>
													</c:choose>
												</li>
												</ul> 
											<input type="hidden" value="${inStock}" class="isInStock"/> 
											
										</div>
										
										<div class="grid_2_half alpha fl">
										    
											<div class="priceQuantityNotAvailable alpha omega fr <c:if test="${inStock==true}">hidden</c:if> clearfix">
												<div class="message fr"> 
													 <input type="hidden" name="oosProdId" value="${oosProdId}" />
													<c:choose>
														<c:when test="${inStock==true}">
															<input type="hidden" value="" name="oosSKUId" class="_oosSKUId"/>
														</c:when>
														<c:otherwise>
															<input type="hidden" value="${skuid}" name="oosSKUId" class="_oosSKUId"/>
														</c:otherwise>
													</c:choose>
												 	<div class="error textRight"><bbbl:label key='lbl_pdp_product_notavailable_shipping' language="${pageContext.request.locale.language}" /></div>
											   		<c:if test="${OutOfStockOn}">
												   		<c:choose>
															<c:when test="${(null eq emailStockAlertsEnabled) || (emailStockAlertsEnabled==true) || (inStock==false)}">
																<div class="info"><a class="info fr lnkNotifyOOS" href="#"><bbbl:label key='lbl_pdp_product_notify_item_available' language="${pageContext.request.locale.language}" /> &raquo;</a></div>
													 		</c:when>
													 		<c:otherwise>
																<div class="info hidden"><a class="info fr lnkNotifyOOS" href="#"><bbbl:label key='lbl_pdp_product_notify_item_available' language="${pageContext.request.locale.language}" /> &raquo;</a></div>
													 		</c:otherwise>
												  		</c:choose>
											 		</c:if>
												</div>
											</div> 
											
											<div class="fr prodAttributeContainer marLeft_10 <c:if test="${inStock==false}">hidden</c:if>">
												<div class="rebateContainer collectionRebateContainer prodAttribWrapper fr">
													<c:set var="rebatesOn" value="${false}" />
													<c:if test="${not empty hasRebate && hasRebate}">
														<c:if test="${(null != eligibleRebates) && (fn:length(eligibleRebates) == 1 )}"> 
															<c:set var="rebatesOn" value="${true}" />
														</c:if>
													</c:if>	
													<c:set var="showShipCustomMsg" value="true"/>
													<c:if test="${not empty attributeList}">
													<dsp:droplet name="/atg/dynamo/droplet/ForEach">
														<dsp:param value="${attributeList}" name="array" />
															<dsp:oparam name="output">
															<dsp:getvalueof var="placeHolder" param="key"/>
															<c:if test="${(placeHolder != null) && (placeHolder eq AttributePDPCollection)}">
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
																	<dsp:getvalueof var="attributeDescripTop" param="element.attributeDescrip"/>
																	<dsp:getvalueof var="attrId" param="element.attributeName" />
																	<c:if test="${fn:contains(shippingAttributesList,attrId)}">
																		<c:set var="showShipCustomMsg" value="false"/>
																	</c:if>
																	<dsp:getvalueof var="imageURLTop" param="element.imageURL"/>
																	<dsp:getvalueof var="actionURLTop" param="element.actionURL"/>
																	<c:choose>
																		 <c:when test="${null ne attributeDescripTop}">
																			<c:choose>
																			   <c:when test="${null ne imageURLTop}">
																					<span class="attribs  ${sep}"><img src="${imageURLTop}" alt="" /><span class="clearfix"><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></span>
																			   </c:when>
																			   <c:otherwise>
																				 <c:choose>
																					 <c:when test="${null ne actionURLTop}">
																						<span class="attribs  ${sep}"><a href="${actionURLTop}" class="newOrPopup"><span class="clearfix"><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a></span>
																					 </c:when>
																					 <c:otherwise>
																						 <span class="attribs  ${sep}"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></span>
																					 </c:otherwise>
																				  </c:choose>
																			   </c:otherwise>
																			 </c:choose>
																		 </c:when>
																		 <c:otherwise>
																		   <c:if test="${null ne imageURLTop}">
																			   <c:choose>
																				  <c:when test="${null ne actionURLTop}">
																						<span class="attribs ${sep}"><a href="${actionURLTop}" class="newOrPopup"><img src="${imageURLTop}" alt="" /></a></span>
																				  </c:when>
																				  <c:otherwise>
																						<span class="attribs ${sep}"><img src="${imageURLTop}" alt="" /></span>
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
													</c:if>						
													<c:if test="${not empty hasRebate && hasRebate}">
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
																<span class="attribs ${sep1}" ><a href="${rebate.rebateURL}" class="links" target="_blank" title="Rebate"><c:out value="${rebate.rebateDescription}" escapeXml="false"/></a></span>
															</dsp:oparam>
														</dsp:droplet>
													</c:if>
													<div class="shippingRestrictionsLink <c:if test="${not shippingRestricted or empty shippingRestricted}">hidden</c:if>">
														<span class="prod-attrib prod-attrib-fly">
		  												<a class="shippingRestrictionsApplied" href="/store/cart/static/shipping_restrictions_applied.jsp" data-skuId="${skuid}" title="<bbbl:label key="lbl_shipping_restrictions_applied" language="${pageContext.request.locale.language}"/>">
		  												<bbbl:label key="lbl_shipping_restrictions_details" language="<c:out param='${language}'/>"/></a>
		  												</span>
      												</div>
												</div>										
											</div>
										</div>
									</div>
									<div class="width_8 clearfix">
										<div class="grid_3 alpha">
											<ul class="prodInfo">
												<dsp:getvalueof var="ReviewCountvar" param="element.bvReviews.totalReviewCount"/>
												<c:set var="ReviewCount">${ReviewCountvar}</c:set>

												<c:if test="${BazaarVoiceOn}">
													<dsp:getvalueof var="ratingAvailable" param="element.bvReviews.ratingAvailable"></dsp:getvalueof>
													<c:choose>
														<c:when test="${ratingAvailable == true}">
															<dsp:getvalueof var="fltValue" param="element.bvReviews.averageOverallRating"/>
															<dsp:getvalueof param="element.bvReviews.totalReviewCount" var="totalReviewCount"/>
															<c:choose>
																<c:when test="${totalReviewCount == 1}">
																	<li class="prodReviews clearfix metaFeedback marTop_5">
																	<span class="ratingTxt ratingsReviews  prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="visuallyhidden"><dsp:valueof param="element.bvReviews.ratingsTitle"/></span></span><span class="reviewTxt"><a href="${pageContext.request.contextPath}${finalUrl}?skuId=${catalogRefId}&categoryId=${CategoryId}&showRatings=true" role="link" aria-label="${ReviewCountvar} ${lblReviewCount} ${lblForThe} ${prodName}">
																		${ReviewCount} <bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" /></a></span></li>
																</c:when>
																<c:otherwise>
																	<li class="prodReviews clearfix marTop_5">
																	<span class="ratingsReviews prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/> ratingTxt"><span class="visuallyhidden"><dsp:valueof param="element.bvReviews.ratingsTitle"/></span></span><span class="reviewTxt"><a href="${pageContext.request.contextPath}${finalUrl}?skuId=${catalogRefId}&categoryId=${CategoryId}&showRatings=true" role="link" aria-label="${ReviewCountvar} ${lblReviewsCount} ${lblForThe} ${prodName}">
																		${ReviewCount} <bbbl:label key='lbl_reviews_count' language="${pageContext.request.locale.language}" /></a></span></li>
																</c:otherwise>
															</c:choose>	
														</c:when>
														<c:otherwise>
															<dsp:getvalueof var="fltValue" param="element.bvReviews.averageOverallRating"/>
															<dsp:getvalueof var="totalReviewCount" value="0"></dsp:getvalueof>
															<li class="prodReviews clearfix metaFeedback">																
																<span class="ratingsReviews prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/> ratingTxt"><span class="visuallyhidden"><dsp:valueof param="element.bvReviews.ratingsTitle"/></span></span><span class="reviewTxt"><a href="${pageContext.request.contextPath}${finalUrl}?skuId=${catalogRefId}&categoryId=${CategoryId}&showRatings=true" role="link" aria-label="${ReviewCountvar} ${lblReviewCount} ${lblForThe} ${prodName}">
																${ReviewCount} <bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" /></a></span></li>
														</c:otherwise>
													</c:choose>
												</c:if>	
											</ul>
										</div>
										
										<div class="omega fl dropWrapper">																	
											<div class="fl prodInfoContainer alpha omega listViewQtyWrapper swatchPickers">			
												<dsp:droplet name="/atg/dynamo/droplet/ForEach">
												<dsp:param param="element.rollupAttributes" name="array" />
												<dsp:param name="sortProperties" value="_key"/>
													<dsp:oparam name="output">
													<dsp:getvalueof var="prodType" param="key"/>
													<dsp:getvalueof var="sizeParam" param="size" />
													<c:choose>												
													<c:when test="${prodType eq 'SIZE'}">
														<div class="prodSize noMarRight fl noMarLeft swatchPicker">
															<label for="selProdSize"><bbbl:label key="lbl_sizes_dropdown" language ="${pageContext.request.locale.language}"/></label>
															<div class="swatches">
														<c:choose>
															<c:when test="${displaySizeAsSwatch eq 'true'}">
																<div id="selProdSize">
																<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																<dsp:param param="element" name="array" />
																
																	<dsp:oparam name="output">
																		<dsp:getvalueof var="elementValue" param="element.rollupAttribute"/>
																			<c:choose>
																			<c:when test="${(sizeParam != null) && ((sizeParam eq elementValue))}">
																				<c:set var="selectedClass" value="selected"/>
																				<a class="selProdSizeBox ${selectedClass}" tabindex="0">
																					<span><c:out value="${elementValue}" escapeXml="false"/></span>
																				</a>	
																			</c:when>
																			<c:otherwise>
																				<c:set var="selectedClass" value=""/>
																				<a class="selProdSizeBox ${selectedClass}" tabindex="0">
																					<span><c:out value="${elementValue}" escapeXml="false"/></span>
																				</a>	
																			</c:otherwise>
																			
																			</c:choose>
																	</dsp:oparam>
																</dsp:droplet>
																</div>
															</c:when>
															<c:otherwise>
																<select id="selProdSize" name="selProdSize" class="customSelectBoxCollection" aria-required="false" >
																	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																	<dsp:param param="element" name="array" />
																	<dsp:oparam name="outputStart">
																		<dsp:getvalueof var="size" param="size" />
																			<option value=""><bbbl:label key='lbl_pdp_select_size' language="${pageContext.request.locale.language}" /></option>
																		</dsp:oparam>
																		<dsp:oparam name="output">
																			<dsp:getvalueof var="elementValue" param="element.rollupAttribute"/>
																				<option value="${elementValue}"><c:out value="${elementValue}" escapeXml="false"/></option>
																		</dsp:oparam>
																	</dsp:droplet>
																</select>
															</c:otherwise>
														</c:choose>
															</div>
															<input type="hidden" name="prodSize" class="_prodSize addItemToRegis addItemToList" value="" />
														</div>
													</c:when>
													<c:otherwise>
														<c:choose>									
														<c:when test="${prodType eq 'FINISH'}">
															<div class="prodFinish fr noMarRight noMarLeft swatchPicker">
																<label><bbbl:label key="lbl_finish_dropdown" language ="${pageContext.request.locale.language}"/></label>
																<div class="pfinish">
																	<div id="selProdFinish" class="swatches" >
																		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																		<dsp:param param="element" name="array" />	
																				 <dsp:getvalueof var="size" param="size" />
																		<dsp:getvalueof var="count" param="count" />
																		<dsp:getvalueof var="colorParam" param="color" />	
																		    <c:choose>
																			   <c:when test="${count % 11 == 0}">   
																					<c:set var="marginClass" value="fl noMarRight"/>
																			   </c:when>
																				<c:otherwise>
																						<c:set var="marginClass" value="fl"/>
																				</c:otherwise>
																			</c:choose>
																			
																			<dsp:oparam name="output">
																				<dsp:getvalueof var="elementValue" param="element.rollupAttribute"/>
																					<dsp:getvalueof var="smallImagePath" param="element.smallImagePath"/>
																					<dsp:getvalueof var="thumbnailImagePath" param="element.thumbnailImagePath"/>
																					<dsp:getvalueof var="swatchImagePath" param="element.swatchImagePath"/>
																					<c:choose>
																						<c:when test="${(colorParam != null) && (colorParam eq elementValue)}">
																							<c:set var="selectedClass" value="selected"/>
																							<c:set var="colorMatched" value="${elementValue}"/>
																					  	</c:when>
																					  	<c:otherwise>
																							<c:set var="selectedClass" value=""/>
																					  	</c:otherwise>
																					</c:choose>		
																					<a data-attr="${elementValue}" title="${elementValue}" data-imgSrc="${scene7Path}/${swatchImagePath}" data-imgURLSmall="${scene7Path}/${smallImagePath}" data-imgURLThumb="${scene7Path}/${thumbnailImagePath}"class="${marginClass} ${selectedClass}" href="#">
																						<img width="30" height="30" src="${scene7Path}/${swatchImagePath}" alt="${elementValue}" title="${elementValue}">
																					</a>
																			</dsp:oparam>
																		</dsp:droplet>
																	</div>
																</div>
																<input type="hidden" name="prodFinish" class="_prodFinish addItemToRegis addItemToList" value="${finishValue}" />												
															</div>
														</c:when>
														<c:otherwise>
														<c:if test="${prodType eq 'COLOR'}">
														<fieldset>
					                                    <legend class="hidden"><bbbl:label key="lbl_colors_dropdown" language ="${pageContext.request.locale.language}"/></legend>
															 <div class="prodColor noMarRight fl noMarLeft swatchPicker">
																<label><bbbl:label key="lbl_colors_dropdown" language ="${pageContext.request.locale.language}"/></label>
																<span class="txtOffScreen radioTxtSelectLabel radioSel_Color" aria-live="assertive"></span>
																<div class="pcolor">
																	<ul id="selProdColor" class="swatches" role="radiogroup">
																		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																		<dsp:param param="element" name="array" />
																			<dsp:getvalueof var="count" param="count" />
																				<dsp:getvalueof var="size" param="size" />
																			<dsp:getvalueof var="colorParam" param="color" />
																				<c:choose>
																				   <c:when test="${count % 11 == 0}">   
																						<c:set var="marginClass" value="fl noMarRight"/>
																				   </c:when>
																					<c:otherwise>
																							<c:set var="marginClass" value="fl"/>
																					</c:otherwise>
																				</c:choose>
																			<dsp:oparam name="output">
																		    	<dsp:getvalueof var="elementValue" param="element.rollupAttribute"/>
																				<dsp:getvalueof var="smallImagePath" param="element.smallImagePath"/>
																				<dsp:getvalueof var="thumbnailImagePath" param="element.thumbnailImagePath"/>
																				<dsp:getvalueof var="swatchImagePath" param="element.swatchImagePath"/>
																	            <c:choose>
																						<c:when test="${(colorParam != null) && (colorParam eq elementValue)}">
																						<c:set var="selectedClass" value="colorswatch-overlay-selected"/>
																							<c:set var="colorMatched" value="${elementValue}"/>
																						<c:set var="ariaChecked" value="true" />
																					</c:when>
																					<c:otherwise>
																							<c:set var="selectedClass" value=""/>
																						<c:set var="ariaChecked" value="false" />
																					</c:otherwise>
																				</c:choose>	
																					
																					
																					<li tabindex="${count == 1 ? 0 : -1}" role="radio" aria-checked="${ariaChecked}" data-attr="${elementValue}" title="${elementValue}" data-imgSrc="${scene7Path}/${swatchImagePath}" data-imgURLSmall="${scene7Path}/${smallImagePath}" data-imgURLThumb="${scene7Path}/${thumbnailImagePath}" class="${marginClass} colorSwatchLi" href="#" aria-hidden="false">
																						<div class="colorswatch-overlay ${selectedClass}"></div>
																						<c:choose>
																							<c:when test="${(swatchImagePath != null) && (swatchImagePath ne '')}">
																								<img src="${scene7Path}/${swatchImagePath}" height="20" width="20" alt="${elementValue}"/>
																							</c:when>
																							<c:otherwise>
																								<img src="${imagePath}/_assets/global/images/blank.gif" height="20" width="20" alt="${elementValue}" class="noImageFound" />
																							</c:otherwise>
																						</c:choose>
																					</li>
																			</dsp:oparam>
																		</dsp:droplet>
																	</ul>
																</div>
																<input type="hidden" name="prodColor" class="_prodColor addItemToRegis addItemToList" value="${colorValue}" />											
															</div>
															</fieldset>
														
														</c:if>
														</c:otherwise>
														</c:choose>
													</c:otherwise>
													</c:choose>
													</dsp:oparam>
											</dsp:droplet>
											</div> 								
										</div>
									</div>
								</div>
								

								<div class="clearfix fr width_8">
								<div class="fl width270">
                                    <div itemprop="price" class="prodPrice fl width270">
											<c:set var="inputFieldPrice"></c:set>
											<dsp:getvalueof var="priceRangeDescription" param="element.priceRangeDescription" />
											<dsp:getvalueof var="salePriceRangeDescription" param="element.salePriceRangeDescription" />
											<dsp:getvalueof var="displayShipMsg" param="element.displayShipMsg"/>
											<c:choose>
												<c:when test="${not empty skuid}">
												<div class="isPrice">
													<dsp:include page="../browse/product_details_price.jsp">	
														<dsp:param name="product" value="${productID}"/>
														<dsp:param name="sku" value="${skuid}"/>
														<dsp:param name="priceLabelCodeSKU" value="${priceLabelCodeSKU}" />
														<dsp:param name="inCartFlagSKU" value="${inCartFlagSKU}" />
														<dsp:param name="isFromPDP" value="false" />
													</dsp:include>
													<c:set var="inputFieldPrice">${omniPrice}</c:set>
												</div>	
												</c:when>
												<c:otherwise>
												<dsp:getvalueof var="isdynamicPriceProd" param="element.dynamicPricingProduct" />
												<dsp:getvalueof var="priceLabelCodeProd" param="element.priceLabelCode" />
												<dsp:getvalueof var="inCartFlagProd" param="element.inCartFlag" />
												<dsp:getvalueof var="salePriceRangeDescription" param="element.salePriceRangeDescription" />
												<dsp:getvalueof var="priceRangeDescription" param="element.priceRangeDescription" />
										
												<dsp:include page="/browse/browse_price_frag.jsp">
													<dsp:param name="priceLabelCode" value="${priceLabelCodeProd}" />
													<dsp:param name="inCartFlag" value="${inCartFlagProd}" />
													<dsp:param name="salePrice" value="${salePriceRangeDescription}" />
													<dsp:param name="listPrice" value="${priceRangeDescription}" />
													<dsp:param name="isdynamicPriceEligible" value="${isdynamicPriceProd}" />
													<dsp:param name="isFromPDP" value="false" />
												</dsp:include>   
															
												</c:otherwise>
											</c:choose>
                                            <div class="freeShipBadge">
											<c:if test="${showShipCustomMsg}">
												${displayShipMsg}
											</c:if>
											</div>
										</div>
									<%--Katori integration on Product Compare page as a part of BPSI-2430 changes start --%>
										<c:set var="customizableCodes" value="${skuVO.customizableCodes}" scope="request"/>
										<c:set var="customizeCTACodes" scope="request">
											<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys" />
										</c:set>
										<c:choose>
											<c:when test="${not empty skuVO.customizableCodes && fn:contains(customizeCTACodes, skuVO.customizableCodes)}">
												<c:set var="personalizationTxt">
													<bbbl:label key="lbl_customize_this" language="${pageContext.request.locale.language}"/>
												</c:set>
												<c:set var="customizeAttr" value="customize"/>
											</c:when>
											<c:otherwise>
												<c:set var="personalizationTxt">
													<bbbl:label key="lbl_personalize_this" language="${pageContext.request.locale.language}"/>
												</c:set>
												<c:set var="customizeAttr" value=""/>
											</c:otherwise>
										</c:choose>
										<c:choose>
											<c:when test="${ (skuVO.customizableRequired || skuVO.customizationOffered) && not empty skuVO.customizableCodes && !isInternationalCustomer}">
												<div class="personalizationOffered clearfix cb fl">
													<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="${pageContext.request.contextPath}${finalUrl}?poc=${parentProductId}&color=${color}&openKatori=true&skuId=${skuid}"</c:otherwise></c:choose> class="collectionPersonalize ${customizeAttr}<c:if test="${not enableKatoriFlag}"> disabled </c:if>" role="button" data-product="${productId}" data-sku="${pDefaultChildSku}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
														${personalizationTxt}
													</a>
													<c:choose>
						       						<c:when test="${enableKatoriFlag}">
						       							<c:if test="${not empty skuVO.personalizationType && skuVO.personalizationType =='PB'}">
															<span class="feeApplied"><bbbl:label key='lbl_PB_Fee_detail' language="${pageContext.request.locale.language}" /></span>	
														</c:if>
														<c:if test="${not empty skuVO.personalizationType && skuVO.personalizationType =='PY'}">
														<span class="feeApplied">	<bbbl:label key='lbl_PY_Fee_detail' language="${pageContext.request.locale.language}" /></span>	
														</c:if>
														<c:if test="${not empty skuVO.personalizationType && skuVO.personalizationType =='CR'}">
														<span class="feeApplied">	<bbbl:label key='lbl_CR_Fee_detail' language="${pageContext.request.locale.language}" /></span>	
														</c:if>
														<span class="personalizationDetail">
															<bbbl:label key='lbl_cart_personalization_detail' language="${pageContext.request.locale.language}" />
														</span>
														<div class="personalizeToProceed <c:if test="!${isCustomizationRequired}"> hidden </c:if>" >
	                                                         <c:choose>
																<c:when test="${not empty skuVO.customizableCodes && fn:contains(customizeCTACodes, skuVO.customizableCodes)}">
																	<bbbl:label key='lbl_customization_required_msg' language="${pageContext.request.locale.language}" />
																</c:when>
																<c:otherwise>
																	<bbbl:label key='lbl_personalization_required_msg' language="${pageContext.request.locale.language}" />
																</c:otherwise>
															</c:choose>
	                                                       </div>
	                                                 </c:when>
													<c:otherwise>
														<span><bbbl:label key='lbl_pdp_personalization_unavailable' language="${pageContext.request.locale.language}" /></span>
													</c:otherwise>
													</c:choose>
													
												</div>
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${isInternationalCustomer}">
														<c:choose>
														<c:when test="${(skuVO.customizableRequired || skuVO.customizationOffered) && not empty skuVO.customizableCodes}">
															<div class="personalizationOffered clearfix cb fl">
																<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="#"</c:otherwise></c:choose> class="personalize ${customizeAttr} disabled" role="button" data-sku="${pDefaultChildSku}" data-product="${productId}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
																	${personalizationTxt}
																</a>
																<span class="personalizationIntlShippingMsg"><bbbt:textArea key="txt_pdp_per_unavailable_intl" language="${pageContext.request.locale.language}" /></span>
															</div> 
															
														</c:when>
														<c:otherwise>
															<div class="personalizationOffered clearfix cb fl hidden ">
																<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="#"</c:otherwise></c:choose> class="personalize ${customizeAttr} disabled" role="button" data-sku="${pDefaultChildSku}" data-product="${productId}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
																	${personalizationTxt}
																</a>
																<span class="personalizationIntlShippingMsg"><bbbt:textArea key="txt_pdp_per_unavailable_intl" language="${pageContext.request.locale.language}" /></span>
															</div>
														
														</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
													<div class="personalizationOffered clearfix cb fl hidden ">
														<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="${pageContext.request.contextPath}${finalUrl}?poc=${parentProductId}&color=${color}&openKatori=true&skuId=${skuid}"</c:otherwise></c:choose> class="collectionPersonalize ${customizeAttr} <c:if test="${not enableKatoriFlag}"> disabled </c:if>" role="button" data-sku="${pDefaultChildSku}" data-product="${productId}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
															${personalizationTxt}
														</a>
														<c:choose>
						      								 <c:when test="${enableKatoriFlag}">
						      								 	<span class="feeAppliedPB hidden"><bbbl:label key='lbl_PB_Fee_detail' language="${pageContext.request.locale.language}" /></span>	
																<span class="feeAppliedPY hidden">	<bbbl:label key='lbl_PY_Fee_detail' language="${pageContext.request.locale.language}" /></span>	
																<span class="feeAppliedCR hidden"><bbbl:label key='lbl_CR_Fee_detail' language="${pageContext.request.locale.language}" /></span>	
																<span class="personalizationDetail">
																	<bbbl:label key='lbl_cart_personalization_detail' language="${pageContext.request.locale.language}" />
																</span>	
																<div class="personalizeToProceed <c:if test="!${isCustomizationRequired}"> hidden </c:if>" >
			                                                          <c:choose>
																		<c:when test="${not empty skuVO.customizableCodes && fn:contains(customizeCTACodes, skuVO.customizableCodes)}">
																			<bbbl:label key='lbl_customization_required_msg' language="${pageContext.request.locale.language}" />
																		</c:when>
																		<c:otherwise>
																			<bbbl:label key='lbl_personalization_required_msg' language="${pageContext.request.locale.language}" />
																		</c:otherwise>
																	</c:choose>
			                                                    </div>
		                                                    </c:when>
															<c:otherwise>
																<span><bbbl:label key='lbl_pdp_personalization_unavailable' language="${pageContext.request.locale.language}" /></span>
															</c:otherwise>
														</c:choose>		
													</div>
												</c:otherwise>
												</c:choose>
											</c:otherwise>
									</c:choose>
									<%--Katori integration on Product Compare page as a part of BPSI-2430 changes end --%>
										
										<c:if test="${(isInternationalCustomer && intlRestricted)}">	
											<div class="notAvailableIntShipMsg compListIntShipMsg cb padTop_15 clearfix"><bbbl:label key='lbl_pdp_intl_restricted_message' language="${pageContext.request.locale.language}" /></div>						
										</c:if>
										</div>
																	
										<div class="fr prodInfoContainer alpha omega selectCompareButton">
											<div class="btnPD clearfix <c:if test="${frmComparisonPage}">hidden</c:if>">
											  	<div class="fr">
											  	 <%-- Hide quantity spinner for LTL item LTL-1166 --%>
											  		<div class="quantityPDP fl <c:if test="${not empty skuid && not empty isLtlItem && isLtlItem}">hidden</c:if>">
													  <div class="spinner">
														  	<c:if test="${not frmComparisonPage}">
														  	    <label id="lblprodCompListDescQty" class="txtOffScreen" for="prodCompDescQty" aria-hidden="true"><bbbl:label key="lbl_decrease_quantity" language="${pageContext.request.locale.language}" /></label>
																<a href="#" class="scrollDown down" id="prodCompDescQty" title="<bbbl:label key='lbl_decrease_quantity' language="${pageContext.request.locale.language}" />"><span class="txtOffScreen"><bbbl:label key="lbl_decrease_quantity" language="${pageContext.request.locale.language}" /></span></a>
																<span class="incDec visuallyhidden" aria-live="assertive" aria-atomic="true"></span>
																<input id="quantity" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="fl addItemToRegis _qty itemQuantity addItemToList" type="text" role="textbox" name="qty" value="1" maxlength="2" data-change-store-submit="qty" data-change-store-errors="required digits nonZero" aria-required="true" aria-describedby="quantity" /> 
																<label id="lblprodCompIncQty" class="txtOffScreen" for="prodCompIncQty" aria-hidden="true"><bbbl:label key="lbl_increase_quantity" language="${pageContext.request.locale.language}" /></label>
                                								<a href="#" class="scrollUp up" id="prodCompIncQty" title="<bbbl:label key='lbl_increase_quantity' language="${pageContext.request.locale.language}" />"><span class="txtOffScreen"><bbbl:label key="lbl_increase_quantity" language="${pageContext.request.locale.language}" /></span></a>
															</c:if>
													  </div>
												  </div>
													<div class="fl marginButtonFix <c:if test="${not empty skuid && not empty isLtlItem && isLtlItem}">hidden</c:if>">
														<dsp:include page="../addgiftregistry/add_item_gift_registry.jsp">
															<dsp:param name="ltlProductFlag" value="${ltlProductFlag}"/>
															<dsp:param name="isCustomizationRequired" value="${isCustomizationRequired}"/>
														<input type="hidden" name="parentProdId" class="_prodId addItemToRegis addItemToList" value="${parentProductId}" />
														</dsp:include>	
													</div>			
                                                  <c:set var="button_disabled" value=""/>												   
													<c:if test="${isCustomizationRequired || inStock==false || (isInternationalCustomer && intlRestricted)}">
															<c:set var="button_disabled" value="button_disabled" />
													</c:if>
													
													<div class="fl addToCart <c:if test="${not empty skuid && not empty isLtlItem && isLtlItem}">hidden</c:if>">
														<div class="button button_active button_active_orange ${button_disabled}">
															<input type="submit" name="btnAddToCart" value="Add to Cart" onclick="rkg_micropixel('${appid}','cart')" role="button" <c:if test="${isCustomizationRequired || inStock==false || (isInternationalCustomer && intlRestricted)}">disabled="disabled"</c:if>/>
														</div>
													</div>
													<dsp:getvalueof var="regId" param="registryId" />
													<input type="hidden" name="registryId" class="sflRegistryId  addItemToRegis addItemToList" value="${regId}" data-change-store-submit="registryId" /> 
													<input type="hidden" name="prodId" class="_prodId temp addItemToRegis productId addItemToList" value="${productID}" data-change-store-submit="prodId" data-change-store-errors="required" data-change-store-internalData="true" /> 
													<input type="hidden" name="skuId" value="${pDefaultChildSku}" class="addItemToRegis _skuId addItemToList changeStoreSkuId" data-change-store-submit="skuId" data-change-store-errors="required" data-change-store-internalData="true" /> 
													<input type="hidden" name="price" value="${omniPrice}" class="addItemToList addItemToRegis" /> 
													<input type="hidden" class="addToCartSubmitData" name="storeId" value="" data-change-store-storeid="storeId" /> 
													<input type="hidden" class="addToCartSubmitData" name="bts" value="${bts}" data-change-store-storeid="bts" />
													<input type="hidden" name="parentProdId" class="_prodId addItemToRegis addItemToList" value="${parentProductId} " />
													<c:if test="${not empty regId}">
														<input type="hidden" class="addToCartSubmitData" name="registryId" value="${regId}" data-change-store-submit="registryId" />
													</c:if>
													
													 <%-- LTL Sku changes... If SKU has LTL shipping methods --%>
												  <c:choose>
													<c:when test="${not empty skuid && not empty isLtlItem && isLtlItem}">
														<div class="fr selectOption">
													</c:when>
													<c:otherwise>
														<div class="fr selectOption hidden">
													</c:otherwise>
												  </c:choose>
																					  
														<div class="button button_active">
															<c:set var="chooseOptionBtn"><bbbl:label key='lbl_pdp_grid_choose_options' language="${pageContext.request.locale.language}" /></c:set>
															<input type="button" class="showOptionMultiSku" name="btnSelectOption" id="btnSelectOption" value="${chooseOptionBtn}" role="button" aria-pressed="false" onclick=""/>
														</div>
													</div>
												<%-- LTL Sku changes... If SKU has LTL shipping methods --%>
																						
												</div>												
												<%-- R2.2.1 Story - 131 Quick View Page --%>
													<div class="btnPDLinks padTop_15 clearfix cb fr <c:if test="${not empty skuid && not empty isLtlItem && isLtlItem}">hidden</c:if>">
														<div class="addToList fl" id="btnAddToList">
														<%--Adding customization disable flag check for quick view --%>	
														     <c:choose>
	                                       						 <c:when test="${isCustomizationRequired}">
	                                       					  		<bbbl:label key='lbl_compare_save_for_later' language="${pageContext.request.locale.language}" />
	                                       						 </c:when>
																 <c:otherwise>
														         	 <a href="javascript:void(0);" onclick="rkg_micropixel('${appid}','wish')" role="link" aria-label="${lblSaveThe} ${prodName} ${lblFuturePurchase}"><bbbl:label key='lbl_compare_save_for_later' language="${pageContext.request.locale.language}" /></a>
														    	 </c:otherwise>
														     </c:choose>
													     </div>
														
														<c:if test="${MapQuestOn}">
															<div class="fl findInStore padLeft_30">
																
																<c:choose>
																	<c:when test="${bopusAllowed}">
																		<div class="disableText fr cb">
														 					<a href="javascript:void(0);" role="link" aria-disabled="true"><bbbl:label key='lbl_compare_find_in_store' language="${pageContext.request.locale.language}" /></a>
														 				</div>
																	</c:when>
																	<c:when test="${isInternationalCustomer eq true}">
																		<div class="fr cb disableText" role="link" aria-disabled="true">
														 					<bbbl:label key='lbl_compare_find_in_store' language="${pageContext.request.locale.language}" />
														 				</div>
																	</c:when>
																	<c:otherwise>							
																		<%--Adding customization disable flag check for quick view --%>	
																		     <c:choose>
					                                       						 <c:when test="${isCustomizationRequired}">
					                                       						 	<div class="fr cb disableText" role="link" aria-disabled="true">
					                                       					  			<bbbl:label key='lbl_compare_find_in_store' language="${pageContext.request.locale.language}" />
					                                       							</div>
					                                       						 </c:when>
																				 <c:otherwise>
																				 	<div class="changeStore fr cb">
																		         		<a href="javascript:void(0);" onclick="pdpOmnitureProxy('${productID}', 'findinstore');rkg_micropixel('${appid}','findstore');" role="link"  aria-label="${lblFindThe} ${prodName} ${lblStoreNear}"><bbbl:label key='lbl_compare_find_in_store' language="${pageContext.request.locale.language}" /></a>
																		    		</div>
																		    	 </c:otherwise>
																		     </c:choose>																										 				
																	</c:otherwise>
																</c:choose>
															</div>
														</c:if>
													</div>
													
													<%-- R2.2.1 Story - 131 Quick View Page --%>
										  </div>
										  <c:if test="${frmComparisonPage}">
										  <c:choose>
										       <c:when test="${empty skuid}">
											   <dsp:getvalueof var="buttonClass" value="button_disabled"/>
											 
											   </c:when>
											   <c:otherwise>
											    <dsp:getvalueof var="buttonClass" value="button_lightblue"/>
												
											   </c:otherwise>
										  </c:choose>
										  <div class="button ${buttonClass} selectToCompare">
												<input type="button" name="btnSelectToCompare" value="Select To Compare" role="button" aria-pressed="false" <c:if test="${empty skuid}">disabled="disabled"</c:if>/>
										  </div>
										  </c:if>
										</div> 
									</div>			
								</fieldset> 
							</li>
						</c:if>
					</dsp:oparam>
				</dsp:droplet>
										</ul>
                                     </dsp:form> 
									 
		<c:set var="DoubleClickOn" scope="request"><tpsw:switch tagName="DoubleClickTag_us" /></c:set>
		<c:set var="DoubleClickOn" scope="request"><tpsw:switch tagName="DoubleClickTag_baby" /></c:set>	
		<c:set var="DoubleClickOn" scope="request"><tpsw:switch tagName="DoubleClickTag_ca" /></c:set>		
		<%-- BBBSL-4343 DoubleClick Floodlight START  
	   <c:if test="${DoubleClickOn}">
	     <c:if test="${(currentSiteId eq BedBathUSSite)}">
    		   <c:set var="cat"><bbbc:config key="cat_product_bedBathUS" configName="RKGKeys" /></c:set>
    		   <c:set var="src"><bbbc:config key="src_bedBathUS" configName="RKGKeys" /></c:set>
    		   <c:set var="type"><bbbc:config key="type_1_bedBathUS" configName="RKGKeys" /></c:set>
    		 </c:if>
    		 <c:if test="${(currentSiteId eq BuyBuyBabySite)}">
    		   <c:set var="cat"><bbbc:config key="cat_product_baby" configName="RKGKeys" /></c:set>
    		   <c:set var="src"><bbbc:config key="src_baby" configName="RKGKeys" /></c:set>
    		   <c:set var="type"><bbbc:config key="type_1_baby" configName="RKGKeys" /></c:set>
    		 </c:if>
    		  <c:if test="${(currentSiteId eq BedBathCanadaSite)}">
		    		   <c:set var="cat"><bbbc:config key="cat_product_bedbathcanada" configName="RKGKeys" /></c:set>
		    		   <c:set var="src"><bbbc:config key="src_bedbathcanada" configName="RKGKeys" /></c:set>
		    		   <c:set var="type"><bbbc:config key="type_1_bedbathcanada" configName="RKGKeys" /></c:set>
		    		 </c:if>
			 		<dsp:include page="/_includes/double_click_tag.jsp">
			 			<dsp:param name="doubleClickParam" value="src=${src};type=${type};cat=${cat};u4=${parentProductId};u5=${refinedNameProduct};u10=${categoryId};u11=${rootCategoryName};u12=${categoryNameL1};u13=${categoryNameL2}"/>
			 		</dsp:include>
		 		</c:if>
		 		 DoubleClick Floodlight END --%>
                                </div>
				
			</dsp:oparam>
			<dsp:oparam name="error">
					<c:set var="productNotfound" value="true"/>
				</dsp:oparam>
		</dsp:droplet>
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
			 <dsp:getvalueof var="siteId" bean="Site.id" />
			
			<dsp:getvalueof var="categoryId" param="poc"/>
				
	        <c:forEach var="conMap" items="${configMap}">
			    <c:if test="${conMap.key eq appid}">
					<c:choose>
						<c:when test="${phantomCategory}">
							<c:set var="floatingNodes" value="Floating nodes" />
							<c:set var="prop2Var">${floatingNodes} > ${rootCategoryName}</c:set>
							<c:set var="rkgCategoryNames" value="${floatingNodes},${rootCategoryName}"/>
							<c:if test="${!empty categoryNameL1}">
							<c:set var="prop3Var">${floatingNodes} > ${rootCategoryName} > ${categoryNameL1}</c:set>
							<c:set var="rkgCategoryNames" value="${floatingNodes},${rootCategoryName},${categoryNameL1}"/>
							</c:if>
							<c:set var="channel">${floatingNodes}</c:set>
							<c:if test="${fn:contains(queryString, 'fromCollege')}">
								<c:set var="rootCategoryName" value="College" />
								<c:set var="prop3Var"> ${rootCategoryName} > ${rootCategoryName} > ${refinedNameProduct}</c:set>
								<c:set var="rkgCategoryNames" value="${rootCategoryName},${refinedNameProduct}"/>
							</c:if>
						</c:when>
						<c:otherwise>
							<c:choose>
							
								<c:when test="${(clearanceCategory == rootCategory) || (whatsNewCategory==rootCategory) }">
								<c:set var="prop2Var">${rootCategoryName} > ${rootCategoryName}</c:set>
								<c:set var="rkgCategoryNames" value="${rootCategoryName},${refinedNameProduct}"/>
								<c:set var="channel">${rootCategoryName}</c:set>
								<c:if test="${!empty categoryNameL1}">
									<c:set var="prop3Var">${rootCategoryName} > ${rootCategoryName} > ${categoryNameL1}</c:set>
									<c:set var="rkgCategoryNames" value="${rootCategoryName},${refinedNameProduct},${categoryNameL1}"/>
								</c:if>
								</c:when>
								<c:otherwise>
									<c:set var="queryString" value="${queryString}" />
									<c:choose>
									<c:when test="${!empty categoryNameL1}">
									<c:set var="prop2Var">${rootCategoryName} > ${categoryNameL1}</c:set>
									<c:set var="rkgCategoryNames" value="${rootCategoryName},${categoryNameL1}"/>
									</c:when>
									<c:otherwise>
									<c:set var="prop2Var">${rootCategoryName} > ${rootCategoryName}</c:set>
									<c:set var="rkgCategoryNames" value="${rootCategoryName}"/>
									</c:otherwise>
									</c:choose>
									<c:set var="channel">${rootCategoryName}</c:set>
									<c:if test="${!empty categoryNameL2}">
										<c:set var="prop3Var">${rootCategoryName} > ${categoryNameL1} > ${categoryNameL2}</c:set>
										<c:set var="rkgCategoryNames" value="${rootCategoryName},${categoryNameL1},${categoryNameL2}"/>
									</c:if>
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
					</c:if>
				</c:forEach>
								
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
				   var price = '${certonaPrice}';
				   totalPrice=(qty*price).toFixed(2);
				   var finalOut= ';'+'${fn:escapeXml(param.productId)}'+';;;event38='+'${fn:escapeXml(param.qty)}'+'|event39='+totalPrice+';eVar30='+'${fn:escapeXml(param.skuId)}';
				   additemtolist(finalOut);
			}
			</script>
			 <script type="text/javascript">
                var productNotfound = ${productNotfound};
              var  productOOS = ${productOOS};
                var BBB = BBB || {};
                var omni_channel = '${fn:replace(fn:replace(channel,'\'',''),'"','')}';
                var omni_refinedNameProduct = '${fn:replace(fn:replace(refinedNameProduct,'\'',''),'"','')}';
                var omni_prop2Var = '${fn:replace(fn:replace(prop2Var,'\'',''),'"','')}';
                var omni_prop3Var = '${fn:replace(fn:replace(prop3Var,'\'',''),'"','')}';
                var fireEvent90="${fireEvent90}";
                var evar1='${fn:escapeXml(strategyName)}';
                if (typeof s !== "undefined") {
                    BBB.omnitureObj.backup_s("eVar1,eVar2,eVar3,eVar4,eVar5,eVar6,eVar7,eVar8,eVar47,productNum,prop7,prop8,prop25");
                    BBB.omnitureObj.clear_s("eVar3");
                    BBB.omnitureObj.delete_s("eVar1,eVar2,eVar4,eVar5,eVar6,eVar7,eVar8,eVar47,productNum,prop7,prop8,prop25");
                    s.prop1="Product Details Page";
                    if(productNotfound){
                        s.pageName="product not available";
                        s.channel="Error page";
                        s.products="${omni_prod}".replace(/,(?=[^,]*$)/, '');
                        s.prop2="Error page";
                        s.prop3="Error page";
                        s.prop4="";
                        s.prop5="";
                    } else {
                        s.pageName="Product Detail > " + omni_refinedNameProduct;
                        s.channel=omni_channel;
                        s.products="${omni_prod}".replace(/,(?=[^,]*$)/, '');
                        s.prop2=omni_prop2Var;
                        s.prop3=omni_prop3Var;
                        s.prop5="Collection modal";
                        if(evar1!=='undefined' && evar1!==''){
                        	s.eVar1=evar1+'(personal store page)';
                        	s.eVar7='My Account>Personal Page';
                        	s.eVar8='unknown at time of referral';
                        }
                    }
                    s.events="event57";
                    if(fireEvent90 == 'true') {
                  		 s.events=s.events+',event90';
                  	 }
                   	s.eVar61="";
                    fixOmniSpacing();
                    s.t();
                    BBB.omnitureObj.restore_s("eVar1,eVar2,eVar3,eVar4,eVar5,eVar6,eVar7,eVar8,eVar47,productNum,prop7,prop8,prop25");
                }
			</script>
		<%-- BBBSL -3291   start --%>
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

				<script type="text/javascript">
					var resx = new Object();
					resx.appid = "${appIdCertona}";
					resx.event = "quickview_op";
					resx.itemid = "${parentProductId}";
					resx.pageid = "";
					resx.links = "${childProductsCertonaPlk}";
					if (typeof certonaResx === 'object') { certonaResx.run();  }
				</script>
				<input type='hidden' id="childProductsCertonaPlk" value="${childProductsCertonaPlk }"/>
			</c:if>
		<%-- BBBSL -3291  End--%>
</dsp:page>
<%-- R2.2 Story - 178 Product Comparison Page | End--%>