<dsp:page>
	<dsp:importbean bean="/com/bbb/account/WalletFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/com/bbb/account/GetCouponsDroplet" />
	<dsp:importbean var="Profile" bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof var="contextPath"bean="/OriginatingRequest.contextPath" />
	<c:set var="section" value="accounts" scope="request" />
	<c:set var="pageWrapper" value="coupons" scope="request" />
	<dsp:getvalueof var="appid" bean="Site.id" />
	
<%-- TEMPLATE FOR EXAMPLE COMPONENT
			<hr />
			<div class="marTop_10 marBottom_10">
				<div class="grid_4 alpha ">
					<img src="images/" />
				</div>
				<div class="grid_4 ">
					<a href="#" target="_blank">www.bedbathandbeyond.com/store/whatever</a>
				</div>
				<div class="grid_4  omega">
					<div class="toggle"><a href='#'>+ HTML</a></div>
					<div class="toggleChild hidden">
						<pre><code  class="language-html">
<c:set var="html">
//html code goes here
</c:set>
${fn:escapeXml(html)}
						</code></pre>

					</div>
					<div class="toggle"><a href='#'>+ CSS</a></div>
					<div class="toggleChild hidden">
						<pre><code class="language-css">
/* css code goes here
						</code></pre>

					</div>
				</div>
			</div>
			
--%>



	<bbb:pageContainer section="accounts">
		<div id="content" class="container_12 clearfix ">

			<div class="grid_12 ">
				<h1 style="background-color:#eee;">Buttons</h1>
				
				<div class="spacing">
					<div class="grid_4 alpha ">
						<h3>Example</h3>
					</div>
					<div class="grid_4 ">
						<h3>Appears On</h3>
					</div>
					<div class="grid_4  omega">
						<h3>Code</h3>
					</div>
				</div>
            </div>


				<div class="grid_12 alpha omgega marTop_10 marBottom_10">
					<div class="grid_4 alpha ">
						<img src="images/btnShopNow.png" />
					</div>
					<div class="grid_4 ">
						<a href="http://bedbathandbeyonddev.com:7003/store/registry/PersonalizedInvitations" target="_blank">Personalized Invitations</a>
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
							<pre><code  class="language-html">
<c:set var="html">
<div class="fr cb"> 
	<a style="height:auto;" href="http://invitations.bedbathandbeyond.com/?utm_source=bedbath&amp;utm_medium=interstitialpage&amp;utm_content=billboard&amp;utm_campaign=201503homepage" class="button-Med registry2-md" target="_blank">Shop Now</a> 
</div>
</c:set>
${fn:escapeXml(html)}
							</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">
.cb {
    clear: both!important;
}

.fr {
    float: right!important;
}
.br .button-Med {
    font-size: 13px!important;
    padding: 8px 12px 9px;
    height: 32px;
}
							</code></pre>

						</div>
					</div>
				</div><%--end component--%>


			<hr />
			<div class="marTop_10">
					<div class="grid_4 alpha ">
						<img src="images/addToCartCertona.png" />
					</div>
					<div class="grid_4 ">
						Certona slots
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
							<pre><code  class="language-html">
<c:set var="html">
<div class="button button_secondary">
	<input title="Add Radiance Traditional 6-Foot 6-Inch x 9-Foot 10-Inch Area Rug in Rust to Your Cart" name="submit" value="ADD TO CART" role="button" onclick="addItemCartOmniture('1043144037', '43144037');" type="submit">
	<input name="_D:submit" value=" " type="hidden">
</div>
</c:set>
${fn:escapeXml(html)}
							</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">
.cartDetail .certonaProducts .productContent div.button {
    margin-left: 17px;
    margin-top: 7px;
}
.by .button, .by .button a, .by .button a:visited {
    font-family: "FuturaStdHeavy",Arial,Helvetica,sans-serif;
    font-size: 14px;
    line-height: 20px;
}
.by .button, .by .button a, .by .button a:visited {
    font-family: FuturaStdHeavy, Arial, Helvetica, sans-serif;
    font-size: 14px;
    line-height: 20px;
}
.button_secondary {
    background-position: -494px -148px;
}
.button, .by .compareProducts .button.addRegButton {
    position: relative;
    float: left;
    display: inline;
    padding-left: 6px;
    background-position: -494px -173px;
}
.button_secondary, .button_secondary input, .button_secondary a {
    height: 26px;
    background: url('../images/uniform/button-secondary-26px.png') top right no-repeat;
}
.button, .button input, .button a {
    display: block;
    height: 31px;
    cursor: pointer;
    overflow: hidden;
    background: url('../images/uniform/button-primary-31px.png') top right no-repeat;
}
.button_secondary {
    background-position: -494px -148px;
}
.button, .by .compareProducts .button.addRegButton {
    position: relative;
    float: left;
    display: inline;
    padding-left: 6px;
    background-position: -494px -173px;
}

.button_secondary, .button_secondary input, .button_secondary a {
    height: 26px;
    background-image: url('../images/uniform/button-secondary-26px.png');
    background-attachment: initial;
    background-origin: initial;
    background-clip: initial;
    background-color: initial;
    background-position: 100% 0%;
    background-repeat: no-repeat no-repeat;
}
.button, .button input, .button a {
    display: block;
    height: 31px;
    cursor: pointer;
    overflow-x: hidden;
    overflow-y: hidden;
    background-image: url('../images/uniform/button-primary-31px.png');
    background-attachment: initial;
    background-origin: initial;
    background-clip: initial;
    background-color: initial;
    background-position: 100% 0%;
    background-repeat: no-repeat no-repeat;
    outline-width: initial !important;
    outline-color: initial !important;
}
							</code></pre>

						</div>
					</div>
				</div><%--end component --%>


				<hr />
			<div class="marTop_10 marBottom_10">
					<div class="grid_4 alpha ">
						<img src="images/btnSearch.png" />
					</div>
					<div class="grid_4 ">
						Wedding and Gift Registry main menu hover
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
							<pre><code  class="language-html">
<c:set var="html">

 <div class="button button_secondary button_active">                        
  <input id="btnFindRegistry" name="btnFindRegistry" value="Search" >
    <input name="_D:btnFindRegistry" value=" " type="hidden">
 </div>

</c:set>
${fn:escapeXml(html)}
							</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">
.findARegistryForm .button_secondary,.findARegistryForm .button_secondary input,#frmRegInfo .button_secondary,#frmRegInfo .button_secondary input {
    background-image: url('../../bbregistry/images/uniform/button-secondary-26px.png');
    color: #43165E;
}
							</code></pre>

						</div>
					</div>
				</div><%--end component--%>


				<hr />
			<div class="marTop_10 marBottom_10">
					<div class="grid_4 alpha ">
						<img src="images/btnBookAppointment.png" />
						<img src="images/btnBookAppointment2.png" />
					</div>
					<div class="grid_4 ">
						Shop For College hover menu
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
							<pre><code  class="language-html">
<c:set var="html">
<div class="button button_secondary">
<input id="scheduleAppointmentBtn" type="button" role="button" value="Book an Appointment" name="btnFindInStore">				
</div>

</c:set>
${fn:escapeXml(html)}
							</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">
.by #shopForCollegelFlyout .button_secondary, 
.by #shopForCollegelFlyout .button_secondary input, 
.by #shopForCollegelFlyout .button_secondary a {
    color: #4e8800;
    background-image: url("/_assets/bbcollege/images/uniform/button-secondary-26px.png?jcb=1422877164");
}
							</code></pre>

						</div>
					</div>
				</div><%--end component--%>



				<hr />
			<div class="marTop_10 marBottom_10">
					<div class="grid_4 alpha ">
						<img src="images/btnSubmit.png" />
					</div>
					<div class="grid_4 ">
						<a href="http://bedbathandbeyonddev.com:7003/store/selfservice/ContactUs" target="_blank">http://bedbathandbeyonddev.com:7003/store/selfservice/ContactUs</a>
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
							<pre><code  class="language-html">
<c:set var="html">
<div class="button button_prodLight ">
 <input type="submit" value="Submit" id="Submit" 
   onclick="javascript:omnitureExternalLinks('Email Sign Up: submit a Email Sign Up Special Offer button');" 
   name="submit" role="button" aria-pressed="false" aria-labelledby="Submit">
</div>
</c:set>
${fn:escapeXml(html)}
							</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">
#footer .box .form .button {
    margin-bottom: 10px;
    margin-top: 5px;
}

							</code></pre>

						</div>
					</div>
				</div><%--end component--%>


			<hr />
			<div class="marTop_10 marBottom_10">
				<div class="grid_4 alpha ">
					<img src="images/btnConnectWithUs.png" />
				</div>
				<div class="grid_4 ">
					<a href="http://bedbathandbeyond.ed4.net/prefs/pref.cfm?&wrap&email=rajan.patel%40bedbath.com
" target="_blank">http://bedbathandbeyond.ed4.net/prefs/pref.cfm?&wrap&email=rajan.patel%40bedbath.com
</a>
				</div>
				<div class="grid_4  omega">
					<div class="toggle"><a href='#'>+ HTML</a></div>
					<div class="toggleChild hidden">
						<pre><code  class="language-html">
<c:set var="html">
<div align="left">
<input type="image" name="submit" src="images/btnTanSubmit.gif" alt="SUBMIT" title="SUBMIT" border="0">
</div>
</c:set>
${fn:escapeXml(html)}
						</code></pre>

					</div>
					<div class="toggle"><a href='#'>+ CSS</a></div>
					<div class="toggleChild hidden">
						<pre><code class="language-css">
button is an image - images/btnTanSubmit.gif
						</code></pre>

					</div>
				</div>
			</div><%--component end--%>



				<hr />
			<div class="marTop_10 marBottom_10">
				<div class="grid_4 alpha ">
					<img src="images/btnShoppingCartPage.png" />
					<img src="images/btnShoppingCartPage2.png" />
				</div>
				<div class="grid_4 ">
					<a href="http://invitations.bedbathandbeyond.com/shopping-cart/" target="_blank">http://invitations.bedbathandbeyond.com/shopping-cart/</a>
				</div>
				<div class="grid_4  omega">
					<div class="toggle"><a href='#'>+ HTML</a></div>
					<div class="toggleChild hidden">
						<pre><code  class="language-html">
<c:set var="html">
<div class="cartButtons">
<a href="http://invitations.bedbathandbeyond.com/" class="checkoutBtn">Continue Shopping</a>
<a href="/checkout/" class="checkoutBtn HighlightBtn">Checkout</a>
</div>
</c:set>
${fn:escapeXml(html)}
						</code></pre>

					</div>
					<div class="toggle"><a href='#'>+ CSS</a></div>
					<div class="toggleChild hidden">
						<pre><code class="language-css">
/* css code goes here
						</code></pre>

					</div>
				</div>
			</div><%--end component--%>

			<hr />
			<div class="marTop_10 marBottom_10">
					<div class="grid_4 alpha ">
						<img src="images/btnAddToCart.png" />
						<img src="images/btnAddToCart2.png" />
					</div>
					<div class="grid_4 ">
						<a href="http://bedbathandbeyonddev.com:7003/store/product/mizone-nia-comforter-set-in-teal/217661?categoryId=10620" target="_blank">http://bedbathandbeyonddev.com:7003/store/product/mizone-nia-comforter-set-in-teal/217661?categoryId=10620</a>
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
							<pre><code  class="language-html">
<c:set var="html">
<div class="button button_active button_active_orange  ">
    <div id="of" class="visuallyhidden">
     <span class="qtty">1</span> of</div>
     <div id="custAddCart" class="visuallyhidden">added to cart</div>   
<input type="submit" aria-describedby="of productTitle custAddCart" name="btnAddToCart" value="Add to Cart">	
</div>
</c:set>
${fn:escapeXml(html)}
							</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">
.by .button.button_active_orange {
    background-color: #ff6a23;
    color: #fff;
    border-bottom: 2px solid #c1471a;
}
.addToCart .button {
    width: 200px;
    background: none;
    height: auto;
}
							</code></pre>

						</div>
					</div>
				</div><%--end component--%>


				<hr />
			<div class="marTop_10 marBottom_10">
					<div class="grid_4 alpha ">
						<img src="images/btnAddToRegistry.png" />
					</div>
					<div class="grid_4 ">
						<a href="http://bedbathandbeyonddev.com:7003/store/product/mizone-nia-comforter-set-in-teal/217661?categoryId=10620" target="_blank">http://bedbathandbeyonddev.com:7003/store/product/mizone-nia-comforter-set-in-teal/217661?categoryId=10620</a>
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
							<pre><code  class="language-html">
<c:set var="html">
<div class="selector_primaryAlt" id="uniform-addItemToRegMultipleRegID"><span aria-hidden="true">Add To Registry</span><select id="addItemToRegMultipleRegID" name="registryId" aria-hidden="false" class="addItemToRegis addItemToRegMultipleReg selector_primaryAlt" aria-required="false" style="opacity: 0;">
	<option value="">Add To Registry</option>
	<option value="203528078" class="Baby" data-poboxflag="false" data-notify-reg="true">
		Baby 03/31/2016
	</option>	
	<option value="203528572" class="Housewarming" data-poboxflag="false" data-notify-reg="true">
		Housewarming 06/25/2016
	</option>	
</c:set>
${fn:escapeXml(html)}
							</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">
#selectBoxRedesign .addToRegistry.addToRegistrySel div.selector_primaryAlt {
    background: #e8e8e8;
}
#selectBoxRedesign .addToRegistry div.selector_primaryAlt {
    height: 42px;
    text-decoration: none;
    text-transform: uppercase;
    position: relative;
    float: left;
    outline: 0;
    cursor: pointer;
    overflow: hidden;
    display: block;
    background: #eee;
    padding: 0!important;
    margin: 0!important;
}
#selectBoxRedesign .addToRegistrySel .selector_primaryAlt {
    width: 236px!important;
}
.by .productDetails .addToRegistrySel .selector_primaryAlt {
    width: 125px;
}
.productDetails .addToRegistrySel .selector_primaryAlt {
    width: 130px;
    padding: 0 0 0 3px;
    margin-right: 2px;
}
							</code></pre>

						</div>
					</div>
				</div><%--end component--%>


					<hr />
			<div class="marTop_10 marBottom_10">
					<div class="grid_4 alpha ">
						<img src="images/btnCart.png" />
					</div>
					<div class="grid_4 ">
						Top Nav, Mini cart
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
							<pre><code  class="language-html">
<c:set var="html">
<a rel="nofollow" href="/store/cart/cart.jsp" 
    onclick="s_objectID=&quot;http://www.bedbathandbeyond.com/store/cart/cart.jsp_2&quot;;return this.s_oc?this.s_oc(e):true">
Cart
</a>
</c:set>
${fn:escapeXml(html)}
							</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">
#headerWrapper #shoppingCartCheckout a {
    color: rgb(255, 255, 255);
    font-size: 11px;
    font-family: Arial;
    font-weight: bold;
    margin-top: 0px;
    margin-right: 12px;
    margin-bottom: 0px;
    margin-left: 12px;
    padding-top: 2px;
    padding-right: 2px;
    padding-bottom: 2px;
    padding-left: 2px;
    display: block;
    text-decoration: none;
    text-transform: uppercase;
    position: relative;
    border-top-left-radius: 2px 2px;
    border-top-right-radius: 2px 2px;
    border-bottom-right-radius: 2px 2px;
    border-bottom-left-radius: 2px 2px;
    background-color: rgb(255, 106, 35);
}

							</code></pre>

						</div>
					</div>
				</div><%--end component--%>



			<hr />
			<div class="marTop_10 marBottom_10">
					<div class="grid_4 alpha ">
						<img src="images/btnSelectType.png" />
					</div>
					<div class="grid_4 ">
						<a href="http://bedbathandbeyonddev.com:7003/store/account/myaccount.jsp" target="_blank">http://bedbathandbeyonddev.com:7003/store/account/myaccount.jsp</a>
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
							<pre><code  class="language-html">
<c:set var="html">
<div class="selector_primary" id="uniform-typeofregselect">
<span aria-hidden="true">Select Type</span>
<select id="typeofregselect" name="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler.registryEventType" aria-hidden="false" class="selector_primary" aria-required="false" onchange="callMyAccRegistryTypesFormHandler();" aria-label="select the registry you would like to create. You will be redirected to the begin the creation process" style="opacity: 0;">		
<option value="BRD">
</select></div>
</c:set>
${fn:escapeXml(html)}
							</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">

element.style {
    opacity: 0;
}

div.selector_primary select, div.selector_primaryAlt select {
    height: 31px;
}
div.selector select, div.selector_boxArrow select, div.selector_arrowOnly select, div.selector_primary select, div.selector_primaryAlt select, div.selector_secondary select, div.selector_secondaryAlt select {
    position: absolute;
    border: 0;
    background: 0;
    padding-left: 2px;
}
div.selector_primary select, div.selector_primaryAlt select, div.selector_secondary select, div.selector_secondaryAlt select {
    width: 100%;
    font-family: Arial,Helvetica,sans-serif;
    font-size: 12px;
}
div.selector select, 
div.selector_primary select, 
div.selector_primaryAlt select, 
div.selector_secondary select, 
div.selector_secondaryAlt select, 
div.selector_boxArrow select, 
div.selector_arrowOnly select {
    top: 0;
    left: 0;
}
.by select {
    color: #594d43;
    
}
.selector select, 
.selector_boxArrow select,
.selector_arrowOnly select,
.selector_primary select, 
.selector_primaryAlt select, 
.selector_secondary select, 
.selector_secondaryAlt select, 
.checker input, 
.radio input {
    cursor: pointer;
}
.selector, 
.selector_boxArrow, 
.selector_arrowOnly, 
.selector_primary, 
.selector_primaryAlt, 
.selector_secondary, 
.selector_secondaryAlt, 
.checker, .radio {
    display: inline-block;
    vertical-align: middle;
    zoom: 1;
}
							</code></pre>

						</div>
					</div>
				</div><%--end component--%>


			<hr />
			<div class="marTop_10 marBottom_10">
					<div class="grid_4 alpha ">
						<img src="images/btnFAQ-liveHelp.png" />
					</div>
					<div class="grid_4 ">
						<a href="http://bedbathandbeyond.custhelp.com/" target="_blank">http://bedbathandbeyond.custhelp.com/</a>
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
							<pre><code  class="language-html">
<c:set var="html">
<a class="rn_SelectedTab" href="/app/answers/list" target="_self">
        <span>Frequently Asked Questions</span>
    </a>
</c:set>
${fn:escapeXml(html)}
							</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">
.rn_NavigationTab2 a {
    cursor: pointer;
    /* float: left; */
    font-size: 1em;
    font-weight: bold;
    height: 20px;
    _height: 18px;
    margin-right: 4px;
    padding: 5px 20px;
    position: relative;
}

