<dsp:page>
<dsp:importbean bean="com/bbb/selfservice/SearchInStoreDroplet" />
<dsp:importbean var="SystemErrorInfo" bean="/com/bbb/utils/SystemErrorInfo"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
<dsp:importbean bean="com/bbb/selfservice/SearchStoreFormHandler" />
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:getvalueof var="isInternationalCustomer" bean="/com/bbb/profile/session/SessionBean.internationalShippingContext" />
<c:if test="${isInternationalCustomer ne true}">
	<c:set var="supplyBalance">
		<bbbc:config key="SUPPLY_BALANCE_ON" configName="FlagDrivenFunctions" />
	</c:set>
	<c:set var="localStorePDP">
		<bbbc:config key="LOCAL_STORE_PDP_FLAG" configName="FlagDrivenFunctions" />
	</c:set>
</c:if>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<%--BBBSL-6922 initialize session if first request start--%>
<dsp:getvalueof var="formInitialised" bean="SearchStoreFormHandler.formInitialised" />
<c:if test="${not formInitialised }">
	<dsp:include src="change_store_error_status.jsp">
		<dsp:param name="skipJsonError" value="true"/>
	</dsp:include>
</c:if>
<%--BBBSL-6922 End--%>
<dsp:getvalueof var="vdcPassed" param="vdcPassed" />

