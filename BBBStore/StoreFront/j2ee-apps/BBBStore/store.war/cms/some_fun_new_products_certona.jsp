<dsp:page>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof var="funNewProductsVOsList" param="funNewProductsVOsList" />
<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
<dsp:getvalueof var="isInternationalCustomer" bean="SessionBean.internationalShippingContext" />
<c:set var="lblAboutThe"><bbbl:label key="lbl_accessibility_about_the" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblGridWriteReviewLink"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="ShipMsgDisplayFlag" scope="request"><bbbc:config key="ShipMsgDisplayFlag" configName="FlagDrivenFunctions"/></c:set>
	<c:set var="shippingAttributesList">
		<bbbc:config key="shippingAttributesList" configName="ContentCatalogKeys" />
	</c:set>
<dsp:getvalueof var="key" param="key"/>	 

	<div id="someFunNewProducts" class="grid_12">
    <bbbl:label key="lbl_homePage_newProducts" language="<c:out param='${pageContext.request.locale.language}'/>"/>
    <div class="someFunNewProductsList">
    
    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
			<dsp:param name="array" param="funNewProductsVOsList" />
			<dsp:param name="elementName" value="productVO"/>
			<dsp:oparam name="output">
				 
			
        <dsp:getvalueof var="count" param="count" />
        <dsp:getvalueof var="size" param="size" />
    	<dsp:getvalueof var="isIntlRestricted" param="productVO.intlRestricted"/>
        <c:choose>
	    <c:when test="${count eq 1}">
	        
	        <div class="grid_6 alpha">
	            <div class="productShadow"></div>
	            <div class="productContent">
					<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
						<dsp:param name="id" param="productVO.productId" />
						<dsp:param name="itemDescriptorName" value="product" />
						<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
						</dsp:oparam>
					</dsp:droplet>
			
	                <dsp:a iclass="prodImg" href="${contextPath}${finalUrl}">
	                	<dsp:getvalueof var="imgSrc" param="productVO.productImages.largeImage" />
						<dsp:getvalueof var="imageAltText" param="productVO.name" />
						
						<c:choose>
							<c:when test="${empty imgSrc}">
								<img src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${imageAltText}" height="478" width="478" title="${imageAltText}"/>
							</c:when>
							<c:otherwise>
								<img class="loadingGIF lazyLoad" data-lazyloadsrc="${scene7Path}/${imgSrc}" src="" alt="${imageAltText}" height="478" width="478"  title="${imageAltText}"/>
							</c:otherwise>
						</c:choose>
	                </dsp:a>
	                
	                <ul class="prodInfo">
						<dsp:getvalueof param="productVO.name" var="prodName"/>
	                    <li class="prodName">
						<dsp:a page="${finalUrl}" title="${prodName}"><c:out value="${prodName}" escapeXml="false"/> </dsp:a>
						</li>
	                    <li class="prodPrice">
	                    <dsp:getvalueof var="salePriceDescrip" param="productVO.salePriceRangeDescription"/>
	                    <dsp:getvalueof var="isdynamicPriceProd" param="productVO.dynamicPricingProduct" />
						<dsp:getvalueof var="priceLabelCodeProd" param="productVO.priceLabelCode" />
						<dsp:getvalueof var="inCartFlagProd" param="productVO.inCartFlag" />
						<dsp:getvalueof var="priceRangeDescription" param="productVO.priceRangeDescription" />
							<dsp:include page="/browse/browse_price_frag.jsp">
							    <dsp:param name="priceLabelCode" value="${priceLabelCodeProd}" />
								<dsp:param name="inCartFlag" value="${inCartFlagProd}" />
								<dsp:param name="salePrice" value="${salePriceDescrip}" />
								<dsp:param name="listPrice" value="${priceRangeDescription}" />
								<dsp:param name="isdynamicPriceEligible" value="${isdynamicPriceProd}" />
							</dsp:include>   		
						</li>
						<c:set var="showShipCustomMsg" value="true"/>
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param param="productVO.attributesList" name="array" />
								<dsp:param name="elementName" value="attributeVOList"/>
								<dsp:oparam name="output">
									<dsp:getvalueof var="placeholderMain" param="key"/>
									<c:if test="${placeholderMain eq 'CRSL'}">
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
								<li> <dsp:valueof param="productVO.displayShipMsg" valueishtml="true" /></li>
							</c:if>
			
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param param="productVO.attributesList" name="array" />
								<dsp:param name="elementName" value="attributeVOList"/>
								<dsp:oparam name="output">
									<dsp:getvalueof var="placeholderMain" param="key"/>
									<c:if test="${placeholderMain eq 'CRSL'}">
										<dsp:droplet name="/atg/dynamo/droplet/ForEach">
											<dsp:param param="attributeVOList" name="array" />
											<dsp:param name="elementName" value="attributeVO"/>
											<dsp:param name="sortProperties" value="+priority"/>
											<dsp:oparam name="output">
											<li class="productAttributes prodAttribWrapper">
												<dsp:getvalueof	param="attributeVO.attributeDescrip" var="attributeDescrip"/>
								                <c:out value="${attributeDescrip}" escapeXml="false"/>
											</li>
											</dsp:oparam>
										</dsp:droplet>
									</c:if>
								</dsp:oparam>
							</dsp:droplet>
	                    <c:if test="${BazaarVoiceOn}">
						<dsp:getvalueof var="ratingAvailable" param="productVO.bvReviews.ratingAvailable"></dsp:getvalueof>
							<c:choose>
							<c:when test="${ratingAvailable == true}">
								<dsp:getvalueof var="fltValue" param="productVO.bvReviews.averageOverallRating"/>
								<dsp:getvalueof param="productVO.bvReviews.averageOverallRating" var="averageOverallRating"/>
								<dsp:getvalueof param="productVO.bvReviews.totalReviewCount" var="totalReviewCount"/>
								<c:choose>
									<c:when test="${totalReviewCount == 1}">
									  <li class="clearfix metaFeedback"><span title="${averageOverallRating}" class="ratingTxt prodReview prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel"><dsp:valueof param="productVO.bvReviews.ratingsTitle"/></span></span><span class="reviewTxt">(<dsp:valueof param="productVO.bvReviews.totalReviewCount"/><bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" />)</span></li>
									</c:when>
									<c:otherwise>
										<li class="clearfix metaFeedback"><span title="${averageOverallRating}" class="ratingTxt prodReview prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel"><dsp:valueof param="productVO.bvReviews.ratingsTitle"/></span></span><span class="reviewTxt">(<dsp:valueof param="productVO.bvReviews.totalReviewCount"/><bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" />)</span></li>
									</c:otherwise>
								</c:choose>	
							</c:when>
							<c:otherwise>
								<c:set var="writeReviewLink"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></c:set>
								<li class="metaFeedback"><span class="ratingTxt prodReview"><span class="ariaLabel"><dsp:valueof param="productVO.bvReviews.ratingsTitle"/></span> </span><span class="writeReview reviewTxt"><dsp:a page="${finalUrl}?writeReview=true"  title="${writeReviewLink}"><bbbl:label key='lbl_grid_write_review_link' language="${pageContext.request.locale.language}" /></dsp:a></span></li>
							</c:otherwise>
							</c:choose>
						</c:if>
						<c:if test="${isInternationalCustomer && isIntlRestricted}">
	                		<div class="notAvailableIntShipMsg">
	                			<bbbl:label key="lbl_plp_intl_restrict_list_msg" language="${pageContext.request.locale.language}" />
	                		</div>
               			</c:if>
	                </ul>
	            </div>
	        </div>
       </c:when>
       <c:otherwise>
	   <c:if test="${count eq 2}">
         <div class="grid_6 omega">
	   </c:if>  	 
						<c:set var="custClass" value="grid_2" />
						<c:if test="${(count-1) % 3 == 1}">
							<c:set var="custClass" value="grid_2 alpha" />
						</c:if>
						<c:if test="${(count-1) % 3 == 0}">
							<c:set var="custClass" value="grid_2 omega" />
						</c:if>
						<div class="homeProd <c:out value="${custClass}"/>">		
							<div class="productShadow"></div>
			                <div class="productContent">
								<dsp:getvalueof var="imageAltText" param="productVO.name" />
								<dsp:getvalueof var="imgSrc" param="productVO.productImages.mediumImage" />
								<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
									<dsp:param name="id" param="productVO.productId" />
									<dsp:param name="itemDescriptorName" value="product" />
									<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />										
									</dsp:oparam>
								</dsp:droplet>
								
								<a class="prodImg" href="${contextPath}${finalUrl}">
								
								<c:choose>
									<c:when test="${empty imgSrc}">
										<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="146" width="146" alt="${imageAltText}" title="${imageAltText}"/>
									</c:when>
									<c:otherwise>
										<img class="loadingGIF lazyLoad" data-lazyloadsrc="${scene7Path}/${imgSrc}" src="" height="146" width="146" alt="${imageAltText}" title="${imageAltText}"/>
									</c:otherwise>
								</c:choose>
								</a>
			                    
			                    <ul class="prodInfo">
								<dsp:getvalueof param="productVO.name" var="prodName"/>
			                        <li class="prodName">
											<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
												<dsp:param name="id" param="productVO.productId" />
												<dsp:param name="itemDescriptorName" value="product" />
												<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
												<dsp:oparam name="output">
													<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
													<dsp:a page="${finalUrl}" title="${prodName}"><c:out value="${prodName}" escapeXml="false"/> </dsp:a>
												</dsp:oparam>
											</dsp:droplet>
										
										</li>
									
			                          <li class="prodPrice">
			                          <dsp:getvalueof var="salePriceDescrip" param="productVO.salePriceRangeDescription"/>
					                    <dsp:getvalueof var="isdynamicPriceProd" param="productVO.dynamicPricingProduct" />
										<dsp:getvalueof var="priceLabelCodeProd" param="productVO.priceLabelCode" />
										<dsp:getvalueof var="inCartFlagProd" param="productVO.inCartFlag" />
										<dsp:getvalueof var="priceRangeDescription" param="productVO.priceRangeDescription" />
											<dsp:include page="/browse/browse_price_frag.jsp">
											    <dsp:param name="priceLabelCode" value="${priceLabelCodeProd}" />
												<dsp:param name="inCartFlag" value="${inCartFlagProd}" />
												<dsp:param name="salePrice" value="${salePriceDescrip}" />
												<dsp:param name="listPrice" value="${priceRangeDescription}" />
												<dsp:param name="isdynamicPriceEligible" value="${isdynamicPriceProd}" />
											</dsp:include> 
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
													<dsp:oparam name="output">
													<dsp:getvalueof var="attrId" param="attributeVO.attributeName" />
																<c:if test="${fn:contains(shippingAttributesList,attrId)}">
																	<c:set var="showShipCustomMsg" value="false"/>
																</c:if>
													<li class="productAttributes prodAttribWrapper">
														<dsp:getvalueof var="attributeDescrip" param="attributeVO.attributeDescrip"/>
															<c:out value="${attributeDescrip}" escapeXml="false"/>
													</li>
													</dsp:oparam>
												</dsp:droplet>
											</c:if>
										</dsp:oparam>
									</dsp:droplet>
									<c:if test="${ShipMsgDisplayFlag && showShipCustomMsg}">
									<li><dsp:valueof param="productVO.displayShipMsg" valueishtml="true" /></li>
								</c:if>	
			                       <c:if test="${BazaarVoiceOn}">
									<dsp:getvalueof var="ratingAvailable" param="productVO.bvReviews.ratingAvailable"></dsp:getvalueof>
										<c:choose>
										<c:when test="${ratingAvailable == true}">
											<dsp:getvalueof var="fltValue" param="productVO.bvReviews.averageOverallRating"/>
											<dsp:getvalueof param="productVO.bvReviews.averageOverallRating" var="averageOverallRating"/>
											<dsp:getvalueof param="productVO.bvReviews.totalReviewCount" var="totalReviewCount"/>
											<c:choose>
												<c:when test="${totalReviewCount == 1}">
													<li class="clearfix metaFeedback"><span title="${averageOverallRating}" class="ratingTxt prodReview prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel"><dsp:valueof param="productVO.bvReviews.ratingsTitle"/></span></span><span class="reviewTxt">(<dsp:valueof param="productVO.bvReviews.totalReviewCount"/><bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" />)</span></li>
												</c:when>
												<c:otherwise>
													<li class="prodReviews clearfix metaFeedback"><span title="${averageOverallRating}" class="ratingTxt prodReview prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel"><dsp:valueof param="productVO.bvReviews.ratingsTitle"/></span></span><span class="reviewTxt">(<dsp:valueof param="productVO.bvReviews.totalReviewCount"/><bbbl:label key='lbl_reviews_count' language="${pageContext.request.locale.language}" />)</span></li>
												</c:otherwise>
												</c:choose>					
										</c:when>
										<c:otherwise>
											
											<c:set var="writeReviewLink"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></c:set>
											<li class="metaFeedback"><span class="ratingTxt prodReview"><span class="ariaLabel"><dsp:valueof param="productVO.bvReviews.ratingsTitle"/></span> </span><span class="writeReview reviewTxt"><a href="${contextPath}${finalUrl}?writeReview=true"  title="${writeReviewLink}" role="link" aria-label="${lblGridWriteReviewLink} ${lblAboutThe} ${prodName}"><bbbl:label key='lbl_grid_write_review_link' language="${pageContext.request.locale.language}" /></a></span></li>
											
										</c:otherwise>
										</c:choose>
									</c:if>
									<c:if test="${isInternationalCustomer && isIntlRestricted}">
	                					<div class="notAvailableIntShipMsg">
	                						<bbbl:label key="lbl_plp_intl_restrict_list_msg" language="${pageContext.request.locale.language}" />
	                					</div>
                					</c:if>
			                    </ul>
			                </div>
						</div>
						
					 
			 
		<c:if test="${count eq size}">				
        </div>
        </c:if>
         
        </c:otherwise>
        </c:choose>
        
        
        </dsp:oparam>
		</dsp:droplet>
        <div class="clear"></div>
    </div>
</div>
</dsp:page>