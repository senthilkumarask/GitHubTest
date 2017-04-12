<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/com/bbb/selfservice/SearchStoreFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/com/bbb/selfservice/SearchStoreDroplet"/>
	
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

		<%-- only show when user does not have a favorite store --%>
		<c:if test="${empty favouriteStoreId}">
			<h2 class="noMarTop"><bbbl:label key="lbl_favstorewidget_favorite_store" language="${pageContext.request.locale.language}" /></h2>
			<p><bbbl:label key="lbl_favstorewidget_info" language="${pageContext.request.locale.language}" /></p>
			<c:set var="selectfavStore">
				<bbbl:label key="lbl_favstorewidget_select_fav" language="${pageContext.request.locale.language}" />
			</c:set>
			<dsp:a href="${contextPath}/selfservice/store/find_store.jsp" iclass="small button primary" name="btnFindInStore"><bbbl:label key="lbl_favstorewidget_select_fav" language="${pageContext.request.locale.language}" /></dsp:a>
		</c:if>
		<c:if test="${!empty favouriteStoreId}">
			<h2 class="noMarTop"><bbbl:label key="lbl_favstorewidget_fav_store_2" language="${pageContext.request.locale.language}" /></h2>
			<dsp:droplet name="SearchStoreDroplet">
				<dsp:param name="storeId" value="${favouriteStoreId}"/>
				<dsp:param name="searchType" value="2"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="StoreDetailsVar" param="StoreDetails" scope="page"/>
					<h3><dsp:valueof param="StoreDetails.storeName"/></h3>
					<ul class="address">
						<li><dsp:valueof param="StoreDetails.address"/></li>
						<li><dsp:valueof param="StoreDetails.city"/>, <dsp:valueof param="StoreDetails.state"/> <dsp:valueof param="StoreDetails.postalCode"/></li>
						<li><dsp:valueof param="StoreDetails.storePhone"/></li>
					</ul>
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
						<li>
							<dsp:valueof param="StoreDetails.storeDescription"/>
						</li>
					</ul>
					<c:set var="remove">
						<bbbl:label key="lbl_favstorewidget_remove" language="${pageContext.request.locale.language}" />
					</c:set>
					<c:set var="change">
						<bbbl:label key="lbl_favstorewidget_chng_store" language="${pageContext.request.locale.language}" />
					</c:set>
					<a href="${contextPath}/selfservice/store/find_store.jsp" title="Change Store" class="itemLink" title="${change}"><bbbl:label key="lbl_favstorewidget_chng_store" language="${pageContext.request.locale.language}" /></a>
					<dsp:a iclass="itemLink right" title="${remove}"href="${contextPath}/account/myaccount.jsp?favouriteStoreId=">
						<bbbl:label key="lbl_favstorewidget_remove" language="${pageContext.request.locale.language}" />
						<dsp:property bean="SearchStoreFormHandler.modifyFavStore" value=""/>
					</dsp:a>
				</dsp:oparam>
				<dsp:oparam name="empty">
					<bbbl:label key="lbl_favoritestore_disablemessage" language="${pageContext.request.locale.language}"/>
					<a href="${contextPath}/selfservice/store/find_store.jsp" title="Change Store" class="itemLink" title="${change}"><bbbl:label key="lbl_favstorewidget_chng_store" language="${pageContext.request.locale.language}" /></a>
				</dsp:oparam>
			</dsp:droplet>
		</c:if>

</dsp:page>
