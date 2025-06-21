package com.ecommerce.eshop.ecommerce_backend.service;

import com.ecommerce.eshop.ecommerce_backend.entity.Division;
import com.ecommerce.eshop.ecommerce_backend.payload.request.DivisionRequest;
import com.ecommerce.eshop.ecommerce_backend.payload.response.DivisionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface DivisionService {

    public Division createDivision(DivisionRequest divisionRequest);

    public DivisionResponse updateDivision(Long id, DivisionRequest divisionRequest);

    public void deleteDivision(Long id);

    public DivisionResponse getDivisionById(Long id);

    public Page<DivisionResponse> getAllDivisions(Pageable pageable);

}
