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
		<c:if test="${!empty favouriteStoreId}">
		<div class="storeListResultsWrap" data-status="true">
		
		<ul class="findStoreResultHeader clearfix">
			<li class="column1"><bbbl:label key="lbl_store_header" language="${pageContext.request.locale.language}"/></li>
			<li class="column2"><bbbl:label key="lbl_store_hour" language="${pageContext.request.locale.language}"/></li>
		</ul>
			<div class="storeResult favorite clearfix noPadLeft noPadRight noPadBot noMarBot">
				<h3 class="padLeft_10"><bbbl:label key="lbl_store_info_frag_fav_store" language="${pageContext.request.locale.language}"/></h3>
				
				<ul class="findStoreResult favoriteStore">
				<li class="storeInformation noBorder noPad">
				<dsp:droplet name="/com/bbb/selfservice/SearchStoreDroplet">
					<dsp:param name="storeId" value="${favouriteStoreId}"/>
					<dsp:param name="searchType" value="2"/>
					<dsp:oparam name="output">
					<ul class="clearfix">
						<li class="column1">
						<div class="grid_3 alpha omega location">
							<h4><span class="storeTitle"><dsp:valueof param="StoreDetails.storeName"/></span> <span></span></h4>
							<div class="address">
								<div class="street"><dsp:valueof param="StoreDetails.address"/></div>
								<div>
									<span class="city"><dsp:valueof param="StoreDetails.city"/>,</span>
									<span class="state"><dsp:valueof param="StoreDetails.state"/></span>
									<span class="zip"><dsp:valueof param="StoreDetails.postalCode"/></span>
								</div>
								<div class="phone"><dsp:valueof param="StoreDetails.storePhone"/></div>
								
							</div>
							<p class="marTop_5">
								<div class="actionLink">
                                    <dsp:getvalueof var="storeId" param="StoreDetails.storeId" />
                                    <a class="viewDirectionsNew" href="#" data-storeid="${storeId}" title="Get Map & Directions">Get Map & Directions</a>
									<%-- <a class="viewDirections" href="#viewDirections"><bbbl:label key="lbl_store_info_frag_get_dir" language="${pageContext.request.locale.language}"/></a> --%>
								</div>
							</p>
							<%--<p class="marTop_5">
								<div class="actionLink">
									<a class="viewMap" href="#viewMap"><bbbl:label key="lbl_store_info_frag_view_map" language="${pageContext.request.locale.language}"/></a>
								</div>
							</p>--%>
						</div>
						</li>
						
						<li class="column2">
								<dsp:getvalueof var="StoreDetailsVar" param="StoreDetails" scope="page"/>	
								<div class="grid_4 alpha omega">
										<p><c:forTokens items="${StoreDetailsVar.weekdaysStoreTimings}" delims="," var="item">
											${item}
										</c:forTokens>
										<br/>
										<c:forTokens items="${StoreDetailsVar.satStoreTimings}" delims="," var="item">
											${item}
										</c:forTokens>
										<br/>
										<c:forTokens items="${StoreDetailsVar.sunStoreTimings}" delims="," var="item">
											${item}
										</c:forTokens>
										<br/>
										<c:forTokens items="${StoreDetailsVar.otherTimings1}" delims="," var="item">
											${item}
										</c:forTokens>
										<br/>
										<c:forTokens items="${StoreDetailsVar.otherTimings2}" delims="," var="item">
												${item}
										</c:forTokens>
										<br/>
											${StoreDetailsVar.storeDescription}
										</p>
										
										<c:set var="lblhealth">
											<bbbl:label key="lbl_store_info_frag_health" language="${pageContext.request.locale.language}"/>
										</c:set>
										<div class="formRow clearfix">
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
															<div class="benefitItem padBottom_5"><img src="${imagePath}${image}" alt="${storeListAltText}" /></div>
														</dsp:oparam>	
													</dsp:droplet>
												</dsp:oparam>
											</dsp:droplet>
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
	</div>
	</c:if>
		<dsp:droplet name="SearchStoreDroplet">
			<dsp:param name="searchBasedOn" value="cookie"  />
			<%-- <dsp:param name="pageSize" value="4"/> --%>
			<dsp:droplet name="/atg/dynamo/droplet/Switch">
				<dsp:param name="value" param="pageKey" />
				<dsp:oparam name="true">
					<dsp:param name="pageKey" param="pageKey" />
					<dsp:param name="pageNumber" param="pageNumber" />
				</dsp:oparam>
			</dsp:droplet>
			<dsp:oparam name="addressSuggestion">
				<div class="grid_5 alpha multipleAddFound">
					<p class="noMar marTop_10">
						<span class="error">
							<bbbl:label key="lbl_find_store_choose_address"	language="${pageContext.request.locale.language}"/>
						</span>
					</p>
					<ul>
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array" param="StoreDetailsWrapper.storeAddressSuggestion" />
							<dsp:param name="elementName" value="storeAddressSuggestion" />
							<dsp:oparam name="output">
									<li class="clearfix"><span>
										<dsp:valueof param="storeAddressSuggestion.city" />,
										<dsp:valueof param="storeAddressSuggestion.address" />,
										<dsp:valueof param="storeAddressSuggestion.stateCode" />
										</span>
										<dsp:setvalue bean="SearchStoreFormHandler.siteContext" beanvalue="/atg/multisite/Site.id" />
										<dsp:setvalue bean="SearchStoreFormHandler.searchStoreSuccessURL"
											value="${contextPath}/selfservice/store/store_info_frag.jsp" />
										<dsp:setvalue bean="SearchStoreFormHandler.searchStoreErrorURL"
											value="${contextPath}/selfservice/store/store_info_frag.jsp" />
										<%--multiple_address.jsp will call the handleSearchStore method of SearchStoreFormHandler with suggested Address. --%>
										<dsp:a href="${contextPath}/selfservice/store/multiple_address.jsp" iclass="changeStoreDataPageLink">
											<bbbl:label key="lbl_find_store_use_address" language="${pageContext.request.locale.language}"/>
											<dsp:param name="address" param="storeAddressSuggestion.address"/>
											<dsp:param name="city" param="storeAddressSuggestion.city"/>
											<dsp:param name="stateCode" param="storeAddressSuggestion.stateCode"/>
										</dsp:a>
									</li>
							</dsp:oparam>
						</dsp:droplet> 
					</ul> 
				</div>
			</dsp:oparam>
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
								<li class="storeInformation noPad">								
									<dsp:include page="store_info.jsp">
										<dsp:param name="storeDetails" param="StoreDetails" />
										<%-- To display hyperlinks.. under search results --%>
										<dsp:param name="linkViewMap" value="1" />
										<dsp:param name="linkgetDirections" value="1" />
										<dsp:param name="linkFavorite" value="0" />
									</dsp:include>
								</li>
								<dsp:getvalueof id="currentPage" param="StoreDetailsWrapper.currentPage" />
								<dsp:getvalueof id="totalPageCount" param="StoreDetailsWrapper.totalPageCount" />
							</dsp:oparam>
						</dsp:droplet>
					</ul>
				</div>
	
				<div class="width_3 fr marLeft_10 marBottom_10 viewMorePrevLinks">
					<dsp:getvalueof id="pageKey" param="StoreDetailsWrapper.pageKey" />
					<c:if test="${currentPage gt 1}">
						<dsp:getvalueof id="pageKey" param="StoreDetailsWrapper.pageKey" />
						<dsp:a href="${contextPath}/selfservice/store/store_info_frag.jsp" iclass="marRight_5 changeStoreDataPageLink">
							<dsp:param name="pageKey" value="${pageKey}" />
							<dsp:param name="pageNumber" value="${currentPage-1}" />										
							<bbbl:label key="lbl_search_store__previous" language="${pageContext.request.locale.language}"/>
						</dsp:a>
					</c:if>
					<c:if test="${currentPage lt totalPageCount}">
						<dsp:getvalueof id="pageKey" param="StoreDetailsWrapper.pageKey" />
						<dsp:a href="${contextPath}/selfservice/store/store_info_frag.jsp" iclass="marLeft_20 changeStoreDataPageLink">
							<dsp:param name="pageKey" value="${pageKey}" />
							<dsp:param name="pageNumber" value="${currentPage+1}" /><bbbl:label key="lbl_search_store__next" language="${pageContext.request.locale.language}"/>
						</dsp:a>
					</c:if>
				</div>
			</dsp:oparam>
			<dsp:oparam name="error">
			<div class="error marTop_20">
				<p>
					<bbbl:label key="lbl_search_store_error" language="${pageContext.request.locale.language}" />
				</p>
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
	</div>
	
</dsp:page>