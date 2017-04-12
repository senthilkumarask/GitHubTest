<dsp:page>
<dsp:importbean bean="com/bbb/selfservice/SearchStoreDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="com/bbb/selfservice/SearchStoreFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	
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
	<c:if test="${!empty favouriteStoreId}">
	<div class="storeListResultsWrap" data-status="true">
	
	<ul class="findStoreResultHeader clearfix">
		<li class="column1"><bbbl:label key="lbl_store_header" language="${pageContext.request.locale.language}"/></li>
		<li class="column2"><bbbl:label key="lbl_store_hour" language="${pageContext.request.locale.language}"/></li>
	</ul>
		<ul class="findStoreResult favoriteStore">
			<li class="storeInformation noBorder">
			<dsp:droplet name="/com/bbb/selfservice/SearchStoreDroplet">
				<dsp:param name="storeId" value="${favouriteStoreId}"/>
				<dsp:param name="searchType" value="2"/>
				<dsp:oparam name="output">
					<ul class="clearfix">
						<li class="column1">
							<div class="fl">
								<div class="clearfix">
									<label for="radioStoreAddressFav_1" class="storeAdd">
										<span class="storeName upperCase highlight"><strong><dsp:valueof param="StoreDetails.storeName"/></strong></span>
										<span class="miles">(<dsp:valueof param="StoreDetails.distance"/> <dsp:valueof param="StoreDetails.distanceUnit"/>)</span>
										<span class="street"><dsp:valueof param="StoreDetails.address"/></span>
										<span class="city"><dsp:valueof param="StoreDetails.city"/></span>
										<span class="state"><dsp:valueof param="StoreDetails.state"/> </span><span class="zip"><dsp:valueof param="StoreDetails.postalCode"/></span>
									</label>
								</div>
								
								<strong><dsp:valueof param="StoreDetails.storePhone"/></strong>
								<div class="actionLinks">
									<p class="marTop_10"><a class="viewDirectionsNew" href="#"  data-storeid="${favouriteStoreId}"  title="Get Map & Directions">Get Map & Directions</a></p>
									<%-- 
									<p class="marTop_10"><a class="viewDirections" href="#"><bbbl:label key='lbl_find_store_get_directions' language="${pageContext.request.locale.language}" /></a></p>
									<p class="marTop_10"><a class="viewOnMap" href="#"><bbbl:label key='lbl_find_store_view_on_map' language="${pageContext.request.locale.language}" /></a></p>
									--%>
								</div>
							</div>
						</li>
						
						<li class="column2">
						<div class="fl storePickupBenifitsWrap">
							<dsp:getvalueof param="StoreDetails.weekdaysStoreTimings" id="weekdaysStoreTimings"/>
							<dsp:getvalueof param="StoreDetails.satStoreTimings" id="satStoreTimings"/>
							<dsp:getvalueof param="StoreDetails.sunStoreTimings" id="sunStoreTimings"/>
							<dsp:getvalueof param="StoreDetails.otherTimings1" id="otherTimings1"/>
							<dsp:getvalueof param="StoreDetails.otherTimings2" id="otherTimings2"/>
							<p>
							<c:forTokens items="${weekdaysStoreTimings}" delims="," var="item">${item}</c:forTokens>
								<br/>
								<c:forTokens items="${satStoreTimings}" delims="," var="item">${item}</c:forTokens>
								<br/>
								<c:forTokens items="${sunStoreTimings}" delims="," var="item">${item}</c:forTokens>
								<br/>
								<c:forTokens items="${otherTimings1}" delims="," var="item">${item}</c:forTokens>
								<br/>
								<c:forTokens items="${otherTimings2}" delims="," var="item">${item}</c:forTokens>
								<br/>
								<span class="storePickupStorePhone">
									<strong><dsp:valueof param="StoreDetails.storePhone"/></strong><br/>
								</span>
								<dsp:valueof param="StoreDetails.storeDescription" />
								
							</p>
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
					
					<li class="column3">	
					 <div class="ui-dialog-buttonset">
						<div class="button button_active">
							<input type="hidden" value="${favouriteStoreId}" name="storeId"/>
							<input type="submit" value="Select Store" name="changeStoreBtn" id="changeStoreBtn" />
						</div>
					 </div>
					</li>
				</ul>
			
		</dsp:oparam>
		<dsp:oparam name="empty">
				<bbbl:label key="lbl_favoritestore_disablemessage" language="${pageContext.request.locale.language}"/>
		</dsp:oparam>
		</dsp:droplet>
		</li>
		</ul>
		</div>
		</c:if>
		<dsp:droplet name="SearchStoreDroplet">
				<%-- <dsp:param name="searchString" value="12345"/>
				<dsp:param name="searchType" value="2"/> --%>
			<dsp:param name="searchBasedOn" value="cookie"  />
			<%-- <dsp:param name="pageSize" value="4"/> --%>
			<dsp:droplet name="/atg/dynamo/droplet/Switch">
				<dsp:param name="value" param="pageKey" />
				<dsp:oparam name="true">
					<dsp:param name="pageKey" param="pageKey" />
					<dsp:param name="pageNumber" param="pageNumber" />
				</dsp:oparam>
			</dsp:droplet>
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
						<c:set target="${placeHolderMap}" property="storeMiles" value="${miles}" />
						<c:choose>
							<c:when test="${itemcount gt 1}">
								<bbbt:textArea key="txt_store_multiple_found_msg" placeHolderMap="${placeHolderMap}" language="${pageContext.request.locale.language}" />
							</c:when>
							<c:otherwise>
								<bbbt:textArea key="txt_store_single_found_msg" placeHolderMap="${placeHolderMap}" language="${pageContext.request.locale.language}" />
							</c:otherwise>
						</c:choose>
					<dsp:droplet name="IsEmpty">
						<dsp:param name="value"	bean="SearchStoreFormHandler.inputSearchString" />
						<dsp:oparam name="true">
							 <dsp:valueof param="inputSearchString" /> 
						</dsp:oparam>
						<dsp:oparam name="false">									
							<dsp:valueof bean="SearchStoreFormHandler.inputSearchString" />
						</dsp:oparam>
					</dsp:droplet>
					
				</p>
				<div class="fl padLeft_10">
					<div class="button button_secondary">
						<input type="submit" value="Change location" name="btnChangeLocation" class="showWithResult searchAgainBtn"/>
					</div>
				</div>
				<div class="clear"></div>
			</div>	
			<ul class="findStoreResultHeader clearfix">
					<li class="column1"><bbbl:label key="lbl_store_header" language="${pageContext.request.locale.language}"/></li>
					<li class="column2"><bbbl:label key="lbl_store_hour" language="${pageContext.request.locale.language}"/></li>
			</ul>
			<div class="findStoreResultWrapper clearfix">
			<ul class="findStoreResult noMarTop">
			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:param name="array" param="StoreDetailsWrapper.storeDetails" />
				<dsp:param name="elementName" value="StoreDetails" />
				<dsp:oparam name="output">	
					<dsp:getvalueof var="storeDetails" param="StoreDetails"/>
					<dsp:getvalueof var="storeId" param="StoreDetails.storeId"/>
				<li class="storeInformation">
					<ul class="clearfix">
						<li class="column1">
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
							<div class="actionLinks">
								<%--  New version of view map/get directions --%>	
								<p class="marTop_10"><a class="viewDirectionsNew" href="#"  data-storeid="${storeId}"  title="Get Map & Directions">Get Map & Directions</a></p>
								<%--
								<p class="marTop_10"><a class="viewDirections" href="#"><bbbl:label key='lbl_find_store_get_directions' language="${pageContext.request.locale.language}" /></a></p>
								<p class="marTop_10"><a class="viewOnMap" href="#"><bbbl:label key='lbl_find_store_view_on_map' language="${pageContext.request.locale.language}" /></a></p>
								--%>
							</div>
								<br/>
							</div>
						</li>
							
							<li class="column2">	
								<div class="fl storePickupBenifitsWrap">
									<dsp:getvalueof param="StoreDetails.weekdaysStoreTimings" id="weekdaysStoreTimings"/>
									<dsp:getvalueof param="StoreDetails.satStoreTimings" id="satStoreTimings"/>
									<dsp:getvalueof param="StoreDetails.sunStoreTimings" id="sunStoreTimings"/>
									<dsp:getvalueof param="StoreDetails.otherTimings1" id="otherTimings1"/>
									<dsp:getvalueof param="StoreDetails.otherTimings2" id="otherTimings2"/>
									<p><c:forTokens items="${weekdaysStoreTimings}" delims="," var="item">${item}</c:forTokens>
										<br/>
										<c:forTokens items="${satStoreTimings}" delims="," var="item">${item}</c:forTokens>
										<br/>
										<c:forTokens items="${sunStoreTimings}" delims="," var="item">${item}</c:forTokens>
										<br/>
										<c:forTokens items="${otherTimings1}" delims="," var="item">${item}</c:forTokens>
										<br/>
										<c:forTokens items="${otherTimings2}" delims="," var="item">${item}</c:forTokens>
										<br/>
										<span class="storePickupStorePhone">
											<strong><dsp:valueof param="StoreDetails.storePhone"/></strong><br/>
										</span>
										<dsp:valueof param="StoreDetails.storeDescription" />
									</p>
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
							
						<li class="column3">	
						 <div class="ui-dialog-buttonset">
							<div class="button button_active">
								<input type="hidden" value="${storeId}" name="storeId"/>
								<input type="submit" value="Select Store" name="changeStoreBtn" id="changeStoreBtn"/>
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
					<div class="button button_secondary">
						<input type="submit" value="Change location" name="btnChangeLocation" class="showWithResult searchAgainBtn"/>
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