import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义注解解析类（1.0）
 * 未加上自定义函数解析，后续需要再补上
 * @author  limbo
 * @Date  2022/2/28 17:20
 **/
@Component
public class EventLogOperationSource {
    @Autowired
    private LogRecordExpressionEvaluator logRecordExpressionEvaluator;

    public Map<String, String> analysisEventLog(JoinPoint joinPoint, Object returnValue, Exception ex,
                                                Object annotationObject, Class annotationClass) throws Exception {
        Map<String, String> result = new HashMap<>();
        Object target = joinPoint.getTarget();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        Method currentMethod = target.getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());

        AnnotatedElementKey methodKey = new AnnotatedElementKey(currentMethod, joinPoint.getTarget().getClass());

        List<Method> methods = Arrays.asList(annotationClass.getMethods());
        ParameterNameDiscoverer discoverer = new StandardReflectionParameterNameDiscoverer();
        ExpressionRootObject root = new ExpressionRootObject(joinPoint.getTarget(), joinPoint.getArgs());
        LogRecordEvaluationContext logRecordEvaluationContext = new LogRecordEvaluationContext
                (root, methodSignature.getMethod(), joinPoint.getArgs(), discoverer, returnValue, "");

        if (!CollectionUtils.isEmpty(methods)) {
            for (Method method : methods) {
                String name = method.getName();
                if (Constant.blockerMethod.contains(name)) {
                    continue;
                }
                Method getKeyById = annotationClass.getDeclaredMethod(name);

                Object model = getKeyById.invoke(annotationObject);
                Map<String, String> spelMap = new HashMap<>();
                String modelString = analysisEventLogValue(model.toString(), spelMap);
                if (MapUtils.isNotEmpty(spelMap)) {
                    for (String key : spelMap.keySet()) {
                        String value = logRecordExpressionEvaluator.parseExpression(spelMap.get(key), methodKey, logRecordEvaluationContext);
                        modelString = modelString.replace(key, value);
                    }
                }
                result.put(name, modelString);

            }
        }
        return result;
    }

    public static int getCharacterPosition(String url, String s, int i) {
        Matcher slashMatcher = Pattern.compile(s).matcher(url);
        int mIdx = 0;
        while (slashMatcher.find()) {
            mIdx++;
            //当"Z"符号第i次出现的位置
            if (mIdx == i) {
                break;
            }
        }
        return slashMatcher.start();
    }

    public String analysisEventLogValue(String model, Map<String, String> spelMap) {
        StringBuilder modelString = new StringBuilder(model);
        if (model.contains("{")) {
            Integer start = getCharacterPosition(model, "\\{", 1);
            if (start != null && model.contains("}")) {
                Integer end = getCharacterPosition(model, "\\}", 1);
                if (end != null) {
                    String key = IdWorker.getuniqueIndex();
                    String spel = model.substring(start, , end + 1);
                    spelMap.put(key, spel);
                    modelString.replace(start, end + 1, key);
                    this.analysisEventLogValue(modelString.toString(), spelMap);
                }
            }
        }
        return modelString.toString();
    }


}