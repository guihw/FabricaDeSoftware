public class Main {

    public static void main(String[] args) {

        Consulta consulta = new Consulta.Builder("Tabela").
                condicao("Condicao").
                ordenacao("Ordenada").
                build();

        System.out.println(consulta.getTabela());
    }
}
