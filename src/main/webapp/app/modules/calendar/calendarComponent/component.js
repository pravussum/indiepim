define(['Boiler', 'text!./view.html', './viewmodel', './fullcalviewmodel', 'path!./style.css', 'i18n!./nls/resources'],
    function(Boiler, template, ViewModel, FullCalViewModel, styleText, nls) {

	var Component = function(moduleContext) {

		var panel, vm = null;
		
		this.activate = function(parent, params) {
			if (!panel) {
				panel = new Boiler.ViewTemplate(parent, template, nls);
                panel.getJQueryElement().addClass("fillParent");
                vm = new ViewModel(moduleContext);
				ko.applyBindings(vm, panel.getDomElement());
                Boiler.ViewTemplate.setStyleLink(styleText);
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
