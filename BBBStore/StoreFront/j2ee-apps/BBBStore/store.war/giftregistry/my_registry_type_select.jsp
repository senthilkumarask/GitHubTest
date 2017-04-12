<dsp:page>
    <dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	    
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <dsp:form action="${contextPath}/giftregistry/simpleReg_creation_form.jsp" requiresSessionConfirmation="false">
        <dsp:droplet name="ErrorMessageForEach">
            <dsp:param bean="GiftRegistryFormHandler.formExceptions" name="exceptions"/>
            <dsp:oparam name="output">
                <dsp:valueof param="message"/><br>
            </dsp:oparam>
        </dsp:droplet>

      
            <div id="selectRegistryType">
                <div class="downarrow">
                    <div class="arrow"></div>                
                </div>
                <div class="clear"></div>
            </div>
            <div class="clear"></div>

           <div class="input width_2">
                <div class="regType marTop_10 button_select button_select_active selectTypeFOR">
                <label id="selectRegistry"><bbbl:label key="lbl_select_registry_type" language ="${pageContext.request.locale.language}"/></label>
					<dsp:select bean="GiftRegistryFormHandler.registryEventType" iclass="selector_primary selectRegToCreate" id="typeofregselect4">    
						<dsp:tagAttribute name="data-submit-button" value="submitClick4"/>
                        <dsp:tagAttribute name="aria-required" value="true"/>
						<dsp:tagAttribute name="aria-hidden" value="false"/>
						<dsp:droplet name="GiftRegistryTypesDroplet">
							<dsp:param name="siteId" value="${currentSiteId}"/>
							<dsp:oparam name="output">
								<dsp:option value="" selected="selected"><bbbl:label key="lbl_regcreate_select_type" language ="${pageContext.request.locale.language}"/></dsp:option>
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
                        <dsp:tagAttribute name="aria-label" value="select the Registry you would like to create. You will be redirected to the begin the creation process."/>
					</dsp:select>
				</div>
		  </div>
            
            <%-- dsp:input bean="GiftRegistryFormHandler.ErrorURL" type="hidden" value="${contextPath}/giftregistry/registry_type_select.jsp" />
            <dsp:input bean="GiftRegistryFormHandler.SuccessURL" type="hidden" value="${contextPath}/giftregistry/simpleReg_creation_form.jsp" /> --%>
            <dsp:input bean="GiftRegistryFormHandler.fromPage" type="hidden" 
					value="bridalLoggedNoReg"/>
            <dsp:input id="submitClick4" bean="GiftRegistryFormHandler.registryTypes" type="submit" value="submit" style="display:none;"></dsp:input>
        
    </dsp:form>
	<script type="text/javascript">
    $('#submitClick4').attr('disabled', 'disabled');
    function updateFormEnabled() {
    if ($('#typeofregselect4').val() == ' ') {
      $('#submitClick4').prop('disabled', 'disabled');
    } else {
       $('#submitClick4').removeAttr('disabled');
    }
    }
    $('#typeofregselect4').change(updateFormEnabled);
    </script>
</dsp:page>