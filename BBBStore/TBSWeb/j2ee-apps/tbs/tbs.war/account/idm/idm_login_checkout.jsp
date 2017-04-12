<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page import="atg.servlet.*" %>

<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
<c:set var="emailPlaceholder">
    <bbbl:label key="lbl_login_frag__email" language="${pageContext.request.locale.language}" />
</c:set>
<c:set var="passPlaceholder">
    <bbbl:label key="lbl_login_frag__password" language="${pageContext.request.locale.language}" />
</c:set>
<c:set var="orderIdPlaceholder">
    <bbbl:label key="tbs_gs_orderId" language="${pageContext.request.locale.language}" />
</c:set>
<dsp:getvalueof bean="/OriginatingRequest.contextPath" var="context"/>
<dsp:page>

    <div id="associateLoginModalCheckout" class="reveal-modal small" data-reveal data-options="close_on_background_click:false;close_on_esc:false;">
        <div class="small-12 columns">
            <h2>Associate Login</h2>
        </div>
        <dsp:form id="login" action="idm_login_checkout.jsp" method="post">
            <div class="row" id="idmErrors">

            </div>
            <div class="row">
                <!-- <div class="small-12 columns">
                    <label id="lblemail" for="email">Username</label>
                </div> -->
                <div class="small-12 columns">
                    <dsp:input id="emailid" bean="CartModifierFormHandler.assoUsername" type="text">
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
                        <dsp:tagAttribute name="placeholder" value="BBBUID"/>
                    </dsp:input>
                </div>
                <!-- <div class="small-12 columns">
                    <label id="lblpassword" for="password">Password</label>
                </div> -->
                <div class="small-12 columns">
                    <dsp:input bean="CartModifierFormHandler.assoPassword" id="password" type="password">
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblpassword errorpassword"/>
                        <dsp:tagAttribute name="placeholder" value="${passPlaceholder}"/>
                    </dsp:input>
                </div>

                <div class="small-6 columns">
                    <a class="small button secondary column" href="${context}/cart/cart.jsp">Cancel</a>    
                </div>
                
                <dsp:input type="hidden" bean="CartModifierFormHandler.multiship" value="true" id="multi_ship_popup" />
                <dsp:input id="express_checkout_popup" bean="CartModifierFormHandler.expressCheckout" type="hidden"/>
             <%-- <dsp:input bean="CartModifierFormHandler.assoLoginFailureURL" type="hidden" value="${context}/account/idm/checkout_asso_login_fail.jsp"/>
                <dsp:input bean="CartModifierFormHandler.assoLoginSuccessURL" type="hidden" value="${context}/account/idm/checkout_asso_login_success.jsp"/> --%>
                	<dsp:input bean="CartModifierFormHandler.fromPage"  type="hidden" value="idmLoginCheckout" />
                <div class="small-6 columns">
                    <dsp:input bean="CartModifierFormHandler.tBSCheckout" id="tBSCheckoutIDM" type="submit" iclass="small button service expand" value="Log In" />
                </div>
            </div>
        </dsp:form>
    </div>
    <div id="result" class="reveal-modal small" data-reveal>
        <a class="close-reveal-modal">&#215;</a>
    </div>

    <script type="text/javascript">
        $(document).ready(function(){
            // js functions to handle associate login
            var checkOutWithPayPal = false;
            BBB.eventTarget.on('click', '#tBSCheckout', function (event) {
                
                checkOutWithPayPal = false;
                event.preventDefault();
               
                var associate1 = "${sessionScope.associate1}";
                if (associate1 == null || associate1 == ""){
                	$('#associateLoginModalCheckout').foundation('reveal', 'open');
                    var multi_ship = $('#multi_ship').is(':checked');
                    $("#multi_ship_popup").attr("value", multi_ship);
                    var express_checkout = $('#express_checkout').is(':checked');
                    $("#express_checkout_popup").attr("value", express_checkout);
                }
                else{
                    var multi_ship = $('#multi_ship').is(':checked');
                    $("#multi_ship_popup").attr("value", multi_ship);
                    var express_checkout = $('#express_checkout').is(':checked');
                    $("#express_checkout_popup").attr("value", express_checkout);
                    $("#tBSCheckoutIDM").trigger('click');              
                }
            
            })
            
            $(".paypalCheckoutButton").click(function (event){
                if($(this).attr('src').indexOf('paypal_disabled.png') > 0){
                    return true;
                }else{
                    checkOutWithPayPal = true;
                    event.preventDefault();
                    var associate1 = "${sessionScope.associate1}";
                    if (associate1 == null || associate1 == ""){
                        $('#associateLoginModalCheckout').foundation('reveal', 'open');
                        var multi_ship = $('#multi_ship').is(':checked');
                        $("#multi_ship_popup").attr("value", multi_ship);
                        var express_checkout = $('#express_checkout').is(':checked');
                        $("#express_checkout_popup").attr("value", express_checkout);
                    }
                    else{
                        var multi_ship = $('#multi_ship').is(':checked');
                        $("#multi_ship_popup").attr("value", multi_ship);
                        var express_checkout = $('#express_checkout').is(':checked');
                        $("#express_checkout_popup").attr("value", express_checkout);
                        $("#tBSCheckoutIDM").trigger('click');              
                    }
                }
            
            })
            
            
            $("#tBSCheckoutIDM").click(function( event ) {
                $('#login').ajaxForm({
                    url: '/tbs/account/idm/idm_login_checkout.jsp',
                    type: 'POST',
                    success:  function(data) {
                        var jsonObj = JSON.parse(data);
                        if(jsonObj.error == true){
                            var errors = jsonObj.errors;
                            var errHtml = "<ul>";
                            for (i = 0; i < errors.length; i++) {
                                errHtml += "<li>" +errors[i];
                            }
                            errHtml += "</ul>";
                            if(jsonObj.idmError == true){
                                $('#idmErrors').html(errHtml);
                            } else {
                                $('#associateLoginModalCheckout').foundation('reveal', 'close');
                                $('.error').html(errHtml);
                            }
                        } else {
                            var successURL = jsonObj.successURL;
                            $('#associateLoginModalCheckout').foundation('reveal', 'close');
                            if(checkOutWithPayPal){
                                location.href = $(".paypalCheckoutContainer").find('a').attr('href');
                            }else{
                                location.href = successURL;
                            }
                            
                        }
                    }
                })
            });
        });
    </script>

</dsp:page>
