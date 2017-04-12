<dsp:page>
	<dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />		
	<dsp:getvalueof var="contextPath"
		bean="/OriginatingRequest.contextPath" />
	<dsp:form>
		<dsp:droplet name="ErrorMessageForEach">
			<dsp:param bean="GiftRegistryFormHandler.formExceptions"
				name="exceptions" />
			<dsp:oparam name="output">
				<dsp:valueof param="message" />
				<br>
			</dsp:oparam>
		</dsp:droplet>
		<div class="grid_6 infoContent">
			<div id="heroInfo" class="grid_3 suffix_3 alpha">
				<div class="guests">
					<p>
						<span><bbbl:label key="lbl_regcreate_for" language="${pageContext.request.locale.language}" /></span>&nbsp;<bbbl:label key="lbl_regcreate_registrants" language="${pageContext.request.locale.language}" />
					</p>
				</div>
				<h2><bbbl:label key="lbl_regcreate_create_registry" language="${pageContext.request.locale.language}" /></h2>
				<div class="grid_2 alpha clearfix">
					<div id="selectRegistryType" class='marTop_20 marBottom_20'>
						<label id="selectRegistry"><bbbl:label key="lbl_select_registry_type" language ="${pageContext.request.locale.language}"/></label>
							<dsp:select bean="GiftRegistryFormHandler.registryEventType"
								iclass="selector_primary selectRegToCreate" id="typeofregselect3">
								<dsp:tagAttribute name="data-submit-button" value="submitClick3" />
								<dsp:droplet name="GiftRegistryTypesDroplet">
									<dsp:param name="siteId" value="${currentSiteId}"/>								
									<dsp:oparam name="output">
										<dsp:option value="" selected="selected">
											<bbbl:label key="lbl_regcreate_select_type"
												language="${pageContext.request.locale.language}" />
										</dsp:option>
										<dsp:droplet name="ForEach">
											<dsp:param name="array" param="registryTypes" />
											<dsp:oparam name="output">
												<dsp:param name="regTypes" param="element" />
												<dsp:getvalueof var="regTypesId"
													param="regTypes.registryCode" />
												<dsp:option value="${regTypesId}">
													<li><a href="#"><dsp:valueof
																param="element.registryName"></dsp:valueof> </a>
													</li>
												</dsp:option>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
                                <dsp:tagAttribute name="aria-required" value="false"/>
                                <dsp:tagAttribute name="aria-label" value="select the registry you would like to create. You will be redirected to the begin the creation process"/>
                                <dsp:tagAttribute name="aria-hidden" value="false"/>
							</dsp:select>
					</div>
				</div>
				<%-- Client DOM XSRF | Part -1 
				<dsp:input bean="GiftRegistryFormHandler.ErrorURL" type="hidden"
					value="${contextPath}/giftregistry/registry_type_select.jsp" />
				<dsp:input bean="GiftRegistryFormHandler.SuccessURL" type="hidden"
					value="${contextPath}/giftregistry/simpleReg_creation_form.jsp" /> --%>
					<dsp:input bean="GiftRegistryFormHandler.fromPage" type="hidden"
					value="bridalLoggedNoReg" />
				<dsp:input id="submitClick3"
					bean="GiftRegistryFormHandler.registryTypes" type="submit" value="submit"
					style="display:none;"></dsp:input>
				
				<%--
				<h1><bbbl:label key="lbl_regcreate_manage_registry" language="${pageContext.request.locale.language}" /></h1>
				<p><bbbl:label key="lbl_regcreate_already_registered" language="${pageContext.request.locale.language}" /></p>
				<p>
					<dsp:a page="/account/login.jsp"><bbbl:label key="lbl_regcreate_login_view_reg" language="${pageContext.request.locale.language}" /></dsp:a>				
				</p>
				--%>
				
			</div>

			<div id="heroProd">
				<img src="${imagePath}/_assets/bbregistry/images/prod.png"
					alt="" />
			</div>
		</div>
	</dsp:form>
       <script type="text/javascript">
       $('#submitClick3').attr('disabled', 'disabled');
       function updateFormEnabled() {
      if ($('#typeofregselect3').val() == ' ') {
      $('#submitClick3').prop('disabled', 'disabled');
      } else {
      $('#submitClick3').removeAttr('disabled');
      }
     }
     $('#typeofregselect3').change(updateFormEnabled);
    </script>
	<dsp:include page="find_registry.jsp" />
</dsp:page>