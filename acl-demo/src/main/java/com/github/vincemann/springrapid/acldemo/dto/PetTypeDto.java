package com.github.vincemann.springrapid.acldemo.dto;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.*;

import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString(callSuper = true)
public class PetTypeDto extends IdentifiableEntityImpl<Long> {
    @Size(min = 2, max = 20)
    private String name;
}
