package com.episen.ms_product.application.service;

import com.episen.ms_product.application.dto.ProductRequestDTO;
import com.episen.ms_product.application.dto.ProductResponseDTO;
import com.episen.ms_product.application.mapper.ProductMapper;
import com.episen.ms_product.domain.entity.Product;
import com.episen.ms_product.domain.repository.ProductRepository;
import com.episen.ms_product.infrastructure.exception.ResourceAlreadyExistsException;
import com.episen.ms_product.infrastructure.exception.ResourceNotFoundException;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private MeterRegistry meterRegistry;

    @InjectMocks
    private ProductService productService;

    // --- TEST 1 : Création impossible si le nom existe déjà ---
    @Test
    void createProduct_shouldThrow_whenNameAlreadyExists() {
        // Given
        ProductRequestDTO req = ProductRequestDTO.builder().name("iPhone 15").build();
        when(productRepository.existsByName("iPhone 15")).thenReturn(true); // On simule que ça existe

        // When + Then
        assertThrows(ResourceAlreadyExistsException.class, () -> productService.createProduct(req));
        verify(productRepository, never()).save(any());
    }

    // --- TEST 2 : Mise à jour du stock ---
    @Test
    void updateStock_shouldUpdateAndSave_whenProductExists() {
        // Given
        Long productId = 1L;
        Product existingProduct = Product.builder().id(productId).stock(10).build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        // Quand on save, on retourne l'objet modifié
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        // Mock du mapper pour le retour
        ProductResponseDTO expectedDto = ProductResponseDTO.builder().stock(50).build();
        when(productMapper.toDto(any(Product.class))).thenReturn(expectedDto);

        // When
        ProductResponseDTO result = productService.updateStock(productId, 50);

        // Then
        assertEquals(50, result.getStock()); // Vérifie le retour
        assertEquals(50, existingProduct.getStock()); // Vérifie que l'entité a changé
        verify(productRepository).save(existingProduct); // Vérifie l'appel au repo
    }

    // --- TEST 3 : Mise à jour stock échoue si produit inconnu ---
    @Test
    void updateStock_shouldThrow_whenProductNotFound() {
        // Given
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // When + Then
        assertThrows(ResourceNotFoundException.class, () -> productService.updateStock(99L, 10));
    }
}