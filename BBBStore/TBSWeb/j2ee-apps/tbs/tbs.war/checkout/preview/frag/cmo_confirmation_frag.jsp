<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<dsp:page>

<div class="small-12 columns">

	<div class="row">			
		<dsp:droplet name="/com/bbb/commerce/order/droplet/TBSGetStoreAddressDroplet">
			<dsp:oparam name="output">
				<dsp:getvalueof  param="storeDetails" var="storeDetails"/>
				
				<div class="small-12 columns">
					<p class="p-secondary"><span style="text-decoration: underline;">Order Placed At</span></p>
					<div class="small-12 large-4 columns">
						<ul class="address">
							<li><c:out value="${storeDetails.storeName}"/>&nbsp;#<c:out value="${storeDetails.storeId}"/>
							<li><c:out value="${storeDetails.address}"/>
							<li><c:out value="${storeDetails.city}"/>, &nbsp;<c:out value="${storeDetails.state}"/>&nbsp;<c:out value="${storeDetails.postalCode}"/>
							<li>Phone: <c:out value="${storeDetails.storePhone}"/>
						</ul>
					</div>
				</div>
				
			</dsp:oparam>
		</dsp:droplet>
	</div>
</div>


</dsp:page>