<dsp:page>
<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/commerce/order/purchase/CommitOrderFormHandler"/>
	<dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
    <dsp:importbean bean="/com/bbb/commerce/order/purchase/CheckoutProgressStates"/>
    <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
    <dsp:getvalueof var="currentState" bean="CheckoutProgressStates.currentLevelAsInt"/>
    <dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
    
    
        <div class="floating_footer clearfix" id="spcOrderFooter">
		

            <div class="container_12 clearfix">
                <div class="inner_wrapper grid_12 alpha omega">
                    <dsp:form name="commit_form" id="chekoutReview" method="post" >
                        
                        <div class="grid_6 alpha">
                            <a id="spcPreviewNow" href="/store/checkout/singlePage/preview/review_cart.jsp" aria-label="Continue to preview your order" class="button-Med btnPrimary disabled"><bbbl:label key="lbl_spc_button_preview_now" language="${pageContext.request.locale.language}" /></a>
                            <button id="spcPlaceOrder"                            
                                    class="btnAddtocart button-Med disabled"                            
                                    aria-pressed="false"
                                    aria-labelledby="shippingPlaceOrder1"
                                    role="button"
                                    disabled="disabled" ><bbbl:label key="lbl_spc_button_place_order_now" language="${pageContext.request.locale.language}" /></button>

                            <dsp:input id="spcPlaceOrderSubmit" bean="CommitOrderFormHandler.verifiedByVisaLookup" type="submit" value="PLACE ORDER NOW" iclass="button-Med btnAddtocart hidden" >
                                <dsp:tagAttribute name="aria-pressed" value="false"/>
                                <dsp:tagAttribute name="aria-labelledby" value="shippingPlaceOrder1"/>
                                <dsp:tagAttribute name="role" value="button"/>                 
                            </dsp:input>        
                            <img id="placeOrderLoader" src="/_assets/global/images/widgets/small_loader.gif" class="marTop_5 marLeft_10 hidden">
                        </div>
                        <div class="grid_6 omega">
                            <p class="footer_text"><bbbl:label key="lbl_spc_submit_all_required_forms" language="${pageContext.request.locale.language}" /></p>
                        </div>


                        

                    </dsp:form>                 
                    <%--<a href="#" class="button-Med btnAddtocart ">PLACE ORDER NOW</a> --%>
                </div>
            </div>
        </div>

        
            





</dsp:page>