package com.knowmorecs.dianping.recommend;

import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author knowmoreCS
 * @create 2020-01-10 12:46
 */

@Service
public class RecommendSortService {

    private SparkSession spark;
    private LogisticRegressionModel lrModel;

    @PostConstruct
    public void init(){
        //加载LR模型
        //初始化spark运行环境
        spark = SparkSession.builder().master("local").appName("DianpingApp").getOrCreate();
        lrModel = LogisticRegressionModel.load("file:///D:\\develop\\Java\\recommender\\src\\main\\resources\\lrmodel");
    }

    public List<Integer> sort(List<Integer> shopIdList,Integer userId){
        //需要根据lrmodel所需要的11维的x，生成特征，然后调用其预测方法
        List<ShopSortModel> list = new ArrayList<>();
        for (Integer shopId : shopIdList){
            //假数据，可以从数据库中拿到真的数据
            Vector v = Vectors.dense(1,0,0,0,0,1,0.6,0,0,1,0);

            Vector result = lrModel.predictProbability(v);

            double[] arr = result.toArray();
            double score = arr[1];

            ShopSortModel shopSortModel = new ShopSortModel();
            shopSortModel.setShopId(shopId);
            shopSortModel.setScore(score);

            list.add(shopSortModel);
        }
        list.sort(new Comparator<ShopSortModel>() {
            @Override
            public int compare(ShopSortModel o1, ShopSortModel o2) {
                if (o1.getScore() < o2.getScore()){
                    return 1;
                }else if (o1.getScore() > o2.getScore()){
                    return -1;
                }else {
                    return 0;
                }
            }
        });
        return list.stream().map(shopSortModel -> shopSortModel.getShopId()).
                collect(Collectors.toList());
    }
}
