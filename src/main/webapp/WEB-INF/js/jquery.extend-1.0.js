;(function ($) {
    var ondebug = true;

    /**
     * 单选checkBox
     */
    $.fn.singleChecked = function (options) {
        var settings = {};
        settings.after = undefined;//事件触发之后要做的事情
        if (options != undefined) ;
        $.extend(settings, options);
        settings.checkBoxs = $(this).find("input:checkbox");
        $(this).on("click", "input:checkbox", function () {
            var $e = $(event.target);
            var checked = $e.prop("checked");
            if (checked) {
                $(settings.checkBoxs).each(function (i, e) {
                    $(e).prop("checked", false);
                });
                $e.prop("checked", true);
            }
            if (settings.after != undefined) {
                settings.after(event.target, checked, settings.checkBoxs);
            }
        });

    };

    /**
     * 清空表单元素(包括不可见的),忽略具有fixed属性的
     */
    $.fn.clearValue = function (options) {
        var settings = {};
        if (options != undefined) ;
        $.extend(settings, options);
        settings.exclude = "fixed";//除外属性
        settings.base = "input,select,textarea";//基本元素
        settings.extra = undefined;//额外元素

        $(this).find(settings.base).each(function (i, e) {
            e = $(e);
            var fixed = e.attr(settings.exclude);
            if (fixed == undefined) {
                e.val("");
            }
        });
        if (settings.extra != undefined) {
            $(this).find(settings.extra).each(function (i, e) {
                e = $(e);
                var fixed = e.attr(settings.exclude);
                if (fixed == undefined) {
                    e.empty();
                }
            });


        }
    }

    /**
     * 将表单元素序列化成一个属性对象
     */
    $.fn.serialize2object = function (options) {
        var setting = {};
        setting.allowEmpty = false;//序列化空值
        setting.includeHidden = true;//序列化隐藏值
        setting.trimString = true;//trim值
        setting.extra = undefined;//额外序列化标签
        setting.base = "input,select,textarea";//序列化标签
        setting.area = this;
        setting.subSelector = null;
        setting.exclude = "exclude";
        $.extend(setting, options);
        if (setting.subSelector == null) {
            return doSerialize(this);
        } else {
            var datas = new Array();
            $(this).find(setting.subSelector).each(function (i, e) {
                var data = doSerialize(e);
                datas.push(data)
            });
            return datas;
        }

        function doSerialize(selector) {
            var data = {};
            var tags = setting.base;
            if (setting.extra != undefined) {
                tags = tags + "," + setting.extra;
            }
            var items = $(selector).find(tags);
            if (items.size() === 0) {
                console.log("no filed found in this container");
                return data;
            }
            items.each(function (i, e) {
                var item = $(e);
                var name = item.attr("name");
                var tagName = e.tagName.toLowerCase();
                //没有name属性不序列化
                if (name === undefined || name === "") return;
                //设置排除标签的不序列化
                if (e.hasAttribute(setting.exclude)) return;
                //配置要求隐藏元素不序列化
                if (item.css("display") === "none" && !setting.includeHidden) return;

                var value;
                if (setting.base.indexOf(tagName) > -1) {

                    if (e.tagName === "input" && item.attr("type") === 'radio') {//多选按钮特殊处理

                        if (data[name] !== undefined) return;
                        var $checked = $(setting.area).find("input[name='" + name + "']:checked");
                        if ($checked.length === 0) {
                            data[name] = "";
                            return;
                        }
                        data[name] = $checked.val();
                    }
                    else if (e.tagName === "input" && item.attr("type") === 'checkbox') {
                        data[name] = item.prop("checked");
                    }
                    else {
                        value = item.val();
                    }
                }
                else {
                    value = item.text();
                }

                var trim = value.trim();
                if (setting.trimString === true) value = trim;

                //配置要求空值不序列化
                if (trim === "" && setting.allowEmpty === false) return;
                //select双name文本序列化
                if (tagName === "select" && name.indexOf(",") > -1) {
                    var names = name.split(",");
                    data[names[0]] = trim;
                    data[names[1]] = $(e).find("option[value='" + value + "']").text().trim();
                } else {
                    data[name] = trim;
                }
            });

            return data;

        }
    }

    /**
     * 判断一个对象是否为空
     * @param data
     * @returns {boolean}
     */
    $.isEmpty = function (data) {
        console.log(typeof data);

        if (data === null || data === undefined || data.toString().trim().length === 0) {
            return true;
        } else if ((typeof data) === 'object') {
            var count = 0;
            for (var key in data) {
                count++;
            }
            return count === 0;
        }
    }

e
    /**
     * 方法执行前注入方法
     * @param injection
     * @returns {Function} 返回新的方法，需覆盖原方法才能生效
     */
    $.inject = function (host, injection) {
        if (typeof injection !== "function") console.error("injection must be a fucntion !");
        if (typeof host !== "function") console.error("host must be a fucntion !");
        return function () {
            var args = Array.prototype.slice.call(arguments)
            if (injection.apply(null, args) != false) {
                host.apply(null, args);
            }
        }
    }

    /**
     * 获取系统位数
     * @returns {String byte}
     */
    $.getSysByte = function () {
        return navigator.userAgent.toLowerCase().match(/wow\d+/).toString().match(/\d+/).toString();
    }


})(jQuery);


