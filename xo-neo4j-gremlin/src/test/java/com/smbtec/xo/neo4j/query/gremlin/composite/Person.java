package com.smbtec.xo.neo4j.query.gremlin.composite;

import java.util.Collection;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.smbtec.xo.tinkerpop.blueprints.api.annotation.Vertex;

@Label
public interface Person {

    String getFirstname();

    void setFirstname(String firstName);

    String getLastname();

    void setLastname(String lastName);

    int getAge();

    void setAge(int age);

    Collection<Person> getFriends();

}
