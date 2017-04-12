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
<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBSPShippingGroupFormhandler"/>
 <dsp:importbean bean="/atg/commerce/ShoppingCart" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:getvalueof var="formExceptionFlag" param="formExceptionFlag" />
<dsp:getvalueof param="ignoreDefaultApplied" var="ignoreDefaultApplied" />
<dsp:getvalueof bean="BBBSPShippingGroupFormhandler.shippingOption" id="shippingMethodSelected" />


<c:set var="ignoreDefaultApplied" value="${false}" />
<dsp:getvalueof var="customerZip" param="customerZip" />
<!-- BBBH-2385 GetApplicableShippingMethodsDroplet gets the Eligibility status for SDD and
the shipping methods are displayed accordingly. -->
<dsp:droplet name="GetApplicableShippingMethodsDroplet">
	
	<dsp:param name="operation" value="perOrder" />
	<dsp:param name="order" bean="ShoppingCart.current" />
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
			<dsp:setvalue bean="BBBSPShippingGroupFormhandler.shippingOption" value="${shippingMethodSelected}" />
		</c:when>
		<c:otherwise>
			<dsp:setvalue bean="BBBSPShippingGroupFormhandler.shippingOption" paramvalue="preSelectedShipMethod" />
		</c:otherwise>
	</c:choose>
             

	<dsp:oparam name="output">
	<dsp:getvalueof var="sddEligiblityStatus" param="sddEligiblityStatus"/>
	<dsp:getvalueof var="sddOptionEnabled" param="sddOptionEnabled"/>
	<c:if test="${sddEligiblityStatus eq 'marketEligible'}">
		<c:set var="displayMessage"><bbbl:label key="lbl_sdd_market_ineligible" language="${pageContext.request.locale.language}" /></c:set>
		<c:set var="displayTooltip"><bbbt:textArea key="txt_sdd_market_ineligible" language="${pageContext.request.locale.language}" /></c:set>
		</c:if>
		<dsp:droplet name="ForEach">
			<dsp:param name="array" param="shipMethodVOList" />
			<dsp:param name="sortProperties" value="sortShippingCharge,shipMethodId"/>
			<dsp:oparam name="outputStart">


				<dsp:getvalueof bean="BBBSPShippingGroupFormhandler.shippingOption" var="shippingOptionVal"  />							
            <%--
            This input is on the singleShipping JSP because this file will be ajaxed in, 
            and it does not have a dsp:form tag, so cannot have dsp:input tags on it
            <dsp:input type="hidden" bean="BBBSPShippingGroupFormhandler.shippingOption" name="shippingMethodHidden" id="shippingMethod" value="${shippingOptionVal}" />
				--%>
			
			<!--<div>session value here</div>-->
		   	<div id="shippingMethods"> 
			
			<input type="hidden" name="shippingPreSelectedVal" id="shippingPreSelectedVal" value="${shippingOptionVal}" />
		  	</dsp:oparam>
			<dsp:oparam name="output">
				<dsp:getvalueof param="element.shipMethodDescription" id="elementVal" />
				<dsp:getvalueof param="element.shipMethodId" id="elementId" />				
				<dsp:getvalueof param="element.estdShipDurationInDaysLowerLimit" var="estdShipDurationInDaysLowerLimit" />
				<dsp:getvalueof param="element.estdShipDurationInDaysHigherLimit" var="estdShipDurationInDaysHigherLimit" />
				<c:choose>
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
								<c:when test="${sddEligiblityStatus eq 'itemEligible'}">
								<c:set var="displayMessage"><bbbl:label key="lbl_sdd_item_eligible" language="${pageContext.request.locale.language}" /></c:set>
							</c:when>
							<c:when test="${sddEligiblityStatus eq 'itemUnavailable'}">
								<c:set var="displayMessage"><bbbl:label key="lbl_sdd_item_unavailable" language="${pageContext.request.locale.language}" /></c:set>
								<c:set var="displayTooltip"><bbbt:textArea key="txt_sdd_item_unavailable" language="${pageContext.request.locale.language}" /></c:set>
							</c:when>
						</c:choose>
					</c:when>
					<c:otherwise>
						<c:set var="sddClass"></c:set>
						<c:set var="hideSdd"></c:set>
						<c:set var="disableSdd">false</c:set>
					</c:otherwise>
				</c:choose>
				

<div class="shipMethodWrapper clearfix ${hideSdd}">
				<div class="shipMethod <c:if test="${elementId eq 'SDD'}"> noMarRight </c:if>  <c:if test="${disableSdd eq 'disabled'}">sddShipDisable</c:if> <c:if test="${shippingOptionVal eq elementId && !ignoreDefaultApplied}">active</c:if>">					
					


