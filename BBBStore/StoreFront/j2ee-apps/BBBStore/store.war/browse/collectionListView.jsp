<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet"/>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
<dsp:getvalueof var="appid" bean="Site.id" />
<dsp:getvalueof var="fromAjax" param="fromAjax" />
<c:set var="showShipCustomMsg" value="true"/>
<c:set var="AttributePDPCollection">
	<bbbl:label key='lbl_pdp_attributes_collection' language="${pageContext.request.locale.language}" />
</c:set>
<dsp:getvalueof var="skuIdfromURL" param="skuId" />
<dsp:getvalueof var="crossSellFlag" param="crossSellFlag"/>
<dsp:getvalueof var="desc" value="${fn:escapeXml(param.desc)}"/>
<dsp:getvalueof var="view" param="view"/>
<dsp:getvalueof var="btsValue" param="bts"/>
<c:set var="count" value="1"/>
<c:set var="collectionId_Omniture" scope="request"></c:set>
<c:set var="scene7Path">
	<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
</c:set>
<c:set var="omniPersonalizeButtonClick">
	<bbbl:label key='omni_personalize_button_click' language="${pageContext.request.locale.language}" />
</c:set>
<c:set var="ShipMsgDisplayFlag" scope="request"><bbbc:config key="ShipMsgDisplayFlag" configName="FlagDrivenFunctions"/></c:set>
<c:set var="shippingAttributesList">
	<bbbc:config key="shippingAttributesList" configName="ContentCatalogKeys" />
</c:set>
<c:set var="BazaarVoiceOn" scope="request" value="${param.BazaarVoiceOn}"/>
<c:set var="MapQuestOn" scope="request" value="${param.MapQuestOn}"/>
<c:set var="enableKatoriFlag" scope="request"><bbbc:config key="enableKatori" configName="EXIMKeys"/></c:set>
<c:set var="lblProductQuickView"><bbbl:label key="lbl_product_quick_view" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblReviewCount"><bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblReviewsCount"><bbbl:label key='lbl_reviews_count' language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblOfThe"><bbbl:label key="lbl_accessibility_of_the" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblAboutThe"><bbbl:label key="lbl_accessibility_about_the" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblSaveThe"><bbbl:label key="lbl_accessibility_save_the" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblFuturePurchase"><bbbl:label key="lbl_accessibility_future_purchase" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblFindThe"><bbbl:label key="lbl_accessibility_find_the" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblStoreNear"><bbbl:label key="lbl_accessibility_store_near_you" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblForThe"><bbbl:label key="lbl_accessibility_for_the" language="${pageContext.request.locale.language}" /></c:set>
<input type="hidden" id="enableKatoriFlag" name="enableKatoriFlag" value="${enableKatoriFlag}">
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof param="parentProductId" var="parentProductId"/>
	<dsp:getvalueof var="color" value="${fn:escapeXml(param.color)}"/>
