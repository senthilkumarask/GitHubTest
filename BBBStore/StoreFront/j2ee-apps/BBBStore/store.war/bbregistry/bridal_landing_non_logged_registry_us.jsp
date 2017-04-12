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
			
		<dsp:include page="find_registry.jsp" />
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
				<!-- <h2><label for="createReg" class="createRegLabel"> <bbbl:label key="lbl_regcreate_create_registry" language="${pageContext.request.locale.language}" /></label></h2> -->
				<h3><label for="createReg" class="createRegLabel"> create<span>a registry</span></label></h3>
				<p><bbbl:label key="lbl_regsearch_havent_created" language="${pageContext.request.locale.language}" /></p>
				<div class=''>
					<c:set var="selRegLabel"><bbbl:label key="lbl_select_registry_type" language ="${pageContext.request.locale.language}"/></c:set>
					
					<dsp:select bean="GiftRegistryFormHandler.registryEventType"
						iclass="selector_primaryAlt selectRegToCreate">
						<dsp:tagAttribute name="data-submit-button" value="submitClick2" />
                        <dsp:tagAttribute name="aria-required" value="false"/>
                        <dsp:tagAttribute name="aria-hidden" value="false"/>
                        <dsp:tagAttribute name="id" value="createReg"/>                      
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
					</dsp:select>
				</div>
				<p class="infoContentHeader"><bbbt:textArea key="txt_why_question" language="${pageContext.request.locale.language}" /></p>
				<a class="newOrPopup" title="" href="/store/static/registryreasonsiframe" ><img alt="Bed Bath &amp; Beyond Video" src="//s7d9.scene7.com/is/image/BedBathandBeyond/registry%5Fredesign%5Fvideo?$other$"></a>
				<p class="infoContentPar"><bbbt:textArea key="txt_make_planning_better" language="${pageContext.request.locale.language}" /> <a title="Learn More" href="/store/registry/RegistryFeatures" ><bbbt:textArea key="lbl_learn_more" language="${pageContext.request.locale.language}" /></a></p>
		       
			</div>
			<div id="heroProd">
		<img src="${imagePath}/_assets/bbregistry/images/prod.png"
			alt="" />
		</div>
		</div>
		
		<%-- Client DOM XSRF | Part -1 
		<dsp:input bean="GiftRegistryFormHandler.ErrorURL" type="hidden"
			value="${contextPath}/giftregistry/registry_type_select.jsp" />
		<dsp:input bean="GiftRegistryFormHandler.SuccessURL" type="hidden"
			value="${contextPath}/giftregistry/simpleReg_creation_form.jsp" /> --%>
			 <dsp:input bean="GiftRegistryFormHandler.fromPage" type="hidden" value="bridalLoggedNoReg" />
		<dsp:input id="submitClick2"
			bean="GiftRegistryFormHandler.registryTypes" type="submit" value=""
			style="display:none;"></dsp:input>
	</dsp:form>
       <dsp:form>
	
    	<div class="grid_6 bookAppointment">
			<h3><bbbt:textArea key="txt_book_an appointment" language="${pageContext.request.locale.language}" /></h3>
			<p class="bookP1"><bbbt:textArea key="txt_stress_free_registry" language="${pageContext.request.locale.language}" /></p>
			<hr>
			<p><bbbt:textArea key="txt_schedule_instore_consultation" language="${pageContext.request.locale.language}" /></p>
			<div class="scheduleAppointmentCall" data-param-appointmentcode="REG" data-param-storeid="" data-param-registryid="" data-param-coregfn="" data-param-coregln="" data-param-eventdate="" id="regSectBookAppt">
				<link rel="stylesheet" type="text/css" property="stylesheet" href="/_assets/global/css/contact_store.min.css?v=20160829140551ce1458dDC1">
				<input type="hidden" value="REG" id="defaultAppointmentCode">								
				<input type="hidden" value="false" id="errorOnModal">
				
				<div class="button button_secondary">
					<input type="button" name="btnFindInStore" value="Book an Appointment" role="button" id="scheduleAppointmentBtn">
				</div>
			</div>
		</div>
    </dsp:form>	
	<style>
		h3.modalTitle {text-indent: 100%; white-space: nowrap; overflow: hidden; height: 0 !important;}
	</style>

	<script type="text/javascript">
    $('#submitClick2').attr('disabled', 'disabled');
    function updateFormEnabled() {
    if ($('#createReg').val() == ' ') {
      $('#submitClick2').prop('disabled', 'disabled');
    } else {
       $('#submitClick2').removeAttr('disabled');
    }
    }
    $('#createReg').change(updateFormEnabled);
    	</script>
</dsp:page>
