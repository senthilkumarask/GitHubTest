<dsp:page>

<%-- 
<dsp:getvalueof var="shippingGroups" param="shippingGroups"/>
<dsp:getvalueof var="registryMap" param="registryMap"/>
<dsp:getvalueof var="commerceItemVOList" param="commerceItemVOList"/>
<dsp:getvalueof var="carrierUrlMap" param="carrierUrlMap"/>
<dsp:getvalueof var="trackingInfos" param="trackingInfos"/>
<dsp:getvalueof var="orderType" param="orderType"/>
<dsp:getvalueof var="orderStatus" param="orderStatus"/>
--%>


<dsp:importbean bean="com/bbb/account/OrderSummaryDetails" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/com/bbb/account/TrackingInfoDroplet" />
<dsp:importbean bean="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet" />


<c:set var="scene7Path">
    <bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
</c:set>

<dsp:getvalueof var="orderNum" param="orderNum" />
<dsp:getvalueof var="orderType" param="orderType" />
<dsp:getvalueof var="emailIdMD5" param="emailIdMD5" />
<dsp:getvalueof var="wsOrder" param="wsOrder" />


<dsp:droplet name="OrderSummaryDetails">
	<dsp:param name="orderNum" value="${orderNum}"/>
	<dsp:param name="emailIdMD5" value="${emailIdMD5}"/>
	<dsp:param name="wsOrder" value="${wsOrder}"/>
	<dsp:oparam name="output">
		<dsp:getvalueof var="BBBTrackOrderVO" param="BBBTrackOrderVO"/>
		<c:set var="TrackOrderVO" value="${BBBTrackOrderVO}" scope="request"/>		
		<dsp:getvalueof var="order" value="${TrackOrderVO.legacyOrderVO}" scope="request"/>
			
			<dsp:include page="track_order_frag_legacy.jsp">
						<dsp:param name="orderDetails" value="${order}"/>
						<dsp:param name="orderId" value="${orderNum}"/>
						<dsp:param name="fromOrderSummary" value="true"/>
					</dsp:include>	
	</dsp:oparam>
	<dsp:oparam name="error">
		<bbbe:error key="err_orderhistory_techerr"
					language="${pageContext.request.locale.language}" />
	</dsp:oparam>
</dsp:droplet>	
</dsp:page>