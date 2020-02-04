package com.ita.if103java.ims.dto.warehouse.advice;

public class Address {
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
