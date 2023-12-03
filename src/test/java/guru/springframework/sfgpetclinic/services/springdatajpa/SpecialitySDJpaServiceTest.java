package guru.springframework.sfgpetclinic.services.springdatajpa;

import guru.springframework.sfgpetclinic.model.Speciality;
import guru.springframework.sfgpetclinic.repositories.SpecialtyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class SpecialitySDJpaServiceTest {

    @Mock(lenient = true)
    SpecialtyRepository specialtyRepository;

    @InjectMocks
    SpecialitySDJpaService service;

    @Test
    void testDeleteByObject() {
        // given
        Speciality speciality = new Speciality();

        // when
        service.delete(speciality);

        //then
        then(specialtyRepository).should().delete(any(Speciality.class));
    }

    @Test
    void findByIdTest() {
        // given
        Speciality speciality = new Speciality();
        given(specialtyRepository.findById(1L)).willReturn(Optional.of(speciality));

        // when
        Speciality foundSpecialty = service.findById(1L);

        // then
        assertThat(foundSpecialty).isNotNull();
        then(specialtyRepository).should(timeout(100)).findById(anyLong());
        then(specialtyRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void deleteById() {
        // given
        long specialityId = 1L;

        // when
        service.deleteById(specialityId);
        service.deleteById(specialityId);

        // then
        then(specialtyRepository).should(timeout(100).times(2)).deleteById(specialityId);
    }

    @Test
    void deleteByIdAtLeast() {
        // given
        long specialityId = 1L;

        // when
        service.deleteById(specialityId);
        service.deleteById(specialityId);

        // then
        then(specialtyRepository).should(timeout(100).atLeastOnce()).deleteById(specialityId);
    }

    @Test
    void deleteByIdAtMost() {
        // given
        long specialityId = 1L;

        // when
        service.deleteById(specialityId);
        service.deleteById(specialityId);

        // then
        then(specialtyRepository).should(atMost(5)).deleteById(specialityId);
    }

    @Test
    void deleteByIdNever() {
        // when
        service.deleteById(1L);
        service.deleteById(1L);

        // then
        then(specialtyRepository).should(timeout(100).atLeastOnce()).deleteById(1L);
        then(specialtyRepository).should(never()).deleteById(5L);
    }

    @Test
    void testDelete() {
        // when
        service.delete(new Speciality());

        // then
        then(specialtyRepository).should().delete(any(Speciality.class));
        then(specialtyRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void testDeleteDoThrow() {
        doThrow(new RuntimeException("boom")).when(specialtyRepository).delete(any(Speciality.class));

        assertThrows(RuntimeException.class, () -> service.delete(new Speciality()));

        verify(specialtyRepository).delete(any(Speciality.class));
    }

    @Test
    void testFindByIdThrowBDD() {
        // given
        given(specialtyRepository.findById(anyLong())).willThrow(new RuntimeException("boom"));

        // when & then
        assertThrows(RuntimeException.class, () -> service.findById(1L));
        then(specialtyRepository).should().findById(anyLong());
    }

    @Test
    void testDeleteThrowBDD() {
        // given
        willThrow(new RuntimeException("boom")).given(specialtyRepository).delete(any(Speciality.class));

        // when & then
        assertThrows(RuntimeException.class, () -> service.delete(new Speciality()));
        then(specialtyRepository).should().delete(any(Speciality.class));
    }

    @Test
    void testSaveLambda() {
        // given
        final String MATCH_ME = "MATCH ME";
        Speciality speciality = new Speciality();
        speciality.setDescription(MATCH_ME);

        Speciality savedSpeciality = new Speciality();
        savedSpeciality.setId(1L);

            // need mock to only return on match MATCH_ME string
        given(specialtyRepository.save(argThat(argument -> argument.getDescription().equals(MATCH_ME))))
                .willReturn(savedSpeciality);

        // when
        Speciality returnedSpeciality = service.save(speciality);

        // then
        assertThat(returnedSpeciality).isNotNull();
        assertThat(returnedSpeciality.getId()).isEqualTo(1L);
    }
    @Test
    void testSaveLambdaNoMatch() {
        // given
        final String MATCH_ME = "MATCH ME";
        Speciality speciality = new Speciality();
        speciality.setDescription("NO MATCH");

        Speciality savedSpeciality = new Speciality();
        savedSpeciality.setId(1L);

            // need mock to only return on match MATCH_ME string
        given(specialtyRepository.save(argThat(argument -> argument.getDescription().equals(MATCH_ME))))
                .willReturn(savedSpeciality);

        // when
        Speciality returnedSpeciality = service.save(speciality);

        // then
        assertNull(returnedSpeciality);
    }
}