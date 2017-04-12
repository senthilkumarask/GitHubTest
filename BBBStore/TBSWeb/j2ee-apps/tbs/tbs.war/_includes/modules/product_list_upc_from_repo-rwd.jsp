<dsp:page>
<c:choose>
    <c:when test="${minifiedJSFlag}">
        <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/pageJS/min/mie_search.min.js"></script>
    </c:when>
    <c:otherwise>
        <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/pageJS/mie_search.js"></script>
    </c:otherwise>
</c:choose>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
	<dsp:importbean bean="/atg/dynamo/droplet/Range" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/com/bbb/search/droplet/TBSQuickViewDroplet"/>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/com/bbb/selfservice/TBSPDPInventoryDroplet" />
    <dsp:importbean bean="/com/bbb/commerce/droplet/TBSItemExclusionDroplet"/>
    <dsp:importbean bean="/com/bbb/commerce/browse/droplet/SKUDetailDroplet"/>
     <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
     <dsp:importbean bean="/com/bbb/commerce/checkout/droplet/GetApplicableLTLShippingMethodsDroplet" />
	<dsp:getvalueof var="count" param="BBBProductListVO.bbbProductCount" />
	<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductListHandler"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/TBSUPCSearchInventoryDroplet"/>
	<dsp:getvalueof var="CategoryId" param="CatalogRefId" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof param="searchType" var="searchType"/>
	<dsp:getvalueof param="keyword" var="keyword"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
	<c:set var="ltlCount" value="0"/>
	<c:set var="customizeCTACodes">
		<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
	</c:set>
	<c:set var="compareCustomizationText">
		<bbbc:config key="CompareCustomizationText" configName="EXIMKeys"/>
	</c:set>
	<c:set var="AttributePDPTOP">
		<bbbl:label key='lbl_pdp_attributes_top' language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="omniPersonalizeButtonClick">
			<bbbl:label key='omni_personalize_button_click' language="${pageContext.request.locale.language}" />
	    </c:set>
	    
	<c:set var="MIEskusearchList" value ="" />
		
	<c:set var="compareProducts"><bbbc:config key="compareProducts" configName="FlagDrivenFunctions" /></c:set>
	<c:set var="TruckSrc"><bbbc:config key="truck_image_src" configName="LTLConfig_KEYS" /></c:set>
	
	<div class="small-12 columns product-info upcProductDetails">
	<dsp:form action="/tbs/search/upcResultsFromRepo.jsp?keyword=${searchTerm}&type=upc" method="post" id="upcgridForm">
	<div class="listDataItemsWrap registryDataItemsWrap">
	<dsp:droplet name="TBSQuickViewDroplet">
		<dsp:param name="array" param="BBBProductListVO.bbbProducts" />
		<dsp:oparam name="outputStart">
			<%-- Add three categorized ul list in which searched sku are going to render --%>
			<ul class="small-block-grid-1 plpListGridToggle OrderableSku hidden"></ul>
			<ul class="small-block-grid-1 plpListGridToggle PersonalizedSku hidden"></ul>
			<ul class="small-block-grid-1 plpListGridToggle OutOfStockSku hidden">
				<bbbt:textArea key='txt_mie_outOfStock_sku_header' language="${pageContext.request.locale.language}"/>
			</ul>
			<c:set var="disableCTA" value="true"></c:set>
			<c:set var="orderableSkuCount" value="0"></c:set>
			
			<script type="text/javascript">
				//initialize the variable inside function
				MIESearchEnhancement();
			</script>
		</dsp:oparam>
		<dsp:oparam name="outputEnd">
		</dsp:oparam>
		<dsp:getvalueof param="size" var="totalSize"/>
		<dsp:oparam name="output">
			<dsp:getvalueof param="count" var="currentCount"/>
			<dsp:getvalueof var="productId" param="productVO.productID" />

			<dsp:getvalueof var="previousProductId" param="productVO.previousProductId" />
			<dsp:getvalueof var="nextProductId" param="productVO.nextProductId" />
			<dsp:getvalueof var="ltlProduct" param="productVO.ltlProduct" />
			<dsp:getvalueof var="cmokirschFlag" param="productVO.cmokirschFlag" />
			<dsp:getvalueof var="skuid" param="productVO.skuId"/>
			<c:choose>
			<c:when test="${not empty MIEskusearchList}">
		     	<c:set var="MIEskusearchList" value ="${MIEskusearchList},${skuid}" />
			</c:when>
			<c:otherwise>
			   <c:set var="MIEskusearchList" value ="${skuid}" />
			</c:otherwise>
			</c:choose>
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
					<dsp:getvalueof var="isCustomizationRequired" value=""/>
					<dsp:getvalueof var="customizationOffered" value=""/>
					<dsp:getvalueof var="personalizationType" value=""/>
					<dsp:getvalueof value="" var="vdcSku"/>
					<dsp:getvalueof value="" var="ltlItem"/>
					<dsp:droplet name="SKUDetailDroplet">
						<dsp:param name="skuId" value="${skuid}"/>
						<dsp:oparam name="output">
						 <dsp:getvalueof var="skuVO" param="pSKUDetailVO" />
						 <c:choose>
							<c:when test="${not empty skuVO}">
								<dsp:getvalueof var="attribs" param="pSKUDetailVO.skuAttributes"/>
							</c:when>
							<c:otherwise>
								<dsp:getvalueof var="attribs" param="productVO.attributesList"/>
							</c:otherwise>
						</c:choose>
							<dsp:getvalueof var="isCustomizationRequired" param="pSKUDetailVO.customizableRequired"/>
							<dsp:getvalueof var="customizationOffered" param="pSKUDetailVO.customizationOffered"/>
							<dsp:getvalueof var="personalizationType" param="pSKUDetailVO.personalizationType"/>
							<dsp:getvalueof param="pSKUDetailVO.vdcSku" var="vdcSku"/>
							<dsp:getvalueof param="pSKUDetailVO.ltlItem" var="ltlItem"/>
							<dsp:getvalueof param="pSKUDetailVO.inCartFlag" var="skuInCartFlag"/>
							<dsp:getvalueof param="pSKUDetailVO.customizableCodes" var="customizableCodes"/>
							<c:if test="${not isCustomizationRequired}">
								<c:set var="disableCTA" value="false"></c:set>
							</c:if>
							</dsp:oparam>
						</dsp:droplet>
				
						<dsp:getvalueof var="seoUrl" param="productVO.seoUrl"/>
							<input type="hidden" name="isCustomizationRequired" value="${isCustomizationRequired}"/>
							<input type="hidden" name="customizationOffered" value="${customizationOffered}"/>
							<input type="hidden" name="personalizationType" value="${personalizationType}"/>
						<c:choose>
							<c:when test="${empty seoUrl}">
								<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
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
						<c:choose>
								<c:when test="${not empty customizableCodes && fn:contains(customizeCTACodes, customizableCodes)}">
									<c:set var="customizeTxt" value="true"/>
								</c:when>
								<c:otherwise>
									<c:set var="customizeTxt" value="false"/>
								</c:otherwise>
							</c:choose>
						<c:if test="${enableKatoriFlag && isCustomizationRequired}">
							<div class= "personalizedPDPContainer container_12">
								<jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request" />
								<c:set target="${placeHolderMap}" property="contextPath" value="${contextPath}" />
								<c:set target="${placeHolderMap}" property="finalUrl" value="${finalUrl}" />
								<c:set target="${placeHolderMap}" property="skuid" value="${skuid}" />
									<h3 class= "personaliseMsg">
									<c:choose>
								 		<c:when test="${customizeTxt eq true}">
								 			<bbbt:textArea key="txt_customization_required_upc" placeHolderMap="${placeHolderMap}" language="${pageContext.request.locale.language}" />
								 		</c:when>
								 		<c:otherwise>
								 			<bbbt:textArea key="txt_personalization_required_upc" placeHolderMap="${placeHolderMap}" language="${pageContext.request.locale.language}" />
								 		</c:otherwise>
								 	</c:choose>
									</h3>
							</div>
						</c:if>
						
						<%-- HYD-113 --%>
						<c:if test="${enableKatoriFlag && personalizationType eq 'PY'}">
	                           <div class= "personalizedPDPContainer container_12">
	                           <jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request" />
	                           <c:set target="${placeHolderMap}" property="contextPath" value="${contextPath}" />
	                           <c:set target="${placeHolderMap}" property="finalUrl" value="${finalUrl}" />
	                           <c:set target="${placeHolderMap}" property="skuid" value="${skuid}" />
	                           <h3 class= "personaliseMsg">
	                           		<c:choose>
								 		<c:when test="${customizeTxt eq true}">
								 			<bbbt:textArea key="txt_customization_PY_upc" placeHolderMap="${placeHolderMap}" language="${pageContext.request.locale.language}" />
								 		</c:when>
								 		<c:otherwise>
								 			<bbbt:textArea key="txt_personalization_PY_upc" placeHolderMap="${placeHolderMap}" language="${pageContext.request.locale.language}" />
								 		</c:otherwise>
								 	</c:choose>
	                           </h3>
	                           </div>
                         </c:if>
						
						
						<div class="small-12 columns large-5">
					
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
												
							<c:set var="lblPriceTBD"><bbbl:label key='lbl_price_is_tbd_tbs' language="${pageContext.request.locale.language}"/></c:set>	
							<div class="prodAttribs prodAttribWrapper">
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array" value="${attribs}"/>
								<dsp:oparam name="output">
								<dsp:getvalueof var="placeHolderTop" param="key"/>
									<c:if test="${(not empty placeHolderTop) && (placeHolderTop eq AttributePDPTOP)}">
										<dsp:droplet name="/atg/dynamo/droplet/ForEach">
											<dsp:param param="element" name="array" />
											<dsp:param name="sortProperties" value="+priority"/>
												<dsp:oparam name="output">
												<dsp:getvalueof var="placeHolderTop" param="element.placeHolder"/>
												<dsp:getvalueof var="attributeDescripTop" param="element.attributeDescrip"/>
												<dsp:getvalueof var="imageURLTop" param="element.imageURL"/>
												<dsp:getvalueof var="actionURLTop" param="element.actionURL"/>
												<c:if test="${not empty compareCustomizationText && not empty attributeDescripTop && fn:contains(attributeDescripTop, compareCustomizationText)}">
													<c:set var="lblPriceTBD"><bbbl:label key="lbl_price_is_tbd_customize" language="${pageContext.request.locale.language}"/></c:set>
												</c:if>
												<div>
																<c:choose>
																     <c:when test="${null ne attributeDescripTop}">
																            <c:choose>
																                   <c:when test="${null ne imageURLTop}">
																                         <img src="${imageURLTop}" alt="" /><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span>${element.attributeDescrip}
																                   </c:when>
																                   <c:otherwise>
																                         <c:choose>
																                                 <c:when test="${null ne actionURLTop}">
																                                       <a href="${actionURLTop}" class="newOrPopup"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a>
																                                 </c:when>
																                                 <c:otherwise>
																                                       <span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span>
																                                 </c:otherwise>
																                          </c:choose>
																                   </c:otherwise>
																             </c:choose>
																     </c:when>
																     <c:otherwise>
																           <c:if test="${null ne imageURLTop}">
																                   <c:choose>
																                          <c:when test="${null ne actionURLTop}">
																                                <a href="${actionURLTop}" class="newOrPopup"><img src="${imageURLTop}" alt="" /></a>
																                          </c:when>
																                          <c:otherwise>
																                                <img src="${imageURLTop}" alt="" />
																                          </c:otherwise>
																                   </c:choose>
																            </c:if>
																     </c:otherwise>
																</c:choose>
												</div>
												</dsp:oparam>
										</dsp:droplet>
									</c:if>
								</dsp:oparam>
								</dsp:droplet>
							</div>
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
				

							<c:if test="${swatchFlag == 'true'}">
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
											<c:when test="${rollupFlag eq 'true' || collectionFlag == 'true'}">
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

							<!-- BBBH-2889 TBS incart price dispaly. Changed the existing logic to display price because of number format exception on upc search -->
							<div class="price product-info large-right small-12 columns large-2">
							<c:set var="TBS_BedBathUSSite">
								<bbbc:config key="TBS_BedBathUSSiteCode"
									configName="ContentCatalogKeys" />
							</c:set>
							<c:set var="TBS_BuyBuyBabySite">
								<bbbc:config key="TBS_BuyBuyBabySiteCode"
									configName="ContentCatalogKeys" />
							</c:set>
							<c:set var="TBS_BedBathCanadaSite">
								<bbbc:config key="TBS_BedBathCanadaSiteCode"
									configName="ContentCatalogKeys" />
							</c:set>
							<c:choose>
								<c:when test="${currentSiteId eq TBS_BedBathUSSite || currentSiteId eq TBS_BuyBuyBabySite}">
									<c:set var="incartPriceList"><bbbc:config key="BedBathUSInCartPriceList" configName="ContentCatalogKeys" /></c:set>
								</c:when>
								<c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
									<c:set var="incartPriceList"><bbbc:config key="BedBathCanadaInCartPriceList" configName="ContentCatalogKeys" /></c:set>
								</c:when>
						    </c:choose>
							 
							 <%-- The first call to price droplet is going to get the price from the profile's list price or 
								   the default price list --%>
								  
										  <dsp:droplet name="PriceDroplet">
											<dsp:param name="product" param="productVO.productID"/>
											<dsp:param name="sku" param="productVO.skuId"/>
											<dsp:oparam name="output">
											  <dsp:setvalue param="theListPrice" paramvalue="price"/>
											  <%-- The second call is in case the incart price exists --%>
											<c:if test="${skuInCartFlag}">
												<dsp:droplet name="PriceDroplet">
													<dsp:param name="product" param="productVO.productID" />
													<dsp:param name="sku" param="productVO.skuId" />
													<dsp:param name="priceList" value="${incartPriceList}" />
													<dsp:oparam name="output">
														<dsp:getvalueof var="inCartPrice" vartype="java.lang.Double"
															param="price.listPrice" />
													</dsp:oparam>
												</dsp:droplet>
											</c:if>

										<%-- The third call is in case the sale price exists --%>
										<dsp:getvalueof var="profileSalePriceList"
											bean="Profile.salePriceList" />
										<c:choose>
											<c:when test="${not empty profileSalePriceList}">
												<dsp:droplet name="PriceDroplet">
													<dsp:param name="priceList" value="${profileSalePriceList}" />
													<dsp:oparam name="output">

														<c:choose>
															<c:when test="${skuInCartFlag}">
																<c:set var="listPrice" value="${inCartPrice}" />
															</c:when>
															<c:otherwise>
																<dsp:getvalueof var="listPrice" vartype="java.lang.Double"
																	param="price.listPrice" />
															</c:otherwise>
														</c:choose>
														
														<c:choose>
																<c:when test="${listPrice<=0.01}">
																	<h3 class="isPrice price priceTbdCat">${lblPriceTBD}</h3>
																</c:when>
																<c:otherwise>
																	<h3 class="price-sale no-margin-bottom">
																	<dsp:include page="/global/gadgets/formattedPrice.jsp">
																		<dsp:param name="price" value="${listPrice}" />
																	</dsp:include>
																	</h3>
																	
																	<h3 class="price-original no-padding-left">
																		<dsp:getvalueof var="price" vartype="java.lang.Double"
																			param="theListPrice.listPrice" />
																			<dsp:include page="/global/gadgets/formattedPrice.jsp">
																				<dsp:param name="price" value="${price}" />
																			</dsp:include>
																	</h3>
																</c:otherwise>
														</c:choose>
													</dsp:oparam>
													<dsp:oparam name="empty">
														<dsp:getvalueof var="price" vartype="java.lang.Double"
																			param="theListPrice.listPrice" />
														<c:choose>
														<c:when test="${price<=0.01}">
																<h3 class="isPrice price priceTbdCat">${lblPriceTBD}</h3>
														</c:when>
														<c:otherwise>
															<c:choose>
															<c:when test="${skuInCartFlag}">
																<c:set var="price" value="${inCartPrice}" />
																<h3 class="isPrice price">
																	<dsp:include page="/global/gadgets/formattedPrice.jsp">
																				<dsp:param name="price" value="${price}" />
																	</dsp:include>
																</h3>
															</c:when>
															<c:otherwise>
																<h3 class="isPrice price">
																	<dsp:include page="/global/gadgets/formattedPrice.jsp">
																				<dsp:param name="price" value="${price}" />
																	</dsp:include>
																</h3>
															</c:otherwise>
														</c:choose>
														</c:otherwise>
														</c:choose>
													</dsp:oparam>
												</dsp:droplet>
												<%-- End price droplet on sale price --%>
											</c:when>
											<c:otherwise>
												<dsp:getvalueof var="price" vartype="java.lang.Double"
																			param="theListPrice.listPrice" />
														<c:choose>
														<c:when test="${price<=0.01}">
																<h3 class="isPrice price priceTbdCat">${lblPriceTBD}</h3>
														</c:when>
														<c:otherwise>
															<c:choose>
															<c:when test="${skuInCartFlag}">
																<c:set var="price" value="${inCartPrice}" />
																<h3 class="isPrice price">
																	<dsp:include page="/global/gadgets/formattedPrice.jsp">
																				<dsp:param name="price" value="${price}" />
																	</dsp:include>
																</h3>
															</c:when>
															<c:otherwise>
																<h3 class="isPrice price">
																	<dsp:include page="/global/gadgets/formattedPrice.jsp">
																				<dsp:param name="price" value="${price}" />
																	</dsp:include>
																</h3>
															</c:otherwise>
														</c:choose>
														</c:otherwise>
														</c:choose>
											</c:otherwise>
										</c:choose>
										<%-- End Is Empty Check --%>
											</dsp:oparam>
										  </dsp:droplet><%-- End Price Droplet --%>
							 
							 
							 
							 
							</div>
							<!-- BBBH-2889 end -->
							<div class="small-12 columns large-10 no-padding-left">
								<c:if test="${not empty searchType && searchType eq 'upc'}">
									<div class="prodQty fl">
										<div class="small-12 large-4 large-right columns quantity no-padding-left">
											<dsp:getvalueof var="skuid" param="productVO.skuId"/>
											<dsp:getvalueof var="productID" param="productVO.productID"/>
											<dsp:getvalueof param="productVO.priceRange" var="omniPrice"/>
											<input type="hidden" name="prodId" class="_prodId addItemToRegis productId addItemToList" value="${productID}" data-change-store-submit="prodId" data-change-store-errors="required" data-change-store-internalData="true"/>
											<input type="hidden" name="skuId" value="${skuid}" class="addItemToRegis _skuId addItemToList changeStoreSkuId" data-change-store-submit="skuId" data-change-store-errors="required" data-change-store-internalData="true"/>		
											<dsp:input bean="CartModifierFormHandler.productIds" type="hidden" paramvalue="productVO.productID" />
											<dsp:input bean="CartModifierFormHandler.catalogRefIds"  type="hidden" paramvalue="productVO.skuId" />
											<input type="hidden" name="price" value="${price}" class="addItemToList addItemToRegis" />
											<input type="hidden" name="pagetype" value="MIE" />
											<div class="qty-spinner">
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
									<div class="small-12 product-other-links availability large-right">
												<c:if test="${not ltlItem && vdcSku ne true}">
													<dsp:droplet name="TBSPDPInventoryDroplet">
														<dsp:param name="sku" value="${skuid}" />
														<dsp:param name="siteId" value="${appid}" />
														<dsp:oparam name="output">
															<dsp:getvalueof param="timeframe" var="timeframe"></dsp:getvalueof>
															<dsp:getvalueof param="nearbyStore" var="nearbyStore"></dsp:getvalueof>
															<dsp:getvalueof param="nearbyStoreLink" var="nearbyStoreLink"></dsp:getvalueof>
															<dsp:getvalueof param="currentStoreQty" var="currentStoreQty"></dsp:getvalueof>
															<div class="columns no-padding">
															
																<a class="pdp-sprite in-store <c:if test='${currentStoreQty < 1}'> no-stock noline</c:if>" <c:if test="${currentStoreQty < 1}">disabled='disabled'</c:if>>
																	${currentStoreQty} In Store
																</a>

															<a href="/tbs/browse/shipping_policies.jsp" class="pdp-sprite online popupShipping marLeft_20 <c:if test="${timeframe eq '0004'}"> no-stock</c:if>" <c:if test="${timeframe eq 0004}">disabled='disabled'</c:if>>
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
															 	<dsp:include page="/common/holidayMessaging.jsp">
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
																		<span class="pdp-sprite nearby-stores no-padding-right no-stock" disabled="disabled" />
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
												</c:if>
												 <c:if test="${vdcSku eq true}">
													<dsp:droplet name="/com/bbb/commerce/browse/VDCShippingMessagingDroplet">
														<dsp:param name="skuId" value="${skuid}"/>
														<dsp:param name="siteId" value="${currentSiteId}"/>
														<dsp:param name="fromShippingPage" value="true"/>
														<dsp:oparam name="output">
															<dsp:getvalueof var="vdcDelTime" param="vdcShipMsg"/>
															<dsp:getvalueof var="offsetDateVDC" param="offsetDateVDC"/>
															<c:set var="vdcOffsetFlag">
																<bbbc:config key="vdcOffsetFlag" configName="FlagDrivenFunctions" />
															</c:set>
														</dsp:oparam>
														<dsp:oparam name="error">
														</dsp:oparam>
													</dsp:droplet>
													<div class="small-6 large-7 columns shipping">
														<c:if test="${not ltlItem && vdcOffsetFlag && not empty offsetDateVDC}">
															<jsp:useBean id="placeHolderMapServiceLevel" class="java.util.HashMap" scope="request" />
															<c:set target="${placeHolderMapServiceLevel}" property="actualOffSetDate" value="${offsetDateVDC}" />
															<jsp:useBean id="placeHolderMapVdcLearnMore" class="java.util.HashMap" scope="request" />
															<c:set target="${placeHolderMapVdcLearnMore}" property="shipMethod" value="" />
															<c:set target="${placeHolderMapVdcLearnMore}" property="skuId" value="${skuid}" />
															<ul><li>
																	<span class="highlightRed vdcOffsetMsg bold">
																<bbbt:textArea key="txt_vdc_offset_msg" placeHolderMap="${placeHolderMapServiceLevel}" language="${pageContext.request.locale.language}" />
																<c:set var="lbl_offset_learn_more_link" scope="request"><bbbl:label key="lbl_offset_learn_more" placeHolderMap="${placeHolderMapVdcLearnMore}" language="<c:out   param='${language}'/>"/></c:set>
																<c:set var="lbl_offset_learn_moreTBS" value="${fn:replace(lbl_offset_learn_more_link, '/store/includes','/tbs/includes')}" />
																<dsp:valueof value="${lbl_offset_learn_moreTBS}" valueishtml="true"/>
																	</span>
															</li></ul>
														</c:if>
														<c:if test="${not empty vdcDelTime }">
															<a href="/tbs/browse/dynamicStaticContent.jsp?pageName=ItemShippedDirectlyFromVendor&isVdcSku=${vdcSku}&skuId=${skuid}" class="pdp-sprite online popupShipping <c:if test="${timeframe eq '0004'}"> no-stock</c:if>" <c:if test="${timeframe eq 0004}">disabled='disabled'</c:if>>
																<bbbl:label key="lbl_vdc_arrives_in" language="${pageContext.request.locale.language}" /> ${vdcDelTime}
															</a>
														</c:if>
													</div>
												</c:if>
												</div>
										 <dsp:input bean="CartModifierFormHandler.queryParam"
												type="hidden"
												value="keyword=${searchTerm}&type=upc" /> 
							           <dsp:input bean="CartModifierFormHandler.fromPage"  type="hidden" value="productListUPCRepo" />		
									<%-- <dsp:input bean="CartModifierFormHandler.addItemToOrderSuccessURL"  type="hidden" value="/tbs/search/upcResultsFromRepo.jsp?keyword=${searchTerm}&type=upc"/>
									<dsp:input bean="CartModifierFormHandler.addItemToOrderErrorURL"  type="hidden" value="/tbs/search/upcResultsFromRepo.jsp?keyword=${searchTerm}&type=upc" />
 --%>
								</c:if>
							
	              <c:if test="${appid ne TBS_BedBathCanadaSite && ltlProduct}">
		                 <dsp:getvalueof param="productVO.skuId" var="skuId"/> 
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
								<input type="hidden" name="regReturnUrl" class="_prodId addItemToRegis addItemToList" value="/tbs/search/upcResultsFromRepo.jsp?keyword=${searchTerm}&type=upc" />
							</div>
						</div>
						<div class="hide">
							<div class="p-secondary product-list-description">
								<dsp:valueof param="productVO.shortDescription" valueishtml="true"/>
							</div>
							<a class="button secondary" href="${contextPath}${finalUrl}">Learn More</a>
						</div>

					</div>
					<dsp:droplet name="TBSUPCSearchInventoryDroplet">
					<dsp:param name="siteId" value="${appid}"/>
					<dsp:param name="upcItems" value="${skuid}"/>
					<dsp:oparam name="output">
						<dsp:getvalueof var="inStock" param="inStock"/>
					</dsp:oparam>
				</dsp:droplet>
				
				<dsp:droplet name="TBSItemExclusionDroplet">
					<dsp:param name="siteId" value="${appid}"/>
					<dsp:param name="upcItems" value="${skuid}"/>
					<dsp:oparam name="output">
					<c:set var="reasonCode" value="" />
					<c:set var="isItemExcluded" value="${false}" />
					
					<dsp:getvalueof var="isItemExcluded" param="validItemExcludedSku"/>
					<dsp:getvalueof var="caDisabled" param="caDisabled"/>
					<dsp:getvalueof var="reasonCode" param="reasonCode"/>
						<c:if test="${isItemExcluded || caDisabled }">           
						       
							<div class="small-12 columns product-other-links availability" style="color:red;font-weight:bold;float:right;width:auto;">
                            	<dsp:include page="/browse/frag/reasonCodeMessage.jsp" flush="true" >
									<dsp:param name="reasonCode" value="${reasonCode}"/>
								</dsp:include>
                            </div>
						</c:if>
						
						
					</dsp:oparam>
				</dsp:droplet>
				</li>
				<c:choose>
					<c:when test="${isCustomizationRequired }">
						<script type="text/javascript">
							//add the currently rendered sku to PersonalizedSku list
							addPersonalizedSkuToPersonalizedCategory();
						</script>
					</c:when>
					<c:when test="${!isCustomizationRequired && !isItemExcluded && !caDisabled && inStock}">
						<c:set var="orderableSkuCount" value="${orderableSkuCount+1 }"></c:set>
						<script type="text/javascript">
							//add the currently rendered sku to OrderableSku list
							addOrderableSkuToOrderableCategory();
						</script>
					</c:when>
					<c:otherwise>
						<script type="text/javascript">
							//add the currently rendered out of stock sku to OutOfStockSku list
							addOutOfStockSkuToOutOfStockCategory();
						</script>
					</c:otherwise>
				</c:choose>
		</dsp:oparam>
	</dsp:droplet>
		<c:if test="${not empty searchType && searchType eq 'upc'}">
		<c:if test="${orderableSkuCount > 0  }">
		<div id="addToCartRegistryButton" class="small-12 columns hidden">
			<div class="showing-text small-12 large-3">
				<h3 class="show-for-medium-down">
					<span class="subheader"><bbbl:label key="lbl_pagination_header_2" language="${pageContext.request.locale.language}" />&nbsp;2&nbsp;<bbbl:label key="lbl_pagination_header_4" language="${pageContext.request.locale.language}" />
				</span>
				</h3>
				<h3 class="show-for-large-up"><bbbl:label key="lbl_pagination_header_1" language="${pageContext.request.locale.language}" />&nbsp;<c:out value="${orderableSkuCount }"/>
					<span class="subheader"><bbbl:label key="lbl_pagination_header_2" language="${pageContext.request.locale.language}" />&nbsp;<c:out value="${orderableSkuCount }"/>&nbsp;<bbbl:label key="lbl_pagination_header_4" language="${pageContext.request.locale.language}" />
				</span>
				</h3>
						
			</div>
			<div class="small-12 columns large-9">
				<c:if test="${not cmokirschFlag}">
					<dsp:include page="/giftregistry/upc_check_gift_registry.jsp">
					<dsp:param name="ltlCount" value="${ltlCount}"/>
					<dsp:param name="count" value="${count}"/>
					<dsp:param name="disableCTA" value="${disableCTA}" />
					</dsp:include>
			 	</c:if>
				
				<div class="small-12 large-3 columns addToList <c:if test="${not upcGiftRegistryEnabled}"> large-offset-3</c:if>"> 
				
					<c:choose>
						<c:when test="${cmokirschFlag}">
							<input type="button" name="btnAddToList" id="btnAddToList" value="Save for Later" disabled="disabled" role="button" aria-pressed="false" class="tiny button expand secondary" onclick="rkg_micropixel('${appid}','wish')" />
						</c:when>
						<c:otherwise>
							<c:choose>
							
								<c:when test="${(currentSiteId ne TBS_BedBathCanadaSite) && not empty productVO && productVO.ltlProduct && (registryIdValue != null || registryIdValue != '') || disableCTA}">
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
						
						<dsp:param name="disableCTA" value="${disableCTA}" />
						 
						
					  </dsp:include>
		   				<input type="hidden" name="parentProdId" class="_prodId addItemToRegis addItemToList" value="${productId}" />
				</div>
				
				<dsp:getvalueof var="registryIdValue" param="registryId"/>
				
				<c:if test="${not empty cmokirschFlag && cmokirschFlag eq true}">
					<c:set var="isItemExcluded" value="true"></c:set>
				</c:if>
				<div class="small-12 large-3 columns addToCart large-right" >
					<div class="">
						<input type="submit" name="btnAllAddToCart" value="Add to Cart" onclick="rkg_micropixel('${appid}','cart')" role="button" aria-pressed="false"
							class="tiny button expand transactional" />
					</div>
				</div>
				
				 <dsp:input bean="CartModifierFormHandler.queryParam"
												type="hidden"
												value="keyword=${searchTerm}&type=upc" /> 
							           <dsp:input bean="CartModifierFormHandler.fromPage"  type="hidden" value="productListUPCRepo" />
				<%-- <dsp:input bean="CartModifierFormHandler.addMultipleItemsToOrderSuccessURL"  type="hidden" value="/tbs/search/upcResultsFromRepo.jsp?keyword=${searchTerm}&type=upc" />
				<dsp:input bean="CartModifierFormHandler.addMultipleItemsToOrderErrorURL"  type="hidden" value="/tbs/search/upcResultsFromRepo.jsp?keyword=${searchTerm}&type=upc" /> --%>
			
			</div>
		</div>
		</c:if>
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