a:link, a:visited, a:active {
    color: #0000ff;
    text-decoration: none;
}
.rn_NavigationTab2 a {
    color: #FFFFFF !important;
}

							</code></pre>

						</div>
					</div>
				</div><%--end component--%>



				<hr />
			<div class="marTop_10 marBottom_10">
					<div class="grid_4 alpha ">
						<img src="images/btnSaveForLater.png" />
					</div>
					<div class="grid_4 ">
						<a href="#" target="_blank">www.bedbathandbeyond.com/store/whatever</a>
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
							<pre><code  class="language-html">
<c:set var="html">
<input type="button" name="btnAddToList" id="btnAddToList" value="Save for Later" role="button" onclick="rkg_micropixel('BedBathUS','wish')">
</c:set>
${fn:escapeXml(html)}
							</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">
.btnPD .button input, .addToRegistry .button input, .collectionCart .button input {
    background: none;
    font-family: 'FuturaStdMedium';
    font-size: 13px !important;
    margin-left: 0;
}
.btnPD .button input, .btnPD .button a, .btnPD .button a:visited {
    padding-left: 4px;
}
.btnPD .button input, .btnPD .addToRegistrySel option {
    font-size: 14px;
    font-weight: normal!important;
}

							</code></pre>

						</div>
					</div>
				</div><%--end component--%>



				<hr />
			<div class="marTop_10 marBottom_10">
					<div class="grid_4 alpha ">
						<img src="images/btnExpandCollapse.png" />
					</div>
					<div class="grid_4 ">
						<a href="http://bedbathandbeyonddev.com:7003/store/giftregistry/view_registry_owner.jsp?registryId=203528572&eventType=Housewarming&_requestid=10877#t=myItems" target="_blank">See buttons under My Items</a>
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
							<pre><code  class="language-html">
<c:set var="html">
<div class="grid_3 omega expandCollapse"><ul><li class="expandAll"><a href="#" class="button-Small btnSecondary">Expand All</a></li><li class="collapseAll"><a href="#" class="button-Small btnSecondary">Collapse All</a></li></ul></div>
</c:set>
${fn:escapeXml(html)}
							</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">
.br .giftView .expandCollapse {
    float: right;
    width: auto;
}
.br .ownerView .expandCollapse {
    padding-bottom: 10px;
}
.br .ownerView .expandCollapse ul li {
    display: inline-block;
    margin-left: 6px;
}
.br .ui-widget-content a {
    color: #43165e;
    text-decoration: none;
}
							</code></pre>

						</div>
					</div>
				</div><%--end component--%>



				<hr />
			<div class="marTop_10 marBottom_10">
					<div class="grid_4 alpha ">
						<img src="images/btnAddItems.png" />
						<img src="images/btnAddItems2.png" />
					</div>
					<div class="grid_4 ">
						<a href="http://bedbathandbeyonddev.com:7003/store/giftregistry/view_registry_owner.jsp?registryId=203528572&eventType=Housewarming&_requestid=10877#t=myItems" target="_blank">See buttons under My Items</a>
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
							<pre><code  class="language-html">
<c:set var="html">
<a href="/store/category/dining/10003/" title="FINE DINING/GIFTWARE" class="fr addNewItem">Add more items</a>
</c:set>
${fn:escapeXml(html)}
							</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">
.br .ownerView .accordion .ui-state-default a.addNewItem, .br .ownerView .accordion .ui-state-active a.addNewItem {
    background: 0;
    background-color: #fff;
    border-bottom: #bbb 2px solid!important;
    font-family: 'FuturaStdMedium';
    font-size: 13px;
    text-align: center;
    text-decoration: none;
    text-transform: uppercase;
    color: #49176d;
}
.ui-accordion .ui-accordion-header a.addNewItem {
    padding: 5px 20px;
}
.ui-accordion .ui-accordion-header a.addNewItem {
    display: block;
    font-size: 1em;
    padding: .5em .5em .5em .7em;
}
							</code></pre>

						</div>
					</div>
				</div><%--end component--%>



				<hr />
			<div class="marTop_10 marBottom_10">
					<div class="grid_4 alpha ">
						<img src="images/btnDecreaseQty.png" />
					</div>
					<div class="grid_4 ">
						<a href="http://bedbathandbeyonddev.com:7003/store/product/marchesa-by-lenox-reg-empire-pearl-indigo-5-piece-place-setting/1040123646?skuId=40123646" target="_blank">http://bedbathandbeyonddev.com:7003/store/product/marchesa-by-lenox-reg-empire-pearl-indigo-5-piece-place-setting/1040123646?skuId=40123646</a>
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
							<pre><code  class="language-html">
<c:set var="html">
<a href="#" class="scrollDown down" id="prodDetailDescQty" title="Decrease Quantity" onclick="s_objectID=&quot;http://www.bedbathandbeyond.com/store/product/cz-by-kenneth-jay-lane-plated-cubic-zirconia-and-fr_9&quot;;return this.s_oc?this.s_oc(e):true"><span class="txtOffScreen" aria-hidden="true">
Decrease Quantity</span></a>
</c:set>
${fn:escapeXml(html)}
							</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">
#themeWrapper .spinner a.scrollUp, #themeWrapper .spinner a.scrollDown {
    width: 40px;
    height: 0;
    background: none;
    background-color: #eee;
    border-bottom: 2px solid #cbcbcb;
    speak: none;
    font-style: normal;
    font-weight: normal;
    font-variant: normal;
    -webkit-font-smoothing: antialiased;
    font-size: 14px;
    text-indent: 15px !important;
    color: transparent;
    padding: 15px 0 25px 0;
}

							</code></pre>

						</div>
					</div>
				</div><%--end component--%>


				<hr />
			<div class="marTop_10 marBottom_10">
					<div class="grid_4 alpha ">
						<img src="images/btnDecreaseQtyProduct.png" />
					</div>
					<div class="grid_4 ">
						<a href="http://bedbathandbeyonddev.com:7003/store/product/staybowlizer-in-red/1042901228?Keyword=accessories
" target="_blank">Product Page</a>
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
							<pre><code  class="language-html">
<c:set var="html">
<div class="spinner fr">
      <a href="#" class="scrollDown down" id="prodDetailDescQty" title="Decrease Quantity"   onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/product/staybowlizer-in-red/1042901228?Keyword=accessor_9&quot;;return this.s_oc?this.s_oc(e):true"><span class="txtOffScreen" aria-hidden="true">Decrease Quantity</span></a> <span class="incDec visuallyhidden" aria-live="assertive" aria-atomic="true"></span> <input id="quantity" title="Enter Quantity" class="fl addItemToRegis _qty itemQuantity addItemToList escapeHTMLTag" type="text" name="qty" role="textbox" value="1" maxlength="2" data-change-store-submit="qty" data-change-store-errors="required digits nonZero" aria-required="true" aria-describedby="quantity">

<a href="#" class="scrollUp up" id="prodDetailIncQty" title="Increase Quantity" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/product/staybowlizer-in-red/1042901228?Keyword=accessor_10&quot;;return this.s_oc?this.s_oc(e):true"><span class="txtOffScreen" aria-hidden="true">Increase Quantity</span></a>
	
<input type="hidden" name="registryId" class="sflRegistryId  addItemToRegis addItemToList" value="" data-change-store-submit="registryId"> <input type="hidden" name="prodId" class="_prodId addItemToRegis productId addItemToList" value="1042901228" data-change-store-submit="prodId" data-change-store-errors="required" data-change-store-internaldata="true"> <input type="hidden" name="skuId" value="42901228" class="addItemToRegis _skuId addItemToList changeStoreSkuId" data-change-store-submit="skuId" data-change-store-errors="required" data-change-store-internaldata="true"> <input type="hidden" name="price" value="$24.99" class="addItemToList addItemToRegis"> <input type="hidden" name="priceNoCur" value="24.99" class="addItemToList addItemToRegis"> <input type="hidden" class="addToCartSubmitData" name="storeId" value="" data-change-store-storeid="storeId"> <input type="hidden" class="addToCartSubmitData" name="bts" value="false" data-change-store-storeid="bts">
</div>

</c:set>
${fn:escapeXml(html)}
							</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">
#themeWrapper .spinner {
width: 93px;
}
#themeWrapper .spinner a.scrollDown{
background: url("../images/icons/common_icons_sprite.png?jcb=1458198609") no-repeat scroll 0 -108px;
border: 0;
height: 32px;
width: 30px;
cursor: pointer;
text-indent: -9999px;
outline: 0;
}


							</code></pre>

						</div>
					</div>
				</div><%--end component--%>



				<hr />
			<div class="marTop_10 marBottom_10">
					<div class="grid_4 alpha ">
						<img src="images/btnReturns.png" />
					</div>
					<div class="grid_4 ">
						<a href="http://bedbathandbeyonddev.com:7003/store/static/EasyReturns" target="_blank">http://bedbathandbeyonddev.com:7003/store/static/EasyReturns</a>
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
							<pre><code  class="language-html">
<c:set var="html">
<div class="acceptDelivery active">General Returns
<div class="textLeft subNav" style="display: none;">
<ul>
<li><a href="" class="navreturnsReceipt" onclick="s_objectID=&quot;http://www.bedbathandbeyond.com/store/static/EasyReturns_1&quot;;return this.s_oc?this.s_oc(e):true">Returns with a Receipt</a></li>
<li><a href="" class="navreturnsNoReceipt" onclick="s_objectID=&quot;http://www.bedbathandbeyond.com/store/static/EasyReturns_2&quot;;return this.s_oc?this.s_oc(e):true">Returns without a Receipt</a></li>
<li><a href="" class="navreturnsProcessing" onclick="s_objectID=&quot;http://www.bedbathandbeyond.com/store/static/EasyReturns_3&quot;;return this.s_oc?this.s_oc(e):true">Returns to Our Processing Center</a></li>
</ul>
</div>
</div>
</c:set>
${fn:escapeXml(html)}
							</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">
.acceptDelivery, .returnSchedule, .mattressReturns, .paypal {
    border-radius: 5px;
    color: #555!important;
    border: #aaa 1px solid;
    background-color: #fff;
    cursor: pointer;
    cursor: hand;
    padding: 10px!important;
    text-align: center;
    font-size: 15px!important;
}

							</code></pre>

						</div>
					</div>
				</div><%--end component--%>


				
					<hr />
			<div class="marTop_10 marBottom_10">
					<div class="grid_4 alpha ">
						<img src="images/btnLookupStore.png" />
						<img src="images/btnLookupStore2.png" />
					</div>
					<div class="grid_4 ">
						<a href="http://bedbathandbeyonddev.com:7003/store/account/myaccount.jsp" target="_blank">http://bedbathandbeyonddev.com:7003/store/account/myaccount.jsp</a>
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
							<pre><code  class="language-html">
<c:set var="html">
<div class="button button_secondary">
<input id="go" type="button" aria-pressed="false" role="button" value="LOOK UP STORES" name="sheduleAppBtnGo">
</div>
</c:set>
${fn:escapeXml(html)}
							</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">
css code goes here
							</code></pre>

						</div>
					</div>
				</div><%--end component--%>



				<hr />
				<div class="marTop_10 marBottom_10">
					<div class="grid_4 alpha ">
						<img src="images/btnPrintChecklist.png" />
					</div>
					<div class="grid_4 ">
						<a href="http://bedbathandbeyonddev.com:7003/store/registry/RegistryChecklist" target="_blank">http://bedbathandbeyonddev.com:7003/store/registry/RegistryChecklist</a>
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
							<pre><code  class="language-html">
<c:set var="html">
<div class="button button_active marRight_5 fl">
 <a href="javascript:void(0);" title="Print Checklist" onclick="s_objectID=&quot;javascript:void(0);_3&quot;;return this.s_oc?this.s_oc(e):true">Print Checklist</a>
</div>
</c:set>
${fn:escapeXml(html)}
							</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">
