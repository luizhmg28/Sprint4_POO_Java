package service;

import model.TrechoRodovia;

public class RocadaManual extends ServicoOperacional {
    public RocadaManual() {
        super("Rocada Manual");
    }

    @Override
    public void executarServico(TrechoRodovia trecho) {
        System.out.println("Executando rocada manual no KM " + trecho.getKmInicial()
                + " ao KM " + trecho.getKmFinal() + ".");
    }
}
