<dsp:page>
<dsp:importbean bean="/com/bbb/cms/droplet/ChallengeQuestionDroplet" />
<dsp:importbean var="ProfileFormHandler" bean="/atg/userprofiling/ProfileFormHandler" />
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/atg/userprofiling/ForgotPasswordHandler"/>
<c:set var="pageName" value="${param.pageName}"/>

<div>
<dsp:form name="challengeQuestionIntercepted" id="challengeQuestionIntercepted" action="/store/account/frags/challengeQuestionModal.jsp">
	<dsp:include page="/global/gadgets/errorMessage.jsp">
 		<dsp:param name="formhandler" bean="ForgotPasswordHandler"/>
    </dsp:include>
	<div class="pageErrors"></div>
   		<dsp:getvalueof var="email" value="${fn:toLowerCase(forgetEmail)}"/>
		<dsp:droplet name="ChallengeQuestionDroplet">
		<dsp:param name="challengeQuestionEmailID" value="${email}"/>
		<dsp:oparam name="output">
		<dsp:getvalueof param="PreferredQuestion1" var="PreferredQuestion1" />
		<dsp:getvalueof param="PreferredQuestion2" var="PreferredQuestion2" />
		
		<div class="input formRow clearfix marTop_10">
		<div class="width_3 fl label noMar">
			<label id="lblemail" for="challengeQuestionModal1"><bbbl:label key="lbl_quest1" language="${pageContext.request.locale.language}" /></label>
		</div>
		<div class="width_3 fl text noMar">
			<dsp:input type="text" name="challengeQuestionModal1"  id="challengeQuestionModal1"  bean="ProfileFormHandler.editValue.challengeQuestion1" value="${PreferredQuestion1}">
			<dsp:tagAttribute name="readonly" value="true" />
			</dsp:input>
		</div>
		</div>
		<div class="input formRow clearfix">
		<div class="width_3 fl label noMar">
			<label id="lblemail" for="challengeAnswerModal1"><bbbl:label key="lbl_answer1" language="${pageContext.request.locale.language}" /></label>
		</div>
		<div class="width_3 fl text noMar">
			<dsp:input type="text" name="challengeAnswerModal1" id="challengeAnswerModal1" bean="ForgotPasswordHandler.challengeAnswer1" value="" autocomplete="off">
			<dsp:tagAttribute name="placeholder" value="type your answer"/>
			</dsp:input>
		</div>
		</div>
		<div class="input formRow clearfix">
		<div class="width_3 fl label noMar">
			<label id="lblemail" for="challengeQuestionModal2"><bbbl:label key="lbl_question2" language="${pageContext.request.locale.language}" /></label>
		</div>
		<div class="width_3 fl text noMar">
			<dsp:input type="text" name="challengeQuestionModal2" id="challengeQuestionModal2" bean="ProfileFormHandler.editValue.challengeQuestion2" value="${PreferredQuestion2}">
			<dsp:tagAttribute name="readonly" value="true" />
			</dsp:input>
		</div>
		</div>
		<div class="input formRow clearfix">
		<div class="width_3 fl label noMar">
			<label id="lblemail" for="challengeAnswerModal2"><bbbl:label key="lbl_answer2" language="${pageContext.request.locale.language}" /></label>
		</div>
		<div class="width_3 fl text noMar">
			<dsp:input type="text" name="challengeAnswerModal2" id="challengeAnswerModal2" bean="ForgotPasswordHandler.challengeAnswer2" value="" autocomplete="off" >
			<dsp:tagAttribute name="placeholder" value="type your answer"/>
			</dsp:input>
		</div>
		</div>
		</dsp:oparam>
		</dsp:droplet>
		

	<div class="formRow fl noMarBot clearfix width_2 marRight_20 marTop_5">
		<dsp:input type="hidden" bean="ForgotPasswordHandler.emailProvided" value="${param.forgetEmail}"/>
		<dsp:input type="hidden" iclass="blueButton button-Med saveChallengeQues" bean="ForgotPasswordHandler.validateChallengeQuestion" value="save answers">  
		<input type="submit" class="blueButton button-Med saveChallengeQues" value="save answers"> 
		<dsp:tagAttribute name="role" value="button" />
		</dsp:input>		
	</div>
		</dsp:form>
<dsp:form iclass="" name="challengeQuestionskip" id="challengeQuestionskip" action="" >
		<dsp:input bean="ForgotPasswordHandler.forgotPasswordErrorURL" type="hidden" value="/store/account/frags/forgot_password_json.jsp"/>
		<dsp:input bean="ForgotPasswordHandler.forgotPasswordSuccessURL" type="hidden" value="/store/account/frags/forgot_password_json.jsp"/>
		<dsp:input bean="ForgotPasswordHandler.forgotPassword" type="hidden" value="Skip questions"/>
	<div>
		<input type="submit" class="submitLink" value="Skip questions"/>
		<%-- <dsp:input type="submit" iclass="submitLink" bean="" value="Skip questions">
			<dsp:tagAttribute name="aria-pressed" value="false" />
			<dsp:tagAttribute name="role" value="button" />
		</dsp:input> --%>
	</div> 
	<a href="#" title="Cancel" class="close-any-dialog buttonTextLink hidden" role="link"><bbbl:label key="lbl_profile_Cancel" language ="${pageContext.request.locale.language}"/></a>
</dsp:form>
</div>

<c:set var="omniPageName" value="My Account"/>
<c:if test="${pageName == 'spcPage' || pageName=='multiShip'}">
	<c:set var="omniPageName" value="Checkout"/>
</c:if>

<script type="text/javascript">
		
	function answerChallengeQuestionLoadOmniture() {
	if (typeof s !== "undefined") {
			s.prop1 = s.prop2 = s.prop3 = s.prop4 = s.prop5 = s.prop6 =s.prop7 = s.prop8 = s.prop25 ='';
			s.eVar1 = s.eVar2 = s.eVar3 = s.eVar4 = s.eVar5 = s.eVar6 =s.eVar7 = s.eVar8 = s.eVar47 ='';
			s.pageName  = '${omniPageName}>Security Questions';
			s.channel = s.prop1 = s.prop2 = s.prop3 = "${omniPageName}";
			s.prop4 = s.prop5 = "";
			s.events ='';
			s.products ='';

			s.t();
			s.linkTrackVars="None";
			s.linkTrackEvents="None";

		}	
	}
	
	answerChallengeQuestionLoadOmniture();
</script>	
</dsp:page>
