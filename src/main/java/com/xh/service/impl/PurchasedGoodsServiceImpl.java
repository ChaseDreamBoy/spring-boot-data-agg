package com.xh.service.impl;

import com.xh.entity.PurchasedGoods;
import com.xh.service.IPurchasedGoodsService;
import io.github.lvyahui8.spring.annotation.DataProvider;
import io.github.lvyahui8.spring.annotation.InvokeParameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author xiaohe
 * @version V1.0.0
 */
@Slf4j
@Service("purchasedGoodsServiceImpl")
public class PurchasedGoodsServiceImpl implements IPurchasedGoodsService {

    /**
     * 获取用户已购商品实体
     *
     * @param userId user id.
     *
     * @return 用户已购商品实体
     */
    @DataProvider("purchasedGoods")
    @Override
    public PurchasedGoods getPurchasedGoods(@InvokeParameter("userId") Long userId) {
        try {
            // 假设这一步耗时 1s
            log.info("get purchased goods ...");
            Thread.sleep(1000L);
            return PurchasedGoods.builder().userId(userId).name("PurchasedGoods").build();
        } catch (InterruptedException e) {
            log.error("get purchased goods has error.", e);
            return null;
        }
    }

}
