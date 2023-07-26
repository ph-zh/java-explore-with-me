package ru.practicum.mainservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFullDto {
    private Long id;
    @NotBlank
    @Size(max = 255)
    private String name;
    @NotBlank
    @Email
    @Size(max = 512)
    private String email;
}
