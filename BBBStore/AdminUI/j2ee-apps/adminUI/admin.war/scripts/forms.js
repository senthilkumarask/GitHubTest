
function createList(node, option) {

	listForm(node, "insert");
}

function editList(node, option) {

	updateList(node, "update");
}

function cloneList(node, option) {

	updateList(node, "clone");
}

function updateList(node, action) {

	var tree = global.$list_tree.jstree(true);

	var id = node.data.list_id;

	var data = {};
	var postData = {};
	var url;

	postData.list_id = id;

	url = global.config.api.BBB_LIST_getList;

	$.ajax({
		url:url,
		type: 'POST',
		data:postData,
		dataType: 'json',
		success: function (result) {

			if (serverError(result, true)) {

				return;
			}

			if (!result.data || !result.data[0]) {

				showMessageBox("no data returned", "Error");

				return;
			}

			listForm(node, action, result.data[0]);

          	},
                error: function (xhr, status, error) {

			showAjaxError(xhr, status, error);

               	},
            	async: true
	});

}

function deleteList(node, option) {

	deleteNode(node);
}

function deleteNode(node) {

	var tree = global.$list_tree.jstree(true);

	var url = null;
	var postData = {};
	var title = "Remove";
	var confirm = true;
	var pnode = [];
	var xnode = null;
	var xtype = "";

	switch(node.type) {
	case "list":
	case "xlist":
		title = "Delete";
		url = global.config.api.BBB_LIST_removeList;
		postData.list_id = node.data.list_id;
		xtype = "list";
		break;
	case "category":
	case "xcategory":
	case "dcategory":
	case "dxcategory":
		if (node.data.xname == "bbb_list_chldcat") {
			postData.list_id = node.data.list_id;
			postData.list_cat_id = node.data.list_cat_id;
			url = global.config.api.BBB_LIST_CHLDCAT_removeCategory;
		}
		else {
			postData.list_cat_id = node.data.list_cat_id;
			postData.child_list_cat_id = node.data.child_list_cat_id;
			url = global.config.api.BBB_LIST_CAT_CHLDCAT_removeCategory;
		}
		xtype = "category";
		break;
	case "sku":
	case "xsku":
		url = global.config.api.BBB_LIST_RULES_SKU_CAT_delete;
		postData.rule_id = node.data.rule_id;
		postData.list_cat_id = node.data.list_cat_id;
		postData.sku_id = node.data.sku_id;
		xtype = "SKU";
		break;
	case "eph_node":
		url = global.config.api.BBB_LIST_RULES_EPH_CAT_delete;
		postData.rule_id = node.data.rule_id;
		postData.list_cat_id = node.data.list_cat_id;
		xtype = "EPH";
		break;
	case "jda_category":
		url = global.config.api.BBB_LIST_RULES_JDA_CAT_delete;
		postData.rule_id = node.data.rule_id;
		postData.list_cat_id = node.data.list_cat_id;	
		xtype = "JDA";
		break;
	case "rule":
	case "eph_rule":
	case "jda_rule":
		pnode.push(tree.get_node(node.parents[0]));
		pnode.push(tree.get_node(node.parents[1]));
		postData.list_cat_id = node.data.list_cat_id;
		if (node.type == "eph_rule") {
			url = global.config.api.BBB_LIST_RULES_EPH_CAT_update;
			postData.rule_id = pnode[0].data.rule_id || "";
			postData.eph_node_id = pnode[0].data.eph_node_id || "";
			xnode = tree.get_node(node.parents[1]);
		}
		else if (node.type == "jda_rule") {
			url = global.config.api.BBB_LIST_RULES_JDA_CAT_update;
			postData.rule_id = pnode[0].data.rule_id || "";
			postData.jda_dept_id = pnode[0].data.jda_dept_id || "";
			postData.jda_sub_dept_id = pnode[0].data.jda_sub_dept_id || "";
			postData.jda_class = pnode[0].data.jda_class || "";
			xnode = tree.get_node(node.parents[1]);
		}
		else {
			url = global.config.api.BBB_LIST_RULES_FACET_CAT_delete;
			postData.rule_id = node.data.rule_id || "";
		//	xnode = tree.get_node(node.parent);
		//	xnode.data.load_empty = true;
		}
		pnode[0].data.facet_rule_name = "";
		pnode[0].data.facet_value_pair_list = "";
		xtype = "rule";
		break;
	case "facet":
	case "eph_facet":
	case "jda_facet":
		pnode.push(tree.get_node(node.parents[0]));
		pnode.push(tree.get_node(node.parents[1]));
		postData.list_cat_id = pnode[0].data.list_cat_id;
		postData.rule_id = pnode[0].data.rule_id;
		if (node.type == "eph_facet") {
			url = global.config.api.BBB_LIST_RULES_EPH_CAT_update;
			postData.eph_node_id = pnode[0].data.eph_node_id;
			xnode = tree.get_node(node.parents[2]);
		}
		else if (node.type == "jda_facet") {
			url = global.config.api.BBB_LIST_RULES_JDA_CAT_update;
			postData.jda_dept_id = pnode[0].data.jda_dept_id || "";
			postData.jda_sub_dept_id = pnode[0].data.jda_sub_dept_id || "";
			postData.jda_class = pnode[0].data.jda_class || "";
			xnode = tree.get_node(node.parents[2]);
		}
		else {
			url = global.config.api.BBB_LIST_RULES_FACET_CAT_update;
			xnode = tree.get_node(node.parents[1]);
		}
		postData.facet_rule_name = pnode[0].data.facet_rule_name;
		postData.facet_value_pair_list = removeFacet(pnode[0].data.facet_value_pair_list, node.data.facet_id, node.data.facet_value_id);
		pnode[0].data.facet_value_pair_list = postData.facet_value_pair_list;
		xtype = "facet";
		break;
	default:
		url = null;
	}

	if (!url) {
		return;
	}

	if (confirm) {

	//	$('#confirmDialog-message').text("Are You Sure?");
	//	$('#confirmDialog-message').html("<p>" + title + "&nbsp;" + xtype + "</p><p><b style=\"cursor:hand\">" + node.text + "</b></p><p>Are You Sure?</p>");
		$('#confirmDialog-message').html("<p>" + title + "&nbsp;" + xtype + "</p><ul class=\"" + node.type + "\"><li style=\"cursor:hand\"><b>" + node.text + "</b></li></ul><p>Are You Sure?</p>");

		var dialog_config = {
			modal: true,
  			title:title,
			open: function (event, ui) {
				$(event.target).parent().css('position', (global.config.dialogFixed?'fixed':'absolute'));
			},
			"resize":"auto",
  			buttons: [
    			{
      				text: "Cancel",
				click: function() {
        				$( this ).dialog( "close" );
				}
			},
			{
      				text: "Ok",
				click: function() {
					performAction();
					$( this ).dialog( "close" );
				}
			}]
		};

		dialog_config.position = dialog_fixed_position;

		global.$confirmDialog.dialog(dialog_config).dialog('open');
	}
	else {
		performAction();
	}

	function performAction() {

		$.ajax({
			url:url,
			type: 'POST',
			data: postData,
			dataType: 'json',
                       	success: function (result) {

				if (serverError(result, true)) {

					return;
				}

				if (xnode) {	

					tree.refresh_node(xnode, function() {
						if (xnode.data.load_empty) {
							xnode.data.load_empty = false;
						}
					});
				}
				else {

					tree.delete_node(node);
				}

                    	},
                    	error: function (xhr, status, error) {

				showAjaxError(xhr, status, error);
			},
               	 	async: true
		});
	}
}

function reOrderNode(e, state) {

	var node = state.node;

	var tree = global.$list_tree.jstree(true);

	var url = null;
	var data = {};
	var postData = {};

	var position = state.position;

	if (state.position > state.old_position) {
		var pNode = tree.get_node(node.parent);
		if (pNode && pNode.children && pNode.children.length > 1 && pNode.children.length > state.position-1) {
			var cNode = tree.get_node(pNode.children[state.position-1]);
			if (cNode && cNode.data && cNode.data.sequence_num > position) {
				position = cNode.data.sequence_num;
			}
		}
	}

	switch(node.type) {
	case "list":
	case "xlist":
		url = global.config.api.BBB_LIST_reorderList;
		postData.list_id = node.data.list_id;
		postData.sequence_num = position;
		break;
	case "category":
	case "xcategory":
	case "dcategory":
	case "dxcategory":
		if (node.data.xname == "bbb_list_chldcat") {
			postData.list_id = node.data.list_id;
			postData.list_cat_id = node.data.list_cat_id;
			url = global.config.api.BBB_LIST_CHLDCAT_reorderCategory;
		}
		else {
			postData.list_cat_id = node.data.list_cat_id;
			postData.child_list_cat_id = node.data.child_list_cat_id;
			url = global.config.api.BBB_LIST_CAT_CHLDCAT_reOrder;
		}
		postData.sequence_num = position;
		break;
	case "sku":
	case "xsku":
		url = global.config.api.BBB_LIST_RULES_SKU_CAT_xsequence
		postData.rule_id = node.data.rule_id;
		postData.list_cat_id = node.data.list_cat_id;
		postData.sku_id = node.data.sku_id;
		postData.sequence_num = position;
		break;
	case "eph_node":
		url = global.config.api.BBB_LIST_RULES_EPH_CAT_xsequence;
		postData.rule_id = node.data.rule_id;
		postData.list_cat_id = node.data.list_cat_id;
		postData.sequence_num = position;
		break;
	case "jda_category":
		url = global.config.api.BBB_LIST_RULES_JDA_CAT_xsequence;
		postData.rule_id = node.data.rule_id;
		postData.list_cat_id = node.data.list_cat_id;
		postData.sequence_num = position;
		break;
	case "rule":
		url = global.config.api.BBB_LIST_RULES_FACET_CAT_xsequence;
		postData.rule_id = node.data.rule_id;
		postData.list_cat_id = node.data.list_cat_id;
		postData.sequence_num = position;
		break;
	case "eph_rule":
		break;
	case "jda_rule":
		break;
	default:
		return;
	}

	if (!url) return;

	$.ajax({
		url:url,
		type: 'POST',
		data:postData,
		dataType: 'json',
                success: function (result) {

			serverError(result, true);
		},
        	error: function (xhr, status, error) {

			showAjaxError(xhr, status, error);
		},
        	async: true
	});
}

function listForm(node, action, record) {

	var tree = global.$list_tree.jstree(true);	

	var d = global.config.delim;		

	var keys = getNodeKeys(node.id);
	var url = null;
	var title = "";
	var formName = "listForm";
	var id = 0;
	var $form = null;
	var action = action || "insert";
	var data = {};

	if (action == "insert") {
		title = "Create List";
	}
	else if (action == "update") {
		title = "Edit List";
		id = node.data.list_id;
	}
	else if (action == "clone") {
		title = "Clone List";
		id = node.data.list_id;
	}

	if (!global["$" + formName]) {

		global["$" + formName] = $("#" + formName);

		getJson("listForm_site_flag", global.site);

		var params = {};

		getJson(formName + "_type_name", global.list_type, params, function(data) {

			return $.map(data, function(item) {

				if (params.type_name == item.type_name) return null;
				params.type_name = item.type_name;

				return {"text":item.type_name,"value":item.type_name};
			});

		});

		$("#" + formName + "_type_name").on("change", function() {

			element = this;

			var params = {type_name:element.value};

			getJson(formName + "_sub_type_code", global.list_type, params, function(data) {
				
				return $.map(data, function(item) {

					if (params.type_name != item.type_name) return null;

					return {"text":item.sub_type_name,"value":item.sub_type_code};
				});
			});

		}).trigger("change");

	}
	$form = global["$" + formName];

	var $display_name = $("#" + formName + "_display_name");
	var $display_name_error = $("#" + formName + "_display_name-error");

	var $site_flag = $("#" + formName + "_site_flag");
	var $site_flag_error = $("#" + formName + "_site_flag-error");

	var $type_name = $("#" + formName + "_type_name");
	var $sub_type_code = $("#" + formName + "_sub_type_code");

	var $form_Error = $("#" + formName + "_error");
	var $form_Message = $("#" + formName + "_message");

	$form_Error.text("");
	$form_Message.text("");

	resetForm($form[0]);
	$form[0].reset();

	if (record) {

		record.is_enabled = !record.is_disabled;
		delete record.is_disabled;
		record.allow_duplicates = !!record.allow_duplicates;
		record.site_flag = (record.site_flag||'').toString().split("").sort();

		if (action == "clone") {

			record.display_name += " - Copy";
		}

		deserializeForm($form[0], record, formName + "_");

		$type_name.trigger("change");

		$sub_type_code.val(record.sub_type_code);
	}
	else {

		$site_flag.val(global.state.site_flag);
		$type_name.trigger("change");
	}

	var dialog_config = {
		autoOpen: false,
                modal: true,
                title: title,
                buttons:
                        [{
                            text: "Cancel",
                            click: function () {

                                $(this).dialog("close");
                            }
                        }, 
			{
                            text: "Ok",
			    id: formName + "-ok-button",
                            click: function () {

				var that = this;

				var formData = serializeForm($form[0], formName + "_"); 

				for (var p in formData) {

					if (typeof(formData[p]) == "string") {
						formData[p] = formData[p].trim();
					}
				}

				formData.site_flag = formData.site_flag.sort().join("");
				formData.is_disabled = !formData.is_enabled;
				delete formData.is_enabled;
				formData.allow_duplicates = !!formData.allow_duplicates;

				formData.sequence_num = formData.sequence_num||(global.config.newNodeTop?0:999999);

				resetForm($form[0]);

				if (!formData.display_name) {

					$display_name.focus();

					$display_name_error.text("Please enter a list name");
					return;
				}

			//	if (formData.display_name.replace(/[a-z0-9\-\_\s]/gi, '')) {
				if (!printable(formData.display_name)) {
			
					$display_name.focus();
			
					$display_name_error.text("Invalid characters in list name");
					return;
				}

				if (!formData.site_flag) {

					$site_flag.focus();

					$site_flag_error.text("Please select a site");
					return;
				}

				if (action == "insert" || action == "clone") {

					url = global.config.api.BBB_LIST_createList;
				}
				else if (action == "update") {

					url = global.config.api.BBB_LIST_editList;
					formData.list_id = id;
				}

				if (action == "clone") {
					formData.clone_list_id = node.data.list_id;
				}


				formData.create_user = "AdminUI";
      				formData.last_mod_user= "AdminUI";

				$.ajax({
					url:url,
					type: 'POST',
					data: formData,
					dataType: 'json',
                        		success: function (result) {

						if (serverError(result, false, true)) {

							return;
						}

                            			// server-side validation
						if (!result || result.status == "error") {

							$form_Error.text(getServerMessageText(result));

							return;
						}

						var xnode = node;

						if (action == "clone" || action == "update") {
							xnode = tree.get_node(node.parent);
						}

						tree.refresh_node(xnode, function() {
							tree.open_node(xnode);
						});

						$(that).dialog("close");
                    			},
                    			error: function (xhr, status, error) {

						showAjaxError(xhr, status, error);
                    			},
                    			async: true
                    		});

                            }
                        }],
                open: function (event, ui) {

			$(event.target).parent().css('position', (global.config.dialogFixed?'fixed':'absolute'));

			$form.unbind("keydown").keydown(function(e) {

    				if (e.keyCode == $.ui.keyCode.ENTER) {

					event.preventDefault();

        				$("#" + formName + "-ok-button").click();
				}
			});

                },
                close: function () {
			$(this).dialog("close");
                }
	};

	dialog_config.position = dialog_fixed_position;

	$form.dialog(dialog_config).dialog('open');
}

