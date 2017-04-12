<dsp:importbean bean="/com/bbb/commerce/browse/droplet/StatesLookup" />

<dsp:page>

	<bbb:pageContainer>
		<jsp:attribute name="section">accounts</jsp:attribute>
		<jsp:attribute name="pageWrapper">manageRegistry useCertonaJs myAccount</jsp:attribute>
		<jsp:attribute name="PageType">MyRegistry</jsp:attribute>
		<jsp:body>
			<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
			<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
			<dsp:importbean bean="/atg/multisite/Site" />
			<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
			<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/MyRegistriesDisplayDroplet" />
			<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />
			<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
			<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/DateCalculationDroplet" />

			<dsp:getvalueof var="appid" bean="Site.id" />
	        <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />

			<div id="content" class="container_12 clearfix manageRegistry">				
				<div class="grid_12">
					<h1 class="account fl">
						<bbbl:label key="lbl_regsrchguest_my_account" language="${pageContext.request.locale.language}" />
					</h1>
					<h3 class="subtitle fl">
						<bbbl:label key="lbl_regsrchguest_registries" language="${pageContext.request.locale.language}" />
					</h3>
				
					<div id="chatModal" class="fr marTop_20">
                		<div id="chatModalDialogs"></div>
							<dsp:include page="/common/click2chatlink.jsp">
                    			<dsp:param name="pageId" value="2" />
                        	</dsp:include>							
                       		<br>
                	</div>
                </div>
                <div class="grid_2">				
					<c:import url="/account/left_nav.jsp">
						<c:param name="currentPage">
							<bbbl:label key="lbl_myaccount_registries" language="${pageContext.request.locale.language}" />
						</c:param>
					</c:import>
				</div>
                 
				<dsp:droplet name="MyRegistriesDisplayDroplet">
					<dsp:param name="profile" bean="/atg/userprofiling/Profile" />
					<dsp:param name="siteId" value="${appid}" />
					<dsp:oparam name="output">
							<dsp:getvalueof param="registryCount" var="registryCount" />
							<dsp:getvalueof param="registrySummaryVO" var="registrySummaryVO" />
							<dsp:getvalueof param="priRegSummVO" var="priRegSummVO" />
							<dsp:getvalueof param="coRegSummVO" var="coRegSummVO" />
							<dsp:getvalueof param="priRegListSize" var="priRegListSize" />
							<dsp:getvalueof param="coRegListSize" var="coRegListSize" />
						<div class="grid_9 prefix_1">
							<table class="simpleTable registryHeader noMarTop">
								<c:if test="${registryCount >=1}">
								<thead>
									<tr>
										<th class="thCol1" tabindex="0"><bbbl:label key="lbl_regsrchguest_EventType"	language="${pageContext.request.locale.language}" /></th>
										<th class="thCol2" tabindex="0"><bbbl:label key="lbl_regsrchguest_CoRegistrant" language="${pageContext.request.locale.language}" /></th>
										<th class="thCol3" tabindex="0"><bbbl:label key="lbl_regsrchguest_EventDate"	language="${pageContext.request.locale.language}" /></th>                                       
										<th class="thCol4" tabindex="0"><bbbl:label key="lbl_regsrchguest_GiftRegPur" language="${pageContext.request.locale.language}" /></th>
										<th class="thCol5" tabindex="0"><bbbl:label key="lbl_regsrchguest_Registry" language="${pageContext.request.locale.language}" /></th>
										<th class="thCol6" tabindex="0"></th>
									</tr>
								</thead>
								</c:if>
								<dsp:droplet name="ForEach">
								<dsp:param name="array" param="registrySummaryVO" />
								<dsp:oparam name="output">
									<dsp:getvalueof param="element.eventType" var="eventType" />
									<dsp:getvalueof param="element.registryId" var="regId" />
									<dsp:getvalueof var="daysToGo" param="daysToGo"/>
									<dsp:getvalueof var="coRegistrantFullName" param="element.coRegistrantFullName" />
									<c:set var="daysToGoPreText" value=""/>
						            <c:set var="daysToGoText" value=""/>
									<dsp:getvalueof var="daysToGo" value=""/>
									<dsp:droplet name="DateCalculationDroplet">
						            <dsp:param name="eventDate" param="element.eventDate"/>
						            <dsp:oparam name="output">
						            	<dsp:droplet name="Switch">
						            	<dsp:param name="value" param="check" />
						               	<dsp:oparam name="true">
										
						               	<c:choose>
					                		<c:when test="${not empty appid && appid eq 'BuyBuyBaby'}">
	                						<c:choose>
						                		<c:when test="${not empty eventType && eventType eq 'Baby'}">
						                			<dsp:getvalueof var="daysToGo" param="daysToGo"/>
						                			<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_baby_arrives' language="${pageContext.request.locale.language}" /></c:set>
						                		</c:when>
						                		<c:when test="${not empty eventType && eventType eq 'Birthday'}">
						                			<dsp:getvalueof var="daysToGo" param="daysToGo"/>
						                			<c:set var="daysToGoPreText"><bbbl:label key='lbl_regflyout_daystogo_only' language="${pageContext.request.locale.language}" /></c:set>
						                			<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_daystogo' language="${pageContext.request.locale.language}" /></c:set>
						                		</c:when>
			                					<c:otherwise>
			                					</c:otherwise>
		                					</c:choose>
					                		</c:when>
					                		
					                		<c:otherwise>
						                	<c:choose>
						                		<c:when test="${not empty eventType && eventType eq 'Wedding'}">
							                		<dsp:getvalueof var="daysToGo" param="daysToGo"/>
							                		<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_day_until_wedding' language="${pageContext.request.locale.language}" /></c:set>
			                					</c:when>
			                					<c:when test="${not empty eventType && eventType eq 'Baby'}">
							                		<dsp:getvalueof var="daysToGo" param="daysToGo"/>
							                		<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_day_until_baby' language="${pageContext.request.locale.language}" /></c:set>
			                					</c:when>
												<c:when test="${not empty eventType && eventType eq 'Birthday'}">
						                			<dsp:getvalueof var="daysToGo" param="daysToGo"/>
						                			<c:set var="daysToGoPreText"><bbbl:label key='lbl_regflyout_daystogo_only' language="${pageContext.request.locale.language}" /></c:set>
						                			<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_daystogo' language="${pageContext.request.locale.language}" /></c:set>
			                					</c:when>
												<c:when test="${not empty eventType && eventType eq 'Retirement'}">
							                		<dsp:getvalueof var="daysToGo" param="daysToGo"/>
							                		<c:set var="daysToGoPreText"><bbbl:label key='lbl_regflyout_daystogo_only' language="${pageContext.request.locale.language}" /></c:set>
							                		<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_daystogo' language="${pageContext.request.locale.language}" /></c:set>
			                					</c:when>
			                					<c:when test="${not empty eventType && eventType eq 'Anniversary'}">
							                		<dsp:getvalueof var="daysToGo" param="daysToGo"/>
							                		<c:set var="daysToGoPreText"><bbbl:label key='lbl_regflyout_daystogo_only' language="${pageContext.request.locale.language}" /></c:set>
							                		<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_daystogo' language="${pageContext.request.locale.language}" /></c:set>
			                					</c:when>
			                					<c:when test="${not empty eventType && eventType eq 'Housewarming'}">
							                		<dsp:getvalueof var="daysToGo" param="daysToGo"/>
							                		<c:set var="daysToGoPreText"><bbbl:label key='lbl_regflyout_daystogo_only' language="${pageContext.request.locale.language}" /></c:set>
							                		<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_daystogo' language="${pageContext.request.locale.language}" /></c:set>
			                					</c:when>
			                					<c:when test="${not empty eventType && eventType eq 'Commitment Ceremony'}">
							                		<dsp:getvalueof var="daysToGo" param="daysToGo"/>
							                		<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_day_until_wedding' language="${pageContext.request.locale.language}" /></c:set>
			                					</c:when>
			                					<c:when test="${not empty eventType && eventType eq 'College'}">
							                		<dsp:getvalueof var="daysToGo" param="daysToGo"/>
							                		<c:set var="daysToGoPreText"><bbbl:label key='lbl_regflyout_daystogo_only' language="${pageContext.request.locale.language}" /></c:set>
							                		<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_daystogo' language="${pageContext.request.locale.language}" /></c:set>
			                					</c:when>
			                					<c:when test="${not empty eventType && eventType eq 'University'}">
							                		<dsp:getvalueof var="daysToGo" param="daysToGo"/>
							                		<c:set var="daysToGoPreText"><bbbl:label key='lbl_regflyout_daystogo_only' language="${pageContext.request.locale.language}" /></c:set>
							                		<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_daystogo' language="${pageContext.request.locale.language}" /></c:set>
			                					</c:when>
			                					<c:when test="${not empty eventType && eventType eq 'College/University'}">
							                		<dsp:getvalueof var="daysToGo" param="daysToGo"/>
							                		<c:set var="daysToGoPreText"><bbbl:label key='lbl_regflyout_daystogo_only' language="${pageContext.request.locale.language}" /></c:set>
							                		<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_daystogo' language="${pageContext.request.locale.language}" /></c:set>
			                					</c:when>
		                					</c:choose>
					                		</c:otherwise>
					                	</c:choose>
							            </dsp:oparam>
							            <dsp:oparam name="false">
							            <c:choose>
							            	<c:when test="${not empty appid && appid eq 'BuyBuyBaby'}">
	                						<c:choose>
						                		<c:when test="${not empty eventType && eventType eq 'Baby'}">
						                			<dsp:getvalueof var="daysToGo" param="daysToGo"/>
						                			<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_days_since_life' language="${pageContext.request.locale.language}" /></c:set>
						                		</c:when>
						                		<c:when test="${not empty eventType && eventType eq 'Birthday'}">
						                			<dsp:getvalueof var="daysToGo" param="daysToNextCeleb"/>
						                			<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_days_next_celeb' language="${pageContext.request.locale.language}" /></c:set>
						                		</c:when>
			                					<c:otherwise>
			                					</c:otherwise>
		                					</c:choose>
					                		</c:when>
		                					<c:otherwise>
						                	<c:choose>
						                		<c:when test="${not empty eventType && eventType eq 'Wedding'}">
							                		<dsp:getvalueof var="daysToGo" param="daysToGo"/>
							                		<c:set var="daysToGoPreText"><bbbl:label key='lbl_regflyout_daystogo_day' language="${pageContext.request.locale.language}" /></c:set>
							                		<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_happy_ever_after' language="${pageContext.request.locale.language}" /></c:set>
			                					</c:when>
			                					<c:when test="${not empty eventType && eventType eq 'Baby'}">
							                		<dsp:getvalueof var="daysToGo" param="daysToGo"/>
							                		<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_days_since_life' language="${pageContext.request.locale.language}" /></c:set>
			                					</c:when>
			                					<c:when test="${not empty eventType && eventType eq 'Birthday'}">
							                		<dsp:getvalueof var="daysToGo" param="daysToNextCeleb"/>
							                		<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_days_next_celeb' language="${pageContext.request.locale.language}" /></c:set>
			                					</c:when>
			                					<c:when test="${not empty eventType && eventType eq 'Retirement'}">
							                		<dsp:getvalueof var="daysToGo" param="daysToGo"/>
													<c:set var="daysToGoPreText"><bbbl:label key='lbl_regflyout_daystogo_day' language="${pageContext.request.locale.language}" /></c:set>
							                		<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_rest_of_life' language="${pageContext.request.locale.language}" /></c:set>
			                					</c:when>
			                					<c:when test="${not empty eventType && eventType eq 'Anniversary'}">
							                		<dsp:getvalueof var="daysToGo" param="daysToNextCeleb"/>
							                		<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_another_year' language="${pageContext.request.locale.language}" /></c:set>
			                					</c:when>
			                					<c:when test="${not empty eventType && eventType eq 'Housewarming'}">
							                		<dsp:getvalueof var="daysToGo" param="daysToGo"/>
							                		<c:set var="daysToGoPreText"><bbbl:label key='lbl_regflyout_daystogo_day' language="${pageContext.request.locale.language}" /></c:set>
							                		<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_new_place' language="${pageContext.request.locale.language}" /></c:set>
			                					</c:when>
			                					<c:when test="${not empty eventType && eventType eq 'Commitment Ceremony'}">
							                		<dsp:getvalueof var="daysToGo" param="daysToGo"/>
							                		<c:set var="daysToGoPreText"><bbbl:label key='lbl_regflyout_daystogo_day' language="${pageContext.request.locale.language}" /></c:set>
							                		<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_happy_ever_after' language="${pageContext.request.locale.language}" /></c:set>
			                					</c:when>
			                				</c:choose>
			                				</c:otherwise>
			                			</c:choose>	
			                			</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
			                		</dsp:droplet>
									<tbody>
									<tr>
										<td class="tdCol1" tabindex="0"><span class="visuallyhidden"><bbbl:label key="lbl_regsrchguest_EventType"	language="${pageContext.request.locale.language}" /></span><dsp:valueof param="element.eventType" /></td>
										<td class="tdCol2" tabindex="0"><span class="visuallyhidden"><bbbl:label key="lbl_regsrchguest_CoRegistrant" language="${pageContext.request.locale.language}" /></span>
										<%--Fixing AC-37 accessibility issue for displaying No Coregistrant changes start--%>
										<c:set var="coRegistrantFullNameTrim" value="${fn:trim(coRegistrantFullName)}" />
											<c:choose>
												<c:when test="${not empty coRegistrantFullNameTrim}">
													<dsp:valueof param="element.coRegistrantFullName" /> 
												</c:when>
												<c:otherwise>
													<span class="visuallyhidden"><bbbl:label key="lbl_regsrchguest_CoRegistrantEmpty" language="${pageContext.request.locale.language}" /></span>
												</c:otherwise>
											</c:choose> 
										</td>
										<%--Fixing AC-37 accessibility issue for displaying No Coregistrant changes start--%>
										<dsp:getvalueof param="element.eventDate" var="eventDateCheck" />
										<c:if test="${eventDateCheck != null}">
										<td class="eventDate tdCol3" tabindex="0">
											<span class="visuallyhidden"><bbbl:label key="lbl_regsrchguest_EventDate"	language="${pageContext.request.locale.language}" /></span>
											<c:choose>
										    	<c:when test="${appid eq 'BedBathCanada'}">
										      		<dsp:valueof param="element.eventDateCanada" />
										       	</c:when>
										     	<c:otherwise>
										     		<dsp:valueof param="element.eventDate" />
										     	</c:otherwise>
										    </c:choose>
											<c:if test="${not empty daysToGo}">
												<span><bbbl:label key="lbl_regsrchguest_bracOpen" language ="${pageContext.request.locale.language}"/>
												<c:if test="${not empty daysToGoPreText}">
													<span>${daysToGoPreText}</span>
												</c:if>
												<span>${daysToGo}</span>
												<c:if test="${not empty daysToGoText}">
													<span>${daysToGoText}</span>
												</c:if>
												<bbbl:label key="lbl_regsrchguest_bracClose" language ="${pageContext.request.locale.language}"/></span>
											</c:if>
										</td>
										</c:if>
										<td class="tdCol4" tabindex="0"><span class="visuallyhidden"><bbbl:label key="lbl_regsrchguest_GiftRegPur" language="${pageContext.request.locale.language}" /></span><dsp:valueof param="element.giftRegistered" /> / <dsp:valueof param="element.giftPurchased" /></td>
										<td class="tdCol5" tabindex="0"><span class="visuallyhidden"><bbbl:label key="lbl_regsrchguest_Registry" language="${pageContext.request.locale.language}" /></span><dsp:valueof	param="element.registryId" /></td>
									   	<td class="textRight tdCol6" tabindex="0">
									   		<dsp:a href="view_registry_owner.jsp" bean="GiftRegistryFormHandler.viewEditRegistry" value=""  iclass="allCaps" requiresSessionConfirmation="false">
												<dsp:param name="registryId" value="${regId}" />
												<dsp:param name="eventType" value="${eventType}" />
                                               <span class="txtOffScreen">Edit my ${eventType} registry .   </span>
												<strong><bbbl:label key="lbl_regflyout_view_edit" language="${pageContext.request.locale.language}" /></strong>
											</dsp:a>
										</td>
									</tr>                            
									</tbody>
						   </dsp:oparam>	
			               	<dsp:oparam name="error">
							<dsp:getvalueof param="errorMsg" var="errorMsg" />
							<div class="grid_9 prefix_3">
							<div class="error bold">
								<bbbe:error key="${errorMsg}" language="${pageContext.request.locale.language}" />
							</div>
							</div>
							</dsp:oparam> 		

								</dsp:droplet>
								</table>

					</dsp:oparam>
			        </dsp:droplet>
						
					
					
							<div class="grid_4 alpha">
								<c:choose>
									<c:when test="${registryCount ==0}">
										<bbbt:textArea key="txt_create_a_registry" language="${pageContext.request.locale.language}" />	
									</c:when>
									<c:otherwise>
										<bbbt:textArea key="txt_another_registry" language="${pageContext.request.locale.language}" />								
									</c:otherwise>
								</c:choose> 
								<div class="input width_2">
									<div class="select">
										<dsp:include page="/giftregistry/my_registry_type_select.jsp" />
									</div>
								</div>
							</div>
                
							<div class="grid_4 whyRegister omega">
								<bbbt:textArea key="txt_why_registry" language="${pageContext.request.locale.language}" />
							</div>	
							
							<div class="clear"></div>
							
						<div class="findOldRegWrapper grid_9 section alpha marTop_20">
							<div class="spacer clearfix">
								<h3 class="lookingOldRegistry"><bbbl:label key="lbl_regflyout_looking_old_reg" language="${pageContext.request.locale.language}" /></h3>
								<p class="noMar marTop_5 marBottom_10">&nbsp;<bbbl:label key="lbl_regflyout_if_created"	language="${pageContext.request.locale.language}" /></p>
								<div class="clearfix " id="findForm">
									<dsp:form action="#" id="findOldRegistry" method="post">
									<h3 class="findOldRegistry"><bbbl:label key='lbl_regsearch_lnktxt' language="${pageContext.request.locale.language}" /></h3>
                                    <div class="clearfix marTop_5">
										<div class="grid_2 alpha omega">
											<fieldset>
											<legend class="hidden"><bbbl:label key="lbl_regflyout_looking_old_reg" language="${pageContext.request.locale.language}"/></legend>
												<div class="input width_2 alpha">
													<div class="label">
														<label id="lblregistryFirstName" for="registryFirstName"> <bbbl:label key='lbl_reg_firstname' language="${pageContext.request.locale.language}" /><span class="txtOffScreen"><bbbl:label key='lbl_enter_first_name' language="${pageContext.request.locale.language}" /></span></label>
													</div>
													<c:set var="titleFirstName">
						                            <bbbl:label key="lbl_registrants_firstname" language="${pageContext.request.locale.language}" />
					                                </c:set>
													<div class="text">
														<dsp:input type="text" name="registryFirstName" title="${titleFirstName}" id="registryFirstName" bean="GiftRegistryFormHandler.registrySearchVO.firstName" >
                                                            <dsp:tagAttribute name="aria-required" value="false"/>
                                                            <dsp:tagAttribute name="aria-labelledby" value="lblregistryFirstName errorregistryFirstName"/>
															<dsp:tagAttribute name="placeholder" value="${titleFirstName}"/>
                                                        </dsp:input>
													</div>
												</div>

												<div class="input width_2 alpha">
													<div class="label">
														<label id="lblregistryLastName" for="registryLastName"> <bbbl:label key='lbl_reg_lastname' language="${pageContext.request.locale.language}" /><span class="txtOffScreen"><bbbl:label key='lbl_enter_last_name' language="${pageContext.request.locale.language}" /></span></label>
													</div>
													<c:set var="titleLastName">
						                            <bbbl:label key="lbl_registrants_lastname" language="${pageContext.request.locale.language}" />
					                                </c:set>
													<div class="text">
														<dsp:input type="text" name="registryLastName" title="${titleLastName}" id="registryLastName" bean="GiftRegistryFormHandler.registrySearchVO.lastName" >
                                                            <dsp:tagAttribute name="aria-required" value="false"/>
                                                            <dsp:tagAttribute name="aria-labelledby" value="lblregistryLastName errorregistryLastName"/>
							    <dsp:tagAttribute name="placeholder" value="${titleLastName}"/>
                                                        </dsp:input>
													</div>
												</div>
												
												<div class="input selectBox width_2 alpha">		
													<div class="label">
														<label id="lblstateName" for="stateName">
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
														</label>
													</div>		
													<div class="">
													<dsp:select	bean="GiftRegistryFormHandler.registrySearchVO.state"
																name="bbRegistryState" 
																id="stateName" 
																iclass="uniform">
								                         <dsp:option value="">
												<bbbl:label key="lbl_addressBook_select_state" language="${pageContext.request.locale.language}" />
											</dsp:option>
															<dsp:droplet name="/com/bbb/selfservice/StateDroplet">
														        <dsp:param name="NoShowUSTerr" value="noShowOnRegistry" />
																<dsp:oparam name="output">																	
																	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																	   <dsp:param name="array" param="location" />
																	   	<dsp:oparam name="output">
																	   	<dsp:getvalueof param="element.stateName" id="stateName" />
																	   	<dsp:getvalueof param="element.stateCode" id="stateCode" />
																	   	<dsp:option value="${stateCode}" >
																			${stateName}
																	   	</dsp:option>														
																		</dsp:oparam>
																	</dsp:droplet>
																</dsp:oparam>
													   		</dsp:droplet>
									                   		<dsp:tagAttribute name="aria-required" value="true"/>
									                        <dsp:tagAttribute name="aria-labelledby" value="lblstateName errorstateName"/>
														</dsp:select>
														
													</div>
													<div class="error"></div>
												</div>
												
												
												
											</fieldset>
										</div>
										<div class="grid_1 optionOr">
											<p class="seperator"></p>
											<p class="or"><bbbl:label key='lbl_regsearch_or' language="${pageContext.request.locale.language}" /></p>
											<p class="seperator"></p>
										</div>
										<div class="grid_2 alpha omega">
											<fieldset>
											<legend class="hidden"><bbbl:label key='lbl_regsearch_email' language="${pageContext.request.locale.language}" /></legend>
												<div class="input width_2 alpha">
													<div class="label">
														<label id="lblregistryEmail" for="registryEmail"> <bbbl:label key='lbl_regsearch_email' language="${pageContext.request.locale.language}" /></label>
													</div>
													<c:set var="titleregistryemail">
						<bbbl:label key='lbl_regsearch_email' language="${pageContext.request.locale.language}" />
					</c:set>
													<div class="text">
														<dsp:input type="text" name="registryEmail" title="${titleregistryemail}" id="registryEmail" bean="GiftRegistryFormHandler.registrySearchVO.email" >
                                                            <dsp:tagAttribute name="aria-required" value="false"/>
                                                            <dsp:tagAttribute name="aria-labelledby" value="lblregistryEmail errorregistryEmail"/>
                                                            <dsp:tagAttribute name="placeholder" value="${titleregistryemail}"/>
                                                        </dsp:input>
													</div>
												</div>
											</fieldset>
										</div>
										<div class="grid_1 optionOr">
											<p class="seperator"></p>
											<p class="or"><bbbl:label key='lbl_regsearch_or' language="${pageContext.request.locale.language}" /></p>
											<p class="seperator"></p>
										</div>
										<div class="grid_2 omega alpha">
											<fieldset>
											<legend class="hidden"><bbbl:label key='lbl_regsearch_registry_num' language="${pageContext.request.locale.language}" /></legend>
												<div class="input width_2">
													<div class="label"><label id="lblregistryNumber" for="registryNumber"> <bbbl:label key='lbl_regsearch_registry_num' language="${pageContext.request.locale.language}" /></label></div>
													<c:set var="titleregistrynumber">
						<bbbl:label key='lbl_regsearch_registry_num' language="${pageContext.request.locale.language}" />
					</c:set>
													<div class="text">
                                                        <dsp:input type="text" name="registryNumber" title="${titleregistrynumber}" id="registryNumber" bean="GiftRegistryFormHandler.registrySearchVO.registryId" >
                                                            <dsp:tagAttribute name="aria-required" value="false"/>
                                                            <dsp:tagAttribute name="aria-labelledby" value="lblregistryNumber errorregistryNumber"/>
							    <dsp:tagAttribute name="placeholder" value="${titleregistrynumber}"/>
                                                        </dsp:input>
                                                    </div>
												</div>
											</fieldset>
										</div>
										<div class="clear"></div>
                                    </div>
                                    <div class="clear"></div>
									<div class="button">
										<c:set var="findRegistryBtn"><bbbl:label key='lbl_regsearch_find_registry' language="${pageContext.request.locale.language}"></bbbl:label></c:set>
										<dsp:input bean="GiftRegistryFormHandler.hidden" type="hidden" value="2" />
										<dsp:input type="submit" bean="GiftRegistryFormHandler.registrySearchFromImportRegistryPage" value="${findRegistryBtn}" id="findRegistryBtn" >
                                            <dsp:tagAttribute name="aria-required" value="false"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="findRegistryBtn"/>
                                        </dsp:input>
									</div>
									</dsp:form>
								</div>
							<div class="clear"></div>
							
						</div>
						</div>
						</div>
					
			</div>
			
