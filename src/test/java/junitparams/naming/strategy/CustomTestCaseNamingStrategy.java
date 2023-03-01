package junitparams.naming.strategy;

import junitparams.internal.Utils;
import junitparams.naming.TestCaseName;
import junitparams.naming.TestCaseNamingStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class CustomTestCaseNamingStrategy implements TestCaseNamingStrategy {

    private static final String MACRO_START = "{";
    private static final String MACRO_END = "}";
    static final String DEFAULT_TEMPLATE = "[{ID}]";

    private String[] templateParts;
    private String methodName;

    public CustomTestCaseNamingStrategy(TestCaseName testCaseName, String methodName) {
        this.templateParts = split(getTemplate(testCaseName));
        this.methodName = methodName;
    }

    public static String getTemplate(TestCaseName testCaseName) {
        if (testCaseName != null) {
            return testCaseName.value();
        }

        return DEFAULT_TEMPLATE;
    }

    @Override
    public String getTestCaseName(int parametersIndex, Object parameters) {
        return buildNameByTemplate(templateParts, parametersIndex, parameters);
    }

    private String buildNameByTemplate(String[] parts, int parametersIndex, Object parameters) {
        StringBuilder nameBuilder = new StringBuilder();

        for (String part : parts) {
            String transformedPart = transformPart(part, parametersIndex, parameters);
            nameBuilder.append(transformedPart);
        }

        return nameBuilder.toString();
    }

    private String transformPart(String part, int parametersIndex, Object parameters) {
        if (isMacro(part)) {
            return lookupMacroValue(part, parametersIndex, parameters);
        }

        return part;
    }

    private String lookupMacroValue(String macro, int parametersIndex, Object parameters) { //parameters of same object type
        String macroKey = getMacroKey(macro);

        switch (Macro.parse(macroKey)) {
            case INDEX: return String.valueOf(parametersIndex);
            case PARAMS: return Utils.stringify(parameters);
            case METHOD: return methodName;
            case ID: return ((Person[])parameters)[parametersIndex].getId();
            case FULLNAME: return ((Person[])parameters)[parametersIndex].getFullName();
            case AGE: return String.valueOf(((Person[])parameters)[parametersIndex].getAge());
            default: return substituteDynamicMacro(macro, macroKey, parameters);
        }
    }

    private String substituteDynamicMacro(String macro, String macroKey, Object parameters) {
        if (isMethodParameterIndex(macroKey)) {
            int index = parseIndex(macroKey);
            return Utils.getParameterStringByIndexOrEmpty(parameters, index);
        }

        return macro;
    }

    private boolean isMethodParameterIndex(String macroKey) {
        return macroKey.matches("\\d+");
    }

    private int parseIndex(String macroKey) {
        return Integer.parseInt(macroKey);
    }

    private String getMacroKey(String macro) {
        return macro
                .substring(MACRO_START.length(), macro.length() - MACRO_END.length())
                .toUpperCase(Locale.ENGLISH);
    }

    private boolean isMacro(String part) {
        return part.startsWith(MACRO_START) && part.endsWith(MACRO_END);
    }

    private enum Macro {
        INDEX,
        PARAMS,
        METHOD,
        ID,
        FULLNAME,
        AGE,
        NONE;

        public static Macro parse(String value) {
            if (macros.contains(value)) {
                return Macro.valueOf(value);
            } else {
                return Macro.NONE;
            }
        }

        private static final HashSet<String> macros = new HashSet<String>(Arrays.asList(
                Macro.INDEX.toString(), Macro.PARAMS.toString(),
                Macro.METHOD.toString(), Macro.ID.toString(),
                Macro.FULLNAME.toString(), Macro.AGE.toString())
        );
    }

    private static String[] split(String input) {
        char macroStart = MACRO_START.charAt(0);
        char macroEnd = MACRO_END.charAt(0);

        int startIndex = 0;
        boolean inMacro = false;

        List<String> list = new ArrayList<String>();

        for (int endIndex = 0; endIndex < input.length(); endIndex++) {
            char chr = input.charAt(endIndex);

            if (!inMacro) {
                if (chr == macroStart) {
                    String result = input.substring(startIndex, endIndex);
                    if (result.length() > 0) {
                        list.add(result);
                    }
                    inMacro = true;
                    startIndex = endIndex;
                }
            } else {
                if (chr == macroEnd) {
                    String result = input.substring(startIndex, endIndex + 1);
                    if (result.length() > 0) {
                        list.add(result);
                    }
                    inMacro = false;
                    startIndex = endIndex + 1;
                }
            }
        }

        String result = input.substring(startIndex);
        if (result.length() > 0) {
            list.add(result);
        }

        return list.toArray(new String[list.size()]);
    }
}
