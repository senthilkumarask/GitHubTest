<dsp:page>
    <dsp:getvalueof var="qty" param="qty" />
    <dsp:getvalueof var="flag" param="flag" />
    <dsp:getvalueof var="prodList" param="prodList" />
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>

    <c:choose>
        <c:when test="${flag==true}">
        	<c:set var="itemAdded">
        		<bbbl:label key="lbl_item_added_to_list" language="${language}"/>
        	</c:set>
            <div title="${qty} ${itemAdded}">
                <div class="fl noMarBot clearfix">
                    <div class="fl button button_active">
                        <input class="close-any-dialog" type="button" name="CLOSE" value="CLOSE" role="button" aria-pressed="false" />
                    </div>
                    <div class="fl bold marTop_5 marLeft_10">
                        <a href="${contextPath}/wishlist/wish_list.jsp" class="ctaLink"><bbbl:label key="lbl_overview_saved_items_manage" language ="${pageContext.request.locale.language}"/></a>
                        <div class="clear"></div>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
                <script type="text/javascript">
                    if (typeof resx === 'object') {

                        var prodList = "${prodList}";
                        resx.event = "wishlist";
                        resx.itemid = prodList.replace(/[\[\s]/g,'').replace(/[,\]]/g,';');
                    }
                    if (typeof certonaResx === 'object') { certonaResx.run();  }
                </script>
            </div>
        </c:when>
        <c:otherwise>
            <div title="Error occured while adding item(s) to Saved Items">
                <div class="fl noMarBot clearfix">
                    <div class="fl button button_active">
                        <input class="close-any-dialog" type="button" name="CLOSE" value="CLOSE" role="button" aria-pressed="false" />
                    </div>
                    <div class="fl bold marTop_5 marLeft_10">
                        <a href="${contextPath}/wishlist/wish_list.jsp" class="ctaLink"><bbbl:label key="lbl_overview_saved_items_manage" language ="${pageContext.request.locale.language}"/></a>
                        <div class="clear"></div>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
            </div>
        </c:otherwise>
    </c:choose>
</dsp:page>


