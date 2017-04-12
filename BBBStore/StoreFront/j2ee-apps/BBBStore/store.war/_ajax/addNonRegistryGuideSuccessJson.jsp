<dsp:page>
	<dsp:importbean bean="/com/bbb/integration/interactive/InteractiveChecklistFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:droplet name="IsEmpty">
		<dsp:param name="value" bean="InteractiveChecklistFormHandler.addGuideRedirectURL"/>
		<dsp:oparam name="false">
			<json:object>
				<json:property escapeXml="false" name="addGuideRedirectURL">
					<dsp:valueof bean="InteractiveChecklistFormHandler.addGuideRedirectURL"/>
				</json:property>
			</json:object>	
		</dsp:oparam>
	</dsp:droplet>	
</dsp:page>