<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Range" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
	<dsp:getvalueof var="count" param="BBBProductListVO.bbbProductCount" />
	<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductListHandler"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<c:set var="rowIndex" value="1" />
	<dsp:getvalueof var="plpGridSize" param="plpGridSize"/>
	<dsp:getvalueof var="origSearchWord" param="searchTerm"/>
	<c:set var="compareProducts"><bbbc:config key="compareProducts" configName="FlagDrivenFunctions" /></c:set>

	<c:choose>
		<c:when test="${plpGridSize == '3'}">
			<c:set var="parentGridClass" value="grid_9"/>
		</c:when>
		<c:otherwise>
			<c:set var="parentGridClass" value="grid_10"/>
		</c:otherwise>
	</c:choose>
	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="browseSearchVO.partialSearchResults"/>
		<dsp:param name="elementName" value="browseSearchVO"/>
		<dsp:oparam name="output">
			<dsp:param name="BBBProductListVO" param="browseSearchVO.bbbProducts"/>
			 <c:set var="origSearchList" value="${fn:split(origSearchWord,' ')}"/>
			 <dsp:getvalueof var="partialCount" param="BBBProductListVO.bbbProductCount" />
			 <c:if test="${partialCount!=0}">
			 <div class="partial_matching_heading <c:out value="${parentGridClass}"/>">
						<h3><bbbl:label key="lbl_partialheader_search_results_page_2" language="${pageContext.request.locale.language}" />&nbsp;
						<dsp:droplet name="com/bbb/search/droplet/StrikePartialSearchKeyword">
							<dsp:param name="origSearchList" value="${origSearchList}"/>
							<dsp:param name="partialSearchKeyword" param="key"/>
							<dsp:getvalueof var="origSearchListLength" param="size"/>
							<dsp:getvalueof var="count" param="count"/>
							<dsp:getvalueof var="origTerm" param="searchWord"></dsp:getvalueof>
								<dsp:oparam name="search">
									<c:if test="${count == 1}"><c:out value="\""></c:out></c:if><span class="matched-word"><c:out value="${origTerm}" escapeXml="true"></c:out></span><c:if test="${count == origSearchListLength}"><c:out value="\""></c:out></c:if>
								</dsp:oparam>
								<dsp:oparam name="strike">
									<c:if test="${count == 1}"><c:out value="\""></c:out></c:if><span class="unmatched-word"><c:out value="${origTerm}" escapeXml="true"></c:out></span><c:if test="${count == origSearchListLength}"><c:out value="\""></c:out></c:if>
								</dsp:oparam>
							</dsp:droplet>
						</h3>
						<dsp:getvalueof var="partialSearchTerm" param="browseSearchVO.partialKeywordURL"/>
						<dsp:a iclass="fr marTop_10" href="${contextPath}/s/${partialSearchTerm}" ><dsp:param name="partialFlag" value="true"/><bbbl:label key="lbl_partial_see_all_link" language="${pageContext.request.locale.language}" />&nbsp;<dsp:valueof param="BBBProductListVO.bbbProductCount"/>&nbsp;<bbbl:label key="lbl_partial_matches_link" language="${pageContext.request.locale.language}" />
						</dsp:a>
			</div>

		<div class="scrollable2 scrollable marBottom_20 <c:out value="${parentGridClass}"/> noMar marTop_20">
				<div class="productShadow product"></div>

			 <div class="partial-matching-wrapper <c:out value="${parentGridClass}"/> noMar viewport">
					<div class="grid_10 noMar overview">
						<ul id="row1" class="clearfix prodGridRow">
						<dsp:droplet name="ForEach">
							<dsp:param name="array" param="browseSearchVO.bbbProducts.bbbProducts"/>
							<dsp:getvalueof var="prodCount" param="array"></dsp:getvalueof>
							<c:set var="prodCount" value="${fn:length(prodCount)}"></c:set>
							<dsp:param name="elementName" value="BBBProduct"/>
							<dsp:oparam name="output">
								<dsp:getvalueof var="productId" param="BBBProduct.productID" />
								<dsp:getvalueof var="productName" param="BBBProduct.productName"/>
								<%-- Check for Swatch Flag attribute returned from Search Engine--%>
								<dsp:getvalueof var="swatchFlag" param="BBBProduct.swatchFlag"/>
								<dsp:getvalueof var="rollupFlag" param="BBBProduct.rollupFlag"/>
								<dsp:getvalueof var="collectionFlag" param="BBBProduct.collectionFlag"/>
								<dsp:getvalueof var="promoSC" param="promoSC"/>
								<dsp:getvalueof var="portrait" param="portrait"/>
								<dsp:getvalueof id="count" param="count" />
								<dsp:getvalueof id="productCountDisplayed" param="size" />

								<c:set var="imageSize" value="146"/>
									<c:if test="${count == 1}">
									<li class="grid_2 product alpha">
									</c:if>
									<c:if test="${count> 1 && count<prodCount}">
									<li class="grid_2 product">
									</c:if>
									<c:if test="${count == prodCount && prodCount!=1}">
									<li class="grid_2 product omega">
									</c:if>
										<div class="productContent">
															<dsp:getvalueof var="seoUrl" param="BBBProduct.seoUrl"/>
															<c:choose>
																<c:when test="${empty seoUrl}">
																	<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
																		<dsp:param name="id" param="BBBProduct.productID" />
																		<dsp:param name="itemDescriptorName" value="product" />
																		<dsp:param name="repositoryName"
																			value="/atg/commerce/catalog/ProductCatalog" />
																		<dsp:oparam name="output">
																		<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
																				param="url" />
																		</dsp:oparam>
																	</dsp:droplet>
																</c:when>
																<c:otherwise>
																	<c:set var="finalUrl" value="${seoUrl}"></c:set>
																</c:otherwise>
															</c:choose>
															<c:url value="${finalUrl}" var="urlSe">
																<c:param name="Keyword" value="${searchTerm}"/>
															</c:url>
															<dsp:a iclass="prodImg" href="${urlSe}" title="${productName}">
																<dsp:getvalueof var="imageURL" param="BBBProduct.imageURL"/>

																<%-- Thumbnail image exists OR not--%>
																<c:choose>
																	<c:when test="${not empty imageURL}">
																		<img class="productImage noImageFound" src="${scene7Path}/${imageURL}" height="${imageSize}" width="${imageSize}" alt="image of ${productName}" />
																	</c:when>
																	<c:otherwise>
																		<img class="productImage" src='${imagePath}/_assets/global/images/no_image_available.jpg' alt="image of ${productName}" width="${imageSize}" height="${imageSize}" />
																	</c:otherwise>
																</c:choose>
															</dsp:a>
															<c:if test="${swatchFlag == '1'}">
																<div class="prodSwatchesContainer clearfix">
																	<div class="prodSwatches">
																		<dsp:droplet name="ForEach">
																			<dsp:param name="array" param="BBBProduct.colorSet" />
																			<dsp:oparam name="output">
																				<dsp:getvalueof var="id" param="key"/>

																				 <%-- Added for R2-141 --%>
																				<c:choose>
																				 <c:when test="${rollupFlag eq 'true' || collectionFlag == '1'}">
																				 <dsp:getvalueof var="colorValue" param="BBBProduct.color"/>
																				   <dsp:getvalueof var="colorParam" value="color"/>
										                                           <c:url value="${finalUrl}" var="colorProdUrl">
										                                                <c:param name="categoryId" value="${CategoryId}"/>
										                                                <c:param name="color" value="${colorValue}"/>
										                                                <c:param name="Keyword" value="${searchTerm}"/>
										                                            </c:url>
																				  </c:when>
																				 <c:otherwise>
																				   <dsp:getvalueof var="colorValue" param="BBBProduct.skuID"/>
																				   <dsp:getvalueof var="colorParam" value="skuId"/>
										                                           <c:url value="${finalUrl}" var="colorProdUrl">
										                                                <c:param name="categoryId" value="${CategoryId}"/>
										                                                <c:param name="skuId" value="${colorValue}"/>
										                                                <c:param name="Keyword" value="${searchTerm}"/>
										                                            </c:url>
																				 </c:otherwise>
																				</c:choose>
																				<dsp:getvalueof var="productUrl" param="BBBProduct.skuMedImageURL"/>
																				<dsp:getvalueof var="swatchUrl" param="BBBProduct.skuSwatchImageURL"/>
																				<dsp:getvalueof var="colorName" param="BBBProduct.color"/>

																				<%-- R2.2 Stroy 116. Changing image sizes to 229 px if grid size is 3x3 --%>
																				<c:if test="${is_gridview_3x3}">
																					<c:set var="productUrl"><c:out value='${fn:replace(productUrl,"$146$","$229$")}'/></c:set>
																				</c:if>
																				<c:choose>
																					<c:when test="${not empty productUrl}">
																						<a href="${colorProdUrl}" class="fl" title="${colorName}"  data-color-value="${colorValue}" data-color-param="${colorParam}"  data-main-image-src="${scene7Path}/${productUrl}">
																							<span>
																								<img src="${scene7Path}/${swatchUrl}" height="10" width="10" alt="${colorName}"/>
																							</span>
																						</a>
																					</c:when>
																					<c:otherwise>
																						<a href="${colorProdUrl}" class="fl" title="${colorName}" data-color-value="${colorName}" data-color-value="${colorValue}" data-color-param="${colorParam}" data-main-image-src="${imagePath}/_assets/global/images/no_image_available.jpg">
																							<span>
																								<img src="${scene7Path}/${swatchUrl}" height="10" width="10" alt="${colorName}"/>
																							</span>
																						</a>
																					</c:otherwise>
																				</c:choose>
																			</dsp:oparam>
																		</dsp:droplet>
																		<div class="clear"></div>
																	</div>
																</div>
															</c:if>

													<c:choose>
														<c:when test="${collectionFlag eq 1}">   
															<c:set var="quickViewClass" value="showOptionsCollection"/>
														</c:when>
														<c:otherwise>
															<c:set var="quickViewClass" value="showOptionMultiSku"/>
														</c:otherwise>
													</c:choose>
											<ul class="prodInfo">
												<li class="listDataItemsWrap">
													 <div class="padBottom_10 textCenter quickViewAndCompare">
														<span tabindex="0" class="quickView ${quickViewClass}"><bbbl:label key="lbl_product_quick_view" language="${pageContext.request.locale.language}" /></span>
														<div class="checkboxItem input clearfix noPad noBorder">
														 <c:if test="${compareProducts eq true}">
															<dsp:getvalueof var="productId" param="BBBProduct.productID"/>
															<dsp:getvalueof var="inCompareDrawer" param="BBBProduct.inCompareDrawer"/>
																<c:choose>
																	<c:when test="${inCompareDrawer eq true}">
																			<input name="Compare" id="compareChkTxt_${productId}" class="compareChkTxt" type="checkbox" value="compareItem" data-productId="${productId}"  checked = "true" />
																			<label for="compareChkTxt_${productId}" class="compareChkTxt"><bbbl:label key="lbl_compare_product" language="${pageContext.request.locale.language}" /></label>
																	</c:when>
																	<c:otherwise>
																			<input name="Compare" id="compareChkTxt_${productId}" class="compareChkTxt" type="checkbox" value="compareItem" data-productId="${productId}" />
																			<label for="compareChkTxt_${productId}" class="compareChkTxt"><bbbl:label key="lbl_compare_product" language="${pageContext.request.locale.language}" /></label>
																	</c:otherwise>
																</c:choose>
															</c:if>
														</div>
	                    
															<%-- Data to submit for Add to Cart / Find In Store --%>
															<input type="hidden" class="addItemToList addItemToRegis itemQuantity" value="1" name="qty" data-change-store-submit="qty" />
															<input type="hidden" data-change-store-internaldata="true" data-change-store-errors="required" data-change-store-submit="prodId" value="${productId}" class="_prodId addItemToRegis productId addItemToList" name="prodId"/>
															<input type="hidden" data-change-store-internaldata="true" data-change-store-errors="required" data-change-store-submit="skuId" class="addItemToRegis _skuId addItemToList changeStoreSkuId" value="${skuID}" name="skuId"/>
															<input type="hidden" name="price" value="${omniPrice}" class="addItemToList addItemToRegis" />
															<input type="hidden" data-change-store-storeid="storeId" value="" name="storeId" class="addToCartSubmitData"/>
															<input type="hidden" name="parentProductId" value="${productId}" class="addItemToList addItemToRegis" />
															<input type="hidden" value="" class="addToCartSubmitData" name="bts" data-change-store-storeid="bts"/>
															<input type="hidden" value="true" class="addItemToList addItemToRegis" name="fromSearch"/>
															<input type="hidden" value="${LiveClickerOn}" name="lcFlag"/>
															<input type="hidden" value="${CategoryId}" class="categoryId"/>
													   </div>
												</li>
												<li class="prodName">
													<dsp:getvalueof var="seoUrl" param="BBBProduct.seoUrl"/>
													<c:choose>
														<c:when test="${empty seoUrl}">
															<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
																<dsp:param name="id" param="BBBProduct.productID" />
																<dsp:param name="itemDescriptorName" value="product" />
																<dsp:param name="repositoryName"
																	value="/atg/commerce/catalog/ProductCatalog" />
																<dsp:oparam name="output">
																<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
																		param="url" />
																</dsp:oparam>
															</dsp:droplet>
														</c:when>
														<c:otherwise>
															<c:set var="finalUrl" value="${seoUrl}"></c:set>
														</c:otherwise>
													</c:choose>
													<c:url value="${finalUrl}" var="urlSe">
														<c:param name="Keyword" value="${searchTerm}"/>
													</c:url>
													<dsp:getvalueof var="prodName" param="BBBProduct.productName"/>
													<dsp:a  href="${urlSe}" title="${prodName}">
														<dsp:valueof param="BBBProduct.productName" valueishtml="true" />
													</dsp:a>
												</li>
												<li class="prodPrice">
													<dsp:valueof converter="currency" param="BBBProduct.priceRange" valueishtml="true" />
												</li>
													<dsp:droplet name="ForEach">
														<dsp:param name="array" param="BBBProduct.attribute" />
														<dsp:oparam name="output">
															<li><dsp:valueof param="element" valueishtml="true"/></li>
														</dsp:oparam>
													</dsp:droplet>

										  				<c:if test="${BazaarVoiceOn}">
																<dsp:getvalueof var="reviews" param="BBBProduct.reviews"/>
																<dsp:getvalueof var="ratings" param="BBBProduct.ratings" vartype="java.lang.Integer"/>
																<dsp:getvalueof var="rating" value="${ratings * 10}" vartype="java.lang.Integer"/>
																<c:choose>
																	<c:when test="${ratings ne null && ratings ne '0'}">
																		<li>
																			<span class="prodReviews ratingsReviews block clearfix prodReviews<fmt:formatNumber value="${rating}" pattern="#0" />"></span>
																			<span class="block normalFontSize padTop_5">
																				<c:if test="${reviews ne null && reviews ne '0' && reviews gt '1'}">
																					<dsp:valueof param="BBBProduct.reviews"/><bbbl:label key="lbl_reviews_count" language="${pageContext.request.locale.language}" />
																				</c:if>
																				<c:if test="${reviews ne null && reviews ne '0' && reviews eq '1'}">
																					<dsp:valueof param="BBBProduct.reviews"/><bbbl:label key="lbl_review_count" language="${pageContext.request.locale.language}" />
																				</c:if>
																			</span>
																		</li>
																	</c:when>
																	<c:otherwise>
																		<li>
																		<span class="prodReviews ratingsReviews writeReview block"></span>
																		<dsp:getvalueof var="seoUrl" param="BBBProduct.seoUrl"/>
																		<c:choose>
																		<c:when test="${empty seoUrl}">
																			<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
																				<dsp:param name="id" param="BBBProduct.productID" />
																				<dsp:param name="itemDescriptorName" value="product" />
																				<dsp:param name="repositoryName"
																					value="/atg/commerce/catalog/ProductCatalog" />
																				<dsp:oparam name="output">
																				<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
																						param="url" />
																				</dsp:oparam>
																			</dsp:droplet>
																		</c:when>
																		<c:otherwise>
																		<c:set var="finalUrl" value="${seoUrl}"></c:set>
																		</c:otherwise>
																		</c:choose>
																		<c:set var="writeReviewLink"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></c:set>
																		<span class="block normalFontSize padTop_5">
																			<dsp:a  page="${finalUrl}?categoryId=${CategoryId}&writeReview=true" title="${writeReviewLink}">${writeReviewLink}</dsp:a>
																		</span> 
																				
																		</li>
																			<dsp:getvalueof var="intlRestrictionMssgFlag" param="element.intlRestricted"/>
																		<c:if test="${isInternationalCustomer and intlRestrictionMssgFlag}">
											<div class="notAvailableIntShipMsg compListIntShipMsg cb padTop_15 clearfix"><bbbl:label key="lbl_plp_intl_restrict_list_msg" language="${pageContext.request.locale.language}"/></div>
											</c:if>
																		
																	</c:otherwise>
																</c:choose>
														</c:if>		
											</ul>
										</div>
										</li>
									
							</dsp:oparam>
						</dsp:droplet>
					</ul>
				</div>
			</div>
		   <div class="scrollbar"><div class="track"><div class="thumb"><div class="end"></div></div></div></div>
		  </div>
		</c:if>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>