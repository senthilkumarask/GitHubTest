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

<dsp:page>

    <div id="associateLoginModalCustOrd" class="reveal-modal small" data-reveal data-options="close_on_background_click:false;close_on_esc:false;">
        <div class="small-12 columns">
            <h2>Associate Login</h2>
        </div>
        <dsp:form id="login" action="idm_login_custom_order.jsp" method="post">
            <div class="row" id="errors">

            </div>
            <div class="row">
                <!-- <div class="small-12 columns">
                    <label id="lblemail" for="email">Username</label>
                </div> -->
                <div class="small-12 columns">
                    <dsp:input id="email" bean="CartModifierFormHandler.assoUsername" type="text">
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

                <div class="small-12 columns">
                    <label id="lblorderId" for="orderId">Order ID</label>
                </div>
                <div class="small-12 columns">
                    <dsp:input bean="CartModifierFormHandler.gsOrderId" id="gsOrderId" type="text">
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblorderId errororderId"/>
                        <dsp:tagAttribute name="placeholder" value="${orderIdPlaceholder}"/>
                    </dsp:input>
                </div>

                <div class="small-6 columns">
                    <a class="small button secondary column" href="<dsp:valueof bean='/OriginatingRequest.requestURI' />">Cancel</a>
                </div>
                <div class="small-6 columns">
                    <dsp:input bean="CartModifierFormHandler.createGSOrder" id="createGSOrder" type="submit" iclass="small button service expand" value="Log In" />
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

            var orderId = $("#orderId").val();
            if(orderId){
                $("#gsOrderId").attr("value",orderId);
                $('#associateLoginModalCustOrd').foundation('reveal', 'open');
            }

            $("#guided_selling").click(function (event){
                event.preventDefault();
                $('#associateLoginModalCustOrd').foundation('reveal', 'open');
            })
            $("#createGSOrder").click(function( event ) {
                $('#login').ajaxForm({
                    url: '/tbs/account/idm/idm_login_custom_order.jsp',
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
                            $('#errors').html(errHtml);

                        } else {
                            $('#associateLoginModalCustOrd').foundation('reveal', 'close');
                            var context = "${pageContext.request.contextPath}";
                            location.href = context+"/cart/cart.jsp"
                        }
                        /* $('#result').html(data);
                        $('#result').foundation('reveal', 'open');
                        $(document).foundation('reflow'); */

                    }
                })
            });
        });
    </script>

</dsp:page>
