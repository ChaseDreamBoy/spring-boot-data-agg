package com.xh.service;

import com.xh.entity.PurchasedGoods;

/**
 * @author xiaohe
 * @version V1.0.0
 */
public interface IPurchasedGoodsService {

    /**
     * 获取用户已购商品实体
     *
     * @param userId user id.
     *
     * @return 用户已购商品实体
     */
    PurchasedGoods getPurchasedGoods(Long userId);

}
