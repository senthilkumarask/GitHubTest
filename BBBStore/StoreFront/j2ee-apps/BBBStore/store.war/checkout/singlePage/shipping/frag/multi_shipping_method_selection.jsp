<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/GetApplicableShippingMethodsDroplet" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart" />
<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
<c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
 
 
<dsp:getvalueof var="index" param="cisiIndex" />
<dsp:getvalueof var="sku" param="sku" />
<dsp:getvalueof var="shippingMethod" param="shippingMethod" />
<fieldset class="radioGroup noMarTop">
	<legend class="hidden">Shipping Method</legend>
	
	<dsp:droplet name="GetApplicableShippingMethodsDroplet">
		<dsp:param name="operation" value="perSku" />
		<dsp:param name="order" bean="ShoppingCart.current" />
		
		<dsp:oparam name="output">
			<dsp:getvalueof var="mapShip" param="skuMethodsMap"></dsp:getvalueof>
			<dsp:droplet name="ForEach">
				<dsp:param name="array" value="${mapShip[sku]}" />
				<dsp:param name="sortProperties" value="shippingCharge,shipMethodId"/>
				<dsp:oparam name="output">
					 <dsp:getvalueof var="countIndex" param="count" />
					 <dsp:getvalueof var="shipMethodDescription" param="element.shipMethodDescription" />
					 <dsp:getvalueof var="shipMethodId" param="element.shipMethodId" />
					 <dsp:getvalueof var="cisiCount" param="cisiCount" />
					 
					 <div class="radioItem input clearfix">		
						<div class="radio">				
							<c:choose>
								<c:when test="${countIndex eq 1}">
									<dsp:input type="radio" bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingMethod"
										   value="${shipMethodId}" name="shippingMethods_ProdName_${index}" id="shippingMethod_${index}${countIndex}" checked="true" >
										   <dsp:tagAttribute name="role" value="radio"/>
										   </dsp:input>
								</c:when>
								<c:otherwise>
									<dsp:input type="radio" bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingMethod"
										   value="${shipMethodId}" name="shippingMethods_ProdName_${index}" id="shippingMethod_${index}${countIndex}" >
										   <dsp:tagAttribute name="role" value="radio"/>
										   </dsp:input>
								</c:otherwise>
							</c:choose>
						</div>
						<div class="label">
							<label for="shippingMethod_${index}${countIndex}">
								<span>
									${shipMethodDescription}									
								</span>
							</label>
						</div>
					</div>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>	
</fieldset>
</dsp:page>