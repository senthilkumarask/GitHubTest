<dsp:page>
        <dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
        <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
		<dsp:importbean bean="/atg/commerce/ShoppingCart" />
        <c:set var="bopusOnlyText" value="BOPUS_ONLY" />
        <c:set var="hybrid" value="HYBRID" />
        <dsp:getvalueof id="formAction" param="action" />
        <dsp:getvalueof id="orderHasErrorPrsnlizedItem" param="orderHasErrorPrsnlizedItem" />
        <dsp:getvalueof id="currentLevel" param="currentLevel" />
        <dsp:getvalueof var="fullPage" param="couponPage"/>
        
        <%-- Setting iclass on the applyCoupons button based on the parent page .Included as part of BBBSL-7069. --%>
         <c:if test="${fullPage eq 'COUPONS'}"><c:set var="checkoutCoupon">hidden</c:set></c:if>
         
        <dsp:form id="frmCoupons" formid="frmCoupons" action="${formAction}">
       		<%-- Below If block is for R2 requirement --%>
			<dsp:getvalueof var="orderStatus" bean="ShoppingCart.current.OnlineBopusItemsStatusInOrder" />
			<dsp:droplet name="/com/bbb/commerce/order/droplet/UserCouponWalletDroplet">
                <dsp:param name="profile" bean="/atg/userprofiling/Profile"/>
                <dsp:param name="order" bean="/atg/commerce/ShoppingCart.current"/>
                <dsp:param name="site" value="${currentSiteId}"/>
                <dsp:oparam name="outputStart">
                </dsp:oparam>
                <dsp:oparam name="output">
                	<dsp:getvalueof var="onlineCouponList" param="onlineCouponList" />
			      	<dsp:getvalueof var="useAnywhereCouponList" param="useAnywhereCouponList" />
			      	<c:if test="${fn:length(useAnywhereCouponList)>0 ||fn:length(onlineCouponList)>0}">
			      	 <dsp:setvalue bean="CartModifierFormHandler.couponPage" paramvalue="couponPage"/>
                    <dsp:input bean="CartModifierFormHandler.couponPage" type="hidden" paramvalue="couponPage" />
                    <div class="couponSectHeader grid_8 alpha omega">
                        	                <c:choose>
                                            	<c:when test="${fullPage eq 'COUPONS'}">
	                                            	<bbbl:label key="lbl_checkout_coupon_pagetitle" language="${language}"/>
			                                    </c:when>
			                                    <c:otherwise>
			                                    			<h3 class="frmCpnHdr grid_5 alpha omega"><bbbl:label key="lbl_coupon_pagetitle" language="${language}"/></h3>
							                    <p class="grid_5 alpha omega"><bbbl:label key="lbl_coupon_cart_section" language="${language}"/></p>
			                                    </c:otherwise>
			                                </c:choose>
                            <div class="">
                                <c:set var="applyButton"><bbbl:label key="lbl_coupon_apply" language="${language}"/></c:set>
                                <dsp:getvalueof id="cartCheck" param="cartCheck" />
                                <c:if test="${not empty cartCheck}">
                                    <input name="cart" type="hidden" value="CART"/>
                                </c:if>
                            </div>
                        </div>
                    <dsp:droplet name="IsEmpty">
                        <dsp:param name="value" param="couponPage"/><%--This parameter is sent from the coupon page not the cart page --%>
                        <dsp:oparam name="true">
							<c:if test="${CouponOn && ((orderStatus eq bopusOnlyText) or (orderStatus eq hybrid))}">
								<div class="couponStoreInfo cb clearfix grid_8 alpha omega">
									<h3 class="grid_5 alpha omega"><bbbl:label key="lbl_online_coupon_store_pickup" language="${language}"/></h3>
									<p class="grid_5 alpha omega"><bbbl:label key="lbl_online_coupon_store_pickup_desc" language="${language}"/></p>
                                    <div class="grid_3 alphe omega <c:if test="${orderHasErrorPrsnlizedItem}">disabled </c:if>">
                                            <dsp:getvalueof id="cartCheck" param="cartCheck" />
                                            <c:if test="${not empty cartCheck}">
                                                <input name="cart" type="hidden" value="CART"/>
                                            </c:if>
                                            <c:choose>
                                            	<c:when test="${orderHasErrorPrsnlizedItem}">
	                                            	<dsp:input bean="CartModifierFormHandler.applyCoupons" disabled="disabled" type="submit" value='${applyButton}' id="btnApplyCoupons" >
				                                    <dsp:tagAttribute name="aria-pressed" value="false"/>
				                                    <dsp:tagAttribute name="aria-labelledby" value="btnApplyCoupons"/>
				                                    <dsp:tagAttribute name="role" value="button"/>
				                                    <dsp:tagAttribute name="class" value="btnApply posBtm btnPrimary button-Large" />
				                                    </dsp:input>
			                                    </c:when>
			                                    <c:otherwise>
			                                    	<dsp:input bean="CartModifierFormHandler.applyCoupons" type="submit" value='${applyButton}' id="btnApplyCoupons" >
				                                    <dsp:tagAttribute name="aria-pressed" value="false"/>
				                                    <dsp:tagAttribute name="aria-labelledby" value="btnApplyCoupons"/>
				                                    <dsp:tagAttribute name="role" value="button"/>
				                                    <dsp:tagAttribute name="class" value="btnApply posBtm btnPrimary button-Large" />
				                                    </dsp:input>
			                                    </c:otherwise>
			                                </c:choose>
			                                
                                    </div>
								</div>
							</c:if>
						</dsp:oparam>
                    </dsp:droplet>
                    </c:if>           
                    <dsp:include page="/cart/couponLineitem.jsp">
                    <dsp:param name="onlineCouponList" value="${onlineCouponList }"/>
                    <dsp:param name="useAnywhereCouponList" value="${useAnywhereCouponList }"/>
                    </dsp:include>
                </dsp:oparam>
                <dsp:oparam name="outputEnd">
                <c:if test="${fn:length(useAnywhereCouponList)>0 ||fn:length(onlineCouponList)>0}">
                    <div class="frmCouponsFooter grid_8 alpha omega <c:if test="${orderHasErrorPrsnlizedItem}">disabled</c:if> ${checkoutCoupon}">
                        <div class="">
                            <c:set var="applyButton"><bbbl:label key="lbl_coupon_apply" language="${language}"/></c:set>
    						<dsp:getvalueof id="cartCheck" param="cartCheck" />
    						<c:if test="${not empty cartCheck}">
    							<input name="cart" type="hidden" value="CART"/>
    						</c:if>
                            <c:choose>
                               <c:when test="${orderHasErrorPrsnlizedItem}">
                                   <dsp:input bean="CartModifierFormHandler.applyCoupons" disabled="disabled" type="submit" value='${applyButton}' id="btnApplyCoupons">
	                               <dsp:tagAttribute name="aria-pressed" value="false"/>
	                               <dsp:tagAttribute name="aria-labelledby" value="btnApplyCoupons"/>
	                               <dsp:tagAttribute name="role" value="button"/>
	                               <dsp:tagAttribute name="class" value="btnApply posBtm btnPrimary button-Large" />
	                               </dsp:input>
                              </c:when>
                              <c:otherwise>
                              	   <dsp:input bean="CartModifierFormHandler.applyCoupons" type="submit" value='${applyButton}' id="btnApplyCoupons">
	                               <dsp:tagAttribute name="aria-pressed" value="false"/>
	                               <dsp:tagAttribute name="aria-labelledby" value="btnApplyCoupons"/>
	                               <dsp:tagAttribute name="role" value="button"/>
	                               <dsp:tagAttribute name="class" value="btnApply posBtm btnPrimary button-Large" />
	                               </dsp:input>
                              </c:otherwise>
                              </c:choose>
                        </div>
                    </div>
                    </c:if>
                </dsp:oparam>
                <dsp:oparam name="empty">
                    <dsp:droplet name="IsEmpty">
                        <dsp:param name="value" param="couponPage"/><%--This parameter is sent from the coupon page not the cart page --%>
                        <dsp:oparam name="false">
                            <p><bbbl:label key="lbl_coupon_no_coupons_message" language="${language}"/></p>
                        </dsp:oparam>
                    </dsp:droplet>
                </dsp:oparam>
            </dsp:droplet>
      </dsp:form>
</dsp:page>
