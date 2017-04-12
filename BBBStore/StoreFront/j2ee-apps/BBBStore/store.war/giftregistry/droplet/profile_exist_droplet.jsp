<dsp:page>
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/ProfileExistCheckDroplet" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
		
	<dsp:getvalueof var="emailId" param="emailId"/>

	<dsp:droplet name="ProfileExistCheckDroplet">
		<dsp:param name="siteId" value="${appid}"/>
		<dsp:param value="${emailId}" name="emailId" />
		<dsp:oparam name="output">
	          <dsp:valueof param="doesCoRegistrantExist"/>
		</dsp:oparam>
	</dsp:droplet>

</dsp:page>


