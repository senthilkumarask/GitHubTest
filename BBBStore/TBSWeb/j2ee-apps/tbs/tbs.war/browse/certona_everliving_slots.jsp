<dsp:page>
    <dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProdToutDroplet" />
    <dsp:importbean bean="/com/bbb/commerce/browse/droplet/ExitemIdDroplet" />
    <dsp:importbean bean="/com/bbb/certona/droplet/CertonaDroplet" />
    <dsp:importbean bean="/com/bbb/certona/CertonaConfig" />
    <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
    <dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:importbean bean="/atg/multisite/Site" />
    <dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${appid}" />
    <dsp:getvalueof var="parentProductId" param="parentProductId" />
    <dsp:getvalueof var="productId" param="productId" />
    <dsp:getvalueof var="linkStringNonRecproduct" param="linkStringNonRecproduct" />
    <dsp:getvalueof var="appid" bean="Site.id" />
    <c:set var="CertonaContext" scope="request">${parentProductId};</c:set>
    <c:set var="SchemeName" value="pdp_cav;pdp_fbw" />
    <c:set var="CertonaOn" scope="request">
        <tpsw:switch tagName="CertonaTag_us" />
    </c:set>
    <c:set var="scene7Path" scope="request">
        <bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
    </c:set>
    <dsp:droplet name="Switch">
        <dsp:param name="value" bean="Profile.transient" />
        <dsp:oparam name="false">
            <dsp:getvalueof var="userId" bean="Profile.id" />
        </dsp:oparam>
        <dsp:oparam name="true">
            <dsp:getvalueof var="userId" value="" />
        </dsp:oparam>
    </dsp:droplet>
    <c:set var="relatedItemsDisplayFlag" value="false" />
    <c:set var="frequentlyBoughtDisplayFlag" value="false" />
    <dsp:droplet name="ProdToutDroplet">
        <dsp:param value="lastviewed" name="tabList" />
        <dsp:param param="categoryId" name="id" />
        <dsp:param value="${appid}" name="siteId" />
        <dsp:param name="productId" param="productId" />
        <dsp:oparam name="output">
            <div class="clearfix">
                <div id="certonaBottomTabs">
                    <div class="row">
                        <dsp:getvalueof var="clearanceProductsList" param="clearanceProductsList" />
                        <dsp:getvalueof var="lastviewedProductsList" param="lastviewedProductsList" />
                        <dsp:droplet name="ExitemIdDroplet">
                            <dsp:param value="${lastviewedProductsList}" name="lastviewedProductsList" />
                            <dsp:param name="certonaExcludedItems" value="${linkStringNonRecproduct}" />
                            <dsp:oparam name="output">
                                <dsp:getvalueof var="productList" param="productList" />
                            </dsp:oparam>
                        </dsp:droplet>
                        <c:if test="${CertonaOn}">
                            <c:set var="custAlsoViewedProdMax" scope="request">
                                <bbbc:config key="PDPCustAlsoViewProdMax" configName="CertonaKeys" />
                            </c:set>
                            <c:set var="frequentlyBuyProdMax" scope="request">
                                <bbbc:config key="PDPFreqBoughtProdMax" configName="CertonaKeys" />
                            </c:set>
                            <dsp:droplet name="CertonaDroplet">
                                <dsp:param name="scheme" value="${SchemeName}" />
                                <dsp:param name="userid" value="${userId}" />
                                <dsp:param name="context" value="${CertonaContext}" />
                                <dsp:param name="exitemid" value="${productList}" />
                                <dsp:param name="productId" value="${parentProductId}" />
                                <dsp:param name="siteId" value="${appid}" />
                                <dsp:param name="number" value="${custAlsoViewedProdMax};${frequentlyBuyProdMax}" />
                                <dsp:oparam name="output">
                                    <c:set var="schemeArray" value="${fn:split(SchemeName, ';')}" />
                                    <dsp:getvalueof var="relatedItemsProductsVOsList" param="certonaResponseVO.resonanceMap.${schemeArray[0]}.productsVOsList" />
                                    <dsp:getvalueof var="frequentlyBoughtProductsVOsList" param="certonaResponseVO.resonanceMap.${schemeArray[1]}.productsVOsList" />
                                    <dsp:getvalueof var="linksCertona" param="certonaResponseVO.resxLinks" />
                                    <dsp:getvalueof var="pageIdCertona" param="certonaResponseVO.pageId" />
                                </dsp:oparam>
                                <dsp:oparam name="error">
                                </dsp:oparam>
                                <dsp:oparam name="empty">
                                    <dsp:getvalueof var="isRobot" param="isRobot" />
                                    <c:if test="${isRobot}">
                                        <!-- CERTONA_REQUESTED_BY_ROBOT -->
                                    </c:if>
                                </dsp:oparam>
                            </dsp:droplet>
                        </c:if>
                        <c:if test="${(not empty relatedItemsProductsVOsList) || (not empty lastviewedProductsList) || (not empty frequentlyBoughtProductsVOsList)}">
                            <c:set var="customerAlsoViewedTab">
                                <bbbl:label key='lbl_pdp_product_related_items' language="${pageContext.request.locale.language}" />
                            </c:set>
                            <c:set var="lastViewedTab">
                                <bbbl:label key='lbl_pdp_product_last_viewed' language="${pageContext.request.locale.language}" />
                            </c:set>
                            <c:set var="frequentlyBoughtWithTab">
                                <bbbl:label key='lbl_pdp_product_frequently_bought' language="${pageContext.request.locale.language}" />
                            </c:set>
                            <%--<ul class="categoryProductTabsLinks">--%>
                                <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
                                    <dsp:param name="value" value="${relatedItemsProductsVOsList}" />
                                    <dsp:oparam name="false">
                                        <%--<li><div class="arrowSouth"></div> <a title="Customer Also Viewed" href="#botCrossSell-tabs1"><bbbl:label key='lbl_pdp_product_related_items'
													language="${pageContext.request.locale.language}" /></a></li>--%>
                                            <c:set var="relatedItemsDisplayFlag" value="true" />
                                    </dsp:oparam>
                                    <dsp:oparam name="true">
                                        <c:set var="relatedItemsDisplayFlag" value="false" />
                                    </dsp:oparam>
                                </dsp:droplet>
                                <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
                                    <dsp:param name="value" value="${frequentlyBoughtProductsVOsList}" />
                                    <dsp:oparam name="false">
                                        <%--<li><div class="arrowSouth"></div> <a title="Frequently Bought With" href="#botCrossSell-tabs2"><bbbl:label key='lbl_pdp_product_frequently_bought'
													language="${pageContext.request.locale.language}" /></a></li>--%>
                                            <c:set var="frequentlyBoughtDisplayFlag" value="true" />
                                    </dsp:oparam>
                                    <dsp:oparam name="true">
                                        <c:set var="frequentlyBoughtDisplayFlag" value="false" />
                                    </dsp:oparam>
                                </dsp:droplet>
                                <c:if test="${not empty lastviewedProductsList}">
                                    <%--<li><div class="arrowSouth"></div> <a title="Last Viewed Items" href="#botCrossSell-tabs3"><bbbl:label key='lbl_pdp_product_last_viewed'
												language="${pageContext.request.locale.language}" /></a></li>--%>
                                </c:if>
                                <%--</ul>--%>
                                    <ul data-tab="" class="tabs radius">
                                        <c:if test="${relatedItemsDisplayFlag eq 'true'}">
                                            <li class="tab-title tab active">
                                                <a href="#botCrossSell-tabs1">
                                                    <bbbl:label key='lbl_pdp_product_related_items' language="${pageContext.request.locale.language}" />
                                                </a>
                                            </li>
                                        </c:if>
                                        <c:if test="${frequentlyBoughtDisplayFlag eq 'true'}">
                                            <li class="tab-title tab <c:if test='${relatedItemsDisplayFlag eq " false "}'> active</c:if>">
                                                <a href="#botCrossSell-tabs2">
                                                    <bbbl:label key='lbl_pdp_product_frequently_bought' language="${pageContext.request.locale.language}" />
                                                </a>
                                            </li>
                                        </c:if>
                                        <c:if test="${not empty lastviewedProductsList}">
                                            <li class="tab-title tab <c:if test='${relatedItemsDisplayFlag eq " false " && frequentlyBoughtDisplayFlag eq "false "}'> active</c:if>">
                                                <a href="#botCrossSell-tabs3">
                                                    <bbbl:label key='lbl_pdp_product_last_viewed' language="${pageContext.request.locale.language}" />
                                                </a>
                                            </li>
                                        </c:if>
                                    </ul>
                                    <div class="tabs-content">
                                        <c:if test="${relatedItemsDisplayFlag eq 'true'}">
                                            <div id="botCrossSell-tabs1" class="categoryProductTabsData content active">
                                                <dsp:include page="certona_prod_carousel.jsp">
                                                    <dsp:param name="productsVOsList" value="${relatedItemsProductsVOsList}" />
                                                    <dsp:param name="crossSellFlag" value="true" />
                                                    <dsp:param name="desc" value="Customer Also Viewed (pdp)" />
                                                </dsp:include>
                                            </div>
                                        </c:if>
                                        <c:if test="${frequentlyBoughtDisplayFlag eq 'true'}">
                                            <div id="botCrossSell-tabs2" class="categoryProductTabsData content <c:if test='${relatedItemsDisplayFlag eq " false "}'> active</c:if>">
                                                <dsp:include page="certona_prod_carousel.jsp">
                                                    <dsp:param name="productsVOsList" value="${frequentlyBoughtProductsVOsList}" />
                                                    <dsp:param name="crossSellFlag" value="true" />
                                                    <dsp:param name="desc" value="Frequently Bought(pdp)" />
                                                </dsp:include>
                                            </div>
                                        </c:if>
                                        <c:if test="${not empty lastviewedProductsList}">
                                            <div id="botCrossSell-tabs3" class="categoryProductTabsData content <c:if test='${relatedItemsDisplayFlag eq " false " && frequentlyBoughtDisplayFlag eq "false "}'> active</c:if>">
                                                <dsp:include page="last_viewed.jsp">
                                                    <dsp:param name="lastviewedProductsList" value="${lastviewedProductsList}" />
                                                    <dsp:param name="desc" value="Last  Viewed Item (pdp)" />
                                                </dsp:include>
                                            </div>
                                        </c:if>
                                    </div>
                        </c:if>
                        <input id="certonaInStockLinks" type="hidden" value="${linksCertona}${linkStringNonRecproduct}${productList}" />
                        <input id="certonaOutOfStock" type="hidden" value="${linksCertonaForOOSscheme}${linkStringNonRecproduct}${productList}" />
                        <script type="text/javascript">
                        setTimeout(function() {
                            resx.event = "product";
                            resx.pageid = "${pageIdCertona}";
                            resx.itemid = '${linkStringNonRecproduct}';
                            resx.links = '${linksCertona}' + '${productList}';
                            if (typeof BBB.loadCertonaJS === "function") {
                                BBB.loadCertonaJS();
                            }
                        }, 100);

                        $(document).ready(function() {
                            $('.carouselContent').each(function() {
                                var _this = $(this);
                                if (_this.hasClass('slick-initialized')) {
                                    return;
                                }
                                _this.slick({
                                    infinite: true,
                                    slidesToShow: 4,
                                    slidesToScroll: 4,
                                    dots: true,
                                    responsive: [{
                                        breakpoint: 455,
                                        settings: {
                                            slidesToShow: 1,
                                            slidesToScroll: 1
                                        }
                                    }]
                                });
                            });

                            $(this).foundation('reflow');
                        });
                        </script>
                    </div>
               	</div>
            </div>
        </dsp:oparam>
    </dsp:droplet>
</dsp:page>
