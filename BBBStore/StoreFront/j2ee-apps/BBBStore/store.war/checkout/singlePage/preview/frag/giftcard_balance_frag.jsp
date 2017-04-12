<dsp:page>
 	<dsp:importbean bean="/atg/dynamo/droplet/CurrencyFormatter"/>
 	<dsp:importbean bean="/com/bbb/payment/droplet/BBBPaymentGroupDroplet"/>
 	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:droplet name="BBBPaymentGroupDroplet">
      <dsp:param param="order" name="order"/>
      <dsp:param name="serviceType" value="GiftCardDetailService"/>
      <dsp:oparam name="output">
      	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
			<dsp:param name="array" param="giftcards" />
			<dsp:param name="elementName" value="giftcard" />
			<dsp:getvalueof id="cardNumber" param="giftcard.cardNumber"/>
			<dsp:oparam name="output">
			<li class="grid_4 noMarLeft marBottom_10">
				<span class="grid_1 noMar">
					<img class="creditCardIcon" src="/_assets/global/images/products/giftcards/giftcard2002.jpg" width="41" height="26" alt="Gift Card">
				</span>
				<span class="grid_3">
					<bbbl:label key="lbl_spc_preview_giftcard" language="<c:out param='${language}'/>"/> <bbbl:label key="lbl_spc_preview_giftcardMask" language="<c:out param='${language}'/>"/> ${fn:substring(cardNumber,fn:length(cardNumber)-4,fn:length(cardNumber))}
					<br>
					<span class="marTop_10 editPreviewLink">
						<a class="capitalize" href="${pageContext.request.contextPath}/checkout/checkout_single.jsp#spcGiftCard" title="<bbbl:label key="lbl_spc_preview_edit" language="<c:out param='${language}'/>"/>"><strong><bbbl:label key="lbl_spc_preview_edit" language="<c:out param='${language}'/>"/></strong></a>
					</span>
				</span>
				</li>
				<%-- <dt><bbbl:label key="lbl_spc_preview_giftcardamount" language="<c:out param='${language}'/>"/></dt>
				<dsp:droplet name="CurrencyFormatter">
				    <dsp:param name="currency" param="giftcard.amount"/>
				    <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
				    <dsp:oparam name="output">
					    <dd><dsp:valueof param="formattedCurrency"/></dd>
				    </dsp:oparam>
				</dsp:droplet> --%>
			</dsp:oparam>
		</dsp:droplet>
      </dsp:oparam>
	</dsp:droplet>
</dsp:page>