function selectCategoryForm(node, option) {

	var tree = global.$list_tree.jstree(true);	
	var $lists = tree.get_node( "lists" );	

	var d = global.config.delim;

	var title = "Select Category";
	var formName = "selectCategoryForm";
	var $form = null;
	var selectedItem = null;
	var id = null;
	var data = {};
	var postData = {};
	var action = "insert";
	var url = null;
	var keyField = "list_cat_id";
	var displayField = "display_name";
	var displayField2 = "name";
	var level = 0;

	if (!global["$" + formName]) {

		global["$" + formName] = $("#" + formName);

	}
	$form = global["$" + formName];

	var $form_Error = $("#" + formName + "_error");
	var $form_Message = $("#" + formName + "_message");

	$form_Error.text("");
	$form_Message.text("");

	resetForm($form[0], true);
	$form[0].reset();

	var $searchString = $("#" + formName + "_searchString");
	var $selected = $("#" + formName + "_selected");
	var $searchField1 = $("#" + formName + "_searchField1");
	var $includeAll = $("#" + formName + "_includeAll");
	var $selectedFieldset = $("#" + formName + "_selectedFieldset");

	$selectedFieldset.css({color:"silver","background-color":""}).prop("disabled", true);

	$selected.text("");

	var dialog_config = {

		autoOpen: false,
                modal: true,
                title: title,
                buttons:
                        [{
                            text: "Cancel",
                            click: function () {

                                $(this).dialog("close");
                            }
                        }, 
 			{
                            id: formName + "-select-button",
                            text: "Select",
			    disabled:true,
                            click: function () {

				var that = this;

				if (node.type == "list" || node.type == "xlist") {

					url = global.config.api.BBB_LIST_CHLDCAT_addCategory;
					postData.list_id = node.data.list_id;
					postData.list_cat_id = selectedItem.id;
				}
				else if (node.type == "categories" || node.type == "xcategories") {

					url = global.config.api.BBB_LIST_CAT_CHLDCAT_addCategory;
					postData.list_cat_id = node.data.list_cat_id;
					postData.child_list_cat_id = selectedItem.id;
				}

				postData.sequence_num = (global.config.newNodeTop?0:999999);

				$.ajax({
					url:url,
					type: 'POST',
					data: postData,
					dataType: "json",
                        		success: function (result) {

						if (serverError(result, false, true)) {

							return;
						}

                            			// server-side validation
						if (!result || result.status == "error") {

							$form_Error.text(getServerMessageText(result));

							return;
						}

						tree.refresh_node(node, function() {
							tree.open_node(node);
						});

						$(that).dialog("close");
                    			},
                    			error: function (xhr, status, error) {

						showAjaxError(xhr, status, error);
                    			},
                    			async: true
                    		});

			    }

			}
		],
                open: function (event, ui) {

			$(event.target).parent().css('position', (global.config.dialogFixed?'fixed':'absolute'));

			$form.unbind("keydown").keydown(function(e) {

    				if (e.keyCode == $.ui.keyCode.ENTER) {

					event.preventDefault();

        				$("#" + formName + "-select-button").click();
				}
			});

			$(this).parent().css("overflow", "visible"); 

			$("#" + formName + "_searchField1, " + "#" + formName + "_searchField2").click(function() {

				$searchString.attr({placeholder:"type " + this.value + " to search, <space> for all"}).focus().val("");
			});

			$searchField1.trigger("click");

			$searchString.autocomplete({
				html:true,
				source: function (request, response) {

					$("body").css("cursor", "progress");

					$form_Error.text("");
					$form_Message.text("");

					var url = "";

					var isKey = !$searchField1[0].checked;

					var searchField = $searchField1[0].checked?displayField:keyField;
					var searchString = $searchString.val();

        				var params = {};

					params[searchField] = searchString;
					if (global.config.exactSearch) params[searchField] += (isKey?"%":"");

					url = global.config.api.BBB_LIST_CATEGORY_searchByName;

					$.ajax({
						url: url,
						type: "POST",
						data: params,
						dataType: "json",
						dataFilter: function (data) { return data; },
						success: function (result) {

							$("body").css("cursor", "default");

							if (serverError(result, true)) {

								return;
							}

							if (result.status != "ok") {

								return;
							}

							var data = result.data;

							response($.map(data, function (item) {

								item[searchField] = new String(item[searchField]);

				    			//	if (item[searchField].substr(0, params[searchField].length).toLowerCase() == params[searchField].toLowerCase()) {

									item.id = item[keyField];
									item.label =  item[keyField] + ": " + htmlEncode(item[displayField]) + "&nbsp;<b>(</b>" + htmlEncode(item[displayField2]) + "<b>)</b>";
									return item;
				    			//	}
                                			}))
                            			},
                            			error: function (xhr, status, error) {

							$("body").css("cursor", "default");

							showAjaxError(xhr, status, error);
                            			}
                        		});

				},
				select: function(event, ui) {

					event.preventDefault();

					selectedItem = ui.item;

					$selectedFieldset.css({color:"","background-color":"#F0F0F0"}).prop("disabled", false);
					$selected.html("<li style=\"margin-left:10px\">" + ui.item.label + "</li>");
				//	$selected.html(ui.item.label);
					$searchString.val("");
					$("#" + formName + "-select-button").removeAttr('disabled').removeClass('ui-state-disabled');
				},
        			open: function(event, ui) {
				//	prevent DOM elements from overlapping autocomplete list
            				$(".ui-autocomplete").css({"z-index":1000,"overflow-y":"auto","overflow-x":"hidden","padding-right":"20px","max-height":"200px"});
        			}
			});

                },
                close: function () {
			$(this).dialog("close");
                }
	};

	if ({"list":1, "xlist":1, "categories":1}[node.type]) {
		dialog_config.buttons.splice(1, 0, {

			text: "New",
			click: function () {

				categoryForm(node);

				$(this).dialog("close");
			}
		});
	}

	dialog_config.position = dialog_fixed_position;

	$form.dialog(dialog_config).dialog('open');
}

function selectEPHForm(node, option) {

	var tree = global.$list_tree.jstree(true);	
	var $lists = tree.get_node( "lists" );	

	var d = global.config.delim;

	var title = "Select EPH Node";
	var formName = "selectEPHForm";
	var $form = null;
	var selectedItem = null;
	var id = null;
	var data = {};
	var postData = {};
	var action = "insert";
	var url = null;
	var keyField = "eph_node_id";
	var displayField = "display_name";
	var xnode = null;

	if (node.type == "eph_node") {

		action = "update";
	}

	if (!global["$" + formName]) {

		global["$" + formName] = $("#" + formName);

	}
	$form = global["$" + formName];

	var $form_Error = $("#" + formName + "_error");
	var $form_Message = $("#" + formName + "_message");

	$form_Error.text("");
	$form_Message.text("");

	resetForm($form[0], true);
	$form[0].reset();

	var $searchString = $("#" + formName + "_searchString");
	var $selected = $("#" + formName + "_selected");
	var $searchField1 = $("#" + formName + "_searchField1");
	var $includeAll = $("#" + formName + "_includeAll");
	var $selectedFieldset = $("#" + formName + "_selectedFieldset");

	var disabledSelect = true;
	var selection = [];
	var xselection = (node.data || {}).eph_nodes || [];
	popSelected(true);
	if (xselection.length) {
		for (var i=0; i < xselection.length; i++) {
			pushSelected(xselection[i]);
		}

		selectedItem = getLastSelectedItem();

		if (!selectedItem || global.config.strictEPH && parseInt(selectedItem.children, 10) && !$includeAll[0].checked) {
			// don't allow selection
		}
		else {

			$selectedFieldset.css({color:"","background-color":"#F0F0F0"}).prop("disabled", false);
			disabledSelect = false;
		}

		action = "update";
	}
	else {
		$selectedFieldset.css({color:"silver","background-color":""}).prop("disabled", true);
	}

	var dialog_config = {

		autoOpen: false,
                modal: true,
                title: title,
                buttons:
                        [{
                            text: "Cancel",
                            click: function () {

                                $(this).dialog("close");
                            }
                        }, 
 			{
                            id: formName + "-select-button",
                            text: "Select",
			    disabled:disabledSelect,
                            click: function () {

				var that = this;

				selectedItem = getLastSelectedItem();

				if (!selectedItem || global.config.strictEPH && parseInt(selectedItem.children, 10) && !$includeAll[0].checked) {
					// don't allow selection
					return;
				}

				if (action == "insert") {
					url = global.config.api.BBB_LIST_RULES_EPH_CAT_insert;	
				}
				else 
				{
					url = global.config.api.BBB_LIST_RULES_EPH_CAT_update;
					postData.rule_id = node.data.rule_id || "";
					postData.facet_rule_name = node.data.facet_rule_name || "";
					postData.facet_value_pair_list = node.data.facet_value_pair_list || "";
				}
				postData.include_all = ($includeAll[0].checked?1:0);
				postData.list_cat_id = node.data.list_cat_id || "";
				postData.eph_node_id = selectedItem.eph_node_id || "";
				postData.sequence_num = (global.config.newNodeTop?0:999999);

				$.ajax({
					url:url,
					type: 'POST',
					data: postData,
					dataType: "json",
                        		success: function (result) {

						if (serverError(result, false, true)) {

							return;
						}

                            			// server-side validation
						if (!result || result.status == "error") {

							$form_Error.text(getServerMessageText(result));

							return;
						}

						xnode = node;
						if (node.type == "eph_node") {

							xnode = tree.get_node(node.parent);
						}
						
						tree.refresh_node(xnode, function() {
							tree.open_node(xnode);
						});

						$(that).dialog("close");
                    			},
                    			error: function (xhr, status, error) {
		
						showAjaxError(xhr, status, error);
                    			},
                    			async: true
                    		});

			    }

			}
		],
                open: function (event, ui) {

			$(event.target).parent().css('position', (global.config.dialogFixed?'fixed':'absolute'));

			$form.unbind("keydown").keydown(function(e) {

    				if (e.keyCode == $.ui.keyCode.ENTER) {

					event.preventDefault();

        				$("#" + formName + "-select-button").click();
				}
			});

		//	$("#" + formName + "_selected-remove").click(function() {
			document.getElementById(formName + "_selected-remove").onclick = function() {

				var selectedItem = popSelected();

				if (!selectedItem || global.config.strictEPH && parseInt(selectedItem.children, 10) && !$includeAll[0].checked) {
					// don't allow selection

					$selectedFieldset.css({color:"silver","background-color":""}).prop("disabled", true);
					$("#" + formName + "-select-button").attr("disabled", true).addClass('ui-state-disabled');
				}

			//	event.stopPropagation();
			//	event.preventDefault();
				return false;
			};
		//	});

			$(this).parent().css("overflow", "visible"); 

			$("#" + formName + "_searchField1, " + "#" + formName + "_searchField2").click(function() {

				$searchString.attr({placeholder:"type " + this.value + " to search, <space> for all"}).focus().val("");
			});

			$searchField1.trigger("click");

		//	$("#" + formName + "_includeAll").click(function() {
			document.getElementById(formName + "_includeAll").onclick = function() {

				var selectedItem = getLastSelectedItem();

				if (!selectedItem || global.config.strictEPH && parseInt(selectedItem.children, 10) && !$includeAll[0].checked) {
					// don't allow selection

					$selectedFieldset.css({color:"silver","background-color":""}).prop("disabled", true);
					$("#" + formName + "-select-button").attr("disabled", true).addClass('ui-state-disabled');
				}
				else 
				{
					$selectedFieldset.css({color:"","background-color":"#F0F0F0"}).prop("disabled", false);
					$("#" + formName + "-select-button").removeAttr('disabled').removeClass('ui-state-disabled');
				}
				return true;
			};
		//	});

			$searchString.autocomplete({

				source: function (request, response) {

					$form_Error.text("");
					$form_Message.text("");

					var url = "";

					var isKey = !$searchField1[0].checked;

					var searchField = $searchField1[0].checked?displayField:keyField;
					var searchString = $searchString.val();

        				var params = {};

					params[searchField] = searchString;
					if (global.config.exactSearch) params[searchField] += (isKey?"%":"");

					selectedItem = getLastSelectedItem();

					params.parent_node_id = (selectedItem||{})[keyField] || "";

					url = global.config.api.BBB_EPH_NODE_select;

					$("body").css("cursor", "progress");

					$.ajax({
						url: url,
						type: "POST",
						data: params,
						dataType: "json",
						dataFilter: function (data) { return data; },
						success: function (result) {

							$("body").css("cursor", "default");

							if (serverError(result, true)) {

								return;
							}

							if (result.status != "ok") {

								return;
							}

							var data = result.data;

							response($.map(data, function (item) {

								item[searchField] = new String(item[searchField]);

				    			//	if (item[searchField].substr(0, params[searchField].length).toLowerCase() == params[searchField].toLowerCase()) {

									item.id = item[keyField];
									item.label = item[keyField] + ": " + item[displayField];
									return item;
				    			//	}
                                			}))
                            			},
                            			error: function (xhr, status, error) {

							$("body").css("cursor", "default");

							showAjaxError(xhr, status, error);
                            			}
                        		});

				},
				select: function(event, ui) {

					event.preventDefault();

					pushSelected(ui.item);
					$searchString.val("");

					selectedItem = getLastSelectedItem();

					if (!selectedItem || global.config.strictEPH && parseInt(selectedItem.children, 10) && !$includeAll[0].checked) {
						// don't allow selection
						return;
					}

					$selectedFieldset.css({color:"","background-color":"#F0F0F0"}).prop("disabled", false);
					$("#" + formName + "-select-button").removeAttr('disabled').removeClass('ui-state-disabled');
				},
        			open: function(event, ui) {
				//	prevent DOM elements from overlapping autocomplete list
            				$(".ui-autocomplete").css({"z-index":1000,"overflow-y":"auto","overflow-x":"hidden","padding-right":"20px","max-height":"200px"});
        			}
			});

                },
                close: function () {
			$(this).dialog("close");
                }
	};

	dialog_config.position = dialog_fixed_position;

	$form.dialog(dialog_config).dialog('open');

	function pushSelected(item) {
    		var ul = document.getElementById("selectEPHForm_selected");
    		var li = document.createElement("li");
    		var index = ul.children.length + 1;
    		li.setAttribute("id", "selectEPHForm_selected" + index);
		li.setAttribute("class", "eph_node");
		li.style.marginLeft = (index * 10);
    		li.appendChild(document.createTextNode(item[keyField] + ": " + item[displayField]));
    		ul.appendChild(li);
		selection.push(item);
	}

	function popSelected(all) {
    		var ul = document.getElementById("selectEPHForm_selected");
		if (all) {
			ul.innerText = "";
			selection = [];
			return null;
		}
		if (!ul.children.length) {
			return;
		}
    		ul.removeChild(ul.children[ul.children.length-1]);
		selection.pop();
		return selection[selection.length-1] || null;
	}

	function getLastSelectedItem() {
		return selection[selection.length-1] || null;
	}
}

