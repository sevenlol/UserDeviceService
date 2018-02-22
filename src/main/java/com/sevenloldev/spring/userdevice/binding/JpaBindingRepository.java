package com.sevenloldev.spring.userdevice.binding;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface JpaBindingRepository extends CrudRepository<Binding, Integer>,
    JpaSpecificationExecutor<Binding> {
  /** get binding by ID with embedded device entity */
  @EntityGraph(value = "Binding.device", type = EntityGraphType.LOAD)
  Optional<Binding> getById(Integer id);
}
