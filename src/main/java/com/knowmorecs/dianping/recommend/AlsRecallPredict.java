package com.knowmorecs.dianping.recommend;

import org.apache.commons.lang.StringUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.ForeachPartitionFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;

import java.io.Serializable;
import java.sql.*;
import java.util.*;

/**
 * @author knowmoreCS
 * @create 2020-01-09 15:30
 */
public class AlsRecallPredict {
    public static void main(String[] args) {
        //初始化spark运行环境
        SparkSession spark = SparkSession.builder().master("local").appName("DianpingApp").getOrCreate();

        //将模型加载到内存之中
        ALSModel alsModel = ALSModel.load("file:///D:\\develop\\Java\\recommender\\src\\main\\resources\\alsmodel");

        JavaRDD<String> csvFile = spark.read().textFile("file:///D:\\develop\\Java\\recommender\\src\\main\\resources\\behavior.csv").toJavaRDD();

        JavaRDD<Rating> ratingJavaRDD = csvFile.map(new Function<String, Rating>() {
            @Override
            public Rating call(String s) throws Exception {
                return Rating.parseRating(s);
            }
        });
        Dataset<Row> rating = spark.createDataFrame(ratingJavaRDD,Rating.class);

        //给5个用户做离线的召回结果预测
        Dataset<Row> users = rating.select(alsModel.getUserCol()).distinct().limit(5);
        Dataset<Row> userRecs = alsModel.recommendForUserSubset(users,20);
        userRecs.foreachPartition(new ForeachPartitionFunction<Row>() {
            @Override
            public void call(Iterator<Row> iterator) throws Exception {

                //新建数据库链接
                Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dianpingdb?"+
                        "user=root&password=root&useUnicode=true&characterEncoding=UTF-8");
                PreparedStatement preparedStatement =
                        connection.prepareStatement("insert into recommend(id,recommend)values (?,?)");

                List<Map<String,Object>> data = new ArrayList<>();

                iterator.forEachRemaining(action->{
                    int userId = action.getInt(0);
                    List<GenericRowWithSchema> recommendationList = action.getList(1);
                    List<Integer> shopIdList = new ArrayList<Integer>();
                    recommendationList.forEach(row->{
                        Integer shopId = row.getInt(0);
                        shopIdList.add(shopId);
                    });
                    String recommendData = StringUtils.join(shopIdList,",");
                    Map<String,Object> map = new HashMap<>();
                    map.put("userId",userId);
                    map.put("recommend",recommendData);
                    data.add(map);
                });

                data.forEach(stringObjectMap -> {
                    try {
                        preparedStatement.setInt(1, (Integer) stringObjectMap.get("userId"));
                        preparedStatement.setString(2, (String) stringObjectMap.get("recommend"));
                        preparedStatement.addBatch();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                preparedStatement.executeBatch();
                connection.close();
            }
        });

    }

    public static class Rating implements Serializable {
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
