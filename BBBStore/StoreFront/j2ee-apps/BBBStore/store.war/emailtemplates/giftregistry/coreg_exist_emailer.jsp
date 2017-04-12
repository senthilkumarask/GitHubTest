<dsp:page>
<dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>


<dsp:getvalueof var="serverName" bean="OriginatingRequest.serverName" />
<dsp:getvalueof var="serverPort" bean="OriginatingRequest.serverPort" />
<dsp:getvalueof var="contextPath" bean="OriginatingRequest.contextPath" />
<c:set var="serverPath" value="https://${serverName}:${serverPort}"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-2" />
<title>Email BBB Gift Registry</title>
</head>
<body>
<div style="width:810px;"> 	
<table width="100%" border="0" cellspacing="0" cellpadding="0">
       <tr>
        <td>One or more registry linked with your profile by the registrant</td>
        
    </tr>
    
    <tr>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td colspan="4" style="padding:3px; font-size:10px; color:#FFFFFF; background:#273691; text-align:center;">&copy;1999-2011 Bed Bath &amp; Beyond Inc. and its subsidiaries. All rights reserved.</td>
        </tr>
</table>


</div>
</body>
</html>
</dsp:page>    