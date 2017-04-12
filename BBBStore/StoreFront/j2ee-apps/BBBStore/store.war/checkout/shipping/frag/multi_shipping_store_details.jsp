<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
<c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>

<dsp:droplet name="/com/bbb/selfservice/SearchStoreDroplet">
	<dsp:param name="storeId" param="storeId" />
	<dsp:oparam name="output">
		<li><strong>
			<bbbl:label key="lbl_shipping_store_pickup" language="<c:out param='${language}'/>"/>
		</strong></li>
		<li class="smallText marTop_5">
			<bbbl:label key="lbl_shipping_available_for_store_pickup" language="<c:out param='${language}'/>"/>
		</li>
		<li class="address">
			<p><strong><dsp:valueof param="StoreDetails.storeName"/></strong></p>
			<p class="street"><dsp:valueof param="StoreDetails.address"/></p>
			<p><dsp:valueof param="StoreDetails.city"/>,&nbsp;<dsp:valueof param="StoreDetails.state"/>,&nbsp;<dsp:valueof param="StoreDetails.postalCode"/></p>
			<p class="zipCode"><dsp:valueof param="StoreDetails.storePhone"/></p>
		</li>
		<li class="marTop_5"><a href="#" class="upperCase marTop_5 changeStore"><strong>
			<bbbl:label key="lbl_cartdetail_change" language="<c:out param='${language}'/>"/>
		</strong></a></li>
	</dsp:oparam>
</dsp:droplet>
		
	

</dsp:page>