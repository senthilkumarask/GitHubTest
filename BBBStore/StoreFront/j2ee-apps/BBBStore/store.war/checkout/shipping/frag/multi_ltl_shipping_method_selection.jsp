<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/GetApplicableLTLShippingMethodsDroplet" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
<c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
 
 
<dsp:getvalueof var="index" param="cisiIndex" />
<dsp:getvalueof var="sku" param="sku" />
<dsp:getvalueof var="selectedShippingMethod" param="shippingMethod" />
<dsp:getvalueof var="commItem" param="commItem" />
<dsp:getvalueof var="siteId" param="siteId" />
<dsp:getvalueof var="skuAssemblyOffered" param="skuAssemblyOffered" />
<c:set var="isWhiteGlovePresent" value="${false}" />
<dsp:getvalueof var="highestShipMethod" param="highestShipMethod"></dsp:getvalueof>
<dsp:getvalueof var="commItem" param="commItem" />
<dsp:getvalueof var="siteId" param="siteId" />
<dsp:getvalueof var="shippingGroupName" param="shippingGroupName" />
<dsp:getvalueof var="ajaxParam" param="ajaxParam"/>
<c:set var="registryAdd" value="false"/>
<c:choose>
 	<c:when test="${ajaxParam}">
 		<c:set var="formIdAppend" value="formUpdateOrderSummary_"/>
 	</c:when>
 	<c:otherwise>
 		<c:set var="formIdAppend" value=""/>
 	</c:otherwise>
 </c:choose>
<c:if test="${not empty shippingGroupName and fn:contains(shippingGroupName, 'registry') }">
	<c:set var="registryAdd" value="true"/>
</c:if>
<c:if test="${ registryAdd eq true and empty selectedShippingMethod }">
	<dsp:getvalueof var="selectedShippingMethod" value="${highestShipMethod}" />
