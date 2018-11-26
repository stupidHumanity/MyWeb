/**
 * 方法执行前注入方法
 * @param injection
 * @returns {Function} 返回新的方法，需覆盖原方法才能生效
 */
Function.prototype.inject = function (injection) {
    if (typeof injection !== "function") console.error("injection must be a fucntion !");
    var host=this as Function;
    return  function () {
        var args=Array.prototype.slice.call(arguments)
        if(injection.apply(null,args)!=false){
            host.apply(null,args);
        }
    }
}

/**
 * 获取系统位数
 * @returns {String byte}
 */
function getSysByte () {
    return navigator.userAgent.toLowerCase().match(/wow\d+/).toString().match(/\d+/).toString();
}