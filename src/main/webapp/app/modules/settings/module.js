define(function(require) {

	var Boiler = require('Boiler'),
		settings = require('./settings'),
		SettingsComponent = require('./settingsComponent/component');

	return {
		initialize : function(parentContext) {

			var context = new Boiler.Context(parentContext);
			context.addSettings(settings);

			var controller = new Boiler.UrlController($("#content"));
			controller.addRoutes({
                'settings' : new SettingsComponent(context)
			});
			controller.start();

		}
	};

});
