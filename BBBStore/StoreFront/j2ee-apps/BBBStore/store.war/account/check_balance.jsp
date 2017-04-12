<dsp:page>
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <c:set var="section" value="accounts" scope="request" />
    <bbb:pageContainerNoHeaderFooter index="false" follow="false">
        <jsp:attribute name="bodyClass">atg_store_pageHome</jsp:attribute>
        <jsp:attribute name="section">${section}</jsp:attribute>
        <jsp:attribute name="pageWrapper">${pageWrapper} modalCheckBalance</jsp:attribute>
        <jsp:body>
            <dsp:importbean bean="/com/bbb/payment/giftcard/BBBGiftCardFormHandler" />
            <c:set var="lbl_giftcard_checkbal_heading">
                <bbbl:label key='lbl_giftcard_checkbal_heading' language='${pageContext.request.locale.language}' />
            </c:set>
            <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
            <div title="${lbl_giftcard_checkbal_heading}">
                <dsp:form iclass="frmCheckGiftCardBal frmCheckGiftCardBalModal" action="${contextPath}/account/check_giftcard_balance.jsp" method="post">
                    <div class="clearfix">
                        <div class="formRow marTop_5 noMarBot fl">
                            <div class="input noMarBot giftCardNumber fl marRight_20">
                                <div class="label">
                                    <label id="lblgiftCardNumberPU" for="giftCardNumberPU">
                                        <bbbl:label key="lbl_giftcard_number" language ="${pageContext.request.locale.language}"/><span class="required">&nbsp;<bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
                                    </label>
                                </div>
                                <div class="text noMarBot">
                                    <dsp:input type="text" id="giftCardNumberPU" bean="BBBGiftCardFormHandler.giftCardNumber" name="giftCardNumber" maxlength="16" value="" autocomplete="off">
                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblgiftCardNumberPU errorgiftCardNumberPU"/>
                                    </dsp:input>
                                </div>
                            </div>
                            <div class="input noMarBot giftCardPin fl">
                                <div class="label">
                                    <label id="lblgiftCardPinPU" for="giftCardPinPU">
                                        <bbbl:label key="lbl_giftcard_pin" language ="${pageContext.request.locale.language}"/><span class="required">&nbsp;<bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
                                    </label>
                                </div>
                                <div class="text noMarBot">
                                    <dsp:input type="text" id="giftCardPinPU" bean="BBBGiftCardFormHandler.giftCardPin" name="giftCardPin" maxlength="8" value="" autocomplete="off">
                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblgiftCardPinPU errorgiftCardPinPU"/>
                                    </dsp:input>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="clearfix errorWrap hidden">
                        <div class="noMarBot fl marTop_10">
                            <label class="error serverErrors"></label>
                        </div>
                    </div>
                    <div class="clearfix">
                        <div class="noMarBot fl chkBalLnkTxt">
                            <div class="noMarBot fl marRight_20">
                                <dsp:a href="BBBGiftCardFormHandler.balance" iclass="btnCheckBal hidden">
                                    <bbbl:label key="lbl_giftcard_checkbal_button" language ="${pageContext.request.locale.language}"/>
                                </dsp:a>
                            </div>
                            <div class="clearfix giftCardBalanceWrap marTop_10 hidden">
                                <div class="noMarBot fl">
                                    <strong>
                                        <bbbl:label key="lbl_giftcard_checkbal_giftcard" language ="${pageContext.request.locale.language}"/>
                                    </strong>&nbsp;
                                    <span class="giftCardNumberResult giftCardResultItem"></span>
                                </div>
                                <div class="bal noMarBot fl marLeft_20">
                                    <strong>
                                        <bbbl:label key="lbl_giftcard_checkbal_balance" language ="${pageContext.request.locale.language}"/>
                                    </strong>&nbsp;
                                    <span class="balance giftCardResultItem"></span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="clearfix">
                        <div class="noMarBot clearfix">
                            <div class="fl marRight_10 marTop_10">
                                <div class="button button_active">
                                <dsp:input type="hidden" bean="BBBGiftCardFormHandler.balance" value="Check Balance" />
                                    <c:set var="lbl_giftcard_checkbal_button">
                                        <bbbl:label key='lbl_giftcard_checkbal_button' language='${pageContext.request.locale.language}' />
                                    </c:set>
                                    <dsp:input type="submit" bean="BBBGiftCardFormHandler.balance" iclass="btnSubmit" value="${lbl_giftcard_checkbal_button}" >
                                        <dsp:tagAttribute name="aria-pressed" value="false"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="btnSubmit"/>
                                        <dsp:tagAttribute name="role" value="button"/>
                                    </dsp:input>
                                </div>
                            </div>
                        </div>
                    </div>
                </dsp:form>
            </div>
            <script type="text/javascript">
                if (typeof s !== 'undefined') {
                    s.pageName = 'Gift Card Balance';
                    s.channel = 'Gift Cards';
                    s.prop1 = 'Gift Cards';
                    s.prop2 = 'Gift Cards';
                    s.prop3 = 'Gift Cards';
                    s.prop4 = '';
                    var s_code = s.t();
                    if (s_code)
                        document.write(s_code);
                }
            </script>
        </jsp:body>
    </bbb:pageContainerNoHeaderFooter>
</dsp:page>