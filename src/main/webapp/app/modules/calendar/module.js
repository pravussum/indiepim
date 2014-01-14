define(function(require) {

	var Boiler = require('Boiler'),
		settings = require('./settings'),
		CalendarSettings = require('./settingsComponent/component'),
        CalendarComponent = require('./calendarComponent/component');

	return {
		initialize : function(parentContext) {

			var context = new Boiler.Context(parentContext);
			context.addSettings(settings);

			var controller = new Boiler.UrlController($("#content"));
			controller.addRoutes({
				'calendar' : new CalendarComponent(context),
                'calendarsettings' : new CalendarSettings(context)
			});
			controller.start();

		}
	};

});
