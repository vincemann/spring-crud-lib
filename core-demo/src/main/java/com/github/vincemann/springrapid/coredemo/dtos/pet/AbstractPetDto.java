package com.github.vincemann.springrapid.coredemo.dtos.pet;

import com.github.vincemann.springrapid.coredemo.model.Owner;

import com.github.vincemann.springrapid.coredemo.model.PetType;
import com.github.vincemann.springrapid.coredemo.model.Toy;
import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.BiDirChildIdCollection;
import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.UniDirChildId;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.BiDirParentDto;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.UniDirParentDto;
import lombok.*;
import com.github.vincemann.springrapid.entityrelationship.dto.child.BiDirChildDto;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.annotation.BiDirParentId;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public abstract class AbstractPetDto extends IdentifiableEntityImpl<Long>
        implements UniDirParentDto, BiDirChildDto, BiDirParentDto {

    public AbstractPetDto(@Size(min = 2, max = 20) String name, Long petTypeId, Set<Long> toyIds, Long ownerId, LocalDate birthDate) {
        this.name = name;
        this.petTypeId = petTypeId;
        if (toyIds!=null)
            this.toyIds = toyIds;
        this.ownerId = ownerId;
        this.birthDate = birthDate;
    }

    @Size(min = 2, max = 20)
    private String name;

    @UniDirChildId(PetType.class)
    private Long petTypeId;

    @BiDirChildIdCollection(Toy.class)
    private Set<Long> toyIds = new HashSet<>();

    @BiDirParentId(Owner.class)
    private Long ownerId;

    private LocalDate birthDate;
}