<dsp:getvalueof var="childProducts" param="collectionVO.childProducts"/>
					<ul class="clearfix" id="listView">
					<dsp:droplet name="/atg/dynamo/droplet/ForEach">
					<dsp:param param="collectionVO.childProducts" name="array" />

						<dsp:oparam name="output">
						<%-- Check if product is available for international customer --%>
						<dsp:getvalueof var="isIntlRestricted" param="element.intlRestricted"/>
						<dsp:getvalueof var="childSKU" param="element.childSKUs"/>
						<dsp:getvalueof var="isLtlProduct" param="element.ltlProduct"/>						
						<c:if test="${not empty childSKU}">
						<li class="bdrBot collectionItems clearfix">

						<fieldset class="registryDataItemsWrap listDataItemsWrap <c:if test="${ (skuVO.customizableRequired || skuVO.customizationOffered) && not empty skuVO.customizableCodes}">personalizedItem</c:if>" data-refNum="" itemscope <c:if test="${empty fromAjax}">itemtype="http://schema.org/Product"</c:if> >
                            <dsp:getvalueof var="prodImage" param="element.productImages.mediumImage"/>
							<dsp:getvalueof var="prodName" param="element.name"/>
                            <c:choose>
                                <c:when test="${empty prodImage}">
                                    <img class="hidden productImage" height="83" width="83" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${prodName}" />
                                </c:when>
                                <c:otherwise>
                                    <img class="hidden productImage noImageFound" height="83" width="83" src="${scene7Path}/${prodImage}" alt="${prodName}" />
                                </c:otherwise>
                            </c:choose>
							<dsp:getvalueof var="productID" param="element.productId"/>
							<dsp:getvalueof var="productAttributes" param="element.attributesList"/>
							<dsp:getvalueof var="skuAttributes" value=""/>
							<dsp:getvalueof var="hasRebate" value=""/>
							<dsp:getvalueof var="eligibleRebates" value=""/>
							<dsp:getvalueof var="skuid" value=""/>
							<dsp:getvalueof var="bopusAllowed" value=""/>
							<c:set var="isLtlItem" value="${false}"/>	
							<%-- <input type="hidden" name="skuId" value="${skuid}" class="_skuId addItemToRegis addItemToList" /> --%>
							<dsp:getvalueof var="emailStockAlertsEnabled" value="" />
							<dsp:getvalueof var="skuinStock" value="" />
							<dsp:getvalueof var="selectedSKUStock" value="" />
							<%-- <input type="hidden" name="prodId" class="_prodId addItemToRegis addItemToList" value="${productID}" /> --%>
							<dsp:getvalueof var="childSKU" param="element.childSKUs"/>
							<c:set var="CertonaContext" scope="request">${CertonaContext}${productID};</c:set>
							<c:set var="collectionId_Omniture" scope="request">${collectionId_Omniture};${productID};;;;eVar29=${parentProductId},</c:set>
							<dsp:getvalueof var="colorMatched" param="element.colorMatched"/>
							<c:set var="isCustomizationRequired" value=""/>
							<c:set var="customizationOffered" value=""/>
							<c:choose>
							<c:when test="${(null != childSKU) && ((fn:length(childSKU) == 1 ) || ((fn:length(childSKU) > 1 ) && not empty color && colorMatched))}">
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array" value="${childSKU}" />
								<dsp:oparam name="output">
										<dsp:getvalueof var="childSkuId" param="element"/>
										<dsp:droplet name="ProductDetailDroplet">
											<dsp:param name="id" value="${productID}" />
											<dsp:param name="siteId" param="siteId"/>
											<dsp:param name="skuId" value="${childSkuId}"/>
												<dsp:oparam name="output">
												<dsp:getvalueof var="skuColor" param="pSKUDetailVO.color"/>
													<c:if test="${(fn:length(childSKU) == 1) || (not empty color and color eq skuColor)}">
														<dsp:getvalueof var="selectedSKUTemp" param="selected"/>
														<c:if test="${not empty selectedSKUTemp}">
															<c:set var="selectedSKU" value="${selectedSKUTemp}"/>
															<dsp:getvalueof var="selectedSKUStock" param="inStock" />
														</c:if>
														<c:if test="${not empty color and color eq skuColor}">
															<dsp:getvalueof var="prodName" param="pSKUDetailVO.displayName"/>
														</c:if>
														<dsp:getvalueof var="skuVO" param="pSKUDetailVO"/>
														
														<dsp:getvalueof var="skuAttributes" param="pSKUDetailVO.skuAttributes"/>
														<dsp:getvalueof var="displayShipMsg" param="childProducts.displayShipMsg"/>
														<dsp:getvalueof var="hasRebate" param="pSKUDetailVO.hasRebate"/>
													<dsp:getvalueof var="sKUDetailVO" param="pSKUDetailVO"/>
													<dsp:getvalueof var="shippingRestricted" value="${sKUDetailVO.shippingRestricted}"/>
														<dsp:getvalueof var="eligibleRebates" param="pSKUDetailVO.eligibleRebates"/>
														<dsp:getvalueof var="skuid" param="pSKUDetailVO.skuId"/>
													<dsp:getvalueof var="isCustomizationRequired" param="pSKUDetailVO.customizableRequired"/>		
													<input type="hidden" name="isCustomizationRequired" value="${isCustomizationRequired}" />											
														<dsp:getvalueof var="ltlItem" param="pSKUDetailVO.ltlItem"/> <%-- LTL 398  --%>
														<c:set var="isLtlItem" value="${ltlItem}"/>
														<dsp:getvalueof var="bopusAllowed" param="pSKUDetailVO.bopusAllowed"/>
														<%-- <input type="hidden" name="skuId" value="${skuid}" class="_skuId addItemToRegis addItemToList" /> --%>
														<dsp:getvalueof var="emailStockAlertsEnabled" param="pSKUDetailVO.emailStockAlertsEnabled" />
														<dsp:getvalueof var="skuinStock" param="inStock" />
													<dsp:getvalueof var="customizationOffered" param="pSKUDetailVO.customizationOffered"/>
													<dsp:getvalueof var="customizableCodes" param="pSKUDetailVO.customizableCodes"/>
													<dsp:getvalueof var="vendorName" param="productVO.vendorInfoVO.vendorName"/>
														<%-- <dsp:getvalueof var="isIntlRestricted" param="productVO.intlRestricted"/> --%>
													</c:if>
												</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
							</c:when>
							<c:otherwise>		
								<c:forEach var="obj" items="${childSKU}">
									<c:if test="${obj == skuIdfromURL}">
										<dsp:getvalueof var="skuIdToBeSend" value="${obj}"/>
											<dsp:droplet name="PriceDroplet">
											<dsp:param name="product" value="${skuIdToBeSend}"/>
											<dsp:param name="sku" value="${skuIdToBeSend}"/>
												<dsp:oparam name="output">
													<dsp:getvalueof var="omniPrice" vartype="java.lang.Double" param="price.listPrice"/>
												</dsp:oparam>
											</dsp:droplet>
										<input id="certonaPrice" type="hidden" value="${omniPrice}" />
									</c:if>
								</c:forEach>
							</c:otherwise>
							</c:choose>
							<!-- Display product attributes if MSWP else sku attributes if SSWP - Start -->
							<c:choose>
								<c:when test="${not empty skuAttributes}">
									<c:set var="attributeList" value= "${skuAttributes}"/>
								</c:when>
								<c:otherwise>
									<c:set var="attributeList" value="${productAttributes}"/>
								</c:otherwise>
							</c:choose>
							<!-- Display product attributes if MSWP else sku attributes if SSWP - End -->
							<c:set var="collFirstChildSKU">
							        <c:out value="${childSKU[0]}" />
						    </c:set>
							<c:if test="${not empty collFirstChildSKU && count eq 1}">
								<div id="firstproduct" style="display:none">
								LT_F_PRD_ID:=${productID}
								LT_F_SKU_ID:=${collFirstChildSKU}
								</div>
								<c:set var="count" value="2"/>
							</c:if>

							<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
								<dsp:param name="id" value="${productID}" />
								<dsp:param name="itemDescriptorName" value="product" />
								<dsp:param name="repositoryName"
									value="/atg/commerce/catalog/ProductCatalog" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
										param="url" />
								</dsp:oparam>
							</dsp:droplet>

							<c:choose>
							<c:when test="${(crossSellFlag ne null) && (crossSellFlag eq 'true')}">
                                <script type="text/javascript">
                                    function quickViewCrossSell() {
                                        pdpCrossSellProxy('crossSell', '${desc}');
                                    };
                                </script>
								<c:set var="onClickEvent">javascript:pdpCrossSellProxy('crossSell', '${desc}')</c:set>
							</c:when>
							<c:otherwise>
                                <script type="text/javascript">
                                    function quickViewCrossSell() {
                                    };
                                </script>
								<c:set var="onClickEvent" value=""/>
							</c:otherwise>
							</c:choose>
							<dsp:getvalueof var="CategoryId" param="categoryId"/>
							<div class="grid_2 alpha marRight_20">
								<c:choose>
									<c:when test="${not empty CategoryId}">
										<dsp:a iclass="prodImg" page="${finalUrl}?poc=${parentProductId}&categoryId=${CategoryId}" title="${prodName}" onclick="${onClickEvent}">
											<dsp:getvalueof var="showImage" param="collectionVO.showImagesInCollection"/>
											<c:if test="${showImage != false}">
											<c:choose>
											<c:when test="${empty prodImage}">
												<img src="${imagePath}/_assets/global/images/no_image_available.jpg" width="146" height="146" alt="${prodName}" />
											</c:when>
											<c:otherwise>
												<img class="noImageFound" src="${scene7Path}/${prodImage}" width="146" height="146" alt="${prodName}" />
											</c:otherwise>
											</c:choose>
											</c:if>
										</dsp:a>
									</c:when>
									<c:otherwise>
										<dsp:a iclass="prodImg" page="${finalUrl}?poc=${parentProductId}" title="${prodName}" onclick="${onClickEvent}">
											<dsp:getvalueof var="showImage" param="collectionVO.showImagesInCollection"/>
											<c:if test="${showImage != false}">
											<c:choose>
											<c:when test="${empty prodImage}">
												<img src="${imagePath}/_assets/global/images/no_image_available.jpg" width="146" height="146" alt="${prodName}" />
											</c:when>
											<c:otherwise>
												<img class="noImageFound" src="${scene7Path}/${prodImage}" width="146" height="146" alt="${prodName}" />
											</c:otherwise>
											</c:choose>
											</c:if>
										</dsp:a>
									</c:otherwise>
								</c:choose>
								<span onclick="${onClickEvent}" class="quickView showOptionMultiSku block marTop_5" role="link" aria-label="${lblProductQuickView} ${lblOfThe} ${prodName}"><bbbl:label key="lbl_product_quick_view" language="${pageContext.request.locale.language}" /></span>
							</div>
							<dsp:getvalueof var="oosProdId" value="${productID}" />
									<c:choose>
											<c:when test="${not empty selectedSKUStock}">
												<c:set var="inStock" value="${selectedSKUStock}" />
											</c:when>
									<c:when test="${empty skuinStock}">
										<c:set var="inStock" value="true" />
									</c:when>
									<c:otherwise>
										<dsp:getvalueof var="inStock" value="${skuinStock}" />
									</c:otherwise>
									</c:choose>

							<div class="${showImage?'grid_4':'grid_6'} alpha">
								<ul class="prodInfo">
									<li class="prodName">
										<c:choose>
											<c:when test="${not empty CategoryId}">
													<dsp:a page="${finalUrl}?poc=${parentProductId}&categoryId=${CategoryId}" title="${prodName}" onclick="${onClickEvent}">
														${prodName}
													</dsp:a>
											</c:when>
											<c:otherwise>
												<dsp:a page="${finalUrl}?poc=${parentProductId}" title="${prodName}" onclick="${onClickEvent}">
													${prodName}
												</dsp:a>
											</c:otherwise>
										</c:choose>
									</li>

									<%-- Rating --%>
									 <dsp:getvalueof var="ReviewCountvar" param="element.bvReviews.totalReviewCount"/>
									<c:set var="ReviewCount">${ReviewCountvar}</c:set>

									<c:if test="${BazaarVoiceOn}">
										<dsp:getvalueof var="ratingAvailable" param="element.bvReviews.ratingAvailable"></dsp:getvalueof>
										<c:choose>
											<c:when test="${ratingAvailable == true}">
												<dsp:getvalueof var="fltValue" param="element.bvReviews.averageOverallRating"/>
												<dsp:getvalueof param="element.bvReviews.totalReviewCount" var="totalReviewCount"/>
												<c:choose>
													<c:when test="${totalReviewCount == 1}">
														<li class="prodReviews clearfix noMar metaFeedback">														
														<span class="ratingTxt ratingsReviews prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel"><dsp:valueof param="element.bvReviews.ratingsTitle"/></span></span><span class="reviewTxt"><a href="${finalUrl}?skuId=${catalogRefId}&amp;categoryId=${CategoryId}&amp;showRatings=true" role="link" aria-label="${totalReviewCount} ${lblReviewCount} ${lblForThe} ${prodName}">
														${ReviewCount} <bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" /></a></span></li>
													</c:when>
													<c:otherwise>
														<li class="prodReviews clearfix noMar metaFeedback">														
															<span class="ratingTxt ratingsReviews prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel"><dsp:valueof param="element.bvReviews.ratingsTitle"/></span></span><span class="reviewTxt"><a href="${finalUrl}?skuId=${catalogRefId}&amp;categoryId=${CategoryId}&amp;showRatings=true" role="link" aria-label="${totalReviewCount} ${lblReviewsCount} ${lblForThe} ${prodName}">
														${ReviewCount} <bbbl:label key='lbl_reviews_count' language="${pageContext.request.locale.language}" /></a></span></li>
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:otherwise>
												<dsp:getvalueof var="fltValue" param="element.bvReviews.averageOverallRating"/>
												<dsp:getvalueof var="totalReviewCount" value="0"></dsp:getvalueof>
												<li class="prodReviews clearfix noMar metaFeedback">												
												<span class="ratingTxt ratingsReviews prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel"><dsp:valueof param="element.bvReviews.ratingsTitle"/></span></span><span class="reviewTxt"><a href="${finalUrl}?skuId=${catalogRefId}&amp;categoryId=${CategoryId}&amp;showRatings=true" role="link" aria-label="${totalReviewCount} ${lblReviewCount} ${lblForThe} ${prodName}"> ${ReviewCount} <bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" /></a></span></li>
											</c:otherwise>
										</c:choose>
									</c:if>
									</ul>
									<input type="hidden" class="isInStock" value="${inStock}"/>
								
								<div class="prodPrice fl">
									<c:set var="inputFieldPrice"></c:set>
									<dsp:getvalueof var="priceRangeDescription" param="element.priceRangeDescription" />
									<dsp:getvalueof var="salePriceRangeDescription" param="element.salePriceRangeDescription" />
									<dsp:getvalueof var="priceLabelCodeSKU" value="${skuVO.pricingLabelCode }" />
									<dsp:getvalueof var="inCartFlagSKU" value="${skuVO.inCartFlag }" />
									<c:choose>
										<c:when test="${not empty skuid}">
										
											<dsp:include page="product_details_price.jsp">
												<dsp:param name="product" value="${productID}"/>
												<dsp:param name="sku" value="${skuid}"/>
												<dsp:param name="priceLabelCodeSKU" value="${priceLabelCodeSKU}" />
												<dsp:param name="inCartFlagSKU" value="${inCartFlagSKU}" />
											</dsp:include>
										
											<c:set var="inputFieldPrice">${omniPrice}</c:set>
											<c:if test="${skuIdfromURL == skuid}">
												<input id="certonaPrice" type="hidden" value="${certonaPrice}" />
											</c:if>
										</c:when>
										<c:otherwise>
											<dsp:getvalueof var="isdynamicPriceProd" param="element.dynamicPricingProduct" />
											<dsp:getvalueof var="priceLabelCodeProd" param="element.priceLabelCode" />
											<dsp:getvalueof var="inCartFlagProd" param="element.inCartFlag" />
											<%--BBBH-4488 Price Code Change for InCart Stories Start --%>
											<dsp:include page="browse_price_frag.jsp">
											    <dsp:param name="priceLabelCode" value="${priceLabelCodeProd}" />
												<dsp:param name="inCartFlag" value="${inCartFlagProd}" />
												<dsp:param name="salePrice" value="${salePriceRangeDescription}" />
												<dsp:param name="listPrice" value="${priceRangeDescription}" />
												<dsp:param name="isdynamicPriceEligible" value="${isdynamicPriceProd}" />
												<dsp:param name="isFromPDP" value="false" />
											</dsp:include>   		
											<%--BBBH-4488 Price Code Change for InCart Stories End --%>
											<c:set var="inputFieldPrice">${salePriceRangeDescription}</c:set>
										    <c:set var="inputFieldPrice">${priceRangeDescription}</c:set>
										</c:otherwise>
									</c:choose>
									 <c:if test="${ShipMsgDisplayFlag && showShipCustomMsg}">
										<div class="freeShipBadge">
											<span class="prod-attrib prod-attrib-free-ship">
												<c:choose>
													<c:when test="${not empty displayShipMsg}">
														${displayShipMsg}
													</c:when>
													<c:otherwise>
													<dsp:valueof param="element.displayShipMsg" valueishtml="true"/>
													</c:otherwise>
												</c:choose>
											</span>										</div>
									</c:if>
								</div>
									<%--Katori integration on Product Compare page as a part of BPSI-2430 changes start --%>
									<c:set var="customizableCodes" value="${skuVO.customizableCodes}" scope="request"/>
									<c:set var="customizeCTACodes" scope="request">
										<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys" />
									</c:set>
										<c:choose>
											<c:when test="${not empty skuVO.customizableCodes && fn:contains(customizeCTACodes, skuVO.customizableCodes)}">
												<c:set var="personalizationTxt">
													<bbbl:label key="lbl_customize_this" language="${pageContext.request.locale.language}"/>
												</c:set>
												<c:set var="customizeAttr" value="customize"/>
											</c:when>
											<c:otherwise>
												<c:set var="personalizationTxt">
													<bbbl:label key="lbl_personalize_this" language="${pageContext.request.locale.language}"/>
												</c:set>
												<c:set var="customizeAttr" value=""/>
											</c:otherwise>
										</c:choose>
										<c:choose>
											<c:when test="${ (isCustomizationRequired || customizationOffered) && not empty customizableCodes && !isInternationalCustomer}">
												<div class="personalizationOffered clearfix cb fl marTop_5">
												<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="${contextPath}${finalUrl}?poc=${parentProductId}&openKatori=true&skuId=${skuid}"</c:otherwise></c:choose> class="collectionPersonalize ${customizeAttr} <c:if test="${enableKatoriFlag=='false'}">disabled</c:if>" role="button" data-sku="${skuid}" data-product="${productId}" data-refnum="" data-quantity="" data-vendor-name="${vendorName}" <c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
														${personalizationTxt}
													</a>
												<c:choose>
												<c:when test="${enableKatoriFlag}"><span class="freeDetailWrap">
													<c:if test="${not empty skuVO.personalizationType && skuVO.personalizationType =='PB'}">
														<span class="feeApplied"><bbbl:label key='lbl_PB_Fee_detail' language="${pageContext.request.locale.language}" /></span>	
													</c:if>
													<c:if test="${not empty skuVO.personalizationType && skuVO.personalizationType =='PY'}">
													<span class="feeApplied">	<bbbl:label key='lbl_PY_Fee_detail' language="${pageContext.request.locale.language}" /></span>	
													</c:if>
													<c:if test="${not empty skuVO.personalizationType && skuVO.personalizationType =='CR'}">
													<span class="feeApplied">	<bbbl:label key='lbl_CR_Fee_detail' language="${pageContext.request.locale.language}" /></span>	
													</c:if>
													
														<bbbl:label key='lbl_cart_personalization_detail' language="${pageContext.request.locale.language}" />
													</span>
													<div class="personalizeToProceed <c:if test="${not isCustomizationRequired}"> hidden </c:if>" >
                                                         <c:choose>
															<c:when test="${not empty customizableCodes && fn:contains(customizeCTACodes, customizableCodes)}">
																<bbbl:label key='lbl_customization_required_msg' language="${pageContext.request.locale.language}" />
															</c:when>
															<c:otherwise>
																<bbbl:label key='lbl_personalization_required_msg' language="${pageContext.request.locale.language}" />
															</c:otherwise>
														</c:choose>
                                                       </div>	
												</c:when>
												<c:otherwise>
												
													<span class="unavailablePersonalize marTop_10">
														<bbbl:label key='lbl_pdp_personalization_unavailable' language="${pageContext.request.locale.language}" />
													</span>
												</c:otherwise>
												</c:choose>
												</div>
											</c:when>
											<c:otherwise>
													<c:choose>
														<c:when test="${isInternationalCustomer}">
															<c:choose>
																<c:when test="${(isCustomizationRequired || customizationOffered) && not empty customizableCodes}">
																	<div class="personalizationOffered clearfix cb fl marTop_5">
																		<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="#"</c:otherwise></c:choose> class="personalize ${customizeAttr} disabled" role="button" data-vendor-name="${vendorName}" data-product="${productId}" data-sku="${pDefaultChildSku}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
																			${personalizationTxt}
																		</a>
																		<span class="personalizationIntlShippingMsg"><bbbt:textArea key="txt_pdp_per_unavailable_intl" language="${pageContext.request.locale.language}" /></span>
																	</div>
																</c:when>
																<c:otherwise>
																	<div class="personalizationOffered clearfix cb fl hidden marTop_5">
																		<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="#"</c:otherwise></c:choose> class="personalize ${customizeAttr} disabled" role="button" data-vendor-name="${vendorName}" data-product="${productId}" data-sku="${pDefaultChildSku}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
																			${personalizationTxt}
																		</a>
																		<span class="personalizationIntlShippingMsg"><bbbt:textArea key="txt_pdp_per_unavailable_intl" language="${pageContext.request.locale.language}" /></span>
																	</div>
																</c:otherwise>
															</c:choose>														
														</c:when>
														<c:otherwise>
															<div class="personalizationOffered clearfix cb fl hidden marTop_5">
															<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="${contextPath}${finalUrl}?poc=${parentProductId}&openKatori=true&skuId=${skuid}"</c:otherwise></c:choose> class="collectionPersonalize ${customizeAttr} <c:if test="${enableKatoriFlag=='false'}">disabled</c:if>" data-vendor-name="${vendorName}" role="button" data-product="${productId}" data-sku="${skuid}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
																	${personalizationTxt}
																</a>
															<c:choose>
																<c:when test="${enableKatoriFlag}">
																<span class="feeAppliedPB hidden"><bbbl:label key='lbl_PB_Fee_detail' language="${pageContext.request.locale.language}" /></span>	
																<span class="feeAppliedPY hidden">	<bbbl:label key='lbl_PY_Fee_detail' language="${pageContext.request.locale.language}" /></span>	
																<span class="feeAppliedCR hidden">	<bbbl:label key='lbl_CR_Fee_detail' language="${pageContext.request.locale.language}" /></span>	
																<span class="personalizationDetail">
																	<bbbl:label key='lbl_cart_personalization_detail' language="${pageContext.request.locale.language}" />
																</span>	
			                                                       <div class="personalizeToProceed <c:if test="${not isCustomizationRequired}"> hidden </c:if>" >
			                                                         <c:choose>
																		<c:when test="${not empty customizableCodes && fn:contains(customizeCTACodes, customizableCodes)}">
																			<bbbl:label key='lbl_customization_required_msg' language="${pageContext.request.locale.language}" />
																		</c:when>
																		<c:otherwise>
																			<bbbl:label key='lbl_personalization_required_msg' language="${pageContext.request.locale.language}" />
																		</c:otherwise>
																	</c:choose>
			                                                       </div>
																   </c:when>
																	<c:otherwise>
																		<span class="unavailablePersonalize marTop_10"><bbbl:label key='lbl_pdp_personalization_unavailable' language="${pageContext.request.locale.language}" /></span>
																	</c:otherwise>
																</c:choose>
															</div>
														</c:otherwise>
													</c:choose>
											</c:otherwise>
									</c:choose>
									<%--Katori integration on Product Compare page as a part of BPSI-2430 changes end --%>	
								<c:if test="${isInternationalCustomer && isIntlRestricted}">
									<div class="notAvailableIntShipMsg padTop_15 padBottom_10 cb clearfix"><bbbl:label key='lbl_pdp_intl_restricted_message' language="${pageContext.request.locale.language}" /></div>
								</c:if>
							</div>
							<div class="grid_6 omega">
								<div class="priceQuantityNotAvailable grid_6 alpha omega <c:if test="${inStock==true}">hidden</c:if> fl">
										  <div class="message fr">
												 <input type="hidden" name="oosProdId" value="${oosProdId}" />
												 <c:choose>
													<c:when test="${inStock==true}">
													<input type="hidden" value="" name="oosSKUId" class="_oosSKUId"/>
													</c:when>
													<c:otherwise>
													<input type="hidden" value="${skuid}" name="oosSKUId" class="_oosSKUId"/>
													</c:otherwise>
												</c:choose>
												 <div class="error normalFontSize textRight"><bbbl:label key='lbl_pdp_product_notavailable_shipping' language="${pageContext.request.locale.language}" /></div>
												   <c:if test="${OutOfStockOn}">
												   <c:choose>
													<c:when test="${((null eq emailStockAlertsEnabled) || (emailStockAlertsEnabled==true)) && (inStock==false)}">
												 <div class="info normalFontSize textRight bold"><a class="info fl marLeft_10 lnkNotifyOOS" href="#"><bbbl:label key='lbl_pdp_product_notify_item_available' language="${pageContext.request.locale.language}" /></a></div>
												 </c:when>
												 <c:otherwise>
												 <div class="info normalFontSize textRight bold hidden"><a class="info fl marLeft_10 lnkNotifyOOS" href="#"><bbbl:label key='lbl_pdp_product_notify_item_available' language="${pageContext.request.locale.language}" /></a></div>
												 </c:otherwise>
												 </c:choose>
												 </c:if>
										  </div>
								</div>
									<div class="fr grid_6 prodAttributeContainer alpha omega <c:if test="${inStock==false}">hidden</c:if>">
										<div class="rebateContainer collectionRebateContainer prodAttribWrapper fr">
											<c:set var="rebatesOn" value="${false}" />
											<c:if test="${not empty hasRebate && hasRebate}">
												<c:if test="${(null != eligibleRebates) && (fn:length(eligibleRebates) == 1 )}">
													<c:set var="rebatesOn" value="${true}" />
												</c:if>
											</c:if>
										 <c:if test="${not empty attributeList}">
											<dsp:droplet name="/atg/dynamo/droplet/ForEach">
													<dsp:param value="${attributeList}" name="array" />
														<dsp:oparam name="output">
														<dsp:getvalueof var="placeHolder" param="key"/>
														<c:if test="${(placeHolder != null) && (placeHolder eq AttributePDPCollection)}">
														<dsp:droplet name="/atg/dynamo/droplet/ForEach">
														<dsp:param param="element" name="array" />
														<dsp:param name="sortProperties" value="+priority"/>
															<dsp:oparam name="output">
																<dsp:getvalueof var="attrId" param="element.attributeName" />
																	<c:if test="${fn:contains(shippingAttributesList,attrId)}">
																		<c:set var="showShipCustomMsg" value="false"/>
																</c:if>
																
																<dsp:getvalueof var="chkCount" param="count"/>
																<dsp:getvalueof var="chkSize" param="size"/>
																<c:set var="sep" value="seperator" />
																<c:if test="${chkCount == chkSize}">
																	<c:choose>
																	<c:when test="${rebatesOn}">
																		<c:set var="sep" value="seperator" />
																	</c:when>
																	<c:otherwise>
																		<c:set var="sep" value="" />
																	</c:otherwise>
																	</c:choose>
																</c:if>
																<dsp:getvalueof var="attributeDescripTop" param="element.attributeDescrip"/>
																<dsp:getvalueof var="imageURLTop" param="element.imageURL"/>
																<dsp:getvalueof var="actionURLTop" param="element.actionURL"/>
																	<c:choose>
																			 <c:when test="${null ne attributeDescripTop}">
																					<c:choose>
																						   <c:when test="${null ne imageURLTop}">
																									 <span class="attribs"><img src="${imageURLTop}" alt="" /><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></span>
																						   </c:when>
																						   <c:otherwise>
																								 <c:choose>
																										 <c:when test="${null ne actionURLTop}">
																											   <span class="attribs "><a href="${actionURLTop}" class="newOrPopup"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a></span>
																										 </c:when>
																										 <c:otherwise>
																											   <span class="attribs "><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></span>
																										 </c:otherwise>
																								  </c:choose>
																						   </c:otherwise>
																					 </c:choose>
																			 </c:when>
																			 <c:otherwise>
																				   <c:if test="${null ne imageURLTop}">
																						   <c:choose>
																								  <c:when test="${null ne actionURLTop}">
																											<span class="attribs"><a href="${actionURLTop}" class="newOrPopup"><img src="${imageURLTop}" alt="" /></a></span>
																								  </c:when>
																								  <c:otherwise>
																											<span class="attribs"><img src="${imageURLTop}" alt="" /></span>
																								  </c:otherwise>
																						   </c:choose>
																					</c:if>
																			 </c:otherwise>
																		</c:choose>
															</dsp:oparam>
														</dsp:droplet>
														</c:if>
														</dsp:oparam>
												</dsp:droplet>
											  </c:if>
											<c:if test="${not empty hasRebate && hasRebate}">
											<dsp:droplet name="/atg/dynamo/droplet/ForEach">
											<dsp:param name="array" value="${eligibleRebates}"/>
												<dsp:oparam name="output">
													<dsp:getvalueof var="chkCount1" param="count"/>
													<dsp:getvalueof var="chkSize1" param="size"/>
													<c:set var="sep1" value="seperator" />
													<c:if test="${chkCount1 == chkSize1}">
														<c:set var="sep1" value="" />
													</c:if>
													<dsp:getvalueof var="rebate" param="element"/>
													<span class="attribs" ><a href="${rebate.rebateURL}" class="links" target="_blank" title="Rebate"><c:out value="${rebate.rebateDescription}" escapeXml="false"/></a></span>
												</dsp:oparam>
											</dsp:droplet>
											</c:if>
											<span class="shippingRestrictionsLink attribs <c:if test="${not shippingRestricted or empty shippingRestricted}"> hidden</c:if>">
		  										<a class="shippingRestrictionsApplied" href="/store/cart/static/shipping_restrictions_applied.jsp" data-skuId="${skuid}" title="<bbbl:label key="lbl_shipping_restrictions_applied" language="${pageContext.request.locale.language}"/>">
		  										<bbbl:label key="lbl_shipping_restrictions_details" language="<c:out param='${language}'/>"/></a>
		  									</span>
										</div>
									</div>

									<div class="fr prodInfoContainer alpha omega listViewQtyWrapper">
										<dsp:droplet name="/atg/dynamo/droplet/ForEach">
											<dsp:param param="element.rollupAttributes" name="array" />
												<dsp:oparam name="output">
												<dsp:getvalueof var="prodType" param="key"/>
												<dsp:getvalueof var="attributeSize" param="size" />
												<c:choose>
													<c:when test="${attributeSize == 1}">
														<c:set var="isMultiRollUpFlag" value="false" />	
													</c:when>
													<c:otherwise>
														<c:set var="isMultiRollUpFlag" value="true" />
													</c:otherwise>
												</c:choose>
												<c:choose>
												<c:when test="${prodType eq 'SIZE'}">
													<div class="prodSize fl">
														<label for="selProdSize"><bbbl:label key="lbl_sizes_dropdown" language ="${pageContext.request.locale.language}"/></label>
														<div class="psize">
																	<select name="selProdSize" class="customSelectBoxCollection" aria-required="false" >
															<dsp:droplet name="/atg/dynamo/droplet/ForEach">
															<dsp:param param="element" name="array" />
																<dsp:oparam name="outputStart">
																	<option value=""><bbbl:label key='lbl_pdp_product_select' language="${pageContext.request.locale.language}" /></option>
																</dsp:oparam>
																<dsp:oparam name="output">
																	<dsp:getvalueof var="elementValue" param="element.rollupAttribute"/>
																	<option value="${elementValue}"><c:out value="${elementValue}" escapeXml="false"/></option>
																</dsp:oparam>
															</dsp:droplet>
															</select>
														</div>
														<input type="hidden" name="prodSize" class="_prodSize addItemToRegis addItemToList" value="" />
													</div>
												</c:when>
												<c:otherwise>
													<c:choose>
													<c:when test="${prodType eq 'FINISH'}">
														<div class="prodFinish fl">
															<label for="selProdFinish"><bbbl:label key="lbl_finish_dropdown" language ="${pageContext.request.locale.language}"/></label>
															<div class="pfinish">
																<select name="selProdFinish" class="customSelectBoxCollection" aria-required="false" >
																	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																	<dsp:param param="element" name="array" />
																		<dsp:oparam name="outputStart">
																			<option value=""><bbbl:label key='lbl_pdp_product_select' language="${pageContext.request.locale.language}" /></option>
																		</dsp:oparam>
																		<dsp:oparam name="output">
																			<dsp:getvalueof var="elementValue" param="element.rollupAttribute"/>
																				<dsp:getvalueof var="smallImagePath" param="element.smallImagePath"/>
																				<dsp:getvalueof var="thumbnailImagePath" param="element.thumbnailImagePath"/>
																				<dsp:getvalueof var="swatchImagePath"
																				param="element.swatchImagePath" />

																				<c:choose>
																				  <c:when test="${color eq elementValue}">
																				  	<c:set var="finishValue" value="${elementValue}" />
																					 <option value="${elementValue}" selected="selected" data-imgURLSmall="${scene7Path}/${smallImagePath}" data-imgURLThumb="${scene7Path}/${thumbnailImagePath}"
																					 data-imgSrc="${scene7Path}/${swatchImagePath}">${elementValue}</option>
																				  </c:when>
																				  <c:otherwise>
																					<option value="${elementValue}" data-imgURLSmall="${scene7Path}/${smallImagePath}" data-imgURLThumb="${scene7Path}/${thumbnailImagePath}"
																					data-imgSrc="${scene7Path}/${swatchImagePath}">${elementValue}</option>
																				  </c:otherwise>
																				</c:choose>
																		</dsp:oparam>
																	</dsp:droplet>
																</select>
															</div>
															<input type="hidden" name="prodFinish" class="_prodFinish addItemToRegis addItemToList" value="${finishValue}" />
														</div>
													</c:when>
													<c:otherwise>
													<c:if test="${prodType eq 'COLOR'}">
														<div class="prodColor fl">
															<label for="selProdColor"><bbbl:label key="lbl_colors_dropdown" language ="${pageContext.request.locale.language}"/></label>
															<div class="pcolor">
																<select name="selProdColor" class="customSelectBoxCollection" aria-required="false" >
																	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																	<dsp:param param="element" name="array" />
																		<dsp:oparam name="outputStart">
																			<option value=""><bbbl:label key='lbl_pdp_product_colour_select' language="${pageContext.request.locale.language}" /></option>
																		</dsp:oparam>
																		<dsp:oparam name="output">
																			<dsp:getvalueof var="elementValue" param="element.rollupAttribute"/>
																				<dsp:getvalueof var="smallImagePath" param="element.smallImagePath"/>
																				<dsp:getvalueof var="thumbnailImagePath" param="element.thumbnailImagePath"/>
																				<dsp:getvalueof var="swatchImagePath" param="element.swatchImagePath"/>
																				<c:choose>
																				  <c:when test="${color eq elementValue}">
																					<c:set var="colorValue" value="${elementValue}" />
																					 <option value="${elementValue}" selected="selected" data-imgSrc="${scene7Path}/${swatchImagePath}" data-imgURLSmall="${scene7Path}/${smallImagePath}" data-imgURLThumb="${scene7Path}/${thumbnailImagePath}">${elementValue}</option>
																				  </c:when>
																				  <c:otherwise>
																					<option value="${elementValue}" data-imgSrc="${scene7Path}/${swatchImagePath}" data-imgURLSmall="${scene7Path}/${smallImagePath}" data-imgURLThumb="${scene7Path}/${thumbnailImagePath}">${elementValue}</option>
																				  </c:otherwise>
																				</c:choose>
																		</dsp:oparam>
																	</dsp:droplet>
																</select>
															</div>
															<input type="hidden" name="prodColor" class="_prodColor addItemToRegis addItemToList" value="${colorValue}" />
														</div>
													</c:if>
													</c:otherwise>
													</c:choose>
												</c:otherwise>
												</c:choose>
												</dsp:oparam>
										</dsp:droplet>
									</div>
							<c:choose>
								<c:when test="${appid eq 'BuyBuyBaby'}">
									<div class="fr grid_6 prodInfoContainer alpha omega marTop_10">
								</c:when>
								<c:otherwise>
									<div class="fr grid_6 prodInfoContainer alpha omega marTop_20">
								</c:otherwise>
							</c:choose>
								<input type="hidden" class="addItemToList" value="${fn:escapeXml(view)}" name="pageView" />
								<%-- LTL 498 start --%> 
								<div class="fr btnPD">
								
										<div class="prodQty fl">
											<div class="spinner fl <c:if test="${isLtlItem}">hidden</c:if>">
												<a href="#" class="scrollDown down" title="<bbbl:label key='lbl_decrease_quantity' language="${pageContext.request.locale.language}" />"><span class="txtOffScreen"><bbbl:label key="lbl_decrease_quantity" language="${pageContext.request.locale.language}" /></span></a>
												<input name="qty" id="pqty${productID}" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="fl pqty qty addItemToRegis _qty itemQuantity addItemToList" type="text" value="1"  data-change-store-submit="qty" data-change-store-errors="required digits nonZero" role="textbox" aria-required="true" aria-describedby="pqty${productID}"  />
												<a href="#" class="scrollUp up" title="<bbbl:label key='lbl_increase_quantity' language="${pageContext.request.locale.language}" />"><span class="txtOffScreen"><bbbl:label key="lbl_increase_quantity" language="${pageContext.request.locale.language}" /></span></a>
													<c:if test="${not empty prodType && prodType ne 'NONE' }">
													<c:choose>
														<c:when test="${prodType eq 'COLOR' || prodType eq 'FINISH'}">
															<c:if test="${not empty selectedSKU}">	
																<c:set var="skuid" value="${selectedSKU}"/>
															</c:if>
														</c:when>
														<c:otherwise>
															<c:set var="skuid" value=""/>
														</c:otherwise>
													</c:choose>
													</c:if>
												<input type="hidden" name="parentProductId"  value="${parentProductId}" />
												<input type="hidden" name="price" value="${omniPrice}" class="addItemToList addItemToRegis" />
												<input type="hidden" name="prodId" class="_prodId addItemToRegis productId addItemToList" value="${productID}" data-change-store-submit="prodId" data-change-store-errors="required" data-change-store-internalData="true" />
												<input type="hidden" name="skuId" value="${skuid}" class="addItemToRegis _skuId addItemToList changeStoreSkuId" data-change-store-submit="skuId" data-change-store-errors="required" data-change-store-internalData="true" />
												<input type="hidden" class="addToCartSubmitData" name="storeId" value="" data-change-store-storeid="storeId" />
												<input type="hidden" name="bts" class="addToCartSubmitData" value="${fn:escapeXml(btsValue)}" />
												<input type="hidden" value="${LiveClickerOn}" name="lcFlag"/>
												<input type="hidden" value="collectionList" name="fromPage"/>
												<input type="hidden" value="${CategoryId}" class="categoryId"/>
												<input type="hidden" value="${colorValue}" class="selectedRollUpValue"/>
						                        <input type="hidden" value="${isMultiRollUpFlag}" class="isMultiRollUpFlag"/>
											</div>
										</div>	
								
										<div class="fl noMar <c:if test="${isLtlItem}">hidden</c:if>">
											<dsp:include page="../addgiftregistry/add_item_gift_registry.jsp">
											<dsp:param name="isCustomizationRequired" value="${isCustomizationRequired}"/>
											<dsp:param name="ltlProductFlag" value="${isLtlProduct}"/>
											</dsp:include>
										</div>
										<!-- <div class="fl addToCart <c:if test="${isLtlItem}">hidden</c:if>">
											<div class="button button_active button_active_orange <c:if test="${isCustomizationRequired || not inStock || (isInternationalCustomer && isIntlRestricted) }">button_disabled</c:if>">
												<input type="submit" name="btnAddToCart" value="Add to Cart" onclick="rkg_micropixel('${appid}','cart')"  role="button" aria-pressed="false" <c:if test="${isCustomizationRequired || not inStock || (isInternationalCustomer && isIntlRestricted) }">disabled="disabled"</c:if> />
											</div>
										</div> -->

										<div id="buttonCartCollectionRedesign" class="fl addToCart <c:if test="${isLtlItem}">hidden</c:if>">
											<div class="mainCollectionProductContainer togglePDPButtons <c:if test="${isCustomizationRequired || not inStock || (isInternationalCustomer && isIntlRestricted) }">button_disabled</c:if>">
												<input class="button-Large btnSecondary" type="submit" name="btnAddToCart" value="Add to Cart" onclick="rkg_micropixel('${appid}','cart')"  role="button" aria-pressed="false" <c:if test="${isCustomizationRequired || not inStock || (isInternationalCustomer && isIntlRestricted) }">disabled="disabled"</c:if> />
											</div>
										</div>

										<input type="hidden" name="parentProdId" class="_prodId addItemToRegis addItemToList" value="${parentProductId} " />
										
										<%-- LTL related change start --%>
											<c:choose>
												<c:when test="${isLtlItem}">
													<div class="fr selectOption selectListView">
												</c:when>
												<c:otherwise>
													<div class="fr selectOption hidden">
												</c:otherwise>
											</c:choose>
														<div class="button button_active">
															<c:set var="chooseOptionBtn"><bbbl:label key='lbl_pdp_grid_choose_options' language="${pageContext.request.locale.language}" /></c:set>
															<input type="button" class="showOptionMultiSku" name="btnSelectOption" value="${chooseOptionBtn}" role="button" aria-pressed="false" onclick=""/>
														</div>
													</div>
										<%-- LTL related change end --%>
										
										<%-- R2.2.1 Story - 131 Quick View Page --%>
										<div class="btnPDLinks padTop_15 clearfix cb fr <c:if test="${isLtlItem}">hidden</c:if>">
											<div class="addToList fl <c:if test="${isCustomizationRequired}">disabled opacity_1</c:if>">
												<c:choose>
		                                          <c:when test="${isCustomizationRequired}">
                                                  		<div class="disableText fr cb" role="link" aria-disabled="true"><bbbl:label key='lbl_compare_save_for_later' language="${pageContext.request.locale.language}" /></div>
                                                  	</c:when>
                                                  	<c:otherwise>
                                                  		<a href="javascript:void(0);" onclick="rkg_micropixel('${appid}','wish')" role="link" aria-label="${lblSaveThe} ${prodName} ${lblFuturePurchase}"><bbbl:label key='lbl_compare_save_for_later' language="${pageContext.request.locale.language}" /></a>
                                                  	</c:otherwise>
                                                  </c:choose>											
										         <%-- <a href="javascript:void(0);"><bbbl:label key='lbl_compare_save_for_later' language="${pageContext.request.locale.language}" /></a> --%>
										     </div>
											
											<c:if test="${MapQuestOn}">
											
											<%--START:: change for defect no:BPS-439    --%>
		                                       										
												<div class="fl findInStore padLeft_20">
													<c:choose>
														<c:when test="${bopusAllowed}">
															<div class="disableText fr cb" role="link" aria-disabled="true">
														 		<bbbl:label key='lbl_compare_find_in_store' language="${pageContext.request.locale.language}" />
														 	</div>
														</c:when>
														 <c:when test="${isCustomizationRequired || isInternationalCustomer eq true}">
															<div class="disableText fr cb" role="link" aria-disabled="true">
														 		<bbbl:label key='lbl_compare_find_in_store' language="${pageContext.request.locale.language}" />
														 	</div>
														</c:when>
														<c:otherwise>
															<div class="changeStore fr cb">
											 					<a href="javascript:void(0);" role="link" onclick="pdpOmnitureProxy('${productID}', 'findinstore')" aria-label=" ${lblFindThe} ${prodName} ${lblStoreNear}"><bbbl:label key='lbl_compare_find_in_store' language="${pageContext.request.locale.language}" /></a>
																<span class="txtOffScreen">Find ${productName} in a store</span>
											 				</div>
														</c:otherwise>
													</c:choose>
												</div>
											</c:if>
										</div>
										<%-- R2.2.1 Story - 131 Quick View Page --%>
								</div>	

							<%-- LTL 498 end --%> 
							</div>
						</div>
					</fieldset>
				</li>
					</c:if>
						</dsp:oparam>
						</dsp:droplet>

					</ul>
</dsp:page>