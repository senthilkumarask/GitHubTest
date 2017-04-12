<dsp:page>
	<dsp:importbean var="BridalBookFormHandler"
		bean="/com/bbb/selfservice/BridalBookFormHandler" />
		<dsp:getvalueof bean="BridalBookFormHandler.successMessage" var="exists"/>
	<json:object>		
		<json:property name="exists" value="${exists}"/>				
	</json:object> 
</dsp:page>