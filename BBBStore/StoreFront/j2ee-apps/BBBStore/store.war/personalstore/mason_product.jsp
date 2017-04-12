<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/MinimalProductDetailDroplet"/>
<dsp:getvalueof var="collectionFlag" param="BBBProductListVO.collectionFlag"/>
	
	<dsp:getvalueof var="strategyType" param="strategyType"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="BazaarVoiceOn" param="BazaarVoiceOn" />
 	<c:set var="scene7Path" scope="request"><bbbc:config key="scene7_url" configName="ThirdPartyURLs"/></c:set>
 	<c:set var="lblOfThe"><bbbl:label key="lbl_accessibility_of_the" language="${pageContext.request.locale.language}" /></c:set>
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
	<c:set var="lblViewAll">
		<bbbl:label key="lbl_view_all"
			language="${pageContext.request.locale.language}" />
	</c:set>
	<dsp:getvalueof var="strategyType" param="strategyType" />
	<dsp:getvalueof var="strategyId" param="strategyId" />
	<dsp:getvalueof var="strategyContextCode" param="strategyContextValue" />
	<dsp:getvalueof var="strategyNameFromMap" param="strategyNameFromMap" />
	<c:set var="viewAll" scope="request">
		<bbbc:config key="psViewAllOn" configName="ContentCatalogKeys" />
	</c:set> 
	
	<c:choose>
		<c:when test="${strategyType == 'Recommendation'}">
			<dsp:getvalueof var="strategyContextValue"
				param="strategyContextValue" />
			<%-- Droplet to get the product details of the context product --%>
			<dsp:droplet name="MinimalProductDetailDroplet">
				<dsp:param name="id" param="strategyContextValue" />
				<dsp:oparam name="output">
					<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
						<dsp:param name="id" param="strategyContextValue" />
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
			<c:set var="strategyContext"
				value="${fn:replace(strategyContextTitle,'$context', strategyContextValue)}" />

		 	<%-- Display the context details --%>
	  		<div class="product psSectionHeadingWithCImage grid_12">
				<div class="contextualImage fl">
					<div>
						<dsp:a href="${contextPath}${finalUrlContextParam}">
							<img width="52" height="52" src="${scene7Path}/${collectionThumbnailImage}"
								class="productImage" title="${strategyContextValue}"
								alt="${strategyContextValue}" />
						</dsp:a>
					</div>
					<div class="clearfix fl noBorder prodInfo">
						${strategyContext}
					</div>
				</div>
				<c:if test="${viewAll eq 'TRUE'}">
				<span class="viewAll grid_3 padTop_20 fr"> <%-- TO-DO create label in View All Story--%>
					<a class="allResults capitalize" href="/store/personalstore/view_products_link.jsp?strategyId=${strategyId}&strategyContextCode=${strategyContextCode}&strategyContextValue=${strategyContextValue}"
					 class="allResults"><img src="/_assets/global/images/viewAllWithText.png" alt="View All"></a> </span>  
				</c:if>

			</div>
		</c:when>
		<c:otherwise>
			<div class="psSectionHeading capitalize grid_12">
				<span class="headingText"><dsp:valueof param="strategyTitle"></dsp:valueof>
				</span> 
				<c:if test="${viewAll eq 'TRUE'}">
				<span class="viewAll grid_3 padTop_5 fr"> <%-- TO-DO create label in View All Story--%>
					<a class="allResults capitalize" href="/store/personalstore/view_products_link.jsp?strategyId=${strategyId}&strategyContextCode=${strategyContextCode}&strategyContextValue=${strategyContextValue}"
					 class="allResults"><img src="/_assets/global/images/viewAllWithText.png" alt="View All"></a> 
				</span>
				</c:if>
			</div>
		</c:otherwise>
	</c:choose>
	  
	<c:choose>
	<c:when test="${collectionFlag eq 1}">   
		<c:set var="quickViewClass" value="showOptionsCollection"/>
	</c:when>
	<c:otherwise>
		<c:set var="quickViewClass" value="showOptionMultiSku"/>
	</c:otherwise>
	</c:choose>
    
		<div class="product grid_12 marBottom_20 personalStoreWithCImage">
			
		<div class="grid_12 alpha omega marBottom_20 padTop_5">
			<%-- Iterate over the product list to display the products --%>
			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
			<dsp:param name="array" param="productsVOsList" />
			<dsp:param name="elementName" value="productVO"/>
			<dsp:oparam name="output">
			 <dsp:getvalueof var="productId" param="productVO.productId" />
			 <dsp:getvalueof var="count" param="count" />
        	 <dsp:getvalueof var="size" param="size" />
        	 
       		 <c:if test="${count eq 1}">
       		 	<%-- Display the large image in the layout --%>
				<div class="grid_6 alpha largeImage">
				
				<div class="productContent">
						<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
								<dsp:param name="id" param="productVO.productId" />
								<dsp:param name="itemDescriptorName" value="product" />
								<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
								</dsp:oparam>
						</dsp:droplet>
							
							
						<dsp:a href="${contextPath}${finalUrl}?strategyName=${strategyNameFromMap}" onclick="javascript:personalStore('${strategyNameFromMap}')">
								<dsp:getvalueof var="imgSrc" param="productVO.productImages.largeImage" />
								<dsp:getvalueof var="imageAltText" param="productVO.name" />
							<c:choose>
								<c:when test="${empty imgSrc}">
									<img width="470" height="470" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${imageAltText}" height="470" width="470" title="${imageAltText}"/>
								</c:when>
								<c:otherwise>
									<img width="470" height="470" src="${scene7Path}/${imgSrc}" alt="${imageAltText}" height="470" width="470" class="noImageFound" title="${imageAltText}"/>
								</c:otherwise>
							</c:choose>
						
						</dsp:a>
						
						<div class="prodData listDataItemsWrap">
							<div class="grid_5 alpha padBottom_10 fl quickViewAndCompare"> 
								<!-- <span class="quickView showOptionMultiSku fl marRight_30 padRight_5">Quick View</span> -->
								<span class="quickView ${quickViewClass} fl marRight_30 padRight_5" role="link" aria-label="${lblProductQuickView} ${lblOfThe} ${prodName}">${lblProductQuickView}</span>
								
								<div class="checkboxItem input clearfix noPad fl noBorder"> 
									<div class="checkbox noMar">
										<input name="Compare" type="checkbox"	data-productid="${productId}" value="compareItem" class="compareChkTxt"
															name="Compare" style="opacity: 0;"> 
									</div>
									<div class="label"> 
										<label class="compareChkTxt" for="compareChkTxt_${productId}">${lblCompareProduct}</label> 
									</div> 
								</div> 

							<input type="hidden" class="addItemToList addItemToRegis itemQuantity" value="1" name="qty" data-change-store-submit="qty" />
							<input type="hidden" data-change-store-internaldata="true" data-change-store-errors="required" data-change-store-submit="prodId" value="${productId}" class="_prodId addItemToRegis productId addItemToList" name="prodId"/>
							<input type="hidden" data-change-store-internaldata="true" data-change-store-errors="required" data-change-store-submit="skuId" class="addItemToRegis _skuId addItemToList changeStoreSkuId" value="${skuID}" name="skuId"/>
							<input type="hidden" name="price" value="${omniPrice}" class="addItemToList addItemToRegis" />
							<input type="hidden" data-change-store-storeid="storeId" value="" name="storeId" class="addToCartSubmitData"/>
							<input type="hidden" name="parentProductId" value="${productId}" class="addItemToList addItemToRegis" />
							<input type="hidden" value="" class="addToCartSubmitData" name="bts" data-change-store-storeid="bts"/>
							<input type="hidden" value="true" class="addItemToList addItemToRegis" name="fromSearch"/>
							<input type="hidden" value="true" name="lcFlag"/>
							<input type="hidden" value="${CategoryId}" class="categoryId"/>
							<input type="hidden" value="" class="selectedRollUpValue"/>
							<input type="hidden" value="false" class="isMultiRollUpFlag"/>
							<input type="hidden" data-change-store-storeid="strategyName" value="${strategyNameFromMap}" name="strategyName"  />
							</div>
							<div class="cb"></div>
							<ul class="prodInfo">
							<dsp:getvalueof param="productVO.name" var="prodName"/>
								<li class="prodName"> 
									<dsp:a page="${finalUrl}?strategyName=${strategyNameFromMap}" title="${prodName}" onclick="javascript:personalStore('${strategyNameFromMap}')"><c:out value="${prodName}" escapeXml="false"/> </dsp:a>
								</li>
										 
						 	
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
										<dsp:getvalueof var="count" param="count" />
										<c:if test="${count == 1}">
										<li class="prodAttribute">
											<dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/> 
										</li>
										</c:if>
										</dsp:oparam>
									</dsp:droplet>
								</c:if>
							</dsp:oparam>
							</dsp:droplet>
						 	
							<c:if test="${BazaarVoiceOn}">
							<dsp:getvalueof var="ratingAvailable" param="productVO.bvReviews.ratingAvailable"></dsp:getvalueof>
							<dsp:getvalueof var="ratingsTitle" param="productVO.bvReviews.ratingsTitle"></dsp:getvalueof>
							<c:choose>
							<c:when test="${ratingAvailable == true}">
								<dsp:getvalueof var="fltValue" param="productVO.bvReviews.averageOverallRating"/>
								<dsp:getvalueof param="productVO.bvReviews.averageOverallRating" var="averageOverallRating"/>
								<dsp:getvalueof param="productVO.bvReviews.totalReviewCount" var="totalReviewCount"/>
								<c:choose>
									<c:when test="${totalReviewCount == 1}">
									  <li class="prodReviews metaFeedback clearfix"><span class="ratingTxt ratingsReviews prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel">${ratingsTitle}</span></span><span class="reviewTxt">(<c:out value="${totalReviewCount}"/>
									  ${lblReviewCount})</span></li>
									</c:when>
									<c:otherwise>
										<li class="clearfix metaFeedback"><span class="ratingTxt ratingsReviews prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel">${ratingsTitle}</span></span><span class="reviewTxt">(<c:out value="${totalReviewCount}"/>
										${lblReviewsCount})</span></li>
									</c:otherwise>
								</c:choose>	
							</c:when>
							<c:otherwise>
								<li class="prodReviews metaFeedback"> 
								<span class="ratingTxt ratingsReviews"><span class="ariaLabel">${ratingsTitle}</span></span><span class="prodReviewSpanFont reviewTxt writeReview"> 
								<c:set var="writeReviewLink">${lblGridWriteReviewLink}</c:set>
								<a href="${contextPath}${finalUrl}?writeReview=true" title="${writeReviewLink}" role="link" aria-label="${lblGridWriteReviewLink} ${lblAboutThe} ${prodName}">${lblGridWriteReviewLink}</a>
								</span> 
								</li>
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
					 	</li>
						</ul>
						</div>
						
					</div>
					</div>
					</c:if>
			
				</dsp:oparam>
				</dsp:droplet>
				
				<%-- Display the smaller images in the layout --%>
				<div class="grid_6 omega smallImages">
				
				<dsp:droplet name="/atg/dynamo/droplet/ForEach">
					<dsp:param name="array" param="productsVOsList" />
					<dsp:param name="elementName" value="productVO"/>
					<dsp:oparam name="output">
					<dsp:getvalueof var="productId" param="productVO.productId" />
				 <dsp:getvalueof var="count" param="count" />
        	 	<dsp:getvalueof var="size" param="size" />
        	 	 
        	 	 <c:if test="${count > 1 && count < 6}">
				    <c:choose>
				    	<c:when test="${count == 2}">
				    		<div class="product homeProd grid_3 alpha marBottom_20">
				    	</c:when>
				    	<c:when test="${count == 3}">
				    		<div class="product homeProd grid_3 omega marBottom_20"> 
				    	</c:when>
				    	<c:when test="${count == 4}">
				    		<div class="product homeProd grid_3 alpha marBottom_10 cb">
				    	</c:when>
				    	<c:otherwise>
				    		<div class="product homeProd grid_3 omega marBottom_10">
				    	</c:otherwise>												 
				    </c:choose>
			 
						<div class="productContent">
								<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
									<dsp:param name="id" param="productVO.productId" />
									<dsp:param name="itemDescriptorName" value="product" />
									<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />										
									</dsp:oparam>
								</dsp:droplet>
								
								<a href="${contextPath}${finalUrl}" class="prodImg" onclick="javascript:personalStore('${strategyNameFromMap}')">
								<dsp:getvalueof var="imageAltText" param="productVO.name" />
								<dsp:getvalueof var="imgSrc" param="productVO.productImages.regularImage" />
								
								<c:choose>
							<c:when test="${empty imgSrc}">
								<img width="226" height="226" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${imageAltText}"   title="${imageAltText}"/>
							</c:when>
							<c:otherwise>
								<img width="226" height="226" src="${scene7Path}/${imgSrc}" alt="${imageAltText}"  class="noImageFound" title="${imageAltText}"/>
							</c:otherwise>
						   </c:choose>
							 </a>
							
							<div class="dataContainer ">
								<div class="prodData listDataItemsWrap">
									<div class="grid_3 alpha padBottom_10 fl quickViewAndCompare"> 
										<span class="quickView ${quickViewClass} fl marRight_30 padRight_5" role="link" aria-label="${lblProductQuickView} ${lblOfThe} ${prodName}">${lblProductQuickView}</span>
										<div class="checkboxItem input clearfix noPad fl noBorder"> 
									<div class="checkbox noMar">
										<input type="checkbox"	data-productid="${productId}" value="compareItem" class="compareChkTxt" id="compareChkTxt_${productId}"
															name="Compare" style="opacity: 0;"> 
									</div>
									<div class="label"> 
										<label class="compareChkTxt" for="compareChkTxt_${productId}">${lblCompareProduct}</label> 
									</div> 
									</div>
							<input type="hidden" class="addItemToList addItemToRegis itemQuantity" value="1" name="qty" data-change-store-submit="qty" />
							<input type="hidden" data-change-store-internaldata="true" data-change-store-errors="required" data-change-store-submit="prodId" value="${productId}" class="_prodId addItemToRegis productId addItemToList" name="prodId"/>
							<input type="hidden" data-change-store-internaldata="true" data-change-store-errors="required" data-change-store-submit="skuId" class="addItemToRegis _skuId addItemToList changeStoreSkuId" value="${skuID}" name="skuId"/>
							<input type="hidden" name="price" value="${omniPrice}" class="addItemToList addItemToRegis" />
							<input type="hidden" data-change-store-storeid="storeId" value="" name="storeId" class="addToCartSubmitData"/>
							<input type="hidden" name="parentProductId" value="${productId}" class="addItemToList addItemToRegis" />
							<input type="hidden" value="" class="addToCartSubmitData" name="bts" data-change-store-storeid="bts"/>
							<input type="hidden" value="true" class="addItemToList addItemToRegis" name="fromSearch"/>
							<input type="hidden" value="true" name="lcFlag"/>
							<input type="hidden" value="${CategoryId}" class="categoryId"/>
							<input type="hidden" value="" class="selectedRollUpValue"/>
							<input type="hidden" value="false" class="isMultiRollUpFlag"/>
							<input type="hidden" data-change-store-storeid="strategyName" value="${strategyNameFromMap}" name="strategyName"  />
									</div>
									<div class="cb"></div>
									<ul class="prodInfo">
											<dsp:getvalueof param="productVO.name" var="prodName" />
											<li class="prodName"><dsp:a page="${finalUrl}"
													title="${prodName}" onclick="javascript:personalStore('${strategyNameFromMap}')">
													<c:out value="${prodName}" escapeXml="false" />
												</dsp:a>
											</li>
							
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
										<dsp:getvalueof var="count" param="count" />
										<c:if test="${count == 1}">
										<li class="prodAttribute">
											<dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/>
										</li>
										</c:if>
										</dsp:oparam>
									</dsp:droplet>
								</c:if>
							</dsp:oparam>
							</dsp:droplet>
											
							<c:if test="${BazaarVoiceOn}">
							<dsp:getvalueof var="ratingAvailable" param="productVO.bvReviews.ratingAvailable"></dsp:getvalueof>
							<dsp:getvalueof param="productVO.bvReviews.ratingsTitle" var="ratingsTitle"/>
							<c:choose>
							<c:when test="${ratingAvailable == true}">
								<dsp:getvalueof var="fltValue" param="productVO.bvReviews.averageOverallRating"/>
								<dsp:getvalueof param="productVO.bvReviews.averageOverallRating" var="averageOverallRating"/>
								<dsp:getvalueof param="productVO.bvReviews.totalReviewCount" var="totalReviewCount"/>
								<c:choose>
									<c:when test="${totalReviewCount == 1}">
									  <li class="prodReviews metaFeedback clearfix"><span class="ratingTxt ratingsReviews prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel">${ratingsTitle}</span></span><span class="reviewTxt">(<c:out value="${totalReviewCount}"/>
									  ${lblReviewCount})</span></li>
									</c:when>
									<c:otherwise>
										<li class="prodReviews clearfix metaFeedback"><span class="ratingTxt ratingsReviews prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel">${ratingsTitle}</span></span><span class="reviewTxt">(<c:out value="${totalReviewCount}"/>
										${lblReviewsCount})</span></li>
									</c:otherwise>
								</c:choose>	
							</c:when>
							<c:otherwise>
								<li class="prodReviews metaFeedback"> 
								<span class="ratingTxt ratingsReviews"><span class="ariaLabel">${ratingsTitle}</span></span><span class="prodReviewSpanFont reviewTxt writeReview"> 
								<c:set var="writeReviewLink">${lblGridWriteReviewLink}</c:set>
								<a href="${contextPath}${finalUrl}?writeReview=true" title="${writeReviewLink}" role="link" aria-label="${lblGridWriteReviewLink} ${lblAboutThe} ${prodName}">${lblGridWriteReviewLink}</a>
								</span> 
								</li>
							</c:otherwise>
							</c:choose>
							  </c:if>
											<li class="prodPrice">
												<c:choose>
													<c:when test="${not empty salePriceRangeDescription}">
														<ul>
															<li class="isPrice"><span class="highlightRed"><dsp:valueof
																		converter="currency"
																		param="productVO.salePriceRangeDescription" /> </span>
															</li>
															<li class="wasPrice"><span class="was">${lblWasText} </span>
																<dsp:valueof converter="currency"
																	param="productVO.priceRangeDescription" />
															</li>
														</ul>
													</c:when>
													<c:otherwise>
														<ul>
															<li class="isPrice"><dsp:valueof converter="currency"
																	param="productVO.priceRangeDescription" />
															</li>
														</ul>
													</c:otherwise>
												</c:choose>
											</li>
										</ul>
								</div>
							</div>
						</div>
						</div>
						</c:if>
						</dsp:oparam>
						</dsp:droplet>
					</div>
				</div>
				<div class="clear"></div>
			</div>
</dsp:page>