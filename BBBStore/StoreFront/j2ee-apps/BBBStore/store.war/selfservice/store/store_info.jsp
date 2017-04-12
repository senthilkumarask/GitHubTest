
<%-- ====================== Description===================
/**
* This page is used to display store search results which includes store Name, address, distance, phone number, store timings and various links
like view map, view directions, make favorite store.  
* @author Seema
**/
--%>
<dsp:page>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:importbean bean="com/bbb/selfservice/SearchStoreFormHandler" />
		<div class="storeResult makeFavStoreResults clearfix noPadBot">
			<ul class="clearfix noMarBot padBottom_10">
				<li class="column1">
				  <div class="clearfix">
						<h4 class="noMarTop">
							<%--Displays Store name and distance in miles from customer's place. --%>
							<span class="storeTitle"><dsp:valueof param="storeDetails.storeName" /></span> 
							<span class="storeMiles">(<dsp:valueof param="storeDetails.distance"
									converter="number" /> <bbbl:label key="lbl_store_route_miles"
									language="${pageContext.request.locale.language}" />)</span>
						</h4>
						<div class="address">
							<%--Displays Store's phone number and address which includes street, city, state and Zip code  --%>
							<div class="street">
								<dsp:valueof param="storeDetails.address" />
							</div>
							<div>
								<span class="city"> <dsp:valueof param="storeDetails.city" />,
								</span> <span class="state"><dsp:valueof
										param="storeDetails.state" /> </span> <span class="zip"><dsp:valueof
										param="storeDetails.postalCode" /> </span>
							</div>
							<div class="phone">
								<dsp:valueof param="storeDetails.storePhone" />
							</div>
							
						</div>
						<dsp:getvalueof var="address" param="storeDetails.address" />
						<dsp:getvalueof var="city" param="storeDetails.city" />
						<dsp:getvalueof var="state" param="storeDetails.state" />
						<dsp:getvalueof var="postalCode" param="storeDetails.postalCode" />

						<%--Displays link to view MAP to locate the store location with zoom functionality. --%>
						<div class="padTop_5 actionLink">
							<c:set var="lbl_find_store_get_directions"><bbbl:label key="lbl_find_store_get_directions"
									language="${pageContext.request.locale.language}" /></c:set>
                            <dsp:getvalueof var="storeId" param="storeDetails.storeId" />
                            <a class="viewDirectionsNew" href="#" data-storeid="${storeId}" title="Get Map & Directions">Get Map & Directions</a>
                            <%--
							<dsp:a iclass="viewDirections" href="#viewDirections"  title="${lbl_find_store_get_directions}">
								<bbbl:label key="lbl_find_store_get_directions"
									language="${pageContext.request.locale.language}" />
								<dsp:param name="routeEndPoint"
									value="${address}, ${city}, ${state} ${postalCode}" />

							</dsp:a>
                            --%>
						</div>
						<%--<div class="padTop_5 actionLink">
							<c:set var="lbl_find_store_view_map"><bbbl:label key="lbl_find_store_view_map"
										language="${pageContext.request.locale.language}" /></c:set>
							<dsp:a iclass="viewMap" href="#viewMap" title="${lbl_find_store_view_map}">
								<bbbl:label key="lbl_find_store_view_map"
									language="${pageContext.request.locale.language}" />
							</dsp:a>
						</div>--%>
						<%--Displays link to view directions to reach store  --%>
				  </div>
				</li>
				
				<li class="column2">	
				<%--Displays Store's timings and Phone number --%>
				<dsp:getvalueof var="StoreDetailsVar" param="StoreDetails" scope="page"/>
					<div class="clearfix">
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
						</c:forTokens><br/>
						<dsp:valueof param="StoreDetails.storeDescription" />
						</p>
						
						<div class="clearfix padTop_5">
							<dsp:getvalueof param="storeDetails.specialtyShopsCd" id="code"/>
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
				</li>
				
				<li class="column3">
					<dsp:droplet name="/atg/dynamo/droplet/Switch">
						<dsp:param name="value" bean="/atg/userprofiling/Profile.transient"/>
						<dsp:oparam name="false">
							<%--Displays link to Make Favorite Store --%>
							<div class="actionLink">
								<dsp:getvalueof id="storeId" param="storeDetails.storeId"/>
								<dsp:getvalueof id="favouriteStoreId" param="favouriteStoreId"/>
								
								<c:if test="${!(favouriteStoreId eq storeId)}">
								<c:set var="lbl_find_store_make_fav_store"><bbbl:label key="lbl_find_store_make_fav_store"	language="${pageContext.request.locale.language}" /></c:set>
								<div class="button button_active button_active_orange cf block">
									<dsp:a iclass="makeFavorite" title="${lbl_find_store_make_fav_store}"href="${currUrl}?favouriteStoreId=${storeId}" onclick="javascript:acctUpdate('favorite store')">
										<bbbl:label key="lbl_find_store_make_fav_store"	language="${pageContext.request.locale.language}" />
										<dsp:property bean="SearchStoreFormHandler.modifyFavStore" value=""/>
									</dsp:a>
								</div>
								</c:if>
							</div>
						</dsp:oparam>
					</dsp:droplet>	
				</li>	
			</ul>
		</div>
	<script type="text/javascript">
		var pageAction = "277";
	</script>

</dsp:page>