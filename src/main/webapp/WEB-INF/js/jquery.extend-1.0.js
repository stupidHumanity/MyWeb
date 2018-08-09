;(function ($) {
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
        $.extend(setting, options);
        var data = {};
        var tags = setting.base;
        if (setting.extra != undefined) {
            tags = tags + "," + setting.extra;
        }
        var items = $(this).find(tags);
        if (items.size() == 0) {
            console.log("no filed found in this container");
            return data;
        }
        items.each(function (i, e) {
            var item = $(e);
            var hidden = item.css("display") == "none";
            var name = item.attr("name");
            //没有name属性不序列化
            if (name == undefined || name == "") return;
            var baseTags = setting.base.split(",");
            var isBase = false;
            for (var i = 0; i < baseTags.length; i++) {
                if (item.is(baseTags[i])) {
                    isBase = true;
                    break;
                }
            }
            var value;
            if (isBase) {
                value = item.val();
            }
            else {
                value = item.text();
            }
            //配置要求隐藏元素不序列化
            if (hidden && !setting.includeHidden) return;
            //控件值异常不序列化
            if (value == undefined) return;
            value = value.trim();
            //配置要求空值不序列化
            if (value == "" && setting.allowEmpty == false) return;
            data[name] = value;
        });
        for (var key in data) {
            var value = data[key];
            //去掉字符串前后的空格
            if (setting.trimString) {
                value = typeof(value) == "string" ? value.trim() : value;
                data[key] = value;
            }
            value = data[key];
            //是否允许空值
            if (!setting.allowEmpty && value == "") {
                delete data[key];
            }
        }
        return data;
    }
})(jQuery);