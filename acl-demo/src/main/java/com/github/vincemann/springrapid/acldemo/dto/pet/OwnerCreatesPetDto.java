package com.github.vincemann.springrapid.acldemo.dto.pet;

import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.entityrelationship.dto.child.BiDirChildDto;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.BiDirParentDto;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.annotation.BiDirParentId;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class OwnerCreatesPetDto extends AbstractPetDto implements BiDirParentDto, BiDirChildDto {

    @NotNull
    @BiDirParentId(Owner.class)
    private Long ownerId;

    @NotBlank
    private String name;

    @Builder
    public OwnerCreatesPetDto(String name, Long petTypeId, LocalDate birthDate, Long ownerId) {
        super(petTypeId, birthDate);
        this.name = name;
        this.ownerId = ownerId;
    }

    public OwnerCreatesPetDto(Pet pet, Long ownerId) {
        super(pet);
        this.name=pet.getName();
        this.ownerId = ownerId;
    }
}
