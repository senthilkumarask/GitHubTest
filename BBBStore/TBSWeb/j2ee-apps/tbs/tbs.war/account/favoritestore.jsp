<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/com/bbb/selfservice/SearchStoreFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/com/bbb/selfservice/SearchStoreDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/RQLQueryForEach"/>

	<%-- Variables --%>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>

	<dsp:droplet name="ForEach">
		<dsp:param name="array" bean="Profile.userSiteItems"/>
		<dsp:param name="elementName" value="sites"/>
		<dsp:oparam name="output">
			<dsp:getvalueof id="key" param="key"/>
			<c:if test="${currentSiteId eq key}">
				<dsp:getvalueof id="favouriteStoreId" param="sites.favouriteStoreId" scope="session"/>
			</c:if>
		</dsp:oparam>
	</dsp:droplet>

	<bbb:pageContainer pageWrapper="myAccount favoriteStore useMapQuest useStoreLocator" bodyClass="favorite-store" section="accounts" index="false" follow="false">

		<div class="row" id="content">
			<div class="small-12 columns">
				<h1><bbbl:label key="lbl_favroitestore_my_account" language="${pageContext.request.locale.language}"/>: <span class="subheader"><bbbl:label key="lbl_favroitestore_fav_store" language="${pageContext.request.locale.language}"/></span></h1>
			</div>
			<div class="show-for-medium-down small-12">
				<a class="right-off-canvas-toggle secondary expand button">Account Menu</a>
			</div>
			<div class="large-3 columns small-medium-right-off-canvas-menu left-nav">
				<c:import url="/account/left_nav.jsp">
					<c:param name="currentPage">
						<bbbl:label key="lbl_myaccount_favorite_stores" language ="${pageContext.request.locale.language}"/>
					</c:param>
				</c:import>
			</div>
			<div class="small-12 large-9 columns">
				<dsp:getvalueof id="currUrl" value="${pageContext.request.requestURI}" scope="session"/>
				<c:if test="${!empty favouriteStoreId}">
					<dsp:droplet name="SearchStoreDroplet">
						<dsp:param name="storeId" value="${favouriteStoreId}"/>
						<dsp:param name="searchType" value="2"/>
						<dsp:oparam name="output">
							<h2><bbbl:label key="lbl_favroitestore_fav_store_3" language="${pageContext.request.locale.language}"/></h2>
							<h3><dsp:valueof param="StoreDetails.storeName"/></h3>
							<ul class="address">
								<li><dsp:valueof param="StoreDetails.address"/></li>
								<li><dsp:valueof param="StoreDetails.city"/>, <dsp:valueof param="StoreDetails.state"/> <dsp:valueof param="StoreDetails.postalCode"/></li>
								<li><dsp:valueof param="StoreDetails.storePhone"/></li>
							</ul>

							<dsp:getvalueof var="StoreDetailsVar" param="StoreDetails" scope="page"/>
							<ul class="address">
								<li>
									<c:forTokens items="${StoreDetailsVar.weekdaysStoreTimings}" delims="," var="item">
										${item}
									</c:forTokens>
								</li>
								<li>
									<c:forTokens items="${StoreDetailsVar.satStoreTimings}" delims="," var="item">
										${item}
									</c:forTokens>
								</li>
								<li>
									<c:forTokens items="${StoreDetailsVar.sunStoreTimings}" delims="," var="item">
										${item}
									</c:forTokens>
								</li>
								<li>
									<c:forTokens items="${StoreDetailsVar.otherTimings1}" delims="," var="item">
										${item}
									</c:forTokens>
								</li>
								<li>
									<c:forTokens items="${StoreDetailsVar.otherTimings2}" delims="," var="item">
										${item}
									</c:forTokens>
								</li>
							</ul>
							<ul class="address">
								<li><dsp:valueof param="StoreDetails.storeDescription" /></li>
							</ul>
							<ul class="address">
								<dsp:getvalueof param="StoreDetails.specialtyShopsCd" id="code"/>
								<dsp:droplet name="RQLQueryForEach">
									<dsp:param name="code" value="${code}"/>
									<dsp:param name="queryRQL" value="specialityShopCd=:code"/>
									<dsp:param name="repository" value="/com/bbb/selfservice/repository/StoreRepository"/>
									<dsp:param name="itemDescriptor" value="specialityCodeMap"/>
									<dsp:param name="elementName" value="item"/>
									<dsp:oparam name="output">
										<dsp:droplet name="ForEach">
											<dsp:param name="array" param="item.specialityCd"/>
											<dsp:param name="elementName" value="list"/>
											<dsp:oparam name="output">
												<dsp:getvalueof param="list.codeImage" id="image"/>
												<dsp:getvalueof param="list.storeListTitleText" id="storeListTitleText"/>
												<dsp:getvalueof param="list.storeListAltText" id="storeListAltText"/>
												<li><img src="${imagePath}${image}" alt="${storeListAltText}" /></li>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
							</ul>
							<p class="p-secondary">
								<bbbl:label key="lbl_favroitestore_info" language="${pageContext.request.locale.language}"/>
							</p>
							<c:set var="chngstore">
								<bbbl:label key="lbl_favroitestore_chng_store" language="${pageContext.request.locale.language}"/>
							</c:set>
							<c:choose>
								<c:when test="${!(currentSiteId eq TBS_BedBathCanadaSite)}">
									<dsp:a href="${contextPath}/selfservice/store/find_store.jsp" title="${chngstore}" iclass="itemLink small button primary"><bbbl:label key="lbl_favroitestore_chng_store" language="${pageContext.request.locale.language}"/></dsp:a>
								</c:when>
								<c:otherwise>
									<dsp:a href="${contextPath}/selfservice/CanadaStoreLocator" title="${chngstore}" iclass="itemLink small button primary"><bbbl:label key="lbl_favroitestore_chng_store" language="${pageContext.request.locale.language}"/></dsp:a>
								</c:otherwise>
							</c:choose>
							<c:set var="Remove">
								<bbbl:label key="lbl_favroitestore_remove" language="${pageContext.request.locale.language}"/>
							</c:set>
							<dsp:a href="${contextPath}/account/favoritestore.jsp?favouriteStoreId=" title="${Remove}" iclass="small button secondary">
								<bbbl:label key="lbl_favroitestore_remove" language="${pageContext.request.locale.language}"/>
								<dsp:property bean="SearchStoreFormHandler.modifyFavStore" value=""/>
							</dsp:a>
						</dsp:oparam>
						<dsp:oparam name="empty">
							<h2><bbbl:label key="lbl_favroitestore_fav_store_3" language="${pageContext.request.locale.language}"/></h2>
							<bbbl:label key="lbl_favoritestore_disablemessage" language="${pageContext.request.locale.language}"/></br>
							<a href="${contextPath}/selfservice/store/find_store.jsp" title="Change Store" class="small button primary" title="${change}"><bbbl:label key="lbl_favstorewidget_chng_store" language="${pageContext.request.locale.language}" /></a>
						</dsp:oparam>
					</dsp:droplet>
				</c:if>
				<c:if test="${empty favouriteStoreId}">
					<h2><bbbl:label key="lbl_favstorewidget_favorite_store" language="${pageContext.request.locale.language}" /></h2>
					<p><bbbl:label key="lbl_favstorewidget_info" language="${pageContext.request.locale.language}" /></p>
					<c:choose>
						<c:when test="${!(currentSiteId eq TBS_BedBathCanadaSite)}">
							<a href="${contextPath}/selfservice/store/find_store.jsp" class="small button primary"><bbbl:label key="lbl_favstorewidget_select_fav" language="${pageContext.request.locale.language}" /></a>
						</c:when>
						<c:otherwise>
							<dsp:a href="${contextPath}/selfservice/CanadaStoreLocator" iclass="small button primary"><bbbl:label key="lbl_favstorewidget_select_fav" language="${pageContext.request.locale.language}"/></dsp:a>
						</c:otherwise>
					</c:choose>
				</c:if>				
				<c:import url="/selfservice/store/find_store_pdp.jsp" ></c:import>

			</div>
		</div>

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
