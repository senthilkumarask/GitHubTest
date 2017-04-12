<dsp:page>
<c:if test="${not empty sessionScope.Registry_Success_Back_Btn_Url && sessionScope.registry_Success_Back_Btn_Url_Flag =='redirectEnabled'}">
	<c:redirect url="${sessionScope.Registry_Success_Back_Btn_Url}"/>
	<c:set var="registry_Success_Back_Btn_Url_Flag" value="none" scope="session"/>   
</c:if>

  <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/SimplifyRegFormHandler" />
    <dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryVODroplet" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryTypeNameDroplet"/>
    <dsp:importbean bean="/atg/multisite/Site"/>
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>	
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
	
    <dsp:getvalueof bean="SessionBean.registryTypesEvent" id="event"/>
    
    <c:choose>
        <c:when test="${appid == 'BuyBuyBaby'}">
            <c:set var="pageVariation" value="bb" scope="request" />
        </c:when>
        <c:otherwise>
            <c:choose>
                <c:when test="${event == 'BA1' }">
                    <c:set var="pageVariation" value="by" scope="request" />
                </c:when>
                <c:otherwise>
                    <c:set var="pageVariation" value="br" scope="request" />
                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>
	
    <c:set var="BedBathCanadaSite">
        <bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
    </c:set>
		<c:choose>
		<c:when test="${not empty registryId}">
		<c:set var="pageWrapper" value="createSimpleRegistry updateRegistryInfo useStoreLocator useGoogleAddress" scope="request" />
		</c:when>
		<c:otherwise>
		<c:set var="pageWrapper" value="createSimpleRegistry useStoreLocator useGoogleAddress" scope="request" />
		</c:otherwise>
		</c:choose>
		
    <bbb:pageContainer>
        <jsp:attribute name="section">createSimpleRegistry</jsp:attribute>
        <jsp:attribute name="pageVariation">${pageVariation}</jsp:attribute>
        
  		<jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>
        <jsp:attribute name="headerRenderer">
           <dsp:include page="/giftregistry/gadgets/simpleRegHeader.jsp" flush="true" />
        </jsp:attribute>       
     
     	<dsp:getvalueof var="isTransient" bean="Profile.transient"/>
     	<c:set var="dataBreach" value="false" scope="request"/>
     	
     	<c:if test="${( not empty registryId ) && !isTransient }">
	     	<dsp:droplet name="/com/bbb/commerce/giftregistry/droplet/ValidateRegistryDroplet">
	     		<dsp:param name="registryId" value="${registryId}"/>
	     		<dsp:param name="profile" bean="/atg/userprofiling/Profile"/>
	     		<dsp:oparam name="valid">
	     			<c:set var="dataBreach" value="false" scope="request"/>
	     		</dsp:oparam>
	     		<dsp:oparam name="inValid">
	     			<c:set var="dataBreach" value="false" scope="request"/>
	     		</dsp:oparam>	     		
	     	</dsp:droplet>
     	</c:if>
     	
     	<c:choose>
     		<c:when test="${dataBreach}">
	     		<jsp:body>
					<div class="container_12 clearfix">
						<div class="grid_12">
		                	<div class="error marTop_20"><bbbe:error key="err_invalid_reg_info_req" language="${pageContext.request.locale.language}"/></div>
		                </div>
		            </div>     		
	     		</jsp:body>
	     	</c:when>
	     	<c:otherwise>
	     		   	
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
	            <script type="text/javascript">
	                var errorobj =  <json:object>
	                    <json:property name="error"><dsp:valueof bean="GiftRegistryFormHandler.formError"/></json:property>
	                    
	                    <dsp:getvalueof bean="GiftRegistryFormHandler.formError" var="formError"/>
	                    
	                    <c:if test="${formError == true}">
	                        <json:array name="errorMessages">
	                            <dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
	                                <dsp:param param="GiftRegistryFormHandler.formExceptions" name="exceptions"/>
	                                
	                                <dsp:oparam name="output">
	                                    <json:object>
	                                        <json:property name="propertyName"><dsp:valueof param="propertyName"/></json:property>    
	                                        <dsp:getvalueof param="message"  var="errorMsg"/>
	                                        <json:property name="propertyError"><bbbe:error key="${errorMsg}" language="${pageContext.request.locale.language}"/></json:property>
	                                    </json:object>
	                                </dsp:oparam>
	                            </dsp:droplet>
	                        </json:array>
	                    </c:if>
	                </json:object> 
	            </script> 
	            
	            <c:set var="errorString" value=""/>
	            <dsp:droplet name="ErrorMessageForEach">
	                <dsp:param bean="GiftRegistryFormHandler.formExceptions" name="exceptions" />
	                <dsp:oparam name="output">
	                    <c:set var="errorString" >true</c:set>                                                                      
	                </dsp:oparam>
	            </dsp:droplet>
	
	            <c:if test="${not empty errorString}">
	                <div class="pageErrors container_12 clearfix">
	                    <div class="grid_12 pushDown">
	                        <p class="noMar bold"><bbbl:label key='lbl_regcreate_following_error' language ="${pageContext.request.locale.language}"/></p>
	                        <ul>
	                            <li><bbbe:error key="err_reginfo_sys_error" language="${pageContext.request.locale.language}"/></li>
	                        </ul>
	                    </div>
	                    <div class="clear"></div>
	                </div>
	            </c:if>
	
	            <div class="container_12 clearfix">
	              
	                <div class="grid_1 omega" style="height:200px;"></div> 
   				 <div class="grid_10 center flatform">
   				 <hr>
	                
	                <dsp:form id="registryFormPost" iclass="pushDown clearfix form post noMarTop" method="post" formid="registryFormPost">
							<dsp:input id="registryEventType" bean="SimplifyRegFormHandler.registryEventType" type="hidden" value="${event}" />
	
	                        <dsp:input id="coRegEmailFoundPopupStatus" bean="SimplifyRegFormHandler.coRegEmailFoundPopupStatus" type="hidden" name="coRegEmailFoundPopupStatus"/>
	                        <dsp:input id="coRegEmailNotFoundPopupStatus" bean="SimplifyRegFormHandler.coRegEmailNotFoundPopupStatus" type="hidden" name="coRegEmailNotFoundPopupStatus"/>
	                        <dsp:input id="regIdHidden" type="hidden" name="regIdHidden" value="${regVO.registryId}" bean="SimplifyRegFormHandler.registryVO.registryId" />
	                        <dsp:input id="regTypeHidden" type="hidden" name="regTypeHidden" value="${regVO.registryType.registryTypeName}" bean="SimplifyRegFormHandler.registryVO.registryType.registryTypeName" />
	                        <dsp:droplet name="GetRegistryTypeNameDroplet">
                        		<dsp:param name="siteId" value="${appid}"/>
							 	<dsp:param name="registryTypeCode" value="${event}"/>
    							<dsp:oparam name="output">
									<dsp:getvalueof var="eventType" param="registryTypeName" scope="request"/>
								</dsp:oparam>
							</dsp:droplet> 
							
							          <div class="grid_4 alpha clearfix">
                
                            <label class="noMarTop padTop_10 padBottom_10">your info</label>
                            <c:choose>
                    <c:when test="${isTransient}"> 
                     <div class="inputField clearfix " id="regFullName">
                                             <dsp:input tabindex="1" id="txtPrimaryRegistrantFirstName" type="text" bean="SimplifyRegFormHandler.userFullName" iclass="required cannotStartWithSpecialChars alphabasicpunc ${focusCoRegClass}" maxlength="30" >
                                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtPrimaryRegistrantFirstName errortxtPrimaryRegistrantFirstName"/>
                                                </dsp:input>
                                           </div>
                    </c:when>
                    <c:otherwise>
                                        <c:choose>
                                         <c:when test="${not empty regVO.primaryRegistrant.firstName}">
                                         <c:set var="primaryfirstName" value="${regVO.primaryRegistrant.firstName}"></c:set>
                                         </c:when>
                                         <c:when test="${not empty regVO.primaryRegistrant.lastName}">
                                         <c:set var="primarylastName" value="${regVO.primaryRegistrant.lastName}"></c:set>
                                         </c:when>
                                         <c:otherwise>
                                         <c:set var="primaryfirstName"><dsp:valueof bean="Profile.firstName"/></c:set>
                                         <c:set var="primarylastName"><dsp:valueof bean="Profile.lastName"/></c:set>
                                         </c:otherwise></c:choose>
                                           <div class="inputField clearfix" id="regFullName">
                                         <dsp:input tabindex="1" id="txtPrimaryRegistrantFirstName" type="text" 
                                                    name="txtPrimaryRegistrantFirstName" value="${primaryfirstName} ${primarylastName}"
                                                    bean="SimplifyRegFormHandler.userFullName" iclass="required cannotStartWithSpecialChars alphabasicpunc ${focusCoRegClass}" maxlength="30" >
                                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtPrimaryRegistrantFirstName errortxtPrimaryRegistrantFirstName"/>
                                                </dsp:input>
                                        </div>
                                         
                        </c:otherwise>
                        </c:choose>
                        
                    	</div>
                    	<div class="grid_6 fr omega">   
              			<c:choose>
                    	<c:when test="${isTransient}"> 
                    <label class="noMarTop marLeft_10 padTop_10 padBottom_10">account info</label>
                              <dsp:include page="/account/simpleReg/login_frag.jsp">
	                            <dsp:param name="siteId" value="${appid}" />
	                        </dsp:include>
                        </c:when>
                        <c:otherwise>                    
                               
                                 <c:choose>
                                         <c:when test="${not empty regVO.primaryRegistrant.email}">
                                         <c:set var="primaryemail" value="${regVO.primaryRegistrant.email}"></c:set>
                                         </c:when>
                                         <c:otherwise>
                                          <c:set var="primaryemail"><dsp:valueof bean="Profile.email"/></c:set>
                                          </c:otherwise>    
                                         </c:choose>
                                         
                          				 <div class="grid_3 alpha  clearfix">
                          				 <label class="noMarTop marLeft_10 padTop_10 padBottom_10">account info</label>
                          				 <div class="input_wrap grid_3 " aria-live="assertive">
                          				 <input tabindex="3" id="email" name="email" value="${primaryemail}" type="email" disabled></input>
                          				 </div>
                          				 </div>
                          				 <div class="formRow grid_3 clearfix ">
										<div class="input_wrap ">
										<input tabindex="4" id="password" name="loginPasswd" value="XXXXXX" type="password" disabled></input>
										</div>
										</div>	
                        </c:otherwise>
                        </c:choose>  
            		</div>
	                    </dsp:form>
	                  
	                </div>
	                </div>
	                
	         
	           
	            
	            <c:import url="/selfservice/find_in_store.jsp" >
	                <c:param name="enableStoreSelection" value="true"/>
	                <c:param name="errMessageShown" value="true"/>
	            </c:import>
	            <div class="clear"></div>
	            <c:import url="/_includes/modules/change_store_form.jsp" >
	                <c:param name="action" value="${contextPath}${finalUrl}"/>
	            </c:import>
				<div class="clear"></div>
				<%--  New version of view map/get directions --%>
				<c:import url="/selfservice/store/find_store_pdp.jsp" ></c:import>
				
       			</jsp:body>
      	     	</c:otherwise>
     	</c:choose>
     	
		<jsp:attribute name="footerContent">
           <script type="text/javascript">
												if (typeof s !== 'undefined') {
													s.pageName = 'Registry Create Page';
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
    
    <%-- YourAmigo code start  6/18/2013--%>
		<c:if test="${YourAmigoON}">
		<dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>
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
	</bbb:pageContainer>
</dsp:page>