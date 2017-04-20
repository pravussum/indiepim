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
                // intialize color picker
                $("#calendarColor").colpick({layout:'rgbhex', onSubmit:vm.colorChanged});
			}
            if(params.calendarId) {
                vm.loadCalendar(params.calendarId);
            } else {
                vm.clearCalendar();
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
