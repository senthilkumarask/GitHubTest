<dsp:page>

	<%-- Imports --%>
	<dsp:importbean var="ProfileFormHandler" bean="/atg/userprofiling/ProfileFormHandler"/>

	<dsp:form method="post" iclass="clearfix" id="updatePass" action="personalinfo.jsp">
	<dsp:input bean="ProfileFormHandler.fromPage" type="hidden" value="tbsChangePassword" />
		<!--<dsp:input bean="ProfileFormHandler.changePasswordSuccessURL" type="hidden" value="personalinfo.jsp"/>
		<dsp:input bean="ProfileFormHandler.changePasswordErrorURL" type="hidden" value="personalinfo.jsp"/>-->
		<div class="row">
			<div class="small-12 columns">
				<h3><bbbl:label key="lbl_personalinfo_updatepassword" language ="${pageContext.request.locale.language}"/></h3>
			</div>
			<div class="small-12 columns">
				<c:set var="placeholder"><bbbl:label key="lbl_profilemain_email" language ="${pageContext.request.locale.language}"/></c:set>
				<dsp:input bean="ProfileFormHandler.value.email" type="hidden" iclass="hidden" name="pwdSaveFixEmail" />
			</div>
			<div class="small-12 large-6 columns">
				<%--  <span>
					<label class="inline-rc checkbox lblShowPassword" id="lblshowPasswordOld" for="showPasswordOld">
						<input name="showPassword" type="checkbox" value="" data-toggle-class="showpassChangePasswordOld" class="showPassword" id="showPasswordOld" aria-labelledby="lblshowPasswordOld" />
						<span></span>
						<bbbl:label key="lbl_show_password" language="${pageContext.request.locale.language}"/>
					</label>
				</span>
				--%>				
				<span>
					<c:set var="placeholder"><bbbl:label key="lbl_personalinfo_currentpassword" language ="${pageContext.request.locale.language}"/></c:set>
					<dsp:input bean="ProfileFormHandler.value.oldpassword" maskcharacter="*"  id="cpassword" name="currentpassword" value="" type="password" autocomplete="off" iclass="showpassChangePasswordOld">
						<dsp:tagAttribute name="placeholder" value="${placeholder} *"/>
						<dsp:tagAttribute name="aria-required" value="true"/>
						<dsp:tagAttribute name="aria-labelledby" value="lblcPassword errorcpassword"/>
					</dsp:input>
				</span>
				
			</div>
			<div class="small-12 large-6 columns">
				<div class="row">
					<div class="small-12 columns">
						<%--  <span>
							<label class="inline-rc checkbox lblShowPassword" for="showPassword">
								<input name="showPassword" type="checkbox" value="" data-toggle-class="showpassChangePassword" class="showPassword" id="showPassword" />
								<span></span>
								<bbbl:label key="lbl_show_password" language="${pageContext.request.locale.language}"/>
							</label>
						</span>--%>
						<span>
							<c:set var="placeholder"><bbbl:label key="lbl_personalinfo_newpassword" language ="${pageContext.request.locale.language}"/></c:set>
							<dsp:input bean="ProfileFormHandler.value.password" id="password" maskcharacter="*" name="password" value="" type="password" autocomplete="off" iclass="showpassChangePassword">
								<dsp:tagAttribute name="placeholder" value="${placeholder} *"/>
								<dsp:tagAttribute name="aria-required" value="true"/>
								<dsp:tagAttribute name="aria-labelledby" value="lblpassword errorpassword"/>
							</dsp:input>
						<span>
					</div>
					<div class="small-12 columns">
						<c:set var="placeholder"><bbbl:label key="lbl_personalinfo_confirmnewpassword" language ="${pageContext.request.locale.language}"/></c:set>
						<dsp:input bean="ProfileFormHandler.value.confirmpassword" type="password" maskcharacter="*" autocomplete="off" name="confirmPassword" id="confirmPassword" value="" iclass="showpassChangePassword">
							<dsp:tagAttribute name="placeholder" value="${placeholder} *"/>
							<dsp:tagAttribute name="aria-required" value="true"/>
							<dsp:tagAttribute name="aria-labelledby" value="lblconfirmPassword errorconfirmPassword"/>
						</dsp:input>
					</div>
				</div>
			</div>
			<div class="small-12 columns">
				<c:set var="submitKey">
					<bbbl:label key='lbl_personalinfo_update' language='${pageContext.request.locale.language}' />
				</c:set>
				<dsp:input bean="ProfileFormHandler.value.firstName" name="fname"  id="firstName" type="hidden"/>
				<dsp:input bean="ProfileFormHandler.value.lastName" name="lname" id="lastName"  type="hidden"/>
				<dsp:input iclass="small button primary" bean="ProfileFormHandler.changePassword" id="updatePassBtn" type="submit" value="${submitKey}">
					<dsp:tagAttribute name="aria-pressed" value="false"/>
					<dsp:tagAttribute name="aria-labelledby" value="updatePassBtn"/>
				</dsp:input>
				<dsp:input bean="ProfileFormHandler.changePassword" type="hidden" value="submit"/>
			</div>
		</div>
	</dsp:form>

</dsp:page>
