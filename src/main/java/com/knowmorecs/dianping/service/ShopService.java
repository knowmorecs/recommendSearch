package com.knowmorecs.dianping.service;

import com.knowmorecs.dianping.common.BusinessException;
import com.knowmorecs.dianping.model.ShopModel;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ShopService {
    ShopModel create(ShopModel shopModel) throws BusinessException;
    ShopModel get(Integer id);
    List<ShopModel> selectAll();
    List<ShopModel> recommend(BigDecimal longitude,BigDecimal latitude);

    List<Map<String,Object>> searchGroupByTags(String keyword,Integer categoryId,String tags);

    Integer countAllShop();

    List<ShopModel> search(BigDecimal longitude,BigDecimal latitude,
                           String keyword,Integer orderby,Integer categoryId,String tags);

    Map<String,Object> searchES(BigDecimal longitude,BigDecimal latitude,
                              String keyword,Integer orderby,Integer categoryId,String tags) throws IOException;
}
