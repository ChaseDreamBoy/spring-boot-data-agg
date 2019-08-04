package com.xh.service.impl;

import com.xh.entity.OrderedGoods;
import com.xh.entity.PurchasedGoods;
import com.xh.entity.User;
import com.xh.service.IOrderedGoodsService;
import com.xh.service.IPurchasedGoodsService;
import com.xh.service.IUserService;
import io.github.lvyahui8.spring.aggregate.facade.DataBeanAggregateQueryFacade;
import io.github.lvyahui8.spring.annotation.DataConsumer;
import io.github.lvyahui8.spring.annotation.DataProvider;
import io.github.lvyahui8.spring.annotation.InvokeParameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.concurrent.*;

/**
 * @author xiaohe
 * @version V1.0.0
 */
@Slf4j
@Service("userServiceImpl")
public class UserServiceImpl implements IUserService {

    private IOrderedGoodsService orderedGoodsServiceImpl;

    private IPurchasedGoodsService purchasedGoodsServiceImpl;

    private DataBeanAggregateQueryFacade dataBeanAggregateQueryFacade;

    public UserServiceImpl(IOrderedGoodsService orderedGoodsServiceImpl,
                           IPurchasedGoodsService purchasedGoodsServiceImpl,
                           DataBeanAggregateQueryFacade dataBeanAggregateQueryFacade) {
        this.orderedGoodsServiceImpl = orderedGoodsServiceImpl;
        this.purchasedGoodsServiceImpl = purchasedGoodsServiceImpl;
        this.dataBeanAggregateQueryFacade = dataBeanAggregateQueryFacade;
    }

    /**
     * 获取 用户的基本信息
     * <p>
     * 注解 @DataProvider 表示这个方法是一个数据提供者, 数据Id为 userBaseInfo
     * 注解 @InvokeParameter 表示方法执行时, 需要手动传入的参数，该注解需要用户在最上层调用时手动传参
     * </p>
     *
     * @param userId user id.
     *
     * @return 用户基本信息
     */
    @DataProvider("userBaseInfo")
    @Override
    public User getUserBaseInfo(@InvokeParameter("userId") Long userId) {
        try {
            // 假设这一步耗时 1s
            log.info("get base user ...");
            Thread.sleep(1000L);
            return User.builder().userId(userId).build();
        } catch (InterruptedException e) {
            log.error("get base user has error.", e);
            return null;
        }
    }

    /**
     * 通过串行的方式获取用户详情
     * <p>
     * 先获取用户基本信息<br />
     * 在获取用户已购商品<br />
     * 然后再 获取用户已下单的商品
     * </p>
     *
     * @param userId user id.
     *
     * @return 用户详情
     */
    @Override
    public User getUserBySerial(Long userId) {
        long startTime = System.currentTimeMillis();
        User user = getUserBaseInfo(userId);

        // 获取用户已购商品
        PurchasedGoods purchasedGoods = purchasedGoodsServiceImpl.getPurchasedGoods(userId);

        // 获取用户已下单的商品
        OrderedGoods orderedGoods = orderedGoodsServiceImpl.getOrderedGoods(userId);

        user.setPurchasedGoods(purchasedGoods);
        user.setOrderedGoods(orderedGoods);
        long consumerTime = System.currentTimeMillis() - startTime;
        log.info("serial consumer time : [{}]", consumerTime);
        return user;
    }

    /**
     * 通过 移步线程 的方式获取用户详情
     *
     * @param userId user id.
     *
     * @return 用户详情
     */
    @Override
    public User getUserByAsyncThread(Long userId) throws InterruptedException, ExecutionException {
        long startTime = System.currentTimeMillis();

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CountDownLatch countDownLatch = new CountDownLatch(3);
        // 获取 用户的基本信息
        Future<User> userFuture = executorService.submit(() -> {
            try {
                return getUserBaseInfo(userId);
            } finally {
                countDownLatch.countDown();
            }
        });

        // 获取用户已购商品
        Future<PurchasedGoods> purchasedGoodsFuture = executorService.submit(() -> {
            try {
                return purchasedGoodsServiceImpl.getPurchasedGoods(userId);
            } finally {
                countDownLatch.countDown();
            }
        });

        // 获取用户已下单的商品
        Future<OrderedGoods> orderedGoodsFuture = executorService.submit(() -> {
            try {
                return orderedGoodsServiceImpl.getOrderedGoods(userId);
            } finally {
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();

        User user = userFuture.get();
        user.setPurchasedGoods(purchasedGoodsFuture.get());
        user.setOrderedGoods(orderedGoodsFuture.get());

        long consumerTime = System.currentTimeMillis() - startTime;
        log.info("Asynchronous threads consumer time : [{}]", consumerTime);

        return user;
    }

    /**
     * 通过并行的方式获取用户详情
     * <p>
     * 同时获取 用户基本信息 、 用户已购商品 、 用户已下单的商品
     * </p>
     *
     * @param userId user id.
     *
     * @return 用户详情
     */
    @Override
    public User getUserByParallel(Long userId) throws InterruptedException, IllegalAccessException, InvocationTargetException {
        long startTime = System.currentTimeMillis();

        User user = dataBeanAggregateQueryFacade.get("userAggregate", Collections.singletonMap("userId", userId), User.class);

        long consumerTime = System.currentTimeMillis() - startTime;
        log.info("parallel consumer time : [{}]", consumerTime);
        return user;
    }

    /**
     * <p>
     * 注解 @DataProvider 表示这个方法是一个数据提供者, 数据Id为 userAggregate
     * 注解 @DataConsumer 表示这个方法的参数, 需要消费数据, 数据Id分别为 userBaseInfo ,purchasedGoods, orderedGoods.
     * </p>
     *
     * @param user           用户实体
     * @param purchasedGoods 用户已购商品实体
     * @param orderedGoods   用户已下单的商品实体
     *
     * @return 用户详情
     */
    @DataProvider("userAggregate")
    public User userAggregate(@DataConsumer("userBaseInfo") User user,
                              @DataConsumer("purchasedGoods") PurchasedGoods purchasedGoods,
                              @DataConsumer("orderedGoods") OrderedGoods orderedGoods) {
        user.setPurchasedGoods(purchasedGoods);
        user.setOrderedGoods(orderedGoods);
        return user;
    }

}
