define(function(require) {

	var Boiler = require('Boiler'),
		settings = require('./settings'),
		ChatComponent = require('./chatComponent/component');

	return {
		initialize : function(parentContext) {
			var context = new Boiler.Context(parentContext);
			context.addSettings(settings);
			var chat = new ChatComponent(context);
            chat.activate($("body"));
		}
	};

});
