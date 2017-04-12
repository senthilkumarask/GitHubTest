<%@ taglib prefix="dsp"
	uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>

<dsp:page>
	<dsp:importbean bean="/com/bbb/integration/configkeys/ConfigKeyChangeValueDroplet" />
	<dsp:droplet name="ConfigKeyChangeValueDroplet">
		<dsp:param param="configKeyName" name="configKeyName" />
		<dsp:param param="configKeyValue" name="configKeyValue" />
		<dsp:param param="configKeyType" name="configKeyType" />
		<dsp:param param="site" name="site" />
		<dsp:param param="channel" name="channel" />
		
		<dsp:oparam name="output">
			Config key updated successfully.
		</dsp:oparam>
		<dsp:oparam name="empty">
			One of the required input parameters is empty. Please provide configKeyName,configKeyValue,configKeyType.
		</dsp:oparam>
		<dsp:oparam name="error">
			Error in updating the config key.
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>