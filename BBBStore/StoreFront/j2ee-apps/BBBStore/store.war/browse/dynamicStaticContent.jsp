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
			<dsp:param name="skuId" value="${skuId}"/>
			<dsp:oparam name="output">
				<dsp:getvalueof var="vdcDelTime" param="vdcShipMsg"/>
				<dsp:getvalueof var="ltlSku" param="ltlSku" scope="request"/>
				<dsp:getvalueof var="personalizedSku" param="personalizedSku" scope="request"/>
			</dsp:oparam>
			<dsp:oparam name="error">
			</dsp:oparam>
		</dsp:droplet>
		<c:if test="${not empty vdcDelTime }">
			<c:set target="${placeHolderMapVDCMsg}" property="vdcDelTime" value="${vdcDelTime}" />
			<div class="noMar highlightRed" title=" Item Ships Directly from Vendor">
				<c:choose>
					<c:when test="${ltlSku}">
						<bbbl:label key="lbl_vdc_del_ltl_msg" placeHolderMap="${placeHolderMapVDCMsg}" language="${pageContext.request.locale.language}" />					
					</c:when>					
					<c:when test="${personalizedSku}">
						<bbbl:label key="lbl_vdc_del_personalize_msg" placeHolderMap="${placeHolderMapVDCMsg}" language="${pageContext.request.locale.language}" />					      
					</c:when>
					<c:otherwise>
						<bbbt:label key="lbl_vdc_del_rest_msg" placeHolderMap="${placeHolderMapVDCMsg}" language="${pageContext.request.locale.language}" />
					</c:otherwise>
				</c:choose>
			</div>
		 </c:if>
	</c:if>
	<dsp:include page="/cms/static/staticpage.jsp">	
		<dsp:param name="pageName" param="pageName"/>
	</dsp:include>
	<%-- END BPSI 1928 Retrieve VDC attributes to formulate logic to construct the message--%>
</dsp:page>