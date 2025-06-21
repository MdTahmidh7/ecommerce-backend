package com.ecommerce.eshop.ecommerce_backend.service;

import com.ecommerce.eshop.ecommerce_backend.entity.Upazila;
import org.springframework.data.domain.Page;

public interface UpazilaService {

    Upazila create(Upazila upazila);

    Page<Upazila> getAll();

    Upazila getById(Integer id);

    Page<Upazila> getByDistrictId(Integer districtId);

    Upazila update(Integer id, Upazila upazila);

    void delete(Integer id);

}

