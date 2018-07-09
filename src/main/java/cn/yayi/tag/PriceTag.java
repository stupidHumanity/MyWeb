package cn.yayi.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class PriceTag extends BodyTagSupport {
    private String value;


    @Override
    public int doStartTag() throws JspException {
        DecimalFormat df = new DecimalFormat("###,###,###.00");
        try {
            BigDecimal price = new BigDecimal(this.value);
            this.value = df.format(price);
            this.value = this.value.replace(",", " ");
            pageContext.getOut().write(this.value);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return SKIP_BODY;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
