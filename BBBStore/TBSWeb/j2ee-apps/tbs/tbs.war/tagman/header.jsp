<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	
	<dsp:getvalueof var="transient" bean="Profile.transient" />
	<dsp:param name="order" bean="ShoppingCart.current"/>
	<dsp:param name="lastOrder" bean="ShoppingCart.last"/>
	<dsp:getvalueof var="billingAddressCurrent" param="order.billingAddress"/>
	<dsp:getvalueof var="billingAddressLast" param="lastOrder.billingAddress"/>
	<dsp:getvalueof var="profileId" bean="Profile.id"/>
	<dsp:getvalueof var="currentSiteId" bean="/atg/multisite/Site.id" />
	<c:set var="TBS_BedBathUSSiteCode"><bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" /></c:set>
    <c:set var="TBS_BuyBuyBabySiteCode"><bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" /></c:set>
    <c:set var="TBS_BedBathCanadaSiteCode"><bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" /></c:set>
    <c:choose>
        <c:when test="${currentSiteId eq TBS_BedBathUSSiteCode}">
            <c:set var="GAAccountId"><bbbc:config key="googleAnalyticsId_US" configName="ThirdPartyURLs" /></c:set>
        </c:when>
        <c:when test="${currentSiteId eq TBS_BuyBuyBabySiteCode}">
            <c:set var="GAAccountId"><bbbc:config key="googleAnalyticsId_Baby" configName="ThirdPartyURLs" /></c:set>
        </c:when>
        <c:otherwise>
            <c:set var="GAAccountId"><bbbc:config key="googleAnalyticsId_CA" configName="ThirdPartyURLs" /></c:set>
        </c:otherwise>
    </c:choose>
    <c:choose>
    	<c:when test="${transient == 'false'}">
			<c:set var="email">
				<dsp:valueof bean="Profile.email" />
			</c:set>
		</c:when>
    	<c:otherwise>
    		<c:choose>
    			<c:when test="${billingAddressCurrent ne null}">
    				<c:set var="email" value="${billingAddressCurrent.email}"></c:set>
    			</c:when>
    			<c:otherwise>
    				<c:set var="email" value="${billingAddressLast.email}"></c:set>
    			</c:otherwise>
    		</c:choose>
    	</c:otherwise>
    </c:choose>
    
		
	<%-- Begin TagMan --%>				
	<script type="text/javascript">
		window.tmParam = {site_id:'${currentSiteId}'};			
		window.tmParam.customer_email = '${email}';	
		window.tmParam.profile_id = '${profileId}';
		window.tmParam.GAAccount = 'GAAccountId';
		window.tmParam.trackPageview = '_trackPageview';
	</script>
	<%-- End TagMan --%>
	
</dsp:page>