package by.doni.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor

public class Post implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 120, message = "Максимальный размер текста - 120 символов")
    private String text;

    @Pattern(regexp = "^#.*", message = "Тэг должен начинаться с '#'")
    private String tag;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    public Post(String text, String tag) {
        this.text = text;
        this.tag = tag;
    }
}
