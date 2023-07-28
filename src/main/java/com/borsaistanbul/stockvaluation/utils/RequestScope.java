package com.borsaistanbul.stockvaluation.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.NamedThreadLocal;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestScope {

    private static final String ERROR_CODE = "ERROR_CODE";
    private static final String ERROR_MESSAGE = "ERROR_MESSAGE";

    private static final ThreadLocal<Map<String, String>> REQ_ATTRIBUTE_HOLDER =
            new NamedThreadLocal<>("Request Attributes");

    public static void add(String key, String value) {
        final Optional<Map<String, String>> scopeAttr = getAll();
        final Map<String, String> attrMap = scopeAttr.orElse(new HashMap<>());
        attrMap.put(key, value);
        REQ_ATTRIBUTE_HOLDER.set(attrMap);
    }

    private static Optional<Map<String, String>> getAll() {
        return Optional.ofNullable(REQ_ATTRIBUTE_HOLDER.get());
    }

    public static void setErrorCode(String errorCode) {
        add(ERROR_CODE, errorCode);
    }

    public static void setErrorMessage(String errorMessage) {
        add(ERROR_MESSAGE, errorMessage);
    }

    public static void unload() {
        REQ_ATTRIBUTE_HOLDER.remove();
    }

}
