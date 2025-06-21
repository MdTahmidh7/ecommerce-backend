package com.ecommerce.eshop.ecommerce_backend.service;


import com.ecommerce.eshop.ecommerce_backend.entity.District;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DistrictService {

    District create(District district);

    Page<District> getAll();

    District getById(Integer id);

    Page<District> getByDivisionId(Integer divisionId);

    District update(Integer id, District district);

    void delete(Integer id);

}

