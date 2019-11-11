package domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @NotNull
    private Integer age;
    @Id
    private String id;
    @NotEmpty
    private String name;
    @NotNull
    private Long userId;
}
