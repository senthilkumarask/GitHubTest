<dsp:page>
    <%-- import required beans --%>
    <dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProdToutDroplet"/>
    <dsp:importbean bean="/com/bbb/commerce/browse/droplet/ExitemIdDroplet"/>
    <dsp:importbean bean="/com/bbb/certona/droplet/CertonaDroplet"/>
    <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
    <dsp:importbean bean="/atg/multisite/Site"/>
    <dsp:importbean bean="/com/bbb/cms/droplet/PageTabsOrderingDroplet"/>
    <dsp:importbean bean="/atg/userprofiling/Profile"/>
    <dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/RedirectDroplet"/>
	<dsp:importbean bean="/com/bbb/framework/security/ValidateParametersDroplet" />
	 <dsp:importbean var="SystemErrorInfo" bean="/com/bbb/utils/SystemErrorInfo"/>

    <%-- init default values/variables --%>
    <c:set var="BedBathUSSite" scope="request"><bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys"/></c:set>
    <c:set var="BuyBuyBabySite" scope="request"><bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys"/></c:set>
    <c:set var="BedBathCanadaSite" scope="request"><bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys"/></c:set>
    <c:set var="justForYouTab" scope="request"><dsp:valueof bean="PageTabsOrderingDroplet.justForYou"/></c:set>
    <c:set var="lastViewedTab" scope="request"><dsp:valueof bean="PageTabsOrderingDroplet.lastViewedItems"/></c:set>
    <c:set var="clearanceTab" scope="request"><dsp:valueof bean="PageTabsOrderingDroplet.clearanceDeals"/></c:set>
    <c:set var="alsoCheckOutTab" scope="request"><dsp:valueof bean="PageTabsOrderingDroplet.alsoCheckOut"/></c:set>
    <c:set var="topCollegeItemsTab" scope="request"><dsp:valueof bean="PageTabsOrderingDroplet.topCollegeItems"/></c:set>
    <c:set var="scene7Path" scope="request"><bbbc:config key="scene7_url" configName="ThirdPartyURLs"/></c:set>
    <c:set var="appid" scope="request"><dsp:valueof bean="Site.id"/></c:set>
    <c:set var="appIdCertona" scope="request"><dsp:valueof bean="CertonaConfig.siteIdAppIdMap.${appid}"/></c:set>
	<c:choose>
		<c:when test="${appid eq BedBathUSSite}">
			<c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_us"/></c:set>
		</c:when>
		<c:when test="${appid eq BuyBuyBabySite}">
			<c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_baby"/></c:set>
		</c:when>
		<c:otherwise>
			<c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_ca"/></c:set>
		</c:otherwise>
	</c:choose>

    <%-- set default flags --%>
    <c:set var="clearanceDealsFlag" value="false"/>
    <c:set var="clearanceBackUpEmptyFlag" value="false"/>
    <c:set var="justForYouFlag" value="false"/>
    <c:set var="certonaBottomTabsFlag" value="true"/>
    <c:set var="funNewProductsFlag" value="false"/>
    
    <c:set var="alsoCheckOutFlag" value="false"/>
    <c:set var="topCollegeItemsFlag" value="false"/>
        
    <c:set var="displayFlag" value="true"/>
    <c:set var="certonaDefaultFlag" value="true"/>
    <c:set var="hasTabs" value="false"/>
    <%-- <c:choose>
        <c:when test="${currentSiteId eq BedBathUSSite}"><c:set var="CertonaOn" scope="request"><tpsw:switch tagName="CertonaTag_us"/></c:set></c:when>
        <c:when test="${currentSiteId eq BuyBuyBabySite}"><c:set var="CertonaOn" scope="request"><tpsw:switch tagName="CertonaTag_baby"/></c:set></c:when>
        <c:otherwise><c:set var="CertonaOn" scope="request"><tpsw:switch tagName="CertonaTag_ca"/></c:set></c:otherwise>
    </c:choose> --%>


    <%-- capture url params --%>
    <dsp:getvalueof var="categoryId" param="categoryId"/>
    <dsp:getvalueof var="scheme" param="scheme"/>
    <dsp:getvalueof var="number" value="${fn:escapeXml(param.number)}"/>
    <dsp:getvalueof var="lastViewedTabLbl" param="lastViewedTabLbl"/>
    <dsp:getvalueof var="justForYouTabLbl" value="${fn:escapeXml(param.justForYouTabLbl)}"/>
    <dsp:getvalueof var="clearanceTabLbl"  value="${fn:escapeXml(param.clearanceTabLbl)}" />
    <dsp:getvalueof var="certVO_fnp" param="fnp"/>
    <dsp:getvalueof var="certVO_cd" param="cd"/>
    <dsp:getvalueof var="certVO_jfy" param="jfy"/>
    <dsp:getvalueof var="clearanceDealsFlagParam" param="clearanceDealsFlagParam"/>
    <dsp:getvalueof var="justForYouFlagParam" param="justForYouFlagParam"/>
    <dsp:getvalueof var="funNewProductsFlagParam" param="funNewProductsFlagParam"/>
    <dsp:getvalueof var="certonaBottomTabsFlagParam" param="certonaBottomTabsFlagParam"/>
    <dsp:getvalueof var="omniCrossSellPageName" value="${fn:escapeXml(param.omniCrossSellPageName)}"/>
    <dsp:getvalueof var="certonaPageName" param="certonaPageName"/>
    <dsp:getvalueof var="linksCertonaNonRecomm" value="${fn:escapeXml(param.linksCertonaNonRecomm)}"/>
    <dsp:getvalueof var="alsoCheckoutTabLbl" param="alsoCheckoutTabLbl"/> 
    <dsp:getvalueof var="topCollegeItemsTabLbl" param="topCollegeItemsTabLbl"/>
    
    
    <dsp:getvalueof var="alsoCheckOutFlagParam" param="alsoCheckOutFlag"/>
    <dsp:getvalueof var="topCollegeItemsFlagParam" param="topCollegeItemsFlag"/>
    <dsp:getvalueof var="certonaSwitch" param="certonaSwitch"/>
    
		<%-- Validate external parameters for Cross site scripting changes start --%>
	
		<dsp:droplet name="ValidateParametersDroplet">
			<dsp:param value="omniCrossSellPageName" name="paramArray" />
			<dsp:param value="${omniCrossSellPageName}" name="paramsValuesArray" />
				<dsp:oparam name="error">
				      <dsp:droplet name="RedirectDroplet">
				        <dsp:param name="url" value="/404.jsp" />
				      </dsp:droplet>
				</dsp:oparam>
			 	<dsp:oparam name="valid">
				 	<c:choose>
						<c:when test="${fn:contains(omniCrossSellPageName,'home')|| fn:contains(omniCrossSellPageName,'category')}">	
							<dsp:droplet name="ValidateParametersDroplet">
							    <dsp:param value="justForYouTabLbl;clearanceTabLbl;lastViewedTabLbl;omniCrossSellPageName" name="paramArray" />
							    <dsp:param value="${fn:escapeXml(param.justForYouTabLbl)};${param.clearanceTabLbl};${param.lastViewedTabLbl};${fn:escapeXml(param.omniCrossSellPageName)}" name="paramsValuesArray" />
							    <dsp:oparam name="error">
							      <dsp:droplet name="RedirectDroplet">
							        <dsp:param name="url" value="/404.jsp" />
							      </dsp:droplet>
							    </dsp:oparam>
						    </dsp:droplet>
						</c:when>
						<c:when test="${fn:contains(omniCrossSellPageName,'college')}">	
						 	<dsp:droplet name="ValidateParametersDroplet">
							    <dsp:param value="topCollegeItemsTabLbl;alsoCheckoutTabLbl;lastViewedTabLbl;omniCrossSellPageName" name="paramArray" />
							    <dsp:param value="${param.topCollegeItemsTabLbl};${param.alsoCheckoutTabLbl};${param.lastViewedTabLbl};${fn:escapeXml(param.omniCrossSellPageName)}" name="paramsValuesArray" />
							    <dsp:oparam name="error">
							      <dsp:droplet name="RedirectDroplet">
							        <dsp:param name="url" value="/404.jsp" />
							      </dsp:droplet>
							    </dsp:oparam>
						    </dsp:droplet>
						</c:when>
				</c:choose>
			</dsp:oparam>   
	</dsp:droplet>
	<%--Validate external parameters for Cross site scripting changes end --%>
    <%-- check if certona if off --%>
    <c:if test="${not empty certonaSwitch}">
        <c:set var="certonaDefaultFlag" value="${certonaSwitch}"/>
    </c:if>

	
    <%-- check if certona bottom tabs need to be shown --%>
    <c:if test="${not empty certonaBottomTabsFlagParam}">
        <c:set var="certonaBottomTabsFlag" value="${certonaBottomTabsFlagParam}"/>
    </c:if>


    <%-- check if fun new products need to be shown --%>
    <c:if test="${not empty funNewProductsFlagParam}">
        <c:set var="funNewProductsFlag" value="${funNewProductsFlagParam}"/>
    </c:if>


    <%-- exclude last viewed items from certona call/product list --%>
    <dsp:droplet name="ProdToutDroplet">
        <dsp:param name="tabList" value="lastviewed"/>
        <dsp:param name="siteId" value="${appid}"/>
        <dsp:param name="id" param="categoryId"/>
        <dsp:oparam name="output">
            <dsp:getvalueof var="lastviewedProductsList"  vartype="java.util.List" param="lastviewedProductsList"/>
            <c:if test="${not empty lastviewedProductsList}">
                <dsp:droplet name="ExitemIdDroplet">
                    <dsp:param value="${lastviewedProductsList}" name="lastviewedProductsList"/>
                    <dsp:oparam name="output">
                        <dsp:getvalueof var="productList"  vartype="java.util.List" param="productList"/>
                    </dsp:oparam>
                </dsp:droplet>
            </c:if>
        </dsp:oparam>
    </dsp:droplet>


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


    <%-- get productsVOsList to show from Certona --%>
    <c:if test="${certonaDefaultFlag eq true}">
	    <dsp:droplet name="CertonaDroplet">
	        <dsp:param name="scheme" value="${scheme}"/>
	        <dsp:param name="exitemid" value="${productList}"/>
	        <dsp:param name="userid" value="${userId}"/>
	        <dsp:param name="siteId" value="${appid}"/>
	        <dsp:param name="number" value="${number}"/>
	        <dsp:oparam name="output">
	            <c:if test="${certonaPageName eq 'Home Page'}">
	                <dsp:getvalueof var="certona_funNewProductsList" param="certonaResponseVO.resonanceMap.${certVO_fnp}.productsVOsList"/>
	            </c:if>
	            <dsp:getvalueof var="certona_clearanceDealProductsList" param="certonaResponseVO.resonanceMap.${certVO_cd}.productsVOsList"/>
	            <dsp:getvalueof var="certona_justForYouProductsList" param="certonaResponseVO.resonanceMap.${certVO_jfy}.productsVOsList"/>
	            <dsp:getvalueof var="certona_topRegistryProductsVOsList" param="certonaResponseVO.resonanceMap.${'clp_tci'}.productsVOsList"/>
		        <dsp:getvalueof var="certona_alsoChkOutProductsVOsList" param="certonaResponseVO.resonanceMap.${'clp_aco'}.productsVOsList"/>
	            <dsp:getvalueof var="linksCertona" param="certonaResponseVO.resxLinks"/>
	            <dsp:getvalueof var="pageIdCertona" param="certonaResponseVO.pageId"/>
	            
	            	<%--BBBSL-6574| Printing Certona WS call on source --%>
					<dsp:getvalueof var="requestURL" param="certonaResponseVO.requestURL" scope="request"/>
					<dsp:getvalueof var="responseXML" param="certonaResponseVO.responseXML" scope="request"  />
	        </dsp:oparam>
	        <dsp:oparam name="error">
	       		<dsp:getvalueof var="requestURL" param="requestURL" scope="request"/>
				<dsp:getvalueof var="errorMsg" param="errorMsg" scope="request"  />
	            <c:set var="displayFlag" value="false"/>
	        </dsp:oparam>
	        <dsp:oparam name="empty">
	        <dsp:getvalueof var="requestURL" param="requestURL" scope="request"/>
	        <dsp:getvalueof var="errorMsg" param="errorMsg" scope="request"  />
	            <c:set var="displayFlag" value="false"/>
	        </dsp:oparam>
	    </dsp:droplet>
    </c:if>


    <%-- check if clearanceDeals backup products need to be fetched --%>
    <c:if test="${empty certona_clearanceDealProductsList || displayFlag eq false}">
        <dsp:droplet name="/com/bbb/commerce/browse/droplet/ProdToutDroplet">
            <dsp:param name="tabList" value="clearance"/>
            <dsp:param name="siteId" value="${appid}"/>
            <dsp:param name="id" param="categoryId"/>
            <dsp:oparam name="output">
                <dsp:getvalueof var="backup_clearanceDealProductsList" param="clearanceProductsList"/>
                <c:if test="${empty backup_clearanceDealProductsList}">
                    <c:set var="clearanceBackUpEmptyFlag" value="true"/>
                </c:if>
            </dsp:oparam>
        </dsp:droplet>
    </c:if>


    <%-- ajax response markup wrapper --%>
    <div class="clearfix">
        <%-- fun new products (only on home page) --%>
        <c:if test="${funNewProductsFlag eq true}">
            <div id="funNewProducts">
                <c:choose>
                    <%-- has fun new products from certona --%>
                    <c:when test="${displayFlag eq true && not empty certona_funNewProductsList}">
                    	<div id="hp_fnp">
	                        <dsp:include page="/cms/some_fun_new_products_certona.jsp" >
	                            <dsp:param name="funNewProductsVOsList" value="${certona_funNewProductsList}"/>
	                        </dsp:include>
	                    </div>
                    </c:when>
                    <%-- doesn't have fun new products from certona ... show backup products --%>
                    <c:otherwise>
                        <dsp:include page="/cms/some_fun_new_products.jsp"/>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>
          <c:if test="${(justForYouFlagParam eq true) && (displayFlag eq true) && (not empty certona_justForYouProductsList) && (certonaPageName eq 'Home Page')}">
            <div id="justForYou">              
                <div id="hp_jfy">
                    <c:set var="justForYouTabLable"><c:out value="${justForYouTabLbl}" escapeXml="true"/></c:set>
                    <h2>${justForYouTabLable}</h2>                   
                    <div id="certonaJustForYou" class="categoryProductTabsData" >
                        <dsp:include page="/browse/certona_prod_carousel.jsp">
                            <dsp:param name="productsVOsList" value="${certona_justForYouProductsList}"/>
                            <dsp:param name="crossSellFlag" value="true"/>
                            <dsp:param name="desc" value="${justForYouTabLbl} ${omniCrossSellPageName}"/>
                     </dsp:include>
                 </div>
            </div>
        </div>
        </c:if>

		
    <%--BBBSL-6574 | Printing Certona WS call on source --%>
		<div id="requestResponse" class="hidden">
		     <c:choose>
	            <c:when test="${not empty errorMsg }">
	            			   <!--
					 <div id="certonaRequestResponse" class="hidden"> 
						<ul> 
							<li id="requestURL"><dsp:valueof value="${requestURL}" valueishtml="true" /></li>
							<li id="errorMsg"><dsp:valueof value="${errorMsg}" valueishtml="true" /></li>
						</ul> 
					</div>  
					 --> 
	            </c:when>
	            <c:otherwise>
	             <!--
						 <div id="certonaRequestResponse" class="hidden"> 
							<ul> 
								<li id="requestURL"><dsp:valueof value="${requestURL}" valueishtml="true" /></li>
								<li id="responseXML"><dsp:valueof value="${responseXML}" valueishtml="true"/></li>  
							</ul>
						</div>
						 -->
	            </c:otherwise>
	          </c:choose>
			  
		</div> 
		 

        <%-- has one of the tabs' content (clearance-deals, last-viewed, just-for-you) (not empty certona_alsoChkOutProductsVOsList) || not empty certona_topRegistryProductsVOsList)}--%>
        <c:if test="${(certonaBottomTabsFlag eq true) && ((clearanceBackUpEmptyFlag eq false) || (not empty lastviewedProductsList) || (not empty certona_justForYouProductsList))}">
            
			<div id="certonaBottomTabs"> 
			
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
			
                <dsp:droplet name="PageTabsOrderingDroplet">
                    <dsp:param name="pageName" value="${certonaPageName}"/>
                    <dsp:oparam name="output">
                        <dsp:getvalueof var="tabList" vartype="java.util.List" param="pageTab"/>
                        <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                            <dsp:param param="pageTab" name="array"/>
                            <dsp:oparam name="output">
                                <dsp:getvalueof var="tabName" param="element"/>
                                <c:choose>
                                    <%-- has clearance items --%>
                                    <c:when test="${(tabName eq clearanceTab) && (clearanceDealsFlagParam eq true) && (clearanceBackUpEmptyFlag eq false)}">
                                    	<c:set var="clearanceTabLable"><c:out value="${clearanceTabLbl}" escapeXml="true"/></c:set>
                                        <c:if test="${hasTabs eq false}"><c:set var="hasTabs" value="true"/><div class="categoryProductTabs grid_12 clearfix"><ul role="tablist" class="categoryProductTabsLinks"></c:if>
                                        <c:set var="clearanceDealsFlag" value="true"/>
                                        <li role="tab"><div class="arrowSouth"></div><a title="${clearanceTabLable}" href="#categoryProductTabs-tabs1">${clearanceTabLable}</a></li>
                                    </c:when>

                                    <%-- has last viewed items --%>
                                    <c:when test="${(tabName eq lastViewedTab) && (not empty lastviewedProductsList)}">
                                    	<c:set var="lastViewedTabLable"><c:out value="${lastViewedTabLbl}" escapeXml="true"/></c:set>
                                        <c:if test="${hasTabs eq false}"><c:set var="hasTabs" value="true"/><div class="categoryProductTabs grid_12 clearfix"><ul role="tablist" class="categoryProductTabsLinks"></c:if>
                                        <li role="tab"><div class="arrowSouth"></div><a title="${lastViewedTabLable}" href="#categoryProductTabs-tabs2">${lastViewedTabLable}</a></li>
                                    </c:when>

                                    <%-- has just for your items --%>
                                    <c:when test="${(tabName eq justForYouTab) && (justForYouFlagParam eq true) && (displayFlag eq true) && (not empty certona_justForYouProductsList) && (certonaPageName ne 'Home Page')}">
                                    	<c:set var="justForYouTabLable"><c:out value="${justForYouTabLbl}" escapeXml="true"/></c:set>
                                        <c:if test="${hasTabs eq false}"><c:set var="hasTabs" value="true"/><div class="categoryProductTabs grid_12 clearfix"><ul role="tablist" class="categoryProductTabsLinks"></c:if>
                                        <c:set var="justForYouFlag" value="true"/> 
                                        <li role="tab"><div class="arrowSouth"></div><a title="${justForYouTabLable}" href="#categoryProductTabs-tabs3">${justForYouTabLable}</a></li>
                                    </c:when>
                                  
								 <%-- has just Also Checkout items --%>	
                                     <c:when test="${(tabName eq alsoCheckOutTab) && (alsoCheckOutFlagParam eq true) && (not empty certona_alsoChkOutProductsVOsList) && (displayFlag eq true)}">
									<c:if test="${hasTabs eq false}"><c:set var="hasTabs" value="true"/><div class="categoryProductTabs grid_12 clearfix"><ul role="tablist" class="categoryProductTabsLinks"></c:if>
								        <c:set var="alsoCheckOutFlag" value="true"/> 
								        <li role="tab"><div class="arrowSouth"></div><a title="${alsoCheckoutTabLbl}" href="#categoryProductTabs-tabs4">${alsoCheckoutTabLbl}</a></li>
								    </c:when>
								    
							     <%-- has just Top College items --%>	    	
								    <c:when test="${(tabName eq topCollegeItemsTab) && (topCollegeItemsFlagParam eq true) && (not empty certona_topRegistryProductsVOsList) && (displayFlag eq true)}">
									    <c:if test="${hasTabs eq false}"><c:set var="hasTabs" value="true"/><div class="categoryProductTabs grid_12 clearfix"><ul role="tablist" class="categoryProductTabsLinks"></c:if>
                                        <c:set var="topCollegeItemsFlag" value="true"/> 
                                        <li role="tab"><div class="arrowSouth"></div><a title="${topCollegeItemsTabLbl}" href="#categoryProductTabs-tabs5">${topCollegeItemsTabLbl}</a></li>
                                    </c:when>
                                </c:choose>
                            </dsp:oparam>
                        </dsp:droplet>
                    </dsp:oparam>
                </dsp:droplet>

                <c:if test="${hasTabs eq true}">
                    </ul>
                    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                        <dsp:param value="${tabList}" name="array"/>
                        <dsp:oparam name="output">
                            <dsp:getvalueof var="tabName" param="element"/>

                            <c:choose>

                                <%-- clearance deals items --%>
                                <c:when test="${(tabName eq clearanceTab) && (clearanceDealsFlag eq true)}">
                                	<c:choose>
										<c:when test="${certonaPageName eq 'Category Landing Page'}">
											<div id="clp_cd">
										</c:when>
										<c:when test="${certonaPageName eq 'Home Page'}">
											<div id="hp_cd">
										</c:when>
										<c:otherwise>
										</c:otherwise>
									</c:choose>
	                                    <div id="categoryProductTabs-tabs1" class="categoryProductTabsData">
	                                        <c:choose>
	                                            <%-- clearance deals items from certona --%>
	                                            <c:when test="${(not empty certona_clearanceDealProductsList) && (displayFlag eq true)}">
	                                                <dsp:include page="/browse/certona_prod_carousel.jsp">
	                                                    <dsp:param name="productsVOsList" value="${certona_clearanceDealProductsList}"/>
	                                                    <dsp:param name="desc" value="${clearanceTabLbl} ${omniCrossSellPageName}"/>
	                                                    <dsp:param name="crossSellFlag" value="true"/>
	                                                </dsp:include>
	                                            </c:when>
	
	                                            <%-- clearance deals items from backup list --%>
	                                            <c:otherwise>
	                                                <dsp:include page="/browse/certona_prod_carousel.jsp">
	                                                    <dsp:param name="productsVOsList" value="${backup_clearanceDealProductsList}"/>
	                                                    <dsp:param name="desc" value="${clearanceTabLbl} ${omniCrossSellPageName}"/>
	                                                    <dsp:param name="crossSellFlag" value="true"/>
	                                                </dsp:include>
	                                            </c:otherwise>
	                                        </c:choose>
	                                    </div>
	                                <c:if test="${certonaPageName eq 'Category Landing Page' || certonaPageName eq 'Home Page'}">
										</div>
									</c:if>
                                </c:when>

                                <%-- last viewed items --%>
                                <c:when test="${(tabName eq lastViewedTab) && (not empty lastviewedProductsList)}">
                                    <div id="categoryProductTabs-tabs2" class="categoryProductTabsData">
                                        <dsp:include page="/browse/certona_prod_carousel.jsp">
                                            <dsp:param name="productsVOsList" value="${lastviewedProductsList}"/>
                                            <dsp:param name="desc" value="Last Viewed ${omniCrossSellPageName}"/>
                                            <dsp:param name="crossSellFlag" value="true"/>
                                        </dsp:include>
                                    </div>
                                </c:when>

                                <%-- just for you items --%>
                                <c:when test="${(tabName eq justForYouTab) && (justForYouFlag eq true)}">
                                	<c:choose>
										<c:when test="${certonaPageName eq 'Category Landing Page'}">
											<div id="clp_jfy">
										</c:when>
										<c:when test="${certonaPageName eq 'Home Page'}">
											<div id="hp_jfy">
										</c:when>
										<c:otherwise>
										</c:otherwise>
									</c:choose>
	                                    <div id="categoryProductTabs-tabs3" class="categoryProductTabsData">
	                                        <dsp:include page="/browse/certona_prod_carousel.jsp">
	                                            <dsp:param name="productsVOsList" value="${certona_justForYouProductsList}"/>
	                                            <dsp:param name="crossSellFlag" value="true"/>
	                                            <dsp:param name="desc" value="${justForYouTabLbl} ${omniCrossSellPageName}"/>
	                                        </dsp:include>
	                                    </div>
	                                <c:if test="${certonaPageName eq 'Category Landing Page' || certonaPageName eq 'Home Page'}">
										</div>
									</c:if>
                                </c:when>
                               <%-- Also Checkout items --%>
                                	<c:when test="${(tabName eq alsoCheckOutTab) && (alsoCheckOutFlag eq true) && (not empty certona_alsoChkOutProductsVOsList)}"> 
                                	  	<c:if test="${certonaPageName eq 'College Landing Page'}">
											<div id="clp_aco">
										</c:if>
										<c:set var="alsoCheckoutLabel"><bbbl:label key='lbl_also_checkout' language="${pageContext.request.locale.language}" /></c:set>
											<div id="categoryProductTabs-tabs4" class="categoryProductTabsData">
											    <dsp:include page="/browse/certona_prod_carousel.jsp">
													<dsp:param name="productsVOsList" value="${certona_alsoChkOutProductsVOsList}"/>
													<dsp:param name="crossSellFlag" value="true"/> 
													<dsp:param name="desc" value="${alsoCheckoutLabel}"/>
												</dsp:include>
											</div>
										<c:if test="${certonaPageName eq 'College Landing Page'}">
											</div>
										</c:if>
									    </c:when>
								 <%-- Top College items --%>	    
									<c:when test="${(tabName eq topCollegeItemsTab) && (topCollegeItemsFlag eq true) && (not empty certona_topRegistryProductsVOsList)}">
										<c:if test="${certonaPageName eq 'College Landing Page'}">
											<div id="clp_tci">
										</c:if>  
										<c:set var="topCollegeItemLabel"><bbbl:label key='lbl_top_college_items' language="${pageContext.request.locale.language}" /></c:set>
									 		 <div id="categoryProductTabs-tabs5" class="categoryProductTabsData">
												    <dsp:include page="/browse/certona_prod_carousel.jsp">
													  	<dsp:param name="productsVOsList" value="${certona_topRegistryProductsVOsList}"/>
													  	<dsp:param name="crossSellFlag" value="true"/> 
													  	<dsp:param name="desc" value="${topCollegeItemLabel}"/>
													</dsp:include>
											  </div>
										<c:if test="${certonaPageName eq 'College Landing Page'}">
											</div>
										</c:if>
									</c:when>
                            </c:choose>
                        </dsp:oparam>
                    </dsp:droplet>
                    </div>
                </c:if>
            </div>
        </c:if>


        <%-- certona JS call --%>
        <script type="text/javascript">
            setTimeout(function(){
                resx.appid = "${appIdCertona}";
                resx.pageid = "${pageIdCertona}";
                resx.customerid = "${userId}";
                resx.links = '${linksCertona}' + '${linksCertonaNonRecomm}' +'${productList}';

                if (typeof BBB.loadCertonaJS === "function") { BBB.loadCertonaJS(); }
            }, 100);
        </script>
		jjjjjerrorLIst on certona : 
	<dsp:getvalueof var="errorList" bean="SystemErrorInfo.errorList"/>
    </div>
	
</dsp:page>
