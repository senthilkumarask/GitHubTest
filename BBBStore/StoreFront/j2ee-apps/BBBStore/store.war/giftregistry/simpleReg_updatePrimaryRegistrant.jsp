<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>	
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="isTransient" bean="Profile.transient"/>
	<dsp:getvalueof var="event" param="event"/>
	<dsp:getvalueof var ="securityStatus" bean="/atg/userprofiling/Profile.securityStatus"/>
	<c:if test="${securityStatus eq '2' }">
		<c:set var="recognisedUser">true</c:set>
	</c:if>
	<dsp:getvalueof var="coRegOwner" param="coRegOwner" />
	<c:if test="${coRegOwner == 'true'}">
		<dsp:input id="isCoRegOwner" bean="GiftRegistryFormHandler.registryVO.coRegOwner" type="hidden" value="true"/>
	</c:if>
	
	<c:choose>
        <c:when test="${event == 'BA1'}">
		     <c:set var="accountInfoClass">alpha</c:set>
		     <c:set var="gridClass1">grid_5</c:set>
		     <c:set var="gridClass2">grid_4 noMar</c:set>
		     <c:set var="gridClass3">grid_4 noMar</c:set>
		     <c:set var="noMarClass">noMar</c:set>
		     <c:set var="floatRightClass">fr</c:set>
		     <c:set var="alphaClass">alpha</c:set>
		     <c:set var="gridClass4">grid_4</c:set>

        </c:when>
        <c:otherwise>
            <c:set var="accountInfoClass">omega</c:set>
             <c:set var="gridClass1">grid_6</c:set>
             <c:set var="gridClass2">grid_3</c:set>
             <c:set var="gridClass3">grid_6</c:set>
             <c:set var="noMarClass"></c:set>
             <c:set var="floatRightClass"></c:set>
             <c:set var="alphaClass"></c:set>
              <c:set var="gridClass4"></c:set>
        </c:otherwise>
    </c:choose>
	 
	<c:choose>
        <c:when test="${event == 'BRD'}">
			<div class="grid_4 alpha clearfix brdFields">
		</c:when>
		<c:otherwise>
			<div class="grid_4 alpha clearfix">
		</c:otherwise>
	</c:choose>   
	   <dsp:getvalueof var="inputListMap" param="inputListMap"/>
                  <label for="txtPrimaryRegistrantFirstName" class="padBottom_5"><bbbl:label key="lbl_your_info" language ="${pageContext.request.locale.language}"/></label>
               <c:choose>
                   <c:when test="${isTransient}"> 
                    <div class="inputField noMarRight clearfix alpha omega" id="regFullName">
                     <div class="inputField full-name form-group grid_4">				
				            <input type="text" id="fullName" name="txtRegistrantFullName" placeholder="<bbbl:label key='lbl_reg_ph_fullname' language ='${pageContext.request.locale.language}'/>" class="required"/>
			         </div>
					 <div class="inputField noMarRight clearfix alpha omega ${firstnamegrid} width165 ">
					   <c:set var="firstNamePlaceHolder"><bbbl:label key='lbl_reg_ph_firstname' language ='${pageContext.request.locale.language}'/></c:set>
		                    <dsp:input type="text" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.firstName" id="txtPrimaryRegistrantFirstName" name="txtPrimaryRegistrantFirstNameAltName" iclass="required cannotStartWithSpecialChars alphabasicpunc hidden">
		                        <dsp:tagAttribute name="aria-required" value="true"/>
		                        <dsp:tagAttribute name="aria-labelledby" value="txtPrimaryRegistrantFirstNameAlt errortxtPrimaryRegistrantFirstNameAlt"/>
		                        <dsp:tagAttribute name="placeholder" value="${firstNamePlaceHolder}"/>
		                    </dsp:input>
		            </div>			
		                <div class="inputField noMarRight clearfix alpha omega grid_2">
		                       <c:set var="lastNamePlaceHolder"><bbbl:label key='lbl_reg_ph_lastname' language ='${pageContext.request.locale.language}'/></c:set>
		                       <dsp:input type="text"	bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.lastName" id="txtPrimaryRegistrantLastName" name="txtPrimaryRegistrantLastNameAltName" iclass="required cannotStartWithSpecialChars alphabasicpunc hidden borderLeftnone">
		                        <dsp:tagAttribute name="aria-required" value="true"/>
		                        <dsp:tagAttribute name="aria-labelledby" value="lastNameReg errorlastNameReg"/>
		                        <dsp:tagAttribute name="placeholder" value="${lastNamePlaceHolder}"/>
		                    </dsp:input>
		                </div>
         			  </div>
                   </c:when>
                   <c:otherwise>
				   
						<dsp:getvalueof var="firstNameFromVoOrProf" value="${regVO.primaryRegistrant.firstName}"/>
						<c:if test="${empty firstNameFromVoOrProf}">
							<dsp:getvalueof var="firstNameFromVoOrProf" bean="Profile.firstName"/>
						</c:if>
						<dsp:getvalueof var="lastNameFromVoOrProf" value="${regVO.primaryRegistrant.lastName}"/>
						<c:if test="${empty lastNameFromVoOrProf}">
							<dsp:getvalueof var="lastNameFromVoOrProf" bean="Profile.lastName"/>
						</c:if>

						<c:if test="${not empty firstNameFromVoOrProf && not empty lastNameFromVoOrProf}">
						<div class="inputField full-name form-group grid_4">				
				            <input type="text" id="fullName" name="txtRegistrantFullName" placeholder="<bbbl:label key='lbl_reg_ph_fullname' language ='${pageContext.request.locale.language}'/>" class="required" value="${firstNameFromVoOrProf} ${lastNameFromVoOrProf}"/>
						</div>
						</c:if>
				   
		                <div class="inputField noMarRight clearfix alpha omega" id="regFullName">
		                    <div class="inputField noMarRight clearfix alpha omega ${firstnamegrid}">
								<c:choose>
								<c:when test="${coRegOwner == 'true'}">
									<dsp:input type="text" id="txtPrimaryRegistrantFirstName" value="${firstNameFromVoOrProf}" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.firstName" name="txtPrimaryRegistrantFirstNameAltName">
										<dsp:tagAttribute name="aria-required" value="true"/>
										<dsp:tagAttribute name="aria-labelledby" value="txtPrimaryRegistrantFirstNameAlt errortxtPrimaryRegistrantFirstNameAlt"/>
										<dsp:tagAttribute name="class" value="required hidden disabled"/>
										<dsp:tagAttribute name="readonly" value="true" />
										<dsp:tagAttribute name="aria-disabled" value="true" />
									</dsp:input>
								</c:when>
								<c:otherwise>
									<dsp:input type="text" id="txtPrimaryRegistrantFirstName" value="${firstNameFromVoOrProf}" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.firstName" name="txtPrimaryRegistrantFirstNameAltName">
										<dsp:tagAttribute name="aria-required" value="true"/>
										<dsp:tagAttribute name="aria-labelledby" value="txtPrimaryRegistrantFirstNameAlt errortxtPrimaryRegistrantFirstNameAlt"/>
										<dsp:tagAttribute name="class" value="required hidden"/>
									</dsp:input>
								</c:otherwise>
								</c:choose>
		           			    </div>			
				                <div class="inputField noMarRight clearfix alpha omega ${lastnamegrid} width165">
								<c:choose>
								<c:when test="${coRegOwner == 'true'}">
				                       <dsp:input type="text" id="txtPrimaryRegistrantLastName" value="${lastNameFromVoOrProf}" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.lastName" iclass="borderLeftnone" name="txtPrimaryRegistrantLastNameAltName">
											<dsp:tagAttribute name="aria-required" value="true"/>
											<dsp:tagAttribute name="aria-labelledby" value="lastNameReg errorlastNameReg"/>
											<dsp:tagAttribute name="class" value="required hidden disabled"/>
											<dsp:tagAttribute name="readonly" value="true" />
											<dsp:tagAttribute name="aria-disabled" value="true" />
				                       </dsp:input>
								</c:when>
								<c:otherwise>
									<dsp:input type="text" id="txtPrimaryRegistrantLastName" value="${lastNameFromVoOrProf}" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.lastName" iclass="borderLeftnone" name="txtPrimaryRegistrantLastNameAltName">
											<dsp:tagAttribute name="aria-required" value="true"/>
											<dsp:tagAttribute name="aria-labelledby" value="lastNameReg errorlastNameReg"/>
											<dsp:tagAttribute name="class" value="required hidden"/>
									</dsp:input>
								</c:otherwise>
								</c:choose>
		                        </div>
		                     </div>            
                       </c:otherwise>
                </c:choose>
                 <c:if test="${event == 'BRD' || event == 'COM'}">
				 <c:if test="${not empty regVO.regBG}">
				 <c:set var="regBgBrideChecked" value=""/>
				 <c:set var="regBgGroomBtnChecked" value=""/>
					<c:choose>
					<c:when test="${regVO.regBG eq 'B'}">
						<c:set var="regBgBrideChecked" value="true"/>
					</c:when>
					<c:when test="${regVO.regBG eq 'G'}">
						<c:set var="regBgGroomBtnChecked" value="true"/>
					</c:when>
					<c:otherwise>
					</c:otherwise>
					</c:choose>
				 </c:if>
				 
                 <div class="grid_1 clearfix alpha omega radioPosLeft">
										<div class="square-radio brideBox">
											<label id="lblBG" for="coRegbrideRadioBtn">B<br><span class="smlTxt"><bbbl:label key="lbl_reg_bride" language ="${pageContext.request.locale.language}"/></span></label>
                                          	<dsp:input tabindex="4" type="radio" iclass="square-radio--content" bean="GiftRegistryFormHandler.regBG"  value="B" checked="${regBgBrideChecked}">
												<dsp:tagAttribute name="aria-checked" value="${regBgBrideChecked}"/>
												<dsp:tagAttribute name="aria-hidden" value="false"/>
												<dsp:tagAttribute name="id" value="coRegbrideRadioBtn"/>
												<dsp:tagAttribute name="aria-label" value="bride"/>
                                 			</dsp:input>
                                        </div>
										<div class="square-radio brideBox clearfix">
											<label id="lblBG" for="coRegGroomRadioBtn">G<br><span class="smlTxt"><bbbl:label key="lbl_reg_groom" language ="${pageContext.request.locale.language}"/></span></label>
											<dsp:input tabindex="4" type="radio" iclass="square-radio--content" bean="GiftRegistryFormHandler.regBG"  value="G" checked="${regBgGroomBtnChecked}">
												<dsp:tagAttribute name="aria-checked" value="${regBgGroomBtnChecked}"/>
												<dsp:tagAttribute name="aria-hidden" value="false"/>
												<dsp:tagAttribute name="id" value="coRegGroomRadioBtn"/>
												<dsp:tagAttribute name="aria-label" value="groom"/>
                                           </dsp:input>
										</div>                
       			</div>
  				</c:if>
             </div>
             
               <c:if test="${event == 'BA1'}"> 
					  
               	        <div class="grid_2 omega marRight_20">  
             				 <div class="inputField grid_2 noMarRight clearfix alpha omega">
             				 <c:set var="regmaidenname">	<bbbl:label key="lbl_reg_ph_maidenname" language ="${pageContext.request.locale.language}"/></c:set>
							 <label id="lbltxtRegistrantMaiden" for="txtPrimaryRegistrantbabyMaidenName" class="noMarTop" style="text-transform: lowercase;">
                                <bbbl:label key="lbl_registrants_maiden_name" language ="${pageContext.request.locale.language}"/>
                             </label>

							 <c:choose>
							 <c:when test="${not empty regVO.primaryRegistrant.babyMaidenName}">
								<dsp:getvalueof var="babyMaidenNm" value="${regVO.primaryRegistrant.babyMaidenName}"/>
							 </c:when>
							 <c:otherwise>
								<dsp:getvalueof var="babyMaidenNm" value=""/>
							 </c:otherwise>
							 </c:choose>
							 <c:choose>
								<c:when test="${coRegOwner == 'true'}">
								<dsp:input type="text" id="txtPrimaryRegistrantbabyMaidenName"  bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.babyMaidenName" iclass="cannotStartWithSpecialChars alphabasicpunc escapeHTMLTag disabled" value="${babyMaidenNm}">
									<dsp:tagAttribute name="minlength" value="2"/>
									<dsp:tagAttribute name="aria-required" value="true"/>
									<dsp:tagAttribute name="aria-labelledby" value="lastNameReg errorlastNameReg"/>
									<dsp:tagAttribute name="placeholder" value="${regmaidenname}"/>
									<dsp:tagAttribute name="readonly" value="true" />
									<dsp:tagAttribute name="aria-disabled" value="true" />
								</dsp:input>
								</c:when>
								<c:otherwise>
								<dsp:input type="text" id="txtPrimaryRegistrantbabyMaidenName"  bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.babyMaidenName" iclass="cannotStartWithSpecialChars alphabasicpunc escapeHTMLTag" value="${babyMaidenNm}">
									<dsp:tagAttribute name="minlength" value="2"/>
									<dsp:tagAttribute name="aria-required" value="true"/>
									<dsp:tagAttribute name="aria-labelledby" value="lastNameReg errorlastNameReg"/>
									<dsp:tagAttribute name="placeholder" value="${regmaidenname}"/>
								</dsp:input>
								</c:otherwise>
							 </c:choose>
        					  </div>
        				   </div>
                </c:if>
				
						<dsp:getvalueof var="emailFromVoOrProf" value="${regVO.primaryRegistrant.email}"/>
						<c:if test="${empty emailFromVoOrProf}">
							<dsp:getvalueof var="emailFromVoOrProf" bean="Profile.email"/>
						</c:if>
               
                <div class="${gridClass3} ${accountInfoClass}">  
				
                        				<label for="email" class="padBottom_5"><bbbl:label key="lbl_account_info" language ="${pageContext.request.locale.language}"/></label>              
                          				 <div class="grid_3 alpha  clearfix">
                          				 
                          				 <div class="input_wrap ${gridClass2}" aria-live="assertive">
										 <c:choose>
											<c:when test="${coRegOwner == 'true'}">
												<dsp:input tabindex="3" id="email" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.email" name="email" value="${emailFromVoOrProf}" type="email" iclass="disabled widthHundred" >
													<dsp:tagAttribute name="readonly" value="true" />
													<dsp:tagAttribute name="aria-disabled" value="true" />
												</dsp:input>
											</c:when>
											<c:otherwise>
												<dsp:input tabindex="3" id="email" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.email" name="email" value="${emailFromVoOrProf}" type="email" iclass="widthHundred" >
												</dsp:input>
											</c:otherwise>
										 </c:choose>
                          				 </div>
                          				 </div>
            	</div>
</dsp:page>