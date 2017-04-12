<dsp:page>
    <dsp:importbean bean="/com/bbb/commerce/order/droplet/TBSOverrideReasonDroplet"/>
    <dsp:importbean bean="/com/bbb/commerce/order/droplet/TBSSurchargeDetailsDroplet"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/com/bbb/commerce/order/purchase/TBSPriceOverrideFormhandler"/>
    

	<dsp:form id="shipSurchargePriceOverrideForm" name="shipSurchargePriceOverrideForm" method="post">
		<dsp:input type="hidden" bean="TBSPriceOverrideFormhandler.shippingGroupID" iclass="shippingShippingGroupID" value=""/>
		<dsp:input type="hidden" bean="TBSPriceOverrideFormhandler.overridePrice" id="shipSurchargeOverridePrice" value="0" />
		<dsp:input type="hidden" bean="TBSPriceOverrideFormhandler.reasonCode" id="shipSurchargeReasonCode" value="" />
		<dsp:input bean="TBSPriceOverrideFormhandler.fromPage" type="hidden" value="surchargeOverride" />
		<%-- Client DOM XSRF | Part -2
		<dsp:input type="hidden" bean="TBSPriceOverrideFormhandler.successURL" value="/tbs/checkout/overrides/json/shippingPriceOverride_json.jsp" />
		<dsp:input type="hidden" bean="TBSPriceOverrideFormhandler.errorURL" value="/tbs/checkout/overrides/json/shippingPriceOverride_json.jsp" /> --%>
		<dsp:input type="submit" bean="TBSPriceOverrideFormhandler.shipSurchargePriceOverride" id="shipSurchargePriceOverrideSubmit" iclass="hidden" value="Override"/>
	</dsp:form>
	<dsp:getvalueof var="fromCart" param="fromCart"/>
	<dsp:droplet name="TBSSurchargeDetailsDroplet">
		<dsp:param name="fromCart" param="fromCart"/>
		<dsp:oparam name="output">
			<dsp:getvalueof param="surcharge" var="surchargeAmount"/>
		</dsp:oparam>
	</dsp:droplet>
	<div id="shipSurchargeOverrideModal_1" class="reveal-modal medium ship-surcharge-override" data-reveal="">
		<div class="row">
			<div class="small-12 columns no-padding">
				<h1>Surcharge Override</h1>
			</div>
		</div>
		<div class="row">
			<div class="small-12 columns">
				<ul class="errors"></ul>
			</div>
		</div>
		<div class="row">
			<div class="small-12 large-4 columns">
				<label class="right inline">Surcharge Price</label>
			</div>
			<div class="small-12 large-8 columns">
				<input type="text" id="originalShipSurchargePrice" value="${surchargeAmount}" disabled/>
			</div>
		</div>
		<div class="row">
			<div class="small-12 large-4 columns">
				<label for="newShipSurchargePrice" class="right inline">New Price</label>
			</div>
			<div class="small-12 large-8 columns">
				<input type="text" id="newShipSurchargePrice" value="$0.00" disabled/>
			</div>
		</div>
		<div class="row">
			<div class="small-12 large-4 columns">
				<label for="shipSurchargePriceReasonList" class="right inline">Reason</label>
			</div>
			<div class="small-12 large-8 columns">
				<select name="shipSurchargePriceReasonList" id="shipSurchargePriceReasonList" class="reasonList">
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
				<c:choose>
					<c:when test="${not empty fromCart}">
						<a href="#" class="button small service expand submit-surcharge-price-override">Override</a>
					</c:when>
					<c:otherwise>
						<a href="#" class="button small service expand submit-preview-surcharge-price-override">Override</a>
					</c:otherwise>
				</c:choose>
				
			</div>
			<div class="small-12 large-2 columns left">
                <a href="#" class="close-modal small button secondary expand">Cancel</a>
            </div>
		</div>
		<a href="#" class="close-reveal-modal">&times;</a>
	</div>
</dsp:page>