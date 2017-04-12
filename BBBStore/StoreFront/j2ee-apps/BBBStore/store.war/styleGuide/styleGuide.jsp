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
	


	<bbb:pageContainer section="accounts">
		<div id="content" class="container_12 clearfix flatform">

			<div class="grid_12 alpha omega font_heavy">
				<h1 style="background-color:#eee;">COLORS AND VARIATIONS</h1>
				<div class="grid_4 alpha omega">
					<h3>PRIMARY #273691</h3>
				</div>
				<div class="grid_4 alpha omega">
					<h3>SECONDARY #00AEF0</h3>
				</div>
				<div class="grid_4 alpha omega">
					<h3>SHOP #FF6A23</h3>
				</div>
			</div>

			<div class="grid_12 alpha omega font_heavy">
				<h1 style="background-color:#eee;">TYPOGRAPHY
				</h1>

				<div class="grid_6 fl alpha omega">
					<h1 class="font_heavy">HEAVY - .font_heavy</h1>

					<h3 class="font_heavy">Aa Bb Cc Dd Ee Ff Gg Hh Ii Jj Kk Ll Mm Nn Oo</br>
						Pp Qq Rr Ss Tt Uu Vv Ww Xx Yy Zz</br>
						1234567890!@#$%^&*
					</h3>
				</div>

				<div class="grid_6 fl alpha omega font_light">
					<h1 class="font_light">LIGHT - .font_light</h1>

					<h3 class="font_light">Aa Bb Cc Dd Ee Ff Gg Hh Ii Jj Kk Ll Mm Nn Oo</br>
						Pp Qq Rr Ss Tt Uu Vv Ww Xx Yy Zz</br>
						1234567890!@#$%^&*
					</h3>
				</div>

				<div class="grid_12 alpha omega">
					<h1 class="font_heavy" style="background-color:#eee;">
						HEADINGS
					</h1>
					<div class="grid_6 alpha omega">
						<div class="grid_6 alpha omega">
							<H1 class="font_heavy">H1 - This is a Header One</H1>
							<h2 class="font_heavy">H2 - This is a Header Two</h2>
							<h3 class="font_heavy">H3 - This is a Header Three</h3>
							<h4 class="font_heavy">H4 - This is a Header Four</h4>
							<h5 class="font_heavy">H5 - This is a Header Five</h5>
							<h6 class="font_heavy">H6 - This is a Header Six</h6>
						</div>
						<div class="grid_6 alpha omega spec_txt">
							<h2>
								SPECIAL TEXT
								<span>COMPONENT</span>
							</h2>
						</div>
					</div>
					<div class="grid_6 alpha omega">
						<h3>PARAGRAPH</h3>
						<p>This is a paragraph text and <a href="#">link text is bold.</a> And the hover link text changes color to <a href="#">look like this.</a> It should always be 13px font size on Desktop, with a line-height of 18px. Paragraphs should have a margin between them 13px.</p>
						<p>This is a second paragraph just to show how the margin should work in between the paragraphs. This shows the styling for <e>emphasized text</e> and <b>bold text</b></p>
						<div class="grid_3 alpha omega">
							<h3>UNORDERED LIST</h3>
							<ul>
								<li><p>Curabitur blandit tempus porttitor.</p></li>
								<li><p>Lorem ipsum dolor sit amet.</p></li>
								<li><p>Maecenas fausibus mollis interdum.</br>
									Nullam id dolor id vehicula ut elit.</p>
								</li>
								<li><p>Curabitur blandit tempus porttitor.</p></li>
							</ul>
						</div>
						<div class="grid_3 alpha omega">
							<h3>ORDERED LIST</h3>
							<ol>
								<li><p>Curabitur blandit tempus porttitor.</p></li>
								<li><p>Lorem ipsum dolor sit amet.</p></li>
								<li><p>Maecenas fausibus mollis interdum.</br>
									Nullam id dolor id vehicula ut elit.</p>
								</li>
								<li><p>Curabitur blandit tempus porttitor.</p></li>
							</ol>
						</div>
						<div class="grid_6 alpha omega">
							<h3>BLOCKQUOTE</h3>
							<i><blockquote>"Curabitur blandit tempus porttitor. Sed posuere consectetur est at lobortis."</blockquote></i>
							<p>-Firstname Lastname</p>
						</div>
					</div>
				</div>
			</div>
			<div class="grid_12 alpha omega">
				<h1 style="background-color:#eee;">ICONS
					<a href="#icons"></a>
				</h1>

			</div>

			<div class="grid_12 alpha omega">
				<h1 class="font_heavy" style="background-color:#eee;">THE ALMIGHTY GRID</h1>
				<ul>
					<li><h5>.grid_[#] .alpha .omega</h5></li>
					<li><h5>.alpha - reduce margin-left</h5></li>
					<li><h5>.omega - reduce margin-right</h5></li>
				</ul>
				<h3>THE COLUMNS</h3>

				<div class="grid_12 alpha omega marTop_10">
					<div class="grid_12 alpha omega height_118 gridBkgd algn_ctr">
						<h1>12</h1>
					</div>
				</div>
				<div class="grid_12 alpha omega marTop_10">
					<div class="grid_1 alpha height_118 gridBkgd algn_ctr">
						<h1>1</h1>
					</div>
					<div class="grid_11 omega height_118 gridBkgd algn_ctr">
						<h1>11</h1>
					</div>
				</div>
				<div class="grid_12 alpha omega marTop_10">
					<div class="grid_2 alpha height_118 gridBkgd algn_ctr">
						<h1>2</h1>
					</div>
					<div class="grid_10 omega height_118 gridBkgd algn_ctr">
						<h1>10</h1>
					</div>
				</div>
				<div class="grid_12 alpha omega marTop_10">
					<div class="grid_3 alpha height_118 gridBkgd algn_ctr">
						<h1>3</h1>
					</div>
					<div class="grid_9 omega height_118 gridBkgd algn_ctr">
						<h1>9</h1>
					</div>
				</div>
				<div class="grid_12 alpha omega marTop_10">
					<div class="grid_4 alpha height_118 gridBkgd algn_ctr">
						<h1>4</h1>
					</div>
					<div class="grid_8 omega height_118 gridBkgd algn_ctr">
						<h1>8</h1>
					</div>
				</div>
				<div class="grid_12 alpha omega marTop_10">
					<div class="grid_5 alpha height_118 gridBkgd algn_ctr">
						<h1>5</h1>
					</div>
					<div class="grid_7 omega height_118 gridBkgd algn_ctr">
						<h1>7</h1>
					</div>
				</div>
				<div class="grid_12 alpha omega marTop_10">
					<div class="grid_6 alpha height_118 gridBkgd algn_ctr">
						<h1>6</h1>
					</div>
					<div class="grid_6 omega height_118 gridBkgd algn_ctr">
						<h1>6</h1>
					</div>
				</div>
			</div>

			<div class="grid_12 alpha omega">
				<h1 style="background-color:#eee;">Alerts</h1>

				<div class="grid_6 alpha">
					<h3>ERROR - .alert .alert-error</h3>
					<div class="alert alert-error">
						<p>This is a paragraph text and <a href="#">link text is bold.</a> And the hover link text changes color to <a href="#">look like this.</a> It should always be 13px font size on Desktop, with a line-height of 18px. Paragraphs should have a margin between them 13px.</p>
					</div>
				</div>
				<div class="grid_6 alpha omega">
					<h3>SUCCESS - .alert .alert-success</h3>
					<div class="alert alert-success">
						<p>This is a paragraph text and <a href="#">link text is bold.</a> And the hover link text changes color to <a href="#">look like this.</a> It should always be 13px font size on Desktop, with a line-height of 18px. Paragraphs should have a margin between them 13px.</p>
					</div>
				</div>
				<div class="grid_6 alpha">
					<h3>WARNING - .alert .alert-warn</h3>
					<div class="alert alert-warn">
						<!-- <span class="warn">&nbsp;</span> -->
						<p>This is a paragraph text and <a href="#">link text is bold.</a> And the hover link text changes color to <a href="#">look like this.</a> It should always be 13px font size on Desktop, with a line-height of 18px. Paragraphs should have a margin between them 13px.</p>
					</div>
				</div>
				<div class="grid_6 alpha omega">
					<h3>INFO - .alert .alert-info</h3>
					<div class="alert alert-info">
						<!-- <span class="info">&nbsp;</span> -->
						<p>This is a paragraph text and <a href="#">link text is bold.</a> And the hover link text changes color to <a href="#">look like this.</a> It should always be 13px font size on Desktop, with a line-height of 18px. Paragraphs should have a margin between them 13px.</p>
					</div>
					
				</div>
				
			</div>
	
			<div class="grid_12 alpha omega">

				<H1 style="background-color:#eee;">Buttons / Link buttons</H1>

				<div class="bulletsReset">

					<p>Button display is controlled by the combination of 2 classes, one for size, one for style. 
					The available size classes are:</p>

					<ul>
						<li>button-Small</li>
						<li>button-Med</li>
						<li>button-Large</li>
					</ul>

					The available style classes are:
					<ul>
						<li>btnPrimary</li>
						<li>btnSecondary</li>
						<li>addtocart</li>
						<li>btnInverse</li>
						<li>btnRegistryPrimary (Beyond theme only)</li>
						<li>btnRegistrySecondary (Beyond theme only)</li>						
					</ul>

				</div>

				<h6 style="background-color:#eee;">INPUT TYPE BUTTON</h6>

				<table cellpadding="10">
					<thead>
						<tr>
							<th>STYLE</th>
							<th>button-Small</th>
							<th>button-Med</th>
							<th>button-Large</th>
						</tr>
						
					</thead>

					<tbody>
						<tr>
							<th>btnPrimary</th>
							<td><input type="button" class="button-Small btnPrimary" value="Small Primary"/></td>
							<td><input type="button" class="button-Med btnPrimary" value="Medium Primary"/></td>
							<td><input type="button" class="button-Large btnPrimary" value="Large Primary"/></td>
						</tr>

						<tr>
							
							<th>btnSecondary</th>
							<td><input type="button" class="button-Small btnSecondary" value="Small Secondary"/></td>
							<td><input type="button" class="button-Med btnSecondary" value="Medium Secondary"/></td>
							<td><input type="button" class="button-Large btnSecondary" value="Large Secondary"/></td>
						</tr>	

						<tr>
							
							<th>addtocart</th>
							<td><input type="button" class="button-Small addtocart" value="Add to Cart"/></td>
							<td><input type="button" class="button-Med addtocart" value="Add to Cart"/></td>
							<td><input type="button" class="button-Large addtocart" value="Add to Cart"/></td>
						</tr>	
							
						<tr style="background-color:#eee;">
							<th>btnInverse</th>
							<td><input type="button" class="button-Small btnInverse" value="Small Inverse"/></td>
							<td><input type="button" class="button-Med btnInverse" value="Medium Inverse"/></td>
							<td><input type="button" class="button-Large btnInverse" value="Large Inverse"/></td>
						</tr>	
							
						<tr>
							<th>btnRegistryPrimary (Beyond theme only)</th>
							<td><input type="button" class="button-Small btnRegistryPrimary" value="Small Registry Primary"/></td>
							<td><input type="button" class="button-Med btnRegistryPrimary" value="Medium Registry Primary"/></td>
							<td><input type="button" class="button-Large btnRegistryPrimary" value="Large Registry Primary"/></td>
						</tr>	
							
							
						<tr>
							<th>btnRegistrySecondary (Beyond theme only)</th>
							<td><input type="button" class="button-Small btnRegistrySecondary" value="Small "/></td>
							<td><input type="button" class="button-Med btnRegistrySecondary" value="Medium Primary"/></td>
							<td><input type="button" class="button-Large btnRegistrySecondary" value="Large Primary"/></td>
						</tr>	
					</tbody>
				</table>

				<h6 style="background-color:#eee;">LINK BUTTON</h6>

				<table cellpadding="10">
					<thead>
						<tr>
							<th>STYLE</th>
							<th>button-Small</th>
							<th>button-Med</th>
							<th>button-Large</th>
						</tr>
						
					</thead>

					<tbody>
						<tr>
							<th>btnPrimary</th>
							<td><a href="#" class="button-Small btnPrimary">Small Primary</a></td>
							<td><a href="#" class="button-Med btnPrimary" >Medium Primary</a></td>
							<td><a href="#" class="button-Large btnPrimary" >Large Primary</a></td>
						</tr>

						<tr>
							
							<th>btnSecondary</th>
							<td><a href="#"  class="button-Small btnSecondary" >Small Secondary</a></td>
							<td><a href="#"  class="button-Med btnSecondary" >Medium Secondary</a></td>
							<td><a href="#" class="button-Large btnSecondary" >Large Secondary</a></td>
						</tr>	

						<tr>
							
							<th>addtocart</th>
							<td><a href="#"  class="button-Small addtocart" >Add to Cart</a></td>
							<td><a href="#"  class="button-Med addtocart" >Add to Cart</a></td>
							<td><a href="#"  class="button-Large addtocart" >Add to Cart</a></td>
						</tr>	
							
						<tr style="background-color:#eee;">
							<th>btnInverse</th>
							<td><a href="#"  class="button-Small btnInverse">Small Inverse</a></td>
							<td><a href="#"  class="button-Med btnInverse" >Medium Inverse</a></td>
							<td><a href="#"  class="button-Large btnInverse" >Large Inverse</a></td>
						</tr>	
							
						<tr>
							<th>btnRegistryPrimary (Beyond theme only)</th>
							<td><a href="#" class="button-Small btnRegistryPrimary" >Small Registry Primary</a></td>
							<td><a href="#" class="button-Med btnRegistryPrimary"  >Medium Registry Primary</a></td>
							<td><a href="#"  class="button-Large btnRegistryPrimary" >Large Registry Primary</a></td>
						</tr>	
							
							
						<tr>
							<th>btnRegistrySecondary (Beyond theme only)</th>
							<td><a href="#" class="button-Small btnRegistrySecondary" >Small</a></td>
							<td><a href="#" class="button-Med btnRegistrySecondary"  >Medium Primary</a></td>
							<td><a href="#" class="button-Large btnRegistrySecondary" >Large Primary</a></td>
						</tr>	
					</tbody>
				</table>


			</div>


			<div class="grid_12">

				<H1 style="background-color:#eee;">Forms</H1>

				<p>All new forms should have a class of "flatform", either on the the form tag or a wrapper element</p>

				<div class="alert alert-info ">
					Popup labels for text type inputs (text, tel, password, email) is in progress
				</div>

				<div class="alert alert-error  ">
					<span class="warn">&nbsp;</span>
					TO DO: select lists, checkboxes, radio buttons, date input, textarea
				</div>

				<dsp:form  action="#" method="post" iclass="flatform">

					<h3>Unfilled form</h3>

					
					<div class="input_wrap">
						<label class="popUpLbl" for="email">Email Address</label>
						<input  name="email" class="grid_3 alpha omega" type="email" />
					</div>
					
					<div class="clearfix"></div>

					<div class="input_wrap">
						<label class="popUpLbl" for="tel">Telephone</label>
						<input name="tel" class="grid_3 alpha omega" type="tel" />
					</div>

					<div class="clearfix"></div>

					<div class="input_wrap">
						<label class="popUpLbl" for="firstname">First Name</label>
						<input name="firstname" class="grid_3 alpha omega" type="text" />
					</div>

					<div class="input_wrap">
						<label class="popUpLbl" for="lastname">Last Name</label>
						<input name="lastname" class="grid_3 alpha omega" type="text" />
					</div>

					<div class="clearfix"></div>

					<div class="input_wrap">
						<label class="popUpLbl" for="password">Password</label>
						<input id="flatForm_password"  name="password" class="grid_3 alpha omega" type="password" />
					</div>

					<div class="clearfix"></div>

					<div class="input_wrap">
						<label class="popUpLbl" for="date">Date</label>
						<input  name="date" class="hasDatepicker  grid_3 alpha omega" type="text" />
					</div>

					<div class="clearfix"></div>

					<div class="input_wrap">
						<label class="popUpLbl" for="date">Text area label</label>
						<textarea name="textarea" class="grid_3 alpha omega"></textarea>
						
					</div>

					<div class="clearfix"></div>


					<fieldset class="radioList">
						<legend>Your favorite color</legend>
					
						<div class="input_wrap">
							<input type="radio" class="noUniform" name="favColor" />
							<label for="green">Green</label>
						</div>
						
						<div class="input_wrap">
							<input type="radio" name="favColor" />
							<label for="yellow">Yellow</label>
						</div>
						
						<div class="input_wrap">
							<input type="radio" name="favColor" />
							<label for="red">Red</label>
						</div>
					</fieldset>

					<div class="clearfix"></div>

					<div class="input_wrap">
						<label class="popUpLbl" for="selectList">Select List w/ Default</label>
						<select>
							<option>Choose an option</option>
							<option>Option 1</option>
							<option>option 2</option>
						</select>
					</div>
						
					<div class="clearfix"></div>


					<div class="input_wrap">
						<label class="popUpLbl" for="selectList">Select List no default</label>
						<select>
							<option></option>
							<option>Option 1</option>
							<option>option 2</option>
						</select>
					</div>
						
					<div class="clearfix"></div>



					<div class="input_wrap checkboxSingle">
						<input type="checkbox" name="spam">
						<label for="spam">Do you like spam?</label>
					</div>

					<div class="clearfix"></div>


					<fieldset class="checkboxGroup">
						<legend>Select Pizza toppings</legend>
					
						<div class="input_wrap">								
							<input type="Checkbox" class="noUniform" name="peppers" />
							<label for="peppers">Peppers</label>
						</div>
						
						<div class="input_wrap">
							<input type="Checkbox" class="noUniform" name="Mushrooms" />
							<label for="Mushrooms">Mushrooms</label>
						</div>
						
						<div class="input_wrap">
							<input type="Checkbox" class="noUniform" name="Onion" />
							<label for="Onion">Onion</label>
						</div>
					</fieldset>

					<div class="clearfix"></div>


               <div class="spinner fl">
               	<label for="flatForm_qty">Quantity</label>
						<a href="#" class="down button-Med btnSecondary" title="Decrease Quantity">
							<span class="icon-fallback-text">
								<span class="icon-minus" aria-hidden="true"></span>
								<span class="icon-text">Decrease Quantity</span>
							</span>
						</a>
						<span class="incDec visuallyhidden" aria-live="assertive" aria-atomic="true"></span>
						<input name="qty" id="flatForm_qty" type="text" value="1" title="Enter Quantity" class="input_tiny_giftView fl itemQuantity" data-change-store-submit="qty" data-change-store-errors="required digits nonZero" role="textbox" aria-required="false" maxlength="2">
						<a href="#" class="up button-Med btnSecondary" title="Increase Quantity">
							<span class="icon-fallback-text">
								<span class="icon-plus" aria-hidden="true"></span>
								<span class="icon-text">Increase Quantity</span>
							</span>
						</a>
					</div>	
					
               <div class="clear"></div>

					<dsp:input 	iclass="button-Large btnPrimary" 
									bean="WalletFormHandler.createWallet" 
									type="submit" 
									value="Submit">
						<dsp:tagAttribute name="aria-pressed" value="false"/>
					</dsp:input>

				</dsp:form>

				<div class="clear"></div>
				<div class="clear"></div>

				<dsp:form  action="#" method="post" iclass="flatform">

					<h3>Prepopulated form fields</h3>

					
					<div class="input_wrap">
						<label class="popUpLbl" for="email">Email Address</label>
						<input  name="email" class="grid_3 alpha omega" type="email" value="bp@bedbath.com" />
					</div>
					
					<div class="clearfix"></div>

					<div class="input_wrap">
						<label class="popUpLbl" for="tel">Telephone</label>
						<input name="tel" class="grid_3 alpha omega" type="tel"  value="(123) 234-6789"/>
					</div>

					<div class="clearfix"></div>

					<div class="input_wrap">
						<label class="popUpLbl" for="firstname">First Name</label>
						<input name="firstname" class="grid_3 alpha omega" type="text"  value="Bruce" />
					</div>

					<div class="input_wrap">
						<label class="popUpLbl" for="lastname">Last Name</label>
						<input name="lastname" class="grid_3 alpha omega" type="text"  value="Lee" />
					</div>

					<div class="clearfix"></div>

					<div class="input_wrap">
						<label class="popUpLbl" for="password">Password</label>
						<input id="flatForm_password"  name="password" class="grid_3 alpha omega" type="password"  value="asdf"/>
					</div>

					<div class="clearfix"></div>

					<div class="input_wrap">
						<label class="popUpLbl" for="date">Date</label>
						<input  name="date" class="hasDatepicker  grid_3 alpha omega" type="text"  value="12/05/2014" />
					</div>

					<div class="clearfix"></div>

					<div class="input_wrap">
						<label class="popUpLbl" for="date">Text area label</label>
						<textarea name="textarea" class="grid_3 alpha omega">This is some prepopulated text</textarea>
						
					</div>

					<div class="clearfix"></div>


					<fieldset class="radioList">
						<legend>Your favorite color</legend>
					
						<div class="input_wrap">							
							<input type="radio" name="favColor"  />
							<label for="green">Green</label>
						</div>
						
						<div class="input_wrap">
							<input type="radio" name="favColor" checked="checked" />
							<label for="yellow">Yellow</label>
						</div>
						
						<div class="input_wrap">
							<input type="radio" name="favColor" />
							<label for="red">Red</label>
						</div>
					</fieldset>

					<div class="clearfix"></div>

					<div class="input_wrap">
						<label class="popUpLbl" for="selectList">Select List</label>
						<select>
							<option selected="selected">Option 1</option>
							<option>option 2</option>
						</select>
					</div>
					
					<div class="clearfix"></div>

					<div class="input_wrap checkboxSingle">
						<input type="checkbox" name="spam" checked="checked">
						<label for="spam"></label>
					</div>

					<div class="clearfix"></div>


					<fieldset class="checkboxGroup">
						<legend>Select Pizza toppings</legend>
					
						<div class="input_wrap">
							<input type="Checkbox" name="peppers" checked="checked" />
							<label for="peppers">Peppers</label>
						</div>
						
						<div class="input_wrap">
							<input type="Checkbox" name="Mushrooms" />
							<label for="Mushrooms">Mushrooms</label>
						</div>
						
						<div class="input_wrap">
							<input type="Checkbox" name="Onion" checked="checked" />
							<label for="Onion">Onion</label>
						</div>
					</fieldset>

					<div class="clearfix"></div>


               <div class="spinner fl">
               	<label for="flatForm_qty">Quantity</label>
						<a href="#" class="down button-Med btnSecondary" title="Decrease Quantity">
							<span class="icon-fallback-text">
								<span class="icon-minus" aria-hidden="true"></span>
								<span class="icon-text">Decrease Quantity</span>
							</span>
						</a>
						<span class="incDec visuallyhidden" aria-live="assertive" aria-atomic="true"></span>
						<input name="qty" id="flatForm_qty" type="text" value="7" title="Enter Quantity" class="input_tiny_giftView fl itemQuantity" data-change-store-submit="qty" data-change-store-errors="required digits nonZero" role="textbox" aria-required="false" maxlength="2">
						<a href="#" class="up button-Med btnSecondary" title="Increase Quantity">
							<span class="icon-fallback-text">
								<span class="icon-plus" aria-hidden="true"></span>
								<span class="icon-text">Increase Quantity</span>
							</span>
						</a>
					</div>	
					

               <div class="clear"></div>


					<dsp:input 	iclass="button-Large btnPrimary" 
									bean="WalletFormHandler.createWallet" 
									type="submit" 
									value="Submit">
						<dsp:tagAttribute name="aria-pressed" value="false"/>
					</dsp:input>

				</dsp:form>
			</div>

			<div class="grid_12">

				<h1>Tabs</h1>


				<div class="styleGuideTabs alpha omega grid_12 clearfix">
					
			      
					<ul class="categoryProductTabsLinks noprint">
						<li><a href="#myItems" >My Items</a></li>
						<li><a href="#kickStarters">Kickstarters</a></li>
						<li><a href="#thirdTab" >Very Long Tab Name</a></li>
						
					</ul>
					<div id="myItems">
						THis is my items content
					</div>
					<div id="kickStarters">
						This is kickstarters content
					</div>
					<div id="thirdTab">
						THis is third tab content.
					</div>



			</div>

		</div>


		<script type="text/javascript">

			window.onload = function() {
				BBB.fn.makeTabs($('.styleGuideTabs'));			  	
			};

			//$(document).ready(function() {
			//	BBB.fn.makeTabs($('#styleGuideTabs'));
			//});

		</script>

	</bbb:pageContainer>
</dsp:page>