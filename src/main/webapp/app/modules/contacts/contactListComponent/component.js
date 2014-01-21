define(['Boiler', 'text!./view.html', './viewmodel', 'text!./style.css', 'i18n!./nls/resources'], function(Boiler, template, ViewModel, styleText, nls) {

	var Component = function(moduleContext) {
		Boiler.ViewTemplate.setStyleText("{CSS_IDENTIFIER}", styleText);

		var panel, vm = null;
		
		this.activate = function(parent, params) {
			if (!panel) {
				panel = new Boiler.ViewTemplate(parent, template, nls);
                panel.getJQueryElement().addClass("fillParent");
                vm = new ViewModel(moduleContext);
				ko.applyBindings(vm, panel.getDomElement());

				// Initialize Table Data
	            var dataSet = [
	                    /* Reduced data set */
	                    [ 'contact1', 'contact2'],
	                    [ 'contact2', 'contact4'],
	
	                ]
//	            $('#contactListTable').dataTable( {
//	                "aaData": dataSet,
//	                "aoColumns": [
//	                        { "sTitle": "C 1" },
//	                        { 'sTitle': "C 2" },
//	                    ],
//	                "bJQueryUI" : true,
//	            } );
			}

			panel.show();
		}

		this.deactivate = function() {
			if(panel) {
				panel.hide();
			}
		}
	};

	return Component;

});
