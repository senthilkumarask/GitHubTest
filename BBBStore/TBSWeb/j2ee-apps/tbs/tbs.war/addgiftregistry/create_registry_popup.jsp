<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<c:set var="TBS_BedBathUSSite">
		<bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BuyBuyBabySite">
		<bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BedBathCanadaSite">
		<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>

	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	
			
	<div class="createRegistryModal" title="Create Registry">
		<h2>Create Registry</h2>
		<dsp:form name="createRegisDialogForm" id="createRegisDialogForm">
			<p class="smallerFont"><bbbl:label key="lbl_regcreate_new_registry" language ="${pageContext.request.locale.language}"/></p>
			
			<dsp:select
				bean="GiftRegistryFormHandler.registryEventType"
				id="registeryType"  name="registeryType" iclass="small-12 medium-5">
				<dsp:droplet name="GiftRegistryTypesDroplet">
					<dsp:param name="siteId" value="${currentSiteId}"/>
					<dsp:oparam name="output">
						<dsp:option value="" selected="selected"><bbbl:label key="lbl_regsearch_select_type" language ="${pageContext.request.locale.language}"/></dsp:option>
						<dsp:droplet name="ForEach">
							<dsp:param name="array" param="registryTypes" />
							<dsp:oparam name="output">
								<dsp:param name="regTypes" param="element" />
								<dsp:getvalueof var="regTypesId"
									param="regTypes.registryName" />
									<dsp:getvalueof var="registryCode"
									param="regTypes.registryCode" />
								<dsp:option value="${registryCode}">
									<dsp:valueof param="element.registryName"></dsp:valueof>
								</dsp:option>
							</dsp:oparam>
						</dsp:droplet>
					</dsp:oparam>
				</dsp:droplet>
                <dsp:tagAttribute name="aria-required" value="false"/>
                <dsp:tagAttribute name="aria-labelledby" value="registeryType lblregisteryType errorregisteryType"/>
			</dsp:select>

			<p class="smallerFont"><bbbt:textArea key="txt_createreg_promos" language ="${pageContext.request.locale.language}"/></p>
			<div class="marTop_20 buttonpane clearfix">
			<div class="ui-dialog-buttonset">
				<div class="fl small-6 columns button_active no-padding-left">
				 <c:set var="submitBtn"><bbbl:label key='lbl_create' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
				<dsp:input id="submitClick"	bean="GiftRegistryFormHandler.registryTypes" type="submit" name="createRegisteryBtn" value="${submitBtn}" iclass="small button service column">
                    <dsp:tagAttribute name="aria-pressed" value="false"/>
                    <dsp:tagAttribute name="aria-labelledby" value="submitClick"/>
                    <dsp:tagAttribute name="role" value="button"/>
                </dsp:input>
				</div>
				<c:set var="cancelBtn"><bbbl:label key='lbl_cancel' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
                <div class="small-6 columns no-padding-right">
                    <a href="#" title="${cancelBtn}" class="close-any-dialog buttonTextLink small button column secondary close-modal">${cancelBtn}</a>
                </div>
				<c:choose>
											<c:when test="${currentSiteId == TBS_BedBathUSSite}">
												
												<dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="headerSimpleRegBedBath" />
											</c:when>
											<c:when test="${currentSiteId == TBS_BuyBuyBabySite}">
												
												<dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="headerSimpleRegBuyBuy" />
											</c:when>
											<c:otherwise>
											
												<dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="headerSimpleRegBedBath" />
											</c:otherwise>
										</c:choose>
		</div>
			</div>
		</dsp:form>
  <a class="close-reveal-modal">&#215;</a>
	</div>
<!-- <script type="text/javascript">
$(document).ready(function(){
	$( "input.service" ).click(function() {
	    $( "#createRegisDialogForm" ).submit();
	});
});
</script> -->


</dsp:page>