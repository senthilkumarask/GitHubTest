<dsp:page>

	<%-- Imports --%>
	<dsp:importbean var="ProfileFormHandler" bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:importbean var="Profile" bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/multisite/Site"/>

	<dsp:form  id="personalInfo" iclass="" action="personalinfo.jsp" method="post">
		<div class="row">
			<div class="small-12 columns">
				<dsp:include page="/global/gadgets/errorMessage.jsp">
					<dsp:param name="formhandler" bean="ProfileFormHandler"/>
				</dsp:include>
				<c:if test="${ProfileFormHandler.successMessage== true}">
					<p class="p-secondary"><bbbt:textArea key="txtarea_updateprofile_successmessage" language="${pageContext.request.locale.language}"/></p>
					<dsp:setvalue bean="ProfileFormHandler.successMessage" value="false"/>
				</c:if>
			</div>
			<div class="small-12 large-6 columns">
				<c:set var="placeholder"><bbbl:label key="lbl_profile_firstname" language="${pageContext.request.locale.language}"/></c:set>
				<dsp:input bean="ProfileFormHandler.value.firstName" name="firstName" type="text" id="fname">
					<dsp:tagAttribute name="placeholder" value="${placeholder} *"/>
					<dsp:tagAttribute name="aria-required" value="true"/>
					<dsp:tagAttribute name="aria-labelledby" value="lblfname errorfname"/>
				</dsp:input>

			</div>
			<div class="small-12 large-6 columns">
				<c:set var="placeholder"><bbbl:label key="lbl_profile_lastname" language="${pageContext.request.locale.language}"/></c:set>
				<dsp:input bean="ProfileFormHandler.value.lastName" name="lastName" id="lname" type="text">
					<dsp:tagAttribute name="placeholder" value="${placeholder} *"/>
					<dsp:tagAttribute name="aria-required" value="true"/>
					<dsp:tagAttribute name="aria-labelledby" value="lbllastName errorlname"/>
				</dsp:input>
			</div>
			<div class="small-12 large-6 columns">
				<c:set var="placeholder"><bbbl:label key="lbl_profilemain_email" language="${pageContext.request.locale.language}"/></c:set>
				<dsp:input bean="ProfileFormHandler.value.email" name="email" type="text" id="email">
					<dsp:tagAttribute name="placeholder" value="${placeholder} *"/>
					<dsp:tagAttribute name="aria-required" value="true"/>
					<dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
				</dsp:input>
				<c:set var="flag" value="false"/>
				<dsp:getvalueof var="siteId" bean="Site.id"/>
				<dsp:getvalueof id="emailFlag" bean="Profile.userSiteItems.${siteId}.emailoptin"/>
				<c:if test="${emailFlag== 1}">
					<c:set var="flag" value="true"/>
				</c:if>
				<dsp:input type="hidden" bean="ProfileFormHandler.emailOptIn" id="optIn" name="Opt-In" value="${flag}"></dsp:input>
			</div>
			<div class="small-12 large-6 columns">
				<c:set var="placeholder"><bbbl:label key="lbl_profile_primaryphone" language="${pageContext.request.locale.language}"/></c:set>
				<dsp:input type="tel" bean="ProfileFormHandler.value.phoneNumber" name="basePhoneFull" id="basePhoneFull" iclass="phone phoneField">
					<dsp:tagAttribute name="placeholder" value="${placeholder}"/>
					<dsp:tagAttribute name="maxlength" value="10"/>
					<dsp:tagAttribute name="aria-pressed" value="false"/>
					<dsp:tagAttribute name="aria-labelledby" value="lblbasePhoneFull errorbasePhoneFull"/>
				</dsp:input>
			</div>
			<div class="small-12 large-offset-6 large-6 columns">
				<c:set var="placeholder"><bbbl:label key="lbl_profile_mobilephone" language="${pageContext.request.locale.language}"/></c:set>
				<dsp:input type="tel" bean="ProfileFormHandler.value.mobileNumber" name="cellPhoneFull" id="cellPhoneFull" iclass="phone phoneField">
					<dsp:tagAttribute name="placeholder" value="${placeholder}"/>
					<dsp:tagAttribute name="maxlength" value="10"/>
					<dsp:tagAttribute name="aria-pressed" value="false"/>
					<dsp:tagAttribute name="aria-labelledby" value="lblcellPhoneFull errorcellPhoneFull"/>
				</dsp:input>
				<label id="errorcellPhoneFull" for="cellPhoneFull" class="error" generated="true"></label>
			</div>
			<div class="small-12 columns">
				<c:set var="submitKey">
					<bbbl:label key='lbl_updateprofile_save' language='${pageContext.request.locale.language}' />
				</c:set>
				<dsp:input iclass="small button primary" bean="ProfileFormHandler.update"  id="personalInfoBtn" type="Submit" value="${submitKey}">
					<dsp:tagAttribute name="aria-pressed" value="false"/>
					<dsp:tagAttribute name="aria-labelledby" value="personalInfoBtn"/>
				</dsp:input>
			</div>
		</div>
	</dsp:form>

</dsp:page>