<div class="label">

						<c:choose>
						<c:when test="${elementId eq 'SDD'}">
                           <label id="lblshippingMethod${elementId}" for="shippingMethod${elementId}">
                         </c:when>
						<c:otherwise>
						<label id="lblshippingMethod${elementVal}" for="shippingMethod${elementVal}">
						</c:otherwise>
						</c:choose>
							<span class="shippingDesc"><c:out value="${elementVal}" /></span>

							<c:if test="${not empty estdShipDurationInDaysLowerLimit && not empty estdShipDurationInDaysHigherLimit }" >
								<span class="shippingDaysToShip">
									${estdShipDurationInDaysLowerLimit} - ${estdShipDurationInDaysHigherLimit}
								</span>							
							</c:if>
							<span class="shippingCharge">
								<c:if test="${elementId ne 'SDD' || (!empty sddEligiblityStatus && sddEligiblityStatus ne 'marketIneligible')}">
									<dsp:valueof param="element.shippingCharge" converter="currency" number="000.00"/>
								</c:if>
							</span>
						</label>
					</div>
					<c:choose>
						<c:when test="${shippingOptionVal eq elementId && !ignoreDefaultApplied}">	

						 <c:choose>
								<c:when test="${elementId eq 'SDD'}">
									  <button 
                                                        class="btnPrimary button-Med shipMethodBtn applied shippingMethod${elementId}" 
                                                        id="shippingMethod${elementId}"                        
                                                        data-methodvalue="${elementId}" >
                                                        <span class="icon-checkmark icon-font" aria-hidden="true"></span>
                                                        <bbbl:label key="lbl_spc_shipping_methods_selected" language="${pageContext.request.locale.language}" />
                                                    </button>
                                                    <c:set var="hideSddInvMsg">hidden</c:set>
                                        
								</c:when><c:otherwise>
									<button 
                                                  class="btnPrimary button-Med shipMethodBtn applied" 
                                                  id="shippingMethod${elementVal}"                        
                                                  data-methodvalue="${elementId}" >
                                                  <span class="icon-checkmark icon-font" aria-hidden="true"></span>
                                                  <bbbl:label key="lbl_spc_shipping_methods_selected" language="${pageContext.request.locale.language}" />
                                              </button>
								</c:otherwise>
							</c:choose>
							
						</c:when>
						<c:otherwise>
						
							
							<c:choose>
							<c:when test="${elementId eq 'SDD'}">
							<input 
								type="button" 						
								id="shippingMethod${elementId}" <c:if test="${disableSdd eq 'disabled'}">disabled='disabled'</c:if>					
								data-methodvalue="${elementId}"
								value="<bbbl:label key='lbl_spc_apply_shipping' language='${pageContext.request.locale.language}' />"
								class="btnPrimary button-Med shipMethodBtn shippingMethod${elementId}"		
							/><c:set var="hideSddInvMsg"></c:set></c:when><c:otherwise>
								<input 
								type="button" 						
								id="shippingMethod${elementVal}" 						
								data-methodvalue="${elementId}"
								value="<bbbl:label key='lbl_spc_apply_shipping' language='${pageContext.request.locale.language}' />"
								class="btnPrimary button-Med shipMethodBtn"		
							/></c:otherwise>
							</c:choose>
							
						
						</c:otherwise>
					</c:choose>
</div>
				<!-- BBBH-2385 if SDD the display messge for various eligibilty status is displayed below -->
						<%-- <c:choose>
	 						<c:when test="${elementId eq 'SDD' && disableSdd ne 'disabled'}">
								 <div class="sddMessage ${hideSddInvMsg}">
									<small> ${displayMessage}</small> 
								 </div>
							</c:when>
							<c:otherwise>
							 <div class="sddMessage hidden">
									<small> ${displayMessage}</small> 
								 </div>
							</c:otherwise>
						</c:choose> --%>
</div>
				
			</dsp:oparam>
			<dsp:oparam name="outputEnd">
			
			<c:choose>
                 <c:when test="${disableSdd eq 'disabled'}">
  						<span class='sddNotAvailable clearfix'><span>${displayMessage} </span>
									<c:if test="${not empty displayTooltip}">
									<a href="javascript:;" class="shippingPageSDDLink" >Why Not<span class="questionIcon">2</span></a>
							</c:if>
				 	</span>
  				</c:when>
  				<c:when test="${elementId eq 'SDD' && disableSdd ne 'disabled'}">
								<span class='sddNotAvailable clearfix ${hideSddInvMsg}'><span>${displayMessage} </span>
									</span>
				</c:when>
  				<c:otherwise>
  					<span class='sddNotAvailable clearfix hidden'><span>${displayMessage} </span>
							<c:if test="${not empty displayTooltip}">
							<a href="javascript:;" class="shippingPageSDDLink" >Why Not<span class="questionIcon">2</span></a>
						</c:if>
					</span>
  				</c:otherwise>
         	</c:choose>
			<span id="sddTooltip" class="hidden">
				${displayTooltip}
			</span>
		   	</div> 
		   	<div class="clear"></div>
		  </dsp:oparam>

				
		</dsp:droplet>
	</dsp:oparam>

</dsp:droplet>

<c:if test="${frompaypal}">
	<dsp:importbean bean="/atg/commerce/order/purchase/RepriceOrderDroplet"/>
	<dsp:droplet name="RepriceOrderDroplet">
		<dsp:param value="OP_REPRICE_TAX" name="pricingOp"/>
	</dsp:droplet>
	<dsp:setvalue bean="BBBSPShippingGroupFormhandler.fromPaypalEdit" value="false" />
	<c:set var="frompaypal" value="false" scope="session"/>
</c:if>
</dsp:page>