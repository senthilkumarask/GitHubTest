<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>    
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryAnnouncementCardDroplet"/>
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean" />   
	<dsp:importbean bean="/atg/multisite/Site"/>  
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof id="servername" idtype="java.lang.String" bean="/OriginatingRequest.servername"/>
	<dsp:getvalueof id="scheme" idtype="java.lang.String" bean="/OriginatingRequest.scheme"/>
	<c:if test="${not empty appid && appid eq 'BedBathUS'}">
	 <c:set var="pageVariation" value="br" scope="request" />
	</c:if>
	<bbb:pageContainer>
		<jsp:attribute name="section">accounts</jsp:attribute>
		<jsp:attribute name="pageWrapper">announcement</jsp:attribute>
		<jsp:attribute name="pageVariation">${pageVariation}</jsp:attribute>
		<jsp:body>
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
						    <c:when test="${siteId eq 'BedBathUS' || siteId eq 'BedBathCanada'}">
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
					</span> <span class="bold"><bbbl:label key="lbl_leftnavguide_registryannouncement" language="${pageContext.request.locale.language}" /></span>
				</div>
				<c:import url="/cms/left_navigation.jsp">
					<c:param name="pageName">RegistryAnnouncement</c:param>
				</c:import>
				
		           
				<div class="grid_9 clearfix">     
					<dsp:droplet name="GiftRegistryAnnouncementCardDroplet">
						<dsp:param name="profile" bean="Profile"/>
						<dsp:param name="siteId" value="${appid}"/>
					    <dsp:oparam name="output">
			                
			                <jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request"/>
							<c:set target="${placeHolderMap}" property="imagePath"><dsp:valueof value="${imagePath}"/></c:set>
							<bbbt:textArea key="txt_reg_anncard_head_content1" placeHolderMap="${placeHolderMap}" language ="${pageContext.request.locale.language}"/>
			                
			                <dsp:form id="announcementCardsMain" action="${contextPath}/giftregistry/registry_confirmation.jsp">
			                
			                	<dsp:setvalue bean="GiftRegSessionBean.clear" value="true"/>	

			                    <div class="grid_5 omega">
									<bbbt:textArea key="txt_reg_anncard_msg_content2" language ="${pageContext.request.locale.language}"/>

				                    <%-- Drop down for multiple registries --%>    
				                    <dsp:getvalueof param="count" idtype="java.lang.Integer" id="count">
									<c:if test="${count>1}">
										<%-- multiple registries --%>
										<div class="input width_3 omega">
											<div class="label">
												<label id="lblwedding" for="wedding"></label>
											</div>												
											<div class="select">
												<dsp:select bean="GiftRegSessionBean.registryVO.registryId" name="wedding" id="wedding" 
														iclass="selector_primary">	
                                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lblwedding errorwedding"/>
													<dsp:droplet name="ForEach">
														<dsp:param name="array" param="registries"/>
														<dsp:param name="elementName" value="registry" />
														<dsp:oparam name="output">
															<dsp:option paramvalue="registry.registryId">
																<dsp:valueof param="registry.eventType" /> - <dsp:valueof param="registry.eventDate" />
															</dsp:option>
														</dsp:oparam>	
													</dsp:droplet>
												</dsp:select>
											</div>
										</div>
									</c:if>
									<c:if test="${count==1}">
										<%-- one registry --%>
										<dsp:input type="hidden" bean="GiftRegSessionBean.registryVO.registryId" paramvalue="registries[0].registryId" />
									</c:if>					
								</dsp:getvalueof>
				                        
		                        <div class="marTop_10 marBottom_20">
		                            
		                            <dsp:input name="registryAnnouncement" type="radio" 
										bean="GiftRegSessionBean.registryVO.numRegAnnouncementCards" id="reg25"  value="25" checked="true">
                                        <dsp:tagAttribute name="aria-checked" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblreg25 lblregistryAnnouncement"/>
                                    </dsp:input>
		                            <div class="label">
										<label id="lblreg25" for="reg25" class="marRight_10"><bbbl:label key="lbl_announce_send_25" language ="${pageContext.request.locale.language}"/></label>
									</div>
		                            <dsp:input name="registryAnnouncement" type="radio"			
										bean="GiftRegSessionBean.registryVO.numRegAnnouncementCards" id="reg50"  value="50">
                                        <dsp:tagAttribute name="aria-checked" value="false"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblreg50 lblregistryAnnouncement"/>
                                    </dsp:input>
									<div class="label">
										<label id="lblreg50" for="reg50" class="marRight_10"><bbbl:label key="lbl_announce_send_50" language ="${pageContext.request.locale.language}"/></label>
									</div>
		                            <dsp:input name="registryAnnouncement" type="radio" 
										bean="GiftRegSessionBean.registryVO.numRegAnnouncementCards" id="reg75"  value="75">
                                        <dsp:tagAttribute name="aria-checked" value="false"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblreg75 lblregistryAnnouncement"/>
                                    </dsp:input>
		                            <div class="label">
										<label id="lblreg75" for="reg75" class="marRight_10"><bbbl:label key="lbl_announce_send_75" language ="${pageContext.request.locale.language}"/></label>
									</div>
		                            <dsp:input name="registryAnnouncement" type="radio" 
										bean="GiftRegSessionBean.registryVO.numRegAnnouncementCards" id="reg100"  value="100">
                                        <dsp:tagAttribute name="aria-checked" value="false"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblreg100 lblregistryAnnouncement"/>
                                    </dsp:input>	
									<div class="label">	
										<label id="lblreg100" for="reg100" class="marRight_10"><bbbl:label key="lbl_announce_send_100" language ="${pageContext.request.locale.language}"/></label>
									</div>
		                            <label id="lblregistryAnnouncement" for="registryAnnouncement" class="error" generated="true"></label>
									<div class="clear"></div>
		                        </div>

		                        <%-- dsp:input bean="GiftRegistryFormHandler.successURL" type="hidden" 
									value="${contextPath}/giftregistry/registry_confirmation.jsp"/>
								<dsp:input bean="GiftRegistryFormHandler.errorURL" type="hidden" 
									value="${contextPath}/giftregistry/reg_announcement_cards.jsp"/> --%>
								<dsp:input bean="GiftRegistryFormHandler.fromPage" type="hidden" 
									value="giftRegAnnouncementCard"/>    
		                        <div class="button button_active alpha">
		                            <dsp:input type="submit" value="Submit" 
									bean="GiftRegistryFormHandler.announcementCardCount" id="registryAnnouncementSubmit" >
                                        <dsp:tagAttribute name="aria-pressed" value="false"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="registryAnnouncementSubmit"/>
                                        <dsp:tagAttribute name="role" value="button"/>
                                    </dsp:input>
		                        </div>
                        
		                    </div>
		                </dsp:form>
					</dsp:oparam>
				    <dsp:oparam name="error">
						<div class="error marTop_20 padTop_10">
							<dsp:valueof param="errorMsg"/>
						 </div>
					</dsp:oparam>
					  <dsp:oparam name="empty">
						<div class="error marTop_20 padTop_10">
							<dsp:valueof param="errorMsg"/>
						</div>
					</dsp:oparam>
				</dsp:droplet>
				</div>
            </div>
		</jsp:body>
<jsp:attribute name="footerContent">
	<script type="text/javascript">
			if (typeof s !== 'undefined') {
				s.channel = 'Registry';
				s.pageName = 'Registry Announcement Cards'; 
				s.prop1 = 'Registry';// page title
				s.prop2 = 'Registry';// category level 1
				s.prop3 = 'Registry';// category level 1
				s.prop6 = '${pageContext.request.serverName}';
				s.eVar9 = '${pageContext.request.serverName}';
				var s_code = s.t();
				if (s_code)
					document.write(s_code);
			}
	</script>
  </jsp:attribute>		
	</bbb:pageContainer>
</dsp:page>