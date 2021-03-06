package com.github.vincemann.springrapid.coredemo.service.jpa;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.util.Lists;
import com.github.vincemann.springrapid.coredemo.model.Specialty;
import com.github.vincemann.springrapid.coredemo.model.Vet;
import com.github.vincemann.springrapid.coredemo.service.SpecialtyService;
import com.github.vincemann.springrapid.coretest.service.resolve.EntityPlaceholder;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;

import static com.github.vincemann.springrapid.coretest.service.ExceptionMatchers.noException;
import static com.github.vincemann.springrapid.coretest.service.ExistenceMatchers.notPresentInDatabase;
import static com.github.vincemann.springrapid.coretest.service.PropertyMatchers.propertyAssert;
import static com.github.vincemann.springrapid.coretest.service.request.CrudServiceRequestBuilders.*;

/**
 * Test to showcase that auto management of many-many bidir-relationships work for child side
 */
public class SpecialtyServiceIntegrationTest
        extends ManyToManyServiceIntegrationTest<SpecialtyService, Specialty, Long> {


    @Test
    public void canSaveSpecialty_getLinkedToVets() throws BadEntityException {
        Vet savedMeier = vetService.save(meier);
        Vet savedKahn = vetService.save(kahn);

        dentism.setVets(new HashSet<>(Lists.newArrayList(savedMeier,savedKahn)));
        test(save(dentism))
                .andExpect(() -> propertyAssert(resolve(EntityPlaceholder.DB_ENTITY))
                        .assertSize(SpecialtyType::getVets,2));

        assertVetHasSpecialties(MEIER, DENTISM);
        assertVetHasSpecialties(KAHN, DENTISM);
        assertSpecialtyHasVets(DENTISM, MEIER, KAHN);
    }

    @Test
    public void canSaveSpecialty_getLinkedToVets_whoAlreadyHaveSpecialties() throws BadEntityException {
        Vet savedMeier = vetService.save(meier);
        Vet savedKahn = vetService.save(kahn);

        dentism.setVets(new HashSet<>(Lists.newArrayList(savedMeier,savedKahn)));
        getServiceUnderTest().save(dentism);


        gastro.setVets(new HashSet<>(Lists.newArrayList(savedKahn)));
        test(save(gastro))
                .andExpect(() -> propertyAssert(resolve(EntityPlaceholder.DB_ENTITY))
                        .assertSize(SpecialtyType::getVets,1));

        assertVetHasSpecialties(MEIER, DENTISM);
        assertVetHasSpecialties(KAHN, DENTISM, GASTRO);
        assertSpecialtyHasVets(DENTISM, MEIER, KAHN);
        assertSpecialtyHasVets(GASTRO, KAHN);
    }

    @Test
    public void canRemoveSpecialty_getUnlinkedFromVets() throws BadEntityException {
        // meier -> dentism
        // kahn -> dentism, gastro
        Vet savedMeier = vetService.save(meier);
        Vet savedKahn = vetService.save(kahn);
        dentism.setVets(new HashSet<>(Lists.newArrayList(savedMeier,savedKahn)));
        Specialty savedDentism = getServiceUnderTest().save(dentism);
        gastro.setVets(new HashSet<>(Lists.newArrayList(savedKahn)));
        getServiceUnderTest().save(gastro);

        // remove dentism
        test(deleteById(savedDentism.getId()))
                .andExpect(noException())
                .andExpect(notPresentInDatabase(savedDentism.getId()));


        assertVetHasSpecialties(MEIER);
        assertVetHasSpecialties(KAHN, GASTRO);
        assertSpecialtyHasVets(GASTRO, KAHN);
    }

    @Test
    public void canUnlinkSpecialtyFromVet_viaPartialUpdate() throws BadEntityException {
        // meier -> dentism
        // kahn -> dentism, gastro
        Vet savedMeier = vetService.save(meier);
        Vet savedKahn = vetService.save(kahn);
        dentism.setVets(new HashSet<>(Lists.newArrayList(savedMeier,savedKahn)));
        Specialty savedDentism = getServiceUnderTest().save(dentism);
        gastro.setVets(new HashSet<>(Lists.newArrayList(savedKahn)));
        getServiceUnderTest().save(gastro);

        // unlink dentism from kahn
        Specialty updateSpecialty = Specialty.builder()
                .vets(new HashSet<>(Lists.newArrayList(savedMeier)))
                .build();
        updateSpecialty.setId(savedDentism.getId());
        test(partialUpdate(updateSpecialty))
                .andExpect(() -> propertyAssert(resolve(EntityPlaceholder.SERVICE_RETURNED_ENTITY))
                .assertSize(SpecialtyType::getVets,1));

        assertVetHasSpecialties(MEIER,DENTISM);
        assertVetHasSpecialties(KAHN, GASTRO);
        assertSpecialtyHasVets(GASTRO, KAHN);
        assertSpecialtyHasVets(DENTISM, MEIER);
    }

    @Test
    public void canUnlinkSpecialtyFromVet_viaFullUpdate() throws BadEntityException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // meier -> dentism
        // kahn -> dentism, gastro
        Vet savedMeier = vetService.save(meier);
        Vet savedKahn = vetService.save(kahn);
        dentism.setVets(new HashSet<>(Lists.newArrayList(savedMeier,savedKahn)));
        Specialty savedDentism = getServiceUnderTest().save(dentism);
        gastro.setVets(new HashSet<>(Lists.newArrayList(savedKahn)));
        getServiceUnderTest().save(gastro);

        // unlink dentism from kahn
        Specialty updateSpecialty = (Specialty) BeanUtilsBean.getInstance().cloneBean(savedDentism);
        updateSpecialty.setVets(new HashSet<>(Lists.newArrayList(savedMeier)));
        test(update(updateSpecialty))
                .andExpect(() -> propertyAssert(resolve(EntityPlaceholder.SERVICE_RETURNED_ENTITY))
                        .assertSize(SpecialtyType::getVets,1));

        assertVetHasSpecialties(MEIER,DENTISM);
        assertVetHasSpecialties(KAHN, GASTRO);
        assertSpecialtyHasVets(GASTRO, KAHN);
        assertSpecialtyHasVets(DENTISM, MEIER);
    }

    @Test
    public void canUnlinkSpecialtyFromMultipleVets_viaPartialUpdate() throws BadEntityException {
        // meier -> dentism, heart
        // kahn -> dentism, gastro, heart
        // schuhmacher -> gastro, heart
        Vet savedMeier = vetService.save(meier);
        Vet savedKahn = vetService.save(kahn);
        Vet savedSchuhmacher = vetService.save(schuhmacher);

        dentism.setVets(new HashSet<>(Lists.newArrayList(savedMeier,savedKahn)));
        getServiceUnderTest().save(dentism);
        gastro.setVets(new HashSet<>(Lists.newArrayList(savedKahn,savedSchuhmacher)));
        getServiceUnderTest().save(gastro);
        heart.setVets(new HashSet<>(Lists.newArrayList(savedKahn,savedMeier,savedSchuhmacher)));
        Specialty savedHeart = getServiceUnderTest().save(heart);

        // unlink heart from kahn and meier
        Specialty updateSpecialty = Specialty.builder()
                .vets(new HashSet<>(Lists.newArrayList(savedSchuhmacher)))
                .build();
        updateSpecialty.setId(savedHeart.getId());
        test(partialUpdate(updateSpecialty))
                .andExpect(() -> propertyAssert(resolve(EntityPlaceholder.SERVICE_RETURNED_ENTITY))
                        .assertSize(SpecialtyType::getVets,1));

        assertVetHasSpecialties(MEIER,DENTISM);
        assertVetHasSpecialties(KAHN, DENTISM, GASTRO);
        assertVetHasSpecialties(SCHUHMACHER,  GASTRO, HEART);

        assertSpecialtyHasVets(GASTRO, KAHN, SCHUHMACHER);
        assertSpecialtyHasVets(DENTISM, MEIER, KAHN);
        assertSpecialtyHasVets(HEART, SCHUHMACHER);
    }

    @Test
    public void canUnlinkSpecialtyFromMultipleVets_viaFullUpdate() throws BadEntityException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // meier -> dentism, heart
        // kahn -> dentism, gastro, heart
        // schuhmacher -> gastro, heart
        Vet savedMeier = vetService.save(meier);
        Vet savedKahn = vetService.save(kahn);
        Vet savedSchuhmacher = vetService.save(schuhmacher);

        dentism.setVets(new HashSet<>(Lists.newArrayList(savedMeier,savedKahn)));
        getServiceUnderTest().save(dentism);
        gastro.setVets(new HashSet<>(Lists.newArrayList(savedKahn,savedSchuhmacher)));
        getServiceUnderTest().save(gastro);
        heart.setVets(new HashSet<>(Lists.newArrayList(savedKahn,savedMeier,savedSchuhmacher)));
        Specialty savedHeart = getServiceUnderTest().save(heart);

        // unlink heart from kahn and meier
        Specialty updateSpecialty = (Specialty) BeanUtilsBean.getInstance().cloneBean(savedHeart);
        updateSpecialty.setVets(new HashSet<>(Lists.newArrayList(savedSchuhmacher)));
        test(update(updateSpecialty))
                .andExpect(() -> propertyAssert(resolve(EntityPlaceholder.SERVICE_RETURNED_ENTITY))
                        .assertSize(SpecialtyType::getVets,1));

        assertVetHasSpecialties(MEIER,DENTISM);
        assertVetHasSpecialties(KAHN, DENTISM, GASTRO);
        assertVetHasSpecialties(SCHUHMACHER,  GASTRO, HEART);

        assertSpecialtyHasVets(GASTRO, KAHN, SCHUHMACHER);
        assertSpecialtyHasVets(DENTISM, MEIER, KAHN);
        assertSpecialtyHasVets(HEART, SCHUHMACHER);
    }

    @Test
    public void canUnlinkSpecialtyFromAllVets_viaPartialUpdate() throws BadEntityException {
        // meier -> dentism, heart
        // kahn -> dentism, gastro, heart
        // schuhmacher -> gastro, heart
        Vet savedMeier = vetService.save(meier);
        Vet savedKahn = vetService.save(kahn);
        Vet savedSchuhmacher = vetService.save(schuhmacher);

        dentism.setVets(new HashSet<>(Lists.newArrayList(savedMeier,savedKahn)));
        getServiceUnderTest().save(dentism);
        gastro.setVets(new HashSet<>(Lists.newArrayList(savedKahn,savedSchuhmacher)));
        getServiceUnderTest().save(gastro);
        heart.setVets(new HashSet<>(Lists.newArrayList(savedKahn,savedMeier,savedSchuhmacher)));
        Specialty savedHeart = getServiceUnderTest().save(heart);

        // unlink heart from all
        Specialty updateSpecialty = Specialty.builder()
                .vets(new HashSet<>())
                .build();
        updateSpecialty.setId(savedHeart.getId());
        test(partialUpdate(updateSpecialty))
                .andExpect(() -> propertyAssert(resolve(EntityPlaceholder.SERVICE_RETURNED_ENTITY))
                        .assertEmpty(SpecialtyType::getVets));

        assertVetHasSpecialties(MEIER,DENTISM);
        assertVetHasSpecialties(KAHN, DENTISM, GASTRO);
        assertVetHasSpecialties(SCHUHMACHER,  GASTRO);

        assertSpecialtyHasVets(GASTRO, KAHN, SCHUHMACHER);
        assertSpecialtyHasVets(DENTISM, MEIER, KAHN);
        assertSpecialtyHasVets(HEART);
    }

    @Test
    public void canUnlinkSpecialtyFromAllVets_viaFullUpdate() throws BadEntityException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // meier -> dentism, heart
        // kahn -> dentism, gastro, heart
        // schuhmacher -> gastro, heart
        Vet savedMeier = vetService.save(meier);
        Vet savedKahn = vetService.save(kahn);
        Vet savedSchuhmacher = vetService.save(schuhmacher);

        dentism.setVets(new HashSet<>(Lists.newArrayList(savedMeier,savedKahn)));
        getServiceUnderTest().save(dentism);
        gastro.setVets(new HashSet<>(Lists.newArrayList(savedKahn,savedSchuhmacher)));
        getServiceUnderTest().save(gastro);
        heart.setVets(new HashSet<>(Lists.newArrayList(savedKahn,savedMeier,savedSchuhmacher)));
        Specialty savedHeart = getServiceUnderTest().save(heart);

        // unlink heart from all
        Specialty updateSpecialty = (Specialty) BeanUtilsBean.getInstance().cloneBean(savedHeart);
        updateSpecialty.setVets(new HashSet<>());
        test(update(updateSpecialty))
                .andExpect(() -> propertyAssert(resolve(EntityPlaceholder.SERVICE_RETURNED_ENTITY))
                        .assertEmpty(SpecialtyType::getVets));

        assertVetHasSpecialties(MEIER,DENTISM);
        assertVetHasSpecialties(KAHN, DENTISM, GASTRO);
        assertVetHasSpecialties(SCHUHMACHER,  GASTRO);

        assertSpecialtyHasVets(GASTRO, KAHN, SCHUHMACHER);
        assertSpecialtyHasVets(DENTISM, MEIER, KAHN);
        assertSpecialtyHasVets(HEART);
    }

    @Test
    public void canLinkSpecialtyToSomeVets_viaPartialUpdate() throws BadEntityException {
        // meier -> dentism
        // kahn -> dentism, gastro
        // schuhmacher -> gastro
        Vet savedMeier = vetService.save(meier);
        Vet savedKahn = vetService.save(kahn);
        Vet savedSchuhmacher = vetService.save(schuhmacher);

        dentism.setVets(new HashSet<>(Lists.newArrayList(savedMeier,savedKahn)));
        getServiceUnderTest().save(dentism);
        gastro.setVets(new HashSet<>(Lists.newArrayList(savedKahn,savedSchuhmacher)));
        getServiceUnderTest().save(gastro);
        heart.setVets(new HashSet<>());
        Specialty savedHeart = getServiceUnderTest().save(heart);

        // link heart to meier and kahn
        Specialty updateSpecialty = Specialty.builder()
                .vets(new HashSet<>(Lists.newArrayList(savedMeier,savedKahn)))
                .build();
        updateSpecialty.setId(savedHeart.getId());
        test(partialUpdate(updateSpecialty))
                .andExpect(() -> propertyAssert(resolve(EntityPlaceholder.SERVICE_RETURNED_ENTITY))
                        .assertSize(SpecialtyType::getVets,2));

        assertVetHasSpecialties(MEIER,DENTISM, HEART);
        assertVetHasSpecialties(KAHN, DENTISM, GASTRO,HEART);
        assertVetHasSpecialties(SCHUHMACHER,  GASTRO);

        assertSpecialtyHasVets(GASTRO, KAHN, SCHUHMACHER);
        assertSpecialtyHasVets(DENTISM, MEIER, KAHN);
        assertSpecialtyHasVets(HEART, MEIER, KAHN);
    }

    @Test
    public void canLinkSpecialtyToSomeVets_viaFullUpdate() throws BadEntityException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // meier -> dentism
        // kahn -> dentism, gastro
        // schuhmacher -> gastro
        Vet savedMeier = vetService.save(meier);
        Vet savedKahn = vetService.save(kahn);
        Vet savedSchuhmacher = vetService.save(schuhmacher);

        dentism.setVets(new HashSet<>(Lists.newArrayList(savedMeier,savedKahn)));
        getServiceUnderTest().save(dentism);
        gastro.setVets(new HashSet<>(Lists.newArrayList(savedKahn,savedSchuhmacher)));
        getServiceUnderTest().save(gastro);
        heart.setVets(new HashSet<>());
        Specialty savedHeart = getServiceUnderTest().save(heart);

        // link heart to meier and kahn
        Specialty updateSpecialty = (Specialty) BeanUtilsBean.getInstance().cloneBean(savedHeart);
        updateSpecialty.setVets(new HashSet<>(Lists.newArrayList(savedMeier,savedKahn)));
        test(update(updateSpecialty))
                .andExpect(() -> propertyAssert(resolve(EntityPlaceholder.SERVICE_RETURNED_ENTITY))
                        .assertSize(SpecialtyType::getVets,2));

        assertVetHasSpecialties(MEIER,DENTISM, HEART);
        assertVetHasSpecialties(KAHN, DENTISM, GASTRO,HEART);
        assertVetHasSpecialties(SCHUHMACHER,  GASTRO);

        assertSpecialtyHasVets(GASTRO, KAHN, SCHUHMACHER);
        assertSpecialtyHasVets(DENTISM, MEIER, KAHN);
        assertSpecialtyHasVets(HEART, MEIER, KAHN);
    }

    @Test
    public void canReLinkSpecialty_viaPartialUpdate() throws BadEntityException {
        // meier -> dentism
        // kahn -> dentism, gastro, heart
        // schuhmacher -> gastro, heart
        Vet savedMeier = vetService.save(meier);
        Vet savedKahn = vetService.save(kahn);
        Vet savedSchuhmacher = vetService.save(schuhmacher);

        dentism.setVets(new HashSet<>(Lists.newArrayList(savedMeier,savedKahn)));
        getServiceUnderTest().save(dentism);
        gastro.setVets(new HashSet<>(Lists.newArrayList(savedKahn,savedSchuhmacher)));
        getServiceUnderTest().save(gastro);
        heart.setVets(new HashSet<>(Lists.newArrayList(savedKahn,savedSchuhmacher)));
        Specialty savedHeart = getServiceUnderTest().save(heart);

        // relink heart from kahn and schuhmacher to meier
        Specialty updateSpecialty = Specialty.builder()
                .vets(new HashSet<>(Lists.newArrayList(savedMeier)))
                .build();
        updateSpecialty.setId(savedHeart.getId());
        test(partialUpdate(updateSpecialty))
                .andExpect(() -> propertyAssert(resolve(EntityPlaceholder.SERVICE_RETURNED_ENTITY))
                        .assertSize(SpecialtyType::getVets,1));

        assertVetHasSpecialties(MEIER,DENTISM, HEART);
        assertVetHasSpecialties(KAHN, DENTISM, GASTRO);
        assertVetHasSpecialties(SCHUHMACHER,  GASTRO);

        assertSpecialtyHasVets(GASTRO, KAHN, SCHUHMACHER);
        assertSpecialtyHasVets(DENTISM, MEIER, KAHN);
        assertSpecialtyHasVets(HEART, MEIER);
    }

    @Test
    public void canReLinkSpecialty_viaFullUpdate() throws BadEntityException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // meier -> dentism
        // kahn -> dentism, gastro, heart
        // schuhmacher -> gastro, heart
        Vet savedMeier = vetService.save(meier);
        Vet savedKahn = vetService.save(kahn);
        Vet savedSchuhmacher = vetService.save(schuhmacher);

        dentism.setVets(new HashSet<>(Lists.newArrayList(savedMeier,savedKahn)));
        getServiceUnderTest().save(dentism);
        gastro.setVets(new HashSet<>(Lists.newArrayList(savedKahn,savedSchuhmacher)));
        getServiceUnderTest().save(gastro);
        heart.setVets(new HashSet<>(Lists.newArrayList(savedKahn,savedSchuhmacher)));
        Specialty savedHeart = getServiceUnderTest().save(heart);

        // relink heart from kahn and schuhmacher to meier
        Specialty updateSpecialty = (Specialty) BeanUtilsBean.getInstance().cloneBean(savedHeart);
        updateSpecialty.setVets(new HashSet<>(Lists.newArrayList(savedMeier)));
        test(update(updateSpecialty))
                .andExpect(() -> propertyAssert(resolve(EntityPlaceholder.SERVICE_RETURNED_ENTITY))
                        .assertSize(SpecialtyType::getVets,1));

        assertVetHasSpecialties(MEIER,DENTISM, HEART);
        assertVetHasSpecialties(KAHN, DENTISM, GASTRO);
        assertVetHasSpecialties(SCHUHMACHER,  GASTRO);

        assertSpecialtyHasVets(GASTRO, KAHN, SCHUHMACHER);
        assertSpecialtyHasVets(DENTISM, MEIER, KAHN);
        assertSpecialtyHasVets(HEART, MEIER);
    }




}
