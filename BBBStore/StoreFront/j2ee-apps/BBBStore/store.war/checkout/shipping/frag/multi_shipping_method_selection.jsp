<%@ page import="atg.servlet.ServletUtil" %>
<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/GetApplicableShippingMethodsDroplet" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart" />
<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
<c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
 
<dsp:getvalueof var="ajaxParam" param="ajaxParam"/>
<c:choose>
	<c:when test="${ajaxParam}">
		<c:set var="formIdAppend" value="formUpdateOrderSummary_"/>
	</c:when>
	<c:otherwise>
		<c:set var="formIdAppend" value=""/>
	</c:otherwise>
</c:choose>
<dsp:getvalueof var="index" param="cisiIndex" />
<dsp:getvalueof var="sku" param="sku" />
<dsp:getvalueof var="shippingMethod" param="shippingMethod" />
<fieldset class="radioGroup noMarTop shippingMethodRadioGroup">
	<legend class="hidden">Shipping Method</legend>
	
	<dsp:droplet name="GetApplicableShippingMethodsDroplet">
		<dsp:param name="operation" value="perSku" />
		<dsp:param name="order" bean="ShoppingCart.current" />
		<dsp:getvalueof var="sddEligiblityStatus" param="sddEligiblityStatus"/>
		<dsp:getvalueof var="sddOptionEnabled" param="sddOptionEnabled"/>
		<%--	BBBH-2379 | Shipping page changes (MPC) --%>
		 <c:set var="displaySDDAlways">
								<%=ServletUtil.getCurrentRequest().getSession().getAttribute("displaySDDAlways")%>
						</c:set>
		<c:if test="${sddEligiblityStatus eq 'marketEligible' || displaySDDAlways eq 'true'}">
		<c:set var="displayMessage"><bbbl:label key="lbl_sdd_market_ineligible" language="${pageContext.request.locale.language}" /></c:set>
		<c:set var="displayTooltip"><bbbt:textArea key="txt_sdd_market_ineligible" language="${pageContext.request.locale.language}" /></c:set>
		</c:if>  
		<dsp:oparam name="output">
			<dsp:getvalueof var="mapShip" param="skuMethodsMap"></dsp:getvalueof>
			<dsp:droplet name="ForEach">
				<dsp:param name="array" value="${mapShip[sku]}" />
				<dsp:param name="sortProperties" value="sortShippingCharge,shipMethodId"/>
				<dsp:oparam name="output">
					 <dsp:getvalueof var="countIndex" param="count" />
					 <dsp:getvalueof var="shipMethodDescription" param="element.shipMethodDescription" />
					 <dsp:getvalueof var="shipMethodId" param="element.shipMethodId" />
					 <dsp:getvalueof var="cisiCount" param="cisiCount" />
					 <%--	BBBH-2379 | Shipping page changes (MPC) --%>
					 <c:choose>
					 <c:when test="${shipMethodId eq 'SDD'}">
						 <c:set var="disabled">disabled</c:set>
						 
						
						  <c:choose>
							 <c:when test="${sddEligiblityStatus eq 'marketIneligible' && displaySDDAlways ne 'true'}">
							 	<c:set var="sddClass">hidden</c:set>
							 </c:when>
							  <c:otherwise>
							 	 <c:set var="sddClass">sddClass</c:set>
							  </c:otherwise>
						  </c:choose>
					  </c:when>
					  <c:otherwise>
					  <c:set var="disabled"></c:set>
					  <c:set var="sddClass"></c:set>
					  </c:otherwise>
					  </c:choose>
					 <div class="radioItem input clearfix ${sddClass}">		
						<div class="radio">				
							<c:choose>
								<c:when test="${countIndex eq 1}">
								
								<c:choose>
								 <c:when test="${shipMethodId eq 'SDD'}">
									<dsp:input type="radio" iclass="radioInputShippingMethod" bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingMethod"
										   value="${shipMethodId}" name="shippingMethods_ProdName_${index}" id="${formIdAppend}shippingMethod_${index}${countIndex}" checked="false">
										   <dsp:tagAttribute name="role" value="radio"/>
                                              <dsp:tagAttribute name="disabled" value="${disabled}"/>
										   </dsp:input>
								</c:when>
								<c:otherwise>
								    <dsp:input type="radio" iclass="radioInputShippingMethod" bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingMethod"
										   value="${shipMethodId}" name="shippingMethods_ProdName_${index}" id="${formIdAppend}shippingMethod_${index}${countIndex}" checked="true" >
										   <dsp:tagAttribute name="role" value="radio"/>
									</dsp:input>
								</c:otherwise>
								</c:choose>
								</c:when>
								<c:otherwise>
								
								<c:choose>
								 <c:when test="${shipMethodId eq 'SDD'}">
									<dsp:input type="radio" iclass="radioInputShippingMethod" bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingMethod"
										   value="${shipMethodId}" name="shippingMethods_ProdName_${index}" id="${formIdAppend}shippingMethod_${index}${countIndex}" checked="false" >
										   <dsp:tagAttribute name="role" value="radio"/>
                                              <dsp:tagAttribute name="disabled" value="${disabled}"/>
										   </dsp:input>
									</c:when>
									<c:otherwise>
									<dsp:input type="radio" iclass="radioInputShippingMethod" bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingMethod"
										   value="${shipMethodId}" name="shippingMethods_ProdName_${index}" id="${formIdAppend}shippingMethod_${index}${countIndex}" >
										   <dsp:tagAttribute name="role" value="radio"/>
										   </dsp:input>
									</c:otherwise>
								</c:choose>
								</c:otherwise>
							</c:choose>
						</div>
						<c:if test="${not ajaxParam}">
						<div class="label">
							<label for="shippingMethod_${index}${countIndex}" class="${disabled}">
								<span>
									${shipMethodDescription}									
								</span>
							</label>
							<%--	BBBH-2379 | Shipping page changes (MPC) --%>
							<c:if test="${shipMethodId eq 'SDD'}">
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
						</c:if>
					</div>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>	
</fieldset>
</dsp:page>