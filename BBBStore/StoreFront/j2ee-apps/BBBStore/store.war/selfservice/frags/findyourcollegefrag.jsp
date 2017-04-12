<dsp:page>

	<dsp:importbean bean="/atg/userprofiling/Profile" />
	
	<dsp:getvalueof id="collegeId" param="selCollege"/>
	<dsp:getvalueof id="selState" param="selState"/>
	
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<c:set var="localStoreLinkToYext">
		<bbbc:config key="LOCAL_STORE_LINK_TO_YEXT" configName="FlagDrivenFunctions" />
    </c:set>
	<c:set var="hostNameForYEXTLink"><bbbl:label key="lbl_host_name_for_yext" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="linkDisplayNameForYEXTLink"><bbbl:label key="lbl_link_display_name_for_yext" language="${pageContext.request.locale.language}" /></c:set>
	
	<dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
		<dsp:param name="itemId"  param="selCollege"/>
		<dsp:param name="queryRQL" value="id=:itemId"/>
  		<dsp:param name="repository" value="/com/bbb/selfservice/repository/SchoolRepository"/>
  		<dsp:param name="itemDescriptor" value="schools"/>
  		<dsp:param name="elementName" value="schoolItem"/>
	  	<dsp:oparam name="output">
	  	
  			<dsp:getvalueof id="schooolName" param="schoolItem.schoolName"/>
  			<dsp:getvalueof id="schoolId" param="schoolItem.id"/>
  			<dsp:getvalueof id="address" param="schoolItem.addrLine1"/>
  			<dsp:getvalueof id="zip" param="schoolItem.zip"/>
  			<dsp:getvalueof id="city" param="schoolItem.city"/>
			<dsp:getvalueof id="state" param="schoolItem.state"/>
			<dsp:getvalueof id="addr" value="${city},${state},${zip}"/>
			
			<dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
				<dsp:param name="collegeId" param="schoolItem.id"/>
				<dsp:param name="queryRQL" value="schools=:collegeId"/>
				<dsp:param name="repository" value="/com/bbb/selfservice/repository/SchoolVerRepository"/>
				<dsp:param name="itemDescriptor" value="schoolsVer"/>
			  	<dsp:param name="elementName" value="item"/>
				<dsp:oparam name="output">
						<dsp:getvalueof id="collegeLogo" param="item.collegeLogo"/>
						<dsp:getvalueof id="pdfUrl" param="item.pdfURL"/>
	  			</dsp:oparam>
			</dsp:droplet>
	
			<jsp:useBean id="placeHolder" class="java.util.HashMap" scope="request"/>
		
			<c:set var="pageSize" scope="request"><bbbc:config key="pageSizeSearchCollege" configName="ContentCatalogKeys" /></c:set>
			<c:choose>
				<c:when test="${currentSiteId eq BedBathUSSite}">
					<c:set var="radius"><bbbc:config key="radius_college_store_us" configName="MapQuestStoreType" /></c:set> 
				</c:when>
				<c:otherwise>
					<c:set var="radius"><bbbc:config key="radius_default_us" configName="MapQuestStoreType" /></c:set> 
				</c:otherwise>
			</c:choose>
	
		 	<%-- only logged in users should see make favorite store buttons --%>
		 	<dsp:droplet name="/atg/dynamo/droplet/Switch">
			    <dsp:param name="value" bean="Profile.transient"/>
			    <dsp:oparam name="false">
			    	<c:set var="enableFavoriteStores" value="true" />        
			    </dsp:oparam>
			    <dsp:oparam name="true">
					<c:set var="enableFavoriteStores" value="false" />        
				</dsp:oparam>
			</dsp:droplet>   
	
			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:param name="array" bean="Profile.userSiteItems"/>
				<dsp:param name="elementName" value="sites"/>
				<dsp:oparam name="output">
					<dsp:getvalueof id="key" param="key"/>
					<c:if test="${currentSiteId eq key}">
						<dsp:getvalueof id="favouriteStoreId" param="sites.favouriteStoreId"/>
					</c:if>
				</dsp:oparam>
			</dsp:droplet>
			
			
			
			
