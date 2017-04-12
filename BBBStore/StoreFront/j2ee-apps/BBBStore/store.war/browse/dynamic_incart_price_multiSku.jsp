<dsp:page>
<dsp:getvalueof var="pDefaultChildSku" param="pDefaultChildSku"/>
<c:set var="dynamicPriceClass" value="isPrice"/>
<c:if test="${not empty pDefaultChildSku}">
<c:set var="dynamicPriceClass" value=""/>
</c:if>
<ul class="inCartPricing hidden ${dynamicPriceClass}">
      <li class="red fontSize_18 bold skuPrice hidden"></li>
      <li class="red fontSize_14 bold inCartMsg hidden"><bbbl:label key="lbl_discounted_incart_text" language="${pageContext.request.locale.language}" /></li>
      <li class="grayText fontSize_11 bold wasOrigMsg hidden"></li>
      <li class="grayText priceVariationMsg hidden"><bbbl:label key='lbl_price_variations_orig_text' language="${pageContext.request.locale.language}" /></li>
      <li class="grayText inCartDisclaimerMsg hidden"><bbbl:label key="lbl_discount_incart_orig_text" language="${pageContext.request.locale.language}" /></li>
</ul>

</dsp:page>
                