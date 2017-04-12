<%@ page import="atg.servlet.ServletUtil" %>
<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean"/>
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
	<dsp:getvalueof var="email" bean="SessionBean.userEmailId"/>
	<div title="<bbbl:label key="lbl_account_creation" language ="${pageContext.request.locale.language}"/>">
		<jsp:useBean id="placeHolderExtendAccount" class="java.util.HashMap" scope="request"/>
		<c:set target="${placeHolderExtendAccount}" property="sisterSiteName">${sisterSiteName}</c:set>
		<c:set target="${placeHolderExtendAccount}" property="currentSiteName">${currentSiteName}</c:set>
		<c:set target="${placeHolderExtendAccount}" property="emailAddress"><dsp:valueof value="${email}"/></c:set>
		<bbbt:textArea key="txt_extend_account_message" language="${pageContext.request.locale.language}" placeHolderMap="${placeHolderExtendAccount}"/>
		<dsp:form id="frmExtendAccount" action="${contextPath}/account/frags/extend_profile_password_check.jsp" method="post">
			<dsp:input bean="ProfileFormHandler.value.login" type="hidden" value="${email}"/>
			<dsp:input bean="ProfileFormHandler.assoSite" type="hidden" beanvalue="Site.id"/>
			<div class="input clearfix">
				<div class="label posRel">
					<label id="lblpassword" for="password"><bbbl:label key="lbl_spc_login_frag__password" language="${pageContext.request.locale.language}" /> <span class="required">*</span></label>
                    <div class="showPassDiv clearfix">
                        <input name="showPassword" type="checkbox" value="" data-toggle-class="showpassLoginExtension" class="showPassword" id="showPasswordLoginEx" aria-labelledby="lblshowPasswordLoginEx" />
                        <label id="lblshowPasswordLoginEx" for="showPasswordLoginEx" class="lblShowPassword"><bbbl:label key="lbl_show_password" language="${pageContext.request.locale.language}"/></label>
                        <div class="clear"></div> 
                    </div>
				</div>
				<div class="text width_3">
					<dsp:input id="password" bean="ProfileFormHandler.value.password" name="password" value="" type="password" autocomplete="off" iclass="showpassLoginExtension">
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblpassword errorpassword"/>
                    </dsp:input>
					<label id="errorpassword" class="error"><dsp:valueof bean="ProfileFormHandler.errorMap.loginPasswordError"/></label>
				</div>
				<div>		
					<dsp:input type="checkbox" bean="ProfileFormHandler.extendEmailOptn" id="optIn_extend" name="emailOptIn_extend" checked="false" iclass="fl">
                    <dsp:tagAttribute name="aria-checked" value="false"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
                    </dsp:input>
                    <label id="lbl_optIn_extend" for="optIn_extend" class="textDgray11 wrapTextAfterCheckBox"><bbbl:label key="lbl_email_optin_for_extend_account" language="${pageContext.request.locale.language}"/></label>
                </div>	
			</div>
			<%--  Added to fix  BBB-93 | DSK- Adding additional access points for the extend account modal --%>
			<dsp:getvalueof var="fromPage" param="fromPage"/>
			<dsp:getvalueof var="queryParam" param="queryParam"/>
			<dsp:setvalue bean="SessionBean.fromPage" value="${fromPage}" />
			<dsp:setvalue bean="SessionBean.extendModal" value="true" />
			<dsp:setvalue bean="SessionBean.queryParam" value="${queryParam}"/>
			<div class="formRow clearfix">
				<div class="button fl marRight_10">					
					<dsp:input bean="ProfileFormHandler.extendedlogin" name="btnExtend" type="submit" value="EXTEND ACCOUNT">
                        <dsp:tagAttribute name="aria-pressed" value="false"/>
                        <dsp:tagAttribute name="role" value="button"/>
                    </dsp:input>
				</div>
				<div class="button">				
					<input type="button" id="btnCancel" value="Cancel" role="link" aria-labelledby="btnCancel"/>				
				</div>
			</div>
			
		</dsp:form>
	</div>
</dsp:page>