import java.util.List;

public class Consulta {

    private String tabela;
    private List<String> campos;
    private String condicao;
    private String ordenacao;
    private int limite = 100;
    private int offset = 0;
    private boolean apenasDistintos = false;

    private Consulta (Builder builder) {
        this.tabela = builder.tabela;
        this.campos = builder.campos;
        this.condicao = builder.condicao;
        this.ordenacao = builder.ordenacao;
        this.limite = builder.limite;
        this.offset = builder.offset;
        this.apenasDistintos = builder.apenasDistintos;
    }

    public static class Builder {

        private String tabela;
        private List<String> campos;
        private String condicao;
        private String ordenacao;
        private int limite = 100;
        private int offset = 0;
        private boolean apenasDistintos = false;

        Builder (String tabela) {}

        Builder campos(List<String> campos) {
            this.campos = campos;
            return this;
        }

        Builder condicao(String condicao) {
            this.condicao = condicao;
            return this;
        }

        Builder ordenacao(String ordenacao) {
            this.ordenacao = ordenacao;
            return this;
        }

        Builder limite(int limite) {
            this.limite = limite;
            return this;
        }

        Builder offset(int offset) {
            this.offset = offset;
            return this;
        }

        Builder apenasDistintos(boolean apenasDistintos) {
            this.apenasDistintos = apenasDistintos;
            return this;
        }

        public Consulta build() {
            return new Consulta(this);
        }
    }

    public String getTabela() {
        return tabela;
    }

    public void setTabela(String tabela) {
        this.tabela = tabela;
    }

    public List<String> getCampos() {
        return campos;
    }

    public void setCampos(List<String> campos) {
        this.campos = campos;
    }

    public String getCondicao() {
        return condicao;
    }

    public void setCondicao(String condicao) {
        this.condicao = condicao;
    }

    public String getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(String ordenacao) {
        this.ordenacao = ordenacao;
    }

    public int getLimite() {
        return limite;
    }

    public void setLimite(int limite) {
        this.limite = limite;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean isApenasDistintos() {
        return apenasDistintos;
    }

    public void setApenasDistintos(boolean apenasDistintos) {
        this.apenasDistintos = apenasDistintos;
    }
}
