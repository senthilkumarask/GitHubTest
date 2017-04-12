<dsp:page>
 <c:set var="localStorePDP">
            <bbbc:config key="LOCAL_STORE_PDP_FLAG" configName="FlagDrivenFunctions" />
         </c:set> 
	<dsp:getvalueof var="inStock" param="inStock" />
	<dsp:getvalueof var="pDefaultChildSku" param="pDefaultChildSku" />
	<dsp:getvalueof var="skuIdfromURL" param="skuIdfromURL" />
	<dsp:getvalueof var="certonaPrice" param="certonaPrice" />
	<dsp:getvalueof var="oosProdId" param="oosProdId" />
	<dsp:getvalueof var="OutOfStockOn" param="OutOfStockOn" />
	<dsp:getvalueof var="itemAlreadyPersonalized" param="itemAlreadyPersonalized" />
	<dsp:getvalueof var="personalizationComplete" param="personalizationComplete" />
	<dsp:getvalueof var="personalizationType" param="personalizationType" />
	<dsp:getvalueof var="isInternationalCustomer"
		param="isInternationalCustomer" />
	<dsp:getvalueof var="parentProductId" param="parentProductId" />
	<dsp:getvalueof var="omniPrice" param="omniPrice" />
	<dsp:getvalueof var="bts" param="bts" />
	<dsp:getvalueof var="isFromPDP" param="isFromPDP" />
	<dsp:getvalueof var="MapQuestOn" param="MapQuestOn" />
	<dsp:getvalueof var="freeShipingMsg" param="freeShipingMsg" />
	<dsp:getvalueof var="salePriceRangeDescription"
		param="productVO.salePriceRangeDescription" />
    <dsp:getvalueof var="defaultPriceRangeDescription"
		param="productVO.defaultPriceRangeDescription" />
	<dsp:getvalueof var="CategoryId" param="CatalogRefId" />
	<dsp:getvalueof var="priceLabelCodeSKU" param="priceLabelCodeSKU"/>
   	<dsp:getvalueof var="inCartFlagSKU" param="inCartFlagSKU"/>
	<dsp:getvalueof var="porchPayLoadJson" param="porchPayLoadJson"/>

	<div
		class="grid_6 priceQuantity noPadTop clearfix <c:if test="${inStock==false}">priceQuantityNotAvailable</c:if>">
		<c:set var="format" value="${defaultPriceRangeDescription}"/>
		<input type="hidden" value="true" id="isFromPDP">
		<dsp:getvalueof var="isdynamicPriceEligible" param="productVO.dynamicPricingProduct" />
		<dsp:getvalueof var="priceLabelCodeProd" param="productVO.priceLabelCode" />
		<dsp:getvalueof var="inCartFlagProd" param="productVO.inCartFlag" />
		<dsp:getvalueof var="priceRangeDescription" param="productVO.priceRangeDescription" />
		<dsp:getvalueof var="childSKUs" param="productVO.childSKUs" />
		<c:choose>
			<c:when test="${not empty pDefaultChildSku}">
			<c:set var ="itempropClass">http://schema.org/Offer</c:set>
			</c:when>
			<c:when test="${fn:length(childSKUs) eq 1}">
				<c:set var ="itempropClass">http://schema.org/Offer</c:set>
			</c:when>
			<c:otherwise>
				<c:set var ="itempropClass">
					<c:choose>
						<c:when test="${fn:contains(format, '-')}">http://schema.org/AggregateOffer</c:when>
						<c:otherwise>http://schema.org/Offer</c:otherwise>
					</c:choose>
				</c:set>
			</c:otherwise>
		</c:choose>
		<div itemprop="offers" itemscope itemtype="${itempropClass}">	
			<div class="prodPrice">
                 <dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
								<dsp:param name="product" param="productId" />
								<dsp:param name="itemDescriptorName" value="product" />
								<dsp:param name="repositoryName"
									value="/atg/commerce/catalog/ProductCatalog" />
								<dsp:oparam name="output">
								<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
								<link itemprop="url" href="//www.bedbathandbeyond.com/store${finalUrl}"/>
								</dsp:oparam>
								
				</dsp:droplet>
				<c:set var="listPriceClass" value="isPrice"/>
				<c:choose>
					<c:when test="${not empty pDefaultChildSku}">
					<c:choose>
						<c:when test="${itemAlreadyPersonalized and personalizationComplete eq false && eximItemPrice <= 0.01 && (personalizationType eq 'CR') }">
							<div class="noMar priceOfProduct" style="display:inline-block">
								<c:if test="${inCartFlagSKU}">
									<div class="disPriceString highlightRed"> <bbbl:label key="lbl_discounted_incart_text" language="${pageContext.request.locale.language}" /></div>
									<c:set var="listPriceClass" value="isPrice"/>
								</c:if>
								<div class="${listPriceClass}"><bbbl:label key="lbl_dsk_pdp_price_is_tbd" language ="${pageContext.request.locale.language}"/>
								<span class="visuallyhidden countryPrice"><bbbl:label key="lbl_dsk_pdp_price_is_tbd" language ="${pageContext.request.locale.language}"/></span>
								</div>
							</div>
						</c:when> 
						<c:when test="${itemAlreadyPersonalized && (personalizationComplete eq false)  && (personalizationType eq 'PY' || personalizationType eq 'CR')}">
							<c:set var="isFromPDP" value="true:true"/>
							<div class="noMar priceOfProduct" style="display:inline-block">
								<c:if test="${inCartFlagSKU}">
									<div class="disPriceString"> <bbbl:label key="lbl_discounted_incart_text" language="${pageContext.request.locale.language}"/></div>
									<c:set var="listPriceClass" value="isPrice"/>
								</c:if>
								<div class="${listPriceClass}">
									<c:set var="format"><dsp:valueof value="${defaultPriceRange}"/></c:set>
									<c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${eximItemPrice}"/></c:set>
									<dsp:valueof symbol="${isFromPDP}" format="${format}" converter="formattedPrice" value="${priceFromConvertor}" />
									 <span class="visuallyhidden countryPrice">${shopperCurrency}${eximItemPrice }</span>
								</div>
							</div>
						</c:when>
						<c:when test="${itemAlreadyPersonalized && (personalizationComplete eq false)  && (personalizationType eq 'PB')}">
							<c:set var="isFromPDP" value="true:true"/>
							<div class="noMar priceOfProduct" style="display:inline-block">
								<c:if test="${inCartFlagSKU}">
									<div class="disPriceString highlightRed"> <bbbl:label key="lbl_discounted_incart_text" language="${pageContext.request.locale.language}" /> </div>
									<c:set var="listPriceClass" value="isPrice"/>
								</c:if>
								<div class="${listPriceClass}">
									<c:set var="format"><dsp:valueof value="${defaultPriceRange}"/></c:set>
									<c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${eximItemPrice}"/></c:set>
									<dsp:valueof symbol="${isFromPDP}" format="${format}" converter="formattedPrice" value="${priceFromConvertor}" />
									 <span class="visuallyhidden countryPrice">${shopperCurrency}${eximItemPrice }</span>
								</div>
							</div>
						</c:when>
						<c:when test="${itemAlreadyPersonalized}">
							<c:set var="isFromPDP" value="true:true"/>
							<div class="noMar priceOfProduct" style="display:inline-block">
								<c:if test="${inCartFlagSKU}">
									<div class="disPriceString highlightRed"> <bbbl:label key="lbl_discounted_incart_text" language="${pageContext.request.locale.language}" /> </div>
									<c:set var="listPriceClass" value="isPrice"/>
								</c:if>
								<div class="${listPriceClass}">
										<c:set var="format"><dsp:valueof value="${defaultPriceRange}"/></c:set>
										<c:set var="priceFromConvertor"><dsp:valueof converter="currency" value="${eximItemPrice}"/></c:set>
										<dsp:valueof symbol="${isFromPDP}" format="${format}" converter="formattedPrice" value="${priceFromConvertor}" />
										 <span class="visuallyhidden countryPrice">${shopperCurrency}${eximItemPrice }</span>
								</div>
							</div>
						</c:when>
					<c:otherwise>
							<dsp:include page="product_details_price.jsp">
								<dsp:param name="product" param="productId" />
								<dsp:param name="sku" value="${pDefaultChildSku}" />
								<dsp:param name="isFromPDP" value="${isFromPDP}" />
								<dsp:param name="priceLabelCodeSKU" value="${priceLabelCodeSKU}" />
								<dsp:param name="inCartFlagSKU" value="${inCartFlagSKU}" />
								
							</dsp:include> <c:if test="${skuIdfromURL == pDefaultChildSku}">
							<input id="certonaPrice" type="hidden" value="${certonaPrice}" />
						</c:if>
					</c:otherwise>
					</c:choose>
					
					</c:when>
					<c:otherwise>
							<dsp:include page="browse_price_frag.jsp">
								<dsp:param name="priceLabelCode" value="${priceLabelCodeProd}" />
								<dsp:param name="inCartFlag" value="${inCartFlagProd}" />
								<dsp:param name="salePrice" value="${salePriceRangeDescription}" />
								<dsp:param name="listPrice" value="${priceRangeDescription}" />
								<dsp:param name="defaultPriceRange" value="${defaultPriceRange}" />
								<dsp:param name="isdynamicPriceEligible" value="${isdynamicPriceEligible}" />
								<dsp:param name="isFromPDP" value="true" />
								<dsp:param name="shopperCurrency" value="${shopperCurrency}" />
							</dsp:include>
					</c:otherwise>
				</c:choose>
				<div class="freeShipBadge">${freeShipingMsg}</div>
				
			</div>
			<span class="prodAvailabilityStatus hidden"><c:choose>
					<c:when test="${inStock==false}">
						<link itemprop="availability" href="http://schema.org/OutOfStock" />
						<bbbl:label key='lbl_pdp_out_of_stock'
							language="${pageContext.request.locale.language}" />
					</c:when>
					<c:otherwise>
						<link itemprop="availability" href="http://schema.org/InStock" />
						<bbbl:label key='lbl_pdp_in_stock'
							language="${pageContext.request.locale.language}" />
					</c:otherwise>
				</c:choose>
			</span>
			<span class="pricesbjctchnge <c:if test="${not (not empty pDefaultChildSku && itemAlreadyPersonalized && (personalizationComplete eq false)  && (personalizationType eq 'PY' || personalizationType eq 'CR'))}">hidden</c:if>"><bbbl:label key="lbl_price_change" language="${pageContext.request.locale.language}" /></li></span>
		</div>
		<div class="quantityPDP">
			<%-- <label id="lblquantity" class="fl"><bbbl:label key='lbl_pdp_product_quantity' language="${pageContext.request.locale.language}" /></label> --%>
			<div class="spinner fr">
				<%--
				<label id="lblprodDetailDescQty" class="txtOffScreen" for="prodDetailDescQty"><bbbl:label key="lbl_decrease_quantity" language="${pageContext.request.locale.language}" /></label>
				--%>
				<a href="#" class="scrollDown down" id="prodDetailDescQty"
					title="<bbbl:label key='lbl_decrease_quantity' language="${pageContext.request.locale.language}" />"><span
					class="txtOffScreen" aria-hidden="true"><bbbl:label
							key="lbl_decrease_quantity"
							language="${pageContext.request.locale.language}" /></span></a> <span
					class="incDec visuallyhidden" aria-live="assertive"
					aria-atomic="true"></span> <input id="quantity"
					title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />"
					class="fl addItemToRegis _qty itemQuantity addItemToList"
					type="text" name="qty" role="textbox" value="1" maxlength="2"
					data-change-store-submit="qty"
					data-change-store-errors="required digits nonZero"
					aria-required="true" aria-describedby="quantity" />
				<%--<label id="lblprodDetailIncQty" class="txtOffScreen" for="prodDetailIncQty"><bbbl:label key="lbl_increase_quantity" language="${pageContext.request.locale.language}" /></label>--%>
				<a href="#" class="scrollUp up" id="prodDetailIncQty"
					title="<bbbl:label key='lbl_increase_quantity' language="${pageContext.request.locale.language}" />"><span
					class="txtOffScreen" aria-hidden="true"><bbbl:label
							key="lbl_increase_quantity"
							language="${pageContext.request.locale.language}" /></span></a>
				<dsp:getvalueof var="prodId" param="productId" />
				<dsp:getvalueof var="regId" param="registryId" />
				<input type="hidden" name="registryId"
					class="sflRegistryId  addItemToRegis addItemToList"
					value="${regId}" data-change-store-submit="registryId" /> <input
					type="hidden" name="prodId"
					class="_prodId addItemToRegis productId addItemToList"
					value="${prodId}" data-change-store-submit="prodId"
					data-change-store-errors="required"
					data-change-store-internalData="true" /> <input type="hidden"
					name="skuId" value="${pDefaultChildSku}"
					class="addItemToRegis _skuId addItemToList changeStoreSkuId"
					data-change-store-submit="skuId"
					data-change-store-errors="required"
					data-change-store-internalData="true" /> <input type="hidden"
					name="price" value="${omniPrice}"
					class="addItemToList addItemToRegis" /> 
					<input type="hidden"
					name="salePrice" value="${sflSalePrice}" class="addItemToList addItemToRegis"/>						
					<input type="hidden"
					name="priceNoCur" value="${certonaPrice}"
					class="addItemToList addItemToRegis" /> <input type="hidden"
					class="addToCartSubmitData" name="storeId" value=""
					data-change-store-storeid="storeId" /> <input type="hidden"
					class="addToCartSubmitData" name="bts" value="${bts}"
					data-change-store-storeid="bts" />
				<c:if test="${not empty regId}">
					<input type="hidden" class="addToCartSubmitData" name="registryId"
						value="${regId}" data-change-store-submit="registryId" />
				</c:if>
				
				<input type="hidden" class="addToCartSubmitData addToRegSubmitData" name="porchPayLoadJson" value='${porchPayLoadJson}'/>
			</div>
		</div>
		<dsp:getvalueof var="emailStockAlertsEnabled"
			param="pSKUDetailVO.emailStockAlertsEnabled" />
		<div
			class="message noMar cb marTop_20 marBottom_20 <c:if test="${inStock==true}">hidden</c:if>">
			<c:choose>
				<c:when test="${inStock==true}">
					<input type="hidden" value="" name="oosSKUId" class="_oosSKUId" />
				</c:when>
				<c:otherwise>
					<input type="hidden" value="${pDefaultChildSku}" name="oosSKUId"
						class="_oosSKUId" />
				</c:otherwise>
			</c:choose>
			<input type="hidden" value="${oosProdId}" name="oosProdId" />
			

			<c:if test="${OutOfStockOn}">
				<dsp:getvalueof var="bopusExcluded" param="pSKUDetailVO.bopusAllowed" />
				<c:choose>
					<c:when
						test="${(null eq emailStockAlertsEnabled) || (emailStockAlertsEnabled==true)}">
						<div class="error" aria-hidden="true">
				<bbbl:label key='lbl_pdp_product_notavailable_shipping'
					language="${pageContext.request.locale.language}" />
			</div>
						<div class="info">
							<a class="lnkNotifyOOS" href="#" aria-label="<bbbl:label key='lbl_oos_notify_me_aria'
					language='${pageContext.request.locale.language}' />"><bbbl:label
									key='lbl_pdp_product_notify_item_available'
									language="${pageContext.request.locale.language}" /></a>
						</div>
					</c:when>
					<c:otherwise>
						<div class="error" <c:if test="${MapQuestOn && not bopusExcluded && not isInternationalCustomer}">aria-hidden="true"</c:if>>
				<bbbl:label key='lbl_pdp_product_notavailable_shipping'
					language="${pageContext.request.locale.language}" />
			</div>
						<div class="info hidden">
							<a class="lnkNotifyOOS" href="#" aria-label="<bbbl:label key='lbl_oos_notify_me_aria' language='${pageContext.request.locale.language}' />"><bbbl:label
									key='lbl_pdp_product_notify_item_available'
									language="${pageContext.request.locale.language}" /></a>
						</div>
					</c:otherwise>
				</c:choose>
			</c:if>
			<%--   Disabled the findInStore for internationalUser --%>
			<c:if test="${MapQuestOn}">
				<dsp:getvalueof var="bopusAllowed" param="pSKUDetailVO.bopusAllowed" />
				
			<c:if test="${localStorePDP eq false}">
				<c:if test="${not bopusAllowed}">
					<c:choose>
						<c:when test='${isInternationalCustomer}'>
							<div class="info disabled">
								<a href="javascript:void(0);" class="changeStore disabled" aria-hidden="true"><bbbl:label
										key='lbl_pdp_product_findstore_near'
										language="${pageContext.request.locale.language}" /></a>
							</div>
						</c:when>
						<c:otherwise>
							<div class="info">
								<a href="javascript:void(0);" class="changeStore"
									onclick="pdpOmnitureProxy('${parentProductId}', 'findinstore')" aria-label="<bbbl:label key='lbl_oos_find_in_store_aria'
									language='${pageContext.request.locale.language}' />"><bbbl:label
										key='lbl_pdp_product_findstore_near'
										language="${pageContext.request.locale.language}" /></a>
							</div>
						</c:otherwise>
					</c:choose>
				</c:if>
			</c:if>
			</c:if>
		</div>
	</div>
</dsp:page>