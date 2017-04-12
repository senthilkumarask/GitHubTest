<dsp:page>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<div title='<bbbl:label key="lbl_fb_auto_login_register_title" language ="${pageContext.request.locale.language}"/>'>
	
		<p><bbbl:label key="lbl_fb_auto_login_register" language ="${pageContext.request.locale.language}"/></p>
		<dsp:form method="post" action="${contextPath}/account/myaccount.jsp" id="frmRedirectToMyAcnt">
			<div class="formRow clearfix">
				<div class="button fl marRight_10 textCenter">
					<dsp:input bean="ProfileFormHandler.fbLogin" type="submit" value="OK" name="btnExtend">
                        <dsp:tagAttribute name="aria-pressed" value="false"/>
                        <dsp:tagAttribute name="role" value="button"/>
                    </dsp:input>
				</div>
			</div>
		</dsp:form>
		
	</div>
</dsp:page>
	
