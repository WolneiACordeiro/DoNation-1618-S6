package com.fatec.donation.domain.mapper;

import com.fatec.donation.domain.dto.*;
import com.fatec.donation.domain.entity.ChatMessage;
import com.fatec.donation.domain.entity.Donation;
import com.fatec.donation.domain.entity.Group;
import com.fatec.donation.domain.entity.User;
import com.fatec.donation.domain.enums.DonationStatus;
import com.fatec.donation.domain.request.CreateDonationRequest;
import com.fatec.donation.domain.request.CreateGroupRequest;
import com.fatec.donation.domain.request.DonationRequest;
import com.fatec.donation.domain.request.UpdateGroupRequest;
import com.fatec.donation.repository.DonationRepository;
import com.fatec.donation.repository.GroupRepository;
import com.fatec.donation.repository.JoinGroupRequestRepository;
import com.fatec.donation.repository.UserRepository;
import com.fatec.donation.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class DonationMapper {

    @Autowired
    DonationRepository donationRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupMapper groupMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    JoinGroupRequestRepository joinGroupRequestRepository;

    public DonationRequestDTO toDonationRequestDTO(DonationRequest donation) {
        DonationRequestDTO donationRequestDTO = new DonationRequestDTO();
        donationRequestDTO.setId(donation.getId());
        donationRequestDTO.setUserReceiver(userMapper.toUserDTO(userRepository.findByUsername(donation.getUserReceiver().getUsername())));
        donationRequestDTO.setUserDonor(userMapper.toUserDTO(userRepository.findByUsername(donation.getUserDonor().getUsername())));
        donationRequestDTO.setGroup(groupMapper.toGroupDTO(donation.getGroup()));
        donationRequestDTO.setCreatedAt(donation.getCreatedAt().toString());
        donationRequestDTO.setDonationStatus(donation.getDonationStatus());
        return donationRequestDTO;
    }

    public DonationDTO toDonationDTO(Donation donation) {
        DonationDTO donationDTO = new DonationDTO();
        donationDTO.setName(donation.getName());
        donationDTO.setDescription(donation.getDescription());
        donationDTO.setCreatedAt(donation.getCreatedAt().toString());
        donationDTO.setAddress(donation.getAddress());
        donationDTO.setTags(donation.getTags());
        donationDTO.setDonor(userRepository.findOwnerDTOByGroupId(donation.getGroup().getId()));
        donationDTO.setGroup(groupMapper.toGroupDTO(groupRepository.findByGroupname(donation.getGroup().getGroupname())));
        donationDTO.setAvaliableDate(donation.getAvaliableDate().stream().toList());
        donationDTO.setDonationImage(donation.getDonationImage().getName());

        if (donation.getAvailability() == null || donation.getAvailability().isBlank()) {
            donation.setAvailability("INF");
        }
        donationDTO.setAvailability(donation.getAvailability());

        return donationDTO;
    }


    public Donation toDonation(CreateDonationRequest request, User donor, Group group) throws IOException {
        return Donation.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .description(request.getDescription())
                .address(request.getAddress())
                .tags(request.getTags())
                .createdAt(LocalDateTime.now())
                .donationImage(request.getDonationImage())
                .availability(request.getAvailability())
                .avaliableDate(request.getAvaliableDate())
                .group(group)
                .donor(donor)
                .build();
    }

    public DonationRequest toDonationRequest(User receptor, Group group, UUID donationId) throws IOException {
        return DonationRequest.builder()
                .id(UUID.randomUUID())
                .userReceiver(receptor)
                .userDonor(userRepository.findUserByDonationId(donationId))
                .donationStatus(DonationStatus.REQUESTED)
                .createdAt(LocalDateTime.now())
                .group(group)
                .chatMessages(null)
                .build();
    }

    public String createUniqueGroupName(String name) {
        String normalizedBaseName = Normalizer.normalize(name, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String baseName = "@" + pattern.matcher(normalizedBaseName).replaceAll("").trim().replace(" ", "").toLowerCase();
        String uniqueName = baseName;
        Random random = new Random();
        while (groupRepository.existsByGroupname(uniqueName)) {
            String randomSuffix = random.ints(48, 122)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(5)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            uniqueName = baseName + randomSuffix;
        }
        return uniqueName;
    }

    public void updateGroupWithRequest(Group group, UpdateGroupRequest request) {
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setAddress(request.getAddress());
        group.setGroupImage(request.getGroupImage());
        group.setLandscapeImage(request.getLandscapeImage());
    }
}