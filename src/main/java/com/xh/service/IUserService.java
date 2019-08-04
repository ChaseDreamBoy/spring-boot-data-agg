package com.xh.service;

import com.xh.entity.User;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

/**
 * @author xiaohe
 * @version V1.0.0
 */
public interface IUserService {

    /**
     * 获取 用户的基本信息
     *
     * @param userId user id.
     *
     * @return 用户基本信息
     */
    User getUserBaseInfo(Long userId);

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
    User getUserBySerial(Long userId);

    /**
     * 通过 移步线程 的方式获取用户详情
     *
     * @param userId user id.
     *
     * @return 用户详情
     */
    User getUserByAsyncThread(Long userId) throws InterruptedException, ExecutionException;

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
    User getUserByParallel(Long userId) throws InterruptedException, IllegalAccessException, InvocationTargetException;

}
