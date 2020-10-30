package com.github.jinchunzhao;

/**
 * $start$
 *
 * @author JinChunZhao
 * @version 1.0
 * @date 2020-10-15 16:56
 */
public class JyHashMapTest {

    public static void main(String[] args) {
        JyHashMap<String,String> map = new JyHashMap();
        map.put("li","123123123");
        map.put("li1","123123123");
        map.put("li2","123123123");
        map.put("li","123123124");
        map.put("li3","123123124");
        map.put("li4","123123124");
        map.put("li5","123123124");
        map.put("li6","123123124");
        map.put("li7","123123124");
        map.put("li8","123123124");
        map.put("li9","123123124");
        map.put("li10","123123124");
        map.put("li11","123123124");
        map.put("li12","123123124");
        map.put("li14","123123124");
        map.put("li13","123123124");
        map.put("li15","123123124");
        map.put("li16","123123124");
        map.put("li17","123123124");
        map.print();
        String li = map.get("li");
        System.out.println(li);
        boolean li1 = map.containsKey("li");
        System.out.println(li1);
                JyHashMap<String,String> map1 = (JyHashMap<String, String>) map.clone();
        map1.print();
//        map.clear();
//        map.print();
    }

}
