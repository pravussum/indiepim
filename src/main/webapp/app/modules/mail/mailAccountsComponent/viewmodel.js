define([], function () {

    var MessageAccount = function(data) {
        var self = this;
        self.id = ko.observable(data.id);
        self.email = ko.observable(data.email);
        self.accountName = ko.observable(data.accountName);
        self.userName = ko.observable(data.userName);
        self.host = ko.observable(data.host);
        self.outgoingHost = ko.observable(data.outgoingHost);
        self.port = ko.observable(data.port);
        self.outgoingPort = ko.observable(data.outgoingPort);
        self.authentication = ko.observable(data.authentication);
        self.outgoingAuthentication = ko.observable(data.outgoingAuthentication);
        self.encryption = ko.observable(data.encryption);
        self.outgoingEncryption = ko.observable(data.outgoingEncryption);
        self.tag = ko.observable(data.tag);
        self.password = ko.observable(data.password);
        self.protocol = ko.observable(data.protocol);
        self.tagHierarchy = ko.observable(data.tagHierarchy);
        self.syncMethod = ko.observable(data.syncMethod);
        self.syncInterval = ko.observable(data.syncInterval);
        self.lastSyncRun = ko.observable(data.lastSyncRun);
        self.newMessages = ko.observable(data.newMessages);
        self.trustInvalidSSLCertificates = ko.observable(data.trustInvalidSSLCertificates);
        self.version = data.version; /* update timestamp */
        self.syncProgress = ko.observable("...");
        self.syncProgressValue = ko.observable(0);
        self.syncActive = ko.observable(false);

    };

    function mapDictionaryToArray(dictionary) {
        var result = [];
        for (var key in dictionary) {
            if (dictionary.hasOwnProperty(key)) {
                result.push({ key: key, value: dictionary[key] });
            }
        }

        return result;
    }

	var ViewModel = function (moduleContext) {
        var self = this;

        self.availableEncryptions = mapDictionaryToArray({"NONE":1,"STARTTLS":2,"TLS":3,"SSL":4});
        self.availableAuthentications = mapDictionaryToArray({"NONE":1,"PASSWORD_NORMAL":2});
        self.availableSyncUpdateMethods = mapDictionaryToArray({"FLAGS":1,"FULL":2});
        self.availableProtocols = mapDictionaryToArray({"IMAP":1,"POP3":2});

        self.messageAccounts = ko.observableArray();
        self.selectedAccount = ko.observable();

        self.selectAccount = function(account) {
            self.selectedAccount(account);
        };

        self.addAccount = function() {
            self.messageAccounts.push(new MessageAccount({accountName : "New Account"}));
        }

        self.removeAccount = function(account) {
            self.selectedAccount(undefined);
            self.messageAccounts.remove(account);
            // TODO implement account removal
        }

        self.saveAccount = function(account) {
            var url = "command/createOrUpdateMessageAccount";
            $.ajax(url, {
                data: ko.toJSON(account),
                type: "post",
                contentType: "application/json",
                success : function(result) {
                    if(typeof result.error != "undefined")
                        toastr.error("Saving account failed. See server log for details." + JSON.stringify(result.error));
                    else {
                        if(typeof result.id === 'undefined' || result.id == null) {
                            toastr.error("No valid account id received.");
                        } else {
                            account.id = result.id;
                            account.version = result.version;
                            toastr.success("Account saved successfully.");
                        }
                    }
                },
                error : function(error, textStatus, errorThrown) {
                    if(error.status == 403)
                        window.location.href = contextPath + "/login";
                    else
                        toastr.error("Saving account failed. See server log for details.");
                }
            });
        }

        var initAccounts = function() {
            var url = "command/getMessageAccounts";
            $.getJSON(url, function(data) {
                    if(typeof data.error != "undefined") {
                        toastr.error("Receiving the account list failed. See server log for details.");
                    } else {
                        var mappedAccounts = $.map(data.accounts, function(item) {return new MessageAccount(item)});
                        self.messageAccounts(mappedAccounts);
                    }
                }
            ).fail(function(xhr, textStatus, errorThrown) {
                if(xhr.status == 403)
                    window.location.href = contextPath + "/login"; // access denied = not logged in --> redirect to login
                else
                    toastr.error(JSON.stringify(textStatus));
            })
        };
        initAccounts();
        // catch sync progress message for this account and show/update progress bar
        moduleContext.listen("ACCOUNT_SYNC_PROGRESS", function(data) {
            if(self.selectedAccount() != undefined && self.selectedAccount().id() == data.accountId) {
                self.selectedAccount().syncProgress(data.msgDoneCount + " of " + data.msgCount + " in folder " + data.folder + " done.")
                var progress;
                if(data.msgCount == 0) {
                    progress = 0.0;
                } else {
                    progress = data.msgDoneCount  / data.msgCount * 100.0;
                }
                self.selectedAccount().syncProgressValue(progress);
                self.selectedAccount().syncActive(true);
            }
        });
        // catch sync end message for this account: update sync date and hide progress bar
        moduleContext.listen("ACCOUNT_SYNCED", function(data){
            if(self.selectedAccount() != undefined && self.selectedAccount().id() == data.accountId) {
                self.selectedAccount().lastSyncRun(data.syncDate);
                self.selectedAccount().syncActive(false);
            }
        });
    };

    ko.bindingHandlers.progressBind = {
        init: function(element, valueAccessor) {
            $(element).progressbar({
                value: valueAccessor()()
            });
        },
        update: function (element, valueAccessor) {
            //assign observable value to progress bar value
            $(element).progressbar("value", valueAccessor()());
        }
    };

    return ViewModel;
});