function selectJDAForm(node, option) {

	var tree = global.$list_tree.jstree(true);	
	var $lists = tree.get_node( "lists" );	

	var d = global.config.delim;

	var title = "Select JDA Classification";
	var formName = "selectJDAForm";
	var $form = null;
	var selectedItem = null;
	var id = null;
	var data = {};
	var postData = {};
	var action = "insert";
	var url = null;
	var keyField = "id";
	var displayField = "descrip";
	var xnode = null;

	if (node.type == "jda_category") {

		action = "update";
	}

	if (!global["$" + formName]) {

		global["$" + formName] = $("#" + formName);

	}
	$form = global["$" + formName];

	var $form_Error = $("#" + formName + "_error");
	var $form_Message = $("#" + formName + "_message");

	$form_Error.text("");
	$form_Message.text("");

	resetForm($form[0], true);
	$form[0].reset();

	var $searchString = $("#" + formName + "_searchString");
	var $selected = $("#" + formName + "_selected");
	var $searchField1 = $("#" + formName + "_searchField1");
	var $includeAll = $("#" + formName + "_includeAll");
	var $selectedFieldset = $("#" + formName + "_selectedFieldset");

	var disabledSelect = true;
	var selection = [];
	var xselection = node.data || {};
	popSelected(true);

	if (xselection.jda_dept_id) {

		pushSelected({
			jda_dept_id:xselection.jda_dept_id,
		//	jda_sub_dept_id:xselection.jda_sub_dept_id || "",
		//	jda_class:xselection.jda_class || "",
			id:xselection.jda_dept_id,
			descrip:xselection.jda_dept_descrip,
			children:xselection.jda_sub_dept_count
		});

		if (xselection.jda_sub_dept_id) {
			pushSelected({
				jda_dept_id:xselection.jda_dept_id,
				jda_sub_dept_id:xselection.jda_sub_dept_id,
			//	jda_class:xselection.jda_class || "",
				id:xselection.jda_sub_dept_id,
				descrip:xselection.jda_sub_dept_descrip,
				children:xselection.jda_class_count
			});	

			if (xselection.jda_class) {
				pushSelected({
					jda_dept_id:xselection.jda_dept_id,
					jda_sub_dept_id:xselection.jda_sub_dept_id,
					jda_class:xselection.jda_class,
					id:xselection.jda_class,
					descrip:xselection.jda_class_descrip,
					children:0
				});	
			}
		}

		selectedItem = getLastSelectedItem();

		if (!selectedItem || global.config.strictJDA == 2 && parseInt(selectedItem.children, 10) || global.config.strictJDA == 1 && selection.length < 2) {
			// don't allow selection
		}
		else {
			
			$selectedFieldset.css({color:"","background-color":"#F0F0F0"}).prop("disabled", false);
			disabledSelect = false;
		}

		action = "update";
	}
	else {
		$selectedFieldset.css({color:"silver","background-color":""}).prop("disabled", true);
	}

	var dialog_config = {

		autoOpen: false,
                modal: true,
                title: title,
                buttons:
                        [{
                            text: "Cancel",
                            click: function () {

                                $(this).dialog("close");
                            }
                        }, 
 			{
                            id: formName + "-select-button",
                            text: "Select",
			    disabled:disabledSelect,
                            click: function () {

				var that = this;

				selectedItem = getLastSelectedItem();

				if (!selectedItem || global.config.strictJDA == 2 && parseInt(selectedItem.children, 10) || global.config.strictJDA == 1 && selection.length < 2) {
					// don't allow selection
					return;
				}

				if (action == "insert") {
					url = global.config.api.BBB_LIST_RULES_JDA_CAT_insert;
				}
				else 
				{
					url = global.config.api.BBB_LIST_RULES_JDA_CAT_update;
					postData.rule_id = node.data.rule_id || "";
					postData.facet_rule_name = node.data.facet_rule_name || "";
					postData.facet_value_pair_list = node.data.facet_value_pair_list || "";
				}
				postData.list_cat_id = node.data.list_cat_id || "";
				postData.jda_dept_id = selectedItem.jda_dept_id || "";
				postData.jda_sub_dept_id = selectedItem.jda_sub_dept_id || "";
				postData.jda_class = selectedItem.jda_class || "";
				postData.sequence_num = (global.config.newNodeTop?0:999999);

				$.ajax({
					url:url,
					type: 'POST',
					data: postData,
					dataType: "json",
                        		success: function (result) {

						if (serverError(result, false, true)) {

							return;
						}

                            			// server-side validation
						if (!result || result.status == "error") {

							$form_Error.text(getServerMessageText(result));

							return;
						}

						xnode = node;
						if (node.type == "jda_category") {

							xnode = tree.get_node(node.parent);
						}
						
						tree.refresh_node(xnode, function() {
							tree.open_node(xnode);
						});

						$(that).dialog("close");
                    			},
                    			error: function (xhr, status, error) {

						showAjaxError(xhr, status, error);
                    			},
                    			async: true
                    		});

			    }

			}
		],
                open: function (event, ui) {

			$(event.target).parent().css('position', (global.config.dialogFixed?'fixed':'absolute'));

			$form.unbind("keydown").keydown(function(e) {

    				if (e.keyCode == $.ui.keyCode.ENTER) {

					event.preventDefault();

        				$("#" + formName + "-select-button").click();
				}
			});

		//	$("#" + formName + "_selected-remove").click(function() {
			document.getElementById(formName + "_selected-remove").onclick = function() {
	
				var selectedItem = popSelected();

				if (!selectedItem || global.config.strictJDA == 2 && parseInt(selectedItem.children, 10) || global.config.strictJDA == 1 && selection.length < 2) {
					// don't allow selection

					$selectedFieldset.css({color:"silver","background-color":""}).prop("disabled", true);
					$("#" + formName + "-select-button").attr("disabled", true).addClass('ui-state-disabled');
				}

			//	event.stopPropagation();
			//	event.preventDefault();
				return false;
			};
		//	});

			$(this).parent().css("overflow", "visible"); 

			$("#" + formName + "_searchField1, " + "#" + formName + "_searchField2").click(function() {

				$searchString.attr({placeholder:"type " + this.value + " to search, <space> for all"}).val("").focus().val("");
			});

			$searchField1.trigger("click");

			$searchString.autocomplete({

				source: function (request, response) {

					$form_Error.text("");
					$form_Message.text("");

					var url = "";
        				var params = {};

					selectedItem = getLastSelectedItem();

					if (selection.length == 0) {
						url = global.config.api.BBB_DEPT_select;
						keyField = "jda_dept_id";
						displayField = "descrip";
					}
					else if (selection.length == 1) {
						url = global.config.api.BBB_SUB_DEPT_select;
						keyField = "jda_sub_dept_id";
						displayField = "descrip";
						params.jda_dept_id = selection[0].jda_dept_id;
					}
					else if (selection.length == 2) {
						url = global.config.api.BBB_CLASS_select;
						keyField = "jda_class";
						displayField = "descrip";
						params.jda_dept_id = selection[0].jda_dept_id;
						params.jda_sub_dept_id = selection[1].jda_sub_dept_id;
					}
					else {
						url = null;
					}

					var isKey = !$searchField1[0].checked;
					var searchField = $searchField1[0].checked?displayField:keyField;
					var searchString = $searchString.val();

					params[searchField] = searchString;
					if (global.config.exactSearch) params[searchField] += (isKey?"%":"");

					if (!url) {
						return;
					}

					$("body").css("cursor", "progress");

					$.ajax({
						url: url,
						type: "POST",
						data: params,
						dataType: "json",
						dataFilter: function (data) { return data; },
						success: function (result) {

							$("body").css("cursor", "default");

							if (serverError(result, true)) {

								return;
							}

							if (result.status != "ok") {

								return;
							}

							var data = result.data;

							response($.map(data, function (item) {

								item[searchField] = new String(item[searchField]);

				    			//	if (item[searchField].substr(0, params[searchField].length).toLowerCase() == params[searchField].toLowerCase()) {

									item.id = item[keyField];
									item.label = item[keyField] + ": " + item[displayField];
									
									return item;
				    			//	}
                                			}))
                            			},
                            			error: function (xhr, status, error) {

							$("body").css("cursor", "default");

							showAjaxError(xhr, status, error);
                            			}
                        		});

				},
				select: function(event, ui) {

					event.preventDefault();

					pushSelected(ui.item);
					$searchString.val("");

					selectedItem = getLastSelectedItem();

					if (!selectedItem || global.config.strictJDA == 2 && parseInt(selectedItem.children, 10) || global.config.strictJDA == 1 && selection.length < 2) {
						// don't allow selection
						return;
					}

					$selectedFieldset.css({color:"","background-color":"#F0F0F0"}).prop("disabled", false);
					$("#" + formName + "-select-button").removeAttr('disabled').removeClass('ui-state-disabled');
				},
        			open: function(event, ui) {
				//	prevent DOM elements from overlapping autocomplete list
            				$(".ui-autocomplete").css({"z-index":1000,"overflow-y":"auto","overflow-x":"hidden","padding-right":"20px","max-height":"200px"});
        			}
			});

                },
                close: function () {
			$(this).dialog("close");
                }
	};

	dialog_config.position = dialog_fixed_position;

	$form.dialog(dialog_config).dialog('open');

	function pushSelected(item) {
    		var ul = document.getElementById("selectJDAForm_selected");
    		var li = document.createElement("li");
    		var index = ul.children.length + 1;
    		li.setAttribute("id", "selectJDAForm_selected" + index);
		li.setAttribute("class", "jda_category");
		li.style.marginLeft = (index * 10);
    		li.appendChild(document.createTextNode(item.id + ": " + item.descrip));
    		ul.appendChild(li);
		selection.push(item);
	}

	function popSelected(all) {
    		var ul = document.getElementById("selectJDAForm_selected");
		if (all) {
			ul.innerText = "";
			selection = [];
			return null;
		}
		if (!ul.children.length) {
			return;
		}
    		ul.removeChild(ul.children[ul.children.length-1]);
		selection.pop();
		return selection[selection.length-1] || null;
	}

	function getLastSelectedItem() {
		return selection[selection.length-1] || null;
	}
}

