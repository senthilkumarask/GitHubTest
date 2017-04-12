dialog_fixed_position = {
	using: function(obj) {
		this.style.left = Math.max((window.innerWidth - this.offsetWidth)/2, 0);
		this.style.top = Math.max((window.innerHeight - this.offsetHeight)/2, 0);
	//	this.style.left = Math.max(($(window).scrollLeft() + window.innerWidth - this.offsetWidth)/2, 0);
	//	this.style.top = Math.max(($(window).scrollTop() + window.innerHeight - this.offsetHeight)/2, 0);
	}
};

function getSelectedOption(select) {
	if (!select || !select.options) return null;
	var option = select.options[select.selectedIndex];
	if (!option || !option.text) return null;
	return option.text;
}
function changeTheme(theme) {
	if (!theme) return null;
	theme = theme.toLowerCase().replace(/[^a-zA-Z0-9\-]/gi, "");
	if (!theme) return null;
	var logo = document.getElementById('logo');
	if (!logo || !logo.src) return null;
	var sheet = document.getElementById('theme');
	if (!sheet ||!sheet.href) return null;
        var image = new Image();
	image.onload = function() {logo.src=this.src;return false;};
	image.src = logo.src.replace(/\-.*\./, '-' + theme + '.');
	sheet.href = sheet.href.replace(/themes\/.*\//, 'themes/' + theme + '/');
	return theme;
}
function saveTheme(theme, reload) {
	if (!theme) return theme;
	createCookie("theme",theme);
	if (reload) location.reload();
	return theme;
}
function changeDataSource(ds) {

	var tree = global.$list_tree.jstree(true);
	tree.refresh();
	return ds;
}
function saveDataSource(ds) {
	if (!ds) return ds;
	createCookie("data_source",ds);
	return ds;
}
function createCookie(name,value,days) {
	if (days) {
		var date = new Date();
		date.setTime(date.getTime()+(days*24*60*60*1000));
		var expires = "; expires="+date.toGMTString();
	}
	else var expires = "";
	document.cookie = name+"="+value+expires+"; path=/";
}

function readCookie(name) {
	var nameEQ = name + "=";
	var ca = document.cookie.split(';');
	for(var i=0;i < ca.length;i++) {
		var c = ca[i];
		while (c.charAt(0)==' ') c = c.substring(1,c.length);
		if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
	}
	return null;
}
function eraseCookie(name) {
	createCookie(name,"",-1);
}

function isDateValid(date) {

    return !isNaN(Date.parse(date));
}

function isDateValid(sdate) {

    var date = sdate.split("/");
    return checkDate(date[1], date[0], date[2]);
}

function checkDate(day, month, year) {

    var xday = parseInt(day, 10);
    var xmonth = parseInt(month, 10);
    var xyear = parseInt(year, 10);

    if (isNaN(xday) || isNaN(xmonth) || isNaN(xyear)) return false;

    if (xmonth < 1 || xmonth > 12) return false;
	
    var months = [31,28,31,30,31,30,31,31,30,31,30,31];

    if (xyear < 0) return false;

    if (leapYear(xyear)) months[1] = 29;

    if (xday < 1 || xday > months[xmonth-1]) return false;

    return true;

    function leapYear(year) {
        if (year % 400 == 0) return true;
        if (year % 100 == 0) return false;
        if (year % 4 == 0) return true;
    }
}

function formatDate(date) {

    return (101+date.getMonth()).toString().substr(1) + "/" + (100+date.getDate()).toString().substr(1) + "/" + date.getFullYear();
}

String.prototype.trim = function() 
{
    return this.replace(/^\s+|\s+$/g,"");
};

String.prototype.replaceAll = function(str1, str2, ignore) 
{
	return this.replace(new RegExp(str1.replace(/([\/\,\!\\\^\$\{\}\[\]\(\)\.\*\+\?\|\<\>\-\&])/g,"\\$&"),(ignore?"gi":"g")),(typeof(str2)=="string")?str2.replace(/\$/g,"$$$$"):str2);
};

function serializeForm(form, prefix) {

    var obj = {};

    if (typeof(form) == "string") {
        form = document.getElementById(form);
    }
    if (!form) return obj;

    var elements = form.elements;

    for (var i=0; i < elements.length; i++) {
        var element = elements[i];

        if (!element.name) {
            continue;
        }

	var name = element.name;

	if (prefix) {
		if (prefix = name.substr(0, prefix.length)) {
			name = name.substr(prefix.length);
		}
	}

        obj[name] = getElement(element);
    }

    return obj;
}

function deserializeForm(form, obj, prefix) {

    if (typeof(form) == "string") {
        form = document.getElementById(form);
    }
    if (!form) return;

    var elements = form.elements;

    for (var i=0; i < elements.length; i++) {
        var element = elements[i];

        if (!element.name) {
            continue;
        }

	var name = element.name;

	if (prefix) {
		if (prefix = name.substr(0, prefix.length)) {
			name = name.substr(prefix.length);
		}
	}

        setElement(element, obj[name]);
    }

}

function resetForm(form, all) {

    if (typeof(form) == "string") {
        form = document.getElementById(form);
    }
    if (!form) return;

    var elements = form.elements;

    for (var i=0; i < elements.length; i++) {
        var element = elements[i];

        if (!element.name) {
            continue;
        }

	var error = document.getElementById(element.id + '-error');
	if (error) error.innerHTML = '';

        if (all) setElement(element);
    }
}

function getElement(e) {

    var element = e;
    var value;

    if (typeof(element) == "string") {
        element = document.getElementById(element);
    }
    if (!element) return value;

    switch (element.tagName) {
    case 'INPUT':
        switch (element.type) {
        case 'file':
            break;
        case 'checkbox':
		value = (element.checked?1:0);
        case 'radio':
	    var radio = document.getElementsByName(typeof(e)=="string"?e:e.name);
	    for (var i = 0; i < radio.length; i++) {
    		if (radio[i].checked) {
        		value = radio[i].value;
			break;
    		}
	    }
	    break;                        
        case 'text':
        case 'hidden':
        case 'password':
        case 'button':
        case 'reset':
        case 'submit':
        default:
            value = element.value;
            break;
        }
        break;
    case 'SELECT':
        switch (element.type) {
        case 'select-multiple':
            value = [];
            for (var j = element.options.length - 1; j >= 0; j--) {
                if (element.options[j].selected) {
                    value.push(element.options[j].value);
                }
            }
            break;
        default:
            value = element.value;
            break;
        }
        break;
    case 'BUTTON':
    case 'TEXTAREA':
        value = element.value;
        break;
    case 'DIV':
        value = element.innerText;
        break;
    default:
        break;
    }

    return value;
}

function setElement(e, value) {

    var element = e;
    if (value === undefined || value === null) value = '';

    if (typeof(element) == "string") {
        element = document.getElementById(element);
    }
    if (!element) return;

    switch (element.tagName) {
    case 'INPUT':
        switch (element.type) {
        case 'file':
            break;
        case 'checkbox':
            element.checked = !!value;
            break;
        case 'radio': 
	    var radio = document.getElementsByName(typeof(e)=="string"?e:e.name);
	    for (var i = 0; i < radio.length; i++) {
    		if (radio[i].value == value) {
        		radio[i].checked = true;
        		break;
    		}
	    }
            break;                                           
        case 'text':
        case 'hidden':
        case 'password':
        case 'button':
        case 'reset':
        case 'submit':
        default:
            element.value = value;
            break;
        }
        break;
    case 'SELECT':
        switch (element.type) {
        case 'select-multiple':
            value = value || [];
	    for (var i=0; i < value.length; i++) {
                for (var j=0; j < element.options.length; j++) {

                    if (element.options[j].value == value[i]) {
                        element.options[j].selected = true;
                    }
                }
	    }
            break;
        default:
            element.value = value;
	    if (element.selectedIndex == -1) {
		element.selectedIndex = 0;
	    }
            break;
        }
        break;
    case 'BUTTON':
    case 'TEXTAREA':
        element.value = value;
        break;
    case 'DIV':
        element.innerText = value;
        break;
    default:
        break;
    }
}

function ping(callback) {

    var isOK = !!callback;

    $.ajax({
        url: 'ping.txt?t=' + (new Date()).getTime(),
        success: function (result) {
            if (result.substr(0, 2) != "Ok") {
            	sessionTimeout();
            }
            if (typeof(callback) == "function") {
		callback();
	    }
	    isOK = true;
        },
        error: function (result) {
            sessionTimeout();
        },
        async: isOK
    });
    return isOK;
}

function sessionTimeout() {

    location = "login.jsp";
}

function scrollRight(element) {

    if (element.scrollWidth > element.offsetWidth) {

        element.scrollLeft = element.scrollWidth - element.offsetWidth;
    }
}

function getPos(el, root) {

    for (var lx=0, ly=0;
         el != null && el != root;
         lx += el.offsetLeft, ly += el.offsetTop, el = el.offsetParent);
    return {x: lx,y: ly};
}

function removeElement(element) {
    if (typeof(element) == "string") {
        element = document.getElementById(element);
    }
    if (!element) return false;
    element.parentNode.removeChild(element);
    return false;
}

function serializeFormFragment(form) {

    var obj = {};

    if (typeof(form) == "string") {
        form = document.getElementById(form);
    }
    if (!form) return obj;

    var elements = [];

    var tags = ['input','select','button','textarea'];

    for (var i=0; i < tags.length; i++) {
        var element = form.getElementsByTagName(tags[i]);
        for (var j=0; j < element.length; j++) elements.push(element[j]);
    }

    for (var i=0; i < elements.length; i++) {
        var element = elements[i];

        var name = element.name.replace(/[0-9]/g, '');

        if (!name) {
            continue;
        }

        obj[name] = getElement(element);
    }

    return obj;
}

function queryString() {
    var list = {};
 
    location.search.replace(new RegExp( "([^?=&]+)(=([^&]*))?", "g" ),
        function($0, $1, $2, $3) {

            list[$1] = unescape($3);
    });

    return list;
}

function getJson(target, source, params, map, cb) {

	// gets json data from source to target
	// target: a select control or a function(data) to receive the data
	// source: a string (url), a javascript data object, or a function returing a string (url) or data object
	// params: a javascript object containing post data, or a function() returning post data
	// map: a map function to remap/filter the data 
	// cb: a callback when the load is completed

	if (typeof(source) == "function") {

		source = source();
	}

	if (typeof(params) == "function") {

		params = params();
	}

	if (typeof(source) == "string") {

	//	console.log(source);
	//	console.log(params);

		$.ajax({
			url: source,
			data: params || {},
			dataType: "json",
			type: "POST",
			dataFilter: function (data) { return data; },
			success: function (data) {

				load(data);
			},
			error: function (xhr, status, error) {

				if (cb && typeof(cb) == "function") {

					cb();
				}

				console.log(xhr.status + " " + error);
			}
		});

		return;
	}

	if (typeof(source) == "object") {

		load(source);

		return;
	}

	function load(data) {

		if (map && typeof(map) == "function") {

			data = map(data);
		}

		if (typeof(target) == "function") {

			target(data);

			if (cb && typeof(cb) == "function") {

				cb();
			}

			return;
		}

		if (typeof(target) == "string") {

			target = document.getElementById(target);
		}

		if (!target) {

			if (cb && typeof(cb) == "function") {

				cb();
			}

			return;
		}

		target.options.length = 0;

		for(var i=0; data && i < data.length; i++) {
    			target.options.add(new Option(data[i].text || data[i].label,  data[i].value || data[i].id));
		}

		if (cb && typeof(cb) == "function") {

			cb();
		}
	}
}

function appendQS(url, qs) {

	if (!url) return "";
	return url + (url.indexOf('?')==-1?"?":"&") + (qs || "");
}

function isNullOrEmpty(str) {
	if (str === undefined) return true;
	if (str === null) return true;
	if (!str.trim()) return true;
}

function htmlEncode(s) {
	s = s || "";
	return s.replace(/&/g, '&amp;')
        	.replace(/"/g, '&quot;')
        	.replace(/'/g, '&#39;')
        	.replace(/</g, '&lt;')
        	.replace(/>/g, '&gt;');
}

function printable(s) {
	for (var i=0; s &&  i < s.length; i++) {
		if (s.charCodeAt(i) > 127) return false;
		if (s.charCodeAt(i) < 32) return false;
	}
	return true;
}

function getStyle(el, prop) {

	if (document.defaultView && document.defaultView.getComputedStyle) {
    		return document.defaultView.getComputedStyle(el, null)[prop];
  	} else if (el.currentStyle) {
    		return el.currentStyle[prop];
  	} else {
    		return el.style[prop];
  	}
}

function authError(xhr) {

	if (xhr.status == 200 && xhr.responseText && xhr.responseText.trim().substr(0, 1) == "<") {
		location = "login.jsp?DPSTimeout=true&l";
		return true;
	}
	return false;
}

function showAjaxError(xhr, status, error) {

//	console.log(xhr.status + " " + error);

	if (authError(xhr)) {
		return;
	}

	showMessageBox(xhr.status + " " + error, "System Error");
}

function serverError(result, show, suppress) {

	var message;

	if (!result || result.status == "error") {

		if (result && result.message && result.message.id == 67) {
			location = "login.jsp?DPSTimeout=true&l";
			return true;
		}

		if (!show) {

			return suppress?false:true;
		}

		message = getServerMessage(result);

		showMessageBox(message.description, message.title);

		return true;
	}
		
	return false;
}

function getServerMessage(result) {

	var message = {};

	if (result && result.message && typeof(result.message) == "object") {

		message = getMessage(result.message.id) || result.message;
	}

	message.id = message.id || 0;
	message.title = message.title || "";
	message.description = message.description || "unknown error";

	return message;
}

function getServerMessageText(result) {

	var message = getServerMessage(result);

	return (message.title?message.title+" - ":"") + message.description;
}

function getMessage(id) {

	var message = MESSAGES[id];
	if (message) {
		message.id = id;
	}
	return message;
}

function showMessageBox(text, title) {

	$('#alertDialog-message').text(text);

	var dialog_config = {

		modal: true,
  		title:title,
		open: function (event, ui) {
			$(event.target).parent().css('position', (global.config.dialogFixed?'fixed':'absolute'));
		},
		"resize":"auto",
  		buttons: [
    		{
      			text: "Ok",
			click: function() {
				$( this ).dialog( "close" );
			}
		}]
	};

	dialog_config.position = dialog_fixed_position;

	global.$alertDialog.dialog(dialog_config).dialog('open');
		
	return true;
}
