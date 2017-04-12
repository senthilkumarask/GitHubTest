<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean var="ProfileFormHandler" bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean var="Profile" bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/multisite/Site"/>





        <dsp:form  id="personalInfo" iclass="clearfix" action="personalinfo.jsp" method="post">

				    <dsp:include page="/global/gadgets/errorMessage.jsp">
				      <dsp:param name="formhandler" bean="ProfileFormHandler"/>
				    </dsp:include>


            <c:choose>
            	 <c:when test="${ProfileFormHandler.successMessage == true}" >
             		 <p class="bold welcomeMsg noMarTop"><bbbt:textArea key="txtarea_updateprofile_successmessage" language ="${pageContext.request.locale.language}"/></p>
             		 <dsp:setvalue bean="ProfileFormHandler.successMessage" value="false"/>
             	</c:when>
            </c:choose>
        	 <div class="input grid_3 alpha suffix_2">
        	 	<div class="label">
        	 		<label id="lblemail" for="email"><bbbl:label key="lbl_profilemain_email" language ="${pageContext.request.locale.language}"/> <span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
              	</div>
                 <div class="text">
                      <dsp:input bean="ProfileFormHandler.value.email" name="email" type="text" id="email" >
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
                      </dsp:input>
                 </div>
              </div>
	          			<c:set var="flag" value="false"/>
						<dsp:getvalueof var="siteId" bean="Site.id"/>
						<dsp:getvalueof id="emailFlag" bean="Profile.userSiteItems.${siteId}.emailoptin"/>
						<c:if test="${emailFlag == 1}">
							<c:set var="flag" value="true"/>
						</c:if>

								<dsp:input type="hidden" bean="ProfileFormHandler.emailOptIn" id="optIn" name="Opt-In" value="${flag}"></dsp:input>

               <div class="input grid_3 alpha suffix_2">
						<div class="label">
							<label id="lblfname" for="fname"><bbbl:label key="lbl_profile_firstname" language ="${pageContext.request.locale.language}"/> <span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
						</div>
						<div class="text">
							 <dsp:input bean="ProfileFormHandler.value.firstName" name="firstName" type="text" id="fname" >
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lblfname errorfname"/>
                             </dsp:input>
						</div>
					</div>


             <div class="input grid_3 alpha suffix_2">
						<div class="label">
							<label id="lbllastName" for="lname"><bbbl:label key="lbl_profile_lastname" language ="${pageContext.request.locale.language}"/> <span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
						</div>
						<div class="text">
							 <dsp:input bean="ProfileFormHandler.value.lastName" name="lastName" id="lname" type="text">
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lbllastName errorlname"/>
                             </dsp:input>
						</div>
					</div>



            <div class="input grid_3 alpha suffix_2">
						<div class="label">
							<label id="lblbasePhoneFull" for="basePhoneFull"><bbbl:label key="lbl_profile_primaryphone" language ="${pageContext.request.locale.language}"/></label>
						</div>

						<div class="phone phoneFieldWrap">
                        <fieldset class="phoneFields"><legend class="phoneFieldLegend"><bbbl:label key='lbl_phonefield_phnumber' language='${pageContext.request.locale.language}'/></legend>
							<div class="text fl">
							<dsp:input bean="ProfileFormHandler.value.phoneNumber" name="basePhoneFull" id="phoneNumber" iclass="phone phoneField" type="text" maxlength="10" />
							</div>
							<dsp:input type="hidden" bean="ProfileFormHandler.value.phoneNumber" name="phone" id="phoneNumber" iclass="fullPhoneNum"/>
							<div class="cb">
								<label id="errorbasePhoneFull" for="basePhoneFull" class="error" generated="true"></label>
							</div>
                            </fieldset>
						</div>
					</div>


        		  <div class="input grid_3 alpha suffix_2">
						<div class="label">
							<label id="lblcellPhoneFull" for="cellPhoneFull"><bbbl:label key="lbl_profile_mobilephone" language ="${pageContext.request.locale.language}"/></label>
						</div>

						<div class="phone phoneFieldWrap">
                            <fieldset class="phoneFields"><legend class="phoneFieldLegend"><bbbl:label key='lbl_phonefield_cellnumber' language='${pageContext.request.locale.language}'/></legend>
							<div class="text fl">
							<dsp:input bean="ProfileFormHandler.value.mobileNumber" name="cellPhoneFull" id="mobileNumber" iclass="phone phoneField" type="text" maxlength="10" />
                            <%--<input class="phone phoneField" id="cellPhoneFull" type="text" name="cellPhoneFull" maxlength="10" aria-required="false" aria-labelledby="lblcellPhoneFull errorcellPhoneFull" /> --%> 
							</div>
							 <dsp:input type="hidden" bean="ProfileFormHandler.value.mobileNumber" name="cell" id="mobileNumber" iclass="fullPhoneNum"/>
							<div class="cb">
								<label id="errorcellPhoneFull" for="cellPhoneFull" class="error" generated="true"></label>
							</div>
                            </fieldset>
						</div>
					</div>

					<div class="clear"></div>
         			<div class="button button_active button_active_orange">
						 <c:set var="submitKey">
							<bbbl:label key='lbl_updateprofile_save' language='${pageContext.request.locale.language}' />
						</c:set>
						<dsp:input bean="ProfileFormHandler.update"  id="personalInfoBtn" type="Submit" value="${submitKey}">
                            <dsp:tagAttribute name="aria-pressed" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="personalInfoBtn"/>
                            <dsp:tagAttribute name="role" value="button"/>
                         </dsp:input>
					</div>
					<div class="clear"></div>



         </dsp:form>
         <div class="clear"></div>

</dsp:page>