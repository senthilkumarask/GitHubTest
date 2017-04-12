<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />	
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="com/bbb/commerce/giftregistry/droplet/GiftRegistryPaginationDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean" />
	
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof var="appid" bean="Site.id" />

	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>

		<bbb:pageContainer>
			<jsp:attribute name="section">accounts</jsp:attribute>
			<jsp:attribute name="pageWrapper">manageRegistry useCertonaJs myAccount</jsp:attribute>
		<jsp:body>
    <div id="content" class="container_12 clearfix manageRegistry" role="main">	
	
	<div class="grid_12">
		<h1 class="account fl"><bbbl:label key='lbl_regsearch_my_account' language ="${pageContext.request.locale.language}"/></h1>
		<h3 class="subtitle fl"><bbbl:label key='lbl_regsearch_registeries' language ="${pageContext.request.locale.language}"/></h3>
		<div id="chatModal" class="fr marTop_20">
			<div id="chatModalDialogs"></div>
				<dsp:include page="/common/click2chatlink.jsp">
                 	<dsp:param name="pageId" value="2"/>
                </dsp:include>							
             <br>
         </div>
	</div>
	
	<div class="grid_2">				
		<c:import url="/account/left_nav.jsp"/>
	</div>
	
	    <%--<div class="grid_12">
        <div id="chatModal">
		    <div id="chatModalDialogs"></div>
               <dsp:include page="/common/click2chatlink.jsp">
                	<dsp:param name="pageId" value="2"/>
                </dsp:include>
				<br>
			</div> --%>
        <div class="error">
			<dsp:droplet name="ErrorMessageForEach"> 
			    <dsp:param bean="GiftRegistryFormHandler.formExceptions"	name="exceptions" />
				<dsp:oparam name="output">
					<dsp:valueof param="message" />
					<br>
				</dsp:oparam>
			</dsp:droplet>
		</div>
		
		<div class="grid_9 prefix_1">
			<div class="grid_4 alpha">
				<c:choose>
					<c:when test="${registryCount ==0}">
						<bbbt:textArea key="txt_create_a_registry"	language="${pageContext.request.locale.language}" />	
					</c:when>
					<c:otherwise>
						<bbbt:textArea key="txt_another_registry"	language="${pageContext.request.locale.language}" />								
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
								<h3 class="lookingOldRegistry"><bbbl:label key='lbl_regflyout_looking_old_reg' language ="${pageContext.request.locale.language}"/></h3>
								<p>&nbsp;<bbbl:label key='lbl_regflyout_if_created' language ="${pageContext.request.locale.language}"/></p>
								<div class="clearfix " id="findForm">
									<dsp:form action="#" id="findOldRegistry" method="post">
										<h3 class="findOldRegistry"><bbbl:label key='lbl_regsearch_lnktxt'
									language="${pageContext.request.locale.language}" /></h3>
                                     <div class="clearfix marTop_5">
											<div class="grid_2 alpha omega">
												<fieldset>
													<div class="input width_2 alpha">
														<div class="label">
														<label id="lblregistryFirstName" for="registryFirstName"> <bbbl:label key='lbl_reg_firstname'
													language="${pageContext.request.locale.language}" /></label>
														</div>
														<div class="text">
														<dsp:input type="text"
												name="registryFirstName" id="registryFirstName"
												bean="GiftRegistryFormHandler.registrySearchVO.firstName"
												beanvalue="GiftRegSessionBean.requestVO.firstName" >
                                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lblregistryFirstName errorregistryFirstName"/>
                                                </dsp:input>
														</div>
													</div>

												
													<div class="input width_2 alpha">
														<div class="label">
														<label id="lblregistryLastName" for="registryLastName"> <bbbl:label
													key='lbl_reg_lastname'
													language="${pageContext.request.locale.language}" /></label>
														</div>
														<div class="text">
														<dsp:input type="text"
												name="registryLastName" id="registryLastName"
												bean="GiftRegistryFormHandler.registrySearchVO.lastName"
												beanvalue="GiftRegSessionBean.requestVO.lastName" >
                                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lblregistryLastName errorregistryLastName"/>
                                                </dsp:input>
														</div>
													</div>
													<div class="input selectBox width_2 alpha">		
														<div class="label">
															<label id="lblregistryLastName" for="registryLastName">
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
																<dsp:option></dsp:option>
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
												<p class="or"><bbbl:label key='lbl_regsearch_or' language ="${pageContext.request.locale.language}"/></p>
												<p class="seperator"></p>
											</div>

											<div class="grid_2 alpha omega">
												<fieldset>
												<div class="input width_2 alpha">
												<div class="label">
												<label id="lblregistryEmail" for="registryEmail"> <bbbl:label
													key='lbl_regsearch_email'
													language="${pageContext.request.locale.language}" /></label>
												</div>
												<div class="text">
												<dsp:input type="text"
												name="registryEmail" id="registryEmail"
												bean="GiftRegistryFormHandler.registrySearchVO.email" 
												beanvalue="GiftRegSessionBean.requestVO.email" >
                                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lblregistryEmail errorregistryEmail"/>
                                                </dsp:input>
												</div>
												</div>
												</fieldset>
											</div>
											<div class="grid_1 optionOr">
												<p class="seperator"></p>
												<p class="or"><bbbl:label key='lbl_regsearch_or' language ="${pageContext.request.locale.language}"/></p>
												<p class="seperator"></p>
											</div>

									<div class="grid_2 omega alpha">
										<fieldset>
										<div class="input width_2">
											<div class="label">
											<label id="lblregistryNumber" for="registryNumber"> <bbbl:label
													key='lbl_regsearch_registry_num'
													language="${pageContext.request.locale.language}" /></label>
											</div>
											<div class="text">
											<dsp:input type="text"
												name="registryNumber" id="registryNumber"
												bean="GiftRegistryFormHandler.registrySearchVO.registryId" 
												beanvalue="GiftRegSessionBean.requestVO.registryId" >
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblregistryNumber errorregistryNumber"/>
                                            </dsp:input>
											</div>
										</div>
										</fieldset>
									</div>
									<div class="clear"></div>
                                    </div>
                                    <div class="clear"></div>
										<div class="button">
										<c:set var="findRegistryBtn">
									<bbbl:label key='lbl_regsearch_find_registry'
										language="${pageContext.request.locale.language}"></bbbl:label>
								</c:set>
										<dsp:input bean="GiftRegistryFormHandler.hidden" type="hidden" value="2"/>
										<dsp:input type="submit" bean="GiftRegistryFormHandler.registrySearchFromImportRegistryPage" value="${findRegistryBtn}" id="findRegistryBtn"/>
										</div>
									</dsp:form>
								</div>
					<div class="clear"></div>							
				</div>
				<div class="searchResults findOldRegistryResults">

          		<%-- <input type="hidden" name="pageNumber" value="${pagNum}" /> --%>          
         		<dsp:getvalueof var="pagNum" param="pagNum" scope="request" />
                <dsp:getvalueof var="perPage" param="pagFilterOpt"	scope="request" />
                <dsp:getvalueof var="sortBy" param="sortPassString"	scope="request" />
				<dsp:getvalueof var="sortOrder" param="sortOrder"	scope="request" />
				
				 <%-- Sorting Logic --%>
         		<c:choose>
                     <c:when test="${sortBy eq 'NAME'}">
	                	<c:set var="sortOrderName" value="ASCE" scope="request" />
                     	 <c:if test="${sortOrder=='ASCE'}">	
	                		<c:set var="sortOrderName" value="DESC" scope="request" />
	                	</c:if>
	        			<c:set var="sortOrderType" value="ASCE" scope="request" />
	        			<c:set var="sortOrderDate" value="ASCE" scope="request" />
	        			<c:set var="sortOrderState" value="ASCE" scope="request" />
            			
            		</c:when>
            		<c:when test="${sortBy eq 'EVENTTYPEDESC'}">
	                	<c:set var="sortOrderName" value="ASCE" scope="request" />
	        			<c:set var="sortOrderType" value="ASCE" scope="request" />
                     	 <c:if test="${sortOrder=='ASCE'}">	
	                		<c:set var="sortOrderType" value="DESC" scope="request" />
	                	</c:if>
	        			<c:set var="sortOrderDate" value="ASCE" scope="request" />
	        			<c:set var="sortOrderState" value="ASCE" scope="request" />            			
            		</c:when>
                     <c:when test="${sortBy eq 'DATE'}">
	   					<c:set var="sortOrderName" value="ASCE" scope="request" />
	        			<c:set var="sortOrderType" value="ASCE" scope="request" />
	        			<c:set var="sortOrderDate" value="ASCE" scope="request" />
                     	 <c:if test="${sortOrder=='ASCE'}">	
	                		<c:set var="sortOrderDate" value="DESC" scope="request" />
	                	</c:if>	        			
	        			<c:set var="sortOrderState" value="ASCE" scope="request" />            			
            		</c:when>
            		<c:when test="${sortBy eq 'STATE'}">
            			<c:set var="sortOrderName" value="ASCE" scope="request" />
	        			<c:set var="sortOrderType" value="ASCE" scope="request" />
	        			<c:set var="sortOrderDate" value="ASCE" scope="request" />
	        			<c:set var="sortOrderState" value="ASCE" scope="request" />
                     	 <c:if test="${sortOrder=='ASCE'}">	
	                		<c:set var="sortOrderState" value="DESC" scope="request" />
	                	</c:if>	        			
            		</c:when>
            		<c:otherwise>
                		<c:set var="sortOrderName" value="DESC" scope="request" />
	        			<c:set var="sortOrderType" value="ASCE" scope="request" />
	        			<c:set var="sortOrderDate" value="ASCE" scope="request" />
	        			<c:set var="sortOrderState" value="ASCE" scope="request" />
					</c:otherwise>
            	</c:choose>
                
                <c:if test="${empty perPage}">
                	<c:set var="perPage" value="24" scope="request" />
				</c:if>
                <c:if test="${empty pagNum}">
                	<c:set var="pagNum" value="1" scope="request" />
                </c:if>
              
                <dsp:getvalueof var="previousPage" param="previousPage" />
                <c:set var="countButton" value="0" scope="request" />
                <dsp:droplet name="GiftRegistryPaginationDroplet">
					<dsp:param name="pageNo" value="${pagNum}" />
					<dsp:param name="perPage" value="${perPage}" />
					<dsp:param name="siteId" value="${appid}"/>
					<dsp:param name="sortOrder" value="${sortOrder}" />
					<dsp:oparam name="output"> 

						<dsp:droplet name="IsEmpty">
							<dsp:param param="registrySummaryResultList" name="value" />
							<dsp:oparam name="false">
								<dsp:getvalueof var="totalCount" param="totalCount" scope="request" />
						 		<dsp:getvalueof var="registrySummaryResultList" param="registrySummaryResultList" />
								<dsp:getvalueof var="currentResultSize"	value="${fn:length(registrySummaryResultList)}" scope="request" />

								<c:set var="totalTabFlot"	value="${totalCount/perPage}" /> 
								<c:set var="totalTab" value="${totalTabFlot+(1-(totalTabFlot%1))%1}"	scope="request" />
							
								<dsp:droplet name="ForEach"> 
									<dsp:param name="array" param="registrySummaryResultList" />
									<dsp:oparam name="outputStart">
										<div id="pagTop"> 
											<dsp:include page="registry_pagination.jsp">
												<dsp:param name="currentResultSize" value="${currentResultSize}" />
												<dsp:param name="totalCount"  value="${totalCount}" />
												<dsp:param name="perPage" value="${perPage}" />
												<dsp:param name="totalTab" value="${totalTab}" />
											</dsp:include>
										</div> 
							            <div class="clearfix">
							            	<form id="importRegistryDialogForm" action="#" method="post">
						                    <ul class="bdrBottom">
						                    	<li class="subhead">
						                       		<ul>
						                                  <li class="findOldRegistrySRGrid_1">
						                                  							<a class="sortContent" href="reg_import_msg_display.jsp?sortPassString=NAME&pagFilterOpt=${perPage}&sortOrder=${sortOrderName}"
																						onclick="" title="<bbbl:label key='lbl_reg_name' language='${pageContext.request.locale.language}' />"><bbbl:label
																								key='lbl_reg_name'
																								language="${pageContext.request.locale.language}" />
																					</a> </li>
						                                  <li class="findOldRegistrySRGrid_2"><a href="reg_import_msg_display.jsp?sortPassString=EVENTTYPEDESC&pagFilterOpt=${perPage}&sortOrder=${sortOrderType}"
																						title="Event Type" class="blue sortContent"><bbbl:label
																								key='lbl_reg_event_type'
																								language="${pageContext.request.locale.language}" />
																					</a> </li>
						                                  <li class="findOldRegistrySRGrid_3"><a href="reg_import_msg_display.jsp?sortPassString=DATE&pagFilterOpt=${perPage}&sortOrder=${sortOrderDate}"
																						title="Date" class="blue sortContent"><bbbl:label key='lbl_reg_date'
																								language="${pageContext.request.locale.language}" />
																					</a> </li>
						                                  <li class="findOldRegistrySRGrid_4"><a href="reg_import_msg_display.jsp?sortPassString=STATE&pagFilterOpt=${perPage}&sortOrder=${sortOrderState}"																							
																						title="State" class="blue sortContent"><bbbl:label key='lbl_reg_state'
																								language="${pageContext.request.locale.language}" />
																					</a> </li>
						                                  <li class="findOldRegistrySRGrid_5 upperCase">
														  <bbbl:label key='lbl_reg_password' language="${pageContext.request.locale.language}" />
														  </li>
						                                  <li class="grid_1 omega"> </li>
						                              </ul>
												</li>
									</dsp:oparam>
									<dsp:oparam name="output">
										<c:set var="countButton" value="${countButton + 1}" scope="request" />
			                        	<li>
			                          		<c:set var="currentCount">
												<dsp:valueof param="count" />
											</c:set>
			                          		<c:choose>
			                          			<c:when test="${currentCount%2 == 0}">
							            			<ul class="detail clearfix registryInformation">
							            		</c:when>
							            		<c:otherwise>
													<ul class="detail clearfix registryInformation">
												</c:otherwise>
							            	</c:choose>
			                                <li class="findOldRegistrySRGrid_1"> 
			                                	<dsp:valueof param="element.primaryRegistrantFirstName" />
											   	<dsp:droplet name="IsEmpty">
														 <dsp:getvalueof param="element.coRegistrantFirstName"
																		id="fName">
															<dsp:param value="${fName}" name="value" />
														 </dsp:getvalueof>
														<dsp:oparam name="false">
															 <c:set var="coRegName"><dsp:valueof param="element.coRegistrantFirstName"/></c:set>
															<c:if test="${not empty coRegName}">& <dsp:valueof param="element.coRegistrantFirstName" />
															</c:if>
														</dsp:oparam>
													</dsp:droplet> 
											</li>
			                                <li class="findOldRegistrySRGrid_2">
			                                	<dsp:droplet name="IsEmpty">
			   										<dsp:param param="element.eventType" name="value"/>
													<dsp:oparam name="false">
														 <dsp:valueof param="element.eventType"/>
												   	</dsp:oparam>
												   	<dsp:oparam name="true">
												   	&nbsp;
												   	</dsp:oparam>
												 </dsp:droplet>	
											</li>
			                                <li class="findOldRegistrySRGrid_3">
			                                	<dsp:droplet name="IsEmpty">
			   										<dsp:param param="element.eventDate" name="value"/>
													<dsp:oparam name="false">
														 <dsp:valueof param="element.eventDate"/>
												   	</dsp:oparam>
												   	<dsp:oparam name="true">
												   	&nbsp;
												   	</dsp:oparam>
												 </dsp:droplet>
											</li>
			                                <li class="findOldRegistrySRGrid_4">
			                                	<dsp:droplet name="IsEmpty">
			   										<dsp:param param="element.state" name="value"/>
													<dsp:oparam name="false">
														 <dsp:valueof param="element.state"/>
												   	</dsp:oparam>
												   	<dsp:oparam name="true">
												   	&nbsp;
												   	</dsp:oparam>
												 </dsp:droplet>
											</li>
                                            <c:set var="registryId"><dsp:valueof param="element.registryId"/></c:set>
			                                <li class="findOldRegistrySRGrid_5">
                                                <div class="showPassDiv clearfix">
                                                    <input name="showPassword" type="checkbox" value="" data-toggle-class="showpassRegImport${registryId}" class="showPassword" id="showPassword${registryId}" />
                                                    <label for="showPassword${registryId}" class="lblShowPassword"><bbbl:label key="lbl_show_password" language="${pageContext.request.locale.language}"/></label>
                                                    <div class="clear"></div> 
                                                </div>
			                                      <div class="input width_3 alpha">
			                                          <div class="text">
			                                              <label id="lblcurrentpassword" class="hidden" for="currentpassword">Password</label>
			                                              <input id="currentpassword" type="password" autocomplete="off" name="currentpassword" class="showpassRegImport${registryId}" aria-required="true" aria-labelledby="lblcurrentpassword errorcurrentpassword" />
                                                          <dsp:a iclass="forgotPassword" title="Forgot Password" href="${contextPath}/giftregistry/modals/reg_forgot_password.jsp">
																	<dsp:param name="registryId" value="${registryId}" />
																	<bbbl:label key='lbl_reg_forgot_password'
																		language="${pageContext.request.locale.language}" /></dsp:a>
			                                            </div>
			                                      </div>
			                                  </li>
			                                  <li class="grid_1 alpha omega textRight">			                                     
			                                      	<input type="hidden"
																name="registry"
																value="<dsp:valueof param="element.registryId"/>" />
														<input type="hidden" name="registryTypeName"
																value="<dsp:valueof param="element.eventCode"/>" />
														<input type="hidden" name="registryEventDate"
																value="<dsp:valueof param="element.eventDate"/>" />
			                                      	<c:set var="importRegistryBtn">
														<bbbl:label key='lbl_reg_import'
																	language="${pageContext.request.locale.language}"></bbbl:label>
													</c:set>
			                                          <input type="button" id="import${countButton}"
																value=${importRegistryBtn}
																class="btnImportRegistry hidden" role="button" aria-pressed="false" aria-labelledby="import${countButton}" />
														<a href="#" role="link" class="allCaps triggerSubmit" data-submit-button="import${countButton}"><strong><bbbl:label key='lbl_reg_import'
																	language="${pageContext.request.locale.language}"></bbbl:label></strong></a>														
			                                     
			                                  </li>
			                              </ul>
			                        	</li>
									</dsp:oparam>
									<dsp:oparam name="outputEnd">
				                    		</ul>
				                  		</form>
					                  	<form class="hidden" id="importRegistryDialogMainForm"
												action="${contextPath}/giftregistry/find_old_reg_response.jsp"
												method="post">
					                          	<input type="hidden" name="registryID"
																	value="" />
					                          	<input type="hidden" name="registryPassword"
																	value="" />
												<input type="hidden" name="eventDate" value="" />
					                          	<input type="hidden" name="eventType"
																	value="" />
					                  	</form>
                                        </div>
						              	<div id="pagBot"> 
							                  <dsp:include page="registry_pagination.jsp">
													<dsp:param name="currentResultSize" value="${currentResultSize}" />
													<dsp:param name="totalCount"  value="${totalCount}" />
													<dsp:param name="perPage" value="${perPage}" />
	   											    <dsp:param name="totalTab" value="${totalTab}" />
												</dsp:include>
							              </div>
                                          <div class="clear"></div>	
									</dsp:oparam>
								</dsp:droplet> <%-- For Each Droplet --%>
						</dsp:oparam>
						<dsp:oparam name="true">
							<span class="error"><bbbl:label key="lbl_regsrchguest_sorrynoregistries" language="${pageContext.request.locale.language}" /></span>
						</dsp:oparam>
				</dsp:droplet> <%-- IsEmpty Droplet --%>
				</dsp:oparam>
				<dsp:oparam name="error">
					<dsp:getvalueof var="errorMsg" param="errorMsg" />
					<span class="error"><bbbl:label key="${errorMsg}" language="${pageContext.request.locale.language}" /></span>
				</dsp:oparam>
				<dsp:oparam name="empty"> 
					<span class="error"><bbbl:label key="lbl_regsrchguest_sorrynoregistries" language="${pageContext.request.locale.language}" /></span>
				</dsp:oparam>				
				</dsp:droplet><%-- GiftRegistryPaginationDroplet--%>
			</div>
			</div>
		</div>
    </div>

    <div id="importRegistryDialog" class=""
				title="Import Registry" style="display: none;">
    <div class="container_6">
        <div class="grid_6 noMar">
            <p>
            <bbbl:label key='lbl_import_success_msg'
								language="${pageContext.request.locale.language}" />
            </p>
        </div> 
		<div class="marTop_20 buttonpane clearfix cb">
			<div class="ui-dialog-buttonset">
				<div class="button button_active">
            <dsp:a id="btnCloseimportRegistryDialog"
							href="${contextPath}/giftregistry/my_registries.jsp"
							>
							<bbbl:label key='lbl_reg_ok'
								language="${pageContext.request.locale.language}" />
						</dsp:a>
				</div>
			</div>
		</div>
    </div> 
</div>
<div id="importRegistryDialogError" class="" title="Import Registry Error"
				style="display: none;"> 
    <div class="container_6">
        <div class="grid_6 noMar">
            <p>
                  <label id="errLabel"></label>
            </p>
        </div>
		<div class="marTop_20 buttonpane clearfix cb">
			<div class="ui-dialog-buttonset">
				<div class="button button_active">
         <dsp:a href="#" id="btnCloseimportRegistryDialogError"
							iclass="close-any-dialog">
          <bbbl:label key='lbl_reg_ok'
								language="${pageContext.request.locale.language}" />
						</dsp:a>
				</div>
			</div>
		</div>
    </div>
</div>
</div>

</jsp:body>
	</bbb:pageContainer>

</dsp:page>
