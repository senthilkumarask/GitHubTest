<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:getvalueof var="collectionFlag"
		param="BBBProductListVO.collectionFlag" />
	<dsp:importbean
		bean="/com/bbb/commerce/browse/droplet/MinimalProductDetailDroplet" />

	<dsp:getvalueof var="strategyTitle" param="strategyTitle" />
	<dsp:getvalueof var="strategyContextTitle" param="strategyContextTitle" />
	<dsp:getvalueof var="strategyContextValue" param="strategyContextValue" />
	<dsp:getvalueof var="strategyContextCode" param="strategyContextCode" />
	<dsp:getvalueof var="strategyType" param="strategyType" />
	<dsp:getvalueof var="strategyId" param="strategyId" />
	<dsp:getvalueof var="BazaarVoiceOn" param="BazaarVoiceOn" />
	<dsp:getvalueof var="strategyType" param="strategyType"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<c:set var="lblAboutThe"><bbbl:label key="lbl_accessibility_about_the" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblProductQuickView">
		<bbbl:label key="lbl_product_quick_view"
			language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="lblCompareProduct">
		<bbbl:label key="lbl_compare_product"
			language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="lblReviewsCount">
		<bbbl:label key="lbl_reviews_count"
			language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="lblReviewCount">
		<bbbl:label key="lbl_review_count"
			language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="lblGridWriteReviewLink">
		<bbbl:label key="lbl_grid_write_review_link"
			language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="lblWasText">
		<bbbl:label key="lbl_was_text"
			language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="ShipMsgDisplayFlag" scope="request"><bbbc:config key="ShipMsgDisplayFlag" configName="FlagDrivenFunctions"/></c:set>
	<c:set var="shippingAttributesList">
		<bbbc:config key="shippingAttributesList" configName="ContentCatalogKeys" />
	</c:set>

	<c:choose>
		<c:when test="${strategyType == 'Recommendation'}">
			<dsp:getvalueof var="strategyContextValue"
				param="strategyContextValue" />
			<%-- Droplet to get the product details of the context product --%>
			<dsp:droplet name="MinimalProductDetailDroplet">
				<dsp:param name="id" param="strategyContextCode" />
				<dsp:oparam name="output">
					<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
						<dsp:param name="id" param="strategyContextCode" />
						<dsp:param name="itemDescriptorName" value="product" />
						<dsp:param name="repositoryName"
							value="/atg/commerce/catalog/ProductCatalog" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="finalUrlContextParam"
								vartype="java.lang.String" param="url" />
						</dsp:oparam>
					</dsp:droplet>
					<dsp:getvalueof var="strategyContextValue" param="productVo.name" />
					<dsp:getvalueof var="collectionThumbnailImage"
						param="productVo.productImages.collectionThumbnailImage" />
				</dsp:oparam>
			</dsp:droplet>

			<dsp:getvalueof var="strategyContextTitle"
				param="strategyContextTitle" />

			<%-- Replace the placeholder of context title with the product details --%>
			<c:set var="strategyContextfinal"
				value='${strategyContextValue}<br></b></div><div class="storeProdName recommendation">'></c:set>
			<c:set var="strategyContext"
				value="${fn:replace(strategyContextTitle,'$context', strategyContextfinal)}" />

			<%-- Display the context details --%>
			<div class="product viewAllSectionHeading grid_12">
				<div class="contextualImage fl">
					<div>
						<dsp:a href="${contextPath}${finalUrlContextParam}">
							<img width="63" height="63" src="${scene7Path}/${collectionThumbnailImage}"
								class="productImage" title="${strategyContextValue}"
								alt="${strategyContextValue}" />
						</dsp:a>
					</div>
					<div class="clearfix fl noBorder prodInfo">
						<div class="noMar storeProdName">${strategyContext}</div>
						<div class="storeProdName recommendation"></div>
					</div>
				</div>
			</div>
		</c:when>
	</c:choose>

	<c:choose>
		<c:when test="${collectionFlag eq 1}">
			<c:set var="quickViewClass" value="showOptionsCollection" />
		</c:when>
		<c:otherwise>
			<c:set var="quickViewClass" value="showOptionMultiSku" />
		</c:otherwise>
	</c:choose>

	<div class="product grid_12 marTop_10 marBottom_20 fourAcross">
		<div class="product grid_12 alpha omega marBottom_10">
			<%-- Iterate over the product list to display the products --%>
			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:param name="array" param="productsVOsList" />
				<dsp:param name="elementName" value="productVO" />
				<dsp:oparam name="output">

					<dsp:getvalueof var="productId" param="productVO.productId" />

					<dsp:getvalueof var="count" param="count" />
					<dsp:getvalueof var="size" param="size" />

					<c:choose>
						<c:when test="${count%4 == 1}">
							<div class="product homeProd grid_3 alpha marBottom_10">
						</c:when>
						<c:when test="${count%4 == 2 || count%4 == 3}">
							<div class="product homeProd grid_3 marBottom_10">
						</c:when>
						<c:otherwise>
							<div class="product homeProd grid_3 omega marBottom_10">
						</c:otherwise>
					</c:choose>

					<div class="productContent">

						<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
							<dsp:param name="id" param="productVO.productId" />
							<dsp:param name="itemDescriptorName" value="product" />
							<dsp:param name="repositoryName"
								value="/atg/commerce/catalog/ProductCatalog" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
									param="url" />
							</dsp:oparam>
						</dsp:droplet>

						<dsp:a href="${contextPath}${finalUrl}">
							<dsp:getvalueof var="imgSrc"
								param="productVO.productImages.regularImage" />
							<dsp:getvalueof var="imageAltText" param="productVO.name" />

							<c:choose>
								<c:when test="${empty imgSrc}">
									<img width="226" height="226"
										src="${imagePath}/_assets/global/images/no_image_available.jpg"
										alt="${imageAltText}" title="${imageAltText}" />
								</c:when>
								<c:otherwise>
									<img width="226" height="226" src="${scene7Path}/${imgSrc}"
										alt="${imageAltText}" class="noImageFound"
										title="${imageAltText}" />
								</c:otherwise>
							</c:choose>

						</dsp:a>
						<div class="prodData listDataItemsWrap">
							<div class="grid_3 alpha padBottom_10 fl quickViewAndCompare">
								<span
									class="quickView ${quickViewClass} fl marRight_30 padRight_5">${lblProductQuickView}</span>
								<div class="checkboxItem input clearfix noPad fl noBorder">
									<div class="checkbox noMar">
										<input name="Compare" type="checkbox"
											data-productid="${productId}" value="compareItem"
											class="compareChkTxt" id="compareChkTxt_${productId}"
											name="Compare" style="opacity: 0;">
									</div>
									<div class="label">
										<label class="compareChkTxt" for="compareChkTxt_${productId}">${lblCompareProduct}</label>
									</div>
								</div>
								<input type="hidden"
									class="addItemToList addItemToRegis itemQuantity" value="1"
									name="qty" data-change-store-submit="qty" /> <input
									type="hidden" data-change-store-internaldata="true"
									data-change-store-errors="required"
									data-change-store-submit="prodId" value="${productId}"
									class="_prodId addItemToRegis productId addItemToList"
									name="prodId" /> <input type="hidden"
									data-change-store-internaldata="true"
									data-change-store-errors="required"
									data-change-store-submit="skuId"
									class="addItemToRegis _skuId addItemToList changeStoreSkuId"
									value="${skuID}" name="skuId" /> <input type="hidden"
									name="price" value="${omniPrice}"
									class="addItemToList addItemToRegis" /> <input type="hidden"
									data-change-store-storeid="storeId" value="" name="storeId"
									class="addToCartSubmitData" /> <input type="hidden"
									name="parentProductId" value="${productId}"
									class="addItemToList addItemToRegis" /> <input type="hidden"
									value="" class="addToCartSubmitData" name="bts"
									data-change-store-storeid="bts" /> <input type="hidden"
									value="true" class="addItemToList addItemToRegis"
									name="fromSearch" /> <input type="hidden" value="true"
									name="lcFlag" /> <input type="hidden" value="${CategoryId}"
									class="categoryId" /> <input type="hidden" value=""
									class="selectedRollUpValue" /> <input type="hidden"
									value="false" class="isMultiRollUpFlag" />
							</div>
							<div class="cb"></div>
							<ul class="prodInfo">
								<dsp:getvalueof param="productVO.name" var="prodName" />
								<li class="prodName"><dsp:a page="${finalUrl}"
										title="${prodName}">
										<c:out value="${prodName}" escapeXml="false" />
									</dsp:a>
								</li>
										<c:set var="showShipCustomMsg" value="true"/>
										<dsp:droplet name="/atg/dynamo/droplet/ForEach">
											<dsp:param param="productVO.attributesList" name="array" />
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
													 	<li class="prodAttribute">
															<dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/>
														</li>
														</dsp:oparam>
													</dsp:droplet>
												</c:if>
											</dsp:oparam>
										</dsp:droplet>     
										
					 
								<c:if test="${BazaarVoiceOn}">
									<dsp:getvalueof var="ratingAvailable" param="productVO.bvReviews.ratingAvailable"></dsp:getvalueof>
                                    <dsp:getvalueof param="productVO.bvReviews.ratingsTitle" var="ratingsTitle" />
									<c:choose>
										<c:when test="${ratingAvailable == true}">
											<dsp:getvalueof var="fltValue"
												param="productVO.bvReviews.averageOverallRating" />
											<dsp:getvalueof param="productVO.bvReviews.averageOverallRating" var="averageOverallRating" />
											<dsp:getvalueof param="productVO.bvReviews.totalReviewCount"
												var="totalReviewCount" />
											<c:choose>
												<c:when test="${totalReviewCount == 1}">
													<li class="prodReviews clearfix metaFeedback">
                                                    <span class="ratingTxt ratingsReviews prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel">${ratingsTitle}</span></span><span class="reviewTxt">(<c:out value="${totalReviewCount}" />${lblReviewCount})</span></li>
												</c:when>
												<c:otherwise>
													<li class="prodReviews clearfix metaFeedback">
                                                    	<span class="ratingTxt ratingsReviews prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel">${ratingsTitle}</span></span><span class="reviewTxt">(<c:out value="${totalReviewCount}" />${lblReviewsCount})</span></li>
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<li class="prodReviews metaFeedback">
												<span class="ratingTxt ratingsReviews"><span class="ariaLabel">${ratingsTitle}<span/></span><span class="prodReviewSpanFont reviewTxt writeReview"> <c:set var="writeReviewLink">${lblGridWriteReviewLink}</c:set> <a href="${contextPath}${finalUrl}?writeReview=true" title="${writeReviewLink}" role="link" aria-label="${lblGridWriteReviewLink} ${lblAboutThe} ${prodName}">${lblGridWriteReviewLink}</a> </span></li>
										</c:otherwise>
									</c:choose>
								</c:if>
								<li class="prodPrice">
								<dsp:getvalueof var="salePriceRangeDescription" param="productVO.salePriceRangeDescription" />
								<dsp:getvalueof var="isdynamicPriceProd" param="productVO.dynamicPricingProduct" />
								<dsp:getvalueof var="priceLabelCodeProd" param="productVO.priceLabelCode" />
								<dsp:getvalueof var="inCartFlagProd" param="productVO.inCartFlag" />
								<dsp:getvalueof var="priceRangeDescription" param="productVO.priceRangeDescription" />
								<dsp:include page="/browse/browse_price_frag.jsp">
								    <dsp:param name="priceLabelCode" value="${priceLabelCodeProd}" />
									<dsp:param name="inCartFlag" value="${inCartFlagProd}" />
									<dsp:param name="salePrice" value="${salePriceRangeDescription}" />
									<dsp:param name="listPrice" value="${priceRangeDescription}" />
									<dsp:param name="isdynamicPriceEligible" value="${isdynamicPriceProd}" />
								</dsp:include>     		
								 <c:if test="${ShipMsgDisplayFlag && showShipCustomMsg}">
									<li><dsp:valueof param="productVO.displayShipMsg" valueishtml="true" /></li>
								 </c:if>
								</li>
							</ul>
						</div>
					</div>
		</div>
		<c:if test="${count%4 == 0}">
			<div class="clear"></div>
		</c:if>
		</dsp:oparam>
		</dsp:droplet>
	</div>
	<div class="clear"></div>
	</div>
</dsp:page>