
<dsp:page>
<!--  BPSI-147- Scheduler to send bulk monthly email Starts-->
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />

	<dsp:getvalueof var="serverName" param="serverName" />
	<dsp:getvalueof var="contextPath"
		bean="/OriginatingRequest.contextPath" />


	<c:set var="serverPath" value="https://${serverName}" />
	<c:set var="imagePath">
https:<bbbc:config key="image_host" configName="ThirdPartyURLs" />
	</c:set>
	<c:set var="scene7Path">
https:<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>


	<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
	<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<title><dsp:valueof param="emailTemplateVO.siteId" /> Email Registrant
	Scheduled Email</title>
</head>
<body bgcolor="#ffffff" leftmargin="0" topmargin="0" marginwidth="0"
	marginheight="0">


	<dsp:valueof param="emailTemplateVO.emailHeader" valueishtml="true" />
	<%-- Main Content --%>

	<%-- // Begin Template Main Content \\ --%>


   <tr>
				<td align="left" valign="top">
					<%-- // Begin Template Body \\ --%>
					<table border="0" cellpadding="0" cellspacing="0" width="100%"
						style="color: #666666; font-family: Arial;">
						<tr>

							<td align="left" valign="top">
							 <table border="0" cellpadding="0" cellspacing="0" width="96%" align="center">
							 <td valign="middle" height="50"><h3 class="resetTextSize" style="color:#666666;font-family:Arial;padding:0;margin:0;font-family:arial;">

								<dsp:getvalueof var="recommendersDetail"  param="recommendersDetail" />
								<dsp:getvalueof var="registFirstName"  param="recommendersDetail.registFirstName" />
								<dsp:getvalueof var="registLastName"  param="recommendersDetail.registLastName" />
								<dsp:getvalueof var="recomCount"  param="recommendersDetail.recomCount" />

                 Hi, ${registFirstName} ${registLastName}, you have ${recomCount} new recommendations.


								</h3></td>
								</table>
								</td>
						</tr>

						<tr>
							<td valign="top" height="15">&nbsp;</td>
						</tr>
					</table></td>
			</tr>



	<%-- // End Template Main Content \\ --%>
	<%-- // Begin Template Footer \\ --%>

	<dsp:valueof param="emailTemplateVO.emailFooter" valueishtml="true" />


 </body>
</html>

</dsp:page>
