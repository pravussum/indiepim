ko.bindingHandlers.tokenInput = {
    disableUpdate : false,
    init: function(element, valueAccessor, allBindings, viewModel, bindingContext) {
        //var url = allBindings.get('tokenInputUrl') || ''; // KNOCKOUT 3 way
        var url = allBindings().tokenInputUrl || ''; // KNOCKOUT 2 way
//                        var options = allBindings.get('tokenInputOptions') || {}; // KNOCKOUT 3 way
        var options = allBindings().tokenInputOptions || {}; // KNOCKOUT 2 way
        tokenVal = options.tokenValue || "id";
        options.onAdd = function(item){
            if(disableUpdate)
                return;
            disableUpdate = true;
            valueAccessor().push(item);
            disableUpdate = false;
        };
        options.onFreeTaggingAdd = function(item){
            return item;
        };
        options.onDelete = function(item) {
            if(disableUpdate)
                return;
            disableUpdate = true;
            valueAccessor().remove(function(obs) {
                return obs[tokenVal] === item[tokenVal];
            });
            disableUpdate = false;
        };

        $(element).tokenInput(url, options);
//                        var value = ko.unwrap(valueAccessor()); // KNOCKOUT 3 way
        var value = ko.utils.unwrapObservable(valueAccessor()); // KNOCKOUT 2 way
        disableUpdate = true;
        for(i=0; i<value.length; i++) {
//                            $(element).tokenInput("add", ko.unwrap(value[i])); // KNOCKOUT 3 way
            $(element).tokenInput("add", ko.utils.unwrapObservable(value[i])); // KNOCKOUT 2 way
        }
        disableUpdate = false;
    },
    update: function(element, valueAccessor, allBindings, viewModel, bindingContext) {
        // clear and refill the field - performance issue? flickering?
        if(disableUpdate)
            return;
        disableUpdate = true;
        $(element).tokenInput("clear");
//                        var value = ko.unwrap(valueAccessor()); // KNOCKOUT 3 way
        var value = ko.utils.unwrapObservable(valueAccessor()); // KNOCKOUT 2 way
        for(i=0; i<value.length; i++) {
//                            $(element).tokenInput("add", ko.unwrap(value[i])); // KNOCKOUT 3 way
            $(element).tokenInput("add", ko.utils.unwrapObservable(value[i])); // KNOCKOUT 2 way
        }
        disableUpdate = false;
    }
};