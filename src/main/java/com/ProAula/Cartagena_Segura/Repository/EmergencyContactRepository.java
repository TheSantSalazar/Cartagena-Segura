package com.ProAula.Cartagena_Segura.Repository;

import com.ProAula.Cartagena_Segura.Model.EmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long> {
    List<EmergencyContact> findByActiveTrue();
    List<EmergencyContact> findByZoneAndActiveTrue(String zone);
    List<EmergencyContact> findByType(EmergencyContact.ContactType type);
    List<EmergencyContact> findByZoneAndType(String zone, EmergencyContact.ContactType type);
}