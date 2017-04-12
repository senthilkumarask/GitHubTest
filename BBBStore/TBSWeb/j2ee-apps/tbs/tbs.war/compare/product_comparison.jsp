<%-- R2.2 Story - 178-a4 Product Comparison Page | Start--%>
<dsp:page>
   <dsp:importbean bean="/com/bbb/commerce/browse/droplet/compare/ProductComparisonDroplet" />
   <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
   <dsp:importbean bean="/atg/commerce/catalog/comparison/ProductComparisonList" />
   <dsp:importbean bean="/atg/multisite/Site" />
   <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/AddItemToGiftRegistryDroplet" />
   <dsp:importbean bean="/atg/userprofiling/Profile" />
   <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
   <dsp:importbean bean="/atg/commerce/catalog/comparison/TableInfo" />
   <dsp:importbean bean="/com/bbb/commerce/droplet/TBSItemExclusionDroplet"/>
   <dsp:getvalueof id="contextroot" idtype="java.lang.String" bean="/OriginatingRequest.contextPath" />
   <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
   <jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request" />
   <c:set target="${placeHolderMap}" property="contextPath" value="${contextPath}" />
   <dsp:getvalueof var="appid" bean="Site.id" />
   <%-- Get the last navigated Search/ PLP page url from the session --%>
   <dsp:getvalueof var="url" bean="ProductComparisonList.url" />
   <c:set var="pageWrapper" value="compareProducts useScene7" scope="request" />
   <c:set var="compare" value="compare" scope="request" />
   <%-- Redirect to home page if user has not visited any PLP/ Search page in the session --%>
   <c:if test="${empty url}">
      <c:redirect url="/" />
   </c:if>
   <bbb:pageContainer>
      <jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>
      <jsp:attribute name="section">${compare}</jsp:attribute>
      <jsp:attribute name="bodyClass">compare</jsp:attribute>
      <jsp:body>
		<c:set var="TBS_BedBathUSSite" scope="request">
			<bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" />
		</c:set>
		<c:set var="TBS_BuyBuyBabySite" scope="request">
			<bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
		</c:set>
		<c:set var="TBS_BedBathCanadaSite" scope="request">
			<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
		</c:set>
		 <c:set var="omniPersonalizeButtonClick">
			<bbbl:label key='omni_personalize_button_click' language="${pageContext.request.locale.language}" />
		</c:set>
		<c:set var="enableKatoriFlag" scope="request"><bbbc:config key="enableKatori" configName="EXIMKeys"/></c:set>

			<input type="hidden" id="omniPageTypeVar46" value="product comparison page" name="omniPageTypeVar46" />
			<div id="subHeader" class="row compare-results-header">
				<div class="share">
					<h1 class="fl small-12 large-11 columns">
	                	<bbbl:label key='lbl_compare_products' language="${pageContext.request.locale.language}" />
	               	</h1>
	           		<div class="large-1 right print-email show-for-large-up columns">
						<a href="#" class="pdp-sprite print print-trigger" title="<bbbl:label key="lbl_cartdetail_print" language="${language}"/>"><span></span></a>
					</div>
					<h2 class="fl small-9 columns find-text headerMessage">
                  <bbbl:label key='lbl_compare_products_title' language="${pageContext.request.locale.language}" />
               </h2>
                            <div class="small-3 columns right hide-for-medium-down"> <a title="Continue Shopping" class="button tiny secondary continue-shopping right" href="javascript: history.go(-1)"> Continue Shopping </a> </div>
					<div class="small-12 columns hide-for-medium-up"> <a title="Continue Shopping" class="button secondary continue-shopping right expand" href="javascript: history.go(-1)"> Continue Shopping </a> </div>
					<a href="${contextPath}/compare/product_comparison.jsp" class="print fr headerCommunication" title="Print"></a>

				</div>
			</div>
			<input type="hidden" id="comparisonPage" value="true" name="comparisonPage" />
			<div id="content" class="compare-page compareProducts">
			<div class="row compare-section">
                <div class="small-12 large-2 columns show-for-large-up">
					<h3>Product</h3>
				</div>
				<div class="compareBuckets small-12 large-10 columns">

					<%-- This droplet iterates over the session Comparison List products and update their VO and return true output if successfull--%>
					<dsp:droplet name="ProductComparisonDroplet">
						<dsp:oparam name="output">
							<dsp:getvalueof var="isCompareSuccess" param="compareProductsSuccess" />
						</dsp:oparam>
						<dsp:oparam name="error">
							<c:set var="isCompareSuccess" value="${false}" />
						</dsp:oparam>
					</dsp:droplet>

					<c:if test="${isCompareSuccess}">
						<dsp:getvalueof var="productsList" bean="ProductComparisonList.items" vartype="java.util.List" />
					</c:if>
					<c:set var="size" value="${fn:length(productsList)}" />

					<dsp:droplet name="ForEach">
						<dsp:param name="array" value="${productsList}" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="dataIndex" param="count" />
							<dsp:getvalueof var="singleSkuId" param="element.defaultSkuId" />
							<dsp:getvalueof var="EmailAlertOn" param="element.emailAlertOn" />
							<dsp:getvalueof var="productId" param="element.productId" />
							<dsp:getvalueof var="isProdActive" param="element.productActive" />
							<dsp:getvalueof var="productName" param="element.productName" />
							<dsp:getvalueof var="mediumImage" param="element.mediumImagePath" />
							<dsp:getvalueof var="thumbnailImagePath" param="element.thumbnailImagePath" />
							<dsp:getvalueof var="largeImage" param="element.largeImage" />
							<dsp:getvalueof var="bvReviews" param="element.reviews" />
							<dsp:getvalueof var="skuID" param="element.skuId" />
							<dsp:getvalueof var="salePriceRangeDescription" param="element.salePriceRangeDescription" />
							<dsp:getvalueof var="isCollection" param="element.collection" />
							<dsp:getvalueof var="isMultiSku" param="element.multiSku" />
							<dsp:getvalueof var="inStock" param="element.inStock" />
							<dsp:getvalueof var="isLTLProduct" param="element.ltlProduct" />
							<dsp:getvalueof var="customizationCode" param="element.customizationCode"/>
							<dsp:getvalueof var="customizableRequired" param="element.customizableRequired"/>
							<dsp:getvalueof var="customizationOffered" param="element.customizationOffered"/>
							<dsp:getvalueof var="personalizationType" param="element.personalizationType"/>
							<dsp:getvalueof var="customizationCodeValues" param="element.customizationCodeValues"/>
							<dsp:getvalueof var="inCartFlag" param="element.inCartFlag" />
							<c:set var="mediumImage">
                        <c:out value='${fn:replace(mediumImage,"$146$","$229$")}' />
                     </c:set>
                     <c:set var="customizableCodes" value="${customizationCode}" scope="request"/>
																			
										<c:set var="customizeCTACodes" scope="request">
											<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys" />
										</c:set>
					 <c:choose>
						   <c:when test="${not empty singleSkuId}">
								 <dsp:getvalueof var="skuID" value="${singleSkuId}" />
							</c:when>
							 <c:otherwise>
							  <dsp:getvalueof var="skuID" value="${skuID}" />
							</c:otherwise>
						</c:choose>
							<c:if test="${inStock eq null}">
								<c:set var="inStock" value="true" />
							</c:if>
							<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
								<dsp:param name="id" param="element.productId" />
								<dsp:param name="itemDescriptorName" value="product" />
								<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
								</dsp:oparam>
							</dsp:droplet>
							<div class="columnData small-12 large-3 columns">
							<dsp:form iclass="compareProductForm" name="compareProductForm" method="post">
								<a href="javascript:void(0);" class="remove-item"><span></span>
                                    <bbbl:label key='lbl_remove_product' language="${pageContext.request.locale.language}" />
                                </a>
								<div id="sku_${skuID}" class="registryDataItemsWrap listDataItemsWrap clearfix ${productId}">
									<div class="small-4 large-12 columns">
                                    <c:choose>
										<c:when test="${isProdActive}">
											<c:choose>
												<c:when test="${not empty mediumImage}">
                                                       <dsp:a page="${finalUrl}?skuId=${skuID}" iclass="prodImg block" title="${productName}">
                                                              <img alt="${productName}" src="${scene7Path}/${mediumImage}" class="noImageFound" />
                                                       </dsp:a>
                                                </c:when>
                                                <c:otherwise>
                                                       <dsp:a page="${finalUrl}?skuId=${skuID}" iclass="prodImg block" title="${productName}">
                                                              <img alt="${productName}" src="${imagePath}/_assets/global/images/no_image_available.jpg" class="noImageFound" />
                                                       </dsp:a>
                                                </c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<c:choose>
												<c:when test="${not empty mediumImage}">
													<img alt="${productName}" src="${scene7Path}/${mediumImage}" class="noImageFound" />
												</c:when>
												<c:otherwise>
													<img alt="${productName}" src="${imagePath}/_assets/global/images/no_image_available.jpg" class="noImageFound" />
												</c:otherwise>
											</c:choose>
										</c:otherwise>
									</c:choose>
                                    </div>
									<ul data-bucket-index="bucket_${dataIndex}" class="prodInfoGrid clearfix alpha omega">
                                    <div class="small-8 large-12 columns">
										<li class="prodDescription">
											<div class="prodName">
                                                <h2 class="subheader">
											<c:choose>
												<c:when test="${isProdActive}">
													<dsp:a page="${finalUrl}?skuId=${skuID}">
                                                <dsp:valueof param="element.productName" valueishtml="true" />
                                             </dsp:a>
												</c:when>
												<c:otherwise>
													<span class="disableText"><dsp:valueof param="element.productName" valueishtml="true" /></span>
												</c:otherwise>
											</c:choose>
                                            </h2>
											</div>

											<div class="ratingsBlock rating">
                                            <h4 class="subheader"><!-- <img width="76" height="14" src="../img/fpo/grid-stars.png">(NNN) -->
											<c:if test="${BazaarVoiceOn}">
												<c:choose>
													<c:when test="${empty bvReviews}">
														<dsp:getvalueof var="totalReviewCount" value="0"></dsp:getvalueof>
													</c:when>
													<c:otherwise>
														<dsp:getvalueof var="reviews" value="${bvReviews.totalReviewCount}" />
														<dsp:getvalueof var="ratings" value="${bvReviews.averageOverallRating}" vartype="java.lang.Integer" />
														<dsp:getvalueof var="rating" value="${ratings * 10}" vartype="java.lang.Integer" />
														<dsp:getvalueof var="totalReviewCount" value="${bvReviews.totalReviewCount}"></dsp:getvalueof>
													</c:otherwise>
											 </c:choose>
											 <c:choose>
												     <c:when test="${ratings ne null && ratings ne '0.0'}">

												     <c:choose>
															<c:when test="${isProdActive}">
																<span title="${ratings}" class="ratingsReviews fl prodReviews clearfix prodReviews<fmt:formatNumber value="${rating}" pattern="#0" />"></span>
																<dsp:a iclass="block" page="${finalUrl}?skuId=${skuID}&showRatings=true">(${totalReviewCount})
                                                      </dsp:a>
															</c:when>
															<c:otherwise>
																<span title="${ratings}" class="ratingsReviews fl prodReviews clearfix prodReviews<fmt:formatNumber value="${rating}" pattern="#0" />"></span>
																(${totalReviewCount})
															</c:otherwise>
															</c:choose>
													</c:when>
													<c:otherwise>
								                       <dsp:getvalueof var="seoUrl" param="element.seoUrl" />
								                       <c:choose>
								                       		<c:when test="${empty seoUrl}">
																<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
																	<dsp:param name="id" param="element.productID" />
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
													<c:set var="writeReviewLink"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></c:set>
                                                    <span class="prodReviews ratingsReviews writeReview">
              											<dsp:a  page="${finalUrl}?categoryId=${CategoryId}&writeReview=true" title="${writeReviewLink}">${writeReviewLink}</dsp:a>
              										</span>

												</c:otherwise>
												</c:choose>
												</c:if>
                                                </h4>
											</div>

										</li>
										<li class="prodpriceGridList marBottom_20 clearfix">
                                        <div class="quantity prodpriceGrid cb <c:if test="${(inStock==false && not empty skuID) || isProdActive==false}">priceQuantityNotAvailable</c:if>">
                                            <dsp:getvalueof var="oldPriceRange" value="" />
                                            <c:if test="${not isCollection && not isMultiSku}">
                                                <dsp:getvalueof var="oldPriceRange" value="oldPriceRange" />
                                                        <div class="qty-spinner">
                                                            <a class="button minus secondary"><span></span></a>
                                                            <input type="text" data-max-value="99" maxlength="2" value="1" name="qty" class="quantity-input qty addItemToRegis addItemToList itemQuantity" id="quantity">
                                                            <a class="button plus secondary"><span></span></a>
                                                        </div>
                                            </c:if>

                                                    <c:choose>
                                                    <c:when test="${not empty skuID}">
                                                            <h1 class="prodPrice price">
	                                                            <dsp:include page="/browse/product_details_price.jsp">
	                                                                <dsp:param name="product" value="${productId}" />
	                                                                <dsp:param name="sku" value="${skuID}" />
	                                                                <dsp:param name="inCartFlag" value="${inCartFlag}"/>
	                                                            </dsp:include>
                                                            </h1>
                                                        </c:when>
                                                        <c:otherwise>
                                                        <c:choose>
                                                            <c:when test="${not empty salePriceRangeDescription}">
                                                            <h1 class="prodPrice price">
                                                            <span class="prodPriceNEW">
                                                                <dsp:valueof converter="currency" param="element.salePriceRangeDescription" />
                                                            </span>
                                                                <dsp:getvalueof var="salePriceRange" param="element.salePriceRangeDescription" />
                                                                <c:choose>
                                                                    <c:when test="${fn:contains(salePriceRange,'-')}">
                                                            <br />
                                                            </h1>
                                                         </c:when>
                                                                    <c:otherwise>&nbsp;</c:otherwise>
                                                                </c:choose>
                                                                <h3 class="price price-original prodPriceOLD <c:if test="${empty oldPriceRange}">oldPriceRange</c:if>">
                                                                    <span class="was"><bbbl:label key='lbl_old_price_text' language="${pageContext.request.locale.language}" /></span>
                                                                    <span class="oldPriceNum"> <dsp:valueof converter="currency" param="element.priceRangeDescription" /></span>
                                                                </h3>
                                                                <dsp:getvalueof idtype="java.lang.String" id="salePrice" param="element.salePriceRangeDescription" />
                                                            </c:when>
                                                            <c:otherwise>
                                                                <h1 class="prodPrice price">
                                                                    <span>
                                                                        <dsp:valueof converter="currency" param="element.priceRangeDescription" />
                                                                    </span>
                                                                </h1>
                                                            </c:otherwise>
                                                        </c:choose>
                                                        </c:otherwise>
                                                    </c:choose>
                                            </div>
                                        </li>
										<li class="outStockMsg clearfix padTop_5 noMarBot padBottom_10">

											<c:if test="${not isCollection && not isMultiSku}">
											  <div class="message">
												    <input type="hidden" name="oosProdId" value="${productId}" />
														 <c:choose>
															<c:when test="${inStock==true}">
																<input type="hidden" value="" name="oosSKUId" class="_oosSKUId" />
															</c:when>
															<c:otherwise>
																<input type="hidden" value="${skuID}" name="oosSKUId" class="_oosSKUId" />
															</c:otherwise>
														</c:choose>

													<c:if test="${inStock eq false}">
														<c:choose>
															<c:when test="${((null eq EmailAlertOn) || (EmailAlertOn==true)) && (inStock==false)}">
																<div class="error noMarBot">
                                                      <bbbl:label key='lbl_pdp_product_notavailable_shipping' language="${pageContext.request.locale.language}" />
                                                   </div>
																<div class="info">
                                                      <a class="info lnkNotifyOOS" href="#"><bbbl:label key='lbl_pdp_product_notify_item_available' language="${pageContext.request.locale.language}" /> &raquo;</a>
                                                   </div>
															</c:when>
															<c:otherwise>
																<div class="error noMarBot">
                                                      <bbbl:label key='lbl_pdp_product_notavailable_shipping' language="${pageContext.request.locale.language}" />
                                                   </div>
															</c:otherwise>
														</c:choose>
													</c:if>
												</div>
											</c:if>
										</li>
                                        </div>
                                        <%-- Katori Intergration --%>




										<c:choose>
											<c:when test="${not empty customizableCodes && fn:contains(customizeCTACodes, customizableCodes)}">
												<c:set var="personalizationTxt">
													<bbbl:label key="lbl_customize_this" language="${pageContext.request.locale.language}"/>
												</c:set>
												<c:set var="personalizationReqTxt">
													<bbbl:label key='lbl_customization_required' language="${pageContext.request.locale.language}" />
												</c:set>
											</c:when>
											<c:otherwise>
												<c:set var="personalizationTxt">
													<bbbl:label key="lbl_personalize_this" language="${pageContext.request.locale.language}"/>
												</c:set>
												<c:set var="personalizationReqTxt">
													<bbbl:label key='lbl_personalization_required' language="${pageContext.request.locale.language}" />
												</c:set>
											</c:otherwise>
										</c:choose>


                                        <%-- END  --%>
									   <div class="small-12 medium-8 large-12 columns">
									   <li>
									   		<c:choose>
											<c:when test="${ (customizableRequired || customizationOffered) && not empty customizationCodeValues}">
												<div class="personalizationOffered">


													<a class="btnPersonalize personalize nonpdpPersonalize tiny button <c:if test='${not enableKatoriFlag}'>personalize disabled </c:if>" <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="${contextPath}${finalUrl}?openKatori=true&skuId=${skuID}"</c:otherwise></c:choose> role="button" data-sku="${skuID}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
														${personalizationTxt}
													</a>
													<span class="personalizationDetail">
														<bbbl:label key='lbl_cart_personalization_detail_tbs' language="${pageContext.request.locale.language}" />
													</span>
													<div id="openReturnPolicyModal" class="reveal-modal large" data-options="close_on_background_click:true;close_on_esc:true;" data-reveal=""></div>
													<c:choose>
						      						 <c:when test="${enableKatoriFlag}">
						      						 	<c:if test="${customizableRequired}">
	                                                         <div class="personalizeToProceed">
	                                                         	${personalizationReqTxt}
	                                                           </div>
														</c:if>
														</c:when>
														<c:otherwise>
															<div class="button_disabled"><bbbl:label key='lbl_pdp_personalization_unavailable' language="${pageContext.request.locale.language}" /></div>
														</c:otherwise>
													</c:choose>
												</div>
											</c:when>
											<c:otherwise>
													<div class="personalizationOffered hidden">


														<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="${contextPath}${finalUrl}?openKatori=true&skuId=${skuID}"</c:otherwise></c:choose>  class="personalizeBtnCompare personalize nonpdpPersonalize btnPersonalize tiny button <c:if test='${not enableKatoriFlag}'>personalize disabled </c:if>" role="button" data-sku="${skuID}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
															${personalizationTxt}
														</a>
														<span class="personalizationDetail">
															<bbbl:label key='lbl_cart_personalization_detail_tbs' language="${pageContext.request.locale.language}" />
														</span>
														<div id="openReturnPolicyModal" class="reveal-modal large" data-options="close_on_background_click:true;close_on_esc:true;" data-reveal=""></div>
															<c:choose>
															<c:when test="${enableKatoriFlag}">
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
									   </li>
						<dsp:droplet name="TBSItemExclusionDroplet">
							<dsp:param name="siteId" value="${appid}"/>
							<dsp:param name="skuId" value="${skuID}"/>
							<dsp:oparam name="output">
								<dsp:getvalueof var="isItemExcluded" param="validItemExcludedSku"/>
								<dsp:getvalueof var="caDisabled" param="caDisabled"/>
								<dsp:getvalueof var="reasonCode" param="reasonCode"/>
							</dsp:oparam>
						</dsp:droplet>
						<c:choose>
							<c:when test="${isItemExcluded  || caDisabled}">
								<div class="small-12 columns product-other-links availability" style="color:red;font-weight:bold;">
							 		<dsp:include page="/browse/frag/reasonCodeMessage.jsp" flush="true" >
										<dsp:param name="reasonCode" value="${reasonCode}"/>
									</dsp:include>
							    </div>
										<li>
											<c:set var="chooseOptionBtn">
                                       <bbbl:label key='lbl_compare_btn_choose_options' language="${pageContext.request.locale.language}" />
                                    </c:set>
											<c:choose>
												<c:when test="${isCollection}">
													<div class="button_active button_active_orange button_disabled">
														<%-- <input type="button" aria-pressed="false" role="button" name="btnAddToCart" value="${chooseOptionBtn}" disabled="disabled" <c:if test="${isProdActive==true}">class="enableOnDOMReady showOptionsCollection small button secondary expand"</c:if> /> --%>
                                                        <a href="/tbs/compare/compare_list_view.jsp?prodToCompare=true&prodId=${productId}&pdpLink=${finalUrl}" data-reveal-id="quickViewModal" data-reveal-ajax="true" class=" small button secondary expand showOptionsCollection">${chooseOptionBtn}</a>
                                                        <div id="quickViewModal" class="reveal-modal xlarge" data-reveal><a class='close-reveal-modal'>&#215;</a></div>
													</div>
												</c:when>
												<c:when test="${isMultiSku}">
													<div class="button_active button_active_orange button_disabled">
														<%-- <input type="button" aria-pressed="false" role="button" name="btnAddToCart" value="${chooseOptionBtn}" disabled="disabled" <c:if test="${isProdActive==true}">class="enableOnDOMReady showOptionMultiSku small button secondary expand"</c:if> /> --%>
                                                        <a href="/tbs/browse/quickview_product_details.jsp?prodToCompare=true&productId=${productId}&pdpLink=${finalUrl}" data-reveal-id="quickViewModal" data-reveal-ajax="true" class=" small button secondary expand showOptionMultiSku">${chooseOptionBtn}</a>
                                                        <div id="quickViewModal" class="reveal-modal xlarge" data-reveal><a class='close-reveal-modal'>&#215;</a></div>
													</div>
												</c:when>
												<c:otherwise>
													<div class="addToCart">
														<div class="button_active button_active_orange button_disabled">
															<input type="submit" aria-pressed="false" role="button" name="btnAddToCart" value="Add to cart" disabled="disabled" <c:choose><c:when test="${customizableRequired==false &&  !isItemExcluded && !caDisabled && inStock==true && isProdActive==true}">class="enableOnDOMReady small button transactional expand"</c:when><c:otherwise>class="small button transactional expand disabled"</c:otherwise></c:choose> />
														</div>
													</div>
												</c:otherwise>
											</c:choose>
											</c:when>
											<c:otherwise>
											<li>
											<c:set var="chooseOptionBtn">
                                       <bbbl:label key='lbl_compare_btn_choose_options' language="${pageContext.request.locale.language}" />
                                    </c:set>
											<c:choose>
												<c:when test="${isCollection}">
													<div class="button_active button_active_orange button_disabled">
														<%-- <input type="button" aria-pressed="false" role="button" name="btnAddToCart" value="${chooseOptionBtn}" disabled="disabled" <c:if test="${isProdActive==true}">class="enableOnDOMReady showOptionsCollection small button secondary expand"</c:if> /> --%>
                                                        <a href="/tbs/compare/compare_list_view.jsp?prodToCompare=true&prodId=${productId}&pdpLink=${finalUrl}" data-reveal-id="quickViewModal" data-reveal-ajax="true" class=" small button secondary expand showOptionsCollection">${chooseOptionBtn}</a>
                                                        <div id="quickViewModal" class="reveal-modal xlarge" data-reveal><a class='close-reveal-modal'>&#215;</a></div>
													</div>
												</c:when>
												<c:when test="${isMultiSku}">
													<div class="button_active button_active_orange button_disabled">
														<%-- <input type="button" aria-pressed="false" role="button" name="btnAddToCart" value="${chooseOptionBtn}" disabled="disabled" <c:if test="${isProdActive==true}">class="enableOnDOMReady showOptionMultiSku small button secondary expand"</c:if> /> --%>
                                                        <a href="/tbs/browse/quickview_product_details.jsp?prodToCompare=true&productId=${productId}&pdpLink=${finalUrl}" data-reveal-id="quickViewModal" data-reveal-ajax="true" class=" small button secondary expand showOptionMultiSku">${chooseOptionBtn}</a>
                                                        <div id="quickViewModal" class="reveal-modal xlarge" data-reveal><a class='close-reveal-modal'>&#215;</a></div>
													</div>
												</c:when>
												<c:otherwise>
													<div class="addToCart">
														<div class="button_active button_active_orange button_disabled">
															<input type="submit" aria-pressed="false" role="button" name="btnAddToCart" value="Add to cart" disabled="disabled" <c:choose><c:when test="${customizableRequired==false && inStock==true && isProdActive==true}">class="enableOnDOMReady small button transactional expand"</c:when><c:otherwise>class="small button transactional expand disabled"</c:otherwise></c:choose> />
														</div>
													</div>
												</c:otherwise>
											</c:choose>
											</c:otherwise>
											</c:choose>
											<!-- Data to submit on add to cart/ Find In store -->
											<input type="hidden" class="addItemToList addItemToRegis itemQuantity" value="1" name="qty" data-change-store-submit="qty" />
											<input type="hidden" data-change-store-internaldata="true" data-change-store-errors="required" data-change-store-submit="prodId" value="${productId}" class="_prodId addItemToRegis productId addItemToList" name="prodId" />
											<c:if test="${not empty skuID}">
												<input type="hidden" data-change-store-internaldata="true" data-change-store-errors="required" data-change-store-submit="skuId" class="addItemToRegis _skuId addItemToList changeStoreSkuId" value="${skuID}" name="skuId" />
											</c:if>
											<input type="hidden" name="price" value="${omniPrice}" class="addItemToList addItemToRegis" />
											<input type="hidden" data-change-store-storeid="storeId" value="" name="storeId" class="addToCartSubmitData" />
											<input type="hidden" name="parentProdId" value="${productId}" class="addItemToList addItemToRegis" />
											<input type="hidden" value="" class="addToCartSubmitData" name="bts" data-change-store-storeid="bts" />
											<input type="hidden" value="true" class="addItemToList addItemToRegis" name="fromComparisonPage" />
											<input type="hidden" name="isLtlItem" value="${isLTLProduct}" />
											<input type="hidden" name="selServiceLevel" value="" class="addItemToList addItemToRegis" />
											<c:choose>
												<c:when test="${empty thumbnailImagePath}">
													<img class="hidden productImage" height="83" width="83" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${productName}" />
												</c:when>
												<c:otherwise>
													<img class="hidden productImage noImageFound" height="83" width="83" src="${scene7Path}/${thumbnailImagePath}" alt="${productName}" />
												</c:otherwise>
											</c:choose>
											<span class="productDetails hidden"><dsp:valueof param="element.productName" valueishtml="true" /></span>
										</li>

										<c:if test="${not isCollection && not isMultiSku && isProdActive}">
											<li class="multiButtonWrapper clearfix noMarBot">


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
														<dsp:param name="isItemExcluded" value="${isItemExcluded}" />
														<dsp:param name="caDisabled" value="${caDisabled}" />
														
													</dsp:include>
												<%-- Add To Registry Link logic on comparison page | End --%>

												<%-- Save for later Link on comparison page --%>
                                                <div id="btnAddToList" class="addToList fl padTop_10" <c:if test="${customizableRequired || inStock==false || isProdActive==false || isItemExcluded || caDisabled}">style="pointer-events: none;"</c:if> >
                                                    <!--img height="16" width="16" src="/_assets/global/images/compare/plus.png" class="noImageFound"/-->
                                                    <c:choose>
														<c:when test="${customizableRequired || inStock==false || isProdActive==false || isItemExcluded || caDisabled}">
															<span class="linkDisabled"><bbbl:label key='lbl_compare_save_for_later' language="${pageContext.request.locale.language}" /></span>
														</c:when>
														<c:otherwise>
															<a href="javascript:void(0);" onclick="rkg_micropixel('${appid}','wish')"><bbbl:label key='lbl_compare_save_for_later' language="${pageContext.request.locale.language}" /></a>
														</c:otherwise>
													</c:choose>
                                                    <!-- <a <c:if test="${customizableRequired || inStock==false || isProdActive==false || isItemExcluded || caDisabled}">class="linkDisabled"</c:if> href="javascript:void(0);" onclick="rkg_micropixel('${appid}','wish')"><bbbl:label key='lbl_compare_save_for_later' language="${pageContext.request.locale.language}" /></a> -->
                                                </div>

											</li>
										</c:if>
                                        </div>
									</ul>
								</div>
							</dsp:form>
							</div>
							<hr class="show-for-medium-down small-12 columns">
						</dsp:oparam>
					</dsp:droplet>

					<%-- Display empty slots on comparison page if products are less than 4 | Start--%>

					<%-- Sprint1(R2.2.1)| Sticky image and title on the comparison page | [Start] --%>
					<c:set var="emptySlotSize" value="${4-size}" />
					<%-- Sprint1(R2.2.1)| Sticky image and title on the comparison page | [End] --%>

					<c:forEach var="i" begin="1" end="${emptySlotSize}">
						<div class="columnData small-12 large-3 columns">
							<form class="compareProductForm" name="compareProductForm" actions="#" method="post">
								<div class="registryDataItemsWrap listDataItemsWrap clearfix noProductToCompare">
									<a href="${url}" class="prodImg block" title="Add Another Item">
										<img alt="Add Another Item" src="/_assets/global/images/copare-img-placeholder.png" class="noImageFound" />
									</a>
								</div>
							</form>
						</div>
					</c:forEach>
					</div>
                    </div>
					<div class="bucketPlaceholder hidden">
						<div class="columnData small-12 large-3 columns">
							<form class="compareProductForm" name="compareProductForm" actions="#" method="post">
								<div class="registryDataItemsWrap listDataItemsWrap clearfix noProductToCompare">
									<a href="${url}" class="prodImg block" title="Image">
										<img alt="Image" src="/_assets/global/images/copare-img-placeholder.png" class="noImageFound" />
									</a>
								</div>
							</form>
						</div>
					</div>
					<%-- Display empty slots on comparison page if products are less than 5 | End--%>
					<div class="row compare-section divider">
						<div class="small-12 large-2 columns">
							<h3 class="specification marLeft_10 headerbackground">
                               <bbbl:label key='lbl_compare_products_description' language="${pageContext.request.locale.language}" />
                            </h3>
						</div>
						<div class="small-12 large-10 columns cb productDescriptions productDescriptionsMain">
							<div class="row">
								<dsp:droplet name="ForEach">
									<dsp:param name="array" value="${productsList}" />
									<dsp:oparam name="output">
									<dsp:getvalueof var="shortDescription" param="element.shortDescription" />
									<dsp:getvalueof var="longDescription" param="element.longDescription" />
                                    <dsp:getvalueof var="productId" param="element.productId" />
                                    	<div class="descColumn"><h2 class="subheader show-for-medium-down">
													<span class="disableText"><dsp:valueof param="element.productName" valueishtml="true" /></span>
                                        </h2></div>
										<div class="descColumn small-12 large-3 columns p-secondary ${productId}">
											<c:if test="${not empty shortDescription}">
												<p class="p-secondary"><dsp:valueof param="element.shortDescription" valueishtml="true" /></p>

											</c:if>
											<c:if test="${not empty longDescription}">
												<dsp:valueof param="element.longDescription" valueishtml="true" />

											</c:if>
										</div>
									</dsp:oparam>
								</dsp:droplet>
							</div>
						</div>
					</div>
					<dsp:droplet name="ForEach">
						<dsp:param bean="ProductComparisonList.tableColumns" name="array" />
						<dsp:oparam name="output">
							<dsp:getvalueof param="element.heading" var="rowHeading" />
							<dsp:getvalueof param="element.name" var="rowPropertyName" />
							<c:set var="property" value="${0}" />
                                <c:if test="${rowPropertyName eq 'color' || rowPropertyName eq 'attributesList'}">
                                  <dsp:getvalueof var="property" bean="ProductComparisonList.${rowPropertyName}FlagClear" />
                                </c:if>
                                <c:if test="${property != size}">
                                <div class="row compare-section divider">
                                    <div class="small-12 large-2 columns">
            							<h3 class="specification marLeft_10 headerbackground">
                                           <bbbl:label key='lbl_pdc_header_${rowPropertyName}' language="${pageContext.request.locale.language}" />
                                        </h3>
                                    </div>
							<div class="small-12 large-10 columns cb productDescriptions productAttrDescriptions">
								<dsp:droplet name="ForEach">
									<dsp:param bean="ProductComparisonList.items" name="array" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="isCollection" param="element.collection" />
										<dsp:getvalueof var="isMultiSku" param="element.multiSku" />
										<dsp:getvalueof var="isProdActive" param="element.productActive" />
                                        <dsp:getvalueof var="productId" param="element.productId" />
                                        <dsp:getvalueof var="productName" param="element.productName" />
										<dsp:droplet name="/atg/dynamo/droplet/Switch">
											<dsp:param name="value" value="${rowPropertyName}" />
											<dsp:oparam name="attributesList">
													<dsp:getvalueof var="attributesList" param="element.attributesList" />
													<dsp:droplet name="ForEach">
														<dsp:param param="element.attributesList" name="array" />
														<dsp:oparam name="outputStart">
															<div class="descColumn"><h2 class="subheader show-for-medium-down">
																<span class="disableText">${productName}</span>
					                                        </h2></div>
		                                                  <div class="descColumn small-12 large-3 columns ${productId}">
                                                        </dsp:oparam>
														<dsp:oparam name="output">
															<dsp:getvalueof var="attributeVo" param="element" />
															<dsp:getvalueof var="imageURl" param="element.imageURL" />
															<dsp:getvalueof var="actionUrl" param="element.actionURL" />
															<c:choose>
																<c:when test="${not empty imageURl}">
																	<a href="${actionUrl}" class="newOrPopup"><img src="${imageURLPrice}" alt=" <dsp:valueof param="element.attributeDescrip" valueishtml="true"/>" /></a>
																</c:when>
																<c:otherwise>
																	<c:choose>
																		<c:when test="${not empty actionUrl}">
																			<a href="${actionUrl}" class="newOrPopup"><dsp:valueof param="element.attributeDescrip" valueishtml="true" /></a>
																		</c:when>
																		<c:otherwise>
																			<dsp:valueof param="element.attributeDescrip" valueishtml="true" />
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
															<div class="descColumn emptyAttribute small-12 large-3 columns ${productId}">
                                                                <bbbl:label key='lbl_compare_empty_table_attribute' language="${pageContext.request.locale.language}" />
                                                            </div>
                                                        </dsp:oparam>
                                                        <dsp:oparam name="outputEnd">
                                                            </div>
                                                        </dsp:oparam>
                                                      </dsp:droplet>

                                                </dsp:oparam>
                                                <dsp:oparam name="color">
                                                   <c:choose>
                                                      <c:when test="${isCollection eq 'true' && isProdActive=='true'}">
                                                      	<div class="descColumn"><h2 class="subheader show-for-medium-down">
															<span class="disableText"><dsp:valueof param="element.productName" valueishtml="true" /></span>
					                                     </h2></div>
                                                         <div class="descColumn small-12 large-3 columns ${productId}">
                                                            <a href="javascript:void(0);" class="compareSelectOptionList"><bbbl:label key='lbl_pdc_multicolor' language="${pageContext.request.locale.language}" /> </a>
                                                         </div>
                                                      </c:when>
                                                      <c:otherwise>
                                                         <dsp:droplet name="ForEach">
                                                                <dsp:param param="element.color" name="array" />
                                                                <dsp:oparam name="outputStart">
                                                                	<div class="descColumn"><h2 class="subheader show-for-medium-down">
																				<span class="disableText"><dsp:valueof param="element.productName" valueishtml="true" /></span>
							                                        </h2></div>
                                                                    <div class="descColumn small-12 large-3 columns ${productId}">
                                                                </dsp:oparam>
                                                                <dsp:oparam name="output">
                                                                   <dsp:getvalueof var="colorImagePath" param="key" />
                                                                   <dsp:getvalueof var="colorIm" param="element" />
                                                                   <img src="${scene7Path}/${colorImagePath}" height="20" width="20" alt="${colorIm}" />
                                                                </dsp:oparam>
                                                                <dsp:oparam name="empty">
                                                                   <div class="descColumn emptyColor small-12 large-3 columns ${productId}">
                                                                     <bbbl:label key='lbl_compare_empty_table_attribute' language="${pageContext.request.locale.language}" />
                                                                   </div>
                                                                </dsp:oparam>
                                                                <dsp:oparam name="outputEnd">
                                                                   </div>
                                                                </dsp:oparam>
                                                           </dsp:droplet>
                                                        </c:otherwise>
                                                    </c:choose>
											</dsp:oparam>
											<dsp:oparam name="vdcSku">
												<div class="descColumn"><h2 class="subheader show-for-medium-down">
															<span class="disableText"><dsp:valueof param="element.productName" valueishtml="true" /></span>
		                                        </h2></div>
												<div class="descColumn small-12 large-3 columns ${productId}">
													<dsp:getvalueof var="vdcSkuFlag" param="element.vdcSkuFlag" />
													<dsp:getvalueof var="isLtlAttributeApplicable" param="element.ltlAttributeApplicable" />
													<c:choose>
														<c:when test="${vdcSkuFlag == 'Yes'}">
															<c:choose>
																<c:when test="${isLtlAttributeApplicable}">
																	<div class="descColumn small-12 large-3 columns ${productId} prodAttribute"">
																		<dsp:getvalueof var="ltlAttributesList" param="element.ltlAttributesList" />
																		<dsp:droplet name="ForEach">
																			<dsp:param param="element.ltlAttributesList" name="array" />
																			<dsp:oparam name="output">
																				<dsp:getvalueof var="imageURl" param="element.imageURL" />
																				<dsp:getvalueof var="actionUrl" param="element.actionURL" />
																				<c:choose>
																					<c:when test="${not empty imageURl}">
																						<a href="${actionUrl}" class="newOrPopup"><img src="${imageURLPrice}" alt=" <dsp:valueof param="element.attributeDescrip" valueishtml="true"/>" /></a>
																					</c:when>
																					<c:otherwise>
																						<c:choose>
																							<c:when test="${not empty actionUrl}">
																								<a href="${actionUrl}" class="newOrPopup"><dsp:valueof param="element.attributeDescrip" valueishtml="true" /></a>
																							</c:when>
																							<c:otherwise>
																								<dsp:valueof param="element.attributeDescrip" valueishtml="true" />
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
																	<dsp:getvalueof var="vdcVO" param="element.vdcSku" />
																	<dsp:droplet name="ForEach">
																<dsp:param name="array" param="element.vdcSku" />
																<dsp:oparam name="output">
																		<dsp:getvalueof var="count" param="count" />
																		<dsp:getvalueof var="length" param="size" />
																		<dsp:getvalueof var="name" param="element.attributeDescrip" />
																		<dsp:getvalueof var="imageURl" param="element.imageURL" />
																		<dsp:getvalueof var="actionUrl" param="element.actionURL" />
																		<c:set var="language" value="${pageContext.request.locale.language}" scope="request" />
																		<c:set var="vendorName">
                                                                           <bbbl:label key='lbl_pdc_vendorship' language="${language}" />
                                                                        </c:set>
											             				<dsp:getvalueof var="chkCount" param="count" />
																		<dsp:getvalueof var="chkSize" param="size" />
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
																	               <a href="${actionUrl}" class="newOrPopup"><img src="${imageURl}" alt="${vendorName}" /></a>
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
																							<a href="${actionUrl}" class="newOrPopup"><img src="${imageURl}" alt="" /></a>
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
												<div class="descColumn"><h2 class="subheader show-for-medium-down">
															<span class="disableText"><dsp:valueof param="element.productName" valueishtml="true" /></span>
		                                        </h2></div>
												<dsp:getvalueof var="freeStandardShipping" param="element.freeStandardShipping" />
												<c:choose>
													<c:when test="${freeStandardShipping == 'Select Options'}">
														<c:choose>
															<c:when test="${isCollection && isProdActive==true}">
																<div class="descColumn small-12 large-3 columns ${productId}">
                                                                   <a href="javascript:void(0);" class="compareSelectOptionList">${freeStandardShipping}</a>
                                                                </div>
															</c:when>
															<c:when test="${isMultiSku && isProdActive==true}">
																<div class="descColumn small-12 large-3 columns ${productId}">
                                                                   <a href="javascript:void(0);" class="compareSelectOption">${freeStandardShipping}</a>
                                                                </div>
															</c:when>
														</c:choose>
													 </c:when>
													 <c:otherwise>
														 <c:choose>
														 	<c:when test="${freeStandardShipping eq 'Yes'}">
														 		<div class="descColumn small-12 large-3 columns ${productId}">${freeStandardShipping}</div>
														 	</c:when>
														 	<c:otherwise>
														 		<c:set var="isFreeShipPromoFlag" scope="request">
                                                                   <tpsw:switch tagName="FreeShipPromo_${appid}" />
                                                                </c:set>
														 		<c:set var="shipPromoMsg">
                                                                   <bbbt:textArea key='txt_compare_ship_promo_msg' placeHolderMap="${placeHolderMap}" language="${pageContext.request.locale.language}" />
                                                                </c:set>
														 		<c:choose>
														 			<c:when test="${isFreeShipPromoFlag && not empty shipPromoMsg}">
														 				<div class="descColumn small-12 large-3 columns ${productId}">${shipPromoMsg}</div>
														 			</c:when>
														 			<c:otherwise>
														 				<div class="descColumn small-12 large-3 columns ${productId}">${freeStandardShipping}</div>
														 			</c:otherwise>
														 		</c:choose>
														 	</c:otherwise>
														 </c:choose>
													</c:otherwise>
							 					</c:choose>
											</dsp:oparam>
											<dsp:oparam name="skuGiftWrapEligible">
												<div class="descColumn"><h2 class="subheader show-for-medium-down">
													<span class="disableText"><dsp:valueof param="element.productName" valueishtml="true" /></span>
			                                     </h2></div>
												<dsp:getvalueof var="skuGiftWrapEligible" param="element.skuGiftWrapEligible" />
												<c:choose>
													<c:when test="${skuGiftWrapEligible == 'Select Options'}">
														<c:choose>
															<c:when test="${isCollection && isProdActive==true}">
																<div class="descColumn small-12 large-3 columns ${productId}">
                                                                   <a href="javascript:void(0);" class="compareSelectOptionList">${skuGiftWrapEligible}</a>
                                                                </div>
															</c:when>
															<c:when test="${isMultiSku && isProdActive==true}">
																<div class="descColumn small-12 large-3 columns ${productId}">
                                                                   <a href="javascript:void(0);" class="compareSelectOption">${skuGiftWrapEligible}</a>
                                                                </div>
															</c:when>
														</c:choose>
													 </c:when>
													 <c:otherwise>
												 		<div class="descColumn small-12 large-3 columns ${productId}">${skuGiftWrapEligible}</div>
													 </c:otherwise>
											 	</c:choose>
											</dsp:oparam>
											<dsp:oparam name="clearance">
												<div class="descColumn"><h2 class="subheader show-for-medium-down">
													<span class="disableText"><dsp:valueof param="element.productName" valueishtml="true" /></span>
		                                        </h2></div>
												<dsp:getvalueof var="isClearence" param="element.clearance" />
												<c:choose>
													<c:when test="${isClearence == 'Select Options'}">
														<c:choose>
															<c:when test="${isCollection && isProdActive==true}">
																<div class="descColumn small-12 large-3 columns ${productId}">
                                                                   <a href="javascript:void(0);" class="compareSelectOptionList">${isClearence}</a>
                                                                </div>
															</c:when>
															<c:when test="${isMultiSku && isProdActive==true}">
																<div class="descColumn small-12 large-3 columns ${productId}">
                                                                   <a href="javascript:void(0);" class="compareSelectOption">${isClearence}</a>
                                                                </div>
															</c:when>
														</c:choose>
													</c:when>
													<c:otherwise>
														<div class="descColumn small-12 large-3 columns ${productId}">${isClearence}</div>
													</c:otherwise>
												</c:choose>
											</dsp:oparam>
											<dsp:oparam name="customizationCode">
												<dsp:getvalueof var="customizationCode" param="element.customizationCode"/>
												<dsp:getvalueof var="customizationCodeValues" param="element.customizationCodeValues"/>
												<div class="descColumn"><h2 class="subheader show-for-medium-down">
													<span class="disableText"><dsp:valueof param="element.productName" valueishtml="true" /></span>
                                        </h2></div>
													<c:choose>
														<c:when test="${customizationCode == 'Select Options' }">
															<c:choose>
															<c:when test="${isCollection && isProdActive==true}">
																<div class="descColumn small-12 large-3 columns ${productId}"><a href="javascript:void(0);" class="compareSelectOptionList">${customizationCode}</a></div>
															</c:when>
															<c:when test="${isMultiSku && isProdActive==true}">
																<div class="descColumn small-12 large-3 columns ${productId}"><a href="javascript:void(0);" class="compareSelectOption">${customizationCode}</a></div>
															</c:when>
															</c:choose>
														</c:when>
														<c:when test="${empty customizationCode}">
															<div class="descColumn small-12 large-3 columns emptyPersonalization"><bbbl:label key='lbl_compare_empty_table_attribute' language="${pageContext.request.locale.language}" /></div>
														</c:when>
														<c:otherwise>
															<c:choose>
																<c:when test="${customizationCodeValues !=null }">
																	<div class="descColumn small-12 large-3 columns ${productId}">

																		<dsp:droplet name="ForEach">
																			<dsp:param name="array" value="${customizationCodeValues}"/>
																			<dsp:oparam name="output">
																				<ul class="eximAttributeLi">

																					<li><dsp:valueof param="element"/></li>
																				</ul>
																			</dsp:oparam>
																		</dsp:droplet>
																	</div>
																</c:when>
																<c:otherwise>
																	<div class="descColumn small-12 large-3 columns ${productId}">${customizationCode}</div>
																</c:otherwise>
															</c:choose>
														</c:otherwise>
													</c:choose>
											</dsp:oparam>



										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
							</div>
       </div>
					</c:if>
						</dsp:oparam>
					</dsp:droplet>
					</div>

         <!-- Close Content Div -->
					<!-- Display each product's column attribute value -->
					<%-- Display tabular attributes of products using TableInfo component on comparison page| Start --%>
					<%-- This outer loop is to display the number of rows and is equal to the properties need to display for the product in table--%>
					<%-- These properties/ rows are configurable thru TableInfo Component --%>
					<%-- Display tabular attributes of products on comparison page| End--%>
					<!-- Find/Change Store Form jsps-->
					<c:import url="${contextPath}/_includes/modules/change_store_form.jsp" />
					<c:import url="${contextPath}/selfservice/find_in_store.jsp" />
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
        </script>
    </jsp:attribute>
   </bbb:pageContainer>
</dsp:page>
<%-- R2.2 Story - 178-a4 Product Comparison Page | End--%>
