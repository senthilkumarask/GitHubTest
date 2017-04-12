<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />

<dsp:page>
		<dsp:getvalueof var="key" param="key"/>	 
		<%--Updated for Omniture Story --|@psin52 --%>
		<dsp:getvalueof var="desc" param="desc"/>
		<c:set var="lblAboutThe"><bbbl:label key="lbl_accessibility_about_the" language="${pageContext.request.locale.language}" /></c:set>
		<c:set var="ShipMsgDisplayFlag" scope="request"><bbbc:config key="ShipMsgDisplayFlag" configName="FlagDrivenFunctions"/></c:set>
		<c:set var="shippingAttributesList">
			<bbbc:config key="shippingAttributesList" configName="ContentCatalogKeys" />
		</c:set>
		<c:set var="disableLazyLoadS7Images">
			<bbbc:config key="disableLazyLoadS7ImagesFlag" configName="FlagDrivenFunctions" />
		</c:set>

		<c:set var="prodImageAttrib">class="productImage noImageFound" src</c:set>
		<c:set var="itemCount" value="1" />

		<div class="carousel clearfix">
				<div class="carouselBody grid_12">
					<div class="grid_1 carouselArrow omega carouselArrowPrevious clearfix">
						&nbsp;
						<a class="carouselScrollPrevious" title="Previous" role="button" href="#"><bbbl:label key="lbl_blanding_previous" language ="${pageContext.request.locale.language}"/></a>
					</div>
					
					<div class="carouselContent grid_10 clearfix">
						<ul class="prodGridRow">
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param param="lastviewedProductsList" name="array" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="imgSrc" param="element.productImages.mediumImage" />
									<dsp:getvalueof var="isIntlRestricted" param="element.intlRestricted"/>
									<dsp:getvalueof var="prodId" param="element.productId"/>
									<dsp:getvalueof var="productId" param="productId" />

									<c:if test="${(not empty disableLazyLoadS7Images and not disableLazyLoadS7Images) || itemCount > 5}">
										<c:set var="prodImageAttrib">class="productImage lazyLoad loadingGIF" src="${imagePath}/_assets/global/images/blank.gif" data-lazyloadsrc</c:set>
									</c:if>

									<c:if test = "${prodId != productId}">
									  <li class="grid_2 product">
										<div class="productShadow"></div>
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
														<img ${prodImageAttrib}="${scene7Path}/${imgSrc}" height="146" width="146" alt="image of ${productName}" <c:if test="${itemCount > 5}"> style="display: none;" </c:if> />
													</c:otherwise>
												</c:choose>
											</dsp:a>
											<ul class="prodInfo">
												<li class="prodName"><dsp:a page="${finalUrl}" onclick="javascript:pdpCrossSellProxy('crossSell','${desc}')" title="${productName}">${productName}</dsp:a></li>
												<li class="prodPrice">
											 
											 <dsp:getvalueof var="salePriceRangeDescription" param="element.salePriceRangeDescription"/>
											 <dsp:getvalueof var="prodListPrice" param="element.priceRangeDescription"/>
											<c:set var= "priceIsTBD"><bbbl:label key='lbl_price_is_tbd' language="${pageContext.request.locale.language}" /></c:set>
											<dsp:getvalueof var="isdynamicPriceProd" param="element.dynamicPricingProduct" />
											<dsp:getvalueof var="priceLabelCodeProd" param="element.priceLabelCode" />
											<dsp:getvalueof var="inCartFlagProd" param="element.inCartFlag" />
											<dsp:include page="browse_price_frag.jsp">
												    <dsp:param name="priceLabelCode" value="${priceLabelCodeProd}" />
													<dsp:param name="inCartFlag" value="${inCartFlagProd}" />
													<dsp:param name="salePrice" value="${salePriceRangeDescription}" />
													<dsp:param name="listPrice" value="${prodListPrice}" />
													<dsp:param name="isdynamicPriceEligible" value="${isdynamicPriceProd}" />
											</dsp:include>   		
											
												</li>
												<%--  BBB AJAX 2.3.1  Was-Is price change on PLP,Search,Brand ends--%>
												<c:set var="showShipCustomMsg" value="true"/>
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
																<dsp:getvalueof var="attrId" param="attributeVO.attributeName" />
																<c:if test="${fn:contains(shippingAttributesList,attrId)}">
																		<c:set var="showShipCustomMsg" value="false"/>
																</c:if>
																<li class="productAttributes prodAttribWrapper">
																	<dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/>
																</li>
																</dsp:oparam>
															</dsp:droplet>
														</c:if>
													</dsp:oparam>
												</dsp:droplet>
												<c:if test="${ShipMsgDisplayFlag && showShipCustomMsg}">
																	<li><dsp:valueof param="element.displayShipMsg" valueishtml="true" /></li>
																</c:if>
												<c:if test="${BazaarVoiceOn}">
												<dsp:getvalueof var="ratingAvailable" param="element.bvReviews.ratingAvailable"></dsp:getvalueof>
												<dsp:getvalueof param="element.bvReviews.averageOverallRating" var="averageOverallRating"/>
												<dsp:getvalueof param="element.bvReviews.totalReviewCount" var="totalReviewCount"/>
												<dsp:getvalueof param="element.bvReviews.ratingsTitle" var="ratingsTitle"/>
												<c:choose>
												<c:when test="${ratingAvailable == true}">
													<dsp:getvalueof var="fltValue" param="element.bvReviews.averageOverallRating"/>
													<dsp:getvalueof param="element.bvReviews.totalReviewCount" var="totalReviewCount"/>
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
													<li class="metaFeedback"><span class="ratingTxt prodReview"><span class="ariaLabel">${ratingsTitle}</span> </span><span class="writeReview reviewTxt"><a href="${pageContext.request.contextPath}${finalUrl}?writeReview=true"  title="${writeReviewLink}" role="link" aria-label="${writeReviewLink} ${lblAboutThe} ${productName}"><bbbl:label key='lbl_grid_write_review_link' language="${pageContext.request.locale.language}" /></a></span></li>
												</c:otherwise>
												</c:choose>
												</c:if>
												<c:if test="${isInternationalCustomer && isIntlRestricted}">
	                								<div class="notAvailableIntShipMsg marBottom_5">
	                									<bbbl:label key="lbl_plp_intl_restrict_list_msg" language="${pageContext.request.locale.language}" />
	                								</div>
                							</c:if>
											</ul>
										</div>
									</li>
										<c:set var="itemCount" value="${itemCount + 1}" />
									</c:if>
								</dsp:oparam>
							</dsp:droplet>
						</ul>
					</div>
					
					<div class="grid_1 carouselArrow alpha carouselArrowNext clearfix">
						&nbsp;
						<a class="carouselScrollNext" title="Next" role="button" href="#"><bbbl:label key="lbl_blanding_next" language ="${pageContext.request.locale.language}"/></a>
					</div>
				</div>

				<div class="carouselPages">
					<div class="carouselPageLinks clearfix">
						<a title="Page 1" class="selected" href="#">1</a>
						<a title="Page 2" href="#">2</a>
						<a title="Page 3" href="#">3</a>
						<a title="Page 4" href="#">4</a>
					</div>
				</div>
			</div>
		<%-- <script type="text/javascript">       
                   
		 function pdpCrossSellProxy(event, desc) {
			   if(event == 'crossSell') {
			       if (typeof s_crossSell === 'function') { s_crossSell(desc); }
			   } else if(event == 'pfm'){
			       if (typeof s_crossSell === 'function') { s_crossSell(desc); }
			   }
			}
           
        </script> --%>
</dsp:page>
