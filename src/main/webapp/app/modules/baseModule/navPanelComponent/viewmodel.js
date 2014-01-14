define([], function () {

    var MessageAccount = function(data) {
        var self = this;
        self.id = ko.observable(data.id);
        self.accountName = ko.observable(data.accountName);
        self.tagHierarchy = new ko.ui.Tree({
            nodes : data.tagHierarchy.rootElement.nodes
        });
        self.url = ko.computed(function(){
            return '#maillist/account/' + self.id();
        });
        self.tagHierarchy.subscribe("click", function(event) {
            if(event.node.id) {
                window.location.href = "#maillist/taglineage/" + event.node.id;
            }
        });
        self.syncAccount = function() {
            $.getJSON(
                "command/syncMessageAccount/" + self.id(),
                function(result) {

                }
            ).fail(function(xhr, textStatus, errorThrown) {
                    if(xhr.status == 403)
                        window.location.href = contextPath + "/login"; // access denied = not logged in --> redirect to login
                    else
                        toastr.error(JSON.stringify(textStatus));
                });
        }
    };

    var ViewModel = function (moduleContext) {

        var self = this;

        self.messageAccounts = ko.observableArray();
        self.selectedAccount = ko.observable();

        self.selectAccount = function(account) {
            self.selectedAccount(account);
        };

        var initTree = function() {
            $.getJSON("command/getMessageAccounts",
                function(data) {
                    if(typeof data.error != "undefined") {

                        toastr.error("Receiving the account list failed. See server log for details.");
                    } else {
                        self.messageAccounts($.map(data.accounts, function(item) {return new MessageAccount(item)}));
                    }
                }
            ).fail(function(xhr, textStatus, errorThrown) {
                if(xhr.status == 403)
                    window.location.href = contextPath + "/login"; // access denied = not logged in --> redirect to login
                else
                    toastr.error(JSON.stringify(textStatus));
            });
        };
        initTree();

    };
    
    return ViewModel;
});
