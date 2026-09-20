package com.declutter.dclutter.service;

import com.declutter.dclutter.dto.AddressDTO;
import java.util.List;

public interface AddressService {

    AddressDTO createAddress(AddressDTO addressDTO);

    List<AddressDTO> getAllAddresses();

    AddressDTO getAddressById(Long addressId);

    List<AddressDTO> getUserAddresses();

    AddressDTO updateAddress(Long addressId, AddressDTO addressDTO);

    String deleteAddress(Long addressId);
}