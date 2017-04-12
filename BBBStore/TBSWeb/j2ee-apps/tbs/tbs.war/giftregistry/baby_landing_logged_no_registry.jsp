 <dsp:page>
	<dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
			
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/> 
	
	<c:set var="TBS_BuyBuyBabySite">
		<bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
    </c:set>
     <c:choose>
		<c:when test="${currentSiteId == TBS_BuyBuyBabySite}">
			<c:set var="simpleRegCreationFormPath" value="${contextPath}/giftregistry/simpleReg_creation_form_bbus.jsp" />
		</c:when>
		<c:otherwise>
			<c:set var="simpleRegCreationFormPath" value="${contextPath}/giftregistry/simpleReg_creation_form.jsp" />
		</c:otherwise>
   </c:choose>
 

 <div class="columns babyPageloggout">
		<div class="small-12 large-4 columns padding-10">
			<!-- RM# 33676 display View and manage registry link-->
			<div class="widget-cover less-pad2">
				<h1 class="purple"><bbbl:label key="lbl_registry_manage_reg" language ="${pageContext.request.locale.language}"/></h1>
				<div class="grid_2 clearfix small-12 columns no-padding">
                    <!-- Dropdown for registries -->
                    <div id="findARegistryInfo" class="grid_3 alpha omega">

					    <div id="findARegistrySelectType">
					    	<dsp:include page= "../giftregistry/frags/view_manage_registry.jsp">
					    		<dsp:param name="fragName" value="babyLanding"/>
					    	</dsp:include>
					    </div>
					    <div class="clear"></div>
					</div>
				</div>
				<div class="txt"><div class="txt"><bbbt:textArea key="txt_manage_reg" language ="${pageContext.request.locale.language}"/></div></div>
			</div>
		</div>
		<div class="small-12 large-4 columns padding-10">
			<div class="widget-cover less-pad">
				<h1><bbbl:label key="lbl_landing_createregistry" language ="${pageContext.request.locale.language}"/></h1>
				<div class="grid_2 clearfix small-12 columns no-padding">
					 <dsp:form>
						<dsp:droplet name="ErrorMessageForEach">
						  <dsp:param bean="GiftRegistryFormHandler.formExceptions" name="exceptions"/>
						  <dsp:oparam name="output">
						  <dsp:valueof param="message"/><br>
						  </dsp:oparam>
						</dsp:droplet>

					   <div id="findARegistrySelectType" class="createRegistry selectTypeFOR">
							
							<dsp:select  bean="GiftRegistryFormHandler.registryEventType"  iclass="selector_primary triggerSubmit">	
								<dsp:tagAttribute name="data-submit-button" value="submitClick8"/>	
										<dsp:droplet name="GiftRegistryTypesDroplet">
											<dsp:param name="siteId" value="${currentSiteId}"/>					
											<dsp:oparam name="output">
											<dsp:option value="" selected="selected"><bbbl:label key="lbl_regsearch_select_type" language ="${pageContext.request.locale.language}"/></dsp:option>
												<dsp:droplet name="ForEach">
													<dsp:param name="array" param="registryTypes" />
													<dsp:oparam name="output">
													
														<dsp:param name="regTypes" param="element" />	
														<dsp:getvalueof var="regTypesId" param="regTypes.registryName" />
														<dsp:getvalueof var="registryCode" param="regTypes.registryCode" />
														<dsp:option value="${registryCode}">
															<li>
																<a href="#"><dsp:valueof param="element.registryName"></dsp:valueof></a>
															</li>
														</dsp:option>
													
													</dsp:oparam>
													
												</dsp:droplet>
											</dsp:oparam>
										</dsp:droplet>
					                    <dsp:tagAttribute name="aria-required" value="false"/>
					                    <dsp:tagAttribute name="autocomplete" value="off"/>
								</dsp:select>
							
							
							<%-- Client DOM XSRF | Part -1
 							 <dsp:input bean="GiftRegistryFormHandler.ErrorURL" type="hidden"
								value="${contextPath}/giftregistry/registry_type_select.jsp" />
							<dsp:input bean="GiftRegistryFormHandler.SuccessURL" type="hidden"
								value="${contextPath}/giftregistry/simpleReg_creation_form.jsp" /> --%>
								<dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="babyLandingLoggedNoReg" />
							<dsp:input id="submitClick8"
								bean="GiftRegistryFormHandler.registryTypes" type="submit" value=""
								style="display:none;"></dsp:input>
							

					    </div>
					</dsp:form>	
				 </div>
				 <div class="oregistery-list">
					  <bbbt:textArea key="txt_reasons_to_register_tbs" language ="${pageContext.request.locale.language}"/>
				</div>
			</div>
		</div>
		<div class="small-12 large-4 columns padding-10">
			<div class="widget-cover">
				<h1><bbbl:label key='lbl_landing_giveguests' language ="${pageContext.request.locale.language}"/></h1>
				<dsp:include page="find_registry.jsp" />
			</div>
		</div>
	</div>

