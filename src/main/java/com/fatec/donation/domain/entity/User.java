package com.fatec.donation.domain.entity;

import com.fatec.donation.domain.enums.BrazilStates;
import com.fatec.donation.domain.enums.Roles;
import com.fatec.donation.domain.images.UserImages;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@Node("User")
@Builder
public class User implements UserDetails {
    @Id
    private UUID id;
    private String name;
    private String username;
    private String email;
    private String password;
    @Relationship(type = "PROFILE_IMAGE", direction = Relationship.Direction.INCOMING)
    private UserImages userImage;
    private String phone;
    private LocalDate birthday;
    private BrazilStates state;
    private String city;
    private List<String> tags;
    private Boolean firstAccess;
    private LocalDateTime createdAt;
    private Set<Roles> roles;

    public User() {
        this.id = UUID.randomUUID();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
