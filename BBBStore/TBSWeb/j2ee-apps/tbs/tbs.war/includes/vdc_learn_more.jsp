<dsp:page>
<!doctype html>
<dsp:droplet name="/com/bbb/utils/BBBLogBuildNumber">
	<dsp:oparam name="output">
		<dsp:getvalueof var="buildRevisionNumber" param="BUILD_TAG_NUM" />
	</dsp:oparam>
</dsp:droplet>
<%-- <c:import url ="/_includes/end_script.jsp" /> --%>
<script type="text/javascript" src="/tbs/_includes/end_script.jsp">

</script>

<c:if test="${mobileRedirectOn}">
<c:choose>
    <c:when test="${minifiedJSFlag == 'true'}">
        <script type="text/javascript" src="${jsPath}/_assets/global/js/mobileRedirect.min.js?v=${buildRevisionNumber}"></script>
    </c:when>
    <c:otherwise>       
        <script type="text/javascript" src="${jsPath}/_assets/global/js/mobileRedirect.js?v=${buildRevisionNumber}"></script>
    </c:otherwise>
</c:choose>
</c:if>

<dsp:getvalueof var="msgType" bean="/OriginatingRequest.msgType"/>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:getvalueof var="siteId" bean="Site.id" />
<dsp:getvalueof var="shippingMethodCode" param="shipMethod"/>
<dsp:getvalueof var="skuId" param="skuId"/>
<dsp:getvalueof var="frmEmail" param="frmEmail"/>

<jsp:useBean id="placeHolderMapVDCMsg" class="java.util.HashMap" scope="request" />
<jsp:useBean id="placeHolderMapServiceLevel" class="java.util.HashMap" scope="request" />

<%-- Droplet to get the vdc message shipping and offset date --%>
<dsp:droplet name="/com/bbb/commerce/browse/VDCShippingMessagingDroplet">
	<dsp:param name="shippingMethodCode" value="${shippingMethodCode}" />
	<dsp:param name="skuId" value="${skuId}"/>
	<dsp:param name="siteId" value="${siteId}"/>
	<dsp:oparam name="output">
		<dsp:getvalueof var="vdcDelTime" param="vdcShipMsg"/>
	</dsp:oparam>
	<dsp:oparam name="vdcMsg">
		<dsp:getvalueof var="offsetDateVDC" param="offsetDateVDC" />
	</dsp:oparam>																
</dsp:droplet>

	<c:choose>
	   <c:when	test="${msgType eq 'vdc'}">
	   		<%-- Display title only if the request is coming from Email learn more link --%>
	   		<c:if test="${frmEmail}">
	   			<h2><bbbt:label key="lbl_vdc_del_learn_more_title" language="${pageContext.request.locale.language}" /></h2>
	   		</c:if>
	   		
	   		<%-- Display dynamic vdc shipping message only if we are getting the shipping date --%>
	   		<c:if test="${not empty vdcDelTime }">
				<c:set target="${placeHolderMapVDCMsg}" property="vdcDelTime" value="${vdcDelTime}" />
				<h2><bbbt:label key="lbl_vdc_del_learn_more_title" language="${pageContext.request.locale.language}" /></h2>
				<br>
				<div class="marTop_n7 highlightRed">
					<bbbt:label key="lbl_vdc_del_rest_msg_tbs" placeHolderMap="${placeHolderMapVDCMsg}" language="${pageContext.request.locale.language}" />
				</div>
		 	</c:if>
		 	<br>
	        <bbbt:textArea key="txt_vdc_learn_more" language ="${pageContext.request.locale.language}"/>
	   </c:when>
       <c:when	test="${msgType eq 'offset'}">
	   		<h2><bbbt:label key="lbl_vdc_offset_learn_more_title" language="${pageContext.request.locale.language}" /></h2>
	   		<br>
	   		<%-- Display dynamic vdc offset message only if we are getting the offset date --%>
       		<c:if test="${not empty offsetDateVDC }">
				<c:set target="${placeHolderMapServiceLevel}" property="actualOffSetDate" value="${offsetDateVDC}" />
				<div class="marTop_n7 highlightRed" >
					<bbbt:textArea key="txt_vdc_offset_msg" placeHolderMap="${placeHolderMapServiceLevel}"	language="${pageContext.request.locale.language}" />
				</div>
			</c:if>
		    <br>
		    <bbbt:textArea key="txt_offset_learn_more" language ="${pageContext.request.locale.language}"/>
	  </c:when>
	</c:choose> 
</dsp:page>