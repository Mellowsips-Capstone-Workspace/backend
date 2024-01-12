package com.capstone.workspace.repositories.partner;

import com.capstone.workspace.entities.partner.Partner;
import com.capstone.workspace.repositories.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PartnerRepository extends BaseRepository<Partner, UUID> {
    Partner findByBusinessCode(String businessCode);

    Partner findByTaxCode(String taxCode);
}