function selectSKUForm(node, option) {

	var tree = global.$list_tree.jstree(true);	
	var $lists = tree.get_node( "lists" );

	var d = global.config.delim;
	var id = null;
	var url = null;
	var title = "Select SKU";
	var formName = "selectSKUForm";
	var selectedItem = null;
	var action = "insert";
	var data = {};
	var postData = {};

	if (!global["$" + formName]) {

		global["$" + formName] = $("#" + formName);

	}
	$form = global["$" + formName];

	var $form_Error = $("#" + formName + "_error");
	var $form_Message = $("#" + formName + "_message");

	$form_Error.text("");
	$form_Message.text("");

	resetForm($form[0], true);
	$form[0].reset();

	var $searchString = $("#" + formName + "_searchString");
	var $selected = $("#" + formName + "_selected");
	var $searchField1 = $("#" + formName + "_searchField1");
	var $searchField2 = $("#" + formName + "_searchField2");
	var $searchField3 = $("#" + formName + "_searchField3");
	var $include = $("#" + formName + "_include");
	var $selectedFieldset = $("#" + formName + "_selectedFieldset");

	$selectedFieldset.css({color:"silver","background-color":""}).prop("disabled", true);
	$selected.text("");

	var dialog_config = {

		autoOpen: false,
                modal: true,
                title: title,
                buttons:
                        [{
                            text: "Cancel",
                            click: function () {

                                $(this).dialog("close");
                            }
                        }, 
 			{
                            id: formName + "-select-button",
                            text: "Select",
			//  disabled:true,
                            click: function () {

				var that = this;

				if (!$searchField3[0].checked) {

					if (!selectedItem) {
						return;
					}
				}
				else {
					if (!$searchString.val()) {

						return;
					}
					selectedItem = {};
					selectedItem.id = $searchString.val();
					selectedItem.label = $searchString.val();
				}

				postData.list_cat_id = node.data.list_cat_id;
				postData.sku_id = selectedItem.id;
				postData.sequence_num = (global.config.newNodeTop?0:999999);
				postData.rule_evaluation_cd = ($include[0].checked?"IN":"EX");

				var url = global.config.api.BBB_LIST_RULES_SKU_CAT_insert;

				$.ajax({
					url:url,
					type: 'POST',
					data:postData,
					dataType: 'json',
                        		success: function (result) {

						if (serverError(result, false, true)) {

							return;
						}

                            			// server-side validation
						if (!result || result.status == "error") {

							$form_Error.text(getServerMessageText(result));

							return;
						}

						tree.refresh_node(node, function() {
							tree.open_node(node);
						});

						$(that).dialog("close");
                    			},
                    			error: function (xhr, status, error) {

						showAjaxError(xhr, status, error);
                    			},
                    			async: true
                    		});

                            }

			}
		],
                open: function (event, ui) {

			$(event.target).parent().css('position', (global.config.dialogFixed?'fixed':'absolute'));

			$form.unbind("keydown").keydown(function(e) {

    				if (e.keyCode == $.ui.keyCode.ENTER) {

					event.preventDefault();

        				$("#" + formName + "-select-button").click();
				}
			});

			$(this).parent().css("overflow", "visible"); 

			$("#" + formName + "_searchField1, " + "#" + formName + "_searchField2, " + "#" + formName + "_searchField3").click(function() {

				if (this.value == "none") {
					$searchString.attr({placeholder:""});
					selectedItem = null;
					$selectedFieldset.css({color:"silver","background-color":""}).prop("disabled", true);
					$selected.text("");
				}
				else {
					$searchString.attr({placeholder:"type " + this.value + " to search"}).focus().val("");
				}
			});

		//	$searchField1.trigger("click");
			$("#" + formName + "_searchField3").trigger("click");

			$searchString.autocomplete({
				minLength:3,
				source: function (request, response) {

					if ($searchField3[0].checked) {

						response([]);
						return;
					}

					$form_Error.text("");
					$form_Message.text("");

					var url = "";

					var keyField = "sku_id";
					var displayField = "display_name";
					var isKey = !$searchField1[0].checked;

					var searchField = $searchField1[0].checked?displayField:keyField;
					var searchString = $searchString.val();

        				var params = {};

					params[searchField] = searchString;
					if (global.config.exactSearch) params[searchField] += (isKey?"%":"");

					params.max = global.config.maxRows || 512;

					url = global.config.api.DCS_SKU_select;
	
					$("body").css("cursor", "progress");

					$.ajax({
						url: url,
						type: "POST",
						data: params,
						dataType: "json",
						dataFilter: function (data) { return data; },
						success: function (result) {

							$("body").css("cursor", "default");

							if (serverError(result, true)) {

								return;
							}

							if (result.status != "ok") {

								return;
							}

							var data = result.data;

							response($.map(data, function (item) {

								item[searchField] = new String(item[searchField]);

				    			//	if (item[searchField].substr(0, params[searchField].length).toLowerCase() == params[searchField].toLowerCase()) {

                                    					item.id = item[keyField];
									item.label = item[keyField] + ": " + item[displayField];
									return item;
				    			//	}
                                			}))
                            			},
                            			error: function (xhr, status, error) {

							$("body").css("cursor", "default");

							showAjaxError(xhr, status, error);
                            			}
                        		});

				},
				select: function(event, ui) {

					event.preventDefault();

					selectedItem = ui.item;

					$selectedFieldset.css({color:"","background-color":"#F0F0F0"}).prop("disabled", false);
				//	$selected.html(ui.item.label);
					$selected.html("<li style=\"margin-left:10px\">" + ui.item.label + "</li>");
					$searchString.val("");
					$("#" + formName + "-select-button").removeAttr('disabled').removeClass('ui-state-disabled');
				},
        			open: function(event, ui) {
				//	prevent DOM elements from overlapping autocomplete list
            				$(".ui-autocomplete").css({"z-index":1000,"overflow-y":"auto","overflow-x":"hidden","padding-right":"20px","max-height":"200px"});
        			}
			});

                },
                close: function () {
			$(this).dialog("close");
                }
	};

	dialog_config.position = dialog_fixed_position;

	$form.dialog(dialog_config).dialog('open');
}

function selectRuleForm(node, option) {

	var tree = global.$list_tree.jstree(true);	
	var $lists = tree.get_node( "lists" );

	var d = global.config.delim;
	var id = null;
	var url = null;
	var title = "Select Facet Rule";
	var formName = "selectRuleForm";
	var selectedItem = null;
	var action = "update";
	var xtype = "";
	var data = {};
	var postData = {};
	var url;

	if (!global["$" + formName]) {

		global["$" + formName] = $("#" + formName);

	}
	$form = global["$" + formName];

	var $form_Error = $("#" + formName + "_error");
	var $form_Message = $("#" + formName + "_message");

	$form_Error.text("");
	$form_Message.text("");

	resetForm($form[0], true);
	$form[0].reset();

	var $searchString = $("#" + formName + "_searchString");
	var $selected = $("#" + formName + "_selected");
	var $searchField1 = $("#" + formName + "_searchField1");
	var $searchField2 = $("#" + formName + "_searchField2");
	var $searchField3 = $("#" + formName + "_searchField3");
	var $include = $("#" + formName + "_include");
	var $selectedFieldset = $("#" + formName + "_selectedFieldset");

	$selectedFieldset.css({color:"silver","background-color":""}).prop("disabled", true);
	$selected.text("");

	var dialog_config = {

		autoOpen: false,
                modal: true,
                title: title,
                buttons:
                        [{
                            text: "Cancel",
                            click: function () {

                                $(this).dialog("close");
                            }
                        }, 
			{
				text: "New",
				click: function () {

					if (global.config.renameRule && !global.config.addRules) {
						setTimeout(function() {createRule(node);}, 1000);
					}
					else {
						ruleForm(node, "insert");
					}

					$(this).dialog("close");

				}
			},
 			{
                            id: formName + "-select-button",
                            text: "Select",
			    disabled:true,
                            click: function () {

				var that = this;

				if (!$searchField3[0].checked) {

					if (!selectedItem) {
						return;
					}
				}
				else {
					if (!$searchString.val()) {

						return;
					}
					selectedItem = {};
					selectedItem.id = $searchString.val();
					selectedItem.label = $searchString.val();
				}

				var onode = node;
				var snode = node;
				var rnode = node;

				if (node.type == "eph_node") {
					if (node.children && node.children.length) {
						url = global.config.api.BBB_LIST_RULES_EPH_CAT_insert;
						postData.sequence_num = (global.config.newNodeTop?0:999999);
						postData.sequence_num = node.data.sequence_num + (global.config.newNodeTop?0:1);
					}
					else {
						url = global.config.api.BBB_LIST_RULES_EPH_CAT_update;
						postData.rule_id = node.data.rule_id || "";
					}
					postData.eph_node_id = node.data.eph_node_id || "";
					rnode = tree.get_node(node.parent);
				}
				else if (node.type == "jda_category") {
					if (node.children && node.children.length) {
						url = global.config.api.BBB_LIST_RULES_JDA_CAT_insert;
						postData.sequence_num = (global.config.newNodeTop?0:999999);
						postData.sequence_num = node.data.sequence_num + (global.config.newNodeTop?0:1);
					}
					else {
						url = global.config.api.BBB_LIST_RULES_JDA_CAT_update;
						postData.rule_id = node.data.rule_id || "";
					}
					postData.jda_dept_id = node.data.jda_dept_id || "";
					postData.jda_sub_dept_id = node.data.jda_sub_dept_id || "";
					postData.jda_class = node.data.jda_class || "";
					rnode = tree.get_node(node.parent);
				}
				else {
					url = global.config.api.BBB_LIST_RULES_FACET_CAT_insert;
					postData.sequence_num = (global.config.newNodeTop?0:999999);
				}
				postData.list_cat_id = node.data.list_cat_id || "";
				postData.facet_rule_name = selectedItem.facet_rule_name || "";
				postData.facet_value_pair_list = selectedItem.facet_value_pair_list;

				if (!url) {
					return;
				}

				$.ajax({
					url:url,
					type: 'POST',
					data:postData,
					dataType: 'json',
                        		success: function (result) {

						if (serverError(result, false, true)) {

							return;
						}

                            			// server-side validation
						if (!result || result.status == "error") {

							$form_Error.text(getServerMessageText(result));

							return;
						}

						var newRecord = (result.data||[])[0];

						if (!newRecord) {

							$form_Error.text(action + " failed - no data returned");
							return;
						}

						if (node.type == "eph_node") {
							if (node.children && node.children.length) {
								snode = rnode.id + d + "e" + d + newRecord.rule_id;
							//	snode = rnode.id + d + "e2" + d + newRecord.rule_id;
								onode = snode;
							}
						}

						if (node.type == "jda_category") {
							if (node.children && node.children.length) {
								snode = rnode.id + d + "j" + d + newRecord.rule_id;
							//	snode = rnode.id + d + "j2" + d + newRecord.rule_id;
								onode = snode;
							}
						}

						tree.refresh_node(rnode, function() {
							tree.deselect_node(node);
							tree.select_node(snode);
							tree.open_node(onode);
						});

						$(that).dialog("close");
                    			},
                    			error: function (xhr, status, error) {

						showAjaxError(xhr, status, error);
                    			},
                    			async: true
                    		});

                            }

			}
		],
                open: function (event, ui) {

			$(event.target).parent().css('position', (global.config.dialogFixed?'fixed':'absolute'));

			$form.unbind("keydown").keydown(function(e) {

    				if (e.keyCode == $.ui.keyCode.ENTER) {

					event.preventDefault();

        				$("#" + formName + "-select-button").click();
				}
			});

			$(this).parent().css("overflow", "visible"); 

			$("#" + formName + "_searchField1, " + "#" + formName + "_searchField2, " + "#" + formName + "_searchField3").click(function() {

				if (this.value == "none") {
					$searchString.attr({placeholder:""});
					selectedItem = null;
					$selectedFieldset.css({color:"silver","background-color":""}).prop("disabled", true);
					$selected.text("");
				}
				else {
					$searchString.attr({placeholder:"type " + this.value + " to search, <space> for all"}).focus().val("");
				}
			});

			$searchField1.trigger("click");

			$searchString.autocomplete({
				minLength:1,
				source: function (request, response) {

					if ($searchField3[0].checked) {

						response([]);
						return;
					}

					$form_Error.text("");
					$form_Message.text("");

					var url = "";

					var keyField = "rule_id";
					var displayField = "facet_rule_name";
					var isKey = !$searchField1[0].checked;

					var searchField = $searchField1[0].checked?displayField:keyField;
					var searchString = $searchString.val();

        				var params = {};

					params[searchField] = searchString;
					if (global.config.exactSearch) params[searchField] += (isKey?"%":"");

				//	params.rule_type_cd = 3;

					if (node.type == "eph_node") {
						params.eph_node_id = node.data.eph_node_id || "";
					//	params.rule_type_cd = 1;
					}
					if (node.type == "jda_category") {
						params.jda_dept_id = node.data.jda_dept_id || "";
						params.jda_sub_dept_id = node.data.jda_sub_dept_id || "";
						params.jda_class = node.data.jda_class || "";
					//	params.rule_type_cd = 2;
					}

					url = global.config.api.BBB_LIST_RULES_select;

					$("body").css("cursor", "progress");

					$.ajax({
						url: url,
						type: "POST",
						data: params,
						dataType: "json",
						dataFilter: function (data) { return data; },
						success: function (result) {

							$("body").css("cursor", "default");

							if (serverError(result, true)) {

								return;
							}

							if (result.status != "ok") {

								return;
							}

							var data = result.data;

							response($.map(data, function (item) {

								item[searchField] = new String(item[searchField]);

				    			//	if (item[searchField].substr(0, params[searchField].length).toLowerCase() == params[searchField].toLowerCase()) {

                                    					item.id = item[keyField];
									item.label = item[keyField] + ": " + item[displayField];
									return item;
				    			//	}
                                			}))
                            			},
                            			error: function (xhr, status, error) {
						
							$("body").css("cursor", "default");

							showAjaxError(xhr, status, error);
                            			}
                        		});

				},
				select: function(event, ui) {

					event.preventDefault();

					selectedItem = ui.item;

					$selectedFieldset.css({color:"","background-color":"#F0F0F0"}).prop("disabled", false);

					pushSelected(ui.item);

					$searchString.val("");
					$("#" + formName + "-select-button").removeAttr('disabled').removeClass('ui-state-disabled');
				},
        			open: function(event, ui) {
				//	prevent DOM elements from overlapping autocomplete list
            				$(".ui-autocomplete").css({"z-index":1000,"overflow-y":"auto","overflow-x":"hidden","padding-right":"20px","max-height":"200px"});
        			}
			});

                },
                close: function () {
			$(this).dialog("close");
                }
	};

	dialog_config.position = dialog_fixed_position;

	$form.dialog(dialog_config).dialog('open');

	function pushSelected(item) {
		var prefix = {"rules":"","eph_node":"eph_","jda_category":"jda_"}[node.type];
		$selected[0].className = prefix + "rule";
		var html = "<li style=\"margin-left:15px\">" + item.label;
		var facets = splitFacets(item.facets || item.facet_value_pair_list);
		if (facets.length) {
			html += "<ul style=\"margin-left:0px;padding:10px 5px 10px 10px\" class=\"" + prefix + "facet" + "\">";
			for (var i=0; i < facets.length; i++) {
				html += "<li style=\"margin-left:15px\">" + facets[i].facet_id + ": " + facets[i].facet_name + " - " + facets[i].facet_value_id + ": " + facets[i].facet_value_name + "</li>";
			}
			html += "</li></ul>";
		}
		html += "</li>";
		$selected[0].innerHTML = html;
	}
}

