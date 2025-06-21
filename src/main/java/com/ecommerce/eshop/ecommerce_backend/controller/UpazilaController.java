package com.ecommerce.eshop.ecommerce_backend.controller;


import com.ecommerce.eshop.ecommerce_backend.entity.Upazila;
import com.ecommerce.eshop.ecommerce_backend.service.UpazilaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/upazilas")
@RequiredArgsConstructor
public class UpazilaController {

    private final UpazilaService upazilaService;

    @PostMapping
    public ResponseEntity<Upazila> create(@RequestBody Upazila upazila) {
        return ResponseEntity.ok(upazilaService.create(upazila));
    }

    @GetMapping
    public ResponseEntity<Page<Upazila>> getAll() {
        return ResponseEntity.ok(upazilaService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Upazila> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(upazilaService.getById(id));
    }

    @GetMapping("/district/{districtId}")
    public ResponseEntity<Page<Upazila>> getByDistrictId(@PathVariable Integer districtId) {
        return ResponseEntity.ok(upazilaService.getByDistrictId(districtId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Upazila> update(@PathVariable Integer id, @RequestBody Upazila upazila) {
        return ResponseEntity.ok(upazilaService.update(id, upazila));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        upazilaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
