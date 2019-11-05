package cn.shaines.blog.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author houyu
 * @createTime 2019/7/10 21:14
 */
public class ReflexUtil {

    /**
     * 方法执行者
     * @param <R>
     */
    public static class InvokeExecutor<R> {

        private Class<?> sourceType;        // 执行方法的实体类型
        private Object sourceObject;        // 执行方法的实体类型
        private String method;              // 方法名
        private Class<?>[] parameterTypes;  // 参数类型Class
        private Object[] parameters;        // 参数

        private InvokeExecutor<R> setSourceType(Class<?> sourceType) {
            this.sourceType = sourceType;
            return this;
        }
        private InvokeExecutor<R> setSourceObject(Object sourceObject) {
            this.sourceObject = sourceObject;
            return this;
        }
        public InvokeExecutor<R> setMethod(String method) {
            this.method = method;
            return this;
        }
        public InvokeExecutor<R> setParameterTypes(Class<?>... parameterTypes) {
            this.parameterTypes = parameterTypes;
            return this;
        }
        public InvokeExecutor<R> setParameters(Object... parameters) {
            this.parameters = parameters;
            return this;
        }
        public InvokeExecutor<R> setExecutor(Object executor) {
            return executor instanceof Class ? this.setSourceType((Class<?>) executor) : this.setSourceObject(executor);
        }


        public <T> T invoke(Class<T> targetType) {
            return (T) invoke();
        }

        public R invoke(){
            try {
                return sourceObject != null ? methodInvoke(sourceObject, method, parameterTypes, parameters) : methodInvoke(sourceType, method, parameterTypes, parameters);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 反射执行方法:
         *      例如:Integer valueOf = SimpleFactory.methodInvoke(Integer.class, "valueOf", Arrays.asList(String.class), Arrays.asList("2"), Integer.class);
         */
        private <T> T methodInvoke(Class<?> clazz, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            if (parameterTypes == null){
                return (T) clazz.getMethod(methodName).invoke(clazz);
            }else {
                Method method = clazz.getMethod(methodName, parameterTypes);
                method.setAccessible(true);
                return (T) method.invoke(clazz, parameters);
            }
        }

        /**
         * 反射执行方法:
         *      Void put = ReflexUtil.methodInvoke(fileObj, "setContentType", Arrays.asList(String.class), Arrays.asList("1"), void.class);
         *      Void put = ReflexUtil.methodInvoke(fileObj, "setContentType", Arrays.asList(String.class), Arrays.asList("3"), null);
         */
        private <T> T methodInvoke(Object o, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            return (T) ((parameterTypes == null) ? o.getClass().getMethod(methodName).invoke(o) : o.getClass().getMethod(methodName, parameterTypes).invoke(o, parameters));
        }
    }

    /**
     * 创建方法执行者
     * @param resultType<R> 结果类型 可选参数
     * @return
     */
    public static <R> InvokeExecutor<R> buildInvoke(Class<R>... resultType) {
        return new InvokeExecutor<>();
    }


    private static ConcurrentHashMap<String, Object> beanMap = new ConcurrentHashMap();

    /**
     * 获取对象
     */
    public static <T extends Object> T getBean(Class<T> clazz){
        String typeName = clazz.getTypeName();
        return beanMap.containsKey(typeName) ? (T) beanMap.get(typeName) : newBean(clazz);
    }

    /**
     * 创建对象
     */
    public static <T extends Object> T newBean(Class<T> clazz){
        try{
            // String typeName = clazz.getTypeName();
            // (T) Class.forName(typeName).newInstance();
            Constructor<T> declaredConstructor = clazz.getDeclaredConstructor();  // 获取构造方法
            declaredConstructor.setAccessible(true);                              // 设置私有可访问
            T t = declaredConstructor.newInstance();                              // 新建实例对象
            beanMap.put(clazz.getTypeName(), t);
            return t;
        }catch (Exception e) {
            // throw new RuntimeException(e);
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 类型转换
     */
    public static <T> T cast(Object o, Class<T> clazz){
        // return clazz.cast(o);             // 这个是直接转,不符合的话直接报错
        // if (o instanceof String){}        // 这个是判断类型
        // clazz.getTypeName().contains("String")
        // clazz.equals(String.class)
        return clazz.isInstance(o) ? (T) o : null;
    }


}
