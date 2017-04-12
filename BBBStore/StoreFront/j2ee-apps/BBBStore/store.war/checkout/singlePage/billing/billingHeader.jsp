
<%-- As of now this is not used. But in case checkout_header.jsp doesn't work, this will be used --%>

<head>
        <title><bbbl:label key="lbl_findstore_bed_beyond" language="${pageContext.request.locale.language}" /> - <bbbl:label key="lbl_checkout_billing_coupons" language="${pageContext.request.locale.language}" /></title>
        <c:set var="section" value="checkout" scope="request" />
        <c:set var="pageWrapper" value="billing billingCoupons" scope="request" />
        <c:import url="/_includes/head/common_head.jsp" />
</head>
<dsp:page>

<div id="header" class="container_12 clearfix">
	<div itemscope itemtype="http://schema.org/Organization" class="marLeft_10 fl">
		<h1 class="noMar">
			<a itemprop="url" href="#" title="Bed Bath & Beyond"><img itemprop="logo"
				src="${imagePath}/_assets/global/images/logo/logo_bbb2.png" width="146"
				height="42" alt="Bed Bath & Beyond" /> </a>
		</h1>
	</div>
	<div class="marRight_10 fr">
		<a class="returnToCart fl" href="/cart/cart.jsp" role="link" title="Return to cart"><bbbl:label key="lbl_checkout_zip_return_cart" language="${pageContext.request.locale.language}" /></a>
		<div id="steps" class="fl">
			<a href="#" class="first"><span>1</span><bbbl:label key="lbl_spc_bread_crumb_shipping" language="${pageContext.request.locale.language}" /></a> <a href="#"
				class="active"><span>2</span><bbbl:label key="lbl_spc_bread_crumb_billing" language="${pageContext.request.locale.language}" /></a> <a href="#"><span>3</span><bbbl:label key="lbl_payment_title" language="${pageContext.request.locale.language}" /></a>
			<a href="#"><span>4</span><bbbl:label key="lbl_bread_crumb_preview" language="${pageContext.request.locale.language}" /></a>
		</div>
	</div>
	<div id="subHeader" class="grid_12 clearfix">
		<h2 class="section">
			<bbbl:label key="lbl_bread_crumb_billing" language="${pageContext.request.locale.language}" />:&nbsp;<span class="subSection"><bbbl:label key="lbl_billing_address" language="${pageContext.request.locale.language}" /></span>
		</h2>
	</div>
</div>
</dsp:page>