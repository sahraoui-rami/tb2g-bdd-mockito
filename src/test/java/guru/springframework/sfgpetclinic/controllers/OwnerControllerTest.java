package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.fauxspring.BindingResult;
import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

/**
 * Created by Rami SAHRAOUI on 03/12/2023
 */
@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {
    private static final String OWNERS_CREATE_OR_UPDATE_OWNER_FORM = "owners/createOrUpdateOwnerForm";
    private static final String REDIRECT_OWNERS_5 = "redirect:/owners/5";
    @Mock
    OwnerService service;

    @InjectMocks
    OwnerController controller;

    @Mock
    BindingResult result;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;
    @Test
    void processCreationFormHasErrors() {
        // given
        Owner owner = new Owner(1L, "Joe", "Buck");
        given(result.hasErrors()).willReturn(true);

        // when
        String returnedView = controller.processCreationForm(owner, result);

        // then
        assertThat(returnedView).isEqualTo(OWNERS_CREATE_OR_UPDATE_OWNER_FORM);
    }
    @Test
    void processCreationFormNoErrors() {
        // given
        Owner owner = new Owner(5L, "Joe", "Buck");
        given(result.hasErrors()).willReturn(false);
        given(service.save(any(Owner.class))).willReturn(owner);

        // when
        String returnedView = controller.processCreationForm(owner, result);

        // then
        assertThat(returnedView).isEqualTo(REDIRECT_OWNERS_5);
        then(service).should().save(any(Owner.class));
    }

    @Test
    void processFindFormWildcardString() {
        // given
        Owner owner = new Owner(1L, "Joe", "Buck");
        List<Owner> ownerList = new ArrayList<>();
        ownerList.add(owner);
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        given(service.findAllByLastNameLike(captor.capture())).willReturn(ownerList);

        // when
        controller.processFindForm(owner, result, null);

        // then
        assertThat(captor.getValue()).isEqualTo("%Buck%");
    }
    @Test
    void processFindFormWildcardStringAnnotation() {
        // given
        Owner owner = new Owner(1L, "Joe", "Buck");
        List<Owner> ownerList = new ArrayList<>();
        ownerList.add(owner);
        given(service.findAllByLastNameLike(stringArgumentCaptor.capture())).willReturn(ownerList);

        // when
        controller.processFindForm(owner, result, null);

        // then
        assertThat(stringArgumentCaptor.getValue()).isEqualTo("%Buck%");
    }
}