<dsp:page>
<dsp:importbean bean="com/bbb/selfservice/SearchStoreDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="com/bbb/selfservice/SearchStoreFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:getvalueof var="Zipcode" param="Zipcode"/>
	<dsp:getvalueof var="storeAddress" param="storeAddress"/>
	<dsp:getvalueof var="storeCity" param="storeCity"/>
	<dsp:getvalueof var="storeState" param="storeState"/>
	<dsp:getvalueof var="radius" param="radius"/>	
	<dsp:getvalueof var="searchType" param="searchType"/>
	
	<c:choose>
		<c:when test="${searchType eq '2'}">
			<c:set var="searchString" value="${Zipcode}" />
		</c:when>
		<c:when test="${searchType eq '3'}">
			<c:choose>
				<c:when test="${not empty storeAddress}">
					<c:set var="searchString" value="${storeAddress}, ${storeCity}, ${storeState}" />
				</c:when>
				<c:otherwise>
					<c:set var="searchString" value="${storeCity}, ${storeState}" />
				</c:otherwise>
			</c:choose>
		</c:when>
	</c:choose>
	
	
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
	
<div class="storeListResultsWrap" data-status="true">
	<form method="post" action="#" name="selectStoreForm" id="selectStoreForm">
		<dsp:droplet name="com/bbb/selfservice/TBSSimpleRegSearchStoreDroplet">
				<dsp:param name="searchString" value="${searchString}"/>
				<dsp:param name="searchType" value="${searchType}"/>
			<dsp:oparam name="output">
			<div class="clearfix padTop_20">
				<p class="storeResults fl padTop_5">
				<dsp:droplet name="/atg/dynamo/droplet/ForEach">
					<dsp:param name="array" param="StoreDetailsWrapper.storeDetails" />
					<dsp:param name="elementName" value="StoreDetails" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="itemcount" param="count"
							vartype="java.lang.Integer" />
					</dsp:oparam>
				</dsp:droplet>
					 <jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request" />
						<c:set target="${placeHolderMap}" property="storeCount" value="${itemcount}" />
						<c:set target="${placeHolderMap}" property="storeMiles" value="${radius}" />
						<c:choose>
							<c:when test="${itemcount gt 1}">
								<bbbt:textArea key="txt_store_multiple_found_msg" placeHolderMap="${placeHolderMap}" language="${pageContext.request.locale.language}" />
							</c:when>
							<c:otherwise>
								<bbbt:textArea key="txt_store_single_found_msg" placeHolderMap="${placeHolderMap}" language="${pageContext.request.locale.language}" />
							</c:otherwise>
						</c:choose>
					<dsp:valueof param="searchString" />
					
				</p>
				<div class="fl padLeft_10">
					<div class="tbsSearchAgainbtnHolder">
						<a href="#" class="createRegistryButtons showWithResult tbsSearchAgainBtn" id='tbsSearchAgainBtnId'>Change location</a>	
					</div>
				</div>
				<div class="clear"></div>
			</div>	
			<ul class="findStoreResultHeader clearfix">
					<li class="large-4 small-4 columns"><bbbl:label key="lbl_store_header" language="${pageContext.request.locale.language}"/></li>
					<li class="large-8 small-8 columns"><bbbl:label key="lbl_store_hour" language="${pageContext.request.locale.language}"/></li>
			</ul>
			<div class="findStoreResultWrapper clearfix">
			<ul class="findStoreResult noMarTop row">
			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:param name="array" param="StoreDetailsWrapper.storeDetails" />
				<dsp:param name="elementName" value="StoreDetails" />
				<dsp:oparam name="output">	
					<dsp:getvalueof var="storeDetails" param="StoreDetails"/>
					<dsp:getvalueof var="storeId" param="StoreDetails.storeId"/>
				<li class="storeInformation large-12 small-12 columns">
					<ul class="clearfix">
						<li class="large-4 small-4 columns">
							<div class="fl">
								<div class="clearfix">
								<label for="radioStoreAddress_<dsp:valueof param="count"/>" class="storeAdd">
									<span class="storeName upperCase"><strong><dsp:valueof param="StoreDetails.storeName" /></strong></span>
									<span class="miles">(<dsp:valueof param="StoreDetails.distance" /> <dsp:valueof param="StoreDetails.distanceUnit" />)</span>
									<span class="street"><dsp:valueof param="StoreDetails.address" /></span>
									<span class="city"><dsp:valueof param="StoreDetails.city" /></span>
									<span class="state"><dsp:valueof param="StoreDetails.state" /> </span><span class="zip"><dsp:valueof param="StoreDetails.postalCode" /></span>
								</label>
								<br/>
								<strong><dsp:valueof param="StoreDetails.storePhone"/></strong>
								
							</div>
							<div class="actionLinks" style="display:none;">
								<%--  New version of view map/get directions --%>	
								<p class="marTop_10"><a class="viewDirectionsNew" href="#"  data-storeid="${storeId}"  title="Get Map & Directions">Get Map & Directions</a></p>
								<%--
								<p class="marTop_10"><a class="viewDirections" href="#"><bbbl:label key='lbl_find_store_get_directions' language="${pageContext.request.locale.language}" /></a></p>
								<p class="marTop_10"><a class="viewOnMap" href="#"><bbbl:label key='lbl_find_store_view_on_map' language="${pageContext.request.locale.language}" /></a></p>
								--%>
							</div>
							<div class="actionLinks">
								<p class="marTop_5">
									<a title="Get Map &amp; Directions" ;="" onclick="javascript:externalLinks('mapquest: view maps')" data-storeid="${storeId}" href="#" class="viewDirectionsNew">Get Map &amp; Directions</a>
								</p>
			                </div>								
							</div>
						</li>
							
							<li class="large-4 small-4 columns">	
								<div class="fl storePickupBenifitsWrap">
									<dsp:getvalueof param="StoreDetails.weekdaysStoreTimings" id="weekdaysStoreTimings"/>
									<dsp:getvalueof param="StoreDetails.satStoreTimings" id="satStoreTimings"/>
									<dsp:getvalueof param="StoreDetails.sunStoreTimings" id="sunStoreTimings"/>
									<dsp:getvalueof param="StoreDetails.otherTimings1" id="otherTimings1"/>
									<dsp:getvalueof param="StoreDetails.otherTimings2" id="otherTimings2"/>
									<ul>
										<li class="storePhone"><strong><dsp:valueof param="StoreDetails.storePhone"/></strong></li>
										<li><c:forTokens items="${weekdaysStoreTimings}" delims="," var="item">${item}</c:forTokens></li>
										<li><c:forTokens items="${satStoreTimings}" delims="," var="item">${item}</c:forTokens></li>
										<li><c:forTokens items="${sunStoreTimings}" delims="," var="item">${item}</c:forTokens></li>
										<li><c:forTokens items="${otherTimings1}" delims="," var="item">${item}</c:forTokens></li>
										<li><c:forTokens items="${otherTimings2}" delims="," var="item">${item}</c:forTokens></li>
										<li>
										<span class="storePickupStorePhone">
											<strong><dsp:valueof param="StoreDetails.storePhone"/></strong><br/>
										</span>
										<dsp:valueof param="StoreDetails.storeDescription" />
										</li>
									</ul>
									<div class="benefitItem">
										<div class="clearfix padTop_5">
											<dsp:getvalueof param="StoreDetails.specialtyShopsCd" id="code"/>
												<dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
												<dsp:param name="code" value="${code}"/>
												<dsp:param name="queryRQL" value="specialityShopCd=:code"/>
												<dsp:param name="repository" value="/com/bbb/selfservice/repository/StoreRepository"/>
												<dsp:param name="itemDescriptor" value="specialityCodeMap"/>
												<dsp:param name="elementName" value="item"/>
												<dsp:oparam name="output">
													<dsp:droplet name="/atg/dynamo/droplet/ForEach">
														<dsp:param name="array" param="item.specialityCd"/>
														<dsp:param name="elementName" value="list"/>
														<dsp:oparam name="output">
															<dsp:getvalueof param="list.codeImage" id="image"/>
															<dsp:getvalueof param="list.storeListTitleText" id="storeListTitleText"/>
															<dsp:getvalueof param="list.storeListAltText" id="storeListAltText"/>
															<div class="benefitsItem padBottom_5"><img src="${imagePath}${image}" alt="${storeListAltText}" /></div>
														</dsp:oparam>
													</dsp:droplet>
												</dsp:oparam>
											</dsp:droplet>
										</div>
									</div>
									
								</div>
							</li>
							
						<li class="large-4 small-4 columns">	
						 <div class="ui-dialog-buttonset">
							<div class="">
								<input type="hidden" value="${storeId}" name="storeId"/>
								<!--<input type="submit" value="Select Store" name="changeStoreBtn" id="changeStoreBtn"/>-->
								<a href="#" class="createRegistryButtons showWithResult tbsSearchAgainBtn tbsSelectStoreButton" id='tbsSearchAgainBtnId_${favouriteStoreId}'>Select Store</a>
							</div>
						 </div>
						</li>
					</ul>
					<input type="hidden" class="contactFlag" name="contactFlag" value="${storeDetails.contactFlag}" />
				</li>
				</dsp:oparam>
				</dsp:droplet>
			</ul>
			</div>
			
			
		</dsp:oparam>
		<dsp:oparam name="empty">
			<div class="clearfix padTop_20">
				<p class="noResultsFound cb padTop_5 fl">
					<bbbl:label key="lbl_search_store_no_record_found"	language="${pageContext.request.locale.language}" />
				</p>
				<div class="fl padLeft_10">
					<div class="tbsSearchAgainbtnHolder">
						<a href="#" class="createRegistryButtons showWithResult tbsSearchAgainBtn" id='tbsSearchAgainBtnId'>Change location</a>	
					</div>
				</div>
				<div class="clear"></div>
			</div>
			</dsp:oparam>
		
		</dsp:droplet>
		<div class="clearfix marTop_10">
			<div class="width_7 fl">
				<p><bbbl:label key='lbl_find_store_availability_based' language="${pageContext.request.locale.language}" /></p>
				<p class="marTop_10"><bbbl:label key='lbl_find_store_store_hours' language="${pageContext.request.locale.language}" /></p>
			</div>
			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:param name="array" param="StoreDetailsWrapper.storeDetails" />
				<dsp:param name="elementName" value="StoreDetails" />
				<dsp:oparam name="output">
					
					<dsp:getvalueof id="currentPage" param="StoreDetailsWrapper.currentPage" />
					<dsp:getvalueof id="totalPageCount" param="StoreDetailsWrapper.totalPageCount" />
				</dsp:oparam>
			</dsp:droplet>
			<div class="width_3 fl marLeft_10 viewMorePrevLinks">
				<%-- <a href="/_ajax/find_in_store_result.jsp?storeZip=10007&add2=270+greenwich&storeCity=murray&storeState=FL&selRadiusName=15&productId=80661234&itemQuantity=2&startNum=10" class="marRight_5 changeStoreDataPageLink"><< View Previous</a> <a href="/_ajax/find_in_store_result.jsp?storeZip=10007&add2=270+greenwich&storeCity=murray&storeState=FL&selRadiusName=15&productId=80661234&itemQuantity=2&startNum=16" class="marLeft_20 changeStoreDataPageLink">View More >></a>  --%>
				<dsp:getvalueof id="pageKey" param="StoreDetailsWrapper.pageKey" />
				<c:if test="${currentPage gt 1}">
					<dsp:getvalueof id="pageKey" param="StoreDetailsWrapper.pageKey" />
					<dsp:a href="${contextPath}/giftregistry/modals/store_info_no_stock_info.jsp" iclass="marRight_5 changeStoreDataPageLink">
						<dsp:param name="pageKey" value="${pageKey}" />
						<dsp:param name="pageNumber" value="${currentPage-1}" />										
						<bbbl:label key="lbl_search_store__previous" language="${pageContext.request.locale.language}"/>
					</dsp:a>
				</c:if>
				<c:if test="${currentPage lt totalPageCount}">
					<dsp:getvalueof id="pageKey" param="StoreDetailsWrapper.pageKey" />
					<dsp:a href="${contextPath}/giftregistry/modals/store_info_no_stock_info.jsp" iclass="marLeft_20 changeStoreDataPageLink">
						<dsp:param name="pageKey" value="${pageKey}" />
						<dsp:param name="pageNumber" value="${currentPage+1}" /><bbbl:label key="lbl_search_store__next" language="${pageContext.request.locale.language}"/>
					</dsp:a>
				</c:if> 
			</div>
		</div>
		
	</form>
</div>
</dsp:page>