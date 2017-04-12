<%@ taglib uri="http://htmlcompressor.googlecode.com/taglib/compressor" prefix="compress" %>
<dsp:page>
	<% pageContext.setAttribute("newLineChar", "\n"); %>
	<% pageContext.setAttribute("lineFeedChar", "\r"); %>
	<% pageContext.setAttribute("tabChar", "\t"); %>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/GetPriceFromKatoriDroplet"/>
	<dsp:getvalueof param="refNum" var="refNum" />
	<dsp:droplet name="GetPriceFromKatoriDroplet">
		<dsp:param name="refNum" param="refNum"/>
		<dsp:param name="skuId" param="skuId"/>
		<dsp:param name="productId" param="productId"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="priceLabelCode" param="priceLabelCode"/>
			<dsp:getvalueof var="inCartFlag" param="inCartFlag"/>
			<dsp:getvalueof var="salePrice" param="eximItemPrice"/>
			<dsp:getvalueof var="listPrice" param="wasPrice"/>
			<dsp:getvalueof var="isdynamicPriceEligible" param="dynamicPriceSku"/>
			<json:object>
				<json:property name="shopperCurrency"><dsp:valueof param="shopperCurrency"/></json:property>
				<json:property name="eximPersonalizedPrice"><dsp:valueof param="eximPersonalizedPrice"/></json:property>
				<json:property name="eximItemPrice"><dsp:valueof param="eximItemPrice"/></json:property>
				<json:property name="shipMsgFlag"><dsp:valueof param="shipMsgFlag"/></json:property>
				<json:property name="freeShippingMsg" escapeXml="false"><dsp:valueof param="freeShippingMsg" valueishtml="true"/></json:property>
				<json:property name="priceHtml" escapeXml="true">
							<compress:html removeIntertagSpaces="true"><dsp:include page="/browse/browse_price_frag.jsp">
								<dsp:param name="priceLabelCode" value="${priceLabelCode}" />
								<dsp:param name="inCartFlag" value="${inCartFlag}" />
								<c:choose>
								<c:when test="${!(empty listPrice)}">
									<dsp:param name="salePrice" value="${salePrice}" />
									<dsp:param name="listPrice" value="${listPrice}" />
								</c:when>
								<c:otherwise>
								 	<dsp:param name="listPrice" value="${salePrice}" />
								</c:otherwise>
								</c:choose>
								<dsp:param name="isdynamicPriceEligible" value="${isdynamicPriceEligible}" />
								<dsp:param name="isFromPDP" value="${true}" />
								<dsp:param name="isKatoriPrice" value="${true}" />
								<dsp:param name="shopperCurrency" param="shopperCurrency" />
								</dsp:include></compress:html>
				</json:property> 
			</json:object>
		</dsp:oparam>
		<dsp:oparam name="error">
			<json:object>
				<json:property name="errorMsg" ><dsp:valueof param="errorMsg" /></json:property>
			</json:object>
		</dsp:oparam>
	</dsp:droplet>
	
 
</dsp:page>

