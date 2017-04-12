<dsp:page>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<%-- <h3 class="overview"><a href="${contextPath}/account/myaccount.jsp"><bbbl:label key="lbl_myaccount_overview" language ="${pageContext.request.locale.language}"/></a></h3> --%>
<dsp:getvalueof var="currentPage" param="currentPage"></dsp:getvalueof>

	<ul class="account-list">
		<c:set var="TBS_BedBathCanadaSite">
			<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
		</c:set>
		 <c:set var="TBS_BedBathUSSite">
			<bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" />
		</c:set>
		
		<c:set var="currentPageLabel">
	       <bbbl:label key="lbl_myaccount_overview" language ="${pageContext.request.locale.language}"/>
	     </c:set>
		<li>
			<a href="${contextPath}/account/myaccount.jsp" ${currentPage eq currentPageLabel ?' class="active"':''}>${currentPageLabel}</a>
		</li>
		
		
		<c:set var="currentPageLabel">
	       <bbbl:label key="lbl_myaccount_orders" language ="${pageContext.request.locale.language}"/>
	     </c:set>
		<li>
			<a href="${contextPath}/account/order_summary.jsp" ${currentPage eq currentPageLabel ?' class="active"':''}>${currentPageLabel}</a>
		</li>
		<c:if test="${MapQuestOn}">
			<c:set var="currentPageLabel">
		      	<bbbl:label key="lbl_myaccount_favorite_stores" language ="${pageContext.request.locale.language}"/>
		    </c:set>
			<li>
				<a href="${contextPath}/account/favoritestore.jsp" ${currentPage eq currentPageLabel ?' class="active"':''}>${currentPageLabel}</a>
			</li>
		</c:if>
		<c:set var="currentPageLabel">
	        <bbbl:label key="lbl_myaccount_address_book" language ="${pageContext.request.locale.language}"/>
	     </c:set>
		<li>
		  	<a href="${contextPath}/account/address_book.jsp" ${currentPage eq currentPageLabel ?' class="active"':''}>${currentPageLabel}</a>
		</li>
		
		<c:set var="currentPageLabel">
	       <bbbl:label key="lbl_myaccount_credit_cards" language ="${pageContext.request.locale.language}"/>
	    </c:set>
		<li>
		    <a href="${contextPath}/account/view_credit_card.jsp" ${currentPage eq currentPageLabel ?' class="active"':''}>${currentPageLabel}</a>
		</li>
		
		<c:set var="currentPageLabel">
	       <bbbl:label key="lbl_myaccount_registries" language ="${pageContext.request.locale.language}"/>
	    </c:set>
		<li>
			<a href="${contextPath}/giftregistry/my_registries.jsp" ${currentPage eq currentPageLabel ?' class="active"':''}>${currentPageLabel}</a>
		</li>
		
		<c:set var="currentPageLabel">
	      <bbbl:label key="lbl_myaccount_wish_list" language ="${pageContext.request.locale.language}"/>
	    </c:set>
		<li>
		    <a href="${contextPath}/wishlist/wish_list.jsp" ${currentPage eq currentPageLabel ?' class="active"':''}>${currentPageLabel}</a>
		</li>
		<%-- RM# 35525 Coupon Wallet is not in scope for TBS Next --%>
		<%-- <c:if test="${CouponOn}">
			<c:set var="currentPageLabel">
				<bbbl:label key="lbl_myaccount_coupons" language ="${pageContext.request.locale.language}"/>
			</c:set>
			<li>
				<a href="${contextPath}/account/coupons.jsp" ${currentPage eq currentPageLabel ?' class="active"':''}>${currentPageLabel}</a>
			</li>
		</c:if> --%>
		
		<c:set var="currentPageLabel">
	        <bbbl:label key="lbl_myaccount_personal_info" language ="${pageContext.request.locale.language}"/>
	    </c:set>  
	   	<li>
			<a href="${contextPath}/account/personalinfo.jsp" ${currentPage eq currentPageLabel ?' class="active"':''}>${currentPageLabel}</a>
		</li>
		
		<c:if test= "${HarteHanksOn}">
			<c:set var="currentPageLabel">
	        	<bbbl:label key="lbl_myaccount_preferences" language ="${pageContext.request.locale.language}"/>
		    </c:set>
			<li>
				<a href="${contextPath}/account/preferences.jsp" ${currentPage eq currentPageLabel ?' class="active"':''}>${currentPageLabel}</a>
			</li>
		</c:if>
		
		<%-- KP COMMENT: Page not present, unknown if in scope --%>
		<%-- <c:set var="currentPageLabel">
	      <bbbl:label key="lbl_myaccount_levolor_projects" language ="${pageContext.request.locale.language}"/>
	    </c:set>
        <c:if test="${KirschOn}">
			<li>
				<a href="${contextPath}/account/kirsch.jsp" onclick="javascript:externalLinks('Levolor Projects')"; ${currentPage eq currentPageLabel ?' class="active"':''}>${currentPageLabel}</a>
			</li>
		</c:if> --%>
		<li><dsp:a href=""><dsp:property bean="ProfileFormHandler.logout" value="true"/><bbbl:label key="lbl_header_logout" language="${pageContext.request.locale.language}" /></dsp:a></li>
	</ul>
</dsp:page>