<%--
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  shippingMethods.jsp
 *
 *  DESCRIPTION: Shipping address page shipping options
 *
 *  HISTORY:
 *  Dec 1, 2011  Initial version
 * 
 *  Dec 29, 2011 Changes made for Single shipping page
 * 
--%>
<%@ page import="atg.servlet.ServletUtil" %>
<dsp:page>

<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/GetApplicableShippingMethodsDroplet" />
<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
 <dsp:importbean bean="/atg/commerce/ShoppingCart" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:getvalueof var="formExceptionFlag" param="formExceptionFlag" />
<dsp:getvalueof bean="BBBShippingGroupFormhandler.shippingOption" id="shippingMethodSelected" />
<dsp:getvalueof var="customerZip" param="customerZip" />
<dsp:droplet name="GetApplicableShippingMethodsDroplet">
	<dsp:param name="operation" value="perOrder" />
	<dsp:param name="order" bean="ShoppingCart.current" />
	<dsp:param name="checkForInventory" value="true" />
	 <c:choose>
	<c:when test="${empty zipOnLoad}">
		<dsp:param name="currentZip" value="${customerZip}" />
	</c:when>
	<c:otherwise>
		<dsp:param name="currentZip" value="${zipOnLoad}" />
		</c:otherwise>
	</c:choose>
	 <c:choose>
		<c:when test="${formExceptionFlag eq 'true'}">
			<dsp:setvalue bean="BBBShippingGroupFormhandler.shippingOption" value="${shippingMethodSelected}" />
		</c:when>
		<c:otherwise>
			<dsp:setvalue bean="BBBShippingGroupFormhandler.shippingOption" paramvalue="preSelectedShipMethod" />
		</c:otherwise>
	</c:choose>

	<dsp:oparam name="output">
	<dsp:getvalueof var="sddEligiblityStatus" param="sddEligiblityStatus"/>
	<dsp:getvalueof var="sddOptionEnabled" param="sddOptionEnabled"/>
		<dsp:droplet name="ForEach">
			<dsp:param name="array" param="shipMethodVOList" />
			<dsp:param name="sortProperties" value="sortShippingCharge,shipMethodId"/>
			<dsp:oparam name="output">
				<dsp:getvalueof param="element.shipMethodDescription" id="elementVal">
				<dsp:getvalueof param="element.shipMethodId" id="elementId">
				<c:choose>
				<%--	BBBH-2379 | Shipping page changes (MPC) --%>
					<c:when test="${elementId eq 'SDD'}">
					<c:set var="sddClass">sddClass</c:set>
					
					<%--Based on displaySDDAlways the SDD is disabled instead of hidden  --%>
						<c:set var="displaySDDAlways">
								<%=ServletUtil.getCurrentRequest().getSession().getAttribute("displaySDDAlways")%>
						</c:set>
						<c:if test="${empty sddEligiblityStatus || sddEligiblityStatus eq 'marketIneligible' || sddEligiblityStatus eq 'addressIneligible'}">
							<c:choose>
								<c:when test="${displaySDDAlways eq 'true' }">
									<c:set var="disableSdd">disabled</c:set>
									<c:set var="hideSdd"></c:set>
								</c:when>
								<c:otherwise>
									<c:set var="hideSdd">hidden</c:set>
								</c:otherwise>
							</c:choose>
							
						</c:if>
					<c:choose>
						<c:when test="${sddEligiblityStatus eq 'addressIneligible'}">
							<c:set var="displayMessage"><bbbl:label key="lbl_sdd_address_ineligible" language="${pageContext.request.locale.language}" /></c:set>
							<c:set var="displayTooltip"><bbbt:textArea key="txt_sdd_address_ineligible" language="${pageContext.request.locale.language}" /></c:set>
						</c:when>
						<c:when test="${sddEligiblityStatus eq 'marketIneligible'}">
							<c:set var="displayMessage"><bbbl:label key="lbl_sdd_market_ineligible" language="${pageContext.request.locale.language}" /></c:set>
							<c:set var="displayTooltip"><bbbt:textArea key="txt_sdd_market_ineligible" language="${pageContext.request.locale.language}" /></c:set>
						</c:when>
							<c:when test="${sddEligiblityStatus eq 'itemIneligible'}">
							<c:set var="displayMessage"><bbbl:label key="lbl_sdd_item_ineligible" language="${pageContext.request.locale.language}" /></c:set>
							<c:set var="displayTooltip"><bbbt:textArea key="txt_sdd_item_ineligible" language="${pageContext.request.locale.language}" /></c:set>
							<c:set var="disableSdd">disabled</c:set>
						</c:when>
						<c:when test="${sddEligiblityStatus eq 'itemUnavailable'}">
							<c:set var="displayMessage"><bbbl:label key="lbl_sdd_item_unavailable" language="${pageContext.request.locale.language}" /></c:set>
							<c:set var="displayTooltip"><bbbt:textArea key="txt_sdd_item_unavailable" language="${pageContext.request.locale.language}" /></c:set>
							<c:set var="disableSdd">disabled</c:set>
						</c:when>
							<c:when test="${sddEligiblityStatus eq 'itemEligible'}">
							<c:set var="displayMessage"><bbbl:label key="lbl_sdd_item_eligible" language="${pageContext.request.locale.language}" /></c:set>
						</c:when>
					</c:choose>
					</c:when>
				<c:otherwise>
					<c:set var="sddClass"></c:set>
					<c:set var="hideSdd"></c:set>
					<c:set var="disableSdd"></c:set>
				</c:otherwise>
				</c:choose>
				<div class="radioItem input clearfix padTop_10 ${sddClass} ${hideSdd}">
					<div class="radio">
					<c:choose>
					<c:when test="${elementId eq 'SDD' && not empty disableSdd}">
						<dsp:input type="radio" value="${elementId}" bean="BBBShippingGroupFormhandler.shippingOption" name="shippingMethod" id="shippingMethod${elementId}" >
                            <dsp:tagAttribute name="aria-checked" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblshippingMethod${elementId} errorshippingMethod"/>
							<dsp:tagAttribute name="disabled" value="${disableSdd}"/>
                        </dsp:input>
					</c:when>
					<c:otherwise>
						<dsp:input type="radio" value="${elementId}" bean="BBBShippingGroupFormhandler.shippingOption" name="shippingMethod" id="shippingMethod${elementId}" >
                            <dsp:tagAttribute name="aria-checked" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblshippingMethod${elementId} errorshippingMethod"/>
                        </dsp:input>
					</c:otherwise>
					</c:choose>
					</div>
					<dsp:getvalueof var="selectedShippingMethod" bean="BBBShippingGroupFormhandler.shippingOption" />
					<div class="label">
						<label id="lblshippingMethod${elementId}" class="${disableSdd}" for="shippingMethod${elementId}">
							<span><c:out value="${elementVal}" />
								<c:if test="${elementId ne 'SDD' || (!empty sddEligiblityStatus && sddEligiblityStatus ne 'marketIneligible')}">
									<dsp:valueof param="element.shippingCharge" converter="currency" number="000.00"/>
								</c:if>
							</span>
							<c:if test="${elementId eq 'SDD' && selectedShippingMethod eq 'SDD'}">
								<img class="sddAvailableTick" src="/_assets/global/images/sdd/sddshippingavailable.png">
							</c:if>
						</label>
						<c:if test="${elementId eq 'SDD'}">
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
					</div>
				</div>

				</dsp:getvalueof>
				</dsp:getvalueof>
			</dsp:oparam>
		</dsp:droplet>
	</dsp:oparam>

</dsp:droplet>

</dsp:page>