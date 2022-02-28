import java.util.HashMap;

public class LogRecordContext {
    private static final InheritableThreadLocal<Map<String,Map<String, Object>>> variableMapStack = new InheritableThreadLocal<>();

    public void putVariable(String key, Object object){
        getContextMap().put(key,object);
    }

    public static Object get(String key) {
        // getContextMap()表示要先获取THREAD_CONTEXT的value，也就是Map<String, Object>。
        // 然后再从Map<String, O
        return getContextMap().get(key);
    }

    public static Object remove(String key) {
        return getContextMap().remove(key);
    }

    public static void remove() {
        getContextMap().clear();
    }

    public static void clear() {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        variableMapStack.get().remove(methodName);
    }

    public static Map<String,Object> getVariables() {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Map<String, Map<String, Object>> mapMap = variableMapStack.get();
        if (mapMap == null) {
            return null;
        }
        if (mapMap.get(methodName) == null) {
            mapMap.put(methodName, new HashMap<>());
        }
        return mapMap.get(methodName);
    }

    private static Map<String, Object> getContextMap() {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        if (variableMapStack.get() == null) {
            Map<String, Map<String, Object>> map = new HashMap<>();
            variableMapStack.set(map);
        }
        if (variableMapStack.get().get(methodName) == null) {
            variableMapStack.get().put(methodName, new HashMap<>());
        }
        return variableMapStack.get().get(methodName);
    }

    private static class MapThreadLocal extends ThreadLocal<Map<String, Object>> {

        @Override
        protected Map<String, Object> initialValue() {
            return new HashMap<String, Object>(8) {

                private static final long serialVersionUID = 3637958959138295593L;

                @Override
                public Object put(String key, Object value) {
                    return super.put(key, value);
                }
            };
        }
    }

}
