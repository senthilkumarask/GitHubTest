<dsp:page>

	<dsp:getvalueof param="spcParam" var="spcParam"/>

	<%-- code to manually override the useSPC value using a URL param --%>
	<c:choose>
		<c:when test="${spcParam and (spcParam == 'true' || spcParam == 'false')}">
		</c:when>
		<c:otherwise>
			<c:set var="spcParam" value="false" /> 
		</c:otherwise>
	</c:choose>


	{"useSPC":${spcParam}}
</dsp:page>