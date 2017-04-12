	<dsp:page>
	 	 <c:set var="localStorePDP">
            <bbbc:config key="LOCAL_STORE_PDP_FLAG" configName="FlagDrivenFunctions" />
         </c:set> 
		<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
		<dsp:importbean	bean="/com/bbb/profile/session/SessionBean"/>
		<dsp:importbean bean="/atg/multisite/Site"/>
		<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BreadcrumbDroplet"/>
        <dsp:importbean bean="/com/bbb/commerce/checkout/droplet/GetApplicableLTLShippingMethodsDroplet" />
		<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BreadcrumbDroplet"/>	
		<dsp:importbean bean="/atg/userprofiling/Profile"/>
		<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductListHandler"/>
		<dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
		
		<dsp:getvalueof var="siteId" bean="Site.id" />
		<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
		<dsp:getvalueof var="categoryId" param="categoryId"/>
		<c:set var="lblForThe"><bbbl:label key="lbl_accessibility_for_the" language="${pageContext.request.locale.language}" /></c:set>
		<c:set var="lblSflReviews"><bbbl:label key="lbl_sfl_reviews" language="${pageContext.request.locale.language}" /></c:set>
		<c:set var="lblOfThe"><bbbl:label key="lbl_accessibility_of_the" language="${pageContext.request.locale.language}" /></c:set>
		<c:set var="lblAboutThe"><bbbl:label key="lbl_accessibility_about_the" language="${pageContext.request.locale.language}" /></c:set>
		<c:set var="lblSaveThe"><bbbl:label key="lbl_accessibility_save_the" language="${pageContext.request.locale.language}" /></c:set>
		<c:set var="lblFuturePurchase"><bbbl:label key="lbl_accessibility_future_purchase" language="${pageContext.request.locale.language}" /></c:set>
		<c:set var="lblFindThe"><bbbl:label key="lbl_accessibility_find_the" language="${pageContext.request.locale.language}" /></c:set>
		<c:set var="lblStoreNear"><bbbl:label key="lbl_accessibility_store_near_you" language="${pageContext.request.locale.language}" /></c:set>


		<c:set var="shippingAttributesList">
			<bbbc:config key="shippingAttributesList" configName="ContentCatalogKeys" />
		</c:set>
		<c:set var="customizeCTACodes" scope="request">
			<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys" />
		</c:set>
		<c:set var="showShipCustomMsg" value="true"/>
		<c:set var="scene7Path">
			<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
		</c:set>
		<c:set var="BedBathUSSite" scope="request">
			<bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
		</c:set>
		<c:set var="BuyBuyBabySite" scope="request">
			<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
		</c:set>
		<%-- BPSI-2446 DSK VDC message & offset message changes --%>
		<c:set var="vdcAttributesList">
		<bbbc:config key="vdcAttributesList" configName="ContentCatalogKeys" />
		</c:set>
		<%-- BPSI-2446 DSK VDC message & offset message changes --%>
		<c:set var="BedBathCanadaSite" scope="request">
			<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
		</c:set>
		<c:set var="LTLToolTip">
			<bbbl:label key='ltl_tool_tip' language="${pageContext.request.locale.language}" />
		</c:set>
		<c:set var="omniPersonalizeButtonClick">
			<bbbl:label key='omni_personalize_button_click' language="${pageContext.request.locale.language}" />
		</c:set>
		<c:set var="AttributePDPTOP">
			<bbbl:label key='lbl_pdp_attributes_top' language="${pageContext.request.locale.language}" />
		</c:set>
		<c:set var="AttributePDPMiddle">
			<bbbl:label key='lbl_pdp_attributes_middle' language="${pageContext.request.locale.language}" />
		</c:set>
		<c:set var="AttributePDPPrice">
			<bbbl:label key='lbl_pdp_attributes_price' language="${pageContext.request.locale.language}" />
		</c:set>
		<c:set var="count" value="1" />
        <c:set var="countOmni" value="1" />
		<%-- R2.2 Story - 178-a4 Product Comparison Page --%>
		<dsp:getvalueof var="frmComparisonPage" param="prodToCompare"/>
		<c:set var="SchemeName" value="pdp_cav;pdp_fbw"/>
		<c:set var="omniProp5" value="Product modal"/>
		<c:set var="productNotfound" value="false"/>
		<c:set var="productOOS" value="false"/>
		<c:choose>
			<c:when test="${currentSiteId eq BedBathUSSite}">
				<c:set var="MapQuestOn" scope="request">
					<tpsw:switch tagName="MapQuestTag_us" />
				</c:set>
				<c:set var="OutOfStockOn" scope="request">
					<tpsw:switch tagName="OutOfStockTag_us"/>
				</c:set>
			</c:when>
			<c:when test="${currentSiteId eq BuyBuyBabySite}">
				<c:set var="MapQuestOn" scope="request">
					<tpsw:switch tagName="MapQuestTag_baby" />
				</c:set>
				<c:set var="OutOfStockOn" scope="request">
					<tpsw:switch tagName="OutOfStockTag_baby"/>
				</c:set>
			</c:when>
			<c:otherwise>
				<c:set var="MapQuestOn" scope="request">
					<tpsw:switch tagName="MapQuestTag_ca" />
				</c:set>
				<c:set var="OutOfStockOn" scope="request">
					<tpsw:switch tagName="OutOfStockTag_ca"/>
				</c:set>
			</c:otherwise>
		</c:choose>
		<c:choose>
			<c:when test="${currentSiteId eq BedBathUSSite}">
				<c:set var="BazaarVoiceOn" scope="request">
					<tpsw:switch tagName="BazaarVoiceTag_us" />
				</c:set>
			</c:when>
			<c:when test="${currentSiteId eq BuyBuyBabySite}">
				<c:set var="BazaarVoiceOn" scope="request">
					<tpsw:switch tagName="BazaarVoiceTag_baby" />
				</c:set>
			</c:when>
			<c:when test="${currentSiteId eq BedBathCanadaSite}">
				<c:set var="BazaarVoiceOn" scope="request">
					<tpsw:switch tagName="BazaarVoiceTag_ca" />
				</c:set>
			</c:when>
	
		</c:choose>
		<c:choose>
			<c:when test="${currentSiteId eq BedBathUSSite}">
				<c:set var="SceneSevenOn" scope="request">
                	<tpsw:switch tagName="SceneSevenTag_us"/>
				</c:set>
			</c:when>
			<c:when test="${currentSiteId eq BuyBuyBabySite}">
				<c:set var="SceneSevenOn" scope="request">
					<tpsw:switch tagName="SceneSevenTag_baby"/>
				</c:set>
			</c:when>
			<c:when test="${currentSiteId eq BedBathCanadaSite}">
				<c:set var="SceneSevenOn" scope="request">
					<tpsw:switch tagName="SceneSevenTag_ca"/>
				</c:set>
			</c:when>
		</c:choose>

		<c:set var="scene7DomainPath">
			<bbbc:config key="BBBSceneSevenDomainPath" configName="ThirdPartyURLs" />
		</c:set>
		
		<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet" />
		<dsp:importbean bean="/atg/multisite/Site" />
		<dsp:getvalueof var="appid" bean="Site.id" />
		<c:set var="enableKatoriFlag" scope="request"><bbbc:config key="enableKatori" configName="EXIMKeys"/></c:set>
		<input type="hidden" id="enableKatoriFlag" name="enableKatoriFlag" value="${enableKatoriFlag}">
		<c:set var="enableUGCQV" scope="request"><bbbc:config key="enableUGCQV" configName="FlagDrivenFunctions"/></c:set>
		<input type="hidden" id="enableUGCQV" name="enableUGCQV" value="${enableUGCQV}">
		<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${appid}"/>
		<dsp:getvalueof var="isMultiRollUpFlag" param="isMultiRollUpFlag" />
		<c:choose>
			<c:when test="${isMultiRollUpFlag eq 'true'}">
			<dsp:getvalueof var="color" value="${fn:escapeXml(param.selectedRollUpValue)}"/>
			</c:when>
			<c:otherwise>
				<dsp:getvalueof var="skuId" value="${fn:escapeXml(param.selectedRollUpValue)}"/>
				<dsp:getvalueof var="color" value="${fn:escapeXml(param.selectedRollUpValue)}"/>
			</c:otherwise>										
		</c:choose>
		<c:if test="${empty skuId}">
		<dsp:getvalueof var="skuId" value="${fn:escapeXml(param.skuId)}"/>
		<dsp:getvalueof var="parentProductId" param="parentProductId" />
		<dsp:getvalueof var="strategyName" param="strategyName" />
		</c:if>
		<dsp:droplet name="ProductDetailDroplet">
			<dsp:param name="siteId" value="${appid}" />
			<dsp:param name="id" param="prodId" />
			<dsp:param name="skuId" value="${skuId}" />
			<dsp:param name="color" value="${color}" />
			<dsp:param name="poc" value="${parentProductId}" />
			<dsp:oparam name="output">
	            <dsp:getvalueof var="skuVO" param="pSKUDetailVO" />
				<dsp:getvalueof var="bopusAllowed" param="pSKUDetailVO.bopusAllowed"/>		
				<dsp:getvalueof var="giftFlag" param="productVO.giftCertProduct" />
				<dsp:getvalueof var="prodId" param="prodId" />
				<dsp:getvalueof var="skuId" param="skuId" />
				<dsp:getvalueof var="productVO" param="productVO" />
				<dsp:getvalueof var="isCustomizationRequired" param="pSKUDetailVO.customizableRequired"/>				
				<dsp:getvalueof var="isIntlRestricted" param="productVO.intlRestricted"/>
				<dsp:getvalueof var="largeImage" param="productVO.productImages.largeImage" />
				<dsp:getvalueof var="smallImage" param="productVO.productImages.smallImage" />
				<dsp:getvalueof var="basicImage" param="productVO.productImages.basicImage" />
				<dsp:getvalueof var="thumbnailImage" param="productVO.productImages.thumbnailImage" />
				<dsp:getvalueof var="productName" param="productVO.name" scope="request" />
				<dsp:getvalueof var="refinedNameProduct" param="productVO.refinedName" scope="request" />
				<dsp:getvalueof var="zoomFlag" param="productVO.zoomAvailable" />
				<dsp:getvalueof var="skuinStock" param="inStock" />
				<dsp:getvalueof var="oosProdId" param="prodId" />
				<dsp:getvalueof var="parentProductId" param="prodId" />
				<dsp:getvalueof var="productId" param="productVO.parentCategoryList"/>
				<dsp:getvalueof var="isLeadSKU" param="collectionVO.leadSKU"/>
				<dsp:getvalueof var="collection" param="productVO.collection"/>
				<dsp:getvalueof var="linkStringNonRecproduct" param="linkStringNonRecproduct" />
				<dsp:getvalueof var="rkgProductList" param="rkgCollectionProdIds" />
				<dsp:getvalueof id="showFlag" idtype="java.lang.String"	param="showFlag" />
				<dsp:getvalueof var="prodGuideId" param="productVO.shopGuideId"/>
				<dsp:getvalueof var="lcFlag" param="lcFlag"/>
				<dsp:getvalueof var="zoomQuery" param="zoomQuery"/>
				<dsp:getvalueof var="vendorName" param="productVO.vendorInfoVO.vendorName"/>
				<dsp:getvalueof var="priceLabelCodeSKU" param="pSKUDetailVO.pricingLabelCode"/>	
				<dsp:getvalueof var="inCartFlagSKU" param="pSKUDetailVO.inCartFlag"/>	
				<dsp:getvalueof var="displaySizeAsSwatch" param="productVO.displaySizeAsSwatch"/>				
				<%-- BPSI-2446 DSK VDC message & offset message changes --%>
				<dsp:getvalueof var="offsetDateVDC" param="offsetDateVDC"/>
				<c:set var="offsetDateVDC" value="${offsetDateVDC}"/>
				<c:set var="skuVO" value="${skuVO}"/>
				<c:set var ="isVdcSku" value="${skuVO.vdcSku}"/>
				<c:set var="customizableRequired" value="${skuVO.customizableRequired}"/>
				<c:set var="customizationOffered" value="${skuVO.customizationOffered}"/>
				<c:set var="customizableCodes" value="${skuVO.customizableCodes}"/>
				<%-- BPSI-2446 DSK VDC message & offset message changes --%>
				<input type="hidden" id="zoomQuery" name="zoomQuery" value="${zoomQuery}">
				<input type="hidden" id="displaySizeAsSwatch" name="displaySizeAsSwatch" value="${displaySizeAsSwatch}">
	            <c:choose>
					<c:when test="${not empty skuVO}">
						<dsp:getvalueof var="attribs" param="pSKUDetailVO.skuAttributes" />
					</c:when>
					<c:otherwise>
						<dsp:getvalueof var="attribs" param="productVO.attributesList" />
					</c:otherwise>
				</c:choose>
				<c:choose>
				<c:when test="${not empty skuVO}">
					<dsp:getvalueof var="prdDisplayShipMsg" param="pSKUDetailVO.displayShipMsg"/>
				</c:when>
				<c:otherwise>
					<dsp:getvalueof var="prdDisplayShipMsg" param="productVO.displayShipMsg"/>
				</c:otherwise>
				</c:choose>
				<c:choose>
					<c:when test="${skuinStock eq null}">
						<c:set var="inStock" value="true" />
					</c:when>
					<c:otherwise>
						<dsp:getvalueof var="inStock" param="inStock" />
					</c:otherwise>
				</c:choose>
				<c:if test="${skuVO != null}">
					<dsp:getvalueof var="pDefaultChildSku" param="pSKUDetailVO.skuId" />
				</c:if>
				<%--Update for Story |@psin52 --%>



			<%--<dsp:getvalueof var="transient" bean="Profile.transient"/>
			<c:choose>
			<c:when test="${transient}">
						<c:set var="eVar16" value='Non registered User'/>
			</c:when>
			<c:otherwise>
					<c:set var="eVar16" value='registered User'/>
				</c:otherwise>
		  </c:choose>--%>
				<div id="collectionMultiSku" class="collectionMultiSku clearfix">
					<div class="prodDetailWrapper clearfix">
						<c:if test="${!isInternationalCustomer }">
							<c:set var ="removeVar" value="${param.removePersonalization}"/>
							<c:choose>
								<c:when test="${removeVar}">
								
									<div class= "personalizedPDPContainer grid_12  <c:if test="${not enableKatoriFlag || (empty skuVO.customizableRequired) || (skuVO.customizableRequired=='false')}">hidden</c:if>">
										<c:choose>
											<c:when test="${not empty skuVO.customizableCodes && fn:contains(customizeCTACodes, skuVO.customizableCodes)}">
												<h3 class= "personaliseMsg"><bbbt:textArea key="txt_customization_removed" language="${pageContext.request.locale.language}" />
												</h3>
											</c:when>
											<c:otherwise>
												<h3 class= "personaliseMsg"><bbbt:textArea key="txt_personalization_removed" language="${pageContext.request.locale.language}" />
												</h3>
											</c:otherwise>
										</c:choose>
									</div>
								</c:when>
								<c:otherwise>	
							
									<div class= "personalizedPDPContainer grid_12  <c:if test="${not enableKatoriFlag || (empty skuVO.customizableRequired) || (skuVO.customizableRequired=='false')}">hidden</c:if>">
										<c:choose>
											<c:when test="${not empty skuVO.customizableCodes && fn:contains(customizeCTACodes, skuVO.customizableCodes)}">
												<h3 class= "personaliseMsg"><bbbl:label key='lbl_customization_required_below' language="${pageContext.request.locale.language}" />
											</c:when>
											<c:otherwise>
												<h3 class= "personaliseMsg"><bbbl:label key='lbl_personalization_required_below' language="${pageContext.request.locale.language}" />
											</c:otherwise>
										</c:choose>
										</h3>
									</div>
								</c:otherwise>
								</c:choose>
						</c:if>
			<%--    <div class="breadcrumb padBottom_5 hidden">
				    		<div class="padLeft_20">
				             <dsp:include page="breadcrumb.jsp">
				                                    <dsp:param name="siteId" value="${appid}"/>
				                                     <dsp:param name="poc" value="${parentProductId}" />
				              </dsp:include>
				             </div>
					</div>
			--%>
						<c:set var="scene7ImgURL" value="${scene7Path}" />
						<c:if test="${(not empty largeImage)}">
							<c:choose>
								<c:when test="${prodId % 2 == 0}">
									<c:set var="scene7ImgURL" value="${fn:replace(scene7DomainPath, 'x', '1')}" />
								</c:when>
								<c:when test="${prodId % 2 == 1}">
									<c:set var="scene7ImgURL" value="${fn:replace(scene7DomainPath, 'x', '2')}" />
								</c:when>
							</c:choose>
						</c:if>


						<%-- zoom product image module start--%>
						<div class="width_312px alpha zoomProduct">
							<div id="collectionMultiSkuImageWrapper" <c:if test="${zoomFlag && SceneSevenOn && (not empty largeImage)}"> class="loadingGIF bbbS7ImageWrapper" data-s7ImgsetID="${largeImage}" </c:if> data-parentProdId="${prodId}">
								<div class="bbbS7Main ">
	                            	<div id="s7ProductImageWrapperInner" class="easyzoom easyzoom--adjacent ">
									<c:choose>
										<c:when test="${empty largeImage || !SceneSevenOn}">
											<img itemprop="image" id="mainProductImg" src="${imagePath}/_assets/global/images/no_image_available.jpg" class="mainProductImage noImageFound" height="312" width="312" alt="${productName}" />
									   </c:when>
									   <c:when test="${!zoomFlag && SceneSevenOn && (not empty largeImage)}">
											<img itemprop="image" id="mainProductImg" src="${scene7ImgURL}/${largeImage}" class="mainProductImage noImageFound" height="312" width="312" alt="${productName}" />
									   </c:when>
									   <c:otherwise>
									   		<a id="mainProductImgZoom" data-zoomhref="${scene7ImgURL}/${basicImage}?${zoomQuery}" href="javascript:void(0)" tabindex="2">
	                                           	<img itemprop="image" id="mainProductImg" src="${scene7ImgURL}/${largeImage}" class="mainProductImage"  alt="${productName}" height="312" width="312"  />
											</a>
									   		<%-- <img id="mainProductImg" src="${imagePath}/_assets/global/images/blank.gif" class="mainProductImage noImageFound" height="312" width="312" alt="${productName}" /> --%>
									   </c:otherwise>
									 </c:choose>
								</div>
							</div>
						</div>
						</div>
						<%-- zoom product image module end--%>
						<%-- R2.2 Story - 178-a4 Product Comparison Page --%>
						<c:if test="${frmComparisonPage}">
							<c:set var="compareClass" value="productComparisonMultiSku"/>
						</c:if>
						<%--In stock product information module start--%>
						
						<div itemscope itemtype="http://schema.org/Product" class="grid_6 alpha omega productDetails ${compareClass}">
							<dsp:form name="prodForm" method="post" id="prodForm" action="/_ajax/add_to_cart.json">
								<div class="registryDataItemsWrap listDataItemsWrap clearfix <c:if test="${ (skuVO.customizableRequired || skuVO.customizationOffered) && not empty skuVO.customizableCodes}">personalizedItem</c:if>" data-refNum="">
									<input type="hidden" name="isCustomizationRequired" value="${skuVO.customizableRequired}"/>
									<div class="prodAttribs clearfix">
										<div class="rebates noMarTop">
										 
											<dsp:droplet name="/atg/dynamo/droplet/ForEach">
												<dsp:param name="array" value="${attribs}" />
												<dsp:oparam name="output">
													<dsp:getvalueof var="placeHolderTop" param="key" />
													<c:if test="${(not empty placeHolderTop) && (placeHolderTop eq AttributePDPTOP)}">
														<dsp:droplet name="/atg/dynamo/droplet/ForEach">
															<dsp:param param="element" name="array" />
															<dsp:param name="sortProperties" value="+priority" />
															<dsp:oparam name="output">
															<dsp:getvalueof var="attrId" param="element.attributeName" />
																<c:if test="${fn:contains(shippingAttributesList,attrId)}">
																	 <c:set var="showShipCustomMsg" value="false"/>
																</c:if>
																<dsp:getvalueof var="placeHolderTop" param="element.placeHolder" />
																<dsp:getvalueof var="attributeDescripTop" param="element.attributeDescrip" />
																<dsp:getvalueof var="imageURLTop" param="element.imageURL" />
																<dsp:getvalueof var="actionURLTop" param="element.actionURL" />
																<%-- BPSI-2446 DSK VDC message & offset message changes --%>
																<dsp:getvalueof var="attributeName" param="element.attributeName"/>
																<c:if test="${fn:contains(attributeDescripTop,'Pers')}">
																	<c:set var="persAttr" value="true"/>
																</c:if>
																<div>
																	<c:choose>
																		<c:when test="${null ne attributeDescripTop}">
																			<c:choose>
																				<c:when test="${null ne imageURLTop}">
																					<img src="${imageURLTop}" alt="" />
																					<span><dsp:valueof param="element.attributeDescrip" valueishtml="true" /> </span>
																				</c:when>
																				<c:otherwise>
																					<c:choose>
																						<c:when test="${null ne actionURLTop}">
																				<%-- BPSI-2446 DSK VDC message & offset message changes --%>
																				<c:choose>
																						<c:when test="${not empty skuVO.skuId && isVdcSku && null ne attributeName && fn:contains(attributeName,vdcAttributesList)}">
																					  <a href="/store/browse/dynamicStaticContent.jsp?pageName=ItemShippedDirectlyFromVendor&isVdcSku=${isVdcSku}&skuId=${skuVO.skuId}" class="newOrPopup"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a>
																					</c:when>
																					<c:otherwise>
																							<a href="${actionURLTop}" class="newOrPopup"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true" /> </span> </a>
																					</c:otherwise>
																				</c:choose>
																				<%-- BPSI-2446 DSK VDC message & offset message changes --%>
																						</c:when>
																						<c:otherwise>
																							<span><dsp:valueof param="element.attributeDescrip" valueishtml="true" /> </span>
																						</c:otherwise>
																					</c:choose>
																				</c:otherwise>
																			</c:choose>
																		</c:when>
																		<c:otherwise>
																			<c:if test="${null ne imageURLTop}">
																				<c:choose>
																					<c:when test="${null ne actionURLTop}">
																						<a href="${actionURLTop}" class="newOrPopup"><img src="${imageURLTop}" alt="" /> </a>
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
									</div>
	
									<%-- This Image tag is used for find in store modal dialog Image path should be dynamic--%>
									<c:choose>
                                            <c:when test="${empty thumbnailImage}">
                                                  <img class="hidden productImage" height="83" width="83" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${productName}" />
                                            </c:when>
                                            <c:otherwise>
                                                  <img class="hidden productImage noImageFound" height="83" width="83" src="${scene7Path}/${thumbnailImage}" alt="${productName}" />
                                            </c:otherwise>
                                </c:choose>
									<h2 itemprop="name" id="productTitle" class="productDetails productTitle">
										<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
											<dsp:param name="id" param="prodId" />
											<dsp:param name="itemDescriptorName" value="product" />
											<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />	
												<dsp:getvalueof var="pdpUrl" vartype="java.lang.String" param="url" />							
											</dsp:oparam>
										</dsp:droplet>
									<dsp:getvalueof var="fromPage" param="fromPage"/>
									<c:choose>
										<c:when test="${fromPage eq 'collectionGrid' || fromPage eq 'collectionList'}">
								            <dsp:a  onclick="javascript:(typeof quickViewCrossSell === 'function')?quickViewCrossSell():void(0);" page="${finalUrl}?poc=${parentProductId}&color=${color}&skuId=${skuId}&fromPage=${fromPage}" tabindex="0">
												<dsp:valueof param="productVO.name" valueishtml="true" />
											</dsp:a>
										 </c:when>
										 <c:otherwise>
											<c:choose>
												<c:when test = "${not empty categoryId}">
													<dsp:a  onclick="javascript:(typeof quickViewCrossSell === 'function')?quickViewCrossSell():void(0);" page="${finalUrl}?categoryId=${categoryId}&color=${color}&skuId=${skuId}&fromPage=${fromPage}" tabindex="0">
														<dsp:valueof param="productVO.name" valueishtml="true" />
													</dsp:a>
											 	</c:when>
											 	<c:otherwise>
											 		<dsp:a  onclick="javascript:(typeof quickViewCrossSell === 'function')?quickViewCrossSell():void(0);" page="${finalUrl}?color=${color}&skuId=${skuId}&fromPage=${fromPage}" tabindex="0">
														<dsp:valueof param="productVO.name" valueishtml="true" />
													</dsp:a>
											 	</c:otherwise>
											</c:choose>
										</c:otherwise> 
									</c:choose>
									</h2>
									
									<%-- Product Attributes i.e. Watch Video, View Accessories --%>
									<div class="productLinksWrapper rebateContainer prodAttribWrapper clearfix">
										<div class="pdpAttributeContainer fl clearfix">
										   <dsp:getvalueof var="mediaVO" param="mediaVO"/>
											<c:choose>
											<c:when test="${null ne mediaVO}">
													<dsp:getvalueof var="mediaURL" param="mediaVO.mediaSource"/>
													<c:if test="${null ne mediaURL}">
													<div class="productLinks watchVideo clearfix"><div>
													<a href="${mediaURL}" onClick="javascript:rkg_micropixel('${currentSiteId}','video'); openmedia(); pdpOmnitureProxy('${parentProductId}', 'videoplay'); return true;" target="module35892976">
													<img src="/_assets/global/images/watch_video.png" alt="Watch Video" title="Watch Video" width="119" height="25" border="0"></a></div></div>
													</c:if>
											</c:when>
											<c:otherwise>
											  <%--  Commented Live Clicker code from Quick View as it it breaking Modal Dialog. 											 
													If LC need to be implement in Quick View then LC has to do changes in their API 
													to load data in case of AJAX driven modals.

											  	<c:if test="${lcFlag}">
													<div id="liveClickerWatchVideo" class="productLinks watchVideo clearfix" data-parentProdId="${parentProductId}">
													<script id="tag" type="text/javascript" src="<bbbc:config key="liveclicker_pdp" configName="ThirdPartyURLs" />client_id=${client_ID}&component_id=${component_ID}&dim6=${parentProductId}&widget_id="></script>
													</div>
											  </c:if> --%>
											</c:otherwise>
											</c:choose>
											<c:if test="${not empty prodGuideId}">
												<dsp:droplet name="/atg/repository/seo/GuideItemLink">
													<dsp:param name="repositoryName" value="/com/bbb/cms/repository/GuidesTemplate" />
													<dsp:param name="itemDescriptor" value="guides" />
													<dsp:param name="id" param="productVO.shopGuideId"/>
													<dsp:oparam name="output">
														<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
													</dsp:oparam>
												</dsp:droplet>
												<dsp:droplet
												name="/com/bbb/cms/droplet/GuidesLongDescDroplet">
												<dsp:param name="guideId" value="${prodGuideId }" />
												<dsp:oparam name="output">
													<dsp:getvalueof var="guideTitle"
														param="guidesLongDesc.title" />
												</dsp:oparam>
												</dsp:droplet>
												<div id="viewProductGuides" class="productLinks viewProductGuides clearfix">
													 <a href="${contextPath}${finalUrl}" onclick="pdpOmnitureProxy('${parentProductId}', 'prodguide')" title="View Product Guides" class="productGuideImg"><bbbl:label key="lbl_view_Product_Guide" language="${pageContext.request.locale.language}" />
													 </a>
												</div>
											</c:if>
											<c:if test="${collection==true}">
												<c:if test="${isLeadSKU==true}">
													<div class="productLinks">
														<a class="lnkCollectionItems smoothScrollTo viewAccessories" data-smoothscroll-topoffset="65" href="${contextPath}${pdpUrl}?viewAccessories=true"><bbbl:label key='lbl_pdp_view_accessories' language="${pageContext.request.locale.language}" />
														</a>
														<script>BBB.addPerfMark('ux-primary-action-available');</script>
													</div>
												</c:if>
											</c:if>
										</div>
										<%-- Rebate Container --%>
											<dsp:getvalueof var="hasRebate" param="pSKUDetailVO.hasRebate"/>
											<c:set var="rebatesOn" value="${false}" />
											<c:if test="${not empty hasRebate && hasRebate}">
												<dsp:getvalueof var="chkEligibleRebates" param="pSKUDetailVO.eligibleRebates"/>
												<c:if test="${(null != chkEligibleRebates) && (fn:length(chkEligibleRebates) == 1 )}">
													<c:set var="rebatesOn" value="${true}" />
												</c:if>
											</c:if>
											<dsp:droplet name="/atg/dynamo/droplet/ForEach">
												<dsp:param value="${attribs}" name="array" />
												<dsp:oparam name="output">
												<dsp:getvalueof var="placeHolderPrice" param="key"/>
													<c:if test="${(not empty placeHolderPrice) && (placeHolderPrice eq AttributePDPPrice)}">
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
																<dsp:getvalueof var="placeHolderPrice" param="element.placeHolder"/>
																<dsp:getvalueof var="attributeDescripPrice" param="element.attributeDescrip"/>
																<dsp:getvalueof var="imageURLPrice" param="element.imageURL"/>
																<dsp:getvalueof var="actionURLPrice" param="element.actionURL"/>
																<%-- BPSI-2446 DSK VDC message & offset message changes --%>
																<dsp:getvalueof var="attributeName" param="element.attributeName"/>
																<c:choose>
																	<c:when test="${null ne attributeDescripPrice}">
																		<c:choose>
																			<c:when test="${null ne imageURLPrice}">
																				<span class="rebateAttribs attribs ${sep}"><a href="${actionURLPrice}" class="newOrPopup"><img src="${imageURLPrice}" alt="<dsp:valueof param="element.attributeDescrip" valueishtml="true"/>"/></a></span>
																			</c:when>
																			<c:otherwise>
																				<c:choose>
																				<%-- BPSI-2446 DSK VDC message & offset message changes --%>
																					<c:when test="${null ne actionURLPrice}">
																				<c:choose>
																						<c:when test="${not empty skuVO.skuId && isVdcSku && null ne attributeName && fn:contains(attributeName,vdcAttributesList)}">
																					  <span class="rebateAttribs attribs ${sep}"><a href="/store/browse/dynamicStaticContent.jsp?pageName=ItemShippedDirectlyFromVendor&isVdcSku=${isVdcSku}&skuId=${skuVO.skuId}" class="newOrPopup"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a></span>
																					</c:when>
																					<c:otherwise>
																						<span class="rebateAttribs attribs ${sep}"><a href="${actionURLPrice}" class="newOrPopup"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a></span>
																					</c:otherwise>
																				</c:choose>
																					</c:when>
																					<c:otherwise>
																					<%-- BPSI-2446 DSK VDC message & offset message changes --%>																				
																						<span class="rebateAttribs attribs ${sep}"><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span>
																					</c:otherwise>
																				</c:choose>
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:if test="${null ne imageURLPrice}">
																			<c:choose>
																				<c:when test="${null ne actionURLPrice}">
																					<span class="rebateAttribs attribs ${sep}"><a href="${actionURLPrice}" class="newOrPopup"><img src="${imageURLPrice}" alt=""/></a></span>
																				</c:when>
																				<c:otherwise>
																					<span class="rebateAttribs attribs ${sep}"><img src="${imageURLPrice}" alt="" /></span>
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
												<dsp:getvalueof var="eligibleRebates" param="pSKUDetailVO.eligibleRebates"/>
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
														<span class="rebateAttribs attribs ${sep1}" ><a href="${rebate.rebateURL}" class="links" target="_blank" title="Rebate"><c:out value="${rebate.rebateDescription}" escapeXml="false"/></a></span>
													</dsp:oparam>
												</dsp:droplet>
												</c:if>
												<span class="attribs rebateAttribs <c:if test="${not skuVO.shippingRestricted or empty skuVO.shippingRestricted}"> hidden</c:if>">
		  												<a class="shippingRestrictionsApplied" href="/store/cart/static/shipping_restrictions_applied.jsp" data-skuId="${skuVO.skuId}" title="<bbbl:label key="lbl_shipping_restrictions_applied" language="${pageContext.request.locale.language}"/>">
		  												<span><span class="prod-attrib"><bbbl:label key="lbl_shipping_restrictions_details" language="<c:out param='${language}'/>"/></span></span></a>
      											</span>
									</div>
									
									<%-- Product Attributes i.e. Watch Video, View Accessories --%>
									
									<%--rating review section start--%>
	
									<c:if test="${BazaarVoiceOn}">
                                    <dsp:getvalueof var="ratingsTitle" param="element.ratingsTitle"/>
										<dsp:getvalueof var="reviews" value="${productVO.bvReviews.totalReviewCount}" />
										<dsp:getvalueof var="ratingsTitle" value="${productVO.bvReviews.ratingsTitle}" />
										<dsp:getvalueof var="ratings" value="${productVO.bvReviews.averageOverallRating}" vartype="java.lang.Integer" />
										<dsp:getvalueof var="rating" value="${ratings * 10}" vartype="java.lang.Integer" />
										<c:choose>
											<c:when test="${empty productVO}">
												<dsp:getvalueof var="totalReviewCount" value="0"></dsp:getvalueof>
											</c:when>
											<c:otherwise>
												<dsp:getvalueof var="totalReviewCount" value="${productVO.bvReviews.totalReviewCount}"></dsp:getvalueof>
												<dsp:getvalueof var="ratingsTitle" value="${productVO.bvReviews.ratingsTitle}" />
											</c:otherwise>
										</c:choose>
										<c:if test="${not flagOff}">
											<c:choose>
												<c:when test="${ratings ne null && ratings ne '0' && (reviews eq '1' || reviews gt '1') }">
													<div class="prodReviews clearfix metaFeedback">														 
														<span class="ratingTxt ratingsReviews prodReviews<fmt:formatNumber value="${rating}" pattern="#0" />"><span class="ariaLabel">${ratingsTitle}</span></span><span class="reviewTxt"><a onclick="javascript:(typeof quickViewCrossSell === 'function')?quickViewCrossSell():void(0);" href="${contextPath}${pdpUrl}?skuId=${skuId}&amp;categoryId=${CategoryId}&amp;showRatings=true" role="link" aria-label="${reviews} ${lblSflReviews} ${lblForThe} ${productName}"> ${totalReviewCount} <bbbl:label key="lbl_sfl_reviews" language="<c:out param='${language}'/>" />
														</a></span></div>
												</c:when>
												<c:otherwise>
													<div class="prodReviews clearfix metaFeedback">														 
													<span class="ratingsReviews prodReviews<fmt:formatNumber value="${rating}" pattern="#0" /> ratingTxt" ><span class="ariaLabel">${ratingsTitle}</span></span><span class="reviewTxt"><a onclick="javascript:(typeof quickViewCrossSell === 'function')?quickViewCrossSell():void(0);" href="${contextPath}${pdpUrl}?skuId=${skuId}&amp;showRatings=true" role="link" aria-label="${reviews} ${lblSflReviews} ${lblForThe} ${productName}">	${totalReviewCount} <bbbl:label key="lbl_sfl_reviews" language="<c:out param='${language}'/>" />
														</a></span></div>
												</c:otherwise>
											</c:choose>
										</c:if>
									</c:if>
								
								<%-- Rating End--%>
	              <dsp:droplet name="BreadcrumbDroplet">		
					<dsp:param name="productId" value="${prodId}" />
					<dsp:param name="siteId" param="siteId"/>
					<dsp:oparam name="output">
							<dsp:getvalueof param="isPrimaryCat" var="isPrimaryCat"/>
							<dsp:getvalueof param="isOrphanProduct" var="isOrphanProduct"/>
							<dsp:getvalueof param="bts" var="bts"/>
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array" param="breadCrumb" />
								<dsp:oparam name="output">
								 <dsp:droplet name="/atg/dynamo/droplet/ForEach">
							     <dsp:param name="array" param="breadCrumb" />
								<dsp:oparam name="output">
									<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
										<dsp:param name="id" param="element.categoryId" />
										<dsp:param name="itemDescriptorName" value="category" />
										<dsp:param name="repositoryName"
											value="/atg/commerce/catalog/ProductCatalog" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="catName" param="element.categoryName" />
											<c:set var="categoryPath" scope="request">${categoryPath} > <dsp:valueof param="element.categoryName" /></c:set>	
											<c:set var="phantomCategory" scope="request"><dsp:valueof param="element.phantomCategory" /></c:set>
											<c:if test="${count eq '1'}">
												<c:set var="rootCategory" scope="request"><dsp:valueof param="element.categoryId" /></c:set>
												<c:set var="rootCategoryName" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
												<c:set var="count" value="${count+1}" />
											</c:if>
											<c:if test="${countOmni eq '2'}">
												<c:set var="categoryNameL1" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
											</c:if>			
											<c:if test="${countOmni eq '3'}">
												<c:set var="categoryNameL2" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
											</c:if>
											<c:set var="countOmni" value="${countOmni+1}" />
										</dsp:oparam>
									</dsp:droplet>
								</dsp:oparam>
							</dsp:droplet>
								</dsp:oparam>
							</dsp:droplet>
						</dsp:oparam>
					</dsp:droplet>
								<dsp:getvalueof var="colorParam" param="color" />
								<dsp:getvalueof var="sizeParam" param="size" />
								<%-- LTL 498 start--%>
							 <dsp:getvalueof var="itemQty" value="${fn:escapeXml(param.itemQty)}" />
								<%-- <c:if test="${itemQty == 'null' ||  itemQty == ''}">
								<c:set var="itemQty" value="1" />	 
								</c:if>  --%>
								<%-- LTL 498 end--%>
	
								<div class="swatchPickers clearfix">
	
									<input type="hidden" name="prodId" value="${prodId}" />
									<%-- <input type="hidden" name="skuId" value=""/> --%>	
	
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
										<dsp:param param="productVO.rollupAttributes" name="array" />
										<dsp:param name="sortProperties" value="_key"/>
										<dsp:oparam name="output">
											<dsp:getvalueof var="menu" param="key" />
											<c:set var="sizeOrientation" value="fr"/>
											<dsp:getvalueof var="size" param="size" />
											<c:if test="${size eq 1 && menu eq 'SIZE'}">
												<c:set var="sizeOrientation" value="fl"/>
											</c:if>
											<c:choose>											
												<c:when test="${menu eq 'COLOR'}">
													<%-- SAMPLE DATA FOR COLOR SWATCHES --%>
													<fieldset> 
					                                <legend class="hidden"><bbbl:label key="lbl_colors_dropdown" language ="${pageContext.request.locale.language}"/></legend>

													<div class="colorPicker fl marTop_10 swatchPicker">
														<label> <bbbl:label key="lbl_colors_dropdown" language ="${pageContext.request.locale.language}"/></label>
														<span id="radioSel_Color" class="txtOffScreen radioTxtSelectLabel" aria-live="assertive"> </span>
														<ul class="swatches fl" id="colorSwatches" role="radiogroup"> 	
															 <dsp:droplet name="/atg/dynamo/droplet/ForEach">
																	<dsp:param param="element" name="array" />
																	<dsp:getvalueof var="size" param="size" />
																	<dsp:getvalueof var="count" param="count" />
																	
																	<c:choose>
																	   <c:when test="${count % 11 == 0}">   
																	        <c:set var="marginClass" value="fl noMarRight"/>
																	   </c:when>
																	<c:otherwise>
																	        <c:set var="marginClass" value="fl"/>
																	</c:otherwise>
																	</c:choose>
																	
																	
																	<dsp:oparam name="output">
																		<dsp:getvalueof var="colorImagePath" param="element.swatchImagePath" />
																		<dsp:getvalueof var="attribute" param="element.rollupAttribute" />
																		<dsp:getvalueof var="largeImagePath" param="element.largeImagePath" />
																		<dsp:getvalueof var="thumbnailImagePath" param="element.thumbnailImagePath" />
															            <c:choose>
																			<c:when test="${(colorParam != null) && (colorParam eq attribute)}">
																				<c:set var="selectedClass" value="colorswatch-overlay-selected"/>
																				<c:set var="colorMatched" value="${attribute}"/>
																				<c:set var="ariaChecked" value="true"/>
																			</c:when>
																			<c:otherwise>
																				<c:set var="selectedClass" value=""/>
																				<c:set var="ariaChecked" value="false"/>
																			</c:otherwise>
																		</c:choose>
																		<li class="${marginClass} colorSwatchLi" role="radio" tabindex="${count == 1 ? 0 : -1}" aria-checked="${ariaChecked}" data-attr="${attribute}" 
																		 title="${attribute}" data-s7ImgsetID="${largeImagePath}" data-imgURLThumb="${scene7Path}/${thumbnailImagePath}" aria-hidden="false">
																			<div class="colorswatch-overlay ${selectedClass}"> </div>
																			<c:choose>
																				<c:when test="${(colorImagePath != null) && (colorImagePath ne '')}">
																					<img src="${scene7Path}/${colorImagePath}" height="20" width="20" alt="${attribute}"/>
																				</c:when>
																				<c:otherwise>
																					<img src="${imagePath}/_assets/global/images/blank.gif" height="20" width="20" alt="${attribute}" class="noImageFound" />
																				</c:otherwise>
																			</c:choose>
																		</li>
															        </dsp:oparam>
															</dsp:droplet>
															<c:choose>
																<c:when test="${(colorParam != null)}">
																	<input type="hidden" name="prodColor" class="_prodColor addItemToRegis addItemToList" value="${colorMatched}" />
																</c:when>
																<c:otherwise>
																	<input type="hidden" name="prodColor" class="_prodColor addItemToRegis addItemToList" value="" />
																</c:otherwise>
															</c:choose>
														</ul>
														<script>BBB.addPerfMark('ux-primary-action-available');</script>
													</div>
													</fieldset>
												</c:when>
												<c:when test="${menu eq 'FINISH'}">
													<div class="finishPicker fl marTop_10 swatchPicker">
														<label><bbbl:label key="lbl_finish_dropdown" language ="${pageContext.request.locale.language}"/></label>
														<div class="swatches fl hideSwatchRows" id="finishSwatches">
															 <dsp:droplet name="/atg/dynamo/droplet/ForEach">
																	<dsp:param param="element" name="array" />
																	<dsp:getvalueof var="size" param="size" />
																	<dsp:getvalueof var="count" param="count" />
																	
																	<c:choose>
																	   <c:when test="${count % 11 == 0}">   
																	        <c:set var="marginClass" value="fl noMarRight"/>
																	   </c:when>
																	<c:otherwise>
																	        <c:set var="marginClass" value="fl"/>
																	</c:otherwise>
																	</c:choose>
																	
																	
																	<dsp:oparam name="output">
																		<dsp:getvalueof var="finishImagePath" param="element.swatchImagePath" />
																		<dsp:getvalueof var="attribute" param="element.rollupAttribute" />
																		<dsp:getvalueof var="largeImagePath" param="element.largeImagePath" />
																		<dsp:getvalueof var="thumbnailImagePath" param="element.thumbnailImagePath" />
																		<c:choose>
																			<c:when test="${(colorParam != null) && (colorParam eq attribute)}">
																				<c:set var="selectedClass" value="selected"/>
																				<c:set var="finishMatched" value="${attribute}"/>
																			</c:when>
																			<c:otherwise>
																				<c:set var="selectedClass" value=""/>
																			</c:otherwise>
																		</c:choose>
																        <a title="${attribute}" data-attr="${attribute}" data-s7imgsetid="${largeImagePath}" data-imgurlthumb="${scene7Path}/${thumbnailImagePath}" data-imgurlsmall="${scene7Path}/${largeImagePath}" class="${marginClass} ${selectedClass}" href="#">
																           <img width="30" height="30" src="${scene7Path}/${finishImagePath}" alt="${attribute}" title="${attribute}">
															            </a>
																	 </dsp:oparam>
																</dsp:droplet>
															<c:choose>
																<c:when test="${(colorParam != null)}">
																	<input type="hidden" name="prodFinish" class="_prodFinish addItemToRegis addItemToList" value="${finishMatched}" />
																</c:when>
																<c:otherwise>
																	<input type="hidden" name="prodFinish" class="_prodFinish addItemToRegis addItemToList" value="" />
																</c:otherwise>
															</c:choose>
														</div>
														<script>BBB.addPerfMark('ux-primary-action-available');</script>
													</div>
												</c:when>
												
												<c:when test="${menu eq 'SIZE'}">
													<div class="sizePicker ${sizeOrientation} marTop_10 swatchPicker">
														<label for=""><bbbl:label key="lbl_sizes_dropdown" language ="${pageContext.request.locale.language}"/></label>
														<div class="swatches" tabindex="0">
														<c:choose>
														<c:when test="${displaySizeAsSwatch eq 'true'}">
															<div id="selectProductSize">
																<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																	<dsp:param param="element" name="array" />
																	<dsp:oparam name="output">
																		<dsp:getvalueof var="attribute" param="element.rollupAttribute" />
																		
																		<c:choose>
																			<c:when test="${(sizeParam != null) && ((sizeParam eq attribute))}">
																			<c:set var="selectedClass" value="selected"/>
																			<a class="selectProdSizeBox ${selectedClass}" tabindex="0">
																				<span>${attribute}</span>
																			</a>	
																			</c:when>
																			<c:otherwise>
																			<c:set var="selectedClass" value=""/>
																			<a class="selectProdSizeBox ${selectedClass}" tabindex="0">
																				<span>${attribute}</span>
																			</a>	
																			</c:otherwise>
																		</c:choose>
																	</dsp:oparam>
																</dsp:droplet>
															</div>
															</c:when>
															<c:otherwise>
															<select id="selectProductSize" name="selectProductSize" class="customSelectBox">
																<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																	<dsp:param param="element" name="array" />
																	<dsp:oparam name="outputStart">
																		<option value=""><bbbl:label key='lbl_pdp_select_size' language="${pageContext.request.locale.language}" /></option>
																	</dsp:oparam>
																	<dsp:oparam name="output">
																		<dsp:getvalueof var="attribute" param="element.rollupAttribute" />
																		<c:choose>
																			<c:when test="${(sizeParam != null) && ((sizeParam eq attribute))}">
																				<option data-attr="${attribute}" value="${attribute}" selected="selected">${attribute}</option>
																			</c:when>
																			<c:otherwise>
																				<option data-attr="${attribute}" value="${attribute}">${attribute}</option>
																			</c:otherwise>
																		</c:choose>
																	</dsp:oparam>
																</dsp:droplet>
															</select>
															</c:otherwise>
														</c:choose>
															<c:choose>
																<c:when test="${(sizeParam != null)}">
																	<input type="hidden" name="prodSize" class="_prodSize addItemToRegis addItemToList" value="${sizeParam}" />
																</c:when>
																<c:otherwise>
																	<input type="hidden" name="prodSize" class="_prodSize addItemToRegis addItemToList" value="" />
																</c:otherwise>
															</c:choose>
														</div>
														<script>BBB.addPerfMark('ux-primary-action-available');</script>
													</div>
												</c:when>
											</c:choose>
										</dsp:oparam>
									</dsp:droplet>
								</div>
	
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param value="${attribs}" name="array" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="placeHolderMiddle" param="key" />
										<c:if test="${(not empty placeHolderMiddle) && (placeHolderMiddle eq AttributePDPMiddle)}">
											<dsp:droplet name="/atg/dynamo/droplet/ForEach">
												<dsp:param param="element" name="array" />
												<dsp:param name="sortProperties" value="+priority" />
												<dsp:oparam name="outputStart">
													<div class="productLinks moreProdAttribs PDPM">
												</dsp:oparam>
												<dsp:oparam name="output">
														<dsp:getvalueof var="attrId" param="element.attributeName" />
														<c:if test="${fn:contains(shippingAttributesList,attrId)}">
															 <c:set var="showShipCustomMsg" value="false"/>
														</c:if>
													<dsp:getvalueof var="placeHolderMiddle" param="element.placeHolder" />
													<dsp:getvalueof var="attributeDescripMiddle" param="element.attributeDescrip" />
													<dsp:getvalueof var="imageURLMiddle" param="element.imageURL" />
													<dsp:getvalueof var="actionURLMiddle" param="element.actionURL" />
													<%-- BPSI-2446 DSK VDC message & offset message changes --%>
													<dsp:getvalueof var="attributeName" param="element.attributeName"/>
													<div>
														<c:choose>
															<c:when test="${null ne attributeDescripMiddle}">
																<c:choose>
																	<c:when test="${null ne imageURLMiddle}">
																		<a href="${actionURLMiddle}" class="newOrPopup"> <img src="${imageURLMiddle}" alt="<dsp:valueof param="element.attributeDescrip" valueishtml="true"/>" /> </a>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${null ne actionURLMiddle}">
																				<%-- BPSI-2446 DSK VDC message & offset message changes --%>
																				<c:choose>
																					<c:when test="${not empty skuVO.skuId && isVdcSku && null ne attributeName && fn:contains(attributeName,vdcAttributesList)}">
																					  <a href="/store/browse/dynamicStaticContent.jsp?pageName=ItemShippedDirectlyFromVendor&isVdcSku=${isVdcSku}&skuId=${skuVO.skuId}" class="newOrPopup"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a>
																					</c:when>
																					<c:otherwise>
																				<a href="${actionURLMiddle}" class="newOrPopup"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true" /> </span> </a>
																					</c:otherwise>
																				</c:choose>
																				<%-- BPSI-2446 DSK VDC message & offset message changes --%>
																			</c:when>
																			<c:otherwise>
																				<span><dsp:valueof param="element.attributeDescrip" valueishtml="true" /> </span>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:if test="${null ne imageURLMiddle}">
																	<c:choose>
																		<c:when test="${null ne actionURLMiddle}">
																			<a href="${actionURLMiddle}" class="newOrPopup"><img src="${imageURLMiddle}" alt="" /> </a>
																		</c:when>
																		<c:otherwise>
																			<img src="${imageURLMiddle}" alt="" />
																		</c:otherwise>
																	</c:choose>
																</c:if>
															</c:otherwise>
														</c:choose>
													</div>
												</dsp:oparam>
												<dsp:oparam name="outputEnd">
													</div>
												</dsp:oparam>
											</dsp:droplet>
										</c:if>
									</dsp:oparam>
								</dsp:droplet>
								<div class="clear"></div>
	
								<%--price & quantity module start--%>
								<dsp:getvalueof var="showTab" param="tabLookUp" />
								<dsp:getvalueof var="salePriceRangeDescription" param="productVO.salePriceRangeDescription" />
	
								<span itemprop="offers" itemscope itemtype="http://schema.org/Offer"></span>
					<%--Katori integration on Product Compare page as a part of BPSI-2430 changes start --%>
					<c:set var="customizableCodes" value="${skuVO.customizableCodes}" scope="request"/>
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
						<c:when test="${(skuVO.customizableRequired || skuVO.customizationOffered) && not empty skuVO.customizableCodes && not isInternationalCustomer}">
							<div class="personalizationOffered clearfix cb fl">
								<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="${contextPath}${pdpUrl}?color=${color}&openKatori=true&skuId=${skuId}"</c:otherwise></c:choose> class="collectionPersonalize ${customizeAttr} <c:if test="${not enableKatoriFlag}">disabled</c:if>" role="button" data-product="${prodId}" data-sku="${pDefaultChildSku}" data-custom-vendor="${vendorName}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
									${personalizationTxt}
								</a>
								<c:choose>
									<c:when test="${enableKatoriFlag}">
										<c:if test="${not empty skuVO.personalizationType && skuVO.personalizationType =='PB'}">
											<span class="feeApplied"><bbbl:label key='lbl_PB_Fee_detail' language="${pageContext.request.locale.language}" /></span>	
										</c:if>
										<c:if test="${not empty skuVO.personalizationType && skuVO.personalizationType =='PY'}">
											<span class="feeApplied">	<bbbl:label key='lbl_PY_Fee_detail' language="${pageContext.request.locale.language}" /></span>	
										</c:if>
										<c:if test="${not empty skuVO.personalizationType && skuVO.personalizationType =='CR'}">
											<span class="feeApplied">	<bbbl:label key='lbl_CR_Fee_detail' language="${pageContext.request.locale.language}" /></span>	
										</c:if>
										<span class="personalizationDetail">
										<bbbl:label key='lbl_cart_personalization_detail' language="${pageContext.request.locale.language}" />
										</span>
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
									<c:when test="${(skuVO.customizableRequired || skuVO.customizationOffered) && not empty skuVO.customizableCodes}">
										<div class="personalizationOffered clearfix cb fl">
											<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="#"</c:otherwise></c:choose> class="personalize ${customizeAttr} disabled" role="button" data-product="${prodId}" data-sku="${pDefaultChildSku}" data-custom-vendor="${vendorName}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
												${personalizationTxt}
											</a>
											<span class="personalizationIntlShippingMsg"><bbbt:textArea key="txt_pdp_per_unavailable_intl" language="${pageContext.request.locale.language}" /></span>
										</div>
									</c:when>
									<c:otherwise>
										<div class="personalizationOffered clearfix cb fl hidden ">
											<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="#"</c:otherwise></c:choose> class="personalize ${customizeAttr} disabled" role="button" data-product="${prodId}" data-sku="${pDefaultChildSku}" data-refnum="" data-quantity="" data-custom-vendor="${vendorName}" <c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
												${personalizationTxt}
											</a>
											<span class="personalizationIntlShippingMsg"><bbbt:textArea key="txt_pdp_per_unavailable_intl" language="${pageContext.request.locale.language}" /></span>
										</div>
									</c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise>
									<div class="personalizationOffered clearfix cb fl hidden ">
										<a <c:choose><c:when test="${enableKatoriFlag eq false}">href="javascript:void(0)"</c:when><c:otherwise>href="${contextPath}${pdpUrl}?color=${color}&openKatori=true&skuId=${skuId}"</c:otherwise></c:choose> class="collectionPersonalize ${customizeAttr} <c:if test="${not enableKatoriFlag}">disabled</c:if>" role="button" data-custom-vendor="${vendorName}" data-product="${prodId}" data-sku="${pDefaultChildSku}" data-refnum="" data-quantity="" <c:if test="${enableKatoriFlag}">onclick="customLinks('${omniPersonalizeButtonClick}')"</c:if>;>
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
											</c:when>
											<c:otherwise>
												&nbsp;<bbbl:label key='lbl_pdp_personalization_unavailable' language="${pageContext.request.locale.language}" />
											</c:otherwise>
										</c:choose>	
									</div>
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
					<%--Katori integration on Product Compare page as a part of BPSI-2430 changes end --%>	
								<c:choose>
									<c:when test="${showTab}">
	
	
										<div class="priceQuantity clearfix cb <c:if test="${inStock==false}">priceQuantityNotAvailable</c:if>">
											<div itemprop="offers" itemscope itemtype="http://schema.org/Offer">
												<div class="prodPrice">
													<c:choose>
														<c:when test="${not empty pDefaultChildSku}">
													
															<dsp:include page="product_details_price.jsp">
																<dsp:param name="product" param="productIdproductId" />
																<dsp:param name="sku" value="${pDefaultChildSku}" />
																<dsp:param name="persAttr" value="${persAttr}" />
																<dsp:param name="priceLabelCodeSKU" value="${priceLabelCodeSKU}" />
																<dsp:param name="inCartFlagSKU" value="${inCartFlagSKU}" />
																<dsp:param name="isFormatRequired" value="true" />
																
															</dsp:include>
													
														</c:when>
														<c:otherwise>
														<dsp:getvalueof var="isdynamicPriceProd" param="productVO.dynamicPricingProduct" />
														<dsp:getvalueof var="priceLabelCodeProd" param="productVO.priceLabelCode" />
														<dsp:getvalueof var="inCartFlagProd" param="productVO.inCartFlag" />
														<dsp:getvalueof var="priceRangeDescription" param="productVO.priceRangeDescription" />
														<dsp:include page="browse_price_frag.jsp">
															<dsp:param name="priceLabelCode" value="${priceLabelCodeProd}" />
															<dsp:param name="inCartFlag" value="${inCartFlagProd}" />
															<dsp:param name="salePrice" value="${salePriceRangeDescription}" />
															<dsp:param name="listPrice" value="${priceRangeDescription}" />
															<dsp:param name="isdynamicPriceEligible" value="${isdynamicPriceProd}" />
															<dsp:param name="isFromPDP" value="false" />
															<dsp:param name="mutliSKUQuickView" value="true" />
														</dsp:include>   		
														</c:otherwise>
													</c:choose>
													<c:if test="${currentSiteId ne BedBathCanadaSite and not empty skuVO and skuVO.ltlItem}">
														<span class="ltlDeliveryMethod"> + Delivery</span>
													</c:if>
												</div>
											<%-- BPSI-2446 DSK VDC message & offset message changes --%>
											<dsp:getvalueof var="offsetDateVDC" param="offsetDateVDC"/>
											 <c:set var="vdcOffsetFlag">
												<bbbc:config key="vdcOffsetFlag" configName="FlagDrivenFunctions" />
											</c:set>											
											<dsp:droplet name="/atg/dynamo/droplet/IsNull"> 
										  		<dsp:param name="value" param="pSKUDetailVO"/>										  		
												<dsp:oparam name="true">												
												    	<p class="grid_6 vdcOffsetMsg highlightRed bold hidden"></p>											 	
												</dsp:oparam>
												<dsp:oparam name="false">
													<c:if test="${vdcOffsetFlag && not empty offsetDateVDC}">				
															<jsp:useBean id="placeHolderMapServiceLevel" class="java.util.HashMap" scope="request" />
																<c:set target="${placeHolderMapServiceLevel}" property="actualOffSetDate" value="${offsetDateVDC}" />
																<c:if test="${not empty skuVO && !skuVO.ltlItem}">
																	<p class="grid_6 vdcOffsetMsg highlightRed bold marTop_n7">			
																		<bbbt:textArea key="txt_vdc_offset_msg" placeHolderMap="${placeHolderMapServiceLevel}" language="${pageContext.request.locale.language}" />
																	</p>
																</c:if>	
															</c:if>
												</dsp:oparam>
											</dsp:droplet>						
						<%-- BPSI-2446 DSK VDC message & offset message changes --%>
												
												
												
												
												
												
												<span class="prodAvailabilityStatus hidden"> <c:choose>
														<c:when test="${inStock==false}">
															<link itemprop="availability" href="http://schema.org/OutOfStock" />
															<bbbl:label key='lbl_pdp_out_of_stock' language="${pageContext.request.locale.language}" />
														</c:when>
														<c:otherwise>
															<link itemprop="availability" href="http://schema.org/InStock" />
															<bbbl:label key='lbl_pdp_in_stock' language="${pageContext.request.locale.language}" />
														</c:otherwise>
													</c:choose> </span> </div>
											<dsp:getvalueof var="emailStockAlertsEnabled" param="pSKUDetailVO.emailStockAlertsEnabled" />
	
                                        <c:if test="${inStock==false}">
                                            <c:set var="productOOS" value="true"/>
                                        </c:if>
											<div class="message <c:if test="${inStock==true}">hidden</c:if>">
												<c:choose>
													<c:when test="${inStock==true}">
														<input type="hidden" value="" name="oosSKUId" class="_oosSKUId" />
													</c:when>
													<c:otherwise>
														<input type="hidden" value="${pDefaultChildSku}" name="oosSKUId" class="_oosSKUId" />
													</c:otherwise>
												</c:choose>
												<input type="hidden" value="${oosProdId}" name="oosProdId" />
	
												<c:if test="${OutOfStockOn}">
													<c:choose>
														<c:when test="${(null eq emailStockAlertsEnabled) || (emailStockAlertsEnabled==true)}">
															<div class="error" aria-hidden="true">
															<bbbl:label key='lbl_pdp_product_notavailable_shipping' language="${pageContext.request.locale.language}" />
															</div>
															<div class="info" aria-label="<bbbl:label key='lbl_oos_notify_me_aria' language='${pageContext.request.locale.language}' />">
																<a class="lnkNotifyOOS" href="#"><bbbl:label key='lbl_pdp_product_notify_item_available' language="${pageContext.request.locale.language}" /> </a>
															</div>
														</c:when>
														<c:otherwise>
															<div class="error" <c:if test="${MapQuestOn && not bopusAllowed && not isInternationalCustomer}">aria-hidden="true"</c:if>>
																<bbbl:label key='lbl_pdp_product_notavailable_shipping' language="${pageContext.request.locale.language}" />
															</div>
															<div class="info hidden">
																<a class="lnkNotifyOOS" href="#"><bbbl:label key='lbl_pdp_product_notify_item_available' language="${pageContext.request.locale.language}" /> </a>
															</div>
														</c:otherwise>
													</c:choose>
												</c:if>
												 <%--   Disabled the findInStore for internationalUser --%>
												<c:if test="${MapQuestOn}">
	
												<c:if test="${localStorePDP eq false}">
													<c:if test="${not bopusAllowed}">
														<c:if test="${not frmComparisonPage}">
														<c:choose>
										                  <c:when test='${isInternationalCustomer}'>
														     <div class="info disabled">
																<a href="javascript:void(0);" class="changeStore disabled" aria-hidden="true"><bbbl:label key='lbl_pdp_product_findstore_near' language="${pageContext.request.locale.language}" /> </a>
															  </div>
														   </c:when>
										                   <c:otherwise>
															<div class="info">
																<a href="javascript:void(0);" class="changeStore" onclick="pdpOmnitureProxy('${prodId}', 'findinstore');" aria-label="<bbbl:label key='lbl_oos_find_in_store_aria'
																	language='${pageContext.request.locale.language}' />"><bbbl:label key='lbl_pdp_product_findstore_near' language="${pageContext.request.locale.language}" /> </a>
															</div>
										                   </c:otherwise>
														</c:choose>
														</c:if>
													</c:if>	
												</c:if>
												</c:if>
											</div>
										</div>
										<c:if test="${showShipCustomMsg}">
										<div class="freeShipBadge">
										${prdDisplayShipMsg}
										</div>
										</c:if>
									
										<%-- R2.2 Story - 178-a4 Product Comparison Page --%>
										<c:if test="${frmComparisonPage}">
											<input type="hidden" value="true" class="addItemToList" name="fromComparisonPage"/>
										</c:if>
										<%-- LTL-PDP related changes Nikhil Koul start --%>
									<c:if test="${currentSiteId ne BedBathCanadaSite}">
									<dsp:droplet name="GetApplicableLTLShippingMethodsDroplet">
											<dsp:param name="skuId" value="${skuVO.skuId}" />
											<dsp:param name="siteId" value="${appid}" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="shipMethodVOList" param="shipMethodVOList"/>
											</dsp:oparam>
									</dsp:droplet>
									<div id="ltlDeliveryMethodWrapper" class="clearfix <c:if test='${empty menu}'>extendList</c:if> <c:if test='${empty skuVO || not skuVO.ltlItem}'>hidden</c:if>"> 
										<jsp:useBean id="placeHolderMapContext" class="java.util.HashMap" scope="request"/>
										<c:set target="${placeHolderMapContext}" property="contextPath" value="${contextPath}"/>
										<a href="<bbbt:textArea key="txt_static_deliveryinfo" placeHolderMap="${placeHolderMapContext}" language ="${pageContext.request.locale.language}"/>" role="link" class="fl popupShipping"> 
											<img class="marBottom_5" width="20" height="15" src="/_assets/global/images/LTL/truck.png" alt="Truck Options">
											&#32;<span><bbbl:label key='ltl_truck_delivery_options' language="${pageContext.request.locale.language}" /> :</span>
											<img class="quesMark marLeft_5" width="15" height="15" src="/_assets/global/images/LTL/quesMark.png" alt="Question Mark" />
										</a>
										<%-- <a href="#" class="info"><img class="quesMark marLeft_5" width="15" height="15" src="/_assets/global/images/LTL/quesMark.png" alt="Question Mark" /><span class="textLeft">Some text related to LTL tooltip will come here.</span></a> --%> 
										
										 
										<select data-ltlflag="true" aria-required="true" class="customSelectBoxCollection" id="selServiceLevel" name="selServiceLevel"> 
											<option value=""><bbbl:label key="lbl_pdp_sel_service" language="<c:out param='${language}'/>"/></option>
											<dsp:droplet name="/atg/dynamo/droplet/ForEach">
											<dsp:param value="${shipMethodVOList}" name="array" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="shipMethodId" param="element.shipMethodId"/>
												<dsp:getvalueof var="shipMethodName" param="element.shipMethodDescription"/>	
												<dsp:getvalueof var="deliverySurchargeval" param="element.deliverySurcharge"/>
												<c:set var="deliverySurcharge" value="" />
												<c:if test="${deliverySurchargeval gt 0}">
													<c:set var="deliverySurcharge"><dsp:valueof  converter="currency" param="element.deliverySurcharge" /></c:set>
												</c:if>
												<c:if test="${deliverySurchargeval eq 0}">
												<c:set var="deliverySurcharge">Free</c:set>
											    </c:if>
																									
												<option value="${shipMethodId}" class="enable">${shipMethodName} - ${deliverySurcharge}</option>												
											</dsp:oparam>
										</dsp:droplet>
										</select>
										
										<input type="hidden" value="" class="_prodSize addItemToRegis addItemToList" id="serviceLevelFlag" name="selServiceLevel"> 
										<a href="${contextPath}/static/EasyReturns" title="<bbbl:label key='lbl_return_policy_title' language="${pageContext.request.locale.language}" />" class="fr marTop_10 marLeft_10"><bbbl:label key='lbl_return_policy' language="${pageContext.request.locale.language}" /></a>
									</div>
									</c:if>

									<%-- LTL-PDP related changes Nikhil Koul start --%>
										<%-- R2.2.1 Story - 131 Quick View Page --%>	
										<div class="qvProdDescription marBottom_20">
											<p class="prodDescHead noMar" tabindex="0"><bbbl:label key="lbl_prod_description" language="${pageContext.request.locale.language}" /></p>
											<div id="qvProdDesc">
												<div class="viewport clearfix heightFix">
													<div class="overview" tabindex="0" style="min-height:100px;">
														<dsp:valueof param="productVO.longDescription" valueishtml="true" />
													</div>											
												</div>
											 </div>
										</div>
										<%-- R2.2.1 Story - 131 Quick View Page --%>	
										<c:if test="${not frmComparisonPage}">
										  	<div class="btnPD clearfix noPadBot">
											  	<div class="fr">
													  <%-- R2.2.1 Story - 131 Quick View Page --%>									  
													  <div class="quantityPDP">
														  <div class="spinner">
															  	<c:if test="${not frmComparisonPage}">
																	<!-- BPS-2060 -->
																	<!-- label id="lbldetCollDecQty" for="detCollDecQty" class="txtOffScreen" for="accListDecQty"><bbbl:label key="lbl_decrease_quantity" language="${pageContext.request.locale.language}" /></label -->
																	<a href="#" class="scrollDown down" id="accListDecQty" title="<bbbl:label key='lbl_decrease_quantity' language="${pageContext.request.locale.language}" />"><span class="txtOffScreen"><bbbl:label key="lbl_decrease_quantity" language="${pageContext.request.locale.language}" /></span></a>
																	<span class="incDec visuallyhidden" aria-live="assertive" aria-atomic="true"></span>
																	<input id="quantity" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="fl addItemToRegis _qty itemQuantity addItemToList" type="text" name="qty" role="textbox" 
																	value="<c:choose>
																	<c:when test="${itemQty == 'null' ||  itemQty == ''}">
																	1	
																	</c:when>
																	<c:otherwise>
																		<dsp:valueof value="${fn:escapeXml(param.itemQty)}"/>
																	</c:otherwise>
																	</c:choose>" maxlength="2" data-change-store-submit="qty" data-change-store-errors="required digits nonZero" aria-required="true" aria-describedby="quantity" /> 
																	<!-- label id="lbldetCollIncQty" class="txtOffScreen" for="detCollIncQty"><bbbl:label key="lbl_increase_quantity" language="${pageContext.request.locale.language}" /></label -->
																	<a href="#" class="scrollUp up" id="detCollIncQty" title="<bbbl:label key='lbl_increase_quantity' language="${pageContext.request.locale.language}" />"><span class="txtOffScreen"><bbbl:label key="lbl_increase_quantity" language="${pageContext.request.locale.language}" /></span></a>
																</c:if>
																<dsp:getvalueof var="regId" value="${fn:escapeXml(param.registryId)}" />
																<input type="hidden" name="registryId" class="sflRegistryId  addItemToRegis addItemToList" value="${regId}" data-change-store-submit="registryId" /> 
																<input type="hidden" name="prodId" class="_prodId temp addItemToRegis productId addItemToList" value="${prodId}" data-change-store-submit="prodId" data-change-store-errors="required" data-change-store-internalData="true" /> 
																<input type="hidden" name="skuId" value="${pDefaultChildSku}" class="addItemToRegis _skuId addItemToList changeStoreSkuId" data-change-store-submit="skuId" data-change-store-errors="required" data-change-store-internalData="true" /> 
																<input type="hidden" name="price" value="${omniPrice}" class="addItemToList addItemToRegis" /> 
																<input type="hidden" name="regReturnUrl" value="${param.bbbCurrentURL}" class="addItemToList addItemToRegis" />
																<input type="hidden" class="addToCartSubmitData" name="storeId" value="" data-change-store-storeid="storeId" /> 
																<input type="hidden" class="addToCartSubmitData" name="bts" value="${bts}" data-change-store-storeid="bts" />
																<c:if test="${not empty regId}">
																	<input type="hidden" class="addToCartSubmitData" name="registryId" value="${regId}" data-change-store-submit="registryId" />
																</c:if>
														  </div>
													  </div>
													  <%-- R2.2.1 Story - 131 Quick View Page --%>
											       <c:choose>
													<c:when test="${not empty skuVO.personalizationType && (skuVO.personalizationType =='PB' || skuVO.personalizationType =='CR')}">
													    <c:set var="disableCTA" value="true"/>
													</c:when>
													<c:otherwise>
														<c:set var="disableCTA" value="false"/>
													</c:otherwise>
												    </c:choose>
													<div class="fl marginButtonFix">
														<dsp:include page="../addgiftregistry/add_item_gift_registry.jsp">
															<dsp:param name="ltlFlag" value="${not empty skuVO && skuVO.ltlItem && currentSiteId ne BedBathCanadaSite}" />
															<dsp:param name="isCustomizationRequired" value="${isCustomizationRequired}"/>
															<dsp:param name="ltlProductFlag" value="${productVO.ltlProduct}"/>															
															<dsp:param name="disableCTA" value="${disableCTA}"/>
														</dsp:include>
														<input type="hidden" name="parentProdId" class="_prodId addItemToRegis addItemToList" value="${parentProductId}" />
													</div>
													<c:if test="${isCustomizationRequired || inStock==false || (isInternationalCustomer && isIntlRestricted)}">
														<c:set var="button_disabled" value="button_disabled" />
													</c:if>
													<div class="fl addToCart">
														<div class="button button_active button_active_orange ${button_disabled}">
															<input type="submit" name="btnAddToCart" value="Add to Cart" onclick="rkg_micropixel('${appid}','cart')" role="button" <c:if test="${isCustomizationRequired || inStock==false || (isInternationalCustomer && isIntlRestricted)}">disabled="disabled"</c:if>/>
															<%--LTL 498 start--%>
															<c:choose>
																<c:when test="${not empty skuVO && skuVO.ltlItem}">
																	<input type="hidden" name="isLtlItem" value="${true}" />
																</c:when>
																<c:otherwise>
																	<input type="hidden" name="isLtlItem" value="${false}" />
																</c:otherwise>
															</c:choose>
															<%--LTL 498 end--%>
														</div>
													</div>												
												</div>
												<c:if test="${(isInternationalCustomer && isIntlRestricted)}">
												<div class="notAvailableIntShipMsg fr padTop_10 cb clearfix"><bbbl:label key='lbl_pdp_intl_restricted_message' language="${pageContext.request.locale.language}" /></div>
												</c:if>
												<%-- R2.2.1 Story - 131 Quick View Page --%>
													<div class="btnPDLinks padTop_5 clearfix cb fr">
														<div class="addToList fl <c:if test="${isCustomizationRequired}">disabled opacity_1</c:if>" id="btnAddToList${skuid}">
															<c:choose>
																<c:when test="${isCustomizationRequired}">
																	<a class="disableText ptrEvtNone" href="javascript:void(0);" role="link" aria-disabled="true"><bbbl:label key='lbl_compare_save_for_later' language="${pageContext.request.locale.language}" /></a>
																</c:when>
																<c:otherwise>
													         <a href="javascript:void(0);" onclick="rkg_micropixel('${appid}','wish')" role="link" aria-label="${lblSaveThe} ${productName} ${lblFuturePurchase}"><bbbl:label key='lbl_compare_save_for_later' language="${pageContext.request.locale.language}" /></a>
																</c:otherwise>
															</c:choose>
													     </div>
														
														<c:if test="${MapQuestOn}">
															<div class="fl findInStore padLeft_20">
																<dsp:getvalueof var="bopusAllowed" param="pSKUDetailVO.bopusAllowed" />
																<c:choose>
																	<c:when test="${bopusAllowed || (not empty skuVO && skuVO.ltlItem)}">
																		<div class="changeStore fr cb disabled opacity_1">
														 					<a class="disableText ptrEvtNone" href="javascript:void(0);" role="link" aria-disabled="true" onclick="pdpOmnitureProxy('${prodId}', 'findinstore');rkg_micropixel('${appid}','findstore');" aria-disabled="true"><bbbl:label key='lbl_compare_find_in_store' language="${pageContext.request.locale.language}" /></a>
														 				</div>
																	</c:when>
																<%--  Disabled the findInStore for internationalUser --%>
																	    <c:when test="${isCustomizationRequired || isInternationalCustomer}">
																		<div class="changeStore fr cb disabled opacity_1" role="link" aria-disabled="true">
														 					<a class="disableText" href="javascript:void(0);" role="link" aria-disabled="true"><bbbl:label key='lbl_compare_find_in_store' language="${pageContext.request.locale.language}" /></a>
														 				</div>
																	</c:when>
																	<c:otherwise>
																		<div class="changeStore fr cb" tabindex="0" aria-labelledby="findVO productTitle inStoreVO">
														 					<a tabindex="-1" href="javascript:void(0);" role="link" onclick="pdpOmnitureProxy('${prodId}', 'findinstore');rkg_micropixel('${appid}','findstore');"><bbbl:label key='lbl_compare_find_in_store' language="${pageContext.request.locale.language}" /></a>
														 					<span id="findVO" class="visuallyhidden">Find</span>
														 					<span id="inStoreVO" class="visuallyhidden">in store near you</span>
														 				</div>
																	</c:otherwise>
																</c:choose>
															</div>
														</c:if>
													</div>
													<%-- R2.2.1 Story - 131 Quick View Page --%>
										  </div>
										</c:if>
										
										<div class="hidden appendSKUInfo">
											<c:if test="${not empty pDefaultChildSku}">
												<p class="smalltext prodSKU">
													<bbbl:label key='lbl_pdp_sku_id' language="${pageContext.request.locale.language}" />
													${pDefaultChildSku}
												</p>
											</c:if>
										</div>
	
										<%--  CR Changes  --%>
										<c:if test="${MapQuestOn && (appid eq BuyBuyBabySite)}">
											<bbbt:textArea key="txt_pdp_pickupinstore" language="${pageContext.request.locale.language}" />
										</c:if>
	
										<div class="message">
											<div id="addToCartErrorDiv" class="error"></div>
										</div>
	
									</c:when>
									<c:otherwise>
										<div class="grid_6 alpha">
	
											<div itemprop="offers" itemscope itemtype="http://schema.org/Offer">
												<div class="prodPrice">
													<c:choose>
														<c:when test="${not empty salePriceRangeDescription}">
														
									
															<dsp:getvalueof var="salePriceRange" param="productVO.salePriceRangeDescription" />
															<c:choose>
																<c:when test="${fn:contains(salePriceRange,'-')}">
																	<br />
																</c:when>
																<c:otherwise>&nbsp;</c:otherwise>
															</c:choose>
															<span class="prodPriceNEW"> <dsp:valueof converter="currency" param="productVO.salePriceRangeDescription" /> </span>
														
															<span class="prodPriceOLD"> <span class="was"><bbbl:label key='lbl_old_price_text' language="${pageContext.request.locale.language}" /> </span> <span class="oldPriceNum"> <dsp:valueof converter="currency" param="productVO.priceRangeDescription" /> </span> </span>
															<dsp:getvalueof idtype="java.lang.String" id="salePrice" param="productVO.salePriceRangeDescription" />
														</c:when>
														<c:otherwise>
															<dsp:valueof converter="currency" param="productVO.priceRangeDescription" />
														</c:otherwise>
													</c:choose>
												</div> </div>
	
	
											<%-- R2.2 117-a1 story --%>
											<c:if test="${collection==true}">
												<c:if test="${isLeadSKU==false}">
													<div class="productLinks noMarTop clearfix marBottom_5">
														<div class="button button_active">
															<a class="lnkCollectionItems smoothScrollTo" data-smoothscroll-topoffset="65" href="#collectionItems"><bbbl:label key='lbl_pdp_show_collection' language="${pageContext.request.locale.language}" /> </a>
															<script>BBB.addPerfMark('ux-primary-action-available');</script>
														</div>
													</div>
												</c:if>
											</c:if>
										</div>
									</c:otherwise>
								</c:choose>
							</div>
						</dsp:form>
					</div>
				</div>
				
			
				<%-- R2.2 Story - 178-a4 Product Comparison Page --%>
		<c:if test="${frmComparisonPage}">
					<div class="compareMultiSku clearfix">
						<div class="fl compareText"><bbbl:label key='lbl_comparison_chart' language="${pageContext.request.locale.language}" /></div>
						     <c:set var="language" value="${pageContext.request.locale.language}" scope="request"/>
							<c:set var="AddToCompare"><bbbl:label key='lbl_compare_selected_options' language="${language}"/></c:set>						
						<dsp:form name="prodcompareform" method="post" id="prodcompareform">
						<input type="hidden" bean="ProductListHandler.productID" value="${prodId}" name="prodIdToCompare" />
						<input type="hidden" bean="ProductListHandler.skuId" value="${skuId}" id="skuIdforSubmission" name="skuIdToCompare" />
						<div class="button button_disabled fr compWithSelectButton"> 
								<label for="submitProdCompareForm" class="hidden"><bbbl:label key='lbl_compare_selected_options' language='${pageContext.request.locale.language}' /></label>
								<dsp:input type="submit" bean="ProductListHandler.addProduct" name="btnAddToCart" disabled="true" priority="-200"  id="submitProdCompareForm" value="${AddToCompare}" />
							</div>			 
							</dsp:form>
					</div>
				</c:if>
				</dsp:oparam>
				<dsp:oparam name="error">
					<c:set var="productNotfound" value="true"/>
				</dsp:oparam>
			
				</dsp:droplet>
				<%-- R2.2 Omniture Implementation Start --%>
				<dsp:droplet name="/com/bbb/account/droplet/BBBConfigKeysDroplet">
			    <dsp:param name="configKey" value="DimNonDisplayConfig"/>
			    <dsp:oparam name="output">
			        <dsp:getvalueof var="configMap" param="configMap"/>
			    </dsp:oparam>
			</dsp:droplet>
	
			<dsp:droplet name="/com/bbb/account/droplet/BBBURLEncodingDroplet">
				<dsp:param name="URL" value="${pageContext.request.requestURL}?${pageContext.request.queryString}"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="queryString" param="queryString"/>
	            </dsp:oparam>
	         </dsp:droplet>
			
			<c:if test="${collection==true}">
				<c:choose>
				<c:when test="${isLeadSKU==true}">
				<dsp:include page="accessoriesForms.jsp">
				<dsp:param name="color" value="${color}"/>
				<dsp:param name="parentProductId" value="${parentProductId}"/>
				<dsp:param name="crossSellFlag" value="true"/>
				<dsp:param name="showAccessories" value="false"/>
				<dsp:param name="desc" value="Accessories (pdp)"/>
				<dsp:param name="color" value="${color}"/>
				</dsp:include>
				<dsp:getvalueof var="childProducts" param="collectionVO.childProducts"/>
				<c:set var="SchemeName" value="pdp_acccav;pdp_accfbw"/>
				<c:set var="omniProp5" value="Product with Accessory"/>
				</c:when>
				<c:otherwise>
				<dsp:include page="collectionForms.jsp">
				<dsp:param name="parentProductId" value="${parentProductId}"/>
				<dsp:param name="crossSellFlag" value="true"/>
				<dsp:param name="desc" value="Collection (pdp)"/>
					<dsp:param name="color" value="${color}"/>
	
				</dsp:include>
				<dsp:getvalueof var="childProducts" param="collectionVO.childProducts"/>
				<c:set var="SchemeName" value="pdp_collcav;pdp_collfbw"/>
				<c:set var="omniProp5" value="Collection modal"/>
				</c:otherwise>
				</c:choose>
			</c:if>
			
		
			<dsp:getvalueof var="siteId" bean="Site.id" />
			
			<dsp:getvalueof var="categoryId" param="poc"/>
								
		
		 <c:forEach var="conMap" items="${configMap}">
			    <c:if test="${conMap.key eq appid}">
					<c:choose>
						<c:when test="${phantomCategory}">
						
						
							<c:set var="floatingNodes" value="Floating nodes" />
							<c:set var="prop2Var">${floatingNodes} > ${rootCategoryName}</c:set>
							<c:set var="rkgCategoryNames" value="${floatingNodes},${rootCategoryName}"/>
							<c:if test="${!empty categoryNameL1}">
							<c:set var="prop3Var">${floatingNodes} > ${rootCategoryName} > ${categoryNameL1}</c:set>
							<c:set var="rkgCategoryNames" value="${floatingNodes},${rootCategoryName},${categoryNameL1}"/>
							</c:if>
							<c:set var="channel">${floatingNodes}</c:set>
							<c:if test="${fn:contains(queryString, 'fromCollege')}">
								<c:set var="rootCategoryName" value="College" />
								<c:set var="prop3Var"> ${rootCategoryName} > ${rootCategoryName} > ${refinedNameProduct}</c:set>
								<c:set var="rkgCategoryNames" value="${rootCategoryName},${refinedNameProduct}"/>
							</c:if>
						</c:when>
						<c:otherwise>
							<c:choose>
							
								<c:when test="${(clearanceCategory == rootCategory) || (whatsNewCategory==rootCategory) }">
								<c:set var="prop2Var">${rootCategoryName} > ${rootCategoryName}</c:set>
								<c:set var="rkgCategoryNames" value="${rootCategoryName},${refinedNameProduct}"/>
								<c:set var="channel">${rootCategoryName}</c:set>
								<c:if test="${!empty categoryNameL1}">
									<c:set var="prop3Var">${rootCategoryName} > ${rootCategoryName} > ${categoryNameL1}</c:set>
									<c:set var="rkgCategoryNames" value="${rootCategoryName},${refinedNameProduct},${categoryNameL1}"/>
								</c:if>
								</c:when>
								<c:otherwise>
									<c:set var="queryString" value="${queryString}" />
									<c:choose>
									<c:when test="${!empty categoryNameL1}">
									<c:set var="prop2Var">${rootCategoryName} > ${categoryNameL1}</c:set>
									<c:set var="rkgCategoryNames" value="${rootCategoryName},${categoryNameL1}"/>
									</c:when>
									<c:otherwise>
									<c:set var="prop2Var">${rootCategoryName} > ${rootCategoryName}</c:set>
									<c:set var="rkgCategoryNames" value="${rootCategoryName}"/>
									</c:otherwise>
									</c:choose>
									<c:set var="channel">${rootCategoryName}</c:set>
									<c:if test="${!empty categoryNameL2}">
										<c:set var="prop3Var">${rootCategoryName} > ${categoryNameL1} > ${categoryNameL2}</c:set>
										<c:set var="rkgCategoryNames" value="${rootCategoryName},${categoryNameL1},${categoryNameL2}"/>
									</c:if>
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
					</c:if>
				</c:forEach>
						<c:choose>
					<c:when test="${not empty collectionId_Omniture}">
						<c:set var="omni_prod" >${fn:substring(collectionId_Omniture,0,fn:length(collectionId_Omniture)-1)}</c:set>
					</c:when>
					<c:otherwise>
                    <%-- <c:choose>
                        <c:when test="${not empty pDefaultChildSku}">
                            <c:set var="omni_prod" >;${parentProductId};;;;eVar30=${pDefaultChildSku}</c:set>
                        </c:when>
                        <c:otherwise> --%>
                            <c:set var="omni_prod" >;${prodId}</c:set>
                        <%-- </c:otherwise>
                    </c:choose> --%>
                </c:otherwise>
            </c:choose>
			<dsp:getvalueof var="addToList" param="addToList"/>
			<dsp:getvalueof var="prodList" param="prodList"/>
			<dsp:getvalueof var="showpopup" param="showpopup"/>
			<dsp:getvalueof var="registryId" value="${fn:escapeXml(param.registryId)}"/>
			<dsp:getvalueof var="registryName" param="registryName"/>
			<dsp:getvalueof var="totQuantity" param="totQuantity"/>
			<dsp:getvalueof id="omniList" value="${sessionScope.added}"/>
			
			<%--DoubleClick Floodlight START  
		<c:set var="DoubleClickOn" scope="request"><tpsw:switch tagName="DoubleClickTag_us" /></c:set>
		<c:set var="DoubleClickOn" scope="request"><tpsw:switch tagName="DoubleClickTag_baby" /></c:set>	
		<c:set var="DoubleClickOn" scope="request"><tpsw:switch tagName="DoubleClickTag_ca" /></c:set>	
	   <c:if test="${DoubleClickOn}">
	     <c:if test="${(currentSiteId eq BedBathUSSite)}">
    		   <c:set var="cat"><bbbc:config key="cat_product_bedBathUS" configName="RKGKeys" /></c:set>
    		   <c:set var="src"><bbbc:config key="src_bedBathUS" configName="RKGKeys" /></c:set>
    		   <c:set var="type"><bbbc:config key="type_1_bedBathUS" configName="RKGKeys" /></c:set>
    		 </c:if>
    		 <c:if test="${(currentSiteId eq BuyBuyBabySite)}">
    		   <c:set var="cat"><bbbc:config key="cat_product_baby" configName="RKGKeys" /></c:set>
    		   <c:set var="src"><bbbc:config key="src_baby" configName="RKGKeys" /></c:set>
    		   <c:set var="type"><bbbc:config key="type_1_baby" configName="RKGKeys" /></c:set>
    		 </c:if>
    		  <c:if test="${(currentSiteId eq BedBathCanadaSite)}">
		    		   <c:set var="cat"><bbbc:config key="cat_product_bedbathcanada" configName="RKGKeys" /></c:set>
		    		   <c:set var="src"><bbbc:config key="src_bedbathcanada" configName="RKGKeys" /></c:set>
		    		   <c:set var="type"><bbbc:config key="type_1_bedbathcanada" configName="RKGKeys" /></c:set>
		    		 </c:if>
			 		<dsp:include page="/_includes/double_click_tag.jsp">
			 			<dsp:param name="doubleClickParam" value="src=${src};type=${type};cat=${cat};u4=${parentProductId};u5=${productName};u10=${rootCategory};u11=${rootCategoryName};u12=${categoryNameL1};u13=${categoryNameL2}"/>
			 		</dsp:include>
		 		</c:if>
		 		 DoubleClick Floodlight END --%>
			
			<%-- BBBI-3048 Omniture Tagging Start--%>
				
				<dsp:getvalueof var="vendorParam" bean="SessionBean.vendorParam"/>
				<c:set var="fireEvent90" value="false"/>
				<c:choose>
					<c:when test="${not empty sessionScope.boostCode && sessionScope.boostCode != '00'}">
						<c:set var="l2l3BoostFlag"><bbbc:config key="L2L3BoostFlag" configName="FlagDrivenFunctions"/></c:set> 
						<c:set var="brandsBoostFlag"><bbbc:config key="BrandsBoostFlag" configName="FlagDrivenFunctions"/></c:set> 
						<c:set var="keywordBoostFlag"><bbbc:config key="KeywordBoostFlag" configName="FlagDrivenFunctions"/></c:set> 
						<c:if test="${((keywordBoostFlag && fromPage == 'searchPage') || (brandsBoostFlag && fromPage == 'brandsPage') || (l2l3BoostFlag && fromPage == 'categoryPage'))
								&& isEndecaControl eq false}">
				    		<c:set var="fireEvent90" value="true"/>
				    	</c:if>       
					</c:when>
					<c:when test="${(not empty vendorParam) && (fromPage == 'searchPage')}">
						<c:set var="fireEvent90" value="true"/> 
					</c:when>
				</c:choose>
				<%-- BBBI-3048 Omniture Tagging End--%> 
		
		<script type="text/javascript">
		$(function () {
			var registryId = '${fn:escapeXml(registryId)}';
			var addtoList='${addToList}';
			if(registryId.length>0){
			rkg_micropixel("${appid}","addtoregistry");
			}
			if(addtoList.length>0){
			rkg_micropixel("${appid}","wish");
			}
		});
		function addToWishList(){
			   var qty = '${param.qty}';
			   var price = '${certonaPrice}';
			   totalPrice=(qty*price).toFixed(2);
			   var finalOut= ';'+'${fn:escapeXml(param.productId)}'+';;;event38='+'${fn:escapeXml(param.qty)}'+'|event39='+totalPrice+';eVar30='+'${fn:escapeXml(param.skuId)}';
			   additemtolist(finalOut);
		}
		</script>



		
	<c:choose>	
		<c:when test="${ (skuVO.customizableRequired || skuVO.customizationOffered) && not empty skuVO.customizableCodes}">
			<script type="text/javascript">
                var productNotfound = ${productNotfound},
                productOOS = ${productOOS},
                collectionPDP = ${collection};
                var BBB = BBB || {};
                var omni_channel = '${fn:replace(fn:replace(channel,'\'',''),'"','')}';
                var omni_refinedNameProduct = '${fn:replace(fn:replace(refinedNameProduct,'\'',''),'"','')}';
                var omni_prop2Var = '${fn:replace(fn:replace(prop2Var,'\'',''),'"','')}';
                var omni_prop3Var = '${fn:replace(fn:replace(prop3Var,'\'',''),'"','')}';
                var fireEvent90="${fireEvent90}";
                if (typeof s !== "undefined") {
                    BBB.omnitureObj.backup_s("eVar1,eVar2,eVar3,eVar4,eVar5,eVar6,eVar7,eVar8,eVar47,pageName,channel,prop2,prop3,prop5,productNum,prop7,prop8,prop25");
                    BBB.omnitureObj.clear_s("eVar3");
                    BBB.omnitureObj.delete_s("eVar1,eVar2,eVar4,eVar5,eVar6,eVar7,eVar8,eVar46,eVar47,productNum,prop7,prop8,prop25");
                    s.prop1="Product Details Page";
                    if(productNotfound){
                        s.pageName="product not available";
                        s.channel="Error page";
                        s.products="${omni_prod}".replace(/,(?=[^,]*$)/, '');
                        s.prop2="Error page";
                        s.prop3="Error page";
                        s.prop4="";
                        s.prop5="";
                        s.events="event57";
                    } else {
                        s.pageName="Product Detail > " + omni_refinedNameProduct;
                        s.channel=omni_channel;
                        s.products="${omni_prod}".replace(/,(?=[^,]*$)/, '');
                        s.prop2=omni_prop2Var;
                        s.prop3=omni_prop3Var;
                        s.prop5="${omniProp5}";
                        if (productOOS && !collectionPDP) {                       	
                            	s.events="event57,event17,event81";                              
                        } else {                        	
                        		s.events="event57,event81";
                        }
                    }
                    if(fireEvent90 == 'true') {
                   		 s.events=s.events+',event90';
                   	 }
                    s.eVar61="";
                    fixOmniSpacing();
                    s.t();
                    BBB.omnitureObj.restore_s("eVar1,eVar2,eVar3,eVar4,eVar5,eVar6,eVar7,eVar8,eVar47,productNum,prop7,prop8,prop25,pageName,channel,prop2,prop3,prop5");
                }
			</script>
		</c:when>
		<c:otherwise>
				<script type="text/javascript">
                var productNotfound = ${productNotfound},
                productOOS = ${productOOS},
                collectionPDP = ${collection};
                var BBB = BBB || {};
                var omni_channel = '${fn:replace(fn:replace(channel,'\'',''),'"','')}';
                var omni_refinedNameProduct = '${fn:replace(fn:replace(refinedNameProduct,'\'',''),'"','')}';
                var omni_prop2Var = '${fn:replace(fn:replace(prop2Var,'\'',''),'"','')}';
                var omni_prop3Var = '${fn:replace(fn:replace(prop3Var,'\'',''),'"','')}';
                var fireEvent90="${fireEvent90}";
				var evar1='${fn:escapeXml(strategyName)}';
				var fireEvent90="${fireEvent90}";
                if (typeof s !== "undefined") {
                    BBB.omnitureObj.backup_s("eVar1,eVar2,eVar3,eVar4,eVar5,eVar6,eVar7,eVar8,eVar47,productNum,prop7,prop8,prop25,pageName,channel,prop2,prop3,prop5");
                    BBB.omnitureObj.clear_s("eVar3");
                    BBB.omnitureObj.delete_s("eVar1,eVar2,eVar4,eVar5,eVar6,eVar7,eVar8,eVar46,eVar47,productNum,prop7,prop8,prop25");
                    s.prop1="Product Details Page";
                    if(productNotfound){
                        s.pageName="product not available";
                        s.channel="Error page";
                        s.products="${omni_prod}".replace(/,(?=[^,]*$)/, '');
                        s.prop2="Error page";
                        s.prop3="Error page";
                        s.prop4="";
                        s.prop5="";
                        s.events="event57";
                        
                    } else {
                        s.pageName="Product Detail > " + omni_refinedNameProduct;
                        s.channel=omni_channel;
                        s.products="${omni_prod}".replace(/,(?=[^,]*$)/, '');
                        s.prop2=omni_prop2Var;
                        s.prop3=omni_prop3Var;
                        s.prop5="${omniProp5}";
                        if (productOOS && !collectionPDP) {                      	
                            	s.events="event57,event17";                              
                        } else {                        	
                        		s.events="event57";
                        }
                    }
                    if(fireEvent90 == 'true') {
                    	s.events=s.events + ',event90';
                   	 }
                    s.eVar61="";
                    fixOmniSpacing();
                    s.t();
                    BBB.omnitureObj.restore_s("eVar1,eVar2,eVar3,eVar4,eVar5,eVar6,eVar7,eVar8,eVar47,productNum,prop7,prop8,prop25,pageName,channel,prop2,prop3,prop5");    
                }
			</script>
		</c:otherwise>
	</c:choose>
			<%-- R2.2 Omniture Implementation End --%>
			<c:if test="${not frmComparisonPage}">
				<c:if test="${productVO.collection and not empty productVO.childProducts }">
					<c:forEach items="${productVO.childProducts }" var="childProductCertona" varStatus="count">
						<c:choose>
							<c:when test="${count.count eq 1 }">
								<c:set var="childProductsCertonaPlk" value="${childProductCertona.productId }"/>
							</c:when>
							<c:otherwise>
								<c:set var="childProductsCertonaPlk" value="${childProductsCertonaPlk };${childProductCertona.productId }"/>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</c:if>
			</c:if>
			</div>
			<input class="hidden getSkuIdComp" value="${pDefaultChildSku}" />
			<input type='hidden' id="childProductsCertonaPlk" value="${childProductsCertonaPlk }"/>
			<div id="botCrossSell" class="clearfix quickViewCertona loadAjaxContent" role="complementary" data-ajax-url="${contextPath}/browse/quickView_certona_slots.jsp" data-ajax-params-count="6" 
				data-ajax-param1-name="parentProductId" data-ajax-param1-value="${parentProductId}" data-ajax-param2-name="productId" data-ajax-param2-value="${prodId}"
				data-ajax-param3-name="linkStringNonRecproduct" data-ajax-param3-value="${linkStringNonRecproduct}" data-ajax-param4-name="OutOfStockOn" data-ajax-param4-value="${OutOfStockOn}" data-ajax-param5-name="inStock" data-ajax-param5-value="${inStock}" data-ajax-param6-name="isMultiSku" data-ajax-param6-value="${fn:escapeXml(isMultiRollUpFlag)}">
            </div>
</dsp:page>

