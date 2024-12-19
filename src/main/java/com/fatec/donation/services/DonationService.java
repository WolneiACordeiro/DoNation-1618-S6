package com.fatec.donation.services;

import com.fatec.donation.domain.dto.DonationDTO;
import com.fatec.donation.domain.dto.DonationRequestDTO;
import com.fatec.donation.domain.dto.DonationSearchDTO;
import com.fatec.donation.domain.request.CreateDonationRequest;
import com.fatec.donation.domain.request.DonationRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface DonationService {
    @Transactional(transactionManager = "transactionManager")
    DonationDTO createDonation(CreateDonationRequest request, MultipartFile imageFile, String groupName) throws IOException;

    @Transactional(transactionManager = "transactionManager")
    List<DonationSearchDTO> searchDonation(String groupName, String searchTerm);

    @Transactional(transactionManager = "transactionManager")
    DonationRequest createDonationRequest(UUID donationID, String groupName) throws IOException;

}
