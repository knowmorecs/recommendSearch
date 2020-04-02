 //package com.knowmorecs.dianping.canal;
//
//import com.alibaba.otter.canal.client.CanalConnector;
//import com.alibaba.otter.canal.client.CanalConnectors;
//import com.google.common.collect.Lists;
//import org.springframework.beans.factory.DisposableBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Component;
//
//import java.net.InetSocketAddress;
//
///**
// * @author knowmoreCS
// * @create 2020-01-08 14:22
// */
//
//@Component
//public class CanalClient implements DisposableBean {
//
//    //和canal连接的一个重要节点
//    private CanalConnector canalConnector;
//
//    @Bean
//    public CanalConnector getCanalConnector(){
//        canalConnector = CanalConnectors.newClusterConnector(Lists.newArrayList(
//                new InetSocketAddress("127.0.0,1", 11111)),
//                "example","canal","canal"
//        );
//        canalConnector.connect();
//        //指定filter，格式{database}.{table}
//        canalConnector.subscribe();
//        //回滚寻找上次中断的位置
//        canalConnector.rollback();
//
//        return canalConnector;
//    }
//
//    @Override
//    public void destroy() throws Exception {
//        if (canalConnector != null){
//            canalConnector.disconnect();
//        }
//    }
//}
