<dsp:page>
<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
<dsp:form method="post" action="index.jsp" id="frmRegistryProduct">
	<dsp:input bean="GiftRegistryFormHandler.successURL"  type="hidden" value="${guestUrl}" />
	<dsp:input bean="GiftRegistryFormHandler.errorURL"	type="hidden" value="${guestUrl}" />
	<dsp:getvalueof var="sortSeq" param="sortSeq"/>
	<dsp:getvalueof var="view" param="view"/>
	<div class="clearfix space sorting giftViewSortingControls padBottom_10">
		<ul class="grid_5 suffix_3">
			<a name="backToTop"></a>
			<li class="bold"><bbbl:label key='lbl_mng_mxregitem_sortby' language="${pageContext.request.locale.language}" /></li>
			<c:if test="${(empty sortSeq) || sortSeq ==1}">
			<li>
				<dsp:input bean="GiftRegistryFormHandler.registrySearchVO.sortSeq" id="sortSeqCat" name="sorting" type="radio"
					checked="true" value="1">
                    <dsp:tagAttribute name="aria-checked" value="true"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lblsortSeqCat"/>
				</dsp:input>
				<label id="lblsortSeqCat" for="sortSeqCat"><bbbl:label key='lbl_mng_mxregitem_sortcat' language="${pageContext.request.locale.language}" /></label>
			</li>
			<li>
				<dsp:input bean="GiftRegistryFormHandler.registrySearchVO.sortSeq" id="sortSeqPrice" name="sorting" type="radio"
					value="2">
                    <dsp:tagAttribute name="aria-checked" value="true"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lblsortSeqPrice"/>
				</dsp:input>
				<label id="lblsortSeqPrice" for="sortSeqPrice"><bbbl:label key='lbl_mng_mxregitem_sortprice' language="${pageContext.request.locale.language}" /></label>
			</li>
			</c:if>
			<c:if test="${(not empty sortSeq) && sortSeq ==2}">
			<li>
				<dsp:input bean="GiftRegistryFormHandler.registrySearchVO.sortSeq" id="sortSeqCat" name="sorting" type="radio"
					value="1">
                    <dsp:tagAttribute name="aria-checked" value="true"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lblsortSeqCat"/>
				</dsp:input>
				<label id="lblsortSeqCat" for="sortSeqCat"><bbbl:label key='lbl_mng_mxregitem_sortcat' language="${pageContext.request.locale.language}" /></label>
			</li>
			<li>
				<dsp:input bean="GiftRegistryFormHandler.registrySearchVO.sortSeq" id="sortSeqPrice" name="sorting" type="radio"
					checked="true" value="2">
                    <dsp:tagAttribute name="aria-checked" value="true"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lblsortSeqPrice"/>
				</dsp:input>
				<label id="lblsortSeqPrice" for="sortSeqPrice"><bbbl:label key='lbl_mng_mxregitem_sortprice' language="${pageContext.request.locale.language}" /></label>
			</li>
			</c:if>
		</ul>
		<div class="grid_5 fr right omega">
			<ul>
				<li class="bold"><bbbl:label key='lbl_mng_mxregitem_view' language="${pageContext.request.locale.language}" /></li>
				<c:if test="${(empty view) || view ==1}">
				<li>
					<dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="all" name="view" type="radio" 
						checked="true" value="1">
                        <dsp:tagAttribute name="aria-checked" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblall"/>
                    </dsp:input>
					<label id="lblall" for="all"><bbbl:label key='lbl_mng_mxregitem_all' language="${pageContext.request.locale.language}" /></label>
				</li>
				<li>
					<dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="purchased" name="view" type="radio"
						value="3">
                        <dsp:tagAttribute name="aria-checked" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblpurchased"/>
                    </dsp:input>
					<label id="lblpurchased" for="purchased"><bbbl:label key='lbl_mng_mxregitem_purchased_sort' language="${pageContext.request.locale.language}" /></label>
				</li>
				<li>
					<dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="Remaining" name="view" type="radio"
						value="2">
                        <dsp:tagAttribute name="aria-checked" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblRemaining"/>
                    </dsp:input>
					<label id="lblRemaining" for="Remaining"><bbbl:label key='lbl_mng_mxregitem_remaining' language="${pageContext.request.locale.language}" /></label>
				</li>
				</c:if>
				<c:if test="${(not empty view) && view ==3}">
				<li>
					<dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="all" name="view" type="radio" 
						value="1">
                        <dsp:tagAttribute name="aria-checked" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblall"/>
                    </dsp:input>
					<label id="lblall" for="all"><bbbl:label key='lbl_mng_mxregitem_all' language="${pageContext.request.locale.language}" /></label>
				</li>
				<li>
					<dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="purchased" name="view" type="radio"
						checked="true" value="3">
                        <dsp:tagAttribute name="aria-checked" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblpurchased"/>
                    </dsp:input>
					<label id="lblpurchased" for="purchased"><bbbl:label key='lbl_mng_mxregitem_purchased_sort' language="${pageContext.request.locale.language}" /></label>
				</li>
				<li>
					<dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="Remaining" name="view" type="radio"
						value="2">
                        <dsp:tagAttribute name="aria-checked" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblRemaining"/>
                    </dsp:input>
					<label id="lblRemaining" for="Remaining"><bbbl:label key='lbl_mng_mxregitem_remaining' language="${pageContext.request.locale.language}" /></label>
				</li>
				</c:if>
				<c:if test="${(not empty view) && view ==2}">
				<li>
					<dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="all" name="view" type="radio" 
						value="1">
                        <dsp:tagAttribute name="aria-checked" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblall"/>
                    </dsp:input>
					<label id="lblall" for="all"><bbbl:label key='lbl_mng_mxregitem_all' language="${pageContext.request.locale.language}" /></label>
				</li>
				<li>
					<dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="purchased" name="view" type="radio"
						value="3">
                        <dsp:tagAttribute name="aria-checked" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblpurchased"/>
                    </dsp:input>
					<label id="lblpurchased" for="purchased"><bbbl:label key='lbl_mng_mxregitem_purchased_sort' language="${pageContext.request.locale.language}" /></label>
				</li>
				<li>
					<dsp:input bean="GiftRegistryFormHandler.registrySearchVO.view" id="Remaining" name="view" type="radio"
						checked="true" value="2">
                        <dsp:tagAttribute name="aria-checked" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblpurchased"/>
                    </dsp:input>
					<label id="lblpurchased" for="Remaining"><bbbl:label key='lbl_mng_mxregitem_remaining' language="${pageContext.request.locale.language}" /></label>
				</li>
				</c:if>
			</ul>
		</div>
	</div>
</dsp:form>
</dsp:page>