package cn.shaines.blog.utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;

/**
 * @description: 简单定时缓存
 * @author: houyu
 * @create: 2018-12-05 00:02
 *
 *
 * 使用:
 *      private SimpleTimeCache<String, JwtUser> jwtUserTimeCache = new SimpleTimeCache(100, 3600000 * 1);  // 阈值:100  有效时长:1小时
 */
public class SimpleTimeCache<K, V> {

    transient int      thresholdSize    = 500;                                             // 阈值      :500
    transient int      overTime         = 1000 * 10;                                       // 过时      :10秒(1000 * 10)
    transient boolean  isSync           = false;                                           // 是否同步  :不同步

    private final Lock lock             = new ReentrantLock();                             // 同步锁对象

    private          Map<K, Long>   timeHashMap         = new ConcurrentHashMap<>();      // 时间Map
    private          Map<K, V>      valueLinkedHashMap  = new LinkedHashMap<>();          // 存储Map
    private volatile Long           lastValueTime       = 0L;                             // 最后一个添加的时间

    public SimpleTimeCache() { }

    public SimpleTimeCache(int thresholdSize, int overTime){
        this.thresholdSize = thresholdSize;
        this.overTime = overTime;
    }

    /** 同步添加 */
    private void putBySync(Map<K, V> map, K key, V value) {
        try {
            lock.lock();
            map.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    /** 同步获取 */
    private V getBySync(Map<K, V> map, K key) {
        try {
            lock.lock();
            return map.get(key);
        } finally {
            lock.unlock();
        }
    }

    /** 检查并清除过时数据 */
    public void checkAndCleanStaleData() {
        if (System.currentTimeMillis() - lastValueTime > overTime) {                     // 最后一次添加的数据已经超时
            this.clear();
        }else if(timeHashMap.size() > thresholdSize){                                    // 容量达到阈值时,需要检查
            List<K> keyList = new ArrayList<>(valueLinkedHashMap.keySet());
            int size = keyList.size(); K tempKey;
            for (int i = 0; i < size; i++) {
                tempKey = keyList.get(i);
                if (System.currentTimeMillis() - timeHashMap.get(tempKey) > overTime){   // 超时数据为僵尸数据,满足删除的条件
                    this.remove(tempKey);
                }else{
                    break;
                }
            }
        }
    }

    /** 新增 */
    public void put(K key, V value) {
        lastValueTime = System.currentTimeMillis();
        timeHashMap.put(key, lastValueTime);
        if (isSync){
            this.putBySync(valueLinkedHashMap, key, value);
        }else{
            valueLinkedHashMap.put(key, value);
        }
        this.checkAndCleanStaleData();                                                    // 检查数据是否过期
    }

    /** 删除 */
    public V remove(K key) {
        timeHashMap.remove(key);
        return valueLinkedHashMap.remove(key);
    }

    /** 获取 */
    public V get(K key) {
        this.checkAndCleanStaleData();
        return (timeHashMap.get(key) == null || System.currentTimeMillis() - timeHashMap.get(key) > overTime) ? null : (isSync ? this.getBySync(valueLinkedHashMap, key) : valueLinkedHashMap.get(key));
    }

    /** 获取 */
    public V getOrDefault(K key, V defaultValue) {
        V v = this.get(key);
        return v == null ? defaultValue : v;
    }

    /** 判断是否存在key */
    public boolean containsKey(K key) {
        return this.get(key) != null;
    }

    /** 判断是否存在value */
    public boolean containsValue(V value) {
        return valueLinkedHashMap.containsValue(value);
    }

    public void clear() {
        timeHashMap.clear();
        valueLinkedHashMap.clear();
    }

    public Set<K> keySet() {
        return valueLinkedHashMap.keySet();
    }

    public Collection<V> values() {
        return valueLinkedHashMap.values();

    }

    public Set<Map.Entry<K, V>> entrySet() {
        return valueLinkedHashMap.entrySet();
    }

    public void forEach(BiConsumer<? super K, ? super V> action) {
        valueLinkedHashMap.forEach(action);
    }

    public int getThresholdSize() {
        return thresholdSize;
    }

    public void setThresholdSize(int thresholdSize) {
        this.thresholdSize = thresholdSize;
    }

    public int getOverTime() {
        return overTime;
    }

    public void setOverTime(int overTime) {
        this.overTime = overTime;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }

    /* ---------------------------------------单例模式---------------------------------------*/
    /** 使用单例的话, 那就是全局使用同一个缓存了 */
    private static class SingletonHolder {
        private static final SimpleTimeCache INSTANCE = new SimpleTimeCache();
    }
    public static SimpleTimeCache get(){
        return SingletonHolder.INSTANCE;
    }
    /* ---------------------------------------单例模式---------------------------------------*/


}
