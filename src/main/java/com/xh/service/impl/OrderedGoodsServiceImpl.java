package com.xh.service.impl;

import com.xh.entity.OrderedGoods;
import com.xh.service.IOrderedGoodsService;
import io.github.lvyahui8.spring.annotation.DataProvider;
import io.github.lvyahui8.spring.annotation.InvokeParameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author xiaohe
 * @version V1.0.0
 */
@Slf4j
@Service("orderedGoodsServiceImpl")
public class OrderedGoodsServiceImpl implements IOrderedGoodsService {

    /**
     * 获取户已下单的商品实体
     *
     * @param userId user id.
     *
     * @return 户已下单的商品实体
     */
    @DataProvider("orderedGoods")
    @Override
    public OrderedGoods getOrderedGoods(@InvokeParameter("userId") Long userId) {
        try {
            // 假设这一步耗时 1s
            log.info("get ordered goods ...");
            Thread.sleep(1000L);
            return OrderedGoods.builder().userId(userId).name("OrderedGoods").build();
        } catch (InterruptedException e) {
            log.error("get ordered goods has error.", e);
            return null;
        }
    }

}
