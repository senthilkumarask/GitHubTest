<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<table class="rightContainer">
		<thead>
			<tr>
				<th>Order Id </th>
				<th>Shipping Address</th>
			</tr>
		</thead>
<dsp:droplet name="/atg/dynamo/droplet/ForEach">
			<dsp:param name="array" param="order.shippingGroups" />
			<dsp:param name="elementName" value="shippingGroup" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
					<dsp:droplet name="/atg/dynamo/droplet/Compare">
						<dsp:param name="obj1" param="shippingGroup.repositoryItem.type"/>
						<dsp:param name="obj2" value="hardgoodShippingGroup"/>
						<dsp:oparam name="equal">
						<dsp:getvalueof var="commerceItemRelationshipCount" param="shippingGroup.commerceItemRelationshipCount"/>
						
						<c:if test="${commerceItemRelationshipCount gt 0}">
									<div id="shippingAddress">									
												<dsp:include page="display_order_details.jsp" flush="true">
													<dsp:param name="order" param="order"/>
													<dsp:param name="shippingGroup" param="shippingGroup"/>
													<dsp:param name="onlineOrderNum" param="onlineOrderNum"/>
												</dsp:include>
									</div>
						</c:if>
						</dsp:oparam>
					</dsp:droplet>	
			</dsp:oparam>
</dsp:droplet>
	
<dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:param name="array" param="order.shippingGroups" />
				<dsp:param name="elementName" value="shippingGroup" />
				<dsp:oparam name="output">
					<dsp:getvalueof var="shippingGroup" param="shippingGroup"/>		
					<dsp:droplet name="/atg/dynamo/droplet/Compare">
						<dsp:param name="obj1" param="shippingGroup.repositoryItem.type"/>
						<dsp:param name="obj2" value="storePickupShippingGroup"/>
						<dsp:oparam name="equal">
						<dsp:getvalueof var="commerceItemRelationshipCount" param="shippingGroup.commerceItemRelationshipCount"/>
						
							<c:if test="${commerceItemRelationshipCount gt 0}">								
								<div id="bopusShippingAddress">									
									<dsp:include page="display_bopus_order_details.jsp" flush="true">
										<dsp:param name="shippingGroup" value="${shippingGroup}"/>
										<dsp:param name="bopusOrderNum" param="bopusOrderNum"/>
									</dsp:include>
								</div>
							</c:if>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
</dsp:droplet>
</table>
</dsp:page>