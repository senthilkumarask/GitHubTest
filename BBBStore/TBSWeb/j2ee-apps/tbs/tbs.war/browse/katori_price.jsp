<dsp:page>
	
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/GetPriceFromKatoriDroplet"/>
	<dsp:getvalueof param="refNum" var="refNum" />
	<dsp:droplet name="GetPriceFromKatoriDroplet">
		<dsp:param name="refNum" param="refNum"/>
		<dsp:param name="skuId" param="skuId"/>
		<dsp:param name="productId" param="productId"/>
		<dsp:oparam name="output">
			<json:object>
				<json:property name="shopperCurrency"><dsp:valueof param="shopperCurrency"/></json:property>
				<json:property name="eximPersonalizedPrice"><dsp:valueof param="eximPersonalizedPrice"/></json:property>
				<json:property name="eximItemPrice"><dsp:valueof param="eximItemPrice"/></json:property>
				<json:property name="shipMsgFlag"><dsp:valueof param="shipMsgFlag"/></json:property>
				<json:property name="freeShippingMsg" escapeXml="false"><dsp:valueof param="freeShippingMsg" valueishtml="true"/></json:property>
			</json:object>
		</dsp:oparam>
		<dsp:oparam name="error">
			<json:object>
				<json:property name="errorMsg" ><dsp:valueof param="errorMsg" /></json:property>
			</json:object>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>