<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/multisite/Site"/>

	<%-- Variables --%>
	<dsp:param name="currentSiteId" bean="Site.id"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<c:set var="BedBathUSSite" scope="request">
		<bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BuyBuyBabySite" scope="request">
		<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BedBathCanadaSite" scope="request">
		<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:choose>
		<c:when test="${currentSiteId==BedBathUSSite}">
			<c:set var="currentSiteName" scope="request">
				<bbbl:label key="lbl_sitename_bedbathus" language ="${pageContext.request.locale.language}"/>
			</c:set>
			<c:set var="sisterSiteName" scope="request">
				<bbbl:label key="lbl_sitename_buybuybaby" language ="${pageContext.request.locale.language}"/>
			</c:set>
		</c:when>
		<c:when test="${currentSiteId==BuyBuyBabySite}">
			<c:set var="currentSiteName" scope="request">
				<bbbl:label key="lbl_sitename_buybuybaby" language ="${pageContext.request.locale.language}"/>
			</c:set>
			<c:set var="sisterSiteName" scope="request">
				<bbbl:label key="lbl_sitename_bedbathus" language ="${pageContext.request.locale.language}"/>
			</c:set>
		</c:when>
	</c:choose>
	<jsp:useBean id="placeHolderExtendAccount" class="java.util.HashMap" scope="request"/>
	<c:set target="${placeHolderExtendAccount}" property="sisterSiteName">${sisterSiteName}</c:set>
	<c:set target="${placeHolderExtendAccount}" property="currentSiteName">${currentSiteName}</c:set>
	<c:set target="${placeHolderExtendAccount}" property="emailAddress"><dsp:valueof value="${email}"/></c:set>

	<div class="row">
		<div class="small-12 columns">
			<h2><bbbl:label key="lbl_account_creation" language="${pageContext.request.locale.language}"/></h2>
			<bbbt:textArea key="txt_extend_account_message" language="${pageContext.request.locale.language}" placeHolderMap="${placeHolderExtendAccount}"/>
		</div>
	</div>
	<div class="row">
		<dsp:form id="frmExtendAccount" action="${contextPath}/account/frags/extend_profile_password_check.jsp" method="post">

			<dsp:input bean="ProfileFormHandler.value.login" type="hidden" value="${email}"/>
			<dsp:input bean="ProfileFormHandler.assoSite" type="hidden" beanvalue="Site.id"/>

			<%-- errors --%>
			<div class="small-12 columns">
				<div class="pageErrors"></div>
			</div>

			<%-- show password --%>
			<div class="small-12 columns">
				<label class="inline-rc checkbox lblShowPassword" for="showPasswordLoginEx">
					<input name="showPassword" type="checkbox" value="" data-toggle-class="showpassLoginExtension" class="showPassword" id="showPasswordLoginEx" />
					<span></span>
					<bbbl:label key="lbl_show_password" language="${pageContext.request.locale.language}"/>
				</label>
			</div>

			<%-- password --%>
			<div class="small-12 columns">
				<c:set var="placeholder"><bbbl:label key="lbl_checkoutconfirmation_password" language="${language}"/></c:set>
				<dsp:input bean="ProfileFormHandler.value.password" id="password" name="password" value="" type="password" autocomplete="off" iclass="showpassLoginExtension" >
					<dsp:tagAttribute name="placeholder" value="${placeholder}"/>
					<dsp:tagAttribute name="aria-required" value="true"/>
					<dsp:tagAttribute name="aria-labelledby" value="lblpassword errorpassword"/>
				</dsp:input>
			</div>

			<div class="small-12 columns">
				<dsp:input bean="ProfileFormHandler.extendedlogin" id="btnExtend" name="btnExtend" type="submit" value="EXTEND ACCOUNT" iclass="button small service">
					<dsp:tagAttribute name="aria-pressed" value="false"/>
					<dsp:tagAttribute name="role" value="button"/>
				</dsp:input>
				<a title="Cancel" class="button small secondary close-modal" href="#">Cancel</a>
			</div>

		</dsp:form>
	</div>
	<a class="close-reveal-modal">&#215;</a>

</dsp:page>
