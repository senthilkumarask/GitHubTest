<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/TBSPriceOverrideFormhandler"/>
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/TBSOverrideReasonDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/multisite/Site" />

	<dsp:getvalueof var="currentSiteId" bean="Site.id"></dsp:getvalueof>
	<c:set var="bedBathCanadaSiteCode"><bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" /></c:set>
	<div class="hidden" id="currentSiteId">${currentSiteId}</div>
	
	
	<div class="hidden" id="bedBathCanadaSiteCode">${bedBathCanadaSiteCode}</div>
	
	<%-- tax price override modal inputs --%>
	<dsp:form id="taxPriceOverrideForm" name="taxPriceOverrideForm" method="post">
		<dsp:input type="hidden" bean="TBSPriceOverrideFormhandler.shippingGroupID" id="taxShippingGroupID" value="" />
		<dsp:input type="hidden" bean="TBSPriceOverrideFormhandler.overridePrice" id="taxOverridePrice" value="0" />
		<dsp:input type="hidden" bean="TBSPriceOverrideFormhandler.taxExeptId" id="taxOverrideExeptId" value="" />
		<dsp:input type="hidden" bean="TBSPriceOverrideFormhandler.reasonCode" id="taxReasonCode" value="" />
		<dsp:input bean="TBSPriceOverrideFormhandler.fromPage" type="hidden" value="taxPriceOverride" />
		<%-- Client DOM XSRF | Part -2
		<dsp:input type="hidden" bean="TBSPriceOverrideFormhandler.successURL" value="/tbs/checkout/overrides/json/taxPriceOverride_json.jsp" />
		<dsp:input type="hidden" bean="TBSPriceOverrideFormhandler.errorURL" value="/tbs/checkout/overrides/json/taxPriceOverride_json.jsp" /> --%>
		<dsp:input type="submit" bean="TBSPriceOverrideFormhandler.taxPriceOverride" id="taxPriceOverrideSubmit" iclass="hidden" value="Override"/>
	</dsp:form>

	<%-- tax price override modal --%>
	<div id="taxPriceOverrideModal" class="reveal-modal medium tax-price-override" data-reveal>
		<div class="row">
			<div class="small-12 columns no-padding">
				<c:choose>
					<c:when test="${currentSiteId ne bedBathCanadaSiteCode}">
						<h1>Tax Price Override</h1>
					</c:when>
					<c:otherwise>
						<h1>Tax Override</h1>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="row">
			<div class="small-12 columns">
				<ul class="errors"></ul>
			</div>
		</div>
		<div class="row">
			<div class="small-12 large-4 columns">
			<c:choose>
					<c:when test="${currentSiteId ne bedBathCanadaSiteCode}">
						<label class="right inline">Tax Price</label>
					</c:when>
					<c:otherwise>
						<label class="right inline">GST/HST/PST</label>
					</c:otherwise>
				</c:choose>
			</div>
			<div class="small-12 large-8 columns">
				<input type="text" id="originalTaxPrice" disabled/>
			</div>
		</div>
		<div class="row">
			<div class="small-12 large-4 columns">
				<label for="newtaxPrice" class="right inline">New Price</label>
			</div>
			<div class="small-12 large-8 columns">
				<input type="text" id="newtaxPrice" value="$0.00" disabled/>
			</div>
		</div>
		<div class="row">
			<div class="small-12 large-4 columns">
				<label for="taxPriceReasonList" class="right inline">Reason*</label>
			</div>
			<div class="small-12 large-8 columns">
				<select name="taxPriceReasonList" id="taxPriceReasonList" class="reasonList">
					<dsp:droplet name="TBSOverrideReasonDroplet">
						<dsp:param name="OverrideType" value="tax" />
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
		
        <c:choose>
		<c:when test="${currentSiteId ne bedBathCanadaSiteCode}">
		<div id="taxExemptId"  class="row taxExeptTxtFieldDiv hidden">
            <div class="small-12 large-4 columns">
                <label for="taxExeptId" class="right inline">Tax Exempt ID *</label>
            </div>
            <div class="small-12 large-8 columns">
                <input type="text" id="taxExeptId" value="" maxlength="40" name="taxExeptId"/>
            </div>
        </div>
		</c:when>
		<c:otherwise>
		<div id="taxExemptId"  class="row">
            <div class="small-12 large-4 columns">
                <label for="taxExeptId" class="right inline">Tax Exempt ID *</label>
            </div>
            <div class="small-12 large-8 columns">
                <input type="text" id="taxExeptId" value="" maxlength="40" name="taxExeptId" onkeypress="return validateKeypress();"/>
            </div>
        </div>
		</c:otherwise>
		</c:choose>
		
		<div class="row">
			<div class="small-12 large-offset-4 large-4 columns">
				<a class="button small service expand submit-tax-price-override">Override</a>
			</div>
			<div class="small-12 large-2 columns left">
				<a class="close-modal small button secondary expand">Cancel</a>
			</div>
		</div>
		<a class="close-reveal-modal">&times;</a>
	</div>

	<script type="text/javascript">
	var alphanumeric = "[ A-Za-z0-9]";
	function validateKeypress() {
		var validChars = new RegExp(alphanumeric);
    var keyChar = String.fromCharCode(event.which || event.keyCode);
    return validChars.test(keyChar) ? keyChar : false;}
	
	function setSelectedIndex(s, i) {
		s.options[i-1].selected = true;
		return; }

		
	</script>
	
</dsp:page>
