<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>


<dsp:page>

		<dsp:getvalueof var="crossSellFlag" param="crossSellFlag"/>	 
		<dsp:getvalueof var="key" param="key"/>	 
		<dsp:getvalueof var="desc" param="desc"/>	 
		<dsp:getvalueof var="kickstarterItems" param="kickstarterItems"/>	
<dsp:getvalueof var="valueMap" bean="SessionBean.values" />
<c:set var="countryCodeLowerCase">${fn:toLowerCase(valueMap.defaultUserCountryCode)}</c:set>		
<c:set var="lblAboutThe"><bbbl:label key="lbl_accessibility_about_the" language="${pageContext.request.locale.language}" /></c:set>
	
<c:set var="disableLazyLoadS7Images">
	<bbbc:config key="disableLazyLoadS7ImagesFlag" configName="FlagDrivenFunctions" />
</c:set>
	
<c:set var="prodImageAttrib">class="productImage noImageFound" src</c:set>	
	
<c:if test="${not empty disableLazyLoadS7Images and not disableLazyLoadS7Images}">
	<c:set var="prodImageAttrib">class="productImage lazyLoad loadingGIF" src="${imagePath}/_assets/global/images/blank.gif" data-lazyloadsrc</c:set>
</c:if>	
	
		<c:set var="onClickEvent" value=""/>
		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
		<dsp:param name="array" param="productsVOsList" />
		<dsp:param name="elementName" value="productVO"/>
			<dsp:oparam name="outputStart">
			<div class="carousel clearfix">
							
			<div class="carouselBody grid_12">
				<div class="grid_1 carouselArrow omega carouselArrowPrevious clearfix">
					&nbsp; <a class="carouselScrollPrevious" role="button" title="Previous" href="#"><bbbl:label key='lbl_certona_slots_previous' language="${pageContext.request.locale.language}" /></a>
				</div>
				
				<div class="carouselContent grid_10 clearfix">
				<ul class="prodGridRow">
			</dsp:oparam>
			<dsp:oparam name="output">
			<li class="grid_2 product resetRLMargin">
			<div class="productShadow"></div>
			<div class="productContent">
				<dsp:getvalueof var="productId" param="productVO.productId"/>
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

			<dsp:getvalueof var="mediumImage" param="productVO.productImages.mediumImage"/>
			<dsp:getvalueof var="basicImage" param="productVO.productImages.basicImage"/>
			<c:set var="basicImage">${basicImage}?$170$</c:set>
				<c:set var="productName">
				<dsp:valueof param="productVO.name" valueishtml="true"/>
				</c:set>
				<dsp:getvalueof var="isIntlRestricted" param="productVO.intlRestricted"/>
				<c:choose>
				
				<c:when test="${(crossSellFlag ne null) && (crossSellFlag eq 'true')}">
			
					<c:set var="onClickEvent">javascript:pdpCrossSellProxy('crossSell', '${desc}')</c:set>
				</c:when>
				<c:otherwise>
			
					<c:set var="onClickEvent" value=""/>
				</c:otherwise>
				</c:choose>
		
				
			<a class="recCertonaLink prodImg" href="${contextPath}${finalUrl}" title="${productName}" data-finalUrl="${contextPath}${finalUrl}" data-desc="${desc}">
				
					<c:choose>
						<c:when test="${empty basicImage}">
							<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="160" width="163" alt=" ${productName}" /> 
						</c:when>
						<c:otherwise>
							<img ${prodImageAttrib}="${scene7Path}/${basicImage}" alt="image of ${productName}" height="160" width="163"/>
						</c:otherwise>
					</c:choose>
					
					<span class="visuallyhidden"><dsp:valueof param="productVO.priceRangeDescription"/>. <c:if test="${BazaarVoiceOn}"> with <dsp:valueof param="productVO.bvReviews.totalReviewCount"/> reviews. and <dsp:valueof param="productVO.bvReviews.ratingsTitle"/>
					</c:if>	</span>
											
					<noscript aria-hidden='true'>
						<c:choose>
						<c:when test="${empty basicImage}">
							<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="160" width="163" alt=" ${productName} ."/>
						</c:when>
						<c:otherwise>
								<img ${prodImageAttrib}="${scene7Path}/${basicImage}" alt="image of ${productName}" height="160" width="163"/>
						</c:otherwise>
						</c:choose>
					</noscript>
					<span class="prodInfo associtedOverlay"  aria-hidden='true'>
					<span class="overlayContent">
						<span class="prodName">${productName}</span>
						<dsp:getvalueof var="salePriceRangeDescription" param="productVO.salePriceRangeDescription" />
					<%--  BBB AJAX 2.3.1  Was-Is price change on PLP,Search,Brand starts--%>
						<c:if test="${BazaarVoiceOn}">
						<dsp:getvalueof var="ratingAvailable" param="productVO.bvReviews.ratingAvailable"></dsp:getvalueof>
						<c:choose>
						<c:when test="${ratingAvailable == true}">
							<dsp:getvalueof var="fltValue" param="productVO.bvReviews.averageOverallRating"/>
							<dsp:getvalueof param="productVO.bvReviews.totalReviewCount" var="totalReviewCount"/>
							<c:choose>
								<c:when test="${totalReviewCount == 1}">
									<span class="clearfix metaFeedback"><span title="${fltValue}" class="ratingTxt prodReview prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel"><dsp:valueof param="productVO.bvReviews.ratingsTitle"/></span><span class="reviewTxt">(<dsp:valueof param="productVO.bvReviews.totalReviewCount"/>)</span></span>
								</c:when>
								<c:otherwise>
									<span class="clearfix metaFeedback"><span title="${fltValue}" class="ratingTxt prodReview prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel"><dsp:valueof param="productVO.bvReviews.ratingsTitle"/></span><span class="reviewTxt">(<dsp:valueof param="productVO.bvReviews.totalReviewCount"/>)</span></span>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<c:set var="writeReviewLink"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></c:set>
							<span class="metaFeedback"><span class="ratingTxt prodReview withoutReviewCount"><span class="ariaLabel"><dsp:valueof param="productVO.bvReviews.ratingsTitle"/></span> </span></span>
						</c:otherwise>
						</c:choose>
						</c:if>	
						<span class="prodPrice">
					
			<dsp:getvalueof var="kickStrtWasPriceRangeDescripMX" param="productVO.kickStarterPrice.kickStrtWasPriceRangeDescripMX"></dsp:getvalueof>
			<dsp:getvalueof var="kickStrtWasPriceRangeDescrip" param="productVO.kickStarterPrice.kickStrtWasPriceRangeDescrip"></dsp:getvalueof>
			<dsp:getvalueof var="prodListPrice" param="productVO.priceRangeDescription"/>
			<c:set var= "priceIsTBD"><bbbl:label key='lbl_price_is_tbd' language="${pageContext.request.locale.language}" /></c:set>
				<c:choose>
				<c:when test="${kickstarterItems}">
					
							<c:choose>
							<c:when test="${countryCodeLowerCase eq 'mx'}">
							
							<c:choose>
						  <c:when test="${not empty kickStrtWasPriceRangeDescripMX}">
							<span>
									<span class="isPrice">
										<span><dsp:valueof 
											param="productVO.kickStarterPrice.kickStrtPriceRangeDescripMX"/></span>
									</span>
									<span class="wasPrice">
									<span class="was"><bbbl:label key='lbl_was_text' language="${pageContext.request.locale.language}" /></span>
										 <dsp:valueof converter="currency" param="productVO.kickStarterPrice.kickStrtWasPriceRangeDescripMX"/></span>
									
									</span>
									</c:when>
									<c:otherwise>
									<span>
									<span class="isPrice">
										<dsp:valueof converter="currency" param="productVO.kickStarterPrice.kickStrtPriceRangeDescripMX"/>
									</span>
								</span>
									</c:otherwise>
									</c:choose>
									</c:when>
									
									<c:otherwise>
									<c:choose>
						           <c:when test="${not empty kickStrtWasPriceRangeDescrip}">

									<span>
									<span class="isPrice">
										<span><dsp:valueof converter="currency"
											param="productVO.kickStarterPrice.kickStrtPriceRangeDescrip"/></span>
									</span>
									
									<span class="wasPrice">
									<span class="was"><bbbl:label key='lbl_was_text' language="${pageContext.request.locale.language}" /></span>
										 <dsp:valueof converter="currency" param="productVO.kickStarterPrice.kickStrtWasPriceRangeDescrip"/></span>
									
									</span>
									</c:when>
									<c:otherwise>
									<span>
									<span class="isPrice">
										<dsp:valueof converter="currency" param="productVO.kickStarterPrice.kickStrtPriceRangeDescrip"/>
									</span>
									</span>
									</c:otherwise>
									</c:choose>
									</c:otherwise>
									</c:choose>
							</c:when>
							<c:otherwise>
							
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
						
					</c:otherwise>
				</c:choose>
				
				
						
					</span>
					<%--  BBB AJAX 2.3.1  Was-Is price change on PLP,Search,Brand ends--%>
					
				</span>
				</span>
				</a>
			</div>
			</li>
			</dsp:oparam>
			<dsp:oparam name="outputEnd">
					</ul>
					</div>
					<div class="grid_1 carouselArrow alpha carouselArrowNext clearfix">
						&nbsp; <a class="carouselScrollNext" title="Next" role="button" href="#"><bbbl:label key='lbl_certona_slots_next' language="${pageContext.request.locale.language}" /></a>
					</div>
				</div>
			  
			</div>
			</dsp:oparam>
		</dsp:droplet>
		
	     
</dsp:page>
