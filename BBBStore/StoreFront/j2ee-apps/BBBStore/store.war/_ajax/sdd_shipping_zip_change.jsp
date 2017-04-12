<%--
 *  Copyright 2016, BBB  All Rights Reserved.
 *  DESCRIPTION: Ajax call made on shipping page when zipcode is changed or Sdd shipping method is choosen
 *
 * 
--%>
<%@ page import="atg.servlet.ServletUtil" %>
<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/GetSddShippingMethodsDroplet" />
 <dsp:importbean bean="/atg/commerce/ShoppingCart" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<c:set var="displaySDDAlways">
	<%=ServletUtil.getCurrentRequest().getSession().getAttribute("displaySDDAlways")%>
</c:set>
<dsp:droplet name="GetSddShippingMethodsDroplet">
	<dsp:param name="order" bean="ShoppingCart.current" />
	<dsp:param name="chooseSddOption" param="chooseSddOption" />
	<dsp:param name="currentZip" param="sddShipZip" />
	<dsp:oparam name="output">
	<dsp:getvalueof var="sddEligiblityStatus" param="sddEligiblityStatus"/>
	<dsp:getvalueof var="sddOptionEnabled" param="sddOptionEnabled"/>
	<c:choose>
	<c:when test="${empty sddEligiblityStatus}">
	<c:set var="displayMessage"><span class="error"><bbbl:label key="lbl_sdd_market_ineligible" language="${pageContext.request.locale.language}" /></span></c:set>
	<c:set var="displayTooltip"><bbbt:textArea key="txt_sdd_market_ineligible" language="${pageContext.request.locale.language}" /></c:set>
	</c:when>
	<c:when test="${sddEligiblityStatus eq 'addressIneligible'}">
	<c:set var="displayMessage"><span class="error"><bbbl:label key="lbl_sdd_address_ineligible" language="${pageContext.request.locale.language}" /></span></c:set>
	<c:set var="displayTooltip"><bbbt:textArea key="txt_sdd_address_ineligible" language="${pageContext.request.locale.language}" /></c:set>
		<c:if test="${displaySDDAlways ne 'true' }">
			<c:set var="sddOptionEnabled" value="hidden"/>
		</c:if>
	</c:when>
	<c:when test="${sddEligiblityStatus eq 'marketIneligible'}">
	<c:set var="displayMessage"><span class="error"><bbbl:label key="lbl_sdd_market_ineligible" language="${pageContext.request.locale.language}" /></span></c:set>
	<c:set var="displayTooltip"><bbbt:textArea key="txt_sdd_market_ineligible" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="sddOptionEnabled" value="hidden"/>
	</c:when>
	<c:when test="${sddEligiblityStatus eq 'itemIneligible'}">
	<c:set var="displayMessage"><span class="error"><bbbl:label key="lbl_sdd_item_ineligible" language="${pageContext.request.locale.language}" /></span></c:set>
	<c:set var="displayTooltip"><bbbt:textArea key="txt_sdd_item_ineligible" language="${pageContext.request.locale.language}" /></c:set>
	</c:when>
	<c:when test="${sddEligiblityStatus eq 'itemEligible'}">
	<c:set var="displayMessage"><bbbl:label key="lbl_sdd_item_eligible" language="${pageContext.request.locale.language}" /></c:set>
	</c:when>
	<c:when test="${sddEligiblityStatus eq 'itemUnavailable'}">
	<c:set var="displayMessage"><span class="error"><bbbl:label key="lbl_sdd_item_unavailable" language="${pageContext.request.locale.language}" /></span></c:set>
	<c:set var="displayTooltip"><bbbt:textArea key="txt_sdd_item_unavailable" language="${pageContext.request.locale.language}" /></c:set>
	</c:when>
	<c:when test="${sddEligiblityStatus eq 'sddEligible'}">
	<c:set var="displayMessage"></c:set>
	</c:when>
	</c:choose>
	 
	<div class="sddChangeZip">
	<span id="sddOptionEnabled"> ${sddOptionEnabled} </span>
	<dsp:droplet name="ForEach">
			<dsp:param name="array" param="shipMethodVOList" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="shipMethodId" param="element.shipMethodId"/>
				<c:if test="${shipMethodId eq 'SDD'}">
					<span id="sddOption"> 
						<dsp:valueof param="element.shipMethodDescription" />
						<c:if test="${shipMethodId ne 'SDD' || (!empty sddEligiblityStatus && sddEligiblityStatus ne 'marketIneligible')}">
							<dsp:valueof param="element.shippingCharge" converter="currency" number="000.00"/>
						</c:if>
					</span>
					<span id="sddMethoodDesc">  <dsp:valueof param="element.shipMethodDescription" /> </span>
					<span id="sddPrice">
						<c:if test="${shipMethodId ne 'SDD' || (!empty sddEligiblityStatus && sddEligiblityStatus ne 'marketIneligible')}">
							<dsp:valueof param="element.shippingCharge" converter="currency" number="000.00"/>
						</c:if>
					</span>
					<span id="sddOptionDetail">
					<small> ${displayMessage}</small> 
					<c:if test="${not empty displayTooltip}">
					<a href="javascript:;" class="shippingPageSDDLink" >Why Not<span class="questionIcon">2</span></a>
					</c:if>
					</span>
					<span id="sddTooltip" class="hidden">
					${displayTooltip}
					</span>
				</c:if>
		</dsp:oparam>
		</dsp:droplet>
	</div>
	</dsp:oparam>

</dsp:droplet>


</dsp:page>