.br .button, .br .button input, .br .button a {
    background-image: url('../images/uniform/button-primary-31px.png?jcb=1458198608');
    color: #43165e;
}
.br .button, .br .button a, .br .button a:visited {
    font-family: "FuturaStdHeavy",Arial,Helvetica,sans-serif;
    font-size: 14px;
    line-height: 20px;
}
.fl {
    float: left!important;
}
.marRight_5 {
    margin-right: 5px!important;
}
.button_active, .button:hover {
    background-position: -494px -207px;
}
.button, .by .compareProducts .button.addRegButton {
    position: relative;
    float: left;
    display: inline;
    padding-left: 6px;
    background-position: -494px -173px;
}
.button, .button input, .button a {
    display: block;
    height: 31px;
    cursor: pointer;
    overflow: hidden;
    background: url('../images/uniform/button-primary-31px.png?jcb=1458198610') top right no-repeat;
}
							</code></pre>

						</div>
					</div>
				</div><%--end component--%>



				<hr />
			<div class="marTop_10 marBottom_10">
					<div class="grid_4 alpha ">
						<img src="images/btnViewAccessories.png" />
					</div>
					<div class="grid_4 ">
						<a href="http://www.bedbathandbeyond.com/store/product/vitamix-turboblend-4500-blender/202355?skuId=17007637" target="_blank">Product Page</a>
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
							<pre><code  class="language-html">
<c:set var="html">
<a class="lnkCollectionItems smoothScrollTo viewAccessories" data-smoothscroll-topoffset="65" href="#collectionItems" onclick="s_objectID=&quot;http://www.bedbathandbeyond.com/store/product/vitamix-turboblend-4500-blender/202355?skuId=170076_8&quot;;return this.s_oc?this.s_oc(e):true">View Accessories</a>
</c:set>
${fn:escapeXml(html)}
							</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">
.productDetails a.viewAccessories.lnkCollectionItems {
color: #fff;
background-color: #00a7f5;
padding: 4px 8px;
text-transform: uppercase;
text-align: center;
border-bottom: 1px solid #0081c4;
font-weight: normal;
}
.productDetails a.viewAccessories {
float: left;
display: block;
font-size: 12px;
height: 15px;
line-height: 18px;
}

							</code></pre>

						</div>
					</div>
				</div><%--end component--%>


				<hr />
			<div class="marTop_10 marBottom_10">
				<div class="grid_4 alpha ">
					<img src="images/btnWatchHowItWorks.png" />
				</div>
				<div class="grid_4 ">
					<a href="https://www.bedbathandbeyond.com/store/static/aboutmyoffers
" target="_blank">https://www.bedbathandbeyond.com/store/static/aboutmyoffers
</a>
				</div>
				<div class="grid_4  omega">
					<div class="toggle"><a href='#'>+ HTML</a></div>
					<div class="toggleChild hidden">
						<pre><code  class="language-html">
<c:set var="html">
<div id="MObtnGetStarted">
        <a href="/store/static/myoffersvideoiframe" class="btnPrimary button-Large newOrPopup" onclick="s_objectID=&quot;https://www.bedbathandbeyond.com/store/static/myoffersvideoiframe_1&quot;;return this.s_oc?this.s_oc(e):true"><p id="MOseeVideo">WATCH HOW IT WORKS</p></a>
        </div>
</c:set>
${fn:escapeXml(html)}
						</code></pre>

					</div>
					<div class="toggle"><a href='#'>+ CSS</a></div>
					<div class="toggleChild hidden">
						<pre><code class="language-css">
#MObtnGetStarted {
padding-top: 15px;
}
#MObtnGetStarted a {
padding: 5px 40px 0 30px;
}
.by .btnPrimary {
color: #FFF!important;
background-color: #00aeef;
border: 0;
border-bottom: #008cc0 2px solid;
}
.by .button-Large {
    height: 45px;
}

						</code></pre>

					</div>
				</div>
			</div><%--end component--%>



			<hr />
			<div class="marTop_10 marBottom_10">
				<div class="grid_4 alpha ">
					<img src="images/btnCompareProducts.png" />
				</div>
				<div class="grid_4 ">
					<a href="
http://bedbathandbeyonddev.com:7003/store/category/outdoor/patio-umbrellas-shade/patio-umbrellas-bases/12462/1-96?pagSortOpt=DEFAULT-0&view=grid4" target="_blank">Categories Page - compare product slider
</a>
				</div>
				<div class="grid_4  omega">
					<div class="toggle"><a href='#'>+ HTML</a></div>
					<div class="toggleChild hidden">
						<pre><code  class="language-html">
<c:set var="html">
<div class="button button_active"> <a href="/store/compare/product_comparison.jsp" class="compare-button" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/compare/product_comparison.jsp_1&quot;;return this.s_oc?this.s_oc(e):true" role="button" aria-disabled="false">Compare Products</a></div>
</c:set>
${fn:escapeXml(html)}
						</code></pre>

					</div>
					<div class="toggle"><a href='#'>+ CSS</a></div>
					<div class="toggleChild hidden">
						<pre><code class="language-css">
.by .compare-controls .compare-button, .compare-controls .compare-button:hover, .compare-alert .compare-button, .compare-alert .compare-button:hover {
    background: url("/_assets/global/images/uniform/button-pdpCollection-32px.png") no-repeat scroll right 0;
    height: 32px;
    font-size: 14px;
    font-weight: normal!important;
}

						</code></pre>

					</div>
				</div>
			</div><%--end component--%>



			<hr />
			<div class="marTop_10 marBottom_10">
				<div class="grid_4 alpha ">
					<img src="images/btnEmployment.png" />
				</div>
				<div class="grid_4 ">
					<a href="http://bedbathandbeyonddev.com:7003/store/static/Careers" target="_blank">http://bedbathandbeyonddev.com:7003/store/static/Careers</a>
				</div>
				<div class="grid_4  omega">
					<div class="toggle"><a href='#'>+ HTML</a></div>
					<div class="toggleChild hidden">
						<pre><code  class="language-html">
<c:set var="html">
<a href="/store/static/storeassociates" class="newOrPopup ux-button ux-button-primary" title="Find a Store" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/static/storeassociates_1&quot;;return this.s_oc?this.s_oc(e):true">INQUIRE WITHIN</a>
</c:set>
${fn:escapeXml(html)}
						</code></pre>

					</div>
					<div class="toggle"><a href='#'>+ CSS</a></div>
					<div class="toggleChild hidden">
						<pre><code class="language-css">
a.ux-button.ux-button-primary, a.ux-button.ux-button-primary[disabled]:hover, a.ux-button.ux-button-primary[disabled]:active {
    border-color: #4dabf3;
    color: #fff;
    letter-spacing: 1px;
    background-color: #7eccfa;
   
}
.by a, .by a:visited {
    font-family: Arial,Helvetica,sans-serif;
    font-size: 11px;
    text-decoration: none;
}
a.ux-button, a.ux-button[disabled]:hover, a.ux-button[disabled]:active {
    margin: 0;
    padding: 8px 9px;
    cursor: pointer;
    display: inline-block;
    border: 1px solid silver;
    border-radius: 3px;
    font: 400 14px/1 "FuturaStdHeavy", Arial, Helvetica, sans-serif;

}
						</code></pre>

					</div>
				</div>
			</div><%--end component--%>


				<hr />
			<h1 style="background-color:#eee;">Tabs</h1>
				<div class="spacing">
					<div class="grid_4 alpha ">
						<h3>Example</h3>
					</div>
					<div class="grid_4 ">
						<h3>Appears On</h3>
					</div>
					<div class="grid_4  omega">
						<h3>Code</h3>
					</div>
				</div>



			<div class="marTop_10 marBottom_10">
					<div class="grid_4 alpha ">
						<img src="images/btnProductInfo.png" />
					</div>
					<div class="grid_4 ">
						<a href="http://bedbathandbeyonddev.com:7003/store/product/mizone-nia-comforter-set-in-teal/217661?categoryId=10620" target="_blank">http://bedbathandbeyonddev.com:7003/store/product/mizone-nia-comforter-set-in-teal/217661?categoryId=10620</a>
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
							<pre><code  class="language-html">
<c:set var="html">
<ul class="categoryProductTabsLinks ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all" role="tablist">
	<li id="prodViewDefault-tab1" role="tab" class="ui-state-default ui-corner-top ui-tabs-selected ui-state-active">
<div class="arrowSouth"></div>
	<a title="Product Information" href="#prodExplore-tabs1" role="tab" aria-controls="prodExplore-tabs1" aria-selected="true" onclick="s_objectID=&quot;http://www.bedbathandbeyond.com/store/product/cz-by-kenneth-jay-lane-plated-cubic-zirconia-and-fr_14&quot;;return this.s_oc?this.s_oc(e):true">
		Product Information
  </a></li>	
<li id="prodViewDefault-tab2" role="tab" class="noBorderRight ui-state-default ui-corner-top"><div class="arrowSouth"></div>
     <a title="Ratings &amp; Reviews" href="#prodExplore-tabs2" role="tab" aria-controls="prodExplore-tabs2" aria-selected="false" onclick="s_objectID=&quot;http://www.bedbathandbeyond.com/store/product/cz-by-kenneth-jay-lane-plated-cubic-zirconia-and-fr_15&quot;;return this.s_oc?this.s_oc(e):true">Ratings &amp; Reviews</a>	
</li>
</c:set>
${fn:escapeXml(html)}
							</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">
ui-tabs .ui-tabs-nav {
background: 0;
}
.ui-tabs .ui-tabs-nav {
margin: 0;
padding: 0;
border: 0;
border-bottom: 3px solid #00aef0;
-moz-border-radius: 0;
-webkit-border-radius: 0;
-khtml-border-radius: 0;
border-radius: 0;
}

							</code></pre>

						</div>
					</div>
				</div><%--end component--%>



			<hr />
			<div class="marTop_10 marBottom_10">
					<div class="grid_4 alpha ">
						<img src="images/tabsProductPageBeta.png" />
					</div>
					<div class="grid_4 ">
						<a href="http://bedbathandbeyonddev.com:7003/store/product/oxo-good-grips-reg-15-piece-kitchen-tool-set/1013876150" target="_blank">Product Page Tabs</a>
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
							<pre><code  class="language-html">
<c:set var="html">
<ul class="categoryProductTabsLinks ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all" role="tablist">
<li id="prodViewDefault-tab1" role="tab" class="ui-state-default ui-corner-top ui-tabs-selected ui-state-active"><div class="arrowSouth"></div><a title="Product Information" href="#prodExplore-tabs1" role="tab" aria-controls="prodExplore-tabs1" aria-selected="true" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/product/staybowlizer-in-red/1042901228?Keyword=accessor_15&quot;;return this.s_oc?this.s_oc(e):true">Product Information</a></li>	
<li id="prodViewDefault-tab2" role="tab" class="ui-state-default ui-corner-top"><div class="arrowSouth"></div>
<a title="Ratings &amp; Reviews" href="#prodExplore-tabs2" role="tab" aria-controls="prodExplore-tabs2" aria-selected="false" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/product/staybowlizer-in-red/1042901228?Keyword=accessor_16&quot;;return this.s_oc?this.s_oc(e):true">Ratings &amp; Reviews</a></li>	
<a title="Product Q&amp;A" class="aaBeta" href="#prodExplore-tabs3" role="tab" aria-controls="prodExplore-tabs3" aria-selected="false" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/product/staybowlizer-in-red/1042901228?Keyword=accessor_17&quot;;return this.s_oc?this.s_oc(e):true"><span>Product Q&amp;A</span><span class="aaBetaImg"></span><span class="txtOffScreen">beta</span><span class="clear"></span></a>
</li>
</ul>

</c:set>
${fn:escapeXml(html)}
							</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">
.ui-tabs .ui-tabs-nav li.ui-tabs-selected {
margin-bottom: 0;
}
.ui-tabs .ui-tabs-nav li {
list-style: none;
float: left;
position: relative;
top: 1px;
margin: 0 2px 1px 0;
border-bottom: 0!important;
padding: 0;
white-space: nowrap;
}
.ui-tabs .ui-tabs-nav {
margin: 0;
padding: 0;
border: 0;
border-bottom: 3px solid #00aef0;
border-radius: 0;
}

							</code></pre>

						</div>
					</div>
				</div><%--end component--%>



				<hr />
			<div class="marTop_10 marBottom_10">
				<div class="grid_4 alpha ">
					<img src="images/tabsAccessories.png" />
				</div>
				<div class="grid_4 ">
					<a href="http://bedbathandbeyonddev.com:7003/store/s/accessories/4294965830/1-24?&view=grid&_dyncharset=UTF-8&media=true&pagFilterOpt=49&partialFlag=&searchMode=
" target="_blank">accessories</a>
				</div>
				<div class="grid_4  omega">
					<div class="toggle"><a href='#'>+ HTML</a></div>
					<div class="toggleChild hidden">
						<pre><code  class="language-html">
<c:set var="html">
<div class="searchGroupResults" id="pagGroupResult">
<div class="button_pill">
<a data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="accessories" data-submit-param2-name="swsterms" data-submit-param2-value=""  class="redirPage dynFormSubmit" href="/store/s/accessories/1-24?&amp;view=grid&amp;_dyncharset=UTF-8&amp;partialFlag=&amp;searchMode=" onclick="javascript:searchTabTrack('Products','26072')">
Products 
	<div>
<span class="prodCount search_tab">26072</span>
<span class="prodCount cornerCount_prod_test">&nbsp;</span>
</div>
</a><div class="button_pill button_pill_active">
<a href="#"  class="redirPage">Videos 
<div><span class="prodCount search_tab">49</span>
<span class="prodCount cornerCount_prod_test"></span></div>
</a>
</div>			
<div class="button_pill">
<a data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="accessories" data-submit-param2-name="swsterms" data-submit-param2-value=""  class="redirPage dynFormSubmit" href="/store/s/accessories/4294965832/1-24?&amp;view=grid&amp;_dyncharset=UTF-8&amp;partialFlag=&amp;searchMode=" onclick="javascript:searchTabTrack('Guides &amp; Advice','24')">Guides &amp; Advice 
</a>
</div>			
</div>

</c:set>
${fn:escapeXml(html)}
						</code></pre>

					</div>
					<div class="toggle"><a href='#'>+ CSS</a></div>
					<div class="toggleChild hidden">
						<pre><code class="language-css">
.subCategory .pagination .pagGroupings {
border-bottom: 3px solid #00aeef;
background-color: #fff;
border-top: 0;
}
#pagGroupResult div.button_pill,#pagGroupResult div.button_pill:hover {
color: #333;
padding: 0 6px 0 20px;
text-align: center;
width: auto;
}
#pagGroupResult div.button_pill_active, #pagGroupResult div.button_pill_active:hover {
border-color: #00aeef;
background: url("/_assets/global/images/jquery-ui/ui-bg_highlight-soft_50_00aef0_1x100.png") repeat-x scroll 50% 50% #00aef0;
color: #273691;
}

						</code></pre>

					</div>
				</div>
			</div><%--end component--%>



			<hr />
			<div class="marTop_10 marBottom_10">
				<div class="grid_4 alpha ">
					<img src="images/tabsOwnerRegistryMain.png" />
				</div>
				<div class="grid_4 ">
					<a href="http://bedbathandbeyonddev.com:7003/store/giftregistry/view_registry_owner.jsp?registryId=203527804&eventType=Anniversary&_requestid=10535#t=myItems" target="_blank">Owner registry page - main tabs</a>
				</div>
				<div class="grid_4  omega">
					<div class="toggle"><a href='#'>+ HTML</a></div>
					<div class="toggleChild hidden">
						<pre><code  class="language-html">
<c:set var="html">
     <div class="registryTabs alpha omega grid_12 clearfix"><%--main container div--%>
<div id="categoryTabs">
        <input type="hidden" id="reloadMyItemsTab" value="false"/>
