package com.github.vincemann.springrapid.coredemo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.github.vincemann.springrapid.coredemo.model.abs.Person;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.BiDirChildEntity;
import com.github.vincemann.springrapid.entityrelationship.model.child.annotation.UniDirChildEntity;
import com.github.vincemann.springrapid.entityrelationship.model.parent.BiDirParent;
import com.sun.istack.NotNull;
import lombok.*;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.CascadeType.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "owners")
public class Owner extends Person implements BiDirParent {


    @Builder
    public Owner(String firstName, String lastName, Set<Pet> pets, String address, String city, String telephone,Set<String> hobbies) {
        super(firstName, lastName);
        if(pets!=null) {
            this.pets = pets;
        }
        if(hobbies!=null) {
            this.hobbies = hobbies;
        }
        this.address = address;
        this.city = city;
        this.telephone = telephone;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner",fetch = FetchType.EAGER)
    @JsonManagedReference
    @BiDirChildCollection(Pet.class)
    private Set<Pet> pets = new HashSet<>();

    @BiDirChildEntity
    // dont use remove cascade to showcase unlink on remove owner
    @OneToOne(cascade = {PERSIST, MERGE, REFRESH, DETACH},fetch = FetchType.EAGER)
    @JoinColumn(name = "clinic_card_id",referencedColumnName = "id")
    private ClinicCard clinicCard;


    @ElementCollection(targetClass = String.class,fetch = FetchType.EAGER)
    private Set<String> hobbies = new HashSet<>();

    @Column(name = "adress")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "telephone")
    private String telephone;



    @Override
    public String toString() {
        return "Owner{" +
                super.toString() +
                "pets=" + Arrays.toString(pets.stream().map(Pet::getName).toArray()) +
                ", hobbies='"+hobbies+"'" +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", telephone='" + telephone + '\'' +
                '}';
    }

}
