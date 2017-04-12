<dsp:page>
	<%@ page import="com.bbb.constants.BBBCoreConstants"%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>	
	<dsp:importbean bean="/com/bbb/selfservice/StateDroplet" />
	<dsp:importbean var="Profile" bean="/atg/userprofiling/Profile" />
	<dsp:importbean var="ActivateBigBlueFormHandler" bean="/com/bbb/browse/ActivateBigBlueFormHandler" />
	<dsp:importbean var="SessionBean" bean="/com/bbb/profile/session/SessionBean"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<bbb:pageContainer>
	<jsp:attribute name="section">browse</jsp:attribute>
	<jsp:attribute name="homepage">bigblue</jsp:attribute>
	<jsp:attribute name="pageWrapper"></jsp:attribute>
	<dsp:getvalueof var="successMessage" bean="/com/bbb/browse/ActivateBigBlueFormHandler.successMessage" />
	<dsp:getvalueof var="emailAddress" bean="SessionBean.couponEmail" />
	<dsp:getvalueof var="expiryDate" bean="SessionBean.couponExpiry" />
		<jsp:body>
		<div id="content" class="container_12 clearfix">
		<div id="cmsPageHead" class="grid_12 clearfix"><h1>Success! You've activated your online offer.</h1>
		</div>	
		<div class="grid_12"> <p>We've saved your coupon for you. Your offer is now tied to your email address, and will be available in checkout.</p> </div> 
		<div class="clearfix"> <div class="grid_4"> <h2>1 Shop As Usual</h2> 
		<p style="line-height: 18px;">Now you can use your offer for purchases online! When you're checking out, simply use the same phone number or email address you used to register the offer, and it will be applied to your qualifying purchase. </p> </div> 
			<div class="grid_4"> <h2>2 Begin Checkout</h2> <p>In the Billing Information section, be sure to use the same email address you used to activate your offer.<br> <br> 
			<span style="color: #bb3249;"><strong>Your Email:</strong></span><br> 
			<a style="color: #bb3249; font-size: 12px;"
							href="mailto:${emailAddress}"><strong>${emailAddress}</strong>
			</a> </p> 
			</div> 
						<div class="grid_4"> <h2>3 Apply Coupon</h2> <p>In the Shipping Options section, you can select the coupon and apply it to your purchase, or save it for a later purchase. Just be sure to use it before the expiration date.<br> 
						<span style="color: #bb3249;"><strong>Expires: ${expiryDate } </strong>
						</span>
					</p> </div> <div class="grid_12">
					<img src="//s7d9.scene7.com/is/image/BedBathandBeyond/add%2Dto%2Dcart?$other$" width="975" height="232">
				</div> 
				<div style="margin-top: 37px;" class="grid_12">
					<strong>Additional Terms and Conditions </strong><br> This offer is available only for use on bedbathandbeyond.com.<br> 20% off one single item will be applied to the highest priced merchandise item in the cart. This offer cannot be applied to gift cards, shipping, gift packaging fees or sales tax.<br> This offer is exclusive for you, using only your email address shown above.<br> This offer is valid for one-time use.<br> This offer will expire on 10/07/2013.<br> Not valid with any other discount or special offer.<br> Not valid for the purchase of Alessi, Arthur Court, Breville®, Britto™ Collection, Brookstone®, DKNY, Dyson Floorcare, kate spade, Kenneth Cole Reaction Home, Kosta Boda, Le Creuset&reg;, Lladró&reg;, Miele, Monique Lhuillier, Nambe&reg;, Nautica&reg;, Orrefors, Riedel, Shun, Swarovski, T-Tech, Vera Wang&reg;, Victorinox Luggage, Vitamix, Waterford&reg;, Wusthof&reg;, or Zwilling; Baby Brezza™, Baby Jogger™, BÉABA&reg;, BOB, Bugaboo, Bumbleride™, Destination Maternity&reg;, ERGObaby&reg;, Foundations&reg;, Maxi-Cosi&reg;, Mountain Buggy, Oeuf, Orbit Baby™, Peg Pérego&reg;, Petunia Pickle Bottom&reg;, Phil &amp; Teds&reg;, Quinny&reg;, Svan&reg;, Teutonia&reg;, Under Armour&reg;, UPPAbaby&reg;, baby furniture, disposable diapers, wipes, formula, baby food or portrait studio services.
					</div> 
					</div>
					</div>
		</jsp:body>
		<jsp:attribute name="footerContent">
		 		<script type="text/javascript">
if (typeof s !== 'undefined') {
	s.pageName='My Account>Offer activation success'; // pageName
	s.channel='My Account';
	s.prop1='My Account';
	s.prop2='My Account';
	s.prop3='My Account';
	s.prop4='My Account';
	s.prop6='${pageContext.request.serverName}'; 
	s.eVar9='${pageContext.request.serverName}';	
	s.server='${pageContext.request.serverName}';
	var s_code = s.t();
	if (s_code)
		document.write(s_code);
}
</script>
		 
		 </jsp:attribute>
	</bbb:pageContainer>
</dsp:page>