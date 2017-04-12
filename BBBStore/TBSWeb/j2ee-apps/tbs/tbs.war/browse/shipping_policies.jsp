<dsp:page>
<dsp:importbean bean="/com/bbb/common/droplet/ShippingMethodDroplet" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:getvalueof var="siteId" bean="Site.id" />

<c:set var="TBS_BedBathUSSite">
	<bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" />
</c:set>
<c:set var="TBS_BuyBuyBabySite">
	<bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
</c:set>
<c:set var="TBS_BedBathCanadaSite">
	<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
</c:set>
<div class="shipping" title="Shipping Policy">
    <div class="listFix clearfix">
        <div class="width_8 clearfix">
            <c:if test="${siteId eq TBS_BedBathUSSite}">
            	<bbbt:textArea key="txt_shippingpage_top" language="${pageContext.request.locale.language}"></bbbt:textArea>
            </c:if>
            <div class="width_8 marAuto cf">
                <dsp:droplet name="ShippingMethodDroplet">
                    <dsp:param name="requestType" value="ShippingMethodDetails" />
                    <dsp:param name="siteId" value="${siteId}" />
                    <dsp:oparam name="output">
                        <dsp:getvalueof var="site" param="siteId"/>
                        <c:set var="classWidth">width_8</c:set>
                        <c:if test="${site eq TBS_BedBathCanadaSite}">
                            <c:set var="classWidth">width_5</c:set>
                        </c:if>
                        <dsp:droplet name="ForEach">
                            <dsp:param name="array" param="ShippingMethodDetails"/>
                            <dsp:param name="elementName" value="mapOfShipDetail"/>
                            <dsp:oparam name="outputStart">
                                <div class="${classWidth} clearfix">
                                    <ul class="${classWidth} clearfix noMar large-block-grid-3">
                                        <c:set var="varShippingUSA"><bbbl:label key="lbl_shippingpage_usa" language="${pageContext.request.locale.language}"></bbbl:label></c:set>
                                        <c:if test="${((site eq TBS_BedBathCanadaSite && not empty varShippingUSA) || site ne TBS_BedBathCanadaSite)}">
                                            <c:if test="${site ne TBS_BedBathCanadaSite}">
                                            	<li class="fl cb"><div class="width_2 fl bold">&nbsp;</div></li>
                                            	<li class="fl"><div class="width_3 fl bold"><bbbl:label key="lbl_shippingpage_usa" language="${pageContext.request.locale.language}"></bbbl:label></div></li>
                                                <li class="fl"><div class="width_3 fl bold"><bbbl:label key="lbl_shippingpage_alaska" language="${pageContext.request.locale.language}"></bbbl:label></div></li>
                                            </c:if>
                                        </c:if>
                            </dsp:oparam>
                            <dsp:oparam name="output">
                                <li class="fl cb"><div class="width_2 fl"><dsp:valueof param="count"/>.&nbsp;<dsp:valueof param="key"/>:</div></li>
                                <li class="fl"><div class="width_3 fl"><dsp:valueof param="mapOfShipDetail[0]"/>-<dsp:valueof param="mapOfShipDetail[1]"/>&nbsp;business days</div></li>
                                <c:if test="${site ne TBS_BedBathCanadaSite}">
                                    <li class="fl"><div class="width_3 fl"><dsp:valueof param="mapOfShipDetail[2]"/>-<dsp:valueof param="mapOfShipDetail[3]"/>&nbsp;business days</div></li>
                                </c:if>
                                <c:if test="${site eq TBS_BedBathCanadaSite}">
                                	<li class="fl cb"><div class="width_2 fl bold">&nbsp;</div></li>
                                </c:if>
                            </dsp:oparam>
                            <dsp:oparam name="outputEnd">
                                    </ul>
                                    <div class="clear"></div>
                                </div>
                                <div class="clear"></div>
                            </dsp:oparam>
                        </dsp:droplet>
                    </dsp:oparam>
                </dsp:droplet>
                <div class="clear"></div>
            </div>
            <div class="clear"></div>
        </div>
        <div class="clear"></div>

        <dsp:droplet name="ShippingMethodDroplet">
            <dsp:param name="requestType" value="ShippingPriceTableDetail" />
            <dsp:param name="siteId" value="${siteId}" />
            <dsp:oparam name="output">
                <div class="width_8 clearfix">
                    <bbbt:textArea key="txt_shippingpage_middle" language="${pageContext.request.locale.language}"></bbbt:textArea>
                    <div class="shippingTable width_8 marAuto clearfix small-12 medium-10 large-right" style="overflow-y:scroll;">
                        <dsp:droplet name="ForEach">
                            <dsp:param name="array" param="ShippingPriceTableDetail"/>
                            <dsp:param name="elementName" value="shippingTable"/>
                            <dsp:oparam name="output">
                                <dsp:getvalueof var="shippingTableVal" param="shippingTable"/>
                                <c:if test="${not empty shippingTableVal }">
                                    <dsp:droplet name="Switch">
                                        <dsp:param name="value" param="key"/>
                                        <dsp:oparam name="AL">
                                            <c:set var="tableTitle" scope="page">
                                                <bbbl:label key="lbl_shipping_policy_alaska" language ="${pageContext.request.locale.language}"/>
                                            </c:set>
                                        </dsp:oparam>
                                        <dsp:oparam name="OTHERS">
                                            <c:set var="tableTitle" scope="page">
                                                <bbbl:label key="lbl_shipping_policy_other" language ="${pageContext.request.locale.language}"/>
                                            </c:set>
                                        </dsp:oparam>
                                        <dsp:oparam name="US">
                                         <%--BBBSL-8934 | Shipping Policy Canada - label name --%>
                                                <c:set var="tableTitle" scope="page">
                                                    <bbbl:label key="lbl_shipping_policy_us" language ="${pageContext.request.locale.language}"/>
                                                </c:set>
                                        </dsp:oparam>
                                    </dsp:droplet>

                                    <div class="table width_5">
                                        <c:if test="${not empty tableTitle}">
                                            <p class="bold">${tableTitle}</p>
                                        </c:if>
                                        <table title="${tableTitle}">
                                            <caption></caption>
                                            <tbody>
                                                <tr>
                                                    <dsp:droplet name="ForEach">
                                                        <dsp:param name="array" param="shippingMethodsTable"/>
                                                        <dsp:param name="elementName" value="shippingTableHeader"/>
                                                        <dsp:oparam name="outputStart">
                                                            <th class="width_2 textLeft" scope="col"><bbbl:label key="lbl_shippingInfo_totalamount" language ="${pageContext.request.locale.language}"/></th>
                                                        </dsp:oparam>
                                                        <dsp:oparam name="output">
                                                            <th class="width_1 textCenter" scope="col"><dsp:valueof param="shippingTableHeader"/></th>
                                                        </dsp:oparam>
                                                    </dsp:droplet>
                                                </tr>

                                                <dsp:droplet name="ForEach">
                                                    <dsp:param name="array" param="shippingTable"/>
                                                    <dsp:param name="elementName" value="shippingTableRow"/>
                                                    <dsp:oparam name="output">
                                                        <tr>
                                                            <dsp:getvalueof var="count" param="count"/>
                                                            <dsp:getvalueof var="size" param="size"/>

                                                            <c:choose>
                                                                <c:when test="${count eq size }">
                                                                    <td class="width_2 textLeft"><dsp:valueof param="shippingTableRow.displayRangeMax"/></td>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <td class="width_2 textLeft"><dsp:valueof param="shippingTableRow.displayRange"/></td>
                                                                </c:otherwise>
                                                            </c:choose>

                                                            <dsp:droplet name="ShippingMethodDroplet">
                                                                <dsp:param name="requestType" value="sortShippingPrice" />
                                                                <dsp:param name="AppShipMethodPriceVOs" param="shippingTableRow.appShipMethodPriceVO"/>
                                                                <dsp:oparam name="sort">
                                                                    <dsp:droplet name="ForEach">
                                                                        <dsp:param name="array" param="sortedAppShipMethodPriceVO"/>
                                                                        <dsp:param name="elementName" value="shippingTableRowEle"/>
                                                                        <dsp:oparam name="output">
                                                                            <td class="width_1 textCenter"><dsp:valueof param="shippingTableRowEle.price"/></td>
                                                                        </dsp:oparam>
                                                                    </dsp:droplet>
                                                                </dsp:oparam>
                                                            </dsp:droplet>
                                                        </tr>
                                                    </dsp:oparam>
                                                </dsp:droplet>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:if>
                            </dsp:oparam>
                        </dsp:droplet>
                        <div class="clear"></div>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
            </dsp:oparam>
        </dsp:droplet>
        <div class="clear"></div>
    </div>
    <div class="clear"></div>
</div>
</dsp:page>