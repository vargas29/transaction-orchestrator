package co.com.tumipay.domain.model;

/**
 * Entidad de dominio Customer
 */
public class Customer {
    
    private String document_type;
    private String document_number;
    private String country_calling_code;
    private String phone_number;
    private String email;
    private String first_name;
    private String second_name;
    private String first_surname;
    private String second_surname;

    public Customer() {
    }

    public Customer(String document_type, String document_number, String country_calling_code,
                   String phone_number, String email, String first_name, String first_surname) {
        this.document_type = document_type;
        this.document_number = document_number;
        this.country_calling_code = country_calling_code;
        this.phone_number = phone_number;
        this.email = email;
        this.first_name = first_name;
        this.first_surname = first_surname;
    }

    public void validate() {
        if (document_type == null || document_type.isBlank()) {
            throw new IllegalArgumentException("document_type es requerido");
        }
        if (document_number == null || document_number.isBlank()) {
            throw new IllegalArgumentException("document_number es requerido");
        }
        if (country_calling_code == null || country_calling_code.isBlank()) {
            throw new IllegalArgumentException("country_calling_code es requerido");
        }
        if (phone_number == null || !phone_number.matches("^\\d+$")) {
            throw new IllegalArgumentException("phone_number debe contener solo dígitos");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("email debe ser válido");
        }
        if (first_name == null || first_name.isBlank()) {
            throw new IllegalArgumentException("first_name es requerido");
        }
        if (first_surname == null || first_surname.isBlank()) {
            throw new IllegalArgumentException("first_surname es requerido");
        }
    }

    // Getters y Setters
    public String getDocument_type() { return document_type; }
    public void setDocument_type(String document_type) { this.document_type = document_type; }

    public String getDocument_number() { return document_number; }
    public void setDocument_number(String document_number) { this.document_number = document_number; }

    public String getCountry_calling_code() { return country_calling_code; }
    public void setCountry_calling_code(String country_calling_code) { this.country_calling_code = country_calling_code; }

    public String getPhone_number() { return phone_number; }
    public void setPhone_number(String phone_number) { this.phone_number = phone_number; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirst_name() { return first_name; }
    public void setFirst_name(String first_name) { this.first_name = first_name; }

    public String getSecond_name() { return second_name; }
    public void setSecond_name(String second_name) { this.second_name = second_name; }

    public String getFirst_surname() { return first_surname; }
    public void setFirst_surname(String first_surname) { this.first_surname = first_surname; }

    public String getSecond_surname() { return second_surname; }
    public void setSecond_surname(String second_surname) { this.second_surname = second_surname; }
}

