package com.sap.cloud.cf.monitoring.spring;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnvUtils {

    public static void setEnvs(String[][] envs) throws Exception {
        Map<String, String> envMap = Stream.of(envs).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        setNewEnvs(envMap);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void setNewEnvs(Map<String, String> newenv) throws Exception {
        Class[] classes = Collections.class.getDeclaredClasses();
        Map<String, String> env = System.getenv();
        for (Class cl : classes) {
            if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                Field field = cl.getDeclaredField("m");
                field.setAccessible(true);
                Object obj = field.get(env);
                Map<String, String> map = (Map<String, String>) obj;
                map.clear();
                map.putAll(newenv);
            }
        }
    }

}
