<dsp:page>
	<%@ page import="com.bbb.constants.BBBCoreConstants"%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/com/bbb/selfservice/StateDroplet" />
	<dsp:importbean var="Profile" bean="/atg/userprofiling/Profile" />
	<dsp:importbean var="BabyBookFormHandler" bean="/com/bbb/selfservice/BabyBookFormHandler" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:getvalueof id="servername" idtype="java.lang.String" bean="/OriginatingRequest.servername"/>
	<dsp:getvalueof id="scheme" idtype="java.lang.String" bean="/OriginatingRequest.scheme"/>

	
	
	<bbb:pageContainer>
		<jsp:attribute name="pageVariation">by</jsp:attribute>
		<jsp:attribute name="bodyClass">atg_store_pageHome</jsp:attribute>
		<jsp:attribute name="section">registry</jsp:attribute>
		<jsp:attribute name="pageWrapper">requestBridalBook useFB babyBookLanding</jsp:attribute>
		<jsp:body>
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
         		<c:set var="babyBookType" value="<%=BBBCoreConstants.BABY_BOOK_REQUEST_TYPE.TYPE_BABY_BOOK_REGISTRATION %>" scope="page" />
         		<c:set var="tellAFriendType" value="<%=BBBCoreConstants.TELLAFRIEND_REQUEST_TYPE.TYPE_TELL_A_FRIEND %>" scope="page" />
            <div id="content" class="container_12 clearfix" role="main">

				<div class="breadcrumbs grid_12">
				<c:set var="lbl_reg_feature_home">
				<bbbl:label key="lbl_reg_feature_home" language="${pageContext.request.locale.language}" />
				</c:set>
					<a href="${scheme}://${servername}" title="${lbl_reg_feature_home}"><bbbl:label key="lbl_reg_feature_home" language ="${pageContext.request.locale.language}"/></a> <span class="rightCarrot">
						&gt;
					</span> 
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
					 <span class="rightCarrot">
						&gt;
					</span> <span class="bold"><bbbl:label key="lbl_leftnavguide_babybook" language="${pageContext.request.locale.language}" /></span>
				</div>
                <div class="grid_3">
                	<dsp:include page="/cms/left_navigation.jsp" >
                		<dsp:param name="babySite" value="true"/>
                	</dsp:include>
                </div>
                <div class="grid_9 clearfix">
                    <div class="grid_9 clearfix alpha omega">
	                    <h2><bbbl:label key="lbl_babybook_request" language="${pageContext.request.locale.language}" /></h2>
	                    <p><bbbl:label key="lbl_babybook_info" language="${pageContext.request.locale.language}" />
                        </p>
	                    <c:if test="${TellAFriendOn}">
	                    	<p>
                           		<bbbl:textArea key="txt_babybook_confirmation" language="${pageContext.request.locale.language}" />
                        	</p>
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
                            <div class="fb-like" data-send="false" data-width="350" data-show-faces="false"></div>
                        <!--<![endif]-->
                        </c:if>
                    </div>
                    <div class="grid_4 alpha">
                        <dsp:form iclass="form" action="#" id="requestABridalBookForm" method="post">		

						    <dsp:include page="/global/gadgets/errorMessage.jsp">
						      <dsp:param name="formhandler" bean="BabyBookFormHandler" />
						    </dsp:include>

						 	<!--<dsp:input bean="BabyBookFormHandler.babyBookSuccessURL" type="hidden" value="baby_book.jsp" />
							<dsp:input bean="BabyBookFormHandler.babyBookErrorURL" type="hidden" value="baby_book.jsp" />-->
							<dsp:input bean="BabyBookFormHandler.babyBookVO.type" type="hidden" value="${babyBookType}" />
						 
		        		     		     
                            <div class="grid_4 clearfix alpha omega">
                                <div class="inputField clearfix">
                                    <label id="lbltxtFirstName" for="txtFirstName">
                                    	<bbbl:label key="lbl_babybook_firstname" language="${pageContext.request.locale.language}" />
                                    	<span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
									</label>
									<dsp:getvalueof var="fName" bean="Profile.firstName" />
									<c:if test="${!empty fName}">
										<dsp:setvalue bean="BabyBookFormHandler.babyBookVO.firstName" beanvalue="Profile.firstName" /> 
									</c:if>
									<dsp:input bean="BabyBookFormHandler.babyBookVO.firstName" name="firstName" id="txtFirstName" type="text" >
                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtFirstName errortxtFirstName"/>
                                    </dsp:input>
							    </div>
                            </div>
                            <div class="grid_4 clearfix alpha omega">
                                
                                <div class="inputField clearfix">
                                    <label id="lbltxtLastName" for="txtLastName">
                                    	<bbbl:label key="lbl_babybook_lastname" language="${pageContext.request.locale.language}" />
										<span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
									</label>
									<dsp:getvalueof var="lName" bean="Profile.lastName" />
										<c:if test="${!empty lName}">
											<dsp:setvalue bean="BabyBookFormHandler.babyBookVO.lastName" beanvalue="Profile.lastName" /> 
										</c:if>
							 		<dsp:input bean="BabyBookFormHandler.babyBookVO.lastName" name="lastName" id="txtLastName" type="text" >
                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtLastName errortxtLastName"/>
                                    </dsp:input>
                                </div>
                            </div>
                            <div class="grid_4 clearfix alpha omega">
                                
                                <div class="inputField clearfix">
                                    <label id="lbltxtAddress1" for="txtAddress1"><bbbl:label key="lbl_babybook_address1" language="${pageContext.request.locale.language}" />
											<span Class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
									</label>									
                                    <dsp:input type="text" name="shippingAddress1" iclass="${iclassValue}" id="txtAddress1" bean="BabyBookFormHandler.babyBookVO.addressLine1" maxlength="30" >
                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtAddress1 errortxtAddress1"/>
                                    </dsp:input>
                                </div>
                            </div>
                            <div class="grid_4 clearfix alpha omega">
                                
                                <div class="inputField clearfix">
                                    <label id="lbltxtAddress2" for="txtAddress2"><bbbl:label key="lbl_babybook_address2" language="${pageContext.request.locale.language}" />
									</label>
                                    <dsp:input type="text" name="shippingAddress2" iclass="${iclassValue}" id="txtAddress2" bean="BabyBookFormHandler.babyBookVO.addressLine2" maxlength="30" >
                                        <dsp:tagAttribute name="aria-required" value="false"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtAddress2 errortxtAddress2"/>
                                    </dsp:input>
                                </div>
                            </div>
                            <div class="grid_4 clearfix alpha omega">
                                
                                <div class="inputField clearfix">
                                    <label id="lbltxtCity" for="txtCity"><bbbl:label key="lbl_babybook_city" language="${pageContext.request.locale.language}" />
											<span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
									</label>
                                    <dsp:input type="text" name="city" id="txtCity" bean="BabyBookFormHandler.babyBookVO.city" >
                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtCity errortxtCity"/>
                                    </dsp:input>
                                </div>
                            </div>
                            <div class="grid_2 alpha clearfix">
                                
                                <div class="inputField width_1">
                                    <c:choose>
			                			<c:when test="${currentSiteId eq BedBathCanadaSite}">
			                			    <label id="lblstateName" for="stateName">
                                				   <bbbl:label key="lbl_shipping_new_province" language="${pageContext.request.locale.language}" />
													<span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
											</label>
										</c:when>
										<c:otherwise>
											 <label id="lblstateName" for="stateName">
			                                    <bbbl:label key="lbl_babybook_state" language="${pageContext.request.locale.language}" />
													<span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
												</label>
										</c:otherwise>
									</c:choose>
                                	<dsp:select bean="BabyBookFormHandler.babyBookVO.state" name="stateName" id="stateName" iclass="uniform">
									<dsp:droplet name="StateDroplet">
											<dsp:oparam name="output">
											    <dsp:option>
												    <c:choose>
							                			<c:when test="${currentSiteId eq BedBathCanadaSite}">
							                			    <bbbl:label key="lbl_babybook_select_province" language="${pageContext.request.locale.language}" />
														</c:when>
														<c:otherwise>
															 <bbbl:label key="lbl_bridalbook_select_state" language="${pageContext.request.locale.language}" />
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
                            <div class="grid_2 omega">
                                
                                <div class="inputField clearfix">
                                    
                                   	 <c:choose>
                						<c:when test="${currentSiteId eq BedBathCanadaSite}">
                			     			<label id="lbltxtZip" for="txtZip"><bbbl:label key="lbl_addcreditcard_postal_code" language="${pageContext.request.locale.language}" />
													<span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
											</label>
											 <dsp:input type="text" name="zipCA" id="txtZip" bean="BabyBookFormHandler.babyBookVO.zipcode" >
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtZip errortxtZip"/>
                                            </dsp:input>
                                    	</c:when>
										<c:otherwise>
											<label id="lbltxtZip" for="txtZip"><bbbl:label key="lbl_babybook_zip" language="${pageContext.request.locale.language}" />
													<span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
											</label>
											 <dsp:input type="text" name="zipUS" id="txtZip" bean="BabyBookFormHandler.babyBookVO.zipcode" >
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtZip errortxtZip"/>
                                            </dsp:input>
		                       		</c:otherwise>
									</c:choose>
			                      
                                </div>
                            </div>
                            <div class="grid_4 clearfix alpha omega">
                                <div class="inputPhone phoneFieldWrap clearfix requiredPhone">
                                    <fieldset class="phoneFields"><legend class="phoneFieldLegend"><bbbl:label key='lbl_phonefield_phnumber' language='${pageContext.request.locale.language}'/></legend>
                                    <label id="lblbasePhoneFull" for="basePhoneFull"><bbbl:label key="lbl_babybook_phone" language="${pageContext.request.locale.language}" />
											<span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
									</label>
                                    <input id="basePhoneFull" role="textbox" type="text" name="basePhoneFull" class="phoneField required" maxlength="10" aria-required="true" aria-labelledby="lblbasePhoneFull errorbasePhoneFull" />
									<dsp:input type="hidden" name="phone" iclass="fullPhoneNum" bean="BabyBookFormHandler.babyBookVO.phoneNumber" />                            		
                                    <div class="cb">
                                        <label id="errorbasePhoneFull" for="basePhoneFull" class="error" generated="true"></label>
                                    </div>
                                        </fieldset>
                                </div>
                            </div>							                                
                            
                            <div class="grid_4 clearfix alpha omega">
                                
                                <div class="inputField clearfix">
                                    <label id="lblregEmail" for="regEmail">
										<bbbl:label key="lbl_babybook_email" language="${pageContext.request.locale.language}" />
									</label>
									<dsp:getvalueof var="pEmail" bean="Profile.email" />
										<c:if test="${!empty pEmail}">
												<dsp:setvalue bean="BabyBookFormHandler.babyBookVO.emailAddr" beanvalue="Profile.email" /> 
											</c:if>
									 		<dsp:input bean="BabyBookFormHandler.babyBookVO.emailAddr" name="regEmail" id="regEmail" type="text" >
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblregEmail errorregEmail"/>
                                            </dsp:input>
						        </div>
                            </div>
                            <div class="grid_2 clearfix alpha omega">
                                
                                <div class="inputField clearfix">
                                    <label id="lbltxtEventDateUS" for="txtEventDateUS"><bbbl:label key="lbl_babybook_event_date" language="${pageContext.request.locale.language}" />
									<span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
									</label>
                                    <dsp:input type="text" name="txtEventDateUS" iclass="required" id="txtEventDateUS" bean="BabyBookFormHandler.babyBookVO.eventDate" date="" >
                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtEventDateUS errortxtEventDateUS"/>
                                    </dsp:input>
                                </div>
                            </div>
                            <div class="grid_2 alpha omega buttonWrapper">
                                <div id="btnEventDataButton" class="calendar"></div>
                            </div>
                            <div class="grid_4 cb alpha omega">
                                <label id="errortxtEventDateUS" for="txtEventDateUS" generated="true" class="error"></label>
								
								<label for="txtEventDateUS" class="offScreen"><bbbl:label key="lbl_babybook_event_date" language="${pageContext.request.locale.language}" />
								<span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
								</label>
                            </div>
                            <div class="grid_4 clearfix alpha omega">
                                
                                <div class="checkboxField">
                                    <dsp:input type="checkbox" name="Name" id="cbPreferredShipping" bean="BabyBookFormHandler.babyBookVO.emailOffer" >
                                        <dsp:tagAttribute name="aria-checked" value="false"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblcbPreferredShipping errorcbPreferredShipping"/>
                                    </dsp:input>
                                    <label id="lblcbPreferredShipping" for="cbPreferredShipping" class="">
                                    	<span class="txtBridalBookPromo">
											<bbbl:label key="lbl_babybook_email_offer" language="${pageContext.request.locale.language}" />
										</span>
									</label>
                                </div>
                            </div>
                            <div class="button btnApply button_active pushDown clearfix">
                            <dsp:input bean="BabyBookFormHandler.fromPage" type="hidden" value="babybook" />
                                <dsp:input id="btnSubmitBabyBookRequest" type="submit" value="Submit" bean="BabyBookFormHandler.babyBookRequest" >
                                    <dsp:tagAttribute name="aria-pressed" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="btnSubmitBabyBookRequest"/>
                                    <dsp:tagAttribute name="role" value="button"/>
                                </dsp:input>
                            </div>
                        </dsp:form>
                    </div>
                    <c:set var="onlineimagepath">
						<bbbl:label key="lbl_babybook_onlineimage_path" language="${pageContext.request.locale.language}" />
					</c:set>					
					<c:set var="onlineimagetarget">
						<bbbl:label key="lbl_babybook_onlineimage_target" language="${pageContext.request.locale.language}" />
					</c:set>				
					<c:set var="bookId">
						<bbbl:label key="lbl_babybook_bookId" language="${pageContext.request.locale.language}" />
					</c:set>				
					
                    <div class="fl prefix_1 alpha omega clearfix promoBrowseBabyBook">
                        <div class="promoBabyBook">
                            <a class="launchBabyBookDemo" href="${contextPath}/bbregistry/baby_book_how_to_book.jsp?bookID=${bookId}" title='<bbbl:label key="lbl_babybook_browse" language="${pageContext.request.locale.language}" />'>
                                <c:set var="onlineimagepath" value ="${fn:trim(onlineimagepath)}"/>
                                <c:choose>
						        	<c:when test="${(fn:indexOf(onlineimagepath, 'http') == 0) || (fn:indexOf(onlineimagepath, '//') == 0)}">
										<img src="${onlineimagepath}" title='<bbbl:label key="lbl_babybook_browse" language="${pageContext.request.locale.language}" />' alt='<bbbl:label key="lbl_babybook_browse" language="${pageContext.request.locale.language}" />'/>
						            </c:when>
						        	<c:when test="${(fn:indexOf(onlineimagepath, '/_assets/') != -1)}">
										<img src="${imagePath}/${onlineimagepath}" title='<bbbl:label key="lbl_babybook_browse" language="${pageContext.request.locale.language}" />' alt='<bbbl:label key="lbl_babybook_browse" language="${pageContext.request.locale.language}" />'/>
						            </c:when>
						            <c:otherwise>
										<img src="${scene7Path}/${onlineimagepath}" title='<bbbl:label key="lbl_babybook_browse" language="${pageContext.request.locale.language}" />' alt='<bbbl:label key="lbl_babybook_browse" language="${pageContext.request.locale.language}" />'/>
						            </c:otherwise>
								</c:choose>
                            </a>
                        </div>
                        <div class="promoBabyBtn marAuto clearfix">
	                        <div class="btnBabyBook button button_active pushDown">
	                            <a class="launchBabyBookDemo" href="${contextPath}/bbregistry/baby_book_how_to_book.jsp?bookID=${bookId}" ><bbbl:label key="lbl_babybook_browse" language="${pageContext.request.locale.language}" /></a>
	                        </div>
                        </div>
                    </div>
                    <div class="grid_9 clearfix alpha omega">  
                       <p>
                          <bbbt:textArea key="txtarea_baby_book_note" language="${pageContext.request.locale.language}" /> 
                       </p>                                         
                    </div>
                </div>
            </div>       
       
		<c:set var="lbl_babybook_popup_title"> <bbbl:label key="lbl_bridalbook_popup_title" language="${pageContext.request.locale.language}" />
			</c:set>	
        <div id="thankYouBridalBookDialog" class="hidden" title="${lbl_babybook_popup_title}">				
				<bbbt:textArea key="txt_babybook_confirmation" language="${pageContext.request.locale.language}" />            
        </div>
        <c:set var="lbl_tellafriend_page_title">
				<bbbl:label key="lbl_tellafriend_page_title" language="${pageContext.request.locale.language}" />
			</c:set>	
        <div id="tellAFriendDialog" title="${lbl_tellafriend_page_title}" style="display: none;">
            <dsp:form iclass="form" id="tellAFriendDialogForm" action="${contextPath}/bbregistry/tell_a_Friend_baby_json.jsp" method="post">
				<dsp:input bean="BabyBookFormHandler.tellAFriendVO.type" type="hidden" value="${tellAFriendType}" />
                <div class="container_6 clearfix">
                    <div class="grid_6 noMar">
                    <bbbt:textArea key="txt_tellafriend_header2" language="${pageContext.request.locale.language}" />                        
                    </div>
                    <div class="grid_6 noMar">
                        <div class="grid_3 alpha">
                            <h3>
									<bbbl:label key="lbl_tellafriend_from" language="${pageContext.request.locale.language}" />
								</h3>
                            
                            <div class="inputField clearfix">
                                <label id="lbltxtFromFirstName" for="txtFromFirstName"><bbbl:label key="lbl_babybook_firstname" language="${pageContext.request.locale.language}" />
											<span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
									</label>
									
									<dsp:getvalueof var="fName" bean="Profile.firstName" />
										<c:if test="${!empty fName}">
										<dsp:setvalue bean="BabyBookFormHandler.tellAFriendVO.senderFirstName" beanvalue="Profile.firstName" /> 
										</c:if>
									 		<dsp:input bean="BabyBookFormHandler.tellAFriendVO.senderFirstName" name="firstName" id="txtFromFirstName" type="text" >
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtFromFirstName errortxtFromFirstName"/>
                                            </dsp:input>
										                             
                            </div>
                            
                            <div class="inputField clearfix">
                                <label id="lbltxtFromLastName" for="txtFromLastName"><bbbl:label key="lbl_babybook_lastname" language="${pageContext.request.locale.language}" />
											<span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
									</label>
									
									<dsp:getvalueof var="lName" bean="Profile.lastName" />
										<c:if test="${!empty lName}">
										    <dsp:setvalue bean="BabyBookFormHandler.tellAFriendVO.senderLastName" beanvalue="Profile.lastName" /> 
										</c:if>
									 		<dsp:input bean="BabyBookFormHandler.tellAFriendVO.senderLastName" name="lastName" id="txtFromLastName" type="text" >
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtFromLastName errortxtFromLastName"/>
                                            </dsp:input>
										
                            </div>
                            
                            <div class="inputField clearfix">
                                <label id="lbltxtFromEmail" for="txtFromEmail"><bbbl:label key="lbl_babybook_email" language="${pageContext.request.locale.language}" />
											<span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
									</label> 
									
									<dsp:getvalueof var="pEmail" bean="Profile.email" />
										<c:if test="${!empty pEmail}">
										    <dsp:setvalue bean="BabyBookFormHandler.tellAFriendVO.senderEmailAddr" beanvalue="Profile.email" /> 
									 	</c:if>
										<dsp:input bean="BabyBookFormHandler.tellAFriendVO.senderEmailAddr" name="email" id="txtFromEmail" type="text" >
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtFromEmail errortxtFromEmail"/>
                                        </dsp:input>
										
                            </div>
                        </div>
                        <div class="grid_3 omega">
                            <h3>To</h3>
                            
                            <div class="inputField clearfix">
                                <label id="lbltxtToFirstName" for="txtToFirstName"><bbbl:label key="lbl_babybook_firstname" language="${pageContext.request.locale.language}" />
											<span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
									</label>
                                <dsp:input type="text" name="firstName2" id="txtToFirstName" bean="BabyBookFormHandler.tellAFriendVO.recipientFirstName" >
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtToFirstName errortxtToFirstName"/>
                                </dsp:input>
                            </div>
                            
                            <div class="inputField clearfix">
                                <label id="lbltxtToLastName" for="txtToLastName"><bbbl:label key="lbl_babybook_lastname" language="${pageContext.request.locale.language}" />
											<span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
									</label>
                                <dsp:input type="text" name="lastName2" id="txtToLastName" bean="BabyBookFormHandler.tellAFriendVO.recipientLastName" >
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtToLastName errortxtToLastName"/>
                                </dsp:input>
                            </div>
                            
                            <div class="inputField clearfix">
                                <label id="lbltxtToEmail" for="txtToEmail">
									<bbbl:label key="lbl_babybook_email" language="${pageContext.request.locale.language}" />
									<span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
								</label>
                                <dsp:input type="text" name="email2" id="txtToEmail" bean="BabyBookFormHandler.tellAFriendVO.recipientEmailAddr" >
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtToEmail errortxtToEmail"/>
                                </dsp:input>
                            </div>
                        </div>
                    </div>
                    <div class="grid_6 noMar marTop_20">
                        <div class="grid_3 alpha clearfix">
                            <div class="checkboxField">
                                <dsp:input type="checkbox" name="cbSendMeACopyName" id="cbSendMeACopy" bean="BabyBookFormHandler.tellAFriendVO.emailCopy" >
                                    <dsp:tagAttribute name="aria-checked" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lblcbSendMeACopy errorcbSendMeACopy"/>
                                </dsp:input>
                                <label id="lblcbSendMeACopy" for="cbSendMeACopy"><span><bbbl:label key="lbl_tellafriend_email_copy" language="${pageContext.request.locale.language}" />
									</span>
									</label>
                            </div>
                        </div>
                        <div class="grid_3 omega clearfix marTop_10">
                            <div class="button button_active">
                                <dsp:input id="btnTellAFriend" type="submit" value="SUBMIT" bean="BabyBookFormHandler.tellAFriend" >
                                    <dsp:tagAttribute name="aria-pressed" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="btnTellAFriend"/>
                                    <dsp:tagAttribute name="role" value="button"/>
                                </dsp:input>
                            </div>
                            <a href="#" class="buttonTextLink close-any-dialog" role="link"><bbbl:label key="lbl_babybook_cancel" language="${pageContext.request.locale.language}" />
								</a>
                        </div>
                    </div>
                </div>
            </dsp:form>
        </div>
		<c:set var="lbl_tellafriend_button">
				<bbbl:label key="lbl_tellafriend_button" language="${pageContext.request.locale.language}" />
		</c:set>	
        <div id="thankYouTellAFriendDialog" class="" title="${lbl_babybook_popup_title}" style="display: none;">
            <div class="container_6 clearfix">
                <div class="grid_6 noMar">
				<bbbt:textArea key="txt_tellafriend_confirmation" language="${pageContext.request.locale.language}" />                    
                </div>
                <div class="grid_6 noMar">
                    <div class="marTop_20 buttonpane clearfix">
                        <div class="ui-dialog-buttonset clearfix">
                            <div class="button button_active">
                                <input id="btnTellAnotherFriend" type="button" value="${lbl_tellafriend_button}" role="button" aria-pressed="false" aria-labelledby="btnTellAnotherFriend" />
                            </div>
                            <a href="#" class="buttonTextLink close-any-dialog" role="link"><bbbl:label key="lbl_babybook_close" language="${pageContext.request.locale.language}" />
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
			s.pageName='Registry Baby Book';// pagename
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