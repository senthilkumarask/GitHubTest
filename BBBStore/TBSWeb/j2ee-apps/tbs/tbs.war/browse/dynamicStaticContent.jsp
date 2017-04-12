<dsp:page>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<dsp:getvalueof var="pageName" param="pageName" scope="page"/>
	<dsp:getvalueof var="isVdcSku" param="isVdcSku"/>
	<dsp:getvalueof var="skuId" param="skuId"/>
	<dsp:getvalueof var="shippingMethodCode" param="shippingMethodCode"/>
	<jsp:useBean id="placeHolderMapVDCMsg" class="java.util.HashMap" scope="request" />
	
<%-- START BPSI 1928 Retrieve VDC attributes to formulate logic to construct the message --%>
	
	<c:if test="${isVdcSku && not empty skuId}">
		<dsp:droplet name="/com/bbb/commerce/browse/VDCShippingMessagingDroplet">
			<dsp:param name="skuId" value="${vdcSkuId}"/>
			<dsp:oparam name="output">
				<dsp:getvalueof var="vdcDelTime" param="vdcShipMsg"/>
			</dsp:oparam>
			<dsp:oparam name="error">
			</dsp:oparam>
		</dsp:droplet>
		<c:if test="${not empty vdcDelTime }">
			<c:set target="${placeHolderMapVDCMsg}" property="vdcDelTime" value="${vdcDelTime}" />
			<div class="noMar highlightRed" title=" Item Ships Directly from Vendor">
				<bbbt:label key="lbl_vdc_del_rest_msg_tbs" placeHolderMap="${placeHolderMapVDCMsg}" language="${pageContext.request.locale.language}" />
			</div>
		 </c:if>
	</c:if>
	<br>
	<dsp:include page="/cms/static/staticpage.jsp">	
		<dsp:param name="pageName" param="pageName"/>
	</dsp:include>
	<%-- END BPSI 1928 Retrieve VDC attributes to formulate logic to construct the message--%>
</dsp:page>