<%-- 			
			<dsp:droplet name="/com/bbb/selfservice/SearchStoreDroplet">
				<dsp:param name="searchString" value="${addr}"/>
				<dsp:param name="searchType" value="3"/>
				<dsp:param name="pageSize" value="4"/>
				<dsp:param name="pageKey" param="pageKey" />
				<dsp:param name="pageNumber" param="pageNumber" />
				<dsp:param name="radius" value="${radius}" />
				
				<dsp:oparam name="output">
					<dsp:getvalueof id="pageNumber" param="pageNumber"/>
				 	
					<c:choose>
						<c:when test="${!empty pageNumber}"> 
							<c:set var="start" value="${(pageNumber * pageSize) - (pageSize-1)}"/>
						</c:when>
						<c:otherwise>
							<c:set var="start" value="1"/>
						</c:otherwise>
					</c:choose>
				 	
					<input type="hidden" name="mapPointerStartNum" value="${start}" /> 			
					<dsp:getvalueof id="currentPage" param="StoreDetailsWrapper.currentPage" />
					<dsp:getvalueof id="totalPageCount" param="StoreDetailsWrapper.totalPageCount" />
					<dsp:getvalueof id="pageKey" param="StoreDetailsWrapper.pageKey" /> --%>
					
					<div class="grid_12 clearfix marTop_20 collegeSearchResults">
					    <div class="grid_7 alpha">
					    <c:set var="alignCss" value="omega"/>
					     <c:if test="${not empty collegeLogo}">
					    	<c:set var="logo">
					    		 <bbbl:label key="lbl_findyourcollegefrag_logo" language="${pageContext.request.locale.language}"/>
					    	</c:set>			    	
					        <div class="grid_1 alpha"><dsp:img alt="${logo}" title="${logo}" src="${collegeLogo}" /></div>
					        <c:set var="alignCss" value="alpha"/>
					        </c:if>
					        <div class="grid_6 ${alignCss}">
					        	<h3 class="auLogo"><dsp:valueof value="${schooolName}"/> </h3>
					            <c:set var="collegeName"><dsp:valueof value="${schooolName}"/></c:set>
					       </div>
					    </div>
					    <c:if test="${!empty pdfUrl}">
							<a class="marRight_10 fr" target="_blank" href="${pdfUrl}" <c:if test="${GoogleAnalyticsOn}"> onclick="_gaq.push(['_trackEvent', 'college', 'click_infopage', '${collegeName}']);" </c:if>>
								<span class="fl marRight_10">
									<c:set var="pdf">
										<bbbl:label key="lbl_findyourcollegefrag_pdf" language="${pageContext.request.locale.language}"/>
									</c:set>
									<dsp:img alt="${pdf}" title="${pdf}" src="${imagePath}/_assets/bbcollege/images/icon_pdf.png"/>	
								</span>
								<c:set target="${placeHolder}" property="schoolName"><dsp:valueof value="${schoolName}"/></c:set>
								<span class="bold grid_4 omega collegeInfo">
									${collegeName} <bbbl:label key="lbl_findyourcollegefrag_drom_moving" language="${pageContext.request.locale.language}" placeHolderMap="${placeHolder}"/>
									<span class="seeWhatLink"> 
									<bbbl:textArea key="txt_findyourcollegefrag_see_what_info" language="${pageContext.request.locale.language}"/>
									</span>
								</span>  
							</a>
						</c:if>
					</div>
					
							
					
					<%-- </dsp:oparam>
				<dsp:oparam name="empty">
					<div class="grid_12 clearfix marTop_20 collegeSearchResults">
					    <div class="grid_7 alpha">
					    <c:set var="alignCss" value="omega"/>
					     <c:if test="${not empty collegeLogo}">
					    	<c:set var="logo">
					    		 <bbbl:label key="lbl_findyourcollegefrag_logo" language="${pageContext.request.locale.language}"/>
					    	</c:set>			    	
					        <div class="grid_1 alpha"><dsp:img alt="${logo}" title="${logo}" src="${collegeLogo}" /></div>
					        <c:set var="alignCss" value="alpha"/>
					        </c:if>
					        <div class="grid_6 ${alignCss}">
					        	<h3 class="auLogo"><dsp:valueof value="${schooolName}"/> </h3>
					            <c:set var="collegeName"><dsp:valueof value="${schooolName}"/></c:set>
					       </div>
					    </div>
					    <c:if test="${!empty pdfUrl}">
							<a class="marRight_10 fr" href="${pdfUrl}" <c:if test="${GoogleAnalyticsOn}"> onclick="_gaq.push(['_trackEvent', 'college', 'click_infopage', '${collegeName}']);" </c:if>>
								<span class="fl marRight_10">
									<c:set var="pdf">
										<bbbl:label key="lbl_findyourcollegefrag_pdf" language="${pageContext.request.locale.language}"/>
									</c:set>
									<dsp:img alt="${pdf}" title="${pdf}" src="${imagePath}/_assets/bbcollege/images/icon_pdf.png"/>	
								</span>
								<c:set target="${placeHolder}" property="schoolName"><dsp:valueof value="${schoolName}"/></c:set>
								<span class="bold grid_4 omega collegeInfo">
									${collegeName} <bbbl:label key="lbl_findyourcollegefrag_drom_moving" language="${pageContext.request.locale.language}" placeHolderMap="${placeHolder}"/>
									<span class="seeWhatLink"> 
									<bbbl:textArea key="txt_findyourcollegefrag_see_what_info" language="${pageContext.request.locale.language}"/>
									</span>
								</span>  
							</a>
						</c:if>
					</div>
					<div class="grid_12 clearfix marTop_20 collegeSearchResults">
						<p class="error"><bbbl:error key="err_no_store_found_for_college" language ="${pageContext.request.locale.language}"/></p>
					</div>
				</dsp:oparam>
				This suggestion variable is created to dynamically populate the address,city and stateCode in addr while clicking on suggested address
				<dsp:getvalueof var="suggestion" param="suggestion"/>
				<c:if test="${suggestion}">
					<dsp:getvalueof var="address" param="address"/>
					<dsp:getvalueof var="city" param="city"/>
					<dsp:getvalueof var="stateCode" param="stateCode"/>
					<dsp:getvalueof var="street" param="street"/>
					<dsp:getvalueof var="addr" value="${address},${city},${stateCode}"/>
				</c:if>
				<dsp:oparam name="addressSuggestion">
					<c:set var="foundAddrSuggestion" value="${true}" />
				
					<div class="grid_12 clearfix marTop_20 collegeSearchResults">
					    <div class="grid_7 alpha">
					    <c:set var="alignCss" value="omega"/>
					     <c:if test="${not empty collegeLogo}">
					    	<c:set var="logo">
					    		 <bbbl:label key="lbl_findyourcollegefrag_logo" language="${pageContext.request.locale.language}"/>
					    	</c:set>			    	
					        <div class="grid_1 alpha"><dsp:img alt="${logo}" title="${logo}" src="${collegeLogo}" /></div>
					        <c:set var="alignCss" value="alpha"/>
					        </c:if>
					        <div class="grid_6 ${alignCss}">
					        	<h3 class="auLogo"><dsp:valueof value="${schooolName}"/> </h3>
					            <c:set var="collegeName"><dsp:valueof value="${schooolName}"/></c:set>
					       </div>
					    </div>
					    <c:if test="${!empty pdfUrl}">
							<a class="marRight_10 fr" href="${pdfUrl}" <c:if test="${GoogleAnalyticsOn}"> onclick="_gaq.push(['_trackEvent', 'college', 'click_infopage', '${collegeName}']);" </c:if>>
								<span class="fl marRight_10">
									<c:set var="pdf">
										<bbbl:label key="lbl_findyourcollegefrag_pdf" language="${pageContext.request.locale.language}"/>
									</c:set>
									<dsp:img alt="${pdf}" title="${pdf}" src="${imagePath}/_assets/bbcollege/images/icon_pdf.png"/>	
								</span>
								<c:set target="${placeHolder}" property="schoolName"><dsp:valueof value="${schoolName}"/></c:set>
								<span class="bold grid_4 omega collegeInfo">
									${collegeName} <bbbl:label key="lbl_findyourcollegefrag_drom_moving" language="${pageContext.request.locale.language}" placeHolderMap="${placeHolder}"/>
									<span class="seeWhatLink"> 
									<bbbl:textArea key="txt_findyourcollegefrag_see_what_info" language="${pageContext.request.locale.language}"/>
									</span>
								</span>  
							</a>
						</c:if>
					</div>
					
					<div class="grid_12 clearfix marTop_20 collegeSearchResults">
						<p class="noMar marTop_10">
							<span class="error"><bbbl:label
									key="lbl_find_store_choose_address"
									language="${pageContext.request.locale.language}" />
							</span>
						</p>
						<ul>
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param name="array"
									param="StoreDetailsWrapper.storeAddressSuggestion" />
								<dsp:param name="elementName" value="storeAddressSuggestion" />
								<dsp:oparam name="output">
									<li class="clearfix"><span> 
										<dsp:getvalueof var="street" param="storeAddressSuggestion.street"/>
												<c:if test="${not empty street}">
													<dsp:valueof param="storeAddressSuggestion.street" />,
												</c:if> 
												<dsp:valueof
												param="storeAddressSuggestion.city" />, <dsp:valueof
												param="storeAddressSuggestion.address" />, <dsp:valueof
												param="storeAddressSuggestion.stateCode" />
									</span> 
									<c:set var="lbl_find_store_use_address"><bbbl:label key="lbl_find_store_use_address"
											language="${pageContext.request.locale.language}" /></c:set> 
										<dsp:a href="${contextPath}/selfservice/findyourcollege.jsp" title="${lbl_find_store_use_address}"	iclass="changeStoreDataPageLink">
										<bbbl:label key="lbl_find_store_use_address"
											language="${pageContext.request.locale.language}" />
										<dsp:param name="address"
											param="storeAddressSuggestion.city" />
										<dsp:param name="city" param="storeAddressSuggestion.address" />
										
										<dsp:param name="stateCode"
											param="storeAddressSuggestion.stateCode" />
										<dsp:param name="selState" param="selState"/>
										<dsp:param name="selCollege" param="selCollege"/>
										<dsp:param name="suggestion" value="true"/>
									</dsp:a></li>
								</dsp:oparam>
							</dsp:droplet>
						</ul>
					</div>
				</dsp:oparam>
				
			</dsp:droplet> --%>
			
			
			
		
					<div class="grid_12 clearfix marTop_20 collegeSearchResults">
						<p class="error"></p>	</div>
			<script type="text/javascript">
            setTimeout(function(){
				$(document).ready(function() {
					var options = {
							origin: "${addr}",												
							conceptID: '${currentSiteId}', 
							enableFavoriteStores: ${enableFavoriteStores},
							favoriteStoreID : '${favouriteStoreId}',
							preloadStoresBasedOnFavorite: false,
							debug:false,
							runSearchOnLoad: true,
							usePrintPage:true
						};
						
					BBB.BBBStoreLocator.init(options);
					BBB.BBBStoreLocator.initMap(); 
				});
			},3000);
			</script> 
			
			<div class="grid_12 clearfix marTop_20 collegeSearchResults">
					<h3 class=""><bbbl:label key="lbl_findyourcollegefrag_near_store" language="${pageContext.request.locale.language}"/></h3>
					
					<div id="storeLocatorPrintAddresses" class="container_12 printOnly" >
	                    <div id="storeLocatorPrintAddressesOrigin" class="grid_6 alpha "></div>                    
	                    <div id="storeLocatorPrintAddressesDestination" class="grid_6 omega "></div>	                    
                	</div> 
					
					<div id="storeLocatorResults" class="container_12" style="display: none;">
						<div id="storeLocatorAddressResults" class="grid_3 alpha omega "></div>
							
						<div id="storeLocatorMapResults" class="grid_9 alpha omega "></div>
						
						<div id="storeLocatorDirections" class="grid_3 alpha omega ">
							
							<div id="storeLocatorDirectionsFormWrapper">
								<a id="storeLocatorDirectionsBackToReultsLink" href="">&lt; Back to List</a>                  

								<h2>Map &amp; Directions</h2>

								<form id="storeLocatorDirectionsForm">
									<input id="localStoreLinkToYext" type="hidden" value="${localStoreLinkToYext}"/>
									<input id="hostNameForYEXTLink" type="hidden" value="${hostNameForYEXTLink}"/>
									<input id="linkDisplayNameForYEXTLink" type="hidden" value="${linkDisplayNameForYEXTLink}"/>
									<input type="hidden" name="storeLocatorDirectionsSearchingOnPoiKey" id="storeLocatorDirectionsSearchingOnPoiKey" value="" />
									<input type="hidden" name="storeLocatorDirectionsStartingLat" id="storeLocatorDirectionsStartingLat" value="" />
									<input type="hidden" name="storeLocatorDirectionsStartingLng" id="storeLocatorDirectionsStartingLng" value="" />
									<fieldset> 
										<legend class="offScreen"><bbbl:label key="lbl_pagination" language="${pageContext.request.locale.language}" /></legend>
			                        									
										<div class="input" id="storeLocatorDirectionsRouteTypeWrapper">
											<div class="label">
												<label ><bbbl:label key="lbl_store_route_option" language="${pageContext.request.locale.language}" /> </label>
											</div>
											<div id="storeLocatorDirectionsRouteTypeRadioGroup">
												<input type="radio" name="routeType" id="storeLocatorDirectionsFastest" checked="checked" value="fastest" />
												<label for="storeLocatorDirectionsFastest"><bbbl:label key="lbl_findstore_driving" language="${pageContext.request.locale.language}" /></label>

												<input type="radio" name="routeType" id="storeLocatorDirectionsTransit" value="multimodal" />
												<label for="storeLocatorDirectionsTransit"><bbbl:label key="lbl_findStore_PublicTransit" language="${pageContext.request.locale.language}" /></label>

												<input type="radio" name="routeType" id="storeLocatorDirectionsWalking" value="pedestrian" />
												<label for="storeLocatorDirectionsWalking"><bbbl:label key="lbl_findstore_walking" language="${pageContext.request.locale.language}" /></label>
											</div>
										</div>                                
										<div class="input" id="storeLocatorDirectionsOmniStartWrapper" >
											<div class="label">
												<label id="lblstoreLocatorDirectionsOmniStartInput" for="storeLocatorDirectionsOmniStartInput"><bbbl:label key="lbl_findstore_from" language="${pageContext.request.locale.language}" /></label>
											</div>
											<div class="text">
												<input type="text" placeholder="street, city, state, zip" name="storeLocatorDirectionsOmniStartInput" id="storeLocatorDirectionsOmniStartInput" aria-required="false" aria-labelledby="lblstoreLocatorDirectionsOmniStartInput errorstoreLocatorDirectionsOmniStartInput" />
											</div>
										</div>
										<div class="input" id="storeLocatorDirectionsOmniEndWrapper" >
											<div class="label">
												<label id="lblstoreLocatorDirectionsOmniEndInput" for="storeLocatorDirectionsOmniEndInput">To</label>
											</div>
											<div class="text">
												<input type="text" placeholder="" name="storeLocatorDirectionsOmniEndInput" id="storeLocatorDirectionsOmniEndInput" aria-required="false" aria-labelledby="lblstoreLocatorDirectionsOmniEndInput errorstoreLocatorDirectionsOmniEndInput" />
											</div>
										</div>
										
										<div class="checkboxItem input clearfix">
											<div class="checkbox">
												<input id="storeLocatorDirectionsAvoidSeasonalRoads" name="storeLocatorDirectionsAvoidSeasonalRoads" value="true" type="checkbox" >                                        
											</div>
											<div class="label">
												<label for="storeLocatorDirectionsAvoidSeasonalRoads"><bbbl:label key="lbl_store_route_avoid_seasonal_roads" language="${pageContext.request.locale.language}" /> </label>	
											</div>	
										</div>
										<div class="checkboxItem input clearfix">
											<div class="checkbox">
												<input id="storeLocatorDirectionsAvoidHighways" name="storeLocatorDirectionsAvoidHighways" value="true" type="checkbox" >
											</div>
											<div class="label">
												<label for="storeLocatorDirectionsAvoidHighways"><bbbl:label key="lbl_store_route_avoid_highways" language="${pageContext.request.locale.language}" /> </label>	
											</div>	
										</div>
										<div class="checkboxItem input clearfix">
											<div class="checkbox">
												<input id="storeLocatorDirectionsAvoidTolls" name="storeLocatorDirectionsAvoidTolls" value="true" type="checkbox" >
											</div>
											<div class="label">
												<label for="storeLocatorDirectionsAvoidTolls"> <bbbl:label key="lbl_store_route_avoid_toll_road" language="${pageContext.request.locale.language}" /></label>	
											</div>	
										</div>


										<div id="storeLocatorDirectionsDestination"></div>

										<div class="button button_active"> 
											<input type="submit" value="Get Directions" />
										</div>                             

									</fieldset>
								</form>
							</div>
							
							
							
							<div id="storeLocatorTurnByTurnRouteInfo"></div>
							<div id="storeLocatorTurnByTurnDirections"></div>
							
							
							
						</div>
					</div>
				</div>
			
		</dsp:oparam>
	</dsp:droplet>
	
	<c:if test="${!(empty schoolId)}">
		<dsp:droplet name="/com/bbb/selfservice/GetCollegeProductDroplet">
			<dsp:param name="collegeId" value="${schoolId}"/>
			<dsp:param name="siteId" bean="/atg/multisite/Site.id"/>
			<dsp:oparam name="output">	
			<dsp:getvalueof var="productList" param="productList"/>
				<div class="grid_12 alpha omega carouselTab">
					<c:set target="${placeHolder}" property="name"><dsp:valueof value="${schooolName}"/></c:set>
					<h3><bbbl:label key="lbl_findyourcollegefrag_merchandise" language="${pageContext.request.locale.language}" placeHolderMap="${placeHolder}"/></h3>							
					
					<dsp:include page="/browse/certona_prod_carousel.jsp">
					  <dsp:param name="productsVOsList" value="${productList}"/>
					  <dsp:param name="crossSellFlag" value="true"/>
				    </dsp:include>
			 	 
				</div>
			</dsp:oparam>
		</dsp:droplet>
	</c:if>
	
</dsp:page>
