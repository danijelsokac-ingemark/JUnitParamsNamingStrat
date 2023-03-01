package junitparams.naming;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TestCaseNameStrategy {
    Class<? extends TestCaseNamingStrategy> value() default MacroSubstitutionNamingStrategy.class;
}
