# recommendSearch
##SpringBoot搭建JavaWeb前后端，ES实现搜索服务，Spark Mllib实现推荐系统
1. 利用SpringBoot及Mybatis框架搭建JavaWeb工程，MySQL做数据库，druid做数据库连接池
2. 用户服务，运营后台服务及商户服务的后端模块设计搭建
3. 封装通用返回和通用异常； Spring AOP捕获异常并进行统一异常处理及进行鉴权接入管理
4. 利用ELK组件（Elasticsearch+Logstash+Kibana）构建搜索服务，实现类别相关性搜索功能 
5. 基于canal消息管道中间件完成准实时增量索引接入
6. 利用Spark Mllib的ALS算法实现召回体系，LR算法实现排序体系，实现推荐服务
7. 优化推荐服务，使用GBDT实现点击率预估模型，使用AB Test评估算法质量
