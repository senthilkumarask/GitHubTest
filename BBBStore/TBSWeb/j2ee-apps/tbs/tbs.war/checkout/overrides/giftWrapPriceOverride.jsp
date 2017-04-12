<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/TBSOverrideReasonDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

	<%-- giftWrap price override modal inputs --%>
	<dsp:form id="giftWrapPriceOverrideForm" name="giftWrapPriceOverrideForm" method="post">
		<dsp:input type="hidden" bean="CartModifierFormHandler.overrideId" id="giftShippingGroupID" value="" />
		<dsp:input type="hidden" bean="CartModifierFormHandler.overridePrice" id="overridePrice" value="0" />
		<dsp:input type="hidden" bean="CartModifierFormHandler.reasonCode" id="giftWrapReasonCode" value="" />
		<dsp:input bean="CartModifierFormHandler.fromPage" type="hidden"
												value="giftWrapPrice" />
		<%-- <dsp:input type="hidden" bean="CartModifierFormHandler.overrideSuccessURL" value="/tbs/checkout/overrides/json/giftWrapPriceOverride_json.jsp" />
		<dsp:input type="hidden" bean="CartModifierFormHandler.overrideErrorURL" value="/tbs/checkout/overrides/json/giftWrapPriceOverride_json.jsp" /> --%>
		<dsp:input type="submit" bean="CartModifierFormHandler.giftWrapOverride" id="giftWrapPriceOverrideSubmit" iclass="hidden" value="Override"/>
	</dsp:form>

	<%-- giftWrap price override modal --%>
	<div id="giftWrapPriceOverrideModal" class="reveal-modal medium giftwrap-price-override" data-reveal>
		<div class="row">
			<div class="small-12 columns no-padding">
				<h1>Gift Wrap Price Override</h1>
			</div>
		</div>
		<div class="row">
			<div class="small-12 columns">
				<ul class="errors"></ul>
			</div>
		</div>
		<div class="row">
			<div class="small-12 large-4 columns">
				<label class="right inline">Gift Wrap Price</label>
			</div>
			<div class="small-12 large-8 columns">
				<input type="text" id="originalGiftWrapPrice" disabled/>
			</div>
		</div>
		<div class="row">
			<div class="small-12 large-4 columns">
				<label for="newgiftWrapPrice" class="right inline">New Price</label>
			</div>
			<div class="small-12 large-8 columns">
				<input type="text" id="newgiftWrapPrice" value="$0.00" disabled/>
			</div>
		</div>
		<div class="row">
			<div class="small-12 large-4 columns">
				<label for="giftWrapPriceReasonList" class="right inline">Reason</label>
			</div>
			<div class="small-12 large-8 columns">
				<select name="giftWrapPriceReasonList" id="giftWrapPriceReasonList" class="reasonList">
					<dsp:droplet name="TBSOverrideReasonDroplet">
						<dsp:param name="OverrideType" value="giftWrap" />
						<dsp:oparam name="output">
							<option value="">Select Reason</option>
							<dsp:droplet name="ForEach">
								<dsp:param name="array" param="reasons" />
								<dsp:oparam name="output">
                                            <dsp:getvalueof var="elementCode" param="key"/>
											<dsp:getvalueof var="elementVal" param="element"/>
                                           <option value="${elementCode}">
                                                ${elementVal}
                                            </option>
                                        </dsp:oparam>
							</dsp:droplet>
						</dsp:oparam>
					</dsp:droplet>
				</select>
			</div>
		</div>
		<div class="row">
			<div class="small-12 large-offset-4 large-4 columns">
				<a class="button small service expand submit-giftwrap-price-override">Override</a>
			</div>
			<div class="small-12 large-2 columns left">
				<a class="close-modal small button secondary expand">Cancel</a>
			</div>
		</div>
		<a class="close-reveal-modal">&times;</a>
	</div>

</dsp:page>
