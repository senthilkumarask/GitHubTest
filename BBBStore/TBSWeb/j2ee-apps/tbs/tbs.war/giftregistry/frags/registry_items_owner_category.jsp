<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFetchProductIdDroplet" />	
	<dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/com/bbb/commerce/droplet/TBSItemExclusionDroplet"/>
        <dsp:importbean bean="/com/bbb/commerce/checkout/droplet/IsProductSKUShippingDroplet"/>
        <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
        <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	<c:set var="othersCat"><bbbc:config key="DefaultCategoryForRegistry" configName="ContentCatalogKeys" /></c:set>
	<c:set var="enableLTLReg"><bbbc:config key="enableLTLRegForSite" configName="FlagDrivenFunctions" /></c:set>
	<c:set var="enableKatoriFlag"><bbbc:config key="enableKatori" configName="EXIMKeys" /></c:set>
	<%-- Droplet Placeholder --%>
	<dsp:getvalueof var="registryId" param="registryId" />
	<dsp:getvalueof var="skuId" value="" />
	<dsp:getvalueof var="productId" param="productId" />
	<dsp:getvalueof var="sortSeq" param="sorting"/>
	<dsp:getvalueof var="view" param="view"/>
	<dsp:getvalueof var="eventType" param="eventType" />
	<dsp:getvalueof var="isBelowLineItem" param="regItem.isBelowLineItem" />
	<dsp:getvalueof var="disable" param="regItem.sKUDetailVO.disableFlag" />
	<dsp:getvalueof var="webOffered" param="regItem.sKUDetailVO.webOfferedFlag" />
	<dsp:getvalueof var="active" param="regItem.sKUDetailVO.activeFlag" />
	<dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
	
	<c:set var="scene7Path">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
	<c:set var="customizeCTACodes">
		<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
	</c:set>
	<dsp:include page="registry_modified_item_form.jsp">
	</dsp:include>
    
	<dsp:form id="formID1" method="post">
	<dsp:input type="hidden" bean="GiftRegistryFormHandler.size" paramvalue="totEntries" />
	<c:set var="valueIndex">0</c:set>
 <dl class="accordion" data-accordion="">
	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="categoryBuckets" />
		<dsp:oparam name="output">
		<dsp:getvalueof var="sizeIE" param="size"/>
			<dsp:getvalueof var="countIE" param="count"/>
			<%-- <c:choose>
                <c:when test="${countIE eq 1}">
                    <dd class="accordion-navigation accordionReg accordionReg1 active">
                </c:when>
                <c:when test="${countIE eq 5}">
                    </dd>
                    <div class="ajaxLoadWrapper load2 loadMore hidden">
                        <bbbt:textArea key="txt_viewregistry_imageloader" language ="${pageContext.request.locale.language}"/>
                    </div>
                    <dd class="accordion-navigation accordionReg accordionReg2 active">
                </c:when>
                <c:when test="${countIE eq 12}">
                    </dd>
                    <div class="ajaxLoadWrapper load3 loadMore hidden">
                        <bbbt:textArea key="txt_viewregistry_imageloader" language ="${pageContext.request.locale.language}"/>
                    </div>
                    <dd class="accordion-navigation accordionReg accordionReg3 active">
                </c:when>
            </c:choose> --%>
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
                            <dsp:getvalueof var="bucketNameNew" value="${fn:replace(bucketName, '/','')}"/>
                            <dsp:getvalueof var="bucketNameNew" value="${fn:replace(bucketNameNew, '-','')}"/>
                            <dsp:getvalueof var="bucketNameNew" value="${fn:replace(bucketNameNew, ',','')}"/>
                            <dsp:getvalueof var="bucketNameNew" value="${fn:replace(bucketNameNew, '&','')}"/>
                        <c:if test="${recommendedCatFlag || count gt 0}">
                            <dd class="accordion-navigation accordionReg accordionReg active">
							<a href="#accordionDiv${fn:replace(bucketNameNew, ' ','')}${count}"><dsp:valueof param="key" />(${count})</a>
							<%-- <c:if test="${!empty catId}">
							<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
								<dsp:param name="id" value="${catId}" />
								<dsp:param name="itemDescriptorName" value="category" />
								<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
										<a href="${contextPath}${finalUrl}" title="${catName}" class="fr bold addNewItem">
	                                		<c:if test="${addItemFlag eq 'true'}">
												<bbbl:label key='lbl_mng_regitem_addmoreitem' language="${pageContext.request.locale.language}" />
											</c:if>
	                               		</a>
									</dsp:oparam>
							</dsp:droplet>
							</c:if> --%>
							<%-- ForEach for List<RegistryItemVO> listRegistryItemVO --%>
                            <div id="accordionDiv${fn:replace(bucketNameNew, ' ','')}${count}" class="accordionDiv content active">
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
									<c:when test="${not empty itemList}">
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
									<%-- <dsp:setvalue bean="CartModifierFormHandler.addItemCount" value="1" /> --%>
									<dsp:getvalueof var="idx" param="index"/>
										<%-- <dsp:input bean="CartModifierFormHandler.addItemCount" type="hidden" iclass="addItemCount" value="1" /> --%>
										<div class="qtyWrapper productRow btngiftViewTopMargin giftViewProductChangeStoreWrapper addToCartGiftRegWrapper clearfix">
                                                                            
                                        <input type="hidden" name="bts" value="false" class="addToCartSubmitData" />
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
										<c:if test="${not empty personalisedCode}">
										    <c:set var="cusDet">${eximCustomizationCodesMap[personalisedCode]}</c:set>									
										    <input type="hidden" value="${cusDet}" id="customizationDetails"/> 
									    </c:if>
										<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.mediumImage" />
                                        <div class="productImage small-4 medium-2 columns print-2">
                                           
                                            <c:if test="${active}">
                                            <a class="prodImg lnkQVPopup" href="${contextPath}${finalUrl}?skuId=${skuID}&registryId=${registryId}" 
                                             data-pdp-qv-url="pdp_quick_view.jsp?skuId=${skuID}&registryId=${registryId}&productId=${productId}"  data-skuId="${skuID}" data-registryId="${registryId}" data-productId="${productId}" data-sopt="${ltlShipMethod}" data-refNum="${refNum}"  title="<c:out value='${productName}'/>" <c:if test="${not empty refNum}">data-personalisedCode="${personalisedCode}"
														data-customizedPrice="${customizedPrice1}" data-customizationDetails="${customizationDetails}" data-color="${color}" data-size="${size}" data-personalizedImageUrls="${personalizedImageUrls}"
														data-personlisedPrice="${personlisedPrice1}" data-personalizedImageUrlThumbs="${personalizedImageUrlThumbs}" data-personalizationType= "${personalizationType}"</c:if>>
                                           </c:if>
                                  				<c:choose>
                                  					<c:when test="${not empty refNum && not empty personalizedImageUrlThumbs}">
                                  						<img src="${personalizedImageUrlThumbs}" data-original="${personalizedImageUrlThumbs}" alt="<c:out value='${productName}'/>" class="prodImage stretch" />
                                  					</c:when>
                                  					<c:when test="${empty imageURL}">
                                                        <img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="96" width="96" alt="${productName}" class="prodImage" />
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="${scene7Path}/${imageURL}" data-original="${scene7Path}/${imageURL}" alt="${productName}" class="prodImage stretch" />
                                                    </c:otherwise>
                                                </c:choose>
                                           <c:if test="${active}">
                                  			 </a>
                                  			</c:if>
                                  			 

                                        </div>
                                        <dsp:getvalueof var="skuDetailVO" param="regItem.sKUDetailVO"/>
										<dsp:getvalueof var="color" param="regItem.sKUDetailVO.color" />
										<dsp:getvalueof var="size" param="regItem.sKUDetailVO.size" />
										<dsp:getvalueof var="upc" param="regItem.sKUDetailVO.upc" />
										<dsp:getvalueof var="skuInCartFlag" param="regItem.sKUDetailVO.inCartFlag" />
										<dsp:getvalueof var="ltlItemFlag" param="regItem.sKUDetailVO.ltlItem" />
										<dsp:getvalueof var="ltlShipMethod" param="regItem.ltlDeliveryServices" />
										<dsp:getvalueof var="ltlShipMethodDesc" param="regItem.ltlShipMethodDesc"/>
										<dsp:getvalueof var="ltlAssemblySelected" param="regItem.assemblySelected"/>
										<dsp:getvalueof var="shipMethodUnsupported" param="regItem.shipMethodUnsupported"/>
										<dsp:getvalueof var="isDSLUpdateable" param="regItem.dslUpdateable"/>
										<dsp:getvalueof var="itemType" param="regItem.itemType"/>
										<dsp:getvalueof var="ltlDeliveryPrices" param="regItem.ltlDeliveryPrices"/>
										
										<dsp:getvalueof var="deliverySurcharge" param="regItem.deliverySurcharge"/> 
										<dsp:getvalueof var="assemblyFee" param="regItem.assemblyFees"/>
										
										<div class="productContainer small-8 medium-10 columns no-padding-right print-10">
											<div class="productTab productHeading clearfix hide-for-small">
												<div class="productName small-12 medium-3 columns print-3">
													<c:if test="${upc ne null}">
														<div class="fl upc"><span class="bold"> <bbbl:label key='lbl_mng_regitem_upc' language="${pageContext.request.locale.language}" /> </span> 
														<span>${upc}</span></div>
													</c:if>
												</div>
												<div class="small-2 columns price print-2 hide-for-small-only">Price</div>
												<div class="small-2 columns requested print-2 hide-for-small-only"><bbbl:label key='lbl_mng_regitem_purchased' language="${pageContext.request.locale.language}" /></div>
												<div class="small-2 columns purchase print-2 hide-for-small-only"><bbbl:label key='lbl_mng_regitem_requested' language="${pageContext.request.locale.language}" /></div>
												<div class="small-1 columns productLastColumn print-1 hide-for-small-only"> </div>
											</div>
											<c:choose>
												<c:when test="${not empty personalisedCode && fn:contains(customizeCTACodes, personalisedCode)}">
													<c:set var="customizeTxt" value="true"/>
												</c:when>
												<c:otherwise>
													<c:set var="customizeTxt" value="false"/>
												</c:otherwise>
											</c:choose>
											<div class="productTab productContent clearfix">
												<div class="productName small-12 medium-3 columns print-3">
													<span class="blueName prodTitle">
														<c:choose>
														<c:when test="${active}">
														<a href="${contextPath}${finalUrl}?skuId=${skuID}&registryId=${registryId}" 
														class="lnkQVPopup prodTitle" data-pdp-qv-url="pdp_quick_view.jsp?skuId=${skuID}&registryId=${registryId}&productId=${productId}"
																  data-skuId="${skuID}" data-registryId="${registryId}" data-productId="${productId}" data-sopt="${ltlShipMethod}" data-refNum="${refNum}"  title="<c:out value='${productName}'/>" <c:if test="${not empty refNum}">data-personalisedCode="${personalisedCode}"
																	data-customizedPrice="${customizedPrice1}" data-customizationDetails="${customizationDetails}" data-color="${color}" data-size="${size}" data-personalizedImageUrls="${personalizedImageUrls}"
																	data-personlisedPrice="${personlisedPrice1}" data-personalizedImageUrlThumbs="${personalizedImageUrlThumbs}" data-personalizationType= "${personalizationType}"</c:if>>
															${productName}
														</a>
														</c:when>
														<c:otherwise>
															${productName}
														</c:otherwise>
														</c:choose>
													</span><br />
												       <span><c:if test='${not empty color}'><bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}"/>&nbsp;:&nbsp;<dsp:valueof value="${color}" valueishtml="true" /></c:if><c:if test='${not empty size}'><c:if test='${not empty color}'><br/></c:if><bbbl:label key="lbl_item_size" language ="${pageContext.request.locale.language}"/>&nbsp;:&nbsp;<dsp:valueof value="${size}" valueishtml="true" /></c:if>
	                                                	</span>
	                                                	<div class="personalizationAttributes"> 
		                                                    <c:if test="${not empty personalisedCode}">	
			        											<span class="exim-code">${eximCustomizationCodesMap[personalisedCode]}:</span>
				        										<span class="exim-detail">${customizationDetails}</span>
		        											</c:if>
		        										</div>
		        									 <c:if test="${not empty personalisedCode}">
		        										<div class="personalizationAttributes">
			        										<div class="pricePersonalization">
			        											<c:choose>
			        											<c:when test="${personalizationType == 'PB'}">
			        												<span class="fee-detail">
			        													<bbbl:label key='lbl_PB_Fee_detail' language="${pageContext.request.locale.language}" />	        										
			        												</span>
			        											</c:when>
			        											<c:when test="${personalizationType == 'PY'}">
			        												<span class="cust-price">
			        													<dsp:valueof value="${customizedPrice1}"/>
			        												</span>	
			        												<span class="fee-detail"><bbbl:label key='lbl_exim_added_price' language="${pageContext.request.locale.language}" /></span>	        											
			        											</c:when>
			        											<c:when test="${personalizationType == 'CR'}">
		        													<span class="cust-price">
			        													<dsp:include page="/global/gadgets/formattedPrice.jsp">
			        													<dsp:param name="price" value="${customizedPrice}"/>
			        												    </dsp:include>
		        													</span>			
		        												    <span class="fee-detail">
		        												    <c:choose>
																 		<c:when test="${customizeTxt eq true}">
																 			<bbbl:label key="lbl_exim_cr_added_price_customize"
																							language="${pageContext.request.locale.language}"/>
																 		</c:when>
																 		<c:otherwise>
																 			<bbbl:label key="lbl_exim_cr_added_price"
																							language="${pageContext.request.locale.language}"/>
																 		</c:otherwise>
																 	</c:choose>
		        												    </span>
			        										    </c:when>
			        											</c:choose>
			        										</div>
	        											</div> 
	        										</c:if>
													</div>
												<div class="price bold small-12 medium-2 columns print-2">
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
														<dsp:param name="ltlItemFlag" value="${ltlItemFlag}"/>	
														<dsp:param name="ltlShipMethodDesc" value="${ltlShipMethodDesc}"/>	
														<dsp:param name="deliverySurcharge" value="${deliverySurcharge}"/>	
														<dsp:param name="assemblyFee" value="${assemblyFee}"/>	
														<dsp:param name="shipMethodUnsupported" value="${shipMethodUnsupported}"/>	
														<dsp:param name="ltlShipMethod" value="${ltlShipMethod}"/>
														<dsp:param name="isDSLUpdateable" value="${isDSLUpdateable}"/>
														<dsp:param name="personalizationOptionsDisplay" value="${personalizationOptionsDisplay}"/>	
														<dsp:param name="skuInCartFlag" value="${skuInCartFlag}"/>
													</dsp:include>
												</div>
												<div class="requested bold small-12 medium-2 columns print-2">
													<h3 class="amount_small show-for-small-only">Purchased:</h3>
													<dsp:valueof param="regItem.qtyPurchased" /> 
													<dsp:input type="hidden" bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].purchasedQuantity" paramvalue="regItem.qtyPurchased" />
												</div>
												<dsp:getvalueof var="qtyPurchased" param="regItem.qtyPurchased"/>
												<div class="purchase quantity small-12 medium-3 columns print-2">
													<div class="input qty-spinner">
																<a href='#' class="button minus secondary"><span></span></a>
                                                                <input type="hidden" name="itemIndex" value="${valueIndex}"/>
                                                                <%-- <dsp:input bean="GiftRegistryFormHandler.viewBean.purchasedQuantity" type="text"  title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" value="${qtyPurchased}" iclass="input_tiny_giftView fl itemQuantity"/>
																<input name="qty" type="hidden" value="${qtyPurchased}" data-change-store-submit="qty" data-change-store-errors="required digits nonZero" />  --%>
                                                                <c:set var="titleqt">
                                                                    <bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />
                                                                </c:set>
																<dsp:input bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].quantity" type="text" paramvalue="regItem.qtyRequested" title="${titleqt}" iclass="valQtyGiftReg input_tiny_giftView fl itemQuantity quantity-input" id="quantityPurchased" maxlength="2">
                                                                    <dsp:tagAttribute name="data-change-store-errors" value="required digits nonZero"/>									 
                                                                    <dsp:tagAttribute name="data-change-store-submit" value="qty"/>
                                                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                                                    <dsp:tagAttribute name="aria-labelledby" value="quantityPurchased"/>
                                                                </dsp:input>
                                                                <dsp:getvalueof var="regItemOldQty" param="regItem.qtyRequested" />
                                                                <input type="hidden" name="regItemOldQty" value="${regItemOldQty}" class="frmAjaxSubmitData" />
																<dsp:input type="hidden" bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].regItemOldQty" paramvalue="regItem.qtyRequested" id="oldquantityd"/>
                                                                <a href='#' class="button plus secondary"><span></span></a>	
														</div>
												</div>
												<div class="productLastColumn small-12 medium-2 columns print-2">
                                                    <dsp:input bean="GiftRegistryFormHandler.value.registryId" type="hidden" paramvalue="registryId" />
                                                    <input type="hidden" name="registryId" value="${registryId}" class="registryId frmAjaxSubmitData" />
                                                    <dsp:input bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].rowId" type="hidden" paramvalue="regItem.rowID"/>
                                                    <dsp:input bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].sku" type="hidden" paramvalue="regItem.sku"/>
                                                                                                       
                                                    <dsp:input bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].itemTypes" type="hidden" value="${itemType}"/>
                                                    <input type="hidden" name="itemTypes" value="${itemType}" class="skuId frmAjaxSubmitData" />
                                                    <input type="hidden" name="updateDslFromModal" value="" class="frmAjaxSubmitData" />
													<input type="hidden" name="alternateNum" value="" class="frmAjaxSubmitData" />
													<input type="hidden" name="refNum" value="${refNum}" class="frmAjaxSubmitData" />
													<input type="hidden" name="personalizationCode" value="${personalizationType}" class="frmAjaxSubmitData" />
													 
													<c:choose>
														<c:when test="${not empty ltlShipMethod}">
														<input type="hidden" name="ltlDeliveryServices" value="${ltlShipMethod}" class="frmAjaxSubmitData" />
														</c:when>
														<c:otherwise>
														<input type="hidden" name="ltlDeliveryServices" value="" class="frmAjaxSubmitData" />
														</c:otherwise>
													</c:choose>
													
													<dsp:input bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].ltlDeliveryPrices" type="hidden" value="${ltlDeliveryPrices}"/>
													<input type="hidden" name="ltlDeliveryPrices" value="${itemType}" class="frmAjaxSubmitData" />
                                                    
                                                    
                                                    <dsp:getvalueof var="skuId" param="regItem.sku" />
                                                    <dsp:getvalueof var="rowId" param="regItem.rowID" />                                                    
                                                    <input type="hidden" name="skuId" value="${skuId}" class="skuId frmAjaxSubmitData" />
                                                    <input type="hidden" name="rowId" value="${rowId}" class="frmAjaxSubmitData" />
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
                                                    <%-- <dsp:getvalueof var="ownerUrl" value="${contextPath}/giftregistry/view_registry_owner.jsp?registryId=${registryId}&eventType=${eventType}"/>
                                                    <dsp:input bean="GiftRegistryFormHandler.successURL"  type="hidden" value="${ownerUrl}" />
                                                    <dsp:input bean="GiftRegistryFormHandler.errorURL"	type="hidden" value="${ownerUrl}" /> --%>
                                                    <dsp:input bean="GiftRegistryFormHandler.queryParam"  type="hidden" value="registryId=${registryId}&eventType=${eventType}" />
                                                     <dsp:input bean="GiftRegistryFormHandler.fromPage" type="hidden" value="registryItemsOwnerCategory" />
                                                    <dsp:input bean="GiftRegistryFormHandler.updateItemToGiftRegistry" 
                                                        id="update${idxCat}${idx}" 	type="submit" style="display:none" value="Update"	/>
                                                    <dsp:input bean="GiftRegistryFormHandler.removeItemFromGiftRegistry" 
                                                        id="remove${idxCat}${idx}" 	type="submit" style="display:none"  value="Remove" />
                                                    <c:set var="valueIndex" value="${valueIndex + 1}"/>
													<div class="button_active button_active_orange">
														<a href="#" data-trigger-button="update${idxCat}${idx}" class="validateQuantity tiny button service" onclick="omniRegistryUpdateItem('${productId}','${eventType}','${registryId}', '${skuId}');" ><bbbl:label key='lbl_mng_regitem_update' language="${pageContext.request.locale.language}" /></a>						
													</div>
													<a href="#" data-submit-button="remove${idxCat}${idx}" class="btnAjaxSubmitRegistry buttonTextLink tiny button secondary" onclick="omniRegistryRemoveItem('${productId}','${eventType}', '${registryId}', '${skuId}');"><bbbl:label key='lbl_mng_regitem_remove' language="${pageContext.request.locale.language}" /></a>
													<div class="clear"></div>
													<dsp:getvalueof param="regItem.qtyRequested" var="requestedQuantity" />
													<dsp:getvalueof param="regItem.qtyPurchased" var="purchasedQuantity" />
													<!-- LTL-328 Hide add to cart button and show not availability message for LTL item -->
 													<c:choose>
														<c:when test="${(((isBelowLineItem eq 'false') || inStoreSku) && (not ltlItemFlag || enableLTLReg))}">
															<c:choose>
									                          	<c:when test="${requestedQuantity > purchasedQuantity}">
																	<dsp:droplet name="TBSItemExclusionDroplet">
																		<dsp:param name="skuId" value="${skuID}" />
																		<dsp:param name="siteId" value="${currentSiteId}" />
																		<dsp:oparam name="output">
																		<dsp:getvalueof var="isItemExcluded" param="validItemExcludedSku"/>
																		<dsp:getvalueof var="caDisabled" param="caDisabled"/>
																		<dsp:getvalueof var="reasonCode" param="reasonCode"/>
																		<c:choose>
																			<c:when test="${isItemExcluded || caDisabled || (enableKatoriFlag eq false && not empty refNum)}">
																				<div class="addToCart hidden button_disabled">
																					<c:set var="lblAddTOCart"><bbbl:label key='lbl_mng_regitem_additemcart' language="${pageContext.request.locale.language}" /></c:set>
																					<input  class="tiny button transactional" type="button" disabled="disabled" name="btnAddToCart" title="${lblAddTOCart}" value="${lblAddTOCart}" role="button" aria-pressed="false" />
																				</div>
																				<div style="color:red;font-weight:bold;">
																		
                                    										<dsp:include page="/browse/frag/reasonCodeMessage.jsp" flush="true" >
																				<dsp:param name="reasonCode" value="${reasonCode}"/>
																			</dsp:include>
                                										</div>
																			</c:when>
																			<c:otherwise>
																				<div class="addToCart">
																					<c:set var="lblAddTOCart"><bbbl:label key='lbl_mng_regitem_additemcart' language="${pageContext.request.locale.language}" /></c:set>
																					<input  class="tiny button transactional" type="button" name="btnAddToCart" title="${lblAddTOCart}" value="${lblAddTOCart}" role="button" aria-pressed="false" />
																				</div>
																			</c:otherwise>
																		</c:choose>
																		</dsp:oparam>
																	</dsp:droplet>
																</c:when>
													            <c:otherwise>
																	<div class="purchasedConfirmation">
															 			 <h2><bbbl:label key='lbl_mng_regitem_purchased_confirm' language="${pageContext.request.locale.language}" /></h2>
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
													<c:set var="writeReviewOn" scope="request"><bbbc:config key="writeReviewOn" configName="FlagDrivenFunctions" /></c:set>
													<c:if test="${writeReviewOn}">
														<dsp:droplet name="/com/bbb/commerce/browse/droplet/ProductStatusDroplet">
															<dsp:param name="productId" value="${productId}" />
															<dsp:oparam name="output">
																	<dsp:getvalueof var="isProductActive" param="isProductActive" />
															</dsp:oparam>
														</dsp:droplet>
														<c:if test="${isProductActive && purchasedQuantity>0}">
														<a  href="#" data-BVProductId="${productId}" class="buttonTextLink triggerBVsubmitReview" onclick="javascript:s_crossSell('write a review - registry');">
															<bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" />
														</a>
														</c:if>
													</c:if>
												</div>
											</div>
											
											<div class="productFooter small-12 columns">
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
																				<li><a href="${actionURL}" class="popup"><span><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></span></a></li>
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
																</dsp:droplet>
															</c:if>
														</dsp:oparam>
													</dsp:droplet>
                                                       <%-- <c:if test="${skuDetailVO.shippingSurcharge gt 0.0}">
														<li class="${nextStyle}"><bbbl:label key="lbl_mng_surcharge" language="<c:out param='${language}'/>"/>${skuDetailVO.shippingSurcharge}</li>
													</c:if> --%>
												</ul>
											</div>
										</div>
									</div>
                                    <hr/>
								</dsp:oparam>
							</dsp:droplet>
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
                        <dsp:getvalueof var="bucketNameNew" value="${fn:replace(bucketName, '$','')}"/>
                        <dsp:getvalueof var="bucketNameNew" value="${fn:replace(bucketNameNew, '-','')}"/>
                    <c:if test="${count gt 0}">
                            <dd class="accordion-navigation accordionReg accordionReg active">
							<a href="#accordionDiv${fn:replace(bucketNameNew, ' ','')}${count}"><dsp:valueof param="key" />(${count})</a>					
                        <div id="accordionDiv${fn:replace(bucketNameNew, ' ','')}${count}" class="accordionDiv content active">
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
									<c:when test="${not empty itemList}"><c:set var="itemList" scope="request">${itemList};${productId}</c:set></c:when>
									<c:otherwise><c:set var="itemList" scope="request">${productId}</c:set></c:otherwise>
								</c:choose>
								<dsp:getvalueof var="isBelowLineItem" param="regItem.isBelowLineItem" />
								<dsp:getvalueof var="disable" param="regItem.sKUDetailVO.disableFlag" />
								<dsp:getvalueof var="webOffered" param="regItem.sKUDetailVO.webOfferedFlag" />
								<dsp:getvalueof var="active" param="regItem.sKUDetailVO.activeFlag" />
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
								<dsp:getvalueof var="idx" param="index"/>
								<c:if test="${not empty personalisedCode}">
								  <c:set var="cusDet">${eximCustomizationCodesMap[personalisedCode]}</c:set>									
								  <input type="hidden" value="${cusDet}" id="customizationDetails"/> 
							    </c:if>
                        
                                    <div class="qtyWrapper productRow btngiftViewTopMargin giftViewProductChangeStoreWrapper addToCartGiftRegWrapper clearfix">
                                        
                                            <input type="hidden" name="bts" value="false" class="addToCartSubmitData" />
											<c:set var="productName"><dsp:valueof param="regItem.sKUDetailVO.displayName" valueishtml="true"/></c:set>
											<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
												<dsp:param name="id" value="${productId}"/>
												<dsp:param name="itemDescriptorName" value="product" />
												<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
												<dsp:oparam name="output">
													<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
												</dsp:oparam>
											</dsp:droplet>			
											<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.mediumImage" />
											<div class="productImage small-4 medium-2 columns print-2">
											
	                                           <c:if test="${active}">
	                                            <a class="prodImg lnkQVPopup" href="${contextPath}${finalUrl}?skuId=${skuID}&registryId=${registryId}" 
	                                             data-pdp-qv-url="pdp_quick_view.jsp?skuId=${skuID}&registryId=${registryId}&productId=${productId}"  data-skuId="${skuID}" data-registryId="${registryId}" data-productId="${productId}" data-sopt="${ltlShipMethod}" data-refNum="${refNum}"  title="<c:out value='${productName}'/>" <c:if test="${not empty refNum}">data-personalisedCode="${personalisedCode}"
															data-customizedPrice="${customizedPrice1}" data-customizationDetails="${customizationDetails}" data-color="${color}" data-size="${size}" data-personalizedImageUrls="${personalizedImageUrls}"
															data-personlisedPrice="${personlisedPrice1}" data-personalizedImageUrlThumbs="${personalizedImageUrlThumbs}" data-personalizationType= "${personalizationType}"</c:if>>
	                                           </c:if>
	                                  				<c:choose>
	                                  					<c:when test="${not empty refNum && not empty personalizedImageUrlThumbs}">
	                                  						<img src="${personalizedImageUrlThumbs}" data-original="${personalizedImageUrlThumbs}" alt="<c:out value='${productName}'/>" class="prodImage stretch" />
	                                  					</c:when>
	                                  					<c:when test="${empty imageURL}">
	                                                        <img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="96" width="96" alt="${productName}" class="prodImage" />
	                                                    </c:when>
	                                                    <c:otherwise>
	                                                        <img src="${scene7Path}/${imageURL}" data-original="${scene7Path}/${imageURL}" alt="${productName}" class="prodImage stretch" />
	                                                    </c:otherwise>
	                                                </c:choose>
	                                            <c:if test="${active}">
	                                  			 </a>
	                                  			</c:if>
	                                  			 
											</div>
											<dsp:getvalueof var="skuDetailVO" param="regItem.sKUDetailVO"/>
											<dsp:getvalueof var="color" param="regItem.sKUDetailVO.color" />
											<dsp:getvalueof var="size" param="regItem.sKUDetailVO.size" />
										    <dsp:getvalueof var="upc" param="regItem.sKUDetailVO.upc" />
											<dsp:getvalueof var="ltlShipMethod" param="regItem.ltlDeliveryServices" />
										<dsp:getvalueof var="ltlShipMethodDesc" param="regItem.ltlShipMethodDesc"/>
										<dsp:getvalueof var="ltlAssemblySelected" param="regItem.assemblySelected"/>
										<dsp:getvalueof var="shipMethodUnsupported" param="regItem.shipMethodUnsupported"/>
										<dsp:getvalueof var="isDSLUpdateable" param="regItem.dslUpdateable"/>	
										<dsp:getvalueof var="itemType" param="regItem.itemType"/>
										<dsp:getvalueof var="deliverySurcharge" param="regItem.deliverySurcharge"/> 
										<dsp:getvalueof var="assemblyFee" param="regItem.assemblyFees"/>

											<div class="productContainer small-8 medium-10 columns no-padding-right print-10">
												<div class="productTab productHeading clearfix hide-for-small">
													<div class="productName small- 12 medium-3 columns print-3">
														<c:if test="${upc ne null}">
															<div class="fl upc"><span class="bold"> <bbbl:label key='lbl_mng_regitem_upc' language="${pageContext.request.locale.language}" /> </span> 
															<span>${upc}</span></div>
														</c:if>
													</div>
													<div class="small-2 columns price print-2 hide-for-small-only">Price</div>
													<div class="small-2 columns requested print-2 hide-for-small-only"><bbbl:label key='lbl_mng_regitem_purchased' language="${pageContext.request.locale.language}" /></div>
													<div class="small-2 columns purchase print-2 hide-for-small-only"><bbbl:label key='lbl_mng_regitem_requested' language="${pageContext.request.locale.language}" /></div>
													<div class="small-1 columns productLastColumn print-1 hide-for-small-only"> </div>
												</div>
												<div class="productTab productContent clearfix">
													<div class="productName small-12 medium-3 columns print-3">
														
														<span class="blueName">
														<c:choose>
															<c:when test="${active}">
																<a href="${contextPath}${finalUrl}?skuId=${skuID}&registryId=${registryId}" 
																class="lnkQVPopup prodTitle" data-pdp-qv-url="pdp_quick_view.jsp?skuId=${skuID}&registryId=${registryId}&productId=${productId}"
																		  data-skuId="${skuID}" data-registryId="${registryId}" data-productId="${productId}" data-sopt="${ltlShipMethod}" data-refNum="${refNum}"  title="<c:out value='${productName}'/>" <c:if test="${not empty refNum}">data-personalisedCode="${personalisedCode}"
																			data-customizedPrice="${customizedPrice1}" data-customizationDetails="${customizationDetails}" data-color="${color}" data-size="${size}" data-personalizedImageUrls="${personalizedImageUrls}"
																			data-personlisedPrice="${personlisedPrice1}" data-personalizedImageUrlThumbs="${personalizedImageUrlThumbs}" data-personalizationType= "${personalizationType}"</c:if>>
																	${productName}
																</a>
															</c:when>
															<c:otherwise>
																${productName}
															</c:otherwise>
														</c:choose>	
														</span><br />
													     <span><c:if test='${not empty color}'><bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}"/>&nbsp;:&nbsp;<dsp:valueof value="${color}" valueishtml="true" /></c:if><c:if test='${not empty size}'><c:if test='${not empty color}'><br/></c:if><bbbl:label key="lbl_item_size" language ="${pageContext.request.locale.language}"/>&nbsp;:&nbsp;<dsp:valueof value="${size}" valueishtml="true" /></c:if>
		                                                	</span>
		                                                	<div class="personalizationAttributes"> 
			                                                    <c:if test="${not empty personalisedCode}">
				        											<span class="exim-code">${eximCustomizationCodesMap[personalisedCode]}:</span>
				        											 <span class="exim-detail">${customizationDetails}</span>
			        											</c:if>
			        										</div>
			        									 <c:if test="${not empty personalisedCode}">
			        										<div class="personalizationAttributes">
				        										<div class="pricePersonalization">
				        											<c:choose>
				        											<c:when test="${personalizationType == 'PB'}">
				        												<span class="fee-detail">
				        													<bbbl:label key='lbl_PB_Fee_detail' language="${pageContext.request.locale.language}" />	        									
				        												</span>			
				        											</c:when>
				        											<c:when test="${personalizationType == 'PY'}">
					        											<span class="cust-price">
					        												<dsp:valueof value="${customizedPrice1}"/>		
					        											</span>
					        											<span class="fee-detail"><bbbl:label key='lbl_exim_added_price' language="${pageContext.request.locale.language}" /></span>
				        											</c:when>
				        											<c:when test="${personalizationType == 'CR'}">
			        												    <span class="cust-price">
				        													<dsp:include page="/global/gadgets/formattedPrice.jsp">		
				        												    <dsp:param name="price" value="${customizedPrice}"/>
			        												    </dsp:include>
			        												    </span>
			        												    <span class="fee-detail">
			        												    <c:choose>
																	 		<c:when test="${customizeTxt eq true}">
																	 			<bbbl:label key="lbl_exim_cr_added_price_customize"
																								language="${pageContext.request.locale.language}"/>
																	 		</c:when>
																	 		<c:otherwise>
																	 			<bbbl:label key="lbl_exim_cr_added_price"
																								language="${pageContext.request.locale.language}"/>
																	 		</c:otherwise>
																	 	</c:choose>
			        												    </span>
				        										    </c:when>
				        											</c:choose>
				        										</div>
		        											</div> 
		        										</c:if>
													</div>
													
													<div class="price bold small-12 medium-2 columns print-2">
													
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
														<dsp:param name="ltlItemFlag" value="${ltlItemFlag}"/>	
														<dsp:param name="ltlShipMethodDesc" value="${ltlShipMethodDesc}"/>	
														<dsp:param name="deliverySurcharge" value="${deliverySurcharge}"/>	
														<dsp:param name="assemblyFee" value="${assemblyFee}"/>	
														<dsp:param name="shipMethodUnsupported" value="${shipMethodUnsupported}"/>	
														<dsp:param name="ltlShipMethod" value="${ltlShipMethod}"/>
														<dsp:param name="isDSLUpdateable" value="${isDSLUpdateable}"/>
														<dsp:param name="personalizationOptionsDisplay" value="${personalizationOptionsDisplay}"/>
														<dsp:param name="skuInCartFlag" value="${skuInCartFlag}"/>
													</dsp:include>

													</div>
													<div class="requested bold small-12 medium-2 columns print-2">
														<dsp:valueof param="regItem.qtyPurchased" /> 
														<dsp:input type="hidden" bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].purchasedQuantity" paramvalue="regItem.qtyPurchased" />
													</div>

													<div class="purchase quantity small-12 medium-3 columns print-2">
                                                        <dsp:getvalueof var="qtyPurchased" param="regItem.qtyPurchased"/>
														<div class="input qty-spinner">
                                                                    <a href='#' class="button minus secondary"><span></span></a>
                                                                    <%-- <dsp:input bean="GiftRegistryFormHandler.viewBean.purchasedQuantity" type="text"  title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" value="${qtyPurchased}" iclass="input_tiny_giftView fl itemQuantity"/>
																	<input name="qty" type="hidden" value="${qtyPurchased}" data-change-store-submit="qty" data-change-store-errors="required digits nonZero" />  --%>
                                                                    <c:set var="titleqt">
                                                                        <bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />
                                                                    </c:set>
																	<dsp:input bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].quantity" type="text" paramvalue="regItem.qtyRequested" id="quantityRequested" title="${titleqt}" iclass="valQtyGiftReg input_tiny_giftView quantity-input qty itemQuantity" maxlength="2">
                                                                        <dsp:tagAttribute name="data-change-store-errors" value="required digits nonZero"/>
                                                                        <dsp:tagAttribute name="data-change-store-submit" value="qty"/>
                                                                        <dsp:tagAttribute name="aria-required" value="false"/>
                                                                        <dsp:tagAttribute name="aria-labelledby" value="quantityRequested"/>
                                                                    </dsp:input>
                                                                  	<dsp:getvalueof var="regItemOldQty" param="regItem.qtyRequested" />

																	<dsp:input type="hidden" bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].regItemOldQty" paramvalue="regItem.qtyRequested" id="oldquantityd"/>	
																	<a href='#' class="button plus secondary"><span></span></a>
															</div>
													</div>

													<div class="productLastColumn small-12 medium-2 columns print-2">
                                                        <input type="hidden" name="regItemOldQty" value="${regItemOldQty}" class="frmAjaxSubmitData" />
                                                        <dsp:input bean="GiftRegistryFormHandler.value.registryId" type="hidden" paramvalue="registryId" />
                                                        <input type="hidden" name="registryId" value="${registryId}" class="registryId frmAjaxSubmitData" />
                                                        <dsp:input bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].rowId" type="hidden" paramvalue="regItem.rowID"/>
                                                        <dsp:input bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].sku" type="hidden" paramvalue="regItem.sku"/>
                                                        <dsp:getvalueof var="skuId" param="regItem.sku" />
                                                    	<dsp:getvalueof var="rowId" param="regItem.rowID" />
                                                        <input type="hidden" name="skuId" value="${skuId}" class="skuId frmAjaxSubmitData" />
                                                    	<input type="hidden" name="rowId" value="${rowId}" class="frmAjaxSubmitData" />
                                                    	
                                                    	<dsp:input bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].itemTypes" type="hidden" value="${itemType}"/>
														<input type="hidden" name="itemTypes" value="${itemType}" class="frmAjaxSubmitData" />
														<input type="hidden" name="updateDslFromModal" value="" class="frmAjaxSubmitData" />
														<input type="hidden" name="alternateNum" value="" class="frmAjaxSubmitData" />
														<input type="hidden" name="refNum" value="${refNum}" class="frmAjaxSubmitData" />
														<input type="hidden" name="personalizationCode" value="${personalizationType}" class="frmAjaxSubmitData" /> 
														<c:choose>
															<c:when test="${not empty ltlShipMethod}">
															<input type="hidden" name="ltlDeliveryServices" value="${ltlShipMethod}" class="frmAjaxSubmitData" />
															</c:when>
															<c:otherwise>
															<input type="hidden" name="ltlDeliveryServices" value="" class="frmAjaxSubmitData" />
															</c:otherwise>
														</c:choose>
														
														<dsp:input bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].ltlDeliveryPrices" type="hidden" value="${ltlDeliveryPrices}"/>
														<input type="hidden" name="ltlDeliveryPrices" value="${itemType}" class="frmAjaxSubmitData" />
																												                                                  
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
                                                     <%--    <dsp:getvalueof var="ownerUrl" value="${contextPath}/giftregistry/view_registry_owner.jsp?registryId=${registryId}&eventType=${eventType}"/>
                                                        <dsp:input bean="GiftRegistryFormHandler.successURL"  type="hidden" value="${ownerUrl}" />
                                                        <dsp:input bean="GiftRegistryFormHandler.errorURL"	type="hidden" value="${ownerUrl}" /> --%>
                                                        <dsp:input bean="EmailAFriendFormHandler.queryParam"  type="hidden" value="registryId=${registryId}&eventType=${eventType}" />
                                                     <dsp:input bean="EmailAFriendFormHandler.fromPage" type="hidden" value="registryItemsOwnerCategory" />
                                                        <dsp:input bean="GiftRegistryFormHandler.updateItemToGiftRegistry" id="update${idxCat}${idx}" 	type="submit" style="display:none" value="Update"	/>
                                                        <dsp:input bean="GiftRegistryFormHandler.removeItemFromGiftRegistry" id="remove${idxCat}${idx}" 	type="submit" style="display:none"  value="Remove" />
                                                        <c:set var="valueIndex" value="${valueIndex + 1}"/>
                                                        <%-- <div class="small-6 columns"> --%>
														<div class="button_active button_active_orange">
															<a href="#" data-trigger-button="update${idxCat}${idx}" class="validateQuantity tiny button service" onclick="omniRegistryUpdateItem('${productId}','${eventType}','${registryId}', '${skuId}');"><bbbl:label key='lbl_mng_regitem_update' language="${pageContext.request.locale.language}" /></a>
														</div>
														<%-- </div> --%>
														
														<a href="#" data-submit-button="remove${idxCat}${idx}" class="btnAjaxSubmitRegistry buttonTextLink tiny button secondary" onclick="omniRegistryRemoveItem('${productId}','${eventType}','${registryId}', '${skuId}');" ><bbbl:label key='lbl_mng_regitem_remove' language="${pageContext.request.locale.language}" /></a>
														<div class="clear"></div>
														<dsp:getvalueof param="regItem.qtyRequested" var="requestedQuantity" />
														<dsp:getvalueof param="regItem.qtyPurchased" var="purchasedQuantity" />
														<!-- LTL-328 Hide add to cart button and show not availability message for LTL item -->
														<c:choose>
															<c:when test="${(((isBelowLineItem eq 'false') || inStoreSku) && (not ltlItemFlag || enableLTLReg))}">
                                                                <c:choose>
									                          		<c:when test="${requestedQuantity > purchasedQuantity}">

																		<dsp:droplet name="TBSItemExclusionDroplet">
																		<dsp:param name="skuId" value="${skuID}" />
																		<dsp:param name="siteId" value="${currentSiteId}" />
																		<dsp:oparam name="output">
																		<dsp:getvalueof var="isItemExcluded" param="validItemExcludedSku"/>
																		<dsp:getvalueof var="caDisabled" param="caDisabled"/>
																		<dsp:getvalueof var="reasonCode" param="reasonCode"/>
											
																		<c:choose>
																			<c:when test="${isItemExcluded || caDisabled || (enableKatoriFlag eq false && not empty refNum)}"> 
																				<div class="addToCart hidden button_disabled">
																					<c:set var="lblAddTOCart"><bbbl:label key='lbl_mng_regitem_additemcart' language="${pageContext.request.locale.language}" /></c:set>
																					<input  class="tiny button transactional" type="button" diasbled="disabled" name="btnAddToCart" title="${lblAddTOCart}" value="${lblAddTOCart}" role="button" aria-pressed="false" />
																				</div>
																				<div style="color:red;font-weight:bold;">	
									
																			<dsp:include page="/browse/frag/reasonCodeMessage.jsp" flush="true" >
																				<dsp:param name="reasonCode" value="${reasonCode}"/>
																			</dsp:include>
							    											</div> 
																			</c:when>
																			<c:otherwise>
																				<div class="addToCart">
																					<c:set var="lblAddTOCart"><bbbl:label key='lbl_mng_regitem_additemcart' language="${pageContext.request.locale.language}" /></c:set>
																					<input class="tiny button transactional" type="button" name="btnAddToCart" title="${lblAddTOCart}" value="${lblAddTOCart}" role="button" aria-pressed="false" />
																				</div>
																			</c:otherwise>
																		</c:choose>
																		</dsp:oparam>
																	</dsp:droplet>
																	</c:when>
													            	<c:otherwise>
																		<div class="purchasedConfirmation">
															 				 <h2><bbbl:label key='lbl_mng_regitem_purchased_confirm' language="${pageContext.request.locale.language}" /></h2>
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
													</div>
												</div>
												
												<div class="productFooter columns clearfix">
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
																						<li><a href="${actionURL}" class="popup"><span><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></span></a></li>
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
																		</dsp:droplet>
																	</c:if>							
																</dsp:oparam>
															</dsp:droplet>		
                                                            <%-- <c:if test="${skuDetailVO.shippingSurcharge gt 0.0}">
																<li class="${nextStyle}"><bbbl:label key="lbl_mng_surcharge" language="<c:out param='${language}'/>"/>${skuDetailVO.shippingSurcharge}</li>
															</c:if> --%>
														</ul>
												</div>
												
										</div>								
                                        							
                                    </div>										
									<hr/>
							</dsp:oparam>
							
						</dsp:droplet>
                            </div>
                    </c:if>
						<%-- <c:if test="${count ne 0 }"><li class="noBorder"><a href="#backToTop"><bbbl:label key='lbl_mng_regitem_back_to_top' language="${pageContext.request.locale.language}" /></a></li></c:if> --%>
					</c:if>
				</dsp:oparam>
			</dsp:droplet>
            <c:if test="${countIE eq sizeIE}">
				</dd>
			</c:if>
		</dsp:oparam>
	</dsp:droplet>
	</dl>
	</dsp:form>
	<c:set var ="operation"><dsp:valueof bean="GiftRegistryFormHandler.registryItemOperation"/></c:set>
	<c:set var ="removedProdID"><dsp:valueof bean="GiftRegistryFormHandler.removedProductId"/></c:set>
	<c:set var ="addedProdID"><dsp:valueof bean="GiftRegistryFormHandler.productStringAddItemCertona"/></c:set>
	<script type="text/javascript">
	$(document).ready(function(){
        $(document).foundation('accordion', 'reflow');
    });
	var operation='${operation}';
			 if ( operation == 'addedCertonaItem') {
	    	var BBB = BBB || {};
	    	
	    	BBB.registryInfo = {
	    			registryPagename: "itemAddedToRegistryFromCertona",
	    			products : '${addedProdID}',
	    			var23 : '${eventType}',
	    			var24 : '${registryId}'
	    		};
	    	}
    </script>
</dsp:page>