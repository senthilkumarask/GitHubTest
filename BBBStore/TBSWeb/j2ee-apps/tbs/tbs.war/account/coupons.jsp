<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/com/bbb/account/GetCouponsDroplet" />
	<dsp:importbean var="Profile" bean="/atg/userprofiling/Profile"/>

	<%-- Variables --%>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<c:set var="section" value="accounts" scope="request" />
	<c:set var="pageWrapper" value="coupons myAccount" scope="request" />

	<bbb:pageContainer index="false" follow="false">

		<jsp:attribute name="bodyClass">my-account</jsp:attribute>
		<jsp:attribute name="section">${section}</jsp:attribute>
		<jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>

		<jsp:body>

			<c:set var="scene7Path">
				<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
			</c:set>

			<div class="row" id="content">
				<div class="small-12 columns">
					<h1><bbbl:label key="lbl_personalinfo_myaccount" language ="${pageContext.request.locale.language}"/>: <span class="subheader"><bbbl:label key="lbl_myaccount_coupons" language ="${pageContext.request.locale.language}"/></span></h1>
				</div>
				<div class="show-for-medium-down small-12">
					<a class="right-off-canvas-toggle secondary expand button">Account Menu</a>
				</div>
				<div class="large-3 columns small-medium-right-off-canvas-menu left-nav">
					<c:import url="/account/left_nav.jsp">
						<c:param name="currentPage"><bbbl:label key="lbl_myaccount_coupons" language ="${pageContext.request.locale.language}"/></c:param>
					</c:import>
				</div>
				<div class="small-12 large-9 columns">
					<dsp:droplet name="GetCouponsDroplet">
                <dsp:param name="EMAIL_ADDR" bean="Profile.email"/>
                <dsp:param name="MOBILE_NUMBER" bean="Profile.mobileNumber"/>
                <dsp:oparam name="output">
                    <h3><bbbl:label key="lbl_coupon_title" language="${pageContext.request.locale.language}"/></h3>
                    <dsp:getvalueof var="onlineCouponList" param="onlineCouponList" />
                    <dsp:getvalueof var="useAnywhereCouponList" param="useAnywhereCouponList" />
                    <c:choose>
                    <c:when test="${empty useAnywhereCouponList && empty onlineCouponList}" >
                    <p><bbbl:label key="lbl_coupon_no_coupons_message" language ="${pageContext.request.locale.language}"/></p>                  
                    </c:when>
                    <c:otherwise>
                    <p><bbbt:textArea key="txtarea_coupons_description" language ="${pageContext.request.locale.language}"/></p>                    
                    </c:otherwise>
                    </c:choose>
                    <c:if test="${fn:length(useAnywhereCouponList)>0}">
                    <dsp:droplet name="ForEach">
                        <dsp:param name="array" param="useAnywhereCouponList" />
                            <dsp:oparam name="outputStart">
                                <h2 class="couponsHeader"><bbbl:label key="lbl_coupon_useanywhere" language="${pageContext.request.locale.language}"/></h2>
                            </dsp:oparam>
                            <dsp:oparam name="output">
                            <dsp:setvalue param="CouponListVo" paramvalue="element"/>
                            <dsp:getvalueof var="CouponListVo" vartype="java.lang.Object" param="CouponListVo"/>
                            <dsp:getvalueof var="count" param="count" />
                            <c:set var="barcode" value="${CouponListVo.uniqueCouponCd}"/>
                                    <div class="coupon small-4 columns no-padding" id="couponTest">
                                        <c:if test="${CouponListVo.disqualify ne 'true'}">
                                            <div class="couponImageWrap useAnywhere" data-barcode='${barcode}' data-count='${count}'>
                                                <div class="flipper">
                                                <div class="couponImage fl">
                                                    <!-- <div class="couponImageLabel expToday">
                                                        <span>
                                                            <bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}"/>
                                                        </span>
                                                    </div> -->

                                                    <!-- <div class="couponImageLabel justAdded">
                                                        <span>
                                                            <bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}"/>
                                                        </span>
                                                    </div> -->

                                                    <c:set var="count" value="${CouponListVo.expiryCount}"/>
                                                    <c:if test="${count eq 1 }">
                                                    <div class="couponImageLabel expToday">
                                                        <span>
                                                            <bbbl:label key="lbl_coupon_expiresIn_0" language="${pageContext.request.locale.language}"/>
                                                        </span>
                                                    </div>
                                                    </c:if>
                                                    <c:if test="${count eq 2 }">
                                                    <div class="couponImageLabel expSoon">
                                                        <span>
                                                            <bbbl:label key="lbl_coupon_expiresIn_1" language="${pageContext.request.locale.language}"/>
                                                        </span>
                                                    </div>
                                                    </c:if>
                                                    <c:if test="${count eq 3 }">
                                                    <div class="couponImageLabel expSoon">
                                                        <span>
                                                            <bbbl:label key="lbl_coupon_expiresIn_2" language="${pageContext.request.locale.language}"/>
                                                        </span>
                                                    </div>
                                                    </c:if>
                                                    <c:if test="${count eq 4 }">
                                                    <div class="couponImageLabel expSoon">
                                                        <span>
                                                            <bbbl:label key="lbl_coupon_expiresIn_3" language="${pageContext.request.locale.language}"/>
                                                        </span>
                                                    </div>
                                                    </c:if>
                                                    <c:if test="${count eq 5 }">
                                                    <div class="couponImageLabel expSoon">
                                                        <span>
                                                            <bbbl:label key="lbl_coupon_expiresIn_4" language="${pageContext.request.locale.language}"/>
                                                        </span>
                                                    </div>
                                                    </c:if>
                                                    <c:if test="${count eq 6 }">
                                                    <div class="couponImageLabel expSoon">
                                                        <span>
                                                            <bbbl:label key="lbl_coupon_expiresIn_5" language="${pageContext.request.locale.language}"/>
                                                        </span>
                                                    </div>
                                                    </c:if>
                                                    <span class="border"></span>

                                                    <dsp:getvalueof var="promoIMGURL" value="${CouponListVo.couponsImageUrl}"/>
                                                    <c:set var="barcode" value="${CouponListVo.uniqueCouponCd}"/>
                                                    <c:choose>
                                                        <c:when test="${(fn:indexOf(promoIMGURL, 'http') == 0) || (fn:indexOf(promoIMGURL, '//') == 0)}">
                                                            <dsp:getvalueof var="img" value="${CouponListVo.couponsImageUrl}"/>
                                                            <img src="${img}" height="100%" width="100%" alt="${CouponListVo.couponsDescription}" />
                                                        </c:when>
                                                        <c:otherwise>
                                                             <dsp:getvalueof var="img1" value="${scene7Path}/${CouponListVo.couponsImageUrl}"/>
                                                                <img src="${img1}" height="100%" width="100%" alt="${CouponListVo.couponsDescription}" />
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                                <div id="barcode${count}" class="barCode">
                                                    <div class="barcodeInstructions">
                                                        <%-- TODO - make BCC label--%>
                                                        Present barcode to be scanned at register
                                                    </div>
                                                    <div class="barcodeCanvas">

                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                            <div class="couponInfo fl marTop_10 grid_2 alpha omega">
                                                <c:if test="${not empty CouponListVo.description}">
                                                    <h2 class="couponHeader grid_2"><dsp:valueof value="${CouponListVo.description}"/></h2>
                                                </c:if>
                                                <!-- <c:if test="${not empty CouponListVo.couponsDescription}">
                                                    <p><dsp:valueof value="${CouponListVo.couponsDescription}"/></p>
                                                </c:if>
                                                <c:if test="${not empty CouponListVo.expiryDate}">
                                                    <p class="couponExp grid_2 alpha omega">Expires <dsp:valueof value="${CouponListVo.expiryDate}"/></p>
                                                </c:if> -->
                                                <dsp:droplet name="IsEmpty">
                                                    <dsp:param name="value" value="${CouponListVo.couponsExclusions}"/>
                                                    <dsp:oparam name="false">
                                                        <dsp:getvalueof var="promoId" value="${CouponListVo.promoId}" />
                                                        <a href="${contextPath}/checkout/coupons/exclusions.jsp?item=${CouponListVo.entryCd}" class="popup" data="${CouponListVo.entryCd}" title="Exclusions"><bbbl:label key="lbl_coupon_exclusions_title" language ="${pageContext.request.locale.language}"/></a>
                                                    </dsp:oparam>
                                                </dsp:droplet>
                                                <a class="redeem btnSecondary button-Med">REDEEM IN STORE</a>
                                            </div>
                                        </c:if>
                                    </div>

                                <!-- <p id="barcode${count}"></p> -->
                            </dsp:oparam>
                            <dsp:oparam name="empty">
                                <p><bbbt:label key="lbl_coupons_no_coupon" language ="${pageContext.request.locale.language}"/></p>
                            </dsp:oparam>
                        </dsp:droplet>
                        </c:if>
                        <%--start test --%>
                        <c:if test="${fn:length(onlineCouponList)>0}">
                        <dsp:droplet name="ForEach">
                        <dsp:param name="array" param="onlineCouponList" />
                        <dsp:oparam name="outputStart">
                                <h2 class="couponsHeader grid_10 alpha omega"><bbbl:label key="lbl_coupon_useOnline" language="${pageContext.request.locale.language}"/></h2>
                            </dsp:oparam>
                            <dsp:oparam name="output">
                            <dsp:getvalueof var="count" param="count" />
                                    <div class="coupon small-4 columns no-padding" id="couponTest">
                                        <dsp:setvalue param="CouponListVo" paramvalue="element"/>
                                        <dsp:getvalueof var="CouponListVo" vartype="java.lang.Object" param="CouponListVo"/>
                                        <c:if test="${CouponListVo.disqualify ne 'true'}">
                                        <div class="couponImageWrap" data-barcode='${barcode}' data-count='${count}'>
                                                <div class="flipper">
                                            <div class="couponImage fl">
                                                <c:set var="count" value="${CouponListVo.expiryCount}"/>
                                                <c:if test="${count eq 1 }">
                                                <div class="couponImageLabel expToday">
                                                    <span>
                                                        <bbbl:label key="lbl_coupon_expiresIn_0" language="${pageContext.request.locale.language}"/>
                                                    </span>
                                                </div>
                                                </c:if>
                                                <c:if test="${count eq 2 }">
                                                <div class="couponImageLabel expSoon">
                                                    <span>
                                                        <bbbl:label key="lbl_coupon_expiresIn_1" language="${pageContext.request.locale.language}"/>
                                                    </span>
                                                </div>
                                                </c:if>
                                                <c:if test="${count eq 3 }">
                                                <div class="couponImageLabel expSoon">
                                                    <span>
                                                        <bbbl:label key="lbl_coupon_expiresIn_2" language="${pageContext.request.locale.language}"/>
                                                    </span>
                                                </div>
                                                </c:if>
                                                <c:if test="${count eq 4 }">
                                                <div class="couponImageLabel expSoon">
                                                    <span>
                                                        <bbbl:label key="lbl_coupon_expiresIn_3" language="${pageContext.request.locale.language}"/>
                                                    </span>
                                                </div>
                                                </c:if>
                                                <c:if test="${count eq 5 }">
                                                <div class="couponImageLabel expSoon">
                                                    <span>
                                                        <bbbl:label key="lbl_coupon_expiresIn_4" language="${pageContext.request.locale.language}"/>
                                                    </span>
                                                </div>
                                                </c:if>
                                                <c:if test="${count eq 6 }">
                                                <div class="couponImageLabel expSoon">
                                                    <span>
                                                        <bbbl:label key="lbl_coupon_expiresIn_5" language="${pageContext.request.locale.language}"/>
                                                    </span>
                                                </div>
                                                </c:if>

                                                <span class="border"></span>

                                                <dsp:getvalueof var="promoIMGURL" value="${CouponListVo.couponsImageUrl}"/>

                                                <c:choose>
                                                    <c:when test="${(fn:indexOf(promoIMGURL, 'http') == 0) || (fn:indexOf(promoIMGURL, '//') == 0)}">
                                                        <dsp:getvalueof var="img" value="${CouponListVo.couponsImageUrl}"/>
                                                        <img src="${img}" height="74" width="398" alt="${CouponListVo.couponsDescription}" />
                                                    </c:when>
                                                    <c:otherwise>
                                                         <dsp:getvalueof var="img1" value="${scene7Path}/${CouponListVo.couponsImageUrl}"/>
                                                            <img src="${img1}" height="74" width="398" alt="${CouponListVo.couponsDescription}" />
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div id="barcode${count}" class="barCode">${barcode}</div>
                                        </div>
                                    </div>
                                            <div class="couponInfo small-12 columns">
                                                <c:if test="${not empty CouponListVo.description}">
                                                    <h2 class="couponHeader"><dsp:valueof value="${CouponListVo.description}"/></h2>
                                                </c:if>
                                                <%-- <c:if test="${not empty CouponListVo.couponsDescription}">
                                                    <p><dsp:valueof value="${CouponListVo.couponsDescription}"/></p>
                                                </c:if> 
                                                <c:if test="${not empty CouponListVo.expiryDate}">
                                                    <p class="couponExp grid_2 alpha omega">Expires <dsp:valueof value="${CouponListVo.expiryDate}"/></p>
                                                </c:if> --%>


                                                <dsp:droplet name="IsEmpty">
                                                    <dsp:param name="value" value="${CouponListVo.couponsExclusions}"/>
                                                    <dsp:oparam name="false">
                                                        <dsp:getvalueof var="promoId" value="${CouponListVo.promoId}" />
                                                        <a href="${contextPath}/checkout/coupons/exclusions.jsp?item=${CouponListVo.entryCd}" class="popup small-12 columns" data="${CouponListVo.entryCd}" title="Exclusions"><bbbl:label key="lbl_coupon_exclusions_title" language ="${pageContext.request.locale.language}"/></a>
                                                    </dsp:oparam>
                                                </dsp:droplet>
                                            </div>
                                        </c:if>
                                    </div>
                            </dsp:oparam>
                            <dsp:oparam name="empty">
                                <p><bbbt:label key="lbl_coupons_no_coupon" language ="${pageContext.request.locale.language}"/></p>
                            </dsp:oparam>
                        </dsp:droplet>
                        </c:if>
                        <%--end test --%>
                    </dsp:oparam>
                    <dsp:oparam name="error">
                        <div class="grid_10">
                        <dsp:getvalueof var="varSystemSrror" param="systemerror"></dsp:getvalueof>
                         <c:choose>
                            <c:when test="${varSystemSrror=='err_mycoupons_system_error' }">
                                 <label class="error">
                                    <bbbe:error key="err_mycoupons_system_error" language="${pageContext.request.locale.language}"/>
                               </label>
                            </c:when>
                       </c:choose>
                       </div>
                    </dsp:oparam>
                </dsp:droplet>

				</div>
			</div>

		</jsp:body>

		<jsp:attribute name="footerContent">
			<script type="text/javascript">
				if (typeof s !== 'undefined') {
					s.pageName = 'My Account>Coupons';
					s.channel = 'My Account';
					s.prop1='My Account';
					s.prop2='My Account';
					s.prop3='My Account';
					s.events="event11";
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
