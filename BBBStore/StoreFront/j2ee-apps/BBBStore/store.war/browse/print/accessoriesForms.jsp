<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet"/>
	<dsp:getvalueof param="isEverLivingProd" var="isEverLivingProd"/>
	<c:set var="AttributePDPCollection">
		<bbbl:label key='lbl_pdp_attributes_collection' language="${pageContext.request.locale.language}" />
	</c:set>
	<dsp:getvalueof var="childProducts" param="collectionVO.childProducts"/>
		<c:if test="${childProducts ne null && not empty childProducts}">					
			<div id="shopAccessories" class="collectionItemsBox grid_12 clearfix collectionListView">
					
					<a name="collectionItems"></a>
					<h2><bbbl:label key='lbl_pdp_accessories' language="${pageContext.request.locale.language}" /></h2>
					<ul class="clearfix">
						
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param param="collectionVO.childProducts" name="array" />	
													
									<dsp:oparam name="output">
									<li class="bdrBot collectionItems clearfix">
										<fieldset class="registryDataItemsWrap" itemscope itemtype="http://schema.org/Product">
										<dsp:getvalueof var="productID" param="element.productId"/>
										<dsp:getvalueof var="childSKU" param="element.childSKUs"/>
										<dsp:droplet name="/atg/dynamo/droplet/ForEach">
											<dsp:param name="array" value="${childSKU}" />
											<dsp:oparam name="output">			
												<dsp:getvalueof var="childSkuId" param="element"/>
												<dsp:droplet name="ProductDetailDroplet">
													<dsp:param name="id" value="${productID}" />
													<dsp:param name="siteId" param="siteId"/>
													<dsp:param name="skuId" value="${childSkuId}"/>
													<dsp:oparam name="output">
														<dsp:getvalueof var="skuAttributes" param="pSKUDetailVO.skuAttributes"/>
														<dsp:getvalueof var="skuid" param="pSKUDetailVO.skuId"/>
														<dsp:getvalueof var="hasRebate" param="pSKUDetailVO.hasRebate"/>
														<dsp:getvalueof var="eligibleRebates" param="pSKUDetailVO.eligibleRebates"/>
														<%-- <input type="hidden" name="skuId" value="${skuid}" class="_skuId addItemToRegis addItemToList" /> --%>
														<dsp:getvalueof var="emailStockAlertsEnabled" param="pSKUDetailVO.emailStockAlertsEnabled" />
														<dsp:getvalueof var="skuinStock" param="inStock" />
													</dsp:oparam>
												</dsp:droplet>
											</dsp:oparam>
										</dsp:droplet>
										<dsp:getvalueof var="prodImage" param="element.productImages.smallImage"/>
                                        <c:set var="prodName"><dsp:valueof param="element.name" valueishtml="true"/></c:set>
										<div class="prodImg fl">
											<c:choose>
											<c:when test="${empty prodImage}">
												<img class="fl productImage" src="${imagePath}/_assets/global/images/no_image_available.jpg" width="146" height="146" alt="${prodName}" />
											</c:when>
											<c:otherwise>
												<img class="fl productImage noImageFound" src="${scene7Path}/${prodImage}" width="146" height="146" alt="${prodName}" />
											</c:otherwise>
											</c:choose>
										</div>
										
										<dsp:getvalueof var="skuinStock" param="inStock" />
										<dsp:getvalueof var="oosProdId" value="${productID}" />
										<c:choose>
										<c:when test="${skuinStock eq null}">
											<c:set var="inStock" value="true" />
										</c:when>
										<c:otherwise>
											<dsp:getvalueof var="inStock" param="inStock" />
										</c:otherwise>
										</c:choose>
										
										<div class="fl grid_4 noMar">
											<ul class="prodInfo">
												<li class="prodName">
													<span class="fl productName"><dsp:valueof param="element.name" valueishtml="true"/></span>
												</li>																							
											</ul>
										</div>
										
									<div class="fr grid_6 prodAttributeContainer noMar">		
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
																									 <span class="attribs ${sep}"><img src="${imageURLTop}" alt="" /><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></span>
																						   </c:when>
																						   <c:otherwise>
																								 <c:choose>
																										 <c:when test="${null ne actionURLTop}">
																											   <span class="attribs ${sep}"><a href="${actionURLTop}" class="newOrPopup"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a></span>
																										 </c:when>
																										 <c:otherwise>
																											   <span class="attribs ${sep}"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></span>
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
										</div>	
									</div>	
									<div class="fr prodInfoContainer grid_10 noMar">	
										<div class="fl grid_4 prodReviewContainer noMar">
											<input type="hidden" class="isInStock" value="${inStock}"/>
										<ul class="prodInfo">
												<c:if test="${BazaarVoiceOn}">
													<dsp:getvalueof var="ratingAvailable" param="element.bvReviews.ratingAvailable"></dsp:getvalueof>
													<c:choose>
													<c:when test="${ratingAvailable == true}">
														<dsp:getvalueof var="fltValue" param="element.bvReviews.averageOverallRating"/>
														<dsp:getvalueof param="element.bvReviews.totalReviewCount" var="totalReviewCount"/>
														<c:choose>
															<c:when test="${totalReviewCount == 1}">
																<li class="prodReview clearfix prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="element.bvReviews.averageOverallRating"/>">
																<dsp:valueof param="element.bvReviews.totalReviewCount"/><bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" />
																</li>
															</c:when>
															<c:otherwise>
																<li class="prodReview clearfix prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="element.bvReviews.averageOverallRating"/>">											
																<dsp:valueof param="element.bvReviews.totalReviewCount"/><bbbl:label key='lbl_reviews_count' language="${pageContext.request.locale.language}" />
																</li>
															</c:otherwise>
														</c:choose>	
													</c:when>
													<c:otherwise>
														<dsp:getvalueof var="fltValue" param="element.bvReviews.averageOverallRating"/>
														<dsp:getvalueof var="totalReviewCount" value="0"></dsp:getvalueof>
														<li class="prodReview clearfix prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="element.bvReviews.averageOverallRating"/>">
														<dsp:a page="${finalUrl}?skuId=${catalogRefId}&categoryId=${CategoryId}&showRatings=true">
														<dsp:valueof param="element.bvReviews.totalReviewCount"/><bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" /></dsp:a>
														</li>
													</c:otherwise>
													</c:choose>
												</c:if>
											</ul>
											<c:if test="${empty isEverLivingProd}">
											<div class="grid_4 noMar">
											<div itemprop="offers" itemscope itemtype="http://schema.org/Offer">
											<div class="prodPrice fl">
											<dsp:getvalueof var="priceRangeDescription" param="element.priceRangeDescription" />
											<dsp:getvalueof var="salePriceRangeDescription" param="element.salePriceRangeDescription" />
											<c:choose>
												<c:when test="${not empty skuid}">
													<dsp:include page="../product_details_price.jsp">	
														<dsp:param name="product" value="${productID}"/>
														<dsp:param name="sku" value="${skuid}"/>
													</dsp:include>
												</c:when>
												<c:otherwise>
														<c:choose>
														<c:when test="${not empty salePriceRangeDescription}">
															<span class="isPrice">
																<span class="highlightRed"><dsp:valueof converter="currency" param="element.salePriceRangeDescription" /></span>
															</span>
															<span class="wasPrice"">
																<span class="was"><bbbl:label key='lbl_was_text' language="${pageContext.request.locale.language}" />
																	<dsp:valueof converter="currency" param="element.priceRangeDescription" /></span>
															</span>
														</c:when>
														<c:otherwise>
															<li class="isPrice">
																 <dsp:valueof converter="currency" param="element.priceRangeDescription" />
															</li>
														</c:otherwise>
													</c:choose>
												</c:otherwise>
											</c:choose>
											</div>
											<span class="prodAvailabilityStatus hidden">
					                             <c:choose>
					                             <c:when test="${inStock==false}">
					                             	<span itemprop="availability" href="http://schema.org/OutOfStock"><bbbl:label key='lbl_pdp_out_of_stock' language="${pageContext.request.locale.language}" /></span>
					                             </c:when>
					                             <c:otherwise>
					                             	<span itemprop="availability" href="http://schema.org/InStock"><bbbl:label key='lbl_pdp_in_stock' language="${pageContext.request.locale.language}" /></span>
					                             </c:otherwise>
					                             </c:choose>
					                        </span>
											</div>
										</div>
										</c:if>
										</div>
									</div>	
										<dsp:droplet name="/atg/dynamo/droplet/ForEach">
											<dsp:param param="element.rollupAttributes" name="array" />
												<dsp:oparam name="output">
												<dsp:getvalueof var="prodType" param="key"/>
												<c:choose>
												
												<c:when test="${prodType eq 'SIZE'}">
													<div class="prodSize fl">
														<label for="psize1" class="fl marRight_10"><bbbl:label key="lbl_sizes_dropdown" language ="${pageContext.request.locale.language}"/></label>
														<div class="psize fl">
															
															<dsp:droplet name="/atg/dynamo/droplet/ForEach">
															<dsp:param param="element" name="array" />
																
																<dsp:oparam name="output">
																	<dsp:getvalueof var="elementValue" param="element.rollupAttribute"/>
																	<li><c:out value="${elementValue}" escapeXml="false"/></li>
																</dsp:oparam>
															</dsp:droplet>
															
														</div>
													</div>
												</c:when>
												<c:otherwise>
													<c:choose>									
													<c:when test="${prodType eq 'FINISH'}">
														<div class="prodFinish fl">
															<label for="pfinish1" class="fl marRight_10"><bbbl:label key="lbl_finish_dropdown" language ="${pageContext.request.locale.language}"/></label>
															<div class="pfinish fl">
																<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																<dsp:param param="element" name="array" />
																	
																	<dsp:oparam name="output">
																		<dsp:getvalueof var="elementValue" param="element.rollupAttribute"/>
																		<li>${elementValue}</li>
																	</dsp:oparam>
																</dsp:droplet>
															</div>
														</div>
													</c:when>
													<c:otherwise>
														<c:if test="${prodType eq 'COLOR'}">
														<div class="prodColor fl">
															<label for="pcolor1" class="fl marRight_10"><bbbl:label key="lbl_colors_dropdown" language ="${pageContext.request.locale.language}"/></label>
															<div class="pcolor fl">
																<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																<dsp:param param="element" name="array" />
																	<dsp:oparam name="output">
																		<dsp:getvalueof var="elementValue" param="element.rollupAttribute"/>
																		<li>${elementValue}</li>
																	</dsp:oparam>
																</dsp:droplet>
															</div>
														</div>
														</c:if>
													</c:otherwise>
													</c:choose>
												</c:otherwise>
												</c:choose>
												</dsp:oparam>
										</dsp:droplet>										
										
										</fieldset>										
									</li>
									</dsp:oparam>
																	
							</dsp:droplet>								
					</ul>
					
					
			</div>
		</c:if>	
</dsp:page>