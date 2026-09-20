package com.declutter.dclutter.controller;

import com.declutter.dclutter.dto.APIResponse;
import com.declutter.dclutter.dto.AddressDTO;
import com.declutter.dclutter.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.createAddress(addressDTO));
    }

    @GetMapping("/addresses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        return ResponseEntity.ok(addressService.getAllAddresses());
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.getAddressById(addressId));
    }

    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressDTO>> getUserAddresses() {
        return ResponseEntity.ok(addressService.getUserAddresses());
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long addressId,
                                                    @Valid @RequestBody AddressDTO addressDTO) {
        return ResponseEntity.ok(addressService.updateAddress(addressId, addressDTO));
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<APIResponse> deleteAddress(@PathVariable Long addressId) {
        return ResponseEntity.ok(new APIResponse(addressService.deleteAddress(addressId), true));
    }
}