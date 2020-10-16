package com.github.jinchunzhao;

import com.github.jinchunzhao.mp.JyMap;

import java.io.Serializable;
import java.util.Objects;

/**
 * $start$
 *
 * @author JinChunZhao
 * @version 1.0
 * @date 2020-10-15 9:42
 */
public class JyHashMap<K, V> implements JyMap<K, V>, Cloneable, Serializable {

    /**
     * table的默认初始化容量 16
     */
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;

    /**
     * table的最大容量
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * 负载因子：在构造函数中未指定时使用的负载系数。
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * table的长度
     */
    transient int size;

    /**
     * 阈值：当达到16*0.75时触发扩容
     */
    int threshold;

    /**
     * 哈希表的负载因子
     *
     * @serial
     */
    final float loadFactor;

    /**
     * 节点数组
     */
    private Node<K, V>[] table;

    public JyHashMap() {
        threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
        this.loadFactor = DEFAULT_LOAD_FACTOR;
    }

    public JyHashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        }

        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);
    }

    public JyHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public JyHashMap(JyHashMap<? extends K, ? extends V> m) {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        putMapEntries(m);
    }

    @Override public int size() {
        return size;
    }

    @Override public V get(K key) {
        Node<K, V> e;
        return (e = getNode(hash(key), key)) == null ? null : e.value;

    }

    @Override public void put(K key, V value) {
        putVal(hash(key), key, value);

    }

    @Override public Object clone() {
        JyHashMap<K, V> result;
        try {
            result = (JyHashMap<K, V>) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new InternalError(e);
        }
        result.reinitialize();
        result.putMapEntries(this);
        return result;
    }

    public boolean containsKey(K key) {
        return getNode(hash(key), key) != null;
    }


    /**
     * 重置为初始默认状态
     */
    void reinitialize() {
        table = null;
        threshold = 0;
        size = 0;
    }

    /**
     * 实现Map.putAll和Map构造函数
     * @param m
     */
    final void putMapEntries(JyHashMap<? extends K, ? extends V> m) {
        int s = m.size();
        if (s > 0) {
            if (table == null) {
                float ft = ((float) s / loadFactor) + 1.0F;
                int t = ((ft < (float) MAXIMUM_CAPACITY) ? (int) ft : MAXIMUM_CAPACITY);
                if (t > threshold) {
                    threshold = tableSizeFor(t);
                }
            } else if (s > threshold) {
                resize();
            }
//            Node<? extends K, ? extends V>[] oldTab = m.table;
            Node< ? extends K, ? extends V>[] oldTab = m.table;
            for (int i = 0; i < oldTab.length; i++) {
                Node<? extends K, ? extends V> node = oldTab[i];
                while (node != null) {
                    K key = node.getKey();
                    V value = node.getValue();
                    putVal(hash(key), key, value);
                    node = node.next;
                }
            }
        }
    }

    /**
     * 判断map是否为空
     * @return
     */
    public boolean isEmpty() {
        return size == 0 || table == null;
    }

    /**
     * 清空map集合
     */
    public void clear() {
        Node<K, V>[] tab;
        if ((tab = table) != null && size > 0) {
            size = 0;
            for (int i = 0; i < tab.length; ++i) {
                tab[i] = null;
            }
        }
    }


    /**
     * 获取节点
     * @param hash 键的hash值
     * @param key  键
     * @return node节点
     */
    final Node<K, V> getNode(int hash, K key) {
        int index = getIndex(key, DEFAULT_INITIAL_CAPACITY);
        Node<K, V>[] tab;
        if ((tab = table) != null && tab.length > 0) {
            Node<K, V> node = table[index];

            if (Objects.equals(key, node.getKey())) {
                return node;
            } else {
                Node<K, V> nextNode = node.getNext();
                do {
                    if (nextNode.hash == hash && Objects.equals(key, nextNode.getKey())) {
                        return nextNode;
                    }
                } while (nextNode != null);
            }
        }
        return null;
    }


    /**
     * put值
     *
     * @param hash
     *        key的hash
     * @param key
     *        key
     * @param value
     *        值
     */
    final void putVal(int hash, K key, V value) {

        //容器是否为空为空则初始化扩容。
        if (table == null) {
            resize();
        }

        //如果size大于阈值则进行扩容
        if (size > DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR) {
            resize();
        }

        //获取index下标
        int index = getIndex(key, DEFAULT_INITIAL_CAPACITY);

        //将k-v键值对放入相对应的下标位置，如果计算出角标相同则以链表的形式存放
        Node<K, V> node = table[index];
        if (node == null) {
            table[index] = newNode(hash, key, value, null);
            size++;
        } else {
            Node<K, V> newNode = node;
            if (Objects.isNull(newNode)){
                table[index] = newNode(hash, key, value, table[index]);
                if (++size > threshold) {
                    resize();
                }
            }else{
                //循环遍历每个节点看看是否存在相同的key
                while (newNode != null) {
                    if (Objects.equals(key,newNode.getKey()) && Objects.equals(hash,node.getHash())) {
                        newNode.setValue(value);
                        size++;
                    }
                    //key不同，hash相同时。node放入下一个节点
                    Node<K, V> nodeNext = node.getNext();
                    if (Objects.isNull(nodeNext)){
                        if (!Objects.equals(key,newNode.getKey()) && Objects.equals(hash,node.getHash())) {
                            newNode.setNext(newNode(hash,key, value, null));
                        }
                    }
                    newNode = node.getNext();
                }
            }
        }

    }

    /**
     * 任何一个key，都需要找到hash后对应的哈希桶位置
     *
     * @param key
     * @param length
     * @return
     */
    public int getIndex(K key, int length) {
        int hashCode = hash(key);
        int index = hashCode % length;
        // int index =  h & (length-1);
        return index;
    }

    /**
     * 对于给定的目标容量，返回两倍大小的幂。
     */
    static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    /**
     * HashCode做一次hash运算。以此更加保证散列性
     *
     * @param key 键
     * @return hash值
     */
    static final int hash(Object key) {
        int h;
        //将hashCode右移16位与hashCode做“异或”运算，即高16位^hashCode（参考jdk8）。如果key为null，固定放到table[0]的位置
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    /**
     * 创建一个常规（非树）节点
     *
     * @param hash
     * @param key
     * @param value
     * @param next
     * @return
     */
    Node<K, V> newNode(int hash, K key, V value, Node<K, V> next) {
        return new Node<>(hash, key, value, next);
    }

    /**
     * 扩容
     *
     * @return
     */
    private Node<K, V>[] resize() {
        Node<K, V>[] oldTab = table;

        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int newCap = 0;
        if (oldCap > 0) {
            if (oldCap >= MAXIMUM_CAPACITY) {
                return oldTab;
            } else {
                newCap = DEFAULT_INITIAL_CAPACITY;
            }
        } else {
            newCap = DEFAULT_INITIAL_CAPACITY;
        }
        Node<K, V>[] newTab = (Node<K, V>[]) new Node[newCap];
        table = newTab;
        if (oldTab != null) {
            for (int j = 0; j < oldCap; ++j) {
                Node<K, V> e;
                if ((e = oldTab[j]) != null) {
                    oldTab[j] = null;
                    if (e.next == null) {
                        newTab[e.hash & (newCap - 1)] = e;
                    }
                }
            }
        }
        table = newTab;
        return newTab;
    }

    /**
     * 打印所有的链表元素
     */
    public void print() {
        System.out.println("-------------------打印开始-----------------------");
        for (int i = 0; i < table.length; i++) {
            Node<K, V> node = table[i];
            System.out.print("下标位置[" + i + "]");
            while (node != null) {
                System.out.print("[ key:" + node.getKey() + ", value:" + node.getValue() + " ]");
                node = node.next;
            }
        }
        System.out.println("-------------------打印结束-----------------------");
    }

    /**
     * 几点元素
     * @param <K>
     * @param <V>
     */
    static class Node<K,V> implements JyMap.Node<K,V> {

        private int hash;

        private K key;

        V value;

        Node<K, V> next; //下一节点

        public Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public Node() {
        }

        @Override public K getKey() {
            return this.key;
        }

        @Override public V getValue() {
            return this.value;
        }

        @Override public V setValue(V value) {
            return this.value = value;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public Node<K, V> getNext() {
            return next;
        }

        public void setNext(Node<K, V> next) {
            this.next = next;
        }

        public int getHash() {
            return hash;
        }

        public void setHash(int hash) {
            this.hash = hash;
        }

        @Override public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }
    }
    }
