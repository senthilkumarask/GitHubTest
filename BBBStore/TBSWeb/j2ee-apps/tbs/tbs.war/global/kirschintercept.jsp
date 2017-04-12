<dsp:page>
<dsp:importbean bean="/com/bbb/account/droplet/BBBSetCookieDroplet"/>
<dsp:importbean bean="/com/bbb/account/droplet/BBBEncryptionDroplet"/>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="/atg/userprofiling/Profile.transient"/>
  <dsp:oparam name="true">
    <%-- not logged in --%>
  </dsp:oparam>
  <dsp:oparam name="false">
  		<dsp:droplet name="BBBEncryptionDroplet">
			<dsp:param name="inputvalue" bean="/atg/userprofiling/Profile.id" />
			<dsp:param name="operation" value="encrypt" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="encryptedProfileId" param="outputvalue"/>
			</dsp:oparam>
  		</dsp:droplet>
    	<jsp:useBean id="cookies" class="java.util.HashMap" scope="request"/>
		<c:set target="${cookies}" property="profileId">${encryptedProfileId}</c:set>
		<dsp:setvalue bean="BBBSetCookieDroplet.cookies" value="${cookies}" />
			<dsp:droplet name="BBBSetCookieDroplet">
				<dsp:oparam name="output">
					<c:redirect url="${param.kirschRedirectUrl}"/>
				</dsp:oparam>
			</dsp:droplet>
			
  </dsp:oparam>
</dsp:droplet>
</dsp:page>
