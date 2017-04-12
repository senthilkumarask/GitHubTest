
<dsp:page>
<dsp:importbean bean="atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="atg/userprofiling/Profile"/>
<bbb:pageContainer index="false" follow="false">
<dsp:form method="post" action="add_item_to_wishlist.jsp">
<table border='1'>
<tr><td>
<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
	<dsp:oparam name="output">
		<dsp:valueof param="message"/>
	</dsp:oparam>
</dsp:droplet>


</td></tr>
<tr><td>
<ul>
  	<li>
		<label id="lblwishlistLogin" for="wishlistLogin">User<span>*</span></label>
		<dsp:input bean="ProfileFormHandler.value.login" type="text"  value="test">
            <dsp:tagAttribute name="aria-required" value="true" id="wishlistLogin"/>
            <dsp:tagAttribute name="aria-labelledby" value="lblwishlistLogin errorwishlistLogin"/>
        </dsp:input>
	</li>
	<li>
        <div class="formRow marBottom_5">
            <input name="showPassword" type="checkbox" value="" data-toggle-class="showpassWishlistLogin" class="showPassword" id="showPassword" />
            <label for="showPassword" class="textDgray11 bold"><bbbl:label key="lbl_show_password" language="${pageContext.request.locale.language}"/></label>
        </div>
		<label>Password<span>*</span></label>
		<dsp:input bean="ProfileFormHandler.value.password" type="password" autocomplete="off" iclass="showpassWishlistLogin">
            <dsp:tagAttribute name="aria-required" value="true"/>
        </dsp:input>
	</li>
	
	<br/>
		<dsp:input bean="ProfileFormHandler.wishListlogin" type="submit" value="submit">
            <dsp:tagAttribute name="aria-pressed" value="false"/>
            <dsp:tagAttribute name="role" value="button"/>
        </dsp:input>
</ul>
</td></tr></table>
</dsp:form>
</bbb:pageContainer>
</dsp:page>