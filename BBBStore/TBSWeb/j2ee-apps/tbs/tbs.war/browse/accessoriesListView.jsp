<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet"/>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
<dsp:importbean bean="/com/bbb/commerce/droplet/TBSItemExclusionDroplet"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/repository/seo/CanonicalItemLink"/>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof var="appid" bean="Site.id" />
   	<c:choose>
	<c:when test="${appid eq 'TBS_BedBathUS'}">
		<c:set var="mapQuestOn" scope="request">
			<tpsw:switch tagName="MapQuestTag_tbs_us" />
		</c:set>
	</c:when>
	<c:when test="${appid eq 'TBS_BuyBuyBaby'}">
		<c:set var="mapQuestOn" scope="request">
			<tpsw:switch tagName="MapQuestTag_tbs_baby" />
		</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="mapQuestOn" scope="request">
			<tpsw:switch tagName="MapQuestTag_tbs_ca" />
		</c:set>
	</c:otherwise>
	</c:choose>
<c:set var="collectionId_Omniture" scope="request"></c:set>
<c:set var="AttributePDPCollection">
	<bbbl:label key='lbl_pdp_attributes_collection' language="${pageContext.request.locale.language}" />
</c:set>
<c:set var="omniPersonalizeButtonClick">
	<bbbl:label key='omni_personalize_button_click' language="${pageContext.request.locale.language}" />
</c:set>
<c:set var="customizeCTACodes" scope="request">
	<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
</c:set>
<c:set var="enableKatoriFlag" scope="request"><bbbc:config key="enableKatori" configName="EXIMKeys"/></c:set>
<dsp:getvalueof var="view" param="view"/>
<dsp:getvalueof var="crossSellFlag" param="crossSellFlag"/>
<dsp:getvalueof var="desc" param="desc"/>
<dsp:getvalueof var="color" param="color"/>
<input type="hidden" id="enableKatoriFlag" name="enableKatoriFlag" value="${enableKatoriFlag}">
<dsp:getvalueof param="parentProductId" var="parentProductId"/>
<dsp:getvalueof var="childProducts" param="collectionVO.childProducts"/>
		<c:if test="${childProducts ne null && not empty childProducts}">
			<!--<div id="shopAccessories" class="collectionItemsBox grid_12 clearfix collectionListView"> -->
				<%-- <dsp:form name="accessoriesForm" method="post" id="accessoriesForm"> --%>
						<ul id="accessoryItems" class="childProductItems small-block-grid-1 medium-block-grid-2 large-block-grid-3 collectionGridRow listGridToggle gridViewChildren">
							<dsp:droplet name="ForEach">
								<dsp:param param="collectionVO.childProducts" name="array" />
<%--                                  <dsp:getvalueof var="indexMain" param="index"/> --%>
									<dsp:oparam name="output">
									<li class="collectionItems">
										<fieldset class="registryDataItemsWrap listDataItemsWrap <c:if test="${ (skuVO.customizableRequired || skuVO.customizationOffered) && not empty skuVO.customizableCodes}">personalizedItem</c:if>" data-refNum="" itemscope itemtype="http://schema.org/Product">
                                        <%-- This Image tag is used for find in store modal dialog Image path should be dynamic--%>
                                        <dsp:getvalueof var="prodImage" param="element.productImages.thumbnailImage"/>
                                        <c:set var="prodName">
											<dsp:valueof param="element.name" valueishtml="true"/>
										</c:set>
                                        <c:choose>
                                            <c:when test="${empty prodImage}">
                                                <img class="hidden productImage" height="83" width="83" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${prodName}" />
                                            </c:when>
                                            <c:otherwise>
                                                <img class="hidden productImage noImageFound" height="83" width="83" src="${scene7Path}/${prodImage}" alt="${prodName}" />
                                            </c:otherwise>
                                        </c:choose>
                                        <dsp:getvalueof var="hasRebate" value=""/>
										<dsp:getvalueof var="skuAttributes" value=""/>
										<dsp:getvalueof var="eligibleRebates" value=""/>
										<dsp:getvalueof var="bopusAllowed" value=""/>
										<c:set var="isLtlItem" value="${false}"/>
										<dsp:getvalueof var="skuid" value=""/>
										<dsp:getvalueof var="emailStockAlertsEnabled" value="" />
										<dsp:getvalueof var="skuinStock" value="" />
										<dsp:getvalueof var="productID" param="element.productId"/>
										<dsp:getvalueof var="childSKU" param="element.childSKUs"/>
										<dsp:getvalueof var="colorMatched" param="element.colorMatched"/>
