<dsp:page> 

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean var="ProfileFormHandler" bean="/atg/userprofiling/ProfileFormHandler"/>

	
			<div class="grid_5 alpha marTop_20 marBottom_10">
					<h2 class="noMar"><bbbl:label key="lbl_personalinfo_updatepassword" language ="${pageContext.request.locale.language}"/></h2>
			</div>
            <div class="clear"></div>
			
             
            	<dsp:form method="post" iclass="clearfix" id="updatePass" action="personalinfo.jsp">
            	<%-- Client DOM XSRF | Part -1
            	< dsp:input bean="ProfileFormHandler.changePasswordSuccessURL" type="hidden" value="personalinfo.jsp"/>
				<dsp:input bean="ProfileFormHandler.changePasswordErrorURL" type="hidden" value="personalinfo.jsp"/> --%>
				
				
                <dsp:input bean="ProfileFormHandler.value.email" type="text" iclass="hidden" name="pwdSaveFixEmail" /> 
                
				 <div class="input grid_3 alpha suffix_2">
                    <div class="label posRel">
                    	 <label id="lblcPassword" for="cpassword"><bbbl:label key="lbl_personalinfo_currentpassword" language ="${pageContext.request.locale.language}"/> <span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
                        <div class="showPassDiv clearfix">
                            <input name="showPassword" type="checkbox" value="" data-toggle-class="showpassChangePasswordOld" class="showPassword" id="showPasswordOld" aria-labelledby="lblshowPasswordOld" />
                            <label id="lblshowPasswordOld" for="showPasswordOld" class="lblShowPassword"><bbbl:label key="lbl_show_password" language="${pageContext.request.locale.language}"/></label>
                            <div class="clear"></div> 
                        </div>
                 	</div>
                 	<div class="text">
                 		 <dsp:input bean="ProfileFormHandler.value.oldpassword"  id="cpassword" name="currentpassword" value="" type="password" autocomplete="off" iclass="showpassChangePasswordOld">
                         <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblcPassword errorcpassword"/>
                         </dsp:input>
                    </div>
                </div>
                 <div class="input grid_3 alpha suffix_2">
                    <div class="label posRel">
                    	<label id="lblpassword" for="password"><bbbl:label key="lbl_personalinfo_newpassword" language ="${pageContext.request.locale.language}"/> <span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
                        <div class="showPassDiv clearfix">
                            <input name="showPassword" type="checkbox" value="" data-toggle-class="showpassChangePassword" class="showPassword" id="showPassword" />
                            <label for="showPassword" class="lblShowPassword"><bbbl:label key="lbl_show_password" language="${pageContext.request.locale.language}"/></label>
                            <div class="clear"></div> 
                        </div>
                 	</div>
                 	<div class="text">
                 		 <dsp:input bean="ProfileFormHandler.value.password" id="password" name="password" value="" type="password" autocomplete="off" iclass="showpassChangePassword">
                         <dsp:tagAttribute name="aria-required" value="true"/>
                         <dsp:tagAttribute name="aria-labelledby" value="lblpassword errorpassword"/>
                         </dsp:input>
                    </div>
                </div>
                <div class="input grid_3 alpha suffix_2">
                    <div class="label">
                    	<label id="lblconfirmPassword" for="confirmPassword"><bbbl:label key="lbl_personalinfo_confirmnewpassword" language ="${pageContext.request.locale.language}"/> <span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
                 	</div>
                 	<div class="text">
                 		<dsp:input bean="ProfileFormHandler.value.confirmpassword" type="password" autocomplete="off" name="confirmPassword" id="confirmPassword" value="" iclass="showpassChangePassword"  >
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblconfirmPassword errorconfirmPassword"/>
                        </dsp:input>
                    </div>
                </div>
                 <div class="clear"></div> 
				<div class="button button_active button_active_orange">
				     <c:set var="submitKey">
							<bbbl:label key='lbl_personalinfo_update' language='${pageContext.request.locale.language}' />
						</c:set>
				   <dsp:input bean="ProfileFormHandler.value.firstName" name="fname"  id="firstName" type="hidden"/>
				   <dsp:input bean="ProfileFormHandler.value.lastName" name="lname" id="lastName"  type="hidden"/>
				    <dsp:input bean="ProfileFormHandler.fromPage" type="hidden" value="update" />
				   <dsp:input bean="ProfileFormHandler.changePassword" id="updatePassBtn" type="Submit" value="${submitKey}">
                       <dsp:tagAttribute name="aria-pressed" value="false"/>
                       <dsp:tagAttribute name="aria-labelledby" value="updatePassBtn"/>
                       <dsp:tagAttribute name="role" value="button"/>
                   </dsp:input>
               </div>
                <dsp:input bean="ProfileFormHandler.changePassword" type="hidden" value="submit"/>
                
             </dsp:form>
             <div class="clear"></div>
	
    </dsp:page>


