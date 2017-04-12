<dsp:page>	
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
	<div id="myFavoriteStore" class="section clearfix myFavoriteStore">
		<%-- only show when user does not have a favorite store --%>
		<c:if test="${empty favouriteStoreId}">
			<div class="spacer clearfix">
				<h2 class="noMarTop"><bbbl:label key="lbl_favstorewidget_favorite_store" language="${pageContext.request.locale.language}" /></h2>
				<p><bbbl:label key="lbl_favstorewidget_info" language="${pageContext.request.locale.language}" /></p>
				<c:set var="selectfavStore">
					<bbbl:label key="lbl_favstorewidget_select_fav" language="${pageContext.request.locale.language}" />
				</c:set>
				<div class="button">
				<c:choose>
					<c:when test="${!(currentSiteId eq BedBathCanadaSite)}">
						<dsp:a href="${contextPath}/selfservice/store/find_store.jsp" name="btnFindInStore"><bbbl:label key="lbl_favstorewidget_select_fav" language="${pageContext.request.locale.language}" /></dsp:a>
					</c:when>
					<c:otherwise>
						<dsp:a href="${contextPath}/selfservice/CanadaStoreLocator" name="btnFindInStore"><bbbl:label key="lbl_favstorewidget_select_fav" language="${pageContext.request.locale.language}" /></dsp:a>
					</c:otherwise>
				</c:choose>
				</div>
			</div>
		</c:if>		
		<c:if test="${!empty favouriteStoreId}">
		<div class="spacer clearfix">
						<h2 class="noMarTop"><bbbl:label key="lbl_favstorewidget_fav_store_2" language="${pageContext.request.locale.language}" /></h2>
			<dsp:droplet name="/com/bbb/selfservice/SearchStoreDroplet">
				<dsp:param name="storeId" value="${favouriteStoreId}"/>
				<dsp:param name="searchType" value="2"/>
				<dsp:oparam name="output">
					
						<div class="item">
							<div class="location">
								<h3 class="bold"><dsp:valueof param="StoreDetails.storeName"/></h3>
								<div class="address">
									<div class="street"><dsp:valueof param="StoreDetails.address"/></div>
									<div>
										<span class="city"><dsp:valueof param="StoreDetails.city"/></span>,
										<span class="state"><dsp:valueof param="StoreDetails.state"/></span>
										<span class="zip"><dsp:valueof param="StoreDetails.postalCode"/></span>
									</div>
									<div class="phone"><dsp:valueof param="StoreDetails.storePhone"/></div>
								</div>
							</div>
							<dsp:getvalueof var="StoreDetailsVar" param="StoreDetails" scope="page"/>	
							<div class="timings">
								<c:forTokens items="${StoreDetailsVar.weekdaysStoreTimings}" delims="," var="item">
									${item}
								</c:forTokens>
								<br/>
								<c:forTokens items="${StoreDetailsVar.satStoreTimings}" delims="," var="item">
									${item}
								</c:forTokens>
								<br/>
								<c:forTokens items="${StoreDetailsVar.sunStoreTimings}" delims="," var="item">
									${item}
								</c:forTokens><br/>
								<c:forTokens items="${StoreDetailsVar.otherTimings1}" delims="," var="item">
									${item}
								</c:forTokens><br/>
								<c:forTokens items="${StoreDetailsVar.otherTimings2}" delims="," var="item">
									${item}
								</c:forTokens><br/>
								<dsp:valueof param="StoreDetails.storeDescription"/>
							</div>
						</div>
						<c:set var="remove">
					<bbbl:label key="lbl_favstorewidget_remove" language="${pageContext.request.locale.language}" />
				</c:set><c:set var="change">
					<bbbl:label key="lbl_favstorewidget_chng_store" language="${pageContext.request.locale.language}" />
				</c:set>
				<c:choose>
					<c:when test="${!(currentSiteId eq BedBathCanadaSite)}">
						<a href="${contextPath}/selfservice/store/find_store.jsp" title="Change Store" class="itemLink" title="${change}"><bbbl:label key="lbl_favstorewidget_chng_store" language="${pageContext.request.locale.language}" /></a>
					</c:when>
					<c:otherwise>
						<a href="${contextPath}/selfservice/CanadaStoreLocator" title="Change Store" class="itemLink" title="${change}"><bbbl:label key="lbl_favstorewidget_chng_store" language="${pageContext.request.locale.language}" /></a>
					</c:otherwise>
				</c:choose>
				
				<dsp:a iclass="itemLink cb" title="${remove}"href="${contextPath}/account/myaccount.jsp?favouriteStoreId=">
					<bbbl:label key="lbl_favstorewidget_remove" language="${pageContext.request.locale.language}" />
					<dsp:property bean="SearchStoreFormHandler.modifyFavStore" value=""/>
				</dsp:a>
					
				</dsp:oparam>
				<dsp:oparam name="empty">
				<bbbl:label key="lbl_favoritestore_disablemessage" language="${pageContext.request.locale.language}"/>
				<c:choose>
					<c:when test="${!(currentSiteId eq BedBathCanadaSite)}">
						<a href="${contextPath}/selfservice/store/find_store.jsp" title="Change Store" class="itemLink" title="${change}"><bbbl:label key="lbl_favstorewidget_chng_store" language="${pageContext.request.locale.language}" /></a>
					</c:when>
					<c:otherwise>
						<a href="${contextPath}/selfservice/CanadaStoreLocator" title="Change Store" class="itemLink" title="${change}"><bbbl:label key="lbl_favstorewidget_chng_store" language="${pageContext.request.locale.language}" /></a>
					</c:otherwise>
				</c:choose>
				</dsp:oparam>
			</dsp:droplet>
			</div>		
		</c:if>
	</div>
</dsp:page>
