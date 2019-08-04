package com.xh.service;

import com.xh.entity.OrderedGoods;

/**
 * @author xiaohe
 * @version V1.0.0
 */
public interface IOrderedGoodsService {

    /**
     * 获取户已下单的商品实体
     *
     * @param userId user id.
     *
     * @return 户已下单的商品实体
     */
    OrderedGoods getOrderedGoods(Long userId);

}
