<%-- ====================== Description===================
/**
* This page is used to display easy return form in which customer inputs his address, city and state or zip code 
* and other details to generate the labels.
* @author Seema
**/
--%>

<dsp:page>
	<dsp:importbean bean="/com/bbb/selfservice/StateDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>  
	<dsp:importbean var="EasyReturnsFormHandler" bean="/com/bbb/selfservice/EasyReturnsFormHandler"/>
	<c:set var="section" value="selfService" scope="request" />
	<c:set var="pageWrapper" value="easyReturnsForm" scope="request" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<c:set var="BedBathCanadaSite">
		<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>

	<bbb:pageContainer titleKey="lbl_easy_return_form_title">
		<jsp:attribute name="section">${section}</jsp:attribute>
		<jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>

		<jsp:body>		
<%--Jsp body starts from here--%>
		<dsp:getvalueof id="currentSiteId" bean="Site.id" />
		<c:set var="BedBathCanadaSite">
			<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
		</c:set>
		<div id="content" class="container_12 clearfix" role="main">
			<div class="grid_12 clearfix">
				<h1>
						<bbbl:label key="lbl_easy_return_form_heading_1"
							language="${pageContext.request.locale.language}" />
					</h1>
				<h3>
						<bbbl:label key="lbl_easy_return_form_heading_2"
							language="${pageContext.request.locale.language}" />
					</h3>
				<p>
						<bbbl:label key="lbl_easy_return_form_heading_3"
							language="${pageContext.request.locale.language}" />
							<br/>
						<bbbl:label key="lbl_easy_returns_mandatory"
							language="${pageContext.request.locale.language}" />
					</p>				
			</div>	

           	<div class="grid_6 clearfix">
                <dsp:form id="frmEasyReturnsForm" action="easy_returns_form.jsp" method="post">
               		<dsp:include page="/global/gadgets/errorMessage.jsp">
				      <dsp:param name="formhandler" bean="EasyReturnsFormHandler"/>
				    </dsp:include>
					<div class="input grid_3 alpha">
						<div class="label">
							<label id="lblfirstName" for="firstName"><bbbl:label
										key="lbl_easy_return_form_fname"
										language="${pageContext.request.locale.language}" /> <span class="required">*</span></label>
						</div>
						<div class="text">
							<dsp:input bean="EasyReturnsFormHandler.firstName" name="firstName" type="text" id="firstName" maxlength="30">
                                   <dsp:tagAttribute name="aria-required" value="true"/>
                                   <dsp:tagAttribute name="aria-labelledby" value="lblfirstName errorfirstName"/>
                            </dsp:input>
						</div>
					</div>
					
					<div class="input grid_3 omega">
						<div class="label">
							<label id="lbllastName" for="lastName"><bbbl:label
										key="lbl_easy_return_form_lname"
										language="${pageContext.request.locale.language}" /> <span class="required">*</span></label>
						</div>
						<div class="text">
							<dsp:input bean="EasyReturnsFormHandler.lastName" name="lastName" type="text" id="lastName" maxlength="30">
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbllastName errorlastName"/>
                            </dsp:input>
						</div>
					</div>
					
					<div class="input grid_4 alpha suffix_2">
						<div class="label">
							<label id="lblcompany" for="company"><bbbl:label
										key="lbl_easy_return_form_company"
										language="${pageContext.request.locale.language}" /></label>
						</div>
						<div class="text">
							<dsp:input bean="EasyReturnsFormHandler.company" name="company" type="text" id="company" maxlength="20">
                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lblcompany errorcompany"/>
                            </dsp:input>
						</div>
					</div>
					
					<div class="input grid_3 alpha suffix_3">
						<div class="label">
							<label id="lbladdress1" for="address1"><bbbl:label
										key="lbl_easy_return_form_addressLine1"
										language="${pageContext.request.locale.language}" /><span class="required">*</span></label>
						</div>
						<div class="text">
							<dsp:input bean="EasyReturnsFormHandler.address1" name="address1" type="text" iclass="poBoxAddNotAllowed" id="address1" maxlength="30">
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbladdress1 erroraddress1"/>
									<dsp:tagAttribute name="autocomplete" value="off"/>
                            </dsp:input>
						</div>
					</div>
					
					<div class="input grid_3 alpha suffix_3">
						<div class="label">
							<label id="lbladdress2" for="address2"><bbbl:label
										key="lbl_easy_return_form_addressLine2"
										language="${pageContext.request.locale.language}" /></label>
						</div>
						<div class="text">
							<dsp:input bean="EasyReturnsFormHandler.address2" name="address2" type="text" iclass="poBoxAddNotAllowed" id="address2" maxlength="30">
                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbladdress2 erroraddress2"/>
                            </dsp:input>
						</div>
					</div>
					
					<div class="grid_6 alpha omega">
					<div class="input grid_2 alpha">
						<div class="label">
							<label id="lblshipfromcity" for="shipfromcity"><bbbl:label
										key="lbl_easy_return_form_city"
										language="${pageContext.request.locale.language}" /><span class="required">*</span></label>
						</div>
						<div class="text">
							<dsp:input bean="EasyReturnsFormHandler.shipfromcity" name="shipfromcity" type="text" id="shipfromcity" maxlength="25">
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lblshipfromcity errorshipfromcity"/>
                            </dsp:input>
						</div>
					</div>
					
															
                <div class="input grid_2 stateBoxPosition">
                    <div class="label">				
					
					<c:choose>
                			<c:when test="${currentSiteId eq BedBathCanadaSite}">
								<label id="lblstate" for="state"><bbbl:label key="lbl_shipping_new_province" language ="${pageContext.request.locale.language}"/><span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>								
							</c:when>
							<c:otherwise>
								<label id="lblstate" for="state"><bbbl:label key="lbl_easy_return_form_state" language ="${pageContext.request.locale.language}"/><span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
							</c:otherwise>
							</c:choose>
					</div>
					<dsp:select bean="EasyReturnsFormHandler.state" iclass="uniform" name="state"
									id="state">
						<dsp:tagAttribute name="aria-required" value="true" />
                        <dsp:tagAttribute name="aria-labelledby" value="lblstate errorstate" />		
						<dsp:droplet name="StateDroplet">
										<dsp:param name="showMilitaryStates" value="false" />
											<dsp:oparam name="output">
											<c:choose>
												<c:when test="${currentSiteId eq BedBathCanadaSite}">
													<dsp:option value="">
														<bbbl:label key="lbl_easy_return_form_select_province"
															language="${pageContext.request.locale.language}" />
													</dsp:option>
												</c:when>									
												<c:otherwise>
													 <dsp:option value="">
														<bbbl:label key="lbl_easy_return_form_select_state"
															language="${pageContext.request.locale.language}" />
													 </dsp:option>
												</c:otherwise>
											</c:choose> 
											
												<dsp:droplet name="ForEach">
													<dsp:param name="array" param="location" />
													<dsp:param name="sortProperties" value="+stateName" />
													<dsp:oparam name="output">
														<dsp:getvalueof param="element.stateName" var="elementVal">
														<dsp:getvalueof param="element.stateCode"
															var="elementCode">
															<dsp:option value="${elementCode}">
																${elementVal}
															</dsp:option>
														</dsp:getvalueof>
														</dsp:getvalueof>
													</dsp:oparam>
												</dsp:droplet>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:select>
					    <label for="state" generated="true" class="error"></label>
						<c:choose>
                			<c:when test="${currentSiteId eq BedBathCanadaSite}">
								<label for="state" class="offScreen"><bbbl:label key="lbl_shipping_new_province" language ="${pageContext.request.locale.language}"/><span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>								
							</c:when>
							<c:otherwise>
								<label for="state" class="offScreen"><bbbl:label key="lbl_easy_return_form_state" language ="${pageContext.request.locale.language}"/><span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
							</c:otherwise>
						</c:choose>
                </div>        
						
				
                    <div class="input grid_2 marLeft_5 omega postalCode clearfix">
                        <c:choose>
                            <c:when test="${currentSiteId eq BedBathCanadaSite}">
                                <div class="label">
                                    <label id="lblpostalcode" for="postalcode"><bbbl:label key="lbl_addcreditcard_postal_code" language ="${pageContext.request.locale.language}"/><span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
                                </div>
                                <div class="text">
		                            <dsp:input bean="EasyReturnsFormHandler.postalcode" name="postalcode" iclass="zipCA"  type="text" id="postalcode" maxlength="7">
		                                    <dsp:tagAttribute name="aria-required" value="true"/>
		                                    <dsp:tagAttribute name="aria-labelledby" value="lblpostalcode errorpostalcode"/>
		                            </dsp:input>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="label">
                                    <label id="lblpostalcode" for="postalcode"><bbbl:label key="lbl_easy_return_form_zip" language ="${pageContext.request.locale.language}"/><span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
                                </div>
                                <div class="text">
                                	<dsp:input bean="EasyReturnsFormHandler.postalcode" name="postalcode" type="text"  iclass="zipUS" id="postalcode" maxlength="10">
		                                    <dsp:tagAttribute name="aria-required" value="true"/>
		                                    <dsp:tagAttribute name="aria-labelledby" value="lblpostalcode errorpostalcode"/>
		                            </dsp:input>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
				</div>
				

				<div class="input grid_3 alpha suffix_3 multiplePhoneFldsWrap">
					<div class="label">
						<label for="basePhoneERF1"><bbbl:label
											key="lbl_easy_return_form_primary_phone"
											language="${pageContext.request.locale.language}" /><span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>

					</div>
						
					<div class="phone multiplePhoneFldsWrap">								
                    <fieldset class="phoneFields"><legend class="phoneFieldLegend"><bbbl:label key='lbl_phonefield_phnumber' language='${pageContext.request.locale.language}'/></legend>
                    <label id="lblbasePhoneERF1" for="basePhoneERF1" class="offScreen" title="<bbbl:label key='lbl_phonefield_areacode' language='${pageContext.request.locale.language}'/>"><bbbl:label key="lbl_phonefield_areacode" language="${pageContext.request.locale.language}"/></label>
					<label id="lblbasePhoneERF2" for="basePhoneERF2" class="offScreen" title="<bbbl:label key='lbl_phonefield_exchange' language='${pageContext.request.locale.language}'/>"><bbbl:label key="lbl_phonefield_exchange" language="${pageContext.request.locale.language}"/></label>
					<label id="lblbasePhoneERF3" for="basePhoneERF3" class="offScreen" title="<bbbl:label key='lbl_phonefield_number' language='${pageContext.request.locale.language}'/>"><bbbl:label key="lbl_phonefield_number" language="${pageContext.request.locale.language}"/></label>
						<div class="text fl">
							<dsp:input bean="EasyReturnsFormHandler.basePhoneERF1" iclass="phone phoneField" name="basePhoneERF1" type="text" id="basePhoneERF1" maxlength="3">
                                  <dsp:tagAttribute name="aria-required" value="true"/>
                                  <dsp:tagAttribute name="aria-labelledby" value="lblbasePhoneERF1 errorbasePhoneERF1"/>
		                    </dsp:input>
							<span class="dateSeperator">-</span>
						</div>
						<div class="text fl">
							<dsp:input bean="EasyReturnsFormHandler.basePhoneERF2" iclass="phone phoneField" name="basePhoneERF2" type="text" id="basePhoneERF2" maxlength="3">
                                  <dsp:tagAttribute name="aria-required" value="true"/>
                                  <dsp:tagAttribute name="aria-labelledby" value="lblbasePhoneERF2 basePhoneERF2"/>
		                    </dsp:input>
							<span class="dateSeperator">-</span>
						</div>
						<div class="text fl">
							<dsp:input bean="EasyReturnsFormHandler.basePhoneERF3" iclass="phone phoneField" name="basePhoneERF3" type="text" id="basePhoneERF3" maxlength="4">
                                  <dsp:tagAttribute name="aria-required" value="true"/>
                                  <dsp:tagAttribute name="aria-labelledby" value="lblbasePhoneERF3 errorbasePhoneERF3"/>
		                    </dsp:input>
						</div>
						<dsp:input type="hidden" name="phone" bean="EasyReturnsFormHandler.phone" iclass="fullPhoneNum" />
						<div class="cb">
							<label class="PhoneErrLabel hidden"></label>
							<label for="basePhoneERF1" class="error fl hidden" generated="true"></label>
							<label for="basePhoneERF2" class="error fl hidden" generated="true"></label>
							<label for="basePhoneERF3" class="error fl hidden" generated="true"></label>
						</div>
                        </fieldset>
					</div>
					</div>	
					
				<div class="input grid_3 alpha suffix_3">
					<div class="label">
						<label id="lblemailaddress" for="emailaddress"><bbbl:label
										key="lbl_easy_return_form_email"
										language="${pageContext.request.locale.language}" /><span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
					</div>
					<div class="text">
						<dsp:input bean="EasyReturnsFormHandler.emailaddress" name="emailaddress"  iclass="emailaddress" type="text" id="emailaddress" maxlength="150">
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lblNewEmail errornewEmail"/>
		                </dsp:input>
					</div>
				</div>
					
					<div class="input grid_6 alpha">
					<c:choose>
						<c:when test="${currentSiteId eq BedBathUSSite}">
							<div class="label">
								<label id="lblshiptorma" for="shiptorma"><bbbl:label key="lbl_easy_return_form_orderNumber" language="${pageContext.request.locale.language}" /><span class="required">*</span></label>
							</div>
							<div class="text grid_3">
								<dsp:input bean="EasyReturnsFormHandler.shiptorma" iclass="shiptormaUS" name="shiptorma" type="text" id="shiptorma" maxlength="23">
			                        <dsp:tagAttribute name="aria-required" value="true"/>
			                        <dsp:tagAttribute name="aria-labelledby" value="lblshiptorma errorshiptorma"/>
				                </dsp:input>
							</div>
						</c:when>
						<c:when test="${currentSiteId eq BuyBuyBabySite}">
							<div class="label">
								<label id="lblshiptorma" for="shiptorma"><bbbl:label key="lbl_easy_return_form_orderNumber" language="${pageContext.request.locale.language}" /><span class="required">*</span></label>
							</div>
							<div class="text grid_3">
								<dsp:input bean="EasyReturnsFormHandler.shiptorma" iclass="shiptormaBB" name="shiptorma" type="text" id="shiptorma" maxlength="23">
			                        <dsp:tagAttribute name="aria-required" value="true"/>
			                        <dsp:tagAttribute name="aria-labelledby" value="lblshiptorma errorshiptorma"/>
				                </dsp:input>
							</div>
						</c:when>
						<c:otherwise>
							<div class="label">
								<label id="lblshiptorma" for="shiptorma"><bbbl:label key="lbl_easy_return_form_orderNumber" language="${pageContext.request.locale.language}" /><span class="required">*</span></label>
							</div>
							<div class="text grid_3">
								<dsp:input bean="EasyReturnsFormHandler.shiptorma" iclass="shiptormaCA" name="shiptorma" type="text" id="shiptorma" maxlength="23">
			                        <dsp:tagAttribute name="aria-required" value="true"/>
			                        <dsp:tagAttribute name="aria-labelledby" value="lblshiptorma errorshiptorma"/>
				                </dsp:input>
							</div>
						</c:otherwise>
					</c:choose>
					</div>
					
					<div class="select grid_2 alpha input">
                        <div class="label">
                            <label id="lblnumboxes" for="numboxes"><bbbl:label
                                            key="lbl_easy_return_form_NumberofBox"
                                            language="${pageContext.request.locale.language}" /> <span class="required">*</span></label>
                        </div>
						<div class="grid_1 alpha suffix_1">
							<dsp:select bean="EasyReturnsFormHandler.numboxes" name="numboxes" id="numboxes" iclass="uniform">
								<dsp:tagAttribute name="aria-required" value="true" />
		                        <dsp:tagAttribute name="aria-labelledby" value="lblnumboxes errornumboxes" />
									<option value="1" selected="selected">1</option>
							</dsp:select>
							<label id="errornumboxes" for="numboxes" generated="true" class="error"></label>
						</div>
						<label for="numboxes" generated="true" class="error"></label>
						<label for="numboxes" class="offScreen"><bbbl:label
                                            key="lbl_easy_return_form_NumberofBox"
                                            language="${pageContext.request.locale.language}" /> <span class="required">*</span></label>
					</div>
					
					<div class="grid_6 alpha">
						<div class="label padBottom_5">
							<label><strong><bbbl:label
											key="lbl_easy_return_form_LabelOptions"
											language="${pageContext.request.locale.language}" /></strong></label>
						</div>
						<div class="radio">
							 <dsp:input id="displayLabelOnly" name="rslevel" type="radio" bean="EasyReturnsFormHandler.rslevel" value="4wrl" checked="true">
                                    <dsp:tagAttribute name="aria-checked" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbldisplayLabelOnly"/>
                            </dsp:input>
						</div>						
							<label id="lbldisplayLabelOnly" for="displayLabelOnly" class="marRight_10"><bbbl:label
									key="lbl_easy_return_form_option1"
									language="${pageContext.request.locale.language}" /></label>							
						
						<div class="radio">
							<dsp:input id="emailReturn" name="rslevel" type="radio" bean="EasyReturnsFormHandler.rslevel" value="4erl">
                                    <dsp:tagAttribute name="aria-checked" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lblemailReturn"/>
                            </dsp:input>
						</div>						
							<label id="lblemailReturn" for="emailReturn"  class="marRight_10"><bbbl:label
									key="lbl_easy_return_form_option2"
									language="${pageContext.request.locale.language}" /></label>			
					</div>
					
						<div class="button button_active marTop_20">
							<dsp:input type="submit" bean="EasyReturnsFormHandler.generateLabel" value="generate label" >
	                            <dsp:tagAttribute name="aria-pressed" value="false"/>
	                            <dsp:tagAttribute name="role" value="button"/>
	                        </dsp:input>
						</div>
					<div class="grid_6 alpha omega">	
						<p><bbbl:label
									key="lbl_easy_return_form_printing_issue_note"
									language="${pageContext.request.locale.language}" /></p>
					</div>
				</dsp:form>
            </div>
			
		</div>		
<%--Jsp body Ends here--%>	
	</jsp:body>
	<jsp:attribute name="footerContent">

			<script type="text/javascript">
			           if(typeof s !=='undefined') {
			        	s.pageName='Content Page>Returns Form';
			        	s.channel='Company Info';
			        	s.prop1='Content Page';
			        	s.prop2='Content Page';
			        	s.prop3='Content Page';
			        	s.prop4='Company Info';
			                
			            var s_code=s.t();
			            if(s_code)document.write(s_code);           
			           }
			           function omnitureExternalLinks(data){
			              	if (typeof s !== 'undefined') {
			              	externalLinks(data);
			              	}
			              }
			</script>
		</jsp:attribute>
	</bbb:pageContainer>
</dsp:page>
