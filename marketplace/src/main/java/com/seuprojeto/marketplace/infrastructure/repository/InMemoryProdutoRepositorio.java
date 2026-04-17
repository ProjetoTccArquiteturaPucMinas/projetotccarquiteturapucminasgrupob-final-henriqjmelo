package com.seuprojeto.marketplace.infrastructure.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.seuprojeto.marketplace.domain.model.CategoriaProduto;
import com.seuprojeto.marketplace.domain.model.Produto;
import com.seuprojeto.marketplace.domain.repository.ProdutoRepositorio;

@Repository
public class InMemoryProdutoRepositorio implements ProdutoRepositorio {

    private final List<Produto> produtos = List.of(
            new Produto(1L, "Capinha Premium", CategoriaProduto.CAPINHA, new BigDecimal("49.90")),
            new Produto(2L, "Carregador Turbo 30W", CategoriaProduto.CARREGADOR, new BigDecimal("119.90")),
            new Produto(3L, "Fone Bluetooth AirSound", CategoriaProduto.FONE, new BigDecimal("199.90")),
            new Produto(4L, "Película 3D", CategoriaProduto.PELICULA, new BigDecimal("29.90")),
            new Produto(5L, "Suporte Veicular Magnético", CategoriaProduto.SUPORTE, new BigDecimal("59.90"))
    );

    @Override
    public List<Produto> findAll() {
        return produtos;
    }

    @Override
    public Optional<Produto> findById(Long id) {
        return produtos.stream()
                .filter(produto -> produto.getId().equals(id))
                .findFirst();
    }
}