package com.fatec.donation.services.impl;

import com.fatec.donation.domain.dto.DonationDTO;
import com.fatec.donation.domain.dto.DonationRequestDTO;
import com.fatec.donation.domain.dto.DonationSearchDTO;
import com.fatec.donation.domain.entity.Donation;
import com.fatec.donation.domain.entity.Group;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.images.DonationImages;
import com.fatec.donation.domain.mapper.DonationMapper;
import com.fatec.donation.domain.mapper.GroupMapper;
import com.fatec.donation.domain.mapper.UserMapper;
import com.fatec.donation.domain.request.CreateDonationRequest;
import com.fatec.donation.domain.request.DonationRequest;
import com.fatec.donation.exceptions.EntityNotFoundException;
import com.fatec.donation.exceptions.IllegalStateException;
import com.fatec.donation.repository.*;
import com.fatec.donation.services.DonationImagesService;
import com.fatec.donation.services.DonationService;
import com.fatec.donation.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {
    private final PlatformTransactionManager transactionManager;
    private final DonationRepository donationRepository;
    private final DonationRequestRepository donationRequestRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final GroupRepository groupRepository;
    private final DonationImagesService donationImagesService;
    private final DonationMapper donationMapper;
    private final UserMapper userMapper;
    private final GroupMapper groupMapper;
    private final JoinGroupRequestRepository joinGroupRequestRepository;

    @Override
    @Transactional(transactionManager = "transactionManager")
    public DonationDTO createDonation(CreateDonationRequest request, MultipartFile imageFile, String groupName) throws IOException {
        // Obtendo o ID do usuário a partir do JWT
        UUID userId = userService.getUserIdByJwt();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        UUID groupId = groupRepository.findIdByGroupname(groupName);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));
        if (Boolean.FALSE.equals((groupRepository.memberByUserIdAndGroupName(userId, groupName))) && Boolean.FALSE.equals((joinGroupRequestRepository.ownerByUserIdAndGroupId(userId, groupId)))) {
            throw new IllegalStateException("Você não pertence a este grupo");
        }

        DonationImages donationImage = null;

        if (imageFile != null && !imageFile.isEmpty()) {
            DonationImages savedProfileImage = donationImagesService.updateOrCreateImageForDonation(null, imageFile);
            donationImage = savedProfileImage;
        }

        request.setDonationImage(donationImage);

        Donation donation = donationMapper.toDonation(request, user, group);

        Donation savedDonation = donationRepository.save(donation);

        return donationMapper.toDonationDTO(savedDonation);
    }

    @Override
    @Transactional(transactionManager = "transactionManager")
    public List<DonationSearchDTO> searchDonation(String groupName, String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            searchTerm = "";
        }
        UUID userId = userService.getUserIdByJwt();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        UUID groupId = groupRepository.findIdByGroupname(groupName);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));
        if (Boolean.FALSE.equals(groupRepository.memberByUserIdAndGroupName(userId, groupName)) &&
                Boolean.FALSE.equals(joinGroupRequestRepository.ownerByUserIdAndGroupId(userId, groupId))) {
            throw new IllegalStateException("Você não pertence a este grupo");
        }
        return donationMapper.toDonationSearchDTOList(donationRepository.findDonationsByGroupAndSearchTerm(groupId, searchTerm));
    }

    @Override
    @Transactional(transactionManager = "transactionManager")
    public DonationRequest createDonationRequest(UUID donationID, String groupName) throws IOException {
        // Obtendo o ID do usuário a partir do JWT
        UUID userId = userService.getUserIdByJwt();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        UUID groupId = groupRepository.findIdByGroupname(groupName);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));
        Donation donation = donationRepository.findById(donationID)
                .orElseThrow(() -> new EntityNotFoundException("Doação não encontrada"));
        if (Boolean.FALSE.equals((groupRepository.memberByUserIdAndGroupName(userId, groupName))) && Boolean.FALSE.equals((joinGroupRequestRepository.ownerByUserIdAndGroupId(userId, groupId)))) {
            throw new IllegalStateException("Você não pertence a este grupo");
        }
        if(Boolean.FALSE.equals(donationRepository.hasDonationFromRelation(donationID, groupName))){
            throw new IllegalStateException("Essa doação não pertence a este grupo");
        }

        return donationMapper.toDonationRequest(user, group, donationID);

//        DonationRequest savedDonation = donationRequestRepository.save(donationRequest);
//
//        return donationMapper.toDonationRequestDTO(savedDonation, donationID);
    }
}
