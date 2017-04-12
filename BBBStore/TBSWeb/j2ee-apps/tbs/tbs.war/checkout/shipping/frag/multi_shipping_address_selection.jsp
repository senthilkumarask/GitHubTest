<dsp:page>
    <dsp:importbean bean="/com/bbb/commerce/order/droplet/CommerceItemCheckDroplet"/>
    <dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
    <dsp:importbean bean="/com/bbb/commerce/shipping/droplet/DisplayMultiShippingAddress"/>
    <dsp:importbean bean="/atg/userprofiling/Profile"/>
    <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
    <dsp:importbean bean="/com/bbb/commerce/checkout/MultiShippingAddContainer"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    
    <c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
    <dsp:getvalueof var="index" param="cisiIndex" />
    <dsp:getvalueof var="ltlItemFlag" param="ltlItemFlag" />
    <%-- <input type="hidden" value="${ltlItemFlag}" name="ltlFlag"/> --%>
    <% pageContext.setAttribute("newLineChar", "\n"); %>

    <dsp:droplet name="CommerceItemCheckDroplet">
    <dsp:param name="commerceItem" param="cisi.commerceItem" />
    <dsp:oparam name="true">
        <h3 class="checkout-title no-margin-top">Ship To</h3>

        <div class="select <c:if test='${ltlItemFlag}'>ltlItem</c:if>">
            <dsp:getvalueof var="registryId" bean="BBBShippingGroupFormhandler.cisiItems[${index}].commerceItem.registryId" />

            <dsp:droplet name="DisplayMultiShippingAddress">
            <dsp:param name="profile" bean="Profile"/>
            <dsp:param name="shippingGroupName" bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingGroupName"/>
            <dsp:param name="order" bean="ShoppingCart.current"/>
            <dsp:param name="addressContainer" bean="MultiShippingAddContainer"/>
            <dsp:param name="siteId" bean="ShoppingCart.current.siteId"/>
            <dsp:oparam name="output">
                <dsp:getvalueof var="registryMap" bean="ShoppingCart.current.registryMap" />
                <c:if test="${not empty registryId}">
                <c:set var="regSelect">registrySelect</c:set>
                </c:if>

                <dsp:select bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingGroupName" id="shippingAddress_${index}" iclass="addressSelect ${regSelect} addNewAddressDataSource" >
                    <dsp:getvalueof var="shippingGroupName" bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingGroupName"/>
                    <dsp:getvalueof var="defaultAddId" param="defaultAddId" />
                    <dsp:getvalueof var="latestAddID" param="latestAddID" />
                    <dsp:getvalueof var="latestAddressMap" param="latestAddressMap" />
                    <dsp:getvalueof var="addressMap" param="addressContainer.addressMap" />

                    <dsp:droplet name="ForEach">
                    <dsp:param name="array"  param="keys"/>
                    <dsp:getvalueof var="addressKey" param="element" />
                    <dsp:oparam name="outputStart">
                        <option value="">
                            <bbbl:label key="lbl_shipping_select_address" language="<c:out param='${language}'/>"/>
                        </option>
                    </dsp:oparam>
                    <dsp:oparam name="output">
                        <c:choose>
                            <c:when test="${fn:contains(addressKey, 'registry')}">
                                <dsp:getvalueof var="registryId" value="${fn:substring(addressKey, 8, fn:length(addressKey))}" />
                                <dsp:getvalueof var="registryVO" value="${registryMap[registryId]}" />
                                <c:if test="${(registryVO ne null)}">
                                    <c:choose>
                                        <c:when test="${(addressKey eq defaultAddId) or (addressKey eq shippingGroupName)}">
                                            <option value="${addressKey}" selected="selected" data-alternatePhoneNumber="${addressMap[addressKey].alternatePhoneNumber}" data-phonenumber="${addressMap[addressKey].phoneNumber}" data-email="${addressMap[addressKey].email}" data-firstName="${addressMap[addressKey].firstName}" data-middleName="${addressMap[addressKey].middleName}" data-lastName="${addressMap[addressKey].lastName}" data-companyName="${addressMap[addressKey].companyName}" data-address1="${addressMap[addressKey].address1}" data-address2="${addressMap[addressKey].address2}" data-city="${addressMap[addressKey].city}" data-state="${addressMap[addressKey].state}" data-zipCode="${addressMap[addressKey].postalCode}"><c:out value='${addressMap[addressKey].registryInfo}'/>:${registryId}</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${addressKey}" data-alternatePhoneNumber="${addressMap[addressKey].alternatePhoneNumber}" data-phonenumber="${addressMap[addressKey].phoneNumber}" data-email="${addressMap[addressKey].email}" data-firstName="${addressMap[addressKey].firstName}" data-middleName="${addressMap[addressKey].middleName}" data-lastName="${addressMap[addressKey].lastName}" data-companyName="${addressMap[addressKey].companyName}" data-address1="${addressMap[addressKey].address1}" data-address2="${addressMap[addressKey].address2}" data-city="${addressMap[addressKey].city}" data-state="${addressMap[addressKey].state}" data-zipCode="${addressMap[addressKey].postalCode}"><c:out value='${addressMap[addressKey].registryInfo}'/>:${registryId}</option>
                                        </c:otherwise>
                                    </c:choose>
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <c:set var="tempCompanyName" value="${addressMap[addressKey].companyName}" />
                                <c:choose>
                                    <c:when test="${not empty tempCompanyName}">
                                        <c:set var="tempAddress" value="${addressMap[addressKey].firstName} ${addressMap[addressKey].lastName} (${addressMap[addressKey].companyName}): " />
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="tempAddress" value="${addressMap[addressKey].firstName} ${addressMap[addressKey].lastName}: " />
                                    </c:otherwise>
                                </c:choose>

                                <c:set var="tempAddress2" value="${addressMap[addressKey].address2}" />
                                <c:set var="tempAddress1" value="${addressMap[addressKey].address1}" />
                                <c:if test="${not empty tempAddress1}">
                                    <c:choose>
                                        <c:when test="${not empty tempAddress2}">
                                            <c:set var="address" value="${tempAddress},${addressMap[addressKey].address1},${addressMap[addressKey].address2} ,${addressMap[addressKey].city} ,${addressMap[addressKey].state} ${addressMap[addressKey].postalCode} ,${addressMap[addressKey].phoneNumber}" />
                                        </c:when>
                                        <c:otherwise>
                                            <c:set var="address" value="${tempAddress},${addressMap[addressKey].address1},${addressMap[addressKey].city} ,${addressMap[addressKey].state} ${addressMap[addressKey].postalCode} ,${addressMap[addressKey].phoneNumber}" />
                                        </c:otherwise>
                                    </c:choose>

                                    <c:choose>
                                        <c:when test="${((addressKey eq defaultAddId) or (addressKey eq shippingGroupName)) && (not ltlItemFlag)}">
                                            <option value="${addressKey}" selected="selected" data-alternatePhoneNumber="${addressMap[addressKey].alternatePhoneNumber}" data-phonenumber="${addressMap[addressKey].phoneNumber}" data-email="${addressMap[addressKey].email}" data-firstName="${addressMap[addressKey].firstName}" data-middleName="${addressMap[addressKey].middleName}" data-lastName="${addressMap[addressKey].lastName}" data-companyName="${addressMap[addressKey].companyName}" data-address1="${addressMap[addressKey].address1}" data-address2="${addressMap[addressKey].address2}" data-city="${addressMap[addressKey].city}" data-state="${addressMap[addressKey].state}" data-zipCode="${addressMap[addressKey].postalCode}"><c:out value='${fn:replace(address, newLineChar, ",")}'/>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${addressKey}" data-alternatePhoneNumber="${addressMap[addressKey].alternatePhoneNumber}" data-phonenumber="${addressMap[addressKey].phoneNumber}" data-email="${addressMap[addressKey].email}" data-firstName="${addressMap[addressKey].firstName}" data-middleName="${addressMap[addressKey].middleName}" data-lastName="${addressMap[addressKey].lastName}" data-companyName="${addressMap[addressKey].companyName}" data-address1="${addressMap[addressKey].address1}" data-address2="${addressMap[addressKey].address2}" data-city="${addressMap[addressKey].city}" data-state="${addressMap[addressKey].state}" data-zipCode="${addressMap[addressKey].postalCode}"><c:out value='${fn:replace(address, newLineChar, ",")}'/>
                                        </c:otherwise>
                                    </c:choose>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </dsp:oparam>
                    <dsp:oparam name="empty">
                        <option value="">
                            <bbbl:label key="lbl_shipping_select_address" language="<c:out param='${language}'/>"/>
                        </option>
                    </dsp:oparam>
                    </dsp:droplet>

                    <option value="addNewAddressOption" class="addNewAddressOpt"><bbbl:label key="lbl_shipping_add_new_address" language="${pageContext.request.locale.language}"/></option>
                </dsp:select>
                <input type="hidden" name="cisiIndex" value="${index}" class="addNewAddressIndexSource" />
                <input type="hidden" value="${ltlItemFlag}" id="isLTL" name="ltlFlag"/>
            </dsp:oparam>
            </dsp:droplet>
        </div>

    </dsp:oparam>
    </dsp:droplet>

</dsp:page>
