<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/com/bbb/wishlist/BBBWishlistItemCountDroplet" />
	<dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler" />
	<dsp:importbean bean="/atg/multisite/Site" />
    <dsp:getvalueof id="currentSiteId" bean="Site.id" />	
	
	<bbb:pageContainer index="false" follow="false">
		<jsp:attribute name="section">accounts</jsp:attribute>
		<jsp:body>
	<div id="pageWrapper" class="wishlist">
		<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
			<dsp:param bean="GiftlistFormHandler.formExceptions"
						name="exceptions" />
			<dsp:oparam name="output">
				<LI><dsp:valueof param="message" />
			</dsp:oparam>
		</dsp:droplet>
		
		<div class="grid_12">
				<h3 class="wishList">Wish List</h3>
		</div>
			
				<dsp:form action="wish_list_item_count.jsp" method="post">
							
					<dsp:setvalue beanvalue="Profile.wishlist" param="wishlist" />
					<dsp:setvalue paramvalue="wishlist.giftlistItems" param="items" />
					<dsp:setvalue paramvalue="wishlist.id" param="giftlistId" />
					<dsp:input
								bean="GiftlistFormHandler.updateGiftlistItemsSuccessURL"
								type="hidden" value="wish_list.jsp" />
					<dsp:input bean="GiftlistFormHandler.updateGiftlistItemsErrorURL"
								type="hidden" value="wish_list.jsp" />
					<dsp:input bean="GiftlistFormHandler.giftlistId"
								paramvalue="giftlistId" type="hidden" />
				</dsp:form>
				<dsp:getvalueof var="titems" param="items" vartype="java.util.List"></dsp:getvalueof>
	
				<dsp:droplet name="IsEmpty">
					<dsp:param name="value" param="items" />
					<dsp:oparam name="false">
						<dsp:droplet name="BBBWishlistItemCountDroplet">
							<dsp:param name="array" param="items" />
							<dsp:param name="wishlistId" param="giftlistId" />
							<dsp:param name="siteId" value="${currentSiteId}"/>
							<dsp:oparam name="outputStart">
							</dsp:oparam>
							<dsp:oparam name="output">
											<li>
									<b>Your Wishlist has <dsp:valueof param="wishlistItemCount"></dsp:valueof> items.</b>	
									<dsp:getvalueof var="itemCount" param="size" />
								
											</li>
										<br>
							</dsp:oparam>
							<dsp:oparam name="outputEnd">
								<b>Total Number Of Items : <dsp:valueof value="${itemCount}" converter="number" /><b>
							</dsp:oparam>
							</dsp:droplet>
					</dsp:oparam>

					<dsp:oparam name="true">
						<b>Total Number Of Items : 0</b>
					</dsp:oparam>
					<dsp:oparam name="outputEnd">
					
					</dsp:oparam>
				</dsp:droplet>

					</div>
				
		<script type="text/javascript">
			BBB.accountManagement = true;
		</script>

   </jsp:body>
	</bbb:pageContainer>
</dsp:page>