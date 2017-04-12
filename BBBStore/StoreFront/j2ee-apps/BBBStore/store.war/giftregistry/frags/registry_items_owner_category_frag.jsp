<dsp:page>
<dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:getvalueof var="productId" param="productId" />
	<dsp:getvalueof var="skuID" param="skuID" />
	<dsp:getvalueof var="personalisedCode" param="personalisedCode" />
	<dsp:getvalueof var="personlisedPrice" param="personlisedPrice" />
	<dsp:getvalueof var="topFragment" param="topFragment" />
	<dsp:getvalueof var="active" param="active" />
	<dsp:getvalueof var="upc" param="upc" />
	<dsp:getvalueof var="size" param="size" />
	<dsp:getvalueof var="color" param="color" />
	<dsp:getvalueof var="customizedPrice1" param="customizedPrice1" />
	<dsp:getvalueof var="personlisedPrice1" param="personlisedPrice1" />
	<dsp:getvalueof var="registryId" param="registryId" />
	<dsp:getvalueof var="refNum" param="refNum" />
	<dsp:getvalueof var="productName" param="productName" />
	<dsp:getvalueof var="contextPath" param="contextPath" />
	<dsp:getvalueof var="finalUrl" param="finalUrl" />
	<dsp:getvalueof var="customizationDetails" param="customizationDetails" />
	<dsp:getvalueof var="personalizedImageUrls" param="personalizedImageUrls" />
	<dsp:getvalueof var="personalizedImageUrlThumbs" param="personalizedImageUrlThumbs" />
	<dsp:getvalueof var="personalizationType" param="personalizationType" />
	<dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
	<dsp:getvalueof var="customizedPrice" param="customizedPrice" />
	<dsp:getvalueof var="regItem" param="regItem" />
	<dsp:getvalueof var="isDSLUpdateable" param="isDSLUpdateable" />
	<dsp:getvalueof var="personalizationOptionsDisplay" param="personalizationOptionsDisplay"/>
		<dsp:getvalueof var="ltlShipMethodDesc" param="ltlShipMethodDesc"/>
								<dsp:getvalueof var="deliverySurcharge" param="deliverySurcharge"/>
								<dsp:getvalueof var="assemblyFee" param="assemblyFee"/>
								<dsp:getvalueof var="shipMethodUnsupported" param="shipMethodUnsupported"/>	
								<dsp:getvalueof var="ltlItemFlag" param="ltlItemFlag"/>	
								<dsp:getvalueof var="ltlShipMethod" param="ltlShipMethod"/>	
	<dsp:getvalueof var="regAddress" param="regAddress" />
	<dsp:getvalueof var="regPublic" param="regPublic" />
	<dsp:getvalueof var="currentSiteId" param="currentSiteId" />
	<dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
	<dsp:getvalueof var="inCartFlagSKU" param="inCartFlagSKU" />
	<dsp:getvalueof var="regItemOldQty" param="regItemOldQty" />
	<dsp:getvalueof var="qtyPurchased" param="qtyPurchased" />
	<dsp:getvalueof var="rowID" param="rowID" />
	<dsp:getvalueof var="itemType" param="itemType" />
	<dsp:getvalueof var="eventType" param="eventType" />
	<dsp:getvalueof var="isBelowLineItem" param="isBelowLineItem" />
	<dsp:getvalueof var="isIntlRestricted" param="isIntlRestricted" />
	<dsp:getvalueof var="isInternationalCustomer" param="isInternationalCustomer" />
	<dsp:getvalueof var="enableLTLReg" param="enableLTLReg" />
	<c:set var="customizeCTACodes">
		<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
	</c:set>
	
	<c:choose>
		<c:when test="${defaultUserCountryCode eq 'MX'}">
			<c:set var="incartPriceList"><bbbc:config key="MexInCartPriceList" configName="ContentCatalogKeys" /></c:set>
		</c:when>
		<c:when test="${currentSiteId eq 'BedBathUS' || currentSiteId eq 'BuyBuyBaby'}">
			<c:set var="incartPriceList"><bbbc:config key="BedBathUSInCartPriceList" configName="ContentCatalogKeys" /></c:set>
		</c:when>
		<c:when test="${currentSiteId eq 'BedBathCanada'}">
			<c:set var="incartPriceList"><bbbc:config key="BedBathCanadaInCartPriceList" configName="ContentCatalogKeys" /></c:set>
		</c:when>
	  </c:choose>
	
					

	<%-- if the registry is private and no shipping address available, product links bring up edit modal--%>
	<c:choose>
		<c:when test="${empty regAddress}">
			<c:set var="productLinkClass" value="editRegInfo completeShipping" />
		</c:when>
		<c:otherwise >
			<c:set var="productLinkClass" value="lnkQVPopup" />
		</c:otherwise>
	</c:choose>


	 <div class="productName grid_2"><span class="blueName prodTitle">
			<c:choose>
				<c:when test="${active}">
				<c:if test="${ltlItemFlag && (ltlShipMethod ne null && ltlShipMethod ne '')}">
					<c:set var="ltlOpt" value="&sopt=${ltlShipMethod}"></c:set>
				</c:if>
					 <a href="${contextPath}${finalUrl}?skuId=${skuID}&registryId=${registryId}${ltlOpt}"
						class="${productLinkClass} prodTitle" data-pdp-qv-url="pdp_quick_view.jsp"  data-upc="${upc}" data-color="${color}" data-size="${size}" data-qtyPurchased="${qtyPurchased}" data-qtyRequested="${regItemOldQty}" data-deliverySurcharge="${deliverySurcharge}"
						data-skuId="${skuID}" data-registryId="${registryId}" data-sopt="${ltlShipMethod}" data-rowID="${rowID}" data-itemType="${itemType}"  data-eventType="${eventType}"
						data-productId="${productId}" data-refNum="${refNum}" data-isBelowLineItem="${isBelowLineItem}" data-isIntlRestricted="${isIntlRestricted}" data-isInternationalCustomer="${isInternationalCustomer}" data-enableLTLReg="${enableLTLReg}" data-regAddress="${regAddress}"
						title="${productName}"
						<c:if test="${not empty refNum}">data-personalisedCode="${personalisedCode}"
															data-customizedPrice="${customizedPrice1}" data-customizationDetails="${customizationDetails}" data-color="${color}" data-size="${size}" data-personalizedImageUrls="${personalizedImageUrls}"
															data-personlisedPrice="${personlisedPrice1}" data-personalizedImageUrlThumbs="${personalizedImageUrlThumbs}" data-personalizationType= "${personalizationType}"</c:if>>
						${productName} </a>
				</c:when>
				<c:otherwise>
															${productName}
														</c:otherwise>
			</c:choose>
	</span>

		<dl class="productAttributes">
			<c:if test="${upc ne null}">
				<dt>
					<bbbl:label key='lbl_mng_regitem_upc' language="${pageContext.request.locale.language}" />
				</dt>
				<dd>${upc}</dd>
			</c:if>
			<c:if test='${not empty color}'>
				<dt>
					<bbbl:label key="lbl_item_color"
						language="${pageContext.request.locale.language}" />
					:
				</dt>
				<dd>
					<dsp:valueof value="${color}" valueishtml="true" />
				</dd>
			</c:if>

			<c:if test='${not empty size}'>
				<dt>
					<bbbl:label key="lbl_item_size"
						language="${pageContext.request.locale.language}" />
					:
				</dt>
				<dd>
					<dsp:valueof value="${size}" valueishtml="true" />
				</dd>
			</c:if>
			<c:if test="${not empty personalisedCode}">
				<dt>${eximCustomizationCodesMap[personalisedCode]}</dt>
				<dd>: ${customizationDetails}</dd>
				<span class="personalizationAttr katoriPrice clearfix">
				<%-- BBBSL-8154 --%>
				<%-- <span>${personalizationOptionsDisplay}</span> --%>
					<c:choose>
						<c:when test="${personalizationType == 'PB'}">
							<div class="eximRegMsg">
															<bbbl:label key='lbl_PB_Fee_detail' language="${pageContext.request.locale.language}" />
															</div>
															</c:when>
															<c:when test="${personalizationType == 'PY'}">
															<div class="eximRegMsg">
															  <dsp:valueof value="${customizedPrice1}"/>&nbsp;<bbbl:label key='lbl_exim_added_price' language="${pageContext.request.locale.language}" />
												        	</div>
															</c:when>
															<c:when test="${personalizationType == 'CR'}">
															<c:choose>
																<c:when test="${not empty personalisedCode && fn:contains(customizeCTACodes, personalisedCode)}">
																	<c:set var="showMsg" value="lbl_exim_cr_added_price_customize"/>
																</c:when>
																<c:otherwise>
																	<c:set var="showMsg" value="lbl_exim_cr_added_price"/>
																</c:otherwise>
															</c:choose>
															<div class="eximRegMsg">
															 <dsp:include page="/global/gadgets/formattedPrice.jsp">												
												               <dsp:param name="price" value="${customizedPrice}"/>
												               <dsp:param name="showMsg" value="${showMsg}"/>
												        	</dsp:include>
															</div>
															</c:when>
					</c:choose></span>
			</c:if>
		</dl>
		
		<c:choose> 
		<c:when test="${topFragment}">
			<c:if test="${active}">
				<span class="quickViewLink"><a href="${contextPath}${finalUrl}?skuId=${skuID}&registryId=${registryId}"
				class="lnkQVPopup prodTitle" role="link" aria-label="${lblProductQuickView} ${lblOfThe} ${productName}" 
				data-pdp-qv-url="pdp_quick_view.jsp" data-upc="${upc}" data-color="${color}" data-size="${size}" data-qtyPurchased="${qtyPurchased}" data-qtyRequested="${regItemOldQty}" data-deliverySurcharge="${deliverySurcharge}"
				data-skuId="${skuID}" data-registryId="${registryId}" data-sopt="${ltlShipMethod}" data-rowID="${rowID}" data-itemType="${itemType}"  data-eventType="${eventType}"
				data-productId="${productId}" data-refNum="${refNum}" data-isBelowLineItem="${isBelowLineItem}" data-isIntlRestricted="${isIntlRestricted}" data-isInternationalCustomer="${isInternationalCustomer}" data-enableLTLReg="${enableLTLReg}" data-regAddress="${regAddress}"
				title="${productName}"
				<c:if test="${not empty refNum}">data-personalisedCode="${personalisedCode}"
															data-customizedPrice="${customizedPrice1}" data-customizationDetails="${customizationDetails}" data-color="${color}" data-size="${size}" data-personalizedImageUrls="${personalizedImageUrls}"
															data-personlisedPrice="${personlisedPrice1}" data-personalizedImageUrlThumbs="${personalizedImageUrlThumbs}" data-personalizationType= "${personalizationType}"</c:if>>
					<span class="icon-fallback-text"> <span class="icon-add"
						aria-hidden="true"></span> <span class="icon-text"
						aria-hidden="true"><bbbl:label
								key='lbl_mng_regitem_quick_view'
								language="${pageContext.request.locale.language}" /></span>
				</span> <bbbl:label key='lbl_mng_regitem_quick_view'
						language="${pageContext.request.locale.language}" />
			</a>

			</span>
			</c:if>
		</c:when>
		<c:otherwise>
			<span class="quickViewLink"> <a href="${contextPath}${finalUrl}?skuId=${skuID}&registryId=${registryId}"
				class="lnkQVPopup prodTitle" data-pdp-qv-url="pdp_quick_view.jsp" data-upc="${upc}" data-color="${color}" data-size="${size}" data-qtyPurchased="${qtyPurchased}" data-qtyRequested="${regItemOldQty}" data-deliverySurcharge="${deliverySurcharge}"
				data-skuId="${skuID}" data-registryId="${registryId}" data-rowID="${rowID}" data-itemType="${itemType}"  data-eventType="${eventType}"
				data-productId="${productId}" data-refNum="${refNum}" data-sopt="${ltlShipMethod}" data-isBelowLineItem="${isBelowLineItem}" data-isIntlRestricted="${isIntlRestricted}" data-isInternationalCustomer="${isInternationalCustomer}" data-enableLTLReg="${enableLTLReg}" data-regAddress="${regAddress}"
				title="${productName}"
				<c:if test="${not empty refNum}">data-personalisedCode="${personalisedCode}"
															data-customizedPrice="${customizedPrice1}" data-customizationDetails="${customizationDetails}" data-color="${color}" data-size="${size}" data-personalizedImageUrls="${personalizedImageUrls}"
															data-personlisedPrice="${personlisedPrice1}" data-personalizedImageUrlThumbs="${personalizedImageUrlThumbs}" data-personalizationType= "${personalizationType}"</c:if>>
					<span class="icon-fallback-text"> <span class="icon-add"
						aria-hidden="true"></span> <span class="icon-text"
						aria-hidden="true"><bbbl:label
								key='lbl_mng_regitem_quick_view'
								language="${pageContext.request.locale.language}" /></span>
				</span> <bbbl:label key='lbl_mng_regitem_quick_view'
						language="${pageContext.request.locale.language}" />
			</a>
			</span>
		</c:otherwise>
		</c:choose>
		</div>
		<c:choose>
		<c:when test="${ltlItemFlag}">
		<dsp:getvalueof var="ltlShipMethodDesc" param="regItem.ltlShipMethodDesc"/>
		<dsp:getvalueof var="deliverySurcharge" param="regItem.deliverySurcharge"/> 
		<dsp:getvalueof var="assemblyFee" param="regItem.assemblyFees"/>
		<dsp:getvalueof var="shipMethodUnsupported" param="regItem.shipMethodUnsupported"/>
			<div class="price grid_2 omega ltlPrice">
				  <span class="columnHeader"> <bbbl:label key="lbl_mng_regitem_sortprice" language="${pageContext.request.locale.language}" /></span>	

				<!--li class="totalLtl"> Total</li-->
				  <div class="toalpriceLtl"><dsp:droplet name="PriceDroplet">
					<dsp:param name="product" value="${productId}" />
					<dsp:param name="sku" value="${skuID}" />
					<dsp:oparam name="output">
					<dsp:setvalue param="theListPrice" paramvalue="price" />
						<%-- The second call is in case the incart price exists --%>
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
						<dsp:getvalueof var="profileSalePriceList"
							bean="Profile.salePriceList" />
						<c:choose>
							<c:when test="${not empty profileSalePriceList}">
								<dsp:droplet name="PriceDroplet">
									<dsp:param name="priceList" bean="Profile.salePriceList" />
									<dsp:oparam name="output">
									<c:choose>
										<c:when test="${inCartFlagSKU}">
										<c:set var="listPrice" value="${inCartPrice}" />
										</c:when>
										<c:otherwise>
											<dsp:getvalueof var="price" vartype="java.lang.Double"
												param="theListPrice.listPrice" />
											<dsp:getvalueof var="listPrice" vartype="java.lang.Double"
												param="price.listPrice" />
										</c:otherwise>
									</c:choose>
										<c:if test="${listPrice gt 0.10}">
											<dsp:include page="/global/gadgets/formattedPrice.jsp">
												<c:choose>	
													<c:when test="${shipMethodUnsupported}">
														<dsp:param name="price" value="${listPrice}" />
													</c:when>
													<c:otherwise>
													 	<dsp:param name="price" value="${listPrice+deliverySurcharge+assemblyFee }" />
													</c:otherwise>
												</c:choose>
											</dsp:include>
										</c:if>										
									</dsp:oparam>
									<dsp:oparam name="empty">
										<c:choose>
											<c:when test="${inCartFlagSKU}">
											 <c:set var="price" value="${inCartPrice}" />
											</c:when>
											<c:otherwise>
												<dsp:getvalueof var="price" vartype="java.lang.Double"
														param="theListPrice.listPrice" />
											</c:otherwise>
										</c:choose>
										<c:if test="${price gt 0.10}">
											<dsp:include page="/global/gadgets/formattedPrice.jsp">
												<c:choose>	
													<c:when test="${shipMethodUnsupported}">
														<dsp:param name="price" value="${price}" />
													</c:when>
													<c:otherwise>
													 	<dsp:param name="price" value="${price+deliverySurcharge+assemblyFee }" />
													</c:otherwise>
												</c:choose>
											</dsp:include>
										</c:if>
									</dsp:oparam>
								</dsp:droplet>
								<%-- End price droplet on sale price --%>
							</c:when>
							<c:otherwise>
							<c:choose>
								<c:when test="${inCartFlagSKU}">
									<c:set var="price" value="${inCartPrice}" />
								</c:when>
								<c:otherwise>
									<dsp:getvalueof var="price" vartype="java.lang.Double"
										param="theListPrice.listPrice" />
								</c:otherwise>
							</c:choose>
								<dsp:getvalueof var="price" vartype="java.lang.Double"
									param="theListPrice.listPrice" />
								<c:if test="${price gt 0.10}">
									<dsp:include page="/global/gadgets/formattedPrice.jsp">
										<c:choose>	
											<c:when test="${shipMethodUnsupported}">
												<dsp:param name="price" value="${price}" />
											</c:when>
											<c:otherwise>
												<dsp:param name="price" value="${price+deliverySurcharge+assemblyFee }" />
											</c:otherwise>
										</c:choose>
									</dsp:include>
								</c:if>
							</c:otherwise>
						</c:choose>
						<c:if test="${topFragment}">
							<c:if test="${not empty listPrice}">
								<c:set value="${listPrice}" var="price" />
							</c:if>
							<input type="hidden" value="${price}" class="itemPrice" />
						</c:if>
					</dsp:oparam>
				</dsp:droplet></div>
				
				<c:choose>
						<c:when test="${empty ltlShipMethod or shipMethodUnsupported}">
						<input name="shipMethodUnsupported" type="hidden" value="${shipMethodUnsupported}"/>
							 <c:choose>
								 <c:when test="${isDSLUpdateable}">
							 		 <div><a class="updateDsl"><bbbl:label key='lbl_update_service_level' language="${pageContext.request.locale.language}" />
									 <img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" />
									 </a></div>
							 		 <div class="deliveryLtl"></div>
	                         		 <div class="deliverypriceLtl"><span class="deliverypriceLtlClass"></span><img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo hidden" /> </div>
								 </c:when>
	                         	 <c:otherwise> <div class="deliveryLtl"><bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/>:</div>
	                                        <div class="deliverypriceLtl"><span class="deliverypriceLtlClass"><bbbl:label key="lbl_cart_tbd" language="${pageContext.request.locale.language}"/></span><img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" /> </div>
	                        	 </c:otherwise>
	                         </c:choose>
                         </c:when>
						<c:otherwise>
							 <c:choose>
								 <c:when test="${ltlShipMethod== 'LWA' }">
								   <div class="deliveryLtl"> <bbbl:label key="lbl_Incl_White_Glove" language="${pageContext.request.locale.language}"/> <span><bbbl:label key="lbl_With_Assembly" language="${pageContext.request.locale.language}"/></span></div>
								   <div class="deliverypriceLtl">
								       <span class="deliverypriceLtlClass"><dsp:valueof value="${deliverySurcharge+assemblyFee}" number="0.00" converter="currency"/></span>
								       <img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" />
								   </div>
								</c:when>
								<c:otherwise>
								<div class="deliveryLtl">
										 Incl ${ltlShipMethodDesc}:
	                                         </div>     <div class="deliverypriceLtl"><span class="deliverypriceLtlClass">       <c:if test="${deliverySurcharge eq 0.0}"> <bbbl:label key="lbl_shipping_free" language="${pageContext.request.locale.language}"/></c:if>
	                                                                 <c:if test="${deliverySurcharge gt 0.0}"><dsp:valueof value="${deliverySurcharge}" number="0.00" converter="currency"/></c:if>
									</span>  <img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" /></div>
									</c:otherwise>
							</c:choose>
						</c:otherwise>
					 </c:choose>
				 
				  <div class="itemLtl"><c:if test="${ltlShipMethod != null && ltlShipMethod != '' && !shipMethodUnsupported}">Item Price</c:if>
				<span class="itempriceLtl">
				<dsp:droplet name="PriceDroplet">
					<dsp:param name="product" value="${productId}" />
					<dsp:param name="sku" value="${skuID}" />
					<dsp:oparam name="output">
						<dsp:setvalue param="theListPrice" paramvalue="price" />
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
						<dsp:getvalueof var="profileSalePriceList"
							bean="Profile.salePriceList" />
						<c:choose>
							<c:when test="${not empty profileSalePriceList}">
								<dsp:droplet name="PriceDroplet">
									<dsp:param name="priceList" bean="Profile.salePriceList" />
									<dsp:oparam name="output">
										<c:choose>
										<c:when test="${inCartFlagSKU}">
											<c:set var="listPrice" value="${inCartPrice}" />
										</c:when>
										<c:otherwise>
											<dsp:getvalueof var="price" vartype="java.lang.Double"
											param="theListPrice.listPrice" />
											<dsp:getvalueof var="listPrice" vartype="java.lang.Double"
											param="price.listPrice" />
										</c:otherwise>
										</c:choose>
										<c:if test="${listPrice gt 0.10}">
										<input type="hidden" name="itemPrice" value="${listPrice}" class="frmAjaxSubmitData" />
											<c:if test="${ltlShipMethod != null && ltlShipMethod != '' && !shipMethodUnsupported}">	
											<dsp:include page="/global/gadgets/formattedPrice.jsp">
												<dsp:param name="price" value="${listPrice }" />
											</dsp:include>
											</c:if>
										</c:if>										
									</dsp:oparam>
									<dsp:oparam name="empty">
											<c:choose>
											<c:when test="${inCartFlagSKU}">
												<c:set var="price" value="${inCartPrice}" />
											</c:when>
											<c:otherwise>
												<dsp:getvalueof var="price" vartype="java.lang.Double"
											param="theListPrice.listPrice" />
											</c:otherwise>
										</c:choose>
										<input type="hidden" name="itemPrice" value="${price}" class="frmAjaxSubmitData" />
										<c:if test="${ltlShipMethod != null && ltlShipMethod != '' && !shipMethodUnsupported}">	
										<c:if test="${price gt 0.10}">
											<dsp:include page="/global/gadgets/formattedPrice.jsp">
												<dsp:param name="price" value="${price }" />
											</dsp:include>
										</c:if>
										</c:if>
									</dsp:oparam>
								</dsp:droplet>
								<%-- End price droplet on sale price --%>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${inCartFlagSKU}">
										<c:set var="price" value="${inCartPrice}" />
									</c:when>
									<c:otherwise>
										<dsp:getvalueof var="price" vartype="java.lang.Double"
									param="theListPrice.listPrice" />
									</c:otherwise>
								</c:choose>
								<c:if test="${price gt 0.10}">
								<input type="hidden" name="itemPrice" value="${price}" class="frmAjaxSubmitData" />
								<c:if test="${ltlShipMethod != null && ltlShipMethod != '' && !shipMethodUnsupported}">	
									<dsp:include page="/global/gadgets/formattedPrice.jsp">
										<dsp:param name="price" value="${price}" />
									</dsp:include>
								</c:if>
								</c:if>
							</c:otherwise>
						</c:choose>
						<c:if test="${topFragment}">
							<c:if test="${not empty listPrice}">
								<c:set value="${listPrice}" var="price" />
							</c:if>
							<input type="hidden" value="${price}" class="itemPrice" />
							<input type="hidden" name="itemPrice" value="${price}" class="frmAjaxSubmitData" />
						</c:if>
					</dsp:oparam>
				</dsp:droplet>
				</span>
			</div> 

					 </div> 
		</c:when>
		<c:otherwise>
				<div class="price grid_2 omega ltlPrice"><span class="columnHeader"><bbbl:label key='lbl_mng_regitem_sortprice' language="${pageContext.request.locale.language}" /></span>
		<c:choose>
			<c:when test="${not empty personalisedCode}">
				<div>
					<dsp:include page="/global/gadgets/formattedPrice.jsp">
						<dsp:param name="price" value="${personlisedPrice}" />
					</dsp:include>
					<span aria-hidden="true" class="fontAreial alignVeritcal">*</span>
					<div class="priceSubject fontAreial">
						<span>*</span> <span class="priceSubString"><bbbl:label
								key='lbl_price_subject_to_change'
								language="${pageContext.request.locale.language}" /></span>
					</div>
				</div>
				<input type="hidden" value="${personlisedPrice}" class="itemPrice" />
			</c:when>
			<c:otherwise>
			<span class="columnHeader rlpPrice">
				<dsp:droplet name="PriceDroplet">
					<dsp:param name="product" value="${productId}" />
					<dsp:param name="sku" value="${skuID}" />
					<dsp:oparam name="output">
					<dsp:setvalue param="theListPrice" paramvalue="price" />
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
					<dsp:getvalueof var="profileSalePriceList"
					bean="Profile.salePriceList" />
					<c:choose>
							<c:when test="${not empty profileSalePriceList}">
								<dsp:droplet name="PriceDroplet">
									<dsp:param name="priceList" bean="Profile.salePriceList" />
									<dsp:oparam name="output">
									<c:choose>
									<c:when test="${inCartFlagSKU}">
										<c:set var="listPrice" value="${inCartPrice}" />
									</c:when>
									<c:otherwise>
										<dsp:getvalueof var="price" vartype="java.lang.Double"
											param="theListPrice.listPrice" />
										<dsp:getvalueof var="listPrice" vartype="java.lang.Double"
											param="price.listPrice" />
									</c:otherwise>
								</c:choose>
										<c:if test="${listPrice gt 0.10}">
											<dsp:include page="/global/gadgets/formattedPrice.jsp">
												<dsp:param name="price" value="${listPrice }" />
											</dsp:include>
										</c:if>										
									</dsp:oparam>
									<dsp:oparam name="empty">
										<c:choose>
											<c:when test="${inCartFlagSKU}">
												<c:set var="price" value="${inCartPrice}" />
											</c:when>
											<c:otherwise>
												<dsp:getvalueof var="price" vartype="java.lang.Double"
													param="theListPrice.listPrice" />
											</c:otherwise>
										</c:choose>
										<c:if test="${price gt 0.10}">
											<dsp:include page="/global/gadgets/formattedPrice.jsp">
												<dsp:param name="price" value="${price }" />
											</dsp:include>
										</c:if>
									</dsp:oparam>
								</dsp:droplet>
								<%-- End price droplet on sale price --%>
							</c:when>
							<c:otherwise>
							<c:choose>
								<c:when test="${inCartFlagSKU}">
									<c:set var="price" value="${inCartPrice}" />
								</c:when>
								<c:otherwise>
									<dsp:getvalueof var="price" vartype="java.lang.Double"
										param="theListPrice.listPrice" />
								</c:otherwise>
							</c:choose>
								<c:if test="${price gt 0.10}">
									<dsp:include page="/global/gadgets/formattedPrice.jsp">
										<dsp:param name="price" value="${price }" />
									</dsp:include>
								</c:if>
							</c:otherwise>
						</c:choose>
						<c:if test="${topFragment}">
							<c:if test="${not empty listPrice}">
								<c:set value="${listPrice}" var="price" />
							</c:if>
							<input type="hidden" value="${price}" class="itemPrice" />
						</c:if>
					</dsp:oparam>
				</dsp:droplet>
				</span>
			</c:otherwise>
		</c:choose>
		</div>
		</c:otherwise>
		</c:choose>
</dsp:page>