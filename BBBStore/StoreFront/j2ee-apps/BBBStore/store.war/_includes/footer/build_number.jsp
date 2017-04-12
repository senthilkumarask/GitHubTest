<dsp:page>
<dsp:importbean bean="com/bbb/utils/BBBLogBuildNumber"/>
	<dsp:droplet name="BBBLogBuildNumber">
		<dsp:oparam name="output">
		<dsp:getvalueof var="buildNum" param="BUILD_NUM"/>
		<dsp:getvalueof var="buildFrom" param="BUILD_FROM"/>
		<dsp:getvalueof var="buildDate" param="BUILD_DATE"/>
		<span class="hidden">${buildNum}${buildFrom}${TagId}${buildDate}</span>
		</dsp:oparam>	
	</dsp:droplet>
</dsp:page>