</c:if> 
<fieldset class="radioGroup noMarTop deliveryMethodRadioGroup">
	<legend class="hidden">Shipping Method</legend>
	<dsp:droplet name="GetApplicableLTLShippingMethodsDroplet">
		<dsp:param name="skuId" value="${sku}" />
		<dsp:param name="siteId" value="${siteId}" />
		<%--	BBBH-2379 | Shipping page changes (MPC) --%>
		<dsp:getvalueof var="sddEligiblityStatus" param="sddEligiblityStatus"/>
		<dsp:getvalueof var="sddOptionEnabled" param="sddOptionEnabled"/>
		<c:if test="${sddEligiblityStatus eq 'marketEligible'}">
		<c:set var="displayMessage"><bbbl:label key="lbl_sdd_market_ineligible" language="${pageContext.request.locale.language}" /></c:set>
		<c:set var="displayTooltip"><bbbt:textArea key="txt_sdd_market_ineligible" language="${pageContext.request.locale.language}" /></c:set>
		</c:if>
		<dsp:oparam name="output">
		<dsp:getvalueof var="shipMethodVOList" param="shipMethodVOList"/>
			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:param value="${shipMethodVOList}" name="array" />
				<dsp:oparam name="output">
					<dsp:getvalueof var="countIndex" param="count" />
					<dsp:getvalueof var="shipMethodId" param="element.shipMethodId"/>
				    <dsp:getvalueof var="shipMethodDescription" param="element.shipMethodDescription"/>							
					<dsp:getvalueof var="deliverySurchargeval" param="element.deliverySurcharge"/>
					<c:set var="deliverySurcharge" value="" />
				    <c:if test="${deliverySurchargeval gt 0}">
					<c:set var="deliverySurcharge"><dsp:valueof  converter="currency" param="element.deliverySurcharge" /></c:set>
					</c:if>
					<c:if test="${deliverySurchargeval eq 0}">
					<c:set var="deliverySurcharge">Free</c:set>
					</c:if>			
					<dsp:getvalueof var="assemblyFees" param="element.assemblyFees"/>
					<%--	BBBH-2379 | Shipping page changes (MPC) --%>
					 <c:choose>
						 <c:when test="${shipMethodId eq 'SDD'}">
						 <c:set var="disabled">disabled</c:set>
						 <c:if test="${sddEligiblityStatus eq 'marketIneligible'}">
						 <c:set var="sddClass">hidden</c:set>
						 </c:if>
						  </c:when>
						  <c:otherwise><c:set var="disabled"></c:set>
						  </c:otherwise>
					  </c:choose>
					<c:if test="${shipMethodId ne 'LWA'}">
						
						<c:choose>
									<c:when test="${shipMethodId eq selectedShippingMethod or (shipMethodId eq 'LW' and selectedShippingMethod eq 'LW' and commItem.whiteGloveAssembly) or (shipMethodId eq 'LW' and selectedShippingMethod eq 'LWA')}">
                                         <div class="radioItem input clearfix ${sddClass}">		
											<div class="radio">
											<c:choose>
											<%--	BBBH-2379 | Shipping page changes (MPC) --%>
											 <c:when test="${shipMethodId eq 'SDD'}">
						  					<dsp:input type="radio" iclass="radioInputDeliveryMethod" bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingMethod"
                                               value="${shipMethodId}${((shipMethodId eq 'LW' and commItem.whiteGloveAssembly) || selectedShippingMethod eq 'LWA')?'A':'' }" name="shippingMethods_ProdName_${index}" id="${formIdAppend}shippingMethod_${index}${countIndex}" checked="false"   >
                                              <dsp:tagAttribute name="role" value="radio"/>
                                              <dsp:tagAttribute name="disabled" value="${disabled}"/>
                                               </dsp:input>
                                              </c:when>
                                              <c:otherwise>
                                              <dsp:input type="radio" iclass="radioInputDeliveryMethod" bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingMethod"
                                               value="${shipMethodId}${((shipMethodId eq 'LW' and commItem.whiteGloveAssembly) || selectedShippingMethod eq 'LWA')?'A':'' }" name="shippingMethods_ProdName_${index}" id="${formIdAppend}shippingMethod_${index}${countIndex}" checked="true"   >
                                              <dsp:tagAttribute name="role" value="radio"/>
                                               </dsp:input>
                                              </c:otherwise>
                                              </c:choose>
                                              <c:if test="${registryAdd eq 'true' }"> <c:set var="got" value="true"/></c:if>
                                                
                                                    
                                    </c:when>
									<c:otherwise>
									 <div class="radioItem input clearfix ${sddClass} <c:if test="${registryAdd eq 'true' }">hidden</c:if>">		
									<div class="radio">
									<c:choose>
									<%--	BBBH-2379 | Shipping page changes (MPC) --%>
									 <c:when test="${shipMethodId eq 'SDD'}">
							  		<dsp:input type="radio" iclass="radioInputDeliveryMethod" bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingMethod"
									   value="${shipMethodId}" name="shippingMethods_ProdName_${index}" id="${formIdAppend}shippingMethod_${index}${countIndex}" checked="false" >
									   <dsp:tagAttribute name="role" value="radio"/>
									  <dsp:tagAttribute name="disabled" value="${disabled}"/>
									   </dsp:input>
									    </c:when>
                                        <c:otherwise> 
                                        <dsp:input type="radio" iclass="radioInputDeliveryMethod" bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingMethod"
										   value="${shipMethodId}" name="shippingMethods_ProdName_${index}" id="${formIdAppend}shippingMethod_${index}${countIndex}" >
										   <dsp:tagAttribute name="role" value="radio"/>
										   </dsp:input>
                                         </c:otherwise>
                                         </c:choose>  
									</c:otherwise>
								</c:choose>
							</div>
							<c:if test="${not ajaxParam}">
                                <div class='label <c:if test="${registryAdd eq 'true' && got ne 'true' }">hidden</c:if>'>
							<div class='label <c:if test="${registryAdd eq 'true' && got ne 'true' }">hidden</c:if>'>
								<label for="shippingMethod_${index}${countIndex}" class="${disabled}">
									<span>
										${shipMethodDescription} - ${deliverySurcharge} + Shipping
									</span>
								</label>
							
								<c:set var="got" value="false"/>
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
							</div>
						</c:if>
						</div>
					</c:if>
					<c:if test="${shipMethodId eq 'LW'}">
						<c:set var="isWhiteGlovePresent" value="${true}" />
					</c:if>
				</dsp:oparam>
			</dsp:droplet>
			<dsp:param name="commItem" param="cisi.commerceItem"/>
			<input type="hidden" name="skuAssemblyOffered" id="skuAssemblyOffered${index}" value="${skuAssemblyOffered}"/>
		<c:if test="${isWhiteGlovePresent && skuAssemblyOffered }">
				<div class="checkboxItem input clearfix marLeft_25 <c:if test="${registryAdd eq 'true' && (selectedShippingMethod ne 'LW' and selectedShippingMethod ne 'LWA') }">hidden</c:if>">
					<div class="checkbox <c:if test="${registryAdd eq 'true' && (selectedShippingMethod eq 'LW' or selectedShippingMethod eq 'LWA' )}">
					checked disabledAssembly disabled</c:if> ">
					<c:if test="${registryAdd eq 'true' && (selectedShippingMethod eq 'LW' or selectedShippingMethod eq 'LWA' )}">
					<c:set var="assemblyDisable" value="disabled" /></c:if> 
						<input id="${formIdAppend}assembly${index}" name="assemblyFees" class="checkBoxAssemblyFees" ${(selectedShippingMethod eq 'LWA') or ((selectedShippingMethod eq 'LW' or selectedShippingMethod eq 'LWA') and commItem.whiteGloveAssembly) ? "checked='checked'":""} ${((selectedShippingMethod eq 'LW' || selectedShippingMethod eq 'LWA')) ? "disabled='disabled'":""} type="checkbox" value="${assemblyFees}"/>
					</div>
					<c:if test="${not ajaxParam}">
					<div class="label">
						<label for="assembly${index}">
							<span><bbbl:label key="lbl_Add_Assembly" language="${pageContext.request.locale.language}"/> - $<fmt:formatNumber pattern="0.00" value="${assemblyFees}"/></span>
						</label>
					</div>
					</c:if>
				</div>
			</c:if>
		</dsp:oparam>
	</dsp:droplet>	
</fieldset>
</dsp:page>