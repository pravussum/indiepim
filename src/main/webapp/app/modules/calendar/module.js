define(function(require) {

	var Boiler = require('Boiler'),
		settings = require('./settings'),
		ImportComponent = require('./importComponent/component'),
        CalendarComponent = require('./calendarComponent/component'),
        CalendarSettings = require('./settingsComponent/component');

	return {
		initialize : function(parentContext) {

			var context = new Boiler.Context(parentContext);
			context.addSettings(settings);

			var controller = new Boiler.UrlController($("#content"));
			var settings = new CalendarSettings(context);
            controller.addRoutes({
				'import' : new ImportComponent(context),
                'calendar' : new CalendarComponent(context),
                'calendarsettings/{calendarId}' : settings,
                'addcalendar' : settings
			});
			controller.start();

		}
	};

});
