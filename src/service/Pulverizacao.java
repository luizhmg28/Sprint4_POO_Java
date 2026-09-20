package service;

import model.TrechoRodovia;

public class Pulverizacao extends ServicoOperacional {
    public Pulverizacao() {
        super("Pulverizacao");
    }

    @Override
    public void executarServico(TrechoRodovia trecho) {
        System.out.println("Executando pulverizacao no KM " + trecho.getKmInicial()
                + " ao KM " + trecho.getKmFinal() + ".");
    }
}
