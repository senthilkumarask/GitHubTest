<dsp:page>

<%-- This page will actually calculate the price for the user.
	This page expects the following parameters
	- price - price to format
	- priceListLocale (optional) - locale in which to format prices. If not passed then Profile's
									price lists's locale will be used.
--%>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/pricing/CurrencyCodeDroplet"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/dynamo/servlet/RequestLocale"/>
	<dsp:importbean bean="/atg/multisite/SiteContext"/>

	<%-- Variables --%>
	<dsp:getvalueof var="priceListLocale" vartype="java.lang.String" param="priceListLocale"/>
	<c:if test="${empty priceListLocale}">
		<dsp:getvalueof var="priceListLocale" vartype="java.lang.String" bean="Profile.priceList.locale"/>
	</c:if>
	<dsp:getvalueof var="requestLocale" vartype="java.lang.String" bean="RequestLocale.locale"/>
	<dsp:getvalueof var="saveFormattedPrice" param="saveFormattedPrice"/>

	<dsp:droplet name="CurrencyCodeDroplet">
		<dsp:param name="locale" value="${priceListLocale}"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="currencyCode" vartype="java.lang.String" param="currencyCode"/>
			<dsp:getvalueof var="price" vartype="java.lang.Double" param="price"/>

		<c:choose>
			<c:when test="${price<=0.01}">
				<c:choose>
					<c:when test="${not empty customizableCodes && fn:contains(customizeCTACodes, customizableCodes)}">
						<bbbl:label key='lbl_price_is_tbd_customize' language="${pageContext.request.locale.language}"/>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${not empty lblPriceTBD}">
								${lblPriceTBD}
							</c:when>
							<c:otherwise>
								<bbbl:label key='lbl_price_is_tbd_tbs' language="${pageContext.request.locale.language}"/>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>
				<c:if test="${priceListLocale != requestLocale}">
					<%-- Reset page's locale to the pricelist locale --%>
					<c:if test="${fn:length(priceListLocale)>5 }">
						<c:set var="variant" value="${fn:substring(priceListLocale,6,fn:length(priceListLocale))}"/>
						<c:set var="priceListLocale" value="${fn:substring(priceListLocale,0,5)}"/>
					</c:if>
					<fmt:setLocale value="${priceListLocale}" variant="${variant}"/>
					<%-- Reset resource bundle so that locale of page's localization context
						will be updated to the pricelist locale too --%>
	
					<dsp:getvalueof var="resourceBundle" bean="SiteContext.site.resourceBundle" />
					<fmt:setBundle basename="${resourceBundle}"/>
				</c:if>
	
				<%-- display price formatted according to the pricelist locale --%>
				<c:choose>
					<c:when test="${saveFormattedPrice}">
						<fmt:formatNumber var="formattedPrice" value="${price}" type="currency" currencyCode="${currencyCode }" scope="request"/>
					</c:when>
					<c:otherwise>
						<fmt:formatNumber value="${price}" type="currency" currencyCode="${currencyCode }"/>
						<fmt:formatNumber var="priceForCertona" value="${price}" type="currency" currencyCode="${currencyCode }"/>
						<dsp:getvalueof var="certonaPrice" value="${price}" scope="request"/>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
			</c:choose>

		</dsp:oparam>
	</dsp:droplet>
	<%-- End CurrencyCode Droplet --%>

</dsp:page>
