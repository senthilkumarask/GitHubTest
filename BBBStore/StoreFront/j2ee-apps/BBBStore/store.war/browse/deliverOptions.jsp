<%@ taglib uri="/WEB-INF/tld/BBBLblTxt.tld" prefix="bbb"%>
<div id="truckDeliveryOptions" title="Truck Delivery Options">
    <p class="marBottom_20">Aenean eu leo quam. Pellentesque ornare sem lacinia quam venenatis vestibulum. Donec sed odio dui. Duis mollis, est non
	commodo luctus, nisi erat porttitor ligula, eget lacinia odio sem nec elit.</p>
	<p class="italic"><strong>Not all delivery options are available for each item. Available options and charges will be shown at the item level.</strong></p>
	<%--<img src="/_assets/global/images/LTL/deliveryOptionTable.png" width="700" class="marLeft_10 marTop_20"/>--%>
	<table id="deliveryOptionTable">
		<thead>
			<tr>
				<td class="textLeft bold"><bbbl:label key="lbl_service_levels" language="${pageContext.request.locale.language}" /></td>
				<td class="textCenter bold"><bbbl:label key="lbl_curbside" language="${pageContext.request.locale.language}" /></td>
				<td class="textCenter bold"><bbbl:label key="lbl_threshold" language="${pageContext.request.locale.language}" /></td>
				<td class="textCenter bold"><bbbl:label key="lbl_room_of_choice" language="${pageContext.request.locale.language}" /></td>
				<td class="textCenter bold"><bbbl:label key="lbl_white_glove" language="${pageContext.request.locale.language}" /></td>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td class="textLeft bold"><bbbl:label key="lbl_flexible_delivery_time" language="${pageContext.request.locale.language}" /></td>
				<td class="textCenter"><img src="/_assets/global/images/LTL/tick.png" width="15" class=""/></td>
				<td class="textCenter"><img src="/_assets/global/images/LTL/tick.png" width="15" class=""/></td>
				<td class="textCenter"><img src="/_assets/global/images/LTL/tick.png" width="15" class=""/></td>
				<td class="textCenter"><img src="/_assets/global/images/LTL/tick.png" width="15" class=""/></td>
			</tr>
			<tr>
				<td class="textLeft bold"><bbbl:label key="lbl_unloading_service" language="${pageContext.request.locale.language}" /></td>
				<td class="textCenter"><img src="/_assets/global/images/LTL/tick.png" width="15" class=""/></td>
				<td class="textCenter"><img src="/_assets/global/images/LTL/tick.png" width="15" class=""/></td>
				<td class="textCenter"><img src="/_assets/global/images/LTL/tick.png" width="15" class=""/></td>
				<td class="textCenter"><img src="/_assets/global/images/LTL/tick.png" width="15" class=""/></td>
			</tr>
			<tr>
				<td class="textLeft bold"><bbbl:label key="lbl_bring_item_inside" language="${pageContext.request.locale.language}" /></td>
				<td></td>
				<td class="textCenter"><img src="/_assets/global/images/LTL/tick.png" width="15" class=""/></td>
				<td class="textCenter"><img src="/_assets/global/images/LTL/tick.png" width="15" class=""/></td>
				<td class="textCenter"><img src="/_assets/global/images/LTL/tick.png" width="15" class=""/></td>
			</tr>
			<tr>
				<td class="textLeft bold"><bbbl:label key="lbl_set_item_desired_room" language="${pageContext.request.locale.language}" /></td>
				<td></td>
				<td></td>
				<td class="textCenter"><img src="/_assets/global/images/LTL/tick.png" width="15" class=""/></td>
				<td class="textCenter"><img src="/_assets/global/images/LTL/tick.png" width="15" class=""/></td>
			</tr>
			<tr>
				<td class="textLeft bold"><bbbl:label key="lbl_unboxing" language="${pageContext.request.locale.language}" /></td>
				<td></td>
				<td></td>
				<td></td>
				<td class="textCenter"><img src="/_assets/global/images/LTL/tick.png" width="15" class=""/></td>
			</tr>
			<tr>
				<td class="textLeft bold"><bbbl:label key="lbl_packaging_removal" language="${pageContext.request.locale.language}" /></td>
				<td></td>
				<td></td>
				<td></td>
				<td class="textCenter"><img src="/_assets/global/images/LTL/tick.png" width="15" class=""/></td>
			</tr>
			<tr>
				<td class="textLeft bold"><bbbl:label key="lbl_assembly_optional" language="${pageContext.request.locale.language}" /></td>
				<td></td>
				<td></td>
				<td></td>
				<td class="textCenter"><img src="/_assets/global/images/LTL/tick.png" width="15" class=""/></td>
			</tr>
		</tbody>
	</table>
	<div class="productLinks clearfix marTop_30"> 
		<div class="button button_active button_light_blue"> 
			<a href="#" data-smoothscroll-topoffset="65" class="lnkCollectionItems smoothScrollTo close-any-dialog" onclick=""><bbbl:label key="lbl_pdp_oos_confirm_close_button" language="${pageContext.request.locale.language}" /></a> 
		</div>
	</div>
</div>