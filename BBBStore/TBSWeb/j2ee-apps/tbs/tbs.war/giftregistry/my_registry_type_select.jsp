<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/multisite/Site" />

	<%-- Variables --%>
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>

	<dsp:form action="${contextPath}/giftregistry/registry_creation_form.jsp">
		<div class="row">
			<div class="small-12 columns">
				<dsp:droplet name="ErrorMessageForEach">
					<dsp:param bean="GiftRegistryFormHandler.formExceptions" name="exceptions"/>
					<dsp:oparam name="output">
						<dsp:valueof param="message"/><br>
					</dsp:oparam>
				</dsp:droplet>
			</div>
			<div class="small-12 columns">
				<dsp:select bean="GiftRegistryFormHandler.registryEventType" iclass="create-registry-trigger">
					<dsp:tagAttribute name="data-submit-button" value="submitClick4"/>
					<dsp:tagAttribute name="aria-required" value="true"/>
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
				</dsp:select>
			</div>
			<div class="small-12 columns">
				<%-- <dsp:input bean="GiftRegistryFormHandler.ErrorURL" type="hidden" value="${contextPath}/giftregistry/registry_type_select.jsp" />
				<dsp:input bean="GiftRegistryFormHandler.SuccessURL" type="hidden" value="${contextPath}/giftregistry/registry_creation_form.jsp" /> --%>
				        <dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="myRegistryTypeSelect" />
				<dsp:input id="submitClick4" bean="GiftRegistryFormHandler.registryTypes" type="submit" iclass="hidden" value="" />
			</div>
		</div>
	</dsp:form>
</dsp:page>
