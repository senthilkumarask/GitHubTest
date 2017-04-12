<dsp:page>
  	<c:set var="pageWrapper" value="trackOrder useBazaarVoice" scope="request" />
	<bbb:pageContainer section="accounts" bodyClass="">
    <jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>
    <jsp:body>
	<%-- Removed the content from this jsp and placed in new jsp named legacy_track_order_detail.jsp --%>
		<dsp:include page="legacy_track_order_detail_frag.jsp"></dsp:include>
	</jsp:body>
  </bbb:pageContainer>
</dsp:page>