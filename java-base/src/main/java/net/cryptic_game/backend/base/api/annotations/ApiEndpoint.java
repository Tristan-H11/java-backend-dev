package net.cryptic_game.backend.base.api.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ApiEndpoint {

    @NotNull
    String id();

    @NotNull
    String[] groups() default {};

    @NotNull
    String[] description() default "";

    boolean disabled() default false;
}
