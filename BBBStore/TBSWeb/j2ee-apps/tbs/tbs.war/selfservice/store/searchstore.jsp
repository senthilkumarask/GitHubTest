<dsp:page>
	<bbb:pageContainer index="false" follow="false">
		<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
		<dsp:importbean bean="com/bbb/selfservice/SearchStoreFormHandler" />
		<dsp:importbean bean="com/bbb/selfservice/SearchStoreDroplet" />
		<dsp:valueof param="SearchStoreFormHandler.mapQuestSearchString"></dsp:valueof>
		<dsp:form method="post" action="searchstore.jsp">
			<table border='1'>
				<tr>
					<td><dsp:droplet
							name="/atg/dynamo/droplet/ErrorMessageForEach">
							<dsp:oparam name="output">
								<dsp:valueof param="message" />
							</dsp:oparam>
						</dsp:droplet><!-- <dsp:setvalue bean="SearchStoreFormHandler.searchStoreSuccessURL"
							value="searchstore.jsp" /> <dsp:setvalue
							bean="SearchStoreFormHandler.searchStoreErrorURL"
							value="searchstore.jsp" /> --></td>
							<dsp:input bean="SearchStoreFormHandler.fromPage" type="hidden" value="searchstore" />
				</tr>
				<tr>
					<td>
						<ul>
							<li><label><bbbl:label key="lbl_search_store_zip" language="${pageContext.request.locale.language}" /></label> <dsp:input
									bean="SearchStoreFormHandler.storeLocator.postalCode"
									type="text" /></li>
							<li><label><bbbl:label key="lbl_search_store_address" language="${pageContext.request.locale.language}" /></label> <dsp:input
									bean="SearchStoreFormHandler.storeLocator.address" type="text" />
							</li>
							<li><label><bbbl:label key="lbl_search_store_city" language="${pageContext.request.locale.language}" /></label> <dsp:input
									bean="SearchStoreFormHandler.storeLocator.city" type="text" />
							</li>
							<li><label><bbbl:label key="lbl_search_store_state" language="${pageContext.request.locale.language}" /></label> <dsp:input
									bean="SearchStoreFormHandler.storeLocator.state" type="text" />
							</li>
							<li><label><bbbl:label key="lbl_search_store_radius" language="${pageContext.request.locale.language}" /></label> <dsp:input
									bean="SearchStoreFormHandler.storeLocator.radius" type="text" />
							</li>
							<br />
							<dsp:input bean="SearchStoreFormHandler.siteContext" type="hidden" beanvalue="/atg/multisite/Site.id"/>
							<dsp:input bean="SearchStoreFormHandler.searchStore"
								type="submit" value="submit" >
                                <dsp:tagAttribute name="aria-pressed" value="false"/>
                                <dsp:tagAttribute name="role" value="button"/>
                            </dsp:input>
						</ul>
					</td>
				</tr>
			</table>
			<table>

			</table>
			<dsp:droplet name="SearchStoreDroplet">
				<%-- Switch case if pagekey is part of request then pass it to droplet --%>
				<dsp:droplet name="/atg/dynamo/droplet/Switch">
					<dsp:param name="value" param="pageKey" />
					<dsp:oparam name="true">
						<dsp:param name="pageKey" param="pageKey" />
						<dsp:param name="pageNumber" param="pageNumber" />
					</dsp:oparam>
				</dsp:droplet>
				<dsp:oparam name="output">
					<dsp:valueof param="StoreDetailsWrapper.currentPage"></dsp:valueof>
					<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param name="array" param="StoreDetailsWrapper.storeDetails" />
						<dsp:param name="elementName" value="StoreDetails" />
						<dsp:oparam name="output">
							<dsp:param name="mapQuestKeyURL"
								bean="SearchStoreDroplet.staticMapKey" />
					    	<div id="storeMapModal">
	    						<div id="mapModalDialogs"></div>
								<li>Store Id: <dsp:valueof param="StoreDetails.storeId" />
									<a href="#" onclick="javascript:showMapModal('<dsp:valueof param="StoreDetails.address"/>',
																	'<dsp:valueof param="StoreDetails.city"/>',
																	'<dsp:valueof param="StoreDetails.state"/>',
																	'<dsp:valueof param="StoreDetails.country"/>',
																	'<dsp:valueof param='mapQuestKeyURL'/>',
																	'<dsp:valueof param='StoreDetails.lat'/>',
																	'<dsp:valueof param='StoreDetails.lng'/>',
																	'<dsp:valueof bean='SearchStoreDroplet.staticMapZoom'/>',
																	'<dsp:valueof bean='SearchStoreDroplet.staticMapSize'/>',
																	'${contextPath}/selfservice/store/storemap.jsp', 
																	'View Map')">
									Click here to see the map </a>
								</li>
							</div>
							<li>Address: <dsp:valueof param="StoreDetails.address"></dsp:valueof>
							</li>
							<li>City: <dsp:valueof param="StoreDetails.city"></dsp:valueof>
							</li>
							<li>State: <dsp:valueof param="StoreDetails.state"></dsp:valueof>
							</li>
							<li>Country: <dsp:valueof param="StoreDetails.country"></dsp:valueof>
							<li>Lat: <dsp:valueof param="StoreDetails.lat"></dsp:valueof>
							<li>Langitude: <dsp:valueof param="StoreDetails.lng"></dsp:valueof>
							</li>
						</dsp:oparam>
					</dsp:droplet>
					<dsp:getvalueof id="currentPage" idtype="java.lang.String"
						param="StoreDetailsWrapper.currentPage" />
					<dsp:getvalueof id="totalPageCount" idtype="java.lang.String"
						param="StoreDetailsWrapper.totalPageCount" />
					<c:if test="${currentPage gt 1}">
						<dsp:getvalueof id="pageKey" param="StoreDetailsWrapper.pageKey" />
						<dsp:valueof param='StoreDetailsWrapper.pageKey' />
						<dsp:a
							href="searchstore.jsp?pageKey=${pageKey}&pageNumber=${currentPage-1}">Previous</a>
						</dsp:a>
					</c:if>
					<c:if test="${currentPage lt totalPageCount}">
						<dsp:getvalueof id="pageKey" param="StoreDetailsWrapper.pageKey" />
						<dsp:valueof param='StoreDetailsWrapper.pageKey' />
						<dsp:a
							href="searchstore.jsp?pageKey=${pageKey}&pageNumber=${currentPage+1}">Next</a>
						</dsp:a>
					</c:if>
				</dsp:oparam>
				<dsp:oparam name="empty">
				No Records found based on input criteria. Please try again.
				</dsp:oparam>
				<dsp:page>

					<head>
