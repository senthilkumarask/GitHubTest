<%-- R2.2 Story - 178 Product Comparison Page | Start--%>
<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductListHandler"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BreadcrumbDroplet"/>
	<c:set var="AttributePDPCollection">
		<bbbl:label key='lbl_pdp_attributes_collection' language="${pageContext.request.locale.language}" />
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
	
	<c:choose>
		<c:when test="${currentSiteId eq BedBathUSSite}">
			<c:set var="MapQuestOn" scope="request">
				<tpsw:switch tagName="MapQuestTag_tbs_us" />
			</c:set>
			<c:set var="OutOfStockOn"><tpsw:switch tagName="OutOfStockTag_us"/></c:set>
		</c:when>
		<c:when test="${currentSiteId eq BuyBuyBabySite}">
			<c:set var="MapQuestOn" scope="request">
				<tpsw:switch tagName="MapQuestTag_tbs_baby" />
			</c:set>
			<c:set var="OutOfStockOn"><tpsw:switch tagName="OutOfStockTag_baby"/></c:set>
		</c:when>
		<c:otherwise>
			<c:set var="MapQuestOn" scope="request">
				<tpsw:switch tagName="MapQuestTag_tbs_ca" />
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
	<c:set var="productNotfound" value="false"/>
		<c:set var="productOOS" value="false"/>
	<dsp:getvalueof var="frmComparisonPage" param="prodToCompare"/>
	<dsp:getvalueof var="categoryId" param="categoryId"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
	<c:set var="count" value="1"/>
	<c:set var="countOmni" value="1" />
	<dsp:getvalueof var="parentProductId" param="prodId" />
	<dsp:droplet name="BreadcrumbDroplet">		
						<dsp:param name="productId" value="${parentProductId}" />
						<dsp:param name="siteId" value="${currentSiteId}"/>
							<dsp:oparam name="output">
								<dsp:getvalueof param="isPrimaryCat" var="isPrimaryCat"/>
								<dsp:getvalueof param="isOrphanProduct" var="isOrphanProduct"/>
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
			<dsp:getvalueof var="color" param="selectedRollUpValue" />
		</c:when>
		<c:otherwise>
			<dsp:getvalueof var="skuId" param="selectedRollUpValue" />
		</c:otherwise>										
	</c:choose>
	<dsp:droplet name="ProductDetailDroplet">
		<dsp:param name="siteId" value="${appid}" />
		<dsp:param name="id" param="prodId" />
		<dsp:param name="color" value="${color}" />
		<dsp:param name="skuId" value="${skuId}" />
		<dsp:oparam name="output">
		<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductListHandler"/>
			<div class="collectionItemsBox clearfix productComparisonListView grid_12 collectionGridView" id="shopCollection">
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
						<span class="visitCollection"><dsp:a page="${collectionFinalUrl}" iclass="prod-attrib-exclusive"><bbbl:label key='lbl_qview_visit_collection' language="${pageContext.request.locale.language}" /></dsp:a></span>
					</h2>
					<h2 class="divider"></h2>
						<dsp:getvalueof var="productVO" param="productVO" />
						<dsp:getvalueof var="refinedNameProduct" param="productVO.refinedName" scope="request" />
					<ul class="clearfix productRowContainer compareModalIE collectionGridRow listGridToggle small-block-grid-1 nearbyStoreULS"> 
					<dsp:droplet name="/atg/dynamo/droplet/ForEach">
					<dsp:param param="collectionVO.childProducts" name="array" />
						<dsp:oparam name="output">
						<dsp:getvalueof var="childSKU" param="element.childSKUs"/>
						<c:if test="${not empty childSKU}">
							<li class="bdrBot collectionItems clearfix list"> 
							<fieldset itemtype="http://schema.org/Product" itemscope="" class="registryDataItemsWrap listDataItemsWrap "> 
							<dsp:getvalueof var="prodImage" param="element.productImages.smallImage"/>
							<dsp:getvalueof var="prodName" param="element.name"/> 
							<dsp:getvalueof var="productID" param="element.productId"/>	
							<dsp:getvalueof var="prodThumbImage" param="element.productImages.thumbnailImage"/>
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
							<dsp:getvalueof var="hasRebate" value=""/>
							<dsp:getvalueof var="eligibleRebates" value=""/>
						<%-- 	<dsp:getvalueof var="skuid" value=""/> --%>
							<dsp:getvalueof var="bopusAllowed" value=""/>
							<dsp:getvalueof var="emailStockAlertsEnabled" value="" />
							<dsp:getvalueof var="skuinStock" value="" />
							
							<c:if test="${(null != childSKU) && (fn:length(childSKU) == 1 )}"> 
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param name="array" value="${childSKU}" />
									<dsp:oparam name="output">		
										<dsp:getvalueof var="childSkuId" param="element"/>
										<dsp:droplet name="ProductDetailDroplet">
										    <dsp:param name="id" value="${productID}" />
											<dsp:param name="siteId" param="siteId"/>
											<dsp:param name="skuId" value="${childSkuId}"/>
												<dsp:oparam name="output">
													<dsp:getvalueof var="selectedSKUTemp" param="selected"/>
													<c:if test="${not empty selectedSKUTemp}">
														<c:set var="selectedSKU" value="${selectedSKUTemp}"/>
														<dsp:getvalueof var="selectedSKUStock" param="inStock" />
													</c:if>
													<dsp:getvalueof var="skuAttributes" param="pSKUDetailVO.skuAttributes"/>
													<dsp:getvalueof var="hasRebate" param="pSKUDetailVO.hasRebate"/>
													<dsp:getvalueof var="eligibleRebates" param="pSKUDetailVO.eligibleRebates"/>
													<dsp:getvalueof var="skuID" param="pSKUDetailVO.skuId"/>
													<dsp:getvalueof var="skuInCartFlag" param="pSKUDetailVO.inCartFlag" />
													<dsp:getvalueof var="isLtlItem" param="pSKUDetailVO.ltlItem"/> <%-- LTL 99  --%>
													<dsp:getvalueof var="bopusAllowed" param="pSKUDetailVO.bopusAllowed"/>
													<dsp:getvalueof var="emailStockAlertsEnabled" param="pSKUDetailVO.emailStockAlertsEnabled" />
													<dsp:getvalueof var="skuinStock" param="inStock" />
												</dsp:oparam>
											</dsp:droplet>
										</dsp:oparam>
									</dsp:droplet>
								</c:if>
							<c:choose>
								<c:when test="${(null != childSKU) && not empty skuID}">
									<dsp:getvalueof var="pDefaultChildSku" value="${skuID}" />
								</c:when>
								<c:otherwise>
									<dsp:getvalueof var="pDefaultChildSku" value="${childSKU[0]}" />
								</c:otherwise>
							</c:choose>
								<%-- <dsp:getvalueof var="pDefaultChildSku" value="${skuid}" /> --%>
								
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
								<dsp:getvalueof var="CategoryId" param="categoryId"/>
								<div class="grid_2 alpha small-6 large-2 columns"> 
									<c:choose>
										<c:when test="${not empty CategoryId}">
											<dsp:a iclass="prodImg" page="${finalUrl}?poc=${parentProductId}&categoryId=${CategoryId}&color=${color}&skuId=${skuID}" title="${prodName}">	
												<dsp:getvalueof var="showImage" param="collectionVO.showImagesInCollection"/>
												<c:if test="${showImage}">
													<c:choose>
														<c:when test="${empty prodThumbImage}">
															<img src="${imagePath}/_assets/global/images/no_image_available.jpg" width="146" height="146" alt="${prodName}" />
														</c:when>
														<c:otherwise>
															<img class="noImageFound" src="${scene7Path}/${prodThumbImage}" width="146" height="146" alt="${prodName}" />
														</c:otherwise>
													</c:choose>
												</c:if>
											</dsp:a>
										</c:when>
										<c:otherwise>
											<dsp:a iclass="prodImg" page="${finalUrl}?poc=${parentProductId}&color=${color}&skuId=${skuID}" title="${prodName}">	
												<dsp:getvalueof var="showImage" param="collectionVO.showImagesInCollection"/>
												<c:if test="${showImage}">								
													<c:choose>
													<c:when test="${empty prodThumbImage}">
														<img src="${imagePath}/_assets/global/images/no_image_available.jpg" width="146" height="146" alt="${prodName}" />
													</c:when>
													<c:otherwise>
														<img class="noImageFound" src="${scene7Path}/${prodThumbImage}" width="146" height="146" alt="${prodName}" />
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
								<div class="grid_8 omega clearfix listItemRowData small-6 large-10 columns product-info">
									<div class="clearfix prodDetail">
										<div class="grid_5_half alpha fl small-12 large-6">
											<ul class="prodInfo"> 
												<li class="prodName">
													<c:choose>
														<c:when test="${not empty CategoryId}">
															<dsp:a page="${finalUrl}?poc=${parentProductId}&categoryId=${CategoryId}&color=${color}&skuId=${skuID}" title="${prodName}">
																${prodName}
															</dsp:a>
														</c:when>
														<c:otherwise>
															<dsp:a page="${finalUrl}?poc=${parentProductId}&color=${color}&skuId=${skuID}" title="${prodName}">
																${prodName}
															</dsp:a>
														</c:otherwise>
													</c:choose>
												</li>
												</ul> 
											<input type="hidden" value="${inStock}" class="isInStock"/> 
											
										</div>
										</div>
									<div class="width_8 clearfix">
										<div class="grid_3 alpha large-6 left">
										<div class="grid_2_half alpha fl">
										    
											<div class="priceQuantityNotAvailable alpha omega fr <c:if test="${inStock==true}">hidden</c:if> clearfix">
												<div class="message fr"> 
													 <input type="hidden" name="oosProdId" value="${oosProdId}" />
													<c:choose>
														<c:when test="${inStock==true}">
															<input type="hidden" value="" name="oosSKUId" class="_oosSKUId"/>
														</c:when>
														<c:otherwise>
															<input type="hidden" value="${pDefaultChildSku}" name="oosSKUId" class="_oosSKUId"/>
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
											
											<div class="fr prodAttributeContainer <c:if test="${inStock==false}">hidden</c:if>">
												<div class="rebateContainer collectionRebateContainer prodAttribWrapper fr">
													<c:set var="rebatesOn" value="${false}" />
													<c:if test="${not empty hasRebate && hasRebate}">
														<c:if test="${(null != eligibleRebates) && (fn:length(eligibleRebates) == 1 )}"> 
															<c:set var="rebatesOn" value="${true}" />
														</c:if>
													</c:if>	
													<dsp:droplet name="/atg/dynamo/droplet/ForEach">
														<dsp:param value="${skuAttributes}" name="array" />
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
												</div>										
											</div>
										</div>
									
											<ul class="prodInfo">
												<dsp:getvalueof var="ReviewCountvar" param="element.bvReviews.totalReviewCount"/>
												<c:set var="ReviewCount">${ReviewCountvar}</c:set>

												<%-- <c:if test="${BazaarVoiceOn}"> --%>
													<dsp:getvalueof var="ratingAvailable" param="element.bvReviews.ratingAvailable"></dsp:getvalueof>
													<c:choose>
														<c:when test="${ratingAvailable == true}">
															<dsp:getvalueof var="fltValue" param="element.bvReviews.averageOverallRating"/>
															<dsp:getvalueof param="element.bvReviews.totalReviewCount" var="totalReviewCount"/>
															<c:choose>
																<c:when test="${totalReviewCount == 1}">
																	<li class="prodReviews ratingsReviews clearfix marTop_5 prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="element.bvReviews.averageOverallRating"/>">
																		<dsp:a page="${finalUrl}?skuId=${catalogRefId}&categoryId=${CategoryId}&showRatings=true">
																		${ReviewCount} <bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" /></dsp:a>
																	</li>
																</c:when>
																<c:otherwise>
																	<li class="prodReviews ratingsReviews clearfix marTop_5 prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="element.bvReviews.averageOverallRating"/>">
																		<dsp:a page="${finalUrl}?skuId=${catalogRefId}&categoryId=${CategoryId}&showRatings=true">
																		${ReviewCount} <bbbl:label key='lbl_reviews_count' language="${pageContext.request.locale.language}" /></dsp:a>
																	</li>
																</c:otherwise>
															</c:choose>	
														</c:when>
														<c:otherwise>
															<dsp:getvalueof var="fltValue" param="element.bvReviews.averageOverallRating"/>
															<dsp:getvalueof var="totalReviewCount" value="0"></dsp:getvalueof>
															<li class="prodReviews ratingsReviews clearfix marTop_5 prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="element.bvReviews.averageOverallRating"/>">
																<dsp:a page="${finalUrl}?skuId=${catalogRefId}&categoryId=${CategoryId}&showRatings=true">
																${ReviewCount} <bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" /></dsp:a>
															</li>
														</c:otherwise>
													</c:choose>
												<%-- </c:if>	 --%>
											</ul>
										</div>
										
										<div class="omega fr dropWrapper productSelection large-6 right">						 
											<div class="fr prodInfoContainer alpha omega listViewQtyWrapper"> 								
												<dsp:droplet name="/atg/dynamo/droplet/ForEach">
												<dsp:param param="element.rollupAttributes" name="array" />
												<dsp:param name="sortProperties" value="_key"/>
													<dsp:oparam name="output">
													<dsp:getvalueof var="prodType" param="key"/>
													<dsp:getvalueof var="sizeParam" param="size" />
													<c:choose>												
													<c:when test="${prodType eq 'SIZE'}">
														<div class="small-12 columns size-selector sizePicker pdp-selector large-12">
															<%-- <label for="selProdSize" class="left inline"><bbbl:label key="lbl_sizes_dropdown" language ="${pageContext.request.locale.language}"/></label> --%>
															<div class="psize">
																<ul id="selectProductSize" name="selectProductSize" class="swatches customSelectBoxCollection" aria-required="false" >
																<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																<dsp:param param="element" name="array" />
																	<dsp:oparam name="output">
																		<dsp:getvalueof var="elementValue" param="element.rollupAttribute"/>
																		<dsp:getvalueof var="size" param="size" />
																		<dsp:getvalueof var="countVar" param="count"/>
																		<li <c:if test="${countVar==1}">class="selected"</c:if> ><a><c:out value="${elementValue}" escapeXml="false"/></a></li>	
																	</dsp:oparam>
																</dsp:droplet>
																</select>
															</div>
															<input type="hidden" name="prodSize" class="_prodSize addItemToRegis addItemToList" value="" />
															
														</div>
													</c:when>
													<c:otherwise>
														<c:choose>									
														<c:when test="${prodType eq 'FINISH'}">
														
															<div class="small-12 columns size-selector sizePicker finish-selector pdp-selector large-12">
																<%-- <label for="selProdFinish"><bbbl:label key="lbl_finish_dropdown" language ="${pageContext.request.locale.language}"/></label> --%>
																<div class="pfinish">
																	<ul id="selectProductSize" name="selectProductSize" class="customSelectBoxCollection" aria-required="false" >
																		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																		<dsp:param param="element" name="array" />
																			<dsp:oparam name="output">
																				<dsp:getvalueof var="elementValue" param="element.rollupAttribute"/>
																				<dsp:getvalueof var="size" param="size" />
																				<dsp:getvalueof var="countVar" param="count"/>
																					<dsp:getvalueof var="smallImagePath" param="element.smallImagePath"/>
																					<dsp:getvalueof var="thumbnailImagePath" param="element.thumbnailImagePath"/>
																					<dsp:getvalueof var="swatchImagePath" param="element.swatchImagePath"/>
																					<c:choose>
																						<c:when test="${(color eq elementValue) || (size == 1)}">
																							<li <c:if test="${countVar==1}">class="selected"</c:if> ><a data-imgSrc="${scene7Path}/${swatchImagePath}" data-imgURLSmall="${scene7Path}/${smallImagePath}" data-imgURLThumb="${scene7Path}/${thumbnailImagePath}"><img src="${scene7Path}/${swatchImagePath}" height="10" width="10" alt="${attribute}" class="noImageFound"/></a></li>
																					  	</c:when>
																					  	<c:otherwise>
																					  		<li <c:if test="${countVar==1}">class="selected"</c:if> ><a data-imgSrc="${scene7Path}/${swatchImagePath}" data-imgURLSmall="${scene7Path}/${smallImagePath}" data-imgURLThumb="${scene7Path}/${thumbnailImagePath}"><img src="${scene7Path}/${swatchImagePath}" height="10" width="10" alt="${attribute}" class="noImageFound"/></a></li>
																					  	</c:otherwise>
																					</c:choose>		
																			</dsp:oparam>
																		</dsp:droplet>
																	</select>
																</div>
																<input type="hidden" name="prodFinish" class="_prodFinish addItemToRegis addItemToList" value="" />												
															</div>
														</c:when>
														<c:otherwise>
														<c:if test="${prodType eq 'COLOR'}">
															<div class="prodColor noMarRight fr marLeft_5 color-selector swatchPicker large-12">
																<%-- <label for="selProdColor" class="left inline"><bbbl:label key="lbl_colors_dropdown" language ="${pageContext.request.locale.language}"/></label> --%>
																<div class="pcolor colorPicker">
																	<ul id="selProdColor" name="selProdColor" class="swatches customSelectBoxCollection" aria-required="false" >
																		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																		<dsp:param param="element" name="array" />
																			<dsp:oparam name="output">
																				<dsp:getvalueof var="size" param="size" />
																				<dsp:getvalueof var="countVar" param="count"/>
																		    	<dsp:getvalueof var="elementValue" param="element.rollupAttribute"/>
																				<dsp:getvalueof var="smallImagePath" param="element.smallImagePath"/>
																				<dsp:getvalueof var="thumbnailImagePath" param="element.thumbnailImagePath"/>
																				<dsp:getvalueof var="swatchImagePath" param="element.swatchImagePath"/>
																				<li <c:if test="${countVar==1}">class="selected"</c:if> ><a data="${fn:toLowerCase(elementValue)}" title="${elementValue}" data-imgSrc="${scene7Path}/${swatchImagePath}" data-s7ImgsetID="${smallImagePath}" data-imgURLSmall="${scene7Path}/${smallImagePath}" data-imgURLThumb="${scene7Path}/${thumbnailImagePath}"><img src="${scene7Path}/${swatchImagePath}" height="10" width="10" alt="${elementValue}" class="noImageFound"/></a></li>
																			</dsp:oparam>
																		</dsp:droplet>
																	</select>
																</div>
																<input type="hidden" name="prodColor" class="_prodColor addItemToRegis addItemToList" value="${colorValue}" />											
															</div>
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
									<div itemprop="price" class="prodPrice fl price">
											<c:set var="inputFieldPrice"></c:set>
											<dsp:getvalueof var="priceRangeDescription" param="element.priceRangeDescription" />
											<dsp:getvalueof var="salePriceRangeDescription" param="element.salePriceRangeDescription" />
											<c:choose>
												<c:when test="${not empty pDefaultChildSku}">
													<dsp:include page="../browse/product_details_price.jsp">	
														<dsp:param name="product" value="${productID}"/>
														<dsp:param name="sku" value="${pDefaultChildSku}"/>
														<dsp:param name="inCartFlag" value="${skuInCartFlag}"/>
													</dsp:include>
													<c:set var="inputFieldPrice">${omniPrice}</c:set>
												</c:when>
												<c:otherwise>
												<c:choose>
													<c:when test="${not empty salePriceRangeDescription}">
														<span class="prodPriceNEW">
															<dsp:valueof converter="currency" param="element.salePriceRangeDescription" />
														</span>
														<dsp:getvalueof var="salePriceRange" param="element.salePriceRangeDescription"/>
														<c:choose>
															<c:when test="${fn:contains(salePriceRange,'-')}">&nbsp;</c:when>
															<c:otherwise>&nbsp;</c:otherwise>
														</c:choose>
														<span class="prodPriceOLD">
															<span class="was"><bbbl:label key='lbl_old_price_text' language="${pageContext.request.locale.language}" /></span>
															<span> <dsp:valueof converter="currency"
																	param="element.priceRangeDescription" /></span>
														</span>
														<dsp:getvalueof param="element.salePriceRangeDescription" id="saleprice"/> 
														<c:set var="inputFieldPrice">${saleprice}</c:set>
													</c:when>
													<c:otherwise>
														<dsp:valueof converter="currency" param="element.priceRangeDescription" />
														<dsp:getvalueof param="element.priceRangeDescription" id="price"/> 
														<c:set var="inputFieldPrice">${price}</c:set>
													</c:otherwise>
												</c:choose>
												</c:otherwise>
											</c:choose>						
										</div>
								</div>

								<div class="clearfix fr width_8 small-12 large-6 columns no-padding-right right end">
										
																	
										<div class="fr prodInfoContainer alpha omega selectCompareButton listViewQtyWrapper">
											<div class="btnPD clearfix <c:if test="${frmComparisonPage}">hidden</c:if>">
											  	<div class="fr">
											  	 <%-- Hide quantity spinner for LTL item LTL-1166 --%>
											  		<div class="quantityPDP small-12 large-3 columns quantity no-padding-right fl <c:if test="${not empty pDefaultChildSku && not empty isLtlItem && isLtlItem}">hidden</c:if>">
													  <div class="qty-spinner">
														  	<c:if test="${not frmComparisonPage}">
														  	    <label id="lblprodCompListDescQty" class="txtOffScreen hidden" for="prodCompDescQty"><bbbl:label key="lbl_decrease_quantity" language="${pageContext.request.locale.language}" /></label>
																<a class="button minus secondary" id="prodCompDescQty" title="<bbbl:label key='lbl_decrease_quantity' language="${pageContext.request.locale.language}" />"><span></span></a>
																<input id="pqty${productID}" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="fl qty quantity-input addItemToRegis _qty itemQuantity addItemToList" type="text" role="textbox" name="qty" value="1" maxlength="2" data-change-store-submit="qty" data-change-store-errors="required digits nonZero" aria-required="true" aria-describedby="quantity" /> 
																<label id="lblprodCompIncQty" class="txtOffScreen hidden" for="prodCompIncQty"><bbbl:label key="lbl_increase_quantity" language="${pageContext.request.locale.language}" /></label>
                                								<a class="button plus secondary" id="prodCompIncQty" title="<bbbl:label key='lbl_increase_quantity' language="${pageContext.request.locale.language}" />"><span></span></a>
															</c:if>
													  </div>
												  </div>
												  
													<div class="fl marginButtonFix small-12 large-5 columns addToReg no-padding-left <c:if test="${not empty pDefaultChildSku && not empty isLtlItem && isLtlItem}">hidden</c:if>">
														<dsp:include page="../addgiftregistry/add_item_gift_registry.jsp">
														<input type="hidden" name="parentProdId" class="_prodId addItemToRegis addItemToList" value="${parentProductId}" />
														</dsp:include>	
													</div>	
													
													<c:if test="${inStock==false}">
														<c:set var="button_disabled" value="button_disabled" />
													</c:if>
													<div class="fl addToCart small-12 large-4 columns addToCart no-padding-right right <c:if test="${not empty pDefaultChildSku && not empty isLtlItem && isLtlItem}">hidden</c:if>">
														<div class="button_active button_active_orange ${button_disabled}">
															<input type="submit" class="tiny button expand transactional" name="btnAddToCart" value="Add to Cart" onclick="rkg_micropixel('${appid}','cart')" role="button" aria-pressed="false" <c:if test="${inStock==false}">disabled="disabled"</c:if>/>
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
													<c:when test="${not empty pDefaultChildSku && not empty isLtlItem && isLtlItem}">
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
													<div class="btnPDLinks padTop_15 clearfix cb fr small-12 large-3 columns no-padding <c:if test="${not empty pDefaultChildSku && not empty isLtlItem && isLtlItem}">hidden</c:if>">
														<div class="addToList fl" id="btnAddToList">
													         <a href="javascript:void(0);" onclick="rkg_micropixel('${appid}','wish')"><bbbl:label key='lbl_compare_save_for_later' language="${pageContext.request.locale.language}" /></a>
													     </div>
														
														<c:if test="${MapQuestOn}">
															<div class="fl findInStore padLeft_30">
																
																<c:choose>
																	<c:when test="${bopusAllowed}">
																		<div class="disableText fr cb">
														 					<bbbl:label key='lbl_compare_find_in_store' language="${pageContext.request.locale.language}" />
														 				</div>
																	</c:when>
																	<c:otherwise>
																		<div class="changeStore fr cb">
														 					<a href="javascript:void(0);" onclick="pdpOmnitureProxy('${productID}', 'findinstore');rkg_micropixel('${appid}','findstore');"><bbbl:label key='lbl_compare_find_in_store' language="${pageContext.request.locale.language}" /></a>
														 				</div>
																	</c:otherwise>
																</c:choose>
															</div>
														</c:if>
													</div>
													
													<%-- R2.2.1 Story - 131 Quick View Page --%>
										  </div>
										  <c:if test="${frmComparisonPage}">
										  <c:choose>
										       <c:when test="${empty pDefaultChildSku}">
											   <dsp:getvalueof var="buttonClass" value="button_disabled"/>
											 
											   </c:when>
											   <c:otherwise>
											    <dsp:getvalueof var="buttonClass" value="button_lightblue"/>
												
											   </c:otherwise>
										  </c:choose>
										  <div class="selectToCompare small-12 large-6 columns right end no-padding-left">
												<input type="button" class="button tiny service expand" name="btnSelectToCompare" value="Select To Compare" role="button" aria-pressed="false" <c:if test="${empty pDefaultChildSku && empty pDefaultChildSku}">disabled="disabled"</c:if>/>
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
				   totalPrice=qty*price;
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

                if (typeof s !== "undefined") {
                    /* NOTE: (mbhatia3) Not an elegant solution for "UAT-591" ... but Omniture bbb_scode is VERY **smart** */
                   // BBB.omnitureObj.backup_s("eVar1,eVar2,eVar3,eVar4,eVar5,eVar6,eVar7,eVar8,eVar47,productNum,prop7,prop8,prop25");
                   // BBB.omnitureObj.clear_s("eVar3");
                    //BBB.omnitureObj.delete_s("eVar1,eVar2,eVar4,eVar5,eVar6,eVar7,eVar8,eVar47,productNum,prop7,prop8,prop25");
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
                    }
                    s.events="event57";
                    fixOmniSpacing();
                    s.t();
                    //BBB.omnitureObj.restore_s("eVar1,eVar2,eVar3,eVar4,eVar5,eVar6,eVar7,eVar8,eVar47,productNum,prop7,prop8,prop25");
                }
			</script>
			 
	<a class='close-reveal-modal'>&#215;</a>
</dsp:page>
<%-- R2.2 Story - 178 Product Comparison Page | End--%>