<dsp:page>
	<bbb:pageContainer pageWrapper="myAccount favoriteStore useMapQuest useStoreLocator" section="accounts" index="false" follow="false">
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="com/bbb/selfservice/SearchStoreFormHandler" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<c:set var="BedBathCanadaSite">
		<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
		<dsp:param name="array" bean="Profile.userSiteItems"/>
		<dsp:param name="elementName" value="sites"/>
		<dsp:oparam name="output">
			<dsp:getvalueof id="key" param="key"/>
			<c:if test="${currentSiteId eq key}">
				<dsp:getvalueof id="favouriteStoreId" param="sites.favouriteStoreId" scope="session"/>
			</c:if>
		</dsp:oparam>
	</dsp:droplet>
	<div id="content" class="container_12 clearfix" role="main">
		<div class="grid_12">
			<h1 class="account fl"><bbbl:label key="lbl_favroitestore_my_account" language="${pageContext.request.locale.language}"/></h1>
			<h3 class="subtitle"><bbbl:label key="lbl_favroitestore_fav_store" language="${pageContext.request.locale.language}"/></h3>
            <div class="clear"></div>
		</div>

		<div class="grid_2">
			
			 <c:import url="/account/left_nav.jsp">
             <c:param name="currentPage"><bbbl:label key="lbl_myaccount_favorite_stores" language ="${pageContext.request.locale.language}"/></c:param>
            </c:import>
			
		</div>
		<div class="grid_9 prefix_1">
			<div class="alpha fsBox">
				<div id="myFavoriteStore" class="section clearfix myFavoriteStore">
					<dsp:getvalueof id="currUrl" value="${pageContext.request.requestURI}" scope="session"/>
					<c:if test="${!empty favouriteStoreId}">
						<dsp:droplet name="/com/bbb/selfservice/SearchStoreDroplet">
							<dsp:param name="storeId" value="${favouriteStoreId}"/>
							<dsp:param name="searchType" value="2"/>
							<dsp:oparam name="output">
								<div class="spacer clearfix location">
									<h2 class="noMar"><bbbl:label key="lbl_favroitestore_fav_store_3" language="${pageContext.request.locale.language}"/></h2>
									<div class="item clearfix marTop_10">
										<div class="location">
											<h3 class="bold"><span class="storeTitle"><dsp:valueof param="StoreDetails.storeName"/></span></h3>
											<div class="address">
												<div class="street"><dsp:valueof param="StoreDetails.address"/></div>
												<div>
													<span class="city"><dsp:valueof param="StoreDetails.city"/>,</span>
													<span class="state"><dsp:valueof param="StoreDetails.state"/></span>
													<span class="zip"><dsp:valueof param="StoreDetails.postalCode"/></span>
												</div>
												<div class="phone"><dsp:valueof param="StoreDetails.storePhone"/></div>
											</div>
										</div>
										<dsp:getvalueof var="StoreDetailsVar" param="StoreDetails" scope="page"/>
										<div class="timings">
											<p>
												<c:forTokens items="${StoreDetailsVar.weekdaysStoreTimings}" delims="," var="item">
													${item}
												</c:forTokens><br/>
												<c:forTokens items="${StoreDetailsVar.satStoreTimings}" delims="," var="item">
													${item}
												</c:forTokens><br/>
												<c:forTokens items="${StoreDetailsVar.sunStoreTimings}" delims="," var="item">
													${item}
												</c:forTokens><br/>
												<c:forTokens items="${StoreDetailsVar.otherTimings1}" delims="," var="item">
													${item}
												</c:forTokens><br/>
												<c:forTokens items="${StoreDetailsVar.otherTimings2}" delims="," var="item">
													${item}
												</c:forTokens><br/>
												<dsp:valueof param="StoreDetails.storeDescription" />
											</p>
										</div>
									</div>
									<div class="item clearfix">
										<ul class="storeInfo">
											<li>
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
										  					<div class="benefitsItem"><img src="${imagePath}${image}" alt="${storeListAltText}" /></div>
										  				</dsp:oparam>	
										  			</dsp:droplet>
										  		</dsp:oparam>
										  	</dsp:droplet>
										  	</li>
										</ul>
									</div>
									<p class="message"><bbbl:label key="lbl_favroitestore_info" language="${pageContext.request.locale.language}"/></p>
									<c:set var="chngstore">
										<bbbl:label key="lbl_favroitestore_chng_store" language="${pageContext.request.locale.language}"/>
									</c:set>
									<c:choose>
										<c:when test="${!(currentSiteId eq BedBathCanadaSite)}">
											<dsp:a href="${contextPath}/selfservice/store/find_store.jsp" title="${chngstore}" iclass="itemLink recognizedUser"><bbbl:label key="lbl_favroitestore_chng_store" language="${pageContext.request.locale.language}"/></dsp:a>
										</c:when>
										<c:otherwise>
											<dsp:a href="${contextPath}/selfservice/CanadaStoreLocator" title="${chngstore}" iclass="itemLink"><bbbl:label key="lbl_favroitestore_chng_store" language="${pageContext.request.locale.language}"/></dsp:a>
										</c:otherwise>
									</c:choose>
									
									<%--
									<c:set var="seemap">
										<bbbl:label key="lbl_favroitestore_see_map" language="${pageContext.request.locale.language}"/>
									</c:set>
									<a class="itemLink cb viewMapModal" title="${seemap}" href="#viewMap"><bbbl:label key="lbl_favroitestore_see_map" language="${pageContext.request.locale.language}"/></a>
									
									<c:set var="addinfo">
										<bbbl:label key="lbl_favroitestore_add_info" language="${pageContext.request.locale.language}"/>
									</c:set>
									<dsp:getvalueof id="zip" param="StoreDetails.postalCode"/>
									<dsp:a href="${contextPath}/selfservice/FindStore/?zip=${zip}" title="${addinfo}" iclass="itemLink cb"><c:out value="${addinfo}"/></dsp:a>
									--%>
									<c:set var="Remove">
										<bbbl:label key="lbl_favroitestore_remove" language="${pageContext.request.locale.language}"/>
									</c:set>
									<dsp:a href="${contextPath}/account/favoritestore.jsp?favouriteStoreId=" title="${Remove}" iclass="itemLink cb recognizedUser">
										<bbbl:label key="lbl_favroitestore_remove" language="${pageContext.request.locale.language}"/>
										<dsp:property bean="SearchStoreFormHandler.modifyFavStore" value=""/>
									</dsp:a>
								</div>
							</dsp:oparam>
							<dsp:oparam name="empty">
							<h2 class="noMar"><bbbl:label key="lbl_favroitestore_fav_store_3" language="${pageContext.request.locale.language}"/></h2>
							<bbbl:label key="lbl_favoritestore_disablemessage" language="${pageContext.request.locale.language}"/></br>
							<c:choose>
								<c:when test="${!(currentSiteId eq BedBathCanadaSite)}">
									<a href="${contextPath}/selfservice/store/find_store.jsp" title="Change Store" class="itemLink recognizedUser" title="${change}"><bbbl:label key="lbl_favstorewidget_chng_store" language="${pageContext.request.locale.language}" /></a>
								</c:when>
								<c:otherwise>
									<a href="${contextPath}/selfservice/CanadaStoreLocator" title="Change Store" class="itemLink" title="${change}"><bbbl:label key="lbl_favstorewidget_chng_store" language="${pageContext.request.locale.language}" /></a>
								</c:otherwise>
							</c:choose>
				            </dsp:oparam>
						</dsp:droplet>
					</c:if>
					<c:if test="${empty favouriteStoreId}">
						<div class="spacer clearfix">
							<h2 class="noMar"><bbbl:label key="lbl_favstorewidget_favorite_store" language="${pageContext.request.locale.language}" /></h2>
							<p><bbbl:label key="lbl_favstorewidget_info" language="${pageContext.request.locale.language}" /></p>
							<div class="button">
							<c:choose>
								<c:when test="${!(currentSiteId eq BedBathCanadaSite)}">
									<a href="${contextPath}/selfservice/store/find_store.jsp" class="recognizedUser"><bbbl:label key="lbl_favstorewidget_select_fav" language="${pageContext.request.locale.language}" /></a>
								</c:when>
								<c:otherwise>
									<a href="${contextPath}/selfservice/CanadaStoreLocator"><bbbl:label key="lbl_favstorewidget_select_fav" language="${pageContext.request.locale.language}" /></a>
								</c:otherwise>
							</c:choose>
							</div>
						</div>
					</c:if>
				</div>
			</div>
		</div>
	</div>
	
	<dsp:include page="/selfservice/find_in_store.jsp"/>
    <%--  New version of view map/get directions --%>
    <c:import url="/selfservice/store/find_store_pdp.jsp" ></c:import>
	<jsp:attribute name="footerContent">
	<script type="text/javascript">
		if (typeof s !== 'undefined') {
		s.pageName ='My Account>Favorite Store';
		s.channel = 'My Account';
		s.prop1 = 'My Account';
		s.prop2 = 'My Account';
		s.prop3 = 'My Account';
		s.prop6='${pageContext.request.serverName}'; 
		s.eVar9='${pageContext.request.serverName}';
		var s_code = s.t();
		if (s_code)
			document.write(s_code);
		}
	</script>	
	</jsp:attribute>					
	</bbb:pageContainer>
</dsp:page>
