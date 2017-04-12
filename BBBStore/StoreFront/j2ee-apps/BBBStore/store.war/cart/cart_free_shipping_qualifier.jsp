<dsp:page>
    <dsp:importbean bean="/atg/commerce/promotion/ClosenessQualifierDroplet" />
    <dsp:importbean bean="/atg/commerce/order/droplet/ValidateClosenessQualifier" />
    <dsp:droplet name="ValidateClosenessQualifier">
		<dsp:param name="order" bean="/atg/commerce/ShoppingCart.current" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="result" param="result"/>
         </dsp:oparam>
       </dsp:droplet>
     <c:if test="${result}">
    <dsp:droplet name="ClosenessQualifierDroplet">
        <dsp:param name="type" value="shipping" />
        <dsp:param name="order" bean="/atg/commerce/ShoppingCart.current" />
        <dsp:oparam name="output">
            <ul id="miniCartBanner" class="noMar noPad">
                <li class="miniCartRow promoRow clearfix">
                    <dsp:getvalueof var="closenessQualifier" param="closenessQualifiers"/>
                    <dsp:param name="qualifier" value="${closenessQualifier[0]}" />
                    <dsp:getvalueof param="qualifier.name" var="promoName" />
                    <dsp:getvalueof param="qualifier.upsellMedia.type" var="promoImageType" />
                    <c:if test="${promoImageType ne 3 }">
                        <%-- <dsp:getvalueof param="qualifier.upsellMedia.url" var="promoImage" /> --%>
                        <%-- ##################### --%>
                        <%-- use this is its an image based banner --%>
                            <%-- <img class="bannerIMG" alt="${promoName}" src="${promoImage}" width="272" height="74" /> --%>
                        <%-- ##################### --%>
                        <%-- use this is its a text based banner --%>
                        <div class="bannerTXT">
                            <p class="promoText"><dsp:valueof param="qualifier.upsellMedia.description" valueishtml="true" /></p>
                        </div>
                        <%-- ##################### --%>
                    </c:if>
                    <div class="clear"></div>
                </li>
            </ul>
            <div class="clear"></div>
        </dsp:oparam>
    </dsp:droplet>
   </c:if>
</dsp:page>

