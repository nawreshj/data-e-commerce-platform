package com.membership.users.application.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.membership.users.application.dto.UserRequestDTO;
import com.membership.users.application.dto.UserResponseDTO;
import com.membership.users.application.mapper.UserMapper;
import com.membership.users.domain.entity.User;
import com.membership.users.domain.repository.UserRepository;
import com.membership.users.infrastructure.exception.ResourceAlreadyExistsException;
import com.membership.users.infrastructure.exception.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des utilisateurs.
 * Best practices :
 * - @Transactional pour la gestion des transactions
 * - Logging avec SLF4J
 * - Métriques personnalisées avec Micrometer
 * - Gestion d'erreurs explicite avec exceptions métier
 * - Séparation de la logique métier du contrôleur
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MeterRegistry meterRegistry;

    /**
     * Récupère tous les utilisateurs
     */
    public List<UserResponseDTO> getAllUsers() {
        log.debug("Récupération de tous les utilisateurs");
        
        List<User> users = userRepository.findAll();
        
        log.info("Nombre d'utilisateurs récupérés: {}", users.size());
        
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un utilisateur par son ID
     */
    public UserResponseDTO getUserById(Long id) {
        log.debug("Récupération de l'utilisateur avec l'ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        log.info("Utilisateur trouvé: {}", user.getEmail());
        
        return userMapper.toDto(user);
    }

    /**
     * Crée un nouvel utilisateur
     */
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        log.debug("Création d'un nouvel utilisateur: {}", userRequestDTO.getEmail());
        
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            log.warn("Tentative de création d'un utilisateur avec un email existant: {}", 
                    userRequestDTO.getEmail());
            throw new ResourceAlreadyExistsException("User", "email", userRequestDTO.getEmail());
        }
        
        User user = userMapper.toEntity(userRequestDTO);
        User savedUser = userRepository.save(user);
        
        // Métrique personnalisée
        Counter.builder("users.created")
                .description("Nombre d'utilisateurs créés")
                .tag("type", "user")
                .register(meterRegistry)
                .increment();
        
        log.info("Utilisateur créé avec succès: ID={}, Email={}", savedUser.getId(), savedUser.getEmail());
        
        return userMapper.toDto(savedUser);
    }

    /**
     * Met à jour un utilisateur existant
     */
    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO) {
        log.debug("Mise à jour de l'utilisateur avec l'ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        // Vérifier si le nouvel email existe déjà (sauf si c'est le même)
        if (!user.getEmail().equals(userRequestDTO.getEmail()) 
                && userRepository.existsByEmail(userRequestDTO.getEmail())) {
            log.warn("Tentative de mise à jour avec un email existant: {}", userRequestDTO.getEmail());
            throw new ResourceAlreadyExistsException("User", "email", userRequestDTO.getEmail());
        }
        
        userMapper.updateEntityFromDto(userRequestDTO, user);
        User updatedUser = userRepository.save(user);
        
        // Métrique personnalisée
        Counter.builder("users.updated")
                .description("Nombre d'utilisateurs mis à jour")
                .tag("type", "user")
                .register(meterRegistry)
                .increment();
        
        log.info("Utilisateur mis à jour avec succès: ID={}, Email={}", 
                updatedUser.getId(), updatedUser.getEmail());
        
        return userMapper.toDto(updatedUser);
    }

    /**
     * Supprime un utilisateur
     */
    @Transactional
    public void deleteUser(Long id) {
        log.debug("Suppression de l'utilisateur avec l'ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        userRepository.delete(user);
        
        // Métrique personnalisée
        Counter.builder("users.deleted")
                .description("Nombre d'utilisateurs supprimés")
                .tag("type", "user")
                .register(meterRegistry)
                .increment();
        
        log.info("Utilisateur supprimé avec succès: ID={}, Email={}", id, user.getEmail());
    }

    /**
     * Recherche des utilisateurs par nom
     */
    public List<UserResponseDTO> searchUsersByLastName(String lastName) {
        log.debug("Recherche d'utilisateurs avec le nom: {}", lastName);
        
        List<User> users = userRepository.searchByLastName(lastName);
        
        log.info("Nombre d'utilisateurs trouvés: {}", users.size());
        
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les utilisateurs actifs
     */
    public List<UserResponseDTO> getActiveUsers() {
        log.debug("Récupération des utilisateurs actifs");
        
        List<User> users = userRepository.findByActiveTrue();
        
        log.info("Nombre d'utilisateurs actifs: {}", users.size());
        
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Désactive un utilisateur (soft delete)
     */
    @Transactional
    public UserResponseDTO deactivateUser(Long id) {
        log.debug("Désactivation de l'utilisateur avec l'ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        user.setActive(false);
        User deactivatedUser = userRepository.save(user);
        
        log.info("Utilisateur désactivé avec succès: ID={}, Email={}", id, user.getEmail());
        
        return userMapper.toDto(deactivatedUser);
    }
}
