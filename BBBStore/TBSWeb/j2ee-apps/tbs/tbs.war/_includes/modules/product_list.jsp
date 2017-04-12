<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:getvalueof var="CategoryId" param="CatalogRefId" />
	<dsp:getvalueof param="browseSearchVO" var="browseSearchVO"/>
	<dsp:getvalueof var="count" param="BBBProductListVO.bbbProductCount"/>
		
	<%-- R2.2 Story - 178-a Product Comparison tool Changes | Check if compare functionality is enabled --%>
	<c:set var="compareProducts"><bbbc:config key="compareProducts" configName="FlagDrivenFunctions" /></c:set>
	
	<ul class="prodListRow clearfix noMar">
	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="BBBProductListVO.bbbProducts" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="productId" param="element.productID" />
			<dsp:getvalueof var="productName" param="element.productName"/>
			<%-- Check for Swatch Flag attribute returned from Search Engine--%>
			<dsp:getvalueof var="swatchFlag" param="element.swatchFlag"/>
			<dsp:getvalueof var="rollupFlag" param="element.rollupFlag"/>
			<dsp:getvalueof var="collectionFlag" param="element.collectionFlag"/>
			<dsp:getvalueof var="forEachIndex" param="count"/>
			<dsp:getvalueof var="portrait" param="portrait"/>
			<dsp:getvalueof id="productCountDisplayed" param="size" />
			<c:set var="liClass" value="" />
			<c:if test="${forEachIndex == 1}"><c:set var="liClass" value=" first" /></c:if>
			
			<li class="product grid_9 ${liClass} registryDataItemsWrap listDataItemsWrap">
				<div class="productShadow"></div>
				<div class="productContent grid_2 alpha">
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
					<dsp:a iclass="prodImg ${portraitClass}" page="${finalUrl}?categoryId=${CategoryId}" title="${productName}">
						<%-- Thumbnail image exists OR not--%>
						<c:choose>
							<c:when test="${not empty imageURL}">
								<img class="productImage noImageFound" src="${scene7Path}/${imageURL}" height="${imgHeight}" width="146" alt="${productName}" />
							</c:when>
							<c:otherwise>
								<img class="productImage" src='${imagePath}/_assets/global/images/no_image_available.jpg' alt="${productName}" width="146" height="${imgHeight}" />
							</c:otherwise>
						</c:choose>
					</dsp:a>
					<%-- Start :- R2.2 Story 546. Adding Ribbon for Featured Product --%>
						<dsp:include page="/search/featured_product.jsp">
							<dsp:param name="browseSearchVO" param="browseSearchVO" />
							<dsp:param name="productId" value="${productId}"/>
							<dsp:param name="productCountDisplayed" value="${productCountDisplayed}"/>
						</dsp:include>
						
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
					 <!-- Adding Qucik View Link : Story : 716_PLP list view re-design |Sprint1(2.2.1)| [Start]  -->
					<div class="padTop_10 padBottom_5">
						<span class="quickView ${quickViewClass}"><bbbl:label key="lbl_product_quick_view" language="${pageContext.request.locale.language}" /></span>
						
						<!-- Data to submit for Add to Cart / Find In Store -->
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
					<!-- Adding Qucik View Link : Story : 716_PLP list view re-design |Sprint1(2.2.1)| [End]  -->
				   <%-- R2.2 Story - 178-a Product Comparison tool Changes : Start --%>	
					<c:if test="${compareProducts eq true}">
						<dsp:getvalueof var="productId" param="element.productId"/>
						<dsp:getvalueof var="inCompareDrawer" param="element.inCompareDrawer"/>
						<c:choose>
							<c:when test="${inCompareDrawer eq true}">
								<input id="compareChkTxt_${productId}" type="checkbox" name ="Compare" class="compareChkTxt" data-productId="${productId}"  checked = "true" />
								<label class="compareChkTxt" for="compareChkTxt_${productId}"><bbbl:label key="lbl_compare_product" language="${pageContext.request.locale.language}" /></label>
								
							</c:when>
							<c:otherwise>
								<input id="compareChkTxt_${productId}" type="checkbox" name ="Compare" class="compareChkTxt" data-productId="${productId}" />
									<label class="compareChkTxt" for="compareChkTxt_${productId}"><bbbl:label key="lbl_compare_product" language="${pageContext.request.locale.language}" /></label>
								
							</c:otherwise>
						</c:choose>
					</c:if>
					<%-- R2.2 Story - 178-a Product Comparison tool Changes : End --%>	
					</div>
					
					
					</div>
					
					<div class="grid_5">
					<ul class="prodInfo">
						<li class="prodName">
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
							<dsp:a  page="${finalUrl}?categoryId=${CategoryId}"  title="${prodName}">
								<dsp:valueof param="element.productName" valueishtml="true" />
							</dsp:a>
						</li>
						<c:if test="${BazaarVoiceOn}">
						<dsp:getvalueof var="reviews" param="element.reviews"/>
						<dsp:getvalueof var="ratings" param="element.ratings" vartype="java.lang.Integer"/>
						<dsp:getvalueof var="rating" value="${ratings * 10}" vartype="java.lang.Integer"/>
						<c:choose>
							<c:when test="${ratings ne null && ratings ne '0'}">
								<li class="prodReviews ratingsReviews clearfix prodReviews<fmt:formatNumber value="${rating}" pattern="#0" />">
									<c:if test="${reviews ne null && reviews ne '0' && reviews gt '1'}">
									   <dsp:a page="${pdpUrl}?showRatings=true" title="${productName}">
									    	<dsp:valueof param="element.reviews"/><bbbl:label key="lbl_reviews_count" language="${pageContext.request.locale.language}" />
										</dsp:a>
									</c:if>
									<c:if test="${reviews ne null && reviews ne '0' && reviews eq '1'}">
									   <dsp:a page="${pdpUrl}?showRatings=true" title="${productName}">
										 <dsp:valueof param="element.reviews"/><bbbl:label key="lbl_review_count" language="${pageContext.request.locale.language}" />
									   </dsp:a>
									</c:if>
								</li>
							</c:when>
							<c:otherwise>
								<li class="prodReviews ratingsReviews writeReview">
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
									<dsp:a page="${finalUrl}?categoryId=${CategoryId}&writeReview=true" title="${writeReviewLink}">${writeReviewLink}</dsp:a>
								</li>
							</c:otherwise>
						</c:choose>
						</c:if>
					<li class="prodDesc marBottom_10">
						<div class="width_5 clearfix wrap">
							<dsp:valueof param="element.description" valueishtml="true"/>
						</div>
						<!-- Adding View More Link : Story : 716_PLP list view re-design |Sprint1(2.2.1)| [Start]  -->
					   	<dsp:a page="${finalUrl}?categoryId=${CategoryId}#prodViewDefault" title="${productName}" iclass="viewMore hidden">
					       <bbbl:label key="lbl_product_list_view_more" language="${pageContext.request.locale.language}" />
					    </dsp:a>
					 	<!-- Adding View More Link : Story : 716_PLP list view re-design |Sprint1(2.2.1)| [End]  -->
					</li>
					<c:if test="${swatchFlag == '1'}">
						<div class="prodSwatchesContainer clearfix">
							<div class="colorPicker fl marTop_10">
							 <label><bbbl:label key="lbl_colors_dropdown" language ="${pageContext.request.locale.language}"/></label>
							<div class="swatches prodSwatches fl hideSwatchRows">
								<dsp:droplet name="ForEach">
									<dsp:param name="array" param="element.colorSet" />
									<dsp:getvalueof var="size" param="size" />
									<dsp:getvalueof var="count" param="count" />
																	
										<c:choose>
										   <c:when test="${count % 9 == 0}">   
										        <c:set var="marginClass" value="fl noMarRight"/>
										   </c:when>
										<c:otherwise>
										        <c:set var="marginClass" value="fl"/>
										</c:otherwise>
										</c:choose>
									<dsp:oparam name="output">
										<dsp:getvalueof var="id" param="key"/>
										
										<!-- Added for R2-141 -->
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
												<a href="${colorProdUrl}" class="${marginClass}" title="${colorName}"  data-color-value="${colorValue}" data-color-param="${colorParam}"  data-main-image-src="${scene7Path}/${productUrl}">
													<img src="${scene7Path}/${swatchUrl}" height="20" width="20" alt="${colorName}"/>
												</a>
											</c:when>
											<c:otherwise>
												<a href="${colorProdUrl}" class="${marginClass}" title="${colorName}" data-color-value="${colorName}" data-color-value="${colorValue}" data-color-param="${colorParam}" data-main-image-src="${imagePath}/_assets/global/images/no_image_available.jpg">
													<img src="${scene7Path}/${swatchUrl}" height="20" width="20" alt="${colorName}"/>
												</a>
											</c:otherwise>
										</c:choose> 	 
									</dsp:oparam>
								</dsp:droplet>
								<div class="clear"></div>
							</div>
							<c:if test="${size > 9 }">
								<span class="toggleColorSwatches collapsed"></span>
							</c:if>
						  </div>
						</div>
					 </c:if>
					</ul>
				</div>
				<div class="grid_2 omega">
					<ul class="prodInfo textRight message bold grid_2 noMar">
						<li class="prod-attribs prodPrice marBottom_10"><dsp:valueof param="element.priceRange" valueishtml="true" /></li>
						<dsp:droplet name="ForEach">
							<dsp:param name="array" param="element.attribute" />
							<dsp:oparam name="output">
								<li><dsp:valueof param="element" valueishtml="true"/></li>
							</dsp:oparam>
						</dsp:droplet>
						
					</ul>
				</div>
			</li>
		</dsp:oparam>
	</dsp:droplet>
	</ul>
</dsp:page>