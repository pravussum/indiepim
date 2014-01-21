define(function (require) {
    var serverPath = require('path!../../../server/');
	return {
	    urls: {
	        maillist: serverPath + "maillist.txt",
                accounts: serverPath + "accounts.txt"
	    }		
	};            
});