<dsp:page>
 <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
 <dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:getvalueof var="attribs" param="attribs" />
	<dsp:getvalueof var="skuVO" param="skuVO" />
	<c:set var ="isSkuOnSale" value="${skuVO.onSale}"/>
	
	<c:set var ="warrantyPriceThreshold">
		<bbbc:config key="WarrantyPrice" configName='ContentCatalogKeys' />
	</c:set>
	
	<c:set var="AttributePDPBelow">PDPB</c:set>
	
	<c:set var="warrantyMsgAtributeId">
		<bbbl:label key='lbl_warranty_msg_atribute_id' language="${pageContext.request.locale.language}" />
	</c:set>
	
	 <c:if test="${not empty skuVO and not empty attribs}">
	   <dsp:droplet name="PriceDroplet">
	       <dsp:param name="product" value="${skuVO.parentProdId}"/>
	           <dsp:param name="sku" value="${skuVO.skuId}"/>
	              <dsp:oparam name="output">
	                 <dsp:getvalueof var="applicablePrice" param="price.listPrice"/>
	                 <dsp:getvalueof var="profileSalePriceList" bean="Profile.salePriceList"/>
	                 <dsp:getvalueof var="profileListPrice" param="price.listPrice"/>
	                   <c:if test="${not empty profileSalePriceList && (profileListPrice > 0.01)}">
		                 <dsp:droplet name="PriceDroplet">
		                  <dsp:param name="priceList" bean="Profile.salePriceList"/>
		                        <dsp:oparam name="output"> 
		                          <dsp:getvalueof var="applicablePrice" vartype="java.lang.Double" param="price.listPrice"/>
		                        </dsp:oparam>
		                  </dsp:droplet>
	                 </c:if>
	               </dsp:oparam>
	       </dsp:droplet>
 <!--   warrantyPriceThreshold:${warrantyPriceThreshold } ,applicablePrice1:${applicablePrice }-->
	           
	      <c:if test="${ warrantyPriceThreshold < applicablePrice }">
		        <dsp:droplet name="/atg/dynamo/droplet/ForEach">
					<dsp:param value="${attribs}" name="array" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="placeHolderBelow" param="key"/>
						<c:set var ="counter" value="0"/>
						<c:if test="${(not empty placeHolderBelow) && (placeHolderBelow eq 'PDPB')}">
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param param="element" name="array" />
								<dsp:param name="sortProperties" value="+priority"/>
								<dsp:oparam name="outputStart">
								</dsp:oparam>
								<dsp:oparam name="output">
									<dsp:getvalueof var="placeHolderBelow" param="element.placeHolder"/>
									<dsp:getvalueof var="attributeDescripBelow" param="element.attributeDescrip"/>
									<dsp:getvalueof var="imageURLBelow" param="element.imageURL"/>
									<dsp:getvalueof var="actionURLBelow" param="element.actionURL"/>
									<dsp:getvalueof var="attributeName" param="element.attributeName"/>	
									<dsp:getvalueof var="atributeItemId" param="element.attributeName"/>
									<c:choose>
										<c:when test="${null ne attributeDescripBelow && atributeItemId eq warrantyMsgAtributeId  and counter < 1 }">
											<dsp:valueof param="element.attributeDescrip" valueishtml="true"/>
											 <c:set var ="counter" value="${1+counter}"/>
										</c:when>
									</c:choose>
								</dsp:oparam>
								<dsp:oparam name="outputEnd">
								</dsp:oparam>
							</dsp:droplet>
						</c:if>
					</dsp:oparam>
				</dsp:droplet>
			</c:if>
	 </c:if>
</dsp:page>