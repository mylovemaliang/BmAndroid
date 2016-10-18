package cn.fuyoushuo.fqbb.domain.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.fuyoushuo.fqbb.domain.entity.CateItem;
import cn.fuyoushuo.fqbb.domain.entity.GoodItem;

/**
 * Created by QA on 2016/6/27.
 */
public class CateGoodMap {


    private static LinkedHashMap<CateItem,List<GoodItem>> linkedHashMap = new LinkedHashMap<CateItem,List<GoodItem>>();

    private  static Map<String,CateItem> allCatesMap = new HashMap<String, CateItem>();

    static {
        CateItem cateItem1 = new CateItem("cate1");
        CateItem cateItem2 = new CateItem("cate2");
        CateItem cateItem3 = new CateItem("cate3");
        CateItem cateItem4 = new CateItem("cate4");
        CateItem cateItem5 = new CateItem("cate5");

        allCatesMap.put("cate1",cateItem1);
        allCatesMap.put("cate2",cateItem2);
        allCatesMap.put("cate3",cateItem3);
        allCatesMap.put("cate4",cateItem4);
        allCatesMap.put("cate5",cateItem5);

        List<String>  mUrls = new ArrayList<String>();
        mUrls.add("http://e.hiphotos.baidu.com/image/pic/item/a1ec08fa513d2697e542494057fbb2fb4316d81e.jpg");
        mUrls.add("http://c.hiphotos.baidu.com/image/pic/item/30adcbef76094b36de8a2fe5a1cc7cd98d109d99.jpg");
        mUrls.add("http://h.hiphotos.baidu.com/image/pic/item/7c1ed21b0ef41bd5f2c2a9e953da81cb39db3d1d.jpg");
        mUrls.add("http://g.hiphotos.baidu.com/image/pic/item/55e736d12f2eb938d5277fd5d0628535e5dd6f4a.jpg");
        mUrls.add("http://e.hiphotos.baidu.com/image/pic/item/4e4a20a4462309f7e41f5cfe760e0cf3d6cad6ee.jpg");
        mUrls.add("http://b.hiphotos.baidu.com/image/pic/item/9d82d158ccbf6c81b94575cfb93eb13533fa40a2.jpg");
        mUrls.add("http://e.hiphotos.baidu.com/image/pic/item/4bed2e738bd4b31c1badd5a685d6277f9e2ff81e.jpg");
        mUrls.add("http://www.huabian.com/uploadfile/2014/1202/20141202025659854.jpg");
        mUrls.add("http://www.huabian.com/uploadfile/2014/1202/20141202025700989.jpg");
        mUrls.add("http://g.hiphotos.baidu.com/image/pic/item/0d338744ebf81a4c87a3add4d52a6059252da61e.jpg");
        mUrls.add("http://a.hiphotos.baidu.com/image/pic/item/f2deb48f8c5494ee5080c8142ff5e0fe99257e19.jpg");
        mUrls.add("http://f.hiphotos.baidu.com/image/pic/item/4034970a304e251f503521f5a586c9177e3e53f9.jpg");
        mUrls.add("http://b.hiphotos.baidu.com/image/pic/item/279759ee3d6d55fbb3586c0168224f4a20a4dd7e.jpg");
        mUrls.add("http://img2.xkhouse.com/bbs/hfhouse/data/attachment/forum/corebbs/2009-11/2009113011534566298.jpg");
        mUrls.add("http://a.hiphotos.baidu.com/image/pic/item/e824b899a9014c087eb617650e7b02087af4f464.jpg");
        mUrls.add("http://c.hiphotos.baidu.com/image/pic/item/9c16fdfaaf51f3de1e296fa390eef01f3b29795a.jpg");
        mUrls.add("http://d.hiphotos.baidu.com/image/pic/item/b58f8c5494eef01f119945cbe2fe9925bc317d2a.jpg");
        mUrls.add("http://h.hiphotos.baidu.com/image/pic/item/902397dda144ad340668b847d4a20cf430ad851e.jpg");
        mUrls.add("http://b.hiphotos.baidu.com/image/pic/item/359b033b5bb5c9ea5c0e3c23d139b6003bf3b374.jpg");
        mUrls.add("http://a.hiphotos.baidu.com/image/pic/item/8d5494eef01f3a292d2472199d25bc315d607c7c.jpg");
        mUrls.add("http://b.hiphotos.baidu.com/image/pic/item/e824b899a9014c08878b2c4c0e7b02087af4f4a3.jpg");
        mUrls.add("http://g.hiphotos.baidu.com/image/pic/item/6d81800a19d8bc3e770bd00d868ba61ea9d345f2.jpg");

        GoodItem goodItem1 = new GoodItem("good1",mUrls.get(0));
        GoodItem goodItem2 = new GoodItem("good2",mUrls.get(1));
        GoodItem goodItem3 = new GoodItem("good3",mUrls.get(2));
        GoodItem goodItem4 = new GoodItem("good4",mUrls.get(3));
        GoodItem goodItem5 = new GoodItem("good5",mUrls.get(4));
        GoodItem goodItem6 = new GoodItem("good6",mUrls.get(5));
        GoodItem goodItem7 = new GoodItem("good7",mUrls.get(6));
        GoodItem goodItem8 = new GoodItem("good8",mUrls.get(10));
        GoodItem goodItem9 = new GoodItem("good9",mUrls.get(11));
        GoodItem goodItem10 = new GoodItem("good10",mUrls.get(12));

        List<GoodItem> goodItems = new ArrayList<GoodItem>();
        goodItems.add(goodItem1);
        goodItems.add(goodItem2);
        goodItems.add(goodItem3);
        goodItems.add(goodItem4);
        goodItems.add(goodItem5);
        goodItems.add(goodItem6);
        goodItems.add(goodItem7);
        goodItems.add(goodItem8);
        goodItems.add(goodItem9);
        goodItems.add(goodItem10);

        linkedHashMap.put(cateItem1,goodItems);
        linkedHashMap.put(cateItem2,goodItems);
        linkedHashMap.put(cateItem3,goodItems);
        linkedHashMap.put(cateItem4,goodItems);
        linkedHashMap.put(cateItem5,goodItems);
    }

    public static LinkedHashMap<CateItem,List<GoodItem>> getCateGoodMap(){
       return linkedHashMap;
    }

    public static List<GoodItem> getGoodItemsByKey(String key){
        CateItem keyCate = allCatesMap.get(key);
        return linkedHashMap.get(keyCate);
    }
}
