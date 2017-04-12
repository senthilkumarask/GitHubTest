<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/porch/droplet/DisplayPorchRestrictionsDroplet" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart" />
<dsp:param name="order" bean="ShoppingCart.current"/>
<dsp:getvalueof param="order" var="order"/>
 <c:set var="imagePath">
	<bbbc:config key="image_host" configName="ThirdPartyURLs" />
</c:set>
<c:set var="scene7Path">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
</c:set> 
 


<div id="porchServiceRestrictionsCheckout" title='<bbbl:label key="lbl_porch_service_restriction" language ="${pageContext.request.locale.language}"/>'>
 <div class="clearfix">
	<div class="error"><bbbl:label key="lbl_porch_service_not_offered" language ="${pageContext.request.locale.language}"/></div>
	<div >
	<dsp:droplet name="DisplayPorchRestrictionsDroplet">
	  <dsp:param name="order" value="${order}"/>
	  <dsp:oparam name="output">
	  <dsp:droplet name="ForEach">
		<dsp:param name="array" param="mapPorchRestrictedAddress"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="key" param="key"/>
				 
				<dsp:getvalueof var="subelement" param="element" />
				  <p class="bold shippingItemAddress">
					 	 	${subelement} 
					 	</p>
				 
			 </dsp:oparam> 
	   </dsp:droplet>	
	  </dsp:oparam>
	</dsp:droplet>
	</div>
	

	<hr />

		<%-- set hidden input value to true and submit shipping form --%>
		<c:set var="lbl_porch_remove_service_continue">
			<bbbl:label key="lbl_porch_remove_service_continue" language="${pageContext.request.locale.language}" />
	  	</c:set>

	 	<input type="button" id="removePorchServiceCheckout" class="close-any-dialog btnPrimary button-Med " value="${lbl_porch_remove_service_continue}" role="button" aria-pressed="false">

		<c:set var="lbl_porch_try_another_shipping_address">
			<bbbl:label key="lbl_porch_try_another_shipping_address" language="${pageContext.request.locale.language}" />
	  	</c:set>
	 	
		<input type="button" id="porchEditAddress" class="close-any-dialog btnPrimary button-Med " value="${lbl_porch_try_another_shipping_address}" role="button" aria-pressed="false">
		

 </div>
</div>	
</dsp:page>
