<dsp:page>
		<c:set var="lblAboutThe"><bbbl:label key="lbl_accessibility_about_the" language="${pageContext.request.locale.language}" /></c:set>
		<c:set var="ShipMsgDisplayFlag" scope="request"><bbbc:config key="ShipMsgDisplayFlag" configName="FlagDrivenFunctions"/></c:set>
		<c:set var="shippingAttributesList">
			<bbbc:config key="shippingAttributesList" configName="ContentCatalogKeys" />
		</c:set>
			
		<ul class="prodGridRow">
		<li class="grid_2 product alpha">
		<div class="productContent marTop_20 hidden">
		
		<dsp:getvalueof var="productId" param="oosProductVo.productId"/>
		<dsp:droplet name="/atg/repository/seo/CanonicalItemLink"> 
					<dsp:param name="id" param="oosProductVo.productId" />
					<dsp:param name="itemDescriptorName" value="product" />
					<dsp:param name="repositoryName"
						value="/atg/commerce/catalog/ProductCatalog" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
							param="url" />
					</dsp:oparam>
				</dsp:droplet>

			<dsp:getvalueof var="mediumImage" param="oosProductVo.productImages.mediumImage"/>
				<c:set var="productName">
					<dsp:valueof param="oosProductVo.name" valueishtml="true"/>
				</c:set>

				<dsp:a iclass="prodImg" page="${finalUrl}" title="${productName}" onclick="javascript:s_crossSell('Similar Items (pdp)')">
					<c:choose>
						<c:when test="${empty mediumImage}">
							<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="146" width="146" class="productImage noImageFound fl" alt="image of ${productName}" /> 
						</c:when>
						<c:otherwise>
							<img class="productImage noImageFound fl" height="146" width="146" src="${scene7Path}/${mediumImage}" alt="image of ${productName}" data-src-url="${scene7Path}/${mediumImage}" height="146" width="146"/>
						</c:otherwise>
					</c:choose>
				</dsp:a>
				<ul class="prodInfo grid_3">
				   <c:set var="subStringNumber">
						<bbbc:config key="subStringCharacterCount" configName="ContentCatalogKeys" />
						</c:set>
                   <c:set var="prodName">${productName}</c:set>
				   <c:if test="${fn:length(productName) gt subStringNumber}">
				   		<c:set var="stringSuffix"><bbbl:label key="lbl_product_threeDot" language="${pageContext.request.locale.language}"/></c:set>
						<c:set var="prodName" value="${fn:substring(prodName, 0, subStringNumber)}${stringSuffix}" />
					</c:if>
					<li class="prodName"><dsp:a page="${finalUrl}" title="${productName}" onclick="javascript:s_crossSell('Similar Items (pdp)')">${prodName}</dsp:a></li>
					
					<c:set var="showShipCustomMsg" value="true"/>
					
					<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param param="oosProductVo.attributesList" name="array" />
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
					<c:if test="${ShipMsgDisplayFlag && showShipCustomMsg}">
						<li><dsp:valueof param="oosProductVo.displayShipMsg" valueishtml="true" /></li>
					</c:if>
					<c:if test="${BazaarVoiceOn}">
					<dsp:getvalueof var="ratingAvailable" param="oosProductVo.bvReviews.ratingAvailable"></dsp:getvalueof>
					<dsp:getvalueof param="oosProductVo.bvReviews.ratingsTitle" var="ratingsTitle"/>
					<c:choose>
					<c:when test="${ratingAvailable == true}">
						<dsp:getvalueof var="fltValue" param="oosProductVo.bvReviews.averageOverallRating"/>
						<dsp:getvalueof param="oosProductVo.bvReviews.totalReviewCount" var="totalReviewCount"/>
						<c:choose>
							<c:when test="${totalReviewCount == 1}">
								<%-- <dsp:a page="${finalUrl}"> --%>
								<li class="clearfix metaFeedback"><span title="${fltValue}" class="ratingTxt prodReview prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel"><dsp:valueof param="oosProductVo.bvReviews.ratingsTitle"/></span></span><span class="reviewTxt">(<dsp:valueof param="oosProductVo.bvReviews.totalReviewCount"/><bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" />)</span></li>
								<%-- </dsp:a> --%>
							</c:when>
							<c:otherwise>
								<%-- <dsp:a page="${finalUrl}"> --%>
								<li class="clearfix metaFeedback"><span title="${fltValue}" class="ratingTxt prodReview prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel"><dsp:valueof param="oosProductVo.bvReviews.ratingsTitle"/></span></span><span class="reviewTxt">(<dsp:valueof param="oosProductVo.bvReviews.totalReviewCount"/><bbbl:label key='lbl_reviews_count' language="${pageContext.request.locale.language}" />)</span></li>
								<%-- </dsp:a> --%>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<c:set var="writeReviewLink"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></c:set>
						<li class="metaFeedback"><span class="ratingTxt prodReview"><span class="ariaLabel"><dsp:valueof param="oosProductVo.bvReviews.ratingsTitle"/></span> </span><span class="writeReview reviewTxt"><a href="${pageContext.request.contextPath}${finalUrl}?writeReview=true"  title="${writeReviewLink}" role="link" aria-label="${writeReviewLink} ${lblAboutThe} ${productName}"><bbbl:label key='lbl_grid_write_review_link' language="${pageContext.request.locale.language}" /></a></span></li>
					</c:otherwise>
					</c:choose>
					</c:if>
					<dsp:getvalueof var="salePriceRangeDescription" param="oosProductVo.salePriceRangeDescription" />
					 <dsp:getvalueof var="oosProductListPrice" param="oosProductVo.priceRangeDescription"/>
					 <c:set var= "priceIsTBD"><bbbl:label key='lbl_price_is_tbd' language="${pageContext.request.locale.language}" /></c:set>
					<li class="prodPrice">
					<dsp:getvalueof var="isdynamicPriceProd" param="oosProductVo.dynamicPricingProduct" />
					<dsp:getvalueof var="priceLabelCodeProd" param="oosProductVo.priceLabelCode" />
					<dsp:getvalueof var="inCartFlagProd" param="oosProductVo.inCartFlag" />
					<dsp:getvalueof var="priceRangeDescription" param="oosProductVo.priceRangeDescription" />
					<dsp:include page="browse_price_frag.jsp">
					    <dsp:param name="priceLabelCode" value="${priceLabelCodeProd}" />
						<dsp:param name="inCartFlag" value="${inCartFlagProd}" />
						<dsp:param name="salePrice" value="${salePriceRangeDescription}" />
						<dsp:param name="listPrice" value="${priceRangeDescription}" />
						<dsp:param name="isdynamicPriceEligible" value="${isdynamicPriceProd}" />
					</dsp:include>   		
					</li>
				</ul>

			</div>
			</li>
			</ul>
</dsp:page>