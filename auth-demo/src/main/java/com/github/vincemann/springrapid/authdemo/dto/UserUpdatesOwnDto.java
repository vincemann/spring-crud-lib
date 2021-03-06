package com.github.vincemann.springrapid.authdemo.dto;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatesOwnDto extends IdentifiableEntityImpl<Long> {
    private String name;
}
