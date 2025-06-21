package com.ecommerce.eshop.ecommerce_backend.controller;

import com.ecommerce.eshop.ecommerce_backend.entity.District;
import com.ecommerce.eshop.ecommerce_backend.service.DistrictService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/districts")
@RequiredArgsConstructor
public class DistrictController {

    private final DistrictService districtService;

    @PostMapping
    public ResponseEntity<District> create(@RequestBody District district) {
        return ResponseEntity.ok(districtService.create(district));
    }

    @GetMapping
    public ResponseEntity<Page<District>> getAll() {
        return ResponseEntity.ok(districtService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<District> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(districtService.getById(id));
    }

    @GetMapping("/division/{divisionId}")
    public ResponseEntity<Page<District>> getByDivisionId(@PathVariable Integer divisionId) {
        return ResponseEntity.ok(districtService.getByDivisionId(divisionId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<District> update(@PathVariable Integer id, @RequestBody District district) {
        return ResponseEntity.ok(districtService.update(id, district));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        districtService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

