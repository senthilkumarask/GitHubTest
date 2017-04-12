<dsp:page>
<dsp:importbean var="ContactUsFormHandler" bean="/com/bbb/selfservice/ContactUsFormHandler"/>


Name:  <dsp:valueof bean="ContactUsFormHandler.firstName"/>&nbsp;<dsp:valueof bean="ContactUsFormHandler.lastName"/> 
<br>Preferred Contact Method: <dsp:valueof bean="ContactUsFormHandler.contactType"/>
<br>Phone Number: <dsp:valueof bean="ContactUsFormHandler.phoneNumber"/>
<br>Phone Ext: <dsp:valueof bean="ContactUsFormHandler.phoneExt"/>
<br>Best Time to Call: <dsp:valueof bean="ContactUsFormHandler.selectedTimeCall"/><dsp:valueof bean="ContactUsFormHandler.amPM"/>&nbsp;<dsp:valueof bean="ContactUsFormHandler.selectedTimeZone"/>
<br>Customer Email Address: <dsp:valueof bean="ContactUsFormHandler.email"/>
<br>Message: <dsp:valueof bean="ContactUsFormHandler.emailMessage"/>
<br>Gender: <dsp:valueof bean="ContactUsFormHandler.gender"/>
</dsp:page>