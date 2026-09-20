package service;

import model.TrechoRodovia;

public abstract class ServicoOperacional {
    private String nomeServico;

    protected ServicoOperacional(String nomeServico) {
        this.nomeServico = nomeServico;
    }

    public String getNomeServico() {
        return nomeServico;
    }

    public abstract void executarServico(TrechoRodovia trecho);
}
