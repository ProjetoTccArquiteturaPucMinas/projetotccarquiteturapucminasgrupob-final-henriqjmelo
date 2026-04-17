package com.seuprojeto.marketplace.application.usecase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.seuprojeto.marketplace.application.dto.SelecaoCarrinho;
import com.seuprojeto.marketplace.domain.model.CategoriaProduto;
import com.seuprojeto.marketplace.domain.model.Produto;
import com.seuprojeto.marketplace.domain.model.ResumoCarrinho;
import com.seuprojeto.marketplace.domain.repository.ProdutoRepositorio;

public class CalcularCarrinhoUseCase {

    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final BigDecimal MAX_DISCOUNT_PERCENT = new BigDecimal("25");
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final ProdutoRepositorio produtoRepositorio;

    public CalcularCarrinhoUseCase(ProdutoRepositorio produtoRepositorio) {
        this.produtoRepositorio = produtoRepositorio;
    }

    public ResumoCarrinho executar(List<SelecaoCarrinho> selecaoCarrinhos) {
        if (selecaoCarrinhos == null || selecaoCarrinhos.isEmpty()) {
            return new ResumoCarrinho(ZERO.setScale(2, RoundingMode.HALF_UP), ZERO.setScale(2, RoundingMode.HALF_UP));
        }

        BigDecimal subtotal = ZERO;
        BigDecimal categoriaPercentualTotal = ZERO;
        int quantidadeTotal = 0;

        for (SelecaoCarrinho selecao : selecaoCarrinhos) {
            if (selecao == null || selecao.getIdProduto() == null || selecao.getQuantidade() == null) {
                continue;
            }

            int quantidade = Math.max(0, selecao.getQuantidade());
            if (quantidade == 0) {
                continue;
            }

            Produto produto = produtoRepositorio.findById(selecao.getIdProduto())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + selecao.getIdProduto()));

            subtotal = subtotal.add(produto.getPreco().multiply(BigDecimal.valueOf(quantidade)));
            quantidadeTotal += quantidade;
            categoriaPercentualTotal = categoriaPercentualTotal.add(getPercentualPorCategoria(produto.getCategoriaProduto()).multiply(BigDecimal.valueOf(quantidade)));
        }

        BigDecimal percentualQuantidade = getPercentualPorQuantidade(quantidadeTotal);
        BigDecimal percentualTotal = percentualQuantidade.add(categoriaPercentualTotal);
        if (percentualTotal.compareTo(MAX_DISCOUNT_PERCENT) > 0) {
            percentualTotal = MAX_DISCOUNT_PERCENT;
        }

        BigDecimal desconto = subtotal
                .multiply(percentualTotal)
                .divide(HUNDRED, 2, RoundingMode.HALF_UP);

        return new ResumoCarrinho(subtotal.setScale(2, RoundingMode.HALF_UP), desconto);
    }

    private BigDecimal getPercentualPorQuantidade(int quantidadeTotal) {
        if (quantidadeTotal <= 1) {
            return ZERO;
        }
        if (quantidadeTotal == 2) {
            return new BigDecimal("5");
        }
        if (quantidadeTotal == 3) {
            return new BigDecimal("7");
        }
        return new BigDecimal("10");
    }

    private BigDecimal getPercentualPorCategoria(CategoriaProduto categoriaProduto) {
        if (categoriaProduto == null) {
            return ZERO;
        }
        switch (categoriaProduto) {
            case CAPINHA:
                return new BigDecimal("3");
            case CARREGADOR:
                return new BigDecimal("5");
            case FONE:
                return new BigDecimal("3");
            case PELICULA:
                return new BigDecimal("2");
            case SUPORTE:
                return new BigDecimal("2");
            default:
                return ZERO;
        }
    }
}