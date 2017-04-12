<dsp:page>
<dsp:importbean bean="/atg/commerce/ShoppingCart" />
<dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />

<%--
<c:choose>
	<c:when test="${payPalOrder eq true}">		
		<c:set var="disabled" value="disabled"/>
	</c:when>
	<c:otherwise>		
		<c:set var="disabled" value=""/>
	</c:otherwise>
</c:choose>
--%>

<div class="SPCPayMethod clearfix">

	
	<h3 class="noBorder"><bbbl:label key="lbl_spc_form_of_payment_header" language="${pageContext.request.locale.language}" /></h3>
	<p><bbbl:label key="lbl_spc_review_and_finalize" language="${pageContext.request.locale.language}" /></p>

	<div>
		<form>

			<%--
			<label class="${disabled}">
			   <input class="noUniform" type="radio" name="paymentOpt" value="giftcard"  />


				<div class="bgImg">
					<span class="icon icon-gift" aria-hidden="true"></span>
					<div class="methodType"><span><bbbl:label key="lbl_spc_add_gift_card_or_merchandise_credit" language="${pageContext.request.locale.language}" /></span></div>
				</div>
			</label>
			--%>

			<input class="noUniform icon-text" type="radio" value="giftcard" name="paymentOpt" id="gcRadio" aria-labeledby="gcMessage">
			<label class="" id="gcMessage" for="gcRadio">				
				<div class="bgImg">
					<span class="icon icon-gift" aria-hidden="true"></span>
					<div class="methodType"><span><bbbl:label key="lbl_spc_add_gift_card_or_merchandise_credit" language="${pageContext.request.locale.language}" /></span></div>
				</div>
			</label>
			
			
			<input class="noUniform icon-text" type="radio" value="creditCard" name="paymentOpt" id="ccRadio" aria-labeledby="ccMessage">
			<label class="" id="ccMessage" for="ccRadio">				
				<div class="bgImg">
					<span class="icon icon-credit-card" aria-hidden="true"></span>
					<div class="methodType"><span><bbbl:label key="lbl_spc_pay_with_credit_card" language="${pageContext.request.locale.language}" /></span></div>
				</div>
			</label>
			
			<input class="noUniform icon-text" type="radio" value="paypal" name="paymentOpt" id="paypalRadio" aria-labeledby="paypalMessage">
			<label class="" id="paypalMessage" for="paypalRadio">				
				<div class="bgImg">
					<img src="/_assets/global/images/icons/PayPal_220px-_2014_logo.png" alt="" id="paymentPaypalLogo" />
					<div class="methodType">
		    			<span><bbbl:label key="lbl_spc_pay_with_paypal" language="${pageContext.request.locale.language}" /></span>
			    	</div>
				</div>
			</label>
			
			

			<%--
			<label class="${disabled}">
			   <input class="noUniform" type="radio" name="paymentOpt" value="creditCard" />
			   <div class="bgImg">
			    	<span class="icon icon-credit-card" aria-hidden="true"></span>
				   <div class="methodType">
			    		<span><bbbl:label key="lbl_spc_pay_with_credit_card" language="${pageContext.request.locale.language}" /></span>
				   </div>
			   </div>
			</label>
			  
			<label class="${disabled}">
			   <input class="noUniform" type="radio" name="paymentOpt" value="paypal"  />
				<div class="bgImg">
					<img src="/_assets/global/images/icons/PayPal_220px-_2014_logo.png" id="paymentPaypalLogo" />
					
			    	<div class="methodType">
		    			<span><bbbl:label key="lbl_spc_pay_with_paypal" language="${pageContext.request.locale.language}" /></span>
			    	</div>
			   </div>
			</label>
			  --%>
			
		</form>	
	</div>

</div>

</dsp:page>