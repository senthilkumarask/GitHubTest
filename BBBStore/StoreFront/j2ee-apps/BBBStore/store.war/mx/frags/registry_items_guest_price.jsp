<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFetchProductIdDroplet" />
	<dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
  	<dsp:importbean bean="/atg/userprofiling/Profile"/>	
  	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/IsProductSKUShippingDroplet"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	<c:set var="BuyBuyBabySite">
		<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="incartPriceList"><bbbc:config key="MexInCartPriceList" configName="ContentCatalogKeys" /></c:set>
	<c:set var="mxListPriceList"><bbbc:config key="MX_ListPriceList" configName="ContentCatalogKeys" /></c:set>
	<c:set var="mxSalePriceList"><bbbc:config key="MX_SalePriceList" configName="ContentCatalogKeys" /></c:set>
	<%-- Droplet Placeholder --%>
	<dsp:getvalueof var="registryId" param="registryId" />
	<dsp:getvalueof var="skuId" value="" />
	<dsp:getvalueof var="productId" param="productId" />
	<dsp:getvalueof var="sortSeq" param="sorting"/>
	<dsp:getvalueof var="view" param="view"/>
	<dsp:getvalueof var="eventType" param="eventType" />
	<c:set var="button_active" value="button_active"/>
	<c:set var="scene7Path">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
    <style>
		.br .viewRegistry ul.productDetailList li ul.productTab li.productName{width:355px;}
		.br .viewRegistry ul.productDetailList li ul.productTab li.price {width:115px;}
		.br .viewRegistry ul.productDetailList li ul.productTab li.requested {width:112px; }
		.br .viewRegistry ul.productDetailList li ul.productTab li.purchase {width:112px; }
		.br .viewRegistry ul.productDetailList li ul.productTab li.productLastColumn {width:30px;}
    </style>

	<c:if test="${eventType eq 'Baby' && currentSiteId ne BuyBuyBabySite}">
		<c:set var="button_active" value=""/>
	</c:if>
	<c:set var="BedBathCanadaSite"><bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" /></c:set>
	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="priceRangeList" />
		<dsp:oparam name="output">
				<dsp:getvalueof var="sizeIE" param="size"/>
				<dsp:getvalueof var="countIE" param="count"/>
				<c:choose>
					<c:when test="${countIE eq 1}">
						<div class="accordionReg1 accordionReg accordion ui-accordion ui-widget ui-helper-reset ui-accordion-icons" id="accordionReg1">
					</c:when>
					<c:when test="${countIE eq 5}">
						</div>
						
						<div class="accordionReg2 accordionReg  accordion ui-accordion ui-widget ui-helper-reset ui-accordion-icons" id="accordionReg2">
					</c:when>
					<c:when test="${countIE eq 12}">
						</div>
						
						<div class="accordionReg3 accordionReg  accordion ui-accordion ui-widget ui-helper-reset ui-accordion-icons" id="accordionReg3">
					</c:when>
				</c:choose>
			<dsp:getvalueof var="priceRange" param="element"/>
			<dsp:droplet name="ForEach">
				<dsp:param name="array" param="inStockCategoryBuckets" />
				<%--<dsp:param name="sortProperties" value="+_key" /> --%>
				<dsp:oparam name="output">
				
				<dsp:getvalueof var="bucketName" param="key" idtype="java.lang.String" />
					<dsp:getvalueof var="bucket" param="inStockCategoryBuckets.${bucketName}" idtype="java.util.List" />
					<c:if test="${priceRange eq bucketName}">
					
					<dsp:getvalueof var="count" value="${fn:length(bucket)}"/>
						<c:if test="${count ne 0}">
							<h2 class="ui-accordion-header ui-helper-reset ui-state-default ui-corner-all"><span class="ui-icon ui-icon-triangle-1-e"></span><a href="#"><dsp:valueof param="key" />(${count})</a></h2>
						</c:if>
					<%-- ForEach for List<RegistryItemVO> listRegistryItemVO --%>
						

						<dsp:droplet name="ForEach">
							<dsp:param name="array" param="inStockCategoryBuckets.${bucketName}" />
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
                                    <dsp:getvalueof var="bucketName" param="key" idtype="java.lang.String" />
                                    <dsp:getvalueof var="bucket" param="inStockCategoryBuckets.${bucketName}" idtype="java.util.List" />
                                    <dsp:getvalueof var="inCartFlagSKU" param="regItem.sKUDetailVO.inCartFlag"/>
                                <%-- <dsp:input bean="CartModifierFormHandler.addItemCount" type="hidden" iclass="addItemCount" value="1" /> --%>
                                        
                                <li class="clearfix productRow btngiftViewTopMargin1 giftViewProductChangeStoreWrapper addToCartGiftRegWrapper "> 							
                                    
                                    <%-- <input type="hidden" name="skuId" value="sku60011" class="skuId" />
                                          <input type="hidden" name="registryId" value="${registryId}" class="registryId" />
                                          <input type="hidden" name="demoData" value="abcde" class="addToCartSubmitData" />
                                          <input type="hidden" name="prodId" value="prod60011" /> --%>	
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
                                        <dsp:getvalueof var="jdaProdDesc" param="regItem.productDescrip"  />
                                        <c:choose>
                                            <c:when test="${jdaProdDesc != null && not empty jdaProdDesc}">${jdaProdDesc}</c:when>
                                            <c:otherwise><dsp:valueof param="regItem.sKUDetailVO.displayName" valueishtml="true"/></c:otherwise>
                                        </c:choose>
                                    </c:set>
                                    <c:choose>
                                        <c:when test="${active}">
                                        <div class="productImage grid_2 alpha">
                                            <dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.mediumImage" />
                                            <c:choose>
                                                <c:when test="${empty imageURL}">
                                                    <img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="116" width="116" alt="${productName}" class="prodImage" />
                                                </c:when>
                                                <c:otherwise>
                                                    <img src="${imagePath}/_assets/global/images/blank.gif" data-original="${scene7Path}/${imageURL}" alt="${productName}" height="116" width="116" class="prodImage noImageFound loadingGIF" />
                                                </c:otherwise>
                                            </c:choose>
                                            <%-- <span class="productTitle grid_3">
                                                <dsp:getvalueof var="color" param="regItem.sKUDetailVO.color" />
                                                <dsp:getvalueof var="size" param="regItem.sKUDetailVO.size" />
                                                <dsp:getvalueof var="displayName" param="regItem.sKUDetailVO.displayName" />
                                                ${productName}
                                                <c:if test="${color ne null}">,<br/>${color}</c:if>
                                                <c:if test="${size ne null}">, ${size}</c:if>
                                            </span> --%>
                                        </div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="productImage grid_2 alpha">
                                                <dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.mediumImage" />
                                                <c:choose>
                                                    <c:when test="${empty imageURL}">
                                                        <img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="116" width="116" alt="${productName}" class="prodImage" />
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="${imagePath}/_assets/global/images/blank.gif" data-original="${scene7Path}/${imageURL}" alt="${productName}" height="116" width="116" class="prodImage noImageFound loadingGIF" />
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                   
                                    <div class="productContainer grid_10 omega"> 
                                        <ul class="productTab productHeading clearfix">
                                        <dsp:getvalueof var="upc" param="regItem.sKUDetailVO.upc" />
                                        <dsp:getvalueof var="color" param="regItem.sKUDetailVO.color" />
                                        <dsp:getvalueof var="size" param="regItem.sKUDetailVO.size" />
                                        <li class="productName">
                                        
                                       	   <c:if test="${upc ne null}">
												<div class="fl upc"><span class="bold"> <bbbl:label key='lbl_mng_regitem_upc' language="${pageContext.request.locale.language}" /> </span> 
												<span>${upc}</span></div>
											</c:if>
                                        </li>
                                            <li class="price"><bbbl:label key='lbl_mng_mxregitem_price' language="${pageContext.request.locale.language}" /></li>
                                            <li class="requested"><bbbl:label key='lbl_mng_mxregitem_requested' language="${pageContext.request.locale.language}" /></li>
                                            <li class="purchase"><bbbl:label key='lbl_mng_mxregitem_purchased' language="${pageContext.request.locale.language}" /></li>
                                            <li class="quantity"></li>
                                            <li class="productLastColumn"> </li>
                                        </ul>
                                        <ul class="productTab productContent clearfix">
                                            <li class="productName">
                                            	  <c:choose>
                                        			<c:when test="${active}">
                                        				
                                                		<span class="blueName prodTitle">
								<dsp:getvalueof var="jdaProdDesc" param="regItem.productDescrip"  />
									<c:choose>
										<c:when test="${jdaProdDesc != null && not empty jdaProdDesc}">${jdaProdDesc}</c:when>
										<c:otherwise><dsp:valueof param="regItem.sKUDetailVO.displayName" valueishtml="true"/></c:otherwise>
									</c:choose>
								</span>
                                                		<br />
                                                	</c:when>
                                                	<c:otherwise>
                                                		<span class="blueName prodTitle">
                                                		<dsp:getvalueof var="jdaProdDesc" param="regItem.productDescrip"  />
                                                           <c:choose>
                                                                   <c:when test="${jdaProdDesc != null && not empty jdaProdDesc}">${jdaProdDesc}</c:when>
                                                                   <c:otherwise><dsp:valueof param="regItem.sKUDetailVO.displayName" valueishtml="true"/></c:otherwise>
                                                           </c:choose>                                                		
                                                		</span><br />
                                                	</c:otherwise>
                                                </c:choose>
                                                <span class="prodColor noPad"><c:if test='${not empty color}'><bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}"/>&nbsp;:&nbsp;<dsp:valueof value="${color}" valueishtml="true" /></c:if><c:if test='${not empty size}'><c:if test='${not empty color}'><br/></c:if><bbbl:label key="lbl_item_size" language ="${pageContext.request.locale.language}"/>&nbsp;:&nbsp;<dsp:valueof value="${size}" valueishtml="true" /></c:if></span>
                                            </li>
                                            <li class="price bold">
											<dsp:getvalueof var="jdaPrice" param="regItem.jdaRetailPrice"  />
											 <c:choose>
                                              <c:when test="${ jdaPrice == 99999.99 }"><bbbl:label key="lbl_mxreg_zeroprice" language="${pageContext.request.locale.language}"/></c:when>
                                                <c:when test="${jdaPrice != null && not empty jdaPrice && jdaPrice>0 && jdaPrice != 99999.99 }">$${jdaPrice}0
                                                </c:when>
											 <c:otherwise>
											 <c:if test="${inCartFlagSKU}">
												<dsp:droplet name="PriceDroplet">
													<dsp:param name="product" param="product" />
													<dsp:param name="sku" param="sku" />
													<dsp:param name="priceList" value="${incartPriceList}" />
													<dsp:oparam name="output">
														<dsp:getvalueof var="inCartPrice" vartype="java.lang.Double"
															param="price.listPrice" />
													</dsp:oparam>
												</dsp:droplet>
											</c:if>
                                            <dsp:droplet name="PriceDroplet">
                                                <dsp:param name="product" value="${productId}" />
                                                <dsp:param name="sku" value="${skuID}"/>
                                                <dsp:param name="priceList" value="${mxListPriceList}" />
                                                <dsp:oparam name="output">
                                                    <dsp:setvalue param="theListPrice" paramvalue="price"/>
                                                    <dsp:getvalueof var="profileSalePriceList" bean="Profile.salePriceList"/>
                                                      <c:choose>
                                                        <c:when test="${not empty profileSalePriceList}">
                                                            <dsp:droplet name="PriceDroplet">
                                                          	<dsp:param name="priceList" value="${mxSalePriceList}" />
                                                                <dsp:oparam name="output">
                                                                    <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
                                                                    <c:choose>
																	  <c:when test="${inCartFlagSKU}">
																		<c:set var="listPrice" value="${inCartPrice}" />
																	  </c:when>
																	  <c:otherwise>
																	    <dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="price.listPrice"/>
																	  </c:otherwise>
																    </c:choose>
                                                                    
                                                                        <c:if test="${listPrice gt 0.10}">
                                                                        <dsp:include page="/global/gadgets/MxformattedPrice.jsp">
                                                                            <dsp:param name="price" value="${listPrice }"/>
                                                                        </dsp:include>
												                        </c:if>
                                                                    
                                                                   
                                                                        <%-- (<bbbl:label key='lbl_mng_regitem_reg' language="${pageContext.request.locale.language}" />&nbsp;	<dsp:include page="/global/gadgets/MxformattedPrice.jsp">
                                                                                            <dsp:param name="price" value="${price }"/>
                                                                                </dsp:include>)
                                                                    
                                                                    <bbbl:label key='lbl_mng_regitem_you_save' language="${pageContext.request.locale.language}" />&nbsp;
                                                                            <dsp:include page="/global/gadgets/MxformattedPrice.jsp">
                                                                                <dsp:param name="price" value="${price-listPrice}"/>
                                                                            </dsp:include>(<fmt:formatNumber type="number" maxFractionDigits="2" value="${(listPrice/100)*price}" />%)
                                                                     --%>
                                                                </dsp:oparam>
                                                                <dsp:oparam name="empty">
                                                                    <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
                                                                    
                                                                       <c:if test="${price gt 0.10}">
                                                                        <dsp:include page="/global/gadgets/MxformattedPrice.jsp">
                                                                            <dsp:param name="price" value="${price }"/>
                                                                        </dsp:include>
													                  	 </c:if> 
                                                                      
                                                                </dsp:oparam>
                                                            </dsp:droplet><%-- End price droplet on sale price --%>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
                                                            
                                                                <c:if test="${price gt 0.10}">
                                                                <dsp:include page="/global/gadgets/MxformattedPrice.jsp">
                                                                    <dsp:param name="price" value="${price }"/>
                                                                </dsp:include>
											                  	 </c:if> 
                                                            
                                                        </c:otherwise>
                                                    </c:choose>
                                                </dsp:oparam>
                                            </dsp:droplet>
											</c:otherwise>
                                          </c:choose>
                                            </li>
                                            <li class="requested bold"><dsp:valueof param="regItem.qtyRequested" /></li>
                                            <li class="purchase bold"><dsp:valueof param="regItem.qtyPurchased" /></li>
                                            <li class="quantity bold"></li>
                                            <li class="productLastColumn"> 
                                                <dsp:getvalueof var="skuID" param="regItem.sku" />
                                                <input type="hidden" name="skuId" value="${skuID}" class="skuId changeStoreSkuId" data-change-store-submit="skuId" data-change-store-errors="required" /> 
                                                <input type="hidden" name="registryId" value="${registryId}" class="registryId" data-change-store-submit="registryId" data-change-store-errors="required"  /> 
                                                <dsp:droplet name="GiftRegistryFetchProductIdDroplet">
                                                    <dsp:param name="skuId" param="regItem.sku" />
                                                    <dsp:oparam name="output">
                                                        <dsp:getvalueof var="productId" param="productId" />
                                                        <input type="hidden" name="prodId" value="${productId}" class="productId" data-change-store-submit="prodId" />
                                                    </dsp:oparam>
                                                    <dsp:oparam name="error">
                                                        <dsp:getvalueof param="errorMsg" var="errorMsg" />
                                                        <div class="error">
                                                            <bbbe:error key="${errorMsg}"
                                                                language="${pageContext.request.locale.language}" />
                                                        </div>
                                                    </dsp:oparam>
                                                </dsp:droplet>
                                                <dsp:getvalueof param="regItem.qtyRequested" var="requestedQuantity" />
												<dsp:getvalueof param="regItem.qtyPurchased" var="purchasedQuantity" />
                                                <%--  <dsp:droplet name="IsProductSKUShippingDroplet">
														<dsp:param name="siteId" value="${currentSiteId}"/>
											
														<dsp:param name="skuId" value="${skuID}"/>
															<dsp:oparam name="true">
															<div class="fl clearfix cb padTop_10">
		
															<div class="clearfix">
											                	
											                	<dsp:getvalueof var="restrictedAttributes" param="restrictedAttributes"/>																
																<c:forEach var="item" items="${restrictedAttributes}">
															
																<c:choose>
																<c:when test="${null ne item.actionURL}">
																 <a href="${item.actionURL}" class="popup"><span>${item.attributeDescrip}</span></a>
																</c:when>
																<c:otherwise>
																${item.attributeDescrip}
																</c:otherwise>
																</c:choose>	
																</c:forEach>
									              			</div>
									              			</div>
														</dsp:oparam>
														<dsp:oparam name="false">
														</dsp:oparam>
														</dsp:droplet>  --%>
                                            </li>
                                        </ul>
                                        
                                        <ul class="productFooter clearfix">
                                        	<li class="fl">
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
                                                                     <c:set var="actionURL"><dsp:valueof param="attributeVO.actionURL" /></c:set>
															         <c:choose>
																		<c:when test="${not empty actionURL}">
																			<li <c:if test="${fn:contains(actionURL,'ltlDeliveryInfo') }">class="ltlTruck"</c:if>><a href="${actionURL}" class="popup"><span><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></span></a></li>
																		</c:when>
																		<c:otherwise>
																			<li><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></li>
																		</c:otherwise>
																	</c:choose>
                                                                    <c:choose>
                                                                        <c:when test="${currentCount%2 == 0}">
                                                                            <c:set var="nextStyle">prodSecondaryAttribute</c:set>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <c:set var="nextStyle">prodPrimaryAttribute</c:set>
                                                                        </c:otherwise>
                                                                    </c:choose>	
                                                                </dsp:oparam>
                                                            </dsp:droplet>
                                                        </c:if>							
                                                    </dsp:oparam>
                                                </dsp:droplet>
                                               </ul>
                                              </li>
                                              <li class="fr">
                                              <dsp:getvalueof var="bopusAllowed" param="regItem.sKUDetailVO.bopusAllowed"/>
											</li>
                                        </ul>
                                    </div>	
                                    
                                    <div class="clear"></div>								
                                </li>
                            </c:if>
						</dsp:oparam>
                        <dsp:oparam name="outputEnd">
                        			<li  class="productRow textRight returnTopRow">
											<a class="returnToTop" href="#backToTop"><bbbl:label key='lbl_mng_mxregitem_back_to_top' language="${pageContext.request.locale.language}" /></a>
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
			
			<c:if test="${countIE eq sizeIE}">
					</div>
				</c:if>
			
		</dsp:oparam>
	</dsp:droplet>
	
    <%-- End ForEach for categoryBuckets map --%>
	<dsp:getvalueof var="emptyOutOfStockListFlag" param="emptyOutOfStockListFlag"/>
    <%-- <p class="alpha space seperator">
		<bbbt:textArea key="txt_mng_regitem_chkfirst" language="${pageContext.request.locale.language}" />
		<a href="${contextPath}/selfservice/store/find_store.jsp" title='<bbbl:label key="lbl_mng_regitem_clickhere" language ="${pageContext.request.locale.language}"/>'><bbbl:label key="lbl_mng_regitem_clickhere" language="${pageContext.request.locale.language}" /></a>&nbsp;
		<c:choose>
		<c:when test="${currentSiteId eq BuyBuyBabySite}">
			<bbbt:textArea key="txt_mng_regitem_callbbby" language="${pageContext.request.locale.language}" />
		</c:when>
		<c:otherwise>
			<bbbt:textArea key="txt_mng_regitem_callbbb" language="${pageContext.request.locale.language}" />
		</c:otherwise>
	    </c:choose>
	</p> --%>
	<c:if test="${emptyOutOfStockListFlag ne 'true' }">	
	<p class="alpha space"><bbbt:textArea key="txt_mng_regitem_items_unavail" language="${pageContext.request.locale.language}" /></p>
	
	<%-- ForEach for categoryBuckets map --%>
	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="priceRangeList" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="sizeIE" param="size"/>
				<dsp:getvalueof var="countIE" param="count"/>
				<c:choose>
					<c:when test="${countIE eq 1}">
						
						<div class="accordionReg4 accordionReg accordion ui-accordion ui-widget ui-helper-reset ui-accordion-icons" id="accordionReg4">
					</c:when>
					<c:when test="${countIE eq 5}">
						</div>
						
						<div class="accordionReg5 accordionReg  accordion ui-accordion ui-widget ui-helper-reset ui-accordion-icons" id="accordionReg5">
					</c:when>
					<c:when test="${countIE eq 12}">
						</div>
						
						<div class="accordionReg6 accordionReg  accordion ui-accordion ui-widget ui-helper-reset ui-accordion-icons" id="accordionReg6">
					</c:when>
				</c:choose>
			<dsp:getvalueof var="priceRange" param="element"/>
			<dsp:droplet name="ForEach">
				<dsp:param name="array" param="notInStockCategoryBuckets" />
				<%--<dsp:param name="sortProperties" value="+_key" />--%>
				<dsp:oparam name="output">
				
				
				
				<dsp:getvalueof var="bucketName" param="key"
					idtype="java.lang.String" />
				<dsp:getvalueof var="bucket"
					param="notInStockCategoryBuckets.${bucketName}" idtype="java.util.List" />
				<c:if test="${priceRange eq bucketName}">
				<dsp:getvalueof var="count" value="${fn:length(bucket)}"/>
				<c:if test="${count ne 0}">
					<h2 class="ui-accordion-header ui-helper-reset ui-state-default ui-corner-all"><span class="ui-icon ui-icon-triangle-1-e"></span><a href="#"><dsp:valueof param="key" />(${count})</a></h2>
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
							<li class="clearfix productRow btngiftViewTopMargin giftViewProductChangeStoreWrapper addToCartGiftRegWrapper "> 							
								<dsp:getvalueof var="skuID" vartype="java.lang.String"  param="regItem.sku" />
								<dsp:droplet name="GiftRegistryFetchProductIdDroplet">
									<dsp:param name="skuId" value="${skuID}" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="productId" vartype="java.lang.String" param="productId" />
										<dsp:getvalueof var="inStoreSku" param="inStore" />
									</dsp:oparam>
								</dsp:droplet>
								
								<input type="hidden" name="bts" value="false" class="addToCartSubmitData" />
								<input type="hidden" name="storeId" value="" data-change-store-storeid="storeId" class="addToCartSubmitData"/>
								<dsp:getvalueof var="inCartFlagSKU" param="regItem.sKUDetailVO.inCartFlag"/>
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
                                    <dsp:getvalueof var="jdaProdDesc" param="regItem.productDescrip"  />
					<c:choose>
						<c:when test="${jdaProdDesc != null && not empty jdaProdDesc}">${jdaProdDesc}</c:when>
						<c:otherwise><dsp:valueof param="regItem.sKUDetailVO.displayName" valueishtml="true"/></c:otherwise>
					</c:choose>
                                </c:set>
								<c:choose>
									<c:when test="${active}">
									<div class="productImage grid_2 alpha">
										<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.mediumImage" />
										<c:choose>
											<c:when test="${empty imageURL}">
												<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="116" width="116" alt="${productName}" class="prodImage" />
											</c:when>
											<c:otherwise>
												<img src="${imagePath}/_assets/global/images/blank.gif" data-original="${scene7Path}/${imageURL}" alt="${productName}" height="116" width="116" class="prodImage noImageFound loadingGIF" />
											</c:otherwise>
										</c:choose>
                                        <%-- <span class="productTitle grid_3">
											<dsp:getvalueof var="color" param="regItem.sKUDetailVO.color" />
											<dsp:getvalueof var="size" param="regItem.sKUDetailVO.size" />
											<dsp:getvalueof var="displayName" param="regItem.sKUDetailVO.displayName" />
											${productName}
											<c:if test="${color ne null}">,<br/>${color}</c:if>
											<c:if test="${size ne null}">, ${size}</c:if>
										</span> --%>
									</div>
									</c:when>
									<c:otherwise>
									<div class="productImage grid_2 alpha">
										<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.mediumImage" />
										<c:choose>
											<c:when test="${empty imageURL}">
												<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="116" width="116" alt="${productName}" class="prodImage" />
											</c:when>
											<c:otherwise>
												<img src="${imagePath}/_assets/global/images/blank.gif" data-original="${scene7Path}/${imageURL}" alt="${productName}" height="116" width="116" class="prodImage noImageFound loadingGIF" />
											</c:otherwise>
										</c:choose>
									</div>
									</c:otherwise>
								</c:choose>
								 <dsp:getvalueof var="upc" param="regItem.sKUDetailVO.upc" />
								 <dsp:getvalueof var="color" param="regItem.sKUDetailVO.color" />						
								  <dsp:getvalueof var="size" param="regItem.sKUDetailVO.size" />
								
								<div class="productContainer grid_10 omega">
									<ul class="productTab productHeading clearfix">
										<li class="productName">
											
											<c:if test="${upc ne null}">
												<div class="fl upc"><span class="bold"> <bbbl:label key='lbl_mng_regitem_upc' language="${pageContext.request.locale.language}" /> </span> 
												<span>${upc}</span></div>
											</c:if>
										</li>
										<li class="price"><bbbl:label key='lbl_mng_regitem_price' language="${pageContext.request.locale.language}" /></li>
										<li class="requested"><bbbl:label key='lbl_mng_regitem_requested' language="${pageContext.request.locale.language}" /></li>
										<li class="purchase"><bbbl:label key='lbl_mng_regitem_purchased' language="${pageContext.request.locale.language}" /></li>
										<li class="quantity"></li>
										<li class="productLastColumn"> </li>
									</ul>
									<ul class="productTab productContent clearfix">
										<li class="productName">
												<c:choose>
                                        			<c:when test="${active}">
                                        				<a href="${contextPath}${finalUrl}?skuId=${skuID}&registryId=${registryId}" 
                                        				class="lnkQVPopup" data-pdp-qv-url="pdp_quick_view.jsp?skuId=${skuID}&registryId=${registryId}&productId=${productId}"
                                        				title="${productName}">
                                        				<span class="blueName prodTitle">
                                        				<dsp:getvalueof var="jdaProdDesc" param="regItem.productDescrip"  />
                                                           <c:choose>
                                                                   <c:when test="${jdaProdDesc != null && not empty jdaProdDesc}">${jdaProdDesc}</c:when>
                                                                   <c:otherwise><dsp:valueof param="regItem.sKUDetailVO.displayName" valueishtml="true"/></c:otherwise>
                                                           </c:choose>
                                                           </span>                                                		 
                                                		</a><br />
                                                	</c:when>
                                                	<c:otherwise>
                                                		<span class="blueName prodTitle">
														<dsp:getvalueof var="jdaProdDesc" param="regItem.productDescrip"  />
															<c:choose>
																<c:when test="${jdaProdDesc != null && not empty jdaProdDesc}">${jdaProdDesc}</c:when>
																	<c:otherwise><dsp:valueof param="regItem.sKUDetailVO.displayName" valueishtml="true"/></c:otherwise>
															</c:choose>
														</span><br />
                                                	</c:otherwise>
                                                </c:choose>
                                                <span class="prodColor noPad"><c:if test='${not empty color}'><bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}"/>&nbsp;:&nbsp;<dsp:valueof value="${color}" valueishtml="true" /></c:if><c:if test='${not empty size}'><c:if test='${not empty color}'><br/></c:if><bbbl:label key="lbl_item_size" language ="${pageContext.request.locale.language}"/>&nbsp;:&nbsp;<dsp:valueof value="${size}" valueishtml="true" /></c:if></span>
										</li>
										<li class="price bold">
											<dsp:getvalueof var="jdaPrice" param="regItem.jdaRetailPrice"  />
												<c:choose>
