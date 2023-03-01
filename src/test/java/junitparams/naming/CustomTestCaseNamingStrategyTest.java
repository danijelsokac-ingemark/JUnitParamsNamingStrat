package junitparams.naming;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.internal.TestMethod;
import junitparams.naming.strategy.CustomTestCaseNamingStrategy;
import junitparams.naming.strategy.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

@RunWith(JUnitParamsRunner.class)
public class CustomTestCaseNamingStrategyTest {

    public Object parametersForTestNaming() {
        return new Object[]{new Object[]{"withoutTestCaseAnnotation", "[ID-1]"},
                new Object[]{"withAnnotationForId", "[ID-1]"},
                new Object[]{"withAnnotationForIdAndFullName", "Person One [ID-1]"},
                new Object[]{"withAnnotationForIdAgeAndFullName", "Person One (1) [ID-1]"},
                new Object[]{"withoutMacro", "plain name"}, new Object[]{"withIndexMacro", "0"},
                new Object[]{"whenGivenMacroDoesntExist", "{not_existing_macro}"}};
    }

    private static final Person[] people = {
            new Person("ID-1", "Person One", 1),
            new Person("ID-2", "Person Two", 2),
            new Person("ID-3", "Person Three", 3)
    };

    @Test
    @Parameters(method = "parametersForTestNaming")
    public void testNaming(String methodName, String expectedTestCaseName) throws NoSuchMethodException {
        TestCaseNamingStrategy strategy = createNamingStrategyForMethod(methodName, Person.class);

        String name = strategy.getTestCaseName(0, people);

        assertEquals(expectedTestCaseName, name);
    }

    private TestCaseNamingStrategy createNamingStrategyForMethod(String name, Class... parameterTypes) throws NoSuchMethodException {
        TestMethod method = getCurrentClassMethod(name, parameterTypes);

        return NamingStrategyFactory.getStrategy(method.getAnnotation(TestCaseNameStrategy.class), method.getAnnotation(TestCaseName.class), method.name());
    }

    private TestMethod getCurrentClassMethod(String name, Class... parameterTypes) throws NoSuchMethodException {
        final Method method = CustomTestCaseNamingStrategyTest.class.getMethod(name, parameterTypes);
        return new TestMethod(new FrameworkMethod(method),
                new TestClass(this.getClass()));
    }

    @TestCaseNameStrategy(CustomTestCaseNamingStrategy.class)
    public void withoutTestCaseAnnotation(Person person) { }

    @TestCaseNameStrategy(CustomTestCaseNamingStrategy.class)
    @TestCaseName("plain name")
    public void withoutMacro(Person person) { }

    @TestCaseNameStrategy(CustomTestCaseNamingStrategy.class)
    @TestCaseName("{index}")
    public void withIndexMacro(Person person) { }

    @TestCaseNameStrategy(CustomTestCaseNamingStrategy.class)
    @TestCaseName("{not_existing_macro}")
    public void whenGivenMacroDoesntExist(Person person) { }

    @TestCaseNameStrategy(CustomTestCaseNamingStrategy.class)
    @TestCaseName("[{ID}]")
    public void withAnnotationForId(Person person) { }

    @TestCaseNameStrategy(CustomTestCaseNamingStrategy.class)
    @TestCaseName("{FULLNAME} [{ID}]")
    public void withAnnotationForIdAndFullName(Person person) { }

    @TestCaseNameStrategy(CustomTestCaseNamingStrategy.class)
    @TestCaseName("{FULLNAME} ({AGE}) [{ID}]")
    public void withAnnotationForIdAgeAndFullName(Person person) { }

}