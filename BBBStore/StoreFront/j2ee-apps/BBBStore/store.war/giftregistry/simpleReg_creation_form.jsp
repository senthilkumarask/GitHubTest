<dsp:page>
<%@ page import="com.bbb.constants.BBBCoreConstants" %>
	<c:if test="${not empty sessionScope.Registry_Success_Back_Btn_Url && sessionScope.registry_Success_Back_Btn_Url_Flag =='redirectEnabled'}">
		<c:redirect url="${sessionScope.Registry_Success_Back_Btn_Url}"/>
		<c:set var="registry_Success_Back_Btn_Url_Flag" value="none" scope="session"/>   
	</c:if>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryVODroplet" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryTypeNameDroplet"/>
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetValidRegistryTypeDroplet"/>
    <dsp:importbean bean="/com/bbb/simplifyRegistry/droplet/SimpleRegFieldsDroplet" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFlyoutDroplet"/>
    <dsp:importbean bean="/atg/multisite/Site"/>
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>	
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:importbean bean="com/bbb/selfservice/SearchStoreFormHandler" />
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/com/bbb/selfservice/StateDroplet" />
	<c:set var="lbl_login_frag__forgot_password"><bbbl:label key="lbl_login_frag__forgot_password" language="${pageContext.request.locale.language}" /></c:set>

	<dsp:droplet name="GetValidRegistryTypeDroplet">
		<dsp:param name="siteId" value="${appid}"/>
		<dsp:param name="regType" param="regType"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="event" param="validRegType" scope="request"/>
		</dsp:oparam>
		<dsp:oparam name="error">
			<dsp:getvalueof var="event" param="regType" scope="request"/>
		</dsp:oparam>
	</dsp:droplet>
  	<dsp:setvalue bean="SessionBean.registryTypesEvent" value="${event}"/>
	
    <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
    
    <c:choose>
        <c:when test="${appid == 'BuyBuyBaby'}">
		            <c:set var="pageVariation" value="bb" scope="request" />
		            <c:set var="leftgrid" value="grid_5" scope="request" />
                    <c:set var="rightgrid" value="grid_5" scope="request" />
                    <c:set var="firstnamegrid" value="grid_2" scope="request" />
                    <c:set var="lastnamegrid" value="grid_2" scope="request" />
                    <c:set var="rightgridPassword" value="grid_4" scope="request" />
                    <c:choose>
		                <c:when test="${event == 'BA1' }">
		                     <c:set var="gridClass1">grid_8</c:set>
		                </c:when>
		                <c:otherwise>
		                    <c:set var="gridClass1">grid_10</c:set>
		                </c:otherwise>
            		</c:choose>

        </c:when>
        <c:otherwise>
            <c:choose>
                <c:when test="${event == 'BA1' }">
                    <c:set var="pageVariation" value="by" scope="request" />
                    <c:set var="leftgrid" value="grid_5" scope="request" />
                    <c:set var="rightgrid" value="grid_5" scope="request" />
                     <c:set var="rightgridPassword" value="grid_4" scope="request" />
                    <c:set var="firstnamegrid" value="grid_2" scope="request" />
                    <c:set var="lastnamegrid" value="grid_2" scope="request" />
                     <c:set var="gridClass1">grid_8</c:set>

                </c:when>
                <c:otherwise>
                    <c:set var="pageVariation" value="br" scope="request" />
                    <c:set var="leftgrid" value="grid_3" scope="request" />
                    <c:set var="firstnamegrid" value="grid_2" scope="request" />
          			<c:set var="lastnamegrid" value="grid_2" scope="request" />
                    <c:set var="rightgrid" value="grid_6" scope="request" />
                     <c:set var="rightgridPassword" value="grid_3" scope="request" />
                    <c:set var="gridClass1">grid_10</c:set>
                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>
	
 	<c:set var="BedBathUSSite"><bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" /></c:set>
	<c:set var="BuyBuyBabySite"><bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" /></c:set>
    <c:set var="BedBathCanadaSite"><bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" /></c:set>
	<c:set var="pageWrapper" value="createSimpleRegistry useStoreLocator useGoogleAddress" scope="request" />
		
    <bbb:pageContainer>
        <jsp:attribute name="section">createSimpleRegistry</jsp:attribute>
        <jsp:attribute name="pageVariation">${pageVariation}</jsp:attribute>
  		<jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>
  		                   <dsp:droplet name="GetRegistryTypeNameDroplet">
                        		<dsp:param name="siteId" value="${appid}"/>
							 	<dsp:param name="registryTypeCode" value="${event}"/>
    							<dsp:oparam name="output">
									<dsp:getvalueof var="eventType" param="registryTypeName" scope="request"/>
								</dsp:oparam>
							</dsp:droplet> 
        <jsp:attribute name="headerRenderer">
           <dsp:include page="/giftregistry/gadgets/simpleRegHeader.jsp" flush="true" >
