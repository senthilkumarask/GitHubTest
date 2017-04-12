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
<dsp:page>

<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/GetApplicableShippingMethodsDroplet" />
<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBSPShippingGroupFormhandler"/>
 <dsp:importbean bean="/atg/commerce/ShoppingCart" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:getvalueof var="formExceptionFlag" param="formExceptionFlag" />
<dsp:getvalueof bean="BBBSPShippingGroupFormhandler.shippingOption" id="shippingMethodSelected" />

<dsp:droplet name="GetApplicableShippingMethodsDroplet">

	<dsp:param name="operation" value="perOrder" />
	<dsp:param name="order" bean="ShoppingCart.current" />
	 <c:choose>
		<c:when test="${formExceptionFlag eq 'true'}">
			<dsp:setvalue bean="BBBSPShippingGroupFormhandler.shippingOption" value="${shippingMethodSelected}" />
		</c:when>
		<c:otherwise>
			<dsp:setvalue bean="BBBSPShippingGroupFormhandler.shippingOption" paramvalue="preSelectedShipMethod" />
		</c:otherwise>
	</c:choose>



	<dsp:oparam name="output">

		<div id="shippingMethods"> 
		<%-- Need this because DSP will not allow input tags without being wrapped in a form--%>
		<dsp:form id="shippingMethodsDummy"  action="#" method="post">

		<dsp:droplet name="ForEach">
			<dsp:param name="array" param="shipMethodVOList" />
			<dsp:param name="sortProperties" value="shippingCharge,shipMethodId"/>
			<dsp:oparam name="outputStart">
		   	
		  	</dsp:oparam>
			<dsp:oparam name="output">
				<dsp:getvalueof param="element.shipMethodDescription" id="elementVal">
				<dsp:getvalueof param="element.shipMethodId" id="elementId">

				<div class="radioItem input clearfix padTop_10">
					<div class="radio">
						<dsp:input type="radio" value="${elementId}" bean="BBBSPShippingGroupFormhandler.shippingOption" name="shippingMethod" id="shippingMethod${elementId}" >
                            <dsp:tagAttribute name="aria-checked" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblshippingMethod${elementId} errorshippingMethod"/>
                        </dsp:input>
					</div>
					<div class="label">
					<c:choose>
						<c:when test="${elementId eq 'SDD'}">
                         <label id="lblshippingMethod${elementId}" for="shippingMethod${elementId}">
                             </c:when>
						<c:otherwise>
						<label id="lblshippingMethod${elementVal}" for="shippingMethod${elementVal}">
						</c:otherwise>
						</c:choose>
						
							<span><c:out value="${elementVal}" /> <dsp:valueof param="element.shippingCharge" converter="currency" number="000.00"/></span>
						</label>
					</div>
				</div>

				</dsp:getvalueof>
				</dsp:getvalueof>
			</dsp:oparam>
		</dsp:droplet>

		</dsp:form>
		</div> 

	</dsp:oparam>

</dsp:droplet>


</dsp:page>