package br.com.motiva;

import br.com.motiva.dto.TrechoRequest;
import br.com.motiva.repository.*;
import br.com.motiva.service.TrechoService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired IntervencaoOperacionalRepository intervencoes;
    @Autowired RelatorioPrioridadeRepository relatorios;
    @Autowired TrechoRodoviaRepository trechos;
    @Autowired EquipeManutencaoRepository equipes;
    @Autowired TrechoService trechoService;

    @BeforeEach
    void limpar() {
        intervencoes.deleteAll();
        relatorios.deleteAll();
        trechos.deleteAll();
        equipes.deleteAll();
    }

    private String trecho(String altura, boolean acesso) {
        return """
                {"kmInicial":10,"kmFinal":11,"alturaVegetacao":%s,"tipoAmbiente":"umido",
                 "acessoDificil":%s,"codigoSensor":101,"monitoradoIoT":true}
                """.formatted(altura, acesso);
    }

    private String equipe(String nome) {
        return """
                {"nome":"%s","especialidade":"Rocada Manual","quantidadeIntegrantes":4,"status":"ATIVA"}
                """.formatted(nome);
    }

    private String intervencao(long trecho, long equipe, String status) {
        return """
                {"trechoId":%d,"equipeId":%d,"tipoServico":"Rocada Manual","prioridade":"URGENTE",
                 "status":"%s","dataProgramada":"2026-10-20","observacao":"Inspecao"}
                """.formatted(trecho, equipe, status);
    }

    private long id(MvcResult result) throws Exception {
        return json.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private MvcResult registrar(List<String> registros, MockHttpServletRequestBuilder pedido, int esperado) throws Exception {
        var resultado = mvc.perform(pedido).andExpect(status().is(esperado)).andReturn();
        var request = resultado.getRequest();
        registros.add("### " + request.getMethod() + " " + request.getRequestURI()
                + "\n\nParametros: " + request.getParameterMap().entrySet().stream()
                        .map(e -> e.getKey() + "=" + String.join(",", e.getValue())).toList()
                + "\n\nCorpo enviado:\n\n```json\n" + request.getContentAsString() + "\n```"
                + "\n\nHTTP " + resultado.getResponse().getStatus()
                + "\n\n```json\n" + resultado.getResponse().getContentAsString() + "\n```\n");
        return resultado;
    }

    @Test
    void fluxoCompletoComRegistroDasRequisicoes() throws Exception {
        var registros = new ArrayList<String>();
        long t = id(registrar(registros, post("/api/trechos").contentType("application/json").content(trecho("80", true)), 201));
        long e = id(registrar(registros, post("/api/equipes").contentType("application/json").content(equipe("Equipe API")), 201));
        long i = id(registrar(registros, post("/api/intervencoes").contentType("application/json").content(intervencao(t, e, "PROGRAMADA")), 201));
        registrar(registros, get("/api/trechos/" + t), 200);
        registrar(registros, put("/api/trechos/" + t).contentType("application/json").content(trecho("85", true)), 200);
        registrar(registros, put("/api/equipes/" + e).contentType("application/json").content(equipe("Equipe Atualizada")), 200);
        registrar(registros, put("/api/intervencoes/" + i).contentType("application/json").content(intervencao(t, e, "CONCLUIDA")), 200);
        registrar(registros, get("/api/equipes/" + e), 200);
        registrar(registros, get("/api/intervencoes/" + i), 200);
        registrar(registros, post("/api/relatorios"), 201);
        registrar(registros, get("/api/relatorios"), 200);
        registrar(registros, get("/api/relatorios/periodo").param("inicio", "2020-01-01T00:00:00").param("fim", "2100-01-01T00:00:00"), 200);
        registrar(registros, delete("/api/trechos/" + t), 400);
        registrar(registros, delete("/api/equipes/" + e), 400);
        registrar(registros, delete("/api/intervencoes/" + i), 204);
        registrar(registros, delete("/api/equipes/" + e), 204);
        registrar(registros, delete("/api/trechos/" + t), 204);
        registrar(registros, get("/api/trechos/" + t), 404);
        mvc.perform(get("/api/relatorios")).andExpect(jsonPath("$[0].qtUrgente").value(1))
                .andExpect(jsonPath("$[0].resumo").value(org.hamcrest.Matchers.containsString("Rocada Manual")));
        Files.createDirectories(Path.of("target/evidencias"));
        Files.writeString(Path.of("target/evidencias/requisicoes.md"),
                "# Registro de requisicoes\n\nSpring Boot + MockMvc + JPA. Perfil test, H2 em modo Oracle.\n\n"
                        + String.join("\n", registros));
    }

    @Test
    void retorna404EmTodosOsRecursosAusentes() throws Exception {
        var corpos = List.of(trecho("20", false), equipe("Equipe"), intervencao(1, 1, "PROGRAMADA"));
        var rotas = List.of("trechos", "equipes", "intervencoes");
        for (int n = 0; n < rotas.size(); n++) {
            var url = "/api/" + rotas.get(n) + "/999999";
            mvc.perform(get(url)).andExpect(status().isNotFound());
            mvc.perform(delete(url)).andExpect(status().isNotFound());
            mvc.perform(put(url).contentType("application/json").content(corpos.get(n))).andExpect(status().isNotFound());
        }
    }

    @Test
    void validaCorposVinculosEParametros() throws Exception {
        for (String rota : List.of("trechos", "equipes", "intervencoes")) {
            mvc.perform(post("/api/" + rota).contentType("application/json").content("{}")).andExpect(status().isBadRequest());
            mvc.perform(post("/api/" + rota).contentType("application/json").content("{")).andExpect(status().isBadRequest());
        }
        mvc.perform(post("/api/trechos").contentType("application/json").content(trecho("-1", false))).andExpect(status().isBadRequest());
        mvc.perform(post("/api/trechos").contentType("application/json").content(trecho("1.001", false))).andExpect(status().isBadRequest());
        mvc.perform(post("/api/trechos").contentType("application/json").content(trecho("20", false).replace("\"kmFinal\":11", "\"kmFinal\":10"))).andExpect(status().isBadRequest());
        mvc.perform(post("/api/trechos").contentType("application/json").content(trecho("20", false).replace("\"codigoSensor\":101", "\"codigoSensor\":null"))).andExpect(status().isBadRequest());
        mvc.perform(post("/api/equipes").contentType("application/json").content(equipe("Equipe").replace("ATIVA", "INVALIDO"))).andExpect(status().isBadRequest());
        mvc.perform(post("/api/intervencoes").contentType("application/json").content(intervencao(999999, 999999, "PROGRAMADA"))).andExpect(status().isNotFound());
        mvc.perform(get("/api/trechos/abc")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/trechos").param("alturaMinima", "-1")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/relatorios/periodo")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/relatorios/periodo").param("inicio", "invalido").param("fim", "2026-10-20T00:00:00")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/relatorios/periodo").param("inicio", "2026-10-21T00:00:00").param("fim", "2026-10-20T00:00:00")).andExpect(status().isBadRequest());
        assertThrows(ConstraintViolationException.class, () -> trechoService.criar(
                new TrechoRequest(1, 2, new BigDecimal("-1"), "seco", false, null, false)));
    }

    @Test
    void preservaLimiaresHistoricoEMedicoesIoT() throws Exception {
        for (String altura : List.of("0", "29.99", "30", "60")) {
            mvc.perform(post("/api/trechos").contentType("application/json").content(trecho(altura, true))).andExpect(status().isCreated());
        }
        mvc.perform(post("/api/trechos").contentType("application/json").content(trecho("60.01", true))).andExpect(status().isCreated());
        mvc.perform(post("/api/trechos").contentType("application/json").content(trecho("60.01", false))).andExpect(status().isCreated());
        var resultado = mvc.perform(post("/api/relatorios")).andExpect(status().isCreated())
                .andExpect(jsonPath("$.qtNormal").value(2)).andExpect(jsonPath("$.qtAtencao").value(2))
                .andExpect(jsonPath("$.qtUrgente").value(1)).andExpect(jsonPath("$.qtCritico").value(1)).andReturn();
        var data = json.readTree(resultado.getResponse().getContentAsString()).get("dataGeracao").asText();
        mvc.perform(get("/api/relatorios/periodo").param("inicio", data).param("fim", data))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/relatorios/periodo").param("inicio", "2000-01-01T00:00:00").param("fim", "2000-01-02T00:00:00"))
                .andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/trechos").param("alturaMinima", "60.01")).andExpect(jsonPath("$.length()").value(2));
        mvc.perform(post("/api/relatorios")).andExpect(jsonPath("$.qtAtencao").value(2)).andExpect(jsonPath("$.qtUrgente").value(1));
        assertEquals(2, relatorios.count());
        assertEquals(0, BigDecimal.ZERO.compareTo(trechos.findAll().get(0).getAlturaVegetacao()));
    }

    @Test
    void geraRelatorioSemTrechos() throws Exception {
        mvc.perform(post("/api/relatorios")).andExpect(status().isCreated())
                .andExpect(jsonPath("$.qtNormal").value(0)).andExpect(jsonPath("$.resumo").value("Nenhum trecho cadastrado."));
        assertEquals(1, relatorios.count());
    }

    @Test
    void consultasDerivadasFiltramEquipesEIntervencoes() throws Exception {
        long t = id(mvc.perform(post("/api/trechos").contentType("application/json").content(trecho("70", true))).andReturn());
        long e = id(mvc.perform(post("/api/equipes").contentType("application/json").content(equipe("Equipe"))).andReturn());
        mvc.perform(post("/api/intervencoes").contentType("application/json").content(intervencao(t, e, "PROGRAMADA"))).andExpect(status().isCreated());
        mvc.perform(get("/api/equipes").param("status", "ATIVA")).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/equipes").param("status", "INATIVA")).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/intervencoes").param("tipoServico", "Rocada Manual")).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/intervencoes").param("tipoServico", "Pulverizacao")).andExpect(jsonPath("$.length()").value(0));
    }
}