<div class="storeListResultsWrap" data-status="true">
<%--  CR Changes  --%>
	<c:set var="BedBathCanadaSite">
		<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>	
	<c:if test="${currentSiteId ne BedBathCanadaSiteCode}">
        <bbbt:textArea key="txt_findinstore_msg" language ="${pageContext.request.locale.language}"/>
    </c:if>
    
	<form method="post" action="#" name="selectStoreForm" id="selectStoreForm">
	<c:set var="BedBathCanadaSite">
		<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	
	
	      <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                     <dsp:param name="array" bean="Profile.userSiteItems" />
                     <dsp:param name="elementName" value="sites" />
                     <dsp:oparam name="output">
                        <dsp:getvalueof id="key" param="key" />
                        <dsp:getvalueof id="favStoreId" param="sites.favouriteStoreId" />
                        <c:if test="${currentSiteId eq key}">
                           <c:set var="favouriteStoreId" value="${favStoreId}"></c:set>
                        </c:if>
                     </dsp:oparam>
                  </dsp:droplet>
	
		          <c:if test="${favouriteStoreId != null && not empty favouriteStoreId }">
							<p class="marTop_10 marBottom_10 bold textCenter">
								<bbbl:label key="lbl_your_favorite_store" language="${pageContext.request.locale.language}" />
							</p>
							<ul class="findStoreResultHeader clearfix">
								<li class="column1"><bbbl:label
									key="lbl_store_header"
									language="${pageContext.request.locale.language}" /></li>
								<li class="column2"><bbbl:label
									key="lbl_store_hour"
									language="${pageContext.request.locale.language}" /></li>
								<!-- <li class="column3">&nbsp;</li> -->
								<li class="column3"><bbbl:label
									key="lbl_store_availability"
									language="${pageContext.request.locale.language}" /></li>
								<li class="column4">Reserve</li>
							</ul>
							
					</c:if>
							
									<dsp:droplet name="SearchInStoreDroplet">
									<dsp:param name="searchBasedOn" value="cookie" />
										<dsp:param name="storeId" value="${favouriteStoreId}" />
										<dsp:param name="productId" value="${param.skuid}" />
										<dsp:param name="orderedQty" value="${param.itemQuantity}" />
                                        <dsp:param name="siteId" bean="/atg/multisite/SiteContext.site.id"/>
										<dsp:oparam name="favstoreoutput">
											<dsp:getvalueof var="storeId" param="StoreDetails.storeId" />
											<dsp:getvalueof var="productChkFlg"
												param="productAvailable.${storeId}" vartype="java.lang.Integer"
												scope="page" />		
											<c:if test="${productChkFlg==0 || productChkFlg==2}">
												<c:set var="favStorePresent" value="true" scope="page"/>
											</c:if>
											<c:if test="${favouriteStoreId != null && not empty favouriteStoreId}">
											 <c:set var="passFavStore" value="favstore"></c:set>
											</c:if>
								   <ul class="findStoreResult favoriteStore">
								     <li class="storeInformation noBorder">
											<dsp:include page="find_in_store_result_info.jsp">
												<dsp:param name="itemcount" value="0" />
												<dsp:param name="favstorePresent" value="${passFavStore}" />
												<dsp:param name="StoreDetails" param="StoreDetails" />
												<dsp:param name="vdcPassed" value="${vdcPassed}" />
												<dsp:param name="supplyBalance" value="${supplyBalance}" />
												<dsp:param name="localStorePDP" value="${localStorePDP}" />
											</dsp:include>
								  	 </li>
							        </ul>
										</dsp:oparam>
										<dsp:oparam name="favstoreempty">
										<bbbl:label key="lbl_favoritestore_disablemessage" language="${pageContext.request.locale.language}"/>
										</dsp:oparam>
										<dsp:oparam name="favstoreerror">
										</dsp:oparam>	
							
											<dsp:oparam name="addressSuggestion">
					<c:set var="foundAddrSuggestion" value="${true}" />
					<div class="grid_5 alpha multipleAddFound">
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
									<li class="clearfix"><span> <dsp:getvalueof var="street" param="storeAddressSuggestion.street"/>
												<c:if test="${not empty street}">
													<dsp:valueof param="storeAddressSuggestion.street" />,
												</c:if>
												<dsp:valueof
												param="storeAddressSuggestion.address" />, <dsp:valueof
												param="storeAddressSuggestion.city" />, <dsp:valueof
												param="storeAddressSuggestion.stateCode" />
									</span> <%--multiple_address.jsp will call the handleSearchStore method of SearchStoreFormHandler with suggested Address. --%>
									<dsp:setvalue bean="SearchStoreFormHandler.siteContext" beanvalue="/atg/multisite/Site.id" />
										<dsp:setvalue
											bean="SearchStoreFormHandler.searchStoreSuccessURL"
											value="${contextPath}/selfservice/find_in_store_result.jsp" /> 
											<dsp:setvalue	bean="SearchStoreFormHandler.searchStoreErrorURL"value="${contextPath}/selfservice/find_in_store_result.jsp" />
											<!--<dsp:input bean="SearchStoreFormHandler.fromPage" type="hidden" value="findinstoreresult" />-->
											<c:set var="lbl_find_store_use_address"><bbbl:label key="lbl_find_store_use_address"
												language="${pageContext.request.locale.language}" /></c:set> 
											<dsp:a href="${contextPath}/selfservice/store/multiple_address.jsp" title="${lbl_find_store_use_address}"	iclass="changeStoreDataPageLink">
											<bbbl:label key="lbl_find_store_use_address"
												language="${pageContext.request.locale.language}" />
											<dsp:param name="address"
												param="storeAddressSuggestion.address" />
											<dsp:param name="city" param="storeAddressSuggestion.city" />
											<dsp:param name="stateCode"
												param="storeAddressSuggestion.stateCode" />
											</dsp:a></li>
								</dsp:oparam>
							</dsp:droplet>
						</ul>
					</div>
				</dsp:oparam>
			<dsp:oparam name="output">
			<dsp:getvalueof var="babyCanadaFlag" param="StoreDetailsWrapper.babyCanadaFlag"/>
			<dsp:getvalueof var="babyCanadaStoreCount" param="StoreDetailsWrapper.babyCanadaStoreCount"/>
			<dsp:getvalueof var="bedBathCanadaStoreCount" param="StoreDetailsWrapper.bedBathCanadaStoreCount"/>
				<dsp:droplet name="/atg/dynamo/droplet/ForEach">
					<dsp:param name="array" param="StoreDetailsWrapper.storeDetails" />
					<dsp:param name="elementName" value="StoreDetails" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="itemcount" param="count"
							vartype="java.lang.Integer" />
					</dsp:oparam>
				</dsp:droplet>
				<div class="clearfix padTop_20">
				 <p class="storeResults fl padTop_5">
				 <dsp:droplet name="IsEmpty">
							<dsp:param name="value"
								bean="SearchStoreFormHandler.inputSearchString" />
							<dsp:oparam name="true">
								<dsp:getvalueof var="inputSearchString" param="inputSearchString" />
							</dsp:oparam>
							<dsp:oparam name="false">
								<dsp:getvalueof var="inputSearchString" bean="SearchStoreFormHandler.inputSearchString" />
							</dsp:oparam>
						</dsp:droplet>
				     <jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request" />
					<c:set target="${placeHolderMap}" property="storeCount" value="${itemcount}" />
					<c:set target="${placeHolderMap}" property="inputSearchString" value="${inputSearchString}" />
					<c:set target="${placeHolderMap}" property="storeMiles" value="${miles}" />
					<c:set target="${placeHolderMap}" property="babyCanadaStoreCount" value="${babyCanadaStoreCount}"/>
					<c:set target="${placeHolderMap}" property="bedBathCanadaStoreCount" value="${bedBathCanadaStoreCount}"/>
					<c:set var="countBaby" scope="request" value="${1}"/>
					<c:set var="countBedBath" scope="request" value="${1}"/>
					<c:choose>
					  <c:when test="${siteId ne 'BedBathCanada'}">
					  <c:choose>
						<c:when test="${itemcount gt 1}">
							<bbbt:textArea key="txt_store_multiple_found_msg" placeHolderMap="${placeHolderMap}" language="${pageContext.request.locale.language}" /> ${inputSearchString}
						</c:when>
						<c:otherwise>
							<bbbt:textArea key="txt_store_single_found_msg" placeHolderMap="${placeHolderMap}" language="${pageContext.request.locale.language}" /> ${inputSearchString}
						</c:otherwise>
						</c:choose>
						</c:when>
						<c:otherwise>
							<c:choose>
									<c:when test="${babyCanadaStoreCount eq 0 || babyCanadaStoreCount gt 1 }">
									<c:set var="countBaby" scope="request" value="${0}"/>
									</c:when >
									<c:when test="${bedBathCanadaStoreCount eq 0 || bedBathCanadaStoreCount gt 1}">
									<c:set var="countBedBath" scope="request" value="${0}"/>
									</c:when >	
							</c:choose>
							<bbbt:textArea key="txt_store_canada_${countBaby}_${countBedBath}_found_msg" placeHolderMap="${placeHolderMap}" language="${pageContext.request.locale.language}" />
						</c:otherwise>
					</c:choose>
		
				</p>
				
				<div class="fl padLeft_10">
					<div class="button button_secondary">
						<input type="submit" value="Change location" name="btnChangeLocation" class="showWithResult hidden searchAgainBtn"/>
					</div>
				</div>
				<div class="clear"></div>
				</div>
				<ul class="findStoreResultHeader clearfix">
					<li class="column1"><bbbl:label
									key="lbl_store_header"
									language="${pageContext.request.locale.language}" /></li>
								<li class="column2"><bbbl:label
									key="lbl_store_hour"
									language="${pageContext.request.locale.language}" /></li>
							<!-- 	<li class="column3">&nbsp;</li> -->
								<li class="column3"><bbbl:label
									key="lbl_store_availability"
									language="${pageContext.request.locale.language}" /></li>
								<li class="column4">Reserve</li>
				</ul>
				<div class="findStoreResultWrapper wrapper3 clearfix">
				<div class="wrapper4 viewport clearfix">
					<ul class="findStoreResult overview clearfix  noMarTop">
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array" param="StoreDetailsWrapper.storeDetails" />
							<dsp:param name="elementName" value="StoreDetails" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="itemcount" param="count"
									vartype="java.lang.Integer" />
								<li class="storeInformation"><dsp:include
										page="find_in_store_result_info.jsp">
										<dsp:param name="itemcount" value="${itemcount}" />
										<dsp:param name="StoreDetails" param="StoreDetails" />
										<dsp:param name="vdcPassed" value="${vdcPassed}" />
										<dsp:param name="supplyBalance" value="${supplyBalance}" />
										<dsp:param name="localStorePDP" value="${localStorePDP}" />
									</dsp:include>
								</li>
							</dsp:oparam>
						</dsp:droplet>
					</ul>
				</div>
				</div>
				<%-- <dsp:getvalueof id="currentPage" idtype="java.lang.String"
					param="StoreDetailsWrapper.currentPage" />
				<dsp:getvalueof id="totalPageCount" idtype="java.lang.String"
					param="StoreDetailsWrapper.totalPageCount" /> --%>
					
					<div class="fl marTop_20 bottomMsg">
						<bbbt:textArea key="txt_find_store_footer" language="${pageContext.request.locale.language}" />
					
					<div class="width_3 fl  viewMorePrevLinks">
				<%-- 		<c:if test="${currentPage gt 1}">
							<dsp:getvalueof id="pageKey" param="StoreDetailsWrapper.pageKey" />
							<c:set var="lbl_search_store__previous">	<bbbl:label key="lbl_search_store__previous"
									language="${pageContext.request.locale.language}" /></c:set>
							<dsp:a href="/store/selfservice/find_in_store_result.jsp"
								iclass="marRight_5 changeStoreDataPageLink" title="${lbl_search_store__previous}">
								<dsp:param name="pageKey" value="${pageKey}" />
								<dsp:param name="pageNumber" value="${currentPage-1}" />
								<dsp:param name="productId" value="${param.productId}" />
								<dsp:param name="skuid" value="${param.skuid}" />
								<dsp:param name="orderedQty" value="${param.orderedQty}" />
								<dsp:param name="enableStoreSelection"
									value="${param.enableStoreSelection}" />
								<bbbl:label key="lbl_search_store__previous"
									language="${pageContext.request.locale.language}" />
							</dsp:a>
						</c:if> --%>
						<%-- <c:if test="${currentPage lt totalPageCount}">
							<dsp:getvalueof id="pageKey" param="StoreDetailsWrapper.pageKey" />
							<c:set var="lbl_search_store__next" ><bbbl:label key="lbl_search_store__next"
									language="${pageContext.request.locale.language}" /></c:set>
							<dsp:a href="/store/selfservice/find_in_store_result.jsp"
								iclass="marRight_5 changeStoreDataPageLink" title="${lbl_search_store__next }">
								<dsp:param name="pageKey" value="${pageKey}" />
								<dsp:param name="pageNumber" value="${currentPage+1}" />
								<dsp:param name="productId" value="${param.productId}" />
								<dsp:param name="skuid" value="${param.skuid}" />
								<dsp:param name="orderedQty" value="${param.orderedQty}" />
								<dsp:param name="enableStoreSelection"
									value="${param.enableStoreSelection}" />
								<bbbl:label key="lbl_search_store__next"
									language="${pageContext.request.locale.language}" />
							</dsp:a>
						</c:if> --%>
						<%-- <a
					href="/_ajax/change_store.jsp?storeZip=10007&add2=270+greenwich&storeCity=murray&storeState=FL&selRadiusName=15&productId=80661234&itemQuantity=2&startNum=10"
					class="marRight_5 chnageStoreDataPageLink"><< View Previous</a> <a
					href="/_ajax/change_store.jsp?storeZip=10007&add2=270+greenwich&storeCity=murray&storeState=FL&selRadiusName=15&productId=80661234&itemQuantity=2&startNum=16"
					class="marLeft_20 chnageStoreDataPageLink">View More >></a> --%>
				</div>
			</div>
				</dsp:oparam>
				<dsp:oparam name="empty">
					<c:if test="${favStorePresent != true}" >
						<c:set var="hideclass" value="hidden" />
					</c:if>
					
					<div class="clearfix padTop_20">
					 <p class="storeResults fl padTop_5">
				
						<bbbe:error key="err_find_in_store_no_store"
							language="${pageContext.request.locale.language}" />
							
					</p>
					
					<div class="fl padLeft_10">
						<div class="button button_secondary">
							<input type="submit" value="Change location" name="btnChangeLocation" class="showWithResult hidden searchAgainBtn"/>
						</div>
					</div>
					<div class="clear"></div>
					</div>
				</dsp:oparam>
				<dsp:oparam name="error">
				 <c:if test="${favStorePresent != true}" >
					<c:set var="hideclass" value="hidden" />
				 </c:if>
				 <div class="error marTop_20">
				
				 
					<bbbe:error key="err_find_in_store_exception"
						language="${pageContext.request.locale.language}" />
						
						</div>
				</dsp:oparam>								
								    </dsp:droplet>
