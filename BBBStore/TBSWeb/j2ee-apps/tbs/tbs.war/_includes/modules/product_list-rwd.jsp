<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:importbean bean="/atg/repository/seo/CanonicalItemLink"/>

	<dsp:getvalueof var="count" param="BBBProductListVO.bbbProductCount" />
	<dsp:getvalueof var="CategoryId" param="CatalogRefId" />
	<dsp:getvalueof var="promoSC" param="promoSC"/>
	<dsp:getvalueof var="plpGridSize" param="plpGridSize"/>
	<dsp:getvalueof param="browseSearchVO" var="browseSearchVO"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof param="type" var="searchType"/>
	<dsp:getvalueof param="keyword" var="keyword"/>
	<dsp:getvalueof var="appid" bean="Site.id" />

	<c:set var="compareProducts"><bbbc:config key="compareProducts" configName="FlagDrivenFunctions" /></c:set>
	<c:set var="prodImageAttrib" scope="request">class="productImage lazyLoad loadingGIF" src="${imagePath}/_assets/tbs_assets/img/ajax-loader.gif" data-lazyloadsrc</c:set>
	<c:set var="swatchImageAttrib" scope="request">class="swatchImage lazySwatchLoad loadingGIF" src="${imagePath}/_assets/tbs_assets/img/ajax-loader.gif" data-lazyloadsrc</c:set>

	<%-- render normal classes/src attribute if disableLazyLoadS7Images is set to "true" --%>
	<c:if test="${disableLazyLoadS7Images eq true}">
		<c:set var="prodImageAttrib" scope="request">class="productImage noImageFound" src</c:set>
		<c:set var="swatchImageAttrib" scope="request">class="swatchImage noImageFound" src</c:set>
	</c:if>

	<dsp:form action="" method="post" id="upcgridForm">
	<dsp:droplet name="/com/bbb/search/droplet/TBSMinimalQuickViewDroplet">
		<dsp:param name="array" param="BBBProductListVO.bbbProducts" />
		<dsp:param name="elementName" value="productVO"/>
		<dsp:oparam name="outputStart">
			<ul class="small-block-grid-1 product-list plpListGridToggle">
		</dsp:oparam>
		<dsp:oparam name="outputEnd">
			</ul>
		</dsp:oparam>
		<dsp:getvalueof param="size" var="totalSize"/>
		<dsp:oparam name="output">
			<dsp:getvalueof param="count" var="currentCount"/>
			<dsp:getvalueof var="productId" param="productVO.productID" />

			<dsp:getvalueof var="productName" param="productVO.productName"/>
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
				<li class="categoryItems">
					<div class="row">
						<dsp:getvalueof var="seoUrl" param="productVO.seoUrl"/>
						<c:choose>
							<c:when test="${empty seoUrl}">
								<dsp:droplet name="CanonicalItemLink">
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
						<dsp:getvalueof var="pdpUrl" value="${finalUrl}" />
						<dsp:getvalueof var="portraitClass" value=""/>
						<dsp:getvalueof var="imageURL" param="productVO.imageURL"/>
						<c:set var="imageURL"><c:out value='${fn:replace(imageURL,"$146$","$229$")}'/></c:set>
						<c:if test="${portrait eq 'true'}">
							<dsp:getvalueof var="portraitClass" value="prodImgPortrait"/>
							<dsp:getvalueof var="imgHeight" value="200"/>
							<dsp:getvalueof var="imageURL" param="productVO.verticalImageURL"/>
						</c:if>
						<div class="small-6 medium-3 columns productContent">
							<div class="category-prod-img">
								<c:choose>
									<c:when test="${CategoryId eq null}">
										<dsp:a iclass="prodImg" page="${finalUrl}" title="${productName}">
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
									</c:when>
									<c:otherwise>
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
									<div id="quickViewModal" class="reveal-modal large" data-reveal>
									</div>
								</div>
							</div>
						</div>

						<div class="small-6 medium-4 columns">

							<c:choose>
								<c:when test="${rollupFlag eq 'true' || collectionFlag == '1'}">
									<c:set var="isMultiRollUpFlag" value="true" />
								</c:when>
								<c:otherwise>
									<c:set var="isMultiRollUpFlag" value="false" />
								</c:otherwise>
							</c:choose>

						<%--  End | R2.2.1 Story 131. Quick View on PLP Search Pages --%>

							<c:if test="${swatchFlag == '1'}">
								<div class="color-swatches">
									<dsp:droplet name="ForEach">
										<dsp:param name="array" param="productVO.colorSet" />
										<dsp:param name="elementName" value="colorSetVO" />

										<dsp:oparam name="outputStart">
											<ul class="color-swatch-grid listView">
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
											
											<li><a href="${colorProdUrl}" class="fl" title="${colorName}"  data-color-value="${colorValue}" data-color-param="${colorParam}"  data-main-image-src="${scene7Path}/${productUrl}">
													<img ${swatchImageAttrib}="${scene7Path}/${swatchUrl}" alt="${colorName}"/>
												</a></li>

										</dsp:oparam>
									</dsp:droplet>
								</div>
							</c:if>

							<h2 class="subheader">
								<dsp:getvalueof var="seoUrl" param="productVO.seoUrl"/>
								<c:choose>
									<c:when test="${empty seoUrl}">
										<dsp:droplet name="CanonicalItemLink">
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
								<c:set var="prodName">
									<dsp:valueof param="productVO.productName" valueishtml="true"/>
								</c:set>
								<c:choose>
									<c:when test="${CategoryId eq null}">
										<dsp:a  page="${finalUrl}"  title="${prodName}">
											<dsp:valueof param="productVO.productName" valueishtml="true" />
										</dsp:a>
									</c:when>
									<c:otherwise>
										<dsp:a  page="${finalUrl}?categoryId=${CategoryId}"  title="${prodName}">
											<dsp:valueof param="productVO.productName" valueishtml="true" />
										</dsp:a>
									</c:otherwise>
								</c:choose>
							</h2>


							<div class="price">
								<dsp:valueof param="productVO.priceRange" valueishtml="true"/>
								<%-- <span class="price-sale">$69.99</span>&nbsp;<span class="price-original">Was $89.99</span> --%>
							</div>
							<div class="marTop_10">
								<c:if test="${not empty searchType && searchType eq 'upc'}">
									<div class="prodQty fl">
										<div class="small-12 large-6 columns quantity no-padding-left">
											<c:set var="compareProducts" value="false"/>
											<dsp:getvalueof var="skuid" param="productVO.skuId"/>
											<dsp:getvalueof var="productID" param="productVO.productID"/>
											<dsp:getvalueof param="productVO.priceRange" var="omniPrice"/>
											<input type="hidden" name="prodId" class="_prodId addItemToRegis productId addItemToList" value="${productID}" data-change-store-submit="prodId" data-change-store-errors="required" data-change-store-internalData="true"/>
											<input type="hidden" name="skuId" value="${skuid}" class="addItemToRegis _skuId addItemToList changeStoreSkuId" data-change-store-submit="skuId" data-change-store-errors="required" data-change-store-internalData="true"/>
											<dsp:input bean="CartModifierFormHandler.productIds" type="hidden" paramvalue="productVO.productID" />
											<dsp:input bean="CartModifierFormHandler.catalogRefIds"  type="hidden" paramvalue="productVO.skuId" />
											<input type="hidden" name="price" value="${omniPrice}" class="addItemToList addItemToRegis" />
											<div class="qty-spinner">
												<a class="button minus secondary"><span></span></a>
												<input name="${skuid}" id="pqty${productID}" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="fl pqty qty addItemToRegis _qty itemQuantity addItemToList quantity-input" type="text" value="1"  data-change-store-submit="qty" data-change-store-errors="required digits nonZero" role="textbox" aria-required="true" aria-describedby="pqty${productID}" maxlength="2"  />
												<a class="button plus secondary"><span></span></a>
											</div>
										</div>
									</div>
									<div class="small-12 large-6 columns addToCart no-padding">
										<input type="submit" class="tiny button transactional" name="btnAddToCart" value="Add to Cart" onclick="rkg_micropixel('${appid}','cart')"  role="button" aria-pressed="false" />
									</div>
									<dsp:input bean="CartModifierFormHandler.queryParam"
												type="hidden"
												value="keyword=${searchTerm}&type=upc" /> 
							           <dsp:input bean="CartModifierFormHandler.fromPage"  type="hidden" value="productListRwd" />	
									<%-- <dsp:input bean="CartModifierFormHandler.addItemToOrderSuccessURL"  type="hidden" value="/tbs/search/upcresults.jsp?keyword=${searchTerm}&type=upc"/>
									<dsp:input bean="CartModifierFormHandler.addItemToOrderErrorURL"  type="hidden" value="/tbs/search/upcresults.jsp?keyword=${searchTerm}&type=upc" /> --%>
								</c:if>
								<div class="small-6 columns no-padding show-for-large-up qv-link">
									<c:if test="${empty searchType}">
										<dsp:a id="product_${productId}" iclass=" ${quickViewClass} quick-view" href="${contextPath}/browse/quickview_product_details.jsp">
											<dsp:param name="productId" value="${productId}" />
											<dsp:param name="pdpLink" value="${finalUrl}" />
											<dsp:param name="CategoryId" value="${CategoryId}" />
											<dsp:param name="previousProductId" value="${previousProductId}" />
											<dsp:param name="nextProductId" value="${nextProductId}" />
											<bbbl:label key="lbl_product_quick_view" language="${pageContext.request.locale.language}" />
										</dsp:a>
									</c:if>
								</div>
								<c:if test="${compareProducts eq true}">
									<dsp:getvalueof var="productId" param="productVO.productID"/>
									<dsp:getvalueof var="inCompareDrawer" param="productVO.inCompareDrawer"/>
									<c:choose>
										<c:when test="${inCompareDrawer eq true}">
											<div class="compare-link small-6 columns no-padding">
												<label for="compareChkTxt_${productId}" class="compareChkTxt inline-rc checkbox">
													<input name="Compare" id="compareChkTxt_${productId}" class="compareChkTxt" type="checkbox" value="compareItem" data-productid="${productId}" data-imgsrc="${scene7Path}/${imageURL}" checked = "true" />
													<span></span>
													<bbbl:label key="lbl_compare_product" language="${pageContext.request.locale.language}" />
												</label>
											</div>
										</c:when>
										<c:otherwise>
											<div class="compare-link small-6 columns no-padding">
												<label for="compareChkTxt_${productId}" class="inline-rc checkbox">
													<input name="Compare" id="compareChkTxt_${productId}" data-imgsrc="${scene7Path}/${imageURL}" class="compareChkTxt" type="checkbox" value="compareItem" data-productid="${productId}" />
													<span></span>
													<bbbl:label key="lbl_compare_product" language="${pageContext.request.locale.language}" />
												</label>
											</div>
										</c:otherwise>
									</c:choose>
								</c:if>
							</div>
						</div>
						<div class="medium-5 columns show-for-medium-up prod-list-desc">
							<div class="p-secondary product-list-description">
								<dsp:valueof param="productVO.productName" valueishtml="true"/>
							</div>
							<a class="button secondary" href="${contextPath}${finalUrl}">Learn More</a>
							
							<div>
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
					</div>
				</li>
		</dsp:oparam>
	</dsp:droplet>
	<c:if test="${not empty searchType && searchType eq 'upc'}">
		<hr>
		<div class="small-12 large-3 large-right columns">
		<dsp:input name="submit" bean="CartModifierFormHandler.addMultipleItemsToOrder" type="submit" iclass="button expand transactional" value="Add All Items To Cart"/>
		<dsp:input bean="CartModifierFormHandler.queryParam"
												type="hidden"
												value="keyword=${searchTerm}&type=upc" /> 
							           <dsp:input bean="CartModifierFormHandler.fromPage"  type="hidden" value="productListRwd" />	
		<%-- <dsp:input bean="CartModifierFormHandler.addMultipleItemsToOrderSuccessURL"  type="hidden" value="/tbs/search/upcresults.jsp?keyword=${searchTerm}&type=upc" />
		<dsp:input bean="CartModifierFormHandler.addMultipleItemsToOrderErrorURL"  type="hidden" value="/tbs/search/upcresults.jsp?keyword=${searchTerm}&type=upc" /> --%>
		</div>
	</c:if>
	</dsp:form>
	<script type="text/javascript">
			$(document).ready(function(){
				$(".quick-view").attr("data-reveal-id","quickViewModal");
				$(".quick-view").attr("data-reveal-ajax","true");
				$(document).foundation('reflow');
			});
	</script>
	<dsp:getvalueof bean="CartModifierFormHandler.collectionQty" var="collectionQty"/>
	<c:if test="${collectionQty > 0 }">
		<script type="text/javascript">
			var qty = ${collectionQty};
			$(function(){
				if(qty == 1){
					$('#infoModal').html("<h2>"+ qty +"&nbsp;new item added to your cart</h2> <a class='close-reveal-modal'>&#215;</a>").foundation('reveal', 'open');
				} else {
					$('#infoModal').html("<h2>"+ qty +"&nbsp;new items added to your cart</h2> <a class='close-reveal-modal'>&#215;</a>").foundation('reveal', 'open');
				}
			});
		</script>
	</c:if>
</dsp:page>