function selectFacetForm(node, option) {

	var tree = global.$list_tree.jstree(true);	
	var $lists = tree.get_node( "lists" );	

	var d = global.config.delim;

	var keys = getNodeKeys(node.id);
	var url = null;
	var title = "Select Facet";
	var formName = "selectFacetForm";
	var selectedItem = null;
	var action = "update";
	var postData;
	var pnode;
	var id = null;

	if (node.type == "rule") {

	}
	if (node.type == "eph_rule") {

		title = "Select EPH Facet";
	}
	else if (node.type == "jda_rule") { 

		title = "Select JDA Facet";
	}

	if (!global["$" + formName]) {

		global["$" + formName] = $("#" + formName);

	}
	$form = global["$" + formName];

	var $form_Error = $("#" + formName + "_error");
	var $form_Message = $("#" + formName + "_message");

	$form_Error.text("");
	$form_Message.text("");

	resetForm($form[0], true);
	$form[0].reset();

	var $searchString = $("#" + formName + "_searchString");
	var $selected = $("#" + formName + "_selected");
	var $searchField1 = $("#" + formName + "_searchField1");
	var $selectedFieldset = $("#" + formName + "_selectedFieldset");
	var $facetvalue = $("#" + formName + "_facetvalue");

	$selectedFieldset.css({color:"silver","background-color":""}).prop("disabled", true);
	$selected.text("");

	$facetvalue[0].length = 0;

	var dialog_config = {

		autoOpen: false,
                modal: true,
                title: title,
                buttons:
                        [{
                            text: "Cancel",
                            click: function () {

                                $(this).dialog("close");
                            }
                        }, 
 			{
                            id: formName + "-select-button",
                            text: "Select",
			    disabled:true,
                            click: function () {

				var that = this;

				var xtypes = {
					"rule":{"type":"facet","prefix":"f"}, 
					"eph_rule":{"type":"eph_facet","prefix":"ef"},
					"jda_rule":{"type":"jda_facet","prefix":"jf"}
				}

				var $selectedFacetValue = $( "#" + formName + "_facetvalue option:selected");
				var facet_id = selectedItem.id;
				var facet_value_id = $selectedFacetValue.val();

				postData = {};
				postData.rule_id = node.data.rule_id || "";
				postData.list_cat_id = node.data.list_cat_id || "";
				postData.facet_rule_name = node.data.facet_rule_name || "";
				postData.facet_value_pair_list = addFacet(node.data.facet_value_pair_list, facet_id, facet_value_id);

				if (node.type == "eph_rule") {
					url = global.config.api.BBB_LIST_RULES_EPH_CAT_update;
					postData.eph_node_id = node.data.eph_node_id;
				}
				else if (node.type == "jda_rule") { 
					url = global.config.api.BBB_LIST_RULES_JDA_CAT_update;
					postData.jda_dept_id = node.data.jda_dept_id || "";
					postData.jda_sub_dept_id = node.data.jda_sub_dept_id || "";
					postData.jda_class = node.data.jda_class || "";
				}
				else if (node.type == "rule") { 
					url = global.config.api.BBB_LIST_RULES_FACET_CAT_update;
				}
				else {
					url = null;
				}

				if (!url) return;

                   		$.ajax({
                        		url: url,
					type: 'POST',
					dataType: 'json',
					data:postData,
                        		success: function (result) {

						if (serverError(result, false, true)) {

							return;
						}

                            			// server-side validation
						if (!result || result.status == "error") {

							$form_Error.text(getServerMessageText(result));

							return;
						}

						var xnode = node;

						if (node.type == "eph_rule") {
							xnode = tree.get_node(node.parents[1]);
						}
						else if (node.type == "jda_rule") { 
							xnode = tree.get_node(node.parents[1]);
						}
						else { 
							xnode = tree.get_node(node.parent);
						}

						tree.refresh_node(xnode, function() {
							tree.open_node(node);
						});

						$(that).dialog("close");
                    			},
                    			error: function (xhr, status, error) {

						showAjaxError(xhr, status, error);
                    			},
                    			async: true
                    		});

                            }

			}
		],
                open: function (event, ui) {

			$(event.target).parent().css('position', (global.config.dialogFixed?'fixed':'absolute'));

			$form.unbind("keydown").keydown(function(e) {

    				if (e.keyCode == $.ui.keyCode.ENTER) {

					event.preventDefault();

        				$("#" + formName + "-select-button").click();
				}
			});

			$(this).parent().css("overflow", "visible"); 

			$("#" + formName + "_searchField1, " + "#" + formName + "_searchField2").click(function() {

				$searchString.attr({placeholder:"type " + this.value + " to search, <space> for all"}).focus().val("");
			});

			$searchField1.trigger("click");

			$searchString.autocomplete({

				source: function (request, response) {

					$form_Error.text("");
					$form_Message.text("");

					var keyField = "facet_id";
					var displayField = "description";
					var isKey = !$searchField1[0].checked;

					var searchField = $searchField1[0].checked?displayField:keyField;
					var searchString = $searchString.val();

        				var params = {};

					params[searchField] = searchString;
					if (global.config.exactSearch) params[searchField] += (isKey?"%":"");

					if (node.type == "rule") {
						url = global.config.api.BBB_FACET_TYPES_select;
					}
					else if (node.type == "eph_rule") {

						url = global.config.api.BBB_EPH_FACET_TYPES_select;
						params.eph_id = node.data.eph_node_id;
						params.eph_node_id = node.data.eph_node_id;
					}
					else if (node.type == "jda_rule") { 

						url = global.config.api.BBB_JDA_FACET_TYPES_select;
						params.jda_dept_id = node.data.jda_dept_id || "";
						if (node.data.jda_sub_dept_id) params.jda_sub_dept_id = node.data.jda_sub_dept_id || "";
						if (node.data.jda_class) params.jda_class = node.data.jda_class || "";
					}
					else {
						url = global.config.api.BBB_FACET_TYPES_select;
					}

					$("body").css("cursor", "progress");

					$.ajax({
						url: url,
						type: "POST",
						data: params,
						dataType: "json",
						dataFilter: function (data) { return data; },
						success: function (result) {

							$("body").css("cursor", "default");

							if (serverError(result, true)) {

								return;
							}

							if (result.status != "ok") {

								return;
							}

							var data = result.data;

							response($.map(data, function (item) {

								item[searchField] = new String(item[searchField]);

				    			//	if (item[searchField].substr(0, params[searchField].length).toLowerCase() == params[searchField].toLowerCase()) {

									item.id = item[keyField];
									item.label = item[keyField] + ": " + item[displayField];
									return item;
				    			//	}
                                			}))
                            			},
                            			error: function (xhr, status, error) {

							$("body").css("cursor", "default");

							showAjaxError(xhr, status, error);
                            			}
                        		});

				},
				select: function(event, ui) {

					event.preventDefault();

					var params = {};

					selectedItem = ui.item;

					$selectedFieldset.css({color:"","background-color":"#F0F0F0"}).prop("disabled", false);
				//	$selected.html(ui.item.label);
					$selected[0].className = {"eph_rule":"eph_facet","jda_rule":"jda_facet"}[node.type] || "facet";
					$selected.html("<li style=\"margin-left:10px\">" + ui.item.label + "</li>");
					$searchString.val("");
					$("#" + formName + "-selectButton").removeAttr('disabled').removeClass('ui-state-disabled');

					params.facet_id = selectedItem.id;

					if (node.type == "rule") {
						url = global.config.api.BBB_FACET_VALUE_PAIRS_select;
					}
					else if (node.type == "eph_rule") {

						url = global.config.api.BBB_EPH_FACET_VALUE_PAIRS_select;
						params.eph_id = node.data.eph_node_id;
						params.eph_node_id = node.data.eph_node_id;
					}
					else if (node.type == "jda_rule") { 
	
						url = global.config.api.BBB_JDA_FACET_VALUE_PAIRS_select;
						params.jda_dept_id = node.data.jda_dept_id;
						if (node.data.jda_sub_dept_id) params.jda_sub_dept_id = node.data.jda_sub_dept_id;
						if (node.data.jda_class) params.jda_class = node.data.jda_class;
					}
					else {

						url = global.config.api.BBB_FACET_VALUE_PAIRS_select;
					}

					getJson(formName + "_facetvalue", url, params, function(result) {

						if (result.status != "ok") return;

						return $.map(result.data, function(item) {

						//	if (String(selectedItem.id) != String(item.facet_id)) return null;

							return {"text":item.facet_value_id + ": " + item.facet_value_desc,"value":item.facet_value_id};
						});
					},
					function() {

						if ($("#" + formName + "_facetvalue")[0].selectedIndex != -1) {

							$("#" + formName + "-select-button").removeAttr('disabled').removeClass('ui-state-disabled');
						}
					});

				},
        			open: function(event, ui) {
				//	prevent DOM elements from overlapping autocomplete list
            				$(".ui-autocomplete").css({"z-index":1000,"overflow-y":"auto","overflow-x":"hidden","padding-right":"20px","max-height":"200px"});
        			}
			});

                },
                close: function () {
			$(this).dialog("close");
                }
	};

	dialog_config.position = dialog_fixed_position;

	$form.dialog(dialog_config).dialog('open');
}

function createCategory(node, option) {

	categoryForm(node, "insert");
}

function editCategory(node, option) {

	updateCategory(node, "update");
}