<ul class="noprint" role="tablist">
<c:set var="browseGiftsLbl" >
<bbbl:label key="lbl_registry_browse_add_gifts_tab" language="${pageContext.request.locale.language}" />
</c:set>
<li id="browseGiftsTab" aria-labelledby="kickStrtMsg" role="tab" aria-selected="false" aria-controls="browseGifts"><a href="#browseGifts" class="akickStartersTab" id="browseGiftsLink" onclick="javascript:omniRegistryTabs('Browse & Add Gifts: Kickstarters');">${browseGiftsLbl}</a></li>
<span class="visuallyhidden" id="kickStrtMsg">${browseGiftsLbl}</span>	
<li id="myItemsTab" aria-labelledby="myItemsMsg" role="tab" aria-selected="true" aria-controls="myItems"><a href="#myItems" class="amyItemsTab" id="myItemsLink" onclick="javascript:omniRegistryTabs('My Items');"><bbbl:label key="lbl_registry_owner_myitems" language="${pageContext.request.locale.language}" /></a></li>
<span class="visuallyhidden" id="myItemsMsg">My Items Tab</span>
<c:if test="${inviteFriend eq 'false' && (!regPublic || (regPublic && showRecommendationTab eq 'true'))}">
<li>
<a href="#recommendations" id="recommendationTab" onclick="javascript:omniRegistryTabs('Recommendations');">
<bbbl:label key="lbl_registry_owner_recommendations" language="${pageContext.request.locale.language}" />
<c:if test="${recommendationCount gt 0}">
</c:if>
</a>
</li>
</c:if>
<c:set var="lbl_registry_owner_checklist_tab" >
<bbbl:label key="lbl_registry_owner_checklist_tab" language="${pageContext.request.locale.language}" />
</c:set>
<li id="checklistTab" aria-labelledby="checklistMsg" role="tab" aria-selected="true" aria-controls="myItems"><a href="#checklist" class="amyItemsTab" id="checklistLink" onclick="javascript:omniRegistryTabs('Browse & Add Gifts: Checklist');">${lbl_registry_owner_checklist_tab}</a></li>
<span class="visuallyhidden" id="checklistMsg">${lbl_registry_owner_checklist_tab}</span>
</ul>
</div>
</c:set>
${fn:escapeXml(html)}
						</code></pre>

					</div>
					<div class="toggle"><a href='#'>+ CSS</a></div>
					<div class="toggleChild hidden">
						<pre><code class="language-css">
#categoryTabs .br .ui-tabs .ui-tabs-nav {
    border: none;
    border-bottom: 2px solid #DDD;
    background-color: #F2F2F2;
    background-image: none;
}
#categoryTabs .ui-tabs .ui-tabs-nav li a {
    font-size: 15px;
}
#categoryTabs .ui-corner-all {
    border:none;
    border-bottom: 1px solid #797777;
}      
#categoryTabs .ui-widget-header{
    background: none;
}
#categoryTabs .ui-state-default, .ui-widget-content .ui-state-default, .ui-widget-header .ui-state-default {
    border: none;
    background: none;
} 
#categoryTabs .ui-state-default.ui-corner-top.ui-tabs-selected.ui-state-active{
    background-color: #efefef;
}
#categoryTabs .ui-state-default.ui-corner-top.ui-tabs-selected.ui-state-active a{
    color: #49176D;
}
#categoryTabs .ui-tabs-selected.ui-state-active:before {
    width: 0;
    height: 0;
    border-left: 9px solid transparent;
    border-right: 9px solid transparent;
    border-bottom: 10px solid #797777;
    position: absolute;
    top: 38px;
    right: 47%;
    content: "";
}
#categoryTabs .ui-tabs-selected.ui-state-active:after{
    width: 0;
    height: 0;
    border-left: 9px solid transparent;
    border-right: 9px solid transparent;
    border-bottom: 10px solid white;
    position: absolute;
    top: 39px;
    right: 47%;
    content: "";
}
#categoryTabs .ui-widget-header a{
    color: #666666;
 }
						</code></pre>

					</div>
				</div>
			</div><%--end component--%>



			<hr />
			<div class="marTop_10 marBottom_10">
				<div class="grid_4 alpha ">
					<img src="images/tabsOwnerRegistrySub.png" />
				</div>
				<div class="grid_4 ">
					<a href="http://bedbathandbeyonddev.com:7003/store/giftregistry/view_registry_owner.jsp?registryId=203527804&eventType=Anniversary&_requestid=10535#t=myItems" target="_blank">Owner registry page - Sub tabs</a>
				</div>
				<div class="grid_4  omega">
					<div class="toggle"><a href='#'>+ HTML</a></div>
					<div class="toggleChild hidden">
						<pre><code  class="language-html">
<c:set var="html">
<div id="browseGifts" role="tabpanel" aria-labelledby="browseGiftsTab">
<div id="browseAddGiftsTabs">
<ul class="noprint" role="tablist">
<c:set var="kickstartersLbl" >
<bbbl:label key="lbl_registry_owner_kickstarters" language="${pageContext.request.locale.language}" />
</c:set>
<li id="kickStartersTab" aria-labelledby="kickStrtMsg" role="tab" aria-selected="false" aria-controls="kickstarters">
<a href="#kickstarters" class="akickStartersTab" id="kickStartersLink" onclick="javascript:omniRegistryTabs('Browse & Add Gifts: Kickstarters');">${kickstartersLbl}</a>
</li>
<span class="visuallyhidden" id="kickStrtMsg">${kickstartersLbl}</span>	
<c:set var="lbl_shopthelook" >
<bbbl:label key="lbl_shopthelook" language="${pageContext.request.locale.language}" />
</c:set>
<li id="shopThisLookTab" aria-labelledby="myItemsMsg" role="tab" aria-selected="true" aria-controls="shopThisLook">
<a href="#shopThisLook" class="ashopThisLookTab" id="shopThisLookLink" onclick="javascript:omniRegistryTabs('Browse & Add Gifts: shop this look');">${lbl_shopthelook}</a>
</li>
<span class="visuallyhidden" id="myItemsMsg">${lbl_shopthelook}</span>
<li id="registryFavoritesTab" aria-labelledby="registryFavoritesMsg" role="tab" aria-selected="true" aria-controls="registryFavorites">
</li>
<c:set var="lblGiftIdeas" >
<bbbl:label key="lbl_registry_owner_gift_ideas" language="${pageContext.request.locale.language}" />
</c:set>
<li id="giftIdeasTab" aria-labelledby="giftIdeasMsg" role="tab" aria-selected="true" aria-controls="giftIdeas">
<a href="#giftIdeas" data-registryid="${registryId}" data-eventTypeCode="${eventTypeCode}" 
	data-eventtype="${eventTypeVar}"
	data-sortseq="${sortSeq}"
	data-view="${view}"
class="agiftIdeasTab"  id="giftIdeasLink" onclick="javascript:omniRegistryTabs(' Browse & Add Gifts: gift ideas');">${lblGiftIdeas}</a>
</li>
<span class="visuallyhidden" id="checklistMsg">${lblGiftIdeas}</span>
</ul>
</c:set>
${fn:escapeXml(html)}
						</code></pre>

					</div>
					<div class="toggle"><a href='#'>+ CSS</a></div>
					<div class="toggleChild hidden">
						<pre><code class="language-css">
#browseGifts .ui-widget-content ul.ui-widget-header, 
#browseGifts .ui-widget-content ul.ui-tabs-nav li{
    background: none;
    border: none;
}
#browseGifts .ui-widget-content ul.ui-tabs-nav .ui-state-active a, 
#browseGifts .ui-widget-content ul.ui-tabs-nav .ui-state-active li {
    border-bottom: 2px solid rgba(194,15,121,1);
}
#browseGifts .ui-widget-content ul.ui-tabs-nav{
    border:none;
    border-bottom: 1px solid #797777;  
}

#browseGifts .ui-widget-content ul.ui-tabs-nav .ui-state-active a, 
#browseGifts .ui-widget-content ul.ui-tabs-nav .ui-state-active a:link{
    color: #49176d; 
}
#browseGifts ul.ui-tabs-nav .ui-state-hover a,
#browseGifts .ui-widget-content ul.ui-tabs-nav a{
    color: #666666;
}
						</code></pre>

					</div>
				</div>
			</div><%--end component--%>



				<hr />
				<h1 style="background-color:#eee;">Paging</h1>
				<div class="spacing">
					<div class="grid_4 alpha ">
						<h3>Example</h3>
					</div>
					<div class="grid_4 ">
						<h3>Appears On</h3>
					</div>
					<div class="grid_4  omega">
						<h3>Code</h3>
					</div>
				</div>



			<div class="marTop_10 marBottom_10">
					<div class="grid_4 alpha ">
						<img src="images/btnPagGroupings.png" />
					</div>
					<div class="grid_4 ">
						<a href="http://bedbathandbeyonddev.com:7003/store/category/Formal-China/12123/?icid=registrylanding_loggedinPromoarea-registrylanding" target="_blank">Product page</a>
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
							<pre><code  class="language-html">
<c:set var="html">
<div class="pagGroupings clearfix">
<span id="lblpagViewMode" class="txtOffScreen">VALUE NOT FOUND FOR KEY lbl_page_mode</span>
<div id="pagViewMode" class="searchViewMode clearfix" aria-labelledby="lblpagViewMode">
<span class="incDec visuallyhidden" aria-live="assertive" aria-atomic="true"></span>
<a data-submit-param-length="1" data-submit-param1-name="swsterms" data-submit-param1-value="">Grid View</a>
</div>

<ul>
<li class="listPageNumbers"></li>
</ul>
</div>
</c:set>
${fn:escapeXml(html)}
							</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">
#prodGridContainer .pagGroupings, 
.searchGrid .subCategory 
.pagination .pagGroupings, 
.searchList .subCategory .pagination .pagGroupings {
    border-bottom: 3px solid #00aeef;
    background-color: #FFFFFF;
    border-top: 0px;
}

pagination li.listPageNumbers {
    padding-left: 18px;
}
.pagination ul li {
    color: #000000;
    font-size: 12px;
    padding: 0;
    float: left;
    margin: 0;
}
							</code></pre>

						</div>
					</div>
				</div><%--end component--%>



				<hr />
			<div class="marTop_10 marBottom_10">
				<div class="grid_4 alpha ">
					<img src="images/btnPaginateAcessories.png" />
				</div>
				<div class="grid_4 ">
					<a href="http://bedbathandbeyonddev.com:7003/store/s/accessories/4294965830/1-24?&view=grid&_dyncharset=UTF-8&media=true&pagFilterOpt=49&partialFlag=&searchMode=
" target="_blank">Accessories</a>
				</div>
				<div class="grid_4  omega">
					<div class="toggle"><a href='#'>+ HTML</a></div>
					<div class="toggleChild hidden">
						<pre><code  class="language-html">
<c:set var="html">
<div id="headerliks" class="lcheader">
 <span style="width: 105px; height: 25px; top: 8px;left: 640px;position: absolute;font-family: Arial,Helvetica,sans-serif;font-size: 12px;font-weight: bold; ">View options:</span>
 <span id="lcgrid" style="width: 15px; height: 15px; top: 2px;left: 723px;position: absolute; padding: 4px;cursor:pointer; "><img style="opacity:1; position: absolute; top: 0; left: 0px;" src="images/icon-grid-view-on.png"></span>
  <span id="lclist" style="width: 15px; height: 15px; top: 2px;left: 753px;position: absolute; background-color:#00AEF0;padding: 4px;cursor:pointer; "><img style="opacity:0.6;position: absolute; top: 0; left: -4px;" src="images/icon-list-view-on.png"></span>
</div>

</c:set>
${fn:escapeXml(html)}
						</code></pre>

					</div>
					<div class="toggle"><a href='#'>+ CSS</a></div>
					<div class="toggleChild hidden">
						<pre><code class="language-css">
.lcheader {
padding: 0px 0px 0px 0px;
background-color: #F5F5F5;
border: 1px solid #DDDDDD;
width: 783px;
height: 35px;
position: absolute;
font-family: Arial,Helvetica,sans-serif;
}

.paging {
padding: 0px 0px 0px 430px;
text-align: center;
font-size: 11px;
}

						</code></pre>

					</div>
				</div>
			</div><%--end component--%>



				<hr />
			<div class="marTop_10 marBottom_10">
					<div class="grid_4 alpha ">
						<img src="images/btnGlocery.png" />
					</div>
					<div class="grid_4 ">
						<a href="http://bedbathandbeyonddev.com:7003/store/static/GlossaryPage" target="_blank">http://bedbathandbeyonddev.com:7003/store/static/GlossaryPage</a>
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
							<pre><code  class="language-html">
<c:set var="html">
<a href="#startA" title="A" class="first smoothScrollTo" data-smoothscroll-topoffset="70" onclick="s_objectID=&quot;http://www.bedbathandbeyond.com/store/static/GlossaryPage#startA_1&quot;;return this.s_oc?this.s_oc(e):true">A</a>
</c:set>
${fn:escapeXml(html)}
							</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">
.by .GlossaryPage #alphabetMain a {
    margin: 0 0 20px;
    float: left;
    font-weight: bold;
    font-size: 16px;
    border-right: 2px solid #273691;
    padding: 0 10px;
    color: #273691;
    text-transform: uppercase;
}

							</code></pre>

						</div>
					</div>
				</div><%--end component--%>



				<hr />
			<div class="marTop_10 marBottom_10">
					<div class="grid_4 alpha ">
						<img src="images/btnPaginate.png" />
					</div>
					<div class="grid_4 ">
						<a href="http://bedbathandbeyonddev.com:7003/store/registry/RegistryChecklist" target="_blank">RegistryChecklist</a>
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
							<pre><code  class="language-html">
<c:set var="html">
<div id="rn_Paginator_17" class="rn_Paginator ">
    <a href="http://bedbathandbeyond.custhelp.com/app/answers/list/page/0/session/L3RpbWUvMTQ1ODMyNTc0Ni9zaWQvLURCWjlPTG0%3D" id="rn_Paginator_17_Back" class="rn_Hidden">
            &lt; Previous        </a>
    <span id="rn_Paginator_17_Pages" class="rn_PageLinks">
     <span class="rn_CurrentPage">1</span>
</c:set>
${fn:escapeXml(html)}
							</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">
.rn_Paginator {
    margin-top: 10px;
}
.rn_Paginator {
    clear: left;
    padding: 10px 0;
    font-size: 140%;
}

							</code></pre>

						</div>
					</div>
				</div><%--end component--%>




				<div class="grid_12 ">
					<h1 style="background-color:#eee;">Forms</h1>
					<div class="spacing">
						<div class="grid_4 alpha ">
							<h3>Example</h3>
						</div>
						<div class="grid_4 ">
							<h3>Appears On</h3>
						</div>
						<div class="grid_4  omega">
							<h3>Code</h3>
						</div>
					</div>
				</div>



				<hr />
				<div class="marTop_10">
					<div class="grid_4 alpha ">
						<img src="images/formInput.png" />
						<img src="images/formInputError.png" />
					</div>
					<div class="grid_4 ">
						Login Form, checkout form, my account forms
						<a href="https://www.bedbathandbeyond.com/store/account/Login" target="_blank">https://www.bedbathandbeyond.com/store/account/Login</a>
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
							<pre><code  class="language-html">
<c:set var="html">
<div class="formRow clearfix">
	<div class="input grid_3 alpha noMarBot" aria-live="assertive">
		<div class="label">
			<label id="lblNewEmail" class="textLgray12 block" for="newEmail">Email<span class="visuallyhidden">For New Customers</span></label>
		</div>	
		<div class="text noMarBot" aria-live="assertive">
			<input id="newEmail" aria-describedby="lblNewEmail errornewEmail" name="email" value="" class="input_large211 block fbConnectEmail escapeHTMLTag" type="text"><input name="_D:email" value=" " type="hidden">
		</div>	
	</div>	
