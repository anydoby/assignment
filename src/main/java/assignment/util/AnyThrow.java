/*
 * Created on Dec 2, 2014
 * 
 * Copyright (C) Asset Control International B.V. All Rights Reserved.
 */
package assignment.util;

/**
 * Throw undeclared checked exception in Java. Usage: <code>
 * try {
 *     doSomethingThatThrows.exception();
 * } catch (Exception e) {
 *     throw AnyThrow.unchecked(e);
 * }
 * </code>
 *
 * @see "http://www.eishay.com/2011/11/throw-undeclared-checked-exception-in.html"
 */
public class AnyThrow
{
    /**
     * @param e
     * @return unchecked exception
     */
    public static RuntimeException unchecked(final Throwable e)
    {
        AnyThrow.<RuntimeException> throwAny(e);
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void throwAny(final Throwable e) throws E
    {
        throw (E) e;
    }
}