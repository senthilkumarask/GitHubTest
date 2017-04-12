<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet"/>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
<dsp:importbean bean="/com/bbb/commerce/droplet/TBSItemExclusionDroplet"/>

<dsp:getvalueof var="appid" bean="Site.id" />
<c:set var="AttributePDPCollection">
	<bbbl:label key='lbl_pdp_attributes_collection' language="${pageContext.request.locale.language}" />
</c:set>
<c:set var="TruckSrc">
	<bbbc:config key="truck_image_src" configName="LTLConfig_KEYS" />
</c:set>
<dsp:getvalueof var="crossSellFlag" param="crossSellFlag"/>
<dsp:getvalueof var="desc" param="desc"/>
<c:set var="count" value="1"/>
<c:set var="productCount" value="0" />
<c:set var="collectionId_Omniture" scope="request"></c:set>
<dsp:getvalueof param="parentProductId" var="parentProductId"/>
	<dsp:getvalueof var="color" param="color"/>
<dsp:getvalueof var="childProducts" param="collectionVO.childProducts"/>
		<c:if test="${childProducts ne null && not empty childProducts}">
				<dsp:form name="collectionForm" id="testcollectionForm" method="post">
					<ul class="small-block-grid-4 collectionGridRow">
					<dsp:droplet name="/atg/dynamo/droplet/ForEach">
					<dsp:param param="collectionVO.childProducts" name="array" />

						<dsp:oparam name="output">
						<dsp:getvalueof var="childSKU" param="element.childSKUs"/>
						<c:if test="${not empty childSKU}">

							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param param="element.rollupAttributes" name="array" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="prodType" param="key" />
								</dsp:oparam>
							</dsp:droplet>
							<dsp:getvalueof var="productID" param="element.productId"/>
							<dsp:getvalueof var="skuAttributes" value=""/>
							<dsp:getvalueof var="hasRebate" value=""/>
							<dsp:getvalueof var="eligibleRebates" value=""/>
							<dsp:getvalueof var="skuid" value=""/>
							<dsp:getvalueof var="bopusAllowed" value=""/>
							<dsp:getvalueof var="emailStockAlertsEnabled" value="" />
							<dsp:getvalueof var="skuinStock" value="" />
							<dsp:getvalueof var="childSKU" param="element.childSKUs"/>
							<dsp:getvalueof var="colorMatched" param="element.colorMatched"/>
							<c:set var="CertonaContext" scope="request">${CertonaContext}${productID};</c:set>
							<c:set var="collectionId_Omniture" scope="request">${collectionId_Omniture};${productID};;;;eVar29=${parentProductId},</c:set>
							<c:choose>
								<c:when test="${(null != childSKU) && (fn:length(childSKU) == 1 )}">
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param name="array" value="${childSKU}" />
									<dsp:oparam name="output">
											<dsp:getvalueof var="childSkuId" param="element"/>
											<dsp:droplet name="ProductDetailDroplet">
												<dsp:param name="id" value="${productID}" />
												<dsp:param name="siteId" param="siteId"/>
												<dsp:param name="skuId" value="${childSkuId}"/>
												<dsp:param name="isDefaultSku" value="true"/>
													<dsp:oparam name="output">
														<dsp:getvalueof var="selectedSKUTemp" param="selected"/>
														<c:if test="${not empty selectedSKUTemp}">
															<c:set var="selectedSKU" value="${selectedSKUTemp}"/>
															<dsp:getvalueof var="selectedSKUStock" param="inStock" />
														</c:if>
														<dsp:getvalueof var="skuVO" param="pSKUDetailVO"/>
														<dsp:getvalueof var="skuAttributes" param="pSKUDetailVO.skuAttributes"/>
														<dsp:getvalueof var="hasRebate" param="pSKUDetailVO.hasRebate"/>
														<dsp:getvalueof var="eligibleRebates" param="pSKUDetailVO.eligibleRebates"/>
														<dsp:getvalueof var="skuid" param="pSKUDetailVO.skuId"/>
														<dsp:getvalueof var="isLtlItem" param="pSKUDetailVO.ltlItem"/> <%-- LTL 498  --%>
														<dsp:getvalueof var="bopusAllowed" param="pSKUDetailVO.bopusAllowed"/>
														<%-- <input type="hidden" name="skuId" value="${skuid}" class="_skuId addItemToRegis addItemToList" /> --%>
														<dsp:getvalueof var="emailStockAlertsEnabled" param="pSKUDetailVO.emailStockAlertsEnabled" />
														<dsp:getvalueof var="skuinStock" param="inStock" />
														<dsp:getvalueof var="attribs" param="productVO.attributesList"/>
													</dsp:oparam>
											</dsp:droplet>
											</dsp:oparam>
										</dsp:droplet>
									<c:choose>
										<c:when test="${not empty skuVO}">
											<dsp:getvalueof var="attribs" param="pSKUDetailVO.skuAttributes"/>
										</c:when>
										<c:otherwise>
											<dsp:getvalueof var="attribs" param="${attribs}"/>
										</c:otherwise>
									</c:choose>
							</c:when>
							<c:otherwise>
								<c:if test="${(not empty color && colorMatched)}">
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
										<dsp:param name="array" value="${childSKU}" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="childSkuId" param="element"/>
											<dsp:droplet name="ProductDetailDroplet">
												<dsp:param name="id" value="${productID}" />
												<dsp:param name="siteId" param="siteId"/>
												<dsp:param name="skuId" value="${childSkuId}"/>
												<dsp:param name="color" value="${color}"/>
												<dsp:oparam name="output">
													<dsp:getvalueof var="skuColor" param="pSKUDetailVO.color"/>
													<c:set var="prodName_selectedSku" value=""/>
													<c:if test="${not empty color and color eq skuColor}">
														<dsp:getvalueof var="prodName_selectedSku" param="pSKUDetailVO.displayName"/>
														<dsp:getvalueof var="hasRebate" param="pSKUDetailVO.hasRebate"/>
														<dsp:getvalueof var="skuAttributes" param="pSKUDetailVO.skuAttributes"/>
														<dsp:getvalueof var="eligibleRebates" param="pSKUDetailVO.eligibleRebates"/>
														<dsp:getvalueof var="bopusAllowed" param="pSKUDetailVO.bopusAllowed"/>
														<dsp:getvalueof var="skuid" param="pSKUDetailVO.skuId"/>
														<dsp:getvalueof var="inCartFlag" param="pSKUDetailVO.inCartFlag"/>
														<dsp:getvalueof var="ltlItem" param="pSKUDetailVO.ltlItem"/> <%-- LTL 398  --%>
														<c:set var="isLtlItem" value="${ltlItem}"/>
														<%-- <input type="hidden" name="skuId" value="${skuid}" class="_skuId addItemToRegis addItemToList" /> --%>
														<dsp:getvalueof var="emailStockAlertsEnabled" param="pSKUDetailVO.emailStockAlertsEnabled" />
														<dsp:getvalueof var="skuinStock" param="inStock" />																				
													</c:if>
													
													<c:if test="${not empty prodName_selectedSku}">
														<c:set var="prodName" value="${prodName_selectedSku}"/>
													</c:if>
												</dsp:oparam>
											</dsp:droplet>
										 </dsp:oparam>
									</dsp:droplet>
								</c:if>
								<c:forEach var="obj" items="${childSKU}">
									<c:if test="${obj == skuIdfromURL}">
										<dsp:getvalueof var="skuIdToBeSend" value="${obj}"/>
											<dsp:droplet name="PriceDroplet">
											<dsp:param name="product" value="${skuIdToBeSend}"/>
											<dsp:param name="sku" value="${skuIdToBeSend}"/>
												<dsp:oparam name="output">
													<dsp:getvalueof var="omniPrice" vartype="java.lang.Double" param="price.listPrice"/>
												</dsp:oparam>
											</dsp:droplet>
										<input id="certonaPrice" type="hidden" value="${omniPrice}" />
									</c:if>
								</c:forEach>
							</c:otherwise>
							</c:choose>

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
							<%-- <c:set var="productCount" value="${productCount + 1}" scope="page"/>
							<c:choose>
							<c:when test="${productCount % 4 == 1}">
							<li class="grid_3 collectionItems clearfix alpha">
							</c:when>
							<c:when test="${productCount % 4 == 0}">
							<li class="grid_3 collectionItems clearfix omega">
							</c:when>
							<c:otherwise>
							<li class="grid_3 collectionItems clearfix">
							</c:otherwise>
							</c:choose> --%>
					<li class="collectionItems">
						<fieldset class="registryDataItemsWrap listDataItemsWrap collectionItemsSection" itemscope itemtype="//schema.org/Product">

							<dsp:getvalueof var="prodImage" param="element.productImages.thumbnailImage"/>
							<dsp:getvalueof param="element.name" var="prodName"/>
							<c:choose>
								<c:when test="${empty prodImage}">
									<img class="hidden productImage" height="83" width="83" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${prodName}" />
								</c:when>
								<c:otherwise>
									<img class="hidden productImage noImageFound" height="83" width="83" src="${scene7Path}/${prodImage}" alt="${prodName}" />
								</c:otherwise>
							</c:choose>
						<%-- Image --%>

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

							<c:choose>
							<c:when test="${(crossSellFlag ne null) && (crossSellFlag eq 'true')}">
								<script type="text/javascript">
									function quickViewCrossSell() {
										pdpCrossSellProxy('crossSell', '${desc}');
									};
								</script>
								<c:set var="onClickEvent">javascript:pdpCrossSellProxy('crossSell', '${desc}')</c:set>
							</c:when>
							<c:otherwise>
								<script type="text/javascript">
									function quickViewCrossSell() {
									};
								</script>
								<c:set var="onClickEvent" value=""/>
							</c:otherwise>
							</c:choose>
							<dsp:getvalueof var="CategoryId" param="categoryId"/>
							<div class="grid_3 alpha marBottom_10">
								<c:choose>
									<c:when test="${not empty CategoryId}">
										<dsp:a iclass="prodImg" page="${finalUrl}?poc=${parentProductId}&categoryId=${CategoryId}" title="${prodName}" onclick="${onClickEvent}">
											<dsp:getvalueof var="showImage" param="collectionVO.showImagesInCollection"/>
											<c:if test="${showImage != false}">
											<c:choose>
											<c:when test="${empty prodImage}">
												<img class="fl" src="${imagePath}/_assets/global/images/no_image_available.jpg" width="229" height="229" alt="${prodName}" />
											</c:when>
											<c:otherwise>
												<img class="fl noImageFound" src="${scene7Path}/${prodImage}" width="229" height="229" alt="${prodName}" />
											</c:otherwise>
											</c:choose>
											</c:if>
										</dsp:a>
									</c:when>
									<c:otherwise>
										<dsp:a iclass="prodImg" page="${finalUrl}?poc=${parentProductId}" title="${prodName}" onclick="${onClickEvent}">
											<dsp:getvalueof var="showImage" param="collectionVO.showImagesInCollection"/>
											<c:if test="${showImage != false}">
											<c:choose>
											<c:when test="${empty prodImage}">
												<img class="fl" src="${imagePath}/_assets/global/images/no_image_available.jpg" width="229" height="229" alt="${prodName}" />
											</c:when>
											<c:otherwise>
												<img class="fl noImageFound" src="${scene7Path}/${prodImage}" width="229" height="229" alt="${prodName}" />
											</c:otherwise>
											</c:choose>
											</c:if>
										</dsp:a>
									</c:otherwise>
								</c:choose>
								<%-- <span onclick="${onClickEvent}" class="quickView showOptionMultiSku block marTop_10"><bbbl:label key="lbl_product_quick_view" language="${pageContext.request.locale.language}" /></span>
								--%></div>
								<div class="grid_3 alpha">
								<ul class="prodInfoGrid clearfix">
								<li class="pdpCollectionProdAttribs">
								<%-- Product name --%>
								<div class="prodNameGrid prodName">
									<dsp:a onclick="${onClickEvent}" page="${finalUrl}?poc=${parentProductId}">
											<dsp:valueof param="element.name" valueishtml="true"/>
										</dsp:a>
									</div>

									<%-- LTL Delivery Surcharge Attribute start
									<dsp:getvalueof var="ltlProduct" param="element.ltlProduct"/>
									<c:if test="${not empty ltlProduct && ltlProduct}">
										<img class="marBottom_5" width="20" height="15" src="${TruckSrc}" alt="Truck">
										<span class="ltlPromo prod-attrib ltlTruck"><bbbl:label key='ltl_delivery_surcharge_may_apply' language="${pageContext.request.locale.language}" /></span>
									</c:if>
									LTL Delivery Surcharge Attribute end --%>

									<%-- Rating --%>
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
																<div class="prodReviewGrid prodReviews ratingsReviews clearfix prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="element.bvReviews.averageOverallRating"/>">
																<dsp:a page="${finalUrl}?skuId=${catalogRefId}&categoryId=${CategoryId}&showRatings=true">
																${ReviewCount}<bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" /></dsp:a>
																</div>
															</c:when>
															<c:otherwise>
																<div class="prodReviewGrid prodReviews ratingsReviews clearfix prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="element.bvReviews.averageOverallRating"/>">
																<dsp:a page="${finalUrl}?skuId=${catalogRefId}&categoryId=${CategoryId}&showRatings=true">
																${ReviewCount}<bbbl:label key='lbl_reviews_count' language="${pageContext.request.locale.language}" /></dsp:a>
																</div>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<dsp:getvalueof var="fltValue" param="element.bvReviews.averageOverallRating"/>
														<dsp:getvalueof var="totalReviewCount" value="0"></dsp:getvalueof>
														<div class="prodReviewGrid prodReviews ratingsReviews clearfix prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="element.bvReviews.averageOverallRating"/>">
														<dsp:a page="${finalUrl}?skuId=${catalogRefId}&categoryId=${CategoryId}&showRatings=true">
														${ReviewCount}<bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" /></dsp:a>
														</div>
													</c:otherwise>
												</c:choose>
											</c:if>
											<input type="hidden" class="isInStock" value="${inStock}"/>

									<%-- Rating End--%>
									<div class="priceQuantityNotAvailable <c:if test="${inStock==true}">hidden</c:if>">
									  <div class="gridMessage">
											 <input type="hidden" name="oosProdId" value="${oosProdId}" />
											 <c:choose>
												<c:when test="${inStock==true}">
												<input type="hidden" value="" name="oosSKUId" class="_oosSKUId"/>
												</c:when>
												<c:otherwise>
												<input type="hidden" value="${skuid}" name="oosSKUId" class="_oosSKUId"/>
												</c:otherwise>
											</c:choose>
											 <div class="error noMarBot"><bbbl:label key='lbl_pdp_product_notavailable_shipping' language="${pageContext.request.locale.language}" /></div>
											   <c:if test="${OutOfStockOn}">
											   <c:choose>
												 <c:when test="${((null eq emailStockAlertsEnabled) || (emailStockAlertsEnabled==true)) || (inStock==false)}">
											 <div class="info normalFontSize"><a class="info lnkNotifyOOS" href="#"><bbbl:label key='lbl_pdp_product_notify_item_available' language="${pageContext.request.locale.language}" /></a></div>
											 </c:when>
											 <c:otherwise>
											 <div class="info normalFontSize hidden notifyme"><a class="info lnkNotifyOOS" href="#"><bbbl:label key='lbl_pdp_product_notify_item_available' language="${pageContext.request.locale.language}" /></a></div>
											 </c:otherwise>
											 </c:choose>
											 </c:if>
									  </div>
								</div>

						<%-- Attribute--%>
									<div class="prodAttrGrid">
													<c:set var="rebatesOn" value="${false}" /> <c:if
														test="${not empty hasRebate && hasRebate}">
														<c:if
															test="${(null != eligibleRebates) && (fn:length(eligibleRebates) == 1 )}">
															<c:set var="rebatesOn" value="${true}" />
														</c:if>
													</c:if> <dsp:droplet name="/atg/dynamo/droplet/ForEach">
														<dsp:param value="${skuAttributes}" name="array" />
														<dsp:oparam name="output">
															<dsp:getvalueof var="placeHolder" param="key" />
															<c:if
																test="${(placeHolder != null) && (placeHolder eq AttributePDPCollection)}">
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
																		<dsp:getvalueof var="attributeDescripTop"
																			param="element.attributeDescrip" />
																		<dsp:getvalueof var="imageURLTop"
																			param="element.imageURL" />
																		<dsp:getvalueof var="actionURLTop"
																			param="element.actionURL" />
																		<c:choose>
																			<c:when test="${null ne attributeDescripTop}">
																				<c:choose>
																					<c:when test="${null ne imageURLTop}">
																						<span class="attribs noMar ${sep}"><img
																							src="${imageURLTop}" alt="" /><span><dsp:valueof
																									param="element.attributeDescrip"
																									valueishtml="true" /></span></span>
																					</c:when>
																					<c:otherwise>
																						<c:choose>
																							<c:when test="${null ne actionURLTop}">
																								<span class="attribs noMar ${sep}"><a
																									href="${actionURLTop}" class="newOrPopup"><span><dsp:valueof
																												param="element.attributeDescrip"
																												valueishtml="true" /></span></a></span>
																							</c:when>
																							<c:otherwise>
																								<span class="attribs noMar ${sep}"><span><dsp:valueof
																											param="element.attributeDescrip"
																											valueishtml="true" /></span></span>
																							</c:otherwise>
																						</c:choose>
																					</c:otherwise>
																				</c:choose>
																			</c:when>
																			<c:otherwise>
																				<c:if test="${null ne imageURLTop}">
																					<c:choose>
																						<c:when test="${null ne actionURLTop}">
																							<span class="attribs ${sep}"><a
																								href="${actionURLTop}" class="newOrPopup"><img
																									src="${imageURLTop}" alt="" /></a></span>
																						</c:when>
																						<c:otherwise>
																							<span class="attribs ${sep}"><img
																								src="${imageURLTop}" alt="" /></span>
																						</c:otherwise>
																					</c:choose>
																				</c:if>
																			</c:otherwise>
																		</c:choose>
																	</dsp:oparam>
																</dsp:droplet>
															</c:if>
														</dsp:oparam>
													</dsp:droplet> <c:if
														test="${not empty hasRebate && hasRebate}">
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
																<span class="attribs ${sep1}"><a
																	href="${rebate.rebateURL}" class="links"
																	target="_blank" title="Rebate"><c:out
																			value="${rebate.rebateDescription}" escapeXml="false" /></a></span>
															</dsp:oparam>
														</dsp:droplet>
													</c:if>
												</div>
												<input type="hidden" name="price" value="${omniPrice}" class="addItemToList addItemToRegis" />
										<input type="hidden" name="finalUrl" class="finalUrl" value="${finalUrl}" />
										<input type="hidden" name="parentProductId"  value="${parentProductId}" />
										<input type="hidden" name="prodId" class="_prodId addItemToRegis productId addItemToList" value="${productID}" data-change-store-submit="prodId" data-change-store-errors="required" data-change-store-internalData="true" />
										<input type="hidden" name="skuId" value="${skuid}" class="addItemToRegis _skuId addItemToList changeStoreSkuId" data-change-store-submit="skuId" data-change-store-errors="required" data-change-store-internalData="true" />
										<input type="hidden" class="addToCartSubmitData" name="storeId" value="" data-change-store-storeid="storeId" />
										<input type="hidden" name="bts" class="addToCartSubmitData" value="${btsValue}" />
										<input type="hidden" value="${LiveClickerOn}" name="lcFlag"/>
										<input type="hidden" value="collectionGrid" name="fromPage"/>
										<input type="hidden" value="${CategoryId}" class="categoryId"/>
										
										<%-- Added for Multiple items to cart :: START --%>
										<c:if test="${inStock}">
											<dsp:input bean="CartModifierFormHandler.productIds" type="hidden" value="${productID}" />
											<dsp:input bean="CartModifierFormHandler.catalogRefIds"  type="hidden" value="${skuid}"/>
										</c:if>
										<%-- Added for Multiple items to cart :: END --%>
										<div class="fl prodPrice noPadTop">
											<c:set var="inputFieldPrice"></c:set>
											<dsp:getvalueof var="priceRangeDescription" param="element.priceRangeDescription" />
											<dsp:getvalueof var="salePriceRangeDescription" param="element.salePriceRangeDescription" />
											<c:choose>
												<c:when test="${not empty skuid}">
													<dsp:include page="product_details_price.jsp">
														<dsp:param name="product" value="${productID}"/>
														<dsp:param name="sku" value="${skuid}"/>
														<dsp:param name="inCartFlag" value="${inCartFlag}"/>
													</dsp:include>
													<c:set var="inputFieldPrice">${omniPrice}</c:set>
												</c:when>
												<c:otherwise>
												<c:choose>
													<c:when test="${not empty salePriceRangeDescription}">
														<span class="prodPriceNEW">
															<dsp:valueof converter="currency"
																param="element.salePriceRangeDescription" />
														</span>
														<dsp:getvalueof var="salePriceRange" param="element.salePriceRangeDescription"/>
														<c:choose>
															<c:when test="${fn:contains(salePriceRange,'-')}"><br /></c:when>
															<c:otherwise>&nbsp;</c:otherwise>
														</c:choose>
														<span class="prodPriceOLD">
															<span class="was">was</span>
															<span class="oldPriceNum"> <dsp:valueof converter="currency"
																	param="element.priceRangeDescription" /></span>
														</span>
														<dsp:getvalueof param="element.salePriceRangeDescription" id="saleprice"/>
														<c:set var="inputFieldPrice">${saleprice}</c:set>
													</c:when>
													<c:otherwise>
														<dsp:valueof converter="currency"
															param="element.priceRangeDescription" />
															<dsp:getvalueof param="element.priceRangeDescription" id="price"/>
															<c:set var="inputFieldPrice">${price}</c:set>
													</c:otherwise>
												</c:choose>
												</c:otherwise>
											</c:choose>
											</div>
										</li>

									<%-- Price--%>
									<li class="small-6 columns quantity no-padding-left">
									<c:choose>
										<c:when test="${prodType eq 'SIZE' || prodType eq 'FINISH' || prodType eq 'COLOR'}">
										<c:choose>
												<c:when test="${prodType eq 'SIZE'}">
													<div class="small-4 columns size-selector">
														<%-- <label for="psize1"><bbbl:label key="lbl_sizes_dropdown" language ="${pageContext.request.locale.language}"/></label> --%>
															<dsp:droplet name="/atg/dynamo/droplet/ForEach">
															<dsp:param param="element" name="array" />
															<dsp:getvalueof var="index" param="index"/>
																<dsp:oparam name="outputStart">
																	<a class="small secondary radius button dropdown" data-dropdown="selectProductSize${index}" href="#">SIZE<span></span></a>
																	<ul class="f-dropdown" data-dropdown-content="" id="selectProductSize${index}" name="selectProductSize${index}">
																</dsp:oparam>
																<dsp:oparam name="output">
																	<dsp:getvalueof var="elementValue" param="element.rollupAttribute"/>
																	<li><c:out value="${elementValue}" escapeXml="false"/></li>
																</dsp:oparam>
																<dsp:oparam name="outputEnd">
																	</ul>
																	</a>
																</dsp:oparam>
															</dsp:droplet>
														<input type="hidden" name="prodSize" class="_prodSize addItemToRegis addItemToList" value="" />
													</div>
												</c:when>
												<c:otherwise>
													<c:choose>
													<c:when test="${prodType eq 'FINISH'}">
														<div class="prodFinish fl">
															<label for="pfinish1"><bbbl:label key="lbl_finish_dropdown" language ="${pageContext.request.locale.language}"/></label>
															<div class="pfinish">
																<select name="selProdFinish" class="uniform" aria-required="false" >
																	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																	<dsp:param param="element" name="array" />
																		<dsp:oparam name="outputStart">
																			<option value=""><bbbl:label key='lbl_pdp_product_select' language="${pageContext.request.locale.language}" /></option>
																		</dsp:oparam>
																		<dsp:oparam name="output">
																			<dsp:getvalueof var="elementValue" param="element.rollupAttribute"/>
																				<dsp:getvalueof var="smallImagePath" param="element.smallImagePath"/>
																				<dsp:getvalueof var="thumbnailImagePath" param="element.thumbnailImagePath"/>

																				<c:choose>
																				<c:when test="${color eq elementValue}">
																					<option value="${elementValue}" selected="selected" data-imgURLSmall="${scene7Path}/${smallImagePath}" data-imgURLThumb="${scene7Path}/${thumbnailImagePath}">${elementValue}</option>
																				</c:when>
																				<c:otherwise>
																					<option value="${elementValue}" data-imgURLSmall="${scene7Path}/${smallImagePath}" data-imgURLThumb="${scene7Path}/${thumbnailImagePath}">${elementValue}</option>
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
														<div class="prodColor fl">
															<label for="pcolor1"><bbbl:label key="lbl_colors_dropdown" language ="${pageContext.request.locale.language}"/></label>
															<div class="pcolor">
																<select name="selProdColor" class="customSelectBoxCollection" aria-required="false" >
																	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																	<dsp:param param="element" name="array" />
																		<dsp:oparam name="outputStart">
																			<option value=""><bbbl:label key='lbl_pdp_product_colour_select' language="${pageContext.request.locale.language}" /></option>
																		</dsp:oparam>
																		<dsp:oparam name="output">
																			<dsp:getvalueof var="elementValue" param="element.rollupAttribute"/>
																				<dsp:getvalueof var="smallImagePath" param="element.smallImagePath"/>
																				<dsp:getvalueof var="thumbnailImagePath" param="element.thumbnailImagePath"/>
																				<dsp:getvalueof var="swatchImagePath" param="element.swatchImagePath"/>

																				<c:choose>
																				<c:when test="${color eq elementValue}">
																					<c:set var="colorValue" value="${color}" />
																					<option value="${elementValue}" selected="selected" data-imgSrc="${scene7Path}/${swatchImagePath}" data-imgURLSmall="${scene7Path}/${smallImagePath}" data-imgURLThumb="${scene7Path}/${thumbnailImagePath}">${elementValue}</option>
																				</c:when>
																				<c:otherwise>
																					<option value="${elementValue}" data-imgSrc="${scene7Path}/${swatchImagePath}" data-imgURLSmall="${scene7Path}/${smallImagePath}" data-imgURLThumb="${scene7Path}/${thumbnailImagePath}">${elementValue}</option>
																				</c:otherwise>
																				</c:choose>
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
										</c:when>
										<c:otherwise>
												<div class="small-12 columns quantity no-padding qty-spinner">
													<a class="button minus secondary"><span></span></a>
													<input type="text" value="1" data-max-value="99" maxlength="2" class="fl pqty qty addItemToRegis _qty itemQuantity addItemToList escapeHTMLTag quantity-input" id="quantity" name="${skuid}"/>
													
													<a class="button plus secondary"><span></span></a>
												</div>
											</c:otherwise>
										</c:choose>
									</li>
									   <%-- Price end--%>
									<li class="prodbuttonGrid grid_3 alpha clearfix small-6 columns no-padding">
												<dsp:droplet name="/atg/dynamo/droplet/ForEach">
												<dsp:param param="element.rollupAttributes" name="array" />
												<dsp:oparam name="output">
												<dsp:getvalueof var="prodType" param="key"/>
												</dsp:oparam>
												</dsp:droplet>
														<c:choose>
															<c:when test="${prodType eq 'SIZE' || prodType eq 'FINISH' || prodType eq 'COLOR'}">
															<div class="fl collectionCart">
																<div class="button_active">
																	<c:set var="chooseOptionBtn"><bbbl:label key='lbl_pdp_grid_choose_options' language="${pageContext.request.locale.language}" /></c:set>
																	<input onclick="${onClickEvent}" type="button" class="showOptionMultiSku" name="showOptionMultiSku" id="showOptionMultiSku" value="${chooseOptionBtn}" role="button" aria-pressed="false">
																</div>
															</div>
															<input type="hidden" name="prodSize" class="_prodSize addItemToRegis addItemToList" value="" />
															<input type="hidden" name="prodFinish" class="_prodFinish addItemToRegis addItemToList" value="" />
															<input type="hidden" name="prodColor" class="_prodColor addItemToRegis addItemToList" value="${colorValue}" />
															</c:when>
															<c:otherwise>
																<div class="collectionCart addToCart <c:if test="${not empty skuid && not empty isLtlItem && isLtlItem}">hidden</c:if>">
																<dsp:droplet name="TBSItemExclusionDroplet">
																		<dsp:param name="siteId" value="${appid}"/>
																		<dsp:param name="skuId" value="${skuid}"/>
																		<dsp:oparam name="output">
																		<dsp:getvalueof var="isItemExcluded" param="validItemExcludedSku"/>
																		<dsp:getvalueof var="caDisabled" param="caDisabled"/>
																		<dsp:getvalueof var="reasonCode" param="reasonCode"/>
																		<c:choose>
																			<c:when test="${isItemExcluded || caDisabled}">

																				<div class="small-12 columns product-other-links availability" style="color:red;font-weight:bold;">
							 													<dsp:include page="/browse/frag/reasonCodeMessage.jsp" flush="true" >
																					<dsp:param name="reasonCode" value="${reasonCode}"/>
																				</dsp:include>
							    											</div>

																				<div class="button_disabled">
																					<input type="submit" disabled="disabled" name="btnAddToCart"
																						value="Add to Cart" class="tiny button expand transactional"
																						onclick="rkg_micropixel('${appid}','cart')"
																						role="button" aria-pressed="false"
																						<c:if test="${not inStock}">disabled="disabled"</c:if>/>
																				</div>
																			</c:when>
																			<c:otherwise>
																				<div class="<c:if test="${not inStock}">button_disabled</c:if>">
																					<input type="submit" name="btnAddToCart"
																						value="Add to Cart" class="tiny button expand transactional"
																						onclick="rkg_micropixel('${appid}','cart')"
																						role="button" aria-pressed="false"
																						<c:if test="${not inStock}">disabled="disabled"</c:if>/>
																				</div>
																			</c:otherwise>
																		</c:choose>
																		</dsp:oparam>
																</dsp:droplet>
																</div>
																<c:choose>
																	<c:when test="${not empty skuid && not empty isLtlItem && isLtlItem}">
																		<div class="selectOption">
																	</c:when>
																	<c:otherwise>
																		<div class="selectOption hidden">
																	</c:otherwise>
																</c:choose>
																		<div class="button_active">
																			<input type="button" class="showOptionMultiSku" name="btnSelectOption" id="btnSelectOption" value="CHOOSE OPTIONS" role="button" aria-pressed="false" onclick=""/>
																		</div>
																	</div>
																<%--LTL 498 end--%>
															</c:otherwise>
														</c:choose>
									</li>
									<li class="prodbuttonGrid grid_3 alpha clearfix noMarBot <c:if test="${not empty skuid && not empty isLtlItem && isLtlItem}">hidden</c:if>">
									<%--LTL 498 end--%>
										<div class="collectionGridOptions">
										<%-- <div class="cartWrapper hidden"></div> --%>
							<%-- Add to Registry--%>
											 <c:choose>
												<c:when test="${prodType eq 'SIZE' || prodType eq 'FINISH' || prodType eq 'COLOR'}">
												</c:when>
												<c:otherwise>
											<dsp:importbean
															bean="/com/bbb/commerce/giftregistry/droplet/AddItemToGiftRegistryDroplet" />
														<dsp:importbean bean="/atg/userprofiling/Profile" />
														<dsp:importbean
															bean="/atg/dynamo/droplet/ErrorMessageForEach" />
														<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
														<dsp:importbean bean="/atg/multisite/Site" />

														<dsp:importbean
															bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
														<dsp:getvalueof var="appid" bean="Site.id" />

														<dsp:droplet name="AddItemToGiftRegistryDroplet">
															<dsp:param name="siteId" value="${appid}" />
															<c:set var="submitAddToRegistryBtn">
																<bbbl:label key='lbl_compare_add_to_registry' language="${pageContext.request.locale.language}" />
															</c:set>
															<dsp:oparam name="output">
																<c:set var="sizeValue">
																	<dsp:valueof param="size" />
																</c:set>
															</dsp:oparam>
														</dsp:droplet>

														<dsp:getvalueof var="transient" bean="Profile.transient" />
														<c:choose>
															<c:when test="${transient == 'false'}">
																<c:choose>
																	<c:when test="${sizeValue>1}">

																		<div class="prodGridRegistry addToRegistry addItemToRegNoLogin marBottom_5 fr cb">
																				<dsp:select
																					bean="GiftRegistryFormHandler.registryId"
																					name="registryId"
																					iclass="addItemToRegis addItemToRegMultipleReg customSelectBoxCollection">
																					<dsp:droplet name="AddItemToGiftRegistryDroplet">
																						<dsp:param name="siteId" value="${appid}" />
																						<dsp:oparam name="output">
																							<dsp:option>
																							<bbbl:label key='lbl_compare_add_to_registry' language="${pageContext.request.locale.language}" />
																							</dsp:option>
																							<dsp:droplet name="ForEach">
																								<dsp:param name="array"
																									param="registrySkinnyVOList" />
																								<dsp:oparam name="output">
																									<dsp:param name="futureRegList" param="element" />
																									<dsp:getvalueof var="regId"
																										param="futureRegList.registryId" />
																									<dsp:getvalueof var="event_type"
																										param="element.eventType" />
																									<dsp:option value="${regId}"
																										iclass="${event_type} enable">
																										<dsp:valueof param="element.eventType"></dsp:valueof>
																										<dsp:valueof param="element.eventDate"></dsp:valueof>
																									</dsp:option>
																								</dsp:oparam>
																							</dsp:droplet>
																						</dsp:oparam>
																					</dsp:droplet>
																					<dsp:tagAttribute name="aria-required"
																						value="false" />
																				</dsp:select>
																		</div>
																	</c:when>
																	<c:when test="${sizeValue==1}">
																		<dsp:droplet name="AddItemToGiftRegistryDroplet">
																				<dsp:param name="siteId" value="${appid}"/>
																						<dsp:oparam name="output">
																							<dsp:droplet name="ForEach">
																								<dsp:param name="array" param="registrySkinnyVOList" />
																								<dsp:oparam name="output">
																									<dsp:param name="futureRegList" param="element" />
																									<dsp:getvalueof var="regId"
																										param="futureRegList.registryId" />
																										<dsp:getvalueof var="registryName"
																										param="futureRegList.eventType" />
																									<input  type="hidden"value="${regId}" name="registryId" class="addItemToRegis" />
																									<input  type="hidden"value="${registryName}" name="registryName" class="btnAddToRegistry" />
																								</dsp:oparam>
																							</dsp:droplet>
																						</dsp:oparam>
																					</dsp:droplet>
																		<div class="addToRegistry fr cb">
																			<div class="addToRegistrybtn btnAddToRegistry">
																				<input  class="btnAddToRegistry" name="btnAddToRegistry" type="button" value="${submitAddToRegistryBtn}" />
																			</div>
																		</div>
																	</c:when>
																	<c:otherwise>
																			<div class="addToRegistry btnAddToRegNoRegistry marBottom_5 fr cb">

																				<div class="addToRegistrybtn btnAddToRegNoRegistry">
																					<input  class="btnAddToRegNoRegistry" name="btnAddToRegNoRegistry" type="button" value="${submitAddToRegistryBtn}" />
																				</div>
																			</div>
																	</c:otherwise>

																</c:choose>
															</c:when>
															<c:when test="${transient == 'true'}">
																<%-- <div class="addToRegistry addItemToRegNoLogin marBottom_5 fr cb">
																	<div class="addToRegistrybtn addItemToRegNoLogin">
																		<a href="javascript:void(0);"><bbbl:label key='lbl_compare_add_to_registry' language="${pageContext.request.locale.language}" /></a>
																	</div>
																</div> --%>

																<div class="addToRegistry clearfix">
																	<div>
																		<input class="addItemToRegNoLogin tiny button expand secondary" name="addItemToRegNoLogin" type="button" value="${submitAddToRegistryBtn}" />
																	</div>
																</div>

															</c:when>
														</c:choose>
											</c:otherwise>
											</c:choose>
											<%-- Add to Registry--%>

											<%-- R2.2.1 Story - 131 Quick View Page --%>
											<c:choose>
												<c:when test="${prodType eq 'SIZE' || prodType eq 'FINISH' || prodType eq 'COLOR'}">
												</c:when>
												<c:otherwise>
													<div class="small-12 columns">
														<div class="addToList fl padRight_5" id="btnAddToList">
															 <a href="javascript:void(0);"><bbbl:label key='lbl_compare_save_for_later' language="${pageContext.request.locale.language}" /></a>
														 </div>

														<%-- <c:if test="${MapQuestOn}">
															<div class="fl findInStore padLeft_30 noPadTop">

																<c:choose>
																	<c:when test="${bopusAllowed}">
																		<div class="disableText fr cb">
																			<bbbl:label key='lbl_compare_find_in_store' language="${pageContext.request.locale.language}" />
																		</div>
																	</c:when>
																	<c:otherwise>
																		<div class="changeStore fr cb">
																			<a href="javascript:void(0);"><bbbl:label key='lbl_compare_find_in_store' language="${pageContext.request.locale.language}" /></a>
																		</div>
																	</c:otherwise>
																</c:choose>
															</div>
														</c:if> --%>
													</div>
												</c:otherwise>
											</c:choose>
											<%-- R2.2.1 Story - 131 Quick View Page --%>
											 </div>
									</li>
								   </ul>
								</div>
						   </fieldset>

						   <%-- <c:if test="${productCount % 4 == 0}">
						   </ul>
						   <ul class="clearfix collectionGridRow">
						   </c:if> --%>
					   </li>
					</c:if>
						</dsp:oparam>
						</dsp:droplet>

					</ul>
				</dsp:form>
		</c:if>
		</dsp:page>