</div>
</c:set>
${fn:escapeXml(html)}
							</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">
/* _assets/global/css/account.css */
.myAccount .formRow {
    margin: 6px 0 20px;
}

.radioInput .label label, .input .label label, .input label.label {
    font-family: Arial;
    font-size: 12px;
    line-height: 20px;
}
.by label {
    font-family: Arial;
    color: #666666;
    font-size: 12px;
}
.textLgray12 {
    color: #666;
    font-size: 12px;
}
.block {
    display: block !important;
}
label {
    cursor: default;
}
.myAccount .input .text, .myAccount .input .select {
    margin: 3px 0 5px;
}
.by .input .text, .by .input .select, .by .input .selector, .by .input .textarea {
    margin: 2px 0 5px;
}
.input .text, .input .select, div.selector {
    margin: 0 0 5px;
}
.noMarBot {
    margin-bottom: 0 !important;
}
.by .login #content .text input {
    width: 273px;
    height: 18px;
}

.by .input .text input {
    height: 20px;
    color: #666666;
    font-family: Arial;
    font-size: 12px;
    width: 100%;
    border: 2px solid #cccccc;
}
.by input[type="email"], .by input[type="tel"], .by input[type="number"], .by input[type="text"], .by input[type="password"] {
    border: none;
    height: 20px;
    color: #594D43;
    font-family: Arial;
    font-size: 14px;
    border: 2px solid #CFCFCF;
}
input[type="email"], input[type="tel"], input[type="number"], input[type="text"], input[type="password"] {
    padding: 1px 2px;
}
input:valid, textarea:valid {
}
input[type=email], input[type=number], input[type=tel], input[type=password], input[type=text], textarea, select {
    font-size: 14px;
}
.block {
    display: block !important;
}
button, input {
    line-height: normal;
}
button, input, select, textarea {
    font-size: 100%;
    margin: 0;
    vertical-align: baseline;
}
body, button, input, select, textarea, legend, label {
    font-family: Arial, Helvetica, sans-serif;
    font-size: 12px;
    color: #666;
    -webkit-appearance: none;
    -webkit-border-radius: 0;
}
							</code></pre>

						</div>
					</div>
				</div><%--end component--%>



			<hr />
			<div class="marTop_10">
					<div class="grid_4 alpha ">
						<img src="images/uploadACoupon.png" />
						<img src="images/uploadACouponError.png" />
					</div>
					<div class="grid_4 ">
						Account - Upload a Coupon
						<a href="https://www.bedbathandbeyond.com/store/account/coupons.jsp" target="_blank">
						https://www.bedbathandbeyond.com/store/account/coupons.jsp</a>
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
							<pre><code  class="language-html">
<c:set var="html">
<input id="couponCode" aria-labelledby="lblemail erroremail" placeholder="Enter Code" name="couponCode" value="" tabindex="3" class="fl couponCode escapeHTMLTag" type="text" aria-required="true">
</c:set>
${fn:escapeXml(html)}
							</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">
.formWrap.open .inner-formWrap #couponCode {
    /* display: block; */
    /* width: 170px; */
}
.formWrap.open .inner-formWrap h3, .formWrap.open .inner-formWrap p, .formWrap.open .inner-formWrap #couponCode, .formWrap.open .inner-formWrap #addCoupon, .formWrap.open .inner-formWrap #addCouponForm {
    display: block;
}
#themeWrapper .flatform input[type="text"], #themeWrapper .flatform input[type="email"], #themeWrapper .flatform input[type="password"], #themeWrapper .flatform input[type="date"], #themeWrapper .flatform input[type="tel"], #themeWrapper .flatform select {
    border: 1px solid #CFCFCF;
    font-size: 14px;
    height: 32px;
    padding: 7px 5px;
    margin-right: 5px !important;
    box-sizing: border-box;
}
.formWrap .inner-formWrap h3, .formWrap .inner-formWrap p, .formWrap .inner-formWrap #couponCode, .formWrap .inner-formWrap #addCoupon, .formWrap .inner-formWrap #addCouponForm {
    display: none;
    overflow: hidden;
    -webkit-transition: width 0.1s ease-in-out, height 0.1s ease-in-out;
    -moz-transition: width 0.1s ease-in-out, height 0.1s ease-in-out;
    -ms-transition: width 0.1s ease-in-out, height 0.1s ease-in-out;
    transition: width 0.1s ease-in-out, height 0.1s ease-in-out;
}
.by .flatform input[type="text"], .by .flatform input[type="email"], .by .flatform input[type="password"], .by .flatform input[type="date"], .by .flatform input[type="tel"], .by .flatform select {
    border: 1px solid #CFCFCF;
}
.by input[type="email"], .by input[type="tel"], .by input[type="number"], .by input[type="text"], .by input[type="password"] {
    border: none;
    height: 20px;
    color: #594D43;
    font-family: Arial;
    font-size: 14px;
    border: 2px solid #CFCFCF;
}
.by input[type="email"], .by input[type="tel"], .by input[type="number"], .by input[type="text"], .by input[type="password"] {
    border-style: initial;
    border-color: initial;
    height: 20px;
    color: rgb(89, 77, 67);
    font-family: Arial;
    font-size: 14px;
    border-top-width: 2px;
    border-right-width: 2px;
    border-bottom-width: 2px;
    border-left-width: 2px;
    border-top-style: solid;
    border-right-style: solid;
    border-bottom-style: solid;
    border-left-style: solid;
    border-top-color: rgb(207, 207, 207);
    border-right-color: rgb(207, 207, 207);
    border-bottom-color: rgb(207, 207, 207);
    border-left-color: rgb(207, 207, 207);
}
input[type="email"], input[type="tel"], input[type="number"], input[type="text"], input[type="password"] {
    padding: 1px 2px;
}
input[placeholder] {
    text-overflow: ellipsis;
}
input:valid, textarea:valid {
}
input[type=email], input[type=number], input[type=tel], input[type=password], input[type=text], textarea, select {
    font-size: 14px;
}
input[type="email"], input[type="tel"], input[type="number"], input[type="text"], input[type="password"] {
    padding-top: 1px;
    padding-right: 2px;
    padding-bottom: 1px;
    padding-left: 2px;
}
input[placeholder] {
    text-overflow: ellipsis;
}
input[type="email"], input[type="number"], input[type="tel"], input[type="password"], input[type="text"], textarea, select {
    font-size: 14px;
}
							</code></pre>

						</div>
					</div>
				</div><%--end component--%>



			<hr />
			<div class="marTop_10">
					<div class="grid_4 alpha ">
						<img src="images/flatFormInput.png" />
						<img src="images/flatFormInputError.png" />
					</div>
					<div class="grid_4 ">
						Single page checkout, Coupon wallet registration
						<a href="https://www.bedbathandbeyond.com/store/checkout/checkout_single.jsp" target="_blank">
						https://www.bedbathandbeyond.com/store/checkout/checkout_single.jsp</a>
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
		<pre><code  class="language-html">
<c:set var="html">
<div class="input_wrap">
    <label class="popUpLbl _focus _unfocus lblError" id="lblemail" for="shippingEmail">
		Email 
		<span class="required">*</span>
	</label>   
	 <input id="shippingEmail" aria-labelledby="lblemail erroremail errorshippingEmail" name="shippingEmail" value="ayanbabi.pal@bedbath.com" type="text" aria-required="true" class="escapeHTMLTag error">
	 <label for="shippingEmail" generated="true" class="error errorLabel" id="errorshippingEmail">Please enter a valid email address.</label>
 </div>
</c:set>
${fn:escapeXml(html)}
	</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">

							</code></pre>

						</div>
					</div>
				</div><%--end component--%>



			<hr />
			<div class="marTop_10 marBottom_10">
				<div class="grid_4 alpha ">
					<img src="images/btnCreateWeddingRegistry.png" />
					<img src="images/btnCreateWeddingRegistry1.png" />
				</div>
				<div class="grid_4 ">
					<a href="http://bedbathandbeyonddev.com:7003/store/giftregistry/simpleReg_creation_form.jsp?_requestid=11096#" target="_blank">Create Wedding Registry From</a>
				</div>
				<div class="grid_4  omega">
					<div class="toggle"><a href='#'>+ HTML</a></div>
					<div class="toggleChild hidden">
						<pre><code  class="language-html">
<c:set var="html">
<div class="grid_2 alpha omega" aria-live="assertive">
<input id="txtRegistryExpectedDate" aria-labelledby="lbltxtRegistryExpectedDate errortxtRegistryExpectedDate" placeholder="mm/dd/yyyy" name="simpletxtRegDate" value="" tabindex="1" class="step1FocusField escapeHTMLTag hasDatepicker valid" type="text" aria-required="true"><input name="_D:simpletxtRegDate" value=" " type="hidden">
<div id="registryExpectedDateButton" class="calendericon">
<span class="icon-fallback-text"><span class="icon-calendar" aria-hidden="true"></span><span class="icon-text"></span></span>
</div>
</div>
</c:set>
${fn:escapeXml(html)}
						</code></pre>

					</div>
					<div class="toggle"><a href='#'>+ CSS</a></div>
					<div class="toggleChild hidden">
						<pre><code class="language-css">
