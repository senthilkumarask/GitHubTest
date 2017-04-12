<dsp:page>
<dsp:importbean bean="/atg/userprofiling/Profile" />

<dsp:droplet name="/com/bbb/commerce/browse/droplet/AkamaiZipCodeDroplet">
	<dsp:param name="profile" bean="Profile"/>
	<dsp:oparam name="output">
		<dsp:getvalueof var="preferredShippingZipCode" param="zipCode"/>
	</dsp:oparam>
</dsp:droplet>

<json:object>
	<json:property name="zipcode" value="${preferredShippingZipCode}"/>
</json:object>                                                         

</dsp:page>

