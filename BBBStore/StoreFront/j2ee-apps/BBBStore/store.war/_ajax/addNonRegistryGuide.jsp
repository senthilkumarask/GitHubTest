<dsp:page>
	<dsp:importbean bean="/com/bbb/integration/interactive/InteractiveChecklistFormHandler"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<%-- <dsp:form action="" method="post" iclass="addRegistryGuideClass" id="addNonRegistryGuideForm">
		<dsp:input type="hidden" name="addRegistryGuide" value="${guideId}" bean="InteractiveChecklistFormHandler.guideId" />
		<dsp:input type="submit" value="Add this Guide" name="addRegistryGuide${guideId}" id="addRegistryGuideId${guideId}"
			bean="InteractiveChecklistFormHandler.addRegistryGuide"> <dsp:tagAttribute name="role" value="button" />
		</dsp:input>
	</dsp:form> --%>
	<dsp:getvalueof var="guideType" param="guideType"/>
	<dsp:form formid="addNonRegistryGuideForm" name="addNonRegistryGuideForm" method="post">
		<dsp:setvalue bean="InteractiveChecklistFormHandler.addOrHideGuideType" value="${guideType}" />
		<dsp:setvalue bean="InteractiveChecklistFormHandler.addOrHideShoppingGuide" value="true" />
	</dsp:form>
</dsp:page>