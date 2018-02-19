package com.sevenloldev.spring.userdevice.binding;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface JpaBindingRepository extends CrudRepository<Binding, Integer>,
    JpaSpecificationExecutor<Binding> {
  @EntityGraph(value = "Binding.device", type = EntityGraphType.LOAD)
  Optional<Binding> getById(Integer id);
}
