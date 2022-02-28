import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface EventLog {
    //操作日志成功文本模板
    String success() default "";

    //操作日志失败文本模板
    String fail() default "";

    //操作日志执行人
    //String operator() default "";

    //操作日志绑定业务对象标示
    String bizNo() default "";

    //操作日志种类
    String category() default "";

    //拓展参数，记录操作日志修改详情
    String detail() default "";

    //记录日志条
    String condition() default "";

    //自定义执行函数，用于自定义记录其他记录，key在threadLocal拿
    String function() default "";


}