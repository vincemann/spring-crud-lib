package com.github.vincemann.springrapid.acldemo.repositories;

import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@ServiceComponent
public interface OwnerRepository extends JpaRepository<Owner,Long> {
    Optional<Owner> findByLastName(String lastName);
}