function updateCategory(node, action) {

	var tree = global.$list_tree.jstree(true);

	var id = node.data.child_list_cat_id || node.data.list_cat_id;

	var data = {};
	var postData = {};

	postData.list_cat_id = id;
	postData.category_id = id;

	var url = global.config.api.BBB_LIST_CATEGORY_getCategory;

	$.ajax({
		url: url,
		type: 'POST',
		data:postData,
		dataType: 'json',
		success: function (result) {

			if (serverError(result, true)) {

				return;
			}

			if (!result.data || !result.data[0]) {

				showMessageBox("no data returned", "Error");

				return;
			}

			categoryForm(node, action, result.data[0]);

                    },
                    error: function (xhr, status, error) {

			showAjaxError(xhr, status, error);
                    },
                    async: true
	});
}

function deleteCategory(node, option) {

	deleteNode(node);
}

function categoryForm(node, action, record) {

	var tree = global.$list_tree.jstree(true);

	var d = global.config.delim;	

	var url = null;
	var title = "";
	var formName = "categoryForm";
	var id = 0;
	var $form = null;
	var action = action || "insert";
	var $element;

	if (action == "insert") {
		title = "Create Category";
	}
	else if (action == "update") {
		title = "Edit Category";
	}

	if (!global["$" + formName]) {

		global["$" + formName] = $("#" + formName);

	}
	$form = global["$" + formName];

	var $category_name = $("#" + formName + "_name");

	var $primary_parent_cat_SearchString = $("#" + formName + "_primary_parent_cat_SearchString");
	var $primary_parent_cat_Selected = $("#" + formName + "_primary_parent_cat_Selected");
	var $primary_parent_cat_SelectedFieldset = $("#" + formName + "_primary_parent_cat_SelectedFieldset");
	var $primary_parent_cat_SearchField1 = $("#" + formName + "_primary_parent_cat_SearchField1");
	var $primary_parent_cat_id = $("#" + formName + "_primary_parent_cat_id");
	var $primary_parent_cat_id_error = $("#" + formName + "_primary_parent_cat_id-error");

	var $facet_id_pkg_cnt_SearchString = $("#" + formName + "_facet_id_pkg_cnt_SearchString");
	var $facet_id_pkg_cnt_Selected = $("#" + formName + "_facet_id_pkg_cnt_Selected");
	var $facet_id_pkg_cnt_SelectedFieldset = $("#" + formName + "_facet_id_pkg_cnt_SelectedFieldset");
	var $facet_id_pkg_cnt_SearchField1 = $("#" + formName + "_facet_id_pkg_cnt_SearchField1");
	var $facet_id_pkg_cnt = $("#" + formName + "_facet_id_pkg_cnt");
	var $facet_id_pkg_cnt_error = $("#" + formName + "_facet_id_pkg_cnt-error");

	var $form_Error = $("#" + formName + "_error");
	var $form_Message = $("#" + formName + "_message");

	$form_Error.text("");
	$form_Message.text("");

	resetForm($form[0]);
	$form[0].reset();

	if (record) {

		id = record.list_cat_id;
		record.is_enabled = (record.is_disabled?false:true);
		delete record.is_disabled;
		record.is_config_complete = !!record.is_config_complete;
		record.is_visible_on_checklist = !!record.is_visible_on_checklist;
		record.is_visible_on_reg_list = !!record.is_visible_on_reg_list;
		record.is_child_prd_needed_to_disp = !!record.is_child_prd_needed_to_disp;
		record.is_deleted = !!record.is_deleted;

		deserializeForm($form[0], record, formName + "_");

		var channels = [
				{prefix:"_",site_flag:1,is_mobile:0}, 
				{prefix:"_mob_",site_flag:1,is_mobile:1},
				{prefix:"_baby_",site_flag:2,is_mobile:0},
				{prefix:"_mob_baby_",site_flag:2,is_mobile:1},
				{prefix:"_ca_",site_flag:3,is_mobile:0},
				{prefix:"_mob_ca_",site_flag:3,is_mobile:1},
				{prefix:"_tbs_",site_flag:6,is_mobile:0},
				{prefix:"_tbs_baby_",site_flag:7,is_mobile:0},
				{prefix:"_tbs_ca_",site_flag:8,is_mobile:0}
		];

		var categoryPath = getCategoryPath(tree, node);

		for (var i=0; i < channels.length; i++) {

			c = channels[i];

			var categoryURL = getChannelURL(c.site_flag, c.is_mobile) + categoryPath;

			$("#" + formName + c.prefix + "category_url_link").attr('title', categoryURL).attr('href', categoryURL);
		}

		if (!global.config.enableExtraImageURLs) {
			$('#categoryForm_mob_image_url').attr('disabled', true);
			$('#categoryForm_mob_baby_image_url').attr('disabled', true);
			$('#categoryForm_mob_ca_image_url').attr('disabled', true);
			$('#categoryForm_tbs_baby_image_url').attr('disabled', true);
			$('#categoryForm_tbs_ca_image_url').attr('disabled', true);
		}

		if (record.primary_parent_cat_id) {
			$primary_parent_cat_SelectedFieldset.css({color:"","background-color":"#F0F0F0"}).prop("disabled", false);
			$primary_parent_cat_id.val(record.primary_parent_cat_id);
			$primary_parent_cat_Selected.text(record.primary_parent_cat_id + ": " + (record.primary_parent_cat_name || record.primary_parent_cat_display_name));
		}
		else {
			$primary_parent_cat_SelectedFieldset.css({color:"silver","background-color":""}).prop("disabled", true);
			$primary_parent_cat_Selected.text("");
			$primary_parent_cat_id.val("");
		}

		if (record.facet_id_pkg_cnt) {
			$facet_id_pkg_cnt_SelectedFieldset.css({color:"","background-color":"#F0F0F0"}).prop("disabled", false);
			$facet_id_pkg_cnt.val(record.facet_id_pkg_cnt);
			$facet_id_pkg_cnt_Selected.text(record.facet_id_pkg_cnt + ": " + record.facet_id_pkg_cnt_description);
		}
		else {
			$facet_id_pkg_cnt_SelectedFieldset.css({color:"silver","background-color":""}).prop("disabled", true);
			$facet_id_pkg_cnt_Selected.text("");
			$facet_id_pkg_cnt.val("");
		}
	}
	else {
		// if add or clone

		$primary_parent_cat_SelectedFieldset.css({color:"silver","background-color":""}).prop("disabled", true);
		$primary_parent_cat_Selected.text("");
		$primary_parent_cat_id.val("");

		$facet_id_pkg_cnt_SelectedFieldset.css({color:"silver","background-color":""}).prop("disabled", true);
		$facet_id_pkg_cnt_Selected.text("");
		$facet_id_pkg_cnt.val("");
	}

	var dialog_config = {

		autoOpen: false,
                modal: true,
                title: title,
		minWidth:1000,
                buttons:
                        [{
                            text: "Cancel",
                            click: function () {

                                $(this).dialog("close");
                            }
                        },
	 		{
                            text: "Ok",
			    id: formName + "-ok-button",
                            click: function () {

				var that = this;

				var formData = serializeForm($form[0], formName + "_"); 

				for (var p in formData) {

					if (typeof(formData[p]) == "string") {
						formData[p] = formData[p].trim();
					}
				}
				formData.is_disabled = (formData.is_enabled?false:true);
				delete formData.is_enabled;
				formData.is_config_complete = !!formData.is_config_complete;
				formData.is_visible_on_checklist = !!formData.is_visible_on_checklist;
				formData.is_visible_on_reg_list = !!formData.is_visible_on_reg_list;
				formData.is_child_prd_needed_to_disp = !!formData.is_child_prd_needed_to_disp;
				formData.is_deleted = !!formData.is_deleted;
				formData.sequence_num = formData.sequence_num||(global.config.newNodeTop?0:999999);

  				formData.url_override = !!formData.url_override;
      				formData.baby_url_override = !!formData.baby_url_override
      				formData.ca_url_override = !!formData.ca_url_override 
      				formData.mob_url_override = !!formData.mob_url_override 
      				formData.mob_baby_url_override = !!formData.mob_baby_url_override 
       	 			formData.mob_ca_url_override = !!formData.mob_ca_url_override 
       				formData.tbs_url_override = !!formData.tbs_url_override 
      				formData.tbs_baby_url_override = !!formData.tbs_baby_url_override 
         			formData.tbs_ca_url_override = !!formData.tbs_ca_url_override

				resetForm($form[0]);

				if (!formData.name) {

					$("#" + formName + "_name").focus();
					$("#" + formName + "_name-error").text("Please enter a category name");
					return;
				}

			//	if (formData.name.replace(/[a-z0-9\(\)\&\,\-\_\s]/gi, '')) {
				if (!printable(formData.name)) {
			
					$("#" + formName + "_name").focus();
					$("#" + formName + "_name-error").text("Invalid characters in category name");
					return;
				}

				if (!formData.display_name) {
			
					$("#" + formName + "_display_name").focus();
					$("#" + formName + "_display_name-error").text("Please enter a category display name");
					return;
				}

			//	if (formData.display_name.replace(/[a-z0-9\(\)\&\,\-\_\s]/gi, '')) {
				if (!printable(formData.display_name)) {
			
					$("#" + formName + "_display_name").focus();
					$("#" + formName + "_display_name-error").text("Invalid characters in category display name");
					return;
				}

			//	if (!formData.primary_parent_cat_id) {
			//
			//		$primary_parent_cat_SearchString.focus();
			//		$primary_parent_cat_id_error.text("Please select a primary category");
			//		return;
			//	}

			//	if (!formData.facet_id_pkg_cnt) {
			//
			//		$facet_id_pkg_cnt_SearchString.focus();
			//		$facet_id_pkg_cnt_error.text("Please select a facet package count");
			//		return;
			//	}

				if (!isNullOrEmpty(formData.suggested_qty) && formData.suggested_qty.replace(/[0-9]/g,'')) {

					$("#" + formName + "_suggested_qty").focus();
					$("#" + formName + "_suggested_qty-error").text("Please enter a integer quantity");
					return;
				}

			//	if (!isNullOrEmpty(formData.service_type_cd)) {
			//
			//		$("#" + formName + "_service_type_cd").focus();
			//		$("#" + formName + "_service_type_cd-error").text("Please enter a service type code");
			//		return;
			//	}

				if (!isNullOrEmpty(formData.service_type_cd) && formData.service_type_cd.replace(/[a-zA-Z0-9\_]/g,'')) {

					$("#" + formName + "_service_type_cd").focus();
					$("#" + formName + "_service_type_cd-error").text("Please enter an alpha numeric code");
					return;
				}

				if (!isNullOrEmpty(formData.threshold_amt) && isNaN(parseFloat(formData.threshold_amt, 10))) {

					$("#" + formName + "_threshold_amt").focus();
					$("#" + formName + "_threshold_amt-error").text("Please enter a numeric quantity");
					return;
				}

				if (!isNullOrEmpty(formData.threshold_qty) && formData.threshold_qty.replace(/[0-9]/g,'')) {

					$("#" + formName + "_threshold_qty").focus();
					$("#" + formName + "_threshold_qty-error").text("Please enter a integer quantity");
					return;
				}

				if (action == "insert") {

					url = global.config.api.BBB_LIST_CATEGORY_createCategory;

					if (node.type == "list" || node.type == "xlist") {
						formData.list_id = node.data.list_id;
					}
					else if (node.type == "categories") {
						formData.parent_list_cat_id = node.data.list_cat_id;
					}

				}
				else if (action == "update") {

					url = global.config.api.BBB_LIST_CATEGORY_editCategory

					formData.list_cat_id = id;
				}

				formData.create_user = "AdminUI";
      				formData.last_mod_user= "AdminUI";

				$.ajax({
					url: url,
					type: 'POST',
					data: formData,
					dataType: 'json',
                        		success: function (result) {

						if (serverError(result, false, true)) {

							return;
						}

                            			// server-side validation
						if (!result || result.status == "error") {

							$form_Error.text(getServerMessageText(result));

							return;
						}

						var xnode = node;
						var rnode = node;

						if (action == "update") {
							xnode = tree.get_node(node.parent);
							rnode = tree.get_node("lists");
						}

						tree.refresh_node(rnode, function() {
							tree.open_node(xnode);
						});

						$(that).dialog("close");
                    			},
                    			error: function (xhr, status, error) {

						showAjaxError(xhr, status, error);
                    			},
                    			async: true
                    		});

                            }
                        }],
                open: function (event, ui) {

			$(event.target).parent().css('position', (global.config.dialogFixed?'fixed':'absolute'));

			$form.unbind("keydown").keydown(function(e) {

    				if (e.keyCode == $.ui.keyCode.ENTER) {

					event.preventDefault();

        				$("#" + formName + "-ok-button").click();
				}
			});

			// $(this).parent().css("overflow", "visible"); 

			$("#" + formName + "_primary_parent_cat_SearchField1, " + "#" + formName + "_primary_parent_cat_SearchField2").click(function() {

				$primary_parent_cat_SearchString.attr({placeholder:"type " + this.value + " to search, <space> for all"}).focus().val("");
			});

			$primary_parent_cat_SearchField1.trigger("click");

			$("#" + formName + "_primary_parent_cat-remove").click(function() {

				$primary_parent_cat_SelectedFieldset.css({color:"silver","background-color":""}).prop("disabled", true);
				$primary_parent_cat_Selected.text("");
				$primary_parent_cat_id.val("");

				event.stopPropagation();
				event.preventDefault();
			});

			$primary_parent_cat_SearchString.autocomplete({

				source: function (request, response) {

					var url = "";

					var isKey = !$primary_parent_cat_SearchField1[0].checked;

					var keyField = "list_cat_id";
					var displayField = "display_name";

					var searchField = $primary_parent_cat_SearchField1[0].checked?displayField:keyField;
					var searchString = $primary_parent_cat_SearchString.val();

        				var params = {};

					params[searchField] = searchString;
					if (global.config.exactSearch) params[searchField] += (isKey?"%":"");

					url = global.config.api.BBB_LIST_CATEGORY_searchByName;

					$("body").css("cursor", "progress");

					$.ajax({
						url: url,
						type: "POST",
						data: params,
						dataType: "json",
						dataFilter: function (data) { return data; },
						success: function (result) {

							$("body").css("cursor", "default");

							if (serverError(result, true)) {

								return;
							}

							if (result.status != "ok") {

								return;
							}

							var data = result.data;

							response($.map(data, function (item) {

								item[searchField] = new String(item[searchField]);

				    			//	if (item[searchField].substr(0, params[searchField].length).toLowerCase() == params[searchField].toLowerCase()) {

                                    					item.id = item[keyField];
									item.label =  item[keyField] + ": " + item[displayField];
									return item;
				    			//	}
                                			}))
                            			},
                            			error: function (xhr, status, error) {

							$("body").css("cursor", "default");

							showAjaxError(xhr, status, error);
                            			}
                        		});

				},
				select: function(event, ui) {

					event.preventDefault();

					$primary_parent_cat_SelectedFieldset.css({color:"","background-color":"#F0F0F0"}).prop("disabled", false);
					$primary_parent_cat_Selected.text(ui.item.label);
					$primary_parent_cat_SearchString.val("");
					$primary_parent_cat_id_error.text("");
					$primary_parent_cat_id.val(ui.item.id);

				},
        			open: function(event, ui) {
				//	prevent DOM elements from overlapping autocomplete list
            				$(".ui-autocomplete").css({"z-index":1000,"overflow-y":"auto","overflow-x":"hidden","padding-right":"20px","max-height":"200px"});
        			}
			});



			$("#" + formName + "_facet_id_pkg_cnt_SearchField1, " + "#" + formName + "_facet_id_pkg_cnt_SearchField2").click(function() {

				$facet_id_pkg_cnt_SearchString.attr({placeholder:"type " + this.value + " to search, <space> for all"}).focus().val("");
			});

			$facet_id_pkg_cnt_SearchField1.trigger("click");

			$("#" + formName + "_facet_id_pkg_cnt-remove").click(function() {

				$facet_id_pkg_cnt_SelectedFieldset.css({color:"silver","background-color":""}).prop("disabled", true);
				$facet_id_pkg_cnt_Selected.text("");
				$facet_id_pkg_cnt.val("");

				event.stopPropagation();
				event.preventDefault();
			});

			$facet_id_pkg_cnt_SearchString.autocomplete({

				source: function (request, response) {

					var url = "";

					var isKey = !$facet_id_pkg_cnt_SearchField1[0].checked;

					var keyField = "facet_id";
					var displayField = "description";

					var searchField = $facet_id_pkg_cnt_SearchField1[0].checked?displayField:keyField;
					var searchString = $facet_id_pkg_cnt_SearchString.val();

        				var params = {};

					params[searchField] = searchString;
					if (global.config.exactSearch) params[searchField] += (isKey?"%":"");

					url = global.config.api.BBB_FACET_TYPES_select;

					$("body").css("cursor", "progress");

					$.ajax({
						url: url,
						type: "POST",
						data: params,
						dataType: "json",
						dataFilter: function (data) { return data; },
						success: function (result) {

							$("body").css("cursor", "default");

							if (serverError(result, true)) {

								return;
							}

							if (result.status != "ok") {

								return;
							}

							var data = result.data;

							response($.map(data, function (item) {

								item[searchField] = new String(item[searchField]);

				    			//	if (item[searchField].substr(0, params[searchField].length).toLowerCase() == params[searchField].toLowerCase()) {

                                    					item.id = item[keyField];
									item.label =  item[keyField] + ": " + item[displayField];
									return item;
				    			//	}
                                			}))
                            			},
                            			error: function (xhr, status, error) {

							$("body").css("cursor", "default");

							showAjaxError(xhr, status, error);
                            			}
                        		});

				},
				select: function(event, ui) {

					event.preventDefault();

					$facet_id_pkg_cnt_SelectedFieldset.css({color:"","background-color":"#F0F0F0"}).prop("disabled", false);
					$facet_id_pkg_cnt_Selected.text(ui.item.label);
					$facet_id_pkg_cnt_SearchString.val("");
					$facet_id_pkg_cnt_error.text("");
					$facet_id_pkg_cnt.val(ui.item.id);

				},
        			open: function(event, ui) {
				//	prevent DOM elements from overlapping autocomplete list
            				$(".ui-autocomplete").css({"z-index":1000,"overflow-y":"auto","overflow-x":"hidden","padding-right":"20px","max-height":"200px"});
        			}
			});

			$category_name.trigger("focus");

                },
                close: function () {
			$(this).dialog("close");
                }
	};

	dialog_config.position = dialog_fixed_position;

	if (action == "update" && record && !record.is_deleted) {
		dialog_config.buttons.splice(1, 0, {

			text: "Delete",
			click: function () {

				that = this;

			//	$('#confirmDialog-message').text("Are You Sure?");
				$('#confirmDialog-message').html("<p>Delete category</p><ul class=\"" + node.type + "\"><li style=\"cursor:hand\"><b>" + node.text + "</b></li></ul><p>Are You Sure?</p>");
				global.$confirmDialog.dialog({
  					//dialogClass: "no-close",
					modal: true,
  					title:"Delete",
  					buttons: [
    					{
      						text: "Cancel",
						click: function() {
        						$( this ).dialog("close");
						}
					},
					{
      						text: "Ok",
						click: function() {

							that2 = this;

							$.ajax({
								url: global.config.api.BBB_LIST_CATEGORY_deleteCategory,
								type: "POST",
								data: {list_cat_id:id},
								dataType: "json",
								dataFilter: function (data) { return data; },
								success: function (result) {

									if (serverError(result, true)) {

										return;
									}

									if (result.status != "ok") {

										return;
									}

									tree.refresh_node(node.parent);

									$( that ).dialog("close");
									$( that2 ).dialog("close");
									
                            					},
                            					error: function (xhr, status, error) {

									showAjaxError(xhr, status, error);
                            					}
                        				});
						}
					}]
				}).dialog('open');
			}
		});
	}

	dialog_config.position = dialog_fixed_position;

	$form.dialog(dialog_config).dialog('open');
}

