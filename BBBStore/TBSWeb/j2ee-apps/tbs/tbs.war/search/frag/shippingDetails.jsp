<dsp:page>
	<dsp:importbean bean="/atg/commerce/order/OrderLookup" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
		
	<dsp:droplet name="OrderLookup">
		<dsp:param name="orderId" param="orderId" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="order" param="result"/>
				<dsp:droplet name="ForEach">
					<dsp:param name="array" value="${order.relationships}"/>
					<dsp:param name="elementName" value="relations"/>
					<dsp:oparam name="output">
						<dsp:getvalueof param="relations.relationshipClassType" var="relationClass"/>
						<c:if test="${relationClass eq 'shippingGroupCommerceItem' }">
							<dsp:getvalueof param="relations.shippingGroup.stateDetail" var="shipment"/>
							<dsp:a page="items.jsp">
								<dsp:param name="id" param="relations.commerceItem.id"/>
								<dsp:param name="commItem" param="relations.commerceItem"/>
								<c:choose>
									<c:when test="${shipment ne 'Order being Processed'}">
										Shipment &nbsp;<dsp:valueof param="count"/>
									</c:when>
									<c:otherwise>
										Items not yet shipped
									</c:otherwise>
								</c:choose>
							</dsp:a>
							<dsp:valueof param="relations.shippingGroup.priceInfo.amount" locale="en_US" converter="currency"/>
							<dsp:valueof param="relations.shippingGroup.stateDetail"/>
						</c:if><br>
					</dsp:oparam>
				</dsp:droplet>
			
		</dsp:oparam>
		<dsp:oparam name="error">
			<dsp:valueof param="errorMsg"></dsp:valueof>
		</dsp:oparam>
	</dsp:droplet>
	
</dsp:page>