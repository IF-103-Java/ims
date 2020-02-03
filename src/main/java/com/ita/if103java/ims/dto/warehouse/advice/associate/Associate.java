package com.ita.if103java.ims.dto.warehouse.advice.associate;

import com.ita.if103java.ims.dto.warehouse.advice.Address;
import com.ita.if103java.ims.entity.AssociateType;

public class Associate {
        private Long id;
        private String name;
        private Address address;
        private AssociateType type;

        public Associate() {
        }

        public Associate(Long id, String name, Address address, AssociateType type) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.type = type;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public AssociateType getType() {
            return type;
        }

        public void setType(AssociateType type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "Associate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address=" + address +
                ", type=" + type +
                '}';
        }
    }
