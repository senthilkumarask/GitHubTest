<dsp:page>
	<style type="text/css">
		.hide {
			display: none;
		}
	</style>
	<c:choose>
   <c:when test="${currentSiteId eq 'BuyBuyBaby'}">
      <svg class='hide'>
         <symbol id='icon-star-icon' viewBox="0 0 1024 1024">
            <title>star-icon</title>
            <path fill='#f75ea2' class='path1' d="M1024 397.050l-353.78-51.408-158.22-320.582-158.216 320.582-353.784 51.408 256 249.538-60.432 352.352 316.432-166.358 316.432 166.358-60.434-352.352 256.002-249.538z"></path>
         </symbol>
         <symbol id='icon-star-icon_fill' viewBox="0 0 1024 1024">
            <title>star-icon</title>
            <path fill='#3977c5' class='path1' d="M1024 397.050l-353.78-51.408-158.22-320.582-158.216 320.582-353.784 51.408 256 249.538-60.432 352.352 316.432-166.358 316.432 166.358-60.434-352.352 256.002-249.538z"></path>
         </symbol>
      </svg>
   </c:when>
   <c:otherwise>
      <svg class='hide'>
         <symbol id='icon-star-icon' viewBox="0 0 1024 1024">
            <title>star-icon</title>
            <path fill='#01aef0' class='path1' d="M1024 397.050l-353.78-51.408-158.22-320.582-158.216 320.582-353.784 51.408 256 249.538-60.432 352.352 316.432-166.358 316.432 166.358-60.434-352.352 256.002-249.538z"></path>
         </symbol>
         <symbol id='icon-star-icon_fill' viewBox="0 0 1024 1024">
            <title>star-icon</title>
            <path fill='#273691' class='path1' d="M1024 397.050l-353.78-51.408-158.22-320.582-158.216 320.582-353.784 51.408 256 249.538-60.432 352.352 316.432-166.358 316.432 166.358-60.434-352.352 256.002-249.538z"></path>
         </symbol>
      </svg>
   </c:otherwise>
</c:choose>

	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFetchProductIdDroplet" />
	<dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/RegistryInfoDisplayDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/POBoxValidateDroplet" />
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
        <dsp:importbean bean="/com/bbb/commerce/checkout/droplet/IsProductSKUShippingDroplet"/>
        <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
        <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	<c:set var="othersCat"><bbbc:config key="DefaultCategoryForRegistry" configName="ContentCatalogKeys" /></c:set>
	<c:set var="enableLTLReg"><bbbc:config key="enableLTLRegForSite" configName="FlagDrivenFunctions" /></c:set>
	<c:set var="enableKatoriFlag"><bbbc:config key="enableKatori" configName="EXIMKeys" /></c:set>
	<%-- Droplet Placeholder --%>
	<dsp:getvalueof var="registryId" param="registryId" />
	<dsp:getvalueof var="regAddress" value="${fn:escapeXml(param.regAddress)}" />
	<dsp:getvalueof var="skuId" value="" />
	<dsp:getvalueof var="productId" param="productId" />
	<dsp:getvalueof var="sortSeq" param="sorting"/>
	<dsp:getvalueof var="view" param="view"/>
	<dsp:getvalueof var="eventType" param="eventType" />
	<dsp:getvalueof var="isInternationalCustomer" param="isInternationalCustomer" />
	<dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
	<dsp:getvalueof var="regPublic" param="regPublic" />
	
	<c:set var="scene7Path">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
	<jsp:useBean id="placeHolderMapServiceLevel" class="java.util.HashMap" scope="request"/>
		<c:set var="vdcAttributesList">
		<bbbc:config key="vdcAttributesList" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="saSrc"><bbbc:config key="socialAnnexURL" configName="ThirdPartyURLs" /></c:set>

