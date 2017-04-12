<dsp:page>

    <%-- Imports --%>
    <dsp:importbean bean="/com/bbb/account/OrderSummaryDetails" />
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/com/bbb/account/TrackingInfoDroplet" />
    <dsp:importbean bean="/atg/dynamo/droplet/Compare" />

    <%-- Variables --%>
    <c:set var="scene7Path">
        <bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
    </c:set>
    <c:set var="narvarFlag"><bbbc:config key="Narvar_TrackingFlag" configName="FlagDrivenFunctions" /></c:set>
    <dsp:getvalueof var="orderNum" param="orderNum" />
    <dsp:getvalueof var="orderType" param="orderType" />
    <dsp:getvalueof var="emailIdMD5" param="emailIdMD5" />
    <dsp:getvalueof var="legacyOrder" param="legacyOrder"/>

    <dsp:droplet name="OrderSummaryDetails">
        <dsp:param name="orderNum" value="${orderNum}"/>
        <dsp:param name="emailIdMD5" value="${emailIdMD5}"/>
        <dsp:param name="wsOrder" value="${legacyOrder}"/>
        <dsp:oparam name="output">
            <dsp:getvalueof var="BBBTrackOrderVO" param="BBBTrackOrderVO"/>
            <c:set var="TrackOrderVO" value="${BBBTrackOrderVO}" scope="request"/>
            <c:if test="${not empty TrackOrderVO.bbbOrderVO}">
                <dsp:getvalueof var="order" value="${TrackOrderVO.bbbOrderVO}" scope="request"/>
                <c:set var="order" value="${order}" scope="request"/>
                <c:set var="orderId" value="${order.orderId}" scope="request"/>
                <c:set var="orderEmailId" value="${OrderEmail}" scope="request"/>
                <c:set var="orderStatus" value="${order.orderStatus}" scope="request"/>
                <c:set var="orderType" value="${orderType}" scope="request"/>
                <c:set var="shippingGroups" value="${order.shippingGroups}" scope="request"/>
                <c:set var="registryMap" value="${order.registryMap}" scope="request"/>
                <c:set var="commerceItemVOList" value="${order.commerceItemVOList}" scope="request"/>
                <c:set var="carrierUrlMap" value="${order.carrierUrlMap}" scope="request"/>
                <c:set var="trackingInfos" value="${order.trackingInfos}" scope="request"/>
                <c:set var="orderPriceInfoDisplayVO" value="${order.orderPriceInfoDisplayVO}" scope="request"/>
                <c:set var="orderType" value="bopus" scope="request"/>
                <c:if test="${OrderNum eq order.onlineOrderNumber}">
                    <c:set var="orderType" value="online" scope="request"/>
                </c:if>
            </c:if>

            <c:set var="hardgoodShippingGroupCount" value="0" scope="request" />
            <c:set var="storePickupShippingGroupCount" value="0" scope="request" />
            <c:set var="hardgoodShipping" value="hardgoodShippingGroup" scope="request" />
            <c:set var="storeShipping" value="storeShippingGroup" scope="request" />

            <dsp:droplet name="ForEach">
                <dsp:param name="array" value="${shippingGroups}" />
                <dsp:param name="elementName" value="shippingGroup" />
                <dsp:oparam name="output">
                    <dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
                    <c:if test="${shippingGroup.shippingGroupType eq hardgoodShipping}">
                        <c:set var="hardgoodShippingGroupCount" value="${hardgoodShippingGroupCount+1}" />
                    </c:if>
                    <c:if test="${shippingGroup.shippingGroupType eq storeShipping}">
                        <c:set var="storePickupShippingGroupCount" value="${storePickupShippingGroupCount+1}" />
                    </c:if>
                </dsp:oparam>
            </dsp:droplet>

            <c:choose>
				<c:when test="${orderType eq 'online' && shippingGroup.shippingGroupType eq 'hardgoodShippingGroup'}">
                    <%-- Non BOPUS Starts here --%>
                    <c:set var="shipmentCount" value="0" />
                    <dsp:droplet name="ForEach">
                        <dsp:param name="array" value="${shippingGroups}" />
                        <dsp:param name="elementName" value="shippingGroup" />
                        <dsp:oparam name="output">
                            <c:set var="shipmentSeperator" value="shipmentSummary" />
                            <dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
                            <dsp:droplet name="Compare">
                                <dsp:param name="obj1" param="shippingGroup.shippingGroupType"/>
                                <dsp:param name="obj2" value="hardgoodShippingGroup"/>
                                <dsp:oparam name="equal">
                                    <dsp:getvalueof var="commerceItemRelationshipCount" param="shippingGroup.commerceItemRelationshipCount"/>
                                    <c:set var="shipmentCount" value="${shipmentCount+1}" />
                                    <%-- Shipping Items details Start here --%>
                                    <c:if test="${commerceItemRelationshipCount gt 0}">
                                        <dsp:droplet name="TrackingInfoDroplet">
                                            <dsp:oparam name="orderOutput">
                                                <dsp:getvalueof var="carrierURL" param="carrierURL"/>
                                            </dsp:oparam>
                                        </dsp:droplet>
                                        <div class="row">
                                            <div class="small-12 large-8 large-push-4 columns">
                                                <dsp:include page="track_order_items.jsp">
                                                    <dsp:param name="commerceItemVOList" value="${commerceItemVOList}"/>
                                                    <dsp:param name="registryMap" value="${order.registryMap}"/>
                                                    <dsp:param name="isTransient" value="true"/>
                                                    <dsp:param name="fromOrderSummary" value="true"/>
                                                </dsp:include>
                                            </div>
                                            <div class="small-12 large-4 large-pull-8 columns">
                                                <c:if test="${hardgoodShippingGroupCount gt 1}">
                                                    <h4><bbbl:label key="lbl_trackorder_shipment" language='${pageContext.request.locale.language}'/>&nbsp;<c:out value='${shipmentCount}'/></h4>
                                                </c:if>
                                                <dl>
                                                    <dt><bbbl:label key="lbl_trackorder_shipping_method" language='${pageContext.request.locale.language}'/>&nbsp;</dt>
                                                    <dd>${shippingGroup.shippingMethodDescription}</dd>
                                                </dl>
                                                <%-- Start trackingNumber --%>
                                                <c:choose>
                                                    <c:when test="${orderStatus eq 'cancelled' || orderStatus eq 'canceled'}">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <dsp:droplet name="ForEach">
                                                            <dsp:param name="array" value="${shippingGroup.trackingInfoVOList}" />
                                                            <dsp:param name="elementName" value="TrackingInfo" />
                                                            <dsp:oparam name="output">
                                                                <dsp:getvalueof var="trackingNumber" param="TrackingInfo.trackingNumber" />
                                                                <dsp:getvalueof var="carrierName" param="TrackingInfo.carrier" />
                                                                <dsp:getvalueof var="imageKey" value="${carrierName}_Image"/>
                                                                <dsp:getvalueof var="trackKey" value="${carrierName}_Track"/>
                                                                <dsp:getvalueof var="carrierImageURL" value="${carrierURL[imageKey]}"/>
                                                                <dsp:getvalueof var="carrierTrackUrl" value="${carrierURL[trackKey]}"/>
                                                                <c:choose>
																	<c:when test="${narvarFlag eq true && shippingGroup.shippingMethod ne 'SDD'}">
																		<c:set var="narvarUrl"><bbbc:config key="Narvar_TrackingUrl" configName="ThirdPartyURLs" /></c:set>
																		<c:set var="narvarTrackParam"><bbbc:config key="Narvar_Track_Num_Param" configName="ThirdPartyURLs" /></c:set>
																		<dsp:getvalueof var="trackURL" value="${narvarUrl}${fn:toLowerCase(carrierName)}${narvarTrackParam}${trackingNumber}"/>
																	</c:when>
																	<c:otherwise>
																		<dsp:getvalueof var="trackURL" value="${carrierTrackUrl}${trackingNumber}"/>
																	</c:otherwise>
																</c:choose>
                                                                <dl>
                                                                    <dt><bbbl:label key="lbl_trackorder_tracking" language='${pageContext.request.locale.language}'/>&nbsp;#:&nbsp;</dt>
                                                                    <c:choose>
                                                                        <c:when test="${carrierTrackUrl  ne null && not empty carrierTrackUrl}">
                                                                            <dd class="tracking-id"><a href="${trackURL}" target="_blank"><img alt="${carrierName}" src="${imagePath}${carrierImageURL}" align="left"/>${trackingNumber}</a></dd>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <dd class="tracking-id">${trackingNumber}</li></dd>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </dl>
                                                            </dsp:oparam>
                                                        </dsp:droplet>
                                                    </c:otherwise>
                                                </c:choose>
                                                <%-- End tracking Number --%>
                                                <dl>
                                                    <dt><bbbl:label key="lbl_preview_giftoptions" language="<c:out param='${language}'/>"/>:&nbsp;</dt>
                                                    <c:choose>
                                                        <c:when test="${shippingGroup.containsGiftWrapMessage}">
                                                            <dd class="fl tracking-id see-gift">
                                                                <a href="javascript:void(0);" rel="GM${shipmentCount}"><bbbl:label key="lbl_trackorder_gift_message_link" language="<c:out param='${language}'/>"/>
                                                                </a>
                                                               <%--  <span data-tooltip aria-haspopup="true" id="GM${shipmentCount}" class="has-tip tip-bottom gift_message" title="<c:out value='${shippingGroup.giftWrapMessage}'/>"><bbbl:label key="lbl_trackorder_gift_message_link" language="<c:out param='${language}'/>"/></span> --%>
                                                            <div class="gift_message hidden" id="GM${shipmentCount}">
                                                                    <span class="arrow">&nbsp;</span>
                                                                    <a href="javascript:void(0);" class="close"></a>
                                                                    <h3><bbbl:label key="lbl_trackorder_gift_message" language="<c:out param='${language}'/>"/></h3>
                                                                    <p><c:out value='${shippingGroup.giftWrapMessage}'/></p>
                                                                </div>
                                                            </dd>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <dd><bbbl:label key="lbl_trackorder_gift_none" language="<c:out param='${language}'/>"/></dd>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </dl>
                                                <dl>
                                                    <dt><bbbl:label key="lbl_trackorder_ship_head" language="<c:out param='${language}'/>"/></dt>
                                                    <c:choose>
                                                        <c:when test="${not empty shippingGroup.shippingAddress.registryId}">
                                                            <c:forEach var="entry" items="${registryMap}">
                                                            	<c:if test="${shippingGroup.shippingAddress.registryId eq entry.value.registryId}">
                                                            		<dd>
                                                            			<bbbl:label key="lbl_trackorder_ship_regAddress" language='${pageContext.request.locale.language}'/>&nbsp;${entry.value.primaryRegistrantFirstName}
                                                            			<c:if test="${entry.value.coRegistrantFirstName ne null}">
		                                                                 	&amp; ${entry.value.coRegistrantFirstName}
		                                                                </c:if>
		                                                                <bbbl:label key="lbl_cart_registry_name_suffix" language="${language}"/>
		                                                                <dsp:valueof value="${entry.value.registryType.registryTypeDesc}"/>
		                                                                <bbbl:label key="lbl_cart_registry_text" language="${language}"/>
                                                            		</dd>
                                                            		
                                                            	</c:if>
                                                            </c:forEach>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <dd>
                                                                <ul class="address">
                                                                    <li><dsp:valueof param="shippingGroup.shippingAddress.firstName" valueishtml="true"/> <dsp:valueof param="shippingGroup.shippingAddress.lastName" valueishtml="true"/></li>
                                                                    <c:if test="${shippingGroup.shippingAddress.companyName != ''}">
                                                                        <li><dsp:valueof param="shippingGroup.shippingAddress.companyName" valueishtml="true"/><li>
                                                                    </c:if>
                                                                    <li><dsp:valueof param="shippingGroup.shippingAddress.address1" valueishtml="true"/></li>
                                                                    <c:if test="${shippingGroup.shippingAddress.address2 != ''}">
                                                                        <li><dsp:valueof param="shippingGroup.shippingAddress.address2" valueishtml="true"/></li>
                                                                    </c:if>
                                                                    <li><dsp:valueof param="shippingGroup.shippingAddress.city" valueishtml="true"/>, <dsp:valueof param="shippingGroup.shippingAddress.state" /> <dsp:valueof param="shippingGroup.shippingAddress.postalCode" valueishtml="true"/></li>
                                                                    <c:if test="${shippingGroup.shippingAddress.phoneNumber != ''}">
                                                                    	<li><dsp:valueof param="shippingGroup.shippingAddress.phoneNumber" valueishtml="true"/></li>
                                                                    </c:if>
                                                                    <c:if test="${shippingGroup.shippingAddress.alternatePhoneNumber != ''}">
																		<li><dsp:valueof param="shippingGroup.shippingAddress.alternatePhoneNumber" valueishtml="true"/></li>
																	</c:if>
																	<c:if test="${shippingGroup.shippingAddress.email != ''}">
																		<li><dsp:valueof param="shippingGroup.shippingAddress.email" valueishtml="true"/></li>
																	</c:if>
                                                                </ul>
                                                            </dd>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </dl>
                                                <c:set var="orderDetailsURL" value="order_detail.jsp?orderId=${orderId}" />
                                                <c:set var="lbl_orderhistory_vieworder" ><bbbl:label key="lbl_orderhistory_vieworder" language="${pageContext.request.locale.language}" /></c:set>
                                                <a href="${orderDetailsURL}" title="${lbl_orderhistory_vieworder}" class="small button service"><bbbl:label key="lbl_orderhistory_vieworder" language="${pageContext.request.locale.language}" /></a>
                                            </div>
                                        </div>
                                    </c:if>
                                    <%-- Shipping Items details Ends here --%>
                                </dsp:oparam>
                            </dsp:droplet>
                        </dsp:oparam>
                        <dsp:oparam name="error">
                            <bbbe:error key="err_orderhistory_techerr" language="${pageContext.request.locale.language}" />
                        </dsp:oparam>
                    </dsp:droplet>
                    <%-- Non BOPUS Ends here --%>
                </c:when>
                <c:otherwise>
                    <%-- BOPUS Starts here --%>
                    <c:set var="shipmentCount" value="0" />
                    <dsp:droplet name="ForEach">
                        <dsp:param name="array" value="${shippingGroups}" />
                        <dsp:param name="elementName" value="shippingGroup" />
                        <dsp:oparam name="output">
                            <c:set var="shipmentSeperator" value="shipmentSummary" />
                            <dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
                            <dsp:droplet name="Compare">
                                <dsp:param name="obj1" param="shippingGroup.shippingGroupType"/>
                                <dsp:param name="obj2" value="storeShippingGroup"/>
                                <dsp:oparam name="equal">
                                <dsp:getvalueof var="commerceItemRelationshipCount" param="shippingGroup.commerceItemRelationshipCount"/>
                                <c:set var="shipmentCount" value="${shipmentCount+1}" />
                                <%-- Items pick from store details Starts here --%>
                                    <c:if test="${empty showLinks}">
                                        <c:if test="${shipmentCount eq 1}">
                                            <c:set var="shipmentSeperator" value="clearfix" />
                                        </c:if>
                                        <div class="row">
                                            <div class="small-12 large-8 large-push-4 columns">
                                                <dsp:include page="track_order_items.jsp">
                                                    <dsp:param name="commerceItemVOList" value="${commerceItemVOList}"/>
                                                    <dsp:param name="registryMap" value="${order.registryMap}"/>
                                                    <dsp:param name="fromOrderSummary" value="true"/>
                                                </dsp:include>
                                            </div>
                                            <div class="small-12 large-4 large-pull-8 columns">
                                                <c:if test="${storePickupShippingGroupCount gt 1}">
                                                    <h4><bbbl:label key="lbl_trackorder_shipment" language='${pageContext.request.locale.language}'/>&nbsp;<c:out value='${shipmentCount}'/></h4>
                                                </c:if>
                                                <dl>
                                                    <dt><bbbl:label key="lbl_trackorder_shipping_method" language='${pageContext.request.locale.language}'/>&nbsp;</dt>
                                                    <dd><bbbl:label key="lbl_trackorder_ship_store" language='${pageContext.request.locale.language}'/></dd>
                                                </dl>
                                                <dsp:droplet name="/com/bbb/selfservice/SearchStoreDroplet">
                                                    <dsp:param name="storeId" param="shippingGroup.storeId" />
                                                    <dsp:oparam name="output">
                                                        <dl>
                                                            <dt><bbbl:label key="lbl_trackorder_store_method" language='${pageContext.request.locale.language}'/></dt>
                                                            <dd>
                                                                <ul class="address">
                                                                    <li><dsp:valueof param="StoreDetails.storeName"/></li>
                                                                    <li><dsp:valueof param="StoreDetails.address"/></li>
                                                                    <li><dsp:valueof param="StoreDetails.city"/>, <dsp:valueof param="StoreDetails.state"/>&nbsp;<dsp:valueof param="StoreDetails.postalCode"/></li>
                                                                </ul>
                                                            </dd>
                                                        </dl>
                                                    </dsp:oparam>
                                                </dsp:droplet>
                                                <c:set var="orderDetailsURL" value="order_detail.jsp?orderId=${orderId}" />
                                                <c:set var="lbl_orderhistory_vieworder" ><bbbl:label key="lbl_orderhistory_vieworder" language="${pageContext.request.locale.language}" /></c:set>
                                                <a href="${orderDetailsURL}" title="${lbl_orderhistory_vieworder}" class="small button service"><bbbl:label key="lbl_orderhistory_vieworder" language="${pageContext.request.locale.language}" /></a>
                                            </div>
                                        </div>
                                    </c:if>
                                    <%-- Items pick from store details Ends here --%>
                                </dsp:oparam>
                            </dsp:droplet>
                        </dsp:oparam>
                    </dsp:droplet>
                <%-- BOPUS Ends here --%>
                </c:otherwise>
            </c:choose>
        </dsp:oparam>
        <dsp:oparam name="error">
            <bbbe:error key="err_orderhistory_techerr" language="${pageContext.request.locale.language}" />
        </dsp:oparam>
    </dsp:droplet>

</dsp:page>
