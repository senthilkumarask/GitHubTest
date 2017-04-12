<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>
<head><title>TBS IDM login</title></head>
<body>
<dsp:form action="idm_login.jsp" method="post">
	<div class="row">
		<div class="small-12 columns">
			<li> Authorization Failed </li>
		</div><br/><br/>
		<div class="small-6 columns">
			<a class="small button service expand secondary" href="/tbs/account/Login">Retry</a>
		</div>
		<div class="small-6 columns">
			&nbsp;
		</div>
	</div>
</dsp:form>
</body>
</dsp:page>