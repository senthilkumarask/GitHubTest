<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<dsp:page>

<div class="small-12 columns">
	<img alt="" src="/tbs/resources/img/icons/kirsch_icon.jpg">
	<h2 class="divider"></h2>
	<div class="row">
		<div class="small-12 columns">
			<p class="p-secondary"> Congratulations and thank you for ordering from the Kirsch Design Studio at Bed Bath & Beyond. 
			<br />
			Attached is a summary of your order.
			</p>
		</div>
		<div class="small-12 columns">
			<p class="p-secondary"> <span style="text-decoration: underline;">WHAT TO EXPECT NEXT WITH YOUR ORDER</span> </p>
			<p class="p-secondary"> You will receive the following emails from customer.service@kirsch.bedbathandbeyond.com:</p>	
			<ol>
				<li>	Order confirmation with estimated ship dates
				<li>	Confirmation of final ship dates when order goes to production
				<li>	Shipping notifications with tracking information as each product ships	
			
			</ol>
			
		</div>
		<div class="small-12 columns">
			<p class="p-secondary"><span style="text-decoration: underline;">KIRSCH CUSTOMER ORDER RETURN INFORMATION</span></p>
			<p class="p-secondary">We are pleased to offer product that is custom-made to your specifications. Because the products are built to your specifications, the order cannot be cancelled or returned once it has been manufactured.</p>
		</div>
		
		<div class="small-12 columns">
			<p class="p-secondary"> For the Warranty and Exchange Policy covered by Kirsch Trust, please visit http://kirsch.bedbathandbeyond.com/trust </p>	
		</div>
		
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
		
		<div class="small-12 columns">
			<p class="p-secondary"><span style="text-decoration: underline;">CONTACT US</span></p>
			<div class="small-12 large-4 columns">
				<p class="p-secondary"> Custom Order Inquiries: </p>
			</div>
			<div class="small-12 large-6 columns">
				<ul class="address">
					<li>kirsch.bedbathandbeyond.com 
					<li>Call 1-800-642-8307
					<li>Mon-Fri 8AM -7PM EST, Sat 9AM-5:30PM EST 
					<li>Email: customer.service@kirsch.bedbathandbeyond.com
				</ul>
			</div>
		</div>
	</div>
</div>


</dsp:page>