<title>Find a Store</title>
					</head>
					<body>
					</body>
				</dsp:page>
			</dsp:droplet>
			<%-- Result based on  Store Id
		<dsp:droplet name="SearchStoreDroplet">
				<dsp:param name="storeId" value="1032" />
				<dsp:oparam name="output">
					<dsp:param name="elementName" value="StoreDetails" />
					<li>Store Id: <a
						href="<dsp:valueof param='mapQuestKeyURL'/>
							&center=<dsp:valueof param='StoreDetails.lat'/>,
							<dsp:valueof param='StoreDetails.lng'/>
							&zoom=<dsp:valueof bean='SearchStoreDroplet.staticMapZoom'/>
							&size=<dsp:valueof bean='SearchStoreDroplet.staticMapSize'/>" />
						<dsp:valueof param="StoreDetails.storeId" /> </a>
					</li>
					<li>Address: <dsp:valueof param="StoreDetails.address"></dsp:valueof>
					</li>
					<li>City: <dsp:valueof param="StoreDetails.city"></dsp:valueof>
					</li>
					<li>State: <dsp:valueof param="StoreDetails.state"></dsp:valueof>
					</li>
					<li>Country: <dsp:valueof param="StoreDetails.country"></dsp:valueof>
					<li>Lat: <dsp:valueof param="StoreDetails.lat"></dsp:valueof>
					<li>Langitude: <dsp:valueof param="StoreDetails.lng"></dsp:valueof>
					</li>
				</dsp:oparam>
			</dsp:droplet> --%>
		</dsp:form>
	</bbb:pageContainer>
</dsp:page>