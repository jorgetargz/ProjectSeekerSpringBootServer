package com.jorgetargz.projectseekerspringboot.dto.user;

import lombok.Data;

@Data
public class AddDeviceDTO {
    private String deviceToken;
    private String deviceModel;
}
