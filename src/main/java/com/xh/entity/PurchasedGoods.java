package com.xh.entity;

import lombok.Builder;
import lombok.Data;

/**
 * 用户已购商品实体
 *
 * @author xiaohe
 * @version V1.0.0
 */
@Data
@Builder
public class PurchasedGoods {

    private Long id;

    private Long userId;

    private String name;

}
