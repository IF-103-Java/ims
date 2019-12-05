package com.ita.if103java.ims.mapper;

import com.ita.if103java.ims.dto.AddressDto;
import com.ita.if103java.ims.entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressDtoMapper {

    public AddressDto convertAddressToAddressDto(Address address) {
        if (address == null) {
            return null;
        } else {
            AddressDto addressDto = new AddressDto();
            addressDto.setId(address.getId());
            addressDto.setCountry(address.getCountry());
            addressDto.setCity(address.getCity());
            addressDto.setAddress(address.getAddress());
            addressDto.setZip(address.getZip());
            addressDto.setLatitude(address.getLatitude());
            addressDto.setLongitude(address.getLongitude());

            return addressDto;
        }
    }

    public Address convertAddressDtoToAddress(AddressDto addressDto) {
        if (addressDto == null) {
            return null;
        } else {
            Address address = new Address();
            address.setId(addressDto.getId());
            address.setCountry(addressDto.getCountry());
            address.setCity(addressDto.getCity());
            address.setAddress(addressDto.getAddress());
            address.setZip(addressDto.getZip());
            address.setLatitude(addressDto.getLatitude());
            address.setLongitude(addressDto.getLongitude());

            return address;
        }
    }
}
