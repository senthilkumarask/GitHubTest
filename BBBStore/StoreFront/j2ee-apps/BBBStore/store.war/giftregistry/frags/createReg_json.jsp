<dsp:page>Sapient needs to work on this page.
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:droplet name="IsEmpty">
		<dsp:param name="value" bean="GiftRegistryFormHandler.formExceptions"/>
		<dsp:oparam name="false">
	   Error occurred.<br>
				<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
					<dsp:param bean="GiftRegistryFormHandler.formExceptions" name="exceptions"/>
					<dsp:oparam name="output">						
							<dsp:valueof param="message"/>			
					</dsp:oparam>				
				</dsp:droplet>
		</dsp:oparam>
		<dsp:oparam name="true">
			No Error occurred.		
		</dsp:oparam>					
	</dsp:droplet>	
</dsp:page>