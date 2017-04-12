<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
	<dsp:importbean bean="/atg/dynamo/droplet/Range" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/com/bbb/search/droplet/TBSQuickViewDroplet"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/com/bbb/selfservice/TBSPDPInventoryDroplet" />
    <dsp:importbean bean="/com/bbb/commerce/droplet/TBSItemExclusionDroplet"/>
    <dsp:importbean bean="/com/bbb/commerce/browse/droplet/SKUDetailDroplet"/>
    <dsp:importbean bean="/com/bbb/commerce/checkout/droplet/GetApplicableLTLShippingMethodsDroplet" />
    
	<dsp:getvalueof var="count" param="BBBProductListVO.bbbProductCount" />
	<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductListHandler"/>
	<dsp:getvalueof var="CategoryId" param="CatalogRefId" />
	<dsp:getvalueof var="promoSC" param="promoSC"/>
	<dsp:getvalueof var="plpGridSize" param="plpGridSize"/>
	<dsp:getvalueof param="browseSearchVO" var="browseSearchVO"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof param="searchType" var="searchType"/>
	<dsp:getvalueof param="keyword" var="keyword"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
	<c:set var="ltlCount" value="0"/>
	<c:set var="TBS_BedBathCanadaSite">
		<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
		
	<c:set var="compareProducts"><bbbc:config key="compareProducts" configName="FlagDrivenFunctions" /></c:set>
	<c:set var="TruckSrc"><bbbc:config key="truck_image_src" configName="LTLConfig_KEYS" /></c:set>
	<div class="small-12 columns product-info upcProductDetails 1">
	<dsp:form action="/tbs/search/upcresults.jsp?keyword=${searchTerm}&type=upc" method="post" id="upcgridForm">
	<div class="listDataItemsWrap registryDataItemsWrap">
	<dsp:droplet name="TBSItemExclusionDroplet">
			<dsp:param name="siteId" value="${appid}"/>
			<dsp:param name="upcItems" value="${searchTerm}"/>
			<dsp:oparam name="output">
				<dsp:getvalueof var="isItemExcluded" param="validItemExcludedSku"/>
				<dsp:getvalueof var="caDisabled" param="caDisabled"/>
			</dsp:oparam>
			<dsp:oparam name="error">
				<dsp:getvalueof var="caDisabled" param="caDisabled"/>
			</dsp:oparam>
	</dsp:droplet>
	<dsp:droplet name="TBSQuickViewDroplet">
		<dsp:param name="array" param="BBBProductListVO.bbbProducts" />
		<dsp:oparam name="outputStart">
			<ul class="small-block-grid-1 plpListGridToggle">
		</dsp:oparam>
		<dsp:oparam name="outputEnd">
			</ul>
		</dsp:oparam>
		<dsp:getvalueof param="size" var="totalSize"/>
		<dsp:oparam name="output">
			<dsp:getvalueof param="count" var="currentCount"/>
			<dsp:getvalueof var="productId" param="productVO.productID" />

			<dsp:getvalueof var="previousProductId" param="productVO.previousProductId" />
			<dsp:getvalueof var="nextProductId" param="productVO.nextProductId" />
			<dsp:getvalueof var="ltlProduct" param="productVO.ltlProduct" />
			<dsp:getvalueof var="cmokirschFlag" param="productVO.cmokirschFlag" />
			
			<c:if test="${not empty ltlProduct && ltlProduct eq true}">
				<c:set var="ltlCount" value="${ltlCount+1}"/>
			</c:if>

			<dsp:getvalueof var="productName" param="productVO.productName"/>
			<dsp:getvalueof var="attribs" param="productVO.attribute" />
			<dsp:getvalueof var="originalPrice" param="productVO.wasPriceRange" />
			<dsp:getvalueof var="finalPrice" param="productVO.priceRange" />
			
			<%-- Check for Swatch Flag attribute returned from Search Engine--%>
			<dsp:getvalueof var="swatchFlag" param="productVO.swatchFlag"/>
			<dsp:getvalueof var="rollupFlag" param="productVO.rollupFlag"/>
			<dsp:getvalueof var="collectionFlag" param="productVO.collectionFlag"/>
			<dsp:getvalueof var="promoSC" param="promoSC"/>
			<dsp:getvalueof var="portrait" param="portrait"/>
			<dsp:getvalueof id="count" param="count" />
			<dsp:getvalueof id="productCountDisplayed" param="size" />
				<li class="categoryItems" id="categoryItems_${count}">
					<div class="row">
						<div class="small-12 columns large-5">
							<dsp:getvalueof var="seoUrl" param="productVO.seoUrl"/>
							<c:choose>
								<c:when test="${empty seoUrl}">
									<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
										<dsp:param name="id" param="productVO.productID" />
										<dsp:param name="itemDescriptorName" value="product" />
										<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
										<dsp:oparam name="output">
										<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
										<dsp:getvalueof param="productVO.skuId" var="skuId"/>
										<c:if test="${not empty skuId}">
											<dsp:getvalueof var="finalUrl" value="${finalUrl}?skuId=${skuId}" />
										</c:if>
										</dsp:oparam>
									</dsp:droplet>
								</c:when>
								<c:otherwise>
									<c:set var="finalUrl" value="${seoUrl}"></c:set>
								</c:otherwise>
							</c:choose>
							<c:if test="${not empty cmokirschFlag && cmokirschFlag eq true}">
								<c:set var="finalUrl" value="#"></c:set>
							</c:if>
							<dsp:getvalueof var="portraitClass" value=""/>
							<dsp:getvalueof var="imageURL" param="productVO.imageURL"/>
							<c:set var="imageURL"><c:out value='${fn:replace(imageURL,"$146$","$478$")}'/></c:set>
							<c:if test="${portrait eq 'true'}">
								<dsp:getvalueof var="portraitClass" value="prodImgPortrait"/>
								<dsp:getvalueof var="imgHeight" value="200"/>
								<dsp:getvalueof var="imageURL" param="productVO.verticalImageURL"/>
							</c:if>
							<div class="category-prod-img small-12 columns large-4">
								<c:choose>
									<c:when test="${CategoryId eq null}">
										
										<dsp:a iclass="" page="${finalUrl}" title="${productName}">
											<%-- Thumbnail image exists OR not--%>
											<c:choose>
												<c:when test="${not empty imageURL}">
													<img class="stretch" src="${scene7Path}/${imageURL}" height="${imageSize}" width="${imageSize}" alt="${productName}" />
												</c:when>
												<c:otherwise>
													<img class="stretch" src='${imagePath}/_assets/global/images/no_image_available.jpg' alt="${productName}" width="${imageSize}" height="${imageSize}" />
												</c:otherwise>
											</c:choose>
										</dsp:a>
									</c:when>
									<c:otherwise>
										
										<dsp:a iclass="" page="${finalUrl}?categoryId=${CategoryId}" title="${productName}">
											<%-- Thumbnail image exists OR not--%>
											<c:choose>
												<c:when test="${not empty imageURL}">
													<img class="stretch" src="${scene7Path}/${imageURL}" height="${imageSize}" width="${imageSize}" alt="${productName}" />
												</c:when>
												<c:otherwise>
													<img class="stretch" src='${imagePath}/_assets/global/images/no_image_available.jpg' alt="${productName}" width="${imageSize}" height="${imageSize}" />
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
								</div>
							</div>

							<c:choose>
								<c:when test="${rollupFlag eq 'true' || collectionFlag == '1'}">
									<c:set var="isMultiRollUpFlag" value="true" />
								</c:when>
								<c:otherwise>
									<c:set var="isMultiRollUpFlag" value="false" />
								</c:otherwise>
							</c:choose>

							<div class="small-12 columns large-8 product-title">
								<h2 class="subheader">
									<dsp:getvalueof var="seoUrl" param="productVO.seoUrl"/>
										<c:choose>
										<c:when test="${empty seoUrl}">
											<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
												<dsp:param name="id" param="productVO.productID" />
												<dsp:param name="itemDescriptorName" value="product" />
												<dsp:param name="repositoryName"
													value="/atg/commerce/catalog/ProductCatalog" />
												<dsp:oparam name="output">
												<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
														param="url" />
												</dsp:oparam>
												<dsp:getvalueof param="productVO.skuId" var="skuId"/>
												<c:if test="${not empty skuId}">
													<dsp:getvalueof var="finalUrl" value="${finalUrl}?skuId=${skuId}" />
												</c:if>
											</dsp:droplet>
										</c:when>
										<c:otherwise>
											<c:set var="finalUrl" value="${seoUrl}"></c:set>
										</c:otherwise>
										</c:choose>
									<c:set var="prodName">
										<dsp:valueof param="productVO.productName" valueishtml="true"/>
									</c:set>
	
									<c:if test="${not empty cmokirschFlag && cmokirschFlag eq true}">
										<c:set var="finalUrl" value="#"></c:set>
									</c:if>
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
								<span>SKU<dsp:valueof param="productVO.skuId"/></span>
							</div>						
						</div>

						<div class="small-12 columns large-7">
							<c:if test="${swatchFlag == '1'}">
								<div class="color-swatches">
									<dsp:droplet name="ForEach">
										<dsp:param name="array" param="productVO.colorSet" />

										<dsp:oparam name="outputStart">
											<ul class="color-swatch-grid">
										</dsp:oparam>
										<dsp:oparam name="outputEnd">
											</ul>
										</dsp:oparam>
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

											<li><img src="${scene7Path}/${swatchUrl}" alt="${colorName}"/></li>

										</dsp:oparam>
									</dsp:droplet>
								</div>
							</c:if>
							<dsp:droplet name="ForEach">
							<dsp:param name="array" value="${attribs}" />
							<dsp:oparam name="output">
								<div class="prodAttribute small-12 columns"><dsp:valueof param="element" valueishtml="true" /></div>
							</dsp:oparam>
							</dsp:droplet>

							<div class="price product-info large-right small-12 columns large-2 large-offset-1">
								<dsp:droplet name="IsEmpty">
								<dsp:param name="value" param="productVO.wasPriceRange"/>
								<dsp:oparam name="false">
									<h3 class="price-sale no-margin-bottom"><dsp:valueof converter="currency" param="productVO.priceRange" /></h3>
									<h3 class="price-original no-padding-left"><dsp:valueof converter="currency" param="productVO.wasPriceRange" /></h3>
							   	</dsp:oparam >
						   		<dsp:oparam name="true">
						   			<h3 class="isPrice price">$<dsp:valueof param="productVO.priceRange" converter="currency" /></h3>	
						   		</dsp:oparam>
								</dsp:droplet>
							</div>
							<div class="small-12 columns large-9">
								<c:if test="${not empty searchType && searchType eq 'upc'}">
									<div class="row prodQty fl">
										<div class="small-12 large-4 large-right columns quantity no-padding">
											<dsp:getvalueof var="skuid" param="productVO.skuId"/>
											<dsp:getvalueof var="productID" param="productVO.productID"/>
											<dsp:getvalueof param="productVO.priceRange" var="omniPrice"/>
											<input type="hidden" name="prodId" class="_prodId addItemToRegis productId addItemToList" value="${productID}" data-change-store-submit="prodId" data-change-store-errors="required" data-change-store-internalData="true"/>
											<input type="hidden" name="skuId" value="${skuid}" class="addItemToRegis _skuId addItemToList changeStoreSkuId" data-change-store-submit="skuId" data-change-store-errors="required" data-change-store-internalData="true"/>		
											<dsp:input bean="CartModifierFormHandler.productIds" type="hidden" paramvalue="productVO.productID" />
											<dsp:input bean="CartModifierFormHandler.catalogRefIds"  type="hidden" paramvalue="productVO.skuId" />
											<input type="hidden" name="price" value="${omniPrice}" class="addItemToList addItemToRegis" />
											<input type="hidden" name="pagetype" value="MIE" />
											<div class="qty-spinner columns large-right no-padding" style="width:auto;">
											 <c:choose>
                                                <c:when test="${cmokirschFlag}">
                                                    <a class="button minus_disabled secondary" title="Quantities cannot be modified in The Beyond Store"><span></span></a>
                                                    <input name="qty" id="pqty${productID}" data-max-value="99" maxlength="2" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="fl pqty qty addItemToRegis _qty itemQuantity addItemToList quantity-input" type="text" value="1"  data-change-store-submit="qty" data-change-store-errors="required digits nonZero" role="textbox" aria-required="true" aria-describedby="pqty${productID}" disabled="disabled" />
                                                    <a class="button plus_disabled secondary" title="Quantities cannot be modified in The Beyond Store"><span></span></a>
                                                </c:when>
                                                <c:otherwise>
													<a class="button minus secondary"><span></span></a>
													<input name="qty" id="pqty${productID}" data-max-value="99" maxlength="2" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="fl pqty qty addItemToRegis _qty itemQuantity addItemToList quantity-input" type="text" value="1"  data-change-store-submit="qty" data-change-store-errors="required digits nonZero" role="textbox" aria-required="true" aria-describedby="pqty${productID}"  />
													<a class="button plus secondary"><span></span></a>
												 </c:otherwise>
                                             </c:choose>	
											</div>
										</div>
									</div>
									<dsp:droplet name="SKUDetailDroplet">
										<dsp:param name="skuId" value="${skuid}"/>
										<dsp:oparam name="output">
											<dsp:getvalueof param="pSKUDetailVO.vdcSku" var="vdcSku"/>
											<dsp:getvalueof param="pSKUDetailVO.ltlItem" var="ltlItem"/>
												
											<c:if test="${not ltlItem && vdcSku ne true}">
												<div class="small-12 product-other-links availability large-right">
													<dsp:droplet name="TBSPDPInventoryDroplet">
														<dsp:param name="sku" value="${skuid}" />
														<dsp:param name="siteId" value="${appid}" />
														<dsp:oparam name="output">
															<dsp:getvalueof param="timeframe" var="timeframe"></dsp:getvalueof>
															<dsp:getvalueof param="nearbyStore" var="nearbyStore"></dsp:getvalueof>
															<dsp:getvalueof param="nearbyStoreLink" var="nearbyStoreLink"></dsp:getvalueof>
															<dsp:getvalueof param="currentStoreQty" var="currentStoreQty"></dsp:getvalueof>
															<a class="pdp-sprite in-store <c:if test='${currentStoreQty < 1}'> no-stock</c:if>" <c:if test="${currentStoreQty < 1}">disabled='disabled'</c:if>>${currentStoreQty} In Store</a>
							
															<a href="/tbs/browse/shipping_policies.jsp" class="pdp-sprite online popupShipping <c:if test="${timeframe eq '0004'}"> no-stock</c:if>" <c:if test="${timeframe eq 0004}">disabled='disabled'</c:if>>
																<c:if test="${timeframe eq '0001'}">
																	<bbbl:label key="lbl_tbs_ship_time_0001" language="${pageContext.request.locale.language}" />
																</c:if>
																<c:if test="${timeframe eq '0002'}">
																	<bbbl:label key="lbl_tbs_ship_time_0002" language="${pageContext.request.locale.language}" />
																</c:if>
																<c:if test="${timeframe eq '0003'}">
																	<bbbl:label key="lbl_tbs_ship_time_0003" language="${pageContext.request.locale.language}" />
																</c:if>
																<c:if test="${timeframe eq '0004'}">
																	<bbbl:label key="lbl_tbs_ship_time_0004" language="${pageContext.request.locale.language}" />
																</c:if>
																<c:if test="${timeframe eq '0005'}">
																	<bbbl:label key="lbl_tbs_ship_time_0005" language="${pageContext.request.locale.language}" />
																</c:if>
															</a>
															<c:if test="${HolidayMessagingOn}">
															 	<dsp:include src="${contextPath}/common/holidayMessaging.jsp">
																	<dsp:param name="nearbyStoreLink" value="${nearbyStoreLink}"/>
															 		<dsp:param name="showNearbyStorelink" value="true"/>								 		
															 		<dsp:param name="timeframe" value="${timeframe}"/>
															 	</dsp:include>
															</c:if>
															<c:if test="${MapQuestOn}">
																<c:choose>
																	<c:when test='${nearbyStoreLink eq true}'>
																	    <dsp:a iclass="pdp-sprite nearby-stores 
																	         in-store no-padding-right" href="/tbs/selfservice/find_tbs_store.jsp">
																	         <dsp:param name="skuid" value="${skuid}" />
																	         <dsp:param name="itemQuantity" value="1" />
																	         <dsp:param name="id" value="${productID}" />
																	         <dsp:param name="siteId" value="${appid}" />
																	         <dsp:param name="skuId" value="${skuid}" />
																	         <dsp:param name="registryId" param="registryId" />
																	         Nearby Stores
																	     </dsp:a>
																	     <div id="nearbyStore" class="reveal-modal" data-reveal>
																	     </div>
																	</c:when>
																	<c:otherwise>
																		<span class="pdp-sprite nearby-stores no-stock no-padding-right" disabled="disabled" />
																			Nearby Stores
																		</span>
																	</c:otherwise>
																</c:choose>
															</c:if>
														</dsp:oparam>
														<dsp:oparam name="empty">
															<bbbl:label key="lbl_favoritestore_disablemessage" language="${pageContext.request.locale.language}" />
														</dsp:oparam>
														<dsp:oparam name="error">
														</dsp:oparam>
													</dsp:droplet>
													</div>
												</c:if>
											</dsp:oparam>
										</dsp:droplet>
										<dsp:input bean="CartModifierFormHandler.queryParam"
												type="hidden"
												value="keyword=${searchTerm}&type=upc" /> 
							           <dsp:input bean="CartModifierFormHandler.fromPage"  type="hidden" value="productListUPCRwd" />		
									<%-- <dsp:input bean="CartModifierFormHandler.addItemToOrderSuccessURL"  type="hidden" value="/tbs/search/upcresults.jsp?keyword=${searchTerm}&type=upc"/>
									<dsp:input bean="CartModifierFormHandler.addItemToOrderErrorURL"  type="hidden" value="/tbs/search/upcresults.jsp?keyword=${searchTerm}&type=upc" /> --%>

								</c:if>								
							
								<c:if test="${appid ne TBS_BedBathCanadaSite && ltlProduct}">
									<dsp:droplet name="GetApplicableLTLShippingMethodsDroplet">
										<dsp:param name="skuId" value="${skuId}" />
										<dsp:param name="siteId" value="${appid}" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="shipMethodVOList" param="shipMethodVOList" />
										</dsp:oparam>
									</dsp:droplet>
									<div class="row">
										<div class="small-12 large-6 large-right columns no-padding">
											<a class="secondary radius button dropdown selServiceLevel small-11 large-12" data-dropdown="selServiceLevel_${skuId}" href="#" ><bbbl:label key="lbl_pdp_sel_service" language="<c:out param='${language}'/>" /><span></span></a>
											<ul class="f-dropdown medium selServiceUl" data-dropdown-content data-ltlflag="true" aria-hidden="true" id="selServiceLevel_${skuId}" name="selServiceLevel">
												<dsp:droplet name="ForEach">
													<dsp:param value="${shipMethodVOList}" name="array" />
													<dsp:oparam name="outputStart">
														<li class="selected"><a href="#"><bbbl:label key="lbl_pdp_sel_service" language="<c:out param='${language}'/>" /></a></li>
													</dsp:oparam>
													<dsp:oparam name="output">
														<dsp:getvalueof var="shipMethodId" param="element.shipMethodId" />
														<dsp:getvalueof var="shipMethodName" param="element.shipMethodDescription" />
														<dsp:getvalueof var="deliverySurchargeval" param="element.deliverySurcharge" />
														<c:if test="${deliverySurchargeval gt 0}">
															<c:set var="deliverySurcharge">
																<dsp:valueof converter="currency" param="element.deliverySurcharge" />
															</c:set>
														</c:if>
														<c:if test="${deliverySurchargeval eq 0}">
															<c:set var="deliverySurcharge">Free</c:set>
														</c:if>
														<li><a href="#" data-method_id="${shipMethodId}" data-method_name="${shipMethodName}" data-surcharge="${deliverySurcharge}">${shipMethodName} - ${deliverySurcharge}</a></li>
													</dsp:oparam>
													<dsp:oparam name="outputEnd">
													</dsp:oparam>
												</dsp:droplet>
											</ul>
										</div>								
									</div>
								</c:if>
								
							
								
								<input type="hidden" name="isLtlItem" value="${ltlProduct}">
								<input type="hidden" name="selServiceLevel" value="" autocomplete="off" />
								<input type="hidden" data-change-store-storeid="storeId" value="" name="storeId" class="addToCartSubmitData"/>
								<input type="hidden" name="parentProductId" value="${productId}" class="addItemToList addItemToRegis" />
								<input type="hidden" value="" class="addToCartSubmitData" name="bts" data-change-store-storeid="bts"/>
								<input type="hidden" value="true" class="addItemToList addItemToRegis" name="fromSearch"/>
								<input type="hidden" value="${LiveClickerOn}" name="lcFlag"/>
								<input type="hidden" value="${CategoryId}" class="categoryId"/>
								<input type="hidden" value="${isMultiRollUpFlag}" class="isMultiRollUpFlag"/>
								<input type="hidden" value="" class="selectedRollUpValue"/>
								<input type="hidden" name="parentProdId" class="_prodId addItemToRegis addItemToList" value="${productId}" />
								<input type="hidden" name="regReturnUrl" class="_prodId addItemToRegis addItemToList" value="/tbs/search/upcresults.jsp?keyword=${searchTerm}&type=upc" />
							</div>
						</div>
						<div class="hide">
							<div class="p-secondary product-list-description">
								<dsp:valueof param="productVO.shortDescription" valueishtml="true"/>
							</div>
							<a class="button secondary" href="${contextPath}${finalUrl}">Learn More</a>
						</div>

					</div>
				</li>
				<c:if test="${count ne totalSize}">
					<hr/>
				</c:if>
		</dsp:oparam>
	</dsp:droplet>
		<c:if test="${not empty searchType && searchType eq 'upc'}">
		<h2 class="divider"></h2>
		<div class="small-12 columns">
			<div class="showing-text small-12 large-3">	
				<dsp:getvalueof var="count" param="browseSearchVO.bbbProducts.bbbProductCount"/> 
				<dsp:getvalueof	var="size" param="browseSearchVO.pagingLinks.pageSize"/> 
				<dsp:getvalueof	var="currentPage" param="browseSearchVO.pagingLinks.currentPage"/>
				<dsp:getvalueof var="pageCount" param="browseSearchVO.pagingLinks.pageCount" />
				<c:set var="lowerRange" value="${1 + (size * (currentPage - 1 ))}"/>
				<c:set var="upperRange" value="${size * currentPage}"/>
				<h3 class="show-for-medium-down">
					<c:choose>
						<c:when test="${count > size}">
							<c:if test="${currentPage eq pageCount}">
								<c:set var="upperRange" value="${count}"/>
							</c:if>
							${lowerRange} - ${upperRange}
						</c:when>
						<c:otherwise>
							<c:out value="${count}" />
						</c:otherwise>
					</c:choose><span class="subheader"><bbbl:label key="lbl_pagination_header_2" language="${pageContext.request.locale.language}" />&nbsp;<dsp:valueof
						param="browseSearchVO.bbbProducts.bbbProductCount" />&nbsp;<bbbl:label key="lbl_pagination_header_4" language="${pageContext.request.locale.language}" />
				</span>
				</h3>
				<h3 class="show-for-large-up"><bbbl:label key="lbl_pagination_header_1" language="${pageContext.request.locale.language}" />
					<c:choose>
						<c:when test="${count > size}">
							<c:if test="${currentPage eq pageCount}">
								<c:set var="upperRange" value="${count}"/>
							</c:if>
							${lowerRange} - ${upperRange}
						</c:when>
						<c:otherwise>
							<c:out value="${count}" />
						</c:otherwise>
					</c:choose><span class="subheader"><bbbl:label key="lbl_pagination_header_2" language="${pageContext.request.locale.language}" />&nbsp;<dsp:valueof
						param="browseSearchVO.bbbProducts.bbbProductCount" />&nbsp;<bbbl:label key="lbl_pagination_header_4" language="${pageContext.request.locale.language}" />
				</span>
				</h3>
						
			</div>
			<div class="small-12 columns large-9">
				<c:if test="${not cmokirschFlag}">
					<dsp:include page="/giftregistry/upc_check_gift_registry.jsp">
					<dsp:param name="ltlCount" value="${ltlCount}"/>
					<dsp:param name="count" value="${count}"/>
					</dsp:include>
			 	</c:if>
				<div class="small-12 large-3 columns addToList"> 
					<c:choose>
						<c:when test="${cmokirschFlag || isItemExcluded || caDisabled}">
							<input type="button" name="btnAddToList" id="btnAddToList" value="Save for Later" disabled="disabled" role="button" aria-pressed="false" class="tiny button expand secondary" onclick="rkg_micropixel('${appid}','wish')" />
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${(currentSiteId ne TBS_BedBathCanadaSite) && not empty productVO && productVO.ltlProduct && (registryIdValue != null || registryIdValue != '')}">
									<input type="button" name="btnAddToList" id="btnAddToList" value="Save for Later" disabled="disabled" role="button" aria-pressed="false" class="tiny button expand secondary" onclick="rkg_micropixel('${appid}','wish')" />
								</c:when>
								<c:otherwise>
									<input type="button" name="btnAddToList" id="btnAddToList" value="Save for Later" role="button" aria-pressed="false" class="tiny button expand secondary" onclick="rkg_micropixel('${appid}','wish')" />
								</c:otherwise>
					     </c:choose>
						</c:otherwise>
					</c:choose>
				</div>
				<div class="small-12 large-3 columns addToRegistry"> 
					 <dsp:include page="/addgiftregistry/add_item_gift_registry.jsp">
					     <c:if test="${(not empty productVO && productVO.ltlProduct) || cmokirschFlag}">
							<dsp:param name="ltlFlag" value="true" />
						</c:if>
						<dsp:param name="isItemExcluded" value="${isItemExcluded}" />
					  </dsp:include>
					  
		   				<input type="hidden" name="parentProdId" class="_prodId addItemToRegis addItemToList" value="${productId}" />
				</div>
				<dsp:getvalueof var="registryIdValue" param="registryId"/>
				
					<c:if test="${not empty cmokirschFlag && cmokirschFlag eq true}">
						<c:set var="isItemExcluded" value="true"></c:set>
					</c:if>
					<c:choose>
						<c:when test="${isItemExcluded || caDisabled}">           
							<div class="small-12 large-3 columns addToCart large-right" >
								<div class="">
									<input type="submit" name="btnAllAddToCart" value="Add to Cart" onclick="rkg_micropixel('${appid}','cart')" role="button" aria-pressed="false"
									<c:choose><c:when test="${inStock==true}">class="tiny button expand transactional"</c:when><c:otherwise>disabled="disabled" class="tiny button expand transactional"</c:otherwise></c:choose> />
								</div>
							</div>
						</c:when>
						<c:otherwise>
							<div class="small-12 large-3 columns addToCart large-right" >
								<div class="">
									<input type="submit" name="btnAllAddToCart" value="Add to Cart" onclick="rkg_micropixel('${appid}','cart')" role="button" aria-pressed="false"
									<c:choose><c:when test="${inStock==true}">class="tiny button expand transactional"</c:when><c:otherwise> class="tiny button expand transactional"</c:otherwise></c:choose> />
								</div>
							</div>
						</c:otherwise>
					</c:choose>
					<dsp:input bean="CartModifierFormHandler.queryParam"
												type="hidden"
												value="keyword=${searchTerm}&type=upc" /> 
							           <dsp:input bean="CartModifierFormHandler.fromPage"  type="hidden" value="productListUPCRwd" />	
				<%-- <dsp:input bean="CartModifierFormHandler.addMultipleItemsToOrderSuccessURL"  type="hidden" value="/tbs/search/upcresults.jsp?keyword=${searchTerm}&type=upc" />
				<dsp:input bean="CartModifierFormHandler.addMultipleItemsToOrderErrorURL"  type="hidden" value="/tbs/search/upcresults.jsp?keyword=${searchTerm}&type=upc" /> --%>
			
			</div>
		</div>
		<hr />
	</c:if>
	</div>
	<input type='submit' class='hide' name='submit' vlaue='submit'/>
	</dsp:form>
	</div>
	<%-- search for registrant modal --%>
    <div id="searchForRegistrant" class="reveal-modal xlarge reg-search" data-reveal></div>
    <c:if test="${not empty searchType && searchType eq 'upc'}">
    <script type="text/javascript">
		$(document).ready(function(){
			$(".nearby-stores").attr("data-reveal-id","nearbyStore");
			$(".nearby-stores").attr("data-reveal-ajax","true");
			$(document).foundation('reflow');
		});
	</script>
	</c:if>
</dsp:page>
