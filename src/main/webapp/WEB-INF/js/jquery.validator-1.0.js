function Validator() {
    var setting;
    initInnerVariables();
    generateCss();
    /**
     * 验证表单
     * @param selector 父级选择器
     * @return {number} 验证失败个数
     */
    this.validate = function (selector) {

        var failed = 0;
        //part input
        $(selector).find("input").each(function (i, e) {
            var $input = $(e);
            var flag = true;
            var methods = new Array("requireCheck", "numberCheck", "regexCheck");
            for (var i = 0; i < methods.length; i++) {
                var fn = eval(methods[i]);
                flag = fn.call(null, $input);
                if (!flag) {
                    failed++;
                    break;
                }
            }

        });

        //part select
        $(selector).find("select").each(function (i, e) {
            var select = $(e);
            var flag = true;
            var methods = ["selectCheck"];
            for (var i = 0; i < methods.length; i++) {
                var fn = eval(methods[i]);
                flag = fn.call(null, select);
                if (!flag) {
                    failed++;
                    break;
                }
            }
        });

        return failed;
    };

    /**
     * 设置参数
     * @param options: type,delay,regexLabel,messageLabel
     */
    this.setParameters = function (options) {
        $.extend(setting, options);
        generateCss();
    }

    /**
     * 清除输入框后面存在的提示信息
     * @param input input元素
     */
    function clearMessage(input) {
        var next = input.next();
        if (next.attr("name") == "autoAlert") {
            next.remove();
        }
    }

    /**
     * 显示正则验证失败的提示信息
     * @param $input input元素
     * @param time 提示延迟消失的时间（毫秒）
     */
    function showMessage($input, message) {
        if (message == undefined) {
            message = $input.attr(setting.messageLabel);
            if (containEmpty(message)) {
                showError("缺少必要的提示信息。");
                return;
            }
        }

        var offset = $input.offset();
        var x, y;
        if (setting.type == "below") {
            x = offset.left;
            y = offset.top + $input.outerHeight() + parseInt(setting.triangleSize) * 2;
        }
        else {
            x = offset.left + $input.outerWidth() + parseInt(setting.triangleSize) * 2;
            y = offset.top;
        }
        clearMessage($input);

        var container = $(document.createElement("div"));
        container.css("top", y + parseInt(setting.offset[1]));
        container.css("left", x + parseInt(setting.offset[0]));
        container.css("position", "absolute");
        container.css("z-index", "999");
        container.attr("name", "autoAlert");

        var triangle = $(document.createElement("i"));
        triangle.css(setting.triangleCss);
        container.append(triangle);

        var content = $(document.createElement("span"));
        content.css(setting.contentCss);
        content.append(message);
        container.append(content);
        $input.after(container);
        setTimeout(function () {
            container.remove();
        }, setting.delay);
    }

    /**
     * 判断值是否是空值
     * @param variable 变量
     * @returns {boolean} true：为空或未定义
     */
    function containEmpty() {
        var flag = false;
        for (var i = 0; i < arguments.length && flag == false; i++) {
            var arg = arguments[i];
            if (arg == undefined) {
                flag = true;
            }
            var type = typeof(arg);

            if (type == "string" && arg.trim() == "") {
                flag = true;
            }
            else if (arg == null) {
                flag = true;
            }
        }

        return flag;
    }

    /**
     * 数字验证
     * @param $input
     */
    function numberCheck($input) {

        var rule = $input.attr(setting.numberLabel);
        var value = $input.val().trim();
        if (containEmpty(rule, value)) return true;
        var regex = "^-?(?!0[0-9]+)[0-9]+(\\.[0-9]+)?$";
        var pattern = new RegExp(regex);
        if (!pattern.test(value)) {
            showMessage($input, "请输入一个数字。", setting.delay);
            return false;
        }
        rule = rule.trim();
        if (rule == "") return;
        regex = "^[0-9,\\s]+$";
        pattern = new RegExp(regex);
        if (!pattern.test(rule)) {
            showError("不合法的表达式[min，max，precision]。");
            return false;
        }

        var num = parseFloat(value);
        var args = rule.split(",");

        if (args.length > 0 && args[0].trim() != "") {
            var min = parseFloat(args[0]);
            if (num < min) showMessage($input, "请输入一个不小于" + min + "的数字。");
        }
        if (args.length > 1 && args[1].trim() != "") {
            var max = parseFloat(args[1]);
            if (num > max) showMessage($input, "请输入一个不大于" + max + "的数字。");
        }
        if (args.length > 2 && args[2].trim() != "") {
            var precision = args[2];
            regex = "(?<=\\.)[0-9]*";
            var str = new RegExp(regex).exec(value);
            var len = str == null ? 0 : str.toString().length;
            if (len > precision && precision > 0) {
                showMessage($input, "小数部分请不要超过" + precision + "位");
                return false;
            }
            else if (len > precision && precision == 0) {
                showMessage($input, "请输入一个整数！");
                return false;
            }
        }
        return true;
    }

    /**
     * 必填验证
     * @param input
     * @returns {boolean} true：验证通过
     */
    function requireCheck(input) {
        var essential = input.attr(setting.essentialLabel);
        if (essential == undefined) return true;
        //
        var val = input.val().trim();
        essential = essential.trim();
        if (containEmpty(val)) {
            var name = "该字段";
            if (essential != "" && essential != "essential") {
                name = essential;
            }
            showMessage(input, name + "不能为空！", setting.delay);
            return false;
        }
        return true;
    }

    /**
     * 正则验证
     * @param regex 正则表达式
     * @param value 值
     * @returns {boolean} true：验证通过
     */
    function regexCheck(input) {
        var regex = input.attr(setting.regexLabel);
        var msg = input.attr(setting.messageLabel);
        var value = input.val().trim();
        if (containEmpty(regex, msg, value)) return true;
        var pattern = new RegExp(regex);
        if (!pattern.test(value)) {
            showMessage(input, msg, setting.delay);
            return false;
        }
        return true;
    }

    /**
     * 初始化一些内置变量
     */
    function initInnerVariables() {

        setting = {
            type: "after",
            essentialLabel: "essential",
            numberLabel: "number",
            regexLabel: "regex",
            messageLabel: "message",
            delay: 5000,
            showError: false,
            offset: ["0px", "0px"],
            backgroundColor: "#E78081",
            fontColor: "FFF",
            triangleOffset: "5px",
            triangleSize: "7px",
            fontSize: "12px",
            padding: "5px 10px"

        };


    }

    /**
     * 检查是否有选择值
     * @param select
     */
    function selectCheck(select) {
        var value = select.val();
        var essential = select.attr(setting.essentialLabel);
        if (essential == undefined) {
            return true;
        }
        else if (containEmpty(value)) {
            var name = "该选项";
            if (essential != "" && essential != setting.essentialLabel) {
                name = essential;
            }
            showMessage(select, "请选择" + name + "。", setting.delay);
            return false;
        }
        return true;
    }

    function generateCss() {
        //
        var css_content = {
            "min-width": "12px",
            "border-radius": "2px",
            "position": "absolute",
            "box-shadow": " 1px 1px 3px rgba(0, 0, 0, .2)",
            "white-space": "nowrap"
        }
        setting.contentCss = css_content;
        setting.contentCss["padding"] = setting.padding;
        setting.contentCss["background-color"] = setting.backgroundColor;
        setting.contentCss["color"] = setting.fontColor;
        setting.contentCss["font-size"] = setting.fontSize;
        //
        var orderWidth = addPixel(setting.triangleSize, setting.triangleSize);
        var css_triangle = {
            "width": "0px",
            "height": "0px",
            "border-color": "transparent",
            "border-style": "dashed",
            "position": "absolute",
            "border-width": orderWidth
        };
        setting.triangleCss = css_triangle;

        if (setting.type == "below") {
            setting.triangleCss["left"] = addPixel(setting.triangleOffset, "-" + setting.triangleSize);
            setting.triangleCss["top"] = "-" + orderWidth;
            setting.triangleCss["border-right"] = orderWidth + " solid " + setting.backgroundColor;
        }
        else {
            if (setting.type != "after") {
                showError("unsupported type:" + setting.type + ",change to default:after ");
                setting.type = "after";
            }
            setting.triangleCss["top"] = addPixel(setting.triangleOffset, "-" + setting.triangleSize);
            setting.triangleCss["left"] = "-" + orderWidth;
            setting.triangleCss["border-bottom"] = orderWidth + " solid " + setting.backgroundColor;

        }


    }

    function showError(msg) {
        if (setting.showError) {
            console.error(msg);
        }
    }

    function addPixel() {
        var count = 0;
        for (var i = 0; i < arguments.length; i++) {
            count += parseInt(arguments[i]);
        }

        return count + "px";
    }

}
