package service;

import model.TrechoRodovia;

public class RocadaMecanizada extends ServicoOperacional {
    public RocadaMecanizada() {
        super("Rocada Mecanizada");
    }

    @Override
    public void executarServico(TrechoRodovia trecho) {
        System.out.println("Executando rocada mecanizada no KM " + trecho.getKmInicial()
                + " ao KM " + trecho.getKmFinal() + ".");
    }
}
