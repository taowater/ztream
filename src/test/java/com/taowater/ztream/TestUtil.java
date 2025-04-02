package com.taowater.ztream;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class TestUtil {
    public static <T> List<T> initList(T... values) {
        if (values == null || values.length == 0) {
            return new ArrayList<>();
        }
        List<T> list = new ArrayList<>();
        for (T value : values) {
            list.add(value);
        }
        return list;
    }

}
