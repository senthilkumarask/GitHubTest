<dsp:page>
	<%@ page import="com.bbb.constants.BBBCoreConstants"%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>	
    <dsp:importbean var="CurrentDate" bean="/atg/dynamo/service/CurrentDate"/>
	<dsp:importbean bean="/com/bbb/selfservice/StateDroplet" />
	<dsp:importbean var="Profile" bean="/atg/userprofiling/Profile" />
	<dsp:importbean var="HWRegistrationFormHandler" bean="/com/bbb/selfservice/HWRegistrationFormHandler" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<bbb:pageContainer>
		<div id="content" class="container_12 clearfix" role="main">
			<div id="cmsPageHead" class="grid_12 clearfix">
				<h1><bbbl:label key="lbl_hwhome_title" language="${pageContext.request.locale.language}" /></h1>
			</div>	
			<div id="cmsPageContent">
				<div class="grid_12 clearfix">
					<dsp:include page="/cms/static/staticpage.jsp?pageName=HealthyWomen&showAsPopup=true">		
					</dsp:include>
					<dsp:droplet name="Switch">
						<dsp:param name="value" bean="HWRegistrationFormHandler.successMessage"/>
						<dsp:oparam name="true">
							<b>
								<span style="color: #FF0000">
									<bbbl:label key="lbl_hwregistration_confirmation" language="${pageContext.request.locale.language}" />
								</span>
							</b>
						</dsp:oparam>
						<dsp:oparam name="false">
							<div class="grid_12 clearfix alpha omega">
								<p class="noMarTop marBottom_10">
									<bbbl:label key="lbl_hwregistration_title" language="${pageContext.request.locale.language}" />
								</p>
								<p>
										<dsp:include page="/global/gadgets/errorMessage.jsp">
										<dsp:param name="formhandler" bean="HWRegistrationFormHandler"/>
										</dsp:include>
								</p>
		
								<div class="grid_4 clearfix alpha prefix-1">
									<dsp:form iclass="form" action="HWHome.jsp" id="healthyWomanRegistrationForm" method="post">
										<!--<dsp:input bean="HWRegistrationFormHandler.bridalBookSuccessURL" type="hidden" value="HWHome.jsp" />
										<dsp:input bean="HWRegistrationFormHandler.bridalBookErrorURL" type="hidden" value="HWHome.jsp" />-->
										<div class="grid_4 clearfix alpha omega">
											
											<div class="inputField clearfix">
												<label id="lbltxtEmailAddr" for="txtEmailAddr">
													<bbbl:label key="lbl_hwregistration_email" language="${pageContext.request.locale.language}" />
													<span class="required">*</span>
												</label>
												<dsp:input bean="/com/bbb/selfservice/HWRegistrationFormHandler.hwRegistrationVO.emailAddr" name="emailAddr" id="txtEmailAddr" type="text" >
                                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtEmailAddr errortxtEmailAddr"/>
                                                </dsp:input>
											</div>
										</div>
										<div class="grid_4 clearfix alpha omega">
											<div class="inputField clearfix">
												<label id="lbltxtFirstName" for="txtFirstName">
													<bbbl:label key="lbl_hwregistration_firstName" language="${pageContext.request.locale.language}" />
												</label> 
												<dsp:getvalueof var="fName" bean="/atg/userprofiling/Profile.firstName"/>
												<c:if test="${!empty fName}">
												   <dsp:setvalue bean="/com/bbb/selfservice/HWRegistrationFormHandler.hwRegistrationVO.firstName" beanvalue="/atg/userprofiling/Profile.firstName"/> 
												</c:if>
												<dsp:input bean="/com/bbb/selfservice/HWRegistrationFormHandler.hwRegistrationVO.firstName" name="firstName" id="txtFirstName" type="text" >
                                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtFirstName errortxtFirstName"/>
                                                </dsp:input>
											</div>
										</div>
										<div class="grid_4 clearfix alpha omega">
											
											<div class="inputField clearfix">
												<label id="lbltxtLastName" for="txtLastName">
												  <bbbl:label key="lbl_hwregistration_lastName" language="${pageContext.request.locale.language}" />
												</label> 
												<dsp:getvalueof var="lName" bean="/atg/userprofiling/Profile.lastName"/>
												<c:if test="${!empty lName}">
												   <dsp:setvalue bean="/com/bbb/selfservice/HWRegistrationFormHandler.hwRegistrationVO.lastName" beanvalue="/atg/userprofiling/Profile.lastName" /> 
												</c:if>
												<dsp:input bean="/com/bbb/selfservice/HWRegistrationFormHandler.hwRegistrationVO.lastName" name="lastName" id="txtLastName" type="text" >
                                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtLastName errortxtLastName"/>
                                                </dsp:input>
											</div>
										</div>
										<div class="grid_4 clearfix alpha omega">
											
											<div class="inputField clearfix">
												<label id="lbltxtAddress1" for="txtAddress1">
													<bbbl:label key="lbl_hwregistration_address" language="${pageContext.request.locale.language}" />
												</label>
												<dsp:input type="text" name="shippingAddress1" id="txtAddress1" iclass="poBoxAddNotAllowed" bean="/com/bbb/selfservice/HWRegistrationFormHandler.hwRegistrationVO.addressLine1" maxlength="30" >
                                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtAddress1 errortxtAddress1"/>
                                                </dsp:input>
											</div>
										</div>
										<div class="grid_4 clearfix alpha omega">
											
											<div class="inputField clearfix">
												<label id="lbltxtAddress2" for="txtAddress2">
													<bbbl:label key="lbl_hwregistration_address2" language="${pageContext.request.locale.language}" />
												</label> 
												<dsp:input type="text" name="shippingAddress2" id="txtAddress2" iclass="poBoxAddNotAllowed" bean="/com/bbb/selfservice/HWRegistrationFormHandler.hwRegistrationVO.addressLine2" maxlength="30" >
                                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtAddress2 errortxtAddress2"/>
                                                </dsp:input>
											</div>
										</div>
										<div class="grid_4 clearfix alpha omega">
											
											<div class="inputField clearfix">
												<label id="lbltxtCity" for="txtCity">
													<bbbl:label key="lbl_hwregistration_city" language="${pageContext.request.locale.language}" />
												</label> 
												<dsp:input type="text" name="city" id="txtCity" bean="/com/bbb/selfservice/HWRegistrationFormHandler.hwRegistrationVO.city" >
                                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtCity errortxtCity"/>
                                                </dsp:input>
											</div>
										</div>
										<div class="grid_2 alpha clearfix">
											
											<div class="inputField width_1">
												<label id="lblstateName" for="stateName">
													<bbbl:label key="lbl_hwregistration_state" language="${pageContext.request.locale.language}" />
												</label>
													  <dsp:select bean="/com/bbb/selfservice/HWRegistrationFormHandler.hwRegistrationVO.state" name="stateName" id="stateName" iclass="uniform">
                                                      <dsp:tagAttribute name="aria-required" value="true"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lblstateName errorstateName"/>
														<dsp:droplet name="/com/bbb/selfservice/StateDroplet">
															<dsp:oparam name="output">
																<dsp:option>
																	<c:choose>
																	<c:when test="${currentSiteId eq BedBathCanadaSite}">
																	  <bbbl:label key="lbl_bridalbook_select_province"
																	  language="${pageContext.request.locale.language}" />
																	</c:when>
																	 <c:otherwise>
																		<bbbl:label key="lbl_bridalbook_select_state"
																		language="${pageContext.request.locale.language}" />
																	</c:otherwise>
																   </c:choose>
																</dsp:option>
																<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																   <dsp:param name="array" param="location" />
																   <dsp:oparam name="output">
																   <dsp:getvalueof param="element.stateName" id="stateName" />
																   <dsp:getvalueof param="element.stateCode" id="stateCode" />
																   <dsp:option value="${stateCode}">
																	${stateName}
																   </dsp:option>														
																</dsp:oparam>
															</dsp:droplet>
														</dsp:oparam>
													   </dsp:droplet>
													</dsp:select>
											</div>
										</div>
										<div class="grid_2 omega">
											
											<div class="inputField clearfix">
												<label id="lbltxtZip" for="txtZip"><bbbl:label key="lbl_hwregistration_zip" language="${pageContext.request.locale.language}" /></label> 
												<dsp:input type="text" name="zipUS" id="txtZip" bean="/com/bbb/selfservice/HWRegistrationFormHandler.hwRegistrationVO.zipcode" >
                                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtZip errortxtZip"/>
                                                </dsp:input>
											</div>
										</div>
										<div class="grid_4 cb alpha omega">
											<label id="errortxtZip" class="error" generated="true" for="txtEventDateUS"></label>
										</div>
										
										<div class="button btnApply button_active button_active_orange pushDown clearfix">
										   <dsp:input id="btnSubmitHWRegRequest" type="submit" value="Submit" bean="/com/bbb/selfservice/HWRegistrationFormHandler.hWRegistrationRequest" >
                                                <dsp:tagAttribute name="aria-pressed" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="btnSubmitHWRegRequest"/>
                                                <dsp:tagAttribute name="role" value="button"/>
                                            </dsp:input>
										</div>
									</dsp:form>
								</div>
							</div>
							<dsp:getvalueof var="currentYear" bean="CurrentDate.year"/>
					    	<jsp:useBean id="placeHolderMapCopyRight" class="java.util.HashMap" scope="request"/>
							<c:set target="${placeHolderMapCopyRight}" property="currentYear" value="${currentYear}"/>
							<div class="grid_12 clearfix alpha omega padTop_20">
								<bbbl:textArea key="lbl_hwregistration_footer" language="${pageContext.request.locale.language}" placeHolderMap="${placeHolderMapCopyRight}"/>
							</div>
						</dsp:oparam>
					</dsp:droplet>
				</div>
			</div>
		</div>
	</bbb:pageContainer>
<script>
if (typeof s !== 'undefined') {
	s.pageName='Content Page>Healthy Women'; // pageName
	s.channel='Guides & Advice';
	s.prop1='Content Page';
	s.prop2='Content Page';
	s.prop3='Content Page';
	s.prop4='Guides & Advice';
	s.prop6='${pageContext.request.serverName}'; 
	s.eVar9='${pageContext.request.serverName}';	
	s.server='${pageContext.request.serverName}';
	var s_code = s.t();
	if (s_code)
		document.write(s_code);
}
</script>	
</dsp:page>