package com.xh.entity;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author xiaohe
 * @version V1.0.0
 */
@Data
@ToString
@Builder
public class User {

    private Long userId;

    private String userName;

    private PurchasedGoods purchasedGoods;

    private OrderedGoods orderedGoods;

}
