<%--

  Tag provides integration with Tell Apart. 
  
  This tag includes on different pages of the site. 
  Accordingling to the attribute provided to this tag, it renders the 
  script required for tell apart integration.
  
  This tag accepts the following attributes:
  
  actionType (mandatory) - This will render type of action that needs to be triggered at tell apart end.
							For example: login, updatecart, tx (transaction), pv (page view).
  
  pageViewType (optional) - this attribute renders the different type of information required by tell apart as per
							type of view. For example: Product, ProductCategory, SearchQuery, Other
  
  
--%>

<%@ include file="/includes/taglibs.jspf" %>
<%@ attribute name="actionType" %>
<%@ attribute name="pageViewType" %>

<%@ attribute name="addsku" %>
<%@ attribute name="user" %>
<%@ attribute name="transection" %>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:getvalueof var="appid" bean="Site.id" />
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart" />
<dsp:importbean bean="com/bbb/account/OrderHistoryDroplet" />
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet"/>
<dsp:getvalueof var="onlineOrderNumber" bean="ShoppingCart.last.onlineOrderNumber"/>
<dsp:getvalueof var="bopusOrderNumber" bean="ShoppingCart.last.bopusOrderNumber"/>
<dsp:getvalueof id="email" bean="Profile.email"/>
	
	<c:if test="${true}">
	<!--Begin TellApart-->
  	<script language="JavaScript" type="text/javascript">
   		var __cmbLoaded=false,__cmbRunnable=null;
   		(function(){try{var b;
    	// The retailer fills in this section of the snippet.
		var actionType = "${actionType}";
    	function d() {
     		var action=TellApartCrumb.makeCrumbAction("<bbbc:config key='tellapart_merchant_${appid}' configName='ThirdPartyURLs' />",actionType);     
	</c:if>
	
	<c:choose>
		<c:when test="${empty onlineOrderNumber}">
			<c:set var="genOrderCode" value="${bopusOrderNumber}" />
		</c:when>
		<c:otherwise>
			<c:set var="genOrderCode" value="${onlineOrderNumber}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test="${actionType == 'login'}">
		
			action.setActionAttr("Name", "<dsp:valueof bean="Profile.firstName"/> <dsp:valueof bean="Profile.lastName"/>");
			action.setActionAttr("Address", "<dsp:valueof bean="Profile.billingAddress.address1"/>");
			action.setActionAttr("City", "<dsp:valueof bean="Profile.billingAddress.city"/>");
			action.setActionAttr("State", "<dsp:valueof bean="Profile.billingAddress.state"/>");
			action.setActionAttr("PostalCode", "<dsp:valueof bean="Profile.billingAddress.postalcode"/>");			
			action.setActionAttr("Country", "<dsp:valueof bean="Profile.billingAddress.country"/>");
			action.setActionAttr("Email", "${email}");
			
		</c:when>
		<c:when test="${actionType == 'pv'}">
			
			<c:choose>
				<c:when test="${pageViewType == 'Product'}">
					<dsp:droplet name="ProductDetailDroplet">
						<dsp:param name="id" param="productId" />
						<dsp:param name="siteId" value="${appid}"/>
						<dsp:param name="skuId" param="skuId"/>
						<dsp:oparam name="output">
							<dsp:getvalueof var="skuVO" param="pSKUDetailVO" />
							<c:if test="${skuVO != null}">
								<dsp:getvalueof var="pDefaultChildSku" param="pSKUDetailVO.skuId" />
							</c:if>
						</dsp:oparam>
					</dsp:droplet>
					// Product Page
					action.setActionAttr("PageType", "Product");
					<c:choose>
						<c:when test="${not empty pDefaultChildSku}">
							action.setActionAttr("SKU", "${pDefaultChildSku}");
						</c:when>
						<c:otherwise>
							action.setActionAttr("SKU", "<dsp:valueof param="productId"/>");
						</c:otherwise>
					</c:choose>	
					action.setActionAttr("ProductCategoryPath", "${categoryPath}");
				</c:when>
				<c:when test="${pageViewType == 'ProductCategory'}">
					// Category Page
					action.setActionAttr("PageType", "ProductCategory");
					action.setActionAttr("ProductCategoryPath", "${categoryPath}");
					
				</c:when>
				<c:when test="${pageViewType == 'SearchResult'}">
					// Search Page
					action.setActionAttr("PageType", "SearchResult");
					action.setActionAttr("SearchQuery", "<dsp:valueof param="Keyword"/>");
				</c:when>
				<c:when test="${pageViewType == 'Other'}">
					action.setActionAttr("PageType", "Other");
					action.setActionAttr("ProductCategoryPath", "Other");
				</c:when>
				<c:otherwise>
					// Home Page and Other Pages
					action.setActionAttr("PageType", "Other");
					action.setActionAttr("ProductCategoryPath", "Other");
				</c:otherwise>
			</c:choose>	
		</c:when>
		
		<c:when test="${actionType == 'updatecart'}">
			
			action.setActionAttr("UpdateCartType", "Full");

			//Iterate on cart item start
			<dsp:droplet name="/atg/commerce/order/droplet/BBBOrderInfoDroplet">
	        	<dsp:param name="order" bean="ShoppingCart.current" />
	            <dsp:oparam name="output">
					<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param name="array" param="orderList" />
						<dsp:oparam name="output">
							action.beginItem();
							action.setItemAttr("SKU", "<dsp:valueof param="element.skuId"/>");
							action.setItemAttr("ProductPrice", "<dsp:valueof param="element.price"/>");
							action.setItemAttr("ProductCurrency", "<dsp:valueof bean="ShoppingCart.current.priceInfo.CurrencyCode"/>");
							action.setItemAttr("ItemCount", "<dsp:valueof param="element.itemCount"/>");
							action.endItem();
						</dsp:oparam>
					</dsp:droplet>			
				</dsp:oparam>
			</dsp:droplet>
			//Iterate on cart item end
			
		</c:when>
			
		
		
		<c:when test="${actionType == 'tx'}">
			action.setActionAttr("Email", "<dsp:valueof bean="ShoppingCart.last.billingAddress.email"/>");
			action.setActionAttr("BillingName", "<dsp:valueof bean="ShoppingCart.last.billingAddress.firstname"/> <dsp:valueof  bean="ShoppingCart.last.billingAddress.middlename"/> <dsp:valueof bean="ShoppingCart.last.billingAddress.lastname"/>");
			action.setActionAttr("BillingAddress", "<dsp:valueof bean="ShoppingCart.last.billingAddress.address1"/> <dsp:valueof bean="ShoppingCart.last.billingAddress.address2"/> <dsp:valueof bean="ShoppingCart.last.billingAddress.address3"/>");
			action.setActionAttr("BillingCity", "<dsp:valueof bean="ShoppingCart.last.billingAddress.city"/>");
			action.setActionAttr("BillingState", "<dsp:valueof bean="ShoppingCart.last.billingAddress.state"/>");
			action.setActionAttr("BillingPostalCode", "<dsp:valueof bean="ShoppingCart.last.billingAddress.postalcode"/>");
			action.setActionAttr("BillingCountry", "<dsp:valueof bean="ShoppingCart.last.billingAddress.country"/>");
			action.setActionAttr("TransactionId", "${genOrderCode}");

			action.setActionAttr("TransactionTotal", "<dsp:valueof bean="ShoppingCart.last.priceInfo.total" number="###,###,###.00" />");
			action.setActionAttr("TransactionTotalCurrency", "<dsp:valueof bean="ShoppingCart.last.priceInfo.CurrencyCode"/>");
			
			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
            <dsp:param name="array" bean="ShoppingCart.last.shippingGroups" />
            <dsp:param name="elementName" value="shippingGroup" />
            <dsp:oparam name="output">
            	<dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
                <dsp:droplet name="/atg/dynamo/droplet/Compare">
                <dsp:param name="obj1" param="shippingGroup.repositoryItem.type"/>
                <dsp:param name="obj2" value="hardgoodShippingGroup"/>
					<dsp:oparam name="equal">
                       	<dsp:getvalueof var="commerceItemRelationshipCount" param="shippingGroup.commerceItemRelationshipCount"/>
                           <c:if test="${commerceItemRelationshipCount gt 0}">
                            action.setItemAttr("ShippingName","${shippingGroup.shippingAddress.firstName} ${shippingGroup.shippingAddress.middleName} ${shippingGroup.shippingAddress.lastName}");
                            action.setItemAttr("ShippingAddress","${shippingGroup.shippingAddress.address1} ${shippingGroup.shippingAddress.address2} ${shippingGroup.shippingAddress.address3}");
                            action.setItemAttr("ShippingCity","${shippingGroup.shippingAddress.city}");
                            action.setItemAttr("ShippingState","${shippingGroup.shippingAddress.state}");
                            action.setItemAttr("ShippingPostalCode","${shippingGroup.shippingAddress.postalCode}");
                            action.setItemAttr("ShippingCountry","${shippingGroup.shippingAddress.country}");
                        	</c:if>
					</dsp:oparam>
               	</dsp:droplet>       
          	</dsp:oparam>
      		</dsp:droplet>                    
			
			//Iterate on cart item start
			<dsp:droplet name="/atg/commerce/order/droplet/BBBOrderInfoDroplet">
	        	<dsp:param name="order" bean="ShoppingCart.last" />
	           <dsp:oparam name="output">
	           		action.setActionAttr("PromoCode", "<dsp:valueof param="PromoCode"/>");
					action.setActionAttr("PromoAmount", "<dsp:valueof param="PromoAmount"/>");
	           		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param name="array" param="orderList" />
						<dsp:oparam name="output">
							action.beginItem();
							action.setItemAttr("SKU", "<dsp:valueof param="element.skuId"/>");
							action.setItemAttr("ProductPrice", "<dsp:valueof param="element.price"/>");
							action.setItemAttr("ProductCurrency", "<dsp:valueof bean="ShoppingCart.last.priceInfo.CurrencyCode"/>");
							action.setItemAttr("ItemCount", "<dsp:valueof param="element.itemCount"/>");
							action.endItem();
						</dsp:oparam>
					</dsp:droplet>			
				</dsp:oparam>
			</dsp:droplet>
			//Iterate on cart item end
			
		</c:when>
		
	</c:choose>
	
	<c:if test="${true}">
		action.setUserId("${email}");
	    action.finalize();
		};
		if("https:"==document.location.protocol)b="<bbbc:config key='tellapart_secure' configName='ThirdPartyURLs'/>";
	    else{for(var g=navigator.userAgent,h=0,e=0,i=g.length;e<i;e++)h^=g.charCodeAt(e);
	    b="<bbbc:config key='tellapart_unsecure' configName='ThirdPartyURLs'/>"+h%10+".js"}if(actionType==="tx"){__cmbRunnable=d;document.write("\x3Cscript type='text/java"+"script'       src='"+b+"'\x3E\x3C/script\x3E");__cmbLoaded=true}
	    else{var a=document.createElement("script");a.src=b;a.onload=function(){__cmbLoaded=true;d()};a.onreadystatechange=function(){if(/loaded|complete/.test(a.readyState)){__cmbLoaded=true;d()}};var s=document.getElementsByTagName("script")[0];
	    s.parentNode.insertBefore(a, s)}}catch(j){}})();
	  </script>
	  <script language="JavaScript" type="text/javascript">
	   if(__cmbRunnable!=null){__cmbRunnable();__cmbRunnable=null};
	  </script>
	 <!--End TellApart-->

</c:if>