package by.doni.core.web.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePostRequest {

    @Size(max = 120, message = "Максимальный разрер текста - 120 символов")
    private String text;

    @Pattern(regexp = "^#.*", message = "Тэг должен начинаттся с '#'")
    private String tag;
}
