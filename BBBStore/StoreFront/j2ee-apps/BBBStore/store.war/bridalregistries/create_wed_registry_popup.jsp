<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />

	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
			
	<div title="Create Registry">
		<dsp:form name="createRegisDialogForm" id="createRegisDialogForm">
			<p><bbbl:label key="lbl_create_bridal_registry" language ="${pageContext.request.locale.language}"/></p>
			<div class="width_2">
			
			<dsp:select
				bean="GiftRegistryFormHandler.registryEventType"
				id="registeryType"  name="registeryType" iclass="uniform">
                <dsp:tagAttribute name="aria-required" value="false"/>
                <dsp:tagAttribute name="aria-label" value="Select the registry you would like to create"/>
				<dsp:droplet name="GiftRegistryTypesDroplet">
					<dsp:param name="siteId" value="${currentSiteId}"/>
					<dsp:oparam name="output">
						<dsp:option value="" selected="selected"><bbbl:label key="lbl_regsearch_select_type" language ="${pageContext.request.locale.language}"/></dsp:option>
						<dsp:droplet name="ForEach">
							<dsp:param name="array" param="registryTypes" />
							<dsp:oparam name="output">
								<dsp:param name="regTypes" param="element" />
								<dsp:getvalueof var="regTypesId"
									param="regTypes.registryName" />
									<dsp:getvalueof var="registryCode"
									param="regTypes.registryCode" />
									<c:if test="${registryCode eq 'BRD' || registryCode eq 'COM' }">
										<dsp:option value="${registryCode}">
											<dsp:valueof param="element.registryName"></dsp:valueof>
										</dsp:option>
									</c:if>
							</dsp:oparam>
						</dsp:droplet>
					</dsp:oparam>
				</dsp:droplet>

			</dsp:select>
			</div>
			<div class="marTop_5">
				<label id="errorregisteryType" for="registeryType" generated="true" class="error"><bbbe:error key="err_add_required_fields" language="${pageContext.request.locale.language}"/></label>
				<label id="lblregisteryType" class="offScreen"><bbbl:label key="lbl_create_bridal_registry" language ="${pageContext.request.locale.language}"/></label>
			</div>
			<bbbt:textArea key="txt_createreg_bridal_promos" language ="${pageContext.request.locale.language}"/>
			<div class="marTop_20 buttonpane clearfix">
			<div class="ui-dialog-buttonset">
				<div class="fl button button_active">
				 <c:set var="submitBtn"><bbbl:label key='lbl_create' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
				<dsp:input id="submitClick"
						bean="GiftRegistryFormHandler.registryTypes" type="submit" name="createRegisteryBtn"
						value="${submitBtn}">
                    <dsp:tagAttribute name="aria-pressed" value="false"/>
                    <dsp:tagAttribute name="aria-labelledby" value="submitClick"/>
                    <dsp:tagAttribute name="role" value="button"/>
                </dsp:input>
					
				</div>
				<c:set var="cancelBtn"><bbbl:label key='lbl_cancel' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
                <a href="#" title="${cancelBtn}" role="link" class="close-any-dialog buttonTextLink">${cancelBtn}</a>
  				<dsp:input bean="GiftRegistryFormHandler.fromPage" type="hidden" value="registryTypes" />
                                     <%-- Client DOM XSRF | Part -1
                                     <dsp:input bean="GiftRegistryFormHandler.SuccessURL" type="hidden" value="/store/giftregistry/simpleReg_creation_form.jsp" /> --%>
                                     
		</div>
			</div>
		</dsp:form>
	</div>



</dsp:page>