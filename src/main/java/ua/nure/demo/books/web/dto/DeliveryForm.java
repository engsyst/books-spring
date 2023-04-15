package ua.nure.demo.books.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import ua.nure.demo.books.entity.Delivery;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.StringTokenizer;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryForm {
    @NotBlank
    @Size(max = 45)
    private String name;
    @NotBlank
    @Pattern(regexp = "(\\d[- ]?){12}")
    /* \d[- ]?\d[- ]?\d[- ]?\d[- ]?\d[- ]?\d[- ]?\d[- ]?\d[- ]?\d[- ]?\d[- ]?\d[- ]?\d*/
    private String phone;
    @NotBlank
    @Size(max = 45)
    @Pattern(regexp = "\\S+?@\\S+?\\.\\w+")
    private String email;
    @NotBlank
    @Size(max = 1000)
    private String address;
    @Size(max = 1000)
    private String description;

    public void setName(String name) {
        this.name = name.trim();
    }

    public void setPhone(String phone) {
        this.phone = phone.trim();
    }

    public void setEmail(String email) {
        this.email = email.trim();
    }

    public void setAddress(String address) {
        this.address = address.trim();
    }

    public void setDescription(String description) {
        this.description = description.trim();
    }

    public Delivery asDelivery() {
        return new Delivery(null, name, phone, email, address, description, null);
    }
}
