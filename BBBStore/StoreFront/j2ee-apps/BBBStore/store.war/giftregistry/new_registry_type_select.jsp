
<dsp:page>
	<dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/SimplifyRegFormHandler" />
	
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
			
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>

 
<dsp:form action="${contextPath}/giftregistry/simpleReg_creation_form.jsp">
	<dsp:droplet name="ErrorMessageForEach">
	  <dsp:param bean="SimplifyRegFormHandler.formExceptions" name="exceptions"/>
	  <dsp:oparam name="output">
	  <dsp:valueof param="message"/><br>
	  </dsp:oparam>
	</dsp:droplet>


   <div class="createRegistry selectTypeFOR">
		
		<dsp:select  bean="SimplifyRegFormHandler.registryEventType"  iclass="selector_primary triggerSubmit">	
            <dsp:tagAttribute name="aria-required" value="false"/>
			<dsp:tagAttribute name="data-submit-button" value="submitClick"/>	
					<dsp:droplet name="GiftRegistryTypesDroplet">
						<dsp:param name="siteId" value="${currentSiteId}"/>
						<dsp:oparam name="output">
						<dsp:option value="" selected="selected"><bbbl:label key="lbl_regsearch_select_type" language ="${pageContext.request.locale.language}"/></dsp:option>
							<dsp:droplet name="ForEach">
								<dsp:param name="array" param="registryTypes" />
								<dsp:oparam name="output">
								
									<dsp:param name="regTypes" param="element" />	
									<dsp:getvalueof var="regTypesId" param="regTypes.registryName" />
									<dsp:getvalueof var="registryCode" param="regTypes.registryCode" />
									<dsp:option value="${registryCode}">
									<dsp:valueof param="element.registryName"></dsp:valueof>
									</dsp:option>
								
								</dsp:oparam>
								
							</dsp:droplet>
						</dsp:oparam>
					</dsp:droplet>
					<dsp:tagAttribute name="aria-label" value="select the registry you would like to create"/>

			</dsp:select>
		
		
		<dsp:input bean="SimplifyRegFormHandler.ErrorURL" type="hidden"
			value="${contextPath}/giftregistry/new_registry_type_select.jsp" />
		<dsp:input bean="SimplifyRegFormHandler.SuccessURL" type="hidden"
			value="${contextPath}/giftregistry/simpleReg_creation_form.jsp" />
		<dsp:input id="submitClick"
			bean="SimplifyRegFormHandler.registryTypes" type="submit" value="Submit" ></dsp:input>
		

    </div>
    
</dsp:form>
</dsp:page>
