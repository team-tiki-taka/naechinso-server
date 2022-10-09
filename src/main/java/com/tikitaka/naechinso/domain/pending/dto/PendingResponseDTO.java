package com.tikitaka.naechinso.domain.pending.dto;

import com.tikitaka.naechinso.domain.pending.constant.PendingType;
import com.tikitaka.naechinso.domain.pending.entity.Pending;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class PendingResponseDTO {

    private PendingType type;

    private Boolean isAccepted;

    private String reason;

    private List<String> images;

    private Long adminId;

    private LocalDateTime createdAt;


    public static PendingFindResponseDTO of(Pending pending) {
        return PendingFindResponseDTO.builder()
                .type(pending.getType())
                .isAccepted(pending.getIsAccepted())
                .reason(pending.getReason())
                .images(pending.getImages())
                .adminId(pending.getAdminId())
                .createdAt(pending.getCreatedAt())
                .build();
    }
}