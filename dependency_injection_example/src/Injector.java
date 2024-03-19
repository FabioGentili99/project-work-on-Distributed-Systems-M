import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Injector {

    private final Map<Class<?>, Object> instances;

    public Injector() {
        instances = new HashMap<>();
    }

    public void register(Class<?> type, Object instance) {
        instances.put(type, instance);
    }

    public Object inject(Class<?> targetClass) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        /*
            scan, wire and init
         */

        if (targetClass.isAnnotationPresent(Injectable.class)) {
            Constructor<?>[] constructors = targetClass.getDeclaredConstructors();
            for (Constructor<?> constructor : constructors) {
                if (constructor.isAnnotationPresent(Inject.class)) {
                    Class<?>[] parameterTypes = constructor.getParameterTypes();
                    List<Object> parameters = new ArrayList<>();
                    int i = parameterTypes.length;
                    for (Class<?> parameterType: parameterTypes) {
                        Object dependencyValue = parameterType.cast(instances.get(parameterType));
                        if (dependencyValue != null) {
                            parameters.add(parameterType.cast(dependencyValue));
                        } else {
                            throw new IllegalStateException("No instance registered for type: " + parameterType );
                        }
                    }
                    switch (i) {
                        case 1:
                            return constructor.newInstance(parameters.get(0));
                        //case 2: return constructor.newInstance(parameters.get(0), parameters.get(1));
                        default:
                            return new Client();
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Target class is not injectable: " + targetClass);
        }
        return null;
    }
}



    /*
    * field injection

    public void inject(Object target) throws IllegalAccessException {
        Class<?> targetClass = target.getClass();
        if (targetClass.isAnnotationPresent(Injectable.class)) {
            Field[] fields = targetClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Inject.class)) {
                    Class<?> fieldType = field.getType();
                    Object fieldValue = instances.get(fieldType);
                    if (fieldValue != null) {
                        field.setAccessible(true);
                        field.set(target, fieldValue);
                    } else {
                        throw new IllegalStateException("No instance registered for type: " + fieldType);
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Target class is not injectable: " + targetClass);
        }
    }*/

