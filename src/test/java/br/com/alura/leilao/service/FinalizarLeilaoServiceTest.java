package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FinalizarLeilaoServiceTest {

    private FinalizarLeilaoService service;//classe a ser testada

    @Mock
    private LeilaoDao leilaoDao;//mock

    @Mock
    private EnviadorDeEmails enviadorDeEmails;

    @BeforeEach
    public void beforeEach() {//antes de cada teste
        MockitoAnnotations.initMocks(this);//inicia os mocks
        this.service = new FinalizarLeilaoService(leilaoDao,enviadorDeEmails);//tenha esse objeto iniciado
    }

    @Test
    void deveriaFinalizarUmLeilao() {
        List<Leilao>leiloes = leiloes();

        Mockito.when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes);

        service.finalizarLeiloesExpirados();

        Leilao leilao = leiloes.get(0);

        assertTrue(leilao.isFechado());
        Assert.assertEquals(new BigDecimal("900"),
                leilao.getLanceVencedor().getValor());//se eu não declarar todos
                                                      //os comportamentos esperados do método
                                                     //o teste falha
        Mockito.verify(leilaoDao).salvar(leilao);//dentro do meu obj leilaodao
                                                // , o método salvar foi executado
                                                // pro objeto leilao q eu passei
    }

    @Test
    void deveriaEnviarEmailParaVencedorDoLeilao() {
        List<Leilao>leiloes = leiloes();

        Mockito.when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes);

        service.finalizarLeiloesExpirados();

        Leilao leilao = leiloes.get(0);

        Lance lanceVencedor=  leilao.getLanceVencedor();

        Mockito.verify(enviadorDeEmails)
                .enviarEmailVencedorLeilao(lanceVencedor);

    }




    private List<Leilao> leiloes() {//lista com leiloes, para nao devolver uma lista vazia
        List<Leilao> lista = new ArrayList<>();

        Leilao leilao = new Leilao("Celular",
                new BigDecimal("500"),
                new Usuario("Fulano"));

        Lance primeiro = new Lance(new Usuario("Beltrano"),
                new BigDecimal("600"));
        Lance segundo = new Lance(new Usuario("Ciclano"),
                new BigDecimal("900"));

        leilao.propoe(primeiro);
        leilao.propoe(segundo);

        lista.add(leilao);

        return lista;

    }
}