<dsp:page>    

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/com/bbb/account/GetCouponsDroplet" />
<dsp:importbean var="Profile" bean="/atg/userprofiling/Profile"/>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
 <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
 <dsp:importbean bean="/com/bbb/account/AddWalletIdtoProfileDroplet" />
<c:set var="section" value="accounts" scope="request" />
<c:set var="pageWrapper" value="coupons myAccount" scope="request" />


<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:getvalueof var="appid" bean="Site.id" />

<bbb:pageContainer index="false" follow="false">
    <jsp:attribute name="bodyClass">atg_store_pageHome</jsp:attribute>
    <jsp:attribute name="section">${section}</jsp:attribute>
    <jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>
    <jsp:attribute name="PageType">accountCoupons</jsp:attribute>
    <jsp:body>

      
     <dsp:droplet name="AddWalletIdtoProfileDroplet">
     <dsp:oparam name="output">  
     </dsp:oparam>
     </dsp:droplet>
         
     <dsp:getvalueof var="couponsWelcomeMsg" bean="SessionBean.couponsWelcomeMsg" />

    <c:set var="scene7Path">
            <bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
        </c:set>
   
    <div id="content" class="container_12 clearfix" role="main">
        <div class="grid_12">
            <h1 class="account fl"><bbbl:label key="lbl_personalinfo_myaccount" language ="${pageContext.request.locale.language}"/></h1>
            <h3 class="subtitle"><bbbl:label key="lbl_myaccount_coupons" language ="${pageContext.request.locale.language}"/></h3>
            <div class="clear"></div>
        </div>
        

        <%--  <c:if test="${appid ne 'BedBathCanada' && couponsWelcomeMsg}"> --%>
      	<c:if test="${couponsWelcomeMsg}">
            <div class="grid_12" id="couponWalletBanner">
                <c:choose>
                    <c:when test="${not empty appid && appid eq 'BuyBuyBaby'}">                    
                        <bbbt:textArea key="txt_coupons_welcomePromo_baby"  language="${pageContext.request.locale.language}" />
                    </c:when>
                    <c:when test="${appid eq 'BedBathCanada'}">                    
                    <bbbt:textArea key="txt_coupons_welcomePromo_CA"  language="${pageContext.request.locale.language}" />
                    </c:when>
                    <c:otherwise>
                        <bbbt:textArea key="txt_coupons_welcomePromo"  language="${pageContext.request.locale.language}" />
                    </c:otherwise>
                </c:choose>
            </div> 
            
            <%-- can't have this here, add a coupon form depends on the original session value for display rules. 
                will set the value on addAcoupon.jsp 
                <dsp:setvalue bean="SessionBean.couponsWelcomeMsg" value="false"/>
                --%>
        </c:if>  


        <div class="grid_12">
            <div class="grid_3 alpha _leftNav">
                <c:import url="/account/left_nav.jsp">
                 <c:param name="currentPage"><bbbl:label key="lbl_myaccount_coupons" language ="${pageContext.request.locale.language}"/></c:param>
                </c:import>
            </div>
            
            <div class="grid_9 omega couponLists">
               <c:import url="/account/frags/couponList.jsp" />     
           </div>
        </div>
    </div>
</jsp:body>
<jsp:attribute name="footerContent">
    <script type="text/javascript">
        if (typeof s !== 'undefined') {
            s.pageName = 'My Account>Coupon Wallet';
            s.channel = 'My Account';
            s.prop1='My Account';
            s.prop2='My Account';
            s.prop3='My Account';
            s.prop6='${pageContext.request.serverName}'; 
            s.eVar9='${pageContext.request.serverName}';
            var s_code = s.t();
            if (s_code)
                document.write(s_code);
        }
    </script>
</jsp:attribute>
</bbb:pageContainer>
</dsp:page>