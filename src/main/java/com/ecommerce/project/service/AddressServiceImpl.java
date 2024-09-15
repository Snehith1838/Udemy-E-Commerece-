package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.repository.AddressRepository;
import com.ecommerce.project.repository.UserRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService{

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Override
    public AddressDTO createAddress(@Valid AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO, Address.class);

        List<Address> addressList = user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);

        address.setUser(user);
        Address savedAddress = addressRepository.save(address);

        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<Address> addresses = addressRepository.findAll();

        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(()-> new ResourceNotFoundException("Address","addressId",addressId));
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getUserAddresses(User user) {
        List<Address> userAddresses = user.getAddresses();
        return userAddresses.stream()
                .map(address-> modelMapper.map(address, AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO updateAddressById(Long addressId, AddressDTO addressDTO) {

        Address address = addressRepository.findById(addressId)
                .orElseThrow(()-> new ResourceNotFoundException("Address","addressId",addressId));
        address.setCity(addressDTO.getCity());
        address.setPincode(addressDTO.getPincode());
        address.setCountry(addressDTO.getCountry());
        address.setState(addressDTO.getState());
        address.setStreet(addressDTO.getStreet());
        address.setBuildingName(addressDTO.getBuildingName());

        Address updatedAddress = addressRepository.save(address);

        User user = address.getUser();
        user.getAddresses().removeIf(address1 -> address1.getAddressId().equals(addressId));
        user.getAddresses().add(updatedAddress);
        userRepository.save(user);

        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public String deleteAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(()-> new ResourceNotFoundException("Address","addressId",addressId));

        User user = address.getUser();
        user.getAddresses().removeIf(address1 -> address1.getAddressId().equals(addressId));
        userRepository.save(user);

        addressRepository.delete(address);
        return "Address deleted successfully with addressId: " + addressId;
    }
}
