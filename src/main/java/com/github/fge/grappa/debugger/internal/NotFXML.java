package com.github.fge.grappa.debugger.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// TODO: what retention should that be, really?
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface NotFXML
{
}
