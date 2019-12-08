package com.ita.if103java.ims.mapper;

import com.ita.if103java.ims.dto.AddressDto;
import com.ita.if103java.ims.entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressDtoMapper extends AbstractEntityDtoMapper<Address, AddressDto> {

    @Override
    public Address toEntity(AddressDto dto) {
        if (dto == null) {
            return null;
        } else {
            Address address = new Address();
            address.setId(dto.getId());
            address.setCountry(dto.getCountry());
            address.setCity(dto.getCity());
            address.setAddress(dto.getAddress());
            address.setZip(dto.getZip());
            address.setLatitude(dto.getLatitude());
            address.setLongitude(dto.getLongitude());

            return address;
        }
    }

    @Override
    public AddressDto toDto(Address entity) {
        if (entity == null) {
            return null;
        } else {
            AddressDto addressDto = new AddressDto();
            addressDto.setId(entity.getId());
            addressDto.setCountry(entity.getCountry());
            addressDto.setCity(entity.getCity());
            addressDto.setAddress(entity.getAddress());
            addressDto.setZip(entity.getZip());
            addressDto.setLatitude(entity.getLatitude());
            addressDto.setLongitude(entity.getLongitude());

            return addressDto;
        }
    }
}
