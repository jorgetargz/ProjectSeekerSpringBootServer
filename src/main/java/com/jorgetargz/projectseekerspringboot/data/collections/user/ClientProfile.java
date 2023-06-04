package com.jorgetargz.projectseekerspringboot.data.collections.user;

import lombok.Data;

@Data
public class ClientProfile {
    private String title;
    private String description;

    public ClientProfile() {
        title = "";
        description = "";
    }
}
