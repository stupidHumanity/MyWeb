package cn.yayi.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class ActionLogger {
    @AfterReturning(pointcut = "execution( * net.dgg.accountingtools.models.bill.service.impl.BillServiceImpl.spanBill (..))")
    public void log(JoinPoint jp){
        Object[] parames = jp.getArgs();//获取目标方法体参数
      //  String params = parseParames(parames); //解析目标方法体的参数
        String className = jp.getTarget().getClass().toString();//获取目标类名
        className = className.substring(className.indexOf("com"));
        String signature = jp.getSignature().toString();//获取目标方法签名
        String methodName = signature.substring(signature.lastIndexOf(".")+1, signature.indexOf("("));
      //  String modelName = getModelName(className); //根据类名获取所属的模块


    }

}
