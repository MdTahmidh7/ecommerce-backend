package com.ecommerce.eshop.ecommerce_backend.service.impl;


import com.ecommerce.eshop.ecommerce_backend.entity.Upazila;
import com.ecommerce.eshop.ecommerce_backend.repository.UpazilaRepository;
import com.ecommerce.eshop.ecommerce_backend.service.UpazilaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UpazilaServiceImpl implements UpazilaService {

    private final UpazilaRepository upazilaRepository;

    @Override
    public Upazila create(Upazila upazila) {
        return upazilaRepository.save(upazila);
    }

    @Override
    public Page<Upazila> getAll() {
        return upazilaRepository.findAll(Pageable.unpaged());
    }

    @Override
    public Upazila getById(Integer id) {
        return upazilaRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Upazila> getByDistrictId(Integer districtId) {
        return upazilaRepository.findByDistrictId(districtId, Pageable.unpaged());
    }

    @Override
    public Upazila update(Integer id, Upazila updated) {
        Optional<Upazila> optional = upazilaRepository.findById(id);
        if (optional.isPresent()) {
            Upazila existing = optional.get();
            existing.setDistrictId(updated.getDistrictId());
            existing.setName(updated.getName());
            existing.setBnName(updated.getBnName());
            existing.setUrl(updated.getUrl());
            return upazilaRepository.save(existing);
        }
        return null;
    }

    @Override
    public void delete(Integer id) {
        upazilaRepository.deleteById(id);
    }
}
