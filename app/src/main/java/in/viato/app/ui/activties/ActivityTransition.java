package in.viato.app.ui.activties;

/**
 * Created by ranadeep on 12/09/15.
 */

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to specify Activity transitions.
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ActivityTransition {

    /**
     * @return The animation resource ID to be applied to a newly created
     *         Activity that is just appearing
     */
    int createEnterAnimation() default 0;
    /**
     * @return The animation resource ID to be applied to the current Activity
     *         that is going to the background after a new Activity has been
     *         created
     */
    int createExitAnimation() default 0;

    /**
     * @return The animation resource ID to be applied to an Activity that is
     *         coming into the foreground after the current Activity has been
     *         destroyed
     */
    int destroyEnterAnimation() default 0;

    /**
     * @return The animation resource ID to be applied to the curent Activity
     *         that is getting destroyed
     */
    int destroyExitAnimation() default 0;
}

