package model;

public class TrechoMonitoradoIoT extends TrechoRodovia implements MonitoravelViaIoT {
    private int codigoSensor;

    public TrechoMonitoradoIoT(int kmInicial, int kmFinal, double alturaVegetacao, String tipoAmbiente, boolean acessoDificil, int codigoSensor) {
        this(0, kmInicial, kmFinal, alturaVegetacao, tipoAmbiente, acessoDificil, codigoSensor);
    }

    public TrechoMonitoradoIoT(int id, int kmInicial, int kmFinal, double alturaVegetacao, String tipoAmbiente, boolean acessoDificil, int codigoSensor) {
        super(id, kmInicial, kmFinal, alturaVegetacao, tipoAmbiente, acessoDificil);
        this.codigoSensor = codigoSensor;
    }

    @Override
    public boolean isMonitoradoIoT() {
        return true;
    }

    @Override
    public Integer getCodigoSensor() {
        return codigoSensor;
    }

    @Override
    public TrechoRodovia comId(int id) {
        return new TrechoMonitoradoIoT(id, getKmInicial(), getKmFinal(), getAlturaVegetacao(), getTipoAmbiente(), isAcessoDificil(), codigoSensor);
    }

    @Override
    public void transmitirDadosSensor() {
        System.out.println("Sensor " + codigoSensor + " transmitindo dados do trecho KM "
                + getKmInicial() + " ao KM " + getKmFinal() + ".");
        simularCrescimento();
        System.out.println("Nova altura estimada da vegetacao: " + getAlturaVegetacao() + " cm");
    }
}
