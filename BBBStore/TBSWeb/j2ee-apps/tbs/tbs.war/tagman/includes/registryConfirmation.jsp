<dsp:page>
	<dsp:importbean	bean="/com/bbb/tag/droplet/ReferralInfoDroplet" />
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean" />	
	<dsp:getvalueof var="regTypeName" bean="GiftRegSessionBean.registryVO.registryType.registryTypeName" />
	<dsp:getvalueof var="regId" bean="GiftRegSessionBean.registryVO.registryId" />
	<dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI"/>
	
	<c:choose>
		<c:when test="${regTypeName eq 'BA1'}">
			<c:set var="regTypeName">2153</c:set>
		</c:when>
		<c:otherwise>
			<c:set var="regTypeName">1959</c:set>
		</c:otherwise>
	</c:choose>
	
	<c:choose>
 		<c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
			<c:set var="cj_cid"><bbbc:config key="cj_cid_baby" configName="ReferralControls" /></c:set>
			<c:set var="cj_type"><bbbc:config key="cj_type_registry_confirmation_baby" configName="ReferralControls" /></c:set>
 		</c:when>
 		<c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
		<c:set var="cj_cid"><bbbc:config key="cj_cid_ca" configName="ReferralControls" /></c:set>
		<c:set var="cj_type"><bbbc:config key="cj_type_registry_confirmation_ca" configName="ReferralControls" /></c:set>
		</c:when>
 		<c:otherwise>
			<c:set var="cj_cid"><bbbc:config key="cj_cid_us" configName="ReferralControls" /></c:set>
			<c:set var="cj_type"><bbbc:config key="cj_type_registry_confirmation_us" configName="ReferralControls" /></c:set>			
 		</c:otherwise>
 	</c:choose>
 	<dsp:droplet name="ReferralInfoDroplet">
		<dsp:param name="currentPage" value="${requestURI}"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="cjBaseUrl" param="cjSaleUrl" />
			<dsp:getvalueof var="wcBaseUrl" param="wcSaleUrl" />
		</dsp:oparam>
	</dsp:droplet>
	
		<%-- Begin TagMan --%>
		<script type="text/javascript">	
			// client configurable parameters 
			window.tmParam.registry_type = '${regTypeName}';
			window.tmParam.registry_number = '${regId}';
			window.tmParam.currency_code = 'USD';
			
			window.tmParam.cj_base_url = '${cjBaseUrl}';
			window.tmParam.cj_cid = '${cj_cid}';
			window.tmParam.cj_type = '${cj_type}';
			window.tmParam.cj_height = '1';
			window.tmParam.cj_width = '1';
			window.tmParam.cj_amount = '0';
			window.tmParam.cj_method = 'IMG';
			
			window.tmParam.wc_base_url = '${wcBaseUrl}';
			window.tmParam.retailer_uid = '10001';
			window.tmParam.page_type = 'RegistryConfirmation';
		</script>
		<%-- End TagMan --%>
</dsp:page>