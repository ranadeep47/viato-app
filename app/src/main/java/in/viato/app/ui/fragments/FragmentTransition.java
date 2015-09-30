package in.viato.app.ui.fragments;

/**
 * Created by ranadeep on 12/09/15.
 */

import android.support.v4.app.FragmentTransaction;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to specify Fragment transitions.
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface FragmentTransition {

    /**
     * @return The animation resource ID to be applied to a fragment that is
     *         just appearing
     */
    int enterAnimation() default FragmentTransaction.TRANSIT_FRAGMENT_OPEN;

    /**
     * @return The animation resource ID to be applied to a fragment that is
     *         exiting
     */
    int exitAnimation() default FragmentTransaction.TRANSIT_FRAGMENT_CLOSE;

    /**
     * @return The animation resource ID to be applied to a fragment that is
     *         being popped from the backstack
     */
    int popEnterAnimation() default FragmentTransaction.TRANSIT_FRAGMENT_OPEN;

    /**
     * @return The animation resource ID to be applied to a fragment that is
     *         being popped from the backstack
     */
    int popExitAnimation() default FragmentTransaction.TRANSIT_FRAGMENT_CLOSE;
}

