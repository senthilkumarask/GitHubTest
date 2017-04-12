<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryPaginationDroplet" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/StatesLookup" />

	<dsp:getvalueof var="appid" bean="Site.id" />
	<c:choose>
		<c:when test="${not empty appid && appid eq 'TBS_BuyBuyBaby'}">
			<c:set var="pageVariation" value="bb" scope="request" />
		</c:when>
		<c:otherwise>
			<c:set var="pageVariation" value="by" scope="request" />
		</c:otherwise>
	</c:choose>
	
	<bbb:pageContainer>
		<jsp:attribute name="section">registry</jsp:attribute>
		<jsp:attribute name="pageVariation">${pageVariation}</jsp:attribute>
		<jsp:attribute name="pageWrapper">useFB useCertonaJs manageRegistry givingAGift</jsp:attribute>
        <jsp:attribute name="bodyClass">registry-search</jsp:attribute>
		<jsp:body>
	
		<dsp:getvalueof var="appid" bean="Site.id" />
		<c:choose>
			<c:when test="${appid eq 'TBS_BuyBuyBaby'}">
				<c:set var="gridValue" value="1" scope="request" />
			</c:when>
			<c:otherwise>
				<c:set var="gridValue" value="2" scope="request" />
			</c:otherwise>
		</c:choose>
	
    	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
	
		<script type="text/javascript">
			var totalCount = '0';
		</script>
	
	
		<div id="content" class="" role="main">
			<div class="row">
				<h1>
					<bbbl:label key="lbl_regsrchguest_givinggift" language="${pageContext.request.locale.language}" />
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
							
								<dsp:getvalueof var="totalCount" param="totalCount" scope="request" />
						 		<dsp:getvalueof var="registrySummaryResultList" param="registrySummaryResultList" />
								<dsp:getvalueof var="currentResultSize" value="${fn:length(registrySummaryResultList)}" scope="request" />
						 
								<c:set var="totalTabFlot" value="${totalCount/perPage}" /> 
								<c:set var="totalTab" value="${totalTabFlot+(1-(totalTabFlot%1))%1}" scope="request" />
								
								<dsp:droplet name="ForEach">				
									<dsp:param name="array" param="registrySummaryResultList" />			
									<dsp:oparam name="outputStart">
									
								            <dsp:include page="registry_search_filter_form.jsp">
				                				<dsp:param name="showOnlySearchFields" value="false"  />
				                				<dsp:param name="formId" value="1"  />
				                				<dsp:param name="results" value="results"/>
				                			</dsp:include>
                                            <br/><hr/>
				                			<div id="pagTop" class="row"> 
                                                <dsp:include page="registry_pagination.jsp">
                                                    <dsp:param name="currentResultSize" value="${currentResultSize}" />
                                                    <dsp:param name="totalCount" value="${totalCount}" />
                                                    <dsp:param name="perPage" value="${perPage}" />
                                                    <dsp:param name="totalTab" value="${totalTab}" />
                                                </dsp:include>
                                            </div>
                                            <hr/>
                                            <div class="small-3 large-2 columns no-padding">
											    <bbbl:label key="lbl_regsrchguest_Registrants" language="${pageContext.request.locale.language}" />
                                            </div>
											<c:if test="${appid eq 'TBS_BuyBuyBaby'}">
                                            <div class="small-3 large-2 columns no-padding">
											    <bbbl:label key="lbl_regsrchguest_maiden_names" language="${pageContext.request.locale.language}" />
                                            </div>
											</c:if>
											<div class="small-12 large-2 columns no-padding hide-for-small">
											    <bbbl:label key="lbl_regsrchguest_EventType" language="${pageContext.request.locale.language}" />
											</div>
                                            <div class="small-3 large-2 columns">				
											    <bbbl:label key="lbl_regsrchguest_Date"  language="${pageContext.request.locale.language}" />
											</div>
											<c:choose>
												<c:when test="${appid == 'TBS_BedBathCanada'}">
	                                                <div class="small-12 large-2 columns no-padding hide-for-small">
														<bbbl:label key="lbl_registrants_statecanada" language="${pageContext.request.locale.language}" />
	                                                </div>
												</c:when>
												<c:when test="${appid == 'TBS_BuyBuyBaby'}">
													<div class="small-12 large-1 columns no-padding hide-for-small">
														<bbbl:label key="lbl_regsrchguest_State" language="${pageContext.request.locale.language}" />
                                                	</div>
												</c:when>
												<c:otherwise>
	                                                <div class="small-12 large-2 columns no-padding hide-for-small">
														<bbbl:label key="lbl_regsrchguest_State" language="${pageContext.request.locale.language}" />
	                                                </div>
												</c:otherwise>
										   </c:choose>
										   <div class="small-5 large-2 columns">			
										      <bbbl:label key="lbl_regsrchguest_Registry" language="${pageContext.request.locale.language}" />
										   </div>
										   <br/><hr/>
										</dsp:oparam>
										<dsp:oparam name="output">
                                            <div class="searchResultsRow row">
											<c:set var="currentCount">
												<dsp:valueof param="count" />
											</c:set>
											<%-- <c:choose>
												<c:when test="${currentCount%2 == 0}">
													<ul	class="detail clearfix registryInformation noMarTop highlightedRow">													
												</c:when>
												<c:otherwise>
													<ul class="detail clearfix registryInformation noMarTop">
												</c:otherwise>
											</c:choose> --%>
											
											<div class="small-3 large-2 columns"><dsp:valueof param="element.primaryRegistrantFirstName" />
											<dsp:getvalueof var="eventType" param="element.eventType" />
											<dsp:getvalueof var="pRMaidenName" param="element.primaryRegistrantMaidenName" />
											<dsp:getvalueof var="cRMaidenName" param="element.coRegistrantMaidenName" />
											<dsp:getvalueof var="pwsurl" param="element.pwsurl" />
											<dsp:getvalueof var="pwsToken" param="element.personalWebsiteToken" />
											<dsp:getvalueof var="regId" param="element.registryId" />
									
											<dsp:droplet name="IsEmpty">
												 <dsp:getvalueof param="element.coRegistrantFirstName" id="fName">
													<dsp:param value="<%=fName%>" name="value" />
												</dsp:getvalueof>
												<dsp:oparam name="false">
													<c:set var="coRegName">
														<dsp:valueof param="element.coRegistrantFirstName" />
													</c:set>
													<c:if test="${not empty coRegName}">& <dsp:valueof param="element.coRegistrantFirstName" />
													</c:if>
												</dsp:oparam>
                                                <dsp:oparam name="true">   
                                                </dsp:oparam>
											</dsp:droplet>
												</div>		
											<c:if test="${not empty eventType && eventType eq 'Wedding' && not empty pwsurl && not empty pwsToken}">
												<div class="small-3 large-2 columns"><c:set var="personalSite">${contextPath}<bbbc:config
													key="personal_site_url" configName="ThirdPartyURLs" />${pwsToken}&registryId=${regId}
												</c:set>
												<p>
													<a title="Personal Wedding Website"
													href="${personalSite}" id="personalSite"
													target="personalSite">
														<bbbl:label key='lbl_mng_regitem_pwedsite'
														language="${pageContext.request.locale.language}" />
													</a>
												</p></div>
											</c:if>
											<c:if test="${appid eq 'TBS_BuyBuyBaby'}">
												<div class="small-3 large-2 columns">
												<dsp:droplet name="IsEmpty">
													<dsp:getvalueof param="element.primaryRegistrantMaidenName" id="pMaidenName">
														<dsp:param value="${pMaidenName}" name="value" />
													</dsp:getvalueof>
													<dsp:oparam name="false">
														 <c:set var="pRegMaidenName">
															<dsp:valueof param="element.primaryRegistrantMaidenName" />
														</c:set>
														<c:if test="${not empty pRegMaidenName}">
															<dsp:valueof param="element.primaryRegistrantMaidenName" />
														</c:if>
													</dsp:oparam>
													<dsp:oparam name="true">-</dsp:oparam>
												</dsp:droplet>
												<dsp:droplet name="IsEmpty">
													<dsp:getvalueof param="element.coRegistrantMaidenName" id="coMaidenName">
														<dsp:param value="${coMaidenName}" name="value" />
													</dsp:getvalueof>
													<dsp:oparam name="false">
														 <c:set var="coRegMaidenName">
															<dsp:valueof param="element.coRegistrantMaidenName" />
														</c:set>
														<c:if test="${not empty coRegMaidenName}">
															& <dsp:valueof param="element.coRegistrantMaidenName" />
														</c:if>
													</dsp:oparam>
												</dsp:droplet>  </div>
											</c:if>
											<div class="small-12 large-2 columns hide-for-small"><dsp:valueof param="element.eventType" /></div>
											<c:choose>
										    	<c:when test="${appid eq 'TBS_BedBathCanada'}">
										      		<div class="small-12 large-2 columns"><dsp:valueof param="element.eventDateCanada" /></div>
										      	</c:when>
										     	<c:otherwise>
										     		<div class="small-3 large-2 columns"><dsp:valueof param="element.eventDate" /></div>
										     	</c:otherwise>
										     </c:choose>
										     &nbsp;
										     <c:choose>
										    	<c:when test="${appid eq 'TBS_BuyBuyBaby'}">
										      		<div class="small-12 large-1 columns"><dsp:valueof param="element.state" /></div>
										      	</c:when>
										     	<c:otherwise>
										     		<div class="small-12 large-2 columns hide-for-small"><dsp:valueof param="element.state" /></div>
										     	</c:otherwise>
										     </c:choose>
											<div class="small-3 large-2 columns"><dsp:valueof param="element.registryId" /></div>
											<div class="small-3 large-1 columns">
											<%-- <dsp:getvalueof var="istransient" bean="/atg/userprofiling/Profile.transient"/>
											<c:choose>
												<c:when test="${istransient eq 'true'}"> --%>
													<dsp:a href="view_registry_guest.jsp" iclass="tiny button secondary">
														<dsp:param name="registryId" value="${regId}" />
														<dsp:param name="eventType" value="${eventType}" />
														<dsp:param name="pwsurl" value="${pwsurl}" />
														<dsp:param name="pwsToken" value="${pwsToken}" />
														<%-- <bbbl:label key="lbl_regsrchguest_view_registry"
														language="${pageContext.request.locale.language}" /> --%>
	                                                    View
													</dsp:a>
												<%-- </c:when>
												<c:otherwise>
													<dsp:a href="view_registry_owner.jsp" iclass="tiny button secondary">
														<dsp:param name="registryId" value="${regId}" />
														<dsp:param name="eventType" value="${eventType}" />
														<dsp:param name="pwsurl" value="${pwsurl}" />
														<dsp:param name="pwsToken" value="${pwsToken}" />
														<bbbl:label key="lbl_regsrchguest_view_registry"
														language="${pageContext.request.locale.language}" />
	                                                    View
													</dsp:a>
												</c:otherwise>
											</c:choose> --%>
											</div>
                                            </div>
										</dsp:oparam>
										<dsp:oparam name="outputEnd">
            								</div>
            							</dsp:oparam>
						</dsp:droplet> <%-- End ForEach --%>
								
					</dsp:oparam>
					<dsp:oparam name="true"> 
						<div class="small-12 columns">
							<p class="error">
								<bbbl:label key="lbl_regsrchguest_sorrynoregistries" language="${pageContext.request.locale.language}" />
							</p>
						</div>
					</dsp:oparam>
				</dsp:droplet> <%-- End IsNull --%>
					</div>
			</dsp:oparam>
			<dsp:oparam name="error"> 
             	<div id="registryFilterFormWrap" class="clearfix grid_12 alpha omega">
		            <dsp:include page="registry_search_filter_form.jsp">
           				<dsp:param name="showOnlySearchFields" value="true"  />	
           				<dsp:param name="formId" value="3"  />
           				<dsp:param name="results" value=""/>							                				                					
           			</dsp:include>
			    </div>
			    
			    <dsp:getvalueof var="errorMsg" param="errorMsg" />
				<div class="grid_12 alpha omega"  id="noSearchResults">
					<p class="error">
						<bbbl:label key="${errorMsg}" language="${pageContext.request.locale.language}" />
					</p>
					<h3>	
						<bbbl:label key="lbl_regsrchguest_searchtips" language="${pageContext.request.locale.language}" />
					</h3>
	                <ul class="noResultsTips grid_6">
						<li>
							<bbbl:label key="lbl_regsrchguest_search_tips1" language="${pageContext.request.locale.language}" />
						</li>
						<li>
							<bbbl:label key="lbl_regsrchguest_search_tips2" language="${pageContext.request.locale.language}" />
						</li>
						<li>
							<bbbl:label key="lbl_regsrchguest_search_tips3" language="${pageContext.request.locale.language}" />
						</li>
						<li>
							<bbbl:label key="lbl_regsrchguest_search_tips4" language="${pageContext.request.locale.language}" />
						</li>
					</ul>
				</div>
			</dsp:oparam>
			<dsp:oparam name="empty"> 
											
				<div id="registryFilterFormWrap" class="clearfix grid_12 alpha omega">
		            <dsp:include page="registry_search_filter_form.jsp">
              				<dsp:param name="showOnlySearchFields" value="true"  />	
              				<dsp:param name="formId" value="2"  />	
              				<dsp:param name="results" value=""/>						                				                					
              			</dsp:include>
			    </div>
					    
					    <div class="grid_12 alpha omega marTop_10" id="noSearchResults">
							<h3>
								<bbbl:label key="lbl_regsrchguest_sorrynoregistries" language="${pageContext.request.locale.language}" />
							</h3>
							<p>
								<bbbl:label key="lbl_regsrchguest_try_again" language="${pageContext.request.locale.language}" />
							</p>
							<h3>
								<bbbl:label key="lbl_regsrchguest_searchtips" language="${pageContext.request.locale.language}" />
							</h3>
			                <ul class="noResultsTips grid_6">
								<li>
									<bbbl:label key="lbl_regsrchguest_search_tips1" language="${pageContext.request.locale.language}" />
								</li>
								<li>
									<bbbl:label key="lbl_regsrchguest_search_tips2" language="${pageContext.request.locale.language}" />
								</li>
								<li>
									<bbbl:label key="lbl_regsrchguest_search_tips3" language="${pageContext.request.locale.language}" />
								</li>
								<li>
									<bbbl:label key="lbl_regsrchguest_search_tips4" language="${pageContext.request.locale.language}" />
								</li>
							</ul>
						</div>
								
						
					</dsp:oparam>								
				</dsp:droplet>
				<%-- End GiftRegistryPaginationDroplet --%>
			
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

			<script type="text/javascript">
				var totalCount = '${totalCount}';
			</script>
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
	</div></div></div>
	</bbb:pageContainer>
</dsp:page>