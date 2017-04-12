<dsp:page>
    <dsp:importbean bean="/atg/commerce/ShoppingCart" />   
    <dsp:param name="order" bean="ShoppingCart.current" />
		<dsp:include page="/global/gadgets/errorMessage.jsp" />
    <div class="name">
	    <span class="firstName"><dsp:valueof param="order.billingAddress.firstName"  /></span> 
	    <span class="middleName"><dsp:valueof param="order.billingAddress.middleName"  /></span> 
	    <span class="lastName"><dsp:valueof param="order.billingAddress.lastName"  /></span> 
	    <span class="company"><dsp:valueof param="order.billingAddress.companyName"  /></span>
	</div>
	<div class="street1"><dsp:valueof param="order.billingAddress.address1"  /></div>
	<div class="street2"><dsp:valueof param="order.billingAddress.address2"  /></div>
	<div class="cityStateZip">
	    <span class="city"><dsp:valueof param="order.billingAddress.city"  /></span>, 
	    <span class="state"><dsp:valueof param="order.billingAddress.state"/></span> 
	    <span class="zip"><dsp:valueof param="order.billingAddress.postalCode"  /></span><br/>
	    <span class="countryName"><dsp:valueof param="order.billingAddress.countryName"  /></span>
	    <span class="country hidden"><dsp:valueof param="order.billingAddress.country"  /></span>
	</div>
    
</dsp:page>
