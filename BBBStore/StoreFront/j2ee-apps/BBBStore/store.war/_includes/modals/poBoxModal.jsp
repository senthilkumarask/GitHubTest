<c:set var="registryId" value="${param.registryId}"></c:set>
<c:set var="eventType" value="${param.eventType}"></c:set>
    <p class="poBoxMsg" tabindex="1"><bbbl:label key="lbl_PoBox_Popup" language="${pageContext.request.locale.language}" /></p>
    
    <dsp:a href="/store/giftregistry/view_registry_owner.jsp" iclass="button-Med btnRegistryPrimary" id="updatePOBox" title="Update Address">
	<dsp:param name="registryId" value="${registryId}"/>
	<dsp:param name="eventType" value="${eventType}"/>
	<dsp:param name="updateRegistryModal" value="true"/>
	<bbbl:label key="lbl_update_address_heading" language="${pageContext.request.locale.language}" /></dsp:a>