<%--             <dsp:param name="eventType" param="registryTypeName" /> --%>
            <dsp:param name="eventType" value="${eventType}" />
           </dsp:include>
        </jsp:attribute>       
     
	        <jsp:body>
	        <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	            <div id="coRegistrantDialog2" title="Co-registrant Access" class="clear">
	                <bbbt:textArea key="txt_reg_coreg_emailsent" language ="${pageContext.request.locale.language}"/>
	                <p id="newCoRegistrantEmail"></p>
	                <bbbt:textArea key="txt_reg_coreg_manage_msg" language ="${pageContext.request.locale.language}"/>
	            </div>
	            
	            <div id="coRegistrantDialog3" title="Co-registrant Access" class="clear">
	            	<bbbt:textArea key="txt_coregistrant_dialog3" language ="${pageContext.request.locale.language}"/>
	            </div>
	            
	            <div id="coRegistrantDialog1" title="Co-registrant Access" class="clear">
	                <bbbt:textArea key="txt_reg_coreg_account_found" language ="${pageContext.request.locale.language}"/>
	                <p id="newCoRegistrantEmail1"></p>
	                <bbbt:textArea key="txt_reg_coreg_manage_msg2" language ="${pageContext.request.locale.language}"/>
	            </div>
	            
	            <div id="coRegistrantErrorDialog" class="clear">
	                <h3><bbbt:textArea key="txt_reg_coreg_entered_email" language ="${pageContext.request.locale.language}"/></h3>
	                <p><bbbt:textArea key="txt_reg_coreg_enter_coreg_email" language ="${pageContext.request.locale.language}"/></p>
	            </div>
	           
				<dsp:droplet name="SimpleRegFieldsDroplet">
						<dsp:param name="eventType" value="${eventType}" />
						<dsp:oparam name="output">
								<dsp:getvalueof var="inputListMap" param="inputListMap" />
					  	</dsp:oparam>
				</dsp:droplet> 

				<c:choose>
                	<c:when test="${event == 'BA1' }">
	            		<div class="container_12 clearfix noMarTop containerBaby">
	            	</c:when>
	            	<c:otherwise>
	            		<div class="container_12 clearfix noMarTop regFromContainer">
	            	</c:otherwise> 
	            </c:choose>

                
   				<div class="${gridClass1} center flatform cf block">
   				 <%--registry header links --%>
   				<div class="${gridClass1} alpha omega marTop_5">	
   				 <div class="grid_3 fl omega alpha">  
	            <dsp:droplet name="GiftRegistryFlyoutDroplet">
					<dsp:param name="profile" bean="Profile"/>
	 						<dsp:oparam name="output">
	 							
	 								<dsp:droplet name="/atg/dynamo/droplet/Switch">
	         							<dsp:param name="value" param="userStatus"/>
							            <dsp:oparam name="<%= BBBCoreConstants.USER_LOGGED_IN_WITH_MULTIPLE_REGISTRIES %>">
	                         	  		  	<a class="myRegistries" aria-label="<bbbl:label key="lbl_reg_go_back" language ="${pageContext.request.locale.language}"/>" href="${contextPath}/giftregistry/my_registries.jsp"><span>My Registries</span></a>
							   			</dsp:oparam>
										<dsp:oparam name="<%= BBBCoreConstants.USER_LOGGED_IN_WITH_SINGLE_REGISTRY %>">
	                                 	 	<a class="myRegistries" aria-label="<bbbl:label key="lbl_reg_go_back" language ="${pageContext.request.locale.language}"/>" href="${contextPath}/giftregistry/my_registries.jsp"><span>My Registries</span></a>
										</dsp:oparam>
									</dsp:droplet>
								
							</dsp:oparam>
				</dsp:droplet>
				<c:if test="${event == 'BA1'}">
				 <p class="marTop_5 marBottom_5 allFieldReq"> <bbbl:label key="lbl_reg_all_fields_required" language="${pageContext.request.locale.language}" /></p>
				</c:if>
				</div>
				<div class="grid_5 fr omega">
            		<div class="grid_2 fr">
                     		<div id="chatModal">
                    			<div id="chatModalDialogs"></div>
								<dsp:include page="/common/click2chatlink.jsp">
                        				<dsp:param name="pageId" value="2"/>
                       			</dsp:include>
                 			</div>
               		</div>
           		</div>
  			
   				 <%--end registry header links --%>
				 <div class="error">
   				 <dsp:droplet name="IsEmpty">
		<dsp:param name="value" bean="GiftRegistryFormHandler.formExceptions"/>
		<dsp:oparam name="false">
	   Error occurred.<br>
				<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
					<dsp:param bean="GiftRegistryFormHandler.formExceptions" name="exceptions"/>
					<dsp:oparam name="output">
					<dsp:getvalueof var="errormsg"param="message"/> 
						<c:choose>
							<c:when test="${fn:contains(errormsg,'err_')}">
								<bbbe:error key="${errormsg}" language="${pageContext.request.locale.language}" />
							</c:when>
							<c:otherwise>
								<dsp:valueof param="message"></dsp:valueof>
							</c:otherwise>	
						</c:choose>								
					</dsp:oparam>				
				</dsp:droplet>
		</dsp:oparam>
						
	</dsp:droplet>
	</div>
   				 <hr aria-hidden=true>
	                <dsp:form id="registryFormPost" action="/store/giftregistry/createRegistry.jsp" iclass="pushDown clearfix form post noMarTop" method="post" formid="registryFormPost">
							<dsp:input id="registryEventType" bean="GiftRegistryFormHandler.registryEventType" type="hidden" value="${fn:escapeXml(event)}" />
	                        <dsp:input id="createSimplified" bean="GiftRegistryFormHandler.createSimplified" type="hidden" value="true" />
	                        <dsp:input id="coRegEmailFoundPopupStatus" bean="GiftRegistryFormHandler.coRegEmailFoundPopupStatus" type="hidden" name="coRegEmailFoundPopupStatus"/>
	                        <dsp:input id="coRegEmailNotFoundPopupStatus" bean="GiftRegistryFormHandler.coRegEmailNotFoundPopupStatus" type="hidden" name="coRegEmailNotFoundPopupStatus"/>
						     <dsp:include page="simpleReg_primaryRegistrant.jsp">
							            <dsp:param name="event" value="${event}"/>
							             <dsp:param name="inputListMap" value="${inputListMap}"/>
							 </dsp:include>

	                         <c:if test="${inputListMap['CoRegistrantFirstName'].isDisplayonForm || inputListMap['CoRegistrantLastName'].isDisplayonForm || inputListMap['CoRegistrantEmail'].isDisplayonForm}">
						    <dsp:include page="simpleReg_registrant_form.jsp">
	                             <dsp:param name="inputListMap" value="${inputListMap}"/>
	                        </dsp:include>
	                        </c:if>
	                         <div class="clear"></div>

                              
                           <c:choose>
                           <c:when test="${event == 'BA1'}">
                    		<dsp:include page="simpleReg_showerDate_form_baby.jsp">
	                            <dsp:param name="siteId" value="${appid}" />
	                            <dsp:param name="inputListMap" value="${inputListMap}"/>
	                        </dsp:include> 
	                         <dsp:include page="simpleReg_event_form_baby.jsp">
	                            <dsp:param name="siteId" value="${appid}" />
	                            <dsp:param name="inputListMap" value="${inputListMap}"/>
	                        </dsp:include>
	                        
	                        <dsp:include page="simpleReg_shipping_form_baby.jsp">
	                        	<dsp:param name="siteId" value="${appid}" />
	                            <dsp:param name="inputListMap" value="${inputListMap}"/>
	                        </dsp:include>
	                       	<dsp:include page="simpleReg_store_form.jsp">
	                            <dsp:param name="siteId" value="${appid}" />
	                            <dsp:param name="inputListMap" value="${inputListMap}"/>
	                        </dsp:include>
	                    	<div class="clear"></div>
	                    	<dsp:include page="simpleReg_emailOptIn_form_baby.jsp"/>  
                           </c:when>
	                    	<c:otherwise>
							
							 <dsp:include page="simpleReg_event_form.jsp">
	                            <dsp:param name="siteId" value="${appid}" />
	                             <dsp:param name="inputListMap" value="${inputListMap}"/>
	                        </dsp:include>
							 <dsp:include page="simpleReg_shipping_form.jsp">
	                            <dsp:param name="siteId" value="${appid}" />
	                             <dsp:param name="inputListMap" value="${inputListMap}"/>
	                        </dsp:include>
							
							
                              <div class="grid_4 alpha omega clearfix eventtt  fl">
                               <div id="eventflex" class="grid_4 alpha omega clearfix">
                           <div id="" class="grid_4 alpha omega clearfix">  
	                        <dsp:include page="simpleReg_showerDate_form.jsp">
	                                <dsp:param name="siteId" value="${appid}" />
	                                <dsp:param name="inputListMap" value="${inputListMap}"/>
	                            </dsp:include>

	                       	<dsp:include page="simpleReg_store_form.jsp">
	                                <dsp:param name="siteId" value="${appid}" />
	                                <dsp:param name="inputListMap" value="${inputListMap}"/>
	                            </dsp:include>
                   </div> </div> 
	                       
						</div>
	                            <div class="clear"></div>
	                      <dsp:include page="simpleReg_emailOptIn_form.jsp"/>
						</c:otherwise>
						</c:choose>
						
						<c:if test="${inputListMap['networkAffiliation'].isDisplayonForm}">
						<c:set var="registryThirdPartySearchFlag"><bbbc:config key="RegistryThirdPartySearchFlag" configName="FlagDrivenFunctions" /></c:set>
						<div class="marTop_10 networkAff">
						<c:set var="networkAffiliationVar" value="N"/>
	                    <c:choose>
					        <c:when test="${registryThirdPartySearchFlag}">
							<input id="networkAff" name="optInOutCheck" type="checkbox"  checked="checked" title="Make Registry Public" style="opacity: 0;" aria-hidden="false"/>
							<c:set var="networkAffiliationVar" value="Y"/>
							</c:when>
							<c:otherwise>
							<input id="networkAff" name="optInOutCheck" type="checkbox"  title="Make Registry Public" style="opacity: 0;" aria-hidden="false"/>
                            </c:otherwise>
						</c:choose>
							<label class="networkAff marLeft_10" for="networkAff"><bbbl:label key='lbl_update_modal_opt_in_and_out_msg' language="${pageContext.request.locale.language}" /></label>
							<dsp:input id="networkAffiliation" bean="GiftRegistryFormHandler.registryVO.networkAffiliation" type="hidden" value="${networkAffiliationVar}">
								<dsp:tagAttribute name="aria-hidden" value="true"/>
							</dsp:input>
						</div>
						</c:if>
						
						<c:choose>
                			<c:when test="${event == 'BA1' }">
	                        	<div class="button marTop_25 marBottom_25 createRegBtn">
	                        </c:when>
	                        <c:otherwise>
	                        	<div class="button marTop_25 marBottom_25">
	                        </c:otherwise>
	                        </c:choose>
	                        <c:set var="createRegistryButton"><bbbl:label key="lbl_reg_createRegistry" language="${pageContext.request.locale.language}" /></c:set>
						<dsp:input type="button" id="createRegSubmitBtn" value="${createRegistryButton}" bean="GiftRegistryFormHandler.createRegistry" iclass="createRegSubmitBtn" />
						<dsp:input bean="GiftRegistryFormHandler.fromPage" type="hidden" value="createRegistry" />
						 <%-- Client DOM XSRF | Part -1
						 <dsp:input bean="GiftRegistryFormHandler.registryCreationSuccessURL" type="hidden" value="${contextPath}/giftregistry/view_registry_owner.jsp"/>
						<dsp:input bean="GiftRegistryFormHandler.registryCreationErrorURL" type="hidden" value="${contextPath}/giftregistry/simpleReg_creation_form.jsp"/>--%>
	  					<dsp:input bean="GiftRegistryFormHandler.desktop" type="hidden" value="true"/>
						</div>
	                    </dsp:form>
	                </div>
	                </div>
	             	 </div>   
    
	           <c:import url="/selfservice/simpleReg_find_in_store.jsp" >
	                <c:param name="enableStoreSelection" value="true"/>
	                <c:param name="errMessageShown" value="true"/>
	            </c:import>
	            <div class="clear"></div>
	            <c:import url="/_includes/modules/simpleReg_change_store_form.jsp" >
	            </c:import>
				<div class="clear"></div> 
				<!--  New version of view map/get directions -->
		     <c:import url="/selfservice/store/simpleReg_find_store.jsp" ></c:import>
		
		
		<input type="hidden" name="countryName" id="countryName"/>
		   		<input type="hidden" name="PoBoxFlag" id="PoBoxFlag"  />
		   		<input type="hidden" name="PoBoxStatus"id="PoBoxStatus"/>  
		   
		   <dsp:include page="frags/simpleRegAddressChange.jsp"/> 
      			</jsp:body>
     	<dsp:getvalueof var="isTransient" bean="Profile.transient"/>
		<jsp:attribute name="footerContent">
           <script type="text/javascript">
												if (typeof s !== 'undefined') {
													s.pageName = 'Registry Simplified Creation Page';
													s.channel = 'Registry';
													s.prop1 = 'Registry';
													s.prop2 = 'Registry';
													s.prop3 = 'Registry';
													s.events = "event24";
													var s_code = s.t();
													if (s_code)
														document.write(s_code);

												}
			</script>
			
			<%-- ILD-792 | Amigo code moved inside attribute tag to handle the footer performance issue on create registry page --%>
			<%-- YourAmigo code start  6/18/2013--%>
		<c:if test="${YourAmigoON}">
		<c:if test="${isTransient eq false}">
			<%-- ######################################################################### --%>
			<%--  Configuring the javascript for tracking signups (to be placed on the     --%>
			<%--  signup confirmation page, if any).                                       --%>
			<%-- ######################################################################### --%>
			
			<c:choose>
			<c:when test="${(currentSiteId eq BuyBuyBabySite)}">
			<script src="https://support.youramigo.com/52657396/tracev2.js"></script>
			<c:set var="ya_cust" value="52657396"></c:set>
			</c:when>
			<c:when test="${(currentSiteId eq BedBathUSSite)}">
			<script src="https://support.youramigo.com/73053126/trace.js"></script>
			<c:set var="ya_cust" value="73053126"></c:set>
			</c:when>
			<c:when test="${(currentSiteId eq BedBathCanadaSite)}">
			<script src="https://support.youramigo.com/73053127/tracev2.js"></script>
			<c:set var="ya_cust" value="73053127"></c:set>
			</c:when>
			</c:choose> 
			<script type="text/javascript">
			/* <![CDATA[ */
			
			    /*** YA signup tracking code for Bed Bath & Beyond (www.bedbathandbeyond.com) ***/
				  
				// --- begin customer configurable section ---
				
				ya_tid = Math.floor(Math.random()*1000000);	// Set XXXXX to the ID counting the signup, or to a random
			                          // value if you have no such id - eg,
			                          // ya_tid = Math.random();
				ya_pid = ""; // Set YYYYY to the type of signup - can be blank
			                          // if you have only one signup type.
			
				ya_ctype = "REG"; // Indicate that this is a signup and not a purchase.
				// --- end customer configurable section. DO NOT CHANGE CODE BELOW ---
				
				ya_cust = '${ya_cust}';
				try { yaConvert(); } catch(e) {}
			
			/* ]]> */
			</script>
		</c:if>
		</c:if>
    </jsp:attribute>
    
    
    
	</bbb:pageContainer>
</dsp:page>