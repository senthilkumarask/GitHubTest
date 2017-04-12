<%--
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  cartItems.jsp
 *
 *  DESCRIPTION: fragment on coupons page to show a different view of cart
 *
 *  HISTORY:
 *  Dec 1, 2011  Initial version

--%>
<dsp:page>
    <dsp:importbean bean="/atg/commerce/ShoppingCart" />
    <div id="custCart" class="clearfix" style="display:none">
        <ul class="productsListHeader noBorderTop">
            <li class="grid_2"><strong><bbbl:label key="lbl_cartdetail_item" language="${language}"/></strong></li>
            <li class="grid_1 alpha textRight"><strong><bbbl:label key="lbl_cartdetail_quantity" language="${language}"/></strong></li>
            <li class="grid_2 textRight"><strong><bbbl:label key="lbl_cartdetail_unitprice" language="${language}"/></strong></li>
			<li class="grid_2 alpha textRight"><strong><bbbl:label key="lbl_you_pay" language="${language}"/></strong></li>
            <li class="grid_1 omega textRight"><strong><bbbl:label key="lbl_cartdetail_totalprice" language="${language}"/></strong></li>
        </ul>
        <ul class="cartProducts">
            <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                <dsp:param name="array" bean="ShoppingCart.current.commerceItems" />
                <dsp:param name="elementName" value="commItem" />                            
                <dsp:oparam name="output">
                    <dsp:droplet name="/atg/dynamo/droplet/Compare">
                      <dsp:param name="obj1" param="commItem.repositoryItem.type"/>
                      <dsp:param name="obj2" value="bbbCommerceItem"/>
                      <dsp:oparam name="equal">
                      <dsp:getvalueof param="commItem" var="commItem"/>
                      <dsp:getvalueof bean="ShoppingCart.current.registryMap.${commItem.registryId}" var="registratantVO"/>
                        <c:choose>
                            <c:when test="${commItem.registryId ne null}">
                                 <li class="registeryItem cartRow">
                                    <div class="registeryItemHeader clearfix">
                                    	<div class="grid_4 noMar">
                                            <span><bbbl:label key="lbl_cart_registry_from_text" language="${language}"/></span>                                                
                                            <span>${registratantVO.primaryRegistrantFirstName}<c:if test="${not empty registratantVO.coRegistrantFirstName }">&nbsp;&amp;&nbsp;${registratantVO.coRegistrantFirstName}</c:if><bbbl:label key="lbl_cart_registry_name_suffix" language="${language}"/></span>                                       
                                            <span><dsp:valueof value="${registratantVO.registryType.registryTypeDesc}"/></span>
                                            <span><bbbl:label key="lbl_cart_registry_text" language="${language}"/></span>
                                        </div>
                                    </div>
                                    <dsp:include page="/cart/cart_lineitem.jsp"/>
                                </li>
                            </c:when>
                            <c:otherwise>
                                <li class="cartRow">
                                    <dsp:include page="/cart/cart_lineitem.jsp"/>
                                </li>
                            </c:otherwise>
                        </c:choose>
                      </dsp:oparam>
                    </dsp:droplet>
                </dsp:oparam>                
            </dsp:droplet>
        </ul>
    </div>
</dsp:page>