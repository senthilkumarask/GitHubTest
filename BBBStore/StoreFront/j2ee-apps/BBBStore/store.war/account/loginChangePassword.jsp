<dsp:page>
<dsp:importbean bean="atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="atg/userprofiling/Profile"/>

<bbb:pageContainer index="false" follow="false">

<dsp:form method="post" action="changepassword.jsp">
<table border='1'>
<tr><td>
<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
	<dsp:oparam name="output">
		<dsp:valueof param="message"/>
	</dsp:oparam>
</dsp:droplet>

</td></tr>
<tr><td>
<ul>
  	<li>
		<label id="lblchangePasssword" for="changePasssword">User<span>*</span></label>
		<dsp:input bean="ProfileFormHandler.value.login" type="text"  value="test" id="changePasssword" >
            <dsp:tagAttribute name="aria-required" value="true"/>
            <dsp:tagAttribute name="aria-labelledby" value="lblchangePasssword errorchangePasssword"/>
        </dsp:input>
	</li>
	<li>
        <div class="formRow marBottom_5">
            <input name="showPassword" type="checkbox" value="" data-toggle-class="showpassLoginChangePass" class="showPassword" id="showPassword" />
            <label for="showPassword" class="textDgray11 bold"><bbbl:label key="lbl_show_password" language="${pageContext.request.locale.language}"/></label>
        </div>
		<label id="lblpassword" for="password"><bbbl:label key="lbl_reg_password" language="${pageContext.request.locale.language}" /><span>*</span></label>
		<dsp:input bean="ProfileFormHandler.value.password" type="password" id="password" autocomplete="off">
            <dsp:tagAttribute name="aria-required" value="true"/>
            <dsp:tagAttribute name="aria-labelledby" value="lblpassword errorpassword"/>
        </dsp:input>
	</li>
	
	<br/>
		<dsp:input bean="ProfileFormHandler.changePwdlogin" type="submit" value="submit">
            <dsp:tagAttribute name="aria-pressed" value="false"/>
        </dsp:input>
</ul>
</td></tr></table>
</dsp:form>
</bbb:pageContainer>
</dsp:page>