<dsp:page>

    <%-- Imports --%>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/userprofiling/Profile" var="Profile" />
    <dsp:importbean bean="/com/bbb/commerce/order/droplet/TBSOverrideReasonDroplet"/>
    <dsp:importbean bean="/com/bbb/commerce/order/droplet/TBSCommerceItemLookupDroplet"/>
    <dsp:importbean bean="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet"/>

    <%-- Page Variables --%>
    <dsp:getvalueof var="commItem" param="commItem"></dsp:getvalueof>

    <dsp:droplet name="BBBPriceDisplayDroplet">
        <dsp:param name="priceObject" value="${commItem.BBBCommerceItem}" />
        <dsp:param name="profile" bean="Profile"/>
        <dsp:oparam name="output">

            <dsp:getvalueof var="unitSavedAmount" param="priceInfoVO.unitSavedAmount"/>
            <dsp:getvalueof var="unitListPrice" param="priceInfoVO.unitListPrice"/>
            <dsp:getvalueof var="unitSalePrice" param="priceInfoVO.unitSalePrice"/>
            <dsp:getvalueof var="totalSavedAmount" param="priceInfoVO.totalSavedAmount"/>
            <dsp:getvalueof var="adjustmentsList" param="priceInfoVO.adjustmentsList"/>
            <dsp:getvalueof var="undiscountedItemsCount" param="priceInfoVO.undiscountedItemsCount"/>
            <dsp:getvalueof var="deliverySurcharge" param="priceInfoVO.deliverySurcharge"/>
            <dsp:getvalueof var="deliverySurchargeSaving" param="priceInfoVO.deliverySurchargeSaving"/>
            <dsp:getvalueof var="assemblyFee" param="priceInfoVO.assemblyFee"/>
            <dsp:getvalueof var="shippingMethodAvl" param="shippingMethodAvl"/>
            <dsp:getvalueof var="clearancePrice" param="clearancePrice"/>
            <dsp:getvalueof var="shippingMethodDescription" param="shippingMethodDescription"/> 
            
            <div class="small-6 large-3 columns no-padding-left">
                <dl class="totals">

                    <%-- our price --%>
                    <div class="dl-wrap">
                        <dt>
                            <bbbl:label key="lbl_cart_our_price" language="${language}"/>
                        </dt>
                        <dd class="our-price">
                            <a href="#" class="priceOverrideModal" data-reveal-id="priceOverrideModal_${commItem.BBBCommerceItem.id}"><fmt:formatNumber value="${unitListPrice}"  type="currency"/></a>
                        </dd>
                    </div>

                    <%-- your price --%>
                    <div class="dl-wrap">
                        <dt>
                            <bbbl:label key="lbl_cartdetail_yourprice" language="<c:out param='${language}'/>"/>
                        </dt>
                        <dd>
                            <dsp:include page="/cart/cart_includes/your_price.jsp">
                                <dsp:param name="unitSavedAmount" param="priceInfoVO"/>
                                <dsp:param name="cItem" value="${commItem.BBBCommerceItem}" />
                            </dsp:include>
                        </dd>
                        <c:if test="${not empty commItem.BBBCommerceItem.TBSItemInfo && commItem.BBBCommerceItem.TBSItemInfo.overridePrice > 0}">
                            <span class="overrides"><bbbl:label key="lbl_price_override_to" language="<c:out param='${language}'/>"/>
                                <fmt:formatNumber value="${commItem.BBBCommerceItem.TBSItemInfo.overridePrice}"  type="currency"/>
                            </span>
                        </c:if>
                    </div>

                    <c:if test="${unitSavedAmount gt 0.0 || totalSavedAmount gt 0.0 || couponApplied eq 'true'}">
                        <div class="dl-wrap">
                            <dt class="savings">
                                <bbbl:label key="lbl_cartdetail_yousave" language="<c:out param='${language}'/>"/>
                            </dt>
                            <dd class="savings">
                                <c:choose>
                                    <c:when test="${couponApplied ne 'true'}">
                                        <c:if test="${totalSavedAmount gt 0.0}">
                                            <fmt:formatNumber value="${totalSavedAmount}"  type="currency"/> <%-- (<dsp:valueof param="priceInfoVO.totalSavedPercentage" number="0.00"/>%) --%>
                                        </c:if>
                                    </c:when>
                                    <c:otherwise>
                                        <c:choose> 
                                            <c:when test="${unitSavedAmount gt 0.0 && totalSavedAmount gt 0.0 && totalSavedAmount ne unitSavedAmount}">
                                               <fmt:formatNumber value="${totalSavedAmount}"  type="currency"/>
                                            </c:when>
                                            <c:otherwise>
                                                <fmt:formatNumber value="${couponDiscountAmount + totalSavedAmount}"  type="currency"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:otherwise>
                                </c:choose>
                            </dd>
                        </div>
                        </c:if>    
                        <dsp:droplet name="ForEach">
                        <dsp:param value="${commItem.skuDetailVO.skuAttributes}" name="array" />
                        <dsp:param name="elementName" value="attributeVOList"/>
                        <dsp:oparam name="output">
                            <dsp:getvalueof var="placeholder" param="key"/>
                            <c:if test="${placeholder eq 'CRSL'}">
                                <dsp:droplet name="ForEach">
                                <dsp:param param="element" name="array" />
                                <dsp:param param="attributeVOList" name="array" />
                                <dsp:param name="sortProperties" value="+priority"/>
                                <dsp:param name="elementName" value="attributeVO"/>
                                <dsp:oparam name="output">
                                    <div class="dl-wrap">
                                        <dt></dt>
                                        <dd class="productAttributes prodAttribWrapper">
                                            <dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/>
                                        </dd>
                                    </div>
                                </dsp:oparam>
                                </dsp:droplet>
                            </c:if>
                        </dsp:oparam>
                        </dsp:droplet>
                    
                    
                    <c:if test="${commItem.skuDetailVO.giftWrapEligible}">
                        <div class="dl-wrap">
                            <dt></dt>
                            <dd class="savings">
                                <bbbl:label key="lbl_cartdetail_elegibleforgiftpackaging" language="<c:out param='${language}'/>"/>
                            </dd>
                        </div>
                    </c:if>

                    <c:if test="${deliverySurcharge eq 0.0 && shippingMethodAvl eq false && commItem.skuDetailVO.ltlItem }">
                        <div class="dl-wrap">
                            <dt class="savings">
                                + <bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/>:
                            </dt>
                            <dd class="savings">
                                TBD
                            </dd>
                        </div>
                    </c:if>
                    
                    <%-- total --%>
                    <div class="dl-wrap">
                        <dt>
                            <h1 class="price"><bbbl:label key="lbl_cartdetail_totalprice" language="<c:out param='${language}'/>"/></h1>
                        </dt>
                        <dd>
                            <h1 class="price">
                                <dsp:getvalueof param="priceInfoVO.totalAmount" var="totalAmount"/>                               
                                <fmt:formatNumber value="${totalAmount}"  type="currency"/>
                            </h1>
                        </dd>
                    </div>
					<dsp:getvalueof var="omniTotalPrice" param="priceInfoVO.totalAmount" vartype="java.lang.Double"/>
					<c:set var="omniTotalPrice"><dsp:valueof value="${omniTotalPrice}" converter="unformattedCurrency" /></c:set>
					<input name="omniTotalPrice" type="hidden" value="${omniTotalPrice}" class="omniTotalPriceHidden"/>
                    <%-- Additional info for LTL items --%>
                    <c:if test="${shippingMethodAvl && commItem.skuDetailVO.ltlItem}">
                        <div class="dl-wrap">
                            <dt class="ltlsavings">
                                + ${shippingMethodDescription}
                            </dt>
                            <dd class="ltlsavings">
                                <dsp:droplet name="TBSCommerceItemLookupDroplet">
                                    <dsp:param name="id" value="${commItem.BBBCommerceItem.deliveryItemId}"/>
                                    <dsp:param name="order" param="order"/>
                                    <dsp:param name="elementName" value="surchargeItem"/>
                                        <dsp:oparam name="output">
                                            <dsp:param param="surchargeItem" name="surchargeItem"/>
                                            <dsp:getvalueof param="surchargeItem.quantity" var="surchargeQty"/>
                                            <dsp:getvalueof param="listPrice" var="surchargeListPrice"/>
                                            <dsp:getvalueof param="displayName" var="SurchargeSkuName"/>
                                        </dsp:oparam>
                                </dsp:droplet>
                                <c:choose>
                                	<c:when test="${deliverySurcharge eq 0.0}">
                                		<bbbl:label key="lbl_shipping_free" language="<c:out param='${language}'/>"/>
                                	</c:when>
                                	<c:otherwise>
                                		<a href="#" class="surchargeOverrideModal" data-reveal-id="surchargeOverrideModal_${commItem.BBBCommerceItem.deliveryItemId}">
		                                    <fmt:formatNumber value="${deliverySurcharge}"  type="currency"/>
		                                </a>
                                	</c:otherwise>
                                </c:choose>
                            </dd>
                        </div>
                    </c:if>
                    <c:if test="${deliverySurchargeSaving gt 0.0}">
                        <div class="dl-wrap">
                            <dt class="savings">
                                - <bbbl:label key="lbl_cart_delivery_surcharge_saving" language="<c:out param='${language}'/>"/>:
                            </dt>
                            <dd class="savings">
                                (<fmt:formatNumber value="${deliverySurchargeSaving}"  type="currency"/>)
                            </dd>
                        </div>
                    </c:if>
                    <c:if test="${assemblyFee gt 0.0}">
                        <div class="dl-wrap">
                            <dt class="ltlsavings">
                                + <bbbl:label key="lbl_cart_assembly_fee" language="<c:out param='${language}'/>"/>:
                            </dt>
                            <dd class="ltltsavings">
                                <dsp:droplet name="TBSCommerceItemLookupDroplet">
                                    <dsp:param name="id" value="${commItem.BBBCommerceItem.assemblyItemId}"/>
                                    <dsp:param name="order" param="order"/>
                                    <dsp:param name="elementName" value="assemblyItem"/>
                                        <dsp:oparam name="output">
                                            <dsp:param param="assemblyItem" name="assemblyItem"/>
                                            <dsp:getvalueof param="assemblyItem.quantity" var="assemblyQty"/>
                                            <dsp:getvalueof param="listPrice" var="assemblyListPrice"/>
                                            <dsp:getvalueof param="displayName" var="assemblySkuName"/>
                                        </dsp:oparam>
                                </dsp:droplet>
                                <a href="#" class="assemblyFeeOverrideModal" data-reveal-id="assemblyFeeOverrideModal_${commItem.BBBCommerceItem.assemblyItemId}">
                                    <fmt:formatNumber value="${assemblyFee}"  type="currency"/>
                                </a>
                            </dd>
                        </div>
                    </c:if>
                    <%-- /Additional info for LTL items --%>
                </dl>
            </div>

            <div id="priceOverrideModal_${commItem.BBBCommerceItem.id}" class="reveal-modal medium price-override" data-reveal="">
                <div class="row">
                    <div class="small-12 columns no-padding">
                        <h1>Price Override</h1>
                        <p class="note">* required field</p>
                    </div>
                </div>
                <div class="row">
                    <div class="small-12 large-4 columns">
                        <label class="right inline">Item Description</label>
                    </div>
                    <div class="small-12 large-8 columns">
                        <input type="text" value="<c:out value='${commItem.skuDetailVO.displayName}' escapeXml='false' />" disabled />
                    </div>
                </div>
                <div class="row">
                    <div class="small-12 large-4 columns">
                        <label class="right inline">Quantity</label>
                    </div>
                    <div class="small-12 large-8 columns">
                        <input type="text" id="poQuantity" value="<c:out value='${commItem.BBBCommerceItem.quantity} 'escapeXml='false' />" disabled />
                    </div>
                </div>
                <div class="row">
                    <div class="small-12 large-4 columns">
                        <label class="right inline">Price per Unit</label>
                    </div>
                    <div class="small-12 large-8 columns">
                        <c:choose>
                            <c:when test="${unitSalePrice gt 0.0}">
                                <input type="text" class="unitListPrice" value="<fmt:formatNumber value='${unitSalePrice}'  type='currency'/>" disabled />
                            </c:when>
                            <c:otherwise>
                                <input type="text" class="unitListPrice" value="<fmt:formatNumber value='${unitListPrice}'  type='currency'/>" disabled />
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div class="row">
                    <div class="small-12 large-4 columns">
                        <label class="right inline">Quantity to Override</label>
                    </div>
                    <div class="small-12 large-8 columns quantity">
                        <div class="qty-spinner">
                            <a class="button minus secondary"><span></span></a>
                            <input id="qtyToOverride_${commItem.BBBCommerceItem.id}" class="quantity-input" type="text" maxlength="2" value="${commItem.BBBCommerceItem.quantity}" data-max-value="${commItem.BBBCommerceItem.quantity}" maxlength="2"/>
                            <a class="button plus secondary"><span></span></a>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="small-12 large-4 columns">
                        <label for="reasonList_${commItem.BBBCommerceItem.id}" class="right inline">Reason *</label>
                    </div>
                    <div class="small-12 large-8 columns">
                        <select name="reasonList_${commItem.BBBCommerceItem.id}" id="reasonList_${commItem.BBBCommerceItem.id}" class="reasonList">
                            <dsp:droplet name="TBSOverrideReasonDroplet">
                                <dsp:param name="OverrideType" value="item" />
                                <dsp:oparam name="output">
                                    <option value="">Select Reason</option>
                                    <dsp:droplet name="ForEach">
                                        <dsp:param name="array" param="reasons" />
                                        <dsp:param name="elementName" value="elementVal" />
                                        <dsp:oparam name="output">
                                           <option value="<dsp:valueof param='key'/>">
                                               <dsp:valueof param="elementVal" />
                                            </option>
                                        </dsp:oparam>
                                    </dsp:droplet>
                                </dsp:oparam>
                            </dsp:droplet>
                        </select>
                    </div>
                </div>
                <div class="row">
                    <div class="small-12 large-4 columns">
                        <label for="competitorList_${commItem.BBBCommerceItem.id}" class="right inline">Competitor</label>
                    </div>
                    <div class="small-12 large-8 columns">
                        <select name="competitorList_${commItem.BBBCommerceItem.id}" id="competitorList_${commItem.BBBCommerceItem.id}" class="competitorList">
                            <dsp:droplet name="TBSOverrideReasonDroplet">
                                <dsp:param name="OverrideType" value="competitors" />
                                <dsp:oparam name="output">
                                    <option value="">Select Competitor</option>
                                    <dsp:droplet name="ForEach">
                                        <dsp:param name="array" param="reasons" />
                                        <dsp:param name="elementName" value="elementVal" />
                                        <dsp:oparam name="output">
                                           <option value="<dsp:valueof param='key'/>">
                                             <dsp:valueof param="elementVal" />
                                           </option>
                                        </dsp:oparam>
                                    </dsp:droplet>
                                </dsp:oparam>
                            </dsp:droplet>
                        </select>
                        <small for="competitorList_${commItem.BBBCommerceItem.id}" class="error hidden" id="errorComp">Please select a value for Competitor</small>
                    </div>
                </div>
                <div class="row">
                    <div class="small-12 large-4 columns">
                        <label for="newPrice_${commItem.BBBCommerceItem.id}" class="right inline">New Price *</label>
                    </div>
                    <div class="small-12 large-8 columns">
                         <span class="dollarSignOverride">$</span>
                        <input type="tel" value="0.00" id="newPrice_${commItem.BBBCommerceItem.id}" name="newPrice_${commItem.BBBCommerceItem.id}" class="priceOverride newPrice" maxlength="11"/>
                    </div>
                </div>
                <div class="row">
                    <div class="small-12 large-offset-4 large-4 columns">
                        <a href='javascript:void(0);' class="button small service expand submit-price-override" data-parent="#priceOverrideModal_${commItem.BBBCommerceItem.id}" data-cid="${commItem.BBBCommerceItem.id}">Override</a>
                    </div>
                    <div class="small-12 large-2 columns left">
                        <a href='javascript:void(0);' class="close-modal small button secondary expand">Cancel</a>
                    </div>
                </div>
                <a class="close-reveal-modal">&times;</a>
            </div>
            
            <div id="surchargeOverrideModal_${commItem.BBBCommerceItem.deliveryItemId}" class="reveal-modal medium surcharge-override" data-reveal="">
            <div class="row">
                <div class="small-12 columns no-padding">
                    <h1>Surcharge Override</h1>
                    <p class="note">* required field</p>
                </div>
            </div>
            <div class="row">
                <div class="small-12 large-4 columns">
                    <label class="right inline">Item Description</label>
                </div>
                <div class="small-12 large-8 columns">
                    <input type="text" value="<c:out value='${SurchargeSkuName}' escapeXml='false' />" disabled />
                </div>
            </div>
            <div class="row">
                <div class="small-12 large-4 columns">
                    <label class="right inline">Quantity</label>
                </div>
                <div class="small-12 large-8 columns surchargeQty">
                     <input type="text" value="<c:out value='${surchargeQty}' escapeXml='false' />" disabled />
                </div>
            </div>
            <div class="row">
                <div class="small-12 large-4 columns">
                    <label class="right inline">Price per Unit</label>
                </div>
                <div class="small-12 large-8 columns price">
                    <input type="text" value="<fmt:formatNumber value='${surchargeListPrice}'  type='currency'/>" class="surchargeListPrice" disabled />
                </div>
            </div>
            <div class="row">
                <div class="small-12 large-4 columns">
                    <label class="right inline">Quantity to Override</label>
                </div>
                <div class="small-12 large-8 columns quantity">
                    <div class="qty-spinner">
                        <input id="qtyToOverride_${commItem.BBBCommerceItem.deliveryItemId}" class="quantity-input" type="text" data-max-value="99" maxlength="2" value="${surchargeQty}" data-max-value="${surchargeQty}" disabled="disabled">
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="small-12 large-4 columns">
                    <label for="reasonList_${commItem.BBBCommerceItem.deliveryItemId}" class="right inline">Reason *</label>
                </div>
                <div class="small-12 large-8 columns">
                    <select name="reasonList_${commItem.BBBCommerceItem.deliveryItemId}" id="reasonList_${commItem.BBBCommerceItem.deliveryItemId}" class="surchargeReasonList">
                        <dsp:droplet name="TBSOverrideReasonDroplet">
                            <dsp:param name="OverrideType" value="surcharge" />
                            <dsp:oparam name="output">
                                <option value="">Select Reason</option>
                                <dsp:droplet name="ForEach">
                                    <dsp:param name="array" param="reasons" />
                                    <dsp:param name="elementName" value="elementVal" />
                                    <dsp:oparam name="output">
                                          <option value="<dsp:valueof param='key'/>">
                                              <dsp:valueof param="elementVal" />
                                            </option>
                                        </dsp:oparam>
                                </dsp:droplet>
                            </dsp:oparam>
                        </dsp:droplet>
                    </select>
                </div>
            </div>
            <div class="row">
                <div class="small-12 large-4 columns">
                    <label for="competitorList_${commItem.BBBCommerceItem.deliveryItemId}" class="right inline">Competitor</label>
                </div>
                <div class="small-12 large-8 columns">
                    <select name="competitorList_${commItem.BBBCommerceItem.deliveryItemId}" id="competitorList_${commItem.BBBCommerceItem.deliveryItemId}" class="surchargeCompetitorList">
                        <dsp:droplet name="TBSOverrideReasonDroplet">
                            <dsp:param name="OverrideType" value="competitors" />
                            <dsp:oparam name="output">
                                <option value="">Select Competitor</option>
                                <dsp:droplet name="ForEach">
                                    <dsp:param name="array" param="reasons" />
                                    <dsp:param name="elementName" value="elementVal" />
                                    <dsp:oparam name="output">
                                            <option value="<dsp:valueof param='key'/>">
                                             <dsp:valueof param="elementVal" />
                                            </option>
                                        </dsp:oparam>
                                </dsp:droplet>
                            </dsp:oparam>
                        </dsp:droplet>
                    </select>
                </div>
            </div>
            <div class="row">
                <div class="small-12 large-4 columns">
                    <label for="newPrice_${commItem.BBBCommerceItem.deliveryItemId}" class="right inline">New Price *</label>
                </div>
                <div class="small-12 large-8 columns">
                    <span class="dollarSignOverride">$</span>
                    <input type="tel" value="0.00" id="newPrice_${commItem.BBBCommerceItem.deliveryItemId}" name="newPrice_${commItem.BBBCommerceItem.deliveryItemId}" class="priceOverride newSurchargePrice" maxlength="7"/>
                </div>
            </div>
            <div class="row">
                <div class="small-12 large-offset-4 large-4 columns">
                    <a href='javascript:void(0);' class="button small service expand submit-surcharge-override" data-parent="#priceOverrideModal_${commItem.BBBCommerceItem.deliveryItemId}" data-cid="${commItem.BBBCommerceItem.deliveryItemId}">Override</a>
                </div>
                <div class="small-12 large-4 columns">
                    <a href='javascript:void(0);' class="close-modal button secondary">Cancel</a>
                </div>
            </div>
            <a class="close-reveal-modal">&times;</a>
        </div>

        <div id="assemblyFeeOverrideModal_${commItem.BBBCommerceItem.assemblyItemId}" class="reveal-modal medium assemblyFee-override" data-reveal="">
            <div class="row">
                <div class="small-12 columns no-padding">
                    <h1>Assembly Fee Override</h1>
                    <p class="note">* required field</p>
                </div>
            </div>
            <div class="row">
                <div class="small-12 large-4 columns">
                    <label class="right inline">Item Description</label>
                </div>
                <div class="small-12 large-8 columns">
                    <input type="text" value="<c:out value='${assemblySkuName}' escapeXml='false' />" disabled />
                </div>
            </div>
            <div class="row">
                <div class="small-12 large-4 columns">
                    <label class="right inline">Quantity</label>
                </div>
                <div class="small-12 large-8 columns">
                    <input type="text" value="<c:out value='${assemblyQty}' escapeXml='false' />" disabled />
                </div>
            </div>
            <div class="row">
                <div class="small-12 large-4 columns">
                    <label class="right inline">Price per Unit</label>
                </div>
                <div class="small-12 large-8 columns">
                    <input type="text" value="<fmt:formatNumber value='${assemblyListPrice}'  type='currency'/>" class="assemblyListPrice" disabled />
                </div>
            </div>
            <div class="row">
                <div class="small-12 large-4 columns">
                    <label class="right inline">Quantity to Override</label>
                </div>
                <div class="small-12 large-8 columns quantity">
                    <div class="qty-spinner">
                        <input id="qtyToOverride_${commItem.BBBCommerceItem.assemblyItemId}" class="quantity-input" 
                            type="text" maxlength="2" value="${assemblyQty}" data-max-value="${assemblyQty}" class="button plus secondary" disabled="disabled">
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="small-12 large-4 columns">
                    <label for="reasonList_${commItem.BBBCommerceItem.assemblyItemId}" class="right inline">Reason *</label>
                </div>
                <div class="small-12 large-8 columns">
                    <select name="reasonList_${commItem.BBBCommerceItem.assemblyItemId}" id="reasonList_${commItem.BBBCommerceItem.assemblyItemId}" class="assemblyReasonList">
                        <dsp:droplet name="TBSOverrideReasonDroplet">
                            <dsp:param name="OverrideType" value="surcharge" />
                            <dsp:oparam name="output">
                                <option value="">Select Reason</option>
                                <dsp:droplet name="ForEach">
                                    <dsp:param name="array" param="reasons" />
                                    <dsp:param name="elementName" value="elementVal" />
                                    <dsp:oparam name="output">
                                          <option value="<dsp:valueof param='key'/>">
                                             <dsp:valueof param="elementVal" />
                                          </option>
                                        </dsp:oparam>
                                </dsp:droplet>
                            </dsp:oparam>
                        </dsp:droplet>
                    </select>
                </div>
            </div>
            <div class="row">
                <div class="small-12 large-4 columns">
                    <label for="competitorList_${commItem.BBBCommerceItem.assemblyItemId}" class="right inline">Competitor</label>
                </div>
                <div class="small-12 large-8 columns">
                    <select name="competitorList_${commItem.BBBCommerceItem.assemblyItemId}" id="competitorList_${assemblyItemId}" class="assemblyCompetitorList">
                        <dsp:droplet name="TBSOverrideReasonDroplet">
                            <dsp:param name="OverrideType" value="competitors" />
                            <dsp:oparam name="output">
                               <option value="">Select Competitor</option>
                                <dsp:droplet name="ForEach">
                                    <dsp:param name="array" param="reasons" />
                                    <dsp:param name="elementName" value="elementVal" />
                                    <dsp:oparam name="output">
                                           <option value="<dsp:valueof param='key'/>">
                                             <dsp:valueof param="elementVal" />
                                            </option>
                                        </dsp:oparam>
                                </dsp:droplet>
                            </dsp:oparam>
                        </dsp:droplet>
                    </select>
                </div>
            </div>
            <div class="row">
                <div class="small-12 large-4 columns">
                    <label for="newPrice_${commItem.BBBCommerceItem.assemblyItemId}" class="right inline">New Price *</label>
                </div>
                <div class="small-12 large-8 columns">
                    <span class="dollarSignOverride">$</span>
                    <input type="tel" value="0.00" id="newPrice_${commItem.BBBCommerceItem.assemblyItemId}" name="newPrice_${assemblyItemId}" class="priceOverride assemblyNewPrice" maxlength="7"/>
                </div>
            </div>
            <div class="row">
                <div class="small-12 large-offset-4 large-4 columns">
                    <a href='javascript:void(0);' class="button small service expand submit-assemblyFee-override" data-parent="#priceOverrideModal_${commItem.BBBCommerceItem.assemblyItemId}" data-cid="${commItem.BBBCommerceItem.assemblyItemId}">Override</a>
                </div>
                <div class="small-12 large-4 columns">
                    <a href='javascript:void(0);' class="close-modal">Cancel</a>
                </div>
            </div>
            <a class="close-reveal-modal">&times;</a>
        </div>
        
        </dsp:oparam>
    </dsp:droplet>

</dsp:page>
