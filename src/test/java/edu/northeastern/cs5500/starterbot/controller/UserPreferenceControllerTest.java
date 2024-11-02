package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import org.junit.jupiter.api.Test;

class UserPreferenceControllerTest {
    // TODO: replace with discord IDs that are more real
    static final String USER_ID_1 = "23h5ikoqaehokljhaoe";
    static final String USER_ID_2 = "2kjfksdjdkhokljhaoe";
    static final String PREFERRED_NAME_1 = "Joe";
    static final String PREFERRED_NAME_2 = "Penny";
    static final Integer BUDGET_1 = 1000;

    private UserPreferenceController getUserPreferenceController() {
        UserPreferenceController userPreferenceController =
                new UserPreferenceController(new InMemoryRepository<>());
        return userPreferenceController;
    }

    @Test
    void testSetNameActuallySetsName() {
        // setup
        UserPreferenceController userPreferenceController = getUserPreferenceController();

        // precondition
        assertThat(userPreferenceController.getPreferredNameForUser(USER_ID_1))
                .isNotEqualTo(PREFERRED_NAME_1);

        // mutation
        userPreferenceController.setPreferredNameForUser(USER_ID_1, PREFERRED_NAME_1);

        // postcondition
        assertThat(userPreferenceController.getPreferredNameForUser(USER_ID_1))
                .isEqualTo(PREFERRED_NAME_1);
    }

    @Test
    void testSetNameOverwritesOldName() {
        UserPreferenceController userPreferenceController = getUserPreferenceController();
        userPreferenceController.setPreferredNameForUser(USER_ID_1, PREFERRED_NAME_1);
        assertThat(userPreferenceController.getPreferredNameForUser(USER_ID_1))
                .isEqualTo(PREFERRED_NAME_1);

        userPreferenceController.setPreferredNameForUser(USER_ID_1, PREFERRED_NAME_2);
        assertThat(userPreferenceController.getPreferredNameForUser(USER_ID_1))
                .isEqualTo(PREFERRED_NAME_2);
    }

    @Test
    void testSetNameOnlyOverwritesTargetUser() {
        UserPreferenceController userPreferenceController = getUserPreferenceController();

        userPreferenceController.setPreferredNameForUser(USER_ID_1, PREFERRED_NAME_1);
        userPreferenceController.setPreferredNameForUser(USER_ID_2, PREFERRED_NAME_2);

        assertThat(userPreferenceController.getPreferredNameForUser(USER_ID_1))
                .isEqualTo(PREFERRED_NAME_1);
        assertThat(userPreferenceController.getPreferredNameForUser(USER_ID_2))
                .isEqualTo(PREFERRED_NAME_2);
    }

    @Test
    void testSetBudgetActuallySetsBudget() {
        // setup
        UserPreferenceController userPreferenceController = getUserPreferenceController();

        // precondition
        assertThat(userPreferenceController.getMaximumBudgetForUser(USER_ID_1))
                .isNotEqualTo(BUDGET_1);

        // mutation
        userPreferenceController.setMaximumBudgetForUser(USER_ID_1, BUDGET_1);

        // postcondition
        assertThat(userPreferenceController.getMaximumBudgetForUser(USER_ID_1)).isEqualTo(BUDGET_1);
    }

    @Test
    void testThatBudgetMustBeNonnegative() {
        // setup
        UserPreferenceController userPreferenceController = getUserPreferenceController();

        // precondition
        assertThat(userPreferenceController.getMaximumBudgetForUser(USER_ID_1)).isNull();

        // mutation
        try {
            userPreferenceController.setMaximumBudgetForUser(USER_ID_1, -1);
            fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("maximumBudget");
        }
    }
}
