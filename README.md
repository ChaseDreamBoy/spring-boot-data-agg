<h1>Spring Boot 并发数据聚合 demo</h1>

<h1>一、参考链接</h1>

<a href="https://github.com/lvyahui8/spring-boot-data-aggregator"> https://github.com/lvyahui8/spring-boot-data-aggregator </a>


<h1>二、需要的配置</h1>

在 pom.xml 中加入依赖：
```xml
<dependency>
  <groupId>io.github.lvyahui8</groupId>
  <artifactId>spring-boot-data-aggregator-starter</artifactId>
  <version>{$LATEST_VERSION}</version>
</dependency>
```

application.properties 中加入：
<pre><code>
# 指定要扫描注解的包
io.github.lvyahui8.spring.base-packages=com.xh
</code></pre>

<h1>三、注解含义</h1>
@DataProvider 表示这个方法是一个数据提供者, 数据Id为 注解中的值
@DataConsumer 表示这个方法的参数, 需要消费数据, 数据Id分别为 注解中的值
@InvokeParameter 表示方法执行时, 需要手动传入的参数

@InvokeParameter 和 @DataConsumer的区别, 前者需要用户在最上层调用时手动传参; 而后者, 是由框架自动分析依赖, 并异步调用取得结果之后注入的
 
<h1>四、使用示例</h1>

<pre><code>
com.xh.service.impl.UserServiceImpl#getUserByParallel();
</code></pre>

<ul>
<li>1、定义一个聚合方法，com.xh.service.impl.UserServiceImpl#userAggregate();</li>
<li>2、注意该方法中有用到注解 @DataProvider("userAggregate")、@DataConsumer("userBaseInfo") 等</li>
<li>3、在调用用数据提供者时， 调用的id 要与 数据提供者的id一致</li>
<li>4、调用聚合方法：Spring Bean DataBeanAggregateQueryFacade，例如： com.xh.service.impl.UserServiceImpl#getUserByParallel();</li>
<li>注意：该聚合方法需要注入到spring的bean中，这些用到的注解和聚合方法需要被上面的注解扫描扫到。</li>
</ul>

如：
定义的聚合方法
<pre><code>
    @DataProvider("userAggregate")
    public User userAggregate(@DataConsumer("userBaseInfo") User user,
                              @DataConsumer("purchasedGoods") PurchasedGoods purchasedGoods,
                              @DataConsumer("orderedGoods") OrderedGoods orderedGoods) {
        user.setPurchasedGoods(purchasedGoods);
        user.setOrderedGoods(orderedGoods);
        return user;
    }
</code></pre>
调用聚合方法：
<pre><code>
    public User getUserByParallel(Long userId) throws InterruptedException, IllegalAccessException, InvocationTargetException {
        long startTime = System.currentTimeMillis();

        User user = dataBeanAggregateQueryFacade.get("userAggregate", Collections.singletonMap("userId", userId), User.class);

        long consumerTime = System.currentTimeMillis() - startTime;
        log.info("parallel consumer time : [{}]", consumerTime);
        return user;
    }
</code></pre>
上面的 @DataProvider("userAggregate") 与 下面的 dataBeanAggregateQueryFacade.get("userAggregate", ...) 的 id 是一致的。 


又例如：
数据提供者：
<pre><code>
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
</code></pre>
在数据调用的时候：
<pre><code>
    @DataProvider("userAggregate")
    public User userAggregate(@DataConsumer("userBaseInfo") User user,
                              @DataConsumer("purchasedGoods") PurchasedGoods purchasedGoods,
                              @DataConsumer("orderedGoods") OrderedGoods orderedGoods) {
        user.setPurchasedGoods(purchasedGoods);
        user.setOrderedGoods(orderedGoods);
        return user;
    }
</code></pre>
上面的注解中  @DataProvider("orderedGoods") 与 @DataConsumer("orderedGoods")  的 id 是对应的

<h1>五、测试</h1>

<pre><code>
com.xh.service.impl.UserServiceTest#testAgg();
</code></pre>

测试输出：
<pre><code>
2019-08-04 13:50:17.903  INFO 10680 --- [           main] com.xh.service.impl.UserServiceImpl      : get base user ...
2019-08-04 13:50:18.904  INFO 10680 --- [           main] c.x.s.impl.PurchasedGoodsServiceImpl     : get purchased goods ...
2019-08-04 13:50:19.905  INFO 10680 --- [           main] c.x.s.impl.OrderedGoodsServiceImpl       : get ordered goods ...
2019-08-04 13:50:20.905  INFO 10680 --- [           main] com.xh.service.impl.UserServiceImpl      : serial consumer time : [3002]
2019-08-04 13:50:20.908  INFO 10680 --- [pool-1-thread-1] com.xh.service.impl.UserServiceImpl      : get base user ...
2019-08-04 13:50:20.908  INFO 10680 --- [pool-1-thread-2] c.x.s.impl.PurchasedGoodsServiceImpl     : get purchased goods ...
2019-08-04 13:50:20.908  INFO 10680 --- [pool-1-thread-3] c.x.s.impl.OrderedGoodsServiceImpl       : get ordered goods ...
2019-08-04 13:50:21.909  INFO 10680 --- [           main] com.xh.service.impl.UserServiceImpl      : Asynchronous threads consumer time : [1003]
2019-08-04 13:50:21.910  INFO 10680 --- [aggregateTask-3] c.x.s.impl.OrderedGoodsServiceImpl       : get ordered goods ...
2019-08-04 13:50:21.910  INFO 10680 --- [aggregateTask-1] com.xh.service.impl.UserServiceImpl      : get base user ...
2019-08-04 13:50:21.911  INFO 10680 --- [aggregateTask-2] c.x.s.impl.PurchasedGoodsServiceImpl     : get purchased goods ...
2019-08-04 13:50:22.911  INFO 10680 --- [           main] com.xh.service.impl.UserServiceImpl      : parallel consumer time : [1002]
</code></pre>

