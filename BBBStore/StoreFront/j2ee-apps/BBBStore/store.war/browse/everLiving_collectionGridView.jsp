<dsp:page>
	<dsp:importbean
		bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof var="fromAjax" param="fromAjax" />
	<c:set var="AttributePDPCollection">
		<bbbl:label key='lbl_pdp_attributes_collection'
			language="${pageContext.request.locale.language}" />
	</c:set>
	<dsp:getvalueof var="crossSellFlag" param="crossSellFlag" />
	<dsp:getvalueof var="desc" param="desc" />
	<c:set var="count" value="1" />
	<c:set var="productCount" value="0" />
	<c:set var="collectionId_Omniture" scope="request"></c:set>
	<c:set var="BazaarVoiceOn" scope="request"
		value="${param.BazaarVoiceOn}" />
	<c:set var="OutOfStockOn" scope="request" value="${param.OutOfStockOn}" />
	<c:set var="scene7Path">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
<c:set var="lblReviewsCount"><bbbl:label key="lbl_reviews_count" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblReviewCount"><bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblOfThe"><bbbl:label key="lbl_accessibility_of_the" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblForThe"><bbbl:label key="lbl_accessibility_for_the" language="${pageContext.request.locale.language}" /></c:set>
	<dsp:getvalueof param="parentProductId" var="parentProductId" />
	<dsp:getvalueof var="color" param="color" />
	<dsp:getvalueof var="childProducts" param="collectionVO.childProducts" />
	<c:if test="${childProducts ne null && not empty childProducts}">
		<dsp:form name="testcollectionForm" id="testcollectionForm"
			method="post">
			<ul class="clearfix collectionGridRow">
				<dsp:droplet name="/atg/dynamo/droplet/ForEach">
					<dsp:param param="collectionVO.childProducts" name="array" />

					<dsp:oparam name="output">
						<dsp:getvalueof var="childSKU" param="element.childSKUs" />
						<c:if test="${not empty childSKU}">

							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param param="element.rollupAttributes" name="array" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="prodType" param="key" />
								</dsp:oparam>
							</dsp:droplet>
							<dsp:getvalueof var="productID" param="element.productId" />
							<dsp:getvalueof var="skuAttributes" value="" />
							<dsp:getvalueof var="hasRebate" value="" />
							<dsp:getvalueof var="eligibleRebates" value="" />
							<dsp:getvalueof var="skuid" value="" />
							<dsp:getvalueof var="bopusAllowed" value="" />
							<dsp:getvalueof var="emailStockAlertsEnabled" value="" />
							<dsp:getvalueof var="skuinStock" value="" />
							<dsp:getvalueof var="childSKU" param="element.childSKUs" />
							<c:set var="CertonaContext" scope="request">${CertonaContext}${productID};</c:set>
							<c:set var="collectionId_Omniture" scope="request">${collectionId_Omniture};${productID};;;;eVar29=${parentProductId},</c:set>
							<c:if test="${(null != childSKU) && (fn:length(childSKU) == 1 )}">
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param name="array" value="${childSKU}" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="childSkuId" param="element" />
										<dsp:droplet name="ProductDetailDroplet">
											<dsp:param name="id" value="${productID}" />
											<dsp:param name="siteId" param="siteId" />
											<dsp:param name="skuId" value="${childSkuId}" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="selectedSKUTemp" param="selected" />
												<c:if test="${not empty selectedSKUTemp}">
													<c:set var="selectedSKU" value="${selectedSKUTemp}" />
													<dsp:getvalueof var="selectedSKUStock" param="inStock" />
												</c:if>
												<dsp:getvalueof var="skuAttributes"
													param="pSKUDetailVO.skuAttributes" />
												<dsp:getvalueof var="hasRebate"
													param="pSKUDetailVO.hasRebate" />
												<dsp:getvalueof var="eligibleRebates"
													param="pSKUDetailVO.eligibleRebates" />
												<dsp:getvalueof var="skuid" param="pSKUDetailVO.skuId" />
												<dsp:getvalueof var="bopusAllowed"
													param="pSKUDetailVO.bopusAllowed" />
												<%-- <input type="hidden" name="skuId" value="${skuid}" class="_skuId addItemToRegis addItemToList" /> --%>
												<dsp:getvalueof var="emailStockAlertsEnabled"
													param="pSKUDetailVO.emailStockAlertsEnabled" />
												<dsp:getvalueof var="skuinStock" param="inStock" />
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>

								</dsp:droplet>
							</c:if>

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
							<c:set var="productCount" value="${productCount + 1}"
								scope="page" />
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
							</c:choose>
							<fieldset class="registryDataItemsWrap listDataItemsWrap"
								itemscope <c:if test="${empty fromAjax}">itemtype="http://schema.org/Product"</c:if> >

								<dsp:getvalueof var="prodImage"
									param="element.productImages.thumbnailImage" />
								<dsp:getvalueof param="element.name" var="prodName" />
								<c:choose>
									<c:when test="${empty prodImage}">
										<img class="hidden productImage" height="83" width="83"
											src="${imagePath}/_assets/global/images/no_image_available.jpg"
											alt="${prodName}" />
									</c:when>
									<c:otherwise>
										<img class="hidden productImage noImageFound" height="83"
											width="83" src="${scene7Path}/${prodImage}" alt="${prodName}" />
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
									<c:when
										test="${(crossSellFlag ne null) && (crossSellFlag eq 'true')}">

										<c:set var="onClickEvent">javascript:pdpCrossSellProxy('crossSell', '${desc}')</c:set>
									</c:when>
									<c:otherwise>

										<c:set var="onClickEvent" value="" />
									</c:otherwise>
								</c:choose>
								<dsp:getvalueof var="CategoryId" param="categoryId" />
								<div class="grid_3 alpha marBottom_10">
									<c:choose>
										<c:when test="${not empty CategoryId}">
											<dsp:a iclass="prodImg"
												page="${finalUrl}?poc=${parentProductId}&categoryId=${CategoryId}"
												title="${prodName}" onclick="${onClickEvent}">
												<dsp:getvalueof var="showImage"
													param="collectionVO.showImagesInCollection" />
												<c:if test="${showImage}">
													<c:choose>
														<c:when test="${empty prodImage}">
															<img class="fl"
																src="${imagePath}/_assets/global/images/no_image_available.jpg"
																width="229" height="229" alt="${prodName}" />
														</c:when>
														<c:otherwise>
															<img class="fl noImageFound"
																src="${scene7Path}/${prodImage}" width="229"
																height="229" alt="${prodName}" />
														</c:otherwise>
													</c:choose>
												</c:if>
											</dsp:a>
										</c:when>
										<c:otherwise>
											<dsp:a iclass="prodImg"
												page="${finalUrl}?poc=${parentProductId}"
												title="${prodName}" onclick="${onClickEvent}">
												<dsp:getvalueof var="showImage"
													param="collectionVO.showImagesInCollection" />
												<c:if test="${showImage}">
													<c:choose>
														<c:when test="${empty prodImage}">
															<img class="fl"
																src="${imagePath}/_assets/global/images/no_image_available.jpg"
																width="229" height="229" alt="${prodName}" />
														</c:when>
														<c:otherwise>
															<img class="fl noImageFound"
																src="${scene7Path}/${prodImage}" width="229"
																height="229" alt="${prodName}" />
														</c:otherwise>
													</c:choose>
												</c:if>
											</dsp:a>
										</c:otherwise>
									</c:choose>
								</div>
								<div class="grid_3 alpha">
									<ul class="prodInfoGrid clearfix">
										<li class="pdpCollectionProdAttribs">
											<%-- Product name --%>
											<div class="prodNameGrid prodName">
												<dsp:a onclick="${onClickEvent}"
													page="${finalUrl}?poc=${parentProductId}">
													<dsp:valueof param="element.name" valueishtml="true" />
												</dsp:a>
											</div> <%-- Rating --%> <dsp:getvalueof var="ReviewCountvar"
												param="element.bvReviews.totalReviewCount" /> <c:set
												var="ReviewCount">${ReviewCountvar}</c:set> <c:if
												test="${BazaarVoiceOn}">
												<dsp:getvalueof var="ratingAvailable"
													param="element.bvReviews.ratingAvailable"></dsp:getvalueof>
												<c:choose>
													<c:when test="${ratingAvailable == true}">
														<dsp:getvalueof var="fltValue"
															param="element.bvReviews.averageOverallRating" />
														<dsp:getvalueof param="element.bvReviews.totalReviewCount"
															var="totalReviewCount" />
														<c:choose>
															<c:when test="${totalReviewCount == 1}">
																<div
																	class="prodReviewGrid prodReview clearfix prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"
																	title="<dsp:valueof param="element.bvReviews.averageOverallRating"/>">
																	<a
																		href="${finalUrl}?skuId=${catalogRefId}&amp;categoryId=${CategoryId}&amp;showRatings=true" role="link" aria-label="${totalReviewCount} ${lblReviewCount} ${lblForThe} ${prodName}">
																${ReviewCount}<bbbl:label key='lbl_review_count'
																			language="${pageContext.request.locale.language}" />
																	</a>
																</div>
															</c:when>
															<c:otherwise>
																<div
																	class="prodReviewGrid prodReview clearfix prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"
																	title="<dsp:valueof param="element.bvReviews.averageOverallRating"/>">
																	<a
																		href="${finalUrl}?skuId=${catalogRefId}&amp;categoryId=${CategoryId}&amp;showRatings=true" role="link" aria-label="${totalReviewCount} ${lblReviewsCount} ${lblForThe} ${prodName}">
																${ReviewCount}<bbbl:label key='lbl_reviews_count'
																			language="${pageContext.request.locale.language}" />
																	</a>
																</div>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<dsp:getvalueof var="fltValue"
															param="element.bvReviews.averageOverallRating" />
														<dsp:getvalueof var="totalReviewCount" value="0"></dsp:getvalueof>
														<div
															class="prodReviewGrid prodReview clearfix prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"
															title="<dsp:valueof param="element.bvReviews.averageOverallRating"/>">
															<a
																href="${finalUrl}?skuId=${catalogRefId}&amp;categoryId=${CategoryId}&amp;showRatings=true" role="link" aria-label="${totalReviewCount} ${lblReviewCount} ${lblForThe} ${prodName}">
														${ReviewCount}<bbbl:label key='lbl_review_count'
																	language="${pageContext.request.locale.language}" />
															</a>
														</div>
													</c:otherwise>
												</c:choose>
											</c:if> <input type="hidden" class="isInStock" value="${inStock}" />

											<%-- Rating End--%>
											<div
												class="priceQuantityNotAvailable <c:if test="${inStock==true}">hidden</c:if>">
												<div class="gridMessage">
													<input type="hidden" name="oosProdId" value="${oosProdId}" />
													<c:choose>
														<c:when test="${inStock==true}">
															<input type="hidden" value="" name="oosSKUId"
																class="_oosSKUId" />
														</c:when>
														<c:otherwise>
															<input type="hidden" value="${skuid}" name="oosSKUId"
																class="_oosSKUId" />
														</c:otherwise>
													</c:choose>
													<div class="error noMarBot">
														<bbbl:label key='lbl_pdp_product_notavailable_shipping'
															language="${pageContext.request.locale.language}" />
													</div>
													<c:if test="${OutOfStockOn}">
														<c:choose>
															<c:when
																test="${(null eq emailStockAlertsEnabled) || (emailStockAlertsEnabled==true) || (inStock==false)}">
																<div class="info">
																	<a class="info lnkNotifyOOS" href="#"><bbbl:label
																			key='lbl_pdp_product_notify_item_available'
																			language="${pageContext.request.locale.language}" />
																		&raquo;</a>
																</div>
															</c:when>
															<c:otherwise>
																<div class="info hidden">
																	<a class="info lnkNotifyOOS" href="#"><bbbl:label
																			key='lbl_pdp_product_notify_item_available'
																			language="${pageContext.request.locale.language}" />
																		&raquo;</a>
																</div>
															</c:otherwise>
														</c:choose>
													</c:if>
												</div>
											</div> <%-- Attribute--%>
											<div class="prodAttrGrid">
												<c:set var="rebatesOn" value="${false}" />
												<c:if test="${not empty hasRebate && hasRebate}">
													<c:if
														test="${(null != eligibleRebates) && (fn:length(eligibleRebates) == 1 )}">
														<c:set var="rebatesOn" value="${true}" />
													</c:if>
												</c:if>
												<dsp:droplet name="/atg/dynamo/droplet/ForEach">
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
												</dsp:droplet>
												<c:if test="${not empty hasRebate && hasRebate}">
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
																href="${rebate.rebateURL}" class="links" target="_blank"
																title="Rebate"><c:out
																		value="${rebate.rebateDescription}" escapeXml="false" /></a></span>
														</dsp:oparam>
													</dsp:droplet>
												</c:if>
											</div>
										</li>

										<%-- Price--%>
										<%--removing the prices from ever living collections as we do not need these in 2.2 release --%>
										<%-- <li class="prodpriceGrid">
									   		<c:set var="inputFieldPrice"></c:set>
										<dsp:getvalueof var="priceRangeDescription" param="element.priceRangeDescription" />
										<dsp:getvalueof var="salePriceRangeDescription" param="element.salePriceRangeDescription" />
										<c:choose>
											<c:when test="${not empty skuid}">
												<dsp:include page="product_details_price.jsp">	
													<dsp:param name="product" value="${productID}"/>
													<dsp:param name="sku" value="${skuid}"/>
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
									   </li> --%>
										<%-- Price end--%>
										<%--removing the buttons from ever living collections as we do not need these in 2.2 release --%>

										<%--<li class="prodbuttonGrid grid_3 alpha clearfix">
											 <c:choose>
												<c:when test="${prodType eq 'SIZE' || prodType eq 'FINISH' || prodType eq 'COLOR'}">
												</c:when>
												<c:otherwise>
												<div class="spinner fl">
													<label id="lblcollGridDecreaseQty" class="txtOffScreen" for="collGridDecreaseQty"><bbbl:label key="lbl_decrease_quantity" language="${pageContext.request.locale.language}" /></label>
													<a title="<bbbl:label key='lbl_decrease_quantity' language="${pageContext.request.locale.language}" />" id="collGridDecreaseQty" class="scrollDown down" href="#"><span class="txtOffScreen"><bbbl:label key="lbl_decrease_quantity" language="${pageContext.request.locale.language}" /></span></a>
													<input name="qty" id="pqty${productID}" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="fl pqty qty addItemToRegis _qty itemQuantity addItemToList escapeHTMLTag" type="text" value="1"  data-change-store-submit="qty" data-change-store-errors="required digits nonZero" role="textbox" aria-required="true" aria-describedby="pqty${productID}" maxlength="2" />
													<label id="lblcollGridIncreaseQty" class="txtOffScreen" for="collGridIncreaseQty"><bbbl:label key="lbl_increase_quantity" language="${pageContext.request.locale.language}" /></label>
												    <a id="collGridIncreaseQty" title="<bbbl:label key='lbl_increase_quantity' language="${pageContext.request.locale.language}" />" class="scrollUp up" href="#"><span class="txtOffScreen"><bbbl:label key="lbl_increase_quantity" language="${pageContext.request.locale.language}" /></span></a> 
												</div>
												 </c:otherwise>
												 </c:choose>
												 <input type="hidden" name="finalUrl" class="finalUrl" value="${finalUrl}" />
												<input type="hidden" name="prodId" class="_prodId addItemToRegis productId addItemToList" value="${productID}" data-change-store-submit="prodId" data-change-store-errors="required" data-change-store-internalData="true" />
												<input type="hidden" name="skuId" value="${skuid}" class="addItemToRegis _skuId addItemToList changeStoreSkuId" data-change-store-submit="skuId" data-change-store-errors="required" data-change-store-internalData="true" />
												<input type="hidden" class="addToCartSubmitData" name="storeId" value="" data-change-store-storeid="storeId" />
												<input type="hidden" name="bts" class="addToCartSubmitData" value="${btsValue}" />
							                     				 
												 
												 
												 <dsp:droplet name="/atg/dynamo/droplet/ForEach">
												<dsp:param param="element.rollupAttributes" name="array" />
												<dsp:oparam name="output">
												<dsp:getvalueof var="prodType" param="key"/>
												</dsp:oparam>
												</dsp:droplet>
														<c:choose>
															<c:when test="${prodType eq 'SIZE' || prodType eq 'FINISH' || prodType eq 'COLOR'}">
															<div class="fl collectionCart">
																<div class="button">
																	<input type="button" class="showOptionMultiSku" name="showOptionMultiSku" value="CHOOSE OPTIONS" role="button" aria-pressed="false">
																</div>
															</div>
															<input type="hidden" name="prodSize" class="_prodSize addItemToRegis addItemToList" value="" />
															<input type="hidden" name="prodFinish" class="_prodFinish addItemToRegis addItemToList" value="" />
															<input type="hidden" name="prodColor" class="_prodColor addItemToRegis addItemToList" value="${colorValue}" />
															</c:when>
															<c:otherwise>
																<div class="fr collectionCart addToCart padLeft_5">
																	<div class="button button_active button_active_orange <c:if test="${not inStock}">button_disabled</c:if>">
																		<input type="submit" name="btnAddToCart"
																			value="Add to Cart"
																			onclick="rkg_micropixel('${appid}','cart')"
																			role="button"
																			<c:if test="${not inStock}">disabled="disabled"</c:if>/>
																	</div>
																</div>
															</c:otherwise>
														</c:choose>
									   </li> 
									 <li class="prodbuttonGrid grid_3 alpha clearfix noMarBot">
									    <div class="collectionGridOptions">
							
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
																	    
																		<div class="prodGridRegistry addToRegistry addItemToRegNoLogin fr cb">
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
																										iclass="${event_type}">
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
                																<a href="javascript:void(0);">
                																	<bbbl:label key='lbl_compare_add_to_registry' language="${pageContext.request.locale.language}" />
                																</a>
                															</div>
 																		</div>
																	</c:when>
																	<c:otherwise>
																		<div class="addToRegistry fr cb">
	               															<div class="addToRegistrybtn btnAddToRegNoRegistry">
	               																<a href="javascript:void(0);">
	               																	<bbbl:label key='lbl_compare_add_to_registry' language="${pageContext.request.locale.language}" />
	               																</a>
	               															</div>
	 																	</div>
																	</c:otherwise> 

																</c:choose>
															</c:when>
															<c:when test="${transient == 'true'}">
																<div class="addToRegistry fr cb">
	                												<div class="addToRegistrybtn addItemToRegNoLogin">
	                													<a href="javascript:void(0);">
	                														<bbbl:label key='lbl_compare_add_to_registry' language="${pageContext.request.locale.language}" />
	                													</a>
	                												</div>
   																</div>
															</c:when>
														</c:choose>
											 </c:otherwise>
											</c:choose>
											 
											 
											 <div class="width_3 gridStore">
												 <c:if test="${MapQuestOn}">
													 <c:choose>
														<c:when test="${prodType eq 'SIZE' || prodType eq 'FINISH' || prodType eq 'COLOR'}">
														</c:when>
														<c:otherwise>
												 			<div class="prodGridStore changeStore fr cb">
											 					<a href="javascript:void(0);">Find in Store</a>
											 				</div>
														</c:otherwise>
													</c:choose>
												</c:if>
									             
									             <c:choose>
													<c:when test="${prodType eq 'SIZE' || prodType eq 'FINISH' || prodType eq 'COLOR'}">
													</c:when>
													<c:otherwise>			 
											     		<div class="prodSFLStore addToList fr cb" id="btnAddToList" onclick="rkg_micropixel('BedBathUS','wish')">
													         <a href="javascript:void(0);">Save For Later</a>
													     </div>
												    </c:otherwise>
												</c:choose>
											</div>
                                          </div>                                         									   
									   </li>  --%>
									</ul>
								</div>
							</fieldset>

							<c:if test="${productCount % 4 == 0}">
			</ul>
			<ul class="clearfix collectionGridRow">
	</c:if>

	</c:if>
	</dsp:oparam>
	</dsp:droplet>

	</ul>
	</dsp:form>
	</c:if>
</dsp:page>