<dsp:page>
<dsp:importbean bean="/com/bbb/cms/droplet/ChallengeQuestionDroplet" />
<dsp:importbean var="ProfileFormHandler" bean="/atg/userprofiling/ProfileFormHandler" />
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<div class="grid_5 alpha marTop_20 marBottom_10">
	<h2 class="noMar">Challenge Question</h2>
</div>
<dsp:form method="post" iclass="clearfix" name="challengequestion" id="challengequestion">
<dsp:droplet name="ChallengeQuestionDroplet">
<dsp:param name="fromLoginPage" value="fromLoginPage"/>
<dsp:oparam name="output">

<dsp:getvalueof param="PreferredQuestion1" var="PreferredQuestion1" />
<dsp:getvalueof param="PreferredQuestion2" var="PreferredQuestion2" />
<dsp:getvalueof param="ChallengeQuestionsMap1" var="ChallengeQuestionsMap1" />
<dsp:getvalueof param="ChallengeQuestionsMap2" var="ChallengeQuestionsMap2" />
<dsp:getvalueof param="PreferredAnswer1" var="PreferredAnswer1" />
<dsp:getvalueof param="PreferredAnswer2" var="PreferredAnswer2" />
<c:set var="your_answer_here"><bbbl:label key="lbl_type_your_answer" language="${pageContext.request.locale.language}"/></c:set>
	<div class="input grid_3 alpha suffix_2">
		<label class="label" for="challengeQuestion1">	<bbbl:label key="lbl_Question" language="${pageContext.request.locale.language}" /> 1	</label>
		<div class="select">
		
			<dsp:select bean="ProfileFormHandler.editValue.challengeQuestion1" name="challengeQuestion1" id="challengeQuestion1" iclass="selector challengeQuestion">
				<dsp:option value="">Please select First Question</dsp:option>
					<c:forEach items="${ChallengeQuestionsMap1}" var="entry">
					<c:if test="${entry.key ne PreferredQuestion1}">
					<dsp:option value="${entry.key}" >${entry.value}</dsp:option>
					</c:if>
					<c:if test="${entry.key eq PreferredQuestion1}">
					<dsp:option value="${entry.key}" selected="true">${entry.value}</dsp:option>
					</c:if>
					
		            
		            </c:forEach>
		            </dsp:select>

		</div>
		<label class="label" for="challengeAnswer1">	<bbbl:label key="lbl_Answer" language="${pageContext.request.locale.language}" /> 1 </label>
		<div class="text">
			<dsp:input type="password" iclass="challengeAns" name="challengeAnswer1" id="challengeAnswer1"  bean="ProfileFormHandler.editValue.challengeAnswer1" value="${PreferredAnswer1}" autocomplete="off" >
			<dsp:tagAttribute name="placeholder" value="${your_answer_here}"/>
			</dsp:input>
		</div>
	</div>
	<div class="input grid_3 alpha suffix_2">
			<label class="label" for="challengeQuestion2">	<bbbl:label key="lbl_Question" language="${pageContext.request.locale.language}" /> 2	</label>
			<div class="select">
				<dsp:select bean="ProfileFormHandler.editValue.challengeQuestion2" name="challengeQuestion2" id="challengeQuestion2" iclass="selector challengeQuestion">
					<dsp:option value="">Please select Second Question</dsp:option>
					<c:forEach items="${ChallengeQuestionsMap2}" var="entry">
					<c:if test="${entry.key ne PreferredQuestion2}">
					<dsp:option value="${entry.key}" >${entry.value}</dsp:option>
					</c:if>
					<c:if test="${entry.key eq PreferredQuestion2}">
					<dsp:option value="${entry.key}" selected="true">${entry.value}</dsp:option>
					</c:if>
					
		            
		            </c:forEach>
		            </dsp:select>
			</div>
			<label class="label" for="challengeAnswer2">	<bbbl:label key="lbl_Answer" language="${pageContext.request.locale.language}" /> 2 </label>
			<div class="text">
			<dsp:input type="password" iclass="challengeAns" name="challengeAnswer2" id="challengeAnswer2"  bean="ProfileFormHandler.editValue.challengeAnswer2"  value="${PreferredAnswer2}" autocomplete="off">
			<dsp:tagAttribute name="placeholder" value="${your_answer_here}"/>
			</dsp:input>
			</div>
	</div>
</dsp:oparam>
</dsp:droplet>
	<div class="button button_active button_active_orange">
		<dsp:input type="submit" bean="ProfileFormHandler.challengeQuestion" value="save">
		<dsp:tagAttribute name="aria-pressed" value="false"/>
		   <dsp:tagAttribute name="aria-labelledby" value="updatePassBtn"/>
		   <dsp:tagAttribute name="role" value="button"/>
		</dsp:input>      
    </div>     
           
                            
                        
</dsp:form>
	
</dsp:page>
