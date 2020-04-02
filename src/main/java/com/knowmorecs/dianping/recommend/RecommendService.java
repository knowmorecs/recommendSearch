package com.knowmorecs.dianping.recommend;

import com.knowmorecs.dianping.dal.RecommendDOMapper;
import com.knowmorecs.dianping.model.RecommendDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.Int;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author knowmoreCS
 * @create 2020-01-10 11:00
 */
@Service
public class RecommendService implements Serializable {

    @Autowired
    private RecommendDOMapper recommendDOMapper;

    //召回数据，根据userid召回shopidList
    public List<Integer> recall(Integer userId){
        RecommendDO recommendDO = recommendDOMapper.selectByPrimaryKey(userId);
        if (recommendDO == null){
            recommendDO = recommendDOMapper.selectByPrimaryKey(9999999);
        }
        String[] shopIdArr = recommendDO.getRecommend().split(",");
        List<Integer> shopIdList = new ArrayList<>();
        for (int i = 0; i < shopIdArr.length; i++) {
            shopIdList.add(Integer.valueOf(shopIdArr[i]));
        }
        return shopIdList;
    }
}
