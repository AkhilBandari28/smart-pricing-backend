package com.smartpricing.dao;

import com.smartpricing.entity.Negotiation;
import com.smartpricing.repository.NegotiationRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class NegotiationDao {

    private final NegotiationRepository negotiationRepository;

    public NegotiationDao(NegotiationRepository negotiationRepository) {
        this.negotiationRepository = negotiationRepository;
    }

    public Negotiation save(Negotiation negotiation) {
        return negotiationRepository.save(negotiation);
    }

    public Optional<Negotiation> findById(Long negotiationId) {
        return negotiationRepository.findById(negotiationId);
    }
}
