package com.ita.if103java.ims.dto;

import com.ita.if103java.ims.entity.AssociateType;

public class BestAssociateDto {
    private Associate associate;
    private Long totalTransactionQuantity;

    public BestAssociateDto() {
    }

    public BestAssociateDto(Associate associate, Long totalTransactionQuantity) {
        this.associate = associate;
        this.totalTransactionQuantity = totalTransactionQuantity;
    }

    public Associate getAssociate() {
        return associate;
    }

    public void setAssociate(Associate associate) {
        this.associate = associate;
    }

    public Long getTotalTransactionQuantity() {
        return totalTransactionQuantity;
    }

    public void setTotalTransactionQuantity(Long totalTransactionQuantity) {
        this.totalTransactionQuantity = totalTransactionQuantity;
    }

    @Override
    public String toString() {
        return "BestAssociateDto{" +
            "associate=" + associate +
            ", totalTransactionQuantity=" + totalTransactionQuantity +
            '}';
    }

    public static class Associate {
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

        public static class Address {
            private String country;
            private String city;
            private String street;
            private Geo geo;

            public Address(String country, String city, String street, Geo geo) {
                this.country = country;
                this.city = city;
                this.street = street;
                this.geo = geo;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getStreet() {
                return street;
            }

            public void setStreet(String street) {
                this.street = street;
            }

            public Geo getGeo() {
                return geo;
            }

            public void setGeo(Geo geo) {
                this.geo = geo;
            }

            @Override
            public String toString() {
                return "Address{" +
                    "country='" + country + '\'' +
                    ", city='" + city + '\'' +
                    ", street='" + street + '\'' +
                    ", geo=" + geo +
                    '}';
            }

            public static class Geo {
                private Float latitude;
                private Float longitude;

                public Geo() {
                }

                public Geo(Float latitude, Float longitude) {
                    this.latitude = latitude;
                    this.longitude = longitude;
                }

                public Float getLatitude() {
                    return latitude;
                }

                public void setLatitude(Float latitude) {
                    this.latitude = latitude;
                }

                public Float getLongitude() {
                    return longitude;
                }

                public void setLongitude(Float longitude) {
                    this.longitude = longitude;
                }

                @Override
                public String toString() {
                    return "Geo{" +
                        "latitude=" + latitude +
                        ", longitude=" + longitude +
                        '}';
                }
            }
        }
    }
}