function createRule(node, option) {

	var tree = global.$list_tree.jstree(true);

	var d = global.config.delim;

	var xtypes = {
		"rules":{"type":"rule","prefix":"r"}, 
		"eph_node":{"type":"eph_rule","prefix":"er"},
		"jda_category":{"type":"jda_rule","prefix":"jr"}
	}

	node.data.load_empty = true;

	tree.open_node(node, function() {

		var id = "n" + (new Date()).getTime();

		tree.create_node(node, {type:xtypes[node.type].type, "id":id, text:global.config.defaultNodeName, data:{_pending:1}}, (global.config.newNodeTop?'first':'last'), null, true);

		node.data.load_empty = false;

		ruleFormX(tree.get_node(id), "insert");		
	});
}

function renameRule(node, option) {

	ruleFormX(node, "update", null);
}

function ruleFormX(node, action, record) {

	var tree = global.$list_tree.jstree(true);
	var pnode = [];	
	var url;
	var postData;
	var d = global.config.delim;

	var xtypes = {
		"rules":{"type":"rule","prefix":"r"}, 
		"eph_node":{"type":"eph_rule","prefix":"er"},
		"jda_category":{"type":"jda_rule","prefix":"jr"}
	};

	var nodeName = $(node.text).text();

	if (action == "insert") {

		nodeName = global.config.defaultNodeName;
	}

	tree.edit(node, nodeName, function(n) {

		if (n.text.replace(/[a-z0-9\-\_\s]/gi, '')) {

			return;
		}

		postData = {};
		pnode.push(tree.get_node(node.parents[0]));
		pnode.push(tree.get_node(node.parents[1]));

		var xnode = node;

		if (node.type == "eph_rule") {
			url = global.config.api.BBB_LIST_RULES_EPH_CAT_update;
			postData.list_cat_id = pnode[0].data.list_cat_id || "";
			postData.rule_id = pnode[0].data.rule_id || "";
			postData.eph_node_id = pnode[0].data.eph_node_id || "";
			xnode = tree.get_node(node.parents[1]);
		}
		else if (node.type == "jda_rule") {
			url = global.config.api.BBB_LIST_RULES_JDA_CAT_update;
			postData.list_cat_id = pnode[0].data.list_cat_id || "";
			postData.rule_id = pnode[0].data.rule_id || "";
			postData.jda_dept_id = pnode[0].data.jda_dept_id || "";
			postData.jda_sub_dept_id = pnode[0].data.jda_sub_dept_id || "";
			postData.jda_class = pnode[0].data.jda_class || "";
			xnode = tree.get_node(node.parents[1]);
		}
		else {

			if (action == "insert") {
				url = global.config.api.BBB_LIST_RULES_FACET_CAT_insert;
				postData.list_cat_id = pnode[0].data.list_cat_id || "";
			}
			else
			{
				url = global.config.api.BBB_LIST_RULES_FACET_CAT_update;
				postData.list_cat_id = pnode[0].data.list_cat_id || "";
				postData.rule_id = node.data.rule_id || "";
			}
			xnode = tree.get_node(node.parent);
		}

		postData.facet_rule_name = node.text;

		if (action == "insert") {
			postData.sequence_num = (global.config.newNodeTop?0:999999);
		}
		else if (action == "update") {
			postData.facet_value_pair_list = node.data.facet_value_pair_list || "";
		}

		$.ajax({
			url: url,
			type: "POST",
			data: postData,
			dataType: "json",
			success: function (result) {

				if (serverError(result, true)) {

					return;
				}

				if (result.status != "ok") {

					return;
				}

				tree.refresh_node(xnode, function() {
					tree.open_node(node.parent);
				});	
              		},
			error: function (xhr, status, error) {

				showAjaxError(xhr, status, error);
			}
		});

	});
}

function editRule(node, option) {

	ruleForm(node, "update", {"facet_rule_name":$(node.text).text()});
}