<c:set var="lblForThe"><bbbl:label key="lbl_accessibility_for_the" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblOfThe"><bbbl:label key="lbl_accessibility_of_the" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblProductQuickView"><bbbl:label key='lbl_mng_regitem_quick_view' language="${pageContext.request.locale.language}" /></c:set>
	<dsp:include page="registry_modified_item_form.jsp">
	</dsp:include>
	
	<dsp:droplet name="POBoxValidateDroplet">
		 <dsp:param value="${regAddress}" name="address" />
		 <dsp:oparam name="output">
		 <dsp:getvalueof var="isPOBoxAddress" param="isValid"/>
		 </dsp:oparam>
	 </dsp:droplet>

	<%-- if the registry has no shipping address available, product links bring up edit modal--%>
	<c:choose>
		<c:when test="${empty regAddress}">
			<c:set var="productLinkClass" value="editRegInfo completeShipping" />
		</c:when>
		<c:otherwise >
			<c:set var="productLinkClass" value="lnkQVPopup" />
		</c:otherwise>
	</c:choose>

	<dsp:form id="formID1" method="post">
	<dsp:input type="hidden" bean="GiftRegistryFormHandler.size" paramvalue="totEntries" />
	<c:set var="valueIndex">0</c:set>
	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="categoryBuckets" />
		<dsp:oparam name="output">
		<dsp:getvalueof var="sizeIE" param="size"/>
		<dsp:getvalueof var="countIE" param="count"/>
			<c:choose>
				<c:when test="${countIE eq 1}">
					<div class="accordionReg1 accordionReg accordion ui-accordion ui-widget ui-helper-reset ui-accordion-icons" id="accordionReg1">
				</c:when>
				<c:when test="${countIE eq 5}">
					</div>
					<div class="accordionReg2 accordionReg accordion ui-accordion ui-widget ui-helper-reset ui-accordion-icons" id="accordionReg2">
				</c:when>
				<c:when test="${countIE eq 12}">
					</div>
					<div class="accordionReg3 accordionReg accordion ui-accordion ui-widget ui-helper-reset ui-accordion-icons" id="accordionReg3">
				</c:when>
			</c:choose>
			<dsp:getvalueof var="bucketName" param="key" idtype="java.lang.String" />
			<dsp:getvalueof var="bucket" param="categoryBuckets.${bucketName}" idtype="java.util.List" />
			<c:if test="${bucketName ne othersCat}">
				<dsp:getvalueof var="catName" value=""/>
				<dsp:getvalueof var="catId" value=""/>
				<dsp:getvalueof var="addItemFlag" value=""/>
				<dsp:getvalueof var="recommendedCatFlag" value=""/>
				<dsp:droplet name="ForEach">
					<dsp:param name="array" param="categoryVOMap" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="idxCat" param="index"/>
						<dsp:getvalueof var="mapKey" param="key"/>
						<c:if test="${mapKey eq bucketName}">
							<dsp:getvalueof var="recommendedCatFlag" param="element.recommendedCatFlag"/>
							<dsp:getvalueof var="catName" param="element.catName"/>
							<dsp:getvalueof var="catId" param="element.catId"/>
							<dsp:getvalueof var="addItemFlag" param="element.addItemFlag"/>
							<dsp:getvalueof var="count" value="${fn:length(bucket)}"/>
                        <c:if test="${recommendedCatFlag || count gt 0}">
							<h2 class="ui-accordion-header ui-helper-reset ui-state-default ui-corner-all"><span class="ui-icon ui-icon-triangle-1-e"></span><span class="accordionTitle block clearfix"><a href="#" class="fl accordionLink">${fn:toLowerCase(catName)}&nbsp;(${count})</a>
							<c:if test="${!empty catId}">
							<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
								<dsp:param name="id" value="${catId}" />
								<dsp:param name="itemDescriptorName" value="category" />
								<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
										<a href="${contextPath}${finalUrl}" title="${catName}" class="fr addNewItem">
	                                		<c:if test="${addItemFlag eq 'true'}">
												<bbbl:label key='lbl_mng_regitem_addmoreitem' language="${pageContext.request.locale.language}" />
											</c:if>
	                               		</a>
									</dsp:oparam>
							</dsp:droplet>
							</c:if>
							</span></h2>


							<%-- ForEach for List<RegistryItemVO> listRegistryItemVO --%>
                            <div class="accordionDiv ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom">
                                <ul class="productDetailList giftViewProduct">
							<dsp:droplet name="ForEach">
								<dsp:param name="array" param="categoryBuckets.${bucketName}" />
								<dsp:param name="elementName" value="regItem" />
								
								<dsp:oparam name="output">
									<dsp:getvalueof var="skuID" vartype="java.lang.String"  param="regItem.sku" />
									<dsp:droplet name="GiftRegistryFetchProductIdDroplet">
										<dsp:param name="skuId" value="${skuID}" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="productId" vartype="java.lang.String" param="productId" />
											<dsp:getvalueof var="inStoreSku" param="inStore" />
										</dsp:oparam>
									</dsp:droplet>
									
																								
									<c:choose>
									<c:when test="${itemList ne ''}">
										<c:set var="itemList" scope="request">${itemList};${productId}</c:set>
									</c:when>
									<c:otherwise>
										<c:set var="itemList" scope="request">${productId}</c:set>
									</c:otherwise>
									</c:choose>

									<dsp:getvalueof var="isBelowLineItem" param="regItem.isBelowLineItem" />
									<dsp:getvalueof var="disable" param="regItem.sKUDetailVO.disableFlag" />
									<dsp:getvalueof var="webOffered" param="regItem.sKUDetailVO.webOfferedFlag" />
									<dsp:getvalueof var="active" param="regItem.sKUDetailVO.activeFlag" />
									<dsp:getvalueof var="ltlItemFlag" param="regItem.sKUDetailVO.ltlItem" />
									<dsp:getvalueof var="ltlShipMethod" param="regItem.ltlDeliveryServices" />
									<dsp:getvalueof var="ltlShipMethodDesc" param="regItem.ltlShipMethodDesc"/>
									<dsp:getvalueof var="ltlAssemblySelected" param="regItem.assemblySelected"/>
									<dsp:getvalueof var="shipMethodUnsupported" param="regItem.shipMethodUnsupported"/>
									<dsp:getvalueof var="isDSLUpdateable" param="regItem.dslUpdateable"/>	
																	
									
									<dsp:getvalueof var="idx" param="index"/>
										<%-- <dsp:input bean="CartModifierFormHandler.addItemCount" type="hidden" iclass="addItemCount" value="1" /> --%>
										<li class="grid_12 alpha omega qtyWrapper productRow btngiftViewTopMargin giftViewProductChangeStoreWrapper addToCartGiftRegWrapper clearfix">
										<c:if test="${ltlItemFlag && isPOBoxAddress}">
											<div class="ltlMessage displayPOBox"><bbbt:textArea key="txt_ltl_shipping_not_available" language ="${pageContext.request.locale.language}"/></div>
										</c:if>
										<dsp:getvalueof var="notifyRegistrantMsgTyp" param="regItem.displayNotifyRegistrantMsg"/>										
										<c:if test="${not empty notifyRegistrantMsgTyp}">
											<div class="notifyRegistrantMessage"> <bbbl:label key="notifyRegMsg_Status${notifyRegistrantMsgTyp}" language="${pageContext.request.locale.language}" /> </div>
										</c:if>
										
                                        <input type="hidden" name="bts" value="false" class="addToCartSubmitData" />
										<c:if test="${ltlItemFlag && (ltlShipMethod eq null || ltlShipMethod eq '')}">
											<div class="ltlMessage"><bbbl:label key='lbl_rlp_ltl_registry_message' language="${pageContext.request.locale.language}" />
											<c:if test="${isDSLUpdateable}"><a class="updateDsl"><bbbl:label key='lbl_update_service_level' language="${pageContext.request.locale.language}" /></a></c:if></div>
										</c:if>
										
										<c:if test="${ltlItemFlag && not empty ltlShipMethod && shipMethodUnsupported}">
										<c:set target="${placeHolderMapServiceLevel}" property="ShippingMethodDesc" value="${ltlShipMethodDesc}"/>
										<div class="clear"></div>
										<div class="ltlMessage marBottom_10 marTop_10" >
										<bbbt:textArea key="txt_ship_method_not_supported_rlp" placeHolderMap="${placeHolderMapServiceLevel}" language ="${pageContext.request.locale.language}"/>
										<c:if test="${isDSLUpdateable}"><a class="updateDsl"><bbbl:label key='lbl_update_service_level' language="${pageContext.request.locale.language}" /></a></c:if>
										</div>
										</c:if>
										<c:if test="${ltlItemFlag && (ltlShipMethod ne null && ltlShipMethod ne '')}">
											<c:set var="ltlOpt" value="&sopt=${ltlShipMethod}"></c:set>
										</c:if>
										
										<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
											<dsp:param name="id" value="${productId}"/>
											<dsp:param name="itemDescriptorName" value="product" />
											<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
											</dsp:oparam>
										</dsp:droplet>
										<c:set var="productName">
											<dsp:valueof param="regItem.sKUDetailVO.displayName" valueishtml="true"/>
										</c:set>
										<dsp:getvalueof var="personalizedImageUrls" param="regItem.personalizedImageUrls"/>
										<dsp:getvalueof var="personalizedImageUrlThumbs" param="regItem.personalizedImageUrlThumbs"/>
										<dsp:getvalueof var="personalisedCode" param="regItem.personalisedCode"/>
										<dsp:getvalueof var="personalizationOptionsDisplay" param="regItem.personalizationOptionsDisplay"/>
										<dsp:getvalueof var="customizedPrice" vartype="java.lang.Double" param="regItem.customizedDoublePrice"/>
										<dsp:getvalueof var="personlisedPrice" vartype="java.lang.Double" param="regItem.personlisedPrice"/>
										<c:set var="customizedPrice1"><dsp:valueof value="${customizedPrice}" converter="currency"/></c:set>
	                                    <c:set var="personlisedPrice1"><dsp:valueof value="${personlisedPrice}" converter="currency"/></c:set>
										<dsp:getvalueof var="itemType" param="regItem.itemType"/>
										<dsp:getvalueof var="personalizationType" param="regItem.sKUDetailVO.personalizationType" />
										<dsp:getvalueof var="refNum" param="regItem.refNum"/>
										<dsp:getvalueof var="customizationDetails" param="regItem.customizationDetails"/>
										<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.mediumImage"/>
										<dsp:getvalueof var="skuDetailVO" param="regItem.sKUDetailVO"/>
										<dsp:getvalueof var="color" param="regItem.sKUDetailVO.color"/>
										<dsp:getvalueof var="size" param="regItem.sKUDetailVO.size"/>
										<dsp:getvalueof var="upc" param="regItem.sKUDetailVO.upc"/>
										<dsp:getvalueof var="ltlShipMethodDesc" param="regItem.ltlShipMethodDesc"/>
										<dsp:getvalueof var="inCartFlagSKU" value="${skuDetailVO.inCartFlag}" />
										<dsp:getvalueof var="deliverySurcharge" param="regItem.deliverySurcharge"/>
								<dsp:getvalueof var="assemblyFee" param="regItem.assemblyFees"/>
								<dsp:getvalueof var="shipMethodUnsupported" param="regItem.shipMethodUnsupported"/>	
										<c:if test="${not empty personalisedCode}">
											<c:set var="cusDet">${eximCustomizationCodesMap[personalisedCode]}</c:set>									
											<input type="hidden" value="${cusDet}" id="customizationDetails"/> 
										</c:if>
										
                                        <div class="productImage  grid_2 alpha">
                                            <c:choose>
                                            <c:when test="${active}">
                                            <a class="prodImg ${productLinkClass}" href="${contextPath}${finalUrl}?skuId=${skuID}&registryId=${registryId}${ltlOpt}"
                                             data-pdp-qv-url="pdp_quick_view.jsp" data-skuId="${skuID}" data-registryId="${registryId}" data-productId="${productId}" data-sopt="${ltlShipMethod}" data-refNum="${refNum}"  title="<c:out value='${productName}'/>" <c:if test="${not empty refNum}">data-personalisedCode="${personalisedCode}"
															data-customizedPrice="${customizedPrice1}" data-customizationDetails="${customizationDetails}" data-color="${color}" data-size="${size}" data-personalizedImageUrls="${personalizedImageUrls}"
															data-personlisedPrice="${personlisedPrice1}" data-personalizedImageUrlThumbs="${personalizedImageUrlThumbs}" data-personalizationType= "${personalizationType}"</c:if>>
															
                                             		<c:choose>
                                             			<c:when test="${not empty refNum}">
                                             				<c:choose>
                                             					<c:when test="${not empty personalizedImageUrlThumbs}">
                                             						<img src="${imagePath}/_assets/global/images/blank.gif" data-original="${personalizedImageUrlThumbs}" alt="<c:out value='${productName}'/>" height="96" width="96" class="prodImage noImageFound loadingGIF" onerror="this.src = '${imagePath}/_assets/global/images/no_image_available.jpg';" />
                                             					</c:when>
                                             					<c:when test="${not empty imageURL}">
                                                            		<img src="${imagePath}/_assets/global/images/blank.gif" data-original="${scene7Path}/${imageURL}" alt="<c:out value='${productName}'/>" height="146" width="146" class="prodImage noImageFound loadingGIF" />
                                                        		</c:when>
                                             					<c:otherwise>
                                             						<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="146" width="146" alt="<c:out value='${productName}'/>" class="prodImage" />                                             						
                                             					</c:otherwise>
                                             				</c:choose>
                                             				
                                             				
                                             			</c:when>
                                             			<c:otherwise>
                                             				<c:choose>
                                                       			 <c:when test="${empty imageURL}">                                                       			 
                                                            		<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="146" width="146" alt="<c:out value='${productName}'/>" class="prodImage" />
                                                        		</c:when>
                                                        		<c:otherwise>
                                                            		<img src="${imagePath}/_assets/global/images/blank.gif" data-original="${scene7Path}/${imageURL}" alt="<c:out value='${productName}'/>" height="146" width="146" class="prodImage noImageFound loadingGIF" />
                                                        		</c:otherwise>
                                                 			</c:choose>
                                             			</c:otherwise>
                                             		
                                             		</c:choose>
                                                    
                                            </a>
                                            </c:when>
                                            <c:otherwise>
                                                    <c:choose>
                                                        <c:when test="${empty imageURL}">
                                                            <img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="146" width="146" alt="<c:out value='${productName}'/>" class="prodImage" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <img src="${imagePath}/_assets/global/images/blank.gif" data-original="${scene7Path}/${imageURL}" alt="<c:out value='${productName}'/>" height="146" width="146" class="prodImage noImageFound loadingGIF" />
                                                        </c:otherwise>
                                                    </c:choose>
                                            </c:otherwise>
                                            </c:choose>
                                        </div>
                                        
										
										<div class="productContainer  grid_10 omega">
											<%-- TODO don't need in redesign. delete later
											<ul class="productTab productHeading clearfix">
												<li class="productName">
													<c:if test="${upc ne null}">
														<div class="fl upc"><span class="bold"> <bbbl:label key='lbl_mng_regitem_upc' language="${pageContext.request.locale.language}" /> </span>
														<span>${upc}</span></div>
													</c:if>
												</li>
												<li class="price">Price</li>
												<li class="requested"><bbbl:label key='lbl_mng_regitem_purchased' language="${pageContext.request.locale.language}" /></li>
												<li class="purchase"><bbbl:label key='lbl_mng_regitem_requested' language="${pageContext.request.locale.language}" /></li>
												<li class="productLastColumn"> </li>
											</ul>
											--%>
											<dsp:getvalueof var="regItemOldQty" param="regItem.qtyRequested" />
												<dsp:getvalueof var="qtyPurchased" param="regItem.qtyPurchased"/>
												<dsp:getvalueof var="rowID" param="regItem.rowID"/>
												<dsp:getvalueof param="regItem.sKUDetailVO.intlRestricted" var="isIntlRestricted" />

													<dsp:getvalueof var="ltlItemFlag" param="regItem.sKUDetailVO.ltlItem" />
																					
											<div class="productTab productContent clearfix">												
												<dsp:include page="registry_items_owner_category_frag.jsp">
													<dsp:param name="personalisedCode" value="${personalisedCode}"/>
													<dsp:param name="personlisedPrice" value="${personlisedPrice}"/>	
													<dsp:param name="productId" value="${productId}"/>
													<dsp:param name="skuID" value="${skuID}"/>
													<dsp:param name="topFragment" value="${true}"/>	
													<dsp:param name="upc" value="${upc}"/>	
													<dsp:param name="size" value="${size}"/>
													<dsp:param name="color" value="${color}"/>													
													<dsp:param name="active" value="${active}"/>							
													<dsp:param name="personalizationType" value="${personalizationType}"/>
													<dsp:param name="refNum" value="${refNum}"/>
													<dsp:param name="customizationDetails" value="${customizationDetails}"/>
													<dsp:param name="eximCustomizationCodesMap" value="${eximCustomizationCodesMap}"/>
													<dsp:param name="customizedPrice1" value="${customizedPrice1}"/>
													<dsp:param name="personlisedPrice1" value="${personlisedPrice1}"/>
													<dsp:param name="personalizedImageUrls" value="${personalizedImageUrls}"/>
													<dsp:param name="personalizedImageUrlThumbs" value="${personalizedImageUrlThumbs}"/>
													<dsp:param name="productName" value="${productName}"/>
													<dsp:param name="contextPath" value="${contextPath}"/>
													<dsp:param name="finalUrl" value="${finalUrl}"/>
													<dsp:param name="registryId" value="${registryId}"/>
													<dsp:param name="customizedPrice" value="${customizedPrice}"/>
													<dsp:param name="ltlItemFlag" value="${ltlItemFlag}"/>															
														<dsp:param name="ltlShipMethodDesc" value="${ltlShipMethodDesc}"/>	
														<dsp:param name="deliverySurcharge" value="${deliverySurcharge}"/>	
														<dsp:param name="assemblyFee" value="${assemblyFee}"/>	
														<dsp:param name="shipMethodUnsupported" value="${shipMethodUnsupported}"/>	
														<dsp:param name="ltlShipMethod" value="${ltlShipMethod}"/>
														<dsp:param name="isDSLUpdateable" value="${isDSLUpdateable}"/>
														<dsp:param name="personalizationOptionsDisplay" value="${personalizationOptionsDisplay}"/>
													<dsp:param name="regAddress" value="${regAddress}"/>	
													<dsp:param name="regPublic" value="${regPublic}"/>	
													<dsp:param name="inCartFlagSKU" value="${inCartFlagSKU}" />
													<dsp:param name="regItemOldQty" value="${regItemOldQty}" />
													<dsp:param name="qtyPurchased" value="${qtyPurchased}" />
													<dsp:param name="rowID" value="${rowID}" />
													<dsp:param name="itemType" value="${itemType}" />
													<dsp:param name="eventType" value="${eventType}" />
													<dsp:param name="isBelowLineItem" value="${isBelowLineItem}" />
													<dsp:param name="isIntlRestricted" value="${isIntlRestricted}" />
													<dsp:param name="isInternationalCustomer" value="${isInternationalCustomer}" />
													<dsp:param name="enableLTLReg" value="${enableLTLReg}" />
												</dsp:include>	
												

												<div class="requested grid_2 omega">
													<span class="columnHeader"><bbbl:label key='lbl_mng_regitem_requested' language="${pageContext.request.locale.language}" /></span>
													<div class="input alpha marTop_5 clearfix spinner">
															<div class="text clearfix">
																<input type="hidden" name="itemIndex" value="${valueIndex}"/>
                                                                <%-- <dsp:input bean="GiftRegistryFormHandler.viewBean.purchasedQuantity" type="text"  title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" value="${qtyPurchased}" iclass="input_tiny_giftView fl itemQuantity"/>
																<input name="qty" type="hidden" value="${qtyPurchased}" data-change-store-submit="qty" data-change-store-errors="required digits nonZero" />  --%>
                                                                <c:set var="titleqt">
                                                                    <bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />
                                                                </c:set>

                                                                
                                                                <input type="hidden" name="regItemOldQty" id="regItemOnLoadQty" value="${regItemOldQty}" class="frmAjaxSubmitData" />
																<dsp:input type="hidden" bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].regItemOldQty" paramvalue="regItem.qtyRequested" id="oldquantityd"/>
																<a href="#" class="down button-Med btnSecondary" aria-label="Decrease quantity of ${productName}. Please select update once you have finalized your quantity.">
																	<span class="icon-fallback-text">
																		<span class="icon-minus" aria-hidden="true"></span>

																	</span>
																</a>
																<span class="incDec visuallyhidden" aria-live="assertive" aria-atomic="true"></span>
																<dsp:input bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].quantity" type="text" paramvalue="regItem.qtyRequested" title="${regItemOldQty} of ${productName}." iclass="valQtyGiftReg input_tiny_giftView fl itemQuantity" id="quantityPurchased">
                                                                    <dsp:tagAttribute name="data-change-store-errors" value="required digits nonZero"/>
                                                                    <dsp:tagAttribute name="data-change-store-submit" value="qty"/>
                                                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                                                    <dsp:tagAttribute name="aria-labelledby" value="${regItemOldQty} of ${productName}"/>
                                                                </dsp:input>
																<a href="#" class="up button-Med btnSecondary" aria-label="Increase quantity of ${productName}. Please select update once you have finalized your quantity. ">
																	<span class="icon-fallback-text">
																		<span class="icon-plus" aria-hidden="true"></span>

																	</span>
																</a>

															</div>
														</div>
												</div>
												<div class="grid_1 purchase">
													<span class="columnHeader"><bbbl:label key='lbl_mng_regitem_purchased' language="${pageContext.request.locale.language}" /></span>
													<dsp:valueof param="regItem.qtyPurchased" />
													<dsp:input id="sa_vc" type="hidden" bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].purchasedQuantity" paramvalue="regItem.qtyPurchased" />
												</div>
												<div class="productLastColumn grid_2 omega">

                                                    <dsp:input bean="GiftRegistryFormHandler.value.registryId" type="hidden" paramvalue="registryId" />
                                                    <input type="hidden" name="registryId" value="${registryId}" class="registryId frmAjaxSubmitData" />
                                                    <dsp:input bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].rowId" type="hidden" paramvalue="regItem.rowID"/>
                                                    <dsp:input bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].sku" type="hidden" paramvalue="regItem.sku"/>
                                                    <dsp:getvalueof var="skuId" param="regItem.sku" />
                                                    <dsp:getvalueof var="rowId" param="regItem.rowID" />
                                                    <input type="hidden" name="skuId" value="${skuId}" class="skuId frmAjaxSubmitData" />
                                                    <input type="hidden" name="rowId" value="${rowId}" class="frmAjaxSubmitData" />
                                                   <c:choose>
                                                    	<c:when test="${not empty ltlShipMethod}">
                                                    	<input type="hidden" name="ltlDeliveryServices" value="${ltlShipMethod}" class="frmAjaxSubmitData" />
                                                    	</c:when>
                                                    	<c:otherwise>
                                                    	<input type="hidden" name="ltlDeliveryServices" value="" class="frmAjaxSubmitData" />
                                                    	</c:otherwise>
                                                    </c:choose>
                                                     <input type="hidden" name="ltlDeliveryPrices" value="${deliverySurcharge}" class="frmAjaxSubmitData" />
                                                     <input type="hidden" name="itemTypes" value="${itemType}" class="frmAjaxSubmitData" />
                                                    <input type="hidden" name="refNum" value="${refNum}" class="frmAjaxSubmitData" />
                                                    <input type="hidden" name="personalizationCode" value="${personalizationType}" class="frmAjaxSubmitData" />
                                                    <input type="hidden" name="alternateNum" value="" class="frmAjaxSubmitData" />
                                                    <input type="hidden" name="updateDslFromModal" value="" class="frmAjaxSubmitData" />
                                                    <dsp:droplet name="GiftRegistryFetchProductIdDroplet">
                                                        <dsp:param name="skuId" value="${skuId}" />
                                                        <dsp:oparam name="output">
                                                            <dsp:getvalueof var="productId" param="productId" />
                                                            <dsp:input bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].productId" type="hidden" paramvalue="productId"/>
                                                            <input type="hidden" name="prodId" value="${productId}"  class="frmAjaxSubmitData"/>
                                                        </dsp:oparam>
                                                        <dsp:oparam name="error">
                                                            <dsp:getvalueof param="errorMsg"  var="errorMsg"/>
                                                                    <div class="error"><bbbe:error key="${errorMsg}" language="${pageContext.request.locale.language}"/></div>
                                                        </dsp:oparam>
                                                    </dsp:droplet>
                                                     <dsp:input bean="GiftRegistryFormHandler.queryParam"  type="hidden" value="registryId=${registryId}&eventType=${eventType}" />
                                                    <%-- Client DOM XSRF | Part -1
                                                    <dsp:getvalueof var="ownerUrl" value="${contextPath}/giftregistry/view_registry_owner.jsp?registryId=${registryId}&eventType=${eventType}"/>
                                                    <dsp:input bean="GiftRegistryFormHandler.successURL"  type="hidden" value="${ownerUrl}" />
                                                    <dsp:input bean="GiftRegistryFormHandler.errorURL"	type="hidden" value="${ownerUrl}" /> --%>
                                                    <dsp:input bean="GiftRegistryFormHandler.fromPage" type="hidden" value="updateItemRegistry" />
                                                    <dsp:input bean="GiftRegistryFormHandler.updateItemToGiftRegistry"
                                                        id="update${idxCat}${idx}" 	type="submit" style="display:none" value="Update"	/>
                                                    <dsp:input bean="GiftRegistryFormHandler.removeItemFromGiftRegistry"
                                                        id="remove${idxCat}${idx}" 	type="submit" style="display:none"  value="Remove" />
                                                    <c:set var="valueIndex" value="${valueIndex + 1}"/>
													<div class="">
														<a href="#" data-trigger-button="update${idxCat}${idx}" class="validateQuantity  button-Med btnPrimary" onclick="omniRegistryUpdateItem('${productId}','${eventType}','${registryId}', '${skuId}');" ><span class="txtOffScreen">Update Quantity of${productName} &nbsp. Please select update once you have finalized your quantity </span><bbbl:label key='lbl_mng_regitem_update' language="${pageContext.request.locale.language}" /></a>
													</div>

													<div class="clear"></div>
													<dsp:getvalueof param="regItem.qtyRequested" var="requestedQuantity" />
													<dsp:getvalueof param="regItem.qtyPurchased" var="purchasedQuantity" />
													<dsp:getvalueof param="regItem.sKUDetailVO.intlRestricted" var="isIntlRestricted" />

													<%-- LTL-328 Hide add to cart button and show not availability message for LTL item --%>
													<dsp:getvalueof var="ltlItemFlag" param="regItem.sKUDetailVO.ltlItem" />
													<c:choose>
														<c:when test="${(isBelowLineItem eq 'false')}">
															<c:choose>
																<c:when test="${(requestedQuantity > purchasedQuantity) || (requestedQuantity == 0 && purchasedQuantity ==0)}">
																	<div class="addToCart marTop_5 <c:if test="${isInternationalCustomer && (isIntlRestricted || not empty refNum) }">disabled</c:if>">
			                                                         	<c:set var="lblAddTOCart"><bbbl:label key='lbl_mng_regitem_additemcart' language="${pageContext.request.locale.language}" /></c:set>
			                                                         	<%-- if no shipping address available, add to cart brings up edit modal--%>                                                         	
			                                                            <input class="button-Med btnSecondary <c:if test="${empty regAddress}">editRegInfo completeShipping</c:if>" type="button" name="<c:if test="${not empty regAddress}">btnAddToCart</c:if>" title="${lblAddTOCart}" aria-label="Add ${productName} to your cart" value="${lblAddTOCart}" role="button" <c:if test="${isInternationalCustomer && (isIntlRestricted || not empty refNum)}">disabled="disabled"</c:if>/>
		
																	</div>
																	<c:if test="${isInternationalCustomer && isIntlRestricted}">
									                               		<div class="notAvailableIntShipMsg regIntShipMsg cb clearfix"><bbbl:label key='lbl_reg_intl_restricted_message' language="${pageContext.request.locale.language}" /></div>
								                                   </c:if>
																</c:when>
													            <c:otherwise>
																	<div class="purchasedConfirmation">
																		<span class="icon-fallback-text">
																			<span class="icon-checkmark" aria-hidden="true"></span>
																			<span class="icon-text"><bbbl:label key='lbl_mng_regitem_purchased_confirm' language="${pageContext.request.locale.language}" /></span>
																		</span>
															 			<bbbl:label key='lbl_mng_regitem_purchased_confirm' language="${pageContext.request.locale.language}" />
																	</div>
																</c:otherwise>
													        </c:choose>

																			<%-- <dsp:droplet name="IsProductSKUShippingDroplet">
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
																		</c:when>
														<c:otherwise>
															<span class="itemNotAvailable padTop_10 block"><bbbl:label key='lbl_mng_item_belowline' language="${pageContext.request.locale.language}" /></span>
														</c:otherwise>
													</c:choose>

													<c:choose>
														<c:when test="${not empty refNum && refNum ne null}">
															<c:choose>
																<c:when test="${purchasedQuantity gt 0}">
																	<c:set var="hideRemoveButtonCustomProd" value="true" scope="request"/>
																</c:when>
																<c:otherwise>
																	<c:set var="hideRemoveButtonCustomProd" value="false" scope="request"/>
																</c:otherwise>
															</c:choose>
														</c:when>
														<c:otherwise>
															<c:set var="hideRemoveButtonCustomProd" value="false" scope="request"/>
														</c:otherwise>
													</c:choose>
													<a href="#" data-submit-button="remove${idxCat}${idx}" role="link" aria-label="Remove quantity ${lblForThe} ${productName} from cart" class="btnAjaxSubmitRegistry <c:if test="${hideRemoveButtonCustomProd}"> hidden </c:if>  buttonTextLink" onclick="omniRegistryRemoveItem('${productId}','${eventType}','${registryId}', '${skuId}', '${price}', '${qtyPurchased}');">
														<span class="icon-fallback-text">
															<span class="icon-times" aria-hidden="true"></span>
															<span class="icon-text"><bbbl:label key="lbl_remove_personalization" language="${pageContext.request.locale.language}"/> ${productName} <bbbl:label key="lbl_from_cart" language="${pageContext.request.locale.language}"/></span>
														</span>
													<bbbl:label key='lbl_mng_regitem_remove' language="${pageContext.request.locale.language}" />
													</a>

													<c:set var="writeReviewOn" scope="request"><bbbc:config key="writeReviewOn" configName="FlagDrivenFunctions" /></c:set>
													<c:if test="${writeReviewOn}">
														<dsp:droplet name="/com/bbb/commerce/browse/droplet/ProductStatusDroplet">
															<dsp:param name="productId" value="${productId}" />
															<dsp:oparam name="output">
																	<dsp:getvalueof var="isProductActive" param="isProductActive" />
															</dsp:oparam>
														</dsp:droplet>
														<c:if test="${isProductActive && purchasedQuantity>0}">
														<a  href="#" title="Write a review" role="button" data-BVProductId="${productId}" class="buttonTextLink triggerBVsubmitReview" onclick="javascript:s_crossSell('write a review - registry');">
																<span><svg width="11" height="11"><use xlink:href="#icon-star-icon_fill"></use></svg></span>
															<bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" />
														</a>
															<div class="ugc_sa" id="sa_${productId}" data-prodId="${productId}"></div>
														</c:if>
													</c:if>
												</div>
											</div>

											<div class="productFooter clearfix">
												
													<div class="noMar clearfix freeShipContainer prodAttribWrapper">

														<dsp:droplet name="/atg/dynamo/droplet/ForEach">
															<dsp:param name="array" param="regItem.sKUDetailVO.skuAttributes" />
															<dsp:param name="elementName" value="attributeVOList"/>
															<dsp:oparam name="output">
																<dsp:getvalueof var="pageName" param="key" />
																<c:if test="${pageName eq 'RLP'}">
																	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																	<dsp:param param="attributeVOList" name="array" />
																	<dsp:param name="elementName" value="attributeVO"/>
																	<%-- <dsp:param name="sortProperties" value="+priority"/> --%>
																		<dsp:oparam name="output">
																            <c:set var="currentCount"><dsp:valueof param="count" /></c:set>
																            <c:set var="actionURL"><dsp:valueof param="attributeVO.actionURL" /></c:set>
																            <c:set var="attributeName"><dsp:valueof param="attributeVO.attributeName" /></c:set>
																				<c:if test="${not empty actionURL}">
																           			<c:choose>
																						<c:when test="${not empty skuID && null ne attributeName && fn:contains(attributeName,vdcAttributesList)}">
																						  	<div <c:if test="${fn:contains(actionURL,'ltlDeliveryInfo') }">class="ltlTruck"</c:if>><a href="/store/browse/dynamicStaticContent.jsp?pageName=ItemShippedDirectlyFromVendor&isVdcSku=true&skuId=${skuID}" class="popup"><span><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></span></a></div>
																						</c:when>
																						<c:otherwise>
																							<div <c:if test="${fn:contains(actionURL,'ltlDeliveryInfo') }">class="ltlTruck"</c:if>><a href="${actionURL}?skuID=${skuID}" class="popup"><span><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></span></a></div>	
																						</c:otherwise>																					
																					</c:choose>
																					<c:choose>
											                          				<c:when test="${currentCount%2 == 0}">
															            				<c:set var="nextStyle">prodSecondaryAttribute</c:set>
															            			</c:when>
															            			<c:otherwise>
																						<c:set var="nextStyle">prodSecondaryAttribute</c:set>
																					</c:otherwise>
															            			</c:choose>
																				</c:if>
																		</dsp:oparam>
																	</dsp:droplet>
																	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																	<dsp:param param="attributeVOList" name="array" />
																	<dsp:param name="elementName" value="attributeVO"/>
																	<dsp:param name="sortProperties" value="+priority"/>
																		<dsp:oparam name="output">
																            <c:set var="currentCount"><dsp:valueof param="count" /></c:set>
																            <c:set var="actionURL"><dsp:valueof param="attributeVO.actionURL" /></c:set>
																            <c:set var="attributeName"><dsp:valueof param="attributeVO.attributeName" /></c:set>
																			<c:if test="${empty actionURL}">
																				<div><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></div>
																			</c:if>
																			<c:choose>
											                          			<c:when test="${currentCount%2 == 0}">
															            			<c:set var="nextStyle">prodSecondaryAttribute</c:set>
															            		</c:when>
															            		<c:otherwise>
																					<c:set var="nextStyle">prodSecondaryAttribute</c:set>
																				</c:otherwise>
															            	</c:choose>
																		</dsp:oparam>
																	</dsp:droplet>
																</c:if>
															</dsp:oparam>
														</dsp:droplet>
                                                        <%-- <c:if test="${skuDetailVO.shippingSurcharge gt 0.0}">
															<li class="${nextStyle}"><bbbl:label key="lbl_mng_surcharge" language="<c:out param='${language}'/>"/>${skuDetailVO.shippingSurcharge}</li>
														</c:if> --%>

													</div>
												
											</div>

										</div>

									</li>
								</dsp:oparam>

							</dsp:droplet>
                                 </ul>
                            </div>
                        </c:if>
							<%-- <c:if test="${count ne 0 }"><li class="noBorder"><a href="#backToTop"><bbbl:label key='lbl_mng_regitem_back_to_top' language="${pageContext.request.locale.language}" /></a></li></c:if> --%>
						</c:if>
					</dsp:oparam>
				</dsp:droplet>
			</c:if>
		</dsp:oparam>
		<dsp:oparam name="outputEnd">
			<dsp:droplet name="ForEach">
				<dsp:param name="array" param="categoryBuckets" />
				<dsp:oparam name="output">
					<dsp:getvalueof var="bucketName" param="key" idtype="java.lang.String" />
					<c:if test="${bucketName eq othersCat}">
						<dsp:getvalueof var="bucket" param="categoryBuckets.${othersCat}" idtype="java.util.List" />
						<dsp:getvalueof var="count" value="${fn:length(bucket)}"/>
						<dsp:getvalueof var="catName" param="categoryVOMap.${othersCat}.catName"/>
                    <c:if test="${count gt 0}">
							<h2 class="ui-accordion-header ui-helper-reset ui-state-default ui-corner-all"><span class="ui-icon ui-icon-triangle-1-e"></span><span class="accordionTitle block clearfix giftTitle"><a href="#" class="fl accordionLink">${fn:toLowerCase(catName)}&nbsp;(${count})</a></span></h2>

                        <div class="accordionDiv ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom">
                            <ul class="giftViewProduct productDetailList">
						<dsp:droplet name="ForEach">
							<dsp:param name="array" param="categoryBuckets.${othersCat}" />
							<dsp:param name="elementName" value="regItem" />

							<dsp:oparam name="output">
								<dsp:getvalueof var="skuID" vartype="java.lang.String"  param="regItem.sku" />
								<dsp:droplet name="GiftRegistryFetchProductIdDroplet">
									<dsp:param name="skuId" value="${skuID}" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="productId" vartype="java.lang.String" param="productId" />
										<dsp:getvalueof var="inStoreSku" param="inStore" />
									</dsp:oparam>
								</dsp:droplet>
								<c:choose>
									<c:when test="${itemList ne ''}"><c:set var="itemList" scope="request">${itemList};${productId}</c:set></c:when>
									<c:otherwise><c:set var="itemList" scope="request">${productId}</c:set></c:otherwise>
								</c:choose>
								<li class="grid_12 alpha omega qtyWrapper productRow btngiftViewTopMargin giftViewProductChangeStoreWrapper addToCartGiftRegWrapper clearfix">
								<dsp:getvalueof var="isBelowLineItem" param="regItem.isBelowLineItem" />
								<dsp:getvalueof var="disable" param="regItem.sKUDetailVO.disableFlag" />
								<dsp:getvalueof var="webOffered" param="regItem.sKUDetailVO.webOfferedFlag" />
								<dsp:getvalueof var="active" param="regItem.sKUDetailVO.activeFlag" />
								<dsp:getvalueof var="idx" param="index"/>
								<dsp:getvalueof var="ltlItemFlag" param="regItem.sKUDetailVO.ltlItem" />
								<dsp:getvalueof var="ltlShipMethod" param="regItem.ltlDeliveryServices" />
								<dsp:getvalueof var="personalizedImageUrls" param="regItem.personalizedImageUrls"/>
								<dsp:getvalueof var="personalizedImageUrlThumbs" param="regItem.personalizedImageUrlThumbs"/>
								<dsp:getvalueof var="personalizationOptionsDisplay" param="regItem.personalizationOptionsDisplay"/>
								<dsp:getvalueof var="personalisedCode" param="regItem.personalisedCode"/>
								<dsp:getvalueof var="customizedPrice" vartype="java.lang.Double" param="regItem.customizedDoublePrice"/>
								<dsp:getvalueof var="personlisedPrice" vartype="java.lang.Double" param="regItem.personlisedPrice"/>	
								<c:set var="customizedPrice1"><dsp:valueof value="${customizedPrice}" converter="currency"/></c:set>
	                                    <c:set var="personlisedPrice1"><dsp:valueof value="${personlisedPrice}" converter="currency"/></c:set>
										 <dsp:getvalueof var="itemType" param="regItem.itemType"/>
										 <dsp:getvalueof var="personalizationType" param="regItem.sKUDetailVO.personalizationType" />
								<dsp:getvalueof var="refNum" param="regItem.refNum" />
								<dsp:getvalueof var="personalizationType" param="regItem.sKUDetailVO.personalizationType" />
								<dsp:getvalueof var="customizationDetails" param="regItem.customizationDetails"/>		
								<dsp:getvalueof var="skuDetailVO" param="regItem.sKUDetailVO"/>		
								<dsp:getvalueof var="upc" param="regItem.sKUDetailVO.upc" />
								<dsp:getvalueof var="color" param="regItem.sKUDetailVO.color" />
								<dsp:getvalueof var="size" param="regItem.sKUDetailVO.size" />
								<dsp:getvalueof var="ltlShipMethodDesc" param="regItem.ltlShipMethodDesc"/>
								<dsp:getvalueof var="deliverySurcharge" param="regItem.deliverySurcharge"/>
								<dsp:getvalueof var="ltlAssemblySelected" param="regItem.assemblySelected"/>
								<dsp:getvalueof var="assemblyFee" param="regItem.assemblyFees"/>
								<dsp:getvalueof var="shipMethodUnsupported" param="regItem.shipMethodUnsupported"/>	
								<dsp:getvalueof var="isDSLUpdateable" param="regItem.dslUpdateable"/>
								<dsp:getvalueof var="inCartFlagSKU" param="regItem.sKUDetailVO.inCartFlag" />
								
								<c:if test="${not empty personalisedCode}">
											<c:set var="cusDet">${eximCustomizationCodesMap[personalisedCode]}</c:set>									
											<input type="hidden" value="${cusDet}" id="customizationDetails"/> 
										</c:if>
                                    

											<c:if test="${ltlItemFlag && isPOBoxAddress}">
												<div class="ltlMessage"><bbbt:textArea key="txt_ltl_shipping_not_available" language ="${pageContext.request.locale.language}"/></div>
											</c:if>
											<dsp:getvalueof var="notifyRegistrantMsgTyp" param="regItem.displayNotifyRegistrantMsg"/>										
											<c:if test="${not empty notifyRegistrantMsgTyp}">
												<div class="notifyRegistrantMessage"> <bbbl:label key="notifyRegMsg_Status${notifyRegistrantMsgTyp}" language="${pageContext.request.locale.language}" /> </div>
											</c:if>
                                            <input type="hidden" name="bts" value="false" class="addToCartSubmitData" />
											<c:if test="${ltlItemFlag && (ltlShipMethod eq null || ltlShipMethod eq '')}">
												<div class="ltlMessage"><bbbl:label key='lbl_rlp_ltl_registry_message' language="${pageContext.request.locale.language}" />
												<c:if test="${isDSLUpdateable}"><a class="updateDsl"><bbbl:label key='lbl_update_service_level' language="${pageContext.request.locale.language}" /></a></c:if></div>
											</c:if>
											<c:if test="${ltlItemFlag && not empty ltlShipMethod && shipMethodUnsupported}">
												
											<c:set target="${placeHolderMapServiceLevel}" property="ShippingMethodDesc" value="${ltlShipMethodDesc}"/>
											<div class="clear"></div>
											<div class="ltlMessage marBottom_10 marTop_10" >
											<bbbt:textArea key="txt_ship_method_not_supported_rlp" placeHolderMap="${placeHolderMapServiceLevel}" language ="${pageContext.request.locale.language}"/>
											<c:if test="${isDSLUpdateable}"><a class="updateDsl"><bbbl:label key='lbl_update_service_level' language="${pageContext.request.locale.language}" /></a></c:if>
											</div>
											</c:if>
											<c:if test="${ltlItemFlag && (ltlShipMethod ne null && ltlShipMethod ne '')}">
											<c:set var="ltlOpt" value="&sopt=${ltlShipMethod}"></c:set>
											</c:if>	
											<c:set var="productName"><dsp:valueof param="regItem.sKUDetailVO.displayName" valueishtml="true"/></c:set>
											
											<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
												<dsp:param name="id" value="${productId}"/>
												<dsp:param name="itemDescriptorName" value="product" />
												<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
												<dsp:oparam name="output">
													<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
												</dsp:oparam>
											</dsp:droplet>
											<div class="productImage grid_2 alpha">
												<c:choose>
													<c:when test="${active}">
														<a class="prodImg ${productLinkClass}" href="${contextPath}${finalUrl}?skuId=${skuID}&registryId=${registryId}${ltlOpt}"
														data-pdp-qv-url="pdp_quick_view.jsp" data-skuId="${skuID}" data-registryId="${registryId}" data-productId="${productId}" data-sopt="${ltlShipMethod}" data-refNum="${refNum}"  title="<c:out value='${productName}'/>" <c:if test="${not empty refNum}">data-personalisedCode="${personalisedCode}"
															data-customizedPrice="${customizedPrice1}" data-customizationDetails="${customizationDetails}" data-color="${color}" data-size="${size}" data-personalizedImageUrls="${personalizedImageUrls}"
															data-personlisedPrice="${personlisedPrice1}" data-personalizedImageUrlThumbs="${personalizedImageUrlThumbs}" data-personalizationType= "${personalizationType}"</c:if>>
															<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.mediumImage" />
															<c:choose>
                                             			    <c:when test="${not empty refNum}">
                                             				<c:choose>
                                             					<c:when test="${not empty personalizedImageUrls}">
                                             						<img src="${personalizedImageUrls}" height="96" width="96" alt="<c:out value='${productName}'/>" class="prodImage" onerror="this.src = '${imagePath}/_assets/global/images/no_image_available.jpg';">
                                             					</c:when>
                                             					<c:when test="${not empty imageURL}">                                                       			 
                                                            		<img src="${imagePath}/_assets/global/images/blank.gif" data-original="${scene7Path}/${imageURL}" alt="<c:out value='${productName}'/>" height="146" width="146" class="prodImage noImageFound loadingGIF" />
                                                        		</c:when>
                                             					<c:otherwise>
                                             						<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="146" width="146" alt="<c:out value='${productName}'/>" class="prodImage" />                                             						
                                             					</c:otherwise>
                                             				</c:choose>
															</c:when>
                                             			    <c:otherwise>
                                             				<c:choose>
                                                       			 <c:when test="${empty imageURL}">                                                       			 
                                                            		<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="146" width="146" alt="<c:out value='${productName}'/>" class="prodImage" />
                                                        		</c:when>
                                                        		<c:otherwise>
                                                            		<img src="${imagePath}/_assets/global/images/blank.gif" data-original="${scene7Path}/${imageURL}" alt="<c:out value='${productName}'/>" height="146" width="146" class="prodImage noImageFound loadingGIF" />
                                                        		</c:otherwise>
                                                 			</c:choose>
                                             			    </c:otherwise>
                                             		        </c:choose>
														</a>
													</c:when>
													<c:otherwise>
														<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.mediumImage" />
														<c:choose>
															<c:when test="${empty imageURL}">
																<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="146" width="146"  alt="<c:out value='${productName}'/>" class="prodImage" />
															</c:when>
															<c:otherwise>
																<img src="${imagePath}/_assets/global/images/blank.gif" data-original="${scene7Path}/${imageURL}" alt="<c:out value='${productName}'/>" height="146" width="146"  class="prodImage noImageFound loadingGIF" />
															</c:otherwise>
														</c:choose>
													</c:otherwise>
												</c:choose>
											</div>
											<div class="productContainer grid_10 omega">

												<%-- TODO don't need in redesign. delete later
												<ul class="productTab productHeading clearfix">
													<li class="productName">
														<c:if test="${upc ne null}">
															<div class="fl upc"><span class="bold"> <bbbl:label key='lbl_mng_regitem_upc' language="${pageContext.request.locale.language}" /> </span>
															<span>${upc}</span></div>
														</c:if>
														<c:if test="${(not empty upc) && (not empty color)}">
															<div class="fl sep">|</div>
														</c:if>
													</li>
													<li class="price">Price</li>
													<li class="requested"><bbbl:label key='lbl_mng_regitem_purchased' language="${pageContext.request.locale.language}" /></li>
													<li class="purchase"><bbbl:label key='lbl_mng_regitem_requested' language="${pageContext.request.locale.language}" /></li>
													<li class="productLastColumn"> </li>
												</ul>
												--%>
												<div class="productTab productContent clearfix">													
												<dsp:getvalueof var="regItemOldQty" param="regItem.qtyRequested" />
												<dsp:getvalueof var="qtyPurchased" param="regItem.qtyPurchased"/>
												<dsp:getvalueof var="rowID" param="regItem.rowID"/>
												<dsp:getvalueof param="regItem.sKUDetailVO.intlRestricted" var="isIntlRestricted" />

													<dsp:getvalueof var="ltlItemFlag" param="regItem.sKUDetailVO.ltlItem" />
													<dsp:include page="registry_items_owner_category_frag.jsp">
														<dsp:param name="personalisedCode" value="${personalisedCode}"/>
														<dsp:param name="personlisedPrice" value="${personlisedPrice}"/>	
														<dsp:param name="productId" value="${productId}"/>
														<dsp:param name="skuID" value="${skuID}"/>
														<dsp:param name="topFragment" value="false"/>	
														<dsp:param name="upc" value="${upc}"/>	
														<dsp:param name="size" value="${size}"/>
														<dsp:param name="color" value="${color}"/>
														<dsp:param name="active" value="${active}"/>							
														<dsp:param name="personalizationType" value="${personalizationType}"/>
														<dsp:param name="refNum" value="${refNum}"/>
														<dsp:param name="customizationDetails" value="${customizationDetails}"/>
														<dsp:param name="eximCustomizationCodesMap" value="${eximCustomizationCodesMap}"/>
														<dsp:param name="customizedPrice1" value="${customizedPrice1}"/>
														<dsp:param name="personlisedPrice1" value="${personlisedPrice1}"/>
														<dsp:param name="personalizedImageUrls" value="${personalizedImageUrls}"/>
														<dsp:param name="personalizedImageUrlThumbs" value="${personalizedImageUrlThumbs}"/>
														<dsp:param name="productName" value="${productName}"/>
														<dsp:param name="contextPath" value="${contextPath}"/>
														<dsp:param name="finalUrl" value="${finalUrl}"/>
														<dsp:param name="registryId" value="${registryId}"/>
														<dsp:param name="customizedPrice" value="${customizedPrice}"/>
														<dsp:param name="ltlItemFlag" value="${ltlItemFlag}"/>	
														<dsp:param name="ltlShipMethodDesc" value="${ltlShipMethodDesc}"/>	
														<dsp:param name="deliverySurcharge" value="${deliverySurcharge}"/>	
														<dsp:param name="assemblyFee" value="${assemblyFee}"/>	
														<dsp:param name="shipMethodUnsupported" value="${shipMethodUnsupported}"/>	
														<dsp:param name="ltlShipMethod" value="${ltlShipMethod}"/>
														<dsp:param name="isDSLUpdateable" value="${isDSLUpdateable}"/>
														<dsp:param name="personalizationOptionsDisplay" value="${personalizationOptionsDisplay}"/>	
														<dsp:param name="regAddress" value="${regAddress}"/>	
														<dsp:param name="regPublic" value="${regPublic}"/>
														<dsp:param name="currentSiteId" value="${currentSiteId}"/>
														<dsp:param name="inCartFlagSKU" value="${inCartFlagSKU}" />
														<dsp:param name="inCartFlagSKU" value="${inCartFlagSKU}" />
														<dsp:param name="regItemOldQty" value="${regItemOldQty}" />
													<dsp:param name="qtyPurchased" value="${qtyPurchased}" />
													<dsp:param name="rowID" value="${rowID}" />
													<dsp:param name="itemType" value="${itemType}" />
													<dsp:param name="eventType" value="${eventType}" />
													<dsp:param name="isBelowLineItem" value="${isBelowLineItem}" />
													<dsp:param name="isIntlRestricted" value="${isIntlRestricted}" />
													<dsp:param name="isInternationalCustomer" value="${isInternationalCustomer}" />
													<dsp:param name="enableLTLReg" value="${enableLTLReg}" />
													</dsp:include>	
													
													<div class="requested grid_2 omega">
														<span class="columnHeader"><bbbl:label key='lbl_mng_regitem_requested' language="${pageContext.request.locale.language}" /></span>
														<div class="input alpha marTop_5 clearfix spinner">
																<div class="text clearfix">
                                                                    <%-- <dsp:input bean="GiftRegistryFormHandler.viewBean.purchasedQuantity" type="text"  title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" value="${qtyPurchased}" iclass="input_tiny_giftView fl itemQuantity"/>
																	<input name="qty" type="hidden" value="${qtyPurchased}" data-change-store-submit="qty" data-change-store-errors="required digits nonZero" />  --%>
                                                                    <c:set var="titleqt">
                                                                        <bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />
                                                                    </c:set>

                                                                  	
                                                                  	<input type="hidden" name="regItemOldQty" id="regItemOnLoadQty" value="${regItemOldQty}" class="frmAjaxSubmitData" />

																	<dsp:input type="hidden" bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].regItemOldQty" paramvalue="regItem.qtyRequested" id="oldquantityd"/>


																	<a href="#" class="down button-Med btnSecondary" aria-label="Decrease quantity of ${productName}. Please select update once you have finalized your quantity.">
																		<span class="icon-fallback-text">
																			<span class="icon-minus" aria-hidden="true"></span>

																		</span>
																	</a>
																	<span class="incDec visuallyhidden" aria-live="assertive" aria-atomic="true"></span>
																	<dsp:input bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].quantity" type="text" paramvalue="regItem.qtyRequested" title="${regItemOldQty} of ${productName}." iclass="valQtyGiftReg input_tiny_giftView fl itemQuantity" id="quantityPurchased">
	                                                                    <dsp:tagAttribute name="data-change-store-errors" value="required digits nonZero"/>
	                                                                    <dsp:tagAttribute name="data-change-store-submit" value="qty"/>
	                                                                    <dsp:tagAttribute name="aria-required" value="false"/>
	                                                                    <dsp:tagAttribute name="aria-labelledby" value="${regItemOldQty} of ${productName}"/>
	                                                                </dsp:input>
																	<a href="#" class="up button-Med btnSecondary" aria-label="Increase quantity of ${productName}. Please select update once you have finalized your quantity.">
																		<span class="icon-fallback-text">
																			<span class="icon-plus" aria-hidden="true"></span>

																		</span>
																	</a>

																</div>
															</div>
													</div>
													<div class="grid_1 purchase">
														<span class="columnHeader"><bbbl:label key='lbl_mng_regitem_purchased' language="${pageContext.request.locale.language}" /></span>
														<dsp:valueof param="regItem.qtyPurchased" />
														<dsp:input type="hidden" bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].purchasedQuantity" paramvalue="regItem.qtyPurchased" />
													</div>

													<div class="productLastColumn grid_2 omega">
                                                        <input type="hidden" name="regItemOldQty" id="regItemOnLoadQty" value="${regItemOldQty}" class="frmAjaxSubmitData" />
                                                        <dsp:input bean="GiftRegistryFormHandler.value.registryId" type="hidden" paramvalue="registryId" />
                                                        <input type="hidden" name="registryId" value="${registryId}" class="registryId frmAjaxSubmitData" />
                                                        <dsp:input bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].rowId" type="hidden" paramvalue="regItem.rowID"/>
                                                        <dsp:input bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].sku" type="hidden" paramvalue="regItem.sku"/>
                                                        <dsp:getvalueof var="skuId" param="regItem.sku" />
                                                    	<dsp:getvalueof var="rowId" param="regItem.rowID" />
                                                    	<input type="hidden" name="itemTypes" value="${itemType}" class="frmAjaxSubmitData" />
                                                        <input type="hidden" name="skuId" value="${skuId}" class="skuId frmAjaxSubmitData" />
                                                    	<input type="hidden" name="rowId" value="${rowId}" class="frmAjaxSubmitData" />
                                                    	<c:choose>
                                                    	<c:when test="${not empty ltlShipMethod}">
                                                    	<input type="hidden" name="ltlDeliveryServices" value="${ltlShipMethod}" class="frmAjaxSubmitData" />
                                                    	</c:when>
                                                    	<c:otherwise>
                                                    	<input type="hidden" name="ltlDeliveryServices" value="" class="frmAjaxSubmitData" />
                                                    	</c:otherwise></c:choose>
                                                    	 <input type="hidden" name="ltlDeliveryPrices" value="${deliverySurcharge}" class="frmAjaxSubmitData" />
                                                    	<input type="hidden" name="refNum" value="${refNum}" class="frmAjaxSubmitData" />
                                                    	<input type="hidden" name="personalizationCode" value="${personalizationType}" class="frmAjaxSubmitData" />
														<dsp:getvalueof var="ltlShipMethod" param="regItem.ltlDeliveryServices" />
														<input type="hidden" name="alternateNum" value="" class="frmAjaxSubmitData"/>
														<input type="hidden" name="updateDslFromModal" value="" class="frmAjaxSubmitData"/>
                                                        <dsp:droplet name="GiftRegistryFetchProductIdDroplet">
                                                            <dsp:param name="skuId" value="${skuId}" />
                                                            <dsp:oparam name="output">
                                                                <dsp:getvalueof var="productId" param="productId" />
                                                                <dsp:input bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].productId" type="hidden" paramvalue="productId"/>
                                                                <input type="hidden" name="prodId" value="${productId}" class="frmAjaxSubmitData"/>
                                                            </dsp:oparam>
                                                            <dsp:oparam name="error">
                                                                <dsp:getvalueof param="errorMsg"  var="errorMsg"/>
                                                                <div class="error"><bbbe:error key="${errorMsg}" language="${pageContext.request.locale.language}"/></div>
                                                            </dsp:oparam>
                                                        </dsp:droplet>
                                                        <dsp:input bean="GiftRegistryFormHandler.queryParam"  type="hidden" value="registryId=${registryId}&eventType=${eventType}" />
                                                        <%-- Client DOM XSRF | Part -1
                                                        <dsp:getvalueof var="ownerUrl" value="${contextPath}/giftregistry/view_registry_owner.jsp?registryId=${registryId}&eventType=${eventType}"/>
                                                        <dsp:input bean="GiftRegistryFormHandler.successURL"  type="hidden" value="${ownerUrl}" />
                                                        <dsp:input bean="GiftRegistryFormHandler.errorURL"	type="hidden" value="${ownerUrl}" /> --%>
                                                          <dsp:input bean="GiftRegistryFormHandler.fromPage" type="hidden" value="updateItemRegistry" />
                                                        <dsp:input bean="GiftRegistryFormHandler.updateItemToGiftRegistry" id="update${idxCat}${idx}" 	type="submit" style="display:none" value="Update"	/>
                                                        <dsp:input bean="GiftRegistryFormHandler.removeItemFromGiftRegistry" id="remove${idxCat}${idx}" 	type="submit" style="display:none"  value="Remove" />
                                                        <c:set var="valueIndex" value="${valueIndex + 1}"/>
														<div class="">
															<a href="#" data-trigger-button="update${idxCat}${idx}" class="validateQuantity  button-Med btnPrimary" onclick="omniRegistryUpdateItem('${productId}','${eventType}','${registryId}', '${skuId}');"><span class="txtOffScreen">Update Quantity of${productName} &nbsp. Please select update once you have finalized your quantity </span><bbbl:label key='lbl_mng_regitem_update' language="${pageContext.request.locale.language}" /></a>
														</div>

														<div class="clear"></div>
														<dsp:getvalueof param="regItem.qtyRequested" var="requestedQuantity" />
														<dsp:getvalueof param="regItem.qtyPurchased" var="purchasedQuantity" />
														<dsp:getvalueof param="regItem.sKUDetailVO.intlRestricted" var="isIntlRestricted" />
														<%-- LTL-328 Hide add to cart button and show not availability message for LTL item --%>
														<dsp:getvalueof var="ltlItemFlag" param="regItem.sKUDetailVO.ltlItem" />
														<c:choose>
															<c:when test="${(isBelowLineItem eq 'false')}">
                                                                <c:choose>
									                          		<c:when test="${(requestedQuantity > purchasedQuantity) || (requestedQuantity == 0 && purchasedQuantity ==0)}">
																		<div class="addToCart marTop_5 <c:if test="${(isInternationalCustomer && isIntlRestricted) || (enableKatoriFlag eq false && not empty refNum) || (isInternationalCustomer && not empty refNum)}">disabled</c:if>">
																			<c:set var="lblAddTOCart"><bbbl:label key='lbl_mng_regitem_additemcart' language="${pageContext.request.locale.language}" /></c:set>
																			<%-- if the registry is private and no shipping address available, add to cart brings up edit modal--%>			                                                            	
																			<input type="button" class="button-Med btnSecondary <c:if test="${empty regAddress}">editRegInfo completeShipping</c:if>" name="<c:if test="${not empty regAddress}">btnAddToCart</c:if>" title="${lblAddTOCart}" value="${lblAddTOCart}" role="button"<c:if test="${isInternationalCustomer && isIntlRestricted}">disabled="disabled"</c:if>/>
																			<span class="txtOffScreen">Add ${productName} to your cart </span>
																		</div>
																		<c:if test="${isInternationalCustomer && isIntlRestricted}">
									                                <div class="notAvailableIntShipMsg regIntShipMsg cb clearfix"><bbbl:label key='lbl_reg_intl_restricted_message' language="${pageContext.request.locale.language}" /></div>
								                                    </c:if>
																	</c:when>
													            	<c:otherwise>
																		<div class="purchasedConfirmation">
																			<span class="icon-fallback-text">
																				<span class="icon-checkmark" aria-hidden="true"></span>
																				<span class="icon-text"><bbbl:label key='lbl_mng_regitem_purchased_confirm' language="${pageContext.request.locale.language}" /></span>
																			</span>

															 				<bbbl:label key='lbl_mng_regitem_purchased_confirm' language="${pageContext.request.locale.language}" />
																		</div>
																	</c:otherwise>
													            </c:choose>
                                                               <%-- <dsp:droplet name="IsProductSKUShippingDroplet">
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
															</c:when>
															<c:otherwise>
																<span class="itemNotAvailable padTop_10 block"><bbbl:label key='lbl_mng_item_belowline' language="${pageContext.request.locale.language}" /></span>
															</c:otherwise>
														</c:choose>

													    <c:choose>
															 <c:when test="${not empty refNum && refNum ne null}">
																<c:choose>
																	<c:when test="${purchasedQuantity gt 0}">
																		<c:set var="hideRemoveButtonCustomProd" value="true" scope="request"/>
																	</c:when>
																	<c:otherwise>
																		<c:set var="hideRemoveButtonCustomProd" value="false" scope="request"/>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:set var="hideRemoveButtonCustomProd" value="false" scope="request"/>
															</c:otherwise>
													    </c:choose>
														<a href="#" title="Registry submit" role="link" aria-label="Remove quantity ${lblForThe} ${productName}" data-submit-button="remove${idxCat}${idx}" class="btnAjaxSubmitRegistry <c:if test="${hideRemoveButtonCustomProd}"> hidden </c:if>  buttonTextLink" onclick="omniRegistryRemoveItem('${productId}','${eventType}','${registryId}', '${skuId}', '${price}', '${qtyPurchased}');" >
															<span class="icon-fallback-text">
																<span class="icon-times" aria-hidden="true"></span>
																<span class="icon-text"><bbbl:label key='lbl_mng_regitem_remove' language="${pageContext.request.locale.language}" /> ${productName} <bbbl:label key="lbl_from_cart" language="${pageContext.request.locale.language}"/></span>
															</span>
															<bbbl:label key='lbl_mng_regitem_remove' language="${pageContext.request.locale.language}" />
														</a>

													</div>
												</div>

												<div class="productFooter clearfix">

														<div class="noMar clearfix freeShipContainer prodAttribWrapper">
															<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																<dsp:param name="array" param="regItem.sKUDetailVO.skuAttributes" />
																<dsp:param name="elementName" value="attributeVOList"/>
																<dsp:oparam name="output">
																	<dsp:getvalueof var="pageName" param="key" />
																	<c:if test="${pageName eq 'RLP'}">
																		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																	<dsp:param param="attributeVOList" name="array" />
																	<dsp:param name="elementName" value="attributeVO"/>
																	<%-- <dsp:param name="sortProperties" value="+priority"/> --%>
																		<dsp:oparam name="output">
																            <c:set var="currentCount"><dsp:valueof param="count" /></c:set>
																            <c:set var="actionURL"><dsp:valueof param="attributeVO.actionURL" /></c:set>
																            <c:set var="attributeName"><dsp:valueof param="attributeVO.attributeName" /></c:set>
																				<c:if test="${not empty actionURL}">
																           			<c:choose>
																						<c:when test="${not empty skuID && null ne attributeName && fn:contains(attributeName,vdcAttributesList)}">
																						  	<div <c:if test="${fn:contains(actionURL,'ltlDeliveryInfo') }">class="ltlTruck"</c:if>><a href="/store/browse/dynamicStaticContent.jsp?pageName=ItemShippedDirectlyFromVendor&isVdcSku=true&skuId=${skuID}" class="popup"><span><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></span></a></div>
																						</c:when>
																						<c:otherwise>
																							<div <c:if test="${fn:contains(actionURL,'ltlDeliveryInfo') }">class="ltlTruck"</c:if>><a href="${actionURL}?skuID=${skuID}" class="popup"><span><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></span></a></div>	
																						</c:otherwise>																					
																					</c:choose>
																				</c:if>
																			<c:choose>
											                          			<c:when test="${currentCount%2 == 0}">
															            			<c:set var="nextStyle">prodSecondaryAttribute</c:set>
															            		</c:when>
															            		<c:otherwise>
																					<c:set var="nextStyle">prodSecondaryAttribute</c:set>
																				</c:otherwise>
															            	</c:choose>
																		</dsp:oparam>
																	</dsp:droplet>
																	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																	<dsp:param param="attributeVOList" name="array" />
																	<dsp:param name="elementName" value="attributeVO"/>
																	<dsp:param name="sortProperties" value="+priority"/>
																		<dsp:oparam name="output">
																            <c:set var="currentCount"><dsp:valueof param="count" /></c:set>
																            <c:set var="actionURL"><dsp:valueof param="attributeVO.actionURL" /></c:set>
																            <c:set var="attributeName"><dsp:valueof param="attributeVO.attributeName" /></c:set>
																			<c:if test="${empty actionURL}">
																				<div><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></div>
																			</c:if>
																			<c:choose>
											                          			<c:when test="${currentCount%2 == 0}">
															            			<c:set var="nextStyle">prodSecondaryAttribute</c:set>
															            		</c:when>
															            		<c:otherwise>
																					<c:set var="nextStyle">prodSecondaryAttribute</c:set>
																				</c:otherwise>
															            	</c:choose>
																		</dsp:oparam>
																	</dsp:droplet>
																		<%-- <dsp:droplet name="/atg/dynamo/droplet/ForEach">
																		<dsp:param param="attributeVOList" name="array" />
																		<dsp:param name="elementName" value="attributeVO"/>
																		<dsp:param name="sortProperties" value="+priority"/>
																			<dsp:oparam name="output">
																	            <c:set var="currentCount"><dsp:valueof param="count" /></c:set>
																	            <c:set var="actionURL"><dsp:valueof param="attributeVO.actionURL" /></c:set>
																	            <c:set var="attributeName"><dsp:valueof param="attributeVO.attributeName" /></c:set>
																	            <c:choose>
																					<c:when test="${not empty actionURL}">
																						<c:choose>
																							<c:when test="${not empty skuID && null ne attributeName && fn:contains(attributeName,vdcAttributesList)}">
																							  	<li <c:if test="${fn:contains(actionURL,'ltlDeliveryInfo') }">class="ltlTruck"</c:if>><a href="/store/browse/dynamicStaticContent.jsp?pageName=ItemShippedDirectlyFromVendor&isVdcSku=true&skuId=${skuID}" class="popup"><span><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></span></a></li>
																							</c:when>
																							<c:otherwise>
																								<li <c:if test="${fn:contains(actionURL,'ltlDeliveryInfo') }">class="ltlTruck"</c:if>><a href="${actionURL}?skuID=${skuID}" class="popup"><span><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></span></a></li>	
																							</c:otherwise>																					
																						</c:choose>
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
																						<c:set var="nextStyle">prodSecondaryAttribute</c:set>
																					</c:otherwise>
																            	</c:choose>

																			</dsp:oparam>
																		</dsp:droplet> --%>
																	</c:if>
																</dsp:oparam>
															</dsp:droplet>
                                                            <%-- <c:if test="${skuDetailVO.shippingSurcharge gt 0.0}">
																<li class="${nextStyle}"><bbbl:label key="lbl_mng_surcharge" language="<c:out param='${language}'/>"/>${skuDetailVO.shippingSurcharge}</li>
															</c:if> --%>
														</div>

												</div>

										</div>

                                    </li>

							</dsp:oparam>

						</dsp:droplet>
                        </ul>
                            </div>
                    </c:if>
						<%-- <c:if test="${count ne 0 }"><li class="noBorder"><a href="#backToTop"><bbbl:label key='lbl_mng_regitem_back_to_top' language="${pageContext.request.locale.language}" /></a></li></c:if> --%>
					</c:if>
				</dsp:oparam>
			</dsp:droplet>
            <c:if test="${countIE eq sizeIE}">
				</div>
			</c:if>
		</dsp:oparam>
	</dsp:droplet>

	</dsp:form>
	<c:set var ="operation"><dsp:valueof bean="GiftRegistryFormHandler.registryItemOperation"/></c:set>
	<c:set var ="removedProdID"><dsp:valueof bean="GiftRegistryFormHandler.removedProductId"/></c:set>
	<c:set var ="addedProdID"><dsp:valueof bean="GiftRegistryFormHandler.productStringAddItemCertona"/></c:set>
	<script type="text/javascript">
   		var operation='${operation}';
			 if ( operation == 'addedCertonaItem') {
	    	var BBB = BBB || {};

	    	BBB.registryInfo = {
	    			registryPagename: "itemAddedToRegistryFromCertona",
	    			products : '${addedProdID}',
	    			var23 : '${fn:escapeXml(eventType)}',
	    			var24 : '${registryId}'
	    		};
	    	}
    </script>

    <script type="text/javascript">
    	var sa_page,
    		sa_instagram_registry_id,
    		sa_instagram_user_email,
			sa_instagram_registry_id,
			sa_instagram_registry_type,
			sa_instagram_registry_is_owner_view,
			sa_array = [],
			sa_s22_instagram_allow_vc_upload,
			sa_photo_upload_products = [];

		function sa_async_load() {
			var sa = document.createElement('script');sa.type = 'text/javascript';
			sa.async = true;sa.src = '${saSrc}';
			var sax = document.getElementsByTagName('script')[0];sax.parentNode.insertBefore(sa, sax);
		}

		function triggerImplementationCode(){

			sa_page="1",
			sa_instagram_registry_id= '${registryId}',			
			sa_instagram_user_email= $("#userEmail").val(),
			sa_instagram_registry_id= '${registryId}',
			sa_instagram_registry_type= '${eventType}',
			sa_instagram_registry_is_owner_view= '1',
			sa_array = [],
			sa_s22_instagram_allow_vc_upload = '1';

			$('#formID1').find('.accordionReg').each(function(){
				$(this).find('.accordionDiv').each(function(){
					var _this =  $(this).find('li.grid_12');
				 	_this.each(function() {
				 		if($(this).find('#sa_vc').val() > 0) {
				 			var $this = $(this).find('div.ugc_sa');
							var sa_id = $this.attr('id');
							var prod_id = $this.attr('data-prodId');
							sa_array[sa_id] = prod_id;
						}
				 	});
				});
			});


			sa_photo_upload_products = sa_array;
			console.log(sa_photo_upload_products);

			(function() {
				sa_async_load();
			}());



		}


		triggerImplementationCode();

		$(".triggerBVsubmitReview")
		  .on("mouseenter", function() {
		    $(this).find("use").attr("xlink:href", "#icon-star-icon");
		  })
		  .on("mouseleave", function() {
		   $(this).find("use").attr("xlink:href", "#icon-star-icon_fill");
		 });
	</script>
</dsp:page>
