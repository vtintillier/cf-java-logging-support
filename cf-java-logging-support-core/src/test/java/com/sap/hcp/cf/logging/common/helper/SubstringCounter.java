package com.sap.hcp.cf.logging.common.helper;

public class SubstringCounter {

    public int countOccurrencesOfSubstringInBigString(String substring, String bigString) {
        return bigString.split(substring).length - 1;
    }

}
