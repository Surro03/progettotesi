package it.univaq.progettotesi.repository;

import it.univaq.progettotesi.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByEmail(String email);
    boolean existsByEmail(String email);
    Page<Client> findByBuilding_Id(Long buildingId, Pageable pageable);
}
