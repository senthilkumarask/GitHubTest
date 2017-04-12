<dsp:page>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<title>
	<dsp:valueof param="emailTemplateVO.siteId" />
</title>
</head>
<body bgcolor="#ffffff" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<!-- If you see this message, please enable HTML e-mail -->
<style type=text/css>
.ReadMsgBody {width: 100%;}
.ExternalClass {width: 100%;}
</style>

<dsp:include page="/emailtemplates/common/generic_email_css.jsp" >
	<dsp:param name="siteId" param="emailTemplateVO.siteId"/>
	<dsp:param name="emailType" param="emailTemplateVO.emailType"/>
</dsp:include>


<table align="left" border="0" cellpadding="0" cellspacing="0" width="600">

	<dsp:valueof param="emailHeader" valueishtml="true"/>
	
	<%-- Main Content Starts--%>
	
	<dsp:valueof param="emailBody" valueishtml="true"/>
		
	<%-- Main Content Ends--%>	
	<dsp:valueof param="emailFooter" valueishtml="true"/>	

</table>
<span style="padding: 0px;"></span>
</body>
</html>				
</dsp:page>