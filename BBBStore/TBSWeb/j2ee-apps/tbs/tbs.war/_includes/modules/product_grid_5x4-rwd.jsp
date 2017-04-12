<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
	<dsp:importbean bean="/atg/dynamo/droplet/Range" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:getvalueof var="count" param="BBBProductListVO.bbbProductCount" />
	<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductListHandler"/>
	<dsp:getvalueof var="CategoryId" param="CatalogRefId" />
	<dsp:getvalueof var="promoSC" param="promoSC"/>
	<dsp:getvalueof var="plpGridSize" param="plpGridSize"/>
	<dsp:getvalueof param="browseSearchVO" var="browseSearchVO"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof param="searchType" var="searchType"/>
	<dsp:getvalueof param="keyword" var="keyword"/>.
	<dsp:getvalueof var="brandId" param="brandId"/>
	<dsp:getvalueof var="brandId" param="brandId"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet" />
	<c:set var="compareProducts"><bbbc:config key="compareProducts" configName="FlagDrivenFunctions" /></c:set>
	<c:set var="TruckSrc"><bbbc:config key="truck_image_src" configName="LTLConfig_KEYS" /></c:set>
	<c:set var="lblPriceTBD" scope="request"><bbbl:label key="lbl_price_is_tbd_tbs" language="${pageContext.request.locale.language}"/></c:set>
    <c:set var="prodImageAttrib" scope="request">class="productImage lazyLoad loadingGIF" src="${imagePath}/_assets/tbs_assets/img/ajax-loader.gif" data-lazyloadsrc</c:set>
    <c:set var="swatchImageAttrib" scope="request">class="swatchImage lazySwatchLoad loadingGIF" src="${imagePath}/_assets/tbs_assets/img/ajax-loader.gif" data-lazyloadsrc</c:set>
    <%-- render normal classes/src attribute if disableLazyLoadS7Images is set to "true" --%>
    <c:if test="${disableLazyLoadS7Images eq true}">
        <c:set var="prodImageAttrib" scope="request">class="productImage noImageFound" src</c:set>
        <c:set var="swatchImageAttrib" scope="request">class="swatchImage noImageFound" src</c:set>
    </c:if>
	<c:set var="compareCustomizationText">
		<bbbc:config key="CompareCustomizationText" configName="EXIMKeys"/>
	</c:set>
	

