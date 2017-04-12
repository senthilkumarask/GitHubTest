<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet"/>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:getvalueof var="appid" bean="Site.id" />	
<c:set var="collectionId_Omniture" scope="request"></c:set>
<c:set var="AttributePDPCollection">
	<bbbl:label key='lbl_pdp_attributes_collection' language="${pageContext.request.locale.language}" />
</c:set>
<dsp:getvalueof var="view" param="view"/>	
<dsp:getvalueof var="crossSellFlag" param="crossSellFlag"/>	 
<dsp:getvalueof var="desc" param="desc"/>	
<dsp:getvalueof var="color" param="color"/>	
<dsp:getvalueof param="parentProductId" var="parentProductId"/>	
<dsp:getvalueof var="childProducts" param="collectionVO.childProducts"/>
		<c:if test="${childProducts ne null && not empty childProducts}">				
			<%--<div id="shopAccessories" class="collectionItemsBox grid_12 clearfix collectionListView"> --%>
				<dsp:form name="accessoriesForm" method="post" id="accessoriesForm">
					
					<ul class="clearfix">
						
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param param="collectionVO.childProducts" name="array" />	
													
									<dsp:oparam name="output">
									<li class="bdrBot collectionItems clearfix">
										<fieldset class="registryDataItemsWrap listDataItemsWrap" itemscope itemtype="//schema.org/Product">
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
										<dsp:getvalueof var="skuid" value=""/>
										<dsp:getvalueof var="emailStockAlertsEnabled" value="" />	
										<dsp:getvalueof var="skuinStock" value="" />
										<dsp:getvalueof var="productID" param="element.productId"/>
										<dsp:getvalueof var="childSKU" param="element.childSKUs"/>
										<c:set var="collectionId_Omniture" scope="request">${collectionId_Omniture};${productID};;;;eVar29=${parentProductId},</c:set>
										<%-- <input type="hidden" name="prodId" class="_prodId addItemToRegis addItemToList" value="${productID}" /> --%>
										<c:if test="${(null != childSKU) && (fn:length(childSKU) == 1 )}"> 
										<dsp:droplet name="/atg/dynamo/droplet/ForEach">
										<dsp:param name="array" value="${childSKU}" />
											<dsp:oparam name="output">		
												<dsp:getvalueof var="childSkuId" param="element"/>	
													<dsp:droplet name="ProductDetailDroplet">
														<dsp:param name="id" value="${productID}" />
															<dsp:param name="siteId" param="siteId"/>
															<dsp:param name="skuId" value="${childSkuId}"/>
															<dsp:param name="isDefaultSku" value="true"/>
															<dsp:param name="checkInventory"  value="false"/>
																<dsp:oparam name="output">
																	<dsp:getvalueof var="hasRebate" param="pSKUDetailVO.hasRebate"/>
																	<dsp:getvalueof var="skuAttributes" param="pSKUDetailVO.skuAttributes"/>
																	<dsp:getvalueof var="eligibleRebates" param="pSKUDetailVO.eligibleRebates"/>
																	<dsp:getvalueof var="bopusAllowed" param="pSKUDetailVO.bopusAllowed"/>
																	<dsp:getvalueof var="skuid" param="pSKUDetailVO.skuId"/>
																	<%-- <input type="hidden" name="skuId" value="${skuid}" class="_skuId addItemToRegis addItemToList" /> --%>
																	<dsp:getvalueof var="emailStockAlertsEnabled" param="pSKUDetailVO.emailStockAlertsEnabled" />	
																	<dsp:getvalueof var="skuinStock" param="inStock" />
																</dsp:oparam>
													</dsp:droplet>
											</dsp:oparam>
										</dsp:droplet>
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
									
											<c:set var="onClickEvent">javascript:pdpCrossSellProxy('crossSell', '${desc}')</c:set>
										</c:when>
										<c:otherwise>
									
											<c:set var="onClickEvent" value=""/>
										</c:otherwise>
										</c:choose>
										<dsp:getvalueof var="CategoryId" param="categoryId"/>
										<div class="grid_2 alpha marRight_20">
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
										   </c:otherwise>
										</c:choose>
										</div>
										
										<dsp:getvalueof var="oosProdId" value="${productID}" />
										<c:choose>
										<c:when test="${empty skuinStock}">
											<c:set var="inStock" value="true" />
										</c:when>
										<c:otherwise>
											<dsp:getvalueof var="inStock" value="${skuinStock}" />
										</c:otherwise>
										</c:choose>
									<div class="grid_4 alpha">
								<input type="hidden" class="isInStock" value="${inStock}"/>
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
															<li class="prodReview clearfix prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="element.bvReviews.averageOverallRating"/>">
															<dsp:a page="${finalUrl}?skuId=${catalogRefId}&amp;categoryId=${CategoryId}&amp;showRatings=true">
															${ReviewCount} <bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" /></dsp:a>
															</li>
														</c:when>
														<c:otherwise>
															<li class="prodReview clearfix prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="element.bvReviews.averageOverallRating"/>">
															<dsp:a page="${finalUrl}?skuId=${catalogRefId}&amp;categoryId=${CategoryId}&amp;showRatings=true">
															${ReviewCount} <bbbl:label key='lbl_reviews_count' language="${pageContext.request.locale.language}" /></dsp:a>
															</li>
														</c:otherwise>
													</c:choose>	
												</c:when>
												<c:otherwise>
													<dsp:getvalueof var="fltValue" param="element.bvReviews.averageOverallRating"/>
													<dsp:getvalueof var="totalReviewCount" value="0"></dsp:getvalueof>
													<li class="prodReview clearfix prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="element.bvReviews.averageOverallRating"/>">
													<dsp:a page="${finalUrl}?skuId=${catalogRefId}&amp;categoryId=${CategoryId}&amp;showRatings=true">
													${ReviewCount} <bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" /></dsp:a>
													</li>
												</c:otherwise>
												</c:choose>
											</c:if>									
									</ul>
									<%-- keeping the code on the page as we have plans to display this in coming phases --%>
									<%--<div class="prodPrice fl" itemprop="price">
                                            <c:set var="inputFieldPrice"></c:set>
                                            <dsp:getvalueof var="priceRangeDescription" param="element.priceRangeDescription" />
											<dsp:getvalueof var="salePriceRangeDescription" param="element.salePriceRangeDescription" />
											<c:choose>
												<c:when test="${not empty skuid}">
													<dsp:include page="product_details_price.jsp">	
														<dsp:param name="product" value="${productID}"/>
														<dsp:param name="sku" value="${skuid}"/>
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
									</div> --%>
									</div>
									<%-- keeping this code on the page as we have planes to show the call the action buttons in the next phase --%>
									<div class="grid_6 omega">
										<div class="priceQuantityNotAvailable grid_6 alpha omega <c:if test="${inStock==true}">hidden</c:if> fl width_12">
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
															 <div class="error fl"><bbbl:label key='lbl_pdp_product_notavailable_shipping' language="${pageContext.request.locale.language}" /></div>
															 <c:if test="${OutOfStockOn}">
															 <c:choose>
															<c:when test="${(null eq emailStockAlertsEnabled) || (emailStockAlertsEnabled==true)}">
																<div class="info"><a class="info fl marLeft_10 lnkNotifyOOS" href="#"><bbbl:label key='lbl_pdp_product_notify_item_available' language="${pageContext.request.locale.language}" /> &raquo;</a></div>
															 </c:when>
															<c:otherwise>
																<div class="info hidden"><a class="info fl marLeft_10 lnkNotifyOOS" href="#"><bbbl:label key='lbl_pdp_product_notify_item_available' language="${pageContext.request.locale.language}" /> &raquo;</a></div>
															 </c:otherwise>
															</c:choose>
															 </c:if>
													  </div>
										</div>	
									
									<div class="fr grid_6 prodAttributeContainer alpha omega <c:if test="${inStock==false}">hidden</c:if>">
									<%--	<div class="rebateContainer collectionRebateContainer prodAttribWrapper">
												<c:set var="rebatesOn" value="${false}" />
												<c:if test="${not empty hasRebate && hasRebate}">
													<c:if test="${(null != eligibleRebates) && (fn:length(eligibleRebates) == 1 )}"> 
														<c:set var="rebatesOn" value="${true}" />
													</c:if>
												</c:if>
												<dsp:droplet name="/atg/dynamo/droplet/ForEach">
												<dsp:param value="${skuAttributes}" name="array" />
													<dsp:oparam name="output">
													<dsp:getvalueof var="placeHolder" param="key"/>
													<c:if test="${(placeHolder != null) && (placeHolder eq AttributePDPCollection)}">
													<dsp:droplet name="/atg/dynamo/droplet/ForEach">
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
																<c:choose>
																	     <c:when test="${null ne attributeDescripTop}">
																	            <c:choose>
																	                   <c:when test="${null ne imageURLTop}">
																	                        	 <span class="attribs ${sep}"><img src="${imageURLTop}" alt="" /><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></span>
																	                   </c:when>
																	                   <c:otherwise>
																	                         <c:choose>
																	                                 <c:when test="${null ne actionURLTop}">
																	                                       <span class="attribs ${sep}"><a href="${actionURLTop}" class="newOrPopup"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a></span>
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
																	                                	<span class="attribs ${sep}"><a href="${actionURLTop}" class="newOrPopup"><img src="${imageURLTop}" alt="" /></a></span>
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
														<span class="attribs ${sep1}" ><a href="${rebate.rebateURL}" class="links" target="_blank" title="Rebate"><c:out value="${rebate.rebateDescription}" escapeXml="false"/></a></span>
													</dsp:oparam>
												</dsp:droplet>
												</c:if>
										</div>	 --%>
									</div>	
										
								<div class="fr prodInfoContainer alpha omega listViewQtyWrapper">
										<dsp:droplet name="/atg/dynamo/droplet/ForEach">
											<dsp:param param="element.rollupAttributes" name="array" />
												<dsp:oparam name="output">
												<dsp:getvalueof var="prodType" param="key"/>
												<c:choose>
												
												<c:when test="${prodType eq 'SIZE'}">
													<div class="prodSize fl">
														<label for="selProdSize"><dsp:valueof param="key"/>:</label>
														<div class="psize fl">
															<select id="selProdSize" name="selProdSize" class="customSelectBoxCollection" aria-required="false" >
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
															<label for="selProdFinish"><dsp:valueof param="key"/>:</label>
															<div class="pfinish fl">
																<select id="selProdFinish" name="selProdFinish" class="uniform" aria-required="false" >
																<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																<dsp:param param="element" name="array" />
																	<dsp:oparam name="outputStart">
																		<option value=""><bbbl:label key='lbl_pdp_product_select' language="${pageContext.request.locale.language}" /></option>
																	</dsp:oparam>
																	<dsp:oparam name="output">
																		<dsp:getvalueof var="elementValue" param="element.rollupAttribute"/>
																		<dsp:getvalueof var="smallImagePath" param="element.smallImagePath"/>
																		<dsp:getvalueof var="thumbnailImagePath" param="element.thumbnailImagePath"/>
																		<dsp:getvalueof var="swatchImagePath" param="element.swatchImagePath"/>
																		<c:choose>
																		  <c:when test="${color eq elementValue}">
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
															<input type="hidden" name="prodFinish" class="_prodFinish addItemToRegis addItemToList" value="" />												
														</div>
													</c:when>
													<c:otherwise>
														<c:if test="${prodType eq 'COLOR'}">
														<div class="prodColor fl">
															<label for="selProdColor"><dsp:valueof param="key"/>:</label>
															<div class="pcolor fl">
																<select id="selProdColor" name="selProdColor" class="customSelectBoxCollection" aria-required="false">
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
															<input type="hidden" name="prodColor" class="_prodColor addItemToRegis addItemToList" value="" />											
														</div>
														</c:if>
													</c:otherwise>
													</c:choose>
												</c:otherwise>
												</c:choose>
												</dsp:oparam>
										</dsp:droplet>
										
										<%-- <div class="prodQty fl">
												<label for="pqty${productID}" style="display:block"><bbbl:label key='lbl_pdp_product_quantity' language="${pageContext.request.locale.language}" /></label>
												<div class="spinner">
														<a href="#" class="scrollDown down" title="<bbbl:label key='lbl_decrease_quantity' language="${pageContext.request.locale.language}" />"><span class="txtOffScreen"><bbbl:label key="lbl_decrease_quantity" language="${pageContext.request.locale.language}" /></span></a>
														<input name="qty" id="pqty${productID}" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="fl pqty qty addItemToRegis _qty itemQuantity addItemToList" type="text" value="1"  data-change-store-submit="qty" data-change-store-errors="required digits nonZero" role="textbox" aria-required="true" aria-describedby="pqty${productID}"  />
														<a href="#" class="scrollUp up" title="<bbbl:label key='lbl_increase_quantity' language="${pageContext.request.locale.language}" />"><span class="txtOffScreen"><bbbl:label key="lbl_increase_quantity" language="${pageContext.request.locale.language}" /></span></a>
													
														<input type="hidden" name="prodId" class="_prodId addItemToRegis productId addItemToList" value="${productID}" data-change-store-submit="prodId" data-change-store-errors="required" data-change-store-internalData="true" />
														<input type="hidden" name="skuId" value="${skuid}" class="addItemToRegis _skuId addItemToList changeStoreSkuId" data-change-store-submit="skuId" data-change-store-errors="required" data-change-store-internalData="true" />
														<input type="hidden" class="addToCartSubmitData" name="storeId" value="" data-change-store-storeid="storeId" />
														<input type="hidden" name="bts" class="addToCartSubmitData" value="${btsValue}" />
													
												</div>	
											</div>--%>
							</div>
							
							<c:choose>
								<c:when test="${appid eq 'BuyBuyBaby'}">
									<div class="fr grid_6 prodInfoContainer alpha omega marTop_10">
								</c:when>
								<c:otherwise>
									<div class="fr grid_6 prodInfoContainer alpha omega marTop_20">
								</c:otherwise>
							</c:choose>
									<input type="hidden" class="addItemToList" value="${view}" name="pageView" />	
									<%--<div class="fr btnPD">
										<div class="fl addToList">
											<div class="button">
												<input type="button" name="btnAddToList" id="btnAddToList" value="Save for Later" role="button" aria-pressed="false" onclick="rkg_micropixel('${appid}','wish')"/>
											</div>
										</div>
										<c:if test="${MapQuestOn}">
											<div class="findInStore noMar fl noPad <c:if test="${appid eq BedBathCanadaSite}">invisible</c:if>">
												
												<c:choose>
													<c:when test="${bopusAllowed}">
													<div class="button changeStoreItemWrap button_disabled">
													     <input type="button" name="btnFindInStore" value="Find in Store" disabled="disabled"  role="button" aria-pressed="false" <c:if test="${appid ne BedBathCanadaSite}">class="changeStore"</c:if> />
													</div>
													</c:when>
													<c:otherwise>
													<div class="button changeStoreItemWrap">
													     <input type="button" onclick="pdpOmnitureProxy('${parentProductId}', 'findinstore');"  role="button" aria-pressed="false" name="btnFindInStore" value="Find in Store" <c:if test="${appid ne BedBathCanadaSite}">class="changeStore"</c:if> />
													</div>
													</c:otherwise>
												</c:choose>
											</div>
											</c:if>
										<div class="fl noMar">
											<dsp:include page="../addgiftregistry/add_item_gift_registry.jsp">
											</dsp:include>
										</div>
										<div class="fl addToCart">
											<div class="button button_active button_active_orange  <c:if test="${not inStock}">button_disabled</c:if>">
												<input type="submit" name="btnAddToCart" value="Add to Cart" onclick="rkg_micropixel('${appid}','cart')"  role="button" aria-pressed="false"  <c:if test="${not inStock}">disabled="disabled"</c:if>/>
											</div>
										</div>
										<input type="hidden" name="parentProdId" class="_prodId addItemToRegis addItemToList" value="${parentProductId} " />
									</div> --%>	
							</div>		
										
																			
										<div class="fr priceQtyFloatFix">
											
											<span itemprop="offers" itemscope itemtype="//schema.org/Offer">
											
                                            <input type="hidden" name="price" value="${inputFieldPrice}" class="addItemToList addItemToRegis" />
											<span class="prodAvailabilityStatus hidden">
					                             <c:choose>
					                             <c:when test="${inStock==false}">
					                             	<span itemprop="availability" href="//schema.org/OutOfStock"><bbbl:label key='lbl_pdp_out_of_stock' language="${pageContext.request.locale.language}" /></span>
					                             </c:when>
					                             <c:otherwise>
					                             	<span itemprop="availability" href="//schema.org/InStock"><bbbl:label key='lbl_pdp_in_stock' language="${pageContext.request.locale.language}" /></span>
					                             </c:otherwise>
					                             </c:choose>
					                        </span>
											</span>
											
										</div>		
									</div>
										</fieldset>	
																			
									</li>
									</dsp:oparam>
																	
							</dsp:droplet>								
					</ul>
				</dsp:form>
			<%--</div> --%>
		</c:if>	
</dsp:page>