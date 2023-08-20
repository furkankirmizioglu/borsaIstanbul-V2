package com.borsaistanbul.stockvaluation.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UtilsTest {
    @Test
    void isNullOrEmptyNullCase() {
        Assertions.assertTrue(Utils.isNullOrEmpty(null));
    }
    @Test
    void isNullOrEmptyEmptyCase() {
        Assertions.assertTrue(Utils.isNullOrEmpty(""));
    }
    @Test
    void isNullOrEmptyStringCase() {
        Assertions.assertFalse(Utils.isNullOrEmpty("TEST"));
    }

    @Test
    void getCurrentDateTimeAsLong() {
        double result = Utils.getCurrentDateTimeAsLong();
        Assertions.assertTrue(result > 1);
    }

}
