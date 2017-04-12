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
 
<div class="columns babypage">
		<div class="small-12 large-8
		 columns padding-10 notlogIn">
			<!-- RM# 33676 display View and manage registry link-->
			<div class="widget-cover">

				<div id="for-mobi-search-reg">
			     <h1><bbbl:label key="lbl_registry_search_for" language="${pageContext.request.locale.language}" /></h1> 
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

		<h1 class="hide-on-mob"><bbbl:label key="lbl_find_registry_nonlogged" language ="${pageContext.request.locale.language}"/></h1>
		<div class="small-12 columns infoContent alreadyRegCta">
			<div id="heroInfo" class="grid_3 suffix_3 alpha">
                 <%-- #32031 
                 <div class="guests">
                     <p><bbbl:label key="lbl_regsearch_for_registrants" language ="${pageContext.request.locale.language}"/></p>
                     <h2><span><bbbl:label key="lbl_regsearch_create" language ="${pageContext.request.locale.language}"/></span>&nbsp;<bbbl:label key="lbl_regsearch_a_registry" language ="${pageContext.request.locale.language}"/></h2>
			<p><bbbl:label key="lbl_regsearch_havent_created" language ="${pageContext.request.locale.language}"/></p>
                 </div>
                 <dsp:form>
	<dsp:droplet name="ErrorMessageForEach">
	  <dsp:param bean="GiftRegistryFormHandler.formExceptions" name="exceptions"/>
	  <dsp:oparam name="output">
	  <dsp:valueof param="message"/><br>
	  </dsp:oparam>
	</dsp:droplet>


   <div id="findARegistrySelectType" class="createRegistry selectTypeFOR marTop_10">
		
		<dsp:select  bean="GiftRegistryFormHandler.registryEventType"  iclass="selector_primary triggerSubmit">	
			<dsp:tagAttribute name="data-submit-button" value="submitClick7"/>	
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
		<dsp:input id="submitClick7"
			bean="GiftRegistryFormHandler.registryTypes" type="submit" value=""
			style="display:none;"></dsp:input>
		

    </div>
</dsp:form>

--%>
 				<p class="alreadyRegTextBaby babyfontclass"><bbbl:label key="lbl_kickstarters_create_registry_have_existing_registry" language ="${pageContext.request.locale.language}"/></p>
 				<a href="${contextPath}/giftregistry/my_registries.jsp"  class="babyfontclass"><bbbl:label key="lbl_regsearch_login_to_accnt" language="${pageContext.request.locale.language}" /></a>

<%--
 <p style = "color: #999999; font-family: AmericanTypewriter"><bbbl:label key="lbl_reglandinghero_alreadyregistered" language ="${pageContext.request.locale.language}"/></p>
				<p style= "color: #999999; font-family: AmericanTypewriter">
					<a href="${contextPath}/giftregistry/my_registries.jsp"><bbbl:label key="lbl_regsearch_login_to_accnt" language="${pageContext.request.locale.language}" /></a>
				</p>
--%>
             </div>
	<div class="small-12 large-6 columns" style="display:none;">
		<div id="heroProd"><img src="/_assets/bbbaby/images/prod_banner_img.png" alt="" /></div>
	</div>
  </div>
  <%--<div class="grid_6 logIn">
                <div id="findARegistryFormForm" role="application">
                    <div class="findARegistryFormTitle">
                        <p>for guests</p>
                        <h2><span>find</span> a registry</h2>
                    </div>
					<c:import url="/_includes/modules/registry_forms/find_registry.jsp">
 					   <c:param name="findRegistryFormId" value="babyheroForm" />
 					   <c:param name="submitText" value="FIND REGISTRY" />
 					</c:import >
					<dsp:include page="find_registry.jsp" />
                    <div class="clear"></div>
                </div>
            </div>--%>
				<dsp:include page="find_registry.jsp" />
			</div>
		</div>
		<div class="small-12 large-4 columns padding-10 pos-on-mob">
			<!-- RM# 33676 display View and manage registry link-->
			<div class="widget-cover extra-pad">
				<h1 class="create-reg"><bbbl:label key="lbl_overview_create_registries" language ="${pageContext.request.locale.language}"/></h1>
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
	 	<dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="babyLandingNonLoggedReg" />
						
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