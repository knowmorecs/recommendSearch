package com.knowmorecs.dianping.service;

import com.knowmorecs.dianping.common.BusinessException;
import com.knowmorecs.dianping.model.CategoryModel;

import java.util.List;

public interface CategoryService {

    CategoryModel create(CategoryModel categoryModel) throws BusinessException;
    CategoryModel get(Integer id);
    List<CategoryModel> selectAll();

    Integer countAllCategory();
}
