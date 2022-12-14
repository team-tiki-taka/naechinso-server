package com.tikitaka.naechinso.domain.sms;

import com.tikitaka.naechinso.domain.sms.dto.SmsCertificationRequestDTO;
import com.tikitaka.naechinso.domain.sms.dto.SmsCertificationSuccessResponseDTO;
import com.tikitaka.naechinso.global.common.response.TokenResponseDTO;
import org.springframework.stereotype.Service;

@Service
public interface SmsCertificationService {
    String sendVerificationMessage(String to);
    SmsCertificationSuccessResponseDTO verifyCode(SmsCertificationRequestDTO smsCertificationRequestDto);
}
