<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/dynamo/droplet/CurrencyFormatter"/>
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/GiftWrapCheckDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/GiftWrapGreetingsDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>

	<%-- Variables --%>
	<dsp:getvalueof var="sessionBabyCA" bean="SessionBean.babyCA"/>
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<dsp:getvalueof var="formExceptionFlag" param="formExceptionFlag" />
	<dsp:setvalue bean="BBBShippingGroupFormhandler.removeEmptyShippingGroup" />

	<dsp:input bean="BBBShippingGroupFormhandler.BBBShippingInfoBean.shippingGroupId" type="hidden" beanvalue="ShoppingCart.current.shippingGroups[0].id"/>
	<dsp:input bean="BBBShippingGroupFormhandler.siteId" value="${currentSiteId}" type="hidden"/>

	<h3 class="checkout-title">Gift Options</h3>

	<dsp:include page="packnhold.jsp">
		<dsp:param name="formExceptionFlag" value="${formExceptionFlag}" />
	</dsp:include>
	<dsp:droplet name="GiftWrapCheckDroplet">
				<dsp:param name="shippingGroup" bean="ShoppingCart.current.shippingGroups[0]" />
				<dsp:param name="siteId" value="${currentSiteId}" />
				<dsp:oparam name="output">
					<dsp:getvalueof var="nonGiftWrapSkus" param="nonGiftWrapSkus" />
	<dsp:getvalueof var="shippingGroup" bean="ShoppingCart.current.shippingGroups[0]"/>
	<c:if test="${shippingGroup.shippingGroupClassType eq 'hardgoodShippingGroup' }">
		<jsp:useBean id="GiftPriceCounter" class="java.util.HashMap" scope="request"/>
		<c:choose>
			<c:when test="${cmo or kirsch}">
				<label class="inline-rc checkbox gift-message-trigger disabled" for="orderHasGifts">
					<dsp:getvalueof id="checkboxSelection" bean="ShoppingCart.current.shippingGroups[0].specialInstructions.giftMessage"/>
					<dsp:getvalueof id="giftWrapIndicator" bean="ShoppingCart.current.shippingGroups[0].giftWrapInd"/>
					<dsp:getvalueof id="giftWrapItemIndicator" bean="ShoppingCart.current.shippingGroups[0].containsGiftWrap"/>
					<dsp:setvalue bean="BBBShippingGroupFormhandler.BBBShippingInfoBean.giftWrap" value="${giftWrapItemIndicator}" />
					<dsp:setvalue bean="BBBShippingGroupFormhandler.BBBShippingInfoBean.giftingFlag" value="${giftWrapIndicator}" />
					<dsp:input type="checkbox" value="true" name="orderHasGifts" id="" bean="BBBShippingGroupFormhandler.BBBShippingInfoBean.giftingFlag" checked="false" disabled="disabled" />
					<span></span>
					<strong><bbbl:label key="lbl_gift_order_include_gifts" language="<c:out param='${language}'/>"/></strong> 
					 <dsp:droplet name="CurrencyFormatter">
							<dsp:param name="currency" param="giftWrapPrice"/>
							<dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
							<dsp:oparam name="output">
								<c:set target="${GiftPriceCounter}" property="giftPrice"><dsp:valueof param="formattedCurrency"/></c:set>
							</dsp:oparam>
					</dsp:droplet>
					<bbbl:label key="lbl_gift_packing_slip_msg" language="<c:out param='${language}'/>"  placeHolderMap="${GiftPriceCounter}" />
				</label>
			</c:when>
			<c:otherwise>
				<label class="inline-rc checkbox gift-message-trigger" for="orderHasGifts">
					<dsp:getvalueof id="checkboxSelection" bean="ShoppingCart.current.shippingGroups[0].specialInstructions.giftMessage"/>
					<dsp:getvalueof id="giftWrapIndicator" bean="ShoppingCart.current.shippingGroups[0].giftWrapInd"/>
					<dsp:getvalueof id="giftWrapItemIndicator" bean="ShoppingCart.current.shippingGroups[0].containsGiftWrap"/>
					<dsp:setvalue bean="BBBShippingGroupFormhandler.BBBShippingInfoBean.giftWrap" value="${giftWrapItemIndicator}" />
					<dsp:setvalue bean="BBBShippingGroupFormhandler.BBBShippingInfoBean.giftingFlag" value="${giftWrapIndicator}" />
					<dsp:input type="checkbox" value="true" name="orderHasGifts" id="orderHasGifts" bean="BBBShippingGroupFormhandler.BBBShippingInfoBean.giftingFlag" checked="false" />
					<span></span>
					<strong><bbbl:label key="lbl_gift_order_include_gifts" language="<c:out param='${language}'/>"/></strong> 
					<dsp:droplet name="CurrencyFormatter">
							<dsp:param name="currency" param="giftWrapPrice"/>
							<dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
							<dsp:oparam name="output">
							   	<c:set target="${GiftPriceCounter}" property="giftPrice"><dsp:valueof param="formattedCurrency"/></c:set>
							</dsp:oparam>
					</dsp:droplet>
					<bbbl:label key="lbl_gift_packing_slip_msg" language="<c:out param='${language}'/>" placeHolderMap="${GiftPriceCounter}"/>
				</label>
			</c:otherwise>
			
		</c:choose>
	</c:if>
	<div class="small-12 columns gift-message hidden">
	<dsp:droplet name="GiftWrapGreetingsDroplet">
	<dsp:param name="siteId" value="${currentSiteId}" />
		<dsp:oparam name="output">
		<c:choose>
			<c:when test="${cmo or kirsch}">
				<a href="#" class="disabled" title="${commonGreeting}"> <bbbl:label key="lbl_multi_gift_symbol_plus" language="<c:out param='${language}'/>"/> <bbbl:label key="lbl_multi_gift_common_greeting" language="<c:out param='${language}'/>"/></a>
			</c:when>
			<c:otherwise>
				<a href="#" id="addCommonGreeting" title="${commonGreeting}"> <bbbl:label key="lbl_multi_gift_symbol_plus" language="<c:out param='${language}'/>"/> <bbbl:label key="lbl_multi_gift_common_greeting" language="<c:out param='${language}'/>"/></a>
			</c:otherwise>
		</c:choose>
		
			<select id="addCommonGreetingSelect" name="addCommonGreetingSelect" class="hidden">
				<option value='0'><bbbl:label key="lbl_gift_default_greeting_option" language="<c:out param='${language}'/>"/></option>
				<dsp:droplet name="ForEach">
				<dsp:param name="array" param="giftWrapMessages"/>
				<dsp:oparam name="output">
					<option value='<dsp:valueof param="count"/>'><dsp:valueof param="element"/></option>
				</dsp:oparam>
				</dsp:droplet>
			</select>
		</dsp:oparam>
	</dsp:droplet>
		<div class="small-12 columns <c:if test="${cmo or kirsch}">hidden</c:if>">
			<%-- <label for="shippingGiftMessage">
				<bbbl:label key="lbl_multi_gift_add_message" language="<c:out param='${language}'/>"/>
			</label> --%>
			<dsp:textarea name="shippingGiftMessage" bean="BBBShippingGroupFormhandler.BBBShippingInfoBean.giftMessage" id="shippingGiftMessage" rows="4" cols="28" maxlength="200"><%-- ${checkboxSelection} --%></dsp:textarea>
		</div>
		
		
		<div class="small-12 columns">
					<dsp:droplet name="Switch">
						<dsp:param name="value" param="giftWrapFlag"/>
						<dsp:oparam name="true">
							<c:choose>
								<c:when test="${cmo or kirsch}">
									<label class="inline-rc checkbox <c:if test="${cmo or kirsch}">disabled</c:if>" for="includeFigtPackaging">
										<dsp:input type="checkbox" name="includeFigtPackaging" id="" bean="BBBShippingGroupFormhandler.BBBShippingInfoBean.giftWrap"/>
										<span></span>
										<dsp:droplet name="CurrencyFormatter">
											<dsp:param name="currency" param="giftWrapPrice"/>
											<dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
											<dsp:oparam name="output">
												<bbbl:label key="lbl_gift_include_packaging" language="<c:out param='${language}'/>"/><strong><dsp:valueof param="giftWrapPrice" converter="currency" number="0.00"/></strong><bbbl:label key="lbl_gift_include_packaging_end" language="<c:out param='${language}'/>"/>
											</dsp:oparam>
										</dsp:droplet>
									</label>
								</c:when>
								<c:otherwise>
									<label class="inline-rc checkbox" for="includeFigtPackaging">
										<dsp:input type="checkbox" name="includeFigtPackaging" id="includeFigtPackaging" bean="BBBShippingGroupFormhandler.BBBShippingInfoBean.giftWrap"/>
										<span></span>
										<dsp:droplet name="CurrencyFormatter">
											<dsp:param name="currency" param="giftWrapPrice"/>
											<dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
											<dsp:oparam name="output">
												<bbbl:label key="lbl_gift_include_packaging" language="<c:out param='${language}'/>"/><strong><dsp:valueof param="giftWrapPrice" converter="currency" number="0.00"/></strong><bbbl:label key="lbl_gift_include_packaging_end" language="<c:out param='${language}'/>"/>
											</dsp:oparam>
										</dsp:droplet>
									</label>
								</c:otherwise>
							</c:choose>		
							
							<c:if test="${currentSiteId eq 'TBS_BedBathCanada'}">
								<bbbt:textArea key="txt_disclaimer_gift_message" language="<c:out param='${language}'/>"/>
							</c:if>
							<c:if test="${nonGiftWrapSkus ne ''}">
								<div class="giftPackagingMessage">
									<bbbl:label key="lbl_gift_msg_some_items" language="<c:out param='${language}'/>"/> <dsp:valueof param="nonGiftWrapSkus" valueishtml="true" /> <bbbl:label key="lbl_gift_msg_not_eligible" language="<c:out param='${language}'/>"/>
								</div>
							</c:if>
						</dsp:oparam>
						<dsp:oparam name="false">
							<!-- gift: no item available -->
							<p class="p-footnote"><bbbl:label key="lbl_gift_no_item_available" language="<c:out param='${language}'/>"/></p>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
			</dsp:droplet>
		</div>
	</div>

</dsp:page>
<script type="text/javascript">
$(document).ready(function(){
    $('#shippingGiftMessage').attr("placeholder", "Add a free gift message (optional)");
});
</script> 
