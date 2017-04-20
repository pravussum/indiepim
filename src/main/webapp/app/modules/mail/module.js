define(function(require) {

	var Boiler = require('Boiler'),
		settings = require('./settings'),
		MailListComponent = require('./mailListComponent/component'),
		MailViewComponent = require('./mailViewComponent/component'),
        MailComposeComponent = require('./mailComposeComponent/component'),
        MailAccountsComponent = require('./mailAccountsComponent/component');

	return {
		initialize : function(parentContext) {

			var context = new Boiler.Context(parentContext);
			context.addSettings(settings);

			var controller = new Boiler.UrlController($("#content"));
			var listComponent = new MailListComponent(context);
            var composeComponent = new MailComposeComponent(context);
            controller.addRoutes({
				'maillist' : listComponent,
                'maillist/taglineage/{taglineageid}' : listComponent,
                'maillist/account/{accountid}' : listComponent,
                'maillist/search/{query}' : listComponent,
                'maillist/read/{readFlag}' : listComponent,
                'mailview/{id}' : new MailViewComponent(context),
                'mailcompose' : composeComponent,
                'mailcompose/forward/{forwardid}' : composeComponent,
                'mailcompose/reply/{replyid}' : composeComponent,
                'mailcompose/replyall/{replyallid}' : composeComponent,
                'mailaccounts' : new MailAccountsComponent(context)
			});
			controller.start();

		}
	};

});
