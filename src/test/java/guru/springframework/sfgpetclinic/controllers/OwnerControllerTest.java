package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.fauxspring.BindingResult;
import guru.springframework.sfgpetclinic.fauxspring.Model;
import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

/**
 * Created by Rami SAHRAOUI on 03/12/2023
 */
@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {
    private static final String OWNERS_CREATE_OR_UPDATE_OWNER_FORM = "owners/createOrUpdateOwnerForm";
    private static final String REDIRECT_OWNERS_5 = "redirect:/owners/5";
    @Mock(lenient = true)
    OwnerService service;

    @InjectMocks
    OwnerController controller;

    @Mock
    BindingResult result;

    @Mock
    Model model;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    @BeforeEach
    void setUp() {
        given(service.findAllByLastNameLike(stringArgumentCaptor.capture())).willAnswer(invocationOnMock -> {
            List<Owner> ownerList = new ArrayList<>();
            String name = invocationOnMock.getArgument(0);
            switch (name) {
                case "%Buck%":
                    ownerList.add(new Owner(1L, "Joe", "Buck"));
                    return ownerList;
                case "%DontFindMe%":
                    return ownerList;
                case "%FindMe%":
                    ownerList.add(new Owner(1L, "Joe", "Buck"));
                    ownerList.add(new Owner(2L, "Jim", "Franc"));
                    return ownerList;
            }
            throw new RuntimeException("Invalid Arguments");
        });
    }

    @Test
    void processFindFormWildcardSingleOwner() {
        // given
        Owner owner = new Owner(1L, "Joe", "Buck");

        // when
        String viewName = controller.processFindForm(owner, result, null);

        // then
        assertThat(stringArgumentCaptor.getValue()).isEqualToIgnoringCase("%Buck%");
        assertThat(viewName).isEqualToIgnoringCase("redirect:/owners/1");
    }
    @Test
    void processFindFormWildcardNoOwner() {
        // given
        Owner owner = new Owner(1L, "Joe", "DontFindMe");

        // when
        String viewName = controller.processFindForm(owner, result, null);

        // then
        assertThat(stringArgumentCaptor.getValue()).isEqualToIgnoringCase("%DontFindMe%");
        assertThat(viewName).isEqualToIgnoringCase("owners/findOwners");
    }
    @Test
    void processFindFormWildcardOwnerList() {
        // given
        Owner owner = new Owner(1L, "Joe", "FindMe");
        InOrder inOrder = Mockito.inOrder(model, service);

        // when
        String viewName = controller.processFindForm(owner, result, model);

        // then
        assertThat(stringArgumentCaptor.getValue()).isEqualToIgnoringCase("%FindMe%");
        assertThat(viewName).isEqualToIgnoringCase("owners/ownersList");
            // inorder asserts
        inOrder.verify(service).findAllByLastNameLike(anyString());
        inOrder.verify(model).addAttribute(anyString(), anyList());
    }

    @Test
    void processCreationFormHasErrors() {
        // given
        Owner owner = new Owner(1L, "Joe", "Buck");
        given(result.hasErrors()).willReturn(true);

        // when
        String viewName = controller.processCreationForm(owner, result);

        // then
        assertThat(viewName).isEqualTo(OWNERS_CREATE_OR_UPDATE_OWNER_FORM);
    }

    @Test
    void processCreationFormNoErrors() {
        // given
        Owner owner = new Owner(5L, "Joe", "Buck");
        given(result.hasErrors()).willReturn(false);
        given(service.save(any(Owner.class))).willReturn(owner);

        // when
        String viewName = controller.processCreationForm(owner, result);

        // then
        assertThat(viewName).isEqualTo(REDIRECT_OWNERS_5);
        then(service).should().save(any(Owner.class));
    }
}