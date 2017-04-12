<%-- R2.2 Story - 178-a4 Product Comparison Page | Start--%>
<dsp:page>
    
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/compare/ProductComparisonDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductComparisonList"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/AddItemToGiftRegistryDroplet" />
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/commerce/catalog/comparison/TableInfo"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/SKUDetailDroplet"/>
	<dsp:getvalueof id="contextroot" idtype="java.lang.String" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="currentSiteId" bean="/atg/multisite/Site.id" />
	<jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request"/>
	<c:set target="${placeHolderMap}" property="contextPath" value="${contextPath}"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
	<%-- Get the last navigated Search/ PLP page url from the session --%>
	<dsp:getvalueof var="url" bean="ProductComparisonList.url"/>
	<dsp:getvalueof var="skuIdfromURL" param="skuId" /> 
	<c:set var="omniPersonalizeButtonClick">
		<bbbl:label key='omni_personalize_button_click' language="${pageContext.request.locale.language}" />
	</c:set>
	
	<c:set var="pageWrapper" value="compareProducts useScene7" scope="request" />
	<c:set var="compare" value="compare" scope="request" />
	<c:set var="saSrc"><bbbc:config key="socialAnnexURL" configName="ThirdPartyURLs" /></c:set>
	<c:set var="lblSflReviews"><bbbl:label key="lbl_sfl_reviews" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblOfThe"><bbbl:label key="lbl_accessibility_of_the" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblAboutThe"><bbbl:label key="lbl_accessibility_about_the" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblSaveThe"><bbbl:label key="lbl_accessibility_save_the" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblFuturePurchase"><bbbl:label key="lbl_accessibility_future_purchase" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblFindThe"><bbbl:label key="lbl_accessibility_find_the" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblStoreNear"><bbbl:label key="lbl_accessibility_store_near_you" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblForThe"><bbbl:label key="lbl_accessibility_for_the" language="${pageContext.request.locale.language}" /></c:set>
	
	
	<%-- Redirect to home page if user has not visited any PLP/ Search page in the session --%>
	<c:if test="${empty url}">
		<c:redirect url="/" />
	</c:if>
	
	<bbb:pageContainer>
	<jsp:attribute name="pageWrapper">${pageWrapper} useCertonaAjax</jsp:attribute>
	<jsp:attribute name="section">${compare}</jsp:attribute>
		<jsp:body>
			<c:set var="BedBathUSSite">
				<bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
			</c:set>
			<c:set var="BuyBuyBabySite">
				<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
			</c:set>
			<c:set var="BedBathCanadaSite">
				<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
			</c:set>
			<c:set var="enableLTLRegForSite">
				<bbbc:config key="enableLTLRegForSite" configName="FlagDrivenFunctions" />
			</c:set>
			
			<%--<div id="content" class="container_12 clearfix" role="main">
				<div class="breadcrumbs grid_12">
					<a href="${contextroot}"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></a>
				</div>
			</div>--%>
			<input type="hidden" id="omniPageTypeVar46" value="product comparison page" name="omniPageTypeVar46" />
			<input type="hidden" id="comparisonPage" value="true" name="comparisonPage" />
			
			<input type="hidden" id="enableKatoriFlag" name="enableKatoriFlag" value="${enableKatoriFlag}">
			<div id="subHeader" class="container_12 clearfix">
				<div class="grid_12 clearfix share">	
					<h1 class="fl"><bbbl:label key='lbl_compare_products' language="${pageContext.request.locale.language}" /></h1>
					<h2 class="fl marLeft_10 find-text headerMessage"><bbbl:label key='lbl_compare_products_title' language="${pageContext.request.locale.language}" /></h2>
					<a href="/store/compare/product_comparison.jsp" class="print fr headerCommunication" title="Print"></a>
					
				</div>
			</div>
			
			<div id="content" class="container_12 clearfix">
				<div class="compareBuckets clearfix">					
					
					<%-- This droplet iterates over the session Comparison List products and update their VO and return true output if successfull--%>
					<dsp:droplet name="ProductComparisonDroplet">
						<dsp:oparam name="output">
							<dsp:getvalueof var="isCompareSuccess" param="compareProductsSuccess"/>			
						</dsp:oparam>
						<dsp:oparam name="error">
							<c:set var="isCompareSuccess" value="${false}"/>
						</dsp:oparam>
					</dsp:droplet>
					
					<c:if test="${isCompareSuccess}">
						<dsp:getvalueof var="productsList" bean="ProductComparisonList.items" vartype="java.util.List"/>
					</c:if>
					<c:set var="size" value="${fn:length(productsList)}"/>
					
					<dsp:droplet name="AddItemToGiftRegistryDroplet">
						<dsp:param name="siteId" value="${appid}"/>
					</dsp:droplet>
					<dsp:getvalueof bean="SessionBean.values" var="sessionMapValues"/>
					<dsp:getvalueof value="${sessionMapValues.registrySkinnyVOList}" var="registrySkinnyVOList"/>
					<dsp:getvalueof var="sizeValue" value="${fn:length(registrySkinnyVOList)}"/>
					
					<dsp:droplet name="ForEach">
						<dsp:param name="array" value="${productsList}" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="dataIndex" param="count"/>
							<dsp:getvalueof var="singleSkuId" param="element.defaultSkuId"/>
							<dsp:getvalueof var="EmailAlertOn" param="element.emailAlertOn"/>
							<dsp:getvalueof var="productId" param="element.productId"/>
							<dsp:getvalueof var="isProdActive" param="element.productActive"/>
							<dsp:getvalueof var="productName" param="element.productName"/>
							<dsp:getvalueof var="mediumImage" param="element.mediumImagePath"/>
							<dsp:getvalueof var="thumbnailImagePath" param="element.thumbnailImagePath"/>
							<dsp:getvalueof var="bvReviews" param="element.reviews"/>
							<dsp:getvalueof var="skuID" param="element.skuId"/>
							<dsp:getvalueof var="salePriceRangeDescription" param="element.salePriceRangeDescription"/>
							<dsp:getvalueof var="isCollection" param="element.collection"/>
							<dsp:getvalueof var="isMultiSku" param="element.multiSku"/>
							<dsp:getvalueof var="inStock" param="element.inStock"/>
							<dsp:getvalueof var="isLTLProduct" param="element.ltlProduct"/>
							<dsp:getvalueof var="intlRestricted" param="element.intlRestricted"/>
							<dsp:getvalueof var="customizationCode" param="element.customizationCode"/>
							<dsp:getvalueof var="customizableRequired" param="element.customizableRequired"/>
							<dsp:getvalueof var="customizationOffered" param="element.customizationOffered"/>
							<dsp:getvalueof var="personalizationType" param="element.personalizationType"/>
							<dsp:getvalueof var="customizationCodeValues" param="element.customizationCodeValues"/>
							<dsp:getvalueof var="vendorName" param="element.vendorInfoVO.vendorName"/>							
							<dsp:getvalueof var="isdynamicPriceProd" param="element.dynamicPricingProduct" />
							<dsp:getvalueof var="priceLabelCodeProd" param="element.priceLabelCode" />
							<dsp:getvalueof var="inCartFlagProd" param="element.inCartFlag" />
							<dsp:getvalueof var="priceRangeDescription" param="element.priceRangeDescription" />
							<dsp:getvalueof var="element" param="element" />
							<c:set var="mediumImage"><c:out value='${fn:replace(mediumImage,"$146$","$229$")}'/></c:set>
							<c:if test="${inStock eq null}">
								<c:set var="inStock" value="true" />
							</c:if>
							<c:set var="customizableCodes" value="${customizationCode}" scope="request"/>
									
									<c:set var="customizeCTACodes" scope="request">
										<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys" />
									</c:set>
							<c:choose>
									<c:when test="${not empty singleSkuId}">
										<dsp:getvalueof var="skuID" value="${singleSkuId}"/>
									</c:when>
									<c:otherwise>
										<dsp:getvalueof var="skuID" value="${skuID}"/>
									</c:otherwise>
							</c:choose>  
							<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
								<dsp:param name="id" param="element.productId"/>
								<dsp:param name="itemDescriptorName" value="product" />
								<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
								</dsp:oparam>
							</dsp:droplet>
							
							<div class="grid_3 columnData">
							<c:set var="skuIdCompare"> sku_${skuID}</c:set>
							<dsp:form iclass="compareProductForm" name="compareProductForm" method="post">
								<a href="javascript:void(0);" class="removeLink noprint"><strong><bbbl:label key='lbl_remove_product' language="${pageContext.request.locale.language}" /></strong></a>
								<div id="${skuIdCompare}" class="registryDataItemsWrap listDataItemsWrap clearfix" data-skuid="${skuID}">
									<input name="isCustomizationRequired" value="${customizableRequired}" type="hidden"/>
									<input type="hidden" name="isLtlItem" value="${isLTLProduct}"/>
									<c:choose>
										<c:when test="${isProdActive}">
											<c:choose>
												<c:when test="${not empty mediumImage}">
		                                                                                         <dsp:a page="${finalUrl}?skuId=${skuID}" iclass="prodImg block" title="${productName}">
		                                                                                                <img height="229" width="229" alt="${productName}" src="${scene7Path}/${mediumImage}" class="noImageFound"/>
		                                                                                         </dsp:a>
		                                                                                  </c:when>
		                                                                                  <c:otherwise>
		                                                                                         <dsp:a page="${finalUrl}?skuId=${skuID}" iclass="prodImg block" title="${productName}">
		                                                                                                <img height="229" width="229" alt="${productName}" src="${imagePath}/_assets/global/images/no_image_available.jpg" class="noImageFound"/>
		                                                                                         </dsp:a>
		                                                                                  </c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<c:choose>
												<c:when test="${not empty mediumImage}">
													<img height="229" width="229" alt="${productName}" src="${scene7Path}/${mediumImage}" class="noImageFound"/>
												</c:when>
												<c:otherwise>
													<img height="229" width="229" alt="${productName}" src="${imagePath}/_assets/global/images/no_image_available.jpg" class="noImageFound"/>
												</c:otherwise>
											</c:choose>
										</c:otherwise>
									</c:choose>
									
									<ul data-bucket-index="bucket_${dataIndex}" class="prodInfoGrid clearfix grid_3 alpha omega marTop_10">
										<li class="prodDescription">
											<div class="prodName">
											<c:choose>
												<c:when test="${isProdActive}">
													<dsp:a page="${finalUrl}?skuId=${skuID}"><dsp:valueof param="element.productName" valueishtml="true"/></dsp:a>
												</c:when>
												<c:otherwise>
													<span class="disableText"><dsp:valueof param="element.productName" valueishtml="true"/></span>
												</c:otherwise>
											</c:choose>
											</div>
											
											<div class="ratingsBlock noprint"> 
											<c:if test="${BazaarVoiceOn}">
												<c:choose>
													<c:when test="${empty bvReviews}">
														<dsp:getvalueof var="totalReviewCount" value="0"></dsp:getvalueof>
													</c:when>
													<c:otherwise>
														<dsp:getvalueof var="reviews" value="${bvReviews.totalReviewCount}"/>
														<dsp:getvalueof var="ratings" value="${bvReviews.averageOverallRating}" vartype="java.lang.Integer"/>
														<dsp:getvalueof var="rating" value="${ratings * 10}" vartype="java.lang.Integer"/>
														<dsp:getvalueof var="totalReviewCount" value="${bvReviews.totalReviewCount}"></dsp:getvalueof>
													</c:otherwise>
											 </c:choose>
											 <c:choose>
												     <c:when test="${ratings ne null && ratings ne '0.0'}">
												     
												     <c:choose>
															<c:when test="${isProdActive}">
																<span title="${ratings}" class="prodReviewGrid fl prodReview clearfix prodReview<fmt:formatNumber value="${rating}" pattern="#0" />"></span>
																<a class="block" href="${pageContext.request.contextPath}${finalUrl}?skuId=${skuID}&showRatings=true" role="link" aria-label="${totalReviewCount} ${lblSflReviews} ${lblForThe} ${productName}">${totalReviewCount} <bbbl:label key="lbl_sfl_reviews" language="<c:out param='${language}'/>"/></a>
															</c:when>
															<c:otherwise>
																<span title="${ratings}" class="prodReviewGrid fl prodReview clearfix prodReview<fmt:formatNumber value="${rating}" pattern="#0" />"></span>
																${totalReviewCount} <bbbl:label key="lbl_sfl_reviews" language="<c:out param='${language}'/>"/>
															</c:otherwise>
															</c:choose>
													</c:when>
													<c:otherwise>
												    <span class="prodReviewGrid fl prodReview clearfix prodReview">
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
														 
														</span>		
														<a href="${contextroot}${finalUrl}?categoryId=${CategoryId}&writeReview=true" title="${writeReviewLink}" role="link" aria-label="${writeReviewLink} ${lblAboutThe} ${productName}">${writeReviewLink}</a>
												</c:otherwise>
												</c:choose>
												</c:if>
											</div>
										</li>
										
										<li class="outStockMsg clearfix padTop_5 padBottom_10">
										<c:choose>
											   <c:when test="${not empty singleSkuId}">
											         <dsp:getvalueof var="skuID" value="${singleSkuId}"/>
											    </c:when>
											     <c:otherwise>
											      <dsp:getvalueof var="skuID" value="${skuID}"/>
											    </c:otherwise>
											</c:choose>  
											<c:if test="${not isCollection && not isMultiSku}">
											  <div class="message">
												    <input type="hidden" name="oosProdId" value="${productId}" />
														 <c:choose>
															<c:when test="${inStock==true}">
																<input type="hidden" value="" name="oosSKUId" class="_oosSKUId"/>
															</c:when>
															<c:otherwise>
																<input type="hidden" value="${skuID}" name="oosSKUId" class="_oosSKUId"/>
															</c:otherwise>
														</c:choose>
														
													<c:if test="${inStock eq false}">
														<c:choose>
															<c:when test="${((null eq EmailAlertOn) || (EmailAlertOn==true)) && (inStock==false)}">
																<div class="error noMarBot"><bbbl:label key='lbl_pdp_product_notavailable_shipping' language="${pageContext.request.locale.language}" /></div>
																<div class="info"><a class="info lnkNotifyOOS" href="#"><bbbl:label key='lbl_pdp_product_notify_item_available' language="${pageContext.request.locale.language}" /> &raquo;</a></div>
															</c:when>
															<c:otherwise>
																<div class="error noMarBot"><bbbl:label key='lbl_pdp_product_notavailable_shipping' language="${pageContext.request.locale.language}" /></div>
															</c:otherwise>
														</c:choose>
													</c:if>
												</div>
											</c:if>
										</li>
										
										<li class="prodpriceGridList marBottom_20 clearfix">
										<div class="clearfix prodpriceGrid cb <c:if test="${(inStock==false && not empty skuID) || isProdActive==false}">priceQuantityNotAvailable</c:if>">
											<dsp:getvalueof var="oldPriceRange" value=""/>
											<c:if test="${not isCollection && not isMultiSku}">
												<dsp:getvalueof var="oldPriceRange" value="oldPriceRange"/>
												<span class="spinner">
													<%--label id="lblprodCompDescQty" for="prodCompDescQty" class="txtOffScreen" for="prodCompDescQty"><bbbl:label key="lbl_decrease_quantity" language="${pageContext.request.locale.language}" /></label --%>
                                   					<span class="incDec visuallyhidden" aria-live="assertive" aria-atomic="true"></span>
                                   					<a href="#" class="scrollDown down" id="prodCompDescQty" aria-hidden="false" title="<bbbl:label key='lbl_decrease_quantity' language="${pageContext.request.locale.language}" />"></a>
                                   					<input id="quantity" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="fl addItemToRegis _qty itemQuantity addItemToList" type="text" name="qty" value="1" maxlength="2" data-change-store-submit="qty" data-change-store-errors="required digits nonZero" aria-required="true" aria-describedby="quantity" />
													<%--label id="lblprodCompIncQty" class="txtOffScreen" for="prodCompIncQty"><bbbl:label key="lbl_increase_quantity" language="${pageContext.request.locale.language}" /></label --%>
													<a href="#" class="scrollUp up" id="prodCompIncQty" aria-hidden="false" title="<bbbl:label key='lbl_increase_quantity' language="${pageContext.request.locale.language}" />"></a>
												</span>
											</c:if>
												<span class="prodPrice ">
												<c:choose>
													<c:when test="${not empty skuID}">
													<dsp:droplet name="SKUDetailDroplet">
													<dsp:param name="siteId" value="${currentSiteId}"/>
													<dsp:param name="skuId" value="${skuID}"/>
														<dsp:oparam name="output">
															<dsp:getvalueof var="skuVO" param="pSKUDetailVO"/>
															<c:if test="${not empty skuVO}">
																<dsp:getvalueof var="priceLabelCodeSKU" value="${ skuVO.pricingLabelCode}" />
																<dsp:getvalueof var="inCartFlagSKU" value="${skuVO.inCartFlag}" />
																
															</c:if>
														</dsp:oparam>
													</dsp:droplet>
														<dsp:include page="../browse/product_details_price.jsp">
																<dsp:param name="product" value="${productId}"/>
																<dsp:param name="sku" value="${skuID}"/>
																<dsp:param name="priceLabelCodeSKU" value="${priceLabelCodeSKU}"/>
																<dsp:param name="inCartFlagSKU" value="${inCartFlagSKU}"/>
															</dsp:include>
															<c:if test="${skuIdfromURL == skuID}">
																<input id="certonaPrice" type="hidden" value="${certonaPrice}" />
															</c:if>
													    </c:when>
														<c:otherwise>
															<dsp:include page="/browse/browse_price_frag.jsp">
																<dsp:param name="priceLabelCode" value="${priceLabelCodeProd}" />
																<dsp:param name="inCartFlag" value="${inCartFlagProd}" />
																<dsp:param name="salePrice" value="${salePriceRangeDescription}" />
																<dsp:param name="listPrice" value="${priceRangeDescription}" />
																<dsp:param name="isdynamicPriceEligible" value="${isdynamicPriceProd}" />
																<dsp:param name="isFromPDP" value="false" />
															</dsp:include>
													   </c:otherwise>
												</c:choose>
												</span>
											</div>
									<%--Katori integration on Product Compare page as a part of BPSI-2430 changes start --%>
									<c:set var="customizableCodes" value="${customizationCode}" scope="request"/>
									
									<c:set var="customizeCTACodes" scope="request">
										<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys" />
									</c:set>
									
									<c:choose>
										<c:when test="${not empty customizableCodes && fn:contains(customizeCTACodes, customizableCodes)}">
											<c:set var="personalizationTxt">
												<bbbl:label key="lbl_customize_this" language="${pageContext.request.locale.language}"/>
											</c:set>
											<c:set var="customizeAttr" value="customize"/>
											<c:set var="personalizationReqTxt">
												<bbbl:label key='lbl_customization_required' language="${pageContext.request.locale.language}" />
											</c:set>
										</c:when>
										<c:otherwise>
											<c:set var="personalizationTxt">
												<bbbl:label key="lbl_personalize_this" language="${pageContext.request.locale.language}"/>
											</c:set>
											<c:set var="customizeAttr" value=""/>
											<c:set var="personalizationReqTxt">
												<bbbl:label key='lbl_personalization_required' language="${pageContext.request.locale.language}" />
											</c:set>
										</c:otherwise>
									</c:choose>
									
									<c:choose>
											<c:when test="${ (customizableRequired || customizationOffered) && not empty customizationCodeValues && !isInternationalCustomer}">
												<div class="personalizationOffered clearfix cb fl">
													<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="${contextPath}${finalUrl}?openKatori=true&skuId=${skuID}"</c:otherwise></c:choose> <c:choose><c:when test="${not enableKatoriFlag}">class='personalize ${customizeAttr} disabled'</c:when><c:otherwise>class='personalize ${customizeAttr}'</c:otherwise></c:choose> role="button" data-custom-vendor="${vendorName}" data-product="${productId}" data-sku="${skuID}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
														${personalizationTxt}
													</a>
										 			<c:choose>
						      						 <c:when test="${enableKatoriFlag}">
						      						 	<div class="personalizeDetailSection">
						      						 	<c:if test="${personalizationType =='PB'}">
															<span class="feeApplied"><bbbl:label key='lbl_PB_Fee_detail' language="${pageContext.request.locale.language}" /></span>	
														</c:if>
														<c:if test="${personalizationType =='PY'}">
														<span class="feeApplied">	<bbbl:label key='lbl_PY_Fee_detail' language="${pageContext.request.locale.language}" /></span>	
														</c:if>
														<c:if test="${personalizationType =='CR'}">
														<span class="feeApplied">	<bbbl:label key='lbl_CR_Fee_detail' language="${pageContext.request.locale.language}" /></span>	
														</c:if>
															<span class="personalizationDetail">
															<bbbl:label key='lbl_cart_personalization_detail' language="${pageContext.request.locale.language}" />
														</span>
														</div>
														<c:if test="${customizableRequired}"> 
	                                                         <div class="personalizeToProceed">
	                                                         	${personalizationReqTxt}
	                                                           </div>
														</c:if>
														</c:when>
														<c:otherwise>
															<span><bbbl:label key='lbl_pdp_personalization_unavailable' language="${pageContext.request.locale.language}" /></span>
														</c:otherwise>
													</c:choose>
												</div>
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${isInternationalCustomer}">
															<c:choose>
																<c:when test="${(customizableRequired || customizationOffered) && not empty customizationCodeValues}">
																	<div class="personalizationOffered clearfix cb fl">
																		<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="#"</c:otherwise></c:choose> class="personalize ${customizeAttr} disabled" role="button" data-product="${productId}" data-sku="${pDefaultChildSku}" data-custom-vendor="${vendorName}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
																			${personalizationTxt}
																		</a>
																		<span class="personalizationIntlShippingMsg"><bbbt:textArea key="txt_pdp_per_unavailable_intl" language="${pageContext.request.locale.language}" /></span>
																	</div>
																</c:when>
																<c:otherwise>
																	<div class="personalizationOffered clearfix cb fl hidden ">
																		<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="#"</c:otherwise></c:choose> class="personalize ${customizeAttr} disabled" role="button" data-product="${productId}" data-sku="${pDefaultChildSku}" data-custom-vendor="${vendorName}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
																			${personalizationTxt}
																		</a>
																		<span class="personalizationIntlShippingMsg"><bbbt:textArea key="txt_pdp_per_unavailable_intl" language="${pageContext.request.locale.language}" /></span>
																	</div>
																</c:otherwise>
															</c:choose>	
													</c:when>
													<c:otherwise>
												
													<div class="personalizationOffered clearfix cb fl hidden">
														<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="${contextPath}${finalUrl}?openKatori=true&skuId=${skuID}"</c:otherwise></c:choose>  class="personalizeBtnCompare ${customizeAttr} <c:if test='${not enableKatoriFlag}'>personalize disabled </c:if>" role="button" data-custom-vendor="${vendorName}" data-product="${productId}" data-sku="${skuID}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
															${personalizationTxt}
														</a>
														<c:choose>
															<c:when test="${enableKatoriFlag}">
																<div class="personalizeDetailSection">
																<span class="feeAppliedPB hidden"><bbbl:label key='lbl_PB_Fee_detail' language="${pageContext.request.locale.language}" /></span>	
																<span class="feeAppliedPY hidden">	<bbbl:label key='lbl_PY_Fee_detail' language="${pageContext.request.locale.language}" /></span>	
																<span class="feeAppliedCR hidden"><bbbl:label key='lbl_CR_Fee_detail' language="${pageContext.request.locale.language}" /></span>
																<span class="personalizationDetail">
																	<bbbl:label key='lbl_cart_personalization_detail' language="${pageContext.request.locale.language}" />
																</span>
																</div>	
																<c:if test="${customizableRequired}"> 
			                                                         <div class="personalizeToProceed">
			                                                         	${personalizationReqTxt}
			                                                           </div>
																</c:if>
															</c:when>
															<c:otherwise>
																<span><bbbl:label key='lbl_pdp_personalization_unavailable' language="${pageContext.request.locale.language}" /></span>
															</c:otherwise>
														</c:choose>	
													</div>
												</c:otherwise>
												</c:choose>
											</c:otherwise>
									</c:choose>
									<%--Katori integration on Product Compare page as a part of BPSI-2430 changes start end --%>	
										</li>
									
										<li class="clearfix marBottom_10 padTop_20px clearfix">
											<c:set var="chooseOptionBtn"><bbbl:label key='lbl_compare_btn_choose_options' language="${pageContext.request.locale.language}" /></c:set>
											<c:choose>
												<c:when test="${isCollection}">
												
													<div class="button button_active button_active_orange button_disabled">
														<input type="button" role="button" name="btnAddToCart" value="${chooseOptionBtn}" disabled="disabled" <c:if test="${isProdActive==true}">class="enableOnDOMReady showOptionsCollection"</c:if>/>
													</div>
												</c:when>
												<c:when test="${isMultiSku}">
												
													<div class="button button_active button_active_orange button_disabled">
														<input type="button" role="button" name="btnAddToCart" value="${chooseOptionBtn}" disabled="disabled" <c:if test="${isProdActive==true}">class="enableOnDOMReady showOptionMultiSku"</c:if>/>
													</div>
												</c:when>
												<c:otherwise>
													<div class="addToCart">
														<div class="button button_active button_active_orange button_disabled">
															<input type="submit" role="button" name="btnAddToCart" value="Add to cart" disabled="disabled" <c:if test="${(customizableRequired == false) && (inStock==true && isProdActive==true) &&  !(isInternationalCustomer && intlRestricted)}">class="enableOnDOMReady"</c:if>/>
														</div>
													</div>
													
												</c:otherwise>
											</c:choose>
											<%-- Data to submit on add to cart/ Find In store --%>
											<input type="hidden" class="addItemToList addItemToRegis itemQuantity" value="1" name="qty" data-change-store-submit="qty" />
											<input type="hidden" data-change-store-internaldata="true" data-change-store-errors="required" data-change-store-submit="prodId" value="${productId}" class="_prodId addItemToRegis productId addItemToList" name="prodId"/>
											<c:if test="${not empty skuID}">
												<input type="hidden" data-change-store-internaldata="true" data-change-store-errors="required" data-change-store-submit="skuId" class="addItemToRegis _skuId addItemToList changeStoreSkuId" value="${skuID}" name="skuId"/>
											</c:if>
											<input type="hidden" name="price" value="${omniPrice}" class="addItemToList addItemToRegis" />
											<input type="hidden" data-change-store-storeid="storeId" value="" name="storeId" class="addToCartSubmitData"/>
											<input type="hidden" name="parentProdId" value="${productId}" class="addItemToList addItemToRegis" />
											<input type="hidden" value="" class="addToCartSubmitData" name="bts" data-change-store-storeid="bts"/>
											<input type="hidden" value="true" class="addItemToList addItemToRegis" name="fromComparisonPage"/>
											<input type="hidden" value="" class="addItemToList addItemToRegis" name="altNumber"/>
											<input type="hidden" value="" class="addItemToList addItemToRegis" name="selServiceLevel">
											
											<c:choose>
												<c:when test="${empty thumbnailImagePath}">
													<img class="hidden productImage" height="83" width="83" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${productName}" />
												</c:when>
												<c:otherwise>
													<img class="hidden productImage noImageFound" height="83" width="83" src="${scene7Path}/${thumbnailImagePath}" alt="${productName}" />
												</c:otherwise>
											</c:choose>
											<span class="productDetails hidden"><dsp:valueof param="element.productName" valueishtml="true"/></span>
										</li>
										
										<c:if test="${not isCollection && not isMultiSku && isProdActive}">
											<li class="multiButtonWrapper clearfix noMarBot noprint">
																								
												<%-- Add To Registry Link logic on comparison page | Start --%>
													<dsp:include page="../addgiftregistry/add_item_gift_registry_comparison.jsp">
														<dsp:param name="isCustomizationRequired" value="${customizableRequired}"/>
														<dsp:param name="isInternationalCustomer" value="${isInternationalCustomer}"/>
														<dsp:param name="ltlFlag" value="${isLTLProduct}"/>
														<dsp:param name="wishListItemId" value="${wishListItemId}"/>
														<dsp:param name="skuID" value="${skuID}"/>
														<dsp:param name="prodID" value="${prodID}"/>
														<dsp:param name="quantity" value="${quantity}"/>
														<dsp:param name="itemPrice" value="${itemPrice}"/>
														<dsp:param name="kickStarterPage" value="${kickStarterPage}"/>
													</dsp:include>	
												<%-- Add To Registry Link logic on comparison page | End --%>

												<%-- Save for later Link on comparison page --%>												
												<div id="btnAddToList" class="addToList fl padTop_10 <c:if test="${customizableRequired}">disabled opacity_1</c:if>">
		                                                                                         <%--img height="16" width="16" src="/_assets/global/images/compare/plus.png" class="noImageFound"/--%>
		                                                                                      <c:choose>
		                                                                                      	<c:when test="${customizableRequired}">
		                                                                                      		<a class="ptrEvtNone disableText" href="javascript:void(0);" onclick="rkg_micropixel('${appid}','wish')"><bbbl:label key='lbl_compare_save_for_later' language="${pageContext.request.locale.language}" /></a>
		                                                                                      	</c:when>
		                                                                                      	<c:otherwise>
		                                                                                         <a href="javascript:void(0);" onclick="rkg_micropixel('${appid}','wish')"><bbbl:label key='lbl_compare_save_for_later' language="${pageContext.request.locale.language}" /></a> 
		                                                                                      	</c:otherwise>
		                                                                                      </c:choose>   
		                                                                                  </div>
                                                                                  
		                                                                                  <%-- Find In Store Link logic on comparison page | Start --%>
		                                                                                  <c:if test="${MapQuestOn}">
		                                                                                         <dsp:getvalueof var="isBopusExcluded" param="element.bopusExcluded"/>
		                                                                                         <c:choose>
		                                                                                                <c:when test="${isBopusExcluded || (isLTLProduct==true)}">
		                                                                                                       <div class="changeStoreDisable fr padTop_10 disableText">
		                                                                                                              <%--img height="14" width="14" src="/_assets/global/images/compare/location.png" class="noImageFound"/--%>
		                                                                                                              <a href="javascript:void(0);" aria-disabled="true"><bbbl:label key='lbl_compare_find_in_store' language="${pageContext.request.locale.language}" /></a>
		                                                                                                       </div>
		                                                                                                </c:when>
		                                                                                                 <%--   change for defect no:BPS-439 	  --%>
		                                                                                                <c:when test="${customizableRequired || isInternationalCustomer eq true}">
																										
																											 <div class="changeStoreDisable fr padTop_10 disableText">
		                                                                                                              <%--img height="14" width="14" src="/_assets/global/images/compare/location.png" class="noImageFound"/--%>
		                                                                                                              <a href="javascript:void(0);" aria-disabled="true"><bbbl:label key='lbl_compare_find_in_store' language="${pageContext.request.locale.language}" /></a>
		                                                                                                       </div>
																										</c:when>
		                                                                                                <c:otherwise>
		                                                                                                <div class="changeStore fr padTop_10">
		                                                                                                      <%--img height="14" width="14" src="/_assets/global/images/compare/location.png" class="noImageFound"/--%>
		                                                                                                       <a href="javascript:void(0);"onclick="pdpOmnitureProxy('${productId}', 'findinstore');rkg_micropixel('${appid}','findstore');"><bbbl:label key='lbl_compare_find_in_store' language="${pageContext.request.locale.language}" /></a>
		                                                                                                </div>
		                                                                                                </c:otherwise>
		                                                                                         </c:choose>
		                                                                                  </c:if>				
												<%-- Find In Store Link logic on comparison page | End --%>
											</li>
									</c:if>
										<c:if test="${(isInternationalCustomer && intlRestricted)}">
										   <div class="notAvailableIntShipMsg plpIntShipMsg cb clearfix padTop_10"><bbbl:label key='lbl_pdp_intl_restricted_message' language="${pageContext.request.locale.language}" /></div>
									    </c:if>
									</ul>
								</div>
							</dsp:form>
							</div>
						</dsp:oparam>
					</dsp:droplet>
					
					<%-- Display empty slots on comparison page if products are less than 4 | Start--%>
					
					<%-- Sprint1(R2.2.1)| Sticky image and title on the comparison page | [Start] --%>
					<c:set var="emptySlotSize" value="${4-size}"/>
					<%-- Sprint1(R2.2.1)| Sticky image and title on the comparison page | [End] --%>
					
					<c:forEach var="i" begin="1" end="${emptySlotSize}">
						<div class="grid_3 columnData">
							<form class="compareProductForm" name="compareProductForm" actions="#" method="post">
								<div class="registryDataItemsWrap listDataItemsWrap clearfix noProductToCompare">
									<a href="${url}" class="prodImg block noprint" title="Add Another Item">
										<img height="229" width="229" alt="Add Another Item" src="/_assets/global/images/copare-img-placeholder.png" class="noImageFound"/>
									</a>
								</div>
							</form>
						</div>
					</c:forEach>
					</div>
					<div class="bucketPlaceholder hidden">
						<div class="grid_3 columnData">
							<form class="compareProductForm" name="compareProductForm" actions="#" method="post">
								<div class="registryDataItemsWrap listDataItemsWrap clearfix noProductToCompare">
									<a href="${url}" class="prodImg block noprint" title="Image">
										<img height="229" width="229" alt="Image" src="/_assets/global/images/copare-img-placeholder.png" class="noImageFound"/>
									</a>
								</div>
							</form>
						</div>
					</div>
					<%-- Display empty slots on comparison page if products are less than 5 | End--%>
					<h3 class="specification marLeft_10 headerbackground"><bbbl:label key='lbl_compare_products_description' language="${pageContext.request.locale.language}" /></h3>
					<div class="clearfix cb productDescriptions productDescriptionsMain">
						<dsp:droplet name="ForEach">
							<dsp:param name="array" value="${productsList}" />
							<dsp:oparam name="output">
							<dsp:getvalueof var="shortDescription" param="element.shortDescription"/>
							<dsp:getvalueof var="longDescription" param="element.longDescription"/>
								<div class="grid_3 descColumn">
									<c:if test="${not empty shortDescription}">
										<ul class="noMar"><li><dsp:valueof param="element.shortDescription" valueishtml="true"/></li></ul>
									</c:if>
									<c:if test="${not empty longDescription}">
										<dsp:valueof param="element.longDescription" valueishtml="true"/>
									</c:if>
								</div>
							</dsp:oparam>
						</dsp:droplet>
					</div>
					<div class="clear"></div>
					<dsp:droplet name="ForEach">
						<dsp:param bean="ProductComparisonList.tableColumns" name="array"/>
						<dsp:oparam name="output">
							<dsp:getvalueof param="element.heading" var="rowHeading"/>
							<dsp:getvalueof param="element.name" var="rowPropertyName"/>
							<c:set var="property" value="${0}"/>
							<c:if test="${rowPropertyName eq 'color' || rowPropertyName eq 'attributesList' || rowPropertyName eq 'customizationCode'}">
	                                                  <dsp:getvalueof var="property" bean="ProductComparisonList.${rowPropertyName}FlagClear"/>
	                                                </c:if>
	                                                <c:if test="${property != size}">
							<h3 class="specification marLeft_10 headerbackground"><bbbl:label key='lbl_pdc_header_${rowPropertyName}' language="${pageContext.request.locale.language}"/></h3>
							<c:set var="threshold_value"><bbbl:label key='lbl_higher_free_shipping_threshold' language="${pageContext.request.locale.language}"/></c:set>
							<div class="clearfix cb productDescriptions productAttrDescriptions">
								<dsp:droplet name="ForEach"> 
									<dsp:param bean="ProductComparisonList.items" name="array"/>
									<dsp:oparam name="output">
										<dsp:getvalueof var="isCollection" param="element.collection"/>
										<dsp:getvalueof var="isMultiSku" param="element.multiSku"/>
										<dsp:getvalueof var="isProdActive" param="element.productActive"/>
										<%-- Changes made for BBBH-2212 --%>
										<dsp:getvalueof var="displayShipMsg" param="element.displayShipMsg"/>
										<dsp:getvalueof var="shipMsgFlag" param="element.shipMsgFlag"/>
										<dsp:droplet name="/atg/dynamo/droplet/Switch">
											<dsp:param name="value" value="${rowPropertyName}"/>
											<dsp:oparam name="attributesList">
												
													<dsp:getvalueof var="attributesList" param="element.attributesList" />
													<dsp:droplet name="ForEach">
														<dsp:param param="element.attributesList" name="array"/>
														<dsp:oparam name="outputStart">
		                                                   <div class="grid_3 descColumn">
		                                                      </dsp:oparam>
														<dsp:oparam name="output">
															<dsp:getvalueof var="attributeVo" param="element" />
															<dsp:getvalueof var="imageURl" param="element.imageURL"/>
															<dsp:getvalueof var="actionUrl" param="element.actionURL"/>
															<c:choose>
																<c:when test="${not empty imageURl}">
																	<a href="${actionUrl}" class="newOrPopup"><img src="${imageURLPrice}" alt=" <dsp:valueof param="element.attributeDescrip" valueishtml="true"/>"/></a>
																</c:when>
																<c:otherwise>
																	<c:choose>
																		<c:when test="${not empty actionUrl}">
																			<a href="${actionUrl}" class="newOrPopup"><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></a>
																		</c:when>
																		<c:otherwise>
																			<dsp:valueof param="element.attributeDescrip" valueishtml="true"/>
																		</c:otherwise>
																	</c:choose>
																</c:otherwise>
															</c:choose>
															<c:if test="${not empty imageURl}">
																<c:choose>
																	<c:when test="${not empty actionUrl}">
																		<a href="${actionUrl}" class="newOrPopup"><img src="${imageURl}" alt="" /></a>
																	</c:when>
																	<c:otherwise>
																	 	<img src="${imageURl}" alt="" />
																	</c:otherwise>
																</c:choose>
															</c:if>
														</dsp:oparam>
														<dsp:oparam name="empty">
															<div class="grid_3 descColumn emptyAttribute">       <bbbl:label key='lbl_compare_empty_table_attribute' language="${pageContext.request.locale.language}" /></div>
                                                                                                </dsp:oparam>
                                                                                                <dsp:oparam name="outputEnd">
                                                                                                </div>
                                                                                                </dsp:oparam>
                                                                                         </dsp:droplet>
                                                                                  
                                                                           </dsp:oparam>
                                                                           <dsp:oparam name="color">
                                                                                  
                                                                                         <c:choose>
                                                                                                <c:when test="${isCollection eq 'true' && isProdActive=='true'}">
                                                                                                    <div class="grid_3 descColumn">   <a href="javascript:void(0);" class="compareSelectOptionList"><bbbl:label key='lbl_pdc_multicolor' language="${pageContext.request.locale.language}" /> </a></div>
                                                                                                </c:when>
                                                                                                <c:otherwise>
                                                                                                       <dsp:droplet name="ForEach">
                                                                                                              <dsp:param param="element.color" name="array"/>
                                                                                                              <dsp:oparam name="outputStart">
                                                                                                                     <div class="grid_3 descColumn">
                                                                                                              </dsp:oparam>
                                                                                                              <dsp:oparam name="output">
                                                                                                                     <dsp:getvalueof var="colorImagePath" param="key"/>
                                                                                                                     <dsp:getvalueof var="colorIm" param="element"/>
                                                                                                                     <img src="${scene7Path}/${colorImagePath}" height="20" width="20" alt="${colorIm}"/>
                                                                                                              </dsp:oparam>
                                                                                                              <dsp:oparam name="empty">
                                                                                                           <div class="grid_3 descColumn emptyColor">      <bbbl:label key='lbl_compare_empty_table_attribute' language="${pageContext.request.locale.language}" /></div>
                                                                                                              </dsp:oparam>
                                                                                                              <dsp:oparam name="outputEnd">
                                                                                                           </div>
                                                                                                              </dsp:oparam>
                                                                                                       </dsp:droplet>
                                                                                                </c:otherwise>
                                                                                         </c:choose>
                                                                                         
                                                                           		
											</dsp:oparam>
											<dsp:oparam name="vdcSku">				
												<div class="grid_3 descColumn">
													<dsp:getvalueof var="vdcSkuFlag" param="element.vdcSkuFlag"/>
													<dsp:getvalueof var="isLtlAttributeApplicable" param="element.ltlAttributeApplicable"/>													
													<c:choose>
														<c:when test="${vdcSkuFlag == 'Yes'}">
															<c:choose>
																<c:when test="${isLtlAttributeApplicable}">
																	<div class="grid_3 descColumn">
																		<dsp:getvalueof var="ltlAttributesList" param="element.ltlAttributesList" />
																		<dsp:droplet name="ForEach">
																			<dsp:param param="element.ltlAttributesList" name="array"/>
																			<dsp:oparam name="output">
																				<dsp:getvalueof var="imageURl" param="element.imageURL"/>
																				<dsp:getvalueof var="actionUrl" param="element.actionURL"/>
																				<c:choose>
																					<c:when test="${not empty imageURl}">
																						<a href="${actionUrl}" class="newOrPopup"><img src="${imageURLPrice}" alt=" <dsp:valueof param="element.attributeDescrip" valueishtml="true"/>"/></a>
																					</c:when>
																					<c:otherwise>
																						<c:choose>
																							<c:when test="${not empty actionUrl}">
																								<a href="${actionUrl}" class="newOrPopup"><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></a>
																							</c:when>
																							<c:otherwise>
																								<dsp:valueof param="element.attributeDescrip" valueishtml="true"/>
																							</c:otherwise>
																						</c:choose>
																					</c:otherwise>
																				</c:choose>
																			</dsp:oparam>
																			<dsp:oparam name="empty">
																				<bbbl:label key='lbl_compare_empty_table_attribute' language="${pageContext.request.locale.language}" />
																			</dsp:oparam>
																		</dsp:droplet>
																	</div>
																</c:when>
																<c:otherwise>
																	<dsp:getvalueof var="vdcVO" param="element.vdcSku"/>
																	<dsp:droplet name="ForEach">
																<dsp:param name="array" param="element.vdcSku"/>
																<dsp:oparam name="output">
																		<dsp:getvalueof var="count" param="count"/>
																		<dsp:getvalueof var="length" param="size"/>
																		<dsp:getvalueof var="name" param="element.attributeDescrip"/>
																		<dsp:getvalueof var="imageURl" param="element.imageURL"/>
																		<dsp:getvalueof var="actionUrl" param="element.actionURL"/>
																		<c:set var="language" value="${pageContext.request.locale.language}" scope="request"/>
																		<c:set var="vendorName"><bbbl:label key='lbl_pdc_vendorship' language="${language}"/></c:set>
											             				<dsp:getvalueof var="chkCount" param="count"/>
																		<dsp:getvalueof var="chkSize" param="size"/>
																		<c:set var="sep" value="seperator" />																	
																		<c:if test="${chkCount == chkSize}">
																			<c:if test="${!rebatesOn}">
																				<c:set var="sep" value="" />
																			</c:if>
																		</c:if>
																		<c:choose>
																			<c:when test="${!empty name}">
																				<c:choose>
																					<c:when test="${!empty imageURl}">		
																	               <a href="${actionUrl}" class="newOrPopup"><img src="${imageURl}" alt="${vendorName}"/></a>
																					</c:when>
																					<c:otherwise>
																						<c:choose>
																							<c:when test="${!empty actionUrl}">
																								<a href="${actionUrl}" class="newOrPopup">${vendorName}</a>
																							</c:when>
																							<c:otherwise>
																								${vendorName}
																							</c:otherwise>
																						</c:choose>
																					</c:otherwise>
																				</c:choose>
																			</c:when>
																			<c:otherwise>
																				<c:if test="${!empty imageURl}">
																					<c:choose>
																						<c:when test="${!empty actionUrl}">
																							<a href="${actionUrl}" class="newOrPopup"><img src="${imageURl}" alt=""/></a>
																						</c:when>
																						<c:otherwise>
																							<span class="rebateAttribs attribs ${sep}"><img src="${imageURl}" alt="" /></span>
																						</c:otherwise>
																					</c:choose>
																				</c:if>
																			</c:otherwise>
																		</c:choose>
																		</dsp:oparam>
																		</dsp:droplet>
																</c:otherwise>
															</c:choose>
															
														</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${vdcSkuFlag == 'Select Options'}">
																		<c:choose>
																			<c:when test="${isCollection && isProdActive==true}">
																				<a href="javascript:void(0);" class="compareSelectOptionList">${vdcSkuFlag}</a>
																			</c:when>
																			<c:when test="${isMultiSku && isProdActive==true}">
																				<a href="javascript:void(0);" class="compareSelectOption">${vdcSkuFlag}</a>
																			</c:when>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<bbbl:label key='lbl_compare_ship_warehouse' language="${pageContext.request.locale.language}" />
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
													</c:choose>
												</div>
											</dsp:oparam>
											<dsp:oparam name="freeStandardShipping">
												<dsp:getvalueof var="freeStandardShipping" param="element.freeStandardShipping"/>
												<c:choose>
													<c:when test="${freeStandardShipping == 'Select Options'}">
														<c:choose>
															<c:when test="${isCollection && isProdActive==true}">
																<div class="grid_3 descColumn"><a href="javascript:void(0);" class="compareSelectOptionList">${freeStandardShipping}</a></div>
															</c:when>
															<c:when test="${isMultiSku && isProdActive==true}">
																<div class="grid_3 descColumn"><a href="javascript:void(0);" class="compareSelectOption">${freeStandardShipping}</a></div>
															</c:when>
														</c:choose>
													 </c:when>
													 <c:otherwise>
														 <c:choose>
														 	<c:when test="${freeStandardShipping eq 'Yes'|| isInternationalCustomer}">
														 		<div class="grid_3 descColumn">${freeStandardShipping}</div>
														 	</c:when>
														 	<c:otherwise>
														 	<jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request"/>
															<c:set target="${placeHolderMap}" property="threshold" value="${threshold_value}"/>
														 		<c:set var="isFreeShipPromoFlag" scope="request"><tpsw:switch tagName="FreeShipPromo_${appid}"/></c:set>
														 		<c:set var="shipPromoMsg"><bbbt:textArea key='txt_compare_ship_promo_msg' placeHolderMap="${placeHolderMap}" language="${pageContext.request.locale.language}" /></c:set>
														 		<dsp:getvalueof var="freeStandardShipping" param="element.freeStandardShipping"/>
																<c:choose>
														 			<c:when test="${not empty displayShipMsg}">
														 				<div class="grid_3 descColumn">${shipPromoMsg}</div>
														 			</c:when>
														 			<c:otherwise>
														 				<div class="grid_3 descColumn">${freeStandardShipping}</div>
														 			</c:otherwise>
														 		</c:choose>
														 	</c:otherwise>
														 </c:choose>
													</c:otherwise>
							 					</c:choose>	
											</dsp:oparam>				
											<dsp:oparam name="skuGiftWrapEligible">
												<dsp:getvalueof var="skuGiftWrapEligible" param="element.skuGiftWrapEligible"/>
												<c:choose>
													<c:when test="${skuGiftWrapEligible == 'Select Options'}">
														<c:choose>
															<c:when test="${isCollection && isProdActive==true}">
																<div class="grid_3 descColumn"><a href="javascript:void(0);" class="compareSelectOptionList">${skuGiftWrapEligible}</a></div>
															</c:when>
															<c:when test="${isMultiSku && isProdActive==true}">
																<div class="grid_3 descColumn"><a href="javascript:void(0);" class="compareSelectOption">${skuGiftWrapEligible}</a></div>
															</c:when>
														</c:choose>
													 </c:when>
													 <c:otherwise>
												 		<div class="grid_3 descColumn">${skuGiftWrapEligible}</div>
													 </c:otherwise>
											 	</c:choose>
											</dsp:oparam>
											<dsp:oparam name="clearance">
												<dsp:getvalueof var="isClearence" param="element.clearance"/>
												<c:choose>
													<c:when test="${isClearence == 'Select Options'}">
														<c:choose>
															<c:when test="${isCollection && isProdActive==true}">
																<div class="grid_3 descColumn"><a href="javascript:void(0);" class="compareSelectOptionList">${isClearence}</a></div>
															</c:when>
															<c:when test="${isMultiSku && isProdActive==true}">
																<div class="grid_3 descColumn"><a href="javascript:void(0);" class="compareSelectOption">${isClearence}</a></div>
															</c:when>
														</c:choose>
													</c:when>
													<c:otherwise>
														<div class="grid_3 descColumn">${isClearence}</div>
													</c:otherwise>
												</c:choose>					 
											</dsp:oparam>
											<dsp:oparam name="customizationCode">
												<dsp:getvalueof var="customizationCode" param="element.customizationCode"/>
												<dsp:getvalueof var="customizationCodeValues" param="element.customizationCodeValues"/>
													<c:choose>
														<c:when test="${customizationCode == 'Select Options' }">
															<c:choose>
															<c:when test="${isCollection && isProdActive==true}">
																<div class="grid_3 descColumn"><a href="javascript:void(0);" class="compareSelectOptionList">${customizationCode}</a></div>
															</c:when>
															<c:when test="${isMultiSku && isProdActive==true}">
																<div class="grid_3 descColumn"><a href="javascript:void(0);" class="compareSelectOption">${customizationCode}</a></div>
															</c:when>
															</c:choose>
														</c:when>
														<c:when test="${empty customizationCode}">
															<div class="grid_3 descColumn emptyPersonalization"><bbbl:label key='lbl_compare_empty_table_attribute' language="${pageContext.request.locale.language}" /></div>
														</c:when>
														<c:otherwise>
															<c:choose>
																<c:when test="${customizationCodeValues !=null }">
																	<div class="grid_3 descColumn">
																		<ul class="noMar">
																		<dsp:droplet name="ForEach">
																			<dsp:param name="array" value="${customizationCodeValues}"/>
																			<dsp:oparam name="output">																	
																				<li><dsp:valueof param="element"/></li>
																			</dsp:oparam>
																		</dsp:droplet>
																		</ul>
																	</div>																
																</c:when>
																<c:otherwise>
																	<div class="grid_3 descColumn">${customizationCode}</div>
																</c:otherwise>
															</c:choose>
														</c:otherwise>
													</c:choose>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
							</div>
					</c:if>
						</dsp:oparam>
					</dsp:droplet>
					<h3 class="specification marLeft_10 headerbackground"><bbbl:label key='lbl_sa_ugc_description' language="${pageContext.request.locale.language}" /></h3>
					
						<dsp:droplet name="ForEach">
						<dsp:param name="array" value="${productsList}" />
						<dsp:setvalue param="CurrentStudent" paramvalue="element.productId"/>
						
						<dsp:oparam name="output">
						<dsp:getvalueof var="dataIndex" param="count"/>
						<dsp:getvalueof var="productId" param="element.productId"/>
						<c:set var="CurrentProduct" value="'sa_ugc_compare-${dataIndex}':'${productId}-${dataIndex}'"/>
						<div class="grid_3 columnData">
						<div id="currentProduct" data-compareproduct="${CurrentProduct}" class="hidden"></div>	
					<div id="sa_ugc_compare-${dataIndex}"></div>	
			        </div>
					</dsp:oparam>
					</dsp:droplet>	
					</div><%-- Close Content Div --%>
					<%-- Display each product's column attribute value --%>
					<%-- Display tabular attributes of products using TableInfo component on comparison page| Start --%>
					<%-- This outer loop is to display the number of rows and is equal to the properties need to display for the product in table--%>
					<%-- These properties/ rows are configurable thru TableInfo Component --%>
					<%-- Display tabular attributes of products on comparison page| End--%>
					<%-- Find/Change Store Form jsps--%>	
					<c:import url="../_includes/modules/change_store_form.jsp" />
					<c:import url="../selfservice/find_in_store.jsp" />
					
					<script type="text/javascript">
						function addToWishList(){
							   var qty = '${param.qty}';
							   var price = document.getElementById('certonaPrice') ? document.getElementById('certonaPrice').value : "";
							   totalPrice=qty*price;
							   var finalOut= ';'+'${fn:escapeXml(param.productId)}'+';;;event38='+'${fn:escapeXml(param.qty)}'+'|event39='+totalPrice+';eVar30='+'${fn:escapeXml(param.skuId)}';
							   additemtolist(finalOut);
						}
					</script>
					
		</jsp:body>
         <jsp:attribute name="footerContent">
           <script type="text/javascript">
          if(typeof s !=='undefined')
           {
            s.channel = 'Product Comparison';
            s.pageName='Product Comparison Page';// pagename
            s.prop1='Product Comparison';// page title
            s.prop2='Product Comparison';// category level 1
            s.prop3='Product Comparison';// category level 2
            s.prop5='Product Comparison';// category level 2
            var s_code=s.t();
            if(s_code)document.write(s_code);
           }
          var compare_list_array = [];
          var compare_list = Array.prototype.map.call(document.querySelectorAll('div[id^=currentProduct]'), function (div) { 
          var compare_var = div.getAttribute("data-compareproduct"); 
          compare_var = compare_var.replace(new RegExp("'", 'g'), ""); 
          compare_var = compare_var.split(':'); 
          compare_list_array[compare_var[0]] = compare_var[1];
          }); 
          console.log(compare_list_array); 
          var sa_multiple_products = compare_list_array; 
  			
						var sa_page="2";
		
         				(function()
        		 	
         					{function sa_async_load() 
         				
         					{
         						var sa = document.createElement('script');
          						sa.type = 'text/javascript';
          						sa.async = true;
          						sa.src = '${saSrc}';
          						var sax = document.getElementsByTagName('script')[0];
          						
          						sax.parentNode.insertBefore(sa, sax);
          						}
          						
         					if (window.attachEvent) 
         					{
          						
         						window.attachEvent('onload', sa_async_load);
          				
         					}
         					
         					else {
          
         						window.addEventListener('load', sa_async_load,false);
          
         					}
           
         					})
           
         					();       
        </script>
    </jsp:attribute>
	</bbb:pageContainer>
</dsp:page>
 <%-- R2.2 Story - 178-a4 Product Comparison Page | End--%>