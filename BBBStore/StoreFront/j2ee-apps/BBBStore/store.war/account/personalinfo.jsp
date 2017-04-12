<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean var="ProfileFormHandler" bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/com/bbb/cms/droplet/ChallengeQuestionExistDroplet" />
<c:set var="challengeQuestionON"><bbbc:config key="challenge_question_flag" configName="FlagDrivenFunctions" /></c:set>



<bbb:pageContainer index="false" follow="false">
    <jsp:attribute name="bodyClass">atg_store_pageHome</jsp:attribute>
    <jsp:attribute name="section">accounts</jsp:attribute>
    <jsp:attribute name="pageWrapper">personalForms myAccount</jsp:attribute>
    <jsp:body>


    <div id="content" class="container_12 clearfix" role="main">


    		<div class="grid_12">
				<h1 class="account fl"><bbbl:label key="lbl_personalinfo_myaccount" language ="${pageContext.request.locale.language}"/></h1>
				<h3 class="subtitle"><bbbl:label key="lbl_updateprofile_personalinformation" language ="${pageContext.request.locale.language}"/></h3>
					<div class="clear"></div>
			</div>

			<div class="grid_2">
				<c:import url="/account/left_nav.jsp">
				 <c:param name="currentPage"><bbbl:label key="lbl_myaccount_personal_info" language ="${pageContext.request.locale.language}"/></c:param>
				</c:import>
			</div>


        <div class="grid_5 prefix_1 suffix_2">
                <c:choose>
            	 <c:when test="${ProfileFormHandler.changePasswordSuccessMessage == true}" >
             		 	<p class="bold welcomeMsg noMarTop"><bbbt:textArea key="txtarea_changepassword_successmessage" language ="${pageContext.request.locale.language}"/></p>
             		 	<dsp:setvalue bean="ProfileFormHandler.changePasswordSuccessMessage" value="false"/>
             	</c:when>
             	<c:when test="${ProfileFormHandler.challengeQuestionUpdated == true}" >
             		 	<p class="bold welcomeMsg noMarTop"><bbbl:label key="challenge_question_success_message" language ="${pageContext.request.locale.language}"/></p>
             	</c:when>
             	
             </c:choose>

                <dsp:include page="/account/updateprofile.jsp"></dsp:include>
                <c:if test="${challengeQuestionON}">
				<dsp:include page="/account/challengeQuestionSetup.jsp"></dsp:include>
				</c:if>


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