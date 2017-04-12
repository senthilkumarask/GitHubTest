<dsp:page>
	<dsp:importbean bean="/com/bbb/cms/droplet/PageTabsOrderingDroplet"/>
	<dsp:getvalueof var="clearanceBackUpFlag" param="clearanceBackUpFlag"/>
	<dsp:getvalueof var="clearanceDealsFlag" param="clearanceDealsFlag"/>
	<dsp:getvalueof var="justForYouFlag" param="justForYouFlag"/>
	<dsp:getvalueof var="lastviewedProductsList" param="lastviewedProductsList"/>
	<dsp:getvalueof var="justForYouProductList" param="justForYouProductList"/>
	<dsp:getvalueof var="ClearanceDealProductList" param="ClearanceDealProductList"/>
	<dsp:getvalueof var="clearanceProductsList" param="clearanceProductsList"/>
	<dsp:getvalueof var="displayFlag" param="displayFlag"/>
	<dsp:getvalueof var="tabName" param="tabName"/>
    <c:set var="homePage" value=" (home page)"/>

	<c:set var="justForYouTab"><dsp:valueof bean="PageTabsOrderingDroplet.justForYou"/></c:set>
	<c:set var="lastViewedTab"><dsp:valueof bean="PageTabsOrderingDroplet.lastViewedItems"/></c:set>
	<c:set var="clearanceTab"><dsp:valueof bean="PageTabsOrderingDroplet.clearanceDeals"/></c:set>

    <c:set var="justForYouTabLbl"><bbbl:label key="lbl_homepage_just_for_you"   language="<c:out param='${pageContext.request.locale.language}'/>" /></c:set>
    <c:set var="lastViewedTabLbl"><bbbl:label key="lbl_homepage_last_viewed_items"  language="<c:out param='${pageContext.request.locale.language}'/>" /></c:set>
    <c:set var="clearanceTabLbl"><bbbl:label key="lbl_homepage_clearance_deals" language="<c:out param='${pageContext.request.locale.language}'/>" /></c:set>

	<c:choose>

		<c:when test="${tabName eq 'Also Check Out'}">
 		</c:when>

		<c:when test="${tabName eq lastViewedTab}">
			<c:if test="${not empty lastviewedProductsList}" >
				<div id="categoryProductTabs-tabs2" class="categoryProductTabsData">
				 	<dsp:include page="/browse/certona_prod_carousel.jsp">
						<dsp:param name="productsVOsList" value="${lastviewedProductsList}"/>
                           <dsp:param name="desc" value="Last Viewed (home page)"/>
						   <%-- <dsp:param name="desc" value="${lastViewedTabLbl}${homePage}"/> --%>
						   <dsp:param name="crossSellFlag" value="true"/>
					</dsp:include>
				</div>
			</c:if>
		</c:when>

		<c:when test="${tabName eq 'Top College Items'}">

		</c:when>

		<c:when test="${tabName eq clearanceTab}">
			<c:if test="${clearanceDealsFlag eq true}">
			     <div id="categoryProductTabs-tabs1" class="categoryProductTabsData">
					 <c:choose>
					 	<c:when test="${not empty ClearanceDealProductList && displayFlag eq true }">
					  		<dsp:include page="/browse/certona_prod_carousel.jsp">
								<dsp:param name="productsVOsList" value="${ClearanceDealProductList}"/>
				             	<dsp:param name="desc" value="${clearanceTabLbl}${homePage}"/>
				             	<dsp:param name="crossSellFlag" value="true"/>
							</dsp:include>
					 	</c:when>
						<c:otherwise>
					        <dsp:include page="/browse/certona_prod_carousel.jsp">
							  	<dsp:param name="productsVOsList" value="${clearanceProductsList}"/>
						 		<dsp:param name="desc" value="${clearanceTabLbl}${homePage}"/>
						 		<dsp:param name="crossSellFlag" value="true"/>
						    </dsp:include>
					  	</c:otherwise>
					</c:choose>
			 	</div>
          	</c:if>
		</c:when>

		<c:when test="${tabName eq justForYouTab}">
			<c:if test="${justForYouFlag eq true }">
				<div id="categoryProductTabs-tabs3" class="categoryProductTabsData">
				 	<dsp:include page="/browse/certona_prod_carousel.jsp">
					  <dsp:param name="productsVOsList" value="${justForYouProductList}"/>
					  <dsp:param name="crossSellFlag" value="true"/>
					     <dsp:param name="desc" value="${justForYouTabLbl}${homePage}"/>
					</dsp:include>
			    </div>
		    </c:if>
		</c:when>

		<c:when test="${tabName eq 'Customer Also Viewed'}">

		</c:when>

		<c:when test="${tabName eq 'Frequently Bought With'}">

		</c:when>
	</c:choose>
</dsp:page>