function listTreeConfig($tree) {

$tree.jstree({
	"core" : {
		"animation" : 0,
		"check_callback" : function (op, node, par, pos, more) {
			if((op === "move_node" || op === "copy_node") && node.parent !== par.id) { 
				return false; 
			}
			return true;
		},

		"data":function (node, cb) {

			var d = global.config.delim;

			var data = {};
			var xnode = {};
			var id = null;

		//	if (!ping(true)) return;

		//	console.log(node);

			if(node.id === "#") {

				// hard coded node
				cb([{"id":"lists", "type":"root", "text":"Lists", "children" : true}]);
				return;
			}

			if(node.type === "category" || node.type === "xcategory" || node.type === "dcategory" || node.type === "dxcategory") {

				var list_cat_id = node.data.child_list_cat_id||node.data.list_cat_id;

				// hard coded node
				cb([{"id":node.id + d + "c" + d + "", "data":{"list_cat_id":list_cat_id,"xname":"categories"}, "type":"categories", "text":"Categories", "children" : true},
				    {"id":node.id + d + "s" + d + "", "data":{"list_cat_id":list_cat_id,"xname":"skus"}, "type":"skus", "text":"SKU", "children" : true},
				    {"id":node.id + d + "r" + d + "", "data":{"list_cat_id":list_cat_id,"xname":"rules"}, "type":"rules", "text":"Facet", "children" : true},
				    {"id":node.id + d + "e" + d + "", "data":{"list_cat_id":list_cat_id,"xname":"eph"}, "type":"eph", "text":"EPH", "children" : true },
				    {"id":node.id + d + "j" + d + "", "data":{"list_cat_id":list_cat_id,"xname":"jda"}, "type":"jda", "text":"JDA", "children" : true}
				]);

				return;
			}

			// lazy load all other nodes

			var url = "";
			var postData = {};

		   	// set the url based on the node.type
			
			var keys = getNodeKeys(node.id);
			
			if (node.type === "root") {
				postData.site_id = global.state.site_id;
				postData.site_flag = global.state.site_flag;
				url = appendQS(global.config.api.BBB_LIST_getSiteLists, "site_flag=" + postData.site_flag + "&site_id=" + postData.site_flag);
			}
			else if (node.type === "list" || node.type === "xlist") {
				postData.list_id = node.data.list_id;
				url = appendQS(global.config.api.BBB_LIST_CHLDCAT_getCategories, "list_id=" + postData.list_id);							
			}
			else if (node.type === "categories") {
				postData.list_cat_id = node.data.list_cat_id;
				url = appendQS(global.config.api.BBB_LIST_CAT_CHLDCAT_getChildCategories, "list_cat_id=" + postData.list_cat_id);
			}
			else if (node.type === "skus") {
				postData.list_cat_id = node.data.list_cat_id;
				url = appendQS(global.config.api.BBB_LIST_RULES_SKU_CAT_select, "list_cat_id=" + postData.list_cat_id);
			}
			else if (node.type === "rules") {
				postData.list_cat_id = node.data.list_cat_id;
				url = appendQS(global.config.api.BBB_LIST_RULES_FACET_CAT_select, "list_cat_id=" + postData.list_cat_id);
			}
			else if (node.type === "eph") {
				postData.list_cat_id = node.data.list_cat_id;
				url = appendQS(global.config.api.BBB_LIST_RULES_EPH_CAT_select, "list_cat_id=" + postData.list_cat_id);
			}
			else if (node.type === "jda") {
				postData.list_cat_id = node.data.list_cat_id;
				url = appendQS(global.config.api.BBB_LIST_RULES_JDA_CAT_select, "list_cat_id=" + postData.list_cat_id);

			} else {
				cb(false);
				return;
			}

			$.ajax({
				url: url,
				type:'POST',
				data:postData,
				dataType: 'json',
				success: function (result) {

				//	result = "{\"status\":\"ok\",\"data\":[]}";

					if (result && typeof(result) == "string") {
						result = JSON.parse(result);
					}
					if (serverError(result)) {
						cb(false);
						return;
					}
					if (!result) {
						cb(false);
						return;
					}
					if (result.status != "ok") {
				//		cb(false);
				//		return;
					}
					if (!result.data) {
						cb(false);
						return;
					}
					if (!result.data.length) {

						if (global.config.load_empty || node.data && node.data.load_empty) {
							cb([]);
							return;
						}
						else {
							cb(false);
							return;
						}
					}

					// reformat the data here using the map function
					// based on the node.type

					cb($.map(result.data, function (item) {

						var id = null, pNode = null, cNode = null, newNode = null, xtype;

						if (node.type === "root") {

							item.xname = "bbb_list";

							return {
								"id":"l" + d + item.list_id,
								"type":(item.is_disabled?"xlist":"list"),
								"text":"<span title=\"" + item.list_id + "\">" + htmlEncode(item.display_name) + "</span>",
								"data":item,
								"children":true
							};	
						}
						else if (node.type === "list" || node.type === "xlist") {

							item.xname = "bbb_list_chldcat";

							xtype = "category";
							if (item.is_disabled) xtype = "x" + xtype;
							if (item.is_deleted) xtype = "d" + xtype;
					
							return {
								"id":node.id + d + "c" + d + item.list_cat_id,
								"type":xtype,
								"text":"<span title=\"" + item.list_cat_id + "\">" + htmlEncode(item.display_name) + "</span>",
								"data":item,
								"children":true
							};
						}
						else if (node.type === "categories") {

							item.xname = "bbb_list_cat_chldcat";

							xtype = "category";
							if (item.is_disabled) xtype = "x" + xtype;
							if (item.is_deleted) xtype = "d" + xtype;

							return {
								"id":node.id + d + "c" + d + item.child_list_cat_id,
								"type":xtype,
								"text":"<span title=\"" + item.child_list_cat_id + "\">" + htmlEncode(item.display_name) + "</span>",
								"data":item,
								"children":true
							};
						
						}
						else if (node.type === "skus") {

							item.xname = "bbb_list_cat_sku";
							item._root = node.id;

							return {
								"id":node.id + d + "sr" + d + item.rule_id,
								"type":(item.rule_evaluation_cd == "IN"?"sku":"xsku"),
								"text":"<span title=\"" + item.rule_id + "\">" + (item.sku_id == item.display_name?htmlEncode(item.display_name):htmlEncode(item.sku_id + ": " + item.display_name)) + "</span>",
								"data":item,
								"children":false
							};
						
						}
						else if (node.type === "rules") {

							id = node.id + d + "r" + d + item.rule_id;
							newNode = {
								"id":id,
								"type":"rule",
								"text":"<span title=\"" + item.rule_id + "\">" + htmlEncode(item.facet_rule_name) + "</span>",
								"data":item,
								"children":[]
							};
							newNode.data._root = node.id;

							item.facets = item.facets || item.facet_value_pair_list;

							if (!item.facets || typeof(item.facets) == "string") {

								item.facets = splitFacets(item.facets);
							}

							for (var i=0; i < item.facets.length; i++) {
								item.facets[i]._root = node.id;
								item.facets[i]._rule = newNode.id;
								item.facets[i]._facetrule = newNode.id;
								item.facets[i].rule_id = item.rule_id;
								newNode.children.push({"id":newNode.id + d + "f" + d + item.facets[i].facet_id + "x" + item.facets[i].facet_value_id,"data":item.facets[i], "type":"facet", text:"<span title=\"" + item.facets[i].facet_id + "\">" + htmlEncode(item.facets[i].facet_name) + "</span><span style=\"color:blue;font-weight:bold;font-size:1.3em\">:</span>&nbsp;<span title=\"" + item.facets[i].facet_value_id + "\">" + htmlEncode(item.facets[i].facet_value_name) + "</span>"});
							}

							return newNode;
						}
						else if (node.type === "eph") {

							item._root = node.id;

							id = node.id + d + "e" + d + item.rule_id;
						//	id = node.id + d + "e2" + d + item.rule_id;

							var display_name = "<span title=\"" + item.eph_node_id + "\">" + htmlEncode(item.eph_display_name) + "</span>";

							if (!item.eph_nodes || typeof(item.eph_nodes) == "string") {

								item.eph_nodes = splitEPHNodes(item.eph_nodes);
							}

							if (item.eph_nodes && item.eph_nodes.length) {
								display_name = "";
								for (var i=0; i < item.eph_nodes.length; i++) {
									display_name += " <span title=\"" + item.rule_id + "\" style=\"color:green;font-weight:bold\">/</span> ";
									display_name += "<span title=\"" + item.eph_nodes[i].eph_node_id + "\">" + htmlEncode(item.eph_nodes[i].display_name) + "</span>";
								}
							}
							newNode = {
								"id":id,
								"type":"eph_node",
								"text":display_name,
								"data":item,
								"children":[]
							};

							pNode = newNode;

							if (item.facet_rule_name) {

								cNode = {
									"id":pNode.id + d + "er" + d + item.rule_id,
									"type":"eph_rule",
									"text":"<span title=\"" + item.rule_id + "\">" + htmlEncode(item.facet_rule_name) + "</span>",
									"data":item,
									"children":[]		
								}
								cNode.data._root = node.id;

								pNode.children.push(cNode);

								pNode = cNode;

								item.facets = item.facets || item.facet_value_pair_list;

								if (!item.facets || typeof(item.facets) == "string") {

									item.facets = splitFacets(item.facets);
								}

								for (var i=0; i < item.facets.length; i++) {
									item.facets[i]._root = node.id;
									item.facets[i]._rule = newNode.id;
									item.facets[i]._facetrule = pNode.id;
									item.facets[i].rule_id = item.rule_id;
									pNode.children.push({"id":pNode.id + d + "ef" + d + item.facets[i].facet_id + "x" + item.facets[i].facet_value_id,"data":item.facets[i], "type":"eph_facet", text:"<span title=\"" + item.facets[i].facet_id + "\">" + htmlEncode(item.facets[i].facet_name) + "</span><span style=\"color:green;font-weight:bold;font-size:1.3em\">:</span>&nbsp;<span title=\"" + item.facets[i].facet_value_id + "\">" + htmlEncode(item.facets[i].facet_value_name) + "</span>"});
								}
							}

							return newNode;
						}
						else if (node.type === "jda") {

							item._root = node.id;

							id = node.id + d + "j" + d + item.rule_id;
						//	id = node.id + d + "j2" + d + item.rule_id;

							var display_name = " <span title=\"" + item.rule_id + "\" style=\"color:#C00000;font-weight:bold\">/</span> ";

							display_name += "<span title=\"" + item.jda_dept_id + "\">" + htmlEncode(item.jda_dept_descrip || "N/A") + "</span>";

							if (item.jda_sub_dept_descrip) {
						 		display_name += " <span title=\"" + item.rule_id + "\" style=\"color:#C00000;font-weight:bold\">/</span> " + "<span title=\"" + item.jda_sub_dept_id + "\">" + htmlEncode(item.jda_sub_dept_descrip || "N/A") + "</span>";
							}

							if (item.jda_class_descrip) {
								display_name += " <span title=\"" + item.rule_id + "\" style=\"color:red;font-weight:bold\">/</span> " + "<span title=\"" + item.jda_class + "\">" + htmlEncode(item.jda_class_descrip || "N/A") + "</span>";
							}

							newNode = {
								"id":id,
								"type":"jda_category",
								"text":display_name,
								"data":item,
								"children":[],
								"data":item
							};

							pNode = newNode;

							if (item.facet_rule_name) {

								cNode = {
									"id":pNode.id + d + "jr" + d + item.rule_id,
									"type":"jda_rule",
									"text":"<span title=\"" + item.rule_id + "\">" + htmlEncode(item.facet_rule_name) + "</span>",
									"data":item,
									"children":[]
								}
								cNode.data._root = node.id;

								pNode.children.push(cNode);

								pNode = cNode;

								item.facets = item.facets || item.facet_value_pair_list;

								if (!item.facets || typeof(item.facets) == "string") {

									item.facets = splitFacets(item.facets);
								}

								for (var i=0; i < item.facets.length; i++) {
									item.facets[i]._root = node.id;
									item.facets[i]._rule = newNode.id;
									item.facets[i]._facetrule = pNode.id;
									item.facets[i].rule_id = item.rule_id;
									pNode.children.push({"id":pNode.id + d + "jf" + d + item.facets[i].facet_id + "x" + item.facets[i].facet_value_id,"data":item.facets[i], "type":"jda_facet", text:"<span title=\"" + item.facets[i].facet_id + "\">" + htmlEncode(item.facets[i].facet_name) + "</span><span style=\"color:red;font-weight:bold;font-size:1.3em\">:</span>&nbsp;<span title=\"" + item.facets[i].facet_value_id + "\">" + htmlEncode(item.facets[i].facet_value_name) + "</span>"});
								}
							}

							return newNode;
						}

						return null;
					}));
				},
				error: function (xhr, status, error) {

					if (authError(xhr)) {
						return;
					}
				//	showAjaxError(xhr, status, error);

					cb(false);
				},
				async: true
			});
		}

	},
	"types" : {
		"#" : {
			"max_children" : 1,
			"valid_children" : ["root"]
			},
		"root" : {
			"icon" : "",
			"valid_children" : ["list", "xlist"]
		},
		"list" : {
			"icon" : "images/list-icon.png",
			"valid_children" : ["category", "xcategory", "dcategory", "dxcategory"]
		},
		"xlist" : {
			"icon" : "images/xlist-icon.png",
			"valid_children" : ["category", "xcategory", "dcategory", "dxcategory"]
		},
		"categories" : {
			"icon" : "",
			"valid_children" : ["category", "xcategory", "dcategory", "dxcategory"]
		},
		"category" : {
			"icon" : "images/list-category.png",
			"valid_children" : ["categories", "skus", "rules", "eph", "jda"]
		},
		"xcategory" : {
			"icon" : "images/xlist-category.png",
			"valid_children" : ["categories", "skus", "rules", "ehp", "jda"]
		},
		"dcategory" : {
			"icon" : "images/dlist-category.png",
			"valid_children" : ["categories", "skus", "rules", "eph", "jda"]
		},
		"dxcategory" : {
			"icon" : "images/dxlist-category.png",
			"valid_children" : ["categories", "skus", "rules", "ehp", "jda"]
		},
		"skus" : {
			"icon" : "",
			"valid_children" : ["sku", "xsku"]
		},
		"sku" : {
			"icon" : "images/sku.png",
			"valid_children" : []
		},
		"xsku" : {
			"icon" : "images/xsku.png",
			"valid_children" : []
		},
		"rules" : {
			"icon" : "",
			"valid_children" : ["rule"]
		},
		"rule" : {
			"icon" : "images/diamond-crown.png",
			"valid_children" : ["facet"]
		},
		"facet" : {
			"icon" : "images/diamond.png",
			"valid_children" : []
		},
		"eph" : {
			"icon" : "",
			"valid_children" : ["eph_rule","eph_node"]
		},
		"eph_rule" : {
			"icon" : "images/emerald-crown.png",
			"valid_children" : ["eph_node","eph_facet"]
		},
		"eph_node" : {
			"icon" : "images/eph-category.png",
			"valid_children" : ["eph_node", "eph_facet", "eph_rule"]
		},
		"eph_facet" : {
			"icon" : "images/emerald.png",
			"valid_children" : []
		},
		"jda" : {
			"icon" : "",
			"valid_children" : ["jda_rule","jda_category"]
		},
		"jda_rule" : {
			"icon" : "images/ruby-crown.png",
			"valid_children" : ["jda_category","jda_facet"]
		},
		"jda_category" : {
			"icon" : "images/jda-category.png",
			"valid_children" : ["jda_category", "jda_facet", "jda_rule"]
		},
		"jda_facet" : {
			"icon" : "images/ruby.png",
			"valid_children" : []
		},
		"default" : {
			"icon" : "images/default.png",
			"valid_children" : ["default"]
		},
	},
	"plugins" : ["contextmenu", "dnd", "search","state", "types"],
	"contextmenu": {items: function(node) {

		//  var tree = $("#list-tree").jstree(true);

		var tree = $tree.jstree(true);

	//	if (!ping(true)) return false;

		var items;

	//	console.log(node);

		switch (node.type) {

		case "root":

			return {
            		"create": {
                		"separator_before": false,
                		"separator_after": false,
                		"label": "Create",
                		"action": function (obj) { 

					if (node.type == "root") {

						createList(node, obj);
					}
				}
            		},
			"reload": {
				"separator_before": false,
				"separator_after": false,
				"label": "Reload",
				"action": function (obj) { 

					tree.refresh();
				}
			}
			};

			break;
		case "list":
		case "xlist":

			items = {
			"addcat": {
				"separator_before": false,
				"separator_after": false,
				"label": "Add Category",
				"action": function (obj) { 

					selectCategoryForm(node, obj);
				}
			},
			"edit": {
				"separator_before": false,
				"separator_after": false,
				"label": "Edit",
				"action": function (obj) { 

					editList(node, obj);
				}
			},                       
			"delete": {
				"separator_before": false,
				"separator_after": false,
				"label": "Delete",
				"action": function (obj) { 

					deleteList(node, obj);

				}
			},
			"clone": {
				"separator_before": false,
				"separator_after": false,
				"label": "Clone",
				"action": function (obj) { 

					cloneList(node, obj);
				}
			}
			};

			if (global.config.showPropPage) {

				items.prop = {
					"separator_before": false,
					"separator_after": false,
					"label": "Properties",
					"action": function (obj) { 

						propForm(node);
					}
				};
			}

			return items;

			break;
		case "categories":

			return {
			"addcat": {
				"separator_before": false,
				"separator_after": false,
				"label": "Add Category",
				"action": function (obj) { 

					selectCategoryForm(node, obj);
				}
			}
			};

			break;
		case "skus":

			return {
			"addsku": {
				"separator_before": false,
				"separator_after": false,
				"label": "Add SKU",
				"action": function (obj) { 

					 selectSKUForm(node, obj);	
				}
			}
			};

			break;
		case "rules":

			return {
			"addrule": {
				"separator_before": false,
				"separator_after": false,
				"label": "Add Facet Rule",
				"action": function (obj) { 

					if (global.config.selectRule) {
						selectRuleForm(node, obj);
					}
					else {
						if (global.config.renameRule && !global.config.addRules) {
							createRule(node);
						}
						else {
							ruleForm(node, "insert");
						}
					}		
				}
			}
			};

			break;
		case "eph":

			return {
			"addcat": {
				"separator_before": false,
				"separator_after": false,
				"label": "Add EPH Rule",
				"action": function (obj) { 

					selectEPHForm(node, obj);
				}
			}
			};

			break;
		case "jda":

			return {
			"add_jda": {
				"separator_before": false,
				"separator_after": false,
				"label": "Add JDA Rule",
				"action": function (obj) { 

					selectJDAForm(node, obj);
				}
			}
			};

			break;
		case "category":
		case "xcategory":
		case "dcategory":
		case "dxcategory":

			items = {
			"edit": {
				"separator_before": false,
				"separator_after": false,
				"label": "Edit",
				"action": function (obj) { 

					editCategory(node, obj);
				}
			},                       
			"remove": {
				"separator_before": false,
				"separator_after": false,
				"label": "Remove",
				"action": function (obj) { 

					deleteCategory(node, obj);
				}
			}
			};

			if (global.config.showPropPage) {

				items.prop = {
					"separator_before": false,
					"separator_after": false,
					"label": "Properties",
					"action": function (obj) { 

						propForm(node);
					}
				};
			}

			return items;

			break;
		case "sku":

			items = {
			"exclude": {
				"separator_before": false,
				"separator_after": false,
				"label": "Exclude",
				"action": function (obj) { 

					if (node.type == "sku") {
						editSKU(node, obj);
					//	tree.set_type(node, "xsku");
					//	tree.set_icon(node, "images/xsku.png");
					}
				}
			},
			"remove": {
				"separator_before": false,
				"separator_after": false,
				"label": "Remove",
				"action": function (obj) { 

					deleteNode(node); 
				}
			}
			};

			if (global.config.showPropPage) {

				items.prop = {
					"separator_before": false,
					"separator_after": false,
					"label": "Properties",
					"action": function (obj) { 

						propForm(node);
					}
				};
			}

			return items;

			break;
		case "xsku": 

			items = {
			"include": {
				"separator_before": false,
				"separator_after": false,
				"label": "Include",
				"action": function (obj) { 

					if (node.type == "xsku") {
						editSKU(node, obj);
					//	tree.set_type(node, "sku");
					//	tree.set_icon(node, "images/sku.png");
					}
				}
			},
			"remove": {
				"separator_before": false,
				"separator_after": false,
				"label": "Remove",
				"action": function (obj) { 

					deleteNode(node); 
				}
			}
			};

			if (global.config.showPropPage) {

				items.prop = {
					"separator_before": false,
					"separator_after": false,
					"label": "Properties",
					"action": function (obj) { 

						propForm(node);
					}
				};
			}

			return items;

			break;
		case "rule":
		case "eph_rule":
		case "jda_rule":

			items = {

			"addfacet": {
				"separator_before": false,
				"separator_after": false,
				"label": "Add Facet",
				"action": function (obj) { 
					
					selectFacetForm(node, obj);	
				}
			},
			"edit": {
				"separator_before": false,
				"separator_after": false,
				"label": "Edit",
				"action": function (obj) { 

					if (global.config.renameRule && !global.config.addRules) {
						renameRule(node, obj);
					}
					else {
						editRule(node, obj);
					}
				}
			},
			"remove": {
				"separator_before": false,
				"separator_after": false,
				"label": "Remove",
				"action": function (obj) { 

					deleteNode(node); 
				}
			}
			};

			if (global.config.showPropPage) {

				items.prop = {
					"separator_before": false,
					"separator_after": false,
					"label": "Properties",
					"action": function (obj) { 

						propForm(node);
					}
				};
			}

			return items;

			break;
		case "facet":
		case "eph_facet":
		case "jda_facet":

			items = {
			"remove": {
				"separator_before": false,
				"separator_after": false,
				"label": "Remove",
				"action": function (obj) { 

					deleteNode(node); 
				}
			}
			};

			if (global.config.showPropPage) {

				items.prop = {
					"separator_before": false,
					"separator_after": false,
					"label": "Properties",
					"action": function (obj) { 

						propForm(node);
					}
				};
			}

			return items;

			break;
		case "eph_node":

			items = {
			"edit_eph": {
				"separator_before": false,
				"separator_after": false,
				"label": "Edit",
				"action": function (obj) { 

					selectEPHForm(node, obj);
				}
			},  
			"addrule": {
				"separator_before": false,
				"separator_after": false,
				"label": "Add Facet Rule",
				"action": function (obj) { 

					if (global.config.selectRule) {
						selectRuleForm(node, obj);
					}
					else {
						if (global.config.renameRule && !global.config.addRules) {
							createRule(node);
						}
						else {
							ruleForm(node, "insert");
						}
					}
				}
			},                     
			"remove": {
				"separator_before": false,
				"separator_after": false,
				"label": "Remove",
				"action": function (obj) { 

					deleteCategory(node, obj);
				}
			}
			};

			if (global.config.showPropPage) {

				items.prop = {
					"separator_before": false,
					"separator_after": false,
					"label": "Properties",
					"action": function (obj) { 

						propForm(node);
					}
				};
			}

			if (node.children && node.children.length) {
				if (!global.config.addRules) {
					delete items.addrule;
				}
			}

			return items;

			break;
		case "jda_category":

			items = {
			"edit_jda": {
				"separator_before": false,
				"separator_after": false,
				"label": "Edit",
				"action": function (obj) { 

					selectJDAForm(node, obj);
				}
			},  
			"addrule": {
				"separator_before": false,
				"separator_after": false,
				"label": "Add Facet Rule",
				"action": function (obj) { 

					if (global.config.selectRule) {
						selectRuleForm(node, obj);
					}
					else {
						if (global.config.renameRule && !global.config.addRules) {
							createRule(node);
						}
						else {
							ruleForm(node, "insert");
						}
					}
				}
			},                       
			"remove": {
				"separator_before": false,
				"separator_after": false,
				"label": "Remove",
				"action": function (obj) { 

					deleteCategory(node, obj);
				}
			}
			};

			if (global.config.showPropPage) {

				items.prop = {
					"separator_before": false,
					"separator_after": false,
					"label": "Properties",
					"action": function (obj) { 

						propForm(node);
					}
				};
			}

			if (node.children && node.children.length) {
				if (!global.config.addRules) {
					delete items.addrule;
				}
			}

			return items;

			break;
		default:
			return false;
		}

	}}
}).on('changed.jstree', function (e, data) {

//	if (!ping(true)) return;

}).on('create_node.jstree', function (e, data) {


}).on('rename_node.jstree', function (e, data) {


}).on('delete_node.jstree', function (e, data) {


}).on('move_node.jstree', function (e, data) { 

	reOrderNode(e, data);
});

}

