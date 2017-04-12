<dsp:page>

<dsp:importbean bean="/com/bbb/profile/session/SessionBean"/> 
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet"/>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
<dsp:getvalueof var="appid" bean="Site.id" />
<dsp:getvalueof var="fromAjax" param="fromAjax" />
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/AddItemToGiftRegistryDroplet" />
<c:set var="AttributePDPCollection">
	<bbbl:label key='lbl_pdp_attributes_collection' language="${pageContext.request.locale.language}" />
</c:set>
<dsp:getvalueof var="skuIdfromURL" param="skuId" />
<dsp:getvalueof var="crossSellFlag" param="crossSellFlag"/>
<dsp:getvalueof var="desc" param="${fn:escapeXml(desc)}"/>
<c:set var="showShipCustomMsg" value="true"/>
<c:set var="count" value="1"/>
<dsp:getvalueof var="btsValue" param="${fn:escapeXml(bts)}"/>
<c:set var="productCount" value="0" />
<c:set var="collectionId_Omniture" scope="request"></c:set>
<c:set var="enableLTLRegForSite">
				<bbbc:config key="enableLTLRegForSite" configName="FlagDrivenFunctions" />
			</c:set>
<c:set var="scene7Path">
	<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
</c:set>
<c:set var="BazaarVoiceOn" scope="request" value="${param.BazaarVoiceOn}"/>
<c:set var="MapQuestOn" scope="request" value="${param.MapQuestOn}"/>
<c:set var="enableKatoriFlag" scope="request"><bbbc:config key="enableKatori" configName="EXIMKeys"/></c:set>
<c:set var="omniPersonalizeButtonClick">
	<bbbl:label key='omni_personalize_button_click' language="${pageContext.request.locale.language}" />
</c:set>
<c:set var="lblForThe"><bbbl:label key="lbl_accessibility_for_the" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblProductQuickView"><bbbl:label key="lbl_product_quick_view" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblReviewCount"><bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblReviewsCount"><bbbl:label key="lbl_reviews_count" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblOfThe"><bbbl:label key="lbl_accessibility_of_the" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblAboutThe"><bbbl:label key="lbl_accessibility_about_the" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblSaveThe"><bbbl:label key="lbl_accessibility_save_the" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblFuturePurchase"><bbbl:label key="lbl_accessibility_future_purchase" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblFindThe"><bbbl:label key="lbl_accessibility_find_the" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblStoreNear"><bbbl:label key="lbl_accessibility_store_near_you" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="ShipMsgDisplayFlag" scope="request"><bbbc:config key="ShipMsgDisplayFlag" configName="FlagDrivenFunctions"/></c:set>
<c:set var="shippingAttributesList">
		<bbbc:config key="shippingAttributesList" configName="ContentCatalogKeys" />
</c:set>
<input type="hidden" id="enableKatoriFlag" name="enableKatoriFlag" value="${enableKatoriFlag}">
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof param="parentProductId" var="parentProductId"/>
	<dsp:getvalueof var="color" param="color"/>
