//support by jquery.js
;(function ($) {

    //  var request = (typeof window.XMLHttpRequest) == "function" ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");

    XMLHttpRequest.prototype.open = function (type, url, async, user, pass) {
        console.log("=====args=========");
        for (var i = 0; i < arguments.length; i++) {
            console.log(arguments[i]);
        }
        // this.abort();
      //  this.send(null);
        console.log("=====args=========");
    }

})(jQuery);