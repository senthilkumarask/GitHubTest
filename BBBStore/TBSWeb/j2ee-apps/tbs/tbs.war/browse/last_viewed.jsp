<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />

<dsp:page>
		<dsp:getvalueof var="key" param="key"/>	 
		<!--Updated for Omniture Story --|@psin52 -->
		<dsp:getvalueof var="desc" param="desc"/>

		<div class="carousel clearfix">
				<div class="carouselBody grid_12">
					
					<div class="carouselContent grid_10 clearfix">
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param param="lastviewedProductsList" name="array" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="imgSrc" param="element.productImages.mediumImage" />
									<dsp:getvalueof var="salePriceRangeDescription" param="element.salePriceRangeDescription" />
									
										<div class="productContent">
										<dsp:getvalueof var="prodId" param="element.productId"/>
										<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
											<dsp:param name="id" value="${prodId}" />
											<dsp:param name="itemDescriptorName" value="product" />
											<dsp:param name="repositoryName"
												value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
													param="url" />								
											</dsp:oparam>
										</dsp:droplet>
										<c:set var="productName">
											<dsp:valueof param="element.name" valueishtml="true"/>
										</c:set>
										
											<dsp:a iclass="prodImg" page="${finalUrl}" onclick="javascript:pdpCrossSellProxy('crossSell','${desc}')" title="${productName}"> 
											<c:choose>
											<c:when test="${empty imgSrc}">
												<dsp:img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="146" width="146" alt="${productName}" />
											</c:when>
											<c:otherwise>
												<dsp:img src="${scene7Path}/${imgSrc}" height="146" width="146" iclass="noImageFound" alt="image of ${productName}" />
											</c:otherwise>
											</c:choose>
											</dsp:a>
											<ul class="prodInfo">
												<li class="prodName"><dsp:a page="${finalUrl}" onclick="javascript:pdpCrossSellProxy('crossSell','${desc}')" title="${productName}">${productName}</dsp:a></li>
												<li class="prodPrice">
												
													<c:choose>
														<c:when test="${not empty salePriceRangeDescription}">
															<div class="prodPriceNEW">
																<dsp:valueof converter="currency" param="element.salePriceRangeDescription" />
															</div>
															<div class="prodPriceOLD">
																<span> <dsp:valueof converter="currency" param="element.priceRangeDescription" /> </span>
															</div>
														</c:when>
														<c:otherwise>
															<dsp:valueof converter="currency" param="element.priceRangeDescription" />
														</c:otherwise>
													</c:choose>
												</li>
												
												<dsp:droplet name="/atg/dynamo/droplet/ForEach">
													<dsp:param param="element.attributesList" name="array" />
													<dsp:param name="elementName" value="attributeVOList"/>
													<dsp:oparam name="output">
														<dsp:getvalueof var="placeholder" param="key"/>
														<c:if test="${placeholder eq 'CRSL'}">
															<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																<dsp:param param="element" name="array" />
																<dsp:param param="attributeVOList" name="array" />
																<dsp:param name="sortProperties" value="+priority"/>
																<dsp:param name="elementName" value="attributeVO"/>
																<dsp:oparam name="output">
																<li class="productAttributes prodAttribWrapper prodAttribute">
																	<dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/>
																</li>
																</dsp:oparam>
															</dsp:droplet>
														</c:if>
													</dsp:oparam>
												</dsp:droplet>
												<c:if test="${BazaarVoiceOn}">
												<dsp:getvalueof var="ratingAvailable" param="element.bvReviews.ratingAvailable"></dsp:getvalueof>
												<c:choose>
												<c:when test="${ratingAvailable == true}">
													<dsp:getvalueof var="fltValue" param="element.bvReviews.averageOverallRating"/>
													<dsp:getvalueof param="element.bvReviews.totalReviewCount" var="totalReviewCount"/>
													<c:choose>
														<c:when test="${totalReviewCount == 1}">
															<li class="prodReview clearfix ratingsReviews prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="element.bvReviews.averageOverallRating"/>">
															(<dsp:valueof param="element.bvReviews.totalReviewCount"/><bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" />)</li>
														</c:when>
														<c:otherwise>
															<li class="prodReview clearfix ratingsReviews prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="element.bvReviews.averageOverallRating"/>">
															(<dsp:valueof param="element.bvReviews.totalReviewCount"/><bbbl:label key='lbl_reviews_count' language="${pageContext.request.locale.language}" />)</li>
														</c:otherwise>
													</c:choose>	
												</c:when>
												<c:otherwise>
													<c:set var="writeReviewLink"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></c:set>
													<li class="prodReview ratingsReviews writeReview"><dsp:a page="${finalUrl}?writeReview=true" onclick="javascript:pdpCrossSellProxy('crossSell','${desc}')" title="${writeReviewLink}"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></dsp:a></li>
												</c:otherwise>
												</c:choose>
												</c:if>
											</ul>
										</div>
								</dsp:oparam>
							</dsp:droplet>
					</div>
				</div>
			</div>
</dsp:page>
