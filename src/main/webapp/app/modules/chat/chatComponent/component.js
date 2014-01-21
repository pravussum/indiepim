define(['Boiler', 'text!./view.html', './viewmodel', 'path!./style.css', 'i18n!./nls/resources'], 
        function(Boiler, template, ViewModel, styleText, nls) {

	var Component = function(moduleContext) {

        var panel, vm = null;

		// called without UrlController when module is created
        this.activate = function(parent) {
			if (!panel) {
				panel = new Boiler.ViewTemplate(parent, template, nls);
				vm = new ViewModel(moduleContext);
				ko.applyBindings(vm, panel.getDomElement());
                Boiler.ViewTemplate.setStyleLink(styleText);
			}
            panel.show();

            moduleContext.listen("NEW_CHAT_MESSAGE", function(data) {
                vm.addChat( data.fromUserId, data.fromUserName);
                vm.sendMessage(data.fromUserId, data.message);
            });

            moduleContext.listen("OPEN_CHAT", function(data) {
                vm.addChat(data.userId, data.userName);
            });
		}

        // is never called
		this.deactivate = function() {
			if(panel) {
				panel.hide();
			}
		}
	};

	return Component;

});
