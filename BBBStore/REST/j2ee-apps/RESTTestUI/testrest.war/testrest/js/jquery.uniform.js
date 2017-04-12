/* Forked version of Uniform v1.7.5 by Josh Pyles / Pixelmatrix Design LLC (http://pixelmatrixdesign.com) */

(function ($) {
    $.uniform = {
        options: {
            selectClass: 'selector',
            radioClass: 'radio',
            checkboxClass: 'checker',
            fileClass: 'uploader',
            filenameClass: 'filename',
            fileBtnClass: 'action',
            fileDefaultText: 'No file selected',
            fileBtnText: 'Choose File',
            checkedClass: 'checked',
            focusClass: 'focus',
            disabledClass: 'disabled',
            buttonClass: 'button',
            activeClass: 'active',
            hoverClass: 'hover',
            useID: true,
            idPrefix: 'uniform',
            resetSelector: false,
            autoHide: true
        },
        elements: []
    };

    if ($.browser.msie && $.browser.version < 7) {
        $.support.selectOpacity = false;
    } else {
        $.support.selectOpacity = true;
    }

    $.fn.uniform = function (settings) {
        var options = $.extend({}, $.uniform.options, settings),
            el = this;

        function elemIs(elem, tag) {
            var $el = $(elem)[0];

            if (typeof elem === 'undefined' || typeof tag === 'undefined' || typeof tag !== 'string' || !$el) {
                return false;
            }

            return ($el.tagName.toLowerCase() === tag.toLowerCase());
        }

        function resetThis() {
            $.uniform.update(el);
        }

        function doInput(elem) {
            $el = $(elem);
            $el.addClass($el.attr('type'));
            storeElement(elem);
        }

        function doTextarea(elem) {
            $(elem).addClass('uniform');
            storeElement(elem);
        }

        function doButton(elem) {
            var $el = $(elem),
                divTag = $('<div>'),
                spanTag = $('<span>'),
                btnText;

            divTag.addClass(options.buttonClass);

            if (options.useID && $el.attr('id') !== '') divTag.attr('id', options.idPrefix + '-' + $el.attr('id'));

            if (elemIs($el, 'a') || elemIs($el, 'button')) {
                btnText = $el.text();
            } else if ($el.is(':submit') || $el.is(':reset') || $el.is('input[type="button"]')) {
                btnText = $el.attr('value');
            }

            btnText = btnText === '' ? $el.is(':reset') ? 'Reset' : 'Submit' : btnText;

            spanTag.text(btnText);

            $el.css('opacity', 0);
            $el.wrap(divTag);
            $el.wrap(spanTag);

            //redefine variables
            divTag = $el.closest('div');
            spanTag = $el.closest('span');

            if ($el.is(':disabled')) divTag.addClass(options.disabledClass);

            divTag.bind({
                'mouseenter.uniform': function () {
                    divTag.addClass(options.hoverClass);
                },
                'mouseleave.uniform': function () {
                    divTag.removeClass(options.hoverClass);
                    divTag.removeClass(options.activeClass);
                },
                'mousedown.uniform touchbegin.uniform': function () {
                    divTag.addClass(options.activeClass);
                },
                'mouseup.uniform touchend.uniform': function () {
                    divTag.removeClass(options.activeClass);
                },
                'click.uniform touchend.uniform': function (e) {
                    var $el = $(e.target);

                    if (elemIs($el, 'span') || elemIs($el, 'div')) {
                        if (elem[0].dispatchEvent) {
                            var ev = document.createEvent('MouseEvents');
                            ev.initEvent('click', true, true);
                            elem[0].dispatchEvent(ev);
                        } else {
                            elem[0].click();
                        }
                    }
                }
            });

            elem.bind({
                'focus.uniform': function () {
                    divTag.addClass(options.focusClass);
                },
                'blur.uniform': function () {
                    divTag.removeClass(options.focusClass);
                }
            });

            $.uniform.noSelect(divTag);
            storeElement(elem);
        }

        function doSelect(elem) {
            var $el = $(elem),
                divTag = $('<div />'),
                spanTag = $('<span />'),
                selected, text, selWidBeforeAuto, selWidAfterAuto, spanGap, uniformWid, uniformBorder, extraWid = 5;

            if ($el.css('display') === 'none' && options.autoHide) {
                divTag.hide();
            }

            divTag.addClass(options.selectClass);

            if (options.useID && elem.attr('id') !== '') {
                divTag.attr('id', options.idPrefix + '-' + elem.attr('id'));
            }

            selected = elem.find(':selected:first');
            if (selected.length === 0 || elem.prop('selectedIndex') === 0 || elem.prop('selectedIndex') === -1) {
                selected = elem.find('option:first');
                elem.prop('selectedIndex', 0);
            }
            text = selected.text().trim();
            spanTag.text(text);

            elem.css('opacity', 0);
            elem.wrap(divTag);
            elem.before(spanTag);

            //redefine variables
            divTag = elem.parent('div');
            spanTag = elem.siblings('span');

            elem[0].title = text;
            divTag[0].title = text;
            spanTag[0].title = text;

            elem.bind({
                'change.uniform': function () {
                    var text = elem.find(':selected').text().trim();

                    elem[0].title = text;
                    spanTag.text(text)[0].title = text;
                    divTag[0].title = text;
                    divTag.removeClass(options.activeClass);

                    setTimeout(function () {
                        var text = elem.find(':selected').text().trim();

                        elem[0].title = text;
                        spanTag.text(text)[0].title = text;
                        divTag[0].title = text;
                        divTag.removeClass(options.activeClass);
                    }, 100);
                },
                'focus.uniform': function () {
                    divTag.addClass(options.focusClass);
                },
                'blur.uniform': function () {
                    divTag.removeClass(options.focusClass);
                    divTag.removeClass(options.activeClass);
                },
                'mousedown.uniform touchbegin.uniform': function () {
                    divTag.addClass(options.activeClass);
                },
                'mouseup.uniform touchend.uniform': function () {
                    divTag.removeClass(options.activeClass);
                },
                'click.uniform touchend.uniform': function () {
                    divTag.removeClass(options.activeClass);
                },
                'mouseenter.uniform': function () {
                    divTag.addClass(options.hoverClass);
                },
                'mouseleave.uniform': function () {
                    divTag.removeClass(options.hoverClass);
                    divTag.removeClass(options.activeClass);
                },
                'keyup.uniform': function () {
                    var text = elem.find(':selected').text().trim();

                    elem[0].title = text;
                    spanTag.text(text)[0].title = text;
                    divTag[0].title = text;

                    setTimeout(function () {
                        var text = elem.find(':selected').text().trim();

                        elem[0].title = text;
                        spanTag.text(text)[0].title = text;
                        divTag[0].title = text;
                        divTag.removeClass(options.activeClass);
                    }, 100);
                }
            });

            //handle disabled state
            if (elem.attr('disabled')) {
                //box is checked by default, check our box
                divTag.addClass(options.disabledClass);
            }
            $.uniform.noSelect(spanTag);

            storeElement(elem);

            if ($.browser.msie && $.browser.version <= 8 && (typeof elem.data('fixSelectsIE') === 'undefined' || (typeof elem.data('fixSelectsIE') !== 'undefined' && elem.data('fixSelectsIE') === false))) {
                elem.css('height', divTag.getHiddenDimensions(true).height + 'px');

                selWidBeforeAuto = elem.getHiddenDimensions(true).width;
                uniformWid = divTag.getHiddenDimensions().width;

                if (elem.hasClass('selector') || elem.hasClass('uniform')) {
                    spanGap = parseInt(spanTag.css('marginLeft').replace('px', ''), 10) + parseInt(spanTag.css('marginRight').replace('px', ''), 10);
                    uniformBorder = parseInt(divTag.css('border-left-width').replace('px', ''), 10) + parseInt(divTag.css('border-right-width').replace('px', ''), 10) + parseInt(divTag.css('paddingLeft').replace('px', ''), 10);
                    uniformWid = uniformWid + uniformBorder;
                    spanTag.css('width', 'auto');
                    divTag.css('background-position-x', (uniformWid - spanGap) + uniformBorder + 'px');
                }

                elem.addClass('widAuto');

                selWidAfterAuto = elem.getHiddenDimensions(true).width;

                if (selWidBeforeAuto < (uniformWid + extraWid)) {
                    selWidBeforeAuto = (uniformWid + extraWid);
                }

                if (selWidAfterAuto > selWidBeforeAuto) {
                    elem.css('width', selWidAfterAuto + 'px');
                } else {
                    elem.css('width', selWidBeforeAuto + 'px');
                }

                elem.removeClass('widAuto');
                elem.data('fixSelectsIE', true);
            }
            // if (typeof window.bbbFixSelectsIE !== 'undefined' && typeof window.bbbFixSelectsIE === 'function') {
                // window.bbbFixSelectsIE();
            // }
        }

        function doCheckbox(elem) {
            var $el = $(elem),
                divTag = $('<div />'),
                spanTag = $('<span />');

            if ($el.css('display') === 'none' && options.autoHide) {
                divTag.hide();
            }

            divTag.addClass(options.checkboxClass);

            //assign the id of the element
            if (options.useID && elem.attr('id') !== '') {
                divTag.attr('id', options.idPrefix + '-' + elem.attr('id'));
            }

            //wrap with the proper elements
            $(elem).wrap(divTag);
            $(elem).wrap(spanTag);

            //redefine variables
            spanTag = elem.parent();
            divTag = spanTag.parent();

            //hide normal input and add focus classes
            $(elem).css('opacity', 0).bind({
                'focus.uniform': function () {
                    divTag.addClass(options.focusClass);
                },
                'blur.uniform': function () {
                    divTag.removeClass(options.focusClass);
                },
                'click.uniform touchend.uniform': function () {
                    if (!$(elem).attr('checked')) {
                        //box was just unchecked, uncheck span
                        spanTag.removeClass(options.checkedClass);
                    } else {
                        //box was just checked, check span.
                        spanTag.addClass(options.checkedClass);
                    }
                },
                'mousedown.uniform touchbegin.uniform': function () {
                    divTag.addClass(options.activeClass);
                },
                'mouseup.uniform touchend.uniform': function () {
                    divTag.removeClass(options.activeClass);
                },
                'mouseenter.uniform': function () {
                    divTag.addClass(options.hoverClass);
                },
                'mouseleave.uniform': function () {
                    divTag.removeClass(options.hoverClass);
                    divTag.removeClass(options.activeClass);
                }
            });

            //handle defaults
            if ($(elem).attr('checked')) {
                //box is checked by default, check our box
                spanTag.addClass(options.checkedClass);
            }

            //handle disabled state
            if ($(elem).attr('disabled')) {
                //box is checked by default, check our box
                divTag.addClass(options.disabledClass);
            }

            storeElement(elem);
        }

        function doRadio(elem) {
            var $el = $(elem),
                divTag = $('<div />'),
                spanTag = $('<span />');

            if ($el.css('display') === 'none' && options.autoHide) {
                divTag.hide();
            }

            divTag.addClass(options.radioClass);

            if (options.useID && elem.attr('id') !== '') {
                divTag.attr('id', options.idPrefix + '-' + elem.attr('id'));
            }

            //wrap with the proper elements
            $(elem).wrap(divTag);
            $(elem).wrap(spanTag);

            //redefine variables
            spanTag = elem.parent();
            divTag = spanTag.parent();

            //hide normal input and add focus classes
            $(elem).css('opacity', 0).bind({
                'focus.uniform': function () {
                    divTag.addClass(options.focusClass);
                },
                'blur.uniform': function () {
                    divTag.removeClass(options.focusClass);
                },
                'click.uniform touchend.uniform': function () {
                    var classes;

                    if (!$(elem).attr('checked')) {
                        //box was just unchecked, uncheck span
                        spanTag.removeClass(options.checkedClass);
                    } else {
                        //box was just checked, check span
                        classes = options.radioClass.split(' ')[0];
                        $('.' + classes + ' span.' + options.checkedClass + ':has([name="' + $(elem).attr('name') + '"])').removeClass(options.checkedClass);
                        spanTag.addClass(options.checkedClass);
                    }
                },
                'mousedown.uniform touchend.uniform': function () {
                    if (!$(elem).is(':disabled')) {
                        divTag.addClass(options.activeClass);
                    }
                },
                'mouseup.uniform touchbegin.uniform': function () {
                    divTag.removeClass(options.activeClass);
                },
                'mouseenter.uniform touchend.uniform': function () {
                    divTag.addClass(options.hoverClass);
                },
                'mouseleave.uniform': function () {
                    divTag.removeClass(options.hoverClass);
                    divTag.removeClass(options.activeClass);
                }
            });

            //handle defaults
            if ($(elem).attr('checked')) {
                //box is checked by default, check span
                spanTag.addClass(options.checkedClass);
            }
            //handle disabled state
            if ($(elem).attr('disabled')) {
                //box is checked by default, check our box
                divTag.addClass(options.disabledClass);
            }

            storeElement(elem);
        }

        function doFile(elem) {
            //sanitize input
            var $el = $(elem),
                divTag = $('<div />'),
                filenameTag = $('<span>' + options.fileDefaultText + '</span>'),
                btnTag = $('<span>' + options.fileBtnText + '</span>'),
                divWidth;

            if ($el.css('display') === 'none' && options.autoHide) {
                divTag.hide();
            }

            divTag.addClass(options.fileClass);
            filenameTag.addClass(options.filenameClass);
            btnTag.addClass(options.fileBtnClass);

            if (options.useID && $el.attr('id') !== '') {
                divTag.attr('id', options.idPrefix + '-' + $el.attr('id'));
            }

            //wrap with the proper elements
            $el.wrap(divTag);
            $el.after(btnTag);
            $el.after(filenameTag);

            //redefine variables
            divTag = $el.closest('div');
            filenameTag = $el.siblings('.' + options.filenameClass);
            btnTag = $el.siblings('.' + options.fileBtnClass);

            //set the size
            if (!$el.attr('size')) {
                divWidth = divTag.width();
                //$el.css('width', divWidth);
                $el.attr('size', divWidth / 10);
            }

            //actions
            var setFilename = function () {
                var filename = $el.val();
                if (filename === '') {
                    filename = options.fileDefaultText;
                } else {
                    filename = filename.split(/[\/\\]+/);
                    filename = filename[(filename.length - 1)];
                }
                filenameTag.text(filename);
            };

            // Account for input saved across refreshes
            setFilename();

            $el.css('opacity', 0).bind({
                'focus.uniform': function () {
                    divTag.addClass(options.focusClass);
                },
                'blur.uniform': function () {
                    divTag.removeClass(options.focusClass);
                },
                'mousedown.uniform': function () {
                    if (!$(elem).is(':disabled')) {
                        divTag.addClass(options.activeClass);
                    }
                },
                'mouseup.uniform': function () {
                    divTag.removeClass(options.activeClass);
                },
                'mouseenter.uniform': function () {
                    divTag.addClass(options.hoverClass);
                },
                'mouseleave.uniform': function () {
                    divTag.removeClass(options.hoverClass);
                    divTag.removeClass(options.activeClass);
                }
            });

            // IE7 doesn't fire onChange until blur or second fire.
            if ($.browser.msie) {
                // IE considers browser chrome blocking I/O, so it
                // suspends tiemouts until after the file has been selected.
                $el.bind('click.uniform.ie7', function () {
                    setTimeout(setFilename, 0);
                });
            } else {
                // All other browsers behave properly
                $el.bind('change.uniform', setFilename);
            }

            //handle defaults
            if ($el.attr('disabled')) {
                //box is checked by default, check our box
                divTag.addClass(options.disabledClass);
            }

            $.uniform.noSelect(filenameTag);
            $.uniform.noSelect(btnTag);

            storeElement(elem);
        }

        function storeElement(elem) {
            //store this element in our global array
            elem = $(elem).get();
            if (elem.length > 1) {
                $.each(elem, function (i, val) {
                    $.uniform.elements.push(val);
                });
            } else {
                $.uniform.elements.push(elem);
            }
        }

        //code for specifying a reset button
        if (options.resetSelector !== false) {
            $(options.resetSelector).mouseup(function () {
                setTimeout(resetThis, 10);
            });
        }

        $.uniform.restore = function (elem) {
            if (elem === undefined) {
                elem = $($.uniform.elements);
            }

            $(elem).each(function () {
                var $e = $(this),
                    index;

                if ($e.is(':checkbox')) {
                    //unwrap from span and div
                    $e.unwrap().unwrap();
                } else if (elemIs($e, 'select')) {
                    //remove sibling span
                    $e.siblings('span').remove();
                    //unwrap parent div
                    $e.unwrap();
                } else if ($e.is(':radio')) {
                    //unwrap from span and div
                    $e.unwrap().unwrap();
                } else if ($e.is(':file')) {
                    //remove sibling spans
                    $e.siblings('span').remove();
                    //unwrap parent div
                    $e.unwrap();
                } else if (elemIs($e, 'button') || elemIs($e, 'a') || $e.is(':submit, :reset, input[type="button"]')) {
                    //unwrap from span and div
                    $e.unwrap().unwrap();
                }

                //unbind events
                $e.unbind('.uniform');

                //reset inline style
                $e.css('opacity', '1');

                //remove item from list of uniformed elements
                index = $.inArray($(elem), $.uniform.elements);
                $.uniform.elements.splice(index, 1);
            });
        };

        //noSelect v1.0
        $.uniform.noSelect = function (elem) {
            function f() {
                return false;
            }

            $(elem).each(function () {
                this.onselectstart = this.ondragstart = f; // Webkit & IE
                $(this).mousedown(f) // Webkit & Opera
                .css({
                    MozUserSelect: 'none'
                }); // Firefox
            });
        };

        $.uniform.update = function (elem) {
            if (elem === undefined) {
                elem = $($.uniform.elements);
            }

            //sanitize input
            elem = $(elem);

            elem.each(function () {
                //do to each item in the selector
                //function to reset all classes
                var $e = $(this);

                if (elemIs($e, 'select')) {
                    //element is a select
                    var spanTag = $e.siblings('span');
                    var divTag = $e.parent('div');

                    divTag.removeClass(options.hoverClass + ' ' + options.focusClass + ' ' + options.activeClass);

                    //reset current selected text
                    spanTag.text($e.find(':selected').text());

                    if ($e.is(':disabled')) {
                        divTag.addClass(options.disabledClass);
                    } else {
                        divTag.removeClass(options.disabledClass);
                    }

                } else if ($e.is(':checkbox')) {
                    //element is a checkbox
                    var spanTag = $e.closest('span');
                    var divTag = $e.closest('div');

                    divTag.removeClass(options.hoverClass + ' ' + options.focusClass + ' ' + options.activeClass);
                    spanTag.removeClass(options.checkedClass);

                    if ($e.is(':checked')) {
                        spanTag.addClass(options.checkedClass);
                    }
                    if ($e.is(':disabled')) {
                        divTag.addClass(options.disabledClass);
                    } else {
                        divTag.removeClass(options.disabledClass);
                    }

                } else if ($e.is(':radio')) {
                    //element is a radio
                    var spanTag = $e.closest('span');
                    var divTag = $e.closest('div');

                    divTag.removeClass(options.hoverClass + ' ' + options.focusClass + ' ' + options.activeClass);
                    spanTag.removeClass(options.checkedClass);

                    if ($e.is(':checked')) {
                        spanTag.addClass(options.checkedClass);
                    }

                    if ($e.is(':disabled')) {
                        divTag.addClass(options.disabledClass);
                    } else {
                        divTag.removeClass(options.disabledClass);
                    }
                } else if ($e.is(':file')) {
                    var divTag = $e.parent('div');
                    var filenameTag = $e.siblings(options.filenameClass);
                    btnTag = $e.siblings(options.fileBtnClass);

                    divTag.removeClass(options.hoverClass + ' ' + options.focusClass + ' ' + options.activeClass);

                    filenameTag.text($e.val());

                    if ($e.is(':disabled')) {
                        divTag.addClass(options.disabledClass);
                    } else {
                        divTag.removeClass(options.disabledClass);
                    }
                } else if ($e.is(':submit') || $e.is(':reset') || elemIs($e, 'button') || elemIs($e, 'a') || $e.is('input[type="button"]')) {
                    var divTag = $e.closest('div');
                    divTag.removeClass(options.hoverClass + ' ' + options.focusClass + ' ' + options.activeClass);

                    if ($e.is(':disabled')) {
                        divTag.addClass(options.disabledClass);
                    } else {
                        divTag.removeClass(options.disabledClass);
                    }
                }
            });
        };

        return this.each(function () {
            if ($.support.selectOpacity) {
                var elem = $(this);

                if (elem.data('uniformed')) {
                    $.uniform.update(elem);
                } else {
                    if (elemIs(elem, 'select')) {
                        //element is a select
                        if (elem.attr('multiple') !== true) {
                            //element is not a multi-select
                            if (elem.attr('size') === undefined || elem.attr('size') <= 1) {
                                doSelect(elem);
                            }
                        }
                    } else if (elem.is(':checkbox')) {
                        //element is a checkbox
                        doCheckbox(elem);
                    } else if (elem.is(':radio')) {
                        //element is a radio
                        doRadio(elem);
                    } else if (elem.is(':file')) {
                        //element is a file upload
                        doFile(elem);
                    } else if (elem.is(':text, :password, input[type="email"]')) {
                        doInput(elem);
                    } else if (elemIs(elem, 'textarea')) {
                        doTextarea(elem);
                    } else if (elemIs(elem, 'a') || elem.is(':submit') || elem.is(':reset') || elemIs(elem, 'button') || elem.is('input[type="button"]')) {
                        doButton(elem);
                    }

                    elem.data('uniformed', true);
                }
            }
        });
    };
})(jQuery);