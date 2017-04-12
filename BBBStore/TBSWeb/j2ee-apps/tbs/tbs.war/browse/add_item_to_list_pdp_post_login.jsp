<dsp:page>
    <dsp:getvalueof var="qty" param="qty" />
    <dsp:getvalueof var="flag" param="flag" />
    <dsp:getvalueof var="prodList" param="prodList" />
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
   <div class="row">
           	<c:set var="itemAdded">
        		<bbbl:label key="lbl_item_added_to_list" language="${language}"/>
        	</c:set>
        <c:choose>
             <c:when test="${flag==true}">
                <div title="${qty} ${itemAdded}">
		               <label>
		                    ${qty} ${itemAdded}
		                </label><br/><br/>
                </div>
	                <script type="text/javascript">
	                    if (typeof resx === 'object') {
	
	                        var prodList = "${prodList}";
	                        resx.event = "wishlist";
	                        resx.itemid = prodList.replace(/[\[\s]/g,'').replace(/[,\]]/g,';');
	                    }
	                    if (typeof certonaResx === 'object') { certonaResx.run();  }
	                </script>
              </c:when>
                <c:otherwise>
			           <div title="Error occured while adding item(s) to Saved Items">
			            <label class="error">
			               Error occured while adding item(s) to Saved Items
			            </label>
					</div>
				</c:otherwise>
		</c:choose>	
                    <div class="fl small-6 columns button_active">
                       <input class="small button service expand close-modal" type="submit" name="CLOSE" value="CLOSE" role="button" aria-pressed="false" >
                    </div>
                    <div class="fl small-6 columns bold">
                        <a href="${contextPath}/wishlist/wish_list.jsp" class="small button secondary column"><bbbl:label key="lbl_overview_saved_items_manage" language ="${pageContext.request.locale.language}"/></a>
                     </div>
                   
                </div>
    
</dsp:page>


