define(function(require) {

	/**
	 *Namespace variable defining helper classes mainly used by the core classes in 'Boiler' namespace.
	
	 @type Script
	 @namespace Boiler.Helpers
	 @module BoilerCoreClasses
	 @main BoilerCoreClasses
	**/
	return {
		Localizer : require("./localizer"),
		Mediator : require("./mediator"),
//        Logger : require("./logger"),
        Router : require("./router"),
		Settings : require("./settings"),
		Styler : require("./styler")
	};
}); 