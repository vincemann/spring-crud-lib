package io.github.vincemann.demo.dtoCrudControllers;

import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.model.PetType;
import io.github.vincemann.demo.model.Specialty;
import io.github.vincemann.demo.service.PetService;
import io.github.vincemann.demo.service.VisitService;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.DTOCrudControllerSpringAdapter;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.ValidationUrlParamIdDTOCrudControllerSpringAdapterIT;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public abstract class EntityInitializerControllerIT<ServiceE extends IdentifiableEntity<Long>, DTO extends IdentifiableEntity<Long>, Service extends CrudService<ServiceE, Long>, Controller extends DTOCrudControllerSpringAdapter<ServiceE, DTO, Long, Service>> extends ValidationUrlParamIdDTOCrudControllerSpringAdapterIT<ServiceE,DTO,Service,Controller,Long> {

    @Autowired
    private PetTypeController petTypeController;
    private PetType testPetType;

    @Autowired
    private SpecialtyController specialtyController;
    private Specialty testSpecialty;

    @Autowired
    private OwnerController ownerController;
    private Owner testOwner;
    @Autowired
    private PetController petController;
    @Autowired
    private VetController vetController;
    @Autowired
    private VisitService visitService;
    @Autowired
    private PetService petService;
    private Pet testPet;

    public EntityInitializerControllerIT(String url, Controller crudController) {
        super(url, crudController,null);
        this.setNonExistingIdFinder(this::findNonExistingId);
    }

    public EntityInitializerControllerIT(Controller crudController) {
        super(crudController, null);
        this.setNonExistingIdFinder(this::findNonExistingId);
    }

    private Long findNonExistingId(){
        List<Long> allIds = new ArrayList<>();
        Set<ServiceE> allEntities = getCrudController().getCrudService().findAll();
        allEntities.forEach(serviceEntity -> {
            allIds.add(serviceEntity.getId());
        });
        Collections.sort(allIds);
        if(allIds.isEmpty()){
            return 1L;
        }
        Long biggestId = allIds.get(allIds.size()-1);
        return biggestId+1L;
    }

    @BeforeEach
    @Override
    public void before() throws Exception {
        //PetType abspeichern, den muss es vorher geben , bevor ich ein Pet abspeicher
        //ich möchte den nicht per cascade erstellen lassen wenn jmd ein pet added und da ein unbekannter pettype drinhängt
        testPetType = petTypeController.getCrudService().save(PetType.builder()
                .name("dog")
                .build());
        testSpecialty = specialtyController.getCrudService().save(Specialty.builder()
                .description("dogliver expert")
                .build());
        testPet = petController.getCrudService().save(Pet.builder()
                .name("bello")
                .birthDate(LocalDate.of(2012,1,23))
                .petType(testPetType)
                .build());
        testOwner = ownerController.getCrudService().save(Owner.builder()
                .firstName("klaus")
                .lastName("Kleber")
                .address("street 123")
                .city("Berlin")
                .build());
        super.before();
    }


    @AfterEach
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        cleanAllServiceEntities(
                petController.getCrudService(),
                petTypeController.getCrudService(),
                ownerController.getCrudService(),
                specialtyController.getCrudService(),
                vetController.getCrudService(),
                visitService);

    }

    private void cleanAllServiceEntities(CrudService... crudServices) throws NoIdException, EntityNotFoundException {
        for(CrudService crudService: crudServices) {
            Set<IdentifiableEntity> allEntitesOfService = crudService.findAll();
            for (IdentifiableEntity identifyableEntity : allEntitesOfService) {
                crudService.delete(identifyableEntity);
            }
            Assertions.assertTrue(crudService.findAll().isEmpty());
        }
    }
}