<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryPaginationDroplet" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/StatesLookup" />

	<dsp:getvalueof var="appid" bean="Site.id" />
	<c:choose>
		<c:when test="${not empty appid && appid eq 'BuyBuyBaby'}">
			<c:set var="pageVariation" value="bb" scope="request" />
		</c:when>
		<c:otherwise>
			<c:set var="pageVariation" value="by" scope="request" />
		</c:otherwise>
	</c:choose>
	<%--Fetching appId for Certona Tagging --%>
	<dsp:droplet name="Switch">
	 <dsp:param name="value" bean="Site.id"/>
	 	<dsp:oparam name="BedBathUS">
	 		<c:set var="appIdCertona" scope="request"><bbbc:config key="BedBathUS" configName="CertonaKeys"/></c:set>
	 	</dsp:oparam>
	 	<dsp:oparam name="BedBathCanada">
	 		<c:set var="appIdCertona" scope="request"><bbbc:config key="BedBathCanada" configName="CertonaKeys"/></c:set> 
	 	</dsp:oparam>
	 	<dsp:oparam name="BuyBuyBaby">
	 		<c:set var="appIdCertona" scope="request"><bbbc:config key="BuyBuyBaby" configName="CertonaKeys"/></c:set>
	 	</dsp:oparam>
	</dsp:droplet>	
	
	<bbb:pageContainer>
		<jsp:attribute name="section">registry</jsp:attribute>
		<jsp:attribute name="pageVariation">${pageVariation}</jsp:attribute>
		<jsp:attribute name="pageWrapper">useFB useCertonaJs manageRegistry givingAGift</jsp:attribute>
		<jsp:attribute name="PageType">RegistryGuest</jsp:attribute>
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
	
    <dsp:getvalueof var="contextPath"
				bean="/OriginatingRequest.contextPath" />
	<c:set var="saSrc"><bbbc:config key="socialAnnexURL" configName="ThirdPartyURLs" /></c:set>			
	
	<script type="text/javascript">
		var totalCount = '0'
			,sa_s22_unique_registry_id = []
			,sa_obj = {};
	</script>
	
	
	<div id="content" class="container_12 clearfix" role="main">
		<div class="grid_12">
			<div id="chatModal" class="fr marTop_20">
				<div id="chatModalDialogs"></div>
                <dsp:include page="/common/click2chatlink.jsp">
                	<dsp:param name="pageId" value="2" />
                </dsp:include>
			</div>
			<h1>
				<bbbl:label key="lbl_regsrchguest_givinggift"
							language="${pageContext.request.locale.language}" />
			</h1>			
	
			<dsp:getvalueof var="perPage" param="pagFilterOpt" scope="request" />
			<dsp:getvalueof var="pagNum" param="pagNum" scope="request" />
			<dsp:getvalueof var="sessFirstName" bean="/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean.requestVO.firstName" scope="request" />
			<dsp:getvalueof var="sessLastName" bean="/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean.requestVO.lastName" scope="request" />
			<dsp:getvalueof var="sessRegistryId" bean="/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean.requestVO.registryId" scope="request" />
			<dsp:getvalueof var="sessState" bean="/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean.requestVO.state" scope="request" />
			<dsp:getvalueof var="sessEventType" bean="/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean.requestVO.event" scope="request" />			
			<dsp:getvalueof var="sessSort" bean="/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean.requestVO.sort" />
			<dsp:getvalueof var="sessOrder" bean="/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean.requestVO.sortSeqOrder"/>
			<dsp:getvalueof var="sessSortOrder" value="${sessSort}${sessOrder}" scope="request" />
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
			
            <dsp:getvalueof var="sortBy" param="sortPassString" scope="request" />
			<dsp:getvalueof var="sortOrder" param="sortOrder" scope="request" />
			 <%-- Sorting Logic --%>
        		<c:choose>
                    <c:when test="${sortBy eq 'NAME'}">
                	<c:set var="sortOrderName" value="ASCE" scope="request" />
                    <c:if test="${sortOrder=='ASCE'}">	
                		<c:set var="sortOrderName" value="DESC"
									scope="request" />
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
                		<c:set var="sortOrderType" value="DESC"
									scope="request" />
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
                		<c:set var="sortOrderMName" value="DESC"
									scope="request" />
                	</c:if>
        			<c:set var="sortOrderRegNum" value="ASCE" scope="request" />        			           			
           		</c:when>
           		<c:when test="${sortBy eq 'REGISTRYNUM'}">
           			<c:set var="sortOrderName" value="ASCE" scope="request" />
        			<c:set var="sortOrderType" value="ASCE" scope="request" />
        			<c:set var="sortOrderDate" value="ASCE" scope="request" />
        			<c:set var="sortOrderState" value="ASCE" scope="request" />
                	<c:set var="sortOrderMName" value="ASCE"
								scope="request" />
        			<c:set var="sortOrderRegNum" value="ASCE" scope="request" />
                    <c:if test="${sortOrder=='ASCE'}">	
                		<c:set var="sortOrderRegNum" value="DESC"
									scope="request" />
                	</c:if>
           		</c:when>
                <c:when test="${sortBy eq 'DATE'}">
   					<c:set var="sortOrderName" value="ASCE" scope="request" />
        			<c:set var="sortOrderType" value="ASCE" scope="request" />
        			<c:set var="sortOrderDate" value="ASCE" scope="request" />
                    <c:if test="${sortOrder=='ASCE'}">	
                		<c:set var="sortOrderDate" value="DESC"
									scope="request" />
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
                		<c:set var="sortOrderState" value="DESC"
									scope="request" />
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
						<dsp:param name="siteId" value="${appid}" />		
						<dsp:oparam name="output">    
				   
							<dsp:droplet name="IsEmpty">
								<dsp:param param="registrySummaryResultList" name="value" />
								<dsp:oparam name="false">
								
									<dsp:getvalueof var="totalCount" param="totalCount"
												scope="request" />
							 		<dsp:getvalueof var="registrySummaryResultList"
												param="registrySummaryResultList" />
									<dsp:getvalueof var="currentResultSize"
												value="${fn:length(registrySummaryResultList)}"
												scope="request" />
							 
									<c:set var="totalTabFlot" value="${totalCount/perPage}" /> 
									<c:set var="totalTab"
												value="${totalTabFlot+(1-(totalTabFlot%1))%1}"
												scope="request" />
									
									<dsp:droplet name="ForEach">				
										<dsp:param name="array" param="registrySummaryResultList" />			
										<dsp:oparam name="outputStart">
										
											<div id="pagTop"> 
													<dsp:include page="registry_pagination.jsp">
														<dsp:param name="currentResultSize"
																value="${currentResultSize}" />
														<dsp:param name="totalCount" value="${totalCount}" />
														<dsp:param name="perPage" value="${perPage}" />
		   											    <dsp:param name="totalTab" value="${totalTab}" />
													</dsp:include>
											</div>
											<div>
												<ul class="registrySearchResults bdrBottom noMarBot">
												
													<li id="registryFilterFormWrap" class="clearfix grid_12 alpha omega">		
														
											            <dsp:include page="registry_search_filter_form.jsp">
							                				<dsp:param name="showOnlySearchFields" value="false"  />
							                				<dsp:param name="formId" value="1"  />
							                			</dsp:include>
											        </li>
													<li class="subhead grid_12 alpha omega">
														<div class="noMarBot noMarTop noBorderBottom">
															<div class="grid_2 omega" style="height: 1px">
															</div>
															<div class="grid_2 alpha">
																<%-- <a href="registry_search_guest.jsp?sortPassString=NAME&pagFilterOpt=${perPage}&sortOrder=${sortOrderName}"
																						title="Registrants" class="sortContent"><bbbl:label
																								key="lbl_regsrchguest_Registrants"
																								language="${pageContext.request.locale.language}" />
																					</a>--%>
																<bbbl:label key="lbl_regsrchguest_Registrants"
																			language="${pageContext.request.locale.language}" />					
															</div>
															<c:if test="${appid eq 'BuyBuyBaby'}">
															<div class="grid_2 alpha">
																<%-- <a href="registry_search_guest.jsp?sortPassString=MAIDEN&pagFilterOpt=${perPage}&sortOrder=${sortOrderMName}"
																						title='<bbbl:label
																								key="lbl_regsrchguest_maiden_names"
																								language="${pageContext.request.locale.language}" />' class="sortContent"><bbbl:label
																								key="lbl_regsrchguest_maiden_names"
																								language="${pageContext.request.locale.language}" />
																					</a>--%>
																<bbbl:label key="lbl_regsrchguest_maiden_names"
																				language="${pageContext.request.locale.language}" /> 
															</div>
															</c:if>
															<div class="grid_2 alpha">
																<%-- <a href="registry_search_guest.jsp?sortPassString=EVENTTYPEDESC&pagFilterOpt=${perPage}&sortOrder=${sortOrderType}"
																						title="Event Type" class="sortContent"><bbbl:label
																								key="lbl_regsrchguest_EventType"
																								language="${pageContext.request.locale.language}" />
																					</a>--%> 
																<bbbl:label key="lbl_regsrchguest_EventType"
																			language="${pageContext.request.locale.language}" />					
															</div>
															<div class="grid_2 alpha">
																<%-- <a href="registry_search_guest.jsp?sortPassString=DATE&pagFilterOpt=${perPage}&sortOrder=${sortOrderDate}"
																						title="Date" class="sortContent"><bbbl:label key="lbl_regsrchguest_Date"
																								language="${pageContext.request.locale.language}" />
																					</a>
																--%>					 
																<bbbl:label key="lbl_regsrchguest_Date"
																			language="${pageContext.request.locale.language}" />
															</div>
															<div class="grid_1 alpha">
																<%-- <a href="registry_search_guest.jsp?sortPassString=STATE&pagFilterOpt=${perPage}&sortOrder=${sortOrderState}"
																						title="State" class="sortContent"><bbbl:label
																								key="lbl_regsrchguest_State"
																								language="${pageContext.request.locale.language}" />
																					</a>
																--%> 
															
																<c:choose>
																	<c:when test="${appid == 'BedBathCanada'}">
																		<bbbl:label key="lbl_registrants_statecanada"
																					language="${pageContext.request.locale.language}" />
																	  
																	</c:when>
																	<c:otherwise>
																<bbbl:label key="lbl_regsrchguest_State"
																			language="${pageContext.request.locale.language}" />
																	</c:otherwise>
															   </c:choose>			
															</div>
															<div class="grid_${gridValue} alpha">
																<%-- <a href="registry_search_guest.jsp?sortPassString=REGISTRYNUM&pagFilterOpt=${perPage}&sortOrder=${sortOrderRegNum}"
																						title='<bbbl:label
																								key="lbl_regsrchguest_Registry"
																								language="${pageContext.request.locale.language}" />' class="sortContent"><bbbl:label
																								key="lbl_regsrchguest_Registry"
																								language="${pageContext.request.locale.language}" />
																					</a>
																--%> 
																<bbbl:label key="lbl_regsrchguest_Registry"
																			language="${pageContext.request.locale.language}" />
															</div>
														</div>
													</li>
						
												
										
												</dsp:oparam>
										<dsp:oparam name="output">

											 <li>
												<c:set var="currentCount">
													<dsp:valueof param="count" />
												</c:set>
												<c:choose>
													<c:when test="${currentCount%2 == 0}">
														<div class="detail clearfix registryInformation noMarTop highlightedRow">													
													</c:when>
													<c:otherwise>
														<div class="detail clearfix registryInformation noMarTop">
													</c:otherwise>
												</c:choose>
															<div class="grid_2 alpha" style="height: 1px">
																<dsp:getvalueof var="regId" param="element.registryId" />
																<dsp:getvalueof var="eventType" param="element.eventType" />
																<dsp:a href="view_registry_guest.jsp">
																	<dsp:param name="registryId" value="${regId}" />
																	<dsp:param name="eventType" value="${eventType}" />
																	<dsp:param name="pwsurl" value="${pwsurl}" />
																	<dsp:param name="pwsToken" value="${pwsToken}" />
																	<dsp:param name="inventoryCallEnabled" value="${true}" />
																	<div id="${regId}"></div>
																	<!-- set up a unique selector id -->
																</dsp:a>
																<script type="text/javascript">
																	var sa_obj = {};
																	sa_obj['${regId}'] = '${regId}'+'^^'+'${eventType}'
																	sa_s22_unique_registry_id.push(sa_obj);
																</script>
																<div id="sa_s22_instagram"></div>
															</div>
						
															<div class="grid_2 alpha bold">
															
																
																<dsp:getvalueof var="eventType"
															param="element.eventType" />
																<dsp:getvalueof var="pRMaidenName"
															param="element.primaryRegistrantMaidenName" />
																<dsp:getvalueof var="cRMaidenName"
															param="element.coRegistrantMaidenName" />
																<dsp:getvalueof var="pwsurl" param="element.pwsurl" />
																<dsp:getvalueof var="pwsToken"
															param="element.personalWebsiteToken" />
																<dsp:getvalueof var="regId" param="element.registryId" />
											<dsp:a href="view_registry_guest.jsp">
										
																	<dsp:param name="registryId" value="${regId}" />
																	<dsp:param name="eventType" value="${eventType}" />
																	<dsp:param name="pwsurl" value="${pwsurl}" />
																	<dsp:param name="pwsToken" value="${pwsToken}" />
																	<dsp:param name="inventoryCallEnabled" value="${true}" />
																	
																<dsp:valueof param="element.primaryRegistrantFirstName" />
																<dsp:droplet name="IsEmpty">
																	 <dsp:getvalueof param="element.coRegistrantFirstName"
																id="fName">
																		<dsp:param value="<%=fName%>" name="value" />
																	 </dsp:getvalueof>
																	<dsp:oparam name="false">
																	 <c:set var="coRegName">
																	<dsp:valueof param="element.coRegistrantFirstName" />
																</c:set>
																		<c:if test="${not empty coRegName}">& <dsp:valueof
																		param="element.coRegistrantFirstName" />
																		</c:if>
																	</dsp:oparam>
																</dsp:droplet>
																</dsp:a>
		
																<c:if
															test="${not empty eventType && eventType eq 'Wedding' && not empty pwsurl && not empty pwsToken}">
																
																<c:set var="personalSite">${contextPath}<bbbc:config
																	key="personal_site_url" configName="ThirdPartyURLs" />${pwsToken}&registryId=${regId}
																</c:set>
																
																
																<p>
																	<a title="Personal Wedding Website"
																	href="${personalSite}" id="personalSite"
																	target="personalSite">
																		<bbbl:label key='lbl_mng_regitem_pwedsite'
																		language="${pageContext.request.locale.language}" />
																	</a>
																</p>
																</c:if>
															</div>
															<c:if test="${appid eq 'BuyBuyBaby'}">
																<div class="grid_2 alpha">
																	<dsp:droplet name="IsEmpty">
																		<dsp:getvalueof
																	param="element.primaryRegistrantMaidenName"
																	id="pMaidenName">
																			<dsp:param value="${pMaidenName}" name="value" />
																		</dsp:getvalueof>
																		<dsp:oparam name="false">
																			 <c:set var="pRegMaidenName">
																		<dsp:valueof
																			param="element.primaryRegistrantMaidenName" />
																	</c:set>
																				<c:if test="${not empty pRegMaidenName}">
																		<dsp:valueof
																			param="element.primaryRegistrantMaidenName" />
																				</c:if>
																		</dsp:oparam>
																		<dsp:oparam name="true">-</dsp:oparam>
																	</dsp:droplet>
																	<dsp:droplet name="IsEmpty">
																		<dsp:getvalueof param="element.coRegistrantMaidenName"
																	id="coMaidenName">
																			<dsp:param value="${coMaidenName}" name="value" />
																		</dsp:getvalueof>
																		<dsp:oparam name="false">
																			 <c:set var="coRegMaidenName">
																		<dsp:valueof param="element.coRegistrantMaidenName" />
																	</c:set>
																				<c:if test="${not empty coRegMaidenName}">& <dsp:valueof
																			param="element.coRegistrantMaidenName" />
																				</c:if>
																		</dsp:oparam>
																	</dsp:droplet>
																</div>
															</c:if>
															<div class="grid_2 alpha"><dsp:valueof
															param="element.eventType" />
																				</div>
															<div class="grid_2 alpha">
																<c:choose>
															    	<c:when test="${appid eq 'BedBathCanada'}">
															      		<dsp:valueof param="element.eventDateCanada" />
															      	</c:when>
															     	<c:otherwise>
															     		<dsp:valueof param="element.eventDate" />
															     	</c:otherwise>
															     </c:choose>
															</div>
															<!-- <li class="grid_${gridValue} alpha"><dsp:valueof
															param="element.state" />
																				</li> -->
															<div class="grid_1 alpha"><dsp:valueof
															param="element.state" />
																				</div>
															<!-- <li class="grid_${gridValue} alpha"> <dsp:valueof
															param="element.registryId" /> </li> -->
															<div class="grid_1 alpha"> <dsp:valueof
															param="element.registryId" /> </div>
															<div class="grid_2 alpha omega fl">
															<span class="button">
																<dsp:a href="view_registry_guest.jsp">
																	<dsp:param name="registryId" value="${regId}" />
																	<dsp:param name="eventType" value="${eventType}" />
																	<dsp:param name="pwsurl" value="${pwsurl}" />
																	<dsp:param name="pwsToken" value="${pwsToken}" />
																	<dsp:param name="inventoryCallEnabled" value="${true}" />
																	<bbbl:label key="lbl_regsrchguest_view_registry"
																	language="${pageContext.request.locale.language}" />
																</dsp:a>
                                                                                                                        </span>
															</div>
