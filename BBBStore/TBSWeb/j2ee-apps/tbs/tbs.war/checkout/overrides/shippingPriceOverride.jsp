<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/TBSPriceOverrideFormhandler"/>
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/TBSOverrideReasonDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

	<%-- shipping price override modal inputs --%>
	<dsp:form id="shippingPriceOverrideForm" name="shippingPriceOverrideForm" method="post">
		<dsp:input type="hidden" bean="TBSPriceOverrideFormhandler.shippingGroupID" id="shippingShippingGroupID" value="" />
		<dsp:input type="hidden" bean="TBSPriceOverrideFormhandler.overridePrice" id="shippingOverridePrice" value="0" />
		<dsp:input type="hidden" bean="TBSPriceOverrideFormhandler.reasonCode" id="shippingReasonCode" value="" />
		<dsp:input bean="TBSPriceOverrideFormhandler.fromPage" type="hidden" value="shippingPriceOverride" />
		<%-- Client DOM XSRF | Part -2
		<dsp:input type="hidden" bean="TBSPriceOverrideFormhandler.successURL" value="/tbs/checkout/overrides/json/shippingPriceOverride_json.jsp" />
		<dsp:input type="hidden" bean="TBSPriceOverrideFormhandler.errorURL" value="/tbs/checkout/overrides/json/shippingPriceOverride_json.jsp" /> --%>
		<dsp:input type="submit" bean="TBSPriceOverrideFormhandler.shippingPriceOverride" id="shippingPriceOverrideSubmit" iclass="hidden" value="Override"/>
	</dsp:form>

	<%-- shipping price override modal --%>
	<div id="shippingPriceOverrideModal" class="reveal-modal medium shipping-price-override" data-reveal>
		<div class="row">
			<div class="small-12 columns no-padding">
				<h1>Shipping Price Override</h1>
			</div>
		</div>
		<div class="row">
			<div class="small-12 columns">
				<ul class="errors"></ul>
			</div>
		</div>
		<div class="row">
			<div class="small-12 large-4 columns">
				<label class="right inline">Shipping Price</label>
			</div>
			<div class="small-12 large-8 columns">
				<input type="text" id="originalShippingPrice" disabled/>
			</div>
		</div>
		<div class="row">
			<div class="small-12 large-4 columns">
				<label for="newShippingPrice" class="right inline">New Price</label>
			</div>
			<div class="small-12 large-8 columns">
				<input type="text" id="newShippingPrice" value="$0.00" disabled/>
			</div>
		</div>
		<div class="row">
			<div class="small-12 large-4 columns">
				<label for="shippingPriceReasonList" class="right inline">Reason</label>
			</div>
			<div class="small-12 large-8 columns">
				<select name="shippingPriceReasonList" id="shippingPriceReasonList" class="reasonList">
					<dsp:droplet name="TBSOverrideReasonDroplet">
						<dsp:param name="OverrideType" value="shipping" />
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
				<a class="button small service expand submit-shipping-price-override">Override</a>
			</div>
			<div class="small-12 large-2 columns left">
                <a class="close-modal small button secondary expand">Cancel</a>
            </div>
		</div>
		<a class="close-reveal-modal">&times;</a>
	</div>

</dsp:page>