function ruleForm(node, action, record) {

	var tree = global.$list_tree.jstree(true);	

	var d = global.config.delim;		

	var keys = getNodeKeys(node.id);
	var url = null;
	var title = "";
	var formName = "ruleForm";
	var id = 0;
	var $form = null;
	var action = action || "insert";
	var data = {};
	var pnode = [];	
	var postData = {};

	if (action == "insert") {
		title = "Create Facet Rule";
	}
	else if (action == "update") {
		title = "Edit Facet Rule";
		id = node.data.rule_id;
	}

	if (!global["$" + formName]) {

		global["$" + formName] = $("#" + formName);

	}
	$form = global["$" + formName];

	var $facet_rule_name = $("#" + formName + "_facet_rule_name");
	var $facet_rule_name_error = $("#" + formName + "_facet_rule_name-error");

	var $form_Error = $("#" + formName + "_error");
	var $form_Message = $("#" + formName + "_message");

	$form_Error.text("");
	$form_Message.text("");

	resetForm($form[0]);
	$form[0].reset();

	if (record) {

		deserializeForm($form[0], record, formName + "_");
	}

	var dialog_config = {
		autoOpen: false,
                modal: true,
                title: title,
                buttons:
                        [{
                            text: "Cancel",
                            click: function () {

                                $(this).dialog("close");
                            }
                        }, 
			{
                            text: "Ok",
			    id: formName + "-ok-button",
                            click: function () {

				var that = this;

				var formData = serializeForm($form[0], formName + "_"); 

				for (var p in formData) {

					if (typeof(formData[p]) == "string") {
						formData[p] = formData[p].trim();
					}
				}

				resetForm($form[0]);

				if (!formData.facet_rule_name) {

					$facet_rule_name.focus();

					$facet_rule_name_error.text("Please enter a name");
					return;
				}

			//	if (formData.facet_rule_name.replace(/[a-z0-9\-\_\s]/gi, '')) {
				if (!printable(formData.facet_rule_name)) {
			
					$facet_rule_name.focus();
			
					$facet_rule_name_error.text("Invalid characters in name");
					return;
				}

				postData = {};

				postData.facet_rule_name = formData.facet_rule_name;

				if (action == "update") {
					postData.facet_value_pair_list = node.data.facet_value_pair_list || "";
				}

				pnode.push(tree.get_node(node.parents[0]));
				pnode.push(tree.get_node(node.parents[1]));

				var onode = node;
				var snode = node;
				var rnode = node;

				if (node.type == "eph_node") {
					if (node.children && node.children.length) {
						url = global.config.api.BBB_LIST_RULES_EPH_CAT_insert;
						postData.sequence_num = (global.config.newNodeTop?0:999999);
						postData.sequence_num = node.data.sequence_num + (global.config.newNodeTop?0:1);
					}
					else {
						url = global.config.api.BBB_LIST_RULES_EPH_CAT_update;
						postData.rule_id = node.data.rule_id || "";
					}
					postData.list_cat_id = node.data.list_cat_id || "";
					postData.eph_node_id = node.data.eph_node_id || "";
					rnode = tree.get_node(node.parents[0]);
				}
				else if (node.type == "eph_rule") {
					url = global.config.api.BBB_LIST_RULES_EPH_CAT_update;
					postData.list_cat_id = pnode[0].data.list_cat_id || "";
					postData.rule_id = pnode[0].data.rule_id || "";
					postData.eph_node_id = pnode[0].data.eph_node_id || "";
					rnode = tree.get_node(node.parents[1]);
					onode = rnode;
				}
				else if (node.type == "jda_category") {
					if (node.children && node.children.length) {
						url = global.config.api.BBB_LIST_RULES_JDA_CAT_insert;
						postData.sequence_num = (global.config.newNodeTop?0:999999);
						postData.sequence_num = node.data.sequence_num + (global.config.newNodeTop?0:1);
					}
					else {
						url = global.config.api.BBB_LIST_RULES_JDA_CAT_update;
						postData.rule_id = node.data.rule_id || "";
					}
					postData.list_cat_id = node.data.list_cat_id || "";
					postData.jda_dept_id = node.data.jda_dept_id || "";
					postData.jda_sub_dept_id = node.data.jda_sub_dept_id || "";
					postData.jda_class = node.data.jda_class || "";
					rnode = tree.get_node(node.parents[0]);
				}
				else if (node.type == "jda_rule") {
					url = global.config.api.BBB_LIST_RULES_JDA_CAT_update;
					postData.list_cat_id = pnode[0].data.list_cat_id || "";
					postData.rule_id = pnode[0].data.rule_id || "";
					postData.jda_dept_id = pnode[0].data.jda_dept_id || "";
					postData.jda_sub_dept_id = pnode[0].data.jda_sub_dept_id || "";
					postData.jda_class = pnode[0].data.jda_class || "";
					rnode = tree.get_node(node.parents[1]);
					onode = rnode;
				}
				else if (node.type == "rules") {
					url = global.config.api.BBB_LIST_RULES_FACET_CAT_insert;
					postData.list_cat_id = node.data.list_cat_id || "";
					postData.sequence_num = (global.config.newNodeTop?0:999999);
				}
				else if (node.type == "rule") {
					url = global.config.api.BBB_LIST_RULES_FACET_CAT_update;
					postData.list_cat_id = pnode[0].data.list_cat_id || "";
					postData.rule_id = node.data.rule_id || "";
					rnode = tree.get_node(node.parent);
					onode = rnode;
				}
				else {
					return;
				}

				$.ajax({
					url:url,
					type: 'POST',
					data: postData,
					dataType: 'json',
                        		success: function (result) {

						if (serverError(result, false, true)) {

							return;
						}

                            			// server-side validation
						if (!result || result.status == "error") {

							$form_Error.text(getServerMessageText(result));

							return;
						}

						var newRecord = (result.data||[])[0];

						if (!newRecord) {

							$form_Error.text(action + " failed - no data returned");
							return;
						}

						if (node.type == "eph_node") {
							if (node.children && node.children.length) {
								snode = rnode.id + d + "e" + d + newRecord.rule_id;
							//	snode = rnode.id + d + "e2" + d + newRecord.rule_id;
								onode = snode;
							}
						}

						if (node.type == "jda_category") {
							if (node.children && node.children.length) {
								snode = rnode.id + d + "j" + d + newRecord.rule_id;
							//	snode = rnode.id + d + "j2" + d + newRecord.rule_id;
								onode = snode;
							}
						}

						tree.refresh_node(rnode, function() {
							tree.deselect_node(node);
							tree.select_node(snode);
							tree.open_node(onode);
						});

						$(that).dialog("close");
                    			},
                    			error: function (xhr, status, error) {

						showAjaxError(xhr, status, error);
                    			},
                    			async: true
                    		});

                            }
                        }],
                open: function (event, ui) {

			$(event.target).parent().css('position', (global.config.dialogFixed?'fixed':'absolute'));

			$form.unbind("keydown").keydown(function(e) {

    				if (e.keyCode == $.ui.keyCode.ENTER) {

					event.preventDefault();

        				$("#" + formName + "-ok-button").click();
				}
			});

			setTimeout(function() {$facet_rule_name.focus()}, 100);

                },
                close: function () {
			$(this).dialog("close");
                }
	};

	dialog_config.position = dialog_fixed_position;

	$form.dialog(dialog_config).dialog('open');
}

function editSKU(node, option) {

	var tree = global.$list_tree.jstree(true);

	var xinclude = (option.item.label.toLowerCase() == "include");

	var postData = {};

	postData.list_cat_id = node.data.list_cat_id;
	postData.rule_id = node.data.rule_id;
	postData.sku_id = node.data.sku_id;
	postData.rule_evaluation_cd = (xinclude?"IN":"EX");

	var url = global.config.api.BBB_LIST_RULES_SKU_CAT_update;

	$.ajax({
		url: url,
		type: "POST",
		data: postData,
		dataType: "json",
		success: function (result) {

			if (serverError(result, true)) {

				return;
			}

			if (result.status != "ok") {

				return;
			}

		//	tree.set_type(node, (xinclude?"sku":"xsku"));
			tree.refresh_node(node.parent);
		},
		error: function (xhr, status, error) {

			showAjaxError(xhr, status, error);
		}
	});
}

function addFacet(facet_value_pair_list, facet_id, facet_value_id) {

	facet_value_pair_list = removeFacet(facet_value_pair_list, facet_id, facet_value_id);

	if (!facet_id || !facet_value_id) {
		return facet_value_pair_list;
	}
	return facet_value_pair_list + (facet_value_pair_list?",":"") + facet_id + ":" + facet_value_id;
}

function removeFacet(facet_value_pair_list, facet_id, facet_value_id) {

	facet_value_pair_list = facet_value_pair_list || "";

	if (!facet_id || !facet_value_id) {
		return facet_value_pair_list;
	}

	var pair = facet_id + ":" + facet_value_id;
	var pairs = facet_value_pair_list.split(",");
	facet_value_pair_list = "";

	for (var i=0; i < pairs.length; i++) {
		if (!pairs[i]) continue;
		if (pairs[i] == pair) continue;
		if (facet_value_pair_list) facet_value_pair_list += ",";
		facet_value_pair_list += pairs[i];
	}
	return facet_value_pair_list;
}

function propForm(node) {

	var tree = global.$list_tree.jstree(true);	

	var d = global.config.delim;		

	var keys = getNodeKeys(node.id);
	var url = null;
	var title = "";
	var formName = "propForm";
	var id = 0;
	var $form = null;
	var data = {};
	var pnode = [];	
	var postData = {};
	var xnode = null;
	var id = node.id;

	title = "Properties";

	if (!global["$" + formName]) {

		global["$" + formName] = $("#" + formName);

	}
	$form = global["$" + formName];

	var $form_Error = $("#" + formName + "_error");
	var $form_Message = $("#" + formName + "_message");

	$form_Error.text("");
	$form_Message.text("");

	var dialog_config = {
		autoOpen: false,
                modal: true,
                title: title,
		"resize":"auto",
		"width":"350px",
                buttons:
                        [{
                            text: "Ok",
			    id: formName + "-ok-button",
                            click: function () {

				$(this).dialog("close"); 

                            }
                        }],
                open: function (event, ui) {

			$(event.target).parent().css('position', (global.config.dialogFixed?'fixed':'absolute'));

			$form.unbind("keydown").keydown(function(e) {

    				if (e.keyCode == $.ui.keyCode.ENTER) {

					event.preventDefault();

        				$("#" + formName + "-ok-button").click();
				}
			});

                },
                close: function () {
			$(this).dialog("close");
                }
	};

	dialog_config.position = dialog_fixed_position;

	switch(node.type) {
		case "list":
		case "xlist":
		case "category":
		case "xcategory":
		case "dcategory":
		case "dxcategory":
		case "eph_node":
		case "jda_category":
		case "rule":
			xnode = tree.get_node(node.parent);
			break;
		case "sku":
		case "xsku":
		case "facet":
		case "eph_rule":
		case "jda_rule":
			xnode = tree.get_node(node.parents[1]);
			break;
		case "eph_facet":
		case "jda_facet":
			xnode = tree.get_node(node.parents[2]);
			break;
		default:
			xnode = node;
			break;
	}

	if (global.config.showPropPage == 2) {
		tree.refresh_node(xnode, showPage);
	}
	else {
		showPage();
	}

	function showPage() {

		if (global.config.showPropPage == 2) {
			node = tree.get_node(id);
		}

		pnode.push(tree.get_node(node.parents[0]));
		pnode.push(tree.get_node(node.parents[1]));

		$("#" + formName + "_info_ul").attr({"class":node.type});
		$("#" + formName + "_info_li").html(node.text);
		$("#" + formName + "_id").text(keys.pop().value);
		$("#" + formName + "_create_user").text(node.data.create_user || pnode[0].data.create_user || pnode[1].data.create_user);
		$("#" + formName + "_create_date").html(node.data.create_date || pnode[0].data.create_date || pnode[1].data.create_date);
		$("#" + formName + "_last_mod_user").text(node.data.last_mod_user || pnode[0].data.last_mod_user || pnode[1].data.last_mod_user);
		$("#" + formName + "_last_mod_date").text(node.data.last_mod_date || pnode[0].data.last_mod_date || pnode[1].data.last_mod_date);

		$form.dialog(dialog_config).dialog('open');
	}
}

function openCategoryURL(id, site_flag, isMobile) {

	var $element = $('#' + id);
	var $link = $('#' + id + "_link");
	var url = $element.val() || $link.attr("title") || "";
	url = url.trim();
	if (!url) return false;
	if (url.replace(/^([A-Za-z]+\:)?\/\//, '') == url) {
		url = getChannelURL(site_flag, isMobile) + (url.charAt(0)!="/"?"/":"") + url;
	}
	window.open(url, 'categoryURL');
	return false;
}

function revertCategoryURL(id) {

	var $element = $('#' + id);
	$element.val('');
	return false;
}

function getChannelURL(site_flag, isMobile) {

	var scheme = "https://";
	var subdomain = "www.";
	var domain = "bedbathandbeyond.com";
	var path = "/store/checklist";

	site_flag = site_flag || 1;

	switch(site_flag) {
	case 8:
		domain = global.config.domain8 || "tbsbbbyca.bbbystore.com";
		subdomain = "";
		path = "/tbs/checklist";
		break;
	case 7:
		domain = global.config.domain7 || "tbsbaby.bbbystore.com";
		subdomain = "";
		path = "/tbs/checklist";
		break;
	case 6:
		domain = global.config.domain6 || "tbsbbby.bbbystore.com";
		subdomain = "";
		path = "/tbs/checklist";
		break;
	case 5:
		domain = global.config.domain5 || "bedbathandbeyond.mx";
		break;
	case 4:
		domain = global.config.domain4 || "bedbathandbeyond.com";
		break;
	case 3:
		domain = global.config.domain3 || "bedbathandbeyond.ca";
		break;
	case 2:
		domain = global.config.domain2 || "buybuybaby.com";
		break;
	case 1:
	default:
		domain = global.config.domain1 || "bedbathandbeyond.com";
		break;
	}

	if (isMobile) {
		subdomain = "m."
		path = "/m/checklist";
	}

	return scheme + subdomain + domain + path;
}

function getCategoryPath(tree, node) {

	var path = [];
	var xnode;

	for (var i=-1; node && i < node.parents.length; i++) {

		xnode = (i == -1)?node:tree.get_node(node.parents[i]);

		switch(xnode.type) {
		case "list":
		case "xlist":
		case "category":
		case "xcategory":
		case "dcategory":
		case "dxcategory":
			break;
		default:
			continue;
			break;
		}

		if (!xnode.data) {
			continue;
		}	

		if (xnode.type == "category" || xnode.type == "xcategory" || xnode.type == "dcategory" || xnode.type == "dxcategory") {
			if (path.length == 0) {

				if (xnode.data.child_list_cat_id || xnode.data.list_cat_id) {
					path.push("/");
					path.push(xnode.data.child_list_cat_id || xnode.data.list_cat_id);
				}
			}
		}

		if (xnode.data.display_name) {
			path.unshift(formattedDisplayName(xnode.data.display_name));
			path.unshift("/");
		}

		if (xnode.type == "list" || xnode.type == "xlist") {

			if (xnode.data.list_id) {
				path.push("/");
				path.push(xnode.data.list_id);
			}
		}
	}

	return (path.join("") || "").toLowerCase();
}

function formattedDisplayName(displayName) {

	if (!displayName) return "";

	var entities = "&trade;,&amp;,&lt;,&gt;,&quot;,&ndash;,&mdash;,&ensp;,&emsp;,&nbsp;,&shy;,&reg;&#58;,&#63;".split(",");

	for (var i=0; i < entities.length; i++) {
		displayName = displayName.replaceAll(entities[i], "", true);
	}

	displayName = displayName.replace(/[^a-zA-Z0-9]+/g, "-");

	// remove the first and last hyphen
	displayName = displayName.replace(/^\-|\-$/g, "");

	return displayName;
}
