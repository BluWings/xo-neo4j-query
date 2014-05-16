package com.smbtec.xo.neo4j.query.lucene;

import com.buschmais.xo.spi.annotation.QueryDefinition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@QueryDefinition
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Lucene {

    String value();

    Class<?> type();

}
