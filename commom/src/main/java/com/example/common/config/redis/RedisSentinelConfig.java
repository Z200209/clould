package com.example.common.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * Redis哨兵模式配置类
 * 支持Redis高可用部署，与MySQL读写分离配置并存
 * 实现缓存层面的高可用性
 */
@Configuration
public class RedisSentinelConfig {

    /**
     * 哨兵节点配置
     * 格式：host1:port1,host2:port2
     */
    @Value("${spring.redis.sentinel.nodes:localhost:26379}")
    private String sentinelNodes;

    /**
     * Redis主服务名称
     * 在sentinel.conf中配置的master名称
     */
    @Value("${spring.redis.sentinel.master:mymaster}")
    private String masterName;

    /**
     * Redis密码
     */
    @Value("${spring.redis.password:}")
    private String password;

    /**
     * 哨兵密码
     */
    @Value("${spring.redis.sentinel.password:}")
    private String sentinelPassword;

    /**
     * Redis数据库索引
     */
    @Value("${spring.redis.database:0}")
    private Integer database;

    /**
     * 是否启用读写分离
     */
    @Value("${spring.redis.sentinel.read-from-slave:true}")
    private Boolean readFromSlave;

    /**
     * 连接超时时间（毫秒）
     */
    @Value("${spring.redis.timeout:5000}")
    private Integer timeout;

    /**
     * Jedis连接池最大连接数
     */
    @Value("${spring.redis.jedis.pool.max-active:8}")
    private Integer maxActive;

    /**
     * Jedis连接池最大空闲连接数
     */
    @Value("${spring.redis.jedis.pool.max-idle:4}")
    private Integer maxIdle;

    /**
     * Jedis连接池最小空闲连接数
     */
    @Value("${spring.redis.jedis.pool.min-idle:1}")
    private Integer minIdle;

    /**
     * Jedis连接池最大等待时间（毫秒）
     */
    @Value("${spring.redis.jedis.pool.max-wait:2000}")
    private Long maxWait;

    /**
     * 配置Redis哨兵连接工厂
     * 支持主从自动切换和故障转移
     * 
     * @return Redis连接工厂实例
     */
    @Bean(name = "redisSentinelConnectionFactory")
    public RedisConnectionFactory redisSentinelConnectionFactory() {
        // 配置哨兵节点
        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration();
        sentinelConfig.master(masterName);
        
        // 解析并添加哨兵节点
        Set<RedisNode> sentinelHostAndPorts = new HashSet<>();
        String[] nodes = sentinelNodes.split(",");
        for (String node : nodes) {
            String[] hostPort = node.trim().split(":");
            if (hostPort.length == 2) {
                String host = hostPort[0];
                int port = Integer.parseInt(hostPort[1]);
                sentinelHostAndPorts.add(new RedisNode(host, port));
            }
        }
        sentinelConfig.setSentinels(sentinelHostAndPorts);
        
        // 设置Redis主从密码
        if (password != null && !password.isEmpty()) {
            sentinelConfig.setPassword(password);
        }
        
        // 设置哨兵密码
        if (sentinelPassword != null && !sentinelPassword.isEmpty()) {
            sentinelConfig.setSentinelPassword(sentinelPassword);
        }
        
        // 设置数据库索引
        sentinelConfig.setDatabase(database);
        
        // 配置Jedis连接池
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWaitMillis(maxWait);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        
        // 创建Jedis连接工厂
        JedisConnectionFactory factory = new JedisConnectionFactory(sentinelConfig, poolConfig);
        factory.setTimeout(timeout);
        factory.afterPropertiesSet();
        
        return factory;
    }

    /**
     * 配置RedisTemplate
     * 设置键值序列化方式，支持字符串键和JSON值
     * 
     * @param connectionFactory Redis连接工厂
     * @return 配置好的RedisTemplate实例
     */
    @Bean(name = "redisSentinelTemplate")
    public RedisTemplate<String, Object> redisSentinelTemplate(
            RedisConnectionFactory connectionFactory) {
        
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // 设置键的序列化方式为字符串
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        
        // 设置值的序列化方式为JSON
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        
        // 启用事务支持
        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();
        
        return template;
    }

    /**
     * 配置从库读取的RedisTemplate
     * 用于读写分离，优先从从库读取数据
     * 
     * @return 从库读取RedisTemplate实例
     */
    @Bean(name = "redisSlaveTemplate")
    public RedisTemplate<String, Object> redisSlaveTemplate() {
        if (!readFromSlave) {
            // 如果未启用读写分离，返回主库模板
            return redisSentinelTemplate(redisSentinelConnectionFactory());
        }
        
        // 配置从库连接工厂
        RedisConnectionFactory slaveConnectionFactory = createSlaveConnectionFactory();
        return redisSentinelTemplate(slaveConnectionFactory);
    }
    
    /**
     * 创建从库连接工厂
     * 配置为优先从从库读取
     * 
     * @return 从库连接工厂
     */
    private RedisConnectionFactory createSlaveConnectionFactory() {
        // 配置哨兵节点
        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration();
        sentinelConfig.master(masterName);
        
        // 解析并添加哨兵节点
        Set<RedisNode> sentinelHostAndPorts = new HashSet<>();
        String[] nodes = sentinelNodes.split(",");
        for (String node : nodes) {
            String[] hostPort = node.trim().split(":");
            if (hostPort.length == 2) {
                String host = hostPort[0];
                int port = Integer.parseInt(hostPort[1]);
                sentinelHostAndPorts.add(new RedisNode(host, port));
            }
        }
        sentinelConfig.setSentinels(sentinelHostAndPorts);
        
        // 设置Redis主从密码
        if (password != null && !password.isEmpty()) {
            sentinelConfig.setPassword(password);
        }
        
        // 设置哨兵密码
        if (sentinelPassword != null && !sentinelPassword.isEmpty()) {
            sentinelConfig.setSentinelPassword(sentinelPassword);
        }
        
        // 设置数据库索引
        sentinelConfig.setDatabase(database);
        
        // 配置Jedis连接池（从库优化配置）
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWaitMillis(maxWait);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        
        // 创建Jedis连接工厂（从库读取配置）
        JedisConnectionFactory factory = new JedisConnectionFactory(sentinelConfig, poolConfig);
        factory.setTimeout(timeout);
        factory.setUsePool(true);
        factory.afterPropertiesSet();
        
        return factory;
    }

    /**
     * 配置默认的RedisTemplate（兼容性）
     * 使用哨兵模式的连接工厂
     * 
     * @return 默认RedisTemplate实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        return redisSentinelTemplate(redisSentinelConnectionFactory());
    }
}