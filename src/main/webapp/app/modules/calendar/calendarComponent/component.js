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
                $("#calendar").fullCalendar({
                    eventSources: [
                        {   // JSON data source - our server
                            url:"/command/getEvents",
                            data: {
                                calendarId: "1234"
                            }
                        }
                    ],
                    header:{
                        left:"today",
                        center:"prev, title, next",
                        right:"month, agendaWeek, agendaDay"
                    },
                    theme: false,
                    // TODO make configurable!
                    firstDay: 1,
                    weekMode: "variable",
                    aspectRatio: 2.0,
                    selectable: true,
                    selectHelper: true,
                    editable: true,
                    defaultView: "agendaWeek",
                    firstHour: 6,
                    titleFormat: {
                        month: 'MMMM yyyy',                             // September 2009
                        week: "d.[ MMM][ yyyy]{ '&#8212;' d. MMM yyyy}", // Sep 7 - 13 2009
                        day: 'dddd, d. MMMM, yyyy'                  // Tuesday, Sep 8, 2009
                    },
                    monthNames: ['Januar', 'Februar', 'März', 'April', 'Mai', 'Juni', 'Juli',
                     'August', 'September', 'Oktober', 'November', 'Dezember'],
                    monthNamesShort: ['Jan', 'Feb', 'Mär', 'Apr', 'Mai', 'Jun',
                     'Jul', 'Aug', 'Sep', 'Okt', 'Nov', 'Dez'],
                    dayNames: ['Sonntag', 'Montag', 'Dienstag', 'Mittwoch',
                     'Donnerstag', 'Freitag', 'Samstag'],
                    dayNamesShort: ['So', 'Mo', 'Di', 'Mi', 'Do', 'Fr', 'Sa'],
                    weekNumberTitle: "KW",
                    ignoreTimezone: false
                });
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
