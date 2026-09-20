package model;

public class TrechoRodovia {
    private int id;
    private int kmInicial;
    private int kmFinal;
    private double alturaVegetacao;
    private String tipoAmbiente;
    private boolean acessoDificil;

    public TrechoRodovia(int kmInicial, int kmFinal, double alturaVegetacao, String tipoAmbiente, boolean acessoDificil) {
        this(0, kmInicial, kmFinal, alturaVegetacao, tipoAmbiente, acessoDificil);
    }

    public TrechoRodovia(int id, int kmInicial, int kmFinal, double alturaVegetacao, String tipoAmbiente, boolean acessoDificil) {
        this.id = id;
        this.kmInicial = kmInicial;
        this.kmFinal = kmFinal;
        this.alturaVegetacao = alturaVegetacao;
        this.tipoAmbiente = tipoAmbiente;
        this.acessoDificil = acessoDificil;
    }

    public int getId() {
        return id;
    }

    public int getKmInicial() {
        return kmInicial;
    }

    public int getKmFinal() {
        return kmFinal;
    }

    public double getAlturaVegetacao() {
        return alturaVegetacao;
    }

    public void setAlturaVegetacao(double alturaVegetacao) {
        this.alturaVegetacao = alturaVegetacao;
    }

    public String getTipoAmbiente() {
        return tipoAmbiente;
    }

    public boolean isAcessoDificil() {
        return acessoDificil;
    }

    public boolean isMonitoradoIoT() {
        return false;
    }

    public Integer getCodigoSensor() {
        return null;
    }

    public TrechoRodovia comId(int id) {
        return new TrechoRodovia(id, kmInicial, kmFinal, alturaVegetacao, tipoAmbiente, acessoDificil);
    }

    public void simularCrescimento() {
        if (tipoAmbiente.equalsIgnoreCase("umido")) {
            alturaVegetacao += 15;
        } else if (tipoAmbiente.equalsIgnoreCase("seco")) {
            alturaVegetacao += 5;
        } else {
            alturaVegetacao += 10;
        }
    }

    public void exibirDados() {
        System.out.println("Trecho KM " + kmInicial + " ao KM " + kmFinal);
        System.out.println("Altura da vegetacao: " + alturaVegetacao + " cm");
        System.out.println("Tipo de ambiente: " + tipoAmbiente);
        System.out.println("Acesso dificil: " + (acessoDificil ? "Sim" : "Nao"));
        if (isMonitoradoIoT()) {
            System.out.println("Sensor IoT: " + getCodigoSensor());
        }
    }
}
