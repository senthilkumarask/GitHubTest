
<dsp:page>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/dynamo/droplet/CurrencyFormatter"/>
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/GiftWrapGreetingsDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
	
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<dsp:getvalueof var="giftWrapPrice" param="giftWrapPrice" />
	<dsp:getvalueof var="shipGroupId" param="shipGroupId" />
	<dsp:getvalueof var="shipGroupParam" param="shipGroupParam" />
	<dsp:getvalueof var="count" param="count" />
	<dsp:getvalueof var="shipGroupGiftMessage" param="shipGroupGiftMessage" />
	<dsp:getvalueof var="shipGroupGiftInd" param="shipGroupGiftInd" />
	<dsp:getvalueof var="nonGiftWrapSkus" param="nonGiftWrapSkus" />
	<dsp:getvalueof var="giftWrapFlag" param="giftWrapFlag" />

	<c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
	<dsp:setvalue bean="BBBShippingGroupFormhandler.BBBShippingInfoBeanList[${count}].giftMessage" value="${shipGroupGiftMessage}" />
	<dsp:setvalue bean="BBBShippingGroupFormhandler.BBBShippingInfoBeanList[${count}].giftWrap" paramvalue="shipGroupParam.containsGiftWrap" />

	<dsp:getvalueof var="giftingFlag" value="${false}" scope="page" vartype="java.lang.Boolean"/>
	<c:if test="${shipGroupParam.containsGiftWrap}">
		<dsp:getvalueof var="giftingFlag" value="${shipGroupParam.containsGiftWrap}" scope="page" vartype="java.lang.Boolean"/>
	</c:if>
	<c:if test="${shipGroupGiftInd}">
		<dsp:getvalueof var="giftingFlag" value="${shipGroupGiftInd}" scope="page" vartype="java.lang.Boolean"/>
	</c:if>

	<dsp:setvalue bean="BBBShippingGroupFormhandler.BBBShippingInfoBeanList[${count}].giftingFlag" value="${giftingFlag}"/>
	<dsp:input bean="BBBShippingGroupFormhandler.BBBShippingInfoBeanList[${count}].shippingGroupId" type="hidden" value="${shipGroupId}"/>

	<label class="inline-rc checkbox multi-gift-message-trigger large-no-margin-top" for="shippingOption3_${count}">
		<dsp:input type="checkbox" value="true" name="shippingOption2_${count}" id="shippingOption3_${count}" bean="BBBShippingGroupFormhandler.BBBShippingInfoBeanList[${count}].giftingFlag" checked="true" />
		<span></span>
		<bbbl:label key="lbl_multi_gift_include_gifts" language="<c:out param='${language}'/>"/><br/>
		<jsp:useBean id="GiftPriceCounter" class="java.util.HashMap" scope="request"/>
		<dsp:droplet name="CurrencyFormatter">
			<dsp:param name="currency" param="giftWrapPrice"/>
			<dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
			<dsp:oparam name="output">
				<c:set target="${GiftPriceCounter}" property="giftPrice"><dsp:valueof param="formattedCurrency"/></c:set>
			</dsp:oparam>
		</dsp:droplet>
		<bbbl:label key="lbl_multi_gift_packing_slip_info" language="<c:out param='${language}'/>" placeHolderMap="${GiftPriceCounter}"/>
	</label>

	<div class="gift-message">
		<div class="small-12 columns">
			<%-- <label for="shippingGiftMessage_${count}">
				<bbbl:label key="lbl_multi_gift_add_message" language="<c:out param='${language}'/>"/>
				Add a free gift message (optional)
			</label> --%>
			<dsp:textarea name="shippingGiftMessage_${count}" bean="BBBShippingGroupFormhandler.BBBShippingInfoBeanList[${count}].giftMessage" id="shippingGiftMessage_${count}" rows="4" cols="28" iclass="shippingGiftMessage giftMessage" maxlength="200"/>
		</div>
		<div class="small-12 medium-6 columns">
			<dsp:droplet name="Switch">
			<dsp:param name="value" param="giftWrapFlag"/>
			<dsp:oparam name="true">
				<label class="inline-rc checkbox" for="includeFigtPackaging_${count}">
					<dsp:input type="checkbox" name="includeFigtPackaging_[${count}]" id="includeFigtPackaging_${count}" bean="BBBShippingGroupFormhandler.BBBShippingInfoBeanList[${count}].giftWrap"/>
					<span></span>
					<dsp:droplet name="CurrencyFormatter">
					<dsp:param name="currency" param="giftWrapPrice"/>
					<dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
					<dsp:oparam name="output">
						<bbbl:label key="lbl_multi_gift_include_packaging" language="<c:out param='${language}'/>"/> <bbbl:label key="lbl_multigift_include_wrap_start" language="<c:out param='${language}'/>"/><strong><dsp:valueof param="giftWrapPrice" converter="currency" number="0.00"/></strong><bbbl:label key="lbl_multigift_include_wrap_end" language="<c:out param='${language}'/>"/></label>
					</dsp:oparam>
					</dsp:droplet>
				</label>
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
				<div class="giftPackagingMessage"><bbbl:label key="lbl_gift_no_item_available" language="<c:out param='${language}'/>"/></div>
			</dsp:oparam>
			</dsp:droplet>
		</div>
			<div class="small-12 medium-6 columns">
			<c:if test="${giftWrapFlag}">
				<dsp:droplet name="GiftWrapGreetingsDroplet">
				<dsp:param name="siteId" value="${currentSiteId}" />
				<dsp:oparam name="output">
					<a href="#" class="multiAddCommonGreeting shippingAddCommonGreetMsg show-for-medium-up" id="shippingAddCommonGreetMsgs_${count}"> <bbbl:label key="lbl_multi_gift_symbol_plus" language="<c:out param='${language}'/>"/> <bbbl:label key="lbl_multi_gift_common_greeting" language="<c:out param='${language}'/>"/></a>
					<select id="shippingCommonGreetingMsgs_${count}" name="shippingCommonGreetingMsgs" class="hidden">
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
				</c:if>
			</div>
	</div>
	</dsp:page>
    

<script type="text/javascript">
$(document).ready(function(){
	$('.giftMessage').attr("placeholder", "Add a free gift message (optional)");
});
</script> 
