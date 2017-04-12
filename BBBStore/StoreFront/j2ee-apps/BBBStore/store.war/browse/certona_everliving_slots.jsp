<dsp:page>
    <%-- import required beans --%>
    <dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProdToutDroplet"/>
    <dsp:importbean bean="/com/bbb/commerce/browse/droplet/ExitemIdDroplet"/>
    <dsp:importbean bean="/com/bbb/certona/droplet/CertonaDroplet"/>
    <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
    <dsp:importbean bean="/atg/multisite/Site"/>
    <dsp:importbean bean="/atg/userprofiling/Profile"/>
    <dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
    <dsp:importbean var="SystemErrorInfo" bean="/com/bbb/utils/SystemErrorInfo"/>	

    <%-- init default values/variables --%>
    <c:set var="BedBathUSSite" scope="request"><bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys"/></c:set>
    <c:set var="BuyBuyBabySite" scope="request"><bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys"/></c:set>
    <c:set var="BedBathCanadaSite" scope="request"><bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys"/></c:set>
    <c:set var="scene7Path" scope="request"><bbbc:config key="scene7_url" configName="ThirdPartyURLs"/></c:set>
    <c:set var="appid" scope="request"><dsp:valueof bean="Site.id"/></c:set>
    <c:set var="appIdCertona" scope="request"><dsp:valueof bean="CertonaConfig.siteIdAppIdMap.${appid}"/></c:set>
    <c:set var="relatedItemsDisplayFlag" value="false"/>
	<c:set var="frequentlyBoughtDisplayFlag" value="false"/>
	<c:set var="custAlsoViewedProdMax" scope="request"><bbbc:config key="PDPCustAlsoViewProdMax" configName="CertonaKeys" /></c:set>
	<c:set var="frequentlyBuyProdMax" scope="request"><bbbc:config key="PDPFreqBoughtProdMax" configName="CertonaKeys" /></c:set>
    
    <%-- set default flags --%>
    <c:set var="certonaDefaultFlag" value="true"/>
    
     <%-- capture url params --%>
    <dsp:getvalueof var="scheme" param="scheme"/>
    <dsp:getvalueof var="number" param="number"/>
    <dsp:getvalueof var="certonaSwitch" param="certonaSwitch"/>
	<dsp:getvalueof var="searchTerm" param="searchTerm"/>
	<dsp:getvalueof var="certonaPageName" param="certonaPageName"/>
	<dsp:getvalueof var="productId" param="productId"/>
	
	<%--BBBSL-8186 | Populating value of certona context --%>
	<dsp:getvalueof var="parentProductId" param="parentProductId" />
	<c:set var="CertonaContext" scope="request">${parentProductId};</c:set>
	
	
	    <%-- check if certona if off --%>
    <c:if test="${not empty certonaSwitch}">
        <c:set var="certonaDefaultFlag" value="${certonaSwitch}"/>
    </c:if>
    
    
        <%-- find userid (if logged-in) --%>
    <dsp:droplet name="Switch">
        <dsp:param name="value" bean="Profile.transient"/>
        <dsp:oparam name="false">
            <dsp:getvalueof var="userId" bean="Profile.id"/>
        </dsp:oparam>
        <dsp:oparam name="true">
            <dsp:getvalueof var="userId" value=""/>
        </dsp:oparam>
    </dsp:droplet>
    
    <div class="clearfix"> 

    <div id="certonaBottomTabs">
    
    <div id="botCrossSell" class="marTop_20 container_12" role="complementary">
	 
				<c:set var="relatedItemsDisplayFlag" value="false"/>
				<c:set var="frequentlyBoughtDisplayFlag" value="false"/>
            <dsp:droplet name="ProdToutDroplet">
					<dsp:param value="lastviewed" name="tabList" />
						<dsp:param value="" name="id" />
						<dsp:param value="${appid}" name="siteId" />
						<dsp:param name="productId" param="productId" />
					<dsp:oparam name="output">
					<dsp:getvalueof var="clearanceProductsList" param="clearanceProductsList" />
					<dsp:getvalueof var="lastviewedProductsList" param="lastviewedProductsList" />
					<dsp:droplet name="ExitemIdDroplet">
			          <dsp:param value="${lastviewedProductsList}" name="lastviewedProductsList" />
			          <dsp:param name="certonaExcludedItems" value="${childProducts}"/>
			          <dsp:oparam name="output">
					    <dsp:getvalueof var="productList" param="productList" />
                               </dsp:oparam>
			        </dsp:droplet>  
			        

						<c:set var="custAlsoViewedProdMax" scope="request">
			  				<bbbc:config key="PDPCustAlsoViewProdMax" configName="CertonaKeys" />
		    			</c:set>
						<c:set var="frequentlyBuyProdMax" scope="request">
			  				<bbbc:config key="PDPFreqBoughtProdMax" configName="CertonaKeys" />
		    			</c:set>
						
					  <c:if test="${certonaDefaultFlag eq true}">
				        <dsp:droplet name="CertonaDroplet">
					 		<dsp:param name="scheme" value="${scheme}"/>
							 <dsp:param name="userid" value="${userId}"/>
							 <dsp:param name="context" value="${CertonaContext}"/>
							 <dsp:param name="exitemid" value="${productList}"/>
							 <dsp:param name="productId" value="${productId}"/>
							 <dsp:param name="siteId" value="${appid}"/>
							 <dsp:param name="number" value="${custAlsoViewedProdMax};${frequentlyBuyProdMax}"/>
							 <dsp:oparam name="output">
							 	<%--BBBSL-6574 --%>
								<dsp:getvalueof var="requestURL" param="certonaResponseVO.requestURL" scope="request"/>
								<dsp:getvalueof var="responseXML" param="certonaResponseVO.responseXML" scope="request"  />
								
									   <!--
									 <div id="certonaRequestResponse" class="hidden"> 
										<ul> 
											<li id="requestURL"><dsp:valueof value="${requestURL}" valueishtml="true" /></li>
										 
											<li id="responseXML"><dsp:valueof value="${responseXML}" valueishtml="true"/></li>  
										</ul> 
									</div>  
									 --> 	
									<c:set var="schemeArray" value="${fn:split(scheme, ';')}" />
								
									<dsp:getvalueof var="relatedItemsProductsVOsList" param="certonaResponseVO.resonanceMap.${schemeArray[0]}.productsVOsList"/>
									<dsp:getvalueof var="frequentlyBoughtProductsVOsList" param="certonaResponseVO.resonanceMap.${schemeArray[1]}.productsVOsList"/>
									<dsp:getvalueof var="linksCertona" param="certonaResponseVO.resxLinks"/>
			                        <dsp:getvalueof var="pageIdCertona" param="certonaResponseVO.pageId"/>
			                        
							 </dsp:oparam>
							 <dsp:oparam name="error">
							<dsp:getvalueof var="requestURL" param="requestURL" scope="request"/>
							 <dsp:getvalueof var="errorMsg" param="errorMsg" scope="request"  />
							 			   <!--
								 <div id="certonaRequestResponse" class="hidden"> 
									<ul> 
										<li id="requestURL"><dsp:valueof value="${requestURL}" valueishtml="true" /></li>
										<li id="errorMsg"><dsp:valueof value="${errorMsg}" valueishtml="true" /></li>
									</ul> 
								</div>  
								 --> 	
							 </dsp:oparam>
							 <dsp:oparam name="empty">
							 <dsp:getvalueof var="requestURL" param="requestURL" scope="request"/>		 
							 <dsp:getvalueof var="errorMsg" param="errorMsg" scope="request"  />
							 			   <!--
								 <div id="certonaRequestResponse" class="hidden"> 
									<ul> 
										<li id="requestURL"><dsp:valueof value="${requestURL}" valueishtml="true" /></li>
										<li id="errorMsg"><dsp:valueof value="${errorMsg}" valueishtml="true" /></li>
									</ul> 
								</div>  
								 --> 								 
							 </dsp:oparam>
						</dsp:droplet>
						
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
			   
					 </c:if> 
				 
				<c:if test="${(not empty relatedItemsProductsVOsList) || (not empty lastviewedProductsList) || (not empty frequentlyBoughtProductsVOsList)}">
		         <div id="botCrossSell" class="marTop_10 grid_12">
				<div class="categoryProductTabs marTop_20"> 
					<c:set var="customerAlsoViewedTab"><bbbl:label key='lbl_pdp_product_related_items' language="${pageContext.request.locale.language}" /></c:set>
					<c:set var="frequentlyBoughtWithTab"><bbbl:label key='lbl_pdp_product_frequently_bought' language="${pageContext.request.locale.language}" /></c:set>
					<ul class="categoryProductTabsLinks"> 
					<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
							<dsp:param name="value" value="${relatedItemsProductsVOsList}"/>
							<dsp:oparam name="false">
							<c:set var="relatedItemsDisplayFlag" value="true"/>
							</dsp:oparam>
							<dsp:oparam name="true">
							<c:set var="relatedItemsDisplayFlag" value="false"/>
							</dsp:oparam>
					</dsp:droplet>
					<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
							<dsp:param name="value" value="${frequentlyBoughtProductsVOsList}"/>
							<dsp:oparam name="false">
							<c:set var="frequentlyBoughtDisplayFlag" value="true"/>

							</dsp:oparam>
							<dsp:oparam name="true">
							<c:set var="frequentlyBoughtDisplayFlag" value="false"/>
							</dsp:oparam>
					</dsp:droplet>

					</ul>

							<c:if test="${relatedItemsDisplayFlag eq 'true'}">
							<h2><bbbl:label key='lbl_pdp_product_related_items' language="${pageContext.request.locale.language}" /></h2><br/>
								<div id="pdp_cav">
	                            	<div id="botCrossSell-tabs1" class="categoryProductTabsData">
										<dsp:include page="../browse/certona_prod_carousel.jsp" >
										 	<dsp:param name="productsVOsList" value="${relatedItemsProductsVOsList}"/>
										 	<dsp:param name="crossSellFlag" value="true"/>
										 	<dsp:param name="desc" value="Customer Also Viewed (ever living pdp)"/>
		
									  	</dsp:include>
	                                </div>
	                            </div>
                                <br/><br/><br/>
							</c:if>

							<c:if test="${frequentlyBoughtDisplayFlag eq 'true'}">
							<h2><bbbl:label key='lbl_pdp_product_frequently_bought' language="${pageContext.request.locale.language}" /></h2><br/>
								<div id="pdp_fbw">
	                                <div id="botCrossSell-tabs2" class="categoryProductTabsData">
									<dsp:include page="../browse/certona_prod_carousel.jsp" >
									 	<dsp:param name="productsVOsList" value="${frequentlyBoughtProductsVOsList}"/>
									 	<dsp:param name="crossSellFlag" value="true"/>
									 	<dsp:param name="desc" value="Frequently Bought(ever living pdp)"/>
								  	</dsp:include>
	                                </div>
	                             </div>
                                <br/><br/><br/>
							</c:if>
							
							<c:if test="${not empty lastviewedProductsList and fn:length(lastviewedProductsList) > 1}">
                            <h2><bbbl:label key='lbl_pdp_product_last_viewed' language="${pageContext.request.locale.language}" /></h2>
							<div id="botCrossSell-tabs3" class="categoryProductTabsData noBorder">
								<dsp:include page="last_viewed.jsp">
									<dsp:param name="lastviewedProductsList" value="${lastviewedProductsList}" />
									<dsp:param name="desc" value="Last  Viewed Item (pdp)" />
								</dsp:include>
							</div>
						</c:if>
					   </div> 
					</div>
				  </c:if>
				 </dsp:oparam>
				</dsp:droplet>
			   </div> 
			  </div>
    
	
	 <%-- certona JS call --%>
        <script type="text/javascript">
            setTimeout(function(){
                resx.appid = "${appIdCertona}";
                resx.pageid = "${pageIdCertona}";
                resx.customerid = "${userId}";
                resx.links = '${linksCertona}'+'${productList}';

                if (typeof BBB.loadCertonaJS === "function") { BBB.loadCertonaJS(); }
            }, 100);
        </script>
     </div>  
</dsp:page>