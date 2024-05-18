import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public
class Injector {
    private Properties properties;

    public
    Injector() {
        properties = new Properties();
        try {
            properties.load(Injector.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public
    <T> T inject(T obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(AutoInjectable.class)) {
                Class<?> fieldType = field.getType();
                String implementationClassName = properties.getProperty(fieldType.getName());

                if (implementationClassName != null) {
                    Class<?> implementationClass = null;
                    try {
                        implementationClass = Class.forName(implementationClassName);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    Object implementationInstance = null;
                    try {
                        implementationInstance = implementationClass.getDeclaredConstructor().newInstance();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }

                    field.setAccessible(true);
                    try {
                        field.set(obj, implementationInstance);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return obj;
    }
}

