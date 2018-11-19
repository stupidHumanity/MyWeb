;(function ($) {
    var ondebug=true;

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
        setting.changed = false; // 是否只序列化修改过的
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
                //判断是否只序列化修改过的
                if (setting.changed == true) {
                    if (item.attr("isChanged") == "no") {
                        return;
                    }
                }
                //设置排除标签的不序列化
                if (e.hasAttribute(setting.exclude)) {
                    return;
                }

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

                    if (e.tagName == "INPUT" && item.attr("type") == 'radio') {//多选按钮特殊处理

                        if (data[name] != undefined) return;
                        var $checked = $(setting.area).find("input[name='" + name + "']:checked");
                        if ($checked.length == 0) {
                            data[name] = "";
                            return;
                        }
                        data[name] = $checked.val();
                        var text = $checked.attr("text");

                        if (text != undefined) {
                            var attrName = name.endsWith("Code") ? name.replace("Code", "") : name + "Text";
                            data[attrName] = text;
                        }
                    }
                    else if (e.tagName == "INPUT" && item.attr("type") == 'checkbox') {
                        data[name] = item.prop("checked");
                    }
                    else {

                        value = item.val();

                    }
                }
                else {
                    value = item.text();
                }
                //配置要求隐藏元素不序列化
                if (hidden && !setting.includeHidden) return;
                //控件值异常不序列化
                if (value == undefined) return;
                //配置要求空值不序列化
                var trimValue = value.trim();
                if (trimValue == "" && setting.allowEmpty == false) return;
                //select双name文本序列化
                if (e.tagName == "SELECT" && name.indexOf(",") > -1) {
                    var names = name.split(",");
                    data[names[0]] = trimValue;
                    data[names[1]] = $(e).find("option[value='" + value + "']").text().trim();
                } else {
                    data[name] = trimValue;
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

        if (data == null) {
            return true;
        }
        else if (data == undefined) {
            return true;
        }
        else if (data.toString() == "") {
            return true;
        } else if ((typeof data) == 'object') {
            var count = 0;
            for (var key in data) {
                count++;
            }
            return count == 0;
        }
        return false;
    }


    /**
     * 输出错误提示
     * @param message
     */
    function consoleError(message){
        if(ondebug){
        console.error(message);
        }
    }

})(jQuery);


