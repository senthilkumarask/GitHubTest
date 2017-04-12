<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean var="ProfileFormHandler" bean="/atg/userprofiling/ProfileFormHandler"/>

	<bbb:pageContainer index="false" follow="false">

		<jsp:attribute name="bodyClass">my-account</jsp:attribute>
		<jsp:attribute name="section">accounts</jsp:attribute>
		<jsp:attribute name="pageWrapper">personalForms myAccount</jsp:attribute>

		<jsp:body>

			<div class="row" id="content">
				<div class="small-12 columns">
					<h1><bbbl:label key="lbl_personalinfo_myaccount" language ="${pageContext.request.locale.language}"/>: <span class="subheader"><bbbl:label key="lbl_updateprofile_personalinformation" language ="${pageContext.request.locale.language}"/></span></h1>
				</div>
				<div class="show-for-medium-down small-12">
					<a class="right-off-canvas-toggle secondary expand button">Account Menu</a>
				</div>
				<div class="large-3 columns small-medium-right-off-canvas-menu left-nav">
					<c:import url="/account/left_nav.jsp">
						<c:param name="currentPage"><bbbl:label key="lbl_myaccount_personal_info" language ="${pageContext.request.locale.language}"/></c:param>
					</c:import>
				</div>
				<div class="small-12 large-9 columns">
					<c:choose>
						<c:when test="${ProfileFormHandler.changePasswordSuccessMessage == true}" >
							<p><bbbt:textArea key="txtarea_changepassword_successmessage" language ="${pageContext.request.locale.language}"/></p>
							<dsp:setvalue bean="ProfileFormHandler.changePasswordSuccessMessage" value="false"/>
						</c:when>
					</c:choose>

					<dsp:include page="/account/updateprofile.jsp"></dsp:include>
					<dsp:include page="/account/changepassword.jsp"></dsp:include>
				</div>
			</div>

			<script type="text/javascript">
			BBB.accountManagement = true;
				$('#updatePass').on('submit', function(){
					if (typeof acctUpdate === "function") {
						acctUpdate('password updated');
					}
				});
				$('#personalInfo').on('submit', function(){
					if (typeof acctUpdate === "function") {
						acctUpdate('profile updated');
					}
				});
			</script>

		</jsp:body>

		<jsp:attribute name="footerContent">
			<script type="text/javascript">
				if (typeof s !== 'undefined') {
					s.pageName = 'My Account>Personal Info';
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
