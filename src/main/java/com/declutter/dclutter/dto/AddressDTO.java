package com.declutter.dclutter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {

    private Long addressId;

    @NotBlank(message = "Street is required")
    @Size(min = 5, message = "Street must be at least 5 characters")
    private String street;

    @NotBlank(message = "Building name is required")
    @Size(min = 5, message = "Building name must be at least 5 characters")
    private String buildingName;

    @NotBlank(message = "City is required")
    @Size(min = 3, message = "City must be at least 3 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(min = 2, message = "State must be at least 2 characters")
    private String state;

    @NotBlank(message = "Country is required")
    @Size(min = 2, message = "Country must be at least 2 characters")
    private String country;

    @NotBlank(message = "Pincode is required")
    @Size(min = 6, max = 6, message = "Pincode must be exactly 6 digits")
    private String pincode;
}