<style>
															div#sa_s22_searched_registry_image_parent {
                                                           margin: -20px 0 0 -20px;
                                                            }
															</style>
															</div>
															</li>
										</dsp:oparam>
										<dsp:oparam name="outputEnd">
												</ul>
											
				</div>
											<div id="pagBot" class="grid_12 alpha"> 
												<dsp:include page="registry_pagination.jsp">
														<dsp:param name="currentResultSize" value="${currentResultSize}" />
														<dsp:param name="totalCount" value="${totalCount}" />
														<dsp:param name="perPage" value="${perPage}" />
		   											    <dsp:param name="totalTab" value="${totalTab}" />
													</dsp:include>
											</div>
										</dsp:oparam>
									</dsp:droplet> <%-- End ForEach --%>
									
								</dsp:oparam>
								<dsp:oparam name="true"> 
									<div class="grid_12 alpha omega">
										<p class="error">
											<bbbl:label key="lbl_regsrchguest_sorrynoregistries" language="${pageContext.request.locale.language}" />
										</p>
									</div>
									
									
									
									
								</dsp:oparam>
							</dsp:droplet> <%-- End IsNull --%>
				
						</dsp:oparam>
						<dsp:oparam name="error"> 
							
														
                			<div id="registryFilterFormWrap" class="clearfix grid_12 alpha omega">
					            <dsp:include page="registry_search_filter_form.jsp">
	                				<dsp:param name="showOnlySearchFields" value="true"  />	
	                				<dsp:param name="formId" value="3"  />							                				                					
	                			</dsp:include>
						    </div>
						    
						    <dsp:getvalueof var="errorMsg" param="errorMsg" />
							<div class="grid_12 alpha omega"  id="noSearchResults">
								<p class="error">
									<bbbl:label key="${errorMsg}"
										language="${pageContext.request.locale.language}" />
								</p>
							
							
								<h3>	
									<bbbl:label key="lbl_regsrchguest_searchtips"
												language="${pageContext.request.locale.language}" />
								</h3>
								
				                
				                <ul class="noResultsTips grid_6">
									<li><bbbl:label key="lbl_regsrchguest_search_tips1"
													language="${pageContext.request.locale.language}" />
												</li>
									<li><bbbl:label key="lbl_regsrchguest_search_tips2"
													language="${pageContext.request.locale.language}" />
												</li>
									<li><bbbl:label key="lbl_regsrchguest_search_tips3"
													language="${pageContext.request.locale.language}" />
												</li>
									<li><bbbl:label key="lbl_regsrchguest_search_tips4"
													language="${pageContext.request.locale.language}" />
									</li>
								</ul>
							</div>
						</dsp:oparam>
						<dsp:oparam name="empty"> 
												
							<div id="registryFilterFormWrap" class="clearfix grid_12 alpha omega">
					            <dsp:include page="registry_search_filter_form.jsp">
	                				<dsp:param name="showOnlySearchFields" value="true"  />	
	                				<dsp:param name="formId" value="2"  />							                				                					
	                			</dsp:include>
						    </div>
						    
						    <div class="grid_12 alpha omega marTop_10" id="noSearchResults">
								<h3>
									<bbbl:label key="lbl_regsrchguest_sorrynoregistries"
												language="${pageContext.request.locale.language}" />
								</h3>
							
								<p><bbbl:label key="lbl_regsrchguest_try_again"
										language="${pageContext.request.locale.language}" /></p>
							
				                
								<h3>
									<bbbl:label key="lbl_regsrchguest_searchtips"
												language="${pageContext.request.locale.language}" />
								</h3>
								
				                
				                <ul class="noResultsTips grid_6">
									<li><bbbl:label key="lbl_regsrchguest_search_tips1"
													language="${pageContext.request.locale.language}" />
												</li>
									<li><bbbl:label key="lbl_regsrchguest_search_tips2"
													language="${pageContext.request.locale.language}" />
												</li>
									<li><bbbl:label key="lbl_regsrchguest_search_tips3"
													language="${pageContext.request.locale.language}" />
												</li>
									<li><bbbl:label key="lbl_regsrchguest_search_tips4"
													language="${pageContext.request.locale.language}" />
									</li>
								</ul>
							</div>
									
							
						</dsp:oparam>								
					</dsp:droplet>
				<%-- End GiftRegistryPaginationDroplet --%>
		
				</dsp:oparam>
			</dsp:droplet>
			
			<dsp:form id="registryFilterFormHidden" formid="registryFilterFormHidden" iclass="grid_12 alpha omega" action="bbb_search_registry" method="post" style="display:none">
					<dsp:input 	type="hidden" bean="GiftRegistryFormHandler.registrySearchVO.firstName" name="bbRegistryFirstName" maxlength="30" value="${sessFirstName}" />
					<dsp:input 	type="hidden" bean="GiftRegistryFormHandler.registrySearchVO.lastName" name="bbRegistryLastName" maxlength="30" value="${sessLastName}" />
					<dsp:input 	type="hidden" bean="GiftRegistryFormHandler.registrySearchVO.event" name="bbRegistryEventType" maxlength="30" value="${sessEventType}" />						
					<dsp:input 	type="hidden" bean="GiftRegistryFormHandler.registrySearchVO.state" name="bbRegistryState" maxlength="30" value="${sessState}" />						
					<dsp:input 	type="hidden" bean="GiftRegistryFormHandler.registrySearchVO.registryId" name="bbRegistryNumber" value="${sessRegistryId}" />
					<dsp:input 	type="hidden" bean="GiftRegistryFormHandler.registrySearchVO.sort" name="sort" value="${sessSort}" />	
					<dsp:input 	type="hidden" bean="GiftRegistryFormHandler.registrySearchVO.sortSeqOrder" name="sortSeqOrder" value="${sortOrder}" />
							
					<dsp:input bean="GiftRegistryFormHandler.hidden" type="hidden" value="3" />					
					<dsp:input id="searchRegistry" bean="GiftRegistryFormHandler.registrySearch" type="submit" value="submit" />			
			</dsp:form>			
			
			<%--
			<div class="grid_6 container alpha marTop_10 searchRegistry">
                <div class="spacer">
			       <h3>
						<bbbl:label key="lbl_regsrchguest_searchregistries"
								language="${pageContext.request.locale.language}" />
					</h3>
					<h3>
						<bbbl:label key="lbl_regsrchguest_searchagain"
								language="${pageContext.request.locale.language}" />
					</h3>
					
			        <dsp:form id="findRegistry" iclass="clearfix"
							action="bbb_search_registry" method="post">
			            <div class="grid_2 alpha omega">
			                <div class="grid_2 alpha">
			                    <div class="input width_2 alpha">
			                        <div class="label">
			                            <label id="lblbbRegistryFirstName"
												for="bbRegistryFirstName"><bbbl:label
													key="lbl_regsrchguest_firstname"
													language="${pageContext.request.locale.language}" />
									</label>
			                        </div>
			                        <div class="text">
			                           <dsp:input type="text"
												bean="GiftRegistryFormHandler.registrySearchVO.firstName"
												id="bbRegistryFirstName" name="bbRegistryFirstName"
												maxlength="30" value="">
                                            <dsp:tagAttribute
													name="aria-required" value="false" />
                                            <dsp:tagAttribute
													name="aria-labelledby"
													value="lblbbRegistryFirstName errorbbRegistryFirstName" />
                                        </dsp:input>
			                        </div>
			                    </div>
								<div class="clear"></div>
			                </div>
			                <div class="input grid_2 alpha formElement">
			                    <div class="label">
			                        <label id="lblbbRegistryLastName"
											for="bbRegistryLastName"><bbbl:label
												key="lbl_regsrchguest_lastname"
												language="${pageContext.request.locale.language}" />
								</label>
			                    </div>
			                    <div class="text">
			                        <dsp:input type="text"
											bean="GiftRegistryFormHandler.registrySearchVO.lastName"
											id="bbRegistryLastName" name="bbRegistryLastName"
											maxlength="30" value="">
                                        <dsp:tagAttribute
												name="aria-required" value="false" />
                                        <dsp:tagAttribute
												name="aria-labelledby"
												value="lblbbRegistryLastName errorbbRegistryLastName" />
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
			                            <label id="lblbbRegistryNumber"
											for="bbRegistryNumber"><bbbl:label
												key="lbl_regsrchguest_registrynumber"
												language="${pageContext.request.locale.language}" />
								</label>
			                        </div>
			                        <div class="text">
			                            <dsp:input type="text"
											bean="GiftRegistryFormHandler.registrySearchVO.registryId"
											id="bbRegistryNumber" name="bbRegistryNumber" value="">
                                            <dsp:tagAttribute
												name="aria-required" value="false" />
                                            <dsp:tagAttribute
												name="aria-labelledby"
												value="lblbbRegistryNumber errorbbRegistryNumber" />
                                        </dsp:input>
			                        </div>
			                    </div>
						<div class="clear"></div>
			            </div>
			            <dsp:input bean="GiftRegistryFormHandler.hidden"
								type="hidden" value="3" />
						<dsp:input bean="GiftRegistryFormHandler.registrySearchSuccessURL"
								type="hidden" value="registry_search_guest.jsp" />
						<dsp:input bean="GiftRegistryFormHandler.registrySearchErrorURL"
								type="hidden" value="registry_search_guest.jsp" />
			            <div class="clear"></div>
			            <div class="button cf cb inlineBlock">
							<c:set var="findButton">
								<bbbl:label key='lbl_find_reg_submit_button'
										language="${pageContext.request.locale.language}"></bbbl:label>
							</c:set>
			                 <dsp:input id="searchRegistry"
									bean="GiftRegistryFormHandler.registrySearch" type="submit"
									value="${findButton}">
                                <dsp:tagAttribute name="aria-pressed"
										value="false" />
                                <dsp:tagAttribute name="aria-labelledby"
										value="searchRegistry" />
                                <dsp:tagAttribute name="role"
										value="button" />
                            </dsp:input>
			            </div>
					</dsp:form>
                    <div class="clear"></div>
                </div>
			</div>
			<div class="grid_6 omega container whyRegister omega marTop_10">
                <div class="spacer">
				<h3>
					<bbbl:label key="lbl_regsrchguest_searchtips"
								language="${pageContext.request.locale.language}" />
				</h3>
				<ul>
					<li><bbbl:label key="lbl_regsrchguest_search_tips1"
									language="${pageContext.request.locale.language}" />
								</li>
					<li><bbbl:label key="lbl_regsrchguest_search_tips2"
									language="${pageContext.request.locale.language}" />
								</li>
					<li><bbbl:label key="lbl_regsrchguest_search_tips3"
									language="${pageContext.request.locale.language}" />
								</li>
					<li><bbbl:label key="lbl_regsrchguest_search_tips4"
									language="${pageContext.request.locale.language}" />
								</li>
				</ul> 
                <div class="clear"></div>
                </div>
			</div>
			--%>
		</div>
 	</div>


		<script type="text/javascript">
			var totalCount = '${totalCount}';
			var sa_page='11';

			(function() {function sa_async_load() {
				var sa = document.createElement('script');
				sa.type = 'text/javascript';
				sa.async = true;
				sa.src = '${saSrc}';
				var sax = document.getElementsByTagName('script')[0];
				sax.parentNode.insertBefore(sa, sax);}
				if (window.attachEvent) {
				window.attachEvent('onload', sa_async_load);
				}else {
				window.addEventListener('load', sa_async_load,false);
				}
			})();
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
				
				
	<<%--BBBSL-6813  Include Certona Call on registry search --%> 
		<script type="text/javascript">
			var resx = new Object();
			resx.appid = "${appIdCertona}"; 
			resx.pageid = "${pageIdCertona}";
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
	 			value="src=${src};type=${type};cat=${cat};u10=null,u11=null"/>
	 		</dsp:include>
 		</c:if>
		DoubleClick Floodlight END --%>	
													</bbb:pageContainer>
</dsp:page>