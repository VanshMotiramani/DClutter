package com.declutter.dclutter.service;

import com.declutter.dclutter.dto.AddressDTO;
import com.declutter.dclutter.exception.ResourceNotFoundException;
import com.declutter.dclutter.model.Address;
import com.declutter.dclutter.model.User;
import com.declutter.dclutter.repository.AddressRepository;
import com.declutter.dclutter.repository.UserRepository;
import com.declutter.dclutter.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthUtil authUtil;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO) {
        // Get logged-in user
        User user = authUtil.loggedInUser();

        // Create new address
        Address address = new Address();
        address.setStreet(addressDTO.getStreet());
        address.setBuildingName(addressDTO.getBuildingName());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setCountry(addressDTO.getCountry());
        address.setPincode(addressDTO.getPincode());

        // Save address first
        Address savedAddress = addressRepository.save(address);

        // Add to user's addresses
        user.getAddresses().add(savedAddress);
        userRepository.save(user);

        return mapToDTO(savedAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDTO> getAllAddresses() {
        return addressRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        return mapToDTO(address);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDTO> getUserAddresses() {
        String username = authUtil.loggedInUsername();
        List<Address> addresses = addressRepository.findAddressesByUsername(username);

        return addresses.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        // Update fields
        address.setStreet(addressDTO.getStreet());
        address.setBuildingName(addressDTO.getBuildingName());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setCountry(addressDTO.getCountry());
        address.setPincode(addressDTO.getPincode());

        Address updatedAddress = addressRepository.save(address);

        return mapToDTO(updatedAddress);
    }

    @Override
    public String deleteAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        List<User> linkedUsers = List.copyOf(address.getUsers());
        for (User user : linkedUsers) {
            user.getAddresses().remove(address);
        }
        address.getUsers().clear();

        addressRepository.delete(address);

        return "Address deleted successfully";
    }

    private AddressDTO mapToDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        dto.setAddressId(address.getAddressId());
        dto.setStreet(address.getStreet());
        dto.setBuildingName(address.getBuildingName());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setCountry(address.getCountry());
        dto.setPincode(address.getPincode());
        return dto;
    }
}