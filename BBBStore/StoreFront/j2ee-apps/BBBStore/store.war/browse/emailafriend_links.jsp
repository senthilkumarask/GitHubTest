<%--
  Default welcome page
 --%> 

<dsp:page>
<bbb:pageContainer>

    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <br>
    <p>
    <bbbl:label key='lbl_email_friend_welcome' language="${pageContext.request.locale.language}" />
    
 	   	<div id="modalDialogs" class="clear"></div>
 	   	
    	<BR/>
    	<form>
  <bbbl:label key="lbl_prod_id" language="${pageContext.request.locale.language}" /> <input type="text" value="" id="productId" aria-required="true" aria-labelledby="productId" /><br/>
  CategoryId <input type="text" value="" id="catId" aria-required="true" aria-labelledby="catId" /><br/>
  
  </form>
  <br/>
    	 <dsp:a href="#" onclick="javascript:showModalEmailFriend('${contextPath}/browse/emailAFriend.jsp', 'Email A Friend')"><bbbl:label key='lbl_email_friend_email' language="${pageContext.request.locale.language}" /></dsp:a><br>
</bbb:pageContainer>
    				<script type="text/javascript">

						function doAjaxEmailFriend(options) {
				jQuery.ajax({
					url: options.url,
					success: function(data) {
						jQuery(options.dialogName).html("");
						jQuery(options.dialogName).append(data);
						jQuery(options.dialogName).removeClass("clear");
						jQuery(options.dialogName).dialog({
							autoOpen: true,
							resizable: false,
							width: 400,
							modal: true,
							title: options.title,
							beforeClose: function(event, ui) {
								jQuery(options.dialogName).addClass("clear");
							}
						});
						eval($(data).text());
					}
				});
			}
			
						function showModalEmailFriend(url, title) {
							var options = {};
							options.dialogName = '#modalDialogs';
					
							url = url + '?productId='+document.getElementById("productId").value+'&categoryId='+document.getElementById("catId").value;
							options.url = url;
							options.title = title;
							doAjaxEmailFriend(options);
						}
		</script>
  
  
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/Storefront/j2ee/store.war/index.jsp#3 $$Change: 635969 $ --%>

