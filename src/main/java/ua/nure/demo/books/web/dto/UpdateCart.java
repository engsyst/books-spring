package ua.nure.demo.books.web.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.web.bind.annotation.RequestParam;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString
public class UpdateCart {
    @Nullable
    private Long remove;
    @Nullable
    private Long update;
    @Setter
    private int count;

    public boolean isValid() {
        return !(remove == null && update == null);
    }
}