<dsp:getvalueof var="childProducts" param="collectionVO.childProducts"/>
		<c:if test="${childProducts ne null && not empty childProducts}">
				<dsp:form name="testcollectionForm" id="testcollectionForm" method="post">
					<ul class="clearfix collectionGridRow"> 
					
					<dsp:droplet name="AddItemToGiftRegistryDroplet">
						<dsp:param name="siteId" value="${appid}" />
						<c:set var="submitAddToRegistryBtn">
							<bbbl:label key='lbl_compare_add_to_registry' language="${pageContext.request.locale.language}" />
						</c:set>
					</dsp:droplet>
					<dsp:getvalueof bean="SessionBean.values" var="sessionMapValues"/>
					<dsp:getvalueof value="${sessionMapValues.registrySkinnyVOList}" var="registrySkinnyVOList"/>
					<dsp:getvalueof var="sizeValue" value="${fn:length(registrySkinnyVOList)}"/>
					
					<dsp:droplet name="/atg/dynamo/droplet/ForEach">
					<dsp:param param="collectionVO.childProducts" name="array" />

						<dsp:oparam name="output">
								<%-- Check if product is available for international customer --%>
								<dsp:getvalueof var="isIntlRestricted" param="element.intlRestricted"/>
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param param="element.rollupAttributes" name="array" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="prodType" param="key" />
									</dsp:oparam>
								</dsp:droplet>
							<dsp:getvalueof var="productID" param="element.productId"/>
							<dsp:getvalueof var="skuAttributes" value=""/>
							<dsp:getvalueof var="hasRebate" value=""/>
							<dsp:getvalueof var="eligibleRebates" value=""/>
							<dsp:getvalueof var="skuid" value=""/>
							<dsp:getvalueof var="bopusAllowed" value=""/>
							<dsp:getvalueof var="emailStockAlertsEnabled" value="" />
							<dsp:getvalueof var="skuinStock" value="" />
							<dsp:getvalueof var="childSKU" param="element.childSKUs"/>
							<c:set var="CertonaContext" scope="request">${CertonaContext}${productID};</c:set>
							<c:set var="collectionId_Omniture" scope="request">${collectionId_Omniture};${productID};;;;eVar29=${parentProductId},</c:set>
							<c:set var="isCustomizationRequired" value=""/>
							<c:set var="customizationOffered" value=""/>
							<c:choose>
							<c:when test="${(null != childSKU) && (fn:length(childSKU) == 1 )}">
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array" value="${childSKU}" />
								<dsp:oparam name="output">
										<dsp:getvalueof var="childSkuId" param="element"/>
										<dsp:droplet name="ProductDetailDroplet">
											<dsp:param name="id" value="${productID}" />
											<dsp:param name="siteId" param="siteId"/>
											<dsp:param name="skuId" value="${childSkuId}"/>
												<dsp:oparam name="output">
													<dsp:getvalueof var="selectedSKUTemp" param="selected"/>
													<c:if test="${not empty selectedSKUTemp}">
														<c:set var="selectedSKU" value="${selectedSKUTemp}"/>
														<dsp:getvalueof var="selectedSKUStock" param="inStock" />
													</c:if>
													<dsp:getvalueof var="skuVO" param="pSKUDetailVO"/>
													<dsp:getvalueof var="skuAttributes" param="pSKUDetailVO.skuAttributes"/>
													<dsp:getvalueof var="hasRebate" param="pSKUDetailVO.hasRebate"/>
													<dsp:getvalueof var="eligibleRebates" param="pSKUDetailVO.eligibleRebates"/>
													<dsp:getvalueof var="skuid" param="pSKUDetailVO.skuId"/>
													<dsp:getvalueof var="isLtlItem" param="pSKUDetailVO.ltlItem"/> <%-- LTL 498  --%>
													<dsp:getvalueof var="bopusAllowed" param="pSKUDetailVO.bopusAllowed"/>
													<dsp:getvalueof var="isCustomizationRequired" param="pSKUDetailVO.customizableRequired"/>
													<dsp:getvalueof var="customizationOffered" param="pSKUDetailVO.customizationOffered"/>
													<%-- <input type="hidden" name="skuId" value="${skuid}" class="_skuId addItemToRegis addItemToList" /> --%>
													<dsp:getvalueof var="emailStockAlertsEnabled" param="pSKUDetailVO.emailStockAlertsEnabled" />
													<dsp:getvalueof var="skuinStock" param="inStock" />		
													<dsp:getvalueof var="vendorName" param="productVO.vendorInfoVO.vendorName"/>											
													<%-- Check if product is available for international customer --%>
													<%-- <dsp:getvalueof var="isIntlRestricted" param="productVO.intlRestricted"/> --%>
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
							<c:set var="productCount" value="${productCount + 1}" scope="page"/>
							<c:choose>
							<c:when test="${productCount % 4 == 1}">
							<li class="grid_3 collectionItems clearfix alpha">
							</c:when>
							<c:when test="${productCount % 4 == 0}">
							<li class="grid_3 collectionItems clearfix omega">
							</c:when>
							<c:otherwise>
							<li class="grid_3 collectionItems clearfix">
							</c:otherwise>
							</c:choose>
						   <fieldset class="registryDataItemsWrap listDataItemsWrap <c:if test="${ (isCustomizationRequired || skuVO.customizationOffered) && not empty skuVO.customizableCodes}">personalizedItem</c:if>" data-refNum="" itemscope <c:if test="${empty fromAjax}">itemtype="http://schema.org/Product"</c:if> >
							<input type="hidden" name="isCustomizationRequired" value="${isCustomizationRequired}"/>
						    <dsp:getvalueof var="prodImage" param="element.productImages.thumbnailImage"/>
							<dsp:getvalueof param="element.name" var="prodName"/>
                            <c:choose>
                                <c:when test="${empty prodImage}">
                                    <img class="hidden productImage" height="83" width="83" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${prodName}" />
                                </c:when>
                                <c:otherwise>
                                    <img class="hidden productImage noImageFound" height="83" width="83" src="${scene7Path}/${prodImage}" alt="${prodName}" />
                                </c:otherwise>
                            </c:choose>

						   <%-- Image --%>

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
							<div class="grid_3 alpha marBottom_10">
								<c:choose>
									<c:when test="${not empty CategoryId}">
									<dsp:a iclass="prodImg" page="${finalUrl}?poc=${parentProductId}&categoryId=${CategoryId}" title="${prodName}" onclick="${onClickEvent}">
										<c:choose>
										<c:when test="${empty prodImage}">
											<img src="${imagePath}/_assets/global/images/no_image_available.jpg" width="229" height="229" alt="${prodName}" />
										</c:when>
										<c:otherwise>
											<img class="lazyLoad productImage noImageFound loadingGIF" src="${imagePath}/_assets/global/images/blank.gif" width="229" height="229" alt="${prodName}" data-lazyloadsrc="${scene7Path}/${prodImage}"/>
										</c:otherwise>
										</c:choose>
									</dsp:a>
								</c:when>
								<c:otherwise>
									<dsp:a iclass="prodImg" page="${finalUrl}?poc=${parentProductId}" title="${prodName}" onclick="${onClickEvent}">
											<c:choose>
											<c:when test="${empty prodImage}">
												<img  src="${imagePath}/_assets/global/images/no_image_available.jpg" width="229" height="229" alt="${prodName}" />
											</c:when>
											<c:otherwise>
												<img class="lazyLoad productImage noImageFound loadingGIF" src="${imagePath}/_assets/global/images/blank.gif" width="229" height="229" alt="${prodName}" data-lazyloadsrc="${scene7Path}/${prodImage}"/>
											</c:otherwise>
											</c:choose>
										</dsp:a>
								</c:otherwise>
							</c:choose>
							<span onclick="${onClickEvent}" tabindex="0" class="quickView showOptionMultiSku block marTop_10" role="link" aria-label="${lblProductQuickView} ${lblOfThe} ${prodName}"><bbbl:label key="lbl_product_quick_view" language="${pageContext.request.locale.language}" /></span>
							</div>
								<div class="grid_3 alpha">
								   <ul class="prodInfoGrid clearfix">
								   <li class="pdpCollectionProdAttribs">
								   <%-- Product name --%>
								    <div class="prodNameGrid prodName">
									   <dsp:a onclick="${onClickEvent}" page="${finalUrl}?poc=${parentProductId}">
										    <dsp:valueof param="element.name" valueishtml="true"/>
										</dsp:a>
									</div>

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
																<div class="prodReviewGrid prodReviews metaFeedback clearfix">																
																	<span class="ratingTxt ratingsReviews prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel"><dsp:valueof param="element.bvReviews.ratingsTitle"/></span></span><span class="reviewTxt">
                                                                                                                                  <a href="${contextPath}${finalUrl}?skuId=${catalogRefId}&amp;categoryId=${CategoryId}&amp;showRatings=true" role="link" aria-label="${totalReviewCount} ${lblReviewCount} ${lblForThe} ${prodName}"> ${ReviewCount}<bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" /></a></span></div>
															</c:when>
															<c:otherwise>
																<div class="prodReviewGrid prodReviews clearfix metaFeedback">
																<span class="ratingTxt ratingsReviews prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel"><dsp:valueof param="element.bvReviews.ratingsTitle"/></span></span><span class="reviewTxt">
                                                                                                                                <a href="${contextPath}${finalUrl}?skuId=${catalogRefId}&amp;categoryId=${CategoryId}&amp;showRatings=true" role="link" aria-label="${totalReviewCount} ${lblReviewsCount} ${lblForThe} ${prodName}"> ${ReviewCount}<bbbl:label key='lbl_reviews_count' language="${pageContext.request.locale.language}" /></a></span>
																</div>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<dsp:getvalueof var="fltValue" param="element.bvReviews.averageOverallRating"/>
														<dsp:getvalueof var="totalReviewCount" value="0"></dsp:getvalueof>
														<div class="prodReviewGrid prodReviews clearfix metaFeedback">
															<span class="ratingTxt ratingsReviews prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel"><dsp:valueof param="element.bvReviews.ratingsTitle"/></span></span><span class="reviewTxt">
                                                                                                                   <a href="${contextPath}${finalUrl}?skuId=${catalogRefId}&amp;categoryId=${CategoryId}&amp;showRatings=true" role="link" aria-label="${totalReviewCount} ${lblReviewCount} ${lblForThe} ${prodName}"> ${ReviewCount}<bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" /></a></span>
														</div>
													</c:otherwise>
												</c:choose>
											</c:if>
											<input type="hidden" class="isInStock" value="${inStock}"/>

									<%-- Rating End--%>
									<div class="priceQuantityNotAvailable <c:if test="${inStock==true}">hidden</c:if>">
								      <div class="gridMessage">
								             <input type="hidden" name="oosProdId" value="${oosProdId}" />
								             <c:choose>
												<c:when test="${inStock==true}">
												<input type="hidden" value="" name="oosSKUId" class="_oosSKUId"/>
												</c:when>
												<c:otherwise>
												<input type="hidden" value="${skuid}" name="oosSKUId" class="_oosSKUId"/>
												</c:otherwise>
											</c:choose>
								             <div class="error noMarBot"><bbbl:label key='lbl_pdp_product_notavailable_shipping' language="${pageContext.request.locale.language}" /></div>
								               <c:if test="${OutOfStockOn}">
                                               <c:choose>
								             	<c:when test="${((null eq emailStockAlertsEnabled) || (emailStockAlertsEnabled==true)) && (inStock==false)}">
								             <div class="info normalFontSize"><a class="info lnkNotifyOOS" href="#"><bbbl:label key='lbl_pdp_product_notify_item_available' language="${pageContext.request.locale.language}" /></a></div>
								             </c:when>
                                             <c:otherwise>
								             <div class="info normalFontSize hidden"><a class="info lnkNotifyOOS" href="#"><bbbl:label key='lbl_pdp_product_notify_item_available' language="${pageContext.request.locale.language}" /></a></div>
								             </c:otherwise>
                                             </c:choose>
                                             </c:if>
								      </div>
								</div>

						<%-- Attribute--%>
									   <div class="prodAttrGrid">
													<c:set var="rebatesOn" value="${false}" /> <c:if
														test="${not empty hasRebate && hasRebate}">
														<c:if
															test="${(null != eligibleRebates) && (fn:length(eligibleRebates) == 1 )}">
															<c:set var="rebatesOn" value="${true}" />
														</c:if>
													</c:if> <c:if test="${not empty skuAttributes}"> <dsp:droplet name="/atg/dynamo/droplet/ForEach">
														<dsp:param value="${skuAttributes}" name="array" />
														<dsp:oparam name="output">
															<dsp:getvalueof var="placeHolder" param="key" />
															<c:if
																test="${(placeHolder != null) && (placeHolder eq AttributePDPCollection)}">
																<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																	<dsp:param param="element" name="array" />
																	<dsp:param name="sortProperties" value="+priority" />
																	<dsp:oparam name="output">
																		<dsp:getvalueof var="attrId" param="element.attributeName" />
																		<c:if test="${fn:contains(shippingAttributesList,attrId)}">
																				<c:set var="showShipCustomMsg" value="false"/>
																		</c:if>
																		<dsp:getvalueof var="chkCount" param="count" />
																		<dsp:getvalueof var="chkSize" param="size" />
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
																		<dsp:getvalueof var="attributeDescripTop"
																			param="element.attributeDescrip" />
																		<dsp:getvalueof var="imageURLTop"
																			param="element.imageURL" />
																		<dsp:getvalueof var="actionURLTop"
																			param="element.actionURL" />
																		<c:choose>
																			<c:when test="${null ne attributeDescripTop}">
																				<c:choose>
																					<c:when test="${null ne imageURLTop}">
																						<span class="attribs noMar"><img
																							src="${imageURLTop}" alt="" /><dsp:valueof
																									param="element.attributeDescrip"
																									valueishtml="true" /></span>
																					</c:when>
																					<c:otherwise>
																						<c:choose>
																							<c:when test="${null ne actionURLTop}">
																								<span class="attribs noMar"><a
																									href="${actionURLTop}" class="newOrPopup"><span><dsp:valueof
																												param="element.attributeDescrip"
																												valueishtml="true" /></span></a></span>
																							</c:when>
																							<c:otherwise>
																								<span class="attribs noMar"><dsp:valueof
																											param="element.attributeDescrip"
																											valueishtml="true" /></span>
																							</c:otherwise>
																						</c:choose>
																					</c:otherwise>
																				</c:choose>
																			</c:when>
																			<c:otherwise>
																				<c:if test="${null ne imageURLTop}">
																					<c:choose>
																						<c:when test="${null ne actionURLTop}">
																							<span class="attribs"><a
																								href="${actionURLTop}" class="newOrPopup"><img
																									src="${imageURLTop}" alt="" /></a></span>
																						</c:when>
																						<c:otherwise>
																							<span class="attribs "><img
																								src="${imageURLTop}" alt="" /></span>
																						</c:otherwise>
																					</c:choose>
																				</c:if>
																			</c:otherwise>
																		</c:choose>
																	</dsp:oparam>
																</dsp:droplet>
															</c:if>
														</dsp:oparam>
													</dsp:droplet> </c:if> <c:if
														test="${not empty hasRebate && hasRebate}">
														<dsp:droplet name="/atg/dynamo/droplet/ForEach">
															<dsp:param name="array" value="${eligibleRebates}" />
															<dsp:oparam name="output">
																<dsp:getvalueof var="chkCount1" param="count" />
																<dsp:getvalueof var="chkSize1" param="size" />
																<c:set var="sep1" value="seperator" />
																<c:if test="${chkCount1 == chkSize1}">
																	<c:set var="sep1" value="" />
																</c:if>
																<dsp:getvalueof var="rebate" param="element" />
																<span class="attribs"><a
																	href="${rebate.rebateURL}" class="links"
																	target="_blank" title="Rebate"><c:out
																			value="${rebate.rebateDescription}" escapeXml="false" /></a></span>
															</dsp:oparam>
														</dsp:droplet>
													</c:if>
												</div>
										</li>
									   
									<%-- Price--%>	
									   <li class="prodpriceGrid clearfix">
									   <c:choose>
										   <c:when test="${prodType eq 'SIZE' || prodType eq 'FINISH' || prodType eq 'COLOR'}">
										   </c:when>
										   <c:otherwise>
											   <div class="spinner fl noMarTop padRight_10 <c:if test="${not empty skuid && not empty isLtlItem && isLtlItem}">hidden</c:if>">
													<a  id="accGridDecQty${skuid}" class="scrollDown down" href="#"><span class="txtOffScreen"><bbbl:label key="lbl_decrease_quantity" language="${pageContext.request.locale.language}" /></span></a>
													<input name="qty" id="pqty${productID}" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="fl pqty qty addItemToRegis _qty itemQuantity addItemToList escapeHTMLTag" type="text" value="1"  data-change-store-submit="qty" data-change-store-errors="required digits nonZero" role="textbox" aria-required="true" aria-describedby="pqty${productID}" maxlength="2" />
													<a id="accGridIncQty${skuid}" class="scrollUp up" href="#" title="<bbbl:label key='lbl_increase_quantity' language="${pageContext.request.locale.language}" />"><span class="txtOffScreen"><bbbl:label key="lbl_increase_quantity" language="${pageContext.request.locale.language}" /></span></a>
											  </div>
											</c:otherwise>
									  </c:choose>
										<div class="fl prodPrice noPadTop">
									   		<c:set var="inputFieldPrice"></c:set>
									   		<dsp:getvalueof var="isdynamicPriceProd" param="element.dynamicPricingProduct" />
												<dsp:getvalueof var="priceLabelCodeProd" param="element.priceLabelCode" />
												<dsp:getvalueof var="inCartFlagProd" param="element.inCartFlag" />
											<dsp:getvalueof var="priceRangeDescription" param="element.priceRangeDescription" />
											<dsp:getvalueof var="salePriceRangeDescription" param="element.salePriceRangeDescription" />
											<dsp:getvalueof var="priceLabelCodeSKU" value="${skuVO.pricingLabelCode }" />
											<dsp:getvalueof var="inCartFlagSKU" value="${skuVO.inCartFlag }" vartype="java.lang.Boolean"/>
										
										
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
												<dsp:include page="browse_price_frag.jsp">
													    <dsp:param name="priceLabelCode" value="${priceLabelCodeProd}" />
														<dsp:param name="inCartFlag" value="${inCartFlagProd}" />
														<dsp:param name="salePrice" value="${salePriceRangeDescription}" />
														<dsp:param name="listPrice" value="${priceRangeDescription}" />
														<dsp:param name="isdynamicPriceEligible" value="${isdynamicPriceProd}" />
														<dsp:param name="isFromPDP" value="false" />
														
													</dsp:include>   		
													
													<c:set var="inputFieldPrice">${salePriceRangeDescription}</c:set>
													<c:set var="inputFieldPrice">${priceRangeDescription}</c:set>
												</c:otherwise>
												
											</c:choose>
											</div>
											<c:if test="${ShipMsgDisplayFlag && showShipCustomMsg}">
												<div class="freeShipBadge"> 
													<span><dsp:valueof param="element.displayShipMsg" valueishtml="true"/></span>
												</div>
											</c:if>
											<input type="hidden" name="price" value="${omniPrice}" class="addItemToList addItemToRegis" />
											<input type="hidden" name="finalUrl" class="finalUrl" value="${finalUrl}" />
											<input type="hidden" name="parentProductId"  value="${parentProductId}" />
											<input type="hidden" name="prodId" class="_prodId addItemToRegis productId addItemToList" value="${productID}" data-change-store-submit="prodId" data-change-store-errors="required" data-change-store-internalData="true" />
											<input type="hidden" name="skuId" value="${skuid}" class="addItemToRegis _skuId addItemToList changeStoreSkuId" data-change-store-submit="skuId" data-change-store-errors="required" data-change-store-internalData="true" />
											<input type="hidden" class="addToCartSubmitData" name="storeId" value="" data-change-store-storeid="storeId" />
											<input type="hidden" name="bts" class="addToCartSubmitData" value="${btsValue}" />
											<input type="hidden" value="${LiveClickerOn}" name="lcFlag"/>
											<input type="hidden" value="${CategoryId}" class="categoryId"/>
      									<%--Katori integration on Product Compare page as a part of BPSI-2430 changes start --%>
      									<c:set var="customizableCodes" value="${skuVO.customizableCodes}" scope="request"/>
      									<c:set var="customizeCTACodes" scope="request">
											<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
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
												<c:when test="${ (isCustomizationRequired || customizationOffered) && not empty skuVO.customizableCodes && !isInternationalCustomer}">
													<div class="personalizationOffered clearfix cb fl marTop_5">
														<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="${contextPath}${finalUrl}?poc=${parentProductId}&openKatori=true&skuId=${skuid}"</c:otherwise></c:choose> class="collectionPersonalize ${customizeAttr} <c:if test="${not enableKatoriFlag}">disabled</c:if>" role="button" data-custom-vendor="${vendorName}" data-product="${productId}" data-sku="${pDefaultChildSku}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
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
																		<c:when test="${not empty skuVO.customizableCodes && fn:contains(customizeCTACodes, skuVO.customizableCodes)}">
																			<bbbl:label key='lbl_customization_required_msg' language="${pageContext.request.locale.language}" />
																		</c:when>
																		<c:otherwise>
																			<bbbl:label key='lbl_personalization_required_msg' language="${pageContext.request.locale.language}" />
																		</c:otherwise>
																	</c:choose>
		                                                       </div>
		                                                      </c:when>
															<c:otherwise>
																<span class='personalizationUnavailable marTop_10'><bbbl:label key='lbl_pdp_personalization_unavailable' language="${pageContext.request.locale.language}" /></span>
															</c:otherwise>
														</c:choose>	
														
													</div>
												</c:when>
												<c:otherwise>
												
													<c:choose>
														<c:when test="${isInternationalCustomer}">
															<c:choose>
																<c:when test="${(isCustomizationRequired || customizationOffered) && not empty skuVO.customizableCodes}">
																	<div class="personalizationOffered clearfix cb fl marTop_5">
																		<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="#"</c:otherwise></c:choose> class="personalize ${customizeAttr} disabled" role="button" data-sku="${pDefaultChildSku}" data-product="${productId}" data-custom-vendor="${vendorName}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
																			${personalizationTxt}																			
																		</a>
																		<span class="personalizationIntlShippingMsg"><bbbt:textArea key="txt_pdp_per_unavailable_intl" language="${pageContext.request.locale.language}" /></span>
																	</div>
																</c:when>
																<c:otherwise>
																	<div class="personalizationOffered clearfix cb fl marTop_5 hidden ">
																		<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="#"</c:otherwise></c:choose> class="personalize ${customizeAttr} disabled" role="button" data-sku="${pDefaultChildSku}" data-product="${productId}" data-custom-vendor="${vendorName}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
																			${personalizationTxt}
																		</a>
																		<span class="personalizationIntlShippingMsg"><bbbt:textArea key="txt_pdp_per_unavailable_intl" language="${pageContext.request.locale.language}" /></span>
																	</div>
																</c:otherwise>
															</c:choose>	
														</c:when>
														<c:otherwise>
															<div class="personalizationOffered clearfix cb fl marTop_5 hidden ">
																<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="${contextPath}${finalUrl}?poc=${parentProductId}&openKatori=true&skuId=${skuid}"</c:otherwise></c:choose> class="collectionPersonalize ${customizeAttr} <c:if test="${not enableKatoriFlag}">disabled</c:if>" role="button" data-custom-vendor="${vendorName}" data-sku="${pDefaultChildSku}" data-product="${productId}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
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
																				<c:when test="${not empty skuVO.customizableCodes && fn:contains(customizeCTACodes, skuVO.customizableCodes)}">
																					<bbbl:label key='lbl_customization_required_msg' language="${pageContext.request.locale.language}" />
																				</c:when>
																				<c:otherwise>
																					<bbbl:label key='lbl_personalization_required_msg' language="${pageContext.request.locale.language}" />
																				</c:otherwise>
																			</c:choose>
					                                                     </div>
																	</c:when>
																	<c:otherwise>
																		<span class='personalizationUnavailable marTop_10'><bbbl:label key='lbl_pdp_personalization_unavailable' language="${pageContext.request.locale.language}" /></span>
																	</c:otherwise>
																	</c:choose>
															</div>
														</c:otherwise>
													</c:choose>

												</c:otherwise>
											</c:choose>
										<%--Katori integration on Product Compare page as a part of BPSI-2430 changes end --%>  											
									   </li>
							           <%-- Price end--%>
									   <li class="prodbuttonGrid grid_3 alpha clearfix marLeft_5">
											 <c:choose>
												<c:when test="${prodType eq 'SIZE' || prodType eq 'FINISH' || prodType eq 'COLOR'}">
												</c:when>
												<c:otherwise>
												
												 </c:otherwise>
												 </c:choose>
												 
							                     <%-- Add to cart--%>					 
												 
												 
												 <dsp:droplet name="/atg/dynamo/droplet/ForEach">
												<dsp:param param="element.rollupAttributes" name="array" />
												<dsp:oparam name="output">
												<dsp:getvalueof var="prodType" param="key"/>
												</dsp:oparam>
												</dsp:droplet>
														<c:choose>
															<c:when test="${prodType eq 'SIZE' || prodType eq 'FINISH' || prodType eq 'COLOR'}">
															<div class="fl collectionCart">
																<div class="button button_active">
																	<c:set var="chooseOptionBtn"><bbbl:label key='lbl_pdp_grid_choose_options' language="${pageContext.request.locale.language}" /></c:set>
																	<input onclick="${onClickEvent}" type="button" class="showOptionMultiSku" name="showOptionMultiSku" value="${chooseOptionBtn}" role="button" aria-pressed="false">
																</div>
															</div>
															<input type="hidden" name="prodSize" class="_prodSize addItemToRegis addItemToList" value="" />
															<input type="hidden" name="prodFinish" class="_prodFinish addItemToRegis addItemToList" value="" />
															<input type="hidden" name="prodColor" class="_prodColor addItemToRegis addItemToList" value="${colorValue}" />
															</c:when>
															<c:otherwise>
																<%--LTL 498 start--%>
																<div class="fr collectionCart addToCart padLeft_5 <c:if test="${not empty skuid && not empty isLtlItem && isLtlItem}">hidden</c:if>">
																	<div class="button button_active button_active_orange <c:if test="${isCustomizationRequired || not inStock || (isInternationalCustomer && isIntlRestricted) }">button_disabled</c:if>">
																		<input type="submit" name="btnAddToCart"
																			value="Add to Cart"
																			onclick="rkg_micropixel('${appid}','cart')"
																			role="button"
																			<c:if test="${isCustomizationRequired || not inStock || (isInternationalCustomer && isIntlRestricted) }">disabled="disabled"</c:if>/>
																	</div>
																</div>
																
																<c:choose>
																<c:when test="${not empty skuid && not empty isLtlItem && isLtlItem}">
																	<div class="selectOption">
																</c:when>
																<c:otherwise>
																	<div class="selectOption hidden">
																</c:otherwise>
															</c:choose>
																		<div class="button button_active">
																			<c:set var="chooseOptionBtn"><bbbl:label key='lbl_pdp_grid_choose_options' language="${pageContext.request.locale.language}" /></c:set>
																			<input type="button" class="showOptionMultiSku" name="btnSelectOption" id="btnSelectOption${skuid}" value="${chooseOptionBtn}" role="button" aria-pressed="false" onclick=""/>
																		</div>
																	</div>
																<%--LTL 498 end--%>	
															</c:otherwise>
														</c:choose>
									   </li>
									   <li class="prodbuttonGrid grid_3 alpha clearfix <c:if test="${not empty skuid && not empty isLtlItem && isLtlItem}">hidden</c:if>">
									    <%--LTL 498 end--%>	
									    <div class="collectionGridOptions">
											<%-- Add to Registry--%>
                                             <c:choose>
												<c:when test="${prodType eq 'SIZE' || prodType eq 'FINISH' || prodType eq 'COLOR'}">
												</c:when>
												<c:otherwise>
													<dsp:include page="../addgiftregistry/add_item_giftregistry_gridview.jsp">
														<dsp:param name="isCustomizationRequired" value="${isCustomizationRequired}"/>
														<dsp:param name="isInternationalCustomer" value="${isInternationalCustomer}"/>
														<dsp:param name="ltlFlag" value="${isLtlItem}"/>
														<dsp:param name="wishListItemId" value="${wishListItemId}"/>
														<dsp:param name="skuID" value="${skuID}"/>
														<dsp:param name="prodID" value="${prodID}"/>
														<dsp:param name="quantity" value="${quantity}"/>
														<dsp:param name="itemPrice" value="${itemPrice}"/>
													</dsp:include>
												</c:otherwise>
											</c:choose>
											<%-- Add to Registry--%>
											
											<%-- R2.2.1 Story - 131 Quick View Page --%>
											<c:choose>
												<c:when test="${prodType eq 'SIZE' || prodType eq 'FINISH' || prodType eq 'COLOR'}">
												</c:when>
												<c:otherwise>
													<div class="btnPDLinks padTop_10 clearfix cb">
														<div class="addToList fl padRight_5 <c:if test="${isCustomizationRequired}">disabled opacity_1</c:if>" id="btnAddToList${skuid}" onclick="rkg_micropixel('BedBathUS','wish')">
															<c:choose>
					                                          	<c:when test="${isCustomizationRequired}">
			                                                  		<div class="disableText fr cb" role="link" aria-disabled="true"><bbbl:label key='lbl_compare_save_for_later' language="${pageContext.request.locale.language}" /></div>
			                                                  	</c:when>
			                                                  	<c:otherwise>
			                                                  		<a href="javascript:void(0);" role="link" aria-label="${lblSaveThe} ${prodName} ${lblFuturePurchase}"><bbbl:label key='lbl_compare_save_for_later' language="${pageContext.request.locale.language}" /></a>
			                                                  	</c:otherwise>
			                                                </c:choose>
														</div>
														<c:if test="${MapQuestOn}">
													
															<div class="fl findInStore padLeft_30 noPadTop">
																																
																<c:choose>
																	<c:when test="${bopusAllowed}">
																		<div class="disableText fr cb" role="link" aria-disabled="true">
														 					<bbbl:label key='lbl_compare_find_in_store' language="${pageContext.request.locale.language}" />
														 				</div>
																	</c:when>
																	<%-- change for defect no:BPS-439    --%>
																	 <c:when test="${isCustomizationRequired || isInternationalCustomer eq true}">
																		<div class="disableText fr cb" role="link" aria-disabled="true">
														 					<bbbl:label key='lbl_compare_find_in_store' language="${pageContext.request.locale.language}" />
														 				</div>
																	</c:when>
																	<c:otherwise>
																		<div class="changeStore fr cb">
																			<a href="javascript:void(0);" role="link" onclick="pdpOmnitureProxy('${productID}', 'findinstore');rkg_micropixel('${appid}','findstore');" aria-label="${lblFindThe} ${prodName} ${lblStoreNear}"><bbbl:label key='lbl_compare_find_in_store' language="${pageContext.request.locale.language}" /></a>
																			<span class="txtOffScreen">Find ${productName} in a store</span>
																		</div>
																	</c:otherwise>
																</c:choose>
															</div>
														  </c:if>
													</div>
												</c:otherwise>
											</c:choose>
											<%-- R2.2.1 Story - 131 Quick View Page --%>
                                     	</div>                                         									   
									   </li>
                                   </ul>									   
								</div>
                           </fieldset>
                           <c:if test="${isInternationalCustomer && isIntlRestricted}">
                           		<div class="notAvailableIntShipMsg padTop_5 cb clearfix"><bbbl:label key='lbl_pdp_intl_restricted_message' language="${pageContext.request.locale.language}" /></div>
                           </c:if>
                           <c:if test="${productCount % 4 == 0}">
                           </ul>
                           <ul class="clearfix collectionGridRow">
				       </li>
					</c:if>
						</dsp:oparam>
						</dsp:droplet>
						
					</ul>
				</dsp:form>
		</c:if>		
		</dsp:page>