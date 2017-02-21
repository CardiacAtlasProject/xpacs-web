package org.cardiacatlas.xpacs.repository;

import org.cardiacatlas.xpacs.domain.CapModel;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the CapModel entity.
 */
@SuppressWarnings("unused")
public interface CapModelRepository extends JpaRepository<CapModel,Long> {

}
