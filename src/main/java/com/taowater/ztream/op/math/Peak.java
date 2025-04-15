package com.taowater.ztream.op.math;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 记录最值的实体
 *
 * @author zhu56
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Peak<T> {

    private T max;

    private T min;
}
