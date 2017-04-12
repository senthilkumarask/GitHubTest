<%@ taglib uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"
	prefix="dsp"%>
<dsp:page>
<dsp:importbean bean="atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="atg/userprofiling/Profile"/>
<dsp:form method="post" action="create_account.jsp">
<h1><bbbl:label key="lbl_reg_login_page" language="${pageContext.request.locale.language}" /></h1>
<table border='1'>
<tr><td>
<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
	<dsp:oparam name="output">
		<li><dsp:valueof param="message"/></li>
	</dsp:oparam>
</dsp:droplet>

</td></tr>
<tr><td>
<ul>
  	<li>
		<label id="lblregisterLogin" for="registerLogin"><bbbl:label key="lbl_login_email" language="${pageContext.request.locale.language}" />:<span>*</span></label>
		<dsp:input bean="ProfileFormHandler.value.email" type="text" id="registerLogin" >
            <dsp:tagAttribute name="aria-required" value="false"/>
            <dsp:tagAttribute name="aria-labelledby" value="lblregisterLogin errorregisterLogin"/>
        </dsp:input>
	</li>
	<br/>
		<dsp:input bean="ProfileFormHandler.loginRegister" type="submit" value="submit">
            <dsp:tagAttribute name="aria-pressed" value="false"/>
            <dsp:tagAttribute name="role" value="button"/>
        </dsp:input>
</ul>
</td></tr></table>
</dsp:form>
</dsp:page>