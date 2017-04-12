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
			<dsp:oparam name="output">
			<dl class="clearfix">
				<dt><bbbl:label key="lbl_preview_giftcard" language="<c:out param='${language}'/>"/></dt>			
				<dsp:getvalueof id="cardNumber" param="giftcard.cardNumber"/>
				<dd><bbbl:label key="lbl_preview_giftcardMask" language="<c:out param='${language}'/>"/> ${fn:substring(cardNumber,fn:length(cardNumber)-4,fn:length(cardNumber))}</dd>
				<dt><bbbl:label key="lbl_preview_giftcardamount" language="<c:out param='${language}'/>"/></dt>
				<dsp:droplet name="CurrencyFormatter">
				    <dsp:param name="currency" param="giftcard.amount"/>
				    <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
				    <dsp:oparam name="output">
					    <dd><dsp:valueof param="formattedCurrency"/></dd>
				    </dsp:oparam>
				</dsp:droplet>
			</dl>
			<p class="marBottom_10"/>
			</dsp:oparam>
		</dsp:droplet>
      </dsp:oparam>
	</dsp:droplet>
</dsp:page>
