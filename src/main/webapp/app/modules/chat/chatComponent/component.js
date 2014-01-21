define(['Boiler', './viewmodel'],
        function(Boiler, ViewModel) {

	var Component = function(moduleContext) {

        var vm = null;

		// called without UrlController when module is created
        this.activate = function(parent) {
			if (!vm) {
				vm = new ViewModel(moduleContext);
			}
            console.log("adding NEW_CHAT_MESSAGE listener...");
            moduleContext.listen("NEW_CHAT_MESSAGE", function(data) {
                console.log("NEW_CHAT_MESSAGE, adding window for user " + data.fromUserId);
                vm.addChat( data.fromUserId, data.fromUserName);
                vm.sendMessage(data.fromUserId, data.message);
            });

            console.log("adding OPEN_CHAT listener...");
            moduleContext.listen("OPEN_CHAT", function(data) {
                console.log("OPEN_CHAT, adding window for user " + data.userId);
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
