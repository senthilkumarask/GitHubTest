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

<fieldset class="radioGroup noMarTop">
	<legend class="hidden">Shipping Method</legend>
	<dsp:droplet name="GetApplicableLTLShippingMethodsDroplet">
		<dsp:param name="skuId" value="${sku}" />
		<dsp:param name="siteId" value="${siteId}" />
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
					<c:if test="${shipMethodId ne 'LWA'}">
						 <div class="radioItem input clearfix">		
							<div class="radio">
								<c:choose>
									<c:when test="${shipMethodId eq selectedShippingMethod or (shipMethodId eq 'LW' and selectedShippingMethod eq 'LW' and commItem.whiteGloveAssembly) or (shipMethodId eq 'LW' and selectedShippingMethod eq 'LWA')}">
                                           <dsp:input type="radio" bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingMethod"
                                                    value="${shipMethodId}${((shipMethodId eq 'LW' and commItem.whiteGloveAssembly) || selectedShippingMethod eq 'LWA')?'A':'' }" name="shippingMethods_ProdName_${index}" id="shippingMethod_${index}${countIndex}" checked="true" >
                                                   <dsp:tagAttribute name="role" value="radio"/>
                                                    </dsp:input>
                                    </c:when>
									<c:otherwise>
										<dsp:input type="radio" bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingMethod"
											   value="${shipMethodId}" name="shippingMethods_ProdName_${index}" id="shippingMethod_${index}${countIndex}" >
											   <dsp:tagAttribute name="role" value="radio"/>
											   </dsp:input>
									</c:otherwise>
								</c:choose>
							</div>
							<div class="label">
								<label for="shippingMethod_${index}${countIndex}">
									<span>
										${shipMethodDescription} - ${deliverySurcharge} + Shipping
									</span>
								</label>
							</div>
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
				<div class="checkboxItem input clearfix marLeft_25">
					<div class="checkbox">
						<input id="assembly${index}" name="assemblyFees" ${(selectedShippingMethod eq 'LWA') or ((selectedShippingMethod eq 'LW' or selectedShippingMethod eq 'LWA') and commItem.whiteGloveAssembly) ? "checked='checked'":""} ${(selectedShippingMethod ne 'LW' && selectedShippingMethod ne 'LWA') ? "disabled='disabled'":""} type="checkbox" value="${assemblyFees}"/>
					</div>
					<div class="label">
						<label for="assembly${index}">
							<span><bbbl:label key="lbl_Add_Assembly" language="${pageContext.request.locale.language}"/> - $<fmt:formatNumber pattern="0.00" value="${assemblyFees}"/></span>
						</label>
					</div>
				</div>
			</c:if>
		</dsp:oparam>
	</dsp:droplet>	
</fieldset>
</dsp:page>