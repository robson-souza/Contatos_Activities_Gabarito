package aulas.pdmi.contatos_activities_gabarito.model;

import java.io.Serializable;

/**
 * Created by vagner on 21/08/16.
 */
public class Contato implements Serializable {
    private static final long serialVersionUID = 1L;

    public Long _id;
    public String nome;
    public String sobrenome;
    public String telefone;
    public byte[] imagem;

    @Override
    public String toString() {
        return "Contato{" +
                "_id=" + _id +
                ", nome='" + nome + '\'' +
                ", sobrenome='" + sobrenome + '\'' +
                ", telefone='" + telefone + '\'' +
                ", imagem='" + imagem + '\'' +
                '}';
    }
}
