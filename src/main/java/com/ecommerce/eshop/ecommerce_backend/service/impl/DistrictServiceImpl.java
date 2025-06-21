package com.ecommerce.eshop.ecommerce_backend.service.impl;


import com.ecommerce.eshop.ecommerce_backend.entity.District;
import com.ecommerce.eshop.ecommerce_backend.repository.DistrictRepository;
import com.ecommerce.eshop.ecommerce_backend.service.DistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.Optional;


@Service
@RequiredArgsConstructor
public class DistrictServiceImpl implements DistrictService {

    private final DistrictRepository districtRepository;

    @Override
    public District create(District district) {
        return districtRepository.save(district);
    }

    @Override
    public Page<District> getAll() {

        return districtRepository.findAll(Pageable.unpaged());
    }

    @Override
    public District getById(Integer id) {
        return districtRepository.findById(id).orElse(null);
    }

    @Override
    public Page<District> getByDivisionId(Integer divisionId) {
        return districtRepository.findByDivisionId(divisionId, Pageable.unpaged());
    }

    @Override
    public District update(Integer id, District updated) {
        Optional<District> optional = districtRepository.findById(id);
        if (optional.isPresent()) {
            District existing = optional.get();
            existing.setDivisionId(updated.getDivisionId());
            existing.setName(updated.getName());
            existing.setBnName(updated.getBnName());
            existing.setLat(updated.getLat());
            existing.setLon(updated.getLon());
            existing.setUrl(updated.getUrl());
            return districtRepository.save(existing);
        }
        return null;
    }

    @Override
    public void delete(Integer id) {
        districtRepository.deleteById(id);
    }
}

