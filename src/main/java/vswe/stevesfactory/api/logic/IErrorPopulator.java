package vswe.stevesfactory.api.logic;

import java.util.List;

/**
 * Error populator that collects error of a linked procedure into a list. It is not required to populate all errors in one pass, i.e. there
 * can be separate error populators for each type of data.
 * <p>
 * This class is meant to be client only, therefore it is legal to use client only classes in implementation.
 */
public interface IErrorPopulator {

    /**
     * Populate the given list with (potential) errors. This method should directly return the parameter {@code list} with the errors added;
     * no modification to previous existing errors should be done.
     * <p>
     * This will be used to present a list of errors to the player in the client, for example.
     *
     * @param errors The list to add errors to
     * @return The parameter {@code list} with errors added
     */
    @SuppressWarnings("UnusedReturnValue")
    List<String> populateErrors(List<String> errors);
}
