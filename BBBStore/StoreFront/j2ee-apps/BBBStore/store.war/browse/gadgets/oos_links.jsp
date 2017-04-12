<dsp:page>
<bbb:pageContainer>

    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <br>
    <p>
    <bbbl:label key="lbl_email_friend_welcome" language="${pageContext.request.locale.language}" />
    
 	   	<div id="modalDialogs" class="clear"></div>
 	   	
    	<BR/>
    	<form>
  <bbbl:label key="lbl_sku_id" language="${pageContext.request.locale.language}" /> <input type="text" value="" id="skuId" aria-required="true" /><br/>
  <bbbl:label key="lbl_prod_id" language="${pageContext.request.locale.language}" /> <input type="text" value="" id="productId" aria-required="true" /><br/>
  
  </form>
  <br/>
    	 <dsp:a href="#" onclick="javascript:showModalOOS('${contextPath}/browse/gadgets/notifyMeRequest.jsp', 'EMAIL ME WHEN ITEM IS IN STOCK')"><bbbl:label key="lbl_oos_email" language="${pageContext.request.locale.language}" /></dsp:a><br>
</bbb:pageContainer>
    				<script type="text/javascript">

						function doAjaxOOS(options) {
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
			
						function showModalOOS(url, title) {
							var options = {};
							options.dialogName = '#modalDialogs';
					
							url = url + '?skuId='+document.getElementById("skuId").value+'&productId='+document.getElementById("productId").value;
							options.url = url;
							options.title = title;
							doAjaxOOS(options);
						}
		</script>
  
  
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/Storefront/j2ee/store.war/index.jsp#3 $$Change: 635969 $ --%>

