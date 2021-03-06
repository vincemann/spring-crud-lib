package com.github.vincemann.springrapid.acldemo.dto.pet;

import com.github.vincemann.springrapid.acldemo.model.Illness;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.entityrelationship.dto.child.BiDirChildDto;
import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.BiDirChildIdCollection;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.BiDirParentDto;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.annotation.BiDirParentId;
import lombok.*;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@NoArgsConstructor
@ToString(callSuper = true)
@Getter
@Setter
public class FullPetDto extends AbstractPetDto implements BiDirParentDto, BiDirChildDto {

    @BiDirChildIdCollection(Illness.class)
    private Set<Long> illnessIds = new HashSet<>();

    @BiDirParentId(Owner.class)
    private Long ownerId;

    private String name;

    @Builder
    public FullPetDto(String name, Long petTypeId, LocalDate birthDate, Set<Long> illnessIds, Long ownerId) {
        super(petTypeId, birthDate);
        this.name = name;
        this.illnessIds = illnessIds;
        this.ownerId = ownerId;
    }
}
