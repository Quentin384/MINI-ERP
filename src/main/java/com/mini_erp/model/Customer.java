package com.mini_erp.model;

public class Customer {

    private final int id;
    private final String firstname;
    private final String lastname;
    private final String address1;
    private final String address2;
    private final String city;
    private final String state;
    private final int zip;
    private final String country;
    private final short region;
    private final String email;
    private final String phone;
    private final String creditcardtype;
    private final String creditcard;
    private final String creditcardexpiration;
    private final String username;
    private final String password;
    private final short age;
    private final int income;
    private final String gender;

    public Customer(int id, String firstname, String lastname, String address1, String address2, String city,
                    String state, int zip, String country, short region, String email, String phone,
                    String creditcardtype, String creditcard, String creditcardexpiration, String username,
                    String password, short age, int income, String gender) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.country = country;
        this.region = region;
        this.email = email;
        this.phone = phone;
        this.creditcardtype = creditcardtype;
        this.creditcard = creditcard;
        this.creditcardexpiration = creditcardexpiration;
        this.username = username;
        this.password = password;
        this.age = age;
        this.income = income;
        this.gender = gender;
    }

    // Getters

    public int getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public int getZip() {
        return zip;
    }

    public String getCountry() {
        return country;
    }

    public short getRegion() {
        return region;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getCreditcardtype() {
        return creditcardtype;
    }

    public String getCreditcard() {
        return creditcard;
    }

    public String getCreditcardexpiration() {
        return creditcardexpiration;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public short getAge() {
        return age;
    }

    public int getIncome() {
        return income;
    }

    public String getGender() {
        return gender;
    }

    @Override
    public String toString() {
        return firstname + " " + lastname;
    }
}
