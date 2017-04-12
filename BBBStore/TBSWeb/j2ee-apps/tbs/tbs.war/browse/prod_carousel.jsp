<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
	<dsp:importbean bean="/atg/repository/seo/CanonicalItemLink"/>
	
	<dsp:getvalueof var="crossSellFlag" param="crossSellFlag"/>	 
	<dsp:getvalueof var="desc" param="desc"/>

	<div class="carousel clearfix">
		<div class="carouselBody grid_12">
			<div
				class="grid_1 carouselArrow omega carouselArrowPrevious clearfix">
				&nbsp; <a class="carouselScrollPrevious" title="Previous" href="#"><bbbl:label key="lbl_blanding_previous" language ="${pageContext.request.locale.language}"/></a>
			</div>
			
			<dsp:getvalueof var="invokeplace" param="invoke" />
			
			<c:choose>
				<c:when test="${invokeplace == 'clearance'}">
					<dsp:getvalueof var="listName" param="clearanceProductsList" />
				</c:when>
				<c:otherwise>
					<dsp:getvalueof var="listName" param="lastviewedProductsList" />								
				</c:otherwise>
			</c:choose>
			
			<dsp:droplet name="IsNull">
				<dsp:param name="value" param="clearanceProductsList" />				
				<dsp:oparam name="false">
					<div class="carouselContent grid_10 clearfix">
						<ul class="prodGridRow">
							<dsp:droplet name="ForEach">
								<dsp:param param="clearanceProductsList" name="array" />
								<dsp:param name="sortProperties" value="+" />
								<dsp:param name="elementName" value="vo"/>
								<dsp:oparam name="output">
									<dsp:getvalueof var="imgSrc" param="vo.productImages.mediumImage" />									
									<li class="grid_2 product">
										<div class="productShadow"></div>
										<div class="productContent">
										<dsp:droplet name="CanonicalItemLink">
											<dsp:param name="id" param="vo.productId" />
											<dsp:param name="itemDescriptorName" value="product" />
											<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
											</dsp:oparam>
										</dsp:droplet>
										<c:set var="productName">
										<dsp:valueof param="vo.name" valueishtml="true"/>
										</c:set>
										<c:choose>
										<c:when test="${(crossSellFlag ne null) && (crossSellFlag eq 'true')}">
			
											<c:set var="onClickEvent">javascript:pdpCrossSellProxy('crossSell', '${desc}')</c:set>
										</c:when>
										<c:otherwise>
									
											<c:set var="onClickEvent" value=""/>
										</c:otherwise>
										</c:choose>
										<dsp:a page="${finalUrl}" iclass="prodImg" title="${productName}" onclick="${onClickEvent}">
											<c:choose>
												<c:when test="${empty imgSrc}">
													<img class="productImage" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${productName}" height="146" width="146"/>
												</c:when>
												<c:otherwise>
													<img class="productImage noImageFound" src="/_assets/global/images/image-preloader.gif" alt="${productName}" data-src-url="${scene7Path}/${imgSrc}" height="146" width="146"/>
												</c:otherwise>
											</c:choose>
											<noscript>
												<c:choose>
													<c:when test="${empty imgSrc}">
														<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="146" alt="${productName}" width="146"/>
													</c:when>
													<c:otherwise>
														<img src="${scene7Path}/${imgSrc}" height="146" width="146" alt="${productName}" class="noImageFound"/>
													</c:otherwise>
												</c:choose>
											</noscript> 
										</dsp:a>
										<ul class="prodInfo">
											<li class="prodName">
												<dsp:a page="${finalUrl}" title="${productName}" onclick="${onClickEvent}">${productName} </dsp:a>
											</li>
											<li class="prodPrice">
												<dsp:getvalueof var="salePriceDescrip" param="vo.salePriceRangeDescription"/>
												<c:choose>
													<c:when test="${not empty salePriceDescrip}">
														<div class="prodPriceOLD">
															<span> <dsp:valueof 														param="vo.priceRangeDescription" /> </span>
														</div>
														<dsp:getvalueof var="salePriceRange" param="vo.salePriceRangeDescription"/>
														<c:choose>
															<c:when test="${fn:contains(salePriceRange,'-')}"><br /></c:when>
															<c:otherwise></c:otherwise>
														</c:choose>
														<div class="prodPriceNEW">
															<dsp:valueof 
																param="vo.salePriceRangeDescription" />
														</div>
													</c:when>
													<c:otherwise>
														<dsp:valueof 
															param="vo.priceRangeDescription" />
													</c:otherwise>
												</c:choose>
											</li>	
												<dsp:include page="attributes.jsp">
													<dsp:param name="attributesList" param="vo.attributesList"/>
												</dsp:include>
												<c:if test="${BazaarVoiceOn}">
												<dsp:getvalueof var="ratingAvailable" param="vo.bvReviews.ratingAvailable"></dsp:getvalueof>
												<c:choose>
												<c:when test="${ratingAvailable == true}">
													<dsp:getvalueof var="fltValue" param="vo.bvReviews.averageOverallRating"/>
													<dsp:getvalueof param="vo.bvReviews.totalReviewCount" var="totalReviewCount"/>
													<c:choose>
														<c:when test="${totalReviewCount == 1}">
															<li class="prodReview clearfix ratingsReviews prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="vo.bvReviews.averageOverallRating"/>">
															(<dsp:valueof param="vo.bvReviews.totalReviewCount"/><bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" />)</li>	
														</c:when>
														<c:otherwise>
															<li class="prodReview clearfix ratingsReviews prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="vo.bvReviews.averageOverallRating"/>">
															(<dsp:valueof param="vo.bvReviews.totalReviewCount"/><bbbl:label key='lbl_reviews_count' language="${pageContext.request.locale.language}" />)</li>
														</c:otherwise>
													</c:choose>
													
												</c:when>
												<c:otherwise>
													<li class="prodReview ratingsReviews writeReview"><dsp:a page="${finalUrl}?writeReview=true" title="Write a Review"><bbbl:label key="lbl_certona_slots_write_reviews" language ="${pageContext.request.locale.language}"/></dsp:a></li>
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
				</dsp:oparam>
			</dsp:droplet>
			
			<div class="grid_1 carouselArrow alpha carouselArrowNext clearfix">
				&nbsp; <a class="carouselScrollNext" title="Next" href="#"><bbbl:label key="lbl_blanding_next" language ="${pageContext.request.locale.language}"/></a>
			</div>
		</div>

		<div class="carouselPages">
			<div class="carouselPageLinks clearfix">
				<a title="Page 1" class="selected" href="#">1</a> <a title="Page 2"
					href="#">2</a> <a title="Page 3" href="#">3</a> <a title="Page 4"
					href="#">4</a>
			</div>
		</div>
	</div>

</dsp:page>
