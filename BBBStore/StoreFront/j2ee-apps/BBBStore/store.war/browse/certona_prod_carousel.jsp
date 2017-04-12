<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
<dsp:getvalueof var="isInternationalCustomer" bean="SessionBean.internationalShippingContext" />

		<dsp:getvalueof var="crossSellFlag" param="crossSellFlag"/>	 
		<dsp:getvalueof var="key" param="key"/>	 
		<dsp:getvalueof var="desc" param="desc"/>	 
		<dsp:getvalueof var="kickstarterItems" param="kickstarterItems"/>	
<dsp:getvalueof var="valueMap" bean="SessionBean.values" />
<c:set var="countryCodeLowerCase">${fn:toLowerCase(valueMap.defaultUserCountryCode)}</c:set>		
<c:set var="lblAboutThe"><bbbl:label key="lbl_accessibility_about_the" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="ShipMsgDisplayFlag" scope="request"><bbbc:config key="ShipMsgDisplayFlag" configName="FlagDrivenFunctions"/></c:set>
<c:set var="shippingAttributesList">
	<bbbc:config key="shippingAttributesList" configName="ContentCatalogKeys" />
</c:set>
<c:set var="BedBathUSSite">
	<bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
</c:set>
<c:set var="BuyBuyBabySite">
	<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
</c:set>
<c:set var="BedBathCanadaSite">
	<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
</c:set>
<c:set var="disableLazyLoadS7Images">
	<bbbc:config key="disableLazyLoadS7ImagesFlag" configName="FlagDrivenFunctions" />