<dsp:form action="/tbs/search/upcresults.jsp?keyword=${searchTerm}&type=upc" method="post" id="upcgridForm">
	<dsp:droplet name="/com/bbb/search/droplet/TBSMinimalQuickViewDroplet">
		<dsp:param name="array" param="BBBProductListVO.bbbProducts" />
		<dsp:param name="elementName" value="productVO"/>
		<dsp:oparam name="outputStart">
		<ul class="small-block-grid-1 medium-block-grid-${plpGridSize} large-block-grid-${plpGridSize} plpListGridToggle">
		</dsp:oparam>
		<dsp:oparam name="outputEnd">
		</ul>
		</dsp:oparam>
		<dsp:getvalueof param="size" var="totalSize"/>
		<dsp:oparam name="output">
			<dsp:getvalueof param="count" var="currentCount"/>
			<dsp:getvalueof var="productId" param="productVO.productID" />

			<dsp:getvalueof var="productName" param="productVO.productName"/>
			<dsp:getvalueof var="attribs" param="productVO.attribute" />
			<dsp:getvalueof var="hideAttributeList" param="productVO.hideAttributeList" />
			<dsp:getvalueof var="wasPriceRange" param="productVO.wasPriceRange" />
			<dsp:getvalueof var="isPriceRange" param="productVO.priceRange" />
			
			<%-- Check for Swatch Flag attribute returned from Search Engine--%>
			<dsp:getvalueof var="swatchFlag" param="productVO.swatchFlag"/>
			<dsp:getvalueof var="rollupFlag" param="productVO.rollupFlag"/>
			<dsp:getvalueof var="collectionFlag" param="productVO.collectionFlag"/>
			<dsp:getvalueof var="promoSC" param="promoSC"/>
			<dsp:getvalueof var="portrait" param="portrait"/>
			<dsp:getvalueof id="count" param="count" />
			<dsp:getvalueof id="productCountDisplayed" param="size" />
			<dsp:getvalueof var="previousProductId" value="" />
			<dsp:getvalueof var="nextProductId" value="" />
			<c:if test="${count > 1}"><dsp:getvalueof var="previousProductId" param="previousProductId" /></c:if>
			<c:if test="${count < productCountDisplayed}"><dsp:getvalueof var="nextProductId" param="nextProductId" /></c:if>


			<%-- for first 9 items, send img source, not lazy load --%>
			<c:choose>
				<c:when test="${count < 10}">
			        <c:set var="prodImageAttrib" scope="request">class="productImage noImageFound" src</c:set>
			        <c:set var="swatchImageAttrib" scope="request">class="swatchImage noImageFound" src</c:set>
				 </c:when>
				<c:otherwise>
					<c:set var="prodImageAttrib" scope="request">class="productImage lazyLoad loadingGIF" src="${imagePath}/_assets/tbs_assets/img/ajax-loader.gif" data-lazyloadsrc</c:set>
    				<c:set var="swatchImageAttrib" scope="request">class="swatchImage lazySwatchLoad loadingGIF" src="${imagePath}/_assets/tbs_assets/img/ajax-loader.gif" data-lazyloadsrc</c:set>

				</c:otherwise>
			</c:choose>

		    
				<li class="categoryItems">

					<div class="row">
						<div class="small-12 columns productContent">
							<dsp:getvalueof var="seoUrl" param="productVO.seoUrl"/>
							<c:choose>
								<c:when test="${empty seoUrl}">
									<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
										<dsp:param name="id" param="productVO.productID" />
										<dsp:param name="itemDescriptorName" value="product" />
										<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
										<dsp:oparam name="output">
										<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
										</dsp:oparam>
									</dsp:droplet>
								</c:when>
								<c:otherwise>
									<c:set var="finalUrl" value="${seoUrl}"></c:set>
								</c:otherwise>
							</c:choose>
							
							<dsp:getvalueof var="portraitClass" value=""/>
							<dsp:getvalueof var="imageURL" param="productVO.imageURL"/>
							<c:set var="imageURL"><c:out value='${fn:replace(imageURL,"$146$","$478$")}'/></c:set>
							<c:if test="${portrait eq 'true'}">
								<dsp:getvalueof var="portraitClass" value="prodImgPortrait"/>
								<dsp:getvalueof var="imgHeight" value="200"/>
								<dsp:getvalueof var="imageURL" param="productVO.verticalImageURL"/>
							</c:if>
							
							<div class="category-prod-img">
								<c:choose>
								    <c:when test="${not empty CategoryId}">
								      <dsp:a iclass="prodImg" page="${finalUrl}?categoryId=${CategoryId}" title="${productName}">
											<%-- Thumbnail image exists OR not--%>
											<c:choose>
												<c:when test="${not empty imageURL}">
												 <c:choose>
												  <c:when test="${imageSize ne null && not empty imageSize}">
													<img ${prodImageAttrib}="${scene7Path}/${imageURL}" height="${imageSize}" width="${imageSize}" alt="${productName}" />
												  </c:when>
												  <c:otherwise>
												    <img ${prodImageAttrib}="${scene7Path}/${imageURL}" alt="${productName}" />
												  </c:otherwise>
												 </c:choose>
												</c:when>
												<c:otherwise>
												  <c:choose>
												   <c:when test="${imageSize ne null && not empty imageSize}">
													<img class="stretch productImage" src='${imagePath}/_assets/global/images/no_image_available.jpg' alt="${productName}" width="${imageSize}" height="${imageSize}" />
												   </c:when>
												   <c:otherwise>
												    <img class="stretch productImage" src='${imagePath}/_assets/global/images/no_image_available.jpg' alt="${productName}"/>
												   </c:otherwise>
												  </c:choose>
												</c:otherwise>
											</c:choose>
										</dsp:a>
										<c:set var="fromPage" value="categoryPage"/>
								    </c:when>
								    <c:when test="${not empty brandId}">
								      <dsp:a iclass="prodImg" page="${finalUrl}?brandId=${brandId}" title="${productName}">
											<%-- Thumbnail image exists OR not--%>
											<c:choose>
												<c:when test="${not empty imageURL}">
													<img class="stretch" src="${scene7Path}/${imageURL}" height="${imageSize}" width="${imageSize}" alt="${productName}" />
												</c:when>
												<c:otherwise>
													<img class="stretch" src='${imagePath}/_assets/global/images/no_image_available.jpg' alt="${productName}" width="${imageSize}" height="${imageSize}" />
												</c:otherwise>
											</c:choose>
										</dsp:a>
										<c:set var="fromPage" value="brandPage"/>
								    </c:when>
								    <c:when test="${not empty searchTerm}">
								      <dsp:a iclass="prodImg" page="${finalUrl}?keyword=${searchTerm}" title="${productName}">
											<%-- Thumbnail image exists OR not--%>
											<c:choose>
												<c:when test="${not empty imageURL}">
												  <c:choose>
												   <c:when test="${imageSize ne null && not empty imageSize}">
													<img ${prodImageAttrib}="${scene7Path}/${imageURL}" height="${imageSize}" width="${imageSize}" alt="${productName}" />
												   </c:when>
												   <c:otherwise>
												    <img ${prodImageAttrib}="${scene7Path}/${imageURL}" alt="${productName}" />
												   </c:otherwise>
												   </c:choose>
												</c:when>
												<c:otherwise>
												  <c:choose>
												   <c:when test="${imageSize ne null && not empty imageSize}">
													<img class="stretch productImage" src='${imagePath}/_assets/global/images/no_image_available.jpg' alt="${productName}" width="${imageSize}" height="${imageSize}" />
												   </c:when>
												   <c:otherwise>
												    <img class="stretch productImage" src='${imagePath}/_assets/global/images/no_image_available.jpg' alt="${productName}"/>
												   </c:otherwise>
												  </c:choose>
												</c:otherwise>
											</c:choose>
										</dsp:a>
										<c:set var="fromPage" value="searchPage"/>
								    </c:when>
								    <c:otherwise>
								      <dsp:a iclass="prodImg" page="${finalUrl}" title="${productName}">
											<%-- Thumbnail image exists OR not--%>
											<c:choose>
												<c:when test="${not empty imageURL}">
													<img class="stretch" src="${scene7Path}/${imageURL}" height="${imageSize}" width="${imageSize}" alt="${productName}" />
												</c:when>
												<c:otherwise>
													<img class="stretch" src='${imagePath}/_assets/global/images/no_image_available.jpg' alt="${productName}" width="${imageSize}" height="${imageSize}" />
												</c:otherwise>
											</c:choose>
										</dsp:a>
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
								<div class="quickview-button quickViewAndCompare">
								</div>
							</div>

							<c:choose>
								<c:when test="${rollupFlag eq 'true' || collectionFlag == '1'}">
									<c:set var="isMultiRollUpFlag" value="true" />
								</c:when>
								<c:otherwise>
									<c:set var="isMultiRollUpFlag" value="false" />
								</c:otherwise>
							</c:choose>

					<%--  End | R2.2.1 Story 131. Quick View on PLP Search Pages --%>
							<div class="qv-compare">
								<div class="qv-link small-6 columns">
									<a id="product_${productId}" class="${quickViewClass} quick-view" data-reveal-id="quickViewModal_${productId}" data-reveal-ajax="true"
                                    	href="${contextPath}/browse/quickview_product_details.jsp?previousProductId=${previousProductId}&pdpLink=${finalUrl}&fromPage=${fromPage}&CategoryId=${CategoryId}&nextProductId=${nextProductId}&productId=${productId}">
										<bbbl:label key="lbl_product_quick_view" language="${pageContext.request.locale.language}" />
									</a>
								</div>
								
								<c:if test="${compareProducts eq true}">
									<dsp:getvalueof var="productId" param="productVO.productID"/>
									<dsp:getvalueof var="inCompareDrawer" param="productVO.inCompareDrawer"/>
									
									<c:choose>
										<c:when test="${inCompareDrawer eq true}">
											<div class="compare-link small-6 columns">
												<label for="compareChkTxt_${productId}" class="compareChkTxt inline-rc checkbox">
													<input name="Compare" id="compareChkTxt_${productId}" class="compareChkTxt" type="checkbox" value="compareItem" data-productid="${productId}" data-imgsrc="${scene7Path}/${imageURL}" checked = "true" />
													<span></span>
													<bbbl:label key="lbl_compare_product" language="${pageContext.request.locale.language}" />
												</label>
											</div>
										</c:when>
										<c:otherwise>
											<div class="compare-link small-6 columns">
												<label for="compareChkTxt_${productId}" class="inline-rc checkbox">
													<input name="Compare" id="compareChkTxt_${productId}" data-imgsrc="${scene7Path}/${imageURL}" class="compareChkTxt" type="checkbox" value="compareItem" data-productid="${productId}" />
													<span></span>
													<bbbl:label key="lbl_compare_product" language="${pageContext.request.locale.language}" />
												</label>
											</div>
										</c:otherwise>
									</c:choose>
								</c:if>
                                <div id="quickViewModal_${productId}" class="qView reveal-modal xlarge" data-options="close_on_background_click:true;close_on_esc:true;" data-reveal></div>
							</div>
						</div>

						<div class="small-12 columns">
							<c:if test="${swatchFlag == '1'}">
								<div class="color-swatches small-12 columns">
									<dsp:droplet name="ForEach">
										<dsp:param name="array" param="productVO.colorSet" />
										<dsp:param name="elementName" value="colorSetVO" />

										<dsp:oparam name="outputStart">
											<ul class="color-swatch-grid">
										</dsp:oparam>
										<dsp:oparam name="outputEnd">
											</ul>
										</dsp:oparam>
										<dsp:oparam name="output">
											<dsp:getvalueof var="id" param="key"/>

											<!-- Added for R2-141 -->
											<c:choose>
											<c:when test="${rollupFlag eq 'true' || collectionFlag == '1'}">
											<dsp:getvalueof var="colorValue" param="colorSetVO.color"/>
											<dsp:getvalueof var="colorParam" value="color"/>

												<c:url value="${finalUrl}" var="colorProdUrl">
													<c:param name="categoryId" value="${CategoryId}"/>
													<c:param name="color" value="${colorValue}"/>
												</c:url>
											</c:when>
											<c:otherwise>
											<dsp:getvalueof var="colorValue" param="colorSetVO.skuID"/>
											<dsp:getvalueof var="colorParam" value="skuId"/>

											<c:url value="${finalUrl}" var="colorProdUrl">
													<c:param name="categoryId" value="${CategoryId}"/>
													<c:param name="skuId" value="${colorValue}"/>
												</c:url>
											</c:otherwise>
											</c:choose>


											<dsp:getvalueof var="productUrl" param="colorSetVO.skuMedImageURL"/>
											<c:if test="${portrait eq 'true'}">
												<dsp:getvalueof var="productUrl" param="colorSetVO.skuVerticalImageURL"/>
											</c:if>
											<dsp:getvalueof var="swatchUrl" param="colorSetVO.skuSwatchImageURL"/>
											<dsp:getvalueof var="colorName" param="colorSetVO.color"/>
											
											<li>
												<a href="${colorProdUrl}" class="fl" title="${colorName}"  data-color-value="${colorValue}" data-color-param="${colorParam}"  data-main-image-src="${scene7Path}/${productUrl}">
													<img ${swatchImageAttrib}="${scene7Path}/${swatchUrl}" alt="${colorName}"/>
												</a>
											</li>
										</dsp:oparam>
									</dsp:droplet>
								</div>
							</c:if>

							<h2 class="subheader small-12 columns">
								<dsp:getvalueof var="seoUrl" param="productVO.seoUrl"/>
									<c:choose>
									<c:when test="${empty seoUrl}">
										<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
											<dsp:param name="id" param="productVO.productID" />
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
									<dsp:getvalueof var="pageURL" value="${finalUrl}" />
								<c:set var="prodName">
									<dsp:valueof param="productVO.productName" valueishtml="true"/>
								</c:set>
								
								<c:choose>
									<c:when test="${not empty CategoryId}">
										<dsp:a  page="${finalUrl}?categoryId=${CategoryId}"  title="${prodName}">
										<dsp:valueof param="productVO.productName" valueishtml="true" />
										</dsp:a>
									</c:when>
									<c:when test="${not empty brandId}">
										<dsp:a  page="${finalUrl}?brandId=${brandId}"  title="${prodName}">
										<dsp:valueof param="productVO.productName" valueishtml="true" />
										</dsp:a>
									</c:when>
									<c:when test="${not empty searchTerm}">
										<dsp:a  page="${finalUrl}?keyword=${searchTerm}"  title="${prodName}">
										<dsp:valueof param="productVO.productName" valueishtml="true" />
										</dsp:a>
									</c:when>								
									<c:otherwise>
										<dsp:a  page="${finalUrl}"  title="${prodName}">
										<dsp:valueof param="productVO.productName" valueishtml="true" />
										</dsp:a>
									</c:otherwise>
								</c:choose>
							</h2>
							<%-- TBSN-262 fix (reviews & ratings) changes starts here)--%>
							
							 <div class="small-12 columns product-reviews">
	                            <%-- <a class="stars" href="#">Read reviews (34)</a> | <a href="#">Write a review</a> | <a href="#">Read Q&amp;A (10,2)</a> --%>
	                            <dsp:getvalueof var="productVO" param="productVO"/>
	                            
	                            <c:if test="${BazaarVoiceOn}">
                                       	<dsp:getvalueof var="reviews" value="${productVO.reviews}" />
                                        <dsp:getvalueof var="ratings" value="${productVO.ratings}" vartype="java.lang.Integer" />
                                        <dsp:getvalueof var="rating" value="${ratings * 10}" vartype="java.lang.Integer" />
                                        <c:choose>
                                           <c:when test="${empty productVO}">
                                               <dsp:getvalueof var="totalReviewCount" value="0"></dsp:getvalueof>
                                           </c:when>
                                           <c:otherwise>
                                               <dsp:getvalueof var="totalReviewCount" value="${productVO.reviews}"></dsp:getvalueof>
                                           </c:otherwise>
                                       </c:choose>
                                       <c:if test="${not flagOff}">
                                           <c:choose>
                                               <c:when test="${ratings ne null && ratings ne '0' && reviews eq '1'}">
                                                    <div class="prodReviews ratingsReviews clearfix prodReviews<fmt:formatNumber value="${rating}" pattern="#0" />"> 
                                                        <dsp:a onclick="javascript:(typeof quickViewCrossSell === 'function')?quickViewCrossSell():void(0);" page="${pageURL}?skuId=${skuId}&amp;categoryId=${CategoryId}&amp;showRatings=true"> ${totalReviewCount} <bbbl:label key="lbl_sfl_reviews" language="<c:out param='${language}'/>" />
                                                        </dsp:a> 
                                                    </div>
                                                </c:when>
                                                <c:when test="${ratings ne null && ratings ne '0' && reviews gt '1'}">
	                                                <div class="prodReviews ratingsReviews clearfix prodReviews<fmt:formatNumber value="${rating}" pattern="#0" />"> 
	                                                        <dsp:a onclick="javascript:(typeof quickViewCrossSell === 'function')?quickViewCrossSell():void(0);" page="${pageURL}?skuId=${skuId}&amp;showRatings=true"> ${totalReviewCount} <bbbl:label key="lbl_sfl_reviews" language="<c:out param='${language}'/>" />
	                                                        </dsp:a> 
	                                                </div>
                                                </c:when>
                                                <c:otherwise>
                                                	<div class="prodReviews ratingsReviews clearfix writeReview">
	                                                	<c:set var="writeReviewLink"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></c:set>
														<dsp:a page="${pageURL}?skuId=${skuId}&amp;categoryId=${CategoryId}&writeReview=true" title="${writeReviewLink}">${writeReviewLink}</dsp:a>
                                                    </div>
                                                </c:otherwise>
                                           </c:choose>
                                       </c:if>
                                   </c:if>
	                        </div>
	                        
	                        
							<%-- TBSN-262 fix (reviews & ratings) changes ends here)--%>
							<c:set var="hideAttribute" value="${false}"/>
							<dsp:droplet name="ForEach">
								<dsp:param name="array" value="${attribs}" />
								<dsp:param name="elementName" value="element"/>
								<dsp:oparam name="output">
								<c:set var="attDesc">
								<dsp:valueof param="element" valueishtml="true"/></c:set>
								<c:if test="${not empty compareCustomizationText && not empty attDesc && fn:contains(attDesc, compareCustomizationText)}">
									<c:set var="lblPriceTBD" scope="request"><bbbl:label key="lbl_price_is_tbd_customize" language="${pageContext.request.locale.language}"/></c:set>
								</c:if>
									    <c:choose>
											<c:when test="${not empty hideAttributeList}">
												<c:forEach var="attribList" items="${hideAttributeList}">
													<c:if test="${attribList ne attDesc}">
														<c:set var="hideAttribute" value="${true}" />
													</c:if>
												</c:forEach>
												<c:if test="${hideAttribute}">
													<div class="prodAttribute small-12 columns">
														<dsp:valueof param="element" valueishtml="true" />
													</div>
												</c:if>
											</c:when>
											<c:otherwise>
												<div class="prodAttribute small-12 columns">
													<dsp:valueof param="element" valueishtml="true" />
												</div>
											</c:otherwise>
										</c:choose>
								</dsp:oparam>
							</dsp:droplet>

						<%-- BBBJ-790| PLP - Load all content and SKU data from Endeca. Minimise JDBC calls on the PLP page | STARTS --%>
						<dsp:getvalueof var="pDefaultChildSku" value="" />
						<dsp:getvalueof var="skuInCartFlag" value="${false}" />
						<dsp:getvalueof var="prodInCartFlag" value="${false}" />
						<dsp:getvalueof var="pennyPrice" value="${false}" />
						<dsp:getvalueof var="dynamicProdEligible" param="productVO.dynamicPriceVO.dynamicProdEligible"/>
						<c:if test="${dynamicProdEligible}">
								<c:choose>
									<c:when test="${appid eq 'TBS_BedBathCanada'}">
										<dsp:getvalueof var="wasPriceRange" param="productVO.dynamicPriceVO.caListPriceString"/>
										<dsp:getvalueof var="isPriceRange" param="productVO.dynamicPriceVO.caSalePriceString"/>
										<dsp:getvalueof var="prodInCartFlag" param="productVO.dynamicPriceVO.caIncartFlag" />
									</c:when>
									<c:when test="${appid eq 'TBS_BuyBuyBaby'}">
										<dsp:getvalueof var="wasPriceRange" param="productVO.dynamicPriceVO.babyListPriceString"/>
										<dsp:getvalueof var="isPriceRange" param="productVO.dynamicPriceVO.babySalePriceString"/>
										<dsp:getvalueof var="prodInCartFlag" param="productVO.dynamicPriceVO.babyIncartFlag" />
									</c:when>
									<c:otherwise>
										<dsp:getvalueof var="wasPriceRange" param="productVO.dynamicPriceVO.bbbListPriceString"/>
										<dsp:getvalueof var="isPriceRange" param="productVO.dynamicPriceVO.bbbSalePriceString"/>
										<dsp:getvalueof var="prodInCartFlag" param="productVO.dynamicPriceVO.bbbIncartFlag" />
									</c:otherwise>
								</c:choose>
						</c:if>
						<c:if test="${prodInCartFlag}">
							<dsp:droplet name="ProductDetailDroplet">
								<dsp:param name="id" value="${productId}" />
								<dsp:param name="siteId" value="${appid}" />
								<dsp:param name="isDefaultSku" value="true"/>
								<dsp:param name="checkInventory" value="${false}"/>
								<dsp:oparam name="output">
									<dsp:getvalueof var="skuVO" param="pSKUDetailVO" />
										<c:if test="${skuVO != null}">
											<dsp:getvalueof var="pDefaultChildSku" param="pSKUDetailVO.skuId" />
											<dsp:getvalueof var="skuInCartFlag" param="pSKUDetailVO.inCartFlag" />
										</c:if>
									</dsp:oparam>
							</dsp:droplet>
						</c:if>
							<div class="price product-info small-12 columns">
							<dsp:getvalueof var="pennyPrice" param="productVO.pennyPrice" />
								<c:choose>
									<c:when test="${pennyPrice}">${lblPriceTBD}</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${not empty pDefaultChildSku}">
											<dsp:include page="../../browse/product_details_price.jsp">
												<dsp:param name="priceFromPage" value="gridView" />
												<dsp:param name="product" value="${productId}" />
												<dsp:param name="sku" value="${pDefaultChildSku}" />
												<dsp:param name="inCartFlag" value="${skuInCartFlag}" />
											</dsp:include>
										</c:when>
										<c:otherwise>
											<dsp:droplet name="IsEmpty">
												<dsp:param name="value" value="${isPriceRange}" />
												<dsp:oparam name="false">
													<h1 class="price price-sale">
														<c:set var="priceFromConvertor">
															<dsp:valueof converter="currency" value="${isPriceRange}" />
														</c:set>
														<dsp:valueof format="${format}" converter="formattedPrice"
															value="${priceFromConvertor}" />
													</h1>
													<h3 class="price price-original">
														<c:set var="priceFromConvertor">
															<dsp:valueof converter="currency"
																value="${wasPriceRange}" />
														</c:set>
														${priceFromConvertor}
													</h3>
												</dsp:oparam>
												<dsp:oparam name="true">
														<c:set var="priceFromConvertor">
															<dsp:valueof converter="currency" value="${wasPriceRange}" />
														</c:set>
														<div class="isPrice"><dsp:valueof format="${format}" converter="formattedPrice"
															value="${priceFromConvertor}" /></div>
												</dsp:oparam>
											</dsp:droplet>
										</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>

							</div> 
							<%-- BBBJ-790| PLP - Load all content and SKU data from Endeca. Minimise JDBC calls on the PLP page | ENDS --%>
						
							<div class="">
								<input type="hidden" data-change-store-storeid="storeId" value="" name="storeId" class="addToCartSubmitData"/>
								<input type="hidden" name="parentProductId" value="${productId}" class="addItemToList addItemToRegis" />
								<input type="hidden" value="" class="addToCartSubmitData" name="bts" data-change-store-storeid="bts"/>
								<input type="hidden" value="true" class="addItemToList addItemToRegis" name="fromSearch"/>
								<input type="hidden" value="${LiveClickerOn}" name="lcFlag"/>
								<input type="hidden" value="${CategoryId}" class="categoryId"/>
								<input type="hidden" value="${isMultiRollUpFlag}" class="isMultiRollUpFlag"/>
								<input type="hidden" value="" class="selectedRollUpValue"/>
							</div>
						</div>
						
						<div class="hide">
							<div class="p-secondary product-list-description">
								<dsp:valueof param="productVO.productName" valueishtml="true"/>
							</div>
							<a class="button secondary" href="${contextPath}${finalUrl}">Learn More</a>
						</div>

					</div>
				</li>
				<hr class="show-for-small"/>
				
		</dsp:oparam>
	</dsp:droplet>
	<c:if test="${not empty searchType && searchType eq 'upc'}">
		<hr>
		<div class="small-12 large-3 large-right columns">
		<dsp:include page="/giftregistry/upc_check_gift_registry.jsp"/>
		<input type="text" value="" name="upcregistryId" placeholder="ENTER REGISTRY ID" id="upcSearchRegistryId"/>
		<dsp:input name="submit" bean="CartModifierFormHandler.addMultipleItemsToOrder" type="submit" iclass="button transactional" value="Add To Cart" id="addAllItemsToRegistry"/>
		<dsp:input bean="CartModifierFormHandler.errorQueryParam"
												type="hidden"
												value="keyword=${searchTerm}&type=upc" /> 
							           <dsp:input bean="CartModifierFormHandler.fromPage"  type="hidden" value="productGrid5*4" />
		<%-- <dsp:input bean="CartModifierFormHandler.addMultipleItemsToOrderSuccessURL"  type="hidden" value="/tbs/cart/cart.jsp" />
		<dsp:input bean="CartModifierFormHandler.addMultipleItemsToOrderErrorURL"  type="hidden" value="/tbs/search/upcresults.jsp?keyword=${searchTerm}&type=upc" /> --%>
		</div>
	</c:if>
	<input type='submit' class='hide' name='submit' vlaue='submit'/>
	</dsp:form>
	<%-- search for registrant modal --%>
    <div id="searchForRegistrant" class="reveal-modal xlarge reg-search" data-reveal></div>
    <c:if test="${not empty searchType && searchType eq 'upc'}">
    <script type="text/javascript">
		$(document).ready(function(){
			$(".nearby-stores").attr("data-reveal-id","nearbyStore");
			$(".nearby-stores").attr("data-reveal-ajax","true");
			$(document).foundation('reflow');
		});
	</script>
	</c:if>
</dsp:page>
