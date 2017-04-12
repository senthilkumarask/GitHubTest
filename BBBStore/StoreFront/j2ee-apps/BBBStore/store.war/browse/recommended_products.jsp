<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
	<dsp:importbean bean="/com/bbb/certona/CertonaConfig" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:getvalueof var="isInternationalCustomer"
		bean="SessionBean.internationalShippingContext" />

	<dsp:getvalueof var="crossSellFlag" param="crossSellFlag" />
	<dsp:getvalueof var="key" param="key" />
	<dsp:getvalueof var="productVOSize" param="productVOSize" />
	<dsp:getvalueof var="showRecommendations" param="showRecommendations" />
	<dsp:getvalueof var="desc" param="desc" />
	<dsp:getvalueof var="fromAccColl" param="true" />
	<dsp:getvalueof var="kickstarterItems" param="kickstarterItems" />
	<dsp:getvalueof var="registryName" param="registryName" />
	<dsp:getvalueof var="registryId" param="registryId" />
	<dsp:getvalueof var="valueMap" bean="SessionBean.values" />	
	
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${appid}" />
	
	<c:set var="countryCodeLowerCase">${fn:toLowerCase(valueMap.defaultUserCountryCode)}</c:set>
	<c:set var="grid_class">grid_2 product alpha registryDataItemsWrap listDataItemsWrap</c:set>
	<c:if test="${fromAccColl}">
	<c:set var="grid_class">clearfix alpha</c:set>
	</c:if>
	 
	<c:set var="lblAboutThe">
		<bbbl:label key="lbl_accessibility_about_the"
			language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="lblForThe"><bbbl:label key="lbl_accessibility_for_the" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="scene7Path" scope="request">
			<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set> 
	<c:set var="ShipMsgDisplayFlag" scope="request">
		<bbbc:config key="ShipMsgDisplayFlag" configName="FlagDrivenFunctions" />
	</c:set>
	<c:set var="shippingAttributesList">
		<bbbc:config key="shippingAttributesList"
			configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BedBathUSSite">
		<bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BuyBuyBabySite">
		<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BedBathCanadaSite">
		<bbbc:config key="BedBathCanadaSiteCode"
			configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="disableLazyLoadS7Images">
		<bbbc:config key="disableLazyLoadS7ImagesFlag"
			configName="FlagDrivenFunctions" />
	</c:set>

	<c:set var="prodImageAttrib">class="productImage noImageFound" src</c:set>

	<c:set var="onClickEvent" value="" />
	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
		<dsp:param name="array" param="productsVOsList" />
		<dsp:oparam name="outputStart">
			<div class="carousel clearfix">

				<div class="carouselBody">
					<c:if test="${(productVOSize> 4 && !showRecommendations) || (showRecommendations && productVOSize> 2)}">
					<div
						class="grid_1 carouselArrow omega carouselArrowPrevious clearfix">
						&nbsp; <a class="carouselScrollPrevious" role="button"
							title="Previous" href="#">
							<span class='hidden' aria-hidden='false'>Previous</span><span
							class="addCartNext icon icon-chevron-left" aria-hidden='true'></span> </a>
					</div>
					</c:if>

					<div class="carouselContent grid_10 clearfix carouselDataWidth">
					<div class="caroufredsel_wrapper"> 

						<ul class="prodGridRow clearfix">
		</dsp:oparam>
		<dsp:oparam name="output">
			<dsp:getvalueof id="itemCount" param="count" />
			<c:if
				test="${(not empty disableLazyLoadS7Images and not disableLazyLoadS7Images) || itemCount > 5}">
				<c:set var="prodImageAttrib">class="productImage lazyLoad loadingGIF" src="${imagePath}/_assets/global/images/blank.gif" data-lazyloadsrc</c:set>
			</c:if>

			<li class="${grid_class}">
				<div class="productShadow"></div>
				<div class="productContent">
					<dsp:getvalueof var="childSKU" param="element.childSKUs" />
					<dsp:getvalueof var="isLtlProduct" param="element.ltlProduct" />
					<dsp:getvalueof var="personalizedSku" param="element.personalizedSku" />
					<dsp:getvalueof var="defaultSKUVO" param="element.defaultSkuDetailVO" />
					<c:set var="isMultiSku" value="false" />
					<c:if test="${fn:length(childSKU) > 1}">
						<c:set var="isMultiSku" value="true" />
					</c:if>
					<dsp:getvalueof var="productId" param="element.productId" />
					<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
						<dsp:param name="id" param="element.productId" />
						<dsp:param name="itemDescriptorName" value="product" />
						<dsp:param name="repositoryName"
							value="/atg/commerce/catalog/ProductCatalog" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
								param="url" />
						</dsp:oparam>
					</dsp:droplet>

					<dsp:getvalueof var="mediumImage"
						param="element.productImages.mediumImage" />
					<c:set var="productName">
						<dsp:valueof param="element.name" valueishtml="true" />
					</c:set>
					<dsp:getvalueof var="isIntlRestricted"
						param="element.intlRestricted" />
					<c:choose>
						<c:when
							test="${(crossSellFlag ne null) && (crossSellFlag eq 'true')}">
							<c:set var="onClickEvent">javascript:pdpCrossSellProxy('crossSell', '${desc}')</c:set>
						</c:when>
						<c:otherwise>
							<c:set var="onClickEvent" value="" />
						</c:otherwise>
					</c:choose>

					<dsp:a iclass="prodImg" page="${finalUrl}" title='${productName}'
						onclick="${onClickEvent}">
						<c:choose>
							<c:when test="${empty mediumImage}">
								<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="146" width="146" alt="image of ${productName}" />
							</c:when>
							<c:otherwise>
								<img ${prodImageAttrib}="${scene7Path}/${mediumImage}" height="146"	width="146" alt="image of ${productName}"<c:if test="${itemCount > 5}"> style="display: none;" </c:if> />
							</c:otherwise>
						</c:choose>
						<noscript>
							<c:choose>
								<c:when test="${empty mediumImage}">
									<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="146" width="146" alt="image of ${productName}" />
								</c:when>
								<c:otherwise>
									<img src="${scene7Path}/${mediumImage}" height="146" width="146" alt="image of ${productName}" class="noImageFound" />
								</c:otherwise>
							</c:choose>
						</noscript>
					</dsp:a>
					<ul class="prodInfo">
						<li class="prodName"><dsp:a page="${finalUrl}"
								title="${productName}" onclick="${onClickEvent}">${productName}</dsp:a></li>
						<dsp:getvalueof var="salePriceRangeDescription"
							param="element.salePriceRangeDescription" />

						<%--  BBB AJAX 2.3.1  Was-Is price change on PLP,Search,Brand ends--%>
						<c:set var="showShipCustomMsg" value="true" />
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param param="element.attributesList" name="array" />
							<dsp:param name="elementName" value="attributeVOList" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="placeholder" param="key" />
								<c:if test="${placeholder eq 'CRSL'}">
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
										<dsp:param param="attributeVOList" name="array" />
										<dsp:param name="elementName" value="attributeVO" />
										<dsp:param name="sortProperties" value="+priority" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="attrId"
												param="attributeVO.attributeName" />
											<c:if test="${fn:contains(shippingAttributesList,attrId)}">
												<c:set var="showShipCustomMsg" value="false" />
											</c:if>
										</dsp:oparam>
									</dsp:droplet>
								</c:if>
							</dsp:oparam>
						</dsp:droplet>

						<c:choose>
							<c:when test="${currentSiteId eq BedBathUSSite}">
								<c:set var="BazaarVoiceOn" scope="request">
									<tpsw:switch tagName="BazaarVoiceTag_us" />
								</c:set>
							</c:when>
							<c:when test="${currentSiteId eq BuyBuyBabySite}">
								<c:set var="BazaarVoiceOn" scope="request">
									<tpsw:switch tagName="BazaarVoiceTag_baby" />
								</c:set>
							</c:when>
							<c:otherwise>
								<c:set var="BazaarVoiceOn" scope="request">
									<tpsw:switch tagName="BazaarVoiceTag_ca" />
								</c:set>
							</c:otherwise>
						</c:choose>
						<c:if test="${BazaarVoiceOn}">
							<dsp:getvalueof var="ratingAvailable"
								param="element.bvReviews.ratingAvailable"></dsp:getvalueof>
							<c:choose>
								<c:when test="${ratingAvailable == true}">
									<dsp:getvalueof var="fltValue"
										param="element.bvReviews.averageOverallRating" />
									<dsp:getvalueof param="element.bvReviews.totalReviewCount"
										var="totalReviewCount" />
									<c:choose>
										<c:when test="${totalReviewCount == 1}">
											<li class="clearfix metaFeedback"><span
												title="${fltValue}"
												class="ratingTxt prodReview prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span
													class="ariaLabel"><dsp:valueof
															param="element.bvReviews.ratingsTitle" /></span></span>
														<c:set var="totalReviewCount">
															<dsp:valueof
														param="element.bvReviews.totalReviewCount" />
														</c:set>
														<c:set var="lblReviewsCount">
															<bbbl:label
														key='lbl_review_count'
														language="${pageContext.request.locale.language}" />
														</c:set>
															<span
												class="reviewTxt">
												(<a
											href="${pageContext.request.contextPath}${finalUrl}?showRatings=true"
											title="${totalReviewCount} ${lblReviewsCount}" role="link"
											aria-label="${totalReviewCount} ${lblReviewsCount} ${lblForThe} ${productName}">${totalReviewCount} ${lblReviewsCount}</a>)
											</span></li>
											
										</c:when>
										<c:otherwise>
											<li class="clearfix metaFeedback"><span
												title="${fltValue}"
												class="ratingTxt prodReview prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span
													class="ariaLabel"><dsp:valueof
															param="element.bvReviews.ratingsTitle" /></span></span>
															<c:set var="totalReviewCount">
																<dsp:valueof
															param="element.bvReviews.totalReviewCount" />
															</c:set>
															<c:set var="lblReviewsCount">
																<bbbl:label
															key='lbl_reviews_count'
															language="${pageContext.request.locale.language}" />
															</c:set>
															<span
												class="reviewTxt">(<a
											href="${pageContext.request.contextPath}${finalUrl}?showRatings=true"
											title="${totalReviewCount} ${lblReviewsCount}" role="link"
											aria-label="${totalReviewCount} ${lblReviewsCount} ${lblForThe} ${productName}">${totalReviewCount} ${lblReviewsCount}</a>)
											</span></li>
										</c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise>
									<c:set var="writeReviewLink">
										<bbbl:label key="lbl_grid_write_review_link"
											language="${pageContext.request.locale.language}" />
									</c:set>
									<li class="metaFeedback"><span
										class="ratingTxt prodReview"><span class="ariaLabel"><dsp:valueof
													param="element.bvReviews.ratingsTitle" /></span> </span><span
										class="writeReview reviewTxt"><a
											href="${pageContext.request.contextPath}${finalUrl}?writeReview=true"
											title="${writeReviewLink}" role="link"
											aria-label="${writeReviewLink} ${lblAboutThe} ${productName}"><bbbl:label
													key='lbl_grid_write_review_link'
													language="${pageContext.request.locale.language}" /></a></span></li>
								</c:otherwise>
							</c:choose>
						</c:if>
					
						<%--  BBB AJAX 2.3.1  Was-Is price change on PLP,Search,Brand starts--%>
						<li class="prodPrice">
							<dsp:getvalueof var="prodListPrice"	param="element.priceRangeDescription" /> 
							<c:set	var="priceIsTBD"><bbbl:label key='lbl_price_is_tbd'	language="${pageContext.request.locale.language}" />
							</c:set> 
							<c:choose>
								<c:when test="${empty defaultSKUVO}">
								  <c:choose>
									<c:when test="${kickstarterItems}">
										<c:choose>
											<c:when test="${countryCodeLowerCase eq 'mx'}">
												<dsp:getvalueof var="isdynamicPrice"
													param="element.bbbProduct.dynamicPriceVO.dynamicProdEligible" />
												<dsp:getvalueof var="priceLabelCode"
													param="element.bbbProduct.dynamicPriceVO.mxPricingLabelCode" />
												<dsp:getvalueof var="inCartFlag"
													param="element.bbbProduct.dynamicPriceVO.mxIncartFlag" />
												<dsp:getvalueof var="wasPriceRange"
													param="element.bbbProduct.wasPriceRangeMX" />
												<dsp:getvalueof var="isPriceRange"
													param="element.bbbProduct.priceRangeMX" />
												<c:if test="${empty wasPriceRange}">
													<c:set var="wasPriceRange" value="${isPriceRange}" />
													<c:set var="isPriceRange" value="" />
												</c:if>
												<dsp:include page="browse_price_frag.jsp">
													<dsp:param name="priceLabelCode" value="${priceLabelCode}" />
													<dsp:param name="inCartFlag" value="${inCartFlag}" />
													<dsp:param name="salePrice" value="${isPriceRange}" />
													<dsp:param name="listPrice" value="${wasPriceRange}" />
													<dsp:param name="isdynamicPriceEligible"
														value="${isdynamicPrice}" />
												</dsp:include>
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${currentSiteId eq BedBathUSSite}">
														<dsp:getvalueof var="isdynamicPrice"
															param="element.bbbProduct.dynamicPriceVO.dynamicProdEligible" />
														<dsp:getvalueof var="priceLabelCode"
															param="element.bbbProduct.dynamicPriceVO.bbbPricingLabelCode" />
														<dsp:getvalueof var="inCartFlag"
															param="element.bbbProduct.dynamicPriceVO.bbbIncartFlag" />
														<dsp:getvalueof var="wasPriceRange"
															param="element.bbbProduct.wasPriceRange" />
														<dsp:getvalueof var="isPriceRange"
															param="element.bbbProduct.priceRange" />
													</c:when>
													<c:when test="${currentSiteId eq BuyBuyBabySite}">
														<dsp:getvalueof var="isdynamicPrice"
															param="element.bbbProduct.dynamicPriceVO.dynamicProdEligible" />
														<dsp:getvalueof var="priceLabelCode"
															param="element.bbbProduct.dynamicPriceVO.babyPricingLabelCode" />
														<dsp:getvalueof var="inCartFlag"
															param="element.bbbProduct.dynamicPriceVO.babyIncartFlag" />
														<dsp:getvalueof var="wasPriceRange"
															param="element.bbbProduct.wasPriceRange" />
														<dsp:getvalueof var="isPriceRange"
															param="element.bbbProduct.priceRange" />
													</c:when>
													<c:when test="${currentSiteId eq BedBathCanadaSite}">
														<dsp:getvalueof var="isdynamicPrice"
															param="element.bbbProduct.dynamicPriceVO.dynamicProdEligible" />
														<dsp:getvalueof var="priceLabelCode"
															param="element.bbbProduct.dynamicPriceVO.caPricingLabelCode" />
														<dsp:getvalueof var="inCartFlag"
															param="element.bbbProduct.dynamicPriceVO.caIncartFlag" />
														<dsp:getvalueof var="wasPriceRange"
															param="element.bbbProduct.wasPriceRange" />
														<dsp:getvalueof var="isPriceRange"
															param="element.bbbProduct.priceRange" />
													</c:when>
												</c:choose>
												<c:if test="${empty wasPriceRange}">
													<c:set var="wasPriceRange" value="${isPriceRange}" />
													<c:set var="isPriceRange" value="" />
												</c:if>
												<dsp:include page="browse_price_frag.jsp">
													<dsp:param name="priceLabelCode" value="${priceLabelCode}" />
													<dsp:param name="inCartFlag" value="${inCartFlag}" />
													<dsp:param name="salePrice" value="${isPriceRange}" />
													<dsp:param name="listPrice" value="${wasPriceRange}" />
													<dsp:param name="isdynamicPriceEligible"
														value="${isdynamicPrice}" />
												</dsp:include>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<dsp:getvalueof var="isdynamicPriceProd"
											param="element.dynamicPricingProduct" />
										<dsp:getvalueof var="priceLabelCodeProd"
											param="element.priceLabelCode" />
										<dsp:getvalueof var="inCartFlagProd"
											param="element.inCartFlag" />
										<dsp:getvalueof var="priceRangeDescription"
											param="element.priceRangeDescription" />
										<dsp:include page="browse_price_frag.jsp">
											<dsp:param name="priceLabelCode" value="${priceLabelCodeProd}" />
											<dsp:param name="inCartFlag" value="${inCartFlagProd}" />
											<dsp:param name="salePrice"
												value="${salePriceRangeDescription}" />
											<dsp:param name="listPrice" value="${priceRangeDescription}" />
											<dsp:param name="isdynamicPriceEligible"
												value="${isdynamicPriceProd}" />
										</dsp:include>
									</c:otherwise>
								</c:choose></li>
								</c:when>
								<c:otherwise> <%-- Sho default SKU price--%>
									<dsp:getvalueof var="priceLabelCodeSku" param="defaultSKUVO.pricingLabelCode" />
									<dsp:getvalueof var="inCartFlagSku" param="defaultSKUVO.inCartFlag" />
									<dsp:include page="product_details_price.jsp">
										<dsp:param name="product" param="productId" />
										<dsp:param name="sku" value="${defaultSKUVO.skuId}" />
										<dsp:param name="isFromPDP" value="${true}" />
										<dsp:param name="priceLabelCodeSKU" value="${priceLabelCodeSku}" />
										<dsp:param name="inCartFlagSKU" value="${inCartFlagSku}" />
									</dsp:include>
									
								</c:otherwise>
						   </c:choose>
					</ul>
					<c:if test="${isInternationalCustomer && isIntlRestricted}">
						<div class="notAvailableIntShipMsg cb clearfix marBottom_10">
							<bbbl:label key="lbl_plp_intl_restrict_list_msg"
								language="${pageContext.request.locale.language}" />
						</div>
					</c:if>
				</div> <dsp:getvalueof var="type" param="type" />
						
						<input type="hidden" class="addItemToList addItemToRegis" name="skuId" value="${defaultSKUVO.skuId}">
						<input type="hidden" class="addItemToList addItemToRegis" name="prodId" value="${productId}">
						
						<input type="hidden" class="addItemToList addItemToRegis" name="qty" value="1">
						<input type="hidden" class="addItemToList addItemToRegis" name="price" value="${finalPrice}">
						<input type="hidden" class="addItemToList addItemToRegis" name="registryName" value="${registryName}">
						<input type="hidden" class="addItemToList addItemToRegis" name="registryId" value="${registryId}">
						<input type="hidden" class="addItemToList addItemToRegis" name="parentProdId" value="${productId}">
						
						<input type="hidden" name="bts" class="addToCartSubmitData" value=""> 
					
					 <c:choose>
					<c:when test="${isMultiSku || isLtlProduct || personalizedSku}">
						<dsp:getvalueof var="collectionFlag" param="element.collection" />
						<c:choose>
							<c:when test="${collectionFlag}">
								<c:set var="quickViewClass" value="showOptionsCollection" />
							</c:when>
							<c:otherwise>
								<c:set var="quickViewClass" value="showOptionMultiSku" />
							</c:otherwise>
						</c:choose>
					    <div class="button button_active button_active_orange ">
					    <input type="submit" class="${quickViewClass} closeOptionModal" name="${quickViewClass}" value="CHOOSE OPTIONS" onclick="rkg_micropixel('${appid}','cart')" role="button">
					    </div>


						


					</c:when>
					<c:when test="${type eq 'cart'}">
						
						<div class="fl addToCart">

							<c:choose>
								<c:when
									test="${(isInternationalCustomer && isIntlRestricted)}">
									<div class="button_disabled">
									    <div class="button button_active button_active_orange ">
									    <input type="submit" name="btnAddToCart" value="add to cart" onclick="rkg_micropixel('${appid}','cart')" role="button" disabled="disabled">
									    </div>
										<script>
											BBB.addPerfMark('ux-primary-action-available');
										</script>
									</div>
								</c:when>
								<c:otherwise>
									<div
										class="mainProductContainer">
										<div class="button button_active button_active_orange showhideButton">
									    <input type="submit" name="btnAddToCart" value="add to cart" onclick="rkg_micropixel('${appid}','cart')" role="button" >
									    </div>
									    <div class="addedToCartText">
										<span class="icon-checkmark" aria-hidden='true'></span>
										<span class="modalHead" aria-hidden='false'><bbbl:label key="lbl_added_to_cart"
											language="${pageContext.request.locale.language}" /></span>
										</div> 
										
										<!-- <input type="submit" name="btnAddToCart" value="ADD TO CART"
											onclick="rkg_micropixel('${appid}','cart')" role="button"
											disabled="disabled"
											class='button-Large btnSecondary button_active button_active_orange<c:if test="${inStock==true}">enableOnDOMReady</c:if>' /> -->

										<script>
											BBB.addPerfMark('ux-primary-action-available');
										</script>
									</div>
								</c:otherwise>
							</c:choose>

						</div>

					</c:when>
					<c:when test="${type eq 'registry'}">
						<dsp:param name="ltlProductFlag" value="${isLtlProduct}" />
						<dsp:include page="../addgiftregistry/add_item_gift_registry.jsp">
						
						</dsp:include>
					</c:when>
				</c:choose>
			</li>
		</dsp:oparam>
		<dsp:oparam name="outputEnd">
			</ul>
			</div>	
			</div>
			<c:if test="${(productVOSize> 4 &&  !showRecommendations) || (showRecommendations && productVOSize> 2)}">
			<div class="grid_1 carouselArrow alpha carouselArrowNext clearfix">
				&nbsp; <a class="carouselScrollNext" title="Next" role="button"
					href="#"><span class='hidden' aria-hidden='false'>Next</span>
					<span class="addCartNext icon icon-chevron-right" aria-hidden='true'></span> </a>
			</div>
			</c:if>

			</div></div>
				
		</dsp:oparam>
	</dsp:droplet>
	<c:choose>
		<c:when test="${appIdCertona eq 'bedbathandbeyond01' || appIdCertona eq 'bedbathandbeyond03' || appIdCertona eq 'bedbathandbeyond05' || appIdCertona eq 'bedbathandbeyond06'}">
		   <c:set var="onPageEvent" value="registrywedding_op"/>
		</c:when>
		<c:otherwise>
			<c:set var="onPageEvent" value="registrybaby_op"/>
		</c:otherwise>
	</c:choose>
	
	<dsp:droplet name="Switch">
	<dsp:param name="value" bean="Profile.transient" />
		<dsp:oparam name="false">
			<dsp:getvalueof var="userId" bean="Profile.id" />
		</dsp:oparam>
		<dsp:oparam name="true">
			<dsp:getvalueof var="userId" value="" />
		</dsp:oparam>
	</dsp:droplet>
	
	<script type="text/javascript">			  
	   function certonaAddRegModal(json) {
			var cfProdId = '';
			for (var i in json.addItemResults) {
			 cfProdId += json.addItemResults[i].prodId + ';';
			}
			resx.event = "${onPageEvent}"; 
			resx.itemid = cfProdId; 
			resx.customerid = "${userId}";
			resx.pageid = ""; 
			resx.links = ""; 
			if (typeof certonaResx === 'object') {
				certonaResx.run();
			} else if (typeof certonaResx === "undefined") {
				if (typeof BBB.loadCertonaJS === "function") {
					BBB.loadCertonaJS();
				}
			}
		}
	</script>
</dsp:page>