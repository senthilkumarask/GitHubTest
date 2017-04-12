<dsp:page>
	<dsp:droplet name="ForEach">
			<dsp:param name="array" param="notInStockCategoryBuckets" />
			<dsp:param name="sortProperties" value="+_key" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="bucketName" param="key" idtype="java.lang.String" />
				<dsp:getvalueof var="bucket" param="notInStockCategoryBuckets.${bucketName}" idtype="java.util.List" />
				<dsp:getvalueof id="registryId" param="registryId"/>
				<c:if test="${bucketName ne othersCat}">
					<dsp:getvalueof var="count" value="${fn:length(bucket)}"/>
					<c:if test="${count ne 0}">
						<h3 class="ui-accordion-header ui-helper-reset ui-state-default ui-corner-all"><span class="ui-icon ui-icon-triangle-1-e"></span><a href="#"><dsp:valueof param="key" />(${count})</a></h3>	
					</c:if>
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="notInStockCategoryBuckets.${bucketName}" />
						<dsp:param name="elementName" value="regItem" />
                        <dsp:oparam name="outputStart">
                            <div class="accordionDiv ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom">
                                <ul class="clearfix productDetailList giftViewProduct">
                        </dsp:oparam>
						<dsp:oparam name="output">
							<c:if test="${count ne 0}">
							<dsp:getvalueof var="skuID" vartype="java.lang.String"  param="regItem.sku" />
							<dsp:droplet name="GiftRegistryFetchProductIdDroplet">
								<dsp:param name="skuId" value="${skuID}" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="productId" vartype="java.lang.String" param="productId" />
									<dsp:getvalueof var="inStoreSku" param="inStore" />
								</dsp:oparam>
							</dsp:droplet>
									<li class="clearfix productRow btngiftViewTopMargin giftViewProductChangeStoreWrapper addToCartGiftRegWrapper">
										<input type="hidden" name="bts" value="false" class="addToCartSubmitData bts" data-change-store-submit="bts" />
						                 <input type="hidden" name="storeId" value="" data-change-store-storeid="storeId" class="addToCartSubmitData" />
										<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
											<dsp:param name="id" value="${productId}"/>
											<dsp:param name="itemDescriptorName" value="product" />
											<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
											</dsp:oparam>
										</dsp:droplet>
										<dsp:getvalueof var="disable" param="regItem.sKUDetailVO.disableFlag" />
										<dsp:getvalueof var="webOffered" param="regItem.sKUDetailVO.webOfferedFlag" />
										<dsp:getvalueof var="active" param="regItem.sKUDetailVO.activeFlag" />
										<c:set var="productName">
											<dsp:valueof param="regItem.sKUDetailVO.displayName" valueishtml="true"/>
										</c:set>
										<div class="productImage">
										<c:choose>
											<c:when test="${active}">
												<a href="${contextPath}${finalUrl}" 
												class="lnkQVPopup" data-pdp-qv-url="pdp_quick_view.jsp?skuId=${skuID}&registryId=${registryId}&productId=${productId}&registryView=guest"
												title="${productName}">
													<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.thumbnailImage" />
													<c:choose>
														<c:when test="${empty imageURL}">
															<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="96" width="96" alt="${productName}" class="prodImage" />
														</c:when>
														<c:otherwise>
															<img src="${imagePath}/_assets/global/images/blank.gif" data-original="${scene7Path}/${imageURL}" height="96" width="96" alt="${productName}" class="prodImage noImageFound loadingGIF" />
														</c:otherwise>
													</c:choose>													
												</a>											
											</c:when>
											<c:otherwise>
												<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.thumbnailImage" />
													<c:choose>
														<c:when test="${empty imageURL}">
															<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="96" width="96" alt="${productName}" class="prodImage" />
														</c:when>
														<c:otherwise>
															<img src="${imagePath}/_assets/global/images/blank.gif" data-original="${scene7Path}/${imageURL}" alt="${productName}" height="96" width="96" class="prodImage noImageFound loadingGIF" />
														</c:otherwise>
													</c:choose>
											</c:otherwise>
										</c:choose>
										</div>
										<div class="productContainer">
											<ul class="productTab productHeading">
												<li class="productName">
													<dsp:getvalueof var="upc" param="regItem.sKUDetailVO.upc" />
													<dsp:getvalueof var="color" param="regItem.sKUDetailVO.color" />
													<dsp:getvalueof var="size" param="regItem.sKUDetailVO.size" />
                                                    <c:choose><c:when test="${active}"><c:if test="${upc ne null}"><strong><bbbl:label key='lbl_mng_regitem_upc' language="${pageContext.request.locale.language}" /></strong>&nbsp;${upc}</c:if><c:if test="${(not empty upc) && (not empty color)}"><span><br/></span></c:if><c:if test="${color ne null}"><strong><bbbl:label key='lbl_mng_regitem_color' language="${pageContext.request.locale.language}" /></strong>&nbsp;${color}</c:if></c:when><c:otherwise>&nbsp;</c:otherwise></c:choose>
												</li>
												<li class="price"><bbbl:label key='lbl_mng_regitem_price' language="${pageContext.request.locale.language}" /></li>
												<li class="requested"><bbbl:label key='lbl_mng_regitem_requested' language="${pageContext.request.locale.language}" /></li>
												<li class="purchase"><bbbl:label key='lbl_mng_regitem_purchased' language="${pageContext.request.locale.language}" /></li>
												<li class="quantity"><bbbl:label key='lbl_mng_regitem_quantity' language="${pageContext.request.locale.language}" /></li>
												<li class="productLastColumn"> </li>
											</ul>
											<ul class="productTab productContent clearfix">
												<li class="productName">
													<c:choose>
														<c:when test="${active}">
														<span class="blueName prodTitle"> 
															<a href="${contextPath}${finalUrl}" 
															class="lnkQVPopup prodTitle" data-pdp-qv-url="pdp_quick_view.jsp?skuId=${skuID}&registryId=${registryId}&productId=${productId}&registryView=guest"
															title="${productName}">
																<dsp:getvalueof var="displayName" param="regItem.sKUDetailVO.displayName" />
																${productName}
															</a>
														</span><br/>
														</c:when>
														<c:otherwise>	
															<span class="blueName prodTitle"> 												
																<dsp:getvalueof var="color" param="regItem.sKUDetailVO.color" />
																<dsp:getvalueof var="size" param="regItem.sKUDetailVO.size" />
																<dsp:getvalueof var="displayName" param="regItem.sKUDetailVO.displayName" />
																<dsp:valueof param="regItem.sKUDetailVO.displayName" valueishtml="true"/>
															</span><br/>
														</c:otherwise>
													</c:choose>
                                                    <span><c:if test='${not empty color}'><bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}"/>&nbsp;:&nbsp;<dsp:valueof value="${color}" valueishtml="true" /></c:if><c:if test='${not empty size}'><c:if test='${not empty color}'><br/></c:if><bbbl:label key="lbl_item_size" language ="${pageContext.request.locale.language}"/>&nbsp;:&nbsp;<dsp:valueof value="${size}" valueishtml="true" /></c:if></span>
												</li>
												<li class="price bold">
												<dsp:droplet name="PriceDroplet">
									                <dsp:param name="product" value="${productId}" />
									                <dsp:param name="sku" value="${skuID}"/>
									                <dsp:oparam name="output">
									                  	<dsp:setvalue param="theListPrice" paramvalue="price"/>
									                  	<dsp:getvalueof var="profileSalePriceList" bean="Profile.salePriceList"/>
										                  <c:choose>
										                  	<c:when test="${not empty profileSalePriceList}">
										                      	<dsp:droplet name="PriceDroplet">
											                        <dsp:param name="priceList" bean="Profile.salePriceList"/>
											                        <dsp:oparam name="output">
											                            <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
											                            <dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="price.listPrice"/>
											                            
												                            <dsp:include page="/global/gadgets/formattedPrice.jsp">
												                              	<dsp:param name="price" value="${listPrice }"/>
												                            </dsp:include>
												                        
												                        
												                        	<%-- (<bbbl:label key='lbl_mng_regitem_reg' language="${pageContext.request.locale.language}" />&nbsp; 	<dsp:include page="/global/gadgets/formattedPrice.jsp">
														          								<dsp:param name="price" value="${price }"/>
														              				</dsp:include>)
																       
																		<bbbl:label key='lbl_mng_regitem_you_save' language="${pageContext.request.locale.language}" />&nbsp;
															      				<dsp:include page="/global/gadgets/formattedPrice.jsp">
												                                	<dsp:param name="price" value="${price-listPrice}"/>
												                              	</dsp:include>(<fmt:formatNumber type="number" maxFractionDigits="2" value="${(listPrice/100)*price}" />%)
												                         --%>
													                </dsp:oparam>
											                        <dsp:oparam name="empty">
												                    	<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
												                    	
													                        <dsp:include page="/global/gadgets/formattedPrice.jsp">
													                          	<dsp:param name="price" value="${price }"/>
													                        </dsp:include>
													                  	  
											                        </dsp:oparam>
											                	</dsp:droplet><%-- End price droplet on sale price --%>
										                    </c:when>
										                    <c:otherwise>
										                      	<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
										                      	
											                      	<dsp:include page="/global/gadgets/formattedPrice.jsp">
											                        	<dsp:param name="price" value="${price }"/>
											                      	</dsp:include>
											                   
										                    </c:otherwise>
										             	</c:choose>
									                </dsp:oparam>
									            </dsp:droplet>
												</li>
												<li class="requested bold"><dsp:valueof param="regItem.qtyRequested" /></li>
												<li class="purchase bold"><dsp:valueof param="regItem.qtyPurchased" /></li>
												<li class="quantity bold">
												<c:choose>
													<c:when test="${((active || inStoreSku) || (currentSiteId ne TBS_BedBathCanadaSite))}">
                                                        <dsp:getvalueof var="qtyPurchased" param="regItem.qtyPurchased"/>
                                                        <dsp:getvalueof var="qtyRequested" param="regItem.qtyRequested"/>
                                                        <c:set var="cartQuantity" value="0"/>
			                                            <dsp:droplet name="/atg/commerce/order/droplet/BBBCartDisplayDroplet">
														<dsp:param name="order" bean="ShoppingCart.current" />
														<dsp:oparam name="output">										
															<dsp:droplet name="ForEach">
																<dsp:param name="array" param="commerceItemList" />
																<dsp:param name="elementName" value="commerceItem" />
																<dsp:oparam name="output">
																	<dsp:getvalueof var="commItem" param="commerceItem"/>
																	<dsp:getvalueof id="newQuantity" param="commerceItem.BBBCommerceItem.quantity"/>
																	<dsp:getvalueof id="commerceItemId" param="commerceItem.BBBCommerceItem.id"/>
																	<dsp:getvalueof id="regID" param="registryId"/>
																	<c:if test="${(not empty commItem.BBBCommerceItem.registryId) && (commItem.BBBCommerceItem.registryId eq regID)}">
																		<c:if test="${(not empty commItem.skuDetailVO.skuId) && (commItem.skuDetailVO.skuId eq skuID)}">
																			<c:set var="cartQuantity" value="${newQuantity}"/>
																		</c:if>
																	</c:if>
																</dsp:oparam>
															</dsp:droplet>
			                                            </dsp:oparam>
			                                            </dsp:droplet>
			                                             <c:set var="qtyRP">${qtyRequested-(qtyPurchased + cartQuantity)}</c:set>
													<div class="input alpha width_30">
														<div class="text">
															<input name="qty" type="text" value="1" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="input_tiny_giftView fl itemQuantity" data-change-store-submit="qty" data-requested="${qtyRP}" data-change-store-errors="required digits nonZero notGreater" data-change-store-errors-notGreater="${qtyRP}" role="textbox" aria-required="false" />
															<a href="#" class="scrollUp up" title="<bbbl:label key='lbl_increase_quantity' language="${pageContext.request.locale.language}" />"></a> 
															<a href="#" class="scrollDown down" title="<bbbl:label key='lbl_decrease_quantity' language="${pageContext.request.locale.language}" />"></a>
														</div>
													</div>	
													</c:when>
													<c:otherwise>1</c:otherwise>
												</c:choose>
												</li>
												
												<li class="productLastColumn"> 
												<dsp:getvalueof var="skuID" param="regItem.sku" />
												<input type="hidden" name="skuId" value="${skuID}" class="skuId changeStoreSkuId" data-change-store-submit="skuId" data-change-store-errors="required" /> 
												<input type="hidden" name="registryId" value="${registryId}" class="registryId" data-change-store-submit="registryId" data-change-store-errors="required"  /> 
													<dsp:droplet name="GiftRegistryFetchProductIdDroplet">
														<dsp:param name="skuId" value="${skuID}" />
														<dsp:oparam name="output">
															<dsp:getvalueof var="productId" param="productId" />
															<input type="hidden" name="prodId" value="${productId}" class="productId" data-change-store-submit="prodId" data-change-store-errors="required"  />
														</dsp:oparam>
														<dsp:oparam name="error">
															<dsp:getvalueof param="errorMsg" var="errorMsg" />
															<div class="error">
																<bbbe:error key="${errorMsg}" language="${pageContext.request.locale.language}" />
															</div>
														</dsp:oparam>
													</dsp:droplet>
													
												</li>
											</ul>
											
											<ul class="productFooter clearfix">
													<li class="fl">
													 <dsp:getvalueof var="skuDetailVO" param="regItem.sKUDetailVO"/>
									            
										            <c:if test="${active}">
										            	<ul class="noMar clearfix freeShipContainer prodAttribWrapper">
														<dsp:droplet name="/atg/dynamo/droplet/ForEach">
															<dsp:param name="array" param="regItem.sKUDetailVO.skuAttributes" />
															<dsp:param name="elementName" value="attributeVOList"/>
															<dsp:oparam name="output">
																<dsp:getvalueof var="pageName" param="key" />
			
																<c:if test="${pageName eq 'RLP'}">
																	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																	<dsp:param param="attributeVOList" name="array" />
																	<dsp:param name="elementName" value="attributeVO"/>
																	<dsp:param name="sortProperties" value="+priority"/>
																		<dsp:oparam name="output">
																			 <c:set var="currentCount"><dsp:valueof param="count" /></c:set>
																			<c:choose>
											                          			<c:when test="${currentCount%2 == 0}">
															            			<li><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></li>
															            			<c:set var="nextStyle">prodSecondaryAttribute</c:set>
															            		</c:when>
															            		<c:otherwise>
																					<li><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></li>
																					<c:set var="nextStyle">prodPrimaryAttribute</c:set>
																				</c:otherwise>
															            	</c:choose>	
																		</dsp:oparam>
																	</dsp:droplet>
																</c:if>							
															</dsp:oparam>
														</dsp:droplet>
														</ul>
														<%-- <c:if test="${skuDetailVO.shippingSurcharge gt 0.0}">
															<span class="${nextStyle}"><bbbl:label key="lbl_mng_surcharge" language="<c:out param='${language}'/>"/>${skuDetailVO.shippingSurcharge}</span>
														</c:if>
														<c:if test="${skuDetailVO.vdcSku}">
															<span class="prodSecondaryAttribute">${skuDetailVO.vdcSKUShipMesage}</span>
														</c:if>	 --%>
													</c:if>	
													</li>
													<li class="fr">
													
															<a class="pickupStore changeStore" title="<bbbl:label key='lbl_mng_regitem_pickupinstore' language='${pageContext.request.locale.language}' />" href="#"><bbbl:label key='lbl_mng_regitem_pickupinstore' language="${pageContext.request.locale.language}" /></a>
													
													</li>
											</ul>
										</div>	
										<div class="clear"></div>
									</li>
							</c:if>
						</dsp:oparam>
                        <dsp:oparam name="outputEnd">
                        				<li  class="productRow textRight returnTopRow">
												<a class="returnToTop" href="#backToTop"><bbbl:label key='lbl_mng_regitem_back_to_top' language="${pageContext.request.locale.language}" /></a>
												<div class="clear"></div>
										</li>
										<div class="clear"></div>
                                    </ul>
                                </div>
                        </dsp:oparam>
					</dsp:droplet>					
				</c:if>
			</dsp:oparam>
			<dsp:oparam name="outputEnd">
				<dsp:droplet name="ForEach">
					<dsp:param name="array" param="notInStockCategoryBuckets" />
					<dsp:param name="sortProperties" value="+_key" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="bucketName" param="key" idtype="java.lang.String" />
						<dsp:getvalueof var="bucket" param="notInStockCategoryBuckets.${bucketName}" idtype="java.util.List" />
						<c:if test="${bucketName eq othersCat}">
							<dsp:getvalueof var="count" value="${fn:length(bucket)}"/>
							<c:if test="${count ne 0}">
								<h3 class="ui-accordion-header ui-helper-reset ui-state-default ui-corner-all"><span class="ui-icon ui-icon-triangle-1-e"></span><a href="#"><dsp:valueof param="key" />(${count})</a></h3>	
							</c:if>
							<dsp:droplet name="ForEach">
								<dsp:param name="array" param="notInStockCategoryBuckets.${bucketName}" />
								<dsp:param name="elementName" value="regItem" />
                                <dsp:oparam name="outputStart">
                                    <div class="accordionDiv ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom">
                                        <ul class="clearfix productDetailList giftViewProduct">
                                </dsp:oparam>
								<dsp:oparam name="output">
									<c:if test="${count ne 0}">
										<dsp:getvalueof var="skuID" vartype="java.lang.String"  param="regItem.sku" />
										<dsp:droplet name="GiftRegistryFetchProductIdDroplet">
											<dsp:param name="skuId" value="${skuID}" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="productId" vartype="java.lang.String" param="productId" />
												<dsp:getvalueof var="inStoreSku" param="inStore" />
											</dsp:oparam>
										</dsp:droplet>
									<li class="clearfix productRow btngiftViewTopMargin giftViewProductChangeStoreWrapper addToCartGiftRegWrapper">
										<input type="hidden" name="bts" value="false" class="addToCartSubmitData bts" data-change-store-submit="bts" />
						                 <input type="hidden" name="storeId" value="" data-change-store-storeid="storeId" class="addToCartSubmitData" />
										<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
											<dsp:param name="id" value="${productId}"/>
											<dsp:param name="itemDescriptorName" value="product" />
											<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
											</dsp:oparam>
										</dsp:droplet>
										<dsp:getvalueof var="disable" param="regItem.sKUDetailVO.disableFlag" />
										<dsp:getvalueof var="webOffered" param="regItem.sKUDetailVO.webOfferedFlag" />
										<dsp:getvalueof var="active" param="regItem.sKUDetailVO.activeFlag" />
										<c:set var="productName">
											<dsp:valueof param="regItem.sKUDetailVO.displayName" valueishtml="true"/>
										</c:set>
										<div class="productImage">
										<c:choose>
											<c:when test="${active}">
												<a href="${contextPath}${finalUrl}" 
												class="lnkQVPopup" data-pdp-qv-url="pdp_quick_view.jsp?skuId=${skuID}&registryId=${registryId}&productId=${productId}&registryView=guest"
												title="${productName}">
													<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.thumbnailImage" />
													<c:choose>
														<c:when test="${empty imageURL}">
															<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="96" width="96" alt="${productName}" class="prodImage" />
														</c:when>
														<c:otherwise>
															<img src="${imagePath}/_assets/global/images/blank.gif" data-original="${scene7Path}/${imageURL}" height="96" width="96" alt="${productName}" class="prodImage noImageFound loadingGIF" />
														</c:otherwise>
													</c:choose>													
												</a>											
											</c:when>
											<c:otherwise>
												<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.thumbnailImage" />
													<c:choose>
														<c:when test="${empty imageURL}">
															<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="96" width="96" alt="${productName}" class="prodImage" />
														</c:when>
														<c:otherwise>
															<img src="${imagePath}/_assets/global/images/blank.gif" data-original="${scene7Path}/${imageURL}" alt="${productName}" height="96" width="96" class="prodImage noImageFound loadingGIF" />
														</c:otherwise>
													</c:choose>
											</c:otherwise>
										</c:choose>
										</div>
										<div class="productContainer">
											<ul class="productTab productHeading">
												<li class="productName">
													<dsp:getvalueof var="upc" param="regItem.sKUDetailVO.upc" />
													<dsp:getvalueof var="color" param="regItem.sKUDetailVO.color" />
													<dsp:getvalueof var="size" param="regItem.sKUDetailVO.size" />
                                                    <c:choose><c:when test="${active}"><c:if test="${upc ne null}"><strong><bbbl:label key='lbl_mng_regitem_upc' language="${pageContext.request.locale.language}" /></strong>&nbsp;${upc}</c:if></c:when><c:otherwise>&nbsp;</c:otherwise></c:choose>
												</li>
												<li class="price"><bbbl:label key='lbl_mng_regitem_price' language="${pageContext.request.locale.language}" /></li>
												<li class="requested"><bbbl:label key='lbl_mng_regitem_requested' language="${pageContext.request.locale.language}" /></li>
												<li class="purchase"><bbbl:label key='lbl_mng_regitem_purchased' language="${pageContext.request.locale.language}" /></li>
												<li class="quantity"><bbbl:label key='lbl_mng_regitem_quantity' language="${pageContext.request.locale.language}" /></li>
												<li class="productLastColumn"> </li>
											</ul>
											<ul class="productTab productContent clearfix">
												<li class="productName">
													<c:choose>
														<c:when test="${active}">
														<span class="blueName prodTitle"> 
															<a href="${contextPath}${finalUrl}" 
															class="lnkQVPopup prodTitle" data-pdp-qv-url="pdp_quick_view.jsp?skuId=${skuID}&registryId=${registryId}&productId=${productId}&registryView=guest"
															title="${productName}">
																<dsp:getvalueof var="displayName" param="regItem.sKUDetailVO.displayName" />
																${productName}
															</a>
														</span><br/>
														</c:when>
														<c:otherwise>	
															<span class="blueName prodTitle"> 												
																<dsp:getvalueof var="displayName" param="regItem.sKUDetailVO.displayName" />
																<dsp:valueof param="regItem.sKUDetailVO.displayName" valueishtml="true"/>
															</span><br/>
														</c:otherwise>
													</c:choose>
                                                    <span><c:if test='${not empty color}'><bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}"/>&nbsp;:&nbsp;<dsp:valueof value="${color}" valueishtml="true" /></c:if><c:if test='${not empty size}'><c:if test='${not empty color}'><br/></c:if><bbbl:label key="lbl_item_size" language ="${pageContext.request.locale.language}"/>&nbsp;:&nbsp;<dsp:valueof value="${size}" valueishtml="true" /></c:if></span>
												</li>
												<li class="price bold">
												<dsp:droplet name="PriceDroplet">
									                <dsp:param name="product" value="${productId}" />
									                <dsp:param name="sku" value="${skuID}"/>
									                <dsp:oparam name="output">
									                  	<dsp:setvalue param="theListPrice" paramvalue="price"/>
									                  	<dsp:getvalueof var="profileSalePriceList" bean="Profile.salePriceList"/>
										                  <c:choose>
										                  	<c:when test="${not empty profileSalePriceList}">
										                      	<dsp:droplet name="PriceDroplet">
											                        <dsp:param name="priceList" bean="Profile.salePriceList"/>
											                        <dsp:oparam name="output">
											                            <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
											                            <dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="price.listPrice"/>
											                            
												                            <dsp:include page="/global/gadgets/formattedPrice.jsp">
												                              	<dsp:param name="price" value="${listPrice }"/>
												                            </dsp:include>
												                        
												                        
												                        	<%-- (<bbbl:label key='lbl_mng_regitem_reg' language="${pageContext.request.locale.language}" />&nbsp; 	<dsp:include page="/global/gadgets/formattedPrice.jsp">
														          								<dsp:param name="price" value="${price }"/>
														              				</dsp:include>)
																       
																		<bbbl:label key='lbl_mng_regitem_you_save' language="${pageContext.request.locale.language}" />&nbsp;
															      				<dsp:include page="/global/gadgets/formattedPrice.jsp">
												                                	<dsp:param name="price" value="${price-listPrice}"/>
												                              	</dsp:include>(<fmt:formatNumber type="number" maxFractionDigits="2" value="${(listPrice/100)*price}" />%)
												                         --%>
													                </dsp:oparam>
											                        <dsp:oparam name="empty">
												                    	<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
												                    	
													                        <dsp:include page="/global/gadgets/formattedPrice.jsp">
													                          	<dsp:param name="price" value="${price }"/>
													                        </dsp:include>
													                  	  
											                        </dsp:oparam>
											                	</dsp:droplet><%-- End price droplet on sale price --%>
										                    </c:when>
										                    <c:otherwise>
										                      	<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
										                      	
											                      	<dsp:include page="/global/gadgets/formattedPrice.jsp">
											                        	<dsp:param name="price" value="${price }"/>
											                      	</dsp:include>
											                   
										                    </c:otherwise>
										             	</c:choose>
									                </dsp:oparam>
									            </dsp:droplet>
												</li>
												<li class="requested bold"><dsp:valueof param="regItem.qtyRequested" /></li>
												<li class="purchase bold"><dsp:valueof param="regItem.qtyPurchased" /></li>
												<li class="quantity bold ">
												<c:choose>
													<c:when test="${((active || inStoreSku) || (currentSiteId ne TBS_BedBathCanadaSite))}">
                                                        <dsp:getvalueof var="qtyPurchased" param="regItem.qtyPurchased"/>
                                                        <dsp:getvalueof var="qtyRequested" param="regItem.qtyRequested"/>
                                                        <c:set var="cartQuantity" value="0"/>
			                                            <dsp:droplet name="/atg/commerce/order/droplet/BBBCartDisplayDroplet">
														<dsp:param name="order" bean="ShoppingCart.current" />
														<dsp:oparam name="output">										
															<dsp:droplet name="ForEach">
																<dsp:param name="array" param="commerceItemList" />
																<dsp:param name="elementName" value="commerceItem" />
																<dsp:oparam name="output">
																	<dsp:getvalueof var="commItem" param="commerceItem"/>
																	<dsp:getvalueof id="newQuantity" param="commerceItem.BBBCommerceItem.quantity"/>
																	<dsp:getvalueof id="commerceItemId" param="commerceItem.BBBCommerceItem.id"/>
																	<dsp:getvalueof id="regID" param="registryId"/>
																	<c:if test="${(not empty commItem.BBBCommerceItem.registryId) && (commItem.BBBCommerceItem.registryId eq regID)}">
																		<c:if test="${(not empty commItem.skuDetailVO.skuId) && (commItem.skuDetailVO.skuId eq skuID)}">
																			<c:set var="cartQuantity" value="${newQuantity}"/>
																		</c:if>
																	</c:if>
																</dsp:oparam>
															</dsp:droplet>
			                                            </dsp:oparam>
			                                            </dsp:droplet>
			                                             <c:set var="qtyRP">${qtyRequested-(qtyPurchased + cartQuantity)}</c:set>
													<div class="input alpha width_30">
														<div class="text">
															<input name="qty" type="text" value="1" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="input_tiny_giftView fl itemQuantity" data-change-store-submit="qty" data-requested="${qtyRP}" data-change-store-errors="required digits nonZero notGreater" data-change-store-errors-notGreater="${qtyRP}" role="textbox" aria-required="false" />
															<a href="#" class="scrollUp up" title="<bbbl:label key='lbl_increase_quantity' language="${pageContext.request.locale.language}" />"></a> 
															<a href="#" class="scrollDown down" title="<bbbl:label key='lbl_decrease_quantity' language="${pageContext.request.locale.language}" />"></a>
														</div>
													</div>	
													</c:when>
													<c:otherwise>1</c:otherwise>
												</c:choose>
												</li>
												<li class="productLastColumn"> 
												<dsp:getvalueof var="skuID" param="regItem.sku" />
												<input type="hidden" name="skuId" value="${skuID}" class="skuId changeStoreSkuId" data-change-store-submit="skuId" data-change-store-errors="required" /> 
												<input type="hidden" name="registryId" value="${registryId}" class="registryId" data-change-store-submit="registryId" data-change-store-errors="required"  /> 
													<dsp:droplet name="GiftRegistryFetchProductIdDroplet">
														<dsp:param name="skuId" value="${skuID}" />
														<dsp:oparam name="output">
															<dsp:getvalueof var="productId" param="productId" />
															<input type="hidden" name="prodId" value="${productId}" class="productId" data-change-store-submit="prodId" data-change-store-errors="required"  />
														</dsp:oparam>
														<dsp:oparam name="error">
															<dsp:getvalueof param="errorMsg" var="errorMsg" />
															<div class="error">
																<bbbe:error key="${errorMsg}" language="${pageContext.request.locale.language}" />
															</div>
														</dsp:oparam>
													</dsp:droplet>									
													
												</li>
											</ul>
											
											<ul class="productFooter clearfix">
												<li class="fl">
													 <dsp:getvalueof var="skuDetailVO" param="regItem.sKUDetailVO"/>
									            
										            <c:if test="${active}">
										            	<ul class="noMar clearfix freeShipContainer prodAttribWrapper">
														<dsp:droplet name="/atg/dynamo/droplet/ForEach">
															<dsp:param name="array" param="regItem.sKUDetailVO.skuAttributes" />
															<dsp:param name="elementName" value="attributeVOList"/>
															<dsp:oparam name="output">
																<dsp:getvalueof var="pageName" param="key" />
			
																<c:if test="${pageName eq 'RLP'}">
																	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																	<dsp:param param="attributeVOList" name="array" />
																	<dsp:param name="elementName" value="attributeVO"/>
																	<dsp:param name="sortProperties" value="+priority"/>
																		<dsp:oparam name="output">
																			 <c:set var="currentCount"><dsp:valueof param="count" /></c:set>
																			<c:choose>
											                          			<c:when test="${currentCount%2 == 0}">
															            			<li><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></li>
															            			<c:set var="nextStyle">prodSecondaryAttribute</c:set>
															            		</c:when>
															            		<c:otherwise>
																					<li><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></li>
																					<c:set var="nextStyle">prodPrimaryAttribute</c:set>
																				</c:otherwise>
															            	</c:choose>	
																		</dsp:oparam>
																	</dsp:droplet>
																</c:if>							
															</dsp:oparam>
														</dsp:droplet>
														</ul>
														<%-- <c:if test="${skuDetailVO.shippingSurcharge gt 0.0}">
															<span class="${nextStyle}"><bbbl:label key="lbl_mng_surcharge" language="<c:out param='${language}'/>"/>${skuDetailVO.shippingSurcharge}</span>
														</c:if>
														<c:if test="${skuDetailVO.vdcSku}">
															<span class="prodSecondaryAttribute">${skuDetailVO.vdcSKUShipMesage}</span>
														</c:if>	 --%>
													</c:if>	
													</li>
												
												<li class="fr">
													<a class="pickupStore changeStore" title="<bbbl:label key='lbl_mng_regitem_pickupinstore' language='${pageContext.request.locale.language}' />" href="#"><bbbl:label key='lbl_mng_regitem_pickupinstore' language="${pageContext.request.locale.language}" /></a>
												</li>	
																						
											</ul>
										</div>	
										<div class="clear"></div>
									</li>
									</c:if>
								</dsp:oparam>
                                <dsp:oparam name="outputEnd">
                                			<li  class="productRow textRight returnTopRow">
													<a class="returnToTop" href="#backToTop"><bbbl:label key='lbl_mng_regitem_back_to_top' language="${pageContext.request.locale.language}" /></a>
													<div class="clear"></div>
											</li>
											<div class="clear"></div>
                                            </ul>
                                        </div>
                                </dsp:oparam>
							</dsp:droplet>
						</c:if>
					</dsp:oparam>
				</dsp:droplet>
			</dsp:oparam>
		</dsp:droplet>
</dsp:page>