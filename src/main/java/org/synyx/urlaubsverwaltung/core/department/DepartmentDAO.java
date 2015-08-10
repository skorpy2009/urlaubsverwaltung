package org.synyx.urlaubsverwaltung.core.department;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/**
 * Daniel Hammann - <hammann@synyx.de>.
 */
public interface DepartmentDAO extends JpaRepository<Department, Integer> {

    Optional<Department> findAbsenceMappingByName(String name);
}
