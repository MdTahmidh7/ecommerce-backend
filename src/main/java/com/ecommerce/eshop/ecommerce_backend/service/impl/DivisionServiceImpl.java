package com.ecommerce.eshop.ecommerce_backend.service.impl;

import com.ecommerce.eshop.ecommerce_backend.entity.Division;
import com.ecommerce.eshop.ecommerce_backend.payload.request.DivisionRequest;
import com.ecommerce.eshop.ecommerce_backend.payload.response.DivisionResponse;
import com.ecommerce.eshop.ecommerce_backend.repository.DivisionRepository;
import com.ecommerce.eshop.ecommerce_backend.service.DivisionService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DivisionServiceImpl implements DivisionService {


    private final DivisionRepository divisionRepository;

    @Override
    public Division createDivision(DivisionRequest divisionRequest) {

        Division division = new Division();
        division.setName(divisionRequest.getName());
        division.setBnName(divisionRequest.getBnName());
        return divisionRepository.save(division);

    }

    @Override
    public DivisionResponse updateDivision(Long id, DivisionRequest divisionRequest) {
        return null;
    }

    @Override
    public void deleteDivision(Long id) {

    }

    @Override
    public DivisionResponse getDivisionById(Long id) {

        Division division = divisionRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new RuntimeException("Division not found or is deleted with ID: " + id));

        DivisionResponse divisionResponse = new DivisionResponse();
        divisionResponse.setId(Long.valueOf(division.getId()));
        divisionResponse.setName(division.getName());
        divisionResponse.setBnName(division.getBnName());

        return divisionResponse;

    }

    @Override
    public Page<DivisionResponse> getAllDivisions(Pageable pageable) {

        Page<Division> divisionPage = divisionRepository.findAll(pageable);
        return divisionPage.map(this::convertToResponse);
    }


    private DivisionResponse convertToResponse(Division division) {

        DivisionResponse divisionResponse = new DivisionResponse();
        divisionResponse.setId(Long.valueOf(division.getId()));
        divisionResponse.setName(division.getName());
        divisionResponse.setBnName(division.getBnName());
        return divisionResponse;

    }
}