/* css code goes here#themeWrapper .createSimpleRegistry .flatform #txtRegistryExpectedDate, #themeWrapper .createSimpleRegistry .flatform #txtRegistryShowerDate, #themeWrapper .createSimpleRegistry .flatform #txtRegistryShowerDateCA, #themeWrapper .createSimpleRegistry .flatform #txtRegistryExpectedDateCA {
    padding-right: 30px;
}
#themeWrapper .createSimpleRegistry .flatform input[type="text"], #themeWrapper .createSimpleRegistry .flatform input[type="password"] {
    color: #666666;
}
#themeWrapper .createSimpleRegistry .flatform input[type="text"], #themeWrapper .createSimpleRegistry .flatform input[type="email"], #themeWrapper .createSimpleRegistry .flatform input[type="password"], #themeWrapper .createSimpleRegistry .flatform input[type="date"], #themeWrapper .createSimpleRegistry .flatform input[type="tel"], #themeWrapper .createSimpleRegistry .flatform select {
    background: #fcfcfc;
}
#themeWrapper .createSimpleRegistry .flatform input[type="password"], #themeWrapper .createSimpleRegistry .flatform input[type="text"] {
    width: 100%;
}
#themeWrapper .flatform input[type="text"], #themeWrapper .flatform input[type="email"], #themeWrapper .flatform input[type="password"], #themeWrapper .flatform input[type="date"], #themeWrapper .flatform input[type="tel"], #themeWrapper .flatform select {
    border: 1px solid #cfcfcf;
    font-size: 14px;
    height: 32px;
    padding: 7px 5px;
    margin-right: 5px!important;
    box-sizing: border-box;
}						</code></pre>

					</div>
				</div>
			</div><%--component end--%>


<hr />
			<div class="marTop_10 marBottom_10">
				<div class="grid_4 alpha ">
					<img src="images/btnCreateWeddingRegistryDate.png" />
				</div>
				<div class="grid_4 ">
					<a href="http://bedbathandbeyonddev.com:7003/store/giftregistry/simpleReg_creation_form.jsp?_requestid=11096#" target="_blank">Create Wedding Registry From</a>
				</div>
				<div class="grid_4  omega">
					<div class="toggle"><a href='#'>+ HTML</a></div>
					<div class="toggleChild hidden">
						<pre><code  class="language-html">
<c:set var="html">
<div class="grid_2 alpha omega" aria-live="assertive">
<input id="txtRegistryExpectedDate" aria-labelledby="lbltxtRegistryExpectedDate errortxtRegistryExpectedDate" placeholder="mm/dd/yyyy" name="simpletxtRegDate" value="" tabindex="1" class="step1FocusField escapeHTMLTag hasDatepicker valid" type="text" aria-required="true"><input name="_D:simpletxtRegDate" value=" " type="hidden">
<div id="registryExpectedDateButton" class="calendericon">
<span class="icon-fallback-text"><span class="icon-calendar" aria-hidden="true"></span><span class="icon-text"></span></span>
</div>
</div>
</c:set>
${fn:escapeXml(html)}
						</code></pre>

					</div>
					<div class="toggle"><a href='#'>+ CSS</a></div>
					<div class="toggleChild hidden">
						<pre><code class="language-css">

.br .inputField input, .br .inputField select, .br .inputField textarea, .br .selector {
    width: 100%;
    border: 2px solid #c28ded;
}
button, input {
    line-height: normal;
}
button, input, select, textarea {
    font-size: 100%;
    margin: 0;
    vertical-align: baseline;
}
body, button, input, select, textarea, legend, label {
    font-family: Arial,Helvetica,sans-serif;
    font-size: 12px;
    color: #666;
    -webkit-appearance: none;
    -webkit-border-radius: 0;
}

					</code></pre>

					</div>
				</div>
			</div><%--component end--%>

				<hr />
			<div class="marTop_10 marBottom_10">
				<div class="grid_4 alpha ">
					<img src="images/formSearchCriteria.png" />
				</div>
				<div class="grid_4 ">
					<a href="http://bedbathandbeyonddev.com:7003/store/category/bedding/bedding-basics/mattress-pads-toppers/13115/" target="_blank">Category Page Left Sidebar</a>
				</div>
				<div class="grid_4  omega">
					<div class="toggle"><a href='#'>+ HTML</a></div>
					<div class="toggleChild hidden">
						<pre><code  class="language-html">
<c:set var="html">
<div class="facetContent">	
<div aria-hidden="false" class="facetScroller">
<ul class="facetList">	
<li class="facetListItem clearfix"><div class="checker" id="uniform-COLORGROUP_0"><span><input type="checkbox" id="COLORGROUP_0" onclick="omnitureRefineCall('COLORGROUP', 'Metallic');" value="/store/category/personalized-gifts/personalized-wedding-gifts/14019/14019-4294966331-4294967203/1-48?pagSortOpt=DEFAULT-0&amp;view=grid&amp;refType=true" name="brandSearch" title="Metallic" class="checkbox facetedCheckBox" aria-checked="false" aria-describedby="COLORGROUP_0" style="opacity: 0;"></span></div><label for="COLORGROUP_0">	
Metallic
&nbsp;<span class="facetCount">(119)</span></label>	</li>		
<li class="facetListItem clearfix">	
<div class="checker" id="uniform-COLORGROUP_1"><span><input type="checkbox" id="COLORGROUP_1" onclick="omnitureRefineCall('COLORGROUP', 'Black');" value="/store/category/personalized-gifts/personalized-wedding-gifts/14019/14019-4294966331-4294949617/1-48?pagSortOpt=DEFAULT-0&amp;view=grid&amp;refType=true" name="brandSearch" title="Black" class="checkbox facetedCheckBox" aria-checked="false" aria-describedby="COLORGROUP_1" style="opacity: 0;"></span></div><label for="COLORGROUP_1">
Black
&nbsp;<span class="facetCount">(10)</span></label></li>
</c:set>
${fn:escapeXml(html)}
						</code></pre>

					</div>
					<div class="toggle"><a href='#'>+ CSS</a></div>
					<div class="toggleChild hidden">
						<pre><code class="language-css">
/* css code goes here
						</code></pre>

					</div>
				</div>
			</div><%--end component--%>

			<hr />
			<div class="marTop_10 marBottom_10">
				<div class="grid_4 alpha ">
					<img src="images/formStationaryLeftSidebar.png" />
				</div>
				<div class="grid_4 ">
					<a href="http://invitations.bedbathandbeyond.com/shop/baby/man-shower-invitations.html" target="_blank">Stationary - left sidebar</a>
				</div>
				<div class="grid_4  omega">
					<div class="toggle"><a href='#'>+ HTML</a></div>
					<div class="toggleChild hidden">
						<pre><code  class="language-html">
<c:set var="html">
<div class="refinementType ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom ui-accordion-content-active ui-accordion-header-active" id="Your_Style" aria-labelledby="ui-accordion-refinementArea-header-1" role="tabpanel" aria-expanded="false" aria-hidden="true" style="display: block;">
<ul>
<li><input type="checkbox" name="modern_ys" id="modern_ys" value="modern_ys" class="refinementSelector">&nbsp;<span class="swatch-modern_ys"></span>&nbsp;<span class="refinementname">Modern</span><span id="modern_ys_count" class="prodCount"> (1)</span></li>
</c:set>
${fn:escapeXml(html)}
						</code></pre>

					</div>
					<div class="toggle"><a href='#'>+ CSS</a></div>
					<div class="toggleChild hidden">
						<pre><code class="language-css">
.searchLeft h3 {
    border-bottom: 2px dotted #D6D5D5;
    color: #000000;
    font-size: 12px;
    line-height: 18px;
    margin-bottom: 10px;
    margin-top: 10px;
    padding-bottom: 9px;
    text-transform: uppercase;
    cursor: pointer;
}
						</code></pre>

					</div>
				</div>
			</div><%--end component--%>



			<hr />
			<div class="marTop_10 marBottom_10">
				<div class="grid_4 alpha ">
					<img src="images/formUpdateQty.png" />
				</div>
				<div class="grid_4 ">
					<a href="http://bedbathandbeyonddev.com:7003/store/cart/cart.jsp" target="_blank">http://bedbathandbeyonddev.com:7003/store/cart/cart.jsp</a>
				</div>
				<div class="grid_4  omega">
					<div class="toggle"><a href='#'>+ HTML</a></div>
					<div class="toggleChild hidden">
						<pre><code  class="language-html">
<c:set var="html">
<li class="quantityBox">
<label id="lblquantity_text_1" class="hidden" for="quantity_text_1">Quantity</label>
<div class="spinner" style="height: 32px;">
<span class="incDec visuallyhidden" aria-live="assertive" aria-atomic="true"></span>
<input name="DC2ci1737000017" type="text" id="quantity_text_1" value="1" title="1  of Marchesa by Lenox® Empire Pearl Indigo 5-Piece Place Setting" class="itemQuantity fl escapeHTMLTag" role="textbox" maxlength="2" aria-required="true" aria-label="1  of Marchesa by Lenox® Empire Pearl Indigo 5-Piece Place Setting">
</div>
</li>
</c:set>
${fn:escapeXml(html)}
						</code></pre>

					</div>
					<div class="toggle"><a href='#'>+ CSS</a></div>
					<div class="toggleChild hidden">
						<pre><code class="language-css">
#cartBody #cartContent .changeStoreItemWrap .quantityBox, #cartBody #cartContent .changeStoreItemWrap .lnkUpdate, #saveForLaterBody #saveForLaterContent .changeStoreItemWrap .quantityBox, #saveForLaterBody #saveForLaterContent .changeStoreItemWrap .lnkUpdate {
    float: left;
}
.cartDetail #content .prodDeliveryInfo li.quantityBox {
    margin-bottom: 10px;
}

						</code></pre>

					</div>
				</div>
			</div><%--end component--%>

			<hr />
			<div class="marTop_10 marBottom_10">
				<div class="grid_4 alpha ">
					<img src="images/formNarrowSearch.png" />
				</div>
				<div class="grid_4 ">
					<a href="http://bedbathandbeyonddev.com:7003/store/category/bedding/bedding-basics/mattress-pads-toppers/13115/" target="_blank">Category Page Left Sidebar</a>
				</div>
				<div class="grid_4  omega">
					<div class="toggle"><a href='#'>+ HTML</a></div>
					<div class="toggleChild hidden">
						<pre><code  class="language-html">
<c:set var="html">
<fieldset class="facetGroup firstGroup narrowSearch" style="display:block">
<div class="facetTitle padTop_10">
<h5 class="noMar noPad"><span>NARROW YOUR SEARCH</span></h5>
</div>
<div class="facetContent">
<form method="post" action="#" id="narrowForm">
<div class="narrowSearch">
<input id="additionalKeyword" class="removeDisabled escapeHTMLTag" type="text" value="" title="narrowDown" name="additionalKeywordsearch" placeholder="Search Within">
<input type="submit" class="removeDisabled hidden" value="submit" title="narrow">
<a role="button" title="narrow" aria-label="submit" class="removeDisabled facetSearchLink" href="javascript:void(0);" onclick="s_objectID=&quot;javascript:void(0);_4&quot;;return this.s_oc?this.s_oc(e):true"></a>
<input type="hidden" name="additionalKeyword" value="">
</c:set>
${fn:escapeXml(html)}
						</code></pre>

					</div>
					<div class="toggle"><a href='#'>+ CSS</a></div>
					<div class="toggleChild hidden">
						<pre><code class="language-css">
.firstGroup {
    border-top: 0;
}
.facetGroup, .searchContent {
    padding: 5px 0 5px 0;
    margin: 0;
}
fieldset {
    margin-top: 0;
}
p, dl, hr, h1, h2, h3, h4, h5, h6, ol, ul, pre, table, address, fieldset, figure {
    margin-bottom: 20px;
    margin-top: 20px;
}
fieldset {
    border: 0;
    margin: 0;
    padding: 0;
}
user agent stylesheet
fieldset {
    display: block;
    -webkit-margin-start: 2px;
    -webkit-margin-end: 2px;
    -webkit-padding-before: 0.35em;
    -webkit-padding-start: 0.75em;
    -webkit-padding-end: 0.75em;
    -webkit-padding-after: 0.625em;
    min-width: -webkit-min-content;
    border: 2px groove threedface;
}
						</code></pre>

					</div>
				</div>
			</div><%--end component--%>



				<div class="grid_12 ">
					<h1 style="background-color:#eee;">Links and Misc</h1>
					<div class="spacing">
						<div class="grid_4 alpha ">
							<h3>Example</h3>
						</div>
						<div class="grid_4 ">
							<h3>Appears On</h3>
						</div>
						<div class="grid_4  omega">
							<h3>Code</h3>
						</div>
					</div>
				</div>



				<hr />
			<div class="marTop_10 marBottom_10">
				<div class="grid_4 alpha ">
					<img src="images/linkChat1.png" />
				</div>
				<div class="grid_4 ">
					<a href="http://bedbathandbeyonddev.com:7003/store/giftregistry/simpleReg_creation_form.jsp?_requestid=9625
" target="_blank">Create Registry</a>
				</div>
				<div class="grid_4  omega">
					<div class="toggle"><a href='#'>+ HTML</a></div>
					<div class="toggleChild hidden">
						<pre><code  class="language-html">
<c:set var="html">
<div id="chatModal">
<div id="chatModalDialogs"></div>
<a title="Chat Now with a Representative" class="chatNow liveChat" data-chatdata="_icf_1/2" href="/store/selfservice/click_to_chat.jsp" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/selfservice/click_to_chat.jsp_1&quot;;return this.s_oc?this.s_oc(e):true"></a>
</c:set>
${fn:escapeXml(html)}
						</code></pre>

					</div>
					<div class="toggle"><a href='#'>+ CSS</a></div>
					<div class="toggleChild hidden">
						<pre><code class="language-css">
.createSimpleRegistry .chatNow {
width: 63px;
height: 15px;
background: url("../../bbregistry/images/chat_logo_registry_simple.png?jcb=1449146382") no-repeat scroll right 0 transparent;
float: right;
margin-bottom: 8px;
margin-right: -11px;
}
.chatNow {
display: block;
text-indent: -999px;
overflow: hidden;
cursor: pointer;
}

						</code></pre>

					</div>
				</div>
			</div><%--end component--%>


			<hr />
			<div class="marTop_10 marBottom_10">
				<div class="grid_4 alpha ">
					<img src="images/linkChat2.png" />
				</div>
				<div class="grid_4 ">
					<a href="http://bedbathandbeyonddev.com:7003/store/giftregistry/my_registries.jsp" target="_blank">My Registries</a>
				</div>
				<div class="grid_4  omega">
					<div class="toggle"><a href='#'>+ HTML</a></div>
					<div class="toggleChild hidden">
						<pre><code  class="language-html">
<c:set var="html">
<div id="chatModal">
<div id="chatModalDialogs"></div>
<a title="Chat Now with a Representative" class="chatNow liveChat" data-chatdata="_icf_1/2" href="/store/selfservice/click_to_chat.jsp" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/selfservice/click_to_chat.jsp_1&quot;;return this.s_oc?this.s_oc(e):true"></a>
</c:set>
${fn:escapeXml(html)}
						</code></pre>

					</div>
					<div class="toggle"><a href='#'>+ CSS</a></div>
					<div class="toggleChild hidden">
						<pre><code class="language-css">
.createSimpleRegistry .chatNow {
width: 63px;
height: 15px;
background: url("../../bbregistry/images/chat_logo_registry_simple.png?jcb=1449146382") no-repeat scroll right 0 transparent;
float: right;
margin-bottom: 8px;
margin-right: -11px;
}
.chatNow {
display: block;
text-indent: -999px;
overflow: hidden;
cursor: pointer;
}

						</code></pre>

					</div>
				</div>
			</div><%--end component--%>
			


		<hr />
			<div class="marTop_10 marBottom_10">
					<div class="grid_4 alpha ">
						<img src="images/btnPersonalizeNow.png" />
					</div>
					<div class="grid_4 ">
						<a href="#" target="_blank">www.bedbathandbeyond.com/store/whatever</a>
					</div>
					<div class="grid_4  omega">
						<div class="toggle"><a href='#'>+ HTML</a></div>
						<div class="toggleChild hidden">
							<pre><code  class="language-html">
<c:set var="html">
<a href="/store/category/personalized-shop/13807/" class="PerLink">
   <span class="Picon">p</span>
   <span class="PnowLink">Personalize now</span>
</a>
</c:set>
${fn:escapeXml(html)}
							</code></pre>

						</div>
						<div class="toggle"><a href='#'>+ CSS</a></div>
						<div class="toggleChild hidden">
							<pre><code class="language-css">
.personalizeContent .Picon {
    font-family: 'FuturaStd-Heavy';
    color: #fff;
    background: #43a3f4;
    font-size: 27px;
    padding: 5px 9px 1px;
}
.personalizeContent .PnowLink {
    position: relative;
    bottom: 4px;
    margin-left: 4px;
    font-size: 16px;
}

							</code></pre>

						</div>
					</div>
				</div><%--end component--%>


				<hr />
			<div class="marTop_10 marBottom_10">
				<div class="grid_4 alpha ">
					<img src="images/linkWriteReview.png" />
				</div>
				<div class="grid_4 ">
					<a href="http://bedbathandbeyonddev.com:7003/store/product/royal-doulton-reg-1815-16-piece-dinnerware-set-in-blue/1018544261?categoryId=12121
" target="_blank">Product Page</a>
				</div>
				<div class="grid_4  omega">
					<div class="toggle"><a href='#'>+ HTML</a></div>
					<div class="toggleChild hidden">
						<pre><code  class="language-html">
<c:set var="html">
<div class="BVRRRatingSummaryNoReviews"> <div id="BVRRRatingSummaryNoReviewsWriteImageLinkID" class="BVRRRatingSummaryLink BVRRRatingSummaryNoReviewsWriteImageLink">
<a data-bvjsref="" data-bvcfg="343261495" onclick="bvShowContentOnReturnPRR('2009-en_us', '1018544261', 'BVRRWidgetID');" name="BV_TrackingTag_Rating_Summary_1_WriteReview_1018544261" href="javascript://" title="Write a Review"> <img src="http://bedbathandbeyond.ugc.bazaarvoice.com/bvstaging/static/2009-en_us/translucent.gif" alt="Write a Review">
</a> </div>
 <div id="BVRRRatingSummaryLinkWriteFirstID" class="BVRRRatingSummaryLink BVRRRatingSummaryLinkWriteFirst">
 <span class="BVRRRatingSummaryLinkWriteFirstPrefix"></span>
<a data-bvjsref="" data-bvcfg="343261495" onclick="bvShowContentOnReturnPRR('2009-en_us', '1018544261', 'BVRRWidgetID');" name="BV_TrackingTag_Rating_Summary_1_WriteReview_1018544261" href="javascript://">Write the first review</a><span class="BVRRRatingSummaryLinkWriteFirstSuffix"></span> </div>
</div>
</c:set>
${fn:escapeXml(html)}
						</code></pre>

					</div>
					<div class="toggle"><a href='#'>+ CSS</a></div>
					<div class="toggleChild hidden">
						<pre><code class="language-css">
#BVRRSummaryContainer .BVRRRatingSummary .BVRRRatingSummaryNoReviews {
    margin-top: -13px;
}

#prodRatings .BVRRRatingSummaryNoReviews {
    margin-top: -5px;
}
.BVRRRatingSummaryNoReviews {
    padding: 5px 0;
    overflow: hidden;
    zoom: 1;
}
.BVRRRatingSummaryNoReviewsWriteImageLink {
    float: left;
    padding-right: 10px;
}

.BVRRRootElement a:visited {
    color: #273691;
    text-decoration: none;
    outline: 0;
}
						</code></pre>

					</div>
				</div>
			</div><%--end component--%>


			<hr />
			<div class="marTop_10 marBottom_10">
				<div class="grid_4 alpha ">
					<img src="images/linkCompareMenu.png" />
				</div>
				<div class="grid_4 ">
					<a href="http://bedbathandbeyonddev.com:7003/store/category/outdoor/patio-umbrellas-shade/patio-umbrellas-bases/12462/1-96?pagSortOpt=DEFAULT-0&view=list" target="_blank">Category Page - Compare Product Dialog</a>
				</div>
				<div class="grid_4  omega">
					<div class="toggle"><a href='#'>+ HTML</a></div>
					<div class="toggleChild hidden">
						<pre><code  class="language-html">
<c:set var="html">
<div id="compareDrawer" class="compare-box-wrapper" style="display: block; bottom: 0px;"><div class="compare-box width_12 padTop_10"><div class="compare-text-wrapper marRight_20"><h3 class="marBottom_5">Compare Products</h3><span>Select up to 4 products to compare, and see which one fits your needs the most.</span></div><div class="compare-items"><div class="compare-item" data-productid="3265178"><a href="javascript:void(0);" aria-label="Remove item" class="close-button" onclick="s_objectID=&quot;javascript:void(0);_5&quot;;return this.s_oc?this.s_oc(e):true"></a><img class="compare-image" src="//s7d1.scene7.com/is/image/BedBathandBeyond/25946740728964p?$400$" title="9-Foot Half Round Aluminum Umbrella" alt="9-Foot Half Round Aluminum Umbrella" tabindex="1" aria-labelledby="3265178"><div class="visuallyhidden" id="3265178">9-Foot Half Round Aluminum Umbrellaadded to compare list</div></div><div class="compare-item empty-item"><a href="javascript:void(0);" aria-label="Remove item" class="close-button" onclick="s_objectID=&quot;javascript:void(0);_6&quot;;return this.s_oc?this.s_oc(e):true"></a><img class="compare-image" src="/_assets/global/images/compare_add_item.png" title="Empty slot for product to compare" alt="Empty slot for product to compare"></div><div class="compare-item empty-item"><a href="javascript:void(0);" aria-label="Remove item" class="close-button" onclick="s_objectID=&quot;javascript:void(0);_7&quot;;return this.s_oc?this.s_oc(e):true"></a><img class="compare-image" src="/_assets/global/images/compare_add_item.png" title="Empty slot for product to compare" alt="Empty slot for product to compare"></div><div class="compare-item empty-item"><a href="javascript:void(0);" aria-label="Remove item" class="close-button" onclick="s_objectID=&quot;javascript:void(0);_8&quot;;return this.s_oc?this.s_oc(e):true"></a><img class="compare-image" src="/_assets/global/images/compare_add_item.png" title="Empty slot for product to compare" alt="Empty slot for product to compare"></div></div><div class="compare-controls"><div class="button button_active button_disabled"> <a href="/store/compare/product_comparison.jsp" class="compare-button" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/compare/product_comparison.jsp_1&quot;;return this.s_oc?this.s_oc(e):true" role="button" aria-disabled="true" aria-pressed="false">Compare Products</a></div><div class="clear"></div><div class="remove-btn fl cb marTop_10"><a href="#" id="remove-product" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/category/outdoor/patio-umbrellas-shade/patio-umbrellas-_37&quot;;return this.s_oc?this.s_oc(e):true"><span class="remove-icon marRight_5"></span><span>Remove All</span></a></div></div></div></div>
</c:set>
${fn:escapeXml(html)}
						</code></pre>

					</div>
					<div class="toggle"><a href='#'>+ CSS</a></div>
					<div class="toggleChild hidden">
						<pre><code class="language-css">
element.style {
    display: block;
    bottom: 0px;
}

.compare-box-wrapper {
    width: 100%;
    background-color: #e8e8e8;
    bottom: -100px;
    position: fixed;
    z-index: 1000;
    overflow: hidden;
    border-top: 1px solid #fff;
    box-shadow: 0 0 10px 0 #aaa;
    -webkit-box-shadow: 0 0 10px 0 #aaa;
}

.compare-box {
    position: relative;
    margin: 0 auto;
}
.padTop_10 {
    padding-top: 10px!important;
}
.container_12 .grid_12, .width_12 {
    width: 976px;
}

div {
    display: block;
}

.compare-text-wrapper {
    width: 245px;
    float: left;
}
.marRight_20 {
    margin-right: 20px!important;
}
.compare-items {
    margin: auto;
    float: left;
    width: 451px;
}

.compare-item {
    margin: 0 30px 10px 0;
    float: left;
}

						</code></pre>

					</div>
				</div>
			</div><%--end component--%>

			<hr />
			<div class="marTop_10 marBottom_10">
				<div class="grid_4 alpha ">
					<img src="images/linkQuickView.png" />
				</div>
				<div class="grid_4 ">
					<a href="http://bedbathandbeyonddev.com:7003/store/category/more/audio-electronics/alarm-clocks/12329/" target="_blank">Category page</a>
				</div>
				<div class="grid_4  omega">
					<div class="toggle"><a href='#'>+ HTML</a></div>
					<div class="toggleChild hidden">
						<pre><code  class="language-html">
<c:set var="html">
<span tabindex="0" role="button" aria-label="Quick View of the Braun® Flip Cover Alarm Clock" class="quickView showOptionMultiSku fl marRight_30 padRight_5">Quick View</span>
</c:set>
${fn:escapeXml(html)}
						</code></pre>

					</div>
					<div class="toggle"><a href='#'>+ CSS</a></div>
					<div class="toggleChild hidden">
						<pre><code class="language-css">
.quickView {
    background: url(/_assets/global/images/icons/common_icons_sprite.png) no-repeat 0 -42px;
    color: #339;
    font-size: 14px;
    font-family: futuraStdMedium;
    padding-left: 18px;
    cursor: pointer;
}

.fl {
    float: left!important;
}
.padRight_5 {
    padding-right: 5px!important;
}
.marRight_30 {
    margin-right: 30px!important;
}
Inherited from div.grid_3.alpha.padBottom_10.fl.quickViewAndCompare
.quickViewAndCompare {
    text-align: center;
}
						</code></pre>

					</div>
				</div>
			</div><%--end component--%>

			<hr />
			<div class="marTop_10 marBottom_10">
				<div class="grid_4 alpha ">
					<img src="images/linkProductSwatches.png" />
				</div>
				<div class="grid_4 ">
					<a href="#" target="_blank">Category Page - Product Swatches</a>
				</div>
				<div class="grid_4  omega">
					<div class="toggle"><a href='#'>+ HTML</a></div>
					<div class="toggleChild hidden">
						<pre><code  class="language-html">
<c:set var="html">
<div class="prodSwatchesContainer clearfix">
<div class="prodSwatches hideSwatchRows">
<a href="/store/product/braun-reg-flip-cover-alarm-clock/3247854?categoryId=12329&amp;skuId=42192787" class="fl selected" title="BLACK" data-color-value="42192787" data-color-param="skuId" data-main-image-src="//s7d1.scene7.com/is/image/BedBathandBeyond/33428542192787p?$229$" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/product/braun-reg-flip-cover-alarm-clock/3247854?catego_2&quot;;return this.s_oc?this.s_oc(e):true">
<span>
</span>
</a>
<a href="/store/product/braun-reg-flip-cover-alarm-clock/3247854?categoryId=12329&amp;skuId=42192800" class="fl" title="GREY" data-color-value="42192800" data-color-param="skuId" data-main-image-src="//s7d1.scene7.com/is/image/BedBathandBeyond/33428342192800p?$229$" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/product/braun-reg-flip-cover-alarm-clock/3247854?catego_3&quot;;return this.s_oc?this.s_oc(e):true">
<span>
</span>
</a><a href="/store/product/braun-reg-flip-cover-alarm-clock/3247854?categoryId=12329&amp;skuId=42192794" class="fl" title="WHITE" data-color-value="42192794" data-color-param="skuId" data-main-image-src="//s7d1.scene7.com/is/image/BedBathandBeyond/33428442192794p?$229$" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/product/braun-reg-flip-cover-alarm-clock/3247854?catego_4&quot;;return this.s_oc?this.s_oc(e):true">
<span>
</span>
</a>
<div class="clear"></div>
</div>
</div>
</c:set>
${fn:escapeXml(html)}
						</code></pre>

					</div>
					<div class="toggle"><a href='#'>+ CSS</a></div>
					<div class="toggleChild hidden">
						<pre><code class="language-css">
ul.prodGridRow li.product div.prodSwatchesContainer {
    display: block;
}

ul.prodGridRow li.grid_3 .prodSwatches {
    width: 200px;
    float: left;
}
ul.prodGridRow li.product .prodSwatches {
    text-align: left;
    width: 145px;
    float: left;
}
.prodSwatchesContainer .hideSwatchRows {
    height: 28px;
    overflow: hidden;
}
						</code></pre>

					</div>
				</div>
			</div>

			<hr />
			<div class="marTop_10 marBottom_10">
				<div class="grid_4 alpha ">
					<img src="images/linkContactUS.png" />
				</div>
				<div class="grid_4 ">
					<a href="http://bedbathandbeyond.custhelp.com/" target="_blank">http://bedbathandbeyond.custhelp.com/</a>
				</div>
				<div class="grid_4  omega">
					<div class="toggle"><a href='#'>+ HTML</a></div>
					<div class="toggleChild hidden">
						<pre><code  class="language-html">
<c:set var="html">
<div id="rn_SideBar">
<div class="rn_Padding">
<div class="rn_Module">
<h2>Contact Us</h2>
<div class="rn_HelpResources">
<div class="rn_Feedback">
<div id="rn_SiteFeedback2_18" class="rn_SiteFeedback2">
<a id="rn_SiteFeedback2_18_FeedbackLink" href="javascript:void(0);">Give Feedback</a>
<div class="rn_Hidden">
<div id="rn_SiteFeedback2_18_SiteFeedback2Form" class="rn_SiteFeedback2Form">
<div id="rn_SiteFeedback2_18_ErrorMessage"></div>
<form>
<label for="rn_SiteFeedback2_18_EmailInput">Email<span class="rn_Required"> *</span><span class="rn_ScreenReaderOnly">Required</span></label>
<input id="rn_SiteFeedback2_18_EmailInput" class="rn_EmailField" type="text" value="">
<label for="rn_SiteFeedback2_18_FeedbackTextarea">Your Feedback<span class="rn_Required"> *</span><span class="rn_ScreenReaderOnly">Required</span></label>
<textarea id="rn_SiteFeedback2_18_FeedbackTextarea" name="rn_SiteFeedback2_18_FeedbackTextarea" class="rn_Textarea" rows="4" cols="60"></textarea>
</form>
</div>
 </div>
</div>
<span>How can we make this site more useful for you?</span>
</div>
</div>
</div>
</div>
</div>
</c:set>
${fn:escapeXml(html)}
						</code></pre>

					</div>
					<div class="toggle"><a href='#'>+ CSS</a></div>
					<div class="toggleChild hidden">
						<pre><code class="language-css">
#rn_SideBar {
    width: 24%;
    float: left;
    margin-top: 8px;
}
.rn_Padding {
    padding: 8px 10px 2px 10px;
}
.rn_Module {
    background: #F7F7F7;
    border: 1px solid #CCC;
    clear: both;
    margin-bottom: 12px;
    overflow: hidden;
    padding-bottom: 8px;
}
.rn_HelpResources .rn_Questions, 
.rn_HelpResources .rn_Community, 
.rn_HelpResources .rn_Contact, 
.rn_HelpResources .rn_Chat, 
rn_HelpResources 
.rn_Feedback {
    margin-top: 10px;
}
						</code></pre>

					</div>
				</div>
			</div>


			<hr />
			<div class="marTop_10 marBottom_10">
				<div class="grid_4 alpha ">
					<img src="images/linkCarousel.png" />
				</div>
				<div class="grid_4 ">
					<a href="http://bedbathandbeyonddev.com:7003/store/giftregistry/view_registry_owner.jsp?registryId=203528702&eventType=Wedding&_requestid=11998#t=browseGifts" target="_blank">Owner Registry tabs - Gift Ideas</a>
				</div>
				<div class="grid_4  omega">
					<div class="toggle"><a href='#'>+ HTML</a></div>
					<div class="toggleChild hidden">
						<pre><code  class="language-html">
<c:set var="html">
<div class="carouselBody grid_12">
      <div class="grid_1 carouselArrow omega carouselArrowPrevious clearfix">
         &nbsp; 
          <a class="carouselScrollPrevious disabled" role="button" title="Previous" href="#" style="display: block;">Previous</a>
            </div><div class="carouselContent grid_10 clearfix">
             <div class="caroufredsel_wrapper" style="text-align: start; float: none; position: relative; top: auto; right: auto; bottom: auto; left: auto; width: 830px; height: 276px; margin: 0px 0px 20px; overflow: hidden;">
              <ul class="prodGridRow" style="text-align: left; float: none; position: absolute; top: 0px; left: 0px; margin: 0px; width: 4482px; height: 276px;">
                 <li class="grid_2 product alpha" style="height: 276px;">
                   <div class="productShadow"></div>
                     <div class="productContent">
                      <a title="KitchenAid® Artisan® 5 qt. Stand Mixer" class="prodImg" onclick="javascript:pdpCrossSellProxy('crossSell', 'Popular items (viewKickStarters)')" href="/store/product/kitchenaid-reg-artisan-reg-5-qt-stand-mixer/102986">
                       <img class="productImage noImageFound" src="//s7d9.scene7.com/is/image/BedBathandBeyond/251198102986c?$400$" alt="image of KitchenAid® Artisan® 5 qt. Stand Mixer" height="146" width="146">
                          <noscript>&lt;img src="//s7d9.scene7.com/is/image/BedBathandBeyond/251198102986c?$400$" height="146" width="146" alt="image of KitchenAid&amp;reg; Artisan&amp;reg; 5 qt. Stand Mixer" class="noImageFound"/&gt;</noscript></a>
                          <ul class="prodInfo">
                            <li class="prodName">
                             <a title="KitchenAid® Artisan® 5 qt. Stand Mixer" onclick="javascript:pdpCrossSellProxy('crossSell', 'Popular items (viewKickStarters)')" href="/store/product/kitchenaid-reg-artisan-reg-5-qt-stand-mixer/102986">KitchenAid® Artisan® 5 qt. Stand Mixer</a></li><li class="prodPrice"><ul><li class="isPrice">$349.99 Each</li></ul></li></ul></div></li>
							<li class="grid_2 product alpha" style="height: 276px;"><div class="productShadow"></div>
						<div class="grid_1 carouselArrow alpha carouselArrowNext clearfix">&nbsp; <a class="carouselScrollNext" title="Next" role="button" href="#" style="display: block;">Next</a></div><div class="carouselPages"><div class="carouselPageLinks clearfix" style="display: block;">   <a href="#" class="selected"><span>1 of 3 </span></a><a href="#"><span>2 of 3 </span></a><a href="#"><span>3 of 3 </span></a></div></div></div>
</c:set>
${fn:escapeXml(html)}
						</code></pre>

					</div>
					<div class="toggle"><a href='#'>+ CSS</a></div>
					<div class="toggleChild hidden">
						<pre><code class="language-css">
div.carousel, div.carousel div.carouselBody, div.carousel div.carouselPages {
    margin: 0;
}
.container_12 .grid_12, .width_12 {
    width: 976px;
}
.grid_1, .grid_2, .grid_3, .grid_4, .grid_5, .grid_6, .grid_7, .grid_8, .grid_9, .grid_10, .grid_11, .grid_12 {
    display: inline;
    float: left;
    position: relative;
    margin-left: 10px;
    margin-right: 10px;
}
						</code></pre>

					</div>
				</div>
			</div>


			<hr />
			<div class="marTop_10 marBottom_10">
				<div class="grid_4 alpha ">
					<img src="images/linkOwnerRegistryHeaderMenu.png" />
				</div>
				<div class="grid_4 ">
					<a href="http://bedbathandbeyonddev.com:7003/store/giftregistry/view_registry_owner.jsp?registryId=203528702&eventType=Wedding&_requestid=11998#t=browseGifts" target="_blank">Owner Registry Header menu</a>
				</div>
				<div class="grid_4  omega">
					<div class="toggle"><a href='#'>+ HTML</a></div>
					<div class="toggleChild hidden">
						<pre><code  class="language-html">
<c:set var="html">
<div id="regToolbar" class="noprint grid_12 alpha omega">
<div class="regGiftTools topLevel fl">
<ul>
<li id="browseAddGifts">
<a href="#" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/giftregistry/view_registry_owner.jsp?registryId=2035287_10&quot;;return this.s_oc?this.s_oc(e):true">
<span class="icon-fallback-text">
<span class="icon-gift" aria-hidden="true"></span>
<span class="icon-text"></span>
</span>
<span class="toolLbl">Browse &amp; Add Gifts</span>
</a>
</li>
<li class="nolink">
<span class="giftCounter" id="regGiftsWanted">0</span> 
<span class="toolLbl">Added</span>
</li>
<li class="nolink">
<span class="giftCounter">0</span> 
<span class="toolLbl">Purchased</span></li>
</ul>
</div>
<div class="regUtilityLinks topLevel fr">
<ul>
<li id="scheduleAppointment" class="scheduleAppointmentCall" data-param-isregheader="true" data-param-appointmentcode="REG" data-param-storeid="" data-param-registryid="203528702" data-param-coregfn="" data-param-coregln="" data-param-eventdate="05/20/2016"><!-- if user is not transient then get the profile details -->
<link rel="stylesheet" type="text/css" property="stylesheet" href="/_assets/global/css/contact_store.min.css?v=2.08.16.002">
<input type="hidden" value="REG" id="defaultAppointmentCode">								
<input type="hidden" value="false" id="errorOnModal">
<input type="hidden" value="203528702" id="registryId">
<input type="hidden" value="05/20/2016" id="eventDate">
<input type="hidden" value="" id="coregFN">
<input type="hidden" value="" id="coregLN">
<a id="registryHeaderSchedule" href="#" class="scheduleAppointmentLink" title="Schedule Appointment" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/giftregistry/view_registry_owner.jsp?registryId=2035287_11&quot;;return this.s_oc?this.s_oc(e):true">
<span class="icon-fallback-text">
<span class="icon-calendar" aria-hidden="true"></span>
<span class="icon-text"></span>
</span>
<span class="toolLbl">Schedule Appointment</span></a></li>
<li>	
<div id="chatNowLink">
<a title="Chat Now with a Representative" class="chatNow liveChat" data-chatdata="_icf_1/2" href="/store/selfservice/click_to_chat.jsp" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/selfservice/click_to_chat.jsp_1&quot;;return this.s_oc?this.s_oc(e):true"><span class="icon-fallback-text">
<span class="icon-bubbles" aria-hidden="true"></span>
<span class="icon-text"></span>
</span>
<span class="toolLbl">Chat Live</span></a>	
</div> </li> 
<li class="hidden">
<input type="hidden" name="emptyRegistrant" value="true">
</li> 
<li id="share">
<a href="#" id="regHeaderShare" title="Share" aria-label="Share" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/giftregistry/view_registry_owner.jsp?registryId=2035287_12&quot;;return this.s_oc?this.s_oc(e):true">
<span class="icon-fallback-text">
<span class="icon-share-alt" aria-hidden="true"></span>
<span class="icon-text"></span>
</span>
<span class="toolLbl">Share</span></a>
<form id="frmRegistryInfo" action="index.jsp?_DARGS=/store/giftregistry/view_registry_owner.jsp" method="post"><div style="display:none"><input name="_dyncharset" value="UTF-8" type="hidden"> </div><div style="display:none"><input name="_dynSessConf" value="-7988468930285773051" type="hidden"> </div>
<div id="emailAFriendData">
<input type="hidden" name="dateCheck" value="true" class="emailAFriendFields">
<input type="hidden" name="daysToNextCeleb" value="" class="emailAFriendFields">
<input type="hidden" name="daysToGo" value="17" class="emailAFriendFields">
<input type="hidden" name="eventType" value="BRD" class="emailAFriendFields">
<input type="hidden" name="registryURL" value="http://bedbathandbeyonddev.com:7003/store/giftregistry/view_registry_guest.jsp?registryId=203528702&amp;eventType=Wedding&amp;pwsurl=" class="emailAFriendFields">
<input type="hidden" id="userEmail" name="senderEmail" value="ayanbabi.pal@bedbath.com" class="emailAFriendFields">
<input type="hidden" name="registryId" value="203528702" class="emailAFriendFields">
<input type="hidden" name="formComponentName" value="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" class="emailAFriendFields">
<input type="hidden" name="handlerName" value="EmailRegistry" class="emailAFriendFields">
<input type="hidden" name="title" value="Email Your Registry to Friends" class="emailAFriendFields">
<input type="hidden" name="pRegFirstName" value="Babi" class="emailAFriendFields">
<input type="hidden" name="pRegLastName" value="Pal" class="emailAFriendFields">
<input type="hidden" name="coRegFirstName" value="Canada" class="emailAFriendFields">
<input type="hidden" name="coRegLastName" value="Man" class="emailAFriendFields">
<input type="hidden" name="eventDate" value="05/20/2016" class="emailAFriendFields">
<input type="hidden" name="eventTypeRegistry" value="Wedding" class="emailAFriendFields">
<input type="hidden" name="subject" value="Email a Registry Subject" class="emailAFriendFields">
<input name="isInternationalCustomer" value="false" type="hidden"><input name="_D:isInternationalCustomer" value=" " type="hidden">
</div>
<input type="submit" class="visuallyhidden hidden" value=" ">
<div style="display:none"><input name="_DARGS" value="/store/giftregistry/view_registry_owner.jsp" type="hidden"> </div></form>
<div id="regSharePopup" class="hidden">
<div id="regSharePopupLinks" class="sharePopupSection">
<h2>Share your registry</h2>
<h3>with friends &amp; family!</h3>
<div>						
<a href="http://www.facebook.com/sharer.php?s=100&amp;p[url]=http%3A%2F%2Fbedbathandbeyonddev.com%3A7003%2Fstore%2Fgiftregistry%2Fview_registry_guest.jsp%3FregistryId%3D203528702%26eventType%3DWedding%26pwsurl%3D" target="_blank" class="social_button icon_facebook" aria-label="Share Via Facebook" onclick="s_objectID=&quot;http://www.facebook.com/sharer.php?s=100&amp;p[url]=http%3A%2F%2Fbedbathandbeyonddev.com%3A7003%2Fsto_1&quot;;return this.s_oc?this.s_oc(e):true"></a>
<a href="https://twitter.com/share?url=http%3A%2F%2Fbedbathandbeyonddev.com%3A7003%2Fstore%2Fgiftregistry%2Fview_registry_guest.jsp%3FregistryId%3D203528702%26eventType%3DWedding%26pwsurl%3D" target="_blank" class="social_button icon_twitter" aria-label="Share Via Twitter" onclick="s_objectID=&quot;https://twitter.com/share?url=http%3A%2F%2Fbedbathandbeyonddev.com%3A7003%2Fstore%2Fgiftregistry%_1&quot;;return this.s_oc?this.s_oc(e):true"></a>
<a href="https://pinterest.com/pin/create/bookmarklet/?url=http%3A%2F%2Fbedbathandbeyonddev.com%3A7003%2Fstore%2Fgiftregistry%2Fview_registry_guest.jsp%3FregistryId%3D203528702%26eventType%3DWedding%26pwsurl%3D&amp;media=http://bedbathandbeyonddev.com/_assets/global/images/logo/logo_br.png" target="_blank" class="social_button icon_pin" aria-label="Share via Pinterest" onclick="s_objectID=&quot;https://pinterest.com/pin/create/bookmarklet/?url=http%3A%2F%2Fbedbathandbeyonddev.com%3A7003%2Fs_1&quot;;return this.s_oc?this.s_oc(e):true"></a>
<a href="https://plus.google.com/share?url=http%3A%2F%2Fbedbathandbeyonddev.com%3A7003%2Fstore%2Fgiftregistry%2Fview_registry_guest.jsp%3FregistryId%3D203528702%26eventType%3DWedding%26pwsurl%3D" target="_blank" class="social_button icon_gplus" aria-label="Share via Google Plus" onclick="s_objectID=&quot;https://plus.google.com/share?url=http%3A%2F%2Fbedbathandbeyonddev.com%3A7003%2Fstore%2Fgiftregis_1&quot;;return this.s_oc?this.s_oc(e):true"></a>
<a href="#" class="btnEmail " title="Email Your Registry to Friends" aria-label="Share via Email" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/giftregistry/view_registry_owner.jsp?registryId=2035287_13&quot;;return this.s_oc?this.s_oc(e):true">				            	
<span class="icon-fallback-text">
<span class="icon-envelope" aria-hidden="true"></span>
<span class="icon-text">Email Your Registry to Friends</span>
</span>
</a>
</div>
<input name="bedBath" id="shareRegURL" type="text" class="width_4 escapeHTMLTag" readonly="readonly" value="http://bedbathandbeyonddev.com:7003/store/giftregistry/view_registry_guest.jsp?registryId=203528702&amp;eventType=Wedding&amp;pwsurl=" aria-required="false" aria-labelledby="shareRegURL"> 
<div class="clearfix"></div>
<a href="#" class="copy2Clip button-Med btnSecondary" data-copy-source="shareRegURL" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/giftregistry/view_registry_owner.jsp?registryId=2035287_14&quot;;return this.s_oc?this.s_oc(e):true">COPY URL</a>	
</div>	
<div id="regSharePopupSocialRec" class="sharePopupSection ">
<h2>get recommendations</h2>	
<h3>from friends &amp; family</h3>
<p>Your friends and family know you best. get helpful advice on what to register for.</p>	
<!-- BPS-1112 Persist registry Details in GiftRegistryRepository --> 
<a href="/store/_includes/modals/inviteFriendsRegistry.jsp?eventType=Wedding&amp;registryId=203528702&amp;regEventDate=05/20/2016&amp;regFirstName=Babi&amp;emptyRegistrant=0" class="button-Med btnPrimary inviteFriends" title="INVITE NOW" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/_includes/modals/inviteFriendsRegistry.jsp?eventType=We_1&quot;;return this.s_oc?this.s_oc(e):true">INVITE NOW</a> 		
</div></div> </li> 
<li id="registryTools" class="last">
<a href="#" title="Registry Tools" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/giftregistry/view_registry_owner.jsp?registryId=2035287_15&quot;;return this.s_oc?this.s_oc(e):true">
<span class="icon-fallback-text">
<span class="icon-menu" aria-hidden="true"></span>
<span class="icon-text">Registry Tools</span>
</span>
<span class="toolLbl">Registry Tools</span>	
</a>	
<div class="grid_3 fr" id="regToolsFlyout" style="display: none;">
<a href="#" class="regToolsClose" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/giftregistry/view_registry_owner.jsp?registryId=2035287_16&quot;;return this.s_oc?this.s_oc(e):true">
<span class="icon-fallback-text">
<span class="icon-times" aria-hidden="true"></span>
<span class="icon-text">close</span>
</span>
</a>
<ul id="regToolsList">
<li class="header">registry tools</li>
<li><a title="Guides &amp; Advice" href="/store/registry/GuidesAndAdviceLandingPage" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/registry/GuidesAndAdviceLandingPage_1&quot;;return this.s_oc?this.s_oc(e):true">Guides &amp; Advice</a></li>          
<li><a href="/store/bbregistry/BridalBook" title="Wedding Registry Book" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/bbregistry/BridalBook_1&quot;;return this.s_oc?this.s_oc(e):true">Wedding Registry Book</a></li>
<li><a href="/store/printCards/printCards.jsp" title="Registry Announcement Cards" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/printCards/printCards.jsp_1&quot;;return this.s_oc?this.s_oc(e):true">Announcement Cards</a></li>
<li><a title="Personalized Invitations" href="/store/registry/PersonalizedInvitations" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/registry/PersonalizedInvitations_1&quot;;return this.s_oc?this.s_oc(e):true">Personalized Invitations</a></li>
<li class="separator"></li>
<li><a title="Registry Features" href="/store/registry/RegistryFeatures" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/registry/RegistryFeatures_1&quot;;return this.s_oc?this.s_oc(e):true">Registry Features</a></li>
 <li><a title="Registry Incentives" href="/store/registry/RegistryIncentives" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/registry/RegistryIncentives_1&quot;;return this.s_oc?this.s_oc(e):true">Registry Incentives</a></li>
</ul>
</div></li>	
</ul> </div></div>
</c:set>
${fn:escapeXml(html)}
						</code></pre>

					</div>
					<div class="toggle"><a href='#'>+ CSS</a></div>
					<div class="toggleChild hidden">
						<pre><code class="language-css">

#regToolbar {
    background: #cdcdcd;
}
#regToolbar {
    height: 64px;
}

.fl {
    float: left!important;
}
#regToolbar .topLevel>ul>li {
    display: inline-block;
    margin: 0;
    text-align: center;
    height: 64px;
    padding: 15px;
    box-sizing: border-box;
    vertical-align: top;
}
.regGiftTools #browseAddGifts {
    position: relative;
}
ul, ol, li {
    list-style: none;
}

#regToolbar li .giftCounter, #regToolbar li .icon-fallback-text {
    display: block;
    font-size: 22px;
    height: 22px;
    font-family: ITCNewBaskervilleRoman;
    color: #6d3e8f;
    margin-bottom: 5px;
}
[class^="icon-"], [class*=" icon-"] {
    font-family: 'BedBathIcons'!important;
    speak: none;
    font-style: normal;
    font-weight: normal;
    font-variant: normal;
    text-transform: none;
    line-height: 1;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
}

#regToolbar .toolLbl {
    font-family: FuturaStdMedium;
    font-size: 10px;
    text-transform: uppercase;
    color: #444;
}

.regUtilityLinks {
    background: #cdcdcd;
}
.regUtilityLinks {
    height: 65px;
}

#regToolbar:after {
    background: url(/_assets/bbregistry/images/regHeaderBottomShadow.jpg) no-repeat center top;
    height: 13px;
    width: 976px;
    content: "";
    position: absolute;
    display: block;
    bottom: -13px;
    left: 0;
    -webkit-print-color-adjust: exact;
}
						</code></pre>

					</div>
				</div>
			</div>
			<hr />
			<div class="marTop_10 marBottom_10">
				<div class="grid_4 alpha ">
					<img src="images/linkRegistryEditHeader.png" />
				</div>
				<div class="grid_4 ">
					<a href="http://bedbathandbeyonddev.com:7003/store/giftregistry/view_registry_owner.jsp?registryId=203528702&eventType=Wedding&_requestid=11998#t=browseGifts" target="_blank">Registry Header Edit</a>
				</div>
				<div class="grid_4  omega">
					<div class="toggle"><a href='#'>+ HTML</a></div>
					<div class="toggleChild hidden">
						<pre><code  class="language-html">
<c:set var="html">
<div id="regEditCtrlRow" class="">
<ul>
<li>
<span class="icon-fallback-text"> 
<a class="icon icon-pencil editRegInfo" href="#" title="Edit Info" aria-label="Edit Info" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/giftregistry/view_registry_owner.jsp?registryId=2035287_6&quot;;return this.s_oc?this.s_oc(e):true">
</a>
<span class="icon-text">Edit Info</span></span>	</li><li>	
<span class="icon-fallback-text"> 
<a class="icon icon-print btnPrint" title="Print" href="#" aria-label="Print" onclick="s_objectID=&quot;http://bedbathandbeyonddev.com:7003/store/giftregistry/view_registry_owner.jsp?registryId=2035287_7&quot;;return this.s_oc?this.s_oc(e):true">								 
</a>
<span class="icon-text">Print</span>
</span>
</li>
</ul>
</c:set>
${fn:escapeXml(html)}
						</code></pre>

					</div>
					<div class="toggle"><a href='#'>+ CSS</a></div>
					<div class="toggleChild hidden">
						<pre><code class="language-css">

#regEditCtrlRow {
    position: absolute;
    right: 250px;
    z-index: 1;
}
[class^="icon-"], [class*=" icon-"] {
    font-family: 'BedBathIcons'!important;
    speak: none;
    font-style: normal;
    font-weight: normal;
    font-variant: normal;
    text-transform: none;
    line-height: 1;
    -webkit-font-smoothing: antialiased;
}
						</code></pre>

					</div>
				</div>
			</div>

		<link href="css/prism.css" rel="stylesheet" />
		<script src="js/prism.js"></script>
		
		<script type="text/javascript">

			window.onload = function() {
				BBB.fn.makeTabs($('.styleGuideTabs'));			  	
			};

			$(document).ready(function() {
				$('.toggle a').click(function(e){
					e.preventDefault();
					$(this).parent().next('.toggleChild').toggleClass('hidden');
				});
			});

		</script>

	</bbb:pageContainer>
</dsp:page>