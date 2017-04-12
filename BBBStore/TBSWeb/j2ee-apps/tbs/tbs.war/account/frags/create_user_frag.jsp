<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	
	<c:set var="TBS_BedBathUSSite">
		<bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BuyBuyBabySite">
		<bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BedBathCanadaSite">
		<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<dsp:getvalueof var="emid" param="emid"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	
		<div class="small-12 large-6 columns">
			<div class="row">
				<div class="large-offset-1 large-9 columns">
					<h2 class="divider"><bbbl:label key="lbl_createaccountmain_main_header" language="${pageContext.request.locale.language}"/></h2>
					<h3><bbbl:label key="lbl_createaccount_main_header" language="${pageContext.request.locale.language}"/></h3>
					<dsp:form id="newAccount" action="${contextPath}/account/frags/create_user_json.jsp" method="post" iclass="clearfix">
					<div class="row">
						
							<div class="small-12 columns">
								<c:set var="emailPlaceholder">
									<bbbl:label key="lbl_profilemain_email" language="${pageContext.request.locale.language}"/>
								</c:set>
								<dsp:input tabindex="1" bean="ProfileFormHandler.value.email" type="text" id="newEmail" name="email" iclass="fbConnectEmail" value="${emid}">
									<dsp:tagAttribute name="aria-required" value="true"/>
									<dsp:tagAttribute name="aria-labelledby" value="lblNewEmail errornewEmail"/>
									<dsp:tagAttribute name="placeholder" value="${emailPlaceholder}"/>
								</dsp:input>
							</div>
							<div class="small-12 large-6 columns">
								
								<c:set var="lbl_createaccount_button">
									<bbbl:label key='lbl_createaccount_main_header' language='${pageContext.request.locale.language}' />
								</c:set>
								
								<dsp:input bean="ProfileFormHandler.createUserFragRegister" id="newEmailBtn" type="submit" value="${lbl_createaccount_button}" disabled="true" iclass="enableOnDOMReady small button service expand">
									<dsp:tagAttribute name="aria-pressed" value="false"/>
								</dsp:input>
									
							    <dsp:input bean="ProfileFormHandler.createUserFragRegister" value="" type="hidden"></dsp:input>
							    <!--<dsp:input bean="ProfileFormHandler.extenstionSuccessURL" type="hidden" value="${contextPath}/account/frags/create_user_json.jsp?email="/>			
								<dsp:input bean="ProfileFormHandler.legacyPasswordPopupURL" type="hidden" value="${contextPath}/account/frags/legacy_password_popup.jsp"/>
							    <dsp:input bean="ProfileFormHandler.migratedUserPopupURL" type="hidden" value="${contextPath}/account/frags/migrated_user_popup_json.jsp"/>-->
			
							</div>
						
		  			</div>
		  			</dsp:form>
				</div>
			</div>
		</div>
	
</dsp:page>					