<%-- 										<c:set var="collectionId_Omniture" scope="request">${collectionId_Omniture};${productID};;;;eVar29=${parentProductId},</c:set> --%>
										<dsp:getvalueof var="collectionId_Omniture" param="collectionVO.omnitureCollectionEvar29" scope="request"/>
										<c:set var="isCustomizationRequired" value=""/>
										<c:set var="customizationOffered" value=""/>
										<c:choose>
											<c:when test="${childSKU != null && fn:length(childSKU) == 1}">
												<c:set var="checkInventory" value="${true}" />
											</c:when>
											<c:when test="${childSKU != null && fn:length(childSKU) > 1}">
												<c:set var="checkInventory" value="${false}" />
											</c:when>
										</c:choose>
										<%-- <input type="hidden" name="prodId" class="_prodId addItemToRegis addItemToList" value="${productID}" /> --%>
										<c:choose>
											<c:when test="${(null != childSKU) && (fn:length(childSKU) == 1 )}">
											<dsp:droplet name="ForEach">
											<dsp:param name="array" value="${childSKU}" />
												<dsp:oparam name="output">
													<dsp:getvalueof var="childSkuId" param="element"/>
														<dsp:droplet name="ProductDetailDroplet">
															<dsp:param name="id" value="${productID}" />
																<dsp:param name="siteId" param="siteId"/>
																<dsp:param name="skuId" value="${childSkuId}"/>
																<dsp:param name="isDefaultSku" value="true"/>
																<dsp:param name="startIndex" value="0" />
																	<dsp:oparam name="output">
																		<dsp:getvalueof var="hasRebate" param="pSKUDetailVO.hasRebate"/>
																		<dsp:getvalueof var="skuAttributes" param="pSKUDetailVO.skuAttributes"/>
																		<dsp:getvalueof var="eligibleRebates" param="pSKUDetailVO.eligibleRebates"/>
																		<dsp:getvalueof var="bopusAllowed" param="pSKUDetailVO.bopusAllowed"/>
																		<dsp:getvalueof var="skuid" param="pSKUDetailVO.skuId"/>
																		<dsp:getvalueof var="ltlItem" param="pSKUDetailVO.ltlItem"/> <!-- LTL 398  -->
																		<c:set var="isLtlItem" value="${ltlItem}"/>
																		<%-- <input type="hidden" name="skuId" value="${skuid}" class="_skuId addItemToRegis addItemToList" /> --%>
																		<dsp:getvalueof var="emailStockAlertsEnabled" param="pSKUDetailVO.emailStockAlertsEnabled" />
																		<dsp:getvalueof var="skuinStock" param="inStock" />
																		<dsp:getvalueof var="isCustomizationRequired" param="pSKUDetailVO.customizableRequired"/>
																		<dsp:getvalueof var="skuVO" param="pSKUDetailVO"/>
																	</dsp:oparam>
														</dsp:droplet>
												</dsp:oparam>
											</dsp:droplet>
											</c:when>
											<c:otherwise>
												<c:if test="${(not empty color && colorMatched)}">
													<dsp:droplet name="/atg/dynamo/droplet/ForEach">
														<dsp:param name="array" value="${childSKU}" />
														<dsp:oparam name="output">
															<dsp:getvalueof var="childSkuId" param="element"/>
															<dsp:droplet name="ProductDetailDroplet">
																<dsp:param name="id" value="${productID}" />
																<dsp:param name="siteId" param="siteId"/>
																<dsp:param name="skuId" value="${childSkuId}"/>
																<dsp:param name="color" value="${color}"/>
																<dsp:param name="checkInventory" value="${checkInventory}"/>
																<dsp:param name="startIndex" value="0" />
																<dsp:oparam name="output">
																	<dsp:getvalueof var="skuColor" param="pSKUDetailVO.color"/>
																	<c:set var="prodName_selectedSku" value=""/>
																	<c:set var="selectedColorFound" value="${false}"></c:set>
																	<c:if test="${not empty color and color eq skuColor}">
																		<dsp:getvalueof var="prodName_selectedSku" param="pSKUDetailVO.displayName"/>
																		<dsp:getvalueof var="hasRebate" param="pSKUDetailVO.hasRebate"/>
																		<dsp:getvalueof var="skuAttributes" param="pSKUDetailVO.skuAttributes"/>
																		<dsp:getvalueof var="eligibleRebates" param="pSKUDetailVO.eligibleRebates"/>
																		<dsp:getvalueof var="bopusAllowed" param="pSKUDetailVO.bopusAllowed"/>
																		<dsp:getvalueof var="skuid" param="pSKUDetailVO.skuId"/>
																		<dsp:getvalueof var="ltlItem" param="pSKUDetailVO.ltlItem"/> <%-- LTL 398  --%>
																		<c:set var="isLtlItem" value="${ltlItem}"/>
																		<%-- <input type="hidden" name="skuId" value="${skuid}" class="_skuId addItemToRegis addItemToList" /> --%>
																		<dsp:getvalueof var="emailStockAlertsEnabled" param="pSKUDetailVO.emailStockAlertsEnabled" />
																		<dsp:getvalueof var="skuinStock" param="inStock" />	
																		<dsp:getvalueof var="customizableCodes" param="pSKUDetailVO.customizableCodes" />
																		<dsp:getvalueof var="customizationOffered" param="pSKUDetailVO.customizationOffered" />
																		<dsp:getvalueof var="isCustomizationRequired" param="pSKUDetailVO.customizableRequired"/>
																		<dsp:getvalueof var="skuVO" param="pSKUDetailVO"/>																			
																		<dsp:getvalueof var="selectedColorFound" param="selectedColorFound"/>
																		<c:if test="${not empty selectedColorFound and selectedColorFound}">
																		<dsp:getvalueof var="selectedSKUStock" param="inStock" />
																	</c:if>
																	</c:if>
																	
																	<c:if test="${not empty prodName_selectedSku}">
																		<c:set var="prodName" value="${prodName_selectedSku}"/>
																	</c:if>
																</dsp:oparam>
															</dsp:droplet>
														 </dsp:oparam>
													</dsp:droplet>
												</c:if>
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
										<c:set var="customizableCodes" value="${skuVO.customizableCodes}" scope="request"/>
									<c:set var="customizeCTACodes" scope="request">
										<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys" />
									</c:set>
										<dsp:droplet name="CanonicalItemLink">
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

									<div class="small-6 columns no-padding prodImageContainer">
										<c:choose>
											<c:when test="${not empty CategoryId}">
												<dsp:a iclass="prodImg" page="${finalUrl}?poc=${parentProductId}&categoryId=${CategoryId}" title="${prodName}" onclick="${onClickEvent}">
													<c:choose>
													<c:when test="${empty prodImage}">
														<img  src="${imagePath}/_assets/global/images/no_image_available.jpg" width="146" height="146" alt="${prodName}" />
													</c:when>
													<c:otherwise>
														<img class="noImageFound" src="${scene7Path}/${prodImage}" width="146" height="146" alt="${prodName}" />
													</c:otherwise>
													</c:choose>
												</dsp:a>
												<%-- <span onclick="${onClickEvent}" tabindex="0" class="quickView showOptionMultiSku block marTop_5"><bbbl:label key="lbl_product_quick_view" language="${pageContext.request.locale.language}" /></span> --%>
											</c:when>
											<c:otherwise>
												<dsp:a iclass="prodImg" page="${finalUrl}?poc=${parentProductId}" title="${prodName}" onclick="${onClickEvent}">
													<c:choose>
													<c:when test="${empty prodImage}">
														<img  src="${imagePath}/_assets/global/images/no_image_available.jpg" width="146" height="146" alt="${prodName}" />
													</c:when>
													<c:otherwise>
														<img class="noImageFound" src="${scene7Path}/${prodImage}" width="146" height="146" alt="${prodName}" />
													</c:otherwise>
													</c:choose>
												</dsp:a>
												<%-- <span onclick="${onClickEvent}" tabindex="0" class="quickView showOptionMultiSku"><bbbl:label key="lbl_product_quick_view" language="${pageContext.request.locale.language}" /></span> --%>
										   </c:otherwise>
										</c:choose>
									</div>
                    				<div class="small-12 columns no-padding productDetailsWrapper">
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

								<div class="productDetailsContainer small-6 large-12 columns no-padding">
									<div class="columns small-12 prodInfoWrapper">
										<input type="hidden" class="isInStock" value="${inStock}"/>

										<ul class="prodInfo">
	                    					<%-- Product Name --%>
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
                    <%-- <li class="smallProdAttributeContainer">
                  <div class="fr prodAttributeContainer alpha omega <c:if test="${inStock==false}">hidden</c:if>">
                    <div class="rebateContainer collectionRebateContainer prodAttribWrapper">
                        <c:set var="rebatesOn" value="${false}" />
                        <c:if test="${not empty hasRebate && hasRebate}">
                          <c:if test="${(null != eligibleRebates) && (fn:length(eligibleRebates) == 1 )}">
                            <c:set var="rebatesOn" value="${true}" />
                          </c:if>
                        </c:if>
                        <dsp:droplet name="ForEach">
                        <dsp:param value="${skuAttributes}" name="array" />
                          <dsp:oparam name="output">
                          <dsp:getvalueof var="placeHolder" param="key"/>
                          <c:if test="${(placeHolder != null) && (placeHolder eq AttributePDPCollection)}">
                          <dsp:droplet name="ForEach">
                          <dsp:param param="element" name="array" />
                          <dsp:param name="sortProperties" value="+priority"/>
                            <dsp:oparam name="output">
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
                              <c:set var="actionURLTop">${actionURLTop}</c:set>
                                <c:choose>
                                       <c:when test="${null ne attributeDescripTop}">
                                              <c:choose>
                                                     <c:when test="${null ne imageURLTop}">
                                                             <span class="attribs ${sep}"><img src="${imageURLTop}" alt="" /><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></span>
                                                     </c:when>
                                                     <c:otherwise>
                                                           <c:choose>
                                                                   <c:when test="${null ne actionURLTop}">
                                                                         <span class="attribs ${sep}"><a href="${actionURLTop}" class="newOrPopup highlightRed"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a></span>
                                                                   </c:when>
                                                                   <c:otherwise>
                                                                         <span class="attribs ${sep}"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></span>
                                                                   </c:otherwise>
                                                            </c:choose>
                                                     </c:otherwise>
                                               </c:choose>
                                       </c:when>
                                       <c:otherwise>
                                             <c:if test="${null ne imageURLTop}">
                                                     <c:choose>
                                                            <c:when test="${null ne actionURLTop}">
                                                                    <span class="attribs ${sep}"><a href="${actionURLTop}" class="newOrPopup highlightRed"><img src="${imageURLTop}" alt="" /></a></span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                    <span class="attribs ${sep}"><img src="${imageURLTop}" alt="" /></span>
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

                        <c:if test="${not empty hasRebate && hasRebate}">
                        <dsp:droplet name="ForEach">
                        <dsp:param name="array" value="${eligibleRebates}"/>
                          <dsp:oparam name="output">
                            <dsp:getvalueof var="chkCount1" param="count"/>
                            <dsp:getvalueof var="chkSize1" param="size"/>
                            <c:set var="sep1" value="seperator" />
                            <c:if test="${chkCount1 == chkSize1}">
                              <c:set var="sep1" value="" />
                            </c:if>
                            <dsp:getvalueof var="rebate" param="element"/>
                            <span class="attribs ${sep1}" ><a href="${rebate.rebateURL}" class="links" target="_blank" title="Rebate"><c:out value="${rebate.rebateDescription}" escapeXml="false"/></a></span>
                          </dsp:oparam>
                        </dsp:droplet>
                        </c:if>
                    </div>
                  </div>
                </li> --%>

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
															<li class="prodReviews ratingsReviews clearfix noMar prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="element.bvReviews.averageOverallRating"/>">
															<dsp:a page="${finalUrl}?skuId=${catalogRefId}&amp;categoryId=${CategoryId}&amp;showRatings=true">
															${ReviewCount} <bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" /></dsp:a>
															</li>
														</c:when>
														<c:otherwise>
															<li class="prodReviews ratingsReviews clearfix noMar prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="element.bvReviews.averageOverallRating"/>">
															<dsp:a page="${finalUrl}?skuId=${catalogRefId}&amp;categoryId=${CategoryId}&amp;showRatings=true">
															${ReviewCount} <bbbl:label key='lbl_reviews_count' language="${pageContext.request.locale.language}" /></dsp:a>
															</li>
														</c:otherwise>
													</c:choose>
												</c:when>
												<c:otherwise>
													<dsp:getvalueof var="fltValue" param="element.bvReviews.averageOverallRating"/>
													<dsp:getvalueof var="totalReviewCount" value="0"></dsp:getvalueof>
													<li class="prodReviews ratingsReviews clearfix noMar prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="element.bvReviews.averageOverallRating"/>">
													<dsp:a page="${finalUrl}?skuId=${catalogRefId}&amp;categoryId=${CategoryId}&amp;showRatings=true">
													${ReviewCount} <bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" /></dsp:a>
													</li>
												</c:otherwise>
												</c:choose>
											</c:if>
									</ul>
									<div class="prodPrice fl" itemprop="price">
                                            <c:set var="inputFieldPrice"></c:set>
                                            <dsp:getvalueof var="priceRangeDescription" param="element.priceRangeDescription" />
											<dsp:getvalueof var="salePriceRangeDescription" param="element.salePriceRangeDescription" />
											<c:choose>
												<c:when test="${not empty skuid}">
													<dsp:include page="product_details_price.jsp">
														<dsp:param name="product" value="${productID}"/>
														<dsp:param name="sku" value="${skuid}"/>
														<dsp:param name="inCartFlag" value="${inCartFlag}"/>
													</dsp:include>
                                                    <c:set var="inputFieldPrice">${omniPrice}</c:set>
												</c:when>
												<c:otherwise>
												<c:choose>
													<c:when test="${not empty salePriceRangeDescription}">
														<dsp:getvalueof var="salePriceRange" param="element.salePriceRangeDescription"/>
														<c:choose>
															<c:when test="${fn:contains(salePriceRange,'-')}"><br /></c:when>
															<c:otherwise>&nbsp;</c:otherwise>
														</c:choose>
														<span class="prodPriceNEW">
															<dsp:valueof converter="currency"
																param="element.salePriceRangeDescription" />
														</span>
														<span class="prodPriceOLD">
															<span><bbbl:label key='lbl_old_price_text' language="${pageContext.request.locale.language}" /></span>
															<span class="was">was</span>
															<span class="oldPriceNum"> <dsp:valueof converter="currency"
																	param="element.priceRangeDescription" /></span>
														</span>
                                                        <dsp:getvalueof param="element.salePriceRangeDescription" id="saleprice"/>
                                                        <c:set var="inputFieldPrice">${saleprice}</c:set>
													</c:when>
													<c:otherwise>
														<dsp:valueof converter="currency"
															param="element.priceRangeDescription" />
                                                        <dsp:getvalueof param="element.priceRangeDescription" id="price"/>
                                                        <c:set var="inputFieldPrice">${price}</c:set>
													</c:otherwise>
												</c:choose>
												</c:otherwise>
											</c:choose>
									</div>
								

                  <div class="priceQuantityNotAvailable alpha omega <c:if test="${inStock==true}">hidden</c:if> fl width_12">
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
                            <c:when test="${((null eq emailStockAlertsEnabled) || (emailStockAlertsEnabled==true)) || (inStock==false)}">
                              <dsp:include page="notifyme.jsp">
								<dsp:param name="productId" value="${productID}"/>
								<dsp:param name="skuId" value="${skuid}" />
							</dsp:include>
                             </c:when>
                            <c:otherwise>
                              <div class="info normalFontSize textRight bold hidden notifyme"><a class="info fl marLeft_10 lnkNotifyOOS" href="#"><bbbl:label key='lbl_pdp_product_notify_item_available' language="${pageContext.request.locale.language}" /></a></div>
                             </c:otherwise>
                            </c:choose>
                             </c:if>
                          </div>
                  </div>
                <div class="fr prodAttributeContainer alpha omega columns<c:if test="${inStock==false}"> hidden</c:if>">
                  <div class="rebateContainer collectionRebateContainer prodAttribWrapper">
                      <c:set var="rebatesOn" value="${false}" />
                      <c:if test="${not empty hasRebate && hasRebate}">
                        <c:if test="${(null != eligibleRebates) && (fn:length(eligibleRebates) == 1 )}">
                          <c:set var="rebatesOn" value="${true}" />
                        </c:if>
                      </c:if>
                      <dsp:droplet name="ForEach">
                      <dsp:param value="${skuAttributes}" name="array" />
                        <dsp:oparam name="output">
                        <dsp:getvalueof var="placeHolder" param="key"/>
                        <c:if test="${(placeHolder != null) && (placeHolder eq AttributePDPCollection)}">
                        <dsp:droplet name="ForEach">
                        <dsp:param param="element" name="array" />
                        <dsp:param name="sortProperties" value="+priority"/>
                          <dsp:oparam name="output">
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
                            <c:set var="actionURLTop">${actionURLTop}</c:set>
                              <c:choose>
                                     <c:when test="${not empty attributeDescripTop}">
                                            <c:choose>
                                                   <c:when test="${not empty imageURLTop}">
                                                           <span class="attribs ${sep}"><img src="${imageURLTop}" alt="" /><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></span>
                                                   </c:when>
                                                   <c:otherwise>
                                                         <c:choose>
                                                                 <c:when test="${not empty actionURLTop}">
                                                                       <span class="attribs ${sep}"><a href="${actionURLTop}" class="newOrPopup highlightRed"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a></span>
                                                                 </c:when>
                                                                 <c:otherwise>
                                                                       <span class="attribs ${sep}"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></span>
                                                                 </c:otherwise>
                                                          </c:choose>
                                                   </c:otherwise>
                                             </c:choose>
                                     </c:when>
                                     <c:otherwise>
                                           <c:if test="${not empty imageURLTop}">
                                                   <c:choose>
                                                          <c:when test="${not empty actionURLTop}">
                                                                  <span class="attribs ${sep}"><a href="${actionURLTop}" class="newOrPopup highlightRed"><img src="${imageURLTop}" alt="" /></a></span>
                                                          </c:when>
                                                          <c:otherwise>
                                                                  <span class="attribs ${sep}"><img src="${imageURLTop}" alt="" /></span>
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

                      <c:if test="${not empty hasRebate && hasRebate}">
                      <dsp:droplet name="ForEach">
                      <dsp:param name="array" value="${eligibleRebates}"/>
                        <dsp:oparam name="output">
                          <dsp:getvalueof var="chkCount1" param="count"/>
                          <dsp:getvalueof var="chkSize1" param="size"/>
                          <c:set var="sep1" value="seperator" />
                          <c:if test="${chkCount1 == chkSize1}">
                            <c:set var="sep1" value="" />
                          </c:if>
                          <dsp:getvalueof var="rebate" param="element"/>
                          <span class="attribs ${sep1}" ><a href="${rebate.rebateURL}" class="links" target="_blank" title="Rebate"><c:out value="${rebate.rebateDescription}" escapeXml="false"/></a></span>
                        </dsp:oparam>
                      </dsp:droplet>
                      </c:if>
                  </div>
                </div>
                
                <dsp:droplet name="ForEach">
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

						<c:if test="${prodType eq 'SIZE'}">
							<div class="small-12 columns size-selector no-padding">
									<dsp:droplet name="ForEach">
									<dsp:param param="element" name="array" />
									<dsp:getvalueof var="index" param="index"/>
										<dsp:oparam name="outputStart">
											<a class="small secondary radius button dropdown" data-dropdown="selectProductSize${currentChildProdIndex}" href="#">SIZE<span></span></a>
											<ul class="f-dropdown" data-dropdown-content="" id="selectProductSize${currentChildProdIndex}" name="selectProductSize${currentChildProdIndex}">
										</dsp:oparam>
										<dsp:oparam name="output">
											<dsp:getvalueof var="elementValue" param="element.rollupAttribute"/>
											<li><a><c:out value="${elementValue}" escapeXml="false"/></a></li>
										</dsp:oparam>
										<dsp:oparam name="outputEnd">
											</ul>
											</a>
										</dsp:oparam>
									</dsp:droplet>
								<input type="hidden" name="prodSize" class="_prodSize addItemToRegis addItemToList" value="" />
                                           <br/><br/>
                            </div>
						</c:if>
						
						<c:if test="${prodType eq 'FINISH'}">
							<div class="small-12 columns prodFinish fl no-padding">
										<div class="pfinish">
												<dsp:droplet name="ForEach">
												<dsp:param param="element" name="array" />
													<dsp:oparam name="outputStart">
                                                                       <a class="small secondary radius button dropdown" data-dropdown="selProdFinish${currentChildProdIndex}" href="#"><bbbl:label key="lbl_finish_dropdown" language ="${pageContext.request.locale.language}"/><span></span></a>
                                                                       <ul class="f-dropdown" data-dropdown-content="" id="selProdFinish${currentChildProdIndex}" name="selProdFinish">
													</dsp:oparam>
													<dsp:oparam name="output">
														<dsp:getvalueof var="elementValue" param="element.rollupAttribute"/>
															<dsp:getvalueof var="smallImagePath" param="element.smallImagePath"/>
															<dsp:getvalueof var="thumbnailImagePath" param="element.thumbnailImagePath"/>

															<c:choose>
															<c:when test="${color eq elementValue}">
                                                                               <li class="selected" data-imgSrc="${scene7Path}/${smallImagePath}" data-imgURLSmall="${scene7Path}/${smallImagePath}" data-imgURLThumb="${scene7Path}/${thumbnailImagePath}"><a>${elementValue}</a></li>
															</c:when>
															<c:otherwise>
																<li data-imgSrc="${scene7Path}/${smallImagePath}" data-imgURLSmall="${scene7Path}/${smallImagePath}" data-imgURLThumb="${scene7Path}/${thumbnailImagePath}"><a>${elementValue}</a></li>
															</c:otherwise>
															</c:choose>
													</dsp:oparam>
                                                                   <dsp:oparam name="outputEnd">
                                                                       </ul>
                                                                   </dsp:oparam>
												</dsp:droplet>
										</div>
										<input type="hidden" name="prodFinish" class="_prodFinish addItemToRegis addItemToList" value="" />
                                               <br/><br/>
								</div>
							</c:if>
							
							<c:if test="${prodType eq 'COLOR'}">
								<div class="small-12 columns color-selector no-padding">
									<div class="prodColor fl">
										<div class="pcolor">
											<dsp:droplet name="ForEach">
											<dsp:param param="element" name="array" />
												<dsp:oparam name="outputStart">
													<a class="small secondary radius button dropdown" data-dropdown="selProdColor${currentChildProdIndex}" href="#"><bbbl:label key="lbl_colors_dropdown" language ="${pageContext.request.locale.language}"/><span></span></a>
													<ul class="f-dropdown" data-dropdown-content="" id="selProdColor${currentChildProdIndex}" name="selProdColor">
												</dsp:oparam>
												<dsp:oparam name="output">
													<dsp:getvalueof var="elementValue" param="element.rollupAttribute"/>
														<dsp:getvalueof var="smallImagePath" param="element.smallImagePath"/>
														<dsp:getvalueof var="thumbnailImagePath" param="element.thumbnailImagePath"/>
														<dsp:getvalueof var="swatchImagePath" param="element.swatchImagePath"/>

														<c:choose>
														<c:when test="${color eq elementValue}">
															<c:set var="colorValue" value="${color}" />
															<li class="selected" data-imgSrc="${scene7Path}/${swatchImagePath}" data-imgURLSmall="${scene7Path}/${smallImagePath}" data-imgURLThumb="${scene7Path}/${thumbnailImagePath}"><a data-skuSelected="${fn:toLowerCase(elementValue)}">${elementValue}</a></li>
														</c:when>
														<c:otherwise>
															<li data-imgSrc="${scene7Path}/${swatchImagePath}" data-imgURLSmall="${scene7Path}/${smallImagePath}" data-imgURLThumb="${scene7Path}/${thumbnailImagePath}"><a data-skuSelected="${fn:toLowerCase(elementValue)}">${elementValue}</a></li>
														</c:otherwise>
														</c:choose>
												</dsp:oparam>
											</dsp:droplet>
										<dsp:oparam name="outputEnd">
											</ul>
										</dsp:oparam>
									</div>
									<input type="hidden" name="prodColor" class="_prodColor addItemToRegis addItemToList" value="${colorValue}" />
									</div>
                                              <br/><br/>
								</div>
							</c:if>
							<c:set var="currentChildProdIndex" value="${currentChildProdIndex + 1}" />
						</dsp:oparam>
				</dsp:droplet>
                
                								<%-- LTL 498 start --%>

				<div class="personalizationContainer small-12 large-12 columns no-padding">
                  <c:choose>
                    <c:when test="${appid eq 'TBS_BuyBuyBaby'}">
                      <div class="fr prodInfoContainer alpha omega marTop_10">
                    </c:when>
                    <c:otherwise>
                      <div class="fr prodInfoContainer alpha omega marTop_20">
                    </c:otherwise>
                  </c:choose>

                    <%--Katori integration on Accessories list and grid view pdp page as a part of BBBH-841 changes start --%>
					<c:choose>
						<c:when test="${not empty skuVO.customizableCodes && fn:contains(customizeCTACodes, skuVO.customizableCodes)}">
							<c:set var="personalizationTxt">
								<bbbl:label key="lbl_customize_this" language="${pageContext.request.locale.language}"/>
							</c:set>
							<c:set var="personalizationReqTxt">
													<bbbl:label key='lbl_customization_required_below_tbs' language="${pageContext.request.locale.language}" />
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
                  <c:choose>
                    <c:when test="${(isCustomizationRequired || customizationOffered) && not empty customizableCodes}">
                      <div class="personalizationOffered small-12 columns no-padding  ">
                          <a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="${contextPath}${finalUrl}?color=${color}&openKatori=true&skuId=${skuid}"</c:otherwise></c:choose> class="personalise tiny button nonpdpPersonalize <c:if test="${not enableKatoriFlag}">disabled</c:if>" role="button" data-sku="${pDefaultChildSku}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
                            ${personalizationTxt}
                          </a>
                      <c:choose>
                        <c:when test="${enableKatoriFlag}">
                          <span class="personalizationDetail">
                            <bbbl:label key='lbl_cart_personalization_detail_tbs' language="${pageContext.request.locale.language}" />
                          </span>
                          <div id="openReturnPolicyModal" class="reveal-modal large" data-options="close_on_background_click:true;close_on_esc:true;" data-reveal=""></div>
                        </c:when>
                        <c:otherwise>
                          <span><bbbl:label key='lbl_pdp_personalization_unavailable' language="${pageContext.request.locale.language}" /></span>
                        </c:otherwise>
                      </c:choose>
                      <div class="personalizeToProceed <c:if test="${not isCustomizationRequired}"> hidden </c:if>" >
                                            ${personalizationReqTxt}
                                        </div>
                      </div>
                    </c:when>
                    <c:otherwise>
                         <div class="personalizationOffered small-12 columns no-padding hidden">
                          <a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="${contextPath}${finalUrl}?color=${color}&openKatori=true&skuId=${skuid}"</c:otherwise></c:choose> class="personalise tiny button nonpdpPersonalize <c:if test="${not enableKatoriFlag}">linkDisabled</c:if>" role="button" data-sku="${pDefaultChildSku}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
                          ${personalizationTxt}
                          </a>
                          <c:choose>
                          <c:when test="${enableKatoriFlag}">
                            <span class="personalizationDetail">
                              <bbbl:label key='lbl_cart_personalization_detail_tbs' language="${pageContext.request.locale.language}" />
                            </span>
                        <div id="openReturnPolicyModal" class="reveal-modal large" data-options="close_on_background_click:true;close_on_esc:true;" data-reveal=""></div>
                          </c:when>
                          <c:otherwise>
                            &nbsp;<bbbl:label key='lbl_pdp_personalization_unavailable' language="${pageContext.request.locale.language}" />
                          </c:otherwise>
                        </c:choose>
                        <div class="personalizeToProceed <c:if test="${not isCustomizationRequired}"> hidden </c:if>" >
                                            ${personalizationReqTxt}
                                         </div>
                      </div>
                    </c:otherwise>
                  </c:choose>

                    <%--Katori integration on Accessories list and grid view pdp page as a part of BBBH-841 changes end --%>

                  </div>
				</div>
				
				
             </div>
             
             <div class="fr prodInfoContainer alpha omega listViewQtyWrapper columns">
										
								<input type="hidden" class="addItemToList" value="${view}" name="pageView" />

									<div class="ctaContainer small-12 large-12 columns no-padding">
										<input type="hidden" class="addItemToList" value="${view}" name="pageView" />
											<div class="small-12 large-6 columns quantity no-padding-left <c:if test="${isLtlItem}">hidden</c:if>">
    										<div class="prodQty fl">
												<div class="qty-spinner clearfix">
													<a class="button minus secondary"><span></span></a>
													<!-- changing name="${skuid}" to name="qty" for RM# 30783 -->
													<%-- <input name="${skuid}" data-max-value="99" maxlength="2" id="pqty${productID}" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="fl pqty qty addItemToRegis _qty itemQuantity addItemToList quantity-input" type="text" value="1"  data-change-store-submit="qty" data-change-store-errors="required digits nonZero" role="textbox" aria-required="true" aria-describedby="pqty${productID}"  /> --%>
													<input name="qty" data-max-value="99" maxlength="2" id="pqty${productID}" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="fl pqty qty addItemToRegis _qty itemQuantity addItemToList quantity-input" type="text" value="1"  data-change-store-submit="qty" data-change-store-errors="required digits nonZero" role="textbox" aria-required="true" aria-describedby="pqty${productID}"  />
													<a class="button plus secondary"><span></span></a>
												</div>
												<%-- <a href="#" class="scrollDown down" title="<bbbl:label key='lbl_decrease_quantity' language="${pageContext.request.locale.language}" />"><span class="txtOffScreen"><bbbl:label key="lbl_decrease_quantity" language="${pageContext.request.locale.language}" /></span></a>
												<input name="${skuid}" id="pqty${productID}" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="fl pqty qty addItemToRegis _qty itemQuantity addItemToList" type="text" value="1"  data-change-store-submit="qty" data-change-store-errors="required digits nonZero" role="textbox" aria-required="true" aria-describedby="pqty${productID}"  />
												<a href="#" class="scrollUp up" title="<bbbl:label key='lbl_increase_quantity' language="${pageContext.request.locale.language}" />"><span class="txtOffScreen"><bbbl:label key="lbl_increase_quantity" language="${pageContext.request.locale.language}" /></span></a> --%>

												<input type="hidden" name="prodId" class="_prodId addItemToRegis productId addItemToList" value="${productID}" data-change-store-submit="prodId" data-change-store-errors="required" data-change-store-internalData="true" />
												<input type="hidden" name="skuId" value="${skuid}" class="addItemToRegis _skuId addItemToList changeStoreSkuId" data-change-store-submit="skuId" data-change-store-errors="required" data-change-store-internalData="true" />
												<input type="hidden" class="addToCartSubmitData" name="storeId" value="" data-change-store-storeid="storeId" />
												<input type="hidden" name="bts" class="addToCartSubmitData" value="${btsValue}" />
												<input type="hidden" name="parentProductId"  value="${parentProductId}" />
												<input type="hidden" value="${LiveClickerOn}" name="lcFlag"/>
												<input type="hidden" value="${CategoryId}" class="categoryId"/>
												<input type="hidden" value="${elementValue}" class="selectedRollUpValue"/>
						                        <input type="hidden" value="${isMultiRollUpFlag}" class="isMultiRollUpFlag"/>

						                        <%-- Added for Multiple items to cart :: START --%>
												<c:if test="${inStock}">
													<dsp:input bean="CartModifierFormHandler.productIds" type="hidden" value="${productID}" />
													<dsp:input bean="CartModifierFormHandler.catalogRefIds"  type="hidden" value="${skuid}"/>
												</c:if>
												<%-- Added for Multiple items to cart :: END --%>
												<%--<br class="hide-for-large-up">
                        <br class="hide-for-large-up">--%>
											</div>
											</div>
                                            <%-- PERSONALIZATION CONTAINER STARTS --%>
                                            <div class="personalizationContainer small-12 large-12 columns no-padding">
                          <c:choose>
                            <c:when test="${appid eq 'TBS_BuyBuyBaby'}">
                              <div class="fr prodInfoContainer alpha omega marTop_10">
                            </c:when>
                            <c:otherwise>
                              <div class="fr prodInfoContainer alpha omega marTop_20">
                            </c:otherwise>
                          </c:choose>

                            <%--Katori integration on Accessories list and grid view pdp page as a part of BBBH-841 changes start --%>

                          <c:choose>
                            <c:when test="${(isCustomizationRequired || customizationOffered) && not empty customizableCodes}">
                              <div class="personalizationOffered small-12 columns no-padding  ">
                                  <a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="${contextPath}${finalUrl}?color=${color}&openKatori=true&skuId=${skuid}"</c:otherwise></c:choose> class="personalise tiny button nonpdpPersonalize <c:if test="${not enableKatoriFlag}">disabled</c:if>" role="button" data-sku="${pDefaultChildSku}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
                                    ${personalizationTxt}
                                  </a>
                              <c:choose>
                                <c:when test="${enableKatoriFlag}">
                                  <span class="personalizationDetail">
                                    <bbbl:label key='lbl_cart_personalization_detail_tbs' language="${pageContext.request.locale.language}" />
                                  </span>
                                  <div id="openReturnPolicyModal" class="reveal-modal large" data-options="close_on_background_click:true;close_on_esc:true;" data-reveal=""></div>
                                </c:when>
                                <c:otherwise>
                                  <span><bbbl:label key='lbl_pdp_personalization_unavailable' language="${pageContext.request.locale.language}" /></span>
                                </c:otherwise>
                              </c:choose>
                              <div class="personalizeToProceed <c:if test="${not isCustomizationRequired}"> hidden </c:if>" >
                                                    <bbbl:label key='lbl_personalization_required_message_tbs' language="${pageContext.request.locale.language}" />
                                                </div>
                              </div>
                            </c:when>
                            <c:otherwise>
                                 <div class="personalizationOffered small-12 columns no-padding hidden">
                                  <a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="${contextPath}${finalUrl}?color=${color}&openKatori=true&skuId=${skuid}"</c:otherwise></c:choose> class="personalise tiny button nonpdpPersonalize <c:if test="${not enableKatoriFlag}">linkDisabled</c:if>" role="button" data-sku="${pDefaultChildSku}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
                                  ${personalizationTxt}
                                  </a>
                                  <c:choose>
                                  <c:when test="${enableKatoriFlag}">
                                    <span class="personalizationDetail">
                                      <bbbl:label key='lbl_cart_personalization_detail_tbs' language="${pageContext.request.locale.language}" />
                                    </span>
                                <div id="openReturnPolicyModal" class="reveal-modal large" data-options="close_on_background_click:true;close_on_esc:true;" data-reveal=""></div>
                                  </c:when>
                                  <c:otherwise>
                                    &nbsp;<bbbl:label key='lbl_pdp_personalization_unavailable' language="${pageContext.request.locale.language}" />
                                  </c:otherwise>
                                </c:choose>
                                <div class="personalizeToProceed <c:if test="${not isCustomizationRequired}"> hidden </c:if>" >
                                                    <bbbl:label key='lbl_personalization_required_message_tbs' language="${pageContext.request.locale.language}" />
                                                 </div>
                              </div>
                            </c:otherwise>
                          </c:choose>

                            <%--Katori integration on Accessories list and grid view pdp page as a part of BBBH-841 changes end --%>

                          </div>
        									</div>
                                            <%-- PERSONALIZATION CONTAINER ENDS --%>
										<div class="small-12 large-6 columns addToCart no-padding <c:if test="${isLtlItem}">hidden</c:if>">
										<dsp:droplet name="TBSItemExclusionDroplet">
											<dsp:param name="skuId" value="${skuid}" />
											<dsp:param name="siteId" value="${appid}" />
											<dsp:oparam name="output">
											<dsp:getvalueof var="isItemExcluded" param="validItemExcludedSku"/>
											<dsp:getvalueof var="caDisabled" param="caDisabled"/>
											<dsp:getvalueof var="reasonCode" param="reasonCode"/>
											<c:choose>
												<c:when test="${isItemExcluded || caDisabled}">
											    <div class="small-12 columns product-other-links availability" style="color:red;font-weight:bold;">
												<dsp:include page="/browse/frag/reasonCodeMessage.jsp" flush="true" >
													<dsp:param name="reasonCode" value="${reasonCode}"/>
												</dsp:include>
																						</div>
													<div class="button_disabled">
															<input type="submit" disabled="disabled" class=" tiny button expand transactional" name="btnAddToCart" value="Add to Cart" onclick="rkg_micropixel('${appid}','cart')"  role="button" aria-pressed="false"  <c:if test="${not inStock}">disabled="disabled"</c:if>/>
													</div>
												</c:when>
												<c:otherwise>
														<div class="<c:if test="${not inStock || isCustomizationRequired }">button_disabled</c:if>">
															<input type="submit" name="btnAddToCart" class="tiny button expand transactional" value="Add to Cart" onclick="rkg_micropixel('${appid}','cart')" role="button" aria-pressed="false" <c:if test="${not inStock || isCustomizationRequired}">disabled="disabled"</c:if> />
														</div>
													</c:otherwise>
											</c:choose>
											</dsp:oparam>
										</dsp:droplet>
										</div>
										<div class="fl small-12 large-6 addToList columns no-padding-left" id="btnAddToList">
											 <c:choose>
		                                          <c:when test="${isCustomizationRequired}">
                                                  		<div class="disabled fr cb"><bbbl:label key='lbl_compare_save_for_later' language="${pageContext.request.locale.language}" /></div>
                                                  	</c:when>
                                                  	<c:otherwise>
                                                  		<a href="javascript:void(0);" ><bbbl:label key='lbl_compare_save_for_later' language="${pageContext.request.locale.language}" /></a>
                                                  	</c:otherwise>
                                                  </c:choose>
                                        </div>
										<div id="accessories" class="fl small-12 large-6 columns no-padding-left no-padding-right <c:if test="${isLtlItem}">hidden</c:if>">
											<dsp:include page="../addgiftregistry/add_item_gift_registry.jsp">
											<dsp:param name="isCustomizationRequired" value="${isCustomizationRequired}"/>
											</dsp:include>
										</div>
										<input type="hidden" name="parentProdId" class="_prodId addItemToRegis addItemToList" value="${parentProductId} " />
										<%-- LTL related change start --%>
										<c:choose>
											<c:when test="${isLtlItem}">
												<div class="fl selectOption selectListView">
											</c:when>
											<c:otherwise>
												<div class="fl selectOption hidden">
											</c:otherwise>
										</c:choose>
												<%-- <div class="button_active">
													<c:set var="chooseOptionBtn"><bbbl:label key='lbl_pdp_grid_choose_options' language="${pageContext.request.locale.language}" /></c:set>
													<input type="button" class="button showOptionMultiSku" name="btnSelectOption" value="${chooseOptionBtn}" role="button" aria-pressed="false" onclick=""/>
												</div> --%>
												<div class="button_active small-12 large-6 columns no-padding">
															<a type="button" id="product_${productID}" class="showOptionMultiSku quick-view tiny button expand transactional" data-reveal-id="quickViewModal_${productID}" data-reveal-ajax="true"
						                                    	href="${contextPath}/browse/quickview_product_details.jsp?previousProductId=&pdpLink=${finalPDPUrl}&nextProductId=&productId=${productID}">
																CHOOSE OPTIONS
															</a>
														</div>
														<div id="quickViewModal_${productID}" class="qView reveal-modal large" data-options="close_on_background_click:true;close_on_esc:true;" data-reveal></div>
									<%-- LTL related change start --%>

										<%-- R2.2.1 Story - 131 Quick View Page --%>
										<div class="small-12 large-5 columns btnPDLinks cb fr no-padding <c:if test="${isLtlItem}">hidden</c:if>">
											<div class="addToList fl">
                                               <c:choose>
		                                          <c:when test="${isCustomizationRequired}">
                                                  		<div class="disabled fr cb"><bbbl:label key='lbl_compare_save_for_later' language="${pageContext.request.locale.language}" /></div>
                                                  	</c:when>
                                                  	<c:otherwise>
                                                  		<a href="javascript:void(0);" ><bbbl:label key='lbl_compare_save_for_later' language="${pageContext.request.locale.language}" /></a>
                                                  	</c:otherwise>
                                                  </c:choose>
										     </div>

											<div class="fl findInStore padLeft_20 " id="findInStoreLinkCollection">
													<c:choose>
		                                          <c:when test="${isCustomizationRequired}">
                                                  		<div class="disabled fr cb"><bbbl:label key='lbl_compare_find_in_store' language="${pageContext.request.locale.language}" /></div>
                                                  	</c:when>
                                                  <c:otherwise>
													<c:choose>
													<c:when test="${bopusAllowed}">
														    <dsp:a iclass="nearby-stores
														         in-store" href="/tbs/selfservice/find_tbs_store.jsp">
														         <dsp:param name="skuid" value="${skuid}" />
														         <dsp:param name="itemQuantity" value="1" />
														         <dsp:param name="id" value="${productID}" />
														         <dsp:param name="siteId" value="${appid}" />
														         <dsp:param name="skuId" value="${skuid}" />
														         <dsp:param name="registryId" param="registryId" />
														         <bbbl:label key='lbl_compare_find_in_store' language="${pageContext.request.locale.language}" />
														     </dsp:a>
														</c:when>
													<c:otherwise>
														    <dsp:a iclass="nearby-stores
														         in-store" href="/tbs/selfservice/find_tbs_store.jsp">
														          <dsp:param name="skuid" value="${skuid}" />
														         <dsp:param name="itemQuantity" value="1" />
														         <dsp:param name="id" value="${productID}" />
														         <dsp:param name="siteId" value="${appid}" />
														         <dsp:param name="skuId" value="${skuid}" />
														         <dsp:param name="registryId" param="registryId" />
														        <bbbl:label key='lbl_compare_find_in_store' language="${pageContext.request.locale.language}" />
														     </dsp:a>
													</c:otherwise>
												</c:choose>
                                              </c:otherwise>
                                             </c:choose>
											</div>
										</div>
										<%-- R2.2.1 Story - 131 Quick View Page --%>
									<%-- LTL 498 end --%>

										<div class="small-12 columns no-padding fr priceQtyFloatFix">

											<span itemprop="offers" itemscope itemtype="http://schema.org/Offer">

                                            <input type="hidden" name="price" value="${inputFieldPrice}" class="addItemToList addItemToRegis" />
											<span class="prodAvailabilityStatus hidden">
					                             <c:choose>
					                             <c:when test="${inStock==false}">
					                             	<span itemprop="availability" href="http://schema.org/OutOfStock"><bbbl:label key='lbl_pdp_out_of_stock' language="${pageContext.request.locale.language}" /></span>
					                             </c:when>
					                             <c:otherwise>
					                             	<span itemprop="availability" href="http://schema.org/InStock"><bbbl:label key='lbl_pdp_in_stock' language="${pageContext.request.locale.language}" /></span>
					                             </c:otherwise>
					                             </c:choose>
					                        </span>
											</span>

										</div>


								</div>
              </div>
										</fieldset>
									</li>
									</dsp:oparam>

							</dsp:droplet>
					</ul>
				<%-- </dsp:form> --%>
			<!--</div> -->
		</c:if>
		<div id="nearbyStore" class="reveal-modal" data-reveal>
		</div>
		<div id="accessoryCollectionScript">
		<script type="text/javascript">
			  $(document).ready(function(){
			   $(".nearby-stores").attr("data-reveal-id","nearbyStore");
			   $(".nearby-stores").attr("data-reveal-ajax","true");
			   $(document).foundation('reflow');
			  });
			</script>
			</div>
</dsp:page>
