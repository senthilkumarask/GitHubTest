<dsp:page>
<dsp:importbean
        bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
<dsp:form method="post" action="index.jsp" id="frmRegistryProduct">
    <%-- <dsp:input bean="GiftRegistryFormHandler.successURL"  type="hidden" value="${guestUrl}" />
    <dsp:input bean="GiftRegistryFormHandler.errorURL"  type="hidden" value="${guestUrl}" /> --%>
    <dsp:input bean="EmailAFriendFormHandler.queryParam"  type="hidden" value="?registryId=${registryId}&eventType=${eventType}&pwsurl=${pwsurl}" />
    <dsp:input bean="EmailAFriendFormHandler.fromPage" type="hidden" value="registrySortControls" />
    <dsp:getvalueof var="sortSeq" param="sortSeq"/>
    <dsp:getvalueof var="view" param="view"/>
    <div class="row sorting giftViewSortingControls registrySorting">
        <div class="small-12 large-6 columns left no-padding">
            <bbbl:label key='lbl_mng_regitem_sortby' language="${pageContext.request.locale.language}" />:
            <c:if test="${(empty sortSeq) || sortSeq ==1}">
                <label id="lblsortSeqCat" for="sortSeqCat" class="tiny button">
                <dsp:input bean="GiftRegistryFormHandler.registrySearchVO.sortSeq" id="sortSeqCat" name="sorting" type="radio"  style="display:none"
                    checked="true" value="1">
                    <dsp:tagAttribute name="aria-checked" value="true"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lblsortSeqCat"/>
                </dsp:input>
                <bbbl:label key='lbl_mng_regitem_sortcat' language="${pageContext.request.locale.language}" /></label>
            
                <label id="lblsortSeqPrice" for="sortSeqPrice" class="tiny button notActive">
                <dsp:input bean="GiftRegistryFormHandler.registrySearchVO.sortSeq" id="sortSeqPrice" name="sorting" type="radio" style="display:none"
                    value="2">
                    <dsp:tagAttribute name="aria-checked" value="true"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lblsortSeqPrice"/>
                </dsp:input>
                <bbbl:label key='lbl_mng_regitem_sortprice' language="${pageContext.request.locale.language}" /></label>
            
            </c:if>
            <c:if test="${(not empty sortSeq) && sortSeq ==2}">
                <label id="lblsortSeqCat" for="sortSeqCat" class="tiny button notActive"> 
                <dsp:input bean="GiftRegistryFormHandler.registrySearchVO.sortSeq" id="sortSeqCat" name="sorting" type="radio" style="display:none"
                    value="1">
                    <dsp:tagAttribute name="aria-checked" value="true"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lblsortSeqCat"/>
                </dsp:input>
                <bbbl:label key='lbl_mng_regitem_sortcat' language="${pageContext.request.locale.language}" /></label>
             
                <label id="lblsortSeqPrice" for="sortSeqPrice" class="tiny button">
                <dsp:input bean="GiftRegistryFormHandler.registrySearchVO.sortSeq" id="sortSeqPrice" name="sorting" type="radio" style="display:none"
                    checked="true" value="2">
                    <dsp:tagAttribute name="aria-checked" value="true"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lblsortSeqPrice"/>
                </dsp:input>
                <bbbl:label key='lbl_mng_regitem_sortprice' language="${pageContext.request.locale.language}" /></label>
            
            </c:if>
        </div>
        <div class="small-12 large-6 columns right no-padding">
                <bbbl:label key='lbl_mng_regitem_view' language="${pageContext.request.locale.language}" />:
                <c:if test="${(empty view) || view ==1}">
                    <label id="lblall" for="all" class="tiny button">
                    <dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="all" name="view" type="radio"  style="display:none"
                        checked="true" value="1">
                        <dsp:tagAttribute name="aria-checked" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblall"/>
                    </dsp:input>
                    <bbbl:label key='lbl_mng_regitem_all' language="${pageContext.request.locale.language}" /></label>
                
                    <label id="lblpurchased" for="purchased" class="tiny button notActive">
                    <dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="purchased" name="view" type="radio" style="display:none"
                        value="3">
                        <dsp:tagAttribute name="aria-checked" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblpurchased"/>
                    </dsp:input>
                    <bbbl:label key='lbl_mng_regitem_purchased_sort' language="${pageContext.request.locale.language}" /></label>
                
                    <label id="lblRemaining" for="Remaining" class="tiny button notActive">
                    <dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="Remaining" name="view" type="radio" style="display:none"
                        value="2">
                        <dsp:tagAttribute name="aria-checked" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblRemaining"/>
                    </dsp:input>
                    <bbbl:label key='lbl_mng_regitem_remaining' language="${pageContext.request.locale.language}" /></label>
                
                </c:if>
                <c:if test="${(not empty view) && view ==3}">
                    <label id="lblall" for="all" class="tiny button notActive">
                    <dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="all" name="view" type="radio" style="display:none" 
                        value="1">
                        <dsp:tagAttribute name="aria-checked" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblall"/>
                    </dsp:input>
                    <bbbl:label key='lbl_mng_regitem_all' language="${pageContext.request.locale.language}" /></label>
                
                    <label id="lblpurchased" for="purchased" class="tiny button">
                    <dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="purchased" name="view" type="radio" style="display:none"
                        checked="true" value="3">
                        <dsp:tagAttribute name="aria-checked" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblpurchased"/>
                    </dsp:input>
                    <bbbl:label key='lbl_mng_regitem_purchased_sort' language="${pageContext.request.locale.language}" /></label>
                
                    <label id="lblRemaining" for="Remaining" class="tiny button notActive">
                    <dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="Remaining" name="view" type="radio" style="display:none"
                        value="2">
                        <dsp:tagAttribute name="aria-checked" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblRemaining"/>
                    </dsp:input>
                    <bbbl:label key='lbl_mng_regitem_remaining' language="${pageContext.request.locale.language}" /></label>
                
                </c:if>
                <c:if test="${(not empty view) && view ==2}">
                    <label id="lblall" for="all" class="tiny button notActive">
                    <dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="all" name="view" type="radio"  style="display:none"
                        value="1">
                        <dsp:tagAttribute name="aria-checked" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblall"/>
                    </dsp:input>
                    <bbbl:label key='lbl_mng_regitem_all' language="${pageContext.request.locale.language}" /></label>
                
                    <label id="lblpurchased" for="purchased" class="tiny button notActive">
                    <dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="purchased" name="view" type="radio" style="display:none"
                        value="3">
                        <dsp:tagAttribute name="aria-checked" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblpurchased"/>
                    </dsp:input>
                    <bbbl:label key='lbl_mng_regitem_purchased_sort' language="${pageContext.request.locale.language}" /></label>
                
                    <label id="lblpurchased" for="Remaining" class="tiny button">
                    <dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="Remaining" name="view" type="radio" style="display:none"
                        checked="true" value="2">
                        <dsp:tagAttribute name="aria-checked" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblpurchased"/>
                    </dsp:input>
                    <bbbl:label key='lbl_mng_regitem_remaining' language="${pageContext.request.locale.language}" /></label>
                
                </c:if>
        </div>
    </div>
</dsp:form>
</dsp:page>