<c:when test="${ jdaPrice == 99999.99 }"><bbbl:label key="lbl_mxreg_zeroprice" language="${pageContext.request.locale.language}"/></c:when>
                                                <c:when test="${jdaPrice != null && not empty jdaPrice && jdaPrice>0 && jdaPrice != 99999.99 }">$${jdaPrice}0
                                                </c:when>												<c:otherwise>
                                                
                                                <c:if test="${inCartFlagSKU}">
												<dsp:droplet name="PriceDroplet">
													<dsp:param name="product" param="product" />
													<dsp:param name="sku" param="sku" />
													<dsp:param name="priceList" value="${incartPriceList}" />
													<dsp:oparam name="output">
														<dsp:getvalueof var="inCartPrice" vartype="java.lang.Double"
															param="price.listPrice" />
													</dsp:oparam>
												</dsp:droplet>
											</c:if>
										<dsp:droplet name="PriceDroplet">
							                <dsp:param name="product" value="${productId}" />
							                <dsp:param name="sku" value="${skuID}"/>
							                <dsp:param name="priceList" value="${mxListPriceList}" />
							                <dsp:oparam name="output">
							                  	<dsp:setvalue param="theListPrice" paramvalue="price"/>
							                  	<dsp:getvalueof var="profileSalePriceList" bean="Profile.salePriceList"/>
								                  <c:choose>
								                  	<c:when test="${not empty profileSalePriceList}">
								                      	<dsp:droplet name="PriceDroplet">
								                      	<dsp:param name="priceList" value="${mxSalePriceList}" />
									                        <dsp:oparam name="output">
									                            <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
									                            <c:choose>
																 <c:when test="${inCartFlagSKU}">
																	<c:set var="listPrice" value="${inCartPrice}" />
																 </c:when>
																 <c:otherwise>
																    <dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="price.listPrice"/>
																 </c:otherwise>
															    </c:choose>
									                           
										                            <c:if test="${listPrice gt 0.10}">
										                            <dsp:include page="/global/gadgets/MxformattedPrice.jsp">
										                              	<dsp:param name="price" value="${listPrice }"/>
										                            </dsp:include>
											                        </c:if>
										                        
										                       
                                                                    <%-- (<bbbl:label key='lbl_mng_regitem_reg' language="${pageContext.request.locale.language}" />&nbsp;	<dsp:include page="/global/gadgets/MxformattedPrice.jsp">
												          								<dsp:param name="price" value="${price }"/>
												              				</dsp:include>)
														       
																<bbbl:label key='lbl_mng_regitem_you_save' language="${pageContext.request.locale.language}" />&nbsp;
													      				<dsp:include page="/global/gadgets/MxformattedPrice.jsp">
										                                	<dsp:param name="price" value="${price-listPrice}"/>
										                              	</dsp:include>(<fmt:formatNumber type="number" maxFractionDigits="2" value="${(listPrice/100)*price}" />%)
										                         --%>
											                </dsp:oparam>
									                        <dsp:oparam name="empty">
										                    	<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
										                    	<c:if test="${price gt 0.10}">
											                        <dsp:include page="/global/gadgets/MxformattedPrice.jsp">
											                          	<dsp:param name="price" value="${price }"/>
											                        </dsp:include>
											                  	 </c:if> 
									                        </dsp:oparam>
									                	</dsp:droplet><%-- End price droplet on sale price --%>
								                    </c:when>
								                    <c:otherwise>
								                      	<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
								                      	
									                      	<c:if test="${price gt 0.10}">
									                      	<dsp:include page="/global/gadgets/MxformattedPrice.jsp">
									                        	<dsp:param name="price" value="${price }"/>
									                      	</dsp:include>
										                  	 </c:if> 
									                   
								                    </c:otherwise>
								             	</c:choose>
							                </dsp:oparam>
							            </dsp:droplet>
							            	</c:otherwise>
							            	</c:choose>
										</li>
										<li class="requested bold"><dsp:valueof param="regItem.qtyRequested" /></li>
										<li class="purchase bold"><dsp:valueof param="regItem.qtyPurchased" /></li>
										<li class="quantity bold"></li>
										<li class="productLastColumn"> 
                                            <dsp:getvalueof var="skuID" param="regItem.sku" />
                                            <input type="hidden" name="skuId" value="${skuID}" class="skuId changeStoreSkuId" data-change-store-submit="skuId" data-change-store-errors="required" /> 
                                            <input type="hidden" name="registryId" value="${registryId}" class="registryId" data-change-store-submit="registryId" data-change-store-errors="required" />
                                            <dsp:droplet name="GiftRegistryFetchProductIdDroplet">
												<dsp:param name="skuId" param="regItem.sku" />
												<dsp:oparam name="output">
													<dsp:getvalueof var="productId" param="productId" />
													<input type="hidden" name="prodId" value="${productId}" class="productId" data-change-store-submit="prodId"/>
												</dsp:oparam>
												<dsp:oparam name="error">
													<dsp:getvalueof param="errorMsg" var="errorMsg" />
													<div class="error">
														<bbbe:error key="${errorMsg}"
															language="${pageContext.request.locale.language}" />
													</div>
												</dsp:oparam>
											</dsp:droplet> 
											
																						
											<dsp:getvalueof param="regItem.qtyRequested" var="requestedQuantity" />
											<dsp:getvalueof param="regItem.qtyPurchased" var="purchasedQuantity" />
								        	<c:if test="${purchasedQuantity ge requestedQuantity}">
												<div class="purchasedConfirmation">
													  <h2><bbbl:label key='lbl_mng_regitem_purchased_confirm' language="${pageContext.request.locale.language}" /></h2>
												</div>
											</c:if>
											
										</li>
									</ul>
									
									<ul class="productFooter clearfix">
										<li class="fl">
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
																 <c:set var="actionURL"><dsp:valueof param="attributeVO.actionURL" /></c:set>
															         <c:choose>
																		<c:when test="${not empty actionURL}">
																			<li <c:if test="${fn:contains(actionURL,'ltlDeliveryInfo') }">class="ltlTruck"</c:if>><a href="${actionURL}" class="popup"><span><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></span></a></li>
																		</c:when>
																		<c:otherwise>
																			<li><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></li>
																		</c:otherwise>
																	</c:choose>
																<c:choose>
								                          			<c:when test="${currentCount%2 == 0}">
												            			<c:set var="nextStyle">prodSecondaryAttribute</c:set>
												            		</c:when>
												            		<c:otherwise>
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
											</c:if> --%>
											</li>
											<li class="fr">
											<dsp:getvalueof param="regItem.qtyRequested" var="requestedQuantity" />
											<dsp:getvalueof param="regItem.qtyPurchased" var="purchasedQuantity" />
											<dsp:getvalueof var="bopusAllowed" param="regItem.sKUDetailVO.bopusAllowed"/>											
											</li>
									</ul>
								</div>
                                <div class="clear"></div>									
							</li>
							</c:if>
							</dsp:oparam>
                    <dsp:oparam name="outputEnd">
                    
										<c:if test="${count ne 0 }">	
										<li  class="productRow textRight returnTopRow">
											<a class="returnToTop" href="#backToTop"><bbbl:label key='lbl_mng_mxregitem_back_to_top' language="${pageContext.request.locale.language}" /></a>
											<div class="clear"></div>
										</li>
										</c:if>	
								</ul>
							</div>
                    </dsp:oparam>
							</dsp:droplet>

					
				</c:if>
				
				
				</dsp:oparam>
			</dsp:droplet>
			
			<c:if test="${countIE eq sizeIE}">
					</div>
				</c:if>
			
		</dsp:oparam>
	</dsp:droplet>
	
	</c:if>
</dsp:page>