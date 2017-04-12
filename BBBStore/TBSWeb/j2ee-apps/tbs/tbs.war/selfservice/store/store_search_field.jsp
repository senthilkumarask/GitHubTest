
<%-- ====================== Description===================
/**
* This page is used to take inputs his address, city and state or zip code as search criteria to find the nearby store(s).
* @author Seema
**/
--%>
<dsp:page>
<dsp:importbean bean="com/bbb/selfservice/SearchStoreFormHandler" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<c:set var="lbl_find_store_using_coordinates" scope="page">
		<bbbl:label key="lbl_find_store_using_coordinates" language="${pageContext.request.locale.language}" />
</c:set>
<div class="grid_5 alpha clearfix">
	<div class="imgaeStoreFront"></div>
</div>
<div class="grid_4 alpha omega clearfix">
    <div id="btnUseGeoLocation" class="clearfix padTop_10 padBottom_10 hidden">
        <div class="loadingWrapper fl">
            <div class="seeThroughWrapper fl">
                <div class="button">
                    <input type="button" value="Use My Location" name="btnUseGeoLocation" />
                </div>
                <div class="clear"></div>
            </div>
            <div class="clear"></div>
        </div>
        <div class="clear"></div>
    </div>
    <div class="hidden clearfix" >
	    <dsp:form id="frmUserLocation" action="${contextPath}/selfservice/store/find_store.jsp" method="post" name="frmUserLocation" >
	    	<dsp:input type="hidden" name="latitude" bean="SearchStoreFormHandler.storeLocator.latitude" value="" />
	        <dsp:input type="hidden" name="longitude" bean="SearchStoreFormHandler.storeLocator.longitude" value="" />
	        <dsp:input type="hidden" name="radius" bean="SearchStoreFormHandler.storeLocator.radius" value="" />
	        <dsp:input type="hidden" name="searchBasedOn" bean="SearchStoreFormHandler.searchBasedOn" value="UserLocation" />
	       <!--  <dsp:setvalue bean="SearchStoreFormHandler.searchStoreSuccessURL" value="find_store.jsp" />
			<dsp:setvalue bean="SearchStoreFormHandler.searchStoreErrorURL"   value="find_store.jsp" />-->
			<dsp:input type="hidden" bean="SearchStoreFormHandler.siteContext" beanvalue="/atg/multisite/Site.id"/>
	        <dsp:input type="submit" name="findStores" value="${lbl_find_store_using_coordinates}" bean="SearchStoreFormHandler.searchStore" />
	   		<dsp:input bean="SearchStoreFormHandler.fromPage" type="hidden" value="storesearchfield" />
	    </dsp:form>
    </div>
	<h3>
		<bbbl:label key="lbl_find_store_searchBy"
			language="${pageContext.request.locale.language}" />
	</h3>

	<dsp:form id="storeSearchFormPost" action="${contextPath}/selfservice/store/find_store.jsp" method="post">
	

	    <dsp:include page="/global/gadgets/errorMessage.jsp">
	      <dsp:param name="formhandler" bean="SearchStoreFormHandler"/>
	    </dsp:include>

		<dsp:input type="hidden" name="searchBasedOn" bean="SearchStoreFormHandler.searchBasedOn" value="UserInputs" />
		<div class="grid_4 alpha omega" id="searchByBox">
			<div class="grid_2 alpha">
			<%--START ADDRESS INPUT FIELD --%>
				<div class="input clearfix">
					<div class="label">
						<label id="lbltxtAddress" for="txtAddress"><bbbl:label
								key="lbl_search_store_address"
								language="${pageContext.request.locale.language}" /><span></span>
						</label>
					</div>
								
					<div class="text">
						<div class="grid_2 alpha omega">
							<dsp:input bean="SearchStoreFormHandler.storeLocator.address"
								type="text" name="txtAddress" id="txtAddress" >
                                <dsp:tagAttribute name="aria-required" value="false"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtAddress errortxtAddress"/>
                            </dsp:input>
						</div>
					</div>
					<div class="error"></div>
				</div>
				<div class="clearfix">
				<%--START CITY INPUT FIELD --%>
				<div class="input clearfix">
					<div class="label">
						<label id="lbltxtStoreCity" for="txtStoreCity"><bbbl:label
								key="lbl_search_store_city"
								language="${pageContext.request.locale.language}" /><span
							class="required"><bbbl:label key="lbl_mandatory"
									language="${pageContext.request.locale.language}" /> </span> </label>
					</div>
					<div class="text">
						<div class="grid_2 alpha omega">
							<dsp:input bean="SearchStoreFormHandler.storeLocator.city"
								type="text" name="storeCity" id="txtStoreCity" maxlength="25" >
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtStoreCity errortxtStoreCity"/>
                            </dsp:input>
						</div>
					</div>
					<div class="error"></div>
				</div>
	<%--END CITY INPUT FIELD --%>	
	<%--START STATE INPUT FIELD --%>			
				<div class="input clearfix">
					<div class="label">
						<label id="lbltxtStoreState" for="txtStoreState"><bbbl:label
								key="lbl_search_store_state"
								language="${pageContext.request.locale.language}" /><span
							class="required"><bbbl:label key="lbl_mandatory"
									language="${pageContext.request.locale.language}" /> </span> </label>
					</div>
					<div class="select">
						<div class="grid_2 alpha suffix_3">
							<dsp:select bean="SearchStoreFormHandler.storeLocator.state"
								id="txtStoreState" name="storeState" iclass="uniform">
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtStoreState errorctxtStoreState"/>
								<dsp:option value="">Select State</dsp:option>
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param name="array" bean="SearchStoreFormHandler.stateList" />
									<dsp:param name="elementName" value="statelist" />
									<dsp:oparam name="output">
										<dsp:getvalueof id="stateName" param="statelist.stateName" />
										<dsp:getvalueof id="stateCode" param="statelist.stateCode" />
										<dsp:option value="${stateCode}">
											<c:out value="${stateName}" />
										</dsp:option>
									</dsp:oparam>
								</dsp:droplet>
							</dsp:select>
						</div>
					</div>
				</div>
	<%--END STATE INPUT FIELD --%>
			</div>
			</div>
		 <%-- END ADDRESS INPUT FIELD --%>
			<div class="grid_1">
				<div id="orText">
					<bbbl:label key="lbl_OR"
						language="${pageContext.request.locale.language}" />
				</div>
			</div>

			<div class="grid_1 omega">

		<%--START ZIP INPUT FIELD --%>
				<div class="input clearfix">
					<div class="label">
						<label id="lbltxtStoreZip" for="txtStoreZip"><bbbl:label
								key="lbl_search_store_zip"
								language="${pageContext.request.locale.language}" /><span
							class="required"><bbbl:label key="lbl_mandatory"
									language="${pageContext.request.locale.language}" /> </span> </label>
					</div>
					<div class="text">
						<div class="grid_1 alpha omega">
							<dsp:input bean="SearchStoreFormHandler.storeLocator.postalCode"
								type="tel" name="storeZip" id="txtStoreZip" >
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtStoreZip errortxtStoreZip"/>
                            </dsp:input>
						</div>
					</div>
					<div class="error"></div>
				</div>
			</div>
       
	 <%--END ZIP INPUT FIELD --%>
			<div class="clear"></div>			
		
		</div>
		<div class="grid_4 alpha omega clearfix">
			<div class="grid_1 alpha">
	<%--START RADIUS INPUT FIELD --%>
				<div class="input clearfix">
					<div class="label">
						<label id="lblselRadius" for="selRadius"><bbbl:label
								key="lbl_search_store_radius"
								language="${pageContext.request.locale.language}" /> </label>
					</div>
					<div class="select">
						<div class="grid_1 alpha omega">
							<c:set var="radius_default_us"><bbbc:config key="radius_default_us" configName="MapQuestStoreType" /></c:set> 
							<c:set var="radius_default_baby"><bbbc:config key="radius_default_baby" configName="MapQuestStoreType" /></c:set> 
							<c:set var="radius_range_us"><bbbc:config key="radius_range_us" configName="MapQuestStoreType" /></c:set> 
							<c:set var="radius_range_baby"><bbbc:config key="radius_range_baby" configName="MapQuestStoreType" /></c:set> 
							<c:set var="radius_range_type"><bbbc:config key="radius_range_type" configName="MapQuestStoreType" /></c:set> 
							<c:choose>
								<c:when test="${currentSiteId eq TBS_BedBathUSSite}">
									<c:set var="radius_default_selected">${radius_default_us}</c:set> 
									<c:set var="radius_range">${radius_range_us}</c:set> 
								</c:when>
								<c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
									<c:set var="radius_default_selected">${radius_default_baby}</c:set> 
									<c:set var="radius_range">${radius_range_baby}</c:set> 
								</c:when>
								<c:otherwise>
									<c:set var="radius_default_selected">${radius_default_us}</c:set> 
									<c:set var="radius_range">${radius_range_us}</c:set> 
								</c:otherwise>
							</c:choose>
								
							<dsp:select bean="SearchStoreFormHandler.storeLocator.radius"
								id="selRadius" name="selRadiusName" iclass="uniform">
                                <dsp:tagAttribute name="aria-required" value="false"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lblselRadius errorselRadius"/>
								<c:forTokens items="${radius_range}" delims="," var="item">
								<c:choose>
									<c:when test="${item == radius_default_selected}">
										<dsp:option value="${item}" iclass="default" selected="true">${item} ${radius_range_type}</dsp:option>
									</c:when>
									<c:otherwise>
										<dsp:option value="${item}">${item} ${radius_range_type} </dsp:option>
									</c:otherwise>
								</c:choose>
								</c:forTokens>	
							</dsp:select>
						</div>
					</div>
				</div>
			</div>	
	<%--END RADIUS INPUT FIELD --%>
	<%--GO Button --%>			
			<div class="fl" id="findStoreGo">
				<div class="button button_active">
					<dsp:input bean="SearchStoreFormHandler.searchStore" type="submit"	value="go" >
						<dsp:tagAttribute name="aria-pressed" value="false"/>
						<dsp:tagAttribute name="role" value="button"/>
					</dsp:input>
				</div>
			</div>
			</div>
	
	
		<!-- 	<dsp:setvalue bean="SearchStoreFormHandler.searchStoreSuccessURL"
				value="find_store.jsp" />
			<dsp:setvalue bean="SearchStoreFormHandler.searchStoreErrorURL"
				value="find_store.jsp" />-->
			<dsp:input bean="SearchStoreFormHandler.fromPage" type="hidden" value="storesearchfield" />
			<dsp:input bean="SearchStoreFormHandler.siteContext" 
				type="hidden" beanvalue="/atg/multisite/Site.id"/>	
			
	</dsp:form>
</div>
</dsp:page>