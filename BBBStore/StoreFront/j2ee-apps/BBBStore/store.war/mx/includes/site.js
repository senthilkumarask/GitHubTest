$(function() {
	  
	  
	//load the survey form via ajax and append it to the body
	$.ajax({
		url: "surveyForm.html",
		success: function (data) { 
			$('body').append(data); 
			
			$("input[name=emailList]").change(function() {
				($("#emailListYes").prop("checked") == true) ? $('#emailListEmail').show() : $('#emailListEmail').hide() ;
			});
			
			var q1 = $( "#q1" ),
			  q2 = $( "#q2" ),
			  q3 = $( "#q3" ),
			  allFields = $( [] ).add( q1 ).add( q2 ).add( q3 ),
			  tips = $( ".validateTips" );
			
                         var wHeight = $(window).height();
                        var dHeight = wHeight * 0.8;
                        
                        
			$( "#dialog-form" ).dialog({
			  autoOpen: false,
			  height: dHeight,
			  width: '85%',
			  modal: true,
			  buttons: {
				"Enviar encuesta": function(event) {
					var bValid = true;
				  	allFields.removeClass( "ui-state-error" );
					
					//bValid = bValid && checkLength( q1, "username", 1, 16 );
					//bValid = bValid && checkLength( q2, "email", 1, 80 );
					//bValid = bValid && checkLength( q3, "password", 1, 16 );
					
					//bValid = bValid && checkRegexp( name, /^[a-z]([0-9a-z_])+$/i, "Username may consist of a-z, 0-9, underscores, begin with a letter." );
					// From jquery.validate.js (by joern), contributed by Scott Gonzalez: http://projects.scottsplayground.com/email_address_validation/
					//bValid = bValid && checkRegexp( email, /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i, "eg. ui@jquery.com" );
					//bValid = bValid && checkRegexp( password, /^([0-9a-zA-Z])+$/, "Password field only allow : a-z 0-9" );
					
					if ( bValid ) {
						//$( "#users tbody" ).append( "<tr>" +
						// "<td>" + name.val() + "</td>" +
						//  "<td>" + email.val() + "</td>" +
						//  "<td>" + password.val() + "</td>" +
						//"</tr>" );
				
						$(event.target).hide();// hide the submit button
						$.ajax({
							//url: 'http://www.bedbathandbeyond.com.mx/surveyPost.php',
							url: 'http://www.bedbathandbeyond.com.mx/surveyPostbbb.php',
							type: 'post',
							crossDomain: true,
							data: $('#surveyForm').serialize(),
							dataType: 'json',
							success: function(response) {
								//alert(response);
								if(response.status == 'success')
								{
									$('#surveyFormContainer').html('Gracias por participar en la encuesta!');
									//$("#updateEditorConfirm").disDialog("close");
									//$.get(response.htmlUrl, function(data){
									//	$('#dialog-form').fadeOut(100).replaceWith('Thanks for taking the survey!').fadeIn(100);
									//});
								}
								else if(response.status == 'error')
								{
									alert('Error: '+response.msg);
								}
							}
						});
					
						$(".ui-dialog-buttonpane button:contains('Cancel') span").text("Close");
						//$( this ).dialog( "close" );
					}
				},
				Cancelar: function() {
				  $( this ).dialog( "close" );
				}
			  },
			  close: function() {
				allFields.val( "" ).removeClass( "ui-state-error" );
			  }
			});
		},
		dataType: 'html'
	});	  
	  
	  
    
 
    function updateTips( t ) {
      $(".validateTips")
        .text( t )
        .addClass( "ui-state-highlight" );
      setTimeout(function() {
        $(".validateTips").removeClass( "ui-state-highlight", 1500 );
      }, 500 );
    }
 
    function checkLength( o, n, min, max ) {
      if ( o.val().length > max || o.val().length < min ) {
        o.addClass( "ui-state-error" );
        updateTips( "Length of " + n + " must be between " +
          min + " and " + max + "." );
        return false;
      } else {
        return true;
      }
    }
 
    function checkRegexp( o, regexp, n ) {
      if ( !( regexp.test( o.val() ) ) ) {
        o.addClass( "ui-state-error" );
        updateTips( n );
        return false;
      } else {
        return true;
      }
    }
 
 	
    
 
	//$("body").on("click", "#surveyLink", function() {
	//	$( "#dialog-form" ).dialog( "open" );
	//	return false;
    //});
 
    $( "#surveyLink, .surveyLink" ).click(function() {
        $( "#dialog-form" ).dialog( "open" );
		return false;
    });
});

