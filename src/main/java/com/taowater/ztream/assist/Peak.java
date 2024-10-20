package com.taowater.ztream.assist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 记录最值的实体
 *
 * @author zhu56
 * @date 2024/10/21 01:37
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Peak<T> {

    private T max;

    private T min;
}
