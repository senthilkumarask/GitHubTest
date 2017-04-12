<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:getvalueof var="crossSellFlag" param="crossSellFlag"/>	 
<dsp:getvalueof var="desc" param="desc"/>
<dsp:getvalueof id="contextroot" idtype="java.lang.String" bean="/OriginatingRequest.contextPath"/>
<c:set var="lblAboutThe"><bbbl:label key="lbl_accessibility_about_the" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="writeReviewLink"><bbbl:label key="lbl_certona_slots_write_reviews" language ="${pageContext.request.locale.language}"/></c:set>
<c:set var="ShipMsgDisplayFlag" scope="request"><bbbc:config key="ShipMsgDisplayFlag" configName="FlagDrivenFunctions"/></c:set>
<c:set var="shippingAttributesList">
	<bbbc:config key="shippingAttributesList" configName="ContentCatalogKeys" />
</c:set>
<dsp:page>
	<div class="carousel clearfix">
		<div class="carouselBody grid_12">
			<div
				class="grid_1 carouselArrow omega carouselArrowPrevious clearfix">
				&nbsp; <a class="carouselScrollPrevious" title="Previous" role="button" href="#"><bbbl:label key="lbl_blanding_previous" language ="${pageContext.request.locale.language}"/></a>
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
			
			<dsp:droplet name="/atg/dynamo/droplet/IsNull">
				<dsp:param name="value" param="clearanceProductsList" />				
				<dsp:oparam name="false">
					<div class="carouselContent grid_10 clearfix">
						<ul class="prodGridRow">
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param param="clearanceProductsList" name="array" />
								<dsp:param name="elementName" value="vo"/>
								<dsp:oparam name="output">
								
									<dsp:getvalueof var="imgSrc" param="vo.productImages.mediumImage" />									
									<li class="grid_2 product">
										<div class="productShadow"></div>
										<div class="productContent">
										<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
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
													<img class="productImage" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="image of ${productName}" height="146" width="146"/>
												</c:when>
												<c:otherwise>
													<img class="productImage noImageFound" src="/_assets/global/images/image-preloader.gif" alt="image of ${productName}" data-src-url="${scene7Path}/${imgSrc}" height="146" width="146"/>
												</c:otherwise>
											</c:choose>
											<noscript>
												<c:choose>
													<c:when test="${empty imgSrc}">
														<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="146" alt="image of ${productName}" width="146"/>
													</c:when>
													<c:otherwise>
														<img src="${scene7Path}/${imgSrc}" height="146" width="146" alt="image of ${productName}" class="noImageFound"/>
													</c:otherwise>
												</c:choose>
											</noscript> 
										</dsp:a>
										<ul class="prodInfo">
											<li class="prodName">
												<dsp:a page="${finalUrl}" title="${productName}" onclick="${onClickEvent}">${productName} </dsp:a>
											</li>
											
											<dsp:getvalueof var="isdynamicPriceProd" param="vo.dynamicPricingProduct" />
											<dsp:getvalueof var="priceLabelCodeProd" param="vo.priceLabelCode" />
											<dsp:getvalueof var="inCartFlagProd" param="vo.inCartFlag" />
											<dsp:getvalueof var="salePriceRangeDescription" param="vo.salePriceRangeDescription" />
											<dsp:getvalueof var="priceRangeDescription" param="vo.priceRangeDescription" />
											<li class="prodPrice">		
											<dsp:include page="browse_price_frag.jsp">
											    <dsp:param name="priceLabelCode" value="${priceLabelCodeProd}" />
												<dsp:param name="inCartFlag" value="${inCartFlagProd}" />
												<dsp:param name="salePrice" value="${salePriceRangeDescription}" />
												<dsp:param name="listPrice" value="${priceRangeDescription}" />
												<dsp:param name="isdynamicPriceEligible" value="${isdynamicPriceProd}" />
											</dsp:include>   		
											</li>	
											<dsp:getvalueof var="isIntlRestricted"
															param="vo.intlRestricted" />
											
												<dsp:include page="attributes.jsp">
													<dsp:param name="attributesList" param="vo.attributesList"/>
												</dsp:include>
								<dsp:param name="attributesList" param="vo.attributesList"/>										
								<c:set var="showShipCustomMsg" value="true"/>
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param param="attributesList" name="array" />
								<dsp:param name="elementName" value="attributeVOList"/>
								<dsp:oparam name="output">

									<dsp:getvalueof var="placeholder" param="key"/>
									<c:if test="${placeholder eq 'CRSL'}">
										<dsp:droplet name="/atg/dynamo/droplet/ForEach">
											<dsp:param param="attributeVOList" name="array" />
											<dsp:param name="elementName" value="attributeVO"/>
											<dsp:param name="sortProperties" value="+priority"/>
											<dsp:oparam name="output">
											<dsp:getvalueof var="attrId" param="attributeVO.attributeName" />
											<c:if test="${fn:contains(shippingAttributesList,attrId)}">
											
												<c:set var="showShipCustomMsg" value="false"/>
											</c:if>
											
											</dsp:oparam>
										</dsp:droplet>
									</c:if>
								</dsp:oparam>
								</dsp:droplet>

												<c:if test="${ShipMsgDisplayFlag && showShipCustomMsg}">
													<li> <dsp:valueof param="vo.displayShipMsg" valueishtml="true" /></li>
												</c:if>
												<c:if test="${BazaarVoiceOn}">
												<dsp:getvalueof var="ratingAvailable" param="vo.bvReviews.ratingAvailable"></dsp:getvalueof>
												<dsp:getvalueof param="vo.bvReviews.ratingsTitle" var="ratingsTitle"/>
												<dsp:getvalueof param="vo.bvReviews.totalReviewCount" var="totalReviewCount"/>
												<c:choose>
												<c:when test="${ratingAvailable == true}">
													<dsp:getvalueof var="fltValue" param="vo.bvReviews.averageOverallRating"/>
													
													<c:choose>
														<c:when test="${totalReviewCount == 1}">
															<li class="clearfix metaFeedback"><span title="${fltValue}" class="ratingTxt prodReview prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel">${ratingsTitle}</span></span><span class="reviewTxt">(${totalReviewCount}<bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" />)</span></li>
														</c:when>
														<c:otherwise>
															<li class="clearfix metaFeedback"><span title="${fltValue}" class="ratingTxt prodReview prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel">${ratingsTitle}</span></span><span class="reviewTxt">(${totalReviewCount}<bbbl:label key='lbl_reviews_count' language="${pageContext.request.locale.language}" />)</span></li>
														</c:otherwise>
													</c:choose>
													
												</c:when>
												<c:otherwise>
													<c:set var="writeReviewLink"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></c:set>
													<li class="metaFeedback"><span class="ratingTxt prodReview"><span class="ariaLabel">${ratingsTitle}</span> </span><span class="writeReview reviewTxt"><a href="${contextroot}${finalUrl}?writeReview=true"  title="${writeReviewLink}" role="link" aria-label="${writeReviewLink} ${lblAboutThe} ${productName}"><bbbl:label key='lbl_grid_write_review_link' language="${pageContext.request.locale.language}" /></a></span></li>
												</c:otherwise>
												</c:choose>
												</c:if>
												
												
											
											 <c:if test="${isInternationalCustomer && isIntlRestricted}">
                	                          <div class="notAvailableIntShipMsg cb clearfix marBottom_10">
                		                            <bbbl:label key="lbl_plp_intl_restrict_list_msg" language="${pageContext.request.locale.language}" />
                	                          </div>
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
				&nbsp; <a class="carouselScrollNext" title="Next" role="button" href="#"><bbbl:label key="lbl_blanding_next" language ="${pageContext.request.locale.language}"/></a>
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
