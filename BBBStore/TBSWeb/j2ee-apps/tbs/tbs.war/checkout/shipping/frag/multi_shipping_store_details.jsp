<dsp:page>
<dsp:importbean bean="/com/bbb/selfservice/SearchStoreDroplet"/>

<c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
<label>
<dsp:droplet name="SearchStoreDroplet">
	<dsp:getvalueof var="productId" param="productId" />
	<dsp:getvalueof var="registryId" param="registryId" />
	<dsp:getvalueof var="index" param="cisiIndex" />
	<dsp:getvalueof var="sku" param="sku" />
	<dsp:getvalueof var="shippingMethod" param="shippingMethod" />
	<dsp:getvalueof var="shippingGroupName" param="shippingGroupName" />
	<dsp:getvalueof param="commItem.quantity" var="qty"/>
	<dsp:getvalueof param="commItem.id" var="commerceid"/>
	<dsp:getvalueof var="skuid" param="commItem.auxiliaryData.catalogRef.id" />
		<dsp:oparam name="output">
		<strong>
			<bbbl:label key="lbl_shipping_store_pickup" language="<c:out param='${language}'/>"/>
		</strong><br/>
		
			<bbbl:label key="lbl_shipping_available_for_store_pickup" language="<c:out param='${language}'/>"/>
		<div class="address">
			<strong><dsp:valueof param="StoreDetails.storeName"/></strong>
			<div class="street"><dsp:valueof param="StoreDetails.address"/>
			<dsp:valueof param="StoreDetails.city"/>,&nbsp;<dsp:valueof param="StoreDetails.state"/>,&nbsp;<dsp:valueof param="StoreDetails.postalCode"/>
			<div class="zipCode"><dsp:valueof param="StoreDetails.storePhone"/>
		</div>

	  <label class="no-padding-left" id="lblshippingMethodStorePickup" for="shippingMethod_${index}${sku}StorePickup" data-qty="${qty}" data-shipid="${shippingGroupName}" data-commerceid="${commerceid}" data-skuid="${skuid}">
			<input type="radio" class="hidden" name="shippingMethods_ProdName_${index}${sku}" id="shippingMethod_${index}${sku}StorePickup" value="storepickup">			
			<a><bbbl:label key="lbl_cartdetail_change" language="<c:out param='${language}'/>"/></a>
		</label>
	
	</dsp:oparam>
</dsp:droplet>
</label>		
	

</dsp:page>