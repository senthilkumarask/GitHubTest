
<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<dsp:page>

	<dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
	<dsp:getvalueof var="order" param="order"/>
	<dsp:getvalueof var="onlineOrderNum" param="onlineOrderNum"/>
	
    
	<div id="addressDetails">
	
		<tbody>
			<tr>
				<td>${onlineOrderNum}</td>
				<td>
				<dsp:valueof param="shippingGroup.shippingAddress.firstName" valueishtml="true"/> <dsp:valueof param="shippingGroup.shippingAddress.lastName" valueishtml="true"/>
				<c:if test="${shippingGroup.shippingAddress.companyName != ''}">
					<dsp:valueof param="shippingGroup.shippingAddress.companyName" valueishtml="true"/>
				</c:if>															
				<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
					<dsp:param name="value" param="shippingGroup.shippingAddress.address2"/>
					<dsp:oparam name="true">
					
						<dsp:valueof param="shippingGroup.shippingAddress.address1" valueishtml="true"/>
					</dsp:oparam>
					<dsp:oparam name="false">
					
						<dsp:valueof param="shippingGroup.shippingAddress.address1" valueishtml="true"/>, <dsp:valueof param="shippingGroup.shippingAddress.address2" valueishtml="true"/>
					</dsp:oparam>
				</dsp:droplet>
				<dsp:valueof param="shippingGroup.shippingAddress.city" valueishtml="true"/>, <dsp:valueof param="shippingGroup.shippingAddress.state" /> <dsp:valueof param="shippingGroup.shippingAddress.postalCode" valueishtml="true"/>	
				</td>
			</tr>
		</tbody>
		</div>
</dsp:page>