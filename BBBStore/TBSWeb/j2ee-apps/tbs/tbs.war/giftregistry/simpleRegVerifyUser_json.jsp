<%@page contentType="application/json"%>
<dsp:page>
	<dsp:droplet
		name="/com/bbb/simplifyRegistry/droplet/SimpleRegVerifyPrimaryUserDroplet">
		<dsp:param name="email" param="email" />
		<dsp:oparam name="output">
		<dsp:getvalueof var="profileStatus" param="profileStatus"/>
			<json:object escapeXml="false">
				<json:property name="success" value="${true}"/>
				<json:property name="profileStatus">${profileStatus}</json:property>
				</json:object>
		</dsp:oparam>
		<dsp:oparam name="error">
			Error occurred while validating email.
		</dsp:oparam>
		
	</dsp:droplet>

</dsp:page>