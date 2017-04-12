<dsp:page>
        <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
        <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
		<dsp:importbean bean="/atg/commerce/ShoppingCart" />
        <c:set var="bopusOnlyText" value="BOPUS_ONLY" />
        <c:set var="hybrid" value="HYBRID" />
        <dsp:getvalueof id="formAction" param="action" />
        <dsp:getvalueof id="currentLevel" param="currentLevel" />
        <dsp:form id="frmCoupons" formid="frmCoupons" action="${formAction}">
       		<%-- Below If block is for R2 requirement --%>
			<dsp:getvalueof var="orderStatus" bean="ShoppingCart.current.OnlineBopusItemsStatusInOrder" />
			<dsp:droplet name="/com/bbb/commerce/order/droplet/UserCouponWalletDroplet">
                <dsp:param name="profile" bean="/atg/userprofiling/Profile"/>
                <dsp:param name="order" bean="/atg/commerce/ShoppingCart.current"/>
                <dsp:param name="site" value="${currentSiteId}"/>
                <dsp:oparam name="outputStart">
                    <dsp:setvalue bean="CartModifierFormHandler.couponPage" paramvalue="couponPage"/>
                    <dsp:input bean="CartModifierFormHandler.couponPage" type="hidden" paramvalue="couponPage" />
                        
                    <div class="hidden">
                        <c:set var="applyButton"><bbbl:label key="lbl_coupon_apply" language="${language}"/></c:set>
                        <dsp:getvalueof id="cartCheck" param="cartCheck" />
                        <c:if test="${not empty cartCheck}">
                            <input name="cart" type="hidden" value="CART"/>
                        </c:if>
                        <dsp:input bean="CartModifierFormHandler.applyCoupons" type="submit" value='${applyButton}' id="btnApplyCoupons" >
                            <dsp:tagAttribute name="aria-pressed" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="btnApplyCoupons"/>
                            <dsp:tagAttribute name="role" value="button"/>
                            <dsp:tagAttribute name="class" value="btnApply posBtm btnPrimary button-Large" />
                        </dsp:input>
                    </div>
                   
                    
                </dsp:oparam>
                <dsp:oparam name="output">
                	<dsp:getvalueof var="onlineCouponList" param="onlineCouponList" />
			      	<dsp:getvalueof var="useAnywhereCouponList" param="useAnywhereCouponList" />
			    	
			    	<c:if test="${fn:length(useAnywhereCouponList)>0 ||fn:length(onlineCouponList)>0}">
			    		<input name="couponsAvailable" class="couponsAvailable" type="hidden" value="true"/>
			    	</c:if>
			    	
                    <hr />

                    <h1 class="SPCSectionHeading">2. <bbbl:label key="lbl_coupon_pagetitle" language="${language}"/></h1>


                   	<a id="viewCartContents" role="button" class="iconExpand marLeft_10" href="#" title='<bbbl:label key="lbl_coupon_view_cart" language="${language}"/>' aria-expanded="false" aria-live="assertive"><bbbl:label key="lbl_coupon_view_cart" language="${language}"/></a>
                	
                    <div class="grid_8 alpha">
                        <div class="highlightSection grid_8 alpha omega">
                        	<%-- Below if block is for R2 change --%>
                        	<dsp:getvalueof id="errorMap" bean="CartModifierFormHandler.couponErrorList"/>
                        	<c:if test="${(empty errorMap) or (errorMap.size eq 0)}">
						    	<dsp:include page="/global/gadgets/errorMessage.jsp" />
						    </c:if>
                            <dsp:include page="/cart/cartItems.jsp"/>
                        </div>
                 	</div>
                    <dsp:include page="/cart/spcCouponLineitem.jsp">
                        <dsp:param name="onlineCouponList" value="${onlineCouponList }"/>
                        <dsp:param name="useAnywhereCouponList" value="${useAnywhereCouponList }"/>
                    </dsp:include>
                </dsp:oparam>
                <dsp:oparam name="outputEnd">
                    
                </dsp:oparam>
                <dsp:oparam name="empty">
                    <dsp:droplet name="IsEmpty">
                        <dsp:param name="value" param="couponPage"/><%--This parameter is sent from the coupon page not the cart page --%>



                        <dsp:oparam name="false">
                            <%-- what do we show for no coupons on SPC ? 
                            <p><bbbl:label key="lbl_coupon_no_coupons_message" language="${language}"/></p>
                            --%>
                        </dsp:oparam>
                    </dsp:droplet>
                </dsp:oparam>
            </dsp:droplet>
      </dsp:form>
</dsp:page>
