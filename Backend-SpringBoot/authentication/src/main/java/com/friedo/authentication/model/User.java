package com.friedo.authentication.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "utilisateurs")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_utilisateur", unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "mot_de_passe", nullable = false)
    private String password;

    @Column(name = "code_verification_OTP")
    private String otpVerificationCode;

    @Column(name = "code_verification_OTP")
    private LocalDateTime otpVerificationCodeExpiresAt;

    @Column(name = "est_active")
    private boolean enabled;

//    Constructeur pour créer un utilisateur non verifié (non activé)
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Constructeur par défaut


    public User() {
    }

//    getAuthorities() doit retourner une Collection car Spring Security traite souvent plusieurs autorisations
//   GrantedAuthority C'est une interface de Spring Security qui représente une autorisation (rôle ou permission)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return List.of();
    }

    //Ajouter des vérifications booléennes appropriées
    //Est-ce que le compte n'est pas expiré --> True  Donc le compte est valide
    @Override
    public boolean isAccountNonExpired(){
        return true;
    }
     @Override
    public boolean isAccountNonLocked(){
        return true;
    }
     @Override
    public boolean isCredentialsNonExpired(){
        return true;
    }
     @Override
    public boolean isEnabled(){
        return enabled; //return l'attribut enabled car c'est déja un attribut de type booléen
    }


}
