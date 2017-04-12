<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<div class="storeResult favorite grid_7 clearfix">
			<dsp:droplet name="/com/bbb/selfservice/SearchStoreDroplet">
				<dsp:param name="storeId" param="favouriteStoreId"/>
				<dsp:param name="searchType" value="2"/>
				<dsp:oparam name="output">
					<h3><bbbl:label key="lbl_favstore_fav_store" language="${pageContext.request.locale.language}"/></h3>
					<div class="grid_3 alpha omega location">									
						<h4>
							<span class="storeTitle"><dsp:valueof param="StoreDetails.storeName"/></span>
						</h4>
						<div class="address">
							<div class="street"><dsp:valueof param="StoreDetails.address"/></div>
							<div>
								<span class="city"><dsp:valueof param="StoreDetails.city"/>,</span>
								<span class="state"><dsp:valueof param="StoreDetails.state"/></span>
								<span class="zip"><dsp:valueof param="StoreDetails.postalCode"/></span>
							</div>
							<div class="phone"><dsp:valueof param="StoreDetails.storePhone"/></div>
						</div>
                        <%--
						<div class="actionLink">
							<a class="viewMap" href="#viewMap"><bbbl:label key="lbl_favstore_view_map" language="${pageContext.request.locale.language}"/></a>
						</div>--%>
						<div class="actionLink">
                            <dsp:getvalueof var="storeId" param="StoreDetails.storeId" />
                            <a class="viewDirectionsNew" href="#" data-storeid="${storeId}" title="Get Map & Directions">Get Map & Directions</a>
							<%-- <a class="viewDirections" href="#viewDirections"><bbbl:label key="lbl_favstore_get_dir" language="${pageContext.request.locale.language}"/></a> --%>
						</div>
					</div>
					<dsp:getvalueof var="StoreDetailsVar" param="StoreDetails" scope="page"/>
					<div class="grid_3 alpha omega padLeft_10">
						<p class="noMarBot">
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
							<dsp:valueof param="StoreDetails.storeDescription"/><br/>
							<dsp:valueof param="StoreDetails.storePhone"/><br/>
						</p>
						<c:set var="favstorehealth">
							<bbbl:label key="lbl_favstore_health" language="${pageContext.request.locale.language}"/>
						</c:set>						
						<div class="clearfix padTop_10 padRight_10">
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
						</div>
					</div>
						
				</dsp:oparam>
			</dsp:droplet>
	
	</div>
</dsp:page>