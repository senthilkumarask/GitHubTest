<dsp:page>
	<dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />		
	<dsp:getvalueof var="contextPath"
		bean="/OriginatingRequest.contextPath" />

	<dsp:form>
		<dsp:droplet name="ErrorMessageForEach">
			<dsp:param bean="GiftRegistryFormHandler.formExceptions"
				name="exceptions" />
			<dsp:oparam name="output">
				<dsp:valueof param="message" />
				<br>
			</dsp:oparam>
		</dsp:droplet>
		<div class="small-12 columns infoContent">
			<%-- <div id="heroInfo" class="grid_3 suffix_3 alpha">
				<div class="guests">
					<p>
						<span><bbbl:label key="lbl_regcreate_for" language="${pageContext.request.locale.language}" /></span>&nbsp;<bbbl:label key="lbl_regcreate_registrants" language="${pageContext.request.locale.language}" />
					</p>
				</div>
				<h2><bbbl:label key="lbl_regcreate_create_registry" language="${pageContext.request.locale.language}" /></h2>
				<div class="grid_2 alpha clearfix">
					<div id="selectRegistryType">
						<ul>
							<dsp:select bean="GiftRegistryFormHandler.registryEventType"
								iclass="selector_primary triggerSubmit">
								<dsp:tagAttribute name="data-submit-button" value="submitClick3" />
								<dsp:droplet name="GiftRegistryTypesDroplet">
									<dsp:param name="siteId" value="${currentSiteId}"/>								
									<dsp:oparam name="output">
										<dsp:option value="" selected="selected">
											<bbbl:label key="lbl_regcreate_select_type"
												language="${pageContext.request.locale.language}" />
										</dsp:option>
										<dsp:droplet name="ForEach">
											<dsp:param name="array" param="registryTypes" />
											<dsp:oparam name="output">
												<dsp:param name="regTypes" param="element" />
												<dsp:getvalueof var="regTypesId"
													param="regTypes.registryCode" />
												<dsp:option value="${regTypesId}">
													<li><a href="#"><dsp:valueof
																param="element.registryName"></dsp:valueof> </a>
													</li>
												</dsp:option>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
                                <dsp:tagAttribute name="aria-required" value="false"/>
							</dsp:select>
						</ul>
					</div>
				</div>
				<dsp:input bean="GiftRegistryFormHandler.ErrorURL" type="hidden"
					value="${contextPath}/giftregistry/registry_type_select.jsp" />
				<dsp:input bean="GiftRegistryFormHandler.SuccessURL" type="hidden"
					value="${contextPath}/giftregistry/registry_creation_form.jsp" />
				<dsp:input id="submitClick3"
					bean="GiftRegistryFormHandler.registryTypes" type="submit" value=""
					style="display:none;"></dsp:input>
				
				
				<h1><bbbl:label key="lbl_regcreate_manage_registry" language="${pageContext.request.locale.language}" /></h1>
				<p><bbbl:label key="lbl_regcreate_already_registered" language="${pageContext.request.locale.language}" /></p>
				<p>
					<dsp:a page="/account/login.jsp"><bbbl:label key="lbl_regcreate_login_view_reg" language="${pageContext.request.locale.language}" /></dsp:a>				
				</p>
				
				
			</div> 
   KP Comment - removed create registry--%>

			<div id="heroProd">
				<img src="${imagePath}/_assets/bbregistry/images/prod.png"
					alt="" />
			</div>
		</div>
	</dsp:form>
    

	<div class="grid_2 clearfix gift marTop_20 no-registry">		
		<div class="grid_1 clearfix alpha omega registery-logo">
			<img src="${imagePath}/_assets/bbregistry/images/b_gift_regist_bigBBB_white.png"
			width="143" height="62" alt="BBB Registery" />
		</div>
		<div class="columns">
			<div class="small-12 large-4 columns padding-10">
				<!-- RM# 33676 display View and manage registry link-->
				<div class="widget-cover">
					<h3 class="purple"><bbbl:label key="lbl_registry_manage_reg" language ="${pageContext.request.locale.language}"/></h3>
					<div class="grid_2 clearfix manageReg alpha small-12 columns frmFindARegistry no-padding">	
	              		<!-- Dropdown for registries -->
			              <div id="findARegistryInfo" class="grid_3 alpha omega">
						    <div id="findARegistrySelectType">
						    	<dsp:include page= "../giftregistry/frags/view_manage_registry.jsp">
						    		<dsp:param name="fragName" value="bridalLanding"/>
						    	</dsp:include>
						    </div>
				   			<div class="clear"></div>
						  </div>
						<div class="clear"></div>
					</div>
					<div class="txt"><bbbt:textArea key="txt_manage_reg" language ="${pageContext.request.locale.language}"/></div>
				</div>
			</div>
			<div class="small-12 large-4 columns padding-10">
				<div class="widget-cover pad-less-left">
					<h3><bbbl:label key="lbl_landing_createregistry" language ="${pageContext.request.locale.language}"/></h3>
					<div class="grid_2 clearfix small-12 columns no-padding">
						<dsp:form>
							<dsp:select bean="GiftRegistryFormHandler.registryEventType" onchange="callMyAccRegistryTypesFormHandler();" id="typeofregselect">
							<dsp:droplet name="GiftRegistryTypesDroplet">
							<dsp:param name="siteId" value="${currentSiteId}"/>
							<dsp:oparam name="output">
								<dsp:option value="" selected="selected"><bbbl:label key="lbl_overview_select_type" language="${pageContext.request.locale.language}"/></dsp:option>
								<dsp:droplet name="ForEach">
								<dsp:param name="array" param="registryTypes" />
								<dsp:oparam name="output">
								<dsp:param name="regTypes" param="element" />
									<dsp:getvalueof var="regTypesId" param="regTypes.registryName" />
									<dsp:getvalueof var="registryCode" param="regTypes.registryCode" />
									<dsp:option value="${registryCode}">
										<dsp:valueof param="element.registryName" />
									</dsp:option>
									<dsp:getvalueof var="regName" param="element.registryName" />
								</dsp:oparam>
								</dsp:droplet>
							</dsp:oparam>
							</dsp:droplet>
							<dsp:tagAttribute name="aria-required" value="false"/>
							<dsp:tagAttribute name="aria-labelledby" value="typeofregselect"/>
							<dsp:tagAttribute name="class" value="selector_primary"/>
							<dsp:tagAttribute name="autocomplete" value="off"/>
							</dsp:select>
							<%--  Client DOM XSRF | Part -1
		<dsp:input bean="GiftRegistryFormHandler.ErrorURL" type="hidden"
			value="${contextPath}/giftregistry/registry_type_select.jsp" />
		<dsp:input bean="GiftRegistryFormHandler.SuccessURL" type="hidden"
			value="${contextPath}/giftregistry/simpleReg_creation_form.jsp" /> --%>
	 	<dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="bridalLandingLoggedNoReg" />
							
							<dsp:input bean="GiftRegistryFormHandler.registryTypes" type="submit" value="" id="submitRegistryClick" iclass="hidden" />
						</dsp:form>
					 </div>
					 <div class="oregistery-list">
						  <bbbt:textArea key="txt_reasons_to_register_tbs" language ="${pageContext.request.locale.language}"/>
					</div>
				</div>
			</div>
			<div class="small-12 large-4 columns padding-10">
				<div class="widget-cover">
					<h3><bbbl:label key='lbl_landing_giveguests' language ="${pageContext.request.locale.language}"/></h3>
					<dsp:include page="find_registry.jsp" />
				</div>
			</div>
		</div>
		
	</div>

	<script type="text/javascript">
		function callMyAccRegistryTypesFormHandler() {
			if (document.getElementById('typeofregselect').value!="")
			{
				document.getElementById("submitRegistryClick").click();
			}
	
		}
	</script>
</dsp:page>