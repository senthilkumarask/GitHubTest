
<%-- ====================== Description===================
/**
* This page is used to take the Start and destination locations input from customer for displaying the directions without and with map options.  
* @author Seema
**/
--%>
<dsp:page>
	<dsp:importbean bean="com/bbb/selfservice/P2PDirectionsFormHandler" />
	<div id="directionsDialogWrapper" class="clearfix hidden" title="<bbbl:label key="lbl_store_route_print_driving _directions"
					language="${pageContext.request.locale.language}" />">
		<div id="directionsForm" class="directionsForm clearfix">
		<div class="hidden errorWrap marBottom_10 marLeft_10"></div>
		
			<div class="grid_3 alpha omega padRight_10">
				<h3>
						<bbbl:label key="lbl_p2p_dir_inputs_start"
							language="${pageContext.request.locale.language}" />
				</h3>
				<p class="marBottom_20">
					<bbbl:label key="lbl_p2p_dir_inputs_heading"
						language="${pageContext.request.locale.language}" />
				</p>
			<div id="startLocationFormWrapper">
				<%-- This method will call the p2p_directions.jsp through ajax to get the directions --%>
				<dsp:form id="startLocationForm" name="startLocationForm"
					action="/store/selfservice/store/p2p_directions_ajax.jsp" method="post">
						   <dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">		        
			        		<dsp:oparam name="output">
			        		<dsp:oparam name="outputStart">
			                  <LI>
									</dsp:oparam>
			                  <b><label class="error"><dsp:valueof param="message" /></label>
									</b>
									<br>
			                </dsp:oparam>			                
			                <dsp:oparam name="outputEnd">
			                  </LI>
			                </dsp:oparam>		        
		      		  </dsp:droplet>  
					
						<%--START STREET INPUT FIELD --%>
                        <div class="input">
                            <div class="label">
                                <label id="lbltxtMultiStoreStreet" for="txtMultiStoreStreet"><bbbl:label
										key="lbl_store_route_street"
										language="${pageContext.request.locale.language}" /></label>
                            </div>
                            <div class="text">
                                <dsp:input type="text" name="storeMultiStreet"
										bean="P2PDirectionsFormHandler.street" id="txtMultiStoreStreet" >
                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtMultiStoreStreet errortxtMultiStoreStreet"/>
                                </dsp:input>
                            </div>
                        </div>
						<%--END STREET INPUT FIELD --%>
						<%--START CITY INPUT FIELD --%>
                        <div class="input">
                            <div class="label">
                                <label id="lbltxtMultiStoreCity" for="txtMultiStoreCity"><bbbl:label
										key="lbl_search_store_city"
										language="${pageContext.request.locale.language}" /> <span
									class="required"><bbbl:label key="lbl_mandatory"
											language="${pageContext.request.locale.language}" /> </span> </label>
                            </div>
                            <div class="text">
                                <dsp:input type="text" name="storeMultiCity"
										bean="P2PDirectionsFormHandler.city" id="txtMultiStoreCity" maxlength="25" >
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtMultiStoreCity errortxtMultiStoreCity"/>
                                </dsp:input>
                            </div>
                        </div>
						<%--END STREET INPUT FIELD --%>
						<%--START STATE INPUT FIELD --%>
                        <div class="input select clearfix">
                            <div class="label">
                                <label id="lbltxtMultiStoreState" for="txtMultiStoreState"><bbbl:label
										key="lbl_search_store_state"
										language="${pageContext.request.locale.language}" /> <span
									class="required"><bbbl:label key="lbl_mandatory"
											language="${pageContext.request.locale.language}" /> </span> </label>
                            </div>
                            <div class="select">
                                <dsp:select id="txtMultiStoreState"
									bean="P2PDirectionsFormHandler.state" name="storeMultiState"
									iclass="uniform">
									<dsp:option value="">
										<bbbl:label key="lbl_search_store_select_state"
											language="${pageContext.request.locale.language}" />
									</dsp:option>
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
										<dsp:param name="array"
											bean="P2PDirectionsFormHandler.stateList" />
										<dsp:param name="elementName" value="statelist" />
										<dsp:param name="sortProperties" value="+stateName"/>
										<dsp:oparam name="output">
											<dsp:getvalueof id="stateName" param="statelist.stateName" />
											<dsp:getvalueof id="stateCode" param="statelist.stateCode" />
											<dsp:option value="${stateCode}">
												<c:out value="${stateName}" />
											</dsp:option>
										</dsp:oparam>
									</dsp:droplet>
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtMultiStoreState errortxtMultiStoreState"/>
								</dsp:select>							
                            </div>
                        </div>
						<%--START STATE INPUT FIELD --%>
						
						<%--START ZIPCODE INPUT FIELD --%>
                        <div class="input">
                            <div class="label">
                                <label id="lbltxtMultiStoreZip" for="txtMultiStoreZip"><bbbl:label
                                            key="lbl_search_store_zip"
                                            language="${pageContext.request.locale.language}" /> <span
                                        class="required"><bbbl:label key="lbl_mandatory"
                                                language="${pageContext.request.locale.language}" /> </span> </label>
                            </div>
                            <div class="text">
                                <dsp:input bean="P2PDirectionsFormHandler.postalCode"
                                            type="text" name="storeMultiZip" id="txtMultiStoreZip" >
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtMultiStoreZip errortxtMultiStoreZip"/>
                                </dsp:input>
                                <label id="errortxtMultiStoreZip" for="txtMultiStoreZip" generated="true" class="error"></label>
                            </div>
                        </div>
						<%--END ZIPCODE INPUT FIELD --%>
						<%--START WITHOUT MAP RADIO BUTTON --%>
                        <div class="radioItem input clearfix">
                            <div class="radio">
                                <dsp:input
									bean="P2PDirectionsFormHandler.showDirectionsWithMaps"
									id="dirWithoutMap" type="radio" name="dirShowMap" value="false"
									checked="true" >
                                    <dsp:tagAttribute name="aria-checked" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbldirWithoutMap"/>
                                </dsp:input>
                            </div>
                            <div class="label">
                                <label id="lbldirWithoutMap" for="dirWithoutMap"><bbbl:label
										key="lbl_store_route_map_option_without"
										language="${pageContext.request.locale.language}" /> </label>
                            </div>
                        </div>
						<%--END WITHOUT MAP RADIO BUTTON --%>
						<%--START WITH MAP RADIO BUTTON --%>
                        <div class="radioItem input clearfix">
                            <div class="radio">
                                <dsp:input
									bean="P2PDirectionsFormHandler.showDirectionsWithMaps"
									id="dirWithMap" type="radio" name="dirShowMap" value="true" >
                                    <dsp:tagAttribute name="aria-checked" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbldirWithMap"/>
                                </dsp:input>
                            </div>
                            <div class="label">
                                <label id="lbldirWithMap" for="dirWithMap"><bbbl:label
										key="lbl_store_route_map_option_with"
										language="${pageContext.request.locale.language}" /> </label>
                            </div>
                        </div>
						<%--END WITH MAP RADIO BUTTON --%>
						<input type="hidden" name="dialogDestStreet"
							data-dest-class="street" value="" /> <input type="hidden"
							name="dialogDestCity" data-dest-class="city" value="" /> <input
							type="hidden" name="dialogDestState" data-dest-class="state"
							value="" /> <input type="hidden" name="dialogDestZip"
							data-dest-class="zip" value="" />
						<input type="submit" class="hidden" value=""/>
				</dsp:form>
			</div>
			
			<div id="startLocationSet"></div>
		</div>
        <div class="grid_3 padLeft_20 alpha omega">
			<h3><bbbl:label key="lbl_store_route_destination"
					language="${pageContext.request.locale.language}" /></h3>
			<div id="destinationLocation"></div>
            <div><a class="close-any-dialog" href="#"><bbbl:label key="lbl_store_route_destination_change"
					language="${pageContext.request.locale.language}" /></a></div>
		</div>
    </div>
	<div id="directionResultWrapper"></div>
</div>
</dsp:page>

