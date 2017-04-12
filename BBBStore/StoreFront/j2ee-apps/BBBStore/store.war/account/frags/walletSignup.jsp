<dsp:page>
					<div class="couponListForm grid_9 alpha omega">
				  			<h3 class=""><bbbl:label key="lbl_want_more" language="${pageContext.request.locale.language}" /> <b><bbbl:label key="lbl_coupons" language="${pageContext.request.locale.language}" /></b></h3>
				  			<div class="flatform cpnForm grid_4 alpha fl">

				  				<form id="couponSignupFormEmail" action="#" >
				  					<%-- TODO - labels--%>
				  					<legend><bbbl:label key="lbl_sign_up_for" language="${pageContext.request.locale.language}" /> <b><bbbl:label key="lbl_email_coupons" language="${pageContext.request.locale.language}" /></b></legend>

					  				<div class="clearfix">
					  					<div class="input_wrap">
					  					<label class="popUpLbl" for="email"><bbbl:label key="lbl_chat_email" language ="${pageContext.request.locale.language}"/></label>
						  				<input id="email" name="email" class="fl grid_3 alpha omega" type="text" />
					  					</div>
						  				<input type="button" class="button-Large btnPrimary" value="Submit" />
						  			</div>
				  				</form>
				  			</div>
				  			<div class="flatform cpnForm fl">

				  				<form id="couponSignupFormSMS" action="#">
					  				
									<legend><bbbl:label key="lbl_sign_up_for" language="${pageContext.request.locale.language}" /> <b><bbbl:label key="lbl_sms_coupons" language="${pageContext.request.locale.language}" /></b></legend>
									<div class="clearfix">
						  				<div class="input_wrap">
						  					<label class="popUpLbl" for="basePhone"><bbbl:label key="lbl_phonefield_phnumber" language ="${pageContext.request.locale.language}"/></label>
							  				<input id="basePhone" class="fl grid_3 alpha omega" name="basePhone" type="text" />
							  			</div>
						  			<input type="button" class="button-Large btnPrimary" value="Submit" />
							  		</div>
					  			</form>
				  			</div>
				  			<!-- <div class="dirMailCpnForm grid_10 alpha omega">
				  				<label for="">
				  					Sign Up for <b>Direct Mail Coupons</b>
				  				</label>
				  				<div class="tab address1">
					  				<input id="address1" class="fl grid_4 alpha omega" name="" placeholder="Address" type="text" />
				  				</div>
				  				<div class="tab apt">
					  				<input class="fl" name="" placeholder="Apt/Suite" type="text" />
					  			</div>
					  			<div class="tab city">
					  				<input id="city" class="fl" name="" placeholder="City" type="text" />
					  			</div>
				  				<input id="state" class="fl grid_1 alpha" name="" placeholder="State" type="text" />
				  				<div class="tab zip">
					  				<input id="zip" class="fl" name="" placeholder="Zip Code" type="text" />
				  				</div>
				  				<input type="button" class="button-Med btnPrimary" value="Submit" />
				  			</div> -->
				  		</div>

				  		<div class="clear"></div>
</dsp:page>