<%-- 					</c:if>
 --%>				
		
		<%-- <dsp:droplet name="SearchInStoreDroplet">
			<dsp:param name="searchBasedOn" value="cookie" />
			<dsp:param name="storeId" value="" />
			<dsp:param name="productId" value="${param.skuid}" />
			<dsp:param name="orderedQty" value="${param.itemQuantity}" />
            <dsp:param name="siteId" bean="/atg/multisite/SiteContext.site.id"/>
			<dsp:droplet name="/atg/dynamo/droplet/Switch">
				<dsp:param name="value" param="pageKey" />
				<dsp:oparam name="true">
					<dsp:param name="pageKey" param="pageKey" />
					<dsp:param name="pageNumber" param="pageNumber" />
				</dsp:oparam>
			</dsp:droplet>
			<dsp:oparam name="addressSuggestion">
					<c:set var="foundAddrSuggestion" value="${true}" />
					<div class="grid_5 alpha multipleAddFound">
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
									<li class="clearfix"><span> <dsp:getvalueof var="street" param="storeAddressSuggestion.street"/>
												<c:if test="${not empty street}">
													<dsp:valueof param="storeAddressSuggestion.street" />,
												</c:if>
												<dsp:valueof
												param="storeAddressSuggestion.address" />, <dsp:valueof
												param="storeAddressSuggestion.city" />, <dsp:valueof
												param="storeAddressSuggestion.stateCode" />
									</span> multiple_address.jsp will call the handleSearchStore method of SearchStoreFormHandler with suggested Address.
									<dsp:setvalue bean="SearchStoreFormHandler.siteContext" beanvalue="/atg/multisite/Site.id" />
										<dsp:setvalue
											bean="SearchStoreFormHandler.searchStoreSuccessURL"
											value="${contextPath}/selfservice/find_in_store_result.jsp" /> 
											<dsp:setvalue	bean="SearchStoreFormHandler.searchStoreErrorURL"value="${contextPath}/selfservice/find_in_store_result.jsp" />
											<c:set var="lbl_find_store_use_address"><bbbl:label key="lbl_find_store_use_address"
												language="${pageContext.request.locale.language}" /></c:set> 
											<dsp:a href="${contextPath}/selfservice/store/multiple_address.jsp" title="${lbl_find_store_use_address}"	iclass="changeStoreDataPageLink">
											<bbbl:label key="lbl_find_store_use_address"
												language="${pageContext.request.locale.language}" />
											<dsp:param name="address"
												param="storeAddressSuggestion.address" />
											<dsp:param name="city" param="storeAddressSuggestion.city" />
											<dsp:param name="stateCode"
												param="storeAddressSuggestion.stateCode" />
											</dsp:a></li>
								</dsp:oparam>
							</dsp:droplet>
						</ul>
					</div>
				</dsp:oparam>
			<dsp:oparam name="output">
			<dsp:getvalueof var="babyCanadaFlag" param="StoreDetailsWrapper.babyCanadaFlag"/>
			<dsp:getvalueof var="babyCanadaStoreCount" param="StoreDetailsWrapper.babyCanadaStoreCount"/>
			<dsp:getvalueof var="bedBathCanadaStoreCount" param="StoreDetailsWrapper.bedBathCanadaStoreCount"/>
				<dsp:droplet name="/atg/dynamo/droplet/ForEach">
					<dsp:param name="array" param="StoreDetailsWrapper.storeDetails" />
					<dsp:param name="elementName" value="StoreDetails" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="itemcount" param="count"
							vartype="java.lang.Integer" />
					</dsp:oparam>
				</dsp:droplet>
				<div class="clearfix padTop_20">
				 <p class="storeResults fl padTop_5">
				 <dsp:droplet name="IsEmpty">
							<dsp:param name="value"
								bean="SearchStoreFormHandler.inputSearchString" />
							<dsp:oparam name="true">
								<dsp:getvalueof var="inputSearchString" param="inputSearchString" />
							</dsp:oparam>
							<dsp:oparam name="false">
								<dsp:getvalueof var="inputSearchString" bean="SearchStoreFormHandler.inputSearchString" />
							</dsp:oparam>
						</dsp:droplet>
				     <jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request" />
					<c:set target="${placeHolderMap}" property="storeCount" value="${itemcount}" />
					<c:set target="${placeHolderMap}" property="inputSearchString" value="${inputSearchString}" />
					<c:set target="${placeHolderMap}" property="storeMiles" value="${miles}" />
					<c:set target="${placeHolderMap}" property="babyCanadaStoreCount" value="${babyCanadaStoreCount}"/>
					<c:set target="${placeHolderMap}" property="bedBathCanadaStoreCount" value="${bedBathCanadaStoreCount}"/>
					<c:set var="countBaby" scope="request" value="${1}"/>
					<c:set var="countBedBath" scope="request" value="${1}"/>
					<c:choose>
					  <c:when test="${siteId ne 'BedBathCanada'}">
					  <c:choose>
						<c:when test="${itemcount gt 1}">
							<bbbt:textArea key="txt_store_multiple_found_msg" placeHolderMap="${placeHolderMap}" language="${pageContext.request.locale.language}" /> ${inputSearchString}
						</c:when>
						<c:otherwise>
							<bbbt:textArea key="txt_store_single_found_msg" placeHolderMap="${placeHolderMap}" language="${pageContext.request.locale.language}" /> ${inputSearchString}
						</c:otherwise>
						</c:choose>
						</c:when>
						<c:otherwise>
							<c:choose>
									<c:when test="${babyCanadaStoreCount eq 0 || babyCanadaStoreCount gt 1 }">
									<c:set var="countBaby" scope="request" value="${0}"/>
									</c:when >
									<c:when test="${bedBathCanadaStoreCount eq 0 || bedBathCanadaStoreCount gt 1}">
									<c:set var="countBedBath" scope="request" value="${0}"/>
									</c:when >	
							</c:choose>
							<bbbt:textArea key="txt_store_canada_${countBaby}_${countBedBath}_found_msg" placeHolderMap="${placeHolderMap}" language="${pageContext.request.locale.language}" />
						</c:otherwise>
					</c:choose>
		
				</p>
				
				<div class="fl padLeft_10">
					<div class="button button_secondary">
						<input type="submit" value="Change location" name="btnChangeLocation" class="showWithResult hidden searchAgainBtn"/>
					</div>
				</div>
				<div class="clear"></div>
				</div>
				<ul class="findStoreResultHeader clearfix">
					<li class="column1"><bbbl:label
									key="lbl_store_header"
									language="${pageContext.request.locale.language}" /></li>
								<li class="column2"><bbbl:label
									key="lbl_store_hour"
									language="${pageContext.request.locale.language}" /></li>
							<!-- 	<li class="column3">&nbsp;</li> -->
								<li class="column3"><bbbl:label
									key="lbl_store_availability"
									language="${pageContext.request.locale.language}" /></li>
								<li class="column4">Reserve</li>
				</ul>
				<div class="findStoreResultWrapper wrapper3 clearfix">
				<div class="wrapper4 viewport clearfix">
					<ul class="findStoreResult overview clearfix  noMarTop">
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array" param="StoreDetailsWrapper.storeDetails" />
							<dsp:param name="elementName" value="StoreDetails" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="itemcount" param="count"
									vartype="java.lang.Integer" />
								<li class="storeInformation"><dsp:include
										page="find_in_store_result_info.jsp">
										<dsp:param name="itemcount" value="${itemcount}" />
										<dsp:param name="StoreDetails" param="StoreDetails" />
									</dsp:include>
								</li>
							</dsp:oparam>
						</dsp:droplet>
					</ul>
				</div>
				</div>
				<dsp:getvalueof id="currentPage" idtype="java.lang.String"
					param="StoreDetailsWrapper.currentPage" />
				<dsp:getvalueof id="totalPageCount" idtype="java.lang.String"
					param="StoreDetailsWrapper.totalPageCount" />
					
					<div class="fl marTop_20 bottomMsg">
						<bbbt:textArea key="txt_find_store_footer" language="${pageContext.request.locale.language}" />
					
					<div class="width_3 fl  viewMorePrevLinks">
						<c:if test="${currentPage gt 1}">
							<dsp:getvalueof id="pageKey" param="StoreDetailsWrapper.pageKey" />
							<c:set var="lbl_search_store__previous">	<bbbl:label key="lbl_search_store__previous"
									language="${pageContext.request.locale.language}" /></c:set>
							<dsp:a href="/store/selfservice/find_in_store_result.jsp"
								iclass="marRight_5 changeStoreDataPageLink" title="${lbl_search_store__previous}">
								<dsp:param name="pageKey" value="${pageKey}" />
								<dsp:param name="pageNumber" value="${currentPage-1}" />
								<dsp:param name="productId" value="${param.productId}" />
								<dsp:param name="skuid" value="${param.skuid}" />
								<dsp:param name="orderedQty" value="${param.orderedQty}" />
								<dsp:param name="enableStoreSelection"
									value="${param.enableStoreSelection}" />
								<bbbl:label key="lbl_search_store__previous"
									language="${pageContext.request.locale.language}" />
							</dsp:a>
						</c:if>
						<c:if test="${currentPage lt totalPageCount}">
							<dsp:getvalueof id="pageKey" param="StoreDetailsWrapper.pageKey" />
							<c:set var="lbl_search_store__next" ><bbbl:label key="lbl_search_store__next"
									language="${pageContext.request.locale.language}" /></c:set>
							<dsp:a href="/store/selfservice/find_in_store_result.jsp"
								iclass="marRight_5 changeStoreDataPageLink" title="${lbl_search_store__next }">
								<dsp:param name="pageKey" value="${pageKey}" />
								<dsp:param name="pageNumber" value="${currentPage+1}" />
								<dsp:param name="productId" value="${param.productId}" />
								<dsp:param name="skuid" value="${param.skuid}" />
								<dsp:param name="orderedQty" value="${param.orderedQty}" />
								<dsp:param name="enableStoreSelection"
									value="${param.enableStoreSelection}" />
								<bbbl:label key="lbl_search_store__next"
									language="${pageContext.request.locale.language}" />
							</dsp:a>
						</c:if>
						<a
					href="/_ajax/change_store.jsp?storeZip=10007&add2=270+greenwich&storeCity=murray&storeState=FL&selRadiusName=15&productId=80661234&itemQuantity=2&startNum=10"
					class="marRight_5 chnageStoreDataPageLink"><< View Previous</a> <a
					href="/_ajax/change_store.jsp?storeZip=10007&add2=270+greenwich&storeCity=murray&storeState=FL&selRadiusName=15&productId=80661234&itemQuantity=2&startNum=16"
					class="marLeft_20 chnageStoreDataPageLink">View More >></a>
				</div>
			</div>
				</dsp:oparam>
				<dsp:oparam name="empty">
					<c:if test="${favStorePresent != true}" >
						<c:set var="hideclass" value="hidden" />
					</c:if>
					
					<div class="clearfix padTop_20">
					 <p class="storeResults fl padTop_5">
				
						<bbbe:error key="err_find_in_store_no_store"
							language="${pageContext.request.locale.language}" />
							
					</p>
					
					<div class="fl padLeft_10">
						<div class="button button_secondary">
							<input type="submit" value="Change location" name="btnChangeLocation" class="showWithResult hidden searchAgainBtn"/>
						</div>
					</div>
					<div class="clear"></div>
					</div>
				</dsp:oparam>
				<dsp:oparam name="error">
				 <c:if test="${favStorePresent != true}" >
					<c:set var="hideclass" value="hidden" />
				 </c:if>
				 <div class="error marTop_20">
				
				 
					<bbbe:error key="err_find_in_store_exception"
						language="${pageContext.request.locale.language}" />
						
						</div>
				</dsp:oparam>
			</dsp:droplet> --%>
		</form>
	</div>
	
	<dsp:getvalueof var="errorList" bean="SystemErrorInfo.errorList"/>
		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
			<dsp:param bean="SystemErrorInfo.errorList" name="array" />
			<dsp:param name="elementName" value="ErrorInfoVO" />
			<dsp:oparam name="outputStart"><div id="error" class="hidden"><ul></dsp:oparam>
			<dsp:oparam name="output">
				<li id="tl_atg_err_code"><dsp:valueof param="ErrorInfoVO.errorCode"/></li>
				<li id="tl_atg_err_value"><dsp:valueof param="ErrorInfoVO.errorDescription"/></li>
			</dsp:oparam>
			<dsp:oparam name="outputEnd"></ul></div></dsp:oparam>
		</dsp:droplet>
	    <script type="text/javascript">
		 // rkg micro pixel
			var appid = '${currentSiteId}';
			var type = 'findstore';
			$(function () {
				rkg_micropixel(appid,type);
			});
		</script>
	</dsp:page>