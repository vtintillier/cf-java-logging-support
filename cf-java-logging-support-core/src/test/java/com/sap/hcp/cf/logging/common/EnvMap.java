package com.sap.hcp.cf.logging.common;

import java.util.HashMap;
import java.util.Map;

public class EnvMap {
    public static final String VCAP_APP_NAME = "test-app";
    public static final String VCAP_APP_ID = "867b99ba-1caa-4d91-b2ee-82982234f58a";
    public static final String VCAP_SPACE_NAME = "test-space";
    public static final String VCAP_SPACE_ID = "69ca5b0f-cc66-4f8e-9d17-f102f60142e2";
    public static final String VCAP_INSTANCE_IDX = "0";

    public static final String VCAP_APPLICATION = "{\"limits\":{\"mem\":256,\"disk\":1024,\"fds\":16384}, " +
                                                  "\"application_version\":\"a9efcfae-ee23-465f-9985-401099500825\"," +
                                                  "\"application_name\":\"" + VCAP_APP_NAME + "\"," +
                                                  "\"application_uris\":[\"test-app.acme.org\"],\"version\":\"a9efcfae-ee23-465f-9985-401099500825\",\"name\":\"test-app\"," +
                                                  "\"space_name\":\"" + VCAP_SPACE_NAME + "\"," + "\"space_id\":\"" +
                                                  VCAP_SPACE_ID + "\"," +
                                                  "\"uris\":[\"test-app.acme.org\"],\"users\":null," +
                                                  "\"application_id\":\"" + VCAP_APP_ID + "\"," +
                                                  "\"instance_id\":\"3b671ef5fd86407cb8807f303824c870\"," +
                                                  "\"instance_index\":\"" + VCAP_INSTANCE_IDX + "\"," +
                                                  "\"host\":\"0.0.0.0\",\"port\":61999,\"started_at\":\"2015-03-17 12:07:49 +0000\",\"started_at_timestamp\":1426594069,\"start\":\"2015-03-17 12:07:49 +0000\",\"state_timestamp\":1426594069}";

    public static final String NOT_SET = "not-set";

    public static Map<String, String> getMap() {
        Map<String, String> envMap = new HashMap<String, String>();
        envMap.put(VcapEnvReader.ENV_VCAP_APPLICATION, VCAP_APPLICATION);
        return envMap;
    }
}
