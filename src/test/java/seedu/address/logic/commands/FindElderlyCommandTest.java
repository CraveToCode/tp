package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.commons.core.Messages.MESSAGE_ELDERLIES_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalElderlies.CARL;
import static seedu.address.testutil.TypicalElderlies.ELLE;
import static seedu.address.testutil.TypicalElderlies.FIONA;
import static seedu.address.testutil.TypicalElderlies.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameContainsKeywordsPredicate;

/**
 * Contains integration tests (interaction with the Model) for {@code FindElderlyCommand}.
 */
public class FindElderlyCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        NameContainsKeywordsPredicate firstPredicate =
                new NameContainsKeywordsPredicate(Collections.singletonList("first"));
        NameContainsKeywordsPredicate secondPredicate =
                new NameContainsKeywordsPredicate(Collections.singletonList("second"));

        FindElderlyCommand findFirstCommand = new FindElderlyCommand(firstPredicate);
        FindElderlyCommand findSecondCommand = new FindElderlyCommand(secondPredicate);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindElderlyCommand findFirstCommandCopy = new FindElderlyCommand(firstPredicate);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different command -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_zeroKeywords_noElderlyFound() {
        String expectedMessage = String.format(MESSAGE_ELDERLIES_LISTED_OVERVIEW, 0);
        CommandResult expectedCommandResult = new CommandResult(expectedMessage,
                CommandResult.ListDisplayChange.ELDERLY);

        NameContainsKeywordsPredicate predicate = preparePredicate(" ");
        FindElderlyCommand command = new FindElderlyCommand(predicate);
        expectedModel.updateFilteredElderlyList(predicate);

        assertCommandSuccess(command, model, expectedCommandResult, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredElderlyList());
    }

    @Test
    public void execute_multipleKeywords_multipleElderliesFound() {
        String expectedMessage = String.format(MESSAGE_ELDERLIES_LISTED_OVERVIEW, 3);
        CommandResult expectedCommandResult = new CommandResult(expectedMessage,
                CommandResult.ListDisplayChange.ELDERLY);

        NameContainsKeywordsPredicate predicate = preparePredicate("Kurz Elle Kunz");
        FindElderlyCommand command = new FindElderlyCommand(predicate);
        expectedModel.updateFilteredElderlyList(predicate);

        assertCommandSuccess(command, model, expectedCommandResult, expectedModel);
        assertEquals(Arrays.asList(CARL, ELLE, FIONA), model.getFilteredElderlyList());
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private NameContainsKeywordsPredicate preparePredicate(String userInput) {
        return new NameContainsKeywordsPredicate(Arrays.asList(userInput.split("\\s+")));
    }
}