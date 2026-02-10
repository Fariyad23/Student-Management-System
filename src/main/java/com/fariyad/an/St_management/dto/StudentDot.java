package com.fariyad.an.St_management.dto;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor // <--- YE ZAROORI HAI (Iske bina {} aata hai)
public class StudentDot {
    private Long id;
    private String name;
    private String email;
}