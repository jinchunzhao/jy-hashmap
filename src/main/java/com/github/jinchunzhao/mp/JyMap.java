package com.github.jinchunzhao.mp;

/**
 * map接口
 *
 * @author JinChunZhao
 * @version 1.0
 * @date 2020-10-11 19:45
 */
public interface JyMap<K, V> {

    /**
     * @return the number of key-value mappings in this map
     */
    public int size();

    /**
     * 取值
     *
     * @param k 键
     * @return 值
     */
    public V get(K k);

    /**
     * 添加值
     *
     * @param k 键
     * @param v 值
     * @return 值
     */
    public void put(K k, V v);

    interface Node<K, V> {

        K getKey();

        V getValue();

        V setValue(V value);

    }
}
