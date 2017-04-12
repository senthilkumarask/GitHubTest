<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/DisplayShippingRestrictionsDroplet" />
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
 
<div id="shippingRestrictionsCheckout" title="Shipping Restrictions">
 <div class="width_8 clearfix">
	<div class="error"></div>
	<div class="shippingRestrictionErrorAddress">
	  <dsp:droplet name="DisplayShippingRestrictionsDroplet">
	  <dsp:param name="order" value="${order}"/>
	  <dsp:oparam name="output">
	  <dsp:droplet name="ForEach">
		<dsp:param name="array" param="mapSkuRestrictedZip"/>
			 
			<dsp:oparam name="output">
			<dsp:getvalueof var="key" param="key"/>
				<p class="shippingErrorMsg">    
				<bbbl:label key="lbl_checkout_zipres_msg" language ="${pageContext.request.locale.language}"/>
				&nbsp;${key}&nbsp;
				<bbbt:textArea key="txt_checkout_zipres_msg" language ="${pageContext.request.locale.language}"/>
			    </p> 
				<dsp:getvalueof var="subelement" param="element" />
			   <dsp:droplet name="ForEach">
			     <dsp:param name="array" value="${subelement}"/>
			       <dsp:oparam name="output">
			       <dsp:getvalueof var="skuImage" param="element.skuImage"/>
			       <dsp:getvalueof var="skuName" param="element.skuName"/>
			       <dsp:getvalueof var="skuDescription" param="element.skuDescription"/>
			       <dsp:getvalueof var="address" param="element.address"/>
					
		
			<dsp:getvalueof bean="ShoppingCart.current.registryMap" var="registratantVO1"/>	
			<c:forEach var="entry" items="${registratantVO1}">
			<c:set var="regAddr" value=""/>
			<c:set var="regAddrStatus" value="false"/>
			<c:if test="${entry.value.shippingAddress.addressLine1 ne null && fn:length(entry.value.shippingAddress.addressLine1) > 0}">
				<c:set var="regAddr" value="${entry.value.shippingAddress.addressLine1},"/>
			</c:if>
			<c:if test="${entry.value.shippingAddress.addressLine2 ne null && fn:length(entry.value.shippingAddress.addressLine2) > 0}">
				<c:set var="regAddr" value="${regAddr}${entry.value.shippingAddress.addressLine2},"/>
			</c:if>

			<c:if test="${entry.value.shippingAddress.city ne null && fn:length(entry.value.shippingAddress.city) > 0}">
				<c:set var="regAddr" value="${regAddr}${entry.value.shippingAddress.city},"/>
			</c:if>

			<c:if test="${entry.value.shippingAddress.state ne null && fn:length(entry.value.shippingAddress.state) > 0}">
				<c:set var="regAddr" value="${regAddr}${entry.value.shippingAddress.state}"/>
			</c:if>

			<c:if test="${entry.value.shippingAddress.zip ne null && fn:length(entry.value.shippingAddress.zip) > 0}">
				<c:set var="regAddr" value="${regAddr}${entry.value.shippingAddress.zip}"/>
			</c:if>
					
						  
			<c:if test="${regAddr eq address}">
				<c:set var="regAddrStatus" value="true"/>
				<c:set var="regName" value="${entry.value.primaryRegistrantFirstName}"/>
			</c:if>

			</c:forEach>
			 <c:choose>
				 <c:when test="${regAddrStatus ne true}">
				  <p class="error shippingItemErrorMsg"><bbbl:label key="lbl_checkout_zipdetails" language ="${pageContext.request.locale.language}"/></p>
				  <p class="bold shippingItemAddress">
				 		${address}
				 	</p>
				 </c:when>
				 <c:otherwise>
				 	 <p class="error shippingItemErrorMsg">
				 	 <bbbl:label key="txt_checkout_zip_regaddr1" language ="${pageContext.request.locale.language}"/>
				 	 ${regName}
				 	 <bbbl:label key="txt_checkout_zip_regaddr2" language ="${pageContext.request.locale.language}"/> 
				 	 </p>
				 </c:otherwise>
			 </c:choose>
					
					
				

					 <div class="shippingRestrictItemBlock clearfix">
						<c:choose>
						   <c:when test="${empty skuImage}">
							  <dsp:img width="83" height="83" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${skuName}"/>
							</c:when>
							<c:otherwise>
							  <dsp:img src="${scene7Path}/${skuImage}" alt="${skuName}" width="83" height="83" onerror="${imagePath}/_assets/global/images/no_image_available.jpg"/>
							</c:otherwise>
						</c:choose>
						<span class="shippingRestrictItemName">${skuDescription}</span>
				     </div>
			     </dsp:oparam>
			   </dsp:droplet>
			 </dsp:oparam> 
	   </dsp:droplet>	
	  </dsp:oparam>
	</dsp:droplet> 
 </div>
	 <div class="button button_active buttonFirst">
		<a href="${contextPath}/cart/cart.jsp"><bbbl:label key="lbl_checkout_zip_return_cart" language ="${pageContext.request.locale.language}"/></a>
	 </div>
	  <c:set var="lbl_checkout_try_shipping_address">
		<bbbl:label key="lbl_checkout_try_shipping_address" language="${pageContext.request.locale.language}" />
	  </c:set>
	 <div class="button button_active">
		<input type="button" class="close-any-dialog" value="${lbl_checkout_try_shipping_address}" role="button" aria-pressed="false">
	</div>
 </div>
</div>	
</dsp:page>
