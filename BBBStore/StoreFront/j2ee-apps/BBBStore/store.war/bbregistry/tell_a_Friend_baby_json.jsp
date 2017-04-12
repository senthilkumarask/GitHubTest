<dsp:page>
	<dsp:importbean var="BabyBookFormHandler"
		bean="/com/bbb/selfservice/BabyBookFormHandler" />
		<dsp:getvalueof bean="BabyBookFormHandler.successMessage" var="exists"/>
	<json:object>		
		<json:property name="exists" value="${exists}"/>				
	</json:object> 
</dsp:page>