function getNodeKeys(id) {

	var token = id.split(global.config.delim);

	var keys = [];

	for (var i=0; i < token.length-1 && token.length > 1; i+=2) {

		if (token[i+1] === "") continue;
		keys.push({name:token[i],value:token[i+1]});
	}

//	console.log(id);
//	console.log(JSON.stringify(keys));

	return keys;
}

function getNextKeyToken(token, alt) {

	var index = getKeyTokenIndex(token, alt);
	if (!index) return global.config.delim + alt + global.config.delim;
	index = parseInt(index, 10) + 1;
	var prefix = token.replace(/[0-9]+.+/g, '');
	return global.config.delim + prefix + index + global.config.delim;
}

function getKeyTokenIndex(token, alt) {

	return parseInt(token.replace(/[^0-9]/g, ''), 10) || 0;

}

function splitFacets(strlist) {

	var list = [];
	if (!strlist) return list;
	var pairlist = strlist.split("\n");
	for (var i=0; i < pairlist.length; i++) {

		var pair = pairlist[i].split("\r");
		
		if (!pair || pair.length < 2) continue;

		var name = pair[0].split("\t");
		var value = pair[1].split("\t");

		list.push({
			"facet_id":name[0] || "",
			"facet_name":name[1] || name[0] || "",
			"facet_value_id":value[0] || "",
			"facet_value_name":value[1] || value[0] || ""
		});
	}
	return list;
}

function splitEPHNodes(strlist) {

	var list = [];
	if (!strlist) return list;
	var itemlist = strlist.split("\n");
	for (var i=0; i < itemlist.length; i++) {

		var item = itemlist[i];
		
		if (!item) continue;

		var record = item.split("\t");

		list.push({
			"eph_node_id":record[0] || name,
			"display_name":record[1] || "",
			"children":parseInt(record[2], 10) || 0
		});
	}
	return list;
}