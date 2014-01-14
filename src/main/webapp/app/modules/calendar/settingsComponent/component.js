define(['Boiler', 'text!./view.html', './viewmodel', 'path!./style.css', 'i18n!./nls/resources'], function(Boiler, template, ViewModel, styleText, nls) {

	var Component = function(moduleContext) {

		var panel, vm = null;
		
		this.activate = function(parent, params) {
			if (!panel) {
				panel = new Boiler.ViewTemplate(parent, template, nls);
                panel.getJQueryElement().addClass("fillParent");
				vm = new ViewModel(moduleContext);
				ko.applyBindings(vm, panel.getDomElement());
                Boiler.ViewTemplate.setStyleLink(styleText);
                $('#uploadform').ajaxForm(function(result) {
                    $('#uploadresult').html(result);
                });

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
