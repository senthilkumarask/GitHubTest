<dsp:page>
<c:set var="BedBathUSSite">
		<bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BuyBuyBabySite">
		<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BedBathCanadaSite">
		<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<dsp:getvalueof var="emid" param="emid"/>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="referer" bean="/OriginatingRequest.referer"/>
	<c:set var="urlParam" value="${param.writeReview}"/>
	<div id="newCustomer" class="grid_4 alpha fbConnectWrap">
	    <h3><bbbl:label key="lbl_createaccountmain_main_header" language="${pageContext.request.locale.language}"/></h3>
	    <h4><bbbl:label key="lbl_createaccount_main_header" language="${pageContext.request.locale.language}"/></h4>
	    <dsp:form id="newAccount" action="${contextPath}/account/frags/create_user_json.jsp" method="post" iclass="clearfix">		    
		    <div class="formRow clearfix">
				<div class="input grid_3 alpha noMarBot" aria-live="assertive">
					<div class="label">
						<label id="lblNewEmail" class="textLgray12 block" for="newEmail"><bbbl:label key="lbl_profilemain_email" language="${pageContext.request.locale.language}"/><span class="visuallyhidden"><bbbl:label key="lbl_for_new_customers" language="${pageContext.request.locale.language}"/></span></label>
					</div>	
					<div class="text noMarBot" aria-live="assertive">
						<dsp:input bean="ProfileFormHandler.value.email" type="text" id="newEmail" name="email" iclass="input_large211 block fbConnectEmail" value="${emid}">
						
						<dsp:tagAttribute name="aria-describedby" value="lblNewEmail errornewEmail"/>
						</dsp:input>
					</div>					
				</div>
			</div>
			<c:set var="lbl_createaccount_button">
				<bbbl:label key='lbl_createaccount_main_header' language='${pageContext.request.locale.language}' />
			</c:set>
			<div id="createAccountButton" class="button button_active button_active_orange button_disabled">
			   <c:if test="${urlParam == 'true'}">
					  <input type="hidden" name="writeReview" id="writeReview" value="${urlParam}"/>
					  <dsp:getvalueof var="sUrl" bean="ProfileFormHandler.writeReviewSuccessURL" />
					  <c:choose>
						    <c:when test="${empty sUrl}">
						      <c:choose>
							    <c:when test="${fn:contains(referer, '?') }">
							      <c:set var="delimiter" value="&"/>
							    </c:when>
							    <c:otherwise>
							       <c:set var="delimiter" value="?"/>
							    </c:otherwise>
						  	  </c:choose>
						  	  <c:set var="sUrl" value="${referer}${delimiter}writeReview=true" />
						  	  <dsp:input bean="ProfileFormHandler.writeReviewSuccessURL" type="hidden" value="${sUrl}"/>
					    </c:when>					    
					    <c:otherwise>
					    	<dsp:input bean="ProfileFormHandler.writeReviewSuccessURL" type="hidden" beanvalue="ProfileFormHandler.writeReviewSuccessURL"/>
					    </c:otherwise>
					  </c:choose>		    
		       </c:if>				
					   
	           <dsp:input bean="ProfileFormHandler.createUserFragRegister" id="newEmailBtn" type="submit" value="${lbl_createaccount_button}" disabled="true" iclass="enableOnDOMReady"/>				
	        </div>
	        <dsp:input bean="ProfileFormHandler.createUserFragRegister" value="" type="hidden"></dsp:input>
	        
	        <%-- Client DOM XSRF | Part -1
	        dsp:input bean="ProfileFormHandler.extenstionSuccessURL" type="hidden" value="${contextPath}/account/frags/create_user_json.jsp?email="/>			
			<dsp:input bean="ProfileFormHandler.legacyPasswordPopupURL" type="hidden" value="${contextPath}/account/frags/legacy_password_popup.jsp"/>
	        <dsp:input bean="ProfileFormHandler.migratedUserPopupURL" type="hidden" value="${contextPath}/account/frags/migrated_user_popup_json.jsp"/> --%>
	  	</dsp:form>
	  	<c:if test="${FBOn}">	
	  	<p class="marTop_20 marBottom_20"><bbbl:label key="lbl_OR" language="${pageContext.request.locale.language}" /></p>
		<div class="renderFBConnect" data-fb-connect-section="loginRegistration">
			<img src="/_assets/global/images/fb-loader.gif" alt="Facebook User" class="fbConnectLoader" />
		</div>
		</c:if>
	</div>
</dsp:page>					



