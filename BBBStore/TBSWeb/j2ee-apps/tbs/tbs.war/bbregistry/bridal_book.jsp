<dsp:page>
	<%@ page import="com.bbb.constants.BBBCoreConstants"%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/com/bbb/selfservice/StateDroplet" />
	<dsp:importbean var="Profile" bean="/atg/userprofiling/Profile" />
	<dsp:importbean var="BridalBookFormHandler"
		bean="/com/bbb/selfservice/BridalBookFormHandler" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<bbb:pageContainer>
		<jsp:attribute name="pageVariation">br</jsp:attribute>
		<jsp:attribute name="bodyClass">atg_store_pageHome</jsp:attribute>
		<jsp:attribute name="section">registry</jsp:attribute>
		<jsp:attribute name="pageWrapper">requestBridalBook useFB</jsp:attribute>
		<jsp:body>
        <dsp:getvalueof var="contextPath"
				bean="/OriginatingRequest.contextPath" />
				 <dsp:getvalueof id="currentSiteId" bean="Site.id" />
				 <c:set var="BedBathCanadaSite">
					<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
				</c:set>
				<c:choose>
				<c:when test="${not empty shipTo_POBoxOn &&  shipTo_POBoxOn}">
				<c:set var="iclassValue" value=""/>
				</c:when>
				<c:otherwise>
				<c:set var="iclassValue" value="poBoxAddNotAllowed"/>
				</c:otherwise>
				</c:choose>
         <c:set var="bridalBookType"
				value="<%=BBBCoreConstants.BRIDAL_REQUEST_TYPE.TYPE_BRIDAL_BOOK_REGISTRATION %>"
				scope="page" />
         <c:set var="tellAFriendType"
				value="<%=BBBCoreConstants.TELLAFRIEND_REQUEST_TYPE.TYPE_TELL_A_FRIEND %>"
				scope="page" />
            <div id="content" class="container_12 clearfix" role="main">
				<div class="breadcrumbs grid_12">
				<c:set var="lbl_reg_feature_home">
				<bbbl:label key="lbl_reg_feature_home" language="${pageContext.request.locale.language}" />
				</c:set>
					<a href="${contextPath}" title="${lbl_reg_feature_home}"><bbbl:label key="lbl_reg_feature_home" language ="${pageContext.request.locale.language}"/></a>
					<c:choose>
						<c:when test="${pageName eq 'CollegeChecklistPage' || pageType eq 'College'}">
							<a href="${contextPath}/page/College"><bbbl:label key="lbl_reg_feature_college_reg" language ="${pageContext.request.locale.language}"/></a>
						</c:when>
						<c:otherwise>
						
						    <c:choose>
						    <c:when test="${currentSiteId eq 'BedBathUS' || currentSiteId eq 'BedBathCanada'}">
						    <c:set var="lbl_reg_feature_bridal_reg">
							<bbbl:label key="lbl_reg_feature_bridal_reg" language="${pageContext.request.locale.language}" />
							</c:set>
						      <a href="${contextPath}/page/Registry"  title="${lbl_reg_feature_bridal_reg}"><bbbl:label key="lbl_reg_feature_bridal_reg" language ="${pageContext.request.locale.language}"/></a>
						    </c:when>
						    <c:otherwise>
						    <c:set var="lbl_reg_feature_baby_reg">
							<bbbl:label key="lbl_reg_feature_baby_reg" language="${pageContext.request.locale.language}" />
							</c:set>
						      <a href="${contextPath}/page/BabyRegistry"  title="${lbl_reg_feature_baby_reg}"><bbbl:label key="lbl_reg_feature_baby_reg" language ="${pageContext.request.locale.language}"/></a>
						    </c:otherwise>
						    </c:choose>
							
						</c:otherwise>
					</c:choose> 
					 <span class="bold">Bridal Book</span>
				</div>
                <div class="large-3 columns small-medium-right-off-canvas-menu left-nav" role="navigation">
                    <c:import url="/cms/left_navigation.jsp">
                        <c:param name="gridClass" value="grid_3" />
                        <c:param name="selOpt" value="bridalBook" />
                    </c:import>
                </div>
                <div class="small-12 large-9 columns product-grid-container">
                    <div class="small-12 columns">
                    <bbbt:textArea key="txt_bridalbook_header_title"
							language="${pageContext.request.locale.language}" />
                    <bbbt:textArea key="txt_bridalbook_header_content"
							language="${pageContext.request.locale.language}" />
                    <c:if test="${TellAFriendOn}">
                    <bbbt:textArea key="txt_bridalbook_header_tellafriend" language="${pageContext.request.locale.language}" />
					</c:if>
                    <c:if test="${FBOn}">
                    <!--[if IE 7]>
                        <dsp:droplet name="/com/bbb/account/droplet/BBBURLEncodingDroplet">
                            <dsp:param name="URL" value="${pageContext.request.requestURL}?${pageContext.request.queryString}"/>
                            <dsp:oparam name="output">
                                <dsp:getvalueof id="encodedURL" param="encodedURL"/>
                            </dsp:oparam>
                        </dsp:droplet>
                        <dsp:droplet name="/com/bbb/account/droplet/BBBConfigKeysDroplet">
                            <dsp:param name="configKey" value="FBAppIdKeys"/>
                            <dsp:oparam name="output">
                                <dsp:getvalueof var="fbAppIDConfigMap" param="configMap"/>
                            </dsp:oparam>
                        </dsp:droplet>
                        <div class="fb-like">
                            <iframe type="some_value_to_prevent_js_error_on_ie7" title="Facebook Like" src="<bbbc:config key='fb_like_plugin_url' configName='ThirdPartyURLs' />?href=${encodedURL}&amp;send=false&amp;layout=standard&amp;width=350&amp;show_faces=false&amp;action=like&amp;colorscheme=light&amp;font&amp;height=24&amp;appId=${fbConfigMap[currentSiteId]}" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:350px; height:24px;" allowTransparency="true"></iframe>
                        </div>
                    <![endif]-->
                    <!--[if !IE 7]><!-->
                        <!-- <div class="fb-like" data-send="false" data-width="350" data-show-faces="false"></div> -->
                    <!--<![endif]-->
                    </c:if>
                    </div>
                    <div class="small-12 large-8 columns">
                        <dsp:form iclass="form" action="#" id="requestABridalBookForm" method="post">		

							    <dsp:include page="/global/gadgets/errorMessage.jsp">
							      <dsp:param name="formhandler" bean="BridalBookFormHandler"/>
							    </dsp:include>
					
					 <!--	<dsp:input bean="BridalBookFormHandler.bridalBookSuccessURL"
								type="hidden" value="bridal_book.jsp" />
						<dsp:input bean="BridalBookFormHandler.bridalBookErrorURL"
								type="hidden" value="bridal_book.jsp" /> -->
								<dsp:input bean="BridalBookFormHandler.fromPage" type="hidden" value="bridalbook" />
						<dsp:input bean="BridalBookFormHandler.bridalBookVO.type"
								type="hidden" value="${bridalBookType}" />
						 
		        		     		     
                            <div class="small-12 columns no-padding">
                                <div class="inputField clearfix">
                                    <c:set var="firstNamePlaceholder" scope="page">
                                        <bbbl:label key="lbl_bridalbook_firstname" language="${pageContext.request.locale.language}" />
                                        <bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/>
                                    </c:set>
									<dsp:getvalueof var="fName" bean="Profile.firstName"/>
										<c:if test="${!empty fName}">
												<dsp:setvalue bean="BridalBookFormHandler.bridalBookVO.firstName" beanvalue="Profile.firstName"/> 
										</c:if>
									 		<dsp:input bean="BridalBookFormHandler.bridalBookVO.firstName" name="firstName" id="txtFirstName" type="text" >
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtFirstName errortxtFirstName"/>
                                                <dsp:tagAttribute name="placeholder" value="${firstNamePlaceholder}"/>
                                            </dsp:input>
							    </div>
                            </div>
                            <div class="small-12 columns no-padding">
                                
                                <div class="inputField clearfix">
                                    <c:set var="lastNamePlaceholder" scope="page">
                                        <bbbl:label key="lbl_bridalbook_lastname" language="${pageContext.request.locale.language}" />
                                        <bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/>
                                    </c:set>
									<dsp:getvalueof var="lName" bean="Profile.lastName"/>
										<c:if test="${!empty lName}">
												<dsp:setvalue bean="BridalBookFormHandler.bridalBookVO.lastName" beanvalue="Profile.lastName" /> 
											</c:if>
							 		<dsp:input bean="BridalBookFormHandler.bridalBookVO.lastName" name="lastName" id="txtLastName" type="text" >
                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtLastName errortxtLastName"/>
                                        <dsp:tagAttribute name="placeholder" value="${lastNamePlaceholder}"/>
                                    </dsp:input>
                                </div>
                            </div>
                            <div class="small-12 columns no-padding">
                                
                                <div class="inputField clearfix">
                                    <c:set var="addr1Placeholder" scope="page">
                                        <bbbl:label key="lbl_bridalbook_address1" language="${pageContext.request.locale.language}" />
                                        <bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/>
                                    </c:set>									
                                    <dsp:input type="text"
										name="shippingAddress1" id="txtAddress1" iclass="${iclassValue}" 
										bean="BridalBookFormHandler.bridalBookVO.addressLine1" maxlength="30" >
                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtAddress1 errortxtAddress1"/>
                                        <dsp:tagAttribute name="placeholder" value="${addr1Placeholder}"/>
                                    </dsp:input>
                                </div>
                            </div>
                            <div class="small-12 columns no-padding">
                                
                                <div class="inputField clearfix">
                                    <c:set var="addr2Placeholder" scope="page">
                                        <bbbl:label key="lbl_bridalbook_address2" language="${pageContext.request.locale.language}" />
                                    </c:set>
                                    <dsp:input type="text"
										name="shippingAddress2" id="txtAddress2" iclass="${iclassValue}" 
										bean="BridalBookFormHandler.bridalBookVO.addressLine2" maxlength="30" >
                                        <dsp:tagAttribute name="aria-required" value="false"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtAddress2 errortxtAddress2"/>
                                        <dsp:tagAttribute name="placeholder" value="${addr2Placeholder}"/>
                                    </dsp:input>
                                </div>
                            </div>
                            <div class="small-12 columns no-padding">
                                
                                <div class="inputField clearfix">
                                    <c:set var="cityPlaceholder" scope="page">
                                        <bbbl:label key="lbl_bridalbook_city" language="${pageContext.request.locale.language}" />
                                        <bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/>
                                    </c:set>
                                    <dsp:input type="text" name="city"
										id="txtCity" bean="BridalBookFormHandler.bridalBookVO.city" >
                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtCity errortxtCity"/>
                                        <dsp:tagAttribute name="placeholder" value="${cityPlaceholder}"/>
                                    </dsp:input>
                                </div>
                            </div>
                            <div class="small-12 columns no-padding">
                                
                                <div class="inputField width_1">
                                    <dsp:select bean="BridalBookFormHandler.bridalBookVO.state"
										name="stateName" id="stateName" iclass="uniform">
									<dsp:droplet name="StateDroplet">
											<dsp:oparam name="output">
											    <dsp:option>
												    <c:choose>
							                			<c:when test="${currentSiteId eq BedBathCanadaSite}">
							                			    <bbbl:label key="lbl_bridalbook_select_province"
																language="${pageContext.request.locale.language}" /> *
														</c:when>
														<c:otherwise>
															 <bbbl:label key="lbl_bridalbook_select_state"
																language="${pageContext.request.locale.language}" /> *
														</c:otherwise>
													</c:choose>
													
												</dsp:option>
												<dsp:droplet name="ForEach">
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
                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblstateName errorstateName"/>
                                    </dsp:select>
                                    <label id="errorstateName" for="stateName" generated="true" class="error"></label>
                                </div>
                            </div>
                            <div class="small-12 columns no-padding">
                                
                                <div class="inputField clearfix">
                                    
                                   	 <c:choose>
                						<c:when test="${currentSiteId eq BedBathCanadaSite}">
                			     			<c:set var="zipCAPlaceholder" scope="page">
                                                <bbbl:label key="lbl_addcreditcard_postal_code" language="${pageContext.request.locale.language}" />
                                                <bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/>
                                            </c:set>
                                            <dsp:input type="text" name="zipCA" id="txtZip"
												bean="BridalBookFormHandler.bridalBookVO.zipcode" >
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtZip errortxtZip"/>
                                                <dsp:tagAttribute name="placeholder" value="${zipCAPlaceholder}"/>
                                            </dsp:input>
                                    	</c:when>
										<c:otherwise>
											<c:set var="zipUSPlaceholder" scope="page">
                                                <bbbl:label key="lbl_bridalbook_zip" language="${pageContext.request.locale.language}" />
                                                <bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/>
                                            </c:set>
                                            <dsp:input type="text" name="zipUS" id="txtZip"
												bean="BridalBookFormHandler.bridalBookVO.zipcode" >
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtZip errortxtZip"/>
                                                <dsp:tagAttribute name="placeholder" value="${zipUSPlaceholder}"/>
                                            </dsp:input>
		                       		</c:otherwise>
									</c:choose>
			                      
                                </div>
                            </div>
                            <div class="small-12 columns no-padding">
                                <div class="inputPhone phoneFieldWrap clearfix requiredPhone">
										<input placeholder="<bbbl:label key='lbl_bridalbook_phone' language='${pageContext.request.locale.language}' />" id="basePhoneFull" role="textbox" type="text" name="basePhoneFull" class="phoneField required" maxlength="10" aria-required="true" aria-labelledby="lblbasePhoneFull errorbasePhoneFull" />
										
										<dsp:input type="hidden" name="phone" iclass="fullPhoneNum" bean="BridalBookFormHandler.bridalBookVO.phoneNumber" />                            		
										<div class="cb">
											<label id="errorbasePhoneFull" for="basePhoneFull" class="error" generated="true"></label>
										</div>
                                </div>
                            </div>							                                
                            
                            <div class="small-12 columns no-padding">
                                
                                <div class="inputField clearfix">
                                     <c:set var="emailPlaceholder" scope="page">
                                        <bbbl:label key="lbl_bridalbook_email" language="${pageContext.request.locale.language}" />
                                    </c:set>
                                    <dsp:getvalueof var="pEmail" bean="Profile.email"/>
										<c:if test="${!empty pEmail}">
												<dsp:setvalue bean="BridalBookFormHandler.bridalBookVO.emailAddr" beanvalue="Profile.email" /> 
											</c:if>
									 		<dsp:input bean="BridalBookFormHandler.bridalBookVO.emailAddr" name="regEmail" id="regEmail" type="text" >
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblregEmail errorregEmail"/>
                                                <dsp:tagAttribute name="placeholder" value="${emailPlaceholder}"/>
                                            </dsp:input>
						        </div>
                            </div>
                            <div class="small-12 columns no-padding">
                                
                                <div class="inputField clearfix">
                                    <c:choose>
                                        <c:when test="${currentSiteId eq BedBathCanadaSite}">
                                           <c:set var="eventCAPlaceholder" scope="page">
                                                <bbbl:label key="lbl_bridalbook_eventdate" language="${pageContext.request.locale.language}" />
                                                <bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/>
                                            </c:set>
                                            <dsp:input type="text"
                                                name="txtEventDateCA" iclass="required"
                                                id="txtEventDateCA"
                                                bean="BridalBookFormHandler.bridalBookVO.eventDate" date="" >
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtEventDateCA errortxtEventDateCA"/>
                                                <dsp:tagAttribute name="placeholder" value="${eventCAPlaceholder}"/>
                                            </dsp:input>
                                        </c:when>
                                        <c:otherwise>
                                            <c:set var="eventPlaceholder" scope="page">
                                                <bbbl:label key="lbl_bridalbook_eventdate" language="${pageContext.request.locale.language}" />
                                                <bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/>
                                            </c:set>
                                            <dsp:input type="text"
                                                name="txtEventDateUS" iclass="required"
                                                id="txtEventDateUS"
                                                bean="BridalBookFormHandler.bridalBookVO.eventDate" date="" >
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtEventDateUS errortxtEventDateUS"/>
                                                <dsp:tagAttribute name="placeholder" value="${eventPlaceholder}"/>
                                            </dsp:input>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                            <div class="small-12 columns no-padding">
                                
                                <div class="checkboxField">
                                    <dsp:input type="checkbox" iclass="small-1 columns no-padding no-margin"
										name="Name" id="cbPreferredShipping"
										bean="BridalBookFormHandler.bridalBookVO.emailOffer" >
                                        <dsp:tagAttribute name="aria-checked" value="false"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblcbPreferredShipping errorcbPreferredShipping"/>
                                    </dsp:input>
                                    <label id="lblcbPreferredShipping" for="cbPreferredShipping" class="small-11 columns no-padding no-margin">
                                        <bbbl:label key="lbl_bridalbook_email_offer" language="${pageContext.request.locale.language}" />
									</label>
                                </div>
                            </div>
                            <div class="btnApply button_active pushDown clearfix">
                                <dsp:input iclass="small button"
									id="btnSubmitBridalBookRequest" type="submit" value="Submit"
									bean="BridalBookFormHandler.bridalBookRequest" >
                                    <dsp:tagAttribute name="aria-pressed" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="btnSubmitBridalBookRequest"/>
                                    <dsp:tagAttribute name="role" value="button"/>
                                </dsp:input>
                            </div>
                        </dsp:form>
                    </div>
                    <c:set var="onlineimagepath">
						<bbbl:label key="lbl_bridalbook_onlineimage_path"
							language="${pageContext.request.locale.language}" />
					</c:set>					
					<c:set var="onlineimagetarget">
						<bbbl:label key="lbl_bridalbook_onlineimage_target"
							language="${pageContext.request.locale.language}" />
					</c:set>				
					<c:set var="bookId">
						<bbbl:label key="lbl_bridalbook_bookId"
							language="${pageContext.request.locale.language}" />
					</c:set>				
					
                    <div class="small-12 medium-6 large-4 columns promoBrowseBridalBook">
                        <div class="promoBridalBook">
                            <a href="${contextPath}/bbregistry/bridal_book_how_to_book.jsp?bookID=${bookId}" title='<bbbl:label
                            		 key="lbl_bridalbook_online" language="${pageContext.request.locale.language}" />'>
                                <c:choose>
						        	<c:when test="${fn:indexOf(onlineimagepath, 'http') == 0 || fn:indexOf(onlineimagepath, '//') == 0}">
                                        <img src="${onlineimagepath}" title='<bbbl:label key="lbl_bridalbook_online" language="${pageContext.request.locale.language}" />' alt='<bbbl:label key="lbl_bridalbook_online" language="${pageContext.request.locale.language}" />' />
                                    </c:when>
						            <c:otherwise>
                                        <img src="${imagePath}${onlineimagepath}" title='<bbbl:label key="lbl_bridalbook_online" language="${pageContext.request.locale.language}" />' alt='<bbbl:label key="lbl_bridalbook_online" language="${pageContext.request.locale.language}" />' />
                                    </c:otherwise>
								</c:choose>
                            </a>
                        </div>
                        <div class="promoBridalBtn marAuto clearfix">
	                        <div
								class="btnBridalBook button_active pushDown">
	                            <a class="small button" href="${contextPath}/bbregistry/bridal_book_how_to_book.jsp?bookID=${bookId}"><bbbl:label
										key="lbl_bridalbook_online"
										language="${pageContext.request.locale.language}" />
								</a>
	                        </div>
                        </div>
                    </div>
                    <div class="small-12 medium-6 large-12 columns">  
                       <p>
                          <bbbt:textArea key="txtarea_bridal_book_note"
								language="${pageContext.request.locale.language}" /> 
                       </p>                                         
                    </div>
                </div>
            </div>       
       
		<c:set var="lbl_bridalbook_popup_title"> <bbbl:label
					key="lbl_bridalbook_popup_title"
					language="${pageContext.request.locale.language}" />
			</c:set>	
        <div id="thankYouBridalBookDialog" class="hidden"
				title="${lbl_bridalbook_popup_title}">				
				<bbbt:textArea key="txt_bridalbook_confirmation"
					language="${pageContext.request.locale.language}" />            
        </div>
        <c:set var="lbl_tellafriend_page_title">
				<bbbl:label key="lbl_tellafriend_page_title"
					language="${pageContext.request.locale.language}" />
			</c:set>	
        <div id="tellAFriendDialog"
				title="${lbl_tellafriend_page_title}" style="display: none;">
            <dsp:form iclass="form" id="tellAFriendDialogForm"
					action="${contextPath}/bbregistry/tell_A_Friend_json.jsp" method="post">
				<dsp:input bean="BridalBookFormHandler.tellAFriendVO.type"
						type="hidden" value="${tellAFriendType}" />
                <div class="container_6 clearfix">
                    <div class="grid_6 noMar">
                    <bbbt:textArea key="txt_tellafriend_header"
								language="${pageContext.request.locale.language}" />                        
                    </div>
                    <div class="grid_6 noMar">
                        <div class="grid_3 alpha">
                            <h3>
									<bbbl:label key="lbl_tellafriend_from"
										language="${pageContext.request.locale.language}" />
								</h3>
                            
                            <div class="inputField clearfix">
                                <label id="lbltxtFromFirstName" for="txtFromFirstName"><bbbl:label
											key="lbl_bridalbook_firstname"
											language="${pageContext.request.locale.language}" />
											<span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
									</label>
									
									<dsp:getvalueof var="fName" bean="Profile.firstName"/>
										<c:if test="${!empty fName}">
										<dsp:setvalue
										bean="BridalBookFormHandler.tellAFriendVO.senderFirstName"
										beanvalue="Profile.firstName" /> 
										</c:if>
                                    <dsp:input
										bean="BridalBookFormHandler.tellAFriendVO.senderFirstName"
										name="firstName" id="txtFromFirstName" type="text" >
                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtFromFirstName errortxtFromFirstName"/>
                                    </dsp:input>
										                             
                            </div>
                            
                            <div class="inputField clearfix">
                                <label id="lbltxtFromLastName" for="txtFromLastName"><bbbl:label
											key="lbl_bridalbook_lastname"
											language="${pageContext.request.locale.language}" />
											<span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
									</label>
									
									<dsp:getvalueof var="lName" bean="Profile.lastName"/>
										<c:if test="${!empty lName}">
										    <dsp:setvalue bean="BridalBookFormHandler.tellAFriendVO.senderLastName" beanvalue="Profile.lastName" /> 
										</c:if>
									 		<dsp:input
										bean="BridalBookFormHandler.tellAFriendVO.senderLastName"
										name="lastName" id="txtFromLastName" type="text" >
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtFromLastName errortxtFromLastName"/>
                                        </dsp:input>
										
                            </div>
                            
                            <div class="inputField clearfix">
                                <label id="lbltxtFromEmail" for="txtFromEmail"><bbbl:label
											key="lbl_bridalbook_email"
											language="${pageContext.request.locale.language}" />
											<span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
									</label> 
									
									<dsp:getvalueof var="pEmail" bean="Profile.email"/>
										<c:if test="${!empty pEmail}">
										    <dsp:setvalue	bean="BridalBookFormHandler.tellAFriendVO.senderEmailAddr"	beanvalue="Profile.email" /> 
									 	</c:if>
										<dsp:input
										bean="BridalBookFormHandler.tellAFriendVO.senderEmailAddr"
										name="email" id="txtFromEmail" type="text" >
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtFromEmail errortxtFromEmail"/>
                                        </dsp:input>
										
                            </div>
                        </div>
                        <div class="grid_3 omega">
                            <h3>To</h3>
                            
                            <div class="inputField clearfix">
                                <label id="lbltxtToFirstName" for="txtToFirstName"><bbbl:label
											key="lbl_bridalbook_firstname"
											language="${pageContext.request.locale.language}" />
											<span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
									</label>
                                <dsp:input type="text" name="firstName2"
										id="txtToFirstName"
										bean="BridalBookFormHandler.tellAFriendVO.recipientFirstName" >
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtToFirstName errortxtToFirstName"/>
                                </dsp:input>
                            </div>
                            
                            <div class="inputField clearfix">
                                <label id="lbltxtToLastName" for="txtToLastName"><bbbl:label
											key="lbl_bridalbook_lastname"
											language="${pageContext.request.locale.language}" />
											<span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
									</label>
                                <dsp:input type="text" name="lastName2"
										id="txtToLastName"
										bean="BridalBookFormHandler.tellAFriendVO.recipientLastName" >
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtToLastName errortxtToLastName"/>
                                </dsp:input>
                            </div>
                            
                            <div class="inputField clearfix">
                                <label id="lbltxtToEmail" for="txtToEmail">
									<bbbl:label key="lbl_bridalbook_email"
											language="${pageContext.request.locale.language}" />
									<span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
								</label>
                                <dsp:input type="text" name="email2"
										id="txtToEmail"
										bean="BridalBookFormHandler.tellAFriendVO.recipientEmailAddr" >
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtToEmail errortxtToEmail"/>
                                </dsp:input>
                            </div>
                        </div>
                    </div>
                    <div class="grid_6 noMar marTop_20">
                        <div class="grid_3 alpha clearfix">
                            <div class="checkboxField">
                                <dsp:input type="checkbox"
										name="cbSendMeACopyName" id="cbSendMeACopy"
										bean="BridalBookFormHandler.tellAFriendVO.emailCopy" >
                                    <dsp:tagAttribute name="aria-checked" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lblcbSendMeACopy errorcbSendMeACopy"/>
                                </dsp:input>
                                <label id="lblcbSendMeACopy" for="cbSendMeACopy"><span><bbbl:label
												key="lbl_tellafriend_email_copy"
												language="${pageContext.request.locale.language}" />
									</span>
									</label>
                            </div>
                        </div>
                        <div class="grid_3 omega clearfix marTop_10">
                            <div class="button_active">
                                <dsp:input id="btnTellAFriend" iclass="small button"
										type="submit" value="SUBMIT"
										bean="BridalBookFormHandler.tellAFriend" >
                                    <dsp:tagAttribute name="aria-pressed" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="btnTellAFriend"/>
                                    <dsp:tagAttribute name="role" value="button"/>
                                </dsp:input>
                            </div>
                            <a href="#"
									class="small button secondary close-any-dialog" role="link"><bbbl:label
										key="lbl_bridalbook_cancel"
										language="${pageContext.request.locale.language}" />
								</a>
                        </div>
                    </div>
                </div>
            </dsp:form>
        </div>
	<c:set var="lbl_tellafriend_button">
				<bbbl:label key="lbl_tellafriend_button"
					language="${pageContext.request.locale.language}" />
			</c:set>	
        <div id="thankYouTellAFriendDialog" class=""
				title="${lbl_bridalbook_popup_title}" style="display: none;">
            <div class="container_6 clearfix">
                <div class="grid_6 noMar">
				<bbbt:textArea key="txt_tellafriend_confirmation"
							language="${pageContext.request.locale.language}" />                    
                </div>
                <div class="grid_6 noMar">
                    <div class="marTop_20 buttonpane clearfix">
                        <div class="ui-dialog-buttonset clearfix">
                            <div class="button_active">
                                <input id="btnTellAnotherFriend" class="small button"
                                        type="button" value="${lbl_tellafriend_button}" role="button" aria-pressed="false" aria-labelledby="btnTellAnotherFriend" />
                            </div>
                            <a href="#"
                                    class="small button secondary close-any-dialog" role="link"><bbbl:label
                                        key="lbl_bridalbook_close"
                                        language="${pageContext.request.locale.language}" />
                                </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        </jsp:body>
      <jsp:attribute name="footerContent">
          <script type="text/javascript">
         if(typeof s !=='undefined') 
          {
          	s.channel = 'Registry';
			s.pageName='Registry Bridal Book Request';// pagename
			s.prop1='Registry';// page title
			s.prop2='Registry';// category level 1 
			s.prop3='Registry';// category level 2
			s.prop6='${pageContext.request.serverName}'; 
			s.eVar9='${pageContext.request.serverName}';
			var s_code=s.t();
				if(s_code)document.write(s_code);		
          	}
       </script>
   </jsp:attribute>
        
	</bbb:pageContainer>
</dsp:page>