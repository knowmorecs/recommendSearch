package com.knowmorecs.dianping.recommend;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.recommendation.ALS;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author knowmoreCS
 * @create 2020-01-09 11:43
 * ALS召回算法的训练
 */
public class AlsRecallTrain implements Serializable {

    public static void main(String[] args) throws IOException {
//        //初始化spark运行环境
//        SparkSession spark = SparkSession.builder().master("local").appName("DianpingApp").getOrCreate();
//
//        //读取csvFile，输出javaRDD 必须三个斜杠
//        JavaRDD<String> csvFile = spark.read().textFile("file:///D:\\develop\\Java\\recommender\\src\\main\\resources\\behavior.csv").toJavaRDD();
//
//        JavaRDD<Rating> ratingJavaRDD = csvFile.map(new Function<String, Rating>() {
//            @Override
//            public Rating call(String v1) throws Exception {
//                return Rating.parseRating(v1);
//            }
//        });
//
//        Dataset<Row> rating = spark.createDataFrame(ratingJavaRDD,Rating.class);
//
//        //将所有的rating数据集分成82份
//        Dataset<Row>[] splits = rating.randomSplit(new double[]{0.8,0.2});
//        Dataset<Row> trainingData = splits[0];
//        Dataset<Row> testingData = splits[1];
//
//        //迭代次数10，分解矩阵后的feature的数量,正则化系数0.01，防止过拟合
//        //过拟合：增大数据规模，减少Rank,增大正则化系数
//        //欠拟合：增加rank，减少正则化系数
//        ALS als = new ALS().setMaxIter(10).setRank(5).setRegParam(0.01).
//                setUserCol("userId").setItemCol("shopId").setRatingCol("rating");
//
//        ALSModel alsModel = als.fit(trainingData);
//        alsModel.save("file:///D:\\develop\\Java\\recommender\\src\\main\\resources\\alsmodel");



        //初始化spark初始环境
        SparkSession spark = SparkSession.builder().master("local").appName("DianpingApp").getOrCreate();
        JavaRDD<String> csvFile = spark.read().textFile("file:///D:\\develop\\Java\\recommender\\src\\main\\resources\\behavior.csv").toJavaRDD();
        JavaRDD<Rating> ratingJavaRDD = csvFile.map(new Function<String, Rating>() {
            public Rating call(String s) throws Exception {
                return Rating.parseRating(s);
            }
        });
        Dataset<Row> rating = spark.createDataFrame(ratingJavaRDD, Rating.class);

        //将所有的rating数据分成82分
        Dataset<Row>[] splits = rating.randomSplit(new double[]{0.8, 0.2});
        Dataset<Row> trainingData = splits[0];
        Dataset<Row> testingData = splits[1];
        //5个特征,
        //解决过拟合:1增大数据规模，减少rank,增大正则化系数
        //欠拟合：增加rank,减少正则化系数
        ALS als = new ALS().setMaxIter(10).setRank(5).setRegParam(0.01).setUserCol("userId")
                .setItemCol("shopId").setRatingCol("rating");
        ALSModel alsModel = als.fit(trainingData);

        //模型评测 将测试集使用模型做一次转化的预测
        Dataset<Row> predictions =  alsModel.transform(testingData);

        //rmse 均方根误差，预测值与真实值的偏差的平方除以观测次数，开个根号
        RegressionEvaluator evaluator = new RegressionEvaluator().setMetricName("rmse")
                .setLabelCol("rating").setPredictionCol("prediction");
        double rmse = evaluator.evaluate(predictions);
        System.out.println("rmse" + rmse);

        alsModel.save("file:///D:\\develop\\Java\\recommender\\src\\main\\resources\\alsmodel");
    }

    public static class Rating implements Serializable{
        private int userId;
        private int shopId;
        private int rating;

        public static Rating parseRating(String str){
            str = str.replace("\"","");
            String[] strArr = str.split(",");
            int userId = Integer.parseInt(strArr[0]);
            int shopId = Integer.parseInt(strArr[1]);
            int rating = Integer.parseInt(strArr[2]);

            return new Rating(userId,shopId,rating);
        }

        public Rating(int userId, int shopId, int rating) {
            this.userId = userId;
            this.shopId = shopId;
            this.rating = rating;
        }

        public int getUserId() {
            return userId;
        }

        public int getShopId() {
            return shopId;
        }

        public int getRating() {
            return rating;
        }
    }
}
