package com.ecommerce.eshop.ecommerce_backend.controller;

import com.ecommerce.eshop.ecommerce_backend.payload.response.DivisionResponse;
import com.ecommerce.eshop.ecommerce_backend.service.DivisionService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class DivisionController {

    private final DivisionService divisionService;

    //create api for getting all Division
    @GetMapping("/api/divisions")
    public ResponseEntity<Page<DivisionResponse>> getAllDivisions() {

        Pageable pageable = Pageable.unpaged();
        return ResponseEntity.ok(divisionService.getAllDivisions(pageable));
    }


}
