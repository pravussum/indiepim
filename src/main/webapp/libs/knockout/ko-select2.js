ko.bindingHandlers.select2 = {
    disableKOS2Update : false,
    init: function(element, valueAccessor, allBindings, viewModel, bindingContext) {

        var options = allBindings().select2options || {}; // KNOCKOUT 2 way
        $(element).select2(options);


        // ui control update
        $(element).bind("change", function(event) {
            if(disableKOS2Update)
                return;
            disableKOS2Update = true;
            if(event.added) {
                valueAccessor().push(event.added);
            }
            if(event.removed) {
                valueAccessor().remove(event.removed);
            }
            disableKOS2Update = false;
        });

        // initialize ui control by viewmodel data
        var value = ko.utils.unwrapObservable(valueAccessor());
        disableKOS2Update = true;
        $(element).select2("data",value);
        disableKOS2Update = false;

        // viewmodel update - to work around knockout update function not always being fired.
        valueAccessor().subscribe(function(newValue) {
            if(disableKOS2Update)
                return;
            disableKOS2Update = true;
            $(element).select2("data",ko.utils.unwrapObservable(valueAccessor()));
            disableKOS2Update = false;
        });
    }
};