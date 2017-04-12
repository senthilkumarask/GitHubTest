<dsp:page>
<link rel="stylesheet" type="text/css" href="/includes/bbregistry/theme.css" media="print" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />	
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryPaginationDroplet" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	
	<dsp:getvalueof var="appid" bean="Site.id" />
	<c:choose>
		<c:when test="${not empty appid && appid eq 'BuyBuyBaby'}">
			<c:set var="pageVariation" value="bb" scope="request" />
		</c:when>
		<c:otherwise>
			<c:set var="pageVariation" value="by" scope="request" />
		</c:otherwise>
	</c:choose>
	<bbb:mxPageContainer>	
	<jsp:attribute name="section">registry</jsp:attribute>
	<jsp:attribute name="pageVariation">${pageVariation}</jsp:attribute>
	<jsp:attribute name="pageWrapper">useFB useCertonaJs manageRegistry givingAGift</jsp:attribute>	
	<jsp:body>
	
	<dsp:getvalueof var="appid" bean="Site.id" />
	<c:choose>
		<c:when test="${appid eq 'BuyBuyBaby'}">
			<c:set var="gridValue" value="1" scope="request" />
		</c:when>
		<c:otherwise>
			<c:set var="gridValue" value="2" scope="request" />
		</c:otherwise>
	</c:choose>
	
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
	
	<div id="content" class="container_12 clearfix" role="main">
		<div class="grid_12">
			<h1>
				<bbbl:label key="lbl_mxregsrchguest_givinggift" language="${pageContext.request.locale.language}" />
			</h1>			
	
			<dsp:getvalueof var="perPage" param="pagFilterOpt" scope="request" />
			<dsp:getvalueof var="pagNum" param="pagNum" scope="request" />
	
			<c:if test="${empty perPage}">
					   <c:set var="perPage" value="24" scope="request" />
			 </c:if>
					
			<c:if test="${empty pagNum}">
			   <c:set var="pagNum" value="1" scope="request" />
			</c:if>
			
			<dsp:getvalueof var="tabNo" param="tabNo" />
			<c:if test="${ empty tabNo}">
				<c:set var="tabNo" value="1" />
			</c:if>
	
			<dsp:getvalueof var="previousPage" param="previousPage" />
			
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
        			<c:set var="sortOrderState" value="ASC" scope="request" />
        			<c:set var="sortOrderMName" value="ASCE" scope="request" />
        			<c:set var="sortOrderRegNum" value="ASCE" scope="request" />
           		</c:when>
           		<c:when test="${sortBy eq 'EVENTTYPEDESC'}">
                	<c:set var="sortOrderName" value="ASCE" scope="request" />
        			<c:set var="sortOrderType" value="ASCE" scope="request" />
                    <c:if test="${sortOrder=='ASCE'}">	
                		<c:set var="sortOrderType" value="DESC" scope="request" />
                	</c:if>
        			<c:set var="sortOrderDate" value="ASCE" scope="request" />
        			<c:set var="sortOrderState" value="ASCE" scope="request" />
        			<c:set var="sortOrderMName" value="ASCE" scope="request" />
        			<c:set var="sortOrderRegNum" value="ASCE" scope="request" />        			            			
           		</c:when>
                <c:when test="${sortBy eq 'MAIDEN'}">
   					<c:set var="sortOrderName" value="ASCE" scope="request" />
        			<c:set var="sortOrderType" value="ASCE" scope="request" />
	       			<c:set var="sortOrderDate" value="ASCE" scope="request" />
        			<c:set var="sortOrderState" value="ASCE" scope="request" />
        			<c:set var="sortOrderMName" value="ASCE" scope="request" />        			
        			<c:if test="${sortOrder=='ASCE'}">	
                		<c:set var="sortOrderMName" value="DESC" scope="request" />
                	</c:if>
        			<c:set var="sortOrderRegNum" value="ASCE" scope="request" />        			           			
           		</c:when>
           		<c:when test="${sortBy eq 'REGISTRYNUM'}">
           			<c:set var="sortOrderName" value="ASCE" scope="request" />
        			<c:set var="sortOrderType" value="ASCE" scope="request" />
        			<c:set var="sortOrderDate" value="ASCE" scope="request" />
        			<c:set var="sortOrderState" value="ASCE" scope="request" />
                	<c:set var="sortOrderMName" value="ASCE" scope="request" />
        			<c:set var="sortOrderRegNum" value="ASCE" scope="request" />
                    <c:if test="${sortOrder=='ASCE'}">	
                		<c:set var="sortOrderRegNum" value="DESC" scope="request" />
                	</c:if>
           		</c:when>
                <c:when test="${sortBy eq 'DATE'}">
   					<c:set var="sortOrderName" value="ASCE" scope="request" />
        			<c:set var="sortOrderType" value="ASCE" scope="request" />
        			<c:set var="sortOrderDate" value="ASCE" scope="request" />
                    <c:if test="${sortOrder=='ASCE'}">	
                		<c:set var="sortOrderDate" value="DESC" scope="request" />
                	</c:if>	        			
        			<c:set var="sortOrderState" value="ASCE" scope="request" />
        			<c:set var="sortOrderMName" value="ASCE" scope="request" />
        			<c:set var="sortOrderRegNum" value="ASCE" scope="request" />        			            			
           		</c:when>
           		<c:when test="${sortBy eq 'STATE'}">
           			<c:set var="sortOrderName" value="ASCE" scope="request" />
        			<c:set var="sortOrderType" value="ASCE" scope="request" />
        			<c:set var="sortOrderDate" value="ASCE" scope="request" />
        			<c:set var="sortOrderState" value="ASCE" scope="request" />
                    <c:if test="${sortOrder=='ASCE'}">	
                		<c:set var="sortOrderState" value="DESC" scope="request" />
                	</c:if>
        			<c:set var="sortOrderMName" value="ASCE" scope="request" />
        			<c:set var="sortOrderRegNum" value="ASCE" scope="request" />                		        			
           		</c:when>
           		<c:otherwise>
					<c:set var="sortOrderName" value="DESC" scope="request" />
        			<c:set var="sortOrderType" value="ASCE" scope="request" />
        			<c:set var="sortOrderDate" value="ASCE" scope="request" />
        			<c:set var="sortOrderState" value="ASCE" scope="request" />
        			<c:set var="sortOrderMName" value="ASCE" scope="request" />
        			<c:set var="sortOrderRegNum" value="ASCE" scope="request" />        			
				</c:otherwise>
           	</c:choose>


			<dsp:droplet name="IsEmpty">
				<dsp:param name="value" param="noSearch" />
				<dsp:oparam name="true">
					<dsp:droplet name="GiftRegistryPaginationDroplet">
					    <dsp:param name="pageNo" value="${pagNum}" />
					    <dsp:param name="perPage" value="${perPage}" />
						<dsp:param name="tabNo" value="${tabNo}" />
						<dsp:param name="previousPage" value="${previousPage}" />
						<dsp:param name="sortOrder" value="${sortOrder}" />		
						<dsp:param name="siteId" value="${appid}"/>
						<dsp:param name="mx_registry_pagination" value="mxregistry"/>
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
											<div>
												<ul class="bdrBottom noMarBot">
													<li class="subhead">
														<ul class="noMarBot noBorderBottom">
															<li class="grid_2 alpha"> 
																<a href="registry_search_guest.jsp?sortPassString=NAME&pagFilterOpt=${perPage}&sortOrder=${sortOrderName}"
																						title="Registrants" class="sortContent"><bbbl:label
																								key="lbl_regsrchguest_mxRegistrants"
																								language="${pageContext.request.locale.language}" />
																					</a>
															</li>
															<c:if test="${appid eq 'BuyBuyBaby'}">
															<li class="grid_2 alpha">
															<a href="registry_search_guest.jsp?sortPassString=MAIDEN&pagFilterOpt=${perPage}&sortOrder=${sortOrderMName}"
																						title='<bbbl:label
																								key="lbl_regsrchguest_maiden_names"
																								language="${pageContext.request.locale.language}" />' class="sortContent"><bbbl:label
																								key="lbl_regsrchguest_maiden_names"
																								language="${pageContext.request.locale.language}" />
																					</a> </li>
															</c:if>
															<li class="grid_2 alpha">
															<a href="registry_search_guest.jsp?sortPassString=EVENTTYPEDESC&pagFilterOpt=${perPage}&sortOrder=${sortOrderType}"
																						title="Event Type" class="sortContent"><bbbl:label
																								key="lbl_regsrchguest_mxEventType"
																								language="${pageContext.request.locale.language}" />
																					</a> </li>
															<li class="grid_2 alpha">
															<a href="registry_search_guest.jsp?sortPassString=DATE&pagFilterOpt=${perPage}&sortOrder=${sortOrderDate}"
																						title="Date" class="sortContent"><bbbl:label key="lbl_regsrchguest_mxDate"
																								language="${pageContext.request.locale.language}" />
																					</a> </li>
															<li class="grid_${gridValue} alpha">
															<a href="registry_search_guest.jsp?sortPassString=STATE&pagFilterOpt=${perPage}&sortOrder=${sortOrderState}"
																						title="State" class="sortContent"><bbbl:label
																								key="lbl_regsrchguest_mxState"
																								language="${pageContext.request.locale.language}" />
																					</a> </li>
															<li class="grid_3 alpha">
															<a href="registry_search_guest.jsp?sortPassString=REGISTRYNUM&pagFilterOpt=${perPage}&sortOrder=${sortOrderRegNum}"
																						title='<bbbl:label
																								key="lbl_mxregsrchguest_Registry"
																								language="${pageContext.request.locale.language}" />' class="sortContent"><bbbl:label
																								key="lbl_mxregsrchguest_Registry"
																								language="${pageContext.request.locale.language}" />
																					</a> </li>
															<li class="grid_1 omega"> </li>
														</ul>
													</li>
						
												
										</dsp:oparam>
										<dsp:oparam name="output">

											 <li>
												<c:set var="currentCount">
													<dsp:valueof param="count" />
												</c:set>
												<c:choose>
													<c:when test="${currentCount%2 == 0}">
														<ul class="detail clearfix registryInformation noMarTop highlightedRow">
													</c:when>
													<c:otherwise>
														<ul class="detail clearfix registryInformation noMarTop">
													</c:otherwise>
												</c:choose>
						
															<li class="grid_2 alpha bold">
															
																<dsp:valueof param="element.primaryRegistrantFirstName" />
																<dsp:getvalueof var="eventType" param="element.eventType" />
																<dsp:getvalueof var="pRMaidenName" param="element.primaryRegistrantMaidenName" />
																<dsp:getvalueof var="cRMaidenName" param="element.coRegistrantMaidenName" />
																<dsp:getvalueof var="pwsurl" param="element.pwsurl" />
																<dsp:getvalueof var="pwsToken" param="element.personalWebsiteToken" />
																<dsp:getvalueof var="regId" param="element.registryId" />
											
																<dsp:droplet name="IsEmpty">
																	 <dsp:getvalueof param="element.coRegistrantFirstName"
																							id="fName">
																		<dsp:param value="<%=fName%>" name="value" />
																	 </dsp:getvalueof>
																	<dsp:oparam name="false">
																	 <c:set var="coRegName"><dsp:valueof param="element.coRegistrantFirstName"/></c:set>
																		<c:if test="${not empty coRegName}">& <dsp:valueof param="element.coRegistrantFirstName" />
																		</c:if>
																	</dsp:oparam>
																</dsp:droplet>
																
		
																<c:if test="${not empty eventType && eventType eq 'Wedding' && not empty pwsurl && not empty pwsToken}">
																
																<c:set var="personalSite">${contextPath}<bbbc:config key="personal_site_url" configName="ThirdPartyURLs" />${pwsToken}
																</c:set>
																
																
																<p>
																	<a title="Personal Wedding Website"  href="${personalSite}" id="personalSite" target="_blank">
																		<bbbl:label key='lbl_mng_regitem_pwedsite' language="${pageContext.request.locale.language}" />
																	</a>
																</p>
																</c:if>
															</li>
															<c:if test="${appid eq 'BuyBuyBaby'}">
																<li class="grid_2 alpha">
																	<dsp:droplet name="IsEmpty">
																		<dsp:getvalueof param="element.primaryRegistrantMaidenName" id="pMaidenName">
																			<dsp:param value="${pMaidenName}" name="value" />
																		</dsp:getvalueof>
																		<dsp:oparam name="false">
																			 <c:set var="pRegMaidenName"><dsp:valueof param="element.primaryRegistrantMaidenName"/></c:set>
																				<c:if test="${not empty pRegMaidenName}"><dsp:valueof param="element.primaryRegistrantMaidenName" />
																				</c:if>
																		</dsp:oparam>
																		<dsp:oparam name="true">-</dsp:oparam>
																	</dsp:droplet>
																	<dsp:droplet name="IsEmpty">
																		<dsp:getvalueof param="element.coRegistrantMaidenName" id="coMaidenName">
																			<dsp:param value="${coMaidenName}" name="value" />
																		</dsp:getvalueof>
																		<dsp:oparam name="false">
																			 <c:set var="coRegMaidenName"><dsp:valueof param="element.coRegistrantMaidenName"/></c:set>
																				<c:if test="${not empty coRegMaidenName}">& <dsp:valueof param="element.coRegistrantMaidenName" />
																				</c:if>
																		</dsp:oparam>
																	</dsp:droplet>
																</li>
															</c:if>
															<li class="grid_2 alpha">
																<dsp:getvalueof var="eventType" param="element.eventType"/>
																<dsp:getvalueof var="spEventTypeLabel" value="lbl_mx_event_type_${eventType}" /> 
																<bbbl:label key="${spEventTypeLabel}" language="${pageContext.request.locale.language}" />
															</li>
															<li class="grid_2 alpha">
																<c:choose>
															    	<c:when test="${appid eq 'BedBathCanada'}">
															      		<dsp:valueof param="element.eventDateCanada"/>
															      	</c:when>
															     	<c:otherwise>
															     		<dsp:getvalueof param="element.eventDate" var="eventDate" />
															     		<dsp:droplet name="/com/bbb/commerce/giftregistry/droplet/MxDateFormatDroplet">
																			<dsp:param name="currentDate" value="${eventDate}"/>
																			<dsp:oparam name="output">
											                            						<dsp:getvalueof var="mxConvertedDate" param="mxConvertedDate" />
											                            					</dsp:oparam>
											                            			</dsp:droplet>
													                		${mxConvertedDate}													                	
															     	</c:otherwise>
															     </c:choose>
															</li>
															<li class="grid_${gridValue} alpha"><dsp:valueof
																						param="element.state" />
																				</li>
															<li class="grid_${gridValue} alpha"> <dsp:valueof
																						param="element.registryId" /> </li>
															<li class="alpha omega fl">
															<div class="button">
																<dsp:a href="view_registry_guest.jsp">
																	<dsp:param name="registryId" value="${regId}"/>
																	<dsp:param name="eventType" value="${eventType}"/>
																	<dsp:param name="pwsurl" value="${pwsurl}"/>
																	<dsp:param name="pwsToken" value="${pwsToken}"/>
																	<bbbl:label key="lbl_regsrchguest_view_mxregistry" language="${pageContext.request.locale.language}" />
																</dsp:a>
															</div>
															</li>
												</ul>	
											</li>
										</dsp:oparam>
										<dsp:oparam name="outputEnd">
												</ul>
											</div>
											<div id="pagBot" class="grid_12 alpha"> 
												<dsp:include page="registry_pagination.jsp">
														<dsp:param name="currentResultSize" value="${currentResultSize}" />
														<dsp:param name="totalCount"  value="${totalCount}" />
														<dsp:param name="perPage" value="${perPage}" />
		   											    <dsp:param name="totalTab" value="${totalTab}" />
													</dsp:include>
											</div>
										</dsp:oparam>
									</dsp:droplet> <%-- End ForEach --%>
									
								</dsp:oparam>
								<dsp:oparam name="true"> 
									<div class="grid_12 alpha omega">
										<p class="error"><bbbl:label key="lbl_mx_regsrchguest_sorrynoregistries" language="${pageContext.request.locale.language}" /></p>
									</div>
								</dsp:oparam>
							</dsp:droplet> <%-- End IsNull --%>
				
						</dsp:oparam>
						<dsp:oparam name="error"> 
							<dsp:getvalueof var="errorMsg" param="errorMsg" />
							<div class="grid_12 alpha omega">
									<p class="error"><bbbl:label key="${errorMsg}" language="${pageContext.request.locale.language}" /></p>
							</div>
						</dsp:oparam>
						<dsp:oparam name="empty"> 
							<div class="grid_12 alpha omega">
								<p class="error"><bbbl:label key="lbl_mx_regsrchguest_sorrynoregistries" language="${pageContext.request.locale.language}" /></p>
							</div>
						</dsp:oparam>								
					</dsp:droplet><%-- End GiftRegistryPaginationDroplet --%>
		
				</dsp:oparam>
			</dsp:droplet>
			<div class="grid_6 container alpha marTop_10 searchRegistry">
                <div class="spacer">
			       <h3>
						<bbbl:label key="lbl_mxregsrchguest_searchregistries"	language="${pageContext.request.locale.language}" />
					</h3>
					<h3>
						<bbbl:label key="lbl_mxregsrchguest_searchagain" language="${pageContext.request.locale.language}" />
					</h3>
					
			        <dsp:form id="findRegistry" iclass="clearfix" action="bbb_search_registry" method="post">
			            <div class="grid_2 alpha omega">
			                <div class="grid_2 alpha">
			                    <div class="input width_2 alpha">
			                        <div class="label">
			                            <label for="mxbbRegistryFirstName"><bbbl:label
											key="lbl_mxregsrchguest_firstname"
											language="${pageContext.request.locale.language}" />
									</label>
			                        </div>
			                        <div class="text">
			                           <dsp:input type="text"
										bean="GiftRegistryFormHandler.registrySearchVO.firstName"
										id="mxbbRegistryFirstName" name="mxbbRegistryFirstName" maxlength="30" value="" >
                                            <dsp:tagAttribute name="aria-required" value="false"/>
                                            <dsp:tagAttribute name="aria-describedby" value="mxbbRegistryFirstName errormxbbRegistryFirstName"/>
                                        </dsp:input>
			                        </div>
			                    </div>
								<div class="clear"></div>
			                </div>
			                <div class="input grid_2 alpha formElement">
			                    <div class="label">
			                        <label for="mxbbRegistryLastName"><bbbl:label
										key="lbl_mxregsrchguest_lastname"
										language="${pageContext.request.locale.language}" />
								</label>
			                    </div>
			                    <div class="text">
			                        <dsp:input type="text"
									bean="GiftRegistryFormHandler.registrySearchVO.lastName"
									id="mxbbRegistryLastName" name="mxbbRegistryLastName" maxlength="30"  value="" >
                                        <dsp:tagAttribute name="aria-required" value="false"/>
                                        <dsp:tagAttribute name="aria-describedby" value="mxbbRegistryLastName errormxbbRegistryLastName"/>
                                    </dsp:input>
			                    </div>
								<div class="clear"></div>
			                </div>
							<div class="clear"></div>
						</div>
			            <div class="grid_1 alpha omega txtCenter">
							<bbbl:label key="lbl_regsrchguest_or"
								language="${pageContext.request.locale.language}" />
						<div class="clear"></div>
						</div>
			            <div class="grid_2">
			                    <div class="input width_2 alpha formElement">
			                        <div class="label">
			                            <label for="mxbbRegistryNumber"><bbbl:label
										key="lbl_mxregsrchguest_registrynumber"
										language="${pageContext.request.locale.language}" />
								</label>
			                        </div>
			                        <div class="text">
			                            <dsp:input type="text"
                                        bean="GiftRegistryFormHandler.registrySearchVO.registryId"
                                        id="mxbbRegistryNumber" name="mxbbRegistryNumber" value="" >
                                            <dsp:tagAttribute name="aria-required" value="false"/>
                                            <dsp:tagAttribute name="aria-describedby" value="mxbbRegistryNumber errormxbbRegistryNumber"/>
                                        </dsp:input>
			                        </div>
			                    </div>
						<div class="clear"></div>
			            </div>
			            <dsp:input bean="GiftRegistryFormHandler.hidden"
							type="hidden" value="3" />
						<%-- CLient DOM XSRF
						<dsp:input bean="GiftRegistryFormHandler.registrySearchSuccessURL"
							type="hidden" value="registry_search_guest.jsp" />
						<dsp:input bean="GiftRegistryFormHandler.registrySearchErrorURL"
							type="hidden" value="registry_search_guest.jsp" /> --%>
						<dsp:input bean="GiftRegistryFormHandler.fromPage" type="hidden" value="registrySearchMx" />
			            <div class="clear"></div>
			            <div class="button cf cb inlineBlock">
							<c:set var="findButton">
								<bbbl:label key='lbl_find_mxreg_submit_button'
									language="${pageContext.request.locale.language}"></bbbl:label>
							</c:set>
			                 <dsp:input id="searchRegistry"	bean="GiftRegistryFormHandler.mxRegistrySearch" type="submit"
								value="${findButton}" >
                                <dsp:tagAttribute name="aria-pressed" value="false"/>
                                <dsp:tagAttribute name="aria-describedby" value="searchRegistry errorsearchRegistry"/>
                                <dsp:tagAttribute name="role" value="button"/>
                            </dsp:input>
			            </div>
					</dsp:form>
                    <div class="clear"></div>
                </div>
			</div>
			<div class="grid_6 omega container whyRegister omega marTop_10">
                <div class="spacer">
				<h3>
					<bbbl:label key="lbl_mxregsrchguest_searchtips"
								language="${pageContext.request.locale.language}" />
				</h3>
				<ul>
					<li><bbbl:label key="lbl_mxregsrchguest_search_tips1"
										language="${pageContext.request.locale.language}" />
								</li>
					<li><bbbl:label key="lbl_mxregsrchguest_search_tips2"
										language="${pageContext.request.locale.language}" />
								</li>
					<li><bbbl:label key="lbl_mxregsrchguest_search_tips3"
										language="${pageContext.request.locale.language}" />
								</li>
					<li><bbbl:label key="lbl_mxregsrchguest_search_tips4"
										language="${pageContext.request.locale.language}" />
								</li>
				</ul> 
                <div class="clear"></div>
                </div>
			</div>
		</div>
 	</div>


		<script type="text/javascript">
		   var totalCount='${totalCount}';
		</script>


	<%--dsp:input bean="GiftRegistryFormHandler.SuccessURL" type="hidden"
		value="registry_registrant_form.jsp" /--%>
	</jsp:body>
	<jsp:attribute name="footerContent">
    <script type="text/javascript">
  		if (typeof s !== 'undefined') {
			s.pageName = 'Registry Search Results';
			s.channel = 'Registry';
			s.prop1 = 'Registry';
			s.prop2 = 'Registry';
			s.prop3 = 'Registry';
			s.events = "event29";
			var s_code = s.t();
				if (s_code)
					document.write(s_code);
			}
	</script>
    </jsp:attribute>
	</bbb:mxPageContainer>
</dsp:page>