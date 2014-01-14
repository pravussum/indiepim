define(['Boiler', 'text!./view.html', './viewmodel', 'path!./style.css', 'i18n!./nls/resources'], 
        function(Boiler, template, ViewModel, styleText, nls) {

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

            if(params.taglineageid) {
                vm.getMailsForTagLineageId(params.taglineageid);
            } else if(params.accountid) {
                vm.getMailsForAccount(params.accountid);
            } else if(params.query) {
                vm.search(params.query);
            } else if(params.readFlag) {
                vm.getMailsByReadFlag(params.readFlag);
            } else {
                vm.getAllMails();
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
