package junitparams.naming;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class NamingStrategyFactory {

    private static final Class<? extends TestCaseNamingStrategy> DEFAULT_NAMING_STRATEGY = MacroSubstitutionNamingStrategy.class;

    private NamingStrategyFactory() {}

    public static TestCaseNamingStrategy getStrategy(TestCaseNameStrategy testCaseNameStrategy, TestCaseName testCaseName, String methodName) {
        try {
            Class<? extends  TestCaseNamingStrategy> namingStrategy = getNamingStrategy(testCaseNameStrategy);
            Constructor<? extends TestCaseNamingStrategy> constructor = namingStrategy.getConstructor(TestCaseName.class, String.class);
            return constructor.newInstance(testCaseName, methodName);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Class<? extends  TestCaseNamingStrategy> getNamingStrategy(TestCaseNameStrategy testCaseNameStrategy) {
        if(testCaseNameStrategy == null) {
            return DEFAULT_NAMING_STRATEGY;
        }
        return testCaseNameStrategy.value();
    }
}
