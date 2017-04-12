<dsp:page>
	
	<dsp:importbean bean="/atg/dynamo/service/IdGenerator"/>
	<dsp:importbean bean="/com/bbb/account/BBBStoreSessionDroplet"/>
	
	<%-- Variables --%>
	<c:set var="section" value="accounts" scope="request" />
	<c:set var="pageWrapper" value="wishlist myAccount hhPreferences" scope="request" />

	<bbb:pageContainer index="false" follow="false">

		<jsp:attribute name="bodyClass">my-account preferences</jsp:attribute>
		<jsp:attribute name="section">${section}</jsp:attribute>
		<jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>

		<jsp:body>

			<div class="row" id="content">
				<div class="small-12 columns">
					<h1><bbbl:label key="lbl_prefrences_myaccount" language ="${pageContext.request.locale.language}"/>: <span class="subheader"><bbbl:label key="lbl_myaccount_preferences" language ="${pageContext.request.locale.language}"/></span></h1>
				</div>
				<div class="show-for-medium-down small-12">
					<a class="right-off-canvas-toggle secondary expand button">Account Menu</a>
				</div>
				<div class="large-3 columns small-medium-right-off-canvas-menu left-nav">
					<c:import url="/account/left_nav.jsp">
						<c:param name="currentPage"><bbbl:label key="lbl_myaccount_preferences" language ="${pageContext.request.locale.language}"/></c:param>
					</c:import>
				</div>

				<dsp:getvalueof var="dataCenter" bean="IdGenerator.dcPrefix"/>
				<c:set var="dc">1</c:set>
				<c:choose>
					<c:when test="${dataCenter=='DC1'}">
						<c:set var="dc">1</c:set>
					</c:when>
					<c:when test="${dataCenter=='DC2'}">
						<c:set var="dc">2</c:set>
					</c:when>
					<c:when test="${dataCenter=='STG1'}">
						<c:set var="dc">s1</c:set>
					</c:when>
					<c:when test="${dataCenter=='STG2'}">
						<c:set var="dc">s2</c:set>
					</c:when>
				</c:choose>

				<div class="small-12 large-9 columns">
					<c:if test="${HarteHanksOn}">
						<c:choose>
							<c:when test="${currentSiteId eq 'TBS_BedBathUS'}">
								<c:set var="currentSiteId">BedBathUS</c:set>
							</c:when>
							<c:when test="${currentSiteId eq 'TBS_BuyBuyBaby'}">
								<c:set var="currentSiteId">BuyBuyBaby</c:set>
							</c:when>
							<c:when test="${currentSiteId eq 'TBS_BedBathCanada'}">
								<c:set var="currentSiteId">BedBathCanada</c:set>
							</c:when>
						</c:choose>
						<dsp:droplet name="BBBStoreSessionDroplet">
							<dsp:param name="currSite" value="${currentSiteId}"/>
							<dsp:oparam name="success">
								<dsp:getvalueof id="URL" param="URL"/>
								<iframe type="some_value_to_prevent_js_error_on_ie7" id="hhIframe" title='<bbbl:label key="lbl_myaccount_preferences" language ="${pageContext.request.locale.language}"/>' src='${URL}&dc=${dc}' width="1000" height="1450" scrolling="no" frameBorder="0" class="noOverflow"></iframe>
							</dsp:oparam>
							<dsp:oparam name="fail">
								<bbbl:label key="lbl_prefrences_info" language ="${pageContext.request.locale.language}"/>
							</dsp:oparam>
						</dsp:droplet>
					</c:if>
				</div>
			</div>

		</jsp:body>

		<jsp:attribute name="footerContent">
			<script type="text/javascript">
				if (typeof s !== 'undefined') {
					s.pageName = 'My Account>Preferences';
					s.channel = 'My Account';
					s.prop1='My Account';
					s.prop2='My Account';
					s.prop3='My Account';
					s.prop6='${pageContext.request.serverName}';
					s.eVar9='${pageContext.request.serverName}';
					var s_code = s.t();
					if (s_code)
						document.write(s_code);
				}
			</script>
		</jsp:attribute>

	</bbb:pageContainer>

</dsp:page>
