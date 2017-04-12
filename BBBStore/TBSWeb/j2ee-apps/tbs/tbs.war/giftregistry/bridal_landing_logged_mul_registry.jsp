<dsp:page>
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />
	<dsp:importbean
			bean="/com/bbb/commerce/giftregistry/droplet/AddItemToGiftRegistryDroplet" />		
		
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/DateCalculationDroplet" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<dsp:getvalueof var="contextPath"
		bean="/OriginatingRequest.contextPath" /> 
	
	<div class="grid_2 clearfix gift marTop_20">		
		<div class="grid_1 clearfix alpha omega registery-logo">
			<img src="${imagePath}/_assets/bbregistry/images/b_gift_regist_bigBBB_white.png"
			width="143" height="62" alt="BBB Registry" />
		</div>
		<div class="row">
			<div class="small-12 large-4 columns padding-10">
				<!-- RM# 33676 display View and manage registry link-->
				<div class="widget-cover">
					<h3 class="purple"><bbbl:label key="lbl_registry_manage_reg" language ="${pageContext.request.locale.language}"/></h3>
					<div class="grid_2 clearfix manageReg alpha small-12 columns frmFindARegistry no-padding">	
			              <%-- Dropdown for registries --%>
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
				<div class="widget-cover">
					<h3><bbbl:label key="lbl_overview_create_another_registry" language ="${pageContext.request.locale.language}"/></h3>
					<div class="grid_2 clearfix small-12 columns no-padding">
						<div id="findARegistrySelectType" class="createRegistry selectTypeFOR">
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
	 	<dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="bridalLandingLoggedMulRegistry" />
							
							<dsp:input bean="GiftRegistryFormHandler.registryTypes" type="submit" value="" id="submitRegistryClick" iclass="hidden" />
						</dsp:form>
						</div>
					 </div>
					 <div class="oregistery-list">
						  <bbbt:textArea key="txt_reasons_to_register_tbs" language ="${pageContext.request.locale.language}"/>
					</div>
				</div>
			</div>
			<div class="small-12 large-4 columns padding-10">
				<div class="widget-cover">
					<h3><bbbl:label key='lbl_landing_giveguests' language ="${pageContext.request.locale.language}"/></h3>
					<c:set var="findButton"><bbbl:label key='lbl_find_reg_submit_button' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
					<c:set var="findRegistryFormCount" value="1" scope="request"/>
					<dsp:include page="/addgiftregistry/find_registry_widget.jsp">
						<dsp:param name="findRegistryFormId" value="frmFindRegistry" />
						<dsp:param name="submitText" value="${findButton}" />
						<dsp:param name="handlerMethod" value="registrySearchFromBridalLanding" />
						<dsp:param name="bridalException" value="false" />
						<dsp:param name="findRegistryFormCount" value="${findRegistryFormCount}" />
					</dsp:include>
				</div>
			</div>
		</div>
		
	</div>
	
	<div class="grid_1 clearfix alpha omega giftOfr">
		<img src="${imagePath}/_assets/bbregistry/images/gift_offr.jpg"
			width="63" height="64" alt="Gift Offer" />
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
