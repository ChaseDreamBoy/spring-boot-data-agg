package com.xh.entity;

import lombok.Builder;
import lombok.Data;

/**
 * 用户已下单的商品实体
 *
 * @author xiaohe
 * @version V1.0.0
 */
@Data
@Builder
public class OrderedGoods {

    private Long id;

    private Long userId;

    private String name;

}
