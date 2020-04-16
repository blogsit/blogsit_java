package com.blogsit.cache;

import java.util.LinkedHashMap;
import java.util.Map;

public class BlogsitLRUCache {
    /**
     * 使用 linkedHashMap实现LRU算法
     * @param <K>
     * @param <V>
     */
    class  LRUCache<K,V> extends LinkedHashMap<K,V>{
        private  final  int CACHE_SIZE;
        public LRUCache(int cacheSize){
            //true 表示让 linkedhashMap 按照访问顺序来进行排序，最近访问的放在头部，最老访问的放在尾部。
            super((int)Math.ceil(cacheSize/0.75)+1,0.75f,true);
            CACHE_SIZE = cacheSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            // 当 map中的数据量大于指定的缓存个数时候，就自动删除最老的数据。
            return size() > CACHE_SIZE;
        }
    }

}
