define(function(require) {

	var Boiler = require('Boiler'),
		settings = require('./settings'),
		ContactListComponent = require('./contactListComponent/component'),
		ContactViewComponent = require('./contactViewComponent/component');

	return {
		initialize : function(parentContext) {

			var context = new Boiler.Context(parentContext);
			context.addSettings(settings);

			var controller = new Boiler.UrlController($("#content"));
			controller.addRoutes({
				'contactlist' : new ContactListComponent(context),
				'contactview' : new ContactViewComponent(context)
			});
			controller.start();

		}
	};

});
