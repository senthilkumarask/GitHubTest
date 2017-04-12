<dsp:page>
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/ValidateRecommenderDroplet" />
	<dsp:importbean	bean="/com/bbb/profile/session/SessionBean" />
	<dsp:getvalueof bean="SessionBean.values" var="sessionMapValues"/>

	<dsp:droplet name="ValidateRecommenderDroplet">
		<dsp:param name="registryId" value="${sessionMapValues.recommendedRegistry}" />
		<dsp:param name="fromURI" value="VRR" /> <!-- From Gift Viewer/ View Registry Recommender -->
		<dsp:oparam name="output">

		</dsp:oparam>
		<dsp:oparam name="error">
			<dsp:getvalueof param="errormsg" var="errormsg" />
		</dsp:oparam>
	</dsp:droplet>
	<dsp:include page="/giftregistry/view_registry_guest.jsp">
		<dsp:param name="invalidateTokenError" value="${errormsg}"  />
		<dsp:param name="fromGiftGiver" value="true"  />
	</dsp:include>

</dsp:page>