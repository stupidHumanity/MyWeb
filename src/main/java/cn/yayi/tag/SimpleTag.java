package cn.yayi.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;

public class SimpleTag extends BodyTagSupport {
    private String value="";
    //doStartTag()→doInitBody()→setBodyContent()→doAfterBody()→doEndTag()
    public String initBody(String value) {
        return value;
    }

    @Override
    public int doStartTag() throws JspException {
        //EVAL_BODY_INCLUDE,SKIP_BODY,EVAL_BODY_BUFFERED
        return EVAL_BODY_BUFFERED;
    }

    @Override
    public int doAfterBody() throws JspException {
        //EVAL_BODY_AGAIN、SKIP_BODY、EVAL_BODY_BUFFERED
        value = this.getBodyContent().getString();
        return SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {

        JspWriter out = this.pageContext.getOut();
        value = initBody(value);

        if (value != null && !value.equals("")) {
            try {
                out.write(value);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //EVAL_PAGE、SKIP_PAGE
        return EVAL_PAGE;
    }
}
