<dsp:page>

  <%-- This page will actually calculate the price for the user.
       This page expects the following parameters
       - price - price to format
       - priceListLocale (optional) - locale in which to format prices. If not passed then Profile's
                                      price lists's locale will be used.
  --%>
  <dsp:importbean bean="/atg/commerce/pricing/CurrencyCodeDroplet"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/dynamo/servlet/RequestLocale"/>
  <dsp:importbean bean="/atg/multisite/SiteContext"/>
	<dsp:importbean bean="/atg/store/StoreConfiguration" />

  <dsp:getvalueof var="priceListLocale" vartype="java.lang.String" param="priceListLocale"/>
  <c:if test="${empty priceListLocale}">
    <dsp:getvalueof var="priceListLocale" vartype="java.lang.String" bean="Profile.priceList.locale"/>
  </c:if>
  <dsp:getvalueof var="requestLocale" vartype="java.lang.String" bean="RequestLocale.locale"/>
  <dsp:getvalueof var="saveFormattedPrice" param="saveFormattedPrice"/>
   <dsp:getvalueof var="showMsg" param="showMsg" />
  <dsp:getvalueof var="isFromPDP" param="isFromPDP"/>
  <dsp:droplet name="CurrencyCodeDroplet">
    <dsp:param name="locale" value="${priceListLocale}"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="currencyCode" vartype="java.lang.String" param="currencyCode"/>
      
      <dsp:getvalueof var="price" vartype="java.lang.Double" param="price"/>
      
     
      <%-- display price formatted according to the pricelist locale --%>
      <c:set var="formatedPrice"><dsp:valueof value="${price}" converter="unformattedCurrency" /></c:set>
      
      <fmt:parseNumber var="formatedPriceVal" type="number"  value="${formatedPrice}" />

      <c:choose>
      	<c:when test="${price<=0.01}">
	     	 <bbbl:label key='lbl_price_is_tbd' language="${pageContext.request.locale.language}"/>
		</c:when>
		<c:otherwise>
		   <c:set var="format"><dsp:valueof param="productVO.defaultPriceRangeDescription"/></c:set>
			<c:set var="priceFromConvertor"><dsp:valueof value="${price}" converter="mxCurrency"/></c:set>
			 <dsp:valueof symbol="${isFromPDP}" format="${format}" converter="formattedPrice" value="${priceFromConvertor}"/>
				<fmt:formatNumber var="priceForCertona" value="${price}" type="currency" currencyCode="${currencyCode }"/>
				<c:set var="omniCertonaPrice"><dsp:valueof value="${price}" converter="unformattedCurrency"/></c:set>
				 <dsp:getvalueof var="certonaPrice" value="${omniCertonaPrice}" scope="request"/>
				 <c:if test="${not empty showMsg}">
					<bbbl:label key='${showMsg}' language="${pageContext.request.locale.language}" />					
				</c:if>
			</c:otherwise>
     	 </c:choose>
		
      
     
    </dsp:oparam>
  </dsp:droplet><%-- End CurrencyCode Droplet --%>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/Storefront/j2ee/store.war/global/gadgets/formattedPrice.jsp#2 $$Change: 635969 $--%>
