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


    <%-- init default values/variables --%>
    <c:set var="TBS_BedBathUSSite" scope="request"><bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys"/></c:set>
    <c:set var="TBS_BuyBuyBabySite" scope="request"><bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys"/></c:set>
    <c:set var="TBS_BedBathCanadaSite" scope="request"><bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys"/></c:set>
    <c:set var="justForYouTab" scope="request"><dsp:valueof bean="PageTabsOrderingDroplet.justForYou"/></c:set>
    <c:set var="lastViewedTab" scope="request"><dsp:valueof bean="PageTabsOrderingDroplet.lastViewedItems"/></c:set>
    <c:set var="clearanceTab" scope="request"><dsp:valueof bean="PageTabsOrderingDroplet.clearanceDeals"/></c:set>
    <c:set var="scene7Path" scope="request"><bbbc:config key="scene7_url" configName="ThirdPartyURLs"/></c:set>
    <c:set var="appid" scope="request"><dsp:valueof bean="Site.id"/></c:set>
    <c:set var="appIdCertona" scope="request"><dsp:valueof bean="CertonaConfig.siteIdAppIdMap.${appid}"/></c:set>


    <%-- set default flags --%>
    <c:set var="clearanceDealsFlag" value="false"/>
    <c:set var="clearanceBackUpEmptyFlag" value="false"/>
    <c:set var="justForYouFlag" value="false"/>
    <c:set var="certonaBottomTabsFlag" value="true"/>
    <c:set var="funNewProductsFlag" value="false"/>
    <c:set var="displayFlag" value="true"/>
    <c:set var="certonaDefaultFlag" value="true"/>
    <c:set var="hasTabs" value="false"/>
    <%-- <c:choose>
        <c:when test="${currentSiteId eq TBS_BedBathUSSite}"><c:set var="CertonaOn" scope="request"><tpsw:switch tagName="CertonaTag_us"/></c:set></c:when>
        <c:when test="${currentSiteId eq TBS_BuyBuyBabySite}"><c:set var="CertonaOn" scope="request"><tpsw:switch tagName="CertonaTag_baby"/></c:set></c:when>
        <c:otherwise><c:set var="CertonaOn" scope="request"><tpsw:switch tagName="CertonaTag_ca"/></c:set></c:otherwise>
    </c:choose> --%>


    <%-- capture url params --%>
    <dsp:getvalueof var="categoryId" param="categoryId"/>
    <dsp:getvalueof var="scheme" param="scheme"/>
    <dsp:getvalueof var="number" param="number"/>
    <dsp:getvalueof var="lastViewedTabLbl" param="lastViewedTabLbl"/>
    <dsp:getvalueof var="justForYouTabLbl" param="justForYouTabLbl"/>
    <dsp:getvalueof var="clearanceTabLbl" param="clearanceTabLbl"/>
    <dsp:getvalueof var="certVO_fnp" param="fnp"/>
    <dsp:getvalueof var="certVO_cd" param="cd"/>
    <dsp:getvalueof var="certVO_jfy" param="jfy"/>
    <dsp:getvalueof var="clearanceDealsFlagParam" param="clearanceDealsFlagParam"/>
    <dsp:getvalueof var="justForYouFlagParam" param="justForYouFlagParam"/>
    <dsp:getvalueof var="funNewProductsFlagParam" param="funNewProductsFlagParam"/>
    <dsp:getvalueof var="certonaBottomTabsFlagParam" param="certonaBottomTabsFlagParam"/>
    <dsp:getvalueof var="omniCrossSellPageName" param="omniCrossSellPageName"/>
    <dsp:getvalueof var="certonaPageName" param="certonaPageName"/>
    <dsp:getvalueof var="linksCertonaNonRecomm" param="linksCertonaNonRecomm"/>
    <dsp:getvalueof var="certonaSwitch" param="certonaSwitch"/>

	
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
	            <dsp:getvalueof var="linksCertona" param="certonaResponseVO.resxLinks"/>
	            <dsp:getvalueof var="pageIdCertona" param="certonaResponseVO.pageId"/>
	        </dsp:oparam>
	        <dsp:oparam name="error">
	            <c:set var="displayFlag" value="false"/>
	        </dsp:oparam>
	        <dsp:oparam name="empty">
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
                        <dsp:include page="/cms/some_fun_new_products_certona.jsp" >
                            <dsp:param name="funNewProductsVOsList" value="${certona_funNewProductsList}"/>
                        </dsp:include>
                    </c:when>
                    <%-- doesn't have fun new products from certona ... show backup products --%>
                    <c:otherwise>
                        <dsp:include page="/cms/some_fun_new_products.jsp"/>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>

        <%-- has one of the tabs' content (clearance-deals, last-viewed, just-for-you) --%>
        <c:if test="${(certonaBottomTabsFlag eq true) && ((clearanceBackUpEmptyFlag eq false) || (not empty lastviewedProductsList) || (not empty certona_justForYouProductsList))}">
            <div id="certonaBottomTabs">
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
                                        <c:if test="${hasTabs eq false}"><c:set var="hasTabs" value="true"/><div class="categoryProductTabs grid_12 clearfix"><ul class="categoryProductTabsLinks tabs radius" data-tab=""></c:if>
                                        <c:set var="clearanceDealsFlag" value="true"/>
                                        <li class="tab-title tab"><div class="arrowSouth"></div><a title="${clearanceTabLbl}" href="#categoryProductTabs-tabs1">${clearanceTabLbl}</a></li>
                                    </c:when>

                                    <%-- has last viewed items --%>
                                    <c:when test="${(tabName eq lastViewedTab) && (not empty lastviewedProductsList)}">
                                        <c:if test="${hasTabs eq false}"><c:set var="hasTabs" value="true"/><div class="categoryProductTabs grid_12 clearfix"><ul class="categoryProductTabsLinks tabs radius" data-tab=""></c:if>
                                        <li class="tab-title tab active"><div class="arrowSouth"></div><a title="${lastViewedTabLbl}" href="#categoryProductTabs-tabs2">${lastViewedTabLbl}</a></li>
                                    </c:when>

                                    <%-- has just for your items --%>
                                    <c:when test="${(tabName eq justForYouTab) && (justForYouFlagParam eq true) && (displayFlag eq true) && (not empty certona_justForYouProductsList)}">
                                        <c:if test="${hasTabs eq false}"><c:set var="hasTabs" value="true"/><div class="categoryProductTabs grid_12 clearfix"><ul class="categoryProductTabsLinks tabs radius" data-tab=""></c:if>
                                        <c:set var="justForYouFlag" value="true"/> 
                                        <li class="tab-title tab"><div class="arrowSouth"></div><a title="${justForYouTabLbl}" href="#categoryProductTabs-tabs3">${justForYouTabLbl}</a></li>
                                    </c:when>
                                </c:choose>
                            </dsp:oparam>
                        </dsp:droplet>
                    </dsp:oparam>
                </dsp:droplet>

                <c:if test="${hasTabs eq true}">
                    </ul>
                    <div class="tabs-content">
                    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                        <dsp:param value="${tabList}" name="array"/>
                        <dsp:oparam name="output">
                            <dsp:getvalueof var="tabName" param="element"/>
                            <c:choose>
                                <c:when test="${tabName eq 'Also Check Out'}"></c:when>

                                <c:when test="${tabName eq 'Top College Items'}"></c:when>

                                <c:when test="${tabName eq 'Customer Also Viewed'}"></c:when>

                                <c:when test="${tabName eq 'Frequently Bought With'}"></c:when>

                                <%-- clearance deals items --%>
                                <c:when test="${(tabName eq clearanceTab) && (clearanceDealsFlag eq true)}">
                                    <div id="categoryProductTabs-tabs1" class="categoryProductTabsData content">
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
                                </c:when>

                                <%-- last viewed items --%>
                                <c:when test="${(tabName eq lastViewedTab) && (not empty lastviewedProductsList)}">
                                    <div id="categoryProductTabs-tabs2" class="categoryProductTabsData content active">
                                        <dsp:include page="/browse/certona_prod_carousel.jsp">
                                            <dsp:param name="productsVOsList" value="${lastviewedProductsList}"/>
                                            <dsp:param name="desc" value="Last Viewed ${omniCrossSellPageName}"/>
                                            <dsp:param name="crossSellFlag" value="true"/>
                                        </dsp:include>
                                    </div>
                                </c:when>

                                <%-- just for you items --%>
                                <c:when test="${(tabName eq justForYouTab) && (justForYouFlag eq true)}">
                                    <div id="categoryProductTabs-tabs3" class="categoryProductTabsData content">
                                        <dsp:include page="/browse/certona_prod_carousel.jsp">
                                            <dsp:param name="productsVOsList" value="${certona_justForYouProductsList}"/>
                                            <dsp:param name="crossSellFlag" value="true"/>
                                            <dsp:param name="desc" value="${justForYouTabLbl} ${omniCrossSellPageName}"/>
                                        </dsp:include>
                                    </div>
                                </c:when>

                            </c:choose>
                        </dsp:oparam>
                    </dsp:droplet>
                    </div>
                    </div>
                </c:if>
            </div>
        </c:if>


        <%-- certona JS call --%>
        <script type="text/javascript">
        $(document).ready(function() {
            /*slick sllider for PDP carouselContent*/
            $('.carouselContent').each(function(){
                var _this = $(this);
                if(_this.hasClass('slick-initialized')){
                    return;
                }
                _this.slick({
                    infinite: true,
                    slidesToShow: 4,
                    slidesToScroll: 4,
                    dots: true,
                    responsive: [
                    {
                        breakpoint: 455,
                        settings: {
                            slidesToShow: 1,
                            slidesToScroll: 1
                        }
                    }
                    ]
                });
            });
            $(document).foundation('reflow');
        });
            setTimeout(function(){
                resx.appid = "${appIdCertona}";
                resx.pageid = "${pageIdCertona}";
                resx.customerid = "${userId}";
                resx.links = '${linksCertona}' + '${linksCertonaNonRecomm}' +'${productList}';

                if (typeof BBB.loadCertonaJS === "function") { BBB.loadCertonaJS(); }
            }, 100);
        </script>
    </div>
</dsp:page>
