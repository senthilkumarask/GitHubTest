<dsp:page>
<dsp:importbean bean="/atg/commerce/order/OrderLookup" />
<dsp:importbean bean="com/bbb/account/OrderHistoryDroplet" />
<dsp:importbean bean="com/bbb/account/OrderSummaryDetails" />
<c:set var="BedBathCanadaSite">
	<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
</c:set>
<c:set var="orderTrackingWidgetIFrameUrl" scope="request">
<bbbc:config key="Order_Tracking_IFrame_URL" configName="International_Shipping"/></c:set>
<bbb:pageContainer>
    <jsp:attribute name="bodyClass">atg_store_pageHome</jsp:attribute>
    <jsp:attribute name="section">accounts</jsp:attribute>
    <jsp:attribute name="pageWrapper">orderDetailWrapper myAccount useBazaarVoice</jsp:attribute>
    
    <%-- use url param to show all orders, for debugging only --%>
    <c:set var="showAll" scope="request" value="false"/>
	<c:if test="${not empty param.showAll}">
		<c:set var="showAll" scope="request" value="${param.showAll}"/>
	</c:if>
    
    <jsp:body>
    
   		<div id="content" class="small-12 columns" role="main">
				<iframe id="borderFreeOrderTrackingId" width="100%" name="borderFreeOrderTracking" height="750" src="${orderTrackingWidgetIFrameUrl}" frameBorder="1" scrolling="no" title="Border Free Order Tracking Widget"></iframe>
		</div>
   		</div>
	</jsp:body>
	
	</bbb:pageContainer>
</dsp:page>