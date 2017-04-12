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

	<div class="grid_1 clearfix alpha omega registery-logo">
		<img src="${imagePath}/_assets/bbregistry/images/b_gift_regist_bigBBB_white.png"
		width="143" height="62" alt="BBB Registery" />
	</div>
	<div class="row">
		<div class="small-12 large-8
		 columns padding-10 notlogIn">
			<!-- RM# 33676 display View and manage registry link-->
			<div class="widget-cover">
				<dsp:form>
		<dsp:droplet name="ErrorMessageForEach">
			<dsp:param bean="GiftRegistryFormHandler.formExceptions"
				name="exceptions" />
			<dsp:oparam name="output">
				<dsp:valueof param="message" />
				<br>
			</dsp:oparam>
		</dsp:droplet>
		 <div id="for-mobi-search-reg">
		     <h3><bbbl:label key="lbl_registry_search_for" language="${pageContext.request.locale.language}" /></h3> 
		     <div class="input textBox small-12 columns no-padding">
		     	 <div class="text"> 
		     	 	<input type="text" value="" class="required " autocomplete="off" name="RegistryID" placeholder="Registry ID">
		     	  </div> 
		      </div>  
		      <div class="input small-12 columns no-padding"> 
		      	 <input type="submit" class="button tiny small-12" role="button" value="Search" autocomplete="off">
		      </div> 	
		      <h3>or</h3>
		 </div>
		<h3 class="hide-on-mob"><bbbl:label key="lbl_find_registry_nonlogged" language ="${pageContext.request.locale.language}"/></h3>
		<div class="small-12 columns infoContent alreadyRegCta">
			<div id="heroInfo" class="grid_3 suffix_3 alpha">
				<%-- <div class="guests">
					<p>
						<span><bbbl:label key="lbl_regcreate_for" language="${pageContext.request.locale.language}" /></span>&nbsp;<bbbl:label key="lbl_regcreate_registrants" language="${pageContext.request.locale.language}" />
					</p>
				</div>
				<h2><bbbl:label key="lbl_regcreate_create_registry" language="${pageContext.request.locale.language}" /></h2>
				<p><bbbl:label key="lbl_regsearch_havent_created" language="${pageContext.request.locale.language}" /></p>
				<div id="selectRegistryType">
					<ul>
						<dsp:select bean="GiftRegistryFormHandler.registryEventType"
							iclass="selector_primaryAlt triggerSubmit">
							<dsp:tagAttribute name="data-submit-button" value="submitClick2" />
                            <dsp:tagAttribute name="aria-required" value="false"/>
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
						</dsp:select>
					</ul>
				</div> 
                KP Comment - remove create registry
		         <h2><bbbl:label key="lbl_reglandinghero_alreadyregistered" language ="${pageContext.request.locale.language}"/></h2>--%>
		        
		         <p class="alreadyRegText"><bbbl:label key="lbl_kickstarters_create_registry_have_existing_registry" language ="${pageContext.request.locale.language}"/></p>
        		 <dsp:a href="${contextPath}/giftregistry/my_registries.jsp"><bbbl:label key="lbl_regsearch_login_to_accnt" language="${pageContext.request.locale.language}" /></dsp:a>
			</div>
			<div id="heroProd">
		<img src="${imagePath}/_assets/bbregistry/images/prod.png"
			alt="" />
		</div>
		</div>
		<%-- <dsp:input bean="GiftRegistryFormHandler.ErrorURL" type="hidden"
			value="${contextPath}/giftregistry/registry_type_select.jsp" />
		<dsp:input bean="GiftRegistryFormHandler.SuccessURL" type="hidden"
			value="${contextPath}/giftregistry/registry_creation_form.jsp" /> --%>
			<dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="bridalLandingnonLoggedReg" />
		<dsp:input id="submitClick2"
			bean="GiftRegistryFormHandler.registryTypes" name="submitButton" type="submit" value=""
			style="display:none;"></dsp:input>
	</dsp:form>

	<dsp:include page="find_registry.jsp" />
			</div>
		</div>
		<div class="small-12 large-4 columns padding-10 pos-on-mob">
			<div class="widget-cover">
				<h3 class="create-reg"><bbbl:label key="lbl_overview_create_registries" language ="${pageContext.request.locale.language}"/></h3>
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
	 	<dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="bridalLandingNonLoggedRegistry" />
						
						<dsp:input bean="GiftRegistryFormHandler.registryTypes" type="submit" value="" id="submitRegistryClick" iclass="hidden" />
					</dsp:form>
				 </div>
				 <div class="oregistery-list">
				 	  <strong><bbbl:label key="lbl_registry_reasons_to_register" language ="${pageContext.request.locale.language}"/></strong>
					  <bbbt:textArea key="txt_reasons_to_register_tbs" language ="${pageContext.request.locale.language}"/>
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