</c:set>

		<c:set var="prodImageAttrib">class="productImage noImageFound" src</c:set>

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
				<dsp:getvalueof id="itemCount" param="count" />
				<c:if test="${(not empty disableLazyLoadS7Images and not disableLazyLoadS7Images) || itemCount > 5}">
					<c:set var="prodImageAttrib">class="productImage lazyLoad loadingGIF" src="${imagePath}/_assets/global/images/blank.gif" data-lazyloadsrc</c:set>
				</c:if>

				<li class="grid_2 product alpha">
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
		
				<dsp:a iclass="prodImg" page="${finalUrl}" title='${productName}' onclick="${onClickEvent}">
					<c:choose>
						<c:when test="${empty mediumImage}">
							<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="146" width="146" alt="image of ${productName}" /> 
						</c:when>
						<c:otherwise>
							<img ${prodImageAttrib}="${scene7Path}/${mediumImage}" height="146" width="146" alt="image of ${productName}" <c:if test="${itemCount > 5}"> style="display: none;" </c:if> />
						</c:otherwise>
					</c:choose>
					<noscript>
						<c:choose>
						<c:when test="${empty mediumImage}">
							<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="146" width="146" alt="image of ${productName}"/>
						</c:when>
						<c:otherwise>
							<img src="${scene7Path}/${mediumImage}" height="146" width="146" alt="image of ${productName}" class="noImageFound"/>
						</c:otherwise>
						</c:choose>
					</noscript>
				</dsp:a>
				<ul class="prodInfo">
					<li class="prodName"><dsp:a page="${finalUrl}" title="${productName}" onclick="${onClickEvent}">${productName}</dsp:a></li>
					<dsp:getvalueof var="salePriceRangeDescription" param="productVO.salePriceRangeDescription" />
					<%--  BBB AJAX 2.3.1  Was-Is price change on PLP,Search,Brand starts--%>
			<li class="prodPrice">
			<dsp:getvalueof var="kickStrtWasPriceRangeDescripMX" param="productVO.kickStarterPrice.kickStrtWasPriceRangeDescripMX"></dsp:getvalueof>
			<dsp:getvalueof var="kickStrtWasPriceRangeDescrip" param="productVO.kickStarterPrice.kickStrtWasPriceRangeDescrip"></dsp:getvalueof>
			<dsp:getvalueof var="prodListPrice" param="productVO.priceRangeDescription"/>
			<c:set var= "priceIsTBD"><bbbl:label key='lbl_price_is_tbd' language="${pageContext.request.locale.language}" /></c:set>
				<c:choose>
				<c:when test="${kickstarterItems}">
							<c:choose>
							<c:when test="${countryCodeLowerCase eq 'mx'}">
								<dsp:getvalueof var="isdynamicPrice" param="productVO.bbbProduct.dynamicPriceVO.dynamicProdEligible" />
								<dsp:getvalueof var="priceLabelCode" param="productVO.bbbProduct.dynamicPriceVO.mxPricingLabelCode" />
								<dsp:getvalueof var="inCartFlag" param="productVO.bbbProduct.dynamicPriceVO.mxIncartFlag" />
								<dsp:getvalueof var="wasPriceRange" param="productVO.bbbProduct.wasPriceRangeMX" />
								<dsp:getvalueof var="isPriceRange" param="productVO.bbbProduct.priceRangeMX" />
								<c:if test="${empty wasPriceRange}">
							      <c:set var="wasPriceRange" value="${isPriceRange}"/>
								  <c:set var="isPriceRange" value=""/>
							    </c:if>
								<dsp:include page="browse_price_frag.jsp">
								    <dsp:param name="priceLabelCode" value="${priceLabelCode}" />
									<dsp:param name="inCartFlag" value="${inCartFlag}" />
									<dsp:param name="salePrice" value="${isPriceRange}" />
									<dsp:param name="listPrice" value="${wasPriceRange}" />
									<dsp:param name="isdynamicPriceEligible" value="${isdynamicPrice}" />
								</dsp:include> 
						     </c:when>									
							<c:otherwise>
								<c:choose>
									<c:when test="${currentSiteId eq BedBathUSSite}">
										<dsp:getvalueof var="isdynamicPrice" param="productVO.bbbProduct.dynamicPriceVO.dynamicProdEligible" />
										<dsp:getvalueof var="priceLabelCode" param="productVO.bbbProduct.dynamicPriceVO.bbbPricingLabelCode" />
										<dsp:getvalueof var="inCartFlag" param="productVO.bbbProduct.dynamicPriceVO.bbbIncartFlag" />
										<dsp:getvalueof var="wasPriceRange" param="productVO.bbbProduct.wasPriceRange" />
										<dsp:getvalueof var="isPriceRange" param="productVO.bbbProduct.priceRange" />
									</c:when>
									<c:when test="${currentSiteId eq BuyBuyBabySite}">
										<dsp:getvalueof var="isdynamicPrice" param="productVO.bbbProduct.dynamicPriceVO.dynamicProdEligible" />
										<dsp:getvalueof var="priceLabelCode" param="productVO.bbbProduct.dynamicPriceVO.babyPricingLabelCode" />
										<dsp:getvalueof var="inCartFlag" param="productVO.bbbProduct.dynamicPriceVO.babyIncartFlag" />
										<dsp:getvalueof var="wasPriceRange" param="productVO.bbbProduct.wasPriceRange" />
										<dsp:getvalueof var="isPriceRange" param="productVO.bbbProduct.priceRange" />
									</c:when>
									<c:when test="${currentSiteId eq BedBathCanadaSite}">
										<dsp:getvalueof var="isdynamicPrice" param="productVO.bbbProduct.dynamicPriceVO.dynamicProdEligible" />
										<dsp:getvalueof var="priceLabelCode" param="productVO.bbbProduct.dynamicPriceVO.caPricingLabelCode" />
										<dsp:getvalueof var="inCartFlag" param="productVO.bbbProduct.dynamicPriceVO.caIncartFlag" />
										<dsp:getvalueof var="wasPriceRange" param="productVO.bbbProduct.wasPriceRange" />
										<dsp:getvalueof var="isPriceRange" param="productVO.bbbProduct.priceRange" />
									</c:when>
							   </c:choose>
								<c:if test="${empty wasPriceRange}">
								  <c:set var="wasPriceRange" value="${isPriceRange}"/>
								  <c:set var="isPriceRange" value=""/>
							   	</c:if>
								<dsp:include page="browse_price_frag.jsp">
								    <dsp:param name="priceLabelCode" value="${priceLabelCode}" />
									<dsp:param name="inCartFlag" value="${inCartFlag}" />
									<dsp:param name="salePrice" value="${isPriceRange}" />
									<dsp:param name="listPrice" value="${wasPriceRange}" />
									<dsp:param name="isdynamicPriceEligible" value="${isdynamicPrice}" />
								</dsp:include> 
							</c:otherwise>
							</c:choose>
				</c:when>
				<c:otherwise>
					<dsp:getvalueof var="isdynamicPriceProd" param="productVO.dynamicPricingProduct" />
					<dsp:getvalueof var="priceLabelCodeProd" param="productVO.priceLabelCode" />
					<dsp:getvalueof var="inCartFlagProd" param="productVO.inCartFlag" />
					<dsp:getvalueof var="priceRangeDescription" param="productVO.priceRangeDescription" />
					<dsp:include page="browse_price_frag.jsp">
					    <dsp:param name="priceLabelCode" value="${priceLabelCodeProd}" />
						<dsp:param name="inCartFlag" value="${inCartFlagProd}" />
						<dsp:param name="salePrice" value="${salePriceRangeDescription}" />
						<dsp:param name="listPrice" value="${priceRangeDescription}" />
						<dsp:param name="isdynamicPriceEligible" value="${isdynamicPriceProd}" />
					</dsp:include>   		
				</c:otherwise>
				</c:choose>
				</li>
					<%--  BBB AJAX 2.3.1  Was-Is price change on PLP,Search,Brand ends--%>
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
									<li class="productAttributes prodAttribWrapper">
										<dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/>
									</li>
									</dsp:oparam>
								</dsp:droplet>
							</c:if>
						</dsp:oparam>
					</dsp:droplet>
					<c:if test="${ShipMsgDisplayFlag && isInternationalCustomer ne true && showShipCustomMsg}">
							<c:choose>
								<c:when test="${kickstarterItems}">

									<li><dsp:valueof
											param="productVO.kickStarterPrice.displayShipMsg"
											valueishtml="true" /></li>
								</c:when>
								<c:otherwise>
									<li><dsp:valueof param="productVO.displayShipMsg"
											valueishtml="true" /></li>
								</c:otherwise>
							</c:choose>
						</c:if>
					<c:choose>
                       	<c:when test="${currentSiteId eq BedBathUSSite}">
			                 <c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_us"/></c:set>
		                </c:when>
		                <c:when test="${currentSiteId eq BuyBuyBabySite}">
			                 <c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_baby"/></c:set>
		                </c:when>
		                <c:otherwise>
			                 <c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_ca"/></c:set>
		                </c:otherwise>
	                </c:choose>
					<c:if test="${BazaarVoiceOn}">
					<dsp:getvalueof var="ratingAvailable" param="productVO.bvReviews.ratingAvailable"></dsp:getvalueof>
					<c:choose>
					<c:when test="${ratingAvailable == true}">
						<dsp:getvalueof var="fltValue" param="productVO.bvReviews.averageOverallRating"/>
						<dsp:getvalueof param="productVO.bvReviews.totalReviewCount" var="totalReviewCount"/>
						<c:choose>
							<c:when test="${totalReviewCount == 1}">
								<li class="clearfix metaFeedback"><span title="${fltValue}" class="ratingTxt prodReview prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel"><dsp:valueof param="productVO.bvReviews.ratingsTitle"/></span></span><span class="reviewTxt">(<dsp:valueof param="productVO.bvReviews.totalReviewCount"/><bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" />)</span></li>
							</c:when>
							<c:otherwise>
								<li class="clearfix metaFeedback"><span title="${fltValue}" class="ratingTxt prodReview prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel"><dsp:valueof param="productVO.bvReviews.ratingsTitle"/></span></span><span class="reviewTxt">(<dsp:valueof param="productVO.bvReviews.totalReviewCount"/><bbbl:label key='lbl_reviews_count' language="${pageContext.request.locale.language}" />)</span></li>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<c:set var="writeReviewLink"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></c:set>
						<li class="metaFeedback"><span class="ratingTxt prodReview"><span class="ariaLabel"><dsp:valueof param="productVO.bvReviews.ratingsTitle"/></span> </span><span class="writeReview reviewTxt"><a href="${pageContext.request.contextPath}${finalUrl}?writeReview=true"  title="${writeReviewLink}" role="link" aria-label="${writeReviewLink} ${lblAboutThe} ${productName}"><bbbl:label key='lbl_grid_write_review_link' language="${pageContext.request.locale.language}" /></a></span></li>
					</c:otherwise>
					</c:choose>
					</c:if>				
				</ul>
				<c:if test="${isInternationalCustomer && isIntlRestricted}">
                	<div class="notAvailableIntShipMsg cb clearfix marBottom_10">
                		<bbbl:label key="lbl_plp_intl_restrict_list_msg" language="${pageContext.request.locale.language}" />
                	</div>
                </c:if>
			</div>
			</li>
			</dsp:oparam>
			<dsp:oparam name="outputEnd">
					</ul>
					</div>
	
					<div class="grid_1 carouselArrow alpha carouselArrowNext clearfix">
						&nbsp; <a class="carouselScrollNext" title="Next" role="button" href="#"><bbbl:label key='lbl_certona_slots_next' language="${pageContext.request.locale.language}" /></a>
					</div>
					<div class="carouselPages">
						<div class="carouselPageLinks clearfix">
							<a title="Page 1" class="selected" href="#">1</a> <a title="Page 2"
								href="#">2</a> <a title="Page 3" href="#">3</a> <a title="Page 4"
								href="#">4</a>
						</div>
					</div>
				</div>
			  
			</div>
			</dsp:oparam>
		</dsp:droplet>
		
	     <%-- pdpOmnitureProxy is now defined globally in omniture_tracking.js with a different signature and thus this cannot be used.
         if something breaks ... please add a new function with a different name for your specific use-case... as "pdpOmnitureProxy" needs that specific signature as defined in omniture_tracking.js
         <script type="text/javascript">       
	     function pdpOmnitureProxy(productId, event,event1,desc) {
			   if(event == 'crossSell') {
				   if (typeof s_crossSell === 'function') { s_crossSell(); }
			   } 
			   if(event1 == 'pfm') {
				
				   if (typeof s_crossSell === 'function') { s_crossSell(desc); }
			   } 
			   
		   }
           
        </script>	--%>
</dsp:page>
