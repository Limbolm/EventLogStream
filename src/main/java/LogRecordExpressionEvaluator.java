import java.util.concurrent.ConcurrentHashMap;

@Component
public class LogRecordExpressionEvaluator extends CachedExpressionEvaluator {
    private Map<ExpressionKey, Expression> expressionCache = new ConcurrentHashMap<>(64);
    private final Map<AnnotatedElementKey, Method> targetMethodCache = new ConcurrentHashMap<>(64);
    public String parseExpression(String conditionExpression, AnnotatedElementKey methodKey,
                                  EvaluationContext evalContext) {
        return getExpression(this.expressionCache, methodKey, conditionExpression).getValue(evalContext, String.class);
    }
}
