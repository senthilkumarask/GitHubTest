<dsp:page>

	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:getvalueof var="CategoryId" param="CatalogRefId" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:getvalueof param="browseSearchVO" var="browseSearchVO"/>
	<dsp:getvalueof var="count" param="BBBProductListVO.bbbProductCount"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	
	<%-- R2.2 Story - 178-a Product Comparison tool Changes | Check if compare functionality is enabled --%>
	<c:set var="compareProducts"><bbbc:config key="compareProducts" configName="FlagDrivenFunctions" /></c:set>
	<c:set var="featuredProductMinCount" scope="request"><bbbc:config key="featured_product_display_count" configName="FlagDrivenFunctions" /></c:set>
	<c:set var="promoKeyCenter" scope="request"><bbbl:label key="lbl_promo_key_center" language="${pageContext.request.locale.language}" /></c:set>
    <c:set var="lbl_was_text"><bbbl:label key="lbl_was_text" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblReviewCount"><bbbl:label key="lbl_review_count" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblReviewsCount"><bbbl:label key="lbl_reviews_count" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblOfThe"><bbbl:label key="lbl_accessibility_of_the" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblAboutThe"><bbbl:label key="lbl_accessibility_about_the" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblForThe"><bbbl:label key="lbl_accessibility_for_the" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lbl_compare_product"><bbbl:label key="lbl_compare_product" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblProductQuickView"><bbbl:label key="lbl_product_quick_view" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="prodCount"><bbbc:config key="PageLoadProductCountListView" configName="ContentCatalogKeys" /></c:set>
	
		<%-- Imran Chnage Start --%>	
	<c:set var="WAS"><bbbc:config key="dynamicPriceLabelWas" configName="ContentCatalogKeys" /></c:set>	
	<c:set var="ORIG"><bbbc:config key="dynamicPriceLabelOrig" configName="ContentCatalogKeys" /></c:set>
	
	<c:set var="isDynamicPriceEnabled"><bbbc:config key="FlagDrivenFunctions" configName="DynamicPricing" /></c:set>	
	
	<c:set var="lblDiscountedIncart"><bbbl:label key="lbl_discounted_incart_text" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblWasText"><bbbl:label key='lbl_was_text' language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblOrigText"><bbbl:label key='lbl_orig_text' language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblPVOrigText"><bbbl:label key='lbl_price_variations_orig_text' language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblInCartOrigText"><bbbl:label key="lbl_discount_incart_orig_text" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblPriceTBD"><bbbl:label key="lbl_price_is_tbd" language="${pageContext.request.locale.language}"/></c:set>
	<c:set var="compareCustomizationText">
		<bbbc:config key="CompareCustomizationText" configName="EXIMKeys"/>
	</c:set>
	
    <%-- render normal classes/src attribute if disableLazyLoadS7Images is set to "true" --%>
    <c:if test="${disableLazyLoadS7Images eq true}">
        <c:set var="prodImageAttrib" scope="request">class="productImage noImageFound" src</c:set>
        <c:set var="swatchImageAttrib" scope="request">class="swatchImage noImageFound" src</c:set>
    </c:if>
	<c:set var="globalShipMsgDisplayOn" scope="request"><bbbc:config key="ShipMsgDisplayFlag" configName="FlagDrivenFunctions"/></c:set>
	<ul class="prodListRow clearfix noMar">
	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="BBBProductListVO.bbbProducts" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="productId" param="element.productID" />
			<dsp:getvalueof var="scene7DomainPath" param="element.sceneSevenURL" />
			<dsp:getvalueof var="productName" param="element.productName"/>
			<dsp:getvalueof var="prdShipMsgFlag" param="element.shipMsgFlag"/>
			<dsp:getvalueof var="prdDisplayShipMsg" param="element.displayShipMsg"/>  
			<%-- Check for Swatch Flag attribute returned from Search Engine--%>
			<dsp:getvalueof var="swatchFlag" param="element.swatchFlag"/>
			<dsp:getvalueof var="rollupFlag" param="element.rollupFlag"/>
			<dsp:getvalueof var="collectionFlag" param="element.collectionFlag"/>
			<dsp:getvalueof var="forEachIndex" param="count"/>
			<dsp:getvalueof var="portrait" param="portrait"/>
			<dsp:getvalueof id="productCountDisplayed" param="size" scope="request"/>
			<dsp:getvalueof var="vdcFlag" param="element.vdcFlag"/>
			<dsp:getvalueof var="mswpFlag" param="element.mswpFlag"/>
			<dsp:param name="rollupFlag" param="element.rollupFlag"/>
			<dsp:param name="collectionFlag" param="element.collectionFlag" />
			<c:choose>
			  <c:when test="${forEachIndex > prodCount}">
			     <%-- render lazy-load classes/src attribute by default (used when disableLazyLoadS7Images flag is "false" or not-defined) --%>
			     <c:set var="prodImageAttrib" scope="request">class="productImage lazyLoad loadingGIF" src="${imagePath}/_assets/global/images/blank.gif" data-lazyloadsrc</c:set>
    			 <c:set var="swatchImageAttrib" scope="request">class="swatchImage lazySwatchLoad loadingGIF" src="${imagePath}/_assets/global/images/blank.gif" data-lazyloadsrc</c:set>
			  </c:when>
			  <c:otherwise>
			     <c:set var="prodImageAttrib" scope="request">class="productImage noImageFound" src</c:set>
    			 <c:set var="swatchImageAttrib" scope="request">class="swatchImage noImageFound" src</c:set>
			  </c:otherwise>  
			</c:choose>
			<c:set var="productIdForFeaturedProducts" value="${productId}" scope="request"/>
			<c:set var="liClass" value="" />
			<c:if test="${forEachIndex == 1}"><c:set var="liClass" value=" first" /></c:if>
			
			<li class="product grid_9 ${liClass} registryDataItemsWrap listDataItemsWrap">
				<div class="productShadow"></div>
				<div class="productContent grid_2 alpha ec_listing">
				<dsp:getvalueof var="seoUrl" param="element.seoUrl"/>
					<c:choose>
						<c:when test="${empty seoUrl}">
							<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
								<dsp:param name="id" param="element.productID" />
								<dsp:param name="itemDescriptorName" value="product" />
								<dsp:param name="repositoryName"
									value="/atg/commerce/catalog/ProductCatalog" />
								<dsp:oparam name="output">
								<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
										param="url" />
								</dsp:oparam>
							</dsp:droplet>
						</c:when>
						<c:otherwise>
							<c:set var="finalUrl" value="${seoUrl}"></c:set>
						</c:otherwise>
					</c:choose>
					<dsp:getvalueof var="pdpUrl" value="${finalUrl}" />
					<dsp:getvalueof var="portraitClass" value=""/>
					<dsp:getvalueof var="imgHeight" value="146"/>
					<dsp:getvalueof var="imageURL" param="element.imageURL"/>
					<c:if test="${portrait eq 'true'}">
						<dsp:getvalueof var="portraitClass" value="prodImgPortrait"/>
						<dsp:getvalueof var="imgHeight" value="200"/>
						<dsp:getvalueof var="imageURL" param="element.verticalImageURL"/>
					</c:if>
					<c:choose>
					<c:when test="${frmBrandPage eq true}">
					<dsp:a iclass="prodImg ${portraitClass}" page="${finalUrl}?brandId=${searchTerm}" title="${productName}">
						<%-- Thumbnail image exists OR not--%>
						<c:choose>
							<c:when test="${not empty imageURL}">
								<img class="productImage lazyLoad loadingGIF" ${prodImageAttrib}="${scene7DomainPath}/${imageURL}" height="${imgHeight}" width="146" alt="image of ${productName}" />
							</c:when>
							<c:otherwise>
								<img class="productImage" src='${imagePath}/_assets/global/images/no_image_available.jpg' alt="image of ${productName}" width="146" height="${imgHeight}" />
							</c:otherwise>
						</c:choose>
					</dsp:a>
					</c:when>
					<c:otherwise>
					<dsp:a iclass="prodImg ${portraitClass}" page="${finalUrl}?categoryId=${CategoryId}" title="${productName}">
						<%-- Thumbnail image exists OR not--%>
						<c:choose>
							<c:when test="${not empty imageURL}">
								<img class="productImage lazyLoad loadingGIF" ${prodImageAttrib}="${scene7DomainPath}/${imageURL}" height="${imgHeight}" width="146" alt="image of ${productName}" />
							</c:when>
							<c:otherwise>
								<img class="productImage" src='${imagePath}/_assets/global/images/no_image_available.jpg' alt="image of ${productName}" width="146" height="${imgHeight}" />
							</c:otherwise>
						</c:choose>
					</dsp:a>
					</c:otherwise>
					</c:choose>
					<%-- Start :- R2.2 Story 546. Adding Ribbon for Featured Product --%>
					<%@ include file="/search/featured_product.jsp" %>
						
						<c:choose>
				     	    	<c:when test="${collectionFlag eq 1}">   
				        			<c:set var="quickViewClass" value="showOptionsCollection"/>
				   				</c:when>
								<c:otherwise>
				        			<c:set var="quickViewClass" value="showOptionMultiSku"/>
								</c:otherwise>
						</c:choose>
						<c:choose>
								<c:when test="${rollupFlag eq 'true' || collectionFlag == '1'}">
									<c:set var="isMultiRollUpFlag" value="true" />	
								</c:when>
								<c:otherwise>
									<c:set var="isMultiRollUpFlag" value="false" />
								</c:otherwise>
						</c:choose>
					<%-- End :- R2.2 Story 546. Adding Ribbon for Featured Product --%>
					<div class="textCenter includeCompare">
					 <%-- Adding Qucik View Link : Story : 716_PLP list view re-design |Sprint1(2.2.1)| [Start]  --%>
					<div class="padTop_10 padBottom_5">
						<span tabindex="0" class="quickView ${quickViewClass}" role="link" aria-label="${lblProductQuickView} ${lblOfThe} ${productName}"><bbbl:label key="lbl_product_quick_view" language="${pageContext.request.locale.language}" /></span>
						
						<%-- Data to submit for Add to Cart / Find In Store --%>
						<input type="hidden" class="addItemToList addItemToRegis itemQuantity" value="1" name="qty" data-change-store-submit="qty" />
						<input type="hidden" data-change-store-internaldata="true" data-change-store-errors="required" data-change-store-submit="prodId" value="${productId}" class="_prodId addItemToRegis productId addItemToList" name="prodId"/>
						<input type="hidden" data-change-store-internaldata="true" data-change-store-errors="required" data-change-store-submit="skuId" class="addItemToRegis _skuId addItemToList changeStoreSkuId" value="${skuID}" name="skuId"/>
						<input type="hidden" name="price" value="${omniPrice}" class="addItemToList addItemToRegis" />
						<input type="hidden" data-change-store-storeid="storeId" value="" name="storeId" class="addToCartSubmitData"/>
						<input type="hidden" name="parentProductId" value="${productId}" class="addItemToList addItemToRegis" />
						<input type="hidden" value="" class="addToCartSubmitData" name="bts" data-change-store-storeid="bts"/>
						<input type="hidden" value="true" class="addItemToList addItemToRegis" name="fromSearch"/>
						<input type="hidden" value="${LiveClickerOn}" name="lcFlag"/>
						<input type="hidden" value="${isMultiRollUpFlag}" class="isMultiRollUpFlag"/>
						<input type="hidden" value="" class="selectedRollUpValue"/>
						<input type="hidden" value="${CategoryId}" class="categoryId"/>
					</div>
					<%-- Adding Qucik View Link : Story : 716_PLP list view re-design |Sprint1(2.2.1)| [End]  --%>
				   <%-- R2.2 Story - 178-a Product Comparison tool Changes : Start --%>	
					<c:if test="${compareProducts eq true}">
						<dsp:getvalueof var="productId" param="element.productId"/>
						<dsp:getvalueof var="inCompareDrawer" param="element.inCompareDrawer"/>
						<c:choose>
							<c:when test="${inCompareDrawer eq true}">
								<input id="compareChkTxt_${productId}" type="checkbox" name ="Compare" class="compareChkTxt" data-productId="${productId}"  checked = "true" aria-label="${lbl_compare_product} ${productName}"/>
								<label class="compareChkTxt" for="compareChkTxt_${productId}"><bbbl:label key="lbl_compare_product" language="${pageContext.request.locale.language}" /></label>
								
							</c:when>
							<c:otherwise>
								<input id="compareChkTxt_${productId}" type="checkbox" name ="Compare" class="compareChkTxt" data-productId="${productId}" aria-label="${lbl_compare_product} ${productName}"/>
									<label class="compareChkTxt" for="compareChkTxt_${productId}"><bbbl:label key="lbl_compare_product" language="${pageContext.request.locale.language}" /></label>
								
							</c:otherwise>
						</c:choose>
					</c:if>
					<%-- R2.2 Story - 178-a Product Comparison tool Changes : End --%>	
				</div>
				</div>
				<script>
		                  $('#compareChkTxt_${productId}').change(function(){
                                  var ariaMsg = this.checked ? '<c:out value='${productName}'/> has been added to your compare list' : 'compare <c:out value='${productName}'/>';
                                  $(this).attr('aria-label',ariaMsg);
                                   });
			         </script>	
					<div class="grid_5">
					<div class="prodInfo">
						<div class="prodName">
							<dsp:getvalueof var="seoUrl" param="element.seoUrl"/>
							<c:choose>
								<c:when test="${empty seoUrl}">
									<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
										<dsp:param name="id" param="element.productID" />
										<dsp:param name="itemDescriptorName" value="product" />
										<dsp:param name="repositoryName"
											value="/atg/commerce/catalog/ProductCatalog" />
										<dsp:oparam name="output">
										<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
												param="url" />
										</dsp:oparam>
									</dsp:droplet>
								</c:when>
								<c:otherwise>
									<c:set var="finalUrl" value="${seoUrl}"></c:set>
								</c:otherwise>
							</c:choose>
							<c:set var="prodName">
								<dsp:valueof param="element.productName" valueishtml="true"/>
							</c:set>
							<c:choose>
								<c:when test="${frmBrandPage eq true}">
								<dsp:a  page="${finalUrl}?brandId=${searchTerm}"  title="${prodName}">
											<dsp:valueof param="element.productName" valueishtml="true" />
										</dsp:a>
								</c:when>
								<c:otherwise>
								<dsp:a  page="${finalUrl}?categoryId=${CategoryId}"  title="${prodName}">
											<dsp:valueof param="element.productName" valueishtml="true" />
										</dsp:a>
								</c:otherwise>
					     </c:choose>
							
						</div>
						
						<c:if test="${BazaarVoiceOn}">
						<dsp:getvalueof var="reviews" param="element.reviews"/>
                        <dsp:getvalueof var="ratingsTitle" param="element.ratingsTitle"/>
						<dsp:getvalueof var="ratings" param="element.ratings" vartype="java.lang.Integer"/>
						<dsp:getvalueof var="rating" param="element.ratingForCSS" vartype="java.lang.Integer"/>
						<c:choose>
							<c:when test="${ratings ne null && ratings ne '0'}">
								<div class="clearfix prodReviews metaFeedback ">									
									<span class="ratingTxt ratingsReviews prodReviews<fmt:formatNumber value="${rating}" pattern="#0" />"> <span class="ariaLabel">${ratingsTitle}</span></span>
									<span class="reviewTxt"><c:if test="${reviews ne null && reviews ne '0' && reviews gt '1'}">
									   <a href="${contextPath}${pdpUrl}?showRatings=true" title="${productName}" role="link" aria-label="${reviews} ${lblReviewsCount} ${lblForThe} ${productName}">
									    	<dsp:valueof param="element.reviews"/><bbbl:label key="lbl_reviews_count" language="${pageContext.request.locale.language}" />
										</a>
									</c:if>
									<c:if test="${reviews ne null && reviews ne '0' && reviews eq '1'}">
									   <a href="${contextPath}${pdpUrl}?showRatings=true" title="${productName}" role="link" aria-label="${reviews} ${lblReviewCount} ${lblForThe} ${productName}">
										 <dsp:valueof param="element.reviews"/><bbbl:label key="lbl_review_count" language="${pageContext.request.locale.language}" />
									   </a>
									</c:if></span>
								</div>
							</c:when>
							<c:otherwise>
								<div class="prodReviews metaFeedback">
									<dsp:getvalueof var="seoUrl" param="element.seoUrl"/>
									<c:choose>
										<c:when test="${empty seoUrl}">
											<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
												<dsp:param name="id" param="element.productID" />
												<dsp:param name="itemDescriptorName" value="product" />
												<dsp:param name="repositoryName"
													value="/atg/commerce/catalog/ProductCatalog" />
												<dsp:oparam name="output">
												<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
														param="url" />
												</dsp:oparam>
											</dsp:droplet>
										</c:when>
										<c:otherwise>
											<c:set var="finalUrl" value="${seoUrl}"></c:set>
										</c:otherwise>
									</c:choose>
									<c:set var="writeReviewLink"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></c:set>
									
									<span class="ratingTxt ratingsReviews"><span class="ariaLabel">${ratingsTitle}</span></span><span class="reviewTxt writeReview">
														<a href="${pageContext.request.contextPath}${finalUrl}?categoryId=${CategoryId}&writeReview=true" title="${writeReviewLink}"  role="link" aria-label="${writeReviewLink} ${lblAboutThe} ${productName}">${writeReviewLink}</a>
									</span>
								</div>
							</c:otherwise>
						</c:choose>
						</c:if>
					<div class="prodDesc marBottom_10">
						<div class="width_5 clearfix wrap">
							<div class="descWrap">
							<dsp:valueof param="element.description" valueishtml="true"/>
							</div>
						</div>
						<%-- Adding View More Link : Story : 716_PLP list view re-design |Sprint1(2.2.1)| [Start]  --%>
					   	<dsp:a page="${finalUrl}?categoryId=${CategoryId}#prodExplore" title="${productName}" iclass="viewMore hidden">
					       <bbbl:label key="lbl_product_list_view_more" language="${pageContext.request.locale.language}" />
					    </dsp:a>
					 	<%-- Adding View More Link : Story : 716_PLP list view re-design |Sprint1(2.2.1)| [End]  --%>
					</div>
					<c:if test="${swatchFlag == '1'}">
					<div class="prodSwatchListItem">	<div class="prodSwatchesContainer clearfix">
							<div class="colorPicker fl marTop_10">
							 <label><bbbl:label key="lbl_colors_dropdown" language ="${pageContext.request.locale.language}"/></label>
							<span id="radioSel_Color" class="txtOffScreen radioTxtSelectLabel" aria-live="assertive"> </span>
							<ul class="swatches prodSwatches fl hideSwatchRows">
								<dsp:droplet name="ForEach">
									<dsp:param name="array" param="element.colorSet" />
									<dsp:getvalueof var="size" param="size" />
									<dsp:getvalueof var="count" param="count" />
																	
										<c:choose>
										   <c:when test="${count % 6 == 0}">   
										        <c:set var="marginClass" value="fl noMarRight"/>
										   </c:when>
										<c:otherwise>
										        <c:set var="marginClass" value="fl"/>
										</c:otherwise>
										</c:choose>
									<dsp:oparam name="output">
										<dsp:getvalueof var="id" param="key"/>
										
										<%-- Added for R2-141 --%>
										<c:choose>
										 <c:when test="${rollupFlag eq 'true' || collectionFlag == '1'}">
										 <dsp:getvalueof var="colorValue" param="element.color"/>
										   <dsp:getvalueof var="colorParam" value="color"/> 
                                           
                                            <c:url value="${finalUrl}" var="colorProdUrl">
                                                <c:param name="categoryId" value="${CategoryId}"/>
                                                <c:param name="color" value="${colorValue}"/>
                                            </c:url>
										  </c:when>
										 <c:otherwise>
										   <dsp:getvalueof var="colorValue" param="element.skuID"/>
										   <dsp:getvalueof var="colorParam" value="skuId"/>
                                           
                                           <c:url value="${finalUrl}" var="colorProdUrl">
                                                <c:param name="categoryId" value="${CategoryId}"/>
                                                <c:param name="skuId" value="${colorValue}"/>
                                            </c:url>
										 </c:otherwise>										
										</c:choose>
										<dsp:getvalueof var="productUrl" param="element.skuMedImageURL"/>
										<c:if test="${portrait eq 'true'}">
											<dsp:getvalueof var="productUrl" param="element.skuVerticalImageURL"/>
										</c:if>
										<dsp:getvalueof var="swatchUrl" param="element.skuSwatchImageURL"/>
										<dsp:getvalueof var="colorName" param="element.color"/>
										
										<c:choose>
											<c:when test="${not empty productUrl}">
												<li class="${marginClass} colorSwatchLi" role="radio" tabindex="${count == 1 ? 0 : -1}" aria-checked="false" data-color-value="${colorValue}" data-color-param="${colorParam}"  data-main-image-src="${scene7DomainPath}/${productUrl}" data-attr="${colorName}">
													<div class="colorswatch-overlay"> </div> 
													<img ${swatchImageAttrib}="${scene7DomainPath}/${swatchUrl}" height="30" width="30" alt="${colorName}"/>
												</li>
											</c:when>
											<c:otherwise>
												<li class="fl colorSwatchLi" role="radio" tabindex="${count == 1 ? 0 : -1}" aria-checked="false" data-color-value="${colorValue}" data-color-param="${colorParam}"  data-main-image-src="${imagePath}/_assets/global/images/no_image_available.jpg" title="${colorName}" data-attr="${colorName}" >
													<div class="colorswatch-overlay"> </div> 
													<img ${swatchImageAttrib}="${scene7DomainPath}/${swatchUrl}" height="30" width="30" alt="${colorName}"/>
												</li>
											</c:otherwise>
										</c:choose> 	 
									</dsp:oparam>
								</dsp:droplet>
								<div class="clear"></div>
							</ul>
							<c:if test="${size > 6 }">
								<span class="toggleColorSwatches collapsed"></span>
							</c:if>
						  </div>
						</div>
			</div>
					 </c:if>
					</div>
				</div>
				<div class="grid_2 omega">
					<div class="prodInfo textRight message bold grid_2 noMar">
						<div class="prod-attribs prodPrice marBottom_10">
						<dsp:getvalueof var="wasPriceRange" param="element.wasPriceRange"/>
						<dsp:getvalueof var="dynamicProdEligible" param="element.dynamicPriceVO.dynamicProdEligible"></dsp:getvalueof>
						</div>
						<div class="prodPrice">
						<%--  BBB Dynamic price string logic starts--%>

						   <%@ include file="product_list_dynamic_product_price_frag.jsp" %> 

						   <%-- <dsp:include page="product_list_dynamic_product_price_frag.jsp">
						<dsp:param name="defaultUserCountryCode" value="${defaultUserCountryCode}" />
						<dsp:param name="dynamicProdEligible" value="${dynamicProdEligible}" />
														<dsp:param name="WAS" value="${WAS}" />
								<dsp:param name="ORIG" value="${ORIG}" />
								<dsp:param name="lblDiscountedIncart" value="${lblDiscountedIncart}" />
								<dsp:param name="lblWasText" value="${lblWasText}" />
								<dsp:param name="lblOrigText" value="${lblOrigText}" />
								<dsp:param name="lblPVOrigText" value="${lblPVOrigText}" />
								<dsp:param name="lblInCartOrigText" value="${lblInCartOrigText}" />
								<dsp:param name="lblPriceTBD" value="${lblPriceTBD}" />
								<dsp:param name="isDynamicPriceEnabled" value="${isDynamicPriceEnabled}" />
						
						
						</dsp:include> --%>

						</div>
						<c:set var="lblPriceTBD"><bbbl:label key="lbl_price_is_tbd" language="${pageContext.request.locale.language}"/></c:set>
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param param="element.attributeVO" name="array" />
								<dsp:param name="elementName" value="attributeVO"/>
								<dsp:oparam name="output">
									<dsp:getvalueof var="attributeVO" param="attributeVO"/>
									<!--PS-56773 : Checking for customization product attribute with compareCustomizationText -->
									<c:if test="${not empty compareCustomizationText && not empty attributeVO.attributeDescrip && fn:contains(attributeVO.attributeDescrip, compareCustomizationText)}">
										<c:set var="lblPriceTBD"><bbbl:label key="lbl_price_is_tbd_customize" language="${pageContext.request.locale.language}"/></c:set>
									</c:if>
									<c:choose>
										<c:when test="${isInternationalCustomer eq true}">
											<c:if test="${attributeVO.intlProdAttr eq 'true'}">
												<div class="prodAttribute"><dsp:valueof value="${attributeVO.attributeDescrip}" valueishtml="true"/></div>
											</c:if>
										</c:when>
										<c:otherwise>
											<c:choose>
												<c:when test="${attributeVO.intlProdAttr != 'X' && !(attributeVO.hideAttribute)}">
													<div class="prodAttribute"><dsp:valueof value="${attributeVO.attributeDescrip}" valueishtml="true"/></div>
												</c:when>
												<c:when test="${attributeVO.hideAttribute}">
													<div class="prodAttribute ${attributeVO.skuAttributeId} hidden"><dsp:valueof value="${attributeVO.attributeDescrip}" valueishtml="true"/></div>
												</c:when>
											</c:choose>
										</c:otherwise>
									</c:choose>
								</dsp:oparam>
							</dsp:droplet>
					
						
						<c:if test="${globalShipMsgDisplayOn && isInternationalCustomer ne true && prdShipMsgFlag}">
							<div class="freeShipBadge">
							${prdDisplayShipMsg}
							</div>
						</c:if>
						
						
							<dsp:getvalueof var="intlRestrictionMssgFlag" param="element.intlRestricted"/>
							
							<c:if test="${isInternationalCustomer and intlRestrictionMssgFlag}">
								<div class="notAvailableIntShipMsg cb clearfix"><bbbl:label key="lbl_plp_intl_restrict_list_msg" language="${pageContext.request.locale.language}"/></div>
							</c:if>

						<c:choose>
							<c:when test="${inStore eq true && (mswpFlag eq true || collectionFlag eq 1)}">
								<div class="inStoreMayNotBeAvailable">
									<span class="prod-attrib prod-attrib-free-ship"><bbbl:label key="lbl_plp_some_items_may_not_be_available_in_store" language="${pageContext.request.locale.language}"/></span>
								</div>
							</c:when>
							<c:when test="${inStore eq true && (vdcFlag eq true)}">
								<div class="inStoreMayNotBeAvailable">
									<span class="prod-attrib prod-attrib-free-ship"><bbbl:label key="lbl_plp_please_call_for_store_availability" language="${pageContext.request.locale.language}"/></span>
								</div>
							</c:when>
							<c:otherwise>

							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</li>
		</dsp:oparam>
	</dsp:droplet>
	</ul>
		<%--Compare metrics changes start --%>
		<c:if test="${CMon}">
			<script type="text/javascript">
			$CMPR.configure({
				categoryID: "${CategoryId}",
				pageType: "category"
				});
		</script>
		</c:if>
	<%--Compare metrics changes end --%>	
</dsp:page>