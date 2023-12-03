package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.model.Pet;
import guru.springframework.sfgpetclinic.model.Visit;
import guru.springframework.sfgpetclinic.services.PetService;
import guru.springframework.sfgpetclinic.services.VisitService;
import guru.springframework.sfgpetclinic.services.map.PetMapService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

/**
 * Created by Rami SAHRAOUI on 03/12/2023
 */
@ExtendWith(MockitoExtension.class)
class VisitControllerTest {

    @Mock
    VisitService visitService;

    @Spy //@Mock
    PetMapService petService; //PetService petService;

    @InjectMocks
    VisitController visitController;
    @Test
    void loadPetWithVisit() {
        // given
        Map<String, Object> model = new HashMap<>();

        Pet pet1 = new Pet(1L);
        Pet pet2 = new Pet(2L);

        petService.save(pet1);
        petService.save(pet2);

        given(petService.findById(anyLong())).willCallRealMethod();

        // when
        Visit visit = visitController.loadPetWithVisit(1L, model);

        // then
        then(petService).should().findById(anyLong());
        assertThat(visit).isNotNull();
        assertThat(visit.getPet()).isNotNull();
        assertThat(visit.getPet().getId()).isEqualTo(1L);
    }
    @Test
    void loadPetWithVisitWithStubbing() {
        // given
        Map<String, Object> model = new HashMap<>();

        Pet pet1 = new Pet(1L);
        Pet pet2 = new Pet(2L);

        petService.save(pet1);
        petService.save(pet2);

        given(petService.findById(anyLong())).willReturn(pet2);

        // when
        Visit visit = visitController.loadPetWithVisit(1L, model);

        // then
        then(petService).should().findById(anyLong());
        assertThat(visit).isNotNull();
        assertThat(visit.getPet()).isNotNull();
        assertThat(visit.getPet().getId()).isNotEqualTo(1L);
        assertThat(visit.getPet().getId()).isEqualTo(2L);
    }
}