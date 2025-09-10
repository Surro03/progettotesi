package it.univaq.progettotesi.repository;

import it.univaq.progettotesi.entity.Building;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long>{
    Optional<Building> findByName(String name);
    boolean existsByName(String name);

    Page<Building> findByAdmin_Id(Long id, Pageable pageable);
}
