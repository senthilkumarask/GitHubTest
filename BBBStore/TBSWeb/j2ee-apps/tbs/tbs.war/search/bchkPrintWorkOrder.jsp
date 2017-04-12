<dsp:page>
	<dsp:importbean bean="/com/bbb/search/droplet/TBSPrintWorkOrderDroplet"/>
	
	<dsp:droplet name="TBSPrintWorkOrderDroplet">
		<dsp:param name="onlineOrderNumber" param="order_num"/>
		<dsp:oparam name="output">
			<dsp:getvalueof param="atgorderId" var="atgorderId"/>
		</dsp:oparam>
	</dsp:droplet>
	<dsp:include src="/tbs/checkout/checkout_confirmation.jsp">
		<dsp:param name="orderId" value="${atgorderId}"/>
	</dsp:include>
</dsp:page>