<%--
 <div class="grid_6 infoContent">
                <div id="heroInfo" class="grid_3 alpha">
                    <div class="guests">
                        <p><bbbl:label key="lbl_regsearch_for_registrant" language ="${pageContext.request.locale.language}"/></p>
                        <h2><span><bbbl:label key="lbl_regsearch_create" language ="${pageContext.request.locale.language}"/></span>&nbsp;<bbbl:label key="lbl_regsearch_a_registry" language ="${pageContext.request.locale.language}"/></h2>
                    </div>
    <%-- <dsp:form>
	<dsp:droplet name="ErrorMessageForEach">
	  <dsp:param bean="GiftRegistryFormHandler.formExceptions" name="exceptions"/>
	  <dsp:oparam name="output">
	  <dsp:valueof param="message"/><br>
	  </dsp:oparam>
	</dsp:droplet>


   <div id="findARegistrySelectType" class="createRegistry selectTypeFOR">
		
		<dsp:select  bean="GiftRegistryFormHandler.registryEventType"  iclass="selector_primary triggerSubmit">	
			<dsp:tagAttribute name="data-submit-button" value="submitClick8"/>	
					<dsp:droplet name="GiftRegistryTypesDroplet">
						<dsp:param name="siteId" value="${currentSiteId}"/>					
						<dsp:oparam name="output">
						<dsp:option value="" selected="selected"><bbbl:label key="lbl_regsearch_select_type" language ="${pageContext.request.locale.language}"/></dsp:option>
							<dsp:droplet name="ForEach">
								<dsp:param name="array" param="registryTypes" />
								<dsp:oparam name="output">
								
									<dsp:param name="regTypes" param="element" />	
									<dsp:getvalueof var="regTypesId" param="regTypes.registryName" />
									<dsp:getvalueof var="registryCode" param="regTypes.registryCode" />
									<dsp:option value="${registryCode}">
										<li>
											<a href="#"><dsp:valueof param="element.registryName"></dsp:valueof></a>
										</li>
									</dsp:option>
								
								</dsp:oparam>
								
							</dsp:droplet>
						</dsp:oparam>
					</dsp:droplet>
                    <dsp:tagAttribute name="aria-required" value="false"/>
			</dsp:select>
		
		
		<dsp:input bean="GiftRegistryFormHandler.ErrorURL" type="hidden"
			value="${contextPath}/giftregistry/registry_type_select.jsp" />
		<dsp:input bean="GiftRegistryFormHandler.SuccessURL" type="hidden"
			value="${contextPath}/giftregistry/registry_creation_form.jsp" />
		<dsp:input id="submitClick8"
			bean="GiftRegistryFormHandler.registryTypes" type="submit" value=""
			style="display:none;"></dsp:input>
		

    </div>
</dsp:form>	 --%>		
--%>		
                    <%--
					<div class="guests">
                        <h2><span><bbbl:label key="lbl_regsearch_manage" language ="${pageContext.request.locale.language}"/></span>&nbsp;<bbbl:label key="lbl_regsearch_registry" language ="${pageContext.request.locale.language}"/></h2>
                    </div>
                    <p class="alreadyReg"><bbbl:label key="lbl_regsearch_already_regd" language ="${pageContext.request.locale.language}"/></p>
                    <p class="links"><a href="#" titile="login">&nbsp;<bbbl:label key="lbl_regsearch_login_to_accnt" language ="${pageContext.request.locale.language}"/></a></p>
                    <p class="wedding links bold"><a href="">Your personal wedding website</a></p> --%>
			 <%--		
                </div>
				<div class="grid_3 omega">
					<div id="heroProd"><img src="/_assets/bbbaby/images/prod_banner_img.png" alt="" /></div>
				</div>
            </div>
            --%>
            <%-- <div class="grid_6 logIn">
                <div id="findARegistryFormForm" role="application">
                    <div class="findARegistryFormTitle">
                        <p>for guests</p>
                        <h2><span>find</span> a registry</h2>
                    </div>
					<c:import url="/_includes/modules/registry_forms/find_registry.jsp">
 					   <c:param name="findRegistryFormId" value="babyheroForm" />
 					   <c:param name="submitText" value="FIND REGISTRY" />
 					</c:import >
                    <div class="clear"></div>
                </div>
            </div> --%>
            <%--
            <dsp:include page="find_registry.jsp" /> 
            --%>
</dsp:page>			