<script type="text/javascript">
   var totalCount='${totalCount}';
    </script>
		</jsp:body>
<jsp:attribute name="footerContent">
    <script type="text/javascript">
        if (typeof s !== 'undefined') {
            s.pageName ='My Account>My Registries';
            s.channel = 'My Account';
            s.prop1 = 'My Account';
            s.prop2 = 'My Account';
            s.prop3 = 'My Account';
            s.prop6='${pageContext.request.serverName}'; 
            s.eVar9='${pageContext.request.serverName}';
            if (totalCount > 0) {
                s.pageName= document.location.href;
                s.events="event29";
            }
            var s_code=s.t();
            if(s_code)document.write(s_code);
        }
    </script>
    </jsp:attribute>
	<%-- BBBSL-4343 DoubleClick Floodlight START  
	    <c:if test="${DoubleClickOn}">
		     <c:set var="rkgcollectionProd" value="${fn:trim(rkgProductList)}" />
			 <c:choose>
				 <c:when test="${not empty rkgcollectionProd }">
				 	<c:set var="rkgProductList" value="${rkgcollectionProd}"/>
				 </c:when>
				 <c:otherwise>
				 	<c:set var="rkgProductList" value="null"/>
				 </c:otherwise>
			 </c:choose>
			 <c:choose>
	   		 <c:when test="${(currentSiteId eq BuyBuyBabySite)}">
	   		   <c:set var="cat"><bbbc:config key="cat_registry_baby" configName="RKGKeys" /></c:set>
	   		   <c:set var="src"><bbbc:config key="src_baby" configName="RKGKeys" /></c:set>
	   		   <c:set var="type"><bbbc:config key="type_1_baby" configName="RKGKeys" /></c:set>
	   		 </c:when>
	   		  <c:when test="${(currentSiteId eq BedBathUSSite)}">
		    		   <c:set var="cat"><bbbc:config key="cat_registry_bedBathUS" configName="RKGKeys" /></c:set>
		    		   <c:set var="src"><bbbc:config key="src_bedBathUS" configName="RKGKeys" /></c:set>
		    		   <c:set var="type"><bbbc:config key="type_1_bedBathUS" configName="RKGKeys" /></c:set>
	    		 	</c:when>
	    		 	 <c:when test="${(currentSiteId eq BedBathCanadaSite)}">
		    		   <c:set var="cat"><bbbc:config key="cat_registry_bedbathcanada" configName="RKGKeys" /></c:set>
		    		   <c:set var="src"><bbbc:config key="src_bedbathcanada" configName="RKGKeys" /></c:set>
		    		   <c:set var="type"><bbbc:config key="type_1_bedbathcanada" configName="RKGKeys" /></c:set>
	   		 </c:when>
	   		 </c:choose>
	 		<dsp:include page="/_includes/double_click_tag.jsp">
	 			<dsp:param name="doubleClickParam" 
	 			value="src=${src};type=${type};cat=${cat};u10=null;u11=null"/>
	 		</dsp:include>
 		</c:if>
		DoubleClick Floodlight END --%>
    
	</bbb:pageContainer>
</dsp:page>
