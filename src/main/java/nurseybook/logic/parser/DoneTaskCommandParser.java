package nurseybook.logic.parser;

import static nurseybook.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static nurseybook.logic.parser.ParserUtil.MESSAGE_INDEX_TOO_EXTREME;
import static nurseybook.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;

import nurseybook.commons.core.index.Index;
import nurseybook.logic.commands.DoneTaskCommand;
import nurseybook.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new DoneTaskCommand object
 */
public class DoneTaskCommandParser implements Parser<DoneTaskCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DoneTaskCommand
     * and returns a DoneTaskCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public DoneTaskCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new DoneTaskCommand(index);
        } catch (ParseException pe) {
            if (pe.getMessage().equals(MESSAGE_INDEX_TOO_EXTREME) || pe.getMessage().equals(MESSAGE_INVALID_INDEX)) {
                throw pe;
            } else {
                throw new ParseException(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, DoneTaskCommand.MESSAGE_USAGE), pe);
            }
        }
    }

}
