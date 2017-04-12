<%--
  This page will render third party tags when Home page is cached
--%>
<%@ page contentType="text/html; charset=UTF-8" %>

<%@page trimDirectiveWhitespaces="true"%>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart" />
<dsp:importbean bean="/com/bbb/internationalshipping/droplet/InternationalShippingContextSetterDroplet"/>
<dsp:getvalueof var="currentSiteId" bean="Site.id" />
<dsp:param name="order" bean="ShoppingCart.current"/>
<dsp:param name="lastOrder" bean="ShoppingCart.last"/>
<dsp:getvalueof var="billingAddressCurrent" param="order.billingAddress"/>
<dsp:getvalueof var="billingAddressLast" param="lastOrder.billingAddress"/>
<dsp:getvalueof var="profileId" bean="Profile.id"/>
<dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>
<dsp:importbean bean="/com/bbb/profile/session/SessionBean"/>
<dsp:getvalueof var="valueMap" bean="SessionBean.values" />
<c:set var="BedBathCanadaSiteCode"><bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" /></c:set>
<c:set var="countryCode">CA</c:set>
<c:set var="countryCode">CAD</c:set>
 <c:if test="${currentSiteId ne BedBathCanadaSiteCode}">
 	<c:set var="countryCode">${valueMap.defaultUserCountryCode}</c:set>
	<c:set var="currencyCode">${valueMap.defaultUserCurrencyCode}</c:set>
 </c:if>


<c:choose>
	<c:when test="${isTransient eq 'false'}">
		<dsp:getvalueof var="prop23" value="${profileId}"/>
		<dsp:getvalueof var="eVar38"  value="${profileId}"/>
		
		<dsp:getvalueof var ="securityStatus" bean="/atg/userprofiling/Profile.securityStatus"/>
		<c:choose>
			<c:when test="${securityStatus eq '2' }">
				<dsp:getvalueof var="prop16" value="recognized user"/>
				<dsp:getvalueof var="eVar16" value="recognized user" />
			</c:when>
			<c:otherwise>
				<dsp:getvalueof var="prop16" value="registered user"/>
				<dsp:getvalueof var="eVar16" value="registered user" />
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<dsp:getvalueof var="prop16" value="non registered user"/>
		<dsp:getvalueof var="eVar16" value="non registered user"/>
	</c:otherwise>
</c:choose>

 <c:choose>
   	<c:when test="${isTransient == 'false'}">
		<c:set var="email">
			<dsp:valueof bean="Profile.email" />
		</c:set>
	</c:when>
   	<c:otherwise>
   		<c:choose>
   			<c:when test="${billingAddressCurrent ne null}">
   				<c:set var="email" value="${billingAddressCurrent.email}"></c:set>
   			</c:when>
   			<c:otherwise>
   				<c:set var="email" value="${billingAddressLast.email}"></c:set>
   			</c:otherwise>
   		</c:choose>
   	</c:otherwise>
</c:choose>

<dsp:page>

	<%-- adding this flag for International Shipping switch--%>
	<c:set var="internationalShippingOn" scope="request"><bbbc:config key="international_shipping_switch" configName="International_Shipping"/></c:set>
	<%-- adding this flag for International Merchant Id --%>
	<c:set var="internationalMerchantId" scope="request"><bbbc:config key="international_merchant_id" configName="International_Shipping"/></c:set>
	<%-- adding this flag for International Welcome Mat Url --%>
	<c:set var="welcomeMatUrl" scope="request"><bbbc:config key="welcome_mat_url" configName="International_Shipping"/></c:set>
	

	<script type="text/javascript">
	 	<jsp:useBean id="dateTimeNow" class="java.util.Date" />
	    BBB.config.datePickerDateToday = new Date('<fmt:formatDate value="${dateTimeNow}" pattern="MM.dd.yyyy" />'.replace(/\./g,'/'));

		function bbbThirdParty(){
		    window.tmParam = {site_id:'${currentSiteId}'};
			window.tmParam.customer_email = '${email}';	
			window.tmParam.profile_id = '${profileId}';
			window.tmParam.GAAccount = 'GAAccountId';
			window.tmParam.trackPageview = '_trackPageview';
			window.tmParam.customer_country = '${countryCode}';
			window.tmParam.currency_code = '${currencyCode}';
			isTransient='${isTransient}';
			//omniture
			if(typeof s !=='undefined') {
				<c:if test="${isTransient eq 'false'}">
				s.prop23 = '${profileId}';
				s.eVar38 ='${profileId}';
				</c:if>
				s.prop16= '${prop16}';
				s.eVar16= '${eVar16}';
				s.pageName='Home Page';
	            s.channel='Home Page'; <%--set s.channel equal to the main nav category--%>
	            s.prop1='Home Page';<%--set prop1 equal to the page type--%>
	            s.prop2='Home Page';
	            s.prop3='Home Page';<%-- sub categories--%>
				s.eVar9='${pageContext.request.serverName}';
				fixSpacing();
				var s_code=s.t();if(s_code)document.write(s_code);
		   }
		
			//tagman
			(function(d,s){
				function loadLevexisJS() {
					var client = 'bedbathbeyond'; 
					var siteId = 1; 
					// do not edit 
					var a=d.createElement(s),b=d.getElementsByTagName(s)[0]; 
					a.async=true;a.type='text/javascript'; 
					a.src='//sec.levexis.com/clients/'+client+'/'+siteId+'.js'; 
					a.tagman='st='+(+new Date())+'&c='+client+'&sid='+siteId; 
					b.parentNode.insertBefore(a,b); 
				}
				if (window.attachEvent) {
			        window.attachEvent('onload', loadLevexisJS);
			    }
			    else {
			        window.addEventListener('load', loadLevexisJS, false);
			    }
			})(document,'script');
		}
	</script>
	
	 <dsp:droplet name="InternationalShippingContextSetterDroplet">
			<dsp:oparam name="output">
			<dsp:getvalueof var="countryCode" param="countryCode"/>
			</dsp:oparam>
	</dsp:droplet>
			
	 
	
	<c:set var="sddOnHomePage" scope="request">
		<bbbc:config key="sddOnHomePage" configName="SameDayDeliveryKeys" />
	</c:set>
	
	<input type="hidden" value="${sddOnHomePage}" id="onOffSddFlag"/>
	
	<%-- START Script for International Shipping Welcome Mat --%>
	<c:if test="${internationalShippingOn and countryCode ne 'US'}">
             
		<script type="text/javascript">
			function wlcme51func(url) {
				var wlcme51 = document.createElement("script");
				wlcme51.src = url;
				wlcme51.type = "text/javascript";
				document.getElementsByTagName("head")[0]
						.appendChild(wlcme51);
			}

			// Drop / Check for cookie to ensure visitor only sees Welcome Mat once per session
			function isWelcome() {
				var c_name = 'wlcme';
				if (document.cookie.length > 0) {
					c_start = document.cookie.indexOf(c_name + "=");
					if (c_start != -1) {
						c_start = c_start + c_name.length + 1;
						c_end = document.cookie.indexOf(";", c_start);
						if (c_end == -1)
							c_end = document.cookie.length;
						return unescape(document.cookie.substring(c_start,
								c_end));
					}
				}
				return "";
			}

			if (!isWelcome()) {

				wlcme51func("${welcomeMatUrl}?merchId=${internationalMerchantId}&countryId=${countryCode}&setCookie=Y");
			}
		</script>
	</c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/Storefront/j2ee/store.war/includes/pageStart.jsp#2 $$Change: 635969 $--%>
