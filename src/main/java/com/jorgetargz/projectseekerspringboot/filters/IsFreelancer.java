package com.jorgetargz.projectseekerspringboot.filters;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ROLE_FREELANCER')")
public @interface IsFreelancer {
}
