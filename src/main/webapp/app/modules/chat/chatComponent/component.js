define(['Boiler', './viewmodel'],
        function(Boiler, ViewModel) {

	var Component = function(moduleContext) {

        var vm = null;

		// called without UrlController when module is created
        this.activate = function(parent) {
			if (!vm) {
				vm = new ViewModel(moduleContext);
			}
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
            vm = null;
		}
	};

	return Component;

});
