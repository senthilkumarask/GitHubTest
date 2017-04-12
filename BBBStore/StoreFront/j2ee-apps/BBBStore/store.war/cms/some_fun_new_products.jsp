<dsp:page>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
<dsp:getvalueof var="isInternationalCustomer" bean="SessionBean.internationalShippingContext" />
<c:set var="flyoutCacheTimeout">
    <bbbc:config key="FlyoutCacheTimeout" configName="HTMLCacheKeys" />
</c:set>
<c:set var="ShipMsgDisplayFlag" scope="request"><bbbc:config key="ShipMsgDisplayFlag" configName="FlagDrivenFunctions"/></c:set>
<c:set var="cert_funNewProductMax" scope="request"><bbbc:config key="HPFunNewProdMax" configName="CertonaKeys" /></c:set>
	<c:set var="shippingAttributesList">
		<bbbc:config key="shippingAttributesList" configName="ContentCatalogKeys" />
	</c:set>
<c:set var="lblAboutThe"><bbbl:label key="lbl_accessibility_about_the" language="${pageContext.request.locale.language}" /></c:set>
<dsp:droplet name="/atg/dynamo/droplet/Cache">
    <dsp:param name="key" value="${currentSiteId}_${defaultUserCountryCode}_HomePageProducts" />
    <dsp:param name="cacheCheckSeconds" value="${flyoutCacheTimeout}"/>
    <dsp:oparam name="output">
	<dsp:importbean bean="/com/bbb/cms/droplet/HomePageProductsDroplet" />
	 <dsp:droplet name="HomePageProductsDroplet">
		<dsp:param name="siteId" value="${currentSiteId}" />
		<dsp:oparam name="output">
	<div id="someFunNewProducts" class="grid_12">
	    <bbbl:label key="lbl_homePage_newProducts" language="<c:out param='${pageContext.request.locale.language}'/>"/>
	    <div class="someFunNewProductsList">
	        <div class="grid_6 alpha">
				<dsp:getvalueof var="isFeaturedProductIntlRestricted" param="homePageTemplateVO.featuredProduct.intlRestricted" />
	            <div class="productContent">
					<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
						<dsp:param name="id" param="homePageTemplateVO.featuredProduct.productId" />
						<dsp:param name="itemDescriptorName" value="product" />
						<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
						</dsp:oparam>
					</dsp:droplet>


	                <dsp:a iclass="prodImg" href="${contextPath}${finalUrl}">
	                	<dsp:getvalueof var="imgSrc" param="homePageTemplateVO.featuredProduct.productImages.largeImage" />
						<dsp:getvalueof var="imageAltText" param="homePageTemplateVO.featuredProduct.name" />
						<c:choose>
							<c:when test="${empty imgSrc}">
								<img class="productImage" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="image of ${imageAltText}"  height="478" width="478"/>
							</c:when>
							<c:otherwise>
								<img  class="productImage loadingGIF lazyLoad" data-lazyloadsrc="${scene7Path}/${imgSrc}" src="" alt="image of ${imageAltText}" height="478" width="478" />
							</c:otherwise>
						</c:choose>
	                </dsp:a>

	                <ul class="prodInfo">
						<dsp:getvalueof param="homePageTemplateVO.featuredProduct.name" var="prodName"/>
	                    <li class="prodName">
						<dsp:a page="${finalUrl}" title="${prodName}"><c:out value="${prodName}" escapeXml="false"/> </dsp:a>
						</li>
	                    <li class="prodPrice">
	                    <dsp:getvalueof var="salePriceDescrip" param="homePageTemplateVO.featuredProduct.salePriceRangeDescription"/>
						<c:choose>
							<c:when test="${not empty salePriceDescrip}">
									<div class="isPrice">
										<span class="highlightRed">
											<dsp:getvalueof
												param="homePageTemplateVO.featuredProduct.salePriceRangeDescription" var="salePriceRangeDescription"/>
											<dsp:valueof value="${salePriceRangeDescription}" converter="currency"/>
										</span>
									</div>
									<div class="wasPrice">
										<span class="was"><bbbl:label key='lbl_was_text' language="${pageContext.request.locale.language}" /></span>
											<dsp:getvalueof	param="homePageTemplateVO.featuredProduct.priceRangeDescription" var="priceRangeDescription"/>
											<dsp:valueof value="${priceRangeDescription}" converter="currency"/>
									</div>
							</c:when>
							<c:otherwise>
							<dsp:getvalueof	param="homePageTemplateVO.featuredProduct.priceRangeDescription" var="priceRangeDescription"/>
								<dsp:valueof value="${priceRangeDescription}" converter="currency"/>
							</c:otherwise>
						</c:choose>
						</li>
						<li> <dsp:valueof param="productVO.displayShipMsg" valueishtml="true" /></li>
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param param="homePageTemplateVO.featuredProduct.attributesList" name="array" />
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
												 <%-- <dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/>--%>
												<dsp:getvalueof	param="attributeVO.attributeDescrip" var="attributeDescrip"/>
								                <c:out value="${attributeDescrip}" escapeXml="false"/>
											</li>
											</dsp:oparam>
										</dsp:droplet>
									</c:if>
								</dsp:oparam>
							</dsp:droplet>

	                    <c:if test="${BazaarVoiceOn}">
						<dsp:getvalueof var="ratingAvailable" param="homePageTemplateVO.featuredProduct.bvReviews.ratingAvailable"></dsp:getvalueof>
							<c:choose>
							<c:when test="${ratingAvailable == true}">
								<dsp:getvalueof var="fltValue" param="homePageTemplateVO.featuredProduct.bvReviews.averageOverallRating"/>
								<dsp:getvalueof param="homePageTemplateVO.featuredProduct.bvReviews.averageOverallRating" var="averageOverallRating"/>
								<dsp:getvalueof param="homePageTemplateVO.featuredProduct.bvReviews.totalReviewCount" var="totalReviewCount"/>
								<dsp:getvalueof var="fltValue" param="element.bvReviews.averageOverallRating"/>
								<dsp:getvalueof param="element.bvReviews.averageOverallRating" var="averageOverallRating"/>
								<dsp:getvalueof param="element.bvReviews.totalReviewCount" var="totalReviewCount"/>
								<dsp:getvalueof param="element.bvReviews.ratingsTitle" var="ratingsTitle"/>
								<c:choose>
									<c:when test="${totalReviewCount == 1}">
										<li class="clearfix metaFeedback"><span title="${averageOverallRating}" class="ratingTxt prodReview prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel">${ratingsTitle}</span></span><span class="reviewTxt">(${totalReviewCount} <bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" />)</span></li>
									</c:when>
									<c:otherwise>
										<li class="clearfix metaFeedback"><span title="${averageOverallRating}" class="ratingTxt prodReview prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel">${ratingsTitle}</span></span><span class="reviewTxt">(${totalReviewCount} <bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" />)</span></li>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
									<c:set var="writeReviewLink"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></c:set>
									<li class="metaFeedback"><span class="ratingTxt prodReview"><span class="ariaLabel"><dsp:valueof param="productVO.bvReviews.ratingsTitle"/></span> </span><span class="writeReview reviewTxt"><dsp:a page="${finalUrl}?writeReview=true"  title="${writeReviewLink}"><bbbl:label key='lbl_grid_write_review_link' language="${pageContext.request.locale.language}" /></dsp:a></span></li>

								</c:otherwise>
							</c:choose>
							</c:if>
	 						<c:if test="${isInternationalCustomer && isFeaturedProductIntlRestricted}">
		                		<div class="notAvailableIntShipMsg">
		                			<bbbl:label key="lbl_plp_intl_restrict_list_msg" language="${pageContext.request.locale.language}" />
		                		</div>
	                		</c:if>
	                </ul>
	            </div>
	        </div>
	        <div class="grid_6 omega">
				<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="homePageTemplateVO.secondaryProducts" />
						<dsp:oparam name="output">

							<dsp:getvalueof id="count" param="count"/>
							<c:set var="custClass" value="grid_2" />
							<c:if test="${count % 3 == 1}">
								<c:set var="custClass" value="grid_2 alpha" />
							</c:if>
							<c:if test="${count % 3 == 0}">
								<c:set var="custClass" value="grid_2 omega" />
							</c:if>
							<dsp:getvalueof var="isSecondaryProdcutIntlRestricted" param="element.intlRestricted"/>
							<c:if test="${count < cert_funNewProductMax}">
							<div class="homeProd <c:out value="${custClass}"/>">
				                <div class="productContent">
									<dsp:getvalueof var="imageAltText" param="element.name" />
									<dsp:getvalueof var="imgSrc" param="element.productImages.mediumImage" />
									<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
										<dsp:param name="id" param="element.productId" />
										<dsp:param name="itemDescriptorName" value="product" />
										<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
										</dsp:oparam>
									</dsp:droplet>

									<a class="prodImg" href="${contextPath}${finalUrl}"  title="${imageAltText}">
										<c:choose>
											<c:when test="${empty imgSrc}">
												<img class="productImage" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="image of ${imageAltText}" height="146" width="146" />
											</c:when>
											<c:otherwise>
			                    				<img class="productImage loadingGIF lazyLoad" data-lazyloadsrc="${scene7Path}/${imgSrc}" src="" alt="image of ${imageAltText}" height="146" width="146" />
											</c:otherwise>
										</c:choose>
									</a>

				                    <ul class="prodInfo">
									<dsp:getvalueof param="element.name" var="prodName"/>
				                        <li class="prodName">
												<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
													<dsp:param name="id" param="element.productId" />
													<dsp:param name="itemDescriptorName" value="product" />
													<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
													<dsp:oparam name="output">
														<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
														<dsp:a page="${finalUrl}" title="${prodName}"><c:out value="${prodName}" escapeXml="false"/> </dsp:a>
													</dsp:oparam>
												</dsp:droplet>

											</li>

				                         <li class="prodPrice">
						                    <dsp:getvalueof var="salePriceDescrip" param="element.salePriceRangeDescription"/>
											<c:choose>
												<c:when test="${not empty salePriceDescrip}">
														<div class="isPrice">
															<span class="highlightRed">
																<dsp:getvalueof param="element.salePriceRangeDescription" var="salePriceRangeDescription"/>
																<dsp:valueof value="${salePriceRangeDescription}" converter="currency"/>
															</span>
														</div>
														<div class="wasPrice">
															<span class="was"><bbbl:label key='lbl_was_text' language="${pageContext.request.locale.language}" /></span>
																<dsp:getvalueof	param="element.priceRangeDescription" var="priceRangeDescription"/>
																<dsp:valueof value="${priceRangeDescription}" converter="currency"/>
														</div>
												</c:when>
												<c:otherwise>
													<dsp:getvalueof	param="element.priceRangeDescription" var="priceRangeDescription"/>
													<dsp:valueof value="${priceRangeDescription}" converter="currency"/>
												</c:otherwise>
											</c:choose>
											</li>
											
											
										<c:set var="showShipCustomMsg" value="true"/>
				                        <dsp:droplet name="/atg/dynamo/droplet/ForEach">
											<dsp:param param="element.attributesList" name="array" />
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
											<li> <dsp:valueof param="productVO.displayShipMsg" valueishtml="true" /></li>
										</c:if>
				                       <c:if test="${BazaarVoiceOn}">
										<dsp:getvalueof var="ratingAvailable" param="element.bvReviews.ratingAvailable"></dsp:getvalueof>
											<c:choose>
											<c:when test="${ratingAvailable == true}">
												<dsp:getvalueof var="fltValue" param="element.bvReviews.averageOverallRating"/>
												<dsp:getvalueof param="element.bvReviews.averageOverallRating" var="averageOverallRating"/>
												<dsp:getvalueof param="element.bvReviews.totalReviewCount" var="totalReviewCount"/>
												<dsp:getvalueof param="element.bvReviews.ratingsTitle" var="ratingsTitle"/>
												<c:choose>
													<c:when test="${totalReviewCount == 1}">
													 <li class="clearfix metaFeedback"><span title="${averageOverallRating}" class="ratingTxt prodReview prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel">${ratingsTitle}</span></span><span class="reviewTxt">(${totalReviewCount} <bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" />)</span></li>
													</c:when>
													<c:otherwise>
													<li class="clearfix metaFeedback"><span title="${averageOverallRating}" class="ratingTxt prodReview prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel">${ratingsTitle}</span></span><span class="reviewTxt">(${totalReviewCount} <bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" />)</span></li>
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:otherwise>
												<c:set var="writeReviewLink"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></c:set>
												<li class="metaFeedback"><span class="ratingTxt prodReview"><span class="ariaLabel">${ratingsTitle}</span> </span><span class="writeReview reviewTxt"><a href="${finalUrl}?writeReview=true"  title="${writeReviewLink}" role="link" aria-label="${writeReviewLink} ${lblAboutThe} ${prodName}"><bbbl:label key='lbl_grid_write_review_link' language="${pageContext.request.locale.language}" /></a></span></li>


											</c:otherwise>
											</c:choose>
										</c:if>
										<c:if test="${isInternationalCustomer && isSecondaryProdcutIntlRestricted}">
	                						<div class="notAvailableIntShipMsg">
	                							<bbbl:label key="lbl_plp_intl_restrict_list_msg" language="${pageContext.request.locale.language}" />
	                						</div>
                						</c:if>
				                    </ul>
				                </div>
							</div>
						</c:if>
						</dsp:oparam>
					</dsp:droplet>

	        </div>
	        <div class="clear"></div>
	    </div>
	</div>
	</dsp:oparam>
	</dsp:droplet>
</dsp:oparam>
</dsp:droplet>
</dsp:page>